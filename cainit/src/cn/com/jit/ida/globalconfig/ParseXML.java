package cn.com.jit.ida.globalconfig;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.StringTokenizer;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

public class ParseXML {
	public static final int Encrypted = -1;
	public static final int NoEncrypted = 1;
	public static final int NeverEncrypted = 0;
	public static final String DELIM = "||";
	private Document doc;
	private String fileName = null;
	private final String cryptoAttr = "isEncrypted";

	public ParseXML(Document paramDocument) {
		this.doc = paramDocument;
	}

	public ParseXML(String paramString) throws ConfigException {
		this.fileName = paramString;
		try {
			DocumentBuilder localDocumentBuilder = DocumentBuilderFactory
					.newInstance().newDocumentBuilder();
			this.doc = localDocumentBuilder.parse(paramString);
		} catch (Exception localException) {
			ConfigException localConfigException;
			if ((localException instanceof IOException))
				localConfigException = new ConfigException(
						ConfigException.READ_FILE_ERROR, "读配置错误",
						localException);
			else
				localConfigException = new ConfigException(
						ConfigException.CONFIG_ERROR, "配置错误", localException);
			localConfigException.appendMsg(this.fileName);
			throw localConfigException;
		}
	}

	public ParseXML(byte[] paramArrayOfByte) throws ConfigException {
		try {
			DocumentBuilder localDocumentBuilder = DocumentBuilderFactory
					.newInstance().newDocumentBuilder();
			this.doc = localDocumentBuilder.parse(new ByteArrayInputStream(
					paramArrayOfByte));
		} catch (Exception localException) {
			throw new ConfigException(ConfigException.CONFIG_ERROR, "配置错误");
		}
	}

	public String getString(String paramString) {
		String str = "";
		Element localElement = getElement(paramString);
		if (localElement == null)
			return "";
		Text localText = (Text) localElement.getFirstChild();
		if (localText != null)
			str = localText.getData();
		if ((str.equalsIgnoreCase("")) || (str == null))
			return "";
		if (localElement.hasAttribute("isEncrypted"))
			if (localElement.getAttribute("isEncrypted").toString()
					.equalsIgnoreCase("true"))
				try {
					str = ProtectConfig.getInstance().Decrypto(str);
				} catch (ConfigException localConfigException1) {
					str = "";
				}
			else
				try {
					setString(paramString, str);
				} catch (ConfigException localConfigException2) {
				}
		return str;
	}

	public int CryptoProperty(String paramString) {
		Element localElement = getElement(paramString);
		if (localElement == null)
			return 0;
		if (localElement.hasAttribute("isEncrypted")) {
			if (localElement.getAttribute("isEncrypted").toString()
					.equalsIgnoreCase("true"))
				return -1;
			return 1;
		}
		return 0;
	}

	public Element getElement(String paramString) {
		StringTokenizer localStringTokenizer = new StringTokenizer(paramString,
				"||");
		Element localElement1 = null;
		Element localElement2 = null;
		String str;
		if (localStringTokenizer.hasMoreTokens()) {
			str = localStringTokenizer.nextToken();
			NodeList localNodeList = this.doc.getElementsByTagName(str);
			localElement1 = (Element) localNodeList.item(0);
			if (!localStringTokenizer.hasMoreTokens()) {
				localElement2 = localElement1;
				return localElement2;
			}
		}
		while (localStringTokenizer.hasMoreTokens()) {
			str = localStringTokenizer.nextToken();
			localElement1 = (Element) localElement1.getElementsByTagName(str)
					.item(0);
			if (localStringTokenizer.hasMoreTokens())
				continue;
			localElement2 = localElement1;
			return localElement2;
		}
		return localElement2;
	}

