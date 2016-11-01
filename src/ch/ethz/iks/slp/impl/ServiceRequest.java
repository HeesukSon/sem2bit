/* Copyright (c) 2005-2007 Jan S. Rellermeyer
 * Information and Communication Systems Research Group (IKS),
 * Department of Computer Science, ETH Zurich.
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

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import ch.ethz.iks.slp.ServiceLocationException;
import ch.ethz.iks.slp.ServiceType;
import ch.ethz.iks.slp.impl.filter.Filter;
import heesuk.percom.sem2bit.Configurations;
import heesuk.percom.sem2bit.ExperimentStat;
import heesuk.percom.sem2bit.ProbeLogger;
import heesuk.percom.sem2bit.kb.TreeFactory;
import heesuk.percom.sem2bit.kb.sdp.MessageField;
import heesuk.percom.sem2bit.kb.sdp.SDPKBUtil;
import heesuk.percom.sem2bit.kb.sdp.enums.SDPName;
import heesuk.percom.sem2bit.msg.ModificationCandidate;
import heesuk.percom.sem2bit.msg.ProbeMessageComposer;

/**
 * ServiceRequest message is used to find services in the network.
 * 
 * @author Jan S. Rellermeyer, IKS, ETH Z�rich
 * @since 0.1
 */
class ServiceRequest extends RequestMessage {

	/**
	 * the ServiceType.
	 */
	ServiceType serviceType;

	/**
	 * a RFC 1960 compliant filter predicate.
	 */
	Filter predicate;

	/**
	 * the spi String.
	 */
	String spi;

	/**
	 * creates a new ServiceRequest for a ServiceType.
	 * 
	 * @param type
	 *            the ServiceType.
	 * @param scopes
	 *            a list of scopes to be included.
	 * @param filterStr
	 *            a filter String, RFC 1960 compliant.
	 * @param theLocale
	 *            the Locale of the message.
	 * @throws InvalidSyntaxException
	 *             if the filter is not well-formed.
	 */
	ServiceRequest(final ServiceType type, final List scopes, String filterStr,
			final Locale theLocale) throws IllegalArgumentException {
		funcID = SRVRQST;
		prevRespList = new ArrayList();
		serviceType = type;
		predicate = filterStr == null ? null : SLPCore.platform
				.createFilter(filterStr);
		scopeList = scopes;
		if (scopeList == null) {
			scopeList = new ArrayList();
			scopeList.add("default");
		}

		locale = theLocale == null ? SLPCore.DEFAULT_LOCALE : theLocale;
		spi = SLPCore.CONFIG.getSecurityEnabled() ? SLPCore.CONFIG.getSPI()
				: "";
	}

	/**
	 * create a new ServiceRequest from a DataInput streaming the bytes of a
	 * ServiceRequest message body.
	 * 
	 * @param input
	 *            stream of bytes forming the message body.
	 * @throws ServiceLocationException
	 *             in case that the IO caused an exception.
	 * @throws IOException
	 */
	protected ServiceRequest(final DataInputStream input) throws IOException {
		prevRespList = stringToList(input.readUTF(), ",");
		serviceType = new ServiceType(input.readUTF());
		scopeList = stringToList(input.readUTF(), ",");
		try {
			final String filterStr = input.readUTF();
			predicate = "".equals(filterStr) ? null : SLPCore.platform
					.createFilter(filterStr);
		} catch (IllegalArgumentException ise) {
			SLPCore.platform.logError("Invalid filter in incoming message "
						+ xid, ise);
		}
		spi = input.readUTF();
	}

