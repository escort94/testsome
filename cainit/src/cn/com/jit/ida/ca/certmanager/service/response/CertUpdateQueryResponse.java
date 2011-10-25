package cn.com.jit.ida.ca.certmanager.service.response;

import cn.com.jit.ida.IDAException;
import cn.com.jit.ida.Response;
import cn.com.jit.ida.ca.certmanager.CertException;
import cn.com.jit.ida.ca.certmanager.reqinfo.CertExtensions;
import cn.com.jit.ida.ca.certmanager.reqinfo.CertInfo;
import cn.com.jit.ida.util.xml.XMLTool;
import java.util.Hashtable;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class CertUpdateQueryResponse extends Response
{
  private CertInfo[] certInfos = null;
  private String totalRowCount = null;
  private CertExtensions extensions = null;
  private Hashtable standardExtensions = null;

  public CertUpdateQueryResponse()
  {
    super.setOperation("CERTUPDATEQUERY");
  }

  public CertUpdateQueryResponse(byte[] paramArrayOfByte)
    throws IDAException
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

  public CertUpdateQueryResponse(Response paramResponse)
    throws IDAException
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

  public void setCertInfo(CertInfo[] paramArrayOfCertInfo)
  {
    this.certInfos = paramArrayOfCertInfo;
  }

  public CertInfo[] getCertInfo()
  {
    return this.certInfos;
  }

  public CertExtensions getExtensions()
  {
    return this.extensions;
  }

  public void setExtensions(CertExtensions paramCertExtensions)
  {
    this.extensions = paramCertExtensions;
  }

  public void setStandardExtensions(Hashtable paramHashtable)
  {
    this.standardExtensions = paramHashtable;
  }

  public Hashtable getStandardExtensions()
  {
    return this.standardExtensions;
  }

  protected void updateBody(boolean paramBoolean)
    throws IDAException
  {
    Object localObject2;
    Element localElement1;
    Object localObject3;
    Object localObject4;
    Object localObject5;
    Object localObject6;
    if (paramBoolean)
    {
      if ((!getErr().equals("0")) || (this.totalRowCount == null))
        return;
      this.body = this.doc.createElement("body");
      localObject1 = XMLTool.newElement(this.doc, "totalCount", this.totalRowCount);
      this.body.appendChild((Node)localObject1);
      for (i = 0; i < this.certInfos.length; i++)
      {
        CertInfo localCertInfo = this.certInfos[i];
        localObject2 = this.doc.createElement("entry");
        this.body.appendChild((Node)localObject2);
        localElement1 = XMLTool.newElement(this.doc, "certSN", localCertInfo.getCertSN());
        ((Element)localObject2).appendChild(localElement1);
        localObject3 = XMLTool.newElement(this.doc, "authCode", localCertInfo.getAuthCode());
        ((Element)localObject2).appendChild((Node)localObject3);
        localObject4 = XMLTool.newElement(this.doc, "subject", localCertInfo.getSubject());
        ((Element)localObject2).appendChild((Node)localObject4);
        localObject5 = XMLTool.newElement(this.doc, "ctmlName", localCertInfo.getCtmlName());
        ((Element)localObject2).appendChild((Node)localObject5);
        localObject6 = XMLTool.newElement(this.doc, "applicant", localCertInfo.getApplicant());
        ((Element)localObject2).appendChild((Node)localObject6);
        Element localElement2 = XMLTool.newElement(this.doc, "certStatus", localCertInfo.getCertStatus());
        ((Element)localObject2).appendChild(localElement2);
        Element localElement3 = XMLTool.newElement(this.doc, "validity", Integer.toString(localCertInfo.getValidity()));
        ((Element)localObject2).appendChild(localElement3);
        Element localElement4 = XMLTool.newElement(this.doc, "notBefore", Long.toString(localCertInfo.getNotBefore()));
        ((Element)localObject2).appendChild(localElement4);
        Element localElement5 = XMLTool.newElement(this.doc, "notAfter", Long.toString(localCertInfo.getNotAfter()));
        ((Element)localObject2).appendChild(localElement5);
        Element localElement6 = XMLTool.newElement(this.doc, "createTime", Long.toString(localCertInfo.getCreateTime()));
        ((Element)localObject2).appendChild(localElement6);
        Element localElement7 = XMLTool.newElement(this.doc, "cdpid", Long.toString(localCertInfo.getCdpid()));
        ((Element)localObject2).appendChild(localElement7);
        if ((localCertInfo.getEmail() != null) && (localCertInfo.getEmail().trim().length() != 0))
        {
          localElement8 = XMLTool.newElement(this.doc, "email", localCertInfo.getEmail());
          ((Element)localObject2).appendChild(localElement8);
        }
        if ((localCertInfo.getRemark() != null) && (localCertInfo.getRemark().trim().length() != 0))
        {
          localElement8 = XMLTool.newElement(this.doc, "remark", localCertInfo.getRemark());
          ((Element)localObject2).appendChild(localElement8);
        }
        if ((localCertInfo.getRevokeReason() != null) && (!localCertInfo.getRevokeReason().trim().equals("")))
        {
          localElement8 = XMLTool.newElement(this.doc, "revokeReason", localCertInfo.getRevokeReason());
          ((Element)localObject2).appendChild(localElement8);
        }
        if ((localCertInfo.getRevokeDesc() == null) || (localCertInfo.getRevokeDesc().trim().equals("")))
          continue;
        Element localElement8 = XMLTool.newElement(this.doc, "revokeDesc", localCertInfo.getRevokeDesc());
        ((Element)localObject2).appendChild(localElement8);
      }
    }
    this.totalRowCount = XMLTool.getValueByTagName(this.body, "totalCount");
    Object localObject1 = this.body.getElementsByTagName("entry");
    int i = ((NodeList)localObject1).getLength();
    if (i == 0)
      return;
    this.certInfos = new CertInfo[i];
    for (int j = 0; j < i; j++)
    {
      localObject2 = new CertInfo();
      localElement1 = (Element)((NodeList)localObject1).item(j);
      ((CertInfo)localObject2).setCertSN(XMLTool.getValueByTagName(localElement1, "certSN"));
      ((CertInfo)localObject2).setAuthCode(XMLTool.getValueByTagName(localElement1, "authCode"));
      ((CertInfo)localObject2).setSubject(XMLTool.getValueByTagName(localElement1, "subject"));
      ((CertInfo)localObject2).setCtmlName(XMLTool.getValueByTagName(localElement1, "ctmlName"));
      ((CertInfo)localObject2).setApplicant(XMLTool.getValueByTagName(localElement1, "applicant"));
      ((CertInfo)localObject2).setCertStatus(XMLTool.getValueByTagName(localElement1, "certStatus"));
      ((CertInfo)localObject2).setValidity(Integer.parseInt(XMLTool.getValueByTagName(localElement1, "validity")));
      ((CertInfo)localObject2).setNotBefore(Long.parseLong(XMLTool.getValueByTagName(localElement1, "notBefore")));
      ((CertInfo)localObject2).setNotAfter(Long.parseLong(XMLTool.getValueByTagName(localElement1, "notAfter")));
      ((CertInfo)localObject2).setCdpid(Long.parseLong(XMLTool.getValueByTagName(localElement1, "cdpid")));
      ((CertInfo)localObject2).setCreateTime(Long.parseLong(XMLTool.getValueByTagName(localElement1, "createTime")));
      localObject3 = XMLTool.getValueByTagName(localElement1, "email");
      if ((localObject3 != null) && (((String)localObject3).trim().length() != 0))
        ((CertInfo)localObject2).setEmail((String)localObject3);
      localObject4 = XMLTool.getValueByTagName(localElement1, "remark");
      if ((localObject4 != null) && (((String)localObject4).trim().length() != 0))
        ((CertInfo)localObject2).setRemark((String)localObject4);
      localObject5 = XMLTool.getValueByTagName(localElement1, "revokeReason");
      if ((localObject5 != null) && (!((String)localObject5).trim().equals("")))
        ((CertInfo)localObject2).setRevokeReason((String)localObject5);
      localObject6 = XMLTool.getValueByTagName(localElement1, "revokeDesc");
      if ((localObject6 != null) && (!((String)localObject6).trim().equals("")))
        ((CertInfo)localObject2).setRevokeDesc((String)localObject6);
      this.certInfos[j] = localObject2;
    }
  }

  public String getTotalRowCount()
  {
    return this.totalRowCount;
  }

  public void setTotalRowCount(String paramString)
  {
    this.totalRowCount = paramString;
  }
}

/* Location:           C:\Program Files\JIT\CA50\lib\IDA\ida.jar
 * Qualified Name:     cn.com.jit.ida.ca.certmanager.service.response.CertUpdateQueryResponse
 * JD-Core Version:    0.6.0
 */