package heesuk.percom.sem2bit.kb.probtree;

import java.util.ArrayList;
import java.util.HashMap;

import heesuk.percom.sem2bit.ConfigUtil;
import heesuk.percom.sem2bit.kb.protocol.enums.Domain;
import heesuk.percom.sem2bit.kb.protocol.MessageFieldUpdate;
import heesuk.percom.sem2bit.kb.protocol.Protocol;
import heesuk.percom.sem2bit.kb.protocol.ProtocolKBUtil;
import heesuk.percom.sem2bit.kb.protocol.ProtocolMessage;
import heesuk.percom.sem2bit.kb.protocol.iot.IoTProtocolKBUtil;
import heesuk.percom.sem2bit.kb.protocol.sdp.SDPKBUtil;
import heesuk.percom.sem2bit.kb.protocol.enums.Functionality;
import heesuk.percom.sem2bit.kb.protocol.enums.MessageFieldType;
import heesuk.percom.sem2bit.kb.protocol.enums.RequirementChange;
import heesuk.percom.sem2bit.kb.protocol.enums.ProtocolName;
import heesuk.percom.sem2bit.kb.protocol.enums.UpdatePattern;
import heesuk.percom.sem2bit.msg.ModificationCandidate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ModificationProbTree {
	private static final Logger LOG = LoggerFactory.getLogger(ModificationProbTree.class);

	private ProbTreeNode root;
	private ProtocolKBUtil kb;

	public ModificationProbTree() {
		this.root = new ProbTreeNode("ROOT", 1);
		if(ConfigUtil.getInstance().domain == Domain.SDP){
			this.kb = SDPKBUtil.getInstance();
		}else if(ConfigUtil.getInstance().domain == Domain.IoT_Protocol){
			this.kb = IoTProtocolKBUtil.getInstance();
		}else{
			this.kb = null;
		}
		init();
	}
	
	public ModificationCandidate[] getSortedCandidates(){
		ArrayList<ModificationCandidate> list = new ArrayList<ModificationCandidate>();
		computeCandidateProb(this.root, 1f, list);
		LOG.info("Computing each modification candidate's probability is done.");

		sortCandidates(list);
		LOG.info("Sorting the modification candidate list is done.");
		LOG.info("Modification candidate list size : {}", list.size());

		ModificationCandidate[] arr = new ModificationCandidate[list.size()];
		return list.toArray(arr);
	}
	
	public void sortCandidates(ArrayList<ModificationCandidate> list){
		for(int i=0; i<list.size()-1; i++){
			for(int j=i+1; j<list.size(); j++){
				ModificationCandidate max = list.get(i);
				ModificationCandidate cur = list.get(j);
				
				if(max.getProb() < cur.getProb()){
					ModificationCandidate tmp = max;
					list.set(i, cur);
					list.set(j, max);
				}
			}
		}
	}
	
	private void computeCandidateProb(ProbTreeNode node, float prob, ArrayList<ModificationCandidate> list){
		if(node.getDepth()==4){
			for(ProbTreeEdge out : node.getOutEdges()){
				String field = kb.getLocalProtocol().getMessage().getFieldName(node.getLabel());
				String update = out.getNext().getLabel();
				float final_prob = prob*out.getWeight();
				ModificationCandidate candidate = new ModificationCandidate(field, update, final_prob);
				list.add(candidate);
			}
		}else{
			for(ProbTreeEdge out : node.getOutEdges()){
				computeCandidateProb(out.getNext(), prob*out.getWeight(), list);
			}
		}
	}

	public void localize(ProtocolName pName) {
		// (1) prune Service Search
		this.root.removeChild(Functionality.SERVICE_SEARCH.toString());
		
		// (2) prune meaningless field updates
		this.pruneMeaninglessUpdates(pName, this.root);
	}
	
	public void pruneMeaninglessUpdates(ProtocolName pName, ProbTreeNode node){
		for(ProbTreeEdge out : node.getOutEdges()){
			if(out.getNext().getDepth() == 4){
				Protocol p = kb.getProtocol(pName);
				ProtocolMessage msg = p.getMessage();
				
				if(msg.contains(out.getNext().getLabel())){
					// (2-1) if D-4 field is present, delete D-5 node, [A]
					out.getNext().removeChild(UpdatePattern.ADD_NEW_FIELD.toString());
				}else{
					// (2-2) if D-4 field is not present, delete D-5 nodes, [D], [L], [V]
					out.getNext().removeChild(UpdatePattern.DELETE_FIELD.toString());
					out.getNext().removeChild(UpdatePattern.CHANGE_FIELD_LENGTH.toString());
					out.getNext().removeChild(UpdatePattern.CHANGE_VOCA.toString());
				}
			}else{
				pruneMeaninglessUpdates(pName, out.getNext());
			}
		}
	}

	public void computeWeights() {
		this.computeWeight_1_2();
		this.computeWeight_2_3();
		this.computeWeight_3_4();
		this.computeWeight_4_5();
		
		LOG.info("Weight value computation for modification probability tree is done.");
	}
	
	private void computeWeight_1_2(){
		int cp_cnt = 0;
		int mh_cnt = 0;
		
		for(MessageFieldUpdate update : kb.getUpdateHistory()){
			if(update.getFunc().equals(Functionality.CONTENT_PARSING)){
				cp_cnt++;
			}else if(update.getFunc().equals(Functionality.MESSAGE_HANDLING)){
				mh_cnt++;
			}
		}
		
		this.root.updateOutEdgeWeight(Functionality.CONTENT_PARSING.toString(), (float)cp_cnt/(cp_cnt+mh_cnt));
		this.root.updateOutEdgeWeight(Functionality.MESSAGE_HANDLING.toString(), (float)mh_cnt/(cp_cnt+mh_cnt));
	}
	
	private void computeWeight_2_3(){
		HashMap<String, Integer> evidence = new HashMap<String, Integer>();
		HashMap<String, Integer> candidate = new HashMap<String, Integer>();
		
		updateEvidence(this.root, evidence);
		updateCandidate(this.root, candidate);
		
		updateReqWeight(this.root, evidence, candidate);
	}
	
	private void updateReqWeight(ProbTreeNode node, HashMap<String, Integer> evidence, HashMap<String, Integer> candidate){
		if(node.getDepth()==2){
			int cnt = 0;
			
			// computation of the overall count for the depth-2 node 
			for(ProbTreeEdge out : node.getOutEdges()){
				cnt += evidence.get(out.getNext().getLabel());
				cnt += candidate.get(out.getNext().getLabel());
			}
			
			// value update
			for(ProbTreeEdge out : node.getOutEdges()){
				int evi = evidence.get(out.getNext().getLabel());
				int can = candidate.get(out.getNext().getLabel());
				node.updateOutEdgeWeight(out.getNext().getLabel(), (float)(evi+can)/(float)cnt);
			}
		}else{
			for(ProbTreeEdge out : node.getOutEdges()){
				updateReqWeight(out.getNext(), evidence, candidate);
			}
		}
	}
	
	private void updateEvidence(ProbTreeNode node, HashMap<String, Integer> evidence){
		if(node.getDepth()==3){
			evidence.put(node.getLabel(), kb.getRequirementChangeCount(node.getLabel()));
		}else{
			for(ProbTreeEdge out : node.getOutEdges()){
				updateCandidate(out.getNext(), evidence);
			}
		}
	}
	
	private void updateCandidate(ProbTreeNode node, HashMap<String, Integer> candidate){
		if(node.getDepth()==3){
			candidate.put(node.getLabel(), node.getLeafNodeNum());
		}else{
			for(ProbTreeEdge out : node.getOutEdges()){
				updateEvidence(out.getNext(), candidate);
			}
		}
	}
	
	private void computeWeight_3_4(){
		computeWeight_3_4(this.root);
	}
	
	private void computeWeight_3_4(ProbTreeNode node){
		if(node.getDepth()==3){
			for(ProbTreeEdge out : node.getOutEdges()){
				node.updateOutEdgeWeight(out.getNext().getLabel(), 1);
			}
		}else{
			for(ProbTreeEdge out : node.getOutEdges()){
				computeWeight_3_4(out.getNext());
			}
		}
	}
	
	private void computeWeight_4_5(){
		computeWeight_4_5(this.root);
	}
	
	private void computeWeight_4_5(ProbTreeNode node){
		if(node.getDepth()==4){
			for(ProbTreeEdge out : node.getOutEdges()){
				if(out.getNext().getLabel().equals(UpdatePattern.ADD_NEW_FIELD.toString())){
					float patternProb = kb.getUpdatePatternProb(UpdatePattern.ADD_NEW_FIELD.toString());
					float fieldExProb = kb.getFieldExProb(node.getLabel());
					node.updateOutEdgeWeight(out.getNext().getLabel(), patternProb*fieldExProb);
				}else if(out.getNext().getLabel().equals(UpdatePattern.DELETE_FIELD.toString())){
					float patternProb = kb.getUpdatePatternProb(UpdatePattern.DELETE_FIELD.toString());
					float fieldExProb = kb.getFieldExProb(node.getLabel());
					node.updateOutEdgeWeight(out.getNext().getLabel(), patternProb*(1-fieldExProb));
				}else if(out.getNext().getLabel().equals(UpdatePattern.CHANGE_FIELD_LENGTH.toString())){
					float patternProb = kb.getUpdatePatternProb(UpdatePattern.CHANGE_FIELD_LENGTH.toString());
					node.updateOutEdgeWeight(out.getNext().getLabel(), patternProb);
				}else{
					float patternProb = kb.getUpdatePatternProb(UpdatePattern.CHANGE_VOCA.toString());
					node.updateOutEdgeWeight(out.getNext().getLabel(), patternProb);
				}
			}
		}else{
			for(ProbTreeEdge out : node.getOutEdges()){
				computeWeight_4_5(out.getNext());
			}
		}
	}

	public void printTree() {
		LOG.info("\n################# PRINT MODIFICATION PROBABILITY TREE #################");
		printTree("",this.root);
	}

	private void printTree(String prev, ProbTreeNode node) {
		if (node.getLabel().equals("ROOT")) {
			// root node
			for (ProbTreeEdge out : node.getOutEdges()) {
				printTree(node.getLabel()+"--("+out.getWeight()+")-->"+out.getNext().getLabel(), out.getNext());
			}
		} else if (node.getOutEdges().length==0) {
			// leaf node
			LOG.info("{} (END)",prev);
		} else {
			// intermediate nodes
			for (ProbTreeEdge out : node.getOutEdges()) {
				printTree(prev+"--("+out.getWeight()+")-->"+out.getNext().getLabel(), out.getNext());
			}
		}
	}

	public void init() {
		this.addTreeNodes();
		//this.computeWeights();
	}

	public void addTreeNodes() {
		// Depth 1 -> Depth 2
		root.addChild(Functionality.SERVICE_SEARCH.toString());
		root.addChild(Functionality.CONTENT_PARSING.toString());
		root.addChild(Functionality.MESSAGE_HANDLING.toString());

		// Depth 2 -> Depth 3
		root.getChild(Functionality.CONTENT_PARSING.toString())
				.addChild(RequirementChange.MULTI_QUERY_SUPPORT.toString());
		root.getChild(Functionality.CONTENT_PARSING.toString())
				.addChild(RequirementChange.CONTENT_LENGTH_CHANGE.toString());
		root.getChild(Functionality.CONTENT_PARSING.toString())
				.addChild(RequirementChange.ENCODING_INTEGRATION.toString());
		root.getChild(Functionality.CONTENT_PARSING.toString())
				.addChild(RequirementChange.MULTI_ENCODING_SUPPORT.toString());
		root.getChild(Functionality.CONTENT_PARSING.toString())
				.addChild(RequirementChange.LANGUAGE_SUPPORT_CHANGE.toString());

		root.getChild(Functionality.MESSAGE_HANDLING.toString())
				.addChild(RequirementChange.SECURITY_REQUIREMENT_CHANGE.toString());
		root.getChild(Functionality.MESSAGE_HANDLING.toString())
				.addChild(RequirementChange.CONTROL_OPTION_ADDITION.toString());
		root.getChild(Functionality.MESSAGE_HANDLING.toString())
				.addChild(RequirementChange.SESSION_MGMT_CHANGE.toString());

		// Depth 3 -> Depth 4
		root.getChild(Functionality.CONTENT_PARSING.toString())
				.getChild(RequirementChange.MULTI_QUERY_SUPPORT.toString())
				.addChild(MessageFieldType.QUERY_COUNT.toString());
		root.getChild(Functionality.CONTENT_PARSING.toString())
				.getChild(RequirementChange.MULTI_QUERY_SUPPORT.toString())
				.addChild(MessageFieldType.ANSWER_COUNT.toString());
		root.getChild(Functionality.CONTENT_PARSING.toString())
				.getChild(RequirementChange.CONTENT_LENGTH_CHANGE.toString())
				.addChild(MessageFieldType.MESSAGE_LENGTH.toString());
		root.getChild(Functionality.CONTENT_PARSING.toString())
				.getChild(RequirementChange.ENCODING_INTEGRATION.toString())
				.addChild(MessageFieldType.ENCODING.toString());
		root.getChild(Functionality.CONTENT_PARSING.toString())
				.getChild(RequirementChange.MULTI_ENCODING_SUPPORT.toString())
				.addChild(MessageFieldType.ENCODING.toString());
		root.getChild(Functionality.CONTENT_PARSING.toString())
				.getChild(RequirementChange.LANGUAGE_SUPPORT_CHANGE.toString())
				.addChild(MessageFieldType.LANGUAGE_CODE.toString());
		root.getChild(Functionality.CONTENT_PARSING.toString())
				.getChild(RequirementChange.LANGUAGE_SUPPORT_CHANGE.toString())
				.addChild(MessageFieldType.LANGUAGE_TAG.toString());
		root.getChild(Functionality.CONTENT_PARSING.toString())
				.getChild(RequirementChange.LANGUAGE_SUPPORT_CHANGE.toString())
				.addChild(MessageFieldType.LANGUAGE_TAG_LENGTH.toString());

		root.getChild(Functionality.MESSAGE_HANDLING.toString())
				.getChild(RequirementChange.SECURITY_REQUIREMENT_CHANGE.toString())
				.addChild(MessageFieldType.CONTROL_FLAG.toString());
		root.getChild(Functionality.MESSAGE_HANDLING.toString())
				.getChild(RequirementChange.CONTROL_OPTION_ADDITION.toString())
				.addChild(MessageFieldType.CONTROL_FLAG.toString());
		root.getChild(Functionality.MESSAGE_HANDLING.toString())
				.getChild(RequirementChange.SESSION_MGMT_CHANGE.toString())
				.addChild(MessageFieldType.SESSION_MGMT.toString());

		// Depth 4 -> Depth 5
		root.getChild(Functionality.CONTENT_PARSING.toString())
				.getChild(RequirementChange.MULTI_QUERY_SUPPORT.toString())
				.getChild(MessageFieldType.QUERY_COUNT.toString()).addChild(UpdatePattern.ADD_NEW_FIELD.toString());
		root.getChild(Functionality.CONTENT_PARSING.toString())
				.getChild(RequirementChange.MULTI_QUERY_SUPPORT.toString())
				.getChild(MessageFieldType.QUERY_COUNT.toString()).addChild(UpdatePattern.DELETE_FIELD.toString());
		root.getChild(Functionality.CONTENT_PARSING.toString())
				.getChild(RequirementChange.MULTI_QUERY_SUPPORT.toString())
				.getChild(MessageFieldType.QUERY_COUNT.toString())
				.addChild(UpdatePattern.CHANGE_FIELD_LENGTH.toString());

		root.getChild(Functionality.CONTENT_PARSING.toString())
				.getChild(RequirementChange.MULTI_QUERY_SUPPORT.toString())
				.getChild(MessageFieldType.ANSWER_COUNT.toString()).addChild(UpdatePattern.ADD_NEW_FIELD.toString());
		root.getChild(Functionality.CONTENT_PARSING.toString())
				.getChild(RequirementChange.MULTI_QUERY_SUPPORT.toString())
				.getChild(MessageFieldType.ANSWER_COUNT.toString()).addChild(UpdatePattern.DELETE_FIELD.toString());
		root.getChild(Functionality.CONTENT_PARSING.toString())
				.getChild(RequirementChange.MULTI_QUERY_SUPPORT.toString())
				.getChild(MessageFieldType.ANSWER_COUNT.toString())
				.addChild(UpdatePattern.CHANGE_FIELD_LENGTH.toString());

		root.getChild(Functionality.CONTENT_PARSING.toString())
				.getChild(RequirementChange.CONTENT_LENGTH_CHANGE.toString())
				.getChild(MessageFieldType.MESSAGE_LENGTH.toString()).addChild(UpdatePattern.ADD_NEW_FIELD.toString());
		root.getChild(Functionality.CONTENT_PARSING.toString())
				.getChild(RequirementChange.CONTENT_LENGTH_CHANGE.toString())
				.getChild(MessageFieldType.MESSAGE_LENGTH.toString()).addChild(UpdatePattern.DELETE_FIELD.toString());
		root.getChild(Functionality.CONTENT_PARSING.toString())
				.getChild(RequirementChange.CONTENT_LENGTH_CHANGE.toString())
				.getChild(MessageFieldType.MESSAGE_LENGTH.toString())
				.addChild(UpdatePattern.CHANGE_FIELD_LENGTH.toString());

		root.getChild(Functionality.CONTENT_PARSING.toString())
				.getChild(RequirementChange.ENCODING_INTEGRATION.toString())
				.getChild(MessageFieldType.ENCODING.toString()).addChild(UpdatePattern.DELETE_FIELD.toString());

		root.getChild(Functionality.CONTENT_PARSING.toString())
				.getChild(RequirementChange.MULTI_ENCODING_SUPPORT.toString())
				.getChild(MessageFieldType.ENCODING.toString()).addChild(UpdatePattern.ADD_NEW_FIELD.toString());

		root.getChild(Functionality.CONTENT_PARSING.toString())
				.getChild(RequirementChange.LANGUAGE_SUPPORT_CHANGE.toString())
				.getChild(MessageFieldType.LANGUAGE_CODE.toString()).addChild(UpdatePattern.ADD_NEW_FIELD.toString());
		root.getChild(Functionality.CONTENT_PARSING.toString())
				.getChild(RequirementChange.LANGUAGE_SUPPORT_CHANGE.toString())
				.getChild(MessageFieldType.LANGUAGE_CODE.toString()).addChild(UpdatePattern.DELETE_FIELD.toString());
		root.getChild(Functionality.CONTENT_PARSING.toString())
				.getChild(RequirementChange.LANGUAGE_SUPPORT_CHANGE.toString())
				.getChild(MessageFieldType.LANGUAGE_CODE.toString()).addChild(UpdatePattern.CHANGE_VOCA.toString());

		root.getChild(Functionality.CONTENT_PARSING.toString())
				.getChild(RequirementChange.LANGUAGE_SUPPORT_CHANGE.toString())
				.getChild(MessageFieldType.LANGUAGE_TAG.toString()).addChild(UpdatePattern.ADD_NEW_FIELD.toString());
		root.getChild(Functionality.CONTENT_PARSING.toString())
				.getChild(RequirementChange.LANGUAGE_SUPPORT_CHANGE.toString())
				.getChild(MessageFieldType.LANGUAGE_TAG.toString()).addChild(UpdatePattern.DELETE_FIELD.toString());
		root.getChild(Functionality.CONTENT_PARSING.toString())
				.getChild(RequirementChange.LANGUAGE_SUPPORT_CHANGE.toString())
				.getChild(MessageFieldType.LANGUAGE_TAG.toString()).addChild(UpdatePattern.CHANGE_VOCA.toString());

		root.getChild(Functionality.CONTENT_PARSING.toString())
				.getChild(RequirementChange.LANGUAGE_SUPPORT_CHANGE.toString())
				.getChild(MessageFieldType.LANGUAGE_TAG_LENGTH.toString())
				.addChild(UpdatePattern.ADD_NEW_FIELD.toString());

		root.getChild(Functionality.MESSAGE_HANDLING.toString())
				.getChild(RequirementChange.SECURITY_REQUIREMENT_CHANGE.toString())
				.getChild(MessageFieldType.CONTROL_FLAG.toString()).addChild(UpdatePattern.CHANGE_VOCA.toString());

		root.getChild(Functionality.MESSAGE_HANDLING.toString())
				.getChild(RequirementChange.CONTROL_OPTION_ADDITION.toString())
				.getChild(MessageFieldType.CONTROL_FLAG.toString()).addChild(UpdatePattern.CHANGE_VOCA.toString());
		root.getChild(Functionality.MESSAGE_HANDLING.toString())
				.getChild(RequirementChange.CONTROL_OPTION_ADDITION.toString())
				.getChild(MessageFieldType.CONTROL_FLAG.toString())
				.addChild(UpdatePattern.CHANGE_FIELD_LENGTH.toString());

		root.getChild(Functionality.MESSAGE_HANDLING.toString())
				.getChild(RequirementChange.SESSION_MGMT_CHANGE.toString())
				.getChild(MessageFieldType.SESSION_MGMT.toString()).addChild(UpdatePattern.ADD_NEW_FIELD.toString());
		root.getChild(Functionality.MESSAGE_HANDLING.toString())
				.getChild(RequirementChange.SESSION_MGMT_CHANGE.toString())
				.getChild(MessageFieldType.SESSION_MGMT.toString()).addChild(UpdatePattern.DELETE_FIELD.toString());
		root.getChild(Functionality.MESSAGE_HANDLING.toString())
				.getChild(RequirementChange.SESSION_MGMT_CHANGE.toString())
				.getChild(MessageFieldType.SESSION_MGMT.toString())
				.addChild(UpdatePattern.CHANGE_FIELD_LENGTH.toString());
	}
}
