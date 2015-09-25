/*
* Author: Iceflower S
*/

import javax.swing.*;

import javafx.scene.shape.Line;

import java.io.*;
//fIn.getChannel().position(0);
//bRead = new BufferedReader(new InputStreamReader(fIn));
public class Prim {
	public static void main(String[] args) throws IOException {
		String dir = "data";
		String file = "primes.txt";


		if (checkDir(dir + "/" + file)) {
			System.out.println("files correct");
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
		fastCheckPrimList(dir + "/" + file);
		File primStorage = new File(dir + "/" + file);
		System.out.println(dir + "/" + file);
		System.out.println(primStorage.exists());
	}

	public static boolean checkDir(String dir) {
		File dirFile = new File(dir);
		if (dirFile.exists()) {
			return true;
		}
		return false;
	}
	public static boolean createDir(String dir, String file) throws IOException {
		File dirFile = new File(dir);
		File fileFile = new File(dir + "/" + file);
		if (!dirFile.mkdir()) {
			return false;
		}
		fileFile.createNewFile();
		//add first prim 2
		FileWriter fw = new FileWriter(dir + "/" + file);
		BufferedWriter bw = new BufferedWriter(fw);
		bw.write("2");
		bw.close();
		return true;
	}
	public static double[] fastCheckPrimList(String relPath) { //double: 0 = correct prim; 1 = biggest prim; 2 = status (-2 = recreate all, -1 = error file need to be cutted, 0 = empty, 1 = all ok)
		double correctPrim = 0, biggestPrim = 0, status = 0;
		relPath = "/" + relPath;
		System.out.println(relPath);
		relPath = (new File("").getAbsolutePath() + "/" + relPath);
		relPath = relPath.replace("\\", "/"); //change to win theme
		try {
			BufferedReader bRead = new BufferedReader(new FileReader(new File(relPath)));
		
		String curLine;
		Double curPrim = Double.parseDouble(bRead.readLine());
		if (curPrim == 2) {
			biggestPrim = curPrim;
			correctPrim = 1;
			status = 1;
		} else {
			status = -2;
			int choosen_temp = JOptionDialog("Error in the primnumber list", "List will be recreated (you will lose maybe some primnumbers)", 2, new Object[] {"Yes", "No & Exit"});
			if(choosen_temp == -1 || choosen_temp == 1) { //close
				System.exit(0);
			} else { //try to repair
				if (repairPrimList(status)[2] == 1) { //need work
					JOptionDialog("", "Succesfull deleted", 1, new Object[] {"Ok"});
					return new double[] {1, biggestPrim, status};
				} else {
					choosen_temp = JOptionDialog("Error while deleting", "Try to deleted it yourself (dir/data/Primzahlen.txt)", 2, new Object[] {"Ok & Exit"});
					System.exit(0);
				}
			} //end error handling
		}
		while ((curLine = bRead.readLine()) == null) { //check if everything is ok
			curPrim = Double.parseDouble(curLine);
			if (curPrim > biggestPrim) { //main check
				biggestPrim = curPrim;
				correctPrim += 1;
			} else { //if one next prim is smaller or equal as an previous its an error
				status = 1;
				int choosen_temp = JOptionDialog("Primnumber list arent correct", "Try to repair it? (you will lose maybe some primnumbers)", 2, new Object[] {"Yes", "No & Exit"});
				if(choosen_temp == -1 || choosen_temp == 1) { //close
					System.exit(0);
				} else { //try to repair
					if (repairPrimList(status)[2] == 1) { //need work
						JOptionDialog("", "Succesfull repaired", 1, new Object[] {"Ok"});
						return new double[] {1, biggestPrim, status};
					} else {
						choosen_temp = JOptionDialog("Error in the primnumber list", "List will be deleted", 2, new Object[] {"Yes", "No & Exit"});
					}
				}
			} //end error handling
		}
		status = 1;
		} catch (Exception e) {
			System.out.println("Error");
			System.exit(0);
		}
		return new double[] {correctPrim, biggestPrim, status};
	}

	private static double[] repairPrimList(double status) {
		
		return ;
	}

	public static int JOptionDialog(String title, String hint, int message, Object[] options) {
		return JOptionPane.showOptionDialog(null, hint, title, JOptionPane.DEFAULT_OPTION, message, null, options, options[0]);
	}
}