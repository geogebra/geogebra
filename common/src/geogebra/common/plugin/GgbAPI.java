package geogebra.common.plugin;

import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.TreeSet;

import geogebra.common.GeoGebraConstants;
import geogebra.common.euclidian.AbstractEuclidianView;
import geogebra.common.kernel.Construction;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.arithmetic.ExpressionNodeConstants.StringType;
import geogebra.common.kernel.cas.GeoGebraCasInterface;
import geogebra.common.kernel.commands.AlgebraProcessor;
import geogebra.common.kernel.geos.GeoBoolean;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoNumeric;
import geogebra.common.kernel.geos.GeoPoint2;
import geogebra.common.kernel.geos.GeoText;
import geogebra.common.kernel.geos.GeoVector;
import geogebra.common.kernel.geos.PointProperties;
import geogebra.common.kernel.geos.Traceable;
import geogebra.common.main.AbstractApplication;


public abstract class GgbAPI {
	// /// ----- Properties ----- /////

	protected Kernel kernel = null;
	protected Construction construction = null;
	protected AlgebraProcessor algebraprocessor = null;
	protected AbstractApplication app = null;

	// private PluginManager pluginmanager= null;
	// /// ----- Interface ----- /////
	/** Returns reference to Construction */
	public Construction getConstruction() {
		return this.construction;
	}

	/** Returns reference to Kernel */
	public Kernel getKernel() {
		return this.kernel;
	}

	/** Returns reference to AlgebraProcessor */
	public AlgebraProcessor getAlgebraProcessor() {
		return this.algebraprocessor;
	}

	/**
	 * Evaluates the given XML string and changes the current construction.
	 * Note: the construction is NOT cleared before evaluating the XML string.
	 */
	public synchronized void evalXML(String xmlString) {
		StringBuilder sb = new StringBuilder();

		sb.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n");
		sb.append("<geogebra format=\"" + GeoGebraConstants.XML_FILE_FORMAT
				+ "\">\n");
		sb.append("<construction>\n");
		sb.append(xmlString);
		sb.append("</construction>\n");
		sb.append("</geogebra>\n");
		getApplication().setXML(sb.toString(), false);
	}

	/**
	 * Evaluates the given string as if it was entered into GeoGebra's input
	 * text field.
	 */
	public synchronized boolean evalCommand(String cmdString) {

		// Application.debug("evalCommand called..."+cmdString);
		GeoElement[] result;

		if (cmdString.indexOf('\n') == -1) {
			result = kernel.getAlgebraProcessor().processAlgebraCommand(
					cmdString, false);
			// return success
			return result != null;

		}

		boolean ret = true;
		String[] cmdStrings = cmdString.split("[\\n]+");
		for (int i = 0; i < cmdStrings.length; i++) {
			result = kernel.getAlgebraProcessor().processAlgebraCommand(
					cmdStrings[i], false);
			ret = ret & (result != null);
		}

		return ret;
	}

	/**
	 * Evaluates the given string as if it was entered into GeoGebra CAS's input
	 * text field.
	 * 
	 * @return evaluation result in GeoGebraCAS syntax
	 */
	public synchronized String evalGeoGebraCAS(String cmdString) {
		return evalGeoGebraCAS(cmdString, false);
	}

	/**
	 * Evaluates the given string as if it was entered into GeoGebra CAS's input
	 * text field.
	 * 
	 * @param debugOutput
	 *            states whether debugging information should be printed to the
	 *            console
	 * @return evaluation result in GeoGebraCAS syntax
	 */
	public synchronized String evalGeoGebraCAS(String cmdString,
			boolean debugOutput) {
		String ret = "";
		GeoGebraCasInterface ggbcas = kernel.getGeoGebraCAS();
		try {
			ret = ggbcas.evaluateGeoGebraCAS(cmdString);
		} catch (Throwable t) {
			AbstractApplication.debug(t.toString());
		}// try-catch

		// useful for debugging JavaScript
		if (debugOutput)
			AbstractApplication.debug("evalGeoGebraCAS\n input:" + cmdString
					+ "\n" + "output: " + ret);
		return ret;
	}// evalGeoGebraCAS(String)

	/**
	 * prints a string to the Java Console
	 */
	public synchronized void debug(String string) {

		AbstractApplication.debug(string);
	}

