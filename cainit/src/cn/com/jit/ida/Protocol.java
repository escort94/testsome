package cn.com.jit.ida;

import cn.com.jit.ida.util.xml.XMLTool;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public abstract class Protocol
{
  public static final String PROTOCOL = "protocol";
  public static final String HEAD = "header";
  public static final String TYPE = "type";
  public static final String OPERATION = "operation";
  public static final String BODY = "body";
  public static final String ENTRY = "entry";
  protected Document doc;
  protected Element protocol;
  protected Element body;
  protected Element header;
  protected byte[] original_data;

  public final void setData(byte[] paramArrayOfByte)
    throws IDAException
  {
    this.original_data = paramArrayOfByte;
    this.doc = XMLTool.parseDocument(paramArrayOfByte);
    this.protocol = ((Element)this.doc.getFirstChild());
    this.header = XMLTool.getElementByTagName(this.protocol, "header");
    this.body = XMLTool.getElementByTagName(this.protocol, "body");
    updateHeader(false);
    if (this.body != null)
      updateBody(false);
  }

  public final void setData(Document paramDocument)
    throws IDAException
  {
	  if(this.doc != null){
//		  this.doc = paramDocument;
	  }else{
		  this.doc = paramDocument;
	  }
    this.protocol = ((Element)this.doc.getFirstChild());
    this.header = XMLTool.getElementByTagName(this.protocol, "header");
    this.body = XMLTool.getElementByTagName(this.protocol, "body");
    updateHeader(false);
    if (this.body != null)
      updateBody(false);
  }

  public final byte[] getData()
    throws IDAException
  {
    this.doc = XMLTool.newDocument();
    updateHeader(true);
    updateBody(true);
    this.protocol = this.doc.createElement("protocol");
    this.doc.appendChild(this.protocol);
    this.protocol.appendChild(this.header);
    if (this.body != null)
      this.protocol.appendChild(this.body);
    return XMLTool.xmlSerialize(this.doc);
  }

  public final Document getDocument()
    throws IDAException
  {
    if (this.doc != null)
      return this.doc;
    this.doc = XMLTool.newDocument();
    updateHeader(true);
    updateBody(true);
    this.protocol = this.doc.createElement("protocol");
    this.doc.appendChild(this.protocol);
    this.protocol.appendChild(this.header);
    if (this.body != null)
      this.protocol.appendChild(this.body);
    return this.doc;
  }

  public final byte[] getOriginalData()
  {
    return this.original_data;
  }

  protected abstract void updateHeader(boolean paramBoolean)
    throws IDAException;

  protected abstract void updateBody(boolean paramBoolean)
    throws IDAException;
}

/* Location:           C:\Program Files\JIT\CA50\lib\IDA\ida.jar
 * Qualified Name:     cn.com.jit.ida.Protocol
 * JD-Core Version:    0.6.0
 */