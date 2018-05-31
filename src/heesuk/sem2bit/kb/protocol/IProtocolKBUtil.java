package heesuk.sem2bit.kb.protocol;

public interface IProtocolKBUtil {
	public void addProtocolInfo();
	public void addUpdateHistory();
	public MessageField getMsgField(String fType);
	public int getNewFieldLength(String fName);
}
