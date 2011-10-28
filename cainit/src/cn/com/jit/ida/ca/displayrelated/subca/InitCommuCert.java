package cn.com.jit.ida.ca.displayrelated.subca;

import java.io.FileInputStream;
import java.math.BigInteger;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.Certificate;
import java.security.spec.RSAPublicKeySpec;
import java.util.Enumeration;

import sun.security.x509.X509CertImpl;
import cn.com.jit.ida.IDAException;
import cn.com.jit.ida.ca.key.GenericKey;
import cn.com.jit.ida.ca.key.keyutils.KeyUtils;
import cn.com.jit.ida.ca.key.keyutils.Keyalg;
import cn.com.jit.ida.ca.key.keyutils.Keytype;
import cn.com.jit.ida.globalconfig.ConfigTool;
import cn.com.jit.ida.globalconfig.KeyPairException;
import cn.com.jit.ida.util.pki.PKIException;

public class InitCommuCert {
	public static String identity;
	private static final int KEY_SIZE = 256;
	public static final String SUB_CA_JKS = "SUB_CA_JKS" ;
	public static final String COMM_JKS = "COMM_JKS" ;
	
	public InitCommuCert(String identity) {
		try {
			this.identity = identity;
		} catch (Exception localException1) {
			localException1.printStackTrace();
		}
	}

	public int generalReq() throws IDAException {
		String jksFliePath = ConfigTool.getFilePathFromUser(
				"请输入服务器证书密钥储存的文件名\n(默认： ./keystore/subCAKeystore.jks，直接回车使用默认文件名)：",
				ConfigTool.FILE_TO_WRITE, "./keystore/subCAKeystore.jks");
		if (jksFliePath == null)
			return 0;
		String reqFilePath = ConfigTool.getFilePathFromUser(
				"请输入申请书的文件名\n(默认：./keystore/subCAKeystore.req，直接回车使用默认文件名)：",
				ConfigTool.FILE_TO_WRITE, "./keystore/subCAKeystore.req");
		if (reqFilePath == null)
			return 0;
		int j = 0;
		j = ConfigTool.displayMenu(
				"**                     算法                   **", new String[] {
						"RSA(默认)", "SM2" }, 1);
		String keyAlag;
		int keySize = 0;
		if (j == 1) {
			keyAlag = "RSA";
			keySize = ConfigTool.displayMenu(
					"**                   密钥长度                 **",
					new String[] { "1024(默认)", "2048" }, 1);
			if (keySize == 0) {
				return 0;
			}
			keySize = (int) Math.pow(2.0D, keySize + 9);
		} else if (j == 2) {
			keyAlag = "SM2";
			keySize = ConfigTool.displayMenu(
					"**                   密钥长度                 **",
					new String[] { "256(默认)" }, 1);
			if (keySize == 0) {
				return 0;
			}
			keySize = (int) Math.pow(2.0D, keySize + 7);
		} else {
			return 0;
		}
		if (keySize < 256)
			return 0;
		String dn = ConfigTool.getDN("请输入通信证书DN:");
		if (dn == null)
			return 0;
		String passwords = ConfigTool.getNewPassword("请输入密钥存储密码", 6, 16);
		if (passwords == null) {
			return 0;
		}
		int result = -1;
		// 重点操作  软加密库  加密机 两种生成方式 控制台提示操作 稍后修改
		KeyUtils kUtils = new KeyUtils();
		result = kUtils.generalP10(jksFliePath, dn, keyAlag, keySize,
				reqFilePath, Keytype.FISHMAN_VALUE, passwords.toCharArray());
		return result;
	}
	/**
	 * 反转主题，以逗号为间隔看做一个元素反转
	 * @param dn
	 * @return
	 */
	public String reverseDN(String dn){
		String[] dns = dn.split(",");
		int length = dns.length;
		String[] returndns = new String[length];
		StringBuffer sb = new StringBuffer();
		for(String s : dns){
			returndns[--length] = s.trim();
		}
		for(String s : returndns){
			sb.append(s + ",");
		}
		return sb.substring(0, sb.length() - 1);
	}
	/**
	 * 用于更新证书req请求文件，流程为读取jks文件内证书内容，收集证书所有信息
	 * 生成P10请求
	 * @return
	 * @throws IDAException
	 */
	public int generalReqForUpdataSM2() throws IDAException {
		String reqPath = null;
		KeyPair kPair = null;
		int keysize = 0;
		String dn = null;
		String keyalag = null;
		String password = null;
		CommuCert comucert = CommuCert.getInstance();
		password = comucert.getCommKeyStorePWD();
		GenericKey gKey = comucert.getGKey();
		KeyStore keyStore = gKey.getM_keyStore();
		String alias = getAlias(keyStore);
		try {
			Certificate cert = keyStore.getCertificate(alias);
			X509CertImpl x509CertImpl = (X509CertImpl) cert;
			dn = x509CertImpl.getSubjectDN().getName();
			dn = reverseDN(dn);
			PublicKey publicKey = x509CertImpl.getPublicKey();
			PrivateKey privateKey = (PrivateKey) keyStore.getKey(alias,
					password.toCharArray());
			kPair = new KeyPair(publicKey, privateKey);
			keyalag = privateKey.getAlgorithm(); // 密码具体的算法 貌似是这样的
			if (keyalag.equals(Keyalg.RSA_VALUE)) {
				KeyFactory keyFact = KeyFactory.getInstance(keyalag);
				RSAPublicKeySpec keySpec = (RSAPublicKeySpec) keyFact
						.getKeySpec(cert.getPublicKey(), RSAPublicKeySpec.class);
				BigInteger modulus = keySpec.getModulus();
				keysize = modulus.toString(2).length();
			} else if (keyalag.equals(Keyalg.SM2_VALUE)) {
				keysize = this.KEY_SIZE;
			}
		} catch (Exception e) {
			KeyPairException kpExeption = new KeyPairException(
					KeyPairException.EXPORT_REQ_ERROR,
					KeyPairException.EXPORT_REQ_ERROR_DES, e);
			throw kpExeption;
		}
		reqPath = ConfigTool.getFilePathFromUser("请输入申请书的路径与文件名：",
				ConfigTool.FILE_TO_WRITE);
		if (reqPath == null) {
			return 0;
		}
		// 重点操作
		KeyUtils kUtils = new KeyUtils();
		return kUtils.generalP10ForUpdateReq(dn, keyalag, keysize, reqPath,
				Keytype.SOFT_VALUE, kPair);
	}

