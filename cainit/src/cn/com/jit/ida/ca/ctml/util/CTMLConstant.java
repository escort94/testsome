package cn.com.jit.ida.ca.ctml.util;

import java.util.ArrayList;
import java.util.Hashtable;

public class CTMLConstant
{
  private static ArrayList ExtensionOid;
  public static final String CTMLID_0_0 = "00";
  public static final String CTMLID_1_0 = "10";
  public static final String CTMLID_1_1 = "11";
  public static final String CTMLID_2_0 = "20";
  public static final String CTMLID_3_0 = "30";
  public static final String OTHERNAMESTYLE_UPN = "UPN";
  public static final String OTHERNAMESTYLE_GUID = "GUID";
  public static final String OTHERNAMESTYLE_GUID_NAME = "域控制器的GUID";
  public static final String OTHERNAMESTYLE_UPN_NAME = "用户主要名称";
  public static final String PERUNITSECONDS = "86400000";
  public static Hashtable standardExtAndEncodeType = new Hashtable();
  public static Hashtable standardExtAndLable;

  public static final ArrayList getExtensionOid()
  {
    ExtensionOid = new ArrayList();
    ExtensionOid.add("1.3.6.1.5.5.7.1.1");
    ExtensionOid.add("2.5.29.35");
    ExtensionOid.add("2.5.29.19");
    ExtensionOid.add("2.5.29.32");
    ExtensionOid.add("2.5.29.31");
    ExtensionOid.add("2.5.29.37");
    ExtensionOid.add("1.3.6.1.5.5.7.3.2");
    ExtensionOid.add("1.3.6.1.5.5.7.3.3");
    ExtensionOid.add("1.3.6.1.5.5.7.3.4");
    ExtensionOid.add("1.3.6.1.5.5.7.3.9");
    ExtensionOid.add("1.3.6.1.5.5.7.3.1");
    ExtensionOid.add("1.3.6.1.5.5.7.3.8");
    ExtensionOid.add("1.3.6.1.4.1.311.20.2.2");
    ExtensionOid.add("2.5.29.15");
    ExtensionOid.add("2.5.29.14");
    ExtensionOid.add("2.16.840.1.113730.1.1");
    ExtensionOid.add("2.5.29.17");
    ExtensionOid.add("1.3.6.1.4.1.311.20.2");
    ExtensionOid.add("2.5.29.36");
    ExtensionOid.add("2.5.29.33");
    ExtensionOid.add("1.2.86.11.7.1");
    ExtensionOid.add("1.2.86.11.7.2");
    ExtensionOid.add("1.2.86.11.7.4");
    ExtensionOid.add("1.2.86.11.7.5");
    ExtensionOid.add("1.2.86.11.7.3");
    return ExtensionOid;
  }

