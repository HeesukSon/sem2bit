package ch.ethz.iks.slp.test;

import ch.ethz.iks.slp.Locator;
import ch.ethz.iks.slp.ServiceLocationManager;
import ch.ethz.iks.slp.ServiceLocationException;
import ch.ethz.iks.slp.ServiceLocationEnumeration;
import ch.ethz.iks.slp.ServiceType;
import heesuk.percom.sem2bit.Constants;
import heesuk.percom.sem2bit.kb.protocol.enums.ProtocolName;
import heesuk.percom.sem2bit.kb.protocol.sdp.SDPKBUtil;
import org.apache.log4j.PropertyConfigurator;

import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Locale;

public class Lookup {

	public static void main(String[] args) throws ServiceLocationException, SocketTimeoutException, IllegalArgumentException {
		PropertyConfigurator.configure(Constants.ROOT_DIR+"log4j.properties");
		SDPKBUtil.getInstance().buildKB();
		
		// get Locator instance
		Locator locator = ServiceLocationManager.getLocator(new Locale("en"));
		
		// find all services of type "test" that have attribute "cool=yes"
		ArrayList<String> scopes = new ArrayList<String>();
		ArrayList<String> attrs = new ArrayList<String>();
		scopes.add("default");
		attrs.add("max-connections");

		// find service
		long before = System.currentTimeMillis();
		System.out.println("Start finding services..");
		ServiceLocationEnumeration sle = locator.findServices(new ServiceType("service:test"), scopes, "(cool=yes)");
		long after = System.currentTimeMillis();
		System.out.println("[DONE] Elapsed Time = " + (after - before) + " (ms)");
	}
}
