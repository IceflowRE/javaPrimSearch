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
		Main.log("START: test primes (" + Main.biggestPrim + " - " + upTo + ")");
		long time1 = System.nanoTime();
		try {
			for (long i = Main.biggestPrim + 2; i <= upTo; i += 2) { //loop for testing the number and give them to the distributor which will test them with MT
				distributor(i); //Multi
				if (isCurTestPrime) { //Multi
				//if (isPrimWithBigList(i) == true) { //single
					Main.primList.add(i);
				}
			}
			Main.log("DONE: test primes  " + Main.timerFormat(System.nanoTime() - time1));
		} catch (Throwable e) {
			Main.log("ERROR: test primes  " + Main.timerFormat(System.nanoTime() - time1));
			e.printStackTrace();
			Main.JOptionDialog("Error", "An undetected problem occurred, test primes isnt finished\ntry to save the found primes", 0, new Object[] {"Ok"});
		}
	}

	private final static void distributor(long curTest) { //alternative a fixed parts size; tests the number like this easy e.g. test 800: the primlist is 80 large, so Thread1 will test primList 0-10, Thread2 11-20, and so on. the eachPart size is here 10
		int useThreads = 1;
		int start;
		isCurTestPrime = true; //if the number is a prim, at start its a prim until you check its not a prime
		if (Main.primList.size() < (Main.maxCalcThreads * Main.maxCalcThreads)) { //DEV // use MT only if there are enough index's, maybe later calc the best threads quantity until maxThreads which you are use
			useThreads = 1;
		} else {
			useThreads = Main.maxCalcThreads;
		}
		ExecutorService pool = Executors.newFixedThreadPool(Main.maxCalcThreads);
		System.out.println(Main.primList);
		System.out.println("MT starts" + curTest); //DEV
		start = 0;
		Runnable worker = new PrimeDistributor(curTest, start, useThreads); //init the first worker, at least there is every time one worker
		pool.execute(worker);
		for (int i = 1; i < useThreads; i++) { // Main.maxCalcThreads = 3: thread1 tests: 0,3,6,9,... thread2 tests: 1,4,7,10,... thread3 tests: 2,5,8,11,...  (numbers are index of primList)
			start = i;
			worker = new PrimeDistributor(curTest, start, useThreads);
			pool.execute(worker);
		}
		pool.shutdown();
		// Wait until all threads have finished
		while(!pool.isTerminated()) {
			if (!isCurTestPrime) {
				pool.shutdownNow();
				break;
			}
		}
		while(!pool.isShutdown()) { //needed?

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
	private int listSize;
	private long useThreads;
	
	PrimeDistributor(long curTest, int start, long useThreads) {
		this.curTest = curTest;
		this.start = start;
		this.listSize = Main.primList.size();
		this.useThreads = useThreads;
	}

	@Override
	public final void run() {
		long curPrim = 0;
		long sqrt = (long) Math.sqrt(curTest);
		for (int i = this.start; (i < this.listSize) && ((curPrim = Main.primList.get(i)) <= sqrt); i += this.useThreads) {
			System.out.println(this.getName() + " - " + curPrim);
			if (this.curTest % curPrim == 0) {
				PrimeCalc.isCurTestPrime = false; //change this boolean to shutdown all other threads via shutdownNow()
				return;
			}
		}
		return;
	}
}
