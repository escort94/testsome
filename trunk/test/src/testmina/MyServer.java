package testmina;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;

import org.apache.mina.core.filterchain.IoFilter;
import org.apache.mina.core.filterchain.IoFilterChain;
import org.apache.mina.core.service.IoAcceptor;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.core.write.WriteRequest;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.textline.LineDelimiter;
import org.apache.mina.filter.codec.textline.TextLineCodecFactory;
import org.apache.mina.filter.logging.LogLevel;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.filter.util.ReferenceCountingFilter;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;

public class MyServer {
	public static void main(String[] args) throws IOException {
		IoAcceptor acceptor = new NioSocketAcceptor();
		LoggingFilter lf = new LoggingFilter();
		lf.setSessionOpenedLogLevel(LogLevel.DEBUG);
		acceptor.getFilterChain().addLast("logger", lf);
		acceptor.getSessionConfig().setReadBufferSize(2048);
		acceptor.getSessionConfig().setIdleTime(IdleStatus.BOTH_IDLE, 10);
		acceptor.setHandler(new MyIoHandler());
		acceptor.getFilterChain().addLast("myIoFilter",
				new ReferenceCountingFilter(new MyIoFilter()));
		acceptor.getFilterChain().addLast("myIoFilter1",
				new ReferenceCountingFilter(new MyIoFilter1()));
		acceptor.getFilterChain().addLast(
				"codec",
				new ProtocolCodecFilter(new TextLineCodecFactory(Charset
						.forName("UTF-8"), LineDelimiter.WINDOWS.getValue(),
						LineDelimiter.WINDOWS.getValue())));
		acceptor.bind(new InetSocketAddress(9123));
	}

}

class MyIoHandler extends IoHandlerAdapter {
	// 这里我们使用的SLF4J作为日志门面，至于为什么在后面说明。
	// private final static Logger log =
	// LoggerFactory.getLogger(MyIoHandler.class);

	public void messageReceived(IoSession session, Object message)
			throws Exception {
		String str = message.toString();
		System.out.println("The message received is [" + str + "]");
		if (str.endsWith("quit")) {
			session.close(true);
			return;
		}
	}
}

class MyIoFilter implements IoFilter {
	public void destroy() throws Exception {
		System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%destroy");
	}

	public void exceptionCaught(NextFilter nextFilter, IoSession session,
			Throwable cause) throws Exception {
		System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%exceptionCaught");
		nextFilter.exceptionCaught(session, cause);
	}

	public void filterClose(NextFilter nextFilter, IoSession session)
			throws Exception {
		System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%filterClose");
		nextFilter.filterClose(session);
	}

	public void filterWrite(NextFilter nextFilter, IoSession session,
			WriteRequest writeRequest) throws Exception {
		System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%filterWrite");
		nextFilter.filterWrite(session, writeRequest);
	}

	public void init() throws Exception {
		System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%init");
	}

	public void messageReceived(NextFilter nextFilter, IoSession session,
			Object message) throws Exception {
		System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%messageReceived");
		System.out.println("======asdfasdf");
		nextFilter.messageReceived(session, message);
	}

	public void messageSent(NextFilter nextFilter, IoSession session,
			WriteRequest writeRequest) throws Exception {
		System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%messageSent");
		nextFilter.messageSent(session, writeRequest);
	}

	public void onPostAdd(IoFilterChain parent, String name,
			NextFilter nextFilter) throws Exception {
		System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%onPostAdd");
	}

	public void onPostRemove(IoFilterChain parent, String name,
			NextFilter nextFilter) throws Exception {
		System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%onPostRemove");
	}

	public void onPreAdd(IoFilterChain parent, String name,
			NextFilter nextFilter) throws Exception {
		System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%onPreAdd");
	}

	public void onPreRemove(IoFilterChain parent, String name,
			NextFilter nextFilter) throws Exception {
		System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%onPreRemove");
	}

	public void sessionClosed(NextFilter nextFilter, IoSession session)
			throws Exception {
		System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%sessionClosed");
		nextFilter.sessionClosed(session);
	}

	public void sessionCreated(NextFilter nextFilter, IoSession session)
			throws Exception {
		System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%sessionCreated");
		nextFilter.sessionCreated(session);
	}

	public void sessionIdle(NextFilter nextFilter, IoSession session,
			IdleStatus status) throws Exception {
		System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%sessionIdle");
		nextFilter.sessionIdle(session, status);
	}

	public void sessionOpened(NextFilter nextFilter, IoSession session)
			throws Exception {
		System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%sessionOpened");
		nextFilter.sessionOpened(session);
	}
}
class MyIoFilter1 implements IoFilter {
	public void destroy() throws Exception {
		System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%destroy1");
	}

	public void exceptionCaught(NextFilter nextFilter, IoSession session,
			Throwable cause) throws Exception {
		System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%exceptionCaught1");
		nextFilter.exceptionCaught(session, cause);
	}

	public void filterClose(NextFilter nextFilter, IoSession session)
			throws Exception {
		System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%filterClose1");
		nextFilter.filterClose(session);
	}

	public void filterWrite(NextFilter nextFilter, IoSession session,
			WriteRequest writeRequest) throws Exception {
		System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%filterWrite1");
		nextFilter.filterWrite(session, writeRequest);
	}

	public void init() throws Exception {
		System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%init1");
	}

	public void messageReceived(NextFilter nextFilter, IoSession session,
			Object message) throws Exception {
		System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%messageReceived1");
		System.out.println("======asdfasdf");
		nextFilter.messageReceived(session, message);
	}

	public void messageSent(NextFilter nextFilter, IoSession session,
			WriteRequest writeRequest) throws Exception {
		System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%messageSent1");
		nextFilter.messageSent(session, writeRequest);
	}

	public void onPostAdd(IoFilterChain parent, String name,
			NextFilter nextFilter) throws Exception {
		System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%onPostAdd1");
	}

	public void onPostRemove(IoFilterChain parent, String name,
			NextFilter nextFilter) throws Exception {
		System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%onPostRemove1");
	}

	public void onPreAdd(IoFilterChain parent, String name,
			NextFilter nextFilter) throws Exception {
		System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%onPreAdd1");
	}

	public void onPreRemove(IoFilterChain parent, String name,
			NextFilter nextFilter) throws Exception {
		System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%onPreRemove1");
	}

	public void sessionClosed(NextFilter nextFilter, IoSession session)
			throws Exception {
		System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%sessionClosed1");
		nextFilter.sessionClosed(session);
	}

	public void sessionCreated(NextFilter nextFilter, IoSession session)
			throws Exception {
		System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%sessionCreated1");
		nextFilter.sessionCreated(session);
	}

	public void sessionIdle(NextFilter nextFilter, IoSession session,
			IdleStatus status) throws Exception {
		System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%sessionIdle1");
		nextFilter.sessionIdle(session, status);
	}

	public void sessionOpened(NextFilter nextFilter, IoSession session)
			throws Exception {
		System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%sessionOpened1");
		nextFilter.sessionOpened(session);
	}
}