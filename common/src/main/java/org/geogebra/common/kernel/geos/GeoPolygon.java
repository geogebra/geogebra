/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package org.geogebra.common.kernel.geos;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.MatrixTransformable;
import org.geogebra.common.kernel.Path;
import org.geogebra.common.kernel.PathMover;
import org.geogebra.common.kernel.PathMoverGeneric;
import org.geogebra.common.kernel.PathParameter;
import org.geogebra.common.kernel.RegionParameters;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.Matrix.CoordSys;
import org.geogebra.common.kernel.Matrix.Coords;
import org.geogebra.common.kernel.algos.AlgoAnglePolygon;
import org.geogebra.common.kernel.algos.AlgoAnglePolygonND;
import org.geogebra.common.kernel.algos.AlgoJoinPointsSegment;
import org.geogebra.common.kernel.algos.AlgoJoinPointsSegmentInterface;
import org.geogebra.common.kernel.algos.AlgoPolygon;
import org.geogebra.common.kernel.algos.AlgoPolygonRegularND;
import org.geogebra.common.kernel.algos.AlgoTransformation;
import org.geogebra.common.kernel.algos.PolygonAlgo;
import org.geogebra.common.kernel.algos.SymbolicParametersBotanaAlgo;
import org.geogebra.common.kernel.arithmetic.ExpressionNodeConstants.StringType;
import org.geogebra.common.kernel.arithmetic.ExpressionNodeEvaluator;
import org.geogebra.common.kernel.arithmetic.MyDouble;
import org.geogebra.common.kernel.arithmetic.NumberValue;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.kernelND.GeoCoordSys2D;
import org.geogebra.common.kernel.kernelND.GeoLineND;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.kernel.kernelND.GeoSegmentND;
import org.geogebra.common.kernel.kernelND.HasSegments;
import org.geogebra.common.kernel.prover.NoSymbolicParametersException;
import org.geogebra.common.kernel.prover.polynomial.Polynomial;
import org.geogebra.common.kernel.prover.polynomial.Variable;
import org.geogebra.common.plugin.GeoClass;
import org.geogebra.common.util.MyMath;

/**
 * Polygon through given points
 * 
 * @author Markus Hohenwarter
 */
