package cn.com.jit.ida.ca.ctml.x509v3.extension;

import cn.com.jit.ida.IDAException;
import cn.com.jit.ida.ca.ctml.ExtensionPolicy;
import cn.com.jit.ida.ca.ctml.util.CTMLException;
import cn.com.jit.ida.ca.ctml.util.CTMLException.CTML;
import cn.com.jit.ida.ca.ctml.x509v3.extension.policy.BasicConstraintsPolicy;
import cn.com.jit.ida.ca.ctml.x509v3.extension.policy.CertificateTemplatePolicy;
import cn.com.jit.ida.ca.ctml.x509v3.extension.policy.ExtendKeyUsagePolicy;
import cn.com.jit.ida.ca.ctml.x509v3.extension.policy.ICRegistrationNumberPolicy;
import cn.com.jit.ida.ca.ctml.x509v3.extension.policy.IdentifyCodePolicy;
import cn.com.jit.ida.ca.ctml.x509v3.extension.policy.InsuranceNumberPolicy;
import cn.com.jit.ida.ca.ctml.x509v3.extension.policy.KeyUsagePolicy;
import cn.com.jit.ida.ca.ctml.x509v3.extension.policy.NetscapeCertTypePolicy;
import cn.com.jit.ida.ca.ctml.x509v3.extension.policy.OrganizationCodePolicy;
import cn.com.jit.ida.ca.ctml.x509v3.extension.policy.PolicyConstraintsPolicy;
import cn.com.jit.ida.ca.ctml.x509v3.extension.policy.PolicyMappingsPolicy;
import cn.com.jit.ida.ca.ctml.x509v3.extension.policy.SelfExtensionPolicy;
import cn.com.jit.ida.ca.ctml.x509v3.extension.policy.SubjectAltNameExtPolicy;
import cn.com.jit.ida.ca.ctml.x509v3.extension.policy.TaxationNumberPolicy;
import cn.com.jit.ida.util.xml.XMLTool;
import java.io.PrintStream;
import org.w3c.dom.Element;

public class X509V3ExtensionPolicy extends ExtensionPolicy
{
  protected String name;
  protected boolean crucial = false;
  protected String userProcessPolicy;
  protected String type;
  protected XMLTool xml;
  protected Element xmlElement;
  protected String description = "";
  protected String oid = "";
  protected String encoding = "Standard";
  protected static final String XMLTAG_NAME = "name";
  protected static final String XMLTAG_USERPROCESS = "userprocess";
  protected static final String XMLTAG_CRUCIAL = "crucial";
  protected static final String XMLTAG_VALUE = "value";
  private static final String[][] typeList = { { "userDefineExtension" }, { "AuthorityInformationAccess", "签发机构信息访问", "1.3.6.1.5.5.7.1.1" }, { "AuthorityKeyIdentifier", "签发机构密钥标志符", "2.5.29.35" }, { "BasicConstraints", "基本限制", "2.5.29.19" }, { "CertificatePolicies", "证书策略", "2.5.29.32" }, { "NetscapeCertType", "Netscape证书类型", "2.16.840.1.113730.1.1" }, { "CRLDistributionPoints", "CRL发布点", "2.5.29.31" }, { "ExtendKeyUsage", "增强型密钥用法", "2.5.29.37" }, { "KeyUsage", "密钥用法", "2.5.29.15" }, { "SubjectKeyIdentifier", "主题密钥标志符", "2.5.29.14" }, { "SubjectAltNameExt", "主题备用名称", "2.5.29.17" }, { "CertificateTemplate", "证书模板名称", "1.3.6.1.4.1.311.20.2" }, { "IdentifyCode", "身份标识码", "1.2.86.11.7.1" }, { "InsuranceNumber", "个人社会保险号", "1.2.86.11.7.2" }, { "OrganizationCode", "企业组织机构代码", "1.2.86.11.7.3" }, { "ICRegistrationNumber", "企业工商注册号", "1.2.86.11.7.4" }, { "TaxationNumber", "企业税号", "1.2.86.11.7.5" }, { "PolicyConstrants", "策略限制", "2.5.29.36" }, { "PolicyMappings", "策略映射", "2.5.29.33" } };

