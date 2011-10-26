package cn.com.jit.ida.ca.displayrelated;

import cn.com.jit.ida.ca.displayrelated.initserver.InitSigningJKS;
import cn.com.jit.ida.globalconfig.ConfigException;
import cn.com.jit.ida.globalconfig.ParseXML;

public class UpdateSigningJKS {
	private ParseXML caconfig;

	public UpdateSigningJKS() throws ConfigException {
		caconfig = new ParseXML("./config/CAConfig.xml");
	}

	public UpdateSigningJKS(ParseXML parseXml) {
		this.caconfig = parseXml;
	}
	
	public void updateSigningJKS() throws Exception{
		InitSigningJKS isj = new InitSigningJKS(caconfig);
		isj.getSignedByItselfCer();
	}
}
