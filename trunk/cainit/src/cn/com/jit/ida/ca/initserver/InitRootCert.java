package cn.com.jit.ida.ca.initserver;

import cn.com.jit.ida.IDAException;
import cn.com.jit.ida.ca.certmanager.service.operation.CodeGenerator;
import cn.com.jit.ida.ca.config.CAConfig;
import cn.com.jit.ida.ca.config.InternalConfig;
import cn.com.jit.ida.ca.db.DBException;
import cn.com.jit.ida.ca.issue.ISSException;
import cn.com.jit.ida.ca.issue.IssuanceManager;
import cn.com.jit.ida.ca.issue.entity.CAEntity;
import cn.com.jit.ida.globalconfig.ConfigException;
import cn.com.jit.ida.globalconfig.ParseXML;
import cn.com.jit.ida.globalconfig.ProtectConfig;
import cn.com.jit.ida.util.pki.PKIException;
import cn.com.jit.ida.util.pki.Parser;
import cn.com.jit.ida.util.pki.cert.X509Cert;
import cn.com.jit.ida.util.pki.cert.X509CertGenerator;
import cn.com.jit.ida.util.pki.cipher.JKey;
import cn.com.jit.ida.util.pki.cipher.JKeyPair;
import cn.com.jit.ida.util.pki.cipher.Mechanism;
import cn.com.jit.ida.util.pki.cipher.Session;
import cn.com.jit.ida.util.pki.cipher.param.GenKeyAttribute;
import cn.com.jit.ida.util.pki.encoders.Base64;
import cn.com.jit.ida.util.pki.extension.AuthorityKeyIdentifierExt;
import cn.com.jit.ida.util.pki.extension.BasicConstraintsExt;
import cn.com.jit.ida.util.pki.extension.CRLDistPointExt;
import cn.com.jit.ida.util.pki.extension.CertificatePoliciesExt;
import cn.com.jit.ida.util.pki.extension.DistributionPointExt;
import cn.com.jit.ida.util.pki.extension.Extension;
import cn.com.jit.ida.util.pki.extension.KeyUsageExt;
import cn.com.jit.ida.util.pki.extension.PolicyInformationExt;
import cn.com.jit.ida.util.pki.extension.SubjectKeyIdentifierExt;
import cn.com.jit.ida.util.pki.pkcs.P7B;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.Key;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

public class InitRootCert {
	Session session;
	ParseXML config;
	JKeyPair jKeyPair = null;
	byte[] RootCerts;
	byte[] RootCert;
	KeyStore keyStore;
	char[] KeyStorePWD;
	String DN;
	String deviceName;

	public void run(Session paramSession, ParseXML paramParseXML)
			throws InitServerException {
		this.session = paramSession;
		this.config = paramParseXML;
		this.KeyStorePWD = paramParseXML.getString("SigningKeyStorePWD")
				.toCharArray();
		this.deviceName = paramParseXML.getString("CASigningDeviceID");
		this.DN = paramParseXML.getString("CASubject");
		String str = paramParseXML.getString("CACertChainPath");
		if ((str == null) || (str.trim().equals(""))) {
			//产生KeyPair
			GenerateKey();
			//生成自签名的证书cer 当然是利用上面的密钥对
			GenerateSelfCert();
			//保存根证书rootcert.p7b  里面只含有一个DemoCA的根证书
			SavaRootCert();
			//keyStore里面储存上面产生的p7b
			GenKeyStore();
			//储存keystore到jks文件
			SavaKeyStore();
			//是否以LDAP形式发布根CA签名证书---是否发布以文件形式发布根CA签名证书
			pubCert(this.RootCert, paramParseXML);
		} else {
			File localFile = new File(str);
			if (!localFile.exists())
				throw new InitServerException("0999",
						"CA根证书文件不存在，请检查<CACertChainPath>项配置是否正确");
			byte[] arrayOfByte = null;
			try {
				FileInputStream localFileInputStream = new FileInputStream(
						localFile);
				arrayOfByte = new byte[localFileInputStream.available()];
				localFileInputStream.read(arrayOfByte);
				localFileInputStream.close();
			} catch (IOException localIOException) {
				throw new InitServerException("0999", "读取CA根证书文件出错:"
						+ localIOException.toString());
			}
			ImportCert localImportCert = new ImportCert(arrayOfByte,
					paramParseXML);
			localImportCert.run();
			P7B localP7B = new P7B();
			X509Cert[] arrayOfX509Cert = null;
			try {
				arrayOfX509Cert = localP7B.parseP7b(arrayOfByte);
				for (int i = 0; i < arrayOfX509Cert.length; i++)
					pubCert(arrayOfX509Cert[i].getEncoded(), paramParseXML);
			} catch (Exception localException) {
				throw new InitServerException("0999", "发布CA根证书文件出错:"
						+ localException.toString());
			}
		}
	}

