package cn.com.jit.ida.ca.ctml.x509v3.extension.policy;

import cn.com.jit.ida.IDAException;
import cn.com.jit.ida.ca.ctml.x509v3.extension.X509V3ExtensionPolicy;
import cn.com.jit.ida.util.xml.XMLTool;
import org.w3c.dom.Element;

public class IdentifyCodePolicy extends X509V3ExtensionPolicy
{
  private static final String XMLTAG_ID_CARD_NUMBER = "idCardNum";
  private static final String XMLTAG_MILITARY_CARD_NUMBER = "militaryCardNum";
  private static final String XMLTAG_PASS_PORT_NUMBER = "passportNum";
  private static final String XMLTAG_ALLOW_NULL = "allowNull";
  private static final String XMLTAG_CHECK = "check";
  private String idCardNum = null;
  private String militaryCardNum = null;
  private String passportNum = null;
  private String idCardNumAllowNull = "TRUE";
  private String militaryCardNumAllowNull = "TRUE";
  private String passportNumAllowNull = "TRUE";

  public IdentifyCodePolicy()
    throws IDAException
  {
    super("IdentifyCode");
  }

  public String getIdCardNum()
  {
    return this.idCardNum;
  }

  public String getMilitaryCardNum()
  {
    return this.militaryCardNum;
  }

  public String getPassportNum()
  {
    return this.passportNum;
  }

  public String getIdCardNumAllowNull()
  {
    return this.idCardNumAllowNull;
  }

  public String getMilitaryCardNumAllowNull()
  {
    return this.militaryCardNumAllowNull;
  }

  public String getPassportNumAllowNull()
  {
    return this.passportNumAllowNull;
  }

  public void setIdCardNum(String paramString)
  {
    this.idCardNum = paramString;
  }

  public void setMilitaryCardNum(String paramString)
  {
    this.militaryCardNum = paramString;
  }

  public void setPassportNum(String paramString)
  {
    this.passportNum = paramString;
  }

  public void setIdCardNumAllowNull(String paramString)
  {
    this.idCardNumAllowNull = paramString;
  }

  public void setMilitaryCardNumAllowNull(String paramString)
  {
    this.militaryCardNumAllowNull = paramString;
  }

  public void setPassportNumAllowNull(String paramString)
  {
    this.passportNumAllowNull = paramString;
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
      if (this.idCardNum != null)
      {
        localElement2 = XMLTool.newChildElement(localElement1, "idCardNum");
        XMLTool.newChildElement(localElement2, "check", this.idCardNum.trim());
        XMLTool.newChildElement(localElement2, "allowNull", this.idCardNumAllowNull.trim());
      }
      if (this.militaryCardNum != null)
      {
        localElement2 = XMLTool.newChildElement(localElement1, "militaryCardNum");
        XMLTool.newChildElement(localElement2, "check", this.militaryCardNum.trim());
        XMLTool.newChildElement(localElement2, "allowNull", this.militaryCardNumAllowNull.trim());
      }
      if (this.passportNum != null)
      {
        localElement2 = XMLTool.newChildElement(localElement1, "passportNum");
        XMLTool.newChildElement(localElement2, "check", this.passportNum.trim());
        XMLTool.newChildElement(localElement2, "allowNull", this.passportNumAllowNull.trim());
      }
    }
    else
    {
      localElement1 = XMLTool.getElementByTagName(this.xmlElement, "value");
      if (localElement1 != null)
      {
        localElement2 = XMLTool.getElementByTagName(localElement1, "idCardNum");
        if (localElement2 != null)
        {
          this.idCardNum = XMLTool.getValueByTagName(localElement2, "check").trim();
          this.idCardNumAllowNull = XMLTool.getValueByTagName(localElement2, "allowNull").trim();
        }
        Element localElement3 = XMLTool.getElementByTagName(localElement1, "militaryCardNum");
        if (localElement3 != null)
        {
          this.militaryCardNum = XMLTool.getValueByTagName(localElement3, "check").trim();
          this.militaryCardNumAllowNull = XMLTool.getValueByTagName(localElement3, "allowNull").trim();
        }
        Element localElement4 = XMLTool.getElementByTagName(localElement1, "passportNum");
        if (localElement4 != null)
        {
          this.passportNum = XMLTool.getValueByTagName(localElement4, "check").trim();
          this.passportNumAllowNull = XMLTool.getValueByTagName(localElement4, "allowNull").trim();
        }
      }
    }
  }
}

/* Location:           C:\Program Files\JIT\CA50\lib\IDA\ida.jar
 * Qualified Name:     cn.com.jit.ida.ca.ctml.x509v3.extension.policy.IdentifyCodePolicy
 * JD-Core Version:    0.6.0
 */