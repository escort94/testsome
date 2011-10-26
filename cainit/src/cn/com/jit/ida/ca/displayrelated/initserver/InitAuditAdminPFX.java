package cn.com.jit.ida.ca.displayrelated.initserver;

import java.security.KeyPair;
import java.security.KeyStore;

import cn.com.jit.ida.ca.key.GenericKey;
import cn.com.jit.ida.ca.key.keyutils.KeyUtils;
import cn.com.jit.ida.globalconfig.ConfigException;
import cn.com.jit.ida.globalconfig.ParseXML;
import cn.com.jit.ida.privilege.Admin;

public class InitAuditAdminPFX extends InitFather {
	//审计管理员签名算法
	private String auditAdminSigningKeyAlg;
	//审计管理员的密钥算法
	private String auditAdminKeyAlg;
	//签发有效期限
	private int validityNum;
	//pfx存储路径
	private String p12Path;
	//审计管理员p7b文件位置
	private String auditAdminCertPath;
	//审计管理员DN中的CN名字
	private String auditAdminDnNameInCn;
	private String password;
	private String DN;
	
	public InitAuditAdminPFX()throws Exception{
		super();
	}
	public InitAuditAdminPFX(ParseXML init) throws Exception {
		super(init);
	}
	public void initialize() throws ConfigException{
		this.auditAdminSigningKeyAlg = init.getString("AuditAdminSigningKeyAlg");
		if (auditAdminSigningKeyAlg.equalsIgnoreCase("SHA1withRSA")) {
			auditAdminKeyAlg = RSA;
		} else if (auditAdminSigningKeyAlg.equalsIgnoreCase("SM3WITHSM2")) {
			auditAdminKeyAlg = SM2;
		}
		validityNum = this.init.getNumber("AuditAdminCertValidity");
		p12Path = this.init.getString("AuditAdminKeyStorePath").trim();
		if (p12Path.equalsIgnoreCase("")){
			p12Path = "./keystore/auditAdmin.pfx";
		}
		auditAdminCertPath = this.init.getString("AuditAdminCertPath");
		if (auditAdminCertPath.equalsIgnoreCase("")){
			auditAdminCertPath = "./keystore/auditAdmin.p7b";
		}
		auditAdminDnNameInCn = this.init.getString("AuditAdminName");
		DN = "CN=" + auditAdminDnNameInCn + "," + baseDN;
		password = this.init.getString("AdminKeyStorePWD");
	}
	
	public void makeAuditAdminPFX(String keytype, int keysize) throws Exception{
		KeyPair keyPair = KeyUtils.createKeyPair(auditAdminKeyAlg, keytype, keysize);
		GenericKey gKey = new GenericKey(true, p12Path, password.toCharArray(), keyPair, GenericKey.PKCS12);
		gKey.setAdminIdentity(Admin.AUDIT_ADMIN);
		gKey.addKeystoreStruct(auditAdminSigningKeyAlg, DN, password.toCharArray(), validityNum);
		gKey.saveToFile();
	}
	public ParseXML getInit() {
		return init;
	}
}
