package heesuk.percom.sherlock.io.kb.seqtree;

import java.util.ArrayList;
import java.util.List;

import heesuk.percom.sherlock.io.msg.ModificationCandidate;

public class SeqTreeNode {
	private ModificationCandidate item;
	private int depth;
	private SeqTreeNode parent;
	private ArrayList<SeqTreeNode> children;
	private boolean dead;
	
	public SeqTreeNode(){
		this.item = new ModificationCandidate("DEFAULT", "[DEFAULT]", 0f);
		this.children = new ArrayList<SeqTreeNode>();
		this.dead = false;
	}
	
	public SeqTreeNode(int depth){
		this();
		this.depth = depth;
	}
	
	public SeqTreeNode(ModificationCandidate item){
		this();
		this.item = new ModificationCandidate(item);
	}
	
	public SeqTreeNode(ModificationCandidate item, SeqTreeNode parent){
		this(item);
		this.parent = parent;
	}
	
	public SeqTreeNode getChild(ModificationCandidate candidate){
		for(int i=0; i<this.children.size(); i++){
			if(this.children.get(i).getItem().sameWith(candidate))
				return this.children.get(i);
		}
		
		return null;
	}
	
	public boolean hasChild(ModificationCandidate candidate){
		for(SeqTreeNode child : this.children){
			if(child.getItem().sameWith(candidate))
				return true;
		}
		
		return false;
	}
	
	public SeqTreeNode getRightMostChild(){
		return this.children.get(this.children.size()-1);	
	}
	
	public void addChild(SeqTreeNode child){
		child.setParent(this);
		child.setDepth(this.getDepth()+1);
		this.children.add(child);
	}

	public ModificationCandidate getItem() {
		return item;
	}

	public void setItem(ModificationCandidate item) {
		this.item = item;
	}

	public SeqTreeNode getParent() {
		return this.parent;
	}
	
	public ArrayList<SeqTreeNode> getChildren(){
		return this.children;
	}

	public void setParent(SeqTreeNode parent) {
		this.parent = parent;
	}

	public int getDepth() {
		return depth;
	}

	public void setDepth(int depth) {
		this.depth = depth;
	}

	public boolean isDead() {
		return dead;
	}

	public void setDead(boolean dead) {
		this.dead = dead;		
	}
	
	public boolean hasMoreSequence(ArrayList<ModificationCandidate> candidates){
		return hasMoreSequence(this, candidates);
	}
	
	// Assumption: if there's a room, candidates always give a child to add
	private boolean hasMoreSequence(SeqTreeNode node, ArrayList<ModificationCandidate> candidates) {
		//System.out.println(node.getItem().toStringWithoutWeight()+".getChildren().size() = "+node.getChildren().size()+", candidates.size()="+candidates.size());
		if (/* node has room for a child addition */ node.getChildren().size() < candidates.size()) {
			return true;
		} else/* node cannot add more children */ {
			if (/* all children node are dead */ this.allChildrenDead()) {
				return false;
			} else /* right-most-node is alive */ {
				return hasMoreSequence(node.getRightMostChild(), candidates);
			}
		}
	}
	
	public boolean allChildrenDead(){
		boolean allDead = true;
		
		for(SeqTreeNode child : this.children){
			if(child.isDead() == false)
				return false;
		}
		
		return allDead;
	}

	public int getLeafNodeNum(){
		if(this.children.size()==0){
			return 1;
		}else{
			int cnt = 0;
			for(SeqTreeNode child : this.children){
				cnt += child.getLeafNodeNum();
			}
			
			return cnt;
		}
	}
	
	public int getTreeSize(){
		if(this.children.size()==0){
			return 1;
		}else{
			int cnt = 1;
			for(SeqTreeNode child : this.children){
				cnt += child.getTreeSize();
			}
			
			return cnt;
		}
	}
}
