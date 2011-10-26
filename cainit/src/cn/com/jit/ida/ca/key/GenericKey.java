package cn.com.jit.ida.ca.key;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.math.BigInteger;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Enumeration;
import java.util.GregorianCalendar;
import java.util.Vector;

import sun.security.x509.X509CertImpl;
import cn.com.jit.ida.IDAException;
import cn.com.jit.ida.ca.certmanager.service.operation.CodeGenerator;
import cn.com.jit.ida.ca.displayrelated.DbUtils;
import cn.com.jit.ida.ca.exception.OperateException;
import cn.com.jit.ida.ca.key.keyutils.KeyUtils;
import cn.com.jit.ida.globalconfig.ConfigException;
import cn.com.jit.ida.globalconfig.ConfigTool;
import cn.com.jit.ida.globalconfig.KeyPairException;
import fisher.man.jce.X509Principal;
import fisher.man.x509.X509V3CertificateGenerator;

/**
 * 负责存储jks文件信息
 * 
 * @author fangguoqing
 * 
 */
@SuppressWarnings("all")
public class GenericKey {
	private static GenericKey instance = null;
	private boolean m_isNewKeyStore = false;
	private String jksFilePath = null;
	private int m_keyID = -1;
	private char[] m_pwd = null;
	private String m_deviceID = "JSOFT_LIB";
	private Vector m_KeyStruct = new Vector();
	public KeyStore m_keyStore;
	Vector m_TrustCerts = new Vector();
	KeyPair keyPair;
	public static final String JKS = "JKS";
	public static final String PKCS12 = "PKCS12";
	public String fileType;
	public int validity;
	public int adminIdentity;

	/**
	 * 
	 * @param isNewKeyStore是否是新证书
	 * @param jksFilePath路径
	 * @param password密码
	 * @param keyPair
	 * @param fileType文件类型
	 * @throws ConfigException
	 * @throws KeyPairException
	 */
	public GenericKey(boolean isNewKeyStore, String jksFilePath,
			char[] password, KeyPair keyPair, String fileType)
			throws ConfigException, KeyPairException {
		this.m_isNewKeyStore = isNewKeyStore;
		this.jksFilePath = jksFilePath;
		this.m_pwd = password;
		this.keyPair = keyPair;
		this.fileType = fileType;
		initKeyStore();
	}

	/**
	 * 
	 * @param isNewKeyStore是否是新证书
	 * @param jksFilePath路径
	 * @param password密码
	 * @param keyPair
	 * @param fileType文件类型
	 * @throws ConfigException
	 * @throws KeyPairException
	 */
	public GenericKey(boolean isNewKeyStore, String jksFilePath,
			char[] password, String fileType) throws ConfigException,
			KeyPairException {
		this.m_isNewKeyStore = isNewKeyStore;
		this.jksFilePath = jksFilePath;
		this.m_pwd = password;
		this.fileType = fileType;
		initKeyStore();
	}

	/**
	 * 
	 * @param isNewKeyStore是否是新证书
	 * @param jksFilePath路径
	 * @param password密码
	 * @param keyPair
	 * @param fileType文件类型
	 * @throws ConfigException
	 * @throws KeyPairException
	 */
	public GenericKey(boolean isNewKeyStore, String jksFilePath,
			char[] password, String fileType, int validity)
			throws ConfigException, KeyPairException {
		this.m_isNewKeyStore = isNewKeyStore;
		this.jksFilePath = jksFilePath;
		this.m_pwd = password;
		this.fileType = fileType;
		this.validity = validity;
		initKeyStore();
	}

	/**
	 * 初始化KeyStore 用于申请与更新证书
	 * 
	 * @throws ConfigException
	 */
	public void initKeyStore() throws ConfigException {
		try {
			if (this.m_isNewKeyStore) {
				this.m_keyStore = KeyStore.getInstance(this.fileType);
				this.m_keyStore.load(null, null);
				return;
			}
			this.m_keyStore = KeyStore.getInstance(this.fileType);
			FileInputStream localFileInputStream = new FileInputStream(
					this.jksFilePath);
			this.m_keyStore.load(localFileInputStream, this.m_pwd);
			localFileInputStream.close();
		} catch (Exception localException1) {
			if (localException1.getMessage().equals(
					"Keystore was tampered with, or password was incorrect")) {
				throw new ConfigException(ConfigException.KEY_STORE_PWD_ERROR,
						ConfigException.KEY_STORE_PWD_ERROR_DES,
						localException1);
			} else {
				throw new ConfigException(ConfigException.KEY_STORE_ERROR,
						ConfigException.KEY_STORE_ERROR_DES, localException1);
			}
		}
		initData();
	}

