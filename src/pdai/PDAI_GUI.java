package pdai;
/**
 * @author 越隆
 * @version 1.1
 * @param HPrice[] Requested Price according to date
 * @param HDate[] Date reference for HistoryPrice[]
 * @param RPrice[] Real time Price from database
 * @param RDate[] References for RPrice[]
 */
import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.*;
import java.util.*;
import java.util.List;

import codeLibrary.Console;
import codeLibrary.JSizer;
import javax.swing.border.LineBorder;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.ActionEvent;

public class PDAI_GUI extends JFrame{
	/**
	 * @definition Screen Width
	 */
	final double SWidth;
	/**
	 * @definition Screen Height
	 */
	final double SHeight;
	
	protected final int ListIndex;
	
	private JPanel contentPane;
	private JSizer sizer;
	private JPanel mainPane;
	private JTextField InstrumentSearch;
	private JTextField PeriodInput;
	private JTextField AccuracyInput;
	private JScrollBar timeScroll;
	JProgressBar progressBar;
	JLabel lblInstrument;
	
	/**
	 * relative time: standarlized x coordinate
	 * <div/><b> unit </b> standard exchange unit
	 */
	protected List <Double> x;
	/**
	 * relative stock price: standarlized y coordinate
	 * <div/><b> unit </b> standard exchange unit
	 */
	protected List <Double> y;
	int percent = 0;
	
	int I_X = 573;
	double I_x;
	/*
	 * @definition units of time that could be contained within the screen
	 */
	List <Double> X = new ArrayList<Double>(0);
	int Xlength = 0;
	//xax
	List <Double> Y = new ArrayList<Double>(0);
	//Tay
	int Ylength = 0;
	List <Double> XY = new ArrayList<Double>(0);
	//xay
	int XYlength = 0;
	List <Double> C = new ArrayList<Double>(0);
	//Tax
	int Clength = 0;
	List <Double> D = new ArrayList<Double>(0);
	int Dlength = 0;
	List <Double> AD = new ArrayList<Double>(0);
	int ADlength = 0;
	List <Double> DD = new ArrayList<Double>(0);
	int DDlength = 0;
	List <Double> DDAD = new ArrayList<Double>(0);
	int DDADlength = 0;
	double demultiplyer = 0.8;
	int Scale_x = 30;
	/**
	 * @definition amount of points that are given its coordinate-value every unit of time
	 */
	double PointDensity = 25;
	/**
	 * @definition inaccurate period, which is caused by the average calculation, locates at the end of the average price array
	 * @unit units*pointDensity
	 */
	int AC;
	/**@definition inaccurate period, which is caused by the average calculation and xaverage calculation, locates at the end of the average price array
	* @unit units*pointDensity
	*/
	int XAC;
	/**@definition inaccurate period, which is caused by the average calculation, xaverage calculation and differential average calculation, locates at the end of the average price array
	* @unit units*pointDensity
	*/
	int ADC = 0;
	
	int I_d;
	TradePanel tradePanel = new TradePanel();
	IndicatorPanel indicatorPanel = new IndicatorPanel();
	JScrollPane TradeChart;
	JScrollPane IndicaterChart;
	JComboBox PeriodUnit;
	class TradePanel extends JPanel{
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		/**
		 * @definition pixels available for [Price or APrice] across the screen
		 */
		int I_Y = 327;
		/**
		 * @definition pixels per unit of [Price or APrice]
		 * @unit pixels
		 */
		int Scale_y = 30;
		/**
		 * @definition units of [Price or APrice] that could be contained within the screen 
		 * @unit standard
		 */
		
		double I_y = I_Y / Scale_y;
		int i;
		int r;
		int RT;
		double RP;
		List <Double> A = new ArrayList<Double>();
		int Alength = 0;
		List <Double> B = new ArrayList<Double>();
		int Blength = 0;
		int Xa;
		int Xb;
		
