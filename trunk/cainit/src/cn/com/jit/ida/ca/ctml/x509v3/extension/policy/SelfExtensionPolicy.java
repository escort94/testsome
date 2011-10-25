package cn.com.jit.ida.ca.ctml.x509v3.extension.policy;

import cn.com.jit.ida.IDAException;
import cn.com.jit.ida.ca.ctml.x509v3.extension.X509V3ExtensionPolicy;
import cn.com.jit.ida.util.xml.XMLTool;
import org.w3c.dom.Element;

public class SelfExtensionPolicy extends X509V3ExtensionPolicy
{
  protected static final String XMLTAG_OID = "oid";
  protected static final String XMLTAG_ENCODING = "encoding";
  private String value;

  public SelfExtensionPolicy()
    throws IDAException
  {
    super("userDefineExtension");
  }

  public void setName(String paramString)
  {
    this.name = paramString;
  }

  public void setOID(String paramString)
  {
    this.oid = paramString;
  }

  public void setEncoding(String paramString)
  {
    this.encoding = paramString;
  }

  public String getValue()
  {
    return this.value;
  }

  public void setValue(String paramString)
  {
    this.value = paramString;
  }

  protected void updateData(boolean paramBoolean)
    throws IDAException
  {
    super.updateData(paramBoolean);
    if (paramBoolean)
    {
      Element localElement;
      if (this.oid != null)
      {
        localElement = this.xml.newElement("oid", this.oid);
        this.xmlElement.appendChild(localElement);
      }
      if (this.encoding != null)
      {
        localElement = this.xml.newElement("encoding", this.encoding);
        this.xmlElement.appendChild(localElement);
      }
      if (this.value != null)
      {
        localElement = this.xml.newElement("value", this.value);
        this.xmlElement.appendChild(localElement);
      }
    }
    else
    {
      this.value = XMLTool.getValueByTagName(this.xmlElement, "value");
      this.oid = XMLTool.getValueByTagName(this.xmlElement, "oid");
      this.encoding = XMLTool.getValueByTagName(this.xmlElement, "encoding");
    }
  }
}

/* Location:           C:\Program Files\JIT\CA50\lib\IDA\ida.jar
 * Qualified Name:     cn.com.jit.ida.ca.ctml.x509v3.extension.policy.SelfExtensionPolicy
 * JD-Core Version:    0.6.0
 */