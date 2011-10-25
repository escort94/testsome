package cn.com.jit.ida.ca.ctml.x509v3;

import cn.com.jit.ida.IDAException;
import cn.com.jit.ida.ca.certmanager.reqinfo.CertInfo;
import cn.com.jit.ida.ca.ctml.CTML;
import cn.com.jit.ida.ca.ctml.CTMLInformation;
import cn.com.jit.ida.ca.ctml.CTMLPolicy;
import cn.com.jit.ida.ca.ctml.x509v3.extension.X509V3ExtensionPolicy;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

public class X509V3CTML extends CTML
{
  protected X509V3CTMLPolicy policy;
  protected X509V3ExtensionPolicy[] extensions;

  public X509V3CTML(CTMLInformation paramCTMLInformation)
    throws IDAException
  {
    super(paramCTMLInformation);
    CTMLPolicy localCTMLPolicy = this.ctmlInfo.getCTMLPolicy();
    this.policy = ((X509V3CTMLPolicy)localCTMLPolicy);
    Hashtable localHashtable = this.policy.getStandardExtensionPolicys();
    Vector localVector = this.policy.getSelfExtensionPolicys();
    int i = localHashtable.size() + localVector.size();
    this.extensions = new X509V3ExtensionPolicy[i];
    i = 0;
    Enumeration localEnumeration = localHashtable.elements();
    while (localEnumeration.hasMoreElements())
      this.extensions[(i++)] = ((X509V3ExtensionPolicy)localEnumeration.nextElement());
    localEnumeration = localVector.elements();
    while (localEnumeration.hasMoreElements())
      this.extensions[(i++)] = ((X509V3ExtensionPolicy)localEnumeration.nextElement());
  }

  public int getDefaultValidity()
  {
    return this.policy.getValidityPolicy().defaultValidity;
  }

  public int getMaxValidty()
  {
    return this.policy.getValidityPolicy().maxValidity;
  }

  public long getNotAfter()
  {
    return this.policy.getValidityPolicy().notAfter;
  }

  public String getKeyType()
  {
    return this.policy.getKeyPolicy().type;
  }

  public int getKeyLength()
  {
    return this.policy.getKeyPolicy().length;
  }

  public String getKeyGenPlace()
  {
    return this.policy.getKeyPolicy().genPlace;
  }

  public String getKeySpec()
  {
    return this.policy.getKeySpecPolicy().keySpec;
  }

  public String getUpdateReplace()
  {
    return this.policy.getUpdateReplacePolicy().updateReplace;
  }

  public String getUpdTransPeriod()
  {
    return this.policy.getUpdTransPeriodPolicy().updTransPeriod;
  }

  public boolean isIssue()
  {
    return this.policy.getIssuePolicy().isIssue;
  }

  public boolean isIssueToLDAP()
  {
    return this.policy.getIssuePolicy().issueMedium.contains("LDAP");
  }

  public boolean isIssueToDisk()
  {
    return this.policy.getIssuePolicy().issueMedium.contains("FILE");
  }

  public boolean isIssueCRL()
  {
    return true;
  }

  public int getExtensionCount()
  {
    return this.extensions.length;
  }

  public String getExtensionName(int paramInt)
  {
    return this.extensions[paramInt].getName();
  }

  public boolean isCrucialExtension(int paramInt)
  {
    return this.extensions[paramInt].isCrucial();
  }

  public String getUserProcessPolicy(int paramInt)
  {
    return this.extensions[paramInt].getUserProcessPolicy();
  }

  public boolean isStandard(int paramInt)
  {
    X509V3CTMLProcessor localX509V3CTMLProcessor = new X509V3CTMLProcessor();
    return localX509V3CTMLProcessor.isStandard(this.extensions[paramInt].getType());
  }

  public X509V3ExtensionPolicy getExtension(int paramInt)
  {
    return this.extensions[paramInt].copy();
  }

  public String getExtensionDesc(int paramInt)
  {
    return this.extensions[paramInt].getDescription();
  }

  public String getExtensionEncoding(int paramInt)
  {
    return this.extensions[paramInt].getEncoding();
  }

  public String getExtensionOID(int paramInt)
  {
    return this.extensions[paramInt].getOID();
  }

  public byte[] generateCertificate(CertInfo paramCertInfo)
    throws IDAException
  {
    X509V3CTMLProcessor localX509V3CTMLProcessor = new X509V3CTMLProcessor();
    localX509V3CTMLProcessor.setCTMLInfo(this);
    localX509V3CTMLProcessor.setCertInfo(paramCertInfo);
    return localX509V3CTMLProcessor.generateCertificate();
  }

  public boolean isCACTML()
  {
    return (this.policy.getAttribute().attribute & 1L) != 0L;
  }

  public Hashtable getStandardExt()
  {
    return this.policy.getStandardExtensionPolicys();
  }
}

/* Location:           C:\Program Files\JIT\CA50\lib\IDA\ida.jar
 * Qualified Name:     cn.com.jit.ida.ca.ctml.x509v3.X509V3CTML
 * JD-Core Version:    0.6.0
 */