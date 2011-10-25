package cn.com.jit.ida.ca.ctml.x509v3.extension.policy;

import cn.com.jit.ida.IDAException;
import cn.com.jit.ida.ca.ctml.util.CTMLException;
import cn.com.jit.ida.ca.ctml.util.CTMLException.CTML;
import cn.com.jit.ida.ca.ctml.x509v3.extension.X509V3ExtensionPolicy;
import cn.com.jit.ida.util.xml.XMLTool;
import java.util.Enumeration;
import java.util.Properties;
import java.util.Vector;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class ExtendKeyUsagePolicy extends X509V3ExtensionPolicy
{
  public static final String XMLTAG_STANDKEYUSAGE = "standardkeyusages";
  public static final String XMLTAG_USERKEYUSAGE = "userdefinekeyusages";
  public static final String XMLTAG_USERKEYUSAGE_ENTRY = "usageentry";
  public static final String XMLTAG_KEYUSAGE_OID = "oid";
  public static final String XMLTAG_KEYUSAGE_NAME = "name";
  public static final String XMLTAG_SERVERAUTH = "serverAuth";
  public static final String XMLTAG_CLIENTAUTH = "clientAuth";
  public static final String XMLTAG_CODESIGN = "codeSigning";
  public static final String XMLTAG_EMAIL = "emailProtection";
  public static final String XMLTAG_TIMESTAMP = "timeStamping";
  public static final String XMLTAG_OCSPSIGN = "ocspSinging";
  public static final String XMLTAG_SMARTCARDLOGON = "smartCardLogon";
  private static final Vector KEY_USAGES = new Vector();
  private Vector standardKeyUsages = new Vector();
  private Properties selfKeyUsages = new Properties();

  public ExtendKeyUsagePolicy()
    throws IDAException
  {
    super("ExtendKeyUsage");
  }

  public Vector getStandardKeyUsage()
  {
    Vector localVector = new Vector();
    localVector.addAll(this.standardKeyUsages);
    return localVector;
  }

  public void setStandardKeyUsage(Vector paramVector)
    throws IDAException
  {
    Enumeration localEnumeration = paramVector.elements();
    this.standardKeyUsages = new Vector();
    while (localEnumeration.hasMoreElements())
    {
      String str = (String)localEnumeration.nextElement();
      if (!KEY_USAGES.contains(str))
      {
        CTMLException localCTMLException = new CTMLException(CTMLException.CTML.INVALIDEXTKEYUSAGE);
        localCTMLException.appendMsg(str);
        throw localCTMLException;
      }
      this.standardKeyUsages.add(str);
    }
  }

  public void addStandardKeyUsage(String paramString)
    throws IDAException
  {
    if (!KEY_USAGES.contains(paramString))
    {
      CTMLException localCTMLException = new CTMLException(CTMLException.CTML.INVALIDEXTKEYUSAGE);
      localCTMLException.appendMsg(paramString);
      throw localCTMLException;
    }
    this.standardKeyUsages.add(paramString);
  }

  public Properties getSelfKeyUsage()
  {
    return this.selfKeyUsages;
  }

  public void setSelfKeyUsage(Properties paramProperties)
  {
    this.selfKeyUsages = paramProperties;
  }

  public void addSelfKeyUsage(String paramString1, String paramString2)
  {
    this.selfKeyUsages.setProperty(paramString1, paramString2);
  }

  protected void updateData(boolean paramBoolean)
    throws IDAException
  {
    super.updateData(paramBoolean);
    Object localObject5;
    Object localObject6;
    Object localObject7;
    String str;
    Object localObject8;
    if (paramBoolean)
    {
      localElement = this.xml.newElement("value");
      this.xmlElement.appendChild(localElement);
      localObject1 = this.xml.newElement("standardkeyusages");
      this.xmlElement.appendChild((Node)localObject1);
      localObject2 = this.standardKeyUsages.elements();
      while (((Enumeration)localObject2).hasMoreElements())
      {
        localObject3 = (String)((Enumeration)localObject2).nextElement();
        localObject5 = this.xml.newElement((String)localObject3);
        ((Element)localObject1).appendChild((Node)localObject5);
      }
      Object localObject3 = this.xml.newElement("userdefinekeyusages");
      this.xmlElement.appendChild((Node)localObject3);
      localObject2 = this.selfKeyUsages.propertyNames();
      while (((Enumeration)localObject2).hasMoreElements())
      {
        localObject5 = this.xml.newElement("usageentry");
        ((Element)localObject3).appendChild((Node)localObject5);
        localObject6 = (String)((Enumeration)localObject2).nextElement();
        localObject7 = this.xml.newElement("oid", (String)localObject6);
        ((Element)localObject5).appendChild((Node)localObject7);
        str = this.selfKeyUsages.getProperty((String)localObject6);
        localObject8 = this.xml.newElement("name", str);
        ((Element)localObject5).appendChild((Node)localObject8);
      }
    }
    this.standardKeyUsages = new Vector();
    this.selfKeyUsages = new Properties();
    Element localElement = XMLTool.getElementByTagName(this.xmlElement, "value");
    if (localElement == null)
    {
      localObject1 = new CTMLException(CTMLException.CTML.EXTKEYUSAGE_VALUEISNULL);
      throw ((Throwable)localObject1);
    }
    Object localObject1 = XMLTool.getElementByTagName(this.xmlElement, "standardkeyusages");
    if (localObject1 != null)
    {
      localObject2 = XMLTool.getChildElements((Element)localObject1);
      for (int i = 0; i < ((NodeList)localObject2).getLength(); i++)
      {
        localObject5 = ((NodeList)localObject2).item(i);
        localObject6 = ((Element)localObject5).getTagName();
        if (!KEY_USAGES.contains(localObject6))
        {
          localObject7 = new CTMLException(CTMLException.CTML.FOUND_INVALIDEXTKEYUSAGE);
          ((CTMLException)localObject7).appendMsg((String)localObject6);
          throw ((Throwable)localObject7);
        }
        this.standardKeyUsages.add(localObject6);
      }
    }
    Object localObject2 = XMLTool.getElementByTagName(this.xmlElement, "userdefinekeyusages");
    Object localObject4;
    if (localObject2 != null)
    {
      localObject4 = ((Element)localObject2).getElementsByTagName("usageentry");
      for (int j = 0; j < ((NodeList)localObject4).getLength(); j++)
      {
        localObject6 = (Element)((NodeList)localObject4).item(j);
        localObject7 = XMLTool.getValueByTagName((Element)localObject6, "oid");
        str = XMLTool.getValueByTagName((Element)localObject6, "name");
        if (localObject7 == null)
        {
          localObject8 = new CTMLException(CTMLException.CTML.EXTKEYUSAGE_OIDISNULL);
          throw ((Throwable)localObject8);
        }
        if (str == null)
        {
          localObject8 = new CTMLException(CTMLException.CTML.EXTKEYUSAGE_NAMEISNULL);
          throw ((Throwable)localObject8);
        }
        this.selfKeyUsages.setProperty((String)localObject7, str);
      }
    }
    if ((this.standardKeyUsages.size() == 0) && (this.selfKeyUsages.size() == 0))
    {
      localObject4 = new CTMLException(CTMLException.CTML.EXTKEYUSAGE_NOTFINDANY);
      throw ((Throwable)localObject4);
    }
  }

  static
  {
    KEY_USAGES.add("serverAuth");
    KEY_USAGES.add("clientAuth");
    KEY_USAGES.add("codeSigning");
    KEY_USAGES.add("emailProtection");
    KEY_USAGES.add("timeStamping");
    KEY_USAGES.add("ocspSinging");
    KEY_USAGES.add("smartCardLogon");
  }
}

/* Location:           C:\Program Files\JIT\CA50\lib\IDA\ida.jar
 * Qualified Name:     cn.com.jit.ida.ca.ctml.x509v3.extension.policy.ExtendKeyUsagePolicy
 * JD-Core Version:    0.6.0
 */