package org.geogebra.common.plugin;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.TreeSet;

import org.geogebra.common.GeoGebraConstants;
import org.geogebra.common.awt.GColor;
import org.geogebra.common.awt.GFont;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.euclidian.EuclidianViewInterfaceCommon;
import org.geogebra.common.euclidian3D.EuclidianView3DInterface;
import org.geogebra.common.gui.toolbar.ToolBar;
import org.geogebra.common.io.layout.Perspective;
import org.geogebra.common.io.layout.PerspectiveDecoder;
import org.geogebra.common.kernel.CircularDefinitionException;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.GeoGebraCasInterface;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.Locateable;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.Matrix.Coords;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.arithmetic.Traversing.CommandCollector;
import org.geogebra.common.kernel.commands.AlgebraProcessor;
import org.geogebra.common.kernel.geos.AbsoluteScreenLocateable;
import org.geogebra.common.kernel.geos.GeoCasCell;
import org.geogebra.common.kernel.geos.GeoConic;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoLine;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.geos.GeoText;
import org.geogebra.common.kernel.geos.PointProperties;
import org.geogebra.common.kernel.geos.TextProperties;
import org.geogebra.common.kernel.geos.Traceable;
import org.geogebra.common.kernel.kernelND.GeoAxisND;
import org.geogebra.common.kernel.kernelND.GeoConicND;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.kernel.scripting.CmdSetCoords;
import org.geogebra.common.kernel.scripting.CmdSetValue;
import org.geogebra.common.main.App;
import org.geogebra.common.main.error.ErrorHelper;
import org.geogebra.common.main.settings.EuclidianSettings;
import org.geogebra.common.util.Assignment.Result;
import org.geogebra.common.util.Exercise;
import org.geogebra.common.util.StringUtil;
import org.geogebra.common.util.debug.Log;

/**
 * <h3>GgbAPI - API for PlugLets</h3>
 * 
 * <pre>
 *    The Api the plugin program can use.
 * </pre>
 * <ul>
 * <h4>Interface:</h4>
 * <li>GgbAPI(Application) //Application owns it
 * <li>getApplication()
 * <li>getKernel()
 * <li>getConstruction()
 * <li>getAlgebraProcessor()
 * <li>getPluginManager()
 * <li>evalCommand(String)
 * <li>and the rest of the methods from the Applet JavaScript/Java interface
 * <li>...
 * </ul>
 * 
 * @author H-P Ulven
 * @version 31.10.08 29.05.08: Tranferred applet interface methods (the relevant
 *          ones) from GeoGebraAppletBase
 */

public abstract class GgbAPI implements JavaScriptAPI {
	// /// ----- Properties ----- /////
	/** kernel */
	protected Kernel kernel = null;
	/** construction */
	protected Construction construction = null;
	/** algebra processor */
	protected AlgebraProcessor algebraprocessor = null;
	/** application */
	protected App app = null;

	// private PluginManager pluginmanager= null;
	// /// ----- Interface ----- /////
	/**
	 * Returns reference to Construction
	 * 
	 * @return construction
	 */
	public Construction getConstruction() {
		return this.construction;
	}

	/**
	 * Returns reference to Kernel
	 * 
	 * @return kernel
	 */
	public Kernel getKernel() {
		return this.kernel;
	}

	/**
	 * Returns reference to AlgebraProcessor
	 * 
	 * @return algebra processor
	 */
	public AlgebraProcessor getAlgebraProcessor() {
		return this.algebraprocessor;
	}

