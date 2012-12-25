package demo.Zhihao;

import java.awt.Font;
import java.awt.GridLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;


class PropertyPanel extends JPanel {
	private static PropertyPanel defaultPanel = new PropertyPanel();
	private final static Font titleFont = new Font("Arial", Font.BOLD, 13);
	
	public static PropertyPanel getDefaultPanel() {
		return defaultPanel;
	}
	
	private PropertyPanel() {
		this.setLayout(null);
	}
	
	public void show(FileType file) {
		if (file instanceof Document) {
			JLabel icon = new JLabel(file.getIcon());
			JLabel filename = new JLabel("Name: " + file.getName());
			JLabel size = new JLabel("Size: " + ((Document)file).size());
			JLabel createTimeLabel = new JLabel("Create Time:");
			JLabel createTime = new JLabel(file.getCreateTime());
			JLabel modifTimeLabel = new JLabel("Modified Time:");
			JLabel modifTime = new JLabel(file.getModifiedTime());
			icon.setBounds(36, 0, 128, 128);
			filename.setBounds(20, 128, 180, 14);
			filename.setFont(titleFont);
			size.setBounds(20, 148, 180, 14);
			size.setFont(titleFont);
			createTimeLabel.setBounds(20, 168, 180, 14);
			createTimeLabel.setFont(titleFont);
			createTime.setBounds(25, 188, 180, 14);
			modifTimeLabel.setBounds(20, 208, 180, 14);
			modifTimeLabel.setFont(titleFont);
			modifTime.setBounds(25, 228, 180, 14);
			this.add(filename);
			this.add(icon);
			this.add(size);
			this.add(modifTime);
			this.add(modifTimeLabel);
			this.add(createTime);
			this.add(createTimeLabel);
			
			System.out.println("show");
		} else if (file instanceof Directory) {
			JLabel icon = new JLabel(file.getIcon());
			JLabel filename = new JLabel("Name: " + file.getName());
			JLabel size = new JLabel("Contents: " + ((Directory)file).numberOfObjects());
			JLabel createTimeLabel = new JLabel("Create Time:");
			JLabel createTime = new JLabel(file.getCreateTime());
			JLabel modifTimeLabel = new JLabel("Modified Time:");
			JLabel modifTime = new JLabel(file.getModifiedTime());
			icon.setBounds(36, 0, 128, 128);
			filename.setBounds(20, 128, 180, 14);
			filename.setFont(titleFont);
			size.setBounds(20, 148, 180, 14);
			size.setFont(titleFont);
			createTimeLabel.setBounds(20, 168, 180, 14);
			createTimeLabel.setFont(titleFont);
			createTime.setBounds(25, 188, 180, 14);
			modifTimeLabel.setBounds(20, 208, 180, 14);
			modifTimeLabel.setFont(titleFont);
			modifTime.setBounds(25, 228, 180, 14);
			this.add(filename);
			this.add(icon);
			this.add(size);
			this.add(modifTime);
			this.add(modifTimeLabel);
			this.add(createTime);
			this.add(createTimeLabel);
			
			System.out.println("show");
		}

		this.updateUI();
	}
	
	public void clear() {
		this.removeAll();
		this.updateUI();
	}
}
