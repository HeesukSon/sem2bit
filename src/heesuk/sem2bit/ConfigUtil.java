package heesuk.sem2bit;

import heesuk.sem2bit.exception.ConfigNotDefinedException;
import heesuk.sem2bit.exception.DomainNotDefinedException;
import heesuk.sem2bit.exception.LocalProtocolNotSpecifiedException;
import heesuk.sem2bit.kb.protocol.enums.Domain;
import heesuk.sem2bit.kb.protocol.enums.ProtocolName;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class ConfigUtil {
	private static ConfigUtil _instance;

	public String local_address = "";
	public String broker_address = "";
	public int tcp_timeout = 100; // default
	public int iteration_bound = 10000; // default
	public String exp_mode = "mockup"; // default
	public String log_mode = "probe"; // default
	public String role = "user_agent"; //default
	public int req_interval = 2; // default
	public Domain domain = Domain.IoT_Protocol; //default
	public ProtocolName localP = ProtocolName.MQTTv3; //default
	public boolean seqBoundRandom = true; // default
	public int seqBound = 7; // default
	public String broker_version = "v3.1.1"; // default

	private ConfigUtil(){
		try {
			FileReader reader = new FileReader(Constants.ROOT_DIR+"config");
			BufferedReader bf = new BufferedReader(reader);

			String line;
			while((line = bf.readLine()) != null){
				if(!line.startsWith("//")) {
					String[] keyValue = line.split("=");
					if (keyValue[0].trim().equals("local_address")) {
						local_address = keyValue[1].trim();
					} else if (keyValue[0].trim().equals("broker_address")){
						broker_address = keyValue[1].trim();
					} else if (keyValue[0].trim().equals("tcp_timeout")) {
						tcp_timeout = Integer.parseInt(keyValue[1].trim());
					} else if (keyValue[0].trim().equals("req_interval")) {
						req_interval = Integer.parseInt(keyValue[1].trim());
					} else if (keyValue[0].trim().equals("iteration_bound")) {
						iteration_bound = Integer.parseInt(keyValue[1].trim());
					} else if (keyValue[0].trim().equals("exp_mode")) {
						exp_mode = keyValue[1].trim();
					} else if (keyValue[0].trim().equals("log_mode")) {
						log_mode = keyValue[1].trim();
					} else if (keyValue[0].trim().equals("role")) {
						role = keyValue[1].trim();
					} else if (keyValue[0].trim().equals("domain")) {
						String domainStr = keyValue[1].trim();
						if(domainStr.equals(Domain.SDP.toString())){
							domain = Domain.SDP;
						}else if(domainStr.equals(Domain.IoT_Protocol.toString())){
							domain = Domain.IoT_Protocol;
						}else{
							try {
								throw new DomainNotDefinedException();
							} catch (DomainNotDefinedException e) {
								e.printStackTrace();
							}
						}
					} else if (keyValue[0].trim().equals("local_protocol")) {
						String pStr = keyValue[1].trim();
						if(pStr.equals(ProtocolName.SLPv1.toString())){
							localP = ProtocolName.SLPv1;
						}else if(pStr.equals(ProtocolName.SLPv2.toString())){
							localP = ProtocolName.SLPv2;
						}else if(pStr.equals(ProtocolName.UPnPv1.toString())){
							localP = ProtocolName.UPnPv1;
						}else if(pStr.equals(ProtocolName.UPnPv2.toString())){
							localP = ProtocolName.UPnPv2;
						}else if(pStr.equals(ProtocolName.DNS_SD.toString())){
							localP = ProtocolName.DNS_SD;
						}else if(pStr.equals(ProtocolName.COAP.toString())){
							localP = ProtocolName.COAP;
						}else if(pStr.equals(ProtocolName.MQTTv3.toString())){
							localP = ProtocolName.MQTTv3;
						}else if(pStr.equals(ProtocolName.MQTTv5.toString())){
							localP = ProtocolName.MQTTv5;
						}else if(pStr.equals(ProtocolName.AMQP.toString())){
							localP = ProtocolName.AMQP;
						}else if(pStr.equals(ProtocolName.SIPv1.toString())){
							localP = ProtocolName.SIPv1;
						}else if(pStr.equals(ProtocolName.SIPv2.toString())){
							localP = ProtocolName.SIPv2;
						}else{
							try {
								throw new LocalProtocolNotSpecifiedException();
							} catch (LocalProtocolNotSpecifiedException e) {
								e.printStackTrace();
							}
						}
					} else if(keyValue[0].trim().equals("seq_bound_random")){
						seqBoundRandom = new Boolean(keyValue[1].trim());
					} else if(keyValue[0].trim().equals("seq_bound")){
						seqBound = new Integer(keyValue[1].trim());
					} else if(keyValue[0].trim().equals("broker_version")){
						broker_version = keyValue[1].trim();
					} else {
						throw new ConfigNotDefinedException();
					}
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ConfigNotDefinedException e) {
			e.printStackTrace();
		}
	}

	public static ConfigUtil getInstance(){
		if(_instance == null){
			_instance = new ConfigUtil();
		}

		return _instance;
	}
}
