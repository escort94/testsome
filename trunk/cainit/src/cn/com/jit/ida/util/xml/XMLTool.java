package cn.com.jit.ida.util.xml;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

public class XMLTool
{
  private Document doc = newDocument();

  public static void main(String[] paramArrayOfString)
  {
    try
    {
      XMLTool localXMLTool = new XMLTool();
      Document localDocument1 = newDocument();
      Element localElement1 = localDocument1.createElement("MYBOOK");
      localDocument1.appendChild(localElement1);
      Element localElement2 = localDocument1.createElement("BOOK");
      localElement1.appendChild(localElement2);
      Element localElement3 = localDocument1.createElement("TITLE");
      Text localText1 = localDocument1.createTextNode("JAVA Book");
      localElement3.appendChild(localText1);
      localElement2.appendChild(localElement3);
      Element localElement4 = localDocument1.createElement("AUTHOR");
      Attr localAttr1 = localDocument1.createAttribute("BORN");
      localAttr1.setValue("1975");
      localElement4.setAttributeNode(localAttr1);
      Text localText2 = localDocument1.createTextNode("AUTHOR_NAME");
      localText2.setData("MACAL");
      localElement4.appendChild(localText2);
      localElement2.appendChild(localElement4);
      Element localElement5 = localDocument1.createElement("BOOK");
      localElement1.appendChild(localElement5);
      Element localElement6 = localDocument1.createElement("TITLE");
      Text localText3 = localDocument1.createTextNode("TITLE_Name");
      localText3.setData("C++ Book");
      localElement6.appendChild(localText3);
      localElement5.appendChild(localElement6);
      Element localElement7 = localDocument1.createElement("AUTHOR");
      Attr localAttr2 = localDocument1.createAttribute("BORN");
      localAttr2.setValue("1976");
      localElement7.setAttributeNode(localAttr2);
      Text localText4 = localDocument1.createTextNode("AUTHOR_NAME");
      localText4.setData("JOHN");
      localElement7.appendChild(localText4);
      localElement5.appendChild(localElement7);
      byte[] arrayOfByte = xmlSerialize(localDocument1);
      System.out.println(new String(arrayOfByte));
      System.out.println("generate ok");
      Document localDocument2 = parseDocument(arrayOfByte);
      arrayOfByte = xmlSerialize(localDocument2);
      System.out.println(new String(arrayOfByte));
      System.out.println("ok");
    }
    catch (Exception localException)
    {
      System.out.println(localException);
    }
  }

  public static Document newDocument()
  {
    DocumentBuilder localDocumentBuilder = null;
    Document localDocument = null;
    try
    {
      localDocumentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
      localDocument = localDocumentBuilder.newDocument();
    }
    catch (Exception localException)
    {
      throw new RuntimeException(localException.toString());
    }
    return localDocument;
  }

  public static Document parseDocument(byte[] paramArrayOfByte)
  {
    Document localDocument = null;
    try
    {
      Transformer localTransformer = TransformerFactory.newInstance().newTransformer();
      localTransformer.setOutputProperty("encoding", "gb2312");
      localTransformer.setOutputProperty("indent", "yes");
      DOMResult localDOMResult = new DOMResult();
      ByteArrayInputStream localByteArrayInputStream = new ByteArrayInputStream(paramArrayOfByte);
      StreamSource localStreamSource = new StreamSource(localByteArrayInputStream);
      localTransformer.transform(localStreamSource, localDOMResult);
      localDocument = (Document)localDOMResult.getNode();
    }
    catch (Exception localException)
    {
      throw new RuntimeException(localException.toString());
    }
    return localDocument;
  }

  public static byte[] xmlSerialize(Document paramDocument)
  {
    byte[] arrayOfByte = null;
    try
    {
      Transformer localTransformer = TransformerFactory.newInstance().newTransformer();
      localTransformer.setOutputProperty("encoding", "gb2312");
      localTransformer.setOutputProperty("indent", "yes");
      DOMSource localDOMSource = new DOMSource(paramDocument);
      ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream();
      StreamResult localStreamResult = new StreamResult(localByteArrayOutputStream);
      localTransformer.transform(localDOMSource, localStreamResult);
      arrayOfByte = localByteArrayOutputStream.toByteArray();
    }
    catch (Exception localException)
    {
      throw new RuntimeException(localException.toString());
    }
    return arrayOfByte;
  }

