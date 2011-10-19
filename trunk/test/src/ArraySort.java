import java.util.Arrays;

public class ArraySort {
	public static void main(String[] args) {
		String dn1 = "CN=kmcrsatest,O=JIT,C=CN";
		String dn2 = "C=CN,O=JIT,CN=kmcrsatest";
		isSameDN(dn1, dn2);
	}
	public static boolean isSameDN(String dn1, String dn2){
		String dn1s[] = new String[3];
		int i = 0;
		for(String s : dn1.split(",")){
			dn1s[i++] = s.trim();
		}
		Arrays.sort(dn1s);
		for(String s : dn1s){
			System.out.print(s + ",");
		}
		return false;
	}
	
	
//	public static void main(String[] args) {
//		String[] str = new String[] { "bbb ", "www ", "eeee ", "sssss " };
//		Arrays.sort(str);
//		for (int i = 0; i < str.length; i++) {
//			System.out.println(str[i]);
//		}
//	}
}