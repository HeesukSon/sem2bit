package heesuk.percom.sem2bit.kb.sdp.enums;

public enum UpdatePattern {
	CHANGE_FIELD_LENGTH{
		public String toString(){
			return "[L]";
		}
	},DELETE_FIELD{
		public String toString(){
			return "[D]";
		}
	},ADD_NEW_FIELD{
		public String toString(){
			return "[A]";
		}
	},CHANGE_VOCA{
		public String toString(){
			return "[V]";
		}
	};
	public String toString(){
		return toString();
	}
}