  static
  {
    standardExtAndEncodeType.put("ip@SubjectAltNameExt", "IP");
    standardExtAndEncodeType.put("email@SubjectAltNameExt", "EMAIL");
    standardExtAndEncodeType.put("dnsName@SubjectAltNameExt", "DNS");
    standardExtAndEncodeType.put("1.3.6.1.4.1.311.25.1@SubjectAltNameExt", "OtherName GUID");
    standardExtAndEncodeType.put("1.3.6.1.4.1.311.20.2.3@SubjectAltNameExt", "OtherName UPN");
    standardExtAndEncodeType.put("uriName@SubjectAltNameExt", "URI");
    standardExtAndEncodeType.put("dnName@SubjectAltNameExt", "X500Name");
    standardExtAndEncodeType.put("residenterCardNumber@IdentifyCode", "Printable String");
    standardExtAndEncodeType.put("militaryOfficerCardNumber@IdentifyCode", "UTF-8 String");
    standardExtAndEncodeType.put("passportNumber@IdentifyCode", "Printable String");
    standardExtAndEncodeType.put("InsuranceNumber@InsuranceNumber", "Printable String");
    standardExtAndEncodeType.put("TaxationNumber@TaxationNumber", "Printable String");
    standardExtAndEncodeType.put("ICRegistrationNumber@ICRegistrationNumber", "Printable String");
    standardExtAndEncodeType.put("OrganizationCode@OrganizationCode", "Printable String");
    standardExtAndEncodeType.put("inhibitPolicyMapping@PolicyConstrants", "Integer");
    standardExtAndEncodeType.put("requireExplicitPolicy@PolicyConstrants", "Integer");
    standardExtAndEncodeType.put("issuerDomainPolicy@PolicyMappings", "OID");
    standardExtAndEncodeType.put("subjectDomainPolicy@PolicyMappings", "OID");
    standardExtAndLable = new Hashtable();
    standardExtAndLable.put("ip@SubjectAltNameExt", "主题备用名称 : IP地址");
    standardExtAndLable.put("email@SubjectAltNameExt", "主题备用名称 : 电子邮件地址");
    standardExtAndLable.put("dnsName@SubjectAltNameExt", "主题备用名称 : 域名");
    standardExtAndLable.put("otherName@SubjectAltNameExt", "主题备用名称 : 其他名称");
    standardExtAndLable.put("uriName@SubjectAltNameExt", "主题备用名称 : 统一资源定位符");
    standardExtAndLable.put("dnName@SubjectAltNameExt", "主题备用名称 : 用户甄别名");
    standardExtAndLable.put("residenterCardNumber@IdentifyCode", "身份标识码 : 居民身份证号码");
    standardExtAndLable.put("militaryOfficerCardNumber@IdentifyCode", "身份标识码 : 军官证号码");
    standardExtAndLable.put("passportNumber@IdentifyCode", "身份标识码 : 护照号码");
    standardExtAndLable.put("InsuranceNumber@InsuranceNumber", "个人社会保险号");
    standardExtAndLable.put("TaxationNumber@TaxationNumber", "企业税号");
    standardExtAndLable.put("ICRegistrationNumber@ICRegistrationNumber", "企业工商注册号");
    standardExtAndLable.put("OrganizationCode@OrganizationCode", "企业组织机构代码");
    standardExtAndLable.put("inhibitPolicyMapping@PolicyConstrants", "禁止的策略映射");
    standardExtAndLable.put("requireExplicitPolicy@PolicyConstrants", "可以接受的策略");
    standardExtAndLable.put("issuerDomainPolicy@PolicyMappings", "颁发者域策略");
    standardExtAndLable.put("subjectDomainPolicy@PolicyMappings", "主体域策略");
  }

  public static class EXTENSION
  {
    public static final String ID_CE = "2.5.29.";
    public static final String ID_PE = "1.3.6.1.5.5.7.1.";
    public static final String ID_KP = "1.3.6.1.5.5.7.3.";
    public static final long MAX_LEN = 1024L;
    public static final String USERDEFINE = "userDefineExtension";

    public class NAME_CONSTRANTS
    {
      public static final String NAME = "NameConstrants";
      public static final String OID = "2.5.29.30";
      public static final String POLICY_CONSTRANTS_LABEL = "名称限制";

      public NAME_CONSTRANTS()
      {
      }
    }

    public class POLICY_CONSTRANTS
    {
      public static final String NAME = "PolicyConstrants";
      public static final String OID = "2.5.29.36";
      public static final String POLICY_CONSTRANTS_LABEL = "策略限制";
      public static final String REQUIRE_EXPLICIT_POLICY = "requireExplicitPolicy";
      public static final String REQUIRE_EXPLICIT_POLICY_LABLE = "可以接受的策略";
      public static final String INHIBIT_POLICY_MAPPING = "inhibitPolicyMapping";
      public static final String INHIBIT_POLICY_MAPPING_LABLE = "禁止的策略映射";

      public POLICY_CONSTRANTS()
      {
      }
    }

    public class POLICY_MAPPINGS
    {
      public static final String NAME = "PolicyMappings";
      public static final String OID = "2.5.29.33";
      public static final String POLICY_MAPPINGS_LABEL = "策略映射";
      public static final String ISSUER_DOMAIN_POLICY = "issuerDomainPolicy";
      public static final String ISSUER_DOMAIN_POLICY_LABLE = "颁发者域策略";
      public static final String SUBJECT_DOMAIN_POLICY = "subjectDomainPolicy";
      public static final String SUBJECT_DOMAIN_POLICY_LABLE = "主体域策略";

