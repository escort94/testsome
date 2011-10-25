package cn.com.jit.ida.ca.initKeyCert.template;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.SAXException;




public class Xml2Template {
	
	Document doc = null;
	DocumentBuilder db = null;
	
	/**
	 * 解析XML文件，组成模板信息的类
	 * 
	 * @param xml
	 *            证书模板XML文件
	 * @return 证书模板类
	 * @throws Exception
	 *             XML格式不正确时抛出异常
	 */
	public Object createCertTempateInfoBean(String xml)
			throws Exception {
		try {
			DocumentBuilderFactory dbf = null;
			dbf = DocumentBuilderFactory.newInstance();
			db = dbf.newDocumentBuilder();
			ByteArrayInputStream bi = new ByteArrayInputStream(xml
					.getBytes("utf-8"));
			doc = db.parse(bi);
			doc.normalize();
		} catch (DOMException dom) {
			dom.printStackTrace();
			throw new Exception();
		} catch (IOException ioe) {
			ioe.printStackTrace();
			throw new Exception();
		} catch (SAXException saxe) {
			saxe.printStackTrace();
			throw new Exception();
		}

		NodeList CertTemplateInfoNodes = doc.getElementsByTagName("ctml");
		Node certTemplateInfoNode = CertTemplateInfoNodes.item(0);

//		return createCertTempateInfoBean(certTemplateInfoNode);
		return null;
	}
}
