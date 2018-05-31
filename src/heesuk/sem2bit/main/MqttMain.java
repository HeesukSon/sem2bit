package heesuk.sem2bit.main;

import heesuk.sem2bit.*;
import heesuk.sem2bit.exception.DomainNotDefinedException;
import heesuk.sem2bit.exception.LocalProtocolNotSpecifiedException;
import org.apache.log4j.PropertyConfigurator;

public class MqttMain {
	public static void main(String[] args) throws DomainNotDefinedException, LocalProtocolNotSpecifiedException {
		PropertyConfigurator.configure(Constants.ROOT_DIR+"log4j.properties");

		long before = System.currentTimeMillis();
		ModificationController.getInstance().init();
		ModificationController.getInstance().startMessageModification(ConfigUtil.getInstance().iteration_bound);
		long after = System.currentTimeMillis();
		//ExperimentStat.getInstance().setTotalExpTime(after-before);
		//ExperimentStat.getInstance().printStat();

		// print
		//ProbeLogger.printLog();
		//ProbeLogger.printStat();
	}
}
