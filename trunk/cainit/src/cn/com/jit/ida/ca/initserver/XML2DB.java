package cn.com.jit.ida.ca.initserver;

import cn.com.jit.ida.IDAException;
import cn.com.jit.ida.ca.config.CAConfigConstant;
import cn.com.jit.ida.ca.db.DBException;
import cn.com.jit.ida.ca.db.DBManager;
import cn.com.jit.ida.globalconfig.Information;
import cn.com.jit.ida.globalconfig.ParseXML;
import cn.com.jit.ida.globalconfig.ProtectConfig;
import cn.com.jit.ida.util.pki.cert.X509Cert;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class XML2DB
{
  ParseXML config;
  ParseXML caConfig;
  DBInit dbInit;
  DBManager dbManager;

  public XML2DB(ParseXML paramParseXML)
    throws IDAException
  {
    this.config = paramParseXML;
  }

  private void savaToDB(String paramString1, String paramString2)
    throws IDAException
  {
    String str1 = this.config.getString(paramString2);
    Information localInformation;
    if (this.config.CryptoProperty(paramString2) == 0)
    {
      localInformation = new Information(paramString2, str1, "N");
    }
    else
    {
      String str2 = null;
      str2 = ProtectConfig.getInstance().Encrypto(str1);
      localInformation = new Information(paramString2, str2, "Y");
    }
    this.dbManager.setConfig(paramString1, localInformation);
  }

  private void savaToXML(String paramString)
    throws IDAException
  {
    String str = this.config.getString(paramString);
    this.caConfig.setString(paramString, str);
  }

  public void run_file()
    throws InitServerException
  {
    try
    {
      this.caConfig = new ParseXML("./config/CAConfig.xml");
      savaToXML("CASigningDeviceID");
      savaToXML("SigningKeyStore");
      String str = this.config.getString("SigningKeyStorePWD");
      if (((str != null) || (!str.equals(""))) && (CAConfigConstant.SaveSignPwd.equals("0")))
        savaToXML("SigningKeyStorePWD");
      savaToXML("Driver_URL");
      savaToXML("Driver_Class");
      savaToXML("DBUser");
      savaToXML("DBUser_PWD");
      savaToXML("Maximum_Connection_Count");
      savaToXML("ServiceTimeOut");
      savaToXML("CACommDeviceID");
      savaToXML("CACommKeyID");
      savaToXML("CommKeyStore");
      savaToXML("CommKeyStorePWD");
      savaToXML("ServerAddress");
      savaToXML("ServicePort");
      savaToXML("ServerType");
      savaToXML("MaxProcessThread");
      savaToXML("AcceptThreadCount");
      savaToXML("ControlPort");
      savaToXML("WebServerPort");
      savaToXML("WebAppBasePath");
      savaToXML("WebAppPath");
      savaToXML("WebAppDocBasePath");
      savaToXML("WebHomePath");
      savaToXML("WebType");
      savaToXML("WebSessionTimeOut");
      savaToXML("SecureWebServerPort");
      savaToXML("SecureWebAppBasePath");
      savaToXML("SecureWebAppPath");
      savaToXML("SecureWebAppDocBasePath");
      savaToXML("SecureWebHomePath");
      savaToXML("SecureWebSessionTimeOut");
      savaToXML("LDAPServerAddress");
      savaToXML("LDAPPort");
      savaToXML("LDAPUserDN");
      savaToXML("LDAP_USER_Password");
      savaToXML("CountPerPage");
      savaToXML("MaxCountPerPage");
      savaToXML("CertCountInCRL");
      savaToXML("Synchrointerval");
      savaToXML("AuthCodeValidity");
      savaToXML("CertSNLength");
      savaToXML("AuthCodeLength");
      savaToXML("MaxCountPerPage");
      savaToXML("CASigningKeyID");
      savaToXML("KMCServerAddress");
      savaToXML("KMCServerPort");
      savaToXML("CRLPeriods");
      savaToXML("CRLFilePublish");
      savaToXML("CRLLDAPPublish");
      savaToXML("PUBALLCRL");
      savaToXML("CRLFilePath");
      savaToXML("CRLLDAPPath");
      savaToXML("CDP_URI");
      savaToXML("CDP_URI_Publish");
      savaToXML("CDP_DN_Publish");
      savaToXML("CDP_LDAP_URI_Publish");
      savaToXML("CDP_LDAP_URI");
      savaToXML("AuthorityInfoAccess");
      savaToXML("CertPubPeriods");
      savaToXML("CertFilePath");
      savaToXML("CASigningAlg");
      savaToXML("TimeDifAllow");
      savaToXML("RootCertFilePublish");
      savaToXML("RootCertLDAPPublish");
      savaToXML("AdminKeyStorePath");
      savaToXML("LOG_Path");
      savaToXML("EnableUpdatePeriod");
      savaToXML(CAConfigConstant.CSPAdminShowType);
      savaToXML(CAConfigConstant.CSPAdminSpecify);
      savaToXML(CAConfigConstant.CSPUserShowType);
      savaToXML(CAConfigConstant.CSPUserSpecify);
      savaToXML("CheckUpdateServiceInterval");
      savaToXML("UseAutoCertArchive");
      savaToXML("AutoCertArchiveInterval");
      savaToXML("CertAfterDays");
      savaToXML("UseAutoLogArchive");
      savaToXML("AutoLogArchiveInterval");
      savaToXML("LogBeforeDays");
    }
    catch (IDAException localIDAException)
    {
      throw new InitServerException(localIDAException.getErrCode(), localIDAException.getErrDesc(), localIDAException);
    }
  }

  public void run_DB()
    throws InitServerException
  {
    try
    {
      this.dbManager = DBManager.getInstance();
      this.dbInit = DBInit.getInstance();
      this.caConfig = new ParseXML("./config/CAConfig.xml");
      savaToDB("LOGConfig", "LOG_Path");
      savaToDB("CAConfig", "isSendAuthCode");
      savaToDB("CAConfig", "SigningKeyStorePWD");
      savaToDB("CAConfig", "CertificatePolicies");
      savaToDB("CAConfig", "BaseDN");
      //注入数据库数据
      SetCAName();
    }
    catch (IDAException localIDAException)
    {
      if ((localIDAException instanceof InitServerException))
        throw ((InitServerException)localIDAException);
      throw new InitServerException(localIDAException.getErrCode(), localIDAException.getErrDesc(), localIDAException);
    }
  }

  private void SetCAName()
    throws InitServerException
  {
    String str1 = this.config.getString("CACertChainPath");
    String str2 = null;
    str2 = this.config.getString("CASubject");
    if ((str1 != null) && (!str1.trim().equals("")))
    {
      File localFile = new File(str1);
      if (!localFile.exists())
        throw new InitServerException("0999", "CA根证书文件不存在，请检查<CACertChainPath>项配置是否正确");
      String str3 = null;
      FileInputStream localFileInputStream = null;
      try
      {
        localFileInputStream = new FileInputStream(localFile);
        byte[] arrayOfByte = new byte[localFileInputStream.available()];
        localFileInputStream.read(arrayOfByte);
        localFileInputStream.close();
        str3 = new String(arrayOfByte);
      }
      catch (IOException localIOException)
      {
        throw new InitServerException("0999", "读取CA根证书文件出错:" + localIOException.toString());
      }
      P7Bxp localP7Bxp = new P7Bxp(str3);
      String str4 = localP7Bxp.getUserCert().getSubject();
      if (!str2.equalsIgnoreCase(str4))
        throw new InitServerException("0987", "<CACert_CHAIN>项中的证书主题信息与<CASubject>项中标识的不一致，请重新确认CA主题信息");
    }
    Information localInformation = new Information("CASubject", str2, "N");
    try
    {
      this.dbManager.setConfig("CAConfig", localInformation);
    }
    catch (DBException localDBException)
    {
      throw new InitServerException(localDBException.getErrCode(), localDBException.getErrDesc(), localDBException);
    }
  }
}

/* Location:           C:\Program Files\JIT\CA50\lib\IDA\ida.jar
 * Qualified Name:     cn.com.jit.ida.ca.initserver.XML2DB
 * JD-Core Version:    0.6.0
 */