package heesuk.percom.sherlock.io.msg;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import heesuk.percom.sherlock.io.kb.sdp.MessageField;
import heesuk.percom.sherlock.io.kb.sdp.MessageFieldLocation;
import heesuk.percom.sherlock.io.kb.sdp.MessageFieldType;

public class ProbeMessageComposer {
	private static ProbeMessageComposer _instance;

	private ProbeMessageComposer() {

	}

	public static ProbeMessageComposer getInstance() {
		if (_instance == null) {
			_instance = new ProbeMessageComposer();
		}

		return _instance;
	}

	public void writeMsgHeader(ArrayList<MessageField> fields, final DataOutputStream out, int msgSize, int xid)
			throws IOException {
		for (MessageField field : fields) {
			if (field.getLocation().equals(MessageFieldLocation.HEADER)) {
				if (field.getType() == MessageFieldType.SESSION_MGMT) {
					field.setValue(xid);
					if (field.getLength().equals("8")) {
						out.write((int) field.getValue());
					} else if (field.getLength().equals("16")) {
						out.writeShort((int) field.getValue());
					}
					
					continue;
				} 
				
				if (field.getType() == MessageFieldType.MESSAGE_LENGTH) {
					field.setValue(msgSize);
					int value = (int) field.getValue();
					for (int i = 0; i < Integer.parseInt(field.getLength()) / 8; i++) {
						int shift = (8 * (Integer.parseInt(field.getLength()) / 8 - i-1));
						out.write(((byte) ((value >> shift) & 0xFF)));
					}
					
					continue;
				} 
				
				if (field.getType() == MessageFieldType.LANGUAGE_TAG) {
					out.writeUTF((String) field.getValue());
					
					continue;
				} 
				
				if (field.getValue() == null) {
					// assumption: only the field with fixed length can have no
					// value
					for (int i = 0; i < Integer.parseInt(field.getLength()) / 8; i++) {
						out.write(0);
					}
					continue;
				}
				
				
					out.write((int) field.getValue());
					for (int i = 1; i < Integer.parseInt(field.getLength()) / 8; i++) {
						out.write(0);
					
				}
			}
		}
	}

	public void writeMsgHeader() {

	}
}
