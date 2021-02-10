package org.geogebra.common.geogebra3D.kernel3D.geos;

import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.geogebra3D.kernel3D.algos.AlgoJoinPoints3D;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.MyPoint;
import org.geogebra.common.kernel.PathMover;
import org.geogebra.common.kernel.PathMoverGeneric;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.Transform;
import org.geogebra.common.kernel.arithmetic.MyDouble;
import org.geogebra.common.kernel.arithmetic.ValueType;
import org.geogebra.common.kernel.geos.ChangeableParent;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.kernelND.GeoCurveCartesianND;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.kernel.kernelND.GeoSegmentND;
import org.geogebra.common.kernel.matrix.Coords;
import org.geogebra.common.plugin.GeoClass;
import org.geogebra.common.util.DoubleUtil;
import org.geogebra.common.util.ExtendedBoolean;

/**
 * 
 * Class for 3D segments.
 * <p>
 * See {@link GeoCoordSys1D} for 1D coord sys abilities (matrix description,
 * path for points).
 * 
 * 
 * @author ggb3d
 * 
 */
public class GeoSegment3D extends GeoCoordSys1D implements GeoSegmentND {

	private boolean allowOutlyingIntersections = false;
	private boolean keepTypeOnGeometricTransform = true; // for mirroring,
	private ChangeableParent changeableParent = null;
	private boolean forceSimpleTransform;
	private GeoElement meta = null;

	/**
	 * constructor with no points
	 * 
	 * @param c
	 *            the construction
	 */
	public GeoSegment3D(Construction c) {
		super(c);
	}

	/**
	 * creates a segment linking p1 to p2
	 * 
	 * @param c
	 *            construction
	 * @param p1
	 *            start point
	 * @param p2
	 *            end point
	 */
	public GeoSegment3D(Construction c, GeoPointND p1, GeoPointND p2) {
		this(c, p1, p2, false);
	}

	/**
	 * creates a segment linking p1 to p2
	 * 
	 * @param c
	 *            construction
	 * @param p1
	 *            start point
	 * @param p2
	 *            end point
	 * @param isIntersection
	 *            if this is an intersection curve
	 */
	public GeoSegment3D(Construction c, GeoPointND p1, GeoPointND p2,
			boolean isIntersection) {
		super(c, p1, p2, isIntersection);
	}

	/**
	 * creates a segment linking v1 to v2
	 * 
	 * @param c
	 *            construction
	 * @param v1
	 *            start point
	 * @param v2
	 *            end point
	 */
	private GeoSegment3D(Construction c, Coords v1, Coords v2) {
		super(c, v1, v2.sub(v1));
	}

	/**
	 * returns segment's length
	 * 
	 * @return length
	 */
	@Override
	public double getLength() {
		if (isDefined()) {
			return getUnit();
		}

		return Double.NaN;
	}

	/**
	 * return {@link GeoClass}
	 * 
	 * @return {@link GeoClass}
	 */
	@Override
	public GeoClass getGeoClassType() {
		return GeoClass.SEGMENT3D;
	}

	@Override
	protected GeoCoordSys1D create(Construction cons2) {
		return new GeoSegment3D(cons2);
	}

	/**
	 * Yields true iff startpoint and endpoint of s are equal to startpoint and
	 * endpoint of this segment.
	 */

	@Override
	final public boolean isEqual(GeoElementND geo) {
		if (!geo.isGeoSegment()) {
			return false;
		}
		GeoSegmentND s = (GeoSegmentND) geo;

		return (getStartInhomCoords().equalsForKernel(s.getStartInhomCoords())
				&& getEndInhomCoords().equalsForKernel(s.getEndInhomCoords()))
				|| (getStartInhomCoords().equalsForKernel(s.getEndInhomCoords())
						&& getEndInhomCoords()
								.equalsForKernel(s.getStartInhomCoords()));
	}

	/**
	 * TODO say if this is to be shown in (3D) euclidian view
	 * 
	 * @return if this is to be shown in (3D) euclidian view
	 * 
	 */
	@Override
	protected boolean showInEuclidianView() {

		return isDefined();
	}

	@Override
	public String toValueString(StringTemplate tpl) {

		return kernel.format(getLength(), tpl);
	}

	/**
	 * return the length of the segment as a string
	 * 
	 * @return the length of the segment as a string
	 * 
	 */
	@Override
	final public String toString(StringTemplate tpl) {

		StringBuilder sbToString = getSbToString();
		sbToString.setLength(0);
		sbToString.append(label);
		sbToString.append(" = "); // TODO use kernel property

		sbToString.append(kernel.format(getLength(), tpl));

		return sbToString.toString();
	}

	@Override
	public boolean isGeoSegment() {
		return true;
	}

