package geogebra.common.plugin;

import java.util.Iterator;
import java.util.TreeSet;

import geogebra.common.GeoGebraConstants;
import geogebra.common.kernel.Construction;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.cas.GeoGebraCasInterface;
import geogebra.common.kernel.commands.AlgebraProcessor;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoPoint2;
import geogebra.common.kernel.geos.PointProperties;
import geogebra.common.kernel.geos.Traceable;
import geogebra.common.main.AbstractApplication;

public abstract class GgbAPI {
	// /// ----- Properties ----- /////

	protected Kernel kernel = null;
	protected Construction construction = null;
	protected AlgebraProcessor algebraprocessor = null;

	// private PluginManager pluginmanager= null;
	// /// ----- Interface ----- /////
	abstract public AbstractApplication getApplication();

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
			objNames[i] = geo.getLabel();
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

}
