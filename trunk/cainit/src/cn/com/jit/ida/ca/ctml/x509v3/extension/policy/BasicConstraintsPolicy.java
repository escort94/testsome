package cn.com.jit.ida.ca.ctml.x509v3.extension.policy;

import cn.com.jit.ida.IDAException;
import cn.com.jit.ida.ca.ctml.x509v3.extension.X509V3ExtensionPolicy;
import cn.com.jit.ida.util.xml.XMLTool;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class BasicConstraintsPolicy extends X509V3ExtensionPolicy
{
  private static final String XMLTAG_CA = "ca";
  private static final String XMLTAG_PATHLEN = "pathLenConstraint";
  private boolean ca = false;
  private int pathLenConstraint = -1;

  public BasicConstraintsPolicy()
    throws IDAException
  {
    super("BasicConstraints");
  }

  public boolean isCA()
  {
    return this.ca;
  }

  public void setCAConstraint(boolean paramBoolean)
  {
    this.ca = paramBoolean;
  }

  public int getPathLenConstraint()
  {
    return this.pathLenConstraint;
  }

  public void setPathLenConstraint(int paramInt)
  {
    this.pathLenConstraint = paramInt;
  }

  protected void updateData(boolean paramBoolean)
    throws IDAException
  {
    super.updateData(paramBoolean);
    Element localElement1;
    Object localObject;
    if (paramBoolean)
    {
      localElement1 = this.xml.newElement("value");
      this.xmlElement.appendChild(localElement1);
      localObject = this.xml.newElement("ca", Boolean.toString(this.ca));
      localElement1.appendChild((Node)localObject);
      if (this.pathLenConstraint >= 0)
      {
        Element localElement2 = this.xml.newElement("pathLenConstraint", Integer.toString(this.pathLenConstraint));
        localElement1.appendChild(localElement2);
      }
    }
    else
    {
      localElement1 = XMLTool.getElementByTagName(this.xmlElement, "value");
      if (localElement1 != null)
      {
        localObject = XMLTool.getValueByTagName(localElement1, "ca");
        if (localObject != null)
          this.ca = new Boolean((String)localObject).booleanValue();
        else
          this.ca = false;
        localObject = XMLTool.getValueByTagName(localElement1, "pathLenConstraint");
        if (localObject != null)
          this.pathLenConstraint = Integer.parseInt((String)localObject);
        else
          this.pathLenConstraint = -1;
      }
    }
  }
}

/* Location:           C:\Program Files\JIT\CA50\lib\IDA\ida.jar
 * Qualified Name:     cn.com.jit.ida.ca.ctml.x509v3.extension.policy.BasicConstraintsPolicy
 * JD-Core Version:    0.6.0
 */