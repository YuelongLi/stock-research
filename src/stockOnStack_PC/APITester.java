package stockOnStack_PC;

import java.io.IOException;

import yahoofinance.Stock;
import yahoofinance.YahooFinance;

public class APITester {
	public static void main(String[] args) {
		try {
			Stock a = YahooFinance.get("appl");
			System.out.println(a.getCurrency());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
				
	}
}
