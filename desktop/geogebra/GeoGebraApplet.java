/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package geogebra;

import geogebra.common.main.App;
import geogebra.common.plugin.JavaScriptAPI;
import geogebra.main.AppletImplementation;

import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JApplet;
import javax.swing.RepaintManager;
import javax.swing.SwingUtilities;

/**
 * GeoGebra applet
 * 
 * @see geogebra.main.AppletImplementation for the actual implementation
 * @author Markus Hohenwarter
 * @date 2008-10-24
 */
public class GeoGebraApplet extends JApplet implements JavaScriptAPI {

	private static final long serialVersionUID = -350682076336303151L;

	// applet member variables
	/** Implamentation of applet's methods*/
	AppletImplementationInterface appletImplementation = null;
	private boolean animationRunningAtLastStop = false;

	/** Splash screen */
	AppletSplashScreen splashScreen = null;
	private boolean appletIsIniting = true;
	
	/**
	 * Loads necessary jar files and initializes applet. During the loading of
	 * jar files, a splash screen is shown.
	 */
	@Override
	public void init() {		
		App.debug("init");
		try {
			SwingUtilities.invokeAndWait(new Runnable() {
				public void run() {										
					// init splash screen 
					splashScreen = new AppletSplashScreen(GeoGebraApplet.this);	
					repaint();
					
					// init APPLET in background task
					initAppletInBackground();
				}
			});
		} catch (Throwable e) {		
			e.printStackTrace();
		} 					
	}
	
	/**
	 * Initializes applet in separate thread
	 */
	void initAppletInBackground() {
		Thread runner = new Thread() {
			@Override
			public void run() {						
				App.debug("initAppletInBackground");
				// init applet
				getAppletImplementation();
																									
				// init CAS, GUI dialogs, and download jar files in background
				SwingUtilities.invokeLater(new Runnable() {										
			      public void run() {	
			    	// update GUI
				    if (GeoGebraApplet.this.isShowing())
				   		SwingUtilities.updateComponentTreeUI(GeoGebraApplet.this);	
						
				    // dispose splash screen
					splashScreen.dispose();
					splashScreen = null;
					
					// init some things in background (like CAS, more GUI components)
					appletImplementation.initInBackground();										
			      }			      			      
			    });													
			}
		};
		runner.start();
	}

	@Override
	public void start() {
		// restart animation if necessary
		if (animationRunningAtLastStop) {
			appletImplementation.startAnimation();
		}
		
		// request focus
		// important for accessibility (ie where the mouse can't be used)
		// as the browser may not provide a keyboard shortcut
		requestFocus();
		requestFocusInWindow();
		requestFocus();

		repaint();
	}

	@Override
	public void stop() {
		// stop animation and remember that it needs to be restarted later
		if (appletImplementation != null) {
			animationRunningAtLastStop = appletImplementation.isAnimationRunning();
			if (animationRunningAtLastStop) {
				appletImplementation.stopAnimation();
			}
		}
		
		repaint();
	}

	@Override
	public void destroy() {	
		// stop animation
		if (appletImplementation != null) {
			appletImplementation.stopAnimation();	
			appletImplementation.dispose();		
			appletImplementation = null;
		}
		
		if (splashScreen != null) {
			splashScreen.dispose();
			splashScreen = null;	
		}
		
		// free up memory on reload, see Java bug
		// http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=6550586
		RepaintManager.setCurrentManager(null);
		
	}

	/**
	 * Returns the appletImplementation object.
	 * @return the applet implementation
	 */
	public synchronized AppletImplementationInterface getAppletImplementation() {
		if (appletIsIniting) {
			initAppletImplementation();						
		}

		return appletImplementation;
	}
	
	/**
	 * @param appImp applet implementation
	 */
	public void setAppletImplementation(AppletImplementationInterface appImp) {
		appletImplementation = appImp;
		appletIsIniting = false;
	}
	
	/**
	 * Initializes the appletImplementation object. Loads geogebra_main.jar file
	 * and initializes applet if necessary.
	 */
	protected synchronized void initAppletImplementation() {
		if (isAppletFullyLoaded()) return;

		// create delegate object that implements our applet's methods
		AppletImplementation applImpl = new AppletImplementation(this);

		// initialize applet's user interface, this changes the content pane
		applImpl.initGUI();		

		// remember the applet implementation
		setAppletImplementation(applImpl);
	}		
	
