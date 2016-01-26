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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.LineNumberReader;

import Exceptions.WriteFileException;
import Exceptions.WrongPrimException;

public class FileManager {
//methodes
	static void fileCheck() {
		Main.log("START: file check");
		long time1 = System.nanoTime();

		if (Main.primStorage.exists()) { //filecheck
			Main.log("DONE: file check  " + Main.timerFormat(System.nanoTime() - time1));
		} else { //show message with option to create them
			Main.log("FAILED: file check  " + Main.timerFormat(System.nanoTime() - time1));
			
			int choosen_temp = Main.JOptionDialog("Missing files!", "Try to create them?", 2, new Object[] {"Yes", "No & Exit"});
			if(choosen_temp == -1 || choosen_temp == 1) {
				Main.log("User canceled");
				Main.log("CLOSED");
				System.exit(0);
			} else { //create files				
				try {
					recreateDir();
				} catch (IOException e) {
					Main.JOptionDialog("Error", e.getMessage(), 0, new Object[] {"Ok & Exit"});
					Main.log("CLOSED");
					System.exit(0);
				} catch (Exception e) {
					Main.JOptionDialog("Error", e.getMessage(), 0, new Object[] {"Ok & Exit"});
					Main.log("CLOSED");
					System.exit(0);
				}
				Main.JOptionDialog("", "Succesfull created", 1, new Object[] {"Ok"});
			} //end creating
		} //end error handling
	}
	
	static void checkPrimes() {

		try {
			fastCheckPrimStoreBigList();
		} catch (WrongPrimException e) {
			switch (Main.status) {
			case (-2): { //recreate file
				int choosen_temp = Main.JOptionDialog("Mistake in the primes file", "Try to recreate it? (you will lose all found primes)", 2, new Object[] {"Yes", "No & Exit"});
				if (choosen_temp == -1 || choosen_temp == 1) { //close
					Main.log("User canceled");
					Main.log("CLOSED");
					System.exit(0);
				} else { //try to repair					
					try {
						recreateDir();
					} catch (IOException f) {
						Main.JOptionDialog("Error", e.getMessage(), 0, new Object[] {"Ok & Exit"});
						Main.log("CLOSED");
						System.exit(0);
					} catch (Exception f) {
						Main.JOptionDialog("Error", e.getMessage(), 0, new Object[] {"Ok & Exit"});
						Main.log("CLOSED");
						System.exit(0);
					}
					Main.JOptionDialog("", "Succesfull recreated", 1, new Object[] {"Ok"});
				} //end creating
			} case (-1): { //repair file
				int choosen_temp = Main.JOptionDialog("Mistake in the primes file", "List should be repaired (you will lose some found primes)", 2, new Object[] {"Yes", "No & Exit"});
				if(choosen_temp == -1 || choosen_temp == 1) { //close
					Main.log("User canceled");
					Main.log("CLOSED");
					System.exit(0);
				} else { //try to repair
					try {
						repairPrimListWithBigList(true); //option to show the progress; true = show, false = hide
					} catch (Exception f) {
						Main.JOptionDialog("Error",  e.getMessage(), 0, new Object[] {"Ok & Exit"});
						Main.log("CLOSED");
						System.exit(0);
					}
					Main.JOptionDialog("", "Succesfull repaired", 1, new Object[] {"Ok"});
				}
			}} //end repairing
		} catch (Exception e) {
			Main.JOptionDialog("Error", e.getMessage(), 0, new Object[] {"Ok & Exit"});
			Main.log("CLOSED");
			System.exit(0);
		}
	}
	
///////////////
	static void writeFiles() { //writing primes into the file
		try {
			writePrimStorage(true);
		} catch (Exception e) {
			
			throw new WriteFileException();
		}
	}
	
