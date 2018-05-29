package heesuk.percom.sem2bit;

import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Locale;

import ch.ethz.iks.slp.Locator;
import ch.ethz.iks.slp.ServiceLocationEnumeration;
import ch.ethz.iks.slp.ServiceLocationException;
import ch.ethz.iks.slp.ServiceLocationManager;
import ch.ethz.iks.slp.ServiceType;
import heesuk.percom.sem2bit.exception.DomainNotDefinedException;
import heesuk.percom.sem2bit.exception.LocalProtocolNotSpecifiedException;
import heesuk.percom.sem2bit.kb.TreeFactory;
import heesuk.percom.sem2bit.kb.protocol.ProtocolKBUtil;
import heesuk.percom.sem2bit.kb.protocol.enums.Domain;
import heesuk.percom.sem2bit.kb.protocol.iot.IoTProtocolKBUtil;
import heesuk.percom.sem2bit.kb.protocol.sdp.SDPKBUtil;
import heesuk.percom.sem2bit.msg.ModificationCandidate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ModificationController {
	private static final Logger LOG = LoggerFactory.getLogger(ModificationController.class);

	private static ModificationController _instance;
	private Locator locator;
	private ArrayList<String> scopes;
	private ArrayList<String> attrs;
	private ProtocolKBUtil kb;

	private ModificationController() {
		try {
			locator = ServiceLocationManager.getLocator(new Locale("en"));

			// find all services of type "test" that have attribute "cool=yes"
			scopes = new ArrayList<String>();
			attrs = new ArrayList<String>();
			scopes.add("default");
			attrs.add("max-connections");
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

		if(domain == Domain.SDP){
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
		}else if(domain == Domain.IoT_Protocol){
			// TODO
		}else{
			LOG.error("Non-defined domain is asked.");
		}
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

	public void sendModifiedMessage(int cnt) throws ServiceLocationException, SocketTimeoutException, IllegalArgumentException{
		ServiceLocationEnumeration sle = locator.findServices(cnt, new ServiceType("service:test"), scopes, "(cool=yes)");
	}

	public void startSeqVerification(int bound) {
		for (int i = 0; i < bound; i++) {
			ModificationCandidate[] seq = TreeFactory.getInstance().getNextSequence();
			ProbeLogger.appendLog("probe", "[returned sequence:" + i + "] ");
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
				System.out.println("["+cnt+":SUCCESS] A reply message is returned!!");
				//ProbeLogger.appendLogln("probe", "["+cnt+":SUCCESS] A reply message is returned!!");
				ExperimentStat.getInstance().setMsgTransTimeTotal(ExperimentStat.getInstance().getMsgTransTimeTotal()+(after-before));
				ProbingStatus.success = true;
			} catch (SocketTimeoutException e) {
				System.out.println("["+cnt+":FAIL] SocketTimeoutException!!");
				//ProbeLogger.appendLogln("probe", "["+cnt+":FAIL] SocketTimeoutException!!");
				long after = System.currentTimeMillis();
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
