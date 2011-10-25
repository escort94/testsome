package cn.com.jit.ida.ca.ctml.x509v3.extension.policy;

import cn.com.jit.ida.IDAException;
import cn.com.jit.ida.ca.ctml.x509v3.extension.X509V3ExtensionPolicy;
import cn.com.jit.ida.util.xml.XMLTool;
import org.w3c.dom.Element;

public class ICRegistrationNumberPolicy extends X509V3ExtensionPolicy
{
  private static final String XMLTAG_IC_REG_NUM = "ICRegistrationNum";
  private String icRegistNum = null;
  private String icRegistNumAllowNull = "TRUE";
  private static final String XMLTAG_ALLOW_NULL = "allowNull";
  private static final String XMLTAG_CHECK = "check";

  public ICRegistrationNumberPolicy()
    throws IDAException
  {
    super("ICRegistrationNumber");
  }

  public String getIcRegistNum()
  {
    return this.icRegistNum;
  }

  public void setIcRegistNum(String paramString)
  {
    this.icRegistNum = paramString;
  }

  protected void updateData(boolean paramBoolean)
    throws IDAException
  {
    super.updateData(paramBoolean);
    Element localElement1;
    Element localElement2;
    if (paramBoolean)
    {
      localElement1 = this.xml.newElement("value");
      this.xmlElement.appendChild(localElement1);
      if (this.icRegistNum != null)
      {
        localElement2 = XMLTool.newChildElement(localElement1, "ICRegistrationNum");
        XMLTool.newChildElement(localElement2, "check", this.icRegistNum.trim());
        XMLTool.newChildElement(localElement2, "allowNull", this.icRegistNumAllowNull.trim());
      }
    }
    else
    {
      localElement1 = XMLTool.getElementByTagName(this.xmlElement, "value");
      if (localElement1 != null)
      {
        localElement2 = XMLTool.getElementByTagName(localElement1, "ICRegistrationNum");
        if (localElement2 != null)
        {
          this.icRegistNum = XMLTool.getValueByTagName(localElement2, "check").trim();
          this.icRegistNumAllowNull = XMLTool.getValueByTagName(localElement2, "allowNull").trim();
        }
      }
    }
  }

  public String getIcRegistNumAllowNull()
  {
    return this.icRegistNumAllowNull;
  }

  public void setIcRegistNumAllowNull(String paramString)
  {
    this.icRegistNumAllowNull = paramString;
  }
}

/* Location:           C:\Program Files\JIT\CA50\lib\IDA\ida.jar
 * Qualified Name:     cn.com.jit.ida.ca.ctml.x509v3.extension.policy.ICRegistrationNumberPolicy
 * JD-Core Version:    0.6.0
 */