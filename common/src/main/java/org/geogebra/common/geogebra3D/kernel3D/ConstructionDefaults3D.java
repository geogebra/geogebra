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
import org.geogebra.common.main.Feature;
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

	/** default polyhedron type (also used for limited quadrics) */
	public static final int DEFAULT_POLYHEDRON = 3300;
	/** default quadric type */
	public static final int DEFAULT_QUADRIC = 3301;
	/** default function 2 var type */
	public static final int DEFAULT_FUNCTION_NVAR = 3302;

	/** default surface type */
	public static final int DEFAULT_SURFACECARTESIAN3D = 3304;

	/** default net type */
	public static final int DEFAULT_NET = 3305;

	// DEFAULT COLORs

	// curve 3D
	/** default color for 3D curve */
	public static final GColor colCurveCartesian3D = colConic;// new Color(255,
																// 128, 0);

	// plane
	/** default color for 3D planes */
	private static final GColor colPlane3D = GColor.DARK_CYAN; // new Color(99,
																// 219, 219);
	/** default alpha for 3D planes */
	public static final float DEFAULT_PLANE3D_ALPHA = 0.5f;
	/** default grid thickness for 3D planes */
	public static final int DEFAULT_PLANE3D_GRID_THICKNESS = 1;
	/** default fading for 3D planes */
	public static final float DEFAULT_PLANE3D_FADING = 0.10f;

	// polyhedrons
	/** default color for polyhedrons */
	private static final GColor colPolyhedron = colPolygon;// new Color(153, 51,
															// 0);

	/** default alpha for polyhedrons and limited quadrics */
	public static final float DEFAULT_POLYHEDRON_ALPHA = 0.4f;

	// intersection

	public static final GColor colIntersectionCurve = GColor.ORANGE;

	// axes TODO use gui
	public static final GColor colXAXIS = GColor.RED;
	public static final GColor colYAXIS = GColor.GREEN;
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

	@Override
	public void createDefaultGeoElements() {
		super.createDefaultGeoElements();

		// line intersection
		GeoConic3D intersectionCurve = new GeoConic3D(cons);
		intersectionCurve.setLocalVariableLabel("Intersection curve");
		intersectionCurve.setObjColor(colIntersectionCurve);
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
		GeoPolyhedron polyhedron = new GeoPolyhedron(cons);
		polyhedron.setLocalVariableLabel("Polyhedron");
		polyhedron.setObjColor(colPolyhedron);
		polyhedron.setAlphaValue(DEFAULT_POLYHEDRON_ALPHA);
		polyhedron.setDefaultGeoType(DEFAULT_POLYHEDRON);
		defaultGeoElements.put(DEFAULT_POLYHEDRON, polyhedron);

		// polyhedron
		GeoPolyhedronNet polyhedronNet = new GeoPolyhedronNet(cons);
		polyhedronNet.setLocalVariableLabel("Net");
		polyhedronNet.setObjColor(colPolyhedron);
		polyhedronNet.setAlphaValue(DEFAULT_POLYHEDRON_ALPHA);
		polyhedronNet.setDefaultGeoType(DEFAULT_NET);
		defaultGeoElements.put(DEFAULT_NET, polyhedronNet);

		if (cons.getKernel().getApplication().has(Feature.ALL_QUADRICS)) {
			// quadric
			GeoQuadric3D quadric = new GeoQuadric3D(cons);
			quadric.setLocalVariableLabel("Quadric");
			quadric.setObjColor(colQuadric);
			quadric.setAlphaValue(DEFAULT_QUADRIC_ALPHA);
			quadric.setDefaultGeoType(DEFAULT_QUADRIC);
			defaultGeoElements.put(DEFAULT_QUADRIC, quadric);
		}

		// surface
		GeoSurfaceCartesian3D surface = new GeoSurfaceCartesian3D(cons);
		surface.setLocalVariableLabel("surface");
		surface.setObjColor(colQuadric);
		surface.setAlphaValue(DEFAULT_QUADRIC_ALPHA);
		surface.setDefaultGeoType(DEFAULT_SURFACECARTESIAN3D);
		defaultGeoElements.put(DEFAULT_SURFACECARTESIAN3D, surface);
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
		case QUADRIC_LIMITED:
			return DEFAULT_POLYHEDRON;

		case NET:
			return DEFAULT_NET;

		case QUADRIC:
		case QUADRIC_PART:
			return DEFAULT_QUADRIC;

		case SURFACECARTESIAN3D:
			return DEFAULT_SURFACECARTESIAN3D;

		}

		return super.getDefaultType(geo);

	}

	@Override
	protected void getXML(GeoElement geo, StringBuilder sb) {
		if (!geo.isGeoElement3D())
			super.getXML(geo, sb);

		// TODO implement also 3D defaults
	}
}
