package heesuk.percom.sherlock.io;

import heesuk.percom.sherlock.io.kb.TreeFactory;
import heesuk.percom.sherlock.io.kb.probtree.ModificationCandidate;
import heesuk.percom.sherlock.io.kb.sdp.SDPKBUtil;

public class ModificationController {
	private static ModificationController _instance;
	
	private ModificationController(){
		
	}
	
	public static ModificationController getInstance(){
		if(_instance == null){
			_instance = new ModificationController();
		}
		
		return _instance;
	}
	
	public void init(){
		SDPKBUtil.getInstance().buildKB();
		SDPKBUtil.getInstance().printStat();
		
		TreeFactory.getInstance().buildTree();	
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
}