public class GeoPolygon extends GeoElement implements GeoNumberValue, Path,
GeoSurfaceFinite, Traceable, PointRotateable, MatrixTransformable,
Mirrorable, Translateable, Dilateable, GeoCoordSys2D,
GeoPoly, Transformable, SymbolicParametersBotanaAlgo, HasSegments, FromMeta{

	/** maximal number of vertices for polygon tool */
	public static final int POLYGON_MAX_POINTS = 1000;
	/** polygon vertices */
	protected GeoPointND[] points;
	/** polygon edges */
	protected GeoSegmentND[] segments;

	/** first point for region coord sys */
	protected GeoPoint p0;
	/** second point for region coord sys */
	protected GeoPoint p1;
	/** third point for region coord sys */
	protected GeoPoint p2;
	/** number of points in coord sys */
	protected int numCS = 0;

	/** directed area */
	protected double area;
	private boolean defined = false;
	/** @see #wasInitLabelsCalled()  */
	protected boolean initLabelsCalled = false;

	/** says if the polygon had created its segments itself (used for 3D) */
	private boolean createSegments = true;

	/**
	 * common constructor for 2D.
	 * 
	 * @param c
	 *            the construction
	 * @param points
	 *            vertices
	 */
	public GeoPolygon(Construction c, GeoPointND[] points) {
		this(c, points, null, true);
	}

	/**
	 * common constructor for 3D.
	 * 
	 * @param c
	 *            the construction
	 * @param points
	 *            vertices
	 * @param cs
	 *            for 3D stuff : 2D coord sys
	 * @param createSegments
	 *            says if the polygon has to creates its edges
	 */
	public GeoPolygon(Construction c, GeoPointND[] points, CoordSys cs,
			boolean createSegments) {
		this(c);
		// Application.printStacktrace("poly");
		this.createSegments = createSegments;
		setPoints(points, cs, createSegments);
		setLabelVisible(false);
	}
	
	protected boolean isIntersection;

	/**
	 * Creates new GeoPolygon
	 * 
	 * @param cons
	 *            construction
	 */
	public GeoPolygon(Construction cons) {
		this(cons, false);
	}

	/**
	 * Creates new GeoPolygon
	 * 
	 * @param cons
	 *            construction
	 */
	public GeoPolygon(Construction cons, boolean isIntersection) {
		super(cons);

		this.isIntersection = isIntersection;
		
		// moved from GeoElement's constructor
		// must be called from the subclass, see
		// http://benpryor.com/blog/2008/01/02/dont-call-subclass-methods-from-a-superclass-constructor/
		setConstructionDefaults(); // init visual settings

	}

	/**
	 * not used for 2D
	 * 
	 * @param cs
	 *            GeoCoordSys2D
	 */
	public void setCoordSys(CoordSys cs) {
		//nothing to do here
	}

	/**
	 * for 3D stuff (unused here)
	 * 
	 * @param p polygon
	 * 			
	 */
	public void setCoordSys(GeoPolygon poly) {
		//3D only
	}

	/**
	 * for 3D stuff (unused here)
	 * 
	 * @param p polygon
	 * 			
	 */
	public void setCoordSysAndPoints3D(GeoPolygon p) {
		//3D only
	}

	private boolean notFixedPointsLength = false;

	/**
	 * set that this polygon hasn't fixed points length (e.g. for regular polygons with slider).
	 * Used in getTypeString() to avoid bad type display in algebra view, properties view, etc.
	 * @param flag true if not fixed points length
	 */
	public void setNotFixedPointsLength(boolean flag){
		notFixedPointsLength = flag;
	}

	@Override
	public String getTypeString() {

		if (notFixedPointsLength || points == null)
			return "Polygon";

		switch (getPointsLength()) {
		case 3:
			return "Triangle";

		case 4:
			return "Quadrilateral";

		case 5:
			return "Pentagon";

		case 6:
			return "Hexagon";

		default:
			return "Polygon";
		}
	}

	@Override
	public GeoClass getGeoClassType() {
		return GeoClass.POLYGON;
	}

	/**
	 * set the vertices to points
	 * 
	 * @param points
	 *            the vertices
	 */
	final public void setPoints(GeoPointND[] points) {
		setPoints(points, null, true);
	}

	/**
	 * set the vertices to points (cs is only used for 3D stuff)
	 * 
	 * @param points
	 *            the vertices
	 * @param cs
	 *            used for 3D stuff
	 * @param createSegments
	 *            says if the polygon has to creates its edges
	 */
	public void setPoints(GeoPointND[] points, CoordSys cs,
			boolean createSegments) {
		this.points = points;
		setCoordSys(cs);

		if (createSegments)
			updateSegments();

		// if (points != null) {
		// Application.debug("*** " + this + " *****************");
		// Application.debug("POINTS: " + points.length);
		// for (int i=0; i < points.length; i++) {
		// Application.debug(" " + i + ": " + points[i]);
		// }
		// Application.debug("SEGMENTS: " + segments.length);
		// for (int i=0; i < segments.length; i++) {
		// Application.debug(" " + i + ": " + segments[i]);
		// }
		// Application.debug("********************");
		// }
	}

	// /////////////////////////////
	// ggb3D 2009-03-08 - start

	/**
	 * return number for points
	 * 
	 * @return number for points
	 */
	public int getPointsLength() {
		if (getPoints() == null) // TODO remove this (preview bug)
			return 0;
		return getPoints().length;
	}

	/**
	 * return the x-coordinate of the i-th vertex
	 * 
	 * @param i
	 *            number of vertex
	 * @return the x-coordinate
	 */
	public double getPointX(int i) {
		return getPoint(i).inhomX;
	}

	/**
	 * return the y-coordinate of the i-th vertex
	 * 
	 * @param i
	 *            number of vertex
	 * @return the y-coordinate
	 */
	public double getPointY(int i) {
		return getPoint(i).inhomY;
	}

	// ggb3D 2009-03-08 - end
	// /////////////////////////////

	/**
	 * Inits the labels of this polygon, its segments and its points. labels[0]
	 * for polygon itself, labels[1..n] for segments, labels[n+1..2n-2] for
	 * points (only used for regular polygon)
	 * 
	 * @param labels labels of points and segments
	 */
	public void initLabels(String[] labels) {
		if (cons.isSuppressLabelsActive())
			return;
		initLabelsCalled = true;
		// Application.debug("INIT LABELS");

		// label polygon
		if (labels == null || labels.length == 0) {
			// Application.debug("no labels given");

			setLabel(null);
			if (segments != null) {
				defaultSegmentLabels();
			}
			return;
		}

		// label polygon
		// first label for polygon itself
		setLabel(labels[0]);

		// label segments and points
		if (points != null && segments != null) {

			// additional labels for the polygon's segments
			// poly + segments + points - 2 for AlgoPolygonRegular
			if (labels.length == 1 + segments.length + getPointsLength() - 2) {
				// Application.debug("labels for segments and points");

				int i = 1;
				for (int k = 0; k < segments.length; k++, i++) {
					segments[k].setLabel(labels[i]);
				}
				for (int k = 2; k < getPointsLength(); k++, i++) {
					points[k].setLabel(labels[i]);
				}
			}

			// additional labels for the polygon's segments
			// poly + segments for AlgoPolygon
			else if (labels.length == 1 + segments.length) {
				// Application.debug("labels for segments");

				int i = 1;
				for (int k = 0; k < segments.length; k++, i++) {
					segments[k].setLabel(labels[i]);
				}
			}

			else {
				// Application.debug("label for polygon (autoset segment labels)");
				defaultSegmentLabels();
			}
		}
	}

	/**
	 * Returns whether the method initLabels() was called for this polygon. This
	 * is important to know whether the segments have gotten labels.
	 * 
	 * @return true iff the method initLabels() was called for this polygon.
	 */
	final public boolean wasInitLabelsCalled() {
		return initLabelsCalled;
	}

	private void defaultSegmentLabels() {
		// no labels for segments specified
		// set labels of segments according to point names
		if (getPointsLength() == 3) {

			// make sure segment opposite C is called c not a_1
			if (getParentAlgorithm() instanceof AlgoPolygonRegularND)
				points[2].setLabel(null);

			setLabel(segments[0], points[2]);
			setLabel(segments[1], points[0]);
			setLabel(segments[2], points[1]);
		} else {
			for (int i = 0; i < getPointsLength(); i++) {
				setLabel(segments[i], points[i]);
			}
		}
	}

	/**
	 * Sets label of segment to lower case label of point. If the point has no
	 * label, a default label is used for the segment. If the lower case label
	 * of the point is already used, an indexed label is created.
	 */
	private static void setLabel(GeoSegmentND s, GeoPointND p) {
		if (!p.isLabelSet()
				|| p.getLabel(StringTemplate.defaultTemplate) == null) {
			s.setLabel(null);
		} else {
			// use lower case of point label as segment label
			String lowerCaseLabel = ((GeoElement) p).getFreeLabel(p.getLabel(
					StringTemplate.get(StringType.GEOGEBRA)).toLowerCase());
			s.setLabel(lowerCaseLabel);
		}
	}
	
	private ArrayList<GeoSegmentND> segmentsArray;

	/**
	 * Updates all segments of this polygon for its point array. Note that the
	 * point array may be changed: this method makes sure that segments are
	 * reused if possible.
	 */
	protected void updateSegments() {
		if (points == null)
			return;
		
		// make sure the polygon is defined to get correct euclidian visibility
		setDefined();
		
		boolean euclidianVisible;
		
		// check array zand euclidian visibility
		if (segmentsArray == null){
			segmentsArray = new ArrayList<GeoSegmentND>();
		}
		if(segmentsArray.size()<1){
			euclidianVisible = isEuclidianVisible();
		}else{
			euclidianVisible = segmentsArray.get(0).isEuclidianVisible();
		}
		
		segments = new GeoSegmentND[getPointsLength()];
		
		// set first values
		for (int i = 0; i < segmentsArray.size() && i < points.length; i++) {
			GeoPointND startPoint = points[i];
			GeoPointND endPoint = points[(i + 1)  % getPointsLength()];	
			GeoSegmentND segment = segmentsArray.get(i);
			AlgoJoinPointsSegmentInterface algo = (AlgoJoinPointsSegmentInterface) segment.getParentAlgorithm();
			algo.modifyInputPoints(startPoint, endPoint);
			algo.compute();
			segments[i] = segment;
			segment.setEuclidianVisible(euclidianVisible);
		}


		// adjust size
		for (int i = segmentsArray.size(); i < points.length; i++) {
			GeoPointND startPoint = points[i];
			GeoPointND endPoint = points[(i + 1) % getPointsLength()];
			GeoSegmentND segment = createSegment(startPoint, endPoint, euclidianVisible);
			segment.getParentAlgorithm().setProtectedInput(true); // avoid remove by other algos
			segmentsArray.add(segment);
			segments[i] = segment;
		}



		// set last segments undefined
		for (int i = points.length; i < segmentsArray.size(); i++) {
			segmentsArray.get(i).setUndefined();
		}
		
		
		
	}


	/**
	 * return a segment joining startPoint and endPoint
	 * 
	 * @param startPoint
	 *            the start point
	 * @param endPoint
	 *            the end point
	 * @param euclidianVisible true to make the segment visible
	 * @return the segment
	 */
	public GeoSegmentND createSegment(GeoPointND startPoint,
			GeoPointND endPoint, boolean euclidianVisible) {

		AlgoJoinPointsSegment algoSegment = new AlgoJoinPointsSegment(cons,
				(GeoPoint) startPoint, (GeoPoint) endPoint, this);
		cons.removeFromConstructionList(algoSegment);

		return createSegment(algoSegment.getSegment(), euclidianVisible);
	}

	/**
	 * ends the creation of the segment
	 * 
	 * @param segment segment
	 * @param euclidianVisible true to make the segment visible
	 * @return the segment modified
	 */
	protected GeoSegmentND createSegment(GeoSegmentND segment,
			boolean euclidianVisible) {
		// refresh color to ensure segments have same color as polygon:
		segment.setObjColor(getObjectColor());
		segment.setLineThickness(getLineThickness());
		segment.setLineType(getLineType());
		segment.setEuclidianVisible(euclidianVisible);

		if (condShowObject != null) {
			try {
				((GeoElement) segment)
				.setShowObjectCondition(getShowObjectCondition());
			} catch (Exception e) {
				//circular definition
			}
		}


		return segment;
	}

	/**
	 * The copy of a polygon is a number (!) with its value set to the polygons
	 * current area
	 */
	@Override
	public GeoElement copy() {
		return new GeoNumeric(cons, getArea());
	}





	@Override
	public GeoElement copyInternal(Construction cons1) {

		GeoPolygon ret = newGeoPolygon(cons1);
		copyInternal(cons1, ret);
		return ret;
	}

	/**
	 * @param cons1 consctruction
	 * @param ret poly where to copy
	 */
	public void copyInternal(Construction cons1, GeoPolygon ret){
		ret.setPoints2D(GeoElement.copyPoints(cons1, getPoints()));
		ret.set(this);
	}

	/**
	 * Factory method for polygons, overridden in 3D
	 * @param cons1 construction
	 * @return new polygon
	 */
	protected GeoPolygon newGeoPolygon(Construction cons1) {
		return new GeoPolygon(cons, null);
	}


	private ArrayList<GeoPoint> pointsArray;

	@Override
	public void set(GeoElement geo) {
		
		GeoPolygon poly = (GeoPolygon) geo;
		area = poly.area;
		
		setReverseNormalForDrawing(poly.getReverseNormalForDrawing());
		
		if (!notFixedPointsLength){ // maybe already set by AlgoListElement
			notFixedPointsLength = poly.notFixedPointsLength;
		}

		// fix for Sequence[Polygon[Element[liste1, i], Element[liste1, i + 1], j], i, 0, 300] 
		if (poly.getPoints() == null) { 
			setUndefined();
			return;
		}

		

		int polyLength = poly.getPoints().length;

		setPointsLength(polyLength, null);

		// set values
		for (int i = 0; i < getPoints().length; i++) {
			getPoint(i).set(poly.getPoint(i).toGeoElement());
		}
		

		setCoordSysAndPoints3D(poly);
		updateSegments();
		defined = poly.defined;

		if (poly.hasChangeableCoordParentNumbers())
			setChangeableCoordParent(poly.changeableCoordParent.getNumber(),poly.changeableCoordParent.getDirector());
		updateRegionCS();
	}
	
	/**
	 * set points matching geos list, and segments
	 * @param geos input points
	 */
	public void setPointsAndSegments(GeoPointND[] geos){
		setPointsLength(geos.length, geos);

		// set values
		for (int i = 0; i < getPoints().length; i++) {
			getPoint(i).set(geos[i]);
		}
		
		updateSegments();

	}
	
	/**
	 * set points and segments length to arbitrary value (create new points and segments)
	 * @param polyLength length
	 */
	public void setPointsAndSegmentsLength(int polyLength){
		setPointsLength(polyLength, null);
		updateSegments();
	}


	/**
	 * set points length to arbitrary value (create new points)
	 * @param polyLength length
	 */
	protected void setPointsLength(int polyLength, GeoPointND[] template){
		
		if (pointsArray == null){
			pointsArray = new ArrayList<GeoPoint>();
		}

		// augment array size if array < polyLength
		for (int i = pointsArray.size() ; i < polyLength ; i++){
			if(template !=null && template.length > i && template[i] instanceof GeoPoint){
				pointsArray.add((GeoPoint)template[i]);
			}else{
				pointsArray.add(new GeoPoint(cons));
			}
		}

		// set last points undefined if array > polyLength
		for (int i = polyLength ; i < pointsArray.size() ; i++){
			pointsArray.get(i).setUndefined();
		}					

		// make sure both arrays have same size
		if (getPoints() == null || getPoints().length != polyLength){
			// copy usable array part
			GeoPoint[] tempPoints = new GeoPoint[polyLength];
			for (int i = 0; i < polyLength; i++) {
				tempPoints[i] = pointsArray.get(i);
			}
			setPoints2D(tempPoints);
		}
	}




	/**
	 * Returns the i-th point of this polygon. Note that this array may change
	 * dynamically.
	 * 
	 * @param i
	 *            number of point
	 * @return the i-th point
	 */
	public GeoPoint getPoint(int i) {
		return (GeoPoint) points[i];
	}

	/**
	 * Returns the points of this polygon as GeoPoints. Note that this array may
	 * change dynamically.
	 * 
	 * @return points of this polygon.
	 */
	public GeoPointND[] getPoints() {
		return points;
	}

	/**
	 * set the 2D points
	 * @param points 2D points
	 */
	public void setPoints2D(GeoPoint[] points){
		this.points = points;
	}
	

	/**
	 * Returns the points of this polygon as GeoPointNDs. Note that this array
	 * may change dynamically.
	 * 
	 * @return points of this polygon
	 */
	final public GeoPointND[] getPointsND() {

		return points;
	}

	/**
	 * Returns i-th vertex of this polygon
	 * 
	 * @param i index
	 * @return i-th pointt
	 */
	final public GeoPointND getPointND(int i) {
		return points[i];
	}

	/**
	 * Returns the segments of this polygon. Note that this array may change
	 * dynamically.
	 * 
	 * @return segments of this polygon.
	 */
	public GeoSegmentND[] getSegments() {
		return segments;
	}

	/**
	 * sets the segments (used by GeoPolyhedron)
	 * 
	 * @param segments
	 *            the segments
	 */
	public void setSegments(GeoSegmentND[] segments) {
		this.segments = segments;
	}

	@Override
	public boolean isFillable() {
		return true;
	}

	@Override
	public boolean isInverseFillable() {
		return isFillable();
	}

	// Michael Borcherds 2008-01-26 BEGIN
	/**
	 * Sets this polygon's area . This method should only be called by its
	 * parent algorithm of type AlgoPolygon
	 */
	final public void setArea(double area) {
		this.area = area;
		defined = !(Double.isNaN(area) || Double.isInfinite(area));
	}

	/**
	 * Returns undirected area
	 * 
	 * @return undirected area
	 */
	public double getArea() {
		if (isDefined()) {
			return Math.abs(area);
		}
		return Double.NaN;
	}

	@Override
	public double getMeasure() {
		return getArea();
	}

	public Path getBoundary() {
		boolean suppress = this.cons.isSuppressLabelsActive();
		this.getConstruction().getKernel().setSilentMode(true);

		GeoPointND[] pointsForPolyLine = new GeoPointND[getPointsLength() + 1];
		System.arraycopy(points, 0, pointsForPolyLine, 0, getPointsLength());
		pointsForPolyLine[getPointsLength()] = pointsForPolyLine[0];

		GeoPolyLine pl = new GeoPolyLine(this.getConstruction(),
				pointsForPolyLine);

		this.getConstruction().getKernel().setSilentMode(false);
		cons.setSuppressLabelCreation(suppress);
		return pl;
	}

	/**
	 * clockwise=-1 anticlockwise=+1 no area=0
	 * 
	 * @return orientation of area
	 */
	public double getDirection() {
		if (defined) {
			return MyMath.sgn(kernel, area);
		}
		return Double.NaN;
	}

	/**
	 * Returns directed area
	 * 
	 * @return directed area
	 */
	public double getAreaWithSign() {
		if (defined) {
			return area;
		}
		return Double.NaN;
	}

	// Michael Borcherds 2008-01-26 END

	/*
	 * overwrite methods
	 */
	@Override
	public boolean isDefined() {
		return defined;
	}

	/**
	 * Sets the polygon state to "defined"
	 */
	public void setDefined() {
		defined = true;
	}

	@Override
	public void setUndefined() {
		defined = false;
	}

	@Override
	public final boolean showInAlgebraView() {
		// return defined;
		return true;
	}
	
	@Override
	public boolean hasLineOpacity() {
		return true;
	}
	
	@Override
	public void setLineOpacity(int lineOpacity) {
		setLineOpacity(lineOpacity, true);
	}
	
	/**
	 * Sets the line opacity for this GeoElement. 
	 * @param lineOpacity  opacity value between 0 - 255
	 * @param updateSegments true to apply this setting to segments
	 */
	public void setLineOpacity(int lineOpacity, boolean updateSegments) {
		super.setLineOpacity(lineOpacity);
		if (updateSegments && segments != null) {
			for (int i = 0; i < segments.length; i++) {
				segments[i].setLineOpacity(lineOpacity);
				segments[i].updateVisualStyle();
			}
		}
	}

	/**
	 * Yields true if the area of this polygon is equal to the area of polygon
	 * p.
	 */
	// this method is the original isEqual, modified 2014-01
	public boolean hasSameArea(GeoElement geo) {
		// return false if it's a different type
		if (geo.isGeoPolygon()) {
			return Kernel.isEqual(getArea(), ((GeoPolygon) geo).getArea());
		}
		return false;
	}

	/**
	 * Yields true if the points of this polygon is equal to the points of polygon
	 * p.
	 */
	@Override
	public boolean isEqual(GeoElement geo) {
		// test 3D is geo is 3D
		if (geo.isGeoElement3D()) {
			return geo.isEqual(this);
		}
		// return false if it's a different type
		if (geo.isGeoPolygon()) {
			GeoPolygon g = (GeoPolygon) geo;

			//return false if the number of points is different
			int gLength = g.getPointsLength(); 
			if (gLength == this.getPointsLength()){

				//search for a first common point
				GeoPoint firstPoint = this.getPoint(0);
				boolean fPointFound = false;
				int iFirstPoint = 0;
				while ((!fPointFound)&&(iFirstPoint < gLength)){
					if (firstPoint.isEqual(g.getPoint(iFirstPoint))) {
						fPointFound = true;
					} else {
						iFirstPoint++;
					}
				}

				//next point
				if (fPointFound) {
					boolean sPointFound = false;
					int step = 1;
					if (this.getPoint(1).isEqual(g.getPoint((iFirstPoint+step)%gLength))){
						sPointFound = true;
					} else {
						step = -1;
						int j = iFirstPoint+step;
						if (j<0) j = gLength-1;
						if (this.getPoint(1).isEqual(g.getPoint(j))){
							sPointFound = true;
						}
					}

					//other points
					if (sPointFound){
						int i = 2;
						int j = iFirstPoint+step+step;
						if (j<0) j = j+ gLength;
						j = j%gLength;
						boolean pointOK = true;
						while ((pointOK)&&(i<gLength)){
							pointOK =  (this.getPoint(i).isEqual(g.getPoint(j)));
							if (pointOK) {
								j = j + step; 
								if (j<0) j = gLength-1;
								j = j%gLength;
								i++;
							}
						}
						return pointOK;
					}
				}
			}
		}
		return false;
	}

	/**
	 * @param polygon
	 *            input
	 * @return true - if input is congruent with this polygon false - otherwise
	 */
	public boolean isCongruent(GeoPolygon polygon) {
		// Polygons:
		// two polygon are congruent if the corresponding sides has same length
		// and corresponding angles has same size
		int nrSidesPoly1 = this.getSegments().length;
		int nrSidesPoly2 = polygon.getSegments().length;
		// two polygon can be congruent when their number of sides are equal
		// and have the same area
		if (nrSidesPoly1 == nrSidesPoly2 && (this).hasSameArea(polygon)) {
			GeoSegmentND[] segmentsPoly1 = this.getSegments();
			GeoSegmentND[] segmentsPoly2 = polygon.getSegments();
			AlgoAnglePolygonND algo1 = new AlgoAnglePolygon(cons, null, this);
			AlgoAnglePolygonND algo2 = new AlgoAnglePolygon(cons, null, polygon);
			GeoElement[] anglesPoly1 = algo1.getAngles();
			GeoElement[] anglesPoly2 = algo2.getAngles();
			return (checkSegmentsAreCongruent(segmentsPoly1, segmentsPoly2) && checkAnglesAreCongruent(
					anglesPoly1, anglesPoly2));
		}
		return false;
	}

	private boolean checkSegmentsAreCongruent(GeoSegmentND[] segmentsPoly1,
			GeoSegmentND[] segmentsPoly2) {
		Set<GeoSegmentND> setOfSegPoly2 = new HashSet<GeoSegmentND>();
		for (int i = 0; i < segmentsPoly2.length; i++) {
			setOfSegPoly2.add(segmentsPoly2[i]);
		}
		for (int i = 0; i < segmentsPoly1.length; i++) {
			for (GeoSegmentND geoSegmentND : setOfSegPoly2) {
				if (ExpressionNodeEvaluator.evalEquals(kernel,
						segmentsPoly1[i], geoSegmentND).getBoolean()) {
					setOfSegPoly2.remove(geoSegmentND);
					break;
				}
			}
		}
		if (setOfSegPoly2.isEmpty()) {
			return true;
		}
		return false;
	}

	private boolean checkAnglesAreCongruent(GeoElement[] anglesPoly1,
			GeoElement[] anglesPoly2) {
		Set<GeoElement> setOfAnglePoly2 = new HashSet<GeoElement>();
		for (int i = 0; i < anglesPoly2.length; i++) {
			setOfAnglePoly2.add(anglesPoly2[i]);
		}
		for (int i = 0; i < anglesPoly1.length; i++) {
			for (GeoElement geoElement : setOfAnglePoly2) {
				if (ExpressionNodeEvaluator.evalEquals(kernel, geoElement,
						anglesPoly1[i]).getBoolean()) {
					setOfAnglePoly2.remove(geoElement);
					break;
				}
			}
		}
		if (setOfAnglePoly2.isEmpty()) {
			return true;
		}
		return false;
	}

	@Override
	public void setEuclidianVisible(boolean visible) {

		setEuclidianVisible(visible, true);

	}

	/**
	 * Switches visibility of this polygon
	 * 
	 * @param visible
	 *            visibility flag
	 * @param updateSegments
	 *            if true, applies also to segments
	 */
	public void setEuclidianVisible(boolean visible, boolean updateSegments) {
		super.setEuclidianVisible(visible);
		if (updateSegments && segments != null) {
			for (int i = 0; i < segments.length; i++) {
				segments[i].setEuclidianVisible(visible);
				segments[i].updateVisualStyle();
			}
		}
	}

	@Override
	public void setObjColor(GColor color) {
		super.setObjColor(color);
		if (segments != null && createSegments) {
			for (int i = 0; i < segments.length; i++) {
				segments[i].setObjColor(color);
				segments[i].updateVisualStyle();
			}
		}
	}

	@Override
	public void setLineType(int type) {
		setLineType(type, true);
	}

	/**
	 * set the line type (and eventually the segments)
	 * 
	 * @param type line type
	 * @param updateSegments true to apply this setting to segments
	 */
	public void setLineType(int type, boolean updateSegments) {
		super.setLineType(type);
		if (updateSegments)
			if (segments != null) {
				for (int i = 0; i < segments.length; i++) {
					segments[i].setLineType(type);
					segments[i].updateVisualStyle();
				}
			}
	}

	@Override
	public void setLineTypeHidden(int type) {
		setLineTypeHidden(type, true);
	}

	/**
	 * set the hidden line type (and eventually the segments)
	 * 
	 * @param type line type for hidden lines
	 * @param updateSegments true to apply this setting to segments
	 */
	public void setLineTypeHidden(int type, boolean updateSegments) {
		super.setLineTypeHidden(type);
		if (updateSegments)
			if (segments != null) {
				for (int i = 0; i < segments.length; i++) {
					((GeoElement) segments[i]).setLineTypeHidden(type);
					segments[i].updateVisualStyle();
				}
			}
	}

	@Override
	public void setLineThickness(int th) {
		setLineThickness(th, true);
	}

	/**
	 * set the line thickness (and eventually the segments)
	 * 
	 * @param th new thickness
	 * @param updateSegments true to apply this setting to segments as well
	 */
	public void setLineThickness(int th, boolean updateSegments) {
		super.setLineThickness(th);

		if (updateSegments)
			if (segments != null) {
				for (int i = 0; i < segments.length; i++) {
					segments[i].setLineThickness(th);
					segments[i].updateVisualStyle();
				}
			}
	}
	
	@Override
	public void setLineThicknessOrVisibility(int th) {

		super.setLineThickness(th);

		if (segments != null) {
			for (int i = 0; i < segments.length; i++) {
				((GeoElement) segments[i]).setLineThicknessOrVisibility(th);
				segments[i].updateVisualStyle();
			}
		}
	}

	@Override
	final public String toString(StringTemplate tpl) {
		sbToString.setLength(0);
		sbToString.append(label);
		sbToString.append(" = ");
		sbToString.append(kernel.format(getArea(), tpl));
		return sbToString.toString();
	}

	@Override
	final public String toStringMinimal(StringTemplate tpl) {
		sbToString.setLength(0);
		sbToString.append(regrFormat(getArea()));
		return sbToString.toString();
	}

	private StringBuilder sbToString = new StringBuilder(50);

	@Override
	final public String toValueString(StringTemplate tpl) {
		return kernel.format(getArea(), tpl);
	}

	/**
	 * interface NumberValue
	 */
	public MyDouble getNumber() {
		return new MyDouble(kernel, getArea());
	}

	final public double getDouble() {
		return getArea();
	}

	@Override
	protected boolean showInEuclidianView() {
		return defined;
	}

	@Override
	public boolean isNumberValue() {
		return true;
	}

	@Override
	public boolean isGeoPolygon() {
		return true;
	}

	/*
	 * Path interface implementation
	 */

	@Override
	public boolean isPath() {
		return true;
	}

	public PathMover createPathMover() {
		return new PathMoverGeneric(this);
	}

	public double getMaxParameter() {
		return getPointsLength();
	}

	public double getMinParameter() {
		return 0;
	}

	public boolean isClosedPath() {
		return true;
	}

	public boolean isOnPath(GeoPointND PI, double eps) {

		GeoPoint P = (GeoPoint) PI;

		if (P.getPath() == this)
			return true;

		// check if P is on one of the segments
		for (int i = 0; i < segments.length; i++) {
			if (segments[i].isOnPath(P, eps))
				return true;
		}
		return false;
	}
	
	public boolean isOnPath(Coords coords, double eps) {


		// check if P is on one of the segments
		for (int i = 0; i < segments.length; i++) {
			if (segments[i].isOnPath(coords, eps))
				return true;
		}
		return false;
	}

	public void pathChanged(GeoPointND PI) {

		// if kernel doesn't use path/region parameters, do as if point changed
		// its coords
		if (!getKernel().usePathAndRegionParameters(PI)) {
			pointChanged(PI);
			return;
		}

		// parameter is between 0 and segment.length,
		// i.e. floor(parameter) gives the segment index

		PathParameter pp = PI.getPathParameter();
		pp.t = pp.t % segments.length;
		if (pp.t < 0)
			pp.t += segments.length;
		int index = (int) Math.floor(pp.t);
		GeoSegmentND seg = segments[index];
		double segParameter = pp.t - index;

		// calc point for given parameter
		PI.setCoords2D(seg.getPointX(segParameter),
				seg.getPointY(segParameter), 1);
	}

	public void pointChanged(GeoPointND PI) {

		Coords coords = PI.getCoordsInD2();
		double qx = coords.getX() / coords.getZ();
		double qy = coords.getY() / coords.getZ();

		double minDist = Double.POSITIVE_INFINITY;
		double resx = 0, resy = 0, resz = 0, param = 0;

		// find closest point on each segment
		PathParameter pp = PI.getPathParameter();
		for (int i = 0; i < segments.length; i++) {
			PI.setCoords2D(qx, qy, 1);
			segments[i].pointChanged(PI);

			coords = PI.getCoordsInD2();
			double x = coords.getX() / coords.getZ() - qx;
			double y = coords.getY() / coords.getZ() - qy;
			double dist = x * x + y * y;
			if (dist < minDist) {
				minDist = dist;
				// remember closest point
				resx = coords.getX();
				resy = coords.getY();
				resz = coords.getZ();
				param = i + pp.t;
			}
		}

		PI.setCoords2D(resx, resy, resz);

		pp.t = param;
	}

	/*
	 * Region interface implementation
	 */

	// by default, a polygon is always a region.
	// A polygon can also be a path (as the boundary of the region)
	@Override
	public boolean isRegion() {
		return true;
	}

	/**
	 * @param PI point
	 * @param update TODO unused even in 3D 
	 * @return true if PI is in this polygon
	 */
	public boolean isInRegion(GeoPointND PI, boolean update) {

		Coords coords = PI.getCoordsInD2();
		return isInRegion(coords.getX() / coords.getZ(),
				coords.getY() / coords.getZ());

	}

	public boolean isInRegion(GeoPointND PI) {

		return isInRegion(PI, false);

	}

	/**
	 * says if the point (x0,y0) is in the region
	 * 
	 * @param x0
	 *            x-coord of the point
	 * @param y0
	 *            y-coord of the point
	 * @return true if the point (x0,y0) is in the region
	 */
	public boolean isInRegion(double x0, double y0) {

		double x1, y1, x2, y2;
		int numPoints = getPointsLength();
		x1 = getPointX(numPoints - 1) - x0;
		y1 = getPointY(numPoints - 1) - y0;

		boolean ret = false;
		for (int i = 0; i < numPoints; i++) {
			x2 = getPointX(i) - x0;
			y2 = getPointY(i) - y0;
			int inter = intersectOx(x1, y1, x2, y2);
			if (inter == 2)
				return true; // point on an edge
			ret = ret ^ (inter == 1);
			x1 = x2;
			y1 = y2;
		}

		return ret;
	}

	final public void regionChanged(GeoPointND P) {

		// if kernel doesn't use path/region parameters, do as if point changed
		// its coords
		if (!getKernel().usePathAndRegionParameters(P)
				|| P.getRegionParameters().isNaN()) {
			pointChangedForRegion(P);
			return;
		}

		// GeoPoint P = (GeoPoint) PI;
		RegionParameters rp = P.getRegionParameters();

		if (rp.isOnPath())
			pathChanged(P);
		else {
			// Application.debug(rp.getT1()+ "," + rp.getT2());
			// pointChangedForRegion(P);
			double xu = p1.inhomX - p0.inhomX;
			double yu = p1.inhomY - p0.inhomY;
			double xv = p2.inhomX - p0.inhomX;
			double yv = p2.inhomY - p0.inhomY;

			setRegionChanged(P, p0.inhomX + rp.getT1() * xu + rp.getT2() * xv,
					p0.inhomY + rp.getT1() * yu + rp.getT2() * yv);

			if (!isInRegion(P, false)) {
				pointChanged(P);
				rp.setIsOnPath(true);
			}

		}
	}

	/**
	 * set region coords (x,y) to point PI
	 * 
	 * @param PI
	 *            point
	 * @param x
	 *            x-coord
	 * @param y
	 *            y-coord
	 */
	public void setRegionChanged(GeoPointND PI, double x, double y) {
		GeoPoint P = (GeoPoint) PI;
		P.x = x;
		P.y = y;
		P.z = 1;

	}

	public void pointChangedForRegion(GeoPointND P) {

		P.updateCoords2D();

		RegionParameters rp = P.getRegionParameters();

		// Application.debug("isInRegion : "+isInRegion(P));

		if (!isInRegion(P.getX2D(), P.getY2D())) {
			pointChanged(P);
			rp.setIsOnPath(true);
		} else {
			if (numCS != 3) { // if the coord sys is not defined by 3
				// independent points, then the point lies on
				// the path
				pointChanged(P);
				rp.setIsOnPath(true);
			} else {
				rp.setIsOnPath(false);
				double xu = p1.inhomX - p0.inhomX;
				double yu = p1.inhomY - p0.inhomY;
				double xv = p2.inhomX - p0.inhomX;
				double yv = p2.inhomY - p0.inhomY;
				double x = P.getX2D() - p0.inhomX;
				double y = P.getY2D() - p0.inhomY;
				rp.setT1((xv * y - x * yv) / (xv * yu - xu * yv));
				rp.setT2((x * yu - xu * y) / (xv * yu - xu * yv));

				// Application.debug(rp.getT1()+","+rp.getT2());

				P.updateCoordsFrom2D(false, null);
			}
		}
	}

	/**
	 * @param newp0 new 1st point for region coords
	 * @param newp1 new 2nd point for region coords
	 * @param newp2 new 3rd point for region coords
	 */
	final public void updateRegionCS(GeoPoint newp0, GeoPoint newp1, GeoPoint newp2) {
		this.p0 = newp0;
		this.p1 = newp1;
		this.p2 = newp2;
		numCS = 3;

	}

	/**
	 * update the region coord sys with the 3 first points
	 */
	final public void updateRegionCSWithFirstPoints(){
		updateRegionCS(getPoint(0), getPoint(1), getPoint(2));
	}

	/**
	 * update the coord sys used for region parameters
	 */
	final public void updateRegionCS() {
		
		if (getPoints() == null){
			return;
		}
		
		if (p2 != null && !GeoPoint.collinear(p0, p1, p2)){
			numCS = 3;
			return;
		}

		p0 = getPoint(0);
		numCS = 1;
		// App.debug(" p0 = " + p0.inhomX + "," + p0.inhomY);

		int secondPoint = -1;
		boolean secondPointFound = false;
		for (secondPoint = 1; secondPoint < getPoints().length
				&& !secondPointFound; secondPoint++) {
			p1 = getPoint(secondPoint);
			// Application.debug(" p1 ("+secondPoint+") = "+p1.inhomX+","+p1.inhomY);
			if (!Kernel
					.isEqual(p0.inhomX, p1.inhomX, Kernel.STANDARD_PRECISION))
				secondPointFound = true;
			else if (!Kernel.isEqual(p0.inhomY, p1.inhomY,
					Kernel.STANDARD_PRECISION))
				secondPointFound = true;
			// App.debug(" secondPointFound = " + secondPointFound);
		}

		int thirdPoint = -1;
		if (secondPointFound) {
			numCS++;
			secondPoint--;
			boolean thirdPointFound = false;
			for (thirdPoint = getPoints().length - 1; thirdPoint > secondPoint
					&& !thirdPointFound; thirdPoint--) {
				p2 = getPoint(thirdPoint);
				if (!GeoPoint.collinear(p0, p1, p2)) {
					thirdPointFound = true;
					numCS++;
				}
			}
		}

		// thirdPoint++;
		// Application.debug(" secondPoint = "+secondPoint+"\n thirdPoint = "+thirdPoint);

		// App.debug("numCS = " + numCS);

	}

	// //////////////////////////
	// interface GeoSurfaceFinite
	// /////////////////////////////
	private boolean asBoundary = false;

	public void setRole(boolean isAsBoundary) {
		this.asBoundary = isAsBoundary; // false means 'as region'
	}

	public boolean asBoundary() {
		return asBoundary;
	}

	/**
	 * returns 1 if the segment ((x1,y1),(x2,y2)) intersects y=0 for x>0, 2 if
	 * (0,0) is on the segment and -1 otherwise If the segment only touches the
	 * line for x>0, this touch is counted only if the segment is in y>0.
	 * 
	 * Segments lying entirely on y=0 are ignored, unless they go through (0,0).
	 * */
	private static int intersectOx(double px1, double py1, double px2, double py2) {
		double x1=px1,x2=px2,y1=py1,y2=py2;
		double eps = Kernel.STANDARD_PRECISION;

		if (Kernel.isZero(y1)) { // first point on (Ox)
			if (Kernel.isZero(y2)) { // second point on (Ox)
				if (Kernel.isGreaterEqual(0, x1 * x2)) // 0 on segment
					return 2;
				// ignore the segment on 0x if it is whole on left or right
				return -1;
			}
			// only first point is on (Ox)
			if (Kernel.isZero(x1)) // first point ~ 0
				return 2;
			return y2 > eps && x1 > eps ? 1 : -1;
		} else if (Kernel.isZero(y2)) {
			// only second point is on (0x)
			if (Kernel.isZero(x2)) // second point ~ 0
				return 2;
			return y1 > eps && x2 > eps ? 1 : -1;
		} else if (y1 * y2 > eps) // segment totally above or under
			return -1;
		else {
			if (y1 > y2) { // first point under (Ox)
				double y = y1;
				y1 = y2;
				y2 = y;
				double x = x1;
				x1 = x2;
				x2 = x;
			}

			if ((x1 + eps < 0) && (x2 + eps < 0)) // segment totally on the left
				return -1;
			else if ((x1 > eps) && (x2 > eps)) // segment totally on the right
				return 1;
			else if (x1 * y2 > x2 * y1 + eps) // angle > 0
				return 1;
			else if (x1 * y2 + eps < x2 * y1) // angle < 0
				return -1;
			else
				return 2; // angle ~ 0
		}
	}

	/**
	 * returns all class-specific xml tags for getXML GeoGebra File Format
	 */
	@Override
	protected void getXMLtags(StringBuilder sb) {
		getLineStyleXML(sb);
		getXMLvisualTags(sb);
		getXMLanimationTags(sb);
		getXMLfixedTag(sb);
		getAuxiliaryXML(sb);
		getBreakpointXML(sb);
		getScriptTags(sb);
	}

	/**
	 * @return minimum line thickness (normally 1, but 0 for polygons, integrals
	 *         etc)
	 */
	@Override
	public int getMinimumLineThickness() {
		return 0;
	}

	private boolean trace;

	@Override
	public boolean isTraceable() {
		return true;
	}

	public void setTrace(boolean trace) {
		this.trace = trace;
	}

	public boolean getTrace() {
		return trace;
	}

	// ///////////////////
	// 3D stuff
	// ///////////////////

	/**
	 * Returns the i-th 3D point of this polygon.
	 * 
	 * @param i
	 *            number of point
	 * @return the i-th point
	 */
	public Coords getPoint3D(int i) {
		return getPoint(i).getInhomCoordsInD3();
	}

	/**
	 * if this is a part of a closed surface
	 * 
	 * @return if this is a part of a closed surface
	 */
	public boolean isPartOfClosedSurface() {
		return false;
		//return (getMetasLength() > 0);
	}


	/**
	 * if this is a convex polygon
	 * 
	 * @return if this is a convex polygon
	 */
	public boolean isConvex() {

		/*
		if (getPointsLength() <= 3){
			return true;
		}
		*/


		// remove same successive points
		ArrayList<Double> xList = new ArrayList<Double>();
		ArrayList<Double> yList = new ArrayList<Double>();
		double x0 = getPointX(0);
		double y0 = getPointY(0);
		xList.add(x0);
		yList.add(y0);
		for (int i = 1; i < getPointsLength(); i++){
			double x1 = getPointX(i);
			double y1 = getPointY(i);
			if (!Kernel.isEqual(x0, x1) || !Kernel.isEqual(y0, y1)){
				xList.add(x1);
				yList.add(y1);
				x0 = x1;
				y0 = y1;
			}
		}

		int n = xList.size();
		/*
		if (n<=3){
			return true;
		}
		*/


		// remove last point if equals first points
		if (Kernel.isEqual(xList.get(0), xList.get(n-1)) && Kernel.isEqual(yList.get(0), yList.get(n-1))){
			/*
			if (n==4){
				return true;
			}
			*/
			xList.remove(n-1);
			yList.remove(n-1);
			n--;
		}






		// check orientations
		boolean answer = true;
		boolean hasAngle360 = false;

		double x1 = xList.get(n-1);
		double y1 = yList.get(n-1);
		double dx1 = x1 - xList.get(n-2);
		double dy1 = y1 - yList.get(n-2);

		double x2 = xList.get(0);
		double y2 = yList.get(0);
		double dx2 = x2 - x1;
		double dy2 = y2 - y1;


		// calc first orientation
		int orientation = Kernel.compare(dy1*dx2, dx1*dy2);
		if (orientation == 0){
			if (Kernel.isGreater(0, dx1*dx2+dy1*dy2)){ // U-turn
				answer = false; 
			}
		}





		int i = 1;
		while ((answer == true)&&(i<n)) {
			dx1 = dx2;
			dy1 = dy2;
			x1 = x2;
			y1 = y2;
			x2 = xList.get(i);
			y2 = yList.get(i);
			dx2 = x2 - x1;
			dy2 = y2 - y1;
			int orientation2 = Kernel.compare(dy1*dx2, dx1*dy2);
			//App.debug(""+answer+","+hasAngle360);
			//App.debug("i : "+i+" -- orientations : "+orientation+","+orientation2);

			if (!hasAngle360 && orientation2 == 0){ // U-turn
				if (Kernel.isGreater(0, dx1*dx2+dy1*dy2)){
					answer = false;
				}
			}

			if (answer){
				if (orientation == 0){ // no orientation for now
					orientation = orientation2;
				}else{				
					if (orientation2 != 0 && orientation2 != orientation){
						answer = false; // stop here
					}
				}
			}
			i++;
		}
		
		if (answer){
			convexOrientation = orientation;
		}



		return answer;
	}

	
	/**
	 * orientation (1/-1) when convex
	 */
	private int convexOrientation;



	/**
	 * 
	 * @return true if points orientation are not the same as xOy plane
	 * (only used in 2D)
	 */
	public boolean isConvexInverseDirection(){
		
		//App.debug(""+convexOrientation);
		
		return (convexOrientation > 0);
	}


	/*
	private class SweepComparator implements Comparator<Integer>{

		private GeoPolygon p;

		/**
	 * constructor
	 * @param p polygon 
	 *
		public SweepComparator(GeoPolygon p) {
			this.p = p;
		}

		public int compare(Integer i1, Integer i2) {

			// smallest x
			double x1 = p.getPointX(i1);
			double x2 = p.getPointX(i2);
			if (Kernel.isGreater(x2, x1)){
				return -1;
			}			
			if (Kernel.isGreater(x1, x2)){
				return 1;
			}

			// then smallest y
			double y1 = p.getPointY(i1);
			double y2 = p.getPointY(i2);
			if (Kernel.isGreater(y2, y1)){
				return -1;
			}			
			if (Kernel.isGreater(y1, y2)){
				return 1;
			}

			// then smallest index
			if (i1 < i2){
				return -1;
			}
			if (i1 > i2){
				return 1;
			}

			// same point
			return 0;
		}


	}
	 */


	@Override
	public boolean hasDrawable3D() {
		return true;
	}
	
	private Coords labelPosition;

	@Override
	public Coords getLabelPosition() {
		double x = 0;
		double y = 0;
		double z = 0;
		for (int i = 0; i < getPointsLength(); i++) {
			Coords coords = getPoint3D(i);
			x += coords.getX();
			y += coords.getY();
			z += coords.getZ();
		}
		
		if (labelPosition == null){
			labelPosition =  new Coords(x / getPointsLength(), y / getPointsLength(), z / getPointsLength(), 1);
		}else{
			labelPosition.setX(x / getPointsLength());
			labelPosition.setY(y / getPointsLength());
			labelPosition.setZ(z / getPointsLength());
		}
		return labelPosition;
	}

	// //////////////////////////////////////
	// GEOCOORDSYS2D INTERFACE
	// //////////////////////////////////////

	public CoordSys getCoordSys() {
		return CoordSys.Identity3D;
	}

	public Coords getPoint(double x2d, double y2d) {
		return getCoordSys().getPoint(x2d, y2d);
	}

	public Coords[] getNormalProjection(Coords coords) {
		return getCoordSys().getNormalProjection(coords);
	}

	public Coords[] getProjection(Coords oldCoords, Coords willingCoords,
			Coords willingDirection) {
		
		Coords[] result = new Coords[] { new Coords(4), new Coords(4)};
		willingCoords.projectPlaneThruVIfPossible(getCoordSys()
				.getMatrixOrthonormal(), oldCoords, willingDirection, result[0], result[1]);
		
		return result;
	}

	// ////////////////////////////////////////////////////
	// PARENT NUMBER (HEIGHT OF A PRISM, ...)
	// ////////////////////////////////////////////////////

	private ChangeableCoordParent changeableCoordParent = null;

	/**
	 * sets the parents for changing coords
	 * @param number number
	 * @param direction direction
	 * 
	 */
	final public void setChangeableCoordParent(GeoNumeric number, GeoElement direction) {
		changeableCoordParent = new ChangeableCoordParent(this, number, direction);
	}




	@Override
	public boolean hasChangeableCoordParentNumbers() {
		return (changeableCoordParent != null);
	}


	@Override
	public void recordChangeableCoordParentNumbers() {
		changeableCoordParent.record();
	}

	@Override
	public boolean moveFromChangeableCoordParentNumbers(Coords rwTransVec,
			Coords endPosition, Coords viewDirection,
			ArrayList<GeoElement> updateGeos,
			ArrayList<GeoElement> tempMoveObjectList, EuclidianView view) {

		if (changeableCoordParent == null) {
			return false;
		}

		return changeableCoordParent.move(rwTransVec, endPosition, viewDirection, updateGeos, tempMoveObjectList, view);


	}

	public void rotate(NumberValue r) {
		for (int i = 0; i < getPointsLength(); i++)
			getPoint(i).rotate(r);
		updatePathRegion();
	}

	public void rotate(NumberValue r, GeoPointND S) {
		Coords Scoords = S.getInhomCoords();
		for (int i = 0; i < getPointsLength(); i++)
			getPoint(i).rotate(r, Scoords);
		updatePathRegion();
	}

	public void matrixTransform(double a00, double a01, double a10, double a11) {
		for (int i = 0; i < getPointsLength(); i++)
			getPoint(i).matrixTransform(a00, a01, a10, a11);
		
		calcArea();
		
		updatePathRegion();
	}

	public void translate(Coords v) {
		for (int i = 0; i < getPointsLength(); i++){
			getPoint(i).translate(v);
		}
		updatePathRegion();
	}

	public void dilate(NumberValue r, Coords S) {
		for (int i = 0; i < getPointsLength(); i++)
			getPoint(i).dilate(r, S);

		calcArea();

		updatePathRegion();
	}

	public void mirror(Coords Q) {
		
		// important for centroid calculation
		area *= -1;
		
		for (int i = 0; i < getPointsLength(); i++)
			getPoint(i).mirror(Q);
		updatePathRegion();
	}

	private void updatePathRegion() {
		updateRegionCS();
		this.updateSegments();
	}

	public void mirror(GeoLineND g) {

		// important for centroid calculation
		area *= -1;

		for (int i = 0; i < getPointsLength(); i++)
			getPoint(i).mirror(g);
		updatePathRegion();
	}

	/**
	 * Returns true iff all vertices are labeled
	 * 
	 * @return true iff all vertices are labeled
	 */
	public boolean isAllVertexLabelsSet() {
		for (int i = 0; i < getPointsLength(); i++)
			if (!getPoint(i).isLabelSet())
				return false;
		return true;
	}

	/**
	 * Returns true iff number of vertices is not volatile
	 * 
	 * @return true iff number of vertices is not volatile
	 */
	public boolean isVertexCountFixed() {
		// regularPolygon[vertex,vertex,count]
		if (getParentAlgorithm() instanceof AlgoPolygonRegularND)
			return false;
		// polygon[list]
		if (getParentAlgorithm().getInput().length < 3)
			return false;
		return true;
	}


	public Coords getDirectionInD3() {

		return Coords.VZ;
	}









	public void matrixTransform(double a00, double a01, double a02, double a10,
			double a11, double a12, double a20, double a21, double a22) {

		for (int i = 0; i < getPointsLength(); i++){
			((MatrixTransformable) getPointND(i)).matrixTransform(a00, a01, a02, a10, a11,
					a12, a20, a21, a22);
		}

		calcArea();

		updatePathRegion();

	}

	public void calcArea() {
		
		
		// eg Dilate[Polygon[(0,0),(1,1),(1,0)],4]
		if (algoParent instanceof AlgoTransformation) {
			AlgoTransformation algo = (AlgoTransformation)algoParent;
			
			double sf = algo.getAreaScaleFactor();
			

			GeoPolygon input = (GeoPolygon) algo.getInput(algoParent.getClassName() == Commands.ApplyMatrix ? 1 : 0);
			setArea(input.getAreaWithSign() * sf);
			
			return;
			
		}

		if (algoParent instanceof PolygonAlgo) {
			((PolygonAlgo)algoParent).calcArea();
		}
		
		// eg IntersectRegion[Polygon[(1,1),(0,1),(1,0)],Polygon[(1,1),(0,1),(1,0)]]
		// eg Union[Polygon[(1,1),(0,1),(1,0)],Polygon[(1,1),(0,1),(1,0)]]
		setArea(AlgoPolygon.calcAreaWithSign(getPoints()));
	}

	
	private double[] tmp3;

	public void calcCentroid(GeoPointND p) {
		
		if (algoParent instanceof PolygonAlgo) {
			((PolygonAlgo)algoParent).calcCentroid((GeoPoint) p);
			return;
		}
		
		// just do long method
		// could improve by transforming original centroid, but not worth doing
		// test-case Centroid[Dilate[Polygon[(0,0),(1,1),(1,0)],4]]
		// test-case Centroid[Polygon[(0,0),(1,1),(1,0)]]
		if (tmp3 == null){
			tmp3 = new double[3];
		}
		AlgoPolygon.calcCentroid(tmp3, area, getPoints());
		if (Double.isNaN(tmp3[0])){
			p.setUndefined();
		}else{
			p.setCoords(tmp3[0], tmp3[1], tmp3[2]);
		}
		
		
	}

	public void toGeoCurveCartesian(GeoCurveCartesian curve) {
		curve.setFromPolyLine(points, true);
	}




	@Override
	public String getDefaultLabel(char[] chars, boolean isInteger) {
		if (chars != null)
			return super.getDefaultLabel(chars, isInteger);
		int counter = 0;
		String str;
		String name;
		if (getMetasLength() == 1)
			name = getLoc().getPlainLabel("face"); // Name.face
		else
			name = getLoc().getPlainLabel("polygon"); // Name.polygon
		do {
			counter++;
			str = name
					+ kernel.internationalizeDigits(counter + "",
							StringTemplate.defaultTemplate);
		} while (!cons.isFreeLabel(str));
		return str;
	}

	/**
	 * modify input points. Assume that parent algo is an instance of
	 * AlgoPolygon
	 * 
	 * @param newPoints
	 *            new input points
	 */
	public void modifyInputPoints(GeoPointND[] newPoints) {
		AlgoPolygon algo = (AlgoPolygon) getParentAlgorithm();
		algo.modifyInputPoints(newPoints);
	}





	public Variable[] getBotanaVars(GeoElement geo) {
		// It's OK to return null here:
		return null;
	}

	public Polynomial[] getBotanaPolynomials(GeoElement geo)
			throws NoSymbolicParametersException {
		// It's OK to return null here:
		return null;
	}


	@Override
	public int getMetasLength(){
		if (metas == null){
			return 0;
		}

		return metas.size();
	}

	public GeoElement[] getMetas(){
		GeoElement[] ret = new GeoElement[metas.size()];
		metas.toArray(ret);
		return ret;
	}

	private TreeSet<GeoElement> metas;

	/**
	 * add the polyhedron has meta geo for this (e.g. parent polyhedron, or linked polyhedron)
	 * @param polyhedron polyhedron
	 */
	public void addMeta(GeoElement polyhedron){
		if (metas == null){
			metas = new TreeSet<GeoElement>();
		}

		metas.add(polyhedron);
	}

	/**
	 * remove polyhedron as meta for this
	 * @param polyhedron polyhedron
	 */
	public void removeMeta(GeoElement polyhedron){		
		if (metas != null){
			metas.remove(polyhedron);
		}
	}




	@Override
	public double distance(final GeoPoint p) {
		double d = Double.POSITIVE_INFINITY;
		for (GeoSegmentND seg : getSegments()){
			double d1 = seg.distance(p);
			if (d1 < d){
				d = d1;
			}
		}
		return d;
	}



	///////////////////////////////////
	// REVERSE ORIENTATION FOR DRAWING
	///////////////////////////////////

	private boolean reverseNormalForDrawing = false;

	/**
	 * set that normal should be reversed for 3D drawing
	 * @param flag flag
	 */
	public void setReverseNormalForDrawing(boolean flag) {
		reverseNormalForDrawing = flag;
	}

	/**
	 * @return if normal should be reversed for 3D drawing
	 */
	public boolean getReverseNormalForDrawing(){
		return reverseNormalForDrawing;
	}
	
	
	
	
	
	/**
	 * Sets the point size (and/or visibility)
	 * @param size new point size
	 */
	public void setPointSizeOrVisibility(int size){
		if (size > 0){
			setPointSize(size);
		}else{
			setPointNotVisibile();
		}
	}

	private void setPointSize(int size){
		for (GeoPointND point : points){
			((GeoElement) point).setEuclidianVisibleIfNoConditionToShowObject(true);
			point.setPointSize(size);
			point.updateRepaint();
		}
	}
	
	private void setPointNotVisibile(){
		for (GeoPointND point : points){
			((GeoElement) point).setEuclidianVisibleIfNoConditionToShowObject(false);
			point.updateRepaint();
		}
	}
	
	@Override
	final public HitType getLastHitType(){
		return HitType.ON_FILLING;
	}
	
	/**
	 * 
	 * @param cons construction
	 * @return 2D point if 2D polygon, 3D point if 3D polygon
	 */
	public GeoPointND newGeoPoint(Construction cons){
		return new GeoPoint(cons);
	}

	@Override
	public int getNumPoints() {
		return points == null ? 0 : points.length;
	}
}