	/**
	 * get the bytes of the message body in the following RFC 2608 compliant
	 * format:
	 * <p>
	 * 
	 * <pre>
	 *         0                   1                   2                   3
	 *         0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1
	 *        +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	 *        |       Service Location header (function = SrvRqst = 1)        |
	 *        +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	 *        |      length of &lt;PRList&gt;       |        &lt;PRList&gt; String        \
	 *        +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	 *        |   length of &lt;service-type&gt;    |    &lt;service-type&gt; String      \
	 *        +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	 *        |    length of &lt;scope-list&gt;     |     &lt;scope-list&gt; String       \
	 *        +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	 *        |  length of predicate string   |  Service Request &lt;predicate&gt;  \
	 *        +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	 *        |  length of &lt;SLP SPI&gt; string   |       &lt;SLP SPI&gt; String        \
	 *        +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	 * </pre>.
	 * </p>
	 * 
	 * @return array of bytes.
	 * @throws ServiceLocationException
	 * @throws ServiceLocationException
	 *             if an IO Exception occurs.
	 */
	protected void writeTo(final DataOutputStream out) throws IOException {
		//super.writeHeader(out, getSize());
		long beforeSeqComp = System.currentTimeMillis();
		ModificationCandidate[] seq = TreeFactory.getInstance().getNextSequence();
		long afterSeqComp = System.currentTimeMillis();
		ExperimentStat.getInstance().setSeqComputeTimeTotal(ExperimentStat.getInstance().getSeqComputeTimeTotal()+(afterSeqComp-beforeSeqComp));

		if(Configurations.exp_mode.equals("mockup")){
			boolean result = true;

			ModificationCandidate[] rightAnswer = new ModificationCandidate[8];
			rightAnswer[0] = new ModificationCandidate("DEFAULT", "[DEFAULT]");
			rightAnswer[1] = new ModificationCandidate("Language Code", "[D]");
			rightAnswer[2] = new ModificationCandidate("Control", "[L]");
			rightAnswer[3] = new ModificationCandidate("Control", "[V]");
			rightAnswer[4] = new ModificationCandidate("Length", "[L]");
			rightAnswer[5] = new ModificationCandidate("Char Encoding", "[D]");
			rightAnswer[6] = new ModificationCandidate("LANGUAGE_TAG_LENGTH", "[A]");
			rightAnswer[7] = new ModificationCandidate("LANGUAGE_TAG", "[A]");

			StringBuilder seqStr = new StringBuilder();
			seqStr.append("Computed next sequence = ");

			for(int i=0; i<seq.length; i++){
				seqStr.append(seq[i].toStringWithoutWeight()+"  ");
				if(!seq[i].sameWith(rightAnswer[i]))
					result = false;
			}

			seqStr.append("\n");
			ProbeLogger.appendLogln("probe", seqStr.toString());

			if(result == true){
				ProbeMessageComposer.getInstance().writeMsgHeader(SDPKBUtil.getInstance().getSDP(SDPName.SLPv2).getMesage().getFieldList(), out, getSize(), xid);
			}else{
				ProbeMessageComposer.getInstance().writeMsgHeader(SDPKBUtil.getInstance().getSDP(SDPName.SLPv1).getMesage().getFieldList(), out, getSize(), xid);
			}
		}else {
			ArrayList<MessageField> modifiedFields = ProbeMessageComposer.getInstance().getModifiedFieldList(SDPKBUtil.getInstance().getLocalSDP().getMesage().getFieldList(), seq);
			ProbeMessageComposer.getInstance().writeMsgHeader(modifiedFields, out, getSize(), xid);
		}

		out.writeUTF(listToString(prevRespList, ","));
		out.writeUTF(serviceType.toString());
		out.writeUTF(listToString(scopeList, ","));
		out.writeUTF(predicate == null ? "" : predicate.toString());
		out.writeUTF(spi);
		
		long afterHeaderComposition = System.currentTimeMillis();
		ExperimentStat.getInstance().setMsgComposeTimeTotal(ExperimentStat.getInstance().getMsgComposeTimeTotal()+(afterHeaderComposition-afterSeqComp));
	}

	/**
	 * get the length of the message.
	 * 
	 * @return the length of the message.
	 * @see ch.ethz.iks.slp.impl.SLPMessage#getSize()
	 */
	int getSize() {
		return getHeaderSize() + 2 + listToString(prevRespList, ",").length()
				+ 2 + serviceType.toString().length() + 2
				+ listToString(scopeList, ",").length() + 2
				+ (predicate == null ? 0 : predicate.toString().length()) + 2
				+ spi.length();
	}

	/**
	 * get a string representation of the AttributeReply message.
	 * 
	 * @return a String displaying the properties of this message instance.
	 */
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append(super.toString());
		buffer.append(", prevRespList: " + prevRespList);
		buffer.append(", serviceType: " + serviceType);
		buffer.append(", scopeList: " + scopeList);
		buffer.append(", predicate: "
				+ (predicate == null ? "" : predicate.toString()));
		buffer.append(", slpSpi: " + spi);
		return buffer.toString();
	}

}
