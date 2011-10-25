package cn.com.jit.ida.ca.config;

import cn.com.jit.ida.IDAException;
import cn.com.jit.ida.Response;
import cn.com.jit.ida.ca.initserver.InitServerException;
import cn.com.jit.ida.globalconfig.ConfigTool;
import cn.com.jit.ida.util.xml.XMLTool;
import java.io.PrintStream;
import java.util.Enumeration;
import java.util.Properties;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class GetConfigResponse extends Response
{
  private Properties properties = new Properties();

  public GetConfigResponse()
  {
    super.setOperation("SYETEMVIEWCONFIG");
    GlobalConfig localGlobalConfig = null;
    try
    {
      localGlobalConfig = GlobalConfig.getInstance();
      CAConfig localCAConfig = localGlobalConfig.getCAConfig();
      this.properties.put("AuthorityInfoAccess", localCAConfig.getAuthorityInformation());
      this.properties.put("TimeDifAllow", Long.toString(localCAConfig.getTimeDifAllow() / 60000L));
      this.properties.put("isSendAuthCode", localCAConfig.getIsSendAuthCode());
      localObject = localGlobalConfig.getCrlConfig();
      this.properties.put("CRLPeriods", Long.toString(((CRLConfig)localObject).getPeriods() / 60000L));
      this.properties.put("CRLPubAddress", ConfigTool.Array2String(((CRLConfig)localObject).getCRLPubAddress()));
      LOGConfig localLOGConfig = localGlobalConfig.getLOGConfig();
      this.properties.put("LOG_Path", localLOGConfig.getLOG_Path());
      super.setMsg("success");
      super.setErr("0");
    }
    catch (IDAException localIDAException)
    {
      Object localObject = new InitServerException(localIDAException.getErrCode(), localIDAException.getErrDesc());
      super.setErr("err");
      super.setMsg(((InitServerException)localObject).toString());
      System.err.println(((InitServerException)localObject).toString());
      return;
    }
  }

  public Properties getProperties()
  {
    return this.properties;
  }

  protected void updateBody(boolean paramBoolean)
    throws IDAException
  {
    Object localObject3;
    if (paramBoolean)
    {
      this.body = this.doc.createElement("body");
      localObject1 = this.doc.createElement("entry");
      this.body.appendChild((Node)localObject1);
      Enumeration localEnumeration = this.properties.keys();
      while (localEnumeration.hasMoreElements())
      {
        localObject3 = (String)localEnumeration.nextElement();
        localObject2 = XMLTool.newElement(this.doc, (String)localObject3, (String)this.properties.get(localObject3));
        ((Element)localObject1).appendChild((Node)localObject2);
      }
    }
    Object localObject1 = this.body.getElementsByTagName("entry");
    int i = ((NodeList)localObject1).getLength();
    if (i == 0)
      return;
    Object localObject2 = XMLTool.getChildElements(this.body);
    for (int j = 0; j < ((NodeList)localObject2).getLength(); j++)
    {
      localObject3 = (Element)((NodeList)localObject2).item(j);
      this.properties.setProperty(((Element)localObject3).getLocalName(), XMLTool.getElementValue((Element)localObject3));
    }
  }
}

/* Location:           C:\Program Files\JIT\CA50\lib\IDA\ida.jar
 * Qualified Name:     cn.com.jit.ida.ca.config.GetConfigResponse
 * JD-Core Version:    0.6.0
 */