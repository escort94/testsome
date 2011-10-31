import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class TestFile {
	public static String getString() throws FileNotFoundException {
		String str = "";
		InputStreamReader in = new InputStreamReader(new  FileInputStream("D:\\data.txt"));
		int c;
		ByteArrayOutputStream bais = new ByteArrayOutputStream();
		
		FileOutputStream fout = new FileOutputStream("D:\\data1.txt"); 
	      OutputStreamWriter  osw = new OutputStreamWriter(fout); 

		try {
			while ((c = in.read()) != -1) {
				bais.write(c);
				System.out.write(c);
				osw.write(c);
//				System.out.println(c + " :" +(char)c);
			}
			osw.flush();
			osw.close();
			byte data[] = bais.toByteArray();
			str = new String(data, "ISO8859-1");
			System.out.println(str);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return str;
	}
	public static String getString1() throws FileNotFoundException {
		String str = "";
		InputStreamReader in = new InputStreamReader(new  FileInputStream("D:\\data.txt"));
		BufferedReader br = new BufferedReader(in);
		String c;
		try {
			while ((c = br.readLine()) != null) {
				System.out.println(c);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return str;
	}

	public static void main(String[] args) {
		try {
			getString1();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//		File file = new File("E:/source/console/./keystore/guoqing.txt");
//		if (file.exists()) {
//
//		} else {
//			new File(file.getParent()).mkdirs();
//		}
//
//		String str = "asdfasdfasdf";
//		try {
//			FileOutputStream localFileOutputStream = new FileOutputStream(file);
//			localFileOutputStream.write(str.getBytes());
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
	}
}
