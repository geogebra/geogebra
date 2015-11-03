package org.geogebra.common.euclidian;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.gui.dialog.options.model.OptionsModel;
import org.geogebra.common.kernel.ConstructionDefaults;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.algos.AlgoAttachCopyToView;
import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.algos.AlgoTableText;
import org.geogebra.common.kernel.geos.AbsoluteScreenLocateable;
import org.geogebra.common.kernel.geos.AngleProperties;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoImage;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.geos.GeoText;
import org.geogebra.common.kernel.geos.PointProperties;
import org.geogebra.common.kernel.geos.TextProperties;
import org.geogebra.common.main.App;
import org.geogebra.common.main.MyError;
import org.geogebra.common.main.settings.EuclidianSettings;
import org.geogebra.common.util.debug.Log;

public class EuclidianStyleBarStatic {

	public final static String[] bracketArray = { "\u00D8", "{ }", "( )",
			"[ ]", "| |", "|| ||" };
	public final static String[] bracketArray2 = { "\u00D8", "{ }", "( )",
			"[ ]", "||", "||||" };
	public static Integer[] lineStyleArray;
	public static Integer[] pointStyleArray;

	public static GeoElement applyFixPosition(ArrayList<GeoElement> geos,
			boolean flag, EuclidianViewInterfaceCommon ev) {
		GeoElement ret = geos.get(0);

		AbsoluteScreenLocateable geoASL;

		App app = geos.get(0).getKernel().getApplication();

		// workaround to make sure pin icon disappears
		// see applyFixPosition() called with a geo with label not set below
		app.getSelectionManager().clearSelectedGeos(false);

		for (int i = 0; i < geos.size(); i++) {
			GeoElement geo = geos.get(i);

			// problem with ghost geos
			if (!geo.isLabelSet()) {
				Log.warn("applyFixPosition() called with a geo with label not set: "
						+ geo.getLabelSimple());
				continue;

			}
			
			// put again labelled geo into selection
			app.getSelectionManager().addSelectedGeo(geo, false, false);

			if (geo.isGeoSegment()) {
				if (geo.getParentAlgorithm() != null
						&& geo.getParentAlgorithm().getInput().length == 3) {
					// segment is output from a Polygon
					// AbstractApplication.warn("segment from poly");
					continue;
				}
			}

			if (geo.getParentAlgorithm() instanceof AlgoAttachCopyToView) {

				AlgoAttachCopyToView algo = (AlgoAttachCopyToView) geo
						.getParentAlgorithm();

				if (!flag) {

					GeoElement geo0 = redefineGeo(geo,
							getDefinitonString(algo.getInput()[0]));

					if (i == 0) {
						ret = geo0;
					}

				} else {
					algo.setEV(ev.getEuclidianViewNo()); // 1 or 2
				}

				geo.setEuclidianVisible(true);
				geo.updateRepaint();

			} else if (geo instanceof AbsoluteScreenLocateable
					&& !geo.isGeoList()) {
				geoASL = (AbsoluteScreenLocateable) geo;
				if (flag) {
					// convert real world to screen coords
					int x = ev.toScreenCoordX(geoASL.getRealWorldLocX());
					int y = ev.toScreenCoordY(geoASL.getRealWorldLocY());
					if (!geoASL.isAbsoluteScreenLocActive())
						geoASL.setAbsoluteScreenLoc(x, y);
				} else {
					// convert screen coords to real world
					double x = ev.toRealWorldCoordX(geoASL
							.getAbsoluteScreenLocX());
					double y = ev.toRealWorldCoordY(geoASL
							.getAbsoluteScreenLocY());
					if (geoASL.isAbsoluteScreenLocActive())
						geoASL.setRealWorldLoc(x, y);
				}
				geoASL.setAbsoluteScreenLocActive(flag);
				geo.updateRepaint();

			} else if (geo.isPinnable()) {
				Kernel kernelA = app.getKernel();

				GeoPoint corner1 = new GeoPoint(kernelA.getConstruction());
				GeoPoint corner3 = new GeoPoint(kernelA.getConstruction());
				GeoPoint screenCorner1 = new GeoPoint(kernelA.getConstruction());
				GeoPoint screenCorner3 = new GeoPoint(kernelA.getConstruction());
				if (ev != null) {
					corner1.setCoords(ev.getXmin(), ev.getYmin(), 1);
					corner3.setCoords(ev.getXmax(), ev.getYmax(), 1);
					screenCorner1.setCoords(0, ev.getHeight(), 1);
					screenCorner3.setCoords(ev.getWidth(), 0, 1);
				}

				// "false" here so that pinning works for eg polygons
				GeoElement geo0 = redefineGeo(geo,
						"AttachCopyToView[" + getDefinitonString(geo) + ","
								+ ev.getEuclidianViewNo() + "]");

				if (i == 0) {
					ret = geo0;
				}

				geo.setEuclidianVisible(true);
				geo.updateRepaint();

			} else {
				// can't pin
				App.debug("not pinnable");
				return null;
			}

			// app.addSelectedGeo(geo);

		}
		
		app.getSelectionManager().updateSelection();

		return ret;
	}
	
	
	public static GeoElement applyFixObject(ArrayList<GeoElement> geos,
			boolean flag, EuclidianViewInterfaceCommon ev) {
		GeoElement ret = geos.get(0);


		App app = geos.get(0).getKernel().getApplication();

		// workaround to make sure pin icon disappears
		// see applyFixPosition() called with a geo with label not set below
		app.getSelectionManager().clearSelectedGeos(false);

		for (int i = 0; i < geos.size(); i++) {
			GeoElement geo = geos.get(i);
			
			if (geo.isDefaultGeo()){
				if (geo.isFixable()){
					geo.setFixed(flag);
					continue;
				}
			}

			// problem with ghost geos
			if (!geo.isLabelSet()) {
				Log.warn("applyFixPosition() called with a geo with label not set: "
						+ geo.getLabelSimple());
				continue;

			}
			
			// put again labelled geo into selection
			app.getSelectionManager().addSelectedGeo(geo, false, false);


			if (geo.isFixable()) {
				geo.setFixed(flag);
				ret = geo;
			} else {
				// can't pin
				App.debug("not fixable");
				return null;
			}


		}
		
		app.getSelectionManager().updateSelection();

		return ret;
	}

