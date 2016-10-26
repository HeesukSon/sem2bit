package heesuk.percom.sem2bit.kb.sdp;

public class SDP {
	private String name;
	private SDPMessage reqmsg;
	
	public SDP(){
		
	}
	
	public SDP(String name, SDPMessage msg){
		this();
		this.name = name;
		this.reqmsg = msg;
	}
	
	public SDP(String name){
		this();
		this.name = name;
	}
	
	public void addMessage(SDPMessage msg){
		this.reqmsg = msg;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public SDPMessage getMesage(){
		return this.reqmsg;
	}

	public SDPMessage getReqmsg() {
		return reqmsg;
	}

	public void setReqmsg(SDPMessage reqmsg) {
		this.reqmsg = reqmsg;
	}
}
