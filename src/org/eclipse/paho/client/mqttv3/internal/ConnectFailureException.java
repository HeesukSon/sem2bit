package org.eclipse.paho.client.mqttv3.internal;

public class ConnectFailureException extends Exception{
	public int cnt;
	ConnectFailureException(){
		super();
	}

	public ConnectFailureException(int cnt){
		this.cnt = cnt;
	}
}
