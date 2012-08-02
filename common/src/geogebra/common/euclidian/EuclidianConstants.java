package geogebra.common.euclidian;

/**
 * Mode numbers
 */
public final class EuclidianConstants {

	// prevent instantiation
	private EuclidianConstants() {
	}

	/** Move */
	public static final int MODE_MOVE = 0;

	/** New Point */
	public static final int MODE_POINT = 1;

	/** Line through Two Points */
	public static final int MODE_JOIN = 2;

	/** Parallel Line */
	public static final int MODE_PARALLEL = 3;

	/** Perpendicular Line */
	public static final int MODE_ORTHOGONAL = 4;

	/** Intersect Two Objects */
	public static final int MODE_INTERSECT = 5;

	/** Delete Object */
	public static final int MODE_DELETE = 6;

	/** Vector between Two Points */
	public static final int MODE_VECTOR = 7;

	/** Perpendicular Bisector */
	public static final int MODE_LINE_BISECTOR = 8;

	/** Angle Bisector */
	public static final int MODE_ANGULAR_BISECTOR = 9;

	/** Circle with Center through Point */
	public static final int MODE_CIRCLE_TWO_POINTS = 10;

	/** Circle through Three Points */
	public static final int MODE_CIRCLE_THREE_POINTS = 11;

	/** Conic through Five Points */
	public static final int MODE_CONIC_FIVE_POINTS = 12;

	/** Tangents */
	public static final int MODE_TANGENTS = 13;

	/** Relation between Two Objects */
	public static final int MODE_RELATION = 14;

	/** Segment between Two Points */
	public static final int MODE_SEGMENT = 15;

	/** Polygon */
	public static final int MODE_POLYGON = 16;

	/** Insert Text */
	public static final int MODE_TEXT = 17;

	/** Ray through Two Points */
	public static final int MODE_RAY = 18;

	/** Midpoint or Center */
	public static final int MODE_MIDPOINT = 19;

	/** Circular Arc with Center between Two Points */
	public static final int MODE_CIRCLE_ARC_THREE_POINTS = 20;

	/** Circular Sector with Center between Two Points */
	public static final int MODE_CIRCLE_SECTOR_THREE_POINTS = 21;

	/** Circumcircular Arc through Three Points */
	public static final int MODE_CIRCUMCIRCLE_ARC_THREE_POINTS = 22;

	/** Circumcircular Sector through Three Points */
	public static final int MODE_CIRCUMCIRCLE_SECTOR_THREE_POINTS = 23;

	/** Semicircle through Two Points */
	public static final int MODE_SEMICIRCLE = 24;

	/** Slider */
	public static final int MODE_SLIDER = 25;

	/** Insert Image */
	public static final int MODE_IMAGE = 26;

	/** Show / Hide Object */
	public static final int MODE_SHOW_HIDE_OBJECT = 27;

	/** Show / Hide Label */
	public static final int MODE_SHOW_HIDE_LABEL = 28;

	/** Reflect Object about Point */
	public static final int MODE_MIRROR_AT_POINT = 29;

	/** Reflect Object about Line */
	public static final int MODE_MIRROR_AT_LINE = 30;

	/** Translate Object by Vector */
	public static final int MODE_TRANSLATE_BY_VECTOR = 31;

	/** Rotate Object around Point by Angle */
	public static final int MODE_ROTATE_BY_ANGLE = 32;

	/** Dilate Object from Point by Factor */
	public static final int MODE_DILATE_FROM_POINT = 33;

	/** Circle with Center and Radius */
	public static final int MODE_CIRCLE_POINT_RADIUS = 34;

	/** Copy Visual Style */
	public static final int MODE_COPY_VISUAL_STYLE = 35;

	/** Angle */
	public static final int MODE_ANGLE = 36;

	/** Vector from Point */
	public static final int MODE_VECTOR_FROM_POINT = 37;

	/** Distance or Length */
	public static final int MODE_DISTANCE = 38;

	/** Rotate around Point */
	public static final int MODE_MOVE_ROTATE = 39;

	/** Move Graphics View */
	public static final int MODE_TRANSLATEVIEW = 40;

	/** Zoom In */
	public static final int MODE_ZOOM_IN = 41;

	/** Zoom Out */
	public static final int MODE_ZOOM_OUT = 42;

	/** Select Object */
	public static final int MODE_SELECTION_LISTENER = 43;

	/** Polar or Diameter Line */
	public static final int MODE_POLAR_DIAMETER = 44;

	/** Segment with Given Length from Point */
	public static final int MODE_SEGMENT_FIXED = 45;

	/** Angle with Given Size */
	public static final int MODE_ANGLE_FIXED = 46;

	/** Locus */
	public static final int MODE_LOCUS = 47;
	/** Macro */
	public static final int MODE_MACRO = 48;

	/** Area */
	public static final int MODE_AREA = 49;

	/** Slope */
	public static final int MODE_SLOPE = 50;

	/** Regular Polygon */
	public static final int MODE_REGULAR_POLYGON = 51;

	/** Check Box to Show / Hide Objects */
	public static final int MODE_SHOW_HIDE_CHECKBOX = 52;
	// GeoGebra 3.2 start
	/** Compass */
	public static final int MODE_COMPASSES = 53;

	/** Reflect Object about Circle */
	public static final int MODE_MIRROR_AT_CIRCLE = 54;

