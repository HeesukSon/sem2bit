package heesuk.percom.sherlock.io.kb.sdp;

public class MessageField {
	private String name;
	private MessageFieldType type;
	private MessageFieldLocation location;
	private String length;
	
	public MessageField(){
		
	}
	
	public MessageField(String name, MessageFieldType type, MessageFieldLocation location, String length){
		this();
		this.name = name;
		this.type = type;
		this.location = location;
		this.length = length;
	}
	
	public MessageField(String name, MessageFieldType type, MessageFieldLocation location, int length){
		this(name, type, location, ""+length);
	}

	public MessageFieldType getType() {
		return type;
	}

	public void setType(MessageFieldType type) {
		this.type = type;
	}

	public MessageFieldLocation getLocation() {
		return location;
	}

	public void setLocation(MessageFieldLocation location) {
		this.location = location;
	}

	public String getLength() {
		return length;
	}

	public void setLength(String length) {
		this.length = length;
	}
}