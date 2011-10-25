package cn.com.jit.ida.ca.certmanager.service.kmc;

import cn.com.jit.ida.IDAException;
import cn.com.jit.ida.Request;
import cn.com.jit.ida.ca.certmanager.CertException;
import cn.com.jit.ida.util.xml.XMLTool;
import java.util.Vector;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class KMCCertArcRequest extends Request
{
  public static final String KMC_CERTARC = "KMCCERTARC";
  public static final String XMLTAG_CERTSN = "certSN";
  private Vector vecCertSN;

  public KMCCertArcRequest()
  {
    super.setOperation("KMCCERTARC");
    this.vecCertSN = new Vector();
  }

  public KMCCertArcRequest(byte[] paramArrayOfByte)
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

  public KMCCertArcRequest(Request paramRequest)
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

  protected final void updateBody(boolean paramBoolean)
    throws IDAException
  {
    Object localObject;
    String str;
    int i;
    if (paramBoolean)
    {
      this.body = this.doc.createElement("body");
      localObject = XMLTool.newChildElement(this.body, "entry");
      if ((this.vecCertSN != null) && (this.vecCertSN.size() > 0))
      {
        str = null;
        for (i = 0; i < this.vecCertSN.size(); i++)
        {
          str = (String)this.vecCertSN.get(i);
          XMLTool.newChildElement((Element)localObject, "certSN", str);
        }
      }
    }
    else
    {
      localObject = this.body.getElementsByTagName("entry");
      for (i = 0; i < ((NodeList)localObject).getLength(); i++)
      {
        Element localElement = (Element)((NodeList)localObject).item(i);
        str = XMLTool.getValueByTagName(localElement, "certSN");
        this.vecCertSN.add(str);
      }
    }
  }

  public Vector getVecCertSN()
  {
    return this.vecCertSN;
  }

  public void setVecCertSN(Vector paramVector)
  {
    this.vecCertSN = paramVector;
  }
}

/* Location:           C:\Program Files\JIT\CA50\lib\IDA\ida.jar
 * Qualified Name:     cn.com.jit.ida.ca.certmanager.service.kmc.KMCCertArcRequest
 * JD-Core Version:    0.6.0
 */