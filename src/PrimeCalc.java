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
import java.io.FileReader;
import java.io.IOException;

public class PrimeCalc {
	
	protected static boolean isCurTestPrime;
	//search prims
	static void searchPrimes(long upTo) throws Exception {
		Main.log("START: test primes (" + Main.decimalFormat.format(Main.biggestPrim) + " - " + Main.decimalFormat.format(upTo) + ")");
		long time1 = System.nanoTime();
		
		if (Main.showProgressWhileCalc = true) {
			showCalcProgress progressDaemon = new showCalcProgress(Main.biggestPrim, upTo);
			try {
				for (long i = Main.biggestPrim + 2; i <= upTo; i += 2) { //loop for testing the number and give them to the distributor which will test them with MT
					if (isPrimWithBigList(i) == true) { //single
						Main.primList.add(i);
					}
					progressDaemon.update(i);
				}
				
				if (false) { //prevent Unreachable catch block for IOException if isPrimWithFile() is not used (atm no option for it)
					throw new IOException();
				}
			} catch (IOException e) { //for isPrimWithFile()
				Main.log("ERROR: test primes  " + Main.timerFormat(System.nanoTime() - time1));
				throw e;
			} catch (Exception e) { //TODO: add out of memory exception
				Main.log("ERROR: test primes  " + Main.timerFormat(System.nanoTime() - time1));
				Main.log("\t" + e.getStackTrace());
				throw new Exception("An undetected problem occurred, test primes isnt finished.");
			} finally {
				progressDaemon.interrupt();
				System.out.println();
			}
		} else { // showProgressWhileCalc = false
			try {
				for (long i = Main.biggestPrim + 2; i <= upTo; i += 2) { //loop for testing the number and give them to the distributor which will test them with MT
					if (isPrimWithBigList(i) == true) { //single
						Main.primList.add(i);
					}
				}
			} catch (Exception e) { //TODO: add out of memory exception
				Main.log("ERROR: test primes  " + Main.timerFormat(System.nanoTime() - time1));
				Main.log("\t"+ e.getStackTrace());
				throw new Exception("An undetected problem occurred, test primes isnt finished.");
			}
		}
		
		Main.log("DONE: test primes  " + Main.timerFormat(System.nanoTime() - time1));
	}
	
	@SuppressWarnings("unused")
	private final static boolean isPrimWithFile(long curTest) throws IOException { //unused //dont forget flushing if the sqrtValue isnt in the list
		long sqrtValue = (long) Math.sqrt(curTest);
		if (Main.primStorage.canRead()) {
			try ( BufferedReader bRead = new BufferedReader(new FileReader(Main.primStorage)) ) {
				long curPrim = Long.parseLong(bRead.readLine());
				while (curPrim <= sqrtValue) {
					if (curTest % curPrim == 0) {
						return false;
					}
					curPrim = Long.parseLong(bRead.readLine());
				}
			} catch (Exception e) {
				throw e;
			}
		} else {
			throw new IOException("File is not readable");
		}
		return true;
	}
	
	private final static boolean isPrimWithBigList(long curTest) {
		long sqrtValue = (long) Math.sqrt(curTest);
		long curPrim = 0;
		for (int i = 0; (curPrim = Main.primList.get(i)) <= sqrtValue; i++) {
			if (curTest % curPrim == 0) {
				return false;
			}
		}
		return true;
	}
	
	@SuppressWarnings("unused")
	private final static boolean isPrim(long curTest) { //unused // classic check of primes
		long sqrtValue = (long) Math.sqrt(curTest);
		if (curTest % 2 == 0) {
			return false;
		}
		for (long i = 3; i <= sqrtValue; i += 2) {
			if (curTest % i == 0) {
				return false;
			}
		}
		return true;
	}	
}

class showCalcProgress extends Thread {

	private long start;
	private double factor;
	private ConsoleProgressBar conPBar;
	
	public showCalcProgress(long biggestPrim, long upTo) {
		this.setDaemon(true);
		this.start = biggestPrim;
		this.factor = 100. / (upTo - biggestPrim);
		this.conPBar = new ConsoleProgressBar();
	}
	
	public void update(long curPrim) {
		System.out.print(conPBar.getProgress((int) (this.factor * (curPrim  - this.start))) );
	}
}