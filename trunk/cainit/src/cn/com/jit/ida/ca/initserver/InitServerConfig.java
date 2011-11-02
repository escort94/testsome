package cn.com.jit.ida.ca.initserver;

import cn.com.jit.ida.globalconfig.ConfigException;
import cn.com.jit.ida.globalconfig.ConfigTool;
import cn.com.jit.ida.globalconfig.ParseXML;
import cn.com.jit.license.IDAFormula;
import cn.com.jit.license.LicenseException;
import java.io.File;

public class InitServerConfig
{
  ParseXML config;

  public InitServerConfig(ParseXML paramParseXML)
  {
    this.config = paramParseXML;
  }

  public void readCryptoConfig()
    throws InitServerException
  {
    String str1 = this.config.getString("CASigningDeviceID");
    if (str1.equalsIgnoreCase(""))
      throw new InitServerException("0901", "加密库设备ID错误");
    String str2 = this.config.getString("SigningKeyStore");
    if (str2.equalsIgnoreCase(""))
      throw new InitServerException("0932", "根密钥存储错误");
    String str3 = this.config.getString("SigningKeyStorePWD");
    if (str3.equalsIgnoreCase(""))
    {
      str3 = ConfigTool.getNewPassword("CA签名密钥容器密码", 6, 16);
      cn.com.jit.ida.ca.config.CAConfigConstant.SaveSignPwd = "1";
      if (str3 == null)
        throw new InitServerException("0988", "用户取消操作");
    }
    cn.com.jit.ida.ca.config.CAConfigConstant.SignPwd = new String(str3);
    try
    {
      this.config.setString("SigningKeyStorePWD", str3);
    }
    catch (ConfigException localConfigException)
    {
      throw new InitServerException("0932", "根密钥存储密码错误");
    }
    if (str3.equalsIgnoreCase(""))
      throw new InitServerException("0932", "根密钥存储密码错误");
  }

  public void readDBConfig()
    throws InitServerException
  {
    String str1;
    try
    {
      str1 = this.config.getString("Driver_URL");
    }
    catch (Exception localException1)
    {
      throw new InitServerException("0902", "数据库驱动的URL错误", localException1);
    }
    if (str1.equalsIgnoreCase(""))
      throw new InitServerException("0902", "数据库驱动的URL错误");
    String str2;
    try
    {
      str2 = this.config.getString("Driver_Class");
    }
    catch (Exception localException2)
    {
      throw new InitServerException("0903", "数据库驱动的Class错误", localException2);
    }
    if (str2.equalsIgnoreCase(""))
      throw new InitServerException("0903", "数据库驱动的Class错误");
    String str3;
    try
    {
      str3 = this.config.getString("DBUser");
    }
    catch (Exception localException3)
    {
      throw new InitServerException("0904", "数据库用户名错误", localException3);
    }
    if (str3.equalsIgnoreCase(""))
      throw new InitServerException("0904", "数据库用户名错误");
    String str4;
    try
    {
      str4 = this.config.getString("DBUser_PWD");
    }
    catch (Exception localException4)
    {
      throw new InitServerException("0905", "数据库密码错误", localException4);
    }
    if (str4.equalsIgnoreCase(""))
      throw new InitServerException("0905", "数据库密码错误");
    int i;
    try
    {
      i = this.config.getNumber("Maximum_Connection_Count");
    }
    catch (Exception localException5)
    {
      throw new InitServerException("0906", "数据库最大连接数错误", localException5);
    }
    if (i < 1)
      throw new InitServerException("0906", "数据库最大连接数错误");
  }

