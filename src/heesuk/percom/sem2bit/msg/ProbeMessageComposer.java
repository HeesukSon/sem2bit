package heesuk.percom.sem2bit.msg;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import heesuk.percom.sem2bit.ProbeLogger;
import heesuk.percom.sem2bit.kb.sdp.MessageField;
import heesuk.percom.sem2bit.kb.sdp.SDPKBUtil;
import heesuk.percom.sem2bit.kb.sdp.enums.MessageFieldLocation;
import heesuk.percom.sem2bit.kb.sdp.enums.MessageFieldType;
import heesuk.percom.sem2bit.kb.sdp.enums.UpdatePattern;

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

	public ArrayList<MessageField> getModifiedFieldList(ArrayList<MessageField> fields,
														ModificationCandidate[] candidates) {
		ArrayList<MessageField> modifiedFields = new ArrayList<MessageField>();

		// copy the original fields
		for(MessageField originalField : fields){
			modifiedFields.add(new MessageField(originalField));
		}

		// modify the fields
		for(ModificationCandidate candidate : candidates){
			if(candidate.getUpdate().equals(UpdatePattern.ADD_NEW_FIELD)){

			}else if(candidate.getUpdate().equals(UpdatePattern.DELETE_FIELD)){
				int size = modifiedFields.size();
				for(int i=0; i<size; i++){
					if(modifiedFields.get(i).getName().equals(candidate.getField())){
						modifiedFields.remove(i);
						break;
					}
				}
			}else if(candidate.getUpdate().equals(UpdatePattern.CHANGE_FIELD_LENGTH)){

			}else if(candidate.getUpdate().equals(UpdatePattern.CHANGE_VOCA)){

			}else{
				ProbeLogger.appendErrln("probe", "A non-defined update pattern is detected in ProbeMessageComposer!!");
			}
		}

		return modifiedFields;
	}

	/**
	 *
	 * @param cnt	iteration count is added for debugging purpose.
	 * @param fields
	 * @param candidates
	 * @return
	 */
	public ArrayList<MessageField> getModifiedFieldList(int cnt, ArrayList<MessageField> fields,
			ModificationCandidate[] candidates) {
		ArrayList<MessageField> modifiedFields = new ArrayList<MessageField>();
		
		// copy the original fields
		for(MessageField originalField : fields){
			modifiedFields.add(new MessageField(originalField));
		}
		
		// modify the fields
		for(ModificationCandidate candidate : candidates){
			if(candidate.getUpdate().equals(UpdatePattern.ADD_NEW_FIELD.toString())){
				modifiedFields.add(SDPKBUtil.getInstance().getMsgField(candidate.getField()));
			}else if(candidate.getUpdate().equals(UpdatePattern.DELETE_FIELD.toString())){
				int size = modifiedFields.size();
				for(int i=0; i<size; i++){
					if(modifiedFields.get(i).getName().equals(candidate.getField())){
						modifiedFields.remove(i);
						break;
					}
				}
			}else if(candidate.getUpdate().equals(UpdatePattern.CHANGE_FIELD_LENGTH.toString())){
				int size = modifiedFields.size();
				for(int i=0; i<size; i++){
					if(modifiedFields.get(i).getName().equals(candidate.getField())){
						modifiedFields.get(i).setLength(SDPKBUtil.getInstance().getNewFieldLength(candidate.getField()));
						break;
					}
				}
			}else if(candidate.getUpdate().equals(UpdatePattern.CHANGE_VOCA.toString())){
				
			}else{
				ProbeLogger.appendErrln("probe", "A non-defined update pattern is detected in ProbeMessageComposer: "+candidate.getUpdate());
			}
		}
		
		return modifiedFields;
	}

	public void writeMsgHeader(ArrayList<MessageField> fields, final DataOutputStream out, int msgSize, int xid)
			throws IOException {
		for (MessageField field : fields) {
			try{
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

					if (field.getType() == MessageFieldType.LANGUAGE_TAG_LENGTH) {
						if (field.getLength().equals("8")) {
							out.write((int) field.getValue());
						} else if (field.getLength().equals("16")) {
							out.writeShort((int) field.getValue());
						} else if (field.getLength().equals("32")) {
							out.writeInt((int) field.getValue());
						}

						continue;
					}

					if (field.getType() == MessageFieldType.MESSAGE_LENGTH) {
						field.setValue(msgSize);
						int value = (int) field.getValue();
						for (int i = 0; i < Integer.parseInt(field.getLength()) / 8; i++) {
							int shift = (8 * (Integer.parseInt(field.getLength()) / 8 - i - 1));
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
			}catch(NullPointerException e){
				ProbeLogger.appendErrln("probe","[NullPointerException in writeMsgHeader()] field name = "+field.getName()+", field type = "+field.getType());
			}

		}
	}

	public void writeMsgHeader(int cnt, ArrayList<MessageField> fields, final DataOutputStream out, int msgSize, int xid)
			throws IOException {
		String typeStr = "";

		for (MessageField field : fields) {
			try{
				if (field.getLocation().equals(MessageFieldLocation.HEADER)) {
					typeStr += field.toString();
					if (field.getType() == MessageFieldType.SESSION_MGMT) {
						field.setValue(xid);
						if (field.getLength().equals("8")) {
							out.write((int) field.getValue());
						} else if (field.getLength().equals("16")) {
							out.writeShort((int) field.getValue());
						}

						continue;
					}

					if (field.getType() == MessageFieldType.LANGUAGE_TAG_LENGTH) {
						if (field.getLength().equals("8")) {
							out.write((int) field.getValue());
						} else if (field.getLength().equals("16")) {
							out.writeShort((int) field.getValue());
						} else if (field.getLength().equals("32")) {
							out.writeInt((int) field.getValue());
						}

						continue;
					}

					if (field.getType() == MessageFieldType.MESSAGE_LENGTH) {
						field.setValue(msgSize);
						int value = (int) field.getValue();
						for (int i = 0; i < Integer.parseInt(field.getLength()) / 8; i++) {
							int shift = (8 * (Integer.parseInt(field.getLength()) / 8 - i - 1));
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


			}catch(NullPointerException e){
				ProbeLogger.appendErrln("probe","[NullPointerException in writeMsgHeader()] field name = "+field.getName()+", field type = "+field.getType());
			}

		}

		if(cnt==57){ ProbeLogger.appendLogln("probe", "[cnt:57] typeStr = "+typeStr); }
	}
}
