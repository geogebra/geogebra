package org.geogebra.common.plugin;

/**
 * name MUST be in ggbtrans/properties
 */
public enum GeoClass {

	/** ANGLE */
	ANGLE("Angle", "Angle", 80, 130, false),

	/** AXIS */
	AXIS("Axis", 10, false),

	/** BOOLEAN */
	BOOLEAN("Boolean", 20, false),

	/** BUTTON */
	BUTTON("Button", 155, false),

	/** TEXTFIELD */
	TEXTFIELD("TextField", 155, false),

	/** CONIC */
	CONIC("Conic", 70, false),

	/** CONICPART */
	CONICPART("ConicPart", 70, false),

	/** FUNCTION */
	FUNCTION("Function", 90, false),
	/** INTERVAL */
	INTERVAL("Interval", 90, false),

	/** IMAGE */
	IMAGE("Image", 20, false),
	/** LINE */
	LINE("Line", 100, false),

	/** LIST */
	LIST("List", 40, false),

	/** LOCUS */
	LOCUS("Locus", 130, false),

	/** NUMERIC */
	NUMERIC("Numeric", "Numeric", 80, 130, false),

	/** POINT */
	POINT("Point", 140, false),

	/** POLYGON */
	POLYGON("Polygon", 50, false),

	/** RAY */
	RAY("Ray", 110, false),

	/** SEGMENT */
	SEGMENT("Segment", 110, false),

	/** TEXT */
	TEXT("Text", 150, false),

	/** Formula */
	FORMULA("Formula", 150, false),

	/** VECTOR */
	VECTOR("Vector", 120, false),

	/** CURVE_CARTESIAN */
	CURVE_CARTESIAN("CurveCartesian", 90, false),

	/** IMPLICIT_POLY */
	IMPLICIT_POLY("ImplicitPoly", 60, false),

	/** FUNCTION_NVAR */
	FUNCTION_NVAR("FunctionNVar", 102, false),

	/** POLYLINE */
	POLYLINE("PolyLine", 51, false),

	/** PENSTROKE */
	PENSTROKE("PenStroke", 160, false),

	/** TURTLE */
	TURTLE("Turtle", 140, false),

	/** CAS_CELL */
	CAS_CELL("CasCell", 80, false),

	/* 3Dg geos */
	/** ANGLE */
	ANGLE3D("Angle", "Angle3D", 80, true),

	/** POINT */
	POINT3D("Point", "Point3D", 140, true),

	/** VECTOR */
	VECTOR3D("Vector", "Vector3D", 120, true),

	/** SEGMENT */
	SEGMENT3D("Segment", "Segment3D", 110, true),

	/** LINE */
	LINE3D("Line", "Line3D", 100, true),

	/** RAY */
	RAY3D("Ray", "Ray3D", 110, true),

	/** CONIC */
	CONIC3D("Conic", "Conic3D", 70, true),

	/** CONICSECTION */
	CONICSECTION("ConicPart", "Conic3DPart", 70, true),

	/** AXIS */
	AXIS3D("Axis", "Axis3D", 10, true),

	/** CURVE_CARTESIAN */
	CURVE_CARTESIAN3D("CurveCartesian", "CurveCartesian3D", 90, true),

	/** POLYGON */
	POLYGON3D("Polygon", "Polygon3D", 50, true),

	/** PLANE */
	PLANE3D("Plane", "Plane3D", 45, true),

	/** QUADRIC */
	QUADRIC("Quadric", 46, true),

	/** QUADRIC_PART */
	QUADRIC_PART("Quadric", "QuadricPart", 46, true),

	/** QUADRIC_LIMITED */
	QUADRIC_LIMITED("Quadric", "QuadricLimited", 47, true),

	/** POLYLINE */
	POLYLINE3D("PolyLine", "PolyLine3D", 51, true),

	/** POLYHEDRON */
	POLYHEDRON("Polyhedron", 50, true),

	/** NET */
	NET("Net", 50, true),

	/** SURFACECARTESIAN */
	SURFACECARTESIAN3D("Surface", "SurfaceCartesian3D", 160, true),

	/** SURFACECARTESIAN */
	SURFACECARTESIAN("Surface", "SurfaceCartesian", 160, false),

	/** IMPLICIT_SURFACE_ */
	IMPLICIT_SURFACE_3D("ImplicitSurface", "ImplicitSurface3D", 160, true),

	/** CLIPPINGCUBE */
	CLIPPINGCUBE3D("ClippingCube3D", 160, true),

	/** SPACE */
	SPACE("Space", 160, true),

	/** AUDIO */
	AUDIO("Audio", 160, false),

	/** VIDEO */
	VIDEO("Video", 160, false),

	/**
	 * Embedded GeoGebra applet
	 */
	EMBED("Embed", 160, false),

	/** DEFAULT */
	DEFAULT("Default", 160, false),

	/** SYmbolic row in CAS calc */
	SYMBOLIC("Symbolic", 0, false),

	/** Inline text */
	INLINE_TEXT("InlineText", 150, false),

	/** Inline editable table */
	TABLE("Table", 150, false);

	/**
	 * name MUST be in ggbtrans/properties
	 */
	final public String name;
	/** in XML we need 3d extension */
	final public String xmlName;
	/** for 3D elements */
	final public boolean is3D;
	final private int priority;
	final private int independentPriority;
	/** for FUNCTION_NVAR: Inequality */
	static final public String INEQUALITY = "Inequality";
	/** for FUNCTION_NVAR: MultivariableFunction */
	static final public String MULTIVARIABLE_FUNCTION = "MultivariableFunction";

	GeoClass(String name, int priority, boolean is3D) {
		this(name, name, priority, priority, is3D);
	}

	GeoClass(String name, String xmlName, int priority, int independentPriority,
			boolean is3D) {
		this.name = name;
		this.priority = priority;
		this.independentPriority = independentPriority;
		this.xmlName = xmlName;
		this.is3D = is3D;
	}

	GeoClass(String name, String xmlName, int priority, boolean is3D) {
		this(name, xmlName, priority, priority, is3D);

	}

	/**
	 * @param independent
	 *            whether element is independent
	 * @return drawing priority; the higher the easier to hit the object
	 */
	public int getPriority(boolean independent) {
		return independent ? independentPriority : priority;
	}
}
