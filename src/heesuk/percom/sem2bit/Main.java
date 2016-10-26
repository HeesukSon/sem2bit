package heesuk.percom.sem2bit;

public class Main {
	public static void main(String[] args) {
		long before = System.currentTimeMillis();
		ModificationController.getInstance().init();
		ModificationController.getInstance().startMessageModification(Configurations.iteration_bound);
		long after = System.currentTimeMillis();
		ExperimentStat.getInstance().setTotalExpTime(after-before);
		ExperimentStat.getInstance().printStat();
	}
}
