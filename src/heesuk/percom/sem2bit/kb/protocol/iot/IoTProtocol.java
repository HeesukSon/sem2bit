package heesuk.percom.sem2bit.kb.protocol.iot;

import heesuk.percom.sem2bit.kb.protocol.Protocol;

public class IoTProtocol extends Protocol {
	public IoTProtocol(){
		super();
	}
	
	public IoTProtocol(String name, IoTProtocolMessage msg){
		this();
		this.name = name;
		this.msg = msg;
	}
	
	public IoTProtocol(String name){
		this();
		this.name = name;
	}
	
	public IoTProtocolMessage getMesage(){
		return (IoTProtocolMessage) this.msg;
	}
}
