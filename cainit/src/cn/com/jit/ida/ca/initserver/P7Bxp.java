package cn.com.jit.ida.ca.initserver;

import cn.com.jit.ida.util.pki.PKIException;
import cn.com.jit.ida.util.pki.cert.X509Cert;
import cn.com.jit.ida.util.pki.encoders.Base64;
import cn.com.jit.ida.util.pki.pkcs.P7B;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;

public class P7Bxp {
	String p7String;
	X509Cert[] Certs;

	public P7Bxp(String paramString) throws InitServerException {
		this.p7String = paramString;
		P7B localP7B = new P7B();
		try {
			byte[] arrayOfByte = Base64.decode(paramString);
			this.Certs = localP7B.parseP7b(arrayOfByte);
		} catch (PKIException localPKIException) {
			throw new InitServerException(localPKIException.getErrCode(),
					localPKIException.getErrDesc(), localPKIException);
		}
		orderCert();
	}

	public P7Bxp(X509Cert[] paramArrayOfX509Cert) throws InitServerException {
		this.Certs = paramArrayOfX509Cert;
		orderCert();
		P7B localP7B = new P7B();
		try {
			this.p7String = new String(localP7B.generateP7bData_DER(this.Certs));
		} catch (PKIException localPKIException) {
			throw new InitServerException(localPKIException.getErrCode(),
					localPKIException.getErrDesc(), localPKIException);
		}
		orderCert();
	}

	private void orderCert() throws InitServerException {
		X509Cert[] arrayOfX509Cert = new X509Cert[this.Certs.length];
		String str = null;
		for (int i = 0; i < arrayOfX509Cert.length; i++) {
			if (!this.Certs[i].getSubject().equalsIgnoreCase(
					this.Certs[i].getIssuer()))
				continue;
			str = this.Certs[i].getSubject();
			break;
		}
		if (str == null)
			throw new InitServerException("0957", "P7B证书链错误");
		for (int i = 0; i < arrayOfX509Cert.length; i++)
			for (int j = 0; j < arrayOfX509Cert.length; j++) {
				if (i == 0) {
					if ((!this.Certs[j].getIssuer().equalsIgnoreCase(str))
							|| (!this.Certs[j].getSubject().equalsIgnoreCase(
									str)))
						continue;
					arrayOfX509Cert[(arrayOfX509Cert.length - i - 1)] = this.Certs[j];
					str = this.Certs[j].getSubject();
					break;
				}
				if ((!this.Certs[j].getIssuer().equalsIgnoreCase(str))
						|| (this.Certs[j].getSubject().equalsIgnoreCase(str)))
					continue;
				arrayOfX509Cert[(arrayOfX509Cert.length - i - 1)] = this.Certs[j];
				str = this.Certs[j].getSubject();
				break;
			}
		this.Certs = arrayOfX509Cert;
	}

	public X509Cert getRootCert() {
		return this.Certs[(this.Certs.length - 1)];
	}

	public X509Cert getUserCert() {
		return this.Certs[0];
	}

	public Certificate getJavaUserCert() throws InitServerException {
		return JavaCert2JITCert(this.Certs[0]);
	}

	public X509Cert[] getCertChain() {
		return this.Certs;
	}

	public Certificate[] getJavaCert() throws InitServerException {
		Certificate[] arrayOfCertificate = new Certificate[this.Certs.length];
		CertificateFactory localCertificateFactory = null;
		try {
			localCertificateFactory = CertificateFactory.getInstance("X.509");
		} catch (CertificateException localCertificateException) {
			throw new InitServerException("0999", "系统错误",
					localCertificateException);
		}
		for (int i = 0; i < this.Certs.length; i++) {
			ByteArrayInputStream localByteArrayInputStream = null;
			try {
				localByteArrayInputStream = new ByteArrayInputStream(
						this.Certs[i].getEncoded());
			} catch (PKIException localPKIException) {
				throw new InitServerException("0999", "系统错误", localPKIException);
			}
			Object localObject = null;
			try {
				arrayOfCertificate[i] = localCertificateFactory
						.generateCertificate(localByteArrayInputStream);
			} catch (Exception localException) {
				throw new InitServerException("0999", "系统错误", localException);
			}
		}
		return arrayOfCertificate;
	}

	private Certificate JavaCert2JITCert(X509Cert paramX509Cert)
			throws InitServerException {
		CertificateFactory localCertificateFactory = null;
		try {
			localCertificateFactory = CertificateFactory.getInstance("X.509");
		} catch (CertificateException localCertificateException) {
			throw new InitServerException("0999", "系统错误",
					localCertificateException);
		}
		ByteArrayInputStream localByteArrayInputStream = null;
		try {
			localByteArrayInputStream = new ByteArrayInputStream(paramX509Cert
					.getEncoded());
		} catch (PKIException localPKIException) {
			throw new InitServerException(localPKIException.getErrCode(),
					localPKIException.getErrDesc(), localPKIException);
		}
		Certificate localCertificate;
		try {
			localCertificate = localCertificateFactory
					.generateCertificate(localByteArrayInputStream);
		} catch (Exception localException) {
			throw new InitServerException("0999", "系统错误", localException);
		}
		return localCertificate;
	}

	public String getP7BStr() {
		return this.p7String;
	}

	public Certificate[] getJavaCertChain_NoUser() throws InitServerException {
		Certificate[] arrayOfCertificate1 = new Certificate[this.Certs.length - 1];
		Certificate[] arrayOfCertificate2 = getJavaCert();
		for (int i = 1; i < this.Certs.length; i++)
			arrayOfCertificate1[(i - 1)] = arrayOfCertificate2[i];
		return arrayOfCertificate1;
	}

	public boolean saveToFile(String paramString) {
		try {
			FileOutputStream localFileOutputStream = new FileOutputStream(
					new File(paramString));
			localFileOutputStream.write(getP7BStr().getBytes());
			localFileOutputStream.close();
		} catch (Exception localException) {
			return false;
		}
		return true;
	}
}

/*
 * Location: C:\Program Files\JIT\CA50\lib\IDA\ida.jar Qualified Name:
 * cn.com.jit.ida.ca.initserver.P7Bxp JD-Core Version: 0.6.0
 */