package heesuk.percom.sem2bit.kb.protocol.sdp;

import heesuk.percom.sem2bit.kb.protocol.Protocol;

public class SDP extends Protocol {
	public SDP(){
		super();
	}
	
	public SDP(String name, SDPMessage msg){
		this();
		this.name = name;
		this.msg = msg;
	}
	
	public SDP(String name){
		this();
		this.name = name;
	}
	
	public SDPMessage getMessage() {
		return (SDPMessage) this.msg;
	}
}
