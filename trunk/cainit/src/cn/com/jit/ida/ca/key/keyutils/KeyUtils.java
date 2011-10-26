package cn.com.jit.ida.ca.key.keyutils;


import java.io.FileOutputStream;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.SecureRandom;
import java.sql.SQLException;

import javax.security.auth.x500.X500Principal;

import sun.misc.BASE64Encoder;
import cn.com.jit.ida.IDAException;
import cn.com.jit.ida.ca.key.GenericKey;
import cn.com.jit.ida.globalconfig.ConfigException;
import cn.com.jit.ida.globalconfig.KeyPairException;

/**
 * 1.生成P10请求 并且写入到req文件中 2.负责根据密钥类型生成对应密钥对
 * 
 * @author fangguoqing
 * 
 */
public class KeyUtils {

	private static KeyPair keyPair;

	/**
	 * 生成密钥对
	 * 
	 * @param keyalg密钥类型
	 * @param keytype密钥种类
	 * @param keysize密钥长度
	 * @return KeyPair
	 * @throws Exception
	 */
	public static KeyPair createKeyPair(String keyalg, String keytype,
			int keysize) throws IDAException {
		keyPair = null;
		SecureRandom rand = null;
		KeyPairGenerator ecPair = null;
		if (0 == keyalg.compareTo(Keyalg.SM2_VALUE)) {
			if (keysize != 256) {
				throw new IDAException("0321", "SM2密钥的密钥长度不是256");
			}
		}
		if (0 == keyalg.compareTo(Keyalg.RSA_VALUE)) {
			if (!(keysize == 1024 || keysize == 2048)) {
				throw new IDAException("0322", "RSA密钥的密钥长度不是1024或者2048");
			}
		}
		if (0 == keytype.compareTo(Keytype.FISHMAN_VALUE)) {
			try {
				rand = SecureRandom.getInstance("TrueRandom", "FishermanJCE");
			} catch (Exception e) {
				throw new IDAException("0323",
						KeyPairException.CREATE_KEYPAIR_ERROR_DES, e);
			}
			try {
				ecPair = KeyPairGenerator.getInstance(keyalg, "FishermanJCE");
			} catch (Exception e) {
				throw new IDAException("0324",
						KeyPairException.CREATE_KEYPAIR_ERROR_DES, e);
			}
		}
		if (0 == keytype.compareTo(Keytype.SOFT_VALUE)) {
			rand = new SecureRandom();
			try {
				ecPair = KeyPairGenerator.getInstance(keyalg, "BC");
			} catch (Exception e) {
				throw new IDAException("0325",
						KeyPairException.CREATE_KEYPAIR_ERROR_DES, e);
			}
		}
		try {
			ecPair.initialize(keysize, rand);
			keyPair = ecPair.genKeyPair();
		} catch (Exception e) {
			throw new IDAException("0326",
					KeyPairException.CREATE_KEYPAIR_ERROR_DES, e);
		}
		return keyPair;
	}

	/**
	 * 处理流程：先获得P10字符串 然后生成jks 在写req文件
	 * 
	 * @param dn
	 *            主题
	 * @param keyalg
	 *            算法
	 * @param guding
	 *            固定字符串
	 * @param keysize
	 *            密钥长度
	 * @param reqFilePath
	 *            req文件路径
	 * @param keytype
	 *            生成密钥类型（软硬之分）
	 * @return 是否成功执行完毕
	 * @throws IDAException
	 * @throws SQLException 
	 */
	public int generalP10(String jksFliePath, String dn, String keyalg,
			int keysize, String reqFilePath, String keytype, char[] passwords)
			throws IDAException, SQLException {
		String algorithm = keyalg.equals(Keyalg.RSA_VALUE) ? Keyalgorithm.RSA_ALGORITHM
				: Keyalgorithm.SM2_ALGORITHM;
		KeyPair keyPair = getKeyPair(keyalg, keytype, keysize);
		String pa0Str = null;
		if (keytype.equals(Keytype.FISHMAN_VALUE)) {
			pa0Str = getP10ReqestFishMan(algorithm, dn, keyPair);
		} else if (keytype.equals(Keytype.SOFT_VALUE)) {
			pa0Str = getP10ReqestBC(algorithm, dn, keyPair);
		}
		GenericKey gKey = new GenericKey(true, jksFliePath, passwords, keyPair, GenericKey.JKS);
		gKey.addKeystoreStruct(algorithm, dn, passwords, 365);
		gKey.saveToFile();
		return writeReqFile(pa0Str, reqFilePath);
	}

