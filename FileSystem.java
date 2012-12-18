package demo.Zhihao;

import java.util.Scanner;

public class FileSystem {

	private StringBuffer currentPath = new StringBuffer("/");
	private Directory currentDirectory = null;

	public String pwd() {
		return currentPath.toString();
	}
	private void prompt() {
		System.out.print("FileSys:~ user$ ".replaceFirst("~", pwd())); 
	}
	public void mkfs() {
		Disk.getDefaultDisk().format();
		currentDirectory = Disk.getDefaultDisk().getRootDirectory();
		currentPath = new StringBuffer("/");
		System.out.println("Success");
		System.gc();
	}
	public void ls() {
		for (FileType file : currentDirectory.getDirectoryEntries()) {
			int content = 1;
			String filename = file.getName();
			int size = 0;
			if (file instanceof Document) {
				size = ((Document) file).size();
			} else if (file instanceof Directory) {
				content = ((Directory) file).numberOfObjects();
				filename = filename.concat("/");
			}

			System.out.println(content + "\t" +
					size + "\t" +
					file.getCreateTime() + "\t" +
					file.getModifiedTime() + "\t" +
					filename); // contents of a directory
		}
	}
	public boolean touch(String param) {
		String vParam = param.substring(0, 
				param.indexOf(' ') == -1 ? param.length() : param.charAt(-1));
		if (vParam.length() == 0) {
			System.out.println("touch: Wrong file name");
			return false;
		}
		if (currentDirectory.containsDirectory(vParam) != null) {
			System.out.println("touch: "+vParam+": is already taken");
			return false;
		}
		FileType tmp = currentDirectory.containsFile(vParam); 
		if (tmp != null) {
			tmp.modify();
			return true;
		}
		currentDirectory.addDirectoryEntries(new Document(vParam));
		return true;
	}
	public void mkdir(String param) {
		String vParam = param.substring(0, 
				param.indexOf(' ') == -1 ? param.length() : param.charAt(-1));
		if (vParam.length() == 0) {
			System.out.println("mkdir: Wrong file name");
			return;
		}
		if (currentDirectory.containsFile(vParam) != null) {
			System.out.println("touch: "+vParam+": is already taken");
			return;
		}
		FileType tmp = currentDirectory.containsDirectory(vParam); 
		if (tmp != null)
			System.out.println("mkdir: " + vParam + ": File exists");
		else
			currentDirectory.addDirectoryEntries(new Directory(vParam, currentDirectory));
	}
	public void cd(String param) {
		String vParam = param.substring(0, 
				param.indexOf(' ') == -1 ? param.length() : param.charAt(-1));
		if (vParam.length() == 0) {
			return;
		}		
		if (vParam.charAt(vParam.length()-1) == '/')
			vParam = vParam.substring(0, vParam.length()-1);
		String[] params = vParam.split("/");
		boolean flag = true;
		for (String s : params) {
			if (!s.equals("..")) {
				flag = false;
				break;
			}
		}
		if (flag) {
			for (int i = 0; i < params.length; i++) {
				if (currentDirectory.getParentDirectory() != null) {
					currentPath.delete(
							currentPath.length() - currentDirectory.getName().length() -1,
							currentPath.length());
					currentDirectory = currentDirectory.getParentDirectory();
				} else break;
			}
			return;
		}
		Directory tmpDirectory = currentDirectory;
		for (String s : params) {
			tmpDirectory = (Directory)tmpDirectory.containsDirectory(s);
			if (tmpDirectory == null) {
				System.out.println("cd: "+vParam+": No such file or directory");
				return;
			}
		}
		currentDirectory = tmpDirectory;
		currentPath.append(vParam+"/");
	}
	public void cat(String param) {
		String vParam = param.substring(0, 
				param.indexOf(' ') == -1 ? param.length() : param.charAt(-1));
		if (vParam.length() == 0) {
			return;
		}		
		Document doc = (Document) currentDirectory.containsFile(vParam);
		if (doc == null) {
			System.out.println("cat: "+vParam+": No such file or directory");
			return;
		}
		System.out.println(doc.open());
	}
	public void vi(String param) {
		if (!touch(param)) return;
		String vParam = param.substring(0, 
				param.indexOf(' ') == -1 ? param.length() : param.charAt(-1));
		if (vParam.length() == 0) { 
			return;
		}
		FileType tmp = currentDirectory.containsFile(vParam); 
		if (tmp != null) {
			new Notepad((Document)tmp);
		}
	}
	public void rm(String param) {
		String[] params = param.split(" ");
		if (params.length != 1 && params.length != 2) {
			System.out.println("usage: rm [-r] file");
			return;
		}
		if (params.length == 2 && params[0].equals("-r")) {
			FileType file = currentDirectory.containsDirectory(params[1]);
			if (file != null) {
				file.remove();
				currentDirectory.getDirectoryEntries().remove(file);
			} else {
				System.out.println("rm: " + params[1] + ": No such file or directory");
			}
			return;
		}
		if (params.length == 1) {
			FileType document = (FileType) currentDirectory.containsFile(params[0]);
			if (currentDirectory.containsDirectory(params[0]) != null) {
				System.out.println("rm: "+ params[0] + ": is a directory");
				return;
			}
			if (document == null) {
				System.out.println("rm: " + params[0] + ": No such file or directory");
				return;
			}
			document.remove();
			currentDirectory.getDirectoryEntries().remove(document);
		}
	}
	public void df() {
		System.out.println("Blocks in use: \t" + Disk.getDefaultDisk().spaceUsed());
		System.out.println("Blocks available: \t" + (Disk.DEFAULT_SPACE/512 - Disk.getDefaultDisk().spaceUsed()));
		System.out.println("Space available: \t" +  (Disk.DEFAULT_SPACE - 512*Disk.getDefaultDisk().spaceUsed()));
	}
	public void cp() {
		
	}


	public FileSystem() {
		init();
		//currentDirectory.addDirectoryEntries(new Document("aa"));
		//currentDirectory.addDirectoryEntries(new Directory("bb", null));
		System.out.println();
		String cmd = new String();
		Scanner scanner = new Scanner(System.in);
		while (true) {
			prompt();
			cmd = scanner.nextLine();
			cmd = cmd.trim();
			String prefix = cmd.substring(0, 
					(cmd.indexOf(' ')==-1?cmd.length():(cmd.indexOf(' '))));
			String subfix = cmd.substring((cmd.indexOf(' ')==-1?cmd.length():(cmd.indexOf(' '))),
					cmd.length());
			subfix = subfix.trim();

			if (prefix.equals("exit")) {
				break;
			} else if (prefix.equals("pwd")) {
				System.out.println(pwd());
			} else if (prefix.equals("mkfs")) {
				mkfs();
			} else if (prefix.equals("ls")) {
				ls();
			} else if (prefix.equals("touch")) {
				touch(subfix);
			} else if (prefix.equals("mkdir")) {
				mkdir(subfix);
			} else if (prefix.equals("cd")) {
				cd(subfix);
			} else if (prefix.equals("cat")) {
				cat(subfix);
			} else if (prefix.equals("vi")) {
				vi(subfix);
			} else if (prefix.equals("rm")) {
				rm(subfix);
			} else if (prefix.equals("df")) {
				df();
			}
		}
		Disk.getDefaultDisk().writeHD();
	}
	private void init() {
		Disk.init();
		currentDirectory = Disk.getDefaultDisk().getRootDirectory();
	}

	public static void main(String[] args) {
		new FileSystem();
	}
}
