package geogebra.gui.dialog.options;

import geogebra.common.gui.SetLabels;
import geogebra.gui.GuiManagerD;
import geogebra.gui.util.IconTabbedPane;
import geogebra.gui.view.spreadsheet.SpreadsheetView;
import geogebra.main.AppD;
import geogebra.main.GeoGebraPreferencesD;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;

/**
 * A central dialog with all important options.
 */
public class OptionsDialog extends JDialog implements WindowListener, SetLabels {
	/** */
	private static final long serialVersionUID = 1L;

	/**
	 * Index of the defaults tab.
	 */
	public static final int TAB_DEFAULTS = 0;

	/**
	 * Index of the euclidian tab.
	 */
	public static final int TAB_EUCLIDIAN = 1;

	/**
	 * Index of the spreadsheet tab.
	 */
	public static final int TAB_SPREADSHEET = 2;

	/**
	 * Index of the CAS tab.
	 */
	public static final int TAB_CAS = 3;

	/**
	 * Index of the advanced tab.
	 */
	public static final int TAB_ADVANCED = 4;

	/**
	 * Index of the euclidian 3D tab.
	 */
	public static final int TAB_EUCLIDIAN3D = 5;

	/**
	 * An instance of the Application object of this window.
	 */
	protected AppD app;

	/**
	 * The tabbed pane which is used to switch between the different pages of
	 * the options menu.
	 */
	protected JTabbedPane tabbedPane;

	/**
	 * The panel where the user can select new default values for certain
	 * objects.
	 */
	private OptionsDefaultsD defaultsPanel;

	/**
	 * The panel with all settings for the euclidian view. The
	 * "Drawing Pad Properties" dialog is not longer used, all settings are
	 * stored here for now.
	 */
	private OptionsEuclidianD euclidianPanel;

	private JScrollPane euclidianPanelScroll;

	/**
	 * The panel with all settings for the spreadsheet view.
	 */
	private OptionsSpreadsheetD spreadsheetPanel;

	/**
	 * The panel with all settings for the CAS view.
	 */
	private OptionsCASD casPanel;

	/**
	 * The panel with general options.
	 */
	private OptionsAdvancedD advancedPanel;

	/**
	 * The button to apply settings without closing the window.
	 */
	private JButton saveButton;

	/**
	 * The button which closes the window and stores all changes.
	 */
	private JButton closeButton;

	/**
	 * Button to restore the preferences.
	 */
	private JButton restoreDefaultsButton;

	/**
	 * Initialize the GUI and logics.
	 * 
	 * @param app
	 */
	public OptionsDialog(AppD app) {
		super(app.getFrame(), false);

		this.app = app;

		setResizable(true);
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

		addWindowListener(this);

		initGUI();
		updateGUI();
	}

	public JTabbedPane getTabbedPane(){
		return tabbedPane;
	}
	
	
	/**
	 * Update the GUI.
	 */
	public void updateGUI() {
		defaultsPanel.updateGUI();
		euclidianPanel.updateGUI();
		spreadsheetPanel.updateGUI();
		casPanel.updateGUI();
		advancedPanel.updateGUI();
	}

	/**
	 * Restores default settings in option dialogs
	 */
	public void restoreDefaults() {
		defaultsPanel.restoreDefaults();
		advancedPanel.updateAfterReset();

		// TODO
		// --- add calls to other panels here

		updateGUI();
	}

	/**
	 * Select the tab which shows the euclidian view settings.
	 * 
	 * @param index
	 *            Index of the tab to hide, use the constants defined in this
	 *            class for that
	 */
	public void showTab(int index) {
		if (index == TAB_EUCLIDIAN) {
			euclidianPanel.setView(app.getActiveEuclidianView());
		}
		tabbedPane.setSelectedIndex(index);
	}

