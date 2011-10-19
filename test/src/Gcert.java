
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.security.SignatureException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateParsingException;
import java.security.cert.X509Certificate;
import java.util.Calendar;
import java.util.Date;


import fisher.man.asn1.ASN1EncodableVector;
import fisher.man.asn1.DERBitString;
import fisher.man.asn1.DERInteger;
import fisher.man.asn1.DERSequence;
import fisher.man.asn1.DERUTCTime;
import fisher.man.asn1.x509.AlgorithmIdentifier;
import fisher.man.asn1.x509.SubjectPublicKeyInfo;
import fisher.man.asn1.x509.V3TBSCertificateGenerator;
import fisher.man.asn1.x509.X509CertificateStructure;
import fisher.man.asn1.x509.X509Extensions;
import fisher.man.asn1.x509.X509ExtensionsGenerator;
import fisher.man.asn1.x509.X509Name;

import fisher.man.jce.provider.X509CertificateObject;
import fisher.man.x509.X509V3CertificateGenerator;


public class Gcert {

	/**
	 * @param args
	 * @throws CertificateParsingException 
	 * @throws CertificateEncodingException 
	 * @throws IOException 
	 * @throws NoSuchProviderException 
	 * @throws NoSuchAlgorithmException 
	 * @throws SignatureException 
	 * @throws IllegalStateException 
	 * @throws InvalidKeyException 
	 */
	@SuppressWarnings("deprecation")
	public static void main(String[] args) throws CertificateParsingException, CertificateEncodingException, IOException, NoSuchAlgorithmException, NoSuchProviderException, InvalidKeyException, IllegalStateException, SignatureException {
		long currTime = new Date().getTime();   
        String issuerDN = "CN=DemoCA,O=JIT,C=CN";   
        String subjectDN = "CN=kmcsm2,O=JIT,C=CN";   
        int vday = 5000;   
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
		SecureRandom rand = SecureRandom.getInstance("TrueRandom" , "FishermanJCE");
		KeyPairGenerator ecPair = KeyPairGenerator.getInstance("SM2", "FishermanJCE");
		ecPair.initialize(256, rand);
		keypair = ecPair.generateKeyPair();
		
        //有公钥算法和签名算法生成公钥信息摘要     
        v3CertGen.setPublicKey(keypair.getPublic());   
      
        int   keyUsage = 0 | fisher.man.asn1.x509.KeyUsage.digitalSignature | fisher.man.asn1.x509.KeyUsage.nonRepudiation;
        v3CertGen.addExtension(X509Extensions.KeyUsage, false,
				new fisher.man.asn1.x509.KeyUsage(keyUsage));  
           
        X509Certificate cert = v3CertGen.generate(keypair.getPrivate(), "FishermanJCE");  
           
        byte[] buffer  = cert.getEncoded();   
        BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(new File("E:\\source\\console\\keystore\\kmcsm2.cer")));   
        outputStream.write(buffer);   
        outputStream.flush();   
        outputStream.close(); 
        
        System.out.println("sm2 cert is generated!");

	}

}
