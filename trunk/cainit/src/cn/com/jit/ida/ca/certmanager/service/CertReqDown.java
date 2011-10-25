package cn.com.jit.ida.ca.certmanager.service;

import cn.com.jit.ida.IDAException;
import cn.com.jit.ida.Operator;
import cn.com.jit.ida.Request;
import cn.com.jit.ida.Response;
import cn.com.jit.ida.ca.certmanager.CertException;
import cn.com.jit.ida.ca.certmanager.CertService;
import cn.com.jit.ida.ca.certmanager.reqinfo.CertExtensions;
import cn.com.jit.ida.ca.certmanager.reqinfo.CertInfo;
import cn.com.jit.ida.ca.certmanager.reqinfo.Extension;
import cn.com.jit.ida.ca.certmanager.service.kmc.KMCCertInfo;
import cn.com.jit.ida.ca.certmanager.service.kmc.KMCConnector;
import cn.com.jit.ida.ca.certmanager.service.kmc.KeyApplyResponse;
import cn.com.jit.ida.ca.certmanager.service.kmc.KeyApplyResponse.EnvelopedData;
import cn.com.jit.ida.ca.certmanager.service.operation.CertQueryOpt;
import cn.com.jit.ida.ca.certmanager.service.operation.CertReqDownOpt;
import cn.com.jit.ida.ca.certmanager.service.operation.CodeGenerator;
import cn.com.jit.ida.ca.certmanager.service.request.CertReqDownRequest;
import cn.com.jit.ida.ca.certmanager.service.request.ReqCheck;
import cn.com.jit.ida.ca.certmanager.service.response.CertReqDownResponse;
import cn.com.jit.ida.ca.config.CAConfig;
import cn.com.jit.ida.ca.ctml.CTML;
import cn.com.jit.ida.ca.ctml.CTMLManager;
import cn.com.jit.ida.ca.ctml.util.CTMLConstant;
import cn.com.jit.ida.ca.ctml.x509v3.X509V3CTML;
import cn.com.jit.ida.ca.ctml.x509v3.extension.StandardExtension;
import cn.com.jit.ida.globalconfig.ConfigTool;
import cn.com.jit.ida.log.DebugLogger;
import cn.com.jit.ida.log.LogManager;
import cn.com.jit.ida.log.Operation;
import cn.com.jit.ida.log.OptLogger;
import cn.com.jit.ida.privilege.TemplateAdmin;
import cn.com.jit.ida.util.pki.PKIException;
import cn.com.jit.ida.util.pki.Parser;
import cn.com.jit.ida.util.pki.asn1.ASN1Set;
import cn.com.jit.ida.util.pki.cert.X509Cert;
import cn.com.jit.ida.util.pki.certpair.CertPairGenerator;
import cn.com.jit.ida.util.pki.cipher.JCrypto;
import cn.com.jit.ida.util.pki.cipher.JKey;
import cn.com.jit.ida.util.pki.cipher.JKeyPair;
import cn.com.jit.ida.util.pki.cipher.Mechanism;
import cn.com.jit.ida.util.pki.cipher.Session;
import cn.com.jit.ida.util.pki.encoders.Base64;
import cn.com.jit.ida.util.pki.encoders.Hex;
import cn.com.jit.ida.util.pki.pkcs.P7B;
import cn.com.jit.ida.util.pki.pkcs.PKCS10;
import cn.com.jit.ida.util.pki.pkcs.PKCS12;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Properties;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CertReqDown extends CertService {
	private CertInfo certInfo = new CertInfo();
	private String certDN = null;
	private String ctmlName = null;
	private String notBefore = null;
	private long lnotBefore = 0L;
	private String validity = null;
	private int ivalidity = 0;
	private String notAfter = null;
	private long lnotAfter = 0L;
	private String applicant = null;
	private String email = null;
	private String remark = null;
	private Properties proExtensions = null;
	private String p10 = null;
	private ASN1Set attributes = null;
	private JKeyPair keyPair = null;
	private boolean returnP7B = false;
	private String tempPubKey = null;
	private String certSN = null;
	private String authCode = null;
	private String encryptedSessionKey = null;
	private String sessionKeyAlg = null;
	private String sessionKeyPad = null;
	private String encryptedPrivateKey = null;
	private boolean useKMC = false;
	private DebugLogger debugLogger = LogManager
			.getDebugLogger("cn.com.jit.ida.ca.certmanager.service.CertReqDown");
	private OptLogger optLogger = LogManager.getOPtLogger();
	private Operation operation = null;
	private CertReqDownResponse response = new CertReqDownResponse();
	private Request request = null;
	private Hashtable standardExtensions = null;

	public Response dealRequest(Request paramRequest) {
		try {
			if (LogManager.isDebug2())
				try {
					this.debugLogger.appendMsg_L2("requestXML="
							+ new String(paramRequest.getData()));
				} catch (Exception localException1) {
					throw new CertException("0704", "其他错误 请求信息格式不合法");
				}
			this.request = paramRequest;
			this.operation = new Operation();
			this.operator = paramRequest.getOperator();
			if (this.operator == null) {
				this.debugLogger.appendMsg_L1("operator=null");
				throw new CertException("0701", "其他错误 操作员为空");
			}
			if (this.operator.getOperatorDN() == null) {
				this.debugLogger.appendMsg_L1("operatorDN="
						+ this.operator.getOperatorDN());
				throw new CertException("0702", "其他错误 操作员DN为空");
			}
			this.operation.setOptType(paramRequest.getOperation());
			this.operation.setOperatorSN(this.operator.getOperatorSN());
			this.operation.setOperatorDN(this.operator.getOperatorDN());
			this.applicant = this.operator.getOperatorDN();
			this.debugLogger.appendMsg_L2("operation="
					+ paramRequest.getOperation());
			this.debugLogger.appendMsg_L2("operatorSN="
					+ this.operator.getOperatorSN());
			this.debugLogger.appendMsg_L2("operatorDN="
					+ this.operator.getOperatorDN());
			int i = 0;
			if (i == 0) {
				licenceCheck();
				validateCheck();
				policyCheck();
				certStatusCheck();
				CTMLCheck();
			} else {
				directGetData();
			}
			this.certInfo.setCertSN(this.certSN);
			this.certInfo.setAuthCode(this.authCode);
			this.certInfo.setSubject(this.certDN);
			this.certInfo.setCtmlName(this.ctmlName);
			this.certInfo.setNotBefore(this.lnotBefore);
			this.certInfo.setValidity(this.ivalidity);
			this.certInfo.setNotAfter(this.lnotAfter);
			this.certInfo.setCertStatus("Use");
			this.certInfo.setApplicant(this.applicant);
			this.certInfo.setEmail(this.email);
			this.certInfo.setRemark(this.remark);
			this.certInfo.setExtensions(this.proExtensions);
			X509Cert localX509Cert = null;
			if (this.attributes == null)
				localX509Cert = CertReqDownOpt.certReqDown(this.certInfo);
			else
				localX509Cert = CertReqDownOpt.certReqDown(this.certInfo,
						this.attributes);
			Object localObject2;
			int k;
			Object localObject5;
			if (this.ctmlName.equals("交叉证书模板"))
				try {
					String str1 = "./crosscert";
					localObject2 = localX509Cert.getSubject();
					int j = ((String) localObject2).indexOf("=");
					k = ((String) localObject2).indexOf(",");
					localObject5 = "CA Name";
					if (k != -1)
						localObject5 = ((String) localObject2).substring(j + 1,
								k);
					else
						localObject5 = ((String) localObject2).substring(j + 1);
					File localFile1 = new File(str1);
					if (!localFile1.exists())
						localFile1.mkdir();
					String str3 = str1 + File.separator + (String) localObject5;
					File localFile2 = new File(str3);
					if (!localFile2.exists())
						localFile2.mkdir();
					FileOutputStream localFileOutputStream = new FileOutputStream(
							str3 + File.separator + "reverse.cer");
					localFileOutputStream.write(localX509Cert.getEncoded());
					localFileOutputStream.flush();
					localFileOutputStream.close();
					CertPairGenerator localCertPairGenerator = new CertPairGenerator(
							null, localX509Cert);
					byte[] arrayOfByte = localCertPairGenerator
							.generateCertPair();
					localFileOutputStream = new FileOutputStream(str3
							+ File.separator + "crosscert.dat");
					localFileOutputStream.write(arrayOfByte);
					localFileOutputStream.flush();
					localFileOutputStream.close();
				} catch (Exception localException3) {
					throw new PKIException("0609", "执行业务操作 保存交叉证书失败",
							localException3);
				}
			this.operation.setObjCertSN(this.certSN);
			this.response.setCertSN(this.certSN);
			X509Cert[] localObject3;
			Object localObject4;
			if (this.returnP7B) {
				CAConfig localObject1 = null;
				X509Cert[] localObject21 = null;
				localObject1 = CAConfig.getInstance();
				localObject21 = ((CAConfig) localObject1).getRootCerts();
				localObject3 = new X509Cert[localObject21.length + 1];
				for (k = 0; k < localObject21.length; k++)
					localObject3[k] = localObject21[k];
				localObject3[(localObject3.length - 1)] = localX509Cert;
				localObject4 = new P7B();
				localObject5 = null;
				try {
					localObject5 = ((P7B) localObject4)
							.generateP7bData_B64(localObject3);
				} catch (PKIException localPKIException) {
					throw new CertException(localPKIException.getErrCode(),
							localPKIException.getErrDesc(), localPKIException
									.getHistory());
				}
				String str2 = new String((byte[]) localObject5);
				this.debugLogger.appendMsg_L2("p7b=" + str2);
				this.response.setP7b(str2);
				if (this.useKMC) {
					this.response
							.setEncryptedSessionKey(this.encryptedSessionKey);
					this.response.setSessionKeyAlg(this.sessionKeyAlg);
					this.response.setSessionKeyPad(this.sessionKeyPad);
					this.response
							.setEncryptedPrivateKey(this.encryptedPrivateKey);
				}
			} else {
				JKey localObject1 = this.keyPair.getPrivateKey();
				localObject2 = new PKCS12();
				byte[] localObject31 = ((PKCS12) localObject2)
						.generatePfxData((JKey) localObject1, localX509Cert,
								"JIT".toCharArray());
				localObject4 = new String(Base64.encode(localObject31));
				this.debugLogger.appendMsg_L2("pfx=" + (String) localObject4);
				this.response.setPfx((String) localObject4);
			}
			this.operation.setResult(1);
			this.optLogger.info(this.operation);
			this.response.setErr("0");
			this.response.setErr("0");
			this.response.setMsg("success");
			return response;
		} catch (Exception localException2) {
			Object localObject1;
			this.debugLogger.appendMsg_L1("Exception:"
					+ localException2.toString());
			if (this.operation != null) {
				this.operation.setResult(0);
				try {
					this.optLogger.info(this.operation);
				} catch (IDAException localIDAException1) {
					this.response.setErr("8003"
							+ localIDAException1.getErrCode());
					this.response.setMsg("证书申请并下载服务 "
							+ localIDAException1.getErrDesc());
					this.response.appendDetail(localIDAException1.getHistory());
					this.debugLogger.doLog();
					localObject1 = this.response;
					return response;
				}
			}
			if ((localException2 instanceof IDAException)) {
				IDAException localIDAException2 = (IDAException) localException2;
				this.response.setErr("8003" + localIDAException2.getErrCode());
				this.response.setMsg("证书申请并下载服务 "
						+ localIDAException2.getErrDesc());
				this.response.appendDetail(localIDAException2.getHistory());
			} else {
				this.response.setErr("80030706");
				this.response.setMsg("证书申请并下载服务 其他错误 系统错误");
				this.response.appendDetail(localException2);
			}
		} finally {
			try {
				if (LogManager.isDebug())
					this.debugLogger.appendMsg_L2("responseXML="
							+ new String(this.response.getData()));
			} catch (Exception localException4) {
				throw new RuntimeException(localException4);
			}
			this.debugLogger.doLog();
		}
		return response;
	}

	protected void certStatusCheck() throws CertException {
		Object localObject = null;
		int i = 0;
		try {
			i = CertQueryOpt.checkCertReq(this.certDN, this.ctmlName);
		} catch (IDAException localIDAException) {
			throw new CertException(localIDAException.getErrCode(),
					localIDAException.getErrDesc(), localIDAException
							.getHistory());
		}
		if (i > 0)
			throw new CertException("0401", "证书状态检查 证书状态非法", new Exception(
					"此证书已存在并处于使用状态，请重新确认申请信息"));
		this.debugLogger.appendMsg_L2("certStatus=null");
	}

	protected void validateCheck() throws CertException {
		CertReqDownRequest localCertReqDownRequest = new CertReqDownRequest(
				this.request);
		this.certDN = localCertReqDownRequest.getCertDN();
		this.certDN = ConfigTool.formatDN(this.certDN);
		this.ctmlName = localCertReqDownRequest.getCtmlName();
		this.notBefore = localCertReqDownRequest.getNotBefore();
		this.validity = localCertReqDownRequest.getValidity();
		this.email = localCertReqDownRequest.getEmail();
		this.remark = localCertReqDownRequest.getRemark();
		this.proExtensions = localCertReqDownRequest.getExtensions();
		this.standardExtensions = localCertReqDownRequest
				.getStandardExtensions();
		this.p10 = localCertReqDownRequest.getP10();
		this.tempPubKey = localCertReqDownRequest.getTempPubKey();
		this.debugLogger.appendMsg_L2("certDN=" + this.certDN);
		this.debugLogger.appendMsg_L2("ctmlName=" + this.ctmlName);
		this.debugLogger.appendMsg_L2("notBefore=" + this.notBefore);
		this.debugLogger.appendMsg_L2("validity=" + this.validity);
		this.debugLogger.appendMsg_L2("email=" + this.email);
		this.debugLogger.appendMsg_L2("remark=" + this.remark);
		this.debugLogger.appendMsg_L2("tempPubKey=" + this.tempPubKey);
		if (this.proExtensions != null) {
			Enumeration localEnumeration = this.proExtensions.keys();
			while (localEnumeration.hasMoreElements()) {
				String localObject1 = (String) localEnumeration.nextElement();
				String localObject2 = this.proExtensions
						.getProperty((String) localObject1);
				this.debugLogger.appendMsg_L2((String) localObject1 + "="
						+ (String) localObject2);
			}
		}
		this.operation.setObjSubject(this.certDN);
		this.operation.setObjCTMLName(this.ctmlName);
		if ((this.certDN == null) || (this.certDN.trim().equals(""))) {
			this.debugLogger.appendMsg_L1("certDN=" + this.certDN);
			throw new CertException("0202", "数据有效性检查 证书主题为空");
		}
		if (this.certDN.getBytes().length > 255) {
			this.debugLogger.appendMsg_L1("certDN Length="
					+ this.certDN.getBytes().length);
			throw new CertException("0203", "数据有效性检查 证书主题长度过长");
		}
		if (!ReqCheck.checkDN(this.certDN)) {
			this.debugLogger.appendMsg_L1("certDN=" + this.certDN);
			throw new CertException("0204", "数据有效性检查 证书主题格式不合法");
		}
		this.certDN = ReqCheck.filterDN(this.certDN);
		if ((this.ctmlName == null) || (this.ctmlName.trim().equals(""))) {
			this.debugLogger.appendMsg_L1("ctmlName=" + this.ctmlName);
			throw new CertException("0205", "数据有效性检查 证书模板为空");
		}
		if ((this.notBefore == null) || (this.notBefore.trim().equals(""))) {
			this.debugLogger.appendMsg_L1("notBefore=" + this.notBefore);
			throw new CertException("0206", "数据有效性检查 有效期起始日期为空");
		}
		if (this.notBefore.length() != 17) {
			this.debugLogger.appendMsg_L1("notBefore Length="
					+ this.notBefore.length());
			throw new CertException("0207", "数据有效性检查 有效期起始日期长度错误");
		}
		try {
			this.lnotBefore = Long.parseLong(this.notBefore);
		} catch (Exception localException1) {
			this.debugLogger.appendMsg_L1("notBefore=" + this.notBefore);
			throw new CertException("0209", "数据有效性检查 有效期起始日期格式格式不合法",
					localException1);
		}
		SimpleDateFormat localSimpleDateFormat = new SimpleDateFormat(
				"yyyyMMddHHmmssSSS");
		Object localObject1 = null;
		Object localObject2 = null;
		try {
			localObject1 = localSimpleDateFormat.parse(this.notBefore);
		} catch (Exception localException2) {
			this.debugLogger.appendMsg_L1("notBefore=" + this.notBefore);
			throw new CertException("0209", "数据有效性检查 有效期起始日期格式格式不合法",
					localException2);
		}
		try {
			localObject2 = CAConfig.getInstance();
		} catch (IDAException localIDAException) {
			throw new CertException(localIDAException.getErrCode(),
					localIDAException.getErrDesc(), localIDAException
							.getHistory());
		}
		long l = ((CAConfig) localObject2).getTimeDifAllow();
		Date localDate1 = new Date(((Date) localObject1).getTime() + l);
		Date localDate2 = new Date();
		this.debugLogger.appendMsg_L2("notBefore Date="
				+ ((Date) localObject1).toString());
		this.debugLogger.appendMsg_L2("timeDifAllow=" + l);
		this.debugLogger.appendMsg_L2("System Date=" + localDate2.toString());
		if (localDate1.compareTo(localDate2) < 0) {
			this.debugLogger.appendMsg_L1("notBefore Date="
					+ ((Date) localObject1).toString());
			this.debugLogger.appendMsg_L1("timeDifAllow=" + l);
			this.debugLogger.appendMsg_L1("System Date="
					+ localDate2.toString());
			throw new CertException("0208", "数据有效性检查 有效期起始日期超出服务允许范围");
		}
		if ((this.validity == null) || (this.validity.trim().equals(""))) {
			this.debugLogger.appendMsg_L1("validity=" + this.validity);
			throw new CertException("0210", "数据有效性检查 有效期天数为空");
		}
		if (this.validity.length() > 6) {
			this.debugLogger.appendMsg_L1("validity length="
					+ this.validity.length());
			throw new CertException("0222", "数据有效性检查 有效期长度内容超长");
		}
		try {
			this.ivalidity = Integer.parseInt(this.validity);
		} catch (Exception localException3) {
			this.debugLogger.appendMsg_L1("validity=" + this.validity);
			throw new CertException("0211", "数据有效性检查 有效期格式不合法", localException3);
		}
		if (this.ivalidity <= 0) {
			this.debugLogger.appendMsg_L1("validity value=" + this.ivalidity);
			throw new CertException("0212", "数据有效性检查 有效天数小于0");
		}
		if ((this.email != null) && (this.email.length() > 100)) {
			this.debugLogger
					.appendMsg_L1("email length=" + this.email.length());
			throw new CertException("0220", "数据有效性检查 e-mail内容超长");
		}
		if ((this.remark != null) && (this.remark.length() > 255)) {
			this.debugLogger.appendMsg_L1("remark length="
					+ this.remark.length());
			throw new CertException("0221", "数据有效性检查 备注信息内容超长");
		}
		String[] arrayOfString = CodeGenerator.generateCodes();
		this.certSN = arrayOfString[0];
		this.authCode = arrayOfString[1];
	}

	protected void policyCheck() throws CertException {
		TemplateAdmin localTemplateAdmin = null;
		try {
			localTemplateAdmin = TemplateAdmin.getInstance();
		} catch (IDAException localIDAException) {
			throw new CertException(localIDAException.getErrCode(),
					localIDAException.getErrDesc(), localIDAException
							.getHistory());
		}
		boolean bool = localTemplateAdmin.isPass(this.operator.getOperatorSN(),
				this.ctmlName, this.certDN, null);
		if (!bool) {
			this.debugLogger.appendMsg_L1("operator="
					+ this.operator.getOperatorDN());
			this.debugLogger.appendMsg_L1("ctmlName=" + this.ctmlName);
			this.debugLogger.appendMsg_L1("certDN=" + this.certDN);
			throw new CertException("0301", "操作员权限检查 操作员业务权限检查失败",
					new Exception(
							"操作员不具备此操作权限，请检查操作员是否有操作此模板的权限，并且确保证书DN符合管理员权限范围"));
		}
	}

	protected void CTMLCheck() throws CertException {
		CTMLManager localCTMLManager = CTMLManager.getInstance();
		CTML localCTML = null;
		try {
			localCTML = localCTMLManager.getCTML(this.ctmlName);
		} catch (IDAException localIDAException1) {
			throw new CertException(localIDAException1.getErrCode(),
					localIDAException1.getErrDesc(), localIDAException1
							.getHistory());
		}
		String str1 = localCTML.getCTMLStatus();
		this.debugLogger.appendMsg_L2("ctmlName=" + this.ctmlName);
		this.debugLogger.appendMsg_L2("ctmlStatus=" + str1);
		if (str1.equals("REVOKED")) {
			this.debugLogger.appendMsg_L2("ctmlStatus=" + str1);
			throw new CertException("0501", "模板策略检查 证书模板状态无效");
		}
		X509V3CTML localX509V3CTML1 = (X509V3CTML) localCTML;
		long l1 = localX509V3CTML1.getNotAfter();
		SimpleDateFormat localSimpleDateFormat = new SimpleDateFormat(
				"yyyyMMddHHmmssSSS");
		Date localDate1 = null;
		Date localDate2 = null;
		Date localDate3 = null;
		try {
			localDate1 = localSimpleDateFormat.parse(this.notBefore);
		} catch (Exception localException1) {
			this.debugLogger.appendMsg_L1("notBefore=" + this.notBefore);
			throw new CertException("0209", "数据有效性检查 有效期起始日期格式格式不合法",
					localException1);
		}
		this.debugLogger.appendMsg_L2("certNotBefore Date="
				+ localDate1.toString());
		try {
			localDate3 = localSimpleDateFormat.parse(Long.toString(l1));
		} catch (Exception localException2) {
			this.debugLogger.appendMsg_L1("ctmlNotAfter=" + l1);
			throw new CertException("0508", "模板策略检查 解析模板终止有效期错误");
		}
		this.debugLogger.appendMsg_L2("ctmlNotAfter Date="
				+ localDate3.toString());
		long l2 = localX509V3CTML1.getMaxValidty();
		this.debugLogger.appendMsg_L2("ctmlMaxValidity=" + l2);
		if (this.ivalidity > l2) {
			this.debugLogger.appendMsg_L1("ivalidity=" + this.ivalidity);
			this.debugLogger.appendMsg_L1("ctmlMaxValidity=" + l2);
			throw new CertException("0502", "模板策略检查 超出证书模板允许有效期的最大效期");
		}
		long l3 = localDate1.getTime();
		long l4 = this.ivalidity * 24L * 60L * 60L * 1000L;
		long l5 = l3 + l4;
		localDate2 = new Date(l5);
		this.debugLogger.appendMsg_L2("certNotAfter Date="
				+ localDate2.toString());
		this.notAfter = localSimpleDateFormat.format(localDate2);
		this.lnotAfter = Long.parseLong(this.notAfter);
		if (localDate2.compareTo(localDate3) > 0) {
			this.debugLogger.appendMsg_L1("certNotAfter Date="
					+ localDate2.toString());
			this.debugLogger.appendMsg_L1("ctmlNotAfter Date="
					+ localDate3.toString());
			throw new CertException("0503", "模板策略检查 超出模板有效期范围");
		}
		CAConfig localCAConfig = null;
		try {
			localCAConfig = CAConfig.getInstance();
		} catch (IDAException localIDAException2) {
			throw new CertException(localIDAException2.getErrCode(),
					localIDAException2.getErrDesc(), localIDAException2
							.getHistory());
		}
		X509Cert localX509Cert = localCAConfig.getRootCert();
		Date localDate4 = localX509Cert.getNotAfter();
		if (localDate2.compareTo(localDate4) > 0) {
			this.debugLogger.appendMsg_L1("certNotAfter Date="
					+ localDate2.toString());
			this.debugLogger.appendMsg_L1("rootCertNotAfter Date="
					+ localDate4.toString());
			throw new CertException("0224", "数据有效性检查 证书终止有效期超过CA签名证书有效期");
		}
		Object localObject3;
		Object localObject5;
		Object localObject7;
		if (this.standardExtensions != null) {
			Hashtable localHashtable = new Hashtable();
			Enumeration localEnumeration = this.standardExtensions.keys();
			int k = 0;
			int m = 0;
			while (localEnumeration.hasMoreElements()) {
				localObject3 = (String) localEnumeration.nextElement();
				localObject5 = new Vector();
				localObject7 = (Vector) this.standardExtensions
						.get(localObject3);
				int i3 = ((Vector) localObject7).size();
				for (int i5 = 0; i5 < i3; i5++) {
					StandardExtension localStandardExtension = (StandardExtension) ((Vector) localObject7)
							.get(i5);
					String str3 = localStandardExtension.getStandardValue();
					String str5 = localStandardExtension.getAllowNull();
					String str6 = localStandardExtension.getParentName();
					String str7 = localStandardExtension.getChildName();
					if ((str6.equals("PolicyMappings")) && (str3.equals("")))
						k = k == 0 ? 1 : 0;
					if ((str6.equals("PolicyConstrants")) && (str3.equals("")))
						m = m == 0 ? 1 : 0;
					String str8 = null;
					if (str7.equalsIgnoreCase("otherName")) {
						str9 = localStandardExtension.getOtherNameOid();
						str8 = (String) CTMLConstant.standardExtAndEncodeType
								.get(str9 + "@" + str6);
					} else {
						str8 = (String) CTMLConstant.standardExtAndEncodeType
								.get(str7 + "@" + str6);
					}
					String str9 = (String) CTMLConstant.standardExtAndLable
							.get(str7 + "@" + str6);
					if ((str5.equalsIgnoreCase("FALSE"))
							&& ((str3 == null) || (str3.trim().equals("")))) {
						this.debugLogger.appendMsg_L1("StandardExtension name="
								+ str9);
						this.debugLogger
								.appendMsg_L1("StandardExtension value=" + str3);
						throw new CertException("0518", "模板策略检查 标准扩展域内容格式不合法",
								new Exception("标准扩展域 [" + str9 + "] 不能为空."));
					}
					if ((str5.equalsIgnoreCase("TRUE"))
							&& ((str3 == null) || (str3.trim().equals(""))))
						((Vector) localObject5).add(localStandardExtension);
					if ((str3 == null) || (str3.trim().equals("")))
						continue;
					boolean bool2 = checkExtenValue(str8, str3);
					if (bool2)
						continue;
					this.debugLogger.appendMsg_L1("StandardExtension name="
							+ str9);
					this.debugLogger.appendMsg_L1("StandardExtension encoding="
							+ str8);
					this.debugLogger.appendMsg_L1("StandardExtension value="
							+ str3);
					throw new CertException("0518", "模板策略检查 标准扩展域内容格式不合法",
							new Exception("标准扩展域 [" + str9 + "] 必须是 " + str8
									+ " 类型."));
				}
				((Vector) localObject7).removeAll((Collection) localObject5);
				if (((Vector) localObject7).size() == 0)
					continue;
				localHashtable.put(localObject3, localObject7);
			}
			if ((k == 1) || (m == 1))
				throw new CertException("0518", "模板策略检查 标准扩展域内容格式不合法",
						new Exception("使用策略映射或策略限制必须成对赋值"));
			this.certInfo.setStandardExtensionsHT(localHashtable);
			localObject3 = localHashtable.elements();
			localObject5 = new Vector();
			while (((Enumeration) localObject3).hasMoreElements()) {
				localObject7 = (Vector) ((Enumeration) localObject3)
						.nextElement();
				((Vector) localObject5).addAll((Collection) localObject7);
			}
			this.certInfo.setStandardExtensions((Vector) localObject5);
		}
		int i = localX509V3CTML1.getExtensionCount();
		String str2;
		for (int j = 0; j < i; j++) {
			if (!localX509V3CTML1.getUserProcessPolicy(j).equals("NEED"))
				continue;
			localObject2 = localX509V3CTML1.getExtensionName(j);
			str2 = localX509V3CTML1.getExtensionOID(j);
			if ((this.proExtensions == null)
					|| (!this.proExtensions.containsKey(localObject2))) {
				this.debugLogger.appendMsg_L1((String) localObject2
						+ " value=null");
				throw new CertException("0504", "模板策略检查 必须的扩展域信息不完整",
						new Exception("error occurred in extion iterm "
								+ (String) localObject2));
			}
			localObject3 = (String) this.proExtensions.get(localObject2);
			if ((localObject3 != null) && (!((String) localObject3).equals("")))
				continue;
			this.debugLogger.appendMsg_L1((String) localObject2 + " value="
					+ (String) localObject3);
			throw new CertException("0504", "模板策略检查 必须的扩展域信息不完整",
					new Exception("error occurred in extion iterm "
							+ (String) localObject2));
		}
		Object localObject10;
		Object localObject12;
		if (this.proExtensions != null) {
			localObject1 = this.proExtensions.keys();
			while (((Enumeration) localObject1).hasMoreElements()) {
				localObject2 = (String) ((Enumeration) localObject1)
						.nextElement();
				str2 = this.proExtensions.getProperty((String) localObject2);
				if ((str2 != null) && (!str2.trim().equals("")))
					continue;
				this.proExtensions.remove(localObject2);
			}
			localObject2 = new Extension[this.proExtensions.size()];
			int n = 0;
			for (int i1 = 0; i1 < i; i1++) {
				localObject5 = localX509V3CTML1.getExtensionName(i1);
				localObject7 = localX509V3CTML1.getExtensionEncoding(i1);
				if (!this.proExtensions.containsKey(localObject5))
					continue;
				localObject10 = localX509V3CTML1.getExtensionOID(i1);
				localObject2[n] = new Extension();
				localObject2[n].setName((String) localObject5);
				localObject2[n].setOid((String) localObject10);
				localObject12 = this.proExtensions
						.getProperty((String) localObject5);
				int i6 = ((String) localObject12).getBytes().length;
				if (i6 > 4000) {
					this.debugLogger
							.appendMsg_L1("user self extension value length="
									+ i6);
					this.debugLogger
							.appendMsg_L1("sytem self extension max length=4000");
					throw new CertException(
							"0512",
							"模板策略检查 自定义扩展域内容格式不合法",
							new Exception(
									"the value of self extension is too long, max size [4000]"));
				}
				if ((localObject12 != null)
						&& (!((String) localObject12).trim().equals(""))) {
					boolean bool1 = checkExtValue((String) localObject7,
							(String) localObject12);
					if (!bool1) {
						this.debugLogger.appendMsg_L1("extension name="
								+ (String) localObject5);
						this.debugLogger.appendMsg_L1("extension encoding="
								+ (String) localObject7);
						this.debugLogger.appendMsg_L1("extension value="
								+ (String) localObject12);
						throw new CertException("0512", "模板策略检查 自定义扩展域内容格式不合法",
								new Exception("self extension ["
										+ (String) localObject5 + "] must be "
										+ (String) localObject7 + " type."));
					}
				}
				localObject2[n].setValue(this.proExtensions
						.getProperty((String) localObject5));
				n++;
			}
			localObject4 = new Vector();
			for (int i2 = 0; i2 < localObject2.length; i2++) {
				if (localObject2[i2] == null)
					continue;
				((Vector) localObject4).add(localObject2[i2]);
			}
			localObject6 = new Extension[((Vector) localObject4).size()];
			if (localObject6.length > 0) {
				((Vector) localObject4).toArray(localObject6);
				localObject7 = new CertExtensions(localObject6);
				this.certInfo.setCertExtensions((CertExtensions) localObject7);
			}
		}
		Object localObject1 = JCrypto.getInstance();
		Object localObject2 = null;
		try {
			localObject2 = ((JCrypto) localObject1).openSession("JSOFT_LIB");
		} catch (PKIException localPKIException1) {
			throw new CertException(localPKIException1.getErrCode(),
					localPKIException1.getErrDesc(), localPKIException1
							.getHistory());
		}
		X509V3CTML localX509V3CTML2 = (X509V3CTML) localCTML;
		Object localObject4 = localX509V3CTML2.getKeyGenPlace();
		Object localObject6 = null;
		Object localObject11;
		if (((String) localObject4).equals("LOCAL")) {
			this.returnP7B = true;
			this.useKMC = false;
			if ((this.p10 == null) || (this.p10.trim().equals(""))) {
				this.debugLogger.appendMsg_L1("p10=" + this.p10);
				throw new CertException("0218", "数据有效性检查 P10申请书必须非空");
			}
			Object localObject8 = 0;
			try {
				localObject10 = new PKCS10((Session) localObject2);
				((PKCS10) localObject10).load(this.p10.getBytes());
				localObject6 = ((PKCS10) localObject10).getPubKey();
				this.attributes = ((PKCS10) localObject10).getAttributes();
				localObject8 = Parser.getRSAKeyLength((JKey) localObject6);
			} catch (PKIException localPKIException2) {
				throw new CertException(localPKIException2.getErrCode(),
						localPKIException2.getErrDesc(), localPKIException2
								.getHistory());
			}
			this.debugLogger.appendMsg_L2("p10 keyType="
					+ ((JKey) localObject6).getKeyType());
			this.debugLogger.appendMsg_L2("p10 keyLength=" + localObject8);
			localObject11 = localX509V3CTML2.getKeyType();
			if (((JKey) localObject6).getKeyType().indexOf(
					(String) localObject11) == -1) {
				this.debugLogger.appendMsg_L1("user keyType="
						+ ((JKey) localObject6).getKey());
				this.debugLogger.appendMsg_L1("ctml request keyType="
						+ (String) localObject11);
				throw new CertException("0506", "模板策略检查 密钥类型不合法");
			}
			localObject12 = localObject8;
			if (localObject12 != localX509V3CTML2.getKeyLength()) {
				this.debugLogger.appendMsg_L1("user keyLength=" + localObject8);
				this.debugLogger.appendMsg_L1("ctml request keyLength="
						+ localX509V3CTML2.getKeyLength());
				throw new CertException("0507", "模板策略检查 密钥长度不合法");
			}
		} else {
			Object localObject9;
			if (((String) localObject4).equals("KMC")) {
				this.returnP7B = true;
				this.useKMC = true;
				this.response.setUseKMC("TRUE");
				if ((this.tempPubKey == null)
						|| (this.tempPubKey.trim().equals(""))) {
					this.debugLogger.appendMsg_L1("tempPubKey="
							+ this.tempPubKey);
					throw new CertException("0513",
							"模板策略检查 未检测到KMC产生密钥所需的临时公钥", new Exception(
									"请选用CSP方式向KMCServer申请密钥"));
				}
				localObject9 = new KMCCertInfo();
				((KMCCertInfo) localObject9).setCertSN(this.certSN);
				((KMCCertInfo) localObject9).setCertDN(this.certDN);
				((KMCCertInfo) localObject9).setNotBefore(this.lnotBefore);
				((KMCCertInfo) localObject9).setNotAfter(this.lnotAfter);
				((KMCCertInfo) localObject9).setValidity(this.ivalidity);
				((KMCCertInfo) localObject9).setCtmlName(this.ctmlName);
				((KMCCertInfo) localObject9).setTempPubKey(this.tempPubKey);
				localObject11 = new KMCConnector();
				localObject12 = ((KMCConnector) localObject11)
						.requestKeyFromKMC((KMCCertInfo) localObject9);
				if (!((KeyApplyResponse) localObject12).getErr().equals("0")) {
					localObject13 = ((KeyApplyResponse) localObject12)
							.getDetail();
					Exception localException3 = null;
					if ((localObject13 != null) && (localObject13.length > 0))
						if (localObject13[0].length == 1)
							localException3 = new Exception(localObject13[0][0]);
						else if (localObject13[0].length == 2)
							localException3 = new Exception(localObject13[0][0]
									+ " " + localObject13[0][1]);
					throw new CertException("0517", "模板策略检查 向KMC申请密钥失败:"
							+ ((KeyApplyResponse) localObject12).getMsg(),
							localException3);
				}
				Object localObject13 = ((KeyApplyResponse) localObject12)
						.getPubKey();
				this.encryptedSessionKey = new String(
						((KeyApplyResponse) localObject12).getEnvelopedData()
								.getEncryptedSessionKey());
				this.sessionKeyAlg = ((KeyApplyResponse) localObject12)
						.getEnvelopedData().getSessionKeyAlg();
				this.sessionKeyPad = ((KeyApplyResponse) localObject12)
						.getEnvelopedData().getSessionKeyPad();
				this.encryptedPrivateKey = new String(
						((KeyApplyResponse) localObject12).getEnvelopedData()
								.getEncryptedPrivateKey());
				try {
					localObject13 = Parser.hardKey2SoftKey("RSA_Public", Base64
							.decode(localObject13));
				} catch (PKIException localPKIException4) {
					throw new CertException("05"
							+ localPKIException4.getErrCode(), "模板策略检查 "
							+ localPKIException4.getErrDesc(),
							localPKIException4.getHistory());
				}
				String str4 = localX509V3CTML2.getKeyType();
				if (str4.equals("RSA"))
					localObject6 = new JKey("RSA_Public", localObject13);
			} else if (((String) localObject4).equals("CA")) {
				this.returnP7B = false;
				this.useKMC = false;
				localObject9 = localX509V3CTML2.getKeyType();
				int i4 = localX509V3CTML2.getKeyLength();
				this.debugLogger.appendMsg_L2("CA Generate keyType="
						+ (String) localObject9);
				this.debugLogger.appendMsg_L2("CA Generate keyLength=" + i4);
				localObject12 = null;
				if (((String) localObject9).equals("RSA")) {
					localObject12 = new Mechanism("RSA");
					try {
						this.keyPair = ((Session) localObject2)
								.generateKeyPair((Mechanism) localObject12, i4);
					} catch (PKIException localPKIException3) {
						throw new CertException(
								localPKIException3.getErrCode(),
								localPKIException3.getErrDesc(),
								localPKIException3.getHistory());
					}
				}
				localObject6 = this.keyPair.getPublicKey();
			}
		}
		this.certInfo.setPubKey((JKey) localObject6);
	}

	public static boolean checkExtValue(String paramString1, String paramString2) {
		if (paramString1.equals("Integer")) {
			try {
				new BigInteger(paramString2);
			} catch (Exception localException1) {
				return false;
			}
		} else if (paramString1.equals("Boolean")) {
			if ((!paramString2.toUpperCase().equals("TRUE"))
					&& (!paramString2.toUpperCase().equals("FALSE")))
				return false;
		} else if (paramString1.equals("User Defined")) {
			if (!Parser.isBase64Encode(paramString2.getBytes()))
				return false;
			try {
				Base64.decode(paramString2.getBytes());
			} catch (Exception localException2) {
				return false;
			}
		} else {
			Object localObject;
			if (paramString1.equals("EMAIL")) {
				Pattern localPattern1 = Pattern
						.compile("^[\\w-]+(\\.[\\w-]+)*@[\\w-]+(\\.[\\w-]+)+$");
				localObject = localPattern1.matcher(paramString2);
				boolean bool3 = ((Matcher) localObject).matches();
				return bool3;
			}
			if (paramString1.equals("X500Name")) {
				boolean bool1 = ReqCheck.checkDN(paramString2);
				if (!bool1)
					return false;
			} else {
				String str1;
				if (paramString1.equals("IP")) {
					str1 = "(2[0-4]\\d)|(25[0-5])";
					localObject = "1\\d{2}";
					String str2 = "[1-9]\\d";
					String str3 = "\\d";
					String str4 = "(" + str1 + ")|(" + (String) localObject
							+ ")|(" + str2 + ")|(" + str3 + ")";
					str4 = "(" + str4 + ").(" + str4 + ").(" + str4 + ").("
							+ str4 + ")";
					Pattern localPattern2 = Pattern.compile(str4);
					Matcher localMatcher = localPattern2.matcher(paramString2);
					boolean bool4 = localMatcher.matches();
					return bool4;
				}
				if (paramString1.equals("Printable String")) {
					str1 = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz '+,-./:=?()";
					localObject = paramString2.toCharArray();
					for (int i = 0; i < localObject.length; i++)
						if (str1.indexOf(localObject[i]) < 0)
							return false;
					return true;
				}
				if (paramString1.equals("OtherName GUID"))
					try {
						Hex.decode(paramString2);
						return true;
					} catch (Exception localException3) {
						return false;
					}
				if (paramString1.equals("OID"))
					try {
						ObjectIdentifier(paramString2);
						return true;
					} catch (Exception localException4) {
						return false;
					}
				if (!paramString1.equals("UTF-8 String")) {
					boolean bool2 = isGB2312(paramString2);
					if (bool2)
						return false;
				}
			}
		}
		return true;
	}

	private static boolean isGB2312(String paramString) {
		char[] arrayOfChar = paramString.toCharArray();
		int i = 0;
		for (int j = 0; j < arrayOfChar.length; j++) {
			byte[] arrayOfByte = ("" + arrayOfChar[j]).getBytes();
			if (arrayOfByte.length != 2)
				continue;
			int[] arrayOfInt = new int[2];
			arrayOfByte[0] &= 255;
			arrayOfByte[1] &= 255;
			if ((arrayOfInt[0] < 129) || (arrayOfInt[0] > 254)
					|| (arrayOfInt[1] < 64) || (arrayOfInt[1] > 254))
				continue;
			i = 1;
			break;
		}
		return i;
	}

	private static void ObjectIdentifier(String paramString) throws IOException {
		int j = 46;
		int k = 0;
		int m = 0;
		for (int i = 0; (m = paramString.indexOf(j, k)) != -1; i++)
			k = m + 1;
		i++;
		int[] arrayOfInt = new int[i];
		k = 0;
		int n = 0;
		String str = null;
		while ((m = paramString.indexOf(j, k)) != -1) {
			str = paramString.substring(k, m);
			if ((arrayOfInt[(n++)] = Integer.valueOf(str).intValue()) < 0)
				throw new IOException("oid components must be nonnegative");
			k = m + 1;
		}
		str = paramString.substring(k);
		arrayOfInt[n] = Integer.valueOf(str).intValue();
		if ((arrayOfInt[0] < 0) || (arrayOfInt[0] > 2))
			throw new IOException("First oid component is invalid ");
		if ((arrayOfInt[1] < 0) || (arrayOfInt[1] > 39))
			throw new IOException("Second oid component is invalid ");
	}

	private void directGetData() throws CertException {
		CertReqDownRequest localCertReqDownRequest = new CertReqDownRequest(
				this.request);
		this.certDN = localCertReqDownRequest.getCertDN();
		this.ctmlName = localCertReqDownRequest.getCtmlName();
		this.notBefore = localCertReqDownRequest.getNotBefore();
		this.validity = localCertReqDownRequest.getValidity();
		this.email = localCertReqDownRequest.getEmail();
		this.remark = localCertReqDownRequest.getRemark();
		this.proExtensions = localCertReqDownRequest.getExtensions();
		this.p10 = localCertReqDownRequest.getP10();
		SimpleDateFormat localSimpleDateFormat = new SimpleDateFormat(
				"yyyyMMddHHmmssSSS");
		Date localDate1 = null;
		Date localDate2 = null;
		try {
			localDate1 = localSimpleDateFormat.parse(this.notBefore);
		} catch (Exception localException1) {
			System.out.println(localException1.toString());
		}
		long l1 = localDate1.getTime();
		long l2 = this.ivalidity * 24L * 60L * 60L * 1000L;
		long l3 = l1 + l2;
		localDate2 = new Date(l3);
		this.debugLogger.appendMsg_L2("certNotAfter Date="
				+ localDate2.toString());
		this.notAfter = localSimpleDateFormat.format(localDate2);
		this.lnotAfter = Long.parseLong(this.notAfter);
		CTMLManager localCTMLManager = CTMLManager.getInstance();
		X509V3CTML localX509V3CTML = null;
		try {
			localX509V3CTML = (X509V3CTML) localCTMLManager
					.getCTML(this.ctmlName);
		} catch (IDAException localIDAException) {
			throw new CertException(localIDAException.getErrCode(),
					localIDAException.getErrDesc(), localIDAException
							.getHistory());
		}
		Object localObject5;
		Object localObject6;
		Object localObject3;
		if (this.proExtensions != null) {
			localObject1 = this.proExtensions.keys();
			while (((Enumeration) localObject1).hasMoreElements()) {
				localObject2 = (String) ((Enumeration) localObject1)
						.nextElement();
				String str1 = this.proExtensions
						.getProperty((String) localObject2);
				if ((str1 != null) && (!str1.trim().equals("")))
					continue;
				this.proExtensions.remove(localObject2);
			}
			localObject2 = new Extension[this.proExtensions.size()];
			int i = 0;
			int j = localX509V3CTML.getExtensionCount();
			for (int k = 0; k < j; k++) {
				localObject5 = localX509V3CTML.getExtensionName(k);
				if (!this.proExtensions.containsKey(localObject5))
					continue;
				localObject6 = localX509V3CTML.getExtensionOID(k);
				localObject2[i] = new Extension();
				localObject2[i].setName((String) localObject5);
				localObject2[i].setOid((String) localObject6);
				localObject2[i].setValue(this.proExtensions
						.getProperty((String) localObject5));
				i++;
			}
			localObject3 = new CertExtensions(localObject2);
			this.certInfo.setCertExtensions((CertExtensions) localObject3);
		}
		Object localObject1 = JCrypto.getInstance();
		Object localObject2 = null;
		try {
			localObject2 = ((JCrypto) localObject1).openSession("JSOFT_LIB");
		} catch (PKIException localPKIException1) {
			throw new CertException(localPKIException1.getErrCode(),
					localPKIException1.getErrDesc(), localPKIException1
							.getHistory());
		}
		String str2 = localX509V3CTML.getKeyGenPlace();
		JKey localJKey = null;
		if (str2.equals("LOCAL")) {
			this.returnP7B = true;
			try {
				localObject3 = new PKCS10((Session) localObject2);
				((PKCS10) localObject3).load(this.p10.getBytes());
				localJKey = ((PKCS10) localObject3).getPubKey();
				this.attributes = ((PKCS10) localObject3).getAttributes();
			} catch (PKIException localPKIException2) {
				throw new CertException(localPKIException2.getErrCode(),
						localPKIException2.getErrDesc(), localPKIException2
								.getHistory());
			}
		} else {
			Object localObject4;
			if (str2.equals("KMC")) {
				this.returnP7B = true;
				this.useKMC = true;
				this.response.setUseKMC("TRUE");
				if ((this.tempPubKey == null) || (this.tempPubKey.equals(""))) {
					this.debugLogger.appendMsg_L1("tempPubKey="
							+ this.tempPubKey);
					throw new CertException("0513", "模板策略检查 未检测到KMC产生密钥所需的临时公钥");
				}
				localObject4 = new KMCCertInfo();
				((KMCCertInfo) localObject4).setCertSN(this.certSN);
				((KMCCertInfo) localObject4).setCertDN(this.certDN);
				((KMCCertInfo) localObject4).setNotBefore(this.lnotBefore);
				((KMCCertInfo) localObject4).setNotAfter(this.lnotAfter);
				((KMCCertInfo) localObject4).setValidity(this.ivalidity);
				((KMCCertInfo) localObject4).setCtmlName(this.ctmlName);
				((KMCCertInfo) localObject4).setTempPubKey(this.tempPubKey);
				localObject5 = new KMCConnector();
				localObject6 = ((KMCConnector) localObject5)
						.requestKeyFromKMC((KMCCertInfo) localObject4);
				if (!((KeyApplyResponse) localObject6).getErr().equals("0")) {
					localObject7 = ((KeyApplyResponse) localObject6)
							.getDetail();
					Exception localException2 = null;
					if ((localObject7 != null) && (localObject7.length > 0))
						localException2 = new Exception(localObject7[0][1]);
					throw new CertException("0517", "模板策略检查 向KMC申请密钥失败:"
							+ ((KeyApplyResponse) localObject6).getMsg(),
							localException2);
				}
				Object localObject7 = ((KeyApplyResponse) localObject6)
						.getPubKey();
				this.encryptedSessionKey = new String(
						((KeyApplyResponse) localObject6).getEnvelopedData()
								.getEncryptedSessionKey());
				this.sessionKeyAlg = ((KeyApplyResponse) localObject6)
						.getEnvelopedData().getSessionKeyAlg();
				this.sessionKeyPad = ((KeyApplyResponse) localObject6)
						.getEnvelopedData().getSessionKeyPad();
				this.encryptedPrivateKey = new String(
						((KeyApplyResponse) localObject6).getEnvelopedData()
								.getEncryptedPrivateKey());
				try {
					localObject7 = Parser.hardKey2SoftKey("RSA_Public", Base64
							.decode(localObject7));
				} catch (PKIException localPKIException4) {
					throw new CertException("05"
							+ localPKIException4.getErrCode(), "模板策略检查 "
							+ localPKIException4.getErrDesc(),
							localPKIException4.getHistory());
				}
				String str3 = localX509V3CTML.getKeyType();
				if (str3.equals("RSA"))
					localJKey = new JKey("RSA_Public", localObject7);
			} else if (str2.equals("CA")) {
				this.returnP7B = false;
				localObject4 = localX509V3CTML.getKeyType();
				int m = localX509V3CTML.getKeyLength();
				localObject6 = null;
				if (((String) localObject4).equals("RSA")) {
					localObject6 = new Mechanism("RSA");
					try {
						this.keyPair = ((Session) localObject2)
								.generateKeyPair((Mechanism) localObject6, m);
					} catch (PKIException localPKIException3) {
						throw new CertException(
								localPKIException3.getErrCode(),
								localPKIException3.getErrDesc(),
								localPKIException3.getHistory());
					}
				}
				localJKey = this.keyPair.getPublicKey();
			}
		}
		this.certInfo.setPubKey(localJKey);
	}

	private boolean checkExtenValue(String paramString1, String paramString2) {
		String[] arrayOfString = paramString2.split("[;]");
		int i = 1;
		for (int j = 0; j < arrayOfString.length; j++) {
			boolean bool = checkExtValue(paramString1, arrayOfString[j].trim());
			if (!bool)
				return false;
		}
		return i;
	}
}

/*
 * Location: C:\Program Files\JIT\CA50\lib\IDA\ida.jar Qualified Name:
 * cn.com.jit.ida.ca.certmanager.service.CertReqDown JD-Core Version: 0.6.0
 */