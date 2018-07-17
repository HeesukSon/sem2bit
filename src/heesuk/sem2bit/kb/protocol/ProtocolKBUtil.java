package heesuk.sem2bit.kb.protocol;

import heesuk.sem2bit.ConfigUtil;
import heesuk.sem2bit.kb.protocol.enums.Domain;
import heesuk.sem2bit.kb.protocol.enums.MessageFieldType;
import heesuk.sem2bit.kb.protocol.enums.ProtocolName;
import heesuk.sem2bit.kb.protocol.enums.UpdatePattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

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
	protected ArrayList<Integer> usedModSeqBound;

	protected ProtocolKBUtil() {
		this.pMap = new HashMap<ProtocolName, Protocol>();
		this.field_ex_prob = new HashMap<String, Float>();
		this.updateHistory = new ArrayList<MessageFieldUpdate>();
		this.update_pattern_prob = new HashMap<String, Float>();

		this.localProtocol = ConfigUtil.getInstance().localP;
		this.usedModSeqBound = new ArrayList<Integer>();

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
		LOG.info("starting adding protocol knowledge base...");
		addProtocolInfo();
		LOG.info("Protocol knowledge base addition complete.");

		// add field existence probability
		computeStat();

		// add protocol update history
		addUpdateHistory();
		computeUpdateStat();
		computeModSeqBound();
	}
	
	public void computeModSeqBound(){
		if(ConfigUtil.getInstance().seqBoundRandom == true){
			Random r = new Random();
			int newBound;

			do{
				newBound = (int)r.nextGaussian()+this.modSeqBound;
			} while(newBound <= 0 || isBoundUsed(newBound));
			this.modSeqBound = newBound;
			LOG.info("modSeqBound is set to {}.",modSeqBound);
			this.usedModSeqBound.add(newBound);
		}else{
			this.modSeqBound = ConfigUtil.getInstance().seqBound;
			LOG.info("modSeqBound is set to {}.",modSeqBound);
		}
	}

	private boolean isBoundUsed(int bound){
		boolean used = false;

		for(Integer usedBound : this.usedModSeqBound){
			if(usedBound.intValue() == bound){
				used = true;
				break;
			}
		}

		return used;
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
