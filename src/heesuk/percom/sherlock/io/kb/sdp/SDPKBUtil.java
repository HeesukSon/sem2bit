package heesuk.percom.sherlock.io.kb.sdp;

import java.util.HashMap;

public class SDPKBUtil {
	private static SDPKBUtil _instance;
	private HashMap<String, SDP> sdpMap;

	private SDPKBUtil() {
		buildKB();
		this.sdpMap = new HashMap<String, SDP>();
	}

	public static SDPKBUtil getInstance() {
		if (_instance == null) {
			_instance = new SDPKBUtil();
		}

		return _instance;
	}

	public void buildKB() {
		addSLPv1();
		addSLPv2();
		addUPnPv1();
		addUPnPv2();
		addDNSSD();
		addCoAP();
	}

	public void addSLPv1() {
		SDP slp1 = new SDP("SLPv1");
		SDPMessage msg = new SDPMessage();
		msg.addField(new MessageField("Version", MessageFieldType.VERSION_INFO, MessageFieldLocation.HEADER, 8));
		msg.addField(new MessageField("Function", MessageFieldType.MESSAGE_TYPE, MessageFieldLocation.HEADER, 8));
		msg.addField(new MessageField("Length", MessageFieldType.MESSAGE_LENGTH, MessageFieldLocation.HEADER, 16));
		msg.addField(new MessageField("Control", MessageFieldType.CONTROL_FLAG, MessageFieldLocation.HEADER, 16));
		msg.addField(new MessageField("Language Code", MessageFieldType.LANGUAGE_CODE, MessageFieldLocation.HEADER, 16));
		msg.addField(new MessageField("Char Encoding", MessageFieldType.ENCODING, MessageFieldLocation.HEADER, 8));
		msg.addField(new MessageField("XID", MessageFieldType.SESSION_MGMT, MessageFieldLocation.HEADER, 16));
		
		slp1.addMessage(msg);
		this.sdpMap.put(slp1.getName(), slp1);
	}
	
	public void addSLPv2() {
		SDP slp2 = new SDP("SLPv2");
		SDPMessage msg = new SDPMessage();
		msg.addField(new MessageField("Version", MessageFieldType.VERSION_INFO, MessageFieldLocation.HEADER, 8));
		msg.addField(new MessageField("Function", MessageFieldType.MESSAGE_TYPE, MessageFieldLocation.HEADER, 8));
		msg.addField(new MessageField("Length", MessageFieldType.MESSAGE_LENGTH, MessageFieldLocation.HEADER, 16));
		msg.addField(new MessageField("Control", MessageFieldType.CONTROL_FLAG, MessageFieldLocation.HEADER, 16));
		msg.addField(new MessageField("Language Code", MessageFieldType.LANGUAGE_CODE, MessageFieldLocation.HEADER, 16));
		msg.addField(new MessageField("Char Encoding", MessageFieldType.ENCODING, MessageFieldLocation.HEADER, 8));
		msg.addField(new MessageField("XID", MessageFieldType.SESSION_MGMT, MessageFieldLocation.HEADER, 16));
		
		slp2.addMessage(msg);
		this.sdpMap.put(slp2.getName(), slp2);
	}

	public void addUPnPv1() {

	}

	public void addUPnPv2() {

	}

	public void addDNSSD() {

	}

	public void addCoAP() {

	}
}