	private void GenerateKey() throws InitServerException {
		String str = this.config.getString("CASigningAlg");
		if (str == null)
			throw new InitServerException("0922", "证书签名算法错误");
		Mechanism localMechanism;
		if ((str.equalsIgnoreCase("MD5withRSAEncryption"))
				|| (str.equalsIgnoreCase("SHA1withRSAEncryption")))
			localMechanism = new Mechanism("RSA");
		else
			throw new InitServerException("0922", "证书签名算法错误");
		if (this.deviceName.equalsIgnoreCase("JSOFT_LIB")) {
			int j = 0;
			try {
				j = this.config.getNumber("SigningKeySize");
			} catch (ConfigException localConfigException2) {
				throw new InitServerException("0920", "CA密钥长度错误",
						localConfigException2);
			}
			try {
				this.jKeyPair = this.session.generateKeyPair(localMechanism, j);
			} catch (PKIException localPKIException1) {
				throw new InitServerException(localPKIException1.getErrCode(),
						localPKIException1.getErrDesc());
			}
		} else {
			int i;
			try {
				i = this.config.getNumber("CASigningKeyID");
			} catch (ConfigException localConfigException1) {
				throw new InitServerException("0964", "CA签名密钥ID错误",
						localConfigException1);
			}
			if (i <= 0)
				throw new InitServerException("0964", "CA签名密钥ID错误");
			GenKeyAttribute localGenKeyAttribute = new GenKeyAttribute();
			localGenKeyAttribute.setIsExport(false);
			localGenKeyAttribute.setKeyNum(i);
			localMechanism.setParam(localGenKeyAttribute);
			try {
				this.jKeyPair = this.session.generateKeyPair(localMechanism, 0);
			} catch (PKIException localPKIException2) {
				throw new InitServerException(localPKIException2.getErrCode(),
						localPKIException2.getErrDesc());
			}
		}
	}

