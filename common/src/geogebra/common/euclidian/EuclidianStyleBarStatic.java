package geogebra.common.euclidian;

import geogebra.common.awt.GColor;
import geogebra.common.kernel.ConstructionDefaults;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.algos.AlgoAttachCopyToView;
import geogebra.common.kernel.algos.AlgoElement;
import geogebra.common.kernel.algos.AlgoTableText;
import geogebra.common.kernel.geos.AbsoluteScreenLocateable;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoImage;
import geogebra.common.kernel.geos.GeoList;
import geogebra.common.kernel.geos.GeoNumeric;
import geogebra.common.kernel.geos.GeoPoint;
import geogebra.common.kernel.geos.GeoText;
import geogebra.common.kernel.geos.PointProperties;
import geogebra.common.kernel.geos.TextProperties;
import geogebra.common.main.App;
import geogebra.common.main.MyError;

import java.util.ArrayList;
import java.util.HashMap;

public class EuclidianStyleBarStatic {

	
	public  final static String[] bracketArray = { "\u00D8", "{ }", "( )", "[ ]", "| |",
	"|| ||" };
	public final static String[] bracketArray2 = { "\u00D8", "{ }", "( )", "[ ]",
	"||", "||||" };
	public static Integer[] lineStyleArray;
	public static Integer[] pointStyleArray;

