import java.util.Date;


public class TestStringBuffer {
	public static void main(String[] args) {
		long startBuffer = new Date().getTime();
		StringBuffer sb = new StringBuffer();
		for(int  i = 0;i < 5000;i ++){
			sb.append("asdf");
		}
		long endBuffer = new Date().getTime();
		System.out.println("StringBuffer time:" + (endBuffer - startBuffer));
		
		long startString = new Date().getTime();
		String sbs = new String();
		for(int  i = 0;i < 5000;i ++){
			sbs += "asdf";
		}
		long endString = new Date().getTime();
		System.out.println("String time:" + (endString - startString));
	}
}
