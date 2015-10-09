package org.geogebra.desktop.gui.menubar;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.SystemColor;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.print.PageFormat;
import java.io.File;
import java.io.FileNotFoundException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Scanner;

import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.MenuElement;
import javax.swing.ScrollPaneConstants;

import org.geogebra.common.GeoGebraConstants;
import org.geogebra.common.main.App;
import org.geogebra.common.main.Localization;
import org.geogebra.common.move.events.BaseEvent;
import org.geogebra.common.move.ggtapi.TubeAvailabilityCheckEvent;
import org.geogebra.common.move.ggtapi.events.LogOutEvent;
import org.geogebra.common.move.ggtapi.events.LoginAttemptEvent;
import org.geogebra.common.move.ggtapi.events.LoginEvent;
import org.geogebra.common.move.ggtapi.models.GeoGebraTubeUser;
import org.geogebra.common.move.ggtapi.operations.LogInOperation;
import org.geogebra.common.move.views.EventRenderable;
import org.geogebra.common.plugin.SensorLogger;
import org.geogebra.common.util.debug.Log;
import org.geogebra.desktop.gui.GuiManagerD;
import org.geogebra.desktop.gui.dialog.DialogManagerD;
import org.geogebra.desktop.gui.layout.DockManagerD;
import org.geogebra.desktop.gui.layout.LayoutD;
import org.geogebra.desktop.main.AppD;
import org.geogebra.desktop.main.GeoGebraPreferencesD;
import org.geogebra.desktop.main.LocalizationD;
import org.geogebra.desktop.move.ggtapi.models.LoginOperationD;

public class GeoGebraMenuBar extends JMenuBar implements EventRenderable {
	private static final long serialVersionUID = 1736020764918189176L;

	private BaseMenu fileMenu, editMenu, viewMenu, optionsMenu, toolsMenu,
			windowMenu, helpMenu, languageMenu;

	private final AppD app;
	private LayoutD layout;
	private AbstractButton signInButton;

	private AbstractAction signInAction, signInInProgressAction, signOutAction;

	/**
	 * Creates new menubar
	 * 
	 * @param app
	 *            Application
	 * @param layout
	 *            Layout
	 */
	public GeoGebraMenuBar(AppD app, LayoutD layout) {
		this.layout = layout;

		/**
		 * A nasty workaround to prevent any borders from being drawn. All other
		 * elements will have a border at the top to prevent visual conflicts
		 * while moving the toolbar / algebra input to the top / bottom. The
		 * JMenuBar *always* draws a border at the bottom however, even if the
		 * border set via setBorder() is empty. By drawing an one pixel border
		 * with the color of the background we can prevent this.
		 */
		setBorder(BorderFactory.createCompoundBorder(BorderFactory
				.createMatteBorder(0, 0, 1, 0, SystemColor.control),
				BorderFactory.createEmptyBorder(1, 1, 0, 1)));

		this.app = app;
	}

	/**
	 * Tells if the 3D View is shown in the current window
	 * 
	 * @return whether 3D View is switched on
	 */
	public boolean is3DViewShown() {
		return ((ViewMenuD) viewMenu).is3DViewShown();
	}

