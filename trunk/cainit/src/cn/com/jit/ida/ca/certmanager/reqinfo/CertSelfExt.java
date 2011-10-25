package cn.com.jit.ida.ca.certmanager.reqinfo;

public class CertSelfExt
{
  private String certSn;
  private String oid;
  private String name;
  private String value;
  private String signServer;
  private String signClient;

  public String getName()
  {
    return this.name;
  }

  public String getOid()
  {
    return this.oid;
  }

  public String getSignClient()
  {
    return this.signClient;
  }

  public String getSignServer()
  {
    return this.signServer;
  }

  public String getValue()
  {
    return this.value;
  }

  public String getCertSn()
  {
    return this.certSn;
  }

  public void setName(String paramString)
  {
    this.name = paramString;
  }

  public void setOid(String paramString)
  {
    this.oid = paramString;
  }

  public void setSignClient(String paramString)
  {
    this.signClient = paramString;
  }

  public void setSignServer(String paramString)
  {
    this.signServer = paramString;
  }

  public void setValue(String paramString)
  {
    this.value = paramString;
  }

  public void setCertSn(String paramString)
  {
    this.certSn = paramString;
  }
}

/* Location:           C:\Program Files\JIT\CA50\lib\IDA\ida.jar
 * Qualified Name:     cn.com.jit.ida.ca.certmanager.reqinfo.CertSelfExt
 * JD-Core Version:    0.6.0
 */