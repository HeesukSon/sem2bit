package heesuk.percom.sem2bit.msg;

import heesuk.percom.sem2bit.kb.sdp.MessageField;

public class MessageFieldComposition {
	private MessageField field;
	private byte byteNum;
	public MessageField getField() {
		return field;
	}
	public void setField(MessageField field) {
		this.field = field;
	}
	public byte getByteNum() {
		return byteNum;
	}
	public void setByteNum(byte byteNum) {
		this.byteNum = byteNum;
	}
}
