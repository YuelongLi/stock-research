package pdai;
/**
* @author 越隆
* @version 1.1
* @param HPrice[] Requested Price according to date
*/
import java.awt.EventQueue;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.swing.JFrame;

import codeLibrary.*;

public class PDAI_AI {
	protected int ListIndex;
	/**
	 * @param PeriodScale Constant that transforms the time unit from absolute(seconds) to relative
	 */
	double PeriodScale = 60;
	/**
	 * @param PriceScale A constant that transforms the price scale(USD) from absolute to relative
	 */
	double PriceScale;
	double PointDensity = 25;
	double AveragePeriod;
	double AverageAccuracy = 60;
	double XAveragePeriod;
	double XAverageAccuracy = 60;
	double DifAveragePeriod;
	double DifAverageAccuracy = 60;
	/**
	 * @param HDate History date array
	 */
	List<Double> HDate = new ArrayList<Double>();
	/**
	 * @param HPrice History price array, which corresponds to history date array
	 */
	List<Double> HPrice = new ArrayList<Double>();
	/**
	 * @param x History date coordinate array
	 */
	List<Double> x = new ArrayList<Double>(); 
	/**
	 * @param y History price coordinate array
	 */
	List<Double> y = new ArrayList<Double>();
	/**
	 * @param ax Average history date array
	 */
	List<Double> ax = new ArrayList<Double>();
	/**
	 * @param ay Average history price array, which corresponds to average date array
	 */
	List<Double> ay = new ArrayList<Double>();
	/**
	 * @param xax Secondary average history date array, which corresponds to average date array
	 */
	List<Double> xax = new ArrayList<Double>();
	/**
	 * @param xay Secondary average history price array, which corresponds to average date array
	 */
	List<Double> xay = new ArrayList<Double>();
	/**
	 * @param Dx First order differential x-coordinate array
	 */
	List<Double> Dx = new ArrayList<Double>();
	/**
	 * @param Dy First order differential y-coordinate array
	 */
	List<Double> Dy= new ArrayList<Double>();
	/**
	 * @param ADx First order differential Average-x-coordinate array
	 */
	List<Double> ADx = new ArrayList<Double>();
	/**
	 * @param ADy First order differential Average-y-coordinate array
	 */
	List<Double> ADy = new ArrayList<Double>();
	/**
	 * @param DDx Second order differential x-coordinate array
	 */
	List<Double> DDx = new ArrayList<Double>();
	/**
	 * @param DDy Second order differential y-coordinate array
	 */
	List<Double> DDy = new ArrayList<Double>();
	/**
	 * @param DDADx 
	 */
	List<Double> DDADx = new ArrayList<Double>();
	/**
	 * @param DDADy
	 */
	List<Double> DDADy = new ArrayList<Double>();
	/**
	 * @param buyX the time at which a buying trade is executed
	 */
	List<Double> buyX = new ArrayList<Double>();
	/**
	 * @param sellX the time at which a selling trade is executed
	 */
	List<Double> sellX = new ArrayList<Double>();
	List<Double> clearX =  new ArrayList<Double>();
	
	PDAI_AI(int ListIndex){
		this.ListIndex = ListIndex;
	}
	
	void setAPeriod(){
		AveragePeriod = getAWL(x,y,1);
		System.out.println("  AveragePeriod = " + AveragePeriod);
	}
	
	void setXAPeriod(){
		XAveragePeriod = getAWL(ax,ay,1);
		System.out.println("  XAveragePeriod = " + XAveragePeriod);
	}
	
	void setDifAveragePeriod(){
		DifAveragePeriod = getAWL(Dx,Dy,2);
		System.out.println(" DifAveragePeriod = " + DifAveragePeriod);
	}
	
