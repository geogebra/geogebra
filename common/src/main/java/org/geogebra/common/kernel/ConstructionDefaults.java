/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package org.geogebra.common.kernel;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.io.MyXMLio;
import org.geogebra.common.kernel.geos.GeoAngle;
import org.geogebra.common.kernel.geos.GeoBoolean;
import org.geogebra.common.kernel.geos.GeoConic;
import org.geogebra.common.kernel.geos.GeoConicPart;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.geos.GeoFunctionNVar;
import org.geogebra.common.kernel.geos.GeoImage;
import org.geogebra.common.kernel.geos.GeoLine;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoLocus;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.geos.GeoPolyLine;
import org.geogebra.common.kernel.geos.GeoPolygon;
import org.geogebra.common.kernel.geos.GeoRay;
import org.geogebra.common.kernel.geos.GeoSegment;
import org.geogebra.common.kernel.geos.GeoText;
import org.geogebra.common.kernel.geos.GeoVector;
import org.geogebra.common.kernel.kernelND.GeoConicNDConstants;
import org.geogebra.common.kernel.kernelND.GeoConicPartND;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.main.App;
import org.geogebra.common.main.Feature;
import org.geogebra.common.main.GeoGebraColorConstants;
import org.geogebra.common.plugin.EuclidianStyleConstants;
import org.geogebra.common.plugin.GeoClass;

/**
 * Manages default settings for GeoElement objects in a construction.
 * 
 * @author Markus Hohenwarter
 */
public class ConstructionDefaults {
	/** default alpha for polygons */
	public static final float DEFAULT_POLYGON_ALPHA = 0.1f;

	// DEFAULT GeoElement types
	/** not a default geo */
	public static final int DEFAULT_NONE = -1;
	/** default free point */
	public static final int DEFAULT_POINT_FREE = 10;
	/** default dependent point */
	public static final int DEFAULT_POINT_DEPENDENT = 11;
	/** default point on path */
	public static final int DEFAULT_POINT_ON_PATH = 12;
	/** default point in region */
	public static final int DEFAULT_POINT_IN_REGION = 13;
	/** default complex point */
	public static final int DEFAULT_POINT_COMPLEX = 14;
	/** default type for free point */
	public static final int DEFAULT_POINT_ALL_BUT_COMPLEX = 15;

	/** default line */
	public static final int DEFAULT_LINE = 20;
	/** default segment */
	public static final int DEFAULT_SEGMENT = 21;
	/** default inequality */
	public static final int DEFAULT_INEQUALITY = 23;
	/**
	 * not actively used but we need this to get rid of inequality1var that is
	 * stored in preferences by mistake
	 */
	public static final int DEFAULT_INEQUALITY_1VAR = 24;
	/** default ray */
	public static final int DEFAULT_RAY = 25;

	/** default vector */
	public static final int DEFAULT_VECTOR = 30;
	/** default conic */
	public static final int DEFAULT_CONIC = 40;
	/** default conic sector */
	public static final int DEFAULT_CONIC_SECTOR = 41;

	/** default number */
	public static final int DEFAULT_NUMBER = 50;
	/** default angle */
	public static final int DEFAULT_ANGLE = 52;

	/** default function */
	public static final int DEFAULT_FUNCTION = 60;
	/** default multivariable function */
	public static final int DEFAULT_FUNCTION_NVAR = 65;
	/** default polygon */
	public static final int DEFAULT_POLYGON = 70;
	/** default polyline */
	public static final int DEFAULT_POLYLINE = 71;
	/** default locus */
	public static final int DEFAULT_LOCUS = 80;
	/** default text */
	public static final int DEFAULT_TEXT = 100;
	/** default image */
	public static final int DEFAULT_IMAGE = 110;
	/** default boolean */
	public static final int DEFAULT_BOOLEAN = 120;

	/** default */
	public static final int DEFAULT_LIST = 130;

	private static final GColor[] colorSequence = new GColor[] {
			GeoGebraColorConstants.GGB_GREEN, GeoGebraColorConstants.GGB_RED,
			GColor.BLUE, GeoGebraColorConstants.GGB_ORANGE,
			GeoGebraColorConstants.GGB_PURPLE, GColor.BLACK,
			GeoGebraColorConstants.GGB_GRAY, GeoGebraColorConstants.GGB_BROWN };

	// DEFAULT COLORs
	// points
	/** default color for points */
	public static final GColor colPoint = GColor.BLUE;