	@Override
	public void setTwoPointsInhomCoords(Coords start, Coords end) {
		this.setCoord(start, end.sub(start));
	}

	@Override
	public boolean isOnPath(Coords p, double eps) {
		// first check global line
		if (!super.isOnPath(p, eps)) {
			return false;
		}

		// then check position on segment
		return respectLimitedPath(p, eps);

	}

	@Override
	public boolean respectLimitedPath(Coords p, double eps) {

		if (DoubleUtil.isEqual(p.getW(), 0, eps)) {
			return false;
		}
		double d = p.sub(getStartInhomCoords()).dotproduct(getDirectionInD3());
		if (d < -eps) {
			return false;
		}
		double l = getLength();
		if (d > l * l + eps) {
			return false;
		}

		return true;
	}

	@Override
	public PathMover createPathMover() {
		return new PathMoverGeneric(this);
	}

	@Override
	public double getMaxParameter() {
		return 1;
	}

	@Override
	public double getMinParameter() {
		return 0;
	}

	@Override
	public boolean isClosedPath() {
		// TODO Auto-generated method stub
		return false;
	}

	// ///////////////////////////////////
	// GeoSegmentInterface interface

	@Override
	public double getPointX(double parameter) {
		// TODO delete from GeoSegmentND
		return 0;
	}

	@Override
	public double getPointY(double parameter) {
		// TODO delete from GeoSegmentND
		return 0;
	}

	/**
	 * // TODO add to GeoSegmentND
	 * 
	 * @param parameter
	 *            path parameter
	 * 
	 * @param point
	 *            set to point on segment at parameter
	 */
	public void getPointCoords(double parameter, Coords point) {
		point.setSub3(endPoint.getInhomCoordsInD3(),
				startPoint.getInhomCoordsInD3());
		point.mulInside3(parameter);
		point.setAdd3(startPoint.getInhomCoordsInD3(), point);
		point.setW(1);
	}

	@Override
	public GeoElement getStartPointAsGeoElement() {
		return (GeoElement) startPoint;
	}

	@Override
	public GeoElement getEndPointAsGeoElement() {
		return (GeoElement) endPoint;
	}

	@Override
	public boolean isValidCoord(double x) {
		return (x >= 0) && (x <= 1);
	}

	@Override
	final public boolean isGeoLine() {
		return true;
	}

	// ///////////////////////////////////////
	// LIMITED PATH
	// ///////////////////////////////////////

	// rotation, ...

