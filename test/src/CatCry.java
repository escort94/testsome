class Cat {
	public void cry() {
		System.out.print("miaow ");
	}
}

class DummyCat extends Cat {
	public void cry() {
		System.out.print("miaoeeeeeeee ");
	}
}

public class CatCry {
	public static void main(String args[]) {
		Cat mimi = new Cat();
		Cat dummy = new DummyCat();
		mimi.cry();
		dummy.cry();
	}
}