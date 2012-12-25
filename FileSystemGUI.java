package demo.Zhihao;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.DisplayMode;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;

class StatusBar extends JPanel {
	JLabel capicity = new JLabel();

	public StatusBar() {
		this.setLayout(new FlowLayout(FlowLayout.LEFT));
		setPreferredSize(new Dimension(800, 25));
		capicity.setText("Blocks Used: "
				+ Disk.getDefaultDisk().spaceUsed()
				+ " \t\tSpace Remain: "
				+ (Disk.DEFAULT_SPACE - 512 * Disk.getDefaultDisk().spaceUsed()));
		this.add(capicity);
	}

	public void updates() {
		capicity.setText("Blocks Used: "
				+ Disk.getDefaultDisk().spaceUsed()
				+ " \t\tSpace Remain: "
				+ (Disk.DEFAULT_SPACE - 512 * Disk.getDefaultDisk().spaceUsed()));
	}
}

class PathField extends JPanel implements ActionListener {
	private JTextField path = new JTextField("/");

	public PathField() {
		this.setLayout(new BorderLayout());
		setPreferredSize(new Dimension(800, 35));
		JButton button = new JButton("UP");
		JLabel label = new JLabel("Location");
		JPanel panel = new JPanel();
		path.setPreferredSize(new Dimension(655, 25));
		path.setEditable(false);
		panel.add(button);
		panel.add(label);
		this.add(panel, BorderLayout.WEST);
		this.add(path, BorderLayout.CENTER);
		button.addActionListener(this);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO
		System.out.println("parent");
		Directory dir = FileSystemGUI.getFileSystemGUI().getDelegate()
				.getCurrentDirectory().getParentDirectory();
		if (dir != null)
			FileSystemGUI.getFileSystemGUI().goParentDir(dir);
	}

	public void updates() {
		path.setText(FileSystemGUI.getFileSystemGUI().getDelegate().path());
	}
}

public class FileSystemGUI extends JFrame implements ActionListener {

	private static FileSystemGUI fileSystemGUI = new FileSystemGUI();
	private PropertyPanel propertyPanel = PropertyPanel.getDefaultPanel();
	private StatusBar statusBar = new StatusBar();
	private PathField pathField = new PathField();
	private JPanel content = new JPanel(new BorderLayout());
	private ContentPanel contentPanel = new ContentPanel(this, Disk
			.getDefaultDisk().getRootDirectory());

	private FileIcon lastSelectedIcon = null;
	private FileSystem delegate = null;

	public void setDelegate(FileSystem fs) {
		delegate = fs;
	}

	public FileSystem getDelegate() {
		return delegate;
	}

	public void setLastSelectedIcon(FileIcon icon) {
		lastSelectedIcon = icon;
	}

	public void releaseLastSelectedIcon() {
		if (lastSelectedIcon != null)
			lastSelectedIcon.released();
	}

	public static FileSystemGUI getFileSystemGUI() {
		return fileSystemGUI;
	}

	private FileSystemGUI() {
		// Super init ==============
		super("File System");

		// Menu setup ==============
		JMenuBar menuBar = new JMenuBar();
		JMenu fileMenu = new JMenu("File");
		JMenu aboutMenu = new JMenu("About");
		JMenuItem exitItem = new JMenuItem("Exit");
		JMenuItem helpItem = new JMenuItem("Help");
		JMenuItem aboutItem = new JMenuItem("About");
		exitItem.addActionListener(this);
		helpItem.addActionListener(this);
		aboutItem.addActionListener(this);
		fileMenu.add(exitItem);
		aboutMenu.add(helpItem);
		aboutMenu.addSeparator();
		aboutMenu.add(aboutItem);
		menuBar.add(fileMenu);
		menuBar.add(aboutMenu);
		this.add(menuBar, BorderLayout.NORTH);

		// CenterPanel setup
		JPanel centerPanel = new JPanel();
		centerPanel.setLayout(new BorderLayout());
		this.add(centerPanel, BorderLayout.CENTER);

		// InsideCenterPanel setup
		JPanel insideCenterPanel = new JPanel(new BorderLayout());
		centerPanel.add(insideCenterPanel, BorderLayout.CENTER);
		insideCenterPanel.setBorder(BorderFactory.createEtchedBorder());

		// Path setup ==============
		centerPanel.add(pathField, BorderLayout.NORTH);

		// ContentPanel setup
		insideCenterPanel.add(content, BorderLayout.CENTER);
		content.add(contentPanel, BorderLayout.CENTER);

		// Properties Panel setup ==
		propertyPanel.setPreferredSize(new Dimension(200, 600));
		insideCenterPanel.add(propertyPanel, BorderLayout.WEST);

		// Status Bar setup ========
		this.add(statusBar, BorderLayout.SOUTH);

		// Frame setup =============

		this.setVisible(false); 
		this.setMinimumSize(new Dimension(800, 600));
		this.setSize(800, 600);
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	}

	public void setCurrentPath(Directory dir) {
		delegate.cd(dir.getName());
		content.removeAll();
		content.add(new ContentPanel(this, dir));
		content.updateUI();
		pathField.updates();
		System.gc();
	}

	public void goParentDir(Directory dir) {
		delegate.cd("..");
		content.removeAll();
		content.add(contentPanel = new ContentPanel(this, dir));
		content.updateUI();
		pathField.updates();
		System.gc();
	}
	
	public void updates(Directory dir) {
		content.removeAll();
		content.add(contentPanel = new ContentPanel(this, dir));
		content.updateUI();
		pathField.updates();
		System.gc();
	}
	
	public void removeIcon(FileIcon icon) {
		contentPanel.remove(icon);
		contentPanel.updateUI();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals("Exit")) {
			this.dispose();
		} else if (e.getActionCommand().equals("About")) {
			JOptionPane.showMessageDialog(this, "This is the so called X Window. Too fishy maybe...\n" +
					"By Li Zhihao");
		} else if (e.getActionCommand().equals("Help")) {
			JOptionPane.showMessageDialog(this, "The window is based on the so called Terminal.\n" +
					"The close button can only lead to the disposal of this window.");
		}
	}
}
