
public class TestException {
	public static void main(String[] args) {
		try{
			String s = null;
			s.length();
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
}
