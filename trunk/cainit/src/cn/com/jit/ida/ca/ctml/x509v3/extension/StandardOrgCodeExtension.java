package cn.com.jit.ida.ca.ctml.x509v3.extension;

public class StandardOrgCodeExtension
{
  private String parentOrgCodeName = null;
  private String parentOrgCodeOID = null;
  private String childOrgCodeName = null;
  private String standardOrgCodeValue = null;
  private String parentOrgCodeEncode = null;
  private String childOrgCodeEncode = null;
  private String childOrgCodeNameLabel = null;
  private String specialOrgCodeCtmlID = "00";
  private String allowOrgCodeNull = "true";

  public String getAllowOrgCodeNull()
  {
    return this.allowOrgCodeNull;
  }

  public String getChildOrgCodeEncode()
  {
    return this.childOrgCodeEncode;
  }

  public String getChildOrgCodeNameLabel()
  {
    return this.childOrgCodeNameLabel;
  }

  public String getChildOrgCodeName()
  {
    return this.childOrgCodeName;
  }

  public String getParentOrgCodeEncode()
  {
    return this.parentOrgCodeEncode;
  }

  public String getParentOrgCodeName()
  {
    return this.parentOrgCodeName;
  }

  public String getParentOrgCodeOID()
  {
    return this.parentOrgCodeOID;
  }

  public String getSpecialOrgCodeCtmlID()
  {
    return this.specialOrgCodeCtmlID;
  }

  public String getStandardOrgCodeValue()
  {
    return this.standardOrgCodeValue;
  }

  public void setStandardOrgCodeValue(String paramString)
  {
    this.standardOrgCodeValue = paramString;
  }

  public void setSpecialOrgCodeCtmlID(String paramString)
  {
    this.specialOrgCodeCtmlID = paramString;
  }

  public void setParentOrgCodeOID(String paramString)
  {
    this.parentOrgCodeOID = paramString;
  }

  public void setParentOrgCodeName(String paramString)
  {
    this.parentOrgCodeName = paramString;
  }

  public void setParentOrgCodeEncode(String paramString)
  {
    this.parentOrgCodeEncode = paramString;
  }

  public void setChildOrgCodeNameLabel(String paramString)
  {
    this.childOrgCodeNameLabel = paramString;
  }

  public void setChildOrgCodeName(String paramString)
  {
    this.childOrgCodeName = paramString;
  }

  public void setChildOrgCodeEncode(String paramString)
  {
    this.childOrgCodeEncode = paramString;
  }

  public void setAllowOrgCodeNull(String paramString)
  {
    this.allowOrgCodeNull = paramString;
  }
}

/* Location:           C:\Program Files\JIT\CA50\lib\IDA\ida.jar
 * Qualified Name:     cn.com.jit.ida.ca.ctml.x509v3.extension.StandardOrgCodeExtension
 * JD-Core Version:    0.6.0
 */