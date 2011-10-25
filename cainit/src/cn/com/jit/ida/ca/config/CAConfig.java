package cn.com.jit.ida.ca.config;

import cn.com.jit.ida.IDAException;
import cn.com.jit.ida.ca.db.DBManager;
import cn.com.jit.ida.ca.initserver.InitServerException;
import cn.com.jit.ida.ca.initserver.P7Bxp;
import cn.com.jit.ida.globalconfig.ConfigException;
import cn.com.jit.ida.globalconfig.ConfigFromDB;
import cn.com.jit.ida.globalconfig.ConfigFromXML;
import cn.com.jit.ida.globalconfig.ConfigTool;
import cn.com.jit.ida.util.pki.PKIException;
import cn.com.jit.ida.util.pki.cert.X509Cert;
import cn.com.jit.ida.util.pki.cipher.JCrypto;
import cn.com.jit.ida.util.pki.cipher.JKey;
import cn.com.jit.ida.util.pki.cipher.JKeyPair;
import cn.com.jit.ida.util.pki.cipher.Mechanism;
import cn.com.jit.ida.util.pki.cipher.Session;
import cn.com.jit.ida.util.pki.cipher.param.GenKeyAttribute;
import java.io.FileInputStream;
import java.security.Key;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.util.Enumeration;
import java.util.Vector;

public class CAConfig {
	private ConfigFromDB config;
	private ConfigFromXML configXML;
	private static CAConfig instance;
	private String isSendAuthCode;
	private String authorityInformation;
	private JKey privateKey;
	private X509Cert[] certs;
	private String keyStorePWD;
	private String DN;
	private String accessMethod;
	private long certPubPeriods;
	private String caAdminSN;
	private String caAdminDN;
	private String commCertDN;
	private String certificatePolicies;
	private String baseDN;
	private int CASignKeyID;
	private int CACommKeyID;
	private String certFilePath;
	private int countPerPage;
	private long timeDifAllow;
	private int maxCountPerPage;
	private String CASignAlg;
	private int certSNLen;
	private int authCodeValidity;
	private long synchrointerval;
	private int authCodeLength;
	private String adminKeyStorePath;
	private String auditAdminKeyStorePath;
	private String auditAdminSN;
	private String auditAdminDN;
	private boolean enableUpdatePeriod;

	private CAConfig() throws IDAException {
		init();
	}

	public static CAConfig getInstance() throws IDAException {
		if (instance == null)
			instance = new CAConfig();
		return instance;
	}

	public void init() throws IDAException {
		int i = 0;
		DBManager localDBManager = DBManager.getInstance();
		this.config = new ConfigFromDB(localDBManager, "CAConfig");
		this.configXML = new ConfigFromXML("CAConfig", "./config/CAConfig.xml");
		this.isSendAuthCode = this.config.getString("isSendAuthCode");
		String str1 = this.configXML.getString("TimeDifAllow");
		try {
			this.timeDifAllow = Long.parseLong(str1);
		} catch (Exception localException) {
			throw new IDAException("0930", "<TimeDifAllow>配置错误");
		}
		this.authorityInformation = this.configXML
				.getString("AuthorityInfoAccess");
		this.keyStorePWD = this.config.getString("SigningKeyStorePWD");
		this.DN = this.config.getString("CASubject");
		this.accessMethod = "1.3.6.1.5.5.7.48.1";
		this.certPubPeriods = this.configXML.getLong("CertPubPeriods");
		this.caAdminSN = this.config.getString("CAAdminSN");
		this.caAdminDN = this.config.getString("CAAdminDN");
		this.auditAdminSN = this.config.getString("AuditAdminSN");
		this.auditAdminDN = this.config.getString("AuditAdminDN");
		this.baseDN = this.config.getString("BaseDN");
		this.commCertDN = this.config.getString("CommCertDN");
		if ((this.commCertDN == null) || (this.commCertDN.trim().equals(""))) {
			String str2 = this.configXML.getString("ServerAddress");
			this.commCertDN = ("CN=" + str2 + "," + this.baseDN);
		}
		this.certificatePolicies = this.config.getString("CertificatePolicies");
		this.CASignKeyID = this.configXML.getNumber("CASigningKeyID");
		this.CACommKeyID = this.configXML.getNumber("CACommKeyID");
		this.certFilePath = this.configXML.getString("CertFilePath");
		this.CASignAlg = this.configXML.getString("CASigningAlg");
		this.maxCountPerPage = this.configXML.getNumber("MaxCountPerPage");
		this.countPerPage = this.configXML.getNumber("CountPerPage");
		i = this.configXML.getNumber("CertSNLength");
		if ((i < 6) || (i > 40))
			throw new IDAException("0985", "证书序列号长度错误");
		this.certSNLen = i;
		i = this.configXML.getNumber("AuthCodeLength");
		if ((i < 6) || (i > 40))
			throw new IDAException("0986", "证书授权码长度错误");
		this.authCodeLength = i;
		this.adminKeyStorePath = this.configXML.getString("AdminKeyStorePath");
		this.auditAdminKeyStorePath = this.configXML
				.getString("AuditAdminKeyStorePath");
		this.synchrointerval = this.configXML.getLong("Synchrointerval");
		this.authCodeValidity = this.configXML.getNumber("AuthCodeValidity");
		this.enableUpdatePeriod = this.configXML
				.getBoolean("EnableUpdatePeriod");
		readSigningCertKeyStore();
	}