	/**
	 * 更新证书前将证书中的信息储存到KeyStore
	 * 
	 * @throws ConfigException
	 */
	public void initData() throws ConfigException {
		try {
			Enumeration localEnumeration = this.m_keyStore.aliases();
			String certMessage;
			Certificate certficate = null;
			Key key;
			while (localEnumeration.hasMoreElements()) {
				certMessage = (String) localEnumeration.nextElement();
				if (this.m_keyStore.isCertificateEntry(certMessage)) {
					certficate = this.m_keyStore.getCertificate(certMessage);
					this.m_TrustCerts.add(certficate);
				} else if (this.m_keyStore.isKeyEntry(certMessage)) {
					key = this.m_keyStore.getKey(certMessage, this.m_pwd);
					Certificate[] certificate = this.m_keyStore
							.getCertificateChain(certMessage);
					this.m_KeyStruct.add(certificate[0]);
				}
			}
		} catch (Exception localException2) {
			throw new ConfigException(ConfigException.KEY_STORE_ERROR,
					ConfigException.KEY_STORE_ERROR_DES, localException2);
		}
	}

	public void updateAdmin(int adminType) throws IDAException{
		X509CertImpl localX509CertImpl = null;
		try {
			localX509CertImpl = (X509CertImpl) m_keyStore
					.getCertificate("s1as");
		} catch (KeyStoreException e) {
			OperateException oexception = new OperateException(
					OperateException.GET_CER_BY_ALIAS_ERROR,
					OperateException.GET_CER_BY_ALIAS_ERROR_DES);
			throw oexception;
		}
		String dn = localX509CertImpl.getSubjectX500Principal().getName();
		String sn = localX509CertImpl.getSerialNumber().toString(16);
		// operate database to update config table
		// the operate only update database
		DbUtils.updateConfig(sn, dn, adminType);
	}

	public String resetDN(String dn) {
		String[] dns = dn.split(",");
		StringBuffer sb = new StringBuffer();
		for (String s : dns) {
			sb.append(s.trim() + ",");
		}
		return sb.substring(0, sb.length() - 1);
	}

	public static boolean isSameDn(String dn1, String dn2) {
		boolean bool = false;
		String arrays1[] = arrayString(dn1);
		String arrays2[] = arrayString(dn2);
		for (int i = 0; i < arrays1.length; i++) {
			if (!arrays1[i].equals(arrays2[i])) {
				bool = false;
				break;
			} else {
				bool = true;
			}
		}
		return bool;
	}

	public static String[] arrayString(String str) {
		String arrays[] = new String[str.split(",").length];
		int i = 0;
		for (String s : str.split(",")) {
			arrays[i++] = s.trim();
		}
		Arrays.sort(arrays);
		return arrays;
	}

	public boolean addCertificateEntry(byte[] certArrayOfByte, char[] password)
			throws KeyPairException {
		return addCertificateEntry(certArrayOfByte, password, true);
	}

	/**
	 * 导入cer通讯证书，之后根据parambool判断是否导入根证书
	 * 
	 * @param certArrayOfByte
	 * @param password
	 * @param parambool
	 *            是否导入根证书
	 * @return
	 * @throws KeyPairException
	 */
	public boolean addCertificateEntry(byte[] certArrayOfByte, char[] password,
			boolean parambool) throws KeyPairException {
		try {
			X509CertImpl x509CertImpl = new X509CertImpl(certArrayOfByte);
			String importDN = x509CertImpl.getSubjectDN().getName();
			String alias = getAlias();
			X509CertImpl localX509CertImpl = (X509CertImpl) m_keyStore
					.getCertificate(alias);
			String dn = localX509CertImpl.getSubjectDN().getName();
			if (!isSameDn(importDN, dn)) {
				KeyPairException kException = new KeyPairException(
						KeyPairException.DN_NOT_SAME_ERROR,
						KeyPairException.DN_NOT_SAME_ERROR_DES);
				throw kException;
			}
			Key privateKey = m_keyStore.getKey(alias, password);
			Certificate[] certificate = new Certificate[] { (Certificate) x509CertImpl };
			m_keyStore.setKeyEntry("s1as", privateKey, password, certificate);
			if (parambool) {
				System.out.println("导入通信证书成功，下面导入根证书.");
				boolean bool = false;
				do {
					bool = addDemoCA(privateKey, password);
				} while (bool == true
						&& ConfigTool.getYesOrNo("是否再次需要导入其他根证书[Y/N]:"));
			}
			return true;
		} catch (Exception e) {
			if (e instanceof KeyPairException) {
				throw (KeyPairException) e;
			}
			KeyPairException kException = new KeyPairException(
					KeyPairException.INIT_CER_ERROR,
					KeyPairException.INIT_CER_ERROR_DES, e);
			throw kException;
		}
	}