		protected void paintComponent(Graphics g){
			super.paintComponent(g); 
			try{
			g.setColor(Color.CYAN);
			for(int a = 0; a < r-1; a++){
				g.drawLine((int)Math.round((x.get(a)*Scale_x)),(int) Math.round(-(y.get(a)*Scale_y)+I_Y), (int) Math.round((x.get(a+1)*Scale_x)),(int) Math.round(-y.get(a+1)*Scale_y+I_Y));
			}
			g.setColor(Color.ORANGE);
			for(int a = 0; a <Alength-1; a++){
				g.drawLine((int)Math.round(A.get(a)*Scale_x), (int)Math.round(-(B.get(a)*Scale_y)+I_Y), (int)Math.round(A.get(a+1)*Scale_x), (int)Math.round(-B.get(a+1)*Scale_y+I_Y)); 
			}	
			g.setColor(Color.BLUE);
			for(int a = 0; a <Xlength-1; a++){
				g.drawLine((int)Math.round(X.get(a)*Scale_x), (int)Math.round(-XY.get(a)*Scale_y+I_Y), (int)Math.round(X.get(a+1)*Scale_x), (int)Math.round(-XY.get(a+1)*Scale_y)+I_Y);
			}
			}catch(IndexOutOfBoundsException e){
				try {
					Thread.sleep((long) 1);
				} catch (InterruptedException e1) {
					// 
					e1.printStackTrace();
				}
			}
			g.setColor(Color.getHSBColor(120,20,78));
			for(int i = 0; i<I_x; i++){
				 g.drawLine(i*Scale_x, 0, i*Scale_x, I_Y);
			}
			for(int i = 0; i<I_y; i++){
				g.drawLine(0, -i*Scale_y+I_Y, I_X, -i*Scale_y+I_Y);
			}
			g.setColor(Color.green);
			for(Double pass:PDAI_CentralConsole.AI.get(ListIndex).buyX){
				g.drawLine((int)(pass*Scale_x), 0, (int)(pass*Scale_x), I_Y);
			}
			g.setColor(Color.red);
			for(Double pass:PDAI_CentralConsole.AI.get(ListIndex).sellX){
				g.drawLine((int)(pass*Scale_x), 0, (int)(pass*Scale_x), I_Y);
			}
			g.setColor(Color.orange);
			for(Double pass:PDAI_CentralConsole.AI.get(ListIndex).clearX){
				g.drawLine((int)(pass*Scale_x), 0, (int)(pass*Scale_x), I_Y);
			}
			Xa = (int)((double)timeScroll.getValue()/(I_X-TradeChart.getViewport().getWidth())*(TradeChart.getViewport().getWidth()-ADC/PointDensity*Scale_x)+timeScroll.getValue());
			Xb = Xa+(int)(ADC/PointDensity*Scale_x);
			g.setColor(Color.lightGray);
			g.drawLine(Xa, 0, Xa, 768);
			g.setColor(Color.BLACK);
			g.drawLine(Xb , 0, Xb, 768);
			TradeChart.repaint();
		}
		TradePanel() throws Exception{
			setPreferredSize(new Dimension(I_X,I_Y));
			setBackground(Color.WHITE);
		}
		void setData(){
			r = x.size();
			I_x = x.get(r-1)-x.get(0);
			I_y  = getBiggest(y)-getSmallest(y);
			I_Y = (int) I_y*Scale_y;
			i = (int) (I_x*PointDensity);
		}
		void clearData(){
			C.clear();
			Y.clear();
			A.clear();
			B.clear();
			X.clear();
			XY.clear();
		}

		void getAverage(){
			AC = (int) (PDAI_CentralConsole.AI.get(ListIndex).AveragePeriod*PointDensity/2);
			Clength = (i-AC);
			Ylength = (i-AC);
			Alength = (i-AC);
			Blength = (i-AC);
			I_Y = (int) I_y*Scale_y;
		     for(int a = 0; a<Clength; a++){
		    	percent = (int) (a*100/(i-1));
		  		C.add(Double.valueOf(a/PointDensity));
		  		Y.add(Double.valueOf((PDAI_CentralConsole.TradeAverage(ListIndex, a/PointDensity))));
				progressBar.setValue(percent);
		      }
		      A = C;
		      B = Y;
		}
		void getXAverage(){
			XAC = (int)(AC+PDAI_CentralConsole.AI.get(ListIndex).XAveragePeriod*PointDensity/2);
			Xlength = (i-XAC);
			XYlength = (i-XAC);
			Dlength = XYlength-1;
			for(int a = 0; a<XYlength; a++){
				percent = (int) (a*100/(i-1));
				X.add(Double.valueOf(a/PointDensity));
				XY.add(Double.valueOf((PDAI_CentralConsole.XTradeAverage(ListIndex, a/PointDensity))));
			}
		}
		void appendXAverage(){
			
		}
		void refreshSize(){
			I_X = (int) (Scale_x*I_x);
			I_Y = (int) (Scale_y*I_y);
			setPreferredSize(new Dimension(I_X,I_Y));
			repaint();
		}
	}
	