	/**
	 * 从keystore中获得证书的别名
	 * 
	 * @param m_keyStore
	 * @return
	 */
	public String getAlias(KeyStore m_keyStore) {
		String str = "";
		try {
			Enumeration localEnumeration = m_keyStore.aliases();
			while (localEnumeration.hasMoreElements()) {
				String alias = (String) localEnumeration.nextElement();
				if (m_keyStore.isKeyEntry(alias)) {
					str = alias;
					return str;
				}
			}
		} catch (KeyStoreException e) {
			e.printStackTrace();
		}
		return str;
	}
	/**
	 * 在初始化系统时用于导入p7b格式证书中内容到jks文件当中
	 * 
	 * @return
	 * @throws IDAException
	 */
	public int initServerImportP7b(byte[] arrayOfByte) throws IDAException {
		return initServerImportP7b(arrayOfByte, true);
	}
	public int initServerImportP7b(byte[] arrayOfByte, boolean bool) throws IDAException {
		CommuCert commuCert = CommuCert.getInstance();
		GenericKey gKey = new GenericKey(false, commuCert.getCommKeyStore(),
				commuCert.getCommKeyStorePWD().toCharArray(), GenericKey.JKS);
		try {
			gKey.addCertificateEntry(arrayOfByte, commuCert
					.getCommKeyStorePWD().toCharArray(), bool);
			gKey.saveToFile();
		} catch (KeyPairException e) {
			throw e;
		}
		return 1;
	}
//	public int initServerImportP7b(byte[] arrayOfByte) throws IDAException {
//		CommuCert commuCert = CommuCert.getInstance();
//		GenericKey gKey = new GenericKey(false, commuCert.getCommKeyStore(),
//				commuCert.getCommKeyStorePWD().toCharArray(), null, 0, null);
//		try {
//			gKey.addCertificateEntry(arrayOfByte, commuCert
//					.getCommKeyStorePWD().toCharArray());
//			gKey.saveToFile();
//		} catch (KeyPairException e) {
//			throw e;
//		}
//		return 1;
//	}

	/**
	 * 方法用于单独导入根证书
	 * 
	 * @return
	 * @throws IDAException
	 */
	public int importGenCer() throws IDAException {
		int i = 0;
		CommuCert commuCert = CommuCert.getInstance();
		GenericKey gKey = new GenericKey(false, commuCert.getCommKeyStore(),
				commuCert.getCommKeyStorePWD().toCharArray(), GenericKey.JKS);
		boolean bool = false;
		Key privateKey = null;
		try {
			privateKey = gKey.m_keyStore.getKey(gKey.getAlias(), commuCert.getCommKeyStorePWD().toCharArray());
		} catch (Exception e) {
			KeyPairException kException = new KeyPairException(
					KeyPairException.GET_PRIVATE_KEY_ERROR,
					KeyPairException.GET_PRIVATE_KEY_ERROR_DES);
			throw kException;
		}
		if(null == privateKey){
			KeyPairException kException = new KeyPairException(
					KeyPairException.GET_PRIVATE_KEY_ERROR,
					KeyPairException.GET_PRIVATE_KEY_ERROR_DES);
			throw kException;
		}
		do {
			bool = gKey.addDemoCA(privateKey, commuCert.getCommKeyStorePWD().toCharArray());
			gKey.saveToFile();
		} while (bool == true && ConfigTool.getYesOrNo("是否再次需要导入其他根证书[Y/N]:"));
		return bool == true ? 1 : 0;
	}

	/**
	 * 导入通讯证书而非根证书，不过早导入通讯证书成功后会提示是否导入根证书
	 */
	public int importCertFromFile(boolean paramBoolean) throws IDAException,
			PKIException {
		String p7bPath = ConfigTool.getFilePathFromUser("请输入证书路径(要求cer格式):",
				ConfigTool.FILE_TO_READ);
		if (p7bPath == null)
			return 0;
		byte[] arrayOfByte = null;
		try {
			FileInputStream localFileInputStream = new FileInputStream(p7bPath);
			arrayOfByte = new byte[localFileInputStream.available()];
			localFileInputStream.read(arrayOfByte);
			localFileInputStream.close();
		} catch (Exception localException) {
			throw new IDAException("2602", "通信证书证书链错误"
					+ localException.getMessage(), localException);
		}
		return initServerImportP7b(arrayOfByte, paramBoolean);
	}

	public static String getIdentity() {
		return identity;
	}

	public static void setIdentity(String identity) {
		InitCommuCert.identity = identity;
	}


}