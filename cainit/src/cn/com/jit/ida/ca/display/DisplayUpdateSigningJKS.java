package cn.com.jit.ida.ca.display;

import cn.com.jit.ida.ca.displayrelated.UpdateSigningJKS;
import cn.com.jit.ida.globalconfig.ConfigException;

public class DisplayUpdateSigningJKS extends DisplayOperate{

	public void operate() {
		UpdateSigningJKS usj;
		try {
			usj = new UpdateSigningJKS();
			usj.updateSigningJKS();
		} catch (ConfigException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
