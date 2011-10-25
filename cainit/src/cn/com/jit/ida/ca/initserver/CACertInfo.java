package cn.com.jit.ida.ca.initserver;

public class CACertInfo
{
  private String SN;
  private int CA_ID;
  private String CA_DESC;
  private String SubjectUpperCase;
  private String Subject;
  private long NotBefore;
  private long NotAfter;
  private int Validity;
  private String AuthInfo;
  private byte[] CertEntity;
  private String DeviceID;
  private String BaseDN;
  private String Remark;
  private String Issuer;
  private String PublicKey;
  private String PrivateKey;
  private String CertStatus;

  public String getSN()
  {
    return this.SN;
  }

  public void setSN(String paramString)
  {
    this.SN = paramString;
  }

  public int getCA_ID()
  {
    return this.CA_ID;
  }

  public void setCA_ID(int paramInt)
  {
    this.CA_ID = paramInt;
  }

  public String getCA_DESC()
  {
    return this.CA_DESC;
  }

  public void setCA_DESC(String paramString)
  {
    this.CA_DESC = paramString;
  }

  public String getSubjectUpperCase()
  {
    return this.SubjectUpperCase;
  }

  public void setSubjectREF(String paramString)
  {
    this.SubjectUpperCase = paramString;
  }

  public String getSubject()
  {
    return this.Subject;
  }

  public void setSubject(String paramString)
  {
    this.Subject = paramString;
  }

  public long getNotBefore()
  {
    return this.NotBefore;
  }

  public void setNotBefore(long paramLong)
  {
    this.NotBefore = paramLong;
  }

  public long getNotAfter()
  {
    return this.NotAfter;
  }

  public void setNotAfter(long paramLong)
  {
    this.NotAfter = paramLong;
  }

  public int getValidity()
  {
    return this.Validity;
  }

  public void setValidity(int paramInt)
  {
    this.Validity = paramInt;
  }

  public String getIssuer()
  {
    return this.Issuer;
  }

  public void setIssuer(String paramString)
  {
    this.Issuer = paramString;
  }

  public String getPublicKey()
  {
    return this.PublicKey;
  }

  public void setPublicKey(String paramString)
  {
    this.PublicKey = paramString;
  }

  public String getPrivateKey()
  {
    return this.PrivateKey;
  }

  public void setPrivateKey(String paramString)
  {
    this.PrivateKey = paramString;
  }

  public String getCertStatus()
  {
    return this.CertStatus;
  }

  public void setCertStatus(String paramString)
  {
    this.CertStatus = paramString;
  }

  public String getAuthInfo()
  {
    return this.AuthInfo;
  }

  public void setAuthInfo(String paramString)
  {
    this.AuthInfo = paramString;
  }

  public byte[] getCertEntity()
  {
    return this.CertEntity;
  }

  public void setCertEntity(byte[] paramArrayOfByte)
  {
    this.CertEntity = paramArrayOfByte;
  }

  public String getDeviceID()
  {
    return this.DeviceID;
  }

  public void setDeviceID(String paramString)
  {
    this.DeviceID = paramString;
  }

  public String getBaseDN()
  {
    return this.BaseDN;
  }

  public void setBaseDN(String paramString)
  {
    this.BaseDN = paramString;
  }

  public String getRemark()
  {
    return this.Remark;
  }

  public void setRemark(String paramString)
  {
    this.Remark = paramString;
  }
}

/* Location:           C:\Program Files\JIT\CA50\lib\IDA\ida.jar
 * Qualified Name:     cn.com.jit.ida.ca.initserver.CACertInfo
 * JD-Core Version:    0.6.0
 */