	public static GeoElement applyFixPosition(ArrayList<GeoElement> geos, boolean flag, EuclidianViewInterfaceCommon ev) {
		GeoElement ret = geos.get(0);
		
		AbsoluteScreenLocateable geoASL;
		
		App app = geos.get(0).getKernel().getApplication();
		
		// workaround to make sure pin icon disappears
		// see applyFixPosition() called with a geo with label not set below
		app.clearSelectedGeos(false);

		for (int i = 0; i < geos.size() ; i++) {
			GeoElement geo = geos.get(i);

			// problem with ghost geos
			if (!geo.isLabelSet()) {
				App.warn("applyFixPosition() called with a geo with label not set: "+geo.getLabelSimple());
				continue;
				
			}
			
			if (geo.isGeoSegment()) {
				if (geo.getParentAlgorithm() != null && geo.getParentAlgorithm().getInput().length == 3) {
					// segment is output from a Polygon
					//AbstractApplication.warn("segment from poly");
					continue;
				}
			}
			
			if (geo.getParentAlgorithm() instanceof AlgoAttachCopyToView) {
				
				AlgoAttachCopyToView algo = (AlgoAttachCopyToView)geo.getParentAlgorithm();
				
				if (!flag) {
					
					GeoElement geo0 = redefineGeo(geo, getDefinitonString(algo.getInput()[0]));
					
					if (i == 0) {
						ret = geo0;
					}

				} else {
					algo.setEV(ev.getEuclidianViewNo()); // 1 or 2
				}
				
				geo.setEuclidianVisible(true);
				geo.updateRepaint();
				
			} else if (geo instanceof AbsoluteScreenLocateable && !geo.isGeoList()) {
				geoASL = (AbsoluteScreenLocateable) geo;
				if (flag) {
					// convert real world to screen coords
					int x = ev.toScreenCoordX(geoASL.getRealWorldLocX());
					int y = ev.toScreenCoordY(geoASL.getRealWorldLocY());
					if (!geoASL.isAbsoluteScreenLocActive())
						geoASL.setAbsoluteScreenLoc(x, y);
				} else {
					// convert screen coords to real world
					double x = ev.toRealWorldCoordX(geoASL.getAbsoluteScreenLocX());
					double y = ev.toRealWorldCoordY(geoASL.getAbsoluteScreenLocY());
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
				if(ev!=null){
					corner1.setCoords(ev.getXmin(), ev.getYmin(), 1);
					corner3.setCoords(ev.getXmax(), ev.getYmax(), 1);
					screenCorner1.setCoords(0, ev.getHeight(), 1);
					screenCorner3.setCoords(ev.getWidth(), 0, 1);
				}
				
						
				// "false" here so that pinning works for eg polygons
				GeoElement geo0 = redefineGeo(geo, "AttachCopyToView["+ getDefinitonString(geo) +"," + ev.getEuclidianViewNo() + "]");
				
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
			
			//app.addSelectedGeo(geo);
			
		}
		
		return ret;
	}
	
	private static String getDefinitonString(GeoElement geo) {
		// needed for eg freehand functions
		String definitonStr = geo.getCommandDescription(StringTemplate.maxPrecision);
		
		// everything else
		if (definitonStr.equals("")) {
			// "false" here so that pinning works for eg polygons
			definitonStr = geo.getFormulaString(StringTemplate.maxPrecision, false);
		}
		
		return definitonStr;

	}

	public static GeoElement redefineGeo(GeoElement geo, String cmdtext) {
		GeoElement newGeo = null;
		
		App app = geo.getKernel().getApplication();

		if (cmdtext == null)
			return newGeo;
		
		App.debug("redefining "+geo+" as "+cmdtext);

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
	
	public static void applyTableTextFormat(ArrayList<GeoElement> geos, int justifyIndex, boolean HisSelected, boolean VisSelected, int index, App app) {

		AlgoElement algo = null;
		GeoElement[] input;
		GeoElement geo;
		String arg = null;

		String[] justifyArray = { "l", "c", "r" };
		//arg = justifyArray[btnTableTextJustify.getSelectedIndex()];
		arg = justifyArray[justifyIndex];
		//if (this.btnTableTextLinesH.isSelected())
		if (HisSelected)
			arg += "_";
		//if (this.btnTableTextLinesV.isSelected())
		if (VisSelected)
			arg += "|";
		if (index > 0)
			arg += bracketArray2[index];
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
			cmdText.append("\"]");

			// use the new cmd to redefine the geo and save it to a list.
			// (the list is needed to reselect the geo)
			newGeos.add(redefineGeo(geo, cmdText.toString()));
		}

		// reset the selection
		app.setSelectedGeos(newGeos);
	}
	
	public static boolean applyCaptionStyle(ArrayList<GeoElement> geos, int mode, int index) {
		
		boolean needUndo = false;
		
		App app = geos.get(0).getKernel().getApplication();
		
		for (int i = 0; i < geos.size(); i++) {
			GeoElement geo = geos.get(i);
			if ((mode == EuclidianConstants.MODE_MOVE && (geo.isLabelShowable()
					|| geo.isGeoAngle() || (geo.isGeoNumeric() ? ((GeoNumeric) geo)
					.isSliderFixed() : false)))
					|| (app.getLabelingStyle() == ConstructionDefaults.LABEL_VISIBLE_POINTS_ONLY
							&& geo.isLabelShowable() && geo.isGeoPoint())
					|| (app.getLabelingStyle() == ConstructionDefaults.LABEL_VISIBLE_ALWAYS_ON
							&& geo.isLabelShowable() || geo.isGeoAngle() || (geo
								.isGeoNumeric() ? ((GeoNumeric) geo)
							.isSliderFixed() : false))
					|| (app.getLabelingStyle() == ConstructionDefaults.LABEL_VISIBLE_AUTOMATIC
							&& geo.isLabelShowable() || geo.isGeoAngle() || (geo
								.isGeoNumeric() ? ((GeoNumeric) geo)
							.isSliderFixed() : false))) {
				if (index == 0) {
					if (mode == EuclidianConstants.MODE_MOVE
							|| app.getLabelingStyle() != ConstructionDefaults.LABEL_VISIBLE_ALWAYS_ON) {
						geo.setLabelVisible(false);
					}
				} else {
					geo.setLabelVisible(true);
					geo.setLabelMode(index - 1);
				}
			}
			geo.updateVisualStyle();
			needUndo = true;
		}
		
		app.getKernel().notifyRepaint();
		return needUndo;
	}

	public static boolean applyLineStyle(ArrayList<GeoElement> geos, int lineStyleIndex, int lineSize) {
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
	
	public static boolean applyPointStyle(ArrayList<GeoElement> geos, int pointStyleSelIndex, int pointSize) {
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
	
	public static boolean applyColor(ArrayList<GeoElement> geos, GColor color, float alpha, App app) {
		boolean needUndo = false;
		for (int i = 0; i < geos.size(); i++) {
			GeoElement geo = geos.get(i);
			// apply object color to all other geos except images or text
			if (!(geo.getGeoElementForPropertiesDialog() instanceof GeoImage || geo
					.getGeoElementForPropertiesDialog() instanceof GeoText))
				if ((geo.getObjectColor() != color || geo
						.getAlphaValue() != alpha)) {
					geo.setObjColor(color);
					// if we change alpha for functions, hit won't work properly
					if (geo.isFillable())
						geo.setAlphaValue(alpha);
					geo.updateVisualStyle();
					needUndo = true;
				}
		}

		app.getKernel().notifyRepaint();
		return needUndo;
	}

	public static boolean applyBgColor(ArrayList<GeoElement> geos, GColor color, float alpha) {
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
					geo.updateRepaint();
					needUndo = true;
				}
		}
		return needUndo;
	}

	public static boolean applyTextColor(ArrayList<GeoElement> geos, GColor color) {
		boolean needUndo = false;
		for (int i = 0; i < geos.size(); i++) {
			GeoElement geo = geos.get(i);
			if (geo.getGeoElementForPropertiesDialog() instanceof TextProperties
					&& geo.getObjectColor() != color) {
				geo.setObjColor(color);
				geo.updateRepaint();
				needUndo = true;
			}
		}
		return needUndo;
	}

	/**
	 * @param geos
	 * @param fontStyle Value of fontStyle is 1 if btnBold pressed, 2 if btnItalic pressed, 0 otherwise
	 * @return
	 */
	public static boolean applyFontStyle(ArrayList<GeoElement> geos, int fontStyle) {
		boolean needUndo = false;
		
		for (int i = 0; i < geos.size(); i++) {
			GeoElement geo = geos.get(i);
			if (geo instanceof TextProperties
					&& ((TextProperties) geo).getFontStyle() != fontStyle) {
				((TextProperties) geo).setFontStyle(fontStyle);
				geo.updateRepaint();
				needUndo = true;
			}
		}
		return needUndo;
	}

	public static boolean applyTextSize(ArrayList<GeoElement> geos, int textSizeIndex) {
		boolean needUndo = false;
		
		double fontSize = GeoText.getRelativeFontSize(textSizeIndex); // transform indices to the range -4, .. ,
										// 4

		for (int i = 0; i < geos.size(); i++) {
			GeoElement geo = geos.get(i);
			if (geo instanceof TextProperties
					&& ((TextProperties) geo).getFontSizeMultiplier() != fontSize) {
				((TextProperties) geo).setFontSizeMultiplier(fontSize);
				geo.updateRepaint();
				needUndo = true;
			}
		}
		
		return needUndo;
	}
	
	/**
	 * process the action performed
	 * @param actionCommand
	 * @param targetGeos
	 * @param ev 
	 * @return 
	 */
	// if all cases will be processed here, instead of
	// EuclidianStyleBar.processSource, the return value will be unnecessary
	public static boolean processSourceCommon(String actionCommand, ArrayList<GeoElement> targetGeos, EuclidianViewInterfaceCommon ev) {
		App app = ev.getApplication();
		//cons = app.getKernel().getConstruction();
		
		if (actionCommand.equals("showAxes")) {
			if (app.getEuclidianView1() == ev)
				app.getSettings().getEuclidian(1)
						.setShowAxes(!ev.getShowXaxis(), !ev.getShowXaxis());
			else if (!app.hasEuclidianView2EitherShowingOrNot())
				ev.setShowAxes(!ev.getShowXaxis(), true);
			else if (app.getEuclidianView2() == ev)
				app.getSettings().getEuclidian(2)
						.setShowAxes(!ev.getShowXaxis(), !ev.getShowXaxis());
			else
				ev.setShowAxes(!ev.getShowXaxis(), true);
			ev.repaint();
		}

		else if (actionCommand.equals("showGrid")) {
			if (app.getEuclidianView1() == ev)
				app.getSettings().getEuclidian(1).showGrid(!ev.getShowGrid());
			else if (!app.hasEuclidianView2EitherShowingOrNot())
				ev.showGrid(!ev.getShowGrid());
			else if (app.getEuclidianView2() == ev)
				app.getSettings().getEuclidian(2).showGrid(!ev.getShowGrid());
			else
				ev.showGrid(!ev.getShowGrid());
			ev.repaint();
		}

		else if (actionCommand.equals("pointCapture")) {
			int mode = ev.getStyleBar().getPointCaptureSelectedIndex();
			
			if (mode == 3 || mode == 0)
				mode = 3 - mode; // swap 0 and 3
			ev.setPointCapturing(mode);

			// update other EV stylebars since this is a global property
			app.updateStyleBars();

		} else {
			return false;
		}
		
		return true;
	}
		
	public static AlgoTableText updateTableText(Object[] geos, int mode) {

		AlgoTableText tableText = null;
		if (geos == null || geos.length == 0
				|| EuclidianView.isPenMode(mode))
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
				ConstructionDefaults.DEFAULT_POINT_FREE);
		defaultGeoMap.put(EuclidianConstants.MODE_COMPLEX_NUMBER,
				ConstructionDefaults.DEFAULT_POINT_FREE);
		defaultGeoMap.put(EuclidianConstants.MODE_POINT_ON_OBJECT,
				ConstructionDefaults.DEFAULT_POINT_DEPENDENT);
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
				ConstructionDefaults.DEFAULT_LINE);
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
				ConstructionDefaults.DEFAULT_POLYGON);

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

}
