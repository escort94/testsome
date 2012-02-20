package suanfa;

public class QuickSort {
	public static void method(int[] array) {
		int i = 0;
		int j = array.length - 1;
		int k = array[0];
		while (i != j) {
			while (array[j] > k) {
				j--;
			}
			int temp = k;
			k = array[j];
			array[j] = temp;
			while (array[i] < k) {
				i++;
			}
			int temp1 = k;
			k = array[i];
			array[i] = temp1;
		}
		if (k == array[j]) {
		}
	}

	public static void main(String[] args) {
		int[] array = { 49, 38, 65, 97, 76, 13, 27 };
		QuickSort.method(array);
		for (int i = 0; i < array.length; i++) {
			System.out.print(array[i] + " ");
		}
	}
}