	/**
	 * Returns the GeoGebra XML string for the given GeoElement object, i.e.
	 * only the <element> tag is returned.
	 */
	public synchronized String getXML(String objName) {
		GeoElement geo = kernel.lookupLabel(objName);
		if (geo == null) {
			return "";
		}
		// if (geo.isIndependent()) removed as we want a way to get the
		// <element> tag for all objects
		return geo.getXML();
		// else
		// return "";
	}

	/**
	 * For a dependent GeoElement objName the XML string of the parent algorithm
	 * and all its output objects is returned. For a free GeoElement objName ""
	 * is returned.
	 */
	public synchronized String getAlgorithmXML(String objName) {
		GeoElement geo = kernel.lookupLabel(objName);
		if (geo == null) {
			return "";
		}
		if (geo.isIndependent()) {
			return "";
		}
		return geo.getParentAlgorithm().getXML();
	}

	/**
	 * Shows or hides the object with the given name in the geometry window.
	 */
	public synchronized void setVisible(String objName, boolean visible) {
		GeoElement geo = kernel.lookupLabel(objName);
		if (geo == null)
			return;
		geo.setEuclidianVisible(visible);
		geo.updateRepaint();
	}

	/**
	 * Shows or hides the object with the given name in the geometry window.
	 */
	public synchronized boolean getVisible(String objName) {
		GeoElement geo = kernel.lookupLabel(objName);
		if (geo == null)
			return false;
		return (geo.isEuclidianVisible());
	}

	/**
	 * Sets the layer of the object with the given name in the geometry window.
	 * Michael Borcherds 2008-02-27
	 */
	public synchronized void setLayer(String objName, int layer) {
		GeoElement geo = kernel.lookupLabel(objName);
		if (geo == null)
			return;
		geo.setLayer(layer);
		geo.updateRepaint();
	}

	/**
	 * Returns the layer of the object with the given name in the geometry
	 * window. returns layer, or -1 if object doesn't exist Michael Borcherds
	 * 2008-02-27
	 */
	public synchronized int getLayer(String objName) {
		GeoElement geo = kernel.lookupLabel(objName);
		if (geo == null)
			return -1;
		return geo.getLayer();
	}

	/**
	 * Shows or hides a complete layer Michael Borcherds 2008-02-27
	 */
	public synchronized void setLayerVisible(int layer, boolean visible) {
		if (layer < 0 || layer > EuclidianStyleConstants.MAX_LAYERS)
			return;
		String[] names = getObjNames();
		for (int i = 0; i < names.length; i++) {
			GeoElement geo = kernel.lookupLabel(names[i]);
			if (geo != null)
				if (geo.getLayer() == layer) {
					geo.setEuclidianVisible(visible);
					geo.updateRepaint();
				}
		}
	}

	private String[] objNames;
	public int lastGeoElementsIteratorSize = 0; // ulven 29.05.08: Had to change
												// to public, used by applet

	/**
	 * 
	 * @return
	 */
	public String[] getObjNames() { // ulven 29.05.08: Had to change to public,
									// used by applet

		Construction cons = kernel.getConstruction();
		TreeSet<GeoElement> geoSet = cons.getGeoSetConstructionOrder();
		int size = geoSet.size();

		/*
		 * removed Michael Borcherds 2009-02-09 BUG!
		 * 
		 * // don't build objNames if nothing changed if (size ==
		 * lastGeoElementsIteratorSize) return objNames;
		 */

		// build objNames array
		lastGeoElementsIteratorSize = size;
		objNames = new String[size];

		int i = 0;
		Iterator<GeoElement> it = geoSet.iterator();
		while (it.hasNext()) {
			GeoElement geo = it.next();
			objNames[i] = geo.getLabelSimple();
			i++;
		}
		return objNames;

	}

	/**
	 * Returns an array with all object names.
	 */
	public synchronized String[] getAllObjectNames() {
		return getObjNames();
	}

	/**
	 * Sets the fixed state of the object with the given name.
	 */
	public synchronized void setFixed(String objName, boolean flag) {
		GeoElement geo = kernel.lookupLabel(objName);
		if (geo != null && geo.isFixable()) {
			geo.setFixed(flag);
			geo.updateRepaint();
		}
	}

	/**
	 * Turns the trace of the object with the given name on or off.
	 */
	public synchronized void setTrace(String objName, boolean flag) {
		GeoElement geo = kernel.lookupLabel(objName);
		if (geo != null && geo.isTraceable()) {
			((Traceable) geo).setTrace(flag);
			geo.updateRepaint();
		}
	}

