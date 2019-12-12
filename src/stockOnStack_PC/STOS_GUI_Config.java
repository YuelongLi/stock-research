package stockOnStack_PC;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.event.*;
import java.io.IOException;
import java.util.*;
import codeLibrary.*;

/**
 * Configuration UI for Stock on Stack
 * @version 1.0
 * @author Yuelong Li
 */
public class STOS_GUI_Config extends JFrame {
	/**
	 * serialVersionIdentifier default value
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * queue identifier
	 */
	final int index;
	
	HashMap<String,String> configuration = new HashMap<String,String>();
	String realFrequencyChoice = "per Hour";
	int realRepeatTime = 10;
	double realGap = 360.0;
	int unit = 3600;
	boolean useOriginalConfig = false;
	
	private JPanel contentPane;
	private JTextField realFrequency;
	private JTextField realAvgGap;
	private JTextField startYear;
	private JTextField startMonth;
	private JTextField startDate;
	private JTextField endYear;
	private JTextField endMonth;
	private JTextField endDate;
	private JSlider frequencySlider;
	private JTextField preferedName;
	private JComboBox<String> realtimeFrequencyUnit;
	private JCheckBox chckBox_includeHist;
	private JComboBox<String> histFrequencyUnit;
	private JCheckBox chckbxPVolume;
	private JCheckBox chckbxPHighLow;
	private JCheckBox chckbxIncludStats;
	private JCheckBox chckbxHighLow;
	private JCheckBox chckbxVolume;
	private JCheckBox chckbxAskbidSize;
	private JCheckBox chckbxAvgVolume;
	private JCheckBox chckbxAskbid;
	private JCheckBox chckbxRefreshYear;
	
	JTextArea stockInfo;
	JSizer preference_1;
	JSizer preference_2;
	JSizer stockConfig;

