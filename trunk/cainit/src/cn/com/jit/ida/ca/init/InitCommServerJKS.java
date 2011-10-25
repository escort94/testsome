package cn.com.jit.ida.ca.init;

import java.security.KeyPair;
import java.sql.SQLException;

import cn.com.jit.ida.IDAException;
import cn.com.jit.ida.ca.key.GenericKey;
import cn.com.jit.ida.ca.key.keyutils.KeyUtils;
import cn.com.jit.ida.ca.key.keyutils.Keytype;
import cn.com.jit.ida.globalconfig.ConfigException;
import cn.com.jit.ida.globalconfig.KeyPairException;
import cn.com.jit.ida.globalconfig.ParseXML;

public class InitCommServerJKS extends InitFather {
	// 服务器的密钥签名算法
	protected String keyCommSigningAlg;
	protected String keyCommSigningStoreAlg;
	public int commKeySize;
	
	public InitCommServerJKS() throws Exception {
		super();
	}

	public InitCommServerJKS(ParseXML init) throws Exception {
		super(init);
	}

	public void initialize() throws ConfigException {
		this.keyCommSigningAlg = init.getString("CommSigningKeyAlg");
		if (keyCommSigningAlg.equalsIgnoreCase("SHA1withRSA")) {
			keyCommSigningStoreAlg = RSA;
		} else if (keyCommSigningAlg.equalsIgnoreCase("SM3WITHSM2")) {
			keyCommSigningStoreAlg = SM2;
		}
		commKeySize = init.getNumber("CommSigningKeySize");
	}

	/**
	 * 产生服务器jks
	 * @throws IDAException
	 * @throws SQLException 
	 */
	public void makeServerJKS() throws IDAException, SQLException {
		String CNinDN = init.getString("ServerAddress");
		String DN = "CN=" + CNinDN + "," + baseDN;
		String path = this.init.getString("CommKeyStore");
		char[] password = this.init.getString("CommKeyStorePWD").toCharArray();
		KeyPair keyPair = KeyUtils.createKeyPair(keyCommSigningStoreAlg, Keytype.SOFT_VALUE, commKeySize);
		GenericKey gKey = new GenericKey(true, path, password, keyPair, GenericKey.JKS);
		gKey.addKeystoreStruct(keyCommSigningAlg, DN, password, commJKSValidityDay);
		gKey.saveToFile();
	}
}
