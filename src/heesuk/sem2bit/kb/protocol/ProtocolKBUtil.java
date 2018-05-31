package heesuk.sem2bit.kb.protocol;

import heesuk.sem2bit.ConfigUtil;
import heesuk.sem2bit.ProbeLogger;
import heesuk.sem2bit.kb.protocol.enums.MessageFieldType;
import heesuk.sem2bit.kb.protocol.enums.ProtocolName;
import heesuk.sem2bit.kb.protocol.enums.UpdatePattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * A subclass of ProtocoKBUtil is supposed to be called in a Singleton
 * design. So please add getInstance() method when extending ProtocolKBUtil.
 */
public class ProtocolKBUtil implements IProtocolKBUtil{
	private static final Logger LOG = LoggerFactory.getLogger(ProtocolKBUtil.class);

	private static ProtocolKBUtil _instance;

	protected HashMap<ProtocolName, Protocol> pMap;
	protected HashMap<String, Float> field_ex_prob;
	protected HashMap<String, Float> update_pattern_prob;
	protected ArrayList<MessageFieldUpdate> updateHistory;
	protected ProtocolName localProtocol;
	protected int modSeqBound;

	protected ProtocolKBUtil() {
		this.pMap = new HashMap<ProtocolName, Protocol>();
		this.field_ex_prob = new HashMap<String, Float>();
		this.updateHistory = new ArrayList<MessageFieldUpdate>();
		this.update_pattern_prob = new HashMap<String, Float>();

		this.localProtocol = ConfigUtil.getInstance().localP;

		for(UpdatePattern p : UpdatePattern.values()){
			this.update_pattern_prob.put(p.toString(), new Float(0f));
		}
		for (MessageFieldType type : MessageFieldType.values()) {
			this.field_ex_prob.put(type.toString(), new Float(0f));
		}
	}

	public static ProtocolKBUtil getInstance() {
		if (_instance == null) {
			_instance = new ProtocolKBUtil();
		}

		return _instance;
	}
	
	public Protocol getProtocol(ProtocolName name){
		return this.pMap.get(name);
	}

	public void buildKB() {
		// add IoTProtocol message structure information
		ProbeLogger.appendLogln("tree","start adding IoTProtocol knowledge base...");
		LOG.info("starting adding protocol knowledge base...");
		addProtocolInfo();
		ProbeLogger.appendLogln("tree","IoTProtocol knowledge base addition is done!");
		LOG.info("Protocol knowledge base addition complete.");

		// add field existence probability
		computeStat();

		// add protocol update history
		addUpdateHistory();
		computeUpdateStat();
		computeModSeqBound();
	}
	
	public void computeModSeqBound(){
		// TODO algorithm should be added later
		this.modSeqBound = 7;
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
		ProbeLogger.appendLogln("tree","\n##### Field Existence Probability #####");
		for (String key : this.field_ex_prob.keySet()) {
			System.out.printf("%s\t:\t%f\n", key, this.field_ex_prob.get(key));
		}
		
		ProbeLogger.appendLogln("tree","\n##### Update Pattern Update Probability #####");
		for(String p : this.update_pattern_prob.keySet()){
			System.out.printf("%s\t:\t%f\n", p.toString(), this.update_pattern_prob.get(p));
		}
	}

	public void computeStat() {
		int pNum = this.pMap.size();
		for (ProtocolName key : pMap.keySet()) {
			Protocol p = pMap.get(key);
			ProtocolMessage msg = p.getMessage();
			
			for (MessageField field : msg.getFieldList()) {
				this.field_ex_prob.replace(field.getType().toString(),
						this.field_ex_prob.get(field.getType().toString()) + 1);
			}
		}

		for (String type : this.field_ex_prob.keySet()) {
			this.field_ex_prob.replace(type, this.field_ex_prob.get(type) / pNum);
		}
		ProbeLogger.appendLogln("tree","Field existance probability computation is done.");
	}
	
	public ProtocolName getLocalProtocolName(){
		return this.localProtocol;
	}
	
	public Protocol getLocalProtocol(){
		return this.pMap.get(this.localProtocol);
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

	@Override
	public void addProtocolInfo() {

	}

	@Override
	public void addUpdateHistory() {

	}

	@Override
	public MessageField getMsgField(String fType) {
		return null;
	}

	@Override
	public int getNewFieldLength(String fName) {
		return 0;
	}
}
