/* Copyright (c) 2005-2007 Jan S. Rellermeyer
 * Information and Communication Systems Research Group (IKS),
 * Institute for Pervasive Computing, ETH Zurich.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *    - Redistributions of source code must retain the above copyright notice,
 *      this list of conditions and the following disclaimer.
 *    - Redistributions in binary form must reproduce the above copyright
 *      notice, this list of conditions and the following disclaimer in the
 *      documentation and/or other materials provided with the distribution.
 *    - Neither the name of ETH Zurich nor the names of its contributors may be
 *      used to endorse or promote products derived from this software without
 *      specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */

package ch.ethz.iks.slp.impl;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.lang.reflect.Constructor;
import java.net.BindException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.ProtocolException;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import ch.ethz.iks.slp.ServiceLocationException;
import ch.ethz.iks.slp.ServiceType;
import heesuk.sem2bit.ConfigUtil;
import heesuk.sem2bit.ExperimentStat;
import heesuk.sem2bit.ProbingStatus;

/**
 * the core class of the jSLP implementation.
 * <code>ch.ethz.iks.slp.ServiceLocationManager</code> inherits from this class.
 * 
 * @see ch.ethz.iks.slp.ServiceLocationManager
 * @author Jan S. Rellermeyer, IKS, ETH Zurich
 * @since 0.6
 */
public abstract class SLPCore {
	private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(SLPCore.class);
	private static volatile boolean isMulticastSocketInitialized = false;
	private static volatile boolean isInitialized = false;

	protected static PlatformAbstraction platform;

	/**
	 * the default empty locale. Used for messages that don't specify a locale.
	 */
	static final Locale DEFAULT_LOCALE = Locale.getDefault();

	/**
	 * the port for SLP communication.
	 */
	static final int SLP_PORT;

	/**
	 * the reserved (standard) port.
	 */
	static final int SLP_RESERVED_PORT = 427;

	/**
	 * the standard SLP multicast address.
	 */
	static final String SLP_MCAST_ADDRESS = "239.255.255.253";

	/**
	 * 
	 */
	static final InetAddress MCAST_ADDRESS;

	/**
	 * the SLP configuration.
	 */
	static final SLPConfiguration CONFIG;

	/**
	 * currently only for debugging.
	 */
	static final boolean TCP_ONLY = false;

	/**
	 * the standard service type for DAs.
	 */
	static final String SLP_DA_TYPE = "service:directory-agent";

	/**
	 * my own ip. Used to check if this peer is already in the previous
	 * responder list.
	 */
	static String[] myIPs;

	/**
	 * configured to perform no DA discovery ?
	 */
	static final boolean noDiscovery;

	/**
	 * the constructor for <code>Advertiser</code> instances, if an
	 * implementation exists.
	 */
	protected static final Constructor advertiser;

	/**
	 * the constructor for <code>Locator</code> instances, if an implementation
	 * exists.
	 */
	protected static final Constructor locator;

	/**
	 * the constructor for <code>SLPDaemon</code> instances, if an
	 * implementation exists.
	 */
	private static final Constructor daemonConstr;

	/**
	 * the daemon instance, if the implementation exists and no other daemon is
	 * already running on the machine.
	 */
	private static SLPDaemon daemon;

	/**
	 * the multicast server thread.
	 */
	private static Thread multicastThread;

	/**
	 * the multicast socket.
	 */
	private static MulticastSocket mtcSocket;

	/**
	 * the next free XID.
	 */
	private static short nextXid;

	/**
	 * used to asynchronously receive replies. query XID -> reply queue (List)
	 */
	private static Map replyListeners = new HashMap();

	/**
	 * Map of DAs:
	 * 
	 * String scope -> list of Strings of DA URLs.
	 */
	static Map dAs = new HashMap();

	/**
	 * Map of DA SPIs:
	 * 
	 * String DA URL -> String String.
	 */
	static Map dASPIs = new HashMap(); // DA URL -> List of SPIs

	static InetAddress LOCALHOST;

