package cn.com.jit.ida.ca.ctml.x509v3.extension.policy;

import cn.com.jit.ida.IDAException;
import cn.com.jit.ida.ca.ctml.x509v3.extension.X509V3ExtensionPolicy;
import cn.com.jit.ida.util.xml.XMLTool;
import org.w3c.dom.Element;

public class OrganizationCodePolicy extends X509V3ExtensionPolicy
{
  private static String XMLTAG_ORG_CODE = "orgCode";
  private String orgCode = null;
  private String orgCodeAllowNull = "TRUE";
  private static final String XMLTAG_ALLOW_NULL = "allowNull";
  private static final String XMLTAG_CHECK = "check";

  public OrganizationCodePolicy()
    throws IDAException
  {
    super("OrganizationCode");
  }

  public String getOrgCode()
  {
    return this.orgCode;
  }

  public String getOrgCodeAllowNull()
  {
    return this.orgCodeAllowNull;
  }

  public void setOrgCode(String paramString)
  {
    this.orgCode = paramString;
  }

  public void setOrgCodeAllowNull(String paramString)
  {
    this.orgCodeAllowNull = paramString;
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
      if (this.orgCode != null)
      {
        localElement2 = XMLTool.newChildElement(localElement1, XMLTAG_ORG_CODE);
        XMLTool.newChildElement(localElement2, "check", this.orgCode.trim());
        XMLTool.newChildElement(localElement2, "allowNull", this.orgCodeAllowNull.trim());
      }
    }
    else
    {
      localElement1 = XMLTool.getElementByTagName(this.xmlElement, "value");
      if (localElement1 != null)
      {
        localElement2 = XMLTool.getElementByTagName(localElement1, XMLTAG_ORG_CODE);
        if (localElement2 != null)
        {
          this.orgCode = XMLTool.getValueByTagName(localElement2, "check").trim();
          this.orgCodeAllowNull = XMLTool.getValueByTagName(localElement2, "allowNull").trim();
        }
      }
    }
  }
}

/* Location:           C:\Program Files\JIT\CA50\lib\IDA\ida.jar
 * Qualified Name:     cn.com.jit.ida.ca.ctml.x509v3.extension.policy.OrganizationCodePolicy
 * JD-Core Version:    0.6.0
 */