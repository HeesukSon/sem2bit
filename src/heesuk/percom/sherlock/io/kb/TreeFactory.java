package heesuk.percom.sherlock.io.kb;

import heesuk.percom.sherlock.io.ExperimentStat;
import heesuk.percom.sherlock.io.kb.probtree.ModificationProbTree;
import heesuk.percom.sherlock.io.kb.sdp.SDPKBUtil;
import heesuk.percom.sherlock.io.kb.seqtree.ModificationSeqPlanTree;
import heesuk.percom.sherlock.io.msg.ModificationCandidate;

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
	
	public ModificationCandidate[] getNextSequence(){
		return this.seqPlanTree.getModSeq(0);
	}
	
	public void buildTree(){
		// modification probability tree
		long beforeProbTree = System.currentTimeMillis();
		this.probTree = new ModificationProbTree();
		this.probTree.init();
		this.probTree.localize(SDPKBUtil.getInstance().getLocalSDPName());
		this.probTree.computeWeights();
		
		ModificationCandidate[] candidates = this.probTree.getSortedCandidates();
		/*
		System.out.println("\n##### Sorted Modification Candidates #####");
		for(int i=0; i<candidates.length; i++){
			System.out.println(candidates[i].toString());
		}
		*/
		long afterProbTree = System.currentTimeMillis();
		ExperimentStat.getInstance().setProbTreeBuildTime(afterProbTree-beforeProbTree);
		
		// modification sequence planning tree
		this.seqPlanTree = new ModificationSeqPlanTree(candidates);
		long afterSeqPlanTree = System.currentTimeMillis();
		ExperimentStat.getInstance().setSeqTreeBuildTime(afterSeqPlanTree-afterProbTree);
	}
	
	// for preliminary result
	public boolean getRightSequence(int count){
		ModificationCandidate[] seq = this.seqPlanTree.getModSeq(count);
		boolean result = true;
		
		ModificationCandidate[] rightAnswer = new ModificationCandidate[8];
		rightAnswer[0] = new ModificationCandidate("DEFAULT", "[DEFAULT]");
		rightAnswer[1] = new ModificationCandidate("Language Code", "[D]");
		rightAnswer[2] = new ModificationCandidate("Control", "[L]");
		rightAnswer[3] = new ModificationCandidate("Control", "[V]");
		rightAnswer[4] = new ModificationCandidate("Length", "[L]");
		rightAnswer[5] = new ModificationCandidate("Char Encoding", "[D]");
		rightAnswer[6] = new ModificationCandidate("LANGUAGE_TAG_LENGTH", "[A]");
		rightAnswer[7] = new ModificationCandidate("LANGUAGE_TAG", "[A]");
		
		for(int i=0; i<seq.length; i++){
			System.out.print(seq[i].toStringWithoutWeight()+"  ");
			if(!seq[i].sameWith(rightAnswer[i]))
				result = false;				
		}
		System.out.println(" ("+result+")");
		
		return result;
	} 
	
	public void printProbTree(){
		this.probTree.printTree();
	}
	
	public void printSeqPlanTree(){
		
	}
}
