package cn.com.jit.ida.ca.display;

import cn.com.jit.ida.ca.displayrelated.UpdateServerCommJKS;
import cn.com.jit.ida.globalconfig.ConfigException;
import cn.com.jit.ida.log.LogManager;
import cn.com.jit.ida.log.SysLogger;

/**
 * 更新服务器通信证书
 * @author kmc
 *
 */
public class DisplayUpdateCommjks extends DisplayOperate {

	public void operate() {
		UpdateServerCommJKS ucj;
		try {
			ucj = new UpdateServerCommJKS();
			ucj.updateCommJKS();
		} catch (Exception e) {
			LogManager.init();
			SysLogger logger = LogManager.getSysLogger();
			logger.info(e.getMessage());
		}
	}

}