  public X509V3ExtensionPolicy(String paramString)
    throws IDAException
  {
    for (int i = 0; i < typeList.length; i++)
    {
      if (!typeList[i][0].equalsIgnoreCase(paramString))
        continue;
      this.type = typeList[i][0];
      if (i <= 0)
        break;
      this.name = typeList[i][1];
      this.description = this.name;
      this.oid = typeList[i][2];
      this.encoding = "Standard";
      break;
    }
    if (i >= typeList.length)
    {
      CTMLException localCTMLException = new CTMLException(CTMLException.CTML.INVALIDEXTENSIONTYPE);
      localCTMLException.appendMsg(paramString);
      throw localCTMLException;
    }
    this.userProcessPolicy = new String("DENY");
    this.xml = new XMLTool();
  }

  public final boolean isCrucial()
  {
    return this.crucial;
  }

  public final String getName()
  {
    return this.name;
  }

  public final String getUserProcessPolicy()
  {
    return this.userProcessPolicy;
  }

  public final String getType()
  {
    return this.type;
  }

  public final String getDescription()
  {
    return this.description;
  }

  public final String getEncoding()
  {
    return this.encoding;
  }

  public final String getOID()
  {
    return this.oid;
  }

  public void setUserProcessPolicy(String paramString)
    throws IDAException
  {
    if ((!paramString.equals("ALLOW")) && (!paramString.equals("DENY")) && (!paramString.equals("NEED")))
    {
      CTMLException localCTMLException = new CTMLException(CTMLException.CTML.INVALID_EXTENSION_USERPROCESSPOLICY);
      localCTMLException.appendMsg(paramString);
      throw localCTMLException;
    }
    this.userProcessPolicy = paramString;
  }

  public void setCrucial(boolean paramBoolean)
  {
    this.crucial = paramBoolean;
  }

  public final Element getXMLOjbect()
    throws IDAException
  {
    updateData(true);
    return this.xmlElement;
  }

  public void setXMLObject(Element paramElement)
    throws IDAException
  {
    this.xmlElement = this.xml.importElement(paramElement);
    updateData(false);
  }

  protected void updateData(boolean paramBoolean)
    throws IDAException
  {
    if (paramBoolean)
    {
      this.xmlElement = this.xml.newElement(this.type);
      Element localElement1 = this.xml.newElement("name", this.name);
      this.xmlElement.appendChild(localElement1);
      Element localElement2 = this.xml.newElement("crucial", new Boolean(this.crucial).toString());
      this.xmlElement.appendChild(localElement2);
      Element localElement3 = this.xml.newElement("userprocess", this.userProcessPolicy);
      this.xmlElement.appendChild(localElement3);
    }
    else
    {
      this.name = XMLTool.getValueByTagName(this.xmlElement, "name");
      this.crucial = new Boolean(XMLTool.getValueByTagName(this.xmlElement, "crucial")).booleanValue();
      this.userProcessPolicy = XMLTool.getValueByTagName(this.xmlElement, "userprocess");
    }
  }

  public static boolean isSupport(String paramString)
  {
    for (int i = 1; i < typeList.length; i++)
      if (typeList[i][0].equalsIgnoreCase(paramString))
        return true;
    return false;
  }

