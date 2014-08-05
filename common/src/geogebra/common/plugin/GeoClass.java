package geogebra.common.plugin;


/**
 * name MUST be in ggbtrans/properties
 *
 */
public enum GeoClass {
	ANGLE("Angle"), AXIS("Axis"), BOOLEAN("Boolean"), BUTTON("Button"), TEXTFIELD("TextField"), CONIC("Conic"), CONICPART("ConicPart"), FUNCTION("Function"), INTERVAL("Interval"), FUNCTIONCONDITIONAL("FunctionConditional"), IMAGE("Image"), LINE("Line"), LIST("List"), LOCUS("Locus"), NUMERIC("Numeric"), POINT("Point"), POLYGON("Polygon"), RAY("Ray"), SEGMENT("Segment"), TEXT("Text"), VECTOR("Vector"), CURVE_CARTESIAN("CurveCartesian"), CURVE_POLAR("CurvePolar"), IMPLICIT_POLY("ImplicitPoly"), FUNCTION_NVAR("FunctionNVar"), POLYLINE("PolyLine"), PENSTROKE("PenStroke"), SPLINE("CurveCartesian"),
	TURTLE("Turtle"),
	CAS_CELL("CasCell"),

	ANGLE3D("Angle", "Angle3D"), POINT3D("Point", "Point3D"), VECTOR3D("Vector", "Vector3D"), SEGMENT3D("Segment", "Segment3D"), LINE3D("Line", "Line3D"), RAY3D("Ray", "Ray3D"), CONIC3D("Conic", "Conic3D"), CONICSECTION("ConicPart", "Conic3DPart"), AXIS3D("Axis", "Axis3D"), CURVE_CARTESIAN3D("CurveCartesian", "CurveCartesian3D"), POLYGON3D("Polygon", "Polygon3D"), PLANE3D("Plane", "Plane3D"), QUADRIC("Quadric"), QUADRIC_PART("Quadric", "QuadricPart"), QUADRIC_LIMITED("Quadric", "QuadricLimited"), POLYLINE3D("PolyLine", "PolyLine3D"), POLYHEDRON("Polyhedron"), NET("Net"),

	SURFACECARTESIAN3D("Surface", "SurfaceCartesian3D"),
	
	CLIPPINGCUBE3D("ClippingCube3D"),

	SPACE("Space"),

	DEFAULT("Default");
	
	/**
	 * name MUST be in ggbtrans/properties
	 */
	public String name, xmlName;

	GeoClass(String name) {
		this.name = name;
		this.xmlName = name;
	}
	GeoClass(String name, String xmlName) {
		this.name = name;
		this.xmlName = xmlName;
	}
}
