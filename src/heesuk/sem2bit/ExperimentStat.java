package heesuk.sem2bit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExperimentStat {
	private static final Logger LOG = LoggerFactory.getLogger(ExperimentStat.class);
	private static ExperimentStat _instance;

	private long expRoundCnt;
	private int successRound;
	private long kbLoadingTime;
	private long probTreeBuildTime;
	private long seqTreeBuildTime;
	private long seqComputeTimeAvg;
	private long seqComputeTimeTotal;
	private long msgComposeTimeAvg;
	private long msgComposeTimeTotal;
	private long msgTransTimeAvg;
	private long msgTransTimeTotal;
	private long totalExpTime;
	

	private ExperimentStat() {
		this.expRoundCnt = 0;
		this.seqComputeTimeTotal = 0;
		this.msgComposeTimeTotal = 0;
		this.msgTransTimeTotal = 0;
	}

	public static ExperimentStat getInstance() {
		if (_instance == null) {
			_instance = new ExperimentStat();
		}

		return _instance;
	}

	public void increaseExpRoundCnt() {
		this.expRoundCnt += 1;
	}
	public void setExpRoundCnt(int cnt){
		if(expRoundCnt == 0){
			this.expRoundCnt = cnt;
		}else if(expRoundCnt < cnt){
			// do nothing
		}else{
			expRoundCnt = cnt;
		}
	}

	public void printStat() {
		LOG.info("\n\n########### EXPERIMENT RESULT ###############");
		LOG.info("- Elapsed Experiment Time in Overall: " + totalExpTime + " (ms)");
		LOG.info("- Tried # of Modification Rounds: " + expRoundCnt);
		LOG.info("- Success Round: "+successRound);
		LOG.info("- Elapsed Time for IoTProtocol KB Loading: " + kbLoadingTime + " (ms, "
				+ ((float) kbLoadingTime / totalExpTime * 100) + "%)");
		LOG.info("- Elapsed Time for Probability Tree Construction: " + probTreeBuildTime + " (ms, "
				+ ((float) probTreeBuildTime / totalExpTime * 100) + "%)");
		LOG.info("- Elapsed Time for Sequence Planning Tree Initialization: " + seqTreeBuildTime + " (ms, "
				+ ((float) seqTreeBuildTime / totalExpTime * 100) + "%)");
		LOG.info("- Elapsed Time for Modification Sequence Computation in Total: " + seqComputeTimeTotal
				+ " (ms, " + ((float) seqComputeTimeTotal / totalExpTime * 100) + "%), Average = "
				+ ((float) seqComputeTimeTotal / expRoundCnt) + " (ms)");
		LOG.info("- Elapsed Time for Modified Message Composition in Total: " + msgComposeTimeTotal + " (ms,"
				+ ((float) msgComposeTimeTotal / totalExpTime * 100) + "%), Average = "
				+ ((float) msgComposeTimeTotal / expRoundCnt) + " (ms)");
		LOG.info("- Elapsed Time for Modified Message Transmission including Waiting in Total: "
				+ msgTransTimeTotal + " (ms, " + ((float) msgTransTimeTotal / totalExpTime * 100) + "%), Average = "
				+ ((float) msgTransTimeTotal / expRoundCnt) + " (ms)");
		LOG.info("- Socket Timeout: "+ConfigUtil.getInstance().tcp_timeout+" (ms)");
	}

	//////////////////// getters & setters ////////////////////

	public long getExpRoundCnt() {
		return expRoundCnt;
	}

	public static void set_instance(ExperimentStat _instance) {
		ExperimentStat._instance = _instance;
	}

	public void setExpRoundCnt(long expRoundCnt) {
		this.expRoundCnt = expRoundCnt;
	}

	public void setKbLoadingTime(long kbLoadingTime) {
		this.kbLoadingTime = kbLoadingTime;
	}

	public void setProbTreeBuildTime(long probTreeBuildTime) {
		this.probTreeBuildTime = probTreeBuildTime;
	}

	public void setSeqTreeBuildTime(long seqTreeBuildTime) {
		this.seqTreeBuildTime = seqTreeBuildTime;
	}

	public void setSeqComputeTimeAvg(long seqComputeTimeAvg) {
		this.seqComputeTimeAvg = seqComputeTimeAvg;
	}

	public void setSeqComputeTimeTotal(long seqComputeTimeTotal) {
		this.seqComputeTimeTotal = seqComputeTimeTotal;
	}

	public void setMsgComposeTimeAvg(long msgComposeTimeAvg) {
		this.msgComposeTimeAvg = msgComposeTimeAvg;
	}

	public void setMsgComposeTimeTotal(long msgComposeTimeTotal) {
		this.msgComposeTimeTotal = msgComposeTimeTotal;
	}

	public void setMsgTransTimeAvg(long msgTransTimeAvg) {
		this.msgTransTimeAvg = msgTransTimeAvg;
	}

	public void setMsgTransTimeTotal(long msgTransTimeTotal) {
		this.msgTransTimeTotal = msgTransTimeTotal;
	}
	public void addMsgTransTimeTotal(long msgTransTime){
		this.msgTransTimeTotal += msgTransTime;
	}

	public void setTotalExpTime(long totalExpTime) {
		this.totalExpTime = totalExpTime;
	}

	public long getKbLoadingTime() {
		return kbLoadingTime;
	}

	public long getProbTreeBuildTime() {
		return probTreeBuildTime;
	}

	public long getSeqTreeBuildTime() {
		return seqTreeBuildTime;
	}

	public long getSeqComputeTimeAvg() {
		return seqComputeTimeAvg;
	}

	public long getSeqComputeTimeTotal() {
		return seqComputeTimeTotal;
	}

	public long getMsgComposeTimeAvg() {
		return msgComposeTimeAvg;
	}

	public long getMsgComposeTimeTotal() {
		return msgComposeTimeTotal;
	}

	public long getMsgTransTimeAvg() {
		return msgTransTimeAvg;
	}

	public long getMsgTransTimeTotal() {
		return msgTransTimeTotal;
	}

	public long getTotalExpTime()
	{
		return totalExpTime;
	}

	public void setSuccessRound(int rnd){
		this.successRound = rnd;
	}

	public int getSuccessRound(){
		return this.successRound;
	}
}
