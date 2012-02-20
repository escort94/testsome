package makejks;

import java.io.IOException;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.security.SignatureException;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;

import fisher.man.asn1.x509.X509Extensions;
import fisher.man.asn1.x509.X509Name;
import fisher.man.util.encoders.Base64;
import fisher.man.x509.X509V3CertificateGenerator;


public class FMKS {

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		SM2_V3CertificateToFMKS();
//		getKeystore();

	}
	
	public static void getKeystore() throws Exception{
		KeyStore rks= KeyStore.getInstance("FMKS", "FishermanJCE");
	    rks.load(null, "12345678".toCharArray()); //read keystore passwd 
	    Enumeration<String> aliasT = null;
        //枚举指定Keystore里面所存放的对象对应的别名
        aliasT = rks.aliases();
        while(aliasT.hasMoreElements()) 
        {
        	String alis = aliasT.nextElement();
        	System.out.println(alis);
        }
        Key pk = rks.getKey("test1", "12345678".toCharArray());
	}
	public static void setKeystore() throws Exception{
		
	}
	public static void SM2_V3CertificateToFMKS() throws InvalidKeyException, IllegalStateException, NoSuchProviderException, NoSuchAlgorithmException, SignatureException, IOException, KeyStoreException, CertificateException, UnrecoverableKeyException
	{
		
		long currTime = new Date().getTime();   
        String issuerDN = "cn=sm2root,c=cn";   
        String subjectDN = "cn=sm2root,c=cn";   
        int vday = 5000;   
        
        KeyStore wks = null;
        KeyStore rks = null;
        X509Certificate[] certchain = null;
        //证书生成   
        X509V3CertificateGenerator v3CertGen = new X509V3CertificateGenerator();   
        //系列号   
        v3CertGen.setSerialNumber(new BigInteger("10000011",16));   
        //发行人   
        v3CertGen.setIssuerDN(new X509Name(issuerDN));   
        //开始时间和结束时间  
        Calendar cal=Calendar.getInstance();
        cal.set(2012, Calendar.DECEMBER, 31);
        v3CertGen.setNotBefore(new Date(currTime));   
        v3CertGen.setNotAfter(cal.getTime());   
        //主题   
        v3CertGen.setSubjectDN(new X509Name(subjectDN));   
        //签名算法        
        v3CertGen.setSignatureAlgorithm("SM3WITHSM2");   
                
        KeyPair keypair = null;
		SecureRandom rand = SecureRandom.getInstance("RandomSM2PubKey1" , "FishermanJCE");
		KeyPairGenerator ecPair = KeyPairGenerator.getInstance("SM2", "FishermanJCE");
		ecPair.initialize(256, rand);
		keypair = ecPair.generateKeyPair();
		
        //有公钥算法和签名算法生成公钥信息摘要     
        v3CertGen.setPublicKey(keypair.getPublic());  
        
      
        int   keyUsage = 0 | fisher.man.asn1.x509.KeyUsage.digitalSignature | fisher.man.asn1.x509.KeyUsage.nonRepudiation;
        v3CertGen.addExtension(X509Extensions.KeyUsage, false,
				new fisher.man.asn1.x509.KeyUsage(keyUsage));  
        
        //v3CertGen.addExtension(X509Extensions.SubjectKeyIdentifier, 
        //		                false, new DERObjectIdentifier("1.2.156.10197.1.301"));
        
        X509Certificate cert = v3CertGen.generate(keypair.getPrivate(), "FishermanJCE");  
           
        
        
        wks= KeyStore.getInstance("FMKS", "FishermanJCE");
        
        //加载卡内指定的Keystore
        wks.load(null, "12345678".toCharArray()); //read keystore passwd 
        
        //初始化证书链
        certchain = new X509Certificate[1];
        certchain[0] = cert;
        
        //存放私钥及对应的证书链
        wks.setKeyEntry("test1", keypair.getPrivate(), "12345678".toCharArray(), certchain);
        //写入卡内
        wks.store(null, "12345678".toCharArray());
        
        
        rks= KeyStore.getInstance("FMKS", "FishermanJCE");
        rks.load(null, "12345678".toCharArray()); //read keystore passwd 
        
        boolean iskey = false;
        int i = 0;
        String alis = null;
        Enumeration<String> aliasT = null;
        Certificate[] rchain = null;
        
        //枚举指定Keystore里面所存放的对象对应的别名
        aliasT = rks.aliases();
        while(aliasT.hasMoreElements()) 
        {
        	alis = aliasT.nextElement();
        	
        	iskey = rks.isKeyEntry(alis);
        	//获取证书，这里我们只关心我们需要的东西
        	if(iskey)
        	{
        		rchain = rks.getCertificateChain(alis);
        		for(i=0; i<rchain.length; i++)
        		{
        			System.out.print("\nCert:");
        			String b64 = new String(Base64.encode(rchain[i].getEncoded()));
        			System.out.print(b64);
        			System.out.print("\n");
        		}
        	}
        	else if(rks.isCertificateEntry(alis))
        	{
        		//没见过只存放证书的情况
        	}
        	else
        	{
        		System.out.print("Error\n");
        	}
        }
        
        
        //也可以不需要枚举直接根据以前设定的名字去取
        rchain = rks.getCertificateChain("test1");
        Key pk = rks.getKey("test1", "12345678".toCharArray());
        System.out.println(pk);
        //证书链里面第一个就是我们需要在的证书
        X509Certificate obj = (X509Certificate) rchain[0];
        
        String b64 = new String(Base64.encode(obj.getEncoded()));
		System.out.print(b64);
		
	}

}

