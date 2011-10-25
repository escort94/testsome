package cn.com.jit.ida.ca.certmanager.service.request;

import cn.com.jit.ida.IDAException;
import cn.com.jit.ida.Request;
import cn.com.jit.ida.ca.certmanager.CertException;
import cn.com.jit.ida.util.pki.encoders.Base64;
import cn.com.jit.ida.util.xml.XMLTool;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class CAKeyRecoverRequest extends Request
{
  private static final String XMLTAG_APPLYCADN = "appCaDN";
  private static final String XMLTAG_CERTSERIALNUMBER = "certSerialNumber";
  private static final String XMLTAG_PRIMARYKEY = "primaryKey";
  private static final String XMLTAG_KEYSTORETYPE = "keyStoreType";
  private static final String XMLTAG_PASSWD = "passwd";
  private static final String XMLTAG_CERTIFICATE = "certificate";
  private static final String XMLTAG_PUBKEY = "pubkey";
  private static final String XMLTAG_CERTSUBJECT = "certsubject";
  private static final String XMLTAG_COMPARETIME = "comparetime";
  private static final String XMLTAG_KEYID = "keyID";
  private static final String XMLTAG_CTMLNAME = "ctmlname";
  private String certSerialNumber;
  private String primaryKey;
  private String keyStoreType;
  private String passwd;
  private byte[] certificate;
  private byte[] pubkey;
  private String certSubject;
  private String comparetime;
  private String keyID;
  private String ctmlname;

  public CAKeyRecoverRequest()
  {
    super.setOperation("CERTCARECOVERKEY");
  }

  public CAKeyRecoverRequest(Request paramRequest)
    throws CertException
  {
    try
    {
      super.setData(paramRequest.getDocument());
    }
    catch (Exception localException)
    {
      throw new CertException("81140704", "密钥恢复服务其他错误 请求信息格式不合法", localException);
    }
  }

  public CAKeyRecoverRequest(byte[] paramArrayOfByte)
    throws CertException
  {
    try
    {
      super.setData(paramArrayOfByte);
    }
    catch (Exception localException)
    {
      throw new CertException("81140704", "密钥恢复服务其他错误 请求信息格式不合法", localException);
    }
  }

  public String getCertSerialNumbe()
  {
    return this.certSerialNumber;
  }

  public void setCertSerialNumber(String paramString)
  {
    this.certSerialNumber = paramString;
  }

  public String getPrimaryKey()
  {
    return this.primaryKey;
  }

  public void setPrimaryKey(String paramString)
  {
    this.primaryKey = paramString;
  }

  public String getKeyStoreType()
  {
    return this.keyStoreType;
  }

  public void setKeyStoreType(String paramString)
  {
    this.keyStoreType = paramString;
  }

  public String getPasswd()
  {
    return this.passwd;
  }

  public void setPasswd(String paramString)
  {
    this.passwd = paramString;
  }

  public byte[] getCertificate()
  {
    return this.certificate;
  }

  public void setCertificate(byte[] paramArrayOfByte)
  {
    this.certificate = paramArrayOfByte;
  }

  public void setPubkey(byte[] paramArrayOfByte)
  {
    this.pubkey = paramArrayOfByte;
  }

  public void setCertSubject(String paramString)
  {
    this.certSubject = paramString;
  }

  public void setComparetime(String paramString)
  {
    this.comparetime = paramString;
  }

  public void setKeyID(String paramString)
  {
    this.keyID = paramString;
  }

  public void setCtmlname(String paramString)
  {
    this.ctmlname = paramString;
  }

  public byte[] getPubkey()
  {
    return this.pubkey;
  }

  public String getCertSubject()
  {
    return this.certSubject;
  }

  public String getComparetime()
  {
    return this.comparetime;
  }

  public String getKeyID()
  {
    return this.keyID;
  }

  public String getCtmlname()
  {
    return this.ctmlname;
  }

  protected final void updateBody(boolean paramBoolean)
    throws IDAException
  {
    Element localElement;
    if (paramBoolean)
    {
      this.body = this.doc.createElement("body");
      localElement = XMLTool.newChildElement(this.body, "entry");
      if (this.certSerialNumber != null)
        XMLTool.newChildElement(localElement, "certSerialNumber", this.certSerialNumber);
      if (this.primaryKey != null)
        XMLTool.newChildElement(localElement, "primaryKey", this.primaryKey);
      XMLTool.newChildElement(localElement, "keyStoreType", this.keyStoreType);
      XMLTool.newChildElement(localElement, "passwd", this.passwd);
      if (this.certificate != null)
        XMLTool.newChildElement(localElement, "certificate", new String(Base64.encode(this.certificate)));
      if (this.pubkey != null)
        XMLTool.newChildElement(localElement, "pubkey", new String(this.pubkey));
      if (this.certSubject != null)
        XMLTool.newChildElement(localElement, "certsubject", new String(this.certSubject));
      if (this.comparetime != null)
        XMLTool.newChildElement(localElement, "comparetime", new String(this.comparetime));
      if (this.keyID != null)
        XMLTool.newChildElement(localElement, "keyID", new String(this.keyID));
      if (this.ctmlname != null)
        XMLTool.newChildElement(localElement, "ctmlname", new String(this.ctmlname));
    }
    else
    {
      localElement = XMLTool.getElementByTagName(this.body, "entry");
      this.certSerialNumber = XMLTool.getValueByTagName(localElement, "certSerialNumber");
      this.primaryKey = XMLTool.getValueByTagName(localElement, "primaryKey");
      this.keyStoreType = XMLTool.getValueByTagName(localElement, "keyStoreType");
      this.passwd = XMLTool.getValueByTagName(localElement, "passwd");
      if (XMLTool.getValueByTagName(localElement, "certificate") != null)
        this.certificate = Base64.decode(XMLTool.getValueByTagName(localElement, "certificate").getBytes());
      if (XMLTool.getValueByTagName(localElement, "pubkey") != null)
        this.pubkey = XMLTool.getValueByTagName(localElement, "pubkey").getBytes();
      if (XMLTool.getValueByTagName(localElement, "certsubject") != null)
        this.certSubject = XMLTool.getValueByTagName(localElement, "certsubject");
      if (XMLTool.getValueByTagName(localElement, "comparetime") != null)
        this.comparetime = XMLTool.getValueByTagName(localElement, "comparetime");
      if (XMLTool.getValueByTagName(localElement, "keyID") != null)
        this.keyID = XMLTool.getValueByTagName(localElement, "keyID");
      if (XMLTool.getValueByTagName(localElement, "ctmlname") != null)
        this.ctmlname = XMLTool.getValueByTagName(localElement, "ctmlname");
    }
  }

  public static void main(String[] paramArrayOfString)
  {
  }

  public static class KEYSTORETYPE
  {
    public static final String PKCS1 = "PKCS#1";
    public static final String PKCS8 = "PKCS#8";
    public static final String PKCS11 = "PKCS#11";
    public static final String PKCS12 = "PKCS#12";
  }
}

/* Location:           C:\Program Files\JIT\CA50\lib\IDA\ida.jar
 * Qualified Name:     cn.com.jit.ida.ca.certmanager.service.request.CAKeyRecoverRequest
 * JD-Core Version:    0.6.0
 */