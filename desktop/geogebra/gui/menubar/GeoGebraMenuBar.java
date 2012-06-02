package geogebra.gui.menubar;

import geogebra.common.GeoGebraConstants;
import geogebra.common.kernel.Kernel;
import geogebra.common.main.AbstractApplication;
import geogebra.export.ScalingPrintGridable;
import geogebra.gui.layout.DockManager;
import geogebra.gui.layout.Layout;
import geogebra.main.Application;
import geogebra.main.GeoGebraPreferences;

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
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Scanner;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ImageIcon;
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

	private BaseMenu fileMenu, editMenu, viewMenu, perspectivesMenu, optionsMenu, toolsMenu, windowMenu, helpMenu, languageMenu;

	private Application app;
	private Layout layout;
	

	/**
	 * Creates new menubar
	 * @param app Application
	 * @param layout Layout
	 */
	public GeoGebraMenuBar(Application app, Layout layout) {
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
		if(!app.isApplet()) {
			perspectivesMenu = new PerspectivesMenu(app, layout);
			add(perspectivesMenu);
		}
		
		// "Options"
		optionsMenu = new OptionsMenu(app);
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
		add(Box.createHorizontalGlue());

		// "flag" to select language
		final String flagName = app.getFlagName(false);
		final JLabel languageLabel = new JLabel(app.getFlagIcon(flagName));
		languageLabel.setToolTipText(app.getMenuTooltip("Language"));
		languageLabel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				JPopupMenu myPopup = new JPopupMenu();
				OptionsMenu.addLanguageMenuItems(app,  myPopup, new LanguageActionListener(app));
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
		               				AbstractApplication.debug("updating flag to "+geoIPflagname);
		               			}
		               			
		               			
		               			
		               			
		               			

		                   }
		               }).start();

		
		
}

	/**
	 * Update the menubar.
	 */
	public void updateMenubar() {
		AbstractApplication.debug("update menu");
		fileMenu.update();
		editMenu.update();
		viewMenu.update();
		optionsMenu.update();
		toolsMenu.update();
		if (perspectivesMenu != null)
			perspectivesMenu.update();
		
		if(!app.isApplet())
			windowMenu.update();
		
		helpMenu.update();
		
		updateSelection();
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
			setMenuFontRecursive(getMenu(i), app.getPlainFont());
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
				// System.out.println(m.getText());
				if (com instanceof JComponent) {
					((JComponent) com).setFont(font);
				}
				if (com instanceof JMenuItem) {
					setMenuFontRecursive((JMenuItem) com, font);
				}
			}
		}
		m.setFont(font);
	}
	
	
	/**
	 * Show the print preview dialog.
	 * 
	 * @param app
	 */
	public static void showPrintPreview(final Application app) {
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
						if (app.getGuiManager().hasCasView()){	
							DockManager dm=app.getGuiManager().getLayout().getDockManager();
							//if CAS-view has Focus, print CAS
							if (dm.getFocusedPanel()==dm.getPanel(Application.VIEW_CAS)){
								new geogebra.export.PrintPreview(app, app.getGuiManager().getCasView(), PageFormat.LANDSCAPE);
								printCAS=true;
							}
						}			
						
						if (!printCAS)
							new geogebra.export.PrintPreview(app, app
								.getEuclidianView(), PageFormat.LANDSCAPE);
						
						
	*/					
						
						DockManager dm=app.getGuiManager().getLayout().getDockManager();
						geogebra.export.PrintPreview pre;
						if (dm.getFocusedPanel()==dm.getPanel(AbstractApplication.VIEW_CAS))
							// TODO I think "new ScalingPrintGridable" here is not so nice. Maybe the constructor of PrintPreview should be changed
							pre = new geogebra.export.PrintPreview(app, new ScalingPrintGridable(app.getGuiManager().getCasView()), PageFormat.LANDSCAPE);
						else if (dm.getFocusedPanel()==dm.getPanel(AbstractApplication.VIEW_CONSTRUCTION_PROTOCOL))
							pre = new geogebra.export.PrintPreview(app, app.getGuiManager().getConstructionProtocolView(), PageFormat.LANDSCAPE);
						else if (dm.getFocusedPanel()==dm.getPanel(AbstractApplication.VIEW_SPREADSHEET))
							pre = new geogebra.export.PrintPreview(app, app.getGuiManager().getSpreadsheetView(), PageFormat.LANDSCAPE);
						else if (dm.getFocusedPanel()==dm.getPanel(AbstractApplication.VIEW_EUCLIDIAN2))
							pre = new geogebra.export.PrintPreview(app, app.getEuclidianView2(), PageFormat.LANDSCAPE);
						else if (dm.getFocusedPanel()==dm.getPanel(AbstractApplication.VIEW_ALGEBRA))
							pre = new geogebra.export.PrintPreview(app, app.getGuiManager().getAlgebraView(), PageFormat.LANDSCAPE);
						else if (dm.getFocusedPanel()==dm.getPanel(AbstractApplication.VIEW_EUCLIDIAN))
							pre = new geogebra.export.PrintPreview(app, app.getEuclidianView1(), PageFormat.LANDSCAPE);
						//if there is no view in focus (e.g. just closed the focused view),
						// it prints the GeoGebra main window
						else //if (dm.getFocusedPanel()==null)
							pre = new geogebra.export.PrintPreview(app, (Printable) app.getMainComponent(), PageFormat.LANDSCAPE);
						pre.setVisible(true);
					} catch (Exception e) {
						AbstractApplication.debug("Print preview not available");
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
	public static void showAboutDialog(final Application app) {
		final StringBuilder vsb = new StringBuilder();
		vsb.append(app.getPlain("ApplicationName"));
		vsb.append(" ");
		vsb.append(GeoGebraConstants.VERSION_STRING);
		switch (Kernel.DEFAULT_CAS) {
		case MAXIMA:
			vsb.append('m');
			break;
			// default: do nothing
		}
		if (app.getApplet() != null) vsb.append(" Applet");
		else if (Application.isWebstartDebug()) vsb.append(" Debug");
		else if (Application.isWebstart()) vsb.append(" Webstart");
		
		StringBuilder sb = new StringBuilder();
		sb.append("<html><b>");
		sb.append(vsb);
		sb.append("</b>  (");
		sb.append("Java "); 
		sb.append(System.getProperty("java.version")); 
		sb.append(", ");
		sb.append(app.getHeapSize()/1024/1024);
		sb.append("MB, ");
		sb.append(Application.getCASVersionString());
		sb.append(")<br>");	
		sb.append(GeoGebraConstants.BUILD_DATE);

		// license
		String text = app.loadTextFile(Application.LICENSE_FILE);
		// We may want to modify the window size when the license file changes:
		JTextArea textArea = new JTextArea(24, 72); // window size fine tuning (rows, cols)
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
				StringBuilder systemInfo = new StringBuilder();
				systemInfo.append("[code]");
				systemInfo.append(vsb);
				systemInfo.append(" (");
				systemInfo.append(GeoGebraConstants.BUILD_DATE);
				systemInfo.append(")\nJava: ");
				systemInfo.append(System.getProperty("java.version"));
				systemInfo.append(")\nCodebase: ");
				systemInfo.append(Application.getCodeBase());
				systemInfo.append("\nOS: ");
				systemInfo.append(System.getProperty("os.name"));
				systemInfo.append("\nArchitecture: ");
				systemInfo.append(System.getProperty("os.arch")); // tells us 32 or 64 bit (Java)
				systemInfo.append("\nHeap: ");
				systemInfo.append(app.getHeapSize()/1024/1024);
				systemInfo.append("MB\nCAS: ");
				systemInfo.append(Application.getCASVersionString());
				systemInfo.append("\n\n");
				
				// copy log file to systemInfo
				if (app.logFile != null) {
				    String NL = System.getProperty("line.separator");
				    Scanner scanner = null;
				    try {
					  scanner = new Scanner(new File(app.logFile.toString()));
				      while (scanner.hasNextLine()){
				    	  systemInfo.append(scanner.nextLine() + NL);
				      }
				    } catch (FileNotFoundException e) {
				    	
				    }
				    finally{
				      if (scanner != null) scanner.close();
				    }
				}
				
				// append ggb file (except images)
				systemInfo.append(app.getXML());
				systemInfo.append("\n\n");
				systemInfo.append(app.getMacroXML());
				systemInfo.append("\n\nLibraryJavaScript:\n");
				app.getKernel().getLibraryJavaScript();
				
				systemInfo.append("\n\nPreferences:\n");
				systemInfo.append(GeoGebraPreferences.getPref().getXMLPreferences());
				systemInfo.append("[/code]");
				Toolkit.getDefaultToolkit().getSystemClipboard().setContents(
					new StringSelection(systemInfo.toString()), null
				);
				
				app.showMessage(app.getPlain("SystemInformationMessage"));
			}
		}), BorderLayout.EAST);
		
		JPanel panel = new JPanel(new BorderLayout(5, 5));
		panel.add(systemInfoPanel, BorderLayout.NORTH);
		panel.add(scrollPane, BorderLayout.SOUTH);

		JOptionPane infoPane = new JOptionPane(panel,
				JOptionPane.PLAIN_MESSAGE, JOptionPane.DEFAULT_OPTION);
		
		final JDialog dialog = infoPane.createDialog(app.getMainComponent(),
				app.getMenu("About") + " / " + app.getMenu("License"));

		dialog.setVisible(true);
	}
}