	public void GenerateSelfCert() throws InitServerException {
		X509CertGenerator localX509CertGenerator = new X509CertGenerator();
		try {
			localX509CertGenerator.setIssuer(this.DN);
			localX509CertGenerator.setSubject(this.DN);
		} catch (PKIException localPKIException1) {
			throw new InitServerException(localPKIException1.getErrCode(),
					localPKIException1.getErrDesc(), localPKIException1);
		}
		try {
			String str1 = CodeGenerator.generateRefCode();
			localX509CertGenerator.setSerialNumber(str1);
		} catch (PKIException localPKIException2) {
			throw new InitServerException(localPKIException2.getErrCode(),
					localPKIException2.getErrDesc(), localPKIException2);
		}
		int i = 0;
		try {
			i = this.config.getNumber("SigningCertValidity");
		} catch (ConfigException localConfigException) {
			throw new InitServerException("0963", "CA证书有效期错误",
					localConfigException);
		}
		GregorianCalendar localGregorianCalendar = new GregorianCalendar();
		Date localDate1 = new Date();
		localGregorianCalendar.setTime(localDate1);
		//6 代表的是天 是一个Type
		localGregorianCalendar.add(6, i);
		Date localDate2 = localGregorianCalendar.getTime();
		try {
			localX509CertGenerator.setNotBefore(localDate1);
			localX509CertGenerator.setNotAfter(localDate2);
		} catch (PKIException localPKIException3) {
			throw new InitServerException("0963", "CA证书有效期错误",
					localPKIException3);
		}
		String str2 = this.config.getString("CASigningAlg");
		try {
			localX509CertGenerator.setSignatureAlg(str2);
		} catch (PKIException localPKIException4) {
			throw new InitServerException(localPKIException4.getErrCode(),
					localPKIException4.getErrDesc(), localPKIException4);
		}
		try {
			localX509CertGenerator.setPublicKey(this.jKeyPair.getPublicKey());
		} catch (PKIException localPKIException5) {
			throw new InitServerException(localPKIException5.getErrCode(),
					localPKIException5.getErrDesc(), localPKIException5);
		}
		addCRLDistPointExt(localX509CertGenerator);
		addKeyUsageExt(localX509CertGenerator);
		addBasicConstraints(localX509CertGenerator);
		addCertificatePolicy(localX509CertGenerator);
		addSubjectKeyIdentifier(localX509CertGenerator);
		addAuthorityKeyIdentifier(localX509CertGenerator);
		try {
			this.RootCert = localX509CertGenerator.generateX509Cert(
					this.jKeyPair.getPrivateKey(), this.session);
			X509Cert localX509Cert = new X509Cert(this.RootCert);
			X509Cert[] arrayOfX509Cert = { localX509Cert };
			P7B localP7B = new P7B();
			this.RootCerts = localP7B.generateP7bData_B64(arrayOfX509Cert);
		} catch (PKIException localPKIException6) {
			throw new InitServerException(localPKIException6.getErrCode(),
					localPKIException6.getErrDesc(), localPKIException6);
		}
	}

	private void addCertificatePolicy(X509CertGenerator paramX509CertGenerator)
			throws InitServerException {
		try {
			String str1 = this.config.getString("CertificatePolicy_IsExist");
			if (!str1.equalsIgnoreCase("true"))
				return;
			str1 = this.config.getString("CertificatePolicies");
			String[] arrayOfString = str1.split("[;]");
			CertificatePoliciesExt localCertificatePoliciesExt = new CertificatePoliciesExt();
			for (int i = 0; i < arrayOfString.length; i++) {
				String str2 = arrayOfString[i];
				if ((str2 == null) || (str2.trim().equals("")))
					continue;
				int j = str2.indexOf("=");
				String str3 = "2.5.29.32";
				if (j != -1) {
					str3 = str2.substring(0, j);
					String str4 = str2.substring(j + 1);
					localCertificatePoliciesExt.addPolicy(str3.trim());
					if ((str4 == null) || (str4.trim().equals("")))
						continue;
					localCertificatePoliciesExt.getPolicy(i)
							.addPolicyQualifierinfo(str4.trim());
				} else {
					str3 = str2.substring(0);
					localCertificatePoliciesExt.addPolicy(str3.trim());
				}
			}
			if (this.config.getString("CertificatePolicy_IsCritical")
					.equalsIgnoreCase("true"))
				localCertificatePoliciesExt.setCritical(true);
			paramX509CertGenerator.addExtensiond(localCertificatePoliciesExt);
		} catch (PKIException localPKIException) {
			throw new InitServerException(localPKIException.getErrCode(),
					localPKIException.getErrDesc(), localPKIException);
		}
	}

	private void addAuthorityKeyIdentifier(
			X509CertGenerator paramX509CertGenerator)
			throws InitServerException {
		try {
			String str = this.config
					.getString("AuthorityKeyIdentifier_IsExist");
			if (!str.equalsIgnoreCase("true"))
				return;
			AuthorityKeyIdentifierExt localAuthorityKeyIdentifierExt = new AuthorityKeyIdentifierExt(
					this.jKeyPair.getPublicKey());
			if (this.config.getString("AuthorityKeyIdentifier_IsCritical")
					.equalsIgnoreCase("true"))
				localAuthorityKeyIdentifierExt.setCritical(true);
			paramX509CertGenerator
					.addExtensiond(localAuthorityKeyIdentifierExt);
		} catch (PKIException localPKIException) {
			throw new InitServerException(localPKIException.getErrCode(),
					localPKIException.getErrDesc(), localPKIException);
		}
	}

