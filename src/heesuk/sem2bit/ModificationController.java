package heesuk.sem2bit;

import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Locale;

import ch.ethz.iks.slp.Locator;
import ch.ethz.iks.slp.ServiceLocationEnumeration;
import ch.ethz.iks.slp.ServiceLocationException;
import ch.ethz.iks.slp.ServiceLocationManager;
import ch.ethz.iks.slp.ServiceType;
import heesuk.sem2bit.exception.DomainNotDefinedException;
import heesuk.sem2bit.exception.LocalProtocolNotSpecifiedException;
import heesuk.sem2bit.kb.TreeFactory;
import heesuk.sem2bit.kb.protocol.ProtocolKBUtil;
import heesuk.sem2bit.kb.protocol.enums.Domain;
import heesuk.sem2bit.kb.protocol.iot.IoTProtocolKBUtil;
import heesuk.sem2bit.kb.protocol.sdp.SDPKBUtil;
import heesuk.sem2bit.msg.ModificationCandidate;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.internal.ConnectFailureException;
import org.eclipse.paho.sample.mqttv3app.MQTTConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.security.krb5.Config;

public class ModificationController {
	private static final Logger LOG = LoggerFactory.getLogger(ModificationController.class);

	private static ModificationController _instance;
	private Locator locator;
	private ArrayList<String> scopes;
	private ArrayList<String> attrs;
	private ProtocolKBUtil kb;

	private ModificationController() {
		try {
			if(ConfigUtil.getInstance().domain == Domain.SDP){
				locator = ServiceLocationManager.getLocator(new Locale("en"));

				// find all services of type "test" that have attribute "cool=yes"
				scopes = new ArrayList<String>();
				attrs = new ArrayList<String>();
				scopes.add("default");
				attrs.add("max-connections");
			}
		} catch (ServiceLocationException e) {
			e.printStackTrace();
		}
	}

	public static ModificationController getInstance() {
		if (_instance == null) {
			_instance = new ModificationController();
		}

		return _instance;
	}

	public void init() throws DomainNotDefinedException, LocalProtocolNotSpecifiedException {
		Domain domain = ConfigUtil.getInstance().domain;

		// build and load Protocol-relevant knowledge base
		long beforeKB = System.currentTimeMillis();

		if(domain == Domain.SDP){
			this.kb = SDPKBUtil.getInstance();
		}else if(domain == Domain.IoT_Protocol){
			this.kb = IoTProtocolKBUtil.getInstance();
		}else {
			LOG.error("Non-defined domain is asked.");
		}

		kb.buildKB();
		long afterKB = System.currentTimeMillis();
		ExperimentStat.getInstance().setKbLoadingTime(afterKB-beforeKB);

		// build modification probability tree and modification sequence tree
		TreeFactory.getInstance().buildTree();
		TreeFactory.getInstance().printProbTree();
	}

	public void startMessageModification(int bound) {
		int cnt = 0;
		while(cnt++ < bound){
			long before = System.currentTimeMillis();
				
			try {
				// 2ms sleep is added to prevent socket buffer overflow
				Thread.sleep(ConfigUtil.getInstance().req_interval);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			if(ProbingStatus.success == false){
				Thread interactionT = new Thread(new InteractionRunnable(cnt, before));
				interactionT.start();
			}else {
				break;
			}
		}	
	}

	public void sendModifiedMessage(int cnt)
			throws ServiceLocationException, SocketTimeoutException, IllegalArgumentException, ConnectFailureException {
		if(ConfigUtil.getInstance().domain == Domain.SDP){
			ServiceLocationEnumeration sle = locator.findServices(
					cnt, new ServiceType("service:test"), scopes, "(cool=yes)");
		}else if(ConfigUtil.getInstance().domain == Domain.IoT_Protocol){
			// ModificationCandidate[] seq = TreeFactory.getInstance().getNextSequence();
			// LOG.info("[{}] {}",cnt, seq);

			// Default settings:
			String action 		= "connect";
			String broker = ConfigUtil.getInstance().broker_address;
			int port 			= 1883;
			String clientId 	= "SampleJavaV3_"+action+"("+cnt+")";
			boolean cleanSession = true;			// Non durable subscriptions
			String password = null;
			String userName = null;
			String protocol = "tcp://";
			String url = protocol + broker + ":" + port;

			try {
				MQTTConnector sampleClient =
						new MQTTConnector(cnt,url, clientId, cleanSession,userName,password);
				sampleClient.connect();
			} catch(MqttException me) {
				LOG.debug("reason "+me.getReasonCode());
				LOG.debug("msg "+me.getMessage());
				LOG.debug("loc "+me.getLocalizedMessage());
				LOG.debug("cause "+me.getCause());
				LOG.debug("excep "+me);
				//me.printStackTrace();
				throw new ConnectFailureException(cnt);
			}
		}else{
			try {
				throw new DomainNotDefinedException();
			} catch (DomainNotDefinedException e) {
				e.printStackTrace();
			}
		}

	}

	/**
	 * 
	 * Transmit the actual modified message to the service agents
	 * @author Heesuk Son (heesuk.chad.son@gmail.com)
	 *
	 */
	public class InteractionRunnable implements Runnable{
		long before;
		int cnt;
		long after;

		public InteractionRunnable(int cnt, long before){
			this.cnt = cnt;
			this.before = before;
		}
		
		@Override
		public void run() {
			try {
				sendModifiedMessage(cnt);
				after = System.currentTimeMillis();
				ExperimentStat.getInstance().addMsgTransTimeTotal(after-before);
				ExperimentStat.getInstance().setExpRoundCnt(cnt);
				ExperimentStat.getInstance().setSuccessRound(cnt);
				LOG.info("["+cnt+":SUCCESS] A reply message is returned!!");
				ProbingStatus.success = true;
			} catch (SocketTimeoutException e) {
				LOG.info("["+cnt+":FAIL] SocketTimeoutException!!");
				try {
					this.finalize();
				} catch (Throwable e1) {
					e1.printStackTrace();
				}
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (ServiceLocationException e) {
				e.printStackTrace();
			} catch (ConnectFailureException e) {
				LOG.info("["+cnt+":FAIL] ConnectFailureException!! (e.cnt={},cnt={})",e.cnt,cnt);
				try {
					//Thread.currentThread().interrupt();
					this.finalize();
				} catch (Throwable e1) {
					e1.printStackTrace();
				}
			}
		}
		
	}
}