	/**
	 * 导入根证书：分为两个步骤 1.导入trustCertificateEntry 2.导入KeyEntry
	 * 
	 * @param privateKey
	 * @param password
	 * @return
	 * @throws KeyPairException
	 */
	public boolean addDemoCA(Key privateKey, char[] password)
			throws KeyPairException {
		String filePath = null;
		do {
			filePath = ConfigTool.getFilePathFromUser("请输入要导入的根证书路径：",
					ConfigTool.FILE_TO_READ, "");
		} while (filePath == null
				&& ConfigTool.getYesOrNo("尚未得到根证书路径，是否重新输入路径？[Y/N]："));
		if (filePath == null) {
			return false;
		}
		byte[] arrayOfByte = null;
		try {
			FileInputStream localFileInputStream = new FileInputStream(filePath);
			arrayOfByte = new byte[localFileInputStream.available()];
			localFileInputStream.read(arrayOfByte);
			localFileInputStream.close();
			X509CertImpl x509CertImpl = new X509CertImpl(arrayOfByte);
			m_keyStore.setCertificateEntry(getAlias(x509CertImpl.getSubjectDN()
					.getName()), x509CertImpl);
			Certificate[] certificate1 = m_keyStore.getCertificateChain("s1as");
			Certificate[] certificateuse = new Certificate[] { (Certificate) x509CertImpl };
			certificateuse = contactCertArr(certificate1, certificateuse);
			m_keyStore
					.setKeyEntry("s1as", privateKey, password, certificateuse);
		} catch (Exception localException) {
			KeyPairException kException = new KeyPairException(
					KeyPairException.INSERT_GEN_CER_ERROR,
					KeyPairException.INSERT_GEN_CER_ERROR_DES, localException);
			throw kException;
		}
		return true;
	}

	public Certificate[] contactCertArr(Certificate[] certificateOld,
			Certificate[] certificateNew) {
		Certificate[] certificate = new Certificate[certificateOld.length
				+ certificateNew.length];
		int i = 0;
		for (Certificate cert : certificateOld) {
			certificate[i++] = cert;
		}
		for (Certificate cert : certificateNew) {
			certificate[i++] = cert;
		}
		return certificate;
	}

	public String getAlias() throws KeyStoreException {
		Enumeration localEnumeration = this.m_keyStore.aliases();
		String certMessage = null;
		while (localEnumeration.hasMoreElements()) {
			certMessage = (String) localEnumeration.nextElement();
			if (this.m_keyStore.isKeyEntry(certMessage)) {
				break;
			}
		}
		return certMessage;
	}

	public String getAlias(String str) {
		String[] ss = str.split(",");
		for (String s : ss) {
			if (s.trim().toUpperCase().startsWith("CN")) {
				return s.trim().substring(3);
			}
		}
		return null;
	}

	/**
	 * 将证书信息与证书密码 以流的方式存储到jks文件当中
	 * 
	 * @return
	 * @throws ConfigException
	 */
	public boolean saveToFile() throws ConfigException {
		try {
			FileOutputStream localFileOutputStream = new FileOutputStream(
					new File(this.jksFilePath));
			this.m_keyStore.store(localFileOutputStream, this.m_pwd);
			localFileOutputStream.close();
		} catch (Exception e) {
			throw new ConfigException(ConfigException.WRITE_FILE_ERROR,
					ConfigException.WRITE_FILE_ERROR_DES, e);
		}
		return true;
	}

	/**
	 * 将信息存入到KeyStore 以备存储到jks文件
	 */
	public void saveKeyStore() {

	}

	/**
	 * 日期转换为字符串
	 * 
	 * @param d
	 *            日期
	 * @param patten
	 *            转换格式
	 * @return 日期字符串
	 */
	public static String date2Str(java.util.Date d, String patten) {
		SimpleDateFormat f = new SimpleDateFormat(patten);
		f.setLenient(false);
		return f.format(d);
	}

	public static void main(String[] args) {
		String snStr = CodeGenerator.generateRefCode();
		new BigInteger(snStr, 16);
	}