	void readSigningCertKeyStore() throws IDAException {
		CryptoConfig localCryptoConfig = null;
		JCrypto.getInstance().initialize("JSOFT_LIB", null);
		localCryptoConfig = CryptoConfig.getInstance();
		String str1 = localCryptoConfig.getRootCertPWD();
		if (str1.equals("")) {
			if (CAConfigConstant.SignPwd == null) {
				str1 = ConfigTool.getPassword("CA签名密钥容器密码", 1, 16);
				if (str1 == null)
					throw new InitServerException("0988", "用户取消操作");
				CAConfigConstant.SaveSignPwd = "1";
				CAConfigConstant.SignPwd = str1;
				localCryptoConfig.setRootCertPWD(str1);
			} else {
				str1 = CAConfigConstant.SignPwd;
			}
			if (str1 == null)
				throw new IDAException("用户取消");
		}
		char[] arrayOfChar = str1.toCharArray();
		if (localCryptoConfig.getDeviceID().equalsIgnoreCase("JSJY05B_LIB"))
			JCrypto.getInstance().initialize("JSJY05B_LIB", new Integer(2));
		Object localObject2;
		Object localObject1;
		Object localObject3;
		try {
			KeyStore localKeyStore = KeyStore.getInstance("JKS");
			FileInputStream localFileInputStream = new FileInputStream(
					localCryptoConfig.getRootCert());
			localKeyStore.load(localFileInputStream, arrayOfChar);
			Enumeration localEnumeration = localKeyStore.aliases();
			Vector localVector = new Vector(1);
			while (localEnumeration.hasMoreElements()) {
				String str2 = (String) localEnumeration.nextElement();
				Certificate localCertificate = localKeyStore
						.getCertificate(str2);
				if (localCertificate != null) {
					localVector.add(localCertificate);
					continue;
				}
				localObject2 = localKeyStore.getCertificateChain(str2);
				if (localObject2 == null)
					continue;
				Certificate[] certss = (Certificate[]) localObject2;
				for (int k = 0; k < certss.length; k++) {
					localVector.add(certss[k]);
				}
			}
			this.certs = new X509Cert[localVector.size()];
			int i = 1;
			for (int j = 0; j < localVector.size(); j++) {
				byte[] arrayOfByte1 = ((Certificate) localVector.get(j))
						.getEncoded();
				this.certs[j] = new X509Cert(arrayOfByte1);
				if (!this.certs[j].getSubject().equalsIgnoreCase(this.DN))
					continue;
				i = 0;
			}
			if (i != 0) {
				if ((CAConfigConstant.SaveSignPwd != null)
						&& (CAConfigConstant.SaveSignPwd.equals("1"))) {
					localCryptoConfig.setRootCertPWD("");
					str1 = "";
					CAConfigConstant.SignPwd = null;
					CAConfigConstant.SaveSignPwd = "1";
				}
				throw new ConfigException(ConfigException.KEY_STORE_ERROR,
						"根证书存储错误，未找到匹配的根证书");
			}
			localObject1 = new P7Bxp(this.certs);
			this.certs = ((P7Bxp) localObject1).getCertChain();
			if (localCryptoConfig.getDeviceID().equalsIgnoreCase("JSOFT_LIB")) {
				Key localKey = localKeyStore.getKey(this.DN.toLowerCase(),
						arrayOfChar);
				if (localKey.getAlgorithm() == "RSA")
					this.privateKey = new JKey("RSA_Private", localKey
							.getEncoded());
			} else {
				localObject3 = JCrypto.getInstance();
				localObject2 = ((JCrypto) localObject3)
						.openSession(localCryptoConfig.getDeviceID());
				Mechanism localMechanism = new Mechanism("RSA");
				GenKeyAttribute localGenKeyAttribute = new GenKeyAttribute();
				localGenKeyAttribute.setIsExport(false);
				localGenKeyAttribute.setKeyNum(this.CASignKeyID);
				localMechanism.setParam(localGenKeyAttribute);
				JKeyPair localJKeyPair = null;
				try {
					localJKeyPair = ((Session) localObject2).generateKeyPair(
							localMechanism, 0);
				} catch (PKIException localPKIException2) {
					throw new IDAException(localPKIException2.getErrCode(),
							localPKIException2.getErrDesc());
				}
				this.privateKey = localJKeyPair.getPrivateKey();
			}
		} catch (Exception localException) {
			if ((CAConfigConstant.SaveSignPwd != null)
					&& (CAConfigConstant.SaveSignPwd.equals("1"))) {
				str1 = "";
				CAConfigConstant.SignPwd = null;
				CAConfigConstant.SaveSignPwd = "1";
				localCryptoConfig.setRootCertPWD("");
			}
			throw new ConfigException(ConfigException.KEY_STORE_ERROR,
					"根证书存储错误，获取CA签名私钥失败", localException);
		}
		byte[] arrayOfByte2 = null;
		Vector localVector = null;
		try {
			localObject1 = JCrypto.getInstance();
			Session localSession = ((JCrypto) localObject1)
					.openSession(localCryptoConfig.getDeviceID());
			arrayOfByte2 = "JITIDA".getBytes();
			localObject2 = new Mechanism("SHA1withRSAEncryption");
			byte[] localObject31 = localSession.sign((Mechanism) localObject2,
					this.privateKey, arrayOfByte2);
			if (!localSession.verifySign((Mechanism) localObject2,
					getRootCert().getPublicKey(), arrayOfByte2, localObject31))
				throw new ConfigException(ConfigException.KEY_STORE_ERROR,
						"CA签名密钥错误");
		} catch (PKIException localPKIException1) {
			throw new IDAException(localPKIException1.getErrCode(),
					localPKIException1.getErrDesc(), localPKIException1);
		}
	}

