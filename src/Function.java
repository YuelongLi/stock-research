public class Function extends generator {
	public static void main (String[] args){
		char c;
		int a;
		double b;
			for(int i=0;i<=10000;i++){
				b = Math.sqrt(i);
				a = (int) Math.sqrt(i);
				b = (b-a)*95+32;
				a = (int) b;
				c = (char) a;
				if(b==32){
					println();
				}
				else{
			        print(c);
		            }
			}
			println();
	}
}
class generator{
 protected static void println(){
	 System.out.println("");
 }
 protected static void println(int a){
	 System.out.print(" "+a+" ");
 }
 protected static void println(double a){
	 System.out.print(" "+a+" ");
 }
 protected static void print(String a){
	  System.out.print(a);
 }
 protected static void print(char a){
	  System.out.print(a);
}
}