	private static String getDefinitonString(GeoElement geo) {
		// needed for eg freehand functions
		String definitonStr = geo
				.getCommandDescription(StringTemplate.maxPrecision);

		// everything else
		if (definitonStr.equals("")) {
			// "false" here so that pinning works for eg polygons
			definitonStr = geo.getFormulaString(StringTemplate.maxPrecision,
					false);
		}

		return definitonStr;

	}

	public static GeoElement redefineGeo(GeoElement geo, String cmdtext) {
		GeoElement newGeo = null;

		App app = geo.getKernel().getApplication();

		if (cmdtext == null)
			return newGeo;

		App.debug("redefining " + geo + " as " + cmdtext);

		try {
			newGeo = app.getKernel().getAlgebraProcessor()
					.changeGeoElement(geo, cmdtext, true, true);
			app.doAfterRedefine(newGeo);
			newGeo.updateRepaint();
			return newGeo;

		} catch (Exception e) {
			app.showError("ReplaceFailed");
		} catch (MyError err) {
			app.showError(err);
		}
		return newGeo;
	}

	public static void applyTableTextFormat(ArrayList<GeoElement> geos,
			int justifyIndex, boolean HisSelected, boolean VisSelected,
			int index, App app) {

		AlgoElement algo = null;
		GeoElement[] input;
		GeoElement geo;
		String arg = null;

		String[] justifyArray = { "l", "c", "r" };
		// arg = justifyArray[btnTableTextJustify.getSelectedIndex()];
		arg = justifyArray[justifyIndex];
		// if (this.btnTableTextLinesH.isSelected())
		if (HisSelected) {
			arg += "_";
		}
		// if (this.btnTableTextLinesV.isSelected())
		if (VisSelected) {
			arg += "|";
		}
		if (index > 0) {
			arg += bracketArray2[index];
		}
		ArrayList<GeoElement> newGeos = new ArrayList<GeoElement>();

		StringBuilder cmdText = new StringBuilder();

		for (int i = 0; i < geos.size(); i++) {

			// get the TableText algo for this geo and its input
			geo = geos.get(i);
			algo = geo.getParentAlgorithm();
			input = algo.getInput();

			// create a new TableText cmd
			cmdText.setLength(0);
			cmdText.append("TableText[");
			cmdText.append(((GeoList) input[0]).getFormulaString(
					StringTemplate.defaultTemplate, false));
			cmdText.append(",\"");
			cmdText.append(arg);
			cmdText.append(
					((AlgoTableText) geo.getParentAlgorithm()).getAlignment());
			cmdText.append("\"]");

			// use the new cmd to redefine the geo and save it to a list.
			// (the list is needed to reselect the geo)
			newGeos.add(redefineGeo(geo, cmdText.toString()));
		}

		// reset the selection
		app.getSelectionManager().setSelectedGeos(newGeos);
	}
	
	
	/**
	 * check geos for "label style" button
	 * @param geos geos
	 * @return true if "label style" button applies on all geos
	 */
	final static public GeoElement checkGeosForCaptionStyle(Object[] geos, int mode, App app){
		if (geos.length <= 0) {
			return null;
		}
		
		GeoElement geo = null;
		for (int i = 0; i < geos.length; i++) {
			if (((GeoElement) geos[i]).isLabelShowable()
					|| ((GeoElement) geos[i]).isGeoAngle()
					|| (((GeoElement) geos[i]).isGeoNumeric() ? ((GeoNumeric) geos[i])
							.isSliderFixed() : false)) {
				geo = (GeoElement) geos[i];
				return geo;
			}
		}
		
		return null;
	}

