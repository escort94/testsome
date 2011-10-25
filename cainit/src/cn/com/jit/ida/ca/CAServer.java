package cn.com.jit.ida.ca;

import cn.com.jit.ida.IDAException;
import cn.com.jit.ida.ca.certmanager.service.iss.CRLIss;
import cn.com.jit.ida.ca.certmanager.service.iss.LdapRecover;
import cn.com.jit.ida.ca.config.CAConfig;
import cn.com.jit.ida.ca.config.CAConfigConstant;
import cn.com.jit.ida.ca.config.CRLConfig;
import cn.com.jit.ida.ca.config.CryptoConfig;
import cn.com.jit.ida.ca.config.GlobalConfig;
import cn.com.jit.ida.ca.config.InternalConfig;
import cn.com.jit.ida.ca.config.ServerConfig;
import cn.com.jit.ida.ca.control.CommandConstant;
import cn.com.jit.ida.ca.control.ControlServer;
import cn.com.jit.ida.ca.control.SendCommand;
import cn.com.jit.ida.ca.initserver.GenAdmin;
import cn.com.jit.ida.ca.initserver.ImportCert;
import cn.com.jit.ida.ca.initserver.InitServer;
import cn.com.jit.ida.ca.initserver.InitServerException;
import cn.com.jit.ida.ca.initserver.P7Bxp;
import cn.com.jit.ida.ca.initserver.GenConfigFile;
import cn.com.jit.ida.ca.initserver.updataAdmin;
import cn.com.jit.ida.ca.initserver.updataCommCert;
import cn.com.jit.ida.ca.keytools.KeyTools;
import cn.com.jit.ida.ca.updataRootCert.UpdataRootCert;
import cn.com.jit.ida.ca.updataRootCert.UpdataRootException;
import cn.com.jit.ida.globalconfig.ConfigException;
import cn.com.jit.ida.globalconfig.ConfigTool;
import cn.com.jit.ida.globalconfig.ParseXML;
import cn.com.jit.ida.globalconfig.util.JITKeyStore;
import cn.com.jit.ida.log.DebugLogger;
import cn.com.jit.ida.log.LogManager;
import cn.com.jit.ida.log.Operation;
import cn.com.jit.ida.log.OptLogger;
import cn.com.jit.ida.log.SysLogger;
import cn.com.jit.ida.util.pki.PKIToolConfig;
import cn.com.jit.ida.util.pki.Parser;
import cn.com.jit.ida.util.pki.cert.X509Cert;
import cn.com.jit.ida.util.pki.cipher.JCrypto;
import cn.com.jit.ida.util.pki.cipher.Session;
import cn.com.jit.ida.util.pki.encoders.Base64;
import cn.com.jit.ida.util.pki.pkcs.P7B;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.lang.reflect.Constructor;
import java.security.Provider;
import java.security.Security;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.StringTokenizer;
import java.util.prefs.Preferences;

public class CAServer {
	StartServer startServer;
	ControlServer controlServer;
	public static final int Function_CANCEL = 0;
	public static final int Function_RETURN = 1;
	public static final String KEEP = "keep";
	public static final String REMOVE_SUPER = "removeSuper";
	public static final String REMOVE_AUDIT = "removeAudit";
	public static final String ADD_SUPER = "addSuper";
	public static final String ADD_AUDIT = "addAudit";

	public static void main(String[] paramArrayOfString) {
		try {
			Runtime.getRuntime().addShutdownHook(new FinalizeCryptoDevice());
			System.out.println(Version.asString());
			CAServer localCAServer = new CAServer();
			if (paramArrayOfString.length < 1) {
				localCAServer.displayMemu(localCAServer);
			} else {
				if (paramArrayOfString[0]
						.equalsIgnoreCase(CommandConstant.Start)) {
					if (isBeenInit())
						localCAServer.startServer(null);
					return;
				}
				if (paramArrayOfString[0]
						.equalsIgnoreCase(CommandConstant.Version)) {
					localCAServer.Version();
				} else if (paramArrayOfString[0]
						.equalsIgnoreCase(CommandConstant.Debug_2)) {
					localCAServer.sendCommand(CommandConstant.Debug_2);
				} else if (paramArrayOfString[0]
						.equalsIgnoreCase(CommandConstant.Stop)) {
					localCAServer.sendCommand(CommandConstant.Stop);
				} else if (paramArrayOfString[0]
						.equalsIgnoreCase(CommandConstant.InitUseKey)) {
					localCAServer.initServer(true);
					ConfigTool.waitToContinue();
				} else {
					System.out.println("可用参数:");
					System.out.println(CommandConstant.Start + "      :启动服务器");
					System.out.println(CommandConstant.Stop + "       :关闭服务器");
					System.out.println(CommandConstant.Version
							+ "    :显示本软件版本号");
				}
			}
		} catch (Exception localException) {
			System.out.println("发生系统错误，错误如下：");
			localException.printStackTrace();
		}
	}

	void updataRootCert() {
		SysLogger localSysLogger = LogManager.getSysLogger();
		DebugLogger localDebugLogger = LogManager
				.getDebugLogger("cn.com.jit.ida.ca.CAServer");
		StartServer localStartServer = new StartServer();
		try {
			localStartServer.run_without_service();
		} catch (InitServerException localInitServerException) {
			localDebugLogger.appendMsg_L1(localInitServerException.toString());
			localDebugLogger.doLog();
			localSysLogger.info(localInitServerException.getMessage());
			localSysLogger.info("更新根证书失败");
			return;
		}
		CAConfig localCAConfig = null;
		try {
			localCAConfig = CAConfig.getInstance();
		} catch (IDAException localIDAException1) {
			InitServerException localObject = new InitServerException(localIDAException1
					.getErrCode(), localIDAException1.getErrDesc(),
					localIDAException1);
			localDebugLogger.appendMsg_L1(((InitServerException) localObject)
					.toString());
			localDebugLogger.doLog();
			localSysLogger.info(((InitServerException) localObject)
					.getMessage());
			localSysLogger.info("更新根证书失败");
			return;
		}
		String str = localCAConfig.getRootCert().getSubject();
		Object localObject = localCAConfig.getRootCert().getIssuer();
		if (!str.equalsIgnoreCase((String) localObject)) {
			System.out.println("本CA为子CA，不能进行这种操作");
			return;
		}
		UpdataRootCert localUpdataRootCert = new UpdataRootCert();
		Operation localOperation = new Operation();
		localOperation.setOperatorDN("系统管理员");
		localOperation.setOptTime(System.currentTimeMillis());
		try {
			if (localUpdataRootCert.run()) {
				localOperation.setOptType(CommandConstant.UpdateRoot);
				localOperation.setResult(1);
				localSysLogger.info("更新根证书成功,请重新启动服务器。");
				try {
					LogManager.getOPtLogger().info(localOperation);
				} catch (IDAException localIDAException2) {
				}
			}
		} catch (UpdataRootException localUpdataRootException) {
			localDebugLogger.appendMsg_L1(localUpdataRootException.toString());
			localDebugLogger.doLog();
			localSysLogger.info(localUpdataRootException.getMessage());
			localSysLogger.info("更新根证书失败");
			localOperation.setOptType(CommandConstant.UpdateRoot);
			localOperation.setResult(0);
			try {
				LogManager.getOPtLogger().info(localOperation);
			} catch (IDAException localIDAException3) {
			}
		}
	}