	double getAWL(List<Double> P_x, List<Double> P_y, double EWL){
		int P_size = P_x.size();
		List <Integer> TRI = new ArrayList<Integer>();
		List <Integer> Date = new ArrayList<Integer>();
		List <Double> MidDate = new ArrayList<Double>();
		List <Double> MidPrice = new ArrayList<Double>();
		double SumGap = 0;
		double AverageGap;
		for(int i = 1; i<P_size-1; i++){
			if(P_y.get(i)>P_y.get(i-1)){
				if(P_y.get(i+1)<P_y.get(i)){
					TRI.add(Integer.valueOf(-3));
					Date.add(Integer.valueOf(i));
				}
				else if(!(P_y.get(i+1)>P_y.get(i))){
					TRI.add(Integer.valueOf(-2));
					Date.add(Integer.valueOf(i));
				}
			}
			if(P_y.get(i)<P_y.get(i-1)){
				if(P_y.get(i+1)>P_y.get(i)){
					TRI.add(Integer.valueOf(3));
					Date.add(Integer.valueOf(i));
				}
				else if(!(P_y.get(i+1)<P_y.get(i))){
					TRI.add(Integer.valueOf(2));
					Date.add(Integer.valueOf(i));
				}
			}
			else{
				if(P_y.get(i+1)>P_y.get(i)){
					TRI.add(Integer.valueOf(1));
					Date.add(Integer.valueOf(i));
				}
				if(P_y.get(i+1)<P_y.get(i)){
					TRI.add(Integer.valueOf(-1));
					Date.add(Integer.valueOf(i));
				}
			}
		}
		int TRI_size = TRI.size();
		for(int i = 1; i<TRI_size; i++){
			if(!SameNotation(TRI.get(i),TRI.get(i-1))){
				MidDate.add(Double.valueOf((P_x.get(Date.get(i))+P_x.get(Date.get(i-1)))/2));
				MidPrice.add(Double.valueOf(getY((P_x.get(Date.get(i))+P_x.get(Date.get(i-1)))/2,P_x,P_y)));
			}
		}
		double InstantGap;
		int SumDivider = 0;
		for(int i = 1; i<MidDate.size(); i++){
			InstantGap = Math.sqrt(Square(MidDate.get(i)-MidDate.get(i-1))+Square(MidPrice.get(i)-MidPrice.get(i-1)));
			if(InstantGap < EWL){
				SumDivider+=1;
				SumGap+=Math.sqrt(Square(MidDate.get(i)-MidDate.get(i-1))+Square(MidPrice.get(i)-MidPrice.get(i-1)));
			}
		}
		AverageGap = SumGap/(SumDivider);
		return AverageGap;
	}
	void depthSmoothing(int depth){
		EventQueue.invokeLater(new Runnable(){
			public void run(){
				new FunctionDisplay("depth smoothing"){
					protected double[] function(double x){
						return  new double[]{getPrice(x),(getPrice(x)-getPrice(x-0.0001))*10000};
					}
				};
			}
		});
	}
	/**
	 * @param EWL = Expected wave length
	 * @param AWL = Average wave length
	 */
	double TradeAverage(double Date_x){
		double sumPrice = 0;
		double APrice;
		for(int c = 0; c <= Math.round(AveragePeriod*AverageAccuracy); c++){
			sumPrice+=getPrice(Date_x-AveragePeriod/2+c/AverageAccuracy);	
		}		
		APrice = sumPrice/(Math.round(AveragePeriod*AverageAccuracy)+1);
		ax.add(Double.valueOf(Date_x));
		ay.add(Double.valueOf(APrice));
		return APrice;
	}
	
	double XTradeAverage(double Date_x){
		double sumPrice = 0;
		double XAPrice;
		for(int c = 0; c <= Math.round(XAveragePeriod*XAverageAccuracy); c++){
			sumPrice+=2*c/(XAveragePeriod*XAverageAccuracy)*getAPrice(Date_x-XAveragePeriod/2+c/XAverageAccuracy);
			}
		XAPrice = sumPrice/(Math.round(XAveragePeriod*XAverageAccuracy)+1);
		xax.add(Double.valueOf(Date_x));
		xay.add(Double.valueOf(XAPrice));
		return XAPrice;
	}
	
	double Differential(double Date_x){
		double differential;
		differential = (getXAPrice(Date_x)-getXAPrice(Date_x-0.01))/0.01/getXAPrice(Date_x);
		Dx.add(Double.valueOf(Date_x));
		Dy.add(Double.valueOf(differential));
		return differential;
	}
	
	double DifAverage(double Date_x){
		double sumPrice = 0;
		double ADif;
		for(int c = 0; c <= Math.round(DifAveragePeriod*DifAverageAccuracy); c++){
			sumPrice+=getDifPrice(Date_x-DifAveragePeriod/2 + c/DifAverageAccuracy);
		}
		ADif = sumPrice/(Math.round(DifAveragePeriod*DifAverageAccuracy)+1);
		ADx.add(Double.valueOf(Date_x));
		ADy.add(Double.valueOf(ADif));
		return ADif;
	}
	
	double DDifferential(double Date_x){
		double ddifferential;
		ddifferential = (getADifPrice(Date_x)-getADifPrice(Date_x-0.01))/0.01;
		DDx.add(Double.valueOf(Date_x));
		DDy.add(Double.valueOf(ddifferential));
		return ddifferential;
	}
	