	public static boolean applyCaptionStyle(ArrayList<GeoElement> geos,
			int mode, int index) {

		boolean needUndo = false;

		App app = geos.get(0).getKernel().getApplication();

		for (int i = 0; i < geos.size(); i++) {
			GeoElement geo = geos.get(i);
			if (geo.isLabelShowable()
					|| geo.isGeoAngle() 
					|| (geo.isGeoNumeric() ? ((GeoNumeric) geo).isSliderFixed() : false)
					) {
				geo.setLabelModeFromStylebar(index);
			}
			geo.updateVisualStyle();
			needUndo = true;
		}

		app.getKernel().notifyRepaint();
		return needUndo;
	}

	public static boolean applyLineStyle(ArrayList<GeoElement> geos,
			int lineStyleIndex, int lineSize) {
		int lineStyle = lineStyleArray[lineStyleIndex];
		boolean needUndo = false;

		for (int i = 0; i < geos.size(); i++) {
			GeoElement geo = geos.get(i);
			if (geo.getLineType() != lineStyle
					|| geo.getLineThickness() != lineSize) {
				geo.setLineType(lineStyle);
				geo.setLineThickness(lineSize);
				geo.updateVisualStyleRepaint();
				needUndo = true;
			}
		}

		return needUndo;
	}

	public static boolean applyPointStyle(ArrayList<GeoElement> geos,
			int pointStyleSelIndex, int pointSize) {
		int pointStyle = pointStyleArray[pointStyleSelIndex];
		boolean needUndo = false;
		for (int i = 0; i < geos.size(); i++) {
			GeoElement geo = geos.get(i);
			if (geo instanceof PointProperties) {
				if (((PointProperties) geo).getPointSize() != pointSize
						|| (((PointProperties) geo).getPointStyle() != pointStyle)) {
					((PointProperties) geo).setPointSize(pointSize);
					((PointProperties) geo).setPointStyle(pointStyle);
					geo.updateVisualStyleRepaint();
					needUndo = true;
				}
			}
		}

		return needUndo;
	}

