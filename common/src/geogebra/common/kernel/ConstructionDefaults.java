/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package geogebra.common.kernel;

import geogebra.common.GeoGebraConstants;
import geogebra.common.euclidian.EuclidianStyleConstants;
import geogebra.common.euclidian.EuclidianViewInterface2D;
import geogebra.common.kernel.geos.GeoAngle;
import geogebra.common.kernel.geos.GeoBoolean;
import geogebra.common.kernel.geos.GeoConic;
import geogebra.common.kernel.geos.GeoConicPart;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoElementInterface;
import geogebra.common.kernel.geos.GeoFunction;
import geogebra.common.kernel.geos.GeoFunctionNVar;
import geogebra.common.kernel.geos.GeoImage;
import geogebra.common.kernel.geos.GeoLine;
import geogebra.common.kernel.geos.GeoList;
import geogebra.common.kernel.geos.GeoLocus;
import geogebra.common.kernel.geos.GeoNumeric;
import geogebra.common.kernel.geos.GeoPoint2;
import geogebra.common.kernel.geos.GeoPolygon;
import geogebra.common.kernel.geos.GeoSegment;
import geogebra.common.kernel.geos.GeoText;
import geogebra.common.kernel.geos.GeoVector;
import geogebra.common.main.AbstractApplication;
import geogebra.common.main.GeoGebraColorConstants;

import geogebra.common.awt.Color;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Manages default settings for GeoElement objects in a construction.
 * @author Markus Hohenwarter
 */
public class ConstructionDefaults {
	public static final float DEFAULT_POLYGON_ALPHA = 0.1f;
	
	// DEFAULT GeoElement types
	public static final int DEFAULT_NONE = -1;
	public static final int DEFAULT_POINT_FREE = 10;
	public static final int DEFAULT_POINT_DEPENDENT = 11;
	public static final int DEFAULT_POINT_ON_PATH = 12;
	public static final int DEFAULT_POINT_IN_REGION = 13;
	public static final int DEFAULT_POINT_COMPLEX =  14;
	
	public static final int DEFAULT_LINE = 20;			
	public static final int DEFAULT_SEGMENT = 21;			
	public static final int DEFAULT_INEQUALITY = 23; 
	public static final int DEFAULT_INEQUALITY_1VAR = 24;
	public static final int DEFAULT_VECTOR = 30;	
	public static final int DEFAULT_CONIC = 40;
	public static final int DEFAULT_CONIC_SECTOR = 41;
		
	public static final int DEFAULT_NUMBER = 50;	
	public static final int DEFAULT_ANGLE = 52;			
	
	public static final int DEFAULT_FUNCTION = 60;		
	public static final int DEFAULT_POLYGON = 70;
	public static final int DEFAULT_LOCUS = 80;
	
	public static final int DEFAULT_TEXT = 100;
	public static final int DEFAULT_IMAGE = 110;
	public static final int DEFAULT_BOOLEAN = 120;
	
	public static final int DEFAULT_LIST = 130;	
	// DEFAULT COLORs
	// points
	public static final Color colPoint = Color.blue;
	public static final Color colDepPoint = Color.darkGray;
	public static final Color colPathPoint = GeoGebraColorConstants.LIGHTBLUE; //new Color(125, 125, 255);
	public static final Color colRegionPoint = colPathPoint;
	public static final Color colComplexPoint = colPoint;
	
	// lines
	private static final Color colLine = Color.black;
	private static final Color colInequality= Color.blue;

	// conics
	private static final Color colConic = Color.black;
	public static final float DEFAULT_CONIC_ALPHA = 0f;

	// polygons
	protected static final Color colPolygon = GeoGebraColorConstants.BROWN; //new Color(153, 51, 0);	
	
	public static final float DEFAULT_INEQUALITY_ALPHA = 0.25f;

	// angles
	private static final Color colAngle = GeoGebraColorConstants.DARKGREEN; //new Color(0, 100, 0);
	public static final float DEFAULT_ANGLE_ALPHA = 0.1f;
	
	// numbers eg integrals, barcharts
	public static final float DEFAULT_NUMBER_ALPHA = 0.1f;

	// locus lines	
	private static final Color colLocus = Color.black;
	
	// functions
	private static final Color colFunction = Color.black;
	
