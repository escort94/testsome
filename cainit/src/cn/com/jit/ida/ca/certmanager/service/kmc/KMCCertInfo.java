package cn.com.jit.ida.ca.certmanager.service.kmc;

public class KMCCertInfo
{
  private String certSN = null;
  private String certDN = null;
  private long notBefore = 0L;
  private long notAfter = 0L;
  private int validity = 0;
  private String ctmlName = null;
  private String tempPubKey = null;
  private String retainKey = "false";
  private String oldCertSN = null;

  public String getCertDN()
  {
    return this.certDN;
  }

  public String getCertSN()
  {
    return this.certSN;
  }

  public String getCtmlName()
  {
    return this.ctmlName;
  }

  public long getNotAfter()
  {
    return this.notAfter;
  }

  public long getNotBefore()
  {
    return this.notBefore;
  }

  public String getTempPubKey()
  {
    return this.tempPubKey;
  }

  public int getValidity()
  {
    return this.validity;
  }

  public String getRetainKey()
  {
    return this.retainKey;
  }

  public void setValidity(int paramInt)
  {
    this.validity = paramInt;
  }

  public void setTempPubKey(String paramString)
  {
    this.tempPubKey = paramString;
  }

  public void setNotBefore(long paramLong)
  {
    this.notBefore = paramLong;
  }

  public void setNotAfter(long paramLong)
  {
    this.notAfter = paramLong;
  }

  public void setCtmlName(String paramString)
  {
    this.ctmlName = paramString;
  }

  public void setCertSN(String paramString)
  {
    this.certSN = paramString;
  }

  public void setCertDN(String paramString)
  {
    this.certDN = paramString;
  }

  public void setRetainKey(String paramString)
  {
    this.retainKey = paramString;
  }

  public String getOldCertSN()
  {
    return this.oldCertSN;
  }

  public void setOldCertSN(String paramString)
  {
    this.oldCertSN = paramString;
  }
}

/* Location:           C:\Program Files\JIT\CA50\lib\IDA\ida.jar
 * Qualified Name:     cn.com.jit.ida.ca.certmanager.service.kmc.KMCCertInfo
 * JD-Core Version:    0.6.0
 */