package cn.com.jit.ida.ca.display;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import cn.com.jit.ida.IDAException;
import cn.com.jit.ida.ca.displayrelated.subca.CommuCert;
import cn.com.jit.ida.log.LogManager;
import cn.com.jit.ida.log.SysLogger;

public class DisplayWebServer {
	public void auth() throws IDAException {
		CommuCert.clear();
		CommuCert.getInstance();
	}

	public void stop(){
		LogManager.init();
		SysLogger sysLogger = LogManager.getSysLogger();
		try {
			auth();
		} catch (IDAException e2) {
			sysLogger.info(e2.getMessage());
			sysLogger.info("服务器关闭失败");
			return;
		}
		InputStream fis = null;
		InputStreamReader isr = null;
		BufferedReader br = null;
		Process p = null;
		try {
			p = Runtime.getRuntime().exec(
					"E:\\glassfish\\bin\\asadmin.bat stop-domain");
			sysLogger.info("正在关闭服务器，请稍后...");
			fis = p.getInputStream();
			isr = new InputStreamReader(fis);
			br = new BufferedReader(isr);
			String line = null;
			while ((line = br.readLine()) != null) {
				if (line.indexOf("已停止域") >= 0) {
					sysLogger.info("服务器已正常关闭");
					return;
				} else if (line.indexOf("未运行") >= 0) {
					sysLogger.info("服务器未运行，无需关闭");
					return;
				}
			}
			sysLogger.info("服务器关闭失败");
		} catch (IOException e) {
			sysLogger.info("服务器关闭发生异常:" + e.toString());
		} finally {
			try {
				p.destroy();
				fis.close();
				isr.close();
				br.close();
			} catch (IOException e1) {
				System.out.println("关闭输入流发生错误.");
			}
		}
	}

	public void start()  {
		LogManager.init();
		SysLogger logger = LogManager.getSysLogger();
		try {
			auth();
		} catch (IDAException e2) {
			logger.info(e2.getMessage());
			logger.info("服务器启动失败");
			return;
		}
		boolean run = false;
		InputStream fis = null;
		InputStreamReader isr = null;
		BufferedReader br = null;
		Process p = null;
		try {
			p = Runtime.getRuntime().exec(
					"E:\\glassfish\\bin\\asadmin.bat start-domain");
			logger.info("正在启动服务器请稍后...");
			fis = p.getInputStream();
			isr = new InputStreamReader(fis);
			br = new BufferedReader(isr);
			String line = null;
			while ((line = br.readLine().trim()) != null && !"".equals(line)) {
				if (line.indexOf("正在运行") >= 0 && line.indexOf("域") >= 0
						&& line.indexOf("配置") < 0) {
					run = true;
					logger.info("服务器正在运行，无需再次启动");
					return;
				} else if (line.indexOf("其他独立实例") >= 0) {
					run = true;
					logger.info("服务器启动完成");
					return;
				}
			}
			logger.info("启动服务器失败.");
		} catch (Exception e) {
			if (!run) {
				logger.info("启动服务器发生异常:" + e.toString());
			}
		} finally {
			try {
				p.destroy();
				fis.close();
				isr.close();
				br.close();
			} catch (IOException e1) {
				System.out.println("关闭输入流发生错误.");
			}
		}
	}
}
