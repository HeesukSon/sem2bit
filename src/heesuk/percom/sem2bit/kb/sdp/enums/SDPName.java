package heesuk.percom.sem2bit.kb.sdp.enums;

public enum SDPName {
	SLPv1{
		public String toString(){
			return "SLPv1";
		}
	},SLPv2{
		public String toString(){
			return "SLPv2";
		}
	},UPnPv1{
		public String toString(){
			return "UPnPv1";
		}
	},UPnPv2{
		public String toString(){
			return "UPnPv2";
		}
	},DNS_SD{
		public String toString(){
			return "DNS_SD";
		}
	},COAP{
		public String toString(){
			return "COAP";
		}
	};
	
	public String toString(){
		return toString();
	}
}
