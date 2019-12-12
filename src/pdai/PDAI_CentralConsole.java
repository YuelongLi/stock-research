package pdai;

/**
* @author 越隆
* @version 1.1
*/

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import codeLibrary.CSV;

public class PDAI_CentralConsole {
	static int Index = 0;
	static List <PDAI_GUI> GUI = new ArrayList<PDAI_GUI>();
	static List <PDAI_AI> AI = new ArrayList<PDAI_AI>();
	static List<String> targetStocks = new ArrayList<String>();
	
	public static void main(String[] args) throws Exception{
		InitializePDAI();
		System.out.println("Loading Price Log");
		GetHPrice(0,targetStocks.get(0),14);
		System.out.println("Setting Parameters");
		GUI.get(0).tradePanel.setData();
		System.out.println("Setting Graphic Area");
		GUI.get(0).refreshChartSize();
	}
	
	public static void GetAnalysis(int ListIndex){
		GUI.get(ListIndex).tradePanel.clearData();
		GUI.get(ListIndex).indicatorPanel.clearData();
		GUI.get(ListIndex).addLoading();
		System.out.println(" Determining the AveragePeriod");
		if(AI.get(ListIndex).AveragePeriod==0)AI.get(ListIndex).setAPeriod();
		AI.get(ListIndex).depthSmoothing(1);
		System.out.println(" Calculating First Order Floating Average...");
		GUI.get(ListIndex).tradePanel.getAverage();
		System.out.println(" Determining the XAveragePeriod");
		if(AI.get(ListIndex).XAveragePeriod==0)AI.get(ListIndex).setXAPeriod();
		System.out.println(" Calculating Second Order Floating Average...");
		GUI.get(ListIndex).tradePanel.getXAverage();
		System.out.println(" Calculating First Order Differential...");
		GUI.get(ListIndex).indicatorPanel.getDifferential();
		System.out.println(" Determining the DifAveragePeriod");
		if(AI.get(ListIndex).DifAveragePeriod==0)AI.get(ListIndex).setDifAveragePeriod();
		System.out.println(" Calculating Second Order Differential...");
		GUI.get(ListIndex).indicatorPanel.getDDifferential();
		System.out.println(" Calculating DDAD...");
		GUI.get(ListIndex).indicatorPanel.getDDAD();
		GUI.get(ListIndex).removeLoading();
		GUI.get(ListIndex).refreshChartSize();
		System.out.println(" Finished");
	}
	public static double TradeAverage(int ListIndex, double Date_x){
		return AI.get(ListIndex).TradeAverage(Date_x);
	}
	public static double XTradeAverage(int ListIndex, double Date_x){
		return AI.get(ListIndex).XTradeAverage(Date_x);
	}
	public static double Differential(int ListIndex, double Date){
		return AI.get(ListIndex).Differential(Date);
	}
	public static double DifAverage(int ListIndex, double Date_x){
		return AI.get(ListIndex).DifAverage(Date_x);
	}
	public static double DDifferential (int ListIndex, double Date_x){
		return AI.get(ListIndex).DDifferential(Date_x);
	}
	public static double DDAD (int ListIndex, double Date_x){
		return AI.get(ListIndex).DDAD(Date_x);
	}
	public static void GetHPrice(int ListIndex, String Instrument, int date) throws Exception{
		System.out.println(" Reading Price Log & Transfering into Coordinate");
		JFileChooser temp = new JFileChooser();
		temp.setDialogTitle("Please Choose the file");
		SimpleDateFormat a = new SimpleDateFormat("MM.dd-HH:mm:ss");
		AI.get(ListIndex).setPrice(new CSV("G:/StockDataBase/TSLA/2019/39_TSLA.csv"),a.parse("09.24-10:07:25"),a.parse("09.24-19:39:55"));
		System.out.println(" Saving...");
		GUI.get(ListIndex).x = new ArrayList<Double>(AI.get(ListIndex).x);
		GUI.get(ListIndex).y = new ArrayList<Double>(AI.get(ListIndex).y);
	}
	private static void InitializePDAI() throws Exception{
		targetStocks.add("aapl 2017,5");
		AI.add(new PDAI_AI(Index));
		GUI.add(new PDAI_GUI(Index));
		GUI.get(Index).setVisible(true);
		Index++;
	}
}
/*
 * © Copyright 2016
 * Cannot be used without authorization
 */