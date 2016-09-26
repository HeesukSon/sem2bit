package heesuk.percom.sherlock.io.msg;

import heesuk.percom.sherlock.io.kb.probtree.ModificationCandidate;

public class ProbeMessageComposer {
	private static ProbeMessageComposer _instance;
	
	private ProbeMessageComposer(){
		
	}
	
	public static ProbeMessageComposer getInstance(){
		if(_instance == null){
			_instance = new ProbeMessageComposer();
		}
		
		return _instance;
	}
	
	public BitLevelModification[] getBitModifications(ModificationCandidate[] seq){
		
	}
}
