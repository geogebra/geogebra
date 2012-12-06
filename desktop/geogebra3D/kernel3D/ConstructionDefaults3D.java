package geogebra3D.kernel3D;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.ConstructionDefaults;
import geogebra.common.kernel.geos.GeoAngle;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.plugin.EuclidianStyleConstants;

import java.awt.Color;

/**
 * 3D subclass for {@link ConstructionDefaults}
 *
 * @author ggb3D
 *
 */
public class ConstructionDefaults3D extends ConstructionDefaults {

	// DEFAULT GeoElement types	
	/** default angle 3D type */	
	public static final int DEFAULT_ANGLE3D = 3001;
	
	/** default point 3D type */	
	public static final int DEFAULT_POINT3D_FREE = 3010;
	/** default dependant point 3D type */	
	public static final int DEFAULT_POINT3D_DEPENDENT = 3011;
	/** default point 3D on path type */	
	public static final int DEFAULT_POINT3D_ON_PATH = 3012;
	/** default point 3D in region type */	
	public static final int DEFAULT_POINT3D_IN_REGION = 3013;

	/** default line 3D type */	
	public static final int DEFAULT_LINE3D = 3100;
	public static final int DEFAULT_LINE3D_INTERSECTION = 3150;
	/** default segment 3D type */	
	public static final int DEFAULT_SEGMENT3D = 3101;
	public static final int DEFAULT_SEGMENT3D_INTERSECTION = 3151;
	/** default ray 3D type */	
	public static final int DEFAULT_RAY3D = 3102;
	public static final int DEFAULT_RAY3D_INTERSECTION = 3152;
	/** default axis 3D type */	
	public static final int DEFAULT_AXIS3D = 3103;
	/** default vector 3D type */	
	public static final int DEFAULT_VECTOR3D = 3104;
	/** default conic 3D type */	
	public static final int DEFAULT_CONIC3D = 3105;

	public static final int DEFAULT_CONIC3D_INTERSECTION = 3155;
	/** default curve 3D type */	
	public static final int DEFAULT_CURVECARTESIAN3D = 3106;
		
	/** default plane 3D type */	
	public static final int DEFAULT_PLANE3D = 3200;
	/** default polygon 3D type */	
	public static final int DEFAULT_POLYGON3D = 3201;	
	
	/** default polyhedron type */
	public static final int DEFAULT_POLYHEDRON = 3300;
	/** default quadric type */
	public static final int DEFAULT_QUADRIC = 3301;
	/** default function 2 var type */
	public static final int DEFAULT_FUNCTION_NVAR = 3302;
	/** default quadric type */
	public static final int DEFAULT_QUADRIC_LIMITED = 3303;

	/** default surface type */
	public static final int DEFAULT_SURFACECARTESIAN3D = 3304;
	
	
	// DEFAULT COLORs
	
	// vector 3D
	/** default color for 3D vectors */
	public static final Color colVector = Color.DARK_GRAY;
	
	// conic 3D
	/** default color for 3D conics */
	public static final Color colConic3D = new Color(0, 128, 128);//new Color(255, 128, 0);
	
	// curve 3D
	/** default color for 3D curve */
	public static final Color colCurveCartesian3D = colConic3D;//new Color(255, 128, 0);
	
	// polygon 3D
	/** default color for 3D polygons */
	public static final Color colPolygon3D = geogebra.awt.GColorD.getAwtColor(colPolygon);
	/** default alpha for 3D polygons*/
	public static final float DEFAULT_POLYGON3D_ALPHA = DEFAULT_POLYGON_ALPHA;

	// plane 
	/** default color for 3D planes */
	private static final Color colPlane3D = new Color(99, 219, 219);
	/** default alpha for 3D planes*/
	public static final float DEFAULT_PLANE3D_ALPHA = 0.5f;
	/** default grid thickness for 3D planes*/
	public static final int DEFAULT_PLANE3D_GRID_THICKNESS = 1;
	/** default fading for 3D planes*/
	public static final float DEFAULT_PLANE3D_FADING = 0.10f;
	
	// polyhedrons	
	/** default color for polyhedrons */
	private static final Color colPolyhedron = geogebra.awt.GColorD.getAwtColor(colPolygon);//new Color(153, 51, 0);

	
	/** default alpha for quadrics*/
	public static final float DEFAULT_QUADRIC_LIMITED_ALPHA = 0.5f;
	
