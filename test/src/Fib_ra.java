public class Fib_ra {
	public static int fibonacci(int n) {
		if (n >= 0)
			if (n == 0 || n == 1)
				return 1;
			else
				return fibonacci(n - 2) + fibonacci(n - 1);
		return -1;
	}

	public static void main(String args[]) {

		int m = 40, n;
		int fib[] = new int[m];
		for (n = 0; n < m; n++)
			fib[n] = fibonacci(n);
		for (n = 0; n < fib.length; n++)
			System.out.print(" " + fib[n]);
		System.out.println();
	}
}