  public void readNetConfig()
    throws InitServerException
  {
    int i;
//    try
//    {
//      i = this.config.getNumber("ServiceTimeOut");
//    }
//    catch (Exception localException1)
//    {
//      throw new InitServerException("0984", "服务器超时时间错误", localException1);
//    }
//    if (i < 1)
//      throw new InitServerException("0984", "服务器超时时间错误");
    int j;
    try
    {
      j = this.config.getNumber("CommCertValidity");
    }
    catch (Exception localException2)
    {
      throw new InitServerException("0979", "通信证书有效期错误", localException2);
    }
    if (j < 1)
      throw new InitServerException("0979", "通信证书有效期错误");
//    int k;
//    try
//    {
//      k = this.config.getNumber("ControlPort");
//    }
//    catch (Exception localException3)
//    {
//      throw new InitServerException("0978", "控制服务器端口错误", localException3);
//    }
//    if (k < 1)
//      throw new InitServerException("0978", "控制服务器端口错误");
//    int m;
//    try
//    {
//      m = this.config.getNumber("ServicePort");
//    }
//    catch (Exception localException4)
//    {
//      throw new InitServerException("0911", "服务器端口错误", localException4);
//    }
//    if (m < 1)
//      throw new InitServerException("0911", "服务器端口错误");
//    try
//    {
//      int n = this.config.getNumber("AcceptThreadCount");
//    }
//    catch (Exception localException5)
//    {
//      throw new InitServerException("0912", "服务器接收线程数量错误", localException5);
//    }
//    if (m < 1)
//      throw new InitServerException("0912", "服务器接收线程数量错误");
//    int i1;
//    try
//    {
//      i1 = this.config.getNumber("MaxProcessThread");
//    }
//    catch (Exception localException6)
//    {
//      throw new InitServerException("0929", "服务器最大处理线程数量错误", localException6);
//    }
//    if (i1 < 1)
//      throw new InitServerException("0929", "服务器最大处理线程数量错误");
//    String str1;
//    try
//    {
//      str1 = this.config.getString("ServerType");
//    }
//    catch (Exception localException7)
//    {
//      throw new InitServerException("0931", "服务器类型错误", localException7);
//    }
//    if ((!str1.equalsIgnoreCase("SOCKET")) && (!str1.equalsIgnoreCase("SSL")))
//      throw new InitServerException("0931", "服务器类型错误");
    String str2;
    try
    {
      str2 = this.config.getString("SigningKeyStore");
    }
    catch (Exception localException8)
    {
      throw new InitServerException("0932", "Web的根证书存储错误", localException8);
    }
    if (str2.equalsIgnoreCase(""))
      throw new InitServerException("0932", "Web的根证书存储错误");
    try
    {
      File localFile1 = new File(new File(str2).getParent());
      localFile1.mkdir();
    }
    catch (Exception localException9)
    {
    }
    String str3;
    try
    {
      str3 = this.config.getString("SigningKeyStorePWD");
    }
    catch (Exception localException10)
    {
      throw new InitServerException("0933", "Web的根证书存储密钥错误", localException10);
    }
    if (str3.equalsIgnoreCase(""))
      throw new InitServerException("0933", "Web的根证书存储密钥错误");
    String str4;
    try
    {
      str4 = this.config.getString("CommKeyStore");
    }
    catch (Exception localException11)
    {
      throw new InitServerException("0934", "Web的通信证书存储错误", localException11);
    }
    if (str4.equalsIgnoreCase(""))
      throw new InitServerException("0934", "Web的通信证书存储错误");
    try
    {
      File localFile2 = new File(new File(str4).getParent());
      localFile2.mkdir();
    }
    catch (Exception localException12)
    {
    }
    String str5;
    try
    {
      str5 = this.config.getString("CommKeyStorePWD");
    }
    catch (Exception localException13)
    {
      throw new InitServerException("0935", "Web的通信证书存储密码错误", localException13);
    }
    if (str5.equalsIgnoreCase(""))
      throw new InitServerException("0935", "Web的通信证书存储密码错误");
//    int i2 = 0;
//    try
//    {
//      i2 = this.config.getNumber("WebServerPort");
//    }
//    catch (Exception localException14)
//    {
//      throw new InitServerException("0936", "服务器端口错误", localException14);
//    }
//    if (i2 < 1)
//      throw new InitServerException("0936", "服务器端口错误");
  }

  public void readLdapConfig()
    throws InitServerException
  {
    String str = null;
    try
    {
      str = this.config.getString("BaseDN");
    }
    catch (Exception localException)
    {
      throw new InitServerException("0941", "BASEDN错误", localException);
    }
    if (str.equalsIgnoreCase(""))
      throw new InitServerException("0941", "BASEDN错误");
  }

