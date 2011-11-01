import java.util.Random;


public class TestNothing {
	public static void main(String[] args) {
		Random random = new Random();
		for(int i = 0 ; i < 20 ; i ++){
		double ran = Math.random();
		System.out.println((int)(ran * 100));
		}
//		for(int i = 0 ; i < 20 ; i ++){
//			int rand = random.nextInt();
//			System.out.print(rand + " ");
//		}
	}
}
