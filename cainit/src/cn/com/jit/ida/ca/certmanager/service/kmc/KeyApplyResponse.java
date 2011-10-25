package cn.com.jit.ida.ca.certmanager.service.kmc;

import cn.com.jit.ida.IDAException;
import cn.com.jit.ida.Response;
import cn.com.jit.ida.ca.certmanager.CertException;
import cn.com.jit.ida.util.xml.XMLTool;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class KeyApplyResponse extends Response
{
  private static final String XMLTAG_PUBKEY = "pubKey";
  private byte[] pubKey;
  private EnvelopedData envelopedData;
  public static final String DES_KEY = "RSA";
  public static final String DES_ECB = "DES/ECB/PKCS7Padding";
  public static final String DES_CBC = "DES/CBC/PKCS7Padding";
  public static final String RC2_KEY = "RC2";
  public static final String RC2_ECB = "RC2/ECB/PKCS7Padding";
  public static final String RC2_CBC = "RC2/CBC/PKCS7Padding";
  public static final String RC4_KEY = "RC4";
  public static final String RC4 = "RC4";
  public static final String DES3_KEY = "DESede";
  public static final String DES3_ECB = "DESede/ECB/PKCS7Padding";
  public static final String DES3_CBC = "DESede/CBC/PKCS7Padding";
  public static final String CAST5_KEY = "CAST5";
  public static final String CAST5_ECB = "CAST5/ECB/PKCS7Padding";
  public static final String CAST5_CBC = "CAST5/CBC/PKCS7Padding";
  public static final String IDEA_KEY = "IDEA";
  public static final String IDEA_ECB = "IDEA/ECB/PKCS7Padding";
  public static final String IDEA_CBC = "IDEA/CBC/PKCS7Padding";
  public static final String SF33_KEY = "SF33";
  public static final String SF33_ECB = "SF33_ECB";
  public static final String SF33_CBC = "SF33_CBC";

  public KeyApplyResponse()
  {
    super.setOperation("APPLYKEY");
    this.envelopedData = new EnvelopedData();
  }

  public KeyApplyResponse(byte[] paramArrayOfByte)
    throws CertException
  {
    try
    {
      super.setData(paramArrayOfByte);
    }
    catch (Exception localException)
    {
      throw new CertException("0705", "其他错误 应答信息格式不合法", localException);
    }
  }

  public KeyApplyResponse(Response paramResponse)
    throws CertException
  {
    try
    {
      super.setData(paramResponse.getDocument());
    }
    catch (Exception localException)
    {
      throw new CertException("0705", "其他错误 应答信息格式不合法", localException);
    }
  }

  protected void updateBody(boolean paramBoolean)
    throws IDAException
  {
    Element localElement1;
    Object localObject;
    if (paramBoolean)
    {
      this.body = this.doc.createElement("body");
      localElement1 = this.doc.createElement("entry");
      localObject = XMLTool.newChildElement(localElement1, "pubKey", new String(this.pubKey));
      localElement1.appendChild((Node)localObject);
      localElement1.appendChild(this.envelopedData.getXMLOjbect());
      this.body.appendChild(localElement1);
    }
    else
    {
      localElement1 = XMLTool.getElementByTagName(this.body, "entry");
      localObject = XMLTool.getValueByTagName(localElement1, "pubKey");
      if (localObject != null)
        this.pubKey = ((String)localObject).getBytes();
      Element localElement2 = XMLTool.getElementByTagName(localElement1, "envelopedData");
      this.envelopedData = new EnvelopedData();
      this.envelopedData.setXMLObject(localElement2);
    }
  }

  public byte[] getPubKey()
  {
    return this.pubKey;
  }

  public void setPubKey(byte[] paramArrayOfByte)
  {
    this.pubKey = paramArrayOfByte;
  }

  public EnvelopedData getEnvelopedData()
  {
    return this.envelopedData;
  }

  public class EnvelopedData
  {
    Element xmlElement = null;
    private byte[] encryptedSessionKey;
    private String sessionKeyAlg;
    private byte[] encryptedPrivateKey;
    private String sessionKeyPad;
    private static final String XMLTAG_ENVELOPEDDATA = "envelopedData";
    private static final String XMLTAG_ENCRYPTEDSESSIONKEY = "encryptedSessionKey";
    private static final String XMLTAG_SESSIONKEYALG = "sessionKeyAlg";
    private static final String XMLTAG_SESSIOMKEYPAD = "sessionKeyPad";
    private static final String XMLTAG_ENCRYPTEDPRIVATEKEY = "encryptedPrivateKey";

    public EnvelopedData()
    {
    }

    public Element getXMLOjbect()
      throws IDAException
    {
      updateBody(true);
      return this.xmlElement;
    }

    final void setXMLObject(Element paramElement)
      throws IDAException
    {
      this.xmlElement = paramElement;
      updateBody(false);
    }

    public byte[] getEncryptedSessionKey()
    {
      return this.encryptedSessionKey;
    }

    public void setEncryptedSessionKey(byte[] paramArrayOfByte)
    {
      this.encryptedSessionKey = paramArrayOfByte;
    }

    public String getSessionKeyAlg()
    {
      return this.sessionKeyAlg;
    }

    public void setSessionKeyAlg(String paramString)
    {
      this.sessionKeyAlg = paramString;
    }

    public byte[] getEncryptedPrivateKey()
    {
      return this.encryptedPrivateKey;
    }

    public void setEncryptedPrivateKey(byte[] paramArrayOfByte)
    {
      this.encryptedPrivateKey = paramArrayOfByte;
    }

    public String getSessionKeyPad()
    {
      return this.sessionKeyPad;
    }

    public void setSessionKeyPad(String paramString)
    {
      this.sessionKeyPad = paramString;
    }

    protected void updateBody(boolean paramBoolean)
      throws IDAException
    {
      Object localObject;
      if (paramBoolean)
      {
        this.xmlElement = KeyApplyResponse.this.doc.createElement("envelopedData");
        if (this.encryptedSessionKey != null)
        {
          localObject = XMLTool.newChildElement(this.xmlElement, "encryptedSessionKey", new String(this.encryptedSessionKey));
          this.xmlElement.appendChild((Node)localObject);
        }
        if (getSessionKeyAlg() != null)
        {
          localObject = XMLTool.newChildElement(this.xmlElement, "sessionKeyAlg", getSessionKeyAlg());
          this.xmlElement.appendChild((Node)localObject);
        }
        if (getSessionKeyPad() != null)
        {
          localObject = XMLTool.newChildElement(this.xmlElement, "sessionKeyPad", getSessionKeyPad());
          this.xmlElement.appendChild((Node)localObject);
        }
        if (getEncryptedPrivateKey() != null)
        {
          localObject = XMLTool.newChildElement(this.xmlElement, "encryptedPrivateKey", new String(getEncryptedPrivateKey()));
          this.xmlElement.appendChild((Node)localObject);
        }
      }
      else
      {
        localObject = XMLTool.getValueByTagName(this.xmlElement, "encryptedSessionKey");
        if (localObject != null)
          this.encryptedSessionKey = ((String)localObject).getBytes();
        this.sessionKeyAlg = XMLTool.getValueByTagName(this.xmlElement, "sessionKeyAlg");
        this.sessionKeyPad = XMLTool.getValueByTagName(this.xmlElement, "sessionKeyPad");
        String str = XMLTool.getValueByTagName(this.xmlElement, "encryptedPrivateKey");
        if (str != null)
          this.encryptedPrivateKey = str.getBytes();
      }
    }
  }
}

/* Location:           C:\Program Files\JIT\CA50\lib\IDA\ida.jar
 * Qualified Name:     cn.com.jit.ida.ca.certmanager.service.kmc.KeyApplyResponse
 * JD-Core Version:    0.6.0
 */