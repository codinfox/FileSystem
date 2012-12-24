package demo.Zhihao;

import java.util.ArrayList;

public class FileSystem {

	private StringBuffer currentPath = new StringBuffer("/");
	private Directory currentDirectory = null;
	public String pwd() {
		return currentPath.toString();
	}
	private void prompt() {
		Terminal.getTerminal().print("FileSys:~ user$ ".replaceFirst("~", pwd())); 
	}
	public String prompt_pub() {
		return "FileSys:~ user$ ".replaceFirst("~", pwd());
	}
	public void mkfs() {
		Disk.getDefaultDisk().format();
		currentDirectory = Disk.getDefaultDisk().getRootDirectory();
		currentPath = new StringBuffer("/");
		Terminal.getTerminal().println("Success");
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

			Terminal.getTerminal().println(content + "\t" +
					size + "\t" +
					file.getCreateTime() + "\t" +
					file.getModifiedTime() + "\t" +
					filename); // contents of a directory
		}
	}
	public boolean touch(String param) {
		String vParam = param.substring(0, 
				param.indexOf(' ') == -1 ? param.length() : param.indexOf(' '));
		if (vParam.length() == 0) {
			Terminal.getTerminal().println("touch: Wrong file name");
			return false;
		}
		if (currentDirectory.containsDirectory(vParam) != null) {
			Terminal.getTerminal().println("touch: "+vParam+": is already taken");
			return false;
		}
		currentDirectory.modify();
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
				param.indexOf(' ') == -1 ? param.length() : param.indexOf(' '));
		if (vParam.length() == 0) {
			Terminal.getTerminal().println("mkdir: Wrong file name");
			return;
		}
		if (currentDirectory.containsFile(vParam) != null) {
			Terminal.getTerminal().println("mkdir: "+vParam+": is already taken");
			return;
		}
		FileType tmp = currentDirectory.containsDirectory(vParam); 
		if (tmp != null)
			Terminal.getTerminal().println("mkdir: " + vParam + ": File exists");
		else
			currentDirectory.addDirectoryEntries(new Directory(vParam, currentDirectory));
	}
	public void cd(String param) {
		String vParam = param.substring(0, 
				param.indexOf(' ') == -1 ? param.length() : param.indexOf(' '));
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
				Terminal.getTerminal().println("cd: "+vParam+": No such file or directory");
				return;
			}
		}
		currentDirectory = tmpDirectory;
		currentPath.append(vParam+"/");
	}
	public void cat(String param) {
		String vParam = param.substring(0, 
				param.indexOf(' ') == -1 ? param.length() : param.indexOf(' '));
		if (vParam.length() == 0) {
			return;
		}		
		Document doc = (Document) currentDirectory.containsFile(vParam);
		if (doc == null) {
			Terminal.getTerminal().println("cat: "+vParam+": No such file or directory");
			return;
		}
		Terminal.getTerminal().println(doc.open());
	}
	public void vi(String param) {
		if (!touch(param)) return;
		String vParam = param.substring(0, 
				param.indexOf(' ') == -1 ? param.length() : param.indexOf(' '));
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
			Terminal.getTerminal().println("usage: rm [-r] file");
			return;
		}
		if (params.length == 2 && params[0].equals("-r")) {
			FileType file = currentDirectory.containsDirectory(params[1]);
			if (file != null) {
				file.remove();
				currentDirectory.getDirectoryEntries().remove(file);
			} else {
				Terminal.getTerminal().println("rm: " + params[1] + ": No such file or directory");
			}
			return;
		}
		if (params.length == 1) {
			FileType document = (FileType) currentDirectory.containsFile(params[0]);
			if (currentDirectory.containsDirectory(params[0]) != null) {
				Terminal.getTerminal().println("rm: "+ params[0] + ": is a directory");
				return;
			}
			if (document == null) {
				Terminal.getTerminal().println("rm: " + params[0] + ": No such file or directory");
				return;
			}
			document.remove();
			currentDirectory.getDirectoryEntries().remove(document);
			currentDirectory.modify();
		}
	}
	public void df() {
		Terminal.getTerminal().println("Blocks in use: \t\t" + Disk.getDefaultDisk().spaceUsed());
		Terminal.getTerminal().println("Blocks available: \t" + (Disk.DEFAULT_SPACE/512 - Disk.getDefaultDisk().spaceUsed()));
		Terminal.getTerminal().println("Space available: \t" +  (Disk.DEFAULT_SPACE - 512*Disk.getDefaultDisk().spaceUsed()));
	}
	
	public FileSystem(int i){
		init();
		Terminal.getTerminal().println("");
		prompt();
	}

