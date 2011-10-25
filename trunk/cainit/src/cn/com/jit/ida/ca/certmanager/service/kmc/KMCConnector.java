package cn.com.jit.ida.ca.certmanager.service.kmc;

import cn.com.jit.ida.IDAException;
import cn.com.jit.ida.ca.certmanager.CertException;
import cn.com.jit.ida.ca.certmanager.service.request.CAKeyRecoverRequest;
import cn.com.jit.ida.ca.config.CryptoConfig;
import cn.com.jit.ida.ca.config.KMCConfig;
import cn.com.jit.ida.ca.config.ServerConfig;
import cn.com.jit.ida.ca.ctml.CTMLManager;
import cn.com.jit.ida.ca.ctml.x509v3.X509V3CTML;
import cn.com.jit.ida.net.NetConfig;
import cn.com.jit.ida.net.client.Client;

public class KMCConnector
{
  private KMCCertInfo certInfo = null;
  private NetConfig netConfig = new NetConfig();
  private byte[] responseData = null;
  private CryptoConfig cryptoConfig = null;

  public KMCConnector()
  {
    try
    {
      this.cryptoConfig = CryptoConfig.getInstance();
    }
    catch (IDAException localIDAException)
    {
    }
  }

  public KeyApplyResponse requestKeyFromKMC(KMCCertInfo paramKMCCertInfo)
    throws CertException
  {
    this.certInfo = paramKMCCertInfo;
    String str1 = this.certInfo.getCtmlName();
    CTMLManager localCTMLManager = CTMLManager.getInstance();
    X509V3CTML localX509V3CTML = null;
    try
    {
      localX509V3CTML = (X509V3CTML)localCTMLManager.getCTML(str1);
    }
    catch (IDAException localIDAException)
    {
      throw new CertException(localIDAException.getErrCode(), localIDAException.getMessage(), localIDAException.getHistory());
    }
    String str2 = localX509V3CTML.getKeyType();
    String str3 = Integer.toString(localX509V3CTML.getKeyLength());
    KeyApplyRequest localKeyApplyRequest = new KeyApplyRequest();
    localKeyApplyRequest.getRequestKey().setType(str2);
    localKeyApplyRequest.getRequestKey().setLength(str3);
    localKeyApplyRequest.getCertInfo().setSerialNumber(this.certInfo.getCertSN());
    localKeyApplyRequest.getCertInfo().setSubjectName(this.certInfo.getCertDN());
    localKeyApplyRequest.getCertInfo().setNotBefor(this.certInfo.getNotBefore());
    localKeyApplyRequest.getCertInfo().setNotAfter(this.certInfo.getNotAfter());
    localKeyApplyRequest.getCertInfo().setValidity(this.certInfo.getValidity());
    localKeyApplyRequest.getCertInfo().setModelName(str1);
    localKeyApplyRequest.getCertInfo().setPubKeyAlgorithm("RSA");
    localKeyApplyRequest.getCertInfo().setPubkey(this.certInfo.getTempPubKey().getBytes());
    String str4 = this.certInfo.getRetainKey();
    if ((str4 == null) || (str4.equals("false")))
    {
      localKeyApplyRequest.getRequestKey().setRetainKey(false);
    }
    else
    {
      localKeyApplyRequest.getRequestKey().setRetainKey(true);
      localKeyApplyRequest.getCertInfo().setOldCertSN(this.certInfo.getOldCertSN());
    }
    KeyApplyResponse localKeyApplyResponse = null;
    try
    {
      byte[] arrayOfByte = localKeyApplyRequest.getData();
      connectionInfo(arrayOfByte);
      localKeyApplyResponse = new KeyApplyResponse(this.responseData);
    }
    catch (Exception localException)
    {
      throw new CertException("0516", "模板策略检查 与KMCServer通信异常", localException);
    }
    return localKeyApplyResponse;
  }

