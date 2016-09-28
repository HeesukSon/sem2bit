package heesuk.percom.sherlock.io;

import heesuk.percom.sherlock.io.kb.TreeFactory;
import heesuk.percom.sherlock.io.kb.sdp.SDPKBUtil;
import heesuk.percom.sherlock.io.msg.ModificationCandidate;

public class ModificationController {
	private static ModificationController _instance;
	private Thread interactionMonitorT;
	
	private ModificationController(){
		this.interactionMonitorT = new Thread(new InteractionMonitorJob());
	}
	
	public static ModificationController getInstance(){
		if(_instance == null){
			_instance = new ModificationController();
		}
		
		return _instance;
	}
	
	/**
	 * 
	 */
	public void init(){
		// build and load SDP-relevant knowledge base
		SDPKBUtil.getInstance().buildKB();
		SDPKBUtil.getInstance().printStat();
		
		// build modification probability tree and modification sequence tree
		TreeFactory.getInstance().buildTree();	
		// TODO this.interactionMonitorT.run();
	}
	
	public void startSeqVerification(int bound){
		for(int i=0; i<bound; i++){
			ModificationCandidate[] seq = TreeFactory.getInstance().getNextSequence();
			System.out.print("[returned sequence:"+i+"] ");
			for(int j=0; j<seq.length; j++){
				System.out.print(seq[j].toStringWithoutWeight()+"  ");
			}
			System.out.println();
		}	
	}
	
	public class InteractionMonitorJob implements Runnable{
		@Override
		public void run() {
			// (1) get the next modified message
			// (2) transmit the returned message to the target host
			// (3) wait for a certain interaction timeout TODO: timeout should be defined 
			// if timeout:
			// -> go back to (1)
			// otherwise (a reply message is returned):
			// -> (4) send a notification to the upper-level application
			// (5) terminate TODO: 'what is the termination signal?'
		}
	}
}
