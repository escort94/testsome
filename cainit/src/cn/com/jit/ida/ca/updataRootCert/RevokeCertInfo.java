package cn.com.jit.ida.ca.updataRootCert;

import cn.com.jit.ida.util.pki.cert.X509Cert;

public class RevokeCertInfo
{
  private String certSN;
  private int reason;
  private String reasionESC;
  private long revokeTime;
  private long CDPID;
  private long notAfter;

  public RevokeCertInfo()
  {
  }

  public RevokeCertInfo(X509Cert paramX509Cert)
  {
    this.certSN = paramX509Cert.getStringSerialNumber();
    this.revokeTime = System.currentTimeMillis();
    this.reason = 4;
    this.reasionESC = "根证书更新";
    this.CDPID = 1L;
  }

  public long getNotAfter()
  {
    return this.notAfter;
  }

  public void setNotAfter(long paramLong)
  {
    this.notAfter = paramLong;
  }

  public String getCertSN()
  {
    return this.certSN;
  }

  public void setCertSN(String paramString)
  {
    this.certSN = paramString;
  }

  public long getCDPID()
  {
    return this.CDPID;
  }

  public void setCDPID(long paramLong)
  {
    this.CDPID = paramLong;
  }

  public int getReason()
  {
    return this.reason;
  }

  public void setReason(int paramInt)
  {
    this.reason = paramInt;
  }

  public String getReasionESC()
  {
    return this.reasionESC;
  }

  public void setReasionESC(String paramString)
  {
    this.reasionESC = paramString;
  }

  public long getRevokeTime()
  {
    return this.revokeTime;
  }

  public void setRevokeTime(long paramLong)
  {
    this.revokeTime = paramLong;
  }
}

/* Location:           C:\Program Files\JIT\CA50\lib\IDA\ida.jar
 * Qualified Name:     cn.com.jit.ida.ca.updataRootCert.RevokeCertInfo
 * JD-Core Version:    0.6.0
 */