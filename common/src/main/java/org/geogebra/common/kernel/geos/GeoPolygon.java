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
import java.util.TreeSet;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.MatrixTransformable;
import org.geogebra.common.kernel.Path;
import org.geogebra.common.kernel.PathMover;
import org.geogebra.common.kernel.PathMoverGeneric;
import org.geogebra.common.kernel.PathParameter;
import org.geogebra.common.kernel.RegionParameters;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.algos.AlgoAnglePolygon;
import org.geogebra.common.kernel.algos.AlgoAnglePolygonND;
import org.geogebra.common.kernel.algos.AlgoJoinPointsSegment;
import org.geogebra.common.kernel.algos.AlgoJoinPointsSegmentInterface;
import org.geogebra.common.kernel.algos.AlgoPolygon;
import org.geogebra.common.kernel.algos.AlgoPolygonRegularND;
import org.geogebra.common.kernel.algos.AlgoTransformation;
import org.geogebra.common.kernel.algos.PolygonAlgo;
import org.geogebra.common.kernel.algos.SymbolicParametersBotanaAlgo;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.ExpressionNodeConstants.StringType;
import org.geogebra.common.kernel.arithmetic.ExpressionNodeEvaluator;
import org.geogebra.common.kernel.arithmetic.MyDouble;
import org.geogebra.common.kernel.arithmetic.NumberValue;
import org.geogebra.common.kernel.arithmetic.ValueType;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.discrete.PolygonTriangulation;
import org.geogebra.common.kernel.kernelND.GeoCoordSys2D;
import org.geogebra.common.kernel.kernelND.GeoCurveCartesianND;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.kernel.kernelND.GeoLineND;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.kernel.kernelND.GeoSegmentND;
import org.geogebra.common.kernel.kernelND.HasSegments;
import org.geogebra.common.kernel.matrix.CoordSys;
import org.geogebra.common.kernel.matrix.Coords;
import org.geogebra.common.kernel.prover.NoSymbolicParametersException;
import org.geogebra.common.kernel.prover.polynomial.PPolynomial;
import org.geogebra.common.kernel.prover.polynomial.PVariable;
import org.geogebra.common.plugin.GeoClass;
import org.geogebra.common.util.DoubleUtil;
import org.geogebra.common.util.ExtendedBoolean;
import org.geogebra.common.util.MyMath;

/**
 * Polygon through given points
 * 
 * @author Markus Hohenwarter
 */
