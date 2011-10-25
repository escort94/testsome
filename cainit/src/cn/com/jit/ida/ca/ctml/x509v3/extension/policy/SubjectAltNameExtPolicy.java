package cn.com.jit.ida.ca.ctml.x509v3.extension.policy;

import cn.com.jit.ida.IDAException;
import cn.com.jit.ida.ca.ctml.x509v3.extension.X509V3ExtensionPolicy;
import cn.com.jit.ida.util.xml.XMLTool;
import org.w3c.dom.Element;

public class SubjectAltNameExtPolicy extends X509V3ExtensionPolicy
{
  private static final String XMLTAG_OTHERNAME = "otherName";
  private static final String XMLTAG_OTHERNAMEOID = "otherNameOid";
  private static final String XMLTAG_DNSNAME = "dnsName";
  private static final String XMLTAG_URINAME = "uriName";
  private static final String XMLTAG_DNAME = "dnName";
  private static final String XMLTAG_IPADDRESS = "ipAddress";
  private static final String XMLTAG_EMAIL = "email";
  private static final String XMLTAG_ALLOW_NULL = "allowNull";
  private static final String XMLTAG_CHECK = "check";
  private String otherNameOid;
  private String ipAddress;
  private String ipAddressAllowNull = "TRUE";
  private String email;
  private String emailAllowNull = "TRUE";
  private String otherName;
  private String otherNameAllowNull = "TRUE";
  private String dnsName;
  private String dnsNameAllowNull = "TRUE";
  private String uriName;
  private String uriNameAllowNull = "TRUE";
  private String dnName;
  private String dnNameAllowNull = "TRUE";
  public static final String OTHER_NAME_GUID_OID = "1.3.6.1.4.1.311.25.1";
  public static final String OTHER_NAME_UPN_OID = "1.3.6.1.4.1.311.20.2.3";

  public SubjectAltNameExtPolicy()
    throws IDAException
  {
    super("SubjectAltNameExt");
  }

  public String getDnName()
  {
    return this.dnName;
  }

  public void setDnName(String paramString)
  {
    this.dnName = paramString;
  }

  public void setOtherName(String paramString)
  {
    this.otherName = paramString;
  }

  public void setDnsName(String paramString)
  {
    this.dnsName = paramString;
  }

  public String getDnsName()
  {
    return this.dnsName;
  }

  public String getOtherName()
  {
    return this.otherName;
  }

  public String getOtherNameOid()
  {
    return this.otherNameOid;
  }

  public String getUriName()
  {
    return this.uriName;
  }

  public String getEmail()
  {
    return this.email;
  }

  public String getIpAddress()
  {
    return this.ipAddress;
  }

  public void setUriName(String paramString)
  {
    this.uriName = paramString;
  }

  public void setOtherNameOid(String paramString)
  {
    this.otherNameOid = paramString;
  }

  public void setEmail(String paramString)
  {
    this.email = paramString;
  }

