package cn.com.jit.ida.ca.ctml.x509v3.extension;

public class StandardInsuranceExtension
{
  private String parentInsuranceName = null;
  private String parentInsuranceOID = null;
  private String childInsuranceName = null;
  private String standardInsuranceValue = null;
  private String parentInsuranceEncode = null;
  private String childInsuranceEncode = null;
  private String childInsuranceNameLabel = null;
  private String specialInsuranceCtmlID = "00";
  private String allowInsuranceNull = "true";

  public String getAllowInsuranceNull()
  {
    return this.allowInsuranceNull;
  }

  public String getChildInsuranceEncode()
  {
    return this.childInsuranceEncode;
  }

  public String getChildInsuranceNameLabel()
  {
    return this.childInsuranceNameLabel;
  }

  public String getChildInsuranceName()
  {
    return this.childInsuranceName;
  }

  public String getParentInsuranceEncode()
  {
    return this.parentInsuranceEncode;
  }

  public String getParentInsuranceName()
  {
    return this.parentInsuranceName;
  }

  public String getParentInsuranceOID()
  {
    return this.parentInsuranceOID;
  }

  public String getSpecialInsuranceCtmlID()
  {
    return this.specialInsuranceCtmlID;
  }

  public String getStandardInsuranceValue()
  {
    return this.standardInsuranceValue;
  }

  public void setStandardInsuranceValue(String paramString)
  {
    this.standardInsuranceValue = paramString;
  }

  public void setSpecialInsuranceCtmlID(String paramString)
  {
    this.specialInsuranceCtmlID = paramString;
  }

  public void setParentInsuranceOID(String paramString)
  {
    this.parentInsuranceOID = paramString;
  }

  public void setParentInsuranceName(String paramString)
  {
    this.parentInsuranceName = paramString;
  }

  public void setParentInsuranceEncode(String paramString)
  {
    this.parentInsuranceEncode = paramString;
  }

  public void setChildInsuranceNameLabel(String paramString)
  {
    this.childInsuranceNameLabel = paramString;
  }

  public void setChildInsuranceName(String paramString)
  {
    this.childInsuranceName = paramString;
  }

  public void setChildInsuranceEncode(String paramString)
  {
    this.childInsuranceEncode = paramString;
  }

  public void setAllowInsuranceNull(String paramString)
  {
    this.allowInsuranceNull = paramString;
  }
}

/* Location:           C:\Program Files\JIT\CA50\lib\IDA\ida.jar
 * Qualified Name:     cn.com.jit.ida.ca.ctml.x509v3.extension.StandardInsuranceExtension
 * JD-Core Version:    0.6.0
 */