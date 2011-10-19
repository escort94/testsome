import java.net.UnknownHostException;


public class TestIP {
	public static void main(String[] args) {
		try {
			String ip = java.net.InetAddress.getLocalHost().getHostAddress();
			System.out.println(ip);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
