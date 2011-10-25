package cn.com.jit.ida.ca.certmanager.reqinfo;

public class CertStandardExt
{
  private String CertSn;
  private String extOID;
  private String extName;
  private String childName;
  private String otherNameOID;
  private String value;
  private String signServer;
  private String signClient;

  public String getCertSn()
  {
    return this.CertSn;
  }

  public void setCertSn(String paramString)
  {
    this.CertSn = paramString;
  }

  public void setChildName(String paramString)
  {
    this.childName = paramString;
  }

  public void setExtName(String paramString)
  {
    this.extName = paramString;
  }

  public void setExtOID(String paramString)
  {
    this.extOID = paramString;
  }

  public void setOtherNameOID(String paramString)
  {
    this.otherNameOID = paramString;
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

  public String getChildName()
  {
    return this.childName;
  }

  public String getExtName()
  {
    return this.extName;
  }

  public String getExtOID()
  {
    return this.extOID;
  }

  public String getOtherNameOID()
  {
    return this.otherNameOID;
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
}

/* Location:           C:\Program Files\JIT\CA50\lib\IDA\ida.jar
 * Qualified Name:     cn.com.jit.ida.ca.certmanager.reqinfo.CertStandardExt
 * JD-Core Version:    0.6.0
 */