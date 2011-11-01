public class StringtoInt {
	String str;
	public static void main(String[] args) {
		System.out.println(Integer.MAX_VALUE);
		System.out.println(Integer.MIN_VALUE);
		try {
			System.out.println(new StringtoInt().str);
			System.out.println(getInt("2147483648"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static int getInt(String s) throws Exception {
		try {
			return Integer.parseInt(s);
		} catch (Exception e) {
			System.out.println("input param error");
			throw e;
		}
	}
}