	/**
	 * Paints the applet or a loading screen while the applet is being
	 * initialized.
	 */
	@Override
	final public void paint(Graphics g) {
		// INITING applet
		if (appletIsIniting) {
			// show splash screen
			if (splashScreen != null && splashScreen.isReady()) {
				g.drawImage(splashScreen.getImage(), 0, 0, null);
			} 
			else {
				// OUT OF MEMORY
				// Draw GeoGebra text until splash screen is ready.
			    // This is only shown when we ran out of memory and 
				// couldn't even load the splash screen.
				int width = getWidth();
				int height = getHeight();
				g.setColor(Color.white);
				g.fillRect(0, 0, width, height);				
				g.setColor(Color.darkGray);				
				g.drawString("GeoGebra", width/2 - 10, height/2);

				// less then about 2MB free memory: we may have run out of memory:
				// tell user to restart browser
				if (Runtime.getRuntime().freeMemory() < 2000000) {								
					g.drawString("Out of Memory Error: Please restart your browser. ", width/2 - 120, height/2 + 30);
				}
			}
		} 
		
		// STANDARD CASE
		else {					
			super.paint(g);
		}
	}
	
	/**
	 * @return true once the GUI is fully initialized
	 */
	final boolean isAppletFullyLoaded() {
		return !appletIsIniting;
	}

	/*
	 * JAVASCRIPT interface
	 * 
	 * To add a new JavaScript method, do the following: 1) add the method stub
	 * to the interface geogebra.JavaScriptAPI 2) implement the method in
	 * geogebra.main.AppletImplementation 3) impplement the method here in
	 * geogebra.GeoGebraApplet by delegating it to
	 * geogebra.main.AppletImplementation
	 */

	public synchronized void deleteObject(String objName) {
		getAppletImplementation().deleteObject(objName);
	}
	
	public synchronized boolean evalCommand(final String cmdString) {
		return evalCommand(cmdString, true);
	}

	public synchronized String evalCommandCAS(final String cmdString) {
		return getAppletImplementation().evalCommandCAS(cmdString);
	}

