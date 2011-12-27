package geogebra.web.euclidian;

import com.google.gwt.canvas.client.Canvas;

import geogebra.web.awt.BasicStroke;
import geogebra.web.awt.Color;
import geogebra.web.kernel.gawt.Ellipse2D;
import geogebra.web.kernel.gawt.Line2D;



public class EuclidianView extends BaseEuclidianView {
	
	protected static final long serialVersionUID = 1L;

	protected static final int MIN_WIDTH = 50;
	protected static final int MIN_HEIGHT = 50;
	
	private static final String EXPORT1 = "Export_1"; // Points used to define corners for export (if they exist)
	private static final String EXPORT2 = "Export_2";

	// pixel per centimeter (at 72dpi)
	protected static final double PRINTER_PIXEL_PER_CM = 72.0 / 2.54;

	public static final double MODE_ZOOM_FACTOR = 1.5;

	public static final double MOUSE_WHEEL_ZOOM_FACTOR = 1.1;

	public static final double SCALE_STANDARD = 50;

	// public static final double SCALE_MAX = 10000;
	// public static final double SCALE_MIN = 0.1;
	public static final double XZERO_STANDARD = 215;

	public static final double YZERO_STANDARD = 315;

	public static final int LINE_TYPE_FULL = 0;

	public static final int LINE_TYPE_DASHED_SHORT = 10;

	public static final int LINE_TYPE_DASHED_LONG = 15;

	public static final int LINE_TYPE_DOTTED = 20;

	public static final int LINE_TYPE_DASHED_DOTTED = 30;

	public EuclidianView(Canvas canvas,
            EuclidianController euclidiancontroller, boolean[] showAxes,
            boolean showGrid) {
	    // TODO Auto-generated constructor stub
    }

	public static final Integer[] getLineTypes() {
		Integer[] ret = { new Integer(LINE_TYPE_FULL),
				new Integer(LINE_TYPE_DASHED_LONG),
				new Integer(LINE_TYPE_DASHED_SHORT),
				new Integer(LINE_TYPE_DOTTED),
				new Integer(LINE_TYPE_DASHED_DOTTED) };
		return ret;
	}
	
	// need to clip just outside the viewing area when drawing eg vectors
	// as a near-horizontal thick vector isn't drawn correctly otherwise
	public static final int CLIP_DISTANCE = 5;
	
	public static final int AXES_LINE_TYPE_FULL = 0;

	public static final int AXES_LINE_TYPE_ARROW = 1;

	public static final int AXES_LINE_TYPE_FULL_BOLD = 2;

	public static final int AXES_LINE_TYPE_ARROW_BOLD = 3;



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

	// G.Sturr added 2009-9-21 
	public static final Integer[] getPointStyles() {
		Integer[] ret = { new Integer(POINT_STYLE_DOT),
				new Integer(POINT_STYLE_CROSS),
				new Integer(POINT_STYLE_CIRCLE),
				new Integer(POINT_STYLE_PLUS),
				new Integer(POINT_STYLE_FILLED_DIAMOND),
				new Integer(POINT_STYLE_EMPTY_DIAMOND),
				new Integer(POINT_STYLE_TRIANGLE_NORTH),
				new Integer(POINT_STYLE_TRIANGLE_SOUTH),
				new Integer(POINT_STYLE_TRIANGLE_EAST),
				new Integer(POINT_STYLE_TRIANGLE_WEST)	
				};
		return ret;
	}
	//end		
	
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
	
	public static final int POINT_CAPTURING_OFF = 0;
	public static final int POINT_CAPTURING_ON = 1;
	public static final int POINT_CAPTURING_ON_GRID = 2;
	public static final int POINT_CAPTURING_AUTOMATIC = 3;
	public static final int POINT_CAPTURING_STICKY_POINTS = 4;
	
	public static final int TOOLTIPS_AUTOMATIC = 0;
	public static final int TOOLTIPS_ON = 1;
	public static final int TOOLTIPS_OFF = 2;
	
	protected int tooltipsInThisView = TOOLTIPS_AUTOMATIC;
	
//	 Michael Borcherds 2008-04-28 
	public static final int GRID_CARTESIAN = 0;
	public static final int GRID_ISOMETRIC = 1;
	public static final int GRID_POLAR = 2;
	private int gridType = GRID_CARTESIAN;
	

	// zoom rectangle colors
	protected static final Color colZoomRectangle = new Color(200, 200, 230);
	protected static final Color colZoomRectangleFill = new Color(200, 200, 230, 50);

	// STROKES
	protected static MyBasicStroke standardStroke = new MyBasicStroke(1.0f);

	protected static MyBasicStroke selStroke = new MyBasicStroke(
			1.0f + SELECTION_ADD);

	// protected static MyBasicStroke thinStroke = new MyBasicStroke(1.0f);

	// axes strokes
	protected static BasicStroke defAxesStroke = new BasicStroke(1.0f,
			BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER);

	protected static BasicStroke boldAxesStroke = new BasicStroke(2.0f, // changed from 1.8f (same as bold grid) Michael Borcherds 2008-04-12
			BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER);

	// axes and grid stroke
	protected BasicStroke axesStroke, tickStroke, gridStroke;

	protected Line2D.Double tempLine = new Line2D.Double();
	protected Ellipse2D.Double circle = new Ellipse2D.Double(); //polar grid circles
	protected boolean unitAxesRatio;

	static public MyBasicStroke getDefaultStroke() {
		return standardStroke;
	}

	static public MyBasicStroke getDefaultSelectionStroke() {
		return selStroke;
	}
	
	private boolean disableRepaint;
	public void setDisableRepaint(boolean disableRepaint) {
		this.disableRepaint = disableRepaint;
	}
	
	//temp@Override
	final public void repaintView() {
		if (!disableRepaint) {
			//clear();
			//temppaint();
		}
	}

	/**
	 * Creates a stroke with thickness width, dashed according to line style
	 * type.
	 * @param width 
	 * @param type 
	 * @return stroke
	 */
	public static BasicStroke getStroke(float width, int type) {
		float[] dash;

		switch (type) {
		case EuclidianView.LINE_TYPE_DOTTED:
			dash = new float[2];
			dash[0] = width; // dot
			dash[1] = 3.0f; // space
			break;

		case EuclidianView.LINE_TYPE_DASHED_SHORT:
			dash = new float[2];
			dash[0] = 4.0f + width;
			// short dash
			dash[1] = 4.0f; // space
			break;

		case EuclidianView.LINE_TYPE_DASHED_LONG:
			dash = new float[2];
			dash[0] = 8.0f + width; // long dash
			dash[1] = 8.0f; // space
			break;

		case EuclidianView.LINE_TYPE_DASHED_DOTTED:
			dash = new float[4];
			dash[0] = 8.0f + width; // dash
			dash[1] = 4.0f; // space before dot
			dash[2] = width; // dot
			dash[3] = dash[1]; // space after dot
			break;

		default: // EuclidianView.LINE_TYPE_FULL
			dash = null;
		}

		int endCap = dash != null ? BasicStroke.CAP_BUTT : standardStroke.getEndCap();

		return new BasicStroke(width, endCap, standardStroke.getLineJoin(),
				standardStroke.getMiterLimit(), dash, 0.0f);
	}

}
