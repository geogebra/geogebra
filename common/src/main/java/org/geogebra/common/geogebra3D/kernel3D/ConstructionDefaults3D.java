package org.geogebra.common.geogebra3D.kernel3D;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoAxis3D;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoConic3D;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoCurveCartesian3D;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoLine3D;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoPlane3D;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoPolygon3D;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoPolyhedron;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoPolyhedronNet;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoQuadric3D;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoRay3D;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoSegment3D;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoSurfaceCartesian3D;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.ConstructionDefaults;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunctionNVar;
import org.geogebra.common.kernel.kernelND.GeoQuadricND;
import org.geogebra.common.kernel.kernelND.GeoQuadricNDConstants;
import org.geogebra.common.plugin.GeoClass;

/**
 * 3D subclass for {@link ConstructionDefaults}
 *
 * @author ggb3D
 *
 */
public class ConstructionDefaults3D extends ConstructionDefaults {

	// DEFAULT GeoElement types

	/** default intersection curve 3D type */
	public static final int DEFAULT_INTERSECTION_CURVE = 3150;
	/** default axis 3D type */
	public static final int DEFAULT_AXIS3D = 3103;

	/** default curve 3D type */
	public static final int DEFAULT_CURVECARTESIAN3D = 3106;

	/** default plane 3D type */
	public static final int DEFAULT_PLANE3D = 3200;

	/** default quadric type */
	public static final int DEFAULT_QUADRIC = 3301;

	/** default surface type */
	public static final int DEFAULT_SURFACECARTESIAN3D = 3304;

	/** default net type */
	public static final int DEFAULT_NET = 3305;

	// DEFAULT COLORs

	// curve 3D
	/** default color for 3D curve */
	public static final GColor colCurveCartesian3D = colConic;

	// plane
	/** default color for 3D planes */
	private static final GColor colPlane3D = GColor.DARK_CYAN;

	/** default alpha for 3D planes */
	public static final float DEFAULT_PLANE3D_ALPHA = 0.5f;
	/** default grid thickness for 3D planes */
	public static final int DEFAULT_PLANE3D_GRID_THICKNESS = 0;
	/** default fading for 3D planes */
	public static final float DEFAULT_PLANE3D_FADING = 0.10f;
	
	/** default grid thickness for surfaces */
	public static final int DEFAULT_SURFACE_GRID_THICKNESS = 1;

	// polyhedrons

	/** default alpha for polyhedrons and limited quadrics */
	public static final float DEFAULT_POLYHEDRON_ALPHA = 0.4f;

	/** intersection curve color */
	public static final GColor colIntersectionCurve = GColor.ORANGE;

	// axes TODO use gui
	/** xAxis default color */
	public static final GColor colXAXIS = GColor.RED;
	/** yAxis default color */
	public static final GColor colYAXIS = GColor.GREEN;
	/** zAxis default color */
	public static final GColor colZAXIS = GColor.BLUE;

	/**
	 * default constructor
	 * 
	 * @param cons
	 *            construction
	 */
	public ConstructionDefaults3D(Construction cons) {
		super(cons);
		// Application.debug("ConstructionDefaults3D");
	}

	/** default color for polyhedrons */
	private final GColor colPolyhedron() {
		return getColPolygon();
	}

