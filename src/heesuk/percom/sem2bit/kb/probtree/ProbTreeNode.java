package heesuk.percom.sem2bit.kb.probtree;

import java.util.HashMap;

public class ProbTreeNode {
	private String label;
	private int depth;
	
	// inEdge <name of departing parent node, inEdge>
	private ProbTreeEdge inEdge;
	// outEdge <name of destination child node, outEdge>
	private HashMap<String, ProbTreeEdge> outEdge;
	
	private ProbTreeNode(String label){
		this.label = label;
		this.outEdge = new HashMap<String, ProbTreeEdge>();
	}
	
	public ProbTreeNode(String label, int depth){
		this(label);
		this.depth = depth;
	}
	
	public int getLeafNodeNum(){
		return getLeafNodeNum(this);
	}
	
	private int getLeafNodeNum(ProbTreeNode node){
		int num = 0;
		
		if(node.depth == 5){
			return 1;
		}else{
			for(ProbTreeEdge out : node.outEdge.values()){
				num += getLeafNodeNum(out.getNext());
			}
		}
		
		return num;
	}
	
	public void removeChild(String child){
		ProbTreeEdge[] outEdgeArr = new ProbTreeEdge[this.outEdge.values().size()];
		this.outEdge.values().toArray(outEdgeArr);
		for(ProbTreeEdge out : outEdgeArr){
			if(out.getNext().getLabel().equals(child)){				
				this.outEdge.remove(out.getNext().getLabel());
				out = null;
			}
		}
		
		this.removeChildlessParent(this);
	}
	
	private void removeChildlessParent(ProbTreeNode node){
		if(node.outEdge.isEmpty()){
			node.inEdge.getPrev().removeChild(node.getLabel());
		}
	}
	
	public ProbTreeNode addChild(String child){
		ProbTreeEdge out_E = new ProbTreeEdge();
		ProbTreeNode child_N = new ProbTreeNode(child, this.depth+1);
		child_N.addParent(this, out_E);
		
		out_E.setNext(child_N);
		out_E.setPrev(this);
		
		this.outEdge.put(child, out_E);
		
		return child_N;
	}
	
	public ProbTreeNode getChild(String name){
		for(String key : outEdge.keySet()){
			if(outEdge.get(key).getNext().getLabel().equals(name))
				return outEdge.get(key).getNext();
		}
		
		return null;
	}
	
	public void addParent(ProbTreeNode parent, ProbTreeEdge edge){
		this.inEdge = edge;
	}
	
	public void updateOutEdgeWeight(String childNode, float weight){
		for(String childKey : this.outEdge.keySet()){
			if(childKey.equals(childNode)){
				this.outEdge.replace(childKey, this.outEdge.get(childKey).setWeight(weight));
			}
		}
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}
	
	public int getDepth(){
		return this.depth;
	}
	
	public ProbTreeEdge[] getOutEdges(){
		ProbTreeEdge[] edges = new ProbTreeEdge[this.outEdge.values().size()];
		this.outEdge.values().toArray(edges);
		
		return edges;
	}
}