public class GeoPolygon extends GeoElement implements GeoNumberValue,
		GeoSurfaceFinite, Traceable, PointRotateable, MatrixTransformable,
		Mirrorable, Translateable, Dilateable, GeoCoordSys2D, GeoPoly,
		Transformable, SymbolicParametersBotanaAlgo, HasSegments, FromMeta {

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
	/** @see #wasInitLabelsCalled() */
	protected boolean initLabelsCalled = false;

	/** says if the polygon had created its segments itself (used for 3D) */
	private boolean createSegments = true;

	private boolean isShape = false;
	/** true for polygons created by area intersection methods */
	protected boolean isIntersection;

	private boolean notFixedPointsLength = false;

	private ArrayList<GeoSegmentND> segmentsArray;

	private ArrayList<GeoPoint> pointsArray;

	private boolean trace;

	/**
	 * orientation (1/-1) when convex
	 */
	private int convexOrientation;

	private Coords labelPosition;
	private ChangeableParent changeableParent = null;

	private double[] tmp3;
	private TreeSet<GeoElement> metas;
	private boolean reverseNormalForDrawing = false;
	private PolygonTriangulation pt;
	private boolean isMask = false;

	private boolean showLineProperties = true;
	private boolean fillable = true;
	private boolean traceable = true;

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
		this.createSegments = createSegments;
		setPoints(points, cs, createSegments);
		setLabelVisible(false);
	}

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
	 * @param isIntersection
	 *            whether this is result of intersection; use different style in
	 *            such case
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
		// nothing to do here
	}

	/**
	 * for 3D stuff (unused here)
	 * 
	 * @param poly
	 *            polygon
	 * 
	 */
	public void setCoordSys(GeoPolygon poly) {
		// 3D only
	}

	/**
	 * for 3D stuff (unused here)
	 * 
	 * @param p
	 *            polygon
	 * 
	 */
	public void setCoordSysAndPoints3D(GeoPolygon p) {
		// 3D only
	}

	/**
	 * set that this polygon hasn't fixed points length (e.g. for regular
	 * polygons with slider). Used in getTypeString() to avoid bad type display
	 * in algebra view, properties view, etc.
	 * 
	 * @param flag
	 *            true if not fixed points length
	 */
	public void setNotFixedPointsLength(boolean flag) {
		notFixedPointsLength = flag;
	}

	@Override
	public String getTypeString() {
		if (notFixedPointsLength || points == null) {
			return "Polygon";
		}

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
	
	@Override
	final public HitType getLastHitType() {
		return HitType.ON_FILLING;
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
	public void setPoints(GeoPointND[] points, CoordSys cs, boolean createSegments) {
		this.points = points;
		setCoordSys(cs);

		if (createSegments) {
			updateSegments(cons);
		}
	}

	/**
	 * return number for points
	 * 
	 * @return number for points
	 */
	public int getPointsLength() {
		if (getPoints() == null) {
			return 0;
		}
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
	 * @param labels
	 *            labels of points and segments
	 */
	public void initLabels(String[] labels) {
		if (cons.isSuppressLabelsActive()) {
			return;
		}
		initLabelsCalled = true;

		// label polygon
		if (labels == null || labels.length == 0) {
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
				defaultSegmentLabels();
			}
		}
	}

	/**
	 * set that init label has been called (or not)
	 * 
	 * @param flag
	 *            flag
	 */
	public void setInitLabelsCalled(boolean flag) {
		initLabelsCalled = flag;
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
			if (getParentAlgorithm() instanceof AlgoPolygonRegularND) {
				points[2].setLabel(null);
			}

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
			String lowerCaseLabel = p
					.getLabel(StringTemplate.get(StringType.GEOGEBRA))
					.toLowerCase();

			// for sides A, B, C use labels a, b, c
			// or a_1 etc if necessary
			if (lowerCaseLabel.length() == 1) {
				char[] label = new char[1];
				label[0] = lowerCaseLabel.charAt(0);
				s.setLabel(s.getConstruction().getLabelManager()
						.getNextIndexedLabel(label));

			} else {

				// eg Point is "PtA"
				// -> opposite Segment is "pta"
				if (s.getConstruction()
						.isFreeLabel(lowerCaseLabel)) {
					s.setLabel(lowerCaseLabel);
				} else {
					// fallback: just use next available label
					s.setLabel(null);
				}
			}

		}
	}

	/**
	 * Updates all segments of this polygon for its point array. Note that the
	 * point array may be changed: this method makes sure that segments are
	 * reused if possible.
	 * 
	 * @param cons1
	 *            construction in which new segments are born
	 */
	public void updateSegments(Construction cons1) {
		if (points == null) {
			return;
		}

		// make sure the polygon is defined to get correct euclidian visibility
		setDefined();

		boolean euclidianVisible;

		// check array zand euclidian visibility
		if (segmentsArray == null) {
			segmentsArray = new ArrayList<>();
		}
		if (segmentsArray.size() < 1) {
			euclidianVisible = isEuclidianVisible();
		} else {
			euclidianVisible = segmentsArray.get(0).isEuclidianVisible();
		}

		segments = new GeoSegmentND[getPointsLength()];

		// set first values
		for (int i = 0; i < segmentsArray.size() && i < points.length; i++) {
			GeoPointND startPoint = points[i];
			GeoPointND endPoint = points[(i + 1) % getPointsLength()];
			GeoSegmentND segment = segmentsArray.get(i);
			AlgoJoinPointsSegmentInterface algo = (AlgoJoinPointsSegmentInterface) segment
					.getParentAlgorithm();
			algo.modifyInputPoints(startPoint, endPoint);
			algo.compute();
			segments[i] = segment;
			segment.setEuclidianVisible(euclidianVisible);
		}

		// adjust size
		for (int i = segmentsArray.size(); i < points.length; i++) {
			GeoPointND startPoint = points[i];
			GeoPointND endPoint = points[(i + 1) % getPointsLength()];
			GeoSegmentND segment = createSegment(cons1, startPoint, endPoint,
					euclidianVisible);
			segment.getParentAlgorithm().setProtectedInput(true); // avoid
																	// remove by
																	// other
																	// algos
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
	 * @param cons1
	 *            construction of the new segment
	 * 
	 * @param startPoint
	 *            the start point
	 * @param endPoint
	 *            the end point
	 * @param euclidianVisible
	 *            true to make the segment visible
	 * @return the segment
	 */
	public GeoSegmentND createSegment(Construction cons1, GeoPointND startPoint,
			GeoPointND endPoint, boolean euclidianVisible) {
		return createSegmentOwnDimension(cons1, startPoint, endPoint, euclidianVisible);
	}

	/**
	 * Create a segment with the same dimension as the polygon
	 * @return segment
	 */
	public GeoSegmentND createSegmentOwnDimension(Construction cons1, GeoPointND startPoint,
			GeoPointND endPoint, boolean euclidianVisible) {
		AlgoJoinPointsSegment algoSegment = new AlgoJoinPointsSegment(cons1,
				(GeoPoint) startPoint, (GeoPoint) endPoint, this, false);
		return createSegment(algoSegment.getSegment(), euclidianVisible);
	}

	/**
	 * ends the creation of the segment
	 * 
	 * @param segment
	 *            segment
	 * @param euclidianVisible
	 *            true to make the segment visible
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
				// circular definition
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
	 * @param cons1
	 *            consctruction
	 * @param ret
	 *            poly where to copy
	 */
	public void copyInternal(Construction cons1, GeoPolygon ret) {
		ret.set(this, cons1);
	}

	/**
	 * Factory method for polygons, overridden in 3D
	 * 
	 * @param cons1
	 *            construction
	 * @return new polygon
	 */
	protected GeoPolygon newGeoPolygon(Construction cons1) {
		return new GeoPolygon(cons, null);
	}

	@Override
	public final void set(GeoElementND geo) {
		set(geo, cons);
	}

	/**
	 * @param geo
	 *            template geo
	 * @param cons1
	 *            construction
	 */
	protected void set(GeoElementND geo, Construction cons1) {
		GeoPolygon poly = (GeoPolygon) geo;
		area = poly.area;

		setReverseNormalForDrawing(poly.getReverseNormalForDrawing());

		if (!notFixedPointsLength) { // maybe already set by AlgoListElement
			notFixedPointsLength = poly.notFixedPointsLength;
		}

		// fix for Sequence[Polygon[Element[liste1, i], Element[liste1, i + 1],
		// j], i, 0, 300]
		if (poly.getPoints() == null) {
			setUndefined();
			return;
		}

		// set values
		updatePoints(poly.getPoints());

		setCoordSysAndPoints3D(poly);
		updateSegments(cons1);
		defined = poly.defined;

		if (poly.hasChangeableParent3D()) {
			setChangeableParent(poly.changeableParent);
		}
		updateRegionCS();

	}

	/**
	 * set points matching geos list, and segments
	 * 
	 * @param geos
	 *            input points
	 */
	public void setPointsAndSegments(GeoPointND[] geos) {
		updatePoints(geos);
		updateSegments(cons);

	}

	private void updatePoints(GeoPointND[] geos) {
		setPointsLength(geos.length, null);
		for (int i = 0; i < getPoints().length; i++) {
			ExpressionNode oldDef = getPoint(i).getDefinition();
			getPoint(i).set(geos[i].toGeoElement(), false);
			if (!getPoint(i).isIndependent()) {
				getPoint(i).setDefinition(oldDef);
			}
		}

	}

	/**
	 * set points and segments length to arbitrary value (create new points and
	 * segments)
	 * 
	 * @param polyLength
	 *            length
	 */
	public void setPointsAndSegmentsLength(int polyLength) {
		setPointsLength(polyLength, null);
		updateSegments(cons);
	}

	/**
	 * set points length to arbitrary value (create new points)
	 * 
	 * @param polyLength
	 *            length
	 * @param template
	 *            points to be reused
	 */
	protected void setPointsLength(int polyLength, GeoPointND[] template) {

		if (pointsArray == null) {
			pointsArray = new ArrayList<>();
		}

		// augment array size if array < polyLength
		for (int i = pointsArray.size(); i < polyLength; i++) {
			if (template != null && template.length > i
					&& template[i] instanceof GeoPoint) {
				pointsArray.add((GeoPoint) template[i]);
			} else {
				pointsArray.add(new GeoPoint(cons));
			}
		}

		// set last points undefined if array > polyLength
		for (int i = polyLength; i < pointsArray.size(); i++) {
			pointsArray.get(i).setUndefined();
		}

		// make sure both arrays have same size
		if (getPoints() == null || getPoints().length != polyLength) {
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
	@Override
	public GeoPoint getPoint(int i) {
		return (GeoPoint) points[i];
	}

	/**
	 * Returns the points of this polygon as GeoPoints. Note that this array may
	 * change dynamically.
	 * 
	 * @return points of this polygon.
	 */
	@Override
	public GeoPointND[] getPoints() {
		return points;
	}

	/**
	 * set the 2D points
	 * 
	 * @param points
	 *            2D points
	 */
	public void setPoints2D(GeoPoint[] points) {
		this.points = points;
	}

	/**
	 * Returns the points of this polygon as GeoPointNDs. Note that this array
	 * may change dynamically.
	 * 
	 * @return points of this polygon
	 */
	@Override
	final public GeoPointND[] getPointsND() {
		return points;
	}

	/**
	 * Returns i-th vertex of this polygon
	 * 
	 * @param i
	 *            index
	 * @return i-th pointt
	 */
	@Override
	final public GeoPointND getPointND(int i) {
		return points[i];
	}

	/**
	 * Returns the segments of this polygon. Note that this array may change
	 * dynamically.
	 * 
	 * @return segments of this polygon.
	 */
	@Override
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
		return fillable;
	}

	/**
	 * Set whether this object is fillable.
	 *
	 * @param fillable true to set object to fillable, false otherwise.
	 */
	public void setFillable(boolean fillable) {
		this.fillable = fillable;
	}

	@Override
	public boolean isInverseFillable() {
		return isFillable();
	}

	// Michael Borcherds 2008-01-26 BEGIN
	/**
	 * Sets this polygon's area . This method should only be called by its
	 * parent algorithm of type AlgoPolygon
	 * 
	 * @param area
	 *            area
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
	@Override
	public double getArea() {
		if (isDefined()) {
			return Math.abs(area);
		}
		return Double.NaN;
	}

	@Override
	public Path getBoundary() {
		boolean suppress = this.cons.isSuppressLabelsActive();
		kernel.setSilentMode(true);

		GeoPointND[] pointsForPolyLine = new GeoPointND[getPointsLength() + 1];
		System.arraycopy(points, 0, pointsForPolyLine, 0, getPointsLength());
		pointsForPolyLine[getPointsLength()] = pointsForPolyLine[0];

		GeoPolyLine pl = new GeoPolyLine(this.getConstruction(),
				pointsForPolyLine);

		kernel.setSilentMode(false);
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
			return MyMath.sgn(area);
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
	public boolean hasLineOpacity() {
		return true;
	}

	@Override
	public void setLineOpacity(int lineOpacity) {
		setLineOpacity(lineOpacity, true);
	}

	/**
	 * Sets the line opacity for this GeoElement.
	 * 
	 * @param lineOpacity
	 *            opacity value between 0 - 255
	 * @param updateSegments
	 *            true to apply this setting to segments
	 */
	public void setLineOpacity(int lineOpacity, boolean updateSegments) {
		super.setLineOpacity(lineOpacity);
		if (updateSegments && segments != null) {
			for (int i = 0; i < segments.length; i++) {
				if (segments[i] != null) {
					segments[i].setLineOpacity(lineOpacity);
					segments[i].updateVisualStyle(GProperty.LINE_STYLE);
				}
			}
		}
	}

	/**
	 * Yields true if the area of this polygon is equal to the area of polygon
	 * p.
	 * 
	 * @param geo
	 *            other geo (type check for polygon included)
	 * 
	 * @return whether the two polygons have the same area
	 */
	// this method is the original isEqual, modified 2014-01
	public boolean hasSameArea(GeoElement geo) {
		// return false if it's a different type
		if (geo.isGeoPolygon()) {
			return DoubleUtil.isEqual(getArea(), ((GeoPolygon) geo).getArea());
		}
		return false;
	}

	/**
	 * Yields true if the points of this polygon is equal to the points of
	 * polygon p.
	 */
	@Override
	public boolean isEqual(GeoElementND geo) {
		// test 3D is geo is 3D
		if (geo.isGeoElement3D()) {
			return geo.isEqual(this);
		}
		// return false if it's a different type
		if (geo.isGeoPolygon()) {
			GeoPolygon g = (GeoPolygon) geo;

			// return false if the number of points is different
			int gLength = g.getPointsLength();
			if (gLength == this.getPointsLength()) {

				// search for a first common point
				GeoPoint firstPoint = this.getPoint(0);
				boolean fPointFound = false;
				int iFirstPoint = 0;
				while ((!fPointFound) && (iFirstPoint < gLength)) {
					if (firstPoint.isEqual(g.getPoint(iFirstPoint))) {
						fPointFound = true;
					} else {
						iFirstPoint++;
					}
				}

				// next point
				if (fPointFound) {
					boolean sPointFound = false;
					int step = 1;
					if (this.getPoint(1).isEqual(
							g.getPoint((iFirstPoint + step) % gLength))) {
						sPointFound = true;
					} else {
						step = -1;
						int j = iFirstPoint + step;
						if (j < 0) {
							j = gLength - 1;
						}
						if (this.getPoint(1).isEqual(g.getPoint(j))) {
							sPointFound = true;
						}
					}

					// other points
					if (sPointFound) {
						int i = 2;
						int j = iFirstPoint + step + step;
						if (j < 0) {
							j = j + gLength;
						}
						j = j % gLength;
						boolean pointOK = true;
						while ((pointOK) && (i < gLength)) {
							pointOK = (this.getPoint(i).isEqual(g.getPoint(j)));
							if (pointOK) {
								j = j + step;
								if (j < 0) {
									j = gLength - 1;
								}
								j = j % gLength;
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
	 * @param geo
	 *            input
	 * @return true - if input is congruent with this polygon false - otherwise
	 */
	@Override
	public ExtendedBoolean isCongruent(GeoElement geo) {
		if (geo instanceof GeoPolygon) {
			GeoPolygon polygon = (GeoPolygon) geo;
			// Polygons:
			// two polygon are congruent if the corresponding sides has same
			// length
			// and corresponding angles has same size
			int nrSidesPoly1 = this.getSegments().length;
			int nrSidesPoly2 = polygon.getSegments().length;
			// two polygon can be congruent when their number of sides are equal
			// and have the same area
			if (nrSidesPoly1 == nrSidesPoly2 && (this).hasSameArea(polygon)) {
				GeoSegmentND[] segmentsPoly1 = this.getSegments();
				GeoSegmentND[] segmentsPoly2 = polygon.getSegments();
				AlgoAnglePolygonND algo1 = new AlgoAnglePolygon(cons, this);
				AlgoAnglePolygonND algo2 = new AlgoAnglePolygon(cons, polygon);
				GeoElement[] anglesPoly1 = algo1.getAngles();
				GeoElement[] anglesPoly2 = algo2.getAngles();
				int nrOfShifts = 0;
				while (nrOfShifts <= segmentsPoly2.length) {
					// first two segments from segmentsPoly1 and segmentsPoly2
					// are
					// congruent
					if (ExpressionNodeEvaluator
							.evalEquals(kernel, segmentsPoly1[0],
									segmentsPoly2[0])
							.getBoolean()
							&& ExpressionNodeEvaluator.evalEquals(kernel,
									segmentsPoly1[1], segmentsPoly2[1])
									.getBoolean()) {
						break;
					}
					// first two segment from segmentPoly1 are congruent with
					// the
					// last two segment from segmentPoly2
					if (ExpressionNodeEvaluator
							.evalEquals(kernel, segmentsPoly1[0],
									segmentsPoly2[segmentsPoly2.length - 1])
							.getBoolean()
							&& ExpressionNodeEvaluator.evalEquals(kernel,
									segmentsPoly1[1],
									segmentsPoly2[segmentsPoly2.length - 2])
									.getBoolean()) {
						break;
					}
					segmentsPoly2 = shiftSegments(segmentsPoly2);
					nrOfShifts++;
				}
				nrOfShifts = 0;
				while (nrOfShifts <= anglesPoly2.length) {
					// we have the external angles
					if (((GeoAngle) anglesPoly1[0]).getValue() >= Math.PI
							&& ((GeoAngle) anglesPoly2[0])
									.getValue() >= Math.PI) {
						double d1 = 2 * Math.PI
								- ((GeoAngle) anglesPoly1[0]).getDouble();
						double d2 = 2 * Math.PI
								- ((GeoAngle) anglesPoly2[0]).getDouble();
						double d3 = 2 * Math.PI
								- ((GeoAngle) anglesPoly1[1]).getDouble();
						double d4 = 2 * Math.PI
								- ((GeoAngle) anglesPoly2[1]).getDouble();
						// first two angle values are congruent
						if (DoubleUtil.isEqual(d1, d2) && DoubleUtil.isEqual(d3, d4)) {
							break;
						}
						double d5 = 2 * Math.PI
								- ((GeoAngle) anglesPoly2[anglesPoly2.length
										- 1]).getDouble();
						double d6 = 2 * Math.PI
								- ((GeoAngle) anglesPoly2[anglesPoly2.length
										- 2]).getDouble();
						// first two angle values from anglesPoly1 equals to
						// last
						// two angle values from anglesPoly2
						if (DoubleUtil.isEqual(d1, d5) && DoubleUtil.isEqual(d3, d6)) {
							break;
						}
						anglesPoly2 = shiftAngles(anglesPoly2);
						nrOfShifts++;
					}
					// we have the internal angles of first polygon and external
					// angles of second polygon
					else if (((GeoAngle) anglesPoly1[0]).getValue() < Math.PI
							&& ((GeoAngle) anglesPoly2[0])
									.getValue() >= Math.PI) {
						double d1 = ((GeoAngle) anglesPoly1[0]).getDouble();
						double d2 = 2 * Math.PI
								- ((GeoAngle) anglesPoly2[0]).getDouble();
						double d3 = ((GeoAngle) anglesPoly1[1]).getDouble();
						double d4 = 2 * Math.PI
								- ((GeoAngle) anglesPoly2[1]).getDouble();
						// first two angle values are congruent
						if (DoubleUtil.isEqual(d1, d2) && DoubleUtil.isEqual(d3, d4)) {
							break;
						}
						double d5 = 2 * Math.PI
								- ((GeoAngle) anglesPoly2[anglesPoly2.length
										- 1]).getDouble();
						double d6 = 2 * Math.PI
								- ((GeoAngle) anglesPoly2[anglesPoly2.length
										- 2]).getDouble();
						// first two angle values from anglesPoly1 equals to
						// last
						// two angle values from anglesPoly2
						if (DoubleUtil.isEqual(d1, d5) && DoubleUtil.isEqual(d3, d6)) {
							break;
						}
						anglesPoly2 = shiftAngles(anglesPoly2);
						nrOfShifts++;
					}
					// we have the external angles of first polygon and internal
					// angles of second polygon
					else if (((GeoAngle) anglesPoly1[0]).getValue() >= Math.PI
							&& ((GeoAngle) anglesPoly2[0])
									.getValue() < Math.PI) {
						double d1 = 2 * Math.PI
								- ((GeoAngle) anglesPoly1[0]).getDouble();
						double d2 = ((GeoAngle) anglesPoly2[0]).getDouble();
						double d3 = 2 * Math.PI
								- ((GeoAngle) anglesPoly1[1]).getDouble();
						double d4 = ((GeoAngle) anglesPoly2[1]).getDouble();
						// first two angle values are congruent
						if (DoubleUtil.isEqual(d1, d2) && DoubleUtil.isEqual(d3, d4)) {
							break;
						}
						double d5 = ((GeoAngle) anglesPoly2[anglesPoly2.length
								- 1]).getDouble();
						double d6 = ((GeoAngle) anglesPoly2[anglesPoly2.length
								- 2]).getDouble();
						// first two angle values from anglesPoly1 equals to
						// last
						// two angle values from anglesPoly2
						if (DoubleUtil.isEqual(d1, d5) && DoubleUtil.isEqual(d3, d6)) {
							break;
						}
						anglesPoly2 = shiftAngles(anglesPoly2);
						nrOfShifts++;
					}
					// we have the internal angles of first and second polygon
					else {
						// first two angles from both angelPolys are congruent
						if (ExpressionNodeEvaluator
								.evalEquals(kernel, anglesPoly1[0],
										anglesPoly2[0])
								.getBoolean()
								&& ExpressionNodeEvaluator.evalEquals(kernel,
										anglesPoly1[1], anglesPoly2[1])
										.getBoolean()) {
							break;
						}
						// first two angles from anglesPoly1 equals to last two
						// angles from anglesPoly2
						if (ExpressionNodeEvaluator
								.evalEquals(kernel, anglesPoly1[0],
										anglesPoly2[anglesPoly2.length - 1])
								.getBoolean()
								&& ExpressionNodeEvaluator.evalEquals(kernel,
										anglesPoly1[1],
										anglesPoly2[anglesPoly2.length - 2])
										.getBoolean()) {
							break;
						}
						anglesPoly2 = shiftAngles(anglesPoly2);
						nrOfShifts++;
					}
				}

				boolean result = checkInBothDirection(segmentsPoly1,
						segmentsPoly2, anglesPoly1, anglesPoly2);
				algo1.remove();
				algo2.remove();
				return ExtendedBoolean.newExtendedBoolean(result);
			}
		}
		// case the geo is a GeoNumeric, e.g. area using formula
		// we can check whether the areas are equal or not
		if (geo instanceof GeoNumeric) {
			if (DoubleUtil.isEqual(this.getArea(), ((GeoNumeric) geo).getValue())) {
				return ExtendedBoolean.TRUE;
			}
		}
		return ExtendedBoolean.FALSE;
	}

	// shift angles to left
	// e.g. [alpha,beta,gamma,delta] -> [beta,gamma,delta,alpha]
	private static GeoElement[] shiftAngles(GeoElement[] angles) {
		GeoElement p = angles[0];
		for (int i = 0; i < angles.length - 1; i++) {
			angles[i] = angles[i + 1];
		}
		angles[angles.length - 1] = p;
		return angles;
	}

	// shift segments to left
	// e.g. [a,b,c,d] -> [b,c,d,a]
	private static GeoSegmentND[] shiftSegments(GeoSegmentND[] segments) {
		GeoSegmentND p = segments[0];
		for (int i = 0; i < segments.length - 1; i++) {
			segments[i] = segments[i + 1];
		}
		segments[segments.length - 1] = p;
		return segments;
	}

	private boolean checkInBothDirection(GeoElementND[] segmentsPoly1,
			GeoElementND[] segmentsPoly2, GeoElement[] anglesPoly1,
			GeoElement[] anglesPoly2) {
		boolean rightDirection = true;
		boolean leftDirection = true;
		// check in right direction
		for (int i = 0; i < segmentsPoly1.length; i++) {
			if (!(ExpressionNodeEvaluator
					.evalEquals(kernel, segmentsPoly1[i], segmentsPoly2[i])
					.getBoolean())) {
				rightDirection = false;
				break;
			}
		}
		// the angles must be checked in same direction
		if (rightDirection) {
			// we have the external angles
			if (((GeoAngle) anglesPoly1[0]).getDouble() >= Math.PI
					&& ((GeoAngle) anglesPoly2[0]).getDouble() >= Math.PI) {
				for (int i = 0; i < anglesPoly1.length; i++) {
					double d1 = 2 * Math.PI
							- ((GeoAngle) anglesPoly1[i]).getDouble();
					double d2 = 2 * Math.PI
							- ((GeoAngle) anglesPoly2[i]).getDouble();
					if (!DoubleUtil.isEqual(d1, d2)) {
						return false;
					}
				}
			}
			// we have the internal angles of first polygon and external
			// angles of second polygon
			else if (((GeoAngle) anglesPoly1[0]).getDouble() < Math.PI
					&& ((GeoAngle) anglesPoly2[0]).getDouble() >= Math.PI) {
				for (int i = 0; i < anglesPoly1.length; i++) {
					double d1 = ((GeoAngle) anglesPoly1[i]).getDouble();
					double d2 = 2 * Math.PI
							- ((GeoAngle) anglesPoly2[i]).getDouble();
					if (!DoubleUtil.isEqual(d1, d2)) {
						return false;
					}
				}
			}
			// we have the external angles of first polygon and internal
			// angles of second polygon
			else if (((GeoAngle) anglesPoly1[0]).getDouble() >= Math.PI
					&& ((GeoAngle) anglesPoly2[0]).getDouble() < Math.PI) {
				for (int i = 0; i < anglesPoly1.length; i++) {
					double d1 = 2 * Math.PI
							- ((GeoAngle) anglesPoly1[i]).getDouble();
					double d2 = ((GeoAngle) anglesPoly2[i]).getDouble();
					if (!DoubleUtil.isEqual(d1, d2)) {
						return false;
					}
				}
			}
			// we have the internal angles
			else {
				for (int i = 0; i < anglesPoly1.length; i++) {
					if (!(ExpressionNodeEvaluator
							.evalEquals(kernel, anglesPoly1[i], anglesPoly2[i])
							.getBoolean())) {
						return false;
					}
				}
			}
			return true;
		}
		// check if segmentsPoly2 is the mirror of segmentsPoly1
		for (int i = segmentsPoly1.length - 1; i >= 0; i--) {
			if (!(ExpressionNodeEvaluator.evalEquals(kernel,
					segmentsPoly2[segmentsPoly2.length - i - 1],
					segmentsPoly1[i]).getBoolean())) {
				leftDirection = false;
				break;
			}
		}
		// the angles must be checked in same direction
		if (leftDirection) {
			if (((GeoAngle) anglesPoly1[0]).getDouble() >= Math.PI
					&& ((GeoAngle) anglesPoly2[0]).getDouble() >= Math.PI) {
				for (int i = anglesPoly2.length - 1; i >= 0; i--) {
					double d1 = 360 * Math.PI / 180
							- ((GeoAngle) anglesPoly1[i]).getDouble();
					double d2 = 360 * Math.PI / 180
							- ((GeoAngle) anglesPoly2[anglesPoly2.length - i
									- 1]).getDouble();
					if (!DoubleUtil.isEqual(d1, d2)) {
						return false;
					}
				}
			}
			// case we have the internal angles
			else if (((GeoAngle) anglesPoly1[0]).getDouble() < Math.PI
					&& ((GeoAngle) anglesPoly2[0]).getDouble() >= Math.PI) {
				for (int i = anglesPoly2.length - 1; i >= 0; i--) {
					double d1 = ((GeoAngle) anglesPoly1[i]).getDouble();
					double d2 = 360 * Math.PI / 180
							- ((GeoAngle) anglesPoly2[anglesPoly2.length - i
									- 1]).getDouble();
					if (!DoubleUtil.isEqual(d1, d2)) {
						return false;
					}
				}
			}
			// case we have the external angles
			else if (((GeoAngle) anglesPoly1[0]).getDouble() >= Math.PI
					&& ((GeoAngle) anglesPoly2[0]).getDouble() < Math.PI) {
				for (int i = anglesPoly2.length - 1; i >= 0; i--) {
					double d1 = 360 * Math.PI / 180
							- ((GeoAngle) anglesPoly1[i]).getDouble();
					double d2 = ((GeoAngle) anglesPoly2[anglesPoly2.length - i
							- 1]).getDouble();
					if (!DoubleUtil.isEqual(d1, d2)) {
						return false;
					}
				}
			} else {
				for (int i = segmentsPoly1.length - 1; i >= 0; i--) {
					if (!(ExpressionNodeEvaluator.evalEquals(kernel,
							anglesPoly2[anglesPoly2.length - i - 1],
							anglesPoly1[i]).getBoolean())) {
						return false;
					}
				}
			}
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
				segments[i].updateVisualStyle(GProperty.VISIBLE);
			}
		}
	}

	@Override
	public void setObjColor(GColor color) {
		super.setObjColor(color);
		if (segments != null && createSegments) {
			for (int i = 0; i < segments.length; i++) {
				segments[i].setObjColor(color);
				segments[i].updateVisualStyle(GProperty.COLOR);
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
	 * @param type
	 *            line type
	 * @param updateSegments
	 *            true to apply this setting to segments
	 */
	public void setLineType(int type, boolean updateSegments) {
		super.setLineType(type);
		if (updateSegments) {
			if (segments != null) {
				for (int i = 0; i < segments.length; i++) {
					segments[i].setLineType(type);
					segments[i].updateVisualStyle(GProperty.LINE_STYLE);
				}
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
	 * @param type
	 *            line type for hidden lines
	 * @param updateSegments
	 *            true to apply this setting to segments
	 */
	public void setLineTypeHidden(int type, boolean updateSegments) {
		super.setLineTypeHidden(type);
		if (updateSegments) {
			if (segments != null) {
				for (int i = 0; i < segments.length; i++) {
					segments[i].setLineTypeHidden(type);
					segments[i].updateVisualStyle(GProperty.LINE_STYLE);
				}
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
	 * @param th
	 *            new thickness
	 * @param updateSegments
	 *            true to apply this setting to segments as well
	 */
	public void setLineThickness(int th, boolean updateSegments) {
		super.setLineThickness(th);

		if (updateSegments) {
			if (segments != null) {
				for (int i = 0; i < segments.length; i++) {
					segments[i].setLineThickness(th);
					segments[i].updateVisualStyle(GProperty.LINE_STYLE);
				}
			}
		}
	}

	@Override
	public void setLineThicknessOrVisibility(int th) {
		super.setLineThickness(th);

		if (segments != null) {
			for (int i = 0; i < segments.length; i++) {
				((GeoElement) segments[i]).setLineThicknessOrVisibility(th);
				segments[i].updateVisualStyle(GProperty.COMBINED);
			}
		}
	}

	@Override
	final public String toString(StringTemplate tpl) {
		return label + " = " + kernel.format(getArea(), tpl);
	}

	@Override
	final public String toStringMinimal(StringTemplate tpl) {
		return regrFormat(getArea());
	}

	@Override
	final public String toValueString(StringTemplate tpl) {
		return kernel.format(getArea(), tpl);
	}

	/**
	 * interface NumberValue
	 */
	@Override
	public MyDouble getNumber() {
		return new MyDouble(kernel, getArea());
	}

	@Override
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

	@Override
	public PathMover createPathMover() {
		return new PathMoverGeneric(this);
	}

	@Override
	public double getMaxParameter() {
		return getPointsLength();
	}

	@Override
	public double getMinParameter() {
		return 0;
	}

	@Override
	public boolean isClosedPath() {
		return true;
	}

	@Override
	public boolean isOnPath(GeoPointND PI, double eps) {
		GeoPoint P = (GeoPoint) PI;

		if (P.getPath() == this) {
			return true;
		}

		// check if P is on one of the segments
		for (int i = 0; i < segments.length; i++) {
			if (segments[i].isOnPath(P, eps)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * @param coords
	 *            coordiantes
	 * @param eps
	 *            precision
	 * @return whether given point is on boundary of the polygon within given
	 *         precision
	 */
	public boolean isOnPath(Coords coords, double eps) {
		// check if P is on one of the segments
		for (int i = 0; i < segments.length; i++) {
			if (segments[i].isOnPath(coords, eps)) {
				return true;
			}
		}
		return false;
	}

	@Override
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
		if (pp.t < 0) {
			pp.t += segments.length;
		}
		int index = (int) Math.floor(pp.t);
		GeoSegmentND seg = segments[index];
		double segParameter = pp.t - index;

		// calc point for given parameter
		PI.setCoords2D(seg.getPointX(segParameter), seg.getPointY(segParameter),
				1);
	}

	@Override
	final public void pointChanged(GeoPointND PI) {
		PI.pointChanged(this);
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
	 * @param PI
	 *            point
	 * @return true if PI is in this polygon
	 */
	@Override
	public boolean isInRegion(GeoPointND PI) {
		Coords coords = PI.getCoordsInD2();
		return isInRegion(coords.getX() / coords.getZ(),
				coords.getY() / coords.getZ());

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
	@Override
	public boolean isInRegion(double x0, double y0) {
		return isInRegion(x0, y0, getPoints());
	}

	/**
	 * says if the point (x0,y0) is in the region defined by vertices
	 * 
	 * @param x0
	 *            x-coord of the point
	 * @param y0
	 *            y-coord of the point
	 * @param pts
	 *            vertices
	 * @return true if the point (x0,y0) is in the region
	 */
	public static boolean isInRegion(double x0, double y0, GeoPointND[] pts) {
		double x1, y1, x2, y2;
		int numPoints = pts.length;
		x1 = pts[numPoints - 1].getInhomX() - x0;
		y1 = pts[numPoints - 1].getInhomY() - y0;

		boolean ret = false;
		for (int i = 0; i < numPoints; i++) {
			x2 = pts[i].getInhomX() - x0;
			y2 = pts[i].getInhomY() - y0;
			int inter = intersectOx(x1, y1, x2, y2);
			if (inter == 2) {
				return true; // point on an edge
			}
			ret = ret ^ (inter == 1);
			x1 = x2;
			y1 = y2;
		}

		return ret;
	}

	@Override
	final public void regionChanged(GeoPointND P) {
		// if kernel doesn't use path/region parameters, do as if point changed
		// its coords
		if (!getKernel().usePathAndRegionParameters(P)
				|| P.getRegionParameters().isNaN()) {
			pointChangedForRegion(P);
			return;
		}

		RegionParameters rp = P.getRegionParameters();

		if (rp.isOnPath()) {
			pathChanged(P);
		} else {
			// Application.debug(rp.getT1()+ "," + rp.getT2());
			// pointChangedForRegion(P);
			double xu = p1.inhomX - p0.inhomX;
			double yu = p1.inhomY - p0.inhomY;
			double xv = p2.inhomX - p0.inhomX;
			double yv = p2.inhomY - p0.inhomY;

			setRegionChanged(P, p0.inhomX + rp.getT1() * xu + rp.getT2() * xv,
					p0.inhomY + rp.getT1() * yu + rp.getT2() * yv);

			if (!isInRegion(P)) {
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
		PI.setRegionChanged(x, y);
	}

	@Override
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
	 * @param newp0
	 *            new 1st point for region coords
	 * @param newp1
	 *            new 2nd point for region coords
	 * @param newp2
	 *            new 3rd point for region coords
	 */
	final public void updateRegionCS(GeoPoint newp0, GeoPoint newp1,
			GeoPoint newp2) {
		this.p0 = newp0;
		this.p1 = newp1;
		this.p2 = newp2;
		numCS = 3;

	}

	/**
	 * update the region coord sys with the 3 first points
	 */
	final public void updateRegionCSWithFirstPoints() {
		updateRegionCS(getPoint(0), getPoint(1), getPoint(2));
	}

	/**
	 * update the coord sys used for region parameters
	 */
	final public void updateRegionCS() {
		if (getPoints() == null) {
			return;
		}

		if (p2 != null && !GeoPoint.collinear(p0, p1, p2)) {
			numCS = 3;
			return;
		}

		p0 = getPoint(0);
		numCS = 1;
		// Log.debug(" p0 = " + p0.inhomX + "," + p0.inhomY);

		int secondPoint = -1;
		boolean secondPointFound = false;
		for (secondPoint = 1; secondPoint < getPoints().length
				&& !secondPointFound; secondPoint++) {
			p1 = getPoint(secondPoint);
			if (!DoubleUtil.isEqual(p0.inhomX, p1.inhomX,
					Kernel.STANDARD_PRECISION)) {
				secondPointFound = true;
			} else if (!DoubleUtil.isEqual(p0.inhomY, p1.inhomY,
					Kernel.STANDARD_PRECISION)) {
				secondPointFound = true;
			// Log.debug(" secondPointFound = " + secondPointFound);
			}
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
		// Application.debug(" secondPoint = "+secondPoint+"\n thirdPoint =
		// "+thirdPoint);

		// Log.debug("numCS = " + numCS);

	}

	/**
	 * returns 1 if the segment ((x1,y1),(x2,y2)) intersects y=0 for x>0, 2 if
	 * (0,0) is on the segment and -1 otherwise If the segment only touches the
	 * line for x>0, this touch is counted only if the segment is in y>0.
	 * 
	 * Segments lying entirely on y=0 are ignored, unless they go through (0,0).
	 */
	private static int intersectOx(double px1, double py1, double px2,
			double py2) {
		double x1 = px1, x2 = px2, y1 = py1, y2 = py2;
		double eps = Kernel.STANDARD_PRECISION;

		if (DoubleUtil.isZero(y1)) { // first point on (Ox)
			if (DoubleUtil.isZero(y2)) { // second point on (Ox)
				if (DoubleUtil.isGreaterEqual(0, x1 * x2)) {
					return 2;
				}
				// ignore the segment on 0x if it is whole on left or right
				return -1;
			}
			// only first point is on (Ox)
			if (DoubleUtil.isZero(x1)) {
				return 2;
			}
			return y2 > eps && x1 > eps ? 1 : -1;
		} else if (DoubleUtil.isZero(y2)) {
			// only second point is on (0x)
			if (DoubleUtil.isZero(x2)) {
				return 2;
			}
			return y1 > eps && x2 > eps ? 1 : -1;
		} else if (y1 * y2 > eps) {
			return -1;
		} else {
			if (y1 > y2) { // first point under (Ox)
				double y = y1;
				y1 = y2;
				y2 = y;
				double x = x1;
				x1 = x2;
				x2 = x;
			}

			if ((x1 + eps < 0) && (x2 + eps < 0)) {
				return -1;
			} else if ((x1 > eps) && (x2 > eps)) {
				return 1;
			} else if (x1 * y2 > x2 * y1 + eps) {
				return 1;
			} else if (x1 * y2 + eps < x2 * y1) {
				return -1;
			}
			else {
				return 2; // angle ~ 0
			}
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
		getXMLisShapeTag(sb);
		getAuxiliaryXML(sb);
		getBreakpointXML(sb);
		getScriptTags(sb);
		getMaskXML(sb);
	}

	private void getMaskXML(final StringBuilder sb) {
		if (!isMask) {
			return;
		}

		sb.append("\t<isMask val=\"true\"/>\n");
	}

	/**
	 * @return minimum line thickness (normally 1, but 0 for polygons, integrals
	 *         etc)
	 */
	@Override
	public int getMinimumLineThickness() {
		return 0;
	}

	@Override
	public boolean isTraceable() {
		return traceable;
	}

	/**
	 * Set whether this object is traceable.
	 *
	 * @param traceable true to set object to traceable, false otherwise.
	 */
	public void setTraceable(boolean traceable) {
		this.traceable = traceable;
	}

	@Override
	public void setTrace(boolean trace) {
		this.trace = trace;
	}

	@Override
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
		// return (getMetasLength() > 0);
	}

	/**
	 * If this is a convex polygon. Also updates the convexOrientation value (even for a triangle)
	 * 
	 * @return if this is a convex polygon
	 */
	public boolean isConvex() {
		/*
		 * if (getPointsLength() <= 3){ return true; }
		 */

		// remove same successive points
		ArrayList<Double> xList = new ArrayList<>();
		ArrayList<Double> yList = new ArrayList<>();
		double x0 = getPointX(0);
		double y0 = getPointY(0);
		xList.add(x0);
		yList.add(y0);
		for (int i = 1; i < getPointsLength(); i++) {
			double x1 = getPointX(i);
			double y1 = getPointY(i);
			if (!DoubleUtil.isEqual(x0, x1) || !DoubleUtil.isEqual(y0, y1)) {
				xList.add(x1);
				yList.add(y1);
				x0 = x1;
				y0 = y1;
			}
		}

		int n = xList.size();

		// remove last point if equals first points
		if (DoubleUtil.isEqual(xList.get(0), xList.get(n - 1))
				&& DoubleUtil.isEqual(yList.get(0), yList.get(n - 1))) {
			/*
			 * if (n==4){ return true; }
			 */
			xList.remove(n - 1);
			yList.remove(n - 1);
			n--;
		}

		// check orientations
		boolean answer = true;
		boolean hasAngle360 = false;

		double x1 = xList.get(n - 1);
		double y1 = yList.get(n - 1);
		double dx1 = x1 - xList.get(n - 2);
		double dy1 = y1 - yList.get(n - 2);

		double x2 = xList.get(0);
		double y2 = yList.get(0);
		double dx2 = x2 - x1;
		double dy2 = y2 - y1;

		// calc first orientation
		int orientation = DoubleUtil.compare(dy1 * dx2, dx1 * dy2);
		if (orientation == 0) {
			if (DoubleUtil.isGreater(0, dx1 * dx2 + dy1 * dy2)) { // U-turn
				answer = false;
			}
		}

		int i = 1;
		while (answer && (i < n)) {
			dx1 = dx2;
			dy1 = dy2;
			x1 = x2;
			y1 = y2;
			x2 = xList.get(i);
			y2 = yList.get(i);
			dx2 = x2 - x1;
			dy2 = y2 - y1;
			int orientation2 = DoubleUtil.compare(dy1 * dx2, dx1 * dy2);
			// Log.debug(""+answer+","+hasAngle360);
			// Log.debug("i : "+i+" -- orientations :
			// "+orientation+","+orientation2);

			if (!hasAngle360 && orientation2 == 0) { // U-turn
				if (DoubleUtil.isGreater(0, dx1 * dx2 + dy1 * dy2)) {
					answer = false;
				}
			}

			if (answer) {
				if (orientation == 0) { // no orientation for now
					orientation = orientation2;
				} else {
					if (orientation2 != 0 && orientation2 != orientation) {
						answer = false; // stop here
					}
				}
			}
			i++;
		}

		if (answer) {
			convexOrientation = orientation;
		}

		return answer;
	}

	/**
	 * 
	 * @return true if points orientation are not the same as xOy plane (only
	 *         used in 2D)
	 */
	public boolean isConvexInverseDirection() {
		// Log.debug(""+convexOrientation);

		return (convexOrientation > 0);
	}

	/*
	 * private class SweepComparator implements Comparator<Integer>{
	 * 
	 * private GeoPolygon p;
	 * 
	 * /** constructor
	 * 
	 * @param p polygon
	 *
	 * public SweepComparator(GeoPolygon p) { this.p = p; }
	 * 
	 * public int compare(Integer i1, Integer i2) {
	 * 
	 * // smallest x double x1 = p.getPointX(i1); double x2 = p.getPointX(i2);
	 * if (Kernel.isGreater(x2, x1)){ return -1; } if (Kernel.isGreater(x1,
	 * x2)){ return 1; }
	 * 
	 * // then smallest y double y1 = p.getPointY(i1); double y2 =
	 * p.getPointY(i2); if (Kernel.isGreater(y2, y1)){ return -1; } if
	 * (Kernel.isGreater(y1, y2)){ return 1; }
	 * 
	 * // then smallest index if (i1 < i2){ return -1; } if (i1 > i2){ return 1;
	 * }
	 * 
	 * // same point return 0; }
	 * 
	 * 
	 * }
	 */

	@Override
	public boolean hasDrawable3D() {
		return true;
	}

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

		if (labelPosition == null) {
			labelPosition = new Coords(x / getPointsLength(),
					y / getPointsLength(), z / getPointsLength(), 1);
		} else {
			labelPosition.setX(x / getPointsLength());
			labelPosition.setY(y / getPointsLength());
			labelPosition.setZ(z / getPointsLength());
		}
		return labelPosition;
	}

	// //////////////////////////////////////
	// GEOCOORDSYS2D INTERFACE
	// //////////////////////////////////////

	@Override
	public CoordSys getCoordSys() {
		return CoordSys.Identity3D;
	}

	@Override
	public Coords getPoint(double x2d, double y2d, Coords coords) {
		return getCoordSys().getPoint(x2d, y2d, coords);
	}

	@Override
	public Coords[] getNormalProjection(Coords coords) {
		return getCoordSys().getNormalProjection(coords);
	}

	@Override
	public Coords[] getProjection(Coords oldCoords, Coords willingCoords,
			Coords willingDirection) {

		Coords[] result = new Coords[] { new Coords(4), new Coords(4) };
		willingCoords.projectPlaneThruVIfPossible(
				getCoordSys().getMatrixOrthonormal(), oldCoords,
				willingDirection, result[0], result[1]);

		return result;
	}

	// ////////////////////////////////////////////////////
	// PARENT NUMBER (HEIGHT OF A PRISM, ...)
	// ////////////////////////////////////////////////////

	/**
	 * @param cp
	 *            changeable parent
	 * 
	 */
	final public void setChangeableParent(ChangeableParent cp) {
		changeableParent = cp;
	}

	@Override
	public boolean hasChangeableParent3D() {
		return changeableParent != null;
	}

	@Override
	public ChangeableParent getChangeableParent3D() {
		return changeableParent;
	}

	@Override
	public void rotate(NumberValue r) {
		for (int i = 0; i < getPointsLength(); i++) {
			getPoint(i).rotate(r);
		}
		updatePathRegion();
	}

	@Override
	public void rotate(NumberValue r, GeoPointND S) {
		Coords Scoords = S.getInhomCoords();
		for (int i = 0; i < getPointsLength(); i++) {
			getPoint(i).rotate(r, Scoords);
		}
		updatePathRegion();
	}

	@Override
	public void matrixTransform(double a00, double a01, double a10,
			double a11) {
		for (int i = 0; i < getPointsLength(); i++) {
			getPoint(i).matrixTransform(a00, a01, a10, a11);
		}

		calcArea();

		updatePathRegion();
	}

	@Override
	public void translate(Coords v) {
		for (int i = 0; i < getPointsLength(); i++) {
			getPoint(i).translate(v);
		}
		updatePathRegion();
	}

	@Override
	public boolean isTranslateable() {
		return true;
	}

	@Override
	public void dilate(NumberValue r, Coords S) {
		for (int i = 0; i < getPointsLength(); i++) {
			getPoint(i).dilate(r, S);
		}

		calcArea();

		updatePathRegion();
	}

	@Override
	public void mirror(Coords Q) {
		// important for centroid calculation
		area *= -1;

		for (int i = 0; i < getPointsLength(); i++) {
			getPoint(i).mirror(Q);
		}
		updatePathRegion();
	}

	private void updatePathRegion() {
		updateRegionCS();
		this.updateSegments(cons);
	}

	@Override
	public void mirror(GeoLineND g) {
		// important for centroid calculation
		area *= -1;

		for (int i = 0; i < getPointsLength(); i++) {
			getPoint(i).mirror(g);
		}
		updatePathRegion();
	}

	/**
	 * Returns true iff all vertices are labeled
	 * 
	 * @return true iff all vertices are labeled
	 */
	@Override
	public boolean isAllVertexLabelsSet() {
		for (int i = 0; i < getPointsLength(); i++) {
			if (!getPoint(i).isLabelSet()) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Returns true iff number of vertices is not volatile
	 * 
	 * @return true iff number of vertices is not volatile
	 */
	@Override
	public boolean isVertexCountFixed() {
		// regularPolygon[vertex,vertex,count]
		if (getParentAlgorithm() instanceof AlgoPolygonRegularND) {
			return false;
		}
		// polygon[list]
		if (getParentAlgorithm() == null
				|| getParentAlgorithm().getInput().length < 3) {
			return false;
		}
		return true;
	}

	@Override
	public Coords getDirectionInD3() {

		return Coords.VZ;
	}

	@Override
	public void matrixTransform(double a00, double a01, double a02, double a10,
			double a11, double a12, double a20, double a21, double a22) {

		for (int i = 0; i < getPointsLength(); i++) {
			((MatrixTransformable) getPointND(i)).matrixTransform(a00, a01, a02,
					a10, a11, a12, a20, a21, a22);
		}

		calcArea();

		updatePathRegion();

	}

	/**
	 * Update area from vertices
	 */
	public void calcArea() {
		// eg Dilate[Polygon[(0,0),(1,1),(1,0)],4]
		if (algoParent instanceof AlgoTransformation) {
			AlgoTransformation algo = (AlgoTransformation) algoParent;

			double sf = algo.getAreaScaleFactor();

			GeoPolygon input = (GeoPolygon) algo.getInput(
					algoParent.getClassName() == Commands.ApplyMatrix ? 1 : 0);
			setArea(input.getAreaWithSign() * sf);

			return;

		}

		if (algoParent instanceof PolygonAlgo) {
			((PolygonAlgo) algoParent).calcArea();
		}

		// eg
		// IntersectRegion[Polygon[(1,1),(0,1),(1,0)],Polygon[(1,1),(0,1),(1,0)]]
		// eg Union[Polygon[(1,1),(0,1),(1,0)],Polygon[(1,1),(0,1),(1,0)]]
		setArea(AlgoPolygon.calcAreaWithSign(getPoints()));
	}

	/**
	 * Updates given point's coordinates to centroid of this
	 * 
	 * @param p
	 *            centroid
	 */
	public void calcCentroid(GeoPointND p) {
		if (algoParent instanceof PolygonAlgo) {
			((PolygonAlgo) algoParent).calcCentroid((GeoPoint) p);
			return;
		}

		// just do long method
		// could improve by transforming original centroid, but not worth doing
		// test-case Centroid[Dilate[Polygon[(0,0),(1,1),(1,0)],4]]
		// test-case Centroid[Polygon[(0,0),(1,1),(1,0)]]
		if (tmp3 == null) {
			tmp3 = new double[3];
		}
		AlgoPolygon.calcCentroid(tmp3, area, getPoints());
		if (Double.isNaN(tmp3[0])) {
			p.setUndefined();
		} else {
			p.setCoords(tmp3[0], tmp3[1], tmp3[2]);
		}
	}

	@Override
	public void toGeoCurveCartesian(GeoCurveCartesianND curve) {
		if (!isDefined()) {
			curve.setUndefined();
			return;
		}
		curve.setFromPolyLine(points, true);
	}

	@Override
	public String getDefaultLabel() {
		int counter = 0;
		String str;
		String name;
		if (getMetasLength() == 1) {
			name = getLoc().getPlainLabel("face", "face"); // Name.face
		} else {
			if (points != null && points.length == 3) {
				name = getLoc().getPlainLabel("triangle", "t"); // Name.triangle
			} else if (points != null && points.length == 4) {
				name = getLoc().getPlainLabel("quadrilateral", "q"); // Name.quadrilateral
			} else {
				name = getLoc().getPlainLabel("polygon", "poly");
			}

		}
		do {
			counter++;
			str = name + kernel.internationalizeDigits(counter + "",
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

	@Override
	public PVariable[] getBotanaVars(GeoElementND geo)
			throws NoSymbolicParametersException {
		if (algoParent instanceof SymbolicParametersBotanaAlgo) {
			return ((SymbolicParametersBotanaAlgo) algoParent)
					.getBotanaVars(this);
		}
		return null; // Here maybe an exception should be thrown...?
	}

	@Override
	public PPolynomial[] getBotanaPolynomials(GeoElementND geo)
			throws NoSymbolicParametersException {
		if (algoParent instanceof SymbolicParametersBotanaAlgo) {
			return ((SymbolicParametersBotanaAlgo) algoParent)
					.getBotanaPolynomials(this);
		}
		return null; // Here maybe an exception should be thrown...?
	}

	@Override
	public int getMetasLength() {
		if (metas == null) {
			return 0;
		}

		return metas.size();
	}

	@Override
	public GeoElement[] getMetas() {
		GeoElement[] ret = new GeoElement[metas.size()];
		metas.toArray(ret);
		return ret;
	}

	/**
	 * add the polyhedron has meta geo for this (e.g. parent polyhedron, or
	 * linked polyhedron)
	 * 
	 * @param polyhedron
	 *            polyhedron
	 */
	public void addMeta(GeoElement polyhedron) {
		if (metas == null) {
			metas = new TreeSet<>();
		}

		metas.add(polyhedron);
	}

	/**
	 * remove polyhedron as meta for this
	 * 
	 * @param polyhedron
	 *            polyhedron
	 */
	public void removeMeta(GeoElement polyhedron) {
		if (metas != null) {
			metas.remove(polyhedron);
		}
	}

	@Override
	public double distance(final GeoPoint p) {
		double d = Double.POSITIVE_INFINITY;
		for (GeoSegmentND seg : getSegments()) {
			double d1 = seg.distance(p);
			if (d1 < d) {
				d = d1;
			}
		}
		return d;
	}

	///////////////////////////////////
	// REVERSE ORIENTATION FOR DRAWING
	///////////////////////////////////

	/**
	 * set that normal should be reversed for 3D drawing
	 * 
	 * @param flag
	 *            flag
	 */
	public void setReverseNormalForDrawing(boolean flag) {
		reverseNormalForDrawing = flag;
	}

	/**
	 * @return if normal should be reversed for 3D drawing
	 */
	public boolean getReverseNormalForDrawing() {
		return reverseNormalForDrawing;
	}

	/**
	 * Sets the point size (and/or visibility)
	 * 
	 * @param size
	 *            new point size
	 */
	public void setPointSizeOrVisibility(int size) {
		if (size > 0) {
			setPointSize(size);
		} else {
			setPointNotVisibile();
		}
	}

	private void setPointSize(int size) {
		for (GeoPointND point : points) {
			point.setEuclidianVisibleIfNoConditionToShowObject(true);
			point.setPointSize(size);
			point.updateRepaint();
		}
	}

	private void setPointNotVisibile() {
		for (GeoPointND point : points) {
			point.setEuclidianVisibleIfNoConditionToShowObject(false);
			point.updateRepaint();
		}
	}

	/**
	 * 
	 * @param cons1
	 *            construction
	 * @return 2D point if 2D polygon, 3D point if 3D polygon
	 */
	public GeoPointND newGeoPoint(Construction cons1) {
		return new GeoPoint(cons1);
	}

	@Override
	public int getNumPoints() {
		return points == null ? 0 : points.length;
	}

	@Override
	public ValueType getValueType() {
		return ValueType.NUMBER;
	}

	/**
	 * 
	 * @return polygon triangulation created for this
	 */
	public PolygonTriangulation getPolygonTriangulation() {
		if (pt == null) {
			pt = new PolygonTriangulation();
			pt.setPolygon(this);
		}
		return pt;
	}

	@Override
	public void setVisualStyle(final GeoElement geo,
			boolean setAuxiliaryProperty) {
		super.setVisualStyle(geo, setAuxiliaryProperty);

		if (segments == null) {
			return;
		}

		for (GeoSegmentND segment : segments) {
			segment.setObjColor(geo.getObjectColor());
			segment.updateVisualStyle(GProperty.COLOR);
		}
	}

	@Override
	public boolean isShape() {
		return isShape;
	}

	@Override
	public boolean isMask() {
		return isMask;
	}

	/**
	 * @param isShape
	 *            - true, if geo was created with shape tool
	 */
	@Override
	public void setIsShape(boolean isShape) {
		this.isShape = isShape;
	}

	@Override
	public void setIsMask(boolean isMask) {
		this.isMask = true;
		if (isMask) {
			setMaskPreferences();
		}
	}

	private void setMaskPreferences() {
		this.isShape = true;
		setLabelVisible(false);
		setAlphaValue(1);
		setLineThickness(1);
		setShowLineProperties(false);
		setFillable(false);
		setTraceable(false);
	}

	/**
	 * 
	 * @return true if normal is reversed
	 */
	public boolean hasReverseNormal() {
		return false;
	}

	/**
	 * 
	 * @return true if is a regular polygon
	 */
	public boolean isRegular() {
		if (!isDefined()) {
			return false;
		}
		GeoPointND[] lPoints = getPoints();
		if (lPoints.length < 3) {
			return false;
		}
		// two points
		GeoPoint pA = (GeoPoint) lPoints[lPoints.length - 2];
		double xA = pA.inhomX;
		double yA = pA.inhomY;
		GeoPoint pB = (GeoPoint) lPoints[lPoints.length - 1];
		double xB = pB.inhomX;
		double yB = pB.inhomY;
		double dx0 = xB - xA;
		double dy0 = yB - yA;
		// third point
		xA = xB;
		yA = yB;
		pB = (GeoPoint) lPoints[0];
		xB = pB.inhomX;
		yB = pB.inhomY;
		double dx1 = xB - xA;
		double dy1 = yB - yA;
		// distance and angle
		double sqrDist = dx1 * dx1 + dy1 * dy1;
		boolean isDegenerated = DoubleUtil.isZero(sqrDist);
		double dot = dx0 * dx1 + dy0 * dy1;
		double det = dx0 * dy1 - dx1 * dy0;
		// other points
		for (int i = 1; i < lPoints.length; i++) {
			dx0 = dx1;
			dy0 = dy1;
			xA = xB;
			yA = yB;
			pB = (GeoPoint) lPoints[i];
			xB = pB.inhomX;
			yB = pB.inhomY;
			dx1 = xB - xA;
			dy1 = yB - yA;
			if (!DoubleUtil.isEqual(dx1 * dx1 + dy1 * dy1, sqrDist)) {
				// not the same distance
				return false;
			}
			if (!isDegenerated) {
				if (!DoubleUtil.isEqual(dx0 * dx1 + dy0 * dy1, dot)) {
					// not the same angle
					return false;
				}
				if (!DoubleUtil.isEqual(dx0 * dy1 - dx1 * dy0, det)) {
					// not the same orientation
					return false;
				}
			}
		}
		if (!isDegenerated) {
			double angle = Math.PI - Math.acos(dot / sqrDist);
			if (!DoubleUtil.isEqual(angle * lPoints.length,
					Math.PI * (lPoints.length - 2))) {
				// e.g. star polygon
				return false;
			}
		}
		return true;
	}

	@Override
	public boolean showLineProperties() {
		return showLineProperties && super.showLineProperties();
	}

	/**
	 * Set whether this object should show line properties.
	 *
	 * @param showLineProperties true if it should show line properties
	 */
	public void setShowLineProperties(boolean showLineProperties) {
		this.showLineProperties = showLineProperties;
	}

	/**
	 * Used for synchronizing polygons in old notes files with current
	 * (no labeled edges) polygons
	 */
	public void hideSegments() {
		if (getSegments() != null) {
			for (GeoSegmentND segment : getSegments()) {
				segment.setEuclidianVisible(false);
			}
		}
		setInitLabelsCalled(false);
	}
}
