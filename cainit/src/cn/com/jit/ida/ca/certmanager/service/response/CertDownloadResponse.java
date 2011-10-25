package cn.com.jit.ida.ca.certmanager.service.response;

import cn.com.jit.ida.IDAException;
import cn.com.jit.ida.Response;
import cn.com.jit.ida.ca.certmanager.CertException;
import cn.com.jit.ida.util.xml.XMLTool;
import java.io.PrintStream;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class CertDownloadResponse extends Response
{
  private String p7b;
  private String pfx;
  private String useKMC;
  private String encryptedSessionKey;
  private String sessionKeyAlg;
  private String sessionKeyPad;
  private String encryptedPrivateKey;
  private String certSN;

  public CertDownloadResponse()
  {
    this.p7b = null;
    this.pfx = null;
    this.useKMC = "FALSE";
    this.encryptedSessionKey = null;
    this.sessionKeyAlg = null;
    this.sessionKeyPad = null;
    this.encryptedPrivateKey = null;
    this.certSN = null;
    super.setOperation("CERTDOWNLOAD");
  }

  public CertDownloadResponse(byte[] paramArrayOfByte)
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

  public CertDownloadResponse(Response paramResponse)
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
    Object localObject;
    Element localElement2;
    if (paramBoolean)
    {
      this.body = this.doc.createElement("body");
      localObject = this.doc.createElement("entry");
      this.body.appendChild((Node)localObject);
      Element localElement1 = XMLTool.newElement(this.doc, "certSN", this.certSN);
      ((Element)localObject).appendChild(localElement1);
      localElement2 = XMLTool.newElement(this.doc, "p7b", this.p7b);
      ((Element)localObject).appendChild(localElement2);
      Element localElement3 = XMLTool.newElement(this.doc, "pfx", this.pfx);
      ((Element)localObject).appendChild(localElement3);
      Element localElement4 = XMLTool.newElement(this.doc, "useKmc", this.useKMC);
      ((Element)localObject).appendChild(localElement4);
      Element localElement5 = XMLTool.newElement(this.doc, "enryptedSessionKey", this.encryptedSessionKey);
      ((Element)localObject).appendChild(localElement5);
      Element localElement6 = XMLTool.newElement(this.doc, "sessionKeyAlg", this.sessionKeyAlg);
      ((Element)localObject).appendChild(localElement6);
      Element localElement7 = XMLTool.newElement(this.doc, "sessionKeyPad", this.sessionKeyPad);
      ((Element)localObject).appendChild(localElement7);
      Element localElement8 = XMLTool.newElement(this.doc, "encryptedPrivateKey", this.encryptedPrivateKey);
      ((Element)localObject).appendChild(localElement8);
    }
    else
    {
      localObject = this.body.getElementsByTagName("entry");
      int i = ((NodeList)localObject).getLength();
      if (i == 0)
        return;
      localElement2 = (Element)((NodeList)localObject).item(0);
      this.certSN = XMLTool.getValueByTagName(localElement2, "certSN");
      this.p7b = XMLTool.getValueByTagName(localElement2, "p7b");
      this.pfx = XMLTool.getValueByTagName(localElement2, "pfx");
      this.useKMC = XMLTool.getValueByTagName(localElement2, "useKmc");
      this.encryptedSessionKey = XMLTool.getValueByTagName(localElement2, "enryptedSessionKey");
      this.sessionKeyAlg = XMLTool.getValueByTagName(localElement2, "sessionKeyAlg");
      this.sessionKeyPad = XMLTool.getValueByTagName(localElement2, "sessionKeyPad");
      this.encryptedPrivateKey = XMLTool.getValueByTagName(localElement2, "encryptedPrivateKey");
    }
  }

  public String getP7b()
  {
    return this.p7b;
  }

  public void setP7b(String paramString)
  {
    this.p7b = paramString;
  }

  public String getPfx()
  {
    return this.pfx;
  }

  public void setPfx(String paramString)
  {
    this.pfx = paramString;
  }

  public String getEncryptedSessionKey()
  {
    return this.encryptedSessionKey;
  }

  public String getEncryptedPrivateKey()
  {
    return this.encryptedPrivateKey;
  }

  public void setEncryptedSessionKey(String paramString)
  {
    this.encryptedSessionKey = paramString;
  }

  public void setEncryptedPrivateKey(String paramString)
  {
    this.encryptedPrivateKey = paramString;
  }

  public void setSessionKeyAlg(String paramString)
  {
    this.sessionKeyAlg = paramString;
  }

  public void setSessionKeyPad(String paramString)
  {
    this.sessionKeyPad = paramString;
  }

  public void setUseKMC(String paramString)
  {
    this.useKMC = paramString;
  }

  public String getUseKMC()
  {
    return this.useKMC;
  }

  public String getSessionKeyPad()
  {
    return this.sessionKeyPad;
  }

  public String getSessionKeyAlg()
  {
    return this.sessionKeyAlg;
  }

  public static void main(String[] paramArrayOfString)
  {
    CertDownloadResponse localCertDownloadResponse = new CertDownloadResponse();
    localCertDownloadResponse.setCertSN("123454");
    localCertDownloadResponse.setP7b("P7BTEST");
    localCertDownloadResponse.setPfx("PFXTest");
    try
    {
      byte[] arrayOfByte = localCertDownloadResponse.getData();
      System.out.println(new String(arrayOfByte));
    }
    catch (Exception localException)
    {
      System.out.println(localException.toString());
    }
  }

  public String getCertSN()
  {
    return this.certSN;
  }

  public void setCertSN(String paramString)
  {
    this.certSN = paramString;
  }
}

/* Location:           C:\Program Files\JIT\CA50\lib\IDA\ida.jar
 * Qualified Name:     cn.com.jit.ida.ca.certmanager.service.response.CertDownloadResponse
 * JD-Core Version:    0.6.0
 */