  public recoverResponse recoverkeyFromKMC(CAKeyRecoverRequest paramCAKeyRecoverRequest)
    throws CertException
  {
    recoverRequest localrecoverRequest = new recoverRequest();
    localrecoverRequest.setApplyCaDN("");
    localrecoverRequest.setCertSerialNumber(paramCAKeyRecoverRequest.getCertSerialNumbe());
    localrecoverRequest.setPubkey(paramCAKeyRecoverRequest.getPubkey());
    localrecoverRequest.setCertSubject(paramCAKeyRecoverRequest.getCertSubject());
    localrecoverRequest.setComparetime(paramCAKeyRecoverRequest.getComparetime());
    recoverResponse localrecoverResponse = new recoverResponse();
    try
    {
      byte[] arrayOfByte = localrecoverRequest.getData();
      connectionInfo(arrayOfByte);
      localrecoverResponse = new recoverResponse(this.responseData);
    }
    catch (Exception localException)
    {
      throw new CertException("0516", "模板策略检查 与KMCServer通信异常", localException);
    }
    return localrecoverResponse;
  }

  public KeyStateTrackResponse updateKeyStateFromKMC(KeyStateTrackRequest paramKeyStateTrackRequest)
    throws CertException
  {
    KeyStateTrackResponse localKeyStateTrackResponse = new KeyStateTrackResponse();
    try
    {
      byte[] arrayOfByte = paramKeyStateTrackRequest.getData();
      connectionInfo(arrayOfByte);
      localKeyStateTrackResponse = new KeyStateTrackResponse(this.responseData);
    }
    catch (Exception localException)
    {
      throw new CertException("0516", "模板策略检查 与KMCServer通信异常", localException);
    }
    return localKeyStateTrackResponse;
  }

  public void connectionInfo(byte[] paramArrayOfByte)
    throws Exception
  {
    String str1 = this.cryptoConfig.getCommDeviceID();
    this.netConfig.setConnectType("SSL");
    this.netConfig.setDataType("BYTE");
    KMCConfig localKMCConfig = KMCConfig.getInstance();
    String str2 = localKMCConfig.getKMCServer();
    int i = localKMCConfig.getKMCPort();
    ServerConfig localServerConfig = ServerConfig.getInstance();
    String str3 = null;
    String str4 = null;
    str3 = localServerConfig.getCommunicateCert();
    str4 = localServerConfig.getCommunicateCertPWD();
    if (str1.equalsIgnoreCase("JSOFT_LIB"))
    {
      this.netConfig.setKeyStoreType("JKS");
      this.netConfig.setKeystore(str3);
      this.netConfig.setKeystorePassword(str4);
      this.netConfig.setTrustCerts(str3);
      this.netConfig.setTrustCertsPassword(str4);
    }
    else
    {
      this.netConfig.setKeyStoreType("PKCS11");
      this.netConfig.setTrustCerts(str3);
      this.netConfig.setTrustCertsPassword(str4);
    }
    this.netConfig.setServerAddress(str2);
    this.netConfig.setPort(i);
    Client localClient = new Client(str2, i, this.netConfig);
    this.responseData = localClient.request(paramArrayOfByte);
    if (this.responseData == null)
      throw new CertException("0516", "模板策略检查 与KMCServer通信异常", new Exception("response data is null."));
  }

  public KMCCertArcResponse setCertArcToKMC(KMCCertArcRequest paramKMCCertArcRequest)
    throws CertException
  {
    KMCCertArcResponse localKMCCertArcResponse = null;
    try
    {
      byte[] arrayOfByte = paramKMCCertArcRequest.getData();
      connectionInfo(arrayOfByte);
      localKMCCertArcResponse = new KMCCertArcResponse(this.responseData);
    }
    catch (Exception localException)
    {
      return null;
    }
    return localKMCCertArcResponse;
  }
}

/* Location:           C:\Program Files\JIT\CA50\lib\IDA\ida.jar
 * Qualified Name:     cn.com.jit.ida.ca.certmanager.service.kmc.KMCConnector
 * JD-Core Version:    0.6.0
 */