	@Override
	public void createDefaultGeoElements() {
		cons.setIgnoringNewTypes(true);
		super.createDefaultGeoElements();

		// line intersection
		GeoConic3D intersectionCurve = new GeoConic3D(cons);
		intersectionCurve.setLocalVariableLabel("Intersection curve");
		intersectionCurve.setObjColor(colPolygonG);
		intersectionCurve.setAlphaValue(DEFAULT_POLYGON_ALPHA);
		intersectionCurve.setDefaultGeoType(DEFAULT_INTERSECTION_CURVE);
		defaultGeoElements.put(DEFAULT_INTERSECTION_CURVE, intersectionCurve);

		// axis
		GeoAxis3D axis = new GeoAxis3D(cons);
		// axis.setLineType(EuclidianStyleConstants.LINE_TYPE_DASHED_SHORT);
		axis.setLocalVariableLabel("Axis3D");
		axis.setDefaultGeoType(DEFAULT_AXIS3D);
		defaultGeoElements.put(DEFAULT_AXIS3D, axis);

		// curve
		GeoCurveCartesian3D curve = new GeoCurveCartesian3D(cons);
		curve.setLocalVariableLabel("Curve3D");
		// curve.setLineType(EuclidianStyleConstants.LINE_TYPE_DASHED_LONG);
		curve.setObjColor(colCurveCartesian3D);
		curve.setDefaultGeoType(DEFAULT_CURVECARTESIAN3D);
		defaultGeoElements.put(DEFAULT_CURVECARTESIAN3D, curve);

		// plane
		GeoPlane3D plane = new GeoPlane3D(cons);
		plane.setLocalVariableLabel("Plane3D");
		plane.setObjColor(colPlane3D);
		plane.setAlphaValue(DEFAULT_PLANE3D_ALPHA);
		plane.setLineThickness(DEFAULT_PLANE3D_GRID_THICKNESS);
		plane.setFading(DEFAULT_PLANE3D_FADING);
		// plane.setLineType(EuclidianStyleConstants.LINE_TYPE_DASHED_SHORT);
		plane.setDefaultGeoType(DEFAULT_PLANE3D);
		defaultGeoElements.put(DEFAULT_PLANE3D, plane);

		// polyhedron
		GeoPolyhedron polyhedron = new GeoPolyhedron(cons,
				GeoPolyhedron.TYPE_UNKNOWN);
		polyhedron.setLocalVariableLabel("Polyhedron");
		polyhedron.setObjColor(colPolyhedron());
		polyhedron.setAlphaValue(DEFAULT_POLYHEDRON_ALPHA);
		polyhedron.setDefaultGeoType(DEFAULT_POLYHEDRON);
		defaultGeoElements.put(DEFAULT_POLYHEDRON, polyhedron);

		// archimedean solids (other than tetrahedron and cube)
		GeoPolyhedron archimedean = new GeoPolyhedron(cons,
				GeoPolyhedron.TYPE_UNKNOWN);
		archimedean.setLocalVariableLabel("Archimedean");
		archimedean.setObjColor(colQuadricAndArchimedeanSolid);
		archimedean.setAlphaValue(DEFAULT_POLYHEDRON_ALPHA);
		archimedean.setDefaultGeoType(DEFAULT_ARCHIMDEAN_SOLID);
		defaultGeoElements.put(DEFAULT_ARCHIMDEAN_SOLID, archimedean);

		// pyramid and cone
		GeoPolyhedron pyramid = new GeoPolyhedron(cons,
				GeoPolyhedron.TYPE_PYRAMID);
		pyramid.setLocalVariableLabel("Pyramid");
		pyramid.setObjColor(colPyramidAndCone);
		pyramid.setAlphaValue(DEFAULT_POLYHEDRON_ALPHA);
		pyramid.setDefaultGeoType(DEFAULT_PYRAMID_AND_CONE);
		defaultGeoElements.put(DEFAULT_PYRAMID_AND_CONE, pyramid);

		// prism and cylinder
		GeoPolyhedron prism = new GeoPolyhedron(cons, GeoPolyhedron.TYPE_PRISM);
		prism.setLocalVariableLabel("Prism");
		prism.setObjColor(colPrismAndCylinder);
		prism.setAlphaValue(DEFAULT_POLYHEDRON_ALPHA);
		prism.setDefaultGeoType(DEFAULT_PRISM_AND_CYLINDER);
		defaultGeoElements.put(DEFAULT_PRISM_AND_CYLINDER, prism);

		// polyhedron net
		GeoPolyhedronNet polyhedronNet = new GeoPolyhedronNet(cons);
		polyhedronNet.setLocalVariableLabel("Net");
		polyhedronNet.setObjColor(colPolyhedron());
		polyhedronNet.setAlphaValue(DEFAULT_POLYHEDRON_ALPHA);
		polyhedronNet.setDefaultGeoType(DEFAULT_NET);
		defaultGeoElements.put(DEFAULT_NET, polyhedronNet);

		// quadric
		GeoQuadric3D quadric = new GeoQuadric3D(cons);
		quadric.setLocalVariableLabel("Quadric");
		quadric.setObjColor(colQuadricAndArchimedeanSolid);
		quadric.setAlphaValue(DEFAULT_QUADRIC_ALPHA_NEW);
		quadric.setDefaultGeoType(DEFAULT_QUADRIC);
		defaultGeoElements.put(DEFAULT_QUADRIC, quadric);

		// surface
		GeoSurfaceCartesian3D surface = new GeoSurfaceCartesian3D(cons);
		surface.setLocalVariableLabel("surface");
		surface.setObjColor(colQuadric);
		surface.setAlphaValue(DEFAULT_QUADRIC_ALPHA);
		surface.setLineThickness(DEFAULT_SURFACE_GRID_THICKNESS);
		surface.setDefaultGeoType(DEFAULT_SURFACECARTESIAN3D);
		surface.setAutoColor(true);
		defaultGeoElements.put(DEFAULT_SURFACECARTESIAN3D, surface);
		cons.setIgnoringNewTypes(false);
	}

