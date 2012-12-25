package demo.Zhihao;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.SwingConstants;

class FileIcon extends JLabel implements MouseListener{
	private FileSystemGUI rootGUI = null;
	private FileType _item = null;
	public FileIcon(FileType item, FileSystemGUI root) {
		rootGUI = root;
		_item = item;
		this.setIcon(item.getIcon());
		this.setText(item.getName());
		this.setVerticalTextPosition(SwingConstants.BOTTOM);
		this.setHorizontalTextPosition(SwingConstants.CENTER);
		this.setHorizontalAlignment(SwingConstants.CENTER);
		this.setIconTextGap(-4);
		this.addMouseListener(this);
	}
	@Override
	public void mouseClicked(MouseEvent e) {
		PropertyPanel.getDefaultPanel().clear();
		PropertyPanel.getDefaultPanel().show(_item);
		if (e.isMetaDown()) {
			selected();
			System.out.println("right click");
			JPopupMenu popupMenu = new JPopupMenu();
			JMenuItem openItem = new JMenuItem("Open");
			openItem.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					if (_item instanceof Document) 
						new Notepad((Document)_item);
					else {
						rootGUI.setCurrentPath((Directory)_item);
					}
				}
			});
			JMenuItem deleteItem = new JMenuItem("Delete");
			deleteItem.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					//TODO
					System.out.println("delete");
					if (_item instanceof Document) {
						rootGUI.getDelegate().rm(_item.getName());
					} else {
						rootGUI.getDelegate().rm("-r "+_item.getName());
					}
					removeSelf();
				}
			});
			popupMenu.add(openItem);
			popupMenu.addSeparator();
			popupMenu.add(deleteItem);
			popupMenu.show(this, e.getX(), e.getY());
			return;
		}
		if (e.getClickCount() == 1) {
			selected();
		}
		else if (e.getClickCount() == 2) {
			System.out.println("file double click");
			PropertyPanel.getDefaultPanel().clear();
			if (_item instanceof Document) 
				new Notepad((Document)_item);
			else {
				rootGUI.setCurrentPath((Directory)_item);
			}
		}
	}
	@Override
	public void mousePressed(MouseEvent e) {}
	@Override
	public void mouseReleased(MouseEvent e) {}
	@Override
	public void mouseEntered(MouseEvent e) {}
	@Override
	public void mouseExited(MouseEvent e) {}
	public void selected() {
		rootGUI.releaseLastSelectedIcon();
		Color color = new Color(100, 172, 255);
		this.setBackground(color);
		this.setOpaque(true);
		this.setForeground(Color.WHITE);
		rootGUI.setLastSelectedIcon(this);
	}
	public void released() {
		this.setOpaque(false);
		this.setForeground(Color.BLACK);
		System.out.println("release");
	}
	
	private void removeSelf() {
		rootGUI.removeIcon(this);
	}
}
