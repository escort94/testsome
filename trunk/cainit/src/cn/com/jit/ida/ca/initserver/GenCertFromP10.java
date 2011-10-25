package cn.com.jit.ida.ca.initserver;

import cn.com.jit.ida.IDAException;
import cn.com.jit.ida.Operator;
import cn.com.jit.ida.ca.certmanager.service.CertReqDown;
import cn.com.jit.ida.ca.certmanager.service.request.CertReqDownRequest;
import cn.com.jit.ida.ca.certmanager.service.response.CertDownloadResponse;
import cn.com.jit.ida.util.pki.cert.X509Cert;
import java.security.cert.Certificate;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class GenCertFromP10 {
	private String P10;
	private String CTML_Name;
	private P7Bxp p7bxp;
	private String DN;
	private String validity;

	public GenCertFromP10(String paramString1, String paramString2,
			String paramString3, String paramString4)
			throws InitServerException {
		this.P10 = paramString2;
		this.CTML_Name = paramString3;
		this.DN = paramString1;
		this.validity = paramString4;
		genCert();
	}

	public String getP7BString() {
		return this.p7bxp.getP7BStr();
	}

	public X509Cert getJITCert() throws IDAException {
		return this.p7bxp.getUserCert();
	}

	public X509Cert[] getJITCerts() throws IDAException {
		return this.p7bxp.getCertChain();
	}

	public Certificate[] getCerts() throws InitServerException {
		return this.p7bxp.getJavaCert();
	}

	public Certificate[] getCert_NoUser() throws InitServerException {
		return this.p7bxp.getJavaCertChain_NoUser();
	}

	public Certificate getJavaUserCert() throws InitServerException {
		return this.p7bxp.getJavaUserCert();
	}

	private void genCert() throws InitServerException {
		CertReqDownRequest localCertReqDownRequest = new CertReqDownRequest();
		try {
			localCertReqDownRequest.setP10(this.P10);
			localCertReqDownRequest.setCtmlName(this.CTML_Name);
			localCertReqDownRequest.setCertDN(this.DN);
			Operator localOperator = new Operator();
			localOperator.setOperatorDN("系统管理员");
			localOperator.setOperatorSN("系统管理员");
			localOperator.setOperatorCert("系统管理员".getBytes());
			localCertReqDownRequest.setOperator(localOperator);
			SimpleDateFormat localSimpleDateFormat = new SimpleDateFormat(
					"yyyyMMddHHmmssSSS");
			Date localDate = new Date();
			String localObject = localSimpleDateFormat.format(localDate);
			localCertReqDownRequest.setNotBefore((String) localObject);
			localCertReqDownRequest.setValidity(this.validity);
		} catch (IDAException localIDAException) {
			throw new InitServerException(localIDAException.getErrCode(),
					localIDAException.getErrDesc(), localIDAException);
		}
		CertReqDown localCertReqDown = new CertReqDown();
		Object localObject = null;
		localObject = (CertDownloadResponse) localCertReqDown
				.dealRequest(localCertReqDownRequest);
		if (((CertDownloadResponse) localObject).getMsg().equalsIgnoreCase(
				"success"))
			this.p7bxp = new P7Bxp(((CertDownloadResponse) localObject)
					.getP7b());
		else
			throw new InitServerException(((CertDownloadResponse) localObject)
					.getErr(), ((CertDownloadResponse) localObject).getMsg());
	}
}

/*
 * Location: C:\Program Files\JIT\CA50\lib\IDA\ida.jar Qualified Name:
 * cn.com.jit.ida.ca.initserver.GenCertFromP10 JD-Core Version: 0.6.0
 */