	public static boolean applyColor(List<GeoElement> geos, GColor color,
			float alpha, App app) {
		boolean needUndo = false;
		for (int i = 0; i < geos.size(); i++) {
			GeoElement geo = geos.get(i);

			// apply object color to all other geos except images or text
			if (!(geo.getGeoElementForPropertiesDialog() instanceof GeoText)) {
				if (geo instanceof GeoImage && geo.getAlphaValue() != alpha) {
					geo.setAlphaValue(alpha);
				} else
				if ((geo.getObjectColor() != color || geo.getAlphaValue() != alpha)) {
					geo.setObjColor(color);
					// if we change alpha for functions, hit won't work properly
					if (geo.isFillable())
						geo.setAlphaValue(alpha);
				}

				geo.updateVisualStyle();
				needUndo = true;

			}
		}

		app.getKernel().notifyRepaint();
		return needUndo;
	}

	public static boolean applyBgColor(ArrayList<GeoElement> geos,
			GColor color, float alpha) {
		boolean needUndo = false;

		for (int i = 0; i < geos.size(); i++) {
			GeoElement geo = geos.get(i);

			// if text geo, then apply background color
			if (geo instanceof TextProperties)
				if (geo.getBackgroundColor() != color
						|| geo.getAlphaValue() != alpha) {
					geo.setBackgroundColor(color == null ? null : color);
					// TODO apply background alpha
					// --------
					geo.updateVisualStyleRepaint();
					needUndo = true;
				}
		}
		return needUndo;
	}

	public static boolean applyTextColor(List<GeoElement> geos,
			GColor color) {
		boolean needUndo = false;
		for (int i = 0; i < geos.size(); i++) {
			GeoElement geo = geos.get(i);
			if (geo.getGeoElementForPropertiesDialog() instanceof TextProperties
					&& geo.getObjectColor() != color) {
				geo.setObjColor(color);
				geo.updateVisualStyleRepaint();
				needUndo = true;
			}
		}
		return needUndo;
	}

	/**
	 * @param geos
	 * @param fontStyle
	 *            Value of fontStyle is 1 if btnBold pressed, 2 if btnItalic
	 *            pressed, 0 otherwise
	 * @return
	 */
	public static boolean applyFontStyle(ArrayList<GeoElement> geos, int mask,
			int add) {
		boolean needUndo = false;

		for (int i = 0; i < geos.size(); i++) {
			GeoElement geo = geos.get(i);
			if (geo instanceof TextProperties) {
				int newStyle = (((TextProperties) geo).getFontStyle() & mask)
						| add;
				if (((TextProperties) geo).getFontStyle() != newStyle) {
					((TextProperties) geo).setFontStyle(newStyle);
					geo.updateVisualStyleRepaint();
					needUndo = true;
				}
			}
		}
		return needUndo;
	}

	public static boolean applyTextSize(ArrayList<GeoElement> geos,
			int textSizeIndex) {
		boolean needUndo = false;

		double fontSize = GeoText.getRelativeFontSize(textSizeIndex); // transform
																		// indices
																		// to
																		// the
																		// range
																		// -4,
																		// .. ,
		// 4

		for (int i = 0; i < geos.size(); i++) {
			GeoElement geo = geos.get(i);
			if (geo instanceof TextProperties
					&& ((TextProperties) geo).getFontSizeMultiplier() != fontSize) {
				((TextProperties) geo).setFontSizeMultiplier(fontSize);
				geo.updateVisualStyleRepaint();
				needUndo = true;
			}
		}

		return needUndo;
	}