	// lists
	private static final Color colList = GeoGebraColorConstants.DARKGREEN; //new Color(0, 110, 0);

		
	/** preview color */
	public static final Color colPreview = Color.darkGray;
	/** preview fill color */
	public static final Color colPreviewFill = geogebra.common.factories.AwtFactory.prototype.newColor(
			colPolygon.getRed(), 
			colPolygon.getGreen(), 
			colPolygon.getBlue(), 
			(int) (DEFAULT_POLYGON_ALPHA * 255));	
	
	// label visibility
	public static final int LABEL_VISIBLE_AUTOMATIC = 0;	
	public static final int LABEL_VISIBLE_ALWAYS_ON = 1;
	public static final int LABEL_VISIBLE_ALWAYS_OFF = 2;
	public static final int LABEL_VISIBLE_POINTS_ONLY = 3;
	public static final int LABEL_VISIBLE_USE_DEFAULTS = 4;
	
		
	/** construction */
	protected Construction cons;
	
	/** defaultGeoElement list */
	protected HashMap<Integer,GeoElement> defaultGeoElements;		
	
	private int lineThickness = EuclidianStyleConstants.DEFAULT_LINE_THICKNESS;
	private int pointSize = EuclidianStyleConstants.DEFAULT_POINT_SIZE;
	private int angleSize = EuclidianStyleConstants.DEFAULT_ANGLE_SIZE;
	private float filling = DEFAULT_POLYGON_ALPHA;
	
	private boolean blackWhiteMode = false;
	
	/**
	 * Creates a new ConstructionDefaults object to manage the
	 * default objects of this construction.
	 * @param cons2
	 */
	public ConstructionDefaults(Construction cons2) {
		this.cons = (Construction)cons2;
		createDefaultGeoElements();		
	}

	
	/**
	 * Returns a set of all default GeoElements used by this construction.
	 * @return set of (integer,geo) pairs
	 */
	public Set<Map.Entry<Integer,GeoElement>> getDefaultGeos() {
		return defaultGeoElements.entrySet();
	}
	
	protected String strFree = " (free)";
	protected String strDependent = " (dependent)";
	protected String strIntersection = " (intersection)";
	
