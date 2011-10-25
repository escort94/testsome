package cn.com.jit.ida.ca.initserver;

import cn.com.jit.ida.IDAException;
import cn.com.jit.ida.ca.ctml.service.request.CTMLCreateRequest;
import cn.com.jit.ida.ca.ctml.x509v3.X509V3CTMLPolicy;
import cn.com.jit.ida.ca.ctml.x509v3.X509V3CTMLPolicy.ValidityPolicy;
import cn.com.jit.ida.ca.ctml.x509v3.extension.X509V3ExtensionPolicy;
import cn.com.jit.ida.ca.ctml.x509v3.extension.policy.ExtendKeyUsagePolicy;
import cn.com.jit.ida.ca.ctml.x509v3.extension.policy.KeyUsagePolicy;
import java.util.Hashtable;
import java.util.Vector;

public class AddOCSPCTML extends AddCTML
{
  protected void makeReq()
    throws IDAException
  {
    super.makeReq();
    this.request.setName("OCSP签名证书模板");
    this.request.setType("X509V3");
    this.request.setDescription("用于签发OCSP服务器的签名证书");
    X509V3CTMLPolicy localX509V3CTMLPolicy = null;
    try
    {
      localX509V3CTMLPolicy = new X509V3CTMLPolicy();
    }
    catch (IDAException localIDAException1)
    {
      throw new InitServerException(localIDAException1.getErrCode(), localIDAException1.getErrDesc(), localIDAException1);
    }
    localX509V3CTMLPolicy.getIssuePolicy().isIssue = false;
    Vector localVector1 = new Vector();
    localVector1.add("DB");
    localX509V3CTMLPolicy.getIssuePolicy().issueMedium = localVector1;
    localX509V3CTMLPolicy.getKeyPolicy().genPlace = "LOCAL";
    localX509V3CTMLPolicy.getKeyPolicy().length = 1024;
    localX509V3CTMLPolicy.getKeyPolicy().type = "RSA";
    localX509V3CTMLPolicy.getValidityPolicy().maxValidity = localX509V3CTMLPolicy.getValidityPolicy().maxValidity;
    localX509V3CTMLPolicy.getValidityPolicy().notAfter = 20491231235959000L;
    localX509V3CTMLPolicy.getAttribute();
    localX509V3CTMLPolicy.getAttribute().attribute = 0L;
    Vector localVector2 = new Vector();
    localX509V3CTMLPolicy.setSelfExtensionPolicys(localVector2);
    Hashtable localHashtable = new Hashtable();
    X509V3ExtensionPolicy localX509V3ExtensionPolicy1 = new X509V3ExtensionPolicy("AuthorityKeyIdentifier");
    localHashtable.put("AuthorityKeyIdentifier", localX509V3ExtensionPolicy1);
    X509V3ExtensionPolicy localX509V3ExtensionPolicy2 = new X509V3ExtensionPolicy("SubjectKeyIdentifier");
    localHashtable.put("SubjectKeyIdentifier", localX509V3ExtensionPolicy2);
    X509V3ExtensionPolicy localX509V3ExtensionPolicy3 = new X509V3ExtensionPolicy("CRLDistributionPoints");
    localHashtable.put("CRLDistributionPoints", localX509V3ExtensionPolicy3);
    KeyUsagePolicy localKeyUsagePolicy = new KeyUsagePolicy();
    localKeyUsagePolicy.addKeyUsage("digitalSignature");
    localKeyUsagePolicy.addKeyUsage("nonRepudiation");
    localHashtable.put("KeyUsage", localKeyUsagePolicy);
    ExtendKeyUsagePolicy localExtendKeyUsagePolicy = new ExtendKeyUsagePolicy();
    localExtendKeyUsagePolicy.addStandardKeyUsage("ocspSinging");
    localHashtable.put("ExtendKeyUsage", localExtendKeyUsagePolicy);
    localX509V3CTMLPolicy.setStandardExtensionPolicys(localHashtable);
    try
    {
      this.request.setPolicy(localX509V3CTMLPolicy.getCTMLPolicyDesc());
    }
    catch (IDAException localIDAException2)
    {
      throw new InitServerException(localIDAException2.getErrCode(), localIDAException2.getErrDesc(), localIDAException2);
    }
  }
}

/* Location:           C:\Program Files\JIT\CA50\lib\IDA\ida.jar
 * Qualified Name:     cn.com.jit.ida.ca.initserver.AddOCSPCTML
 * JD-Core Version:    0.6.0
 */