      public POLICY_MAPPINGS()
      {
      }
    }

    public class TAXATION_NUMBER
    {
      public static final String NAME = "TaxationNumber";
      public static final String OID = "1.2.86.11.7.5";
      public static final String TAXATION_NUMBER_LABEL = "企业税号";

      public TAXATION_NUMBER()
      {
      }
    }

    public class IC_REGISTRATION_NUMBER
    {
      public static final String NAME = "ICRegistrationNumber";
      public static final String OID = "1.2.86.11.7.4";
      public static final String IC_REGISTRATION_NUMBER_LABEL = "企业工商注册号";

      public IC_REGISTRATION_NUMBER()
      {
      }
    }

    public class ORGANIZATION_CODE
    {
      public static final String NAME = "OrganizationCode";
      public static final String OID = "1.2.86.11.7.3";
      public static final String ORGANIZATION_CODE_LABEL = "企业组织机构代码";

      public ORGANIZATION_CODE()
      {
      }
    }

    public class INSURANCE_NUMBER
    {
      public static final String NAME = "InsuranceNumber";
      public static final String OID = "1.2.86.11.7.2";
      public static final String INSURANCE_NUMBER_LABEL = "个人社会保险号";

      public INSURANCE_NUMBER()
      {
      }
    }

    public class IDENTIFY_CODE
    {
      public static final String NAME = "IdentifyCode";
      public static final String NAME_LABLE = "身份标识码";
      public static final String OID = "1.2.86.11.7.1";
      public static final String RESIDENTER_CARD_NUMBER = "residenterCardNumber";
      public static final String RESIDENTER_CARD_NUMBER_LABEL = "居民身份证号码";
      public static final String MILITARY_OFFICER_CARD_NUMBER = "militaryOfficerCardNumber";
      public static final String MILITARY_OFFICER_CARD_NUMBER_LABEL = "军官证号码";
      public static final String PASSPORT_NUMBER = "passportNumber";
      public static final String PASSPORT_NUMBER_LABEL = "护照号码";

      public IDENTIFY_CODE()
      {
      }
    }

    public class CERTIFICATETEMPLATE
    {
      public static final String NAME = "CertificateTemplate";
      public static final String OID = "1.3.6.1.4.1.311.20.2";
      public static final String DomainController = "domainController";
      public static final String SmartCardLogon = "smartCardLogon";

      public CERTIFICATETEMPLATE()
      {
      }
    }

    public class SUBJECTALTNAMEEXT
    {
      public static final String NAME = "SubjectAltNameExt";
      public static final String NAME_LABLE = "主题备用名称";
      public static final String OID = "2.5.29.17";
      public static final String OTHERNAME = "otherName";
      public static final String DNSNAME = "dnsName";
      public static final String URINAME = "uriName";
      public static final String DNNAME = "dnName";
      public static final String IPADDRESS = "ip";
      public static final String EMAILADDRESS = "email";
      public static final String OTHERNAMELABEL = "其他名称";
      public static final String DNSNAMELABEL = "域名";
      public static final String URINAMELABEL = "统一资源定位符";
      public static final String DNNAMELABEL = "用户甄别名";
      public static final String IPADDRESSLABEL = "IP地址";
      public static final String EMAILADDRESSLABEL = "电子邮件地址";

      public SUBJECTALTNAMEEXT()
      {
      }
    }

    public class CERTPOLICY
    {
      public static final String NAME = "CertificatePolicies";
      public static final String OID = "2.5.29.32";

      public CERTPOLICY()
      {
      }
    }

    public class CERTTYPE
    {
      public static final String NAME = "NetscapeCertType";
      public static final String OID = "2.16.840.1.113730.1.1";
      public static final String SSLCLIENT = "SSLclient";
      public static final String SSLSERVER = "SSLserver";
      public static final String SMIME = "SMIME";
      public static final String OBJSIGN = "ObjectSigning";
      public static final String SSLCA = "SSLCA";
      public static final String SMIMECA = "SMIMECA";
      public static final String OBJSIGNCA = "ObjectSigningCA";

