
public class SingletonPattern {
	
	private static final SingletonPattern sp = new SingletonPattern();
	
	private SingletonPattern(){}
	
	public synchronized static SingletonPattern getInstance(){
		return sp;
	}
}