  public static X509V3ExtensionPolicy newInstance(String paramString)
    throws IDAException
  {
    if (paramString.equalsIgnoreCase("BasicConstraints"))
      return new BasicConstraintsPolicy();
    if (paramString.equalsIgnoreCase("NetscapeCertType"))
      return new NetscapeCertTypePolicy();
    if (paramString.equalsIgnoreCase("KeyUsage"))
      return new KeyUsagePolicy();
    if (paramString.equalsIgnoreCase("ExtendKeyUsage"))
      return new ExtendKeyUsagePolicy();
    if (paramString.equalsIgnoreCase("SubjectAltNameExt"))
      return new SubjectAltNameExtPolicy();
    if (paramString.equalsIgnoreCase("CertificateTemplate"))
      return new CertificateTemplatePolicy();
    if (paramString.equalsIgnoreCase("userDefineExtension"))
      return new SelfExtensionPolicy();
    if (paramString.equalsIgnoreCase("IdentifyCode"))
      return new IdentifyCodePolicy();
    if (paramString.equalsIgnoreCase("InsuranceNumber"))
      return new InsuranceNumberPolicy();
    if (paramString.equalsIgnoreCase("OrganizationCode"))
      return new OrganizationCodePolicy();
    if (paramString.equalsIgnoreCase("ICRegistrationNumber"))
      return new ICRegistrationNumberPolicy();
    if (paramString.equalsIgnoreCase("TaxationNumber"))
      return new TaxationNumberPolicy();
    if (paramString.equalsIgnoreCase("PolicyConstrants"))
      return new PolicyConstraintsPolicy();
    if (paramString.equalsIgnoreCase("PolicyMappings"))
      return new PolicyMappingsPolicy();
    return new X509V3ExtensionPolicy(paramString);
  }

  public X509V3ExtensionPolicy copy()
  {
    Object localObject = null;
    try
    {
      if (this.type.equalsIgnoreCase("BasicConstraints"))
        localObject = new BasicConstraintsPolicy();
      else if (this.type.equalsIgnoreCase("NetscapeCertType"))
        localObject = new NetscapeCertTypePolicy();
      else if (this.type.equalsIgnoreCase("KeyUsage"))
        localObject = new KeyUsagePolicy();
      else if (this.type.equalsIgnoreCase("SubjectAltNameExt"))
        localObject = new SubjectAltNameExtPolicy();
      else if (this.type.equalsIgnoreCase("CertificateTemplate"))
        localObject = new CertificateTemplatePolicy();
      else if (this.type.equalsIgnoreCase("ExtendKeyUsage"))
        localObject = new ExtendKeyUsagePolicy();
      else if (this.type.equalsIgnoreCase("userDefineExtension"))
        localObject = new SelfExtensionPolicy();
      else if (this.type.equalsIgnoreCase("InsuranceNumber"))
        localObject = new InsuranceNumberPolicy();
      else if (this.type.equalsIgnoreCase("IdentifyCode"))
        localObject = new IdentifyCodePolicy();
      else if (this.type.equalsIgnoreCase("TaxationNumber"))
        localObject = new TaxationNumberPolicy();
      else if (this.type.equalsIgnoreCase("OrganizationCode"))
        localObject = new OrganizationCodePolicy();
      else if (this.type.equalsIgnoreCase("ICRegistrationNumber"))
        localObject = new ICRegistrationNumberPolicy();
      else
        localObject = new X509V3ExtensionPolicy(this.type);
      ((X509V3ExtensionPolicy)localObject).name = this.name;
      ((X509V3ExtensionPolicy)localObject).type = this.type;
      ((X509V3ExtensionPolicy)localObject).crucial = this.crucial;
      ((X509V3ExtensionPolicy)localObject).userProcessPolicy = this.userProcessPolicy;
      ((X509V3ExtensionPolicy)localObject).setXMLObject(this.xmlElement);
    }
    catch (IDAException localIDAException)
    {
      System.err.println(localIDAException);
      throw new RuntimeException(localIDAException);
    }
    return (X509V3ExtensionPolicy)localObject;
  }

  protected static String getExtensionDescName(String paramString)
  {
    for (int i = 1; i < typeList.length; i++)
      if (paramString.equalsIgnoreCase(typeList[i][0]))
        return typeList[i][1];
    return "未知扩展域";
  }
}

/* Location:           C:\Program Files\JIT\CA50\lib\IDA\ida.jar
 * Qualified Name:     cn.com.jit.ida.ca.ctml.x509v3.extension.X509V3ExtensionPolicy
 * JD-Core Version:    0.6.0
 */