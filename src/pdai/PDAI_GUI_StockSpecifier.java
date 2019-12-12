package pdai;

import java.awt.Color;
import java.awt.EventQueue;
import java.util.Date;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerDateModel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import java.awt.Font;

public class PDAI_GUI_StockSpecifier extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					PDAI_GUI_StockSpecifier frame = new PDAI_GUI_StockSpecifier();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public PDAI_GUI_StockSpecifier() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 962, 581);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JPanel mainPanel = new JPanel();
		mainPanel.setBounds(0, 0, 944, 534);
		mainPanel.setBackground(new Color(250,250,250));
		contentPane.add(mainPanel);
		mainPanel.setLayout(null);
		
		JLabel lblStockName = new JLabel("Stock Name");
		lblStockName.setFont(new Font("Arial", Font.PLAIN, 17));
		lblStockName.setBounds(54, 39, 99, 28);
	}

}
