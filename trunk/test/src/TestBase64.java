import java.io.IOException;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

public class TestBase64 {

	public static void main(String[] args) throws IOException {
		System.out
				.println(new String(
						new BASE64Decoder()
								.decodeBuffer("thunder://QUFodHRwOi8vdGh1bmRlci5mZmR5LmNjLzQ1NUI5NDZFQTZGQTZCNTg2Qzk0MTlEODRBNDI5QkYzOTk3OUY3M0Mvy6vIy7SyzPXUvC5ybXZiWlo="),
						"GBK"));
		
		System.out.println(new BASE64Encoder().encodeBuffer("changeit".getBytes()));
	}
}
