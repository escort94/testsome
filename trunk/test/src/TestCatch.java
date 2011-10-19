
public class TestCatch {
	
	
	public int getStr() throws Exception{
		try{
		String s = "ad";
		int i = Integer.parseInt(s);
		return 8;
		}catch(Exception e){
			e.printStackTrace();
			throw new Exception();
		}
	}
	public static void main(String[] args) {
		try{
			System.out.println(new TestCatch().getStr());
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
