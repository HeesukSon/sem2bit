package heesuk.percom.sherlock.io.kb.sdp;

public enum MessageFieldType {
	SERVICE_TYPE{
		public String toString(){
			return "SERVICE_TYPE";
		}
	}, TARGET_ADDRESS{
		public String toString(){
			return "TARGET_ADDRESS";
		}
	},VERSION_INFO{
		public String toString(){
			return "VERSION_INFO";
		}
	},RETURN_CODE{
		public String toString(){
			return "RETURN_CODE";
		}
	},MESSAGE_TYPE{
		public String toString(){
			return "MESSAGE_TYPE";
		}
	},CONTROL_FLAG{
		public String toString(){
			return "CONTROL_FLAG";
		}
	},SESSION_MGMT{
		public String toString(){
			return "SESSION_MGMT";
		}
	},MESSAGE_LENGTH{
		public String toString(){
			return "MESSAGE_LENGTH";
		}
	},ENCODING{
		public String toString(){
			return "ENCODING";
		}
	},LANGUAGE_CODE{
		public String toString(){
			return "LANGUAGE_CODE";
		}
	},LANGUAGE_TAG{
		public String toString(){
			return "LANGUAGE_TAG";
		}
	},LANGUAGE_TAG_LENGTH{
		public String toString(){
			return "LANGUAGE_TAG_LENGTH";
		}
	},QUERY_COUNT{
		public String toString(){
			return "QUERY_COUNT";
		}
	},ANSWER_COUNT{
		public String toString(){
			return "ANSWER_COUNT";
		}
	};
	
	public String toString(){
		return toString();
	}
}
