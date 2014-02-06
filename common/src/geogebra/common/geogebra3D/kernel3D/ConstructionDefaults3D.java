package geogebra.common.geogebra3D.kernel3D;

import geogebra.common.awt.GColor;
import geogebra.common.geogebra3D.kernel3D.geos.GeoAxis3D;
import geogebra.common.geogebra3D.kernel3D.geos.GeoConic3D;
import geogebra.common.geogebra3D.kernel3D.geos.GeoCurveCartesian3D;
import geogebra.common.geogebra3D.kernel3D.geos.GeoLine3D;
import geogebra.common.geogebra3D.kernel3D.geos.GeoPlane3D;
import geogebra.common.geogebra3D.kernel3D.geos.GeoPolyhedron;
import geogebra.common.geogebra3D.kernel3D.geos.GeoQuadric3D;
import geogebra.common.geogebra3D.kernel3D.geos.GeoRay3D;
import geogebra.common.geogebra3D.kernel3D.geos.GeoSegment3D;
import geogebra.common.geogebra3D.kernel3D.geos.GeoSurfaceCartesian3D;
import geogebra.common.kernel.Construction;
import geogebra.common.kernel.ConstructionDefaults;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.plugin.GeoClass;

/**
 * 3D subclass for {@link ConstructionDefaults}
 *
 * @author ggb3D
 *
 */
public class ConstructionDefaults3D extends ConstructionDefaults {

	// DEFAULT GeoElement types	
	

	/** default intersection line 3D type */	
	public static final int DEFAULT_LINE3D_INTERSECTION = 3150;
	/** default segment 3D type */	
	public static final int DEFAULT_SEGMENT3D_INTERSECTION = 3151;
	/** default ray 3D type */	
	public static final int DEFAULT_RAY3D_INTERSECTION = 3152;
	/** default axis 3D type */	
	public static final int DEFAULT_AXIS3D = 3103;

	public static final int DEFAULT_CONIC3D_INTERSECTION = 3155;
	/** default curve 3D type */	
	public static final int DEFAULT_CURVECARTESIAN3D = 3106;
		
	/** default plane 3D type */	
	public static final int DEFAULT_PLANE3D = 3200;
	
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
	
	
	// curve 3D
	/** default color for 3D curve */
	public static final GColor colCurveCartesian3D = colConic;//new Color(255, 128, 0);
	

	// plane 
	/** default color for 3D planes */
	private static final GColor colPlane3D = GColor.darkCyan; //new Color(99, 219, 219);
	/** default alpha for 3D planes*/
	public static final float DEFAULT_PLANE3D_ALPHA = 0.5f;
	/** default grid thickness for 3D planes*/
	public static final int DEFAULT_PLANE3D_GRID_THICKNESS = 1;
	/** default fading for 3D planes*/
	public static final float DEFAULT_PLANE3D_FADING = 0.10f;
	
	// polyhedrons	
	/** default color for polyhedrons */
	private static final GColor colPolyhedron = colPolygon;//new Color(153, 51, 0);

	
	/** default alpha for quadrics*/
	public static final float DEFAULT_QUADRIC_LIMITED_ALPHA = 0.5f;
	
	// intersection
	
	public static final GColor colIntersectionCurve = GColor.orange;
	public static final GColor colIntersectionLine = GColor.red;
	
	
	// axes TODO use gui
	public static final GColor colXAXIS = GColor.red;
	public static final GColor colYAXIS = GColor.green;
	public static final GColor colZAXIS = GColor.blue;	
	
	
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
		
		// line intersection
		GeoLine3D lineIntersection = new GeoLine3D(cons);
		//line.setLineType(EuclidianStyleConstants.LINE_TYPE_DASHED_LONG);
		lineIntersection.setLocalVariableLabel("Line3D" + strIntersection);
		lineIntersection.setObjColor(colIntersectionLine);
		defaultGeoElements.put(DEFAULT_LINE3D_INTERSECTION, lineIntersection);		
		
		// segment intersection
		GeoSegment3D segmentIntersection = new GeoSegment3D(cons);
		//segment.setLineType(EuclidianStyleConstants.LINE_TYPE_DASHED_LONG);
		segmentIntersection.setLocalVariableLabel("Segment3D" + strIntersection);
		lineIntersection.setObjColor(colIntersectionLine);
		defaultGeoElements.put(DEFAULT_SEGMENT3D_INTERSECTION, segmentIntersection);		

