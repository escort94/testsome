package cn.com.jit.ida.ca.certmanager;

import cn.com.jit.ida.ca.res.CAConstant;
import java.util.Hashtable;

public class CertConstant extends CAConstant
{
  public static final String CERT_STATUS_USE = "Use";
  public static final String CERT_STATUS_REVOKE = "Revoke";
  public static final String CERT_STATUS_HOLD = "Hold";
  public static final String CERT_STATUS_UNDOWN = "Undown";
  public static final String CERT_STATUS_UNDOWN_REVOKE = "UndownRevoke";
  public static final String CERT_ISWAITING = "1";
  public static final String CERT_ISNOTWAITING = "0";
  public static final String CERT_STATUS_USE_DISPLAY = "使用中";
  public static final String CERT_STATUS_REVOKE_DISPLAY = "注销";
  public static final String CERT_STATUS_HOLD_DISPLAY = "冻结";
  public static final String CERT_STATUS_UNDOWN_DISPLAY = "未下载";
  public static final String CERT_STATUS_UNDOWN_REVOKE_DISPLAY = "未下载注销";
  public static final String PROTOCOL_CERT_SN = "certSN";
  public static final String PROTOCOL_REVOKE_REASON = "revokeReason";
  public static final String PROTOCOL_REVOKE_DESC = "revokeDesc";
  public static final String PROTOCOL_REF_CODE = "refCode";
  public static final String PROTOCOL_AUTH_CODE = "authCode";
  public static final String PROTOCOL_CTML_NAME = "ctmlName";
  public static final String PROTOCOL_SUBJECT = "subject";
  public static final String PROTOCOL_NOT_BEFORE = "notBefore";
  public static final String PROTOCOL_NOT_AFTER = "notAfter";
  public static final String PROTOCOL_VALIDITY = "validity";
  public static final String PROTOCOL_CERT_STATUS = "certStatus";
  public static final String PROTOCOL_EMAIL = "email";
  public static final String PROTOCOL_REMARK = "remark";
  public static final String PROTOCOL_IFCHECK_VALIDATE = "ifCheckValidate";
  public static final String PROTOCOL_STANDARDEXTENSIONS = "standardExtensions";
  public static final String PROTOCOL_STANDARDEXT_PARENTNAME = "parentName";
  public static final String PROTOCOL_STANDARDEXT_PARENTOID = "parentOID";
  public static final String PROTOCOL_STANDARDEXT_CHILDNAME = "childName";
  public static final String PROTOCOL_STANDARDEXT_VALUE = "value";
  public static final String PROTOCOL_STANDARDEXT_ALLOWNULL = "allowNull";
  public static final String PROTOCOL_STANDARDEXT_OTHERNAMEOID = "otherNameOid";
  public static final String PROTOCOL_STANDARDEXT_CHILDENCODE = "childEncode";
  public static final String PROTOCOL_EXTENSIONS = "selfExtensions";
  public static final String PROTOCOL_EXT_ENTRY = "extEntry";
  public static final String PROTOCOL_EXT_NAME = "name";
  public static final String PROTOCOL_EXT_VALUE = "value";
  public static final String PROTOCOL_P10 = "p10";
  public static final String PROTOCOL_APPLICANT = "applicant";
  public static final String PROTOCOL_APPLICANTUPPERCASE = "applicantuppercase";
  public static final String PROTOCOL_CREATE_TIME = "createTime";
  public static final String PROTOCOL_CREATETIME_BEGIN = "createTimeStart";
  public static final String PROTOCOL_CREATETIME_END = "createTimeEnd";
  public static final String PROTOCOL_CERT_ENTITY = "certEntity";
  public static final String PROTOCOL_P7B = "p7b";
  public static final String PROTOCOL_QUERY_FROM = "fromIndex";
  public static final String PROTOCOL_QUERY_COUNT = "rowCount";
  public static final String PROTOCOL_TOTAL_COUNT = "totalCount";
  public static final String PROTOCOL_PFX = "pfx";
  public static final String PROTOCOL_CDPID = "cdpid";
  public static final String PROTOCOL_ISVALID = "isvalid";
  public static final String PROTOCOL_SUBJECTUPPERCASE = "subjectuppercase";
  public static final String PROTOCOL_EXACT_QUERY = "exactQuery";
  public static final String PROTOCOL_TEMP_PUBKEY = "tempPubKey";
  public static final String PROTOCOL_ENCRYPTED_SESSIONKEY = "enryptedSessionKey";
  public static final String PROTOCOL_SESSIONKEY_ALG = "sessionKeyAlg";
  public static final String PROTOCOL_SESSIONKEY_PAD = "sessionKeyPad";
  public static final String PROTOCOL_ENCRYPTED_RPVKEY = "encryptedPrivateKey";
  public static final String PROTOCOL_USE_KMC = "useKmc";
  public static final String PROTOCOL_REQUIRE_KEYTYPE = "requireKeyType";
  public static final String PROTOCOL_REQUIRE_KEYLENGTH = "requireKeyLength";
  public static final String PROTOCOL_XMLTAG_CRLNAME = "CRLName";
  public static final String PROTOCOL_XMLTAG_CRLENTITY = "CRLEntity";
  public static final String PROTOCOL_XMLTAG_ISRETAINKEY = "isRetainKey";
  public static final String PROTOCOL_RVK_SCHEDULE = "rvkSchedule";
  public static final String PROTOCOL_HOLD_SCHEDULE = "holdSchedule";
  public static final String PROTOCOL_UNHOLD_SCHEDULE = "unHoldSchedule";
  public static final String PROTOCOL_ACTION = "action";
  public static final int RVK_UNSPECIFIED = 0;
  public static final int RVK_KEY_COMPROMISE = 1;
  public static final int RVK_CA_COMPROMISE = 2;
  public static final int RVK_AFFILIATION_CHANGED = 3;
  public static final int RVK_SUPERSEDED = 4;
  public static final int RVK_CESSATION_OF_OPERATION = 5;
  public static final int RVK_CERTIFICATE_HOLD = 6;
  public static final int RVK_REMOVE_FROM_CRL = 8;
  public static final String RVK_DES_UNSPECIFIED = "未指明原因";
  public static final String RVK_DES_KEY_COMPROMISE = "密钥泄密";
  public static final String RVK_DES_CA_COMPROMISE = "CA泄密";
  public static final String RVK_DES_AFFILIATION_CHANGED = "从属关系改变";
  public static final String RVK_DES_SUPERSEDED = "证书被取代";
  public static final String RVK_DES_CESSATION_OF_OPERATION = "操作终止";
  public static final String RVK_DES_REMOVE_FROM_CRL = "从CRL删除";
  public static final String ALL_CTML_IN_PRIVILEGE = "权限内所有模板";
  public static final Hashtable rvkDESAndID = new Hashtable();
  public static final String RA_ADMIN_TEMPLATE_NAME = "RA业务员证书模板";
  public static final String START_TIME = "startTime";
  public static final String END_TIME = "endTime";
  public static final String CERT_COUNT = "certCount";
  public static final String ARCHIVEMODE_YEAR = "0";
  public static final String ARCHIVEMODE_MONTH = "1";
  public static final String ARCHIVEMODE_SELFTIME = "2";
  public static final String AIA_OCSP = "OCSP";
  public static final String AIA_CA_ISSUER = "CAISSUER";

  static
  {
    rvkDESAndID.put("未指明原因", new Integer(0));
    rvkDESAndID.put("密钥泄密", new Integer(1));
    rvkDESAndID.put("CA泄密", new Integer(2));
    rvkDESAndID.put("从属关系改变", new Integer(3));
    rvkDESAndID.put("证书被取代", new Integer(4));
    rvkDESAndID.put("操作终止", new Integer(5));
    rvkDESAndID.put("从CRL删除", new Integer(8));
  }

  public static class CURKEYSTATUS
  {
    public static final String USING = "using";
    public static final String REVOKED = "revoked";
    public static final String STOPUSE = "stopuse";
    public static final String HOLD = "hold";
  }
}

/* Location:           C:\Program Files\JIT\CA50\lib\IDA\ida.jar
 * Qualified Name:     cn.com.jit.ida.ca.certmanager.CertConstant
 * JD-Core Version:    0.6.0
 */