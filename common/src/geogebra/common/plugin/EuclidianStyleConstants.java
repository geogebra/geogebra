package geogebra.common.plugin;

public class EuclidianStyleConstants {
	public static final int LINE_TYPE_FULL = 0;

	public static final int LINE_TYPE_DASHED_SHORT = 10;

	public static final int LINE_TYPE_DASHED_LONG = 15;

	public static final int LINE_TYPE_DOTTED = 20;

	public static final int LINE_TYPE_DASHED_DOTTED = 30;

	
	public static final int RIGHT_ANGLE_STYLE_NONE = 0;

	public static final int RIGHT_ANGLE_STYLE_SQUARE = 1;

	public static final int RIGHT_ANGLE_STYLE_DOT = 2;

	public static final int RIGHT_ANGLE_STYLE_L = 3; // Belgian style

	public static final int DEFAULT_POINT_SIZE = 3;

	public static final int DEFAULT_LINE_THICKNESS = 2;

	public static final int DEFAULT_ANGLE_SIZE = 30;

	public static final int DEFAULT_LINE_TYPE = LINE_TYPE_FULL;
	
	
	public static final int LINE_TYPE_HIDDEN_NONE = 0;
	
	public static final int LINE_TYPE_HIDDEN_DASHED = 1;
	
	public static final int LINE_TYPE_HIDDEN_AS_NOT_HIDDEN = 2;
	
	public static final int DEFAULT_LINE_TYPE_HIDDEN = LINE_TYPE_HIDDEN_DASHED;

	public static final float SELECTION_ADD = 2.0f;

	// ggb3D 2008-10-27 : mode constants moved to EuclidianConstants.java
	
	public static final int AXES_TICK_STYLE_MAJOR_MINOR = 0;

	public static final int AXES_TICK_STYLE_MAJOR = 1;

	public static final int AXES_TICK_STYLE_NONE = 2;
	
	
	// used in the XML, DO NOT CHANGE
	public static final int AXES_RIGHT_ARROW = 	1; // also TOP
	public static final int AXES_BOLD = 		2;
	public static final int AXES_LEFT_ARROW = 	4; // also BOTTOM
	public static final int AXES_FILL_ARROWS = 	8;
		
	// used in the XML, DO NOT CHANGE
	public static final int AXES_LINE_TYPE_FULL = 					0;
	public static final int AXES_LINE_TYPE_ARROW = 					AXES_RIGHT_ARROW;
	public static final int AXES_LINE_TYPE_FULL_BOLD = 				AXES_BOLD;
	public static final int AXES_LINE_TYPE_ARROW_BOLD = 			AXES_RIGHT_ARROW + AXES_BOLD;
	public static final int AXES_LINE_TYPE_ARROW_FILLED = 			AXES_RIGHT_ARROW + AXES_FILL_ARROWS;
	public static final int AXES_LINE_TYPE_TWO_ARROWS = 			AXES_RIGHT_ARROW + AXES_LEFT_ARROW;
	public static final int AXES_LINE_TYPE_TWO_ARROWS_FILLED = 		AXES_RIGHT_ARROW + AXES_LEFT_ARROW 	+ AXES_FILL_ARROWS;
	public static final int AXES_LINE_TYPE_ARROW_FILLED_BOLD = 		AXES_RIGHT_ARROW + AXES_BOLD 		+ AXES_FILL_ARROWS;
	public static final int AXES_LINE_TYPE_TWO_ARROWS_BOLD = 		AXES_RIGHT_ARROW + AXES_LEFT_ARROW 	+ AXES_BOLD;
	public static final int AXES_LINE_TYPE_TWO_ARROWS_FILLED_BOLD = AXES_RIGHT_ARROW + AXES_LEFT_ARROW 	+ AXES_BOLD 	+ AXES_FILL_ARROWS;
	
	// for the options menu
	public static Integer[] lineStyleOptions = { EuclidianStyleConstants.AXES_LINE_TYPE_FULL,
			EuclidianStyleConstants.AXES_LINE_TYPE_ARROW,
			EuclidianStyleConstants.AXES_LINE_TYPE_ARROW_FILLED,
			EuclidianStyleConstants.AXES_LINE_TYPE_TWO_ARROWS,
			EuclidianStyleConstants.AXES_LINE_TYPE_TWO_ARROWS_FILLED
			};



	public static final int POINT_STYLE_DOT = 0;
	public static final int POINT_STYLE_CROSS = 1;
	public static final int POINT_STYLE_CIRCLE = 2;
	public static final int POINT_STYLE_PLUS = 3;
	public static final int POINT_STYLE_FILLED_DIAMOND = 4;
	public static final int POINT_STYLE_EMPTY_DIAMOND = 5;
	public static final int POINT_STYLE_TRIANGLE_NORTH = 6;
	public static final int POINT_STYLE_TRIANGLE_SOUTH = 7;
	public static final int POINT_STYLE_TRIANGLE_EAST = 8;
	public static final int POINT_STYLE_TRIANGLE_WEST = 9;
	public static final int MAX_POINT_STYLE = 9;
	
	public static final int MAX_LAYERS = 9;
	
	public static final int POINT_CAPTURING_OFF = 0;
	public static final int POINT_CAPTURING_ON = 1;
	public static final int POINT_CAPTURING_ON_GRID = 2;
	public static final int POINT_CAPTURING_AUTOMATIC = 3;
	public static final int POINT_CAPTURING_STICKY_POINTS = 4;
	
	public static final int POINT_CAPTURING_DEFAULT = POINT_CAPTURING_AUTOMATIC; 

	// we don't want POINT_CAPTURING_STICKY_POINTS in the XML! 
	public static final int POINT_CAPTURING_XML_MAX = 3;

	public static final int TOOLTIPS_AUTOMATIC = 0;
	public static final int TOOLTIPS_ON = 1;
	public static final int TOOLTIPS_OFF = 2;
	// since V3.0 this factor is 1, before it was 0.5
		final public static double DEFAULT_GRID_DIST_FACTOR = 1;
	public static double automaticGridDistanceFactor = DEFAULT_GRID_DIST_FACTOR;
}
