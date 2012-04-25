package testsocket;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Tserver {
	
	public static void main(String[] args) throws IOException {
		ServerSocket ss = new ServerSocket(10086);
		Socket socket = ss.accept();
		System.out.println(socket.getLocalAddress());
		System.out.println(socket.getLocalSocketAddress());
		System.out.println(socket.getLocalPort());
		System.out.println(socket.getRemoteSocketAddress());
		DataInputStream dis = new DataInputStream(socket.getInputStream());
		// -->用于接收客户端 发来的数据的输入流

		System.out.println("服务器接受到客户端的连接请求:" + dis.readUTF());
		DataOutputStream out = new DataOutputStream(socket
				.getOutputStream());

		out.writeUTF("接受连接请求,连接成功");

		socket.close();

		ss.close();
	}
}
