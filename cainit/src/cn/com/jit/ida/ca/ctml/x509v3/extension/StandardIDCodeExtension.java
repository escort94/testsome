package cn.com.jit.ida.ca.ctml.x509v3.extension;

public class StandardIDCodeExtension
{
  private String parentIDCodeName = null;
  private String parentIDCodeOID = null;
  private String childIDCodeName = null;
  private String standardIDCodeValue = null;
  private String parentIDCodeEncode = null;
  private String childIDCodeEncode = null;
  private String childIDCodeNameLabel = null;
  private String specialIDCodeCtmlID = "00";
  private String allowIDCodeNull = "true";

  public String getAllowIDCodeNull()
  {
    return this.allowIDCodeNull;
  }

  public String getChildIDCodeEncode()
  {
    return this.childIDCodeEncode;
  }

  public String getChildIDCodeNameLabel()
  {
    return this.childIDCodeNameLabel;
  }

  public String getChildIDCodeName()
  {
    return this.childIDCodeName;
  }

  public String getParentIDCodeEncode()
  {
    return this.parentIDCodeEncode;
  }

  public String getParentIDCodeName()
  {
    return this.parentIDCodeName;
  }

  public String getParentIDCodeOID()
  {
    return this.parentIDCodeOID;
  }

  public String getSpecialIDCodeCtmlID()
  {
    return this.specialIDCodeCtmlID;
  }

  public String getStandardIDCodeValue()
  {
    return this.standardIDCodeValue;
  }

  public void setStandardIDCodeValue(String paramString)
  {
    this.standardIDCodeValue = paramString;
  }

  public void setSpecialIDCodeCtmlID(String paramString)
  {
    this.specialIDCodeCtmlID = paramString;
  }

  public void setParentIDCodeOID(String paramString)
  {
    this.parentIDCodeOID = paramString;
  }

  public void setParentIDCodeName(String paramString)
  {
    this.parentIDCodeName = paramString;
  }

  public void setParentIDCodeEncode(String paramString)
  {
    this.parentIDCodeEncode = paramString;
  }

  public void setChildIDCodeNameLabel(String paramString)
  {
    this.childIDCodeNameLabel = paramString;
  }

  public void setChildIDCodeName(String paramString)
  {
    this.childIDCodeName = paramString;
  }

  public void setChildIDCodeEncode(String paramString)
  {
    this.childIDCodeEncode = paramString;
  }

  public void setAllowIDCodeNull(String paramString)
  {
    this.allowIDCodeNull = paramString;
  }
}

/* Location:           C:\Program Files\JIT\CA50\lib\IDA\ida.jar
 * Qualified Name:     cn.com.jit.ida.ca.ctml.x509v3.extension.StandardIDCodeExtension
 * JD-Core Version:    0.6.0
 */