	void Version() {
		System.out.println(Version.getAllVersion());
	}

	void initServer(boolean paramBoolean) {
		File localFile = new File("./config/init.xml");
		if (!localFile.exists()) {
			System.out.println("./config/init.xml不存在，CA无法初始化");
			CAConfigConstant.CAInitConfigIsExist = "1";
			return;
		}
		CAConfigConstant.CAInitConfigIsExist = "0";
		if ((isBeenInit_noPrint())
				&& (!ConfigTool
						.getYesOrNo(
								"系统已经初始化，如果重新初始化系统原有数据将会全部丢失！！！\n\n是否确定要重新初始化？[Y/N(默认)]",
								"N")))
			return;
		//制作CAConfig.xml文件样式
		if (!GenConfigFile.makefile())
			return;
		InitServer localInitServer = null;
		try {
			localInitServer = new InitServer();
			if (paramBoolean)
				localInitServer.setUseKey(true);
			//主要操作方法
			localInitServer.run();
		} catch (InitServerException localInitServerException) {
			localInitServerException.printStackTrace();
			if (LogManager.isInitialized()) {
				DebugLogger localObject = LogManager
						.getDebugLogger("cn.com.jit.ida.ca.initserver.CAServer");
				((DebugLogger) localObject)
						.appendMsg_L1(localInitServerException.toString());
				((DebugLogger) localObject).appendMsg_L1("CA初始化失败。");
				((DebugLogger) localObject).doLog();
				((DebugLogger) localObject).doLog();
				SysLogger localSysLogger2 = LogManager.getSysLogger();
				localSysLogger2.info(localInitServerException.getMessage());
				localSysLogger2.info("CA初始化失败。");
			} else {
				System.out.println(localInitServerException.getMessage());
				System.out.println("初始化失败");
			}
			Object localObject = new File("./config/CAConfig.xml");
			if (!((File) localObject).delete()) {
				System.gc();
				((File) localObject).delete();
			}
			return;
		}
		SysLogger localSysLogger1 = LogManager.getSysLogger();
		localSysLogger1.info("CA初始化成功。");
	}

	public void keyTool() {
		try {
			KeyTools localKeyTools = new KeyTools();
			if (localKeyTools.run())
				System.out.println("产生申请成功");
		} catch (InitServerException localInitServerException) {
			System.err.println(localInitServerException.getMessage());
			System.err.println("产生申请失败");
			return;
		}
	}

	public void debugServer_L1() {
		LogManager.startDebugMode_L1();
	}

	public void debugServer_L2() {
		LogManager.startDebugMode_L2();
	}

	public boolean stopDebugServer_L1() {
		if (LogManager.isDebug1()) {
			LogManager.endDebugMode_L1();
			return true;
		}
		return false;
	}

	public boolean stopDebugServer_L2() {
		if (LogManager.isDebug2()) {
			LogManager.endDebugMode_L2();
			return true;
		}
		return false;
	}

	public boolean updateAdmin(String paramString1, boolean paramBoolean,
			String paramString2) {
		DebugLogger localDebugLogger = LogManager
				.getDebugLogger("cn.com.jit.ida.ca.CAServer");
		SysLogger localSysLogger = LogManager.getSysLogger();
		String str3 = null;
		StringTokenizer localStringTokenizer = new StringTokenizer(
				paramString1, "||");
		String str2;
		if (localStringTokenizer.hasMoreTokens())
			str2 = new String(Base64.decode(localStringTokenizer.nextToken()
					.getBytes()));
		else
			return false;
		String str1;
		if (localStringTokenizer.hasMoreTokens())
			str1 = new String(Base64.decode(localStringTokenizer.nextToken()
					.getBytes()));
		else
			return false;
		if (localStringTokenizer.hasMoreTokens())
			str3 = localStringTokenizer.nextToken();
		updataAdmin localupdataAdmin = null;
		try {
			if (paramBoolean)
				localupdataAdmin = new updataAdmin(str1, str2, str3,
						"SUPER_ADMIN", paramString2);
			else
				localupdataAdmin = new updataAdmin(str1, str2, str3,
						"AUDIT_ADMIN", paramString2);
			localupdataAdmin.run();
		} catch (InitServerException localInitServerException) {
			localSysLogger.info(localInitServerException.getMessage());
			if (paramBoolean)
				localSysLogger.info("更新超级管理员证书失败");
			else
				localSysLogger.info("更新审计管理员证书失败");
			localDebugLogger.appendMsg_L1(localInitServerException.toString());
			localDebugLogger.appendMsg_L2(localInitServerException.toString());
			localDebugLogger.doLog();
			return false;
		}
		if (paramBoolean)
			localSysLogger.info("更新超级管理员证书成功");
		else
			localSysLogger.info("更新审计管理员证书成功");
		return true;
	}

