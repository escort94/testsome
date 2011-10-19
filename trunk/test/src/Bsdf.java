
public class Bsdf {

	public Asdf f = new Asdf(){
		public void geta() {
			System.out.println("asdf");
		}
	};
	public static void main(String[] args) {
		new Bsdf().f.geta();
	}
}