  public void readCAInfo()
    throws InitServerException
  {
    int i = 0;
    try
    {
      i = this.config.getNumber("AuthCodeLength");
    }
    catch (Exception localException1)
    {
      throw new InitServerException("0986", "证书授权码长度错误", localException1);
    }
    if ((i < 6) || (i > 40))
      throw new InitServerException("0986", "证书授权码长度错误");
    int j = 0;
    try
    {
      j = this.config.getNumber("CertSNLength");
    }
    catch (Exception localException2)
    {
      throw new InitServerException("0985", "证书序列号长度错误", localException2);
    }
    if ((j < 6) || (j > 40))
      throw new InitServerException("0985", "证书序列号长度错误");
    String str1 = null;
    try
    {
      str1 = this.config.getString("CertFilePath");
    }
    catch (Exception localException3)
    {
      throw new InitServerException("0965", "证书发布路径错误", localException3);
    }
    if (str1.equalsIgnoreCase(""))
      throw new InitServerException("0965", "证书发布路径错误");
    try
    {
      File localFile = new File(str1);
      localFile.mkdir();
    }
    catch (Exception localException4)
    {
    }
    int k = 0;
    try
    {
      k = this.config.getNumber("CountPerPage");
    }
    catch (Exception localException5)
    {
      throw new InitServerException("0966", "查询页面每页显示数量错误", localException5);
    }
    if (k <= 0)
      throw new InitServerException("0966", "查询页面每页显示数量错误");
    String str2 = null;
    try
    {
      str2 = this.config.getString("CACertChainPath");
    }
    catch (Exception localException6)
    {
      throw new InitServerException("0962", "根证书P7B证书链错误", localException6);
    }
    if (!str2.equalsIgnoreCase(""))
      return;
    String str3 = null;
    try
    {
      str3 = this.config.getString("CASigningAlg");
    }
    catch (Exception localException7)
    {
      throw new InitServerException("0922", "签名算法错误", localException7);
    }
    if ((str3.equalsIgnoreCase("")) || ((!str3.equalsIgnoreCase("SM3WITHSM2")) && (!str3.equalsIgnoreCase("SHA1withRSA"))))
      throw new InitServerException("0922", "签名算法错误");
    int m = 0;
    try
    {
      m = this.config.getNumber("SigningCertValidity");
    }
    catch (Exception localException8)
    {
      throw new InitServerException("0963", "CA证书有效期错误", localException8);
    }
    if ((m < 1) || (m > 1825000))
      throw new InitServerException("0982", "CA证书有效期超出允许范围错误");
    String str4 = null;
    try
    {
      str4 = this.config.getString("CASubject");
    }
    catch (Exception localException9)
    {
      throw new InitServerException("0925", "CA名称不能为空", localException9);
    }
    if (str4.equalsIgnoreCase(""))
      throw new InitServerException("0925", "CA名称不能为空");
    String str5 = null;
    str5 = this.config.getString("BaseDN");
    if (!str4.toLowerCase().endsWith(str5.toLowerCase()))
      throw new InitServerException("0976", "CA名称需要符合BaseDN");
//    try
//    {
//      IDAFormula localIDAFormula = IDAFormula.getInstance("./license.lce");
//      if (!localIDAFormula.getSystemValue1().equalsIgnoreCase("SRQ05 5.0"))
//        throw new InitServerException("0974", "License检查产品名称失败");
//      if (!localIDAFormula.getSystemValue2().equalsIgnoreCase(str4))
//        throw new InitServerException("0973", "License检查CA名称失败");
//      if ((!localIDAFormula.getSystemValue3().equals("NULL")) && (Long.decode(localIDAFormula.getSystemValue3()).longValue() < System.currentTimeMillis() / 1000L))
//        throw new InitServerException("0975", "License检查有效期失败");
//    }
//    catch (LicenseException localLicenseException)
//    {
//      throw new InitServerException("0972", "License检查失败");
//    }
    int n = 0;
    try
    {
      n = this.config.getNumber("SigningKeySize");
    }
    catch (Exception localException10)
    {
      throw new InitServerException("0920", "CA密钥长度错误", localException10);
    }
    if ((n < 512) || (n > 4096))
      throw new InitServerException("0920", "CA密钥长度错误");
  }

  public void readLogInfo()
    throws InitServerException
  {
    String str = null;
    try
    {
      str = this.config.getString("LOG_Path");
    }
    catch (Exception localException1)
    {
      throw new InitServerException("0942", "日志文件路径错误", localException1);
    }
    if (str.equalsIgnoreCase(""))
      throw new InitServerException("0942", "日志文件路径错误");
    try
    {
      File localFile = new File(str);
      localFile.mkdir();
    }
    catch (Exception localException2)
    {
    }
  }

  public void readKMC()
    throws InitServerException
  {
    try
    {
      String str = this.config.getString("KMCServerAddress");
    }
    catch (Exception localException1)
    {
      throw new InitServerException("0944", "KMC服务器地址错误", localException1);
    }
    int i = 0;
    try
    {
      i = this.config.getNumber("KMCServerPort");
    }
    catch (Exception localException2)
    {
      throw new InitServerException("0943", "KMC服务器端口错误", localException2);
    }
  }

