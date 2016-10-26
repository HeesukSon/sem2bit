package heesuk.percom.sem2bit.kb.sdp;

import heesuk.percom.sem2bit.kb.sdp.enums.Functionality;
import heesuk.percom.sem2bit.kb.sdp.enums.RequirementChange;
import heesuk.percom.sem2bit.kb.sdp.enums.UpdatePattern;

public class MessageFieldUpdate {
	private MessageField field;
	private RequirementChange reqChange;
	private UpdatePattern pattern;
	private Functionality func;
	private String prevSDP;
	private String nextSDP;

	public MessageFieldUpdate() {

	}

	public MessageFieldUpdate(MessageField field, RequirementChange reqChange, UpdatePattern pattern,
			Functionality func, String prevSDP, String nextSDP) {
		this();
		
		this.field = field;
		this.reqChange = reqChange;
		this.pattern = pattern;
		this.func = func;
		this.prevSDP = prevSDP;
		this.nextSDP = nextSDP;
	}

	public MessageField getField() {
		return field;
	}

	public void setField(MessageField field) {
		this.field = field;
	}

	public RequirementChange getReqChange() {
		return reqChange;
	}

	public void setReqChange(RequirementChange reqChange) {
		this.reqChange = reqChange;
	}

	public UpdatePattern getPattern() {
		return pattern;
	}

	public void setPattern(UpdatePattern pattern) {
		this.pattern = pattern;
	}

	public Functionality getFunc() {
		return func;
	}

	public void setFunc(Functionality func) {
		this.func = func;
	}

	public String getPrevSDP() {
		return prevSDP;
	}

	public void setPrevSDP(String prevSDP) {
		this.prevSDP = prevSDP;
	}

	public String getNextSDP() {
		return nextSDP;
	}

	public void setNextSDP(String nextSDP) {
		this.nextSDP = nextSDP;
	}
	
	
}
