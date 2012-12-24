package demo.Zhihao;

import java.util.Scanner;

import javax.swing.JFrame;
import javax.swing.JTextArea;
import javax.swing.text.BadLocationException;


public class tst {
	public static void main(String[] args) {
		String cmd = new String("cddd a");
		cmd = cmd.trim();
		String prefix = cmd.substring(0, 
				(cmd.indexOf(' ')==-1?cmd.length():(cmd.indexOf(' '))));
		String subfix = cmd.substring((cmd.indexOf(' ')==-1?cmd.length():(cmd.indexOf(' '))),
				cmd.length());
		subfix = subfix.trim();

		cmd = cmd.replaceFirst("cddd", "eee");
		
		System.out.println(cmd);
	}
}