	public String getIsSendAuthCode() {
		return this.isSendAuthCode;
	}

	public void setIsSendAuthCode(String paramString) throws IDAException {
		this.isSendAuthCode = paramString;
		this.config.setString("isSendAuthCode", paramString);
	}

	public long getTimeDifAllow() {
		return this.timeDifAllow * 60000L;
	}

	public void setTimeDifAllow(long paramLong) throws IDAException {
		this.timeDifAllow = (paramLong / 60000L);
		this.configXML.setLong("TimeDifAllow", paramLong / 60000L);
	}

	public String getAuthorityInformation() {
		return this.authorityInformation;
	}

	public void setAuthorityInformation(String paramString) throws IDAException {
		this.authorityInformation = paramString;
		this.configXML.setString("AuthorityInfoAccess", paramString);
	}

	public JKey getPrivateKey() {
		return this.privateKey;
	}

	public X509Cert[] getRootCerts() {
		return this.certs;
	}

	public X509Cert getRootCert() {
		for (int i = 0; i < this.certs.length; i++)
			if (this.certs[i].getSubject().equalsIgnoreCase(this.DN))
				return this.certs[i];
		return null;
	}

	public X509Cert getCommCert() {
		for (int i = 0; i < this.certs.length; i++)
			if (this.certs[i].getSubject().equalsIgnoreCase(this.commCertDN))
				return this.certs[i];
		return null;
	}

	public String getKeyStorePWD() {
		return this.keyStorePWD;
	}

	public void setKeyStorePWD(String paramString) throws IDAException {
		this.keyStorePWD = paramString;
		this.config.setString("SigningKeyStorePWD", paramString);
	}

	public String getDN() {
		return this.DN;
	}

	public void setDN(String paramString) throws IDAException {
		this.DN = paramString;
		this.config.setString("CASubject", paramString);
	}

	public String[][] getCertPubAddress()
  {
    String[][] arrayOfString = { { "URI", getCertFilePath() } };
    return arrayOfString;
  }

	public void setCertPubAddress(String[][] paramArrayOfString)
			throws IDAException {
		setCertFilePath(paramArrayOfString[0][1]);
		this.certFilePath = paramArrayOfString[0][1];
	}

	public String getAccessMethod() {
		return this.accessMethod;
	}

	public void setAccessMethod(String paramString) throws IDAException {
	}

	public long getCertPubPeriods() {
		return this.certPubPeriods * 60000L;
	}

	public void setCertPubPeriods(long paramLong) throws IDAException {
		this.certPubPeriods = paramLong;
		this.configXML.setLong("CertPubPeriods", paramLong / 60000L);
	}

	public String getCaAdminSN() {
		return this.caAdminSN;
	}

	public void setCaAdminSN(String paramString) throws IDAException {
		this.caAdminSN = paramString;
		this.config.setString("CAAdminSN", paramString);
	}

	public String getCaAdminDN() {
		return this.caAdminDN;
	}

	public void setCaAdminDN(String paramString) throws IDAException {
		this.caAdminDN = paramString;
		this.config.setString("CAAdminDN", paramString);
	}

