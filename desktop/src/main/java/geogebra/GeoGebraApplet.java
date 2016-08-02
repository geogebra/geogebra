/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package geogebra;

import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JApplet;
import javax.swing.RepaintManager;
import javax.swing.SwingUtilities;

import org.geogebra.common.plugin.JavaScriptAPI;
import org.geogebra.common.util.debug.Log;
import org.geogebra.desktop.AppletImplementationInterface;
import org.geogebra.desktop.AppletSplashScreen;
import org.geogebra.desktop.main.AppletImplementation;

/**
 * GeoGebra applet
 * 
 * @see org.geogebra.desktop.main.AppletImplementation for the actual implementation
 * @author Markus Hohenwarter
 * @date 2008-10-24
 */
public class GeoGebraApplet extends JApplet implements JavaScriptAPI {

	private static final long serialVersionUID = -350682076336303151L;

	// applet member variables
	/** Implamentation of applet's methods */
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
		Log.debug("init");
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
				Log.debug("initAppletInBackground");
				// init applet
				getGgbApi();

				// init CAS, GUI dialogs, and download jar files in background
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						// update GUI
						if (GeoGebraApplet.this.isShowing())
							SwingUtilities
									.updateComponentTreeUI(GeoGebraApplet.this);

						// dispose splash screen
						splashScreen.dispose();
						splashScreen = null;

