package heesuk.sem2bit.kb.protocol.sdp;

import java.util.Locale;

import ch.ethz.iks.slp.impl.SLPMessage;
import heesuk.sem2bit.kb.protocol.IProtocolKBUtil;
import heesuk.sem2bit.kb.protocol.MessageField;
import heesuk.sem2bit.kb.protocol.MessageFieldUpdate;
import heesuk.sem2bit.kb.protocol.ProtocolKBUtil;
import heesuk.sem2bit.kb.protocol.enums.ProtocolName;
import heesuk.sem2bit.kb.protocol.enums.Functionality;
import heesuk.sem2bit.kb.protocol.enums.MessageFieldLocation;
import heesuk.sem2bit.kb.protocol.enums.MessageFieldType;
import heesuk.sem2bit.kb.protocol.enums.RequirementChange;
import heesuk.sem2bit.kb.protocol.enums.UpdatePattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SDPKBUtil extends ProtocolKBUtil implements IProtocolKBUtil {
	private static final Logger LOG = LoggerFactory.getLogger(SDPKBUtil.class);

	private static SDPKBUtil _instance;

	private SDPKBUtil() {
		super();
	}

	public static SDPKBUtil getInstance() {
		if (_instance == null) {
			_instance = new SDPKBUtil();
		}

		return _instance;
	}

	@Override
	public void addProtocolInfo() {
		addSLPv1();
		addSLPv2();
		addUPnPv1();
		addUPnPv2();
		addDNSSD();
		addCoAP();
	}

	@Override
	public void addUpdateHistory() {
		MessageFieldUpdate u1 = new MessageFieldUpdate(
				new MessageField("Message Length", MessageFieldType.MESSAGE_LENGTH),
				RequirementChange.CONTENT_LENGTH_CHANGE, UpdatePattern.CHANGE_FIELD_LENGTH,
				Functionality.CONTENT_PARSING, ProtocolName.SLPv1.toString(), ProtocolName.SLPv2.toString());
		MessageFieldUpdate u2 = new MessageFieldUpdate(new MessageField("Next Ext Offset", MessageFieldType.CONTROL_FLAG),
				RequirementChange.CONTROL_OPTION_ADDITION, UpdatePattern.CHANGE_FIELD_LENGTH,
				Functionality.MESSAGE_HANDLING, ProtocolName.SLPv1.toString(), ProtocolName.SLPv2.toString());
		MessageFieldUpdate u3 = new MessageFieldUpdate(new MessageField("Encoding", MessageFieldType.ENCODING),
				RequirementChange.ENCODING_INTEGRATION, UpdatePattern.DELETE_FIELD,
				Functionality.CONTENT_PARSING, ProtocolName.SLPv1.toString(), ProtocolName.SLPv2.toString());
		MessageFieldUpdate u4 = new MessageFieldUpdate(new MessageField("M", MessageFieldType.CONTROL_FLAG),
				RequirementChange.LANGUAGE_SUPPORT_CHANGE, UpdatePattern.DELETE_FIELD,
				Functionality.MESSAGE_HANDLING, ProtocolName.SLPv1.toString(), ProtocolName.SLPv2.toString());
		MessageFieldUpdate u5 = new MessageFieldUpdate(new MessageField("U", MessageFieldType.CONTROL_FLAG),
				RequirementChange.SECURITY_REQUIREMENT_CHANGE, UpdatePattern.DELETE_FIELD,
				Functionality.MESSAGE_HANDLING, ProtocolName.SLPv1.toString(), ProtocolName.SLPv2.toString());
		MessageFieldUpdate u6 = new MessageFieldUpdate(new MessageField("A", MessageFieldType.CONTROL_FLAG),
				RequirementChange.SECURITY_REQUIREMENT_CHANGE, UpdatePattern.DELETE_FIELD,
				Functionality.MESSAGE_HANDLING, ProtocolName.SLPv1.toString(), ProtocolName.SLPv2.toString());
		MessageFieldUpdate u7 = new MessageFieldUpdate(new MessageField("Language Code", MessageFieldType.LANGUAGE_CODE),
				RequirementChange.LANGUAGE_SUPPORT_CHANGE, UpdatePattern.DELETE_FIELD,
				Functionality.CONTENT_PARSING, ProtocolName.SLPv1.toString(), ProtocolName.SLPv2.toString());
		MessageFieldUpdate u8 = new MessageFieldUpdate(new MessageField("R", MessageFieldType.CONTROL_FLAG),
				RequirementChange.CONTROL_OPTION_ADDITION, UpdatePattern.ADD_NEW_FIELD,
				Functionality.MESSAGE_HANDLING, ProtocolName.SLPv1.toString(), ProtocolName.SLPv2.toString());
		MessageFieldUpdate u9 = new MessageFieldUpdate(new MessageField("Language Tag", MessageFieldType.LANGUAGE_TAG),
				RequirementChange.LANGUAGE_SUPPORT_CHANGE, UpdatePattern.ADD_NEW_FIELD,
				Functionality.CONTENT_PARSING, ProtocolName.SLPv1.toString(), ProtocolName.SLPv2.toString());
		MessageFieldUpdate u10 = new MessageFieldUpdate(new MessageField("Language Tag Length", MessageFieldType.LANGUAGE_TAG_LENGTH),
				RequirementChange.LANGUAGE_SUPPORT_CHANGE, UpdatePattern.ADD_NEW_FIELD,
				Functionality.CONTENT_PARSING, ProtocolName.SLPv1.toString(), ProtocolName.SLPv2.toString());
		MessageFieldUpdate u11 = new MessageFieldUpdate(new MessageField("Function Type", MessageFieldType.MESSAGE_TYPE),
				RequirementChange.CONTROL_OPTION_ADDITION, UpdatePattern.CHANGE_VOCA,
				Functionality.MESSAGE_HANDLING, ProtocolName.SLPv1.toString(), ProtocolName.SLPv2.toString());

		this.updateHistory.add(u1);
		this.updateHistory.add(u2);
		this.updateHistory.add(u3);
		this.updateHistory.add(u4);
		this.updateHistory.add(u5);
		this.updateHistory.add(u6);
		this.updateHistory.add(u7);
		this.updateHistory.add(u8);
		this.updateHistory.add(u9);
		this.updateHistory.add(u10);
		this.updateHistory.add(u11);
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
	
	public void addSLPv1() {
		SDP slp1 = new SDP(ProtocolName.SLPv1.toString());
		SDPMessage msg = new SDPMessage();
		msg.addField(new MessageField("Service Type", MessageFieldType.SERVICE_TYPE, MessageFieldLocation.BODY, "v"));
		msg.addField(new MessageField("Target Address", MessageFieldType.TARGET_ADDRESS, MessageFieldLocation.BODY, 32));
		msg.addField(new MessageField("Version", MessageFieldType.VERSION_INFO, MessageFieldLocation.HEADER, 8, 2));
		msg.addField(new MessageField("Function", MessageFieldType.MESSAGE_TYPE, MessageFieldLocation.HEADER, 8, SLPMessage.SRVRQST));
		msg.addField(new MessageField("Length", MessageFieldType.MESSAGE_LENGTH, MessageFieldLocation.HEADER, 16)); // no value yet
		msg.addField(new MessageField("Control", MessageFieldType.CONTROL_FLAG, MessageFieldLocation.HEADER, 16, (byte) 0x20)); 
		msg.addField(new MessageField("Language Code", MessageFieldType.LANGUAGE_CODE, MessageFieldLocation.HEADER, 16, 0));
		msg.addField(new MessageField("Char Encoding", MessageFieldType.ENCODING, MessageFieldLocation.HEADER, 8, 0));
		msg.addField(new MessageField("XID", MessageFieldType.SESSION_MGMT, MessageFieldLocation.HEADER, 16)); // no value yet

		slp1.addMessage(msg);
		this.pMap.put(ProtocolName.SLPv1, slp1);
	}

	public void addSLPv2() {
		SDP slp2 = new SDP(ProtocolName.SLPv2.toString());
		SDPMessage msg = new SDPMessage();
		msg.addField(new MessageField("Service Type", MessageFieldType.SERVICE_TYPE, MessageFieldLocation.BODY, "v"));
		msg.addField(new MessageField("Target Address", MessageFieldType.TARGET_ADDRESS, MessageFieldLocation.BODY, 32));
		msg.addField(new MessageField("Version", MessageFieldType.VERSION_INFO, MessageFieldLocation.HEADER, 8, 2));
		msg.addField(new MessageField("Function", MessageFieldType.MESSAGE_TYPE, MessageFieldLocation.HEADER, 8, SLPMessage.SRVRQST));
		msg.addField(new MessageField("Length", MessageFieldType.MESSAGE_LENGTH, MessageFieldLocation.HEADER, 24)); // no value yet
		msg.addField(new MessageField("Control", MessageFieldType.CONTROL_FLAG, MessageFieldLocation.HEADER, 40, (byte) 0x20));
		msg.addField(new MessageField("XID", MessageFieldType.SESSION_MGMT, MessageFieldLocation.HEADER, 16)); // no value yet
		msg.addField(new MessageField("Language Tag Length", MessageFieldType.LANGUAGE_TAG_LENGTH,MessageFieldLocation.HEADER, 16, 0)); 
		msg.addField(new MessageField("Language Tag", MessageFieldType.LANGUAGE_TAG, MessageFieldLocation.HEADER, "v", (new Locale("ko", "").getLanguage()))); 

		slp2.addMessage(msg);
		this.pMap.put(ProtocolName.SLPv2, slp2);
	}

	public void addUPnPv1() {
		SDP upnp1 = new SDP(ProtocolName.UPnPv1.toString());
		SDPMessage msg = new SDPMessage();
		msg.addField(new MessageField("Service Type", MessageFieldType.SERVICE_TYPE, MessageFieldLocation.HEADER, "v"));
		msg.addField(
				new MessageField("Target Address", MessageFieldType.TARGET_ADDRESS, MessageFieldLocation.HEADER, 32));
		msg.addField(new MessageField("Version Info", MessageFieldType.VERSION_INFO, MessageFieldLocation.HEADER, "v"));
		msg.addField(new MessageField("Message Type", MessageFieldType.MESSAGE_TYPE, MessageFieldLocation.HEADER, "v"));
		msg.addField(new MessageField("Control", MessageFieldType.CONTROL_FLAG, MessageFieldLocation.HEADER, "v"));
		msg.addField(new MessageField("Length", MessageFieldType.MESSAGE_LENGTH, MessageFieldLocation.HEADER, "v"));
		msg.addField(new MessageField("Encoding", MessageFieldType.ENCODING, MessageFieldLocation.HEADER, "v"));

		upnp1.addMessage(msg);
		this.pMap.put(ProtocolName.UPnPv1, upnp1);
	}

	public void addUPnPv2() {
		SDP upnp2 = new SDP(ProtocolName.UPnPv2.toString());
		SDPMessage msg = new SDPMessage();
		msg.addField(new MessageField("Service Type", MessageFieldType.SERVICE_TYPE, MessageFieldLocation.HEADER, "v"));
		msg.addField(
				new MessageField("Target Address", MessageFieldType.TARGET_ADDRESS, MessageFieldLocation.HEADER, 32));
		msg.addField(new MessageField("Version Info", MessageFieldType.VERSION_INFO, MessageFieldLocation.HEADER, "v"));
		msg.addField(new MessageField("Message Type", MessageFieldType.MESSAGE_TYPE, MessageFieldLocation.HEADER, "v"));
		msg.addField(new MessageField("Control", MessageFieldType.CONTROL_FLAG, MessageFieldLocation.HEADER, "v"));
		msg.addField(new MessageField("Length", MessageFieldType.MESSAGE_LENGTH, MessageFieldLocation.HEADER, "v"));
		msg.addField(new MessageField("Encoding", MessageFieldType.ENCODING, MessageFieldLocation.HEADER, "v"));

		upnp2.addMessage(msg);
		this.pMap.put(ProtocolName.UPnPv2, upnp2);
	}

	public void addDNSSD() {
		SDP dns = new SDP(ProtocolName.DNS_SD.toString());
		SDPMessage msg = new SDPMessage();
		msg.addField(new MessageField("Service Type", MessageFieldType.SERVICE_TYPE, MessageFieldLocation.BODY, "v"));
		msg.addField(
				new MessageField("Target Address", MessageFieldType.TARGET_ADDRESS, MessageFieldLocation.BODY, 32));
		msg.addField(new MessageField("Return Code", MessageFieldType.RETURN_CODE, MessageFieldLocation.HEADER, 4));
		msg.addField(new MessageField("Message Type", MessageFieldType.MESSAGE_TYPE, MessageFieldLocation.HEADER, 5));
		msg.addField(new MessageField("Control", MessageFieldType.CONTROL_FLAG, MessageFieldLocation.HEADER, 7));
		msg.addField(
				new MessageField("Session Management", MessageFieldType.SESSION_MGMT, MessageFieldLocation.HEADER, 16));
		msg.addField(new MessageField("Query Count", MessageFieldType.QUERY_COUNT, MessageFieldLocation.HEADER, 16));
		msg.addField(new MessageField("Answer Count", MessageFieldType.ANSWER_COUNT, MessageFieldLocation.HEADER, 16));

		dns.addMessage(msg);
		this.pMap.put(ProtocolName.DNS_SD, dns);
	}

	public void addCoAP() {
		SDP coap = new SDP(ProtocolName.COAP.toString());
		SDPMessage msg = new SDPMessage();
		msg.addField(new MessageField("Service Type", MessageFieldType.SERVICE_TYPE, MessageFieldLocation.BODY, "v"));
		msg.addField(
				new MessageField("Target Address", MessageFieldType.TARGET_ADDRESS, MessageFieldLocation.BODY, 32));
		msg.addField(new MessageField("Version Info", MessageFieldType.VERSION_INFO, MessageFieldLocation.HEADER, 2));
		msg.addField(new MessageField("Return Code", MessageFieldType.RETURN_CODE, MessageFieldLocation.HEADER, 8));
		msg.addField(new MessageField("Message Type", MessageFieldType.MESSAGE_TYPE, MessageFieldLocation.HEADER, 8));
		msg.addField(new MessageField("Control", MessageFieldType.CONTROL_FLAG, MessageFieldLocation.HEADER, 2));
		msg.addField(
				new MessageField("Session Management", MessageFieldType.SESSION_MGMT, MessageFieldLocation.HEADER, 16));
		msg.addField(
				new MessageField("Message Length", MessageFieldType.MESSAGE_LENGTH, MessageFieldLocation.HEADER, 4));

		coap.addMessage(msg);
		this.pMap.put(ProtocolName.COAP, coap);
	}
}