	public boolean generateAdmin(String paramString, boolean paramBoolean) {
		DebugLogger localDebugLogger = LogManager
				.getDebugLogger("cn.com.jit.ida.ca.CAServer");
		SysLogger localSysLogger = LogManager.getSysLogger();
		String str3 = null;
		String str4 = null;
		StringTokenizer localStringTokenizer = new StringTokenizer(paramString,
				"||");
		String str2;
		if (localStringTokenizer.hasMoreTokens())
			str2 = new String(Base64.decode(localStringTokenizer.nextToken()
					.getBytes()));
		else
			return false;
		String str1;
		if (localStringTokenizer.hasMoreTokens())
			str1 = new String(Base64.decode(localStringTokenizer.nextToken()
					.getBytes()));
		else
			return false;
		if (localStringTokenizer.hasMoreTokens())
			str3 = localStringTokenizer.nextToken();
		else
			return false;
		if (localStringTokenizer.hasMoreElements())
			str4 = new String(Base64.decode(localStringTokenizer.nextToken()
					.getBytes()));
		else
			return false;
		try {
			CryptoConfig localCryptoConfig = CryptoConfig.getInstance();
			Session localSession = JCrypto.getInstance().openSession(
					localCryptoConfig.getDeviceID());
			SimpleDateFormat localSimpleDateFormat = new SimpleDateFormat(
					"yyyy_MM_dd");
			String str5 = localSimpleDateFormat.format(new Date());
			GenAdmin localGenAdmin;
			if (str3.equalsIgnoreCase("NULL")) {
				if (paramBoolean) {
					localGenAdmin = new GenAdmin(str4, localSession, str2,
							"SUPER_ADMIN");
					localGenAdmin.SavaPKCS12("./keystore/superAdmin" + str5
							+ ".pfx", str1.toCharArray());
					localSysLogger.info("新产生超级管理员证书文件保存在:./keystore/superAdmin"
							+ str5 + ".pfx");
				} else {
					localGenAdmin = new GenAdmin(str4, localSession, str2,
							"AUDIT_ADMIN");
					localGenAdmin.SavaPKCS12("./keystore/auditAdmin" + str5
							+ ".pfx", str1.toCharArray());
					localSysLogger.info("新产生审计管理员证书文件保存在:./keystore/auditAdmin"
							+ str5 + ".pfx");
				}
			} else if (paramBoolean) {
				localGenAdmin = new GenAdmin(str4, str3, localSession, str2,
						"SUPER_ADMIN");
				localGenAdmin.SavaCert("./keystore/superAdmin" + str5 + ".p7b");
				localSysLogger.info("新产生超级管理员证书文件保存在:./keystore/superAdmin"
						+ str5 + ".p7b");
			} else {
				localGenAdmin = new GenAdmin(str4, str3, localSession, str2,
						"AUDIT_ADMIN");
				localGenAdmin.SavaCert("./keystore/auditAdmin" + str5 + ".p7b");
				localSysLogger.info("新产生审计管理员证书文件保存在:./keystore/auditAdmin"
						+ str5 + ".p7b");
			}
		} catch (Exception localException) {
			localSysLogger.info(localException.getMessage());
			if (paramBoolean)
				localSysLogger.info("更新超级管理员证书失败");
			else
				localSysLogger.info("更新审计管理员证书失败");
			localDebugLogger.appendMsg_L1(localException.toString());
			localDebugLogger.appendMsg_L2(localException.toString());
			localDebugLogger.doLog();
			return false;
		}
		if (paramBoolean)
			localSysLogger.info("更新超级管理员证书成功");
		else
			localSysLogger.info("更新审计管理员证书成功");
		return true;
	}

	public void updataCommCert() {
		LogManager.init();
		DebugLogger localDebugLogger = LogManager
				.getDebugLogger("cn.com.jit.ida.ca.CAServer");
		SysLogger localSysLogger = LogManager.getSysLogger();
		updataCommCert localupdataCommCert = new updataCommCert();
		StartServer localStartServer = new StartServer();
		Operation localOperation = new Operation();
		localOperation.setOperatorDN("系统管理员");
		localOperation.setOptTime(System.currentTimeMillis());
		localOperation.setOptType(CommandConstant.UpdateCommuCert);
		try {
			localStartServer.run_without_service();
			if (localupdataCommCert.run()) {
				localSysLogger.info("更新通信证书成功,新证书将在下次启动Server启动时生效.");
				localOperation.setResult(1);
				try {
					LogManager.getOPtLogger().info(localOperation);
				} catch (IDAException localIDAException1) {
				}
			}
		} catch (InitServerException localInitServerException) {
			localSysLogger.info(localInitServerException.getMessage());
			localSysLogger.info("更新通信证书失败");
			localDebugLogger.appendMsg_L1(localInitServerException.toString());
			localDebugLogger.appendMsg_L2(localInitServerException.toString());
			localDebugLogger.doLog();
			localOperation.setResult(0);
			try {
				LogManager.getOPtLogger().info(localOperation);
			} catch (IDAException localIDAException2) {
			}
		}
	}

	public boolean startServer(String paramString) {
		LogManager.init();
		DebugLogger localDebugLogger = LogManager
				.getDebugLogger("cn.com.jit.ida.ca.CAServer");
		SysLogger localSysLogger = LogManager.getSysLogger();
		OptLogger localOptLogger = LogManager.getOPtLogger();
		Operation localOperation = new Operation();
		localOperation.setOperatorDN("系统管理员");
		localOperation.setOptTime(System.currentTimeMillis());
		localOperation.setOptType(CommandConstant.Start);
		this.startServer = new StartServer();
		try {
			addProvider();
			if (!this.startServer.run())
				return false;
		} catch (InitServerException localInitServerException) {
			localSysLogger.info(localInitServerException.getMessage());
			localSysLogger.info("系统启动失败");
			localDebugLogger.appendMsg_L1(localInitServerException.toString());
			localDebugLogger.appendMsg_L2(localInitServerException.toString());
			localDebugLogger.doLog();
			localOperation.setResult(0);
			try {
				localOptLogger.info(localOperation);
			} catch (IDAException localIDAException4) {
			}
			return false;
		}
		localOperation.setResult(1);
		try {
			localOptLogger.info(localOperation);
		} catch (IDAException localIDAException1) {
		}
		if (paramString != null)
			if (paramString.equalsIgnoreCase(CommandConstant.Debug_1)) {
				LogManager.startDebugMode_L1();
				localOperation.setOptType(CommandConstant.Debug_1);
				localOperation.setResult(1);
				try {
					localOptLogger.info(localOperation);
				} catch (IDAException localIDAException2) {
				}
			} else if (paramString.equalsIgnoreCase(CommandConstant.Debug_2)) {
				LogManager.startDebugMode_L2();
				localOperation.setOptType(CommandConstant.Debug_2);
				localOperation.setResult(1);
				try {
					localOptLogger.info(localOperation);
				} catch (IDAException localIDAException3) {
				}
			}
		this.controlServer = new ControlServer(this);
		this.controlServer.run();
		return true;
	}

