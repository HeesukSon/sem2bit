package heesuk.percom.sem2bit.kb.protocol.enums;

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
