package org.geogebra.common.geogebra3D.kernel3D;

import org.geogebra.common.geogebra3D.kernel3D.geos.GeoAngle3D;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoAxis3D;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoConic3D;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoConicPart3D;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoCurveCartesian3D;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoLine3D;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoPlane3D;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoPoint3D;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoPolyLine3D;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoPolygon3D;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoPolyhedron;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoQuadric3D;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoQuadric3DLimited;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoQuadric3DPart;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoRay3D;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoSegment3D;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoSurfaceCartesian3D;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoVector3D;
import org.geogebra.common.geogebra3D.kernel3D.implicit3D.GeoImplicitCurve3D;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.GeoFactory;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.FunctionNVar;
import org.geogebra.common.kernel.geos.GeoConic;
import org.geogebra.common.kernel.geos.GeoCurveCartesian;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.geos.GeoPolygon;
import org.geogebra.common.kernel.geos.GeoVector;
import org.geogebra.common.kernel.implicit.GeoImplicitCurve;
import org.geogebra.common.kernel.kernelND.GeoConicND;
import org.geogebra.common.kernel.kernelND.GeoConicPartND;
import org.geogebra.common.kernel.kernelND.GeoCurveCartesianND;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.kernel.kernelND.GeoQuadricNDConstants;
import org.geogebra.common.kernel.kernelND.GeoRayND;
import org.geogebra.common.kernel.kernelND.GeoSegmentND;
import org.geogebra.common.kernel.kernelND.GeoSurfaceCartesian2D;
import org.geogebra.common.kernel.kernelND.GeoSurfaceCartesianND;
import org.geogebra.common.kernel.kernelND.GeoVectorND;
import org.geogebra.common.kernel.matrix.CoordSys;
import org.geogebra.common.main.MyError;

/**
 * Produces GeoElements, supports the 3D ones too
 *
 */
public class GeoFactory3D extends GeoFactory {
	/**
	 * Creates a new GeoElement object for the given type string.
	 * 
	 * @param type
	 *            : String as produced by GeoElement.getXMLtypeString()
	 */
	@Override
	public GeoElement createGeoElement(Construction cons1, String type)
			throws MyError {
		switch (type) {
		case "axis3d":
			return new GeoAxis3D(cons1);
		case "angle3d":
			return new GeoAngle3D(cons1);
		case "conic3d":
			return new GeoConic3D(cons1, new CoordSys(2));
		case "curvecartesian3d":
			return new GeoCurveCartesian3D(cons1);
		case "line3d":
			return new GeoLine3D(cons1);
		case "point3d":
			return new GeoPoint3D(cons1);
		case "polygon3d":
			return new GeoPolygon3D(cons1, null);
		case "plane3d":
			return new GeoPlane3D(cons1);
		case "polyline3d":
			return new GeoPolyLine3D(cons1, null);
		case "polyhedron":
			return new GeoPolyhedron(cons1, GeoPolyhedron.TYPE_UNKNOWN);
		case "quadric3d":
		case "quadric":
			GeoQuadric3D quadric = new GeoQuadric3D(cons1);
			quadric.showUndefinedInAlgebraView(true);
			return quadric;
		case "quadric3dpart":
			return new GeoQuadric3DPart(cons1);
		case "quadric3dlimited":
			return new GeoQuadric3DLimited(cons1,
					GeoQuadricNDConstants.QUADRIC_NOT_CLASSIFIED);
		case "ray3d":
			return new GeoRay3D(cons1);
		case "segment3d":
			return new GeoSegment3D(cons1);
		case "surfacecartesian3d":
			return new GeoSurfaceCartesian3D(cons1);
		case "vector3d":
			return new GeoVector3D(cons1);
		default:
			// not a 3D object, now check 2D objects in Kernel
			return super.createGeoElement(cons1, type);
		}
	}

	@Override
	public GeoElement copy3D(GeoElement geo) {

		switch (geo.getGeoClassType()) {

		case POINT:
			return new GeoPoint3D((GeoPointND) geo);

		case VECTOR:
			GeoVector3D v = new GeoVector3D(geo.getConstruction());
			v.set(geo);
			return v;

		case LINE:
		case AXIS:
			GeoElement ret = new GeoLine3D(geo.getConstruction());
			ret.set(geo);
			return ret;
		case SEGMENT:
			ret = new GeoSegment3D(geo.getConstruction());
			ret.set(geo);
			return ret;
		case RAY:
			ret = new GeoRay3D(geo.getConstruction());
			ret.set(geo);
			return ret;

		case POLYGON:
			ret = new GeoPolygon3D(geo.getConstruction());
			ret.set(geo);
			return ret;

		case CONIC:
			return new GeoConic3D((GeoConicND) geo);

		case CONICPART:
			return new GeoConicPart3D((GeoConicPartND) geo);

		case IMPLICIT_POLY:
			return new GeoImplicitCurve3D((GeoImplicitCurve) geo);

		case POLYLINE:
			ret = new GeoPolyLine3D(geo.getConstruction());
			ret.set(geo);
			return ret;

		default:
			return super.copy3D(geo);
		}
	}

	@Override
	public GeoVectorND newVector(int dimension, Construction cons) {
		if (dimension == 3) {
			return new GeoVector3D(cons);
		}
		return new GeoVector(cons);
	}

	@Override
	public GeoElement copyInternal3D(Construction cons1, GeoElement geo) {

		switch (geo.getGeoClassType()) {

		case POLYGON:
			GeoPolygon3D poly = new GeoPolygon3D(cons1, null);
			((GeoPolygon) geo).copyInternal(cons1, poly);
			return poly;

		case CONICPART:
			return new GeoConicPart3D((GeoConicPartND) geo);

		case RAY:
			GeoElement ret = new GeoRay3D(geo.getConstruction(),
					new GeoPoint3D(((GeoRayND) geo).getStartPoint()));
			ret.set(geo);
			return ret;

		case SEGMENT:
			ret = new GeoSegment3D(geo.getConstruction(),
					new GeoPoint3D(((GeoSegmentND) geo).getStartPoint()),
					new GeoPoint3D(((GeoSegmentND) geo).getEndPoint()));
			ret.set(geo);
			return ret;
		case POLYLINE:
			ret = new GeoPolyLine3D(cons1);
			ret.set(geo);
			return ret;

		default:
			return super.copyInternal3D(cons1, geo);
		}
	}

	@Override
	public GeoPointND newPoint(int dimension, Construction cons) {
		return dimension == 3 ? new GeoPoint3D(cons) : new GeoPoint(cons);
	}

	@Override
	public GeoConicND newConic(int dimension, Construction cons) {
		return dimension == 3 ? new GeoConic3D(cons, new CoordSys(2))
				: new GeoConic(cons);
	}

	@Override
	public GeoCurveCartesianND newCurve(int dim, Construction cons) {
		if (dim == 3) {
			return new GeoCurveCartesian3D(cons);
		}
		return new GeoCurveCartesian(cons);
	}

	@Override
	public GeoSurfaceCartesianND newSurface(Construction cons, ExpressionNode point,
			FunctionNVar[] fun) {
		if (fun.length == 2) {
			return new GeoSurfaceCartesian2D(cons, point, fun);
		}
		return new GeoSurfaceCartesian3D(cons, point, fun);
	}
}
