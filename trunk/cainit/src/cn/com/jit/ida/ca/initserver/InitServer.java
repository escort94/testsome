package cn.com.jit.ida.ca.initserver;

import cn.com.jit.ida.IDAException;
import cn.com.jit.ida.ca.StartServer;
import cn.com.jit.ida.ca.config.AutoServiceConfig;
import cn.com.jit.ida.ca.config.CAConfig;
import cn.com.jit.ida.ca.config.DBConfig;
import cn.com.jit.ida.ca.config.GlobalConfig;
import cn.com.jit.ida.ca.config.InternalConfig;
import cn.com.jit.ida.ca.config.KMCConfig;
import cn.com.jit.ida.ca.config.LDAPConfig;
import cn.com.jit.ida.ca.config.LOGConfig;
import cn.com.jit.ida.ca.config.ServerConfig;
import cn.com.jit.ida.ca.ctml.CTMLManager;
import cn.com.jit.ida.ca.db.DBException;
import cn.com.jit.ida.ca.db.DBManager;
import cn.com.jit.ida.ca.issue.ISSException;
import cn.com.jit.ida.ca.issue.initschema.InitLDAPSchema;
import cn.com.jit.ida.globalconfig.ConfigException;
import cn.com.jit.ida.globalconfig.ConfigFromDB;
import cn.com.jit.ida.globalconfig.ConfigTool;
import cn.com.jit.ida.globalconfig.ParseXML;
import cn.com.jit.ida.log.LogManager;
import cn.com.jit.ida.log.SysLogger;
import cn.com.jit.ida.privilege.Privilege;
import cn.com.jit.ida.privilege.PrivilegeException;
import cn.com.jit.ida.privilege.TemplateAdmin;
import cn.com.jit.ida.util.pki.PKIException;
import cn.com.jit.ida.util.pki.cipher.JCrypto;
import cn.com.jit.ida.util.pki.cipher.Session;
import cn.com.jit.ida.util.xml.XMLTool;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import org.w3c.dom.Element;

public class InitServer {
	ParseXML init;
	boolean isInit = true;
	Session session;
	String deviceName;
	boolean useKey = false;

	public InitServer() throws InitServerException {
		try {
			this.init = new ParseXML("./config/init.xml");
			this.isInit = true;
		} catch (ConfigException localConfigException) {
			throw new InitServerException(localConfigException.getErrCode(),
					localConfigException.getErrDescEx(), localConfigException);
		}
		JCrypto localJCrypto = JCrypto.getInstance();
		try {
			localJCrypto.initialize("JSOFT_LIB", null);
			this.deviceName = this.init.getString("CASigningDeviceID");
			if (!this.deviceName.equalsIgnoreCase("JSOFT_LIB"))
				localJCrypto.initialize(this.deviceName, new Integer(2));
			this.session = localJCrypto.openSession(this.deviceName);
		} catch (PKIException localPKIException) {
			throw new InitServerException(localPKIException.getErrCode(),
					localPKIException.getErrDescEx(), localPKIException);
		}
	}

	public void readInitConfig() throws InitServerException {
		InitServerConfig localInitServerConfig = new InitServerConfig(this.init);
		localInitServerConfig.run();
	}

	public void dealInitData() throws InitServerException {
		try {
			InternalConfig.getInstance();
			XML2DB localXML2DB = new XML2DB(this.init);
			//生成配置文件
			localXML2DB.run_file();
			DBConfig localDBConfig = DBConfig.getInstance();
			LogManager localLogManager = new LogManager();
			LogManager.init();
			LDAPConfig.getInstance();
			LOGConfig.getInstance();
			ServerConfig.getInstance();
			KMCConfig.getInstance();
			AutoServiceConfig.getInstance();
			//初始化数据库
			initDB();
			//部分配置文件的数据存入到数据库
			localXML2DB.run_DB();
		} catch (InitServerException localInitServerException) {
			throw localInitServerException;
		} catch (IDAException localIDAException) {
			throw new InitServerException(localIDAException.getErrCode(),
					localIDAException.getErrDesc(), localIDAException);
		}
	}

