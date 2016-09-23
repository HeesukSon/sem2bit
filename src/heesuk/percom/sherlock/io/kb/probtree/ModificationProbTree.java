package heesuk.percom.sherlock.io.kb.probtree;

public class ModificationProbTree {
private static ModificationProbTree _instance;
	
	private ModificationProbTree(){
		initTree();
		localizeTree();
	}
	
	public static ModificationProbTree getInstance(){
		if(_instance == null){
			_instance = new ModificationProbTree();
		}
		
		return _instance;
	}
	
	private void initTree(){
		
	}
	
	private void localizeTree(){
		
	}
	
	public void addNode(ProbTreeNode high, ProbTreeNode low){
		
	}
	
	public void addNode(ProbTreeNode high, ProbTreeEdge e, ProbTreeNode low){
		
	}
}
