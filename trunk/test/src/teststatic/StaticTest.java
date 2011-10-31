package teststatic;

public class StaticTest{
	//初始化时，可以直接调用静态成员，以及同类的非静态成员
	
	//静态变量初始化时不能引用非静态成员
//	public static int staticVar=new AssistClass().getNum2();
	public static int staticVar=0;
	//静态域与静态变量初始化具有相同的优先级
	static{
		staticVar++;
		System.out.println("static block");
		
		//域中可以定义变量以及类，不能定义方法，并且只是在域中使用
		//不能带有public\protected\private标示符
		int var2=3;
		class NSC2{
			int i=2;
		}
//		System.out.println(new NSC2().i);
		
		//不允许定义静态变量和静态类
//!			static int staticVar2;
//!			static class SC2{}
		
	}
	public int var=0;
	public int var2=fun3();
	
	public StaticTest(){
		System.out.println("constructor");
	}
	
	//非静态域与非静态变量初始化具有相同的优先级
	{
		//静态域可以访问所属类的静态
		var++;
		staticVar++;
		System.out.println("not static block");
		
	}
	
	public void fun(){//非静态方法可以直接调用静态和非静态成员
		staticVar=1;
		staticFun();
		fun2();
	}
	
	public void fun2(){
		
	}
	public int fun3(){
		return 3;
	}
	
	public static void staticFun(){
		//静态方法不能直接调用非静态成员
		//需要：1.实例化	2.改为静态
//!		fun2();
		System.out.println("sattic method");
		
	}
	
	public static int staticFun2(){
		System.out.println("static field init");
		return 2;
	}
	
	public static int staticFun3(){
		System.out.println("not static field init");
		return 3;
	}
	
	/**
	 * @param args
	 */
	
	//静态方法在第一次执行时会先执行静态初始化和静态域
	//构造方法可以认为是静态方法
	//每次实例化都会执行一次非静态初始化和非静态域
	public static void main(String[] args) {
		// TODO Auto-generated method stub
//		staticFun();
		
//		new StaticTest();
		new StaticTest();
		
//		System.out.println(st.var+"&"+st.var2+"&"+staticVar);
	}

	//结论：静态成员可以被直接调用，非静态成员则需经实例化(但可以被同类的非静态成员调用)
}