  public void setIpAddress(String paramString)
  {
    this.ipAddress = paramString;
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
      if (this.otherNameOid != null)
      {
        localElement2 = this.xml.newElement("otherNameOid", this.otherNameOid);
        localElement1.appendChild(localElement2);
      }
      if (this.otherName != null)
      {
        localElement2 = XMLTool.newChildElement(localElement1, "otherName");
        XMLTool.newChildElement(localElement2, "check", this.otherName.trim());
        XMLTool.newChildElement(localElement2, "allowNull", this.otherNameAllowNull.trim());
      }
      if (this.dnsName != null)
      {
        localElement2 = XMLTool.newChildElement(localElement1, "dnsName");
        XMLTool.newChildElement(localElement2, "check", this.dnsName.trim());
        XMLTool.newChildElement(localElement2, "allowNull", this.dnsNameAllowNull.trim());
      }
      if (this.uriName != null)
      {
        localElement2 = XMLTool.newChildElement(localElement1, "uriName");
        XMLTool.newChildElement(localElement2, "check", this.uriName.trim());
        XMLTool.newChildElement(localElement2, "allowNull", this.uriNameAllowNull.trim());
      }
      if (this.dnName != null)
      {
        localElement2 = XMLTool.newChildElement(localElement1, "dnName");
        XMLTool.newChildElement(localElement2, "check", this.dnName.trim());
        XMLTool.newChildElement(localElement2, "allowNull", this.dnNameAllowNull.trim());
      }
      if (this.ipAddress != null)
      {
        localElement2 = XMLTool.newChildElement(localElement1, "ipAddress");
        XMLTool.newChildElement(localElement2, "check", this.ipAddress.trim());
        XMLTool.newChildElement(localElement2, "allowNull", this.ipAddressAllowNull.trim());
      }
      if (this.email != null)
      {
        localElement2 = XMLTool.newChildElement(localElement1, "email");
        XMLTool.newChildElement(localElement2, "check", this.email.trim());
        XMLTool.newChildElement(localElement2, "allowNull", this.emailAllowNull.trim());
      }
    }
    else
    {
      localElement1 = XMLTool.getElementByTagName(this.xmlElement, "value");
      if (localElement1 != null)
      {
        localElement2 = XMLTool.getElementByTagName(localElement1, "otherNameOid");
        if (localElement2 != null)
          this.otherNameOid = XMLTool.getValueByTagName(localElement1, "otherNameOid");
        Element localElement3 = XMLTool.getElementByTagName(localElement1, "otherName");
        if (localElement3 != null)
        {
          this.otherName = XMLTool.getValueByTagName(localElement3, "check").trim();
          this.otherNameAllowNull = XMLTool.getValueByTagName(localElement3, "allowNull").trim();
        }
        Element localElement4 = XMLTool.getElementByTagName(localElement1, "dnsName");
        if (localElement4 != null)
        {
          this.dnsName = XMLTool.getValueByTagName(localElement4, "check").trim();
          this.dnsNameAllowNull = XMLTool.getValueByTagName(localElement4, "allowNull").trim();
        }
        Element localElement5 = XMLTool.getElementByTagName(localElement1, "uriName");
        if (localElement5 != null)
        {
          this.uriName = XMLTool.getValueByTagName(localElement5, "check").trim();
          this.uriNameAllowNull = XMLTool.getValueByTagName(localElement5, "allowNull").trim();
        }
        Element localElement6 = XMLTool.getElementByTagName(localElement1, "dnName");
        if (localElement6 != null)
        {
          this.dnName = XMLTool.getValueByTagName(localElement6, "check").trim();
          this.dnNameAllowNull = XMLTool.getValueByTagName(localElement6, "allowNull").trim();
        }
        Element localElement7 = XMLTool.getElementByTagName(localElement1, "ipAddress");
        if (localElement7 != null)
        {
          this.ipAddress = XMLTool.getValueByTagName(localElement7, "check").trim();
          this.ipAddressAllowNull = XMLTool.getValueByTagName(localElement7, "allowNull").trim();
        }
        Element localElement8 = XMLTool.getElementByTagName(localElement1, "email");
        if (localElement8 != null)
        {
          this.email = XMLTool.getValueByTagName(localElement8, "check").trim();
          this.emailAllowNull = XMLTool.getValueByTagName(localElement8, "allowNull").trim();
        }
      }
    }
  }

  public String getDnNameAllowNull()
  {
    return this.dnNameAllowNull;
  }

  public String getDnsNameAllowNull()
  {
    return this.dnsNameAllowNull;
  }

  public String getEmailAllowNull()
  {
    return this.emailAllowNull;
  }

  public String getIpAddressAllowNull()
  {
    return this.ipAddressAllowNull;
  }

  public String getOtherNameAllowNull()
  {
    return this.otherNameAllowNull;
  }

  public String getUriNameAllowNull()
  {
    return this.uriNameAllowNull;
  }

  public void setUriNameAllowNull(String paramString)
  {
    this.uriNameAllowNull = paramString;
  }

  public void setOtherNameAllowNull(String paramString)
  {
    this.otherNameAllowNull = paramString;
  }

  public void setIpAddressAllowNull(String paramString)
  {
    this.ipAddressAllowNull = paramString;
  }

  public void setEmailAllowNull(String paramString)
  {
    this.emailAllowNull = paramString;
  }

  public void setDnsNameAllowNull(String paramString)
  {
    this.dnsNameAllowNull = paramString;
  }

  public void setDnNameAllowNull(String paramString)
  {
    this.dnNameAllowNull = paramString;
  }
}

/* Location:           C:\Program Files\JIT\CA50\lib\IDA\ida.jar
 * Qualified Name:     cn.com.jit.ida.ca.ctml.x509v3.extension.policy.SubjectAltNameExtPolicy
 * JD-Core Version:    0.6.0
 */