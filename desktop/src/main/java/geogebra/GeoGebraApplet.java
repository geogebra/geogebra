package geogebra;

import java.awt.Graphics;

import javax.swing.JApplet;
import javax.swing.SwingUtilities;

// Wrapper for backward compatibility for old JNLP based applets.

class GeoGebraApplet extends JApplet {
	
	private static final long serialVersionUID = 652840263046810738L;

	org.geogebra.desktop.GeoGebraApplet applet;
	
	@Override
	final public void paint(Graphics g) {
		applet.paint(g);
	}

	@Override
	public void init() {
		applet = new org.geogebra.desktop.GeoGebraApplet();
		applet.init();
	}
	
	@Override
	public void start() {
		applet.start();
	}

	@Override
	public void stop() {
		applet.stop();
	}

	@Override
	public void destroy() {
		applet.destroy();
	}

	/*
	 * JAVASCRIPT interface
	 */

	public synchronized void deleteObject(String objName) {
		applet.getAppletImplementation().deleteObject(objName);
	}

	public synchronized boolean evalCommand(final String cmdString) {
		return applet.evalCommand(cmdString, true);
	}

	public synchronized String evalCommandCAS(final String cmdString) {
		return applet.getAppletImplementation().evalCommandCAS(cmdString);
	}

	public synchronized String evalGeoGebraCAS(final String cmdString) {
		return applet.getAppletImplementation().evalGeoGebraCAS(cmdString);
	}

	public synchronized void evalXML(String xmlString) {
		applet.getAppletImplementation().evalXML(xmlString);
	}

	public synchronized void debug(String string) {
		applet.getAppletImplementation().debug(string);
	}

	public synchronized boolean exists(String objName) {
		return applet.getAppletImplementation().exists(objName);
	}

	public synchronized String[] getAllObjectNames() {
		return applet.getAppletImplementation().getAllObjectNames();
	}

	public synchronized String getColor(String objName) {
		return applet.getAppletImplementation().getColor(objName);
	}

	public synchronized double getFilling(String objName) {
		return applet.getAppletImplementation().getFilling(objName);
	}

	public synchronized int getPointStyle(String objName) {
		return applet.getAppletImplementation().getPointStyle(objName);
	}

	public synchronized int getPointSize(String objName) {
		return applet.getAppletImplementation().getPointSize(objName);
	}

	public synchronized int getLineStyle(String objName) {
		return applet.getAppletImplementation().getLineStyle(objName);
	}

	public synchronized int getLineThickness(String objName) {
		return applet.getAppletImplementation().getLineThickness(objName);
	}

	public synchronized String getCommandString(String objName) {
		return applet.getAppletImplementation().getCommandString(objName);
	}

	public synchronized String getDefinitionString(String objName) {
		return applet.getAppletImplementation().getDefinitionString(objName);
	}

	public synchronized byte[] getGGBfile() {
		return applet.getAppletImplementation().getGGBfile();
	}

	public synchronized String getHostname() {
		return applet.getAppletImplementation().getHostname();
	}

	public synchronized String getIPAddress() {
		return applet.getAppletImplementation().getIPAddress();
	}

	public synchronized int getLayer(String objName) {
		return applet.getAppletImplementation().getLayer(objName);
	}

	public synchronized String getObjectName(int i) {
		return applet.getAppletImplementation().getObjectName(i);
	}

	public synchronized String getPNGBase64(double exportScale,
			boolean transparent, double DPI) {
		return applet.getAppletImplementation().getPNGBase64(exportScale, transparent,
				DPI);
	}

	public synchronized boolean writePNGtoFile(String filename,
			double exportScale, boolean transparent, double DPI) {
		return applet.getAppletImplementation().writePNGtoFile(filename, exportScale,
				transparent, DPI);
	}

	public synchronized int getObjectNumber() {
		return applet.getAppletImplementation().getObjectNumber();
	}

	public synchronized String getObjectType(String objName) {
		return applet.getAppletImplementation().getObjectType(objName);
	}

	public synchronized double getValue(String objName) {
		return applet.getAppletImplementation().getValue(objName);
	}

	public synchronized String getValueString(String objName) {
		return applet.getAppletImplementation().getValueString(objName);
	}

	public synchronized String getXML() {
		return applet.getAppletImplementation().getXML();
	}

	public synchronized String getBase64() {
		return applet.getAppletImplementation().getBase64(false);
	}

	public synchronized String getBase64(boolean includeThumbnail) {
		return applet.getAppletImplementation().getBase64(includeThumbnail);
	}

	public synchronized String getXML(String objName) {
		return applet.getAppletImplementation().getXML(objName);
	}

	public synchronized String getAlgorithmXML(String objName) {
		return applet.getAppletImplementation().getAlgorithmXML(objName);
	}

	public synchronized double getXcoord(String objName) {
		return applet.getAppletImplementation().getXcoord(objName);
	}