	public void stopSocketServer() {
		this.startServer.stopSocket();
	}

	public void startSocketServer() {
		this.startServer.startSocket();
	}

	public boolean stopWebServer() {
		return this.startServer.stopWeb();
	}

	public void stopAll() {
		stopSocketServer();
		stopWebServer();
		this.startServer.StopAll();
		if (this.controlServer != null)
			this.controlServer.stopControl();
	}

	public boolean startWebServer() {
		return this.startServer.startWeb();
	}

	public void importCert() {
		String str = null;
		try {
			BufferedReader localBufferedReader = new BufferedReader(
					new InputStreamReader(System.in));
			str = null;
			Object localObject;
			while (true) {
				System.out.print("请输入要导入的证书链文件（*.p7b,quit退出）：");
				try {
					str = localBufferedReader.readLine().trim();
				} catch (IOException localIOException) {
					throw new InitServerException("0999", "系统错误",
							localIOException);
				}
				if (str.equalsIgnoreCase("quit"))
					return;
				localObject = new File(str);
				if (((File) localObject).exists())
					break;
				System.out.println("文件不存在,请重新输入");
			}
			try {
				localObject = new P7B();
				((P7B) localObject).parseP7b(str);
			} catch (Exception localException) {
				throw new InitServerException("0913", "P7B文件内容不正确",
						localException);
			}
			ImportCert localImportCert = new ImportCert(str);
			localImportCert.run();
			System.out.println("导入根证书成功，请重新启动服务器");
		} catch (InitServerException localInitServerException) {
			System.out.println(localInitServerException.getMessage());
			System.out.println("导入根证书失败");
		}
	}

	public void crlDefine() {
		String str1 = null;
		try {
			CryptoConfig localCryptoConfig = CryptoConfig.getInstance();
			str1 = localCryptoConfig.getDeviceID();
			JCrypto localObject = JCrypto.getInstance();
			((JCrypto) localObject).initialize(str1, null);
		} catch (Exception localException1) {
			System.out.println("初始化加密设备[" + str1 + "]失败:"
					+ localException1.toString());
		}
		CRLIss localCRLIss = new CRLIss();
		System.out.print("是否重新定义CRL发布周期([Y/N(默认)])");
		Object localObject = new BufferedReader(
				new InputStreamReader(System.in));
		String str2;
		try {
			str2 = ((BufferedReader) localObject).readLine().trim();
		} catch (IOException localIOException1) {
			return;
		}
		if ((str2 == null) || (str2.equalsIgnoreCase("N"))
				|| (str2.trim().equals("")) || (str2.equalsIgnoreCase("Y"))) {
			if ((str2 == null) || (str2.equalsIgnoreCase("N"))
					|| (str2.trim().equals("")))
				localCRLIss.issCRL();
			if (str2.equalsIgnoreCase("Y")) {
				System.out.print("请输入时间(单位分钟)(quit退出):");
				BufferedReader localBufferedReader = new BufferedReader(
						new InputStreamReader(System.in));
				String str3;
				try {
					str3 = localBufferedReader.readLine();
				} catch (IOException localIOException2) {
					return;
				}
				if (str3.equalsIgnoreCase("quit"))
					return;
				int i = 1;
				for (int j = 0; j < str3.length(); j++) {
					int k = str3.charAt(j);
					if ((j == 0) && (k == 48))
						i = 0;
					if ((k >= 48) && (k <= 57))
						continue;
					i = 0;
				}
				if ((str3 == null) || (str3.equals("")))
					i = 0;
				if (i != 0) {
					long l = -1L;
					try {
						l = Long.parseLong(str3);
					} catch (Exception localException2) {
					}
					if ((l < 0L) || (l > 525600L)) {
						System.out.println("CRL发布周期超出有效范围，可设置范围（0-525600）");
						return;
					}
					localCRLIss.issCRL(str3);
					try {
						InternalConfig localInternalConfig = InternalConfig
								.getInstance();
						if (localInternalConfig.isChangeCRLIssPeriod()) {
							CRLConfig.getInstance().setPeriods(l * 60000L);
							sendCommand(CommandConstant.UpdateCRLInterval + ":"
									+ str3);
						}
					} catch (Exception localException3) {
					}
				} else {
					System.out.println("请输入合法的发布周期");
					crlDefine();
				}
			}
		} else {
			System.out.println("请输入合法的发布周期");
			return;
		}
	}

