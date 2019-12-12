package pdai;
/**
* @author 越隆
* @version 1.1
*/
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.Color;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.JButton;
import java.awt.Font;
import javax.swing.border.EtchedBorder;

import codeLibrary.Conversion;

import javax.swing.JCheckBox;
import javax.swing.JScrollPane;

public class PDAI_SimLog extends JFrame {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTextField textField;
	private JLabel lblInitialFund;
	private JButton btnSimulate;
	private JCheckBox chckbxLog;
	private JTextArea textArea;
	private Thread logThread;
	PDAI_AI targetAI;
	PDAI_SimLog self = this;

	void println(String info){
		textArea.append(info);
		textArea.append("\n");
	}
	
	void print(String info){
		textArea.append(info);
	}

	/**
	 * Create the frame.
	 */
	PDAI_SimLog(int listIndex) {
		targetAI = PDAI_CentralConsole.AI.get(listIndex);
		
		logThread = new Thread(){
			public void run(){
				targetAI.simulateTrade(Double.valueOf(textField.getText()), Conversion.toStringList(PDAI_CentralConsole.targetStocks.get(listIndex), " ")[0], self);
			}
		};
		
		setTitle("Simulation Log");
		setResizable(false);
		setBounds(100, 100, 694, 772);
		contentPane = new JPanel();
		contentPane.setBackground(new Color(176, 196, 222));
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JPanel panel = new JPanel();
		panel.setBorder(new EtchedBorder(EtchedBorder.LOWERED, new Color(128, 128, 128), null));
		panel.setBackground(SystemColor.window);
		panel.setBounds(5, 5, 678, 37);
		contentPane.add(panel);
		panel.setLayout(null);
		
		lblInitialFund = new JLabel("Initial Fund");
		lblInitialFund.setBounds(14, 9, 69, 18);
		lblInitialFund.setFont(new Font("Arial", Font.PLAIN, 15));
		panel.add(lblInitialFund);
		
		textField = new JTextField();
		textField.setBounds(97, 6, 86, 24);
		panel.add(textField);
		textField.setColumns(10);
		
		btnSimulate = new JButton("Simulate");
		btnSimulate.setBounds(560, 5, 104, 27);
		btnSimulate.setFont(new Font("Arial", Font.PLAIN, 15));
		panel.add(btnSimulate);
		btnSimulate.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				targetAI.logging = chckbxLog.isSelected();
				logThread.start();
			}
		});
		
		chckbxLog = new JCheckBox("Log");
		chckbxLog.setBackground(new Color(255, 255, 255));
		chckbxLog.setBounds(193, 5, 69, 27);
		panel.add(chckbxLog);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(5, 42, 678, 688);
		contentPane.add(scrollPane);
		
		textArea = new JTextArea();
		textArea.setFont(new Font("Cambria Math", Font.PLAIN, 18));
		scrollPane.setViewportView(textArea);
	}
}

/*
 * © Copyright 2016
 * Cannot be used without authorization
 */