	public void setString(String paramString1, String paramString2)
			throws ConfigException {
		StringTokenizer localStringTokenizer = new StringTokenizer(
				paramString1, "||");
		for (String str = null; localStringTokenizer.hasMoreElements(); str = localStringTokenizer
				.nextToken()) {
			Text text = this.doc.createTextNode(str);
			Element localElement = getElement(paramString1);
			if (localElement == null) {
				ConfigException localObject = new ConfigException(
						ConfigException.CONFIG_STRUCT_ERROR, "配置文件结构错误");
				((ConfigException) localObject).appendMsg(paramString1);
				throw localObject;
			}
			if (localElement.hasAttribute("isEncrypted")) {
				if (!localElement.getAttribute("isEncrypted").equalsIgnoreCase(
						"true"))
					localElement.setAttribute("isEncrypted", "TRUE");
				if (!paramString2.equalsIgnoreCase(""))
					paramString2 = ProtectConfig.getInstance().Encrypto(
							paramString2);
			}
			((Text) text).setData(paramString2);
			if (localElement.getFirstChild() == null)
				localElement.appendChild((Node) text);
			else
				localElement.replaceChild((Node) text, localElement
						.getFirstChild());
			if (this.fileName != null)
				SavaToFile(this.fileName);
		}
	}

	public int getNumber(String paramString) throws ConfigException {
		String str = getString(paramString);
		int i = -1;
		try {
			i = Integer.parseInt(str);
		} catch (Exception localException) {
			throw new ConfigException(ConfigException.DATA_TYPE_ERROR, "<"
					+ paramString + ">值配置非法");
		}
		if (i < 0)
			throw new ConfigException(ConfigException.DATA_TYPE_ERROR, "<"
					+ paramString + ">值配置不正确(要求>=0)");
		return i;
	}

	public void setNumber(String paramString, int paramInt)
			throws ConfigException {
		String str = Integer.toString(paramInt);
		setString(paramString, str);
	}

	public long getLong(String paramString) throws ConfigException {
		String str = getString(paramString);
		long l = -1L;
		try {
			l = Long.parseLong(str);
		} catch (Exception localException) {
			throw new ConfigException(ConfigException.DATA_TYPE_ERROR, "<"
					+ paramString + ">值配置非法");
		}
		if (l < 0L)
			throw new ConfigException(ConfigException.DATA_TYPE_ERROR, "<"
					+ paramString + ">值配置不正确(要求>=0)");
		return l;
	}

	public void setLong(String paramString, long paramLong)
			throws ConfigException {
		String str = Long.toString(paramLong);
		setString(paramString, str);
	}

	public byte[] ToBits() {
		Transformer localTransformer = null;
		try {
			localTransformer = TransformerFactory.newInstance()
					.newTransformer();
		} catch (Exception localException1) {
			return null;
		}
		localTransformer.setOutputProperty("encoding", "GBK");
		DOMSource localDOMSource = new DOMSource(this.doc);
		ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream();
		StreamResult localStreamResult = new StreamResult(
				localByteArrayOutputStream);
		try {
			localTransformer.transform(localDOMSource, localStreamResult);
		} catch (Exception localException2) {
			return null;
		}
		byte[] arrayOfByte = localByteArrayOutputStream.toByteArray();
		return arrayOfByte;
	}

	public void SavaToFile(String paramString) throws ConfigException {
		byte[] arrayOfByte = ToBits();
		FileOutputStream localFileOutputStream = null;
		try {
			localFileOutputStream = new FileOutputStream(paramString);
			localFileOutputStream.write(arrayOfByte);
		} catch (Exception localException) {
			ConfigException localConfigException = new ConfigException(
					ConfigException.WRITE_FILE_ERROR, "写文件错误", localException);
			localConfigException.appendMsg(paramString);
			throw localConfigException;
		}
	}

	public String ComposeStr(String[] paramArrayOfString) {
		int i = 0;
		String str = "";
		if (paramArrayOfString.length == 0)
			str = "";
		else
			for (i = 0; i < paramArrayOfString.length; i++) {
				str = str + paramArrayOfString[i];
				if (i == paramArrayOfString.length - 1)
					continue;
				str = str + "||";
			}
		return str;
	}
}

/*
 * Location: C:\Program Files\JIT\CA50\lib\IDA\ida.jar Qualified Name:
 * cn.com.jit.ida.globalconfig.ParseXML JD-Core Version: 0.6.0
 */