	public void initRootCert() throws InitServerException {
		Object localObject;
		if (!this.useKey) {
			localObject = new InitRootCert();
			//主要操作方法
			((InitRootCert) localObject).run(this.session, this.init);
		} else {
			localObject = this.init.getString("SigningKeyStore");
			File localFile = new File((String) localObject);
			if (!localFile.exists())
				throw new InitServerException("0999",
						"预置签名证书密钥容器文件不存在,请检查init.xml中<SigningKeyStore>项配置是否正确");
		}
		try {
			//相当于做了一个初始化
			localObject = CAConfig.getInstance();
		} catch (IDAException localIDAException) {
			throw new InitServerException(localIDAException.getErrCode(),
					localIDAException.getErrDesc(), localIDAException);
		}
	}

	public void initDB() throws InitServerException {
		try {
			DBInit localDBInit = DBInit.getInstance();
			localDBInit.createDB(null, null);
			DBManager.close();
		} catch (DBException localDBException) {
			throw new InitServerException(localDBException.getErrCode(),
					localDBException.getErrDesc(), localDBException);
		}
//		try {
//			Privilege localPrivilege = Privilege.getInstance();
//			TemplateAdmin localTemplateAdmin = TemplateAdmin.getInstance();
//			localPrivilege.refreshAdminInfo();
//			localTemplateAdmin.refreshAdminInfo();
//		} catch (PrivilegeException localPrivilegeException) {
//			throw new InitServerException(localPrivilegeException.getErrCode(),
//					localPrivilegeException.getErrDesc(),
//					localPrivilegeException);
//		}
	}

	public void initCTML() throws InitServerException {
		try {
			AddRAAdminCTML localAddRAAdminCTML = new AddRAAdminCTML();
			localAddRAAdminCTML.run();
			AddAdminCTML localAddAdminCTML = new AddAdminCTML();
			localAddAdminCTML.run();
			AddComCTML localAddComCTML = new AddComCTML();
			localAddComCTML.run();
			AddCrossCertCTML localAddCrossCertCTML = new AddCrossCertCTML();
			localAddCrossCertCTML.run();
			AddAcrossCertCTML localAddAcrossCertCTML = new AddAcrossCertCTML();
			localAddAcrossCertCTML.run();
			AddDomainControllerCTML localAddDomainControllerCTML = new AddDomainControllerCTML();
			localAddDomainControllerCTML.run();
			AddSmartLogonCTML localAddSmartLogonCTML = new AddSmartLogonCTML();
			localAddSmartLogonCTML.run();
			AddTSAServerCTML localAddTSAServerCTML = new AddTSAServerCTML();
			localAddTSAServerCTML.run();
			AddOCSPCTML localAddOCSPCTML = new AddOCSPCTML();
			localAddOCSPCTML.run();
			AddCodeSignCTML localAddCodeSignCTML = new AddCodeSignCTML();
			localAddCodeSignCTML.run();
			AddSSLServerCTML localAddSSLServerCTML = new AddSSLServerCTML();
			localAddSSLServerCTML.run();
			AddEncryptCTML localAddEncryptCTML = new AddEncryptCTML();
			localAddEncryptCTML.run();
			AddSignCTML localAddSignCTML = new AddSignCTML();
			localAddSignCTML.run();
			AddCurrencyCTML localAddCurrencyCTML = new AddCurrencyCTML();
			localAddCurrencyCTML.run();
		} catch (IDAException localIDAException) {
			throw new InitServerException(localIDAException.getErrCode(),
					localIDAException.getErrDesc(), localIDAException);
		}
	}

