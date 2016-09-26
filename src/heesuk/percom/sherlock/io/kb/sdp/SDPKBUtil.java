package heesuk.percom.sherlock.io.kb.sdp;

import java.util.ArrayList;
import java.util.HashMap;

public class SDPKBUtil {
	private static SDPKBUtil _instance;
	private HashMap<SDPName, SDP> sdpMap;
	private HashMap<String, Float> field_ex_prob;
	private HashMap<String, Float> update_pattern_prob;
	private ArrayList<MessageFieldUpdate> updateHistory;
	final private SDPName localSDP = SDPName.SLPv1;
	private int modSeqBound;

	private SDPKBUtil() {
		this.sdpMap = new HashMap<SDPName, SDP>();
		this.field_ex_prob = new HashMap<String, Float>();
		this.updateHistory = new ArrayList<MessageFieldUpdate>();
		this.update_pattern_prob = new HashMap<String, Float>();
		
		for(UpdatePattern p : UpdatePattern.values()){
			this.update_pattern_prob.put(p.toString(), new Float(0f));
		}
		for (MessageFieldType type : MessageFieldType.values()) {
			this.field_ex_prob.put(type.toString(), new Float(0f));
		}
	}

	public static SDPKBUtil getInstance() {
		if (_instance == null) {
			_instance = new SDPKBUtil();
		}

		return _instance;
	}
	
	public SDP getSDP(SDPName sdp){
		return this.sdpMap.get(sdp);
	}

	public void buildKB() {
		// add SDP message structure information
		System.out.println("start adding SDP knowledge base...");
		addSLPv1();
		addSLPv2();
		addUPnPv1();
		addUPnPv2();
		addDNSSD();
		addCoAP();
		System.out.println("SDP knowledge base addition is done!");

		// add field existence probability
		computeStat();

		// add SDP update history
		addUpdateHistory();
		computeUpdateStat();
		computeModSeqBound();
	}	
	
	public void computeModSeqBound(){
		// TODO algorithm should be added later
		this.modSeqBound = 8;
	}
	
	public int getModSeqBound(){
		return this.modSeqBound;
	}

	public void computeUpdateStat() {
		int size = this.updateHistory.size();
		
		for(MessageFieldUpdate update : this.updateHistory){
			this.update_pattern_prob.replace(update.getPattern().toString(), this.update_pattern_prob.get(update.getPattern().toString())+1);
		}
		
		for(String p : this.update_pattern_prob.keySet()){
			this.update_pattern_prob.replace(p, this.update_pattern_prob.get(p)/size);
		}
	}
	
	public int getRequirementChangeCount(String req){
		int cnt = 0;
		for(MessageFieldUpdate update : this.updateHistory){
			if(update.getReqChange().toString().equals(req))
				cnt++;
		}
		
		return cnt;
	}

	public void printStat() {
		System.out.println("\n##### Field Existence Probability #####");
		for (String key : this.field_ex_prob.keySet()) {
			System.out.printf("%s\t:\t%f\n", key, this.field_ex_prob.get(key));
		}
		
		System.out.println("\n##### Update Pattern Update Probability #####");
		for(String p : this.update_pattern_prob.keySet()){
			System.out.printf("%s\t:\t%f\n", p.toString(), this.update_pattern_prob.get(p));
		}
	}

	public void computeStat() {
		int sdp_num = this.sdpMap.size();
		for (SDPName key : sdpMap.keySet()) {
			SDP sdp = sdpMap.get(key);
			SDPMessage msg = sdp.getMesage();
			
			for (MessageField field : msg.getFieldList()) {
				this.field_ex_prob.replace(field.getType().toString(),
						this.field_ex_prob.get(field.getType().toString()) + 1);
			}
		}

		for (String type : this.field_ex_prob.keySet()) {
			this.field_ex_prob.replace(type, this.field_ex_prob.get(type) / sdp_num);
		}
		System.out.println("Field existance probability computation is done.");
	}
	
	public SDPName getLocalSDPName(){
		return this.localSDP;
	}
	
	public SDP getLocalSDP(){
		return this.sdpMap.get(this.localSDP);
	}
	
	public ArrayList<MessageFieldUpdate> getUpdateHistory(){
		return this.updateHistory;
	}
	
