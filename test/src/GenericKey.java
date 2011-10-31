
import fisher.man.jce.X509Principal;
import fisher.man.x509.X509V3CertificateGenerator;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.Enumeration;
import java.util.Vector;


import sun.security.x509.X509CertImpl;


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

	/**
	 * 构造方法
	 * 
	 * @param isNewKeyStore是否是新证书
	 * @param jksFilePath路径
	 * @param password密码
	 * @param deviceID加密机ID
	 * @param hardKeyId
	 * @param KeyAlia别名
	 * @throws ConfigException
	 * @throws KeyPairException
	 */
	public GenericKey(boolean isNewKeyStore, String jksFilePath,
			char[] password, String deviceID, int hardKeyId, String KeyAlia)
			throws Exception{
		this.m_isNewKeyStore = isNewKeyStore;
		this.jksFilePath = jksFilePath;
		this.m_pwd = password;
		this.m_deviceID = deviceID;
		this.m_keyID = hardKeyId;
		initKeyStore();
	}

	public GenericKey(boolean isNewKeyStore, String jksFilePath, char[] password)
			throws Exception{
		this.m_isNewKeyStore = isNewKeyStore;
		this.jksFilePath = jksFilePath;
		this.m_pwd = password;
		initKeyStore();
	}

	/**
	 * 初始化KeyStore 用于申请与更新证书
	 * 
	 * @throws ConfigException
	 */
	public void initKeyStore() throws Exception {
		try {
			if (this.m_isNewKeyStore) {
				this.m_keyStore = KeyStore.getInstance("JKS");
				this.m_keyStore.load(null, null);
				return;
			}
			this.m_keyStore = KeyStore.getInstance("JKS");
			FileInputStream localFileInputStream = new FileInputStream(
					this.jksFilePath);
			this.m_keyStore.load(localFileInputStream, this.m_pwd);
			localFileInputStream.close();
		} catch (Exception localException1) {
			localException1.printStackTrace();
		}
		initData();
	}

	/**
	 * 更新证书前将证书中的信息储存到KeyStore
	 * 
	 * @throws ConfigException
	 */
	public void initData() throws Exception {
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
			localException2.printStackTrace();
		}
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
	
	public Certificate[] contactCertArr(Certificate[] certificateOld, Certificate[] certificateNew){
		Certificate[] certificate = new Certificate[certificateOld.length + certificateNew.length];
		int i = 0;
		for(Certificate cert : certificateOld){
			certificate[i++] = cert;
		}
		for(Certificate cert : certificateNew){
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
	public String getAlias(String str){
		String[] ss = str.split(",");
		for(String s : ss){
			if(s.trim().toUpperCase().startsWith("CN")){
				return s.trim().substring(3);
			}
		}
		return null;
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

}
