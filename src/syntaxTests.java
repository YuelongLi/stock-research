import java.util.ArrayList;

public class syntaxTests {
	public static void main(String[] args) {
		ArrayList<String> a = new ArrayList<String>();
		a.add("abc");
		a.add("bac");
		a.add("cbc");
		System.out.println(a.indexOf("bac"));
	}
}
