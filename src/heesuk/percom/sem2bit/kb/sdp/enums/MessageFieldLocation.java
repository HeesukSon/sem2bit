package heesuk.percom.sem2bit.kb.sdp.enums;

public enum MessageFieldLocation {
	BODY{
		public String toString(){
			return "BODY";
		}
	},HEADER{
		public String toString(){
			return "HEADER";
		}
	};
	
	public String toString(){
		return toString();
	}
}
