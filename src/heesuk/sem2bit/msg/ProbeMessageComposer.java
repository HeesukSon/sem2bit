package heesuk.sem2bit.msg;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import heesuk.sem2bit.ConfigUtil;
import heesuk.sem2bit.kb.protocol.enums.Domain;
import heesuk.sem2bit.kb.protocol.MessageField;
import heesuk.sem2bit.kb.protocol.ProtocolKBUtil;
import heesuk.sem2bit.kb.protocol.enums.MessageFieldLocation;
import heesuk.sem2bit.kb.protocol.enums.MessageFieldType;
import heesuk.sem2bit.kb.protocol.enums.UpdatePattern;
import heesuk.sem2bit.kb.protocol.iot.IoTProtocolKBUtil;
import heesuk.sem2bit.kb.protocol.sdp.SDPKBUtil;

public class ProbeMessageComposer {
	private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(ProbeMessageComposer.class);
	private static ProbeMessageComposer _instance;
	private ProtocolKBUtil kb;

	private ProbeMessageComposer() {
		if(ConfigUtil.getInstance().domain == Domain.SDP){
			this.kb = SDPKBUtil.getInstance();
		}else if(ConfigUtil.getInstance().domain == Domain.IoT_Protocol){
			this.kb = IoTProtocolKBUtil.getInstance();
		}else{
			this.kb = null;
		}
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
				LOG.error("A non-defined update pattern is detected in ProbeMessageComposer!!");
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
				modifiedFields.add(kb.getMsgField(candidate.getField()));
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
						modifiedFields.get(i).setLength(kb.getNewFieldLength(candidate.getField()));
						break;
					}
				}
			}else if(candidate.getUpdate().equals(UpdatePattern.CHANGE_VOCA.toString())){
				
			}else if(candidate.getUpdate().equals(UpdatePattern.VALUE_CHANGE.toString())){
				int size = modifiedFields.size();
				for(int i=0; i<size; i++){
					if(modifiedFields.get(i).getName().equals(candidate.getField())){
						if(modifiedFields.get(i).getValue() instanceof Integer){
							modifiedFields.get(i).setValue((int)modifiedFields.get(i).getValue()+1);
						}

						break;
					}
				}
			}else {
				if(!candidate.getUpdate().contains("DEFAULT]")){
					LOG.error("A non-defined update pattern is detected in ProbeMessageComposer: "+candidate.getUpdate());
				}
			}
		}
		
		return modifiedFields;
	}

	public void writeMsgHeader(ArrayList<MessageField> fields, final DataOutputStream out, int msgSize, int xid)
			throws IOException {
		String headerStr = "{";
		for (MessageField field : fields) {
			try{
				if (field.getLocation().equals(MessageFieldLocation.HEADER)) {
					if (field.getType() == MessageFieldType.SESSION_MGMT) {
						field.setValue(xid);
						if (field.getLength().equals("8")) {
							out.write((int) field.getValue());
							headerStr += "xid:";
							headerStr += field.getValue();
							headerStr += ",";
						} else if (field.getLength().equals("16")) {
							out.writeShort((int) field.getValue());
							headerStr += "xid:";
							headerStr += field.getValue();
							headerStr += ",";
						}

						continue;
					}

					if (field.getType() == MessageFieldType.LANGUAGE_TAG_LENGTH) {
						headerStr += "langTagLength:";
						if (field.getLength().equals("8")) {
							out.write((int) field.getValue());
						} else if (field.getLength().equals("16")) {
							out.writeShort((int) field.getValue());
						} else if (field.getLength().equals("32")) {
							out.writeInt((int) field.getValue());
						}
						headerStr += ",";

						continue;
					}

					if (field.getType() == MessageFieldType.MESSAGE_LENGTH) {
						headerStr += "length:";
						field.setValue(msgSize);
						int value = (int) field.getValue();
						for (int i = 0; i < Integer.parseInt(field.getLength()) / 8; i++) {
							int shift = (8 * (Integer.parseInt(field.getLength()) / 8 - i - 1));
							out.write(((byte) ((value >> shift) & 0xFF)));
							headerStr += ((value >> shift) & 0xFF);
						}
						headerStr += ",";

						continue;
					}

					if (field.getType() == MessageFieldType.LANGUAGE_TAG) {
						headerStr += "langTag:";
						out.writeUTF((String) field.getValue());
						headerStr += field.getValue();
						headerStr += ",";

						continue;
					}

					if (field.getValue() == null) {
						// assumption: only the field with fixed length can have no
						// value
						headerStr += "new:";
						for (int i = 0; i < Integer.parseInt(field.getLength()) / 8; i++) {
							out.write(0);
							headerStr += "0";
						}
						headerStr += ",";
						continue;
					}

					headerStr += "field:";
					out.write((int) field.getValue());
					headerStr += field.getValue();
					for (int i = 1; i < Integer.parseInt(field.getLength()) / 8; i++) {
						out.write(0);
						headerStr += "0";
					}
					headerStr += ",";
				}
			}catch(NullPointerException e){
				LOG.error("[NullPointerException in writeMsgHeader()] field name = "+field.getName()+", field type = "+field.getType());
			}

			headerStr += "}";
			LOG.debug("headerStr : {}"+headerStr);
		}
	}

	public void writeMsgHeader(int cnt, ArrayList<MessageField> fields, final DataOutputStream out, int msgSize, int xid)
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

						LOG.debug("[cnt:{}] field = {}",cnt,field.toString());
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

						LOG.debug("[cnt:{}] field = {}",cnt,field.toString());
						continue;
					}

					if (field.getType() == MessageFieldType.MESSAGE_LENGTH) {
						field.setValue(msgSize);
						int value = (int) field.getValue();
						for (int i = 0; i < Integer.parseInt(field.getLength()) / 8; i++) {
							int shift = (8 * (Integer.parseInt(field.getLength()) / 8 - i - 1));
							out.write(((byte) ((value >> shift) & 0xFF)));
						}

						LOG.debug("[cnt:{}] field = {}",cnt,field.toString());
						continue;
					}

					if (field.getType() == MessageFieldType.LANGUAGE_TAG) {
						out.writeUTF((String) field.getValue());

						LOG.debug("[cnt:{}] field = {}",cnt,field.toString());
						continue;
					}

					if (field.getValue() == null) {
						// assumption: only the field with fixed length can have no
						// value
						String tmp = ("<value:null,length:"+field.getLength()+",");
						for (int i = 0; i < Integer.parseInt(field.getLength()) / 8; i++) {
							out.write(0);
							tmp += "0";
						}
						tmp+=">";
						LOG.debug("[cnt:{}] field = {}",cnt,tmp);
						continue;
					}

					LOG.debug("[cnt:{}] field = {}",cnt,field.toString());
					out.write((int) field.getValue());
					for (int i = 1; i < Integer.parseInt(field.getLength()) / 8; i++) {
						out.write(0);
					}
				}
			}catch(NullPointerException e){
				//LOG.error("[NullPointerException in writeMsgHeader()] field name = "+field.getName()+", field type = "+field.getType());
			}
		}
	}
}
