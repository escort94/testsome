import java.util.HashMap;
import java.util.Map;


public class TestMap {
	
	public static void main(String[] args){
		Map map = new HashMap();
		map.put(1, "1");
		map.put(1, "2");
		System.out.println(map.get(1));
	}
}
