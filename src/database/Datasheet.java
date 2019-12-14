package database;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import codeLibrary.CSV;

public class Datasheet extends CSV{
	/**
	 * To make eclipse happy
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * The standard format of all the time entries
	 */
	static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("dd-M-yyyy hh:mm:ss");
	public ArrayList<String> entries;
	/**
	 * Records the column number of the time information
	 */
	protected int timeColumn;
	protected HashMap<String, Integer> entryIndices = new HashMap<String, Integer>();
	/**
	 * @see java.io.File
	 * @param parent
	 * @param child
	 */
	public Datasheet(File parent, String child) throws IOException, NoSuchEntryException{
		super(parent, child, true);
		if(chart.size()==0) {
			System.out.println("No information entries found, added default entries");
			entries = new ArrayList<String>();
			entries.add("time");
			timeColumn = 0;
			chart.add(entries);
		} else {
			entries = this.chart.get(0);
			findTimeColumn();
			readEntries();
		}
	}
	
	/**
	 * @see java.io.File
	 * @param name
	 */
	public Datasheet(String name) throws IOException{
		super(name, true);
	}
	
	/**
	 * @see java.io.File
	 * @param uri
	 */
	public Datasheet(URI uri) throws IOException{
		super(uri, true);
	}
	
	/**
	 * Sets the entries of this datasheet and updates the time column
	 * @param entries
	 */
	public void setEntries(ArrayList<String> entries) throws NoSuchEntryException{
		this.entries = entries;
		this.chart.set(0, entries);
		findTimeColumn();
	}
	
	/**
	 * Finds and sets the time column of this datasheet based on the entries
	 * @throws NoSuchEntryException
	 */
	public void findTimeColumn() throws NoSuchEntryException{
		if((timeColumn = entries.indexOf("time"))==-1)
			throw new NoSuchEntryException("A datasheet has to contain a time entry in the informtion"
					+ " line");
	}
	
	/**
	 * Sets items of the entry indices based on the entries of this
	 * and their corresponding columns
	 */
	public void readEntries() {
		for(int i = 0; i<this.entries.size(); i++)
			entryIndices.put(this.entries.get(i), i);
	}
	
	/**
	 * Reads the data of the datasheet at the given line; skips the first line
	 * of informational entries 
	 * @param line
	 * @return the list of data at line i+1
	 */
	public ArrayList<String> readData(int line){
		return this.getChart().get(line+1);
	}
	
	/**
	 * Finds the data that has time the closest to the specified one
	 * and returns it
	 * @param time
	 * @return the data found nearest to the specified time
	 */
	public ArrayList<String> readData(Calendar time){
		 try {
			 int nearestLine = findNearestLine(time, (chart.size()-1)/2, (chart.size()-1)/2);
			 return readData(nearestLine);
		 }catch(ParseException e) {
			 System.out.println("the time format must be in dd-M-yyyy hh:mm:ss, null returned");
			 e.printStackTrace();
		 }
		 return null;
	}
	
	/**
	 * Binary search
	 * @param index
	 * @return the index found
	 */
	private int findNearestLine (Calendar time, int index, int step) throws ParseException{
		int newstep = step/2+1;
		Calendar timeA = toCalendar(readData(index).get(timeColumn));
		Calendar timeB = toCalendar(readData(index-1).get(timeColumn));
		if(timeA.compareTo(time)>0)
			if(timeB.compareTo(time)<=0)
				return (timeA.getTimeInMillis()-time.getTimeInMillis()
						<time.getTimeInMillis()-timeB.getTimeInMillis())?index: index-1;
			else return findNearestLine(time, index+newstep, newstep);
		else return findNearestLine(time, index-newstep, newstep);
	}
	
	private int findNearestEarlierLine (Calendar time, int index, int step) throws ParseException{
		int newstep = step/2+1;
		Calendar timeA = toCalendar(readData(index).get(timeColumn));
		Calendar timeB = toCalendar(readData(index-1).get(timeColumn));
		if(timeA.compareTo(time)>0)
			if(timeB.compareTo(time)<=0)
				return index-1;
			else return findNearestEarlierLine(time, index+newstep, newstep);
		else return findNearestEarlierLine(time, index-newstep, newstep);
	}
	
	private Calendar toCalendar(String date) throws ParseException {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(TIME_FORMAT.parse(date));
		return calendar;
	}
	
