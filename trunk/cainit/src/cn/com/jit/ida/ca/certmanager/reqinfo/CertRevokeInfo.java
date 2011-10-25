package cn.com.jit.ida.ca.certmanager.reqinfo;

public class CertRevokeInfo
{
  private String certSN = null;
  private String ctmlName = null;
  private String certDN = null;
  private int reasonID = -1;
  private String reasonDESC = null;
  private String applicant = null;
  private long CDPID = 0L;
  private long revokeTime = 0L;

  public void setCDPID(long paramLong)
  {
    this.CDPID = paramLong;
  }

  public long getCDPID()
  {
    return this.CDPID;
  }

  public void setCertSN(String paramString)
  {
    this.certSN = paramString;
  }

  public String getCertSN()
  {
    return this.certSN;
  }

  public void setReasonDESC(String paramString)
  {
    this.reasonDESC = paramString;
  }

  public String getReasonDESC()
  {
    return this.reasonDESC;
  }

  public int getReasonID()
  {
    return this.reasonID;
  }

  public void setReasonID(int paramInt)
  {
    this.reasonID = paramInt;
  }

  public void setRevokeTime(long paramLong)
  {
    this.revokeTime = paramLong;
  }

  public long getRevokeTime()
  {
    return this.revokeTime;
  }

  public String getCertDN()
  {
    return this.certDN;
  }

  public void setCertDN(String paramString)
  {
    this.certDN = paramString;
  }

  public String getCtmlName()
  {
    return this.ctmlName;
  }

  public void setCtmlName(String paramString)
  {
    this.ctmlName = paramString;
  }

  public String getApplicant()
  {
    return this.applicant;
  }

  public void setApplicant(String paramString)
  {
    this.applicant = paramString;
  }
}

/* Location:           C:\Program Files\JIT\CA50\lib\IDA\ida.jar
 * Qualified Name:     cn.com.jit.ida.ca.certmanager.reqinfo.CertRevokeInfo
 * JD-Core Version:    0.6.0
 */