/*
* Author: Iceflower S
* IDE: Eclipse Mars (Java)
* License: Copyright (C) 2015 Iceflower S
* 	GPL: http://www.gnu.org/licenses/gpl.html
*/

import java.sql.Date;
import java.text.SimpleDateFormat;

import javax.swing.JOptionPane;

public class javaPrimesSearch {
	public static void main(String[] args) {
		//init vars/ objects
		Primes primObj = new Primes("data", "primes.txt");
		long time1;
		boolean done_temp;

		//check files
		done_temp = false;
		System.out.println("START: file check");
		time1 = System.nanoTime();
		if (primObj.existsDir()) { //filecheck
			System.out.println("DONE: file check  " + timerFormat(System.nanoTime() - time1));
		} else { //show message with option to create them
			System.out.println("FAILED: file check  " + timerFormat(System.nanoTime() - time1));
			int choosen_temp = JOptionDialog("Missing files!", "Try to create them?", 2, new Object[] {"Yes", "No & Exit"});
			if(choosen_temp == -1 || choosen_temp == 1) {
				System.out.println("CLOSED");
				System.exit(0);
			} else {
				done_temp = false;
				System.out.println("START: creating files");
				time1 = System.nanoTime();
				try {
					done_temp = primObj.recreateDir();
				} catch (Exception e) {
					System.out.println("ERROR: creating files  " + timerFormat(System.nanoTime() - time1));
					e.printStackTrace();
					JOptionDialog("Error", "An error occurred, the files arent created", 0, new Object[] {"Ok & Exit"});
					System.out.println("CLOSED");
					System.exit(0);
				}
				if (done_temp == true ) { //create file
					System.out.println("DONE: creating files  " + timerFormat(System.nanoTime() - time1));
					JOptionDialog("", "Succesfull created", 1, new Object[] {"Ok"});
				} else {
					System.out.println("ERROR: creating files  " + timerFormat(System.nanoTime() - time1));
					JOptionDialog("Error", "An undetected problem occurred, the files arent created", 0, new Object[] {"Ok & Exit"});
					System.out.println("CLOSED");
					System.exit(0);
				}
			} //end creating
		} //end error handling

		//check and load primes
		done_temp = false;
		System.out.println("START: fast primes check/ loading");
		time1 = System.nanoTime();
		try {
			done_temp = primObj.fastCheckPrimStoreBigList();
		} catch (Exception e) {
			System.out.println("ERROR: fast primes check/ loading  " + timerFormat(System.nanoTime() - time1));
			e.printStackTrace();
			JOptionDialog("Error", "An error occurred", 0, new Object[] {"Ok & Exit"});
			System.out.println("CLOSED");
			System.exit(0);
		}
		if (done_temp == true ) {
			System.out.println("DONE: fast primes check/ loading  " + timerFormat(System.nanoTime() - time1));
		} else { //error handling
			System.out.println("FAILED: fast primes check/ loading (line: " + primObj.getCorrectPrim() + ")  " + timerFormat(System.nanoTime() - time1));
			switch (primObj.getStatus()) {
			case (-2): { //recreate file
				int choosen_temp = JOptionDialog("Mistake in the primes file", "Try to recreate it? (you will lose all found primes)", 2, new Object[] {"Yes", "No & Exit"});
				if (choosen_temp == -1 || choosen_temp == 1) { //close
					System.out.println("CLOSED");
					System.exit(0);
				} else { //try to repair
					done_temp = false;
					System.out.println("START: recreating primes file");
					time1 = System.nanoTime();
					try {
						done_temp = primObj.recreateDir();
					} catch (Exception e) {
						System.out.println("ERROR: recreating primes file  " + timerFormat(System.nanoTime() - time1));
						e.printStackTrace();
						JOptionDialog("Error", "An error occurred, the files arent recreated", 0, new Object[] {"Ok & Exit"});
						System.out.println("CLOSED");
						System.exit(0);
					}
					if (done_temp == true ) { //create file
						System.out.println("DONE: recreating primes file  " + timerFormat(System.nanoTime() - time1));
						JOptionDialog("", "Succesfull recreated", 1, new Object[] {"Ok"});
					} else {
						System.out.println("ERROR: recreating primes file  " + timerFormat(System.nanoTime() - time1));
						JOptionDialog("Error", "An undetected problem occurred, the primes file isnt recreated", 0, new Object[] {"Ok & Exit"});
						System.out.println("CLOSED");
						System.exit(0);
					}
				} //end creating
			} case (-1): { //repair file
				int choosen_temp = JOptionDialog("Mistake in the primes file", "List should be repaired (you will lose some found primes)", 2, new Object[] {"Yes", "No & Exit"});
				if(choosen_temp == -1 || choosen_temp == 1) { //close
					System.out.println("CLOSED");
					System.exit(0);
				} else { //try to repair
					done_temp = false;
					System.out.println("START: repairing primes file");
					time1 = System.nanoTime();
					try {
						done_temp = primObj.repairPrimListWithBigList(true); //option to show the progress; true = show, false = hide
					} catch (Exception e) {
						System.out.println("ERROR: repairing primes file  " + timerFormat(System.nanoTime() - time1));
						e.printStackTrace();
						JOptionDialog("Error", "An error occurred, the primes file isnt repaired", 0, new Object[] {"Ok & Exit"});
						System.out.println("CLOSED");
						System.exit(0);
					}
					if (done_temp == true ) { //create file
						System.out.println("DONE: repairing primes file  " + timerFormat(System.nanoTime() - time1));
						JOptionDialog("", "Succesfull repaired", 1, new Object[] {"Ok"});
					} else {
						System.out.println("ERROR: recreating files  " + timerFormat(System.nanoTime() - time1));
						JOptionDialog("Error", "An undetected problem occurred, the primes file isnt repaired", 0, new Object[] {"Ok & Exit"});
						System.out.println("CLOSED");
						System.exit(0);
					}
				} //end repairing
			}}
		}

		System.out.println("Correct primes:  " + primObj.getCorrectPrim());
		System.out.println("Biggest prime:  " + primObj.getBiggestPrim());

		//ask how many numbers should be tested
		long upTo = 0;
		boolean inputIsCorrect = false;
		while (!inputIsCorrect) {
			try {
				String input = JOptionPane.showInputDialog("Search from " + primObj.getBiggestPrim() + " up to X more numbers (bigger as 2)");
				if (input == null) {
					System.out.println("CLOSED");
					System.exit(0);
				}
				if ( (upTo = Long.parseLong(input)) >= 2) {
					upTo = (primObj.getBiggestPrim() + upTo);
					inputIsCorrect = true;	
				}
			} catch (NumberFormatException e) {
				System.out.println("Wrong input (or value to big)");
			}
		}

		//search prims
		System.out.println("START: test primes (" + primObj.getBiggestPrim() + " - " + upTo + ")");
		time1 = System.nanoTime();
		try {
			for (long i = primObj.getBiggestPrim() + 2; i <= upTo; i += 2) {
				if (primObj.isPrimWithBigList(i) == true) {
					primObj.addPrim(i);
				}
			}
			System.out.println("DONE: test primes  " + timerFormat(System.nanoTime() - time1));
		} catch (Throwable e) {
			System.out.println("ERROR: test primes  " + timerFormat(System.nanoTime() - time1));
			e.printStackTrace();
			JOptionDialog("Error", "An undetected problem occurred, test primes isnt finished\ntry to save the found primes", 0, new Object[] {"Ok"});
			//save the primes with the methods below
		}		

		//updating stats
		if (primObj.updateStats() == true) {
			System.out.println("DONE: update stats");
		}

		//writing primes into the file
		System.out.println("START: write primes into file");
		time1 = System.nanoTime();
		try {
			if ( primObj.writePrimStorage(true) == true) {
				System.out.println("DONE: write primes into file  " + timerFormat(System.nanoTime() - time1));
			}
		} catch (Exception e) {
			System.out.println("ERROR: write primes into file  " + timerFormat(System.nanoTime() - time1));
			e.printStackTrace();
			JOptionDialog("Error", "An undetected problem occurred, writing primes into file isnt finished", 0, new Object[] {"Ok & Exit"});
			System.out.println("CLOSED");
			System.exit(0);
		}

		System.out.println("-------------------\nCorrect primes:  " + primObj.getCorrectPrim());
		System.out.println("Biggest prime:  " + primObj.getBiggestPrim());
		System.out.println("CLOSED");
	} //end main
	
	//functions
	public static String timerFormat(long nanosec) {
		return new SimpleDateFormat("mm:ss.SSS").format(new Date((long) (nanosec * 1E-6)));
	}
	public static int JOptionDialog(String title, String hint, int message, Object[] options) {
		return JOptionPane.showOptionDialog(null, hint, title, JOptionPane.DEFAULT_OPTION, message, null, options, options[0]);
	}

}