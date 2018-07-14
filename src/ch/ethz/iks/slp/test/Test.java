package ch.ethz.iks.slp.test;

public class Test {
	public static void main(String[] args){
		for(int i=0; i<3; i++){
			int shift = (8 * (3 - i));
			int shifted = ((int) 56 >> shift);
			byte a = (byte) (shifted & 0xFF);
			System.out.println(a);
		}
	}
}
