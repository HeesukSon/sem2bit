package heesuk.sem2bit.main;

import java.util.Random;

public class Test {
	public static void main(String[] args){
		Random r = new Random();
		for(int i=0; i<100; i++){
			System.out.println((int)r.nextGaussian()+3);
		}
	}
}
