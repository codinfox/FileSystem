package demo.Zhihao;

import java.util.Scanner;


public class tst {
	public static void main(String[] args) {
		Scanner scanner = new Scanner(System.in);
		String dd = new String("dd");
		StringBuffer str = new StringBuffer("asdf/dd/");
		str.delete(str.length()-dd.length()-1, str.length());
		System.out.println(str);
	}
}