  public void readCRL()
    throws InitServerException
  {
    String str1;
    try
    {
      str1 = this.config.getString("CRLFilePublish");
    }
    catch (Exception localException1)
    {
      throw new InitServerException("0967", "CRL是否文件发布错误", localException1);
    }
    if ((str1.equalsIgnoreCase("")) || ((!str1.equalsIgnoreCase("true")) && (!str1.equalsIgnoreCase("false"))))
      throw new InitServerException("0967", "CRL是否文件发布错误");
    String str2;
    try
    {
      str2 = this.config.getString("CRLLDAPPublish");
    }
    catch (Exception localException2)
    {
      throw new InitServerException("0968", "CRL是否LDAP发布错误", localException2);
    }
    if ((str2.equalsIgnoreCase("")) || ((!str2.equalsIgnoreCase("true")) && (!str2.equalsIgnoreCase("false"))))
      throw new InitServerException("0968", "CRL是否LDAP发布错误");
    String str3;
    try
    {
      str3 = this.config.getString("CRLFilePath");
    }
    catch (Exception localException3)
    {
      throw new InitServerException("0969", "CRL文件发布路径错误", localException3);
    }
    if (str3.equalsIgnoreCase(""))
      throw new InitServerException("0969", "CRL文件发布路径错误");
    String str4;
    try
    {
      str4 = this.config.getString("CRLLDAPPath");
    }
    catch (Exception localException4)
    {
      throw new InitServerException("0970", "CRL目录发布路径错误", localException4);
    }
    if (str4.equalsIgnoreCase(""))
      throw new InitServerException("0970", "CRL目录发布路径错误");
    String str5;
    try
    {
      str5 = this.config.getString("CDP_URI");
    }
    catch (Exception localException5)
    {
      throw new InitServerException("0971", "CRL发布点URI错误", localException5);
    }
    if (str5.equalsIgnoreCase(""))
      throw new InitServerException("0971", "CRL发布点URI错误");
    int i = 0;
    try
    {
      i = this.config.getNumber("CRLPeriods");
    }
    catch (Exception localException6)
    {
      throw new InitServerException("0945", "CRL发布周期错误", localException6);
    }
    if ((i < 1) || (i > 10000))
      throw new InitServerException("0945", "CRL发布周期错误");
    int j = 0;
    try
    {
      j = this.config.getNumber("CertCountInCRL");
    }
    catch (Exception localException7)
    {
      throw new InitServerException("0946", "每个CRL包含的证书数量错误", localException7);
    }
    if (j < 1)
      throw new InitServerException("0946", "每个CRL包含的证书数量错误");
    String str6;
    try
    {
      str6 = this.config.getString("CDP_LDAP_URI_Publish");
    }
    catch (Exception localException8)
    {
      throw new InitServerException("0968", "CRL是否包含LDAP_URI信息错误", localException8);
    }
    if ((str6.equalsIgnoreCase("")) || ((!str6.equalsIgnoreCase("true")) && (!str6.equalsIgnoreCase("false"))))
      throw new InitServerException("0968", "CRL是否包含LDAP_URI信息错误");
    String str7;
    try
    {
      str7 = this.config.getString("CDP_LDAP_URI");
    }
    catch (Exception localException9)
    {
      throw new InitServerException("0968", "CRL LDAP_URI信息错误", localException9);
    }
    if ((str6.equalsIgnoreCase("true")) && (str7.trim().equals("")))
      throw new InitServerException("0968", "已设置包含CRL LDAP_URI信息，但CRL LDAP_URI信息为空");
  }

  public void readAdmin()
    throws InitServerException
  {
    int i = 0;
    try
    {
      i = this.config.getNumber("AdminCertValidity");
    }
    catch (Exception localException1)
    {
      throw new InitServerException("0980", "管理员证书有效期错误", localException1);
    }
    if (i < 1)
      throw new InitServerException("0980", "管理员证书有效期错误");
    String str1;
    try
    {
      str1 = this.config.getString("AdminName");
    }
    catch (Exception localException2)
    {
      throw new InitServerException("0960", "管理员证书DN错误", localException2);
    }
    if (str1.equalsIgnoreCase(""))
      throw new InitServerException("0960", "管理员证书DN错误");
    String str2;
    try
    {
      str2 = this.config.getString("AdminKeyStorePWD");
    }
    catch (Exception localException3)
    {
      throw new InitServerException("0961", "管理员证书密钥存储错误", localException3);
    }
    if (str2.equalsIgnoreCase(""))
      throw new InitServerException("0961", "管理员证书密钥存储错误");
    String str3;
    try
    {
      str3 = this.config.getString("AdminCertPath");
    }
    catch (Exception localException4)
    {
      throw new InitServerException("0977", "管理员证书存储路径错误", localException4);
    }
    if (str3.equalsIgnoreCase(""))
      throw new InitServerException("0961", "管理员证书存储路径错误");
    try
    {
      File localFile1 = new File(str3);
      File localFile2 = new File(localFile1.getParent());
      localFile2.mkdir();
    }
    catch (Exception localException5)
    {
    }
  }

  public void run()
    throws InitServerException
  {
    readCryptoConfig();
    readDBConfig();
    readLdapConfig();
    readNetConfig();
    readCAInfo();
    readCRL();
    readAdmin();
    readLogInfo();
  }
}

/* Location:           C:\Program Files\JIT\CA50\lib\IDA\ida.jar
 * Qualified Name:     cn.com.jit.ida.ca.initserver.InitServerConfig
 * JD-Core Version:    0.6.0
 */