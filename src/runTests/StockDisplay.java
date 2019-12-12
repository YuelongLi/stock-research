package runTests;
import java.util.*;
import java.util.concurrent.TimeUnit;

import javax.swing.JFrame;
import javax.swing.JLabel;

public class StockDisplay {
	public static void main(String[] args) throws Exception{
		JFrame frame = new JFrame("Hello Swing");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		JLabel label = new JLabel("a label");
		frame.add(label);
		System.out.println(JFrame.EXIT_ON_CLOSE);
		frame.setSize(300,100);
		frame.setVisible(true);
		TimeUnit.SECONDS.sleep((long) 1);
		label.setText("hey this is different!");
	}
}
