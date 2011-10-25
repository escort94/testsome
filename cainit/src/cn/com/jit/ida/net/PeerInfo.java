package cn.com.jit.ida.net;

public class PeerInfo
{
  String IP;
  byte[] peerCert;

  public String getIP()
  {
    return this.IP;
  }

  public void setIP(String paramString)
  {
    this.IP = paramString;
  }

  public byte[] getPeerCert()
  {
    return this.peerCert;
  }

  public void setPeerCert(byte[] paramArrayOfByte)
  {
    this.peerCert = paramArrayOfByte;
  }
}

/* Location:           C:\Program Files\JIT\CA50\lib\IDA\ida.jar
 * Qualified Name:     cn.com.jit.ida.net.PeerInfo
 * JD-Core Version:    0.6.0
 */