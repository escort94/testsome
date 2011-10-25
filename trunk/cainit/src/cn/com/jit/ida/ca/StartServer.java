package cn.com.jit.ida.ca;

import cn.com.jit.ida.IDAException;
import cn.com.jit.ida.ca.certmanager.service.PendingTaskService;
import cn.com.jit.ida.ca.certmanager.service.iss.CRLSchedule;
import cn.com.jit.ida.ca.certmanager.service.iss.CertArcSchedule;
import cn.com.jit.ida.ca.certmanager.service.iss.CertSchedule;
import cn.com.jit.ida.ca.config.AutoServiceConfig;
import cn.com.jit.ida.ca.config.CAConfig;
import cn.com.jit.ida.ca.config.CryptoConfig;
import cn.com.jit.ida.ca.config.GlobalConfig;
import cn.com.jit.ida.ca.config.InternalConfig;
import cn.com.jit.ida.ca.config.LoadBalance;
import cn.com.jit.ida.ca.config.ServerConfig;
import cn.com.jit.ida.ca.ctml.CTMLManager;
import cn.com.jit.ida.ca.ctml.SelfExtensionManager;
import cn.com.jit.ida.ca.db.DBException;
import cn.com.jit.ida.ca.db.DBManager;
import cn.com.jit.ida.ca.initserver.InitServerException;
import cn.com.jit.ida.ca.log.service.iss.LogArcSchedule;
import cn.com.jit.ida.ca.socketServer.StartSocket;
import cn.com.jit.ida.ca.webserver.HTTPSServer;
import cn.com.jit.ida.ca.webserver.HTTPServer;
import cn.com.jit.ida.ca.webserver.WebServerException;
import cn.com.jit.ida.log.DebugLogger;
import cn.com.jit.ida.log.LogManager;
import cn.com.jit.ida.log.SysLogger;
import cn.com.jit.ida.privilege.Privilege;
import cn.com.jit.ida.privilege.PrivilegeException;
import cn.com.jit.ida.privilege.TemplateAdmin;
import cn.com.jit.ida.util.pki.PKIException;
import cn.com.jit.ida.util.pki.Parser;
import cn.com.jit.ida.util.pki.cipher.JCrypto;
import cn.com.jit.license.IDAFormula;
import cn.com.jit.license.LicenseException;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.net.ServerSocket;

public class StartServer {
	GlobalConfig globalConfig;
	JCrypto jCrypto;
	DBManager dbManager;
	LogManager logManager;
	SysLogger sysLogger;
	DebugLogger debugLogger;
	IDAFormula listenseAPI;
	HTTPServer httpServer;
	HTTPSServer httpsServer;
	StartSocket startSocket;
	CRLSchedule crlSchedule;
	LoadBalance loadBalance;
	CertSchedule certSchedule;
	CertArcSchedule certArcSchedule;
	LogArcSchedule logArcSchedule;

	public boolean configServer() {
		try {
			this.globalConfig = GlobalConfig.getInstance();
			this.globalConfig.reflush();
			return true;
		} catch (Exception localException) {
		}
		return false;
	}

	public void initServer() throws InitServerException {
		this.sysLogger = LogManager.getSysLogger();
		this.debugLogger = LogManager
				.getDebugLogger("cn.com.jit.ida.ca.StartServer.class");
		File localFile = new File("./license.lce");
		if (!localFile.exists()) {
			cn.com.jit.ida.ca.config.CAConfigConstant.isUserCancel = "1";
			throw new InitServerException("0981", "license.lce文件不存在。");
		}
		try {
			this.listenseAPI = IDAFormula.getInstance("./license.lce");
			if (!this.listenseAPI.getSystemValue1().equalsIgnoreCase(
					"SRQ05 5.0")) {
				cn.com.jit.ida.ca.config.CAConfigConstant.isUserCancel = "1";
				throw new InitServerException("0974", "License检查产品名称失败");
			}
		} catch (LicenseException localLicenseException) {
			cn.com.jit.ida.ca.config.CAConfigConstant.isUserCancel = "1";
			throw new InitServerException("0972", "License文件错误",
					localLicenseException);
		}
		String str = null;
		try {
			str = CryptoConfig.getInstance().getDeviceID();
		} catch (IDAException localIDAException) {
			throw new InitServerException(localIDAException.getErrCode(),
					localIDAException.getErrDesc(), localIDAException);
		}
		this.jCrypto = JCrypto.getInstance();
		try {
			if ((!str.equalsIgnoreCase("JSOFT_LIB"))
					&& (!this.jCrypto.initialize(str, new Integer(2))))
				throw new InitServerException("0901", "加密库设备ID错误");
		} catch (PKIException localPKIException) {
			throw new InitServerException(localPKIException.getErrCode(),
					localPKIException.getErrDesc(), localPKIException);
		}
		configServer();
		try {
			this.dbManager = DBManager.getInstance();
		} catch (DBException localDBException) {
			throw new InitServerException(localDBException.getErrCode(),
					localDBException.getErrDesc(), localDBException);
		}
//		try {
//			Privilege localPrivilege = Privilege.getInstance();
//			TemplateAdmin localTemplateAdmin = TemplateAdmin.getInstance();
//		} catch (PrivilegeException localPrivilegeException) {
//			throw new InitServerException(localPrivilegeException.getErrCode(),
//					localPrivilegeException.getErrDesc(),
//					localPrivilegeException);
//		}
	}

