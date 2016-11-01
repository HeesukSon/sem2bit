package heesuk.percom.sem2bit.kb.sdp.enums;

public enum RequirementChange {
	CONTENT_LENGTH_CHANGE{
		public String toString(){
			return "CONTENT_LENGTH_CHANGE";
		}
	},CONTROL_OPTION_ADDITION{
		public String toString(){
			return "CONTROL_OPTION_ADDITION";
		}
	},ENCODING_INTEGRATION{
		public String toString(){
			return "ENCODING_INTEGRATION";
		}
	},LANGUAGE_SUPPORT_CHANGE{
		public String toString(){
			return "LANGUAGE_SUPPORT_CHANGE";
		}
	},SECURITY_REQUIREMENT_CHANGE{
		public String toString(){
			return "SECURITY_REQUIREMENT_CHANGE";
		}
	},MULTI_QUERY_SUPPORT{
		public String toString(){
			return "MULTI_QUERY_SUPPORT";
		}
	},MULTI_ENCODING_SUPPORT{
		public String toString(){
			return "MULTI_ENCODING_SUPPORT";
		}
	},SESSION_MGMT_CHANGE{
		public String toString(){
			return "SESSION_MGMT_CHANGE";
		}
	};
	
	public String toString(){
		return toString();
	}
}