	public void importKMCCert() {
		LogManager.init();
		SysLogger localSysLogger = LogManager.getSysLogger();
		DebugLogger localDebugLogger = LogManager
				.getDebugLogger("cn.com.jit.ida.ca.CAServer");
		OptLogger localOptLogger = LogManager.getOPtLogger();
		Operation localOperation = new Operation();
		localOperation.setOperatorDN("系统管理员");
		localOperation.setOperatorSN("系统管理员");
		localOperation.setOptTime(System.currentTimeMillis());
		localOperation.setOptType(CommandConstant.ImportKMCCert);
		int i = 0;
		try {
			System.out.print("请输入要导入的证书链文件（*.p7b,quit退出）：");
			BufferedReader localBufferedReader = new BufferedReader(
					new InputStreamReader(System.in));
			String localObject1 = null;
			try {
				localObject1 = localBufferedReader.readLine();
			} catch (IOException localIOException) {
				System.out.println("读取用户输入错误:" + localIOException.toString());
				if (i == 0)
					try {
						localOptLogger.info(localOperation);
					} catch (IDAException localIDAException6) {
					}
				return;
			}
			if ((localObject1 == null)
					|| (((String) localObject1).trim().equals(""))) {
				System.out.println("用户输入不正确");
				return;
			}
			if (((String) localObject1).equalsIgnoreCase("quit")) {
				System.out.println("用户取消");
				i = 1;
				return;
			}
			byte[] arrayOfByte = null;
			try {
				FileInputStream localFileInputStream = new FileInputStream(
						(String) localObject1);
				arrayOfByte = new byte[localFileInputStream.available()];
				localFileInputStream.read(arrayOfByte);
				localFileInputStream.close();
			} catch (Exception localException) {
				new IDAException(ConfigException.READ_FILE_ERROR, "读文件错误");
			}
			String str = new String(arrayOfByte);
			if (!Parser.isBase64Encode(str.getBytes()))
				throw new IDAException(ConfigException.READ_FILE_ERROR,
						"P7B文件必须为Base64编码");
			P7Bxp localP7Bxp = new P7Bxp(str);
			JITKeyStore localJITKeyStore = new JITKeyStore(false, ServerConfig
					.getInstance().getCommunicateCert(), ServerConfig
					.getInstance().getCommunicateCertPWD().toCharArray());
			X509Cert[] arrayOfX509Cert = localP7Bxp.getCertChain();
			for (int j = 0; j < arrayOfX509Cert.length; j++)
				localJITKeyStore.addJITCert(arrayOfX509Cert[j].getSubject(),
						arrayOfX509Cert[j], true);
			localJITKeyStore.saveToFile();
			localSysLogger.info("导入成功");
			localOperation.setResult(1);
		} catch (IDAException localIDAException3) {
			Object localObject1 = new InitServerException(localIDAException3
					.getErrCode(), localIDAException3.getErrDescEx(),
					localIDAException3);
			localSysLogger.info(((InitServerException) localObject1)
					.getMessage());
			localSysLogger.info("导入证书失败");
			localDebugLogger.appendMsg_L1(((InitServerException) localObject1)
					.toString());
			localDebugLogger.appendMsg_L2(((InitServerException) localObject1)
					.toString());
			localDebugLogger.doLog();
			localOperation.setResult(0);
		} finally {
			if (i == 0)
				try {
					localOptLogger.info(localOperation);
				} catch (IDAException localIDAException7) {
				}
		}
	}

	public boolean sendCommand(String paramString) {
		SendCommand localSendCommand = new SendCommand(paramString);
		return localSendCommand.run();
	}

	public void displayMemu(CAServer paramCAServer) {
		while (true) {
			System.out
					.println("************************************************");
			System.out
					.println("**          JIT SRQ05 CAServer v5.0           **");
			System.out
					.println("************************************************");
			System.out.println("   1.  启动服务器                              ");
			System.out.println("   2.  以实时故障诊断模式启动服务器              ");
			System.out.println("   3.  停止服务器                              ");
			System.out.println("   4.  产生申请书                              ");
			System.out.println("   5.  初始化系统                              ");
			System.out.println("   6.  更新管理员证书                         ");
			System.out.println("   7.  更新服务器通信证书                        ");
			System.out.println("   8.  更新根证书(子CA更新,请使用 4 和 12 操作)     ");
			System.out.println("   9.  进入实时故障诊断模式                     ");
			System.out.println("  10.  退出实时故障诊断模式                     ");
			System.out.println("  11.  显示版本号                              ");
			System.out.println("  12.  导入证书                                ");
			System.out.println("  13.  导入KMC证书                             ");
			System.out.println("  14.  立即重发证书注销列表                     ");
			System.out.println("  15.  向目录服务器恢复证书                      ");
			System.out.println("   0.  退出                                   ");
			System.out
					.println("************************************************");
			System.out.print("请选择所需功能:");
			BufferedReader localBufferedReader = new BufferedReader(
					new InputStreamReader(System.in));
			String str = null;
			try {
				str = localBufferedReader.readLine().trim();
			} catch (IOException localIOException) {
				return;
			}
			if (str.equalsIgnoreCase("1")) {
				if ((isBeenInit()) && (checkSignPwd()))
					paramCAServer.startServer(null);
				ConfigTool.waitToContinue(localBufferedReader);
			} else if (str.equalsIgnoreCase("2")) {
				if ((isBeenInit()) && (checkSignPwd()))
					paramCAServer.startServer(CommandConstant.Debug_1);
				ConfigTool.waitToContinue(localBufferedReader);
			} else if (str.equalsIgnoreCase("3")) {
				if (checkSignPwd()) {
					sendCommand(CommandConstant.Stop);
					ConfigTool.waitToContinue(localBufferedReader);
				}
			} else if (str.equalsIgnoreCase("4")) {
				paramCAServer.keyTool();
				ConfigTool.waitToContinue(localBufferedReader);
			} else if (str.equalsIgnoreCase("5")) {
				paramCAServer.initServer(false);
				ConfigTool.waitToContinue(localBufferedReader);
			} else if (str.equalsIgnoreCase("6")) {
				if (checkSignPwd()) {
					SendMsgToUpdateAdmin();
					ConfigTool.waitToContinue(localBufferedReader);
				}
			} else if (str.equalsIgnoreCase("7")) {
				if ((isBeenInit()) && (checkSignPwd()))
					paramCAServer.updataCommCert();
				ConfigTool.waitToContinue(localBufferedReader);
			} else if (str.equalsIgnoreCase("8")) {
				if ((isBeenInit()) && (checkSignPwd()))
					paramCAServer.updataRootCert();
				ConfigTool.waitToContinue(localBufferedReader);
			} else if (str.equalsIgnoreCase("9")) {
				if (checkSignPwd()) {
					sendCommand(CommandConstant.Debug_1);
					ConfigTool.waitToContinue(localBufferedReader);
				}
			} else if (str.equalsIgnoreCase("10")) {
				if (checkSignPwd()) {
					sendCommand(CommandConstant.StopDebug_1);
					ConfigTool.waitToContinue(localBufferedReader);
				}
			} else if (str.equalsIgnoreCase("11")) {
				paramCAServer.Version();
				ConfigTool.waitToContinue(localBufferedReader);
			} else if (str.equalsIgnoreCase("12")) {
				if ((isBeenInit()) && (checkSignPwd()))
					paramCAServer.importCert();
				ConfigTool.waitToContinue(localBufferedReader);
			} else if (str.equalsIgnoreCase("13")) {
				if ((isBeenInit()) && (checkSignPwd()))
					paramCAServer.importKMCCert();
				ConfigTool.waitToContinue(localBufferedReader);
			} else if (str.equalsIgnoreCase("14")) {
				if ((isBeenInit()) && (checkSignPwd()))
					crlDefine();
				CAConfigConstant.isUserCancel = "1";
				ConfigTool.waitToContinue(localBufferedReader);
			} else if (str.equalsIgnoreCase("15")) {
				if ((isBeenInit()) && (checkSignPwd()))
					issueCert();
				ConfigTool.waitToContinue(localBufferedReader);
			} else {
				if (str.equalsIgnoreCase("0"))
					return;
				System.out.println("请按菜单项选择");
				ConfigTool.waitToContinue(localBufferedReader);
				continue;
			}
			if (CAConfigConstant.isUserCancel.equals("0")) {
				FinalizeCryptoDevice localFinalizeCryptoDevice = new FinalizeCryptoDevice();
				localFinalizeCryptoDevice.doFinalize();
				continue;
			}
			CAConfigConstant.isUserCancel = "0";
		}
	}

