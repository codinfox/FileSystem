package demo.Zhihao;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;

import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;

class ContentPanel extends JPanel implements MouseListener{
	private FileSystemGUI rootGUI = null;
	public ContentPanel(FileSystemGUI root, Directory dir) {
		rootGUI = root;
		this.setLayout(new FlowLayout(FlowLayout.LEFT));
		this.setBackground(Color.WHITE);
		this.addMouseListener(this);
		
		ArrayList<FileType> contents = dir.getDirectoryEntries();
		for (FileType file : contents) {
			this.add(new FileIcon(file, rootGUI));
		}
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		PropertyPanel.getDefaultPanel().clear();
		rootGUI.releaseLastSelectedIcon();
		System.out.println("content click");
		if (e.isMetaDown()) {
			System.out.println("right click");
			JPopupMenu popupMenu = new JPopupMenu();
			JMenuItem newDocument = new JMenuItem("New Document");
			JMenuItem newDirectory = new JMenuItem("New Directory");
			newDirectory.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					String filename = JOptionPane.showInputDialog("Directory name(only support a-zA-Z_0-9)", "NoTitle");
					boolean valid = filename.matches("[a-zA-Z_0-9]+");
					if (!valid) {
						JOptionPane.showMessageDialog(rootGUI, "Only Support [a-zA-Z_0-9]+.", "Name Error", JOptionPane.ERROR_MESSAGE);
						return;
					}
					if (!rootGUI.getDelegate().mkdir_gui(filename)) {
						JOptionPane.showMessageDialog(rootGUI, "File name already taken.", "Duplicate Error", JOptionPane.ERROR_MESSAGE);
						return;
					}
					rootGUI.updates(rootGUI.getDelegate().getCurrentDirectory());
				}
			});
			newDocument.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					String filename = JOptionPane.showInputDialog("Document name(only support a-zA-Z_0-9)", "NoTitle");
					boolean valid = filename.matches("[a-zA-Z_0-9]+");
					if (!valid) {
						JOptionPane.showMessageDialog(rootGUI, "Only Support [a-zA-Z_0-9]+.", "Name Error", JOptionPane.ERROR_MESSAGE);
						return;
					}
					if (!rootGUI.getDelegate().touch_gui(filename)) {
						JOptionPane.showMessageDialog(rootGUI, "File name already taken.", "Duplicate Error", JOptionPane.ERROR_MESSAGE);
						return;
					}
					rootGUI.updates(rootGUI.getDelegate().getCurrentDirectory());
				}
			});
			popupMenu.add(newDocument);
			popupMenu.add(newDirectory);
			popupMenu.show(this, e.getX(), e.getY());
			return;
		}
	}

	@Override
	public void mousePressed(MouseEvent e) {
	}

	@Override
	public void mouseReleased(MouseEvent e) {		
	}

	@Override
	public void mouseEntered(MouseEvent e) {		
	}

	@Override
	public void mouseExited(MouseEvent e) {		
	}
}
