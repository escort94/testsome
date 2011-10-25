package cn.com.jit.ida.ca.ctml.x509v3.extension.policy;

import cn.com.jit.ida.IDAException;
import cn.com.jit.ida.ca.ctml.x509v3.extension.X509V3ExtensionPolicy;
import cn.com.jit.ida.util.xml.XMLTool;
import org.w3c.dom.Element;

public class TaxationNumberPolicy extends X509V3ExtensionPolicy
{
  private static final String XMLTAG_TAXA_NUM = "taxaNum";
  private String taxaNum = null;
  private String taxaNumAllowNull = "TRUE";
  private static final String XMLTAG_ALLOW_NULL = "allowNull";
  private static final String XMLTAG_CHECK = "check";

  public TaxationNumberPolicy()
    throws IDAException
  {
    super("TaxationNumber");
  }

  public String getTaxaNum()
  {
    return this.taxaNum;
  }

  public String getTaxaNumAllowNull()
  {
    return this.taxaNumAllowNull;
  }

  public void setTaxaNum(String paramString)
  {
    this.taxaNum = paramString;
  }

  public void setTaxaNumAllowNull(String paramString)
  {
    this.taxaNumAllowNull = paramString;
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
      if (this.taxaNum != null)
      {
        localElement2 = XMLTool.newChildElement(localElement1, "taxaNum");
        XMLTool.newChildElement(localElement2, "check", this.taxaNum.trim());
        XMLTool.newChildElement(localElement2, "allowNull", this.taxaNumAllowNull.trim());
      }
    }
    else
    {
      localElement1 = XMLTool.getElementByTagName(this.xmlElement, "value");
      if (localElement1 != null)
      {
        localElement2 = XMLTool.getElementByTagName(localElement1, "taxaNum");
        this.taxaNum = XMLTool.getValueByTagName(localElement2, "check").trim();
        this.taxaNumAllowNull = XMLTool.getValueByTagName(localElement2, "allowNull").trim();
      }
    }
  }
}

/* Location:           C:\Program Files\JIT\CA50\lib\IDA\ida.jar
 * Qualified Name:     cn.com.jit.ida.ca.ctml.x509v3.extension.policy.TaxationNumberPolicy
 * JD-Core Version:    0.6.0
 */