	/**
	 * Shows or hides the label of the object with the given name in the
	 * geometry window.
	 */
	public synchronized void setLabelVisible(String objName, boolean visible) {
		GeoElement geo = kernel.lookupLabel(objName);
		if (geo == null)
			return;
		geo.setLabelVisible(visible);
		geo.updateRepaint();
	}

	/**
	 * Sets the label style of the object with the given name in the geometry
	 * window. Possible label styles are NAME = 0, NAME_VALUE = 1 and VALUE = 2.
	 */
	public synchronized void setLabelStyle(String objName, int style) {
		GeoElement geo = kernel.lookupLabel(objName);
		if (geo == null)
			return;
		geo.setLabelMode(style);
		geo.updateRepaint();
	}

	/**
	 * Shows or hides the label of the object with the given name in the
	 * geometry window.
	 */
	public synchronized void setLabelMode(String objName, boolean visible) {
		GeoElement geo = kernel.lookupLabel(objName);
		if (geo == null)
			return;
		geo.setLabelVisible(visible);
		geo.updateRepaint();
	}

	/**
	 * Sets the color of the object with the given name.
	 */
	public synchronized void setColor(String objName, int red, int green,
			int blue) {
		GeoElement geo = kernel.lookupLabel(objName);
		if (geo == null)
			return;
		geo.setObjColor(geogebra.common.factories.AwtFactory.prototype
				.newColor(red, green, blue));
		geo.updateRepaint();
	}

	/**
	 * Starts/stops an object animating
	 */
	public void setAnimating(String objName, boolean animate) {
		GeoElement geo = kernel.lookupLabel(objName);
		if (geo != null)
			geo.setAnimating(animate);
	}

	/**
	 * Sets the animation speed of an object
	 */
	public void setAnimationSpeed(String objName, double speed) {
		GeoElement geo = kernel.lookupLabel(objName);
		if (geo != null) {
			geo.setAnimationSpeed(speed);
		}
	}

	/**
	 * Returns the color of the object as an hex string. Note that the
	 * hex-string starts with # and uses upper case letters, e.g. "#FF0000" for
	 * red.
	 */
	public synchronized String getColor(String objName) {
		GeoElement geo = kernel.lookupLabel(objName);
		if (geo == null)
			return "";
		return "#"
				+ geogebra.common.util.StringUtil.toHexString(geo
						.getObjectColor());
	}

	public synchronized int getLineThickness(String objName) {
		GeoElement geo = kernel.lookupLabel(objName);
		if (geo == null)
			return -1;
		return geo.getLineThickness();
	}

	public synchronized void setLineThickness(String objName, int thickness) {
		if (thickness == -1)
			thickness = EuclidianStyleConstants.DEFAULT_LINE_THICKNESS;
		if (thickness < 1 || thickness > 13)
			return;
		GeoElement geo = kernel.lookupLabel(objName);
		if (geo == null)
			return;
		geo.setLineThickness(thickness);
		geo.updateRepaint();
	}

	public synchronized int getPointStyle(String objName) {
		GeoElement geo = kernel.lookupLabel(objName);
		if (geo == null) {
			return -1;
		}
		if (geo.isGeoPoint()) {
			return ((GeoPoint2) geo).getPointStyle();
		}
		return -1;
	}

	public synchronized void setPointStyle(String objName, int style) {
		GeoElement geo = kernel.lookupLabel(objName);
		if (geo == null)
			return;
		if (geo instanceof PointProperties) {
			((PointProperties) geo).setPointStyle(style);
			geo.updateRepaint();
		}
	}

	public synchronized int getPointSize(String objName) {
		GeoElement geo = kernel.lookupLabel(objName);
		if (geo == null) {
			return -1;
		}
		if (geo.isGeoPoint()) {
			return ((GeoPoint2) geo).getPointSize();
		}
		return -1;
	}

	public synchronized void setPointSize(String objName, int style) {
		if (style < 1 || style > 9)
			return;
		GeoElement geo = kernel.lookupLabel(objName);
		if (geo == null)
			return;
		if (geo.isGeoPoint()) {
			((GeoPoint2) geo).setPointSize(style);
			geo.updateRepaint();
		}
	}

	public synchronized double getFilling(String objName) {
		GeoElement geo = kernel.lookupLabel(objName);
		if (geo == null)
			return -1;
		return geo.getAlphaValue();
	}

