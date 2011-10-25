package cn.com.jit.ida.ca.certmanager.service.request;

import cn.com.jit.ida.IDAException;
import cn.com.jit.ida.Request;
import cn.com.jit.ida.ca.certmanager.CertException;
import cn.com.jit.ida.ca.ctml.x509v3.extension.StandardExtension;
import cn.com.jit.ida.util.xml.XMLTool;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Properties;
import java.util.Vector;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class CertUPDRequest extends Request
{
  private String certSN = null;
  private String notBefore = null;
  private String validity = null;
  private String certDN = null;
  private String ctmlName = null;
  private boolean isRetainKey = false;
  private Properties extensions = null;
  private Hashtable standardExtensions = null;
  private boolean ifCheckValidate = true;

  public CertUPDRequest()
  {
    super.setOperation("CERTUPDATE");
  }

  public CertUPDRequest(Request paramRequest)
    throws CertException
  {
    try
    {
      super.setData(paramRequest.getDocument());
    }
    catch (Exception localException)
    {
      throw new CertException("81090704", "证书更新服务其他错误 请求信息格式不合法", localException);
    }
  }

  public CertUPDRequest(byte[] paramArrayOfByte)
    throws CertException
  {
    try
    {
      super.setData(paramArrayOfByte);
    }
    catch (Exception localException)
    {
      throw new CertException("81090704", "证书更新服务 其他错误 请求信息格式不合法");
    }
  }

  protected final void updateBody(boolean paramBoolean)
    throws IDAException
  {
    Object localObject1;
    if (paramBoolean)
    {
      this.body = this.doc.createElement("body");
      localObject1 = this.doc.createElement("entry");
      this.body.appendChild((Node)localObject1);
      Element localElement1 = XMLTool.newElement(this.doc, "certSN", this.certSN);
      ((Element)localObject1).appendChild(localElement1);
      Element localElement2 = XMLTool.newElement(this.doc, "notBefore", this.notBefore);
      ((Element)localObject1).appendChild(localElement2);
      Element localElement3 = XMLTool.newElement(this.doc, "validity", this.validity);
      ((Element)localObject1).appendChild(localElement3);
      Element localElement4 = XMLTool.newElement(this.doc, "subject", this.certDN);
      ((Element)localObject1).appendChild(localElement4);
      Element localElement5 = XMLTool.newElement(this.doc, "ctmlName", this.ctmlName);
      ((Element)localObject1).appendChild(localElement5);
      if (this.ifCheckValidate == true)
      {
        localElement6 = XMLTool.newElement(this.doc, "ifCheckValidate", "true");
        ((Element)localObject1).appendChild(localElement6);
      }
      if (!this.ifCheckValidate)
      {
        localElement6 = XMLTool.newElement(this.doc, "ifCheckValidate", "false");
        ((Element)localObject1).appendChild(localElement6);
      }
      Element localElement6 = XMLTool.newElement(this.doc, "isRetainKey", Boolean.toString(this.isRetainKey));
      ((Element)localObject1).appendChild(localElement6);
      Element localElement7;
      Object localObject2;
      Object localObject3;
      Object localObject4;
      Object localObject5;
      if (this.standardExtensions != null)
      {
        localElement7 = XMLTool.newChildElement((Element)localObject1, "standardExtensions");
        StandardExtension localStandardExtension;
        String str1;
        String str2;
        String str3;
        String str4;
        if (this.standardExtensions.get("SubjectAltNameExt") != null)
        {
          localObject2 = XMLTool.newChildElement(localElement7, "SubjectAltNameExt");
          localObject3 = (Vector)this.standardExtensions.get("SubjectAltNameExt");
          localObject4 = ((Vector)localObject3).elements();
          while (((Enumeration)localObject4).hasMoreElements())
          {
            localObject5 = XMLTool.newChildElement((Element)localObject2, "extEntry");
            localStandardExtension = (StandardExtension)((Enumeration)localObject4).nextElement();
            str1 = localStandardExtension.getParentName();
            str2 = localStandardExtension.getChildName();
            str3 = localStandardExtension.getStandardValue();
            str4 = localStandardExtension.getAllowNull();
            String str5 = localStandardExtension.getOtherNameOid();
            XMLTool.newChildElement((Element)localObject5, "parentName", str1);
            XMLTool.newChildElement((Element)localObject5, "parentOID", "2.5.29.17");
            if (str2 != null)
              XMLTool.newChildElement((Element)localObject5, "childName", str2);
            if (str3 != null)
              XMLTool.newChildElement((Element)localObject5, "value", str3);
            if (str4 != null)
              XMLTool.newChildElement((Element)localObject5, "allowNull", str4);
            if (str5 == null)
              continue;
            XMLTool.newChildElement((Element)localObject5, "otherNameOid", str5);
          }
        }
        if (this.standardExtensions.get("IdentifyCode") != null)
        {
          localObject2 = XMLTool.newChildElement(localElement7, "IdentifyCode");
          localObject3 = (Vector)this.standardExtensions.get("IdentifyCode");
          localObject4 = ((Vector)localObject3).elements();
          while (((Enumeration)localObject4).hasMoreElements())
          {
            localObject5 = XMLTool.newChildElement((Element)localObject2, "extEntry");
            localStandardExtension = (StandardExtension)((Enumeration)localObject4).nextElement();
            str1 = localStandardExtension.getParentName();
            str2 = localStandardExtension.getChildName();
            str3 = localStandardExtension.getStandardValue();
            str4 = localStandardExtension.getAllowNull();
            XMLTool.newChildElement((Element)localObject5, "parentName", str1);
            XMLTool.newChildElement((Element)localObject5, "parentOID", "1.2.86.11.7.1");
            if (str2 != null)
              XMLTool.newChildElement((Element)localObject5, "childName", str2);
            if (str3 != null)
              XMLTool.newChildElement((Element)localObject5, "value", str3);
            if (str4 == null)
              continue;
            XMLTool.newChildElement((Element)localObject5, "allowNull", str4);
          }
        }
        if (this.standardExtensions.get("InsuranceNumber") != null)
        {
          localObject2 = XMLTool.newChildElement(localElement7, "InsuranceNumber");
          localObject3 = (Vector)this.standardExtensions.get("InsuranceNumber");
          localObject4 = ((Vector)localObject3).elements();
          while (((Enumeration)localObject4).hasMoreElements())
          {
            localObject5 = XMLTool.newChildElement((Element)localObject2, "extEntry");
            localStandardExtension = (StandardExtension)((Enumeration)localObject4).nextElement();
            str1 = localStandardExtension.getParentName();
            str2 = localStandardExtension.getChildName();
            str3 = localStandardExtension.getStandardValue();
            str4 = localStandardExtension.getAllowNull();
            XMLTool.newChildElement((Element)localObject5, "parentName", str1);
            XMLTool.newChildElement((Element)localObject5, "parentOID", "1.2.86.11.7.2");
            if (str2 != null)
              XMLTool.newChildElement((Element)localObject5, "childName", str2);
            if (str3 != null)
              XMLTool.newChildElement((Element)localObject5, "value", str3);
            if (str4 == null)
              continue;
            XMLTool.newChildElement((Element)localObject5, "allowNull", str4);
          }
        }
        if (this.standardExtensions.get("ICRegistrationNumber") != null)
        {
          localObject2 = XMLTool.newChildElement(localElement7, "ICRegistrationNumber");
          localObject3 = (Vector)this.standardExtensions.get("ICRegistrationNumber");
          localObject4 = ((Vector)localObject3).elements();
          while (((Enumeration)localObject4).hasMoreElements())
          {
            localObject5 = XMLTool.newChildElement((Element)localObject2, "extEntry");
            localStandardExtension = (StandardExtension)((Enumeration)localObject4).nextElement();
            str1 = localStandardExtension.getParentName();
            str2 = localStandardExtension.getChildName();
            str3 = localStandardExtension.getStandardValue();
            str4 = localStandardExtension.getAllowNull();
            XMLTool.newChildElement((Element)localObject5, "parentName", str1);
            XMLTool.newChildElement((Element)localObject5, "parentOID", "1.2.86.11.7.4");
            if (str2 != null)
              XMLTool.newChildElement((Element)localObject5, "childName", str2);
            if (str3 != null)
              XMLTool.newChildElement((Element)localObject5, "value", str3);
            if (str4 == null)
              continue;
            XMLTool.newChildElement((Element)localObject5, "allowNull", str4);
          }
        }
        if (this.standardExtensions.get("OrganizationCode") != null)
        {
          localObject2 = XMLTool.newChildElement(localElement7, "OrganizationCode");
          localObject3 = (Vector)this.standardExtensions.get("OrganizationCode");
          localObject4 = ((Vector)localObject3).elements();
          while (((Enumeration)localObject4).hasMoreElements())
          {
            localObject5 = XMLTool.newChildElement((Element)localObject2, "extEntry");
            localStandardExtension = (StandardExtension)((Enumeration)localObject4).nextElement();
            str1 = localStandardExtension.getParentName();
            str2 = localStandardExtension.getChildName();
            str3 = localStandardExtension.getStandardValue();
            str4 = localStandardExtension.getAllowNull();
            XMLTool.newChildElement((Element)localObject5, "parentName", str1);
            XMLTool.newChildElement((Element)localObject5, "parentOID", "1.2.86.11.7.3");
            if (str2 != null)
              XMLTool.newChildElement((Element)localObject5, "childName", str2);
            if (str3 != null)
              XMLTool.newChildElement((Element)localObject5, "value", str3);
            if (str4 == null)
              continue;
            XMLTool.newChildElement((Element)localObject5, "allowNull", str4);
          }
        }
        if (this.standardExtensions.get("TaxationNumber") != null)
        {
          localObject2 = XMLTool.newChildElement(localElement7, "TaxationNumber");
          localObject3 = (Vector)this.standardExtensions.get("TaxationNumber");
          localObject4 = ((Vector)localObject3).elements();
          while (((Enumeration)localObject4).hasMoreElements())
          {
            localObject5 = XMLTool.newChildElement((Element)localObject2, "extEntry");
            localStandardExtension = (StandardExtension)((Enumeration)localObject4).nextElement();
            str1 = localStandardExtension.getParentName();
            str2 = localStandardExtension.getChildName();
            str3 = localStandardExtension.getStandardValue();
            str4 = localStandardExtension.getAllowNull();
            XMLTool.newChildElement((Element)localObject5, "parentName", str1);
            XMLTool.newChildElement((Element)localObject5, "parentOID", "1.2.86.11.7.5");
            if (str2 != null)
              XMLTool.newChildElement((Element)localObject5, "childName", str2);
            if (str3 != null)
              XMLTool.newChildElement((Element)localObject5, "value", str3);
            if (str4 == null)
              continue;
            XMLTool.newChildElement((Element)localObject5, "allowNull", str4);
          }
        }
        if (this.standardExtensions.get("PolicyConstrants") != null)
        {
          localObject2 = XMLTool.newChildElement(localElement7, "PolicyConstrants");
          localObject3 = (Vector)this.standardExtensions.get("PolicyConstrants");
          localObject4 = ((Vector)localObject3).elements();
          while (((Enumeration)localObject4).hasMoreElements())
          {
            localObject5 = XMLTool.newChildElement((Element)localObject2, "extEntry");
            localStandardExtension = (StandardExtension)((Enumeration)localObject4).nextElement();
            str1 = localStandardExtension.getParentName();
            str2 = localStandardExtension.getChildName();
            str3 = localStandardExtension.getStandardValue();
            str4 = localStandardExtension.getAllowNull();
            XMLTool.newChildElement((Element)localObject5, "parentName", str1);
            XMLTool.newChildElement((Element)localObject5, "parentOID", "2.5.29.36");
            if (str2 != null)
              XMLTool.newChildElement((Element)localObject5, "childName", str2);
            if (str3 != null)
              XMLTool.newChildElement((Element)localObject5, "value", str3);
            if (str4 == null)
              continue;
            XMLTool.newChildElement((Element)localObject5, "allowNull", str4);
          }
        }
        if (this.standardExtensions.get("PolicyMappings") != null)
        {
          localObject2 = XMLTool.newChildElement(localElement7, "PolicyMappings");
          localObject3 = (Vector)this.standardExtensions.get("PolicyMappings");
          localObject4 = ((Vector)localObject3).elements();
          while (((Enumeration)localObject4).hasMoreElements())
          {
            localObject5 = XMLTool.newChildElement((Element)localObject2, "extEntry");
            localStandardExtension = (StandardExtension)((Enumeration)localObject4).nextElement();
            str1 = localStandardExtension.getParentName();
            str2 = localStandardExtension.getChildName();
            str3 = localStandardExtension.getStandardValue();
            str4 = localStandardExtension.getAllowNull();
            XMLTool.newChildElement((Element)localObject5, "parentName", str1);
            XMLTool.newChildElement((Element)localObject5, "parentOID", "2.5.29.33");
            if (str2 != null)
              XMLTool.newChildElement((Element)localObject5, "childName", str2);
            if (str3 != null)
              XMLTool.newChildElement((Element)localObject5, "value", str3);
            if (str4 == null)
              continue;
            XMLTool.newChildElement((Element)localObject5, "allowNull", str4);
          }
        }
      }
      if (this.extensions != null)
      {
        localElement7 = XMLTool.newChildElement((Element)localObject1, "selfExtensions");
        localObject2 = this.extensions.propertyNames();
        while (((Enumeration)localObject2).hasMoreElements())
        {
          localObject3 = XMLTool.newChildElement(localElement7, "extEntry");
          localObject4 = (String)((Enumeration)localObject2).nextElement();
          localObject5 = this.extensions.getProperty((String)localObject4);
          XMLTool.newChildElement((Element)localObject3, "name", (String)localObject4);
          XMLTool.newChildElement((Element)localObject3, "value", (String)localObject5);
        }
      }
    }
    else
    {
      this.certSN = XMLTool.getValueByTagName(this.body, "certSN");
      this.notBefore = XMLTool.getValueByTagName(this.body, "notBefore");
      this.validity = XMLTool.getValueByTagName(this.body, "validity");
      this.certDN = XMLTool.getValueByTagName(this.body, "subject");
      this.ctmlName = XMLTool.getValueByTagName(this.body, "ctmlName");
      localObject1 = XMLTool.getValueByTagName(this.body, "ifCheckValidate");
      if (((String)localObject1).equals("true"))
        this.ifCheckValidate = true;
      if (((String)localObject1).equals("false"))
        this.ifCheckValidate = false;
      this.isRetainKey = Boolean.valueOf(XMLTool.getValueByTagName(this.body, "isRetainKey")).booleanValue();
      this.standardExtensions = getStandardExtensionsFromXML();
      this.extensions = getExtensionsFromXML();
    }
  }

  public String getCertDN()
  {
    return this.certDN;
  }

  public String getCertSN()
  {
    return this.certSN;
  }

  public String getCtmlName()
  {
    return this.ctmlName;
  }

  public String getNotBefore()
  {
    return this.notBefore;
  }

  public String getValidity()
  {
    return this.validity;
  }

  public void setValidity(String paramString)
    throws IDAException
  {
    String str = paramString.trim();
    if (!ReqCheck.checkValidity(str))
      throw new IDAException("81090804", "证书更新服务 设置申请信息错误 有效期长度内容错误");
    this.validity = str;
  }

  public void setNotBefore(String paramString)
    throws IDAException
  {
    String str = paramString.trim();
    if (!ReqCheck.checkNotBefore(str))
      throw new IDAException("81090803", "证书更新服务 设置申请信息错误 起始有效期内容错误");
    this.notBefore = str;
  }

  public void setCtmlName(String paramString)
  {
    if (paramString != null)
      this.ctmlName = paramString.trim();
  }

  public void setCertSN(String paramString)
  {
    if (paramString != null)
      this.certSN = paramString.trim();
  }

  public void setCertDN(String paramString)
    throws IDAException
  {
    if ((paramString != null) && (!paramString.trim().equals("")))
    {
      if (!ReqCheck.checkDN(paramString))
        throw new IDAException("81090801", "证书更新服务 设置申请信息错误 证书主题内容错误");
      this.certDN = ReqCheck.filterDN(paramString);
    }
  }

  public boolean getIsRetainKey()
  {
    return this.isRetainKey;
  }

  public void setIsRetainKey(boolean paramBoolean)
  {
    this.isRetainKey = paramBoolean;
  }

  public Properties getExtensions()
  {
    return this.extensions;
  }

  public Hashtable getStandardExtensions()
  {
    return this.standardExtensions;
  }

  public void setExtensions(Properties paramProperties)
  {
    this.extensions = paramProperties;
  }

  public void setStandardExtensions(Hashtable paramHashtable)
  {
    this.standardExtensions = paramHashtable;
  }

  public void setIfCheckValidate(boolean paramBoolean)
  {
    this.ifCheckValidate = paramBoolean;
  }

  public boolean getIfCheckValidate()
  {
    return this.ifCheckValidate;
  }

  public Hashtable getStandardExtensionsFromXML()
  {
    if (this.body == null)
      return null;
    NodeList localNodeList1 = this.body.getElementsByTagName("entry");
    Element localElement1 = (Element)localNodeList1.item(0);
    if (localElement1 == null)
      return null;
    NodeList localNodeList2 = localElement1.getElementsByTagName("standardExtensions");
    if (localNodeList2.getLength() == 0)
      return null;
    Vector localVector1 = new Vector();
    Vector localVector2 = new Vector();
    Vector localVector3 = new Vector();
    Vector localVector4 = new Vector();
    Vector localVector5 = new Vector();
    Vector localVector6 = new Vector();
    Vector localVector7 = new Vector();
    Vector localVector8 = new Vector();
    StandardExtension localStandardExtension = null;
    Hashtable localHashtable = new Hashtable();
    Element localElement2 = (Element)localNodeList2.item(0);
    NodeList localNodeList3 = localElement2.getElementsByTagName("SubjectAltNameExt");
    int j;
    Object localObject8;
    String str1;
    String str2;
    Object localObject9;
    if (localNodeList3.getLength() > 0)
    {
      localObject1 = (Element)localNodeList3.item(0);
      localObject2 = XMLTool.getChildElements((Element)localObject1);
      int i = ((NodeList)localObject2).getLength();
      for (j = 0; j < i; j++)
      {
        Element localElement3 = (Element)((NodeList)localObject2).item(j);
        localObject6 = XMLTool.getValueByTagName(localElement3, "parentName");
        localObject7 = XMLTool.getValueByTagName(localElement3, "childName");
        localObject8 = XMLTool.getValueByTagName(localElement3, "value");
        str1 = XMLTool.getValueByTagName(localElement3, "otherNameOid");
        str2 = XMLTool.getValueByTagName(localElement3, "parentOID");
        localObject9 = XMLTool.getValueByTagName(localElement3, "allowNull");
        localStandardExtension = new StandardExtension();
        localStandardExtension.setAllowNull((String)localObject9);
        localStandardExtension.setParentName((String)localObject6);
        localStandardExtension.setChildName((String)localObject7);
        localStandardExtension.setStandardValue((String)localObject8);
        localStandardExtension.setOtherNameOid(str1);
        localStandardExtension.setParentOID(str2);
        localVector1.add(localStandardExtension);
      }
      localHashtable.put("SubjectAltNameExt", localVector1);
    }
    Object localObject1 = localElement2.getElementsByTagName("IdentifyCode");
    if (((NodeList)localObject1).getLength() > 0)
    {
      localObject2 = (Element)((NodeList)localObject1).item(0);
      localObject3 = XMLTool.getChildElements((Element)localObject2);
      j = ((NodeList)localObject3).getLength();
      for (int k = 0; k < j; k++)
      {
        localObject6 = (Element)((NodeList)localObject3).item(k);
        localObject7 = XMLTool.getValueByTagName((Element)localObject6, "parentName");
        localObject8 = XMLTool.getValueByTagName((Element)localObject6, "childName");
        str1 = XMLTool.getValueByTagName((Element)localObject6, "value");
        str2 = XMLTool.getValueByTagName((Element)localObject6, "parentOID");
        localObject9 = XMLTool.getValueByTagName((Element)localObject6, "allowNull");
        localStandardExtension = new StandardExtension();
        localStandardExtension.setAllowNull((String)localObject9);
        localStandardExtension.setParentName((String)localObject7);
        localStandardExtension.setChildName((String)localObject8);
        localStandardExtension.setStandardValue(str1);
        localStandardExtension.setParentOID(str2);
        localVector2.add(localStandardExtension);
      }
      localHashtable.put("IdentifyCode", localVector2);
    }
    Object localObject2 = localElement2.getElementsByTagName("InsuranceNumber");
    if (((NodeList)localObject2).getLength() > 0)
    {
      localObject3 = (Element)((NodeList)localObject2).item(0);
      localObject4 = XMLTool.getChildElements((Element)localObject3);
      localObject5 = (Element)((NodeList)localObject4).item(0);
      localObject6 = XMLTool.getValueByTagName((Element)localObject5, "parentName");
      localObject7 = XMLTool.getValueByTagName((Element)localObject5, "childName");
      localObject8 = XMLTool.getValueByTagName((Element)localObject5, "value");
      str1 = XMLTool.getValueByTagName((Element)localObject5, "parentOID");
      str2 = XMLTool.getValueByTagName((Element)localObject5, "allowNull");
      localStandardExtension = new StandardExtension();
      localStandardExtension.setAllowNull(str2);
      localStandardExtension.setParentName((String)localObject6);
      localStandardExtension.setChildName((String)localObject7);
      localStandardExtension.setStandardValue((String)localObject8);
      localStandardExtension.setParentOID(str1);
      localVector3.add(localStandardExtension);
      localHashtable.put("InsuranceNumber", localVector3);
    }
    Object localObject3 = localElement2.getElementsByTagName("OrganizationCode");
    if (((NodeList)localObject3).getLength() > 0)
    {
      localObject4 = (Element)((NodeList)localObject3).item(0);
      localObject5 = XMLTool.getChildElements((Element)localObject4);
      localObject6 = (Element)((NodeList)localObject5).item(0);
      localObject7 = XMLTool.getValueByTagName((Element)localObject6, "parentName");
      localObject8 = XMLTool.getValueByTagName((Element)localObject6, "childName");
      str1 = XMLTool.getValueByTagName((Element)localObject6, "value");
      str2 = XMLTool.getValueByTagName((Element)localObject6, "parentOID");
      localObject9 = XMLTool.getValueByTagName((Element)localObject6, "allowNull");
      localStandardExtension = new StandardExtension();
      localStandardExtension.setAllowNull((String)localObject9);
      localStandardExtension.setParentName((String)localObject7);
      localStandardExtension.setChildName((String)localObject8);
      localStandardExtension.setStandardValue(str1);
      localStandardExtension.setParentOID(str2);
      localVector6.add(localStandardExtension);
      localHashtable.put("OrganizationCode", localVector6);
    }
    Object localObject4 = localElement2.getElementsByTagName("ICRegistrationNumber");
    Object localObject10;
    if (((NodeList)localObject4).getLength() > 0)
    {
      localObject5 = (Element)((NodeList)localObject4).item(0);
      localObject6 = XMLTool.getChildElements((Element)localObject5);
      localObject7 = (Element)((NodeList)localObject6).item(0);
      localObject8 = XMLTool.getValueByTagName((Element)localObject7, "parentName");
      str1 = XMLTool.getValueByTagName((Element)localObject7, "childName");
      str2 = XMLTool.getValueByTagName((Element)localObject7, "value");
      localObject9 = XMLTool.getValueByTagName((Element)localObject7, "parentOID");
      localObject10 = XMLTool.getValueByTagName((Element)localObject7, "allowNull");
      localStandardExtension = new StandardExtension();
      localStandardExtension.setAllowNull((String)localObject10);
      localStandardExtension.setParentName((String)localObject8);
      localStandardExtension.setChildName(str1);
      localStandardExtension.setStandardValue(str2);
      localStandardExtension.setParentOID((String)localObject9);
      localVector4.add(localStandardExtension);
      localHashtable.put("ICRegistrationNumber", localVector4);
    }
    Object localObject5 = localElement2.getElementsByTagName("TaxationNumber");
    String str3;
    if (((NodeList)localObject5).getLength() > 0)
    {
      localObject6 = (Element)((NodeList)localObject5).item(0);
      localObject7 = XMLTool.getChildElements((Element)localObject6);
      localObject8 = (Element)((NodeList)localObject7).item(0);
      str1 = XMLTool.getValueByTagName((Element)localObject8, "parentName");
      str2 = XMLTool.getValueByTagName((Element)localObject8, "childName");
      localObject9 = XMLTool.getValueByTagName((Element)localObject8, "value");
      localObject10 = XMLTool.getValueByTagName((Element)localObject8, "parentOID");
      str3 = XMLTool.getValueByTagName((Element)localObject8, "allowNull");
      localStandardExtension = new StandardExtension();
      localStandardExtension.setAllowNull(str3);
      localStandardExtension.setParentName(str1);
      localStandardExtension.setChildName(str2);
      localStandardExtension.setStandardValue((String)localObject9);
      localStandardExtension.setParentOID((String)localObject10);
      localVector5.add(localStandardExtension);
      localHashtable.put("TaxationNumber", localVector5);
    }
    Object localObject6 = localElement2.getElementsByTagName("PolicyConstrants");
    int n;
    String str4;
    String str5;
    String str6;
    if (((NodeList)localObject6).getLength() > 0)
    {
      localObject7 = (Element)((NodeList)localObject6).item(0);
      localObject8 = XMLTool.getChildElements((Element)localObject7);
      int m = ((NodeList)localObject8).getLength();
      for (n = 0; n < m; n++)
      {
        localObject9 = (Element)((NodeList)localObject8).item(n);
        localObject10 = XMLTool.getValueByTagName((Element)localObject9, "parentName");
        str3 = XMLTool.getValueByTagName((Element)localObject9, "childName");
        str4 = XMLTool.getValueByTagName((Element)localObject9, "value");
        str5 = XMLTool.getValueByTagName((Element)localObject9, "parentOID");
        str6 = XMLTool.getValueByTagName((Element)localObject9, "allowNull");
        localStandardExtension = new StandardExtension();
        localStandardExtension.setAllowNull(str6);
        localStandardExtension.setParentName((String)localObject10);
        localStandardExtension.setChildName(str3);
        localStandardExtension.setStandardValue(str4);
        localStandardExtension.setParentOID(str5);
        localVector7.add(localStandardExtension);
      }
      localHashtable.put("PolicyConstrants", localVector7);
    }
    Object localObject7 = localElement2.getElementsByTagName("PolicyMappings");
    if (((NodeList)localObject7).getLength() > 0)
    {
      localObject8 = (Element)((NodeList)localObject7).item(0);
      NodeList localNodeList4 = XMLTool.getChildElements((Element)localObject8);
      n = localNodeList4.getLength();
      for (int i1 = 0; i1 < n; i1++)
      {
        localObject10 = (Element)localNodeList4.item(i1);
        str3 = XMLTool.getValueByTagName((Element)localObject10, "parentName");
        str4 = XMLTool.getValueByTagName((Element)localObject10, "childName");
        str5 = XMLTool.getValueByTagName((Element)localObject10, "value");
        str6 = XMLTool.getValueByTagName((Element)localObject10, "parentOID");
        String str7 = XMLTool.getValueByTagName((Element)localObject10, "allowNull");
        localStandardExtension = new StandardExtension();
        localStandardExtension.setAllowNull(str7);
        localStandardExtension.setParentName(str3);
        localStandardExtension.setChildName(str4);
        localStandardExtension.setStandardValue(str5);
        localStandardExtension.setParentOID(str6);
        localVector8.add(localStandardExtension);
      }
      localHashtable.put("PolicyMappings", localVector8);
    }
    if (localHashtable.size() == 0)
      localHashtable = null;
    return (Hashtable)(Hashtable)(Hashtable)(Hashtable)(Hashtable)(Hashtable)(Hashtable)(Hashtable)(Hashtable)(Hashtable)localHashtable;
  }

  public Properties getExtensionsFromXML()
  {
    if (this.body == null)
      return null;
    NodeList localNodeList1 = this.body.getElementsByTagName("entry");
    Element localElement1 = (Element)localNodeList1.item(0);
    if (localElement1 == null)
      return null;
    NodeList localNodeList2 = localElement1.getElementsByTagName("selfExtensions");
    if (localNodeList2.getLength() == 0)
      return null;
    Element localElement2 = (Element)localNodeList2.item(0);
    NodeList localNodeList3 = XMLTool.getChildElements(localElement2);
    int i = localNodeList3.getLength();
    Properties localProperties = new Properties();
    for (int j = 0; j < i; j++)
    {
      Element localElement3 = (Element)localNodeList3.item(j);
      String str1 = XMLTool.getValueByTagName(localElement3, "name");
      String str2 = XMLTool.getValueByTagName(localElement3, "value");
      localProperties.setProperty(str1, str2);
    }
    if (localProperties.size() == 0)
      localProperties = null;
    return localProperties;
  }
}

/* Location:           C:\Program Files\JIT\CA50\lib\IDA\ida.jar
 * Qualified Name:     cn.com.jit.ida.ca.certmanager.service.request.CertUPDRequest
 * JD-Core Version:    0.6.0
 */