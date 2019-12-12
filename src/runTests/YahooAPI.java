package runTests;
import java.io.*;
import yahoofinance.*;
import yahoofinance.histquotes.*;
import java.lang.String;
import java.math.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class YahooAPI {
	public static void main(String[] args) throws IOException, InterruptedException{
		Stock stock = YahooFinance.get("AAPL");
		List<HistoricalQuote> history = stock.getHistory(Interval.valueOf("DAILY"));
		BigDecimal price = stock.getQuote(true).getPrice();
		BigDecimal change = stock.getQuote(true).getChange();
		BigDecimal dividend = stock.getDividend().getAnnualYieldPercent();
		for(int i =0;i<history.size();i++){
			Calendar a = history.get(i).getDate();
			BigDecimal OpenPrice= history.get(i).getOpen();
			BigDecimal ClosePrice = history.get(i).getClose();
			String timeStamp = new SimpleDateFormat("yyyy,MM,dd, HH:mm:ss").format(a.getTime());
			System.out.println("open price:"+OpenPrice+" ClosePrice"+ClosePrice+" date:"+timeStamp);
		}
		System.out.println("current price:"+price+" dividend:"+dividend+" change:"+change+" date:"+new Date());
		stock.print();
	}
}
