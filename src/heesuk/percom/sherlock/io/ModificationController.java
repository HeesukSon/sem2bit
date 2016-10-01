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

	private ModificationController() {
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
			long after;
			try {
				ExperimentStat.getInstance().increaseExpRoundCnt();
				this.sendModifiedMessage();
				after = System.currentTimeMillis();
				//ExperimentStat.getInstance().setMsgTransTimeTotal(ExperimentStat.getInstance().getMsgTransTimeTotal()+(after-before));
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
			//ExperimentStat.getInstance().setMsgTransTimeTotal(ExperimentStat.getInstance().getMsgTransTimeTotal()+(after-before));
			break;
		}
		
		if(cnt == bound){
			System.out.println("["+cnt+":FAIL] No right sequence is found before the iteration bound..");
		}else{
			System.out.println("["+cnt+":SUCCESS] A successful interaction is made!!!");
		}	
	}

	public void sendModifiedMessage() throws ServiceLocationException, SocketTimeoutException, IllegalArgumentException{
		// get Locator instance
		Locator locator = ServiceLocationManager.getLocator(new Locale("en"));

		// find all services of type "test" that have attribute "cool=yes"
		ArrayList<String> scopes = new ArrayList<String>();
		ArrayList<String> attrs = new ArrayList<String>();
		scopes.add("default");
		attrs.add("max-connections");

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
}
