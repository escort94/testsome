package radvision.fatherson;

public class Father {
	public String name ;
	public static String address = "haerbin";
	
	public Father(){
		System.out.println("father no param contruct");
	}
	public Father(String name, String address){
		this.name = name;
		this.address = address;
		System.out.println("father contruct");
	}
	
	public void printName(){
		System.out.println("father " + name);
		printAddress();
	}
	public void printAddress(){
		System.out.println("father " + address);
	}
}
