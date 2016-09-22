package heesuk.percom.sherlock.io.kb.probtree;

public class ModificationProbTree {
private static ModificationProbTree _instance;
	
	private ModificationProbTree(){
		
	}
	
	public static ModificationProbTree getInstance(){
		if(_instance == null){
			_instance = new ModificationProbTree();
		}
		
		return _instance;
	}
}