	/**
	 * Initialize the menubar. No update is required after initialization.
	 */
	public void initMenubar() {
		removeAll();

		// "File"
		fileMenu = new FileMenuD(app);
		add(fileMenu);

		// "Edit"
		editMenu = new EditMenuD(app);
		add(editMenu);

		// "View"
		// #3711 viewMenu = app.isApplet()? new ViewMenu(app, layout) : new
		// ViewMenuApplicationD(app, layout);
		viewMenu = new ViewMenuApplicationD(app, layout);
		add(viewMenu);

		// "Perspectives"
		// if(!app.isApplet()) {
		// perspectivesMenu = new PerspectivesMenu(app, layout);
		// add(perspectivesMenu);
		// }

		// "Options"
		optionsMenu = new OptionsMenuD(app);
		add(optionsMenu);

		// "Tools"
		toolsMenu = new ToolsMenuD(app);
		add(toolsMenu);

		// "Window"
		windowMenu = new WindowMenuD(app);

		if (!app.isApplet()) // just add the menu if this is not an applet we're
		{
			add(windowMenu);
		}

		// "Help"
		helpMenu = new HelpMenuD(app);
		add(helpMenu);

		// applets might be running in Java 6 (no JavaFX)
		// and not wanted for applets anyway
		if (!app.isApplet()) {
			// Add the Sign in button (force it to the far right)

			boolean javaFx22Available = false;
			try {
				this.getClass().getClassLoader()
						.loadClass("javafx.embed.swing.JFXPanel");
				javaFx22Available = true;
			} catch (ClassNotFoundException e) {
				App.error("JavaFX 2.2 not available");
			}

			// JavaFX 2.2 available by default only on Java 7u6 or higher
			// http://www.oracle.com/us/corporate/press/1735645
			if (javaFx22Available) {

				// try needed for eg OSX 10.6 with fake jfxrt.jar
				try {
					add(Box.createHorizontalGlue());
					addSignIn();
				} catch (Exception e) {
					App.error("problem starting JavaFX");
				}
			}

		}

		// "flag" to select language
		// addFlag();

		// support for right-to-left languages
		app.setComponentOrientation(this);

	}

