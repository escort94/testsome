package cn.com.jit.ida.ca.ctml.x509v3.extension;

public class StandardPolicyMappingsExtension
{
  private String parentPMName = null;
  private String parentPMOID = null;
  private String childPMName = null;
  private String standardPMValue = null;
  private String parentPMEncode = null;
  private String childPMCEncode = null;
  private String childPMNameLabel = null;
  private String specialPMCtmlID = "00";
  private String allowPMNull = "true";

  public String getAllowPMNull()
  {
    return this.allowPMNull;
  }

  public String getChildPMCEncode()
  {
    return this.childPMCEncode;
  }

  public String getChildPMName()
  {
    return this.childPMName;
  }

  public String getChildPMNameLabel()
  {
    return this.childPMNameLabel;
  }

  public String getParentPMEncode()
  {
    return this.parentPMEncode;
  }

  public String getParentPMName()
  {
    return this.parentPMName;
  }

  public String getParentPMOID()
  {
    return this.parentPMOID;
  }

  public String getSpecialPMCtmlID()
  {
    return this.specialPMCtmlID;
  }

  public String getStandardPMValue()
  {
    return this.standardPMValue;
  }

  public void setStandardPMValue(String paramString)
  {
    this.standardPMValue = paramString;
  }

  public void setSpecialPMCtmlID(String paramString)
  {
    this.specialPMCtmlID = paramString;
  }

  public void setParentPMOID(String paramString)
  {
    this.parentPMOID = paramString;
  }

  public void setParentPMName(String paramString)
  {
    this.parentPMName = paramString;
  }

  public void setParentPMEncode(String paramString)
  {
    this.parentPMEncode = paramString;
  }

  public void setChildPMNameLabel(String paramString)
  {
    this.childPMNameLabel = paramString;
  }

  public void setChildPMName(String paramString)
  {
    this.childPMName = paramString;
  }

  public void setChildPMCEncode(String paramString)
  {
    this.childPMCEncode = paramString;
  }

  public void setAllowPMNull(String paramString)
  {
    this.allowPMNull = paramString;
  }
}

/* Location:           C:\Program Files\JIT\CA50\lib\IDA\ida.jar
 * Qualified Name:     cn.com.jit.ida.ca.ctml.x509v3.extension.StandardPolicyMappingsExtension
 * JD-Core Version:    0.6.0
 */