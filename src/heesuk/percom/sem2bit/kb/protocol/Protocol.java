package heesuk.percom.sem2bit.kb.protocol;

public class Protocol {
	protected String name;
	protected ProtocolMessage msg;
	
	public Protocol(){
		
	}
	
	public Protocol(String name, ProtocolMessage msg){
		this();
		this.name = name;
		this.msg = msg;
	}
	
	public Protocol(String name){
		this();
		this.name = name;
	}
	
	public void addMessage(ProtocolMessage msg){
		this.msg = msg;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public ProtocolMessage getMessage(){
		return this.msg;
	}
}
