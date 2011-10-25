package cn.com.jit.ida.ca.certmanager.service.request;

import cn.com.jit.ida.Request;
import cn.com.jit.ida.ca.certmanager.CertException;
import cn.com.jit.ida.util.xml.XMLTool;
import java.util.Properties;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class CertUpdateQueryRequest extends Request
{
  private String certSN = null;
  private Properties prop = null;

  public CertUpdateQueryRequest()
  {
    super.setOperation("CERTUPDATEQUERY");
  }

  public CertUpdateQueryRequest(Request paramRequest)
    throws CertException
  {
    try
    {
      super.setData(paramRequest.getDocument());
    }
    catch (Exception localException)
    {
      localException.printStackTrace();
    }
  }

  public CertUpdateQueryRequest(byte[] paramArrayOfByte)
    throws CertException
  {
    try
    {
      super.setData(paramArrayOfByte);
    }
    catch (Exception localException)
    {
      localException.printStackTrace();
    }
  }

  public void setCertSN(String paramString)
  {
    this.certSN = paramString;
  }

  public String getCertSN()
  {
    return this.certSN;
  }

  public Properties getCertInfoProperties()
  {
    return this.prop;
  }

  public final void updateBody(boolean paramBoolean)
    throws CertException
  {
    if (paramBoolean)
    {
      this.body = this.doc.createElement("body");
      Element localElement = XMLTool.newChildElement(this.body, "entry");
      if (this.certSN != null)
        XMLTool.newChildElement(localElement, "certSN", this.certSN);
    }
    else
    {
      this.certSN = XMLTool.getValueByTagName(this.body, "certSN");
      if (this.certSN != null)
        this.prop.put("certSN", this.certSN);
    }
  }
}

/* Location:           C:\Program Files\JIT\CA50\lib\IDA\ida.jar
 * Qualified Name:     cn.com.jit.ida.ca.certmanager.service.request.CertUpdateQueryRequest
 * JD-Core Version:    0.6.0
 */