	@Override
	public int getDefaultType(GeoElement geo) {

		switch (geo.getGeoClassType()) {
		case POINT3D:
			return getDefaultType(geo, GeoClass.POINT);

		case ANGLE3D:
			return getDefaultType(geo, GeoClass.ANGLE);

		case LINE3D:
			if (((GeoLine3D) geo).isIntersection()) {
				return DEFAULT_INTERSECTION_CURVE;
			}
			return getDefaultType(geo, GeoClass.LINE);

		case SEGMENT3D:
			if (((GeoSegment3D) geo).isIntersection()) {
				return DEFAULT_INTERSECTION_CURVE;
			}
			return getDefaultType(geo, GeoClass.SEGMENT);

		case RAY3D:
			if (((GeoRay3D) geo).isIntersection()) {
				return DEFAULT_INTERSECTION_CURVE;
			}
			return getDefaultType(geo, GeoClass.RAY);

		case AXIS3D:
			return DEFAULT_AXIS3D;

		case VECTOR3D:
			return getDefaultType(geo, GeoClass.VECTOR);

		case CONIC3D:
		case CONICSECTION:
			if (((GeoConic3D) geo).isIntersection()) {
				return DEFAULT_INTERSECTION_CURVE;
			}
			return getDefaultType(geo, GeoClass.CONIC);

		case CURVE_CARTESIAN3D:
			return DEFAULT_CURVECARTESIAN3D;

		case PLANE3D:
			return DEFAULT_PLANE3D;

		case POLYGON3D:
			if (((GeoPolygon3D) geo).isIntersection()) {
				return DEFAULT_INTERSECTION_CURVE;
			}
			return getDefaultType(geo, GeoClass.POLYGON);

		case POLYHEDRON:
			switch (((GeoPolyhedron) geo).getPolyhedronType()) {
			case GeoPolyhedron.TYPE_PYRAMID:
			case GeoPolyhedron.TYPE_TETRAHEDRON:
				return DEFAULT_PYRAMID_AND_CONE;
			case GeoPolyhedron.TYPE_PRISM:
			case GeoPolyhedron.TYPE_CUBE:
				return DEFAULT_PRISM_AND_CYLINDER;
			case GeoPolyhedron.TYPE_OCTAHEDRON:
			case GeoPolyhedron.TYPE_DODECAHEDRON:
			case GeoPolyhedron.TYPE_ICOSAHEDRON:
				return DEFAULT_ARCHIMDEAN_SOLID;
			default:
				return DEFAULT_POLYHEDRON;
			}
		case QUADRIC_LIMITED:
			switch (((GeoQuadricND) geo).getType()) {
			case GeoQuadricNDConstants.QUADRIC_CONE:
				return DEFAULT_PYRAMID_AND_CONE;
			case GeoQuadricNDConstants.QUADRIC_CYLINDER:
				return DEFAULT_PRISM_AND_CYLINDER;
			default:
				return DEFAULT_POLYHEDRON;
			}
		case NET:
			return DEFAULT_NET;

		case QUADRIC:
		case QUADRIC_PART:
			return DEFAULT_QUADRIC;
		case SURFACECARTESIAN:
		case SURFACECARTESIAN3D:
			return DEFAULT_SURFACECARTESIAN3D;

		}

		return super.getDefaultType(geo);

	}

	@Override
	protected int getDefaultTypeForFunctionNVar(GeoFunctionNVar geo) {
		if (geo.isFun2Var()) {
			return DEFAULT_SURFACECARTESIAN3D;
		}
		return super.getDefaultTypeForFunctionNVar(geo);
	}

}