	public void initComCert() throws InitServerException {
//		try {
//			Privilege localPrivilege1 = Privilege.getInstance();
//			localPrivilege1.setPrivilegeSpecial();
//			TemplateAdmin localObject1 = TemplateAdmin.getInstance();
//			((TemplateAdmin) localObject1).setTemplateAdminSpecial();
//		} catch (PrivilegeException localPrivilegeException1) {
//			throw new InitServerException(
//					localPrivilegeException1.getErrCode(),
//					localPrivilegeException1.getErrDesc(),
//					localPrivilegeException1);
//		}
		//制作 commCert.jks
		String str1 = this.init.getString("BaseDN");
		Object localObject1 = this.init.getString("ServerAddress");
		String str2 = "CN=" + (String) localObject1 + "," + str1;
		String str3 = this.init.getString("CommCertValidity");
		GenComCert localGenComCert = new GenComCert(str2, this.session, str3);
		if (this.init.getString("CACommDeviceID").equalsIgnoreCase("JSOFT_LIB")) {
			String str4 = this.init.getString("CommKeyStore");
			File localFile = new File(str4);
			Object localObject2 = null;
			char[] arrayOfChar = this.init.getString("CommKeyStorePWD")
					.toCharArray();
			localGenComCert.SavaKeyStore(str4, arrayOfChar);
		} else {
			boolean bool = false;
			try {
				bool = localGenComCert.importCert(false);
			} catch (IDAException localIDAException) {
				throw new InitServerException(localIDAException.getErrCode(),
						localIDAException.getErrDesc(), localIDAException
								.getHistory());
			}
			if (!bool)
				throw new InitServerException("0979", "向加密机设备导入通信证书失败");
		}
//		try {
//			Privilege localPrivilege2 = Privilege.getInstance();
//			localPrivilege2.setPrivilegeNormal();
//			TemplateAdmin localTemplateAdmin = TemplateAdmin.getInstance();
//			localTemplateAdmin.setTemplateAdminNormal();
//		} catch (PrivilegeException localPrivilegeException2) {
//			throw new InitServerException(
//					localPrivilegeException2.getErrCode(),
//					localPrivilegeException2.getErrDesc(),
//					localPrivilegeException2);
//		}
	}

	public void initAdmin() throws InitServerException {
		String str1 = this.init.getString("AdminKeyStorePath");
		if (str1.equalsIgnoreCase(""))
			str1 = "./keystore/superAdmin.pfx";
		String str2 = this.init.getString("AdminCertPath");
		if (str2.equalsIgnoreCase(""))
			str2 = "./keystore/superAdmin.p7b";
		String str3 = this.init.getString("BaseDN");
		String str4 = this.init.getString("AdminName");
		String dn = "CN=" + str4 + "," + str3;
		String password = this.init.getString("AdminKeyStorePWD");
		String str7 = this.init.getString("AdminCertReqPath");
		String validityNum = this.init.getString("AdminCertValidity");
		try {
			this.init.getNumber("AdminCertValidity");
			this.init.getNumber("AuditAdminCertValidity");
		} catch (IDAException localIDAException1) {
			throw new InitServerException(localIDAException1.getErrCode(),
					localIDAException1.getErrDesc(), localIDAException1
							.getHistory());
		}
		Object localObject1;
		Object localObject2;
		if (str7.equalsIgnoreCase("")) {
			localObject1 = new GenAdmin(dn, this.session, validityNum, "SUPER_ADMIN");
			((GenAdmin) localObject1).SavaPKCS12(str1, password.toCharArray());
		} else {
			localObject1 = null;
			localObject2 = null;
			try {
				localObject1 = new FileInputStream(str7);
				byte[] arrayOfByte = new byte[((FileInputStream) localObject1)
						.available()];
				((FileInputStream) localObject1).read(arrayOfByte);
				((FileInputStream) localObject1).close();
				localObject2 = new String(arrayOfByte);
			} catch (FileNotFoundException localFileNotFoundException) {
				throw new InitServerException("0999",
						"超级管理员证书申请书文件不存在,请重新检查 <AdminCertReqPath> 项配置是否正确.");
			} catch (IOException localIOException) {
				throw new InitServerException("0999", "初始化超级管理员异常:"
						+ localIOException.toString());
			}
			GenAdmin localGenAdmin = new GenAdmin(dn, (String) localObject2,
					this.session, validityNum, "SUPER_ADMIN");
			localGenAdmin.SavaCert(str2);
		}
		try {
			localObject1 = GlobalConfig.getInstance();
			localObject2 = ((GlobalConfig) localObject1).getCAConfig();
			if (str7.equalsIgnoreCase(""))
				((CAConfig) localObject2).setAdminKeyStorePath(str1);
			else
				((CAConfig) localObject2).setAdminKeyStorePath("");
		} catch (IDAException localIDAException2) {
			throw new InitServerException(localIDAException2.getErrCode(),
					localIDAException2.getErrDesc(), localIDAException2
							.getHistory());
		}
	}

