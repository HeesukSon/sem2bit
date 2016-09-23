package heesuk.percom.sherlock.io.kb.sdp;

import java.util.HashMap;

public class SDPKBUtil {
	private static SDPKBUtil _instance;
	private HashMap<String, SDP> sdpMap;

	private SDPKBUtil() {
		this.sdpMap = new HashMap<String, SDP>();
		buildKB();
	}

	public static SDPKBUtil getInstance() {
		if (_instance == null) {
			_instance = new SDPKBUtil();
		}

		return _instance;
	}

	public void buildKB() {
		// add SDP message structure information
		addSLPv1();
		addSLPv2();
		addUPnPv1();
		addUPnPv2();
		addDNSSD();
		addCoAP();
		
		// add field existence probability
		computeStat();
	}
	
	public void computeStat(){
		
	}

	public void addSLPv1() {
		SDP slp1 = new SDP("SLPv1");
		SDPMessage msg = new SDPMessage();
		msg.addField(new MessageField("Service Type", MessageFieldType.SERVICE_TYPE, MessageFieldLocation.BODY, "v"));
		msg.addField(new MessageField("Target Address", MessageFieldType.TARGET_ADDRESS, MessageFieldLocation.BODY, 32));
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
		msg.addField(new MessageField("Service Type", MessageFieldType.SERVICE_TYPE, MessageFieldLocation.BODY, "v"));
		msg.addField(new MessageField("Target Address", MessageFieldType.TARGET_ADDRESS, MessageFieldLocation.BODY, 32));
		msg.addField(new MessageField("Version", MessageFieldType.VERSION_INFO, MessageFieldLocation.HEADER, 8));
		msg.addField(new MessageField("Function", MessageFieldType.MESSAGE_TYPE, MessageFieldLocation.HEADER, 8));
		msg.addField(new MessageField("Length", MessageFieldType.MESSAGE_LENGTH, MessageFieldLocation.HEADER, 24));
		msg.addField(new MessageField("Control", MessageFieldType.CONTROL_FLAG, MessageFieldLocation.HEADER, 40));
		msg.addField(new MessageField("XID", MessageFieldType.SESSION_MGMT, MessageFieldLocation.HEADER, 16));
		msg.addField(new MessageField("Language Tag Length", MessageFieldType.LANGUAGE_TAG_LENGTH, MessageFieldLocation.HEADER, 16));
		msg.addField(new MessageField("Language Tag", MessageFieldType.LANGUAGE_TAG, MessageFieldLocation.HEADER, "v"));
		
		slp2.addMessage(msg);
		this.sdpMap.put(slp2.getName(), slp2);
	}

	public void addUPnPv1() {
		SDP upnp1 = new SDP("UPnPv1");
		SDPMessage msg = new SDPMessage();
		msg.addField(new MessageField("Service Type", MessageFieldType.SERVICE_TYPE, MessageFieldLocation.HEADER, "v"));
		msg.addField(new MessageField("Target Address", MessageFieldType.TARGET_ADDRESS, MessageFieldLocation.HEADER, 32));
		msg.addField(new MessageField("Version Info", MessageFieldType.VERSION_INFO, MessageFieldLocation.HEADER, "v"));
		msg.addField(new MessageField("Message Type", MessageFieldType.MESSAGE_TYPE, MessageFieldLocation.HEADER, "v"));
		msg.addField(new MessageField("Control", MessageFieldType.CONTROL_FLAG, MessageFieldLocation.HEADER, "v"));
		msg.addField(new MessageField("Length", MessageFieldType.MESSAGE_LENGTH, MessageFieldLocation.HEADER, "v"));
		msg.addField(new MessageField("Encoding", MessageFieldType.ENCODING, MessageFieldLocation.HEADER, "v"));
		
		upnp1.addMessage(msg);
		this.sdpMap.put(upnp1.getName(), upnp1);
	}

