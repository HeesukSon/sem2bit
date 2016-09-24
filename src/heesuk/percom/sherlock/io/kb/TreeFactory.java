package heesuk.percom.sherlock.io.kb;

import heesuk.percom.sherlock.io.kb.probtree.ModificationCandidate;
import heesuk.percom.sherlock.io.kb.probtree.ModificationProbTree;
import heesuk.percom.sherlock.io.kb.sdp.SDPKBUtil;
import heesuk.percom.sherlock.io.kb.seqtree.ModificationSeqPlanTree;

public class TreeFactory {
	private static TreeFactory _instance;
	
	private ModificationProbTree probTree;
	private ModificationSeqPlanTree seqPlanTree;
	
	private TreeFactory(){
		
	}
	
	public static TreeFactory getInstance(){
		if(_instance == null){
			_instance = new TreeFactory();
		}
		
		return _instance;
	}
	
	public void buildTree(){
		this.probTree = new ModificationProbTree();
		this.probTree.init();
		//this.seqPlanTree = new ModificationSeqPlanTree();
		//this.seqPlanTree.prune();
	}
	
	public void localizeProbTree(){
		this.probTree.localize(SDPKBUtil.getInstance().getLocalSDPName());
	}
	
	public void computeModificationProb(){
		this.probTree.computeWeights();
	}
	
	public void printProbTree(){
		this.probTree.printTree();
	}
	
	public ModificationCandidate[] getSortedCandidates(){
		return this.probTree.getSortedCandidates();
	}
	
	public void printSeqPlanTree(){
		
	}
}
