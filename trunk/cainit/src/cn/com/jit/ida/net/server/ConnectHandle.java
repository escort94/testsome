package cn.com.jit.ida.net.server;

import cn.com.jit.ida.net.NetException;
import cn.com.jit.ida.net.PeerInfo;
import java.net.Socket;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import javax.net.ssl.SSLPeerUnverifiedException;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;

public class ConnectHandle
{
  private Socket socket;
  private PeerInfo peerInfo;

  public ConnectHandle(Socket paramSocket)
    throws NetException
  {
    this.socket = paramSocket;
    this.peerInfo = new PeerInfo();
    if (paramSocket.isConnected())
      this.peerInfo.setIP(paramSocket.getRemoteSocketAddress().toString());
    if ((paramSocket instanceof SSLSocket))
      try
      {
        SSLSession localSSLSession = ((SSLSocket)paramSocket).getSession();
        if (localSSLSession == null)
          throw new NetException("05", "获取SSLSession失败:sslSession=null");
        Certificate[] arrayOfCertificate = localSSLSession.getPeerCertificates();
        if (arrayOfCertificate == null)
          throw new NetException("05", "获取对方证书失败失败:certs=null");
        if (arrayOfCertificate.length == 0)
          throw new NetException("05", "获取对方证书失败失败:certs.length=0");
        this.peerInfo.setPeerCert(arrayOfCertificate[0].getEncoded());
      }
      catch (SSLPeerUnverifiedException localSSLPeerUnverifiedException)
      {
        this.peerInfo.setPeerCert(null);
        throw new NetException("05", "获取对方证书失败", localSSLPeerUnverifiedException);
      }
      catch (CertificateEncodingException localCertificateEncodingException)
      {
        this.peerInfo.setPeerCert(null);
        throw new NetException("05", "获取对方证书失败", localCertificateEncodingException);
      }
  }

  public Socket getSocket()
  {
    return this.socket;
  }

  public String getPeerIP()
  {
    return this.socket.getRemoteSocketAddress().toString();
  }

  public PeerInfo getPeerInfo()
  {
    return this.peerInfo;
  }
}

/* Location:           C:\Program Files\JIT\CA50\lib\IDA\ida.jar
 * Qualified Name:     cn.com.jit.ida.net.server.ConnectHandle
 * JD-Core Version:    0.6.0
 */