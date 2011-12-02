import java.net.UnknownHostException;
import java.text.ParseException;



public class TestNothing {
	public static void main(String[] args) throws ParseException, UnknownHostException {
		System.out.println(java.net.InetAddress.getLocalHost().getHostAddress());
    }

}