	/** default color for dependent points */
	public static final GColor colDepPoint = GColor.DARK_GRAY;

	/** default color for points on path */
	public static final GColor colPathPoint = GeoGebraColorConstants.LIGHTBLUE;

	/** default color for points in region */
	public static final GColor colRegionPoint = colPathPoint;

	/** default color for complex numbers */
	public static final GColor colComplexPoint = colPoint;

	// lines
	/** default color for lines */
	private static final GColor colLine = GColor.BLACK;

	/** default color for inequalities */
	private static final GColor colInequality = GColor.BLUE;

	/** Color for conics **/
	protected static final GColor colConic = GColor.BLACK;

	/** default alpha for conics */
	public static final float DEFAULT_CONIC_ALPHA = 0f;

	// polygons
	/** default color for polygons */
	public static final GColor colPolygon = GeoGebraColorConstants.GGB_BROWN;

	/** default alpha for inequalities */
	public static final float DEFAULT_INEQUALITY_ALPHA = 0.25f;

	// angles
	/** default color for angles */
	private static final GColor colAngle() {
		return GeoGebraColorConstants.GGB_GREEN;
	}

	/** default alpha for angles */
	public static final float DEFAULT_ANGLE_ALPHA = 0.1f;

	// numbers eg integrals, barcharts
	/** default alpha for integrals, barcharts, .. */
	public static final float DEFAULT_NUMBER_ALPHA = 0.1f;

	// locus lines
	private static final GColor colLocus = GColor.BLACK;

	// functions
	private static final GColor colFunction = GColor.BLACK;

	// lists
	private static final GColor colList = GeoGebraColorConstants.GGB_GREEN;

	// quadrics
	/** default alpha for quadrics */
	public static final float DEFAULT_QUADRIC_ALPHA = 0.75f;
	/** default color for quadrics */
	public static final GColor colQuadric = GeoGebraColorConstants.GGB_RED;

	/** preview color */
	public static final GColor colPreview = GColor.DARK_GRAY;

	/** preview fill color */
	public static final GColor colPreviewFill = colPolygon
			.deriveWithAlpha((int) (DEFAULT_POLYGON_ALPHA * 255));

	// label visibility
	/** label visible automatic */
	public static final int LABEL_VISIBLE_AUTOMATIC = 0;
	/** label visible for all new objects */
	public static final int LABEL_VISIBLE_ALWAYS_ON = 1;
	/** label visible for no new objects */
	public static final int LABEL_VISIBLE_ALWAYS_OFF = 2;
	/** label visible only for new points */
	public static final int LABEL_VISIBLE_POINTS_ONLY = 3;
	/** label visible based on default geos */
	public static final int LABEL_VISIBLE_USE_DEFAULTS = 4;

	/** default font size multiplier */
	public static final double DEFAULT_BUTTON_SIZE = 2;

	/** construction */
	protected Construction cons;

	/** defaultGeoElement list */
	protected HashMap<Integer, GeoElement> defaultGeoElements;

	private int lineThickness = EuclidianStyleConstants.DEFAULT_LINE_THICKNESS;
	private int pointSize = EuclidianStyleConstants.DEFAULT_POINT_SIZE;
	private int dependentPointSize = EuclidianStyleConstants.DEFAULT_POINT_SIZE_DEPENDENT;
	private int angleSize = EuclidianStyleConstants.DEFAULT_ANGLE_SIZE;
	private float filling = DEFAULT_POLYGON_ALPHA;

	private boolean blackWhiteMode = false;

	/**
	 * Creates a new ConstructionDefaults object to manage the default objects
	 * of this construction.
	 * 
	 * @param cons2
	 *            construction
	 */
	public ConstructionDefaults(Construction cons2) {
		this.cons = cons2;
		createDefaultGeoElements();
	}

	/**
	 * Returns a set of all default GeoElements used by this construction.
	 * 
	 * @return set of (integer,geo) pairs
	 */
	public Set<Map.Entry<Integer, GeoElement>> getDefaultGeos() {
		return defaultGeoElements.entrySet();
	}

	/** suffix for default free point name */
	protected String strFree = " (free)";
	/** suffix for default dependent point name */
	protected String strDependent = " (dependent)";


