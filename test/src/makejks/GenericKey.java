package makejks;

import java.io.File;
import java.io.FileOutputStream;
import java.math.BigInteger;
import java.security.KeyPair;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.Date;

import fisher.man.asn1.x509.X509Name;
import fisher.man.x509.X509V3CertificateGenerator;

@SuppressWarnings("all")
public class GenericKey {
	private String DN = null;
	private char[] password = null;
	public KeyStore keyStore;
	public KeyPair keyPair;
	
	/**
	 * 构造函数，并且初始化keystore
	 * @param keyPair密钥对
	 * @param jksFilePath保存jks地址
	 * @param password密码
	 * @throws Exception
	 */
	public GenericKey(KeyPair keyPair, String DN, char[] password)
			throws Exception {
		this.DN = DN;
		this.password = password;
		this.keyPair = keyPair;
		this.keyStore = KeyStore.getInstance("JKS");
		this.keyStore.load(null, null);
	}

	/**
	 * 生成jks
	 * @param algorithm签名算法
	 * @param dn主题
	 * @param passwords密码
	 * @throws Exception
	 */
	public void addKeystoreStruct(String algorithm, String dn, char[] passwords)
			throws Exception {
		Date date = new Date();
		String curr = date2Str(date, "yyyymmddhhMMss");

		X509V3CertificateGenerator v3CertGen = new X509V3CertificateGenerator();
		v3CertGen.reset();
		v3CertGen.setSerialNumber(new BigInteger(curr, 16));
		v3CertGen.setIssuerDN(new X509Name(dn));
		v3CertGen.setNotBefore(date);
		// 默认签发一年
		v3CertGen.setNotAfter(new Date(date.getTime() + 365
				* (1000 * 60 * 60 * 24)));
		v3CertGen.setSubjectDN(new X509Name(dn));
		v3CertGen.setSignatureAlgorithm(algorithm);
		v3CertGen.setPublicKey(keyPair.getPublic());

		X509Certificate certificate = null;
		try {
			certificate = v3CertGen.generate(keyPair.getPrivate(),
					"FishermanJCE");
		} catch (Exception e) {
			throw new Exception();
		}
		Certificate[] certtificates = { certificate };
		try {
			keyStore.setKeyEntry("s1as", keyPair.getPrivate(), passwords,
					certtificates);
		} catch (KeyStoreException e) {
			throw new Exception();
		}
	}

	/**
	 * 保存jks到本地
	 * @param path 保存地址
	 * @return
	 * @throws Exception
	 */
	public boolean saveToFile(String path) throws Exception {
		try {
			FileOutputStream localFileOutputStream = new FileOutputStream(
					new File(path));
			this.keyStore.store(localFileOutputStream, this.password);
			localFileOutputStream.close();
			System.out.println("生成 " + getAlias(this.DN) + ".jks "  +"文件成功");
		} catch (Exception e) {
			throw new Exception();
		}
		return true;
	}
	/**
	 * 根据主题组织JKS文件名
	 * @param str
	 * @return
	 */
	public static String getAlias(String str) {
		String[] ss = str.split(",");
		for (String s : ss) {
			if (s.trim().toUpperCase().startsWith("CN")) {
				return s.trim().substring(3);
			}
		}
		return null;
	}
	/**
	 * 日期转换为字符串
	 * @param d日期
	 * @param patten转换格式
	 * @return 日期字符串
	 */
	public static String date2Str(java.util.Date d, String patten) {
		SimpleDateFormat f = new SimpleDateFormat(patten);
		f.setLenient(false);
		return f.format(d);
	}
}
