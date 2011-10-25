package cn.com.jit.ida.ca.ctml.x509v3.extension;

public class StandardExtension
{
  private String parentName = null;
  private String parentOID = null;
  private String childName = null;
  private String standardValue = null;
  private String parentEncode = null;
  private String childEncode = null;
  private String childNameLabel = null;
  private String specialCtmlID = "00";
  private String allowNull = "true";
  private String otherNameOid = null;

  public String getChildName()
  {
    return this.childName;
  }

  public String getParentName()
  {
    return this.parentName;
  }

  public String getParentOID()
  {
    return this.parentOID;
  }

  public String getChildEncode()
  {
    return this.childEncode;
  }

  public String getParentEncode()
  {
    return this.parentEncode;
  }

  public String getChildNameLabel()
  {
    return this.childNameLabel;
  }

  public String getStandardValue()
  {
    return this.standardValue;
  }

  public String getSpecialCtmlID()
  {
    return this.specialCtmlID;
  }

  public String getAllowNull()
  {
    return this.allowNull;
  }

  public String getOtherNameOid()
  {
    return this.otherNameOid;
  }

  public void setChildName(String paramString)
  {
    this.childName = paramString;
  }

  public void setParentName(String paramString)
  {
    this.parentName = paramString;
  }

  public void setParentOID(String paramString)
  {
    this.parentOID = paramString;
  }

  public void setChildEncode(String paramString)
  {
    this.childEncode = paramString;
  }

  public void setParentEncode(String paramString)
  {
    this.parentEncode = paramString;
  }

  public void setChildNameLabel(String paramString)
  {
    this.childNameLabel = paramString;
  }

  public void setStandardValue(String paramString)
  {
    this.standardValue = paramString;
  }

  public void setSpecialCtmlID(String paramString)
  {
    this.specialCtmlID = paramString;
  }

  public void setAllowNull(String paramString)
  {
    this.allowNull = paramString;
  }

  public void setOtherNameOid(String paramString)
  {
    this.otherNameOid = paramString;
  }
}

/* Location:           C:\Program Files\JIT\CA50\lib\IDA\ida.jar
 * Qualified Name:     cn.com.jit.ida.ca.ctml.x509v3.extension.StandardExtension
 * JD-Core Version:    0.6.0
 */