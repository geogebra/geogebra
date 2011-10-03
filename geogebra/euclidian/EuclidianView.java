/* 
 GeoGebra - Dynamic Mathematics for Everyone
 http://www.geogebra.org

 This file is part of GeoGebra.

 This program is free software; you can redistribute it and/or modify it 
 under the terms of the GNU General Public License as published by 
 the Free Software Foundation.
 
 */

package geogebra.euclidian;

import geogebra.euclidian.DrawableList.DrawableIterator;
import geogebra.gui.GuiManager;
import geogebra.gui.layout.panels.EuclidianDockPanelAbstract;
import geogebra.kernel.AlgoBoxPlot;
import geogebra.kernel.AlgoElement;
import geogebra.kernel.AlgoFunctionAreaSums;
import geogebra.kernel.AlgoIntegralFunctions;
import geogebra.kernel.AlgoSlope;
import geogebra.kernel.Construction;
import geogebra.kernel.ConstructionDefaults;
import geogebra.kernel.GeoAngle;
import geogebra.kernel.GeoBoolean;
import geogebra.kernel.GeoButton;
import geogebra.kernel.GeoConicPart;
import geogebra.kernel.GeoCurveCartesian;
import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoFunction;
import geogebra.kernel.GeoFunctionNVar;
import geogebra.kernel.GeoImage;
import geogebra.kernel.GeoList;
import geogebra.kernel.GeoLocus;
import geogebra.kernel.GeoNumeric;
import geogebra.kernel.GeoPoint;
import geogebra.kernel.GeoPolyLine;
import geogebra.kernel.GeoPolygon;
import geogebra.kernel.GeoText;
import geogebra.kernel.GeoTextField;
import geogebra.kernel.GeoVec2D;
import geogebra.kernel.Kernel;
import geogebra.kernel.ParametricCurve;
import geogebra.kernel.View;
import geogebra.kernel.Matrix.CoordMatrix;
import geogebra.kernel.Matrix.Coords;
import geogebra.kernel.arithmetic.FunctionalNVar;
import geogebra.kernel.arithmetic.NumberValue;
import geogebra.kernel.cas.AlgoIntegralDefinite;
import geogebra.kernel.implicit.GeoImplicitPoly;
import geogebra.kernel.kernelND.GeoConicND;
import geogebra.kernel.kernelND.GeoDirectionND;
import geogebra.kernel.kernelND.GeoLineND;
import geogebra.kernel.kernelND.GeoPlaneND;
import geogebra.kernel.kernelND.GeoPointND;
import geogebra.kernel.kernelND.GeoRayND;
import geogebra.kernel.kernelND.GeoSegmentND;
import geogebra.kernel.kernelND.GeoVectorND;
import geogebra.main.Application;
import geogebra.main.settings.AbstractSettings;
import geogebra.main.settings.EuclidianSettings;
import geogebra.main.settings.SettingListener;
import geogebra.util.MyMath;
import geogebra.util.Unicode;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Toolkit;
import java.awt.Transparency;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.TreeSet;

import javax.swing.JPanel;
import javax.swing.Timer;

/**
 * 
 * @author Markus Hohenwarter
 * @version
 */
