package cn.com.jit.ida.ca.displayrelated.initserver;

import java.io.File;

import cn.com.jit.ida.IDAException;
import cn.com.jit.ida.ca.config.AutoServiceConfig;
import cn.com.jit.ida.ca.config.DBConfig;
import cn.com.jit.ida.ca.config.InternalConfig;
import cn.com.jit.ida.ca.config.KMCConfig;
import cn.com.jit.ida.ca.config.LDAPConfig;
import cn.com.jit.ida.ca.config.LOGConfig;
import cn.com.jit.ida.ca.config.ServerConfig;
import cn.com.jit.ida.ca.db.DBException;
import cn.com.jit.ida.ca.db.DBManager;
import cn.com.jit.ida.ca.displayrelated.DbUtils;
import cn.com.jit.ida.ca.initserver.DBInit;
import cn.com.jit.ida.ca.initserver.InitServerConfig;
import cn.com.jit.ida.ca.initserver.InitServerException;
import cn.com.jit.ida.ca.initserver.XML2DB;
import cn.com.jit.ida.ca.issue.ISSException;
import cn.com.jit.ida.ca.issue.initschema.InitLDAPSchema;
import cn.com.jit.ida.ca.key.keyutils.Keytype;
import cn.com.jit.ida.globalconfig.ConfigException;
import cn.com.jit.ida.globalconfig.ConfigTool;
import cn.com.jit.ida.globalconfig.ParseXML;
import cn.com.jit.ida.log.LogManager;
import cn.com.jit.ida.log.SysLogger;

/**
 * 本类供DisplayInitServer类调用
 * 
 * @author kmc
 * 
 */
public class InitSystem {
	ParseXML init;
	boolean isInit = true;

	public InitSystem() throws InitServerException {
		try {
			this.init = new ParseXML("./config/init.xml");
			this.isInit = true;
		} catch (ConfigException localConfigException) {
			throw new InitServerException(localConfigException.getErrCode(),
					localConfigException.getErrDescEx(), localConfigException);
		}

	}

	public void runInit() throws IDAException {
		// 验证配置文件正确性 init.xml文件 全部信息
		readInitConfig();
		
		// log initialize
		InitLog initLog = new InitLog();
		SysLogger logger = initLog.initLog();
		logger.info("日志系统初始化成功");
		
		// 生成CAConfig.xml配置文件 初始化数据库 将配置文件中部分信息存入到数据库
		dealInitData();
		logger.info("数据库初始化成功");
		
		//set sysadmin pwd,when you operationg specific function to validation identity
		DbUtils.insertSysAdminPwd();

		// LDAP initialize
		if (initLDAP()) {
			logger.info("LDAP初始化成功");
		}

		// DemoCA.cer initialize
		InitDemoCACer initDemoCACer = new InitDemoCACer();
		initDemoCACer.makeDemoCACer();
		logger.info("根证书初始化成功");

		// signingJKS initialize
		InitSigningJKS initSignJKS = new InitSigningJKS();
		initSignJKS.getSignedByItselfCer();
		logger.info("签名根证书初始化成功");

		// commServerJKS initialize
		InitCommServerJKS initCommServer = new InitCommServerJKS();
		initCommServer.beforeMake();
		logger.info("通信证书初始化成功");

		// superAdminJKS initialize
		InitSuperAdminPFX initSuperAdmin = new InitSuperAdminPFX();
		initSuperAdmin.makeSuperAdminPFX();
		logger.info("超级管理员证书初始化成功");

		// auditAdminJKS initialize
		InitAuditAdminPFX initAuditAdmin = new InitAuditAdminPFX();
		initAuditAdmin.makeAuditAdminPFX();
		logger.info("超级管理员证书初始化成功");

		// 配置文件的处理
		deleteInitFile(logger);
	}

	public void deleteInitFile(SysLogger logger) throws InitServerException {
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
			logger.info("删除配置文件失败。");
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

	public void readInitConfig() throws InitServerException {
		InitServerConfig localInitServerConfig = new InitServerConfig(this.init);
		localInitServerConfig.run();
	}

	public void dealInitData() throws InitServerException {
		try {
			InternalConfig.getInstance();
			XML2DB localXML2DB = new XML2DB(this.init);
			// 生成配置文件
			localXML2DB.run_file();
			DBConfig localDBConfig = DBConfig.getInstance();
			LogManager localLogManager = new LogManager();
			LogManager.init();
			LDAPConfig.getInstance();
			LOGConfig.getInstance();
			ServerConfig.getInstance();
			KMCConfig.getInstance();
			AutoServiceConfig.getInstance();
			// 初始化数据库
			initDB();
			// 部分配置文件的数据存入到数据库
			localXML2DB.run_DB();
		} catch (InitServerException localInitServerException) {
			throw localInitServerException;
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
	}
}
