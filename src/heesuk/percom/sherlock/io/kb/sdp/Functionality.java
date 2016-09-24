package heesuk.percom.sherlock.io.kb.sdp;

public enum Functionality {
	SERVICE_SEARCH{
		public String toString(){
			return "SERVICE_SEARCH";
		}
	},CONTENT_PARSING{
		public String toString(){
			return "CONTENT_PARSING";
		}
	},MESSAGE_HANDLING{
		public String toString(){
			return "MESSAGE_HANDLING";
		}
	};
	
	public String toString(){
		return toString();
	}
}