	public void initAuditAdmin() throws InitServerException {
		String str1 = this.init.getString("AuditAdminKeyStorePath");
		if (str1.equalsIgnoreCase(""))
			str1 = "./keystore/auditAdmin.pfx";
		String str2 = this.init.getString("AuditAdminCertPath");
		if (str2.equalsIgnoreCase(""))
			str2 = "./keystore/auditAdmin.p7b";
		try {
			Privilege localPrivilege = Privilege.getInstance();
			localPrivilege.setPrivilegeSpecial();
			TemplateAdmin localObject1 = TemplateAdmin.getInstance();
			((TemplateAdmin) localObject1).setTemplateAdminSpecial();
		} catch (PrivilegeException localPrivilegeException1) {
			throw new InitServerException(
					localPrivilegeException1.getErrCode(),
					localPrivilegeException1.getErrDesc(),
					localPrivilegeException1);
		}
		String str3 = this.init.getString("BaseDN");
		Object localObject1 = this.init.getString("AuditAdminName");
		String str4 = "CN=" + (String) localObject1 + "," + str3;
		String str5 = this.init.getString("AuditAdminKeyStorePWD");
		String str6 = this.init.getString("AuditAdminCertReqPath");
		String str7 = this.init.getString("AuditAdminKeyStorePath");
		String str8 = this.init.getString("AuditAdminCertValidity");
		Object localObject2;
		Object localObject3;
		if (str6.equalsIgnoreCase("")) {
			localObject2 = new GenAdmin(str4, this.session, str8, "AUDIT_ADMIN");
			((GenAdmin) localObject2).SavaPKCS12(str1, str5.toCharArray());
		} else {
			localObject2 = null;
			localObject3 = null;
			try {
				localObject2 = new FileInputStream(str6);
				byte[] arrayOfByte = new byte[((FileInputStream) localObject2)
						.available()];
				((FileInputStream) localObject2).read(arrayOfByte);
				((FileInputStream) localObject2).close();
				localObject3 = new String(arrayOfByte);
			} catch (FileNotFoundException localFileNotFoundException) {
				throw new InitServerException("0999",
						"审计管理员证书申请书文件不存在,请重新检查 <AuditAdminCertReqPath> 项配置是否正确.");
			} catch (IOException localIOException) {
				throw new InitServerException("0999", "初始化超级管理员异常:"
						+ localIOException.toString());
			}
			GenAdmin localGenAdmin = new GenAdmin(str4, (String) localObject3,
					this.session, str8, "AUDIT_ADMIN");
			localGenAdmin.SavaCert(str2);
		}
		try {
			localObject2 = Privilege.getInstance();
			((Privilege) localObject2).setPrivilegeNormal();
			localObject3 = TemplateAdmin.getInstance();
			((TemplateAdmin) localObject3).setTemplateAdminNormal();
		} catch (PrivilegeException localPrivilegeException2) {
			throw new InitServerException(
					localPrivilegeException2.getErrCode(),
					localPrivilegeException2.getErrDesc(),
					localPrivilegeException2);
		}
		try {
			GlobalConfig localGlobalConfig = GlobalConfig.getInstance();
			localObject3 = localGlobalConfig.getCAConfig();
			if (str6.equalsIgnoreCase(""))
				((CAConfig) localObject3).setAuditAdminKeyStorePath(str1);
			else
				((CAConfig) localObject3).setAuditAdminKeyStorePath("");
		} catch (IDAException localIDAException) {
			throw new InitServerException(localIDAException.getErrCode(),
					localIDAException.getErrDesc(), localIDAException
							.getHistory());
		}
	}

