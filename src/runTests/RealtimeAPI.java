package runTests;
import java.awt.event.*;
import java.awt.*;
import java.io.*;
import java.math.BigDecimal;
import java.net.SocketTimeoutException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import yahoofinance.Stock;

public class RealtimeAPI {
	static final long S = System.nanoTime();
	public static void main(String[] args)throws IOException, ParseException, InterruptedException{
		BigDecimal price;
		String Stockname = "XAUUSD=X";
		String Filename = Stockname + new SimpleDateFormat(" MM,dd,HH").format(new Date())+".txt";
		System.out.println(Filename);
		Stock stock = new Stock(Stockname);
		FileWriter dataReport = new FileWriter(Filename);
		PrintWriter pw = new PrintWriter(dataReport);
		pw.println("Stock type:"+ Stockname);
		pw.println("Start from:"+new Date());
		pw.println("@");
		Terminator a = new Terminator("Termination");
		
		for(int i = 0;i<5000 && a.Break;i++){
			try{
				price = stock.getQuote(true).getPrice();
				pw.println(new SimpleDateFormat("MM/dd HH:mm:ss").format(new Date().getTime()));
				pw.println(price);
		    	System.out.println("current price:"+price+" index:"+i);
		    	Thread.sleep((long) 6000);
				}catch(SocketTimeoutException e){
				continue;
				}catch(FileNotFoundException e){
				System.out.println("FileNotFound");	
				}
		}
		
	    
	    pw.println("@/");
	    pw.println("End at:"+new Date());
		pw.close();
		a.finalize();
	}
}
class Terminator extends JFrame{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	boolean Break = true;
	JButton a = new JButton("Terminate Data Collection");
	public Terminator(String name){
		
		SwingUtilities.invokeLater(new Runnable(){
			public void run(){
				a.addActionListener(new ActionListener(){
					public void actionPerformed(ActionEvent e){
						Break = false;
					}
				});
				setTitle(name);
				setSize(300,150);
				setVisible(true);
				setLayout(new FlowLayout());
				add(a);
			}
		});
	}
	protected void finalize(){
		System.out.println("JFrame finalized");
		setVisible(false);
	}
}
