package org.geogebra.common.euclidian;

import java.util.Locale;
import java.util.stream.Stream;

/**
 * Mode numbers
 */
public final class EuclidianConstants {

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

	/** Insert Button */
	public static final int MODE_BUTTON_ACTION = 60;

	/** Insert Input Box */
	public static final int MODE_TEXTFIELD_ACTION = 61;

	/** Pen Tool */
	public static final int MODE_PEN = 62;

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

	/** Freehand function */
	public static final int MODE_FREEHAND_FUNCTION = 74;

	/** Extremum */
	public static final int MODE_EXTREMUM = 75;

	/** Roots */
	public static final int MODE_ROOTS = 76;
	
	/** Select multiple objects */
	public static final int MODE_SELECT = 77;

	/** */
	public static final int MODE_SELECT_MOW = 78;

	/** Graspable Math tool */
	public static final int MODE_GRASPABLE_MATH = 79;

	/** Photo Library */
	public static final int MODE_PHOTO_LIBRARY = 80;

	/** Point on Object */
	public static final int MODE_POINT_ON_OBJECT = 501;

	// ggb3D start

	/** mode that change the view to be in front of selected plane */
	public static final int MODE_VIEW_IN_FRONT_OF = 502;

	/** creates a plane through three points */
	public static final int MODE_PLANE_THREE_POINTS = 510;

	/** creates a plane through three points */
	public static final int MODE_PLANE = 511;

	/** creates a plane orthogonal to a line */
	public static final int MODE_ORTHOGONAL_PLANE = 512;

	/** creates a plane parallel to another */
	public static final int MODE_PARALLEL_PLANE = 513;

	/** Perpendicular Line (for 3D view) */
	public static final int MODE_ORTHOGONAL_THREE_D = 514;

	/** creates a sphere with midpoint and radius */
	public static final int MODE_SPHERE_POINT_RADIUS = 520;

	/** creates a sphere with midpoint through another point */
	public static final int MODE_SPHERE_TWO_POINTS = 521;

	/**
	 * creates a cone with center of basis, apex point and radius of the basis
	 */
	public static final int MODE_CONE_TWO_POINTS_RADIUS = 522;

	/**
	 * creates a cylinder with center of basis, apex point and radius of the
	 * basis
	 */
	public static final int MODE_CYLINDER_TWO_POINTS_RADIUS = 523;

	/**
	 * creates a prism with basis and first vertex of the second parallel face
	 */
	public static final int MODE_PRISM = 531;

	/** creates a prism/cylinder with basis and height */
	public static final int MODE_EXTRUSION = 532;

	/** creates a prism with basis and top vertex */
	public static final int MODE_PYRAMID = 533;

	/** creates a pyramid/cone with basis and height */
	public static final int MODE_CONIFY = 534;

	/** polyhedronNet */
	public static final int MODE_NET = 535;

	/** creates a cube */
	public static final int MODE_CUBE = 536;

	/** creates a tetrahedron */
	public static final int MODE_TETRAHEDRON = 537;

	/** creates a surface by revolving a line around x-axis */
	public static final int MODE_SURFACE_OF_REVOLUTION = 538;

	/** rotate the view */
	public static final int MODE_ROTATEVIEW = 540;

	/** circle with center, radius, direction */
	public static final int MODE_CIRCLE_POINT_RADIUS_DIRECTION = 550;

	/** circle with center, radius, direction */
	public static final int MODE_CIRCLE_AXIS_POINT = 551;

	/** volume */
	public static final int MODE_VOLUME = 560;

	/** Rotate Object around Axis by Angle */
	public static final int MODE_ROTATE_AROUND_LINE = 570;

	/** Reflect Object about Plane */
	public static final int MODE_MIRROR_AT_PLANE = 571;

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

