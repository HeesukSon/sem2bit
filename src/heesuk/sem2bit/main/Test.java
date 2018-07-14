package heesuk.sem2bit.main;

public class Test {
	public static void main(String[] args){
		long sum = 0;
		for(int i=1; i<=6; i++){
			sum += fact(6)/fact(6-i);
		}

		System.out.println(sum);
	}

	static long fact(int x){
		long result = 1;
		for(int i=x;i>0;i--){
			result *= i;
		}

		return result;
	}
}
