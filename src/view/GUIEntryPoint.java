/**
 * 
 */
package view;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;


//import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;


import view.pokedex.PokedexScreen;
import view.pokemetrics.PokeCardPanel;
import view.pokeparty.PokePartyPanel;
import view.pokevolve.PokevolvePanel;

import data.DataFetch;

/**
 * Main JFrame application container
 * @author James Ford
 *
 */
@SuppressWarnings("serial")
public class GUIEntryPoint extends JFrame implements PokeListener, ActionListener {

	private static String user;
	private static String pass;

	public static final String TITLE = "PokeMonitor";
	public static final String POKEHOME = "PokeHome";
	public static final String POKEDEX = "Pokedex";
	public static final String POKEMETRICS = "Pokemetrics";
	public static final String POKEVOLVE = "Pokevolve";
	public static final String POKEPARTY = "Pokeparty";
	public static final String POKEHELP = "PokeHelp";
	private final DataFetch df;
	private JTabbedPane jtp;

	private PokeSearchPanel psp;
	private PokedexScreen ps;
	private PokePartyPanel ppp;
	private PokeCardPanel pcp;
	private PokevolvePanel pep;
	

	public GUIEntryPoint(String title) throws SQLException {
		super(title);
		this.df = DataFetch.getInstance();
		this.df.setListener(this);
		this.df.connectToRIT(user, pass);
		user = null;
		pass = null;
		initComponents();
		fillComponents();
	}

	private void initComponents() {
		jtp = new JTabbedPane();
		pcp = new PokeCardPanel();
		pcp.pgp.metricsBtn.addActionListener(this);
		pcp.pmp.thruBtn.addActionListener(this);
	}


	public static void showError(String msg, String title) {
		JOptionPane.showMessageDialog(null, msg, title,
				JOptionPane.ERROR_MESSAGE, null);
	}

	private void fillComponents() {
		jtp.addTab(POKEHOME, psp = new PokeSearchPanel(this));
		jtp.addTab(POKEDEX, ps = new PokedexScreen(this));
		jtp.addTab(POKEMETRICS, pcp);
		jtp.addTab(POKEVOLVE, pep = new PokevolvePanel());
		jtp.addTab(POKEPARTY, ppp = new PokePartyPanel());
		jtp.addTab(POKEHELP, new JPanel());
		jtp.setMnemonicAt(0, KeyEvent.VK_1);
		jtp.setMnemonicAt(1, KeyEvent.VK_2);
		jtp.setMnemonicAt(2, KeyEvent.VK_3);
		jtp.setMnemonicAt(3, KeyEvent.VK_4);
		jtp.setMnemonicAt(4, KeyEvent.VK_5);
		jtp.setMnemonicAt(5, KeyEvent.VK_6);
		this.add(jtp, BorderLayout.CENTER);
	}



	public void act(String command, String argument) {
		switch(command) {
		case POKEHOME:
			jtp.setSelectedIndex(0);
			break;
		case POKEDEX:
			jtp.setSelectedIndex(1);
			break;
		case POKEMETRICS:
			pcp.pmp.updatePokemetrics(argument);
			pcp.pmp.updatePokemoves(argument);
			jtp.setSelectedIndex(2);
			break;
		case POKEVOLVE:
			pep.updatePokevolve(argument, 0);
			jtp.setSelectedIndex(3);
			break;
		case POKEPARTY:
			jtp.setSelectedIndex(4);
			break;
		}
	}
	
	protected static void createAndShowGUI() {
		JFrame f = null;
		try {
			f = new GUIEntryPoint(TITLE);
		} catch (SQLException e) {
			showError(e.getMessage(), "SQLException");
		}


		// Setup JFrame deets.
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.setPreferredSize(new Dimension(940,600));
		f.pack(); // Pack before setting location (this determines size)

		// Get the current screen's size
		Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
		// Compute and set the location so the frame is centered
		int x = screen.width/2-f.getSize().width/2;
		int y = screen.height/2-f.getSize().height/2;
		f.setLocation(x, y);
		f.setVisible(true);
	}

	public static void main(String[] args) throws InvocationTargetException, InterruptedException {
		if (!System.getProperty("java.version").startsWith("1.7")) {
			showError("Please install Java version 7", "Error");
			return;
		}

		// Set the Nimbus look and feel because it's new and cool looking
		try {
			for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
				if ("Nimbus".equals(info.getName())) {
					UIManager.setLookAndFeel(info.getClassName());
					break;
				}
			}
		} catch (Exception e) {
			// Will be set to default LAF
		}

		if(args.length < 2) {
			showError("Usage: args[0] = username | args[1] = password", "Error");
			return;
		}
		user = args[0];
		pass = args[1];

		Runnable doCreateAndShowGUI = new Runnable() {

			@Override
			public void run() {
				createAndShowGUI();
			}
		};
		SwingUtilities.invokeAndWait(doCreateAndShowGUI);
	}


	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == pcp.pgp.metricsBtn){
			((CardLayout)(pcp.getLayout())).show(pcp, PokeCardPanel.METRICS_CARD);
			if(!pcp.pgp.name.getText().equals("")){
				pcp.pmp.updatePokemetrics(pcp.pgp.name.getText());
				pcp.pmp.updatePokemoves(pcp.pgp.name.getText());
			}
		}else if(e.getSource() == pcp.pmp.thruBtn){
			((CardLayout)(pcp.getLayout())).show(pcp, PokeCardPanel.GEN_CARD);
			if(!pcp.pmp.name.getText().equals("")){
				pcp.pgp.updatePokeGenerations(pcp.pmp.name.getText());
			}
		}
		
	}


}