	private void SendMsgToUpdateAdmin() {
		if (isBeenInit()) {
			if (!sendCommand("nichileme"))
				return;
			int i = 0;
			CAConfig localCAConfig = null;
			Object localObject2;
			try {
				GlobalConfig localGlobalConfig = GlobalConfig.getInstance();
				localCAConfig = localGlobalConfig.getCAConfig();
				String localObject1 = localCAConfig.getCaAdminDN();
				localObject2 = localCAConfig.getCAAuditAdminDN();
				if (((String) localObject1).equals(localObject2))
					i = 1;
			} catch (IDAException localIDAException) {
				System.out.println("更新管理员操作失败:" + localIDAException.toString());
				return;
			}
			int j = 0;
			if (i != 0)
				while (true) {
					System.out.println();
					System.out.println("原系统超级管理员与审计管理员绑定为同一管理员，请选择更新方式:");
					System.out
							.println("************************************************");
					System.out.println("   1.  保持绑定关系，同时更新超级管理员和审计管理员  ");
					System.out.println("   2.  只单独更新一个管理员，另一管理员作废         ");
					System.out
							.println("************************************************");
					System.out.print("请选择所需功能(quit退出):");
					BufferedReader localObject1 = new BufferedReader(new InputStreamReader(
							System.in));
					localObject2 = null;
					try {
						localObject2 = ((BufferedReader) localObject1)
								.readLine();
					} catch (Exception localException1) {
					}
					if ((localObject2 != null)
							&& (((String) localObject2).trim()
									.equalsIgnoreCase("1"))) {
						j = 1;
						break;
					}
					if ((localObject2 != null)
							&& (((String) localObject2).trim()
									.equalsIgnoreCase("2")))
						break;
					if ((localObject2 != null)
							&& (((String) localObject2).trim()
									.equalsIgnoreCase("quit")))
						return;
					System.out.println("请按菜单项选择");
					ConfigTool.waitToContinue((BufferedReader) localObject1);
				}
			Object localObject1 = null;
			String str;
			if (i != 0) {
				if (j != 0)
					localObject1 = genBindUpdateAdminCommand(true, true);
				else
					while (true) {
						System.out.println("请选择更新管理员类型:");
						System.out
								.println("************************************************");
						System.out
								.println("   1.  更新超级管理员证书                         ");
						System.out
								.println("   2.  更新审计管理员证书                         ");
						System.out
								.println("************************************************");
						System.out.print("请选择所需功能(quit退出):");
						localObject2 = new BufferedReader(
								new InputStreamReader(System.in));
						str = null;
						try {
							str = ((BufferedReader) localObject2).readLine();
						} catch (Exception localException2) {
						}
						if ((str != null) && (str.trim().equalsIgnoreCase("1"))) {
							localObject1 = genBindUpdateAdminCommand(false,
									true);
							break;
						}
						if ((str != null) && (str.trim().equalsIgnoreCase("2"))) {
							localObject1 = genBindUpdateAdminCommand(false,
									false);
							break;
						}
						if ((str != null)
								&& (str.trim().equalsIgnoreCase("quit")))
							return;
						System.out.println("请按菜单项选择");
						ConfigTool
								.waitToContinue((BufferedReader) localObject2);
					}
			} else {
				while (true) {
					System.out.println("请选择更新管理员类型:");
					System.out
							.println("************************************************");
					System.out
							.println("   1.  更新超级管理员证书                         ");
					System.out
							.println("   2.  更新审计管理员证书                         ");
					System.out
							.println("************************************************");
					System.out.print("请选择所需功能(quit退出):");
					localObject2 = new BufferedReader(new InputStreamReader(
							System.in));
					str = null;
					try {
						str = ((BufferedReader) localObject2).readLine();
					} catch (Exception localException3) {
					}
					if ((str != null) && (str.trim().equalsIgnoreCase("1"))) {
						localObject1 = genSingleUpdateAdminCommand(true);
						break;
					}
					if ((str != null) && (str.trim().equalsIgnoreCase("2"))) {
						localObject1 = genSingleUpdateAdminCommand(false);
						break;
					}
					if ((str != null) && (str.trim().equalsIgnoreCase("quit")))
						return;
					System.out.println("请按菜单项选择");
					ConfigTool.waitToContinue((BufferedReader) localObject2);
				}
				if (localObject1 == null)
					return;
			}
			sendCommand((String) localObject1);
		}
	}

	public String genBindUpdateAdminCommand(boolean paramBoolean1,
			boolean paramBoolean2) {
		String str1 = "NULL";
		String str3 = "NULL";
		String str4 = null;
		String str5 = null;
		str3 = ConfigTool.readtoEnd("请输入申请书文件,按Enter键跳过这一步：");
		if (str3 == null) {
			str1 = ConfigTool.getPassword("请输入证书密码:", 6, 16);
			if (str1 == null)
				return null;
		} else if (str3.equalsIgnoreCase("quit")) {
			System.out.println("用户放弃");
			return null;
		}
		String str2 = ConfigTool.getInteger("请输入有效期，如365:", 2147483647, 0, "天");
		if (str2 == null)
			return null;
		if (!ConfigTool.getYesOrNo("确定执行更新管理员操作？[Y/N]", "N"))
			return null;
		if (paramBoolean1)
			str5 = CommandConstant.UpdateAdminBind;
		else if (paramBoolean2)
			str5 = CommandConstant.UpdateSuperRemoveAudit;
		else
			str5 = CommandConstant.UpdateAuditRemoveSuper;
		str4 = str5 + new String(Base64.encode(str2.getBytes())) + "||"
				+ new String(Base64.encode(str1.getBytes())) + "||" + str3;
		return str4;
	}