	//writing
	private final static void writePrimStorage(boolean showProgress) throws Exception {
		Main.log("START: write primes into file");
		long time1 = System.nanoTime();
		
		if (showProgress == true) {
			ConsoleProgressBar cProgress = new ConsoleProgressBar();
			try (BufferedWriter bWriter = new BufferedWriter(new FileWriter(Main.primStorage, Main.primStorage.exists())) ){
				int progress = -1, progTemp = 0;
				int lastWriteIndexBefore = Main.lastWriteIndex;
				double corPrimDou = (100. / (Main.correctPrim - Main.lastWriteIndex));
				for (int i = Main.lastWriteIndex; i < Main.correctPrim; i++) {
					bWriter.write(Main.primList.get(i) + "\n");
					Main.lastWriteIndex = i;
					progTemp = (int) (corPrimDou * (i - lastWriteIndexBefore));
					if (progTemp != progress) {
						//Main.log("List writing: " + progTemp + "%");
						Main.logAdd(cProgress.getProgress(progTemp));
						progress = progTemp;
					}
				}
				Main.log(cProgress.getProgress(100));
			} catch (Exception e) {
				Main.log("ERROR: write primes into file  " + Main.timerFormat(System.nanoTime() - time1));
				Main.log("\t"+ e.getStackTrace());
				throw new Exception("An undetected problem occurred, the primes arent write to file.");
			}
		} else { //showProgress = off
			try (BufferedWriter bWriter = new BufferedWriter(new FileWriter(Main.primStorage, Main.primStorage.exists())) ) {
				for (int i = Main.lastWriteIndex; i <= Main.correctPrim; i++) {
					bWriter.write(Main.primList.get(i) + "\n");
					Main.lastWriteIndex = i;
				}
			} catch (Exception e) {
				Main.log("ERROR: write primes into file  " + Main.timerFormat(System.nanoTime() - time1));
				Main.log("\t"+ e.getStackTrace());
				throw new Exception("An undetected problem occurred, the primes arent write to file.");
			}	
		}
		Main.log("DONE: write primes into file  " + Main.timerFormat(System.nanoTime() - time1));
	}
///////////////
	
	//recreate files
	private final static void recreateDir() throws IOException {
		Main.log("START: creating primes file");
		long time1 = System.nanoTime();
		
		if (!Main.dirFile.exists()) { //if file not exists
			if (!Main.dirFile.mkdir()) { //if creating the file = false
				Main.log("ERROR: recreating primes file  " + Main.timerFormat(System.nanoTime() - time1));
				throw new IOException("An undetected problem occurred, the files arent created");
			}
		}
		try ( BufferedWriter bWriter = new BufferedWriter(new FileWriter(Main.primStorage)) ) {
			Main.primStorage.createNewFile();
			bWriter.write("2" + "\n" + "3" + "\n");
		} catch (Exception e) {
			Main.log("ERROR: recreating primes file  " + Main.timerFormat(System.nanoTime() - time1));
			Main.log("\t" + e.getStackTrace());
			throw new IOException("An undetected problem occurred, the files arent created");
		}
		Main.status = 1;
		Main.log("DONE: creating files  " + Main.timerFormat(System.nanoTime() - time1));
	}

//function
	//checkers
	
