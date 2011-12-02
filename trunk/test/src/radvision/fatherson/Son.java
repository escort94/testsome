package radvision.fatherson;

public class Son extends Father{
	public String name;
	public static String address;
	public Son(){
		System.out.println("son no param contruct");
	}
	public Son(String name, String address){
		this.name = name;
		this.address = address;
		System.out.println("son contruct");
	}
	public void printAddress(){
		System.out.println("son " + address);
	}
}
