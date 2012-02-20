public class Sushu1 {

	public static void main(String[] args) {
		for (int i = 2; i <= 100; i++) {
			for (int j = 2; j < i/2; j++) {
				if (i % j == 0){
					System.out.println("不是素数" + i);
					break;
				}
			}
		}
	}
}
