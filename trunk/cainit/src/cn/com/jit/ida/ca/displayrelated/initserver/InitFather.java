package cn.com.jit.ida.ca.displayrelated.initserver;

import cn.com.jit.ida.globalconfig.ConfigException;
import cn.com.jit.ida.globalconfig.ParseXML;

public abstract class InitFather {
	protected ParseXML init;
	// 审计管理员的密钥算法
	protected String baseDN;
	public static final String RSA = "RSA";
	public static final String SM2 = "SM2";

	public InitFather() throws Exception {
		this.init = new ParseXML("./config/init.xml");
		initConfig();
		initialize();
	}
	public InitFather(ParseXML init) throws Exception {
		this.init = init;
		initConfig();
		try {
			initialize();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public abstract void initialize() throws Exception ;
	
	//初始化方法 供子类实现 
	public void initConfig() throws ConfigException{
		baseDN = init.getString("BaseDN");
	}
	

}
