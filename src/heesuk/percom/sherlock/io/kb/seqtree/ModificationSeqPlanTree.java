package heesuk.percom.sherlock.io.kb.seqtree;

import java.util.ArrayList;
import java.util.List;

import heesuk.percom.sherlock.io.kb.probtree.ModificationCandidate;
import heesuk.percom.sherlock.io.kb.sdp.SDPKBUtil;
import heesuk.percom.sherlock.io.kb.sdp.UpdatePattern;

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

	public ModificationCandidate[] getModSeq() {
		ArrayList<ModificationCandidate> candidates = new ArrayList<ModificationCandidate>();
		for (int i = 0; i < this.candidates.length; i++) {
			candidates.add(this.candidates[i]);
		}
		// return getModSeq(this.root, candidates, new
		// ArrayList<ModificationCandidate>());
		return getModSeq(this.root, this.candidates, new ArrayList<ModificationCandidate>());
	}

	public ModificationCandidate[] getModSeq(SeqTreeNode node, ModificationCandidate[] candidates,
			ArrayList<ModificationCandidate> seq) {
		if (/* at the bound depth: base case */node.getDepth() == SDPKBUtil.getInstance().getModSeqBound() + 1) {
			// System.out.println("[Bottom line] node =
			// "+node.getItem().toStringWithoutWeight());
			// add the current node to the sequence
			seq.add(node.getItem());
			ModificationCandidate[] result = new ModificationCandidate[seq.size()];
			// mark this node as dead
			node.setDead(true);
			// return the result sequence
			return seq.toArray(result);
		} else /* before the bound depth: intermediate nodes */ {
			// System.out.println("[Before the bottom line] node =
			// "+node.getItem().toStringWithoutWeight());
			// add the current node to seq
			seq.add(node.getItem());
			// prune useless candidates accordingly
			// ArrayList<ModificationCandidate> original =
			// this.pruneCandidates(node, candidates);
			ModificationCandidate[] original = this.pruneCandidates(node, candidates);

			if (/* no child */node.getChildren().size() == 0) {
				// System.out.println("[No child exist]");
				// add the candidate with the highest prob. as a child
				// node.addChild(new SeqTreeNode(candidates.get(0)));
				node.addChild(new SeqTreeNode(candidates[0]));
				// System.out.println("[Child added] child =
				// "+node.getRightMostChild().getItem().toStringWithoutWeight());
				// remove the added node from candidates
				// candidates.remove(0);
				this.removeArrElement(candidates, 0);
				return getModSeq(node.getRightMostChild(), candidates, seq);
			} else /* child exists */ {
				// System.out.println("[A child exist]");
				if (/* current node has further sequences */node.hasMoreSequence(candidates)) {
					// System.out.println("[Current node has further
					// sequences]");
					if (/* the right-most-child has further sequences */node.getRightMostChild()
							.hasMoreSequence(candidates)) {
						if (/* right before the bound depth */ node.getDepth() == SDPKBUtil.getInstance()
								.getModSeqBound()) {
							// System.out.println("[Right before the bound
							// depth] children size =
							// "+node.getChildren().size()+", candidate size =
							// "+candidates.size());
							if (/* has more to add */ node.getChildren().size() < candidates.length) {
								// System.out.println("[Has more to add]");

								// add the candidate with the highest prob.
								// which was not added as a child before
								int size = candidates.length;
								for (int i = 0; i < size; i++) {
									if (!node.hasChild(candidates[i])) {
										node.addChild(new SeqTreeNode(candidates[i]));
										// System.out.println("[Child added]
										// child =
										// "+node.getRightMostChild().getItem().toStringWithoutWeight());
										// remove the added node from candidates
										// candidates.remove(i);
										this.removeArrElement(candidates, i);
										size--;
										break;
									}
								}

								return getModSeq(node.getRightMostChild(), candidates, seq);
							} else {
								// System.out.println("[Nothing to add]");
								// mark the current node as dead
								node.setDead(true);
								// remove current node from the sequence
								seq.remove(seq.size() - 1);
								seq.remove(seq.size() - 1);
								// recover the pruned candidates
								candidates = original;
								// System.out.println("[Going back to the
								// parent");
								return getModSeq(node.getParent(), candidates, seq);
							}
						} else {
							// System.out.println("[Right-most-node has further
							// sequences] r-m-n =
							// "+node.getRightMostChild().getItem().toStringWithoutWeight());
							return getModSeq(node.getRightMostChild(), candidates, seq);
						}
					} else /* new child need to be added */ {
						// System.out.println("[R-m-n has no seq. New child need
						// to be added]");
						// add the candidate with the highest prob. which was
						// not added as a child before
						int size = candidates.length;
						for (int i = 0; i < size; i++) {
							if (!node.hasChild(candidates[i])) {
								node.addChild(new SeqTreeNode(candidates[i]));
								// System.out.println("[Child added] child =
								// "+node.getRightMostChild().getItem().toStringWithoutWeight());
								// remove the added node from candidates
								// candidates.remove(i);
								this.removeArrElement(candidates, i);
								size--;
								break;
							}
						}

						return getModSeq(node.getRightMostChild(), candidates, seq);
					}
				} else /* this node has no more sequence */ {
					// System.out.println("[No more sequence] node =
					// "+node.getItem().toStringWithoutWeight());
					// mark this node as dead
					node.setDead(true);
					// System.out.println("[Node marked as dead] node =
					// "+node.getItem().toStringWithoutWeight());
					// remove current node from the sequence
					seq.remove(seq.size() - 1);
					seq.remove(seq.size() - 1);
					// recover the pruned candidates
					candidates = original;
					return getModSeq(node.getParent(), candidates, seq);
				}
			}
		}
	}

	public void removeArrElement(ModificationCandidate[] candidates, int index) {
		int size = candidates.length;
		for (int i = 0; i < candidates.length; i++) {
			if (i == index) {
				candidates[i] = null;
				size--;
			}
		}

		ModificationCandidate[] newCandidates = new ModificationCandidate[size];
		int cnt = 0;

		for (int i = 0; i < candidates.length; i++) {
			if (candidates[i] != null) {
				newCandidates[cnt++] = candidates[i];
			}
		}

		candidates = new ModificationCandidate[newCandidates.length];
		for (int i = 0; i < newCandidates.length; i++) {
			candidates[i] = newCandidates[i];
		}
	}

	// HEURISTICS-based pruning
	private ModificationCandidate[] pruneCandidates(SeqTreeNode node, ModificationCandidate[] candidates) {
		ModificationCandidate[] original = this.removeFutureCandidateWithNoEffect(node,
				removeThisNode(node, candidates));
		this.removeDuplicateCandidates(node, candidates);

		return original;
	}

	private ModificationCandidate[] removeThisNode(SeqTreeNode node, ModificationCandidate[] candidates) {
		System.out.println("[removeThisNode()]");
		int size = candidates.length;
		for (int i = 0; i < candidates.length; i++) {
			try {
				if (node.getItem().sameWith(candidates[i])) {
					candidates[i] = null;
					size--;
				}
			} catch (NullPointerException e) {

			}
		}

		ModificationCandidate[] newCandidates = new ModificationCandidate[size];
		int cnt = 0;

		for (int i = 0; i < candidates.length; i++) {
			if (candidates[i] != null) {
				newCandidates[cnt++] = candidates[i];
			}
		}

		candidates = new ModificationCandidate[newCandidates.length];
		for (int i = 0; i < newCandidates.length; i++) {
			candidates[i] = newCandidates[i];
		}

		return candidates;
	}

	// HEURISTICS NO.1: remove modifications after a deletion
	private ModificationCandidate[] removeFutureCandidateWithNoEffect(SeqTreeNode node,
			ModificationCandidate[] candidates) {
		System.out.println("[removeFutureCandidateWithNoEffect()]");
		for (int i = 0; i < candidates.length; i++) {
			System.out.println("candidates[" + i + "] = "
					+ ((candidates[i] == null) ? "null" : candidates[i].toStringWithoutWeight()));
		}

		ModificationCandidate[] original = new ModificationCandidate[candidates.length];
		for (int i = 0; i < candidates.length; i++) {

		}

		int size = candidates.length;
		for (int i = 0; i < candidates.length; i++) {
			if (node.getItem().getUpdate().equals(UpdatePattern.DELETE_FIELD.toString())
					&& node.getItem().getField().equals(candidates[i].getField())) {
				candidates[i] = null;
				size--;
			}
		}

		ModificationCandidate[] newCandidates = new ModificationCandidate[size];
		int cnt = 0;

		for (int i = 0; i < candidates.length; i++) {
			if (candidates[i] != null) {
				newCandidates[cnt++] = candidates[i];
			}
		}

		candidates = new ModificationCandidate[newCandidates.length];
		for (int i = 0; i < newCandidates.length; i++) {
			candidates[i] = newCandidates[i];
		}

		return original;
	}

	// HEURISTICS NO.2: remove duplicates
	private ModificationCandidate[] removeDuplicateCandidates(SeqTreeNode node, ModificationCandidate[] candidates) {
		System.out.println("[removeDuplcateCandidates()]");
		for (int i = 0; i < candidates.length; i++) {
			System.out.println("candidates[" + i + "] = "
					+ ((candidates[i] == null) ? "null" : candidates[i].toStringWithoutWeight()));
		}

		ModificationCandidate[] original = new ModificationCandidate[candidates.length];
		for (int i = 0; i < candidates.length; i++) {
			original[i] = new ModificationCandidate(candidates[i]);
		}

		int size = candidates.length;
		for (int i = 0; i < candidates.length - 1; i++) {
			for (int j = i + 1; j < candidates.length; j++) {
				try {
					if (candidates[i].sameWith(candidates[j])) {
						System.out.println("[TO-BE-DELETED] " + candidates[j].toStringWithoutWeight());
						candidates[j] = null;
						size--;
					}
				} catch (NullPointerException e) {

				}
			}
		}

		ModificationCandidate[] newCandidates = new ModificationCandidate[size];
		int cnt = 0;

		for (int i = 0; i < candidates.length; i++) {
			if (candidates[i] != null) {
				newCandidates[cnt++] = candidates[i];
			}
		}

		candidates = newCandidates;

		return original;
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