	/**
	 * initialize the core class.
	 */
	static {
		try {
			LOCALHOST = InetAddress.getLocalHost();
		} catch (Throwable t) {
			t.printStackTrace();
		}

		final Class[] locale = new Class[] { Locale.class };

		// check, if an Advertiser implementation is available
		Constructor constr = null;
		try {
			constr = Class.forName("ch.ethz.iks.slp.impl.AdvertiserImpl").getConstructor(locale);
		} catch (Exception e) {
		}
		advertiser = constr;

		// check, if a Locator implementation is available
		constr = null;
		try {
			constr = Class.forName("ch.ethz.iks.slp.impl.LocatorImpl").getConstructor(locale);
		} catch (Exception e) {
		}
		locator = constr;

		// check, if a Daemon is available
		constr = null;
		try {
			constr = Class.forName("ch.ethz.iks.slp.impl.SLPDaemonImpl").getConstructor(null);
		} catch (Exception e) {
		}
		daemonConstr = constr;

		// read in the property file, if it exists
		File propFile = new File("jslp.properties");
		SLPConfiguration config;
		try {
			config = propFile.exists() ? new SLPConfiguration(propFile) : new SLPConfiguration();
		} catch (IOException e1) {
			LOG.error("Could not parse the property file {}",propFile.toString());
			e1.printStackTrace();
			config = new SLPConfiguration();
		}
		CONFIG = config;

		noDiscovery = CONFIG.getNoDaDiscovery();

		// determine the interfaces on which jSLP runs on
		String[] IPs = CONFIG.getInterfaces();
		if (IPs == null) {
			InetAddress[] addresses = null;
			try {
				addresses = InetAddress.getAllByName(InetAddress.getLocalHost().getHostName());
				IPs = new String[addresses.length];
				for (int i = 0; i < addresses.length; i++) {
					IPs[i] = addresses[i].getHostAddress();
				}
			} catch (UnknownHostException e) {
				LOG.error("Reverse lookup of host name failed. Running service discovery on localloop.");
				try {
					addresses = new InetAddress[] { InetAddress.getLocalHost() };
				} catch (UnknownHostException e1) {
					e1.printStackTrace();
				}
			}
		}
		myIPs = IPs;
		SLP_PORT = CONFIG.getPort();

		// initialize the XID with a random number
		nextXid = (short) Math.round(Math.random() * Short.MAX_VALUE);

		InetAddress mcast = null;
		try {
			mcast = InetAddress.getByName(SLPCore.SLP_MCAST_ADDRESS);
		} catch (UnknownHostException e1) {
			e1.printStackTrace();
		}

		MCAST_ADDRESS = mcast;
	}