	/** WHITEBOARD TOOLS */
	public static final int MODE_SHAPE_LINE = 101;
	/** Triangle */
	public static final int MODE_SHAPE_TRIANGLE = 102;
	/** Square */
	public static final int MODE_SHAPE_SQUARE = 103;
	/** Rectangle */
	public static final int MODE_SHAPE_RECTANGLE = 104;
	/** Regular polygon */
	public static final int MODE_SHAPE_PENTAGON = 106;
	/** Freeform TODO same as normal polygon? */
	public static final int MODE_SHAPE_FREEFORM = 107;
	/** Circle */
	public static final int MODE_SHAPE_CIRCLE = 108;
	/** Ellipse */
	public static final int MODE_SHAPE_ELLIPSE = 109;
	/** Eraser */
	public static final int MODE_ERASER = 110;
	/** Highlighter */
	public static final int MODE_HIGHLIGHTER = 111;
	/** Video */
	public static final int MODE_VIDEO = 115;
	/** Audio */
	public static final int MODE_AUDIO = 116;
	/** Geogebra */
	public static final int MODE_CALCULATOR = 117;
	/** Camera */
	public static final int MODE_CAMERA = 118;
	/** PDF tool */
	public static final int MODE_PDF = 119;
	/** Extension embed */
	public static final int MODE_EXTENSION = 120;
	/** Text tool */
	public static final int MODE_MEDIA_TEXT = 121;
	/** Mask */
	public static final int MODE_MASK = 122;
	/** Table */
	public static final int MODE_TABLE = 123;
	/** Equation */
	public static final int MODE_EQUATION = 124;

	public static final int MODE_MIND_MAP = 126;
	/** Ruler */
	public static final int MODE_RULER = 127;
	/** Protractor */
	public static final int MODE_PROTRACTOR = 128;
	/**
	 * Triangle protractor
	 */
	public static final int MODE_TRIANGLE_PROTRACTOR = 129;

	/** macro tools ID offset */
	public static final int MACRO_MODE_ID_OFFSET = 100001;
	/** max delay between taps of a doublecklick */
	public static final long DOUBLE_CLICK_DELAY = 300;
	/** ignore drag until this many milliseconds after drag start */
	public static final long DRAGGING_DELAY = 100;
    /** ignore drag until this many milliseconds after drag start, for moving created point along
     * z axis */
    public static final long DRAGGING_DELAY_FOR_MOVING_POINT_ALONG_Z = 200;
	/** default size of delete tool rectangle in pixels */
	public static final int DEFAULT_ERASER_SIZE = 20;
	/** line thickness for pen (mow) */
	public static final int DEFAULT_PEN_SIZE = 5;
	/** min length of input box that allows display of symbol button */
	public static final int SHOW_SYMBOLBUTTON_MINLENGTH = 8;

	/** 13 in older files */
	public static final int DEFAULT_CHECKBOX_SIZE = 26;

	/**
	 * min line thickness of highlighter
	 */
	public static final int MIN_PEN_HIGHLIGHTER_SIZE = 1;
	/**
	 * default step size to increase line thickness of pen/highlighter
	 */
	public static final int DEFAULT_PEN_STEP = 1;
	/**
	 * default opacity of highlighter (0.3)
	 */
	public static final int DEFAULT_HIGHLIGHTER_OPACITY = 77;
	/**
	 * default line thickness of highlighter
	 */
	public static final int DEFAULT_HIGHLIGHTER_SIZE = 20;
	/**
	 * max line thickness of highlighter
	 */
	public static final int MAX_PEN_HIGHLIGHTER_SIZE = 30;

	public static String getModeIconName(int mode) {
		return mode == EuclidianConstants.MODE_DELETE ? "eraser" : getModeTextSimple(mode);
	}

	/**
	 * @param mode
	 *            mode ID
	 * @return tool name without the .tool suffix
	 */
	public static String getModeTextSimple(int mode) {
		return getModeText(mode).replace(".Tool", "");
	}

