package org.geogebra.common.geogebra3D.kernel3D.geos;

import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.geogebra3D.kernel3D.algos.AlgoJoinPoints3D;
import org.geogebra.common.geogebra3D.kernel3D.algos.AlgoRayPointVector3D;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Transform;
import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoLine;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.geos.LimitedPath;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.kernel.kernelND.GeoRayND;
import org.geogebra.common.kernel.matrix.Coords;
import org.geogebra.common.plugin.GeoClass;
import org.geogebra.common.util.DoubleUtil;

/**
 * 3D ray
 *
 */
public class GeoRay3D extends GeoLine3D implements GeoRayND, LimitedPath {
	private boolean allowOutlyingIntersections = false;
	private boolean keepTypeOnGeometricTransform = true; // for mirroring,
															// rotation, ...

	/**
	 * @param c
	 *            construction
	 * @param O
	 *            start point
	 * @param Q
	 *            end point
	 */
	public GeoRay3D(Construction c, GeoPointND O, GeoPointND Q) {
		super(c, O, Q);
		setStartPoint(O);
	}

	/**
	 * @param c
	 *            construction
	 * @param O
	 *            start point
	 */
	public GeoRay3D(Construction c, GeoPointND O) {
		super(c);
		setStartPoint(O);
	}

	/**
	 * @param construction
	 *            construction
	 */
	public GeoRay3D(Construction construction) {
		super(construction);
	}

	@Override
	public GeoClass getGeoClassType() {
		return GeoClass.RAY3D;
	}

	@Override
	protected GeoCoordSys1D create(Construction cons1) {
		return new GeoRay3D(cons1);
	}

	// Path3D interface
	@Override
	public double getMinParameter() {
		return 0;
	}

	@Override
	public boolean isValidCoord(double x) {
		return (x >= 0);
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

		return true;
	}

	// ///////////////////////////////////////
	// LIMITED PATH
	// ///////////////////////////////////////

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

	@Override
	public GeoElement[] createTransformedObject(Transform t, String label1) {
		AlgoElement algoParent1 = keepTypeOnGeometricTransform
				? getParentAlgorithm() : null;

		// CREATE RAY
		if (algoParent1 instanceof AlgoJoinPoints3D && t.isAffine()) {
			// transform points
			AlgoJoinPoints3D algo = (AlgoJoinPoints3D) algoParent1;
			GeoPointND[] points = { algo.getP(), algo.getQ() };
			points = t.transformPoints(points);
			// if(t.isAffine()){
			GeoElement ray = (GeoElement) kernel.getManager3D().ray3D(label1,
					points[0], points[1]);
			ray.setVisualStyleForTransformations(this);
			GeoElement[] geos = { ray, (GeoElement) points[0],
					(GeoElement) points[1] };
			return geos;
			// }
		}
		// create LINE
		GeoElement transformedLine = t.getTransformedLine(this);
		transformedLine.setLabel(label1);
		GeoElement[] ret = { transformedLine };
		return ret;
	}

	@Override
	public boolean isAllEndpointsLabelsSet() {
		return startPoint.isLabelSet();
	}

	@Override
	public boolean isIntersectionPointIncident(GeoPoint p, double eps) {
		if (allowOutlyingIntersections) {
			return isOnFullLine(p.getCoordsInD3(), eps);
		}
		return isOnPath(p, eps);
	}

	@Override
	public GeoElement copyInternal(Construction cons1) {
		GeoRay3D ray = new GeoRay3D(cons1,
				(GeoPointND) startPoint.copyInternal(cons1));
		ray.set(this);
		return ray;
	}

	@Override
	public void set(GeoElementND geo) {
		super.set(geo);
		if (!geo.isGeoRay()) {
			return;
		}

		if (!geo.isDefined()) {
			setUndefined();
		}

		GeoRayND ray = (GeoRayND) geo;

		setKeepTypeOnGeometricTransform(ray.keepsTypeOnGeometricTransform());

		startPoint = GeoLine.updatePoint(cons, startPoint, ray.getStartPoint());
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
	public boolean isGeoRay() {
		return true;
	}

	@Override
	public boolean respectLimitedPath(double parameter) {
		return DoubleUtil.isGreaterEqual(parameter, 0);
	}

	@Override
	final protected void getCoordsXML(StringBuilder sb) {
		// not needed here
	}

	@Override
	public GeoElement copyFreeRay() {
		GeoPointND startPoint1 = (GeoPointND) getStartPoint()
				.copyInternal(cons);

		Coords direction = getDirectionInD3();

		GeoVector3D directionVec = new GeoVector3D(cons);
		directionVec.setCoords(direction);

		AlgoRayPointVector3D algo = new AlgoRayPointVector3D(cons,
				startPoint1, directionVec);

		return algo.getOutput(0);
	}

	@Override
	public boolean isWhollyIn2DView(EuclidianView ev) {

		// check start point
		if (!DoubleUtil.isZero(getStartPoint().getInhomCoords().getZ())) {
			return false;
		}

		// check direction
		Coords equation = getCartesianEquationVector(ev.getMatrix());
		return equation != null;

	}

}