	class IndicatorPanel extends JPanel{
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		int Scale_d = 30;
		int I_d = 327/Scale_d;
		int I_D = 327;
		//I_d changed to <set data>
		int Xa;
		int Xb;
		
		IndicatorPanel() throws Exception{
			setPreferredSize(new Dimension(I_X,I_D));
			setBackground(Color.white);
		}
		protected void paintComponent(Graphics g){
			try{
			super.paintComponent(g);
			g.setColor(Color.GRAY);
			g.drawLine(0,I_D/2, I_X, I_D/2);
			g.setColor(Color.RED);
			g.drawLine(0, I_D/2+Scale_d, I_X, I_D/2+Scale_d);
			g.setColor(Color.GREEN);
			g.drawLine(0, I_D/2-Scale_d, I_X, I_D/2-Scale_d);
			g.setColor(Color.MAGENTA);
			for(int a = 0; a <Dlength-1; a++){
				g.drawLine((int)Math.round(X.get(a)*Scale_x), (int)Math.round((-D.get(a)+ I_d/2)*Scale_d), (int)Math.round(X.get(a+1)*Scale_x), (int)Math.round((-D.get(a+1)+ I_d/2)*Scale_d)); 
			}
			g.setColor(Color.GREEN);
			for(int a = 0; a < ADlength-1; a++){
				g.drawLine((int)Math.round(X.get(a)*Scale_x), (int)Math.round((-AD.get(a)+ I_d/2)*Scale_d), (int)Math.round(X.get(a+1)*Scale_x), (int)Math.round((-AD.get(a+1)+ I_d/2)*Scale_d)); 
			}
			g.setColor(Color.ORANGE);
			for(int a = 0; a < DDlength-1; a++){
				g.drawLine((int)Math.round(X.get(a)*Scale_x), (int)Math.round((-DD.get(a)+ I_d/2)*Scale_d), (int)Math.round(X.get(a+1)*Scale_x), (int)Math.round((-DD.get(a+1)+ I_d/2)*Scale_d)); 
			}
			g.setColor(Color.RED);
			for(int a = 0; a < DDlength-1; a++){
				g.drawLine((int)Math.round(X.get(a)*Scale_x), (int)Math.round((-DDAD.get(a)+I_d/2)*Scale_d), (int)Math.round(X.get(a+1)*Scale_x),(int)Math.round((-DDAD.get(a+1)+I_d/2)*Scale_d));
			}
			g.setColor(Color.BLACK);
			Xa = (int)((double)timeScroll.getValue()/(I_X-TradeChart.getViewport().getWidth())*(TradeChart.getViewport().getWidth()-ADC/PointDensity*Scale_x)+timeScroll.getValue());
			Xb = Xa+(int)(ADC/PointDensity*Scale_x);
			g.drawLine(Xa, 0, Xa, 768);
			g.setColor(Color.lightGray);
			g.drawLine(Xb , 0, Xb, 768);
			}catch(IndexOutOfBoundsException e){
				try {
					Thread.sleep((long) 1);
				} catch (InterruptedException e1) {
					// 
					e1.printStackTrace();
				}
			}
			IndicaterChart.repaint();
		}
		
		void clearData(){
			D.clear();
			AD.clear();
			DD.clear();
			DDAD.clear();
		}
		
		/**
		 * First order differential calculation
		 */
		void getDifferential(){
			for(int i = 0; i< Dlength; i++ ){
				percent = (int) (i*100/(I_x*PointDensity-1));
				D.add(Double.valueOf(PDAI_CentralConsole.Differential(ListIndex, i/PointDensity)));
				progressBar.setValue(percent);
			}
		}