	private void addSubjectKeyIdentifier(
			X509CertGenerator paramX509CertGenerator)
			throws InitServerException {
		try {
			String str = this.config.getString("SubjectKeyIdentifier_IsExist");
			if (!str.equalsIgnoreCase("true"))
				return;
			SubjectKeyIdentifierExt localSubjectKeyIdentifierExt = new SubjectKeyIdentifierExt(
					this.jKeyPair.getPublicKey());
			if (this.config.getString("SubjectKeyIdentifier_IsCritical")
					.equalsIgnoreCase("true"))
				localSubjectKeyIdentifierExt.setCritical(true);
			paramX509CertGenerator.addExtensiond(localSubjectKeyIdentifierExt);
		} catch (PKIException localPKIException) {
			throw new InitServerException(localPKIException.getErrCode(),
					localPKIException.getErrDesc(), localPKIException);
		}
	}

	private void addBasicConstraints(X509CertGenerator paramX509CertGenerator)
			throws InitServerException {
		try {
			String str = this.config.getString("BasicConstraints_IsExist");
			if (!str.equalsIgnoreCase("true"))
				return;
			int i = 0;
			try {
				i = this.config.getNumber("BasicConstraints_PathLength");
			} catch (ConfigException localConfigException) {
				i = -1;
			}
			BasicConstraintsExt localBasicConstraintsExt = null;
			if (i >= 0)
				localBasicConstraintsExt = new BasicConstraintsExt(true, i);
			else
				localBasicConstraintsExt = new BasicConstraintsExt(true);
			if (this.config.getString("BasicConstraints_IsCritical")
					.equalsIgnoreCase("true"))
				localBasicConstraintsExt.setCritical(true);
			paramX509CertGenerator.addExtensiond(localBasicConstraintsExt);
		} catch (PKIException localPKIException) {
			throw new InitServerException(localPKIException.getErrCode(),
					localPKIException.getErrDesc(), localPKIException);
		}
	}

