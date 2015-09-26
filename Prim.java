/*
* Author: Iceflower S
*/

import javax.swing.*;

import com.sun.javafx.css.CascadingStyle;

import javafx.scene.shape.Line;

import java.io.*;
import java.nio.file.Path;
//fIn.getChannel().position(0);
//bRead = new BufferedReader(new InputStreamReader(fIn));
public class Prim {
	public static void main(String[] args) throws IOException {
		String dir = "data";
		String file = "primes.txt";
		File primStorage = new File(dir + "/" + file);

		if (primStorage.exists()) {
			System.out.println("filescheck done");
		} else { //show message with option to create them
			{ //option dialog
				int choosen_temp = JOptionDialog("Missing files!", "Try to create them?", 2, new Object[] {"Yes", "No & Exit"});
				if(choosen_temp == -1 || choosen_temp == 1) {
					System.exit(0);
				} else {
					if ( createDir(dir, file) ) { //create file
						JOptionDialog("", "Succesfull created", 1, new Object[] {"Ok"});
					} else {
						JOptionDialog("Error", "A problem occurred (check write access and execute files)", 0, new Object[] {"Ok & Exit"});
						System.exit(0);
					}
				}
			}
		} //end error handling
		long[] stats = fastCheckPrimList(dir + "/" + file);
		if (stats[2] < 0) {
			if ( repairPrimList(stats, dir, file) == false) {
				System.out.println("Error while repairing");
				System.exit(0);
			}
		} else {
			System.out.println("fast primlistcheck done");
		}
	}

	public static boolean createDir(String dir, String file) throws IOException {
		File dirFile = new File(dir);
		File fileFile = new File(dir + "/" + file);
		if (!dirFile.exists()) { 
			if (!dirFile.mkdir()) {
				return false;
			}
		}
		fileFile.createNewFile();
		//add first prim 2
		FileWriter fw = new FileWriter(fileFile);
		BufferedWriter bw = new BufferedWriter(fw);
		bw.write("2" + "\n");
		bw.close();
		return true;
	}
	public static long[] fastCheckPrimList(String relPath) { //long: 0 = correct prim; 1 = biggest prim; 2 = status (-2 = recreate all, -1 = error file need to be cutted, 1 = all ok)
		long correctPrim = 0, biggestPrim = 0, status = 0;
		try {
			BufferedReader bRead = new BufferedReader(new FileReader(new File(relPath)));
			String curLine = bRead.readLine();
			long curPrim;
			if ( (curLine == null) || ((curPrim = Long.parseLong(curLine)) != 2)) {//first check
				return new long[] {correctPrim, biggestPrim, -2};
			}
			biggestPrim = 2;
			for (correctPrim = 1; (curLine = bRead.readLine()) != null; correctPrim++) { //check if everything is ok
				curPrim = Long.parseLong(curLine);
				if (curPrim > biggestPrim) { //main check
					biggestPrim = curPrim;
				} else { //if one next prim is smaller or equal as an previous its an error
					bRead.close();
					return new long[] {correctPrim, biggestPrim, -1};
				}
				bRead.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(0);
		}
		return new long[] {correctPrim, biggestPrim, 1};
	}

	private static boolean repairPrimList(long[] stats, String dir, String file) {
		int choosen_temp;
		String statusStr = stats[2] + "";
		File primStorage = new File(dir + "/" + file);
		switch (statusStr) {
		case "-2": {
				choosen_temp = JOptionDialog("Error in the primnumber list", "List will be recreated (you will lose maybe some primnumbers)", 2, new Object[] {"Yes", "No & Exit"});
				if(choosen_temp == -1 || choosen_temp == 1) { //close
					return false;
				} else { //try to repair
					if (primStorage.delete()) { //need work
						try {
							createDir(dir, file);
						} catch (Exception e) {
							e.printStackTrace();
							choosen_temp = JOptionDialog("Error while recreating", "Try to deleted (.../" + (dir + "/" + file) +")", 2, new Object[] {"Ok & Exit"});
							return false;
						}
					}
				}//end error handling
				JOptionDialog("", "Succesfull recreated", 1, new Object[] {"Ok"});
				return true; 
		} case "-1": {
				choosen_temp = JOptionDialog("Primnumber list arent correct", "Try to repair it? (you will lose maybe some primnumbers)", 2, new Object[] {"Yes", "No & Exit"});
				if (choosen_temp == -1 || choosen_temp == 1) { //close
					return false;
				} else { //try to repair need work
					File copyTemp =  new File (dir + "/" + (file.substring(0, file.length() - 4)) + "copy.tmp");
					try {
						BufferedReader bRead = new BufferedReader(new FileReader(primStorage));
						BufferedWriter bWrite = new BufferedWriter(new FileWriter(copyTemp));
						System.out.println(stats[0]);
						for (long i = 1; i <= stats[0]; i++) {
							long curPrim = Long.parseLong(bRead.readLine());
							System.out.println(i);
							bWrite.write(curPrim + "\n");
						}
						bWrite.close();
						bRead.close();
						BufferedReader bRead2 = new BufferedReader(new FileReader(copyTemp));
						BufferedWriter bWrite2 = new BufferedWriter(new FileWriter(primStorage));
						String curLine;
						for (long i = 1; ((curLine = bRead2.readLine()) != null); i++) {
							long curPrim = Long.parseLong(curLine);
							bWrite2.write(curPrim + "\n");
						}
						
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
}