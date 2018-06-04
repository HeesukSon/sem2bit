/*******************************************************************************
 * Copyright (c) 2009, 2014 IBM Corp.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v1.0 which accompany this distribution. 
 *
 * The Eclipse Public License is available at 
 *    http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 *   http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *    Dave Locke - initial API and implementation and/or initial documentation
 */
package org.eclipse.paho.client.mqttv3.internal.wire;

import heesuk.sem2bit.ConfigUtil;
import heesuk.sem2bit.kb.TreeFactory;
import heesuk.sem2bit.kb.protocol.enums.MessageFieldType;
import heesuk.sem2bit.kb.protocol.enums.UpdatePattern;
import heesuk.sem2bit.kb.protocol.iot.IoTProtocolKBUtil;
import heesuk.sem2bit.msg.ModificationCandidate;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.internal.ClientState;
import org.eclipse.paho.client.mqttv3.logging.Logger;
import org.eclipse.paho.client.mqttv3.logging.LoggerFactory;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;


/**
 * An <code>MqttOutputStream</code> lets applications write instances of
 * <code>MqttWireMessage</code>. 
 */
public class MqttOutputStream extends OutputStream {
	private static final String CLASS_NAME = MqttOutputStream.class.getName();
	private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(MqttOutputStream.class);

	private ClientState clientState = null;
	private BufferedOutputStream out;
	
	public MqttOutputStream(ClientState clientState, OutputStream out) {
		this.clientState = clientState;
		this.out = new BufferedOutputStream(out);
	}
	
	public void close() throws IOException {
		out.close();
	}
	
	public void flush() throws IOException {
		out.flush();
	}
	
	public void write(byte[] b) throws IOException {
		out.write(b);
		clientState.notifySentBytes(b.length);
	}
	
	public void write(byte[] b, int off, int len) throws IOException {
		out.write(b, off, len);
		clientState.notifySentBytes(len);
	}
	
	public void write(int b) throws IOException {
		out.write(b);
	}

	/**
	 * Writes an <code>MqttWireMessage</code> to the stream.
	 * @param message The {@link MqttWireMessage} to send
	 * @throws IOException if an exception is thrown when writing to the output stream.
	 * @throws MqttException if an exception is thrown when getting the header or payload
	 */
	public void write(MqttWireMessage message) throws IOException, MqttException {
		final String methodName = "write";
		LOG.debug("methodName = {}",methodName);

		ModificationCandidate[] seq;
		String adaptSeq = "";

		if(ConfigUtil.getInstance().exp_mode.equals("mockup")){
			seq = new ModificationCandidate[3];
			seq[0] = new ModificationCandidate("DEFAULT", "[DEFAULT]");
			seq[1] = new ModificationCandidate("Protocol Level", "[C]");
			seq[2] = new ModificationCandidate("Property Length", "[A]");
		}else{
			seq = TreeFactory.getInstance().getNextSequence();
		}

		for(ModificationCandidate candidate : seq){
			adaptSeq += candidate.toStringWithoutWeight();
			adaptSeq += "\n";
		}
		byte[] originHeader = message.getHeader();
		String messageType = message.getClass().toString();
		MqttWireMessage adaptedMessage = composeAdaptedMessage(message, seq);
		LOG.info("\n[{}]\nBEFORE: {}\nAdapt Sequence: \n{}AFTER: {}\n",messageType, originHeader, adaptSeq, adaptedMessage.getHeader());


		/* BEFORE update for SeM2Bit experiment */
		//byte[] bytes = message.getHeader();
		//byte[] pl = message.getPayload();

		/* AFTER update for SeM2Bit experiment */
		byte[] bytes = adaptedMessage.getHeader();
		byte[] pl = adaptedMessage.getPayload();
		//byte[] pl = new byte[0];

//		out.write(message.getHeader());
//		out.write(message.getPayload());
		out.write(bytes,0,bytes.length);
		clientState.notifySentBytes(bytes.length);

        int offset = 0;
        int chunckSize = 1024;
        while (offset < pl.length) {
        	int length = Math.min(chunckSize, pl.length - offset);
        	out.write(pl, offset, length);
        	offset += chunckSize;
        	clientState.notifySentBytes(length);
        }

		// @TRACE 529= sent {0}
	}

	/**
	 *
	 * added to adapt the message output according to the sequence planning tree output.
	 * @author Heesuk Son (heesuk.chad.son@gmail.com)
	 * @param message
	 * @param seq
	 * @return Adapted message to be transmitted to the broker
	 */
	public MqttWireMessage composeAdaptedMessage(MqttWireMessage message, ModificationCandidate[] seq){
		for(ModificationCandidate candidate : seq){
			String update = candidate.getUpdate();

			if(update.equals(UpdatePattern.ADD_NEW_FIELD.toString())){
				if(message instanceof MqttConnect){
					((MqttConnect) message).increaseAddFieldCnt();
				}
			}else if(update.equals(UpdatePattern.CHANGE_VOCA.toString())){

			}else if(update.equals(UpdatePattern.VALUE_CHANGE.toString())){
				if(message instanceof MqttConnect){
					((MqttConnect) message).increaseMqttVersion();
				}
			}else if(update.equals(UpdatePattern.DELETE_FIELD.toString())){

			}else{
				// Length
			}
		}

		return message;
	}
}