	/**
	 * process the action performed
	 * 
	 * @param actionCommand
	 * @param targetGeos
	 * @param ev
	 * @return
	 */
	// if all cases will be processed here, instead of
	// EuclidianStyleBar.processSource, the return value will be unnecessary
	public static boolean processSourceCommon(String actionCommand,
			ArrayList<GeoElement> targetGeos, EuclidianViewInterfaceCommon ev) {
		App app = ev.getApplication();
		// cons = app.getKernel().getConstruction();
		boolean changed = false;
		if (actionCommand.equals("showAxes")) {
			EuclidianSettings evs = app.getSettings().getEuclidianForView(ev,
					app);

			if (evs != null) {

				changed = evs.setShowAxes(!evs.getShowAxis(0));
			} else {
				changed = ev.setShowAxes(!ev.getShowXaxis(), true);
			}
			ev.repaint();
		}

		else if (actionCommand.equals("showGrid")) {
			EuclidianSettings evs = app.getSettings().getEuclidianForView(ev,
					app);
			if (evs != null) {
				changed = evs.showGrid(!evs.getShowGrid());
			} else {
				changed = ev.showGrid(!ev.getShowGrid());
			}
			ev.repaint();
		}

		else if (actionCommand.equals("standardView")) {

			// no parameters, always do this
			// app.setStandardView();
			ev.setStandardView(true);
		}

		else if (actionCommand.equals("pointCapture")) {
			int mode = ev.getStyleBar().getPointCaptureSelectedIndex();

			if (mode == 3 || mode == 0)
				mode = 3 - mode; // swap 0 and 3
			ev.setPointCapturing(mode);

			// update other EV stylebars since this is a global property
			app.updateStyleBars();
		}
		if (changed) {
			app.storeUndoInfo();
		} else {
			return false;
		}

		return true;
	}

	public static AlgoTableText updateTableText(Object[] geos, int mode) {

		AlgoTableText tableText = null;
		if (geos == null || geos.length == 0 || EuclidianView.isPenMode(mode))
			return tableText;

		boolean geosOK = true;
		AlgoElement algo;

		for (int i = 0; i < geos.length; i++) {
			algo = ((GeoElement) geos[i]).getParentAlgorithm();
			if (algo == null || !(algo instanceof AlgoTableText)) {
				geosOK = false;
			}
		}

		if (geosOK && geos[0] != null) {
			algo = ((GeoElement) geos[0]).getParentAlgorithm();
			tableText = (AlgoTableText) algo;
		}

		return tableText;
	}

