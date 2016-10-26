package heesuk.percom.sem2bit.kb.sdp;

import heesuk.percom.sem2bit.kb.sdp.enums.MessageFieldLocation;
import heesuk.percom.sem2bit.kb.sdp.enums.MessageFieldType;

public class MessageField {
	private String name;
	private MessageFieldType type;
	private MessageFieldLocation location;
	private String length;
	private Object value;
	
	public MessageField(){
		
	}
	
	public MessageField(MessageField field){
		this.name = field.getName();
		this.type = field.getType();
		this.location = field.getLocation();
		this.length = field.getLength();
		this.value = (field.getValue()==null)?null:field.getValue();
	}
	
	public MessageField(String name, MessageFieldType type, MessageFieldLocation location, String length){
		this();
		this.name = name;
		this.type = type;
		this.location = location;
		this.length = length;
	}
	
	public MessageField(String name, MessageFieldType type, MessageFieldLocation location, String length, int value){
		this();
		this.name = name;
		this.type = type;
		this.location = location;
		this.length = length;
		this.value = new Integer(value);
	}
	
	public MessageField(String name, MessageFieldType type, MessageFieldLocation location, String length, String value){
		this();
		this.name = name;
		this.type = type;
		this.location = location;
		this.length = length;
		this.value = new String(value);
	}
	
	public MessageField(String name, MessageFieldType type, MessageFieldLocation location, int length){
		this(name, type, location, ""+length);
	}
	
	public MessageField(String name, MessageFieldType type, MessageFieldLocation location, int length, int value){
		this(name, type, location, ""+length, value);
	}
	
	public MessageField(String name, MessageFieldType type){
		this();
		this.name = name;
		this.type = type;
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
	
	public String getName(){
		return this.name;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = new String(value);
	}
	
	public void setValue(int value){
		this.value = new Integer(value);
	}
}
