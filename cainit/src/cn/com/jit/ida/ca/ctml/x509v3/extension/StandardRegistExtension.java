package cn.com.jit.ida.ca.ctml.x509v3.extension;

public class StandardRegistExtension
{
  private String parentRegistName = null;
  private String parentRegistOID = null;
  private String childRegistName = null;
  private String standardRegistValue = null;
  private String parentRegistEncode = null;
  private String childRegistEncode = null;
  private String childRegistNameLabel = null;
  private String specialRegistCtmlID = "00";
  private String allowRegistNull = "true";

  public String getAllowRegistNull()
  {
    return this.allowRegistNull;
  }

  public String getChildRegistEncode()
  {
    return this.childRegistEncode;
  }

  public String getChildRegistNameLabel()
  {
    return this.childRegistNameLabel;
  }

  public String getChildRegistName()
  {
    return this.childRegistName;
  }

  public String getParentRegistEncode()
  {
    return this.parentRegistEncode;
  }

  public String getParentRegistName()
  {
    return this.parentRegistName;
  }

  public String getParentRegistOID()
  {
    return this.parentRegistOID;
  }

  public String getSpecialRegistCtmlID()
  {
    return this.specialRegistCtmlID;
  }

  public String getStandardRegistValue()
  {
    return this.standardRegistValue;
  }

  public void setStandardRegistValue(String paramString)
  {
    this.standardRegistValue = paramString;
  }

  public void setSpecialRegistCtmlID(String paramString)
  {
    this.specialRegistCtmlID = paramString;
  }

  public void setParentRegistOID(String paramString)
  {
    this.parentRegistOID = paramString;
  }

  public void setParentRegistName(String paramString)
  {
    this.parentRegistName = paramString;
  }

  public void setParentRegistEncode(String paramString)
  {
    this.parentRegistEncode = paramString;
  }

  public void setChildRegistNameLabel(String paramString)
  {
    this.childRegistNameLabel = paramString;
  }

  public void setChildRegistName(String paramString)
  {
    this.childRegistName = paramString;
  }

  public void setChildRegistEncode(String paramString)
  {
    this.childRegistEncode = paramString;
  }

  public void setAllowRegistNull(String paramString)
  {
    this.allowRegistNull = paramString;
  }
}

/* Location:           C:\Program Files\JIT\CA50\lib\IDA\ida.jar
 * Qualified Name:     cn.com.jit.ida.ca.ctml.x509v3.extension.StandardRegistExtension
 * JD-Core Version:    0.6.0
 */