package cn.com.jit.ida.ca.displayrelated;

import cn.com.jit.ida.IDAException;
import cn.com.jit.ida.ca.displayrelated.subca.InitCommuCert;
import cn.com.jit.ida.globalconfig.ConfigTool;
import cn.com.jit.ida.log.LogManager;
import cn.com.jit.ida.log.SysLogger;

public class LaterOperateCommServerJKS {

	public void operateCommServerJKS() {
		// 初始化日志 然后获得日志输出对象
		LogManager.init();
		SysLogger logger = LogManager.getSysLogger();
		// 根据选择 进行具体操作 全部是针对子CA
//		int j = ConfigTool.displayMenu(null, new String[] {
//				"导入证书(根据req文件签出的cer格式证书)", "导入根证书" });
//		if (j == 0) {
//			logger.info("用户取消");
//		}
		int j = 2;
		int i = 0;
		InitCommuCert initCommuCert;
		if (j == 1) {
			initCommuCert = new InitCommuCert(InitCommuCert.COMM_JKS);
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
		} else if (j == 2) {
			initCommuCert = new InitCommuCert(InitCommuCert.COMM_JKS);
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
