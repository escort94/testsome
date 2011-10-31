package testmina;

import java.net.InetSocketAddress;
import java.nio.charset.Charset;

import org.apache.mina.core.service.IoConnector;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.textline.LineDelimiter;
import org.apache.mina.filter.codec.textline.TextLineCodecFactory;
import org.apache.mina.transport.socket.nio.NioSocketConnector;
import org.slf4j.Logger;

public class MyClient {
	public static void main(String[] args) {
		IoConnector connector = new NioSocketConnector();
		connector.setConnectTimeoutMillis(30000);
		connector.getFilterChain().addLast(
				"codec",
				new ProtocolCodecFilter(new TextLineCodecFactory(Charset
						.forName("UTF-8"), LineDelimiter.WINDOWS.getValue(),
						LineDelimiter.WINDOWS.getValue())));
		connector.setHandler(new ClientHandler("你好！\r\n 大家好！"));
		connector.connect(new InetSocketAddress("localhost", 9123));
		connector.setHandler(new ClientHandler("你好！\r\n 大家好！"));
	}
}

class ClientHandler extends IoHandlerAdapter {
	private final static Logger LOGGER = org.slf4j.LoggerFactory
			.getLogger(ClientHandler.class);
	private final String values;

	public ClientHandler(String values) {
		this.values = values;
	}

	@Override
	public void sessionOpened(IoSession session) {
		session.write(values);
	}
}