	/**
	 * Fills the list of default geos
	 */
	public void createDefaultGeoElements() {
		if (defaultGeoElements == null)
			defaultGeoElements = new HashMap<Integer,GeoElement>();		
				
						
		// free point
		GeoPoint2 freePoint = new GeoPoint2(cons);	
//		freePoint.setLocalVariableLabel(app.getPlain("Point") + strFree);
		freePoint.setPointSize(EuclidianStyleConstants.DEFAULT_POINT_SIZE);
		freePoint.setPointStyle(EuclidianStyleConstants.POINT_STYLE_DOT);
		freePoint.setLocalVariableLabel("Point" + strFree);
		freePoint.setObjColor(colPoint);
		freePoint.setPointSize(pointSize);
		freePoint.setDefaultGeoType(DEFAULT_POINT_FREE);
		defaultGeoElements.put(DEFAULT_POINT_FREE, freePoint);
		
		// dependent point
		GeoPoint2 depPoint = new GeoPoint2(cons);	
//		depPoint.setLocalVariableLabel(app.getPlain("Point") + strDependent);
		depPoint.setPointSize(EuclidianStyleConstants.DEFAULT_POINT_SIZE);
		depPoint.setPointStyle(EuclidianStyleConstants.POINT_STYLE_DOT);
		depPoint.setLocalVariableLabel("Point" + strDependent);
		depPoint.setObjColor(colDepPoint);
		depPoint.setPointSize(pointSize);
		depPoint.setDefaultGeoType(DEFAULT_POINT_DEPENDENT);
		defaultGeoElements.put(DEFAULT_POINT_DEPENDENT, depPoint);
		
		// point on path
		GeoPoint2 pathPoint = new GeoPoint2(cons);	
//		pathPoint.setLocalVariableLabel(app.getPlain("PointOn"));
		pathPoint.setPointSize(EuclidianStyleConstants.DEFAULT_POINT_SIZE);
		pathPoint.setPointStyle(EuclidianStyleConstants.POINT_STYLE_DOT);
		pathPoint.setLocalVariableLabel("PointOn");
		pathPoint.setObjColor(colPathPoint);
		pathPoint.setPointSize(pointSize);
		pathPoint.setDefaultGeoType(DEFAULT_POINT_ON_PATH);
		defaultGeoElements.put(DEFAULT_POINT_ON_PATH, pathPoint);
		
		// point in region
		GeoPoint2 regionPoint = new GeoPoint2(cons);	
//		regionPoint.setLocalVariableLabel(app.getPlain("PointOn"));
		regionPoint.setPointSize(EuclidianStyleConstants.DEFAULT_POINT_SIZE);
		regionPoint.setPointStyle(EuclidianStyleConstants.POINT_STYLE_DOT);
		regionPoint.setLocalVariableLabel("PointInRegion");
		regionPoint.setObjColor(colRegionPoint);
		regionPoint.setDefaultGeoType(DEFAULT_POINT_IN_REGION);
		defaultGeoElements.put(DEFAULT_POINT_IN_REGION, regionPoint);
		
		// complex number (handled like a point)
		GeoPoint2 complexPoint = new GeoPoint2(cons);
		complexPoint.setPointSize(EuclidianStyleConstants.DEFAULT_POINT_SIZE);
		complexPoint.setPointStyle(EuclidianStyleConstants.POINT_STYLE_DOT);
		complexPoint.setLocalVariableLabel("PointOn");
		complexPoint.setObjColor(colComplexPoint);
		complexPoint.setPointSize(pointSize);
		complexPoint.setDefaultGeoType(DEFAULT_POINT_COMPLEX);
		defaultGeoElements.put(DEFAULT_POINT_COMPLEX, complexPoint);
		
		// line
		GeoLine line = new GeoLine(cons);	
//		line.setLocalVariableLabel(app.getPlain("Line"));
		line.setLocalVariableLabel("Line");
		line.setObjColor(colLine);
		line.setDefaultGeoType(DEFAULT_LINE);
		defaultGeoElements.put(DEFAULT_LINE, line);
		
		// segment
		GeoSegment seg = new GeoSegment(cons);	
		seg.setLocalVariableLabel("Segment");
		seg.setObjColor(colLine);
		seg.setDefaultGeoType(DEFAULT_SEGMENT);
		defaultGeoElements.put(DEFAULT_SEGMENT, seg);
		
		GeoFunctionNVar inequality = new GeoFunctionNVar(cons);	
		//inequality.setLocalVariableLabel("Inequality");
		inequality.setObjColor(colInequality);
		inequality.setAlphaValue(DEFAULT_INEQUALITY_ALPHA);
		inequality.setDefaultGeoType(DEFAULT_INEQUALITY);
		defaultGeoElements.put(DEFAULT_INEQUALITY, inequality);
		
		GeoFunction inequality1var = new GeoFunction(cons);	
		//inequality.setLocalVariableLabel("Inequality");
		inequality1var.setObjColor(colInequality);
		inequality1var.setAlphaValue(DEFAULT_INEQUALITY_ALPHA);
		inequality1var.setDefaultGeoType(DEFAULT_INEQUALITY_1VAR);
		defaultGeoElements.put(DEFAULT_INEQUALITY_1VAR, inequality1var); 
		
		
		
		
		// vector
		GeoVector vector = new GeoVector(cons);
		vector.setLocalVariableLabel("Vector");
		vector.setObjColor(colLine);
		vector.setDefaultGeoType(DEFAULT_VECTOR);
		defaultGeoElements.put(DEFAULT_VECTOR, vector);
		
		// polygon
		GeoPolygon polygon = new GeoPolygon(cons, null);	
//		polygon.setLocalVariableLabel(app.getPlain("Polygon"));
		polygon.setLocalVariableLabel("Polygon");
		polygon.setObjColor(colPolygon);
		polygon.setAlphaValue(DEFAULT_POLYGON_ALPHA);
		polygon.setDefaultGeoType(DEFAULT_POLYGON);
		defaultGeoElements.put(DEFAULT_POLYGON, polygon);
										
		// conic
		GeoConic conic = new GeoConic(cons);	
//		conic.setLocalVariableLabel(app.getPlain("Conic"));
		conic.setLocalVariableLabel("Conic");
		conic.setObjColor(colConic);
		conic.setAlphaValue(DEFAULT_CONIC_ALPHA);
		conic.setDefaultGeoType(DEFAULT_CONIC);
		defaultGeoElements.put(DEFAULT_CONIC, conic);	
		
		// conic sector
		GeoConicPart conicSector = new GeoConicPart(cons, GeoConicPart.CONIC_PART_SECTOR);	
//		conicSector.setLocalVariableLabel(app.getPlain("Sector"));
		conicSector.setLocalVariableLabel("Sector");
		conicSector.setObjColor(colPolygon);
		conicSector.setAlphaValue(DEFAULT_POLYGON_ALPHA);
		conicSector.setDefaultGeoType(DEFAULT_CONIC_SECTOR);
		defaultGeoElements.put(DEFAULT_CONIC_SECTOR, conicSector);	
		
		
		// number
		GeoNumeric number = new GeoNumeric(cons);	
//		number.setLocalVariableLabel(app.getPlain("Numeric"));
		number.setLocalVariableLabel("Numeric");
		number.setSliderFixed(true);
		number.setLabelMode(GeoElement.LABEL_NAME_VALUE);	
		/*we have to set min/max/increment/speed here because 
		SetEuclideanVisible takes these from default geo*/
		number.setIntervalMax(GeoNumeric.DEFAULT_SLIDER_MAX);
		number.setIntervalMin(GeoNumeric.DEFAULT_SLIDER_MIN);
		number.setAnimationStep(GeoNumeric.DEFAULT_SLIDER_INCREMENT);
		number.setAnimationSpeed(GeoNumeric.DEFAULT_SLIDER_SPEED);
		number.setAlphaValue(DEFAULT_NUMBER_ALPHA);
		number.setDefaultGeoType(DEFAULT_NUMBER);
		defaultGeoElements.put(DEFAULT_NUMBER, number);
				
		// angle
		GeoAngle angle = new GeoAngle(cons);	
//		angle.setLocalVariableLabel(app.getPlain("Angle"));
		angle.setLocalVariableLabel("Angle");
		angle.setSliderFixed(true);
		angle.setObjColor(colAngle);		
		angle.setAlphaValue(DEFAULT_ANGLE_ALPHA);
		angle.setArcSize(angleSize);
		/*we have to set min/max/increment/speed here because 
		SetEuclideanVisible takes these from default geo*/
		angle.setIntervalMax(GeoAngle.DEFAULT_SLIDER_MAX);
		angle.setIntervalMin(GeoAngle.DEFAULT_SLIDER_MIN);
		angle.setAnimationStep(GeoAngle.DEFAULT_SLIDER_INCREMENT);
		angle.setAnimationSpeed(GeoNumeric.DEFAULT_SLIDER_SPEED);
		angle.setDefaultGeoType(DEFAULT_ANGLE);
		angle.setLineTypeHidden(EuclidianStyleConstants.LINE_TYPE_HIDDEN_AS_NOT_HIDDEN);
		defaultGeoElements.put(DEFAULT_ANGLE, angle);
		
		// function
		GeoFunction function = new GeoFunction(cons);	
//		function.setLocalVariableLabel(app.getPlain("Function"));
		function.setLocalVariableLabel("Function");
		function.setObjColor(colFunction);
		function.setDefaultGeoType(DEFAULT_FUNCTION);
		defaultGeoElements.put(DEFAULT_FUNCTION, function);
		
		// locus
		GeoLocus locus = new GeoLocus(cons);	
//		locus.setLocalVariableLabel(app.getPlain("Locus"));
		locus.setLocalVariableLabel("Locus");
		locus.setObjColor(colLocus);		
		locus.setLabelVisible(false);
		locus.setDefaultGeoType(DEFAULT_LOCUS);
		defaultGeoElements.put(DEFAULT_LOCUS, locus);					
		
		// text
		GeoText text = new GeoText(cons);		
//		text.setLocalVariableLabel(app.getPlain("Text"));
		text.setLocalVariableLabel("Text");
		text.setDefaultGeoType(DEFAULT_TEXT);
		defaultGeoElements.put(DEFAULT_TEXT, text);	
		
		// image
		GeoImage img = new GeoImage(cons);
//		img.setLocalVariableLabel(app.getPlain("Image"));
		img.setLocalVariableLabel("Image");
		img.setDefaultGeoType(DEFAULT_IMAGE);
		defaultGeoElements.put(DEFAULT_IMAGE, img);	
		
		// boolean
		GeoBoolean bool = new GeoBoolean(cons);		
//		bool.setLocalVariableLabel(app.getPlain("Boolean"));
		bool.setLocalVariableLabel("Boolean");
		bool.setDefaultGeoType(DEFAULT_BOOLEAN);
		defaultGeoElements.put(DEFAULT_BOOLEAN, bool);
		
		// list
		GeoList list = new GeoList(cons);	
//		list.setLocalVariableLabel(app.getPlain("List"));
		list.setShowAllProperties(true); // show all properties in the defaults dialog
		list.setLocalVariableLabel("List");
		list.setObjColor(colList);
		list.setAlphaValue(-1); // wait until we have an element in the list
								// then we will use the alphaValue of the first element in the list
								// see GeoList.setAlphaValue() and getAlphaValue()
		list.setDefaultGeoType(DEFAULT_LIST);
		defaultGeoElements.put(DEFAULT_LIST, list);
	}
	
