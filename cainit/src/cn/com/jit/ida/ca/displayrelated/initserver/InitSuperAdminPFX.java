package cn.com.jit.ida.ca.displayrelated.initserver;

import java.security.KeyPair;
import java.security.KeyStore;

import cn.com.jit.ida.IDAException;
import cn.com.jit.ida.ca.key.GenericKey;
import cn.com.jit.ida.ca.key.keyutils.KeyUtils;
import cn.com.jit.ida.ca.key.keyutils.Keytype;
import cn.com.jit.ida.globalconfig.ConfigException;
import cn.com.jit.ida.globalconfig.ParseXML;
import cn.com.jit.ida.privilege.Admin;

public class InitSuperAdminPFX extends InitFather {
	
	private String signingKeyAlg;
	//超级管理员的密钥算法
	private String adminKeyAlg;
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
	private int keysize;
	
	public InitSuperAdminPFX(String init) throws IDAException {
		super(init);
	}
	public InitSuperAdminPFX(boolean init) throws IDAException {
		super(init);
	}
	public InitSuperAdminPFX(String keyalag, String password, String path, int validity)throws IDAException{
		super(true);
		this.adminKeyAlg = keyalag;
		this.signingKeyAlg = keyalag.equals(RSA) ? RSA_ALGORITHM : SM2_ALGORITHM;
		this.password = password;
		this.validityNum = validity;
		this.p12Path = path;
	}
	public void initialize() throws ConfigException{
		this.signingKeyAlg = init.getString("SuperAdminSigningKeyAlg");
		if (signingKeyAlg.equalsIgnoreCase("SHA1withRSA")) {
			adminKeyAlg = RSA;
		} else if (signingKeyAlg.equalsIgnoreCase("SM3WITHSM2")) {
			adminKeyAlg = SM2;
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
		keysize = this.init.getNumber("SuperAdminKeySize");
	}
	public void makeSuperAdminPFX() throws IDAException{
		makeSuperAdminPFX(keysize);
	}
	public void makeSuperAdminPFX(int keysize) throws IDAException{
		KeyPair keyPair = KeyUtils.createKeyPair(adminKeyAlg, KEYPAIR_TYPE, keysize);
		GenericKey gKey = new GenericKey(true, p12Path, password.toCharArray(), keyPair, GenericKey.PKCS12);
		gKey.setAdminIdentity(Admin.SUPER_ADMIN);
		gKey.addKeystoreStruct(signingKeyAlg, DN, password.toCharArray(), validityNum);
//		gKey.addDemoCAAtOnce(getCerPath(p12Path), keyPair.getPrivate(), password.toCharArray());
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
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public int getValidityNum() {
		return validityNum;
	}
	public void setValidityNum(int validityNum) {
		this.validityNum = validityNum;
	}
	public String getDN() {
		return DN;
	}
	public void setDN(String dn) {
		DN = dn;
	}
	public String getP12Path() {
		return p12Path;
	}
	public void setP12Path(String path) {
		p12Path = path;
	}
	public String getSuperAdminSigningKeyAlg() {
		return signingKeyAlg;
	}
	public void setSuperAdminSigningKeyAlg(String superAdminSigningKeyAlg) {
		this.signingKeyAlg = superAdminSigningKeyAlg;
	}
	public String getSuperAdminKeyAlg() {
		return adminKeyAlg;
	}
	public void setSuperAdminKeyAlg(String superAdminKeyAlg) {
		this.adminKeyAlg = superAdminKeyAlg;
	}
	
}
