/*
* Author: Iceflower S
*/

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import javax.swing.JOptionPane;

public class Prim {
	public static void main(String[] args) throws IOException {
		String dir = "data";
		String file = "primes.txt";
		File primStorage = new File(dir + "/" + file);

		if (primStorage.exists()) { //filecheck
			System.out.println("DONE: filescheck");
		} else { //show message with option to create them
			System.out.println("FAILED: filescheck");
			{ //option dialog
				int choosen_temp = JOptionDialog("Missing files!", "Try to create them?", 2, new Object[] {"Yes", "No & Exit"});
				if(choosen_temp == -1 || choosen_temp == 1) {
					System.exit(0);
				} else {
					if ( createDir(dir, file) ) { //create file
						System.out.println("DONE: files created");
						JOptionDialog("", "Succesfull created", 1, new Object[] {"Ok"});
					} else {
						System.out.println("FAILED: creating files");
						JOptionDialog("Error", "A problem occurred (check write access and execute files)", 0, new Object[] {"Ok & Exit"});
						System.exit(0);
					}
				}
			}
		} //end error handling
		long[] stats = fastCheckPrimList(dir + "/" + file); //fastlistcheck
		if (stats[2] < 0) { //work with the fastcheck result
			System.out.println("FAILED: fast primeslist check (error line: " + (stats[0] + 1) + ")");
			if ( repairPrimList(stats, dir, file) == false) {
				System.out.println("FAILED: primelist recreating/ repairing");
				System.exit(0);
			}
			System.out.println("DONE: primelist recreating/ repairing");
			stats = new long[] {2, 3, 1};
		} else {
			System.out.println("DONE: fast primeslist check");
		}
		long upTo = 0;
		boolean inputIsCorrect = false;
		while (!inputIsCorrect) {
			try {
				String input = JOptionPane.showInputDialog("Search from " + stats[1] + " up to X more numbers (bigger as 2)");
				if (input == null) {
					System.exit(0);
				}  
				upTo = Long.parseLong(input);
				if (upTo >= 2) {
					upTo = (stats[1] + upTo);
					inputIsCorrect = true;	
				}
			} catch (NumberFormatException e) {
				System.out.println("Wrong input (or value to big)");
			}
		}
		long biggestPrim = stats[1];
		long curBiggestPrim = biggestPrim;
		long sqrtValue;
		if (biggestPrim == 2) { //prevent testing number 4
			biggestPrim = 3;
		} else { //prevent testing the same Prim again
			biggestPrim = biggestPrim + 2;
		}
		try {
			BufferedWriter bWriter = new BufferedWriter(new FileWriter(primStorage, true));
			for (long curTest = biggestPrim; curTest <= upTo; curTest += 2) { //biggestPrim + 2, because you dont want to test the biggest prim again
				if ( ((sqrtValue = (long) Math.sqrt(curTest)) + 1) > curBiggestPrim) { //flush only of you need more prims in your pimStorage
					bWriter.flush();
					curBiggestPrim = biggestPrim;
					System.out.println("flush at: " + curBiggestPrim);
				}
				//System.out.println(curTest + "--" + sqrtValue);
				if (isPrimWithList(curTest, sqrtValue, primStorage) == true) {
					bWriter.write(curTest + "\n");
					biggestPrim = curTest;
				}
			}
			bWriter.close();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(0);
		}
		System.out.println("DONE: calculating primes until " + upTo);
	}

