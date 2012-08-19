package geogebra;

import geogebra.common.kernel.View;
import geogebra.common.main.App;
import geogebra.main.AppD;
import geogebra.plugin.GgbAPID;

import java.awt.BorderLayout;
import java.net.URL;
import java.util.Locale;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

/**
 * GeoGebra application inside a JPanel that can be integrated
 * into other applications.
 */
public class GeoGebraPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	/**
	 * Test method that demonstrates how to embed a GeoGebraPanel
	 * into another application.
	 * @param args
	 */
    public static void main(String[] args) {  
    	// prepare URL for ggb file
//    	URL ggbURL = null;
//    	try {
//        	File ggbFile = new File("test.ggb");
//        	ggbURL = ggbFile.toURL();
//    	} catch (Exception e) {
//    		e.printStackTrace();
//    	}
    	
    	// create GeoGebraPanel and open test file
//    	GeoGebraPanel ggbPanel = new GeoGebraPanel(ggbURL);
    	
    	// create empty GeoGebraPanel
    	GeoGebraPanel ggbPanel = new GeoGebraPanel();
    	
    	// hide input bar
    	ggbPanel.setShowAlgebraInput(false);
    	// use smaller icons in toolbar
    	ggbPanel.setMaxIconSize(24); 
    	
    	// show menu bar and toolbar
    	ggbPanel.setShowMenubar(true);
    	ggbPanel.setShowToolbar(true);
    	
    	// build the user interface of the GeoGebraPanel
    	ggbPanel.buildGUI();
    	
    	// use GeoGebraAPI
    	ggbPanel.getGeoGebraAPI().evalCommand("100 - x");
    	ggbPanel.getGeoGebraAPI().evalCommand("x + 100");
    	ggbPanel.getGeoGebraAPI().setAxesCornerCoordsVisible(false);
    
    	// add GeoGebraPanel to your application
    	JFrame f = new JFrame();
    	f.add(ggbPanel);
    	f.setSize(800, 600);
    	f.setVisible(true);
    }
	
	
	private AppD app;
	
	/**
     * Creates a GeoGebraPanel. Note that you need to 
     * call buildGUI() after this method.
     */
	public GeoGebraPanel() {
		this(null);
	}
	
	/**
     * Creates a GeoGebraPanel and loads the given ggbFile.
     * Note that you need to call buildGUI() after this method.
     */
	public GeoGebraPanel(URL ggbFile) {
		// use filename as argument
		String [] args = null;
		if (ggbFile != null) {
			args = new String[1];
			args[0] = ggbFile.toExternalForm();
		}
		
		// create GeoGebra application
		app = new AppD(new CommandLineArguments(args), this, false);
	}
	
	/**
	 * Loads a ggb or ggt file. 
	 * Note that you need to call buildGUI() after this 
	 * method to update the panel's structure.
	 */
	public synchronized void openFile(URL url) {
		app.getGgbApi().openFile(url.toExternalForm());
	}
	
	/**
	 * Tells the panel to show/hide the tool bar.
	 * Note that you need to call buildGUI() after this 
	 * method to update the panel's structure.
	 */
	public synchronized void setShowToolbar(boolean showToolBar) {
		app.setShowToolBar(showToolBar, true);	
	}
	
	/**
	 * Sets the font size of the GeoGebra user interface.
	 */
	public synchronized void setFontSize(int points) {
		app.setFontSize(points);
	}
	
	/**
	 * Sets the maximum pixel size of all icons in the GeoGebra
	 * user interface including the toolbar. 
	 * @pixel: a value between 16 and 32
	 */
	public synchronized void setMaxIconSize(int pixel) {
		app.setMaxIconSize(pixel);
	}
	
	/**
	 * Tells the panel to show/hide the menu bar.
	 * Note that you need to call buildGUI() after this 
	 * method to update the panel's structure.
	 */
	public synchronized void setShowMenubar(boolean showMenuBar) {
		app.setShowMenuBar(showMenuBar);
	}
	
	/**
	 * Tells the panel to show/hide the input bar.
	 * Note that you need to call buildGUI() after this 
	 * method to update the panel's structure.
	 */
	public synchronized void setShowAlgebraInput(boolean showInputBar) {
		app.setShowAlgebraInput(showInputBar, true);	
	}
	
	/**
	 * Tells the panel to show/hide the algebra view.
	 * Note that you need to call buildGUI() after this 
	 * method to update the panel's structure.
	 */
	public synchronized void setShowAlgebraView(boolean show) {
		app.getGuiManager().setShowView(show, App.VIEW_ALGEBRA);	
	}
	
	/**
	 * Tells the panel to show/hide the spreadsheet view.
	 * Note that you need to call buildGUI() after this 
	 * method to update the panel's structure.
	 */
	public synchronized void setShowSpreadsheetView(boolean show) {
		app.getGuiManager().setShowView(show, App.VIEW_SPREADSHEET);	
	}
	
	/**
	 * Returns the graphics view's panel.
	 */
	public JPanel getGraphicsPanel() {
		return app.getEuclidianView1().getJPanel();
	}
	
	/**
	 * Sets the language of the GeoGebraPanel.
	 * Note that you need to call buildGUI() after this 
	 * method to update the panel's structure.
	 */
	public synchronized void setLanguage(Locale locale) {
		app.setLanguage(locale);
	}
	
	/**
	 * Attaches a view to listen to kernel changes.
	 */
	public synchronized void attachView(View view) {
		app.getKernel().attach(view);
	}
	
	/**
	 * Detaches a view from the kernel.
	 */
	public synchronized void detachView(View view) {
		app.getKernel().detach(view);
	}
	
	/**
	 * Rebuilds the GeoGebra user interface in this panel.
	 */
	public synchronized void buildGUI() {
		removeAll();
		setLayout(new BorderLayout());
		
		// activate undo
		app.setUndoActive(app.showMenuBar() || app.showToolBar());
		
		// create application panel
		add(app.buildApplicationPanel(), BorderLayout.CENTER);
		
		if (isShowing())
			SwingUtilities.updateComponentTreeUI(this);
	}
	
	/**
	 * Returns the GeoGebraAPI object that lets you interact
	 * with the GeoGebra construction.
	 */
	public synchronized GgbAPID getGeoGebraAPI() {
		return app.getGgbApi();
	}

}
