package cn.com.jit.ida.privilege.request;

import cn.com.jit.ida.IDAException;
import cn.com.jit.ida.Request;
import cn.com.jit.ida.privilege.PrivilegeException;
import cn.com.jit.ida.util.xml.XMLTool;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

public class SetTemplateAdminRequest extends Request
{
  private String adminSN = null;
  private String adminDN = null;
  private boolean isRa = false;
  private String[] ctmlName = null;
  private String[] dn = null;
  private String[] reserve = null;

  public SetTemplateAdminRequest()
  {
    super.setOperation("PRIVILEGESETTEMPLATEADMIN");
  }

  public SetTemplateAdminRequest(Request paramRequest)
    throws PrivilegeException
  {
    try
    {
      super.setData(paramRequest.getDocument());
    }
    catch (Exception localException)
    {
      throw new PrivilegeException("80510505", "证书业务管理员授权:请求信息格式不合法");
    }
  }

  public SetTemplateAdminRequest(byte[] paramArrayOfByte)
    throws PrivilegeException
  {
    try
    {
      super.setData(paramArrayOfByte);
    }
    catch (Exception localException)
    {
      throw new PrivilegeException("80510505", "证书业务管理员授权:请求信息格式不合法");
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
      Element localElement1;
      if (this.adminSN != null)
      {
        localElement1 = XMLTool.newElement(this.doc, "TemplateAdminSN", this.adminSN);
        ((Element)localObject1).appendChild(localElement1);
      }
      if (this.adminDN != null)
      {
        localElement1 = XMLTool.newElement(this.doc, "AdminDN", this.adminDN);
        ((Element)localObject1).appendChild(localElement1);
      }
      if (!this.isRa)
      {
        localElement1 = XMLTool.newElement(this.doc, "ISRA", "false");
        ((Element)localObject1).appendChild(localElement1);
      }
      else
      {
        localElement1 = XMLTool.newElement(this.doc, "ISRA", "true");
        ((Element)localObject1).appendChild(localElement1);
      }
      int i;
      Element localElement2;
      if (this.ctmlName != null)
        for (i = 0; i < this.ctmlName.length; i++)
        {
          localElement2 = XMLTool.newElement(this.doc, "CtmlName", this.ctmlName[i]);
          ((Element)localObject1).appendChild(localElement2);
        }
      if (this.dn != null)
        for (i = 0; i < this.ctmlName.length; i++)
        {
          localElement2 = XMLTool.newElement(this.doc, "DN", this.dn[i]);
          ((Element)localObject1).appendChild(localElement2);
        }
      if (this.reserve != null)
        for (i = 0; i < this.ctmlName.length; i++)
        {
          localElement2 = XMLTool.newElement(this.doc, "Reserve", this.reserve[i]);
          ((Element)localObject1).appendChild(localElement2);
        }
    }
    else
    {
      this.adminSN = XMLTool.getValueByTagName(this.body, "TemplateAdminSN");
      this.adminDN = XMLTool.getValueByTagName(this.body, "AdminDN");
      localObject1 = XMLTool.getValueByTagName(this.body, "ISRA");
      if (((String)localObject1).equalsIgnoreCase("true"))
        this.isRa = true;
      else
        this.isRa = false;
      NodeList localNodeList1 = this.body.getElementsByTagName("CtmlName");
      this.ctmlName = new String[localNodeList1.getLength()];
      Object localObject2;
      for (int j = 0; j < localNodeList1.getLength(); j++)
      {
        Element localElement3 = (Element)localNodeList1.item(j);
        localObject2 = (Text)localElement3.getFirstChild();
        this.ctmlName[j] = ((Text)localObject2).getData();
      }
      NodeList localNodeList2 = this.body.getElementsByTagName("DN");
      this.dn = new String[localNodeList2.getLength()];
      Object localObject3;
      for (int k = 0; k < localNodeList2.getLength(); k++)
      {
        localObject2 = (Element)localNodeList2.item(k);
        localObject3 = (Text)((Element)localObject2).getFirstChild();
        this.dn[k] = ((Text)localObject3).getData();
      }
      NodeList localNodeList3 = this.body.getElementsByTagName("Reserve");
      this.reserve = new String[localNodeList3.getLength()];
      for (int m = 0; m < localNodeList3.getLength(); m++)
      {
        localObject3 = (Element)localNodeList3.item(m);
        Text localText = (Text)((Element)localObject3).getFirstChild();
        this.reserve[m] = localText.getData();
      }
    }
  }

  public String getAdminSN()
  {
    return this.adminSN;
  }

  public void setAdminSN(String paramString)
  {
    this.adminSN = paramString;
  }

  public String[] getCtmlName()
  {
    return this.ctmlName;
  }

  public void setCtmlName(String[] paramArrayOfString)
  {
    this.ctmlName = paramArrayOfString;
  }

  public String[] getDN()
  {
    return this.dn;
  }

  public void setDN(String[] paramArrayOfString)
  {
    this.dn = paramArrayOfString;
  }

  public String[] getReserve()
  {
    return this.reserve;
  }

  public void setReserve(String[] paramArrayOfString)
  {
    this.reserve = paramArrayOfString;
  }

  public String getAdminDN()
  {
    return this.adminDN;
  }

  public void setAdminDN(String paramString)
  {
    this.adminDN = paramString;
  }

  public boolean getIsRa()
  {
    return this.isRa;
  }

  public void setIsRa(boolean paramBoolean)
  {
    this.isRa = paramBoolean;
  }
}

/* Location:           C:\Program Files\JIT\CA50\lib\IDA\ida.jar
 * Qualified Name:     cn.com.jit.ida.privilege.request.SetTemplateAdminRequest
 * JD-Core Version:    0.6.0
 */