	// intersection
	
	public static final Color colIntersectionCurve = Color.orange;
	public static final Color colIntersectionLine = Color.red;
	
	
	// axes TODO use gui
	public static final Color colXAXIS = Color.red;
	public static final Color colYAXIS = Color.green;
	public static final Color colZAXIS = Color.blue;	
	
	
	/**
	 * default constructor
	 * @param cons construction
	 */
	public ConstructionDefaults3D(Construction cons) {
		super(cons);
		//Application.debug("ConstructionDefaults3D");
	}
	
	@Override
	public void createDefaultGeoElements() {
		super.createDefaultGeoElements();
		
		// angle
		GeoAngle3D angle = new GeoAngle3D(cons);	
		angle.setVisualStyle(super.getDefaultGeo(DEFAULT_ANGLE));
		angle.setAngleStyle(GeoAngle.ANGLE_ISNOTREFLEX);
		defaultGeoElements.put(DEFAULT_ANGLE3D, angle);
	
		// free point
		GeoPoint3D freePoint = new GeoPoint3D(cons);	
		freePoint.setPointSize(EuclidianStyleConstants.DEFAULT_POINT_SIZE);
		freePoint.setPointStyle(EuclidianStyleConstants.POINT_STYLE_DOT);
		freePoint.setLocalVariableLabel("Point3D" + strFree);
		freePoint.setObjColor(colPoint);
		freePoint.setCartesian3D();
		//freePoint.setLabelOffset(5, -5);
		defaultGeoElements.put(DEFAULT_POINT3D_FREE, freePoint);
		
		// dependent point
		GeoPoint3D depPoint = new GeoPoint3D(cons);	
		depPoint.setPointSize(EuclidianStyleConstants.DEFAULT_POINT_SIZE);
		depPoint.setPointStyle(EuclidianStyleConstants.POINT_STYLE_DOT);
		depPoint.setLocalVariableLabel("Point3D" + strDependent);
		depPoint.setObjColor(colDepPoint);
		depPoint.setCartesian3D();
		//depPoint.setLabelOffset(5, -5);
		defaultGeoElements.put(DEFAULT_POINT3D_DEPENDENT, depPoint);
		
		// point on path
		GeoPoint3D pathPoint = new GeoPoint3D(cons);	
		pathPoint.setPointSize(EuclidianStyleConstants.DEFAULT_POINT_SIZE);
		pathPoint.setPointStyle(EuclidianStyleConstants.POINT_STYLE_DOT);
		pathPoint.setLocalVariableLabel("Point3DOn");
		pathPoint.setObjColor(colPathPoint);
		pathPoint.setCartesian3D();
		//pathPoint.setLabelOffset(5, -5);
		defaultGeoElements.put(DEFAULT_POINT3D_ON_PATH, pathPoint);
		
		// point in region
		GeoPoint3D regionPoint = new GeoPoint3D(cons);	
		regionPoint.setPointSize(EuclidianStyleConstants.DEFAULT_POINT_SIZE);
		regionPoint.setPointStyle(EuclidianStyleConstants.POINT_STYLE_DOT);
		regionPoint.setLocalVariableLabel("Point3DInRegion");
		regionPoint.setObjColor(colRegionPoint);
		regionPoint.setCartesian3D();
		//regionPoint.setLabelOffset(5, -5);
		defaultGeoElements.put(DEFAULT_POINT3D_IN_REGION, regionPoint);
		
		// line
		GeoLine3D line = new GeoLine3D(cons);
		//line.setLineType(EuclidianStyleConstants.LINE_TYPE_DASHED_LONG);
		line.setLocalVariableLabel("Line3D");
		defaultGeoElements.put(DEFAULT_LINE3D, line);		
		
		// segment
		GeoSegment3D segment = new GeoSegment3D(cons);
		//segment.setLineType(EuclidianStyleConstants.LINE_TYPE_DASHED_LONG);
		segment.setLocalVariableLabel("Segment3D");
		defaultGeoElements.put(DEFAULT_SEGMENT3D, segment);		

		// ray
		GeoRay3D ray = new GeoRay3D(cons);
		//ray.setLineType(EuclidianStyleConstants.LINE_TYPE_DASHED_LONG);
		ray.setLocalVariableLabel("Ray3D");
		defaultGeoElements.put(DEFAULT_RAY3D, ray);		
	
		// line intersection
		GeoLine3D lineIntersection = new GeoLine3D(cons);
		//line.setLineType(EuclidianStyleConstants.LINE_TYPE_DASHED_LONG);
		lineIntersection.setLocalVariableLabel("Line3D" + strIntersection);
		lineIntersection.setObjColor(new geogebra.awt.GColorD(colIntersectionLine));
		defaultGeoElements.put(DEFAULT_LINE3D_INTERSECTION, lineIntersection);		
		
		// segment intersection
		GeoSegment3D segmentIntersection = new GeoSegment3D(cons);
		//segment.setLineType(EuclidianStyleConstants.LINE_TYPE_DASHED_LONG);
		segmentIntersection.setLocalVariableLabel("Segment3D" + strIntersection);
		lineIntersection.setObjColor(new geogebra.awt.GColorD(colIntersectionLine));
		defaultGeoElements.put(DEFAULT_SEGMENT3D_INTERSECTION, segmentIntersection);		

		// ray intersection
		GeoRay3D rayIntersection = new GeoRay3D(cons);
		//ray.setLineType(EuclidianStyleConstants.LINE_TYPE_DASHED_LONG);
		rayIntersection.setLocalVariableLabel("Ray3D" + strIntersection);
		lineIntersection.setObjColor(new geogebra.awt.GColorD(colIntersectionLine));
		defaultGeoElements.put(DEFAULT_RAY3D_INTERSECTION, rayIntersection);		
		
		
		// axis
		GeoAxis3D axis = new GeoAxis3D(cons);
		//axis.setLineType(EuclidianStyleConstants.LINE_TYPE_DASHED_SHORT);
		axis.setLocalVariableLabel("Axis3D");
		defaultGeoElements.put(DEFAULT_AXIS3D, axis);		
		
		// vector 3D
		GeoVector3D vector = new GeoVector3D(cons);
		//vector.setLineType(EuclidianStyleConstants.LINE_TYPE_DASHED_LONG);
		vector.setObjColor(new geogebra.awt.GColorD(colVector));
		vector.setLocalVariableLabel("Vector3D");
		defaultGeoElements.put(DEFAULT_VECTOR3D, vector);		
		
		
		// conic
		GeoConic3D conic = new GeoConic3D(cons);	
		conic.setLocalVariableLabel("Conic3D");
		//conic.setLineType(EuclidianStyleConstants.LINE_TYPE_DASHED_LONG);
		conic.setObjColor(new geogebra.awt.GColorD(colConic3D));
		defaultGeoElements.put(DEFAULT_CONIC3D, conic);
		
		
		// conic intersection
		GeoConic3D conicIntersection = new GeoConic3D(cons);	
		conicIntersection.setLocalVariableLabel("Conic3D" + strIntersection);
		conicIntersection.setObjColor(new geogebra.awt.GColorD(colIntersectionCurve));
		defaultGeoElements.put(DEFAULT_CONIC3D_INTERSECTION, conicIntersection);
		
		
		// curve
		GeoCurveCartesian3D curve = new GeoCurveCartesian3D(cons);	
		curve.setLocalVariableLabel("Curve3D");
		//curve.setLineType(EuclidianStyleConstants.LINE_TYPE_DASHED_LONG);
		curve.setObjColor(new geogebra.awt.GColorD(colCurveCartesian3D));
		defaultGeoElements.put(DEFAULT_CURVECARTESIAN3D, curve);
		
			
		// plane
		GeoPlane3D plane = new GeoPlane3D(cons);	
		plane.setLocalVariableLabel("Plane3D");
		plane.setObjColor(new geogebra.awt.GColorD(colPlane3D));
		plane.setAlphaValue(DEFAULT_PLANE3D_ALPHA);
		plane.setLineThickness(DEFAULT_PLANE3D_GRID_THICKNESS);
		plane.setFading(DEFAULT_PLANE3D_FADING);
		//plane.setLineType(EuclidianStyleConstants.LINE_TYPE_DASHED_SHORT);
		defaultGeoElements.put(DEFAULT_PLANE3D, plane);
		
		// polygon
		GeoPolygon3D polygon = new GeoPolygon3D(cons, null, null, false);	
		polygon.setLocalVariableLabel("Polygon3D");
		polygon.setObjColor(new geogebra.awt.GColorD(colPolygon3D));
		polygon.setAlphaValue(DEFAULT_POLYGON3D_ALPHA);
		defaultGeoElements.put(DEFAULT_POLYGON3D, polygon);
	
		
		// polyhedron
		GeoPolyhedron polyhedron = new GeoPolyhedron(cons);	
		polyhedron.setLocalVariableLabel("Polyhedron");
		polyhedron.setObjColor(new geogebra.awt.GColorD(colPolyhedron));
		polyhedron.setAlphaValue(DEFAULT_POLYGON3D_ALPHA);
		defaultGeoElements.put(DEFAULT_POLYHEDRON, polyhedron);
		
		// quadric
		GeoQuadric3D quadric = new GeoQuadric3D(cons);	
		quadric.setLocalVariableLabel("Quadric");
		quadric.setObjColor(colQuadric);
		quadric.setAlphaValue(DEFAULT_QUADRIC_ALPHA);
		defaultGeoElements.put(DEFAULT_QUADRIC, quadric);
		
		// limited quadric
		GeoQuadric3D limitedQuadric = new GeoQuadric3D(cons);	
		limitedQuadric.setLocalVariableLabel("QuadricLimited");
		limitedQuadric.setObjColor(new geogebra.awt.GColorD(colPolyhedron));
		limitedQuadric.setAlphaValue(DEFAULT_QUADRIC_LIMITED_ALPHA);
		defaultGeoElements.put(DEFAULT_QUADRIC_LIMITED, limitedQuadric);

		
		
	
		// surface
		GeoSurfaceCartesian3D surface = new GeoSurfaceCartesian3D(cons);	
		surface.setLocalVariableLabel("surface");
		surface.setObjColor(colQuadric);
		surface.setAlphaValue(DEFAULT_QUADRIC_ALPHA);
		defaultGeoElements.put(DEFAULT_SURFACECARTESIAN3D, surface);
	}
		
