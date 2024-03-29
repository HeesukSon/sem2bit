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
package org.eclipse.paho.client.mqttv3.internal;

import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttToken;
import org.eclipse.paho.client.mqttv3.internal.wire.*;
import org.eclipse.paho.client.mqttv3.logging.Logger;
import org.eclipse.paho.client.mqttv3.logging.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.Semaphore;

/**
 * Receives MQTT packets from the server.
 */
public class CommsReceiver implements Runnable {
	private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(CommsReceiver.class);
	private static final String CLASS_NAME = CommsReceiver.class.getName();

	private boolean running = false;
	private Object lifecycle = new Object();
	private ClientState clientState = null;
	private ClientComms clientComms = null;
	private MqttInputStream in;
	private CommsTokenStore tokenStore = null;
	private Thread recThread = null;
	private volatile boolean receiving;
	private final Semaphore runningSemaphore = new Semaphore(1);
	private String threadName;
	private Future receiverFuture;


	public CommsReceiver(ClientComms clientComms, ClientState clientState,CommsTokenStore tokenStore, InputStream in) {
		this.in = new MqttInputStream(clientState, in);
		this.clientComms = clientComms;
		this.clientState = clientState;
		this.tokenStore = tokenStore;
	}

	/**
	 * Starts up the Receiver's thread.
	 * @param threadName the thread name.
	 * @param executorService used to execute the thread
	 */
	public void start(String threadName, ExecutorService executorService) {
		this.threadName = threadName;
		final String methodName = "start";
		LOG.debug("methodName = {}",methodName);
		//@TRACE 855=starting
		synchronized (lifecycle) {
			if (!running) {
				running = true;
				receiverFuture = executorService.submit(this);
			}
		}
	}

	/**
	 * Stops the Receiver's thread.  This call will block.
	 */
	public void stop() {
		final String methodName = "stop";
		LOG.debug("methodName = {}",methodName);
		synchronized (lifecycle) {
			if (receiverFuture != null) {
				receiverFuture.cancel(true);
			}
			//@TRACE 850=stopping
			if (running) {
				running = false;
				receiving = false;
				if (!Thread.currentThread().equals(recThread)) {
					try {
						// Wait for the thread to finish.
						runningSemaphore.acquire();
					}
					catch (InterruptedException ex) {
					} finally {
						LOG.debug("CommsReceiver releases runningSemaphore.");
						runningSemaphore.release();
					}
				}
			}
		}
		recThread = null;
		//@TRACE 851=stopped
	}

	/**
	 * Run loop to receive messages from the server.
	 */
	public void run() {
		recThread = Thread.currentThread();
		recThread.setName(threadName);
		final String methodName = "run";
		LOG.debug("methodName : {}",methodName);

		MqttToken token = null;

		try {
			runningSemaphore.acquire();
			LOG.debug("runningSemaphore is acquired.");
		} catch (InterruptedException e) {
			running = false;
			return;
		}

		while (running && (in != null)) {
			try {
				//@TRACE 852=network read message
				receiving = in.available() > 0;
				MqttWireMessage message = in.readMqttWireMessage();
				LOG.debug("message.getClass().toString() = {}",message.getClass().toString());
				LOG.debug("message.getHeader() in bytes = {}",message.getHeader());
				LOG.debug("message.getPayload() in bytes = {}", message.getPayload());
				receiving = false;

				// instanceof checks if message is null
				if (message instanceof MqttAck) {
					token = tokenStore.getToken(message);
					if (token!=null) {
						synchronized (token) {
							// Ensure the notify processing is done under a lock on the token
							// This ensures that the send processing can complete  before the
							// receive processing starts! ( request and ack and ack processing
							// can occur before request processing is complete if not!
							clientState.notifyReceivedAck((MqttAck)message);
						}
					} else if(message instanceof MqttPubRec || message instanceof MqttPubComp || message instanceof MqttPubAck) {
						//This is an ack for a message we no longer have a ticket for.
						//This probably means we already received this message and it's being send again
						//because of timeouts, crashes, disconnects, restarts etc.
						//It should be safe to ignore these unexpected messages.
					} else {
						// It its an ack and there is no token then something is not right.
						// An ack should always have a token assoicated with it.
						throw new MqttException(MqttException.REASON_CODE_UNEXPECTED_ERROR);
					}
				} else {
					if (message != null) {
						// A new message has arrived
						clientState.notifyReceivedMsg(message);
					}
				}
			}
			catch (MqttException ex) {
				//@TRACE 856=Stopping, MQttException
				running = false;
				// Token maybe null but that is handled in shutdown
				clientComms.shutdownConnection(token, ex);
			}
			catch (IOException ioe) {
				//@TRACE 853=Stopping due to IOException

				running = false;
				// An EOFException could be raised if the broker processes the
				// DISCONNECT and ends the socket before we complete. As such,
				// only shutdown the connection if we're not already shutting down.
				if (!clientComms.isDisconnecting()) {
					clientComms.shutdownConnection(token, new MqttException(MqttException.REASON_CODE_CONNECTION_LOST, ioe));
				}else{
					LOG.debug("clientComms.isDisconnecting() == {}",clientComms.isDisconnecting());
				}
			}
			finally {
				receiving = false;
				LOG.debug("CommsReceiver releases runningSemaphore.");
				runningSemaphore.release();
			}
		}

		//@TRACE 854=<
	}

	public boolean isRunning() {
		return running;
	}

	/**
	 * Returns the receiving state.
	 *
	 * @return true if the receiver is receiving data, false otherwise.
	 */
	public boolean isReceiving() {
		return receiving;
	}
}