	public int generalP10ForUpdateReq(String dn, String keyalg, int keysize,
			String reqFilePath, String keytype, KeyPair keyPair)
			throws IDAException {
		String algorithm = keyalg.equals(Keyalg.RSA_VALUE) ? Keyalgorithm.RSA_ALGORITHM
				: Keyalgorithm.SM2_ALGORITHM;
		String pa0Str = null;
		if (keytype.equals(Keytype.FISHMAN_VALUE)) {
			pa0Str = getP10ReqestFishMan(algorithm, dn, keyPair);
		} else if (keytype.equals(Keytype.SOFT_VALUE)) {
			pa0Str = getP10ReqestBC(algorithm, dn, keyPair);
		}
		return writeReqFile(pa0Str, reqFilePath);
	}

	/**
	 * 
	 * @param p10Str
	 *            p10请求的字符串
	 * @param reqFilePath
	 *            req文件路径
	 * @return 返回是否正常执行
	 * @throws ConfigException
	 */
	private int writeReqFile(String p10Str, String reqFilePath)
			throws ConfigException {
		try {
			FileOutputStream localFileOutputStream = new FileOutputStream(
					reqFilePath);
			localFileOutputStream.write(p10Str.getBytes());
			localFileOutputStream.flush();
			localFileOutputStream.close();
		} catch (Exception e) {
			ConfigException localConfigException = new ConfigException(
					ConfigException.WRITE_FILE_ERROR,
					"写文件错误", e);
			localConfigException.appendMsg(reqFilePath);
			throw localConfigException;
		}
		return 1;
	}

	/**
	 * @param keyalg加密算法类型
	 * @param keytype生成方式（软硬之分）
	 * @param keysize密钥长度
	 * @return 返回密钥对
	 * @throws KeyPairException
	 */
	public KeyPair getKeyPair(String keyalg, String keytype, int keysize)
			throws KeyPairException {
		KeyPair keyPair = null;
		try {
			keyPair = KeyUtils.createKeyPair(keyalg, keytype, keysize);
		} catch (IDAException e) {
			throw new KeyPairException(e.getErrCode(),
					KeyPairException.CREATE_KEYPAIR_ERROR_DES, e);
		}
		return keyPair;
	}

	/**
	 * @param keyPair密钥对
	 * @return 返回p10请求的字符串
	 * @throws KeyPairException
	 */
	private String getP10ReqestFishMan(String algorithm, String dn,
			KeyPair keyPair) throws KeyPairException {
		String p10RequestString = null;
		fisher.man.jce.PKCS10CertificationRequest p10Request;
		try {
			p10Request = new fisher.man.jce.PKCS10CertificationRequest(
					algorithm, new X500Principal(dn), keyPair.getPublic(),
					null, keyPair.getPrivate());
			byte[] p10buffer = p10Request.getDEREncoded();
			p10RequestString = new BASE64Encoder().encode(p10buffer);
		} catch (Exception e) {
			throw new KeyPairException(KeyPairException.CREATE_P10_ERROR,
					KeyPairException.CREATE_P10_ERROR_DES, e);
		}
		return p10RequestString;
	}
	/**
	 * @param keyPair密钥对
	 * @return 返回p10请求的字符串
	 * @throws KeyPairException
	 */
	private String getP10ReqestBC(String algorithm, String dn, KeyPair keyPair)
			throws KeyPairException {
		String p10RequestString = null;
		org.bouncycastle.jce.PKCS10CertificationRequest p10Request;
		try {
			p10Request = new org.bouncycastle.jce.PKCS10CertificationRequest(
					algorithm, new X500Principal(dn), keyPair.getPublic(),
					null, keyPair.getPrivate());
			byte[] p10buffer = p10Request.getDEREncoded();
			p10RequestString = new BASE64Encoder().encode(p10buffer);
		} catch (Exception e) {
			throw new KeyPairException(KeyPairException.CREATE_P10_ERROR,
					KeyPairException.CREATE_P10_ERROR_DES, e);
		}
		return p10RequestString;
	}

	public static KeyPair getKeyPair() {
		return keyPair;
	}
}
