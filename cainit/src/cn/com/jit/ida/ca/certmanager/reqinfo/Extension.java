package cn.com.jit.ida.ca.certmanager.reqinfo;

public class Extension
{
  private String oid;
  private String name;
  private String value;

  public String getName()
  {
    return this.name;
  }

  public String getOid()
  {
    return this.oid;
  }

  public String getValue()
  {
    return this.value;
  }

  public void setValue(String paramString)
  {
    this.value = paramString;
  }

  public void setOid(String paramString)
  {
    this.oid = paramString;
  }

  public void setName(String paramString)
  {
    this.name = paramString;
  }
}

/* Location:           C:\Program Files\JIT\CA50\lib\IDA\ida.jar
 * Qualified Name:     cn.com.jit.ida.ca.certmanager.reqinfo.Extension
 * JD-Core Version:    0.6.0
 */