	/**
	 * Create the frame.
	 */
	public STOS_GUI_Config(int index){
		this.index = index;
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setIconImage(new ImageIcon("StockonStack.png").getImage());
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		setBounds((screenSize.width-1069)/2, (screenSize.height-753)/2, 1069, 753);
		contentPane = new JPanel();
		contentPane.setBackground(new Color(255, 255, 255));
		contentPane.setBorder(new LineBorder(SystemColor.activeCaptionBorder, 1, true));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JLabel lblStockInformation = new JLabel("Stock Information");
		lblStockInformation.setFont(new Font("Arial", Font.BOLD, 20));
		lblStockInformation.setHorizontalAlignment(SwingConstants.CENTER);
		lblStockInformation.setBounds(66, 95, 498, 35);
		contentPane.add(lblStockInformation);
		
		JButton btnConfirm = new JButton("Confirm");
		btnConfirm.setBounds(679, 628, 138, 42);
		contentPane.add(btnConfirm);
		btnConfirm.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0){
				confirm();
			}
		});
		
		JButton btnCancel = new JButton("Cancel");
		btnCancel.setBounds(856, 628, 138, 42);
		contentPane.add(btnCancel);
		btnCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				cancel();
			}
		});
		
		JPanel panel = new JPanel();
		panel.setBackground(new Color(0, 102, 204));
		panel.setBounds(0, 0, 1051, 68);
		contentPane.add(panel);
		panel.setLayout(new BorderLayout(0, 0));
		
		JLabel lblPleaseConfigureYour = new JLabel("Please configure your selection ");
		lblPleaseConfigureYour.setForeground(SystemColor.inactiveCaptionBorder);
		lblPleaseConfigureYour.setFont(new Font("Arial", Font.PLAIN, 25));
		lblPleaseConfigureYour.setHorizontalAlignment(SwingConstants.CENTER);
		panel.add(lblPleaseConfigureYour);
		
		JLabel lblFrequency = new JLabel("Frequency:");
		lblFrequency.setHorizontalAlignment(SwingConstants.CENTER);
		lblFrequency.setBounds(65, 443, 91, 35);
		contentPane.add(lblFrequency);
		
		realFrequency = new JTextField();
		realFrequency.setText("10");
		realFrequency.setHorizontalAlignment(SwingConstants.TRAILING);
		realFrequency.setBounds(169, 445, 86, 30);
		contentPane.add(realFrequency);
		realFrequency.setColumns(10);
		realFrequency.addKeyListener(new KeyListener(){
			@Override
			public void keyPressed(KeyEvent arg0) {
				// 
				
			}

			@Override
			public void keyReleased(KeyEvent e) {
				if(realFrequency.isFocusOwner()){
					String content;
					content = realFrequency.getText();
					try{
						int pass = Integer.valueOf(content);
						if(pass>60){
							pass = 60;
							realFrequency.setText(String.valueOf(pass));
						}
						if(pass<1){
							pass = 1;
							realFrequency.setText(String.valueOf(pass));
						}
						frequencySlider.setValue(pass);
						realRepeatTime = pass;
						updateFrequencyAmount();
					}catch(NumberFormatException a){
					}
				}
			}

			@Override
			public void keyTyped(KeyEvent e) {
				
			}
		});

		frequencySlider = new JSlider();
		frequencySlider.setBackground(SystemColor.textHighlightText);
		frequencySlider.setBounds(85, 491, 266, 26);
		frequencySlider.setMinimum(1);
		frequencySlider.setMaximum(60);
		frequencySlider.setValue(10);
		contentPane.add(frequencySlider);
		frequencySlider.addChangeListener(new ChangeListener(){

			@Override
			public void stateChanged(ChangeEvent arg0) {
				if(frequencySlider.isFocusOwner()){
					realRepeatTime = frequencySlider.getValue();
					realFrequency.setText(String.valueOf(realRepeatTime));
					updateFrequencyAmount();
				}
			}
			
		});
		JLabel label = new JLabel("1");
		label.setHorizontalAlignment(SwingConstants.CENTER);
		label.setBounds(66, 491, 15, 26);
		contentPane.add(label);
		
		JLabel label_1 = new JLabel("60");
		label_1.setHorizontalAlignment(SwingConstants.CENTER);
		label_1.setBounds(350, 491, 36, 26);
		contentPane.add(label_1);
		
		JLabel lblAverageGap = new JLabel("Average gap:\r\n");
		lblAverageGap.setBounds(66, 530, 108, 35);
		contentPane.add(lblAverageGap);
		
		realAvgGap = new JTextField();
		realAvgGap.setHorizontalAlignment(SwingConstants.TRAILING);
		realAvgGap.setColumns(10);
		realAvgGap.setBounds(169, 532, 138, 30);
		realAvgGap.setText("360.0");
		contentPane.add(realAvgGap);
		realAvgGap.addKeyListener(new KeyListener(){

			@Override
			public void keyPressed(KeyEvent arg0) {
				
			}

			@Override
			public void keyReleased(KeyEvent arg0) {
				try{
					if(realAvgGap.isFocusOwner()){
						String text = realAvgGap.getText();
						realGap = Double.valueOf(text);
					}
					updateFrequencyGap();
				}catch(NumberFormatException e){
					
				}
				
			}

			@Override
			public void keyTyped(KeyEvent arg0) {
			}
			});
		
		JLabel lblSeconds = new JLabel("seconds");
		lblSeconds.setBounds(321, 532, 82, 30);
		contentPane.add(lblSeconds);
		
		realtimeFrequencyUnit = new JComboBox<String>();
		realtimeFrequencyUnit.setModel(new DefaultComboBoxModel<String>(new String[] {"per Minute", "per Hour", "per Day"}));
		realtimeFrequencyUnit.setSelectedIndex(1);
		realtimeFrequencyUnit.setBounds(269, 444, 134, 32);
		realtimeFrequencyUnit.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				realFrequencyChoice = realtimeFrequencyUnit.getSelectedItem().toString();
				updateFrequencyUnit();
			}
		
		});
		contentPane.add(realtimeFrequencyUnit);
		realtimeFrequencyUnit.addComponentListener(new ComponentListener(){

			@Override
			public void componentHidden(ComponentEvent arg0) {
				
			}

			@Override
			public void componentMoved(ComponentEvent e) {
				realtimeFrequencyUnit.revalidate();
				
			}

			@Override
			public void componentResized(ComponentEvent e) {
				realtimeFrequencyUnit.revalidate();
				
			}

			@Override
			public void componentShown(ComponentEvent e) {
				// 
				
			}
			
			
		});

		
		JPanel preferences_2 = new JPanel();
		preferences_2.setForeground(new Color(0, 0, 0));
		preferences_2.setBorder(new EtchedBorder(EtchedBorder.RAISED, SystemColor.controlHighlight, SystemColor.controlShadow));
		preferences_2.setBackground(SystemColor.text);
		preferences_2.setBounds(578, 283, 418, 318);
		contentPane.add(preferences_2);
		preferences_2.setLayout(null);
		
		chckBox_includeHist = new JCheckBox("Include historical quote");
		chckBox_includeHist.setBackground(Color.WHITE);
		chckBox_includeHist.setBounds(10, 9, 272, 27);
		preferences_2.add(chckBox_includeHist);
		
		JLabel label_2 = new JLabel("Frequency:");
		label_2.setBounds(10, 74, 91, 35);
		preferences_2.add(label_2);
		
		histFrequencyUnit = new JComboBox<String>();
		histFrequencyUnit.setModel(new DefaultComboBoxModel<String>(new String[] {"Daily", "Weekily", "Monthly"}));
		histFrequencyUnit.setBounds(119, 75, 138, 32);
		preferences_2.add(histFrequencyUnit);
		histFrequencyUnit.addComponentListener(new ComponentListener(){

			@Override
			public void componentHidden(ComponentEvent arg0) {
				// 
				
			}

			@Override
			public void componentMoved(ComponentEvent e) {
				realtimeFrequencyUnit.revalidate();
				
			}

			@Override
			public void componentResized(ComponentEvent e) {
				histFrequencyUnit.revalidate();
				
			}

			@Override
			public void componentShown(ComponentEvent e) {
				// 
				
			}
			
			
		});
		
		
		JLabel lblFrom = new JLabel(" \r\nFrom:");
		lblFrom.setHorizontalAlignment(SwingConstants.TRAILING);
		lblFrom.setBounds(41, 122, 60, 35);
		preferences_2.add(lblFrom);
		
		startYear = new JTextField();
		startYear.setHorizontalAlignment(SwingConstants.CENTER);
		startYear.setToolTipText("");
		startYear.setBounds(119, 127, 86, 24);
		preferences_2.add(startYear);
		startYear.setColumns(10);
		
		startMonth = new JTextField();
		startMonth.setHorizontalAlignment(SwingConstants.CENTER);
		startMonth.setColumns(10);
		startMonth.setBounds(223, 127, 38, 24);
		preferences_2.add(startMonth);
		
		startDate = new JTextField();
		startDate.setHorizontalAlignment(SwingConstants.CENTER);
		startDate.setColumns(10);
		startDate.setBounds(275, 127, 38, 24);
		preferences_2.add(startDate);
		
		endYear = new JTextField();
		endYear.setHorizontalAlignment(SwingConstants.CENTER);
		endYear.setColumns(10);
		endYear.setBounds(119, 175, 86, 24);
		preferences_2.add(endYear);
		
		endMonth = new JTextField();
		endMonth.setHorizontalAlignment(SwingConstants.CENTER);
		endMonth.setColumns(10);
		endMonth.setBounds(223, 175, 38, 24);
		preferences_2.add(endMonth);
		
		endDate = new JTextField();
		endDate.setHorizontalAlignment(SwingConstants.CENTER);
		endDate.setColumns(10);
		endDate.setBounds(275, 175, 38, 24);
		preferences_2.add(endDate);
		
		JLabel lblTo = new JLabel("To:");
		lblTo.setHorizontalAlignment(SwingConstants.TRAILING);
		lblTo.setBounds(41, 170, 60, 35);
		preferences_2.add(lblTo);
		
		JLabel lblFormat = new JLabel("Format:");
		lblFormat.setForeground(SystemColor.textInactiveText);
		lblFormat.setHorizontalAlignment(SwingConstants.TRAILING);
		lblFormat.setBounds(41, 207, 60, 35);
		preferences_2.add(lblFormat);
		
		JLabel lblYyyy = new JLabel("YYYY");
		lblYyyy.setForeground(SystemColor.textInactiveText);
		lblYyyy.setHorizontalAlignment(SwingConstants.CENTER);
		lblYyyy.setBounds(119, 212, 86, 24);
		preferences_2.add(lblYyyy);
		
		JLabel lblMm = new JLabel("MM");
		lblMm.setForeground(SystemColor.textInactiveText);
		lblMm.setHorizontalAlignment(SwingConstants.CENTER);
		lblMm.setBounds(219, 212, 38, 24);
		preferences_2.add(lblMm);
		
		JLabel lblDd = new JLabel("DD");
		lblDd.setForeground(SystemColor.textInactiveText);
		lblDd.setHorizontalAlignment(SwingConstants.CENTER);
		lblDd.setBounds(275, 212, 38, 24);
		preferences_2.add(lblDd);
		
		chckbxPVolume = new JCheckBox("Volume");
		chckbxPVolume.setBackground(Color.WHITE);
		chckbxPVolume.setBounds(10, 251, 133, 27);
		preferences_2.add(chckbxPVolume);
		
		chckbxPHighLow = new JCheckBox("High & Low");
		chckbxPHighLow.setBackground(Color.WHITE);
		chckbxPHighLow.setBounds(10, 280, 133, 27);
		preferences_2.add(chckbxPHighLow);
		
		chckbxIncludStats = new JCheckBox("Include Stats");
		chckbxIncludStats.setBackground(Color.WHITE);
		chckbxIncludStats.setBounds(67, 606, 133, 27);
		contentPane.add(chckbxIncludStats);
		
		chckbxHighLow = new JCheckBox("High & Low");
		chckbxHighLow.setSelected(true);
		chckbxHighLow.setBackground(Color.WHITE);
		chckbxHighLow.setBounds(67, 574, 133, 27);
		contentPane.add(chckbxHighLow);
		
		chckbxVolume = new JCheckBox("Volume");
		chckbxVolume.setBackground(Color.WHITE);
		chckbxVolume.setBounds(218, 606, 133, 27);
		contentPane.add(chckbxVolume);
		
		chckbxAskbidSize = new JCheckBox("Ask/Bid Size");
		chckbxAskbidSize.setSelected(true);
		chckbxAskbidSize.setBackground(Color.WHITE);
		chckbxAskbidSize.setBounds(350, 574, 133, 27);
		contentPane.add(chckbxAskbidSize);
		
		chckbxAvgVolume = new JCheckBox("Avg Volume");
		chckbxAvgVolume.setBackground(Color.WHITE);
		chckbxAvgVolume.setBounds(350, 606, 133, 27);
		contentPane.add(chckbxAvgVolume);
		
		chckbxAskbid = new JCheckBox("Ask/Bid");
		chckbxAskbid.setSelected(true);
		chckbxAskbid.setBackground(Color.WHITE);
		chckbxAskbid.setBounds(218, 574, 133, 27);
		contentPane.add(chckbxAskbid);
		
		JPanel preferences_1 = new JPanel();
		preferences_1.setBorder(new EtchedBorder(EtchedBorder.RAISED, SystemColor.controlHighlight, SystemColor.controlShadow));
		preferences_1.setBackground(Color.WHITE);
		preferences_1.setBounds(578, 143, 418, 142);
		contentPane.add(preferences_1);
		preferences_1.setLayout(null);
		
		JLabel lblPreferedName = new JLabel("Prefered Name:");
		lblPreferedName.setBounds(14, 25, 117, 31);
		preferences_1.add(lblPreferedName);
		
		preferedName = new JTextField();
		preferedName.setColumns(10);
		preferedName.setBounds(129, 28, 182, 24);
		preferences_1.add(preferedName);
		preferedName.setText(STOS_Console.stocks.get(index).getSymbol().toUpperCase());
		
		chckbxRefreshYear = new JCheckBox("Refresh Year High/Low");
		chckbxRefreshYear.setSelected(true);
		chckbxRefreshYear.setBackground(Color.WHITE);
		chckbxRefreshYear.setBounds(205, 97, 204, 27);
		preferences_1.add(chckbxRefreshYear);
		
		JLabel lblSpecification = new JLabel("Preferences");
		lblSpecification.setHorizontalAlignment(SwingConstants.CENTER);
		lblSpecification.setFont(new Font("Arial", Font.BOLD, 20));
		lblSpecification.setBounds(578, 95, 416, 35);
		contentPane.add(lblSpecification);
		
		JButton btnUseOldConfig = new JButton("Use original configuration");
		btnUseOldConfig.setFont(new Font("Arial", Font.PLAIN, 16));
		btnUseOldConfig.setBounds(14, 64, 233, 24);
		preferences_1.add(btnUseOldConfig);
		btnUseOldConfig.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				useOriginalConfig=true;
				confirm();
			}
		});

		JScrollPane infoPane = new JScrollPane();
		infoPane.setBounds(66, 143, 498, 287);
		contentPane.add(infoPane);
		
		stockInfo = new JTextArea();
		infoPane.setViewportView(stockInfo);
		stockInfo.setEditable(false);
		stockInfo.setFont(new Font("Sitka Display", Font.PLAIN, 18));
		
		
		this.preference_1 = new JSizer(preferences_1);
		
		JButton btnOpenXml = new JButton("Open database");
		btnOpenXml.setFont(new Font("Arial", Font.PLAIN, 16));
		btnOpenXml.setBounds(261, 64, 143, 27);
		preferences_1.add(btnOpenXml);
		btnOpenXml.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				Calendar currentDate = Calendar.getInstance();
				try {
					Desktop.getDesktop().open(new java.io.File(STOS_GUI_Directory.getCurrentDataBase()+"/"+preferedName.getText()));
				} catch (Exception e) {
					new JWarning("NO original collection found!");
					e.printStackTrace();
				}
			}
			
		});
		
		JButton location = new JButton("");
		location.setIcon(new ImageIcon(STOS_GUI_Config.class.getResource("/com/sun/java/swing/plaf/windows/icons/Directory.gif")));
		location.setBounds(578, 628, 46, 42);
		contentPane.add(location);
		location.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				EventQueue.invokeLater(new Runnable() {
					public void run() {
						try {
							STOS_GUI_Directory.setDirectory();
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});
			}
		});
		
		preference_1.addAllComponents();
		preference_1.addListener(0);
		this.preference_2 = new JSizer(preferences_2);
		
		preference_2.addAllComponents();
		preference_2.addListener(0);
		stockConfig = new JSizer(contentPane.getComponents());
		stockConfig.addListener(this);
	}
	protected void updateFrequencyUnit(){
		switch(realFrequencyChoice){
		case "per Minute":
			unit = 60;
			break;
		case "per Hour":
			unit = 3600;
			break;
		case "per Day":
			unit = 3600*24;
			break;
		default: break;
		}
		realGap = (double)Math.round(unit/(double)realRepeatTime*10)/10;
		realAvgGap.setText(String.valueOf(realGap));
	}
	protected void updateFrequencyAmount(){
		realGap = (double)Math.round(unit/(double)realRepeatTime*10)/10;
		realAvgGap.setText(String.valueOf(realGap));
	}
	protected void updateFrequencyGap(){
		realRepeatTime = (int)Math.round(unit/realGap);
		realFrequency.setText(String.valueOf(realRepeatTime));
		frequencySlider.setValue(realRepeatTime);
	}
	protected void cancel(){
		dispose();
		STOS_Console.removeConfig(index);
	}
	
	protected void confirm(){
		configuration.put("UseOriginalConfig", String.valueOf(useOriginalConfig));
		configuration.put("PreferedName", preferedName.getText());
		configuration.put("RefreshYearHighLow", String.valueOf(chckbxRefreshYear.isSelected()));
		configuration.put("RealGap", String.valueOf(realGap));
		configuration.put("HighLow", String.valueOf(chckbxHighLow.isSelected()));
		configuration.put("AskBid",String.valueOf(chckbxAskbid.isSelected()));
		configuration.put("IncludeStats", String.valueOf(chckbxIncludStats.isSelected()));
		configuration.put("Volume", String.valueOf(chckbxVolume.isSelected()));
		configuration.put("AskBidSize",String.valueOf(chckbxAskbidSize.isSelected()));
		configuration.put("AvgVolume", String.valueOf(chckbxAvgVolume.isSelected()));
		if(chckBox_includeHist.isSelected()){
			try{
			configuration.put("HistFrequencyUnit", String.valueOf(histFrequencyUnit.getSelectedItem()));
			configuration.put("HistFrom", String.valueOf(getFromDate()));
			configuration.put("HistFrom", String.valueOf(getEndDate()));
			configuration.put("HistVolume", String.valueOf(chckbxPVolume.isSelected()));
			configuration.put("HistHighlow", String.valueOf(chckbxPHighLow.isSelected()));
			} catch(NumberFormatException e){
				new JWarning("Please specify the starting and ending dates for historical quotes");
				return;
			}
		}
		
		STOS_Console.config(configuration,this.index);
		dispose();
	}
	private Date getFromDate() throws NumberFormatException{
		Calendar startingDate = Calendar.getInstance(getLocale());
		startingDate.set(Integer.valueOf(startYear.getText()), Integer.valueOf(startMonth.getText()), Integer.valueOf(startDate.getText()));
		return startingDate.getTime();
	}
	private Date getEndDate() throws NumberFormatException{
		Calendar endingDate = Calendar.getInstance(getLocale());
		endingDate.set(Integer.valueOf(endYear.getText()), Integer.valueOf(endMonth.getText()), Integer.valueOf(endDate.getText()));
		return endingDate.getTime();
	}
}
/*
 * © Copyright 2016
 * Cannot be used without authorization
 */