	@Override
	public int getDefaultType(GeoElement geo){

		switch (geo.getGeoClassType()) {
		case POINT3D:
			if (geo.isIndependent()) {
				return DEFAULT_POINT3D_FREE;
			}
			GeoPoint3D p = (GeoPoint3D) geo;
			if (p.hasPath())
				return DEFAULT_POINT3D_ON_PATH;	
			else if (p.hasRegion())
				return DEFAULT_POINT3D_IN_REGION;				
			else
				return DEFAULT_POINT3D_DEPENDENT;
			
		case ANGLE3D:
			return DEFAULT_ANGLE3D;
			
		case LINE3D: 
			if (((GeoLine3D)geo).isIntersection())
				return DEFAULT_LINE3D_INTERSECTION;
			return DEFAULT_LINE3D;
		case SEGMENT3D: 
			if (((GeoSegment3D)geo).isIntersection())
				return DEFAULT_SEGMENT3D_INTERSECTION;
			return DEFAULT_SEGMENT3D;
		case RAY3D: 
			if (((GeoRay3D)geo).isIntersection())
				return DEFAULT_RAY3D_INTERSECTION;
			return DEFAULT_RAY3D;
		case AXIS3D: 
			return DEFAULT_AXIS3D;
		case VECTOR3D: 
			return DEFAULT_VECTOR3D;		
		case CONIC3D: 
			if (((GeoConic3D)geo).isIntersection())
				return DEFAULT_CONIC3D_INTERSECTION;
			return DEFAULT_CONIC3D;
		case CURVECARTESIAN3D: 
			return DEFAULT_CURVECARTESIAN3D;	
		case PLANE3D: 
			return DEFAULT_PLANE3D;
		case POLYGON3D: 
			return DEFAULT_POLYGON3D;			
		case POLYHEDRON:
			return DEFAULT_POLYHEDRON;
		case QUADRIC:
		case QUADRIC_PART:
			return DEFAULT_QUADRIC;
		case QUADRIC_LIMITED:
			return DEFAULT_QUADRIC_LIMITED;
		case SURFACECARTESIAN3D:
			return DEFAULT_SURFACECARTESIAN3D;		
			
		default:
			return super.getDefaultType(geo);
		}
	}

}
