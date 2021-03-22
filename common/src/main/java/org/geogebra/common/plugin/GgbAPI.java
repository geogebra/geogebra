package org.geogebra.common.plugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Locale;
import java.util.TreeSet;

import org.geogebra.common.GeoGebraConstants;
import org.geogebra.common.awt.GColor;
import org.geogebra.common.awt.GFont;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.euclidian.EuclidianViewInterfaceCommon;
import org.geogebra.common.euclidian3D.EuclidianView3DInterface;
import org.geogebra.common.export.pstricks.ExportFrameMinimal;
import org.geogebra.common.export.pstricks.GeoGebraExport;
import org.geogebra.common.gui.dialog.handler.RenameInputHandler;
import org.geogebra.common.gui.toolbar.ToolBar;
import org.geogebra.common.gui.view.consprotocol.ConstructionProtocolView;
import org.geogebra.common.gui.view.consprotocol.ConstructionProtocolView.Columns;
import org.geogebra.common.io.layout.Perspective;
import org.geogebra.common.io.layout.PerspectiveDecoder;
import org.geogebra.common.kernel.CircularDefinitionException;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.GeoGebraCasInterface;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.Locateable;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.arithmetic.Traversing.CommandCollector;
import org.geogebra.common.kernel.commands.AlgebraProcessor;
import org.geogebra.common.kernel.geos.AbsoluteScreenLocateable;
import org.geogebra.common.kernel.geos.GProperty;
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
import org.geogebra.common.kernel.matrix.Coords;
import org.geogebra.common.kernel.scripting.CmdSetCoords;
import org.geogebra.common.kernel.scripting.CmdSetValue;
import org.geogebra.common.main.App;
import org.geogebra.common.main.error.ErrorHelper;
import org.geogebra.common.main.settings.EuclidianSettings;
import org.geogebra.common.util.AsyncOperation;
import org.geogebra.common.util.StringUtil;
import org.geogebra.common.util.debug.Log;

