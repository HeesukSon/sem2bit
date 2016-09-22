package heesuk.percom.sherlock.io.kb.seqtree;

public class ModificationSeqPlanTree {
	private static ModificationSeqPlanTree _instance;
	
	private ModificationSeqPlanTree(){
		
	}
	
	public static ModificationSeqPlanTree getInstance(){
		if(_instance == null){
			_instance = new ModificationSeqPlanTree();
		}
		
		return _instance;
	}
	
	public void init(){
		
	}
}