	public synchronized void setFilling(String objName, double filling) {
		if (filling < 0.0 || filling > 1.0)
			return;

		GeoElement geo = kernel.lookupLabel(objName);
		if (geo == null)
			return;

		geo.setAlphaValue((float) filling);
		geo.updateRepaint();
	}
	public void setOnTheFlyPointCreationActive(boolean flag) {
		app.setOnTheFlyPointCreationActive(flag);
		
	}

	public void setUndoPoint() {
		kernel.getConstruction().storeUndoInfo();
	}

	/*
	 * should only be used by web
	 */
	public void initCAS() {
 		if (app.isHTML5Applet()) {
 			kernel.getGeoGebraCAS().getCurrentCAS().initCAS();
			kernel.refreshCASCommands();
			app.getActiveEuclidianView().repaintView();
 		}
	}

	public void startAnimation() {
		kernel.getAnimatonManager().startAnimation();		
	}

	public void stopAnimation() {
		kernel.getAnimatonManager().stopAnimation();		
	}

	public void hideCursorWhenDragging(boolean hideCursorWhenDragging) {
		kernel.getApplication()
		.setUseTransparentCursorWhenDragging(hideCursorWhenDragging);
	}

	public boolean isAnimationRunning() {
		return kernel.getAnimatonManager().isRunning();
	}
	
	public synchronized void registerAddListener(String JSFunctionName) {
		app.getScriptManager().registerAddListener(JSFunctionName);
	}

	public synchronized void unregisterAddListener(String JSFunctionName) {
		app.getScriptManager().unregisterAddListener(JSFunctionName);
	}
	
	public synchronized void registerRemoveListener(String JSFunctionName) {
		app.getScriptManager().registerRemoveListener(JSFunctionName);
	}
	
	public synchronized void unregisterRemoveListener(String JSFunctionName) {
		app.getScriptManager().unregisterRemoveListener(JSFunctionName);
	}
	
	public synchronized void registerClearListener(String JSFunctionName) {
		app.getScriptManager().registerClearListener(JSFunctionName);
	}

	public synchronized void unregisterClearListener(String JSFunctionName) {
		app.getScriptManager().unregisterClearListener(JSFunctionName);
	}

	public synchronized void registerRenameListener(String JSFunctionName) {
		app.getScriptManager().registerRenameListener(JSFunctionName);
	}
	
	public synchronized void unregisterRenameListener(String JSFunctionName) {
		app.getScriptManager().unregisterRenameListener(JSFunctionName);
	}
	
	public synchronized void registerUpdateListener(String JSFunctionName) {
		app.getScriptManager().registerUpdateListener(JSFunctionName);
	}
	
	public synchronized void unregisterUpdateListener(String JSFunctionName) {
		app.getScriptManager().unregisterUpdateListener(JSFunctionName);
	}

	public synchronized void registerObjectUpdateListener(String objName, String JSFunctionName) {
		app.getScriptManager().registerObjectUpdateListener(objName, JSFunctionName);
	}
	
	public synchronized void unregisterObjectUpdateListener(String objName) {
		app.getScriptManager().unregisterObjectUpdateListener(objName);
	}
	
	public synchronized void registerPenListener(String JSFunctionName) {
		app.getScriptManager().registerPenListener(JSFunctionName);
	}
	
	public synchronized void unregisterPenListener(String JSFunctionName) {
		app.getScriptManager().unregisterPenListener(JSFunctionName);
	}

	public boolean isMoveable(String objName) {
		GeoElement geo = kernel.lookupLabel(objName);
		if (geo == null) 
			return false;
		else
			return geo.isMoveable();
	}
	
	/**
	 * Returns the type of the object with the given name as a string (e.g. point, line, circle, ...)
	 */
	public synchronized String getObjectType(String objName) {
		GeoElement geo = kernel.lookupLabel(objName);
		return (geo == null) ? "" : app.toLowerCase(geo.getObjectType());
	}
	
	/**
	 * Sets the mode of the geometry window (EuclidianView). 
	 */
	public synchronized void setMode(int mode) {
		app.setMode(mode);
	}
	
	public synchronized int getLineStyle(String objName) {
		GeoElement geo = kernel.lookupLabel(objName);
		if (geo == null) return -1;		
		int type = geo.getLineType();	
		
		// convert from 0,10,15,20,30
		// to 0,1,2,3,4
		
		Integer[] types = AbstractEuclidianView.getLineTypes();
		for (int i = 0 ; i < types.length ; i++) {
			if (type == types[i].intValue())
				return i;
		}
		
		return -1; // unknown type
	}	
	
