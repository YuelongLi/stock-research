package runTests;

import java.io.IOException;
import java.util.*;
import codeLibrary.*;

public class FormatTransform {
	/**
	 * trade type xml generator
	 */
	static JFile a;
	static ArrayList<String> typeShelf = new ArrayList<String>();

	public static void main(String[] args) throws IOException {
		a = new JFile("trade types.txt", "	%*=\"@<>", "	%=\"@<>");
		Console.println("creating xml");
		XML b = new XML("tradeTypes", "root");
		Console.println("parsing data");
		a.currentIndex = 0;
		a.currentLine = 0;
		while (true) {
			Console.println("line" + a.currentLine);
			a.readTill("<");
			Console.println("< found");
			Console.println("current index: " + a.currentIndex);
			Console.println("current line: " + a.currentLine);
			XML.XObject category = b.newXObject(a.readWord());
			Console.println("New Object created: " + category);
			a.readTill("@");
			Console.println("@ found");
			Console.println("current index: " + a.currentIndex);
			Console.println("current line: " + a.currentLine);
			String attribute;
			if ((attribute = a.read()).equals("ContentFormat")) {
				a.readTill("\"");
				Console.println("\" found: ContentFormat");
				Console.println("current index: " + a.currentIndex);
				Console.println("current line: " + a.currentLine);
				for (int i = 0;; i++) {
					String pass = a.read();
					typeShelf.add(Conversion.filter(pass, " "));
					Console.println("type added: " + typeShelf.get(i));
					if (a.read().equals("\""))
						break;
				}
				Console.println("type finished");
				Console.println("current index: " + a.currentIndex);
				Console.println("current line: " + a.currentLine);
			} else {
				a.readTill("\"");
				Console.println("\" found:");
				category.putAttribute(attribute, a.read());
				a.readTill("\"");
			}
			a.readTill(">");
			Console.println("reading information");
			a.readTill("\\n");
			while (true) {
				Console.println("current index: " + a.currentIndex);
				Console.println("current line: " + a.currentLine);
				Console.println("line breaker found, creating new instrument");
				XML.XObject instrument = null;
				for (int i = 0;; i++) {
					String pass = a.read();
					Console.println("iteration: " + i);
					if (i == 0) {
						pass=Conversion.replace(pass, "/", "-");
						pass=Conversion.replace(pass, "$", "M-");
						pass=Conversion.filter(pass, " ");
						instrument = b.newXObject(pass);
						Console.println("instrument created: " + instrument);
					} else {
						String type = typeShelf.get(i);
						if (type.equals("MARGIN")) {
							double spread = Double.valueOf(pass);
							Console.println("spread: " + spread);
							if (a.read().equals("%")) {
								spread /= 100;
								Console.println("spread: " + spread);
							} else
								a.currentIndex--;
							instrument.putAttribute(type, spread);
							Console.println(instrument.attributes);
						} else if (type.equals("MARKETHOURS")) {
							String[] Interval = Conversion.toStringList(pass, " to");
							Console.println("pass");
							Console.println(Interval);
							if (Interval.length == 2) {
								instrument.putAttribute("startTime", Interval[0]);
								instrument.putAttribute("endTime", Interval[1]);
							}
							if (Interval.length == 4) {
								String[] start = Conversion.toStringList(Interval[0],':');
								String[] end = Conversion.toStringList(Interval[2],':');
								int startHour = Integer.valueOf(start[0]);
								int endHour = Integer.valueOf(start[0]);
								startHour += Interval[1].equals("pm") ? 12 : 0;
								endHour += Interval[3].equals("pm") ? 12 : 0;
								instrument.putAttribute("startTime", startHour+":"+start[1]);
								instrument.putAttribute("endTime", endHour+":"+end[1]);
							}
							Console.println(instrument.attributes);
						} else {
							instrument.putAttribute(type, pass);
							Console.println(instrument.attributes);
						}
					}
					Console.println(pass);
					if (a.read().equals("\\n")) {
						Console.println("instrument difinition finished: " + instrument);
						category.add(instrument);
						break;
					}
				}
				if (a.read().equals("<")) {
					Console.println("all stock initialized");
					break;
				}
				a.currentIndex--;
			}
			if (Conversion.toStringList(a.read(), "/")[0].equals(category.name))
				Console.println("category finished, name matched");
			a.readTill(">");
			Console.println("current index: " + a.currentIndex);
			Console.println("current line: " + a.currentLine);
			b.root.add(category);
			Console.println("_________________");
			Console.println();
			a.readTill("\\n");
			Console.println("current index: " + a.currentIndex);
			Console.println("current line: " + a.currentLine);
			typeShelf.clear();
			if(a.read()!=null){
				a.currentLine--;
			}else{
				break;
			}
			Console.println("starting new category");
		}
		Console.println("out of loop");
		b.parseOut();
		Console.println("parsed out");
	}
	/*
	 * a = new File("trade types.txt", "	%*=\"@<>", "	%=\"@<>");
		Console.println("creating xml");
		XML b = new XML("tradeTypes", "root");
		Console.println("parsing data");
		a.currentIndex = 0;
		a.currentLine = 0;
		while (true) {
			Console.println("line" + a.currentLine);
			a.readTill("<");
			Console.println("< found");
			Console.println("current index: " + a.currentIndex);
			Console.println("current line: " + a.currentLine);
			XML.XObject category = b.newXObject(a.readWord());
			Console.println("New Object created: " + category);
			a.readTill("@");
			Console.println("@ found");
			Console.println("current index: " + a.currentIndex);
			Console.println("current line: " + a.currentLine);
			String attribute;
			if ((attribute = a.read()).equals("ContentFormat")) {
				a.readTill("\"");
				Console.println("\" found: ContentFormat");
				Console.println("current index: " + a.currentIndex);
				Console.println("current line: " + a.currentLine);
				for (int i = 0;; i++) {
					String pass = a.read();
					typeShelf.add(Conversion.filter(pass, " "));
					Console.println("type added: " + typeShelf.get(i));
					if (a.read().equals("\""))
						break;
				}
				Console.println("type finished");
				Console.println("current index: " + a.currentIndex);
				Console.println("current line: " + a.currentLine);
			} else {
				a.readTill("\"");
				Console.println("\" found:");
				category.putAttribute(attribute, a.read());
				a.readTill("\"");
			}
			a.readTill(">");
			Console.println("reading information");
			a.readTill("\\n");
			while (true) {
				Console.println("current index: " + a.currentIndex);
				Console.println("current line: " + a.currentLine);
				Console.println("line breaker found, creating new instrument");
				XML.XObject instrument = null;
				for (int i = 0;; i++) {
					String pass = a.read();
					Console.println("iteration: " + i);
					if (i == 0) {
						pass=Conversion.replace(pass, "/", "-");
						pass=Conversion.replace(pass, "$", "M-");
						pass=Conversion.filter(pass, " ");
						instrument = b.newXObject(pass);
						Console.println("instrument created: " + instrument);
					} else {
						String type = typeShelf.get(i);
						if (type.equals("MARGIN")) {
							double spread = Double.valueOf(pass);
							Console.println("spread: " + spread);
							if (a.read().equals("%")) {
								spread /= 100;
							} else
								a.currentIndex--;
							instrument.putAttribute(type, spread);
							Console.println(instrument.attributes);
						} else if (type.equals("MARKETHOURS")) {
							String[] Interval = Conversion.toStringList(pass, " to");
							Console.println("pass");
							Console.println(Interval);
							if (Interval.length == 2) {
								instrument.putAttribute("startTime", Interval[0]);
								instrument.putAttribute("endTime", Interval[1]);
							}
							if (Interval.length == 4) {
								String[] start = Conversion.toStringList(Interval[0],':');
								String[] end = Conversion.toStringList(Interval[2],':');
								int startHour = Integer.valueOf(start[0]);
								int endHour = Integer.valueOf(start[0]);
								startHour += Interval[1].equals("pm") ? 12 : 0;
								endHour += Interval[3].equals("pm") ? 12 : 0;
								instrument.putAttribute("startTime", startHour+":"+start[1]);
								instrument.putAttribute("endTime", endHour+":"+end[1]);
							}
							Console.println(instrument.attributes);
						} else {
							instrument.putAttribute(type, pass);
							Console.println(instrument.attributes);
						}
					}
					Console.println(pass);
					if (a.read().equals("\\n")) {
						Console.println("instrument difinition finished: " + instrument);
						category.add(instrument);
						break;
					}
				}
				if (a.read().equals("<")) {
					Console.println("all stock initialized");
					break;
				}
				a.currentIndex--;
			}
			if (Conversion.toStringList(a.read(), "/")[0].equals(category.name))
				Console.println("category finished, name matched");
			a.readTill(">");
			Console.println("current index: " + a.currentIndex);
			Console.println("current line: " + a.currentLine);
			b.root.add(category);
			Console.println("_________________");
			Console.println();
			a.readTill("\\n");
			Console.println("current index: " + a.currentIndex);
			Console.println("current line: " + a.currentLine);
			typeShelf.clear();
			if(a.read()!=null){
				a.currentLine--;
			}else{
				break;
			}
			Console.println("starting new category");
		}
		Console.println("out of loop");
		b.parseOut();
		Console.println("parsed out");
	 */
}