	/**
	 * @param mode
	 *            mode number
	 * @return key of the mode description
	 */
	public static String getModeText(int mode) {
		switch (mode) {

		// 3D Modes
		case EuclidianConstants.MODE_VIEW_IN_FRONT_OF:
			return "ViewInFrontOf";

		case EuclidianConstants.MODE_PLANE_THREE_POINTS:
			return "PlaneThreePoint";

		case EuclidianConstants.MODE_PLANE:
			return "Plane.Tool";

		case EuclidianConstants.MODE_ORTHOGONAL_PLANE:
			return "PerpendicularPlane";

		case EuclidianConstants.MODE_PARALLEL_PLANE:
			return "ParallelPlane";

		case EuclidianConstants.MODE_CUBE:
			return "Cube.Tool";

		case EuclidianConstants.MODE_TETRAHEDRON:
			return "Tetrahedron";

		case EuclidianConstants.MODE_PRISM:
			return "Prism";

		case EuclidianConstants.MODE_EXTRUSION:
			return "Extrusion";

		case EuclidianConstants.MODE_SURFACE_OF_REVOLUTION:
			return "SurfaceOfRevolution";

		case EuclidianConstants.MODE_CONIFY:
			return "Conify";

		case EuclidianConstants.MODE_PYRAMID:
			return "Pyramid.Tool";

		case EuclidianConstants.MODE_NET:
			return "Net.Tool";

		case EuclidianConstants.MODE_SPHERE_POINT_RADIUS:
			return "SpherePointRadius";

		case EuclidianConstants.MODE_SPHERE_TWO_POINTS:
			return "Sphere2";

		case EuclidianConstants.MODE_CONE_TWO_POINTS_RADIUS:
			return "Cone.Tool";

		case EuclidianConstants.MODE_CYLINDER_TWO_POINTS_RADIUS:
			return "Cylinder.Tool";

		case EuclidianConstants.MODE_ROTATEVIEW:
			return "RotateView";

		case EuclidianConstants.MODE_CIRCLE_POINT_RADIUS_DIRECTION:
			return "CirclePointRadiusDirection";

		case EuclidianConstants.MODE_CIRCLE_AXIS_POINT:
			return "CircleAxisPoint";

		case EuclidianConstants.MODE_VOLUME:
			return "Volume";

		case EuclidianConstants.MODE_MIRROR_AT_PLANE:
			return "ReflectAboutPlane";

		case EuclidianConstants.MODE_ROTATE_AROUND_LINE:
			return "RotateAroundLine";

		case EuclidianConstants.MODE_ORTHOGONAL_THREE_D:
			return "OrthogonalThreeD";

		case EuclidianConstants.MODE_SELECTION_LISTENER:
			return "Select";

		case EuclidianConstants.MODE_MOVE:
			return "Move";

		case EuclidianConstants.MODE_POINT:
			return "Point.Tool";

		case EuclidianConstants.MODE_COMPLEX_NUMBER:
			return "ComplexNumber.Tool";

		case EuclidianConstants.MODE_POINT_ON_OBJECT:
			return "PointOnObject";

		case EuclidianConstants.MODE_JOIN:
			return "Join";

		case EuclidianConstants.MODE_SEGMENT:
			return "Segment.Tool";

		case EuclidianConstants.MODE_SEGMENT_FIXED:
			return "SegmentFixed";

		case EuclidianConstants.MODE_RAY:
			return "Ray.Tool";

		case EuclidianConstants.MODE_POLYGON:
			return "Polygon.Tool";

		case EuclidianConstants.MODE_POLYLINE:
			return "Polyline.Tool";

		case EuclidianConstants.MODE_RIGID_POLYGON:
			return "RigidPolygon";

		case EuclidianConstants.MODE_VECTOR_POLYGON:
			return "VectorPolygon";

		case EuclidianConstants.MODE_PARALLEL:
			return "ParallelLine";

		case EuclidianConstants.MODE_ORTHOGONAL:
			return "PerpendicularLine";

		case EuclidianConstants.MODE_INTERSECT:
			return "Intersect";

		case EuclidianConstants.MODE_INTERSECTION_CURVE:
			return "IntersectTwoSurfaces";

		case EuclidianConstants.MODE_LINE_BISECTOR:
			return "PerpendicularBisector";

		case EuclidianConstants.MODE_ANGULAR_BISECTOR:
			return "AngleBisector";

		case EuclidianConstants.MODE_TANGENTS:
			return "Tangents";

		case EuclidianConstants.MODE_POLAR_DIAMETER:
			return "PolarDiameter";

		case EuclidianConstants.MODE_CIRCLE_TWO_POINTS:
			return "Circle2";

		case EuclidianConstants.MODE_CIRCLE_THREE_POINTS:
			return "Circle3";

		case EuclidianConstants.MODE_ELLIPSE_THREE_POINTS:
			return "Ellipse.Tool";

		case EuclidianConstants.MODE_PARABOLA:
			return "Parabola.Tool";

		case EuclidianConstants.MODE_HYPERBOLA_THREE_POINTS:
			return "Hyperbola.Tool";

		// Michael Borcherds 2008-03-13
		case EuclidianConstants.MODE_COMPASSES:
			return "Compass";

		case EuclidianConstants.MODE_CONIC_FIVE_POINTS:
			return "Conic5";

		case EuclidianConstants.MODE_RELATION:
			return "Relation";

		case EuclidianConstants.MODE_TRANSLATEVIEW:
			return "MoveGraphicsView";

		case EuclidianConstants.MODE_SHOW_HIDE_OBJECT:
			return "ShowHideObject";

		case EuclidianConstants.MODE_SHOW_HIDE_LABEL:
			return "ShowHideLabel";

		case EuclidianConstants.MODE_COPY_VISUAL_STYLE:
			return "CopyVisualStyle";

		case EuclidianConstants.MODE_DELETE:
			return "Delete";

		case EuclidianConstants.MODE_VECTOR:
			return "Vector.Tool";

		case EuclidianConstants.MODE_TEXT:
		case EuclidianConstants.MODE_MEDIA_TEXT:
			return "Text.Tool";

		case EuclidianConstants.MODE_IMAGE:
			return "Image.Tool";

		case EuclidianConstants.MODE_MIDPOINT:
			return "Midpoint.Tool";

		case EuclidianConstants.MODE_SEMICIRCLE:
			return "Semicircle.Tool";

		case EuclidianConstants.MODE_CIRCLE_ARC_THREE_POINTS:
			return "CircularArc";

		case EuclidianConstants.MODE_CIRCLE_SECTOR_THREE_POINTS:
			return "CircularSector";

		case EuclidianConstants.MODE_CIRCUMCIRCLE_ARC_THREE_POINTS:
			return "CircumcircularArc";

		case EuclidianConstants.MODE_CIRCUMCIRCLE_SECTOR_THREE_POINTS:
			return "CircumcircularSector";

		case EuclidianConstants.MODE_SLIDER:
			return "Slider.Tool";

		case EuclidianConstants.MODE_MIRROR_AT_POINT:
			return "ReflectAboutPoint";

		case EuclidianConstants.MODE_MIRROR_AT_LINE:
			return "ReflectAboutLine";

		case EuclidianConstants.MODE_MIRROR_AT_CIRCLE:
			return "ReflectAboutCircle";

		case EuclidianConstants.MODE_TRANSLATE_BY_VECTOR:
			return "TranslateByVector";

		case EuclidianConstants.MODE_ROTATE_BY_ANGLE:
			return "RotateAroundPoint";

		case EuclidianConstants.MODE_DILATE_FROM_POINT:
			return "DilateFromPoint";

		case EuclidianConstants.MODE_CIRCLE_POINT_RADIUS:
			return "CirclePointRadius";

		case EuclidianConstants.MODE_ANGLE:
			return "Angle.Tool";

		case EuclidianConstants.MODE_ANGLE_FIXED:
			return "AngleFixed";

		case EuclidianConstants.MODE_VECTOR_FROM_POINT:
			return "VectorFromPoint";

		case EuclidianConstants.MODE_DISTANCE:
			return "Distance";

		case EuclidianConstants.MODE_MOVE_ROTATE:
			return "MoveAroundPoint";

		case EuclidianConstants.MODE_ZOOM_IN:
			return "ZoomIn.Tool";

		case EuclidianConstants.MODE_ZOOM_OUT:
			return "ZoomOut.Tool";

		case EuclidianConstants.MODE_LOCUS:
			return "Locus.Tool";

		case EuclidianConstants.MODE_AREA:
			return "Area";

		case EuclidianConstants.MODE_SLOPE:
			return "Slope";

		case EuclidianConstants.MODE_REGULAR_POLYGON:
			return "RegularPolygon";

		case EuclidianConstants.MODE_SHOW_HIDE_CHECKBOX:
			return "CheckBox";

		case EuclidianConstants.MODE_BUTTON_ACTION:
			return "Button.Tool";

		case EuclidianConstants.MODE_TEXTFIELD_ACTION:
			return "InputBox";

		case EuclidianConstants.MODE_PEN:
			return "Pen";

		// case EuclidianConstants.MODE_PENCIL:
		// return "Pencil";

		case EuclidianConstants.MODE_FREEHAND_SHAPE:
			return "FreehandShape";

		case EuclidianConstants.MODE_FREEHAND_FUNCTION:
			return "FreehandFunction";

		// case EuclidianConstants.MODE_VISUAL_STYLE:
		// return "VisualStyle";

		case EuclidianConstants.MODE_FITLINE:
			return "FitLine";

		case EuclidianConstants.MODE_CREATE_LIST:
			return "List.Tool";

		case EuclidianConstants.MODE_PROBABILITY_CALCULATOR:
			return "ProbabilityCalculator";

		case EuclidianConstants.MODE_FUNCTION_INSPECTOR:
			return "FunctionInspector";

		// CAS
		case EuclidianConstants.MODE_CAS_EVALUATE:
			return "Evaluate";

		case EuclidianConstants.MODE_CAS_NUMERIC:
			return "Numeric.Tool";

		case EuclidianConstants.MODE_CAS_KEEP_INPUT:
			return "KeepInput";

		case EuclidianConstants.MODE_CAS_EXPAND:
			return "Expand";

		case EuclidianConstants.MODE_CAS_FACTOR:
			return "Factor";

		case EuclidianConstants.MODE_CAS_SUBSTITUTE:
			return "Substitute.Tool";

		case EuclidianConstants.MODE_CAS_SOLVE:
			return "Solve";

		case EuclidianConstants.MODE_CAS_NUMERICAL_SOLVE:
			return "NSolve";
		case EuclidianConstants.MODE_CAS_DERIVATIVE:
			return "Derivative";

		case EuclidianConstants.MODE_CAS_INTEGRAL:
			return "Integral";

		case EuclidianConstants.MODE_ATTACH_DETACH:
			return "AttachDetachPoint";

		// Spreadsheet
		case EuclidianConstants.MODE_SPREADSHEET_ONEVARSTATS:
			return "OneVarStats";

		case EuclidianConstants.MODE_SPREADSHEET_TWOVARSTATS:
			return "TwoVarStats";

		case EuclidianConstants.MODE_SPREADSHEET_MULTIVARSTATS:
			return "MultiVarStats";

		case EuclidianConstants.MODE_SPREADSHEET_CREATE_LIST:
			return "List.Tool";

		case EuclidianConstants.MODE_SPREADSHEET_CREATE_LISTOFPOINTS:
			return "ListOfPoints";

		case EuclidianConstants.MODE_SPREADSHEET_CREATE_MATRIX:
			return "Matrix.Tool";

		case EuclidianConstants.MODE_SPREADSHEET_CREATE_TABLETEXT:
			return "Table.Tool";

		case EuclidianConstants.MODE_SPREADSHEET_CREATE_POLYLINE:
			return "CreatePolyLine";

		case EuclidianConstants.MODE_SPREADSHEET_SUM:
			return "Sum.Tool";

		case EuclidianConstants.MODE_SPREADSHEET_AVERAGE:
			return "Mean.Tool";

		case EuclidianConstants.MODE_SPREADSHEET_COUNT:
			return "Count.Tool";

		case EuclidianConstants.MODE_SPREADSHEET_MIN:
			return "Minimum";

		case EuclidianConstants.MODE_SPREADSHEET_MAX:
			return "Maximum";

		case EuclidianConstants.MODE_EXTREMUM:
			return "Extremum";

		case EuclidianConstants.MODE_ROOTS:
			return "Roots";

		/* WHITEBOARD TOOLS */
		case EuclidianConstants.MODE_SHAPE_LINE:
			return "Line.Tool";
		case EuclidianConstants.MODE_SHAPE_TRIANGLE:
			return "ShapeTriangle";
		case EuclidianConstants.MODE_SHAPE_SQUARE:
			return "ShapeSquare";
		case EuclidianConstants.MODE_SHAPE_RECTANGLE:
			return "ShapeRectangle";
		case EuclidianConstants.MODE_SHAPE_PENTAGON:
			return "Pentagon";
		case EuclidianConstants.MODE_SHAPE_FREEFORM:
			return "ShapeFreeform";
		case EuclidianConstants.MODE_SHAPE_CIRCLE:
			return "Circle";
		case EuclidianConstants.MODE_SHAPE_ELLIPSE:
			return "Ellipse";
		case EuclidianConstants.MODE_MASK:
			return "MaskTool";
		case EuclidianConstants.MODE_ERASER:
			return "Eraser";
		case EuclidianConstants.MODE_HIGHLIGHTER:
			return "Highlighter";
		case EuclidianConstants.MODE_VIDEO:
			return "Video";
		case EuclidianConstants.MODE_CAMERA:
			return "Camera";
		case EuclidianConstants.MODE_PHOTO_LIBRARY:
			return "PhotoLibrary";
		case EuclidianConstants.MODE_AUDIO:
			return "Audio";
		case EuclidianConstants.MODE_CALCULATOR:
			return "Type.GeoGebra";
        case EuclidianConstants.MODE_GRASPABLE_MATH:
            return "Graspable Math";
		case EuclidianConstants.MODE_PDF:
			return "PDF";
		case EuclidianConstants.MODE_EXTENSION:
			return "Web";
		case EuclidianConstants.MODE_SELECT:
			return "Select";
		case EuclidianConstants.MODE_SELECT_MOW:
			return "Select";
		case EuclidianConstants.MODE_TABLE:
			return "Table";
		case EuclidianConstants.MODE_EQUATION:
			return "Equation";
		case EuclidianConstants.MODE_MIND_MAP:
			return "Mindmap";
		case EuclidianConstants.MODE_RULER:
			return "Ruler";
		case EuclidianConstants.MODE_PROTRACTOR:
			return "Protractor";
		case EuclidianConstants.MODE_TRIANGLE_PROTRACTOR:
			return "TriangleProtractor";
		default:
			return "";
		}
	}