	public static HashMap<Integer, Integer> createDefaultMap() {
		HashMap<Integer, Integer> defaultGeoMap = new HashMap<Integer, Integer>();
		defaultGeoMap.put(EuclidianConstants.MODE_POINT,
				ConstructionDefaults.DEFAULT_POINT_ALL_BUT_COMPLEX);
		defaultGeoMap.put(EuclidianConstants.MODE_COMPLEX_NUMBER,
				ConstructionDefaults.DEFAULT_POINT_COMPLEX);
		defaultGeoMap.put(EuclidianConstants.MODE_POINT_ON_OBJECT,
				ConstructionDefaults.DEFAULT_POINT_ALL_BUT_COMPLEX);
		defaultGeoMap.put(EuclidianConstants.MODE_INTERSECT,
				ConstructionDefaults.DEFAULT_POINT_DEPENDENT);
		defaultGeoMap.put(EuclidianConstants.MODE_MIDPOINT,
				ConstructionDefaults.DEFAULT_POINT_DEPENDENT);

		defaultGeoMap.put(EuclidianConstants.MODE_JOIN,
				ConstructionDefaults.DEFAULT_LINE);
		defaultGeoMap.put(EuclidianConstants.MODE_SEGMENT,
				ConstructionDefaults.DEFAULT_SEGMENT);
		defaultGeoMap.put(EuclidianConstants.MODE_SEGMENT_FIXED,
				ConstructionDefaults.DEFAULT_SEGMENT);
		defaultGeoMap.put(EuclidianConstants.MODE_RAY,
				ConstructionDefaults.DEFAULT_RAY);
		defaultGeoMap.put(EuclidianConstants.MODE_VECTOR,
				ConstructionDefaults.DEFAULT_VECTOR);
		defaultGeoMap.put(EuclidianConstants.MODE_VECTOR_FROM_POINT,
				ConstructionDefaults.DEFAULT_VECTOR);

		defaultGeoMap.put(EuclidianConstants.MODE_ORTHOGONAL,
				ConstructionDefaults.DEFAULT_LINE);
		defaultGeoMap.put(EuclidianConstants.MODE_PARALLEL,
				ConstructionDefaults.DEFAULT_LINE);
		defaultGeoMap.put(EuclidianConstants.MODE_LINE_BISECTOR,
				ConstructionDefaults.DEFAULT_LINE);
		defaultGeoMap.put(EuclidianConstants.MODE_ANGULAR_BISECTOR,
				ConstructionDefaults.DEFAULT_LINE);
		defaultGeoMap.put(EuclidianConstants.MODE_TANGENTS,
				ConstructionDefaults.DEFAULT_LINE);
		defaultGeoMap.put(EuclidianConstants.MODE_POLAR_DIAMETER,
				ConstructionDefaults.DEFAULT_LINE);
		defaultGeoMap.put(EuclidianConstants.MODE_FITLINE,
				ConstructionDefaults.DEFAULT_LINE);
		defaultGeoMap.put(EuclidianConstants.MODE_CREATE_LIST,
				ConstructionDefaults.DEFAULT_LIST);
		defaultGeoMap.put(EuclidianConstants.MODE_LOCUS,
				ConstructionDefaults.DEFAULT_LOCUS);

		defaultGeoMap.put(EuclidianConstants.MODE_POLYGON,
				ConstructionDefaults.DEFAULT_POLYGON);
		defaultGeoMap.put(EuclidianConstants.MODE_REGULAR_POLYGON,
				ConstructionDefaults.DEFAULT_POLYGON);
		defaultGeoMap.put(EuclidianConstants.MODE_RIGID_POLYGON,
				ConstructionDefaults.DEFAULT_POLYGON);
		defaultGeoMap.put(EuclidianConstants.MODE_VECTOR_POLYGON,
				ConstructionDefaults.DEFAULT_POLYGON);
		defaultGeoMap.put(EuclidianConstants.MODE_POLYLINE,
				ConstructionDefaults.DEFAULT_POLYLINE);

		defaultGeoMap.put(EuclidianConstants.MODE_CIRCLE_TWO_POINTS,
				ConstructionDefaults.DEFAULT_CONIC);
		defaultGeoMap.put(EuclidianConstants.MODE_CIRCLE_POINT_RADIUS,
				ConstructionDefaults.DEFAULT_CONIC);
		defaultGeoMap.put(EuclidianConstants.MODE_COMPASSES,
				ConstructionDefaults.DEFAULT_CONIC);
		defaultGeoMap.put(EuclidianConstants.MODE_CIRCLE_THREE_POINTS,
				ConstructionDefaults.DEFAULT_CONIC);
		defaultGeoMap.put(EuclidianConstants.MODE_SEMICIRCLE,
				ConstructionDefaults.DEFAULT_CONIC);
		defaultGeoMap.put(EuclidianConstants.MODE_CIRCLE_ARC_THREE_POINTS,
				ConstructionDefaults.DEFAULT_CONIC);
		defaultGeoMap.put(
				EuclidianConstants.MODE_CIRCUMCIRCLE_ARC_THREE_POINTS,
				ConstructionDefaults.DEFAULT_CONIC);
		defaultGeoMap.put(EuclidianConstants.MODE_CIRCLE_SECTOR_THREE_POINTS,
				ConstructionDefaults.DEFAULT_CONIC_SECTOR);
		defaultGeoMap.put(
				EuclidianConstants.MODE_CIRCUMCIRCLE_SECTOR_THREE_POINTS,
				ConstructionDefaults.DEFAULT_CONIC_SECTOR);

		defaultGeoMap.put(EuclidianConstants.MODE_ELLIPSE_THREE_POINTS,
				ConstructionDefaults.DEFAULT_CONIC);
		defaultGeoMap.put(EuclidianConstants.MODE_HYPERBOLA_THREE_POINTS,
				ConstructionDefaults.DEFAULT_CONIC);
		defaultGeoMap.put(EuclidianConstants.MODE_PARABOLA,
				ConstructionDefaults.DEFAULT_CONIC);
		defaultGeoMap.put(EuclidianConstants.MODE_CONIC_FIVE_POINTS,
				ConstructionDefaults.DEFAULT_CONIC);

		defaultGeoMap.put(EuclidianConstants.MODE_ANGLE,
				ConstructionDefaults.DEFAULT_ANGLE);
		defaultGeoMap.put(EuclidianConstants.MODE_ANGLE_FIXED,
				ConstructionDefaults.DEFAULT_ANGLE);

		defaultGeoMap.put(EuclidianConstants.MODE_DISTANCE,
				ConstructionDefaults.DEFAULT_TEXT);
		defaultGeoMap.put(EuclidianConstants.MODE_AREA,
				ConstructionDefaults.DEFAULT_TEXT);
		defaultGeoMap.put(EuclidianConstants.MODE_SLOPE,
				ConstructionDefaults.DEFAULT_POLYGON);

		defaultGeoMap.put(EuclidianConstants.MODE_MIRROR_AT_LINE,
				ConstructionDefaults.DEFAULT_NONE);
		defaultGeoMap.put(EuclidianConstants.MODE_MIRROR_AT_POINT,
				ConstructionDefaults.DEFAULT_NONE);
		defaultGeoMap.put(EuclidianConstants.MODE_MIRROR_AT_CIRCLE,
				ConstructionDefaults.DEFAULT_NONE);
		defaultGeoMap.put(EuclidianConstants.MODE_ROTATE_BY_ANGLE,
				ConstructionDefaults.DEFAULT_NONE);
		defaultGeoMap.put(EuclidianConstants.MODE_TRANSLATE_BY_VECTOR,
				ConstructionDefaults.DEFAULT_NONE);
		defaultGeoMap.put(EuclidianConstants.MODE_DILATE_FROM_POINT,
				ConstructionDefaults.DEFAULT_NONE);

		defaultGeoMap.put(EuclidianConstants.MODE_TEXT,
				ConstructionDefaults.DEFAULT_TEXT);
		defaultGeoMap.put(EuclidianConstants.MODE_SLIDER,
				ConstructionDefaults.DEFAULT_NUMBER);
		defaultGeoMap.put(EuclidianConstants.MODE_IMAGE,
				ConstructionDefaults.DEFAULT_IMAGE);

		defaultGeoMap.put(EuclidianConstants.MODE_SHOW_HIDE_CHECKBOX,
				ConstructionDefaults.DEFAULT_BOOLEAN);
		defaultGeoMap.put(EuclidianConstants.MODE_BUTTON_ACTION,
				ConstructionDefaults.DEFAULT_NONE);
		defaultGeoMap.put(EuclidianConstants.MODE_TEXTFIELD_ACTION,
				ConstructionDefaults.DEFAULT_NONE);

		return defaultGeoMap;
	}
	