		/**
		 * Second order differential calculation
		 */
		void getDDifferential(){
			ADC = (int)(XAC+PDAI_CentralConsole.AI.get(ListIndex).DifAveragePeriod*PointDensity/2);
			ADlength = (int)(I_x*PointDensity-ADC);
			DDlength = (int)(I_x*PointDensity-ADC)-1;
			for(int i = 0; i < ADlength; i++){
				percent = (int) (i*100/(I_x*PointDensity-1));
				AD.add(Double.valueOf(PDAI_CentralConsole.DifAverage( ListIndex,i/PointDensity)));
				progressBar.setValue(percent);
			}
			for(int i = 0; i < DDlength; i++){
				percent = (int) (i*100/(I_x*PointDensity-1));
				DD.add(Double.valueOf(PDAI_CentralConsole.DDifferential( ListIndex,i/PointDensity)));
				progressBar.setValue(percent);
			}
		}
		
		/**
		 * Comprehensive Analysis
		 */
		void getDDAD(){
			DDADlength = DDlength;
			for(int i = 0; i < DDADlength; i++){
				percent = (int) (i*100/(I_x*PointDensity-1));
				DDAD.add(Double.valueOf(PDAI_CentralConsole.DDAD( ListIndex,i/PointDensity)));
				progressBar.setValue(percent);
			}
		}
		
		void refreshSize(){
			I_D = (int) I_d*Scale_d;
			setPreferredSize(new Dimension(I_X,I_D));
			repaint();
		}
	}	
	
	void refreshChartSize(){
		tradePanel.refreshSize();
		indicatorPanel.refreshSize();
		TradeChart.getHorizontalScrollBar().revalidate();
	}
	
