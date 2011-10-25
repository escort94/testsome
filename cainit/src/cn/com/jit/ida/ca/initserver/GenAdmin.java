package cn.com.jit.ida.ca.initserver;

import cn.com.jit.ida.IDAException;
import cn.com.jit.ida.ca.config.GlobalConfig;
import cn.com.jit.ida.ca.config.InternalConfig;
import cn.com.jit.ida.privilege.Privilege;
import cn.com.jit.ida.privilege.PrivilegeException;
import cn.com.jit.ida.privilege.TemplateAdmin;
import cn.com.jit.ida.util.pki.cert.X509Cert;
import cn.com.jit.ida.util.pki.cipher.JKeyPair;
import cn.com.jit.ida.util.pki.cipher.Session;
import cn.com.jit.ida.util.pki.pkcs.PKCS12;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.Key;
import java.security.KeyStore;
import java.security.cert.Certificate;

public class GenAdmin {
	String p7b;
	String DN;
	GenP10 genP10;
	GenCertFromP10 genCert;
	Session session;
	String validity;
	public static final String SUPER_ADMIN = "SUPER_ADMIN";
	public static final String AUDIT_ADMIN = "AUDIT_ADMIN";

	public GenAdmin(String dn, Session paramSession,
			String paramString2, String paramString3)
			throws InitServerException {
		this.DN = dn;
		this.session = paramSession;
		this.validity = paramString2;
		GlobalConfig localGlobalConfig;
		try {
			localGlobalConfig = GlobalConfig.getInstance();
		} catch (IDAException localIDAException1) {
			throw new InitServerException(localIDAException1.getErrCode(),
					localIDAException1.getErrDesc(), localIDAException1);
		}
		//产生密钥对
		this.genP10 = new GenP10(dn, "RSA", 1024, paramSession);
		//根据密钥对产生P10请求
		String p10Str = this.genP10.getP10String();
		if (p10Str == null)
			throw new InitServerException("0956", "产生PKCS10申请书错误");
		this.genCert = new GenCertFromP10(dn, p10Str, localGlobalConfig
				.getInternalConfig().getAdminTemplateName(), paramString2);
		//产生P7B证书链
		this.p7b = this.genCert.getP7BString();
		if (this.p7b == null)
			throw new InitServerException("0957", "产生P7B证书链错误");
		X509Cert localX509Cert = null;
		try {
			localX509Cert = this.genCert.getJITCert();
		} catch (IDAException localIDAException2) {
			throw new InitServerException(localIDAException2.getErrCode(),
					localIDAException2.getErrDesc(), localIDAException2);
		}
		//向数据库注入管理员
		setPrivilege(localX509Cert.getStringSerialNumber(), dn,
				paramString3);
	}

	public GenAdmin(String paramString1, String paramString2,
			Session paramSession, String paramString3, String paramString4)
			throws InitServerException {
		this.DN = paramString1;
		this.session = paramSession;
		this.validity = paramString3;
		GlobalConfig localGlobalConfig;
		try {
			localGlobalConfig = GlobalConfig.getInstance();
		} catch (IDAException localIDAException1) {
			throw new InitServerException(localIDAException1.getErrCode(),
					localIDAException1.getErrDesc(), localIDAException1);
		}
		this.genCert = new GenCertFromP10(paramString1, paramString2,
				localGlobalConfig.getInternalConfig().getAdminTemplateName(),
				paramString3);
		this.p7b = this.genCert.getP7BString();
		if (this.p7b == null)
			throw new InitServerException("0957", "产生P7B证书链错误");
		X509Cert localX509Cert = null;
		try {
			localX509Cert = this.genCert.getJITCert();
		} catch (IDAException localIDAException2) {
			throw new InitServerException(localIDAException2.getErrCode(),
					localIDAException2.getErrDesc(), localIDAException2);
		}
		setPrivilege(localX509Cert.getStringSerialNumber(), paramString1,
				paramString4);
	}

	public void SavaCert(String paramString) throws InitServerException {
		try {
			FileOutputStream localFileOutputStream = new FileOutputStream(
					paramString);
			localFileOutputStream.write(this.p7b.getBytes());
			localFileOutputStream.flush();
			localFileOutputStream.close();
		} catch (IOException localIOException) {
			throw new InitServerException("0958", "保存请书错误");
		}
	}

	public void SavaKeyStore(char[] paramArrayOfChar)
			throws InitServerException {
		KeyStore localKeyStore = null;
		try {
			localKeyStore = KeyStore.getInstance("JKS");
			localKeyStore.load(null, null);
		} catch (Exception localException1) {
			throw new InitServerException("0917", "产生KeyStore错误",
					localException1);
		}
		Certificate[] arrayOfCertificate = null;
		try {
			arrayOfCertificate = this.genCert.getCerts();
		} catch (IDAException localIDAException) {
			throw new InitServerException(localIDAException.getErrCode(),
					localIDAException.getErrDesc(), localIDAException);
		}
		try {
			Key localKey = this.genP10.getPrivateKey();
			localKeyStore.setKeyEntry(this.DN, localKey, paramArrayOfChar,
					arrayOfCertificate);
		} catch (Exception localException2) {
			throw new InitServerException("0917", "产生KeyStore错误",
					localException2);
		}
		FileOutputStream localFileOutputStream;
		try {
			localFileOutputStream = new FileOutputStream(new File(
					"./adminnew.jks"));
		} catch (Exception localException3) {
			throw new InitServerException("0917", "产生KeyStore错误",
					localException3);
		}
		try {
			localKeyStore.store(localFileOutputStream, paramArrayOfChar);
		} catch (Exception localException4) {
			throw new InitServerException("0917", "产生KeyStore错误",
					localException4);
		} finally {
			try {
				localFileOutputStream.close();
			} catch (IOException localIOException2) {
			}
		}
	}

	public void SavaPKCS12(String paramString, char[] paramArrayOfChar)
			throws InitServerException {
		PKCS12 localPKCS12 = new PKCS12();
		try {
			X509Cert[] arrayOfX509Cert = this.genCert.getJITCerts();
			localPKCS12.generatePfxFile(this.genP10.getKeyPair()
					.getPrivateKey(), arrayOfX509Cert, paramArrayOfChar,
					paramString);
		} catch (IDAException localIDAException) {
			throw new InitServerException(localIDAException.getErrCode(),
					localIDAException.getErrDesc(), localIDAException);
		}
	}

	private void setPrivilege(String sn, String dn,
			String paramString3) throws InitServerException {
		Privilege localPrivilege = null;
		TemplateAdmin localTemplateAdmin = null;
		try {
			localPrivilege = Privilege.getInstance();
			localTemplateAdmin = TemplateAdmin.getInstance();
			if (paramString3.equalsIgnoreCase("SUPER_ADMIN")) {
				localPrivilege.setSuperAdmin(sn, dn);
				localTemplateAdmin.setSuperAdmin(sn);
			} else if (paramString3.equalsIgnoreCase("AUDIT_ADMIN")) {
				localPrivilege.setAuditAdmin(sn, dn);
			} else {
				throw new InitServerException("8041", "初始化管理员", new Exception(
						"generate administrator error: can't recognize PrivilegeType - "
								+ paramString3));
			}
		} catch (PrivilegeException localPrivilegeException) {
			throw new InitServerException(localPrivilegeException.getErrCode(),
					localPrivilegeException.getErrDesc(),
					localPrivilegeException);
		}
	}
}

/*
 * Location: C:\Program Files\JIT\CA50\lib\IDA\ida.jar Qualified Name:
 * cn.com.jit.ida.ca.initserver.GenAdmin JD-Core Version: 0.6.0
 */