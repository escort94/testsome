import java.io.IOException;

import sun.misc.BASE64Decoder;


public class TestReq {
	public static void main(String[] args) {
		BASE64Decoder bd = new BASE64Decoder();
		try {
			byte[] b = bd.decodeBuffer("dOpQcd1X4yg=");
			for(byte bb : b){
				System.out.println(bb);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