	double DDAD(double Date_x){
		double ddad;
		ddad = (getADifPrice(Date_x)+getDDifPrice(Date_x));
		DDADx.add(Date_x);
		DDADy.add(ddad);
		return ddad;
	}
	

	boolean logging = false;
	
	void simulateTrade(double initial, String stockName, PDAI_SimLog Console){
		double commission=0, leverage, spread=0;
		boolean isStock;
		try {
			CSV stockInfo = new CSV("InstrumentTypes/StockTypes.csv");
			ArrayList<ArrayList<String>> list = stockInfo.getChart();
			int index = searchStock(stockName, list);
			if(stockName.toUpperCase().equals(list.get(index).get(0))){
				//INSTRUMENT	COMPANY	MIN TRADED QUANTITY	MARGIN	LONG POSITION SWAP	SHORT POSITION SWAP	MARKET HOURS
				commission = 2.2;
				leverage = 100.0/Integer.valueOf(list.get(index).get(3).split("%")[0]);
			}else{
				isStock = false;
				CSV nonstockInfo = new CSV("InstrumentTypes/NonStockInstrumentTypes.csv");
				list = nonstockInfo.getChart();
				index = searchStock(stockName, list);
				if(stockName.toUpperCase().equals(list.get(index))){
					JWarning.warn("No speciefied stock found in the type reference, trade values are set to default");
				}
				spread = Double.valueOf(list.get(index).get(3));
				leverage = 100.0/Integer.valueOf(list.get(index).get(4).split("%")[0]);
			}
			Trading212Sim stockTrader = new Trading212Sim(spread, initial, leverage, commission);
			for(int i = 0; i<HDate.size(); i++) {
				stockTrader.priceTime.add(HDate.get(i));
				stockTrader.prices.add(HPrice.get(i));
				stockTrader.abTime.add(HDate.get(i));
				stockTrader.asks.add(HPrice.get(i));
				stockTrader.bids.add(HPrice.get(i)-spread);
			}
			stockTrader.updateHistoricProfile(HDate.get(0));
			Console.println("Simulation started");
			Console.println("Initial = "+stockTrader.total);
			Console.println("Lever = "+stockTrader.lever);
			Console.println("One-time commission = "+stockTrader.onetimeCommission);
			try {
			double price=HPrice.get(0);
			}catch(IndexOutOfBoundsException e) {
				e.printStackTrace();
				JWarning.warn("Empty stock data");
			}
			double lastDDAD = DDADy.get(0);
			double lag = (int)((AveragePeriod+XAveragePeriod+DifAveragePeriod)/2);
			for(int i = 0; i<HDate.size(); i++) {
				int ddadIndex = (int)((HDate.get(i)-HDate.get(0))/PeriodScale*PointDensity-lag*PointDensity);
				if(ddadIndex<0)ddadIndex = 0;
				if(ddadIndex>DDADx.size())break;
				if(Math.abs(lastDDAD) <= 1) {
					if(DDADy.get(ddadIndex)>1) {
						Console.println("Purchasing at "+HPrice.get(i));
						if(stockTrader.purchase(20*DDADy.get(ddadIndex), HDate.get(i))==1)
							Console.println("Stock bought "+20*DDADy.get(ddadIndex));
						else 
							Console.println("Purchase failed, inadequate fund");
						buyX.add(DDADx.get(ddadIndex)+lag);
						Console.println("Stock free = "+stockTrader.free);
						Console.println("");
					}
					if(DDADy.get(ddadIndex)<-1) {
						Console.println("Selling at "+HPrice.get(i));
						if(stockTrader.sell(20*DDADy.get(ddadIndex), HDate.get(i))==1)
							Console.println("Stock sold "+20*DDADy.get(ddadIndex));
						else 
							Console.println("Short failed, inadequate fund");
						sellX.add(DDADx.get(ddadIndex)+lag);
						Console.println("Stock free = "+stockTrader.free);
						Console.println("");
					}
				}
				if(Math.abs(lastDDAD)>1){
					if(Math.abs(DDADy.get(ddadIndex))<=1) {
						stockTrader.clear(HDate.get(i));
						clearX.add(DDADx.get(ddadIndex)+lag);
						Console.println("Position Cleared at "+HPrice.get(i));
						Console.println("Stock free = "+stockTrader.free);
						Console.println("Stock total = "+stockTrader.total);
						Console.println("");
					}
				}
				lastDDAD = DDADy.get(ddadIndex);
			}
			stockTrader.clear(HDate.get(HDate.size()-1));
			
			Console.println("Finished");
			Console.println("Total = "+stockTrader.total);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private int searchStock(String stock, ArrayList<ArrayList<String>> data){
		return recurseSearch(stock,data, 1, data.size()-1);
	}
	private int recurseSearch(String stock, ArrayList<ArrayList<String>> data, int start, int end){
		int mid = (start+end)/2;
		int result = stock.compareToIgnoreCase(data.get(mid).get(0));
		return (start>=mid||result==0)? mid:(result<0)?recurseSearch(stock,data,start,mid-1):recurseSearch(stock,data,mid+1,end);
	}
	
	private double readCurrency(String[] currencyInfo){
		double value = Double.valueOf(currencyInfo[2]);
		switch(currencyInfo[0]){
		case "USD":
			return value;
		case "AUD":
			return value*0.75;
		case "CHF":
			return 0.99*value;
		case "CAD":
			return value*0.8;
		case "EUR":
			return value*1.1;
		case "JPY":
			return value*0.01;
		case "NZD":
			return value*0.72;
		case "CNY":
			return value/6.8;
		default :
			break;
		}
		return value;
	}
	
	private double getHPrice(double Date){
		if(Date>HDate.get(HDate.size()-1)) Date = HDate.get(HDate.size()-1);
		if(Date<HDate.get(0)) Date = HDate.get(0);
		double Price;
		int i;
		for(i = 1; i < HDate.size()-1; i++){
			if(Date <= HDate.get(i))break;
		}
		Price = (HPrice.get(i)-HPrice.get(i-1))/(HDate.get(i)-HDate.get(i-1))*(Date-HDate.get(i-1))+y.get(i-1);
		return Price;
	}
	private double getPrice(double Date_x){
		if(Date_x>x.get(x.size()-1)) Date_x = x.get(x.size()-1);
		if(Date_x<x.get(0))Date_x = x.get(0);
		double Price;
		int i;
		for(i = 1; i < x.size()-1; i++){
			if(Date_x<=x.get(i))break;
		}
		Price = (y.get(i)-y.get(i-1))/(x.get(i)-x.get(i-1))*(Date_x-x.get(i-1))+y.get(i-1);
		return Price;
	}
	private double getAPrice(double Date_ax){
		if(Date_ax>ax.get(ax.size()-1)) Date_ax = ax.get(ax.size()-1);
		if(Date_ax<ax.get(0))Date_ax = ax.get(0);
		double Price;
		int i;
		for(i = 1; i < ax.size()-1; i++){
			if(Date_ax<=ax.get(i))break;
		}
		Price = (ay.get(i)-ay.get(i-1))/(ax.get(i)-ax.get(i-1))*(Date_ax-ax.get(i-1))+ay.get(i-1);
		return Price;
	}
	private double getXAPrice(double Date){
		if(Date>xax.get(xax.size()-1)) Date = xax.get(xax.size()-1);
		if(Date<xax.get(0))Date = xax.get(0);
		double Price;
		int i;
		for(i = 1; i < xax.size()-1; i++){
			if(Date<=xax.get(i))break;
		}
		Price = (xay.get(i)-xay.get(i-1))/(xax.get(i)-xax.get(i-1))*(Date-xax.get(i-1))+xay.get(i-1);
		return Price;
	}
	private double getDifPrice(double Date_x){
		if(Date_x>Dx.get(Dx.size()-1)) Date_x = Dx.get(Dx.size()-1);
		if(Date_x<Dx.get(0))Date_x = Dx.get(0);
		double Dif;
		int i;
		for(i = 1; i < Dx.size()-1; i++){
			if(Date_x<=Dx.get(i))break;
		}
		Dif = (Dy.get(i)-Dy.get(i-1))/(Dx.get(i)-Dx.get(i-1))*(Date_x-Dx.get(i-1))+Dy.get(i-1);
		return Dif;
	}
	private double getADifPrice(double Date_x){
		if(Date_x>ADx.get(ADx.size()-1)) Date_x = ADx.get(ADx.size()-1);
		if(Date_x<ADx.get(0))Date_x = ADx.get(0);
		double ADif;
		int i;
		for(i = 1; i < ADx.size()-1; i++){
			if(Date_x <= ADx.get(i))break;
		}
		ADif = (ADy.get(i)-ADy.get(i-1))/(ADx.get(i)-ADx.get(i-1))*(Date_x-ADx.get(i-1))+ADy.get(i-1);
		return ADif;
	}
	private double getDDifPrice(double Date_x){
		if(Date_x>DDx.get(DDx.size()-1)) Date_x = DDx.get(DDx.size()-1);
		if(Date_x<DDx.get(0))Date_x = DDx.get(0);
		double ADif;
		int i;
		for(i = 1; i < DDx.size()-1; i++){
			if(Date_x <= DDx.get(i))break;
		}
		ADif = (DDy.get(i)-DDy.get(i-1))/(DDx.get(i)-DDx.get(i-1))*(Date_x-DDx.get(i-1))+DDy.get(i-1);
		return ADif;
	}
	private double getY(double Date_x,List<Double> x, List<Double> y){
		if(Date_x>x.get(x.size()-1)) Date_x = x.get(x.size()-1);
		if(Date_x<x.get(0))Date_x = x.get(0);
		double Price;
		int i;
		for(i = 1; i < x.size()-1; i++){
			if(Date_x<=x.get(i))break;
		}
		Price = (y.get(i)-y.get(i-1))/(x.get(i)-x.get(i-1))*(Date_x-x.get(i-1))+y.get(i-1);
		return Price;
	}
	private double Square(double a){
		double b;
	    b = a*a;
		return b;
	}
	private void setPriceScale(){
		double PV;
		double SumPV = 0;
		double APV;
		double RegularDateGap = (HDate.get(HDate.size()-1)-HDate.get(0))/HDate.size();
		//@unit RegularDateGap = second
		double IncrementConstant = PeriodScale/RegularDateGap;
		//@unit IncrementConstant = increments every time
		double IncrementTimes = (HDate.size()-1)/IncrementConstant;
		//@unit IncrementTimes = times
		for(int i = 0; i < IncrementTimes-1; i++){
			PV  = HPrice.get((int)((i+1)*IncrementConstant))-HPrice.get((int)(i*IncrementConstant));
			SumPV += Math.abs(PV);
		}
		APV = SumPV/IncrementTimes;
		PriceScale = APV;
		System.out.println("  PriceScale = "+APV);
	}
	
	private double getBiggest(List<Double> a){
		double b = Integer.MIN_VALUE;
		for(int i = 0; i<a.size(); i++){
			if(a.get(i) > b) b = a.get(i);
		}
		return b;
	}
	private double getSmallest(List<Double> a){
		double b = Double.MAX_VALUE;
		for(int i = 0; i<a.size(); i++){
			if(a.get(i) < b) b = a.get(i);
		}
		return b;
	}
	private boolean SameNotation(double a,double b){
		return a/Math.abs(a)==b/Math.abs(b);
	}
	int Index;
	ArrayList<ArrayList<String>> data;
	private SimpleDateFormat transform = new SimpleDateFormat("MM.dd-HH:mm:ss");
	void setPrice(CSV Instrument, Date startDate, Date endDate){
		Console.println("  loading "+Instrument);
		//time:HDate	price:HPrice	ask	bid	dayHigh	dayLow	eps	pe	peg	volume	AvgVolume	askSize	bidSize
		data = Instrument.getChart();
		if(data.size()>2){
			int Index;
			if((Index=searchTime(startDate,(data.size()-1)/2,(data.size()-1)/2))!=data.size()){
				Date date;
				try {
					while(Index<data.size()&&!(date=transform.parse(data.get(Index).get(0))).after(endDate)){
						HDate.add((double)date.getTime()/1000);
						HPrice.add(Double.valueOf(data.get(Index).get(1)));
						Index++;
					}
				} catch (ParseException e) {
					JWarning.warn("Wrong date format, please check line "+Index);
					e.printStackTrace();
				}
			}else{
				JWarning.warn("No data collected on specified date yet");
			}
		}else JWarning.warn("No collection data found in" + Instrument);
		setPriceScale();
		System.out.println("  Transfering into xy coodinate");
		for(int i = 0; i <= HDate.size()-1; i++){
			x.add(Double.valueOf((HDate.get(i)-HDate.get(0))/PeriodScale));
			y.add(Double.valueOf((HPrice.get(i)-getSmallest(HPrice))/PriceScale));
		}
	}
	
	private int searchTime(Date date, int Index, int space){
		if(Index<=2){
			return 2;
		}
		else if(Index>data.size()-1)return data.size();
		else {
			try{
				Date date1 = transform.parse(data.get(Index).get(0));
				Date date2 = transform.parse(data.get(Index+1).get(0));
				if(!date1.after(date)&&date2.after(date)){
					return Index;
				}
				else{
					int move = space/2+space%2;
					if(date2.equals(date))return Index+1;
					if(date1.after(date))return searchTime(date, Index-move, move);
					if(date2.before(date))return searchTime(date,Index+move, move);
					return -1;
				}
			}catch(Exception e){
				JWarning.warn("Wrong date format, please check line "+Index);
				return -1;
			}
		}
	}
}

/*
 * © Copyright 2016
 * Cannot be used without authorization
 */