	/**
	 * check geos for "fix position" button
	 * @param geos geos
	 * @return true if "fix position" button applies on all geos
	 */
	final static public boolean checkGeosForFixPosition(Object[] geos){
		if (geos.length <= 0) {
			return false;
		}

		for (int i = 0; i < geos.length; i++) {
			GeoElement geo = (GeoElement) geos[i];
			
			if (!geo.isPinnable()) {
				return false;
			}

			if (geo.isGeoSegment()) {
				if (geo.getParentAlgorithm() != null
						&& geo.getParentAlgorithm().getInput().length == 3) {
					// segment is output from a Polygon
					return false;
				}
			}

		}
		return true;
	}
	
	
	/**
	 * check geos for "fix object" button
	 * @param geos geos
	 * @return true if "fix object" button applies on all geos
	 */
	final static public boolean checkGeosForFixObject(Object[] geos){
		if (geos.length <= 0) {
			return false;
		}

		for (int i = 0; i < geos.length; i++) {
			GeoElement geo = (GeoElement) geos[i];
			if (!geo.isFixable()) {
				return false;
			}
		}
		return true;
	}
	
	
	/**
	 * check geos for "angle interval" button
	 * @param geos geos
	 * @return true if "angle interval" button applies on all geos
	 */
	final static public GeoElement checkGeosForAngleInterval(Object[] geos){
		if (geos.length <= 0) {
			return null;
		}

		for (int i = 0; i < geos.length; i++) {
			GeoElement geo = (GeoElement) geos[i];
			if ((geo.isDefaultGeo() || !geo.isIndependent())
					&& (geo instanceof AngleProperties) 
					&& !geo.isGeoList() || OptionsModel.isAngleList(geo)) {
				
				return geo;
			}
		}
		
		return null;
	}

