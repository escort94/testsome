package collection;

import java.util.HashSet;
import java.util.Set;

public class TestSet {
	public String name;
	public String address;
	public TestSet(String name, String address){
		this.name = name;
		this.address = address;
	}
	public boolean equals(Object obj) {
		if(null == obj){
			return false;
		}
		if(this == obj){
			return true;
		}
		if(obj instanceof TestSet){
			TestSet o = (TestSet)obj;
			if(o.getName().equals(this.getName())){
				return true;
			}
		}
		return false;
	}

	public int hashCode() {
		 int prime = 31;  
		 int result = 1;  
		 return prime*result + this.getName().hashCode();

	}

	public static void main(String[] args) {
		TestSet o1 = new TestSet("fang", "22");
		TestSet o2 = new TestSet("fang", "33");
		Set<TestSet> set = new HashSet<TestSet>();
		set.add(o1);
		set.add(o2);
		for(TestSet t : set){
			System.out.println(t.getName() + ":" + t.getAddress());
		}
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}
}
