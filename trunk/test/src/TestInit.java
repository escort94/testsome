public class TestInit {
	public static void main(String[] args) {
		System.out.println(System.getProperties().get("java.library.path")
				.toString());
	}
}