	/**
	 * @param mode
	 *            mode number
	 * @return whether mode is MOVE or SELECT
	 */
	static public boolean isMoveOrSelectionMode(int mode) {
		return mode == MODE_MOVE || mode == MODE_SELECT
				|| mode == MODE_SELECT_MOW;
	}

	/**
	 * @param mode mode number
	 * @return true if mode does NOT clear selection when set
	 */
	static public boolean keepSelectionWhenSet(int mode) {
		return isMoveOrSelectionMode(mode)
				|| (mode == MODE_SHOW_HIDE_OBJECT
				|| mode == MODE_SHOW_HIDE_LABEL
				|| mode == MODE_DELETE);
	}

	/**
	 * @param mode
	 *            mode number
	 * @param draggingOccured
	 *            tells if dragging occured
	 *
	 * @return whether mode is MOVE or SELECT, and return false if the mode is
	 *         not compatible with dragging occured
	 */
	static public boolean isMoveOrSelectionModeCompatibleWithDragging(int mode,
			boolean draggingOccured) {
		switch (mode) {
		case MODE_MOVE:
			return !draggingOccured;
		case MODE_SELECT:
		case MODE_SELECT_MOW:
			return true;
		default:
			return false;
		}
	}

	/**
	 * @param mode tool ID
	 * @return whether given tool is only available in Notes app
	 */
	public static boolean isNotesTool(int mode) {
		switch (mode) {
		case EuclidianConstants.MODE_MASK:
		case EuclidianConstants.MODE_MIND_MAP:
		case EuclidianConstants.MODE_RULER:
		case EuclidianConstants.MODE_PROTRACTOR:
		case EuclidianConstants.MODE_TRIANGLE_PROTRACTOR:
		case EuclidianConstants.MODE_VIDEO:
		case EuclidianConstants.MODE_AUDIO:
		case EuclidianConstants.MODE_CALCULATOR:
		case EuclidianConstants.MODE_EXTENSION:
		case EuclidianConstants.MODE_TABLE:
		case EuclidianConstants.MODE_EQUATION:
		case EuclidianConstants.MODE_CAMERA:
		case EuclidianConstants.MODE_PDF:
		case EuclidianConstants.MODE_GRASPABLE_MATH:
		// these do have icons
		case EuclidianConstants.MODE_HIGHLIGHTER:
		case EuclidianConstants.MODE_ERASER:
		case EuclidianConstants.MODE_SHAPE_CIRCLE:
		case EuclidianConstants.MODE_SHAPE_RECTANGLE:
		case EuclidianConstants.MODE_SHAPE_FREEFORM:
		case EuclidianConstants.MODE_SHAPE_PENTAGON:
		case EuclidianConstants.MODE_SHAPE_LINE:
		case EuclidianConstants.MODE_SHAPE_SQUARE:
		case EuclidianConstants.MODE_SHAPE_TRIANGLE:
		case EuclidianConstants.MODE_SHAPE_ELLIPSE:
		case EuclidianConstants.MODE_SELECT_MOW:
			return true;
		default: return false;
		}
	}