	public synchronized void setLineStyle(String objName, int style) {
		Integer[] types = AbstractEuclidianView.getLineTypes();
		
		if (style < 0 || style >= types.length)
			return;
		
		GeoElement geo = kernel.lookupLabel(objName);
		if (geo == null) return;		
		
		geo.setLineType(types[style].intValue());
		geo.updateRepaint();
	}	
	
	/**
	 * Deletes the object with the given name.
	 */
	public synchronized void deleteObject(String objName) {			
		GeoElement geo = kernel.lookupLabel(objName);
		if (geo == null) return;		
		geo.remove();
		kernel.notifyRepaint();
	}	
	
	/**
	 * Renames an object from oldName to newName.
	 * @return whether renaming worked
	 */
	public synchronized boolean renameObject(String oldName, String newName) {		
		GeoElement geo = kernel.lookupLabel(oldName);
		if (geo == null) 
			return false;
		
		// try to rename
		boolean success = geo.rename(newName);
		kernel.notifyRepaint();
		
		return success;
	}	
	
	/**
	 * Returns true if the object with the given name exists.
	 */
	public synchronized boolean exists(String objName) {			
		GeoElement geo = kernel.lookupLabel(objName);
		return (geo != null);				
	}	
	
	/**
	 * Returns true if the object with the given name has a vaild
	 * value at the moment.
	 */
	public synchronized boolean isDefined(String objName) {			
		GeoElement geo = kernel.lookupLabel(objName);
		if (geo == null) 
			return false;
		else
			return geo.isDefined();
	}	
	
	/**
	 * Returns true if the object with the given name is independent.
	 */
	public synchronized boolean isIndependent(String objName) {			
		GeoElement geo = kernel.lookupLabel(objName);
		if (geo == null) 
			return false;
		else
			return geo.isIndependent();
	}	
	
	/**
	 * Returns the value of the object with the given name as a string.
	 */
	public synchronized String getValueString(String objName) {
		GeoElement geo = kernel.lookupLabel(objName);
		if (geo == null) return "";	
		
		if (geo.isGeoText())
			return ((GeoText)geo).getTextString();
		
		return geo.getAlgebraDescription();
	}
	
	/**
	 * Returns the definition of the object with the given name as a string.
	 */
	public synchronized String getDefinitionString(String objName) {
		GeoElement geo = kernel.lookupLabel(objName);
		if (geo == null) return "";		
		return geo.getDefinitionDescription();
	}
	
	/**
	 * Returns the command of the object with the given name as a string.
	 */
	public synchronized String getCommandString(String objName) {		
		GeoElement geo = kernel.lookupLabel(objName);
		if (geo == null) return "";		
		return geo.getCommandDescription(StringTemplate.get(StringType.GEOGEBRA));
	}
	
	/**
	 * Returns the x-coord of the object with the given name. Note: returns 0 if
	 * the object is not a point or a vector.
	 */
	public synchronized double getXcoord(String objName) {
		GeoElement geo = kernel.lookupLabel(objName);
		if (geo == null) return 0;
		
		if (geo.isGeoPoint())
			return ((GeoPoint2) geo).inhomX;
		else if (geo.isGeoVector())
			return ((GeoVector) geo).x;
		else
			return 0;
	}
	
	/**
	 * Returns the y-coord of the object with the given name. Note: returns 0 if
	 * the object is not a point or a vector.
	 */
	public synchronized double getYcoord(String objName) {
		GeoElement geo = kernel.lookupLabel(objName);
		if (geo == null) return 0;
		
		if (geo.isGeoPoint())
			return ((GeoPoint2) geo).inhomY;
		else if (geo.isGeoVector())
			return ((GeoVector) geo).y;
		else
			return 0;
	}
	
	/**
	 * Sets the coordinates of the object with the given name. Note: if the
	 * specified object is not a point or a vector, nothing happens.
	 */
	public synchronized void setCoords(String objName, double x, double y) {
		GeoElement geo = kernel.lookupLabel(objName);
		if (geo == null) return;
		
		if (geo.isGeoPoint()) {
			((GeoPoint2) geo).setCoords(x, y, 1);
			geo.updateRepaint();
		}
		else if (geo.isGeoVector()) {
			((GeoVector) geo).setCoords(x, y, 0);
			geo.updateRepaint();
		}
	}
	
