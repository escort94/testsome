package testsocket;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

	public static void main(String[] args) {

		try {

			ServerSocket ss = new ServerSocket(8099);

			System.out.println("服务器已启动,等到客户端的连接...");

			Socket socket = ss.accept();// -->服务器收到客户端的数据后,创建与此客户端对话的Socket
			System.out.println(socket.getLocalAddress());
			System.out.println(socket.getLocalSocketAddress());
			System.out.println(socket.getLocalPort());
			System.out.println(socket.getRemoteSocketAddress());
			DataInputStream in = new DataInputStream(socket.getInputStream());

			// -->用于接收客户端 发来的数据的输入流

			System.out.println("服务器接受到客户端的连接请求:" + in.readUTF());

			DataOutputStream out = new DataOutputStream(socket
					.getOutputStream());

			out.writeUTF("接受连接请求,连接成功");

			socket.close();

			ss.close();

		} catch (Exception e) {

			e.printStackTrace();
		}

	}

}
