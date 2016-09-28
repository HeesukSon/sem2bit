package ch.ethz.iks.slp.test;

import ch.ethz.iks.slp.Locator;
import ch.ethz.iks.slp.ServiceLocationManager;
import ch.ethz.iks.slp.ServiceLocationException;
import ch.ethz.iks.slp.ServiceLocationEnumeration;
import ch.ethz.iks.slp.ServiceType;
import ch.ethz.iks.slp.ServiceURL;

import java.util.ArrayList;
import java.util.Locale;

public class Lookup {

	public static void main(String[] args) throws ServiceLocationException {
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
