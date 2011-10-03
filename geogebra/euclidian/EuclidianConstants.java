package geogebra.euclidian;

public final class EuclidianConstants {
	
	//prevent instantiation
	private EuclidianConstants(){
	}
	
	public static final int MODE_MOVE = 0;

	public static final int MODE_POINT = 1;

	public static final int MODE_JOIN = 2;

	public static final int MODE_PARALLEL = 3;

	public static final int MODE_ORTHOGONAL = 4;

	public static final int MODE_INTERSECT = 5;

	public static final int MODE_DELETE = 6;

	public static final int MODE_VECTOR = 7;

	public static final int MODE_LINE_BISECTOR = 8;

	public static final int MODE_ANGULAR_BISECTOR = 9;

	public static final int MODE_CIRCLE_TWO_POINTS = 10;

	public static final int MODE_CIRCLE_THREE_POINTS = 11;

	public static final int MODE_CONIC_FIVE_POINTS = 12;

	public static final int MODE_TANGENTS = 13;

	public static final int MODE_RELATION = 14;

	public static final int MODE_SEGMENT = 15;

	public static final int MODE_POLYGON = 16;

	public static final int MODE_TEXT = 17;

	public static final int MODE_RAY = 18;

	public static final int MODE_MIDPOINT = 19;

	public static final int MODE_CIRCLE_ARC_THREE_POINTS = 20;

	public static final int MODE_CIRCLE_SECTOR_THREE_POINTS = 21;

	public static final int MODE_CIRCUMCIRCLE_ARC_THREE_POINTS = 22;

	public static final int MODE_CIRCUMCIRCLE_SECTOR_THREE_POINTS = 23;

	public static final int MODE_SEMICIRCLE = 24;

	public static final int MODE_SLIDER = 25;

	public static final int MODE_IMAGE = 26;

	public static final int MODE_SHOW_HIDE_OBJECT = 27;

	public static final int MODE_SHOW_HIDE_LABEL = 28;

	public static final int MODE_MIRROR_AT_POINT = 29;

	public static final int MODE_MIRROR_AT_LINE = 30;

	public static final int MODE_TRANSLATE_BY_VECTOR = 31;

	public static final int MODE_ROTATE_BY_ANGLE = 32;

	public static final int MODE_DILATE_FROM_POINT = 33;

	public static final int MODE_CIRCLE_POINT_RADIUS = 34;

	public static final int MODE_COPY_VISUAL_STYLE = 35;

	public static final int MODE_ANGLE = 36;

	public static final int MODE_VECTOR_FROM_POINT = 37;

	public static final int MODE_DISTANCE = 38;

	public static final int MODE_MOVE_ROTATE = 39;

	public static final int MODE_TRANSLATEVIEW = 40;

	public static final int MODE_ZOOM_IN = 41;

	public static final int MODE_ZOOM_OUT = 42;

	public static final int MODE_SELECTION_LISTENER = 43;

	public static final int MODE_POLAR_DIAMETER = 44;

	public static final int MODE_SEGMENT_FIXED = 45;

	public static final int MODE_ANGLE_FIXED = 46;

	public static final int MODE_LOCUS = 47;

	public static final int MODE_MACRO = 48;
	
	public static final int MODE_AREA = 49;
	
	public static final int MODE_SLOPE = 50;
	
	public static final int MODE_REGULAR_POLYGON = 51;
	
	public static final int MODE_SHOW_HIDE_CHECKBOX = 52;
	// GeoGebra 3.2 start
	public static final int MODE_COMPASSES = 53;

	public static final int MODE_MIRROR_AT_CIRCLE = 54;

	public static final int MODE_ELLIPSE_THREE_POINTS = 55;

	public static final int MODE_HYPERBOLA_THREE_POINTS = 56;

	public static final int MODE_PARABOLA = 57;

	public static final int MODE_FITLINE = 58;

	public static final int MODE_RECORD_TO_SPREADSHEET = 59;
	// GeoGebra 3.4 start
	public static final int MODE_BUTTON_ACTION = 60;
	
	public static final int MODE_TEXTFIELD_ACTION = 61;
	
	public static final int MODE_PEN = 62;
	
	public static final int MODE_VISUAL_STYLE = 63;
	
