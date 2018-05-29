package heesuk.percom.sem2bit.kb.protocol.iot;

import heesuk.percom.sem2bit.kb.protocol.IProtocolKBUtil;
import heesuk.percom.sem2bit.kb.protocol.MessageField;
import heesuk.percom.sem2bit.kb.protocol.MessageFieldUpdate;
import heesuk.percom.sem2bit.kb.protocol.ProtocolKBUtil;
import heesuk.percom.sem2bit.kb.protocol.enums.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IoTProtocolKBUtil extends ProtocolKBUtil implements IProtocolKBUtil {
	private static final Logger LOG = LoggerFactory.getLogger(IoTProtocolKBUtil.class);

	private static IoTProtocolKBUtil _instance;

	private IoTProtocolKBUtil() {
		super();
	}

	public static IoTProtocolKBUtil getInstance() {
		if (_instance == null) {
			_instance = new IoTProtocolKBUtil();
		}

		return _instance;
	}

	@Override
	public void addProtocolInfo(){
		addCoAP();
		addMQTTv3();
		addMQTTv5();
		addAMQP();
		addSIPv1();
		addSIPv2();
	}

	@Override
	public void addUpdateHistory(){
		MessageFieldUpdate u1 = new MessageFieldUpdate(
				new MessageField("Protocol Level", MessageFieldType.VERSION_INFO),
				RequirementChange.NOTICE_VERSION_UPDATE, UpdatePattern.VALUE_CHANGE,
				Functionality.PROTOCOL_BASIC_INFO, ProtocolName.MQTTv3.toString(), ProtocolName.MQTTv5.toString());
		MessageFieldUpdate u2 = new MessageFieldUpdate(
				new MessageField("Return Code", MessageFieldType.RETURN_CODE),
				RequirementChange.BETTER_DESCRIPTION, UpdatePattern.CHANGE_VOCA,
				Functionality.PROTOCOL_BEHAVIOR, ProtocolName.MQTTv3.toString(), ProtocolName.MQTTv5.toString());
		MessageFieldUpdate u3 = new MessageFieldUpdate(
				new MessageField("Property Length", MessageFieldType.MESSAGE_LENGTH),
				RequirementChange.CONTROL_OPTION_ADDITION, UpdatePattern.ADD_NEW_FIELD,
				Functionality.CONTENT_PARSING, ProtocolName.MQTTv3.toString(), ProtocolName.MQTTv5.toString());
		MessageFieldUpdate u4 = new MessageFieldUpdate(
				new MessageField("SIP-version", MessageFieldType.VERSION_INFO),
				RequirementChange.NOTICE_VERSION_UPDATE, UpdatePattern.VALUE_CHANGE,
				Functionality.PROTOCOL_BASIC_INFO, ProtocolName.SIPv1.toString(), ProtocolName.SIPv2.toString());
		MessageFieldUpdate u5 = new MessageFieldUpdate(
				new MessageField("Expires", MessageFieldType.KEEP_ALIVE),
				RequirementChange.DATA_SIMPLIFICATION, UpdatePattern.CHANGE_VOCA,
				Functionality.CONTENT_PARSING, ProtocolName.SIPv1.toString(), ProtocolName.SIPv2.toString());
		MessageFieldUpdate u6 = new MessageFieldUpdate(
				new MessageField("Content-Language", MessageFieldType.LANGUAGE_CODE),
				RequirementChange.BETTER_DESCRIPTION, UpdatePattern.ADD_NEW_FIELD,
				Functionality.CONTENT_PARSING, ProtocolName.SIPv1.toString(), ProtocolName.SIPv2.toString());

		this.updateHistory.add(u1);
		this.updateHistory.add(u2);
		this.updateHistory.add(u3);
		this.updateHistory.add(u4);
		this.updateHistory.add(u5);
		this.updateHistory.add(u6);
	}

	@Override
	public MessageField getMsgField(String fType){
		MessageField field = new MessageField();

		// TODO inference mechanism should be modified later
		if(fType.equals(MessageFieldType.LANGUAGE_TAG_LENGTH.toString())){
			field.setLength("16");
			field.setValue(0);
			field.setName("Language Tag Length");
			field.setLocation(MessageFieldLocation.HEADER);
			field.setType(MessageFieldType.LANGUAGE_TAG_LENGTH);
		}else if(fType.equals(MessageFieldType.LANGUAGE_TAG.toString())){
			field.setLength("v");
			field.setValue("ko");
			field.setName("Language Tag");
			field.setLocation(MessageFieldLocation.HEADER);
			field.setType(MessageFieldType.LANGUAGE_TAG);
		}

		return field;
	}

	@Override
	public int getNewFieldLength(String fName){
		// TODO length inference mechanism should be added later
		return 40;
	}

	private void addCoAP(){
		IoTProtocol coap = new IoTProtocol(ProtocolName.COAP.toString());
		IoTProtocolMessage msg = new IoTProtocolMessage();
		msg.addField(new MessageField("Version", MessageFieldType.VERSION_INFO, MessageFieldLocation.HEADER, "2"));
		msg.addField(new MessageField("Code", MessageFieldType.RETURN_CODE, MessageFieldLocation.HEADER, "8"));
		msg.addField(new MessageField("Code", MessageFieldType.OP_CODE, MessageFieldLocation.HEADER, "8"));
		msg.addField(new MessageField("T", MessageFieldType.CONTROL_FLAG, MessageFieldLocation.HEADER, "2"));
		msg.addField(new MessageField("Message ID", MessageFieldType.SESSION_MGMT, MessageFieldLocation.HEADER, "16"));
		msg.addField(new MessageField("Token", MessageFieldType.RQST_RPLY_MATCHING, MessageFieldLocation.HEADER, "v"));
		msg.addField(new MessageField("TKL", MessageFieldType.MESSAGE_LENGTH, MessageFieldLocation.HEADER, "4"));
		msg.addField(new MessageField("Options", MessageFieldType.ENCODING, MessageFieldLocation.HEADER, "v"));

		coap.addMessage(msg);
		this.pMap.put(ProtocolName.COAP, coap);
	}

	private void addMQTTv3(){
		IoTProtocol mqttv3 = new IoTProtocol(ProtocolName.MQTTv3.toString());
		IoTProtocolMessage msg = new IoTProtocolMessage();
		msg.addField(new MessageField("Protocol Level", MessageFieldType.VERSION_INFO, MessageFieldLocation.HEADER, "8"));
		msg.addField(new MessageField("Protocol Name", MessageFieldType.PROTOCOL_NAME, MessageFieldLocation.HEADER, "48"));
		msg.addField(new MessageField("Return Code", MessageFieldType.RETURN_CODE, MessageFieldLocation.HEADER, "8"));
		msg.addField(new MessageField("Control Packet Type", MessageFieldType.OP_CODE, MessageFieldLocation.HEADER, "4"));
		msg.addField(new MessageField("Flags", MessageFieldType.CONTROL_FLAG, MessageFieldLocation.HEADER, "4"));
		msg.addField(new MessageField("Connect flag", MessageFieldType.CONTROL_FLAG, MessageFieldLocation.HEADER, "8"));
		msg.addField(new MessageField("Packet ID", MessageFieldType.RQST_RPLY_MATCHING, MessageFieldLocation.HEADER, "16"));
		msg.addField(new MessageField("Keep alive", MessageFieldType.KEEP_ALIVE, MessageFieldLocation.HEADER, "16"));
		msg.addField(new MessageField("Remaining Length", MessageFieldType.MESSAGE_LENGTH, MessageFieldLocation.HEADER, "v"));

		mqttv3.addMessage(msg);
		this.pMap.put(ProtocolName.MQTTv3, mqttv3);
	}

	private void addMQTTv5(){
		IoTProtocol mqttv5 = new IoTProtocol(ProtocolName.MQTTv5.toString());
		IoTProtocolMessage msg = new IoTProtocolMessage();
		msg.addField(new MessageField("Protocol Level", MessageFieldType.VERSION_INFO, MessageFieldLocation.HEADER, "8"));
		msg.addField(new MessageField("Protocol Name", MessageFieldType.PROTOCOL_NAME, MessageFieldLocation.HEADER, "48"));
		msg.addField(new MessageField("Return Code", MessageFieldType.RETURN_CODE, MessageFieldLocation.HEADER, "8"));
		msg.addField(new MessageField("Control Packet Type", MessageFieldType.OP_CODE, MessageFieldLocation.HEADER, "4"));
		msg.addField(new MessageField("Flags", MessageFieldType.CONTROL_FLAG, MessageFieldLocation.HEADER, "4"));
		msg.addField(new MessageField("Connect flag", MessageFieldType.CONTROL_FLAG, MessageFieldLocation.HEADER, "8"));
		msg.addField(new MessageField("Packet ID", MessageFieldType.RQST_RPLY_MATCHING, MessageFieldLocation.HEADER, "16"));
		msg.addField(new MessageField("Keep alive", MessageFieldType.KEEP_ALIVE, MessageFieldLocation.HEADER, "16"));
		msg.addField(new MessageField("Remaining Length", MessageFieldType.MESSAGE_LENGTH, MessageFieldLocation.HEADER, "v"));
		msg.addField(new MessageField("Property Length", MessageFieldType.MESSAGE_LENGTH, MessageFieldLocation.HEADER, "8"));

		mqttv5.addMessage(msg);
		this.pMap.put(ProtocolName.MQTTv5, mqttv5);
	}

	private void addAMQP(){
		IoTProtocol amqp = new IoTProtocol(ProtocolName.AMQP.toString());
		IoTProtocolMessage msg = new IoTProtocolMessage();
		msg.addField(new MessageField("Version", MessageFieldType.VERSION_INFO, MessageFieldLocation.HEADER, "24"));
		msg.addField(new MessageField("Protocol Name", MessageFieldType.PROTOCOL_NAME, MessageFieldLocation.HEADER, "32"));
		msg.addField(new MessageField("Return Code", MessageFieldType.RETURN_CODE, MessageFieldLocation.BODY, "v"));
		msg.addField(new MessageField("Op-Code", MessageFieldType.OP_CODE, MessageFieldLocation.BODY, "v"));
		msg.addField(new MessageField("Control flag", MessageFieldType.CONTROL_FLAG, MessageFieldLocation.BODY, "v"));
		msg.addField(new MessageField("CHANNEL", MessageFieldType.SESSION_MGMT, MessageFieldLocation.HEADER, "16"));
		msg.addField(new MessageField("CHANNEL", MessageFieldType.RQST_RPLY_MATCHING, MessageFieldLocation.HEADER, "16"));
		msg.addField(new MessageField("SIZE", MessageFieldType.MESSAGE_LENGTH, MessageFieldLocation.HEADER, "32"));
		msg.addField(new MessageField("DOff", MessageFieldType.MESSAGE_LENGTH, MessageFieldLocation.HEADER, "8"));
		msg.addField(new MessageField("SASL", MessageFieldType.ENCRYPTION, MessageFieldLocation.HEADER, "8"));

		amqp.addMessage(msg);
		this.pMap.put(ProtocolName.AMQP, amqp);
	}

	private void addSIPv1(){
		IoTProtocol sipv1 = new IoTProtocol(ProtocolName.SIPv1.toString());
		IoTProtocolMessage msg = new IoTProtocolMessage();
		msg.addField(new MessageField("SIP-version", MessageFieldType.VERSION_INFO, MessageFieldLocation.HEADER, "v"));
		msg.addField(new MessageField("Status-Code", MessageFieldType.RETURN_CODE, MessageFieldLocation.HEADER, "v"));
		msg.addField(new MessageField("Method", MessageFieldType.OP_CODE, MessageFieldLocation.HEADER, "v"));
		msg.addField(new MessageField("many", MessageFieldType.CONTROL_FLAG, MessageFieldLocation.HEADER, "v"));
		msg.addField(new MessageField("Call-ID", MessageFieldType.SESSION_MGMT, MessageFieldLocation.HEADER, "v"));
		msg.addField(new MessageField("CSeq_To_From", MessageFieldType.RQST_RPLY_MATCHING, MessageFieldLocation.HEADER, "v"));
		msg.addField(new MessageField("Expires", MessageFieldType.KEEP_ALIVE, MessageFieldLocation.HEADER, "v"));
		msg.addField(new MessageField("Content-Length", MessageFieldType.MESSAGE_LENGTH, MessageFieldLocation.HEADER, "v"));
		msg.addField(new MessageField("Content-Encoding", MessageFieldType.ENCODING, MessageFieldLocation.HEADER, "v"));
		msg.addField(new MessageField("Encryption", MessageFieldType.ENCRYPTION, MessageFieldLocation.HEADER, "v"));

		sipv1.addMessage(msg);
		this.pMap.put(ProtocolName.SIPv1, sipv1);
	}

	private void addSIPv2(){
		IoTProtocol sipv2 = new IoTProtocol(ProtocolName.SIPv2.toString());
		IoTProtocolMessage msg = new IoTProtocolMessage();
		msg.addField(new MessageField("SIP-version", MessageFieldType.VERSION_INFO, MessageFieldLocation.HEADER, "v"));
		msg.addField(new MessageField("Status-Code", MessageFieldType.RETURN_CODE, MessageFieldLocation.HEADER, "v"));
		msg.addField(new MessageField("Method", MessageFieldType.OP_CODE, MessageFieldLocation.HEADER, "v"));
		msg.addField(new MessageField("many", MessageFieldType.CONTROL_FLAG, MessageFieldLocation.HEADER, "v"));
		msg.addField(new MessageField("Call-ID", MessageFieldType.SESSION_MGMT, MessageFieldLocation.HEADER, "v"));
		msg.addField(new MessageField("CSeq_To_From", MessageFieldType.RQST_RPLY_MATCHING, MessageFieldLocation.HEADER, "v"));
		msg.addField(new MessageField("Expires", MessageFieldType.KEEP_ALIVE, MessageFieldLocation.HEADER, "v"));
		msg.addField(new MessageField("Content-Length", MessageFieldType.MESSAGE_LENGTH, MessageFieldLocation.HEADER, "v"));
		msg.addField(new MessageField("Content-Encoding", MessageFieldType.ENCODING, MessageFieldLocation.HEADER, "v"));
		msg.addField(new MessageField("Content-Language", MessageFieldType.LANGUAGE_CODE, MessageFieldLocation.HEADER, "v"));
		msg.addField(new MessageField("Encryption", MessageFieldType.ENCRYPTION, MessageFieldLocation.HEADER, "v"));

		sipv2.addMessage(msg);
		this.pMap.put(ProtocolName.SIPv2, sipv2);
	}
}
