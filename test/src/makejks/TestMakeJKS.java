package makejks;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.security.KeyPair;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.Enumeration;


public class TestMakeJKS {
	//主题
	private String DN;
	//密码
	private char[] passwords;
	//密钥算法
	private String keyAlag;
	//密钥长度
	private int keySize;
	//签名算法
	private String algorithm;
	//软硬加密
	private String keyType;
	
	//静态变量用于测试 写死
	public static final String PASSWORD = "000000";
	public static final String BOCOCAPASSWORD = "changeit";
	//密钥算法
	public static final String SM2 = "SM2";
	public static final String RSA = "RSA";
	
	//加密机端口位置
	public static final String KEYLOCATION0 = "RandomSM2PubKey0";
	public static final String KEYLOCATION1 = "RandomSM2PubKey1";
	public static final String KEYLOCATION2 = "RandomSM2PubKey2";
	
	private static PrivateKey privateKey;
	/**
	 * 测试main函数 创建两个JKS
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		TestMakeJKS tmj = new TestMakeJKS();
		//保存位置
		String savepath = "C:\\";
		
		KeyStore keyStore = getKeyStore();
		//获得私钥
		privateKey = getPrivateKey(keyStore);
		if(null == privateKey){
			System.out.println("获得根证书私钥失败");
			return;
		}
		//设置证书链根证书的环节
		setRootCer(keyStore);
		
		//num one
		tmj.initData("CN=one,C=CN", SM2, PASSWORD);
		GenericKey cert1 = tmj.initGenericKey(KEYLOCATION1);
		tmj.makeJKS(privateKey, cert1, savepath);
		//num two
		Thread.sleep(1000);
		tmj.initData("CN=two,C=CN", SM2, PASSWORD);
		GenericKey cert2 = tmj.initGenericKey(KEYLOCATION2);
		tmj.makeJKS(privateKey, cert2, savepath);
		System.out.println("操作完毕......");
		System.exit(0);
	}
	
	/**
	 * 获得证书KeyStore
	 * @throws Exception
	 */
	public static KeyStore getKeyStore() throws Exception{
		KeyStore keyStore = KeyStore.getInstance("JKS");
		FileInputStream localFileInputStream = new FileInputStream(
				"");
		keyStore.load(localFileInputStream, BOCOCAPASSWORD.toCharArray());
		localFileInputStream.close();
		return keyStore;
	}
	/**
	 * 获得根证书的私钥
	 * @param keyStore
	 * @return
	 * @throws Exception
	 */
	public static PrivateKey getPrivateKey(KeyStore keyStore) throws Exception{
		Enumeration<String> localEnumeration = keyStore.aliases();
		String certMessage;
		while (localEnumeration.hasMoreElements()) {
			certMessage = (String) localEnumeration.nextElement();
			if (keyStore.isKeyEntry(certMessage)) {
				return (PrivateKey) keyStore.getKey(certMessage,
						BOCOCAPASSWORD.toCharArray());
			}
		}
		return null;
	}
	/**
	 * 设置KeyEntry中根证书的证书链一环节
	 * @param keyStore
	 * @throws KeyStoreException
	 */
	public static void setRootCer(KeyStore keyStore) throws KeyStoreException{
		GenericKey.rootcaGen = (X509Certificate) keyStore.getCertificateChain("s1as")[0];
	}
	/**
	 * 初始化所需数据
	 * @param DN
	 * @param alag
	 * @param password
	 */
	public void initData(String DN, String alag, String password){
		if(RSA.equalsIgnoreCase(alag)){
			keyAlag = RSA;
			algorithm = KeyUtils.RSA_ALGORITHM;
			keySize = 1024;
		}else if(SM2.equalsIgnoreCase(alag)){
			keyAlag = SM2;
			algorithm = KeyUtils.SM2_ALGORITHM;
			keySize = 256;
		}else{
			System.err.println("不支持你配置的" + alag + "密钥算法");
			return;
		}
		this.keyType = KeyUtils.FISHMAN_VALUE;
		this.DN = DN;
		this.passwords = password.toCharArray();
	}
	/**
	 * 初始化制作JKS工具类
	 * @param keyLocation
	 * @return
	 * @throws Exception
	 */
	public GenericKey initGenericKey(String keyLocation,boolean identity) throws Exception{
		KeyPair keyPair = KeyUtils.createKeyPair(keyAlag, keyType, keySize, keyLocation);
		return new GenericKey(keyPair, DN, passwords, identity);
	}
	/**
	 * 初始化制作JKS工具类
	 * @param keyLocation
	 * @return
	 * @throws Exception
	 */
	public GenericKey initGenericKey(String keyLocation) throws Exception{
		return initGenericKey(keyLocation, false);
	}
	/**
	 * 产生并保存JKS到本地
	 * @param gkey
	 * @param savepath
	 * @throws Exception
	 */
	public void makeJKS(GenericKey gkey, String savepath) throws Exception{
		makeJKS(gkey.getPrivateKey(), gkey, savepath);
	}
	/**
	 * 产生并保存JKS到本地
	 * @param gkey
	 * @param savepath
	 * @throws Exception
	 */
	public void makeJKS(PrivateKey privatekey, GenericKey gkey, String savepath) throws Exception{
		gkey.setPath(savepath);
		gkey.addKeystoreStruct(privatekey, algorithm, DN, passwords);
		gkey.saveToFile();
	}
	
}
