package cn.com.jit.ida.ca.ctml.x509v3.extension.policy;

import cn.com.jit.ida.IDAException;
import cn.com.jit.ida.ca.ctml.x509v3.extension.X509V3ExtensionPolicy;
import cn.com.jit.ida.util.xml.XMLTool;
import org.w3c.dom.Element;

public class InsuranceNumberPolicy extends X509V3ExtensionPolicy
{
  private static final String XMLTAG_INSURANCE_NUM = "insuranceNum";
  private String insuranceNum = null;
  private String insuranceNumAllowNull = "TRUE";
  private static final String XMLTAG_ALLOW_NULL = "allowNull";
  private static final String XMLTAG_CHECK = "check";

  public InsuranceNumberPolicy()
    throws IDAException
  {
    super("InsuranceNumber");
  }

  public String getInsuranceNum()
  {
    return this.insuranceNum;
  }

  public String getInsuranceNumAllowNull()
  {
    return this.insuranceNumAllowNull;
  }

  public void setInsuranceNum(String paramString)
  {
    this.insuranceNum = paramString;
  }

  public void setInsuranceNumAllowNull(String paramString)
  {
    this.insuranceNumAllowNull = paramString;
  }

  protected void updateData(boolean paramBoolean)
    throws IDAException
  {
    super.updateData(paramBoolean);
    Element localElement1;
    Element localElement2;
    if (paramBoolean)
    {
      localElement1 = this.xml.newElement("value");
      this.xmlElement.appendChild(localElement1);
      if (this.insuranceNum != null)
      {
        localElement2 = XMLTool.newChildElement(localElement1, "insuranceNum");
        XMLTool.newChildElement(localElement2, "check", this.insuranceNum.trim());
        XMLTool.newChildElement(localElement2, "allowNull", this.insuranceNumAllowNull.trim());
      }
    }
    else
    {
      localElement1 = XMLTool.getElementByTagName(this.xmlElement, "value");
      if (localElement1 != null)
      {
        localElement2 = XMLTool.getElementByTagName(localElement1, "insuranceNum");
        if (localElement2 != null)
        {
          this.insuranceNum = XMLTool.getValueByTagName(localElement2, "check").trim();
          this.insuranceNumAllowNull = XMLTool.getValueByTagName(localElement2, "allowNull").trim();
        }
      }
    }
  }
}

/* Location:           C:\Program Files\JIT\CA50\lib\IDA\ida.jar
 * Qualified Name:     cn.com.jit.ida.ca.ctml.x509v3.extension.policy.InsuranceNumberPolicy
 * JD-Core Version:    0.6.0
 */