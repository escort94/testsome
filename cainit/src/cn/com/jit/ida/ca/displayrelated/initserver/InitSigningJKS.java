package cn.com.jit.ida.ca.displayrelated.initserver;

import cn.com.jit.ida.IDAException;
import cn.com.jit.ida.ca.db.DBException;
import cn.com.jit.ida.ca.db.DBManager;
import cn.com.jit.ida.ca.initserver.InitServerException;
import cn.com.jit.ida.ca.key.GenericKey;
import cn.com.jit.ida.ca.key.keyutils.KeyUtils;
import cn.com.jit.ida.ca.key.keyutils.Keytype;
import cn.com.jit.ida.globalconfig.ConfigException;
import cn.com.jit.ida.globalconfig.ParseXML;
import cn.com.jit.ida.util.pki.PKIException;
import cn.com.jit.ida.util.pki.cert.X509CertGenerator;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.sql.SQLException;
import java.util.Date;
import java.util.GregorianCalendar;

import fisher.man.jce.X509Principal;
import fisher.man.x509.X509V3CertificateGenerator;

public class InitSigningJKS extends InitFather {
	public KeyPair kPair;
	protected String singingkeyStorePassword;
	// 签名证书的密钥签名算法
	protected String keySigningAlg;
	protected String keySigningStoreAlg;
	protected String signingjkspath;
	protected int keySize;
	protected String issuer;
	protected int signingValidityDay;

	public InitSigningJKS(String init) throws IDAException {
		super(init);
	}
	public InitSigningJKS(boolean init) throws IDAException {
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
		this.issuer = init.getString("CASubject");
		this.signingValidityDay = this.init.getNumber("SigningCertValidity");
	}

	// 生成自签名的证书cer 当然是利用上面的密钥对
	public void getSignedByItselfCer() throws IDAException {
		KeyPair keyPair =KeyUtils.createKeyPair(keySigningStoreAlg, KEYPAIR_TYPE,
				keySize);
		GenericKey gKey = new GenericKey(true, signingjkspath,
				singingkeyStorePassword.toCharArray(), keyPair, GenericKey.JKS);
		gKey.addKeystoreStruct(keySigningAlg, issuer, singingkeyStorePassword
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

	public int getSigningValidityDay() {
		return signingValidityDay;
	}

	public void setSigningValidityDay(int signingValidityDay) {
		this.signingValidityDay = signingValidityDay;
	}
	public String getIssuer() {
		return issuer;
	}
	public void setIssuer(String issuer) {
		this.issuer = issuer;
	}

}
