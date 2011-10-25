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

public class KeyUsagePolicy extends X509V3ExtensionPolicy
{
  private static final String XMLTAG_DIGISIGN = "digitalSignature";
  private static final String XMLTAG_NONREP = "nonRepudiation";
  private static final String XMLTAG_KEYENC = "keyEncipherment";
  private static final String XMLTAG_DATAENC = "dataEncipherment";
  private static final String XMLTAG_KEYAGREE = "keyAgreement";
  private static final String XMLTAG_KEYCERTSIGN = "keyCertSign";
  private static final String XMLTAG_CRLSIGN = "cRLSign";
  private static final String XMLTAG_ENCONLY = "encipherOnly";
  private static final String XMLTAG_DECONLY = "decipherOnly";
  private static final Vector KEY_USAGES = new Vector();
  private Vector keyUsages = new Vector();

  public KeyUsagePolicy()
    throws IDAException
  {
    super("KeyUsage");
  }

  public Vector getKeyUsage()
  {
    Vector localVector = new Vector();
    localVector.addAll(this.keyUsages);
    return localVector;
  }

  public void setKeyUsage(Vector paramVector)
    throws IDAException
  {
    Enumeration localEnumeration = paramVector.elements();
    this.keyUsages = new Vector();
    while (localEnumeration.hasMoreElements())
    {
      String str = (String)localEnumeration.nextElement();
      if (!KEY_USAGES.contains(str))
      {
        CTMLException localCTMLException = new CTMLException(CTMLException.CTML.INVALIDKEYUSAGE);
        localCTMLException.appendMsg(str + " not a keyusage");
        throw localCTMLException;
      }
      this.keyUsages.add(str);
    }
  }

  public void addKeyUsage(String paramString)
    throws IDAException
  {
    if (!KEY_USAGES.contains(paramString))
    {
      CTMLException localCTMLException = new CTMLException(CTMLException.CTML.INVALIDKEYUSAGE);
      localCTMLException.appendMsg(paramString + " not a keyusage");
      throw localCTMLException;
    }
    this.keyUsages.add(paramString);
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
      localObject1 = this.keyUsages.elements();
      while (((Enumeration)localObject1).hasMoreElements())
      {
        localObject2 = (String)((Enumeration)localObject1).nextElement();
        localObject3 = this.xml.newElement((String)localObject2);
        localElement.appendChild((Node)localObject3);
      }
    }
    this.keyUsages = new Vector();
    Element localElement = XMLTool.getElementByTagName(this.xmlElement, "value");
    if (localElement == null)
    {
      localObject1 = new CTMLException(CTMLException.CTML.KEYUSAGE_VALUEISNULL);
      throw ((Throwable)localObject1);
    }
    Object localObject1 = XMLTool.getChildElements(localElement);
    if (((NodeList)localObject1).getLength() == 0)
    {
      localObject2 = new CTMLException(CTMLException.CTML.KEYUSAGE_NOTFINDANY);
      throw ((Throwable)localObject2);
    }
    for (int i = 0; i < ((NodeList)localObject1).getLength(); i++)
    {
      localObject3 = ((NodeList)localObject1).item(i);
      String str = ((Element)localObject3).getTagName();
      if (!KEY_USAGES.contains(str))
      {
        CTMLException localCTMLException = new CTMLException(CTMLException.CTML.FOUND_INVALIDKEYUSAGE);
        localCTMLException.appendMsg(str);
        throw localCTMLException;
      }
      this.keyUsages.add(str);
    }
  }

  static
  {
    KEY_USAGES.add("digitalSignature");
    KEY_USAGES.add("nonRepudiation");
    KEY_USAGES.add("keyEncipherment");
    KEY_USAGES.add("dataEncipherment");
    KEY_USAGES.add("keyAgreement");
    KEY_USAGES.add("keyCertSign");
    KEY_USAGES.add("cRLSign");
    KEY_USAGES.add("encipherOnly");
    KEY_USAGES.add("decipherOnly");
  }
}

/* Location:           C:\Program Files\JIT\CA50\lib\IDA\ida.jar
 * Qualified Name:     cn.com.jit.ida.ca.ctml.x509v3.extension.policy.KeyUsagePolicy
 * JD-Core Version:    0.6.0
 */