	public static boolean createDir(String dir, String file) throws IOException {
		File dirFile = new File(dir);
		File fileFile = new File(dir + "/" + file);
		if (!dirFile.exists()) { 
			if (!dirFile.mkdir()) {
				return false;
			}
		}
		try {
			fileFile.delete();
			fileFile.createNewFile();
			//add first prim 2
			FileWriter fw = new FileWriter(fileFile);
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write("2" + "\n" + "3" + "\n");
			bw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return true;
	}
	public static long[] fastCheckPrimList(String relPath) { //long: 0 = correct prim; 1 = biggest prim; 2 = status (-2 = recreate all, -1 = error file need to be cutted, 1 = all ok)
		long correctPrim = 0, biggestPrim = 0;
		try {
			BufferedReader bRead = new BufferedReader(new FileReader(new File(relPath)));
			String curLine = bRead.readLine();
			long curPrim;
			try {
				if ( (curLine == null) || ((curPrim = Long.parseLong(curLine)) != 2)) {//first check
					bRead.close();
					return new long[] {correctPrim, biggestPrim, -2};
				}
				curLine = bRead.readLine();
				if ( (curLine == null) || ((curPrim = Long.parseLong(curLine)) != 3)) {//second check
					bRead.close();
					return new long[] {correctPrim, biggestPrim, -2};
				} 
			} catch (Exception e) {
				return new long[] {correctPrim, biggestPrim, -2};
			}
			biggestPrim = 3;
			for (correctPrim = 2; (curLine = bRead.readLine()) != null; correctPrim++) { //check if everything is ok
				curPrim = Long.parseLong(curLine);
				if (curPrim > biggestPrim && (curPrim % 2 == 1)) { //main check
					biggestPrim = curPrim;
				} else { //if one next prim is smaller or equal as an previous its an error
					bRead.close();
					return new long[] {correctPrim, biggestPrim, -1};
				}
			}
			bRead.close();
		} catch (Exception e) {
			return new long[] {correctPrim, biggestPrim, -1};
		}
		return new long[] {correctPrim, biggestPrim, 1};
	}
	private static boolean repairPrimList(long[] stats, String dir, String file) {
		int choosen_temp;
		String statusStr = stats[2] + "";
		File primStorage = new File(dir + "/" + file);
		switch (statusStr) {
		case "-2": {
				choosen_temp = JOptionDialog("Error in the prime list", "List will be recreated (you will lose all primes)", 2, new Object[] {"Yes", "No & Exit"});
				if(choosen_temp == -1 || choosen_temp == 1) { //close
					return false;
				} else { //try to repair
					try {
						createDir(dir, file);
					} catch (Exception e) {
						e.printStackTrace();
						choosen_temp = JOptionDialog("Error while recreating", "Try to deleted (.../" + (dir + "/" + file) +")", 2, new Object[] {"Ok & Exit"});
						return false;
					}
				}//end error handling
				JOptionDialog("", "Succesfull recreated", 1, new Object[] {"Ok"});
				return true; 
		} case "-1": {
				choosen_temp = JOptionDialog("Prime list arent correct", "Try to repair it? (you will lose maybe some primes)", 2, new Object[] {"Yes", "No & Exit"});
				if (choosen_temp == -1 || choosen_temp == 1) { //close
					return false;
				} else { //try to repair
					File copyTemp =  new File (dir + "/" + (file.substring(0, file.length() - 4)) + "copy.tmp");
					try {
						BufferedReader bRead = new BufferedReader(new FileReader(primStorage));
						BufferedWriter bWrite = new BufferedWriter(new FileWriter(copyTemp));
						int progress = 0;
						double corPrimDou = (100. / stats[0]);
						for (long i = 1; i <= stats[0]; i++) {
							String curLine = bRead.readLine();
							bWrite.write(curLine + "\n");
							int progtemp = (int) (corPrimDou * i);
							if (progtemp != progress) {
								System.out.println("List repairing: " + progtemp + "%");
								progress = progtemp;
							}
						}
						bWrite.close();
						bRead.close();
						primStorage.delete();
						copyTemp.renameTo(primStorage);
						copyTemp.delete();
						stats = new long[] {stats[0], stats[1], 1};
					} catch (Exception e) {
						choosen_temp = JOptionDialog("Error while reparing", "Try to deleted (.../" + (dir + "/" + file) +")", 2, new Object[] {"Ok & Exit"});
						e.printStackTrace();
						return false;
					}
					JOptionDialog("", "Succesfull repaired", 1, new Object[] {"Ok"});
					return true;
				}
			}
		}
		return false;
	}
	public static int JOptionDialog(String title, String hint, int message, Object[] options) {
		return JOptionPane.showOptionDialog(null, hint, title, JOptionPane.DEFAULT_OPTION, message, null, options, options[0]);
	}
	public static boolean isPrimWithList(long curTest, long sqrtValue, File primStorage) { //check if the input value is a prim
		if (primStorage.canRead()) {
			try {
				BufferedReader bRead = new BufferedReader(new FileReader(primStorage));
				long curPrim = Long.parseLong(bRead.readLine());
				while (curPrim <= sqrtValue) {
					//System.out.println(curTest + " % " + curPrim + " = " + (curTest % curPrim) + " (" + sqrtNumber + ")");
					if (curTest % curPrim == 0) {
						bRead.close();
						return false;
					}
					curPrim = Long.parseLong(bRead.readLine());
				}
				bRead.close();
			} catch (Exception e) {
				e.printStackTrace();
				System.exit(0);
			}
		} else {
			System.exit(0);
		}
		return true;
	}
}