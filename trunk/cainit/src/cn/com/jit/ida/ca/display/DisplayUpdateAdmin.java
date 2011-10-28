package cn.com.jit.ida.ca.display;

import cn.com.jit.ida.IDAException;
import cn.com.jit.ida.ca.displayrelated.UpdateAdmin;
import cn.com.jit.ida.log.LogManager;
import cn.com.jit.ida.log.SysLogger;

/**
 * 更新管理员证书
 * @author kmc
 *
 */
public class DisplayUpdateAdmin extends DisplayOperate{

	public void operate() {
		UpdateAdmin ua = new UpdateAdmin();
		try {
			ua.updateAdmin();
		} catch (IDAException e) {
			if(LogManager.isInitialized()){
				SysLogger logger = LogManager.getSysLogger();
				logger.info(e.getMessage());
			}else{
				System.out.println(e.getMessage());
			}
		}
	}

}
