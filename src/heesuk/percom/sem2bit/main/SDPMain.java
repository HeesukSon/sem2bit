package heesuk.percom.sem2bit.main;

import heesuk.percom.sem2bit.*;
import heesuk.percom.sem2bit.exception.DomainNotDefinedException;
import heesuk.percom.sem2bit.exception.LocalProtocolNotSpecifiedException;
import org.apache.log4j.PropertyConfigurator;

public class SDPMain {
	public static void main(String[] args) {
		PropertyConfigurator.configure(Constants.ROOT_DIR+"log4j.properties");

		long before = System.currentTimeMillis();
		try {
			ModificationController.getInstance().init();
		} catch (DomainNotDefinedException e) {
			e.printStackTrace();
		} catch (LocalProtocolNotSpecifiedException e) {
			e.printStackTrace();
		}
		ModificationController.getInstance().startMessageModification(ConfigUtil.getInstance().iteration_bound);
		long after = System.currentTimeMillis();
		ExperimentStat.getInstance().setTotalExpTime(after-before);
		ExperimentStat.getInstance().printStat();

		// print
		ProbeLogger.printLog();
		ProbeLogger.printStat();
	}
}
