import java.io.IOException;

import sun.misc.BASE64Decoder;


public class TestBase {
	public static void main(String[] args) {
		BASE64Decoder b = new BASE64Decoder();
		try {
			byte[] E = b.decodeBuffer("ajhvMGoHTN2TSy9eD+fZ6wq5T2GVyooK81Ph7A==");
			for(byte e : E){
				System.out.print(e);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