	protected static void init() {
		if (isInitialized) {
			return;
		}
		isInitialized = true;
		platform = new StandalonePlatformAbstraction();

		LOG.info("jSLP is running on the following interface: {}",java.util.Arrays.asList(myIPs));
		LOG.info("jSLP is using port: {}",SLP_PORT);

		String[] daAddresses = CONFIG.getDaAddresses();
		if (daAddresses == null) {
			if (noDiscovery) {
				throw new IllegalArgumentException(
						"Configuration 'net.slp.noDaDiscovery=true' requires a non-empty list of preconfigured DAs");
			}
		} else {
			try {
				// process the preconfigured DAs
				final ServiceRequest req = new ServiceRequest(new ServiceType(SLP_DA_TYPE), null, null, null);
				req.port = SLP_PORT;
				for (int i = 0; i < daAddresses.length; i++) {
					try {
						req.address = InetAddress.getByName(daAddresses[i]);
						DAAdvertisement daa = (DAAdvertisement) sendMessage(req, true);
						String[] scopes = (String[]) daa.scopeList.toArray(new String[daa.scopeList.size()]);
						LOG.debug("scopes.length = {}",scopes.length);
						for (int j = 0; j < scopes.length; j++) {
							LOG.info("jSLP is adding DA, {} for the Scope, {}",daAddresses[i], scopes[j]);
							SLPUtils.addValue(dAs, scopes[i].toLowerCase(), daAddresses[i]);
						}
					} catch (ServiceLocationException e) {
						LOG.error("Error communicating with {}",daAddresses[i]);
						e.printStackTrace();
					} catch (UnknownHostException e) {
						LOG.error("Unknown net.slp.DAAddresses address: {}",daAddresses[i]);
						e.printStackTrace();
					}
				}
			} catch (IllegalArgumentException ise) {
				ise.printStackTrace();
			}
		}

		//TODO: Not sure if this action is right (by heesuk)
		LOG.debug("noDiscovery = {}",noDiscovery);
		//if (!noDiscovery) {
		if(false){
			// perform an initial lookup
			try {
				List scopes = new ArrayList();
				scopes.add("default");
				daLookup(scopes);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	// a pure UA doesn't need a multicast listener which is only required by a
	// SA or DA
	protected static void initMulticastSocket() {
		if (isMulticastSocketInitialized) {
			return;
		}
		isMulticastSocketInitialized = true;

		try {
			mtcSocket = new MulticastSocket(SLP_PORT);
			mtcSocket.setTimeToLive(CONFIG.getMcastTTL());
			if (CONFIG.getInterfaces() != null) {
				try {
					mtcSocket.setInterface(InetAddress.getByName(myIPs[0]));
				} catch (Throwable t) {
					t.printStackTrace();
				}
			}
			mtcSocket.joinGroup(MCAST_ADDRESS);
		} catch (BindException be) {
			LOG.error(be.getMessage());
			throw new RuntimeException("You have to be root to open port " + SLP_PORT);
		} catch (Exception e) {
			LOG.error(e.getMessage());
		}

		// setup and start the multicast thread
		multicastThread = new Thread() {
			public void run() {
				DatagramPacket packet;
				byte[] bytes = new byte[SLPCore.CONFIG.getMTU()];
				while (true) {
					try {
						packet = new DatagramPacket(bytes, bytes.length);
						mtcSocket.receive(packet);
						
						if (!packet.getAddress().toString().equals(ConfigUtil.getInstance().local_address)) {
							LOG.error("A Non-target host, {}, sent a request message !!",packet.getAddress().toString());
						} else {
							LOG.info("A request message arrived from host: {}",packet.getAddress().toString());
							final SLPMessage reply = handleMessage(
									SLPMessage.parse(packet.getAddress(), packet.getPort(),
											new DataInputStream(new ByteArrayInputStream(packet.getData())), false));
							if (reply != null) {
								final byte[] repbytes = reply.getBytes();
								DatagramPacket datagramPacket = new DatagramPacket(repbytes, repbytes.length,
										reply.address, reply.port);
								mtcSocket.send(datagramPacket);
								LOG.info("SEND ({}:{}) {}",reply.address,reply.port,reply.toString());
							}
						}
					} catch (Exception e) {
						LOG.error("Exception in Multicast Receiver Thread");
					}
				}
			}
		};
		multicastThread.start();

		// check, if there is already a SLP daemon runnung on port 427
		// that can be either a jSLP daemon, or an OpenSLP daemon or something
		// else. If not, try to start a new daemon instance.
		if (daemonConstr != null) {
			try {
				daemon = (SLPDaemon) daemonConstr.newInstance(null);
			} catch (Exception e) {
				daemon = null;
			}
		}
	}

	/**
	 * get my own IP.
	 * 
	 * @return the own IP.
	 */
	static InetAddress getMyIP() {
		try {
			return InetAddress.getByName(myIPs[0]);
		} catch (UnknownHostException e) {
			LOG.error("Unknown net.slp.interfaces address: {}",myIPs[0]);
			return null;
		}
	}

	/**
	 * get the list of all available scopes.
	 * 
	 * @return a List of all available scopes. RFC 2614 proposes
	 *         <code>Vector</code> but jSLP returns <code>List</code>.
	 * @throws ServiceLocationException
	 *             in case of an exception in the underlying framework.
	 */
	public static List findScopes() throws ServiceLocationException {
		return new ArrayList(dAs.keySet());
	}

	/**
	 * handle incoming UDP messages.
	 * 
	 * @param message
	 *            the incoming message.
	 * @throws ServiceLocationException
	 *             if something goes wrong.
	 */
	private static SLPMessage handleMessage(final SLPMessage message) throws ServiceLocationException {
		if (message == null) {
			return null;
		}

		LOG.info("handleMessage():: RECEIVED ({}:{}) {}",message.address, message.port,message);

		switch (message.funcID) {
		case SLPMessage.DAADVERT:
			// drop message, if noDADiscovery is set
			if (noDiscovery) {
				LOG.error("DROPPED ({}:{}) {} (reason: noDADiscovery is set.)",message.address,message.port,message.toString());
				return null;
			}

			DAAdvertisement advert = (DAAdvertisement) message;

			if (advert.errorCode != 0) {
				LOG.error("DROPPED DAAdvertisement ({}:{}) {} (reason: {} !=0)",advert.address,advert.port,advert.toString(),advert.errorCode);

				return null;
			}

			if (advert.url != advert.address.getHostAddress()) {
				advert.url = advert.address.getHostAddress();
			}

			// statelessBootTimestamp = 0 means DA is going down
			if (advert.statelessBootTimestamp == 0) {
				for (Iterator iter = advert.scopeList.iterator(); iter.hasNext();) {
					String scope = ((String) iter.next()).toLowerCase();
					SLPUtils.removeValue(SLPCore.dAs, scope.toLowerCase(), advert.url);
					dASPIs.remove(advert.url);
				}
			} else {
				for (Iterator iter = advert.scopeList.iterator(); iter.hasNext();) {
					String scope = ((String) iter.next()).toLowerCase();

					// If OpenSLP would strictly follow RFC 2608,
					// it should only send a new statelessBootTimestamp
					// if it was really rebooted or has lost
					// registrations for other reasons.
					// But it looks like OpenSLP sends a new sBT whenever
					// it sends a DAADVERT so we will just reregister
					// all of our services if we receive a new DAADVERT
					// not caring for the sBT at all.
					SLPUtils.addValue(SLPCore.dAs, scope, advert.url);

					if (CONFIG.getSecurityEnabled()) {
						dASPIs.put(advert.url, SLPMessage.stringToList(advert.spi, ","));
					}

				}

				synchronized (dAs) {
					dAs.notifyAll();
				}

				// if there is a daemon instance, inform it about the discovered
				// DA
				if (daemon != null) {
					daemon.newDaDiscovered(advert);
				}

			}
			LOG.info("NEW DA LIST: {}",dAs);

			return null;

		// reply messages
		case SLPMessage.ATTRRPLY:
		case SLPMessage.SRVRPLY:
		case SLPMessage.SRVTYPERPLY:
			synchronized (replyListeners) {
				List queue = (List) replyListeners.get(new Integer(message.xid));

				if (queue != null) {
					synchronized (queue) {
						queue.add(message);
						queue.notifyAll();
					}
					return null;
				} else {
					LOG.info("SRVTYPEREPLY recieved ({}:{}) {} but not replyListeners present anymore",message.address,message.port,message.toString());
				}
			}
			return null;

		// request messages
		case SLPMessage.SRVRQST:
		case SLPMessage.ATTRRQST:
		case SLPMessage.SRVTYPERQST:
			// silently drop messages where this peer is in the previous
			// responder list
			LOG.info("[SLPMessage.SRVTYPERQST] handleMessage() - RECEIVED (" + message.address + ":" + message.port + ") " + message);
			
			for (int i = 0; i < SLPCore.myIPs.length; i++) {
				if (((RequestMessage) message).prevRespList.contains(SLPCore.myIPs[i])) {
					LOG.error("DROPPED (" + message.address + ":" + message.port + ") " + message.toString()
							+ "(udp multicast)");
					return null;
				}
			}
			

			// if we have a daemon instance, delegate the
			// message to the daemon.
			if (daemon != null) {
				LOG.info("daemon != null");
				return daemon.handleMessage(message);
			} else {
				LOG.info("SRVTYPERQST recieved (" + message.address + ":" + message.port + ") "
						+ message.toString() + " but no SLPDaemon to handle the message present");
				return null;
			}
		default:
			// if we have a daemon instance, delegate all other
			// messages to the daemon.
			if (daemon != null) {
				return daemon.handleMessage(message);
			} else {
				LOG.info("A message recieved (" + message.address + ":" + message.port + ") "
						+ message.toString() + " but no SLPDaemon to handle the message present");
				return null;
			}
		}

	}

	/**
	 * get the next XID.
	 * 
	 * @return the next XID.
	 */
	static short nextXid() {
		if (nextXid == 0) {
			nextXid = 1;
		}
		return nextXid++;
	}

	/**
	 * find DAs for the scopes by sending a multicast service request for
	 * service <i>service:directory-agent</i>.
	 * 
	 * @param scopes
	 *            a <code>List</code> of scopes.
	 * @throws ServiceLocationException
	 *             in case of network errors.
	 */
	static void daLookup(final List scopes) throws ServiceLocationException {
		int i = 0;
		try {
			// change by TomoTherapy Inc
			// added loop for each IP for each interface
			// used 1.4 SocketAddress
			// altered by Jan to be backwards compatible with Java 2
			for (; i < myIPs.length; i++) {
				// create a socket bound to the next ip address
				final InetAddress addr = InetAddress.getByName(myIPs[i]);
				DatagramSocket socket = new DatagramSocket(0, addr);

				ServiceRequest sreq = new ServiceRequest(new ServiceType(SLP_DA_TYPE), scopes, null,
						SLPCore.DEFAULT_LOCALE);
				sreq.xid = SLPCore.nextXid();
				sreq.scopeList = scopes;
				sreq.address = MCAST_ADDRESS;
				sreq.multicast = true;
				byte[] bytes = sreq.getBytes();
				DatagramPacket d = new DatagramPacket(bytes, bytes.length, MCAST_ADDRESS, SLP_PORT);
				LOG.info("SENT " + sreq + "(udp multicast)");
				setupReceiverThread(socket, CONFIG.getWaitTime(), sreq);
				try {
					socket.send(d);
				} catch (SocketException se) {
					// blacklist address
					final List remaining = new ArrayList(java.util.Arrays.asList(myIPs));
					final String faulty = myIPs[i];
					remaining.remove(faulty);
					myIPs = (String[]) remaining.toArray(new String[remaining.size()]);
					LOG.info("Blacklisting IP " + faulty);
				}
			}
		} catch (IllegalArgumentException ise) {
			ise.printStackTrace();
		} catch (UnknownHostException uhe) {
			LOG.error("Unknown net.slp.interfaces address: " + myIPs[i]);
			throw new ServiceLocationException(ServiceLocationException.NETWORK_ERROR, uhe.getMessage());
		} catch (IOException e) {
			LOG.error("Error connecting to: " + myIPs[i]);
			throw new ServiceLocationException(ServiceLocationException.NETWORK_ERROR, e.getMessage());
		}
	}

	/**
	 * send a unicast message over TCP.
	 * 
	 * @param msg
	 *            the message.
	 * @return the reply.
	 * @throws ServiceLocationException
	 *             in case of network errors.
	 * @throws IOException, SocketTimeoutException 
	 */
	static ReplyMessage sendMessageTCP(final SLPMessage msg) throws ServiceLocationException, SocketTimeoutException {
		long before = System.currentTimeMillis();
		try {
			if (msg.xid == 0) {
				msg.xid = nextXid();
			}
			
			Socket socket = new Socket(msg.address, msg.port);
			socket.setSoTimeout(CONFIG.getTCPTimeout());
			DataOutputStream out = new DataOutputStream(socket.getOutputStream());
			DataInputStream in = new DataInputStream(socket.getInputStream());

			msg.writeTo(out);
			try{
				final ReplyMessage reply = (ReplyMessage) SLPMessage.parse(msg.address, msg.port, in, true);
				
				long after = System.currentTimeMillis();
				ExperimentStat.getInstance().setMsgTransTimeTotal(ExperimentStat.getInstance().getMsgTransTimeTotal()+(after-before));
				LOG.info("[SLPCore.sendMessageTCP()] reply msg = " + reply+"\n");
				ProbingStatus.success = true;

				return reply;
			}catch(SocketTimeoutException ste){
				socket.close();

				long after = System.currentTimeMillis();
				ExperimentStat.getInstance().setMsgTransTimeTotal(ExperimentStat.getInstance().getMsgTransTimeTotal()+(after-before));
				throw new SocketTimeoutException();
			}
			
			
			
		} catch (ServiceLocationException e) {
			throw new ServiceLocationException(ServiceLocationException.NETWORK_ERROR, e.getMessage());
		}catch(IOException e){
			//e.printStackTrace();
			throw new SocketTimeoutException();
		}
	}

	/**
	 * send a unicast message over TCP.
	 *
	 * @param msg
	 *            the message.
	 * @return the reply.
	 * @throws ServiceLocationException
	 *             in case of network errors.
	 * @throws IOException, SocketTimeoutException
	 */
	static ReplyMessage sendMessageTCP(int cnt, final SLPMessage msg) throws ServiceLocationException, SocketTimeoutException {
		long before = System.currentTimeMillis();
		try {
			if (msg.xid == 0) {
				msg.xid = nextXid();
			}

			Socket socket = new Socket(msg.address, msg.port);
			socket.setSoTimeout(CONFIG.getTCPTimeout());
			DataOutputStream out = new DataOutputStream(socket.getOutputStream());
			DataInputStream in = new DataInputStream(socket.getInputStream());

			msg.writeTo(cnt, out);
			try{
				final ReplyMessage reply = (ReplyMessage) SLPMessage.parse(msg.address, msg.port, in, true);

				long after = System.currentTimeMillis();
				ExperimentStat.getInstance().setMsgTransTimeTotal(ExperimentStat.getInstance().getMsgTransTimeTotal()+(after-before));
				LOG.info("[SLPCore.sendMessageTCP()] reply msg = " + reply+"\n");

				return reply;
			}catch(SocketTimeoutException ste){
				socket.close();

				long after = System.currentTimeMillis();
				ExperimentStat.getInstance().setMsgTransTimeTotal(ExperimentStat.getInstance().getMsgTransTimeTotal()+(after-before));
				throw new SocketTimeoutException();
			}
		} catch (ServiceLocationException e) {
			throw new ServiceLocationException(ServiceLocationException.NETWORK_ERROR, e.getMessage());
		}catch(IOException e){
			//e.printStackTrace();
			throw new SocketTimeoutException();
		}
	}

	/**
	 * send a unicast message over UDP.
	 * 
	 * @param msg
	 *            the message to be sent.
	 * @param expectReply
	 *            waits for a reply if set to true.
	 * @return the reply.
	 * @throws ServiceLocationException
	 *             in case of network errors etc.
	 */
	static ReplyMessage sendMessage(final SLPMessage msg, final boolean expectReply) throws ServiceLocationException {
		if (msg.xid == 0) {
			msg.xid = nextXid();
		}
		if (msg.getSize() > CONFIG.getMTU() || TCP_ONLY) {
			try {
				return sendMessageTCP(msg);
			} catch (SocketTimeoutException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		try {
			DatagramSocket dsocket = new DatagramSocket();
			dsocket.setSoTimeout(CONFIG.getDatagramMaxWait());

			byte[] bytes = msg.getBytes();

			DatagramPacket packet = new DatagramPacket(bytes, bytes.length, msg.address, msg.port);
			LOG.debug("msg.address:port = {}:{}",msg.address,msg.port);

			byte[] receivedBytes = new byte[CONFIG.getMTU()];
			DatagramPacket received = new DatagramPacket(receivedBytes, receivedBytes.length);

			dsocket.send(packet);

			LOG.info("SENT (" + msg.address + ":" + msg.port + ") " + msg + " (via udp port "
					+ dsocket.getLocalPort() + ")");

			// if no reply is expected, return
			if (!expectReply) {
				return null;
			}

			dsocket.receive(received);
			dsocket.close();
			final DataInputStream in = new DataInputStream(new ByteArrayInputStream(received.getData()));
			ReplyMessage reply = (ReplyMessage) SLPMessage.parse(received.getAddress(), received.getPort(), in, false);
			return reply;
		} catch (SocketException se) {
			throw new ServiceLocationException(ServiceLocationException.NETWORK_INIT_FAILED, se.getMessage());
		} catch (ProtocolException pe) {
			// Overflow, retry with TCP
			try {
				return sendMessageTCP(msg);
			} catch (SocketTimeoutException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (IOException ioe) {
			LOG.error("Exception during sending of " + msg+"to " + msg.address + ":" + msg.port);
			ioe.printStackTrace();
			throw new ServiceLocationException(ServiceLocationException.NETWORK_ERROR, ioe.getMessage());
		} catch (Throwable t) {
			t.printStackTrace();
			throw new ServiceLocationException((short) 1, t.getMessage());
		}
		return null;
	}

	/**
	 * send a request via multicast convergence algorithm.
	 * 
	 * @param msg
	 *            the message.
	 * @return the collected reply messages.
	 * @throws ServiceLocationException
	 *             in case of network errors.
	 * @throws SocketTimeoutException 
	 */
	static List multicastConvergence(final RequestMessage msg) throws ServiceLocationException, SocketTimeoutException {
		// try loop is commented to throw the Exceptions to the callers
	//	try {

			long start = System.currentTimeMillis();

			List replyQueue = new ArrayList();
			List responders = new ArrayList();
			List responses = new ArrayList();

			if (msg.xid == 0) {
				msg.xid = SLPCore.nextXid();
			}

			// register the reply queue as listener
			Integer queryXID = new Integer(msg.xid);
			synchronized (replyListeners) {
				replyListeners.put(queryXID, replyQueue);
			}

			msg.port = SLPCore.SLP_PORT;
			msg.prevRespList = new ArrayList();
			msg.multicast = true;

			// send to localhost, in case the OS does not support multicast over
			// loopback which can fail if no SA is running locally

			msg.address = LOCALHOST;
		try {
				replyQueue.add(sendMessageTCP(msg)); // TODO uncomment for normal experiment with TCP
				//replyQueue.add(sendMessage(msg, false));

			} catch (ServiceLocationException e) {
				if (e.getErrorCode() != ServiceLocationException.NETWORK_ERROR) {
					throw e;
				}
			}

			msg.address = MCAST_ADDRESS;
			ReplyMessage reply;

			/**
			 * @author heesuk (heesuk.chad.son@gmail.com)
			 * 	myIPs[1] has IPv6 address as the value. So It is prevented for percom 17 experiment
			 */
			/*
			for (int i = 0; i < myIPs.length; i++) {
				// create a socket bound to the next ip address
				final InetAddress addr = InetAddress.getByName(myIPs[i]);
				final MulticastSocket socket = new MulticastSocket();
				socket.setInterface(addr);
				socket.setTimeToLive(CONFIG.getMcastTTL());

				setupReceiverThread(socket, CONFIG.getMcastMaxWait(), msg);

				// the multicast convergence algorithm
				long totalTimeout = System.currentTimeMillis() + CONFIG.getMcastMaxWait();
				int[] transmissionSchedule = SLPCore.CONFIG.getMcastTimeouts();
				int retryCounter = 0;
				long nextTimeout;
				int failCounter = 0;
				boolean seenNew = false;
				boolean seenLocalResponse = false;

				nextTimeout = System.currentTimeMillis() + transmissionSchedule[retryCounter];

				while (!Thread.currentThread().isInterrupted() && totalTimeout > System.currentTimeMillis()
						&& nextTimeout > System.currentTimeMillis() && retryCounter < transmissionSchedule.length
						&& failCounter < CONFIG.getConvergenceFailerCount()) {

					msg.prevRespList = responders;
					byte[] message = msg.getBytes();

					// finish convergence in case of message size exeeds MTU
					if (message.length > CONFIG.getMTU()) {
						break;
					}

					// send the message
					DatagramPacket p = new DatagramPacket(message, message.length,
							InetAddress.getByName(SLP_MCAST_ADDRESS), SLP_PORT);

					try {
						socket.send(p);
					} catch (IOException ioe) {
						break;
					}

					ProbeLogger.appendLogln("slp", "SENT " + msg);

					/**
					 * @fix: bug #1518729. Changed processing of the replyQueue.
					 *       Thanks to Richard Reid for figuring out the problem
					 *       with multicast replies and proposing the fix
					 */
					/*try {
						Thread.sleep(transmissionSchedule[retryCounter]);
					} catch (InterruptedException dontcare) {
						// Restore the interrupted status
						Thread.currentThread().interrupt();
					}

					synchronized (replyQueue) {
						// did something else wake us up ?
						if (replyQueue.isEmpty()) {
							failCounter++;
							nextTimeout = System.currentTimeMillis() + transmissionSchedule[retryCounter++];
							continue;
						}
						while (!replyQueue.isEmpty()) {
							reply = (ReplyMessage) replyQueue.remove(0);
							// silently drop duplicate responses, process only
							// new
							// results
							if (!responders.contains(reply.address.getHostAddress())) {
								if (isLocalResponder(reply.address)) {
									if (seenLocalResponse) {
										continue;
									} else {
										seenLocalResponse = true;
									}
								}
								seenNew = true;
								responders.add(reply.address.getHostAddress());
								responses.addAll(reply.getResult());
							}
						}

						if (!seenNew) {
							failCounter++;
						} else {
							seenNew = false;
						}
					}
					nextTimeout = System.currentTimeMillis() + transmissionSchedule[retryCounter++];
				}
			}
			*/

			// we are done, remove the listener queue
			synchronized (replyListeners) {
				replyListeners.remove(queryXID);
			}

			LOG.info("convergence for xid=" + msg.xid + " finished after "
					+ (System.currentTimeMillis() - start) + " ms, result: " + responses);
			return responses;
			/*
		} catch (IOException ioe) {
			ioe.printStackTrace();
			throw new ServiceLocationException(ServiceLocationException.NETWORK_ERROR, ioe.getMessage());
		}*/
	}

	/**
	 * send a request via multicast convergence algorithm.
	 *
	 * @param msg
	 *            the message.
	 * @return the collected reply messages.
	 * @throws ServiceLocationException
	 *             in case of network errors.
	 * @throws SocketTimeoutException
	 */
	static List multicastConvergence(int cnt, final RequestMessage msg) throws ServiceLocationException, SocketTimeoutException {
		long start = System.currentTimeMillis();

		List replyQueue = new ArrayList();
		List responders = new ArrayList();
		List responses = new ArrayList();

		if (msg.xid == 0) {
			msg.xid = SLPCore.nextXid();
		}

		// register the reply queue as listener
		Integer queryXID = new Integer(msg.xid);
		synchronized (replyListeners) {
			replyListeners.put(queryXID, replyQueue);
		}

		msg.port = SLPCore.SLP_PORT;
		msg.prevRespList = new ArrayList();
		msg.multicast = true;

		// send to localhost, in case the OS does not support multicast over
		// loopback which can fail if no SA is running locally

		try {
			msg.address = InetAddress.getByName(ConfigUtil.getInstance().broker_address);
		} catch (UnknownHostException e) {
			msg.address = LOCALHOST;
		}
		try {
			replyQueue.add(sendMessageTCP(cnt, msg)); // TODO uncomment for normal experiment with TCP
			LOG.debug("A reply message is put into replyQueue.");
			//replyQueue.add(sendMessage(msg, false));

		} catch (ServiceLocationException e) {
			if (e.getErrorCode() != ServiceLocationException.NETWORK_ERROR) {
				throw e;
			}
		}

		msg.address = MCAST_ADDRESS;
		ReplyMessage reply;

		// we are done, remove the listener queue
		synchronized (replyListeners) {
			replyListeners.remove(queryXID);
		}

		LOG.info("convergence for xid=" + msg.xid + " finished after "
				+ (System.currentTimeMillis() - start) + " ms, result: " + responses);
		return responses;
	}

	private static boolean isLocalResponder(InetAddress addr) {
		for (int i = 0; i < SLPCore.myIPs.length; i++) {
			if (addr.getHostAddress().equals(SLPCore.myIPs[i])) {
				return true;
			}
		}
		return false;
	}

	/**
	 * setup a new receiver thread for a socket.
	 * 
	 * @param socket
	 *            the <code>DatagramSocket</code> for which the receiver thread
	 *            is set up.
	 * @param minLifetime
	 *            the minimum lifetime of the receiver thread.
	 */
	private static void setupReceiverThread(final DatagramSocket socket, final long minLifetime, final SLPMessage msg) {
		new Thread() {
			public void run() {

				// prepare an empty datagram for receiving
				DatagramPacket packet;
				byte[] bytes = new byte[SLPCore.CONFIG.getMTU()];

				// calculate the end of lifetime
				long timeout = System.currentTimeMillis() + minLifetime + 1000;

				// while lifetime is not expired
				while (System.currentTimeMillis() < timeout) {
					// set socket timeout
					try {
						long l = timeout - System.currentTimeMillis();
						int soTimeout = (int) (l < 0 ? 1 : l);
						socket.setSoTimeout(soTimeout);
					} catch (SocketException e1) {
						LOG.error("Exception in mcast receiver thread");
						return;
					}

					packet = new DatagramPacket(bytes, bytes.length);
					try {
						// try to receive a datagram packet
						LOG.info("Listening...");
						socket.receive(packet);
					} catch (InterruptedIOException iioe) {
						continue;
					} catch (IOException e) {
						e.printStackTrace();
						return;
					}
					final DataInputStream in = new DataInputStream(new ByteArrayInputStream(packet.getData()));
					try {
						// and delegate it to the SLPCore
						try {
							LOG.info("handleMessage starts.");
							handleMessage(SLPMessage.parse(packet.getAddress(), packet.getPort(), in, false));
						} catch (SocketTimeoutException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					} catch (ProtocolException pe) {
						// Overflow, try to use TCP
						try {
							msg.address = packet.getAddress();
							msg.port = packet.getPort();
							msg.multicast = false;
							handleMessage(sendMessageTCP(msg));
						} catch (ServiceLocationException e) {
						} catch (SocketTimeoutException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					} catch (ServiceLocationException e) {
						e.printStackTrace();
					}
				}

				// close the socket
				socket.close();
			}
		}.start();
	}
}
