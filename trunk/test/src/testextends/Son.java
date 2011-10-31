package testextends;

public class Son extends Father {
	public Son(String s, String s1) {
		System.out.println("son si son");
	}
	public static void main(String[] args) {
		Son son = new Son("asdf", "asdfasdf");
	}
}