	/**
	 * Uses linear approximation to find numeric data at undfined places,
	 * if there are one or less anchors near the data point, returns NaN instead.
	 * For none numerical data, the ones from the closest line are returned.
	 * For time data, the calendar object is returned
	 * @param time
	 * @return
	 */
	public ArrayList<String> readLinearizedData(Calendar time){
		try {
			int earlierLine = findNearestEarlierLine(time, (chart.size()-1)/2, (chart.size()-1)/2);
			long earlierTime = TIME_FORMAT.parse(readData(earlierLine).get(timeColumn)).getTime();
			long currentTime = time.getTimeInMillis();
			if(earlierTime == currentTime) {
				ArrayList<String> line = readData(earlierLine);
				ArrayList<String> data = new ArrayList<String>();
				for(int i = 0; i<line.size(); i++) {
					if(i == timeColumn) {
						data.add(line.get(i));
					}else try {
						data.add(line.get(i));
					}catch(NumberFormatException nfe) {
						data.add(line.get(i));
					}
				}
				return data;
			}else {
				ArrayList<String> lineA = readData(earlierLine);
				if(chart.size()-1<=earlierLine+1) {
					@SuppressWarnings("unchecked")
					ArrayList<String> line = (ArrayList<String>) lineA.clone();
					return line;
				}
				ArrayList<String> lineB = readData(earlierLine+1);
				long laterTime = TIME_FORMAT.parse(lineB.get(timeColumn)).getTime();
				ArrayList<String> data = new ArrayList<String>();
				for(int i = 0; i<Math.max(lineA.size(), lineB.size()); i++) {
					String itemA = (lineA.size()>i)?lineA.get(i):lineB.get(i);
					String itemB = (lineB.size()>i)?lineB.get(i):lineA.get(i);
					if(i == timeColumn) {
						data.add(time.toString());
					}else try {
						double valA = Double.parseDouble(itemA),
								valB = Double.parseDouble(itemB);
						data.add(String.valueOf((valB-valA)/(laterTime-earlierTime)*(currentTime-earlierTime)));
					}catch(NumberFormatException nfe) {
						data.add(itemA);
					}
				}
				return data;
			}
		} catch (ParseException e) {
			 System.out.println("the time format in data sheets must be in dd-M-yyyy hh:mm:ss, "
			 		+ "null returned");
			 e.printStackTrace();
		 }
		 return null;
	}
	
	public void addData(ArrayList<String> data) {
		Calendar time;
		try {
			time = toCalendar(data.get(timeColumn));
			int earlierLine;
			try {
				earlierLine = findNearestEarlierLine(time, (chart.size()-1)/2, (chart.size()-1)/2);
			}catch (ParseException e) {
				 System.out.println("the time format in data sheets must be in dd-M-yyyy hh:mm:ss, "
				 		+ "search terminated");
				 e.printStackTrace();
				 return;
			}
			long earlierTime = TIME_FORMAT.parse(readData(earlierLine).get(timeColumn)).getTime();
			if(earlierTime==time.getTimeInMillis())
				chart.set(1+earlierLine, data);
			else chart.add(2+earlierLine, data);
		} catch (ParseException e) {
			 System.out.println("there must be a time field in data and it"
			 		+ " must be in the format dd-M-yyyy hh:mm:ss:");
			 System.out.println(data);
			e.printStackTrace();
		}
	}
	
	/**
	 * Concatenates the current datasheet with another datasheet
	 * to form a new datasheet, dates are ordered chronologically automatically
	 * @param B the other datasheet
	 * @param keepFile whether the created datasheet should be saved in the system directory
	 * (if true, will be placed in the same folder)
	 * @return te created datasheet
	 */
	public Datasheet concat(Datasheet B, boolean keepFile) throws IOException{
		Datasheet concatenated = null;
		try {
			concatenated = new Datasheet(this.getParentFile(), this.getName()+"_"+B.getName());
			concatenated.setEntries(entries);
		} catch (IOException e) {
			System.out.println("IO exception encountered while reading datasheet");
			e.printStackTrace();
			throw e;
		} catch (NoSuchEntryException e) {
			System.out.println("Unlikely place to have this exception, what happened?");
			e.printStackTrace();
		}			
		for(int i = 0; i<chart.size()-1; i++) {
			ArrayList<String> data = readData(i);
			concatenated.addData(data);
		}
		for(int i = 0; i<B.chart.size()-1; i++) {
			ArrayList<String> data = B.readData(i);
			concatenated.addData(data);
		}
		return concatenated;
	}
	
	
}
