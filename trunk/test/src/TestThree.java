import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;


public class TestThree {
	public static void main(String[] args) throws FileNotFoundException {
		System.out.println(new String("asdf") == new String("asdf") ? "asdf" : "a");
		InputStream fis1 = new FileInputStream("");
		System.out.println(fis1 == null ? "":"");
	}
}