	public void reset() {
		app.reset();
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
	 * Evaluates the given string as if it was entered into GeoGebra's CAS View
	 * (but it won't create any objects etc in GeoGebra)
	 * 
	 * @param cmdString
	 *            input to CAS
	 * @return output from CAS
	 */
	public synchronized String evalCommandCAS(String cmdString) {
		if (!app.getSettings().getCasSettings().isEnabled()) {
			return "?";
		}
		// default (undefined)
		String ret = "?";

		try {
			GeoCasCell f = new GeoCasCell(kernel.getConstruction());
			// kernel.getConstruction().addToConstructionList(f, false);

			f.setInput(cmdString);
			if (f.getInputVE() != null && f.getInputVE().getLabel() != null) {
				kernel.getAlgebraProcessor().checkCasEval(
						f.getInputVE().getLabel(), cmdString, null);
			}
			f.computeOutput();

			boolean includesNumericCommand = false;
			HashSet<Command> commands = new HashSet<Command>();
			
			f.getInputVE().traverse(CommandCollector.getCollector(commands));

			if (!commands.isEmpty()) {
				for (Command cmd : commands) {
					String cmdName = cmd.getName();
					// Numeric used
					includesNumericCommand = includesNumericCommand
							|| ("Numeric".equals(cmdName) && cmd
									.getArgumentNumber() > 1);
				}
			}

			ret = f.getOutputValidExpression() != null ? f
					.getOutputValidExpression().toString(
							StringTemplate.numericDefault) : f
					.getOutput(StringTemplate.testTemplate);
		} catch (Throwable e) {
			e.printStackTrace();
		}

		return ret;

	}

	/**
	 * Evaluates the given string as if it was entered into GeoGebra's input
	 * text field.
	 */
	public synchronized boolean evalCommand(String cmdString) {

		String labels = evalCommandGetLabels(cmdString);

		return labels != null;

	}

	/**
	 * Evaluates the given string as if it was entered into GeoGebra's input
	 * text field.
	 * 
	 * @param cmdString
	 *            command string
	 * @return comma separated labels
	 */
	public synchronized String evalCommandGetLabels(String cmdString) {

		// Application.debug("evalCommand called..."+cmdString);
		GeoElementND[] result;

		// this is new in GeoGebra 4.2 and it will stop some files working
		// but causes problems if the files are opened and edited
		// and in the web project
		boolean oldVal = kernel.isUsingInternalCommandNames();
		kernel.setUseInternalCommandNames(true);

		StringBuilder ret = new StringBuilder();

		if (cmdString.indexOf('\n') == -1) {
			result = kernel.getAlgebraProcessor().processAlgebraCommand(
					cmdString, false);
			// return success
			if (result == null) {
				kernel.setUseInternalCommandNames(oldVal);
				return null;
			}

			for (int i = 0; i < result.length; i++) {
				ret.append(result[i].getLabelSimple());
				ret.append(",");
			}

			if (ret.length() > 0) {
				// remove last comma
				ret.setLength(ret.length() - 1);
			}

			kernel.setUseInternalCommandNames(oldVal);
			return ret.toString();

		}

		String[] cmdStrings = cmdString.split("[\\n]+");
		for (int i = 0; i < cmdStrings.length; i++) {
			result = kernel.getAlgebraProcessor()
					.processAlgebraCommand(cmdStrings[i], false);

			if (result != null) {
				for (int j = 0; j < result.length; j++) {
					ret.append(result[j].getLabelSimple());
					ret.append(",");
				}
			}

		}

		kernel.setUseInternalCommandNames(oldVal);

		if (ret.length() == 0) {
			return null;
		}

		// remove last comma
		ret.setLength(ret.length() - 1);

		return ret.toString();
	}

	/**
	 * prints a string to the Java Console
	 */
	public synchronized void debug(String string) {

		Log.debug(string);
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
		if (geo.isGeoCasCell()) {
			return ((GeoCasCell) geo).getTwinGeo() != null
					&& ((GeoCasCell) geo).getTwinGeo().isEuclidianVisible();
		}
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
	/**
	 * Number of last geo elements
	 */
	public int lastGeoElementsIteratorSize = 0; // ulven 29.05.08: Had to change
												// to public, used by applet

	/**
	 * 
	 * @return object names
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
	 * Sets the fixed state of the object with the given name.
	 * 
	 * @param objName
	 *            object name
	 * @param fixed
	 *            whether it should be fixed
	 * @param selectionAllowed
	 *            whether selection should be allowed
	 */
	public synchronized void setFixed(String objName, boolean fixed,
			boolean selectionAllowed) {
		GeoElement geo = kernel.lookupLabel(objName);
		if (geo != null) {

			geo.setSelectionAllowed(selectionAllowed);

			if (geo.isFixable()) {
				geo.setFixed(fixed);
				geo.updateRepaint();
			}
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

	public synchronized boolean getLabelVisible(String objName) {
		GeoElement geo = kernel.lookupLabel(objName);
		if (geo == null)
			return false;
		return geo.isLabelVisible();
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

	public synchronized int getLabelStyle(String objName) {
		GeoElement geo = kernel.lookupLabel(objName);
		if (geo == null)
			return 0;
		return geo.getLabelMode();
	}

	/**
	 * Sets the color of the object with the given name.
	 */
	public synchronized void setColor(String objName, int red, int green,
			int blue) {
		GeoElement geo = kernel.lookupLabel(objName);
		if (geo == null)
			return;
		geo.setObjColor(GColor.newColor(red, green, blue));
		geo.updateRepaint();
	}

	public synchronized void setCorner(String objName, double x, double y) {
		setCorner(objName, x, y, 1);
	}

	public synchronized void setCorner(String objName, double x, double y,
			int index) {
		GeoElement geo = kernel.lookupLabel(objName);
		if (!(geo instanceof AbsoluteScreenLocateable))
			return;
		AbsoluteScreenLocateable loc = ((AbsoluteScreenLocateable) geo);
		if (loc.isAbsoluteScreenLocActive()) {
			loc.setAbsoluteScreenLoc((int) Math.round(x), (int) Math.round(y));
		} else if (geo instanceof Locateable) {
			GeoPoint corner = new GeoPoint(kernel.getConstruction());
			EuclidianView ev = app.getEuclidianView1();
			if (geo.isVisibleInView(ev.getViewID())
					&& app.hasEuclidianView2EitherShowingOrNot(1)
					&& geo.isVisibleInView(app.getEuclidianView2(1).getViewID())) {
				Log.debug("EV2");
				// ev = app.getEuclidianView2();
			}
			corner.setCoords(ev.toRealWorldCoordX(x), ev.toRealWorldCoordY(y),
					1);
			try {
				((Locateable) loc).setStartPoint(corner, index);
			} catch (CircularDefinitionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
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
				+ org.geogebra.common.util.StringUtil.toHexString(geo
						.getObjectColor());
	}

	public synchronized int getLineThickness(String objName) {
		GeoElement geo = kernel.lookupLabel(objName);
		if (geo == null)
			return -1;
		return geo.getLineThickness();
	}

	public synchronized void setLineThickness(String objName, int lineThickness) {
		int thickness = lineThickness;
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
		if (geo instanceof PointProperties) {
			return ((PointProperties) geo).getPointStyle();
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
		if (geo instanceof PointProperties) {
			return ((PointProperties) geo).getPointSize();
		}
		return -1;
	}

	public synchronized void setPointSize(String objName, int style) {
		if (style < 1 || style > 9)
			return;
		GeoElement geo = kernel.lookupLabel(objName);
		if (geo == null)
			return;
		if (geo instanceof PointProperties) {
			((PointProperties) geo).setPointSize(style);
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

	/**
	 * should only be used by web
	 */
	public void initCAS() {
		if (app.isHTML5Applet()) {
			// kernel.getGeoGebraCAS().getCurrentCAS().initCAS();
			kernel.refreshCASCommands();

			// Don't remove this. It is needed for testing the web platform. (String match is assumed.)
			Log.debug("all CAS up");

			kernel.notifyRepaint();
		}
	}

	public void uploadToGeoGebraTube() {
		app.uploadToGeoGebraTube();
	}

	public void startAnimation() {
		kernel.getAnimatonManager().startAnimation();
	}

	public void stopAnimation() {
		kernel.getAnimatonManager().stopAnimation();
	}

	public void hideCursorWhenDragging(boolean hideCursorWhenDragging) {
		kernel.getApplication().setUseTransparentCursorWhenDragging(
				hideCursorWhenDragging);
	}

	public boolean isAnimationRunning() {
		return kernel.getAnimatonManager().isRunning();
	}

	public double getFrameRate() {
		return kernel.getFrameRate();
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

	public synchronized void registerObjectUpdateListener(String objName,
			String JSFunctionName) {
		app.getScriptManager().registerObjectUpdateListener(objName,
				JSFunctionName);
	}

	public synchronized void unregisterObjectUpdateListener(String objName) {
		app.getScriptManager().unregisterObjectUpdateListener(objName);
	}

	public synchronized void registerClickListener(String JSFunctionName) {
		app.getScriptManager().registerClickListener(JSFunctionName);
	}

	public synchronized void unregisterClickListener(String JSFunctionName) {
		app.getScriptManager().unregisterClickListener(JSFunctionName);
	}

	public void registerClientListener(String JSFunctionName) {
		app.getScriptManager().registerClientListener(JSFunctionName);
	}

	public void unregisterClientListener(String JSFunctionName) {
		app.getScriptManager().unregisterClientListener(JSFunctionName);
	}

	public synchronized void registerObjectClickListener(String objName,
			String JSFunctionName) {
		app.getScriptManager().registerObjectClickListener(objName,
				JSFunctionName);
	}

	public synchronized void unregisterObjectClickListener(String objName) {
		app.getScriptManager().unregisterObjectClickListener(objName);
	}

	public synchronized void registerStoreUndoListener(String JSFunctionName) {
		app.getScriptManager().registerStoreUndoListener(JSFunctionName);
	}

	public boolean isMoveable(String objName) {
		GeoElement geo = kernel.lookupLabel(objName);
		if (geo == null)
			return false;
		return geo.isMoveable();
	}

	/**
	 * Returns the type of the object with the given name as a string (e.g.
	 * point, line, circle, ...)
	 */
	public synchronized String getObjectType(String objName) {
		GeoElement geo = kernel.lookupLabel(objName);
		return (geo == null) ? "" : StringUtil.toLowerCase(geo.getTypeString());
	}

	/**
	 * Sets the mode of the geometry window (EuclidianView).
	 */
	public synchronized void setMode(int mode) {
		app.setMode(mode);
	}

	public synchronized int getMode() {
		return app.getMode();
	}

	public synchronized int getLineStyle(String objName) {
		GeoElement geo = kernel.lookupLabel(objName);
		if (geo == null)
			return -1;
		int type = geo.getLineType();

		// convert from 0,10,15,20,30
		// to 0,1,2,3,4

		for (int i = 0; i < EuclidianView.getLineTypeLength(); i++) {
			if (type == EuclidianView.getLineType(i))
				return i;
		}

		return -1; // unknown type
	}

	public synchronized void setLineStyle(String objName, int style) {

		if (style < 0 || style >= EuclidianView.getLineTypeLength())
			return;

		GeoElement geo = kernel.lookupLabel(objName);
		if (geo == null)
			return;

		geo.setLineType(EuclidianView.getLineType(style));
		geo.updateRepaint();
	}

	/**
	 * Deletes the object with the given name.
	 */
	public synchronized void deleteObject(String objName) {
		GeoElement geo = kernel.lookupLabel(objName);
		if (geo == null)
			return;
		geo.remove();
		kernel.notifyRepaint();
	}

	/**
	 * Renames an object from oldName to newName.
	 * 
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
	 * Returns true if the object with the given name has a vaild value at the
	 * moment.
	 */
	public synchronized boolean isDefined(String objName) {
		GeoElement geo = kernel.lookupLabel(objName);
		if (geo == null)
			return false;
		return geo.isDefined();
	}

	/**
	 * Returns true if the object with the given name is independent.
	 */
	public synchronized boolean isIndependent(String objName) {
		GeoElement geo = kernel.lookupLabel(objName);
		if (geo == null)
			return false;
		return geo.isIndependent();
	}

	/**
	 * Returns the value of the object with the given name as a string.
	 */
	public synchronized String getValueString(String objName) {
		GeoElement geo = kernel.lookupLabel(objName);
		if (geo == null)
			return "";

		if (geo.isGeoText()) {
			return ((GeoText) geo).getTextString();
		}
		
		if (geo.isGeoCasCell()) {
			return ((GeoCasCell)geo).getOutput(StringTemplate.numericDefault);
		}

		return geo.getAlgebraDescriptionDefault();
	}

	/**
	 * Returns the definition of the object with the given name as a string.
	 */
	public synchronized String getDefinitionString(String objName) {
		return getDefinitionString(objName, true);
	}

	public synchronized String getDefinitionString(String objName,
			boolean localize) {
		GeoElement geo = kernel.lookupLabel(objName);
		if (geo == null) {
			return "";
		}
		return geo.getDefinitionDescription(
				localize ? StringTemplate.defaultTemplate
						: StringTemplate.noLocalDefault);
	}

	/**
	 * Returns the object with the given name as a LaTeX string.
	 */
	public synchronized String getLaTeXString(String objName) {
		GeoElement geo = kernel.lookupLabel(objName);
		if (geo == null) {
			return "";
		}
		return geo.toValueString(StringTemplate.latexTemplate);
	}

	/**
	 * Returns the command of the object with the given name as a string.
	 */
	public synchronized String getCommandString(String objName) {
		return getCommandString(objName, true);
	}

	public synchronized String getCommandString(String objName, boolean localize) {

		GeoElement geo = kernel.lookupLabel(objName);
		if (geo == null)
			return "";
		if (geo instanceof GeoCasCell) {
			return geo.getDefinitionDescription(
					localize ? StringTemplate.defaultTemplate
							: StringTemplate.noLocalDefault);
		}
		return geo
				.getDefinition(localize ? StringTemplate.defaultTemplate
						: StringTemplate.noLocalDefault);
	}

	public synchronized String getCaption(String objName, boolean substituteVars) {
		GeoElement geo = kernel.lookupLabel(objName);
		if (geo == null)
			return "";
		return substituteVars ? geo.getCaption(StringTemplate.defaultTemplate)
				: geo.getRawCaption();
	}

	public synchronized void setCaption(String objName, String caption) {
		GeoElement geo = kernel.lookupLabel(objName);
		if (geo == null)
			return;
		geo.setCaption(caption);
		geo.updateRepaint();
	}

	public synchronized String getPerspectiveXML() {
		if (app.getGuiManager() == null
				|| app.getGuiManager().getLayout() == null) {
			return "";
		}
		StringBuilder layoutSB = new StringBuilder();
		app.getGuiManager().getLayout().getCurrentPerspectiveXML(layoutSB);
		return layoutSB.toString();
	}

	/**
	 * Returns the x-coord of the object with the given name. Note: returns 0 if
	 * the object is not a point or a vector.
	 */
	public synchronized double getXcoord(String objName) {
		GeoElement geo = kernel.lookupLabel(objName);
		if (geo == null)
			return 0;
		return kernel.getExpressionNodeEvaluator().handleXcoord(geo,
				Operation.XCOORD);

	}

	/**
	 * Returns the y-coord of the object with the given name. Note: returns 0 if
	 * the object is not a point or a vector.
	 */
	public synchronized double getYcoord(String objName) {
		GeoElement geo = kernel.lookupLabel(objName);
		if (geo == null)
			return 0;

		return kernel.getExpressionNodeEvaluator().handleYcoord(geo,
				Operation.YCOORD);
	}

	public synchronized double getZcoord(String objName) {
		GeoElement geo = kernel.lookupLabel(objName);
		if (geo == null)
			return 0;

		return kernel.getExpressionNodeEvaluator().handleZcoord(geo);
	}

	/**
	 * Sets the coordinates of the object with the given name. Note: if the
	 * specified object is not a point or a vector, nothing happens.
	 */
	public synchronized void setCoords(String objName, double x, double y) {
		GeoElement geo = kernel.lookupLabel(objName);
		if (geo == null)
			return;

		CmdSetCoords.setCoords(geo, x, y);

	}

	public synchronized void setCoords(String objName, double x, double y,
			double z) {
		GeoElement geo = kernel.lookupLabel(objName);
		if (geo == null)
			return;

		CmdSetCoords.setCoords(geo, x, y, z);

	}

	/**
	 * Returns the double value of the object with the given name. For a
	 * boolean, returns 0 for false, 1 for true Note: returns 0 if the object
	 * does not have a value.
	 */
	public synchronized double getValue(String objName) {
		GeoNumberValue geo = kernel.getAlgebraProcessor().evaluateToNumeric(
				objName, ErrorHelper.silent());
		if (geo == null)
			return 0;

		return geo.getDouble();
	}

	/**
	 * Sets the double value of the object with the given name. For a boolean 0
	 * -> false, any other value -> true Note: if the specified object is not a
	 * number, nothing happens.
	 */
	public synchronized void setValue(String objName, double x) {

		GeoElement geo = kernel.lookupLabel(objName);
		if (geo == null || !geo.isIndependent()) {
			return;
		}


		CmdSetValue.setValue2(geo,new GeoNumeric(kernel.getConstruction(), x));

		/*
		 * if (geo.isGeoNumeric()) { ((GeoNumeric) geo).setValue(x);
		 * geo.updateRepaint(); } else if (geo.isGeoBoolean()) { ((GeoBoolean)
		 * geo).setValue(Kernel.isZero(x) ? false : true); geo.updateRepaint();
		 * }
		 */
	}

	public synchronized void setTextValue(String objName, String x) {
		GeoElement geo = kernel.lookupLabel(objName);
		if (geo == null || !geo.isGeoText() || !geo.isIndependent()) {
			return;
		}

		((GeoText) geo).setTextString(x);
		geo.updateRepaint();

	}

	public synchronized void setListValue(String objName, double x, double y) {

		GeoElement geo = kernel.lookupLabel(objName);
		if (geo == null || !geo.isGeoList() || !geo.isIndependent()) {
			return;
		}

		Construction cons = kernel.getConstruction();

		CmdSetValue.setValue3(kernel, (GeoList) geo, (int) x, new GeoNumeric(
				cons, y));

	}

	/**
	 * Turns the repainting of all views on or off.
	 */
	public synchronized void setRepaintingActive(boolean flag) {
		// Application.debug("set repainting: " + flag);
		kernel.setNotifyRepaintActive(flag);
	}

	/*
	 * Methods to change the geometry window's properties
	 */

	/**
	 * Sets the Cartesian coordinate system in the graphics window.
	 */
	public synchronized void setCoordSystem(double xmin, double xmax,
			double ymin, double ymax) {
		app.getEuclidianView1().setRealWorldCoordSystem(xmin, xmax, ymin, ymax);
	}

	/**
	 * @param xmin
	 *            min of x axis range
	 * @param xmax
	 *            max of x axis range
	 * @param ymin
	 *            min of y axis range
	 * @param ymax
	 *            max of y axis range
	 * @param zmin
	 *            min of z axis range
	 * @param zmax
	 *            max of z axis range
	 * @param verticalY
	 *            true to set yAxis in vertical direction
	 */
	public synchronized void setCoordSystem(double xmin, double xmax,
			double ymin, double ymax, double zmin, double zmax,
			boolean verticalY) {
		EuclidianView3DInterface e3d = app.getEuclidianView3D();
		e3d.setYAxisVertical(verticalY);
		Coords boundsMin = new Coords(xmin, ymin, zmin);
		Coords boundsMax = new Coords(xmax, ymax, zmax);
		e3d.zoomRW(boundsMin, boundsMax);

	}

	/**
	 * Shows or hides the x- and y-axis of the coordinate system in the graphics
	 * window.
	 */
	public synchronized void setAxesVisible(boolean xVisible, boolean yVisible) {
		app.getEuclidianView1().setShowAxis(
				EuclidianViewInterfaceCommon.AXIS_X, xVisible, false);
		app.getEuclidianView1().setShowAxis(
				EuclidianViewInterfaceCommon.AXIS_Y, yVisible, false);
		kernel.notifyRepaint();
	}

	public synchronized void setAxesVisible(int view, boolean xVisible,
			boolean yVisible, boolean zVisible) {
		int index = view < 0 ? 3 : view;
		if (index < 1 || index > 3) {
			return;
		}
		EuclidianSettings evs = app.getSettings().getEuclidian(index);
		evs.beginBatch();
		evs.setShowAxis(0, xVisible);
		evs.setShowAxis(1, yVisible);
		evs.setShowAxis(2, zVisible);
		evs.endBatch();
		kernel.notifyRepaint();
	}
	/**
	 * If the origin is off screen and the axes are visible, GeoGebra shows
	 * coordinates of the upper-left and bottom-right screen corner. This method
	 * lets you hide these corner coordinates.
	 * 
	 * @param showAxesCornerCoords
	 *            true to show corner coordinates
	 */
	public synchronized void setAxesCornerCoordsVisible(
			boolean showAxesCornerCoords) {
		app.getEuclidianView1()
				.setAxesCornerCoordsVisible(showAxesCornerCoords);
		if (app.hasEuclidianView2(1)) {
			app.getEuclidianView2(1).setAxesCornerCoordsVisible(
					showAxesCornerCoords);
		}
	}

	/**
	 * Shows or hides the coordinate grid in the graphics window.
	 */
	public synchronized void setGridVisible(boolean flag) {
		app.getSettings().getEuclidian(1).showGrid(flag);
		app.getSettings().getEuclidian(2).showGrid(flag);
	}

	public synchronized void setGridVisible(int view, boolean flag) {
		int index = view < 0 ? 3 : view;
		if (index < 1 || index > 3) {
			return;
		}
		app.getSettings().getEuclidian(index).showGrid(flag);
	}

	/*
	 * Methods to get all object names of the construction
	 */

	/**
	 * Returns an array with the names of all selected objects.
	 * 
	 * @return an array with the names of all selected objects.
	 */
	public synchronized String[] getSelectedObjectNames() {
		ArrayList<GeoElement> selGeos = app.getSelectionManager()
				.getSelectedGeos();
		String[] selObjNames = new String[selGeos.size()];

		for (int i = 0; i < selGeos.size(); i++) {
			GeoElement geo = selGeos.get(i);
			selObjNames[i] = geo.getLabel(StringTemplate.defaultTemplate);
		}
		return selObjNames;
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
		String[] names = getObjNames();

		try {
			return names[i];
		} catch (Exception e) {
			return "";
		}
	}

	/**
	 * Opens construction given in XML format. May be used for loading
	 * constructions.
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

	/**
	 * @return application
	 */
	final public App getApplication() {
		return app;
	}

	/**
	 * For web it needed a callback. Don't forget that.
	 */
	public abstract String getBase64(boolean includeThumbnail);

	final public String getBase64() {
		return getBase64(false);
	}

	final public void setPenColor(int red, int green, int blue) {
		app.getActiveEuclidianView().getEuclidianController().getPen()
				.setPenColor(GColor.newColor(red, green, blue));
	}

	final public void setPenSize(int size) {
		app.getActiveEuclidianView().getEuclidianController().getPen()
				.setPenSize(size);
	}

	public int getPenSize() {
		return app.getActiveEuclidianView().getEuclidianController().getPen()
				.getPenSize();
	}

	public String getPenColor() {
		return "#"
				+ StringUtil.toHexString(app.getActiveEuclidianView()
						.getEuclidianController().getPen().getPenColor());
	}

	public synchronized double getListValue(String objName, int index) {
		GeoElement geo = kernel.lookupLabel(objName);
		if (geo == null || !geo.isGeoList()) {
			return Double.NaN;
		}

		GeoList list = (GeoList) geo;

		if (index < 1 || index >= list.size() + 1) {
			return Double.NaN;
		}

		GeoElement ret = list.get(index - 1);

		// GeoBoolean implements NumberValue, so no need to check for that
		return ret.evaluateDouble();
	}

	/**
	 * Cast undo
	 * 
	 * @param repaint
	 *            true to repaint the views afterwards
	 */
	public void undo(boolean repaint) {
		app.getKernel().undo();
		if (repaint) {
			app.doRepaintViews();
		}
	}

	/**
	 * Cast redo
	 * 
	 * @param repaint
	 *            true to repaint the views afterwards
	 */
	public void redo(boolean repaint) {
		app.getKernel().redo();
		if (repaint) {
			app.doRepaintViews();
		}
	}

	/**
	 * Cast redo
	 */
	public void setSaved() {
		app.setSaved();
	}

	/**
	 * Deletes all construction elements
	 */
	public void newConstruction() {
		app.fileNew();
	}

	/**
	 * @param view
	 *            view number
	 * @return JSON string describing the view
	 */
	public String getViewProperties(int view) {
		EuclidianView ev = view == 2 ? app.getEuclidianView2(1) : app
				.getEuclidianView1();
		StringBuilder sb = new StringBuilder(100);
		sb.append("{\"invXscale\":");
		sb.append(ev.getInvXscale());
		sb.append(",\"invYscale\":");
		sb.append(ev.getInvYscale());
		sb.append(",\"xMin\":");
		sb.append(ev.getXmin());
		sb.append(",\"yMin\":");
		sb.append(ev.getYmin());
		sb.append(",\"width\":");
		sb.append(ev.getWidth());
		sb.append(",\"height\":");
		sb.append(ev.getHeight());
		sb.append(",\"left\":");
		sb.append(ev.getAbsoluteLeft());
		sb.append(",\"top\":");
		sb.append(ev.getAbsoluteTop());
		sb.append("}");
		return sb.toString();
	}

	/**
	 * @param label
	 *            object
	 * @param size
	 *            font size
	 * @param bold
	 *            true for bold
	 * @param italic
	 *            true for italic
	 * @param serif
	 *            true for serif
	 */
	public void setFont(String label, int size, boolean bold, boolean italic,
			boolean serif) {
		GeoElement geo = kernel.lookupLabel(label);
		if (geo instanceof TextProperties) {
			TextProperties text = (TextProperties) geo;
			text.setFontSizeMultiplier(size / (0.0 + app.getFontSize()));
			text.setFontStyle((bold ? GFont.BOLD : GFont.PLAIN)
					| (italic ? GFont.ITALIC : GFont.PLAIN));
			text.setSerifFont(serif);
			geo.updateRepaint();
		}
	}

	/**
	 * Evaluates the given string as if it was entered into GeoGebra CAS's input
	 * text field.
	 * 
	 * @param cmdString
	 *            CAS command
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
	 * @param cmdString
	 *            command string
	 * 
	 * @param debugOutput
	 *            states whether debugging information should be printed to the
	 *            console
	 * @return evaluation result in GeoGebraCAS syntax
	 */
	public synchronized String evalGeoGebraCAS(String cmdString,
			boolean debugOutput) {
		if (!app.getSettings().getCasSettings().isEnabled()) {
			return "?";
		}
		String ret = "";
		GeoGebraCasInterface ggbcas = kernel.getGeoGebraCAS();
		try {
			// TODO -- allow to parametrize this
			ret = ggbcas.evaluateGeoGebraCAS(cmdString, null,
					StringTemplate.numericDefault, kernel);
		} catch (Throwable t) {
			Log.debug(t.toString());
		}// try-catch

		// useful for debugging JavaScript
		if (debugOutput)
			Log.debug("evalGeoGebraCAS\n input:" + cmdString + "\n"
					+ "output: " + ret);
		return ret;
	}

	/**
	 * Performs login
	 * 
	 * @param token
	 *            login token
	 */
	public void login(String token) {
		if (app.getLoginOperation() != null) {
			Log.debug("LTOKEN send via API");
			app.getLoginOperation().performTokenLogin(token, false);
		}
	}

	/**
	 * Log current user out
	 */
	public void logout() {
		if (app.getLoginOperation() != null
				&& app.getLoginOperation().getModel() != null) {

			app.getLoginOperation().getGeoGebraTubeAPI()
					.logout(app.getLoginOperation().getModel().getLoginToken());
		}
	}

	public void setPerspective(String code) {
		if (code.startsWith("search:")) {
			app.openSearch(code.substring("search:".length()));
			return;
		}
		if (code.startsWith("customize:")) {
			app.showCustomizeToolbarGUI();
			return;
		}
		if ("exam".equals(code)) {
			app.setNewExam();
			app.examWelcome();
			return;
		}
		// the exam setting is certainly false
		if (code.startsWith("<")) {
			try {
				app.getXMLio()
						.parsePerspectiveXML(
						"<geogebra format=\"5.0\"><gui><perspectives>"
										+ code
								+ "</perspectives></gui></geogebra>");
				app.getGuiManager().updateGUIafterLoadFile(true, false);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return;
		}
		Perspective ps = PerspectiveDecoder.decode(code, kernel.getParser(),
				ToolBar.getAllToolsNoMacros(app.isHTML5Applet(), app.isExam(),
						app));
		try {
			app.persistWidthAndHeight();
			app.getGuiManager().getLayout().applyPerspective(ps);
			app.updateViewSizes();
			app.getGuiManager().updateMenubar();
			app.getGuiManager().updateToolbar();
			app.updateKeyboard();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return;
	}

	public synchronized boolean getVisible(String label, int view) {
		if (view < -1 || view > 2 || view == 0) {
			return false;
		}
		GeoElement geo = kernel.lookupLabel(label);
		if (geo instanceof GeoAxisND) {
			EuclidianSettings evs = app.getSettings().getEuclidian(
					view < 0 ? 3 : view);
			int type = ((GeoAxisND) geo).getType();
			if (type == 2 && view > 0) {
				return false;
			}
			return evs.getShowAxis(type);
		}
		if (geo == null) {
			return false;
		}
		if (geo.isGeoCasCell()) {
			return ((GeoCasCell) geo).getTwinGeo() != null
					&& isVisibleInView(((GeoCasCell) geo).getTwinGeo(), view);
		}

		return isVisibleInView(geo, view);

	}

	private static boolean isVisibleInView(GeoElement geo, int view) {
		return geo.isVisibleInView(view == -1 ? App.VIEW_EUCLIDIAN3D
				: (view == 1 ? App.VIEW_EUCLIDIAN : App.VIEW_EUCLIDIAN2));
	}

	public synchronized boolean getGridVisible() {
		return getGridVisible(1);
	}

	public synchronized boolean getGridVisible(int view) {
		if (view < -1 || view > 2 || view == 0) {
			return false;
		}
		EuclidianSettings evs = app.getSettings().getEuclidian(
				view < 0 ? 3 : view);
		return evs.getShowGrid();
	}

	public int getCASObjectNumber(){
		return kernel.getConstruction().getCASObjectNumber();
		
	}

	/**
	 * If there are Macros or an Exercise present in the current file this can
	 * be used to check if parts of the construction are equivalent to the
	 * Macros in the file. <br>
	 * It will return the overall Fraction of the Exercise.<br>
	 * This is the sum of all the Fractions in the Assignment or 1 if one of the
	 * Assignments has a fraction of 100 and no negative fractions are present.
	 * Use {@link #getExerciseResult()} to get the fractions of each Assignment.
	 * If you don't want that a standard exercise (using all the Macros in the
	 * Construction and setting each fraction to 100) will be created, check if
	 * this is a Exercise with {@link #isExercise()} first. <br>
	 * 
	 * @return the overall fraction of the Exercise
	 * 
	 */
	public float getExerciseFraction() {
		Exercise ex = kernel.getExercise();
		ex.checkExercise();
		return ex.getFraction();
	}

	/**
	 * Check whether this applet is an Exercise
	 * 
	 * @return true if the Exercise has assignments, this will happen when
	 *         either {@link #getExerciseResult()} or
	 *         {@link #getExerciseFraction()} are called with user defined Tools
	 *         present in the applet or if the ExerciseBuilderDialog was used to
	 *         create the Exercise.
	 */
	public boolean isExercise() {
		Exercise ex = kernel.getExercise();
		return !ex.isEmpty();
	}

	/**
	 * @param localeStr
	 *            language or language_country
	 */
	public void setLanguage(String localeStr) {
		app.setLanguage(localeStr);
	}

	/**
	 * @param rounding
	 *            eg "10" for 10dp or "10s" for 10 significant figures
	 */
	public void setRounding(String rounding) {
		app.setRounding(rounding);
		kernel.updateConstruction();
		app.refreshViews();
		kernel.updateConstruction();
	}

	/**
	 * If there are Macros or an Exercise present in the current file this can
	 * be used to check if parts of the construction are equivalent to the
	 * Macros in the file. <br />
	 * If you don't want that a Standard Exercise (using all the Macros in the
	 * Construction and setting each fraction to 100) will be created, check if
	 * this is a Exercise with {@link #isExercise()} first. <br>
	 * Hint will be empty unless specified otherwise with the ExerciseBuilder.
	 * <br />
	 * Fraction will be 0 or 1 unless specified otherwise with the
	 * ExerciseBuilder. <br />
	 * Result will be in {@link Result},i.e: <br />
	 * CORRECT, The assignment is CORRECT <br />
	 * WRONG, if the assignment is WRONG and we can't tell why <br />
	 * NOT_ENOUGH_INPUTS if there are not enough input geos, so we cannot check
	 * <br />
	 * WRONG_INPUT_TYPES, if there are enough input geos, but one or more are of
	 * the wrong type <br />
	 * WRONG_OUTPUT_TYPE, if there is no output geo matching our macro <br />
	 * WRONG_AFTER_RANDOMIZE, if the assignment was correct in the first place
	 * but wrong after randomization <br />
	 * UNKNOWN, if the assignment could not be checked
	 * 
	 * @return JavaScriptObject representation of the exercise result. For
	 *         Example: "{"Tool1":{ "result":"CORRECT", "hint":"",
	 *         "fraction":1}}", will be empty if now Macros or Assignments have
	 *         been found.
	 */
	public Object getExerciseResult() {
		return "";
	}

	public String getVersion() {
		return GeoGebraConstants.VERSION_STRING;
	}

	/**
	 * Changes display style of line or conic
	 * 
	 * @param objName
	 *            object name
	 * @param style
	 *            one of "parametric", "explicit", "implicit", "specific"
	 */
	public void setDisplayStyle(String objName, String style) {
		GeoElement geo = kernel.lookupLabel(objName);

		if (geo instanceof GeoLine) {

			GeoLine line = (GeoLine) geo;

			if (style.equals("parametric")) {
				line.setMode(GeoLine.PARAMETRIC);
			} else if (style.equals("explicit")) {
				line.setMode(GeoLine.EQUATION_EXPLICIT);
			} else if (style.equals("implicit")) {
				line.setMode(GeoLine.EQUATION_IMPLICIT);
			}

			geo.updateRepaint();

		} else if (geo instanceof GeoConic) {

			GeoConic conic = (GeoConic) geo;

			if (style.equals("parametric")) {
				conic.setToStringMode(GeoConicND.EQUATION_PARAMETRIC);
			} else if (style.equals("explicit")) {
				conic.setToStringMode(GeoConicND.EQUATION_EXPLICIT);
			} else if (style.equals("implicit")) {
				conic.setToStringMode(GeoConicND.EQUATION_IMPLICIT);
			} else if (style.equals("specific")) {
				conic.setToStringMode(GeoConicND.EQUATION_SPECIFIC);
			}

			geo.updateRepaint();

		}


	}

	public void enableCAS(boolean enable) {
		app.getSettings().getCasSettings().setEnabled(enable);
	}

	public void enable3D(boolean enable) {
		app.getSettings().getEuclidian(-1).setEnabled(enable);
	}

	/**
	 * @param enable
	 *            wheter geogebra-web applet rightclick enabled or not
	 */
	public void enableRightClick(boolean enable) {
		app.setRightClickEnabled(enable);
	}

	/**
	 * @param enable
	 * 
	 *            wheter labels draggable in geogebra-web applets or not
	 */
	public void enableLabelDrags(boolean enable) {
		app.setLabelDragsEnabled(enable);
	}

	/**
	 * @param enable
	 * 
	 *            wheter shift - drag - zoom enabled in geogebra-web applets or
	 *            not
	 */
	public void enableShiftDragZoom(boolean enable) {
		app.setShiftDragZoomEnabled(enable);
	}

	public void setAxisSteps(int view, String xStep, String yStep,
			String zStep) {
		int index = view < 0 ? 3 : view;
		if (index < 1 || index > 3) {
			return;
		}
		EuclidianSettings evs = app.getSettings().getEuclidian(index);
		evs.beginBatch();
		evs.setAxisNumberingDistance(0,
				this.algebraprocessor.evaluateToNumeric(xStep,
						ErrorHelper.silent()));
		evs.setAxisNumberingDistance(1,
				this.algebraprocessor.evaluateToNumeric(yStep,
						ErrorHelper.silent()));
		if (evs.is3D()) {
			evs.setAxisNumberingDistance(2,
					this.algebraprocessor.evaluateToNumeric(zStep,
							ErrorHelper.silent()));
		}
		evs.endBatch();
		kernel.notifyRepaint();

	}

	public void setAxisLabels(int view, String xLabel, String yLabel,
			String zLabel) {
		int index = view < 0 ? 3 : view;
		if (index < 1 || index > 3) {
			return;
		}
		EuclidianSettings evs = app.getSettings().getEuclidian(index);
		evs.beginBatch();
		evs.setAxisLabel(0, xLabel);
		evs.setAxisLabel(1, yLabel);
		if (evs.is3D()) {
			evs.setAxisLabel(2, zLabel);
		}
		evs.endBatch();
		kernel.notifyRepaint();

	}

	public void setAxisUnits(int view, String xLabel, String yLabel,
			String zLabel) {
		int index = view < 0 ? 3 : view;
		if (index < 1 || index > 3) {
			return;
		}
		EuclidianSettings evs = app.getSettings().getEuclidian(index);
		evs.beginBatch();
		evs.setAxesUnitLabels(new String[] { xLabel, yLabel, zLabel });

		evs.endBatch();
		kernel.notifyRepaint();

	}

	public void setPointCapture(int view, int capture) {
		int index = view < 0 ? 3 : view;
		if (index < 1 || index > 3) {
			return;
		}
		EuclidianSettings evs = app.getSettings().getEuclidian(index);
		evs.setPointCapturing(capture);
		kernel.notifyRepaint();

	}

	public void setAuxiliary(String objName, boolean flag) {
		GeoElement geo = kernel.lookupLabel(objName);
		geo.setAuxiliaryObject(flag);
		geo.updateRepaint();

	}

}