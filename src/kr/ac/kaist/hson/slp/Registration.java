package kr.ac.kaist.hson.slp;

import ch.ethz.iks.slp.Advertiser;
import ch.ethz.iks.slp.ServiceLocationManager;
import ch.ethz.iks.slp.ServiceLocationException;
import ch.ethz.iks.slp.ServiceURL;
import java.util.Locale;
import java.util.Hashtable;

public class Registration {

public static void main(String[] args) throws ServiceLocationException {
	// get Advertiser instance
	Advertiser advertiser = ServiceLocationManager.getAdvertiser(new Locale("en"));

	// the service has lifetime 60, that means it will only persist for one minute
	ServiceURL myService = new ServiceURL("service:test:myService://my.host.ch", ServiceURL.LIFETIME_PERMANENT);

	// some attributes for the service
	Hashtable attributes = new Hashtable();
	attributes.put("persistent", Boolean.TRUE);
	attributes.put("cool", "yes");
	attributes.put("max-connections", new Integer(5));

	advertiser.register(myService, attributes);

	System.out.println("Registered service:test:myService");
}

}
