package heesuk.sem2bit.main;

import heesuk.sem2bit.exception.DomainNotDefinedException;
import heesuk.sem2bit.exception.LocalProtocolNotSpecifiedException;
import heesuk.sem2bit.*;
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
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		ExperimentStat.getInstance().printStat();
	}
}