	@SuppressWarnings("unused")
	private final void fastCheckPrimList() throws Exception { //long: 0 = correct prim; 1 = biggest prim; 2 = status (-2 = recreate all, -1 = error file need to be cutted, 1 = all ok)
		Main.log("START: fast primes check");
		long time1 = System.nanoTime();
		
		long curPrim;
		String curLine;
		
		try ( LineNumberReader lReader = new LineNumberReader(new BufferedReader(new FileReader(Main.primStorage))) ) {			
			for (int i = 2; i <= 3; i++) { //test if the first is 2 and the second 3
				curLine = lReader.readLine();
				if ( (curLine == null) || ((curPrim = Long.parseLong(curLine)) != i)) {//first check
					throw new WrongPrimException("Primes found which are obviously wrong or file is damaged.");
				}
				Main.primList.add((long) i);
			}
			Main.biggestPrim = 3;
			while ((curLine = lReader.readLine()) != null) { //check if everything is ok
				curPrim = Long.parseLong(curLine);
				if (curPrim > Main.biggestPrim && (curPrim % 2 == 1)) { //main check
					Main.biggestPrim = curPrim;
				} else { //if one prim is smaller or equal as an previous throw an error
					Main.correctPrim = lReader.getLineNumber() - 1;
					Main.status = -1;
					throw new WrongPrimException("Primes found which are obviously wrong or file is damaged.");
				}
			}
			Main.correctPrim = lReader.getLineNumber();
			Main.lastWriteIndex = lReader.getLineNumber();
		} catch (WrongPrimException e) {
			Main.correctPrim = 0;
			Main.biggestPrim = 0;
			Main.status = -2;
			Main.log("FAILED: fast primes check (line: " + Main.correctPrim + ")  " + Main.timerFormat(System.nanoTime() - time1));
			throw e;
		} catch (Exception e) {
			Main.log("ERROR: fast primes check (line: " + Main.correctPrim + ")  " + Main.timerFormat(System.nanoTime() - time1));
			Main.log("\t" + e.getStackTrace());
			throw new Exception("An undetected problem occurred, the primes file isnt checked/ loaded.");
		}
		Main.status = 1;
		Main.log("DONE: fast primes check  " + Main.timerFormat(System.nanoTime() - time1));
	}

	private final static void fastCheckPrimStoreBigList() throws Exception { //long: 0 = correct prim; 1 = biggest prim; 2 = status (-2 = recreate all, -1 = error file need to be cutted, 1 = all ok)
		Main.log("START: fast primes check/ loading");
		long time1 = System.nanoTime();
		
		long curPrim;
		String curLine;
		
		try ( LineNumberReader lReader = new LineNumberReader(new BufferedReader(new FileReader(Main.primStorage))) ) {			
			for (int i = 2; i <= 3; i++) { //test if the first is 2 and the second 3
				curLine = lReader.readLine();
				if ( (curLine == null) || ((curPrim = Long.parseLong(curLine)) != i)) {//first check
					throw new WrongPrimException("Primes found which are obviously wrong or file is damaged.");
				}
				Main.primList.add((long) i);
			}
			Main.biggestPrim = 3;
			while ((curLine = lReader.readLine()) != null) { //check if everything is ok
				curPrim = Long.parseLong(curLine);
				if (curPrim > Main.biggestPrim && (curPrim % 2 == 1)) { //main check
					Main.biggestPrim = curPrim;
				} else { //if one prim is smaller or equal as an previous throw an error
					Main.correctPrim = lReader.getLineNumber() - 1;
					Main.status = -1;
					throw new WrongPrimException("Primes found which are obviously wrong or file is damaged.");
				}
				Main.primList.add(curPrim);
			}
			Main.correctPrim = lReader.getLineNumber();
			Main.lastWriteIndex = lReader.getLineNumber();
		} catch (WrongPrimException e) {
			Main.correctPrim = 0;
			Main.biggestPrim = 0;
			Main.status = -2;
			Main.log("FAILED: fast primes check/ loading (line: " + Main.correctPrim + ")  " + Main.timerFormat(System.nanoTime() - time1));
			throw e;
		} catch (Exception e) {
			Main.log("ERROR: fast primes check/ loading (line: " + Main.correctPrim + ")  " + Main.timerFormat(System.nanoTime() - time1));
			Main.log("\t" + e.getStackTrace());
			throw new Exception("An undetected problem occurred, the primes file isnt checked/ loaded.");
		}
		Main.status = 1;
		Main.log("DONE: fast primes check/ loading  " + Main.timerFormat(System.nanoTime() - time1));
	}
	