		// ray intersection
		GeoRay3D rayIntersection = new GeoRay3D(cons);
		//ray.setLineType(EuclidianStyleConstants.LINE_TYPE_DASHED_LONG);
		rayIntersection.setLocalVariableLabel("Ray3D" + strIntersection);
		lineIntersection.setObjColor(colIntersectionLine);
		defaultGeoElements.put(DEFAULT_RAY3D_INTERSECTION, rayIntersection);		
		
		
		// axis
		GeoAxis3D axis = new GeoAxis3D(cons);
		//axis.setLineType(EuclidianStyleConstants.LINE_TYPE_DASHED_SHORT);
		axis.setLocalVariableLabel("Axis3D");
		defaultGeoElements.put(DEFAULT_AXIS3D, axis);		
		
		
		// conic intersection
		GeoConic3D conicIntersection = new GeoConic3D(cons);	
		conicIntersection.setLocalVariableLabel("Conic3D" + strIntersection);
		conicIntersection.setObjColor(colIntersectionCurve);
		defaultGeoElements.put(DEFAULT_CONIC3D_INTERSECTION, conicIntersection);
		
		
		// curve
		GeoCurveCartesian3D curve = new GeoCurveCartesian3D(cons);	
		curve.setLocalVariableLabel("Curve3D");
		//curve.setLineType(EuclidianStyleConstants.LINE_TYPE_DASHED_LONG);
		curve.setObjColor(colCurveCartesian3D);
		defaultGeoElements.put(DEFAULT_CURVECARTESIAN3D, curve);
		
			
		// plane
		GeoPlane3D plane = new GeoPlane3D(cons);	
		plane.setLocalVariableLabel("Plane3D");
		plane.setObjColor(colPlane3D);
		plane.setAlphaValue(DEFAULT_PLANE3D_ALPHA);
		plane.setLineThickness(DEFAULT_PLANE3D_GRID_THICKNESS);
		plane.setFading(DEFAULT_PLANE3D_FADING);
		//plane.setLineType(EuclidianStyleConstants.LINE_TYPE_DASHED_SHORT);
		defaultGeoElements.put(DEFAULT_PLANE3D, plane);
		
		
		// polyhedron
		GeoPolyhedron polyhedron = new GeoPolyhedron(cons);	
		polyhedron.setLocalVariableLabel("Polyhedron");
		polyhedron.setObjColor(colPolyhedron);
		polyhedron.setAlphaValue(DEFAULT_POLYGON_ALPHA);
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
		limitedQuadric.setObjColor(colPolyhedron);
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
			return getDefaultType(geo, GeoClass.POINT);
			
		case ANGLE3D:
			return getDefaultType(geo, GeoClass.ANGLE);
			
		case LINE3D: 
			if (((GeoLine3D)geo).isIntersection())
				return DEFAULT_LINE3D_INTERSECTION;
			return getDefaultType(geo, GeoClass.LINE);
			
		case SEGMENT3D: 
			if (((GeoSegment3D)geo).isIntersection())
				return DEFAULT_SEGMENT3D_INTERSECTION;
			return getDefaultType(geo, GeoClass.SEGMENT);
			
		case RAY3D: 
			if (((GeoRay3D)geo).isIntersection())
				return DEFAULT_RAY3D_INTERSECTION;
			return getDefaultType(geo, GeoClass.RAY);
			
		case AXIS3D: 
			return DEFAULT_AXIS3D;
			
		case VECTOR3D: 
			return getDefaultType(geo, GeoClass.VECTOR);
			
		case CONIC3D: 
			if (((GeoConic3D)geo).isIntersection())
				return DEFAULT_CONIC3D_INTERSECTION;
			return getDefaultType(geo, GeoClass.CONIC);
			
		case CURVECARTESIAN3D: 
			return DEFAULT_CURVECARTESIAN3D;	
			
		case PLANE3D: 
			return DEFAULT_PLANE3D;
			
		case POLYGON3D: 
			return getDefaultType(geo, GeoClass.POLYGON);	
			
		case POLYHEDRON:
			return DEFAULT_POLYHEDRON;
			
		case QUADRIC:
		case QUADRIC_PART:
			return DEFAULT_QUADRIC;
			
		case QUADRIC_LIMITED:
			return DEFAULT_QUADRIC_LIMITED;
			
		case SURFACECARTESIAN3D:
			return DEFAULT_SURFACECARTESIAN3D;	
			
		}
		
		
		return super.getDefaultType(geo);
		
	}

	
	@Override
	protected void getXML(GeoElement geo, StringBuilder sb){
		if (!geo.isGeoElement3D())
			super.getXML(geo, sb);
		
		//TODO implement also 3D defaults
	}
}
