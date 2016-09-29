package heesuk.percom.sherlock.io;

public class ExperimentStat {
	private static ExperimentStat _instance;
	private long expRoundCnt; // computed, printed
	private long kbLoadingTime; // computed, printed
	private long probTreeBuildTime; // computed, printed
	private long seqTreeBuildTime; // computed, printed
	private long seqComputeTimeAvg; //
	private long seqComputeTimeTotal; // computed, printed
	private long msgComposeTimeAvg; //
	private long msgComposeTimeTotal; // computed, printed
	private long msgTransTimeAvg; //
	private long msgTransTimeTotal; // computed, printed
	private long totalExpTime; // computed, printed

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

	public void printStat() {
		System.out.println("\n\n########### EXPERIMENT RESULT ###############");
		System.out.println("- Elapsed Experiment Time in Overall: " + totalExpTime + " (ms)");
		System.out.println("- Tried # of Modification Rounds: " + expRoundCnt);
		System.out.println("- Elapsed Time for SDP KB Loading: " + kbLoadingTime + " (ms, "
				+ ((float) kbLoadingTime / totalExpTime * 100) + "%)");
		System.out.println("- Elapsed Time for Probability Tree Construction: " + probTreeBuildTime + " (ms, "
				+ ((float) probTreeBuildTime / totalExpTime * 100) + "%)");
		System.out.println("- Elapsed Time for Sequence Planning Tree Initialization: " + seqTreeBuildTime + " (ms, "
				+ ((float) seqTreeBuildTime / totalExpTime * 100) + "%)");
		System.out.println("- Elapsed Time for Modification Sequence Computation in Total: " + seqComputeTimeTotal
				+ " (ms, " + ((float) seqComputeTimeTotal / totalExpTime * 100) + "%), Average = "
				+ ((float) seqComputeTimeTotal / expRoundCnt) + " (ms)");
		System.out.println("- Elapsed Time for Modified Message Composition in Total: " + msgComposeTimeTotal + " (ms,"
				+ ((float) msgComposeTimeTotal / totalExpTime * 100) + "%), Average = "
				+ ((float) msgComposeTimeTotal / expRoundCnt) + " (ms)");
		System.out.println("- Elapsed Time for Modified Message Transmission including Waiting in Total: "
				+ msgTransTimeTotal + " (ms, " + ((float) msgTransTimeTotal / totalExpTime * 100) + "%), Average = "
				+ ((float) msgTransTimeTotal / expRoundCnt) + " (ms)");
		System.out.println("- Socket Timeout: 100 (ms)");
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

	public long getTotalExpTime() {
		return totalExpTime;
	}
}
