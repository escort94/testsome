package cn.com.jit.ida.globalconfig;

public class Information
{
  String name = null;
  String value = null;
  String isEncrypted = null;

  public Information(String paramString1, String paramString2, String paramString3)
  {
    this.name = paramString1;
    this.value = paramString2;
    this.isEncrypted = paramString3;
  }

  public Information()
  {
  }

  public String getName()
  {
    return this.name;
  }

  public void setName(String paramString)
  {
    this.name = paramString;
  }

  public String getValue()
  {
    if (this.value == null)
      return "";
    return this.value;
  }

  public void setValue(String paramString)
  {
    this.value = paramString;
  }

  public String getIsEncrypted()
  {
    if (this.isEncrypted == null)
      return "";
    return this.isEncrypted;
  }

  public void setIsEncrypted(String paramString)
  {
    this.isEncrypted = paramString;
  }
}

/* Location:           C:\Program Files\JIT\CA50\lib\IDA\ida.jar
 * Qualified Name:     cn.com.jit.ida.globalconfig.Information
 * JD-Core Version:    0.6.0
 */