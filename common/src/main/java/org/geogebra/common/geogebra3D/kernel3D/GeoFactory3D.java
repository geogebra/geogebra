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

		switch (type.charAt(0)) {
		case 'a':
			if ("axis3d".equals(type)) {
				return new GeoAxis3D(cons1);
			} else if ("angle3d".equals(type)) {
				return new GeoAngle3D(cons1);
			}

		case 'c':
			if ("conic3d".equals(type)) {
				return new GeoConic3D(cons1, new CoordSys(2));
			} else if ("curvecartesian3d".equals(type)) {
				return new GeoCurveCartesian3D(cons1);
			}

		case 'l':
			if ("line3d".equals(type)) {
				return new GeoLine3D(cons1);
			}

		case 'p':
			if ("point3d".equals(type)) {
				return new GeoPoint3D(cons1);
			} else if ("polygon3d".equals(type)) {
				return new GeoPolygon3D(cons1, null);
			} else if ("plane3d".equals(type)) {
				return new GeoPlane3D(cons1);
			} else if ("polyline3d".equals(type)) {
				return new GeoPolyLine3D(cons1, null);
			} else if ("polyhedron".equals(type)) {
				return new GeoPolyhedron(cons1, GeoPolyhedron.TYPE_UNKNOWN);
			}

		case 'q':
			if ("quadric3d".equals(type) || "quadric".equals(type)) {
				return new GeoQuadric3D(cons1);
			} else if ("quadric3dpart".equals(type)) {
				return new GeoQuadric3DPart(cons1);
			} else if ("quadric3dlimited".equals(type)) {
				return new GeoQuadric3DLimited(cons1,
						GeoQuadricNDConstants.QUADRIC_NOT_CLASSIFIED);
			}

		case 'r':
			if ("ray3d".equals(type)) {
				return new GeoRay3D(cons1);
			}

		case 's':
			if ("segment3d".equals(type)) {
				return new GeoSegment3D(cons1);
			}
			if ("surfacecartesian3d".equals(type)) {
				return new GeoSurfaceCartesian3D(cons1);
			}

		case 'v':
			if ("vector3d".equals(type)) {
				return new GeoVector3D(cons1);
			}

		}

		// not a 3D object, now check 2D objects in Kernel
		return super.createGeoElement(cons1, type);
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
}
