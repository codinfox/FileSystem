package demo.Zhihao;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.LinkedList;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.text.BadLocationException;

public class Terminal extends JFrame implements KeyListener {
	private static final long serialVersionUID = 1L;
	private FileSystem delegate = null;
	private JTextArea textArea = new JTextArea();
	private LinkedList<String> historyCommand = new LinkedList<>();
	private static Terminal terminal = null;
	private int historyCommandPointer = 0;

	public static Terminal getTerminal() {
		if (terminal == null)
			terminal = new Terminal();
		return terminal;
	}

	public void setDelegate(FileSystem delegate) {
		this.delegate = delegate;
	}

	public Terminal() {
		super("Terminal");
		JMenu menu = new JMenu("About");
		JLabel label = new JLabel(" File System");
		JMenuBar menuBar = new JMenuBar();
		JMenuItem item = new JMenuItem("About");
		item.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				JOptionPane
						.showMessageDialog(
								null,
								"Just to show this is not a terminal, this is an emulator.\n" +
								"\t\t(C) Li Zhihao",
								"About File System",
								JOptionPane.INFORMATION_MESSAGE);
			}
		});
		menuBar.add(label);
		menuBar.add(menu);
		menu.add(item);
		this.add(menuBar, BorderLayout.NORTH);

		this.setSize(800, 500);
		this.setMinimumSize(new Dimension(800, 500));
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		JScrollPane scrollPane = new JScrollPane(textArea);
		scrollPane
				.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scrollPane
				.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		this.add(scrollPane, BorderLayout.CENTER);
		this.setVisible(true);
		// textArea.append(fs.prompt_pub());
		textArea.setLineWrap(false);
		textArea.setCaretPosition(textArea.getText().length());
		textArea.addKeyListener(this);
		textArea.addCaretListener(new CaretListener() {
			@Override
			public void caretUpdate(CaretEvent e) {
				try {
					int pos = textArea.getCaretPosition();
					int row = textArea.getLineOfOffset(pos) + 1;
					int col = pos - textArea.getLineStartOffset(row - 1) + 1;
					if (row != textArea.getLineCount())
						textArea.setCaretPosition(textArea.getText().length());
					if (textArea.getText().charAt(
							textArea.getCaretPosition() - 1) == '$') {
						textArea.setCaretPosition(textArea.getText().length());
					}
					System.out.println("current pos " + row + " row , " + col
							+ " col ");
				} catch (IllegalArgumentException ee) {
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		});
	}

	@Override
	public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
			if (textArea.getText().charAt(textArea.getCaretPosition() - 2) == '$') {
				textArea.setCaretPosition(textArea.getText().length());
				if (textArea.getCaretPosition() == textArea.getText().length())
					textArea.append(" ");
			}
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_ENTER) {
			getCommand();
			System.out.println("enter");
		} else if (e.getKeyCode() == KeyEvent.VK_TAB) {
			handleTab();
			System.out.println("tab");
		} else if (e.getKeyCode() == KeyEvent.VK_UP) {
			int begin = textArea.getText().lastIndexOf('$') + 2;
			int end = textArea.getText().length();
			if (historyCommandPointer >= 0 && !historyCommand.isEmpty())
				try {
					historyCommandPointer = (historyCommandPointer - 1 < 0) ? 0
							: (historyCommandPointer - 1);
					textArea.getDocument().remove(begin, end - begin);
					textArea.append(historyCommand.get(historyCommandPointer));
				} catch (BadLocationException e1) {
					e1.printStackTrace();
				}
		} else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
			int begin = textArea.getText().lastIndexOf('$') + 2;
			int end = textArea.getText().length();
			if (historyCommandPointer < historyCommand.size())
				try {
					historyCommandPointer = historyCommandPointer + 1 >= historyCommand
							.size() ? historyCommand.size() - 1
							: historyCommandPointer + 1;
					textArea.getDocument().remove(begin, end - begin);
					textArea.append(historyCommand.get(historyCommandPointer));
				} catch (BadLocationException e1) {
					e1.printStackTrace();
				}
		}
	}

	@Override
	public void keyTyped(KeyEvent e) {
	}

	public void println(String str) {
		textArea.append(str + '\n');
		textArea.setCaretPosition(textArea.getText().length());
		// textArea.append(fs.prompt_pub());
	}

	public void print(String str) {
		textArea.append(str);
		textArea.setCaretPosition(textArea.getText().length());
	}

	public void getCommand() {
		int end = textArea.getText().length();
		int begin = textArea.getText().lastIndexOf('$');
		String cmd = textArea.getText().substring(begin + 2, end - 1);
		if (cmd == null) {
			textArea.append(delegate.prompt_pub());
			return;
		}
		historyCommand.add(cmd);
		historyCommandPointer = historyCommand.size();
		if (delegate.command(cmd)) {
			textArea.setEditable(false);
			textArea.append("logout\n\n[process completed]");
			System.gc();
			textArea.removeKeyListener(this);
		}
	}

	public void handleTab() { // TODO
		int end = textArea.getText().length();
		int begin = textArea.getText().lastIndexOf('$');
		String cmd = textArea.getText().substring(begin + 2, end - 1);
		if (cmd == null) {
			return;
		}

		int begin2 = textArea.getText().lastIndexOf('$') + 2;
		int end2 = textArea.getText().length();
		try {
			if (delegate == null) {
				textArea.getDocument().remove(begin2, end2 - begin2);
				return;
			}
			String tmp = delegate.fillOut(cmd);
			if (tmp != null) {
				textArea.getDocument().remove(begin2, end2 - begin2);
				textArea.append(tmp);
			} else {
				textArea.getDocument().remove(textArea.getText().length() - 1,
						1);
			}
		} catch (BadLocationException e) {
			e.printStackTrace();
		}

	}

	public static void main(String[] args) {
		getTerminal();
		getTerminal().setEnabled(false);
		FileSystem fs = new FileSystem(1);
		getTerminal().setDelegate(fs);
		getTerminal().setEnabled(true);
	}
}
