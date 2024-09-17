package org.geogebra.common.euclidian;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.awt.GFont;
import org.geogebra.common.gui.dialog.options.model.AbsoluteScreenLocationModel;
import org.geogebra.common.gui.dialog.options.model.OptionsModel;
import org.geogebra.common.kernel.ConstructionDefaults;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.algos.AlgoAttachCopyToView;
import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.algos.AlgoTableText;
import org.geogebra.common.kernel.geos.AbsoluteScreenLocateable;
import org.geogebra.common.kernel.geos.AngleProperties;
import org.geogebra.common.kernel.geos.GProperty;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFormula;
import org.geogebra.common.kernel.geos.GeoImage;
import org.geogebra.common.kernel.geos.GeoInlineText;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.geos.GeoText;
import org.geogebra.common.kernel.geos.PointProperties;
import org.geogebra.common.kernel.geos.TextProperties;
import org.geogebra.common.kernel.geos.TextStyle;
import org.geogebra.common.kernel.geos.properties.FillType;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.main.App;
import org.geogebra.common.main.MyError;
import org.geogebra.common.main.MyError.Errors;
import org.geogebra.common.main.settings.EuclidianSettings;
import org.geogebra.common.main.settings.LabelVisibility;
import org.geogebra.common.util.AsyncOperation;
import org.geogebra.common.util.debug.Log;

public class EuclidianStyleBarStatic {

	public final static String[] bracketArray = { "\u00D8", "{ }", "( )", "[ ]",
			"| |", "|| ||" };
	private final static String[] bracketArray2 = { "\u00D8", "{ }", "( )",
			"[ ]", "||", "||||" };