	public String getCommCertDN() {
		return this.commCertDN;
	}

	public void setCommCertDN(String paramString) throws IDAException {
		this.commCertDN = paramString;
		this.config.setString("CommCertDN", paramString);
	}

	public String getCertificatePolicies() {
		return this.certificatePolicies;
	}

	public void setCertificatePolicies(String paramString) throws IDAException {
		this.config.setString("CertificatePolicies", paramString);
		this.certificatePolicies = paramString;
	}

	public String getBaseDN() {
		return this.baseDN;
	}

	public void setBaseDN(String paramString) throws IDAException {
		this.baseDN = paramString;
		this.config.setString("BaseDN", paramString);
	}

	public int getCASignKeyID() {
		return this.CASignKeyID;
	}

	public void setCASignKeyID(int paramInt) throws IDAException {
		this.configXML.setNumber("CASigningKeyID", paramInt);
		this.CASignKeyID = paramInt;
	}

	public String getCertFilePath() {
		return this.certFilePath;
	}

	public void setCertFilePath(String paramString) throws IDAException {
		this.configXML.setString("CertFilePath", paramString);
		this.certFilePath = paramString;
	}

	public int getCountPerPage() {
		return this.countPerPage;
	}

	public void setCountPerPage(int paramInt) throws IDAException {
		this.configXML.setNumber("CountPerPage", paramInt);
		this.countPerPage = paramInt;
	}

	public int getMaxCountPerPage() {
		return this.maxCountPerPage;
	}

	public void setMaxCountPerPage(int paramInt) throws IDAException {
		this.configXML.setNumber("MaxCountPerPage", paramInt);
		this.maxCountPerPage = paramInt;
	}

	public String getCASignAlg() {
		return this.CASignAlg;
	}

	public void setCASignAlg(String paramString) throws IDAException {
		this.CASignAlg = paramString;
		this.configXML.setString("CASigningAlg", paramString);
	}

	public long getSynchrointerval() {
		return this.synchrointerval * 60000L;
	}

	public void setSynchrointerval(long paramLong) throws IDAException {
		this.configXML.setLong("Synchrointerval", paramLong / 60000L);
		this.synchrointerval = paramLong;
	}

	public int getCertSNLen() {
		return this.certSNLen;
	}

	public void setCertSNLen(int paramInt) throws IDAException {
		this.certSNLen = paramInt;
		this.configXML.setNumber("CertSNLength", paramInt);
	}

	public int getAuthCodeValidity() {
		return this.authCodeValidity;
	}

	public void setAuthCodeValidity(int paramInt) throws IDAException {
		this.configXML.setNumber("AuthCodeValidity", paramInt);
		this.authCodeValidity = paramInt;
	}

	public int getAuthCodeLength() {
		return this.authCodeLength;
	}

	public void setAuthCodeLength(int paramInt) throws IDAException {
		this.configXML.setNumber("AuthCodeLength", paramInt);
		this.authCodeLength = paramInt;
	}

	public String getAdminKeyStorePath() {
		return this.adminKeyStorePath;
	}

	public void setAdminKeyStorePath(String paramString) throws IDAException {
		this.configXML.setString("AdminKeyStorePath", paramString);
		this.adminKeyStorePath = paramString;
	}

	public static boolean isInit() {
		return instance != null;
	}

	public String getCAAuditAdminDN() {
		return this.auditAdminDN;
	}

	public String getCAAuditAdminSN() {
		return this.auditAdminSN;
	}

	public void setCAAuditAdminSN(String paramString) throws IDAException {
		this.auditAdminSN = paramString;
		this.config.setString("AuditAdminSN", paramString);
	}

	public void setCAAuditAdminDN(String paramString) throws IDAException {
		this.auditAdminDN = paramString;
		this.config.setString("AuditAdminDN", paramString);
	}

	public String getAuditAdminKeyStorePath() {
		return this.auditAdminKeyStorePath;
	}

	public boolean isEnableUpdatePeriod() {
		return this.enableUpdatePeriod;
	}

	public void setAuditAdminKeyStorePath(String paramString)
			throws IDAException {
		this.configXML.setString("AuditAdminKeyStorePath", paramString);
		this.auditAdminKeyStorePath = paramString;
	}

	public void setEnableUpdatePeriod(boolean paramBoolean) throws IDAException {
		this.configXML.setBoolean("EnableUpdatePeriod", paramBoolean);
		this.enableUpdatePeriod = paramBoolean;
	}
}

/*
 * Location: C:\Program Files\JIT\CA50\lib\IDA\ida.jar Qualified Name:
 * cn.com.jit.ida.ca.config.CAConfig JD-Core Version: 0.6.0
 */