	public void addUPnPv2() {
		SDP upnp2 = new SDP("UPnPv2");
		SDPMessage msg = new SDPMessage();
		msg.addField(new MessageField("Service Type", MessageFieldType.SERVICE_TYPE, MessageFieldLocation.HEADER, "v"));
		msg.addField(new MessageField("Target Address", MessageFieldType.TARGET_ADDRESS, MessageFieldLocation.HEADER, 32));
		msg.addField(new MessageField("Version Info", MessageFieldType.VERSION_INFO, MessageFieldLocation.HEADER, "v"));
		msg.addField(new MessageField("Message Type", MessageFieldType.MESSAGE_TYPE, MessageFieldLocation.HEADER, "v"));
		msg.addField(new MessageField("Control", MessageFieldType.CONTROL_FLAG, MessageFieldLocation.HEADER, "v"));
		msg.addField(new MessageField("Length", MessageFieldType.MESSAGE_LENGTH, MessageFieldLocation.HEADER, "v"));
		msg.addField(new MessageField("Encoding", MessageFieldType.ENCODING, MessageFieldLocation.HEADER, "v"));
		
		upnp2.addMessage(msg);
		this.sdpMap.put(upnp2.getName(), upnp2);
	}

	public void addDNSSD() {
		SDP mDNS = new SDP("DNS-SD/mDNS");
		SDPMessage msg = new SDPMessage();
		msg.addField(new MessageField("Service Type", MessageFieldType.SERVICE_TYPE, MessageFieldLocation.BODY, "v"));
		msg.addField(new MessageField("Target Address", MessageFieldType.TARGET_ADDRESS, MessageFieldLocation.BODY, 32));
		msg.addField(new MessageField("Return Code", MessageFieldType.RETURN_CODE, MessageFieldLocation.HEADER, 4));
		msg.addField(new MessageField("Message Type", MessageFieldType.MESSAGE_TYPE, MessageFieldLocation.HEADER, 5));
		msg.addField(new MessageField("Control", MessageFieldType.CONTROL_FLAG, MessageFieldLocation.HEADER, 7));
		msg.addField(new MessageField("Session Management", MessageFieldType.SESSION_MGMT, MessageFieldLocation.HEADER, 16));
		msg.addField(new MessageField("Query Count", MessageFieldType.QUERY_COUNT, MessageFieldLocation.HEADER, 16));
		msg.addField(new MessageField("Answer Count", MessageFieldType.ANSWER_COUNT, MessageFieldLocation.HEADER, 16));
		
		mDNS.addMessage(msg);
		this.sdpMap.put(mDNS.getName(), mDNS);
	}

	public void addCoAP() {
		SDP coap = new SDP("CoAP");
		SDPMessage msg = new SDPMessage();
		msg.addField(new MessageField("Service Type", MessageFieldType.SERVICE_TYPE, MessageFieldLocation.BODY, "v"));
		msg.addField(new MessageField("Target Address", MessageFieldType.TARGET_ADDRESS, MessageFieldLocation.BODY, 32));
		msg.addField(new MessageField("Version Info", MessageFieldType.VERSION_INFO, MessageFieldLocation.HEADER, 2));
		msg.addField(new MessageField("Return Code", MessageFieldType.RETURN_CODE, MessageFieldLocation.HEADER, 8));
		msg.addField(new MessageField("Message Type", MessageFieldType.MESSAGE_TYPE, MessageFieldLocation.HEADER, 8));
		msg.addField(new MessageField("Control", MessageFieldType.CONTROL_FLAG, MessageFieldLocation.HEADER, 2));
		msg.addField(new MessageField("Session Management", MessageFieldType.SESSION_MGMT, MessageFieldLocation.HEADER, 16));
		msg.addField(new MessageField("Message Length", MessageFieldType.MESSAGE_LENGTH, MessageFieldLocation.HEADER, 4));
		
		coap.addMessage(msg);
		this.sdpMap.put(coap.getName(), coap);
	}
}