	public boolean startWeb() {
		int i = 1;
		boolean bool = true;
		try {
			InternalConfig localInternalConfig = InternalConfig.getInstance();
			bool = localInternalConfig.isStartEndUser();
		} catch (Exception localException) {
		}
		ServerConfig localServerConfig = null;
		InitServerException localInitServerException;
		try {
			localServerConfig = ServerConfig.getInstance();
		} catch (IDAException localIDAException) {
			localInitServerException = new InitServerException(
					localIDAException.getErrCode(), localIDAException
							.getErrDesc(), localIDAException);
			this.sysLogger.info(localInitServerException.getErrDescEx());
			this.debugLogger.appendMsg_L1(localInitServerException.toString());
			this.debugLogger.doLog();
			i = 0;
		}
		if (bool) {
			this.sysLogger.info("Web监听启动...");
			this.httpServer = new HTTPServer();
			try {
				this.httpServer.startTomcat();
				this.sysLogger.info("Web监听启动成功(端口:"
						+ localServerConfig.getWebServerPort() + ")");
			} catch (WebServerException localWebServerException1) {
				localInitServerException = new InitServerException(
						localWebServerException1.getErrCode(),
						localWebServerException1.getErrDesc(),
						localWebServerException1);
				this.sysLogger.info(localInitServerException.getErrDescEx());
				this.debugLogger.appendMsg_L1(localInitServerException
						.toString());
				this.debugLogger.doLog();
				i = 0;
			}
		}
		try {
			this.sysLogger.info("安全Web监听启动...");
			this.httpsServer = new HTTPSServer();
			this.httpsServer.startTomcat();
			this.sysLogger.info("安全Web监听启动成功(端口:"
					+ localServerConfig.getSecureWebPort() + ")");
		} catch (WebServerException localWebServerException2) {
			localInitServerException = new InitServerException(
					localWebServerException2.getErrCode(),
					localWebServerException2.getErrDesc(),
					localWebServerException2);
			this.sysLogger.info(localInitServerException.getErrDescEx());
			this.debugLogger.appendMsg_L1(localInitServerException.toString());
			this.debugLogger.doLog();
			return i != 0;
		}
		return true;
	}

	public boolean stopWeb() {
		try {
			if (this.httpServer != null)
				this.httpServer.stopTomcat();
			else
				System.out.println("Web监听还没有启动");
			if (this.httpsServer != null)
				this.httpsServer.stopTomcat();
			else
				System.out.println("Web监听还没有启动");
		} catch (WebServerException localWebServerException) {
			this.sysLogger.info("Web监听停止失败", new InitServerException(
					localWebServerException.getErrCode(),
					localWebServerException.getErrDesc(),
					localWebServerException));
			return false;
		}
		this.sysLogger.info("Web监听停止");
		return true;
	}

	public boolean startSocket() {
		this.sysLogger.info("Socket监听启动...");
		try {
			this.startSocket = new StartSocket();
			this.startSocket.run();
			ServerConfig localServerConfig = null;
			try {
				localServerConfig = ServerConfig.getInstance();
			} catch (IDAException localIDAException) {
			}
			this.sysLogger.info("Socket监听启动成功(端口:"
					+ localServerConfig.getPort() + ")");
			return true;
		} catch (InitServerException localInitServerException) {
			this.sysLogger.info("Socket启动失败");
		}
		return false;
	}

	public void stopSocket() {
		try {
			this.startSocket.stop();
		} catch (InitServerException localInitServerException) {
			this.sysLogger.info("Socket停止失败");
			return;
		}
		this.sysLogger.info("Socket监听停止成功");
	}

	public void startDBPool() throws InitServerException {
		try {
			this.dbManager = DBManager.getInstance();
		} catch (DBException localDBException) {
			throw new InitServerException(localDBException.getErrCode(),
					localDBException.getErrDesc(), localDBException);
		}
	}

