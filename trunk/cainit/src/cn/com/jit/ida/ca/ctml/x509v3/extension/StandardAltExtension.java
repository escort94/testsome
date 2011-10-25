package cn.com.jit.ida.ca.ctml.x509v3.extension;

public class StandardAltExtension
{
  private String parentAltName = null;
  private String parentAltOID = null;
  private String childAltName = null;
  private String standardAltValue = null;
  private String parentAltEncode = null;
  private String childAltEncode = null;
  private String childAltNameLabel = null;
  private String specialAltCtmlID = "00";
  private String allowAltNull = "true";
  private String otherNameOid;

  public String getAllowAltNull()
  {
    return this.allowAltNull;
  }

  public String getChildAltEncode()
  {
    return this.childAltEncode;
  }

  public String getChildAltNameLabel()
  {
    return this.childAltNameLabel;
  }

  public String getChildAltName()
  {
    return this.childAltName;
  }

  public String getParentAltEncode()
  {
    return this.parentAltEncode;
  }

  public String getParentAltName()
  {
    return this.parentAltName;
  }

  public String getParentAltOID()
  {
    return this.parentAltOID;
  }

  public String getSpecialAltCtmlID()
  {
    return this.specialAltCtmlID;
  }

  public String getStandardAltValue()
  {
    return this.standardAltValue;
  }

  public void setStandardAltValue(String paramString)
  {
    this.standardAltValue = paramString;
  }

  public void setSpecialAltCtmlID(String paramString)
  {
    this.specialAltCtmlID = paramString;
  }

  public void setParentAltOID(String paramString)
  {
    this.parentAltOID = paramString;
  }

  public void setParentAltName(String paramString)
  {
    this.parentAltName = paramString;
  }

  public void setParentAltEncode(String paramString)
  {
    this.parentAltEncode = paramString;
  }

  public void setChildAltNameLabel(String paramString)
  {
    this.childAltNameLabel = paramString;
  }

  public void setChildAltName(String paramString)
  {
    this.childAltName = paramString;
  }

  public void setChildAltEncode(String paramString)
  {
    this.childAltEncode = paramString;
  }

  public void setAllowAltNull(String paramString)
  {
    this.allowAltNull = paramString;
  }

  public String getOtherNameOid()
  {
    return this.otherNameOid;
  }

  public void setOtherNameOid(String paramString)
  {
    this.otherNameOid = paramString;
  }
}

/* Location:           C:\Program Files\JIT\CA50\lib\IDA\ida.jar
 * Qualified Name:     cn.com.jit.ida.ca.ctml.x509v3.extension.StandardAltExtension
 * JD-Core Version:    0.6.0
 */