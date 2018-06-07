package heesuk.sem2bit.kb.seqtree;

import java.util.ArrayList;

import heesuk.sem2bit.ConfigUtil;
import heesuk.sem2bit.kb.protocol.enums.Domain;
import heesuk.sem2bit.kb.protocol.ProtocolKBUtil;
import heesuk.sem2bit.kb.protocol.enums.UpdatePattern;
import heesuk.sem2bit.kb.protocol.iot.IoTProtocolKBUtil;
import heesuk.sem2bit.kb.protocol.sdp.SDPKBUtil;
import heesuk.sem2bit.msg.ModificationCandidate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ModificationSeqPlanTree {
	private static final Logger LOG = LoggerFactory.getLogger(ModificationSeqPlanTree.class);

	private SeqTreeNode root;
	private ModificationCandidate[] candidates;
	private ProtocolKBUtil kb;

	public ModificationSeqPlanTree() {
		this.root = new SeqTreeNode(1);
		if(ConfigUtil.getInstance().domain == Domain.SDP){
			this.kb = SDPKBUtil.getInstance();
		}else if(ConfigUtil.getInstance().domain == Domain.IoT_Protocol){
			this.kb = IoTProtocolKBUtil.getInstance();
		}else{
			this.kb = null;
		}
	}

	public ModificationSeqPlanTree(ModificationCandidate[] candidates) {
		this();
		this.candidates = new ModificationCandidate[candidates.length];
		for (int i = 0; i < candidates.length; i++) {
			this.candidates[i] = candidates[i];
		}
	}

	public synchronized ModificationCandidate[] getModSeq(int count) {
		ArrayList<ModificationCandidate> candidates = new ArrayList<ModificationCandidate>();
		for (int i = 0; i < this.candidates.length; i++) {
			candidates.add(this.candidates[i]);
		}
		// return getModSeq(this.root, candidates, new
		// ArrayList<ModificationCandidate>());
		return getModSeq(count, this.root, candidates, new ArrayList<ModificationCandidate>());
	}

	public synchronized ModificationCandidate[] getModSeq(int count, SeqTreeNode node, ArrayList<ModificationCandidate> candidates,
			ArrayList<ModificationCandidate> seq) {
		
		if (/* at the bound depth: base case */node.getDepth() == kb.getModSeqBound() + 1) {
			// add the current node to the sequence
			seq.add(node.getItem());
			ModificationCandidate[] result = new ModificationCandidate[seq.size()];
			node.setDead(true);
			
			return seq.toArray(result);
		} else /* before the bound depth: intermediate nodes */ {
			// add the current node to seq
			seq.add(node.getItem());
			this.removeThisNode(node, candidates);
			// prune useless candidates accordingly
			ArrayList<ModificationCandidate> original = new ArrayList<ModificationCandidate>();
			for(int i=0; i<candidates.size(); i++){
				original.add(new ModificationCandidate(candidates.get(i)));
			}
			this.pruneCandidates(node, candidates);
			
			if (/* no child */node.getChildren().size() == 0) {
				// add the candidate with the highest prob. as a child
				node.addChild(new SeqTreeNode(candidates.get(0)));
				
				// remove the added node from candidates
				candidates.remove(0);
				
				return getModSeq(count, node.getRightMostChild(), candidates, seq);
			} else /* child exists */ {
				if (/* current node has further sequences */node.hasMoreSequence(candidates)) {
					if (/* the right-most-child has further sequences */
							!node.getRightMostChild().isDead() && node.getRightMostChild()
							.hasMoreSequence(candidates)) {
						if (/* right before the bound depth */ node.getDepth() == kb
								.getModSeqBound()) {
							if (/* has more to add */ node.hasMoreSequence(candidates)) {

								// add the candidate with the highest prob. which was not added as a child before
								int size = candidates.size();
								for (int i = 0; i < size; i++) {
									if (!node.hasChild(candidates.get(i))) {
										node.addChild(new SeqTreeNode(candidates.get(i)));
										// remove the added node from candidates
										candidates.remove(i);
										size--;
										i--;
										break;
									}
								}

								return getModSeq(count, node.getRightMostChild(), candidates, seq);
							} else {
								// mark the current node as dead
								node.setDead(true);
								// remove current node from the sequence
								seq.remove(seq.size() - 1);
								seq.remove(seq.size() - 1);
								// recover the pruned candidates
								//candidates = original;
								candidates.clear();
								return getModSeq(count, node.getParent(), original, seq);
							}
						} else {
							return getModSeq(count, node.getRightMostChild(), candidates, seq);
						}
					} else /* new child need to be added */ {
						// add the candidate with the highest prob. which was not added as a child before
						int size = candidates.size();
						for (int i = 0; i < size; i++) {
							if (!node.hasChild(candidates.get(i))) {
								node.addChild(new SeqTreeNode(candidates.get(i)));
								// remove the added node from candidates
								candidates.remove(i);
								size--;
								i--;
								break;
							}
						}

						return getModSeq(count, node.getRightMostChild(), candidates, seq);
					}
				} else /* this node has no more sequence */ {
					try{
						// mark this node as dead
						node.setDead(true);
						// remove current node from the sequence
						seq.remove(seq.size() - 1);
						seq.remove(seq.size() - 1);
						// recover the pruned candidates

						candidates.clear();
						original.add(node.getItem());
						return getModSeq(count, node.getParent(), original, seq);
					}catch(ArrayIndexOutOfBoundsException e){
						LOG.info("No more sequence can be composed from the current tree.");
						LOG.info("Next sequences will be computed with different seqBound value.");

						kb.computeModSeqBound();

						this.root = new SeqTreeNode(1);
						return getModSeq(0);
					}
				}
			}
		}
	}

	// HEURISTICS-based pruning
	private void pruneCandidates(SeqTreeNode node, ArrayList<ModificationCandidate> candidates) {
		this.removeFutureCandidateWithNoEffect(node, candidates);
		this.removeDuplicateCandidates(node, candidates);
	}

	private void removeThisNode(SeqTreeNode node, ArrayList<ModificationCandidate> candidates) {
		int size = candidates.size();
		for (int i = 0; i < size; i++) {

			if (node.getItem().sameWith(candidates.get(i))) {
				candidates.remove(i);
				size--;
				i--;
			}

		}
	}

	// HEURISTICS NO.1: remove modifications after a deletion
	private void removeFutureCandidateWithNoEffect(SeqTreeNode node, ArrayList<ModificationCandidate> candidates) {
		int size = candidates.size();
		for (int i = 0; i < size; i++) {
			if (node.getItem().getUpdate().equals(UpdatePattern.DELETE_FIELD.toString())
					&& node.getItem().getField().equals(candidates.get(i).getField())) {
				candidates.remove(i);
				size--;
				i--;
			}
		}
	}

	// HEURISTICS NO.2: remove duplicates
	private void removeDuplicateCandidates(SeqTreeNode node, ArrayList<ModificationCandidate> candidates) {
		int size = candidates.size();
		for (int i = 0; i < size - 1; i++) {
			for (int j = i + 1; j < size; j++) {
				if (candidates.get(i).sameWith(candidates.get(j))) {
					candidates.remove(j);
					size--;
					j--;
				}
			}
		}
	}
}