	private void addCRLDistPointExt(X509CertGenerator paramX509CertGenerator)
			throws InitServerException {
		try {
			if (!this.config.getString("CDP_IsExist").equalsIgnoreCase("true"))
				return;
			Object localObject1 = null;
			String str1 = this.config.getString("CRLLDAPPath");
			DistributionPointExt localDistributionPointExt1 = null;
			DistributionPointExt localDistributionPointExt2 = null;
			DistributionPointExt localDistributionPointExt3 = null;
			ArrayList localArrayList = new ArrayList();
			int i = 0;
			
			if (this.config.getString("CDP_DN_Publish")
					.equalsIgnoreCase("true")) {
				String str3 = null;
				String str2 = this.config.getString("BaseDN");
				if (str1.trim().equals(""))
					str3 = "CN=CRL1," + str2;
				else
					str3 = "CN=CRL1," + str1 + "," + str2;
				localDistributionPointExt1 = new DistributionPointExt();
				localDistributionPointExt1.setDistributionPointName(4, str3);
				localArrayList.add(localDistributionPointExt1);
				i++;
			}
			String str2 = this.config.getString("CDP_LDAP_URI");
			if (this.config.getString("CDP_LDAP_URI_Publish").equalsIgnoreCase(
					"true")) {
				String str3 = this.config.getString("BaseDN");
				InternalConfig localObject2 = InternalConfig.getInstance();
				String localObject3 = ((InternalConfig) localObject2).getCrlEnding();
				String str4 = null;
				if (!str2.endsWith("/"))
					str2 = str2 + "/";
				if (str1.equalsIgnoreCase(" "))
					str4 = str2 + "CN=CRL1," + str3 + (String) localObject3;
				else
					str4 = str2 + "CN=CRL1," + str1 + "," + str3
							+ (String) localObject3;
				localDistributionPointExt3 = new DistributionPointExt();
				localDistributionPointExt3.setDistributionPointName(6, str4);
				localArrayList.add(localDistributionPointExt3);
				i++;
			}
			String str3 = this.config.getString("CDP_URI");
			if (this.config.getString("CDP_URI_Publish").equalsIgnoreCase(
					"true")) {
				String localObject2 = null;
				if (str3.endsWith("/"))
					localObject2 = str3 + "crl1.crl";
				else
					localObject2 = str3 + "/crl1.crl";
				localDistributionPointExt2 = new DistributionPointExt();
				localDistributionPointExt2.setDistributionPointName(6,
						(String) localObject2);
				localArrayList.add(localDistributionPointExt2);
				i++;
			}
			if (i == 0)
				return;
			DistributionPointExt[] localObject2 = new DistributionPointExt[i];
			localArrayList.toArray(localObject2);
			Object localObject3 = new CRLDistPointExt();
			((CRLDistPointExt) localObject3)
					.setDistributionPointExts(localObject2);
			if (this.config.getString("CDP_IsCritical")
					.equalsIgnoreCase("true"))
				((CRLDistPointExt) localObject3).setCritical(true);
			paramX509CertGenerator.addExtensiond((Extension) localObject3);
		} catch (PKIException localPKIException) {
			throw new InitServerException(localPKIException.getErrCode(),
					localPKIException.getErrDesc(), localPKIException
							.getHistory());
		} catch (IDAException localIDAException) {
			throw new InitServerException(localIDAException.getErrCode(),
					localIDAException.getErrDesc(), localIDAException
							.getHistory());
		}
	}

	private void addKeyUsageExt(X509CertGenerator paramX509CertGenerator)
			throws InitServerException {
		try {
			String str = this.config.getString("KeyUsage_IsExist");
			if (!str.equalsIgnoreCase("true"))
				return;
			KeyUsageExt localKeyUsageExt = new KeyUsageExt();
			str = this.config.getString("KeyUsage_DIGITAL_SIGNATURE");
			if (str.equalsIgnoreCase("true"))
				localKeyUsageExt.addKeyUsage("digitalSignature");
			str = this.config.getString("KeyUsage_NON_REPUDIATION");
			if (str.equalsIgnoreCase("true"))
				localKeyUsageExt.addKeyUsage("nonRepudiation");
			str = this.config.getString("KeyUsage_KEY_ENCIPHERMENT");
			if (str.equalsIgnoreCase("true"))
				localKeyUsageExt.addKeyUsage("keyEncipherment");
			str = this.config.getString("KeyUsage_DATA_ENCIPHERMENT");
			if (str.equalsIgnoreCase("true"))
				localKeyUsageExt.addKeyUsage("dataEncipherment");
			str = this.config.getString("KeyUsage_KEY_AGREEMENT");
			if (str.equalsIgnoreCase("true"))
				localKeyUsageExt.addKeyUsage("keyAgreement");
			str = this.config.getString("KeyUsage_KEY_CERT_SIGN");
			if (str.equalsIgnoreCase("true"))
				localKeyUsageExt.addKeyUsage("keyCertSign");
			str = this.config.getString("KeyUsage_CRL_SIGN");
			if (str.equalsIgnoreCase("true"))
				localKeyUsageExt.addKeyUsage("cRLSign");
			if (this.config.getString("KeyUsage_IsCritical").equalsIgnoreCase(
					"true"))
				localKeyUsageExt.setCritical(true);
			paramX509CertGenerator.addExtensiond(localKeyUsageExt);
		} catch (PKIException localPKIException) {
			throw new InitServerException(localPKIException.getErrCode(),
					localPKIException.getErrDesc(), localPKIException);
		}
	}

