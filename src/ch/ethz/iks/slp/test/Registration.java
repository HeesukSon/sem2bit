package ch.ethz.iks.slp.test;

import ch.ethz.iks.slp.Advertiser;
import ch.ethz.iks.slp.ServiceLocationManager;
import ch.ethz.iks.slp.ServiceLocationException;
import ch.ethz.iks.slp.ServiceURL;
import heesuk.sem2bit.Constants;

import java.util.Locale;

import org.apache.log4j.PropertyConfigurator;

import java.util.Hashtable;

public class Registration {
	public static void main(String[] args) throws ServiceLocationException{
		PropertyConfigurator.configure(Constants.ROOT_DIR+"log4j.properties");
		//SDPKBUtil.getInstance().buildKB();
		
		// get Advertiser instance
		Advertiser advertiser = ServiceLocationManager.getAdvertiser(new Locale("en"));

		// the service has lifetime 60, that means it will only persist for one
		// minute
		//ServiceURL myService = new ServiceURL("service:test:myService://my.host.ch", 60);
		ServiceURL myService = new ServiceURL("service:test:myService://192.168.1.102", 1200);

		// some attributes for the service
		Hashtable attributes = new Hashtable();
		attributes.put("persistent", Boolean.TRUE);
		attributes.put("cool", "yes");
		attributes.put("max-connections", new Integer(5));

		advertiser.register(myService, attributes);
	}

}
