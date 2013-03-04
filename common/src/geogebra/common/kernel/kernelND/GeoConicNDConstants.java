package geogebra.common.kernel.kernelND;

/**
 * Conic types
 */
public interface GeoConicNDConstants extends GeoQuadricNDConstants{
	
	/** single point type*/    
	public static final int CONIC_SINGLE_POINT = QUADRIC_SINGLE_POINT;
	/** intersecting lines type*/
	public static final int CONIC_INTERSECTING_LINES = QUADRIC_INTERSECTING_LINES;
	/** ellipse type*/
	public static final int CONIC_ELLIPSE = QUADRIC_ELLIPSOID;
	/** circle type*/
	public static final int CONIC_CIRCLE = QUADRIC_SPHERE;
	/** hyperbola type*/
	public static final int CONIC_HYPERBOLA = QUADRIC_HYPERBOLOID;
	/** empty conic type*/
	public static final int CONIC_EMPTY = QUADRIC_EMPTY;
	/** double line type*/
	public static final int CONIC_DOUBLE_LINE = QUADRIC_DOUBLE_LINE;
	/** parallel lines type */
	public static final int CONIC_PARALLEL_LINES = QUADRIC_PARALLEL_LINES;
	/** parabola type */
	public static final int CONIC_PARABOLA = QUADRIC_PARABOLOID;
	/** line type */
	public static final int CONIC_LINE = QUADRIC_LINE;
	
	////////////////////////
	// FOR GEO CONIC PART
	
	
	/** conic arc */
	public static final int CONIC_PART_ARC = 1;
	/** conic sector */
	public static final int CONIC_PART_SECTOR = 2;
	/** conic arcs and  */
	public static final int CONIC_PART_ARCS = 3;



}
