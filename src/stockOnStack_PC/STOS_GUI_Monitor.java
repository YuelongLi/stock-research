package stockOnStack_PC;

import codeLibrary.*;
import codeLibrary.Console;
import yahoofinance.Stock;
import yahoofinance.quotes.stock.StockQuote;
import yahoofinance.quotes.stock.StockStats;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.text.DefaultCaret;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * The collection monitor for stock on stack
 * @version 1.1
 * @author Yuelong
 */
public class STOS_GUI_Monitor extends JFrame {
	/**
	 * default serial version id
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * queue identifier
	 */
	final int index;
	JPanel framePanel;
	JPanel contentPanel;
	StockPanel currentDatas;
	
	/**
	 * Potential types of informations that are to be collected  
	 */
	private final String[] infoTypes =
	new String[]{"time","price","ask","bid","dayHigh","dayLow","eps","pe","peg","volume","AvgVolume",
			"askSize","bidSize"};
	
	/**
	 * Sizing fragments
	 */
	JSizer price;
	JSizer volume;
	JSizer content;
	
	/**
	 * the collector represents the back stage thread for collection
	 */
	Collector collector;
	/**
	 * the stock specified by the user's selection
	 */
	Stock stock;
	/**
	 * dataBase.Stock
	 */
	File stockBase;
	/**
	 * dataBase.Stock.CurrentWeek.CSV
	 */
	private CSV stockLogger;
	
	/**
	 * the collection only runs when this parameter is true
	 */
	private boolean running = true;
	/**
	 * Specifies the content that the monitor will be collecting
	 */
	HashMap<String, String> configuration;
	private JTextField textField;
	private JTextArea collectionLog = new JTextArea();;
	
	private boolean priceGraph = true;
	private boolean showPrice = true,
		showAskBid = false,
		showHighLow = false;
	private boolean volumeGraph = false;
	private boolean volumeCHGGraph = false;
	private JTextField txtPeriod;
	
	/**
	 * sets up the xml parsing configuration
	 * @param configuration2
	 */
	protected void setConfig(HashMap<String, String> configuration2){
		this.configuration=configuration2;
	}

	/**
	 * starts the collection once this method is called
	 * @throws IOException 
	 */
	protected void startCollection() throws IOException{
		DefaultCaret caret = (DefaultCaret) collectionLog.getCaret();
		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
		collector = new Collector();
		collector.start();
		this.addWindowListener(new WindowListener(){

			@Override
			public void windowActivated(WindowEvent arg0) {
				
			}

			@Override
			public void windowClosed(WindowEvent arg0) {
				
			}

			@Override
			public void windowClosing(WindowEvent arg0) {
				collector.stop();
				stockLogger.flushChart();
				collectionLog.setText("");
				Console.println("window closed");
			}

			@Override
			public void windowDeactivated(WindowEvent arg0) {
				
			}

			@Override
			public void windowDeiconified(WindowEvent arg0) {
			}

			@Override
			public void windowIconified(WindowEvent arg0) {
				
			}

			@Override
			public void windowOpened(WindowEvent arg0) {
				
			}
			
		});
	}
	
