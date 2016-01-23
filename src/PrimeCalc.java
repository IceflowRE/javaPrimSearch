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

public class PrimeCalc extends Main {
	
	protected static boolean isCurTestPrime;
	//search prims
	static void searchPrimes(long upTo) {
		log("START: test primes (" + Main.biggestPrim + " - " + upTo + ")");
		long time1 = System.nanoTime();
		try {
			for (long i = Main.biggestPrim + 2; i <= upTo; i += 2) { //loop for testing the number and give them to the distributor which will test them with MT
				distributor(i); //Multi
				if (isCurTestPrime) { //Multi
				//if (isPrimWithBigList(i) == true) { //single
					Main.primList.add(i);
				}
			}
			log("DONE: test primes  " + timerFormat(System.nanoTime() - time1));
		} catch (Throwable e) {
			log("ERROR: test primes  " + timerFormat(System.nanoTime() - time1));
			e.printStackTrace();
			JOptionDialog("Error", "An undetected problem occurred, test primes isnt finished\ntry to save the found primes", 0, new Object[] {"Ok"});
		}
	}

	private final static void distributor(long curTest) { //alternative a fixed parts size; tests the number like this easy e.g. test 800: the primlist is 80 large, so Thread1 will test primList 0-10, Thread2 11-20, and so on. the eachPart size is here 10
		long useParts = 1; //DEV
		int start;
		int end;
		isCurTestPrime = true; //if the number is a prim
		int eachPart = (Main.primList.size() - (Main.primList.size() % Main.maxParts)) / Main.maxParts; // every thread get his own part of this size
		int firstPart = eachPart + (Main.primList.size() % Main.maxParts);
		if (Main.primList.size() < (Main.maxCalcThreads * Main.maxCalcThreads)) { //DEV
			useParts = 1;
			eachPart = Main.primList.size();
		} else {
			useParts = Main.maxParts;
		}
		ExecutorService pool = Executors.newFixedThreadPool(Main.maxCalcThreads);
		start = 0;
		end = firstPart;
		System.out.println("MT starts" + curTest); //DEV
		Runnable worker = new PrimeDistributor(curTest, start, end);
		pool.execute(worker);
		for (int i = 1; i < useParts; i++) {
			start = end;
			end = (end + eachPart);
			worker = new PrimeDistributor(curTest, start, end);
			pool.execute(worker);
		}
		pool.shutdown();
		// Wait until all threads have finished
		while(!pool.isTerminated()) {
			if (!isCurTestPrime) {
				pool.shutdownNow();
				//break;
			}
		}
		System.out.println("MT ends" + curTest); //DEV
	}
	
	private final static boolean isPrimWithFile(long curTest) throws IOException { //dont forget flushing if the sqrtValue isnt in the list
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
	
	private final static boolean isPrim(long curTest) { // classic check of primes
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

class PrimeDistributor extends Thread {
	private long curTest;
	private int start;
	private long end;
	
	PrimeDistributor(long curTest, int start, long end) {
		this.curTest = curTest;
		this.start = start;
		this.end = end;
	}

	@Override
	public final void run() {
		long curPrim = 0;
		for (int i = this.start; (curPrim = Main.primList.get(i)) <= this.end; i++) {
			System.out.println(this.getName() + " - " + curPrim);
			if (this.curTest % curPrim == 0) {
				PrimeCalc.isCurTestPrime = false;
				return;
			}
		}
		return;
	}
}
