package geogebra.gui.menubar;

import geogebra.common.GeoGebraConstants;
import geogebra.common.kernel.Kernel;
import geogebra.common.main.App;
import geogebra.export.ScalingPrintGridable;
import geogebra.gui.GuiManagerD;
import geogebra.gui.layout.DockManager;
import geogebra.gui.layout.LayoutD;
import geogebra.gui.view.Gridable;
import geogebra.main.AppD;
import geogebra.main.GeoGebraPreferencesD;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.SystemColor;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
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

public class GeoGebraMenuBar extends JMenuBar {
	private static final long serialVersionUID = 1736020764918189176L;

	private BaseMenu fileMenu, editMenu, viewMenu, optionsMenu, toolsMenu, windowMenu, helpMenu, languageMenu;

	private AppD app;
	private LayoutD layout;


	/**
	 * Creates new menubar
	 * @param app Application
	 * @param layout Layout
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
	 * Initialize the menubar. No update is required after initialization.
	 */
	public void initMenubar() {
		removeAll();

		// "File"
		fileMenu = new FileMenu(app);
		add(fileMenu);

		// "Edit"
		editMenu = new EditMenu(app);
		add(editMenu);

		// "View"
		viewMenu = new ViewMenu(app, layout);
		add(viewMenu);

		// "Perspectives"
		//if(!app.isApplet()) {
		//	perspectivesMenu = new PerspectivesMenu(app, layout);
		//	add(perspectivesMenu);
		//}

		// "Options"
		optionsMenu = new OptionsMenuD(app);
		add(optionsMenu);

		// "Tools"
		toolsMenu = new ToolsMenu(app);
		add(toolsMenu);

		// "Window"
		windowMenu = new WindowMenu(app);

		if(!app.isApplet()) // just add the menu if this is not an applet we're 
		{
			add(windowMenu);

			if (app.getPluginManager() != null) {
				javax.swing.JMenu pim = app.getPluginManager().getPluginMenu();
				if (pim != null) {
					add(pim);
				} // H-P Ulven 2008-04-17
			}
		}

		// "Help"
		helpMenu = new HelpMenu(app);
		add(helpMenu);


		// force next item to far right
		//add(Box.createHorizontalGlue());

		// "flag" to select language
		//addFlag();

		// support for right-to-left languages
		app.setComponentOrientation(this);


	}