	@Override
	final public boolean isLimitedPath() {
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

	/**
	 * creates new transformed segment
	 */
	@Override
	public GeoElement[] createTransformedObject(Transform t,
			String labelTrans) {

		if (keepTypeOnGeometricTransform && t.isAffine()) {

			// mirror endpoints
			GeoPointND[] points = { getStartPoint(), getEndPoint() };
			points = t.transformPoints(points);
			// create SEGMENT
			GeoElement segment = (GeoElement) kernel.getManager3D()
					.segment3D(labelTrans, points[0], points[1]);
			segment.setVisualStyleForTransformations(this);
			GeoElement[] geos = { segment, (GeoElement) points[0],
					(GeoElement) points[1] };
			return geos;
		} else if (!t.isAffine()) {
			// mirror endpoints
			this.forceSimpleTransform = true;
			GeoElement[] geos = { t.transform(this, labelTrans)[0] };
			return geos;
		} else {
			// create LINE
			GeoElement transformedLine = t.getTransformedLine(this);
			transformedLine.setLabel(labelTrans);
			transformedLine.setVisualStyleForTransformations(this);
			GeoElement[] geos = { transformedLine };
			return geos;
		}
	}

	@Override
	public boolean isAllEndpointsLabelsSet() {
		return !forceSimpleTransform && startPoint.isLabelSet()
				&& endPoint.isLabelSet();
	}

	@Override
	public boolean isIntersectionPointIncident(GeoPoint p, double eps) {
		if (allowOutlyingIntersections) {
			return isOnFullLine(p.getCoordsInD3(), eps);
		}
		return isOnPath(p, eps);
	}

	@Override
	public GeoElement copyInternal(Construction cons2) {
		GeoSegment3D seg = new GeoSegment3D(cons2,
				(GeoPointND) startPoint.copyInternal(cons2),
				(GeoPointND) endPoint.copyInternal(cons2));
		seg.set(this);
		return seg;
	}

	@Override
	public void set(GeoElementND geo) {
		super.set(geo);
		if (!geo.isGeoSegment()) {
			return;
		}

		GeoSegmentND seg = (GeoSegmentND) geo;

		setSegment(seg);
	}

	/**
	 * set the segment to this
	 * 
	 * @param seg
	 *            segment
	 */
	public void setSegment(GeoSegmentND seg) {

		if (!seg.isDefined()) {
			setUndefined();
		}

		setKeepTypeOnGeometricTransform(seg.keepsTypeOnGeometricTransform());

		setCoord(seg.getStartInhomCoords(), seg.getDirectionInD3());
	}

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

	@Override
	final public MyDouble getNumber() {
		return new MyDouble(kernel, getLength());
	}

	@Override
	final public double getDouble() {
		return getLength();
	}

	@Override
	final public boolean isNumberValue() {
		return true;
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
	public void modifyInputPoints(GeoPointND P, GeoPointND Q) {
		AlgoJoinPoints3D algo = (AlgoJoinPoints3D) getParentAlgorithm();
		algo.modifyInputPoints(P, Q);
	}

	/**
	 * modify inputs for segment joining points in a polygon/polyhedron
	 * 
	 * @param poly
	 *            polygon/polyhedron
	 * @param P
	 *            first vertex
	 * @param Q
	 *            second vertex
	 */
	public void modifyInputPolyAndPoints(GeoElement poly, GeoPointND P,
			GeoPointND Q) {
		AlgoJoinPoints3D algo = (AlgoJoinPoints3D) getParentAlgorithm();
		algo.modifyInputPolyAndPoints(poly, P, Q);
	}

	@Override
	public final void removePointOnLine(GeoPointND p) {
		// TODO
	}

	@Override
	public boolean respectLimitedPath(double parameter) {
		return DoubleUtil.isGreaterEqual(parameter, 0)
				&& DoubleUtil.isGreaterEqual(1, parameter);
	}

	/**
	 * set start and end points
	 * 
	 * @param start
	 *            start point
	 * @param end
	 *            end point
	 */
	public void setPoints(GeoPoint3D start, GeoPoint3D end) {
		startPoint = start;
		endPoint = end;
	}

	@Override
	public GeoPointND setStandardStartPoint() {
		// TODO Auto-generated method stub
		return startPoint;
	}

	@Override
	public void setCoords(MyPoint locusPoint, MyPoint locusPoint2) {
		setCoordFromPoints(
				new Coords(locusPoint.x, locusPoint.y, locusPoint.getZ(), 1.0),
				new Coords(locusPoint2.x, locusPoint2.y, locusPoint2.getZ(),
						1.0));
	}

	/*
	 * public double distance(Coords P){ return
	 * P.distLine(getStartInhomCoords(), getDirectionInD3()); }
	 */

	@Override
	public GeoElement copyFreeSegment() {
		GeoPointND startPoint1 = (GeoPointND) getStartPoint()
				.copyInternal(cons);
		GeoPointND endPoint1 = (GeoPointND) getEndPoint().copyInternal(cons);
		AlgoJoinPoints3D algo = new AlgoJoinPoints3D(cons, startPoint1,
				endPoint1, null, GeoClass.SEGMENT3D);

		return algo.getOutput(0);
	}

	@Override
	public boolean setCoord(GeoPointND O, GeoPointND I) {

		if (super.setCoord(O, I)) {
			setUndefined();
			return true;
		}

		return false;
	}

	@Override
	public ValueType getValueType() {
		return ValueType.NUMBER;
	}

	@Override
	public final void setStartPoint(GeoPointND P) {
		startPoint = P;
	}

	@Override
	public ExtendedBoolean isCongruent(GeoElement geo) {
		return ExtendedBoolean.newExtendedBoolean(geo.isGeoSegment() && DoubleUtil
				.isEqual(getLength(), ((GeoSegmentND) geo).getLength()));
	}

	@Override
	public Coords getOrigin() {
		return getCoordSys().getOrigin();
	}

	// ////////////////////////////////////////////////////
	// PARENT NUMBER (HEIGHT OF A PRISM, ...)
	// ////////////////////////////////////////////////////

	/**
	 * Used for polyhedron net: first polygon set it
	 * 
	 * @param cp
	 *            changeable parent
	 * 
	 */
	@Override
	final public void setChangeableParentIfNull(
			ChangeableParent cp) {
		if (changeableParent == null) {
			changeableParent = cp;
		}
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
	public boolean isWhollyIn2DView(EuclidianView ev) {
		return DoubleUtil.isZero(getStartPoint().getInhomCoords().getZ())
				&& DoubleUtil.isZero(getEndPoint().getInhomCoords().getZ());
	}

	@Override
	public void toGeoCurveCartesian(GeoCurveCartesianND curve) {
		curve.setFromPolyLine(new GeoPointND[] { startPoint, endPoint }, false);
	}

    @Override
    public boolean isDefined() {
        return super.isDefined() || coordsys.hasZeroVx();
    }

}
