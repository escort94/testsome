package cn.com.jit.ida.ca.ctml.x509v3.extension.policy;

import cn.com.jit.ida.IDAException;
import cn.com.jit.ida.ca.ctml.x509v3.extension.X509V3ExtensionPolicy;
import cn.com.jit.ida.util.xml.XMLTool;

public class PolicyMappingsPolicy extends X509V3ExtensionPolicy
{
  private static String XMLTAG_POLICY_MAPPINGS = "policyMappings";
  private static final String XMLTAG_ALLOW_NULL = "allowNull";
  private String policyMappingsAllowNull = "TRUE";

  public PolicyMappingsPolicy()
    throws IDAException
  {
    super("PolicyMappings");
  }

  protected void updateData(boolean paramBoolean)
    throws IDAException
  {
    super.updateData(paramBoolean);
    if (paramBoolean)
    {
      if ((this.policyMappingsAllowNull != null) && (!this.policyMappingsAllowNull.equals("")))
        XMLTool.newChildElement(this.xmlElement, "allowNull", this.policyMappingsAllowNull.trim());
    }
    else
      this.policyMappingsAllowNull = XMLTool.getValueByTagName(this.xmlElement, "allowNull").trim();
  }

  public String getPolicyMappingsAllowNull()
  {
    return this.policyMappingsAllowNull;
  }

  public void setPolicyMappingsAllowNull(String paramString)
  {
    this.policyMappingsAllowNull = paramString;
  }
}

/* Location:           C:\Program Files\JIT\CA50\lib\IDA\ida.jar
 * Qualified Name:     cn.com.jit.ida.ca.ctml.x509v3.extension.policy.PolicyMappingsPolicy
 * JD-Core Version:    0.6.0
 */