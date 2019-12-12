package runTests;

import java.awt.Desktop;
import java.io.*;
import java.util.Arrays;

import codeLibrary.*;
import codeLibrary.Console;

/**
 * 
 * @author Yuelong Li
 *
 */

public class DataBaseTest{
	static long start;
	static long end;
	public static void main(String[] args) throws IOException{
		String[] infoTypes = new String[]{"Time", "Price", "Ask", "Bid", "Volume"};
		File dataBase = new File("C:/Users/Yuelong/StockDataBase");
		File stock = new File(dataBase,"Test");
		int span = 7*24;
		int tpm = 30;
		timerStart();
		CSV dataFile = new CSV (stock,span+"_"+tpm+".csv");
		timerStop();
		timerReset("CSV instantiation");
		String[][] info = new String[span*60*tpm+2][infoTypes.length];
		info[0][0]="Tester";
		for(int i = 0; i<infoTypes.length; i++)info[1][i] = infoTypes[i];
		for(int i = 2; i<span*60*tpm; i++){
			for(int a = 0; a<infoTypes.length; a++){
				info[i][a]=String.valueOf((double)((int)(Math.random()*100*1000))/100);
			}
		}
		
		timerStart();
		dataFile.setMatrix(info);
		dataFile.flushMatrix();
		timerStop();
		timerReset("Data write "+span*tpm*60);
		timerStart();
		FileReader reader = new FileReader(dataFile);
		BufferedReader in = new BufferedReader(reader);
		timerStop();
		timerReset("Data open "+span*tpm*60);
		String inputline;
		timerStart();
		while((inputline = in.readLine())!=null)
			inputline.split(",");
		Desktop.getDesktop().open(stock);
		in.close();
		reader.close();
		timerStop();
		timerReset("Data Read "+span*tpm*60);
		new File("C:/Users/Yuelong/StockDataBase/stco/dd").mkdirs();
	}
	
	public static void timerStart(){
		start = System.nanoTime();
	}
	public static void timerStop(){
		end = System.nanoTime();
	}
	public static void timerReset(String object){
		long relativeDuration = end-start;
		timerStart();
		timerStop();
		long error = end-start;
		long duration = relativeDuration-error;
		Console.println("testing object: "+object);
		Console.println("Consumed time: "+duration+"*10^-9 seconds");
		end=0;
		start=0;
	}
}
