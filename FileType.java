package demo.Zhihao;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Date;

abstract class FileType implements Serializable{
	private static final long serialVersionUID = -4205386282975820009L;

	protected String createTime = null;
	protected String modifiedTime = null;
	protected String _name = null;
	public String getName() {
		return _name;
	}
	public void setName(String name) {
		_name = name;
	}
	public FileType()
	{
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss aa");
		modifiedTime = createTime = format.format(new Date());
	}
	public void modify()
	{
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss aa");
		modifiedTime = format.format(new Date());
	}
	public String getCreateTime()
	{
		return createTime;
	}
	public String getModifiedTime() {
		return modifiedTime;
	}
}

class Directory extends FileType {
	private static final long serialVersionUID = -6446295381907151453L;

	private ArrayList<FileType> directoryEntry = new ArrayList<FileType>();
	private Directory parentDirectory = null;
	
	public Directory(String name, Directory parent) {
		super();
		_name = name;
		parentDirectory = parent;
	}
		
	public ArrayList<FileType> getDirectory() {
		return directoryEntry;
	}
	public void addDirectoryEntries(FileType entry){
		directoryEntry.add(entry);
	}

	public Directory getParentDirectory() {
		return parentDirectory;
	}
}

class Document extends FileType {
	private static final long serialVersionUID = -8845790537229462381L;
	
	private BitSet indexBitSet = new BitSet();

	public int size() {
		return indexBitSet.cardinality();
	}
	
	public boolean save(String doc) {

		if (doc.length() > indexBitSet.cardinality()) {
			int[] tmp = Disk.getDefaultDisk().alloc(doc.length() - indexBitSet.cardinality());
			if (tmp != null)
				for (int i : tmp)
					indexBitSet.set(i);
			else return false;
		} else if (doc.length() < indexBitSet.cardinality()) {
			int[] tmp = new int[indexBitSet.cardinality() - doc.length()];
			for (int i = 0; i < tmp.length; i ++) {
				tmp [i]= indexBitSet.nextSetBit((i>0?i-1:0));
				indexBitSet.set(tmp[i], false);
			}
			Disk.getDefaultDisk().dealloc(tmp);
		}
		char[] content = doc.toCharArray();
		for (int i = 0, idx = 0; i < content.length; i ++, idx++) {
			Disk.getDefaultDisk().storage[idx = indexBitSet.nextSetBit(idx)] = content[i];
		}
		return true;
	}
	
	public String open() {
		char[] content = new char[indexBitSet.cardinality()];
		for (int i = 0, idx = 0; i < indexBitSet.cardinality(); i++, idx++) {
			content[i] = Disk.getDefaultDisk().storage[indexBitSet.nextSetBit(idx)];
		}
		String contents = new String(content);
		return contents;
	}
		
	public Document(String name) {
		super();
		_name = name;
	}
}