	void GenKeyStore() throws InitServerException {
		Certificate localCertificate = null;
		try {
			this.keyStore = KeyStore.getInstance("JKS");
			this.keyStore.load(null, null);
			ByteArrayInputStream localByteArrayInputStream = new ByteArrayInputStream(
					this.RootCert);
			CertificateFactory localCertificateFactory = CertificateFactory
					.getInstance("X.509");
			localCertificate = localCertificateFactory
					.generateCertificate(localByteArrayInputStream);
			Certificate[] arrayOfCertificate = { localCertificate };
			if (this.deviceName.equalsIgnoreCase("JSOFT_LIB")) {
				Key localKey = Parser.convertKey(this.jKeyPair.getPrivateKey());
				this.keyStore.setKeyEntry(this.DN, localKey, this.KeyStorePWD,
						arrayOfCertificate);
			} else {
				for (int i = 0; i < arrayOfCertificate.length; i++)
					this.keyStore.setCertificateEntry(this.DN,
							arrayOfCertificate[i]);
			}
		} catch (Exception localException) {
			throw new InitServerException("0917", "产生KeyStore错误",
					localException);
		}
	}

	void SavaKeyStore() throws InitServerException {
		FileOutputStream localFileOutputStream = null;
		Object localObject1 = null;
		String str = this.config.getString("SigningKeyStore");
		File localFile = null;
		try {
			localFile = new File(str);
			new File(localFile.getParent()).mkdir();
		} catch (Exception localException1) {
		}
		try {
			localFileOutputStream = new FileOutputStream(localFile);
			this.keyStore.store(localFileOutputStream, this.KeyStorePWD);
		} catch (Exception localException2) {
			throw new InitServerException("0949", "产生KeyStore文件错误",
					localException2);
		} finally {
			try {
				if (localFileOutputStream != null)
					localFileOutputStream.close();
			} catch (IOException localIOException) {
			}
		}
	}

	void SavaRootCert() throws InitServerException {
		String str2 = ".";
		File localFile1 = new File(this.config.getString("SigningKeyStore"));
		try {
			File localFile2 = new File(localFile1.getParent());
			localFile2.mkdir();
			str2 = localFile1.getParent();
		} catch (Exception localException1) {
		}
		String str1 = str2 + "/rootcert.p7b";
		try {
			FileOutputStream localFileOutputStream = new FileOutputStream(
					new File(str1));
			localFileOutputStream.write(this.RootCerts);
			localFileOutputStream.close();
		} catch (Exception localException2) {
			throw new InitServerException("0950", "存储根证书错误", localException2);
		}
	}

	private void saveCert(X509Cert paramX509Cert, JKey paramJKey,
			String paramString1, String paramString2)
			throws InitServerException {
		CACertInfo localCACertInfo = new CACertInfo();
		localCACertInfo.setBaseDN(this.config.getString("BaseDN"));
		localCACertInfo.setCA_DESC("");
		localCACertInfo.setCA_ID(0);
		localCACertInfo.setSubject(paramX509Cert.getSubject());
		try {
			localCACertInfo.setCertEntity(Base64.encode(paramX509Cert
					.getEncoded()));
		} catch (PKIException localPKIException) {
			throw new InitServerException(localPKIException.getErrCode(),
					localPKIException.getErrDesc(), localPKIException);
		}
		localCACertInfo.setCertStatus(paramString1);
		localCACertInfo.setDeviceID(paramString2);
		try {
			if (paramString2.equalsIgnoreCase("JSOFT_LIB"))
				localCACertInfo.setPrivateKey(ProtectConfig.getInstance()
						.Encrypto(new String(paramJKey.getKey())));
			else if (paramString2.equalsIgnoreCase("JSJY05B_LIB"))
				localCACertInfo.setPrivateKey(ProtectConfig.getInstance()
						.Encrypto(Long.toString(paramJKey.getKeyID())));
			else
				throw new InitServerException("0901", "机密设备名称错误");
		} catch (ConfigException localConfigException) {
			throw new InitServerException(localConfigException.getErrCode(),
					localConfigException.getErrDesc(), localConfigException);
		}
		localCACertInfo.setSN(paramX509Cert.getStringSerialNumber());
		try {
			DBInit localDBInit = DBInit.getInstance();
			localDBInit.saveCACert(localCACertInfo);
		} catch (DBException localDBException) {
			throw new InitServerException(localDBException.getErrCode(),
					localDBException.getErrDesc(), localDBException);
		}
	}

