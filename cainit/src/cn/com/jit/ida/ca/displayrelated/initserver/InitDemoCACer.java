package cn.com.jit.ida.ca.displayrelated.initserver;

import java.io.File;
import java.io.FileOutputStream;
import java.math.BigInteger;
import java.security.KeyPair;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.util.Date;
import java.util.GregorianCalendar;

import cn.com.jit.ida.IDAException;
import cn.com.jit.ida.ca.certmanager.service.operation.CodeGenerator;
import cn.com.jit.ida.ca.exception.OperateException;
import cn.com.jit.ida.ca.key.keyutils.KeyUtils;
import cn.com.jit.ida.ca.key.keyutils.Keytype;
import cn.com.jit.ida.globalconfig.ParseXML;
import fisher.man.jce.X509Principal;
import fisher.man.x509.X509V3CertificateGenerator;

public class InitDemoCACer extends InitFather {
	protected int demoCACertValidity;
	public String democaSigningAlg;
	public String democaStoreAlg;
	public int democaKeySize;
	protected String genCerDN;
	public String democaKeyStore;
	
	public InitDemoCACer() throws IDAException {
		super();
	}

	public InitDemoCACer(ParseXML parseXML) throws IDAException {
		super(parseXML);
	}

	public void initialize() throws IDAException {
		this.demoCACertValidity = this.init.getNumber("DemoCACertValidity");
		this.democaSigningAlg = init.getString("CASigningAlg");
		democaKeySize = init.getNumber("SigningKeySize");
		if(democaSigningAlg.equalsIgnoreCase("SHA1withRSA")){
			democaStoreAlg = "RSA";
		}else if(democaSigningAlg.equalsIgnoreCase("SM3WITHSM2")){
			democaStoreAlg = "SM2";
		}
		this.genCerDN = init.getString("CASubject");
		this.democaKeyStore = this.init.getString("SigningKeyStore");
	}
	public void makeDemoCACer() throws IDAException{
		KeyPair kPair = KeyUtils.createKeyPair(democaStoreAlg,
				Keytype.SOFT_VALUE, democaKeySize);
		String snStr = CodeGenerator.generateRefCode();
		X509Principal x509Principal = new X509Principal(this.genCerDN);
		// 默认签发一年
		GregorianCalendar localGregorianCalendar = new GregorianCalendar();
		Date localDate1 = new Date();
		localGregorianCalendar.setTime(localDate1);
		// 6 代表的是天 是一个Type
		localGregorianCalendar.add(6, demoCACertValidity);
		Date localDate2 = localGregorianCalendar.getTime();
		
		X509V3CertificateGenerator v3CertGen = new X509V3CertificateGenerator();
		v3CertGen.reset();
		v3CertGen.setSerialNumber(new BigInteger(snStr, 16));
		v3CertGen.setIssuerDN(x509Principal);
		v3CertGen.setNotBefore(localDate1);
		v3CertGen.setNotAfter(localDate2);
		v3CertGen.setSubjectDN(x509Principal);
		v3CertGen.setSignatureAlgorithm(democaSigningAlg);
		v3CertGen.setPublicKey(kPair.getPublic());
		X509Certificate certificate = null;
		try {
			certificate = v3CertGen.generate(kPair.getPrivate());
		} catch (Exception e) {
			OperateException oexception = new OperateException(OperateException.INIT_DEMOCA_CER_ERROR, OperateException.INIT_DEMOCA_CER_ERROR_DES);
			throw oexception;
		}
		//存储DemoCA.cer证书到本地
		savaRootCert(certificate);
	}
	public void savaRootCert(X509Certificate ccc) throws OperateException{
		byte[] date = null;
		try {
			date = ccc.getEncoded();
		} catch (CertificateEncodingException e) {
			OperateException oexception = new OperateException(OperateException.BYTE_DEMOCA_CER_PATH_ERROR, OperateException.BYTE_DEMOCA_CER_PATH_ERROR_DES);
			throw oexception;
		}
		String jkspathParent;
		String genCerPath = null;
		File localFile1 = new File(democaKeyStore);
		try {
			File localFile2 = new File(localFile1.getParent());
			localFile2.mkdir();
			jkspathParent = localFile1.getParent();
			genCerPath = jkspathParent + "/DemoCA.cer";
		} catch (Exception localException1) {
			OperateException oexception = new OperateException(OperateException.SAVE_DEMOCA_CER_PATH_ERROR, OperateException.SAVE_DEMOCA_CER_PATH_ERROR_DES);
			throw oexception;
		}
		try {
			FileOutputStream localFileOutputStream = new FileOutputStream(
					new File(genCerPath));
			localFileOutputStream.write(date);
			localFileOutputStream.close();
		} catch (Exception localException2) {
			OperateException oexception = new OperateException(OperateException.SAVE_DEMOCA_CER_ERROR, OperateException.SAVE_DEMOCA_CER_ERROR_DES);
			throw oexception;
		}
	}
}
