package cn.com.jit.ida.ca.ctml.x509v3;

import cn.com.jit.ida.IDAException;
import cn.com.jit.ida.ca.certmanager.reqinfo.CertInfo;
import cn.com.jit.ida.ca.config.CAConfig;
import cn.com.jit.ida.ca.config.CRLConfig;
import cn.com.jit.ida.ca.config.CryptoConfig;
import cn.com.jit.ida.ca.config.GlobalConfig;
import cn.com.jit.ida.ca.config.InternalConfig;
import cn.com.jit.ida.ca.ctml.CTMLManager;
import cn.com.jit.ida.ca.ctml.CTMLProcessor;
import cn.com.jit.ida.ca.ctml.util.CTMLException;
import cn.com.jit.ida.ca.ctml.util.CTMLException.PROCESSOR;
import cn.com.jit.ida.ca.ctml.x509v3.extension.StandardExtension;
import cn.com.jit.ida.ca.ctml.x509v3.extension.X509V3ExtensionPolicy;
import cn.com.jit.ida.ca.ctml.x509v3.extension.policy.BasicConstraintsPolicy;
import cn.com.jit.ida.ca.ctml.x509v3.extension.policy.CertificateTemplatePolicy;
import cn.com.jit.ida.ca.ctml.x509v3.extension.policy.ExtendKeyUsagePolicy;
import cn.com.jit.ida.ca.ctml.x509v3.extension.policy.ICRegistrationNumberPolicy;
import cn.com.jit.ida.ca.ctml.x509v3.extension.policy.IdentifyCodePolicy;
import cn.com.jit.ida.ca.ctml.x509v3.extension.policy.InsuranceNumberPolicy;
import cn.com.jit.ida.ca.ctml.x509v3.extension.policy.KeyUsagePolicy;
import cn.com.jit.ida.ca.ctml.x509v3.extension.policy.NetscapeCertTypePolicy;
import cn.com.jit.ida.ca.ctml.x509v3.extension.policy.OrganizationCodePolicy;
import cn.com.jit.ida.ca.ctml.x509v3.extension.policy.SelfExtensionPolicy;
import cn.com.jit.ida.ca.ctml.x509v3.extension.policy.SubjectAltNameExtPolicy;
import cn.com.jit.ida.ca.ctml.x509v3.extension.policy.TaxationNumberPolicy;
import cn.com.jit.ida.util.pki.PKIException;
import cn.com.jit.ida.util.pki.asn1.x509.X509Name;
import cn.com.jit.ida.util.pki.cert.X509Cert;
import cn.com.jit.ida.util.pki.cert.X509CertGenerator;
import cn.com.jit.ida.util.pki.cipher.JCrypto;
import cn.com.jit.ida.util.pki.cipher.JKey;
import cn.com.jit.ida.util.pki.cipher.JKeyPair;
import cn.com.jit.ida.util.pki.cipher.Session;
import cn.com.jit.ida.util.pki.extension.AccessDescriptionExt;
import cn.com.jit.ida.util.pki.extension.AuthorityInformationAccessExt;
import cn.com.jit.ida.util.pki.extension.AuthorityKeyIdentifierExt;
import cn.com.jit.ida.util.pki.extension.BasicConstraintsExt;
import cn.com.jit.ida.util.pki.extension.CRLDistPointExt;
import cn.com.jit.ida.util.pki.extension.CertificatePoliciesExt;
import cn.com.jit.ida.util.pki.extension.CertificateTemplateExt;
import cn.com.jit.ida.util.pki.extension.DistributionPointExt;
import cn.com.jit.ida.util.pki.extension.ExtendedKeyUsageExt;
import cn.com.jit.ida.util.pki.extension.Extension;
import cn.com.jit.ida.util.pki.extension.ICRegistrationNumberExt;
import cn.com.jit.ida.util.pki.extension.IdentifyCodeExt;
import cn.com.jit.ida.util.pki.extension.InsuranceNumberExt;
import cn.com.jit.ida.util.pki.extension.KeyUsageExt;
import cn.com.jit.ida.util.pki.extension.NetscapeCertTypeExt;
import cn.com.jit.ida.util.pki.extension.OrganizationCodeExt;
import cn.com.jit.ida.util.pki.extension.PolicyConstraintsExt;
import cn.com.jit.ida.util.pki.extension.PolicyInformationExt;
import cn.com.jit.ida.util.pki.extension.PolicyMappingsExt;
import cn.com.jit.ida.util.pki.extension.SelfDefExtension;
import cn.com.jit.ida.util.pki.extension.SubjectAltNameExt;
import cn.com.jit.ida.util.pki.extension.SubjectKeyIdentifierExt;
import cn.com.jit.ida.util.pki.extension.TaxationNumberExt;
import java.math.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Properties;
import java.util.Vector;