      public CERTTYPE()
      {
      }
    }

    public class AUTHINFOACCESS
    {
      public static final String NAME = "AuthorityInformationAccess";
      public static final String OID = "1.3.6.1.5.5.7.1.1";

      public AUTHINFOACCESS()
      {
      }
    }

    public class CRLDP
    {
      public static final String NAME = "CRLDistributionPoints";
      public static final String OID = "2.5.29.31";

      public CRLDP()
      {
      }
    }

    public class BASICCONSTRAINTS
    {
      public static final String NAME = "BasicConstraints";
      public static final String OID = "2.5.29.19";

      public BASICCONSTRAINTS()
      {
      }
    }

    public class EXTENDKEYUSAGE
    {
      public static final String NAME = "ExtendKeyUsage";
      public static final String OID = "2.5.29.37";
      public static final String SERVERAUTH = "serverAuth";
      public static final String CLIENTAUTH = "clientAuth";
      public static final String CODESIGN = "codeSigning";
      public static final String EMAIL = "emailProtection";
      public static final String TIMESTAMP = "timeStamping";
      public static final String OCSPSIGN = "ocspSinging";
      public static final String SMARTCARDLOGON = "smartCardLogon";
      public static final String SERVERAUTH_OID = "1.3.6.1.5.5.7.3.1";
      public static final String CLIENTAUTH_OID = "1.3.6.1.5.5.7.3.2";
      public static final String CODESIGN_OID = "1.3.6.1.5.5.7.3.3";
      public static final String EMAIL_OID = "1.3.6.1.5.5.7.3.4";
      public static final String TIMESTAMP_OID = "1.3.6.1.5.5.7.3.8";
      public static final String OCSPSIGN_OID = "1.3.6.1.5.5.7.3.9";
      public static final String SMARTCARDLOGON_OID = "1.3.6.1.4.1.311.20.2.2";

      public EXTENDKEYUSAGE()
      {
      }
    }

    public class KEYUSAGE
    {
      public static final String NAME = "KeyUsage";
      public static final String OID = "2.5.29.15";
      public static final String DIGISIGN = "digitalSignature";
      public static final String NONREP = "nonRepudiation";
      public static final String KEYENC = "keyEncipherment";
      public static final String DATAENC = "dataEncipherment";
      public static final String KEYAGREE = "keyAgreement";
      public static final String KEYCERTSIGN = "keyCertSign";
      public static final String CRLSIGN = "cRLSign";
      public static final String ENCONLY = "encipherOnly";
      public static final String DECONLY = "decipherOnly";

      public KEYUSAGE()
      {
      }
    }

    public class SUBJECTKEYID
    {
      public static final String NAME = "SubjectKeyIdentifier";
      public static final String OID = "2.5.29.14";

      public SUBJECTKEYID()
      {
      }
    }

    public class AUTHORITYKEYID
    {
      public static final String NAME = "AuthorityKeyIdentifier";
      public static final String OID = "2.5.29.35";

      public AUTHORITYKEYID()
      {
      }
    }

    public static class IF_CHECK
    {
      public static final String CHECK = "TRUE";
      public static final String UN_CHECK = "FALSE";
    }

    public static class ALLOW_NULL
    {
      public static final String ALLOW_NULL = "TRUE";
      public static final String NOT_ALLOW_NULL = "FALSE";
    }

    public static class USERPROCESS
    {
      public static final String DENY = "DENY";
      public static final String ALLOW = "ALLOW";
      public static final String NEED = "NEED";
    }

    public static class ENCODING
    {
      public static final String PRINTABLESTRING = "Printable String";
      public static final String UTF8STRING = "UTF-8 String";
      public static final String USERDEFINED = "User Defined";
      public static final String INTEGER = "Integer";
      public static final String BOOLEAN = "Boolean";
      public static final String IA5STRING = "IA5 String";
      public static final String STANDARD = "Standard";
      public static final String IP = "IP";
      public static final String EMAIL = "EMAIL";
      public static final String URI = "URI";
      public static final String X500_NAME = "X500Name";
      public static final String DNS = "DNS";
      public static final String OTHER_NAME_GUID = "OtherName GUID";
      public static final String OTHER_NAME_UPN = "OtherName UPN";
      public static final String OID = "OID";
    }
  }

