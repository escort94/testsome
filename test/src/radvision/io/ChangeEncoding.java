package radvision.io;

import java.io.*;

public class ChangeEncoding {
	public static void changeEncoding(String inEncoding, String outEncoding,
			String inFileName, String outFileName) throws IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				new FileInputStream(inFileName), inEncoding));
		BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
				new FileOutputStream(outFileName, true), outEncoding));
		String s = null;
		while ((s = reader.readLine()) != null) {
			writer.write(s);
			writer.newLine();
			writer.newLine();
		}
		writer.flush();
		writer.close();
		reader.close();
	}
	
	public void changeEncode() throws UnsupportedEncodingException, FileNotFoundException{
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(""), "GBK"));
	}
	public static void main(String[] args) {
		try {
			changeEncoding("GBK", "UTF-8", "gbk.txt", "utf8.txt");
		} catch (IOException e) {
			System.out.println("转换失败，原因：" + e.getMessage());
		}
	}
}
