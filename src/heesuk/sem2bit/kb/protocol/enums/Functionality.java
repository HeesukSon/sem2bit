package heesuk.sem2bit.kb.protocol.enums;

public enum Functionality {
	SERVICE_SEARCH{
		public String toString(){
			return "SERVICE_SEARCH";
		}
	},CONTENT_PARSING{
		public String toString(){
			return "CONTENT_PARSING";
		}
	},PROTOCOL_BASIC_INFO{
		public String toString(){ return "PROTOCOL_BASIC_INFO";}
	},PROTOCOL_BEHAVIOR{
		public String toString(){ return "PROTOCOL_BEHAVIOR";}
	}, SESSION_MGMT{
		public String toString(){ return "SESSION_MGMT";}
	}, SECURITY{
		public String toString() {return "SECURITY";}
	};
	
	public String toString(){
		return toString();
	}
}
