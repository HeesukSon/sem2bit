package heesuk.percom.sem2bit.kb.protocol.enums;

public enum ProtocolName {
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
	},MQTTv3{
		public String toString() { return "MQTTv3";}
	},MQTTv5{
		public String toString() {return "MQTTv5";}
	},AMQP{
		public String toString() {return "AMQP";}
	},SIPv1{
		public String toString() {return "SIPv1";}
	},SIPv2{
		public String toString() {return "SIPv2";}
	};
	
	public String toString(){
		return toString();
	}
}
