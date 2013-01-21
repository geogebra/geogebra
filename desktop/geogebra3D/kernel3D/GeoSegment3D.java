package geogebra3D.kernel3D;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.PathMover;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.Transform;
import geogebra.common.kernel.Matrix.Coords;
import geogebra.common.kernel.arithmetic.MyDouble;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoPoint;
import geogebra.common.kernel.kernelND.GeoPointND;
import geogebra.common.kernel.kernelND.GeoSegmentND;
import geogebra.common.plugin.GeoClass;


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

	/** if is a segment from a GeoPolygon3D or GeoPolyhedron */
	private GeoElement geoParent = null;

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
		super(c, p1, p2);
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
	public double getLength() {
		return getUnit();
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
	 * TODO return if this is equal to Geo
	 * 
	 * @param Geo
	 *            GeoElement
	 * @return if this is equal to Geo
	 */
	@Override
	public boolean isEqual(GeoElement Geo) {
		return false;
	}

	/**
	 * TODO say if this is to be shown in algebra view
	 * 
	 * @return if this is to be shown in algebra view
	 * 
	 */
	@Override
	public boolean showInAlgebraView() {

		return true;
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

		return kernel.format(getLength(),tpl);
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

		sbToString.append(kernel.format(getLength(),tpl));

		return sbToString.toString();
	}

	@Override
	public boolean isGeoSegment() {
		return true;
	}

	// Path3D interface

	/**
	 * return the 2D segment path linked to
	 * 
	 * @return the 2D segment path linked to
	 */
	/*
	 * public Path getPath2D(){ return (Path) getGeoElement2D(); }
	 */

	/**
	 * {@inheritDoc}
	 */
	@Override
	public GeoElement getGeoElement2D() {

		if (!hasGeoElement2D()) {
			AlgoTo2D algo = new AlgoTo2D(cons, this);
			setGeoElement2D(algo.getOut());
		}
		return super.getGeoElement2D();
	}

	public void setTwoPointsCoords(Coords start, Coords end) {
		this.setCoord(start, end.sub(start));
	}
	


	@Override
	public boolean isOnPath(Coords p, double eps) {
		// first check global line
		if (!super.isOnPath(p, eps))
			return false;

		// then check position on segment
		return respectLimitedPath(p, eps);

	}

	@Override
	public boolean respectLimitedPath(Coords p, double eps) {

		if (Kernel.isEqual(p.getW(), 0, eps))// infinite point
			return false;
		double d = p.sub(getStartInhomCoords()).dotproduct(getDirectionInD3());
		if (d < -eps)
			return false;
		double l = getLength();
		if (d > l * l + eps)
			return false;

		return true;
	}

	public PathMover createPathMover() {
		// TODO Auto-generated method stub
		return null;
	}

	public double getMaxParameter() {
		return 1;
	}

	public double getMinParameter() {
		return 0;
	}

	public boolean isClosedPath() {
		// TODO Auto-generated method stub
		return false;
	}

	

	// ///////////////////////////////////
	// GeoSegmentInterface interface

	public double getPointX(double parameter) {
		// TODO delete from GeoSegmentND
		return 0;
	}

	public double getPointY(double parameter) {
		// TODO delete from GeoSegmentND
		return 0;
	}

	
	/**
	 * // TODO add to GeoSegmentND
	 * @param parameter path parameter
	 * @return coresponding coords
	 */
	public Coords getPointCoords(double parameter) {
		return startPoint.getInhomCoordsInD(3).add(
				(endPoint.getInhomCoordsInD(3).sub(startPoint.getInhomCoordsInD(3)))
						.mul(parameter));
	}

	public GeoElement getStartPointAsGeoElement() {
		return (GeoElement) startPoint;
	}

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

	@Override
	final public boolean isDefined() {
		return coordsys.getMadeCoordSys() >= 0;
	}

	// ///////////////////////////////////////
	// LIMITED PATH
	// ///////////////////////////////////////

	private boolean allowOutlyingIntersections = false;
	private boolean keepTypeOnGeometricTransform = true; // for mirroring,
															// rotation, ...

	@Override
	final public boolean isLimitedPath() {
		return true;
	}

	public boolean allowOutlyingIntersections() {
		return allowOutlyingIntersections;
	}

	public void setAllowOutlyingIntersections(boolean flag) {
		allowOutlyingIntersections = flag;
	}

	public boolean keepsTypeOnGeometricTransform() {
		return keepTypeOnGeometricTransform;
	}

	public void setKeepTypeOnGeometricTransform(boolean flag) {
		keepTypeOnGeometricTransform = flag;
	}

	private boolean forceSimpleTransform;

	/**
	 * creates new transformed segment
	 */
	public GeoElement[] createTransformedObject(Transform t, String labelTrans) {

		if (keepTypeOnGeometricTransform && t.isAffine()) {

			// mirror endpoints
			GeoPointND[] points = { getStartPoint(), getEndPoint() };
			points = t.transformPoints(points);
			// create SEGMENT
			GeoElement segment = (GeoElement) kernel.getManager3D().Segment3D(
					labelTrans, points[0], points[1]);
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

	public boolean isAllEndpointsLabelsSet() {
		return !forceSimpleTransform && startPoint.isLabelSet()
				&& endPoint.isLabelSet();
	}

	public boolean isIntersectionPointIncident(GeoPoint p, double eps) {
		if (allowOutlyingIntersections)
			return isOnFullLine(p.getCoordsInD(3), eps);
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
	public void set(GeoElement geo) {
		super.set(geo);
		if (!geo.isGeoSegment())
			return;

		if (!geo.isDefined())
			setUndefined();

		GeoSegmentND seg = (GeoSegmentND) geo;

		setKeepTypeOnGeometricTransform(seg.keepsTypeOnGeometricTransform());

		startPoint.set(seg.getStartPoint());
		endPoint.set(seg.getEndPoint());
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

	final public MyDouble getNumber() {
		return new MyDouble(kernel, getLength());
	}

	final public double getDouble() {
		return getLength();
	}

	@Override
	final public boolean isNumberValue() {
		return true;
	}

	private GeoElement meta = null;
	
	@Override
	public boolean isFromMeta() {
		return meta!=null;
	}
	
	public GeoElement getMeta(){
		return meta;
	}

	/**
	 * @param poly polygon or polyhedron creating this segment
	 */
	public void setFromMeta(GeoElement poly) {
		meta = poly;
	}
	
	
	public void modifyInputPoints(GeoPointND P, GeoPointND Q){
		AlgoJoinPoints3D algo = (AlgoJoinPoints3D) getParentAlgorithm();
		algo.modifyInputPoints(P,Q);
	}
	
	/**
	 * modify inputs for segment joining points in a polygon/polyhedron
	 * @param poly polygon/polyhedron
	 * @param P first vertex
	 * @param Q second vertex
	 */
	public void modifyInputPolyAndPoints(GeoElement poly, GeoPointND P, GeoPointND Q){
		AlgoJoinPoints3D algo = (AlgoJoinPoints3D) getParentAlgorithm();
		algo.modifyInputPolyAndPoints(poly, P, Q);
	}
	

	public final void removePointOnLine(GeoPointND p) {
		//TODO
	}
	
	public boolean respectLimitedPath(double parameter){
		return Kernel.isGreaterEqual(parameter, 0) && Kernel.isGreaterEqual(1, parameter);
	}

	/**
	 * set start and end points
	 * @param start start point
	 * @param end end point
	 */
	public void setPoints(GeoPoint3D start, GeoPoint3D end) {
		startPoint = start;
		endPoint = end;		
	}
	
	
	
}
