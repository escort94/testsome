package jdom;

import java.io.IOException;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;

public class CreateXmlFile {
	public CreateXmlFile() {
	}

	public static void main(String[] args) throws IOException {
		org.jdom.Document document = new Document();
		org.jdom.Element root = new Element("学生表");
		// 设置根节点
		document.setRootElement(root);
		// root.addContent("xuesheng");//.setText("gj");
		root.addContent(new Element("学生").setAttribute("id", "00001").addContent("<!-- 这段是注释 -->")
				.addContent(new Element("姓名").setText("张三")).addContent(
						new Element("性别").setText("女")).addContent(
						new Element("年龄").setText("10")));

		root.addContent(new Element("学生").setAttribute("id", "00002")
				.addContent(new Element("姓名").setText("张四")).addContent(
						new Element("性别").setText("男")).addContent(
						new Element("年龄").setText("102")));

		org.jdom.output.XMLOutputter out = new org.jdom.output.XMLOutputter();
		out.setFormat(Format.getPrettyFormat().setEncoding("UTF-8"));
		// out.output(document,new java.io.FileOutputStream("d:/1.xml"));
		System.out.println(System.getProperty("user.dir"));
		out.output(document, new java.io.FileOutputStream(System
				.getProperty("user.dir")
				+ "/1.xml"));
		out.output(document, System.out);

	}
}
