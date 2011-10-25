package cn.com.jit.ida.ca.ctml;

import cn.com.jit.ida.IDAException;
import cn.com.jit.ida.ca.ctml.util.CTMLException;
import cn.com.jit.ida.ca.ctml.util.CTMLException.CTML;
import cn.com.jit.ida.util.xml.XMLTool;
import javax.xml.transform.TransformerException;
import org.w3c.dom.Element;

public abstract class CTMLPolicy
  implements CTMLPolicyInterface
{
  public static final String XMLTAG_CTML = "ctml";
  protected XMLTool xml = new XMLTool();
  protected Element rootNode;
  protected byte[] xmlData;

  public CTMLPolicy()
    throws IDAException
  {
  }

  public CTMLPolicy(byte[] paramArrayOfByte)
    throws IDAException
  {
    this();
    setCTMLPolicyDesc(paramArrayOfByte);
  }

  public void setCTMLPolicyDesc(byte[] paramArrayOfByte)
    throws IDAException
  {
    try
    {
      this.xmlData = paramArrayOfByte;
      updateData(false);
    }
    catch (RuntimeException localRuntimeException)
    {
      if ((localRuntimeException.getCause() instanceof TransformerException))
      {
        CTMLException localCTMLException = new CTMLException(CTMLException.CTML.PARSECTMLPOLICY, (Exception)localRuntimeException.getCause());
        throw localCTMLException;
      }
      throw localRuntimeException;
    }
  }

  public byte[] getCTMLPolicyDesc()
    throws IDAException
  {
    updateData(true);
    return this.xmlData = XMLTool.xmlSerialize(this.rootNode);
  }

  protected abstract void updateData(boolean paramBoolean)
    throws IDAException;

  public abstract void modifyCTMLPolicy(CTMLPolicy paramCTMLPolicy);
}

/* Location:           C:\Program Files\JIT\CA50\lib\IDA\ida.jar
 * Qualified Name:     cn.com.jit.ida.ca.ctml.CTMLPolicy
 * JD-Core Version:    0.6.0
 */