import com.himamis.retex.editor.share.util.Unicode;
import com.himamis.retex.renderer.share.TeXFormula;
import com.himamis.retex.renderer.share.serialize.ListSerializationAdapter;
import com.himamis.retex.renderer.share.serialize.TeXAtomSerializer;

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
	/** kernel */
	protected Kernel kernel = null;
	/** construction */
	protected Construction construction = null;
	/** algebra processor */
	protected AlgebraProcessor algebraprocessor = null;
	/** application */
	protected App app = null;

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

	@Override
	public void reset() {
		app.reset();
	}

	/**
	 * Evaluates the given XML string and changes the current construction.
	 * Note: the construction is NOT cleared before evaluating the XML string.
	 */
	@Override
	public synchronized void evalXML(String xmlString) {
		getApplication().getActiveEuclidianView().saveInlines();
		String sb = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n"
				+ "<geogebra format=\"" + GeoGebraConstants.XML_FILE_FORMAT
				+ "\">\n"
				+ "<construction>\n"
				+ xmlString
				+ "</construction>\n"
				+ "</geogebra>\n";
		getApplication().setXML(sb, false);
		getApplication().getActiveEuclidianView().updateInlines();
	}

	/**
	 * Evaluates the given string as if it was entered into GeoGebra's CAS View
	 * (but it won't create any objects etc in GeoGebra)
	 * 
	 * @param cmdString
	 *            input to CAS
	 * @return output from CAS
	 */
	@Override
	public synchronized String evalCommandCAS(String cmdString) {
		if (!app.getSettings().getCasSettings().isEnabled()) {
			return "?";
		}
		GeoCasCell assignment = algebraprocessor.checkCasEval(cmdString,
				"(:=?)|" + Unicode.ASSIGN_STRING);
		if (assignment != null) {
			return getCasCellValue(assignment);
		}
		// default (undefined)
		String ret = "?";

		try {
			GeoCasCell f = new GeoCasCell(kernel.getConstruction());
			// kernel.getConstruction().addToConstructionList(f, false);

			f.setInput(cmdString);
			f.computeOutput();

			boolean includesNumericCommand = false;
			HashSet<Command> commands = new HashSet<>();

			f.getInputVE().traverse(CommandCollector.getCollector(commands));

			if (!commands.isEmpty()) {
				for (Command cmd : commands) {
					String cmdName = cmd.getName();
					// Numeric used
					includesNumericCommand = includesNumericCommand
							|| ("Numeric".equals(cmdName)
									&& cmd.getArgumentNumber() > 1);
				}
			}

			ret = getCasCellValue(f);
		} catch (Throwable e) {
			e.printStackTrace();
		}

		return ret;
	}

	private String getCasCellValue(GeoCasCell f) {
		return f.getValue() != null
				? f.getValue().toString(StringTemplate.numericDefault)
				: f.getOutput(StringTemplate.testTemplate);
	}

	/**
	 * Evaluates the given string as if it was entered into GeoGebra's input
	 * text field.
	 */
	@Override
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
		GeoElementND[] result;

		// this is new in GeoGebra 4.2 and it will stop some files working
		// but causes problems if the files are opened and edited
		// and in the web project
		boolean oldVal = kernel.isUsingInternalCommandNames();
		kernel.setUseInternalCommandNames(true);

		StringBuilder ret = new StringBuilder();

		if (cmdString.indexOf('\n') == -1) {
			result = kernel.getAlgebraProcessor()
					.processAlgebraCommand(cmdString, false);
			// return success
			if (result == null) {
				kernel.setUseInternalCommandNames(oldVal);
				return null;
			}

			for (GeoElementND geoElementND : result) {
				ret.append(geoElementND.getLabelSimple());
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
		for (String string : cmdStrings) {
			result = kernel.getAlgebraProcessor()
					.processAlgebraCommand(string, false);

			if (result != null) {
				for (GeoElementND geoElementND : result) {
					ret.append(geoElementND.getLabelSimple());
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

	@Override
	public synchronized void debug(String string) {
		Log.debug(string);
	}

	/**
	 * Returns the GeoGebra XML string for the given GeoElement object, i.e.
	 * only the &lt;element&gt; tag is returned.
	 */
	@Override
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
	@Override
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
	@Override
	public synchronized void setVisible(String objName, boolean visible) {
		GeoElement geo = kernel.lookupLabel(objName);
		if (geo == null) {
			return;
		}
		geo.setEuclidianVisible(visible);
		geo.updateRepaint();
	}

	/**
	 * Shows or hides the object with the given name in the geometry window.
	 */
	@Override
	public synchronized boolean getVisible(String objName) {
		GeoElement geo = kernel.lookupLabel(objName);
		if (geo == null) {
			return false;
		}
		if (geo.isGeoCasCell()) {
			return ((GeoCasCell) geo).getTwinGeo() != null
					&& ((GeoCasCell) geo).getTwinGeo().isEuclidianVisible();
		}
		return geo.isEuclidianVisible();
	}

	/**
	 * Sets the layer of the object with the given name in the geometry window.
	 * Michael Borcherds 2008-02-27
	 */
	@Override
	public synchronized void setLayer(String objName, int layer) {
		GeoElement geo = kernel.lookupLabel(objName);
		if (geo == null) {
			return;
		}
		geo.setLayer(layer);
		geo.updateRepaint();
	}

	/**
	 * Returns the layer of the object with the given name in the geometry
	 * window. returns layer, or -1 if object doesn't exist Michael Borcherds
	 * 2008-02-27
	 */
	@Override
	public synchronized int getLayer(String objName) {
		GeoElement geo = kernel.lookupLabel(objName);
		if (geo == null) {
			return -1;
		}
		return geo.getLayer();
	}

	/**
	 * Shows or hides a complete layer Michael Borcherds 2008-02-27
	 */
	@Override
	public synchronized void setLayerVisible(int layer, boolean visible) {
		if (layer < 0 || layer > EuclidianStyleConstants.MAX_LAYERS) {
			return;
		}
		String[] names = getAllObjectNames();
		for (String name : names) {
			GeoElement geo = kernel.lookupLabel(name);
			if (geo != null) {
				if (geo.getLayer() == layer) {
					geo.setEuclidianVisible(visible);
					geo.updateRepaint();
				}
			}
		}
	}

	@Override
	public synchronized String[] getAllObjectNames() {
		Construction cons = kernel.getConstruction();
		TreeSet<GeoElement> geoSet = cons.getGeoSetConstructionOrder();
		int size = geoSet.size();

		// build objNames array
		String[] objNames = new String[size];

		int i = 0;
		for (GeoElement geo : geoSet) {
			objNames[i] = geo.getLabelSimple();
			i++;
		}
		return objNames;
	}

	@Override
	public synchronized String[] getAllObjectNames(String type) {
		Construction cons = kernel.getConstruction();
		TreeSet<GeoElement> geoSet = cons.getGeoSetConstructionOrder();
		int size = geoSet.size();

		// build objNames array
		ArrayList<String> objList = new ArrayList<>(size / 2);

		for (GeoElement geo : geoSet) {
			if (StringUtil.empty(type)
					|| type.equalsIgnoreCase(geo.getTypeString())) {
				objList.add(geo.getLabelSimple());
			}
		}
		return objList.toArray(new String[objList.size()]);
	}

	@Override
	public synchronized void setFixed(String objName, boolean fixed) {
		GeoElement geo = kernel.lookupLabel(objName);
		if (geo != null && geo.isFixable()) {
			setFixedAndNotify(fixed, geo);
		}
	}

	@Override
	public synchronized void setFixed(String objName, boolean fixed,
			boolean selectionAllowed) {
		GeoElement geo = kernel.lookupLabel(objName);
		if (geo != null) {
			geo.setSelectionAllowed(selectionAllowed);
			if (geo.isFixable()) {
				setFixedAndNotify(fixed, geo);
			}
		}
	}

	private static void setFixedAndNotify(boolean fixed, GeoElement geo) {
		geo.setFixed(fixed);
		geo.updateVisualStyleRepaint(GProperty.COMBINED);
	}

	/**
	 * Turns the trace of the object with the given name on or off.
	 */
	@Override
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
	@Override
	public synchronized void setLabelVisible(String objName, boolean visible) {
		GeoElement geo = kernel.lookupLabel(objName);
		if (geo == null) {
			return;
		}
		geo.setLabelVisible(visible);
		geo.updateRepaint();
	}

	@Override
	public synchronized boolean getLabelVisible(String objName) {
		GeoElement geo = kernel.lookupLabel(objName);
		if (geo == null) {
			return false;
		}
		return geo.isLabelVisible();
	}

	/**
	 * Sets the label style of the object with the given name in the geometry
	 * window. Possible label styles are NAME = 0, NAME_VALUE = 1 and VALUE = 2.
	 */
	@Override
	public synchronized void setLabelStyle(String objName, int style) {
		GeoElement geo = kernel.lookupLabel(objName);
		if (geo == null) {
			return;
		}
		geo.setLabelMode(style);
		geo.updateRepaint();
	}

	@Override
	public synchronized int getLabelStyle(String objName) {
		GeoElement geo = kernel.lookupLabel(objName);
		if (geo == null) {
			return 0;
		}
		return geo.getLabelMode();
	}

	/**
	 * Sets the color of the object with the given name.
	 */
	@Override
	public synchronized void setColor(String objName, int red, int green,
			int blue) {
		GeoElement geo = kernel.lookupLabel(objName);
		if (geo == null) {
			return;
		}
		geo.setObjColor(GColor.newColor(red, green, blue));
		geo.updateRepaint();
	}

	@Override
	public synchronized void setCorner(String objName, double x, double y) {
		setCorner(objName, x, y, 1);
	}

	@Override
	public synchronized void setCorner(String objName, double x, double y,
			int index) {
		GeoElement geo = kernel.lookupLabel(objName);
		if (!(geo instanceof AbsoluteScreenLocateable)) {
			return;
		}
		AbsoluteScreenLocateable loc = ((AbsoluteScreenLocateable) geo);
		if (loc.isAbsoluteScreenLocActive()) {
			loc.setAbsoluteScreenLoc((int) Math.round(x), (int) Math.round(y));
		} else if (geo instanceof Locateable) {
			GeoPoint corner = new GeoPoint(kernel.getConstruction());
			EuclidianView ev = app.getEuclidianView1();
			if (geo.isVisibleInView(ev.getViewID())
					&& app.hasEuclidianView2EitherShowingOrNot(1)
					&& geo.isVisibleInView(
							app.getEuclidianView2(1).getViewID())) {
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
	@Override
	public void setAnimating(String objName, boolean animate) {
		GeoElement geo = kernel.lookupLabel(objName);
		if (geo != null) {
			geo.setAnimating(animate);
		}
	}

	/**
	 * Sets the animation speed of an object
	 */
	@Override
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
	@Override
	public synchronized String getColor(String objName) {
		GeoElement geo = kernel.lookupLabel(objName);
		if (geo == null) {
			return "";
		}
		return StringUtil.toHtmlColor(geo.getObjectColor());
	}

	@Override
	public synchronized int getLineThickness(String objName) {
		GeoElement geo = kernel.lookupLabel(objName);
		if (geo == null) {
			return -1;
		}
		return geo.getLineThickness();
	}

	@Override
	public synchronized void setLineThickness(String objName,
			int lineThickness) {
		int thickness = lineThickness;
		if (thickness == -1) {
			thickness = EuclidianStyleConstants.DEFAULT_LINE_THICKNESS;
		}
		GeoElement geo = kernel.lookupLabel(objName);
		if (geo == null) {
			return;
		}
		geo.setLineThickness(thickness);
		geo.updateRepaint();
	}

	@Override
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

	@Override
	public synchronized void setPointStyle(String objName, int style) {
		GeoElement geo = kernel.lookupLabel(objName);
		if (geo == null) {
			return;
		}
		if (geo instanceof PointProperties) {
			((PointProperties) geo).setPointStyle(style);
			geo.updateRepaint();
		}
	}

	@Override
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

	@Override
	public synchronized void setPointSize(String objName, int style) {
		if (style < 1 || style > 9) {
			return;
		}
		GeoElement geo = kernel.lookupLabel(objName);
		if (geo == null) {
			return;
		}
		if (geo instanceof PointProperties) {
			((PointProperties) geo).setPointSize(style);
			geo.updateRepaint();
		}
	}

	@Override
	public synchronized double getFilling(String objName) {
		GeoElement geo = kernel.lookupLabel(objName);
		if (geo == null) {
			return -1;
		}
		return geo.getAlphaValue();
	}

	@Override
	public synchronized void setFilling(String objName, double filling) {
		if (filling < 0.0 || filling > 1.0) {
			return;
		}

		GeoElement geo = kernel.lookupLabel(objName);
		if (geo == null) {
			return;
		}

		geo.setAlphaValue(filling);
		geo.updateRepaint();
	}

	@Override
	public synchronized String getImageFileName(String objName) {
		GeoElement geo = kernel.lookupLabel(objName);
		if (geo == null) {
			return "";
		}
		return geo.getImageFileName();
	}

	@Override
	public void setOnTheFlyPointCreationActive(boolean flag) {
		app.setOnTheFlyPointCreationActive(flag);
	}

	@Override
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

			// Don't remove this. It is needed for testing the web platform.
			// (String match is assumed.)
			Log.debug("all CAS up");

			kernel.notifyRepaint();
		}
	}

	@Override
	public void uploadToGeoGebraTube() {
		app.uploadToGeoGebraTube();
	}

	@Override
	public void startAnimation() {
		kernel.getAnimatonManager().startAnimation();
	}

	@Override
	public void stopAnimation() {
		kernel.getAnimatonManager().stopAnimation();
	}

	@Override
	public void hideCursorWhenDragging(boolean hideCursorWhenDragging) {
		kernel.getApplication()
				.setUseTransparentCursorWhenDragging(hideCursorWhenDragging);
	}

	@Override
	public boolean isAnimationRunning() {
		return kernel.getAnimatonManager().isRunning();
	}

	@Override
	public double getFrameRate() {
		return kernel.getFrameRate();
	}

	@Override
	public synchronized void registerAddListener(Object JSFunctionName) {
		app.getScriptManager().registerAddListener(JSFunctionName);
	}

	@Override
	public synchronized void unregisterAddListener(Object JSFunctionName) {
		app.getScriptManager().unregisterAddListener(JSFunctionName);
	}

	@Override
	public synchronized void registerRemoveListener(Object JSFunctionName) {
		app.getScriptManager().registerRemoveListener(JSFunctionName);
	}

	@Override
	public synchronized void unregisterRemoveListener(Object JSFunctionName) {
		app.getScriptManager().unregisterRemoveListener(JSFunctionName);
	}

	@Override
	public synchronized void registerClearListener(Object JSFunctionName) {
		app.getScriptManager().registerClearListener(JSFunctionName);
	}

	@Override
	public synchronized void unregisterClearListener(Object JSFunctionName) {
		app.getScriptManager().unregisterClearListener(JSFunctionName);
	}

	@Override
	public synchronized void registerRenameListener(Object JSFunctionName) {
		app.getScriptManager().registerRenameListener(JSFunctionName);
	}

	@Override
	public synchronized void unregisterRenameListener(Object JSFunctionName) {
		app.getScriptManager().unregisterRenameListener(JSFunctionName);
	}

	@Override
	public synchronized void registerUpdateListener(Object JSFunctionName) {
		app.getScriptManager().registerUpdateListener(JSFunctionName);
	}

	@Override
	public synchronized void unregisterUpdateListener(Object JSFunctionName) {
		app.getScriptManager().unregisterUpdateListener(JSFunctionName);
	}

	@Override
	public synchronized void registerObjectUpdateListener(String objName,
			Object JSFunctionName) {
		app.getScriptManager().registerObjectUpdateListener(objName,
				JSFunctionName);
	}

	@Override
	public synchronized void unregisterObjectUpdateListener(String objName) {
		app.getScriptManager().unregisterObjectUpdateListener(objName);
	}

	@Override
	public synchronized void registerClickListener(Object JSFunctionName) {
		app.getScriptManager().registerClickListener(JSFunctionName);
	}

	@Override
	public synchronized void unregisterClickListener(Object JSFunctionName) {
		app.getScriptManager().unregisterClickListener(JSFunctionName);
	}

	@Override
	public void registerClientListener(Object JSFunctionName) {
		app.getScriptManager().registerClientListener(JSFunctionName);
	}

	@Override
	public void unregisterClientListener(Object JSFunctionName) {
		app.getScriptManager().unregisterClientListener(JSFunctionName);
	}

	@Override
	public synchronized void registerObjectClickListener(String objName,
			Object JSFunctionName) {
		app.getScriptManager().registerObjectClickListener(objName,
				JSFunctionName);
	}

	@Override
	public synchronized void unregisterObjectClickListener(String objName) {
		app.getScriptManager().unregisterObjectClickListener(objName);
	}

	@Override
	public synchronized void registerStoreUndoListener(Object JSFunctionName) {
		app.getScriptManager().registerStoreUndoListener(JSFunctionName);
	}

	@Override
	public synchronized void unregisterStoreUndoListener(Object JSFunctionName) {
		app.getScriptManager().unregisterStoreUndoListener(JSFunctionName);
	}

	@Override
	public boolean isMoveable(String objName) {
		GeoElement geo = kernel.lookupLabel(objName);
		if (geo == null) {
			return false;
		}
		return geo.isMoveable();
	}

	/**
	 * Returns the type of the object with the given name as a string (e.g.
	 * point, line, circle, ...)
	 */
	@Override
	public synchronized String getObjectType(String objName) {
		GeoElement geo = kernel.lookupLabel(objName);
		return (geo == null) ? "" : StringUtil.toLowerCaseUS(geo.getTypeString());
	}

	/**
	 * Sets the mode of the geometry window (EuclidianView).
	 */
	@Override
	public synchronized void setMode(int mode) {
		app.setMode(mode);
	}

	@Override
	public synchronized int getMode() {
		return app.getMode();
	}

	@Override
	public synchronized int getLineStyle(String objName) {
		GeoElement geo = kernel.lookupLabel(objName);
		if (geo == null) {
			return -1;
		}
		int type = geo.getLineType();

		// convert from 0,10,15,20,30
		// to 0,1,2,3,4

		for (int i = 0; i < EuclidianView.getLineTypeLength(); i++) {
			if (type == EuclidianView.getLineType(i)) {
				return i;
			}
		}

		return -1; // unknown type
	}

	@Override
	public synchronized void setLineStyle(String objName, int style) {

		if (style < 0 || style >= EuclidianView.getLineTypeLength()) {
			return;
		}

		GeoElement geo = kernel.lookupLabel(objName);
		if (geo == null) {
			return;
		}

		geo.setLineType(EuclidianView.getLineType(style));
		geo.updateRepaint();
	}

	/**
	 * Deletes the object with the given name.
	 */
	@Override
	public synchronized void deleteObject(String objName) {
		GeoElement geo = kernel.lookupLabel(objName);
		if (geo == null) {
			return;
		}
		geo.remove();
		kernel.notifyRepaint();
	}

	@Override
	public synchronized boolean renameObject(String oldName,
			String suggestedName,
			boolean forceRename) {
		GeoElement geo = kernel.lookupLabel(oldName);
		if (geo == null) {
			return false;
		}
		String newName = forceRename
				? RenameInputHandler.checkFreeLabel(kernel, suggestedName)
				: suggestedName;
		// try to rename
		boolean success = geo.rename(newName);
		kernel.notifyRepaint();

		return success;
	}

	/**
	 * Renames an object from oldName to newName.
	 * 
	 * @return whether renaming worked
	 */
	@Override
	public synchronized boolean renameObject(String oldName, String newName) {
		return renameObject(oldName, newName, false);
	}

	/**
	 * Returns true if the object with the given name exists.
	 */
	@Override
	public synchronized boolean exists(String objName) {
		GeoElement geo = kernel.lookupLabel(objName);
		return (geo != null);
	}

	/**
	 * Returns true if the object with the given name has a vaild value at the
	 * moment.
	 */
	@Override
	public synchronized boolean isDefined(String objName) {
		GeoElement geo = kernel.lookupLabel(objName);
		if (geo == null) {
			return false;
		}
		return geo.isDefined();
	}

	/**
	 * Returns true if the object with the given name is independent.
	 */
	@Override
	public synchronized boolean isIndependent(String objName) {
		GeoElement geo = kernel.lookupLabel(objName);
		if (geo == null) {
			return false;
		}
		return geo.isIndependent();
	}

	@Override
	public synchronized String getValueString(String objName) {
		return getValueString(objName, true);
	}

	@Override
	public synchronized String getValueString(String objName, boolean localized) {
		GeoElement geo = kernel.lookupLabel(objName);
		if (geo == null) {
			return "";
		}

		if (geo.isGeoText()) {
			return ((GeoText) geo).getTextString();
		}

		if (geo.isGeoCasCell()) {
			return ((GeoCasCell) geo).getOutput(StringTemplate.numericDefault);
		}
		StringTemplate template =
				localized ? StringTemplate.algebraTemplate : StringTemplate.noLocalDefault;

		return geo.getAlgebraDescriptionPublic(template);
	}

	/**
	 * Returns the definition of the object with the given name as a string.
	 */
	@Override
	public synchronized String getDefinitionString(String objName) {
		return getDefinitionString(objName, true);
	}

	@Override
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
	@Override
	public synchronized String getLaTeXString(String objName) {
		GeoElement geo = kernel.lookupLabel(objName);
		if (geo == null) {
			return "";
		}
		return geo.toValueString(StringTemplate.latexTemplate);
	}

	@Override
	public void evalLaTeX(String input, int mode) {
		app.getDrawEquation().checkFirstCall(app);
		TeXFormula tf = new TeXFormula(input);
		// TeXParser tp = new TeXParser(input, tf);
		// tp.parse();
		ListSerializationAdapter ad = mode == 1 ? new ListSerializationAdapter() : null;
		evalCommand(new TeXAtomSerializer(ad).serialize(tf.root));
	}

	/**
	 * 
	 * eg ggbApplet.evalMathML(
	 * "<mrow><mi> x</mi><mo> +</mo><mrow><mi> 1</mi><mo>/</mo><mi> 2</mi></mrow></mrow>"
	 * )
	 * 
	 * @param input
	 *            command as presentation mathml
	 * @return success
	 */
	public boolean evalMathML(String input) {
		try {
			kernel.getAlgebraProcessor().parseMathml(input, false, null, false,
					null);
		} catch (RuntimeException e) {
			Log.error(e.getMessage());
			return false;
		}

		return true;
	}

	/**
	 * Returns the command of the object with the given name as a string.
	 */
	@Override
	public synchronized String getCommandString(String objName) {
		return getCommandString(objName, true);
	}

	@Override
	public synchronized String getCommandString(String objName,
			boolean localize) {

		GeoElement geo = kernel.lookupLabel(objName);
		if (geo == null) {
			return "";
		}
		if (geo instanceof GeoCasCell) {
			return geo.getDefinitionDescription(
					localize ? StringTemplate.defaultTemplate
							: StringTemplate.noLocalDefault);
		}
		return geo.getDefinition(localize ? StringTemplate.defaultTemplate
				: StringTemplate.noLocalDefault);
	}

	@Override
	public synchronized String getCaption(String objName,
			boolean substituteVars) {
		GeoElement geo = kernel.lookupLabel(objName);
		if (geo == null) {
			return "";
		}
		return substituteVars ? geo.getCaption(StringTemplate.defaultTemplate)
				: geo.getRawCaption();
	}

	@Override
	public synchronized void setCaption(String objName, String caption) {
		GeoElement geo = kernel.lookupLabel(objName);
		if (geo == null) {
			return;
		}
		geo.setCaption(caption);
		geo.updateRepaint();
	}

	@Override
	public synchronized String getPerspectiveXML() {
		if (app.getGuiManager() == null
				|| app.getGuiManager().getLayout() == null) {
			if (app.getTmpPerspective(null) != null) {
				return app.getTmpPerspective(null).getXml();
			}
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
	@Override
	public synchronized double getXcoord(String objName) {
		GeoElement geo = kernel.lookupLabel(objName);
		if (geo == null) {
			return 0;
		}
		if (geo.isAbsoluteScreenLocateable()) {
			if (((AbsoluteScreenLocateable) geo).isAbsoluteScreenLocActive()) {
				return ((AbsoluteScreenLocateable) geo).getAbsoluteScreenLocX();
			}
		}
		return kernel.getExpressionNodeEvaluator().handleXcoord(geo,
				Operation.XCOORD);
	}

	/**
	 * Returns the y-coord of the object with the given name. Note: returns 0 if
	 * the object is not a point or a vector.
	 */
	@Override
	public synchronized double getYcoord(String objName) {
		GeoElement geo = kernel.lookupLabel(objName);
		if (geo == null) {
			return 0;
		}
		if (geo.isAbsoluteScreenLocateable()) {
			if (((AbsoluteScreenLocateable) geo).isAbsoluteScreenLocActive()) {
				return ((AbsoluteScreenLocateable) geo).getAbsoluteScreenLocY();
			}
		}
		return kernel.getExpressionNodeEvaluator().handleYcoord(geo,
				Operation.YCOORD);
	}

	@Override
	public synchronized double getZcoord(String objName) {
		GeoElement geo = kernel.lookupLabel(objName);
		if (geo == null) {
			return 0;
		}

		return kernel.getExpressionNodeEvaluator().handleZcoord(geo);
	}

	/**
	 * Sets the coordinates of the object with the given name. Note: if the
	 * specified object is not a point or a vector, nothing happens.
	 */
	@Override
	public synchronized void setCoords(String objName, double x, double y) {
		GeoElement geo = kernel.lookupLabel(objName);
		if (geo == null) {
			return;
		}
		CmdSetCoords.setCoords(geo, x, y);
	}

	@Override
	public synchronized void setCoords(String objName, double x, double y,
			double z) {
		GeoElement geo = kernel.lookupLabel(objName);
		if (geo == null) {
			return;
		}
		CmdSetCoords.setCoords(geo, x, y, z);
	}

	/**
	 * Returns the double value of the object with the given name. For a
	 * boolean, returns 0 for false, 1 for true Note: returns 0 if the object
	 * does not have a value.
	 */
	@Override
	public synchronized double getValue(String objName) {
		GeoNumberValue geo = kernel.getAlgebraProcessor()
				.evaluateToNumeric(objName, ErrorHelper.silent());
		if (geo == null) {
			return 0;
		}

		return geo.getDouble();
	}

	/**
	 * Sets the double value of the object with the given name. For a boolean 0
	 * -> false, any other value -> true Note: if the specified object is not a
	 * number, nothing happens.
	 */
	@Override
	public synchronized void setValue(String objName, double x) {
		GeoElement geo = kernel.lookupLabel(objName);
		if (geo == null || !geo.isIndependent()) {
			return;
		}

		CmdSetValue.setValue2(geo, new GeoNumeric(kernel.getConstruction(), x));
	}

	@Override
	public synchronized void setTextValue(String objName, String x) {
		GeoElement geo = kernel.lookupLabel(objName);
		if (geo == null || !geo.isGeoText() || !geo.isIndependent()) {
			return;
		}

		((GeoText) geo).setTextString(x);
		geo.updateRepaint();
	}

	@Override
	public synchronized void setListValue(String objName, double x, double y) {

		GeoElement geo = kernel.lookupLabel(objName);
		if (geo == null || !geo.isGeoList() || !geo.isIndependent()) {
			return;
		}

		Construction cons = kernel.getConstruction();

		CmdSetValue.setValue3(kernel, (GeoList) geo, (int) x,
				new GeoNumeric(cons, y));
	}

	/**
	 * Turns the repainting of all views on or off.
	 */
	@Override
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
	@Override
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
	@Override
	public synchronized void setAxesVisible(boolean xVisible,
			boolean yVisible) {
		setAxesVisible(1, xVisible, yVisible, false);
	}

	@Override
	public synchronized void setAxesVisible(int view, boolean xVisible,
			boolean yVisible, boolean zVisible) {
		int index = view < 0 ? 3 : view;
		if (index < 1 || index > 3) {
			return;
		}
		EuclidianSettings evs = app.getSettings().getEuclidian(index);
		evs.beginBatch();
		evs.setShowAxis(EuclidianViewInterfaceCommon.AXIS_X, xVisible);
		evs.setShowAxis(EuclidianViewInterfaceCommon.AXIS_Y, yVisible);
		evs.setShowAxis(EuclidianViewInterfaceCommon.AXIS_Z, zVisible);
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
			app.getEuclidianView2(1)
					.setAxesCornerCoordsVisible(showAxesCornerCoords);
		}
	}

	/**
	 * Shows or hides the coordinate grid in the graphics window.
	 */
	@Override
	public synchronized void setGridVisible(boolean flag) {
		app.getSettings().getEuclidian(1).showGrid(flag);
		app.getSettings().getEuclidian(2).showGrid(flag);
	}

	@Override
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
	@Override
	public synchronized int getObjectNumber() {
		return getAllObjectNames().length;
	}

	/**
	 * Returns the name of the n-th object of this construction.
	 */
	@Override
	public synchronized String getObjectName(int i) {
		String[] names = getAllObjectNames();

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
	@Override
	public synchronized void setXML(String xml) {
		app.setXML(xml, true);
		app.updateViewSizes();
	}

	/**
	 * Returns current construction in XML format. May be used for saving.
	 */
	@Override
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
	@Override
	public abstract String getBase64(boolean includeThumbnail);

	@Override
	final public String getBase64() {
		return getBase64(false);
	}

	@Override
	final public void setPenColor(int red, int green, int blue) {
		app.getActiveEuclidianView().getEuclidianController().getPen().defaultPenLine
				.setObjColor(GColor.newColor(red, green, blue));
	}

	@Override
	final public void setPenSize(int size) {
		app.getActiveEuclidianView().getEuclidianController().getPen().defaultPenLine
				.setLineThickness(size);
	}

	@Override
	public int getPenSize() {
		return app.getActiveEuclidianView().getEuclidianController().getPen()
				.getPenSize();
	}

	@Override
	public String getPenColor() {
		return StringUtil.toHtmlColor(app.getActiveEuclidianView()
				.getEuclidianController().getPen().getPenColor());
	}

	@Override
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

	@Override
	public void undo(boolean repaint) {
		app.getKernel().undo();
		if (repaint) {
			app.doRepaintViews();
		}
	}

	/**
	 * Undo without forced repaint
	 */
	public void undo() {
		undo(false);
	}

	/**
	 * Redo without forced repaint
	 */
	public void redo() {
		redo(false);
	}

	@Override
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
	 *
	 * @return if construction is saved
	 */
	public boolean isSaved() {
		return app.isSaved();
	}

	/**
	 * Deletes all construction elements
	 */
	@Override
	public void newConstruction() {
		app.fileNew();
	}

	/**
	 * @param view
	 *            view number
	 * @return JSON string describing the view
	 */
	@Override
	public String getViewProperties(int view) {
		EuclidianView ev = view == 2 ? app.getEuclidianView2(1)
				: app.getEuclidianView1();
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
	@Override
	public void setFont(String label, int size, boolean bold, boolean italic,
			boolean serif) {
		GeoElement geo = kernel.lookupLabel(label);
		if (geo instanceof TextProperties) {
			TextProperties text = (TextProperties) geo;
			text.setFontSizeMultiplier(size / (0.0
					+ app.getSettings().getFontSettings().getAppFontSize()));
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
	@Override
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
		} // try-catch

		// useful for debugging JavaScript
		if (debugOutput) {
			Log.debug("evalGeoGebraCAS\n input:" + cmdString + "\n" + "output: "
					+ ret);
		}
		return ret;
	}

	/**
	 * Performs login
	 * 
	 * @param token
	 *            login token
	 */
	@Override
	public void login(String token) {
		if (app.getLoginOperation() != null) {
			Log.debug("LTOKEN send via API");
			app.getLoginOperation().performTokenLogin(token, false);
		}
	}

	/**
	 * Log current user out
	 */
	@Override
	public void logout() {
		if (app.getLoginOperation() != null
				&& app.getLoginOperation().getModel() != null) {
			app.getLoginOperation().performLogOut();
			app.getLoginOperation().getModel().discardTimers();
		}
	}

	@Override
	public void setPerspective(String code) {
		if (code.startsWith("search:")) {
			app.openSearch(code.substring("search:".length()));
			return;
		}
		if (code.startsWith("save:")) {
			app.getGuiManager().save();
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
		
		setPerspectiveWithViews(code);
		if (app.getActiveEuclidianView() != null
				&& !kernel.getConstruction().isScriptRunningForGeo()) {
			app.getActiveEuclidianView().requestFocus();
		}
	}

	/**
	 * Set perspective using standard views, no special UI
	 * 
	 * @param code
	 *            perspective
	 */
	private void setPerspectiveWithViews(String code) {
		app.enableUseFullGui();
		if (code.startsWith("+") || code.startsWith("-")) {
			PerspectiveDecoder.decodeSimple(app, code);
			return;
		}

		// the exam setting is certainly false
		if (code.startsWith("<")) {
			try {
				app.getXMLio().parsePerspectiveXML(
						"<geogebra format=\"5.0\"><gui><perspectives>" + code
								+ "</perspectives></gui></geogebra>");
				if (app.getGuiManager() != null) {
					app.getGuiManager().updateGUIafterLoadFile(true, false);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return;
		}
		Perspective ps = PerspectiveDecoder.decode(code, kernel.getParser(),
				ToolBar.getAllToolsNoMacros(app.isHTML5Applet(), app.isExam(),
						app));
		if (app.getGuiManager() == null) {
			if (ps != null) {
				ArrayList<Perspective> perspectives = new ArrayList<>();
				ps.setId("tmp");
				perspectives.add(ps);
				app.setTmpPerspectives(perspectives);
			}
			return;
		}

		app.setPerspective(ps);
	}

	@Override
	public synchronized boolean getVisible(String label, int view) {
		if (view < -1 || view > 2 || view == 0) {
			return false;
		}
		GeoElement geo = kernel.lookupLabel(label);
		if (geo instanceof GeoAxisND) {
			EuclidianSettings evs = app.getSettings()
					.getEuclidian(view < 0 ? 3 : view);
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

	@Override
	public synchronized boolean getGridVisible() {
		return getGridVisible(1);
	}

	@Override
	public synchronized boolean getGridVisible(int view) {
		if (view < -1 || view > 2 || view == 0) {
			return false;
		}
		EuclidianSettings evs = app.getSettings()
				.getEuclidian(view < 0 ? 3 : view);
		return evs.getShowGrid();
	}

	@Override
	public int getCASObjectNumber() {
		return kernel.getConstruction().getCASObjectNumber();
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
	@Override
	public void setRounding(String rounding) {
		app.setRounding(rounding);
		kernel.updateConstruction(false);
		app.refreshViews();
		kernel.updateConstruction(false);
	}

	@Override
	public String getRounding() {
		if (kernel.useSignificantFigures) {
			return kernel.getPrintFigures() + "s";
		}
		return kernel.getPrintDecimals() + "";
	}

	@Override
	public String getVersion() {
		return GeoGebraConstants.VERSION_STRING;
	}

	public void updateConstruction() {
		kernel.updateConstruction();
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

			if ("parametric".equals(style)) {
				line.setMode(GeoLine.PARAMETRIC);
			} else if ("explicit".equals(style)) {
				line.setMode(GeoLine.EQUATION_EXPLICIT);
			} else if ("implicit".equals(style)) {
				line.setMode(GeoLine.EQUATION_IMPLICIT);
			}

			geo.updateRepaint();

		} else if (geo instanceof GeoConic) {

			GeoConic conic = (GeoConic) geo;

			if ("parametric".equals(style)) {
				conic.setToStringMode(GeoConicND.EQUATION_PARAMETRIC);
			} else if ("explicit".equals(style)) {
				conic.setToStringMode(GeoConicND.EQUATION_EXPLICIT);
			} else if ("implicit".equals(style)) {
				conic.setToStringMode(GeoConicND.EQUATION_IMPLICIT);
			} else if ("specific".equals(style)) {
				conic.setToStringMode(GeoConicND.EQUATION_SPECIFIC);
			}

			geo.updateRepaint();
		}
	}

	@Override
	public void enableCAS(boolean enable) {
		if (!app.isUnbundled()) {
			app.enableCAS(enable);
		}
	}

	@Override
	public void enable3D(boolean enable) {
		if (!app.isUnbundled()) {
			app.getSettings().getEuclidian(-1).setEnabled(enable);
		}
	}

	/**
	 * @param enable
	 *            whether geogebra-web applet rightclick enabled or not
	 */
	@Override
	public void enableRightClick(boolean enable) {
		app.setRightClickEnabled(enable);
	}

	/**
	 * @param enable
	 *            wheter labels draggable in geogebra-web applets or not
	 */
	@Override
	public void enableLabelDrags(boolean enable) {
		app.setLabelDragsEnabled(enable);
	}

	/**
	 * @param enable
	 *            wheter shift - drag - zoom enabled in geogebra-web applets or
	 *            not
	 */
	@Override
	public void enableShiftDragZoom(boolean enable) {
		app.setShiftDragZoomEnabled(enable);
	}

	@Override
	public void setAxisSteps(int view, String xStep, String yStep,
			String zStep) {
		int index = view < 0 ? 3 : view;
		if (index < 1 || index > 3) {
			return;
		}
		EuclidianSettings evs = app.getSettings().getEuclidian(index);
		evs.beginBatch();
		evs.setAxisNumberingDistance(0, this.algebraprocessor
				.evaluateToNumeric(xStep, ErrorHelper.silent()));
		evs.setAxisNumberingDistance(1, this.algebraprocessor
				.evaluateToNumeric(yStep, ErrorHelper.silent()));
		if (evs.is3D()) {
			evs.setAxisNumberingDistance(2, this.algebraprocessor
					.evaluateToNumeric(zStep, ErrorHelper.silent()));
		}
		evs.endBatch();
		kernel.notifyRepaint();
	}

	@Override
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

	@Override
	public String[] getAxisLabels(int view) {
		int index = view < 0 ? 3 : view;
		if (index < 1 || index > 3) {
			return new String[0];
		}
		EuclidianSettings evs = app.getSettings().getEuclidian(index);
		return Arrays.copyOf(evs.getAxesLabels(), evs.getAxesLabels().length);
	}

	@Override
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

	@Override
	public String[] getAxisUnits(int view) {
		int index = view < 0 ? 3 : view;
		if (index < 1 || index > 3) {
			return new String[0];
		}
		EuclidianSettings evs = app.getSettings().getEuclidian(index);
		return Arrays.copyOf(evs.getAxesUnitLabels(), evs.getAxesUnitLabels().length);
	}

	@Override
	public void setPointCapture(int view, int capture) {
		int index = view < 0 ? 3 : view;
		if (index < 1 || index > 3) {
			return;
		}
		EuclidianSettings evs = app.getSettings().getEuclidian(index);
		evs.setPointCapturing(capture);
		kernel.notifyRepaint();
	}

	@Override
	public void setAuxiliary(String objName, boolean flag) {
		GeoElement geo = kernel.lookupLabel(objName);
		geo.setAuxiliaryObject(flag);
		geo.updateRepaint();
	}

	/**
	 * Returns localized name of given tool.
	 *
	 * @param mode
	 *            number
	 * @return name of given tool.
	 */
	@Override
	public String getToolName(int mode) {
		return app.getToolName(mode);
	}

	/**
	 *
	 * @param handler
	 *            handle current construction as PGF/Tikz
	 */
	final public void exportPGF(final AsyncOperation<String> handler) {
		app.newGeoGebraToPgf(exportCallback(handler));
	}

	private AsyncOperation<GeoGebraExport> exportCallback(
			final AsyncOperation<String> handler) {
		return new AsyncOperation<GeoGebraExport>() {

			@Override
			public void callback(GeoGebraExport export) {
				if (export == null) {
					// not implemented eg Android, iOS)
					handler.callback("");
					return;
				}

				EuclidianView ev = app.getActiveEuclidianView();

				ExportFrameMinimal frame = new ExportFrameMinimal(ev.getYmin(),
						ev.getYmax());
				export.setFrame(frame);
				export.generateAllCode();

				handler.callback(frame.getCode());
			}
		};
	}

	/**
	 *
	 * @param handler
	 *            handle current construction as PSTricks
	 */
	final public void exportPSTricks(AsyncOperation<String> handler) {
		app.newGeoGebraToPstricks(exportCallback(handler));
	}

	/**
	 *
	 * @param handler
	 *            handle current construction in Asymptote format
	 */
	final public void exportAsymptote(AsyncOperation<String> handler) {
		app.newGeoGebraToAsymptote(exportCallback(handler));
	}

	/**
	 *
	 * @param text
	 *            text to copy to system clipboard
	 */
	final public void copyTextToClipboard(String text) {
		app.getCopyPaste().copyTextToSystemClipboard(text);
	}

	/**
	 * @param text
	 *            tooltip text
	 */
	public void showTooltip(String text) {
		// TODO Auto-generated method stub
	}

	@Override
	public boolean isTracing(String objName) {
		GeoElement geo = kernel.lookupLabel(objName);
		return geo != null && geo.getTrace();
	}

	@Override
	final public String exportCollada(double xmin, double xmax, double ymin,
			double ymax, double zmin, double zmax, double xyScale,
			double xzScale, double xTickDistance, double yTickDistance,
			double zTickDistance) {
		if (app.is3D()) {
			return app.getCompanion().exportCollada(xmin, xmax, ymin, ymax,
					zmin, zmax, xyScale, xzScale, xTickDistance, yTickDistance,
					zTickDistance);
		}
		return null;
	}

	@Override
	final public void exportGeometry3D(Geometry3DGetter getter, double xmin,
			double xmax, double ymin, double ymax, double zmin, double zmax,
			double xyScale, double xzScale, double xTickDistance,
			double yTickDistance, double zTickDistance) {
		if (app.is3D()) {
			app.getCompanion().exportGeometry3D(getter, xmin, xmax, ymin, ymax,
					zmin, zmax, xyScale, xzScale, xTickDistance, yTickDistance,
					zTickDistance);
		}
	}

	@Override
	final public String exportSimple3d(String name, double xmin, double xmax,
			double ymin,
			double ymax, double zmin, double zmax, double xyScale,
			double xzScale, double xTickDistance, double yTickDistance,
			double zTickDistance) {
		if (app.is3D()) {
			Geometry3DGetterSimple getter = new Geometry3DGetterSimple(name);
			app.getCompanion().exportGeometry3D(getter, xmin, xmax, ymin, ymax,
					zmin, zmax, xyScale, xzScale, xTickDistance, yTickDistance,
					zTickDistance);
			return getter.get().toString();
		}
		return "";
	}

	/**
	 * @param filename
	 *            output filename
	 * @return SVG export
	 */
	public String exportSVG(String filename) {
		// not implemented in Android, iOS
		return null;
	}

	/**
	 * @param exportScale
	 *            scale
	 * @param filename
	 *            output filename
	 * @param sliderLabel
	 *            animation slider
	 * @return PDF
	 */
	public String exportPDF(double exportScale, String filename,
			String sliderLabel) {
		// not implemented in Android, iOS
		return null;
	}

	/**
	 * @param sliderLabel
	 *            animation slider
	 * @param scale
	 *            scale
	 * @param timeBetweenFrames
	 *            delay between frames
	 * @param isLoop
	 *            whether to play as loop
	 * @param filename
	 *            filename
	 * @param rotate
	 *            rotation speed
	 */
	public void exportGIF(String sliderLabel, double scale,
			double timeBetweenFrames, boolean isLoop, String filename,
			double rotate) {
		// not implemented in Android, iOS
	}

	/**
	 * @param sliderLabel
	 *            animation slider
	 * @param scale
	 *            scale
	 * @param timeBetweenFrames
	 *            delay between frames
	 * @param isLoop
	 *            whether to play as loop
	 * @param filename
	 *            filename
	 * @param rotate
	 *            rotation speed
	 */
	public void exportWebM(String sliderLabel, double scale,
			double timeBetweenFrames, boolean isLoop, String filename,
			double rotate) {
		// only works in Chrome
	}

	/**
	 * @param columnNames
	 *            column names
	 * @return html of construction protocol
	 */
	public String exportConstruction(String... columnNames) {
		ArrayList<Columns> columns = new ArrayList<>();
		boolean useColors = false;
		for (String s : columnNames) {
			switch (s.toLowerCase(Locale.US)) {
			case "color":
				useColors = true;
				break;
			case "number":
				columns.add(Columns.NUMBER);
				break;
			case "name":
				columns.add(Columns.NAME);
				break;
			case "definition":
				columns.add(Columns.DEFINITION);
				break;
			case "description":
				columns.add(Columns.DESCRIPTION);
				break;
			case "value":
				columns.add(Columns.VALUE);
				break;
			case "caption":
				columns.add(Columns.CAPTION);
				break;
			case "breakpoint":
				columns.add(Columns.BREAKPOINT);
			default:
				Log.warn("Unknown column" + s);
			}
		}
		return ConstructionProtocolView.getHTML(null, app.getLocalization(),
				kernel, columns, useColors);
	}

	/**
	 *
	 * @param label
	 *            label of GeoElement
	 * @return screen reader output for GeoElement
	 */
	public String getScreenReaderOutput(String label) {
		GeoElement geo = kernel.lookupLabel(label);
		return geo.toValueString(StringTemplate.screenReader);
	}

	/**
	 * @param breakpoints
	 *            whether to return steps taking breakpoints into account
	 * @return number of steps
	 */
	public double getConstructionSteps(boolean breakpoints) {
		if (breakpoints) {
			return kernel.getBreakpointSteps();
		}

		// returns -1 for no objects
		// return kernel.getLastConstructionStep();
		// returns 0 for no objects
		return kernel.getConstruction().steps();
	}

	/**
	 * @param i
	 *            new step
	 * @param breakpoints
	 *            use breakpoints
	 */
	public void setConstructionStep(double i, boolean breakpoints) {
		int step = breakpoints ? kernel.getBreakpointStep((int) i) : (int) i;

		if (app.getGuiManager() != null) {
			app.getGuiManager().getConstructionProtocolView()
					.setConstructionStep(step);
		} else {
			kernel.setConstructionStep(step);
		}
	}

	/**
	 * Advance to previous construction step (using breakpoints if enabled in
	 * .ggb file)
	 */
	public void previousConstructionStep() {
		if (app.getGuiManager() != null) {
			app.getGuiManager().getConstructionProtocolView().previousStep();
		} else {
			kernel.previousStep();
		}
	}

	/**
	 * Advance to next construction step (using breakpoints if enabled in .ggb
	 * file)
	 */
	public void nextConstructionStep() {
		if (app.getGuiManager() != null) {
			app.getGuiManager().getConstructionProtocolView().nextStep();
		} else {
			kernel.nextStep();
		}
	}

	@Override
	public boolean writePNGtoFile(String filename, double exportScale,
			boolean transparent, double DPI, boolean grayscale) {
		// not implemented in iOS / Android
		return false;
	}

	@Override
	public void groupObjects(String[] objects) {
		kernel.getConstruction().groupObjects(objects);
	}

	@Override
	public void ungroupObjects(String[] objects) {
		kernel.getConstruction().ungroupObjects(objects);
	}

	@Override
	public String[] getObjectsOfItsGroup(String object) {
		return kernel.getConstruction().getObjectsOfItsGroup(object);
	}

	@Override
	public void addToGroup(String object, String[] objectsInGroup) {
		kernel.getConstruction().addToGroup(object, objectsInGroup);
	}

	/**
	 * @return exercise fraction (same as getValue("correct"))
	 */
	public double getExerciseFraction() {
		return getValue("correct");
	}

	@Override
	public void enableFpsMeasurement() {
		app.getFpsProfiler().setEnabled(true);
	}

	@Override
	public void disableFpsMeasurement() {
		app.getFpsProfiler().setEnabled(false);
	}

	@Override
	public void testDraw() {
		app.testDraw();
	}

	@Override
	public void startDrawRecording() {
		app.startDrawRecording();
	}

	@Override
	public void endDrawRecordingAndLogResults() {
		app.endDrawRecordingAndLogResults();
	}

	/**
	 * Update geo ordering in notes
	 * @param labels comma separated list of labels
	 */
	public void updateOrdering(String labels) {
		construction.getLayerManager().updateOrdering(labels, kernel);
		app.getActiveEuclidianView().invalidateDrawableList();
	}

	@Override
	public boolean hasUnlabeledPredecessors(String label) {
		return kernel.getConstruction().hasUnlabeledPredecessors(label);
	}
}
