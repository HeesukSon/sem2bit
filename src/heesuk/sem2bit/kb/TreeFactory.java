package heesuk.sem2bit.kb;

import heesuk.sem2bit.ConfigUtil;
import heesuk.sem2bit.kb.probtree.IoTProtocolModificationProbTree;
import heesuk.sem2bit.kb.probtree.SDPModificationProbTree;
import heesuk.sem2bit.kb.protocol.enums.Domain;
import heesuk.sem2bit.exception.DomainNotDefinedException;
import heesuk.sem2bit.ExperimentStat;
import heesuk.sem2bit.exception.LocalProtocolNotSpecifiedException;
import heesuk.sem2bit.kb.probtree.ModificationProbTree;
import heesuk.sem2bit.kb.protocol.ProtocolKBUtil;
import heesuk.sem2bit.kb.protocol.iot.IoTProtocolKBUtil;
import heesuk.sem2bit.kb.protocol.sdp.SDPKBUtil;
import heesuk.sem2bit.kb.seqtree.ModificationSeqPlanTree;
import heesuk.sem2bit.msg.ModificationCandidate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TreeFactory {
	private static final Logger LOG = LoggerFactory.getLogger(TreeFactory.class);
	private static TreeFactory _instance;
	
	private ModificationProbTree probTree;
	private ModificationSeqPlanTree seqPlanTree;
	private ProtocolKBUtil kb;
	
	private TreeFactory(){
		
	}
	
	public static TreeFactory getInstance(){
		if(_instance == null){
			_instance = new TreeFactory();
		}
		
		return _instance;
	}
	
	public synchronized ModificationCandidate[] getNextSequence(){
		return this.seqPlanTree.getModSeq(0);
	}
	
	public void buildTree() throws DomainNotDefinedException, LocalProtocolNotSpecifiedException {
		Domain domain = ConfigUtil.getInstance().domain;
		long beforeProbTree;

		if(domain == Domain.SDP){
			this.kb = SDPKBUtil.getInstance();

			// modification probability tree
			beforeProbTree = System.currentTimeMillis();
			this.probTree = new SDPModificationProbTree();
			this.probTree.init();
		}else if(domain == Domain.IoT_Protocol){
			this.kb = IoTProtocolKBUtil.getInstance();

			// modification probability tree
			beforeProbTree = System.currentTimeMillis();
			this.probTree = new IoTProtocolModificationProbTree();
			this.probTree.init();
		}else{
			throw new DomainNotDefinedException();
		}

		if(kb.getLocalProtocolName() != null){
			this.probTree.localize(kb.getLocalProtocolName());
		}else{
			throw new LocalProtocolNotSpecifiedException();
		}

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
		
		return result;
	} 
	
	public void printProbTree(){
		this.probTree.printTree();
	}
	
	public void printSeqPlanTree(){
		
	}
}
