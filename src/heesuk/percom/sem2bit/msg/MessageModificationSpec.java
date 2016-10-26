package heesuk.percom.sem2bit.msg;

import java.util.ArrayList;

public class MessageModificationSpec {
	private ArrayList<ModificationCandidate> modifications;
	
	public MessageModificationSpec(){
		this.modifications = new ArrayList<ModificationCandidate>();
	}

	public ArrayList<ModificationCandidate> getModifications() {
		return modifications;
	}
}
