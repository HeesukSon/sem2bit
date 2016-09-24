package heesuk.percom.sherlock.io.kb.sdp;

import java.util.ArrayList;

public class SDPMessage {
	private ArrayList<MessageField> fieldList;
	
	public SDPMessage(){
		this.fieldList = new ArrayList<MessageField>();
	}
	
	public void addField(MessageField field){
		this.fieldList.add(field);
	}

	public ArrayList<MessageField> getFieldList() {
		return fieldList;
	}
	
	public boolean contains(String field){
		for(MessageField containedField : this.fieldList){
			if(containedField.getType().toString().equals(field))
				return true;
		}
		
		return false;
	}
}
