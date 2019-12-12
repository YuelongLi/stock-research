package runTests;

import codeLibrary.Console;

public class NameSearchTest {
	static double selfSimilarScale;
	static String[] names = new String[]{"AAPL","ABC","GOOG","TSLA","AA","YHOO","AMZN","DeltaAirLines","JD.com"};
	public static void main(String[] args){
		while(true){
			String a = Console.getLine();
			selfSimilarScale = getSimilarity(a,a);
			for(String name:names){
				if(getSimilarity(a,name)>25)Console.println(name);
			}
		}
	}
	public static double getAbsoluteSimilarity(String a, String b){
		return getSimilarity(a,b)/selfSimilarScale*100;
	}
	public static double getSimilarity(String a, String b){
		char[] ca = a.toUpperCase().toCharArray();
		char[] cb = b.toUpperCase().toCharArray();
		double similarity = 0;
		for(int i = 0; i<ca.length; i++){
			for(int j = 0; j<cb.length ; j++){
				if(ca[i]==cb[j]){
					similarity+=1.0/(Math.abs(i-j)+1);
				}
			}
		}
		return 100*similarity/(b.length()+a.length());
	}
}
