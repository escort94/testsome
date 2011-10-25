package cn.com.jit.ida.ca.ctml.x509v3.extension;

public class StandardPolicyConstExtension
{
  private String parentPCName = null;
  private String parentPCOID = null;
  private String childPCName = null;
  private String standardPCValue = null;
  private String parentPCEncode = null;
  private String childPCEncode = null;
  private String childPCNameLabel = null;
  private String specialPCCtmlID = "00";
  private String allowPCNull = "true";

  public String getAllowPCNull()
  {
    return this.allowPCNull;
  }

  public String getChildPCEncode()
  {
    return this.childPCEncode;
  }

  public String getChildPCName()
  {
    return this.childPCName;
  }

  public String getChildPCNameLabel()
  {
    return this.childPCNameLabel;
  }

  public String getParentPCEncode()
  {
    return this.parentPCEncode;
  }

  public String getParentPCName()
  {
    return this.parentPCName;
  }

  public String getParentPCOID()
  {
    return this.parentPCOID;
  }

  public String getSpecialPCCtmlID()
  {
    return this.specialPCCtmlID;
  }

  public String getStandardPCValue()
  {
    return this.standardPCValue;
  }

  public void setStandardPCValue(String paramString)
  {
    this.standardPCValue = paramString;
  }

  public void setSpecialPCCtmlID(String paramString)
  {
    this.specialPCCtmlID = paramString;
  }

  public void setParentPCOID(String paramString)
  {
    this.parentPCOID = paramString;
  }

  public void setParentPCName(String paramString)
  {
    this.parentPCName = paramString;
  }

  public void setParentPCEncode(String paramString)
  {
    this.parentPCEncode = paramString;
  }

  public void setChildPCNameLabel(String paramString)
  {
    this.childPCNameLabel = paramString;
  }

  public void setChildPCName(String paramString)
  {
    this.childPCName = paramString;
  }

  public void setChildPCEncode(String paramString)
  {
    this.childPCEncode = paramString;
  }

  public void setAllowPCNull(String paramString)
  {
    this.allowPCNull = paramString;
  }
}

/* Location:           C:\Program Files\JIT\CA50\lib\IDA\ida.jar
 * Qualified Name:     cn.com.jit.ida.ca.ctml.x509v3.extension.StandardPolicyConstExtension
 * JD-Core Version:    0.6.0
 */