	public synchronized boolean evalCommand(final String cmdString, boolean waitForResult) {
		
		if (waitForResult) {
			return getAppletImplementation().evalCommand(cmdString);
		}

		// see #106
		// (redraw error if evalCommand() is called again before it's finished)
		try {
			SwingUtilities.invokeLater(new Runnable(){
				public void run(){
					getAppletImplementation().evalCommand(cmdString);
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return true;
		
	}

	public synchronized void evalXML(String xmlString) {
		getAppletImplementation().evalXML(xmlString);
	}

	//public synchronized String evalMathPiper(String cmdString) {
	//	return getAppletImplementation().evalMathPiper(cmdString);
	//}
	
	//public synchronized String evalYacas(String cmdString) {
	//	return evalMathPiper(cmdString);
	//}		

	public synchronized void debug(String string) {
		getAppletImplementation().debug(string);
	}

	public synchronized boolean exists(String objName) {
		return getAppletImplementation().exists(objName);
	}

	public synchronized String[] getAllObjectNames() {
		return getAppletImplementation().getAllObjectNames();
	}

	public synchronized String getColor(String objName) {
		return getAppletImplementation().getColor(objName);
	}

	public synchronized double getFilling(String objName) {
		return getAppletImplementation().getFilling(objName);
	}

	public synchronized int getPointStyle(String objName) {
		return getAppletImplementation().getPointStyle(objName);
	}

	public synchronized int getPointSize(String objName) {
		return getAppletImplementation().getPointSize(objName);
	}

	public synchronized int getLineStyle(String objName) {
		return getAppletImplementation().getLineStyle(objName);
	}

	public synchronized int getLineThickness(String objName) {
		return getAppletImplementation().getLineThickness(objName);
	}

	public synchronized String getCommandString(String objName) {
		return getAppletImplementation().getCommandString(objName);
	}

	public synchronized String getDefinitionString(String objName) {
		return getAppletImplementation().getDefinitionString(objName);
	}

	public synchronized byte[] getGGBfile() {
		return getAppletImplementation().getGGBfile();
	}

	public synchronized String getHostname() {
		return getAppletImplementation().getHostname();
	}

	public synchronized String getIPAddress() {
		return getAppletImplementation().getIPAddress();
	}

	public synchronized int getLayer(String objName) {
		return getAppletImplementation().getLayer(objName);
	}

	public synchronized String getObjectName(int i) {
		return getAppletImplementation().getObjectName(i);
	}

	public synchronized String getPNGBase64(double exportScale, boolean transparent, double DPI) {
		return getAppletImplementation().getPNGBase64(exportScale, transparent, DPI);
	}
	
	public synchronized boolean writePNGtoFile(String filename, double exportScale, boolean transparent, double DPI) {
		return getAppletImplementation().writePNGtoFile(filename, exportScale, transparent, DPI);
	}
	
	public synchronized int getObjectNumber() {
		return getAppletImplementation().getObjectNumber();
	}

	public synchronized String getObjectType(String objName) {
		return getAppletImplementation().getObjectType(objName);
	}

	public synchronized double getValue(String objName) {
		return getAppletImplementation().getValue(objName);
	}

	public synchronized String getValueString(String objName) {
		return getAppletImplementation().getValueString(objName);
	}

	public synchronized String getXML() {
		return getAppletImplementation().getXML();
	}

	public synchronized String getBase64() {
		return getAppletImplementation().getBase64(false);
	}
	
	public synchronized String getBase64(boolean includeThumbnail) {
		return getAppletImplementation().getBase64(includeThumbnail);
	}

	public synchronized String getXML(String objName) {
		return getAppletImplementation().getXML(objName);
	}

	public synchronized String getAlgorithmXML(String objName) {
		return getAppletImplementation().getAlgorithmXML(objName);
	}

	public synchronized double getXcoord(String objName) {
		return getAppletImplementation().getXcoord(objName);
	}

	public synchronized double getYcoord(String objName) {
		return getAppletImplementation().getYcoord(objName);
	}

	public synchronized boolean isDefined(String objName) {
		return getAppletImplementation().isDefined(objName);
	}

	public synchronized boolean isIndependent(String objName) {
		return getAppletImplementation().isIndependent(objName);
	}

	public synchronized void openFile(String strURL) {
		getAppletImplementation().openFile(strURL);
	}

	public synchronized void refreshViews() {
		getAppletImplementation().refreshViews();
	}

	public synchronized void registerAddListener(String JSFunctionName) {
		getAppletImplementation().registerAddListener(JSFunctionName);
	}

	public synchronized void registerClearListener(String JSFunctionName) {
		getAppletImplementation().registerClearListener(JSFunctionName);
	}

	public synchronized void registerObjectUpdateListener(String objName,
			String JSFunctionName) {
		getAppletImplementation().registerObjectUpdateListener(objName,
				JSFunctionName);
	}

	public synchronized void registerObjectClickListener(String objName,
			String JSFunctionName) {
		getAppletImplementation().registerObjectClickListener(objName,
				JSFunctionName);
	}

	public synchronized void registerRemoveListener(String JSFunctionName) {
		getAppletImplementation().registerRemoveListener(JSFunctionName);
	}

	public synchronized void registerRenameListener(String JSFunctionName) {
		getAppletImplementation().registerRenameListener(JSFunctionName);
	}

	public synchronized void registerUpdateListener(String JSFunctionName) {
		getAppletImplementation().registerUpdateListener(JSFunctionName);
	}

	public synchronized void registerClickListener(String JSFunctionName) {
		getAppletImplementation().registerClickListener(JSFunctionName);
	}

	public synchronized boolean renameObject(String oldObjName,
			String newObjName) {
		return getAppletImplementation().renameObject(oldObjName, newObjName);
	}

	public synchronized void setAnimating(String objName, boolean animate) {
		getAppletImplementation().setAnimating(objName, animate);
	}

	public synchronized void setAnimationSpeed(String objName, double speed) {
		getAppletImplementation().setAnimationSpeed(objName, speed);
	}

	public synchronized void startAnimation() {
		getAppletImplementation().startAnimation();
	}

	public synchronized void stopAnimation() {
		getAppletImplementation().stopAnimation();
	}
	
	public void hideCursorWhenDragging(boolean hideCursorWhenDragging) {
		getAppletImplementation().hideCursorWhenDragging(hideCursorWhenDragging);
	}

	public synchronized boolean isAnimationRunning() {
		return getAppletImplementation().isAnimationRunning();
	}

	public synchronized void reset() {
		getAppletImplementation().reset();
	}

	public synchronized void setAxesVisible(boolean xVisible, boolean yVisible) {
		getAppletImplementation().setAxesVisible(xVisible, yVisible);
	}

	public synchronized void setColor(String objName, int red, int green,
			int blue) {
		getAppletImplementation().setColor(objName, red, green, blue);
	}

	public synchronized void setLineThickness(String objName, int thickness) {
		getAppletImplementation().setLineThickness(objName, thickness);
	}

	public synchronized void setLineStyle(String objName, int style) {
		getAppletImplementation().setLineStyle(objName, style);
	}

	public synchronized void setFilling(String objName, double filling) {
		getAppletImplementation().setFilling(objName, filling);
	}

	public synchronized String getGraphicsViewCheckSum(String algorithm, String format) {
		return getAppletImplementation().getGraphicsViewCheckSum(algorithm, format);
	}

	public synchronized void setPointStyle(String objName, int style) {
		getAppletImplementation().setPointStyle(objName, style);
	}
	
	public void setOnTheFlyPointCreationActive(boolean flag) {
		getAppletImplementation().setOnTheFlyPointCreationActive(flag);
	}

	public void setUndoPoint() {
		getAppletImplementation().setUndoPoint();
	}

	public synchronized void setPointSize(String objName, int style) {
		getAppletImplementation().setPointSize(objName, style);
	}

	public synchronized void setCoordSystem(double xmin, double xmax,
			double ymin, double ymax) {
		getAppletImplementation().setCoordSystem(xmin, xmax, ymin, ymax);
	}

	public synchronized void setCoords(String objName, double x, double y) {
		getAppletImplementation().setCoords(objName, x, y);
	}

	public synchronized void setErrorDialogsActive(boolean flag) {
		getAppletImplementation().setErrorDialogsActive(flag);
	}

	public synchronized void setFixed(String objName, boolean flag) {
		getAppletImplementation().setFixed(objName, flag);
	}

	public synchronized void setGridVisible(boolean flag) {
		getAppletImplementation().setGridVisible(flag);
	}

	public synchronized void setLabelMode(String objName, boolean visible) {
		getAppletImplementation().setLabelMode(objName, visible);
	}

	public synchronized void setLabelStyle(String objName, int style) {
		getAppletImplementation().setLabelStyle(objName, style);
	}

	public synchronized void setLabelVisible(String objName, boolean visible) {
		getAppletImplementation().setLabelVisible(objName, visible);
	}

	public synchronized void setLayer(String objName, int layer) {
		getAppletImplementation().setLayer(objName, layer);
	}

	public synchronized void setLayerVisible(int layer, boolean visible) {
		getAppletImplementation().setLayerVisible(layer, visible);
	}

	public synchronized void setMode(int mode) {
		getAppletImplementation().setMode(mode);
	}

	public synchronized void setRepaintingActive(boolean flag) {
		getAppletImplementation().setRepaintingActive(flag);
	}

	public synchronized void setTrace(String objName, boolean flag) {
		getAppletImplementation().setTrace(objName, flag);
	}

	public synchronized void setValue(String objName, double x) {
		getAppletImplementation().setValue(objName, x);
	}

	public synchronized void setVisible(String objName, boolean visible) {
		getAppletImplementation().setVisible(objName, visible);
	}

	public synchronized boolean getVisible(String objName) {
		return getAppletImplementation().getVisible(objName);
	}

	public synchronized void setXML(String xml) {
		getAppletImplementation().setXML(xml);
	}

	public synchronized void setBase64(String base64) {
		getAppletImplementation().setBase64(base64);
	}

	public synchronized void unregisterAddListener(String JSFunctionName) {
		getAppletImplementation().unregisterAddListener(JSFunctionName);
	}

	public synchronized void unregisterClearListener(String JSFunctionName) {
		getAppletImplementation().unregisterClearListener(JSFunctionName);
	}

	public synchronized void unregisterObjectUpdateListener(String objName) {
		getAppletImplementation().unregisterObjectUpdateListener(objName);
	}

	public synchronized void unregisterObjectClickListener(String objName) {
		getAppletImplementation().unregisterObjectClickListener(objName);
	}

	public synchronized void unregisterRemoveListener(String JSFunctionName) {
		getAppletImplementation().unregisterRemoveListener(JSFunctionName);
	}

	public synchronized void unregisterRenameListener(String JSFunctionName) {
		getAppletImplementation().unregisterRenameListener(JSFunctionName);
	}

	public synchronized void unregisterUpdateListener(String JSFunctionName) {
		getAppletImplementation().unregisterUpdateListener(JSFunctionName);
	}

	public synchronized void unregisterClickListener(String JSFunctionName) {
		getAppletImplementation().unregisterClickListener(JSFunctionName);
	}

	public boolean isMoveable(String objName) {
		return getAppletImplementation().isMoveable(objName);
	}
	

	public void drawToImage(String label,double[] x, double[] y) {
		getAppletImplementation().drawToImage(label, x, y);		
	}
	
	public void clearImage(String label) {
		getAppletImplementation().clearImage(label);		
	}

	public void uploadToGeoGebraTube() {
		getAppletImplementation().uploadToGeoGebraTube();
		
	}

	public void setPenColor(int red, int green, int blue) {
		getAppletImplementation().setPenColor(red, green, blue);
		
	}

	public void setPenSize(int size) {
		getAppletImplementation().setPenSize(size);
	}

	public int getPenSize() {
		return getAppletImplementation().getPenSize();
	}

	public String getPenColor() {
		return getAppletImplementation().getPenColor();
	}

	public void setListValue(String objName, int index, double x) {
		getAppletImplementation().setListValue(objName, index, x); 	
	}

	public double getListValue(String objName, int index) {
		return getAppletImplementation().getListValue(objName, index); 
	}
}
