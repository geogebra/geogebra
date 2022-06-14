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
import java.util.Scanner;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
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
import javax.swing.SwingUtilities;

import org.geogebra.common.GeoGebraConstants;
import org.geogebra.common.main.App;
import org.geogebra.common.move.events.BaseEvent;
import org.geogebra.common.move.ggtapi.TubeAvailabilityCheckEvent;
import org.geogebra.common.move.views.EventRenderable;
import org.geogebra.common.util.Charsets;
import org.geogebra.common.util.debug.Log;
import org.geogebra.desktop.export.PrintPreviewD;
import org.geogebra.desktop.gui.GuiManagerD;
import org.geogebra.desktop.gui.layout.DockManagerD;
import org.geogebra.desktop.gui.layout.LayoutD;
import org.geogebra.desktop.main.AppD;
import org.geogebra.desktop.main.GeoGebraPreferencesD;
import org.geogebra.desktop.main.LocalizationD;

public class GeoGebraMenuBar extends JMenuBar implements EventRenderable {
	private static final long serialVersionUID = 1736020764918189176L;

	private BaseMenu fileMenu, editMenu, optionsMenu, toolsMenu, windowMenu,
			helpMenu;

	ViewMenuApplicationD viewMenu;

	private final AppD app;
	private LayoutD layout;

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
		setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createMatteBorder(0, 0, 1, 0,
						SystemColor.control),
				BorderFactory.createEmptyBorder(1, 1, 0, 1)));

		this.app = app;
	}

	/**
	 * Tells if the 3D View is shown in the current window
	 * 
	 * @return whether 3D View is switched on
	 */
	public boolean is3DViewShown() {
		return viewMenu.is3DViewShown();
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

		add(windowMenu);

		// "Help"
		helpMenu = new HelpMenuD(app);
		add(helpMenu);

		// support for right-to-left languages
		app.setComponentOrientation(this);
	}

	/**
	 * Display the result of login events
	 */
	@Override
	public void renderEvent(final BaseEvent event) {
		SwingUtilities.invokeLater(() -> doRenderEvent(event));

	}

	protected void doRenderEvent(BaseEvent event) {
		if (event instanceof TubeAvailabilityCheckEvent) {
			TubeAvailabilityCheckEvent checkEvent = (TubeAvailabilityCheckEvent) event;
			onTubeAvailable(checkEvent.isAvailable());
		}
	}

	private void onTubeAvailable(boolean available) {
		if (available) {
			app.showPopUps();
		}
	}

	/**
	 * Update the menubar.
	 */
	public void updateMenubar() {
		Log.debug("update menu");
		fileMenu.update();
		editMenu.update();
		viewMenu.update();
		optionsMenu.update();
		toolsMenu.update();

		windowMenu.update();

		helpMenu.update();
	}

	/**
	 * Checkbox of Construction protocol view will be checked in view menu if
	 * visible is true. Otherwise won't be checked.
	 * 
	 * @param visible
	 */
	public void updateCPView(boolean visible) {
		viewMenu.updateCPView(visible);
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
	}

	/**
	 * @param m
	 * @param font
	 */
	public static void setMenuFontRecursive(JMenuItem m, Font font) {
		if (m instanceof JMenu) {
			JPopupMenu pm = ((JMenu) m).getPopupMenu();

			MenuElement[] components = pm.getSubElements();

			for (MenuElement com : components) {
				if (com instanceof LanguageRadioButtonMenuItem) {
					// do nothing
				} else if (com instanceof JComponent) {
					((JComponent) com).setFont(font);
				}
				if (com instanceof JMenuItem) {
					setMenuFontRecursive((JMenuItem) com, font);
				}
			}
		}

		if (m instanceof LanguageRadioButtonMenuItem) {
			m.setFont(m.getFont().deriveFont(font.getSize2D()));
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
		Thread runner = new Thread() {
			@Override
			public void run() {

				try {
					app.setWaitCursor();
					GuiManagerD gui = (GuiManagerD) app.getGuiManager();
					DockManagerD dm = gui.getLayout().getDockManager();
					int viewId = (dm.getFocusedPanel() == null) ? -1
							: dm.getFocusedPanel().getViewId();
					PrintPreviewD pre = PrintPreviewD.get(app, viewId,
							PageFormat.LANDSCAPE);

					pre.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
					Log.debug("Print preview not available");
				} finally {
					app.setDefaultCursor();
				}

			}
		};
		runner.start();

	}

	private static String glVersion = null;
	private static String glCard = null;

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

		sb.append(app.getHeapSize() / 1024 / 1024);
		sb.append("MB, ");
		sb.append(App.getCASVersionString());

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

		// copy system information to clipboard

		systemInfoPanel.add(new JButton(
				new AbstractAction(loc.getMenu("SystemInformation")) {

					private static final long serialVersionUID = 1L;

					@Override
					public void actionPerformed(ActionEvent arg0) {

						copyDebugInfoToClipboard(app);

						app.showMessage(
								loc.getMenu("SystemInformationMessage"));
					}
				}), loc.borderEast());

		JPanel panel = new JPanel(new BorderLayout(5, 5));
		panel.add(systemInfoPanel, BorderLayout.NORTH);
		panel.add(scrollPane, BorderLayout.SOUTH);

		JOptionPane infoPane = new JOptionPane(panel, JOptionPane.PLAIN_MESSAGE,
				JOptionPane.DEFAULT_OPTION);

		final JDialog dialog = infoPane.createDialog(app.getMainComponent(),
				loc.getMenu("AboutLicense"));

		dialog.setVisible(true);
	}

	private static void appendVersion(StringBuilder sb, App app) {
		sb.append(GeoGebraConstants.APPLICATION_NAME);
		sb.append(" Classic ");
		sb.append(app.getVersionString());
	}

	public static void copyDebugInfoToClipboard(AppD app) {
		StringBuilder sb = new StringBuilder();
		sb.append("[pre]");
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
		if (glCard != null) {
			sb.append("\nGraphics Card: ").append(glCard);
		}
		if (glVersion != null) {
			sb.append("\nGL Version: " + glVersion);
		}
		sb.append("\n\n");

		if (Log.getLogger() != null) {
			// copy the entire log to systemInfo (maybe not required at all)
			sb.append("GeoGebraLogger log:\n");
			sb.append(Log.getEntireLog());
			sb.append("\n");
		}

		// copy file log
		if (app.logFile != null) {
			sb.append("File log from " + app.logFile.toString() + ":\n");
			String NL = System.getProperty("line.separator");
			Scanner scanner = null;
			try {
				scanner = new Scanner(new File(app.logFile.toString()),
						Charsets.UTF_8);
				while (scanner.hasNextLine()) {
					sb.append(scanner.nextLine() + NL);
				}
			} catch (FileNotFoundException e) {
				app.showMessage(
						app.getLocalization().getMenu("CannotOpenLogFile"));
			} finally {
				if (scanner != null) {
					scanner.close();
				}
			}
			sb.append("\n");
		}

		// append ggb file (except images)
		sb.append("GGB file content:\n");
		sb.append(app.getXML());
		sb.append("\n\n");
		sb.append(app.getMacroXML());
		sb.append("\n\nLibraryJavaScript:\n");
		sb.append(app.getKernel().getLibraryJavaScript());

		sb.append("\n\nPreferences:\n");
		sb.append(GeoGebraPreferencesD.getPref().getXMLPreferences());
		sb.append("[/pre]");
		Toolkit.getDefaultToolkit().getSystemClipboard()
				.setContents(new StringSelection(sb.toString()), null);

	}

	public static void setGlCard(String s) {
		glCard = s;
	}

	public static void setGlVersion(String s) {
		glVersion = s;
	}
}
