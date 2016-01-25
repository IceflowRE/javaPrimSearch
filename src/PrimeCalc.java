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
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PrimeCalc {
	
	protected static boolean isCurTestPrime;
	//search prims
	static void searchPrimes(long upTo) {
		if (Main.showProgressWhileCalc = true) {
			showCalcProgress progressDaemon = new showCalcProgress(Main.biggestPrim, upTo);
			try {
				for (long i = Main.biggestPrim + 2; i <= upTo; i += 2) { //loop for testing the number and give them to the distributor which will test them with MT
					if (isPrimWithBigList(i) == true) { //single
						Main.primList.add(i);
					}
					progressDaemon.update(i);
				}
			} catch (Throwable e) {
				e.printStackTrace();
				throw new Exceptions.undetectedProblemException();
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
			} catch (Throwable e) {
				e.printStackTrace();
				throw new Exceptions.undetectedProblemException();
			}
		}
	}
	
	private final static boolean isPrimWithFile(long curTest) throws IOException { //unused //dont forget flushing if the sqrtValue isnt in the list
		long sqrtValue = (long) Math.sqrt(curTest);
		if (Main.primStorage.canRead()) {
			try {
				BufferedReader bRead = new BufferedReader(new FileReader(Main.primStorage));
				long curPrim = Long.parseLong(bRead.readLine());
				while (curPrim <= sqrtValue) {
					if (curTest % curPrim == 0) {
						bRead.close();
						return false;
					}
					curPrim = Long.parseLong(bRead.readLine());
				}
				bRead.close();
			} catch (Exception e) {
				throw e;
			}
		} else {
			throw new FileNotFoundException("File is not readable");
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