	public String genSingleUpdateAdminCommand(boolean paramBoolean) {
		String str1 = "NULL";
		String str3 = "NULL";
		String str4 = "NULL";
		String str5 = null;
		String str6 = null;
		int i = 0;
		try {
			CAConfig localCAConfig = CAConfig.getInstance();
			String str7 = localCAConfig.getCaAdminDN();
			String str8 = localCAConfig.getCAAuditAdminDN();
			BufferedReader localBufferedReader;
			if (paramBoolean) {
				if ((str7 == null) || (str7.trim().equals(""))) {
					System.out.print("原系统未设置超级管理员，请输入超级管理员DN:");
					localBufferedReader = new BufferedReader(
							new InputStreamReader(System.in));
					str4 = localBufferedReader.readLine();
					str4 = ConfigTool.formatDN(str4);
					if (str4.equalsIgnoreCase(str8)) {
						if (!ConfigTool
								.getYesOrNo(
										"用户设置的超级管理员DN与系统内原有的审计管理员DN相同，是否将为原审计管理员增加超级管理权限？[Y/N]",
										"N"))
							return null;
						str5 = CommandConstant.UpdateAuditAddSuper;
						return str5;
					}
					str6 = CommandConstant.UpdateAdminGen;
				}
			} else if ((str8 == null) || (str8.trim().equals(""))) {
				System.out.print("原系统未设置审计管理员，请输入审计管理员DN:");
				localBufferedReader = new BufferedReader(new InputStreamReader(
						System.in));
				str4 = localBufferedReader.readLine();
				str4 = ConfigTool.formatDN(str4);
				if (str4.equalsIgnoreCase(str7)) {
					if (!ConfigTool
							.getYesOrNo(
									"用户设置的审计管理员DN与系统内原有的超级管理员DN相同，是否将为原超级管理员增加审计权限？[Y/N]",
									"N"))
						return null;
					str5 = CommandConstant.UpdateSuperAddAudit;
					return str5;
				}
				str6 = CommandConstant.UpdateAuditAdminGen;
			}
		} catch (Exception localException) {
			System.out.println("更新管理员证书操作失败:" + localException.toString());
			return null;
		}
		str3 = ConfigTool.readtoEnd("请输入申请书文件,按Enter键跳过这一步：");
		if (str3 == null) {
			str1 = ConfigTool.getPassword("请输入证书密码:", 6, 16);
			if (str1 == null)
				return null;
		} else if (str3.equalsIgnoreCase("quit")) {
			System.out.println("用户放弃");
			return null;
		}
		String str2 = ConfigTool.getInteger("请输入有效期，如365:", 2147483647, 0, "天");
		if (str2 == null)
			return null;
		if (!ConfigTool.getYesOrNo("确定执行更新管理员操作？[Y/N]", "N"))
			return null;
		if (str6 == null)
			if (paramBoolean)
				str6 = CommandConstant.UpdateAdmin;
			else
				str6 = CommandConstant.UpdateAuditAdmin;
		str5 = str6 + new String(Base64.encode(str2.getBytes())) + "||"
				+ new String(Base64.encode(str1.getBytes())) + "||" + str3
				+ "||" + new String(Base64.encode(str4.getBytes()));
		return str5;
	}

	public static boolean isBeenInit() {
		boolean bool = false;
		bool = isBeenInit_noPrint();
		if (!bool)
			System.out.println("系统没有被初始化，或者无法连接数据库。");
		return bool;
	}

	public static boolean isBeenInit_noPrint() {
		int i = 0;
		boolean bool = false;
		File localFile = new File("./config/CAConfig.xml");
		bool = localFile.exists();
		return bool;
	}

	private void issurCrossCert() {
		int i = 0;
		SysLogger localSysLogger = null;
		LogManager.init();
		localSysLogger = LogManager.getSysLogger();
		DebugLogger localDebugLogger = LogManager
				.getDebugLogger("cn.com.jit.ida.ca.CAServer");
		OptLogger localOptLogger = LogManager.getOPtLogger();
		Operation localOperation = new Operation();
		localOperation.setOperatorDN("系统管理员");
		localOperation.setOperatorSN("系统管理员");
		localOperation.setOptTime(System.currentTimeMillis());
		localOperation.setOptType("-IssueCrossCert");
		LdapRecover localLdapRecover = new LdapRecover();
		try {
			localLdapRecover.issCrossCert();
			localSysLogger.info("发布交叉证书成功");
			localOperation.setResult(1);
		} catch (IDAException localIDAException3) {
			localSysLogger.info(localIDAException3.getMessage());
			localSysLogger.info("发布交叉证书失败");
			localDebugLogger.appendMsg_L1(localIDAException3.toString());
			localDebugLogger.appendMsg_L2(localIDAException3.toString());
			localDebugLogger.doLog();
			localOperation.setResult(0);
		} finally {
			try {
				localOptLogger.info(localOperation);
			} catch (IDAException localIDAException4) {
			}
		}
	}

