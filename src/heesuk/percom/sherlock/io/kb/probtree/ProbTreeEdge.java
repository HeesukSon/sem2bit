package heesuk.percom.sherlock.io.kb.probtree;

public class ProbTreeEdge {
	private float weight;
	private ProbTreeNode prev;
	private ProbTreeNode next;
	
	public ProbTreeEdge(){
		
	}
	
	public ProbTreeEdge(float weight){
		this();
		this.weight = weight;
	}
	
	public ProbTreeEdge setWeight(float weight){
		this.weight = weight;
		return this;
	}
	
	public float getWeight(){
		return this.weight;
	}

	public ProbTreeNode getPrev() {
		return prev;
	}

	public void setPrev(ProbTreeNode prev) {
		this.prev = prev;
	}

	public ProbTreeNode getNext() {
		return next;
	}

	public void setNext(ProbTreeNode next) {
		this.next = next;
	}
}
