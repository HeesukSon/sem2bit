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
			if (field.getLength().equals(MessageFieldLocation.HEADER)) {
				if (field.getValue() == null) {
					// assumption: only the field with fixed length can have no
					// value
					for (int i = 0; i < Integer.parseInt(field.getLength()) / 8; i++) {
						out.write(0);
					}
				} else if (field.getType().equals(MessageFieldType.SESSION_MGMT)) {
					field.setValue(xid);
					if (field.getLength().equals("8")) {
						out.write((int) field.getValue());
					} else if (field.getLength().equals("16")) {
						out.writeShort((int) field.getValue());
					}
				} else if (field.getType().equals(MessageFieldType.MESSAGE_LENGTH)) {
					field.setValue(msgSize);
					for (int i = 0; i < Integer.parseInt(field.getLength()) / 8; i++) {
						out.write(((byte) (((int) field.getValue() >> 8 * (Integer.parseInt(field.getLength()) / 8 - i))
								& 0xFF)));
					}
				} else if (field.getType().equals(MessageFieldType.LANGUAGE_TAG)) {
					out.writeUTF((String) field.getValue());
				} else {
					out.write((byte) field.getValue());
					for (int i = 1; i < Integer.parseInt(field.getLength()) / 8; i++) {
						out.write(0);
					}
				}
			}
		}
	}

	public void writeMsgHeader() {

	}
}