	public synchronized double getYcoord(String objName) {
		return applet.getAppletImplementation().getYcoord(objName);
	}

	public synchronized boolean isDefined(String objName) {
		return applet.getAppletImplementation().isDefined(objName);
	}

	public synchronized boolean isIndependent(String objName) {
		return applet.getAppletImplementation().isIndependent(objName);
	}

	public synchronized void openFile(String strURL) {
		applet.getAppletImplementation().openFile(strURL);
	}

	public synchronized void refreshViews() {
		applet.getAppletImplementation().refreshViews();
	}

	public synchronized void registerAddListener(String JSFunctionName) {
		applet.getAppletImplementation().registerAddListener(JSFunctionName);
	}

	public synchronized void registerClearListener(String JSFunctionName) {
		applet.getAppletImplementation().registerClearListener(JSFunctionName);
	}

	public synchronized void registerObjectUpdateListener(String objName,
			String JSFunctionName) {
		applet.getAppletImplementation().registerObjectUpdateListener(objName,
				JSFunctionName);
	}

	public synchronized void registerObjectClickListener(String objName,
			String JSFunctionName) {
		applet.getAppletImplementation().registerObjectClickListener(objName,
				JSFunctionName);
	}

	public synchronized void registerRemoveListener(String JSFunctionName) {
		applet.getAppletImplementation().registerRemoveListener(JSFunctionName);
	}

	public synchronized void registerRenameListener(String JSFunctionName) {
		applet.getAppletImplementation().registerRenameListener(JSFunctionName);
	}

	public synchronized void registerUpdateListener(String JSFunctionName) {
		applet.getAppletImplementation().registerUpdateListener(JSFunctionName);
	}

	public synchronized void registerClickListener(String JSFunctionName) {
		applet.getAppletImplementation().registerClickListener(JSFunctionName);
	}

	public synchronized boolean renameObject(String oldObjName,
			String newObjName) {
		return applet.getAppletImplementation().renameObject(oldObjName, newObjName);
	}

	public synchronized void setAnimating(String objName, boolean animate) {
		applet.getAppletImplementation().setAnimating(objName, animate);
	}

	public synchronized void setAnimationSpeed(String objName, double speed) {
		applet.getAppletImplementation().setAnimationSpeed(objName, speed);
	}

	public synchronized void startAnimation() {
		applet.getAppletImplementation().startAnimation();
	}

	public synchronized void stopAnimation() {
		applet.getAppletImplementation().stopAnimation();
	}

	public void hideCursorWhenDragging(boolean hideCursorWhenDragging) {
		applet.getAppletImplementation()
				.hideCursorWhenDragging(hideCursorWhenDragging);
	}

	public synchronized boolean isAnimationRunning() {
		return applet.getAppletImplementation().isAnimationRunning();
	}

	public synchronized void reset() {
		applet.getAppletImplementation().reset();
	}

	public synchronized void setAxesVisible(boolean xVisible, boolean yVisible) {
		applet.getAppletImplementation().setAxesVisible(xVisible, yVisible);
	}

	public synchronized void setColor(String objName, int red, int green,
			int blue) {
		applet.getAppletImplementation().setColor(objName, red, green, blue);
	}

	public synchronized void setCorner(String objName, double x, double y,
			int index) {
		applet.getAppletImplementation().setCorner(objName, x, y, index);
	}

	public synchronized void setCorner(String objName, double x, double y) {
		applet.getAppletImplementation().setCorner(objName, x, y);
	}

	public synchronized void setLineThickness(String objName, int thickness) {
		applet.getAppletImplementation().setLineThickness(objName, thickness);
	}

	public synchronized void setLineStyle(String objName, int style) {
		applet.getAppletImplementation().setLineStyle(objName, style);
	}

	public synchronized void setFilling(String objName, double filling) {
		applet.getAppletImplementation().setFilling(objName, filling);
	}

	public synchronized String getGraphicsViewCheckSum(String algorithm,
			String format) {
		return applet.getAppletImplementation().getGraphicsViewCheckSum(algorithm,
				format);
	}

	public synchronized void setPointStyle(String objName, int style) {
		applet.getAppletImplementation().setPointStyle(objName, style);
	}

	public void setOnTheFlyPointCreationActive(boolean flag) {
		applet.getAppletImplementation().setOnTheFlyPointCreationActive(flag);
	}

	public void setUndoPoint() {
		applet.getAppletImplementation().setUndoPoint();
	}

	public synchronized void setPointSize(String objName, int style) {
		applet.getAppletImplementation().setPointSize(objName, style);
	}

	public synchronized void setCoordSystem(double xmin, double xmax,
			double ymin, double ymax) {
		applet.getAppletImplementation().setCoordSystem(xmin, xmax, ymin, ymax);
	}

	public synchronized void setCoords(String objName, double x, double y) {
		applet.getAppletImplementation().setCoords(objName, x, y);
	}

	public synchronized void setErrorDialogsActive(boolean flag) {
		applet.getAppletImplementation().setErrorDialogsActive(flag);
	}

