package demo.Zhihao;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.BitSet;


public final class Disk implements Serializable{
	static final int DEFAULT_SPACE = 64*1024;
	private static final long serialVersionUID = -1205155887998076950L;

	private static File diskFile = new File("./DiskFile");

	private static Disk defaultDisk = null;
	public static Disk getDefaultDisk() {
		return defaultDisk;
	}
	
	public static void init() {
		try {
			@SuppressWarnings("resource")
			ObjectInputStream is = new ObjectInputStream(new FileInputStream(diskFile));
			defaultDisk = (Disk)is.readObject();
			Terminal.getTerminal().println("Data has been restored from HD successfully.");
		} catch (Exception e) {
			Terminal.getTerminal().println("Error: Disk record is either nonexistent or damaged, " +
					"a new record will be created.");
			defaultDisk = new Disk();
		}
	}

	public char[] storage = new char[DEFAULT_SPACE];
	private Directory rootDirectory = new Directory("/", null);
	private BitSet spaceBitMap = new BitSet(DEFAULT_SPACE/512); // 512bytes per block
//	private Disk(int i) {
//		try {
//			@SuppressWarnings("resource")
//			ObjectInputStream is = new ObjectInputStream(new FileInputStream(diskFile));
//			defaultDisk = (Disk)is.readObject();
//			Terminal.getTerminal().println("Data has been restored from HD successfully.");
//		} catch (Exception e) {
//			Terminal.getTerminal().println("Error: Disk record is either nonexistent or damaged, " +
//					"a new record will be created.");
//			defaultDisk = new Disk(0);
//		}
//	}
	private Disk(){};

	public int[] alloc(int length) {
		int len = length/512 + ((length%512)==0?0:1);
		int tmp = len;
		int[] returnValue = new int[len];
		for (int i = 0, j = 0; i < DEFAULT_SPACE && len > 0; i++) {
			if (!spaceBitMap.get(i)) {
				returnValue[j++] = i;
				len--;
			}
		}
		if (returnValue.length == tmp) {
			for (int i = 0; i < returnValue.length; i++) {
				spaceBitMap.set(returnValue[i], true);
			}
			return returnValue;
		}
		returnValue = null;
		return null;
	}

	public void dealloc(int[] index) {
		for (int i : index) {
			spaceBitMap.set(i, false);
		}
	}

	public Directory getRootDirectory() {
		return rootDirectory;
	}
	
	public void writeHD() {
		try {
			ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream(diskFile));
			os.writeObject(defaultDisk);
			os.flush();
			os.close();
		} catch (Exception e) {
			Terminal.getTerminal().println("Fatal Error: Data cannot be written to HD. " +
					"All the data will be automatically abandoned.");
		}
	}
	
	public void format() {
		spaceBitMap.clear();
		rootDirectory = new Directory("/", null);
		System.gc();
	}
	
	public int spaceUsed() {
		return spaceBitMap.cardinality();
	}
}
