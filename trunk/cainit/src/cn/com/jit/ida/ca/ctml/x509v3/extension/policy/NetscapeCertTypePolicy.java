package cn.com.jit.ida.ca.ctml.x509v3.extension.policy;

import cn.com.jit.ida.IDAException;
import cn.com.jit.ida.ca.ctml.util.CTMLException;
import cn.com.jit.ida.ca.ctml.util.CTMLException.CTML;
import cn.com.jit.ida.ca.ctml.x509v3.extension.X509V3ExtensionPolicy;
import cn.com.jit.ida.util.xml.XMLTool;
import java.util.Enumeration;
import java.util.Vector;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class NetscapeCertTypePolicy extends X509V3ExtensionPolicy
{
  private static final String XMLTAG_SSLCLIENT = "SSLclient";
  private static final String XMLTAG_SSLSERVER = "SSLserver";
  private static final String XMLTAG_SMIME = "SMIME";
  private static final String XMLTAG_OBJSIGN = "ObjectSigning";
  private static final String XMLTAG_SSLCA = "SSLCA";
  private static final String XMLTAG_SMIMECA = "SMIMECA";
  private static final String XMLTAG_OBJSIGNCA = "ObjectSigningCA";
  private static final Vector CERTTYPES = new Vector();
  private Vector certTypeList = new Vector();

  public NetscapeCertTypePolicy()
    throws IDAException
  {
    super("NetscapeCertType");
  }

  public Vector getCertType()
  {
    Vector localVector = new Vector();
    localVector.addAll(this.certTypeList);
    return localVector;
  }

  public void setCertType(Vector paramVector)
    throws IDAException
  {
    Enumeration localEnumeration = paramVector.elements();
    this.certTypeList = new Vector();
    while (localEnumeration.hasMoreElements())
    {
      String str = (String)localEnumeration.nextElement();
      if (!CERTTYPES.contains(str))
      {
        CTMLException localCTMLException = new CTMLException(CTMLException.CTML.INVALIDCERTTYPE);
        localCTMLException.appendMsg(str);
        throw localCTMLException;
      }
      this.certTypeList.add(str);
    }
  }

  public void addCertType(String paramString)
    throws IDAException
  {
    if (!CERTTYPES.contains(paramString))
    {
      CTMLException localCTMLException = new CTMLException(CTMLException.CTML.INVALIDCERTTYPE);
      localCTMLException.appendMsg(paramString);
      throw localCTMLException;
    }
    this.certTypeList.add(paramString);
  }

  protected void updateData(boolean paramBoolean)
    throws IDAException
  {
    super.updateData(paramBoolean);
    Object localObject2;
    Object localObject3;
    if (paramBoolean)
    {
      localElement = this.xml.newElement("value");
      this.xmlElement.appendChild(localElement);
      localObject1 = this.certTypeList.elements();
      while (((Enumeration)localObject1).hasMoreElements())
      {
        localObject2 = (String)((Enumeration)localObject1).nextElement();
        localObject3 = this.xml.newElement((String)localObject2);
        localElement.appendChild((Node)localObject3);
      }
    }
    this.certTypeList = new Vector();
    Element localElement = XMLTool.getElementByTagName(this.xmlElement, "value");
    if (localElement == null)
    {
      localObject1 = new CTMLException(CTMLException.CTML.CERTTYPE_VALUEISNULL);
      throw ((Throwable)localObject1);
    }
    Object localObject1 = XMLTool.getChildElements(localElement);
    if (((NodeList)localObject1).getLength() == 0)
    {
      localObject2 = new CTMLException(CTMLException.CTML.CERTTYPE_NOTFINDANY);
      throw ((Throwable)localObject2);
    }
    for (int i = 0; i < ((NodeList)localObject1).getLength(); i++)
    {
      localObject3 = ((NodeList)localObject1).item(i);
      String str = ((Element)localObject3).getTagName();
      if (!CERTTYPES.contains(str))
      {
        CTMLException localCTMLException = new CTMLException(CTMLException.CTML.FOUND_INVALIDCERTTYPE);
        localCTMLException.appendMsg(str);
        throw localCTMLException;
      }
      this.certTypeList.add(str);
    }
  }

  static
  {
    CERTTYPES.add("SSLclient");
    CERTTYPES.add("SSLserver");
    CERTTYPES.add("SMIME");
    CERTTYPES.add("ObjectSigning");
    CERTTYPES.add("SSLCA");
    CERTTYPES.add("SMIMECA");
    CERTTYPES.add("ObjectSigningCA");
  }
}

/* Location:           C:\Program Files\JIT\CA50\lib\IDA\ida.jar
 * Qualified Name:     cn.com.jit.ida.ca.ctml.x509v3.extension.policy.NetscapeCertTypePolicy
 * JD-Core Version:    0.6.0
 */