package stockOnStack_PC;

/**
 * This program is a data collector that serves the Predictive AI
 * @version 1.0
 * @author Yuelong
 */
import java.awt.EventQueue;
import java.io.IOException;
import java.util.*;

import codeLibrary.*;
import yahoofinance.Stock;
import yahoofinance.YahooFinance;
/**
 * Central console for stock on stack
 * @author Yuelong Li
 */
public class STOS_Console {
	static List<Stock> stocks = new ArrayList<Stock>(0);
	static List<STOS_GUI_Config> config = new ArrayList<STOS_GUI_Config>(2);
	static STOS_GUI_Search search;
	
	static int stockIndex = 0;
	static int windowIndex = 0;
	
	public static void main(String[] args){
		launchSearch();
	}
	public static boolean search(String stockName){
		try {
			stocks.add(YahooFinance.get(stockName));
		} catch (IOException e) {
			e.printStackTrace();
			new JWarning("Network interupted. Please check your internet connection and reload.");
			return true;
		}catch(StringIndexOutOfBoundsException e){
			e.printStackTrace();
			return false;
		}catch(Exception e){
			e.printStackTrace();
			new JWarning("Unknown Exception");
		}
		if(stocks.get(stockIndex).getName()==null){
			stocks.remove(stockIndex);
			return false;
		}
		for(Stock pass:stocks){
			pass.print();
		}
		stockIndex++;
		addConfig();
		return true;
	}
	public static void config(HashMap<String, String> configuration, int configIndex){
		addMonitor(configuration,configIndex);
	}
	protected static void addMonitor(HashMap<String, String> configuration, int index) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					STOS_GUI_Monitor monitor = new STOS_GUI_Monitor(index);
					monitor.setVisible(true);
					monitor.setConfig(configuration);
					monitor.startCollection();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	protected static void addConfig() {
		Stock stock = stocks.get(stockIndex-1);
		String stockInfo = "";
		stockInfo+=(stock.getSymbol()+"\n");
		try{
			stockInfo+="-------------------------------------------------------------------------"
					+ "-------------------------------------------------------------------------\n";
			stock.toString();
			stockInfo+="name: "+stock.getName().toString()+"\n";
			stockInfo+="symbol: "+stock.getSymbol().toString()+"\n";
			stockInfo+="stock exchange: "+stock.getStockExchange()+"\n";
			stockInfo+="quotes: "+stock.getQuote().toString()+"\n";
			stockInfo+="currency: "+stock.getCurrency().toString()+"\n";
			stockInfo+="dividend: "+"pay date: "+stock.getDividend().getPayDate().getTime().toString()+"\n";
			stockInfo+="          "+"ex date: "+stock.getDividend().getExDate().getTime().toString()+"\n";
			stockInfo+="          "+"yield: "+stock.getDividend().getAnnualYield().toString()+" "+stock.getCurrency().toString()+"\n";
			stockInfo+=stock.getStats().toString()+"\n";
	        stockInfo+="-------------------------------------------------------------------------"
	        		+ "-------------------------------------------------------------------------\n";
		}catch(NullPointerException e){
			stockInfo+=stock.getStats().toString()+"\n";
			stockInfo+="-------------------------------------------------------------------------"
			 		+ "-------------------------------------------------------------------------\n";
		}
		
        final String pass = stockInfo;
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					config.add(new STOS_GUI_Config(stockIndex-1));
					config.get(stockIndex-1).stockInfo.setText(pass);
					config.get(stockIndex-1).setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	protected static void removeConfig(int Index){
		config.remove(Index);
		stocks.remove(Index);
		stockIndex--;
	}

	protected static void launchSearch() {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					STOS_GUI_Search frame = new STOS_GUI_Search();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
}
/*
 * © Copyright 2016
 * Cannot be used without authorization
 */