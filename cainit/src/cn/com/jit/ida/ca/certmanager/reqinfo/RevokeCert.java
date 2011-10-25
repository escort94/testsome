package cn.com.jit.ida.ca.certmanager.reqinfo;

public class RevokeCert
{
  private String CertSn;
  private long cdPID;
  private int reason;
  private String reasonDesc;
  private long revokeTime;
  private String signServer;
  private String signClient;
  private long notAfter;

  public long getCdPID()
  {
    return this.cdPID;
  }

  public String getSignServer()
  {
    return this.signServer;
  }

  public String getSignClient()
  {
    return this.signClient;
  }

  public long getRevokeTime()
  {
    return this.revokeTime;
  }

  public String getReasonDesc()
  {
    return this.reasonDesc;
  }

  public int getReason()
  {
    return this.reason;
  }

  public long getNotAfter()
  {
    return this.notAfter;
  }

  public String getCertSn()
  {
    return this.CertSn;
  }

  public void setCdPID(long paramLong)
  {
    this.cdPID = paramLong;
  }

  public void setReason(int paramInt)
  {
    this.reason = paramInt;
  }

  public void setReasonDesc(String paramString)
  {
    this.reasonDesc = paramString;
  }

  public void setRevokeTime(long paramLong)
  {
    this.revokeTime = paramLong;
  }

  public void setSignClient(String paramString)
  {
    this.signClient = paramString;
  }

  public void setSignServer(String paramString)
  {
    this.signServer = paramString;
  }

  public void setNotAfter(long paramLong)
  {
    this.notAfter = paramLong;
  }

  public void setCertSn(String paramString)
  {
    this.CertSn = paramString;
  }
}

/* Location:           C:\Program Files\JIT\CA50\lib\IDA\ida.jar
 * Qualified Name:     cn.com.jit.ida.ca.certmanager.reqinfo.RevokeCert
 * JD-Core Version:    0.6.0
 */