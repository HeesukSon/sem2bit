package heesuk.percom.sem2bit;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Locale;

import ch.ethz.iks.slp.Locator;
import ch.ethz.iks.slp.ServiceLocationEnumeration;
import ch.ethz.iks.slp.ServiceLocationException;
import ch.ethz.iks.slp.ServiceLocationManager;
import ch.ethz.iks.slp.ServiceType;
import heesuk.percom.sem2bit.kb.TreeFactory;
import heesuk.percom.sem2bit.kb.sdp.SDPKBUtil;
import heesuk.percom.sem2bit.msg.ModificationCandidate;

public class ModificationController {
	private static ModificationController _instance;
	private Locator locator;
	private ArrayList<String> scopes;
	private ArrayList<String> attrs;

	private ModificationController() {
		try {
			this.loadConfig();
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

	public void init() {	
		// build and load SDP-relevant knowledge base
		long beforeKB = System.currentTimeMillis();
		SDPKBUtil.getInstance().buildKB();
		long afterKB = System.currentTimeMillis();
		ExperimentStat.getInstance().setKbLoadingTime(afterKB-beforeKB);

		// build modification probability tree and modification sequence tree
		TreeFactory.getInstance().buildTree();
	}
	
	public void loadConfig(){
		try {
			FileReader reader = new FileReader("config");
			BufferedReader bf = new BufferedReader(reader);
			
			String line;
			while((line = bf.readLine()) != null){
				if(!line.startsWith("//")) {
					String[] keyValue = line.split("=");
					if (keyValue[0].trim().equals("local_address")) {
						Configurations.local_address = keyValue[1].trim();
					} else if (keyValue[0].trim().equals("tcp_timeout")) {
						Configurations.tcp_timeout = Integer.parseInt(keyValue[1].trim());
					} else if (keyValue[0].trim().equals("iteration_bound")) {
						Configurations.iteration_bound = Integer.parseInt(keyValue[1].trim());
					} else if (keyValue[0].trim().equals("exp_mode")) {
						Configurations.exp_mode = keyValue[1].trim();
					} else if (keyValue[0].trim().equals("log_mode")) {
						Configurations.log_mode = keyValue[1].trim();
					} else {
						throw new ConfigNotDefinedException();
					}
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ConfigNotDefinedException e) {
			e.printStackTrace();
		}
	}

	public void startMessageModification(int bound) {
		int cnt = 0;
		while(cnt++ < bound){
			long before = System.currentTimeMillis();
				
			try {
				// 2ms sleep is added to prevent socket buffer overflow
				Thread.sleep(2);
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

	public void sendModifiedMessage() throws ServiceLocationException, SocketTimeoutException, IllegalArgumentException{
		ServiceLocationEnumeration sle = locator.findServices(new ServiceType("service:test"), scopes, "(cool=yes)");
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
	
	public class ConfigNotDefinedException extends Exception{
		
	}
	
	/**
	 * 
	 * Transmit the actual modified message to the service agents
	 * @author Heesuk Son (heesuk.chad.son@gmail.com)
	 *
	 */
	public class InteractionRunnable implements Runnable{
		long before;
		int index;
		
		public InteractionRunnable(int index, long before){
			this.index = index;
			this.before = before;
		}
		
		@Override
		public void run() {
			try {
				sendModifiedMessage();
				long after = System.currentTimeMillis();
				ProbeLogger.appendLogln("probe", "["+index+":SUCCESS] A reply message is returned!!");
				ExperimentStat.getInstance().setMsgTransTimeTotal(ExperimentStat.getInstance().getMsgTransTimeTotal()+(after-before));
			} catch (SocketTimeoutException e) {
				ProbeLogger.appendLogln("probe", "["+index+":FAIL] SocketTimeoutException!!");
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