						// init some things in background (like CAS, more GUI
						// components)
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
			getGgbApi().startAnimation();
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
			animationRunningAtLastStop = getGgbApi()
					.isAnimationRunning();
			if (animationRunningAtLastStop) {
				getGgbApi().stopAnimation();
			}
		}

		repaint();
	}

	@Override
	public void destroy() {
		// stop animation
		if (appletImplementation != null) {
			getGgbApi().stopAnimation();
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
	 * 
	 * @return the applet implementation
	 */
	public synchronized AppletImplementationInterface getAppletImplementation() {
		if (appletIsIniting) {
			initAppletImplementation();
		}

		return appletImplementation;
	}

	/**
	 * @param appImp
	 *            applet implementation
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
		if (isAppletFullyLoaded())
			return;

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
			} else {
				// OUT OF MEMORY
				// Draw GeoGebra text until splash screen is ready.
				// This is only shown when we ran out of memory and
				// couldn't even load the splash screen.
				int width = getWidth();
				int height = getHeight();
				g.setColor(Color.white);
				g.fillRect(0, 0, width, height);
				g.setColor(Color.darkGray);
				g.drawString("GeoGebra", width / 2 - 10, height / 2);

				// less then about 2MB free memory: we may have run out of
				// memory:
				// tell user to restart browser
				if (Runtime.getRuntime().freeMemory() < 2000000) {
					g.drawString(
							"Out of Memory Error: Please restart your browser. ",
							width / 2 - 120, height / 2 + 30);
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
	 * geogebra.main.AppletImplementation 3) implement the method here in
	 * org.geogebra.desktop.GeoGebraApplet by delegating it to
	 * geogebra.main.AppletImplementation
	 */

	public synchronized void deleteObject(String objName) {
		getGgbApi().deleteObject(objName);
	}

	public synchronized boolean evalCommand(final String cmdString) {
		return evalCommand(cmdString, true);
	}

	public synchronized String evalCommandCAS(final String cmdString) {
		return getGgbApi().evalCommandCAS(cmdString);
	}

	public synchronized String evalGeoGebraCAS(final String cmdString) {
		return getAppletImplementation().evalGeoGebraCAS(cmdString);
	}

	public synchronized boolean evalCommand(final String cmdString,
			boolean waitForResult) {

		if (waitForResult) {
			return getGgbApi().evalCommand(cmdString);
		}

		// see #106
		// (redraw error if evalCommand() is called again before it's finished)
		try {
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					getGgbApi().evalCommand(cmdString);
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

	public synchronized void debug(String string) {
		getGgbApi().debug(string);
	}

	public synchronized boolean exists(String objName) {
		return getGgbApi().exists(objName);
	}

	public synchronized String[] getAllObjectNames() {
		return getGgbApi().getAllObjectNames();
	}

	public synchronized String getColor(String objName) {
		return getGgbApi().getColor(objName);
	}

	public synchronized double getFilling(String objName) {
		return getGgbApi().getFilling(objName);
	}

	public synchronized int getPointStyle(String objName) {
		return getGgbApi().getPointStyle(objName);
	}

	public synchronized int getPointSize(String objName) {
		return getGgbApi().getPointSize(objName);
	}

	public synchronized int getLineStyle(String objName) {
		return getGgbApi().getLineStyle(objName);
	}

	public synchronized int getLineThickness(String objName) {
		return getGgbApi().getLineThickness(objName);
	}

	public synchronized String getCommandString(String objName) {
		return getGgbApi().getCommandString(objName);
	}

	public synchronized String getDefinitionString(String objName) {
		return getGgbApi().getDefinitionString(objName);
	}

	public synchronized String getLaTeXString(String objName) {
		return getGgbApi().getLaTeXString(objName);
	}

	public synchronized byte[] getGGBfile() {
		return getGgbApi().getGGBfile();
	}

	public synchronized int getLayer(String objName) {
		return getGgbApi().getLayer(objName);
	}

	public synchronized String getObjectName(int i) {
		return getGgbApi().getObjectName(i);
	}

	public synchronized String getPNGBase64(double exportScale,
			boolean transparent, double DPI, boolean copyToClipboard) {
		return getAppletImplementation().getPNGBase64(exportScale, transparent,
				DPI, copyToClipboard);
	}

	public synchronized boolean writePNGtoFile(String filename,
			double exportScale, boolean transparent, double DPI) {
		return getAppletImplementation().writePNGtoFile(filename, exportScale,
				transparent, DPI);
	}

	public synchronized int getObjectNumber() {
		return getGgbApi().getObjectNumber();
	}

	public synchronized String getObjectType(String objName) {
		return getGgbApi().getObjectType(objName);
	}

	public synchronized double getValue(String objName) {
		return getGgbApi().getValue(objName);
	}

	public synchronized String getValueString(String objName) {
		return getGgbApi().getValueString(objName);
	}

	public synchronized String getXML() {
		return getGgbApi().getXML();
	}

	public synchronized String getBase64() {
		return getGgbApi().getBase64(false);
	}

	public synchronized String getBase64(boolean includeThumbnail) {
		return getGgbApi().getBase64(includeThumbnail);
	}

	public synchronized String getXML(String objName) {
		return getGgbApi().getXML(objName);
	}

	public synchronized String getAlgorithmXML(String objName) {
		return getGgbApi().getAlgorithmXML(objName);
	}

	public synchronized double getXcoord(String objName) {
		return getGgbApi().getXcoord(objName);
	}

	public synchronized double getYcoord(String objName) {
		return getGgbApi().getYcoord(objName);
	}

	public synchronized double getZcoord(String objName) {
		return getGgbApi().getYcoord(objName);
	}

	public synchronized boolean isDefined(String objName) {
		return getGgbApi().isDefined(objName);
	}

	public synchronized boolean isIndependent(String objName) {
		return getGgbApi().isIndependent(objName);
	}

	public synchronized void openFile(String strURL) {
		getAppletImplementation().openFile(strURL);
	}

	public synchronized void refreshViews() {
		getGgbApi().refreshViews();
	}

	public synchronized void registerAddListener(String JSFunctionName) {
		getGgbApi().registerAddListener(JSFunctionName);
	}

	public synchronized void registerClearListener(String JSFunctionName) {
		getGgbApi().registerClearListener(JSFunctionName);
	}

	public synchronized void registerObjectUpdateListener(String objName,
			String JSFunctionName) {
		getGgbApi().registerObjectUpdateListener(objName,
				JSFunctionName);
	}

	public synchronized void registerObjectClickListener(String objName,
			String JSFunctionName) {
		getGgbApi().registerObjectClickListener(objName,
				JSFunctionName);
	}

	public synchronized void registerRemoveListener(String JSFunctionName) {
		getGgbApi().registerRemoveListener(JSFunctionName);
	}

	public synchronized void registerRenameListener(String JSFunctionName) {
		getGgbApi().registerRenameListener(JSFunctionName);
	}

	public synchronized void registerUpdateListener(String JSFunctionName) {
		getGgbApi().registerUpdateListener(JSFunctionName);
	}

	public synchronized void registerClickListener(String JSFunctionName) {
		getGgbApi().registerClickListener(JSFunctionName);
	}

	public synchronized boolean renameObject(String oldObjName,
			String newObjName) {
		return getGgbApi().renameObject(oldObjName, newObjName);
	}

	public synchronized void setAnimating(String objName, boolean animate) {
		getGgbApi().setAnimating(objName, animate);
	}

	public synchronized void setAnimationSpeed(String objName, double speed) {
		getGgbApi().setAnimationSpeed(objName, speed);
	}

	public synchronized void startAnimation() {
		getGgbApi().startAnimation();
	}

	public synchronized void stopAnimation() {
		getGgbApi().stopAnimation();
	}

	public void hideCursorWhenDragging(boolean hideCursorWhenDragging) {
		getGgbApi()
				.hideCursorWhenDragging(hideCursorWhenDragging);
	}

	public synchronized boolean isAnimationRunning() {
		return getGgbApi().isAnimationRunning();
	}

	public synchronized void reset() {
		getAppletImplementation().reset();
	}

	public synchronized void setAxesVisible(boolean xVisible, boolean yVisible) {
		getGgbApi().setAxesVisible(xVisible, yVisible);
	}

	public synchronized void setAxesVisible(int view, boolean xVisible,
			boolean yVisible, boolean zVisible) {
		getGgbApi().setAxesVisible(view, xVisible, yVisible,
				zVisible);
	}

	public synchronized void setColor(String objName, int red, int green,
			int blue) {
		getGgbApi().setColor(objName, red, green, blue);
	}

	public synchronized void setCorner(String objName, double x, double y,
			int index) {
		getGgbApi().setCorner(objName, x, y, index);
	}

	public synchronized void setCorner(String objName, double x, double y) {
		getGgbApi().setCorner(objName, x, y);
	}

	public synchronized void setLineThickness(String objName, int thickness) {
		getGgbApi().setLineThickness(objName, thickness);
	}

	public synchronized void setLineStyle(String objName, int style) {
		getGgbApi().setLineStyle(objName, style);
	}

	public synchronized void setFilling(String objName, double filling) {
		getGgbApi().setFilling(objName, filling);
	}

	public synchronized void setPointStyle(String objName, int style) {
		getGgbApi().setPointStyle(objName, style);
	}

	public void setOnTheFlyPointCreationActive(boolean flag) {
		getGgbApi().setOnTheFlyPointCreationActive(flag);
	}

	public void setUndoPoint() {
		getGgbApi().setUndoPoint();
	}

	public synchronized void setPointSize(String objName, int style) {
		getGgbApi().setPointSize(objName, style);
	}

	public synchronized void setCoordSystem(double xmin, double xmax,
			double ymin, double ymax) {
		getGgbApi().setCoordSystem(xmin, xmax, ymin, ymax);
	}

	public synchronized void setCoords(String objName, double x, double y) {
		getGgbApi().setCoords(objName, x, y);
	}

	public synchronized void setCoords(String objName, double x, double y,
			double z) {
		getGgbApi().setCoords(objName, x, y, z);
	}

	public synchronized void setErrorDialogsActive(boolean flag) {
		getGgbApi().setErrorDialogsActive(flag);
	}

	public synchronized void setFixed(String objName, boolean flag) {
		getGgbApi().setFixed(objName, flag);
	}

	public synchronized void setGridVisible(boolean flag) {
		getGgbApi().setGridVisible(flag);
	}

	public synchronized void setGridVisible(int view, boolean flag) {
		getGgbApi().setGridVisible(flag);
	}

	public synchronized void setLabelStyle(String objName, int style) {
		getGgbApi().setLabelStyle(objName, style);
	}

	public synchronized void setLabelVisible(String objName, boolean visible) {
		getGgbApi().setLabelVisible(objName, visible);
	}

	public synchronized boolean getLabelVisible(String objName) {
		return getGgbApi().getLabelVisible(objName);
	}

	public synchronized void setLayer(String objName, int layer) {
		getGgbApi().setLayer(objName, layer);
	}

	public synchronized void setLayerVisible(int layer, boolean visible) {
		getGgbApi().setLayerVisible(layer, visible);
	}

	public synchronized void setMode(int mode) {
		getGgbApi().setMode(mode);
	}

	public synchronized void setRepaintingActive(boolean flag) {
		getGgbApi().setRepaintingActive(flag);
	}

	public synchronized void setTrace(String objName, boolean flag) {
		getGgbApi().setTrace(objName, flag);
	}

	public synchronized void setValue(String objName, double x) {
		getGgbApi().setValue(objName, x);
	}

	public synchronized void setTextValue(String objName, String x) {
		getGgbApi().setTextValue(objName, x);
	}

	public synchronized void setListValue(String objName, double x, double y) {
		getGgbApi().setListValue(objName, x, y);
	}

	public synchronized void setVisible(String objName, boolean visible) {
		getGgbApi().setVisible(objName, visible);
	}

	public synchronized boolean getVisible(String objName) {
		return getGgbApi().getVisible(objName);
	}

	public synchronized void setXML(String xml) {
		getAppletImplementation().setXML(xml);
	}

	public synchronized void setBase64(String base64) {
		getGgbApi().setBase64(base64);
	}

	public synchronized void unregisterAddListener(String JSFunctionName) {
		getGgbApi().unregisterAddListener(JSFunctionName);
	}

	public synchronized void unregisterClearListener(String JSFunctionName) {
		getGgbApi().unregisterClearListener(JSFunctionName);
	}

	public synchronized void unregisterObjectUpdateListener(String objName) {
		getGgbApi().unregisterObjectUpdateListener(objName);
	}

	public synchronized void unregisterObjectClickListener(String objName) {
		getGgbApi().unregisterObjectClickListener(objName);
	}

	public synchronized void registerStoreUndoListener(String objName) {
		getGgbApi().registerStoreUndoListener(objName);
	}

	public synchronized void unregisterRemoveListener(String JSFunctionName) {
		getGgbApi().unregisterRemoveListener(JSFunctionName);
	}

	public synchronized void unregisterRenameListener(String JSFunctionName) {
		getGgbApi().unregisterRenameListener(JSFunctionName);
	}

	public synchronized void unregisterUpdateListener(String JSFunctionName) {
		getGgbApi().unregisterUpdateListener(JSFunctionName);
	}

	public synchronized void unregisterClickListener(String JSFunctionName) {
		getGgbApi().unregisterClickListener(JSFunctionName);
	}

	public boolean isMoveable(String objName) {
		return getGgbApi().isMoveable(objName);
	}

	public void drawToImage(String label, double[] x, double[] y) {
		getGgbApi().drawToImage(label, x, y);
	}

	public void clearImage(String label) {
		getGgbApi().clearImage(label);
	}

	public void uploadToGeoGebraTube() {
		getGgbApi().uploadToGeoGebraTube();

	}

	public void setPenColor(int red, int green, int blue) {
		getGgbApi().setPenColor(red, green, blue);

	}

	public void setPenSize(int size) {
		getGgbApi().setPenSize(size);
	}

	public int getPenSize() {
		return getGgbApi().getPenSize();
	}

	public String getPenColor() {
		return getGgbApi().getPenColor();
	}

	public double getListValue(String objName, int index) {
		return getGgbApi().getListValue(objName, index);
	}

	public void registerClientListener(String JSFunctionName) {
		getGgbApi().registerClientListener(JSFunctionName);
	}

	public void unregisterClientListener(String JSFunctionName) {
		getGgbApi().unregisterClientListener(JSFunctionName);
	}

	@Override
	public void setPerspective(String code) {
		getGgbApi().setPerspective(code);
	}

	@Override
	public boolean getVisible(String objName, int view) {
		return getGgbApi().getVisible(objName, view);
	}

	public int getCASObjectNumber() {
		return getGgbApi().getCASObjectNumber();
	}
	
	public int getMode(){
		return getGgbApi().getMode();
	}

	public float getExerciseFraction() {
		return getGgbApi().getExerciseFraction();
	}

	public String getCommandString(String objName, boolean localize) {
		return getGgbApi().getCommandString(objName, localize);
	}

	public boolean getGridVisible(int view) {
		return getGgbApi().getGridVisible(view);
	}

	public boolean getGridVisible() {
		return getGgbApi().getGridVisible();
	}

	public int getLabelStyle(String objName) {
		return getGgbApi().getLabelStyle(objName);
	}

	public String getCaption(String objName, boolean subst) {
		return getGgbApi().getCaption(objName, subst);
	}

	public void setCaption(String objName, String caption) {
		getGgbApi().setCaption(objName, caption);
	}

	public String getPerspectiveXML() {
		return getGgbApi().getPerspectiveXML();
	}

	public String getVersion() {
		return getGgbApi().getVersion();
	}

	public double getFrameRate() {
		return getGgbApi().getFrameRate();
	}

	public String getDefinitionString(String objName, boolean localize) {
		return getGgbApi().getDefinitionString(objName, localize);
	}

	public void setEnableCAS(boolean enable) {
		getGgbApi().setEnableCAS(enable);
	}

	public void setEnable3D(boolean enable) {
		getGgbApi().setEnable3D(enable);
	}

	private JavaScriptAPI getGgbApi() {
		return getAppletImplementation().getGgbApi();
	}

	public void enableRightClick(boolean enable) {
		getGgbApi().enableRightClick(enable);

	}

	public void enableLabelDrags(boolean enable) {
		getGgbApi().enableLabelDrags(enable);

	}

	public void enableShiftDragZoom(boolean enable) {
		getGgbApi().enableShiftDragZoom(enable);

	}
}
