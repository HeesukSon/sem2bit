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
			throws ServiceLocationException, SocketTimeoutException, IllegalArgumentException{
		if(ConfigUtil.getInstance().domain == Domain.SDP){
			ServiceLocationEnumeration sle = locator.findServices(
					cnt, new ServiceType("service:test"), scopes, "(cool=yes)");
		}else if(ConfigUtil.getInstance().domain == Domain.IoT_Protocol){
			ModificationCandidate[] seq = TreeFactory.getInstance().getNextSequence();
			LOG.info("[{}] {}",cnt, seq);
			/*
			// Default settings:
			String action 		= "publish";
			String topic 		= "Sample/Java/v3";
			String message 		= "Message from blocking Paho MQTTv3 Java client sample";
			int qos 			= 2;
			//String broker 		= "m2m.eclipse.org";
			String broker = "127.0.0.1";
			int port 			= 1883;
			String clientId 	= "SampleJavaV3_"+action;
			boolean cleanSession = true;			// Non durable subscriptions
			String password = null;
			String userName = null;
			String protocol = "tcp://";

			String url = protocol + broker + ":" + port;

			// With a valid set of arguments, the real work of
			// driving the client API can begin
			try {
				// Create an instance of this class
				MQTTConnector sampleClient = new MQTTConnector(url, clientId, cleanSession,userName,password);
				sampleClient.publish(topic,qos,message.getBytes());
			} catch(MqttException me) {
				// Display full details of any exception that occurs
				LOG.error("reason "+me.getReasonCode());
				LOG.error("msg "+me.getMessage());
				LOG.error("loc "+me.getLocalizedMessage());
				LOG.error("cause "+me.getCause());
				LOG.error("excep "+me);
				me.printStackTrace();
			}
			*/
		}else{
			try {
				throw new DomainNotDefinedException();
			} catch (DomainNotDefinedException e) {
				e.printStackTrace();
			}
		}

	}

	public void startSeqVerification(int bound) {
		for (int i = 0; i < bound; i++) {
			ModificationCandidate[] seq = TreeFactory.getInstance().getNextSequence();
			LOG.info("[returned sequence:{}] ", i);
			for (int j = 0; j < seq.length; j++) {

				ProbeLogger.appendLog("probe", seq[j].toStringWithoutWeight() + "  ");
			}
			ProbeLogger.appendLog("probe", "\n");
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
		
		public InteractionRunnable(int cnt, long before){
			this.cnt = cnt;
			this.before = before;
		}
		
		@Override
		public void run() {
			try {
				sendModifiedMessage(cnt);
				long after = System.currentTimeMillis();
				LOG.info("["+cnt+":SUCCESS] A reply message is returned!!");
				//ProbeLogger.appendLogln("probe", "["+cnt+":SUCCESS] A reply message is returned!!");
				ExperimentStat.getInstance().setMsgTransTimeTotal(ExperimentStat.getInstance().getMsgTransTimeTotal()+(after-before));
				//TODO: set the success variable true before the experiment
				//ProbingStatus.success = true;
				ProbingStatus.success = false;
			} catch (SocketTimeoutException e) {
				LOG.info("["+cnt+":FAIL] SocketTimeoutException!!");
				//ProbeLogger.appendLogln("probe", "["+cnt+":FAIL] SocketTimeoutException!!");
				try {
					this.finalize();
				} catch (Throwable e1) {
					e1.printStackTrace();
				}
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (ServiceLocationException e) {
				e.printStackTrace();
			}
		}
		
	}
}