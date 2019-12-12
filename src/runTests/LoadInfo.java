package runTests;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.BoxLayout;
import javax.swing.JProgressBar;
import java.awt.FlowLayout;
import javax.swing.JLabel;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.Font;

public class LoadInfo extends JFrame {
	
	JProgressBar progressBar = new JProgressBar();
	private JPanel contentPane;
	JLabel label = new JLabel();
	JLabel lblInstrument = new JLabel("Instrument:");
	

	public LoadInfo() {
		setVisible(true);
		setTitle("Loading Analyse");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 445, 131);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JLabel lblLoading = new JLabel("Loading ");
		lblLoading.setBounds(151, 5, 56, 16);
		lblLoading.setFont(new Font("ËÎÌו", Font.PLAIN, 14));
		contentPane.add(lblLoading);
		lblInstrument.setBounds(212, 5, 82, 16);
		lblInstrument.setFont(new Font("ËÎÌו", Font.PLAIN, 14));
		contentPane.add(lblInstrument);
		
		progressBar = new JProgressBar(0,100);
		progressBar.setBounds(62, 32, 304, 27);
		progressBar.setStringPainted(true);
		contentPane.add(progressBar);
	}
}