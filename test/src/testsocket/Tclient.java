package testsocket;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

public class Tclient {
	public static void main(String[] args) throws UnknownHostException, IOException {
		Socket socket = new Socket("localhost", 8099);
		DataOutputStream out = new DataOutputStream(socket
				.getOutputStream());

		out.writeUTF("客户端请求服务器的连接...");

		DataInputStream in = new DataInputStream(socket.getInputStream());

		System.out.println(in.readUTF());
		socket.close();
	}
}
