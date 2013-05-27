package geogebra3D.kernel3D;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.Transform;
import geogebra.common.kernel.Matrix.Coords;
import geogebra.common.kernel.algos.AlgoElement;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoPoint;
import geogebra.common.kernel.geos.LimitedPath;
import geogebra.common.kernel.kernelND.GeoPointND;
import geogebra.common.kernel.kernelND.GeoRayND;
import geogebra.common.plugin.GeoClass;


public class GeoRay3D extends GeoLine3D implements GeoRayND, LimitedPath {

	public GeoRay3D(Construction c, GeoPointND O, GeoPointND Q) {
		super(c, O, Q);
		setStartPoint(O);
	}

	public GeoRay3D(Construction c, GeoPointND O) {
		super(c);
		setStartPoint(O);
	}

	public GeoRay3D(Construction construction) {
		super(construction);
	}

	@Override
	public GeoClass getGeoClassType() {
		return GeoClass.RAY3D;
	}

	@Override
	public String getTypeString() {
		return "Ray3D";
	}

	@Override
	protected GeoCoordSys1D create(Construction cons) {
		return new GeoRay3D(cons);
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

		return true;
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

	public GeoElement[] createTransformedObject(Transform t, String label) {
		AlgoElement algoParent = keepTypeOnGeometricTransform ? getParentAlgorithm()
				: null;

		// CREATE RAY
		if (algoParent instanceof AlgoJoinPoints3D && t.isAffine()) {
			// transform points
			AlgoJoinPoints3D algo = (AlgoJoinPoints3D) algoParent;
			GeoPointND[] points = { algo.getP(), algo.getQ() };
			points = t.transformPoints(points);
			// if(t.isAffine()){
			GeoElement ray = (GeoElement) kernel.getManager3D().Ray3D(label,
					points[0], points[1]);
			ray.setVisualStyleForTransformations(this);
			GeoElement[] geos = { ray, (GeoElement) points[0],
					(GeoElement) points[1] };
			return geos;
			// }
			/*
			 * else { GeoPoint inf = new GeoPoint(cons);
			 * inf.setCoords(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY,
			 * 1); inf = (GeoPoint)t.doTransform(inf); AlgoConicPartCircumcircle
			 * ae = new AlgoConicPartCircumcircle(cons,
			 * Transform.transformedGeoLabel(this), points[0],
			 * points[1],inf,GeoConicPart.CONIC_PART_ARC);
			 * cons.removeFromAlgorithmList(ae); GeoElement arc =
			 * ae.getConicPart(); arc.setVisualStyleForTransformations(this);
			 * GeoElement [] geos = {arc, points[0], points[1]}; return geos; }
			 */
		}
		/*
		 * else if (algoParent instanceof AlgoRayPointVector) { // transform
		 * startpoint GeoPoint [] points = {getStartPoint()}; points =
		 * t.transformPoints(points);
		 * 
		 * boolean oldSuppressLabelCreation = cons.isSuppressLabelsActive();
		 * cons.setSuppressLabelCreation(true); AlgoDirection ad = new
		 * AlgoDirection(cons,this); cons.removeFromAlgorithmList(ad); GeoVector
		 * direction = ad.getVector(); if(t.isAffine()) {
		 * 
		 * direction = (GeoVector)t.doTransform(direction);
		 * cons.setSuppressLabelCreation(oldSuppressLabelCreation);
		 * 
		 * // ray through transformed point with direction of transformed line
		 * GeoElement ray = kernel.Ray(label, points[0], direction);
		 * ray.setVisualStyleForTransformations(this); GeoElement [] geos = new
		 * GeoElement[] {ray, points[0]}; return geos; }else { AlgoTranslate at
		 * = new AlgoTranslate(cons,getStartPoint(),direction);
		 * cons.removeFromAlgorithmList(at); GeoPoint thirdPoint = (GeoPoint)
		 * at.getResult(); GeoPoint inf = new GeoPoint(cons);
		 * inf.setCoords(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, 1);
		 * 
		 * GeoPoint [] points2 = new GeoPoint[] {thirdPoint,inf}; points2 =
		 * t.transformPoints(points2);
		 * cons.setSuppressLabelCreation(oldSuppressLabelCreation);
		 * AlgoConicPartCircumcircle ae = new AlgoConicPartCircumcircle(cons,
		 * Transform.transformedGeoLabel(this), points[0],
		 * points2[0],points2[1],GeoConicPart.CONIC_PART_ARC); GeoElement arc =
		 * ae.getConicPart(); arc.setVisualStyleForTransformations(this);
		 * GeoElement [] geos = {arc, points[0]}; return geos;
		 * 
		 * }
		 * 
		 * 
		 * 
		 * }
		 */
		else {
			// create LINE
			GeoElement transformedLine = t.getTransformedLine(this);
			transformedLine.setLabel(label);
			GeoElement[] ret = { transformedLine };
			return ret;
		}
	}

	public boolean isAllEndpointsLabelsSet() {
		return startPoint.isLabelSet();
	}

	public boolean isIntersectionPointIncident(GeoPoint p, double eps) {
		if (allowOutlyingIntersections)
			return isOnFullLine(p.getCoordsInD(3), eps);
		else
			return isOnPath(p, eps);
	}

	@Override
	public GeoElement copyInternal(Construction cons) {
		GeoRay3D ray = new GeoRay3D(cons,
				(GeoPointND) startPoint.copyInternal(cons));
		ray.set(this);
		return ray;
	}

	@Override
	public void set(GeoElement geo) {
		super.set(geo);
		if (!geo.isGeoRay())
			return;

		if (!geo.isDefined())
			setUndefined();

		GeoRayND ray = (GeoRayND) geo;

		setKeepTypeOnGeometricTransform(ray.keepsTypeOnGeometricTransform());

		startPoint.set(ray.getStartPoint());
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
	public boolean respectLimitedPath(double parameter){
		return Kernel.isGreaterEqual(parameter, 0);
	}

	

	@Override
	final protected void getCoordsXML(StringBuilder sb) {
		//not needed here
	}
}
