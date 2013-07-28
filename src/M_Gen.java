import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

public class M_Gen extends JFrame {
	
	public M_Gen() {
		initUI();
	}
	
	public void initUI() {
		JPanel panel = new JPanel();									// Create the panel
		getContentPane().add(panel);
		panel.setLayout(null);
		
		JMenuBar menubar = new JMenuBar();								// Create the menu bar
		JMenu menuFile = new JMenu("File");
		JMenu menuFileNew = new JMenu("New");
		JMenu menuEdit = new JMenu("Edit");
		JMenu menuView = new JMenu("View");
		
		JMenuItem menuFileOpen = new JMenuItem("Open");						// Create the menu items
		JMenuItem menuFileSave = new JMenuItem("Save");
		JMenuItem menuFileClose = new JMenuItem("Close");
		menuFileClose.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_W, ActionEvent.CTRL_MASK));
		menuFileClose.setMnemonic(KeyEvent.VK_C);
		menuFileClose.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				System.exit(0);
			}
		});
		
		menuFile.add(menuFileNew);										// Add menu items to menu tabs
		menuFile.add(menuFileOpen);
		menuFile.add(menuFileSave);
		menuFile.add(menuFileClose);
		
		menubar.add(menuFile);											// Add menu tabs to menu bar
		menubar.add(menuEdit);
		menubar.add(menuView);
		
		setJMenuBar(menubar);												// Add menu bar to panel
		
		setTitle("Music Generator");
		setSize(600, 500);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(EXIT_ON_CLOSE);		
	}
	
	public static void main(String[] args) {
		M_Gen m = new M_Gen();
		m.setVisible(true);
	}
}