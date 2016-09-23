package heesuk.percom.sherlock.io.kb.sdp;

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
