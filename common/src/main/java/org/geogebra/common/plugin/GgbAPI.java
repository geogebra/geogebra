package org.geogebra.common.plugin;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.TreeSet;

import org.geogebra.common.GeoGebraConstants;
import org.geogebra.common.awt.GFont;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.euclidian.EuclidianViewInterfaceCommon;
import org.geogebra.common.factories.AwtFactory;
import org.geogebra.common.gui.toolbar.ToolBar;
import org.geogebra.common.io.layout.Perspective;
import org.geogebra.common.io.layout.PerspectiveDecoder;
import org.geogebra.common.kernel.CircularDefinitionException;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.GeoGebraCasInterface;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.Locateable;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.UserAwarenessListener;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.arithmetic.NumberValue;
import org.geogebra.common.kernel.arithmetic.Traversing.CommandCollector;
import org.geogebra.common.kernel.commands.AlgebraProcessor;
import org.geogebra.common.kernel.geos.AbsoluteScreenLocateable;
import org.geogebra.common.kernel.geos.GeoBoolean;
import org.geogebra.common.kernel.geos.GeoCasCell;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.geos.GeoText;
import org.geogebra.common.kernel.geos.GeoVector;
import org.geogebra.common.kernel.geos.PointProperties;
import org.geogebra.common.kernel.geos.TextProperties;
import org.geogebra.common.kernel.geos.Traceable;
import org.geogebra.common.kernel.scripting.CmdSetValue;
import org.geogebra.common.main.App;
import org.geogebra.common.util.StringUtil;

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

		// default (undefined)
		String ret = "?";

		try {
			GeoCasCell f = new GeoCasCell(kernel.getConstruction());
			// kernel.getConstruction().addToConstructionList(f, false);

			f.setInput(cmdString);

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

		// Application.debug("evalCommand called..."+cmdString);
		GeoElement[] result;

		// this is new in GeoGebra 4.2 and it will stop some files working
		// but causes problems if the files are opened and edited
		// and in the web project
		boolean oldVal = kernel.isUsingInternalCommandNames();
		kernel.setUseInternalCommandNames(true);

		boolean ret = true;

		if (cmdString.indexOf('\n') == -1) {
			result = kernel.getAlgebraProcessor().processAlgebraCommand(
					cmdString, false);
			// return success
			ret = result != null;

		} else {

			String[] cmdStrings = cmdString.split("[\\n]+");
			for (int i = 0; i < cmdStrings.length; i++) {
				result = kernel.getAlgebraProcessor().processAlgebraCommand(
						cmdStrings[i], false);
				ret = ret & (result != null);
			}
		}

		kernel.setUseInternalCommandNames(oldVal);

		return ret;
	}

	/**
	 * prints a string to the Java Console
	 */
	public synchronized void debug(String string) {

		App.debug(string);
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
	 * Sets the color of the object with the given name.
	 */
	public synchronized void setColor(String objName, int red, int green,
			int blue) {
		GeoElement geo = kernel.lookupLabel(objName);
		if (geo == null)
			return;
		geo.setObjColor(org.geogebra.common.factories.AwtFactory.prototype
				.newColor(red, green, blue));
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
				App.debug("EV2");
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
			App.debug("all CAS up");
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

	public synchronized int getLineStyle(String objName) {
		GeoElement geo = kernel.lookupLabel(objName);
		if (geo == null)
			return -1;
		int type = geo.getLineType();

		// convert from 0,10,15,20,30
		// to 0,1,2,3,4

		Integer[] types = EuclidianView.getLineTypes();
		for (int i = 0; i < types.length; i++) {
			if (type == types[i].intValue())
				return i;
		}

		return -1; // unknown type
	}

	public synchronized void setLineStyle(String objName, int style) {
		Integer[] types = EuclidianView.getLineTypes();

		if (style < 0 || style >= types.length)
			return;

		GeoElement geo = kernel.lookupLabel(objName);
		if (geo == null)
			return;

		geo.setLineType(types[style].intValue());
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

		if (geo.isGeoText())
			return ((GeoText) geo).getTextString();

		return geo.getAlgebraDescriptionDefault();
	}

	/**
	 * Returns the definition of the object with the given name as a string.
	 */
	public synchronized String getDefinitionString(String objName) {
		GeoElement geo = kernel.lookupLabel(objName);
		if (geo == null)
			return "";
		return geo.getDefinitionDescription(StringTemplate.defaultTemplate);
	}

	/**
	 * Returns the command of the object with the given name as a string.
	 */
	public synchronized String getCommandString(String objName) {
		GeoElement geo = kernel.lookupLabel(objName);
		if (geo == null)
			return "";
		return geo.getCommandDescription(StringTemplate.defaultTemplate);
	}

	/**
	 * Returns the x-coord of the object with the given name. Note: returns 0 if
	 * the object is not a point or a vector.
	 */
	public synchronized double getXcoord(String objName) {
		GeoElement geo = kernel.lookupLabel(objName);
		if (geo == null)
			return 0;

		if (geo.isGeoPoint())
			return ((GeoPoint) geo).inhomX;
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
		if (geo == null)
			return 0;

		if (geo.isGeoPoint())
			return ((GeoPoint) geo).inhomY;
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
		if (geo == null)
			return;

		if (geo.isGeoPoint()) {
			((GeoPoint) geo).setCoords(x, y, 1);
		} else if (geo.isGeoVector()) {
			((GeoVector) geo).setCoords(x, y, 0);
		} else if (geo instanceof AbsoluteScreenLocateable) {
			AbsoluteScreenLocateable asl = (AbsoluteScreenLocateable) geo;

			if (asl.isAbsoluteScreenLocActive()) {
				asl.setAbsoluteScreenLoc((int) x, (int) y);
			} else {
				asl.setRealWorldLoc(x, y);
			}

		} else {
			return;
		}

		geo.updateRepaint();

	}

	/**
	 * Returns the double value of the object with the given name. For a
	 * boolean, returns 0 for false, 1 for true Note: returns 0 if the object
	 * does not have a value.
	 */
	public synchronized double getValue(String objName) {
		GeoElement geo = kernel.lookupLabel(objName);
		if (geo == null)
			return 0;

		if (geo instanceof NumberValue)
			return ((NumberValue) geo).getDouble();
		else if (geo.isGeoBoolean())
			return ((GeoBoolean) geo).getBoolean() ? 1 : 0;

		return 0;
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
				.setPenColor(AwtFactory.prototype.newColor(red, green, blue));
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

	public double getListValue(String objName, int index) {
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

	public void addUserAwarenessListener(UserAwarenessListener listener) {
		this.kernel.addUserAwarenessListener(listener);
	}

	public void removeUserAwarenessListener(UserAwarenessListener listener) {
		this.kernel.removeUserAwarenessListener(listener);
	}

	/**
	 * Cast undo
	 */
	public void undo(boolean repaint) {
		app.getKernel().undo();
		if (repaint) {
			app.doRepaintViews();
		}
	}

	/**
	 * Cast redo
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
		String ret = "";
		GeoGebraCasInterface ggbcas = kernel.getGeoGebraCAS();
		try {
			// TODO -- allow to parametrize this
			ret = ggbcas.evaluateGeoGebraCAS(cmdString, null,
					StringTemplate.numericDefault, kernel);
		} catch (Throwable t) {
			App.debug(t.toString());
		}// try-catch

		// useful for debugging JavaScript
		if (debugOutput)
			App.debug("evalGeoGebraCAS\n input:" + cmdString + "\n"
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
			App.debug("LTOKEN send via API");
			app.getLoginOperation().performTokenLogin(token, false);
		}
	}

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
		// the exam setting is certainly false
		Perspective ps = PerspectiveDecoder.decode(code, kernel.getParser(),
				ToolBar.getAllToolsNoMacros(app.isHTML5Applet(), app.isExam()));
		try {
			app.persistWidthAndHeight();
			app.getGuiManager().getLayout().applyPerspective(ps);
			app.updateViewSizes();
			app.getGuiManager().updateMenubar();
			app.getGuiManager().updateToolbar();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return;
	}

	public boolean getVisible(String label, int view) {
		if (view < 1 || view > 2) {
			return false;
		}
		GeoElement geo = kernel.lookupLabel(label);
		if (geo == null) {
			return false;
		}

		return geo.isVisibleInView(view == 1 ? App.VIEW_EUCLIDIAN
				: App.VIEW_EUCLIDIAN2);

	}
}