	/**
	 * The back stage collector for the stock on stack
	 * @author Yuelong
	 */
	class Collector extends Thread{
		public void run(){
			Calendar currentDate = Calendar.getInstance();
			String stockName = configuration.get("PreferedName");
			File dataBase = new File(STOS_GUI_Directory.getCurrentDataBase());
			stockBase = new File(dataBase, stockName);
			stockBase.mkdir();
			File year = new File(stockBase,String.valueOf(currentDate.get(Calendar.YEAR)));
			year.mkdir();
			
			try {
			collectionLog.append("Loading database...");
			
			CSV orgConfig = new CSV(stockBase, "/configuration.csv", true);
			if(Boolean.valueOf(configuration.get("UseOriginalConfig"))){
				if(orgConfig.exists()){
					ArrayList<ArrayList<String>> configs = orgConfig.getChart();
					HashMap<String,String> newConfig = new HashMap<String,String>();
					ArrayList<String> line1 = configs.get(0);
					ArrayList<String> line2 = configs.get(1);
					for(int i = 0; i<line1.size(); i++){
						newConfig.put(line1.get(i), line2.get(i));
					}
					configuration = newConfig;
				}else EventQueue.invokeLater(new Runnable(){
					public void run(){
						new JWarning("No original configuration found! Please specify your configuration");
						dispose();
						throw new NullPointerException("No original configuration found");
					}
				});
			}else{
				orgConfig.createNewFile();
				ArrayList<ArrayList<String>> configs = orgConfig.getChart();
				ArrayList<String> line1 = new ArrayList<String>(),
						line2 = new ArrayList<String>();
				for(String key : configuration.keySet()){
					line1.add(key);
					line2.add((String)configuration.get(key));
				}
				configs.clear();
				configs.add(line1);
				configs.add(line2);
			}
			orgConfig.flushChart();

			Date time = new Date();
			SimpleDateFormat date = new SimpleDateFormat("w_");
			stockLogger = new CSV(year, date.format(time)+stockName+".csv");

			currentDatas = new StockPanel(stockLogger.getChart());
			currentDatas.setBorder(new LineBorder(Color.GRAY));
			currentDatas.setBackground(Color.WHITE);
			currentDatas.setBounds(37, 117, 483, 292);
			contentPanel.add(currentDatas);
			content.add(currentDatas);
			
			ArrayList<ArrayList<String>> realtimeInfos = stockLogger.getChart();
			if(stockLogger.createNewFile()){
				ArrayList<String> pass1 = new ArrayList<String>();
				pass1.add(configuration.get("PreferedName"));
				realtimeInfos.add(pass1);
				ArrayList<String> pass2 = new ArrayList<String>();
				for(String type : infoTypes)pass2.add(type);
				realtimeInfos.add(pass2);
			}
			double gap = 1000*Double.valueOf(String.valueOf(configuration.get("RealGap")));
			SimpleDateFormat timeFormat = new SimpleDateFormat("MM.dd-HH:mm:ss");
			StockQuote realQuote;
			collectionLog.append("collection started\n");
			while(running){
				try{
					ArrayList<String> realtimeInfo = new ArrayList<String>();
					realQuote = stock.getQuote(true);
					realtimeInfo.add(timeFormat.format(time.getTime()));
					collectionLog.append(" time = "+timeFormat.format(time.getTime()));
					realtimeInfo.add(realQuote.getPrice().toString());
					collectionLog.append(", "+stockName+", Price: "+realQuote.getPrice()+"$\n");
					if(Boolean.valueOf(String.valueOf(configuration.get("AskBid")))){
						BigDecimal ask = realQuote.getAsk();
						BigDecimal bid = realQuote.getBid();
						if(ask!=null&&bid!=null){
							realtimeInfo.add(ask.toString());
							realtimeInfo.add(bid.toString());
							collectionLog.append("  Ask="+String.valueOf(ask));
							collectionLog.append("  Bid="+String.valueOf(bid));
						}
					}else{
						realtimeInfo.add("");
						realtimeInfo.add("");
					}
					if(Boolean.valueOf(String.valueOf(configuration.get("HighLow")))){
						BigDecimal high = realQuote.getDayHigh();
						BigDecimal low = realQuote.getDayLow();
						realtimeInfo.add(String.valueOf(high));
						realtimeInfo.add(String.valueOf(low));
						collectionLog.append("  dayHigh="+high);
						collectionLog.append("  dayLow="+low);
					}else{
						realtimeInfo.add("");
						realtimeInfo.add("");
					}
					
					if(Boolean.valueOf(String.valueOf(configuration.get("IncludeStats")))){
						StockStats stats = stock.getStats();
						BigDecimal pe = stats.getPe();
						BigDecimal peg = stats.getPeg();
						BigDecimal eps = stats.getEps();
						if(eps!=null)realtimeInfo.add(eps.toString());
						else realtimeInfo.add("");
						if(pe!=null)realtimeInfo.add(pe.toString());
						else realtimeInfo.add("");
						if(peg!=null)realtimeInfo.add(peg.toString());
						else realtimeInfo.add("");
						collectionLog.append("  eps="+eps);
						collectionLog.append("  peg="+peg);
						collectionLog.append("  pe="+pe);
					}else{
						realtimeInfo.add("");
						realtimeInfo.add("");
						realtimeInfo.add("");
					}
					if(Boolean.valueOf(configuration.get("Volume"))){
						long volume = realQuote.getVolume();
						realtimeInfo.add(String.valueOf(volume));
						collectionLog.append("  volume="+volume);
					}else{
						realtimeInfo.add("");
					}
					if(Boolean.valueOf(configuration.get("AvgVolume"))){
						long avgVolume = realQuote.getAvgVolume();
						realtimeInfo.add(String.valueOf(avgVolume));
						collectionLog.append("  AvgVolume="+avgVolume);
					}else{
						realtimeInfo.add("");
					}
					if(Boolean.valueOf(String.valueOf(configuration.get("AskBidSize")))){
						long askSize = realQuote.getAskSize();
						long bidSize = realQuote.getBidSize();
						realtimeInfo.add(String.valueOf(askSize));
						realtimeInfo.add(String.valueOf(bidSize));
						collectionLog.append("  askSize="+askSize);
						collectionLog.append("  bidSize="+bidSize);
					}else{
						realtimeInfo.add("");
						realtimeInfo.add("");
					}
					realtimeInfos.add(realtimeInfo);
					currentDatas.update();
					collectionLog.append("\n\n");
					Thread.sleep((long) gap);
					time = new Date();
				}catch (InterruptedException e) {
					stockLogger.flushChart();
					collectionLog.append("Collection terminated");
					EventQueue.invokeLater(new Runnable(){
						public void run(){
							new JWarning("Collection Interrupted!");
						}
					});
				}
			}
			}catch(FileNotFoundException e1){
				EventQueue.invokeLater(new Runnable(){
					public void run(){
						new JWarning("The data file is being used by other softwares, please close it and restart");
					}
				});
			}catch (IOException e2) {
				e2.printStackTrace();
				stockLogger.flushChart();
				collectionLog.append("Collection terminated due to I/O exception");
			} catch (NullPointerException e){
				e.printStackTrace();
			}
		}
		private void getHistory(){
			
		}
	}
	
