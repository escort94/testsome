package cn.com.jit.ida.ca.config;

import cn.com.jit.ida.IDAException;
import cn.com.jit.ida.Request;
import cn.com.jit.ida.util.xml.XMLTool;
import java.util.Enumeration;
import java.util.Properties;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class SetConfigRequest extends Request
{
  private Properties properties;

  public SetConfigRequest()
  {
    super.setOperation("SYSTEMMODIFYCONFIG");
    this.properties = new Properties();
  }

  public Properties getProperties()
  {
    return this.properties;
  }

  protected final void updateBody(boolean paramBoolean)
    throws IDAException
  {
    Object localObject2;
    if (paramBoolean)
    {
      this.body = this.doc.createElement("body");
      localObject1 = this.doc.createElement("entry");
      this.body.appendChild((Node)localObject1);
      localObject2 = this.properties.keys();
      while (((Enumeration)localObject2).hasMoreElements())
      {
        String str = (String)((Enumeration)localObject2).nextElement();
        Element localElement = XMLTool.newElement(this.doc, str, (String)this.properties.get(str));
        ((Element)localObject1).appendChild(localElement);
      }
    }
    Object localObject1 = XMLTool.getChildElements(this.body);
    for (int i = 0; i < ((NodeList)localObject1).getLength(); i++)
    {
      localObject2 = (Element)((NodeList)localObject1).item(i);
      this.properties.setProperty(((Element)localObject2).getLocalName(), XMLTool.getElementValue((Element)localObject2));
    }
  }
}

/* Location:           C:\Program Files\JIT\CA50\lib\IDA\ida.jar
 * Qualified Name:     cn.com.jit.ida.ca.config.SetConfigRequest
 * JD-Core Version:    0.6.0
 */