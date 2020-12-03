/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package org.geogebra.common.kernel.geos;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.MyPoint;
import org.geogebra.common.kernel.PathMover;
import org.geogebra.common.kernel.PathMoverGeneric;
import org.geogebra.common.kernel.PathParameter;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.Transform;
import org.geogebra.common.kernel.algos.AlgoJoinPointsSegment;
import org.geogebra.common.kernel.arithmetic.MyDouble;
import org.geogebra.common.kernel.arithmetic.NumberValue;
import org.geogebra.common.kernel.kernelND.GeoCurveCartesianND;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.kernel.kernelND.GeoLineND;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.kernel.kernelND.GeoSegmentND;
import org.geogebra.common.kernel.matrix.Coords;
import org.geogebra.common.plugin.GeoClass;
import org.geogebra.common.util.DoubleUtil;
import org.geogebra.common.util.ExtendedBoolean;

/**
 * @author Markus Hohenwarter
 */
final public class GeoSegment extends GeoLine
		implements GeoSegmentND, SegmentProperties {

	// GeoSegment is constructed by AlgoJoinPointsSegment
	// private GeoPoint A, B;
	private double length;
	private boolean defined;
	private boolean allowOutlyingIntersections = false;
	private boolean keepTypeOnGeometricTransform = true; // for mirroring,
															// rotation, ...
	private boolean isShape = false;
	private StringBuilder sbToString = new StringBuilder(30);

	private boolean forceSimpleTransform;

	private Coords pnt2D;

	private GeoElement meta = null;

	/** no decoration */
	public static final int SEGMENT_DECORATION_NONE = 0;
	/** one tick */
	public static final int SEGMENT_DECORATION_ONE_TICK = 1;
	/** two ticks */
	public static final int SEGMENT_DECORATION_TWO_TICKS = 2;
	/** three ticks */
	public static final int SEGMENT_DECORATION_THREE_TICKS = 3;
	/** one arrow */
	public static final int SEGMENT_DECORATION_ONE_ARROW = 4;
	/** two arrows */
	public static final int SEGMENT_DECORATION_TWO_ARROWS = 5;
	/** three arrows */
	public static final int SEGMENT_DECORATION_THREE_ARROWS = 6;

	/**
	 * Returns array of all decoration types
	 * 
	 * @see #SEGMENT_DECORATION_ONE_TICK etc.
	 * @return array of all decoration types
	 */
	public static Integer[] getDecoTypes() {
		Integer[] ret = { Integer.valueOf(SEGMENT_DECORATION_NONE),
				Integer.valueOf(SEGMENT_DECORATION_ONE_TICK),
				Integer.valueOf(SEGMENT_DECORATION_TWO_TICKS),
				Integer.valueOf(SEGMENT_DECORATION_THREE_TICKS),
				Integer.valueOf(SEGMENT_DECORATION_ONE_ARROW),
				Integer.valueOf(SEGMENT_DECORATION_TWO_ARROWS),
				Integer.valueOf(SEGMENT_DECORATION_THREE_ARROWS) };
		return ret;
	}

	@Override
	public void setDecorationType(int type) {
		setDecorationType(type, getDecoTypes().length);
	}

	/**
	 * Creates new segment
	 * 
	 * @param c
	 *            construction
	 * @param A
	 *            first endpoint
	 * @param B
	 *            second endpoint
	 */
	public GeoSegment(Construction c, GeoPoint A, GeoPoint B) {
		this(c);
		setPoints(A, B);
	}

	/**
	 * common constructor
	 * 
	 * @param c
	 *            construction
	 */
	public GeoSegment(Construction c) {
		super(c);
		setConstructionDefaults();
	}

	/**
	 * sets start and end points
	 * 
	 * @param A
	 *            start point
	 * @param B
	 *            end point
	 */
	public void setPoints(GeoPoint A, GeoPoint B) {
		setStartPoint(A);
		setEndPoint(B);
	}

	@Override
	public void setTwoPointsInhomCoords(Coords start, Coords end) {
		this.startPoint.setCoords(start.get(1), start.get(2), 1);
		this.endPoint.setCoords(end.get(1), end.get(2), 1);
		// set x, y, z coords for equation
		setCoords(start.getY() - end.getY(), end.getX() - start.getX(),
				start.getX() * end.getY() - start.getY() * end.getX());
		setPoints(this.startPoint, this.endPoint);
		calcLength();
	}

	@Override
	public GeoClass getGeoClassType() {
		return GeoClass.SEGMENT;
	}

	/**
	 * the copy of a segment is a number (!) with its value set to the segments
	 * current length
	 *
	 * public GeoElement copy() { return new GeoNumeric(cons, getLength()); }
	 */

	@Override
	public GeoElement copyInternal(Construction cons1) {

		GeoSegment seg;

		if (!this.isDefined()) {
			seg = new GeoSegment(cons1);

		} else {
			seg = new GeoSegment(cons1,
					(GeoPoint) startPoint.copyInternal(cons1),
					(GeoPoint) endPoint.copyInternal(cons1));
		}

		seg.set(this);
		return seg;
	}

	@Override
	public void set(GeoElementND geo) {
		super.set(geo);
		if (!geo.isGeoSegment()) {
			return;
		}

		GeoSegment seg = (GeoSegment) geo;
		length = seg.length;
		defined = seg.defined;
		keepTypeOnGeometricTransform = seg.keepTypeOnGeometricTransform;

		startPoint = (GeoPoint) GeoLine.updatePoint(cons, startPoint,
				seg.startPoint);

		endPoint = (GeoPoint) GeoLine.updatePoint(cons, endPoint, seg.endPoint);

	}

	/**
	 * @param s
	 *            start point
	 * @param e
	 *            end point
	 * @param line
	 *            line
	 */
	public void set(GeoPoint s, GeoPoint e, GeoVec3D line) {
		super.set(line);

		setStartPoint(s);
		setEndPoint(e);
		calcLength();
	}

	@Override
	public void setVisualStyle(GeoElement geo, boolean setAuxiliaryProperty) {
		super.setVisualStyle(geo, setAuxiliaryProperty);

		if (geo.isGeoSegment()) {
			GeoSegmentND seg = (GeoSegmentND) geo;
			allowOutlyingIntersections = seg.allowOutlyingIntersections();
			isShape = isShape || geo.isShape();
		}
	}

	/**
	 * Calculates this segment's length . This method should only be called by
	 * its parent algorithm of type AlgoJoinPointsSegment
	 */
	public void calcLength() {
		defined = startPoint.isFinite() && endPoint.isFinite();
		if (defined) {
			length = startPoint.distance(endPoint);

			if (DoubleUtil.isZero(length)) {
				length = 0;
			}
		} else {
			length = Double.NaN;
		}
	}

	@Override
	public double getLength() {
		return length;
	}

	/*
	 * overwrite GeoLine methods
	 */
	@Override
	public boolean isDefined() {
		return defined;
	}

	@Override
	public void setUndefined() {
		super.setUndefined();
		length = Double.NaN;
		defined = false;
	}

	@Override
	public boolean showInEuclidianView() {
		// segments of polygons can have thickness 0
		return defined && getLineThickness() != 0;
	}

	/**
	 * Yields true iff startpoint and endpoint of s are equal to startpoint and
	 * endpoint of this segment.
	 */
	// Michael Borcherds 2008-05-01
	@Override
	public boolean isEqual(GeoElementND geo) {
		// test 3D is geo is 3D
		if (geo.isGeoElement3D()) {
			return geo.isEqual(this);
		}
		if (!geo.isGeoSegment()) {
			return false;
		}
		GeoSegmentND s = (GeoSegmentND) geo;
		return ((startPoint.isEqualPointND(s.getStartPoint())
				&& endPoint.isEqualPointND(s.getEndPoint()))
				|| (startPoint.isEqualPointND(s.getEndPoint())
						&& endPoint.isEqualPointND(s.getStartPoint())));
	}

	@Override
	public String toString(StringTemplate tpl) {
		sbToString.setLength(0);
		sbToString.append(label);
		sbToString.append(" = ");
		sbToString.append(kernel.format(length, tpl));
		return sbToString.toString();
	}

	@Override
	public String toStringMinimal(StringTemplate tpl) {
		sbToString.setLength(0);
		sbToString.append(regrFormat(length));
		return sbToString.toString();
	}

	@Override
	public String toValueString(StringTemplate tpl) {
		return kernel.format(length, tpl);
	}

	/**
	 * interface NumberValue
	 */
	@Override
	public MyDouble getNumber() {
		return new MyDouble(kernel, getLength());
	}

	@Override
	public double getDouble() {
		return getLength();
	}

	@Override
	public boolean isNumberValue() {
		return true;
	}

	@Override
	public boolean allowOutlyingIntersections() {
		return allowOutlyingIntersections;
	}

	@Override
	public void setAllowOutlyingIntersections(boolean flag) {
		allowOutlyingIntersections = flag;
	}

	@Override
	public boolean keepsTypeOnGeometricTransform() {
		return keepTypeOnGeometricTransform;
	}

	@Override
	public void setKeepTypeOnGeometricTransform(boolean flag) {
		keepTypeOnGeometricTransform = flag;
	}

	@Override
	public boolean isLimitedPath() {
		return true;
	}

	@Override
	public boolean isIntersectionPointIncident(GeoPoint p, double eps) {
		if (allowOutlyingIntersections) {
			return isOnFullLine(p, eps);
		}
		return isOnPath(p, eps);
	}

	/*
	 * GeoSegmentInterface interface
	 */

	@Override
	public GeoElement getStartPointAsGeoElement() {
		return getStartPoint();
	}

	@Override
	public GeoElement getEndPointAsGeoElement() {
		return getEndPoint();
	}

	@Override
	public double getPointX(double parameter) {
		return startPoint.inhomX + parameter * y;
	}

	@Override
	public double getPointY(double parameter) {
		return startPoint.inhomY - parameter * x;
	}

	/*
	 * Path interface
	 */
	@Override
	public void pointChanged(GeoPointND P) {

		PathParameter pp = P.getPathParameter();

		// special case: segment of length 0
		if (length == 0) {
			P.setCoords2D(startPoint.inhomX, startPoint.inhomY, 1);
			P.updateCoordsFrom2D(false, null);
			if (!(pp.t >= 0 && pp.t <= 1)) {
				pp.t = 0.0;
			}
			return;
		}

		// project point on line
		super.pointChanged(P);

		// ensure that the point doesn't get outside the segment
		// i.e. ensure 0 <= t <= 1
		if (pp.t < 0.0) {
			P.setCoords2D(startPoint.x, startPoint.y, startPoint.z);
			P.updateCoordsFrom2D(false, null);
			pp.t = 0.0;
		} else if (pp.t > 1.0) {
			P.setCoords2D(endPoint.x, endPoint.y, endPoint.z);
			P.updateCoordsFrom2D(false, null);
			pp.t = 1.0;
		}
	}

	@Override
	public void pathChanged(GeoPointND P) {

		// if kernel doesn't use path/region parameters, do as if point changed
		// its coords
		if (!getKernel().usePathAndRegionParameters(P)) {
			pointChanged(P);
			return;
		}

		PathParameter pp = P.getPathParameter();

		// special case: segment of length 0
		if (length == 0) {
			P.setCoords2D(startPoint.inhomX, startPoint.inhomY, 1);
			P.updateCoordsFrom2D(false, null);
			if (!(pp.t >= 0 && pp.t <= 1)) {
				pp.t = 0.0;
			}
			return;
		}

		if (pp.t < 0.0) {
			pp.t = 0;
		} else if (pp.t > 1.0) {
			pp.t = 1;
		}

		// calc point for given parameter
		P.setCoords2D(startPoint.inhomX + pp.t * y,
				startPoint.inhomY - pp.t * x, 1);
		P.updateCoordsFrom2D(false, null);
	}

	/**
	 * Returns the smallest possible parameter value for this path.
	 * 
	 * @return smallest possible parameter
	 */
	@Override
	public double getMinParameter() {
		return 0;
	}

	/**
	 * Returns the largest possible parameter value for this path.
	 * 
	 * @return largest possible parameter
	 */
	@Override
	public double getMaxParameter() {
		return 1;
	}

	@Override
	public PathMover createPathMover() {
		return new PathMoverGeneric(this);
	}

	/**
	 * returns all class-specific xml tags for saveXML
	 */
	@Override
	protected void getXMLtags(StringBuilder sb) {
		super.getXMLtags(sb);

		// allowOutlyingIntersections
		sb.append("\t<outlyingIntersections val=\"");
		sb.append(allowOutlyingIntersections);
		sb.append("\"/>\n");

		// keepTypeOnGeometricTransform
		sb.append("\t<keepTypeOnTransform val=\"");
		sb.append(keepTypeOnGeometricTransform);
		sb.append("\"/>\n");

	}

	/**
	 * creates new transformed segment
	 */
	@Override
	public GeoElement[] createTransformedObject(Transform t,
			String transformedLabel) {

		if (keepTypeOnGeometricTransform && t.isAffine()) {
			// mirror endpoints
			GeoPointND[] points = { getStartPoint(), getEndPoint() };
			points = t.transformPoints(points);
			// create SEGMENT
			GeoElement segment = (GeoElement) kernel.segmentND(transformedLabel,
					points[0], points[1]);
			segment.setVisualStyleForTransformations(this);
			GeoElement[] geos = { segment, (GeoElement) points[0],
					(GeoElement) points[1] };
			return geos;
		} else if (!t.isAffine()) {
			// mirror endpoints

			// boolean oldSuppressLabelCreation = cons.isSuppressLabelsActive();
			// cons.setSuppressLabelCreation(true);

			this.forceSimpleTransform = true;
			GeoElement[] geos = { t.transform(this, transformedLabel)[0] };
			return geos;
		} else {
			// create LINE
			GeoElement transformedLine = t.getTransformedLine(this);
			transformedLine.setLabel(transformedLabel);
			transformedLine.setVisualStyleForTransformations(this);
			GeoElement[] geos = { transformedLine };
			return geos;
		}
	}

	@Override
	public boolean isGeoSegment() {
		return true;
	}

	@Override
	public void setZero() {
		setCoords(0, 1, 0);
	}

	//////////////////////////////////////
	// 3D stuff
	//////////////////////////////////////

	@Override
	public boolean hasDrawable3D() {
		return true;
	}

	@Override
	public Coords getLabelPosition() {
		return new Coords(getPointX(0.5), getPointY(0.5), 0, 1);
	}

	/**
	 * returns the paramter for the closest point to P on the Segment
	 * (extrapolated) so answers can be returned outside the range [0,1]
	 * 
	 * @param ptx
	 *            point x-coord
	 * @param pty
	 *            point y-coord
	 * @return closest parameter
	 */
	public double getParameter(double ptx, double pty) {
		double px = ptx;
		double py = pty;
		// project P on line
		// param of projection point on perpendicular line
		double t = -(z + x * px + y * py) / (x * x + y * y);
		// calculate projection point using perpendicular line
		px += t * x;
		py += t * y;

		// calculate parameter
		if (Math.abs(x) <= Math.abs(y)) {
			return (startPoint.z * px - startPoint.x) / (y * startPoint.z);
		}
		return (startPoint.y - startPoint.z * py) / (x * startPoint.z);

	}

	/**
	 * Calculates the euclidian distance between this GeoSegment and GeoPoint P.
	 * 
	 * returns distance from endpoints if appropriate
	 */
	@Override
	public double distance(GeoPoint p) {

		double t = getParameter(p.inhomX, p.inhomY);

		// if t is outside the range [0,1] then the closest point is not on the
		// Segment
		if (t < 0) {
			return p.distance(startPoint);
		}
		if (t > 1) {
			return p.distance(endPoint);
		}

		return super.distance(p);
	}

	@Override
	public double distance(double x0, double y0) {

		double t = getParameter(x0, y0);

		// if t is outside the range [0,1] then the closest point is not on the
		// Segment
		if (t < 0) {
			return startPoint.distance(x0, y0);
		}
		if (t > 1) {
			return endPoint.distance(x0, y0);
		}

		return super.distance(x0, y0);
	}

	@Override
	public boolean isOnPath(Coords Pnd, double eps) {
		if (pnt2D == null) {
			pnt2D = new Coords(3);
		}
		pnt2D.setCoordsIn2DView(Pnd);
		if (!super.isOnFullLine2D(pnt2D, eps)) {
			return false;
		}

		return respectLimitedPath(pnt2D, eps);

	}

	@Override
	public boolean respectLimitedPath(Coords Pnd, double eps) {
		if (pnt2D == null) {
			pnt2D = new Coords(3);
		}
		pnt2D.setCoordsIn2DView(Pnd);
		PathParameter pp = getTempPathParameter();
		doPointChanged(pnt2D, pp);
		double t = pp.getT();

		return t >= -eps && t <= 1 + eps;
	}

	/**
	 * exact calculation for checking if point is on Segment[segStart,segEnd]
	 * 
	 * @param segStart
	 *            start coords
	 * @param segEnd
	 *            end coords
	 * @param point
	 *            point to be checked
	 * @param checkOnFullLine
	 *            - if true, do extra calculation to make sure.
	 * @param eps
	 *            precision
	 * @return true if point belongs to segment
	 */
	public static boolean checkOnPath(Coords segStart, Coords segEnd,
			Coords point, boolean checkOnFullLine, double eps) {
		if (checkOnFullLine) {
			if (segEnd.sub(segStart).crossProduct(point.sub(segStart))
					.equalsForKernel(new Coords(0, 0, 0),
							Kernel.STANDARD_PRECISION)) {
				return false;
			}
		}

		double x1 = segStart.getInhom(0);
		double x2 = segEnd.getInhom(0);
		double x = point.getInhom(0);
		if (x1 - eps <= x2 && x2 <= x1 + eps) {
			double y1 = segStart.getInhom(1);
			double y2 = segEnd.getInhom(1);
			double y = point.getInhom(1);

			if (y1 - eps <= y2 && y2 <= y1 + eps) {
				return true;
			}
			return y1 - eps <= y && y <= y2 + eps
					|| y2 - eps <= y && y <= y1 + eps;

		}
		return x1 - eps <= x && x <= x2 + eps || x2 - eps <= x && x <= x1 + eps;
	}

	@Override
	public boolean isAllEndpointsLabelsSet() {
		return !forceSimpleTransform && startPoint.isLabelSet()
				&& endPoint.isLabelSet();
	}

	@Override
	public void modifyInputPoints(GeoPointND P, GeoPointND Q) {
		AlgoJoinPointsSegment algo = (AlgoJoinPointsSegment) getParentAlgorithm();
		algo.modifyInputPoints(P, Q);
	}

	@Override
	public int getMetasLength() {
		if (meta == null) {
			return 0;
		}

		return 1;
	}

	@Override
	public GeoElement[] getMetas() {
		return new GeoElement[] { meta };
	}

	/**
	 * @param poly
	 *            polygon or polyhedron creating this segment
	 */
	public void setFromMeta(GeoElement poly) {
		meta = poly;
	}

	@Override
	public boolean respectLimitedPath(double parameter) {
		return DoubleUtil.isGreaterEqual(parameter, 0)
				&& DoubleUtil.isGreaterEqual(1, parameter);
	}

	/**
	 * dilate from S by r
	 */
	@Override
	public void dilate(NumberValue rval, Coords S) {

		super.dilate(rval, S);

		startPoint.dilate(rval, S);
		endPoint.dilate(rval, S);

		calcLength();

	}

	/**
	 * rotate this line by angle phi around (0,0)
	 */
	@Override
	public void rotate(NumberValue phiVal) {
		super.rotate(phiVal);

		startPoint.rotate(phiVal);
		endPoint.rotate(phiVal);

		// not needed for rotate
		// calcLength();
	}

	/**
	 * rotate this line by angle phi around Q
	 */
	@Override
	public void rotate(NumberValue phiVal, GeoPointND point) {

		super.rotate(phiVal, point);
		Coords sCoords = point.getInhomCoords();
		startPoint.rotate(phiVal, sCoords);
		endPoint.rotate(phiVal, sCoords);

		// not needed for rotate
		// calcLength();

	}

	/**
	 * mirror this line at point Q
	 */
	@Override
	public void mirror(Coords Q) {

		super.mirror(Q);
		startPoint.mirror(Q);
		endPoint.mirror(Q);

		// not needed for mirror
		// calcLength();

	}

	/**
	 * mirror this point at line g
	 */
	@Override
	public void mirror(GeoLineND g1) {

		super.mirror(g1);
		startPoint.mirror(g1);
		endPoint.mirror(g1);

		// not needed for mirror
		// calcLength();

	}

	/**
	 * translate by vector v
	 */
	@Override
	public void translate(Coords v) {
		super.translate(v);

		startPoint.translate(v);
		endPoint.translate(v);

		// not needed for mirror
		// calcLength();
	}

	@Override
	public void matrixTransform(double p, double q, double r, double s) {

		super.matrixTransform(p, q, r, s);

		startPoint.matrixTransform(p, q, r, s);
		endPoint.matrixTransform(p, q, r, s);

		calcLength();

	}

	@Override
	public void matrixTransform(double a00, double a01, double a02,
			double a10, double a11, double a12, double a20, double a21,
			double a22) {
		super.matrixTransform(a00, a01, a02, a10, a11, a12, a20, a21, a22);

		startPoint.matrixTransform(a00, a01, a02, a10, a11, a12, a20, a21, a22);
		endPoint.matrixTransform(a00, a01, a02, a10, a11, a12, a20, a21, a22);

		calcLength();

	}

	@Override
	public void setCoords(MyPoint locusPoint, MyPoint locusPoint2) {
		double x1 = locusPoint.x;
		double x2 = locusPoint2.x;
		double y1 = locusPoint.y;
		double y2 = locusPoint2.y;

		// line thro' 2 points
		setCoords(y1 - y2, x2 - x1, x1 * y2 - y1 * x2);
		startPoint.setCoords(x1, y1, 1.0);
		endPoint.setCoords(x2, y2, 1.0);
	}

	@Override
	public GeoElement copyFreeSegment() {
		GeoPoint startPoint1 = (GeoPoint) getStartPoint().copyInternal(cons);
		GeoPoint endPoint1 = (GeoPoint) getEndPoint().copyInternal(cons);
		AlgoJoinPointsSegment algo = new AlgoJoinPointsSegment(cons, startPoint1, endPoint1);
		return algo.getSegment();
	}

	@Override
	public ExtendedBoolean isCongruent(GeoElement geo) {
		return ExtendedBoolean.newExtendedBoolean(geo.isGeoSegment() && DoubleUtil
				.isEqual(getLength(), ((GeoSegmentND) geo).getLength()));
	}

	@Override
	public void setChangeableParentIfNull(ChangeableParent ccp) {
		// used for GeoPoint3D
	}

	@Override
	public boolean isShape() {
		return isShape;
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
	public char getLabelDelimiter() {
		return '=';
	}

	@Override
	public void toGeoCurveCartesian(GeoCurveCartesianND curve) {
		curve.setFromPolyLine(new GeoPointND[] { startPoint, endPoint }, false);
	}
}