public class EuclidianView extends JPanel  
implements EuclidianViewInterface, Printable, SettingListener {

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
	
	/**
	 * returns true if the axes ratio is 1
	 * @return true if the axes ratio is 1
	 */
	public boolean isUnitAxesRatio(){
		return unitAxesRatio || gridType == GRID_POLAR;
	}
	/**
	 * Set unit axes ratio to 1
	 * @param flag true to set to 1, false to allow user 
	 */
	public void setUnitAxesRatio(boolean flag){
		unitAxesRatio =flag;
		if(flag)
			updateBounds();
	}

	protected static RenderingHints defRenderingHints = new RenderingHints(null);
	{
		defRenderingHints.put(RenderingHints.KEY_RENDERING,
				RenderingHints.VALUE_RENDER_SPEED);
		defRenderingHints.put(RenderingHints.KEY_ALPHA_INTERPOLATION,
				RenderingHints.VALUE_ALPHA_INTERPOLATION_SPEED);
		defRenderingHints.put(RenderingHints.KEY_COLOR_RENDERING,
				RenderingHints.VALUE_COLOR_RENDER_SPEED);
		
		// This ensures fast image drawing. Note that DrawImage changes
		// this hint for scaled and sheared images to improve their quality 
		defRenderingHints.put(RenderingHints.KEY_INTERPOLATION,
				RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);				
	}

	// FONTS
	public Font fontPoint, fontLine, fontVector, fontConic, fontCoords,
			fontAxes, fontAngle;

	int fontSize;

	// member variables
	protected Application app;

	protected Kernel kernel;

	protected EuclidianController euclidianController;

	AffineTransform coordTransform = new AffineTransform();

	// use sensible defaults, see #640
	int width = Application.getScreenSize().width;
	int height = Application.getScreenSize().height;

	protected NumberFormat[] axesNumberFormat;

	protected NumberFormat printScaleNF;
	NumberValue xminObject, xmaxObject, yminObject, ymaxObject;
	/**
	 * @return the xminObject
	 */
	public GeoNumeric getXminObject() {
		return (GeoNumeric) xminObject;
	}

	/**
	 * @param xminObjectNew the xminObject to set
	 */
	public void setXminObject(NumberValue xminObjectNew) {
		if (xminObject != null)
			((GeoNumeric)xminObject).removeEVSizeListener(this);
		if (xminObjectNew == null) {
			this.xminObject = new GeoNumeric(kernel.getConstruction());
			updateBoundObjects();
		} else {
			this.xminObject = xminObjectNew;
		}
		setSizeListeners();	
	}

	/**
	 * @return the xmaxObject
	 */
	public GeoNumeric getXmaxObject() {
		return (GeoNumeric) xmaxObject;
	}

	/**
	 * @param xmaxObjectNew the xmaxObject to set
	 */
	public void setXmaxObject(NumberValue xmaxObjectNew) {
		if (xmaxObject != null)
			((GeoNumeric)xmaxObject).removeEVSizeListener(this);
		if(xmaxObjectNew == null) {
			this.xmaxObject = new GeoNumeric(kernel.getConstruction());
			updateBoundObjects();
		} else {
			this.xmaxObject = xmaxObjectNew;
		}
		setSizeListeners();	
	}

	/**
	 * @return the yminObject
	 */
	public GeoNumeric getYminObject() {
		return (GeoNumeric) yminObject;
	}

	/**
	 * @param yminObjectNew the yminObject to set
	 */
	public void setYminObject(NumberValue yminObjectNew) {
		if (yminObject != null)
			((GeoNumeric)yminObject).removeEVSizeListener(this);
		if (yminObjectNew == null) {
			this.yminObject = new GeoNumeric(kernel.getConstruction());
			updateBoundObjects();
		} else {
			this.yminObject = yminObjectNew;
		}
		setSizeListeners();		
	}

	private void setSizeListeners() {		
		((GeoNumeric)xminObject).addEVSizeListener(this);
		((GeoNumeric)yminObject).addEVSizeListener(this);
		((GeoNumeric)xmaxObject).addEVSizeListener(this);
		((GeoNumeric)ymaxObject).addEVSizeListener(this);
	}

	/**
	 * @return the ymaxObject
	 */
	public GeoNumeric getYmaxObject() {
		return (GeoNumeric) ymaxObject;
	}

	/**
	 * @param ymaxObjectNew the ymaxObject to set
	 */
	public void setYmaxObject(NumberValue ymaxObjectNew) {
		if (ymaxObject != null)
			((GeoNumeric)ymaxObject).removeEVSizeListener(this);
		if (ymaxObjectNew == null) {
			this.ymaxObject = new GeoNumeric(kernel.getConstruction());
			updateBoundObjects();
		} else {
			this.ymaxObject = ymaxObjectNew;
		}
		setSizeListeners();
	}

	double xmin, xmax, ymin, ymax, invXscale, invYscale, xZero, yZero, xscale,
			yscale, scaleRatio = 1.0; // ratio yscale / xscale
	double xZeroOld, yZeroOld;

	protected double[] AxesTickInterval = { 1, 1 }; // for axes =

	// axesNumberingDistances /
	// 2

	boolean showGrid = false;

	protected boolean antiAliasing = true;

	boolean showMouseCoords = false;
	boolean allowShowMouseCoords = true;
	

	boolean showAxesRatio = false;
	private boolean highlightAnimationButtons = false;

	protected int pointCapturingMode; // snap to grid points

	// added by Loic BEGIN
	// right angle
	//int rightAngleStyle = EuclidianView.RIGHT_ANGLE_STYLE_SQUARE;

	// END
	
	int mode = EuclidianConstants.MODE_MOVE;

	protected boolean[] showAxes = { true, true };
	private boolean showAxesCornerCoords = true;
	
	protected boolean[] showAxesNumbers = { true, true };

	protected String[] axesLabels = { null, null };

	protected String[] axesUnitLabels = { null, null };

	protected boolean[] piAxisUnit = { false, false };

	protected int[] axesTickStyles = { AXES_TICK_STYLE_MAJOR,
			AXES_TICK_STYLE_MAJOR };

	// for axes labeling with numbers
	protected boolean[] automaticAxesNumberingDistances = { true, true };

	protected double[] axesNumberingDistances = { 2, 2 };

	// distances between grid lines
	protected boolean automaticGridDistance = true;
	// since V3.0 this factor is 1, before it was 0.5
	final public static double DEFAULT_GRID_DIST_FACTOR = 1;
	public static double automaticGridDistanceFactor = DEFAULT_GRID_DIST_FACTOR;

	double[] gridDistances = { 2, 2, Math.PI/6 };

	protected int gridLineStyle, axesLineType;
	
	protected boolean gridIsBold=false; // Michael Borcherds 2008-04-11

	// colors: axes, grid, background
	protected Color axesColor, gridColor, bgColor;

	protected double printingScale;

	// Map (geo, drawable) for GeoElements and Drawables
	protected HashMap<GeoElement,Drawable> DrawableMap = new HashMap<GeoElement,Drawable>(500);
	
	protected ArrayList<GeoPointND> stickyPointList = new ArrayList<GeoPointND>();

	protected DrawableList allDrawableList = new DrawableList();
	
	public static final int MAX_LAYERS = 9;
	
	public DrawableList drawLayers[]; 

	// on add: change resetLists()

	protected DrawableList bgImageList = new DrawableList();

	Previewable previewDrawable;

	protected Rectangle selectionRectangle;

	// temp
	// public static final int DRAW_MODE_DIRECT_DRAW = 0;
	// public static final int DRAW_MODE_BACKGROUND_IMAGE = 1;

	// or use volatile image
	// protected int drawMode = DRAW_MODE_BACKGROUND_IMAGE;
	protected BufferedImage bgImage;
	protected Graphics2D bgGraphics; // g2d of bgImage
	protected Image resetImage, playImage, pauseImage, upArrowImage, downArrowImage;
	private boolean firstPaint = true;
	
	// temp image
	private Graphics2D g2Dtemp = new BufferedImage(5, 5, BufferedImage.TYPE_INT_RGB).createGraphics();
	//public Graphics2D lastGraphics2D;
	
	protected StringBuilder sb = new StringBuilder();

	protected Cursor defaultCursor;
	
	// ggb3D 2009-02-05
	private Hits hits;
	
	//set EuclidianView no - 2 for 2nd EulidianView, 1 for 1st EuclidianView and Applet
	// EVNO_GENERAL for others
	private int evNo=1;
	public static int EVNO_GENERAL = 1001;
	
	public EuclidianView(EuclidianController ec, boolean[] showAxes,
			boolean showGrid) {
		this(ec, showAxes, showGrid, 1);
	}

	/**
	 * Creates EuclidianView
	 * @param ec controller
	 * @param showAxes 
	 * @param showGrid 
	 * @param evno number of this view 
	 */
	public EuclidianView(EuclidianController ec, boolean[] showAxes,
			boolean showGrid, int evno) {
		
		// Michael Borcherds 2008-03-01
		  drawLayers = new DrawableList[MAX_LAYERS+1];
		  for (int k=0; k <= MAX_LAYERS ; k++) {
		     drawLayers[k] = new DrawableList();
		  }		  
		evNo = evno;
		euclidianController = ec;
		kernel = ec.getKernel();
		app = ec.getApplication();		
		
		this.showAxes[0] = showAxes[0];
		this.showAxes[1] = showAxes[1];
		this.showGrid = showGrid;

		axesNumberFormat = new NumberFormat[2];
		axesNumberFormat[0] = NumberFormat.getInstance(Locale.ENGLISH);
		axesNumberFormat[1] = NumberFormat.getInstance(Locale.ENGLISH);
		axesNumberFormat[0].setGroupingUsed(false);
		axesNumberFormat[1].setGroupingUsed(false);

		printScaleNF = NumberFormat.getInstance(Locale.ENGLISH);
		printScaleNF.setGroupingUsed(false);
		printScaleNF.setMaximumFractionDigits(5);

		// algebra controller will take care of our key events
		setFocusable(true);


		setLayout(null);
		setMinimumSize(new Dimension(20, 20));
		euclidianController.setView(this);
		euclidianController.setPen(new EuclidianPen(app,this));
		
		attachView();

		// register Listener
		addMouseMotionListener(euclidianController);
		addMouseListener(euclidianController);
		addMouseWheelListener(euclidianController);
		addComponentListener(euclidianController);
			
		// no repaint
		xminObject = new GeoNumeric(kernel.getConstruction());
		xmaxObject = new GeoNumeric(kernel.getConstruction());
		yminObject = new GeoNumeric(kernel.getConstruction());
		ymaxObject = new GeoNumeric(kernel.getConstruction());
		initView(false);
		
		//updateRightAngleStyle(app.getLocale());
		
		// ggb3D 2009-02-05
		hits=new Hits();
		
		// enable drop transfers 
		setTransferHandler(new EuclidianViewTransferHandler(this));
		
		// settings from XML for EV1, EV2
		// not for eg probability calculator
		if (evNo == 1 || evNo == 2) {
			EuclidianSettings es = app.getSettings().getEuclidian(evNo);
			settingsChanged(es);
			es.addListener(this);
		}
	}
	
	public Application getApplication() {
		return app;
	}
	
	
	/** 
	 * Get styleBar 
	 */
	EuclidianStyleBar styleBar;
	public EuclidianStyleBar getStyleBar(){
		if(styleBar==null){
			styleBar = new EuclidianStyleBar(this);
		}
		
		return styleBar;
	}
	
	public boolean hasStyleBar() {
		return styleBar != null;
	}
	
	
	
	public void updateRightAngleStyle(Locale locale) {				
		// change rightAngleStyle for German to
        // EuclidianView.RIGHT_ANGLE_STYLE_DOT
        if (getRightAngleStyle() != RIGHT_ANGLE_STYLE_NONE) {
	        if (locale.getLanguage().equals("de") ||
	        	locale.getLanguage().equals("hu")) {
	        	setRightAngleStyle(RIGHT_ANGLE_STYLE_DOT);
	        } else {
	        	setRightAngleStyle(RIGHT_ANGLE_STYLE_SQUARE);
	        }
        }
	}

	protected void initView(boolean repaint) {
		// preferred size
		setPreferredSize(null);
		
		// init grid's line type
		setGridLineStyle(LINE_TYPE_DASHED_SHORT);
		setAxesLineStyle(AXES_LINE_TYPE_ARROW);
		setAxesColor(Color.black); // Michael Borcherds 2008-01-26 was darkgray
		setGridColor(Color.lightGray);
		setBackground(Color.white);

		// showAxes = true;
		// showGrid = false;
		pointCapturingMode = POINT_CAPTURING_AUTOMATIC;

		// added by Loic BEGIN
		//app.rightAngleStyle = EuclidianView.RIGHT_ANGLE_STYLE_SQUARE;
		// END
			
		showAxesNumbers[0] = true;
		showAxesNumbers[1] = true;
		axesLabels[0] = null;
		axesLabels[1] = null;
		axesUnitLabels[0] = null;
		axesUnitLabels[1] = null;
		piAxisUnit[0] = false;
		piAxisUnit[1] = false;
		axesTickStyles[0] = AXES_TICK_STYLE_MAJOR;
		axesTickStyles[1] = AXES_TICK_STYLE_MAJOR;

		// for axes labeling with numbers
		automaticAxesNumberingDistances[0] = true;
		automaticAxesNumberingDistances[1] = true;

		// distances between grid lines
		automaticGridDistance = true;
		
		setStandardCoordSystem(repaint);
	}
	
	public void setStandardCoordSystem() {
		setStandardCoordSystem(true);
	}
	
	private void setStandardCoordSystem(boolean repaint) {
		setCoordSystem(XZERO_STANDARD, YZERO_STANDARD, SCALE_STANDARD,
				SCALE_STANDARD, repaint);
	}
	
	public boolean hasPreferredSize() {
		Dimension prefSize = getPreferredSize();
		
		return prefSize != null &&
			prefSize.width > MIN_WIDTH &&
			prefSize.height > MIN_HEIGHT;
	}

	protected void resetLists() {
		DrawableMap.clear();
		stickyPointList.clear();
		allDrawableList.clear();
		bgImageList.clear();
		
		for (int i=0 ; i<= app.maxLayerUsed ; i++) drawLayers[i].clear(); // Michael Borcherds 2008-02-29

		setToolTipText(null);
	}

	public ArrayList<GeoPointND> getStickyPointList() {
		return stickyPointList;
	}

	public void attachView() {
		kernel.notifyAddAll(this);
		kernel.attach(this);
	}

	/*
	 * public void detachView() { kernel.detach(this); clearView();
	 * //kernel.notifyRemoveAll(this); }
	 */

	public Kernel getKernel() {
		return kernel;
	}

	/**
	 * Returns point capturing mode.
	 */
	final public int getPointCapturingMode() {
		return pointCapturingMode;
	}

	/**
	 * Set capturing of points to the grid.
	 */
	public void setPointCapturing(int mode) {
		pointCapturingMode = mode;
	}

	/**
	 * Returns grid type.
	 */
	final public int getGridType() {
		return gridType;
	}

	/**
	 * Set grid type.
	 */
	public void setGridType(int type) {
		gridType = type;		
		if(type == GRID_POLAR)
			updateBounds();
	}

	
	/**
	 * Returns the bounding box of all Drawable objects in this view in screen coordinates.	 
	 */
	public Rectangle getBounds() {
		Rectangle result = null;
		
		DrawableIterator it = allDrawableList.getIterator();
		while (it.hasNext()) {    		
			Drawable d = it.next();
			Rectangle bb = d.getBounds();
			if (bb != null) {
				if (result == null) 
					result = new Rectangle(bb); // changed () to (bb) bugfix, otherwise top-left of screen is always included
				// add bounding box of list element
				result.add(bb);
			}			   		
    	}        		
    	
		// Cong Liu
		if (result == null) {
			result = new Rectangle(0,0,0,0);
		}
		return result;
	}    
	
//	 
	/**
	 * Sets the global size for checkboxes.
	 * Michael Borcherds 2008-05-12
	 * @param size 13 or 26
	 */
	public void setBooleanSize(int size) {

		// only 13 and 26 currently allowed
		app.booleanSize = (size == 13) ? 13 : 26;
		
		updateAllDrawables(true);
	}

	final public int getBooleanSize() {
		return app.booleanSize;
	}

	/**
	 * Sets the global style for point drawing.
	 * @param style 
	 */
	public void setPointStyle(int style) {
		if (style > 0 && style <= MAX_POINT_STYLE)
			app.pointStyle = style;
		else
			app.pointStyle = POINT_STYLE_DOT;
		
		updateAllDrawables(true);
	}

	final public int getPointStyle() {
		return app.pointStyle;
	}

	public void setAllowToolTips(int setto) {
		tooltipsInThisView = setto;
	}

	final public int getAllowToolTips() {
		return tooltipsInThisView;
	}

	public void setToolTipText(String plain) {
		if (tooltipsInThisView == TOOLTIPS_ON || tooltipsInThisView == TOOLTIPS_AUTOMATIC)
			super.setToolTipText(plain);
	}

	// added by Loic BEGIN
	/**
	 * Sets the global style for rightAngle drawing.
	 * @param style 
	 */
	public void setRightAngleStyle(int style) {
		app.rightAngleStyle = style;
		updateAllDrawables(true);
	}

	final public int getRightAngleStyle() {
		return app.rightAngleStyle;
	}

	// END
	final void addBackgroundImage(DrawImage img) {
		bgImageList.addUnique(img);
		//drawImageList.remove(img);

		// Michael Borcherds 2008-02-29
		int layer = img.getGeoElement().getLayer();
		drawLayers[layer].remove(img);
	}

	final void removeBackgroundImage(DrawImage img) {
		bgImageList.remove(img);
		//drawImageList.add(img);
		
		// Michael Borcherds 2008-02-29
		int layer = img.getGeoElement().getLayer();
		drawLayers[layer].add(img);
	}

	static public MyBasicStroke getDefaultStroke() {
		return standardStroke;
	}

	static public MyBasicStroke getDefaultSelectionStroke() {
		return selStroke;
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

		int endCap = dash != null ? BasicStroke.CAP_BUTT : standardStroke
				.getEndCap();

		return new BasicStroke(width, endCap, standardStroke.getLineJoin(),
				standardStroke.getMiterLimit(), dash, 0.0f);
	}

	public void updateFonts() {
		fontSize = app.getFontSize();

		fontPoint = app.getPlainFont().deriveFont(Font.PLAIN, fontSize);
		fontAngle = fontPoint;
		fontLine = fontPoint;
		fontVector = fontPoint;
		fontConic = fontPoint;
		fontCoords = app.getPlainFont().deriveFont(Font.PLAIN, fontSize - 2);
		fontAxes = fontCoords;
			
		updateDrawableFontSize();
		updateBackground();
	}
	
	public int getFontSize(){
		return fontSize;
	}

	public void setAntialiasing(boolean flag) {
		if (flag == antiAliasing)
			return;
		antiAliasing = flag;
		repaint();
	}

	public boolean getAntialiasing() {
		return antiAliasing;
	}

	public void setDragCursor() {
		
		if (app.useTransparentCursorWhenDragging)
			setCursor(app.getTransparentCursor());
		else
			setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		
	}

	public void setMoveCursor() {
		setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
	}
	
	public void setResizeXAxisCursor() {
		app.setResizeHorizontalCursor();
	}

	public void setResizeYAxisCursor() {
		app.setResizeVerticalCursor();
	}

	public void setHitCursor() {
		if (defaultCursor == null)
			setCursor(Cursor.getDefaultCursor());
		else
			setCursor(defaultCursor);
	}

	public void setDefaultCursor() {
		if (defaultCursor == null)
			setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
		else
			setCursor(defaultCursor);
	}

	protected void initCursor() {
		defaultCursor = null;

		switch (mode) {	
		case EuclidianConstants.MODE_ZOOM_IN:
			defaultCursor = getCursorForImage(app
					.getInternalImage("cursor_zoomin.gif"));
			break;

		case EuclidianConstants.MODE_ZOOM_OUT:
			defaultCursor = getCursorForImage(app
					.getInternalImage("cursor_zoomout.gif"));
			break;	
		}

		setDefaultCursor();
	}

	protected Cursor getCursorForImage(Image image) {
		if (image == null)
			return null;

		// Query for custom cursor support
		Toolkit tk = Toolkit.getDefaultToolkit();
		Dimension d = tk.getBestCursorSize(32, 32);
		int colors = tk.getMaximumCursorColors();
		if (!d.equals(new Dimension(0, 0)) && (colors != 0)) {
			// load cursor image
			if (image != null) {
				try {
					// Create custom cursor from the image
					Cursor cursor = tk.createCustomCursor(image, new Point(16,
							16), "custom cursor");
					return cursor;
				} catch (Exception exc) {
					// Catch exceptions so that we don't try to set a null
					// cursor
					Application.debug("Unable to create custom cursor.");
				}
			}
		}
		return null;
	}

	public void setMode(int mode) {
		if (mode == this.mode) return; 
		this.mode = mode;
		initCursor();
		euclidianController.clearJustCreatedGeos();
		euclidianController.setMode(mode);
		if (clearRectangle(mode)) setSelectionRectangle(null);
		if(hasStyleBar())
			getStyleBar().setMode(mode);
	}
	
	/*
	 * whether to clear selection rectangle when mode selected
	 */
	final private boolean clearRectangle(int mode) {
		switch (mode)
		{
		case EuclidianConstants.MODE_PEN: return false;
		case EuclidianConstants.MODE_MIRROR_AT_LINE: return false;
		case EuclidianConstants.MODE_MIRROR_AT_POINT: return false;
		case EuclidianConstants.MODE_ROTATE_BY_ANGLE: return false;
		case EuclidianConstants.MODE_TRANSLATE_BY_VECTOR: return false;
		case EuclidianConstants.MODE_DILATE_FROM_POINT: return false;
		default: return true;
		}
	}

	final public int getMode() {
		return mode;
	}

	/**
	 * clears all selections and highlighting
	 */
	public void resetMode() {
		setMode(mode);
	}

	public void setPreview(Previewable p) {
		if (previewDrawable != null)
			previewDrawable.disposePreview();
		previewDrawable = p;
	}

	/**
	 * convert real world coordinate x to screen coordinate x
	 * 
	 * @param xRW
	 * @return screen equivalent of real world x-coord
	 */
	final public int toScreenCoordX(double xRW) {
		return (int) Math.round(xZero + xRW * xscale);
	}

	/**
	 * convert real world coordinate y to screen coordinate y
	 * 
	 * @param yRW
	 * @return screen equivalent of real world y-coord
	 */
	final public int toScreenCoordY(double yRW) {
		return (int) Math.round(yZero - yRW * yscale);
	}

	/**
	 * convert real world coordinate x to screen coordinate x
	 * 
	 * @param xRW
	 * @return screen equivalent of real world x-coord as double
	 */
	final public double toScreenCoordXd(double xRW) {
		return xZero + xRW * xscale;
	}

	/**
	 * convert real world coordinate y to screen coordinate y
	 * 
	 * @param yRW
	 * @return screen equivalent of real world y-coord
	 */
	final public double toScreenCoordYd(double yRW) {
		return yZero - yRW * yscale;
	}

	/**
	 * convert real world coordinate x to screen coordinate x. If the value is
	 * outside the screen it is clipped to one pixel outside.
	 * 
	 * @param xRW
	 * @return real world coordinate x to screen coordinate x clipped to screen
	 */
	final public int toClippedScreenCoordX(double xRW) {
		if (xRW > xmax)
			return width + 1;
		else if (xRW < xmin)
			return -1;
		else
			return toScreenCoordX(xRW);
	}

	/**
	 * convert real world coordinate y to screen coordinate y. If the value is
	 * outside the screen it is clipped to one pixel outside.
	 * 
	 * @param yRW
	 * @return real world coordinate y to screen coordinate x clipped to screen
	 */
	final public int toClippedScreenCoordY(double yRW) {
		if (yRW > ymax)
			return -1;
		else if (yRW < ymin)
			return height + 1;
		else
			return toScreenCoordY(yRW);
	}

	/**
	 * Converts real world coordinates to screen coordinates.
	 * Note that MAX_SCREEN_COORD is used to avoid huge coordinates.
	 * 
	 * @param inOut input and output array with x and y coords
	 * @return if resulting coords are on screen
	 */
	final public boolean toScreenCoords(double[] inOut) {
		// convert to screen coords
		inOut[0] = xZero + inOut[0] * xscale;
		inOut[1] = yZero - inOut[1] * yscale;
		
		// check if (x, y) is on screen
		boolean onScreen = true;
		
		// note that java drawing has problems for huge coord values
		// so we use FAR_OFF_SCREEN for clipping
		if (Double.isNaN(inOut[0]) || Double.isInfinite(inOut[0])) {
			inOut[0] = Double.NaN;
			onScreen = false;
		}
		else if (inOut[0] < 0 ) { // x left of screen
			//inOut[0] = Math.max(inOut[0], -MAX_SCREEN_COORD);
			onScreen = false;
		}
		else if (inOut[0] > width) { // x right of screen
			//inOut[0] = Math.min(inOut[0], width + MAX_SCREEN_COORD);
			onScreen = false;
		}
		
		// y undefined
		if (Double.isNaN(inOut[1]) || Double.isInfinite(inOut[1])) {
			inOut[1] = Double.NaN;
			onScreen = false;
		}
		else if (inOut[1] < 0) { // y above screen
			//inOut[1] = Math.max(inOut[1], -MAX_SCREEN_COORD);
			onScreen = false;
		}
		else if (inOut[1] > height) { // y below screen
			//inOut[1] = Math.min(inOut[1], height + MAX_SCREEN_COORD);
			onScreen = false;
		}
			
		return onScreen;
	}
	
	/**
	 * Checks if (screen) coords are on screen.
	 * @param coords
	 * @return true if coords are on screen
	 */
	final public boolean isOnScreen(double [] coords) {
		return coords[0] >= 0 && coords[0] <= width && coords[1] >=0 && coords[1] <= height;
	}

	//private static final double MAX_SCREEN_COORD = Float.MAX_VALUE; //10000;

//	/**
//	 * Converts real world coordinates to screen coordinates. If a coord value
//	 * is outside the screen it is clipped to a rectangle with border
//	 * PIXEL_OFFSET around the screen.
//	 * 
//	 * @param inOut:
//	 *            input and output array with x and y coords
//	 * @return true iff resulting coords are on screen, note: Double.NaN is NOT
//	 *         checked
//	 */
//	final public boolean toClippedScreenCoords(double[] inOut, int PIXEL_OFFSET) {
//		inOut[0] = xZero + inOut[0] * xscale;
//		inOut[1] = yZero - inOut[1] * yscale;
//
//		boolean onScreen = true;
//
//		// x-coord on screen?
//		if (inOut[0] < 0) {
//			inOut[0] = Math.max(inOut[0], -PIXEL_OFFSET);
//			onScreen = false;
//		} else if (inOut[0] > width) {
//			inOut[0] = Math.min(inOut[0], width + PIXEL_OFFSET);
//			onScreen = false;
//		}
//
//		// y-coord on screen?
//		if (inOut[1] < 0) {
//			inOut[1] = Math.max(inOut[1], -PIXEL_OFFSET);
//			onScreen = false;
//		} else if (inOut[1] > height) {
//			inOut[1] = Math.min(inOut[1], height + PIXEL_OFFSET);
//			onScreen = false;
//		}
//
//		return onScreen;
//	}

	/**
	 * convert screen coordinate x to real world coordinate x
	 * 
	 * @param x
	 * @return real world equivalent of screen x-coord
	 */
	final public double toRealWorldCoordX(double x) {
		return (x - xZero) * invXscale;
	}

	/**
	 * convert screen coordinate y to real world coordinate y
	 * 
	 * @param y
	 * @return real world equivalent of screen y-coord
	 */
	final public double toRealWorldCoordY(double y) {
		return (yZero - y) * invYscale;
	}

	/**
	 * Sets real world coord system, where zero point has screen coords (xZero,
	 * yZero) and one unit is xscale pixels wide on the x-Axis and yscale pixels
	 * heigh on the y-Axis.
	 */
	final public void setCoordSystem(double xZero, double yZero, double xscale,
			double yscale) {
		setCoordSystem(xZero, yZero, xscale, yscale, true);
	}
	
	/** Sets coord system from mouse move */
	final public void setCoordSystemFromMouseMove(int dx, int dy, int mode) {		
		setCoordSystem(xZeroOld + dx, yZeroOld + dy, getXscale(), getYscale());		
	}
	

	/**
	 * Sets real world coord system using min and max values for both axes in
	 * real world values.
	 */
	final public void setRealWorldCoordSystem(double xmin, double xmax,
			double ymin, double ymax) {
		double calcXscale = width / (xmax - xmin);
		double calcYscale = height / (ymax - ymin);
		double calcXzero = -calcXscale * xmin;
		double calcYzero = calcYscale * ymax;

		setCoordSystem(calcXzero, calcYzero, calcXscale, calcYscale);
	}
	
	
		
	

	/**
	 * Sets real world coord system using min and max values for both axes in
	 * real world values.
	 */
	final public void setAnimatedRealWorldCoordSystem(double xmin, double xmax,
			double ymin, double ymax, int steps, boolean storeUndo) {
		if (zoomerRW == null)
			zoomerRW = new MyZoomerRW();
		zoomerRW.init(xmin, xmax, ymin, ymax, steps, storeUndo);
		zoomerRW.startAnimation();
	}
	
	protected MyZoomerRW zoomerRW;
	
	int widthTemp, heightTemp;
	double xminTemp, xmaxTemp, yminTemp, ymaxTemp;

	final public void setTemporaryCoordSystemForExport() {
		widthTemp = width;
		heightTemp = height;
		xminTemp = xmin;
		xmaxTemp = xmax;
		yminTemp = ymin;
		ymaxTemp = ymax;
		
		try {
			GeoPoint export1=(GeoPoint)app.getKernel().lookupLabel(EuclidianView.EXPORT1);	       
			GeoPoint export2=(GeoPoint)app.getKernel().lookupLabel(EuclidianView.EXPORT2);
			
			if (export1 == null || export2 == null) return;
			
			double [] xy1 = new double[2];
			double [] xy2 = new double[2];
			export1.getInhomCoords(xy1);
			export2.getInhomCoords(xy2);

			setRealWorldCoordSystem(Math.min(xy1[0], xy2[0]), Math.max(xy1[0], xy2[0]), Math.min(xy1[1], xy2[1]), Math.max(xy1[1], xy2[1]));

		}
		catch (Exception e) {
			restoreOldCoordSystem();
		}
	}
	
	final public void restoreOldCoordSystem() {
		width = widthTemp;
		height = heightTemp;
		setRealWorldCoordSystem(xminTemp, xmaxTemp, yminTemp, ymaxTemp);
	}

	public void setCoordSystem(double xZero, double yZero, double xscale,
			double yscale, boolean repaint) {
		if (Double.isNaN(xscale) || xscale < Kernel.MAX_DOUBLE_PRECISION || xscale > Kernel.INV_MAX_DOUBLE_PRECISION)
			return;
		if (Double.isNaN(yscale) || yscale < Kernel.MAX_DOUBLE_PRECISION || yscale > Kernel.INV_MAX_DOUBLE_PRECISION)
			return;

		this.xZero = xZero;
		this.yZero = yZero;
		this.xscale = xscale;
		this.yscale = yscale;
		scaleRatio = yscale / xscale;
		invXscale = 1.0d / xscale;
		invYscale = 1.0d / yscale;
		
		// set transform for my coord system:
		// ( xscale 0 xZero )
		// ( 0 -yscale yZero )
		// ( 0 0 1 )
		coordTransform.setTransform(xscale, 0.0d, 0.0d, -yscale, xZero, yZero);

		// real world values
		setRealWorldBounds();
		
		// if (drawMode == DRAW_MODE_BACKGROUND_IMAGE)
		if (repaint) {
			updateBackgroundImage();
			updateAllDrawables(repaint);
			
			// needed so that eg Corner[2,1] updates properly on zoom / pan
			if (app.hasEuclidianView2()) kernel.notifyRepaint();
			//app.updateStatusLabelAxesRatio();
		}
	}
	
	public int temporaryWidth = -1;
	public int temporaryHeight = -1;
	
    public int getWidth() { return (temporaryWidth > 0 ) ? temporaryWidth : super.getWidth(); }

    public int getHeight() { return (temporaryHeight > 0 ) ? temporaryHeight :  super.getHeight(); }

    /*
     * used for rescaling applets when the reset button is hit
     * use setTemporarySize(-1, -1) to disable
     */
	public void setTemporarySize(int w, int h) {
		width = w;
		height = h;
		updateSize();
	}


	public void updateSize() {
		
		
		// record the old coord system
		
		
		width = getWidth();
		height = getHeight();
		if (width <= 0 || height <= 0)
			return;
		
		// real world values
		setRealWorldBounds();		
		
		
		// ================================================
		// G.Sturr 8/27/10: test: rescale on window resize
		//
		// reset the coord system so that our view dimensions are restored 
		// using the new scaling factors. 
		
		//setRealWorldCoordSystem(xminTemp, xmaxTemp, yminTemp, ymaxTemp);
		

		GraphicsConfiguration gconf = getGraphicsConfiguration();
		try {
			createImage(gconf);			
		} catch (OutOfMemoryError e) {
			bgImage = null;
			bgGraphics = null;
			System.gc();
		}

		updateBackgroundImage();
		updateAllDrawables(true);				
	}
	
	private void createImage(GraphicsConfiguration gc) {
		if (gc != null) {
			bgImage = gc.createCompatibleImage(width, height);			
			bgGraphics = bgImage.createGraphics();			
			if (antiAliasing) {
				setAntialiasing(bgGraphics);
			}
		}
	}
	
	// move view:
	/*
	 * protected void setDrawMode(int mode) { if (mode != drawMode) { drawMode =
	 * mode; if (mode == DRAW_MODE_BACKGROUND_IMAGE) updateBackgroundImage(); } }
	 */

	final protected void setRealWorldBounds() {
		xmin = -xZero * invXscale;
		xmax = (width - xZero) * invXscale;
		ymax = yZero * invYscale;
		ymin = (yZero - height) * invYscale;		
		updateBoundObjects();
		updateBounds();	
		setAxesIntervals(xscale, 0);
		setAxesIntervals(yscale, 1);
		calcPrintingScale();

		// tell kernel
		if (evNo != EVNO_GENERAL)
			kernel.setEuclidianViewBounds(evNo, xmin, xmax, ymin, ymax, xscale, yscale);

	}

	public void updateBoundObjects() {
		if(isZoomable()){
			((GeoNumeric)xminObject).setValue(xmin);
			((GeoNumeric)xmaxObject).setValue(xmax);
			((GeoNumeric)yminObject).setValue(ymin);
			((GeoNumeric)ymaxObject).setValue(ymax);
		}
	}

	protected void calcPrintingScale() {
		double unitPerCM = PRINTER_PIXEL_PER_CM / xscale;
		int exp = (int) Math.round(Math.log(unitPerCM) / Math.log(10));
		printingScale = Math.pow(10, -exp);
	}

	// axis: 0 for x-axis, 1 for y-axis
	protected void setAxesIntervals(double scale, int axis) {
		double maxPix = 100; // only one tick is allowed per maxPix pixels
		double units = maxPix / scale;
		int exp = (int) Math.floor(Math.log(units) / Math.log(10));
		int maxFractionDigtis = Math.max(-exp, kernel.getPrintDecimals());
		
		if (automaticAxesNumberingDistances[axis]) {
			//force same unit if scales are same, see #1082
			if(axis==1 && automaticAxesNumberingDistances[0] && Kernel.isEqual(xscale,yscale))
				axesNumberingDistances[1] = axesNumberingDistances[0];
			else if (piAxisUnit[axis]) {
				axesNumberingDistances[axis] = Math.PI;
			} else {
				double pot = Math.pow(10, exp);
				double n = units / pot;

				if (n > 5) {
					axesNumberingDistances[axis] = 5 * pot;
				} else if (n > 2) {
					axesNumberingDistances[axis] = 2 * pot;
				} else {
					axesNumberingDistances[axis] = pot;
				}
			}
		}
		AxesTickInterval[axis] = axesNumberingDistances[axis] / 2.0;		

		// set axes number format
		if (axesNumberFormat[axis] instanceof DecimalFormat) {
			DecimalFormat df = (DecimalFormat) axesNumberFormat[axis];

			// display large and small numbers in scienctific notation
			if (axesNumberingDistances[axis] < 10E-6 || axesNumberingDistances[axis] > 10E6) {
				df.applyPattern("0.##E0");	
				// avoid  4.00000000000004E-11 due to rounding error when computing
				// tick mark numbers
				maxFractionDigtis = Math.min(14, maxFractionDigtis);
			} else {
				df.applyPattern("###0.##");					
			}
		}		
		axesNumberFormat[axis].setMaximumFractionDigits(maxFractionDigtis);


		if (automaticGridDistance) {			
			gridDistances[axis] = axesNumberingDistances[axis] * automaticGridDistanceFactor;
		}
	}

	/**
	 * Returns xscale of this view. The scale is the number of pixels in screen
	 * space that represent one unit in user space.
	 */
	public double getXscale() {
		return xscale;
	}

	/**
	 * Returns the yscale of this view. The scale is the number of pixels in
	 * screen space that represent one unit in user space.
	 */
	public double getYscale() {
		return yscale;
	}

	/**
	 * Returns the ratio yscale / xscale of this view. The scale is the number
	 * of pixels in screen space that represent one unit in user space.
	 * @return yscale / xscale ratio
	 */
	public double getScaleRatio() {
		return yscale / xscale;
	}

	protected String getXYscaleRatioString() {
		StringBuilder sb = new StringBuilder();
		sb.append("x : y = ");
		if (xscale >= yscale) {
			sb.append("1 : ");
			sb.append(printScaleNF.format(xscale / yscale));
		} else {
			sb.append(printScaleNF.format(yscale / xscale));
			sb.append(" : 1");
		}
		sb.append(' ');
		return sb.toString();
	}

	/**
	 * Returns x coordinate of axes origin.
	 */
	public double getXZero() {
		return xZero;
	}

	/**
	 * Returns y coordinate of axes origin.
	 */
	public double getYZero() {
		return yZero;
	}
	
	/** remembers the origins values (xzero, ...) */
	public void rememberOrigins(){
		xZeroOld = xZero;
		yZeroOld = yZero;
	}

	
	/**
	 * change showing flag of the axis
	 * @param axis id of the axis
	 * @param flag show/hide
	 * @param update update (or not) the background image
	 */
	public void setShowAxis(int axis, boolean flag, boolean update){
		if (flag == showAxes[axis])
			return;
		
		showAxes[axis] = flag;
		
		if (update)
			updateBackgroundImage();
			
	}
	
	

	public void setShowAxes(boolean flag, boolean update){
		setShowAxis(AXIS_X, flag, false);
		setShowAxis(AXIS_Y, flag, true);
	}
	
	

	/** sets the visibility of x and y axis
	 * @param xAxis 
	 * @param yAxis
	 * @deprecated use {@link EuclidianViewInterface#setShowAxes(boolean, boolean)} 
	 * or {@link EuclidianViewInterface#setShowAxis(int, boolean, boolean)} instead
	 */
	public void showAxes(boolean xAxis, boolean yAxis) {
		
		/*
		if (xAxis == showAxes[0] && yAxis == showAxes[1])
			return;

		showAxes[0] = xAxis;
		showAxes[1] = yAxis;
		updateBackgroundImage();
		*/
		
		setShowAxis(AXIS_X, xAxis, false);
		setShowAxis(AXIS_Y, yAxis, true);
		
	}
	
	public final boolean isGridOrAxesShown() {
		return showAxes[0] || showAxes[1] || showGrid;
	}	
	
	/**
	 * says if the axis is shown or not
	 * @param axis id of the axis
	 * @return if the axis is shown
	 */
	public boolean getShowAxis(int axis){
		return showAxes[axis];
	}

	public boolean getShowXaxis() {
		//return showAxes[0];
		return getShowAxis(AXIS_X);
	}

	public boolean getShowYaxis() {
		return getShowAxis(AXIS_Y);
	}

	public void showGrid(boolean show) {
		if (show == showGrid)
			return;
		showGrid = show;
		updateBackgroundImage();
	}

	public boolean getShowGrid() {
		return showGrid;
	}

	final public void paint(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		//lastGraphics2D = g2;

		g2.setRenderingHints(defRenderingHints);
		// g2.setClip(0, 0, width, height);

		// BACKGROUND
		// draw background image (with axes and/or grid)
		if (bgImage == null) {
			if (firstPaint) {
				updateSize();
				g2.drawImage(bgImage, 0, 0, null);
				firstPaint = false;				
			} else {
				drawBackgroundWithImages(g2);
			}
		} else {
			// draw background image
			g2.drawImage(bgImage, 0, 0, null);
		}

		/*
		 * switch (drawMode) { case DRAW_MODE_BACKGROUND_IMAGE: // draw
		 * background image (with axes and/or grid) if (bgImage == null)
		 * updateSize(); else g2.drawImage(bgImage, 0,0, null); break;
		 * 
		 * default: // DRAW_MODE_DIRECT_DRAW: drawBackground(g2, true); }
		 */

		// FOREGROUND
		if (antiAliasing)
			setAntialiasing(g2);		
		
		// draw equations, checkboxes and all geo objects
		drawObjects(g2);

		if (selectionRectangle != null) {
			drawZoomRectangle(g2);
		}

		// when mouse over slider, show preview value of slider for that point
		boolean drawn = drawSliderValue(g2);
		
		if (!drawn) {
			if (allowShowMouseCoords && showMouseCoords && (showAxes[0] || showAxes[1] || showGrid))
				drawMouseCoords(g2);
			if (showAxesRatio)
				drawAxesRatio(g2);
		}
		

		if (kernel.needToShowAnimationButton()) {
			drawAnimationButtons(g2);
		}
	}

	public static void setAntialiasing(Graphics2D g2) {
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
				RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
	}

	protected void drawZoomRectangle(Graphics2D g2) {
		g2.setColor(colZoomRectangleFill);
		g2.setStroke(boldAxesStroke);
		g2.fill(selectionRectangle);
		g2.setColor(colZoomRectangle);
		g2.draw(selectionRectangle);
	}

	public int print(Graphics g, PageFormat pageFormat, int pageIndex) {		
		if (pageIndex > 0)
			return (NO_SUCH_PAGE);
		else {
			Graphics2D g2d = (Graphics2D) g;
			AffineTransform oldTransform = g2d.getTransform();

			g2d.translate(pageFormat.getImageableX(), pageFormat
					.getImageableY());

			// construction title
			int y = 0;
			Construction cons = kernel.getConstruction();
			String title = cons.getTitle();
			if (!title.equals("")) {
				Font titleFont = app.getBoldFont().deriveFont(Font.BOLD,
						app.getBoldFont().getSize() + 2);
				g2d.setFont(titleFont);
				g2d.setColor(Color.black);
				// Font fn = g2d.getFont();
				FontMetrics fm = g2d.getFontMetrics();
				y += fm.getAscent();
				g2d.drawString(title, 0, y);
			}

			// construction author and date
			String author = cons.getAuthor();
			String date = cons.getDate();
			String line = null;
			if (!author.equals("")) {
				line = author;
			}
			if (!date.equals("")) {
				if (line == null)
					line = date;
				else
					line = line + " - " + date;
			}

			// scale string:
			// Scale in cm: 1:1 (x), 1:2 (y)
			String scaleString = null;
			if (app.isPrintScaleString()) {
				StringBuilder sb = new StringBuilder(app
						.getPlain("ScaleInCentimeter"));
				if (printingScale <= 1) {
					sb.append(": 1:");
					sb.append(printScaleNF.format(1 / printingScale));
				} else {
					sb.append(": ");
					sb.append(printScaleNF.format(printingScale));
					sb.append(":1");
				}
	
				// add yAxis scale too?
				if (scaleRatio != 1.0) {
					sb.append(" (x), ");
					double yPrintScale = printingScale * yscale / xscale;
					if (yPrintScale < 1) {
						sb.append("1:");
						sb.append(printScaleNF.format(1 / yPrintScale));
					} else {
						sb.append(printScaleNF.format(yPrintScale));
						sb.append(":1");
					}
					sb.append(" (y)");
				}
				scaleString = sb.toString();
			}

			if (scaleString != null) {
				if (line == null)
					line = scaleString;
				else
					line = line + " - " + scaleString;
			}

			if (line != null) {
				g2d.setFont(app.getPlainFont());
				g2d.setColor(Color.black);
				// Font fn = g2d.getFont();
				FontMetrics fm = g2d.getFontMetrics();
				y += fm.getHeight();
				g2d.drawString(line, 0, y);
			}
			if (y > 0) {
				g2d.translate(0, y + 20); // space between title and drawing
			}

			double scale = PRINTER_PIXEL_PER_CM / xscale * printingScale;
			exportPaint(g2d, scale); 

			// clear page margins at bottom and right
			double pagewidth = pageFormat.getWidth();
			double pageheight = pageFormat.getHeight();
			double xmargin = pageFormat.getImageableX();
			double ymargin = pageFormat.getImageableY();

			g2d.setTransform(oldTransform);
			g2d.setClip(null);
			g2d.setPaint(Color.white);

			Rectangle2D.Double rect = new Rectangle2D.Double();
			rect.setFrame(0, pageheight - ymargin, pagewidth, ymargin);
			g2d.fill(rect);
			rect.setFrame(pagewidth - xmargin, 0, xmargin, pageheight);
			g2d.fill(rect);

			System.gc();
			return (PAGE_EXISTS);
		}
	}

	
	public void exportPaint(Graphics2D g2d, double scale) {
		exportPaint(g2d, scale, false);
	}
	/**
	 * Scales construction and draws it to g2d.
	 * @param g2d 
	 * @param scale 
	 * 
	 * @param transparency
	 *            states if export should be optimized for eps. Note: if this is
	 *            set to false, no traces are drawn.
	 * 
	 */
	public void exportPaint(Graphics2D g2d, double scale, boolean transparency) {
		app.exporting = true;
		exportPaintPre(g2d,scale, transparency);
		drawObjects(g2d);
		app.exporting = false;
	}
	
	public void exportPaintPre(Graphics2D g2d, double scale) {
		exportPaintPre(g2d, scale, false);
	}
	public void exportPaintPre(Graphics2D g2d, double scale, boolean transparency) {
		g2d.scale(scale, scale);	
		
		// clipping on selection rectangle
		if (selectionRectangle != null) {
			Rectangle rect = selectionRectangle;
			g2d.setClip(0,0, rect.width, rect.height);
			g2d.translate(-rect.x, -rect.y);					
			//Application.debug(rect.x+" "+rect.y+" "+rect.width+" "+rect.height);
		} else {
			// use points Export_1 and Export_2 to define corner
			try {
				//Construction cons = kernel.getConstruction();
				GeoPoint export1=(GeoPoint)kernel.lookupLabel(EXPORT1);	       
				GeoPoint export2=(GeoPoint)kernel.lookupLabel(EXPORT2);
				double [] xy1 = new double[2];
				double [] xy2 = new double[2];
				export1.getInhomCoords(xy1);
				export2.getInhomCoords(xy2);
				double x1=xy1[0];
				double x2=xy2[0];
				double y1=xy1[1];
				double y2=xy2[1];
				x1 = x1 / invXscale + xZero;
				y1 = yZero - y1 / invYscale;
				x2 = x2 / invXscale + xZero;
				y2 = yZero - y2 / invYscale;
				int x=(int)Math.min(x1,x2);
				int y=(int)Math.min(y1,y2);
				int exportWidth=(int)Math.abs(x1-x2) + 2;
				int exportHeight=(int)Math.abs(y1-y2) + 2;
				
				g2d.setClip(0,0,exportWidth,exportHeight);
				g2d.translate(-x, -y);	
			}
			catch (Exception e) {			
			// or take full euclidian view
			g2d.setClip(0, 0, width, height);	
			}
		}											

		// DRAWING
		if (isTracing() || hasBackgroundImages()) {
			// draw background image to get the traces
			if (bgImage == null)
				drawBackgroundWithImages(g2d, transparency);
			else
				g2d.drawImage(bgImage, 0, 0, this);
		} else {
			// just clear the background if transparency is disabled (clear = draw background color)
            drawBackground(g2d, !transparency);
		}

		g2d.setRenderingHint(RenderingHints.KEY_RENDERING,
				RenderingHints.VALUE_RENDER_QUALITY);

		setAntialiasing(g2d);
	}		


	/**
	 * Tells if there are any traces in the background image.
	 * @return true if there are any traces in background
	 */
	protected boolean isTracing() {
		DrawableIterator it = allDrawableList.getIterator();
		while (it.hasNext()) {
			if (it.next().isTracing)
				return true;
		}
		return false;
	}

	/**
	 * Tells if there are any images in the background.
	 * @return whether there are any images in the background.
	 */
	protected boolean hasBackgroundImages() {
		return bgImageList.size() > 0;
	}

	/**
	 * Returns image of drawing pad sized according to the given scale factor.
	 * @param scale 
	 * @return image of drawing pad sized according to the given scale factor.
	 * @throws OutOfMemoryError 
	 */
	public BufferedImage getExportImage(double scale) throws OutOfMemoryError {
		return getExportImage(scale, false);
	}
	
	public BufferedImage getExportImage(double scale, boolean transparency) throws OutOfMemoryError {
		int width = (int) Math.floor(getExportWidth() * scale);
		int height = (int) Math.floor(getExportHeight() * scale);		
		BufferedImage img = createBufferedImage(width, height, transparency);
		exportPaint(img.createGraphics(), scale, transparency); 
		img.flush();
		return img;
	}
	
	protected BufferedImage createBufferedImage(int width, int height) {
		return createBufferedImage(width, height, false);
	}

	protected BufferedImage createBufferedImage(int width, int height, boolean transparency)
			throws OutOfMemoryError {
		
		
		GraphicsEnvironment ge =
			GraphicsEnvironment.getLocalGraphicsEnvironment();

			GraphicsDevice gs = ge.getDefaultScreenDevice();

			GraphicsConfiguration gc =
			gs.getDefaultConfiguration();
			BufferedImage bufImg = gc.createCompatibleImage(width,
				height, (transparency ? Transparency.TRANSLUCENT : Transparency.BITMASK));



				//Graphics2D g = (Graphics2D)bufImg.getGraphics();

				//g.setBackground(new Color(0,0,0,0));

				//g.clearRect(0,0,width,height);
				
				return bufImg;

	}

	final public Graphics2D getBackgroundGraphics() {
		return bgGraphics;
	}

	final public void updateBackground() {
		// make sure axis number formats are up to date
		setAxesIntervals(xscale, 0);
		setAxesIntervals(yscale, 1);
		
		updateBackgroundImage();
		updateAllDrawables(true);
		// repaint();
	}

	final protected void updateBackgroundImage() {
		if (bgGraphics != null) {
			drawBackgroundWithImages(bgGraphics);
		}
	}
	
	private void drawBackgroundWithImages(Graphics2D g) {
		drawBackgroundWithImages(g, false);
	}
	
	private void drawBackgroundWithImages(Graphics2D g, boolean transparency) {
		if(!transparency)
			clearBackground(g);
		
		bgImageList.drawAll(g); 
		drawBackground(g, false);
	}

	final protected void drawBackground(Graphics2D g, boolean clear) {
		if (clear) {
			clearBackground(g);
		}

		setAntialiasing(g);
		
		// handle drawing axes near the screen edge
 		if(drawBorderAxes[0] || drawBorderAxes[1]){
 			
 			// edge axes are not drawn at the exact edge, instead they
 			// are inset enough to draw the labels
 			// labelOffset = amount of space needed to draw labels 
			Point labelOffset = getMaximumLabelSize(g);
			
			// force the the axisCross position to be near the edge
			if(drawBorderAxes[0])
				axisCross[0] = ymin +  (labelOffset.y + 10)/yscale;
			if(drawBorderAxes[1])
				axisCross[1] = xmin + (labelOffset.x + 10)/xscale;
		}
		
		if (showGrid)
			drawGrid(g);
		if (showAxes[0] || showAxes[1])
			drawAxes(g);

		if (app.showResetIcon() && app.isApplet()) {
			// need to use getApplet().width rather than width so that
			// it works with applet rescaling
			int w = app.onlyGraphicsViewShowing() ? app.getApplet().width : width + 2;
			g.drawImage(getResetImage(), w - 18, 2, null);
		}
	}		
	
	private Image getResetImage() {
		if (resetImage == null) {
			resetImage = app.getRefreshViewImage();
		}
		return resetImage;
	}
	
	private Image getPlayImage() {
		if (playImage == null) {
			playImage = app.getPlayImage();
		}
		return playImage;
	}
	
	private Image getPauseImage() {
		if (pauseImage == null) {
			pauseImage = app.getPauseImage();
		}
		return pauseImage;
	}

	final protected void clearBackground(Graphics2D g) {
		g.setColor(bgColor);
		g.fillRect(0, 0, width, height);
	}

	protected static int SCREEN_BORDER = 10;

	
	
	
	//=================================================
	//         Draw Axes
	//=================================================
	
	// G.Sturr: 2010-8-9  
	// Modified drawAxes() to allow variable 
	// crossing points and positive-only axes
	
	// axis control vars 
	private double[] axisCross = {0,0};
	private boolean[] positiveAxes = {false, false};
	private boolean[] drawBorderAxes = {false,false};
	
	
	
	// getters and Setters for axis control vars

	public double[] getAxesCross() {
		return axisCross;
	}

	public void setAxesCross(double[] axisCross) {
		this.axisCross = axisCross;
	}
	
	// for xml handler
	public void setAxisCross(int axis, double cross) {
		axisCross[axis] = cross;
	}
	
	
	public boolean[] getPositiveAxes() {
		return positiveAxes;
	}

	public void setPositiveAxes(boolean[] positiveAxis) {
		this.positiveAxes = positiveAxis;
	}

	// for xml handler
	public void setPositiveAxis(int axis, boolean isPositiveAxis) {
		positiveAxes[axis] = isPositiveAxis;
	}
	
	
	public boolean[] getDrawBorderAxes() {
		return drawBorderAxes;
	}

	public void setDrawBorderAxes(boolean[] drawBorderAxes) {
		this.drawBorderAxes = drawBorderAxes;
		// don't show corner coordinates if one of the axes is sticky
		this.setAxesCornerCoordsVisible(!(drawBorderAxes[0] || drawBorderAxes[1]));
	}
	
	// for xml handler
	public void setDrawBorderAxis(int axis, boolean drawBorderAxis) {
		drawBorderAxes[axis] = drawBorderAxis;
	}
	
	private double getLabelLength(double rw, FontRenderContext frc){
		TextLayout layout = new TextLayout(kernel.formatPiE(rw,
				axesNumberFormat[0])+(axesUnitLabels[0] != null && !piAxisUnit[0]?axesUnitLabels[0]:""),
				fontAxes, frc);
		return layout.getAdvance();
	}

	/*#********************************************
	 *  drawAxes
	 **********************************************/
	final void drawAxes(Graphics2D g2) {
		
		// xCrossPix: yAxis crosses the xAxis at this x pixel
		double xCrossPix =  this.xZero + axisCross[1] * xscale;
		
		// yCrossPix: xAxis crosses the YAxis at his y pixel
		double yCrossPix =  this.yZero - axisCross[0] * yscale;
		
		
		// yAxis end value (for drawing half-axis)
		int yAxisEnd = positiveAxes[1] ? (int) yCrossPix : height;		
		
		
		// xAxis start value (for drawing half-axis)
		int xAxisStart = positiveAxes[0] ? (int) xCrossPix : 0;		
				
		
		// for axes ticks
		double yZeroTick = yCrossPix;
		double xZeroTick = xCrossPix;
		double yBig = yCrossPix + 4;
		double xBig = xCrossPix - 4;
		double ySmall1 = yCrossPix + 0;
		double ySmall2 = yCrossPix + 2;
		double xSmall1 = xCrossPix - 0;
		double xSmall2 = xCrossPix - 2;
		int xoffset, yoffset;
		
		
		boolean bold = axesLineType == AXES_LINE_TYPE_FULL_BOLD
						|| axesLineType == AXES_LINE_TYPE_ARROW_BOLD;
		boolean drawArrowsx = (axesLineType == AXES_LINE_TYPE_ARROW
								|| axesLineType == AXES_LINE_TYPE_ARROW_BOLD)
								&& !(positiveAxes[0] && xmax < axisCross[1]);
		boolean drawArrowsy = (axesLineType == AXES_LINE_TYPE_ARROW
				|| axesLineType == AXES_LINE_TYPE_ARROW_BOLD)
				&& !(positiveAxes[1] && ymax < axisCross[0]);
		// AXES_TICK_STYLE_MAJOR_MINOR = 0;
		// AXES_TICK_STYLE_MAJOR = 1;
		// AXES_TICK_STYLE_NONE = 2;
		
		boolean[] drawMajorTicks = { axesTickStyles[0] <= 1,
				axesTickStyles[1] <= 1 };
		boolean[] drawMinorTicks = { axesTickStyles[0] == 0,
				axesTickStyles[1] == 0 };

		FontRenderContext frc = g2.getFontRenderContext();
		g2.setFont(fontAxes);
		int fontsize = fontAxes.getSize();
		int arrowSize = fontsize / 3;
		g2.setPaint(axesColor);

		if (bold) {
			axesStroke = boldAxesStroke;
			tickStroke = boldAxesStroke;
			ySmall2++;
			xSmall2--;
			arrowSize += 1;
		} else {
			axesStroke = defAxesStroke;
			tickStroke = defAxesStroke;
		}

		// turn antialiasing off
//		Object antiAliasValue = g2
//				.getRenderingHint(RenderingHints.KEY_ANTIALIASING);	
//		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
//				RenderingHints.VALUE_ANTIALIAS_OFF);

		// make sure arrows don't go off screen (eg EMF export)
		double arrowAdjustx = drawArrowsx ? axesStroke.getLineWidth() : 0;
		double arrowAdjusty = drawArrowsy ? axesStroke.getLineWidth() : 0;
	
		
		// ========================================
		// X-AXIS
		
		if (showAxes[0] && ymin < axisCross[0] && ymax > axisCross[0]) {
			if (showGrid) {
				yoffset = fontsize + 4;
				xoffset = 10;
			} else {
				yoffset = fontsize + 4;
				xoffset = 1;
			}

			// label of x axis
			if (axesLabels[0] != null) {
				TextLayout layout = new TextLayout(axesLabels[0], fontLine, frc);
				g2.drawString(axesLabels[0], (int) (width - 10 - layout
						.getAdvance()), (int) (yCrossPix - 4));
			}

			// numbers
			
			double rw = xmin - (xmin % axesNumberingDistances[0]);
			int labelno = (int) Math.round(rw/axesNumberingDistances[0]);
			//by default we start with minor tick to the left of first major tick, exception is for positive only
			double smallTickOffset = 0;
			double axesStep = xscale * axesNumberingDistances[0]; // pixelstep
			if(getPositiveAxes()[0] && rw > xmin){  
				// start labels at the y-axis instead of screen border
				// be careful: axisCross[1] = x value for which the y-axis crosses, 
				// so xmin is replaced axisCross[1] and not axisCross[0]
				rw = MyMath.nextMultiple(axisCross[1], axesNumberingDistances[0]);
				smallTickOffset = axesStep;		
				labelno = 0;
			}
			
			double pix = xZero + rw * xscale;    
			
			double smallTickPix;
			double tickStep = axesStep / 2;
			double labelLengthMax = Math.max(getLabelLength(rw,frc),getLabelLength(
					MyMath.nextMultiple(xmax,axesNumberingDistances[0]),frc));
			int unitsPerLabelX = (int) MyMath.nextPrettyNumber(labelLengthMax/axesStep);			
			
			if (pix < SCREEN_BORDER) {
				// big tick
				if (drawMajorTicks[0]) {
					g2.setStroke(tickStroke);					
					tempLine.setLine(pix, yZeroTick, pix, yBig);
					g2.draw(tempLine);
				}
				pix += axesStep;
				rw += axesNumberingDistances[0];
				labelno += 1;
			}
			int maxX = width - SCREEN_BORDER;
			
			
			for (; pix < width; rw += axesNumberingDistances[0], pix += axesStep) {
				if (pix <= maxX) {
					if (showAxesNumbers[0]) {
						String strNum = kernel.formatPiE(rw,
								axesNumberFormat[0]);
						
						// flag to handle drawing a label at axis crossing point
						boolean zero = strNum.equals("" + kernel.formatPiE(axisCross[1],
								axesNumberFormat[0]));
						if (labelno % unitsPerLabelX == 0) {
						sb.setLength(0);
						sb.append(strNum);
						if (axesUnitLabels[0] != null && !piAxisUnit[0])
							sb.append(axesUnitLabels[0]);

						TextLayout layout = new TextLayout(sb.toString(),
								fontAxes, frc);
						int x, y = (int) (yCrossPix + yoffset);
						
						// if label intersects the y-axis then draw it 6 pixels to the left
						if (zero && showAxes[1] && !positiveAxes[1]) {
							x = (int) (pix + 6);
						} else {
							x = (int) (pix + xoffset - layout.getAdvance() / 2);
						}
												
						// make sure we don't print one string on top of the other
						
							//prevTextEnd = (int) (x + layout.getAdvance()); 
							g2.drawString(sb.toString(), x, y);
						}
					}

					// big tick
					if (drawMajorTicks[0]) {
						g2.setStroke(tickStroke);
						tempLine.setLine(pix, yZeroTick, pix, yBig);
						g2.draw(tempLine);
					}
				} else if (drawMajorTicks[0] && !drawArrowsx) {
					// draw last tick if there is no arrow
					tempLine.setLine(pix, yZeroTick, pix, yBig);
					g2.draw(tempLine);
				}

				// small tick				
				smallTickPix = pix - tickStep + smallTickOffset;
				if (drawMinorTicks[0]) {
					g2.setStroke(tickStroke);
					tempLine.setLine(smallTickPix, ySmall1, smallTickPix,
							ySmall2);
					g2.draw(tempLine);
				}
				labelno++;
			}
			// last small tick
			smallTickPix = pix - tickStep + smallTickOffset;
			if (drawMinorTicks[0]) {
				g2.setStroke(tickStroke);
				tempLine.setLine(smallTickPix, ySmall1, smallTickPix, ySmall2);
				g2.draw(tempLine);
			}

			// x-Axis
			g2.setStroke(axesStroke);
			
			//tempLine.setLine(0, yCrossPix, width, yCrossPix);
			tempLine.setLine(xAxisStart, yCrossPix, width - arrowAdjustx - 1 , yCrossPix);
			
			g2.draw(tempLine);

			if (drawArrowsx) {

				// draw arrow for x-axis
				tempLine.setLine(width - arrowAdjustx, yCrossPix + 0.5, width - arrowAdjustx - arrowSize, yCrossPix
						- arrowSize);
				g2.draw(tempLine);
				tempLine.setLine(width - arrowAdjustx, yCrossPix - 0.5, width - arrowAdjustx - arrowSize, yCrossPix
						+ arrowSize);
				g2.draw(tempLine);

				//g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				//		RenderingHints.VALUE_ANTIALIAS_OFF);
			}
		}

		
		
		// ========================================
		// Y-AXIS
		
		if (showAxes[1] && xmin < axisCross[1] && xmax > axisCross[1]) {
			
			if (showGrid) {
				xoffset = -2 - fontsize / 4;
				yoffset = -2;
			} else {
				xoffset = -4 - fontsize / 4;
				yoffset = fontsize / 2 - 1;
			}

			// label of y axis
			if (axesLabels[1] != null) {
				TextLayout layout = new TextLayout(axesLabels[1], fontLine, frc);
				g2.drawString(axesLabels[1], (int) (xCrossPix + 5),
						(int) (5 + layout.getAscent()));
			}

			// numbers
			double rw = ymin - (ymin % axesNumberingDistances[1]);
			int labelno = (int) Math.round(rw/axesNumberingDistances[1]);
			//by default we start with minor tick to the left of first major tick, exception is for positive only
			double smallTickOffset = 0;
			double axesStep = yscale * axesNumberingDistances[1]; // pixelstep
			if(getPositiveAxes()[1] && rw > ymin){  
				// start labels at the y-axis instead of screen border
				// be careful: axisCross[1] = x value for which the y-axis crosses, 
				// so xmin is replaced axisCross[1] and not axisCross[0]
				rw = MyMath.nextMultiple(axisCross[0], axesNumberingDistances[1]);
				smallTickOffset = axesStep;
				labelno = 0;
			}			
			
			double pix = yZero - rw * yscale;
			
			double tickStep = axesStep / 2;
			
			double maxHeight = new TextLayout("9",
					fontAxes, frc).getBounds().getHeight()*2;
			int unitsPerLabelY = (int) MyMath.nextPrettyNumber(maxHeight/axesStep);
			
			if (pix > height - SCREEN_BORDER) {
				// big tick
				if (drawMajorTicks[1]) {
					g2.setStroke(tickStroke);
					tempLine.setLine(xBig, pix, xZeroTick, pix);					
					g2.draw(tempLine);
				}
				pix -= axesStep;
				rw += axesNumberingDistances[1];	
				labelno ++;
			}

			double smallTickPix = pix + tickStep;
			
			
			// draw all of the remaining ticks and labels
	
			//int maxY = height - SCREEN_BORDER;
			int maxY = SCREEN_BORDER;
			
			//for (; pix <= height; rw -= axesNumberingDistances[1], pix += axesStep) {
			//yAxisEnd
			
			for (; pix >= maxY; rw += axesNumberingDistances[1], pix -= axesStep, labelno ++) {
				if (pix >= maxY) {					
					if (showAxesNumbers[1]) {
						String strNum = kernel.formatPiE(rw,
								axesNumberFormat[1]);
						
						// flag for handling label at axis cross point
						boolean zero = strNum.equals("" + kernel.formatPiE(axisCross[0],
								axesNumberFormat[0]));
						if(labelno % unitsPerLabelY == 0){
						sb.setLength(0);
						sb.append(strNum);
						if (axesUnitLabels[1] != null && !piAxisUnit[1])
							sb.append(axesUnitLabels[1]);

						TextLayout layout = new TextLayout(sb.toString(),
								fontAxes, frc);
						int x = (int) (xCrossPix + xoffset - layout.getAdvance());
						int y;
						// if the label is at the axis cross point then draw it 2 pixels above
						if (zero && showAxes[0] && !positiveAxes[0]) {
							y = (int) (yCrossPix - 2);
						} else {
							y = (int) (pix + yoffset);
						}
						g2.drawString(sb.toString(), x, y);}
					}
					if(drawMajorTicks[1]){
						g2.setStroke(tickStroke);
						tempLine.setLine(xBig, pix, xZeroTick, pix);
						g2.draw(tempLine);
					}
				}
				else if (drawMajorTicks[1] && !drawArrowsy) {
					// draw last tick if there is no arrow	
					g2.setStroke(tickStroke);
					tempLine.setLine(xBig, pix, xZeroTick, pix);
					g2.draw(tempLine);
				}

				// small tick				
				smallTickPix = pix + tickStep - smallTickOffset;
				if (drawMinorTicks[1]) {
					g2.setStroke(tickStroke);
					tempLine.setLine(xSmall1, smallTickPix, xSmall2,
							smallTickPix);
					g2.draw(tempLine);
				}

				
			}//end for
			smallTickPix = pix + tickStep - smallTickOffset;
			if (drawMinorTicks[0]) {
				g2.setStroke(tickStroke);
				tempLine.setLine(smallTickPix, ySmall1, smallTickPix, ySmall2);
				g2.draw(tempLine);
			}

			// y-Axis
			
			//tempLine.setLine(xZero, 0, xZero, height);
			
			tempLine.setLine(xCrossPix, arrowAdjusty + (drawArrowsy?1:-1), xCrossPix, yAxisEnd);
				
			g2.draw(tempLine);

			if (drawArrowsy) {
				// draw arrow for y-axis
				tempLine.setLine(xCrossPix + 0.5, arrowAdjusty, xCrossPix - arrowSize, arrowAdjusty + arrowSize);
				g2.draw(tempLine);
				tempLine.setLine(xCrossPix - 0.5, arrowAdjusty, xCrossPix + arrowSize, arrowAdjusty + arrowSize);
				g2.draw(tempLine);
			}								
		}
		
	
		// if one of the axes is not visible, show upper left and lower right corner coords
		if (showAxesCornerCoords) {
			if (xmin > 0 || xmax < 0 || ymin > 0 || ymax < 0) {
				// uper left corner								
				sb.setLength(0);
				sb.append('(');			
				sb.append(kernel.formatPiE(xmin, axesNumberFormat[0]));
				sb.append(Application.unicodeComma);
				sb.append(" ");
				sb.append(kernel.formatPiE(ymax, axesNumberFormat[1]));
				sb.append(')');
				
				int textHeight = 2 + fontAxes.getSize();
				g2.setFont(fontAxes);			
				g2.drawString(sb.toString(), 5, textHeight);
				
				// lower right corner
				sb.setLength(0);
				sb.append('(');			
				sb.append(kernel.formatPiE(xmax, axesNumberFormat[0]));
				sb.append(Application.unicodeComma);
				sb.append(" ");
				sb.append(kernel.formatPiE(ymin, axesNumberFormat[1]));
				sb.append(')');
				
				TextLayout layout = new TextLayout(sb.toString(), fontAxes, frc);	
				layout.draw(g2, (int) (width - 5 - layout.getAdvance()), 
										height - 5);					
			}	
		}
	}
	
	
	/**
	 * Finds maximum pixel width and height needed to draw current x and y axis labels.
	 * return[0] = max width, return[1] = max height
	 * @param g2 
	 * @return point (width,height)
	 */
	public Point getMaximumLabelSize (Graphics2D g2) {
		
		Point max = new Point(0,0);
		
		g2.setFont(fontAxes);	
		FontRenderContext frc = g2.getFontRenderContext();
				
		int yAxisHeight = positiveAxes[1] ? (int) yZero : height;		
		int maxY = positiveAxes[1] ? (int) yZero : height - SCREEN_BORDER;


		double rw = ymax - (ymax % axesNumberingDistances[1]);
		double pix = yZero - rw * yscale;
		double axesStep = yscale * axesNumberingDistances[1]; // pixelstep
	
		
		for (; pix <= yAxisHeight; rw -= axesNumberingDistances[1], pix += axesStep) {
			if (pix <= maxY) {
				if (showAxesNumbers[1]) {
					String strNum = kernel.formatPiE(rw,
							axesNumberFormat[1]);					

					sb.setLength(0);
					sb.append(strNum);
					if (axesUnitLabels[1] != null && !piAxisUnit[1])
						sb.append(axesUnitLabels[1]);

					TextLayout layout = new TextLayout(sb.toString(),
							fontAxes, frc);
					//System.out.println(layout.getAdvance() + " : " + sb);
					if(max.x < layout.getAdvance())
						max.x = (int) layout.getAdvance();
				}
			}
		}
		FontMetrics fm = g2.getFontMetrics();
		max.y += fm.getAscent();
		
		return max;
	}



	
	public boolean isAxesCornerCoordsVisible() {
		return showAxesCornerCoords;
	}

	public void setAxesCornerCoordsVisible(boolean showAxesCornerCoords) {
		this.showAxesCornerCoords = showAxesCornerCoords;
	}

	
	
	final void drawGrid(Graphics2D g2) {
		g2.setColor(gridColor);
		g2.setStroke(gridStroke);

		// vars for handling positive-only axes
		double xCrossPix =  this.xZero + axisCross[1] * xscale;
		double yCrossPix =  this.yZero - axisCross[0] * yscale;
		int yAxisEnd = positiveAxes[1] ? (int) yCrossPix : height;		
		int xAxisStart = positiveAxes[0] ? (int) xCrossPix : 0;
		
		// set the clipping region to the region defined by the axes
		Shape oldClip = g2.getClip();
		if(gridType != GRID_POLAR) // don't do this for polar grids
			g2.setClip(xAxisStart, 0, width, yAxisEnd);
		
		
		switch (gridType) {
		
		case GRID_CARTESIAN:

			// vertical grid lines
			double tickStep = xscale * gridDistances[0];
			double start = xZero % tickStep;
			double pix = start;	

			for (int i=0; pix <= width; i++) {	
				//int val = (int) Math.round(i);
				//g2.drawLine(val, 0, val, height);
				tempLine.setLine(pix, 0, pix, height);
				g2.draw(tempLine);

				pix = start + i * tickStep;
			}

			// horizontal grid lines
			tickStep = yscale * gridDistances[1];
			start = yZero % tickStep;
			pix = start;

			for (int j=0; pix <= height; j++) {
				//int val = (int) Math.round(j);
				//g2.drawLine(0, val, width, val);
				tempLine.setLine(0, pix, width, pix);
				g2.draw(tempLine);
				
				pix = start + j * tickStep;			
			}	

		break;
		
		
		case GRID_ISOMETRIC:
					
			double tickStepX = xscale * gridDistances[0] * Math.sqrt(3.0);
			double startX = xZero % (tickStepX);
			double startX2 = xZero % (tickStepX/2);
			double tickStepY = yscale * gridDistances[0];
			double startY = yZero % tickStepY;
			
			// vertical
			pix = startX2;
			for (int j=0; pix <= width; j++) {
				tempLine.setLine(pix, 0, pix, height);
				g2.draw(tempLine);
				pix = startX2 + j * tickStepX/2.0;			
			}		

			// extra lines needed because it's diagonal
			int extra = (int)(height*xscale/yscale * Math.sqrt(3.0) / tickStepX)+3;
			
			// positive gradient
			pix = startX + -(extra+1) * tickStepX;			
			for (int j=-extra; pix <= width; j+=1) {
				tempLine.setLine(pix, startY-tickStepY, pix + (height+tickStepY) * Math.sqrt(3)*xscale/yscale, startY-tickStepY + height+tickStepY);
				g2.draw(tempLine);
				pix = startX + j * tickStepX;			
			}						
			
			// negative gradient
			pix = startX;
			for (int j=0; pix <= width + (height*xscale/yscale+tickStepY) * Math.sqrt(3.0); j+=1) 
			//for (int j=0; j<=kk; j+=1)
			{
				tempLine.setLine(pix, startY-tickStepY, pix - (height+tickStepY) * Math.sqrt(3)*xscale/yscale, startY-tickStepY + height+tickStepY);
				g2.draw(tempLine);
				pix = startX + j * tickStepX;			
			}						
			
			break;
			
			
		case GRID_POLAR:   //G.Sturr 2010-8-13
			
			// find minimum grid radius  
			double min;
			if(xZero > 0 && xZero < width &&  yZero > 0 && yZero < height){
				// origin onscreen: min = 0
				min = 0;
			}else{
				// origin offscreen: min = distance to closest screen border
				double minW = Math.min(Math.abs(xZero), Math.abs(xZero - width));
				double minH = Math.min(Math.abs(yZero), Math.abs(yZero - height));
				min = Math.min(minW, minH);
			}
					
			// find maximum grid radius
			// max =  max distance of origin to screen corners 	
			double d1 = GeoVec2D.length(xZero, yZero);  // upper left
			double d2 = GeoVec2D.length(xZero, yZero-height); // lower left
			double d3 = GeoVec2D.length(xZero-width, yZero); // upper right
			double d4 = GeoVec2D.length(xZero-width, yZero-height); // lower right		
			double max = Math.max(Math.max(d1, d2), Math.max(d3, d4));
			
			
			// draw the grid circles
			// note: x tick intervals are used for the radius intervals, 
			//       it is assumed that the x/y scaling ratio is 1:1
			double tickStepR = xscale * gridDistances[0];
			double r = min - min  % tickStepR;
			while (r <= max) {			
				circle.setFrame(xZero-r, yZero-r, 2*r, 2*r);	
				g2.draw(circle);
				r = r + tickStepR;	
			
			}
			
			// draw the radial grid lines
			double angleStep = gridDistances[2];
			double y1, y2, m;
			
			// horizontal axis
			tempLine.setLine(0, yZero, width, yZero);
			g2.draw(tempLine);
				
			// radial lines
			for(double a = angleStep ; a < Math.PI ; a = a + angleStep){
				
				if(Math.abs(a - Math.PI/2) < 0.0001){
					//vertical axis
					tempLine.setLine(xZero, 0, xZero, height);
				}else{
					m = Math.tan(a);
					y1 = m*(xZero) + yZero;	 
					y2 = m*(xZero - width) + yZero;
					tempLine.setLine(0, y1, width, y2);
				}
				g2.draw(tempLine);
			}
			
			break;		
		}
		
		// reset the clipping region
		g2.setClip(oldClip);
	}

	final protected void drawMouseCoords(Graphics2D g2) {
		Point pos = euclidianController.mouseLoc;
		if (pos == null)
			return;

		sb.setLength(0);
		sb.append('(');
		sb.append(kernel.format(kernel.checkDecimalFraction(euclidianController.xRW)));
		if (kernel.getCoordStyle() == Kernel.COORD_STYLE_AUSTRIAN)
			sb.append(" | ");
		else
			sb.append(", ");
		sb.append(kernel.format(kernel.checkDecimalFraction(euclidianController.yRW)));
		sb.append(')');

		g2.setColor(Color.darkGray);
		g2.setFont(fontCoords);
		g2.drawString(sb.toString(), pos.x + 15, pos.y + 15);
	}
	
	final protected void drawAxesRatio(Graphics2D g2) {
		Point pos = euclidianController.mouseLoc;
		if (pos == null)
			return;						

		g2.setColor(Color.darkGray);
		g2.setFont(fontLine);
		g2.drawString(getXYscaleRatioString(), pos.x + 15, pos.y + 30);
	}
	
	final protected boolean drawSliderValue(Graphics2D g2) {
		
		if (mode != EuclidianConstants.MODE_MOVE) return false;
		
		Point pos = euclidianController.mouseLoc;
		if (pos == null)
			return false;				
		
		String val = euclidianController.getSliderValue();
		
		if (val == null) return false;

		g2.setColor(Color.darkGray);
		g2.setFont(fontLine);
		g2.drawString(val, pos.x + 15, pos.y + 15);
		
		return true;
	}
	
	final protected void drawAnimationButtons(Graphics2D g2) {
		
		// draw button in focused EV only
		if (!drawPlayButtonInThisView()) return;

		int x = 6;
		int y = height - 22;
				
		if (highlightAnimationButtons) {
			// draw filled circle to highlight button
			g2.setColor(Color.darkGray);
		} else {
			g2.setColor(Color.lightGray);			
		}
		
		g2.setStroke(EuclidianView.getDefaultStroke());
		
		// draw pause or play button
		g2.drawRect(x-2, y-2, 18, 18);
		Image img = kernel.isAnimationRunning() ? getPauseImage() : getPlayImage();			
		g2.drawImage(img, x, y, null);
	}
	
	public final boolean hitAnimationButton(MouseEvent e) {
		// draw button in focused EV only
		if (!drawPlayButtonInThisView()) return false;
		
		return kernel.needToShowAnimationButton() && (e.getX() <= 20) && (e.getY() >= height - 20);		
	}
	
	private boolean drawPlayButtonInThisView() {
		
		GuiManager gui;
		// just one view
		if ((gui = app.getGuiManager()) == null) return true;
		EuclidianDockPanelAbstract evp;
		// eg ev1 just closed
		if ((evp = gui.getLayout().getDockManager().getFocusedEuclidianPanel()) == null) return true;
		
		return !(app.getGuiManager() != null && this.getViewID() != evp.getViewId());
	}
	
	/**
	 * Updates highlighting of animation buttons. 
	 * @return whether status was changed
	 */
	public final boolean setAnimationButtonsHighlighted(boolean flag) {

		// draw button in focused EV only
		if (!drawPlayButtonInThisView()) return false;

		if (flag == highlightAnimationButtons) 
			return false;
		else {
			highlightAnimationButtons = flag;
			return true;
		}
	}
	

	// Michael Borcherds 2008-02-29
	public void changeLayer(GeoElement geo, int oldlayer, int newlayer)
	{
		updateMaxLayerUsed(newlayer);
		//Application.debug(drawLayers[oldlayer].size());
		drawLayers[oldlayer].remove((Drawable) DrawableMap.get(geo));
		//Application.debug(drawLayers[oldlayer].size());
		drawLayers[newlayer].add((Drawable) DrawableMap.get(geo));
		
	}
	
	public void updateMaxLayerUsed(int layer)
	{
		if (layer > MAX_LAYERS) layer=MAX_LAYERS;
		if (layer > app.maxLayerUsed) app.maxLayerUsed=layer;
	}

	public int getMaxLayerUsed()
	{
		return app.maxLayerUsed;
	}
	
	// Michael Borcherds 2008-03-01
	public void drawObjectsPre(Graphics2D g2) {
		
		// TODO layers for Buttons and Textfields
		
		//for cross-platform UI the stroke must  be reset to show buttons properly, see #442
		g2.setStroke(getDefaultStroke());
		paintChildren(g2);  // draws Buttons and Textfields
		
	}
	
	// Michael Borcherds 2008-03-01
	protected void drawObjects(Graphics2D g2) {
		
		
		drawGeometricObjects(g2);
		//for cross-platform UI the stroke must  be reset to show buttons properly, see #442
		g2.setStroke(getDefaultStroke());		
		// TODO layers for Buttons and Textfields
		paintChildren(g2);  // draws Buttons and Textfields

		if (previewDrawable != null ) {
			previewDrawable.drawPreview(g2);
		}		
	}


	// Michael Borcherds 2008-03-01
	protected void drawGeometricObjects(Graphics2D g2) {	
		//boolean isSVGExtensions=g2.getClass().getName().endsWith("SVGExtensions");
		int layer;
		
		for (layer=0 ; layer<=app.maxLayerUsed ; layer++) // only draw layers we need
		{
			//if (isSVGExtensions) ((geogebra.export.SVGExtensions)g2).startGroup("layer "+layer);
			drawLayers[layer].drawAll(g2);
			//if (isSVGExtensions) ((geogebra.export.SVGExtensions)g2).endGroup("layer "+layer);
		}
	}
	/*
	protected void drawObjects(Graphics2D g2, int layer) {		
		// draw images
		drawImageList.drawAll(g2);
		
		// draw HotEquations
		// all in layer 0 currently
		// layer -1 means draw all
		if (layer == 0 || layer == -1) paintChildren(g2);
		
		// draw Geometric objects
		drawGeometricObjects(g2, layer);
	} */


	/**
	 * Draws all GeoElements except images.
	 *
	protected void drawGeometricObjects(Graphics2D g2, int layer) {	

		if (previewDrawable != null &&
				(layer == app.getMaxLayer() || layer == -1)) { // Michael Borcherds 2008-02-26 only draw once
			previewDrawable.drawPreview(g2);
		}		
		
		// draw lists of objects
		drawListList.drawAll(g2);

		// draw polygons
		drawPolygonList.drawAll(g2);

		// draw conics
		drawConicList.drawAll(g2);

		// draw angles and numbers
		drawNumericList.drawAll(g2);

		// draw functions
		drawFunctionList.drawAll(g2);

		// draw lines
		drawLineList.drawAll(g2);

		// draw segments
		drawSegmentList.drawAll(g2);

		// draw vectors
		drawVectorList.drawAll(g2);

		// draw locus
		drawLocusList.drawAll(g2);

		// draw points
		drawPointList.drawAll(g2);

		// draw text
		drawTextList.drawAll(g2);
		
		// boolean are not drawn as they are JToggleButtons and children of the view
	} */

	// for use in AlgebraController
	final public void mouseMovedOver(GeoElement geo) {
		Hits geos = null;
		if (geo != null) {
			tempArrayList.clear();
			tempArrayList.add(geo);
			geos = tempArrayList;
		}
		boolean repaintNeeded = euclidianController.refreshHighlighting(geos);
		if (repaintNeeded)
			kernel.notifyRepaint();
	}

	protected Hits tempArrayList = new Hits();

	// for use in AlgebraController
	final public void clickedGeo(GeoElement geo, MouseEvent e) {
		if (geo == null)
			return;

		tempArrayList.clear();
		tempArrayList.add(geo);
		boolean changedKernel = euclidianController.processMode(tempArrayList,
				e);
		if (changedKernel)
			app.storeUndoInfo();
		kernel.notifyRepaint();
	}

	
	
	
	
	
	
	
	
	// ggb3D 2009-02-05
	
	/**get the hits recorded */
	public Hits getHits(){
		return hits;
	}
	
	
	
	/**
	 * sets the hits of GeoElements whose visual representation is at screen
	 * coords (x,y). order: points, vectors, lines, conics
	 */
	final public void setHits(Point p){
		
		hits.init();
				
		DrawableIterator it = allDrawableList.getIterator();
		while (it.hasNext()) {
			Drawable d = it.next();
			if (d.hit(p.x, p.y) || d.hitLabel(p.x, p.y)) {
				GeoElement geo = d.getGeoElement();
				if (geo.isEuclidianVisible()) {
					hits.add(geo);
				}
			}
		}
		
		// look for axis
		if (hits.getImageCount() == 0) {
			if (showAxes[0] && Math.abs(yZero - p.y) < 3) {
				hits.add(kernel.getXAxis());
			}
			if (showAxes[1] && Math.abs(xZero - p.x) < 3) {
				hits.add(kernel.getYAxis());
			}
		}
		
		// keep geoelements only on the top layer
		int maxlayer = 0;
		for (int i = 0; i < hits.size(); ++i) {
			GeoElement geo = (GeoElement) hits.get(i);
			if (maxlayer < geo.getLayer())
				maxlayer = geo.getLayer();
		}
		for (int i = hits.size() - 1; i >= 0; i--) {
			GeoElement geo = (GeoElement) hits.get(i);
			if (geo.getLayer() < maxlayer)
				hits.remove(i);
		}
		
		// remove all lists and  images if there are other objects too
		if (hits.size() - (hits.getListCount() + hits.getImageCount()) > 0) {
			for (int i = hits.size() - 1; i >= 0; i--) {
				GeoElement geo = (GeoElement) hits.get(i);
				if (geo.isGeoList() || geo.isGeoImage())
					hits.remove(i);
			}
		}
		
		
	}
	
	
	/**
	 * sets array of GeoElements whose visual representation is inside of
	 * the given screen rectangle
	 */
	final public void setHits(Rectangle rect) {
		hits.init();		
		
		if (rect == null) return;
		
		DrawableIterator it = allDrawableList.getIterator();
		while (it.hasNext()) {
			Drawable d = it.next();
			GeoElement geo = d.getGeoElement();
			if (geo.isEuclidianVisible() && d.isInside(rect)) {				
				hits.add(geo);
			}
		}
	}
	
	// ggb3D 2009-02-05 (end)
	
	
	
	
	
	
	
	
	
	/**
	 * returns GeoElement whose label is at screen coords (x,y).
	 */
	final public GeoElement getLabelHit(Point p) {
		if (!app.isLabelDragsEnabled()) return null;
		DrawableIterator it = allDrawableList.getIterator();
		while (it.hasNext()) {
			Drawable d = it.next();
			if (d.hitLabel(p.x, p.y)) {
				GeoElement geo = d.getGeoElement();
				if (geo.isEuclidianVisible())
					return geo;
			}
		}
		return null;
	}
	
	/**
	 * Returns array of GeoElements whose visual representation is at screen
	 * coords (x,y). order: points, vectors, lines, conics
	 * @param p 
	 * @return array of GeoElements drawn at (x,y)
	 */
	final public ArrayList<GeoElement> getHits(Point p) {
		return getHits(p, false);
	}
	
	/**
	 * Returns hits that are suitable for new point mode.
	 * A polygon is only kept if one of its sides is also in
	 * hits.
	 * @param hits 
	 * @return list of hits suitable for new point
	 */
	final public ArrayList<GeoElement> getHitsForNewPointMode(ArrayList<GeoElement> hits) {	
		if (hits == null) return null;
		
		Iterator<GeoElement> it = hits.iterator();
		while (it.hasNext()) {
			GeoElement geo = it.next();
			if (geo.isGeoPolygon()) {
				boolean sidePresent = false;
				GeoSegmentND [] sides = ((GeoPolygon) geo).getSegments();
				for (int k=0; k < sides.length; k++) {
					if (hits.contains(sides[k])) {
						sidePresent = true;
						break;
					}
				}
				
				if (!sidePresent)
					it.remove();					
			}				
		}				
		
		return hits;
	}

	final public ArrayList<GeoElement> getPointVectorNumericHits(Point p) {
		foundHits.clear();

		DrawableIterator it = allDrawableList.getIterator();
		while (it.hasNext()) {
			Drawable d = it.next();
			if (d.hit(p.x, p.y) || d.hitLabel(p.x, p.y)) {
				GeoElement geo = d.getGeoElement();
				if (geo.isEuclidianVisible()){
					if (
							//geo.isGeoNumeric() ||
							 geo.isGeoVector()
							|| geo.isGeoPoint()) 
					{
						foundHits.add(geo);
					}
				}
			}
		}

		return foundHits;
	}
	
	/**
	 * returns array of GeoElements whose visual representation is at screen
	 * coords (x,y). order: points, vectors, lines, conics
	 * @param p 
	 * @param includePolygons 
	 * @return array of GeoElements drawn at (x,y) ordered by type
	 */
	final public ArrayList<GeoElement> getHits(Point p, boolean includePolygons) {
		foundHits.clear();

		// count lists, images and Polygons
		int listCount = 0;
		int polyCount = 0;
		int imageCount = 0;

		// get anything but a polygon
		DrawableIterator it = allDrawableList.getIterator();
		while (it.hasNext()) {
			Drawable d = it.next();
			if (d.hit(p.x, p.y) || d.hitLabel(p.x, p.y)) {
				GeoElement geo = d.getGeoElement();

				if (geo.isEuclidianVisible()) {
					if (geo.isGeoList()) {
						listCount++;
					} else if (geo.isGeoImage()) {
						imageCount++;
					} else if (geo.isGeoPolygon()) {
						polyCount++;
					}
					foundHits.add(geo);
				}
			}
		}

		// look for axes
		if (foundHits.size() == 0) {
			if (showAxes[0] && Math.abs(yZero - p.y) < 3) {
				foundHits.add(kernel.getXAxis());
			}
			if (showAxes[1] && Math.abs(xZero - p.x) < 3) {
				foundHits.add(kernel.getYAxis());
			}
		}

		int size = foundHits.size();
		if (size == 0)
			return null;

		// remove all lists, images and polygons if there are other objects too
		if (size - (listCount + imageCount + polyCount) > 0) {
			for (int i = 0; i < foundHits.size(); ++i) {
				GeoElement geo = (GeoElement) foundHits.get(i);
				if (geo.isGeoList() || geo.isGeoImage() || (!includePolygons && geo.isGeoPolygon()))
					foundHits.remove(i);
			}
		}

		return foundHits;
	}
	
	protected ArrayList<GeoElement> foundHits = new ArrayList<GeoElement>();
	
	/**
	 * Returns array of GeoElements whose visual representation is inside of
	 * the given screen rectangle
	 * @param rect 
	 * @return elements drawn inside rectangle
	 */
	final public ArrayList<GeoElement> getHits(Rectangle rect) {
		foundHits.clear();		
		
		if (rect == null) return foundHits;
		
		DrawableIterator it = allDrawableList.getIterator();
		while (it.hasNext()) {
			Drawable d = it.next();
			GeoElement geo = d.getGeoElement();
			if (geo.isEuclidianVisible() && d.isInside(rect)) {				
				foundHits.add(geo);
			}
		}
		return foundHits;
	}

	/**
	 * returns array of independent GeoElements whose visual representation is
	 * at streen coords (x,y). order: points, vectors, lines, conics
	 */
	final public ArrayList getMoveableHits(Point p) {
		return getMoveableHits(getHits(p));
	}

	/**
	 * returns array of changeable GeoElements out of hits
	 */
	final public ArrayList getMoveableHits(ArrayList<GeoElement> hits) {
		return getMoveables(hits, TEST_MOVEABLE, null);
	}
	

	


	/**
	 * returns array of changeable GeoElements out of hits that implement
	 * PointRotateable
	 * @param hits 
	 * @param rotCenter 
	 * @return array of changeable GeoElements out of hits that implement
	 */
	final public ArrayList<GeoElement> getPointRotateableHits(ArrayList<GeoElement> hits,
			GeoPoint rotCenter) {
		return getMoveables(hits, TEST_ROTATEMOVEABLE, rotCenter);
	}

	protected final int TEST_MOVEABLE = 1;

	protected final int TEST_ROTATEMOVEABLE = 2;

	protected ArrayList<GeoElement> getMoveables(ArrayList<GeoElement> hits, int test, GeoPoint rotCenter) {
		if (hits == null)
			return null;

		GeoElement geo;
		moveableList.clear();
		for (int i = 0; i < hits.size(); ++i) {
			geo = hits.get(i);
			switch (test) {
			case TEST_MOVEABLE:
				// moveable object
				if (geo.isMoveable(this)) {
					moveableList.add(geo);
				}
				// point with changeable parent coords
				else if (geo.isGeoPoint()) {
					GeoPoint point = (GeoPoint) geo;
					if (point.hasChangeableCoordParentNumbers())
						moveableList.add(point);
				}
				// not a point, but has moveable input points
				else if (geo.hasMoveableInputPoints(this)) {
					moveableList.add(geo);
				}
				break;			

			case TEST_ROTATEMOVEABLE:
				// check for circular definition
				if (geo.isRotateMoveable()) {
					if (rotCenter == null || !geo.isParentOf(rotCenter))
						moveableList.add(geo);
				}

				break;
			}
		}
		if (moveableList.size() == 0)
			return null;
		else
			return moveableList;
	}

	protected ArrayList<GeoElement> moveableList = new ArrayList<GeoElement>();

	/**
	 * returns array of GeoElements of type geoclass whose visual representation
	 * is at streen coords (x,y). order: points, vectors, lines, conics
	 * @param p 
	 * @param geoclass 
	 * @param result 
	 * @return array of GeoElements of type geoclass drawn at coords (x,y)
	 */
	final public ArrayList<GeoElement> getHits(Point p, Class geoclass, ArrayList<GeoElement> result) {
		return getHits(getHits(p), geoclass, false, result);
	}

	/**
	 * Returns array of GeoElements NOT of type geoclass out of hits
	 * @param hits 
	 * @param geoclass 
	 * @param result 
	 * @return array of GeoElements NOT of type geoclass out of hits
	 */
	final public ArrayList<GeoElement> getOtherHits(ArrayList<GeoElement> hits, Class geoclass,
			ArrayList<GeoElement> result) {
		return getHits(hits, geoclass, true, result);
	}

	final public ArrayList<GeoElement> getHits(ArrayList<GeoElement> hits, Class geoclass,
			ArrayList<GeoElement> result) {
		return getHits(hits, geoclass, false, result);
	}


	/**
	 * Stores all GeoElements of type geoclass to result list.
	 * @param hits 
	 * @param geoclass 
	 * 
	 * @param other ==
	 *            true: returns array of GeoElements NOT of type geoclass out of
	 *            hits.
	 * @param result 
	 * @return either null (if result is emty) or result
	 */
	final protected ArrayList<GeoElement> getHits(ArrayList<GeoElement> hits, Class geoclass,
			boolean other, ArrayList<GeoElement> result) {
		if (hits == null)
			return null;

		result.clear();
		for (int i = 0; i < hits.size(); ++i) {
			boolean success = geoclass.isInstance(hits.get(i));
			if (other)
				success = !success;
			if (success)
				result.add(hits.get(i));
		}
		return result.size() == 0 ? null : result;
	}

	/**
	 * Stores all GeoElements of type GeoPoint, GeoVector, GeoNumeric to result list.
	 * @param hits 
	 * @param result 
	 * @return list of points, vectors and numbers
	 * 
	 */
	final protected ArrayList<GeoElement> getRecordableHits(ArrayList<GeoElement> hits, ArrayList<GeoElement> result) {
		if (hits == null)
			return null;

		result.clear();
		for (int i = 0; i < hits.size(); ++i) {
			GeoElement hit = (GeoElement)hits.get(i);
			boolean success = (hit.isGeoPoint() || hit.isGeoVector() || hit.isGeoNumeric());
			if (success)
				result.add(hits.get(i));
		}
		return result.size() == 0 ? null : result;
	}

	/**
	 * returns array of GeoElements whose visual representation is on top of
	 * screen coords of Point p. If there are points at location p only the
	 * points are returned. Otherwise all GeoElements are returned.
	 * @param p 
	 * @return list of hit GeoElements
	 * 
	 * @see EuclidianController#mousePressed(MouseEvent) 
	 * @see EuclidianController#mouseMoved(MouseEvent)
	 */
	final public ArrayList<GeoElement> getTopHits(Point p) {
		return getTopHits(getHits(p));
	}

	/**
	 * if there are GeoPoints in hits, all these points are returned. Otherwise
	 * hits is returned.
	 * @param hits 
	 * @return either GeoPoints (if selected) or all hits
	 * 
	 * @see EuclidianController#mousePressed(MouseEvent)
	 * @see EuclidianController#mouseMoved(MouseEvent)
	 */
	final public ArrayList<GeoElement> getTopHits(ArrayList<GeoElement> hits) {
		if (hits == null)
			return null;

		// point in there?
		if (containsGeoPoint(hits)) {
			getHits(hits, GeoPoint.class, false, topHitsList);
			return topHitsList;
		} else
			return hits;
	}

	protected ArrayList<GeoElement> topHitsList = new ArrayList<GeoElement>();

	final public boolean containsGeoPoint(ArrayList<GeoElement> hits) {
		if (hits == null)
			return false;

		for (int i = 0; i < hits.size(); i++) {
			if (((GeoElement) hits.get(i)).isGeoPoint())
				return true;
		}
		return false;
	}

	/**
	 * Returns the drawable for the given GeoElement.
	 * @param geo 
	 * @return drawable for the given GeoElement.
	 */
	final Drawable getDrawable(GeoElement geo) {
		return (Drawable) DrawableMap.get(geo);
	}
	
	final public DrawableND getDrawableND(GeoElement geo) {
		return getDrawable(geo);
	}

	/*
	 * interface View implementation
	 */

	/**
	 * adds a GeoElement to this view
	 */
	public void add(GeoElement geo) {
		
		//Application.printStacktrace(""+geo.isVisibleInView(this));
		
		
		//G.Sturr 2010-6-30
		// filter out any geo not marked for this view
		if(!isVisibleInThisView(geo)) return;
		// END G.Sturr
		
		
		// check if there is already a drawable for geo
		Drawable d = getDrawable(geo);
		if (d != null)
			return;
		
		d = createDrawable(geo);
		if (d != null) {
			addToDrawableLists(d);
			repaint();			
		}
		
	}
	
	public boolean isVisibleInThisView(GeoElement geo){
		return geo.isVisibleInView(this.getViewID());
	}

	
	public DrawableND createDrawableND(GeoElement geo) {
		return createDrawable(geo);
	}
	
	/**
	 * adds a GeoElement to this view
	 * @param geo GeoElement to be added
	 * @return drawable for given GeoElement
	 */
	protected Drawable createDrawable(GeoElement geo) {
		Drawable d = null;

		switch (geo.getGeoClassType()) {
		case GeoElement.GEO_CLASS_BOOLEAN:
			d = new DrawBoolean(this, (GeoBoolean) geo);			
			break;
		
		case GeoElement.GEO_CLASS_BUTTON:

			d = new DrawButton(this, (GeoButton) geo);			
			break;
		
		case GeoElement.GEO_CLASS_TEXTFIELD:

			d = new DrawTextField(this, (GeoTextField) geo);	
			break;
		
		case GeoElement.GEO_CLASS_POINT:
		case GeoElement.GEO_CLASS_POINT3D:
			d = new DrawPoint(this, (GeoPointND) geo);			
			break;					

		case GeoElement.GEO_CLASS_SEGMENT:
		case GeoElement.GEO_CLASS_SEGMENT3D:
			d = new DrawSegment(this, (GeoSegmentND) geo);
			break;

		case GeoElement.GEO_CLASS_RAY:
		case GeoElement.GEO_CLASS_RAY3D:
			d = new DrawRay(this, (GeoRayND) geo);
			break;

		case GeoElement.GEO_CLASS_LINE:
		case GeoElement.GEO_CLASS_LINE3D:
			d = new DrawLine(this, (GeoLineND) geo);
			break;		

		case GeoElement.GEO_CLASS_POLYGON:
		case GeoElement.GEO_CLASS_POLYGON3D:
			d = new DrawPolygon(this, (GeoPolygon) geo);
			break;

		case GeoElement.GEO_CLASS_POLYLINE:
			d = new DrawPolyLine(this, (GeoPolyLine) geo);
			break;
			
		case GeoElement.GEO_CLASS_FUNCTION_NVAR:
			if(((GeoFunctionNVar) geo).isBooleanFunction()) {
				d = new DrawInequality(this, (GeoFunctionNVar) geo);
			}
			break;
		case GeoElement.GEO_CLASS_INTERVAL:
			if(((GeoFunction) geo).isBooleanFunction()) {
				d = new DrawInequality(this, (GeoFunction) geo);
			}
			break;

		case GeoElement.GEO_CLASS_ANGLE:
			if (geo.isIndependent()) {
				// independent number may be shown as slider
				d = new DrawSlider(this, (GeoNumeric) geo);
			} else {
				d = new DrawAngle(this, (GeoAngle) geo);
				if (geo.isDrawable()) {
					if (!geo.isColorSet()) {
						Color col = geo.getConstruction()
								.getConstructionDefaults().getDefaultGeo(
										ConstructionDefaults.DEFAULT_ANGLE)
								.getObjectColor();
						geo.setObjColor(col);
					}
				}
			}
			break;

		case GeoElement.GEO_CLASS_NUMERIC:
			AlgoElement algo = geo.getDrawAlgorithm();
			if (algo == null) {
				// independent number may be shown as slider
				d = new DrawSlider(this, (GeoNumeric) geo);
			} else if (algo instanceof AlgoSlope) {
				d = new DrawSlope(this, (GeoNumeric) geo);
			} else if (algo instanceof AlgoIntegralDefinite) {
				d = new DrawIntegral(this, (GeoNumeric) geo);
			} else if (algo instanceof AlgoIntegralFunctions) {
				d = new DrawIntegralFunctions(this, (GeoNumeric) geo);
			} else if (algo instanceof AlgoFunctionAreaSums) {
				d = new DrawUpperLowerSum(this, (GeoNumeric) geo);
			}
			else if (algo instanceof AlgoBoxPlot) {
				d = new DrawBoxPlot(this, (GeoNumeric) geo);
			}
			if (d != null) {
				if (!geo.isColorSet()) {
					ConstructionDefaults consDef = geo.getConstruction()
							.getConstructionDefaults();
					if (geo.isIndependent()) {
						Color col = consDef.getDefaultGeo(
								ConstructionDefaults.DEFAULT_NUMBER).getObjectColor();
						geo.setObjColor(col);
					} else {
						Color col = consDef.getDefaultGeo(
								ConstructionDefaults.DEFAULT_POLYGON)
								.getObjectColor();
						geo.setObjColor(col);
					}
				}			
			}
			break;

		case GeoElement.GEO_CLASS_VECTOR:
		case GeoElement.GEO_CLASS_VECTOR3D:
			d = new DrawVector(this, (GeoVectorND) geo);
			break;

		case GeoElement.GEO_CLASS_CONICPART:
			d = new DrawConicPart(this, (GeoConicPart) geo);
			break;

		case GeoElement.GEO_CLASS_CONIC:
		case GeoElement.GEO_CLASS_CONIC3D:
			d = new DrawConic(this, (GeoConicND) geo);
			break;

		case GeoElement.GEO_CLASS_IMPLICIT_POLY:
			d = new DrawImplicitPoly(this, (GeoImplicitPoly) geo);
			break;

		case GeoElement.GEO_CLASS_FUNCTION:
		case GeoElement.GEO_CLASS_FUNCTIONCONDITIONAL:
			if(((GeoFunction) geo).isBooleanFunction()) {
				d = new DrawInequality(this,(FunctionalNVar) geo);
			}
			else 
				d = new DrawParametricCurve(this, (ParametricCurve) geo);
			break;

		case GeoElement.GEO_CLASS_TEXT:
			GeoText text = (GeoText) geo;
			d = new DrawText(this, text);				
			break;

		case GeoElement.GEO_CLASS_IMAGE:
			d = new DrawImage(this, (GeoImage) geo);
			break;

		case GeoElement.GEO_CLASS_LOCUS:
			d = new DrawLocus(this, (GeoLocus) geo);
			break;

		case GeoElement.GEO_CLASS_CURVE_CARTESIAN:
			d = new DrawParametricCurve(this, (GeoCurveCartesian) geo);
			break;

		case GeoElement.GEO_CLASS_LIST:
			d = new DrawList(this, (GeoList) geo);
			break;
		}
		
		if (d != null) {			
			DrawableMap.put(geo, d);
			if (geo.isGeoPoint())
				stickyPointList.add((GeoPointND)geo);
		}

		return d;
	}	
	
	/**
	 * adds a GeoElement to this view
	 * @param d drawable to be added
	 */
	protected void addToDrawableLists(Drawable d) {
		if (d == null) return;
		
		GeoElement geo = d.getGeoElement();
		int layer = geo.getLayer();

		switch (geo.getGeoClassType()) {

		case GeoElement.GEO_CLASS_ANGLE:
			if (geo.isIndependent()) {				
				drawLayers[layer].add(d);
			} else {				
				if (geo.isDrawable()) {					
					drawLayers[layer].add(d);
				} 
				else 
					d = null;
			}
			break;

		case GeoElement.GEO_CLASS_IMAGE:
			if (!bgImageList.contains(d))
				drawLayers[layer].add(d);
			break;

		default:
			drawLayers[layer].add(d);
			break;

		}

		if (d != null) {
			allDrawableList.add(d);			
		}
	}
	

	/**
	 * removes a GeoElement from this view
	 */
	final public void remove(GeoElement geo) {
		Drawable d = (Drawable) DrawableMap.get(geo);
		int layer = geo.getLayer();

		if (d != null) {
			switch (geo.getGeoClassType()) {
			//case GeoElement.GEO_CLASS_BOOLEAN:
				//drawLayers[layer].remove(d);
				// remove checkbox
				// not needed now it's not drawn by the view
				//((DrawBoolean) d).remove();
				//break;
			
			case GeoElement.GEO_CLASS_BUTTON:
				drawLayers[layer].remove(d);
				// remove button
				((DrawButton) d).remove();
				break;
				
			case GeoElement.GEO_CLASS_TEXTFIELD:
				drawLayers[layer].remove(d);
				// remove button
				((DrawTextField) d).remove();
				break;
			
			case GeoElement.GEO_CLASS_LIST:
				drawLayers[layer].remove(d);
				// remove sub-drawables
				((DrawList) d).remove();
				break;
			
			default:
				drawLayers[layer].remove(d);
				break;

			}

			allDrawableList.remove(d);

			DrawableMap.remove(geo);
			if (geo.isGeoPoint())
				stickyPointList.remove(geo);
			repaint();
		}
	}		

	/**
	 * renames an element
	 */
	public void rename(GeoElement geo) {
		Object d = DrawableMap.get(geo);
		if (d != null) {
			((Drawable) d).update();
			repaint();
		}
	}

	final public void update(GeoElement geo) {
		Object d = DrawableMap.get(geo);
		if (d != null) {
			((Drawable) d).update();
		}
	}
	
	final public void updateVisualStyle(GeoElement geo) {
		update(geo);
	}

	final public Drawable getDrawableFor(GeoElement geo) {
		return (Drawable) DrawableMap.get(geo);
	}

	final public void updateAuxiliaryObject(GeoElement geo) {
		// repaint();
	}

	final public void updateAllDrawables(boolean repaint) {
		allDrawableList.updateAll();
		if (repaint)
			repaint();
	}

	final protected void updateDrawableFontSize() {
		allDrawableList.updateFontSizeAll();
		repaint();
	}

	public void reset() {
		resetMode();
		updateBackgroundImage();
	}

	public void clearView() {		
		removeAll(); // remove hotEqns
		resetLists();
		initView(false);
		updateBackgroundImage(); // clear traces and images
		// resetMode();
	}
	

	final public void repaintView() {
		repaint();
	}
	
	final public void repaintEuclidianView(){
		repaint();
	}

	public String getXML() {
		StringBuilder sb = new StringBuilder();
    	getXML(sb,false);
    	return sb.toString();
    }
	
	/**
	 * This is only needed for second or above euclidian views
	 * @param evNo euclidian view number
	 */
	public void setEuclidianViewNo(int evNo)
	{
		if(evNo>=2){
			this.evNo=evNo;
		}
	}
	
	public int getEuclidianViewNo(){
		return evNo;
	}
	
    	
	/**
	 * returns settings in XML format
	 * @param sb 
	 * @param asPreference 
	 */
	public void getXML(StringBuilder sb,boolean asPreference) {
		sb.append("<euclidianView>\n");
		if(evNo>=2)
		{
			sb.append("\t<viewNumber ");
			sb.append("viewNo=\"");
			sb.append(evNo);
			sb.append("\"");
			sb.append("/>\n");
		}
		
		if (width > MIN_WIDTH && height > MIN_HEIGHT) {
			sb.append("\t<size ");
			sb.append(" width=\"");
			sb.append(width);
			sb.append("\"");
			sb.append(" height=\"");
			sb.append(height);
			sb.append("\"");
			sb.append("/>\n");
		}
		if(!isZoomable() && !asPreference){
			sb.append("\t<coordSystem");
			sb.append(" xMin=\"");
			sb.append(((GeoNumeric)xminObject).getLabel());
			sb.append("\"");
			sb.append(" xMax=\"");
			sb.append(((GeoNumeric)xmaxObject).getLabel());
			sb.append("\"");
			sb.append(" yMin=\"");
			sb.append(((GeoNumeric)yminObject).getLabel());
			sb.append("\"");
			sb.append(" yMax=\"");
			sb.append(((GeoNumeric)ymaxObject).getLabel());
			sb.append("\"");
			sb.append("/>\n");
		}else{
			sb.append("\t<coordSystem");
			sb.append(" xZero=\"");
			sb.append(xZero);
			sb.append("\"");
			sb.append(" yZero=\"");
			sb.append(yZero);
			sb.append("\"");
			sb.append(" scale=\"");
			sb.append(xscale);
			sb.append("\"");
			sb.append(" yscale=\"");
			sb.append(yscale);
			sb.append("\"");
			sb.append("/>\n");
		}
		// NOTE: the attribute "axes" for the visibility state of
		//  both axes is no longer needed since V3.0.
		//  Now there are special axis tags, see below.
		sb.append("\t<evSettings axes=\"");
		sb.append(showAxes[0] || showAxes[1]);
		sb.append("\" grid=\"");		
		sb.append(showGrid);
		sb.append("\" gridIsBold=\"");	// 
		sb.append(gridIsBold);			// Michael Borcherds 2008-04-11
		sb.append("\" pointCapturing=\"");
		sb.append(pointCapturingMode);
		sb.append("\" rightAngleStyle=\"");
		sb.append(app.rightAngleStyle);
		if (asPreference) {
			sb.append("\" allowShowMouseCoords=\"");
			sb.append(getAllowShowMouseCoords());

			sb.append("\" allowToolTips=\"");
			sb.append(getAllowToolTips());
		}

		sb.append("\" checkboxSize=\"");
		sb.append(app.booleanSize); // Michael Borcherds 2008-05-12

		sb.append("\" gridType=\"");
		sb.append(getGridType()); //		 cartesian/isometric/polar


		sb.append("\"/>\n");

		// background color
		sb.append("\t<bgColor r=\"");
		sb.append(bgColor.getRed());
		sb.append("\" g=\"");
		sb.append(bgColor.getGreen());
		sb.append("\" b=\"");
		sb.append(bgColor.getBlue());
		sb.append("\"/>\n");

		// axes color
		sb.append("\t<axesColor r=\"");
		sb.append(axesColor.getRed());
		sb.append("\" g=\"");
		sb.append(axesColor.getGreen());
		sb.append("\" b=\"");
		sb.append(axesColor.getBlue());
		sb.append("\"/>\n");

		// grid color
		sb.append("\t<gridColor r=\"");
		sb.append(gridColor.getRed());
		sb.append("\" g=\"");
		sb.append(gridColor.getGreen());
		sb.append("\" b=\"");
		sb.append(gridColor.getBlue());
		sb.append("\"/>\n");

		// axes line style
		sb.append("\t<lineStyle axes=\"");
		sb.append(axesLineType);
		sb.append("\" grid=\"");
		sb.append(gridLineStyle);
		sb.append("\"/>\n");

		// axis settings
		for (int i = 0; i < 2; i++) {
			sb.append("\t<axis id=\"");
			sb.append(i);
			sb.append("\" show=\"");
			sb.append(showAxes[i]);
			sb.append("\" label=\"");
			sb.append(axesLabels[i] == null ? "" : axesLabels[i]);
			sb.append("\" unitLabel=\"");
			sb.append(axesUnitLabels[i] == null ? "" : axesUnitLabels[i]);
			sb.append("\" tickStyle=\"");
			sb.append(axesTickStyles[i]);
			sb.append("\" showNumbers=\"");
			sb.append(showAxesNumbers[i]);

			// the tick distance should only be saved if
			// it isn't calculated automatically
			if (!automaticAxesNumberingDistances[i]) {
				sb.append("\" tickDistance=\"");
				sb.append(axesNumberingDistances[i]);
			}
			
			//  axis crossing values 	
			if (drawBorderAxes[i]) {
				sb.append("\" axisCrossEdge=\"");
				sb.append(true);
			} else if (!Kernel.isZero(axisCross[i]) && !drawBorderAxes[i]) {
				sb.append("\" axisCross=\"");
				sb.append(axisCross[i]);
			}

			// positive direction only flags
			if (positiveAxes[i]) {
				sb.append("\" positiveAxis=\"");
				sb.append(positiveAxes[i]);
			}

			sb.append("\"/>\n");
		}

		// grid distances
		if (!automaticGridDistance || 
				// compatibility to v2.7:
			automaticGridDistanceFactor != DEFAULT_GRID_DIST_FACTOR) 
		{
			sb.append("\t<grid distX=\"");
			sb.append(gridDistances[0]);
			sb.append("\" distY=\"");
			sb.append(gridDistances[1]);
			sb.append("\" distTheta=\"");
			//polar angle step added in v4.0
			sb.append(gridDistances[2]); 
			sb.append("\"/>\n");
		}


		sb.append("</euclidianView>\n");
	}

	/***************************************************************************
	 * ANIMATED ZOOMING
	 **************************************************************************/

	
	/**
	 * Zooms around fixed point (px, py)
	 */
	public final void zoom(double px, double py, double zoomFactor, int steps,
			boolean storeUndo) {
		if(!isZoomable())
			return;
		if (zoomer == null)
			zoomer = new MyZoomer();
		zoomer.init(px, py, zoomFactor, steps, storeUndo);
		zoomer.startAnimation();
		
		
	}

	/**
	 * Zooms around fixed point (center of screen)
	 * @param zoomFactor 
	 */
	public final void zoomAroundCenter(double zoomFactor) {
		if(!isZoomable())
			return;
		// keep xmin, xmax, ymin, ymax constant, adjust everything else
		
		xscale *= zoomFactor;
		yscale *= zoomFactor;
		
		scaleRatio = yscale / xscale;
		invXscale = 1.0d / xscale;
		invYscale = 1.0d / yscale;
		
		xZero = -xmin * xscale;
		width = (int)(xmax * xscale + xZero);
		yZero = ymax * yscale;
		height = (int)(yZero - ymin * yscale);
		
		setAxesIntervals(xscale, 0);
		setAxesIntervals(yscale, 1);
		calcPrintingScale();
		
		// tell kernel
		if (evNo != EVNO_GENERAL)
			kernel.setEuclidianViewBounds(evNo, xmin, xmax, ymin, ymax, xscale, yscale);

		coordTransform.setTransform(xscale, 0.0d, 0.0d, -yscale, xZero, yZero);

		updateBackgroundImage();
		updateAllDrawables(true);
		
	}
	
	protected MyZoomer zoomer;

	/**
	 * Zooms towards the given axes scale ratio. Note: Only the y-axis is
	 * changed here. ratio = yscale / xscale;
	 * @param newRatio 
	 * @param storeUndo 
	 */
	public final void zoomAxesRatio(double newRatio, boolean storeUndo) {
		if(!isZoomable())
			return;
		if(isUnitAxesRatio())
			return;
		if (axesRatioZoomer == null)
			axesRatioZoomer = new MyAxesRatioZoomer();
		axesRatioZoomer.init(newRatio, storeUndo);
		axesRatioZoomer.startAnimation();
	}

	protected MyAxesRatioZoomer axesRatioZoomer;

	public final void setViewShowAllObjects(boolean storeUndo) {
		
		double x0RW = xmin;
		double x1RW;
		double y0RW;
		double y1RW;
		double y0RWfunctions = 0;
		double y1RWfunctions = 0;
		double factor=0.03d; // don't want objects at edge
		double xGap = 0;
		
		TreeSet<GeoElement> allFunctions = kernel.getConstruction().getGeoSetLabelOrder(GeoElement.GEO_CLASS_FUNCTION);
		
		
		int noVisible = 0;
		// count no of visible functions
		Iterator<GeoElement> it = allFunctions.iterator();
		while (it.hasNext()) 
			if (((GeoFunction)(it.next())).isEuclidianVisible()) noVisible ++;;
		
			Rectangle rect=getBounds();			
			if (Kernel.isZero(rect.getHeight()) || Kernel.isZero(rect.getWidth())) {				
				if (noVisible == 0) return; // no functions or objects
				
				// just functions
				x0RW = Double.MAX_VALUE;
				x1RW = -Double.MAX_VALUE;
				y0RW = Double.MAX_VALUE;
				y1RW = -Double.MAX_VALUE;
				
				//Application.debug("just functions");
				
			}
			else
			{
				
				// get bounds of points, circles etc
				x0RW=toRealWorldCoordX(rect.getMinX());
				x1RW=toRealWorldCoordX(rect.getMaxX());
				y0RW=toRealWorldCoordY(rect.getMaxY());
				y1RW=toRealWorldCoordY(rect.getMinY());		
			}
			
			xGap=(x1RW - x0RW) * factor;
			
			boolean ok = false;			
			
		if (noVisible != 0) {
			
			// if there are functions we don't want to zoom in horizintally
			x0RW = Math.min(xmin, x0RW);
			x1RW = Math.max(xmax, x1RW);
			
			if (Kernel.isEqual(x0RW, xmin) && Kernel.isEqual(x1RW, xmax)) {
				// just functions (at sides!), don't need a gap
				xGap = 0;
			}
			else
			{
				xGap = (x1RW - x0RW) * factor;
			}
			
			//Application.debug("checking functions from "+x0RW+" to "+x1RW);
			
			y0RWfunctions = Double.MAX_VALUE;
			y1RWfunctions = -Double.MAX_VALUE;
		
			it = allFunctions.iterator();
			
			
			while (it.hasNext()) {
				GeoFunction fun = (GeoFunction)(it.next());
				double abscissa;
				// check 100 random heights 
				for (int i = 0 ; i < 200 ; i++) {
					
					if (i == 0)
						abscissa = fun.evaluate(x0RW); // check far left
					else if (i == 1)
						abscissa = fun.evaluate(x1RW); // check far right
					else
						abscissa = fun.evaluate(x0RW + Math.random() * (x1RW - x0RW));
					
					if (!Double.isInfinite(abscissa) && !Double.isNaN(abscissa)) {
						ok = true;
						if (abscissa > y1RWfunctions) y1RWfunctions = abscissa;
						// no else: there **might** be just one value
						if (abscissa < y0RWfunctions) y0RWfunctions = abscissa;
					}
				}
			}
			
		
		}
		
		if (!Kernel.isZero(y1RWfunctions - y0RWfunctions) && ok) {
			y0RW = Math.min(y0RW, y0RWfunctions);
			y1RW = Math.max(y1RW, y1RWfunctions);
			//Application.debug("min height "+y0RW+" max height "+y1RW);
		}

		
		// don't want objects at edge
		double yGap = (y1RW - y0RW) * factor;
		
		final double x0RW2 = x0RW - xGap;
		final double x1RW2 = x1RW + xGap;
		final double y0RW2 = y0RW - yGap;
		final double y1RW2 = y1RW + yGap;
		
		setAnimatedRealWorldCoordSystem(x0RW2, x1RW2, y0RW2, y1RW2, 10, storeUndo);

	}
	
	
	public final void setStandardView(boolean storeUndo) {
		if(!isZoomable())
			return;
		final double xzero, yzero;
		
		// check if the window is so small that we need custom 
		// positions.
		if(getWidth() < XZERO_STANDARD * 3) {
			xzero = getWidth() / 3.0;
		} else {
			xzero = XZERO_STANDARD;
		}
		
		if(getHeight() < YZERO_STANDARD * 1.6) {
			yzero = getHeight() / 1.6;
		} else {
			yzero = YZERO_STANDARD;
		}
		
		if (scaleRatio != 1.0) {
			// set axes ratio back to 1
			if (axesRatioZoomer == null)
				axesRatioZoomer = new MyAxesRatioZoomer();
			axesRatioZoomer.init(1, false);

			Thread waiter = new Thread() {
				public void run() {
					// wait until zoomer has finished
					axesRatioZoomer.startAnimation();
					while (axesRatioZoomer.isRunning()) {
						try {
							Thread.sleep(100);
						} catch (Exception e) {
						}
					}
					setAnimatedCoordSystem(xzero, yzero, 0, SCALE_STANDARD, 15, false);
				}
			};
			waiter.start();
		} else {
			setAnimatedCoordSystem(xzero, yzero, 0, SCALE_STANDARD, 15, false);
		}
		if (storeUndo)
			app.storeUndoInfo();
	}

	/**
	 * Sets coord system of this view. Just like setCoordSystem but with
	 * previous animation.
	 * 
	 * @param ox
	 *            x coord of old origin
	 * @param oy
	 *            y coord of old origin
	 * @param newScale
	 */
	final public void setAnimatedCoordSystem(double ox, double oy, double f, double newScale,
			int steps, boolean storeUndo) {
		
		ox+=(getXZero() - ox)*f;
		oy+=(getYZero() - oy)*f;
		
		if (!Kernel.isEqual(xscale, newScale)) {
			// different scales: zoom back to standard view
			double factor = newScale / xscale;
			zoom((ox - xZero * factor) / (1.0 - factor), (oy - yZero * factor)
					/ (1.0 - factor), factor, steps, storeUndo);
		} else {
			// same scales: translate view to standard origin
			// do this with the following action listener
			if (mover == null)
				mover = new MyMover();
			mover.init(ox, oy, storeUndo);
			mover.startAnimation();
		}
	}

	protected MyMover mover;

	protected class MyZoomer implements ActionListener {
		static final int MAX_STEPS = 15; // frames

		static final int DELAY = 10;

		static final int MAX_TIME = 400; // millis

		protected Timer timer; // for animation

		protected double px, py; // zoom point

		protected double factor;

		protected int counter, steps;

		protected double oldScale, newScale, add, dx, dy;

		protected long startTime;

		protected boolean storeUndo;

		public MyZoomer() {
			timer = new Timer(DELAY, this);
		}

		public void init(double px, double py, double zoomFactor, int steps,
				boolean storeUndo) {
			this.px = px;
			this.py = py;
			// this.zoomFactor = zoomFactor;
			this.storeUndo = storeUndo;

			oldScale = xscale;
			newScale = xscale * zoomFactor;
			this.steps = Math.min(MAX_STEPS, steps);
		}

		public synchronized void startAnimation() {
			if (timer == null)
				return;
			// setDrawMode(DRAW_MODE_DIRECT_DRAW);
			add = (newScale - oldScale) / steps;
			dx = xZero - px;
			dy = yZero - py;
			counter = 0;

			startTime = System.currentTimeMillis();
			timer.start();
		}

		protected synchronized void stopAnimation() {
			timer.stop();
			// setDrawMode(DRAW_MODE_BACKGROUND_IMAGE);
			factor = newScale / oldScale;
			setCoordSystem(px + dx * factor, py + dy * factor, newScale,
					newScale * scaleRatio);

			if (storeUndo)
				app.storeUndoInfo();
		}

		public synchronized void actionPerformed(ActionEvent e) {
			counter++;
			long time = System.currentTimeMillis() - startTime;
			if (counter == steps || time > MAX_TIME) { // end of animation
				stopAnimation();
			} else {
				factor = 1.0 + (counter * add) / oldScale;
				setCoordSystem(px + dx * factor, py + dy * factor, oldScale
						* factor, oldScale * factor * scaleRatio);
			}
		}
	}	
	
	
	protected class MyZoomerRW implements ActionListener {
		static final int MAX_STEPS = 15; // frames

		static final int DELAY = 10;

		static final int MAX_TIME = 400; // millis

		protected Timer timer; // for animation

		protected int counter, steps;

		protected long startTime;

		protected boolean storeUndo;
		
		protected double x0, x1, y0, y1, xminOld, xmaxOld, yminOld, ymaxOld;

		public MyZoomerRW() {
			timer = new Timer(DELAY, this);
		}

		public void init(double x0, double x1,
				double y0, double y1, int steps, boolean storeUndo) {
			this.x0 = x0;
			this.x1 = x1;
			this.y0 = y0;
			this.y1 = y1;
			
			xminOld = xmin;
			xmaxOld = xmax;
			yminOld = ymin;
			ymaxOld = ymax;
			// this.zoomFactor = zoomFactor;
			this.storeUndo = storeUndo;

			this.steps = Math.min(MAX_STEPS, steps);
		}

		public synchronized void startAnimation() {
			if (timer == null)
				return;
			counter = 0;

			startTime = System.currentTimeMillis();
			timer.start();
		}

		protected synchronized void stopAnimation() {
			timer.stop();
			setRealWorldCoordSystem(x0, x1, y0, y1);
			
			if (storeUndo)
				app.storeUndoInfo();
		}

		public synchronized void actionPerformed(ActionEvent e) {
			counter++;
			long time = System.currentTimeMillis() - startTime;
			if (counter == steps || time > MAX_TIME) { // end of animation
				stopAnimation();
			} else {
				double i = counter;
				double j = steps - counter;
				setRealWorldCoordSystem((x0 * i + xminOld * j) / steps,
						(x1 * i + xmaxOld * j) / steps,
						(y0 * i + yminOld * j) / steps,
						(y1 * i + ymaxOld * j ) /steps);
			}
		}
	}

	// changes the scale of the y-Axis continously to reach
	// the given scale ratio yscale / xscale
	protected class MyAxesRatioZoomer implements ActionListener {

		protected Timer timer; // for animation

		protected double factor;

		protected int counter;

		protected double oldScale, newScale, add;

		protected long startTime;

		protected boolean storeUndo;

		public MyAxesRatioZoomer() {
			timer = new Timer(MyZoomer.DELAY, this);
		}

		public void init(double ratio, boolean storeUndo) {
			// this.ratio = ratio;
			this.storeUndo = storeUndo;

			// zoomFactor = ratio / scaleRatio;
			oldScale = yscale;
			newScale = xscale * ratio; // new yscale
		}

		public synchronized void startAnimation() {
			if (timer == null)
				return;
			// setDrawMode(DRAW_MODE_DIRECT_DRAW);
			add = (newScale - oldScale) / MyZoomer.MAX_STEPS;
			counter = 0;

			startTime = System.currentTimeMillis();
			timer.start();
		}

		protected synchronized void stopAnimation() {
			timer.stop();
			// setDrawMode(DRAW_MODE_BACKGROUND_IMAGE);
			setCoordSystem(xZero, yZero, xscale, newScale);
			if (storeUndo)
				app.storeUndoInfo();
		}

		public synchronized void actionPerformed(ActionEvent e) {
			counter++;
			long time = System.currentTimeMillis() - startTime;
			if (counter == MyZoomer.MAX_STEPS || time > MyZoomer.MAX_TIME) { // end
				// of
				// animation
				stopAnimation();
			} else {
				factor = 1.0 + (counter * add) / oldScale;
				setCoordSystem(xZero, yZero, xscale, oldScale * factor);
			}
		}

		final synchronized boolean isRunning() {
			return timer.isRunning();
		}
	}

	// used for animated moving of euclidian view to standard origin
	protected class MyMover implements ActionListener {
		protected double dx, dy, add;

		protected int counter;

		protected double ox, oy; // new origin

		protected Timer timer;

		protected long startTime;

		protected boolean storeUndo;

		public MyMover() {
			timer = new Timer(MyZoomer.DELAY, this);
		}

		public void init(double ox, double oy, boolean storeUndo) {
			this.ox = ox;
			this.oy = oy;
			this.storeUndo = storeUndo;
		}

		public synchronized void startAnimation() {
			dx = xZero - ox;
			dy = yZero - oy;
			if (Kernel.isZero(dx) && Kernel.isZero(dy))
				return;

			// setDrawMode(DRAW_MODE_DIRECT_DRAW);
			add = 1.0 / MyZoomer.MAX_STEPS;
			counter = 0;

			startTime = System.currentTimeMillis();
			timer.start();
		}

		protected synchronized void stopAnimation() {
			timer.stop();
			// setDrawMode(DRAW_MODE_BACKGROUND_IMAGE);
			setCoordSystem(ox, oy, xscale, yscale);
			if (storeUndo)
				app.storeUndoInfo();
		}

		public synchronized void actionPerformed(ActionEvent e) {
			counter++;
			long time = System.currentTimeMillis() - startTime;
			if (counter == MyZoomer.MAX_STEPS || time > MyZoomer.MAX_TIME) { // end
				// of
				// animation
				stopAnimation();
			} else {
				double factor = 1.0 - counter * add;
				setCoordSystem(ox + dx * factor, oy + dy * factor, xscale,
						yscale);
			}
		}
	}

	public final double getPrintingScale() {
		return printingScale;
	}

	public final void setPrintingScale(double printingScale) {
		this.printingScale = printingScale;
	}

	/**
	 * When function (or parabola) is transformed to curve, we need
	 * some good estimate for which part of curve should be ploted
	 * @return lower bound for function -> curve transform
	 */
	public double getXmaxForFunctions() {
		return 2*xmax-xmin+ymax-ymin;
	}
	/**
	 * @see #getXmaxForFunctions()
	 * @return upper bound for function -> curve transform
	 */
	public double getXminForFunctions() {
		return 2*xmin-xmax+ymin-ymax;
	}
	
	/**
	 * @return Returns the xmax.
	 */
	public double getXmax() {
		return xmax;
	}

	/**
	 * @return Returns the xmin.
	 */
	public double getXmin() {
		return xmin;
	}

	/**
	 * @return Returns the ymax.
	 */
	public double getYmax() {
		return ymax;
	}

	/**
	 * @return Returns the ymin.
	 */
	public double getYmin() {
		return ymin;
	}

	public Color getAxesColor() {
		return axesColor;
	}

	public void setAxesColor(Color axesColor) {
		if (axesColor != null)
			this.axesColor = axesColor;
	}

	public String[] getAxesLabels() {
		return axesLabels;
	}

	public void setAxesLabels(String[] axesLabels) {
		this.axesLabels = axesLabels;
		for (int i = 0; i < 2; i++) {
			if (axesLabels[i] != null && axesLabels[i].length() == 0) {
				axesLabels[i] = null;
			}
		}
	}
	
	
	/**
	 * sets the axis label to axisLabel
	 * @param axis
	 * @param axisLabel
	 */
	public void setAxisLabel(int axis, String axisLabel){
		if (axisLabel != null && axisLabel.length() == 0) 
			axesLabels[axis] = null;
		else
			axesLabels[axis] = axisLabel;
	}
	

	public void setAutomaticAxesNumberingDistance(boolean flag, int axis) {
		automaticAxesNumberingDistances[axis] = flag;
		if (axis == 0)
			setAxesIntervals(xscale, 0);
		else
			setAxesIntervals(yscale, 1);
	}

	public boolean[] isAutomaticAxesNumberingDistance() {
		return automaticAxesNumberingDistances;
	}

	public double[] getAxesNumberingDistances() {
		return axesNumberingDistances;
	}

	/**
	 * 
	 * @param dist
	 * @param axis 0 for xAxis, 1 for yAxis
	 */
	public void setAxesNumberingDistance(double dist, int axis) {
		if (!Double.isNaN(dist)) {
			axesNumberingDistances[axis] = dist;
			setAutomaticAxesNumberingDistance(false, axis);
		} else {
			setAutomaticAxesNumberingDistance(true, axis);			
		}
	}

	public Color getBackground() {
		return bgColor;
	}

	public void setBackground(Color bgColor) {
		if (bgColor != null)
			this.bgColor = bgColor;
	}

	public Color getGridColor() {
		return gridColor;
	}

//	 Michael Borcherds 2008-04-11
	public boolean getGridIsBold() {
		return gridIsBold;
	}
	
//	 Michael Borcherds 2008-04-11
	public void setGridIsBold(boolean gridIsBold ) {
		if (this.gridIsBold == gridIsBold) return;
		
		this.gridIsBold=gridIsBold;
		setGridLineStyle(gridLineStyle);
		
		updateBackgroundImage();
	}

	public void setGridColor(Color gridColor) {
		if (gridColor != null)
			this.gridColor = gridColor;
	}

	public void setAutomaticGridDistance(boolean flag) {
		automaticGridDistance = flag;
		setAxesIntervals(xscale, 0);
		setAxesIntervals(yscale, 1);
		if(flag)
			gridDistances[2] = Math.PI/6;
	}

	public boolean isAutomaticGridDistance() {
		return automaticGridDistance;
	}

	public double[] getGridDistances() {
		return gridDistances;
	}

	public void setGridDistances(double[] dist) {
		if (dist == null) {
			Application.debug("NULL");
			return;
		}
		gridDistances = dist;
		setAutomaticGridDistance(false);
	}

	public int getGridLineStyle() {
		return gridLineStyle;
	}

	public void setGridLineStyle(int gridLineStyle) {
		this.gridLineStyle = gridLineStyle;
		gridStroke = getStroke(gridIsBold?2f:1f, gridLineStyle); // Michael Borcherds 2008-04-11 added gridisbold
	}

	public int getAxesLineStyle() {
		return axesLineType;
	}

	public void setAxesLineStyle(int axesLineStyle) {
		this.axesLineType = axesLineStyle;
	}

	public boolean[] getShowAxesNumbers() {
		return showAxesNumbers;
	}

	public void setShowAxesNumbers(boolean[] showAxesNumbers) {
		this.showAxesNumbers = showAxesNumbers;
	}
	
	public void setShowAxisNumbers(int axis, boolean showAxisNumbers){
		showAxesNumbers[axis]=showAxisNumbers;
	}

	public String[] getAxesUnitLabels() {
		return axesUnitLabels;
	}

	public void setAxesUnitLabels(String[] axesUnitLabels) {
		this.axesUnitLabels = axesUnitLabels;

		// check if pi is an axis unit
		for (int i = 0; i < 2; i++) {
			piAxisUnit[i] = axesUnitLabels[i] != null
					&& axesUnitLabels[i].equals(Unicode.PI_STRING);
		}
		setAxesIntervals(xscale, 0);
		setAxesIntervals(yscale, 1);
	}

	public int[] getAxesTickStyles() {
		return axesTickStyles;
	}
	
	public void setAxisTickStyle(int axis, int tickStyle){
		axesTickStyles[axis]=tickStyle;
	}

	public void setAxesTickStyles(int[] axesTickStyles) {
		this.axesTickStyles = axesTickStyles;
	}

	/* --> moved to Kernel and Kernel3D
	public String getModeText(int mode) {
		
		return getKernel().getModeText(mode);
	}
	*/

	public int getSelectedWidth() {
		if (selectionRectangle == null)
			return getWidth();
		else
			return selectionRectangle.width;
	}
	
	public int getSelectedHeight() {
		if (selectionRectangle == null)
			return getHeight();
		else
			return selectionRectangle.height;
	}		
	
	public int getExportWidth() {
		if (selectionRectangle != null) return selectionRectangle.width;
		try {
			GeoPoint export1=(GeoPoint)kernel.lookupLabel(EXPORT1);	       
			GeoPoint export2=(GeoPoint)kernel.lookupLabel(EXPORT2);
			double [] xy1 = new double[2];
			double [] xy2 = new double[2];
			export1.getInhomCoords(xy1);
			export2.getInhomCoords(xy2);
			double x1=xy1[0];
			double x2=xy2[0];
			x1 = x1 / invXscale + xZero;
			x2 = x2 / invXscale + xZero;
			
			return (int)Math.abs(x1-x2) + 2;
		}		
		catch (Exception e) {return getWidth();}

			
	}
	
	public int getExportHeight() {
		if (selectionRectangle != null) return selectionRectangle.height;

		try {
			GeoPoint export1=(GeoPoint)kernel.lookupLabel(EXPORT1);	       
			GeoPoint export2=(GeoPoint)kernel.lookupLabel(EXPORT2);
			double [] xy1 = new double[2];
			double [] xy2 = new double[2];
			export1.getInhomCoords(xy1);
			export2.getInhomCoords(xy2);
			double y1=xy1[1];
			double y2=xy2[1];
			y1 = yZero - y1 / invYscale;
			y2 = yZero - y2 / invYscale;

			return (int)Math.abs(y1-y2) + 2;
		}		
		catch (Exception e) {return getHeight();}
			
	}		
	
	public Rectangle getSelectionRectangle() {
		return selectionRectangle;
	}

	public void setSelectionRectangle(Rectangle selectionRectangle) {
		//Application.printStacktrace("");
		this.selectionRectangle = selectionRectangle;		
	}

	public EuclidianController getEuclidianController() {
		return euclidianController;
	}
	
	final public Graphics2D getTempGraphics2D(Font font) {
		g2Dtemp.setFont(font); // Michael Borcherds 2008-06-11 bugfix for Corner[text,n]		
		return g2Dtemp;
	}

	final public Graphics2D getTempGraphics2D() {
		g2Dtemp.setFont(app.getPlainFont()); 
		return g2Dtemp;
	}

	final public static boolean usesSelectionAsInput(int mode)
	{
		switch (mode)
		{
		case EuclidianConstants.MODE_MIRROR_AT_LINE: return true;
		case EuclidianConstants.MODE_MIRROR_AT_POINT: return true;
		case EuclidianConstants.MODE_ROTATE_BY_ANGLE: return true;
		case EuclidianConstants.MODE_TRANSLATE_BY_VECTOR: return true;
		case EuclidianConstants.MODE_DILATE_FROM_POINT: return true;
		default: return false;
		}
	}

	final public static boolean usesSelectionRectangleAsInput(int mode)
	{
		switch (mode)
		{
		case EuclidianConstants.MODE_VISUAL_STYLE: 
		case EuclidianConstants.MODE_FITLINE: 
		case EuclidianConstants.MODE_CREATE_LIST: 
		case EuclidianConstants.MODE_PEN: 
		case EuclidianConstants.MODE_MIRROR_AT_LINE: 
		case EuclidianConstants.MODE_MIRROR_AT_POINT: 
		case EuclidianConstants.MODE_ROTATE_BY_ANGLE: 
		case EuclidianConstants.MODE_TRANSLATE_BY_VECTOR: 
		case EuclidianConstants.MODE_DILATE_FROM_POINT: 
		case EuclidianConstants.MODE_COPY_VISUAL_STYLE: return true;
		default: return false;
		}
	}

	public void resetMaxLayerUsed() {
		app.maxLayerUsed = 0;		
	}

	public void resetXYMinMaxObjects() {
		if (evNo == 1 || evNo == 2) {
			EuclidianSettings es = app.getSettings().getEuclidian(evNo);
			// this is necessary in File->New because there might have been dynamic xmin bounds
			GeoNumeric xmao = new GeoNumeric(kernel.getConstruction(), xmaxObject.getNumber().getDouble());
			GeoNumeric xmio = new GeoNumeric(kernel.getConstruction(), xminObject.getNumber().getDouble());
			GeoNumeric ymao = new GeoNumeric(kernel.getConstruction(), ymaxObject.getNumber().getDouble());
			GeoNumeric ymio = new GeoNumeric(kernel.getConstruction(), yminObject.getNumber().getDouble());
			es.setXmaxObject(xmao, false);
			es.setXminObject(xmio, false);
			es.setYmaxObject(ymao, false);
			es.setYminObject(ymio, true);
		}
	}

	/**
	 * 
	 * setters and getters for EuclidianViewInterface
	 * 
	 */
	
	
	public void setShowMouseCoords(boolean b){
		showMouseCoords=b;
	}
	
	public boolean getAllowShowMouseCoords() {
		return allowShowMouseCoords;
	}

	public void setAllowShowMouseCoords(boolean neverShowMouseCoords) {
		this.allowShowMouseCoords = neverShowMouseCoords;
	}
	
	
	public boolean getShowMouseCoords(){
		return showMouseCoords;
	}
	
	public void setShowAxesRatio(boolean b){
		showAxesRatio=b;
	}

	public Previewable getPreviewDrawable(){
		return previewDrawable;
	}
	
	
	public double getGridDistances(int i){
		return gridDistances[i];
	}
	
	
	public double getInvXscale(){
		return invXscale;
	}
	
	public double getInvYscale(){
		return invYscale;
	}
	
	
	public int getViewWidth(){
		return width;
	}
	
	public int getViewHeight(){
		return height;
	}	
	
	
	
	
	
	
	
	
	
	/////////////////////////////////////////
	// previewables
	
	
	public Previewable createPreviewLine(ArrayList<GeoPointND> selectedPoints){
		
		return new DrawLine(this, selectedPoints, DrawLine.PREVIEW_LINE);
	}
	
	public Previewable createPreviewPerpendicularBisector(ArrayList<GeoPointND> selectedPoints){
		
		return new DrawLine(this, selectedPoints, DrawLine.PREVIEW_PERPENDICULAR_BISECTOR);
	}
	
	public Previewable createPreviewAngleBisector(ArrayList<GeoPointND> selectedPoints){
		
		return new DrawLine(this, selectedPoints, DrawLine.PREVIEW_ANGLE_BISECTOR);
	}
	
	
	public Previewable createPreviewSegment(ArrayList<GeoPointND> selectedPoints){
		return new DrawSegment(this, selectedPoints);
	}	
	
	
	public Previewable createPreviewRay(ArrayList<GeoPointND> selectedPoints){
		return new DrawRay(this, selectedPoints);
	}	
	
	public Previewable createPreviewVector(ArrayList<GeoPointND> selectedPoints){
		return new DrawVector(this, selectedPoints);
	}
	
	
	public Previewable createPreviewPolygon(ArrayList<GeoPointND> selectedPoints){
		return new DrawPolygon(this, selectedPoints);
	}	
	
	public Previewable createPreviewConic(int mode, ArrayList<GeoPointND> selectedPoints){
		return new DrawConic(this, mode, selectedPoints);
	}	
	
	public Previewable createPreviewAngle(ArrayList<GeoPointND> selectedPoints){
		return new DrawAngle(this, selectedPoints);
	}	
	
	public Previewable createPreviewPolyLine(ArrayList<GeoPointND> selectedPoints){
		return new DrawPolyLine(this, selectedPoints);
	}	
	
	public void updatePreviewable(){
		Point mouseLoc = getEuclidianController().mouseLoc;
		getPreviewDrawable().updateMousePos(toRealWorldCoordX(mouseLoc.x), toRealWorldCoordY(mouseLoc.y));
	}
	
	
	
	
	public void mouseEntered(){
		
	}
	
	public void mouseExited(){
		
	}

	public Previewable createPreviewParallelLine(ArrayList<GeoPointND> selectedPoints,
			ArrayList<GeoLineND> selectedLines) {
		return new DrawLine(this, selectedPoints, selectedLines, true);
	}
	
	public Previewable createPreviewPerpendicularLine(ArrayList<GeoPointND> selectedPoints,
			ArrayList<GeoLineND> selectedLines) {
		return new DrawLine(this, selectedPoints, selectedLines, false);
	}
	public GeneralPathClipped getBoundingPath(){
		GeneralPathClipped gs = new GeneralPathClipped(this);
    	gs.moveTo(0,0);
    	gs.lineTo(width,0);
    	gs.lineTo(width,height);
    	gs.lineTo(0,height);
    	gs.lineTo(0,0);
    	gs.closePath();
    	return gs;
	}
	
	
	
	// object is hit if mouse is within this many pixels
	// (more for points, see DrawPoint)
	private int capturingThreshold = 3;

	public void setCapturingThreshold(int i) {
		capturingThreshold = i;		
	}
	public int getCapturingThreshold() {
		return capturingThreshold;		
	}

	public boolean isZoomable() {
		if(xminObject != null && (!((GeoNumeric)xminObject).isIndependent() || 
				((GeoNumeric)xminObject).isLabelSet()))
			return false;
		if(xmaxObject != null && (!((GeoNumeric)xmaxObject).isIndependent() || 
				((GeoNumeric)xmaxObject).isLabelSet()))
			return false;
		if(yminObject != null && (!((GeoNumeric)yminObject).isIndependent() || 
				((GeoNumeric)yminObject).isLabelSet()))
			return false;
		if(ymaxObject != null && (!((GeoNumeric)ymaxObject).isIndependent() || 
				((GeoNumeric)ymaxObject).isLabelSet()))
			return false;
		return true;
	}
	private boolean updatingBounds = false;
	public void updateBounds() {				
		if (updatingBounds) return;
		updatingBounds = true;
		double xmin2 = xminObject.getDouble();
		double xmax2 = xmaxObject.getDouble();
		double ymin2 = yminObject.getDouble();
		double ymax2 = ymaxObject.getDouble();		
		if(isUnitAxesRatio() && height > 0 && width > 0){			
			double newWidth = (ymax2 - ymin2)*width/(height+0.0);
			double newHeight = (xmax2 - xmin2)*height/(width+0.0);
			
			if(xmax2-xmin2 < newWidth){
				double c = (xmin2 + xmax2)/2; 
				xmin2 =  c - newWidth/2;
				xmax2 =  c + newWidth/2;				
			} else {
				double c = (ymin2 + ymax2)/2;
				ymin2 = c - newHeight/2;
				ymax2 = c + newHeight/2;				
			}			
		}
		if((xmax2-xmin2 > Kernel.MIN_PRECISION)&&(ymax2-ymin2 > Kernel.MIN_PRECISION))
			setRealWorldCoordSystem(xmin2,xmax2,ymin2,ymax2);
		updatingBounds = false;		
	}
	
	
	
	
	
	///////////////////////////////////////////
	// FOR EUCLIDIANVIEWFORPLANE
	///////////////////////////////////////////
	
	
	
	/**
	 * tranform in view coords
	 * @param coords
	 * @return the same coords for classic 2d view
	 */
	public Coords getCoordsForView(Coords coords){
		return coords;
	}
	
	/**
	 * return null if classic 2D view
	 * @return matrix representation of the plane shown by this view
	 */
	public CoordMatrix getMatrix(){
		return null;
	}
	
	/**
	 * 
	 * @param conic
	 * @param M
	 * @param ev
	 * @return affine transform of the conic for this view
	 */
	public AffineTransform getTransform(GeoConicND conic, Coords M, Coords[] ev){
		return conic.getAffineTransform();
	}
	
	
	public String getFromPlaneString(){
		return "xOyPlane";
	}

	
	public String getTranslatedFromPlaneString(){
		return app.getPlain("xOyPlane");
	}
	
	public boolean isDefault2D(){
		return true;
	}
	
	/**
	 * 
	 * @return null (for 2D) and xOyPlane (for 3D)
	 */
	public GeoPlaneND getPlaneContaining(){
		return kernel.getDefaultPlane();
	}
	
	/**
	 * 
	 * @return null (for 2D) and xOyPlane (for 3D)
	 */
	public GeoDirectionND getDirection(){
		return getPlaneContaining();
	}
	
	public void updateForPlane(){
		//only used in EuclidianViewForPlane
	}
	
	public boolean hasForParent(GeoElement geo){
		return false;
	}
	
	public boolean isMoveable(GeoElement geo){
		return geo.isMoveable();
	}
	

	public ArrayList<GeoPoint> getFreeInputPoints(AlgoElement algoParent){
		return algoParent.getFreeInputPoints();
	}

	/**
	 * Replaces num by num2 in xmin, xmax,ymin,ymax. Does not
	 * add / remove EV listeners from these numerics
	 * @param num old numeric
	 * @param num2 new numeric
	 */
	public void replaceBoundObject(GeoNumeric num, GeoNumeric num2) {
		if(xmaxObject == num)
			xmaxObject = num2;
		if(xminObject == num)
			xminObject = num2;
		if(ymaxObject == num)
			ymaxObject = num2;
		if(yminObject == num)
			yminObject = num2;
		updateBounds();
	}
	public void settingsChanged(AbstractSettings settings) {
		EuclidianSettings evs = (EuclidianSettings)settings;
		
		setXminObject(evs.getXminObject());
		setXmaxObject(evs.getXmaxObject());
		setYminObject(evs.getYminObject());
		setYmaxObject(evs.getYmaxObject());

		setBackground(evs.getBackground());
		setAxesColor(evs.getAxesColor());
		setGridColor(evs.getGridColor());
		setAxesLineStyle(evs.getAxesLineStyle());
		setGridLineStyle(evs.getGridLineStyle());

		double[] d = evs.getGridDistances();
		if (!evs.getAutomaticGridDistance() && d == null)
			setAutomaticGridDistance(false);
		else if (d == null)
			setAutomaticGridDistance(true);
		else
			setGridDistances(d);

		setShowAxis(0, evs.getShowAxis(0), false);
		setShowAxis(1, evs.getShowAxis(1), false);
		axesLabels = evs.getAxesLabels();
		setAxesUnitLabels(evs.getAxesUnitLabels());
		
		showAxesNumbers = evs.getShowAxisNumbers();

		// might be Double.NaN, handled in setAxesNumberingDistance()
		if (!evs.getAutomaticAxesNumberingDistance(0) && Double.isNaN(evs.getAxisNumberingDistanceX()))
			setAutomaticAxesNumberingDistance(false, 0);
		else
			setAxesNumberingDistance(evs.getAxisNumberingDistanceX(), 0);
		if (!evs.getAutomaticAxesNumberingDistance(1) && Double.isNaN(evs.getAxisNumberingDistanceY()))
			setAutomaticAxesNumberingDistance(false, 1);
		else
			setAxesNumberingDistance(evs.getAxisNumberingDistanceY(), 1);

		axesTickStyles[0] = evs.getAxesTickStyles()[0];
		axesTickStyles[1] = evs.getAxesTickStyles()[1];

		setDrawBorderAxes(evs.getDrawBorderAxes());

		axisCross[0] = evs.getAxesCross()[0];
		axisCross[1] = evs.getAxesCross()[1];
		positiveAxes[0] = evs.getPositiveAxes()[0];
		positiveAxes[1] = evs.getPositiveAxes()[1];
		
		Dimension ps = evs.getPreferredSize();
		if (ps != null) setPreferredSize(ps);
		
		showGrid(evs.getShowGrid());
		
		setGridIsBold(evs.getGridIsBold());
		
		setGridType(evs.getGridType());
		
		setPointCapturing(evs.getPointCapturingMode());
		
		setAllowShowMouseCoords(evs.getAllowShowMouseCoords());
		
		setAllowToolTips(evs.getAllowToolTips());

		if(evs.getXmaxObject()==null) {
			setCoordSystem(evs.getXZero(), evs.getYZero(), evs.getXscale(), evs.getYscale(), true);
			evs.setXminObject(xminObject, false);
			evs.setXmaxObject(xmaxObject, false);
			evs.setYminObject(yminObject, false);
			evs.setYmaxObject(ymaxObject, false);
		} else {
			updateBounds();
		}
	}
	public int getViewID() {
		switch (evNo) {
		case 1: return Application.VIEW_EUCLIDIAN;
		case 2: return Application.VIEW_EUCLIDIAN2;
		default: return Application.VIEW_NONE;
		}
	}
	
	
	public Graphics2D getGraphicsForPen(){
		return (Graphics2D) getGraphics();
		
	}
}