  public static byte[] xmlSerialize(Element paramElement)
  {
    Document localDocument = newDocument();
    Object localObject = null;
    Node localNode = localDocument.importNode(paramElement, true);
    localDocument.appendChild(localNode);
    return xmlSerialize(localDocument);
  }

  public static String getElementValue(Element paramElement)
  {
    NodeList localNodeList = getChildElements(paramElement, Text.class);
    if (localNodeList.getLength() <= 0)
      return null;
    StringBuffer localStringBuffer = new StringBuffer("");
    for (int i = 0; i < localNodeList.getLength(); i++)
      localStringBuffer.append(((Text)localNodeList.item(i)).getNodeValue());
    return localStringBuffer.toString();
  }

  public static String getValueByTagName(Element paramElement, String paramString)
  {
    NodeList localNodeList = paramElement.getElementsByTagName(paramString);
    if (localNodeList.getLength() <= 0)
      return null;
    Element localElement = (Element)localNodeList.item(0);
    return getElementValue(localElement);
  }

  public static Element getElementByTagName(Element paramElement, String paramString)
  {
    NodeList localNodeList = paramElement.getElementsByTagName(paramString);
    if (localNodeList.getLength() <= 0)
      return null;
    Element localElement = (Element)localNodeList.item(0);
    return localElement;
  }

  public static Element newElement(Document paramDocument, String paramString1, String paramString2)
  {
    Element localElement = newElement(paramDocument, paramString1);
    if (paramString2 != null)
    {
      Text localText = paramDocument.createTextNode(paramString2);
      localElement.appendChild(localText);
    }
    return localElement;
  }

  public static Element newElement(Document paramDocument, String paramString)
  {
    return paramDocument.createElement(paramString);
  }

  public Element newElement(String paramString1, String paramString2)
  {
    Element localElement = newElement(paramString1);
    if (paramString2 != null)
    {
      Text localText = this.doc.createTextNode(paramString2);
      localElement.appendChild(localText);
    }
    return localElement;
  }

  public Element newElement(String paramString)
  {
    return this.doc.createElement(paramString);
  }

  public Element importElement(Element paramElement)
  {
    return (Element)this.doc.importNode(paramElement, true);
  }

  public static NodeList getChildElements(Element paramElement)
  {
    NodeList localNodeList = paramElement.getChildNodes();
    final Node[] arrayOfNode = new Node[localNodeList.getLength() + 1];
    int i = 0;
    for (int j = 0; j < localNodeList.getLength(); j++)
    {
      if (!(localNodeList.item(j) instanceof Element))
        continue;
      arrayOfNode[(i++)] = localNodeList.item(j);
    }
    arrayOfNode[i] = null;
    return new NodeList() {
		private int length;
		private Node[] datas = arrayOfNode;

		public Node item(int paramInt) {
			return this.datas[paramInt];
		}

		public int getLength() {
			return this.length;
		}
	};
  }

  public static Element newChildElement(Element paramElement, String paramString1, String paramString2)
  {
    Document localDocument = paramElement.getOwnerDocument();
    Element localElement = localDocument.createElement(paramString1);
    if (paramString2 != null)
    {
      Text localText = localDocument.createTextNode(paramString2);
      localElement.appendChild(localText);
    }
    paramElement.appendChild(localElement);
    return localElement;
  }

  public static Element newChildElement(Element paramElement, String paramString)
  {
    Document localDocument = paramElement.getOwnerDocument();
    Element localElement = localDocument.createElement(paramString);
    paramElement.appendChild(localElement);
    return localElement;
  }

  private static NodeList getChildElements(Element paramElement,
			Class paramClass) {
		NodeList localNodeList = paramElement.getChildNodes();
		final Node[] arrayOfNode = new Node[localNodeList.getLength() + 1];
		int i = 0;
		for (int j = 0; j < localNodeList.getLength(); j++) {
			if (!paramClass.isInstance(localNodeList.item(j)))
				continue;
			arrayOfNode[(i++)] = localNodeList.item(j);
		}
		arrayOfNode[i] = null;
		return new NodeList() {
			private int length;
			private Node[] datas = arrayOfNode;
			public Node item(int paramInt) {
				return this.datas[paramInt];
			}

			public int getLength() {
				return this.length;
			}
		};
	}
}

/* Location:           C:\Program Files\JIT\CA50\lib\IDA\ida.jar
 * Qualified Name:     cn.com.jit.ida.util.xml.XMLTool
 * JD-Core Version:    0.6.0
 */