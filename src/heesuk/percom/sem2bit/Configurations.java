package heesuk.percom.sem2bit;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class Configurations {
	private static Configurations _instance;

	public String local_address = "";
	public int tcp_timeout = 100; // default
	public int iteration_bound = 10000; // default
	public String exp_mode = "mockup"; // default
	public String log_mode = "probe"; // default
	public String role = "user_agent"; //default
	public int req_interval = 2; // default

	private Configurations(){
		try {
			FileReader reader = new FileReader("config");
			BufferedReader bf = new BufferedReader(reader);

			String line;
			while((line = bf.readLine()) != null){
				if(!line.startsWith("//")) {
					String[] keyValue = line.split("=");
					if (keyValue[0].trim().equals("local_address")) {
						local_address = keyValue[1].trim();
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

	public static Configurations getInstance(){
		if(_instance == null){
			_instance = new Configurations();
		}

		return _instance;
	}
}