	public float getFieldExProb(String field){
		return this.field_ex_prob.get(field);
	}
	
	public float getUpdatePatternProb(String pattern){
		return this.update_pattern_prob.get(pattern);
	}
	
	public void addUpdateHistory() {
		MessageFieldUpdate u1 = new MessageFieldUpdate(
				new MessageField("Message Length", MessageFieldType.MESSAGE_LENGTH),
				RequirementChange.CONTENT_LENGTH_CHANGE, UpdatePattern.CHANGE_FIELD_LENGTH,
				Functionality.CONTENT_PARSING, SDPName.SLPv1.toString(), SDPName.SLPv2.toString());
		MessageFieldUpdate u2 = new MessageFieldUpdate(new MessageField("Next Ext Offset", MessageFieldType.CONTROL_FLAG),
				RequirementChange.CONTROL_OPTION_ADDITION, UpdatePattern.CHANGE_FIELD_LENGTH,
				Functionality.MESSAGE_HANDLING, SDPName.SLPv1.toString(), SDPName.SLPv2.toString());
		MessageFieldUpdate u3 = new MessageFieldUpdate(new MessageField("Encoding", MessageFieldType.ENCODING),
				RequirementChange.ENCODING_INTEGRATION, UpdatePattern.DELETE_FIELD,
				Functionality.CONTENT_PARSING, SDPName.SLPv1.toString(), SDPName.SLPv2.toString());
		MessageFieldUpdate u4 = new MessageFieldUpdate(new MessageField("M", MessageFieldType.CONTROL_FLAG),
				RequirementChange.LANGUAGE_SUPPORT_CHANGE, UpdatePattern.DELETE_FIELD,
				Functionality.MESSAGE_HANDLING, SDPName.SLPv1.toString(), SDPName.SLPv2.toString());
		MessageFieldUpdate u5 = new MessageFieldUpdate(new MessageField("U", MessageFieldType.CONTROL_FLAG),
				RequirementChange.SECURITY_REQUIREMENT_CHANGE, UpdatePattern.DELETE_FIELD,
				Functionality.MESSAGE_HANDLING, SDPName.SLPv1.toString(), SDPName.SLPv2.toString());
		MessageFieldUpdate u6 = new MessageFieldUpdate(new MessageField("A", MessageFieldType.CONTROL_FLAG),
				RequirementChange.SECURITY_REQUIREMENT_CHANGE, UpdatePattern.DELETE_FIELD,
				Functionality.MESSAGE_HANDLING, SDPName.SLPv1.toString(), SDPName.SLPv2.toString());
		MessageFieldUpdate u7 = new MessageFieldUpdate(new MessageField("Language Code", MessageFieldType.LANGUAGE_CODE),
				RequirementChange.LANGUAGE_SUPPORT_CHANGE, UpdatePattern.DELETE_FIELD,
				Functionality.CONTENT_PARSING, SDPName.SLPv1.toString(), SDPName.SLPv2.toString());
		MessageFieldUpdate u8 = new MessageFieldUpdate(new MessageField("R", MessageFieldType.CONTROL_FLAG),
				RequirementChange.CONTROL_OPTION_ADDITION, UpdatePattern.ADD_NEW_FIELD,
				Functionality.MESSAGE_HANDLING, SDPName.SLPv1.toString(), SDPName.SLPv2.toString());
		MessageFieldUpdate u9 = new MessageFieldUpdate(new MessageField("Language Tag", MessageFieldType.LANGUAGE_TAG),
				RequirementChange.LANGUAGE_SUPPORT_CHANGE, UpdatePattern.ADD_NEW_FIELD,
				Functionality.CONTENT_PARSING, SDPName.SLPv1.toString(), SDPName.SLPv2.toString());
		MessageFieldUpdate u10 = new MessageFieldUpdate(new MessageField("Language Tag Length", MessageFieldType.LANGUAGE_TAG_LENGTH),
				RequirementChange.LANGUAGE_SUPPORT_CHANGE, UpdatePattern.ADD_NEW_FIELD,
				Functionality.CONTENT_PARSING, SDPName.SLPv1.toString(), SDPName.SLPv2.toString());
		MessageFieldUpdate u11 = new MessageFieldUpdate(new MessageField("Function Type", MessageFieldType.MESSAGE_TYPE),
				RequirementChange.CONTROL_OPTION_ADDITION, UpdatePattern.CHANGE_VOCA,
				Functionality.MESSAGE_HANDLING, SDPName.SLPv1.toString(), SDPName.SLPv2.toString());

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

	public void addSLPv1() {
		SDP slp1 = new SDP(SDPName.SLPv1.toString());
		SDPMessage msg = new SDPMessage();
		msg.addField(new MessageField("Service Type", MessageFieldType.SERVICE_TYPE, MessageFieldLocation.BODY, "v"));
		msg.addField(
				new MessageField("Target Address", MessageFieldType.TARGET_ADDRESS, MessageFieldLocation.BODY, 32));
		msg.addField(new MessageField("Version", MessageFieldType.VERSION_INFO, MessageFieldLocation.HEADER, 8));
		msg.addField(new MessageField("Function", MessageFieldType.MESSAGE_TYPE, MessageFieldLocation.HEADER, 8));
		msg.addField(new MessageField("Length", MessageFieldType.MESSAGE_LENGTH, MessageFieldLocation.HEADER, 16));
		msg.addField(new MessageField("Control", MessageFieldType.CONTROL_FLAG, MessageFieldLocation.HEADER, 16));
		msg.addField(
				new MessageField("Language Code", MessageFieldType.LANGUAGE_CODE, MessageFieldLocation.HEADER, 16));
		msg.addField(new MessageField("Char Encoding", MessageFieldType.ENCODING, MessageFieldLocation.HEADER, 8));
		msg.addField(new MessageField("XID", MessageFieldType.SESSION_MGMT, MessageFieldLocation.HEADER, 16));

		slp1.addMessage(msg);
		this.sdpMap.put(SDPName.SLPv1, slp1);
	}

	public void addSLPv2() {
		SDP slp2 = new SDP(SDPName.SLPv2.toString());
		SDPMessage msg = new SDPMessage();
		msg.addField(new MessageField("Service Type", MessageFieldType.SERVICE_TYPE, MessageFieldLocation.BODY, "v"));
		msg.addField(
				new MessageField("Target Address", MessageFieldType.TARGET_ADDRESS, MessageFieldLocation.BODY, 32));
		msg.addField(new MessageField("Version", MessageFieldType.VERSION_INFO, MessageFieldLocation.HEADER, 8));
		msg.addField(new MessageField("Function", MessageFieldType.MESSAGE_TYPE, MessageFieldLocation.HEADER, 8));
		msg.addField(new MessageField("Length", MessageFieldType.MESSAGE_LENGTH, MessageFieldLocation.HEADER, 24));
		msg.addField(new MessageField("Control", MessageFieldType.CONTROL_FLAG, MessageFieldLocation.HEADER, 40));
		msg.addField(new MessageField("XID", MessageFieldType.SESSION_MGMT, MessageFieldLocation.HEADER, 16));
		msg.addField(new MessageField("Language Tag Length", MessageFieldType.LANGUAGE_TAG_LENGTH,
				MessageFieldLocation.HEADER, 16));
		msg.addField(new MessageField("Language Tag", MessageFieldType.LANGUAGE_TAG, MessageFieldLocation.HEADER, "v"));

		slp2.addMessage(msg);
		this.sdpMap.put(SDPName.SLPv2, slp2);
	}

	public void addUPnPv1() {
		SDP upnp1 = new SDP(SDPName.UPnPv1.toString());
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
		this.sdpMap.put(SDPName.UPnPv1, upnp1);
	}

	public void addUPnPv2() {
		SDP upnp2 = new SDP(SDPName.UPnPv2.toString());
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
		this.sdpMap.put(SDPName.UPnPv2, upnp2);
	}

	public void addDNSSD() {
		SDP dns = new SDP(SDPName.DNS_SD.toString());
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
		this.sdpMap.put(SDPName.DNS_SD, dns);
	}

	public void addCoAP() {
		SDP coap = new SDP(SDPName.COAP.toString());
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
		this.sdpMap.put(SDPName.COAP, coap);
	}
}
