package heesuk.percom.sem2bit.kb.seqtree;

import java.util.ArrayList;
import java.util.List;

import heesuk.percom.sem2bit.kb.sdp.SDPKBUtil;
import heesuk.percom.sem2bit.kb.sdp.enums.UpdatePattern;
import heesuk.percom.sem2bit.msg.ModificationCandidate;

public class ModificationSeqPlanTree {
	private SeqTreeNode root;
	private ModificationCandidate[] candidates;

	public ModificationSeqPlanTree() {
		this.root = new SeqTreeNode(1);
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
		
		if (/* at the bound depth: base case */node.getDepth() == SDPKBUtil.getInstance().getModSeqBound() + 1) {
			//System.out.println("[Bottom line] node = "+node.getItem().toStringWithoutWeight());
			// add the current node to the sequence
			seq.add(node.getItem());
			ModificationCandidate[] result = new ModificationCandidate[seq.size()];
			// mark this node as dead
			node.setDead(true);
			// return the result sequence
			return seq.toArray(result);
		} else /* before the bound depth: intermediate nodes */ {
			//System.out.println("[Before the bottom line] node = "+node.getItem().toStringWithoutWeight()+", candidates.length = "+candidates.size());
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
				//System.out.println("[No child exist]");
				// add the candidate with the highest prob. as a child
				node.addChild(new SeqTreeNode(candidates.get(0)));
				
				//System.out.println("[Child added] child = "+node.getRightMostChild().getItem().toStringWithoutWeight());
				// remove the added node from candidates
				candidates.remove(0);
				
				return getModSeq(count, node.getRightMostChild(), candidates, seq);
			} else /* child exists */ {
				// System.out.println("[A child exist]");
				if (/* current node has further sequences */node.hasMoreSequence(candidates)) {
					//System.out.println("[Current node has further sequences]");
					if (/* the right-most-child has further sequences */!node.getRightMostChild().isDead() && node.getRightMostChild()
							.hasMoreSequence(candidates)) {
						if (/* right before the bound depth */ node.getDepth() == SDPKBUtil.getInstance()
								.getModSeqBound()) {
							//System.out.println("[Right before the bound depth] children size = "+node.getChildren().size()+", candidate size = "+candidates.size());
							if (/* has more to add */ node.hasMoreSequence(candidates)) {
								//System.out.println("[Has more to add]");

								// add the candidate with the highest prob. which was not added as a child before
								int size = candidates.size();
								for (int i = 0; i < size; i++) {
									if (!node.hasChild(candidates.get(i))) {
										node.addChild(new SeqTreeNode(candidates.get(i)));
										//System.out.println("[Child added] child = "+node.getRightMostChild().getItem().toStringWithoutWeight());
										// remove the added node from candidates
										candidates.remove(i);
										size--;
										i--;
										break;
									}
								}

								return getModSeq(count, node.getRightMostChild(), candidates, seq);
							} else {
								//System.out.println("[Nothing to add]");
								// mark the current node as dead
								node.setDead(true);
								// remove current node from the sequence
								seq.remove(seq.size() - 1);
								seq.remove(seq.size() - 1);
								// recover the pruned candidates
								//candidates = original;
								candidates.clear();
								//System.out.println("[Going back to the parent");
								return getModSeq(count, node.getParent(), original, seq);
							}
						} else {
							//System.out.println("[Right-most-node has further sequences] r-m-n = "+node.getRightMostChild().getItem().toStringWithoutWeight());
							return getModSeq(count, node.getRightMostChild(), candidates, seq);
						}
					} else /* new child need to be added */ {
						//System.out.println("[R-m-n has no seq. New child need to be added]");
						// add the candidate with the highest prob. which was not added as a child before
						int size = candidates.size();
						for (int i = 0; i < size; i++) {
							if (!node.hasChild(candidates.get(i))) {
								node.addChild(new SeqTreeNode(candidates.get(i)));
								//System.out.println("[Child added] child = "+node.getRightMostChild().getItem().toStringWithoutWeight());
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
					//System.out.println("[No more sequence] node = "+node.getItem().toStringWithoutWeight());
					// mark this node as dead
					node.setDead(true);
					//System.out.println("[Node marked as dead] node = "+node.getItem().toStringWithoutWeight());
					// remove current node from the sequence
					seq.remove(seq.size() - 1);
					seq.remove(seq.size() - 1);
					// recover the pruned candidates
					
					/*candidates.clear();
					for (int i = 0; i < original.size(); i++) {
						candidates.add(new ModificationCandidate(original.get(i)));
					}
					return getModSeq(node.getParent(), candidates, seq);
					*/
					candidates.clear();
					original.add(node.getItem());
					return getModSeq(count, node.getParent(), original, seq);
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

	/////////////////// following codes are deprecated ///////////////////
	public void init(ModificationCandidate[] candidates) {
		ArrayList<ModificationCandidate> list = new ArrayList<ModificationCandidate>();
		for (int i = 0; i < candidates.length; i++) {
			list.add(candidates[i]);
		}

		System.out.println("\nBuilding modification sequence planning tree...");
		long before = System.currentTimeMillis();

		buildFullTree(this.root, list);
		// List<ModificationCandidate> tmpList = list.subList(0, 11);
		// buildFullTree(this.root, tmpList);
		long after = System.currentTimeMillis();
		System.out.println("Tree buildup is done! (" + (after - before) + " ms taken)");
		System.out.println("Number of leaf nodes is : " + this.root.getLeafNodeNum());
		System.out.println("Tree size is : " + this.root.getTreeSize());
	}

	private void buildFullTree(SeqTreeNode node, List<ModificationCandidate> candidates) {
		if (candidates.size() == 0 || node.getDepth() > SDPKBUtil.getInstance().getModSeqBound() + 1) {

		} else {
			for (int i = 0; i < candidates.size(); i++) {
				node.addChild(new SeqTreeNode(candidates.get(i)));

				List<ModificationCandidate> list = new ArrayList<ModificationCandidate>();
				for (int j = 0; j < candidates.size(); j++) {
					if (i != j)
						list.add(candidates.get(j));
				}
				buildFullTree(node.getChild(candidates.get(i)), list);
			}
		}
	}

	public void printTree() {
		System.out.println("\n##### Modification Sequence Planning Tree #####");
		this.printTree("", this.root);
	}

	private void printTree(String line, SeqTreeNode node) {
		if (node.getChildren().size() == 0) {
			System.out.println(line + node.getItem().toString());
		} else {
			for (SeqTreeNode child : node.getChildren()) {
				printTree(line + node.getItem().toString() + " --> ", child);
			}
		}
	}

	public void prune() {

	}
}
