package cn.com.jit.ida.ca.displayrelated.initserver;

import java.security.KeyPair;

import cn.com.jit.ida.IDAException;
import cn.com.jit.ida.ca.key.GenericKey;
import cn.com.jit.ida.ca.key.keyutils.KeyUtils;
import cn.com.jit.ida.ca.key.keyutils.Keytype;
import cn.com.jit.ida.globalconfig.ConfigException;
import cn.com.jit.ida.globalconfig.ParseXML;
import cn.com.jit.ida.privilege.Admin;

public class InitAuditAdminPFX extends InitFather {
	//审计管理员签名算法
	private String signingKeyAlg;
	//审计管理员的密钥算法
	private String adminKeyAlg;
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
	private int keysize;
	
	public InitAuditAdminPFX(String init) throws IDAException {
		super(init);
	}
	public InitAuditAdminPFX(String keyalag, String password, String path, int validity)throws IDAException{
		this.adminKeyAlg = keyalag;
		this.signingKeyAlg = keyalag.equals(RSA) ? RSA_ALGORITHM : SM2_ALGORITHM;
		this.password = password;
		this.validityNum = validity;
		this.p12Path = path;
	}
	public InitAuditAdminPFX(boolean init) throws IDAException {
		super(init);
	}
	public void initialize() throws ConfigException{
		this.signingKeyAlg = init.getString("AuditAdminSigningKeyAlg");
		if (signingKeyAlg.equalsIgnoreCase("SHA1withRSA")) {
			adminKeyAlg = RSA;
		} else if (signingKeyAlg.equalsIgnoreCase("SM3WITHSM2")) {
			adminKeyAlg = SM2;
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
		keysize = this.init.getNumber("AuditAdminKeySize");
	}
	
	public void makeAuditAdminPFX() throws IDAException{
		makeAuditAdminPFX(keysize);
	}
	public void makeAuditAdminPFX(int keysize) throws IDAException{
		KeyPair keyPair = KeyUtils.createKeyPair(adminKeyAlg, KEYPAIR_TYPE, keysize);
		GenericKey gKey = new GenericKey(true, p12Path, password.toCharArray(), keyPair, GenericKey.PKCS12);
		gKey.setAdminIdentity(Admin.AUDIT_ADMIN);
		gKey.addKeystoreStruct(signingKeyAlg, DN, password.toCharArray(), validityNum);
		gKey.addDemoCAAtOnce(getCerPath(p12Path), keyPair.getPrivate(), password.toCharArray());
		gKey.saveToFile();
	}
	
	public ParseXML getInit() {
		return init;
	}
	public String getDN() {
		return DN;
	}
	public void setDN(String dn) {
		DN = dn;
	}
}
