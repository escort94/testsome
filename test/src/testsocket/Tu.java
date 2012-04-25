package testsocket;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

public class Tu {
	public static void main(String[] args) throws IOException {
		byte[] buf = new byte[1024];
		DatagramPacket dp = new DatagramPacket(buf, 1024);
		DatagramSocket ds = new DatagramSocket(5678);
		ds.receive(dp);
		System.out.println(new String(buf, 0, 1024));
	}
}
