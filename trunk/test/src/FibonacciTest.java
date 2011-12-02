/**
 * 斐波那契数列,非递归实现
 * 
 * @author
 * 
 */
public class FibonacciTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		guoqing(50);
		System.out.println();
		fib(50);
		System.out.println();
	}

	/**
	 * 斐波那契求解
	 * 
	 * @param n
	 *            前n项
	 */
	public static void fibonacci(int n) {
		int two = 1; // 之前的第二个数
		int one = 1; // 前一个数
		int now = 0; // 当前值
		System.out.print(two + " " + one + " ");
		n -= 2;
		while (n > 0) {
			now = two + one;
			System.out.print(now + " ");
			two = one;
			one = now;
			n--;
		}
	}

	public static void guoqing(int n) {
		if (n <= 0) {
			System.out.println("参数不合法，请调整参数");
			return;
		}
		switch (n) {
		case 1:
			System.out.print(1 + " ");
			break;
		case 2:
			System.out.print(1 + " " + 1 + " ");
			break;
		default:
			long beforOne = 1;
			long beforeTwo = 1;
			long now;
			System.out.print(beforOne + " " + beforeTwo + " ");
			n -= 2;
			while (n > 0) {
				now = beforOne + beforeTwo;
				System.out.print(now + " ");
				beforeTwo = beforOne;
				beforOne = now;
				n--;
			}
		}
	}

	public static void fib(int n) {
		if (n < 0) {
			System.out.println("the param is unrightful");
		}
		switch (n) {
		case 1:
			System.out.println(1 + " ");
			break;
		case 2:
			System.out.println(1 + " " + 1 + " ");
			break;
		default:
			int two = 1;
			int one = 1;
			int now;
			System.out.print(1 + " " + 1 + " ");
			n -= 2;
			while (n > 0) {
				now = one + two;
				two = one;
				one = now;
				System.out.print(51 - n + ":" + now + " ");
				n--;
			}
		}
	}

	public static int fibonacci1(int n) {
		if (n >= 0)
			if (n == 0 || n == 1)
				return 1;
			else
				return fibonacci1(n - 2) + fibonacci1(n - 1);
		return -1;
	}

	public static int fibonaccidigui(int i) {
		if (i > 0)
			if (i == 0 || i == 1)
				return 1;
			else
				return fibonaccidigui(i - 2) + fibonaccidigui(i - 1);
		return -1;
	}
}
