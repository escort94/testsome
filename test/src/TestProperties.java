import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Properties;

public class TestProperties {
	public static void main(String[] arhgs) {
		long start = System.currentTimeMillis();
		Properties pro = new Properties();
		try {
			pro.load(new FileInputStream("count.txt"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		int num = Integer.parseInt(pro.getProperty("count")) + 1;

		pro.setProperty("count", String.valueOf(num));
		pro.setProperty("count1", "asdfasdf");
		try {
			pro.store(new FileOutputStream("count.txt"), "program is used:");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
