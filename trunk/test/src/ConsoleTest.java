import java.io.Console;
public class ConsoleTest{ 
	public static void main(String[] args){ 
		Console console=System.console(); 
		if(console!=null){ 
			String uname=new String(console.readLine("Enter name\n",new Object[0])); 
			String upwd=new String(console.readPassword("Enter Password\n",new Object[0])); 
			//console.printf(uname+" "+upwd); System.out.println(uname+" "+upwd); 
			}else System.out.println("Error"); 
		} 
	}