	/**
	 * Creates and adds the sign in button
	 */
	private void addSignIn() {
		signInAction = new AbstractAction(app.getMenu("SignIn")) {

			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				app.getGuiManager().login();
			}
		};
		signInInProgressAction = new AbstractAction(
				app.getMenu("SignInInProgress")) {

			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				// do nothing
			}
		};
		signOutAction = new AbstractAction(app.getMenu("SignOut")) {

			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				app.getGuiManager().logout();
			}
		};

		if (app.isMacOS()) {
			signInButton = new JMenuItem(signInAction);
			JMenu m = new BaseMenu(app, app.getMenu("GeoGebraTube")) {

				@Override
				public void update() {
					// TODO Auto-generated method stub

				}

				@Override
				protected void initActions() {
					// TODO Auto-generated method stub

				}

				@Override
				protected void initItems() {
					// TODO Auto-generated method stub
					add(signInButton);

				}
				// );
			};
			// m.add(signInButton);
			add(m);
		} else {
			signInButton = new JButton(signInAction);
			add(signInButton);
		}
		signInButton.setContentAreaFilled(false);
		signInButton.setFocusPainted(false);
		Localization loc = app.getLocalization();
		signInButton.setToolTipText(loc.getMenuTooltip("SignIn.Help"));

		// Add the menu bar as a listener for login/logout operations
		LogInOperation signIn = app.getLoginOperation();
		signIn.getView().add(this);
		if (signIn.isLoggedIn()) {
			onLogin(true, signIn.getModel().getLoggedInUser(), true);
		} else if (!((LoginOperationD) signIn).isTubeAvailable()) {
			signInButton.setVisible(false);
		}

	}

	/**
	 * Display the result of login events
	 */
	public void renderEvent(BaseEvent event) {

		if (event instanceof LoginAttemptEvent) {
			signInButton.setAction(signInInProgressAction);
			signInButton.setVisible(true);
		} else if (event instanceof LogOutEvent) {
			signInButton.setAction(signInAction);
			signInButton.setVisible(true);
		} else if (event instanceof LoginEvent) {
			LoginEvent loginEvent = (LoginEvent) event;
			onLogin(loginEvent.isSuccessful(), loginEvent.getUser(),
					loginEvent.isAutomatic());
			signInButton.setVisible(true);
		} else if (event instanceof TubeAvailabilityCheckEvent) {
			TubeAvailabilityCheckEvent checkEvent = (TubeAvailabilityCheckEvent) event;
			onTubeAvailable(checkEvent.isAvailable());
		}
	}

	private void onTubeAvailable(boolean available) {
		if (available) {
			signInButton.setVisible(true);
			app.showPopUps();
		}
	}

	private void onLogin(boolean successful, GeoGebraTubeUser user,
			boolean automatic) {

		Localization loc = app.getLocalization();

		if (successful) {

			// Show the username in the menu
			signInButton.setAction(signOutAction);
			String username = user.getUserName();
			if (app.isMacOS()) {
				username = app.getNormalizer().transform(username);
			}
			signInButton.setText(loc.getPlain("SignedInAsA", username));

			// Show a login success message
			if (!automatic) {
				Object[] options = {
						app.getMenu("OpenFromGeoGebraTube") + "...",
						app.getPlain("OK") };
				int n = JOptionPane.showOptionDialog(app.getMainComponent(),
						app.getPlain("ThanksForSigningIn"),
						app.getPlain("SignInSuccessful"),
						JOptionPane.DEFAULT_OPTION,
						JOptionPane.INFORMATION_MESSAGE, null, options,
						options[0]);
				if (n == 0) {
					((DialogManagerD) app.getDialogManager())
							.showOpenFromGGTDialog();
				} else {
					// open perspective popup after logging in
					if (app.isShowDockBar()) {
						app.getDockBar().showPopup();
					}
				}
			}
		} else {
			signInButton.setAction(signInAction);
			signInButton.setText(loc.getMenu("SignInError"));
		}
	}

	/**
	 * Update the menubar.
	 */
	public void updateMenubar() {
		App.debug("update menu");
		fileMenu.update();
		editMenu.update();
		viewMenu.update();
		optionsMenu.update();
		toolsMenu.update();
		// if (perspectivesMenu != null)
		// perspectivesMenu.update();

		if (!app.isApplet())
			windowMenu.update();

		helpMenu.update();

		// updateSelection(); //it's redundant here, look at editMenu.update();
	}

	/**
	 * Checkbox of Construction protocol view will be checked in view menu if
	 * visible is true. Otherwise won't be checked.
	 * 
	 * @param visible
	 */
	public void updateCPView(boolean visible) {
		if (viewMenu instanceof ViewMenuApplicationD) {
			((ViewMenuApplicationD) viewMenu).updateCPView(visible);
		}
	}

	/**
	 * Update the selection.
	 */
	public void updateSelection() {
		((EditMenuD) editMenu).updateSelection();
	}

	/**
	 * Update the file menu without being forced to updated the other menus as
	 * well.
	 */
	public void updateMenuFile() {
		if (fileMenu != null) {
			fileMenu.update();
		}
	}

	/**
	 * Update the window menu without having to update the other menus as well.
	 */
	public void updateMenuWindow() {
		windowMenu.update();
	}

	/**
	 * Update the menu fonts.
	 */
	public void updateFonts() {
		for (int i = 0; i < this.getMenuCount(); i++) {
			JMenu m;
			if ((m = getMenu(i)) != null) {

				// old method
				// problem with keyboard shortcuts
				// setMenuFontRecursive(m, app.getPlainFont());

				// force rebuild next time menu is opened
				// see BaseMenu.menuSelected()
				m.removeAll();

				// update title (always visible)
				m.setFont(app.getPlainFont());
			}
		}

		// Update the font of the sign in button
		if (signInButton != null && signInButton instanceof JButton) {
			signInButton.setFont(app.getPlainFont());
		}
	}

	/**
	 * @param m
	 * @param font
	 */
	public static void setMenuFontRecursive(JMenuItem m, Font font) {
		// App.debug(m.getClass());
		if (m instanceof JMenu) {
			JPopupMenu pm = ((JMenu) m).getPopupMenu();

			MenuElement[] components = pm.getSubElements();

			// App.debug(components.length);

			for (MenuElement com : components) {
				// System.out.println(m.getText());
				if (com instanceof LanguageRadioButtonMenuItem) {
					// do nothing
				} else if (com instanceof JComponent) {
					((JComponent) com).setFont(font);
				}
				if (com instanceof JMenuItem) {
					setMenuFontRecursive((JMenuItem) com, font);
				}

				// App.debug(com.getClass());
			}
		}

		if (m instanceof LanguageRadioButtonMenuItem) {
			((LanguageRadioButtonMenuItem) m).getFont().deriveFont(
					font.getSize2D());
		} else {
			m.setFont(font);
		}
	}

	/**
	 * Show the print preview dialog.
	 * 
	 * @param app
	 */
	public static void showPrintPreview(final AppD app) {
		try {
			Thread runner = new Thread() {
				@Override
				public void run() {

					try {
						app.setWaitCursor();
						// use reflection for
						// new geogebra.export.PrintPreview(app,
						// app.getEuclidianView(), PageFormat.LANDSCAPE);
						// Class classObject =
						// Class.forName("geogebra.export.PrintPreview");
						// Object[] args = new Object[] { app ,
						// app.getEuclidianView(), new
						// Integer(PageFormat.LANDSCAPE)};
						// Class [] types = new Class[] {Application.class,
						// Printable.class, int.class};
						// Constructor constructor =
						// classObject.getDeclaredConstructor(types);
						// constructor.newInstance(args);
						/*
						 * old code boolean printCAS=false; if
						 * (((GuiManagerD)app.getGuiManager()).hasCasView()){
						 * DockManager
						 * dm=((GuiManagerD)app.getGuiManager()).getLayout
						 * ().getDockManager(); //if CAS-view has Focus, print
						 * CAS if
						 * (dm.getFocusedPanel()==dm.getPanel(Application.
						 * VIEW_CAS)){ new geogebra.export.PrintPreview(app,
						 * ((GuiManagerD)app.getGuiManager()).getCasView(),
						 * PageFormat.LANDSCAPE); printCAS=true; } }
						 * 
						 * if (!printCAS) new geogebra.export.PrintPreview(app,
						 * app .getEuclidianView(), PageFormat.LANDSCAPE);
						 */
						GuiManagerD gui = (GuiManagerD) app.getGuiManager();
						DockManagerD dm = gui.getLayout().getDockManager();
						int viewId = (dm.getFocusedPanel() == null) ? -1 : dm.getFocusedPanel().getViewId();
						org.geogebra.desktop.export.PrintPreview pre = org.geogebra.desktop.export.PrintPreview
								.get(app, viewId,
										PageFormat.LANDSCAPE);

						pre.setVisible(true);
					} catch (Exception e) {
						e.printStackTrace();
						App.debug("Print preview not available");
					} finally {
						app.setDefaultCursor();
					}

				}
			};
			runner.start();
		} catch (java.lang.NoClassDefFoundError ee) {
			app.showError(app.getLocalization().getError("ExportJarMissing"));
			ee.printStackTrace();
		}
	}

	/**
	 * Show the "About" dialog.
	 * 
	 * @param app
	 */
	public static void showAboutDialog(final AppD app) {

		final LocalizationD loc = app.getLocalization();
		StringBuilder sb = new StringBuilder();
		sb.append("<html><b>");
		appendVersion(sb, app);
		sb.append("</b>  (");
		sb.append("Java ");
		AppD.appendJavaVersion(sb);
		sb.append(", ");

		// needed by the user for logging
		if (!app.isApplet()) {
			sb.append("IP: ");
			try {
				sb.append(InetAddress.getLocalHost().getHostAddress());
				sb.append(':');
				sb.append(SensorLogger.port);
			} catch (UnknownHostException e) {
				sb.append("<unknown>");
			}
			sb.append(", ");
		}

		sb.append(app.getHeapSize() / 1024 / 1024);
		sb.append("MB, ");
		sb.append(App.getCASVersionString());

		String v;
		if (App.singularWS != null
				&& (v = App.singularWS.getSingularVersionString()) != null)
			sb.append(",<br>" + v);
		sb.append(")<br>");

		sb.append(GeoGebraConstants.BUILD_DATE);

		// license
		String text = app.loadTextFile(AppD.LICENSE_FILE);
		// We may want to modify the window size when the license file changes:
		JTextArea textArea = new JTextArea(26, 72); // window size fine tuning
													// (rows, cols)
		JScrollPane scrollPane = new JScrollPane(textArea,
				ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		textArea.setEditable(false);
		// not sure if Monospaced is installed everywhere:
		textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
		textArea.setText(text);
		textArea.setCaretPosition(0);

		JPanel systemInfoPanel = new JPanel(new BorderLayout(5, 5));
		systemInfoPanel.add(new JLabel(sb.toString()), BorderLayout.CENTER);

		if (AppD.hasFullPermissions()) {
			// copy system information to clipboard

			systemInfoPanel.add(
					new JButton(new AbstractAction(loc
							.getPlain("SystemInformation")) {

						private static final long serialVersionUID = 1L;

						public void actionPerformed(ActionEvent arg0) {

							copyDebugInfoToClipboard(app);

							app.showMessage(loc
									.getPlain("SystemInformationMessage"));
						}
					}), loc.borderEast());

		}

		JPanel panel = new JPanel(new BorderLayout(5, 5));
		panel.add(systemInfoPanel, BorderLayout.NORTH);
		panel.add(scrollPane, BorderLayout.SOUTH);

		JOptionPane infoPane = new JOptionPane(panel,
				JOptionPane.PLAIN_MESSAGE, JOptionPane.DEFAULT_OPTION);

		final JDialog dialog = infoPane.createDialog(app.getMainComponent(),
				loc.getMenu("AboutLicense"));

		dialog.setVisible(true);
	}

	private static void appendVersion(StringBuilder sb, AppD app) {
		sb.append(GeoGebraConstants.APPLICATION_NAME);
		sb.append(" ");
		sb.append(app.getVersionString());

		if (app.getApplet() != null) {
			sb.append(" Applet");
		}
	}

	public static void copyDebugInfoToClipboard(AppD app) {
		StringBuilder sb = new StringBuilder();
		sb.append("[code]");
		appendVersion(sb, app);
		sb.append(" (");
		sb.append(GeoGebraConstants.BUILD_DATE);
		sb.append(")\nJava: ");
		sb.append(System.getProperty("java.version"));
		sb.append("\nCodebase: ");
		sb.append(AppD.getCodeBase());
		sb.append("\nOS: ");
		sb.append(System.getProperty("os.name"));
		sb.append("\nArchitecture: ");
		sb.append(System.getProperty("os.arch")); // tells us 32 or 64 bit
													// (Java)
		sb.append(" / ");
		sb.append(System.getenv("PROCESSOR_ARCHITECTURE")); // tells us 32 or 64
															// bit (Java)
		sb.append("\nHeap: ");
		sb.append(app.getHeapSize() / 1024 / 1024);
		sb.append("MB\nCAS: ");
		sb.append(App.getCASVersionString());
		sb.append("\n\n");

		if (Log.logger != null) {
			// copy the entire log to systemInfo (maybe not required at all)
			sb.append("GeoGebraLogger log:\n");
			sb.append(Log.logger.getEntireLog());
			sb.append("\n");
		}

		// copy file log
		if (app.logFile != null) {
			sb.append("File log from " + app.logFile.toString() + ":\n");
			String NL = System.getProperty("line.separator");
			Scanner scanner = null;
			try {
				scanner = new Scanner(new File(app.logFile.toString()));
				while (scanner.hasNextLine()) {
					sb.append(scanner.nextLine() + NL);
				}
			} catch (FileNotFoundException e) {
				app.showMessage(app.getPlain("CannotOpenLogFile"));
			} finally {
				if (scanner != null)
					scanner.close();
			}
			sb.append("\n");
		}

		// append ggb file (except images)
		sb.append("GGB file content:\n");
		sb.append(app.getXML());
		sb.append("\n\n");
		sb.append(app.getMacroXML());
		sb.append("\n\nLibraryJavaScript:\n");
		app.getKernel().getLibraryJavaScript();

		sb.append("\n\nPreferences:\n");
		sb.append(GeoGebraPreferencesD.getPref().getXMLPreferences());
		sb.append("[/code]");
		Toolkit.getDefaultToolkit().getSystemClipboard()
				.setContents(new StringSelection(sb.toString()), null);

	}
}
