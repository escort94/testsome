package cn.com.jit.ida.ca.certmanager.service.kmc;

import cn.com.jit.ida.IDAException;
import cn.com.jit.ida.Request;
import cn.com.jit.ida.ca.certmanager.CertException;
import cn.com.jit.ida.util.xml.XMLTool;
import java.math.BigDecimal;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class KeyApplyRequest extends Request
{
  private CertInfo certInfo = null;
  private RequestKey requestKey = null;
  public static final String KMC_APPLYKEY = "APPLYKEY";
  public static final String RSA = "RSA";
  public static final String DSA = "DSA";

  public KeyApplyRequest()
  {
    super.setOperation("APPLYKEY");
    this.certInfo = new CertInfo();
    this.requestKey = new RequestKey();
  }

  public KeyApplyRequest(byte[] paramArrayOfByte)
    throws CertException
  {
    try
    {
      super.setData(paramArrayOfByte);
    }
    catch (Exception localException)
    {
      throw new CertException("0704", "其他错误 请求信息格式不合法", localException);
    }
  }

  public KeyApplyRequest(Request paramRequest)
    throws CertException
  {
    try
    {
      super.setData(paramRequest.getDocument());
    }
    catch (Exception localException)
    {
      throw new CertException("0704", "其他错误 请求信息格式不合法", localException);
    }
  }

  protected void updateBody(boolean paramBoolean)
    throws IDAException
  {
    Element localElement1;
    if (paramBoolean)
    {
      this.body = this.doc.createElement("body");
      localElement1 = this.doc.createElement("entry");
      localElement1.appendChild(this.requestKey.getXMLOjbect());
      localElement1.appendChild(this.certInfo.getXMLOjbect());
      this.body.appendChild(localElement1);
    }
    else
    {
      localElement1 = XMLTool.getElementByTagName(this.body, "entry");
      Element localElement2 = XMLTool.getElementByTagName(localElement1, "requestKey");
      this.requestKey = new RequestKey();
      this.requestKey.setXMLObject(localElement2);
      Element localElement3 = XMLTool.getElementByTagName(localElement1, "certInfo");
      this.certInfo = new CertInfo();
      this.certInfo.setXMLObject(localElement3);
    }
  }

  public CertInfo getCertInfo()
  {
    return this.certInfo;
  }

  public RequestKey getRequestKey()
  {
    return this.requestKey;
  }

  public class CertInfo
  {
    Element xmlElement = null;
    private String serialNumber;
    private String subjectName;
    private long notBefor;
    private long notAfter;
    private int validity;
    private String modelName;
    private byte[] pubkey;
    private String pubKeyAlgorithm;
    private String keyIdentity;
    private static final String XMLTAG_CERTINFO = "certInfo";
    private static final String XMLTAG_SERIALNUMBER = "serialNumber";
    private static final String XMLTAG_SUBJECTNAME = "subjectName";
    private static final String XMLTAG_NOTBEFORE = "notBefore";
    private static final String XMLTAG_NOTAFTER = "notAfter";
    private static final String XMLTAG_VALIDITY = "validity";
    private static final String XMLTAG_MODELNAME = "modelName";
    private static final String XMLTAG_PUBKEY = "pubkey";
    private static final String XMLTAG_PUBKEYALGORITHM = "pubKeyAlgorithm";
    private static final String XMLTAG_KEYIDENTITY = "keyIdentity";
    private String oldCertSN;
    private static final String XMLTAG_OLDCERTSN = "OLDCERTSN";

    public CertInfo()
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

    public String getSerialNumber()
    {
      return this.serialNumber;
    }

    public void setSerialNumber(String paramString)
    {
      this.serialNumber = paramString;
    }

    public String getSubjectName()
    {
      return this.subjectName;
    }

    public void setSubjectName(String paramString)
    {
      this.subjectName = paramString;
    }

    public long getNotBefor()
    {
      return this.notBefor;
    }

    public void setNotBefor(long paramLong)
    {
      this.notBefor = paramLong;
    }

    public long getNotAfter()
    {
      return this.notAfter;
    }

    public void setNotAfter(long paramLong)
    {
      this.notAfter = paramLong;
    }

    public int getValidity()
    {
      return this.validity;
    }

    public void setValidity(int paramInt)
    {
      this.validity = paramInt;
    }

    public String getModelName()
    {
      return this.modelName;
    }

    public void setModelName(String paramString)
    {
      this.modelName = paramString;
    }

    public byte[] getPubkey()
    {
      return this.pubkey;
    }

    public void setPubkey(byte[] paramArrayOfByte)
    {
      this.pubkey = paramArrayOfByte;
    }

    public String getPubKeyAlgorithm()
    {
      return this.pubKeyAlgorithm;
    }

    public void setPubKeyAlgorithm(String paramString)
    {
      this.pubKeyAlgorithm = paramString;
    }

    public String getKeyIdentity()
    {
      return this.keyIdentity;
    }

    public void setKeyIdentity(String paramString)
    {
      this.keyIdentity = paramString;
    }

    public String getOldCertSN()
    {
      return this.oldCertSN;
    }

    public void setOldCertSN(String paramString)
    {
      this.oldCertSN = paramString;
    }

    protected void updateBody(boolean paramBoolean)
      throws IDAException
    {
      if (paramBoolean)
      {
        this.xmlElement = KeyApplyRequest.this.doc.createElement("certInfo");
        Element localElement1 = XMLTool.newChildElement(this.xmlElement, "serialNumber", this.serialNumber);
        this.xmlElement.appendChild(localElement1);
        Element localElement2 = XMLTool.newChildElement(this.xmlElement, "subjectName", this.subjectName);
        this.xmlElement.appendChild(localElement2);
        if (this.notBefor != -1L)
        {
          localElement3 = XMLTool.newChildElement(this.xmlElement, "notBefore", new BigDecimal(this.notBefor).toString());
          this.xmlElement.appendChild(localElement3);
        }
        else
        {
          localElement3 = XMLTool.newChildElement(this.xmlElement, "notBefore", null);
          this.xmlElement.appendChild(localElement3);
        }
        if (this.notAfter != -1L)
        {
          localElement3 = XMLTool.newChildElement(this.xmlElement, "notAfter", new BigDecimal(this.notAfter).toString());
          this.xmlElement.appendChild(localElement3);
        }
        else
        {
          localElement3 = XMLTool.newChildElement(this.xmlElement, "notAfter", null);
          this.xmlElement.appendChild(localElement3);
        }
        if (this.validity != -1)
        {
          localElement3 = XMLTool.newChildElement(this.xmlElement, "validity", new BigDecimal(this.validity).toString());
          this.xmlElement.appendChild(localElement3);
        }
        else
        {
          localElement3 = XMLTool.newChildElement(this.xmlElement, "validity", null);
          this.xmlElement.appendChild(localElement3);
        }
        Element localElement3 = XMLTool.newChildElement(this.xmlElement, "modelName", this.modelName);
        this.xmlElement.appendChild(localElement3);
        Element localElement4 = XMLTool.newChildElement(this.xmlElement, "pubkey", new String(this.pubkey));
        this.xmlElement.appendChild(localElement4);
        Element localElement5 = XMLTool.newChildElement(this.xmlElement, "pubKeyAlgorithm", this.pubKeyAlgorithm);
        this.xmlElement.appendChild(localElement5);
        Element localElement6;
        if (this.keyIdentity != null)
        {
          localElement6 = XMLTool.newChildElement(this.xmlElement, "keyIdentity", this.keyIdentity.toString());
          this.xmlElement.appendChild(localElement6);
        }
        if (this.oldCertSN != null)
        {
          localElement6 = XMLTool.newChildElement(this.xmlElement, "OLDCERTSN", this.oldCertSN.toString());
          this.xmlElement.appendChild(localElement6);
        }
      }
      else
      {
        this.serialNumber = XMLTool.getValueByTagName(this.xmlElement, "serialNumber");
        this.subjectName = XMLTool.getValueByTagName(this.xmlElement, "subjectName");
        this.notBefor = new BigDecimal(XMLTool.getValueByTagName(this.xmlElement, "notBefore")).longValue();
        this.notAfter = new BigDecimal(XMLTool.getValueByTagName(this.xmlElement, "notAfter")).longValue();
        this.validity = new BigDecimal(XMLTool.getValueByTagName(this.xmlElement, "validity")).intValue();
        this.modelName = XMLTool.getValueByTagName(this.xmlElement, "modelName");
        this.pubkey = XMLTool.getValueByTagName(this.xmlElement, "pubkey").getBytes();
        this.pubKeyAlgorithm = XMLTool.getValueByTagName(this.xmlElement, "pubKeyAlgorithm");
        this.keyIdentity = XMLTool.getValueByTagName(this.xmlElement, "keyIdentity");
        this.oldCertSN = XMLTool.getValueByTagName(this.xmlElement, "OLDCERTSN");
      }
    }
  }

  public class RequestKey
  {
    Element xmlElement = null;
    private String type = null;
    private String length = null;
    private boolean isOnlyForCert = true;
    private static final String XMLTAG_REQUESTKEY = "requestKey";
    private static final String XMLTAG_TYPE = "type";
    private static final String XMLTAG_LENGTH = "length";
    private static final String XMLTAG_ISONLYFORCERT = "isOnlyForCert";
    private boolean retainKey = false;
    private static final String XMLTAG_RETAINKEY = "retainKey";

    public RequestKey()
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

    public String getType()
    {
      return this.type;
    }

    public void setType(String paramString)
    {
      this.type = paramString;
    }

    public String getLength()
    {
      return this.length;
    }

    public void setLength(String paramString)
    {
      this.length = paramString;
    }

    public boolean getIsOnlyForCert()
    {
      return this.isOnlyForCert;
    }

    public void setIsOnlyForCert(boolean paramBoolean)
    {
      this.isOnlyForCert = paramBoolean;
    }

    public boolean getRetainKey()
    {
      return this.retainKey;
    }

    public void setRetainKey(boolean paramBoolean)
    {
      this.retainKey = paramBoolean;
    }

    private final void updateBody(boolean paramBoolean)
      throws IDAException
    {
      if (paramBoolean)
      {
        this.xmlElement = KeyApplyRequest.this.doc.createElement("requestKey");
        Element localElement1 = XMLTool.newChildElement(this.xmlElement, "type", this.type);
        Element localElement2 = XMLTool.newChildElement(this.xmlElement, "length", this.length);
        Element localElement3 = XMLTool.newChildElement(this.xmlElement, "isOnlyForCert", new Boolean(this.isOnlyForCert).toString());
        Element localElement4 = XMLTool.newChildElement(this.xmlElement, "retainKey", new Boolean(this.retainKey).toString());
      }
      else
      {
        this.type = XMLTool.getValueByTagName(this.xmlElement, "type");
        this.length = XMLTool.getValueByTagName(this.xmlElement, "length");
        this.isOnlyForCert = new Boolean(XMLTool.getValueByTagName(this.xmlElement, "isOnlyForCert")).booleanValue();
        this.retainKey = new Boolean(XMLTool.getValueByTagName(this.xmlElement, "retainKey")).booleanValue();
      }
    }
  }
}

/* Location:           C:\Program Files\JIT\CA50\lib\IDA\ida.jar
 * Qualified Name:     cn.com.jit.ida.ca.certmanager.service.kmc.KeyApplyRequest
 * JD-Core Version:    0.6.0
 */