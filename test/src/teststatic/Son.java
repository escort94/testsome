package teststatic;

public class Son extends Father {
	public String name;
	
	public static void main(String[] args) {
		Son s = new Son();
		s.name = "asdf";
		s.geto();
	}
}