	private void addFlag() {
		final String flagName = app.getFlagName(false);
		final JLabel languageLabel = new JLabel(app.getFlagIcon(flagName));
		languageLabel.setToolTipText(app.getMenuTooltip("Language"));
		languageLabel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				JPopupMenu myPopup = new JPopupMenu();
				OptionsMenuD.addLanguageMenuItems(app,  myPopup, new LanguageActionListener(app));
				myPopup.setVisible(true);
				myPopup.show(languageLabel, 0, languageLabel.getHeight());
			}
		});
		add(languageLabel);



		new Thread(
				new Runnable() {
					public void run() {

						String geoIPflagname = app.getFlagName(true);

						if (!geoIPflagname.equals(flagName)) {
							languageLabel.setIcon(app.getFlagIcon(flagName));
							App.debug("updating flag to "+geoIPflagname);
						}
					}
				}).start();		
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
		//if (perspectivesMenu != null)
		//	perspectivesMenu.update();

		if(!app.isApplet())
			windowMenu.update();

		helpMenu.update();

		//updateSelection();  //it's redundant here, look at editMenu.update();
	}

	/**
	 * Update the selection.
	 */
	public void updateSelection() {
		((EditMenu)editMenu).updateSelection();
	}

	/**
	 * Update the file menu without being forced to updated the other menus as well.
	 */
	public void updateMenuFile() {
		if (fileMenu!=null) {
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
		for(int i = 0; i < this.getMenuCount(); i++){
			JMenu m;
			if ((m = getMenu(i)) != null) {
				
				// old method
				// problem with keyboard shortcuts
				//setMenuFontRecursive(m, app.getPlainFont());
				
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
		//App.debug(m.getClass());
		if (m instanceof JMenu) {
			JPopupMenu pm = ((JMenu) m).getPopupMenu();

			MenuElement[] components = pm.getSubElements();
			
			//App.debug(components.length);

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
				
				//App.debug(com.getClass());
			}
		}
		
		if (m instanceof LanguageRadioButtonMenuItem) {
			((LanguageRadioButtonMenuItem)m).getFont().deriveFont(font.getSize2D());
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
						/* old code
						boolean printCAS=false;
						if (((GuiManagerD)app.getGuiManager()).hasCasView()){	
							DockManager dm=((GuiManagerD)app.getGuiManager()).getLayout().getDockManager();
							//if CAS-view has Focus, print CAS
							if (dm.getFocusedPanel()==dm.getPanel(Application.VIEW_CAS)){
								new geogebra.export.PrintPreview(app, ((GuiManagerD)app.getGuiManager()).getCasView(), PageFormat.LANDSCAPE);
								printCAS=true;
							}
						}			

						if (!printCAS)
							new geogebra.export.PrintPreview(app, app
								.getEuclidianView(), PageFormat.LANDSCAPE);


						 */					

						DockManager dm=((LayoutD) ((GuiManagerD)app.getGuiManager()).getLayout()).getDockManager();
						geogebra.export.PrintPreview pre;
						if (dm.getFocusedPanel()==dm.getPanel(App.VIEW_CAS))
							// TODO I think "new ScalingPrintGridable" here is not so nice. Maybe the constructor of PrintPreview should be changed
							pre = new geogebra.export.PrintPreview(app, new ScalingPrintGridable((Gridable) ((GuiManagerD)app.getGuiManager()).getCasView()), PageFormat.LANDSCAPE);
						else if (dm.getFocusedPanel()==dm.getPanel(App.VIEW_CONSTRUCTION_PROTOCOL))
							pre = new geogebra.export.PrintPreview(app, ((GuiManagerD)app.getGuiManager()).getConstructionProtocolView(), PageFormat.LANDSCAPE);
						else if (dm.getFocusedPanel()==dm.getPanel(App.VIEW_SPREADSHEET))
							pre = new geogebra.export.PrintPreview(app, ((GuiManagerD)app.getGuiManager()).getSpreadsheetView(), PageFormat.LANDSCAPE);
						else if (dm.getFocusedPanel()==dm.getPanel(App.VIEW_EUCLIDIAN2))
							pre = new geogebra.export.PrintPreview(app, app.getEuclidianView2(), PageFormat.LANDSCAPE);
						else if (dm.getFocusedPanel()==dm.getPanel(App.VIEW_ALGEBRA))
							pre = new geogebra.export.PrintPreview(app, ((GuiManagerD)app.getGuiManager()).getAlgebraView(), PageFormat.LANDSCAPE);
						else if (dm.getFocusedPanel()==dm.getPanel(App.VIEW_EUCLIDIAN))
							pre = new geogebra.export.PrintPreview(app, app.getEuclidianView1(), PageFormat.LANDSCAPE);
						else if (dm.getFocusedPanel()==dm.getPanel(App.VIEW_DATA_ANALYSIS))
							pre = new geogebra.export.PrintPreview(app, ((GuiManagerD)app.getGuiManager()).getDataAnalysisView(), PageFormat.LANDSCAPE);
						//if there is no view in focus (e.g. just closed the focused view),
						// it prints the GeoGebra main window
						else //if (dm.getFocusedPanel()==null)
							pre = new geogebra.export.PrintPreview(app, (Printable) app.getMainComponent(), PageFormat.LANDSCAPE);
						pre.setVisible(true);
					} catch (Exception e) {
						e.printStackTrace();
						App.debug("Print preview not available");
					} finally{
						app.setDefaultCursor();						
					}

				}
			};
			runner.start();
		} catch (java.lang.NoClassDefFoundError ee) {
			app.showErrorDialog(app.getError("ExportJarMissing"));
			ee.printStackTrace();
		}
	}

	/**
	 * Show the "About" dialog.
	 * 
	 * @param app
	 */
	public static void showAboutDialog(final AppD app) {
		

		StringBuilder sb = new StringBuilder();
		sb.append("<html><b>");
		appendVersion(sb, app);
		sb.append("</b>  (");
		sb.append("Java "); 
		AppD.appendJavaVersion(sb);
		sb.append(", ");
		sb.append(app.getHeapSize()/1024/1024);
		sb.append("MB, ");
		sb.append(App.getCASVersionString());
		sb.append(")<br>");	
		sb.append(GeoGebraConstants.BUILD_DATE);

		// license
		String text = app.loadTextFile(AppD.LICENSE_FILE);
		// We may want to modify the window size when the license file changes:
		JTextArea textArea = new JTextArea(26, 72); // window size fine tuning (rows, cols)
		JScrollPane scrollPane = new JScrollPane(textArea,
				ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		textArea.setEditable(false);
		// not sure if Monospaced is installed everywhere:
		textArea.setFont(new Font("Monospaced",Font.PLAIN,12));
		textArea.setText(text);
		textArea.setCaretPosition(0);

		JPanel systemInfoPanel = new JPanel(new BorderLayout(5, 5));
		systemInfoPanel.add(new JLabel(sb.toString()), BorderLayout.CENTER);

		// copy system information to clipboard
		systemInfoPanel.add(new JButton(new AbstractAction(app.getPlain("SystemInformation")) {

			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent arg0) {

				copyDebugInfoToClipboard(app);
				
				app.showMessage(app.getPlain("SystemInformationMessage"));
			}
		}), app.borderEast());

		JPanel panel = new JPanel(new BorderLayout(5, 5));
		panel.add(systemInfoPanel, BorderLayout.NORTH);
		panel.add(scrollPane, BorderLayout.SOUTH);

		JOptionPane infoPane = new JOptionPane(panel,
				JOptionPane.PLAIN_MESSAGE, JOptionPane.DEFAULT_OPTION);

		final JDialog dialog = infoPane.createDialog(app.getMainComponent(),
				app.getMenu("AboutLicense"));

		dialog.setVisible(true);
	}
	
	private static void appendVersion(StringBuilder sb, AppD app) {
		sb.append(app.getPlain("ApplicationName"));
		sb.append(" ");
		sb.append(app.getVersionString());
		switch (Kernel.DEFAULT_CAS) {
		case MAXIMA:
			sb.append('m');
			break;
			// default: do nothing
		}
		if (app.getApplet() != null) sb.append(" Applet");
		else if (AppD.isWebstartDebug()) sb.append(" Debug");
		else if (AppD.isWebstart()) sb.append(" Webstart");		
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
		sb.append(System.getProperty("os.arch")); // tells us 32 or 64 bit (Java)
		sb.append("\nHeap: ");
		sb.append(app.getHeapSize()/1024/1024);
		sb.append("MB\nCAS: ");
		sb.append(App.getCASVersionString());
		sb.append("\n\n");

		if (App.logger != null) {
			// copy the entire log to systemInfo (maybe not required at all)
			sb.append("GeoGebraLogger log:\n");
			sb.append(App.logger.getEntireLog());
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
		Toolkit.getDefaultToolkit().getSystemClipboard().setContents(
				new StringSelection(sb.toString()), null
				);

	}

}