	public synchronized void setFixed(String objName, boolean flag) {
		applet.getAppletImplementation().setFixed(objName, flag);
	}

	public synchronized void setGridVisible(boolean flag) {
		applet.getAppletImplementation().setGridVisible(flag);
	}

	public synchronized void setLabelStyle(String objName, int style) {
		applet.getAppletImplementation().setLabelStyle(objName, style);
	}

	public synchronized void setLabelVisible(String objName, boolean visible) {
		applet.getAppletImplementation().setLabelVisible(objName, visible);
	}

	public synchronized void setLayer(String objName, int layer) {
		applet.getAppletImplementation().setLayer(objName, layer);
	}

	public synchronized void setLayerVisible(int layer, boolean visible) {
		applet.getAppletImplementation().setLayerVisible(layer, visible);
	}

	public synchronized void setMode(int mode) {
		applet.getAppletImplementation().setMode(mode);
	}

	public synchronized void setRepaintingActive(boolean flag) {
		applet.getAppletImplementation().setRepaintingActive(flag);
	}

	public synchronized void setTrace(String objName, boolean flag) {
		applet.getAppletImplementation().setTrace(objName, flag);
	}

	public synchronized void setValue(String objName, double x) {
		applet.getAppletImplementation().setValue(objName, x);
	}

	public synchronized void setTextValue(String objName, String x) {
		applet.getAppletImplementation().setTextValue(objName, x);
	}

	public synchronized void setListValue(String objName, double x, double y) {
		applet.getAppletImplementation().setListValue(objName, x, y);
	}

	public synchronized void setVisible(String objName, boolean visible) {
		applet.getAppletImplementation().setVisible(objName, visible);
	}

	public synchronized boolean getVisible(String objName) {
		return applet.getAppletImplementation().getVisible(objName);
	}

	public synchronized void setXML(String xml) {
		applet.getAppletImplementation().setXML(xml);
	}

	public synchronized void setBase64(String base64) {
		applet.getAppletImplementation().setBase64(base64);
	}

	public synchronized void unregisterAddListener(String JSFunctionName) {
		applet.getAppletImplementation().unregisterAddListener(JSFunctionName);
	}

	public synchronized void unregisterClearListener(String JSFunctionName) {
		applet.getAppletImplementation().unregisterClearListener(JSFunctionName);
	}

	public synchronized void unregisterObjectUpdateListener(String objName) {
		applet.getAppletImplementation().unregisterObjectUpdateListener(objName);
	}

	public synchronized void unregisterObjectClickListener(String objName) {
		applet.getAppletImplementation().unregisterObjectClickListener(objName);
	}

	public synchronized void registerStoreUndoListener(String objName) {
		applet.getAppletImplementation().registerStoreUndoListener(objName);
	}

	public synchronized void unregisterRemoveListener(String JSFunctionName) {
		applet.getAppletImplementation().unregisterRemoveListener(JSFunctionName);
	}

	public synchronized void unregisterRenameListener(String JSFunctionName) {
		applet.getAppletImplementation().unregisterRenameListener(JSFunctionName);
	}

	public synchronized void unregisterUpdateListener(String JSFunctionName) {
		applet.getAppletImplementation().unregisterUpdateListener(JSFunctionName);
	}

	public synchronized void unregisterClickListener(String JSFunctionName) {
		applet.getAppletImplementation().unregisterClickListener(JSFunctionName);
	}

	public boolean isMoveable(String objName) {
		return applet.getAppletImplementation().isMoveable(objName);
	}

	public void drawToImage(String label, double[] x, double[] y) {
		applet.getAppletImplementation().drawToImage(label, x, y);
	}

	public void clearImage(String label) {
		applet.getAppletImplementation().clearImage(label);
	}

	public void uploadToGeoGebraTube() {
		applet.getAppletImplementation().uploadToGeoGebraTube();

	}

	public void setPenColor(int red, int green, int blue) {
		applet.getAppletImplementation().setPenColor(red, green, blue);

	}

	public void setPenSize(int size) {
		applet.getAppletImplementation().setPenSize(size);
	}

	public int getPenSize() {
		return applet.getAppletImplementation().getPenSize();
	}

	public String getPenColor() {
		return applet.getAppletImplementation().getPenColor();
	}

	public double getListValue(String objName, int index) {
		return applet.getAppletImplementation().getListValue(objName, index);
	}

	public void registerClientListener(String JSFunctionName) {
		applet.getAppletImplementation().registerClientListener(JSFunctionName);
	}

	public void unregisterClientListener(String JSFunctionName) {
		applet.getAppletImplementation().unregisterClientListener(JSFunctionName);
	}

	public void setPerspective(String code) {
		applet.getAppletImplementation().setPerspective(code);
	}

	public boolean getVisible(String objName, int view) {
		return applet.getAppletImplementation().getVisible(objName, view);
	}

}