	/**
	 * @param geos
	 *            elements
	 * @param flag
	 *            fixed
	 * @param ev
	 *            view
	 * @return new geoo if redefinition was needed
	 */
	public static GeoElement applyFixPosition(ArrayList<GeoElement> geos,
			boolean flag, EuclidianViewInterfaceCommon ev) {
		GeoElement ret = geos.get(0);
		App app = geos.get(0).getKernel().getApplication();

		// workaround to make sure pin icon disappears
		// see applyFixPosition() called with a geo with label not set below
		app.getSelectionManager().clearSelectedGeos(false);

		for (int i = 0; i < geos.size(); i++) {
			GeoElement geo = geos.get(i);

			// problem with ghost geos
			if (!geo.isLabelSet()) {
				Log.warn(
						"applyFixPosition() called with a geo with label not set: "
								+ geo.getLabelSimple());
				continue;
			}

			// put again labelled geo into selection
			app.getSelectionManager().addSelectedGeo(geo, false, false);

			if (geo.isGeoSegment()) {
				if (geo.getParentAlgorithm() != null
						&& geo.getParentAlgorithm().getInput().length == 3) {
					// segment is output from a Polygon
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
				AbsoluteScreenLocationModel
						.setAbsolute((AbsoluteScreenLocateable) geo, flag, ev);

			} else if (geo.isPinnable()) {
				Kernel kernelA = app.getKernel();

				GeoPoint corner1 = new GeoPoint(kernelA.getConstruction());
				GeoPoint corner3 = new GeoPoint(kernelA.getConstruction());
				GeoPoint screenCorner1 = new GeoPoint(
						kernelA.getConstruction());
				GeoPoint screenCorner3 = new GeoPoint(
						kernelA.getConstruction());

				int viewNo = 1;

				if (ev != null) {
					corner1.setCoords(ev.getXmin(), ev.getYmin(), 1);
					corner3.setCoords(ev.getXmax(), ev.getYmax(), 1);
					screenCorner1.setCoords(0, ev.getHeight(), 1);
					screenCorner3.setCoords(ev.getWidth(), 0, 1);
					viewNo = ev.getEuclidianViewNo();
				}

				// "false" here so that pinning works for eg polygons
				GeoElement geo0 = redefineGeo(geo, "AttachCopyToView["
						+ getDefinitonString(geo) + "," + viewNo + "]");

				if (i == 0) {
					ret = geo0;
				}

				geo.setEuclidianVisible(true);
				geo.updateRepaint();

			} else {
				// can't pin
				Log.debug("not pinnable");
				return null;
			}
		}

		app.getSelectionManager().updateSelection();

		return ret;
	}

	/**
	 * @param geos
	 *            elements
	 * @param flag
	 *            fix flag
	 * @param ev
	 *            view
	 * @return first geo or null if not fixable
	 */
	public static GeoElement applyFixObject(ArrayList<GeoElement> geos,
			boolean flag, EuclidianViewInterfaceCommon ev) {
		GeoElement ret = geos.get(0);

		App app = geos.get(0).getKernel().getApplication();

		// workaround to make sure pin icon disappears
		// see applyFixPosition() called with a geo with label not set below
		app.getSelectionManager().clearSelectedGeos(false);

		for (int i = 0; i < geos.size(); i++) {
			GeoElement geo = geos.get(i);

			if (geo.isDefaultGeo()) {
				if (geo.isFixable()) {
					geo.setFixed(flag);
					continue;
				}
			}

			// problem with ghost geos
			if (!geo.isLabelSet()) {
				Log.warn(
						"applyFixPosition() called with a geo with label not set: "
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
				Log.debug("not fixable");
				return null;
			}

		}

		app.getSelectionManager().updateSelection();

		return ret;
	}

	/**
	 * Fills selected geos with a given type of pattern.
	 * 
	 * @param geos
	 *            to fill.
	 * @param fillType
	 *            Type of the filling pattern
	 * @return whether fill type changed
	 */
	public static boolean applyFillType(ArrayList<GeoElement> geos,
			FillType fillType) {
		boolean changed = false;
		for (GeoElement geo : geos) {
			if (geo.isFillable()) {
				FillType oldType = geo.getFillType();
				if (oldType != fillType) {
					geo.setFillType(fillType);
					geo.updateVisualStyleRepaint(GProperty.HATCHING);
					changed = true;
				}
			}
		}
		return changed;
	}

	private static String getDefinitonString(GeoElement geo) {
		// needed for eg freehand functions
		String definitonStr = geo.getDefinition(StringTemplate.maxPrecision);

		// everything else
		if ("".equals(definitonStr)) {
			// "false" here so that pinning works for eg polygons
			definitonStr = geo.getFormulaString(StringTemplate.maxPrecision,
					false);
		}

		return definitonStr;

	}

	/**
	 * @param geo
	 *            original geo
	 * @param cmdtext
	 *            new definition
	 * @return redefined geo
	 */
	public static GeoElement redefineGeo(GeoElement geo, String cmdtext) {
		GeoElement newGeo = null;

		final App app = geo.getKernel().getApplication();

		if (cmdtext == null) {
			return newGeo;
		}

		Log.debug("redefining " + geo + " as " + cmdtext);

		try {
			app.getKernel().getAlgebraProcessor().changeGeoElement(geo, cmdtext,
					true, true, app.getDefaultErrorHandler(),
					new AsyncOperation<GeoElementND>() {

						@Override
						public void callback(GeoElementND newGeo1) {
							if (newGeo1 != null) {
								app.doAfterRedefine(newGeo1);
								newGeo1.updateRepaint();
							}
						}
					});

		} catch (Exception e) {
			app.showError(Errors.ReplaceFailed);
		} catch (MyError err) {
			app.showError(err);
		}
		return newGeo;
	}

	/**
	 * @param geos
	 *            selected elements
	 * @param justify
	 *            justification
	 * @param hSelected
	 *            horizontal selection
	 * @param vSelected
	 *            vertical selection
	 * @param index
	 *            index
	 * @param app
	 *            application
	 */
	public static void applyTableTextFormat(ArrayList<GeoElement> geos,
			String justify, boolean hSelected, boolean vSelected, int index,
			App app) {

		AlgoElement algo = null;
		GeoElement[] input;
		GeoElement geo;
		String arg = null;

		arg = justify;
		// if (this.btnTableTextLinesH.isSelected())
		if (hSelected) {
			arg += "_";
		}
		// if (this.btnTableTextLinesV.isSelected())
		if (vSelected) {
			arg += "|";
		}
		if (index > 0) {
			arg += bracketArray2[index];
		}
		ArrayList<GeoElement> newGeos = new ArrayList<>();

		StringBuilder cmdText = new StringBuilder();

		for (int i = 0; i < geos.size(); i++) {

			// get the TableText algo for this geo and its input
			geo = geos.get(i);
			algo = geo.getParentAlgorithm();
			input = algo.getInput();

			// create a new TableText cmd
			cmdText.setLength(0);
			cmdText.append("TableText[");
			for (int j = 0; j < input.length; j++) {
				if (input[j] instanceof GeoList) {
					cmdText.append(input[j]
							.getFormulaString(StringTemplate.defaultTemplate,
									false));
					cmdText.append(",");
				}
			}
			cmdText.append("\"");
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
	 * 
	 * @param geos
	 *            geos
	 * @return true if "label style" button applies on all geos
	 */

	public static GeoElement checkGeosForCaptionStyle(List<GeoElement> geos) {
		if (geos.size() <= 0) {
			return null;
		}

		for (GeoElement current : geos) {
			if (current.isLabelShowable()
					|| current.isGeoAngle()
					|| (current.isGeoNumeric() && current
					.isLockedPosition())) {
				return current;
			}
		}

		return null;
	}

	/**
	 * @param geos
	 *            elements
	 * @param mode
	 *            caption mode
	 * @param index
	 *            index
	 * @return success
	 */
	public static boolean applyCaptionStyle(ArrayList<GeoElement> geos,
			int mode, int index) {

		boolean needUndo = false;

		App app = geos.get(0).getKernel().getApplication();

		for (int i = 0; i < geos.size(); i++) {
			GeoElement geo = geos.get(i);
			if (geo.isLabelShowable()
					|| geo.isGeoAngle()
					|| (geo.isGeoNumeric() && geo.isLockedPosition())) {
				geo.setLabelModeFromStylebar(index);
			}
			geo.updateVisualStyle(GProperty.LABEL_STYLE);
			needUndo = true;
		}

		app.getKernel().notifyRepaint();
		return needUndo;
	}

	/**
	 * @param app application
	 * @param geos selected (or default) geos
	 * @param lineStyleIndex
	 *            line style index
	 * @param lineSize
	 *            line thickness
	 * @return success
	 */
	public static boolean applyLineStyle(int lineStyleIndex, int lineSize, App app,
			List<GeoElement> geos) {
		int lineStyle = EuclidianView.getLineType(lineStyleIndex);
		boolean needUndo = false;

		for (GeoElement geo : geos) {
			boolean thicknessChanged = geo.getLineThickness() != lineSize;
			if (geo.getLineType() != lineStyle
					|| thicknessChanged) {
				geo.setLineType(lineStyle);
				geo.setLineThickness(lineSize);
				geo.updateVisualStyleRepaint(GProperty.LINE_STYLE);
				needUndo = needUndo || !thicknessChanged;
			}
		}

		return needUndo;
	}

	/**
	 * @param app application
	 * @param geos selected (or default) geos
	 * @param lineStyleIndex
	 *            line style index
	 * @param lineSize
	 *            line thickness
	 * @return success
	 */
	public static boolean applyLineStyleSplitStrokes(int lineStyleIndex, int lineSize, App app,
			List<GeoElement> geos) {
		List<GeoElement> splitStrokes = splitStrokes(geos, app);
		UpdateStrokeStyleStore stylingHelper = new UpdateStrokeStyleStore(splitStrokes,
				app.getUndoManager());

		boolean needUndo = applyLineStyle(lineStyleIndex, lineSize, app, splitStrokes);

		if (needUndo) {
			stylingHelper.addUpdatedStrokes(splitStrokes);
			stylingHelper.storeStrokeStyleUpdateUndo();
		}

		return needUndo;
	}

	private static List<GeoElement> splitStrokes(List<GeoElement> geos, App app) {
		if (app.getActiveEuclidianView().getEuclidianController().splitSelectedStrokes(true)) {
			return app.getSelectionManager().getSelectedGeos();
		}
		return geos;
	}

	/**
	 * @param geos
	 *            elements
	 * @param pointStyleSelIndex
	 *            point style index
	 * @param pointSize
	 *            point size
	 * @return success
	 */
	public static boolean applyPointStyle(ArrayList<GeoElement> geos,
			int pointStyleSelIndex, int pointSize) {
		int pointStyle = EuclidianView.getPointStyle(pointStyleSelIndex);
		boolean needUndo = false;
		for (int i = 0; i < geos.size(); i++) {
			GeoElement geo = geos.get(i);
			if (geo instanceof PointProperties) {
				if (((PointProperties) geo).getPointSize() != pointSize
						|| (((PointProperties) geo)
								.getPointStyle() != pointStyle)) {
					((PointProperties) geo).setPointSize(pointSize);
					((PointProperties) geo).setPointStyle(pointStyle);
					geo.updateVisualStyleRepaint(GProperty.POINT_STYLE);
					needUndo = true;
				}
			}
		}

		return needUndo;
	}

	/**
	 * @param app application
	 * @param geos selected (or default) geos
	 * @param color
	 *            color
	 * @param alpha
	 *            opacity
	 * @return success
	 */
	public static boolean applyColor(GColor color, double alpha, App app, List<GeoElement> geos) {
		boolean needUndo = false;

		for (GeoElement geo : geos) {
			boolean alphaChanged = false;
			// apply object color to all other geos except images
			// (includes texts since MOW-441)
			if (geo instanceof GeoImage && geo.getAlphaValue() != alpha) {
				geo.setAlphaValue(alpha);
			} else if (geo.getObjectColor() != color
					|| geo.getAlphaValue() != alpha) {
				geo.setObjColor(color);
				// if we change alpha for functions, hit won't work properly
				if (geo.isFillable() && geo.getAlphaValue() != alpha) {
					geo.setAlphaValue(alpha);
					alphaChanged = true;
				}
				needUndo = needUndo || !alphaChanged;
			}
			geo.updateVisualStyle(GProperty.COLOR);
		}
		if (!geos.isEmpty()) {
			geos.get(0).getKernel().notifyRepaint();
		}

		return needUndo;
	}

	/**
	 * @param app application
	 * @param geos selected (or default) geos
	 * @param color
	 *            color
	 * @param alpha
	 *            opacity
	 * @return success
	 */
	public static boolean applyColorSplitStrokes(GColor color, double alpha, App app,
			List<GeoElement> geos) {
		List<GeoElement> splitStrokes = new ArrayList(splitStrokes(geos, app));
		splitStrokes = splitStrokes.stream().filter(geo -> !(geo instanceof GeoInlineText))
				.collect(Collectors.toList()); //Handled by InlineTextFormatter
		UpdateStrokeStyleStore strokeStyleHelper = new UpdateStrokeStyleStore(splitStrokes,
				app.getUndoManager());

		boolean needUndo = applyColor(color, alpha, app, splitStrokes);

		if (needUndo) {
			strokeStyleHelper.addUpdatedStrokes(splitStrokes);
			strokeStyleHelper.storeStrokeStyleUpdateUndo();
		}
		return needUndo;
	}

	/**
	 * @param geos
	 *            elements
	 * @param color
	 *            background color
	 * @param alpha
	 *            opacity
	 * @return success
	 */
	public static boolean applyBgColor(List<GeoElement> geos, GColor color,
			double alpha) {
		boolean needUndo = false;

		for (GeoElement geo : geos) {
			// if text geo, then apply background color
			if (geo instanceof TextStyle || geo instanceof GeoFormula) {
				if (geo.getBackgroundColor() != color
						|| geo.getAlphaValue() != alpha) {
					geo.setBackgroundColor(color);

					geo.updateVisualStyleRepaint(GProperty.COLOR_BG);
					needUndo = true;
				}
			}
		}
		return needUndo;
	}

	/**
	 * @param geos
	 *            elements
	 * @param mask
	 *            bits to filter (&amp;) from font style
	 * @param add
	 *            bits to add to font style
	 * @return success
	 */
	public static boolean applyFontStyle(ArrayList<GeoElement> geos, int mask,
			boolean add) {
		boolean needUndo = false;

		for (int i = 0; i < geos.size(); i++) {
			GeoElement geo = geos.get(i);
			if (geo instanceof TextProperties) {
				TextProperties text = (TextProperties) geo;
				int oldStyle = text.getFontStyle();
				int newStyle = add ? (oldStyle | mask) : (oldStyle & ~mask);
				if (oldStyle != newStyle) {
					text.setFontStyle(newStyle);
					text.updateVisualStyleRepaint(GProperty.FONT);
					needUndo = true;
				}
			}
		}
		return needUndo;
	}

	/**
	 * @param geos geos
	 * @return intersection of font styles of all the geos
	 */
	public static int getFontStyle(List<GeoElement> geos) {
		int style = GFont.ITALIC | GFont.BOLD | GFont.UNDERLINE;
		for (GeoElement geo : geos) {
			if (geo.getGeoElementForPropertiesDialog() instanceof TextStyle) {
				style &= ((TextStyle) geo.getGeoElementForPropertiesDialog()).getFontStyle();
			}
		}
		return style;
	}

	/**
	 * @param geos
	 *            elements
	 * @param textSizeIndex
	 *            text size index
	 * @return success
	 */
	public static boolean applyTextSize(ArrayList<GeoElement> geos,
			int textSizeIndex) {
		boolean needUndo = false;
		// transform indices to the range -4, // .. , 4
		double fontSize = GeoText.getRelativeFontSize(textSizeIndex);

		for (int i = 0; i < geos.size(); i++) {
			GeoElement geo = geos.get(i);
			if (geo instanceof TextProperties && ((TextProperties) geo)
					.getFontSizeMultiplier() != fontSize) {
				((TextProperties) geo).setFontSizeMultiplier(fontSize);
				((TextProperties) geo).updateVisualStyleRepaint(GProperty.FONT);
				needUndo = true;
			}
		}

		return needUndo;
	}

	/**
	 * Set point capturing
	 * @param ev euclidian view
	 * @return whether undo needed
	 */
	public static boolean processPointCapture(EuclidianViewInterfaceCommon ev) {
		int mode = ev.getStyleBar().getPointCaptureSelectedIndex();

		if (mode == 3 || mode == 0) {
			mode = 3 - mode; // swap 0 and 3
		}
		ev.setPointCapturing(mode);

		// update other EV stylebars since this is a global property
		ev.getApplication().updateStyleBars();
		return false;
	}

	/**
	 * Toggle grid visibility
	 * @param ev euclidian view
	 * @return whether undo needed
	 */
	public static boolean processGrid(EuclidianViewInterfaceCommon ev) {
		App app = ev.getApplication();
		EuclidianSettings evs = app.getSettings().getEuclidianForView(ev, app);
		boolean changed;
		if (evs != null) {
			changed = evs.showGrid(!evs.getShowGrid());
		} else {
			changed = ev.showGrid(!ev.getShowGrid());
		}
		ev.repaint();
		return changed;
	}

	/**
	 * Toggle axes visibility
	 * @param ev view
	 * @return whether undo needed
	 */
	public static boolean processAxes(EuclidianViewInterfaceCommon ev) {
		boolean changed;
		App app = ev.getApplication();
		EuclidianSettings evs = app.getSettings().getEuclidianForView(ev, app);
		if (evs != null) {
			changed = evs.setShowAxes(!evs.getShowAxis(0));
		} else {
			changed = ev.setShowAxes(!ev.getShowXaxis(), true);
		}
		ev.repaint();
		return changed;
	}

	/**
	 * @param geos
	 *            tables
	 * @param mode
	 *            current app mode
	 * @return table text
	 */
	public static AlgoTableText updateTableText(List<GeoElement> geos, int mode) {
		AlgoTableText tableText = null;
		if (geos == null || geos.size() == 0 || EuclidianView.isPenMode(mode)) {
			return tableText;
		}

		boolean geosOK = true;
		AlgoElement algo;

		for (int i = 0; i < geos.size(); i++) {
			algo = geos.get(i).getParentAlgorithm();
			if (!(algo instanceof AlgoTableText)) {
				geosOK = false;
			}
		}

		if (geosOK && geos.get(0) != null) {
			algo = geos.get(0).getParentAlgorithm();
			tableText = (AlgoTableText) algo;
		}

		return tableText;
	}

	/**
	 * @return map app mode -&gt; construction default object
	 */
	public static HashMap<Integer, Integer> createDefaultMap() {
		HashMap<Integer, Integer> defaultGeoMap = new HashMap<>();
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
		defaultGeoMap.put(EuclidianConstants.MODE_CIRCUMCIRCLE_ARC_THREE_POINTS,
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
		defaultGeoMap.put(EuclidianConstants.MODE_RELATION,
				ConstructionDefaults.DEFAULT_LIST);

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
	 * 
	 * @param geos
	 *            geos
	 * @return true if "fix position" button applies on all geos
	 */
	public static boolean checkGeosForFixPosition(List<GeoElement> geos) {
		if (geos.size() <= 0) {
			return false;
		}

		for (GeoElement geo : geos) {
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
	 * 
	 * @param geos
	 *            geos
	 * @return true if "fix object" button applies on all geos
	 */
	public static boolean checkGeosForFixObject(List<GeoElement> geos) {
		if (geos.size() <= 0) {
			return false;
		}

		for (GeoElement geo : geos) {
			if (!geo.isFixable()) {
				return false;
			}
		}
		return true;
	}

	/**
	 * check geos for "angle interval" button
	 * 
	 * @param geos
	 *            geos
	 * @return true if "angle interval" button applies on all geos
	 */
	public static GeoElement checkGeosForAngleInterval(List<GeoElement> geos) {
		if (geos.size() <= 0) {
			return null;
		}

		for (GeoElement geo : geos) {
			if ((geo.isDefaultGeo() || !geo.isIndependent())
					&& (geo instanceof AngleProperties) && !geo.isGeoList()
					|| OptionsModel.isAngleList(geo)) {

				return geo;
			}
		}

		return null;
	}

	/**
	 * 
	 * @param geo
	 *            geo
	 * @return true if the "fix position" button should be fixed for geo
	 */
	final static public boolean checkSelectedFixPosition(GeoElement geo) {
		if (geo instanceof AbsoluteScreenLocateable && !geo.isGeoList()) {
			AbsoluteScreenLocateable locateable = (AbsoluteScreenLocateable) geo
					.getGeoElementForPropertiesDialog();
			return locateable.isAbsoluteScreenLocActive();
		}

		return geo.getParentAlgorithm() instanceof AlgoAttachCopyToView;
	}

	/**
	 * 
	 * @param geo
	 *            geo
	 * @return true if the "fix object" button should be fixed for geo
	 */
	static public boolean checkSelectedFixObject(GeoElement geo) {
		return geo.isLocked();
	}

	/**
	 * @param geos
	 *            elements
	 * @param index
	 *            angle interval index
	 * @return success
	 */
	public static boolean applyAngleInterval(ArrayList<GeoElement> geos,
			int index) {

		boolean needUndo = false;

		for (int i = 0; i < geos.size(); i++) {
			GeoElement geo = geos.get(i);
			if (geo instanceof AngleProperties) {
				if (((AngleProperties) geo).getAngleStyle()
						.getXmlVal() != index) {
					((AngleProperties) geo).setAngleStyle(index);
					geo.updateVisualStyleRepaint(GProperty.ANGLE_INTERVAL);
					needUndo = true;
				}
			}
		}
		return needUndo;
	}

	/**
	 * 
	 * @param geo
	 *            geo giving the label mode
	 * @param app
	 *            application
	 * @return index to select label mode
	 */
	public static int getIndexForLabelMode(GeoElement geo, App app) {
		if (geo.isDefaultGeo()) {

			// check if default geo use default label
			if (geo.getLabelMode() == GeoElementND.LABEL_DEFAULT) {
				// label visibility
				LabelVisibility labelingStyle = app == null
						? LabelVisibility.UseDefaults
						: app.getCurrentLabelingStyle();

				// automatic labelling:
				// if algebra window open -> all labels
				// else -> no labels

				switch (labelingStyle) {
				case AlwaysOn:
				case UseDefaults:
				default:
					if (geo.isGeoNumeric()) {
						return GeoElementND.LABEL_NAME_VALUE + 1;
					}
					return GeoElementND.LABEL_NAME + 1;

				case AlwaysOff:
					if (geo.isGeoNumeric()) {
						return GeoElementND.LABEL_NAME + 1;
					}
					return 0;

				case PointsOnly:
					if (geo.isGeoNumeric()) {
						return GeoElementND.LABEL_NAME_VALUE + 1;
					}
					if (geo.isGeoPoint()) {
						return GeoElementND.LABEL_NAME + 1;
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

		if (!geo.getLabelVisible()) {
			return 0;
		}

		return geo.getLabelMode() + 1;
	}

}
