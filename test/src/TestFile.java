import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;


public class TestFile {
	public static void main(String[] args) {
		File file = new File("E:/source/console/./keystore/guoqing.txt");
		if(file.exists()){
			
		}else{
			new File(file.getParent()).mkdirs();
		}
		
		 String str = "asdfasdfasdf";
		 try {
			 FileOutputStream localFileOutputStream = new FileOutputStream(file);
			 localFileOutputStream.write(str.getBytes());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
