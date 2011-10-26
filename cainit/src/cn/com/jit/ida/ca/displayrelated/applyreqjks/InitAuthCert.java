package cn.com.jit.ida.ca.displayrelated.applyreqjks;

import java.io.FileInputStream;
import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.Certificate;
import java.security.spec.RSAPublicKeySpec;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.Vector;

import sun.security.x509.X509CertImpl;
import cn.com.jit.ida.IDAException;
import cn.com.jit.ida.ca.key.GenericKey;
import cn.com.jit.ida.ca.key.keyutils.KeyUtils;
import cn.com.jit.ida.ca.key.keyutils.Keyalg;
import cn.com.jit.ida.ca.key.keyutils.Keytype;
import cn.com.jit.ida.globalconfig.ConfigException;
import cn.com.jit.ida.globalconfig.ConfigTool;
import cn.com.jit.ida.globalconfig.KeyPairException;

public class InitAuthCert {
	private String deviceId = null;
	private String signKeyID = null;
	private static final int KEY_SIZE = 256;

	public InitAuthCert() {
	}

	public int generalReq() throws IDAException, SQLException {
		String jksFliePath = ConfigTool.getFilePathFromUser(
				"请输入授权证书密钥储存的文件名\n(默认： ./keystore/raCert.jks，直接回车使用默认文件名)：",
				ConfigTool.FILE_TO_WRITE, "./keystore/raCert.jks");
		if (jksFliePath == null)
			return 0;
		String reqFilePath = ConfigTool.getFilePathFromUser(
				"请输入申请书的文件名\n(默认：./keystore/raCertReq.req，直接回车使用默认文件名)：",
				ConfigTool.FILE_TO_WRITE, "./keystore/raCertReq.req");
		if (reqFilePath == null) {
			return 0;
		}
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
		// 重点操作
		KeyUtils kUtils = new KeyUtils();
		result = kUtils.generalP10(jksFliePath, dn, keyAlag, keySize,
				reqFilePath, Keytype.SOFT_VALUE, passwords.toCharArray());
		return result;
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

//	/**
//	 * 方法用于单独导入根证书
//	 * 
//	 * @return
//	 * @throws IDAException
//	 */
//	public int importGenCer() throws IDAException {
//		int i = 0;
//		String jksFliePath = ConfigTool.getFilePathFromUser(
//				"请输入授权证书密钥储存的文件名\n(默认：./keystore/raCert.jks，直接回车使用默认文件名)：",
//				ConfigTool.FILE_TO_READ, "./keystore/raCert.jks");
//		if (jksFliePath == null)
//			return 0;
//		String password = ConfigTool.getPassword("请输入密钥存储密码：", 6, 16);
//		if (password == null)
//			return 0;
//		GenericKey gKey = new GenericKey(false, jksFliePath, password
//				.toCharArray());
//		boolean bool = false;
//		do {
//			bool = gKey.addDemoCA();
//			gKey.saveToFile();
//		} while (bool == true && ConfigTool.getYesOrNo("是否再次需要导入其他根证书[Y/N]:"));
//		return bool == true ? 1 : 0;
//	}

	public int generalReqForUpdataSM2() throws IDAException {
		String reqPath = null;
		KeyPair kPair = null;
		int keysize = 0;
		String dn = null;
		String keyalag = null;
		String password = null;
		String jksFliePath = ConfigTool.getFilePathFromUser(
				"请输入授权证书密钥储存的文件名\n(默认：./keystore/raCert.jks，直接回车使用默认文件名)：",
				ConfigTool.FILE_TO_READ, "./keystore/raCert.jks");
		if (jksFliePath == null)
			return 0;
		password = ConfigTool.getPassword("请输入密钥存储密码：", 6, 16);
		if (password == null)
			return 0;
		GenericKey gKey = null;
		try {
			gKey = new GenericKey(false, jksFliePath, password.toCharArray(), GenericKey.JKS);
		} catch (ConfigException localConfigException) {
			if (localConfigException.getErrCode().equalsIgnoreCase(
					ConfigException.KEY_STORE_PWD_ERROR))
				throw localConfigException;
		}
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
		reqPath = ConfigTool.getFilePathFromUser(
				"请输入申请书的文件名\n(默认：./keystore/authCertReq.req，直接回车使用默认文件名)：",
				ConfigTool.FILE_TO_WRITE, "./keystore/authCertReq.req");
		if (reqPath == null)
			return 0;
		// 重点操作
		KeyUtils kUtils = new KeyUtils();
		return kUtils.generalP10ForUpdateReq(dn, keyalag, keysize, reqPath,
				Keytype.FISHMAN_VALUE, kPair);
	}

	/**
	 * 导入通讯证书而非根证书，不过早导入通讯证书成功后会提示是否导入根证书
	 */
	public int importCertFromFileSM2(boolean paramBoolean) throws IDAException {
		String jksFilePath = ConfigTool.getFilePathFromUser(
				"请输入授权证书密钥储存的文件名\n(默认：./keystore/raCert.jks，直接回车使用默认文件名)：",
				ConfigTool.FILE_TO_READ, "./keystore/raCert.jks");
		if (jksFilePath == null)
			return 0;
		String p7bPath = ConfigTool.getFilePathFromUser("请输入证书路径(要求cer格式):",
				ConfigTool.FILE_TO_READ);
		if (p7bPath == null)
			return 0;
		String password = ConfigTool.getPassword("请输入密钥存储密码：", 6, 16);
		if (password == null)
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
		return initServerImportP7b(jksFilePath, password, arrayOfByte,
				paramBoolean);
	}

	public int initServerImportP7b(String jksFilePath, String password,
			byte[] arrayOfByte, boolean bool) throws IDAException {
		GenericKey gKey = new GenericKey(false, jksFilePath, password
				.toCharArray(),GenericKey.JKS);
		try {
			gKey.addCertificateEntry(arrayOfByte, password.toCharArray(), bool);
			gKey.saveToFile();
		} catch (KeyPairException e) {
			throw e;
		}
//		boolean hasImport = false;
//		do {
//			hasImport = gKey.addDemoCA();
//			gKey.saveToFile();
//		} while (hasImport == true
//				&& ConfigTool.getYesOrNo("是否再次需要导入其他根证书[Y/N]:"));
//		return hasImport == true ? 1 : 0;
		return 1;
	}

	private String reverseDN(String paramString) {
		Vector localVector = new Vector();
		while (paramString.indexOf(",") != -1) {
			localVector.add(paramString.substring(0, paramString.indexOf(","))
					.trim());
			paramString = paramString.substring(paramString.indexOf(",") + 1,
					paramString.length()).trim();
		}
		localVector.add(paramString);
		String str = "";
		int i = 0;
		int j = localVector.size();
		for (int k = 0; k < j; k++) {
			if (i == 0)
				str = localVector.get(k).toString().trim() + str;
			else
				str = localVector.get(k).toString().trim() + "," + str;
			i++;
		}
		return str;
	}

//	public static void main(String[] paramArrayOfString) {
//		InitAuthCert localInitAuthCert = new InitAuthCert();
//		try {
//			System.out.println(localInitAuthCert.generalReq());
//		} catch (IDAException localIDAException) {
//			localIDAException.printStackTrace();
//		}
//	}
}