	// prevent instantiation
	private EuclidianConstants() {
	}

	/**
	 * @param mode mode ID
	 * @return name of the manual page for the given tool
	 */
	public static String getModeHelpPage(int mode) {
		switch (mode) {
			case MODE_JOIN:
				return "Line"; // Join
			case MODE_CIRCLE_TWO_POINTS:
				return "Circle_with_Center_through_Point"; // Circle2
			case MODE_CIRCLE_THREE_POINTS:
				return "Circle_through_3_Points"; // Circle3
			case MODE_CONIC_FIVE_POINTS:
				return "Conic_through_5_Points"; // Conic5
			case MODE_MIDPOINT:
				return "Midpoint_or_Center"; // Midpoint.Tool
			case MODE_SEMICIRCLE:
				return "Semicircle_through_2_Points"; // Semicircle.Tool
			case MODE_CIRCLE_POINT_RADIUS:
				return "Circle_with_Center_and_Radius"; // CirclePointRadius
			case MODE_DISTANCE:
				return "Distance_or_Length"; // Distance
			case MODE_POLAR_DIAMETER:
				return "Polar_or_Diameter_Line"; // PolarDiameter
			case MODE_SEGMENT_FIXED:
				return "Segment_with_Given_Length"; // SegmentFixed
			case MODE_ANGLE_FIXED:
				return "Angle_with_Given_Size"; // AngleFixed
			case MODE_FITLINE:
				return "Best_Fit_Line"; // FitLine
			case MODE_SELECTION_LISTENER:
			case MODE_SELECT:
				return "Select_Objects"; // Select
			case MODE_PLANE_THREE_POINTS:
				return "Plane_through_3_Points"; // PlaneThreePoint
			case MODE_ORTHOGONAL_THREE_D:
				return "Perpendicular_Line"; // OrthogonalThreeD
			case MODE_SPHERE_POINT_RADIUS:
				return "Sphere_with_Center_and_Radius"; // SpherePointRadius
			case MODE_SPHERE_TWO_POINTS:
				return "Sphere_with_Center_through_Point"; // Sphere2
			case MODE_EXTRUSION:
				return "Extrude_to_Prism_or_Cylinder"; // Extrusion
			case MODE_CONIFY:
				return "Extrude_to_Pyramid_or_Cone"; // Conify
			case MODE_TETRAHEDRON:
				return "Regular_Tetrahedron"; // Tetrahedron
			case MODE_ROTATEVIEW:
				return "Rotate_3D_Graphics_View"; // RotateView
			case MODE_CIRCLE_POINT_RADIUS_DIRECTION:
				return "Circle_with_Center_Radius_and_Direction"; // CirclePointRadiusDirection
			case MODE_CIRCLE_AXIS_POINT:
				return "Circle_with_Axis_through_Point"; // CircleAxisPoint
			case MODE_CAS_NUMERICAL_SOLVE:
				return "Solve_Numerically"; // NSolve
			case MODE_SPREADSHEET_CREATE_POLYLINE:
				return "Polyline"; // CreatePolyLine
			case MODE_SPREADSHEET_ONEVARSTATS:
				return "One_Variable_Analysis"; // OneVarStats
			case MODE_SPREADSHEET_TWOVARSTATS:
				return "Two_Variable_Regression_Analysis"; // TwoVarStats
			case MODE_SPREADSHEET_MULTIVARSTATS:
				return "Multiple_Variable_Analysis"; // MultiVarStats
			case MODE_VIEW_IN_FRONT_OF:
				return "View_in_front_of";
			default: return toTitleCase(getModeTextSimple(mode)
					.replaceAll("(.)([A-Z])", "$1_$2").replace("_/_", "_"));
		}
	}

	private static String toTitleCase(String name) {
		return Stream.of("About", "Around", "From", "By", "In", "On", "Of")
				.reduce(name, (ret, prop) ->
						ret.replace(prop + "_", prop.toLowerCase(Locale.ROOT) + "_"));
	}

	public static String getHelpTransKey(int mode) {
		return EuclidianConstants.getModeTextSimple(mode) + ".Help";
	}
}
