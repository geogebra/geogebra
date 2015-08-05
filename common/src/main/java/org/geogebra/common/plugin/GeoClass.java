package org.geogebra.common.plugin;

/**
 * name MUST be in ggbtrans/properties
 *
 */
public enum GeoClass {
	ANGLE("Angle", false), AXIS("Axis", false), BOOLEAN("Boolean", false), BUTTON(
			"Button", false), TEXTFIELD("TextField", false), CONIC("Conic",
			false), CONICPART("ConicPart", false), FUNCTION("Function", false), INTERVAL(
			"Interval", false), FUNCTIONCONDITIONAL("FunctionConditional",
			false), IMAGE("Image", false), LINE("Line", false), LIST("List",
			false), LOCUS("Locus", false), NUMERIC("Numeric", false), POINT(
			"Point", false), POLYGON("Polygon", false), RAY("Ray", false), SEGMENT(
			"Segment", false), TEXT("Text", false), VECTOR("Vector", false), CURVE_CARTESIAN(
			"CurveCartesian", false), CURVE_POLAR("CurvePolar", false), IMPLICIT_POLY(
			"ImplicitPoly", false), IMPLICIT_CURVE("ImplicitCurve", false), FUNCTION_NVAR(
			"FunctionNVar", false), POLYLINE("PolyLine", false), PENSTROKE(
			"PenStroke", false), SPLINE("CurveCartesian", false), TURTLE(
			"Turtle", false), CAS_CELL("CasCell", false),

	ANGLE3D("Angle", "Angle3D", true), POINT3D("Point", "Point3D", true), VECTOR3D(
			"Vector", "Vector3D", true), SEGMENT3D("Segment", "Segment3D", true), LINE3D(
			"Line", "Line3D", true), RAY3D("Ray", "Ray3D", true), CONIC3D(
			"Conic", "Conic3D", true), CONICSECTION("ConicPart", "Conic3DPart",
			true), AXIS3D("Axis", "Axis3D", true), CURVE_CARTESIAN3D(
			"CurveCartesian", "CurveCartesian3D", true), POLYGON3D("Polygon",
			"Polygon3D", true), PLANE3D("Plane", "Plane3D", true), QUADRIC(
			"Quadric", true), QUADRIC_PART("Quadric", "QuadricPart", true), QUADRIC_LIMITED(
			"Quadric", "QuadricLimited", true), POLYLINE3D("PolyLine",
			"PolyLine3D", true), POLYHEDRON("Polyhedron", true), NET("Net",
			true),

	SURFACECARTESIAN3D("Surface", "SurfaceCartesian3D", true),
	
	IMPLICIT_SURFACE_3D("ImplicitSurface3D", "ImplicitSurface3D", true),

	TRIANGULATED_SURFACE_3D("TriangulatedSurface3D", "TriangulatedSurface3D",
			true),

	CLIPPINGCUBE3D("ClippingCube3D", true),

	SPACE("Space", true),

	DEFAULT("Default", false);

	/**
	 * name MUST be in ggbtrans/properties
	 */
	public String name, xmlName;
	public boolean is3D;

	GeoClass(String name, boolean is3D) {
		this.name = name;
		this.xmlName = name;
		this.is3D = is3D;
	}

	GeoClass(String name, String xmlName, boolean is3D) {
		this.name = name;
		this.xmlName = xmlName;
		this.is3D = is3D;

	}
}
