package cn.com.jit.ida.ca.certmanager.service.kmc;

import cn.com.jit.ida.Request;
import cn.com.jit.ida.ca.certmanager.CertException;
import cn.com.jit.ida.util.xml.XMLTool;
import java.io.PrintStream;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class KeyStateTrackRequest extends Request
{
  private static final String PROTOCOL_CERTSN = "certsn";
  private static final String PROTOCOL_KEYSTATE = "keystate";
  private static final String KMC_KEYSTATETRACK = "KEYSTATETRACK";
  private String certsn = null;
  private String keystate = null;

  public KeyStateTrackRequest()
  {
    super.setOperation("KEYSTATETRACK");
  }

  public KeyStateTrackRequest(Request paramRequest)
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

  public KeyStateTrackRequest(byte[] paramArrayOfByte)
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

  public final void updateBody(boolean paramBoolean)
    throws CertException
  {
    if (paramBoolean)
    {
      this.body = this.doc.createElement("body");
      Element localElement1 = this.doc.createElement("entry");
      this.body.appendChild(localElement1);
      Element localElement2;
      if (this.certsn != null)
      {
        localElement2 = XMLTool.newElement(this.doc, "certsn", this.certsn);
        localElement1.appendChild(localElement2);
      }
      if (this.keystate != null)
      {
        localElement2 = XMLTool.newElement(this.doc, "keystate", this.keystate);
        localElement1.appendChild(localElement2);
      }
    }
    else
    {
      this.certsn = XMLTool.getValueByTagName(this.body, "certsn");
      this.keystate = XMLTool.getValueByTagName(this.body, "keystate");
    }
  }

  public static void main(String[] paramArrayOfString)
  {
    KeyStateTrackRequest localKeyStateTrackRequest = new KeyStateTrackRequest();
    try
    {
      byte[] arrayOfByte = localKeyStateTrackRequest.getData();
      System.out.println(new String(arrayOfByte));
    }
    catch (Exception localException)
    {
      System.out.println(localException.toString());
    }
  }

  public String getCertsn()
  {
    return this.certsn;
  }

  public String getKeystate()
  {
    return this.keystate;
  }

  public void setCertsn(String paramString)
  {
    this.certsn = paramString;
  }

  public void setKeystate(String paramString)
  {
    this.keystate = paramString;
  }
}

/* Location:           C:\Program Files\JIT\CA50\lib\IDA\ida.jar
 * Qualified Name:     cn.com.jit.ida.ca.certmanager.service.kmc.KeyStateTrackRequest
 * JD-Core Version:    0.6.0
 */