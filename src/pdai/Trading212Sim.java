package pdai;

import java.util.ArrayList;
/**
 * 
 * @author Yuelong Li
 * 
 * <p>
 *The class is structured according to the trading rules of the trading platform -- trading 212.
 *The sole purpose of the class is to run trading simulations on a singular stock, and thus can
 *not be transferred to anyone for commercial use.
 *</p>
 *<p>
 *There are two mechanisms in this trade simulator: historical simulation, which is to run simulations
 *given a time - price dataset, and real time simulation, which is achieved through price updates in realtime.
 *</p>
 *
 *<p>
 *copyright 2017
 *<p/>
 */
public class Trading212Sim {
	public double price, spread, ask, bid, earnings, deposit, free, instock = 0, total, margin, holding, lever,
	tradeAmount, onetimeCommission = 0, marginLimit = 0.25;
	
	//Historical dataset.
	//after initialization, asks, bids and abTime have to share the same size
	ArrayList<Double> asks = new ArrayList<Double>();
	ArrayList<Double> bids =  new ArrayList<Double>();
	ArrayList<Double> abTime = new ArrayList<Double>();
	
	//Historical dataset
	//after initialization, prices and pricetime have to share the same size
	ArrayList<Double> prices = new ArrayList<Double>();
	ArrayList<Double> priceTime = new ArrayList<Double>();
	
	public Trading212Sim(double spread, double total, double lever, double otm) {
		this.spread = spread;
		this.total = total;
		free = total;
		deposit = free;
		this.lever = lever;
		this.onetimeCommission = otm;
	}
	
	/**
	 * 
	 * @param time in seconds
	 * @return historic price at a certain time
	 */
	public double getHisPrice(double time) {
		int index = 0;
		double price;
		while(priceTime.get(index)>time&&index<priceTime.size()-2)index++;
		//unchecked exception: abTime1 - abTime2 = 0
		double slope1 = (prices.get(index+1)-prices.get(index))/(priceTime.get(index+1)-priceTime.get(index));
		price = slope1*(time-priceTime.get(index))+prices.get(index);
		return price;
	}
	
	/**
	 * 
	 * @param purchaseAmount The amount of stocks, which has to be positive, one is expecting to buy
	 * (since there is always a lever in trading212, trade quantity can not be determined
	 * by stock holdings)
	 * @param time in seconds
	 * @return action statement: 1 = purchase successful, 0 = purchase failed
	 */
	public int purchase(double purchaseAmount, double...time) {
		if(purchaseAmount<0)sell(purchaseAmount);
		double commission = 0;
		if(time.length>0) {
			updateHistoricProfile(time[0]);
		}
		if(holding>=0) {
			double earnings1 = earnings;
			double instock1 = instock;
			double deposit1 = deposit;
			double holding1 = holding;
			
			earnings1-=onetimeCommission;
			commission = purchaseAmount*ask/lever;
			instock1 += commission;
			deposit1 -= commission;
			holding1 += purchaseAmount;
			
			if((deposit1+earnings)/total >marginLimit) {
				earnings = earnings1;
				instock = instock1;
				deposit = deposit1;
				holding = holding1;
				free = deposit+earnings;
				return 1;
			}else return 0;
		}else {
			if(Math.abs(purchaseAmount)>Math.abs(holding)) {
				purchase(-holding);
				return purchase(holding+purchaseAmount);
			}else {
				double avbid = instock/holding*lever;
				commission = Math.abs(instock*purchaseAmount/holding);
				double earned = (avbid - ask)*purchaseAmount;
				instock -= commission;
				deposit += commission + earned;
				holding += purchaseAmount;
				earnings -= earned;
				free = deposit + earnings;
				return 1;
			}
		}
	}
	
