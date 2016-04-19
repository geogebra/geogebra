package org.geogebra.common.plugin;

/**
 * name MUST be in ggbtrans/properties
 *
 */
public enum GeoClass {
	
	ANGLE("Angle", "Angle", 80, 130, false),

	AXIS("Axis", 10, false),

	BOOLEAN("Boolean", 20, false),

	BUTTON("Button", 155, false),

	TEXTFIELD("TextField", 155, false),

	CONIC("Conic", 70, false),

	CONICPART("ConicPart", 70, false),

	FUNCTION("Function", 90, false), INTERVAL("Interval", 90,
			false),

	IMAGE("Image", 20, false), LINE("Line", 100, false),

	LIST("List", 40, false),

	LOCUS("Locus", 130, false),

	NUMERIC("Numeric", "Numeric", 80, 130, false),

	POINT("Point", 140, false),

	POLYGON("Polygon", 50, false),

	RAY("Ray", 110, false),

	SEGMENT("Segment", 110, false),

	TEXT("Text", 150, false),

	VECTOR("Vector", 120, false),

	CURVE_CARTESIAN("CurveCartesian", 90, false),

	CURVE_POLAR("CurvePolar", 90, false),

	IMPLICIT_POLY("ImplicitPoly", 60, false),

	FUNCTION_NVAR("FunctionNVar", 102, false),

	POLYLINE("PolyLine", 51, false),

	PENSTROKE("PenStroke", 15, false),

	SPLINE("CurveCartesian", 90, false),

	TURTLE("Turtle", 140, false),

	CAS_CELL("CasCell", 80, false),

	/* 3Dg geos */
	ANGLE3D("Angle", "Angle3D", 80, true),

	POINT3D("Point", "Point3D", 140, true),

	VECTOR3D("Vector", "Vector3D", 120, true),

	SEGMENT3D("Segment", "Segment3D", 110, true),

	LINE3D("Line", "Line3D", 100, true),

	RAY3D("Ray", "Ray3D", 110, true),

	CONIC3D("Conic", "Conic3D", 70, true),

	CONICSECTION("ConicPart", "Conic3DPart", 70, true),

	AXIS3D("Axis", "Axis3D", 10, true),

	CURVE_CARTESIAN3D("CurveCartesian", "CurveCartesian3D", 90, true),

	POLYGON3D("Polygon", "Polygon3D", 50, true),

	PLANE3D("Plane", "Plane3D", 45, true),

	QUADRIC("Quadric", 46, true),

	QUADRIC_PART("Quadric", "QuadricPart", 46, true),

	QUADRIC_LIMITED("Quadric", "QuadricLimited", 47, true),

	POLYLINE3D("PolyLine", "PolyLine3D", 51, true),

	POLYHEDRON("Polyhedron", 50, true),

	NET("Net", 50, true),

	SURFACECARTESIAN3D("Surface", "SurfaceCartesian3D", 160, true),

	IMPLICIT_SURFACE_3D("ImplicitSurface", "ImplicitSurface3D", 160, true),

	TRIANGULATED_SURFACE_3D("TriangulatedSurface", "TriangulatedSurface3D", 160,
			true),

	CLIPPINGCUBE3D("ClippingCube3D", 160, true),
	
	SPACE("Space", 160, true),
	
	DEFAULT("Default", 160, false);

	/**
	 * name MUST be in ggbtrans/properties
	 */
	public String name, xmlName;
	public boolean is3D;
	public int priority;
	private int independentPriority;

	GeoClass(String name, int priority, boolean is3D) {
		this(name, name, priority, priority, is3D);
	}

	GeoClass(String name, String xmlName, int priority, int independentPriority,
			boolean is3D) {
		this.name = name;
		this.priority = priority;
		this.independentPriority = independentPriority;
		this.xmlName = name;
		this.is3D = is3D;
	}

	GeoClass(String name,  String xmlName, int priority, boolean is3D) {
		this(name, xmlName, priority, priority, is3D);

	}

	public int getPriority(boolean independent) {
		return priority;
	}
}