	/** Ellipse */
	public static final int MODE_ELLIPSE_THREE_POINTS = 55;

	/** Hyperbola */
	public static final int MODE_HYPERBOLA_THREE_POINTS = 56;

	/** Parabola */
	public static final int MODE_PARABOLA = 57;

	/** Best Fit Line */
	public static final int MODE_FITLINE = 58;

	/** Record to Spreadsheet */
	public static final int MODE_RECORD_TO_SPREADSHEET = 59;
	// GeoGebra 3.4 start
	/** Insert Button */
	public static final int MODE_BUTTON_ACTION = 60;

	/** Insert Input Box */
	public static final int MODE_TEXTFIELD_ACTION = 61;

	/** Pen Tool */
	public static final int MODE_PEN = 62;

	/** Visual Style, removed */
	//public static final int MODE_VISUAL_STYLE = 63;

	/** Rigid Polygon */
	public static final int MODE_RIGID_POLYGON = 64;

	/** PolyLine between Points */
	public static final int MODE_POLYLINE = 65;

	/** Probability Calculator */
	public static final int MODE_PROBABILITY_CALCULATOR = 66;

	/** Attach / Detach Point */
	public static final int MODE_ATTACH_DETACH = 67;

	/** Function Inspector */
	public static final int MODE_FUNCTION_INSPECTOR = 68;

	/** Intersect Two Surfaces */
	public static final int MODE_INTERSECTION_CURVE = 69;

	/** Vector Polygon */
	public static final int MODE_VECTOR_POLYGON = 70;

	/** Create List */
	public static final int MODE_CREATE_LIST = 71;

	/** Complex Number */
	public static final int MODE_COMPLEX_NUMBER = 72;

	/** Freehand */
	public static final int MODE_FREEHAND_SHAPE = 73;
	
	/** Pencil */
	@Deprecated
	public static final int MODE_PENCIL = 74;
	
	/** Point on Object */
	public static final int MODE_POINT_ON_OBJECT = 501;

	// ggb3D start

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

	/** creates a prism/cylinder with basis and height */
	public static final int MODE_EXTRUSION = 532;

	/** creates a prism with basis and top vertex */
	public static final int MODE_PYRAMID = 533;
	
	/** creates a pyramid/cone with basis and height */
	public static final int MODE_CONIFY = 534;

	/** rotate the view */
	public static final int MODE_ROTATEVIEW = 540;

	/** circle with center, radius, direction */
	public static final int MODE_CIRCLE_POINT_RADIUS_DIRECTION = 550;

	/** circle with center, radius, direction */
	public static final int MODE_CIRCLE_AXIS_POINT = 551;

	// CAS view modes
	/** Evaluate */
	public static final int MODE_CAS_EVALUATE = 1001;
	/** Numeric */
	public static final int MODE_CAS_NUMERIC = 1002;
	/** Keep Input */
	public static final int MODE_CAS_KEEP_INPUT = 1003;
	/** Expand */
	public static final int MODE_CAS_EXPAND = 1004;
	/** Factor */
	public static final int MODE_CAS_FACTOR = 1005;
	/** Substitute */
	public static final int MODE_CAS_SUBSTITUTE = 1006;
	/** Solve */
	public static final int MODE_CAS_SOLVE = 1007;
	/** Derivative */
	public static final int MODE_CAS_DERIVATIVE = 1008;
	/** Integral */
	public static final int MODE_CAS_INTEGRAL = 1009;
	/** Solve Numerically */
	public static final int MODE_CAS_NUMERICAL_SOLVE = 1010;
	// SpreadsheetView modes
	/** Create List */
	public static final int MODE_SPREADSHEET_CREATE_LIST = 2001;
	/** Create Matrix */
	public static final int MODE_SPREADSHEET_CREATE_MATRIX = 2002;
	/** Create List of Points */
	public static final int MODE_SPREADSHEET_CREATE_LISTOFPOINTS = 2003;
	/** Create Table */
	public static final int MODE_SPREADSHEET_CREATE_TABLETEXT = 2004;
	/** Create PolyLine */
	public static final int MODE_SPREADSHEET_CREATE_POLYLINE = 2005;

	/** One Variable Analysis */
	public static final int MODE_SPREADSHEET_ONEVARSTATS = 2020;
	/** Two Variable Regression Analysis */
	public static final int MODE_SPREADSHEET_TWOVARSTATS = 2021;
	/** Multiple Variable Analysis */
	public static final int MODE_SPREADSHEET_MULTIVARSTATS = 2022;
	/** Sort cells */
	public static final int MODE_SPREADSHEET_SORT = 2030;
	/** Sort cells increasingly */
	public static final int MODE_SPREADSHEET_SORT_AZ = 2031;
	/** Sort cells decreasingly */
	public static final int MODE_SPREADSHEET_SORT_ZA = 2032;

	/** Sum */
	public static final int MODE_SPREADSHEET_SUM = 2040;
	/** Mean */
	public static final int MODE_SPREADSHEET_AVERAGE = 2041;
	/** Count */
	public static final int MODE_SPREADSHEET_COUNT = 2042;
	/** Minimum */
	public static final int MODE_SPREADSHEET_MIN = 2043;
	/** Maximum */
	public static final int MODE_SPREADSHEET_MAX = 2044;

	/** macro tools ID offset */
	public static final int MACRO_MODE_ID_OFFSET = 100001;

}