  public class DB
  {
    public DB()
    {
    }

    public class SELFEXT
    {
      public static final String TBLNAME = "SELFEXT";

      public SELFEXT()
      {
      }

      public class FIELDNAME
      {
        public static final String NAME = "selfext_name";
        public static final String OID = "selfext_oid";
        public static final String STATUS = "selfext_status";
        public static final String ENCODETYPE = "selfext_encodetype";
        public static final String DESCRIPTION = "selfext_description";
        public static final String RESERVE = "reserve";

        public FIELDNAME()
        {
        }
      }
    }

    public class CTML
    {
      public static final String TBLNAME = "CTML";

      public CTML()
      {
      }

      public class FIELDNAME
      {
        public static final String NAME = "ctml_name";
        public static final String ID = "ctml_id";
        public static final String TYPE = "ctml_type";
        public static final String STATUS = "ctml_status";
        public static final String DESCRIPTION = "ctml_description";
        public static final String POLICYINFO = "ctml_policyinfo";
        public static final String RESERVE = "reserve";

        public FIELDNAME()
        {
        }
      }
    }
  }

  public static class ISSUE_MEDIUM
  {
    public static final String FILE = "FILE";
    public static final String DB = "DB";
    public static final String LDAP = "LDAP";

    public static boolean isSupport(String paramString)
    {
      if (paramString.equalsIgnoreCase("FILE"))
        return true;
      if (paramString.equalsIgnoreCase("DB"))
        return true;
      return paramString.equalsIgnoreCase("LDAP");
    }
  }

  public static class KEY
  {
    public static class GENPLACE
    {
      public static final String KMC = "KMC";
      public static final String LOCAL = "LOCAL";
      public static final String CA = "CA";

      public static boolean isSupport(String paramString)
      {
        if (paramString.equalsIgnoreCase("KMC"))
          return true;
        if (paramString.equalsIgnoreCase("LOCAL"))
          return true;
        return paramString.equalsIgnoreCase("CA");
      }
    }

    public static class TYPE
    {
      public static final String RSA = "RSA";

      public static boolean isSupport(String paramString)
      {
        return paramString.equalsIgnoreCase("RSA");
      }
    }
  }

  public static class SELFEXT_STATUS extends CTMLConstant.CTMLSTATUS
  {
  }

  public class CTMLNAME
  {
    public static final String ACROSSCERTCTMLNAME = "CA证书模板";
    public static final String CODESIGNCTMLNAME = "代码签名证书模板";
    public static final String CURRENCYCTMLNAME = "通用证书模板";
    public static final String DOMAINCONTROLLERCTMLNAME = "智能卡登录-域控制器模板";
    public static final String ENCRYPTCTMLNAME = "加密证书模板";
    public static final String OCSPCTMLNAME = "OCSP签名证书模板";
    public static final String SSLSERVERCTMLNAME = "SSL服务器证书模板";
    public static final String SIGNCTMLNAME = "签名证书模板";
    public static final String SMARTLOGONCTMLNAME = "智能卡登录-域用户证书模板";
    public static final String TSASERVERCTMLNAME = "时间戳签名证书模板";
    public static final String CROSSCERT_CTMLNAME = "交叉证书模板";

    public CTMLNAME()
    {
    }
  }

  public static class CTMLSTATUS
  {
    public static final String UNUSED = "UNUSED";
    public static final String USING = "USING";
    public static final String REVOKED = "REVOKED";
  }

  public class CTMLTYPE
  {
    public static final String X509V3 = "X509V3";

    public CTMLTYPE()
    {
    }
  }
}

/* Location:           C:\Program Files\JIT\CA50\lib\IDA\ida.jar
 * Qualified Name:     cn.com.jit.ida.ca.ctml.util.CTMLConstant
 * JD-Core Version:    0.6.0
 */