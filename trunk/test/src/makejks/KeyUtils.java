package makejks;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.SecureRandom;

public class KeyUtils {
	private static KeyPair keyPair;
	
	public static final String RSA_ALGORITHM = "SHA1withRSA";
	public static final String SM2_ALGORITHM = "SM3WITHSM2";
	
	public static final String RSA_VALUE = "RSA";
	public static final String SM2_VALUE = "SM2";
	
	public static final String SOFT_VALUE = "SOFT";
	public static final String FISHMAN_VALUE = "FISHMAN";
	
	
	/**
	 * 生成密钥对
	 * @param keyalg密钥类型
	 * @param keytype密钥种类
	 * @param keysize密钥长度
	 * @return KeyPair
	 * @throws Exception
	 */
	public static KeyPair createKeyPair(String keyalg, String keytype,
			int keysize, String keyLocation) throws Exception {
		SecureRandom rand = null;
		KeyPairGenerator ecPair = null;
		if (0 == keyalg.compareTo(SM2_VALUE)) {
			if (keysize != 256) {
				throw new Exception();
			}
		}
		if (0 == keyalg.compareTo(RSA_VALUE)) {
			if (!(keysize == 1024 || keysize == 2048)) {
				throw new Exception();
			}
		}
		if (0 == keytype.compareTo(FISHMAN_VALUE)) {
			try {
				rand = SecureRandom.getInstance(keyLocation, "FishermanJCE");
			} catch (Exception e) {
				throw new Exception();
			}
			try {
				ecPair = KeyPairGenerator.getInstance(keyalg, "FishermanJCE");
			} catch (Exception e) {
				throw new Exception();
			}
		}
		if (0 == keytype.compareTo(SOFT_VALUE)) {
			rand = new SecureRandom();
			try {
				ecPair = KeyPairGenerator.getInstance(keyalg, "BC");
			} catch (Exception e) {
				throw new Exception();
			}
		}
		try {
			ecPair.initialize(keysize, rand);
			keyPair = ecPair.generateKeyPair();
//			System.out.println(keyPair.getPrivate());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return keyPair;
	}

}
