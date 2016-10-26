package heesuk.percom.sem2bit.msg;

import java.util.ArrayList;

public class MessageCompositionSpec {
	private ArrayList<MessageFieldComposition> compositions;
	
	public MessageCompositionSpec(){
		compositions = new ArrayList<MessageFieldComposition>();
	}
	
	public void addModificationSpec(MessageModificationSpec modSpec){
		for(int i=0; i<modSpec.getModifications().size(); i++){
			this.applyModification(modSpec.getModifications().get(i));
		}
	}
	
	public void applyModification(ModificationCandidate modification){
		
	}
}