//	public FileSystem() {
//		init();
//		//currentDirectory.addDirectoryEntries(new Document("aa"));
//		//currentDirectory.addDirectoryEntries(new Directory("bb", null));
//		Terminal.getTerminal().println("");
//		String cmd = new String();
//		Scanner scanner = new Scanner(System.in);
//		while (true) {
//			prompt();
//			cmd = scanner.nextLine();
//			cmd = cmd.trim();
//			String prefix = cmd.substring(0, 
//					(cmd.indexOf(' ')==-1?cmd.length():(cmd.indexOf(' '))));
//			String subfix = cmd.substring((cmd.indexOf(' ')==-1?cmd.length():(cmd.indexOf(' '))),
//					cmd.length());
//			subfix = subfix.trim();
//
//			if (prefix.equals("exit")) {
//				break;
//			} else if (prefix.equals("pwd")) {
//				Terminal.getTerminal().println(pwd());
//			} else if (prefix.equals("mkfs")) {
//				mkfs();
//			} else if (prefix.equals("ls")) {
//				ls();
//			} else if (prefix.equals("touch")) {
//				touch(subfix);
//			} else if (prefix.equals("mkdir")) {
//				mkdir(subfix);
//			} else if (prefix.equals("cd")) {
//				cd(subfix);
//			} else if (prefix.equals("cat")) {
//				cat(subfix);
//			} else if (prefix.equals("vi")) {
//				vi(subfix);
//			} else if (prefix.equals("rm")) {
//				rm(subfix);
//			} else if (prefix.equals("df")) {
//				df();
//			}
//		}
//		Disk.getDefaultDisk().writeHD();
//	}

	public Directory getCurrentDirectory() {
		return currentDirectory;
	}

	public boolean command(String cmd) {
		cmd = cmd.trim();
		String prefix = cmd.substring(0, 
				(cmd.indexOf(' ')==-1?cmd.length():(cmd.indexOf(' '))));
		String subfix = cmd.substring((cmd.indexOf(' ')==-1?cmd.length():(cmd.indexOf(' '))),
				cmd.length());
		subfix = subfix.trim();

		if (prefix.equals("pwd")) {
			Terminal.getTerminal().println(pwd());
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
		} else if (prefix.equals("about")) {
			Terminal.getTerminal().println("This is not a real terminal. By Li Zhihao");
		} else if (prefix.equals("exit")) {
			Disk.getDefaultDisk().writeHD();
			return true;
		}
		prompt();
		return false;
	}

	public String fillOut(String cmd) {
		cmd = cmd.trim();
		System.out.println(cmd);
		String prefix = cmd.substring(0, 
				(cmd.indexOf(' ')==-1?cmd.length():(cmd.indexOf(' '))));
		String subfix = cmd.substring((cmd.indexOf(' ')==-1?cmd.length():(cmd.indexOf(' '))),
				cmd.length());
		subfix = subfix.trim();

		if (subfix.length() == 0) {
			ArrayList<String> tmp = new ArrayList<>();
			if (prefix.length() == 0) {
				Terminal.getTerminal().println("");
				Terminal.getTerminal().println("Display all 12 posibilities");
				Terminal.getTerminal().println("cat\tcd\tdf\nexit\tls\tmkdir\nmkfs\tpwd\trm\ntouch\tvi\tabout");
				prompt();
			} else {
				if ("pwd".startsWith(prefix))
					tmp.add("pwd");
				if ("mkfs".startsWith(prefix))
					tmp.add("mkfs");
				if ("ls".startsWith(prefix))
					tmp.add("ls");
				if ("touch".startsWith(prefix))
					tmp.add("touch");
				if ("mkdir".startsWith(prefix))
					tmp.add("mkdir");
				if ("cd".startsWith(prefix))
					tmp.add("cd");
				if ("cat".startsWith(prefix))
					tmp.add("cat");
				if ("vi".startsWith(prefix))
					tmp.add("vi");
				if ("rm".startsWith(prefix))
					tmp.add("rm");
				if ("df".startsWith(prefix))
					tmp.add("df");
				if ("exit".startsWith(prefix))
					tmp.add("exit");
				if ("about".startsWith(prefix))
					tmp.add("about");
			}
			if (tmp.size() == 1)
				cmd = cmd.replaceFirst(prefix, tmp.get(0));
			else if (tmp.size() > 1) {
				Terminal.getTerminal().println("");
				for (String s : tmp) {
					Terminal.getTerminal().print(s + "\t");
				}
				Terminal.getTerminal().println("");
				prompt();
			}
			return cmd;
		} else {
			String[] str = subfix.split(" ");
			String trailing = str[str.length-1];
			ArrayList<String> tmp = new ArrayList<>();
			for (FileType file : currentDirectory.getDirectoryEntries()) {
				if (file.getName().startsWith(trailing)) {
					if (file instanceof Directory) 
						tmp.add(file.getName()+"/");
					else 
						tmp.add(file.getName());
				}
			}
			if (tmp.size() == 1) {
				int last = cmd.lastIndexOf(' ');
				String s = cmd.substring(0, last);
				cmd = String.format("%s %s", s, tmp.get(0));
				return cmd;
			} else if (tmp.size() == 0) {
				return null;
			} else {
				Terminal.getTerminal().println("");
				for (String s : tmp) {
					Terminal.getTerminal().print(s + "\t");
				}
				Terminal.getTerminal().println("");
				prompt();
				return cmd;
			}
		}
	}

	private void init() {
		Disk.init();
		currentDirectory = Disk.getDefaultDisk().getRootDirectory();
	}
}
