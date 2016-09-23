package heesuk.percom.sherlock.io.kb.sdp;

import java.util.ArrayList;

public class SDP {
	private String name;
	private ArrayList<SDPMessage> msgList;
	
	public SDP(){
		this.msgList = new ArrayList<SDPMessage>();
	}
	
	public SDP(String name, ArrayList<SDPMessage> msgList){
		this();
		this.name = name;
		for(SDPMessage msg : msgList){
			this.msgList.add(msg);
		}
	}
	
	public SDP(String name, SDPMessage msg){
		this();
		this.name = name;
		this.msgList.add(msg);
	}
	
	public SDP(String name){
		this();
		this.name = name;
	}
	
	public void addMessage(SDPMessage msg){
		this.msgList.add(msg);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public ArrayList<SDPMessage> getMsgList() {
		return msgList;
	}

	public void setMsgList(ArrayList<SDPMessage> msgList) {
		this.msgList = msgList;
	}
}
