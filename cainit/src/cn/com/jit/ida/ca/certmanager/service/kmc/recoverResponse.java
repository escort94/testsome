package cn.com.jit.ida.ca.certmanager.service.kmc;

import cn.com.jit.ida.IDAException;
import cn.com.jit.ida.Response;
import cn.com.jit.ida.ca.certmanager.CertException;
import cn.com.jit.ida.util.pki.encoders.Base64;
import cn.com.jit.ida.util.xml.XMLTool;
import java.util.Vector;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class recoverResponse extends Response
{
  public static final String XMLTAG_PRIVATEKEY = "priKey";
  public static final String XMLTAG_CERTIFICATE = "certificate";
  private static final String XMLTAG_ENCRYPTEDSESSIONKEY = "encryptedSessionKey";
  private static final String XMLTAG_SESSIONKEYALG = "sessionKeyAlg";
  private static final String XMLTAG_SESSIOMKEYPAD = "sessionKeyPad";
  private static final String XMLTAG_CERTSN = "certSN";
  private static final String XMLTAG_ENCRYPTOCERTENTITY = "encryptoCertEntity";
  private static final String KMC_CARECOVERKEY = "CARECOVERKEY";
  Vector vrecoverkey = new Vector();

  public recoverResponse()
  {
    super.setOperation("CARECOVERKEY");
  }

  public recoverResponse(byte[] paramArrayOfByte)
    throws IDAException
  {
    try
    {
      super.setData(paramArrayOfByte);
    }
    catch (IDAException localIDAException)
    {
      throw new CertException("81140705", "密钥恢复服务其他错误 应答信息格式不合法", localIDAException);
    }
  }

  public recoverResponse(Response paramResponse)
    throws IDAException
  {
    try
    {
      super.setData(paramResponse.getDocument());
    }
    catch (IDAException localIDAException)
    {
      throw new CertException("81140705", "密钥恢复服务其他错误 应答信息格式不合法", localIDAException);
    }
  }

  protected final void updateBody(boolean paramBoolean)
    throws IDAException
  {
    RecoverKeyInfo localRecoverKeyInfo;
    if (paramBoolean)
    {
      this.body = this.doc.createElement("body");
      if ((this.vrecoverkey != null) && (this.vrecoverkey.size() > 0))
        for (int i = 0; i < this.vrecoverkey.size(); i++)
        {
          Element localElement1 = XMLTool.newChildElement(this.body, "entry");
          localRecoverKeyInfo = new RecoverKeyInfo();
          if (localRecoverKeyInfo.getPrivateKey() != null)
            XMLTool.newChildElement(localElement1, "priKey", new String(Base64.encode(localRecoverKeyInfo.getPrivateKey())));
          if (localRecoverKeyInfo.getCertificate() != null)
            XMLTool.newChildElement(localElement1, "certificate", new String(Base64.encode(localRecoverKeyInfo.getCertificate())));
          if (localRecoverKeyInfo.getEncryptedSessionKey() != null)
            XMLTool.newChildElement(localElement1, "encryptedSessionKey", new String(localRecoverKeyInfo.getEncryptedSessionKey()));
          if (localRecoverKeyInfo.getSessionKeyAlg() != null)
            XMLTool.newChildElement(localElement1, "sessionKeyAlg", localRecoverKeyInfo.getSessionKeyAlg());
          if (localRecoverKeyInfo.getSessionKeyPad() != null)
            XMLTool.newChildElement(localElement1, "sessionKeyPad", localRecoverKeyInfo.getSessionKeyPad());
          if (localRecoverKeyInfo.getCertSN() != null)
            XMLTool.newChildElement(localElement1, "certSN", localRecoverKeyInfo.getCertSN());
          if (localRecoverKeyInfo.getEncryptoCertEntity() == null)
            continue;
          XMLTool.newChildElement(localElement1, "encryptoCertEntity", new String(localRecoverKeyInfo.getEncryptoCertEntity()));
        }
    }
    else
    {
      NodeList localNodeList = XMLTool.getChildElements(this.body);
      if ((localNodeList != null) && (localNodeList.getLength() > 0))
        for (int j = 0; j < localNodeList.getLength(); j++)
        {
          localRecoverKeyInfo = new RecoverKeyInfo();
          Element localElement2 = (Element)localNodeList.item(j);
          localRecoverKeyInfo.setPrivateKey(Base64.decode(XMLTool.getValueByTagName(localElement2, "priKey").getBytes()));
          localRecoverKeyInfo.setCertificate(Base64.decode(XMLTool.getValueByTagName(localElement2, "certificate").getBytes()));
          String str1 = XMLTool.getValueByTagName(localElement2, "encryptedSessionKey");
          if (str1 != null)
            localRecoverKeyInfo.setEncryptedSessionKey(str1.getBytes());
          localRecoverKeyInfo.setSessionKeyAlg(XMLTool.getValueByTagName(localElement2, "sessionKeyAlg"));
          localRecoverKeyInfo.setSessionKeyPad(XMLTool.getValueByTagName(localElement2, "sessionKeyPad"));
          localRecoverKeyInfo.setCertSN(XMLTool.getValueByTagName(localElement2, "certSN"));
          String str2 = XMLTool.getValueByTagName(localElement2, "encryptoCertEntity");
          if (str2 == null)
            continue;
          localRecoverKeyInfo.setEncryptoCertEntity(str2.getBytes());
        }
    }
  }

  public static void main(String[] paramArrayOfString)
  {
  }

  public Vector getVrecoverkey()
  {
    return this.vrecoverkey;
  }

  public void setVrecoverkey(Vector paramVector)
  {
    this.vrecoverkey = paramVector;
  }
}

/* Location:           C:\Program Files\JIT\CA50\lib\IDA\ida.jar
 * Qualified Name:     cn.com.jit.ida.ca.certmanager.service.kmc.recoverResponse
 * JD-Core Version:    0.6.0
 */