package cn.com.jit.ida.ca.issue.entity;

public class CRLEntity extends BaseEntity
{
  private long cdpid = -1L;
  private byte[] crlContent = null;
  private String issuerRelativePath = "ou=crl";

  public CRLEntity(String paramString)
  {
    super(paramString);
    this.entityType = "CRL";
  }

  public String getDistributionPath()
  {
    if (this.cdpid == -1L)
      return "cn=crl," + this.issuerRelativePath + "," + this.baseDN;
    return "cn=crl" + this.cdpid + "," + this.issuerRelativePath + "," + this.baseDN;
  }

  public void setIssuerRelativePath(String paramString)
  {
    this.issuerRelativePath = paramString;
  }

  public String getIssuerRelativePath()
  {
    return this.issuerRelativePath;
  }

  public long getCdpid()
  {
    return this.cdpid;
  }

  public void setCdpid(long paramLong)
  {
    this.cdpid = paramLong;
  }

  public byte[] getCrlContent()
  {
    return this.crlContent;
  }

  public void setCrlContent(byte[] paramArrayOfByte)
  {
    this.crlContent = paramArrayOfByte;
  }
}

/* Location:           C:\Program Files\JIT\CA50\lib\IDA\ida.jar
 * Qualified Name:     cn.com.jit.ida.ca.issue.entity.CRLEntity
 * JD-Core Version:    0.6.0
 */