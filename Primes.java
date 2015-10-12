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
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.LineNumberReader;

import org.magicwerk.brownies.collections.BigList;

class Primes {
	private final File dirFile; //path to data
	private final File primStorage; //path includes data
	private BigList<Long> primList = new BigList<Long>();
	private long correctPrim;
	private long biggestPrim;
	private int status;
	private int lastWriteIndex;
	
	//constructor
	public Primes(String dir, String file) {
		this.dirFile = new File(dir);
		this.primStorage = new File(dir + "/" + file);
		this.correctPrim = 0;
		this.biggestPrim = 0;
		this.status = -2;
		this.lastWriteIndex = 0;
	}
	
	//methodes
	public final boolean recreateDir() throws IOException {
		if (!this.dirFile.exists()) { //if file not exists
			if (!this.dirFile.mkdir()) { //if creating the file = false
				return false;
			}
		}
		try ( BufferedWriter bWriter = new BufferedWriter(new FileWriter(this.primStorage)) ) {
			this.primStorage.createNewFile();
			bWriter.write("2" + "\n" + "3" + "\n");
		} catch (Exception e) {
			throw e;
		}
		this.status = 1;
		return true;
	}
	public final boolean fastCheckPrimList() throws IOException { //long: 0 = correct prim; 1 = biggest prim; 2 = status (-2 = recreate all, -1 = error file need to be cutted, 1 = all ok)
		try ( LineNumberReader lReader = new LineNumberReader(new BufferedReader(new FileReader(this.primStorage))) ) {
			long curPrim;
			String curLine;
			for (int i = 2; i <= 3; i++) { //test if the first is 2 and the second 3
				curLine = lReader.readLine();
				System.out.println(i);
				if ( (curLine == null) || ((curPrim = Long.parseLong(curLine)) != i)) {//first check
					this.correctPrim = 0;
					this.biggestPrim = 0;
					this.status = -2;
					return false;
				}
			}
			biggestPrim = 3;
			while ((curLine = lReader.readLine()) != null) { //check if everything is ok
				curPrim = Long.parseLong(curLine);
				if (curPrim > this.biggestPrim && (curPrim % 2 == 1)) { //main check
					this.biggestPrim = curPrim;
				} else { //if one prim is smaller or equal as an previous throw an error
					this.correctPrim = lReader.getLineNumber() - 1;
					this.status = -1;
					return false;
				}
				primList.add(curPrim);
			}
			this.correctPrim = lReader.getLineNumber();
			this.lastWriteIndex = lReader.getLineNumber();
		} catch (Exception e) {
			this.correctPrim = 0;
			this.biggestPrim = 0;
			this.status = -2;
			return false;
		}
		this.status = 1;
		return true;
	}
	public final boolean fastCheckPrimStoreBigList() throws IOException { //long: 0 = correct prim; 1 = biggest prim; 2 = status (-2 = recreate all, -1 = error file need to be cutted, 1 = all ok)
		try ( LineNumberReader lReader = new LineNumberReader(new BufferedReader(new FileReader(this.primStorage))) ) {
			;
			long curPrim;
			String curLine;
			for (int i = 2; i <= 3; i++) { //test if the first is 2 and the second 3
				curLine = lReader.readLine();
				if ( (curLine == null) || ((curPrim = Long.parseLong(curLine)) != i)) {//first check
					this.correctPrim = 0;
					this.biggestPrim = 0;
					this.status = -2;
					return false;
				}
				primList.add((long) i);
			}
			this.biggestPrim = 3;
			while ((curLine = lReader.readLine()) != null) { //check if everything is ok
				curPrim = Long.parseLong(curLine);
				if (curPrim > this.biggestPrim && (curPrim % 2 == 1)) { //main check
					this.biggestPrim = curPrim;
				} else { //if one prim is smaller or equal as an previous throw an error
					this.correctPrim = lReader.getLineNumber() - 1;
					this.status = -1;
					return false;
				}
				this.primList.add(curPrim);
			}
			this.correctPrim = lReader.getLineNumber();
			this.lastWriteIndex = lReader.getLineNumber();
		} catch (Exception e) {
			this.correctPrim = 0;
			this.biggestPrim = 0;
			this.status = -2;
			return false;
		}
		this.status = 1;
		return true;
	}
	public final boolean repairPrimListWithFile(boolean showProgress) throws IOException {
		String pathName = (this.primStorage.toString());
		File copyFile = new File(pathName.substring(0, pathName.length() - 4) + "copy.tmp");
		if (showProgress == true) {
			try (
				BufferedWriter bWriter = new BufferedWriter(new FileWriter(copyFile));
				BufferedReader bReader = new BufferedReader(new FileReader(this.primStorage));
			) {
				int progress = 0, progTemp;
				double corPrimDou = (100. / this.correctPrim);
				for (int i = 0; i < this.correctPrim; i++) {
					bWriter.write(bReader.readLine() + "\n");
					progTemp = (int) (corPrimDou * i);
					if (progTemp != progress) {
						System.out.println("List repairing: " + progTemp + "%");
						progress = progTemp;
					}
				}
				bWriter.close();
				primStorage.delete();
				copyFile.renameTo(primStorage);
				copyFile.delete();
			} catch (Exception e) {
				throw e;
			}
			this.status = 1;
			return true;
		} else { //showProgress == false
			try (
				BufferedWriter bWriter = new BufferedWriter(new FileWriter(copyFile));
				BufferedReader bReader = new BufferedReader(new FileReader(this.primStorage));
			) {
				for (int i = 1; i <= this.correctPrim; i++) {
					bWriter.write(bReader.readLine() + "\n");
				}
				bWriter.close();
				primStorage.delete();
				copyFile.renameTo(primStorage);
				copyFile.delete();
			} catch (Exception e) {
				throw e;
			}
			this.status = 1;
			return true;
		}
	}
	public final boolean repairPrimListWithBigList(boolean showProgress) throws IOException {
		String pathName = (this.primStorage.toString());
		File copyFile = new File(pathName.substring(0, pathName.length() - 4) + "copy.tmp");
		if (showProgress == true) {
			try ( BufferedWriter bWriter = new BufferedWriter(new FileWriter(copyFile)) ) {
				int progress = 0, progTemp;
				double corPrimDou = (100. / this.correctPrim);
				for (int i = 0; i < this.correctPrim; i++) {
					bWriter.write(this.primList.get(i) + "\n");
					progTemp = (int) (corPrimDou * i);
					if (progTemp != progress) {
						System.out.println("List repairing: " + progTemp + "%");
						progress = progTemp;
					}
				}
				bWriter.close();
				this.primStorage.delete();
				copyFile.renameTo(primStorage);
				copyFile.delete();
			} catch (Exception e) {
				throw e;
			}
			this.status = 1;
			return true;
		} else { //showProgress == false
			try ( BufferedWriter bWriter = new BufferedWriter(new FileWriter(copyFile)) ){
				for (Long i : this.primList) {
					bWriter.write(i + "\n");
				}
				bWriter.close();
				this.primStorage.delete();
				copyFile.renameTo(this.primStorage);
				copyFile.delete();
			} catch (Exception e) {
				throw e;
			}
			this.status = 1;
			return true;
		}
	}
	public final boolean isPrimWithFile(long curTest) throws IOException { //dont forget flushing if the sqrtValue isnt in the list
		long sqrtValue = (long) Math.sqrt(curTest);
		if (this.primStorage.canRead()) {
			try {
				BufferedReader bRead = new BufferedReader(new FileReader(this.primStorage));
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
public final boolean isPrimWithBigList(long curTest) {
		long sqrtValue = (long) Math.sqrt(curTest);
		long curPrim = 0;
		for (int i = 0; (curPrim = this.primList.get(i)) <= sqrtValue; i++) {
			if (curTest % curPrim == 0) {
				return false;
			}
		}
		return true;
	}
	public final boolean isPrim(long curTest) { //check if the input value is a prim
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
	public final boolean writePrimStorage(boolean showProgress) throws IOException {
		if (showProgress == true) {
			try ( BufferedWriter bWriter = new BufferedWriter(new FileWriter(this.primStorage, this.existsDir())) ){
				int progress = 0, progTemp;
				int lastWriteIndexBefore = this.lastWriteIndex;
				double corPrimDou = (100. / (this.correctPrim - this.lastWriteIndex));
				for (int i = this.lastWriteIndex; i < this.correctPrim; i++) {
					bWriter.write(this.primList.get(i) + "\n");
					this.lastWriteIndex = i;
					progTemp = (int) (corPrimDou * (i - lastWriteIndexBefore));
					if (progTemp != progress) {
						System.out.println("List writing: " + progTemp + "%");
						progress = progTemp;
					}
				}
			} catch (Exception e) {
				throw e;
			}
			return true;	
		} else { //showProgress = off
			try (BufferedWriter bWriter = new BufferedWriter(new FileWriter(this.primStorage, this.existsDir())) ) {
				for (int i = this.lastWriteIndex; i <= this.correctPrim; i++) {
					bWriter.write(this.primList.get(i) + "\n");
					this.lastWriteIndex = i;
				}
			} catch (Exception e) {
				throw e;
			}
			return true;	
		}
	}

	//setter
	public final boolean addPrim(long prim) {
		try {
			this.primList.add(prim);
		} catch (Exception e) {
			throw e;
		}
		return true;
	}
	public final boolean updateStats() {
		this.correctPrim = this.primList.size();
		this.biggestPrim = this.primList.getLast();
		return true;
	}

	//getter
	public final boolean existsDir() {
		if (primStorage.exists()) {
			return true;
		}
		return false;
	}
	public final long getCorrectPrim() {
		return this.correctPrim;
	}
	public final long getBiggestPrim() {
		return this.biggestPrim;
	}
	public final int getStatus() {
		return this.status;
	}
}