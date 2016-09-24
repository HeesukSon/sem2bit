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
		TreeFactory.getInstance().printProbTree();
		TreeFactory.getInstance().localizeProbTree();
		TreeFactory.getInstance().printProbTree();
		TreeFactory.getInstance().computeModificationProb();
		TreeFactory.getInstance().printProbTree();
		System.out.println("\n####### Sorted Modification Candidates #######");
		ModificationCandidate[] candidates = TreeFactory.getInstance().getSortedCandidates();
		for(int i=0; i<candidates.length; i++){
			System.out.println(candidates[i].toString());
		}
		
	}
}
