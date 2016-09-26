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
		this.probTree.localize(SDPKBUtil.getInstance().getLocalSDPName());
		this.probTree.computeWeights();
		
		ModificationCandidate[] candidates = this.probTree.getSortedCandidates();
		this.seqPlanTree = new ModificationSeqPlanTree(candidates);
		System.out.println("\n##### Sorted Modification Candidates #####");
		for(int i=0; i<candidates.length; i++){
			System.out.println(candidates[i].toString());
		}
		
		System.out.println("\n");
		System.out.println("##### Modification Sequence #####");
		for(int i=0; i<300; i++){
			System.out.print("["+i+"]");
			this.printSequence();
		}
	}
	
	public void printSequence(){
		ModificationCandidate[] seq = this.seqPlanTree.getModSeq();
		
		for(int i=0; i<seq.length; i++){
			System.out.print(seq[i].toStringWithoutWeight()+"  ");
		}
		System.out.println();
	}
	
	public void printProbTree(){
		this.probTree.printTree();
	}
	
	public void printSeqPlanTree(){
		
	}
}