	public boolean run() throws InitServerException {
		int i = 0;
		ServerConfig localServerConfig = null;
		try {
			localServerConfig = ServerConfig.getInstance();
			localServerConfig.init();
		} catch (IDAException localIDAException) {
			throw new InitServerException(localIDAException.getErrCode(),
					localIDAException.getErrDesc(), localIDAException);
		}
		int j = localServerConfig.getControlPort();
		ServerSocket localServerSocket = null;
		try {
			localServerSocket = new ServerSocket(j);
		} catch (IOException localIOException1) {
			throw new InitServerException("0983", "服务器已经启动或者控制端口被占用");
		} finally {
			try {
				if (localServerSocket != null)
					localServerSocket.close();
			} catch (IOException localIOException2) {
			}
		}
		run_without_service();
		if (startWeb())
			i = 1;
		if (startSocket())
			i = 1;
		startTimer();
		PendingTaskService localPendingTaskService = new PendingTaskService();
		localPendingTaskService.start();
		try {
			DBManager localDBManager = DBManager.getInstance();
			localDBManager.refreshConfig();
		} catch (DBException localDBException) {
			throw new InitServerException(localDBException.getErrCode(),
					localDBException.getErrDesc(), localDBException
							.getHistory());
		}
		return i;
	}

	public void startTimer() {
		this.crlSchedule = CRLSchedule.getInstance();
		if (this.crlSchedule != null)
			this.crlSchedule.start();
		this.loadBalance = LoadBalance.getInstance();
		if (this.loadBalance != null)
			this.loadBalance.start();
		this.certSchedule = CertSchedule.getInstance();
		if (this.certSchedule != null)
			this.certSchedule.start();
		try {
			AutoServiceConfig localAutoServiceConfig = AutoServiceConfig
					.getInstance();
			if (localAutoServiceConfig.isUseAutoCertArchive()) {
				this.certArcSchedule = CertArcSchedule.getInstance();
				if (this.certArcSchedule != null)
					this.certArcSchedule.start();
			}
			if (localAutoServiceConfig.isUseAutoLogArchive()) {
				this.logArcSchedule = LogArcSchedule.getInstance();
				if (this.logArcSchedule != null)
					this.logArcSchedule.start();
			}
		} catch (IDAException localIDAException) {
		}
	}

	public void run_without_service() throws InitServerException {
		this.jCrypto = JCrypto.getInstance();
		try {
			this.jCrypto.initialize("JSOFT_LIB", null);
		} catch (PKIException localPKIException) {
			throw new InitServerException(localPKIException.getErrCode(),
					localPKIException.getErrDesc());
		}
		this.logManager = new LogManager();
		LogManager.init();
		initServer();
		startDBPool();
//		try {
//			CTMLManager.initializeInstance();
//			SelfExtensionManager.initializeInstance();
//		} catch (IDAException localIDAException1) {
//			new InitServerException(localIDAException1.getErrCode(),
//					localIDAException1.getErrDesc(), localIDAException1);
//		}
		try {
			this.globalConfig = GlobalConfig.getInstance();
			this.globalConfig.reflush();
		} catch (IDAException localIDAException2) {
			throw new InitServerException(localIDAException2.getErrCode(),
					localIDAException2.getErrDesc(), localIDAException2);
		}
		try {
			if (!this.listenseAPI.getSystemValue1().equalsIgnoreCase(
					"SRQ05 5.0"))
				throw new InitServerException("0974", "License检查产品名称错误");
			String str = this.globalConfig.getCAConfig().getDN();
			if (!this.listenseAPI.getSystemValue2().equalsIgnoreCase(str)) {
				str = Parser.ReverseDN(str);
				if (!this.listenseAPI.getSystemValue2().equalsIgnoreCase(str))
					throw new InitServerException("0973", "License检查CA名称失败");
			}
			if ((!this.listenseAPI.getSystemValue3().equals("NULL"))
					&& (Long.decode(this.listenseAPI.getSystemValue3())
							.longValue() < System.currentTimeMillis() / 1000L))
				throw new InitServerException("0975", "License检查有效期失败");
		} catch (LicenseException localLicenseException) {
			throw new InitServerException("0972", "License检查失败");
		}
	}

	public void StopAll() {
		if (this.crlSchedule != null)
			this.crlSchedule.cancel();
		if (this.loadBalance != null)
			this.loadBalance.cancel();
		if (this.certSchedule != null)
			this.certSchedule.cancel();
		if (this.certArcSchedule != null)
			this.certArcSchedule.cancel();
		if (this.logArcSchedule != null)
			this.logArcSchedule.cancel();
	}
}

/*
 * Location: C:\Program Files\JIT\CA50\lib\IDA\ida.jar Qualified Name:
 * cn.com.jit.ida.ca.StartServer JD-Core Version: 0.6.0
 */