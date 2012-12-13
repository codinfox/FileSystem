package demo.Zhihao;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;

public class Notepad extends JFrame implements KeyListener {
	private Document _document = null;
	private JTextArea textArea = new JTextArea();
	private boolean edited = false;

	public Notepad(Document document) {
		super(document.getName() + " - Notepad");
		_document = document;
		this.setVisible(true);
		this.setMinimumSize(new Dimension(500, 400));
		this.setSize(500, 400);
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				int needSaveOrNot = -1;
				if (edited)
					needSaveOrNot = JOptionPane.showConfirmDialog(Notepad.this,
							"Document changed, save or not?", "Save", 0);
				if (needSaveOrNot == JOptionPane.YES_OPTION) {
					// TODO save
					System.out.println("saved");
				}
			}
		});

		JMenuBar menuBar = new JMenuBar();
		JMenu fileMenu = new JMenu("File");
		JMenuItem saveItem = new JMenuItem("Save");
		JMenuItem exitItem = new JMenuItem("Exit");
		fileMenu.add(saveItem);
		fileMenu.addSeparator();
		fileMenu.add(exitItem);
		menuBar.add(fileMenu);
		JScrollPane scrollPane = new JScrollPane(textArea);
		scrollPane
				.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		this.add(menuBar, BorderLayout.NORTH);
		this.add(scrollPane, BorderLayout.CENTER);
		textArea.addKeyListener(this);
	}

	public static void main(String[] args) {
		new Notepad(new Document("hello"));
	}

	@Override
	public void keyTyped(KeyEvent e) {
		edited = true;
		System.out.println("changed");
	}

	@Override
	public void keyPressed(KeyEvent e) {
	}

	@Override
	public void keyReleased(KeyEvent e) {
	}
}
