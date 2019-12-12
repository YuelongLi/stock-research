package stockOnStack_PC;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.*;
import javax.swing.border.*;

import codeLibrary.Console;
import codeLibrary.JSizer;
import codeLibrary.JWarning;

/**
 * This class functions as a initial entry for wished stocks
 * @author Yuelong
 */
public class STOS_GUI_Search extends JFrame {
	
	/**
	 * default serial version id
	 */
	private static final long serialVersionUID = 1L;
	
	private JPanel contentPane;
	private JTextField textField;
	private JLabel txtStockOnStack;
	private JLabel label;
	private JLabel label_1;

	/**
	 * Launch the application.
	 */

	/**
	 * Create the frame.
	 *  <div>Launcher icon made by <a href="http://www.flaticon.com/authors/madebyoliver" title="Madebyoliver">Madebyoliver</a> from <a href="http://www.flaticon.com" title="Flaticon">www.flaticon.com</a> is licensed by <a href="http://creativecommons.org/licenses/by/3.0/" title="Creative Commons BY 3.0" target="_blank">CC 3.0 BY</a></div>
	 */
	public STOS_GUI_Search() {
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setResizable(false);
		setIconImage(new ImageIcon("StockonStack.png").getImage());
		setSize(567, 381);
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		setLocation((screenSize.width-567)/2,(screenSize.height-381)/2);
		contentPane = new JPanel();
		contentPane.setBackground(new Color(204, 102, 51));
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JPanel panel = new JPanel();
		panel.setBorder(new LineBorder(Color.BLUE, 1, true));
		panel.setBounds(0, 0, 561, 346);
		contentPane.add(panel);
		panel.setLayout(null);
		
		textField = new JTextField();
		textField.setHorizontalAlignment(SwingConstants.CENTER);
		textField.setFont(new Font("Arial Unicode MS", Font.PLAIN, 25));
		textField.setBounds(81, 144, 330, 48);
		panel.add(textField);
		textField.setColumns(10);
		//Action listener for the button
				ActionListener search = new ActionListener(){
					@Override
					public void actionPerformed(ActionEvent e) {
						if(!STOS_Console.search(textField.getText())){
							new JWarning("The requested stock can not be found!");
						}
						textField.setText("");
					}
				};
		textField.addActionListener(search);
		
		JButton button = new JButton("");
		button.setForeground(Color.WHITE);
		button.setBackground(Color.WHITE);
		button.setIcon(new ImageIcon("lowpixelSearchIcon.png"));
		button.setBounds(425, 144, 50, 48);
		button.addActionListener(search);
		panel.add(button);
		
		
		txtStockOnStack = new JLabel();
		txtStockOnStack.setForeground(SystemColor.inactiveCaptionBorder);
		txtStockOnStack.setFont(new Font("Miriam", Font.PLAIN, 36));
		txtStockOnStack.setHorizontalAlignment(SwingConstants.CENTER);
		txtStockOnStack.setText("Stock on Stack\r\n");
		txtStockOnStack.setBackground(SystemColor.menu);
		txtStockOnStack.setBounds(81, 58, 394, 48);
		panel.add(txtStockOnStack);
		
		label = new JLabel();
		label.setBounds(354, 298, 169, 20);
		panel.add(label);
		label.setText("Designed and built by Yuelong\r\n");
		label.setHorizontalAlignment(SwingConstants.CENTER);
		label.setForeground(Color.WHITE);
		label.setFont(new Font("Miriam", Font.PLAIN, 12));
		label.setBackground(Color.WHITE);
		label_1 = new JLabel("");
		label_1.setBounds(0, 0, 561, 346);
		panel.add(label_1);
		label_1.setIcon(new ImageIcon("waterfall.jpg"));
	}
}
/*
 * © Copyright 2016
 * Cannot be used without authorization
 */