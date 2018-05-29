package heesuk.percom.sem2bit.kb.protocol.enums;

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
	},VALUE_CHANGE{
		public String toString() {return "[C]";}
	};
	public String toString(){
		return toString();
	}
}