	/**
	 * Returns the xml of the default geos - just used by
	 * GeoGebraPreferences 
	 */
	public String getCDXML() {	
		
		cons.getKernel().setTemporaryPrintFigures(15);
		
		StringBuilder sb = new StringBuilder();
		sb.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n");
		sb.append("<geogebra format=\"" + GeoGebraConstants.XML_FILE_FORMAT + "\">\n");
		sb.append("<construction>\n");
		for (GeoElement geo : defaultGeoElements.values()) {
			geo.getXML(sb);
		}
		sb.append("</construction>\n");
		sb.append("</geogebra>\n");
		
		cons.getKernel().restorePrintAccuracy();

		return sb.toString();
	}
	
	/**
	 * Returns a default GeoElement of this construction.
	 * @param type use DEFAULT_* constants (e.g. DEFAULT_POINT_FREE) 
	 * @return default geo for given type
	 */
	public GeoElement getDefaultGeo(int type) {
		return defaultGeoElements.get(type);		
	}
	
	/**
	 * Adds a key/value pair to defaultGeoElements.
	 * (used by Euclidian.EuclidianStyleBar to restore a default geo to previous state) 
	 * @param defaultType 
	 * @param geo 
	 */
	public void addDefaultGeo(Integer defaultType, GeoElement geo) {
		defaultGeoElements.put(defaultType, geo);		
	}
	
	
	/**
	 * return the default type of the GeoElement
	 * @param geo a GeoElement
	 * @return the default type
	 */
	public int getDefaultType(GeoElement geo){

		// all object types that are not specifically supported
		// should get the default values of a line
		int type = DEFAULT_LINE;
		
		switch (geo.getGeoClassType()) {
		case POINT:
			GeoPoint2 p = (GeoPoint2) geo;
			
			if(p.getMode() == AbstractKernel.COORD_COMPLEX) {
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
			GeoConicPart conicPart = (GeoConicPart) geo;
			if (conicPart.getConicPartType() == GeoConicPart.CONIC_PART_SECTOR) {
				type = DEFAULT_CONIC_SECTOR;
			} else {
				type = DEFAULT_CONIC;
			}
			break;

		case FUNCTION_NVAR:
				type = DEFAULT_INEQUALITY;				
			break;
		case FUNCTION:
			if(((GeoFunction)geo).isBooleanFunction())
				type = DEFAULT_INEQUALITY_1VAR; 
			else
				type = DEFAULT_FUNCTION;
			break;
		case INTERVAL:
		case FUNCTIONCONDITIONAL:
			type = DEFAULT_FUNCTION;
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

		case TEXT:
			type = DEFAULT_TEXT;
			break;

		case VECTOR:
			type = DEFAULT_VECTOR;
			break;	

		case SEGMENT:
			type = DEFAULT_SEGMENT;
			break;	


		}		
		
		return type;
	}	
	
	protected void setMaxLayerUsed(GeoElement geo, AbstractApplication app){
		if (app != null) {
			//TODO: if get EuclidianView returns 3D, this will fail
			EuclidianViewInterface2D ev = (EuclidianViewInterface2D)app.getEuclidianView();
			if (ev != null)
				geo.setLayer(ev.getMaxLayerUsed());
		}
	}
	
	/**
	 * Sets default color for given geo. 
	 * Note: this is mostly kept for downward compatibility.
	 * 
	 * @param geo The element which needs new default visual styles
	 * @param isReset If the visual styles should be reset
	 */
	final public void setDefaultVisualStyles(GeoElementInterface geoI, boolean isReset) {
		// all object types that are not specifically supported
		// should get the default values of a line
		//int type = DEFAULT_LINE;
		GeoElement geo = (GeoElement)geoI;
		int type = getDefaultType(geo);
			
		
		// default
		GeoElement defaultGeo = getDefaultGeo(type);
		AbstractApplication app = cons.getApplication();
		
		if (defaultGeo != null) {
			geo.setAllVisualProperties(defaultGeo, isReset);
			if(geo instanceof GeoFunction)
				geo.setAlphaValue(defaultGeo.getAlphaValue());
			
			if(!isReset) {
				// set to highest used layer
				setMaxLayerUsed(geo, app);
			}
		}

        // label visibility
		int labelingStyle = app == null ? LABEL_VISIBLE_USE_DEFAULTS : 
										app.getCurrentLabelingStyle();
		
		// automatic labelling: 
		// if algebra window open -> all labels
		// else -> no labels
		
		
		switch (labelingStyle) {									
			case LABEL_VISIBLE_ALWAYS_ON:
				geo.setLabelVisible(true);
				break;
			
			case LABEL_VISIBLE_ALWAYS_OFF:
				geo.setLabelVisible(false);
				break;
				
			case LABEL_VISIBLE_POINTS_ONLY:
				geo.setLabelVisible(geo.isGeoPoint());
				break;			
				
			default:
			case LABEL_VISIBLE_USE_DEFAULTS:
				// don't change anything
				break;														
		}			
		
		if (blackWhiteMode) {
			// use black color and no filling
			geo.setObjColor(Color.black);
			geo.setAlphaValue(0f);
		}
		
		/*
		void initSetLabelVisible() {
			labelVisible =  ! isPath() || app.showAlgebraView();
		}*/
	}
	
	public void setBlackWhiteMode(boolean flag) {
		blackWhiteMode = flag;
	}
	
	public boolean getBlackWhiteMode() {
		return blackWhiteMode;
	}
	
	public int getLineThicknessDefault() {
		return lineThickness;
	}
	
	public int getAngleSizeDefault() {
		return angleSize;
	}
	
	public int getPointSizeDefault() {
		return pointSize;
	}
	
	public float getAlphaDefault() {
		return filling;
	}
	
	public void resetDefaults() {
		lineThickness = EuclidianStyleConstants.DEFAULT_LINE_THICKNESS;
		pointSize = EuclidianStyleConstants.DEFAULT_POINT_SIZE;
		angleSize = EuclidianStyleConstants.DEFAULT_ANGLE_SIZE;
		filling = DEFAULT_POLYGON_ALPHA;
		
		setDefaultLineThickness(lineThickness);
		setDefaultPointSize(pointSize);
		setDefaultAngleSize(angleSize);
		setDefaultFilling(filling);	
	}
	
	public void setDefaultAngleSize(int angleSize) {
		
		this.angleSize = Math.max(angleSize, 1);
			
		Iterator<GeoElement> it = defaultGeoElements.values().iterator();
		while (it.hasNext()) {
			GeoElement geo = (GeoElement) it.next();	
			
					
			switch (geo.getGeoClassType()) {
			
				case ANGLE:					
					((GeoAngle) geo).setArcSize(angleSize);
					break;				
			}
		}		
	}
	
	public void setDefaultPointSize(int pointSize) {
			
		this.pointSize = Math.max(pointSize, 1);
		
		Iterator<GeoElement> it = defaultGeoElements.values().iterator();
		while (it.hasNext()) {
			GeoElement geo = (GeoElement) it.next();	
			
				
			switch (geo.getGeoClassType()) {
			case POINT:
				((GeoPoint2) geo).setPointSize(pointSize); 
				break;
				
			case LIST:
				((GeoList) geo).setPointSize(pointSize); 
				break;	
			}
		}		
	}
	
	public void setDefaultLineThickness(int lineThickness) {
		
		this.lineThickness = Math.max(lineThickness, 1);
			
		Iterator<GeoElement> it = defaultGeoElements.values().iterator();
		while (it.hasNext()) {
			GeoElement geo = (GeoElement) it.next();	
			
			// set line thickness
			if (!geo.isGeoText() && !geo.isGeoImage()) // affects bounding box
				geo.setLineThickness(lineThickness);
					
			switch (geo.getGeoClassType()) {
				
			case LIST:
				((GeoList) geo).setLineThickness(lineThickness); 
				break;
			}
		}		
	}
	public void setDefaultFilling(float filling) {
		
		this.filling = filling;
			
		Iterator<GeoElement> it = defaultGeoElements.values().iterator();
		while (it.hasNext()) {
			GeoElement geo = (GeoElement) it.next();	
			
			geo.setAlphaValue(filling);
		}		
	}

}