	private void issueCert() {
		int i = 0;
		SysLogger localSysLogger = null;
		LogManager.init();
		localSysLogger = LogManager.getSysLogger();
		DebugLogger localDebugLogger = LogManager
				.getDebugLogger("cn.com.jit.ida.ca.CAServer");
		OptLogger localOptLogger = LogManager.getOPtLogger();
		Operation localOperation = new Operation();
		localOperation.setOperatorDN("系统管理员");
		localOperation.setOperatorSN("系统管理员");
		localOperation.setOptTime(System.currentTimeMillis());
		int j = ConfigTool.displayMenu(null,
				new String[] { "恢复用户证书", "恢复CA根证书" });
		LdapRecover localLdapRecover;
		if (j == 1) {
			localOperation.setOptType("-IssueUserCert");
			localLdapRecover = new LdapRecover();
			try {
				localLdapRecover.issUserCerts();
				localSysLogger.info("恢复用户证书成功");
				localOperation.setResult(1);
			} catch (IDAException localIDAException2) {
				localSysLogger.info(localIDAException2.getMessage());
				localSysLogger.info("恢复用户证书失败");
				localDebugLogger.appendMsg_L1(localIDAException2.toString());
				localDebugLogger.appendMsg_L2(localIDAException2.toString());
				localDebugLogger.doLog();
				localOperation.setResult(0);
			} finally {
				try {
					localOptLogger.info(localOperation);
				} catch (IDAException localIDAException7) {
				}
			}
		} else if (j == 2) {
			localOperation.setOptType("-IssueCARootCert");
			localLdapRecover = new LdapRecover();
			try {
				localLdapRecover.issCARootCert();
				localSysLogger.info("恢复CA根证书成功");
				localOperation.setResult(1);
			} catch (IDAException localIDAException5) {
				localSysLogger.info(localIDAException5.getMessage());
				localSysLogger.info("恢复CA根证书失败");
				localDebugLogger.appendMsg_L1(localIDAException5.toString());
				localDebugLogger.appendMsg_L2(localIDAException5.toString());
				localDebugLogger.doLog();
				localOperation.setResult(0);
			} finally {
				try {
					localOptLogger.info(localOperation);
				} catch (IDAException localIDAException8) {
				}
			}
		}
	}

	private void addProvider() throws InitServerException {
		try {
			ParseXML localParseXML = new ParseXML("./config/CAConfig.xml");
			String str1 = localParseXML.getString("CACommDeviceID");
			String str2 = System.getProperty("java.vm.vendor");
			Object localObject1;
			if (!str1.equalsIgnoreCase("JSOFT_LIB")) {
				Object localObject2;
				if (str2.toUpperCase().indexOf("SUN") != -1) {
					localObject1 = PKIToolConfig.getP11Config();
					localObject2 = CreateProvider(
							"sun.security.pkcs11.SunPKCS11",
							(String) localObject1);
					Security.addProvider((Provider) localObject2);
				} else if (str2.toUpperCase().indexOf("HEWLETT") != -1) {
					localObject1 = PKIToolConfig.getP11Config();
					localObject2 = CreateProvider(
							"sun.security.pkcs11.SunPKCS11",
							(String) localObject1);
					Security.addProvider((Provider) localObject2);
				} else if (str2.toUpperCase().indexOf("IBM") != -1) {
					localObject1 = Class
							.forName("com.ibm.crypto.pkcs11impl.provider.IBMPKCS11Impl");
					localObject2 = Preferences
							.userNodeForPackage((Class) localObject1);
					String str3 = PKIToolConfig.getP11Library();
					String str4 = PKIToolConfig.getP11SlotListIndex();
					String str5 = str3 + ":" + str4;
					String str6 = PKIToolConfig.getP11Password();
					((Preferences) localObject2).put("IBMPKCSImpl DLL", str5);
					((Preferences) localObject2).put("IBMPKCSImpl password",
							str6);
					Provider localProvider1 = CreateProvider("com.ibm.crypto.pkcs11impl.provider.IBMPKCS11Impl");
					Provider localProvider2 = CreateProvider("com.ibm.jsse2.IBMJSSEProvider2");
					((Preferences) localObject2).remove("IBMPKCSImpl DLL");
					((Preferences) localObject2).remove("IBMPKCSImpl password");
					Security.addProvider(localProvider1);
					Security.addProvider(localProvider2);
				} else {
					throw new Exception("unsupportted JAVA_VM vender:" + str2);
				}
			} else if (str2.toUpperCase().indexOf("IBM") != -1) {
				localObject1 = CreateProvider("com.ibm.jsse2.IBMJSSEProvider2");
				Security.addProvider((Provider) localObject1);
			}
		} catch (Exception localException) {
			throw new InitServerException("0979", "加载通信设备用Provider失败:"
					+ localException.toString(), localException);
		}
	}

	private Provider CreateProvider(String paramString1, String paramString2) {
		Class[] arrayOfClass = { String.class };
		Object[] arrayOfObject = { paramString2 };
		try {
			Class localClass = Class.forName(paramString1);
			Constructor localConstructor = localClass
					.getConstructor(arrayOfClass);
			Provider localProvider = (Provider) createObject(localConstructor,
					arrayOfObject);
			return localProvider;
		} catch (ClassNotFoundException localClassNotFoundException) {
			return null;
		} catch (NoSuchMethodException localNoSuchMethodException) {
		}
		return null;
	}

	private Provider CreateProvider(String paramString) {
		Provider localProvider = (Provider) createObject(paramString);
		return localProvider;
	}

	public static Object createObject(Constructor paramConstructor,
			Object[] paramArrayOfObject) {
		Object localObject = null;
		try {
			localObject = paramConstructor.newInstance(paramArrayOfObject);
			return localObject;
		} catch (Exception localException) {
			System.out.println(localException);
		}
		return localObject;
	}

	static Object createObject(String paramString) {
		Object localObject = null;
		try {
			Class localClass = Class.forName(paramString);
			localObject = localClass.newInstance();
		} catch (InstantiationException localInstantiationException) {
			System.out.println(localInstantiationException);
		} catch (IllegalAccessException localIllegalAccessException) {
			System.out.println(localIllegalAccessException);
		} catch (ClassNotFoundException localClassNotFoundException) {
			System.out.println(localClassNotFoundException);
		}
		return localObject;
	}

	public boolean checkSignPwd() {
		CryptoConfig localCryptoConfig = null;
		try {
			localCryptoConfig = CryptoConfig.getInstance();
		} catch (IDAException localIDAException) {
			System.out.println("初始化配置文件失败！");
			return false;
		}
		String str1 = localCryptoConfig.getRootCertPWD();
		String str2 = localCryptoConfig.getSignPWDIsConfirmed();
		if ((CAConfigConstant.SignPwd == null)
				&& (CAConfigConstant.CAInitConfigIsExist.equals("0"))
				&& (str2.equalsIgnoreCase("true"))) {
			String str3 = ConfigTool.getPassword("CA签名密钥容器密码", 1, 16);
			if (str3 == null) {
				System.out.println("用户取消操作");
				return false;
			}
			if (!str1.equals(str3)) {
				CAConfigConstant.isUserCancel = "1";
				System.out.println("密钥容器密码输入错误");
				return false;
			}
			CAConfigConstant.SignPwd = str1;
			CAConfigConstant.SaveSignPwd = "1";
		}
		return true;
	}
}
