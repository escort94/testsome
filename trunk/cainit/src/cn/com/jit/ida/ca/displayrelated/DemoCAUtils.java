package cn.com.jit.ida.ca.displayrelated;

import java.io.FileInputStream;

import sun.security.x509.X509CertImpl;
import cn.com.jit.ida.globalconfig.KeyPairException;

public class DemoCAUtils {

	public static String getIssuer() throws KeyPairException{
		byte[] arrayOfByte = null;
		try {
			FileInputStream localFileInputStream = new FileInputStream("./keystore/DemoCA.cer");
			arrayOfByte = new byte[localFileInputStream.available()];
			localFileInputStream.read(arrayOfByte);
			localFileInputStream.close();
			X509CertImpl x509CertImpl = new X509CertImpl(arrayOfByte);
			return x509CertImpl.getSubjectX500Principal().getName();
		}catch (Exception e) {
			KeyPairException kException = new KeyPairException(
					KeyPairException.GET_DN_ERROR,
					KeyPairException.GET_DN_ERROR_DES, e);
			throw kException;
		}
	}
}