	public static final int MODE_RIGID_POLYGON = 64;
	
	public static final int MODE_POLYLINE = 65;
	
	public static final int MODE_PROBABILITY_CALCULATOR = 66;
	
	public static final int MODE_ATTACH_DETACH = 67;
	
	public static final int MODE_FUNCTION_INSPECTOR = 68;
	
	public static final int MODE_INTERSECTION_CURVE = 69; 
	
	public static final int MODE_VECTOR_POLYGON = 70;

	public static final int MODE_CREATE_LIST = 71;

	public static final int MODE_COMPLEX_NUMBER = 72;

	// ggb3D start
	/** special mode that allows to create point inside a region (polygon, etc.) */
	public static final int MODE_POINT_ON_OBJECT = 501;
	
	/** mode that change the view to be in front of selected plane */
	public static final int MODE_VIEW_IN_FRONT_OF = 502;
	
	/** creates a plane through three points */
	public static final int MODE_PLANE_THREE_POINTS = 510;
	
	/** creates a plane through three points */
	public static final int MODE_PLANE_POINT_LINE = 511;
	
	/** creates a plane orthogonal to a line */
	public static final int MODE_ORTHOGONAL_PLANE = 512;
	
	/** creates a plane parallel to another */
	public static final int MODE_PARALLEL_PLANE = 513;

	/** creates a sphere with midpoint and radius */
	public static final int MODE_SPHERE_POINT_RADIUS = 520;
	
	/** creates a sphere with midpoint through another point */
	public static final int MODE_SPHERE_TWO_POINTS = 521;
	
	/** creates a prism with basis and first vertex of the second parallel face */
	public static final int MODE_PRISM = 531;
	
	/** creates a prism with basis and first vertex of the second parallel face */
	public static final int MODE_RIGHT_PRISM = 532;
	
	/** rotate the view */
	public static final int MODE_ROTATEVIEW = 540;
	
	/** circle with center, radius, direction */
	public static final int MODE_CIRCLE_POINT_RADIUS_DIRECTION = 550;
	
	/** circle with center, radius, direction */
	public static final int MODE_CIRCLE_AXIS_POINT = 551;
	
	// CAS view modes
	public static final int MODE_CAS_EVALUATE = 1001;
	public static final int MODE_CAS_NUMERIC = 1002;
	public static final int MODE_CAS_KEEP_INPUT = 1003;
	public static final int MODE_CAS_EXPAND = 1004;
	public static final int MODE_CAS_FACTOR = 1005;
	public static final int MODE_CAS_SUBSTITUTE = 1006;
	public static final int MODE_CAS_SOLVE = 1007;
	public static final int MODE_CAS_DERIVATIVE = 1008;
	public static final int MODE_CAS_INTEGRAL = 1009;

	
	// SpreadsheetView modes
	public static final int MODE_SPREADSHEET_CREATE_LIST = 2001;
	public static final int MODE_SPREADSHEET_CREATE_MATRIX = 2002;
	public static final int MODE_SPREADSHEET_CREATE_LISTOFPOINTS = 2003;
	public static final int MODE_SPREADSHEET_CREATE_TABLETEXT = 2004;
	public static final int MODE_SPREADSHEET_CREATE_POLYLINE = 2005;
	
	public static final int MODE_SPREADSHEET_ONEVARSTATS = 2020;
	public static final int MODE_SPREADSHEET_TWOVARSTATS = 2021;
	public static final int MODE_SPREADSHEET_MULTIVARSTATS = 2022;
	
	public static final int MODE_SPREADSHEET_SORT = 2030;
	public static final int MODE_SPREADSHEET_SORT_AZ = 2031;
	public static final int MODE_SPREADSHEET_SORT_ZA = 2032;
	
	public static final int MODE_SPREADSHEET_SUM = 2040;
	public static final int MODE_SPREADSHEET_AVERAGE = 2041;
	public static final int MODE_SPREADSHEET_COUNT = 2042;
	public static final int MODE_SPREADSHEET_MIN = 2043;
	public static final int MODE_SPREADSHEET_MAX = 2044;
	
	
	// macro tools ID offset
	public static final int MACRO_MODE_ID_OFFSET = 100001;

}
