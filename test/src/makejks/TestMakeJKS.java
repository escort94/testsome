package makejks;

import java.io.File;
import java.security.KeyPair;


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
	//密钥算法
	public static final String SM2 = "SM2";
	public static final String RSA = "RSA";
	
	//加密机端口位置
	public static final String KEYLOCATION1 = "RandomSM2PubKey1";
	public static final String KEYLOCATION2 = "RandomSM2PubKey2";
	
	/**
	 * 测试main函数 创建两个JKS
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		TestMakeJKS tmj = new TestMakeJKS();
		//保存位置
		String savepath = "C:\\";
		//num one
		tmj.initData("CN=name1,C=CN", SM2, PASSWORD);
		GenericKey gkey1 = tmj.initGenericKey(KEYLOCATION1);
		tmj.makeJKS(gkey1, savepath);
		//num two
		tmj.initData("CN=name2,C=CN", SM2, PASSWORD);
		GenericKey gkey2 = tmj.initGenericKey(KEYLOCATION2);
		tmj.makeJKS(gkey2, savepath);
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
	public GenericKey initGenericKey(String keyLocation) throws Exception{
		KeyPair keyPair = KeyUtils.createKeyPair(keyAlag, keyType, keySize, keyLocation);
		return new GenericKey(keyPair, DN, passwords);
	}
	/**
	 * 产生并保存JKS到本地
	 * @param gkey
	 * @param savepath
	 * @throws Exception
	 */
	public void makeJKS(GenericKey gkey, String savepath) throws Exception{
		gkey.addKeystoreStruct(algorithm, DN, passwords);
		savepath = savepath + File.separator + gkey.getAlias(DN) + ".jks";
		gkey.saveToFile(savepath);
	}
	
}
