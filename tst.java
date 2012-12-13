package demo.Zhihao;

import java.util.BitSet;

public class tst {
	public static void main(String[] args) {
		Disk disk = Disk.getDefaultDisk();
		Document document = new Document("hello");
		if (document.save("hallo")) System.out.println("aa");
		System.out.println(document.open());
	}
}
