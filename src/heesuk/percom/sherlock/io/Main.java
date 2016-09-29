package heesuk.percom.sherlock.io;

public class Main {
	public static void main(String[] args) {
		long before = System.currentTimeMillis();
		ModificationController.getInstance().init();
		ModificationController.getInstance().startMessageModification(500);
		long after = System.currentTimeMillis();
		ExperimentStat.getInstance().setTotalExpTime(after-before);
		ExperimentStat.getInstance().printStat();
	}
}