	@SuppressWarnings("unused")
	private final static void repairPrimListWithFile(boolean showProgress) throws Exception {
		Main.log("START: repairing primes file");
		long time1 = System.nanoTime();
		
		String pathName = (Main.primStorage.toString());
		File copyFile = new File(pathName.substring(0, pathName.length() - 4) + "copy.tmp");
		
		if (showProgress == true) {
			ConsoleProgressBar cProgress = new ConsoleProgressBar();
			try (
				BufferedWriter bWriter = new BufferedWriter(new FileWriter(copyFile));
				BufferedReader bReader = new BufferedReader(new FileReader(Main.primStorage));
			) {
				int progress = -1, progTemp = 0;
				double corPrimDou = (100. / Main.correctPrim);
				for (int i = 0; i < Main.correctPrim; i++) {
					bWriter.write(bReader.readLine() + "\n");
					progTemp = (int) (corPrimDou * i);
					if (progTemp != progress) {
						//Main.log("List repairing: " + progTemp + "%");
						Main.logAdd(cProgress.getProgress(progTemp));
						progress = progTemp;
					}
				}
				bWriter.close();
				Main.primStorage.delete();
				copyFile.renameTo(Main.primStorage);
				copyFile.delete();
				Main.log(cProgress.getProgress(100));
			} catch (Exception e) {
				Main.log("ERROR: repairing primes file  " + Main.timerFormat(System.nanoTime() - time1));
				Main.log("\t" + e.getStackTrace());
				throw new Exception("An undetected problem occurred, the primes file isnt repaired.");
			}
		} else { //showProgress == false
			try (
				BufferedWriter bWriter = new BufferedWriter(new FileWriter(copyFile));
				BufferedReader bReader = new BufferedReader(new FileReader(Main.primStorage));
			) {
				for (int i = 1; i <= Main.correctPrim; i++) {
					bWriter.write(bReader.readLine() + "\n");
				}
				bWriter.close();
				Main.primStorage.delete();
				copyFile.renameTo(Main.primStorage);
				copyFile.delete();
			} catch (Exception e) {
				throw e;
			}
		}
		Main.status = 1;
		Main.log("DONE: repairing primes file  " + Main.timerFormat(System.nanoTime() - time1));
	}

	private final static void repairPrimListWithBigList(boolean showProgress) throws Exception {
		Main.log("START: repairing primes file");
		long time1 = System.nanoTime();
		
		String pathName = (Main.primStorage.toString());
		File copyFile = new File(pathName.substring(0, pathName.length() - 4) + "copy.tmp");
		
		if (showProgress == true) {
			ConsoleProgressBar cProgress = new ConsoleProgressBar();
			try ( BufferedWriter bWriter = new BufferedWriter(new FileWriter(copyFile)) ) {
				int progress = -1, progTemp = 0;
				double corPrimDou = (100. / Main.correctPrim);
				for (int i = 0; i < Main.correctPrim; i++) {
					bWriter.write(Main.primList.get(i) + "\n");
					progTemp = (int) (corPrimDou * i);
					if (progTemp != progress) {
						//Main.log("List repairing: " + progTemp + "%");
						Main.logAdd(cProgress.getProgress(progTemp));
						progress = progTemp;
					}
				}
				bWriter.close();
				Main.primStorage.delete();
				copyFile.renameTo(Main.primStorage);
				copyFile.delete();
				Main.log(cProgress.getProgress(100));
			} catch (Exception e) {
				Main.log("ERROR: repairing primes file  " + Main.timerFormat(System.nanoTime() - time1));
				Main.log("\t" + e.getStackTrace());
				throw new Exception("An undetected problem occurred, the primes file isnt repaired.");
			}
		} else { //showProgress == false
			try ( BufferedWriter bWriter = new BufferedWriter(new FileWriter(copyFile)) ){
				for (Long i : Main.primList) {
					bWriter.write(i + "\n");
				}
				bWriter.close();
				Main.primStorage.delete();
				copyFile.renameTo(Main.primStorage);
				copyFile.delete();
			} catch (Exception e) {
				throw e;
			}
		}
		Main.status = 1;
		Main.log("DONE: repairing primes file  " + Main.timerFormat(System.nanoTime() - time1));
	}
}