	/**
	 * Returns the double value of the object with the given name.
	 * For a boolean, returns 0 for false, 1 for true
	 * Note: returns 0 if the object does not have a value.
	 */
	public synchronized double getValue(String objName) {
		GeoElement geo = kernel.lookupLabel(objName);
		if (geo == null)
			return 0;
		
		if (geo.isNumberValue())
			return ((NumberValue) geo).getDouble();		
		else if (geo.isGeoBoolean())
			return ((GeoBoolean) geo).getBoolean() ? 1 : 0;		
		
		return 0;
	}
	
	/**
	 * Sets the double value of the object with the given name.
	 * For a boolean 0 -> false, any other value -> true
	 * Note: if the specified object is not a number, nothing happens.
	 */
	public synchronized void setValue(String objName, double x) {
		GeoElement geo = kernel.lookupLabel(objName);
		if (geo == null || !geo.isIndependent()) return;
		
		if (geo.isGeoNumeric()) {
			((GeoNumeric) geo).setValue(x);
			geo.updateRepaint();
		} else if (geo.isGeoBoolean()) {
			((GeoBoolean) geo).setValue(Kernel.isZero(x) ? false : true);
			geo.updateRepaint();
		}
	}
	
	/**
	 * Turns the repainting of all views on or off.
	 */
	public synchronized void setRepaintingActive(boolean flag) {		
		//Application.debug("set repainting: " + flag);
		kernel.setNotifyRepaintActive(flag);
	}	
	

	/*
	 * Methods to change the geometry window's properties	 
	 */
	
	/**
	 * Sets the Cartesian coordinate system in the graphics window.
	 */
	public synchronized void setCoordSystem(double xmin, double xmax, double ymin, double ymax) {
		app.getEuclidianView1().setRealWorldCoordSystem(xmin, xmax, ymin, ymax);
	}
	
	/**
	 * Shows or hides the x- and y-axis of the coordinate system in the graphics window.
	 */
	public synchronized void setAxesVisible(boolean xVisible, boolean yVisible) {		
		app.getEuclidianView1().setShowAxis(AbstractEuclidianView.AXIS_X, xVisible, false);
		app.getEuclidianView1().setShowAxis(AbstractEuclidianView.AXIS_Y, yVisible, false);
		kernel.notifyRepaint();
	}	
	
	/**
	 * If the origin is off screen and the axes are visible, GeoGebra shows coordinates
	 * of the upper-left and bottom-right screen corner. This method lets you
	 * hide these corner coordinates.
	 */
	public synchronized void setAxesCornerCoordsVisible(boolean showAxesCornerCoords) {		
		app.getEuclidianView1().setAxesCornerCoordsVisible(showAxesCornerCoords);
		if(app.hasEuclidianView2()){
			app.getEuclidianView2().setAxesCornerCoordsVisible(showAxesCornerCoords);
		}
	}	
	
	/**
	 * Shows or hides the coordinate grid in the graphics window.
	 */
	public synchronized void setGridVisible(boolean flag) {		
		app.getSettings().getEuclidian(1).showGrid(flag);
		app.getSettings().getEuclidian(2).showGrid(flag);
	}
	
	/*
	 * Methods to get all object names of the construction 
	 */
	
		
	/**
	 * Returns an array with the names of all selected objects.
	 */
	public synchronized String [] getSelectedObjectNames() {			
		ArrayList<GeoElement> selGeos = app.getSelectedGeos();
		String [] objNames = new String[selGeos.size()];
		
		for (int i=0; i < selGeos.size(); i++) {
			GeoElement geo = (GeoElement) selGeos.get(i);
			objNames[i] = geo.getLabel();
		}
		return objNames;
	}	
	
	/**
	 * Returns the number of objects in the construction.
	 */
	public synchronized int getObjectNumber() {					
		return getObjNames().length;			
	}	
	
	/**
	 * Returns the name of the n-th object of this construction.
	 */
	public synchronized String getObjectName(int i) {					
		String [] names = getObjNames();
					
		try {
			return names[i];
		} catch (Exception e) {
			return "";
		}
	}
	
	/**
	 * Opens construction given in XML format. May be used for loading constructions.
	 */
	public synchronized void setXML(String xml) {
		app.setXML(xml, true);
	}
	
	/**
	 * Returns current construction in XML format. May be used for saving.
	 */
	public synchronized String getXML() {
		return app.getXML();
	}
	
    final public AbstractApplication getApplication() {
	    return app;
    }
	

}
