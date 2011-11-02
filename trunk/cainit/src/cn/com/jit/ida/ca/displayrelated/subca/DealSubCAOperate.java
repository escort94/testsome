package cn.com.jit.ida.ca.displayrelated.subca;

import java.io.File;

import cn.com.jit.ida.IDAException;
import cn.com.jit.ida.globalconfig.ConfigException;
import cn.com.jit.ida.globalconfig.ConfigTool;
import cn.com.jit.ida.globalconfig.ParseXML;
import cn.com.jit.ida.log.LogManager;
import cn.com.jit.ida.log.SysLogger;
import cn.com.jit.ida.util.pki.PKIException;

/**
 * 操作子CA通信证书具体实现类
 * @author kmc
 *
 */
public class DealSubCAOperate {

	public void dealCommuCert(){
		File initFile = new File("./config/init.xml");
		if (!initFile.exists()) {
			System.out.println("./config/init.xml不存在，无法进行操作");
			return;
		}
		//初始化日志 然后获得日志输出对象
		CommuCert.clear();
		LogManager.init();
		SysLogger logger = LogManager.getSysLogger();
		//根据选择 进行具体操作 全部是针对子CA
//		int j = ConfigTool.displayMenu(null, new String[] { "产生申请书(用于创建通信证书)",
//				"产生申请书(用于更新通信证书)", "导入证书(根据req文件签出的cer格式证书)", "导入根证书" });
		int j = ConfigTool.displayMenu(null, new String[] { "产生申请书(用于创建通信证书)",
				"产生申请书(用于更新通信证书)"});
		if (j == 0) {
			logger.info("用户取消");
		}
		int i = 0;
		InitCommuCert initCommuCert;
		if ((j == 1) || (j == 2)) {
			initCommuCert = new InitCommuCert(InitCommuCert.SUB_CA_JKS);
			if (j == 1) {
				try {
					i = initCommuCert.generalReq();
				} catch (IDAException e) {
					i = -1;
					logger.info(e.getErrCode() + e.getErrDesc());
				}
			} else {
				try {
					i = initCommuCert.generalReqForUpdataSM2();
				} catch (PKIException e) {
					//TODO 打印方式需要后续修改 规范采取 e.getMessage()
					if (!e.getErrCode().equals("3002")) {
						i = -1;
					}
					logger.info(e.getErrCode() + e.getErrDesc());
				} catch (IDAException e) {
					if (!e.getErrCode().equals("3002")) {
						i = -1;
					}
					logger.info(e.getErrCode() + e.getErrDesc());
				}
			}
			if (i == 1) {
				if (logger != null)
					logger.info("产生通信证书申请书成功");
				else
					System.out.println("产生通信证书申请书成功");
			} else if (i == -1) {
				if (logger != null)
					logger.info("产生通信证书申请书失败");
				else
					System.out.println("产生通信证书申请书失败");
			} else if (logger != null) {
				logger.info("用户放弃");
			} else {
				System.out.println("用户放弃");
			}
		} else if (j == 3) {
			initCommuCert = new InitCommuCert(InitCommuCert.SUB_CA_JKS);
			try {
				i = initCommuCert.importCertFromFile(false);
			} catch (IDAException e) {
				i = -1;
				logger.info(e.getErrCode() + e.getErrDesc());
			}
			if (i == 1) {
				if (logger != null)
					logger.info("导入通信证书成功");
				else
					System.out.println("导入通信证书成功");
			} else if (i == -1) {
				if (logger != null)
					logger.info("导入通信证书失败");
				else
					System.out.println("导入通信证书失败");
			} else if (logger != null) {
				logger.info("用户放弃");
			} else {
				System.out.println("用户放弃");
			}
		} else if (j == 4) {
				initCommuCert = new InitCommuCert(InitCommuCert.SUB_CA_JKS);
				try {
					i = initCommuCert.importGenCer();
				} catch (IDAException e) {
					i = -1;
					logger.info(e.getErrCode() + e.getErrDesc());
				}
				if (i == 1) {
					if (logger != null)
						logger.info("导入根证书成功");
					else
						System.out.println("导入根证书成功");
				} else if (i == -1) {
					if (logger != null)
						logger.info("导入根证书失败");
					else
						System.out.println("导入根证书失败");
				} else if (logger != null) {
					logger.info("用户取消");
				} else {
					System.out.println("用户取消");
				}
			} else {
				return;
		} 
	}
}
