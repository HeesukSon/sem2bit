package kr.ac.kaist.hson.slp;

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
		//
		// find all services of type "test" that have attribute "cool=yes"
		ArrayList<String> scopes = new ArrayList<String>();
		ArrayList<String> attrs = new ArrayList<String>();
		scopes.add("default");
		attrs.add("max-connections");
		long before = System.currentTimeMillis();
		ServiceLocationEnumeration sle = locator.findServices(new ServiceType(
				"service:test"), scopes, "(cool=yes)");
		long after = System.currentTimeMillis();
		System.out.println("[Elapsed Time]: "+(after-before));

		// iterate over the results
		System.out.println("Looking up ...");
		while (sle.hasMoreElements()) {
			ServiceURL foundService = (ServiceURL) sle.nextElement();
			System.out.println(foundService);
			/*
			ServiceLocationEnumeration attributes = locator.findAttributes(foundService, scopes, attrs);
			while(attributes.hasMoreElements()){
				System.out.println(attributes.nextElement());
			}
			*/
		}
		System.out.println("Finished.");
	}
}