	public void run() throws InitServerException {
		//验证配置文件正确性 init.xml文件 全部信息
		readInitConfig();
		//生成CAConfig.xml配置文件 初始化数据库 将配置文件中部分信息存入到数据库
		dealInitData();
//		try {
//			//刷新数据
//			Privilege localPrivilege1 = Privilege.getInstance();
//			TemplateAdmin localObject1 = TemplateAdmin.getInstance();
//			localPrivilege1.refreshAdminInfo();
//			localObject1.refreshAdminInfo();
//		} catch (PrivilegeException localPrivilegeException) {
//			throw new InitServerException(localPrivilegeException.getErrCode(),
//					localPrivilegeException.getErrDesc(),
//					localPrivilegeException);
//		}
		LogManager localLogManager = new LogManager();
		LogManager.init();
		SysLogger localObject1 = LogManager.getSysLogger();
		localObject1.info("日志系统初始化成功");
		if (initLDAP()){
			localObject1.info("LDAP初始化成功");
		}
		//制作根证书signingCert.jks
		initRootCert();
		localObject1.info("根证书初始化成功");
		StartServer localStartServer = new StartServer();
		localStartServer.run_without_service();
		//增加模板 注入到数据库
//		initCTML();
		localObject1.info("模板初始化成功---后期将注入模板写入到sql文件当中");
//		try {
//			CTMLManager localCTMLManager = CTMLManager.getInstance();
//			GlobalConfig localObject2 = GlobalConfig.getInstance();
//			localCTMLManager.updateCTMLStatus(localObject2.getInternalConfig()
//					.getRaadminTemplateName(), "USING");
//		} catch (IDAException localIDAException1) {
//			throw new InitServerException(localIDAException1.getErrCode(),
//					localIDAException1.getMessage(), localIDAException1
//							.getHistory());
//		}
		//制作 commCert.jks
		initComCert();
		localObject1.info("通信证书初始化成功");
		//制作超级管理员pfx证书 并且插入到数据库
		initAdmin();
		localObject1.info("超级管理员证书初始化成功");
		String str = this.init.getString("AdminName");
		Object localObject2 = this.init.getString("AuditAdminName");
		if (str.equalsIgnoreCase((String) localObject2)) {
			try {
				CAConfig localCAConfig = CAConfig.getInstance();
				Privilege localPrivilege2 = Privilege.getInstance();
				localPrivilege2.setAuditAdmin(localCAConfig.getCaAdminSN(),
						localCAConfig.getCaAdminDN());
				localObject1.info("审计管理员证书初始化成功");
			} catch (IDAException localIDAException2) {
				localObject1.info("审计管理员证书初始化失败");
				localObject1.info("init audit administrator error: "
						+ localIDAException2.toString());
			}
		} else {
			//生成审计管理员pfx证书 并且插入到数据库
			initAuditAdmin();
			localObject1.info("审计管理员证书初始化成功");
		}
		//将配置文件的SigningCertExtension信息存入到数据库
		savaExtension();
		//配置文件的处理
		deleteInitFile();
	}

	public void savaExtension() throws InitServerException {
		Element localElement = this.init.getElement("SigningCertExtension");
		XMLTool localXMLTool = new XMLTool();
		String str = new String(XMLTool.xmlSerialize(localElement));
		try {
			DBManager localDBManager = DBManager.getInstance();
			ConfigFromDB localConfigFromDB = new ConfigFromDB(localDBManager,
					"SigningCertExtension");
			localConfigFromDB.setString("SigningCertExtension", str);
		} catch (IDAException localIDAException) {
			throw new InitServerException(localIDAException.getErrCode(),
					localIDAException.getErrDesc(), localIDAException);
		}
	}

	public void deleteInitFile() throws InitServerException {
		File localFile1 = new File("./config/init.xml");
		if (!ConfigTool.getYesOrNo(
				"是否删除配置文件init.xml?\n[Y]删除；\n[N]不删除，该文件将被修改为init.xml.bak。", "Y")) {
			File localFile2 = new File("./config/init.xml.bak");
			if (localFile2.exists())
				localFile2.delete();
			localFile1.renameTo(new File("./config/init.xml.bak"));
			return;
		}
		if (!localFile1.delete())
			System.out.println("删除配置文件失败。");
	}

	private boolean initLDAP() throws InitServerException {
		if (this.init.getString("InitLDAPSchema").equalsIgnoreCase("true")) {
			InitLDAPSchema localInitLDAPSchema = new InitLDAPSchema();
			try {
				localInitLDAPSchema.init();
			} catch (ISSException localISSException) {
				throw new InitServerException(localISSException.getErrCode(),
						localISSException.getErrDescEx(), localISSException);
			}
			return true;
		}
		return false;
	}

	public void setUseKey(boolean paramBoolean) {
		this.useKey = paramBoolean;
	}
}

/*
 * Location: C:\Program Files\JIT\CA50\lib\IDA\ida.jar Qualified Name:
 * cn.com.jit.ida.ca.initserver.InitServer JD-Core Version: 0.6.0
 */