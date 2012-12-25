package demo.Zhihao;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.swing.ImageIcon;

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
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		modifiedTime = createTime = format.format(new Date());
	}
	public void modify()
	{
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		modifiedTime = format.format(new Date());
	}
	public String getCreateTime()
	{
		return createTime;
	}
	public String getModifiedTime() {
		return modifiedTime;
	}
	public abstract void remove();
	public abstract ImageIcon getIcon();
}

class Directory extends FileType implements Serializable{
	private static final long serialVersionUID = -6446295381907151453L;

	private ArrayList<FileType> directoryEntries = new ArrayList<FileType>();
	private Directory parentDirectory = null;

	public Directory(String name, Directory parent) {
		super();
		_name = name;
		parentDirectory = parent;
	}

	public ArrayList<FileType> getDirectoryEntries() {
		return directoryEntries;
	}
	public void addDirectoryEntries(FileType entry){
		directoryEntries.add(entry);
	}

	public Directory getParentDirectory() {
		return parentDirectory;
	}

	public int numberOfObjects() {
		return directoryEntries.size();
	}

	public Document containsFile(String name) {
		FileType file = findItem(name);
		if (file instanceof Document)
			return (Document)file;
		return null;
	}

	public Directory containsDirectory(String name) {
		FileType file = findItem(name);
		if (file instanceof Directory)
			return (Directory)file;
		return null;
	}

	public FileType findItem(String name) {
		for (FileType file : directoryEntries) {
			if (file.getName().equals(name))
				return file;
		}
		return null;
	}

	@Override
	public void remove() {
		for (FileType file : directoryEntries) {
			file.remove();
		}
		directoryEntries.clear();
	}
	
	@Override
	public ImageIcon getIcon() {
		return new ImageIcon(getClass().getResource("folder.png"));
	}
}

class Document extends FileType implements Serializable{
	private static final long serialVersionUID = -8845790537229462381L;
	static final char EOF = 0;

	private ArrayList<Integer> blockIndex = new ArrayList<>();
	private int _size = 0;
	private int _blocks = 0;

	public int size() {
		return _size;
	}

	public int block() {
		return _blocks;
	}

	public boolean save(String doc) {

		int blockNeeded  = doc.length()/512 + ((doc.length()%512)==0?0:1);
		int blockPresent = _size/512 + ((_size%512)==0?0:1);

		if (blockNeeded > blockPresent) {

			int[] tmp = Disk.getDefaultDisk().alloc(blockNeeded - blockPresent);
			if (tmp != null)
				for (int i : tmp)
					blockIndex.add(i);
			else return false;
		} else if (blockNeeded < blockPresent) {
			int[] tmp = new int[blockPresent - blockNeeded];
			for (int i = 0; i < tmp.length; i ++) {
				tmp [i]= blockIndex.get(0);
				blockIndex.remove(0);
			}
			Disk.getDefaultDisk().dealloc(tmp);
		}
		char[] content = doc.toCharArray();
		int len = doc.length();
		for (int i = 0; i < blockIndex.size(); i++) {
			if (len >= 512) {
				for (int j = 0; j < 512; j++) {
					Disk.getDefaultDisk().storage[blockIndex.get(i)*512 + j] = 
							content[i*512+j];
				}
				len -= 512;
			} else if (len < 512) {
				for (int j = 0; j < len; j++) {
					Disk.getDefaultDisk().storage[blockIndex.get(i)*512 + j] =
							content[i*512+j];
				}
				Disk.getDefaultDisk().storage[blockIndex.get(i)*512 + len] = EOF;
			}
		}

		_size = doc.length();
		_blocks = blockNeeded;
		return true;
	}

	public String open() {
		char[] content = new char[_size];
		for (int i = 0; i < blockIndex.size(); i++) {
			if (i != (blockIndex.size()-1)) {
				for (int j = 0; j < 512; j++) {
					content[i*512 + j] = 
							Disk.getDefaultDisk().storage[blockIndex.get(i)*512+j];
				}
			} else {
				for (int j = 0; 
						j < 512 && Disk.getDefaultDisk().storage[blockIndex.get(i)*512+j] != EOF;
						j++) {
					content[i*512 + j] = 
							Disk.getDefaultDisk().storage[blockIndex.get(i)*512+j];
				}
			}
		}
		String contents = new String(content);
		return contents;
	}

	public void remove() {
		int[] tmp = new int[blockIndex.size()];
		for (int i = 0; i < blockIndex.size(); i++) {
			tmp[i] = blockIndex.get(i);
		}
		Disk.getDefaultDisk().dealloc(tmp);
		blockIndex.clear();
		blockIndex = null;
	}

	public Document(String name) {
		super();
		_name = name;
	}
	
	@Override
	public ImageIcon getIcon() {
		return new ImageIcon(getClass().getResource("file.png"));
	}
}