public class X509V3CTMLProcessor
  implements CTMLProcessor
{
  private final HashMap EXTENSIONS = new HashMap();
  private X509CertGenerator generator;
  private JKeyPair caKeyPair;
  private Session systemSession;
  private Session innerSession;
  private GlobalConfig config;
  private JCrypto crypto;
  private CertInfo certInfo;
  private X509V3CTML ctml;
  private String subject;
  private X509Name issuer;
  private BigInteger serialNumber;
  private Date notBefore;
  private Date notAfter;
  private JKey pubKey;
  private String signAlgo;
  private Vector extensions;
  private byte[] certificate;

  public X509V3CTMLProcessor()
  {
    this.EXTENSIONS.put("KeyUsage", new KeyUsageProcessor());
    this.EXTENSIONS.put("ExtendKeyUsage", new ExKeyUsageProcessor());
    this.EXTENSIONS.put("NetscapeCertType", new CertTypeProcessor());
    this.EXTENSIONS.put("BasicConstraints", new BasicConstraintsProcessor());
    this.EXTENSIONS.put("AuthorityInformationAccess", new AuthInfoAccessProcessor());
    this.EXTENSIONS.put("AuthorityKeyIdentifier", new AuthorityKeyIDProcessor());
    this.EXTENSIONS.put("CertificatePolicies", new CertPolicyProcessor());
    this.EXTENSIONS.put("CRLDistributionPoints", new CRLDPProcessor());
    this.EXTENSIONS.put("SubjectKeyIdentifier", new SubjectKeyIDProcessor());
    this.EXTENSIONS.put("CertificateTemplate", new CertificateTemplateProcess());
    this.EXTENSIONS.put("SubjectAltNameExt", new SubjectAltNameExtProcess());
    this.EXTENSIONS.put("IdentifyCode", new IdentifyCodeProcessor());
    this.EXTENSIONS.put("ICRegistrationNumber", new ICRegistrationNumberProcessor());
    this.EXTENSIONS.put("OrganizationCode", new OrganizationCodeProcessor());
    this.EXTENSIONS.put("TaxationNumber", new TaxationNumberProcessor());
    this.EXTENSIONS.put("InsuranceNumber", new InsuranceNumberProcessor());
    this.EXTENSIONS.put("PolicyConstrants", new PolicyConstraintsProcessor());
    this.EXTENSIONS.put("PolicyMappings", new PolicyMappingsProcessor());
    this.generator = new X509CertGenerator();
  }

  public void setCTMLInfo(X509V3CTML paramX509V3CTML)
  {
    this.ctml = paramX509V3CTML;
  }

  public void setCertInfo(CertInfo paramCertInfo)
  {
    this.certInfo = paramCertInfo;
  }

  public byte[] generateCertificate()
    throws IDAException
  {
    prepareSystemInfo();
    prepareCertBasicInfo();
    prepareCertExtensions();
    try
    {
      this.generator.setSerialNumber(this.serialNumber);
      this.generator.setSubject(this.subject);
      this.generator.setIssuer(this.issuer);
      this.generator.setPublicKey(this.pubKey);
      this.generator.setNotAfter(this.notAfter);
      this.generator.setNotBefore(this.notBefore);
      this.generator.setSignatureAlg(this.signAlgo);
      this.generator.setExtensiond(this.extensions);
      this.certificate = this.generator.generateX509Cert(this.caKeyPair.getPrivateKey(), this.systemSession);
    }
    catch (PKIException localPKIException)
    {
      CTMLException localCTMLException = new CTMLException(CTMLException.PROCESSOR.GENCERTFAIL, localPKIException);
      throw localCTMLException;
    }
    updateCTMLStatus();
    return this.certificate;
  }

  private void prepareSystemInfo()
  {
    try
    {
      this.config = GlobalConfig.getInstance();
      CAConfig localCAConfig = this.config.getCAConfig();
      CryptoConfig localCryptoConfig = this.config.getCryptoConfig();
      InternalConfig localInternalConfig = this.config.getInternalConfig();
      JKey localJKey1 = localCAConfig.getPrivateKey();
      JKey localJKey2 = localCAConfig.getRootCert().getPublicKey();
      this.caKeyPair = new JKeyPair(localJKey2, localJKey1);
      this.crypto = JCrypto.getInstance();
      String str = localCryptoConfig.getDeviceID();
      this.systemSession = this.crypto.openSession(str);
      this.innerSession = this.crypto.openSession("JSOFT_LIB");
      this.signAlgo = localCAConfig.getCASignAlg();
      this.issuer = localCAConfig.getRootCert().getX509NameSubject();
      localCAConfig.getAuthorityInformation();
    }
    catch (Exception localException)
    {
      throw new RuntimeException(localException);
    }
  }

  private void prepareCertBasicInfo()
    throws IDAException
  {
    this.subject = this.certInfo.getSubject();
    try
    {
      this.serialNumber = new BigInteger(this.certInfo.getCertSN(), 16);
    }
    catch (NumberFormatException localNumberFormatException)
    {
      CTMLException localCTMLException1 = new CTMLException(CTMLException.PROCESSOR.INVALID_SERIALNUMBER, localNumberFormatException);
      localCTMLException1.appendMsg(this.certInfo.getCertSN());
      throw localCTMLException1;
    }
    SimpleDateFormat localSimpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmssSSS");
    CTMLException localCTMLException2;
    try
    {
      this.notBefore = localSimpleDateFormat.parse(Long.toString(this.certInfo.getNotBefore()));
    }
    catch (ParseException localParseException1)
    {
      localCTMLException2 = new CTMLException(CTMLException.PROCESSOR.INVALID_NOTBEFORE, localParseException1);
      localCTMLException2.appendMsg(Long.toString(this.certInfo.getNotBefore()));
      throw localCTMLException2;
    }
    try
    {
      this.notAfter = localSimpleDateFormat.parse(Long.toString(this.certInfo.getNotAfter()));
    }
    catch (ParseException localParseException2)
    {
      localCTMLException2 = new CTMLException(CTMLException.PROCESSOR.INVALID_NOTAFTER, localParseException2);
      localCTMLException2.appendMsg(Long.toString(this.certInfo.getNotAfter()));
      throw localCTMLException2;
    }
    this.pubKey = this.certInfo.getPubKey();
  }

  private void prepareCertExtensions()
    throws IDAException
  {
    this.extensions = new Vector();
    for (int i = 0; i < this.ctml.getExtensionCount(); i++)
    {
      Extension localExtension = newExtension(this.ctml.getExtension(i));
      if (localExtension == null)
        continue;
      this.extensions.add(localExtension);
    }
  }

  private void updateCTMLStatus()
    throws IDAException
  {
    if ("USING".equals(this.ctml.getCTMLStatus()))
      return;
    Object localObject;
    if ("UNUSED".equals(this.ctml.getCTMLStatus()))
    {
      localObject = CTMLManager.getInstance();
      ((CTMLManager)localObject).updateCTMLStatus(this.ctml.getCTMLName(), "USING");
    }
    else
    {
      localObject = new CTMLException(CTMLException.PROCESSOR.INVALID_CTMLSTATUS);
      ((CTMLException)localObject).appendMsg(this.ctml.getCTMLStatus());
      throw ((Throwable)localObject);
    }
  }

  private Extension newExtension(X509V3ExtensionPolicy paramX509V3ExtensionPolicy)
    throws IDAException
  {
    Object localObject1 = null;
    try
    {
      String str = paramX509V3ExtensionPolicy.getType();
      if (this.EXTENSIONS.containsKey(str))
      {
        localObject1 = (Processor)this.EXTENSIONS.get(str);
        ((Processor)localObject1).process(paramX509V3ExtensionPolicy);
        localObject2 = ((Processor)localObject1).getExtension();
        if (localObject2 == null)
          return null;
        ((Extension)localObject2).setCritical(paramX509V3ExtensionPolicy.isCrucial());
        return localObject2;
      }
      if (str.equals("userDefineExtension"))
      {
        localObject1 = new SelfExtProcessor();
        ((Processor)localObject1).process(paramX509V3ExtensionPolicy);
        localObject2 = ((Processor)localObject1).getExtension();
        if (localObject2 == null)
          return null;
        ((Extension)localObject2).setCritical(paramX509V3ExtensionPolicy.isCrucial());
        return localObject2;
      }
    }
    catch (Exception localException)
    {
      Object localObject2 = new CTMLException(CTMLException.PROCESSOR.PROCESSEXTENSIONFAIL, localException);
      ((CTMLException)localObject2).appendMsg(paramX509V3ExtensionPolicy.getType());
      throw ((Throwable)localObject2);
    }
    return (Extension)(Extension)null;
  }

  private boolean isNull(String paramString)
  {
    return (paramString == null) || (paramString.equals(""));
  }

  public boolean isStandard(String paramString)
  {
    return this.EXTENSIONS.containsKey(paramString);
  }

  class PolicyMappingsProcessor extends X509V3CTMLProcessor.Processor
  {
    public PolicyMappingsProcessor()
    {
      super();
      this.extension = new PolicyMappingsExt();
    }

    protected void process(X509V3ExtensionPolicy paramX509V3ExtensionPolicy)
      throws PKIException
    {
      Hashtable localHashtable = X509V3CTMLProcessor.this.certInfo.getStandardExtensionsHT();
      if ((localHashtable != null) && (localHashtable.size() > 0))
      {
        if (localHashtable.get("PolicyMappings") != null)
        {
          Vector localVector = (Vector)localHashtable.get("PolicyMappings");
          Object localObject1 = (StandardExtension)localVector.get(0);
          Object localObject2 = (StandardExtension)localVector.get(1);
          String str1 = ((StandardExtension)localObject1).getChildName();
          if (!str1.equals("issuerDomainPolicy"))
          {
            localObject3 = new StandardExtension();
            localObject3 = localObject1;
            localObject1 = localObject2;
            localObject2 = localObject3;
          }
          Object localObject3 = ((StandardExtension)localObject1).getStandardValue();
          String str2 = ((StandardExtension)localObject2).getStandardValue();
          if ((localObject3 != null) && (!((String)localObject3).equals("")) && (str2 != null) && (!str2.equals("")))
          {
            String[] arrayOfString1 = ((String)localObject3).trim().split("[;]");
            String[] arrayOfString2 = str2.trim().split("[;]");
            if ((arrayOfString1 == null) || (arrayOfString1.length == 0) || (arrayOfString2 == null) || (arrayOfString2.length == 0))
            {
              this.extension = null;
            }
            else
            {
              for (int i = 0; i < arrayOfString1.length; i++)
              {
                String str3 = arrayOfString1[i];
                String str4 = arrayOfString2[i];
                if ((str3.equals("")) || (str4.equals("")))
                  continue;
                ((PolicyMappingsExt)this.extension).addPolicyMapping(str3, str4);
              }
              if (((PolicyMappingsExt)this.extension).getPolicyMappingCount() == 0)
                this.extension = null;
            }
          }
          else
          {
            this.extension = null;
          }
        }
        else
        {
          this.extension = null;
        }
      }
      else
        this.extension = null;
    }
  }

  class PolicyConstraintsProcessor extends X509V3CTMLProcessor.Processor
  {
    public PolicyConstraintsProcessor()
    {
      super();
      this.extension = new PolicyConstraintsExt(-1, -1);
    }

    protected void process(X509V3ExtensionPolicy paramX509V3ExtensionPolicy)
      throws PKIException
    {
      Hashtable localHashtable = X509V3CTMLProcessor.this.certInfo.getStandardExtensionsHT();
      String str1 = null;
      String str2 = null;
      if ((localHashtable != null) && (localHashtable.size() > 0))
      {
        if (localHashtable.get("PolicyConstrants") != null)
        {
          Vector localVector = (Vector)localHashtable.get("PolicyConstrants");
          StandardExtension localStandardExtension;
          Object localObject;
          if (localVector.size() == 1)
          {
            localStandardExtension = (StandardExtension)localVector.get(0);
            localObject = localStandardExtension.getChildName();
            if (((String)localObject).equals("inhibitPolicyMapping"))
            {
              if ((localStandardExtension.getStandardValue() != null) && (!localStandardExtension.getStandardValue().trim().equals("")))
                str1 = localStandardExtension.getStandardValue().trim();
            }
            else if ((localStandardExtension.getStandardValue() != null) && (!localStandardExtension.getStandardValue().trim().equals("")))
              str2 = localStandardExtension.getStandardValue().trim();
          }
          else
          {
            localStandardExtension = (StandardExtension)localVector.get(0);
            localObject = (StandardExtension)localVector.get(1);
            String str3 = localStandardExtension.getChildName();
            if (str3.equals("inhibitPolicyMapping"))
            {
              if ((localStandardExtension.getStandardValue() != null) && (!localStandardExtension.getStandardValue().trim().equals("")))
                str1 = localStandardExtension.getStandardValue().trim();
              if ((((StandardExtension)localObject).getStandardValue() != null) && (!((StandardExtension)localObject).getStandardValue().trim().equals("")))
                str2 = ((StandardExtension)localObject).getStandardValue().trim();
            }
            else
            {
              if ((localStandardExtension.getStandardValue() != null) && (!localStandardExtension.getStandardValue().trim().equals("")))
                str2 = localStandardExtension.getStandardValue().trim();
              if ((((StandardExtension)localObject).getStandardValue() != null) && (!((StandardExtension)localObject).getStandardValue().trim().equals("")))
                str1 = ((StandardExtension)localObject).getStandardValue().trim();
            }
          }
          if ((str2 == null) && (str1 == null))
          {
            this.extension = null;
          }
          else
          {
            int i = -1;
            int j = -1;
            if (str2 != null)
              j = Integer.parseInt(str2);
            if (str1 != null)
              i = Integer.parseInt(str1);
            this.extension = new PolicyConstraintsExt(i, j);
          }
        }
        else
        {
          this.extension = null;
        }
      }
      else
        this.extension = null;
    }
  }

  class TaxationNumberProcessor extends X509V3CTMLProcessor.Processor
  {
    TaxationNumberPolicy taxationNumPolicy = null;

    public TaxationNumberProcessor()
    {
      super();
      this.extension = new TaxationNumberExt();
    }

    protected void process(X509V3ExtensionPolicy paramX509V3ExtensionPolicy)
      throws PKIException
    {
      Hashtable localHashtable = X509V3CTMLProcessor.this.certInfo.getStandardExtensionsHT();
      if ((localHashtable != null) && (localHashtable.size() > 0))
      {
        if (localHashtable.get("TaxationNumber") != null)
        {
          Vector localVector = (Vector)localHashtable.get("TaxationNumber");
          StandardExtension localStandardExtension = (StandardExtension)localVector.get(0);
          String str = localStandardExtension.getStandardValue();
          if ((str != null) && (!str.trim().equalsIgnoreCase("")))
            ((TaxationNumberExt)this.extension).SetTaxationNumber(str);
          else
            this.extension = null;
        }
        else
        {
          this.extension = null;
        }
      }
      else
        this.extension = null;
    }
  }

  class OrganizationCodeProcessor extends X509V3CTMLProcessor.Processor
  {
    OrganizationCodePolicy orgCodePolicy = null;

    public OrganizationCodeProcessor()
    {
      super();
      this.extension = new OrganizationCodeExt();
    }

    protected void process(X509V3ExtensionPolicy paramX509V3ExtensionPolicy)
      throws PKIException
    {
      Hashtable localHashtable = X509V3CTMLProcessor.this.certInfo.getStandardExtensionsHT();
      if ((localHashtable != null) && (localHashtable.size() > 0))
      {
        if (localHashtable.get("OrganizationCode") != null)
        {
          Vector localVector = (Vector)localHashtable.get("OrganizationCode");
          StandardExtension localStandardExtension = (StandardExtension)localVector.get(0);
          String str = localStandardExtension.getStandardValue();
          if ((str != null) && (!str.trim().equalsIgnoreCase("")))
            ((OrganizationCodeExt)this.extension).SetOrganizationCode(str);
          else
            this.extension = null;
        }
        else
        {
          this.extension = null;
        }
      }
      else
        this.extension = null;
    }
  }

  class ICRegistrationNumberProcessor extends X509V3CTMLProcessor.Processor
  {
    ICRegistrationNumberPolicy icRegistNumPolicy = null;

    public ICRegistrationNumberProcessor()
    {
      super();
      this.extension = new ICRegistrationNumberExt();
    }

    protected void process(X509V3ExtensionPolicy paramX509V3ExtensionPolicy)
      throws PKIException
    {
      Hashtable localHashtable = X509V3CTMLProcessor.this.certInfo.getStandardExtensionsHT();
      if ((localHashtable != null) && (localHashtable.size() > 0))
      {
        if (localHashtable.get("ICRegistrationNumber") != null)
        {
          Vector localVector = (Vector)localHashtable.get("ICRegistrationNumber");
          StandardExtension localStandardExtension = (StandardExtension)localVector.get(0);
          String str = localStandardExtension.getStandardValue();
          if ((str != null) && (!str.trim().equalsIgnoreCase("")))
            ((ICRegistrationNumberExt)this.extension).SetICRegistationNumber(str);
          else
            this.extension = null;
        }
        else
        {
          this.extension = null;
        }
      }
      else
        this.extension = null;
    }
  }

  class InsuranceNumberProcessor extends X509V3CTMLProcessor.Processor
  {
    InsuranceNumberPolicy insuranceNumPolicy = null;

    public InsuranceNumberProcessor()
    {
      super();
      this.extension = new InsuranceNumberExt();
    }

    protected void process(X509V3ExtensionPolicy paramX509V3ExtensionPolicy)
      throws PKIException
    {
      Hashtable localHashtable = X509V3CTMLProcessor.this.certInfo.getStandardExtensionsHT();
      if ((localHashtable != null) && (localHashtable.size() > 0))
      {
        if (localHashtable.get("InsuranceNumber") != null)
        {
          Vector localVector = (Vector)localHashtable.get("InsuranceNumber");
          StandardExtension localStandardExtension = (StandardExtension)localVector.get(0);
          String str = localStandardExtension.getStandardValue();
          if ((str != null) && (!str.trim().equalsIgnoreCase("")))
            ((InsuranceNumberExt)this.extension).SetInsuranceNumber(str);
          else
            this.extension = null;
        }
        else
        {
          this.extension = null;
        }
      }
      else
        this.extension = null;
    }
  }

  class IdentifyCodeProcessor extends X509V3CTMLProcessor.Processor
  {
    IdentifyCodePolicy idCodePolicy = null;

    public IdentifyCodeProcessor()
    {
      super();
      this.extension = new IdentifyCodeExt();
    }

    protected void process(X509V3ExtensionPolicy paramX509V3ExtensionPolicy)
      throws PKIException
    {
      Hashtable localHashtable = X509V3CTMLProcessor.this.certInfo.getStandardExtensionsHT();
      if ((localHashtable != null) && (localHashtable.size() > 0))
      {
        if (localHashtable.get("IdentifyCode") != null)
        {
          Vector localVector = (Vector)localHashtable.get("IdentifyCode");
          int i = localVector.size();
          int j = 0;
          for (int k = 0; k < i; k++)
          {
            StandardExtension localStandardExtension = (StandardExtension)localVector.get(k);
            String str1 = localStandardExtension.getChildName();
            String str2 = localStandardExtension.getStandardValue();
            if ((str1 == null) || (str2 == null) || (str2.trim().equalsIgnoreCase("")))
              continue;
            if (str1.equalsIgnoreCase("residenterCardNumber"))
            {
              j = 1;
              ((IdentifyCodeExt)this.extension).setResidenterCardNumber(str2);
            }
            else if (str1.equalsIgnoreCase("militaryOfficerCardNumber"))
            {
              j = 1;
              ((IdentifyCodeExt)this.extension).setMilitaryOfficerCardNumber(str2);
            }
            else
            {
              j = 1;
              ((IdentifyCodeExt)this.extension).setPassportNumber(str2);
            }
          }
          if (j == 0)
            this.extension = null;
        }
        else
        {
          this.extension = null;
        }
      }
      else
        this.extension = null;
    }
  }

  class CRLDPProcessor extends X509V3CTMLProcessor.Processor
  {
    CRLDPProcessor()
    {
      super();
    }

    protected void process(X509V3ExtensionPolicy paramX509V3ExtensionPolicy)
      throws PKIException
    {
      CRLConfig localCRLConfig = X509V3CTMLProcessor.this.config.getCrlConfig();
      int i = localCRLConfig.getCRLPubAddress().length;
      CRLDistPointExt localCRLDistPointExt = new CRLDistPointExt();
      DistributionPointExt[] arrayOfDistributionPointExt = new DistributionPointExt[i];
      for (int j = 0; j < i; j++)
      {
        arrayOfDistributionPointExt[j] = new DistributionPointExt();
        String[][] arrayOfString = localCRLConfig.getCRLPubAddress();
        String str1 = arrayOfString[j][1];
        String str2 = "crl" + X509V3CTMLProcessor.this.certInfo.getCdpid();
        int k;
        if ("DN".equalsIgnoreCase(arrayOfString[j][0]))
        {
          k = 4;
          if ((str1.indexOf("c=") == 0) || (str1.indexOf("C=") == 0))
            str1 = str1 + ",cn=" + str2;
          else
            str1 = "cn=" + str2 + "," + str1;
        }
        else if ("URI".equalsIgnoreCase(arrayOfString[j][0]))
        {
          k = 6;
          str1 = str1 + "/" + str2 + ".crl";
        }
        else if ("LDAP_URI".equalsIgnoreCase(arrayOfString[j][0]))
        {
          k = 6;
          String str3 = localCRLConfig.getCRLLDAPPath();
          String str4 = localCRLConfig.getCDP_LDAP_URI();
          InternalConfig localInternalConfig = null;
          try
          {
            localInternalConfig = InternalConfig.getInstance();
          }
          catch (IDAException localIDAException)
          {
            throw new PKIException(localIDAException.getErrCode(), localIDAException.getErrDesc(), localIDAException.getHistory());
          }
          String str5 = localInternalConfig.getCrlEnding();
          str1 = str4 + "cn=" + str2 + "," + str3 + str5;
        }
        else
        {
          k = 6;
          str1 = null;
        }
        arrayOfDistributionPointExt[j].setDistributionPointName(k, str1);
      }
      localCRLDistPointExt.setDistributionPointExts(arrayOfDistributionPointExt);
      this.extension = localCRLDistPointExt;
    }
  }

  class CertPolicyProcessor extends X509V3CTMLProcessor.Processor
  {
    CertPolicyProcessor()
    {
      super();
    }

    protected void process(X509V3ExtensionPolicy paramX509V3ExtensionPolicy)
      throws PKIException
    {
      CAConfig localCAConfig = X509V3CTMLProcessor.this.config.getCAConfig();
      String str1 = localCAConfig.getCertificatePolicies();
      String[] arrayOfString = str1.split("[;]");
      CertificatePoliciesExt localCertificatePoliciesExt = new CertificatePoliciesExt();
      for (int i = 0; i < arrayOfString.length; i++)
      {
        String str2 = arrayOfString[i];
        if ((str2 == null) || (str2.trim().equals("")))
          continue;
        int j = str2.indexOf("=");
        String str3 = "2.5.29.32";
        if (j != -1)
        {
          str3 = str2.substring(0, j);
          String str4 = str2.substring(j + 1);
          localCertificatePoliciesExt.addPolicy(str3.trim());
          if ((str4 == null) || (str4.trim().equals("")))
            continue;
          localCertificatePoliciesExt.getPolicy(i).addPolicyQualifierinfo(str4.trim());
        }
        else
        {
          str3 = str2.substring(0);
          localCertificatePoliciesExt.addPolicy(str3.trim());
        }
      }
      this.extension = localCertificatePoliciesExt;
    }
  }

  class SubjectKeyIDProcessor extends X509V3CTMLProcessor.Processor
  {
    SubjectKeyIDProcessor()
    {
      super();
    }

    protected void process(X509V3ExtensionPolicy paramX509V3ExtensionPolicy)
      throws PKIException
    {
      this.extension = new SubjectKeyIdentifierExt(X509V3CTMLProcessor.this.pubKey);
    }
  }

  class AuthorityKeyIDProcessor extends X509V3CTMLProcessor.Processor
  {
    AuthorityKeyIDProcessor()
    {
      super();
    }

    protected void process(X509V3ExtensionPolicy paramX509V3ExtensionPolicy)
      throws PKIException
    {
      this.extension = new AuthorityKeyIdentifierExt(X509V3CTMLProcessor.this.caKeyPair.getPublicKey());
    }
  }

  class AuthInfoAccessProcessor extends X509V3CTMLProcessor.Processor
  {
    AuthInfoAccessProcessor()
    {
      super();
    }

    protected void process(X509V3ExtensionPolicy paramX509V3ExtensionPolicy)
      throws PKIException
    {
      CAConfig localCAConfig = X509V3CTMLProcessor.this.config.getCAConfig();
      AuthorityInformationAccessExt localAuthorityInformationAccessExt = new AuthorityInformationAccessExt();
      String str1 = localCAConfig.getAuthorityInformation();
      String[] arrayOfString = str1.split("[;]");
      for (int i = 0; i < arrayOfString.length; i++)
      {
        AccessDescriptionExt localAccessDescriptionExt = new AccessDescriptionExt();
        int j = arrayOfString[i].indexOf("=");
        String str2 = arrayOfString[i].substring(0, j);
        String str3 = arrayOfString[i].substring(j + 1);
        if ((str2 != null) && (!str2.trim().equals("")) && (str3 != null) && (!str3.trim().equals("")))
        {
          if (j == -1)
            continue;
          if (str2.trim().equalsIgnoreCase("OCSP"))
          {
            localAccessDescriptionExt.setAccessMethod(AccessDescriptionExt.METHOD_OCSP);
          }
          else
          {
            if (!str2.trim().equalsIgnoreCase("CAISSUER"))
              continue;
            localAccessDescriptionExt.setAccessMethod(AccessDescriptionExt.METHOD_CA_ISSUERS);
          }
          localAccessDescriptionExt.setAccessLocationType(6);
          localAccessDescriptionExt.setAccessLocation(str3.trim());
        }
        localAuthorityInformationAccessExt.addAccessDescription(localAccessDescriptionExt);
      }
      AccessDescriptionExt[] arrayOfAccessDescriptionExt = localAuthorityInformationAccessExt.getAccessDescription();
      if ((arrayOfAccessDescriptionExt == null) || (arrayOfAccessDescriptionExt.length == 0))
        localAuthorityInformationAccessExt = null;
      this.extension = localAuthorityInformationAccessExt;
    }
  }

  class CertificateTemplateProcess extends X509V3CTMLProcessor.Processor
  {
    private CertificateTemplatePolicy policy;
    private CertificateTemplateExt extension = new CertificateTemplateExt();

    CertificateTemplateProcess()
    {
      super();
    }

    protected void process(X509V3ExtensionPolicy paramX509V3ExtensionPolicy)
      throws PKIException
    {
      this.policy = ((CertificateTemplatePolicy)paramX509V3ExtensionPolicy);
      processValue();
      this.extension = this.extension;
    }

    private void processValue()
      throws PKIException
    {
      if (this.policy.getDomainController() != null)
        this.extension.setTemplate(this.policy.getDomainController());
      else
        this.extension.setTemplate(this.policy.getSmartCardLogonUser());
    }
  }

  class SubjectAltNameExtProcess extends X509V3CTMLProcessor.Processor
  {
    private SubjectAltNameExtPolicy policy;
    private SubjectAltNameExt extension = new SubjectAltNameExt();

    SubjectAltNameExtProcess()
    {
      super();
    }

    protected void process(X509V3ExtensionPolicy paramX509V3ExtensionPolicy)
      throws PKIException
    {
      this.policy = ((SubjectAltNameExtPolicy)paramX509V3ExtensionPolicy);
      processValue();
      this.extension = this.extension;
    }

    private void processValue()
      throws PKIException
    {
      Hashtable localHashtable = X509V3CTMLProcessor.this.certInfo.getStandardExtensionsHT();
      if ((localHashtable != null) && (localHashtable.size() > 0))
      {
        if (localHashtable.get("SubjectAltNameExt") != null)
        {
          Vector localVector = (Vector)localHashtable.get("SubjectAltNameExt");
          int i = localVector.size();
          int j = 0;
          for (int k = 0; k < i; k++)
          {
            StandardExtension localStandardExtension = (StandardExtension)localVector.get(k);
            String str1 = localStandardExtension.getChildName();
            String str2 = localStandardExtension.getStandardValue();
            String[] arrayOfString = str2.split("[;]");
            if ((str1 == null) || (str2 == null) || (str2.trim().equalsIgnoreCase("")))
              continue;
            if (str1.equalsIgnoreCase("otherName"))
            {
              j = 1;
              String str3 = localStandardExtension.getOtherNameOid();
              if ((str3 == null) || (str3.equalsIgnoreCase("1.3.6.1.4.1.311.20.2.3")))
                for (n = 0; n < arrayOfString.length; n++)
                  this.extension.addOtherName_UPN(arrayOfString[n].trim());
              for (int n = 0; n < arrayOfString.length; n++)
                this.extension.addOtherName_GUID(arrayOfString[n].trim());
            }
            if (str1.equalsIgnoreCase("email"))
            {
              j = 1;
              for (m = 0; m < arrayOfString.length; m++)
                this.extension.addRFC822Name(arrayOfString[m].trim());
            }
            if (str1.equalsIgnoreCase("dnName"))
            {
              j = 1;
              for (m = 0; m < arrayOfString.length; m++)
                this.extension.addDirectoryName(arrayOfString[m].trim());
            }
            if (str1.equalsIgnoreCase("dnsName"))
            {
              j = 1;
              for (m = 0; m < arrayOfString.length; m++)
                this.extension.addDNSName(arrayOfString[m].trim());
            }
            if (str1.equalsIgnoreCase("ip"))
            {
              j = 1;
              for (m = 0; m < arrayOfString.length; m++)
                this.extension.addIPAddress(arrayOfString[m].trim());
            }
            if (!str1.equalsIgnoreCase("uriName"))
              continue;
            j = 1;
            for (int m = 0; m < arrayOfString.length; m++)
              this.extension.addUniformResourceIdentifier(arrayOfString[m].trim());
          }
          if (j == 0)
            this.extension = null;
        }
        else
        {
          this.extension = null;
        }
      }
      else
        this.extension = null;
    }
  }

  class BasicConstraintsProcessor extends X509V3CTMLProcessor.Processor
  {
    BasicConstraintsProcessor()
    {
      super();
    }

    protected void process(X509V3ExtensionPolicy paramX509V3ExtensionPolicy)
      throws PKIException
    {
      BasicConstraintsPolicy localBasicConstraintsPolicy = (BasicConstraintsPolicy)paramX509V3ExtensionPolicy;
      if (localBasicConstraintsPolicy.getPathLenConstraint() >= 0)
        this.extension = new BasicConstraintsExt(localBasicConstraintsPolicy.isCA(), localBasicConstraintsPolicy.getPathLenConstraint());
      else
        this.extension = new BasicConstraintsExt(localBasicConstraintsPolicy.isCA());
    }
  }

  class CertTypeProcessor extends X509V3CTMLProcessor.Processor
  {
    private NetscapeCertTypePolicy policy;
    private NetscapeCertTypeExt extension = new NetscapeCertTypeExt();

    CertTypeProcessor()
    {
      super();
    }

    protected void process(X509V3ExtensionPolicy paramX509V3ExtensionPolicy)
      throws PKIException
    {
      this.policy = ((NetscapeCertTypePolicy)paramX509V3ExtensionPolicy);
      processValue();
      this.extension = this.extension;
    }

    private void processValue()
      throws PKIException
    {
      Vector localVector = this.policy.getCertType();
      if (localVector.contains("ObjectSigning"))
        this.extension.addCertType("objectSigning");
      if (localVector.contains("ObjectSigningCA"))
        this.extension.addCertType("objectSigningCA");
      if (localVector.contains("SMIME"))
        this.extension.addCertType("smime");
      if (localVector.contains("SMIMECA"))
        this.extension.addCertType("smimeCA");
      if (localVector.contains("SSLCA"))
        this.extension.addCertType("sslCA");
      if (localVector.contains("SSLclient"))
        this.extension.addCertType("sslClient");
      if (localVector.contains("SSLserver"))
        this.extension.addCertType("sslServer");
    }
  }

  class ExKeyUsageProcessor extends X509V3CTMLProcessor.Processor
  {
    private ExtendKeyUsagePolicy policy;
    private ExtendedKeyUsageExt extension = new ExtendedKeyUsageExt();

    ExKeyUsageProcessor()
    {
      super();
    }

    protected void process(X509V3ExtensionPolicy paramX509V3ExtensionPolicy)
      throws PKIException
    {
      this.policy = ((ExtendKeyUsagePolicy)paramX509V3ExtensionPolicy);
      processValue();
      this.extension = this.extension;
    }

    private void processValue()
      throws PKIException
    {
      Vector localVector = this.policy.getStandardKeyUsage();
      if (localVector.contains("clientAuth"))
        this.extension.addExtendedKeyUsage(ExtendedKeyUsageExt.CLIENT_AUTH);
      if (localVector.contains("codeSigning"))
        this.extension.addExtendedKeyUsage(ExtendedKeyUsageExt.CODE_SIGNING);
      if (localVector.contains("emailProtection"))
        this.extension.addExtendedKeyUsage(ExtendedKeyUsageExt.EMAIL_PROTECTION);
      if (localVector.contains("serverAuth"))
        this.extension.addExtendedKeyUsage(ExtendedKeyUsageExt.SERVER_AUTH);
      if (localVector.contains("timeStamping"))
        this.extension.addExtendedKeyUsage(ExtendedKeyUsageExt.TIME_STAMPING);
      if (localVector.contains("ocspSinging"))
        this.extension.addExtendedKeyUsage(ExtendedKeyUsageExt.OCSP_SIGNING);
      if (localVector.contains("smartCardLogon"))
        this.extension.addExtendedKeyUsage(ExtendedKeyUsageExt.SMART_CARD_LOGON);
      Properties localProperties = this.policy.getSelfKeyUsage();
      Enumeration localEnumeration = localProperties.keys();
      while (localEnumeration.hasMoreElements())
        this.extension.addExtendedKeyUsage((String)localEnumeration.nextElement());
    }
  }

  class KeyUsageProcessor extends X509V3CTMLProcessor.Processor
  {
    private KeyUsagePolicy policy;
    private KeyUsageExt extension = new KeyUsageExt();

    KeyUsageProcessor()
    {
      super();
    }

    protected void process(X509V3ExtensionPolicy paramX509V3ExtensionPolicy)
      throws PKIException
    {
      this.policy = ((KeyUsagePolicy)paramX509V3ExtensionPolicy);
      processValue();
      this.extension = this.extension;
    }

    private void processValue()
      throws PKIException
    {
      Vector localVector = this.policy.getKeyUsage();
      if (localVector.contains("cRLSign"))
        this.extension.addKeyUsage("cRLSign");
      if (localVector.contains("dataEncipherment"))
        this.extension.addKeyUsage("dataEncipherment");
      if (localVector.contains("decipherOnly"))
        this.extension.addKeyUsage("decipherOnly");
      if (localVector.contains("digitalSignature"))
        this.extension.addKeyUsage("digitalSignature");
      if (localVector.contains("encipherOnly"))
        this.extension.addKeyUsage("encipherOnly");
      if (localVector.contains("keyAgreement"))
        this.extension.addKeyUsage("keyAgreement");
      if (localVector.contains("keyCertSign"))
        this.extension.addKeyUsage("keyCertSign");
      if (localVector.contains("keyEncipherment"))
        this.extension.addKeyUsage("keyEncipherment");
      if (localVector.contains("nonRepudiation"))
        this.extension.addKeyUsage("nonRepudiation");
      this.extension.setKeyUsage(localVector);
    }
  }

  class SelfExtProcessor extends X509V3CTMLProcessor.Processor
  {
    private SelfDefExtension extension = new SelfDefExtension();
    private SelfExtensionPolicy policy;

    SelfExtProcessor()
    {
      super();
    }

    protected void process(X509V3ExtensionPolicy paramX509V3ExtensionPolicy)
      throws PKIException
    {
      this.policy = ((SelfExtensionPolicy)paramX509V3ExtensionPolicy);
      this.extension.setEncoding(paramX509V3ExtensionPolicy.getEncoding());
      this.extension.setOID(paramX509V3ExtensionPolicy.getOID());
      processValue();
      this.extension = this.extension;
    }

    private void processValue()
      throws PKIException
    {
      String str = null;
      if ("NEED".equals(this.policy.getUserProcessPolicy()))
      {
        str = X509V3CTMLProcessor.this.certInfo.getExtensions().getProperty(this.policy.getName());
      }
      else if ("ALLOW".equals(this.policy.getUserProcessPolicy()))
      {
        str = X509V3CTMLProcessor.this.certInfo.getExtensions().getProperty(this.policy.getName());
        if (str == null)
        {
          this.extension = null;
          return;
        }
      }
      else if ("DENY".equals(this.policy.getUserProcessPolicy()))
      {
        str = this.policy.getValue();
      }
      this.extension.setExtensionValue(str);
    }
  }

  abstract class Processor
  {
    protected Extension extension;

    Processor()
    {
    }

    public Extension getExtension()
    {
      return this.extension;
    }

    protected abstract void process(X509V3ExtensionPolicy paramX509V3ExtensionPolicy)
      throws PKIException;
  }
}

/* Location:           C:\Program Files\JIT\CA50\lib\IDA\ida.jar
 * Qualified Name:     cn.com.jit.ida.ca.ctml.x509v3.X509V3CTMLProcessor
 * JD-Core Version:    0.6.0
 */