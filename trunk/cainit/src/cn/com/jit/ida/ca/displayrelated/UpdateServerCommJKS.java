package cn.com.jit.ida.ca.displayrelated;

import cn.com.jit.ida.ca.displayrelated.initserver.InitCommServerJKS;
import cn.com.jit.ida.globalconfig.ConfigException;
import cn.com.jit.ida.globalconfig.ConfigTool;
import cn.com.jit.ida.globalconfig.ParseXML;

/**
 * 更新CA服务器证书 输入一个证书有效期，然后根据配置文件的内容生成一个新的jks 这个功能应该非常不常用，因为目前服务器证书的有效期设置的非常大
 * 
 * @author kmc
 * 
 */
public class UpdateServerCommJKS {
	private ParseXML caconfig;

	public UpdateServerCommJKS() throws ConfigException {
		caconfig = new ParseXML("./config/CAConfig.xml");
	}

	public UpdateServerCommJKS(ParseXML parseXml) {
		this.caconfig = parseXml;
	}
	
	public void updateCommJKS() throws Exception{
		String dn = ConfigTool.getDN("请输入管理员证书DN:");
		String str = ConfigTool.getInteger("请输入通信证书有效期，如365", 2147483647, 0, "天");
		String password = ConfigTool.getPassword("请输入证书密码:", 6, 16);
		String path = caconfig.getString("CommKeyStore");
		InitCommServerJKS ick = new InitCommServerJKS(caconfig);
		ick.setCommJKSValidityDay(Integer.parseInt(str));
		ick.makeServerJKS(path, password.toCharArray(), dn);
	}
}
