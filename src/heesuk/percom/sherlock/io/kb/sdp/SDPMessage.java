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
}
