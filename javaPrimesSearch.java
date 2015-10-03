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
			System.out.println("FAILED: fast primes check/ loading  " + timerFormat(System.nanoTime() - time1));
			switch (primObj.getStatus()) {
			case (-2): { //recreate file
				int choosen_temp = JOptionDialog("Mistake in the primes file", "Try to repair it? (maybe you will lose some primes)", 2, new Object[] {"Yes", "No & Exit"});
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
				int choosen_temp = JOptionDialog("Mistake in the primes file", "List should be repaired (you will lose all found primes)", 2, new Object[] {"Yes", "No & Exit"});
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
		
		{ //dev
			System.out.println("Correct primes:  " + primObj.getCorrectPrim());
			System.out.println("Biggest prime:  " + primObj.getBiggestPrim());
			System.out.println("Status:  " + primObj.getStatus());
			primObj.printBigList();
		} //dev
	} //end main
	
	//functions
	public static String timerFormat(long nanosec) {
		return new SimpleDateFormat("mm:ss.SSS").format(new Date((long) (nanosec * 1E-6)));
	}
	public static int JOptionDialog(String title, String hint, int message, Object[] options) {
		return JOptionPane.showOptionDialog(null, hint, title, JOptionPane.DEFAULT_OPTION, message, null, options, options[0]);
	}

}