	/**
	 * Initialize the GUI.
	 */
	protected void initGUI() {
		setLayout(new BorderLayout());

		// init tabs
		createTabs();

		// init scroll panes for tabs (show no extra borders)
		euclidianPanelScroll = new JScrollPane(euclidianPanel.getWrappedPanel());
		euclidianPanelScroll.setBorder(BorderFactory.createEmptyBorder());

		// init tabbing pane
		tabbedPane = new IconTabbedPane();

		// defaults
		tabbedPane.addTab("", app.getImageIcon("options-large.png"),
				defaultsPanel.getWrappedPanel());

		addTabs();

		// disable some tabs for applets
		if (app.isApplet()) {
			tabbedPane.setEnabledAt(TAB_DEFAULTS, false);
		}

		add(tabbedPane, BorderLayout.CENTER);

		// panel with buttons at the bottom
		JPanel buttonPanel = new JPanel(new BorderLayout());
		buttonPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0,
				SystemColor.controlDkShadow));
		buttonPanel.setBackground(Color.white);

		// (restore defaults on the left side)
		JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		panel.setBackground(Color.white);

		if (!app.isApplet()) {
			restoreDefaultsButton = new JButton();
			restoreDefaultsButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					GeoGebraPreferencesD.getPref().clearPreferences();

					// reset defaults for GUI, views etc
					// this has to be called before load XML preferences,
					// in order to avoid overwrite
					app.getSettings().resetSettings();

					// for geoelement defaults, this will do nothing, so it is OK here
					GeoGebraPreferencesD.getPref().loadXMLPreferences(app);

					// reset default line thickness etc
					app.getKernel().getConstruction().getConstructionDefaults().resetDefaults();

					// reset defaults for geoelements; this will create brand new objects
					// so the options defaults dialog should be reset later
					app.getKernel().getConstruction().getConstructionDefaults().createDefaultGeoElementsFromScratch();

					// reset the stylebar defaultGeo
					if (app.getEuclidianView1().hasStyleBar())
						app.getEuclidianView1().getStyleBar().restoreDefaultGeo();
					if (app.hasEuclidianView2EitherShowingOrNot())
						if (app.getEuclidianView2().hasStyleBar())
							app.getEuclidianView2().getStyleBar().restoreDefaultGeo();

					// restore dialog panels to display these defaults
					restoreDefaults();

				}
			});

			panel.add(restoreDefaultsButton);
		}

		buttonPanel.add(panel, BorderLayout.WEST);

		// (save and close on the right side)
		panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		panel.setBackground(Color.white);

		if (!app.isApplet()) {
			saveButton = new JButton();
			saveButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					GeoGebraPreferencesD.getPref().saveXMLPreferences(app);
				}
			});
			panel.add(saveButton);
		}

		closeButton = new JButton();
		closeButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				closeDialog();
			}
		});
		panel.add(closeButton);
		buttonPanel.add(panel, BorderLayout.EAST);

		add(buttonPanel, BorderLayout.SOUTH);

		setLabels(); // update all labels

		setPreferredSize(new Dimension(640, 480));
		pack();

		setLocationRelativeTo(null);
	}

	protected void createTabs() {
		defaultsPanel = new OptionsDefaultsD(app);
		euclidianPanel = new OptionsEuclidianD(app, app.getActiveEuclidianView());
		spreadsheetPanel = new OptionsSpreadsheetD(app, (SpreadsheetView) app.getGuiManager()
				.getSpreadsheetView());
		casPanel = new OptionsCASD(app);
		advancedPanel = new OptionsAdvancedD(app);
	}

	protected void addTabs() {

		// euclidian properties
		tabbedPane.addTab("", app.getImageIcon("euclidian.png"),
				euclidianPanelScroll);

		// spreadsheet properties
		tabbedPane.addTab("", app.getImageIcon("spreadsheet.png"),
				spreadsheetPanel.getWrappedPanel());

		// CAS properties
		tabbedPane.addTab("", app.getImageIcon("cas.png"), casPanel.getWrappedPanel());

		// advanced
		tabbedPane.addTab("", app.getImageIcon("options-advanced.png"),
				advancedPanel.getWrappedPanel());

	}

	/**
	 * Update the labels of the components (e.g. if the language changed).
	 */
	public void setLabels() {
		setTitle(app.getMenu("Settings"));

		closeButton.setText(app.getMenu("Close"));

		if (!app.isApplet()) {
			saveButton.setText(app.getMenu("Settings.Save"));
			restoreDefaultsButton.setText(app.getMenu("Settings.ResetDefault"));
		}

		tabbedPane.setTitleAt(TAB_DEFAULTS, app.getPlain("Defaults"));
		tabbedPane.setTitleAt(TAB_EUCLIDIAN, app.getPlain("DrawingPad"));
		tabbedPane.setTitleAt(TAB_SPREADSHEET, app.getPlain("Spreadsheet"));
		tabbedPane.setTitleAt(TAB_CAS, app.getMenu("CAS"));
		tabbedPane.setTitleAt(TAB_ADVANCED, app.getMenu("Advanced"));

		GuiManagerD.setLabelsRecursive(this);
	}

	/**
	 * Close the dialog.
	 */
	private void closeDialog() {
		setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		app.storeUndoInfo();
		setCursor(Cursor.getDefaultCursor());
		setVisible(false);
	}

	/**
	 * Simulate the pressing of the close button if the window is closed.
	 */
	public void windowClosing(WindowEvent e) {
		closeButton.doClick();
	}

	public void windowActivated(WindowEvent e) {
	}

	public void windowClosed(WindowEvent e) {
	}

	public void windowDeactivated(WindowEvent e) {
	}

	public void windowDeiconified(WindowEvent e) {
	}

	public void windowIconified(WindowEvent e) {
	}

	public void windowOpened(WindowEvent e) {
	}

	public static class Factory {
		public OptionsDialog create(AppD app) {
			return new OptionsDialog(app);
		}
	}
}
