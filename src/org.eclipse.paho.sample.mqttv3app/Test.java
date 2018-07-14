package org.eclipse.paho.sample.mqttv3app;

import heesuk.sem2bit.Constants;
import org.apache.log4j.PropertyConfigurator;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.internal.ConnectFailureException;

public class Test {
	public static void main(String[] args){
		PropertyConfigurator.configure(Constants.ROOT_DIR+"log4j.properties");
		
		// Default settings:
		String action 		= "publish";
		String topic 		= "Sample/Java/v3";
		String message 		= "Message from blocking Paho MQTTv3 Java client sample";
		int qos 			= 2;
		//String broker 		= "m2m.eclipse.org";
		String broker = "127.0.0.1";
		int port 			= 1883;
		String clientId 	= "SampleJavaV3_"+action;
		boolean cleanSession = true;			// Non durable subscriptions
		String password = null;
		String userName = null;
		String protocol = "tcp://";

		String url = protocol + broker + ":" + port;

		// With a valid set of arguments, the real work of
		// driving the client API can begin
		try {
			// Create an instance of this class
			MQTTConnector sampleClient = new MQTTConnector(1,url, clientId, cleanSession,userName,password);
			sampleClient.publish(topic,qos,message.getBytes());
		} catch(MqttException me) {
			me.printStackTrace();
		} catch (ConnectFailureException e) {
			e.printStackTrace();
		}
	}
}