	/**
	 * 
	 * @param geo geo
	 * @return true if the "fix position" button should be fixed for geo
	 */
	final static public boolean checkSelectedFixPosition(GeoElement geo){
		
		if (geo instanceof AbsoluteScreenLocateable
				&& !geo.isGeoList()) {
			AbsoluteScreenLocateable locateable = (AbsoluteScreenLocateable) geo
					.getGeoElementForPropertiesDialog();
			return locateable.isAbsoluteScreenLocActive();
		}
		
		if (geo.getParentAlgorithm() instanceof AlgoAttachCopyToView) {
			return true;
		} 
		
		return false;

	}
	
	/**
	 * 
	 * @param geo geo
	 * @return true if the "fix object" button should be fixed for geo
	 */
	final static public boolean checkSelectedFixObject(GeoElement geo){

		return geo.isFixed();

	}
	
	
	public static boolean applyAngleInterval(ArrayList<GeoElement> geos, int index) {

		boolean needUndo = false;
		
		for (int i = 0; i < geos.size(); i++) {
			GeoElement geo = geos.get(i);
			if (geo instanceof AngleProperties) {
				if (((AngleProperties) geo).getAngleStyle().getXmlVal() != index) {
					((AngleProperties) geo).setAngleStyle(index);
					geo.updateVisualStyleRepaint();
					needUndo = true;
				}
			}
		}
		return needUndo;
		
	}
	
	/**
	 * 
	 * @param geo geo giving the label mode
	 * @param app application
	 * @return index to select label mode 
	 */
	public static int getIndexForLabelMode(GeoElement geo, App app){

		if (geo.isDefaultGeo()) {

			// check if default geo use default label
			if (geo.getLabelMode() == GeoElement.LABEL_DEFAULT) {
				// label visibility
				int labelingStyle = app == null ? ConstructionDefaults.LABEL_VISIBLE_USE_DEFAULTS
						: app.getCurrentLabelingStyle();

				// automatic labelling:
				// if algebra window open -> all labels
				// else -> no labels

				switch (labelingStyle) {
				case ConstructionDefaults.LABEL_VISIBLE_ALWAYS_ON:
				case ConstructionDefaults.LABEL_VISIBLE_USE_DEFAULTS:
				default:
					if (geo.isGeoNumeric()) {
						return GeoElement.LABEL_NAME_VALUE + 1;
					}
					return GeoElement.LABEL_NAME + 1;

				case ConstructionDefaults.LABEL_VISIBLE_ALWAYS_OFF:
					if (geo.isGeoNumeric()) {
						return GeoElement.LABEL_NAME + 1;
					}
					return 0;

				case ConstructionDefaults.LABEL_VISIBLE_POINTS_ONLY:
					if (geo.isGeoNumeric()) {
						return GeoElement.LABEL_NAME_VALUE + 1;
					}
					if (geo.isGeoPoint()) {
						return GeoElement.LABEL_NAME + 1;
					}
					return 0;

				}
			}

			// default geo doesn't use default label
			if (!geo.getLabelVisible()) {
				return 0;
			}

			// shift for GeoElement.LABEL_DEFAULT_NAME, etc.
			return geo.getLabelMode() - 4;

		}
		
		if (!geo.getLabelVisible()){
			return 0;
		}
		
		return geo.getLabelMode() + 1;
	}

}
