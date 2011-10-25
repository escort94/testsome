package cn.com.jit.ida.ca.ctml.service.response;

import cn.com.jit.ida.IDAException;
import cn.com.jit.ida.Response;
import cn.com.jit.ida.ca.ctml.service.CTMLServiceException;
import cn.com.jit.ida.ca.ctml.service.CTMLServiceException.REQUEST;
import cn.com.jit.ida.ca.ctml.x509v3.extension.StandardExtension;
import cn.com.jit.ida.util.xml.XMLTool;
import java.util.Enumeration;
import java.util.Vector;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class RACTMLUpdateResponse extends Response
{
  private static final String XMLTAG_NAME = "ctmlName";
  private static final String XMLTAG_ID = "ctmlID";
  private static final String XMLTAG_TYPE = "ctmlType";
  private static final String XMLTAG_STATUS = "ctmlStatus";
  private static final String XMLTAG_KEYTYPE = "keyType";
  private static final String XMLTAG_KEYSIZE = "keySize";
  private static final String XMLTAG_KEYSPEC = "keySpec";
  private static final String XMLTAG_UPDATEREPLACE = "updateReplace";
  private static final String XMLTAG_UPDATETRANSPERIOD = "updateTransPeriod";
  private static final String XMLTAG_DEFAYKTVALIDATE = "defaultValidate";
  private static final String XMLTAG_MAXVALIDATE = "maxValidate";
  private static final String XMLTAG_NOTAFTERDATE = "notafterDate";
  private static final String XMLTAG_KEYGENLOCATION = "keyGenLocation";
  private static final String XMLTAG_DESCRIPTION = "ctmlDesc";
  private static final String XMLTAG_SELFEXT = "selfExt";
  private static final String XMLTAG_SELFEXTINFO = "selfExtInfo";
  private static final String XMLTAG_SELFEXT_NAME = "name";
  private static final String XMLTAG_SELFEXT_OID = "oid";
  private static final String XMLTAG_SELFEXT_ENCODING = "encoding";
  private static final String XMLTAG_SELFEXT_STATUS = "status";
  private static final String XMLTAG_SELFEXT_USERPROCESS = "userprocess";
  private static final String XMLTAG_SELFEXT_DESCRIPTION = "desc";
  private static final String XMLTAG_BASEDN = "baseDN";
  private static final String XMLTAG_BASEDNINFO = "baseDNInfo";
  private static final String XMLTAG_BASEDN_DN = "DN";
  private Vector resultContainer;

  public RACTMLUpdateResponse()
  {
    super.setOperation("RACTMLUPDATE");
  }

  public RACTMLUpdateResponse(Response paramResponse)
    throws IDAException
  {
    try
    {
      super.setData(paramResponse.getDocument());
    }
    catch (Exception localException)
    {
      CTMLServiceException localCTMLServiceException = new CTMLServiceException(CTMLServiceException.REQUEST.PARSE_REQUESTXMLDOC, localException);
      throw localCTMLServiceException;
    }
  }

  public RACTMLUpdateResponse(byte[] paramArrayOfByte)
    throws IDAException
  {
    try
    {
      super.setData(paramArrayOfByte);
    }
    catch (Exception localException)
    {
      CTMLServiceException localCTMLServiceException = new CTMLServiceException(CTMLServiceException.REQUEST.PARSE_REQUESTXMLDATA, localException);
      throw localCTMLServiceException;
    }
  }

  public void setResult(Object[] paramArrayOfObject)
  {
    this.resultContainer = new Vector(paramArrayOfObject.length);
    for (int i = 0; i < paramArrayOfObject.length; i++)
      this.resultContainer.add(i, (CTMLItem)paramArrayOfObject[i]);
  }

  public CTMLItem[] getResult()
  {
    CTMLItem[] arrayOfCTMLItem = new CTMLItem[this.resultContainer.size()];
    this.resultContainer.copyInto(arrayOfCTMLItem);
    return arrayOfCTMLItem;
  }

  protected final void updateBody(boolean paramBoolean)
    throws IDAException
  {
    Object localObject1;
    Object localObject2;
    Object localObject3;
    Object localObject5;
    Object localObject6;
    Object localObject7;
    Object localObject8;
    String str2;
    if (paramBoolean)
    {
      this.body = this.doc.createElement("body");
      for (int i = 0; i < this.resultContainer.size(); i++)
      {
        Element localElement1 = this.doc.createElement("entry");
        this.body.appendChild(localElement1);
        localObject1 = (CTMLItem)this.resultContainer.get(i);
        if (((CTMLItem)localObject1).getId() != null)
          XMLTool.newChildElement(localElement1, "ctmlID", ((CTMLItem)localObject1).getId());
        if (((CTMLItem)localObject1).getName() != null)
          XMLTool.newChildElement(localElement1, "ctmlName", ((CTMLItem)localObject1).getName());
        if (((CTMLItem)localObject1).getType() != null)
          XMLTool.newChildElement(localElement1, "ctmlType", ((CTMLItem)localObject1).getType());
        if (((CTMLItem)localObject1).getKeytype() != null)
          XMLTool.newChildElement(localElement1, "keyType", ((CTMLItem)localObject1).getKeytype());
        XMLTool.newChildElement(localElement1, "keySize", String.valueOf(((CTMLItem)localObject1).getKeysize()));
        if (((CTMLItem)localObject1).getKeySpec() != null)
          XMLTool.newChildElement(localElement1, "keySpec", ((CTMLItem)localObject1).getKeySpec());
        if (((CTMLItem)localObject1).getUpdateReplace() != null)
          XMLTool.newChildElement(localElement1, "updateReplace", ((CTMLItem)localObject1).getUpdateReplace());
        if (((CTMLItem)localObject1).getUpdateTransPeriod() != null)
          XMLTool.newChildElement(localElement1, "updateTransPeriod", ((CTMLItem)localObject1).getUpdateTransPeriod());
        if (((CTMLItem)localObject1).getStatus() != null)
          XMLTool.newChildElement(localElement1, "ctmlStatus", ((CTMLItem)localObject1).getStatus());
        XMLTool.newChildElement(localElement1, "defaultValidate", String.valueOf(((CTMLItem)localObject1).getDefaultValidate()));
        XMLTool.newChildElement(localElement1, "maxValidate", String.valueOf(((CTMLItem)localObject1).getMaxValidate()));
        XMLTool.newChildElement(localElement1, "notafterDate", ((CTMLItem)localObject1).getNotafterDate());
        if (((CTMLItem)localObject1).getKeyGenLocation() != null)
          XMLTool.newChildElement(localElement1, "keyGenLocation", ((CTMLItem)localObject1).getKeyGenLocation());
        if (((CTMLItem)localObject1).getDescription() != null)
          XMLTool.newChildElement(localElement1, "ctmlDesc", ((CTMLItem)localObject1).getDescription());
        Object localObject4;
        if (((CTMLItem)localObject1).getBaseDN() != null)
        {
          localObject2 = this.doc.createElement("baseDN");
          localElement1.appendChild((Node)localObject2);
          for (int k = 0; k < ((CTMLItem)localObject1).getBaseDN().size(); k++)
          {
            localObject3 = ((CTMLItem)localObject1).getBaseDN().get(k).toString();
            localObject4 = this.doc.createElement("baseDNInfo");
            ((Element)localObject2).appendChild((Node)localObject4);
            XMLTool.newChildElement((Element)localObject4, "DN", (String)localObject3);
          }
        }
        if (((CTMLItem)localObject1).getStandardExtention() != null)
        {
          localObject2 = ((CTMLItem)localObject1).getStandardExtention();
          Element localElement2 = XMLTool.newChildElement(localElement1, "standardExtensions");
          localObject3 = ((Vector)localObject2).elements();
          while (((Enumeration)localObject3).hasMoreElements())
          {
            localObject4 = XMLTool.newChildElement(localElement2, "extEntry");
            localObject5 = (StandardExtension)((Enumeration)localObject3).nextElement();
            localObject6 = ((StandardExtension)localObject5).getParentName();
            String str1 = ((StandardExtension)localObject5).getParentOID();
            localObject7 = ((StandardExtension)localObject5).getChildName();
            localObject8 = ((StandardExtension)localObject5).getAllowNull();
            str2 = ((StandardExtension)localObject5).getChildEncode();
            if (localObject6 != null)
              XMLTool.newChildElement((Element)localObject4, "parentName", (String)localObject6);
            if (str1 != null)
              XMLTool.newChildElement((Element)localObject4, "parentOID", str1);
            if (localObject7 != null)
              XMLTool.newChildElement((Element)localObject4, "childName", (String)localObject7);
            if (localObject8 != null)
              XMLTool.newChildElement((Element)localObject4, "allowNull", (String)localObject8);
            if (str2 == null)
              continue;
            XMLTool.newChildElement((Element)localObject4, "childEncode", str2);
          }
        }
        if (((CTMLItem)localObject1).getExtention() == null)
          continue;
        localObject2 = this.doc.createElement("selfExt");
        localElement1.appendChild((Node)localObject2);
        for (int m = 0; m < ((CTMLItem)localObject1).getExtention().size(); m++)
        {
          localObject3 = this.doc.createElement("selfExtInfo");
          ((Element)localObject2).appendChild((Node)localObject3);
          localObject4 = (Vector)((CTMLItem)localObject1).getExtention().get(m);
          if (((Vector)localObject4).get(0) != null)
            XMLTool.newChildElement((Element)localObject3, "name", ((Vector)localObject4).get(0).toString());
          if (((Vector)localObject4).get(1) != null)
            XMLTool.newChildElement((Element)localObject3, "oid", ((Vector)localObject4).get(1).toString());
          if (((Vector)localObject4).get(2) != null)
            XMLTool.newChildElement((Element)localObject3, "encoding", ((Vector)localObject4).get(2).toString());
          if (((Vector)localObject4).get(3) != null)
            XMLTool.newChildElement((Element)localObject3, "status", ((Vector)localObject4).get(3).toString());
          if (((Vector)localObject4).get(4) != null)
            XMLTool.newChildElement((Element)localObject3, "userprocess", ((Vector)localObject4).get(4).toString());
          if (((Vector)localObject4).get(5) == null)
            continue;
          XMLTool.newChildElement((Element)localObject3, "desc", ((Vector)localObject4).get(5).toString());
        }
      }
    }
    this.resultContainer = new Vector();
    NodeList localNodeList1 = this.body.getElementsByTagName("entry");
    for (int j = 0; j < localNodeList1.getLength(); j++)
    {
      localObject1 = (Element)localNodeList1.item(j);
      localObject2 = new CTMLItem();
      ((CTMLItem)localObject2).setName(XMLTool.getValueByTagName((Element)localObject1, "ctmlName"));
      ((CTMLItem)localObject2).setId(XMLTool.getValueByTagName((Element)localObject1, "ctmlID"));
      ((CTMLItem)localObject2).setType(XMLTool.getValueByTagName((Element)localObject1, "ctmlType"));
      ((CTMLItem)localObject2).setStatus(XMLTool.getValueByTagName((Element)localObject1, "ctmlStatus"));
      ((CTMLItem)localObject2).setKeytype(XMLTool.getValueByTagName((Element)localObject1, "keyType"));
      ((CTMLItem)localObject2).setKeySpec(XMLTool.getValueByTagName((Element)localObject1, "keySpec"));
      ((CTMLItem)localObject2).setUpdateReplace(XMLTool.getValueByTagName((Element)localObject1, "updateReplace"));
      ((CTMLItem)localObject2).setUpdateTransPeriod(XMLTool.getValueByTagName((Element)localObject1, "updateTransPeriod"));
      ((CTMLItem)localObject2).setKeysize(Integer.parseInt(XMLTool.getValueByTagName((Element)localObject1, "keySize")));
      ((CTMLItem)localObject2).setDefaultValidate(Integer.parseInt(XMLTool.getValueByTagName((Element)localObject1, "defaultValidate")));
      ((CTMLItem)localObject2).setMaxValidate(Integer.parseInt(XMLTool.getValueByTagName((Element)localObject1, "maxValidate")));
      ((CTMLItem)localObject2).setNotafterDate(XMLTool.getValueByTagName((Element)localObject1, "notafterDate"));
      ((CTMLItem)localObject2).setKeyGenLocation(XMLTool.getValueByTagName((Element)localObject1, "keyGenLocation"));
      ((CTMLItem)localObject2).setDescription(XMLTool.getValueByTagName((Element)localObject1, "ctmlDesc"));
      NodeList localNodeList2 = ((Element)localObject1).getElementsByTagName("baseDNInfo");
      localObject3 = new Vector();
      for (int n = 0; n < localNodeList2.getLength(); n++)
      {
        localObject5 = (Element)localNodeList2.item(n);
        localObject6 = XMLTool.getValueByTagName((Element)localObject5, "DN");
        ((Vector)localObject3).add(localObject6);
      }
      ((CTMLItem)localObject2).setBaseDN((Vector)localObject3);
      Vector localVector = getStandardExtensionsFromXML((Element)localObject1);
      ((CTMLItem)localObject2).setStandardExtention(localVector);
      localObject5 = ((Element)localObject1).getElementsByTagName("selfExtInfo");
      localObject6 = new Vector();
      for (int i1 = 0; i1 < ((NodeList)localObject5).getLength(); i1++)
      {
        localObject7 = new Vector();
        localObject8 = (Element)((NodeList)localObject5).item(i1);
        str2 = XMLTool.getValueByTagName((Element)localObject8, "name");
        String str3 = XMLTool.getValueByTagName((Element)localObject8, "oid");
        String str4 = XMLTool.getValueByTagName((Element)localObject8, "desc");
        String str5 = XMLTool.getValueByTagName((Element)localObject8, "encoding");
        String str6 = XMLTool.getValueByTagName((Element)localObject8, "status");
        String str7 = XMLTool.getValueByTagName((Element)localObject8, "userprocess");
        ((Vector)localObject7).add(str2);
        ((Vector)localObject7).add(str3);
        ((Vector)localObject7).add(str5);
        ((Vector)localObject7).add(str6);
        ((Vector)localObject7).add(str7);
        ((Vector)localObject7).add(str4);
        ((Vector)localObject6).add(localObject7);
      }
      ((CTMLItem)localObject2).setExtention((Vector)localObject6);
      this.resultContainer.add(localObject2);
    }
  }

  public Vector getStandardExtensionsFromXML(Element paramElement)
  {
    if (paramElement == null)
      return null;
    NodeList localNodeList1 = paramElement.getElementsByTagName("standardExtensions");
    if (localNodeList1.getLength() == 0)
      return null;
    StandardExtension localStandardExtension = null;
    Vector localVector = new Vector();
    if (localNodeList1.getLength() > 0)
    {
      Element localElement1 = (Element)localNodeList1.item(0);
      NodeList localNodeList2 = XMLTool.getChildElements(localElement1);
      int i = localNodeList2.getLength();
      for (int j = 0; j < i; j++)
      {
        Element localElement2 = (Element)localNodeList2.item(j);
        String str1 = XMLTool.getValueByTagName(localElement2, "parentName");
        String str2 = XMLTool.getValueByTagName(localElement2, "childName");
        String str3 = XMLTool.getValueByTagName(localElement2, "childEncode");
        String str4 = XMLTool.getValueByTagName(localElement2, "parentOID");
        String str5 = XMLTool.getValueByTagName(localElement2, "allowNull");
        localStandardExtension = new StandardExtension();
        localStandardExtension.setAllowNull(str5);
        localStandardExtension.setParentName(str1);
        localStandardExtension.setChildName(str2);
        localStandardExtension.setChildEncode(str3);
        localStandardExtension.setParentOID(str4);
        localVector.add(localStandardExtension);
      }
    }
    return localVector;
  }
}

/* Location:           C:\Program Files\JIT\CA50\lib\IDA\ida.jar
 * Qualified Name:     cn.com.jit.ida.ca.ctml.service.response.RACTMLUpdateResponse
 * JD-Core Version:    0.6.0
 */