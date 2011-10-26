package cn.com.jit.ida.ca.init;

import java.security.KeyPair;

import cn.com.jit.ida.IDAException;
import cn.com.jit.ida.ca.db.DBException;
import cn.com.jit.ida.ca.db.DBManager;
import cn.com.jit.ida.ca.key.GenericKey;
import cn.com.jit.ida.ca.key.keyutils.KeyUtils;
import cn.com.jit.ida.ca.key.keyutils.Keytype;
import cn.com.jit.ida.globalconfig.ConfigException;
import cn.com.jit.ida.globalconfig.ParseXML;

public class InitSigningJKS extends InitFather {
	public KeyPair kPair;
	protected String singingkeyStorePassword;
	// 签名证书的密钥签名算法
	protected String keySigningAlg;
	protected String keySigningStoreAlg;
	protected String signingjkspath;
	protected int keySize;
	protected String genCerDN;
	protected int signingValidityDay;
	
	public InitSigningJKS() throws Exception {
		super();
	}

	public InitSigningJKS(ParseXML init) throws Exception {
		super(init);
	}

	// 检查父类得到的配置文件信息 如果为null或者超出范围将抛异常
	// 这个一定要在第一步进行 暂且做注释 稍后补全
	public void initialize() throws ConfigException {
		this.singingkeyStorePassword = init.getString("SigningKeyStorePWD");
		this.keySigningAlg = init.getString("SigningSigningKeyAlg");
		if (keySigningAlg.equalsIgnoreCase("SHA1withRSA")) {
			keySigningStoreAlg = RSA;
		} else if (keySigningAlg.equalsIgnoreCase("SM3WITHSM2")) {
			keySigningStoreAlg = SM2;
		}
		this.signingjkspath = this.init.getString("SigningKeyStore");
		this.keySize = this.init.getNumber("SigningSigningKeySize");
		this.genCerDN = init.getString("CASubject");
		this.signingValidityDay = this.init.getNumber("SigningCertValidity");
	}

	// 生成自签名的证书cer 当然是利用上面的密钥对
	public void getSignedByItselfCer() throws IDAException {
		KeyPair keyPair =KeyUtils.createKeyPair(keySigningStoreAlg, Keytype.SOFT_VALUE,
				keySize);
		GenericKey gKey = new GenericKey(true, signingjkspath,
				singingkeyStorePassword.toCharArray(), keyPair, GenericKey.JKS);
		gKey.addKeystoreStruct(keySigningAlg, genCerDN, singingkeyStorePassword
				.toCharArray(), signingValidityDay);
		gKey.saveToFile();
	}

	/**
	 * 得到模板的ctm数据
	 * 
	 * @throws DBException
	 */
	public static void getTempLateInfo() throws DBException {
		DBManager dbManager = DBManager.getInstance();
		String xml = new String(dbManager.getTemplateInfo());
		System.out.println(xml);
	}

}