	/**
	 * <div>Launcher icon made by <a href="http://www.flaticon.com/authors/madebyoliver" title="Madebyoliver">Madebyoliver</a> from <a href="http://www.flaticon.com" title="Flaticon">www.flaticon.com</a> is licensed by <a href="http://creativecommons.org/licenses/by/3.0/" title="Creative Commons BY 3.0" target="_blank">CC 3.0 BY</a></div>
	 */
	public PDAI_GUI(int ListIndex) throws Exception {
		Dimension ScreenSize = Toolkit.getDefaultToolkit().getScreenSize();
		SWidth = ScreenSize.getWidth();
		SHeight = ScreenSize.getHeight();
		System.out.println("SHeight"+SHeight+"SWidth"+SWidth);
		ImageIcon StockIcon = new ImageIcon("EconomicResearch.png");
		setIconImage(StockIcon.getImage());
		
		this.ListIndex = ListIndex;
		setTitle("StockAnalyse");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(1373,780);
		setLocation((int)(SWidth-1373)/2,(int)(SHeight-780)/2);
		
		contentPane = new JPanel();
		contentPane.setBackground(Color.WHITE);
		contentPane.setForeground(Color.WHITE);
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(null);
		setContentPane(contentPane);
		
		mainPane = new JPanel();
		mainPane.setBackground(Color.WHITE);
		mainPane.setForeground(Color.WHITE);
		mainPane.setBounds(0, 12, 1355,709);
		contentPane.add(mainPane);
		
		lblInstrument = new JLabel("");
		lblInstrument.setBounds(76, 151, 138, 26);
		lblInstrument.setFont(new Font("宋体", Font.PLAIN, 16));
		
		JLabel Prices[] = new JLabel[6];
		Prices[1] = new JLabel("19.6");
		Prices[1].setBounds(0,31, 45, 15);
		Prices[0] = new JLabel("27.5");
		Prices[0].setBounds(0, 51, 45, 15);
		
		progressBar = new JProgressBar();
		progressBar.setStringPainted(true);
		progressBar.setVisible(false);
		progressBar.setBounds(444, 43, 152, 18);
		
		TradeChart = new JScrollPane(tradePanel,
				ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		TradeChart.setBounds(669, 10, 573, 327);
		
		JLabel lblNewLabel = new JLabel("Trade");
		lblNewLabel.setForeground(Color.DARK_GRAY);
		lblNewLabel.setFont(new Font("宋体", Font.PLAIN, 14));
		TradeChart.setColumnHeaderView(lblNewLabel);
		timeScroll = TradeChart.getHorizontalScrollBar();
		
		IndicaterChart = new JScrollPane(indicatorPanel,
				ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		IndicaterChart.setBounds(669, 360, 573, 298);
		IndicaterChart.getHorizontalScrollBar().setModel(TradeChart.getHorizontalScrollBar().getModel());
		
		JLabel lblIndicators = new JLabel("Indicators");
		lblIndicators.setFont(new Font("宋体", Font.PLAIN, 14));
		lblIndicators.setForeground(Color.DARK_GRAY);
		IndicaterChart.setColumnHeaderView(lblIndicators);
		
		JSlider Timeline_x = new JSlider(15,60,30);
		Timeline_x.setBackground(UIManager.getColor("Slider.altTrackColor"));
		Timeline_x.setBorder(new LineBorder(new Color(153, 153, 255)));
		Timeline_x.setBounds(669, 658, 647, 26);
		Timeline_x.addChangeListener(new ChangeListener(){
			public void stateChanged(ChangeEvent e){
				int x = TradeChart.getHorizontalScrollBar().getValue()+TradeChart.getX()/2;
				int tScale = Scale_x;
				Scale_x = ((JSlider)e.getSource()).getValue();
				TradeChart.getHorizontalScrollBar().setValue((int)(x*Scale_x/tScale-TradeChart.getX()/2));
				IndicaterChart.getHorizontalScrollBar().setValue((int)(x*Scale_x/tScale-IndicaterChart.getX()/2));
				refreshChartSize();
			}
		});
		
		JSlider Trade_y = new JSlider(15,60,30);
		Trade_y.setBackground(UIManager.getColor("Slider.altTrackColor"));
		Trade_y.setBorder(new LineBorder(new Color(255, 255, 51)));
		Trade_y.setBounds(1316, 10, 25, 327);
		Trade_y.setOrientation(SwingConstants.VERTICAL);
		Trade_y.addChangeListener(new ChangeListener(){
			public void stateChanged(ChangeEvent e){
				int y = TradeChart.getVerticalScrollBar().getValue()+TradeChart.getY()/2;
				int tScale = tradePanel.Scale_y;
				tradePanel.Scale_y = ((JSlider)e.getSource()).getValue();
				TradeChart.getVerticalScrollBar().setValue((int)(y*tradePanel.Scale_y/tScale-TradeChart.getY()/2));
				tradePanel.refreshSize();
				TradeChart.getVerticalScrollBar().revalidate();
			}
		});
		
		JSlider Indicator_y = new JSlider(15,60,30);
		Indicator_y.setBackground(UIManager.getColor("Slider.altTrackColor"));
		Indicator_y.setBorder(new LineBorder(new Color(255, 255, 51)));
		Indicator_y.setBounds(1316, 360, 25, 298);
		Indicator_y.setOrientation(SwingConstants.VERTICAL);
		Indicator_y.addChangeListener(new ChangeListener(){
			public void stateChanged(ChangeEvent e){
				int y =IndicaterChart.getVerticalScrollBar().getValue()+IndicaterChart.getY()/2;
				int tScale = indicatorPanel.Scale_d;
				indicatorPanel.Scale_d = ((JSlider)e.getSource()).getValue();
				IndicaterChart.getVerticalScrollBar().setValue((int)(y*indicatorPanel.Scale_d/tScale-IndicaterChart.getY()/2));
				indicatorPanel.refreshSize();
				IndicaterChart.revalidate();			
				}
		});
		
		JButton Search = new JButton("");
		Search.setBackground(Color.WHITE);
		Search.setBounds(159, 34, 29, 26);
		Search.setIcon(new ImageIcon("lowpixelSearchIcon.png"));
		
		InstrumentSearch = new JTextField();
		InstrumentSearch.setBounds(20, 34, 138, 27);
		InstrumentSearch.setColumns(10);
		
		JLabel lblLoadInstrument = new JLabel("Load Instrument");
		lblLoadInstrument.setBounds(233, 34, 181, 27);
		lblLoadInstrument.setForeground(new Color(0, 0, 0));
		lblLoadInstrument.setFont(new Font("宋体", Font.BOLD, 20));
		
		JLabel InstrumentInformation = new JLabel("Info:");
		InstrumentInformation.setBounds(20, 71, 138, 26);
		InstrumentInformation.setFont(new Font("宋体", Font.BOLD, 15));
		
		JLabel InstrumentSymbol = new JLabel("Symbol");
		InstrumentSymbol.setBounds(50, 111, 138, 26);
		InstrumentSymbol.setFont(new Font("宋体", Font.PLAIN, 16));
		
		JLabel InstrumentName = new JLabel("Name");
		InstrumentName.setBounds(50, 151, 138, 26);
		InstrumentName.setFont(new Font("宋体", Font.PLAIN, 16));
		
		JLabel InstrumentCurrency = new JLabel("Currency");
		InstrumentCurrency.setBounds(50, 191, 138, 26);
		InstrumentCurrency.setFont(new Font("宋体", Font.PLAIN, 16));
		
		JLabel CurrentPrice = new JLabel("Price");
		CurrentPrice.setBounds(50, 231, 138, 26);
		CurrentPrice.setFont(new Font("宋体", Font.PLAIN, 16));
		
		JLabel Analyse = new JLabel("Analyse Settings:");
		Analyse.setBounds(20, 333, 181, 26);
		Analyse.setFont(new Font("宋体", Font.BOLD, 15));
		
		PeriodUnit = new JComboBox();
		PeriodUnit.setFont(new Font("Arial", Font.PLAIN, 15));
		PeriodUnit.setModel(new DefaultComboBoxModel(new String[] {"Averaging Period", "XAveraging Period", "DAveraging Period", "Averaging Depth"}));
		PeriodUnit.setBounds(159, 382, 239, 26);
		PeriodUnit.addItemListener(new ItemListener(){

			@Override
			public void itemStateChanged(ItemEvent arg0) {
				PDAI_AI ai = PDAI_CentralConsole.AI.get(ListIndex);
				switch(PeriodUnit.getSelectedIndex()){
					case 0:
						PeriodInput.setText(String.valueOf((double)Math.round(ai.AveragePeriod*100)/100));
						break;
					case 1:
						PeriodInput.setText(String.valueOf((double)Math.round(ai.XAveragePeriod*100)/100));
						break;
					case 2:
						PeriodInput.setText(String.valueOf((double)Math.round(ai.DifAveragePeriod*100)/100));
						break;
				}
			}
			
		});
		
		PeriodInput = new JTextField();
		PeriodInput.setBounds(284, 424, 114, 26);
		PeriodInput.setColumns(10);
		PeriodInput.addKeyListener(new KeyListener(){
			@Override
			public void keyPressed(KeyEvent arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void keyReleased(KeyEvent e) {
				PDAI_AI ai = PDAI_CentralConsole.AI.get(ListIndex);
				if(PeriodInput.isFocusOwner()){
					double value;
					try{
						value = Double.valueOf(PeriodInput.getText());
					}catch(NumberFormatException f){
						value=0;
					}
					switch(PeriodUnit.getSelectedIndex()){
					case 0:
						ai.AveragePeriod=value;
						break;
					case 1:
						ai.XAveragePeriod=value;
						break;
					case 2:
						ai.DifAveragePeriod=value;
						break;
					}
				}
			}

			@Override
			public void keyTyped(KeyEvent e) {
				// TODO Auto-generated method stub
				
			}
			
		});
		
		JSlider PeriodSlider = new JSlider();
		PeriodSlider.setBackground(Color.WHITE);
		PeriodSlider.setBounds(35, 423, 239, 27);
		
		JLabel AnalysePeriod = new JLabel("Target:");
		AnalysePeriod.setBounds(30, 381, 86, 26);
		AnalysePeriod.setFont(new Font("宋体", Font.PLAIN, 16));
		
		JLabel AnalyseAccuracy = new JLabel("Accuracy:");
		AnalyseAccuracy.setBounds(30, 462, 138, 26);
		AnalyseAccuracy.setFont(new Font("宋体", Font.PLAIN, 16));
		
		JSlider AccuracySlider = new JSlider();
		AccuracySlider.setBackground(Color.WHITE);
		AccuracySlider.setForeground(Color.WHITE);
		AccuracySlider.setBounds(35, 498, 239, 27);
		
		AccuracyInput = new JTextField();
		AccuracyInput.setBounds(284, 499, 114, 26);
		AccuracyInput.setColumns(10);
		
		JLabel AccuracyUnit = new JLabel("Inputs Per ");
		AccuracyUnit.setBounds(410, 498, 169, 26);
		AccuracyUnit.setFont(new Font("Dialog", Font.PLAIN, 15));
		
		JSeparator separator = new JSeparator();
		separator.setBounds(90, 321, 489, 2);
		
		JButton AnalyseGenerator = new JButton("Generate Analyse");
		AnalyseGenerator.setBounds(50, 581, 145, 25);
		AnalyseGenerator.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				EventQueue.invokeLater(new Runnable(){
					public void run(){
						PDAI_CentralConsole.GetAnalysis(ListIndex);
					}
				});
			}
			
		});
		
		JCheckBox chckbxDynamic = new JCheckBox("Dynamic");
		chckbxDynamic.setBounds(45, 531, 86, 23);
		chckbxDynamic.setBackground(Color.WHITE);
		
		JPanel TradeTag = new JPanel();
		TradeTag.setBorder(new LineBorder(new Color(255, 255, 51)));
		TradeTag.setBackground(UIManager.getColor("Slider.altTrackColor"));
		TradeTag.setLayout(null);
		TradeTag.setBounds(1243, 10, 73, 327);
		TradeTag.add(Prices[1]);
		TradeTag.add(Prices[0]);
		
		JPanel IndicatorTag = new JPanel();
		IndicatorTag.setBorder(new LineBorder(new Color(255, 255, 51)));
		IndicatorTag.setBackground(UIManager.getColor("Slider.altTrackColor"));
		IndicatorTag.setBounds(1243, 360, 73, 298);
		
		JPanel TimelineTag = new JPanel();
		TimelineTag.setBorder(new LineBorder(new Color(255, 204, 0)));
		TimelineTag.setBackground(UIManager.getColor("Slider.altTrackColor"));
		TimelineTag.setBounds(669, 337, 573, 23);
		
		JPanel datePanel = new JPanel();
		datePanel.setBorder(new LineBorder(new Color(255, 255, 51)));
		datePanel.setBackground(UIManager.getColor("Slider.altTrackColor"));
		datePanel.setBounds(1243, 336, 73, 24);
		
		JButton btnRefreshTr = new JButton("");
		btnRefreshTr.setBackground(SystemColor.inactiveCaption);
		btnRefreshTr.setIcon(new ImageIcon(PDAI_GUI.class.getResource("/com/sun/javafx/scene/web/skin/Redo_16x16_JFX.png")));
		btnRefreshTr.setBounds(643, 10, 25, 326);
		
		JButton btnRefreshIn = new JButton("");
		btnRefreshIn.setIcon(new ImageIcon(PDAI_GUI.class.getResource("/com/sun/javafx/scene/web/skin/Redo_16x16_JFX.png")));
		btnRefreshIn.setBackground(SystemColor.inactiveCaption);
		btnRefreshIn.setBounds(643, 361, 25, 323);
		
		JButton btnLastDate = new JButton("");
		btnLastDate.setIcon(new ImageIcon(PDAI_GUI.class.getResource("/javax/swing/plaf/metal/icons/ocean/collapsed.gif")));
		btnLastDate.setBackground(UIManager.getColor("Slider.altTrackColor"));
		btnLastDate.setBounds(643, 337, 25, 23);
		
		JButton btnNextDate = new JButton("");
		btnNextDate.setIcon(new ImageIcon(PDAI_GUI.class.getResource("/javax/swing/plaf/metal/icons/ocean/collapsed-rtl.gif")));
		btnNextDate.setBackground(UIManager.getColor("Slider.altTrackColor"));
		btnNextDate.setBounds(1316, 337, 25, 23);

		JButton tradeSimulation = new JButton("Trade Simulation");
		tradeSimulation.addActionListener(new ActionListener() {
			boolean remove;
			PDAI_SimLog log;
			public void actionPerformed(ActionEvent arg0) {
				if(remove){
					PDAI_CentralConsole.AI.get(ListIndex).buyX.clear();
					PDAI_CentralConsole.AI.get(ListIndex).sellX.clear();
					tradeSimulation.setText("Trade Simulation");
					log.dispose();
					remove=false;
				}else{
					log = new PDAI_SimLog(ListIndex);
					log.setVisible(true);
					tradeSimulation.setText("Remove log");
					remove=true;
				}
			}
		});
		tradeSimulation.setBounds(284, 581, 145, 25);
		
		
		mainPane.setLayout(null);
		mainPane.add(InstrumentSearch);
		mainPane.add(Search);
		mainPane.add(lblLoadInstrument);
		mainPane.add(InstrumentInformation);
		mainPane.add(InstrumentSymbol);
		mainPane.add(CurrentPrice);
		mainPane.add(separator);
		mainPane.add(Analyse);
		mainPane.add(InstrumentCurrency);
		mainPane.add(InstrumentName);
		mainPane.add(TradeChart);
		mainPane.add(Trade_y);
		mainPane.add(AnalysePeriod);
		mainPane.add(PeriodUnit);
		mainPane.add(PeriodSlider);
		mainPane.add(PeriodInput);
		mainPane.add(AnalyseAccuracy);
		mainPane.add(AccuracySlider);
		mainPane.add(AccuracyInput);
		mainPane.add(chckbxDynamic);
		mainPane.add(AnalyseGenerator);
		mainPane.add(AccuracyUnit);
		mainPane.add(IndicaterChart);
		mainPane.add(Indicator_y);
		mainPane.add(Timeline_x);
		mainPane.add(TradeTag);
		mainPane.add(IndicatorTag);
		mainPane.add(TimelineTag);
		mainPane.add(datePanel);
		mainPane.add(btnRefreshTr);
		mainPane.add(btnRefreshIn);
		mainPane.add(btnLastDate);
		mainPane.add(btnNextDate);
		mainPane.add(tradeSimulation);
		mainPane.add(progressBar);
		JLabel lblRelativeUnits = new JLabel("Relative Units");
		lblRelativeUnits.setFont(new Font("Dialog", Font.PLAIN, 15));
		lblRelativeUnits.setBounds(408, 423, 114, 27);
		mainPane.add(lblRelativeUnits);
		
		sizer = new JSizer(mainPane,InstrumentSearch,Search,lblLoadInstrument,InstrumentInformation,InstrumentSymbol
				,CurrentPrice,separator,Analyse,InstrumentCurrency,InstrumentName,TradeChart
				,Trade_y,AnalysePeriod,PeriodUnit,PeriodSlider,PeriodInput,AnalyseAccuracy
				,AccuracySlider,AccuracyInput,chckbxDynamic,AnalyseGenerator,AccuracyUnit
				,IndicaterChart,Indicator_y,Timeline_x,TradeTag,IndicatorTag,TimelineTag,datePanel
				,btnRefreshTr, btnRefreshIn,btnLastDate,btnNextDate,tradeSimulation,progressBar,lblRelativeUnits);
		
		
		
		sizer.addListener(this);
		
	}
	
	/**
	 * @deprecated First function ran on the trade panel
	 * @param x
	 * @return f(x)
	 */
	public double f(double x){
		double y = Math.sin(x)+Math.sin(x/2)+3*Math.cos(x/3);
		return y;
	}
	
	public double getBiggest(List<Double> a){
		double b = Integer.MIN_VALUE;
		for(int i = 0; i<a.size(); i++){
			if(a.get(i) > b) b = a.get(i);
		}
		return b;
	}
	public double getSmallest(List<Double> a){
		double b = Double.MAX_VALUE;
		for(int i = 0; i<a.size(); i++){
			if(a.get(i) < b) b = a.get(i);
		}
		return b;
	}
	
	void addLoading(){
		progressBar.setVisible(true);
	}
	
	void removeLoading(){
		progressBar.setVisible(false);
		repaint();
	}
}

/*
 * © Copyright 2016
 * Cannot be used without authorization
 */