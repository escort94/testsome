package cn.com.jit.ida.ca.ctml.x509v3.extension;

public class StandardTaxExtension
{
  private String parentTaxName = null;
  private String parentTaxOID = null;
  private String childTaxName = null;
  private String standardTaxValue = null;
  private String parentTaxEncode = null;
  private String childTaxEncode = null;
  private String childTaxNameLabel = null;
  private String specialTaxCtmlID = "00";
  private String allowTaxNull = "true";

  public String getAllowTaxNull()
  {
    return this.allowTaxNull;
  }

  public String getChildTaxEncode()
  {
    return this.childTaxEncode;
  }

  public String getChildTaxNameLabel()
  {
    return this.childTaxNameLabel;
  }

  public String getChildTaxName()
  {
    return this.childTaxName;
  }

  public String getParentTaxEncode()
  {
    return this.parentTaxEncode;
  }

  public String getParentTaxName()
  {
    return this.parentTaxName;
  }

  public String getParentTaxOID()
  {
    return this.parentTaxOID;
  }

  public String getSpecialTaxCtmlID()
  {
    return this.specialTaxCtmlID;
  }

  public String getStandardTaxValue()
  {
    return this.standardTaxValue;
  }

  public void setStandardTaxValue(String paramString)
  {
    this.standardTaxValue = paramString;
  }

  public void setSpecialTaxCtmlID(String paramString)
  {
    this.specialTaxCtmlID = paramString;
  }

  public void setParentTaxOID(String paramString)
  {
    this.parentTaxOID = paramString;
  }

  public void setParentTaxName(String paramString)
  {
    this.parentTaxName = paramString;
  }

  public void setParentTaxEncode(String paramString)
  {
    this.parentTaxEncode = paramString;
  }

  public void setChildTaxNameLabel(String paramString)
  {
    this.childTaxNameLabel = paramString;
  }

  public void setChildTaxName(String paramString)
  {
    this.childTaxName = paramString;
  }

  public void setChildTaxEncode(String paramString)
  {
    this.childTaxEncode = paramString;
  }

  public void setAllowTaxNull(String paramString)
  {
    this.allowTaxNull = paramString;
  }
}

/* Location:           C:\Program Files\JIT\CA50\lib\IDA\ida.jar
 * Qualified Name:     cn.com.jit.ida.ca.ctml.x509v3.extension.StandardTaxExtension
 * JD-Core Version:    0.6.0
 */