package demo.Zhihao;

import java.io.Serializable;
import java.util.BitSet;

public final class Disk implements Serializable{
	static final int DEFAULT_SPACE = 64*1024;
	private static final long serialVersionUID = -1205155887998076950L;

	private static Disk defaultDisk = new Disk();
	public static Disk getDefaultDisk() {
		return defaultDisk;
	}

	private int space = DEFAULT_SPACE;
	public char[] storage = new char[DEFAULT_SPACE];
	private Directory rootDirectory = new Directory("root", null);
	private BitSet spaceBitMap = new BitSet(DEFAULT_SPACE);
	private Disk(){}
	
	public int[] alloc(int length) {
		int len = length;
		int[] returnValue = new int[length];
		for (int i = 0, j = 0; i < DEFAULT_SPACE && len > 0; i++, len--) {
			if (!spaceBitMap.get(i)) {
				returnValue[j++] = i;
			}
		}
		if (returnValue.length == length) {
			for (int i = 0; i < returnValue.length; i++) {
				spaceBitMap.set(returnValue[i], true);
			}
			space -= length;
			return returnValue;
		}
		returnValue = null;
		return null;
	}
	
	public void dealloc(int[] index) {
		for (int i : index) {
			spaceBitMap.set(i, false);
		}
		space += index.length;
	}
	
	public Directory getRootDirectory() {
		return rootDirectory;
	}
	
	public int space() {
		return space;
	}
}
