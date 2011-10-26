package cn.com.jit.ida.ca.displayrelated.initserver;

import java.security.KeyPair;
import java.security.KeyStore;

import cn.com.jit.ida.ca.key.GenericKey;
import cn.com.jit.ida.ca.key.keyutils.KeyUtils;
import cn.com.jit.ida.globalconfig.ConfigException;
import cn.com.jit.ida.globalconfig.ParseXML;
import cn.com.jit.ida.privilege.Admin;

public class InitSuperAdminPFX extends InitFather {
	
	private String superAdminSigningKeyAlg;
	//超级管理员的密钥算法
	private String superAdminKeyAlg;
	//签发有效期限
	private int validityNum;
	//pfx存储路径
	private String p12Path;
	//超级管理员p7b文件位置
	private String AdminCertPath;
	//超级管理员DN中的CN名字
	private String adminDnNameInCn;
	private String password;
	private String DN;
	
	public InitSuperAdminPFX()throws Exception{
		super();
	}
	public InitSuperAdminPFX(ParseXML init) throws Exception {
		super(init);
	}
	public void initialize() throws ConfigException{
		this.superAdminSigningKeyAlg = init.getString("SuperAdminSigningKeyAlg");
		if (superAdminSigningKeyAlg.equalsIgnoreCase("SHA1withRSA")) {
			superAdminKeyAlg = RSA;
		} else if (superAdminSigningKeyAlg.equalsIgnoreCase("SM3WITHSM2")) {
			superAdminKeyAlg = SM2;
		}
		validityNum = init.getNumber("AdminCertValidity");
		p12Path = this.init.getString("AdminKeyStorePath").trim();
		if (p12Path.equalsIgnoreCase("")){
			p12Path = "./keystore/superAdmin.pfx";
		}
		AdminCertPath = this.init.getString("AdminCertPath");
		if (AdminCertPath.equalsIgnoreCase("")){
			AdminCertPath = "./keystore/superAdmin.p7b";
		}
		adminDnNameInCn = this.init.getString("AdminName");
		DN = "CN=" + adminDnNameInCn + "," + baseDN;
		password = this.init.getString("AdminKeyStorePWD");
	}
	public void makeSuperAdminPFX(String keytype, int keysize) throws Exception{
		KeyPair keyPair = KeyUtils.createKeyPair(superAdminKeyAlg, keytype, keysize);
		GenericKey gKey = new GenericKey(true, p12Path, password.toCharArray(), keyPair, GenericKey.PKCS12);
		gKey.setAdminIdentity(Admin.SUPER_ADMIN);
		gKey.addKeystoreStruct(superAdminSigningKeyAlg, DN, password.toCharArray(), validityNum);
		gKey.saveToFile();
	}
	public String getString(String paramString){
		return init.getString(paramString);
	}
	public ParseXML getInit() {
		return init;
	}
	public void setInit(ParseXML init) {
		this.init = init;
	}
	
}
