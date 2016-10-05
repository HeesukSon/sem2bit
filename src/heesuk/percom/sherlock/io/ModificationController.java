package heesuk.percom.sherlock.io;

import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Locale;

import ch.ethz.iks.slp.Locator;
import ch.ethz.iks.slp.ServiceLocationEnumeration;
import ch.ethz.iks.slp.ServiceLocationException;
import ch.ethz.iks.slp.ServiceLocationManager;
import ch.ethz.iks.slp.ServiceType;
import heesuk.percom.sherlock.io.kb.TreeFactory;
import heesuk.percom.sherlock.io.kb.sdp.SDPKBUtil;
import heesuk.percom.sherlock.io.msg.ModificationCandidate;

public class ModificationController {
	private static ModificationController _instance;
	private Locator locator;
	private ArrayList<String> scopes;
	private ArrayList<String> attrs;

	private ModificationController() {
		
		// get Locator instance
				try {
					locator = ServiceLocationManager.getLocator(new Locale("en"));
					
					// find all services of type "test" that have attribute "cool=yes"
					scopes = new ArrayList<String>();
					attrs = new ArrayList<String>();
					scopes.add("default");
					attrs.add("max-connections");
				} catch (ServiceLocationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				
	}

	public static ModificationController getInstance() {
		if (_instance == null) {
			_instance = new ModificationController();
		}

		return _instance;
	}

	/**
	 * 
	 */
	public void init() {
		// build and load SDP-relevant knowledge base
		long beforeKB = System.currentTimeMillis();
		SDPKBUtil.getInstance().buildKB();
		long afterKB = System.currentTimeMillis();
		ExperimentStat.getInstance().setKbLoadingTime(afterKB-beforeKB);
		//SDPKBUtil.getInstance().printStat();

		// build modification probability tree and modification sequence tree
		TreeFactory.getInstance().buildTree();
	}

	public void startMessageModification(int bound) {
		int cnt = 0;
		while(cnt++ < bound){
			long before = System.currentTimeMillis();
			//long after;
			
			//try {
				//ExperimentStat.getInstance().increaseExpRoundCnt();
				
			try {
				Thread.sleep(2);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
				Thread interactionT = new Thread(new InteractionRunnable(cnt, before));
				interactionT.start();
				/*sendModifiedMessage();
				after = System.currentTimeMillis();
				ExperimentStat.getInstance().setMsgTransTimeTotal(ExperimentStat.getInstance().getMsgTransTimeTotal()+(after-before));
			} catch (ServiceLocationException e) {
				System.out.println("["+cnt+":FAIL] ServiceLocationException!!");
				after = System.currentTimeMillis();
				//ExperimentStat.getInstance().setMsgTransTimeTotal(ExperimentStat.getInstance().getMsgTransTimeTotal()+(after-before));
				continue;
			} catch (SocketTimeoutException e) {
				System.out.println("["+cnt+":FAIL] SocketTimeoutException!!");
				after = System.currentTimeMillis();
				//ExperimentStat.getInstance().setMsgTransTimeTotal(ExperimentStat.getInstance().getMsgTransTimeTotal()+(after-before));
				continue;
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
				after = System.currentTimeMillis();
				//ExperimentStat.getInstance().setMsgTransTimeTotal(ExperimentStat.getInstance().getMsgTransTimeTotal()+(after-before));
				continue;
			}
			
			after = System.currentTimeMillis();
			ExperimentStat.getInstance().setMsgTransTimeTotal(ExperimentStat.getInstance().getMsgTransTimeTotal()+(after-before));
			break;
			*/
		}
		/*
		if(cnt == bound){
			System.out.println("["+cnt+":FAIL] No right sequence is found before the iteration bound..");
		}else{
			System.out.println("["+cnt+":SUCCESS] A successful interaction is made!!!");
		}
		*/
			
	}

	public void sendModifiedMessage() throws ServiceLocationException, SocketTimeoutException, IllegalArgumentException{
		// find service
		ServiceLocationEnumeration sle = locator.findServices(new ServiceType("service:test"), scopes, "(cool=yes)");
	}

	public void startSeqVerification(int bound) {
		for (int i = 0; i < bound; i++) {
			ModificationCandidate[] seq = TreeFactory.getInstance().getNextSequence();
			System.out.print("[returned sequence:" + i + "] ");
			for (int j = 0; j < seq.length; j++) {
				System.out.print(seq[j].toStringWithoutWeight() + "  ");
			}
			System.out.println();
		}
	}
	
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
				System.out.println("["+index+":SUCCESS] A reply message is returned!!");
				ExperimentStat.getInstance().setMsgTransTimeTotal(ExperimentStat.getInstance().getMsgTransTimeTotal()+(after-before));
			} catch (SocketTimeoutException e) {
				System.out.println("["+index+":FAIL] SocketTimeoutException!!");
				long after = System.currentTimeMillis();
				try {
					this.finalize();
				} catch (Throwable e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				//ExperimentStat.getInstance().setMsgTransTimeTotal(ExperimentStat.getInstance().getMsgTransTimeTotal()+(after-before));
				//e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (ServiceLocationException e) {
				e.printStackTrace();
			}
		}
		
	}
}
