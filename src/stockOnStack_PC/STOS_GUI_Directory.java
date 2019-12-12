package stockOnStack_PC;

import java.awt.BorderLayout;
import java.awt.Desktop;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.EmptyBorder;

import javax.swing.JFileChooser;


import java.io.*;

public class STOS_GUI_Directory{

	static File setting = new File(".settings/com.cloudnest.stos.prefs");
	static StringBuilder content = new StringBuilder();
	private static JPanel contentPane;

	static boolean saveChange = false;
	/**
	 * Create the frame.
	 */
	public static void setDirectory() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
				| UnsupportedLookAndFeelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		};
		saveChange = true;
		contentPane = new JPanel();
		String line=getCurrentDataBase();
		
		contentPane.setLayout(new BorderLayout(0, 0));
		
		JFileChooser chooser = new JFileChooser();
	    chooser.setCurrentDirectory(new File(line));
	    chooser.setDialogTitle("Data Base Location");
	    chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
	    contentPane.add(chooser);
	    int returnVal = chooser.showOpenDialog(contentPane);
	    if(returnVal==JFileChooser.APPROVE_OPTION){
	    	createPath(chooser.getSelectedFile().getAbsolutePath());
	    }
	    saveChange = false;
	    content = new StringBuilder();
	}
	
	static String getCurrentDataBase(){
		String line = "";
		try {
			FileReader in = new FileReader(setting);
			BufferedReader reader = new BufferedReader(in);
			String pass;
			while((pass=reader.readLine())!=null){
				if(pass.indexOf("DataBaseLocation")!=-1){
					line=pass;
				}else{
					content.append(pass);
					content.append("\n");
				}
			};
			reader.close();
			in.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(!saveChange) content = new StringBuilder();
		return replace(line.split("=")[1],"/","\\");
	}
	
	private static void createPath(String newpath){
		try {
			FileWriter out = new FileWriter(setting);
			PrintWriter printer = new PrintWriter(out);
			printer.print(content);
			printer.print("DataBaseLocation="+newpath);
			printer.close();
			out.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	private static String replace(String input, String replaced, String replacer){
		StringBuilder output = new StringBuilder();
		String[] segments = input.split(replaced);
		for(int i = 0; i<segments.length-1; i++){
			output.append(segments[i]);
			output.append(replacer);
		}
		output.append(segments[segments.length-1]);
		return output.toString();
	}
}
