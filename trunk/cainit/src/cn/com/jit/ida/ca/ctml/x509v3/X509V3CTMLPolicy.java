package cn.com.jit.ida.ca.ctml.x509v3;

import cn.com.jit.ida.IDAException;
import cn.com.jit.ida.ca.ctml.CTMLPolicy;
import cn.com.jit.ida.ca.ctml.util.CTMLConstant.ISSUE_MEDIUM;
import cn.com.jit.ida.ca.ctml.util.CTMLConstant.KEY.GENPLACE;
import cn.com.jit.ida.ca.ctml.util.CTMLConstant.KEY.TYPE;
import cn.com.jit.ida.ca.ctml.util.CTMLException;
import cn.com.jit.ida.ca.ctml.util.CTMLException.CTML;
import cn.com.jit.ida.ca.ctml.x509v3.extension.X509V3ExtensionPolicy;
import cn.com.jit.ida.ca.ctml.x509v3.extension.policy.KeyUsagePolicy;
import cn.com.jit.ida.util.xml.XMLTool;
import java.io.PrintStream;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class X509V3CTMLPolicy extends CTMLPolicy
{
  private static final String XMLTAG_BASEDESC = "basedescription";
  private static final String XMLTAG_EXTENSIONDESC = "extensiondescription";
  private static final String XMLTAG_STANDEXT = "standardextension";
  private static final String XMLTAG_SELFEXT = "selfextension";
  private static final String XMLTAG_VALIDITYPOLICY = "validitypolicy";
  private static final String XMLTAG_CRLPOLICY = "crlpolicy";
  private static final String XMLTAG_ISSUEPOLICY = "issuepolicy";
  private static final String XMLTAG_KEYSPECPOLICY = "keyspecpolicy";
  private static final String XMLTAG_UPDATEREPLACEPOLICY = "updatereplacepolicy";
  private static final String XMLTAG_UPDTRANSPERIODPOLICY = "updtransperiodpolicy";
  private static final String XMLTAG_KEYPOLICY = "keypolicy";
  private static final String XMLTAG_SELFEXTENTRY = "userDefineExtension";
  private static final String XMLTAG_ATTRIBUTE = "attribute";
  private ValidityPolicy validityPolicy = new ValidityPolicy();
  private KeyPolicy keyPolicy = new KeyPolicy();
  private IssuePolicy issuePolicy = new IssuePolicy();
  private KeySpecPolicy keySpecPolicy = new KeySpecPolicy();
  private UpdateReplacePolicy updateReplacePolicy = new UpdateReplacePolicy();
  private UpdTransPeriodPolicy updTransPeriodPolicy = new UpdTransPeriodPolicy();
  private Hashtable standardExtPolicys = new Hashtable();
  private Vector selfExtPolicys = new Vector();
  private Attribute attribute = new Attribute();
  private Element standExtensionElement;
  private Element selfExtensionElement;
  public static final int DEFAULT_VALIDITY = 365;
  public static final int DEFAULT_MAXVALIDITY = 3650;
  public static final long DEFAULT_NOTAFTER = 20080909000000000L;
  private static final String KEYTYPE = "RSA";
  private static final int KEYLENGTH = 1024;
  private static final String GENPLACE = "LOCAL";
  private static final boolean ISSUESTATUS = true;
  private static final String KEYSPEC = "2";
  private static final String UPDATEREPLACE = "false";
  private static final Vector ISSUEMEDIUMS = new Vector();

  public static void main(String[] paramArrayOfString)
    throws Exception
  {
    X509V3CTMLPolicy localX509V3CTMLPolicy1 = new X509V3CTMLPolicy();
    Hashtable localHashtable = new Hashtable();
    KeyUsagePolicy localKeyUsagePolicy = new KeyUsagePolicy();
    localKeyUsagePolicy.addKeyUsage("digitalSignature");
    localKeyUsagePolicy.addKeyUsage("keyAgreement");
    localKeyUsagePolicy.addKeyUsage("dataEncipherment");
    localHashtable.put("KeyUsage", localKeyUsagePolicy);
    localX509V3CTMLPolicy1.setStandardExtensionPolicys(localHashtable);
    byte[] arrayOfByte1 = localX509V3CTMLPolicy1.getCTMLPolicyDesc();
    System.out.println(new String(arrayOfByte1));
    System.out.println("\nok\n");
    X509V3CTMLPolicy localX509V3CTMLPolicy2 = new X509V3CTMLPolicy(arrayOfByte1);
    byte[] arrayOfByte2 = localX509V3CTMLPolicy2.getCTMLPolicyDesc();
    System.out.println(new String(arrayOfByte2));
  }

  public X509V3CTMLPolicy()
    throws IDAException
  {
  }

  public X509V3CTMLPolicy(byte[] paramArrayOfByte)
    throws IDAException
  {
    this();
    setCTMLPolicyDesc(paramArrayOfByte);
  }

  public void modifyCTMLPolicy(CTMLPolicy paramCTMLPolicy)
  {
    X509V3CTMLPolicy localX509V3CTMLPolicy = (X509V3CTMLPolicy)paramCTMLPolicy;
    this.issuePolicy.isIssue = localX509V3CTMLPolicy.getIssuePolicy().isIssue;
    this.issuePolicy.issueMedium = localX509V3CTMLPolicy.getIssuePolicy().issueMedium;
    this.keySpecPolicy.keySpec = localX509V3CTMLPolicy.getKeySpecPolicy().keySpec;
    this.updateReplacePolicy.updateReplace = localX509V3CTMLPolicy.getUpdateReplacePolicy().updateReplace;
    this.updTransPeriodPolicy.updTransPeriod = localX509V3CTMLPolicy.getUpdTransPeriodPolicy().updTransPeriod;
  }

  public ValidityPolicy getValidityPolicy()
  {
    return this.validityPolicy;
  }

  public KeyPolicy getKeyPolicy()
  {
    return this.keyPolicy;
  }

  public IssuePolicy getIssuePolicy()
  {
    return this.issuePolicy;
  }

  public Attribute getAttribute()
  {
    return this.attribute;
  }

  public KeySpecPolicy getKeySpecPolicy()
  {
    return this.keySpecPolicy;
  }

  public UpdateReplacePolicy getUpdateReplacePolicy()
  {
    return this.updateReplacePolicy;
  }

  public UpdTransPeriodPolicy getUpdTransPeriodPolicy()
  {
    return this.updTransPeriodPolicy;
  }

  public void setStandardExtensionPolicys(Hashtable paramHashtable)
  {
    this.standardExtPolicys = paramHashtable;
  }

  public Hashtable getStandardExtensionPolicys()
  {
    return this.standardExtPolicys;
  }

  public Vector getSelfExtensionPolicys()
  {
    return this.selfExtPolicys;
  }

  public void setSelfExtensionPolicys(Vector paramVector)
  {
    this.selfExtPolicys = paramVector;
  }

  protected void updateData(boolean paramBoolean)
    throws IDAException
  {
    if (paramBoolean)
      serializeXML();
    else
      parseXML();
  }

  private Element getElement(Element paramElement, String paramString)
    throws IDAException
  {
    Element localElement = XMLTool.getElementByTagName(paramElement, paramString);
    if (localElement == null)
    {
      CTMLException localCTMLException = new CTMLException(CTMLException.CTML.XMLNODENOTFIND);
      localCTMLException.appendMsg(paramString);
      throw localCTMLException;
    }
    return localElement;
  }

  private void parseXML()
    throws IDAException
  {
    Document localDocument = XMLTool.parseDocument(this.xmlData);
    NodeList localNodeList = localDocument.getElementsByTagName("ctml");
    if (localNodeList.getLength() <= 0)
    {
    	CTMLException localObject1 = new CTMLException(CTMLException.CTML.CTMLNODENOTFIND);
    	throw localObject1;
    }
    this.rootNode = ((Element)localNodeList.item(0));
    Object localObject1 = getElement(this.rootNode, "basedescription");
    this.validityPolicy.setXMLObject(getElement((Element)localObject1, "validitypolicy"));
    this.keyPolicy.setXMLObject(getElement((Element)localObject1, "keypolicy"));
    this.issuePolicy.setXMLObject(getElement((Element)localObject1, "issuepolicy"));
    this.keySpecPolicy.setXMLObject(getElement((Element)localObject1, "keyspecpolicy"));
    this.updateReplacePolicy.setXMLObject(getElement((Element)localObject1, "updatereplacepolicy"));
    this.updTransPeriodPolicy.setXMLObject(getElement((Element)localObject1, "updtransperiodpolicy"));
    this.attribute.setXMLObject(XMLTool.getElementByTagName((Element)localObject1, "attribute"));
    Element localElement1 = getElement(this.rootNode, "extensiondescription");
    this.standardExtPolicys = new Hashtable();
    Element localElement2 = getElement(localElement1, "standardextension");
    localNodeList = XMLTool.getChildElements(localElement2);
    Object localObject2;
    Object localObject3;
    for (int i = 0; i < localNodeList.getLength(); i++)
    {
      if (!(localNodeList.item(i) instanceof Element))
        continue;
      Element localElement4 = (Element)localNodeList.item(i);
      localObject2 = null;
      localObject3 = localElement4.getTagName();
      if (!X509V3ExtensionPolicy.isSupport((String)localObject3))
        continue;
      localObject2 = X509V3ExtensionPolicy.newInstance((String)localObject3);
      ((X509V3ExtensionPolicy)localObject2).setXMLObject(localElement4);
      this.standardExtPolicys.put(localObject3, localObject2);
    }
    Element localElement3 = getElement(localElement1, "selfextension");
    localNodeList = localElement3.getElementsByTagName("userDefineExtension");
    for (int j = 0; j < localNodeList.getLength(); j++)
    {
      localObject2 = (Element)localNodeList.item(j);
      localObject3 = null;
      String str = "userDefineExtension";
      localObject3 = X509V3ExtensionPolicy.newInstance(str);
      ((X509V3ExtensionPolicy)localObject3).setXMLObject((Element)localObject2);
      this.selfExtPolicys.add(localObject3);
    }
  }

  private void serializeXML()
    throws IDAException
  {
    this.rootNode = this.xml.newElement("ctml");
    Element localElement1 = this.xml.newElement("basedescription");
    this.rootNode.appendChild(localElement1);
    localElement1.appendChild(this.validityPolicy.getXMLOjbect());
    localElement1.appendChild(this.keyPolicy.getXMLOjbect());
    localElement1.appendChild(this.issuePolicy.getXMLOjbect());
    localElement1.appendChild(this.keySpecPolicy.getXMLOjbect());
    localElement1.appendChild(this.updateReplacePolicy.getXMLOjbect());
    localElement1.appendChild(this.updTransPeriodPolicy.getXMLOjbect());
    localElement1.appendChild(this.attribute.getXMLOjbect());
    Element localElement2 = this.xml.newElement("extensiondescription");
    this.rootNode.appendChild(localElement2);
    this.standExtensionElement = this.xml.newElement("standardextension");
    localElement2.appendChild(this.standExtensionElement);
    Enumeration localEnumeration = this.standardExtPolicys.elements();
    X509V3ExtensionPolicy localX509V3ExtensionPolicy;
    while (localEnumeration.hasMoreElements())
    {
      localX509V3ExtensionPolicy = (X509V3ExtensionPolicy)localEnumeration.nextElement();
      this.standExtensionElement.appendChild(this.xml.importElement(localX509V3ExtensionPolicy.getXMLOjbect()));
    }
    this.selfExtensionElement = this.xml.newElement("selfextension");
    localElement2.appendChild(this.selfExtensionElement);
    localEnumeration = this.selfExtPolicys.elements();
    while (localEnumeration.hasMoreElements())
    {
      localX509V3ExtensionPolicy = (X509V3ExtensionPolicy)localEnumeration.nextElement();
      this.selfExtensionElement.appendChild(this.xml.importElement(localX509V3ExtensionPolicy.getXMLOjbect()));
    }
  }

  static
  {
    ISSUEMEDIUMS.add("LDAP");
  }

  public class Attribute extends X509V3CTMLPolicy.BasicPolicy
  {
    public static final long CERTTYPE_CA = 1L;
    public static final long CERTTYPE_USER = 0L;
    private static final long DEFAULT = 0L;
    private static final long MASK_CERTTYPE = 65520L;
    public long attribute = 0L;
    private static final String XMLTAG_CERTTYPE = "certtype";
    private static final String XMLVALUE_CACERT = "CA Certificate";
    private static final String XMLVALUE_USERCERT = "User Certificate";

    public Attribute()
    {
      super(null);
    }

    protected void updateData(boolean paramBoolean)
      throws IDAException
    {
      Object localObject;
      if (paramBoolean)
      {
        this.xmlElement = X509V3CTMLPolicy.this.xml.newElement("attribute");
        if ((this.attribute & 1L) != 0L)
          localObject = X509V3CTMLPolicy.this.xml.newElement("certtype", "CA Certificate");
        else
          localObject = X509V3CTMLPolicy.this.xml.newElement("certtype", "User Certificate");
        this.xmlElement.appendChild((Node)localObject);
      }
      else
      {
        this.attribute = 0L;
        if (this.xmlElement == null)
          return;
        localObject = null;
        this.attribute &= 65520L;
        localObject = XMLTool.getValueByTagName(this.xmlElement, "certtype");
        if (localObject != null)
          if ("CA Certificate".equalsIgnoreCase((String)localObject))
            this.attribute |= 1L;
          else
            this.attribute |= 0L;
      }
    }
  }

  public class UpdTransPeriodPolicy extends X509V3CTMLPolicy.BasicPolicy
  {
    public String updTransPeriod = "";
    private static final String XMLTAG_UPDTRANSPERIOD = "updtransperiod";

    public UpdTransPeriodPolicy()
    {
      super(null);
    }

    protected void updateData(boolean paramBoolean)
      throws IDAException
    {
      Object localObject;
      if (paramBoolean)
      {
        this.xmlElement = X509V3CTMLPolicy.this.xml.newElement("updtransperiodpolicy");
        localObject = X509V3CTMLPolicy.this.xml.newElement("updtransperiod", this.updTransPeriod);
        this.xmlElement.appendChild((Node)localObject);
      }
      else
      {
        localObject = null;
        localObject = XMLTool.getValueByTagName(this.xmlElement, "updtransperiod");
        if (localObject == null)
          localObject = "";
        try
        {
          this.updTransPeriod = ((String)localObject);
        }
        catch (Exception localException)
        {
          CTMLException localCTMLException = new CTMLException(CTMLException.CTML.INVALIDUPDTRANSPERIOD, localException);
          localCTMLException.appendMsg((String)localObject);
          throw localCTMLException;
        }
      }
    }
  }

  public class UpdateReplacePolicy extends X509V3CTMLPolicy.BasicPolicy
  {
    public String updateReplace = "false";
    private static final String XMLTAG_UPDATEREPLACE = "updatereplace";

    public UpdateReplacePolicy()
    {
      super(null);
    }

    protected void updateData(boolean paramBoolean)
      throws IDAException
    {
      Object localObject;
      if (paramBoolean)
      {
        this.xmlElement = X509V3CTMLPolicy.this.xml.newElement("updatereplacepolicy");
        localObject = X509V3CTMLPolicy.this.xml.newElement("updatereplace", this.updateReplace);
        this.xmlElement.appendChild((Node)localObject);
      }
      else
      {
        localObject = null;
        localObject = XMLTool.getValueByTagName(this.xmlElement, "updatereplace");
        if (localObject == null)
          throwEx(CTMLException.CTML.UPDATEREPLACEISNULL);
        try
        {
          this.updateReplace = ((String)localObject);
        }
        catch (Exception localException)
        {
          CTMLException localCTMLException = new CTMLException(CTMLException.CTML.INVALIDUPDATEREPLACE, localException);
          localCTMLException.appendMsg((String)localObject);
          throw localCTMLException;
        }
      }
    }
  }

  public class KeySpecPolicy extends X509V3CTMLPolicy.BasicPolicy
  {
    public String keySpec = "2";
    private static final String XMLTAG_KEYSPEC = "keyspec";

    public KeySpecPolicy()
    {
      super(null);
    }

    protected void updateData(boolean paramBoolean)
      throws IDAException
    {
      Object localObject;
      if (paramBoolean)
      {
        this.xmlElement = X509V3CTMLPolicy.this.xml.newElement("keyspecpolicy");
        localObject = X509V3CTMLPolicy.this.xml.newElement("keyspec", this.keySpec);
        this.xmlElement.appendChild((Node)localObject);
      }
      else
      {
        localObject = null;
        localObject = XMLTool.getValueByTagName(this.xmlElement, "keyspec");
        if (localObject == null)
          throwEx(CTMLException.CTML.KEYSPECISNULL);
        try
        {
          this.keySpec = ((String)localObject);
        }
        catch (Exception localException)
        {
          CTMLException localCTMLException = new CTMLException(CTMLException.CTML.INVALIDKEYSPEC, localException);
          localCTMLException.appendMsg((String)localObject);
          throw localCTMLException;
        }
      }
    }
  }

  public class IssuePolicy extends X509V3CTMLPolicy.BasicPolicy
  {
    public boolean isIssue = true;
    public Vector issueMedium = new Vector();
    private static final String XMLTAG_STATUS = "issuestatus";
    private static final String XMLTAG_MEDIUM = "issuemedium";

    public IssuePolicy()
    {
      super(null);
    }

    protected void updateData(boolean paramBoolean)
      throws IDAException
    {
      Object localObject2;
      if (paramBoolean)
      {
        this.xmlElement = X509V3CTMLPolicy.this.xml.newElement("issuepolicy");
        localObject1 = X509V3CTMLPolicy.this.xml.newElement("issuestatus", Boolean.toString(this.isIssue));
        this.xmlElement.appendChild((Node)localObject1);
        Enumeration localEnumeration = this.issueMedium.elements();
        while (localEnumeration.hasMoreElements())
        {
          localObject2 = X509V3CTMLPolicy.this.xml.newElement("issuemedium", (String)localEnumeration.nextElement());
          this.xmlElement.appendChild((Node)localObject2);
        }
      }
      Object localObject1 = null;
      localObject1 = XMLTool.getValueByTagName(this.xmlElement, "issuestatus");
      if (localObject1 == null)
        throwEx(CTMLException.CTML.ISSUESTATUSISNULL);
      try
      {
        this.isIssue = new Boolean((String)localObject1).booleanValue();
      }
      catch (Exception localException)
      {
        localObject2 = new CTMLException(CTMLException.CTML.INVALIDISSUESTATUS, localException);
        ((CTMLException)localObject2).appendMsg((String)localObject1);
        throw ((Throwable)localObject2);
      }
      this.issueMedium = new Vector();
      NodeList localNodeList = this.xmlElement.getElementsByTagName("issuemedium");
      for (int i = 0; i < localNodeList.getLength(); i++)
      {
        localObject1 = XMLTool.getElementValue((Element)localNodeList.item(i));
        if (localObject1 == null)
          throwEx(CTMLException.CTML.ISSUEMEDIUMISNULL);
        if (!CTMLConstant.ISSUE_MEDIUM.isSupport((String)localObject1))
        {
          CTMLException localCTMLException = new CTMLException(CTMLException.CTML.INVALIDISSUEMEDIUM);
          localCTMLException.appendMsg((String)localObject1);
          throw localCTMLException;
        }
        this.issueMedium.add(localObject1);
      }
    }
  }

  public class KeyPolicy extends X509V3CTMLPolicy.BasicPolicy
  {
    public String type = "RSA";
    public int length = 1024;
    public String genPlace = "LOCAL";
    private static final String XMLTAG_KEYTYPE = "keytype";
    private static final String XMLTAG_KEYLENGTH = "keylength";
    private static final String XMLTAG_GENPLACE = "genplace";

    public KeyPolicy()
    {
      super(null);
    }

    protected void updateData(boolean paramBoolean)
      throws IDAException
    {
      Object localObject1;
      Object localObject2;
      Object localObject3;
      if (paramBoolean)
      {
        this.xmlElement = X509V3CTMLPolicy.this.xml.newElement("keypolicy");
        localObject1 = X509V3CTMLPolicy.this.xml.newElement("keytype", this.type);
        this.xmlElement.appendChild((Node)localObject1);
        localObject2 = X509V3CTMLPolicy.this.xml.newElement("keylength", Integer.toString(this.length));
        this.xmlElement.appendChild((Node)localObject2);
        localObject3 = X509V3CTMLPolicy.this.xml.newElement("genplace", this.genPlace);
        this.xmlElement.appendChild((Node)localObject3);
      }
      else
      {
        localObject1 = null;
        localObject1 = XMLTool.getValueByTagName(this.xmlElement, "keytype");
        if (localObject1 == null)
          throwEx(CTMLException.CTML.KEYTYPEISNULL);
        if (!CTMLConstant.KEY.TYPE.isSupport((String)localObject1))
        {
          localObject2 = new CTMLException(CTMLException.CTML.INVALIDKEYTYPE);
          ((CTMLException)localObject2).appendMsg((String)localObject1);
          throw ((Throwable)localObject2);
        }
        this.type = ((String)localObject1);
        localObject1 = XMLTool.getValueByTagName(this.xmlElement, "keylength");
        if (localObject1 == null)
          throwEx(CTMLException.CTML.KEYLENGTHISNULL);
        try
        {
          this.length = Integer.parseInt((String)localObject1);
        }
        catch (NumberFormatException localNumberFormatException)
        {
          localObject3 = new CTMLException(CTMLException.CTML.INVALIDKEYLENGTH, localNumberFormatException);
          ((CTMLException)localObject3).appendMsg((String)localObject1);
          throw ((Throwable)localObject3);
        }
        localObject1 = XMLTool.getValueByTagName(this.xmlElement, "genplace");
        if (localObject1 == null)
          throwEx(CTMLException.CTML.KEYGENPLACEISNULL);
        if (!CTMLConstant.KEY.GENPLACE.isSupport((String)localObject1))
        {
          CTMLException localCTMLException = new CTMLException(CTMLException.CTML.INVALIDKEYGENPLACE);
          localCTMLException.appendMsg((String)localObject1);
          throw localCTMLException;
        }
        this.genPlace = ((String)localObject1);
      }
    }
  }

  public class ValidityPolicy extends X509V3CTMLPolicy.BasicPolicy
  {
    public int defaultValidity = 365;
    public int maxValidity = 3650;
    public long notAfter = 20080909000000000L;
    private static final String XMLTAG_DEFAULTVALIDITY = "defaultvalidity";
    private static final String XMLTAG_MAXVALIDITY = "maxvalidity";
    private static final String XMLTAG_NOTAFTER = "notafter";

    public ValidityPolicy()
    {
      super(null);
    }

    protected void updateData(boolean paramBoolean)
      throws IDAException
    {
      Object localObject1;
      Object localObject2;
      if (paramBoolean)
      {
        this.xmlElement = X509V3CTMLPolicy.this.xml.newElement("validitypolicy");
        localObject1 = X509V3CTMLPolicy.this.xml.newElement("defaultvalidity", Long.toString(this.defaultValidity));
        this.xmlElement.appendChild((Node)localObject1);
        Element localElement = X509V3CTMLPolicy.this.xml.newElement("maxvalidity", Long.toString(this.maxValidity));
        this.xmlElement.appendChild(localElement);
        localObject2 = X509V3CTMLPolicy.this.xml.newElement("notafter", Long.toString(this.notAfter));
        this.xmlElement.appendChild((Node)localObject2);
      }
      else
      {
        localObject1 = null;
        localObject1 = XMLTool.getValueByTagName(this.xmlElement, "defaultvalidity");
        if (localObject1 == null)
          throwEx(CTMLException.CTML.DEFAULTVALIDITYISNULL);
        try
        {
          this.defaultValidity = Integer.parseInt((String)localObject1);
        }
        catch (NumberFormatException localNumberFormatException1)
        {
          localObject2 = new CTMLException(CTMLException.CTML.INVALIDDEFAULTVALIDITY, localNumberFormatException1);
          ((CTMLException)localObject2).appendMsg((String)localObject1);
          throw ((Throwable)localObject2);
        }
        localObject1 = XMLTool.getValueByTagName(this.xmlElement, "maxvalidity");
        if (localObject1 == null)
          throwEx(CTMLException.CTML.MAXVALIDITYISNULL);
        try
        {
          this.maxValidity = Integer.parseInt((String)localObject1);
        }
        catch (NumberFormatException localNumberFormatException2)
        {
          localObject2 = new CTMLException(CTMLException.CTML.INVALIDMAXVALIDITY, localNumberFormatException2);
          ((CTMLException)localObject2).appendMsg((String)localObject1);
          throw ((Throwable)localObject2);
        }
        localObject1 = XMLTool.getValueByTagName(this.xmlElement, "notafter");
        if (localObject1 == null)
          throwEx(CTMLException.CTML.NOTAFTERISNULL);
        try
        {
          this.notAfter = Long.parseLong((String)localObject1);
        }
        catch (NumberFormatException localNumberFormatException3)
        {
          localObject2 = new CTMLException(CTMLException.CTML.INVALIDNOTAFTER, localNumberFormatException3);
          ((CTMLException)localObject2).appendMsg((String)localObject1);
          throw ((Throwable)localObject2);
        }
      }
    }
  }

  private abstract class BasicPolicy
  {
    protected Element xmlElement;
    private final X509V3CTMLPolicy this$0;

    private BasicPolicy()
    {
      this.this$0 = this$1;
    }

    final Element getXMLOjbect()
      throws IDAException
    {
      updateData(true);
      return this.xmlElement;
    }

    final void setXMLObject(Element paramElement)
      throws IDAException
    {
      this.xmlElement = paramElement;
      updateData(false);
    }

    protected abstract void updateData(boolean paramBoolean)
      throws IDAException;

    protected void throwEx(int paramInt)
      throws IDAException
    {
      CTMLException localCTMLException = new CTMLException(paramInt);
      throw localCTMLException;
    }

    BasicPolicy(X509V3CTMLPolicy.1 arg2)
    {
      this();
    }
  }
}

/* Location:           C:\Program Files\JIT\CA50\lib\IDA\ida.jar
 * Qualified Name:     cn.com.jit.ida.ca.ctml.x509v3.X509V3CTMLPolicy
 * JD-Core Version:    0.6.0
 */