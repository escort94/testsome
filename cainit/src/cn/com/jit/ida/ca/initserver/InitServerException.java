package cn.com.jit.ida.ca.initserver;

import cn.com.jit.ida.IDAException;

public class InitServerException extends IDAException
{
  public static final String MODAL_NAME = "80";
  public static final String FLOW_NAME = "66";
  public static final String ERROR_HEADER = "8066";
  public static final String CRYPTO_DEVICE_ID_ERROR = "0901";
  public static final String DB_DRIVER_URL_ERROR = "0902";
  public static final String DB_DRIVER_CLASS_ERROR = "0903";
  public static final String DB_USER_ERROR = "0904";
  public static final String DB_PASSWORD_ERROR = "0905";
  public static final String DB_MAXCONNCOUNT_ERROR = "0906";
  public static final String DB_TEST_SQL_ERROR = "0907";
  public static final String DB_ADMIN_ERROR = "0908";
  public static final String DB_ADMIN_PWD_ERROR = "0909";
  public static final String NET_SERVER_ADDRESS_ERROR = "0910";
  public static final String NET_PORT_ERROR = "0911";
  public static final String NET_ACCEPT_THREAD_COUNT_ERROR = "0912";
  public static final String READ_FILE_ERROR = "0913";
  public static final String WRITE_FILE_ERROR = "0914";
  public static final String XML_FILE_ERROR = "0915";
  public static final String GENERATE_KEY_PAIR_ERROR = "0916";
  public static final String GENERATE_KEY_STORE_ERROR = "0917";
  public static final String ROOT_CERT_PATH_ERROR = "0918";
  public static final String ROOT_KEYSTORE_PWD_ERROR = "0919";
  public static final String ROOT_KEY_LEN_ERROR = "0920";
  public static final String ROOT_SIGNING_ALG_ERROR = "0922";
  public static final String ROOT_NOT_BEFORE_ERROR = "0923";
  public static final String ROOT_NOT_AFTER_ERROR = "0924";
  public static final String ROOT_NAME_ERROR = "0925";
  public static final String ROOT_PUB_KEY_ERROR = "0926";
  public static final String GENERALT_ROOTCERT_ERROR = "0927";
  public static final String CA_USER_PWD_ERROR = "0928";
  public static final String MAX_PROCESS_THREAD_ERROR = "0929";
  public static final String TIME_DIF_ALLOW_ERROR = "0930";
  public static final String SERVER_TYPE_ERROR = "0931";
  public static final String ROOT_CERT_KEY_STORE_ERROR = "0932";
  public static final String ROOT_CERT_KEY_STORE_PWD_ERROR = "0933";
  public static final String COMMUNICATE_CERT_ERROR = "0934";
  public static final String COMMUNICATE_CERT_PWD_ERROR = "0935";
  public static final String WEB_SERVER_ADDRESS_ERROR = "0936";
  public static final String WEB_PORT_ERROR = "0936";
  public static final String LDAP_PORT_ERROR = "0937";
  public static final String LDAP_SERVER_ADDRESS_ERROR = "0938";
  public static final String LDAP_USER_DN_ERROR = "0939";
  public static final String LDAP_USER_PWD_ERROR = "0940";
  public static final String LDAP_BASE_DN_ERROR = "0941";
  public static final String LOG_PATH_ERROR = "0942";
  public static final String KMC_PORT_ERROR = "0943";
  public static final String KMC_SERVER_ADDRESS_ERROR = "0944";
  public static final String CRL_PERIODS_ERROR = "0945";
  public static final String CERT_COUNT_IN_CRL_ERROR = "0946";
  public static final String CRL_PUB_ADDRESS_ERROR = "0947";
  public static final String BAD_SIGN_ALGO = "0948";
  public static final String GENERATE_KEY_STORE_FILE_ERROR = "0949";
  public static final String SAVE_KEY_STORE_ERROR = "0950";
  public static final String CRL_DIST_POINT_ERROR = "0951";
  public static final String KEY_USAGE_ERROR = "0952";
  public static final String EXT_KEY_USAGE_ERROR = "0953";
  public static final String CERTIFICATE_POLICY_ERROR = "0954";
  public static final String BASIC_CONSTRAINTS_ERROR = "0955";
  public static final String GEN_P10_ERROR = "0956";
  public static final String GEN_P7B_ERROR = "0957";
  public static final String SAVE_CERT_ERROR = "0958";
  public static final String AdminCertREQ_ERROR = "0959";
  public static final String ADMIN_DN_ERROR = "0960";
  public static final String ADMIN_KEY_STORE_PWD_ERROR = "0961";
  public static final String CA_ROOT_CERT_P7B_ERROR = "0962";
  public static final String ROOT_CERT_VALIDITY_ERROR = "0963";
  public static final String CA_SIGN_KEY_ID_ERROR = "0964";
  public static final String CA_SIGN_KEY_ERROR = "0965";
  public static final String CERT_FILE_PATH_ERROR = "0965";
  public static final String COUNT_PER_PAGE_ERROR = "0966";
  public static final String CRL_FILE_PUBLISH_ERROR = "0967";
  public static final String CRL_LDAPPUBLISH_ERROR = "0968";
  public static final String CRL_FILE_PATH_ERROR = "0969";
  public static final String CRL_LDAP_PATH_ERROR = "0970";
  public static final String CDP_URI_ERROR = "0971";
  public static final String Listense_ERROR = "0972";
  public static final String Listense_CAName_ERROR = "0973";
  public static final String Listense_ProductName_ERROR = "0974";
  public static final String Listense_Validity_ERROR = "0975";
  public static final String ROOT_NAME_NOBASEDN_ERROR = "0976";
  public static final String ADMIN_CERT_PATH_ERROR = "0977";
  public static final String CONTROL_PORT_ERROR = "0978";
  public static final String COMM_CERT_VALIDITY_ERROR = "0979";
  public static final String ADMIN_CERT_VALIDITY_ERROR = "0980";
  public static final String Listense_FILE_ERROR = "0981";
  public static final String ROOT_CERT_VALIDITY_OVERFLOW_ERROR = "0982";
  public static final String SERVER_HAVA_BEEN_RUN_ERROR = "0983";
  public static final String SESSION_TIME_OUT_ERROR = "0984";
  public static final String CERT_SN_LEN_ERROR = "0985";
  public static final String CERT_AUTHCODE_LEN_ERROR = "0986";
  public static final String SUBJECT_NOT_EQUAL = "0987";
  public static final String USER_CANCEL_ERROR = "0988";
  public static final String SAME_KEYID_ERROR = "0989";
  public static final String SAME_KEYSTORE_ERROR = "0990";
  public static final String COMMCERT_PWD_ERROR = "0991";
  public static final String CROSS_CERT_PATH_ERROR = "0991";
  public static final String CROSS_CERT_NOT_MATCH_ERROR = "0992";
  public static final String CROSS_CERT_GENAERATE_ERROR = "0993";
  public static final String READ_CA_CERT_ERROR = "0994";
  public static final String SYS_ERROR = "0999";

  public InitServerException(String paramString)
  {
    super("8066" + paramString);
  }

  public InitServerException(String paramString, Exception paramException)
  {
    super("8066" + paramString, paramException);
  }

  public InitServerException(String paramString1, String paramString2)
  {
    super("8066" + paramString1, paramString2);
  }

  public InitServerException(String paramString1, String paramString2, Exception paramException)
  {
    super("8066" + paramString1, paramString2, paramException);
  }

  public String getMessage()
  {
    return "初始化异常   " + super.getMessage();
  }
}

/* Location:           C:\Program Files\JIT\CA50\lib\IDA\ida.jar
 * Qualified Name:     cn.com.jit.ida.ca.initserver.InitServerException
 * JD-Core Version:    0.6.0
 */