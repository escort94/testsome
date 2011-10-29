package cn.com.jit.ida.ca.displayrelated;

import cn.com.jit.ida.ca.displayrelated.initserver.InitSigningJKS;
import cn.com.jit.ida.globalconfig.ConfigException;
import cn.com.jit.ida.globalconfig.ParseXML;

public class UpdateSigningJKS {
	private ParseXML caconfig;

	public UpdateSigningJKS() throws ConfigException {
	}
	public void updateSigningJKS() throws Exception{
		InitSigningJKS isj = new InitSigningJKS(true);
		isj.getSignedByItselfCer();
	}
}
