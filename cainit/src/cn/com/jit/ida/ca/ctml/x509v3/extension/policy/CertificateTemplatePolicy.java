package cn.com.jit.ida.ca.ctml.x509v3.extension.policy;

import cn.com.jit.ida.IDAException;
import cn.com.jit.ida.ca.ctml.x509v3.extension.X509V3ExtensionPolicy;
import cn.com.jit.ida.util.xml.XMLTool;
import org.w3c.dom.Element;

public class CertificateTemplatePolicy extends X509V3ExtensionPolicy
{
  private static final String XMLTAG_DOMAINCONTROLLER = "domainController";
  private static final String XMLTAG_SMARTCARDLOGONUSER = "smartCardLogonUser";
  private String domainController;
  private String smartCardLogonUser;
  public static final String DOMAINCONTROLLER = "domainController";
  public static final String SMARTCARDLOGONUSER = "smartCardLogonUser";

  public CertificateTemplatePolicy()
    throws IDAException
  {
    super("CertificateTemplate");
  }

  public String getDomainController()
  {
    return this.domainController;
  }

  public String getSmartCardLogonUser()
  {
    return this.smartCardLogonUser;
  }

  public void setDomainController(String paramString)
  {
    this.domainController = paramString;
  }

  public void setSmartCardLogonUser(String paramString)
  {
    this.smartCardLogonUser = paramString;
  }

  protected void updateData(boolean paramBoolean)
    throws IDAException
  {
    super.updateData(paramBoolean);
    Element localElement1;
    if (paramBoolean)
    {
      localElement1 = this.xml.newElement("value");
      this.xmlElement.appendChild(localElement1);
      Element localElement2 = this.xml.newElement("domainController", this.domainController);
      localElement1.appendChild(localElement2);
      Element localElement3 = this.xml.newElement("smartCardLogonUser", this.smartCardLogonUser);
      localElement1.appendChild(localElement3);
    }
    else
    {
      localElement1 = XMLTool.getElementByTagName(this.xmlElement, "value");
      if (localElement1 != null)
      {
        this.domainController = XMLTool.getValueByTagName(localElement1, "domainController");
        this.smartCardLogonUser = XMLTool.getValueByTagName(localElement1, "smartCardLogonUser");
      }
    }
  }
}

/* Location:           C:\Program Files\JIT\CA50\lib\IDA\ida.jar
 * Qualified Name:     cn.com.jit.ida.ca.ctml.x509v3.extension.policy.CertificateTemplatePolicy
 * JD-Core Version:    0.6.0
 */