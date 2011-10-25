package cn.com.jit.ida.ca.ctml;

public class SelfExtensionInformation
{
  private String name;
  private String status;
  private String oid;
  private String encoding;
  private String description;

  public String getExtensionName()
  {
    return this.name;
  }

  public void setExtensionName(String paramString)
  {
    this.name = paramString;
  }

  public String getExtensionOID()
  {
    return this.oid;
  }

  public void setExtensionOID(String paramString)
  {
    this.oid = paramString;
  }

  public String getExtensionStatus()
  {
    return this.status;
  }

  public void setExtensionStatus(String paramString)
  {
    this.status = paramString;
  }

  public String getExtensionEncoding()
  {
    return this.encoding;
  }

  public void setExtensionEncoding(String paramString)
  {
    this.encoding = paramString;
  }

  public String getExtensionDesc()
  {
    return this.description;
  }

  public void setExtensionDesc(String paramString)
  {
    this.description = paramString;
  }
}

/* Location:           C:\Program Files\JIT\CA50\lib\IDA\ida.jar
 * Qualified Name:     cn.com.jit.ida.ca.ctml.SelfExtensionInformation
 * JD-Core Version:    0.6.0
 */