	/**
	 * Fills the list of default geos
	 */
	public void createDefaultGeoElements() {
		defaultGeoElements = new HashMap<Integer, GeoElement>();

		// free point
		GeoPoint freePoint = new GeoPoint(cons);
		// freePoint.setLocalVariableLabel(app.getPlain("Point") + strFree);
		freePoint.setPointStyle(EuclidianStyleConstants.POINT_STYLE_DOT);
		freePoint.setLocalVariableLabel("Point" + strFree);
		freePoint.setObjColor(colPoint);
		freePoint.setPointSize(pointSize);
		freePoint.setDefaultGeoType(DEFAULT_POINT_FREE);
		defaultGeoElements.put(DEFAULT_POINT_FREE, freePoint);

		// dependent point
		GeoPoint depPoint = new GeoPoint(cons);
		// depPoint.setLocalVariableLabel(app.getPlain("Point") + strDependent);
		depPoint.setPointStyle(EuclidianStyleConstants.POINT_STYLE_DOT);
		depPoint.setLocalVariableLabel("Point" + strDependent);
		depPoint.setObjColor(colDepPoint);
		depPoint.setPointSize(dependentPointSize);
		depPoint.setDefaultGeoType(DEFAULT_POINT_DEPENDENT);
		defaultGeoElements.put(DEFAULT_POINT_DEPENDENT, depPoint);

		// point on path
		GeoPoint pathPoint = new GeoPoint(cons);
		// pathPoint.setLocalVariableLabel(app.getPlain("PointOn"));
		pathPoint.setPointStyle(EuclidianStyleConstants.POINT_STYLE_DOT);
		pathPoint.setLocalVariableLabel("PointOn");
		pathPoint.setObjColor(colPathPoint);
		pathPoint.setPointSize(pointSize);
		pathPoint.setDefaultGeoType(DEFAULT_POINT_ON_PATH);
		defaultGeoElements.put(DEFAULT_POINT_ON_PATH, pathPoint);

		// point in region
		GeoPoint regionPoint = new GeoPoint(cons);
		// regionPoint.setLocalVariableLabel(app.getPlain("PointOn"));
		regionPoint.setPointStyle(EuclidianStyleConstants.POINT_STYLE_DOT);
		regionPoint.setLocalVariableLabel("PointInRegion");
		regionPoint.setObjColor(colRegionPoint);
		regionPoint.setPointSize(pointSize);
		regionPoint.setDefaultGeoType(DEFAULT_POINT_IN_REGION);
		defaultGeoElements.put(DEFAULT_POINT_IN_REGION, regionPoint);

		// complex number (handled like a point)
		GeoPoint complexPoint = new GeoPoint(cons);
		complexPoint.setPointStyle(EuclidianStyleConstants.POINT_STYLE_DOT);
		complexPoint.setLocalVariableLabel("PointOn");
		complexPoint.setObjColor(colComplexPoint);
		complexPoint.setPointSize(pointSize);
		complexPoint.setDefaultGeoType(DEFAULT_POINT_COMPLEX);
		complexPoint.setMode(Kernel.COORD_COMPLEX);
		defaultGeoElements.put(DEFAULT_POINT_COMPLEX, complexPoint);

		// line
		GeoLine line = new GeoLine(cons);
		// line.setLocalVariableLabel(app.getPlain("Line"));
		line.setLocalVariableLabel("Line");
		line.setObjColor(colLine);
		line.setDefaultGeoType(DEFAULT_LINE);
		line.setMode(GeoLine.EQUATION_IMPLICIT);
		defaultGeoElements.put(DEFAULT_LINE, line);

		// segment
		GeoSegment seg = new GeoSegment(cons);
		seg.setLocalVariableLabel("Segment");
		seg.setObjColor(colLine);
		seg.setDefaultGeoType(DEFAULT_SEGMENT);
		defaultGeoElements.put(DEFAULT_SEGMENT, seg);

		// segment
		GeoRay ray = new GeoRay(cons);
		ray.setLocalVariableLabel("Segment");
		ray.setObjColor(colLine);
		ray.setDefaultGeoType(DEFAULT_RAY);
		defaultGeoElements.put(DEFAULT_RAY, ray);

		GeoFunctionNVar inequality = new GeoFunctionNVar(cons);
		// inequality.setLocalVariableLabel("Inequality");
		inequality.setObjColor(colInequality);
		inequality.setAlphaValue(DEFAULT_INEQUALITY_ALPHA);
		inequality.setDefaultGeoType(DEFAULT_INEQUALITY);
		defaultGeoElements.put(DEFAULT_INEQUALITY, inequality);

		GeoFunction inequality1 = new GeoFunction(cons);
		inequality1.setDefaultGeoType(DEFAULT_INEQUALITY_1VAR);
		defaultGeoElements.put(DEFAULT_INEQUALITY_1VAR, inequality1);

		// function n var
		GeoFunctionNVar functionNV = new GeoFunctionNVar(cons);
		// functionNV.setLocalVariableLabel("function");
		functionNV.setObjColor(colQuadric);
		functionNV.setAlphaValue(DEFAULT_QUADRIC_ALPHA);
		functionNV.setDefaultGeoType(DEFAULT_FUNCTION_NVAR);
		defaultGeoElements.put(DEFAULT_FUNCTION_NVAR, functionNV);

		// vector
		GeoVector vector = new GeoVector(cons);
		vector.setLocalVariableLabel("Vector");
		vector.setObjColor(colLine);
		vector.setDefaultGeoType(DEFAULT_VECTOR);
		defaultGeoElements.put(DEFAULT_VECTOR, vector);

		// polygon
		GeoPolygon polygon = new GeoPolygon(cons, null);
		// polygon.setLocalVariableLabel(app.getPlain("Polygon"));
		polygon.setLocalVariableLabel("Polygon");
		polygon.setObjColor(colPolygon);
		polygon.setAlphaValue(DEFAULT_POLYGON_ALPHA);
		polygon.setDefaultGeoType(DEFAULT_POLYGON);
		defaultGeoElements.put(DEFAULT_POLYGON, polygon);

		// polyline
		GeoPolyLine polyline = new GeoPolyLine(cons);
		polyline.setLocalVariableLabel("Polyline");
		polyline.setObjColor(colLine);
		polyline.setDefaultGeoType(DEFAULT_POLYLINE);
		defaultGeoElements.put(DEFAULT_POLYLINE, polyline);

		// conic
		GeoConic conic = new GeoConic(cons);
		// conic.setLocalVariableLabel(app.getPlain("Conic"));
		conic.setLocalVariableLabel("Conic");
		conic.setObjColor(colConic);
		conic.setAlphaValue(DEFAULT_CONIC_ALPHA);
		conic.setDefaultGeoType(DEFAULT_CONIC);
		defaultGeoElements.put(DEFAULT_CONIC, conic);

		// conic sector
		GeoConicPart conicSector = new GeoConicPart(cons,
				GeoConicNDConstants.CONIC_PART_SECTOR);
		// conicSector.setLocalVariableLabel(app.getPlain("Sector"));
		conicSector.setLocalVariableLabel("Sector");
		conicSector.setObjColor(colPolygon);
		conicSector.setAlphaValue(DEFAULT_POLYGON_ALPHA);
		conicSector.setDefaultGeoType(DEFAULT_CONIC_SECTOR);
		defaultGeoElements.put(DEFAULT_CONIC_SECTOR, conicSector);

		// number
		GeoNumeric number = new GeoNumeric(cons);
		// number.setLocalVariableLabel(app.getPlain("Numeric"));
		number.setLocalVariableLabel("Numeric");
		number.setSliderFixed(true);

		/*
		 * we have to set min/max/increment/speed here because
		 * SetEuclideanVisible takes these from default geo
		 */
		number.setIntervalMax(GeoNumeric.DEFAULT_SLIDER_MAX);
		number.setIntervalMin(GeoNumeric.DEFAULT_SLIDER_MIN);
		number.setAnimationStep(GeoNumeric.DEFAULT_SLIDER_INCREMENT);
		number.setAutoStep(true);

		number.setAnimationSpeed(GeoNumeric.DEFAULT_SLIDER_SPEED);
		number.setAlphaValue(DEFAULT_NUMBER_ALPHA);
		number.setDefaultGeoType(DEFAULT_NUMBER);
		number.setLineThickness(GeoNumeric.DEFAULT_THICKNESS);
		number.setSliderWidth(GeoNumeric.DEFAULT_SLIDER_WIDTH_PIXEL);
		number.setSliderFixed(true);
		defaultGeoElements.put(DEFAULT_NUMBER, number);

		// angle
		GeoAngle angle = new GeoAngle(cons);
		// angle.setLocalVariableLabel(app.getPlain("Angle"));
		angle.setLocalVariableLabel("Angle");
		angle.setSliderFixed(true);
		angle.setObjColor(colAngle());
		angle.setAlphaValue(DEFAULT_ANGLE_ALPHA);
		angle.setDrawable(true, false);
		angle.setDrawable(true, false);
		angle.setAutoStep(true);
		angle.setArcSize(angleSize);
		/*
		 * we have to set min/max/increment/speed here because
		 * SetEuclideanVisible takes these from default geo
		 */
		angle.setIntervalMax(GeoAngle.DEFAULT_SLIDER_MAX_ANGLE);
		angle.setIntervalMin(GeoAngle.DEFAULT_SLIDER_MIN_ANGLE);
		angle.setAnimationStep(GeoAngle.DEFAULT_SLIDER_INCREMENT_ANGLE);
		angle.setAnimationSpeed(GeoNumeric.DEFAULT_SLIDER_SPEED);
		angle.setDefaultGeoType(DEFAULT_ANGLE);
		// can't do this here for sliders as it affects Angle[A,B,C] too
		// see GeoNumeric.setSliderFromDefault()
		// angle.setLineThickness(GeoNumeric.DEFAULT_THICKNESS);
		angle.setSliderWidth(GeoNumeric.DEFAULT_SLIDER_WIDTH_PIXEL_ANGLE);
		angle.setLineTypeHidden(EuclidianStyleConstants.LINE_TYPE_HIDDEN_AS_NOT_HIDDEN);
		defaultGeoElements.put(DEFAULT_ANGLE, angle);

		// function
		GeoFunction function = new GeoFunction(cons);
		// function.setLocalVariableLabel(app.getPlain("Function"));
		function.setLocalVariableLabel("Function");
		function.setObjColor(colFunction);
		function.setDefaultGeoType(DEFAULT_FUNCTION);
		function.setLineThickness(3);
		function.remove();
		function.setAutoColor(true);

		defaultGeoElements.put(DEFAULT_FUNCTION, function);

		// locus
		GeoLocus locus = new GeoLocus(cons);
		// locus.setLocalVariableLabel(app.getPlain("Locus"));
		locus.setLocalVariableLabel("Locus");
		locus.setObjColor(colLocus);
		locus.setLabelVisible(false);
		locus.setDefaultGeoType(DEFAULT_LOCUS);
		defaultGeoElements.put(DEFAULT_LOCUS, locus);

		// text
		GeoText text = new GeoText(cons);
		// text.setLocalVariableLabel(app.getPlain("Text"));
		text.setLocalVariableLabel("Text");
		text.setDefaultGeoType(DEFAULT_TEXT);
		if (cons.getApplication().has(Feature.ABSOLUTE_TEXTS)) {
			try {
				text.setAbsoluteScreenLocActive(true);
				text.setStartPoint(null);
			} catch (CircularDefinitionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		defaultGeoElements.put(DEFAULT_TEXT, text);

		// image
		GeoImage img = new GeoImage(cons);
		// img.setLocalVariableLabel(app.getPlain("Image"));
		img.setLocalVariableLabel("Image");
		img.setDefaultGeoType(DEFAULT_IMAGE);
		defaultGeoElements.put(DEFAULT_IMAGE, img);

		// boolean
		GeoBoolean bool = new GeoBoolean(cons);
		// bool.setLocalVariableLabel(app.getPlain("Boolean"));
		bool.setLocalVariableLabel("Boolean");
		bool.setDefaultGeoType(DEFAULT_BOOLEAN);
		defaultGeoElements.put(DEFAULT_BOOLEAN, bool);

		// list
		GeoList list = new GeoList(cons);
		// list.setLocalVariableLabel(app.getPlain("List"));
		list.setShowAllProperties(true); // show all properties in the defaults
											// dialog
		list.setLocalVariableLabel("List");
		list.setObjColor(colList);
		list.setAlphaValue(-1); // wait until we have an element in the list
								// then we will use the alphaValue of the first
								// element in the list
								// see GeoList.setAlphaValue() and
								// getAlphaValue()
		list.setDefaultGeoType(DEFAULT_LIST);
		defaultGeoElements.put(DEFAULT_LIST, list);
	}

	/**
	 * Returns a default GeoElement of this construction.
	 * 
	 * @param type
	 *            use DEFAULT_* constants (e.g. DEFAULT_POINT_FREE)
	 * @return default geo for given type
	 */
	public GeoElement getDefaultGeo(int type) {
		return defaultGeoElements.get(type);
	}

	/**
	 * Adds a key/value pair to defaultGeoElements. (used by
	 * Euclidian.EuclidianStyleBar to restore a default geo to previous state)
	 * 
	 * @param defaultType
	 *            default type
	 * @param geo
	 *            geo
	 */
	public void addDefaultGeo(Integer defaultType, GeoElement geo) {
		defaultGeoElements.put(defaultType, geo);
	}

	/**
	 * return the default type of the GeoElement
	 * 
	 * @param geo
	 *            a GeoElement
	 * @return the default type
	 */
	public int getDefaultType(GeoElement geo) {

		return getDefaultType(geo, geo.getGeoClassType());

	}

	/**
	 * return the default type of the geo
	 * 
	 * @param geo
	 *            a GeoElement
	 * @param geoClass
	 *            a GeoClass (may be different for 3D geos)
	 * @return the default type
	 */
	public int getDefaultType(GeoElement geo, GeoClass geoClass) {
		int type;

		switch (geoClass) {
		case POINT:
			GeoPointND p = (GeoPointND) geo;

			if (p.getMode() == Kernel.COORD_COMPLEX) {
				type = DEFAULT_POINT_COMPLEX;
			} else if (p.isIndependent()) {
				type = DEFAULT_POINT_FREE;
			} else {
				if (p.hasPath())
					type = DEFAULT_POINT_ON_PATH;
				else if (p.hasRegion())
					type = DEFAULT_POINT_IN_REGION;
				else
					type = DEFAULT_POINT_DEPENDENT;
			}
			break;

		case ANGLE:
			type = DEFAULT_ANGLE;
			break;

		case BOOLEAN:
			type = DEFAULT_BOOLEAN;
			break;

		case CONIC:
			type = DEFAULT_CONIC;
			break;

		case CONICPART:
			GeoConicPartND conicPart = (GeoConicPartND) geo;
			if (conicPart.getConicPartType() == GeoConicNDConstants.CONIC_PART_SECTOR) {
				type = DEFAULT_CONIC_SECTOR;
			} else {
				type = DEFAULT_CONIC;
			}
			break;

		case FUNCTION_NVAR:
			type = getDefaultTypeForFunctionNVar((GeoFunctionNVar) geo);
			break;
		case FUNCTION:
			if (((GeoFunction) geo).isBooleanFunction()) {
				type = DEFAULT_INEQUALITY;
			} else {
				type = DEFAULT_FUNCTION;
			}
			break;
		case INTERVAL:
			type = DEFAULT_INEQUALITY;
			break;

		case IMAGE:
			type = DEFAULT_IMAGE;
			break;

		case LIST:
			type = DEFAULT_LIST;
			break;

		case LOCUS:
			type = DEFAULT_LOCUS;
			break;

		case NUMERIC:
			type = DEFAULT_NUMBER;
			break;

		case POLYGON:
			type = DEFAULT_POLYGON;
			break;

		case POLYLINE:
			type = DEFAULT_POLYLINE;
			break;

		case TEXT:
			type = DEFAULT_TEXT;
			break;

		case VECTOR:
			type = DEFAULT_VECTOR;
			break;

		case SEGMENT:
			type = DEFAULT_SEGMENT;
			break;
		case RAY:
			type = DEFAULT_RAY;
			break;

		default:
			// all object types that are not specifically supported
			// should get the default values of a line
			type = DEFAULT_LINE;
		}
		

		return type;
	}

	/**
	 * 
	 * @param geo
	 *            function n var
	 * @return default type for this geo
	 */
	protected int getDefaultTypeForFunctionNVar(GeoFunctionNVar geo) {
		int type;
		if (geo.isBooleanFunction()) {
			type = DEFAULT_INEQUALITY;
		} else {
			type = DEFAULT_FUNCTION_NVAR;
		}
		return type;
	}

	/**
	 * 
	 * set geo to max layer used or max layer-1 if all layers used (layer 9
	 * reserved so that it's always over new objects)
	 * 
	 * @param geo
	 *            geo
	 * @param app
	 *            app
	 */
	private static void setMaxLayerUsed(GeoElement geo, App app) {
		if (app != null) {
			int layer = Math.min(EuclidianStyleConstants.MAX_LAYERS - 1,
					app.getMaxLayerUsed());

			geo.setLayer(layer);
		}
	}

	/**
	 * Sets default color for given geo. Note: this is mostly kept for downward
	 * compatibility.
	 * 
	 * @param geo
	 *            The element which needs new default visual styles
	 * @param isReset
	 *            If the visual styles should be reset
	 */
	final public void setDefaultVisualStyles(GeoElement geo, boolean isReset) {
		setDefaultVisualStyles(geo, isReset, true);
	}

	/**
	 * Sets default color for given geo. Note: this is mostly kept for downward
	 * compatibility.
	 * 
	 * @param geo
	 *            The element which needs new default visual styles
	 * @param isReset
	 *            If the visual styles should be reset
	 * @param setEuclidianVisible
	 *            If eucldianVisible should be set
	 */
	final public void setDefaultVisualStyles(GeoElement geo, boolean isReset,
			boolean setEuclidianVisible) {
		// all object types that are not specifically supported
		// should get the default values of a line
		// int type = DEFAULT_LINE;
		int type = getDefaultType(geo);

		// default
		GeoElement defaultGeo = getDefaultGeo(type);
		App app = cons.getApplication();

		boolean defaultLabelMode = true;
		
		if (defaultGeo != null) {
			if (!setEuclidianVisible || geo.isGeoNumeric()) { // don't affect
																// euclidianVisible
																// for
																// slider/angle
				geo.setAllVisualPropertiesExceptEuclidianVisible(defaultGeo,
						isReset);
			} else {
				geo.setAllVisualProperties(defaultGeo, isReset);
			}
			if (geo instanceof GeoFunction)
				geo.setAlphaValue(defaultGeo.getAlphaValue());

			if (!isReset) {
				// set to highest used layer
				setMaxLayerUsed(geo, app);
			}
			
			defaultLabelMode = defaultGeo.getLabelMode() == GeoElement.LABEL_DEFAULT;
		}

		if (defaultLabelMode){
			// label visibility
			int labelingStyle = app == null ? LABEL_VISIBLE_USE_DEFAULTS : app
					.getCurrentLabelingStyle();

			// automatic labelling:
			// if algebra window open -> all labels
			// else -> no labels

			switch (labelingStyle) {
			case LABEL_VISIBLE_ALWAYS_ON:
				geo.setLabelVisible(true);
				break;

			case LABEL_VISIBLE_ALWAYS_OFF:
				// we want sliders and angles to be labeled always
				geo.setLabelVisible(geo.isGeoNumeric());
				break;

			case LABEL_VISIBLE_POINTS_ONLY:
				// we want sliders and angles to be labeled always
				geo.setLabelVisible(geo.isGeoPoint() || geo.isGeoNumeric());
				break;

			default:
			case LABEL_VISIBLE_USE_DEFAULTS:
				// don't change anything
				break;
			}
		}

		if (blackWhiteMode) {
			// use black color and no filling
			geo.setObjColor(GColor.BLACK);
			geo.setAlphaValue(0f);
		}

		/*
		 * void initSetLabelVisible() { labelVisible = ! isPath() ||
		 * app.showAlgebraView(); }
		 */
	}

	/**
	 * @param flag
	 *            true for black-white
	 */
	public void setBlackWhiteMode(boolean flag) {
		blackWhiteMode = flag;
	}

	/**
	 * @return whether blackwhite mode is active
	 */
	public boolean getBlackWhiteMode() {
		return blackWhiteMode;
	}

	/**
	 * Reset construction defaults
	 */
	public void resetDefaults() {
		lineThickness = EuclidianStyleConstants.DEFAULT_LINE_THICKNESS;
		pointSize = EuclidianStyleConstants.DEFAULT_POINT_SIZE;
		dependentPointSize = EuclidianStyleConstants.DEFAULT_POINT_SIZE_DEPENDENT;
		angleSize = EuclidianStyleConstants.DEFAULT_ANGLE_SIZE;
		filling = DEFAULT_POLYGON_ALPHA;

		setDefaultLineThickness(lineThickness);
		setDefaultPointSize(pointSize, dependentPointSize);
		setDefaultAngleSize(angleSize);
		setDefaultFilling(filling);
	}

	/**
	 * 
	 * @return current default line thickness
	 */
	public int getDefaultLineThickness() {
		return lineThickness;
	}

	/**
	 * 
	 * @return current default point size
	 */
	public int getDefaultPointSize() {
		return pointSize;
	}

	/**
	 * 
	 * @return current default point size
	 */
	public int getDefaultDependentPointSize() {
		return dependentPointSize;
	}

	/**
	 * 
	 * @return current default angle size
	 */
	public int getDefaultAngleSize() {
		return angleSize;
	}

	/**
	 * @param angleSize0
	 *            new default angle size
	 */
	public void setDefaultAngleSize(int angleSize0) {

		this.angleSize = Math.max(angleSize0, 1);

		Iterator<GeoElement> it = defaultGeoElements.values().iterator();
		while (it.hasNext()) {
			GeoElement geo = it.next();

			switch (geo.getGeoClassType()) {

			case ANGLE:
				((GeoAngle) geo).setArcSize(this.angleSize);
				break;
			}
		}
	}

	/**
	 * @param pointSizeDraggable
	 *            new default point size for free / semi-free points
	 * @param pointSizeDependent
	 *            new default point size for dependent points
	 */
	public void setDefaultPointSize(int pointSizeDraggable,
			int pointSizeDependent) {

		this.pointSize = Math.max(pointSizeDraggable, 1);
		this.dependentPointSize = Math.max(pointSizeDependent, 1);

		Iterator<GeoElement> it = defaultGeoElements.values().iterator();
		while (it.hasNext()) {
			GeoElement geo = it.next();

			switch (geo.getGeoClassType()) {
			case POINT:

				((GeoPoint) geo).setPointSize(geo.isMoveable() ? this.pointSize
						: this.dependentPointSize);

				break;

			case LIST:
				((GeoList) geo).setPointSize(this.pointSize);
				break;
			}
		}
	}

	/**
	 * @param lineThickness0
	 *            new default thickness
	 */
	public void setDefaultLineThickness(int lineThickness0) {

		this.lineThickness = Math.max(lineThickness0, 1);

		Iterator<GeoElement> it = defaultGeoElements.values().iterator();
		while (it.hasNext()) {
			GeoElement geo = it.next();

			// set line thickness
			if (!geo.isGeoText() && !geo.isGeoImage()) // affects bounding box
				geo.setLineThickness(this.lineThickness);

			switch (geo.getGeoClassType()) {

			case LIST:
				((GeoList) geo).setLineThickness(this.lineThickness);
				break;
			}
		}
	}

	/**
	 * @param filling0
	 *            new default filling
	 */
	public void setDefaultFilling(float filling0) {

		this.filling = filling0;

		Iterator<GeoElement> it = defaultGeoElements.values().iterator();
		while (it.hasNext()) {
			GeoElement geo = it.next();

			geo.setAlphaValue(filling0);
		}
	}
	
	/**
	 * reset label mode to default for all default geos
	 * (and label visibility to true)
	 */
	public void resetLabelModeDefaultGeos(){
		for (GeoElement geo : defaultGeoElements.values()){
			geo.labelMode = GeoElement.LABEL_DEFAULT;
			geo.setLabelVisible(true);
		}
	}

	/**
	 * save construction defaults
	 * 
	 * @param sb2d
	 *            string for 2d geos
	 * @param sb3d
	 *            string for 3d geos
	 */
	public void getDefaultsXML(StringBuilder sb2d, StringBuilder sb3d) {

		MyXMLio.addXMLHeader(sb2d);
		MyXMLio.addGeoGebraHeader(sb2d, true, null);
		sb2d.append("<defaults>\n");

		if (sb3d != null) {
			MyXMLio.addXMLHeader(sb3d);
			MyXMLio.addGeoGebraHeader(sb3d, true, null);
			sb3d.append("<defaults>\n");
		}

		for (GeoElement geo : defaultGeoElements.values()){
			if (geo.isGeoElement3D()) {
				if (sb3d != null) {
					geo.getXML(false, sb3d);
				}
			} else {
				geo.getXML(false, sb2d);
			}
		}
		sb2d.append("</defaults>\n</geogebra>");

		if (sb3d != null) {
			sb3d.append("</defaults>\n</geogebra>");
		}
		
	}
	
	public void setConstructionDefaults(ConstructionDefaults otherDefaults){
		for (GeoElement geo : defaultGeoElements.values()){
			GeoElement otherGeo = otherDefaults.getDefaultGeo(geo.getDefaultGeoType());
			if (otherGeo != null){
				geo.setVisualStyle(otherGeo);
			}
		}
	}

	private int colorIndex = 0;

	public GColor getNextColor() {
		GColor color = colorSequence[colorIndex];
		if (!cons.getKernel().isSilentMode()) {
		colorIndex = (colorIndex + 1) % colorSequence.length;
		}
		return color;
	}

}