package cn.com.jit.ida.ca.ctml.x509v3.extension.policy;

import cn.com.jit.ida.IDAException;
import cn.com.jit.ida.ca.ctml.x509v3.extension.X509V3ExtensionPolicy;
import cn.com.jit.ida.util.xml.XMLTool;

public class PolicyConstraintsPolicy extends X509V3ExtensionPolicy
{
  private static String XMLTAG_POLICY_CONSTRAINTS = "policyConstraints";
  private static final String XMLTAG_ALLOW_NULL = "allowNull";
  private static final String XMLTAG_CHECK = "check";
  private String policyConstraintsAllowNull = "TRUE";

  public PolicyConstraintsPolicy()
    throws IDAException
  {
    super("PolicyConstrants");
  }

  protected void updateData(boolean paramBoolean)
    throws IDAException
  {
    super.updateData(paramBoolean);
    if (paramBoolean)
    {
      if ((this.policyConstraintsAllowNull != null) && (!this.policyConstraintsAllowNull.equals("")))
        XMLTool.newChildElement(this.xmlElement, "allowNull", this.policyConstraintsAllowNull.trim());
    }
    else
      this.policyConstraintsAllowNull = XMLTool.getValueByTagName(this.xmlElement, "allowNull").trim();
  }

  public String getPolicyConstraintsAllowNull()
  {
    return this.policyConstraintsAllowNull;
  }

  public void setPolicyConstraintsAllowNull(String paramString)
  {
    this.policyConstraintsAllowNull = paramString;
  }
}

/* Location:           C:\Program Files\JIT\CA50\lib\IDA\ida.jar
 * Qualified Name:     cn.com.jit.ida.ca.ctml.x509v3.extension.policy.PolicyConstraintsPolicy
 * JD-Core Version:    0.6.0
 */