	/**
	 * Create the frame.
	 */
	protected STOS_GUI_Monitor(int index) {
		setIconImage(Toolkit.getDefaultToolkit().getImage("StockonStack.png"));
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		this.index = index;
		this.stock = STOS_Console.stocks.get(index);
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds((screenSize.width-1051)/2, (screenSize.height-688)/2, 1051, 688);
		framePanel = new JPanel();
		framePanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(framePanel);
		framePanel.setLayout(null);
		contentPanel = new JPanel();
		contentPanel.setBackground(Color.WHITE);
		contentPanel.setBounds(0, 0, 1033, 641);
		framePanel.add(contentPanel);
		contentPanel.setLayout(null);
		
		collectionLog.setEditable(false);
		
		JLabel lblNewLabel = new JLabel("Current Datas");
		lblNewLabel.setFont(new Font("Arial", Font.PLAIN, 25));
		lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel.setBounds(171, 73, 205, 39);
		contentPanel.add(lblNewLabel);
		
		JPanel panel_1 = new JPanel();
		panel_1.setBackground(new Color(51, 153, 51));
		panel_1.setBounds(0, 0, 1033, 65);
		contentPanel.add(panel_1);
		panel_1.setLayout(new BorderLayout(0, 0));
		
		JLabel lblCollectingData = new JLabel("Collecting data");
		lblCollectingData.setForeground(SystemColor.inactiveCaptionBorder);
		lblCollectingData.setHorizontalAlignment(SwingConstants.CENTER);
		lblCollectingData.setFont(new Font("Arial", Font.PLAIN, 25));
		panel_1.add(lblCollectingData, BorderLayout.CENTER);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(534, 117, 460, 422);
		contentPanel.add(scrollPane);
		
		scrollPane.setViewportView(collectionLog);
		
		JLabel lblCollectionDialog = new JLabel("Collection Log");
		lblCollectionDialog.setHorizontalAlignment(SwingConstants.CENTER);
		lblCollectionDialog.setFont(new Font("Arial", Font.PLAIN, 25));
		lblCollectionDialog.setBounds(667, 73, 205, 39);
		contentPanel.add(lblCollectionDialog);
		
		JPanel pricePanel = new JPanel();
		pricePanel.setBorder(new LineBorder(Color.GRAY));
		pricePanel.setBackground(Color.WHITE);
		pricePanel.setBounds(37, 422, 257, 102);
		contentPanel.add(pricePanel);
		pricePanel.setLayout(null);
		
		JCheckBox chckbxAskbid = new JCheckBox("Ask/Bid");
		chckbxAskbid.setBounds(4, 37, 91, 27);
		pricePanel.add(chckbxAskbid);
		chckbxAskbid.setBackground(Color.WHITE);
		
		JCheckBox chckbxHighLow = new JCheckBox("High & Low");
		chckbxHighLow.setBounds(4, 66, 109, 27);
		pricePanel.add(chckbxHighLow);
		chckbxHighLow.setBackground(Color.WHITE);
		
		JPanel volumePanel = new JPanel();
		volumePanel.setBorder(new LineBorder(Color.GRAY));
		volumePanel.setBackground(new Color(255, 255, 255));
		volumePanel.setBounds(37, 537, 257, 71);
		contentPanel.add(volumePanel);
		volumePanel.setLayout(null);
		
		JCheckBox chckbxVolume = new JCheckBox("Volume");
		chckbxVolume.setBounds(5, 9, 125, 27);
		volumePanel.add(chckbxVolume);
		chckbxVolume.setBackground(Color.WHITE);
		
		JCheckBox chckbxVVolume = new JCheckBox("volume CHG");
		chckbxVVolume.setBounds(5, 35, 125, 27);
		volumePanel.add(chckbxVVolume);
		chckbxVVolume.setBackground(Color.WHITE);
		
		JLabel lblEps = new JLabel("EPS:");
		lblEps.setFont(new Font("Arial", Font.PLAIN, 17));
		lblEps.setBounds(304, 422, 45, 18);
		contentPanel.add(lblEps);
		
		JLabel lblPe = new JLabel("PE:");
		lblPe.setFont(new Font("Arial", Font.PLAIN, 17));
		lblPe.setBounds(308, 453, 45, 18);
		contentPanel.add(lblPe);
		
		JLabel lblPeg = new JLabel("PEG:");
		lblPeg.setFont(new Font("Arial", Font.PLAIN, 17));
		lblPeg.setBounds(304, 484, 45, 18);
		contentPanel.add(lblPeg);
		
		JLabel EPSValue = new JLabel("");
		EPSValue.setFont(new Font("Arial", Font.PLAIN, 17));
		EPSValue.setBounds(363, 422, 157, 18);
		contentPanel.add(EPSValue);
		
		JLabel PEValue = new JLabel("");
		PEValue.setFont(new Font("Arial", Font.PLAIN, 17));
		PEValue.setBounds(363, 470, 157, 18);
		contentPanel.add(PEValue);
		
		JLabel PEGValue = new JLabel("");
		PEGValue.setFont(new Font("Arial", Font.PLAIN, 17));
		PEGValue.setBounds(363, 520, 157, 18);
		contentPanel.add(PEGValue);
		
		JComboBox<String> comboBox = new JComboBox();
		comboBox.setFont(new Font("Arial", Font.PLAIN, 16));
		comboBox.setModel(new DefaultComboBoxModel<String>(new String[] {"Seconds", "Minites", "Hours"}));
		comboBox.setBounds(886, 552, 108, 27);
		contentPanel.add(comboBox);
		

		JLabel timeLeft = new JLabel("New label");
		timeLeft.setFont(new Font("Arial", Font.PLAIN, 16));
		timeLeft.setBounds(782, 552, 211, 27);
		timeLeft.setVisible(false);
		contentPanel.add(timeLeft);
		
		JToggleButton btnTerminateCollectionIn = new JToggleButton("Terminate Collection In");
		btnTerminateCollectionIn.setFont(new Font("Arial", Font.PLAIN, 16));
		btnTerminateCollectionIn.setBounds(534, 552, 234, 27);
		contentPanel.add(btnTerminateCollectionIn);
		btnTerminateCollectionIn.addItemListener(new ItemListener(){
			SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
			private Thread terminationTimer;
			public void itemStateChanged(ItemEvent e){
				if(btnTerminateCollectionIn.isSelected()){
					btnTerminateCollectionIn.setText("Terminating Collection In");
					comboBox.setVisible(false);
					textField.setVisible(false);
					timeLeft.setVisible(true);
					terminationTimer = new Thread(){
						public void run(){
							try{
								long timePeriod = Long.valueOf(textField.getText())*1000*(long)Math.pow(60,comboBox.getSelectedIndex());
								Date time = new Date();
								long startTime = time.getTime();
								long currentTime = time.getTime();
								while(!Thread.interrupted()&&timePeriod-(currentTime-startTime)>0){
									timeLeft.setText(format.format(5000*3600+timePeriod-(currentTime-startTime)));
									time = new Date();
									currentTime = time.getTime();
									Thread.sleep(1000);
								}
								if(!Thread.interrupted()) {
									running = false;
									stockLogger.flushChart();
									collectionLog.append("\nCollection terminated");
								}
								btnTerminateCollectionIn.setEnabled(false);
								}catch(NumberFormatException | InterruptedException e1){
								
							}
						}
					};
					terminationTimer.start();
				}else if(terminationTimer!=null){
					terminationTimer.interrupt();
					btnTerminateCollectionIn.setText("Terminate Collection In");
					comboBox.setVisible(true);
					textField.setVisible(true);
					timeLeft.setVisible(false);
					timeLeft.setText("");
				}
			}
		});
		
		
		textField = new JTextField();
		textField.setFont(new Font("Arial", Font.PLAIN, 16));
		textField.setBounds(782, 552, 90, 27);
		contentPanel.add(textField);
		textField.setColumns(10);
		
		JToggleButton tglbtnPause = new JToggleButton("Pause");
		tglbtnPause.setFont(new Font("Arial", Font.PLAIN, 15));
		tglbtnPause.setBounds(534, 592, 234, 27);
		contentPanel.add(tglbtnPause);
		tglbtnPause.addActionListener(new ActionListener(){
			boolean paused =false;
			public void actionPerformed(ActionEvent e){
				paused = tglbtnPause.isSelected();
				if(paused){
						collector.suspend();
					tglbtnPause.setText("Resume");
				}
				else{
					collector.resume();;
					tglbtnPause.setText("Pause");
				}
			}
		});
		
		JButton btnTerminate = new JButton("Terminate");
		btnTerminate.setFont(new Font("Arial", Font.PLAIN, 15));
		btnTerminate.setBounds(776, 592, 218, 27);
		contentPanel.add(btnTerminate);
		btnTerminate.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
					running = false;
					stockLogger.flushChart();
					collectionLog.append("\nCollection terminated");
			}
		});
		
		price = new JSizer(pricePanel);
		
		JCheckBox checkBoxPrice = new JCheckBox("Price");
		checkBoxPrice.setSelected(true);
		checkBoxPrice.setBackground(Color.WHITE);
		checkBoxPrice.setBounds(4, 9, 91, 27);
		pricePanel.add(checkBoxPrice);
		
		ActionListener checked = new ActionListener(){
			public void actionPerformed(ActionEvent e){
				JCheckBox a = (JCheckBox) e.getSource();
				if(a==checkBoxPrice){
					priceGraph = a.isSelected();
					showPrice = a.isSelected();
				}
				if(a==chckbxAskbid){
					priceGraph = checkBoxPrice.isSelected();
					showAskBid = a.isSelected();
				}
				if(a==chckbxHighLow){
					priceGraph = checkBoxPrice.isSelected();
					showHighLow = a.isSelected();
				}
				if(a==chckbxVolume){
					volumeGraph = a.isSelected();
				}
				if(a==chckbxVVolume){
					volumeCHGGraph = a.isSelected();
				}
				currentDatas.repaint();
			}
		};
		
		checkBoxPrice.addActionListener(checked);
		chckbxAskbid.addActionListener(checked);
		chckbxHighLow.addActionListener(checked);
		chckbxVolume.addActionListener(checked);
		chckbxVVolume.addActionListener(checked);
		
		JPanel panel = new JPanel();
		panel.setBackground(new Color(255, 102, 51));
		panel.setBorder(null);
		panel.setBounds(124, 23, 119, 2);
		pricePanel.add(panel);
		
		JPanel panel_2 = new JPanel();
		panel_2.setBorder(null);
		panel_2.setBackground(new Color(0, 153, 153));
		panel_2.setBounds(124, 49, 58, 2);
		pricePanel.add(panel_2);
		
		JPanel panel_3 = new JPanel();
		panel_3.setBorder(null);
		panel_3.setBackground(new Color(255, 255, 51));
		panel_3.setBounds(185, 49, 58, 2);
		pricePanel.add(panel_3);
		
		JPanel panel_4 = new JPanel();
		panel_4.setBorder(null);
		panel_4.setBackground(new Color(51, 153, 102));
		panel_4.setBounds(123, 80, 58, 2);
		pricePanel.add(panel_4);
		
		JPanel panel_5 = new JPanel();
		panel_5.setBorder(null);
		panel_5.setBackground(new Color(255, 0, 51));
		panel_5.setBounds(185, 80, 58, 2);
		pricePanel.add(panel_5);
		price.addAllComponents();
		price.addListener(0);
		
		volume = new JSizer(volumePanel);
		
		JPanel panel_6 = new JPanel();
		panel_6.setBorder(null);
		panel_6.setBackground(new Color(51, 153, 204));
		panel_6.setBounds(124, 24, 119, 2);
		volumePanel.add(panel_6);
		
		JPanel panel_7 = new JPanel();
		panel_7.setBorder(null);
		panel_7.setBackground(new Color(153, 0, 153));
		panel_7.setBounds(124, 50, 119, 2);
		volumePanel.add(panel_7);
		volume.addAllComponents();
		volume.addListener(0);
		
		JLabel lblPeriodMultiples = new JLabel("Period:");
		lblPeriodMultiples.setFont(new Font("Arial", Font.PLAIN, 16));
		lblPeriodMultiples.setBounds(304, 557, 56, 18);
		contentPanel.add(lblPeriodMultiples);
		
		txtPeriod = new JTextField();
		txtPeriod.setBounds(363, 554, 86, 24);
		contentPanel.add(txtPeriod);
		txtPeriod.setColumns(10);
		txtPeriod.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				currentDatas.periodMultiples = Integer.valueOf(txtPeriod.getText());
				repaint();
			}
		});
		
		content = new JSizer(contentPanel);
		content.addAllComponents();
		content.addListener(this);
	}
	
	class StockPanel extends JPanel{
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		double lastVolume=0;
		long lastTime=new Date().getTime()/1000;
		
		Locator locator = new Locator(this){
			public void extra(){
				locator.setOrigninOffset(new double[]{-locator.I_x()/2,0});
			}
		};
		int periodUnit = Double.valueOf(configuration.get("RealGap")).intValue();
		int periodMultiples = 50;
		
		ArrayList<ArrayList<String>> list;
		
		ArrayList<Long> time = new ArrayList<Long>();
		SimpleDateFormat dateFormat = new SimpleDateFormat("MM.dd-HH:mm:ss");
		ArrayList<Double> price = new ArrayList<Double>(),
				ask = new ArrayList<Double>(),
				bid = new ArrayList<Double>();
				double dayHigh,
				dayLow;
				
		ArrayList<Double> volume = new ArrayList<Double>(),
				chgVolume = new ArrayList<Double>();
		
		public StockPanel(ArrayList<ArrayList<String>> dataList){
			list = dataList;
			locator.setOrigninOffset(new double[]{0,0});
			
			for(int i = 2; i<list.size(); i++){
				ArrayList<String> current = list.get(i);
				String pass;
				try {
					time.add(dateFormat.parse(current.get(0)).getTime()/1000);
				} catch (ParseException e) {
					Console.println("format error in line"+(current.size()-1));
					e.printStackTrace();
				}
				price.add(Double.valueOf(current.get(1)));
				if(!(pass = current.get(2)).equals(""))ask.add(Double.valueOf(pass));
				else ask.add(Double.NaN);
				if(!(pass = current.get(3)).equals(""))bid.add(Double.valueOf(pass));
				else bid.add(Double.NaN);
				
				if(!(pass = current.get(9)).equals("")){
					volume.add(Double.valueOf(pass));
					chgVolume.add((volume.get(i-2)-lastVolume)/(time.get(i-2)-lastTime));
					lastVolume = volume.get(i-2);
					lastTime = time.get(i-2);
				}
				else {
					volume.add(Double.NaN);
					chgVolume.add(Double.NaN);
				}
				
			}
		}
		
		private void update(){
			String pass;
			ArrayList<String> current = list.get(list.size()-1);
			
			try {
				time.add(dateFormat.parse(current.get(0)).getTime()/1000);
			} catch (ParseException e) {
				Console.println("format error in line"+(current.size()-1));
				e.printStackTrace();
			}
			price.add(Double.valueOf(current.get(1)));
			if(!(pass = current.get(2)).equals(""))ask.add(Double.valueOf(pass));
			else ask.add(Double.NaN);
			if(!(pass = current.get(3)).equals(""))bid.add(Double.valueOf(pass));
			else bid.add(Double.NaN);
			if(!(pass = current.get(9)).equals("")){
				volume.add(Double.valueOf(pass));
				chgVolume.add((volume.get(price.size()-1)-lastVolume)/(time.get(price.size()-1)-lastTime));
				lastVolume = Double.valueOf(pass);
				lastTime = time.get(price.size()-1);
			}
			else{
				volume.add(Double.NaN);
				chgVolume.add(Double.NaN);
			}
			if(!(pass = current.get(4)).equals(""))dayHigh = Double.valueOf(pass);
			else dayHigh = Double.NaN;
			if(!(pass = current.get(5)).equals(""))dayLow = Double.valueOf(pass);
			else dayLow = Double.NaN;
			repaint();
		}
		
		Stroke regular = new BasicStroke(1.6f);
		
		protected void paintComponent(Graphics g){
			super.paintComponent(g);
			Graphics2D gr = (Graphics2D)g;
			gr.setStroke(regular);
			double priceVariance = getVariance(price);
			double priceAverage = getAverage(price);
			double volumeVariance = getVariance(volume);
			double volumeAverage = getAverage(volume);
			double avgVolumeVariance = getVariance(chgVolume);
			double avgVolumeAverage = getAverage(chgVolume);
			int index = periodMultiples;
			for(int i = price.size()-1; (i-1>0)&&i>price.size()-periodMultiples-1; i--){
				double x2 = getX(index);
				double x1 = getX(index-1);
				index--;
				if(priceGraph){
					if(showHighLow) {
						priceVariance = dayHigh-dayLow;
						priceAverage = (dayHigh + dayLow)/2;
					}
					if(showPrice){
						gr.setColor(new Color(255, 102, 51));
						gr.drawLine(
								locator.toX(x1), 
								locator.toY(getY(price.get(i-1),priceVariance,priceAverage)), 
								locator.toX(x2), 
								locator.toY(getY(price.get(i),priceVariance,priceAverage))
								);
					}
					if(showAskBid){
						if(!(ask.get(i).isNaN()||ask.get(i-1).isNaN())){
							gr.setColor(new Color(0, 153, 153));
							gr.drawLine(
									locator.toX(x1), 
									locator.toY(getY(ask.get(i-1),priceVariance,priceAverage)), 
									locator.toX(x2), 
									locator.toY(getY(ask.get(i),priceVariance,priceAverage))
									);}
						if(!(bid.get(i).isNaN()||bid.get(i-1).isNaN())){
							gr.setColor(new Color(255, 255, 51));
							gr.drawLine(
								locator.toX(x1), 
								locator.toY(getY(bid.get(i-1),priceVariance,priceAverage)), 
								locator.toX(x2), 
								locator.toY(getY(bid.get(i),priceVariance,priceAverage))
								);
						}
					}
					if(showHighLow){
						if(dayHigh != Double.NaN){
							gr.setColor(new Color(51, 153, 102));
							gr.drawLine(
								locator.toX(0), 
								locator.toY(getY(dayHigh,priceVariance,priceAverage)), 
								locator.toX(locator.I_x()), 
								locator.toY(getY(dayHigh,priceVariance,priceAverage))
								);
						}
						if(dayLow != Double.NaN){
							gr.setColor(new Color(255, 0, 51));
							gr.drawLine(
								locator.toX(0), 
								locator.toY(getY(dayLow,priceVariance,priceAverage)), 
								locator.toX(locator.I_x()), 
								locator.toY(getY(dayLow,priceVariance,priceAverage))
								);
						}
					}
				}
				if(volumeGraph&&!(volume.get(i).isNaN()||volume.get(i-1).isNaN())){
					gr.setColor(new Color(51, 153, 204));
					gr.drawLine(
							locator.toX(x1), 
							locator.toY(getY(volume.get(i-1),volumeVariance,volumeAverage)), 
							locator.toX(x2), 
							locator.toY(getY(volume.get(i),volumeVariance,volumeAverage))
							);
				}
				if(volumeCHGGraph&&!(chgVolume.get(i).isNaN()||chgVolume.get(i-1).isNaN())){
					gr.setColor(new Color(153, 0, 153));
					gr.drawLine(
							locator.toX(x1), 
							locator.toY(getY(chgVolume.get(i-1),avgVolumeVariance,avgVolumeAverage)), 
							locator.toX(x2), 
							locator.toY(getY(chgVolume.get(i),avgVolumeVariance,avgVolumeAverage))
							);
				}
			}
		} 
		
		private double getX(int index){
			double I_x = locator.I_x();
			return I_x-(double)(periodMultiples-index)/periodMultiples*I_x;
		}
		
		private double getY(double price, double variance, double average){
			return (price-average)/variance*(locator.I_y()-2);
		}
		
		private double getVariance(ArrayList<Double> data){
			double max = Double.MIN_VALUE;
			for(int i = data.size()-periodMultiples; i<data.size(); i++){
				if(i>=0&&data.get(i)>max) max = data.get(i);
			}
			double min = Double.MAX_VALUE;
			for(int i = data.size()-periodMultiples; i<data.size(); i++){
				if(i>=0&&data.get(i)<min) min = data.get(i);
			}
			if(max-min==0)max+=1;
			return max-min;
		}
		
		private double getAverage(ArrayList<Double> data){
			double sum = 0;
			int terms  = 0;
			for(int i = data.size()-periodMultiples-1; i<data.size(); i++){
				if(i>=0&&!data.get(i).isNaN()){
					sum+=data.get(i);
					terms ++;
				}
			}
			if(terms == 0)terms++;
			return sum/terms;
		}
	}
}
/*
 * © Copyright 2017
 * Cannot be used without authorization
 */