	public void addKeystoreStruct(String algorithm, String dn,
			char[] passwords, int validityDay) throws IDAException {
		X509Certificate certificate = null;
		String signAlg = algorithm;
		String snStr = CodeGenerator.generateRefCode();
		X509V3CertificateGenerator v3CertGen = new X509V3CertificateGenerator();
		X509Principal x509Principal = new X509Principal(dn);
		// DbUtils
		v3CertGen.reset();
		BigInteger bi = new BigInteger(snStr, 16);
		String sn = bi.toString(16);
		v3CertGen.setSerialNumber(bi);
		v3CertGen.setIssuerDN(x509Principal);

		GregorianCalendar localGregorianCalendar = new GregorianCalendar();
		Date localDate1 = new Date();
		localGregorianCalendar.setTime(localDate1);
		// 6 代表的是天 是一个Type
		localGregorianCalendar.add(6, validityDay);
		Date localDate2 = localGregorianCalendar.getTime();

		v3CertGen.setNotBefore(localDate1);
		v3CertGen.setNotAfter(localDate2);
		v3CertGen.setSubjectDN(x509Principal);

		v3CertGen.setPublicKey(keyPair.getPublic());
		v3CertGen.setSignatureAlgorithm(signAlg);

		try {
			certificate = v3CertGen.generate(keyPair.getPrivate());
		} catch (Exception e) {
			KeyPairException kException = new KeyPairException(
					KeyPairException.CREATE_JKS_ERROR,
					KeyPairException.CREATE_JKS_ERROR_DES, e);
			throw kException;
		}
		Certificate[] certtificates = { certificate };
		try {
			m_keyStore.setKeyEntry("s1as", keyPair.getPrivate(), passwords,
					certtificates);
		} catch (KeyStoreException e) {
			KeyPairException kException = new KeyPairException(
					KeyPairException.STORE_JKS_ERROR,
					KeyPairException.STORE_JKS_ERROR_DES, e);
			throw kException;
		}
		if (this.fileType.equals(GenericKey.PKCS12)) {
			DbUtils.updateConfig(sn, dn, this.getAdminIdentity());
		}
	}

	public void addKeystoreStruct(String algorithm, String dn, String issuer,
			char[] passwords, int validityDay) throws IDAException {
		X509Certificate certificate = null;
		String signAlg = algorithm;
		String snStr = CodeGenerator.generateRefCode();
		X509V3CertificateGenerator v3CertGen = new X509V3CertificateGenerator();
		X509Principal x509Principal = new X509Principal(dn);
		X509Principal issuerPrincipal = new X509Principal(issuer);

		// DbUtils
		v3CertGen.reset();
		BigInteger bi = new BigInteger(snStr, 16);
		String sn = bi.toString(16);
		v3CertGen.setSerialNumber(bi);
		v3CertGen.setIssuerDN(issuerPrincipal);

		GregorianCalendar localGregorianCalendar = new GregorianCalendar();
		Date localDate1 = new Date();
		localGregorianCalendar.setTime(localDate1);
		// 6 代表的是天 是一个Type
		localGregorianCalendar.add(6, validityDay);
		Date localDate2 = localGregorianCalendar.getTime();

		v3CertGen.setNotBefore(localDate1);
		v3CertGen.setNotAfter(localDate2);
		v3CertGen.setSubjectDN(x509Principal);

		v3CertGen.setPublicKey(keyPair.getPublic());
		v3CertGen.setSignatureAlgorithm(signAlg);

		try {
			certificate = v3CertGen.generate(keyPair.getPrivate());
		} catch (Exception e) {
			KeyPairException kException = new KeyPairException(
					KeyPairException.CREATE_JKS_ERROR,
					KeyPairException.CREATE_JKS_ERROR_DES, e);
			throw kException;
		}
		Certificate[] certtificates = { certificate };
		try {
			m_keyStore.setKeyEntry("s1as", keyPair.getPrivate(), passwords,
					certtificates);
		} catch (KeyStoreException e) {
			KeyPairException kException = new KeyPairException(
					KeyPairException.STORE_JKS_ERROR,
					KeyPairException.STORE_JKS_ERROR_DES, e);
			throw kException;
		}
		if (this.fileType.equals(GenericKey.PKCS12)) {
			DbUtils.updateConfig(sn, dn, this.getAdminIdentity());
		}
	}

	public boolean isM_isNewKeyStore() {
		return m_isNewKeyStore;
	}

	public void setM_isNewKeyStore(boolean newKeyStore) {
		m_isNewKeyStore = newKeyStore;
	}

	public char[] getM_pwd() {
		return m_pwd;
	}

	public void setM_pwd(char[] m_pwd) {
		this.m_pwd = m_pwd;
	}

	public Vector get_KeyStruct() {
		return m_KeyStruct;
	}

	public void setM_KeyStruct(Vector keyStruct) {
		m_KeyStruct = keyStruct;
	}

	public KeyStore getM_keyStore() {
		return m_keyStore;
	}

	public void setM_keyStore(KeyStore store) {
		m_keyStore = store;
	}

	public Vector getM_TrustCerts() {
		return m_TrustCerts;
	}

	public void setM_TrustCerts(Vector trustCerts) {
		m_TrustCerts = trustCerts;
	}

	public String getJksFilePath() {
		return jksFilePath;
	}

	public void setJksFilePath(String jksFilePath) {
		this.jksFilePath = jksFilePath;
	}

	public int getAdminIdentity() {
		return adminIdentity;
	}

	public void setAdminIdentity(int adminIdentity) {
		this.adminIdentity = adminIdentity;
	}

}
