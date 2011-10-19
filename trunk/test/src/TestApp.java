public class TestApp {
	public static void main(String argv[]) {
		String password = PasswordField.readPassword("Enter password: ");
		System.out.println("The password entered is: " + password);
	}
}