	public static void pubCert(byte[] paramArrayOfByte, ParseXML paramParseXML)
			throws InitServerException {
		IssuanceManager localIssuanceManager = null;
		try {
			localIssuanceManager = IssuanceManager.getInstance();
		} catch (Exception localException) {
			throw new InitServerException("0999", "系统错误 构造发布管理器失败",
					localException);
		}
		CAEntity localCAEntity;
		String str;
		if (paramParseXML.getString("RootCertLDAPPublish").equalsIgnoreCase(
				"true")) {
			localCAEntity = new CAEntity("LDAP");
			str = null;
			try {
				str = CAConfig.getInstance().getBaseDN();
			} catch (IDAException localIDAException1) {
				throw new InitServerException(localIDAException1.getErrCode(),
						localIDAException1.getErrDesc(), localIDAException1);
			}
			localCAEntity.setBaseDN(str);
			X509Cert localX509Cert1 = null;
			try {
				localX509Cert1 = new X509Cert(paramArrayOfByte);
			} catch (PKIException localPKIException1) {
				throw new InitServerException(localPKIException1.getErrCode(),
						localPKIException1.getErrDesc(), localPKIException1);
			}
			try {
				localCAEntity.setCACert(paramArrayOfByte);
				localCAEntity.setCACertSN(localX509Cert1
						.getStringSerialNumber());
				localCAEntity.setCASubject(localX509Cert1.getSubject());
			} catch (ISSException localISSException1) {
				throw new InitServerException(localISSException1.getErrCode(),
						localISSException1.getErrDesc(), localISSException1);
			}
			try {
				localIssuanceManager.issue(localCAEntity);
			} catch (ISSException localISSException2) {
				throw new InitServerException(localISSException2.getErrCode(),
						localISSException2.getErrDesc(), localISSException2);
			}
		}
		if (paramParseXML.getString("RootCertFilePublish").equalsIgnoreCase(
				"true")) {
			localCAEntity = new CAEntity("FILE");
			str = null;
			try {
				str = CAConfig.getInstance().getBaseDN();
			} catch (IDAException localIDAException2) {
				throw new InitServerException(localIDAException2.getErrCode(),
						localIDAException2.getErrDesc(), localIDAException2);
			}
			localCAEntity.setBaseDN(str);
			X509Cert localX509Cert2 = null;
			try {
				localX509Cert2 = new X509Cert(paramArrayOfByte);
			} catch (PKIException localPKIException2) {
				throw new InitServerException(localPKIException2.getErrCode(),
						localPKIException2.getErrDesc(), localPKIException2);
			}
			try {
				localCAEntity.setCACert(paramArrayOfByte);
				localCAEntity.setCACertSN(localX509Cert2
						.getStringSerialNumber());
				localCAEntity.setCASubject(localX509Cert2.getSubject());
			} catch (ISSException localISSException3) {
				throw new InitServerException(localISSException3.getErrCode(),
						localISSException3.getErrDesc(), localISSException3);
			}
			try {
				localIssuanceManager.issue(localCAEntity);
			} catch (ISSException localISSException4) {
				throw new InitServerException(localISSException4.getErrCode(),
						localISSException4.getErrDesc(), localISSException4);
			}
		}
	}
}

/*
 * Location: C:\Program Files\JIT\CA50\lib\IDA\ida.jar Qualified Name:
 * cn.com.jit.ida.ca.initserver.InitRootCert JD-Core Version: 0.6.0
 */