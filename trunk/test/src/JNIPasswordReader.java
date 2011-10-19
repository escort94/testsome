
public class JNIPasswordReader {
 
private native String readPassword();
 static {
 System.loadLibrary("PasswordDLL");
 } 

public static void main(String[] args) {
	JNIPasswordReader reader = new JNIPasswordReader();
	String pwd = reader.readPassword();
	System.out.println("nYour Password is:" + pwd);
 	} 

} 


