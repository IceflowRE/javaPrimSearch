/*
* Author: Iceflower S
* Extern libraries:
* 	org.magicwerk.brownies.collections
* 		- Thomas Mauch
* 		- Apache License 2.0
* 		- http://www.magicwerk.org/page-collections-overview.html
* IDE: Eclipse Mars (Java)
* License: Copyright (C) 2015 Iceflower S
* 	GPL: http://www.gnu.org/licenses/gpl.html
*/

import java.io.File;
import java.sql.Date;
import java.text.SimpleDateFormat;

import javax.swing.JOptionPane;

import org.magicwerk.brownies.collections.BigList;

import Exceptions.WriteFileException;

public class Main {
//storage
	protected static final File dirFile = new File("data"); //path to data
	protected static final File primStorage = new File("data/primes.txt"); //path includes data
	protected static BigList<Long> primList = new BigList<Long>();
	protected static long correctPrim;
	protected static long biggestPrim;
	protected static int status;
	protected static int lastWriteIndex;
	protected static final int maxCalcThreads = 8;
	protected static boolean showProgressWhileCalc;
//storage - end

	private static void checkInit() { //false configuration check
		if (maxCalcThreads < 1) {
			log("ERROR: Illegal settings (you have to use at least one thread)");
			log("Closed");
			System.exit(0);
		}
	}
	
	public static void main(String[] args) {
		checkInit();
		//init vars/ objects
		showProgressWhileCalc = true; //needs more performance
		
		correctPrim = 0;
		biggestPrim = 0;
		status = -2;
		lastWriteIndex = 0;
		
		//file check
		FileManager.fileCheck(); // methode
		
		//prim check
		FileManager.checkPrimes(); // methode
		
		log("Correct primes:  " + correctPrim);
		log("Biggest prime:  " + biggestPrim);
		
		long upTo = calcHowMany();
		try {
			PrimeCalc.searchPrimes(upTo); // methode
		} catch (Exception e) {
			Main.JOptionDialog("Error", e.getMessage(), 0, new Object[] {"Ok"});
			//updateStats(); // methode
			//FileManager.writeFiles(); // methode
			//System.exit(0);
		}
		
		updateStats(); // methode
		
		try {
			FileManager.writeFiles(); // methode
		} catch (WriteFileException e) {
			Main.JOptionDialog("Error", e.getMessage(), 0, new Object[] {"Ok & Exit"});
			Main.log("CLOSED");
			System.exit(0);
		}
		
		log("-------------------");
		log("Correct primes:  " + correctPrim);
		log("Biggest prime:  " + biggestPrim);
		log("CLOSED");
	}
	
	private static long calcHowMany() {
		long upTo = 0;
		boolean inputIsCorrect = false;
		while (!inputIsCorrect) {
			try {
				String input = JOptionPane.showInputDialog("Search from " + biggestPrim + " up to X more numbers (bigger as 2)");
				if (input == null) {
					Main.log("User canceled");
					System.out.println("CLOSED");
					System.exit(0);
				}
				if ( (upTo = Long.parseLong(input)) >= 2) {
					upTo = (biggestPrim + upTo);
					inputIsCorrect = true;
				}
			} catch (NumberFormatException e) {
				System.out.println("Wrong input (or value to big)");
			}
		}
		return upTo;
	}
	
	private static final void updateStats() {
		correctPrim = Main.primList.size();
		biggestPrim = Main.primList.getLast();
		log("DONE: update statistic");
	}

	protected static final void log(String text) {
		System.out.println(text);
	}
	
	protected static final void logAdd(String text) {
		System.out.print(text);
	}
	
	protected static final String timerFormat(long nanosec) {
		return new SimpleDateFormat("mm:ss.SSS").format(new Date((long) (nanosec * 1E-6)));
	}
	protected static final int JOptionDialog(String title, String hint, int message, Object[] options) {
		return JOptionPane.showOptionDialog(null, hint, title, JOptionPane.DEFAULT_OPTION, message, null, options, options[0]);
	}
		
	protected static synchronized long getPrim(int get) { //DEV, not in use
		return primList.get(get);
	}
	
	protected static synchronized void addPrim(long number) { //DEV, not in use
		primList.add(number);
	}
}