	/**
	 * 
	 * @param sellAmount The amount of stocks, which has to be negative, one is expecting to buy
	 * (since there is always a lever in trading212, trade quantity can not be determined
	 * by stock holdings)
	 * @param time in seconds
	 * @return action statement: 1 = sell successful, 0 = sell failed
	 */
	public int sell(double sellAmount, double...time) {
		if(sellAmount>0)purchase(sellAmount);
		double commission;
		if(time.length>0) {
			updateHistoricProfile(time[0]);
		}
		if(holding<=0) {
			double earnings1 = earnings;
			double instock1 = instock;
			double deposit1 = deposit;
			double holding1 = holding;
			
			earnings1-=onetimeCommission;
			commission = Math.abs(sellAmount*bid/lever);
			instock1 += commission;
			deposit1 -= commission;
			holding1 += sellAmount;
			
			if((deposit1+earnings)/total >marginLimit) {
				earnings = earnings1;
				instock = instock1;
				deposit = deposit1;
				free = deposit + earnings;
				holding = holding1;
				return 1;
			}else return 0;
		}else {
			if(Math.abs(sellAmount)>Math.abs(holding)) {
				sell(-holding);
				return purchase(holding+sellAmount);
			}else {
				double avask = instock/holding*lever;
				commission = Math.abs(instock*sellAmount/holding);
				double earned = (avask - bid)*sellAmount;
				instock -= commission;
				deposit += commission + earned;
				holding += sellAmount;
				earnings -= earned;
				free = deposit + earnings;
				return 1;
			}
		}
	}
	/**
	 * Sets fields ask and bid to their corresponding values at a given time. 
	 * If the time falls in the middle between two recorded moments, 
	 * the method returns a linear approximation
	 * @param time
	 */
	public void setAskBidByTime(double time) {
		int index = 0;
		while(abTime.get(index)>time&&index<abTime.size()-2)index++;
		//unchecked exception: abTime1 - abTime2 = 0
		double slope1 = (asks.get(index+1)-asks.get(index))/(abTime.get(index+1)-abTime.get(index));
		double slope2 = (bids.get(index+1)-bids.get(index))/(abTime.get(index+1)-abTime.get(index));
		ask = slope1*(time-abTime.get(index))+asks.get(index);
		bid = slope2*(time-abTime.get(index))+bids.get(index);
	}
	
	/**
	 * Sets field price to its corresponding value at a given time. 
	 * If the time falls in the middle between two recorded moments, 
	 * the method returns a linear approximation
	 * @param time
	 */
	public void setPriceByTime(double time) {
		int index = 0;
		while(priceTime.get(index)>time&&index<priceTime.size()-2)index++;
		//unchecked exception: abTime1 - abTime2 = 0
		double slope1 = (prices.get(index+1)-prices.get(index))/(priceTime.get(index+1)-priceTime.get(index));
		price = slope1*(time-priceTime.get(index))+prices.get(index);
	}
	
	public void updateHistoricProfile(double time) {
		setAskBidByTime(time);
		setPriceByTime(time);
		if(holding>0)earnings = holding*(bid-instock/holding*lever);
		if(holding<0) earnings = -holding*(instock/holding*lever - ask);
		free = deposit+earnings;
		total = free+instock;
	}
	
	public void updateProfile(double price, double ask, double bid) {
		/**
		 * @TODO do something
		 */
		if(holding>0)earnings = holding*(bid-instock/holding*lever);
		if(holding<0) earnings = -holding*(instock/holding*lever - ask);
		free = deposit+earnings;
		total = free+instock;
	}
	
	public void clear(double...time) {
		if(time.length>0) {
			setAskBidByTime(time[0]);
			setPriceByTime(time[0]);
		}
		if(holding > 0) {
			earnings = holding*(bid - instock/holding*lever);
		}
		if(holding > 0) {
			earnings = -holding*(instock/holding*lever - ask);
		}
		deposit += instock + earnings;
		free = deposit;
		earnings = 0;
		instock = 0;
		holding = 0;
		total = free;
	}
}
