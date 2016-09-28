package heesuk.percom.sherlock.io;

public class Main {
	public static void main(String[] args){
		ModificationController.getInstance().init();
		ModificationController.getInstance().startSeqVerification(50);
	}
}
