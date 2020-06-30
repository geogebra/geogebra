package org.geogebra.common.euclidian;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import javax.annotation.CheckForNull;

import org.geogebra.common.awt.GAffineTransform;
import org.geogebra.common.awt.GBasicStroke;
import org.geogebra.common.awt.GBufferedImage;
import org.geogebra.common.awt.GColor;
import org.geogebra.common.awt.GDimension;
import org.geogebra.common.awt.GEllipse2DDouble;
import org.geogebra.common.awt.GFont;
import org.geogebra.common.awt.GGeneralPath;
import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.awt.GLine2D;
import org.geogebra.common.awt.GPoint;
import org.geogebra.common.awt.GPoint2D;
import org.geogebra.common.awt.GRectangle;
import org.geogebra.common.awt.GShape;
import org.geogebra.common.awt.MyImage;
import org.geogebra.common.euclidian.background.DrawBackground;
import org.geogebra.common.euclidian.draw.DrawAngle;
import org.geogebra.common.euclidian.draw.DrawConic;
import org.geogebra.common.euclidian.draw.DrawDropDownList;
import org.geogebra.common.euclidian.draw.DrawImage;
import org.geogebra.common.euclidian.draw.DrawInline;
import org.geogebra.common.euclidian.draw.DrawInputBox;
import org.geogebra.common.euclidian.draw.DrawLine;
import org.geogebra.common.euclidian.draw.DrawLine.PreviewType;
import org.geogebra.common.euclidian.draw.DrawPoint;
import org.geogebra.common.euclidian.draw.DrawPolyLine;
import org.geogebra.common.euclidian.draw.DrawPolygon;
import org.geogebra.common.euclidian.draw.DrawRay;
import org.geogebra.common.euclidian.draw.DrawSegment;
import org.geogebra.common.euclidian.draw.DrawVector;
import org.geogebra.common.euclidian.draw.DrawWidget;
import org.geogebra.common.euclidian.event.PointerEventType;
import org.geogebra.common.factories.AwtFactory;
import org.geogebra.common.factories.FormatFactory;
import org.geogebra.common.gui.SetLabels;
import org.geogebra.common.gui.dialog.options.OptionsEuclidian;
import org.geogebra.common.gui.inputfield.AutoCompleteTextField;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.ModeSetter;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.algos.AlgoAngle;
import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.arithmetic.Function;
import org.geogebra.common.kernel.arithmetic.NumberValue;
import org.geogebra.common.kernel.geos.GProperty;
import org.geogebra.common.kernel.geos.GeoCurveCartesian;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.geos.GeoInputBox;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.geos.GeoPriorityComparator;
import org.geogebra.common.kernel.geos.GeoText;
import org.geogebra.common.kernel.geos.XMLBuilder;
import org.geogebra.common.kernel.kernelND.GeoDirectionND;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.kernel.kernelND.GeoLineND;
import org.geogebra.common.kernel.kernelND.GeoPlaneND;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.kernel.matrix.CoordMatrix;
import org.geogebra.common.kernel.matrix.CoordSys;
import org.geogebra.common.kernel.matrix.Coords;
import org.geogebra.common.main.App;
import org.geogebra.common.main.App.ExportType;
import org.geogebra.common.main.Feature;
import org.geogebra.common.main.GeoGebraColorConstants;
import org.geogebra.common.main.GuiManagerInterface;
import org.geogebra.common.main.ScreenReader;
import org.geogebra.common.main.SelectionManager;
import org.geogebra.common.main.settings.AbstractSettings;
import org.geogebra.common.main.settings.EuclidianSettings;
import org.geogebra.common.plugin.EuclidianStyleConstants;
import org.geogebra.common.plugin.Event;
import org.geogebra.common.plugin.EventType;
import org.geogebra.common.plugin.GeoClass;
import org.geogebra.common.util.AsyncOperation;
import org.geogebra.common.util.DoubleUtil;
import org.geogebra.common.util.MyMath;
import org.geogebra.common.util.NumberFormatAdapter;
import org.geogebra.common.util.StringUtil;
import org.geogebra.common.util.debug.Log;

import com.himamis.retex.editor.share.util.Unicode;

/**
 * View containing graphic representation of construction elements
 */
public abstract class EuclidianView implements EuclidianViewInterfaceCommon,
		SetLabels {
	/** says if the view has the mouse */
	protected boolean hasMouse;
	/** View other than EV1 and EV2 **/
	public static final int EVNO_GENERAL = 1001;
	/**
	 * 3D View TODO: probably needs changing when we support more than 2 views
	 **/
	public static final int EVNO_3D = -1;
	/** euclidian view number */
	protected int evNo = 1;
	/** old onscreen x-coord of origin for animate zoom */
	protected double xZeroOld;
	/** old onscreen y-coord of origin for animate zoom */
	protected double yZeroOld;
	/** old onscreen x-scale for animate zoom */
	protected double xScaleStart;
	/** old onscreen y-scale for animate zoom */
	protected double yScaleStart;
	private int mode = EuclidianConstants.MODE_MOVE;
	/** minimal width */
	public static final int MIN_WIDTH = 50;
	/** minimal height */
	protected static final int MIN_HEIGHT = 50;
	/** corner of export area */
	public static final String EXPORT1 = "Export_1"; // Points used to define
														// corners for export
														// (if they exist)
	/** corner of export area */
	public static final String EXPORT2 = "Export_2";

	/** pixel per centimeter (at 72dpi) */
	protected static final double PRINTER_PIXEL_PER_CM = 72.0 / 2.54;
	/** zoom factor of zoom mode */
	public static final double MODE_ZOOM_FACTOR = 1.5;
	/** zoom factor of mouse wheel */
	public static final double MOUSE_WHEEL_ZOOM_FACTOR = 1.1;
	/** standard pixels per unit */
	public static final double SCALE_STANDARD = 50;
	/** border in which axis numbers are not drawn */
	protected static final int SCREEN_BORDER = 10;

	// public static final double SCALE_MAX = 10000;
	// public static final double SCALE_MIN = 0.1;
	/** default screen x-coord of origin */
	public static final double XZERO_STANDARD = 215; // needs to be positive
	/** default screen y-coord of origin */
	public static final double YZERO_STANDARD = 315; // needs to be positive
	/**
	 * steps to set back to standard view
	 */
	protected static final int STANDARD_VIEW_STEPS = 15;

	// or use volatile image
	// protected int drawMode = DRAW_MODE_BACKGROUND_IMAGE;
	/** background image */
	protected GBufferedImage bgImage;

	/**
	 * g2d of bgImage: used for axis, grid, background images and object traces
	 */
	protected GGraphics2D bgGraphics;
	// zoom rectangle colors
	private static final GColor colZoomRectangle = GColor.newColor(200, 200,
			230);
	private static final GColor colZoomRectangleFill = GColor.newColor(200,
			200, 230, 50);

	// deletion square design
	protected static final GColor colDeletionSquare = GColor
			.newColor(128, 0, 0);
	protected static final GBasicStroke strokeDeletionSquare = AwtFactory
			.getPrototype().newBasicStroke(1.0f);
	protected GRectangle deletionRectangle;

	/** label outlines from 5.0.416.0 */
	protected static int[] LABEL_OUTLINES_FROM = new int[] { 5, 0, 416, 0 };

	/**
	 * bounding box
	 */
	protected BoundingBox<? extends GShape> boundingBox;
	private EuclidianBoundingBoxHandler hitHandler = EuclidianBoundingBoxHandler.UNDEFINED;

	// shape tools
	/**
	 * preview shape for rectangle
	 */
	protected GRectangle shapeRectangle;
	/**
	 * preview rectangle for mask tool
	 */
	private GRectangle maskPreview;
	/**
	 * preview shape for ellipse
	 */
	protected GEllipse2DDouble shapeEllipse;
	/**
	 * preview shape for line
	 */
	protected GLine2D shapeLine;
	/**
	 * preview shape for triangle/regular polygon/polygon
	 */
	protected GGeneralPath shapePolygon;
	// design for shapes
	/**
	 * fill color of shape (transparent)
	 */
	private final GColor shapeFillCol = GColor.newColor(192,
			192, 192, 0.0);
	/**
	 * object color of shape (black by default)
	 */
	private final GColor shapeObjCol = GColor.BLACK;
	/**
	 * stroke of shape
	 */
	private final GBasicStroke shapeStroke = AwtFactory
			.getPrototype().newBasicStroke(2.0f, GBasicStroke.CAP_BUTT,
					GBasicStroke.JOIN_MITER);
	private boolean isRounded = false;

	// colors: axes, grid, background
	GColor axesColor;
	private GColor gridColor;
	protected GRectangle selectionRectangle;
	/**
	 * default axes stroke
	 */
	static GBasicStroke defAxesStroke = AwtFactory.getPrototype()
			.newBasicStroke(1.0f, GBasicStroke.CAP_BUTT,
					GBasicStroke.JOIN_MITER);

	// changed from 1.8f (same as bold grid) Michael Borcherds 2008-04-12
	static GBasicStroke boldAxesStroke = AwtFactory.getPrototype()
			.newBasicStroke(2.0f, GBasicStroke.CAP_BUTT,
					GBasicStroke.JOIN_MITER);

	// axes and grid stroke
	GBasicStroke axesStroke;
	GBasicStroke tickStroke;
	private GBasicStroke gridStroke;

	/** minimal visible real world x */
	private double xmin;
	/** maximal visible real world x */
	private double xmax;
	/** minimal visible real world y */
	private double ymin;
	/** maximal visible real world y */
	private double ymax;
	/** possibly dynamic x min */
	protected NumberValue xminObject;
	/** possibly dynamic x max */
	protected NumberValue xmaxObject;
	/** possibly dynamic y min */
	protected NumberValue yminObject;
	/** possibly dynamic y max */
	protected NumberValue ymaxObject;

	private double invXscale;

	private double invYscale;

	protected double xZero;

	protected double yZero;

	private double xscale;

	private double yscale;

	// private double scaleRatio = 1.0;
	/** print scale ratio */
	protected double printingScale;

	// Map (geo, drawable) for GeoElements and Drawables
	private final HashMap<GeoElement, DrawableND> drawableMap = new HashMap<>(
			500);

	private ArrayList<GeoPointND> stickyPointList = new ArrayList<>();

	public DrawableList allDrawableList;

	// on add: change resetLists()
	/** list of background images */
	private DrawableList bgImageList;

	protected boolean[] piAxisUnit;

	private int[] axesTickStyles;

	// for axes labeling with numbers
	protected boolean[] automaticAxesNumberingDistances = { true, true };

	protected double[] axesNumberingDistances;
	protected GeoNumberValue[] axesDistanceObjects;
	ArrayList<Integer> axesLabelsPositionsX = new ArrayList<>();
	double yLabelMaxWidthPos = 0;
	double yLabelMaxWidthNeg = 0;
	double xLabelHeights = 0;

	// axis control vars
	protected double[] axisCross;
	protected boolean[] positiveAxes;
	protected boolean[] drawBorderAxes;

	private boolean needsAllDrawablesUpdate;
	protected boolean batchUpdate;
	/** kernel */
	protected Kernel kernel;

	private final static int[] lineTypes = {
			EuclidianStyleConstants.LINE_TYPE_FULL,
			EuclidianStyleConstants.LINE_TYPE_DASHED_LONG,
			EuclidianStyleConstants.LINE_TYPE_DASHED_SHORT,
			EuclidianStyleConstants.LINE_TYPE_DOTTED,
			EuclidianStyleConstants.LINE_TYPE_DASHED_DOTTED,
			EuclidianStyleConstants.LINE_TYPE_POINTWISE };

	private final static int[] pointStyles = {
			EuclidianStyleConstants.POINT_STYLE_DOT,
			EuclidianStyleConstants.POINT_STYLE_CROSS,
			EuclidianStyleConstants.POINT_STYLE_CIRCLE,
			EuclidianStyleConstants.POINT_STYLE_PLUS,
			EuclidianStyleConstants.POINT_STYLE_FILLED_DIAMOND,
			EuclidianStyleConstants.POINT_STYLE_EMPTY_DIAMOND,
			EuclidianStyleConstants.POINT_STYLE_TRIANGLE_NORTH,
			EuclidianStyleConstants.POINT_STYLE_TRIANGLE_SOUTH,
			EuclidianStyleConstants.POINT_STYLE_TRIANGLE_EAST,
			EuclidianStyleConstants.POINT_STYLE_TRIANGLE_WEST,
			EuclidianStyleConstants.POINT_STYLE_NO_OUTLINE };

	private final static int[] axesStyles = { EuclidianStyleConstants.NO_AXES,
			EuclidianStyleConstants.AXES_LINE_TYPE_ARROW,
			EuclidianStyleConstants.AXES_LINE_TYPE_TWO_ARROWS,
			EuclidianStyleConstants.AXES_LINE_TYPE_FULL };

	// end
	private int fontSize;
	private GAffineTransform coordTransform = AwtFactory.getPrototype()
			.newAffineTransform();
	/** tick interval for axes */
	protected double[] axesTickInterval;
	/** number formats for axes */
	protected NumberFormatAdapter[] axesNumberFormat;
	/** Flags for axis visibility */
	protected boolean[] showAxes = { true, true };
	/** Flags for logarithmic axes */
	protected boolean[] logAxes = { false, false };

	// distances between grid lines
	protected boolean automaticGridDistance = true;
	/** distance between gridlines */
	protected double[] gridDistances;

	private int gridLineStyle;
	/** bit mask for axis style, see EuclidianStyleConstants.AXES_BOLD */
	int axesLineType;
	/** whether grid is bold */
	protected boolean gridIsBold = false;
	/** tooltip mode in this view */
	protected int tooltipsInThisView = EuclidianStyleConstants.TOOLTIPS_AUTOMATIC;

	// Michael Borcherds 2008-04-28
	/** for toggle buttons */
	public static final int GRID_NOT_SHOWN = -1;
	/** cartesian grid */
	public static final int GRID_CARTESIAN = 0;
	/** isometric grid */
	public static final int GRID_ISOMETRIC = 1;
	/** polar grid */
	public static final int GRID_POLAR = 2;
	/** cartesian grid with minor gridlines */
	public static final int GRID_CARTESIAN_WITH_SUBGRID = 3;

	private int gridType = GRID_CARTESIAN;

	// FONTS
	private GFont fontPoint;

	private GFont fontCoords;

	/** number format for print scale */
	protected NumberFormatAdapter printScaleNF;

	/** true if grid is displayed */
	protected boolean showGrid = false;

	/** whether mouse coords are shown see also allowShowMouseCoords */
	protected boolean showMouseCoords = false;

	/**
	 * whether to allow onscreen mouse coords.
	 * 
	 * set to false because it was set to false in Desktop anyway
	 */
	protected boolean allowShowMouseCoords = false;
	/** whether axes ratio should be visible (true while axis zooming) */
	protected boolean showAxesRatio = false;
	/** true if animation button need highlighting */
	protected boolean highlightAnimationButtons = false;
	/** point capturing mode */
	protected int pointCapturingMode;
	/** show coords of view corners */
	private boolean showAxesCornerCoords = true;

	private boolean animationButtonSelected = false;
	/**
	 * Whether axes numbers should be shown
	 */
	protected boolean[] showAxesNumbers;

	/**
	 * Labels fo xAxis and yAxis
	 */
	protected String[] axesLabels;

	/**
	 * Styles (GFont.ITALIC, GFont.BOLD)
	 */
	protected int[] axesLabelsStyle;
	/**
	 * Units for axes
	 */
	protected String[] axesUnitLabels;

	private Previewable previewDrawable;
	/** true if painting this for the first time */
	protected boolean firstPaint = true;
	/** application */
	protected App app;

	private EuclidianSettings settings;

	// member variables
	/** controller */
	protected EuclidianController euclidianController;

	// ggb3D 2009-02-05

	private GEllipse2DDouble circle = AwtFactory.getPrototype()
			.newEllipse2DDouble(); // polar
									// grid
									// circles
	private GLine2D tempLine = AwtFactory.getPrototype().newLine2D();
	private GeoElement[] previewFromInputBarGeos;
	private ArrayList<GeoElement> geosWaiting = new ArrayList<>();
	private boolean labelHitNeedsRefresh = true;
	private GeoElement labelHitLastGeo = null;
	/** reIniting is used by GeoGebraWeb */
	protected boolean reIniting = false;
	private boolean backgroundIsUpdating = false;

	private Hits tempArrayList = new Hits();

	private CoordSystemAnimation zoomer;
	private CoordSystemAnimation axesRatioZoomer;
	private CoordSystemAnimation zoomerRW;
	private CoordSystemAnimation mover;

	private OptionsEuclidian optionPanel = null;
	private DrawDropDownList openedComboBox = null;

	protected ViewTextField viewTextField;
	private EuclidianStyleBar dynamicStyleBar;

	/**
	 * Get styleBar
	 */
	protected org.geogebra.common.euclidian.EuclidianStyleBar styleBar;
	private DrawGrid drawGrid;
	private DrawAxis da;

	// keep same center after layout resize
	private boolean keepCenter = false;

	private boolean screenChanged = false;

	private boolean tracing = false;

	protected EuclidianViewCompanion companion;

	private boolean updatingBounds = false;
	private boolean coordSystemTranslatedByAnimation;
	/**
	 * axes ratio if locked; -1 otherwise
	 */
	protected double lockedAxesRatio = -1;
	private boolean updateBackgroundOnNextRepaint;
	
	private List<GeoElement> specPoints;
	private GRectangle exportFrame;
	private GRectangle tempFrame;
	private GPoint2D[] tmpClipPoints;
	private NumberFormatAdapter[] axesNumberFormatsNormal = new NumberFormatAdapter[16];
	private NumberFormatAdapter[] axesNumberFormatsExponential = new NumberFormatAdapter[16];

	private DrawBackground drawBg = null;
	private final HitDetector hitDetector;
	private boolean isResetIconSelected = false;
	private BoundingBox<? extends GShape> focusedGroupGeoBoundingBox;

	protected SymbolicEditor symbolicEditor = null;

	/** @return line types */
	public static final Integer[] getLineTypes() {
		Integer[] ret = new Integer[lineTypes.length];

		for (int i = 0; i < lineTypes.length; i++) {
			ret[i] = lineTypes[i];
		}

		return ret;
	}

	public static int getLineType(int i) {
		return lineTypes[i];
	}

	public static int getLineTypeLength() {
		return lineTypes.length;
	}

	public static int getAxesStyle(int i) {
		return axesStyles[i];
	}

	public static int getPointStyleLength() {
		return pointStyles.length;
	}

	/** @return point styles */
	public static final Integer[] getPointStyles() {
		Integer[] ret = new Integer[pointStyles.length];

		for (int i = 0; i < pointStyles.length; i++) {
			ret[i] = pointStyles[i];
		}

		return ret;
	}

	public static int getPointStyle(int i) {
		return pointStyles[i];
	}

	/**
	 * Default constructor needed for Android Bean
	 */
	public EuclidianView() {
		hitDetector = new HitDetector(this);
	}

	/**
	 * @param ec
	 *            controller
	 * @param viewNo
	 *            view number
	 * @param settings
	 *            settings
	 */
	public EuclidianView(EuclidianController ec, int viewNo,
		EuclidianSettings settings) {
		this();
		init(ec, viewNo, settings);
	}

	/**
	 * @param ec
	 *            controller
	 * @param viewNo
	 *            view number can be 1, 2 or EVNO_GENERAL
	 * @param settings
	 *            settings
	 */
	protected void init(EuclidianController ec, int viewNo,
			EuclidianSettings settings) {

		// 1, 2 or EVNO_GENERAL
		setEuclidianViewNo(viewNo);

		companion = newEuclidianViewCompanion();

		this.euclidianController = ec;
		this.kernel = ec.getKernel();
		this.app = kernel.getApplication();
		this.settings = settings;

		GeoPriorityComparator cmp = app.getGeoPriorityComparator();
		allDrawableList = new DrawableList(cmp);
		bgImageList = new DrawableList(cmp);

		initAxesValues();

		// no repaint
		if (kernel.getConstruction() != null) {
			kernel.getConstruction().setIgnoringNewTypes(true);
			xminObject = new GeoNumeric(kernel.getConstruction());
			xmaxObject = new GeoNumeric(kernel.getConstruction());
			yminObject = new GeoNumeric(kernel.getConstruction());
			ymaxObject = new GeoNumeric(kernel.getConstruction());
			kernel.getConstruction().setIgnoringNewTypes(false);
		}
		// ggb3D 2009-02-05
		hitDetector.reset();

		printScaleNF = FormatFactory.getPrototype().getNumberFormat("#.#####",
				5);
        setXscale(SCALE_STANDARD);
        setYscale(SCALE_STANDARD);
	}

	/**
	 * 
	 * @return new view companion attached to this
	 */
	protected EuclidianViewCompanion newEuclidianViewCompanion() {
		return new EuclidianViewCompanion(this);
	}

	/**
	 * 
	 * @return companion
	 */
	public EuclidianViewCompanion getCompanion() {
		return companion;
	}

	/**
	 * init axes values
	 */
	protected void initAxesValues() {
		axesNumberFormat = new NumberFormatAdapter[2];
		showAxesNumbers = new boolean[] { true, true };
		axesLabels = new String[] { null, null };
		axesLabelsStyle = new int[] { GFont.PLAIN, GFont.PLAIN };
		axesUnitLabels = new String[] { null, null };
		axesTickStyles = new int[] {
				EuclidianStyleConstants.AXES_TICK_STYLE_MAJOR,
				EuclidianStyleConstants.AXES_TICK_STYLE_MAJOR };
		automaticAxesNumberingDistances = new boolean[] { true, true };
		axesNumberingDistances = new double[] { 2, 2 };
		axesDistanceObjects = new GeoNumberValue[] { null, null };
		drawBorderAxes = new boolean[] { false, false };
		axisCross = new double[] { 0, 0 };
		positiveAxes = new boolean[] { false, false };
		piAxisUnit = new boolean[] { false, false };
		gridDistances = new double[] { 2, 2, Math.PI / 6 };
		axesTickInterval = new double[] { 1, 1 };

	}

	@Override
	public void setAxesColor(GColor axesColor) {
		if (axesColor != null) {
			this.axesColor = axesColor;
		}
	}

	/**
	 * Sets the coord system to default
	 * 
	 * @param repaint
	 *            whether to repaint afterwards
	 */
	protected void setStandardCoordSystem(boolean repaint) {
		setCoordSystem(getXZeroStandard(), getYZeroStandard(), SCALE_STANDARD,
				SCALE_STANDARD, repaint);
	}

	/**
	 * Attach this view to kernel and add all objects created so far
	 */
	public void attachView() {
		companion.attachView();
	}

	/**
	 * Returns point capturing mode.
	 */
	@Override
	public int getPointCapturingMode() {
		if (settings != null) {
			return settings.getPointCapturingMode();
		}
		return pointCapturingMode;
	}

	/**
	 * Set capturing of points to the grid.
	 */
	@Override
	public void setPointCapturing(int mode) {
		if (settings != null) {
			settings.setPointCapturing(mode);
		} else {
			pointCapturingMode = mode;
		}
	}

	@Override
	public int getMode() {
		return mode;
	}

	@Override
	public void setMode(int mode, ModeSetter m) {
		if (mode == this.mode && mode != EuclidianConstants.MODE_IMAGE) {
			return;
		}
		this.mode = mode;
		initCursor();
		getEuclidianController().clearJustCreatedGeos();
		getEuclidianController().setMode(mode, m);
		if (clearRectangle(mode)) {
			setSelectionRectangle(null);
			if (hasDynamicStyleBar()) {
				dynamicStyleBar.setVisible(false);
			}
		}
		setStyleBarMode(mode);

		getCompanion().setMode(mode, m);
	}

	/** @return kernel */
	public Kernel getKernel() {
		return kernel;
	}

	/**
	 * whether to clear selection rectangle when mode selected
	 */
	final private static boolean clearRectangle(int mode) {
		switch (mode) {
		// case EuclidianConstants.MODE_PENCIL:
		case EuclidianConstants.MODE_PEN:
			return true; // changed
		case EuclidianConstants.MODE_MIRROR_AT_LINE:
			return false;
		case EuclidianConstants.MODE_MIRROR_AT_POINT:
			return false;
		case EuclidianConstants.MODE_ROTATE_BY_ANGLE:
			return false;
		case EuclidianConstants.MODE_TRANSLATE_BY_VECTOR:
			return false;
		case EuclidianConstants.MODE_DILATE_FROM_POINT:
			return false;
		default:
			return true;
		}
	}

	/**
	 * @return the xminObject
	 */
	@Override
	public GeoNumeric getXminObject() {
		return (GeoNumeric) xminObject;
	}

	@Override
	public void updateBoundObjects() {
		if (isZoomable() && xminObject != null) {
			((GeoNumeric) xminObject).setValue(getXmin());
			((GeoNumeric) xmaxObject).setValue(getXmax());
			((GeoNumeric) yminObject).setValue(getYmin());
			((GeoNumeric) ymaxObject).setValue(getYmax());
		}
	}

	/**
	 * returns true if the axes ratio is 1
	 * 
	 * @return true if the axes ratio is 1
	 */
	@Override
	public boolean isLockedAxesRatio() {
		return lockedAxesRatio > 0 || (gridType == GRID_POLAR);
	}

	/**
	 * Set unit axes ratio to 1
	 * 
	 * @param flag
	 *            true to set to 1, false to allow user
	 */
	public void setLockedAxesRatio(double flag) {
		lockedAxesRatio = flag;
		if (flag > 0) {
			updateBounds(true, true);
		}
	}

	@Override
	public void updateBounds(boolean updateDrawables, boolean updateSettings) {
		if (updatingBounds) {
			return;
		}

		updatingBounds = true;
		for (int i = 0; i < axesDistanceObjects.length; i++) {
			if (axesDistanceObjects[i] != null
					&& axesDistanceObjects[i].getDouble() > 0) {
				axesNumberingDistances[i] = axesDistanceObjects[i].getDouble();
			}
		}
		if (xminObject == null) {
			return;
		}
		double xmin2 = xminObject.getDouble();
		double xmax2 = xmaxObject.getDouble();
		double ymin2 = yminObject.getDouble();
		double ymax2 = ymaxObject.getDouble();
		if (isLockedAxesRatio() && (getHeight() > 0) && (getWidth() > 0)) {
			double ratio = gridType == GRID_POLAR ? 1 : lockedAxesRatio;
			double newWidth = ratio * ((ymax2 - ymin2) * getWidth())
					/ (getHeight() + 0.0);
			double newHeight = 1 / ratio * ((xmax2 - xmin2) * getHeight())
					/ (getWidth() + 0.0);

			if ((xmax2 - xmin2) < newWidth) {
				double c = (xmin2 + xmax2) / 2;
				xmin2 = c - (newWidth / 2);
				xmax2 = c + (newWidth / 2);
			} else {
				double c = (ymin2 + ymax2) / 2;
				ymin2 = c - (newHeight / 2);
				ymax2 = c + (newHeight / 2);
			}
		}
		if (((xmax2 - xmin2) > Kernel.MAX_PRECISION)
				&& ((ymax2 - ymin2) > Kernel.MAX_PRECISION)) {
			xmax = xmax2;
			xmin = xmin2;
			ymin = ymin2;
			ymax = ymax2;

			setXscale(getWidth() / (xmax2 - xmin2));
			setYscale(getHeight() / (ymax2 - ymin2));
			xZero = -xscale * xmin2;
			yZero = yscale * ymax2;
			if (updateSettings && settings != null) {
				settings.setCoordSystem(xZero, yZero, xscale, yscale, false);
			}
			setAxesIntervals(getXscale(), 0);
			setAxesIntervals(getYscale(), 1);
			calcPrintingScale();
			if (isLockedAxesRatio()) {
				this.updateBoundObjects();
			}

			app.dispatchEvent(new Event(EventType.VIEW_CHANGED_2D)
					.setJsonArgument(getCoordinates()));
		}
		// tell kernel
		if (evNo != EVNO_GENERAL) {
			kernel.setEuclidianViewBounds(evNo, getXmin(), getXmax(),
					getYmin(), getYmax(), getXscale(), getYscale());
		}

		// needed for images eg in zoomed EV2
		setCoordTransformIfNeeded();

		// tell option panel
		if (optionPanel != null) {
			optionPanel.updateBounds();
		}

		if (updateDrawables) {
			updateAllDrawablesForView(true);
			invalidateBackground();
		}

		updatingBounds = false;
	}

	@Override
	public boolean isZoomable() {
		if (!GeoNumeric.isChangeable(xminObject)) {
			return false;
		}
		if (!GeoNumeric.isChangeable(xmaxObject)) {
			return false;
		}
		if (!GeoNumeric.isChangeable(yminObject)) {
			return false;
		}
		if (!GeoNumeric.isChangeable(ymaxObject)) {
			return false;
		}
		return true;
	}

	/**
	 * @param xminObjectNew
	 *            the xminObject to set
	 */
	@Override
	public void setXminObject(NumberValue xminObjectNew) {
		if (xminObject != null) {
			((GeoNumeric) xminObject).removeEVSizeListener(this);
		}
		if (xminObjectNew == null && kernel.getConstruction() != null) {
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
	@Override
	public GeoNumeric getXmaxObject() {
		return (GeoNumeric) xmaxObject;
	}

	/**
	 * @param xmaxObjectNew
	 *            the xmaxObject to set
	 */
	@Override
	public void setXmaxObject(NumberValue xmaxObjectNew) {
		if (xmaxObject != null) {
			((GeoNumeric) xmaxObject).removeEVSizeListener(this);
		}
		if (xmaxObjectNew == null && kernel.getConstruction() != null) {
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
	@Override
	public GeoNumeric getYminObject() {
		return (GeoNumeric) yminObject;
	}

	/**
	 * @param yminObjectNew
	 *            the yminObject to set
	 */
	@Override
	public void setYminObject(NumberValue yminObjectNew) {
		if (yminObject != null) {
			((GeoNumeric) yminObject).removeEVSizeListener(this);
		}
		if (yminObjectNew == null && kernel.getConstruction() != null) {
			this.yminObject = new GeoNumeric(kernel.getConstruction());
			updateBoundObjects();
		} else {
			this.yminObject = yminObjectNew;
		}
		setSizeListeners();
	}

	/**
	 * @return handler that was hit
	 */
	public EuclidianBoundingBoxHandler getHitHandler() {
		return hitHandler;
	}

	/**
	 * @param hitHandler
	 *            - handler that was hit
	 */
	public void setHitHandler(EuclidianBoundingBoxHandler hitHandler) {
		this.hitHandler = hitHandler;
	}

	private void setSizeListeners() {
		if (xminObject != null) {
			((GeoNumeric) xminObject).addEVSizeListener(this);
			((GeoNumeric) yminObject).addEVSizeListener(this);
			((GeoNumeric) xmaxObject).addEVSizeListener(this);
			((GeoNumeric) ymaxObject).addEVSizeListener(this);
		}
	}

	/**
	 * convert real world coordinate x to screen coordinate x
	 * 
	 * @param xRW
	 *            real world x coord
	 * @return screen equivalent of real world x-coord
	 */
	@Override
	final public int toScreenCoordX(double xRW) {
		return (int) Math.round(getXZero() + xRW * getXscale());
	}

	/**
	 * convert real world coordinate y to screen coordinate y
	 * 
	 * @param yRW
	 *            real world y coord
	 * @return screen equivalent of real world y-coord
	 */
	@Override
	final public int toScreenCoordY(double yRW) {
		return (int) Math.round(getYZero() - (yRW * getYscale()));
	}

	/**
	 * convert real world coordinate x to screen coordinate x
	 * 
	 * @param xRW
	 *            real world x-coord
	 * @return screen equivalent of real world x-coord as double
	 */
	@Override
	final public double toScreenCoordXd(double xRW) {
		if (getXaxisLog()) {
			return getWidth() * (Math.log10(xRW) - Math.log10(xmin))
					/ (Math.log10(xmax) - Math.log10(xmin));
		}
		return getXZero() + (xRW * getXscale());
	}

	/**
	 * convert real world coordinate y to screen coordinate y
	 * 
	 * @param yRW
	 *            real world y-coord
	 * @return screen equivalent of real world y-coord
	 */
	@Override
	final public double toScreenCoordYd(double yRW) {
		if (getYaxisLog()) {
			return getHeight()
					* (1 - (Math.log10(yRW) - Math.log10(ymin))
							/ (Math.log10(ymax) - Math.log10(ymin)));
		}
		return getYZero() - (yRW * getYscale());
	}

	/**
	 * convert real world coordinate x to screen coordinate x. If the value is
	 * outside the screen it is clipped to one pixel outside.
	 * 
	 * @param xRW
	 *            real world x coordinate
	 * @return real world coordinate x to screen coordinate x clipped to screen
	 */
	final public int toClippedScreenCoordX(double xRW) {
		if (xRW > getXmax()) {
			return getWidth() + 1;
		} else if (xRW < getXmin()) {
			return -1;
		} else {
			return toScreenCoordX(xRW);
		}
	}

	/** remembers the origins values (xzero, ...) */
	@Override
	public void rememberOrigins() {
		xZeroOld = getXZero();
		yZeroOld = getYZero();
		xScaleStart = getXscale();
		yScaleStart = getYscale();
	}

	/**
	 * convert real world coordinate y to screen coordinate y. If the value is
	 * outside the screen it is clipped to one pixel outside.
	 * 
	 * @param yRW
	 *            real world y coordinate
	 * @return real world coordinate y to screen coordinate x clipped to screen
	 */
	final public int toClippedScreenCoordY(double yRW) {
		if (yRW > getYmax()) {
			return -1;
		} else if (yRW < getYmin()) {
			return getHeight() + 1;
		} else {
			return toScreenCoordY(yRW);
		}
	}

	/**
	 * Converts real world coordinates to screen coordinates. Note that
	 * MAX_SCREEN_COORD is used to avoid huge coordinates.
	 * 
	 * @param inOut
	 *            input and output array with x and y coords
	 * @return if resulting coords are on screen
	 */
	final public boolean toScreenCoords(double[] inOut) {
		// convert to screen coords

		if (getXaxisLog()) {
			inOut[0] = getWidth() * (Math.log10(inOut[0]) - Math.log10(xmin))
					/ (Math.log10(xmax) - Math.log10(xmin));
		} else {
			inOut[0] = getXZero() + (inOut[0] * getXscale());
		}

		if (getYaxisLog()) {
			inOut[1] = getHeight()
					* (1 - (Math.log10(inOut[1]) - Math.log10(ymin))
							/ (Math.log10(ymax) - Math.log10(ymin)));
		} else {
			inOut[1] = getYZero() - (inOut[1] * getYscale());
		}

		// check if (x, y) is on screen
		boolean onScreen = true;

		// note that java drawing has problems for huge coord values
		// so we use FAR_OFF_SCREEN for clipping
		if (Double.isNaN(inOut[0]) || Double.isInfinite(inOut[0])) {
			inOut[0] = Double.NaN;
			onScreen = false;
		} else if (inOut[0] < 0) { // x left of screen
			// inOut[0] = Math.max(inOut[0], -MAX_SCREEN_COORD);
			onScreen = false;
		} else if (inOut[0] > getWidth()) { // x right of screen
			// inOut[0] = Math.min(inOut[0], width + MAX_SCREEN_COORD);
			onScreen = false;
		}

		// y undefined
		if (Double.isNaN(inOut[1]) || Double.isInfinite(inOut[1])) {
			inOut[1] = Double.NaN;
			onScreen = false;
		} else if (inOut[1] < 0) { // y above screen
			// inOut[1] = Math.max(inOut[1], -MAX_SCREEN_COORD);
			onScreen = false;
		} else if (inOut[1] > getHeight()) { // y below screen
			// inOut[1] = Math.min(inOut[1], height + MAX_SCREEN_COORD);
			onScreen = false;
		}

		return onScreen;
	}

	/**
	 * Checks if (screen) coords are on screen.
	 * 
	 * @param coords
	 *            coords
	 * @return true if coords are on screen
	 */
	final public boolean isOnScreen(double[] coords) {
		return (coords[0] >= 0) && (coords[0] <= getWidth())
				&& (coords[1] >= 0) && (coords[1] <= getHeight());
	}

	/**
	 * Checks if (real world) coords are on view.
	 * 
	 * @param coords
	 *            coords
	 * @return true if coords are on view
	 */
	public boolean isOnView(double[] coords) {
		return (coords[0] >= getXmin()) && (coords[0] <= getXmax())
				&& (coords[1] >= getYmin()) && (coords[1] <= getYmax());
	}

	/**
	 * 
	 * @param p1
	 *            first point
	 * @param p2
	 *            second point
	 * @return (p2-p1) vector in screen coordinates
	 */
	public double[] getOnScreenDiff(double[] p1, double[] p2) {
		double[] ret = new double[2];
		ret[0] = (p2[0] - p1[0]) * getXscale();
		ret[1] = (p2[1] - p1[1]) * getYscale();
		return ret;
	}

	/**
	 * Performs a quick test whether the segment p1 to p2 is off view.
	 * 
	 * @param p1
	 *            first point
	 * @param p2
	 *            second point
	 * @return true if segment is on / close to view
	 */
	public boolean isSegmentOffView(double[] p1, double[] p2) {
		double tolerance = EuclidianStatic.CLIP_DISTANCE / getYscale();

		// bottom
		if (DoubleUtil.isGreater(getYmin(), p1[1], tolerance)
				&& DoubleUtil.isGreater(getYmin(), p2[1], tolerance)) {
			return true;
		}

		// top
		if (DoubleUtil.isGreater(p1[1], getYmax(), tolerance)
				&& DoubleUtil.isGreater(p2[1], getYmax(), tolerance)) {
			return true;
		}

		tolerance = EuclidianStatic.CLIP_DISTANCE / getXscale();

		// left
		if (DoubleUtil.isGreater(getXmin(), p1[0], tolerance)
				&& DoubleUtil.isGreater(getXmin(), p2[0], tolerance)) {
			return true;
		}

		// right
		if (DoubleUtil.isGreater(p1[0], getXmax(), tolerance)
				&& DoubleUtil.isGreater(p2[0], getXmax(), tolerance)) {
			return true;
		}

		// close to screen
		return false;
	}

	/**
	 * convert screen coordinate x to real world coordinate x
	 * 
	 * @param x
	 *            screen coord
	 * @return real world equivalent of screen x-coord
	 */
	@Override
	final public double toRealWorldCoordX(double x) {
		return (x - getXZero()) * getInvXscale();
	}

	/**
	 * convert screen coordinate y to real world coordinate y
	 * 
	 * @param y
	 *            screen coord
	 * @return real world equivalent of screen y-coord
	 */
	@Override
	final public double toRealWorldCoordY(double y) {
		return (getYZero() - y) * getInvYscale();
	}

	/**
	 * Sets real world coord system, where zero point has screen coords (xZero,
	 * yZero) and one unit is xscale pixels wide on the x-Axis and yscale pixels
	 * heigh on the y-Axis.
	 * 
	 * Also updates settings *before* all the algos that might need them are
	 * updated
	 */
	@Override
	final public void setCoordSystem(double xZero, double yZero, double xscale,
			double yscale) {
		if (settings != null) {
			settings.setCoordSystem(xZero, yZero, xscale, yscale, false);
		}
		setCoordSystem(xZero, yZero, xscale, yscale, true);
	}

	protected Map<String, Object> getCoordinates() {
		Map<String, Object> coordinates = new HashMap<>();
		coordinates.put("xZero", getXZero());
		coordinates.put("yZero", getYZero());
		coordinates.put("scale", getXscale());
		coordinates.put("yscale", getYscale());
		coordinates.put("viewNo", getEuclidianViewNo());

		return coordinates;
	}

	/** Sets coord system from mouse move */
	@Override
	public void translateCoordSystemInPixels(int dx, int dy, int dz) {
		setCoordSystem(xZeroOld + dx, yZeroOld + dy, getXscale(), getYscale());
	}

	/**
	 * @param dy
	 *            y-movement in pixels
	 */
	public void translateCoordSystemForAnimation(int dy) {
		coordSystemTranslatedByAnimation = true;
		setCoordSystem(xZero, yZero + dy, getXscale(), getYscale());
		coordSystemTranslatedByAnimation = false;
	}

	/**
	 * Sets coord system from mouse move
	 * 
	 * @param dx
	 *            x-displacement
	 * @param dy
	 *            y-displacement
	 * @param mode
	 *            app mode
	 */
	public void setCoordSystemFromMouseMove(int dx, int dy, int mode) {
		translateCoordSystemInPixels(dx, dy, 0);
	}

	@Override
	public void pageUpDownTranslateCoordSystem(int height) {
		translateCoordSystemInPixels(0, height, 0);
	}

	/**
	 * Sets real world coord system using min and max values for both axes in
	 * real world values.
	 */
	@Override
	final public void setRealWorldCoordSystem(double xmin2, double xmax2,
			double ymin2, double ymax2) {
		double calcXscale = getWidth() / (xmax2 - xmin2);
		if (getXaxisLog()) {
			calcXscale = getWidth() / (xmax2 - xmin2);
		}
		double calcYscale = getHeight() / (ymax2 - ymin2);
		double calcXzero = -calcXscale * xmin2;
		double calcYzero = calcYscale * ymax2;
		setCoordSystem(calcXzero, calcYzero, calcXscale, calcYscale);
	}

	/**
	 * @param xZero
	 *            new x coord of origin
	 * @param yZero
	 *            new y coord of origin
	 * @param xscale
	 *            x scale
	 * @param yscale
	 *            y scale
	 * @param repaint
	 *            true to repaint
	 */
	public final void setCoordSystem(double xZero, double yZero, double xscale,
			double yscale, boolean repaint) {
		if (Double.isNaN(xscale) || (xscale < Kernel.MAX_DOUBLE_PRECISION)
				|| (xscale > Kernel.INV_MAX_DOUBLE_PRECISION)) {
			return;
		}
		if (Double.isNaN(yscale) || (yscale < Kernel.MAX_DOUBLE_PRECISION)
				|| (yscale > Kernel.INV_MAX_DOUBLE_PRECISION)) {
			return;
		}
		this.xZero = xZero;
		this.yZero = yZero;
		this.setXscale(xscale);
		this.setYscale(yscale);
		// setScaleRatio(yscale / xscale);
		setCoordTransformIfNeeded();
		// real world values
		setXYMinMaxForSetCoordSystem();
		setRealWorldBounds();
        onCoordSystemChangedFromSetCoordSystem();
		// if (drawMode == DRAW_MODE_BACKGROUND_IMAGE)
		if (repaint) {
			invalidateBackground();
			updateAllDrawablesForView(repaint);

			// needed so that eg Corner[2,1] updates properly on zoom / pan
			if (getApplication().hasEuclidianView2(1)) {
				kernel.notifyRepaint();
				// app.updateStatusLabelAxesRatio();
			}
		}
		// tells app that set coord system occured during user interaction
		if (!app.getKernel().getLoadingMode()) {
			app.setCoordSystemOccured();
		}
	}

	/**
	 * Make sure background gets updated
	 */
	public void invalidateBackground() {
		updateBackgroundOnNextRepaint = true;
	}

    /**
     * notify controller that coordinate system has changed from setCoordSystem()
     */
	protected void onCoordSystemChangedFromSetCoordSystem() {
        getEuclidianController().onCoordSystemChanged();
    }

	/**
	 * If the background was marked for update (axes changed), repaint it
	 */
	public void updateBackgroundIfNecessary() {
		if (updateBackgroundOnNextRepaint) {
			updateBackgroundImage();
		}
		updateBackgroundOnNextRepaint = false;
	}

	/**
	 * @return the ymaxObject
	 */
	@Override
	public GeoNumeric getYmaxObject() {
		return (GeoNumeric) ymaxObject;
	}

	/**
	 * This is only needed for second or above euclidian views
	 * 
	 * @param evNo
	 *            euclidian view number 1, 2 or EVNO_GENERAL
	 */
	public void setEuclidianViewNo(int evNo) {
		if (evNo >= 2) {
			this.evNo = evNo;
		}
	}

	@Override
	public int getEuclidianViewNo() {
		return evNo;
	}

	/**
	 * @param ymaxObjectNew
	 *            the ymaxObject to set
	 */
	@Override
	public void setYmaxObject(NumberValue ymaxObjectNew) {
		if (ymaxObject != null) {
			((GeoNumeric) ymaxObject).removeEVSizeListener(this);
		}
		if (ymaxObjectNew == null && kernel.getConstruction() != null) {
			this.ymaxObject = new GeoNumeric(kernel.getConstruction());
			updateBoundObjects();
		} else {
			this.ymaxObject = ymaxObjectNew;
		}
		setSizeListeners();
	}

	private void setXscale(double xscale) {
		this.xscale = xscale;
		this.invXscale = 1 / xscale;
	}

	private void setYscale(double yscale) {
		this.yscale = yscale;
		this.invYscale = 1 / yscale;
	}

	/**
	 * @param fontSize
	 *            default font size
	 */
	protected void setFontSize(int fontSize) {
		this.fontSize = fontSize;
	}

	/**
	 * Returns x coordinate of axes origin.
	 */
	@Override
	public double getXZero() {
		return xZero;
	}

	/**
	 * Returns y coordinate of axes origin.
	 */
	@Override
	public double getYZero() {
		return yZero;
	}

	/**
	 * @return string "x : y= _ : 1" or "x : y= 1 : _"
	 */
	protected String getXYscaleRatioString() {
		StringBuilder ratioSb = new StringBuilder();
		ratioSb.append("x : y = ");
		if (getXscale() >= getYscale()) {
			ratioSb.append("1 : ");
			ratioSb.append(printScaleNF.format(getXscale() / getYscale()));
		} else {
			ratioSb.append(printScaleNF.format(getYscale() / getXscale()));
			ratioSb.append(" : 1");
		}
		ratioSb.append(' ');
		return ratioSb.toString();
	}

	/**
	 * Returns xscale of this view. The scale is the number of pixels in screen
	 * space that represent one unit in user space.
	 */
	@Override
	public double getXscale() {
		return xscale;
	}

	/**
	 * Returns the yscale of this view. The scale is the number of pixels in
	 * screen space that represent one unit in user space.
	 */
	@Override
	public double getYscale() {
		return yscale;
	}

	@Override
	public double getInvXscale() {
		return invXscale;
	}

	@Override
	public double getInvYscale() {
		return invYscale;
	}

	@Override
	public int getViewWidth() {
		return getWidth();
	}

	@Override
	public int getViewHeight() {
		return getHeight();
	}

	/**
	 * Returns the ratio yscale / xscale of this view. The scale is the number
	 * of pixels in screen space that represent one unit in user space.
	 * 
	 * @return yscale / xscale ratio
	 */
	public double getScaleRatio() {
		return getYscale() / getXscale();
	}

	/**
	 * @return Returns the xmax.
	 */
	@Override
	public double getXmax() {
		return xmax;
	}

	/**
	 * @return Returns the xmin.
	 */
	@Override
	public double getXmin() {
		return xmin;
	}

	/**
	 * @return Returns the ymax.
	 */
	@Override
	public double getYmax() {
		return ymax;
	}

	/**
	 * @return Returns the ymin.
	 */
	@Override
	public double getYmin() {
		return ymin;
	}

	/**
	 * Returns grid type.
	 */
	@Override
	final public int getGridType() {
		return gridType;
	}

	/**
	 * Set grid type.
	 */
	@Override
	public void setGridType(int type) {
		gridType = type;
	}

	/**
	 * TODO check whether this is what we want
	 * 
	 * @param minMax
	 *            minima and maxima
	 */
	public void setXYMinMax(double[][] minMax) {
		xmin = minMax[0][0];
		xmax = minMax[0][1];
		ymin = minMax[1][0];
		ymax = minMax[1][1];
	}

	/**
	 * Update real world bounds.
	 */
	protected void setRealWorldBounds() {
		updateBoundObjects();
		updateBounds(false, false);
	}

	/**
	 * Updates xmin, xmax, ... for setCoordSystem()
	 */
	protected void setXYMinMaxForSetCoordSystem() {
		xmin = (-getXZero() * getInvXscale());
		xmax = ((getWidth() - getXZero()) * getInvXscale());
		ymax = (getYZero() * getInvYscale());
		ymin = ((getYZero() - getHeight()) * getInvYscale());
	}

	/**
	 * Zooms around fixed point (center of screen)
	 * 
	 * @param zoomFactor
	 *            zoom factor
	 */
	public final void zoomAroundCenter(double zoomFactor) {
		if (!isZoomable()) {
			return;
			// keep xmin, xmax, ymin, ymax constant, adjust everything else
		}

		setXscale(getXscale() * zoomFactor);
		setYscale(getYscale() * zoomFactor);

		// setScaleRatio(getYscale() / getXscale());

		xZero = -getXmin() * getXscale();
		yZero = getYmax() * getYscale();

		setAxesIntervals(getXscale(), 0);
		setAxesIntervals(getYscale(), 1);
		calcPrintingScale();

		// tell kernel
		if (evNo != EVNO_GENERAL) {
			kernel.setEuclidianViewBounds(evNo, getXmin(), getXmax(),
					getYmin(), getYmax(), getXscale(), getYscale());
		}

		setCoordTransformIfNeeded();

		updateBackgroundImage();
		updateAllDrawablesForView(true);

	}

	private void setCoordTransformIfNeeded() {
		if (coordTransform != null) {
			coordTransform.setTransform(xscale, 0.0d, 0.0d, -yscale, xZero,
					yZero);
		}
	}

	/**
	 * @param repaint
	 *            true to repaint
	 */
	final public void updateAllDrawables(boolean repaint) {
		if (repaint && this.batchUpdate) {
			this.needsAllDrawablesUpdate = true;
			return;
		}
		allDrawableList.updateAll();
		if (repaint) {
			repaint();
		}
	}

	/**
	 * Called when the drawing priorities of the objects in the view have changed
	 */
	public void invalidateDrawableList() {
		allDrawableList.sort();
		repaintView();
	}

	/**
	 * @param repaint
	 *            true to repaint
	 */
	final public void updateAllDrawablesForView(boolean repaint) {
		if (repaint && this.batchUpdate) {
			this.needsAllDrawablesUpdate = true;
			return;
		}

		for (Drawable d : allDrawableList) {
			d.updateForView();
		}
		for (Drawable d : bgImageList) {
			d.updateForView();
		}
		GeoElement focused = app.getSelectionManager().getFocusedGroupElement();
		DrawableND focusedDrawable = getDrawableFor(focused);
		if (focusedDrawable != null) {
			focusedGroupGeoBoundingBox.setRectangle(focusedDrawable.getBoundsForStylebarPosition());
		}
		if (repaint) {
			repaint();
		}

		// show the dynamic style bar again (if was visible) at the right location
		if (dynamicStyleBar != null && dynamicStyleBar.isVisible()) {
			dynamicStyleBar.updateStyleBar();
		}
	}

	// may need to be synchronized for some 3D implementations
	@Override
	public void startBatchUpdate() {
		this.batchUpdate = true;
	}

	@Override
	public void endBatchUpdate() {
		this.batchUpdate = false;
		if (this.needsAllDrawablesUpdate) {
			allDrawableList.updateAll();
			repaint();
		}
	}

	/**
	 * 
	 * @return true if currently batch update
	 */
	public boolean isBatchUpdate() {
		return this.batchUpdate;
	}

	/**
	 * @param list
	 *            list
	 * @param b
	 *            whether the list should be drawn as combobox
	 */
	public void drawListAsComboBox(GeoList list, boolean b) {
		DrawableND d = getDrawableFor(list);
		if (d != null) {
			remove(list);
			add(list);
		}
	}

	/**
	 * Recompute printing scale
	 */
	public void calcPrintingScale() {
		double unitPerCM = PRINTER_PIXEL_PER_CM / getXscale();
		int exp = (int) Math.round(Math.log10(unitPerCM));
		printingScale = Math.pow(10, -exp);
	}

	/**
	 * axis: 0 for x-axis, 1 for y-axis
	 * 
	 * @param scale
	 *            axis scale
	 * @param axis
	 *            axis index
	 */
	protected void setAxesIntervals(double scale, int axis) {
		double maxPix = 100; // only one tick is allowed per maxPix pixels
		double units = maxPix / scale;
		int exp = (int) Math.floor(Math.log10(units));

		int maxFractionDigits = Math.max(-exp, kernel.getPrintDecimals());
		
		if (automaticAxesNumberingDistances[axis]) {
			// force same unit if scales are same, see #1082
			if ((axis == 1) && automaticAxesNumberingDistances[0]
					&& DoubleUtil.isEqual(getXscale(), getYscale())) {

				if (piAxisUnit[0] == piAxisUnit[1]) {
					axesNumberingDistances[1] = axesNumberingDistances[0];
				} else if (piAxisUnit[0]) {
					axesNumberingDistances[1] = axesNumberingDistances[0]
							/ Math.PI;
				} else if (piAxisUnit[1]) {
					axesNumberingDistances[1] = axesNumberingDistances[0]
							* Math.PI;
				}

			} else if (piAxisUnit[axis]) {
				axesNumberingDistances[axis] = Math.PI;
			} else {
				// see #2682
				double pot = DoubleUtil.checkDecimalFraction(Math.pow(10, exp));
				double n = DoubleUtil.checkDecimalFraction(units / pot);

				if (n > 5) {
					axesNumberingDistances[axis] = 5 * pot;
				} else if (n > 2) {
					axesNumberingDistances[axis] = 2 * pot;
				} else {
					axesNumberingDistances[axis] = pot;
				}

			}
		}
		axesTickInterval[axis] = axesNumberingDistances[axis] / 2.0;

		// set axes number format

		// NumberFormatAdapter df = axesNumberFormat[axis];

		// display large and small numbers in scienctific notation
		if ((axesNumberingDistances[axis] < 10E-6)
				|| (axesNumberingDistances[axis] > 10E6)) {
			maxFractionDigits = Math.min(14, maxFractionDigits);
			
			if (axesNumberFormatsExponential[maxFractionDigits] == null) {
				axesNumberFormatsExponential[maxFractionDigits] = FormatFactory.getPrototype()
						.getNumberFormat("0.##E0", maxFractionDigits);
			}

			axesNumberFormat[axis] = axesNumberFormatsExponential[maxFractionDigits];
			
			// avoid 4.00000000000004E-11 due to rounding error when
			// computing
			// tick mark numbers
		} else {
			if (axesNumberFormatsNormal[maxFractionDigits] == null) {
				axesNumberFormatsNormal[maxFractionDigits] = FormatFactory.getPrototype()
						.getNumberFormat("###0.##", maxFractionDigits);
			}
			
			axesNumberFormat[axis] = axesNumberFormatsNormal[maxFractionDigits];
		}

		if (automaticGridDistance && axis < 2) {
			gridDistances[axis] = axesNumberingDistances[axis]
					* EuclidianStyleConstants.DEFAULT_GRID_DIST_FACTOR;
		}
	}

	/**
	 * @return font size
	 */
	public int getFontSize() {
		return fontSize;
	}

	/**
	 * renames an element
	 */
	@Override
	public void rename(GeoElement geo) {
		Object d = drawableMap.get(geo);
		if (d != null) {
			((Drawable) d).update();
			repaint();
		}
	}

	@Override
	public void update(GeoElement geo) {
		DrawableND d = drawableMap.get(geo);
		if (d != null) {
			if (!d.isCompatibleWithGeo()) {
				remove(geo);
				add(geo);
				return;
			}
			if (d instanceof DrawImage) {
				this.updateBackgroundOnNextRepaint = ((DrawImage) d)
						.checkInBackground()
						|| this.updateBackgroundOnNextRepaint;
			} else if (!needsSynchUpdate(geo, d.isTracing())) {
				d.setNeedsUpdate(true);
			} else {
				d.update();
			}
			if (geo == app.getSelectionManager().getFocusedGroupElement()
					&& focusedGroupGeoBoundingBox != null) {
				focusedGroupGeoBoundingBox.setRectangle(d.getBoundsForStylebarPosition());
			}
		} else if (drawableNeeded(geo) && geosWaiting.contains(geo)) {
			geosWaiting.remove(geo);
			add(geo);
			d = drawableMap.get(geo);
			if (d != null) {
				d.setNeedsUpdate(true);
				repaint();
			}
		}
	}

	private static boolean needsSynchUpdate(GeoElement geo, boolean tracing) {
		// Keep update of input boxes synchronous #4416
		return (geo.isGeoText() && ((GeoText) geo).isNeedsUpdatedBoundingBox())
				|| geo.isGeoInputBox() || (geo.getTrace() && !tracing) || geo.isMask();
	}

	/**
	 * adds a GeoElement to this view
	 */
	@Override
	public void add(GeoElement geo) {
		// G.Sturr 2010-6-30
		// filter out any geo not marked for this view
		if (!drawableNeeded(geo)) {
			if (isVisibleInThisView(geo)) {
				this.geosWaiting.add(geo);
			}
			return;
			// END G.Sturr
		}

		// check if there is already a drawable for geo
		DrawableND d = getDrawableFor(geo);

		if (d != null) {
			return;
		}

		if (createAndAddDrawable(geo)) {
			repaint();
		}
	}

	/**
	 * create and add geo to this view
	 * 
	 * @param geo
	 *            geo
	 * @return true if drawable created
	 */
	protected boolean createAndAddDrawable(GeoElement geo) {
		DrawableND d = createDrawable(geo);
		if (d != null) {
			if (!bgImageList.contains(d)) {
				allDrawableList.add((Drawable) d);
			}
			return true;
		}
		return false;
	}

	private boolean createPreviewDrawable(GeoElement geo) {
		if (drawableMap.containsKey(geo)) {
			return false;
		}
		return createAndAddDrawable(geo);
	}

	/**
	 * Updates the special points of a function.
	 * 
	 * @param geos
	 *            special points
	 */
	public void updateSpecPointFromInputBar(List<GeoElement> geos) {
		if (specPoints != null) {
			for (GeoElement geo : specPoints) {
				remove(geo);
			}
		}
		specPoints = geos;
		repaintForPreviewFromInputBar();
	}

	@Override
	public void updatePreviewFromInputBar(GeoElement[] geos) {
		if (previewFromInputBarGeos != null) {
			for (GeoElement geo : previewFromInputBarGeos) {
				if (!geo.isLabelSet()) {
					remove(geo);
				}
			}
		}
		previewFromInputBarGeos = geos;
		updatePreviewFromInputBar();
	}

	protected void updatePreviewFromInputBar() {
		if (app.getConfig().hasPreviewPoints()) {
			GeoElement geo0 = (previewFromInputBarGeos == null
					|| previewFromInputBarGeos.length == 0) ? null
					: previewFromInputBarGeos[0];
			app.getSpecialPointsManager().updateSpecialPoints(geo0);
		} else {
			repaintForPreviewFromInputBar();
		}
	}

	protected void repaintForPreviewFromInputBar() {
		boolean needsRepaint = false;
		if (previewFromInputBarGeos != null) {
			for (GeoElement geo : previewFromInputBarGeos) {
				needsRepaint = createAndUpdatePreviewDrawable(geo) || needsRepaint;
			}
		}

		needsRepaint = createPreviewsForSpecsPoints() || needsRepaint;

		if (needsRepaint) {
			repaint();
		}
	}

	protected boolean createAndUpdatePreviewDrawable(GeoElement geo) {
		boolean needsRepaint = createPreviewDrawable(geo);
		needsRepaint = updatePreviewDrawable(geo) || needsRepaint;
		return needsRepaint;
	}

	private boolean updatePreviewDrawable(GeoElement geo) {
		if (drawableMap.containsKey(geo)) {
			DrawableND drawable = drawableMap.get(geo);
			drawable.setNeedsUpdate(true);
			return true;
		}
		return false;
	}

	/**
	 * create previews for special points if needed
	 * 
	 * @return true if at least one preview was created
	 */
	protected boolean createPreviewsForSpecsPoints() {
		boolean needsRepaint = false;
		if (specPoints != null) {
			for (GeoElement geo : specPoints) {
				needsRepaint = createPreviewDrawable(geo) || needsRepaint;
				setupPreviewsSpecsPointDrawable(geo);
			}
		}
		return needsRepaint;
	}

	private void setupPreviewsSpecsPointDrawable(GeoElement specialPoint) {
		DrawableND drawable = getDrawableFor(specialPoint);
		if (drawable instanceof DrawPoint) {
			DrawPoint drawPoint = (DrawPoint) drawable;
			drawPoint.setPreview(true);
		}
	}

	private boolean drawableNeeded(GeoElement geo) {
		if (geo.isVisibleInView(App.VIEW_FUNCTION_INSPECTOR)) {
			return true;
		}
		if (isVisibleInThisView(geo)
				&& (geo.isLabelSet() || isPlotPanel())) {
			return geo.isEuclidianVisible()

					|| (geo.isGeoText() && ((GeoText) geo)
							.isNeedsUpdatedBoundingBox())
					|| (geo.isGeoAngle() && geo.getParentAlgorithm() instanceof AlgoAngle);
		}
		return false;
	}

	/**
	 * @return whether this view is used as plotPanel in prob calc
	 */
	public boolean isPlotPanel() {
		return false;
	}

	/**
	 * removes a GeoElement from this view
	 */
	@Override
	public void remove(GeoElement geo) {
		this.geosWaiting.remove(geo);
		Drawable d = (Drawable) drawableMap.remove(geo);
		if (d == null) {
			return;
		}

		allDrawableList.remove(d);
		resetBoundingBoxes();

		if (d instanceof RemoveNeeded) {
			((RemoveNeeded) d).remove();
		}

		if (geo.isGeoPoint()) {
			stickyPointList.remove(geo);
		}
		euclidianController.clear(geo);
		if (!d.isCreatedByDrawListVisible()) {
			repaint();
		}
	}

	/** get the hits recorded */
	@Override
	public Hits getHits() {
		return hitDetector.getHits();
	}

	/**
	 * event coords
	 * 
	 * @param x
	 *            event screen x-coord
	 * @param y
	 *            event screen y-coord
	 * @param type
	 *            event type
	 *
	 * @return whether textfield was clicked
	 */
	public boolean textfieldClicked(int x, int y, PointerEventType type) {
		if (getEuclidianController().isDraggingBeyondThreshold()) {
			return false;
		}

		for (Drawable d : allDrawableList) {
			if (d instanceof DrawInputBox
					&& (d.hit(x, y, app.getCapturingThreshold(type))
					|| d.hitLabel(x, y))) {
				GeoElement geo = d.getGeoElement();
				if (geo.isEuclidianVisible() && geo.isSelectionAllowed(this)) {
					focusTextField((GeoInputBox) geo);
					((DrawInputBox) d).setWidgetVisible(true);
					return true;
				}

				((DrawInputBox) d).setWidgetVisible(false);
			}
		}

		return false;
	}

	/**
	 * set hits for current mouse loc
	 * 
	 * @param type
	 *            event type
	 */
	final public void setHits(PointerEventType type) {
		getCompanion().setHits(type);
	}

	@Override
	public void setHits(GPoint p, PointerEventType type) {
		hitDetector.setHits(p, type);
	}

	/**
	 * returns GeoElement whose label is at screen coords (x,y).
	 */
	@Override
	public GeoElement getLabelHit(GPoint p, PointerEventType type) {
		if (!getApplication().isLabelDragsEnabled()) {
			return null;
		}
		for (Drawable d : allDrawableList) {
			if (d.hitLabel(p.x, p.y)) {
				GeoElement geo = d.getGeoElement();
				if (geo.isEuclidianVisible()) {
					return geo;
				}
			}
		}
		return null;
	}

	/**
	 * Returns Drawable whose bounding box handler is at screen coords (x,y).
	 * 
	 * @param p
	 *            pointer event position
	 * @param type
	 *            pointer event type
	 * @return hit drawable
	 */
	public Drawable getBoundingBoxHandlerHit(GPoint p, PointerEventType type) {
		if (p == null || getEuclidianController().isMultiSelection()) {
			return null;
		}
		for (Drawable d : allDrawableList) {
			hitHandler = d.hitBoundingBoxHandler(p.x, p.y, app.getCapturingThreshold(type));
			if (hitHandler != EuclidianBoundingBoxHandler.UNDEFINED) {
				GeoElement geo = d.getGeoElement();
				if (geo.isEuclidianVisible()) {
					return d;
				}
			}
		}
		return null;
	}

	/**
	 * says that label hit needs to be refreshed
	 */
	public void setLabelHitNeedsRefresh() {
		labelHitNeedsRefresh = true;
	}

	/**
	 * WARNING: ensure setLabelHitNeedsRefresh() is call once since last mouse
	 * event
	 * 
	 * @param p
	 *            mouse position
	 * @param type
	 *            event type
	 * @return geo hit on label since last refresh
	 */
	public GeoElement getLabelHitCheckRefresh(GPoint p, PointerEventType type) {
		if (labelHitNeedsRefresh) {
			labelHitLastGeo = getLabelHit(p, type);
			labelHitNeedsRefresh = false;
		}
		return labelHitLastGeo;
	}

	/**
	 * Returns the drawable for the given GeoElement.
	 * 
	 * @param geo
	 *            geo
	 * @return drawable for the given GeoElement.
	 */
	@Override
	@CheckForNull
	final public DrawableND getDrawableFor(GeoElementND geo) {
		return drawableMap.get(geo);
	}

	@Override
	public DrawableND getDrawableND(GeoElement geo) {
		return getDrawableFor(geo);
	}

	/**
	 * @param geo
	 *            geo
	 * @return true if geo is visible in this view
	 */
	public boolean isVisibleInThisView(GeoElementND geo) {
		return companion.isVisibleInThisView(geo);
	}

	/**
	 * adds a GeoElement to this view
	 * 
	 * @param geo
	 *            GeoElement to be added
	 * @return drawable for given GeoElement
	 */
	protected DrawableND createDrawable(GeoElement geo) {
		DrawableND d = newDrawable(geo);
		if (d != null) {
			drawableMap.put(geo, d);
			if (geo.isGeoPoint()) {
				stickyPointList.add((GeoPointND) geo);
			}
		}

		return d;
	}

	/**
	 * @param geo
	 *            geo
	 * @return new drawable for the geo
	 */
	@Override
	public DrawableND newDrawable(GeoElementND geo) {
		return companion.newDrawable(geo);
	}

	@Override
	public void reset() {
		resetMode();
		updateBackgroundImage();
	}

	/**
	 * clears all selections and highlighting
	 */
	@Override
	public void resetMode() {
		setMode(mode);
	}

	/**
	 * @param mode2
	 *            mode number
	 */
	public void setMode(int mode2) {
		setMode(mode2, ModeSetter.TOOLBAR);
	}

	@Override
	public void repaintView() {
		repaint();

	}

	@Override
	public void updateVisualStyle(GeoElement geo, GProperty prop) {
		update(geo);
		if (styleBar != null) {
			styleBar.updateVisualStyle(geo);
		}
		if (app.isUnbundledOrWhiteboard()) {
			if (dynamicStyleBar != null) {
				dynamicStyleBar.updateVisualStyle(geo);
			}
		}
		if (app.hasSpecialPointsManager()) {
			app.getSpecialPointsManager().updateSpecialPoints(null);
		}
	}

	@Override
	public void updateHighlight(GeoElementND geo) {
		// nothing to do here
	}

	@Override
	public boolean hasVisibleObjects() {
		for (GeoElement geo : drawableMap.keySet()) {
			if (geo.isEuclidianVisible()) {
				return true;
			}
		}
		return false;
	}

	@Override
	final public void updateAuxiliaryObject(GeoElement geo) {
		// repaint();
	}

	/**
	 * Updates font size for all drawables
	 */
	protected void updateDrawableFontSize() {
		for (Drawable d : allDrawableList) {
			d.updateFontSize();
		}
		repaint();
	}

	/**
	 * @return plain font
	 */
	public GFont getFontPoint() {
		if (fontPoint == null) {
			return app.getPlainFontCommon();
		}
		return fontPoint;
	}

	private void setFontPoint(GFont fontPoint) {
		this.fontPoint = fontPoint;
	}

	/**
	 * @return font for lines
	 */
	public GFont getFontLine() {
		return getFontPoint();
	}

	/**
	 * @return font for vectors
	 */
	public GFont getFontVector() {
		return getFontPoint();
	}

	/**
	 * @return font for conics
	 */
	public GFont getFontConic() {
		return getFontPoint();
	}

	/**
	 * @return font for coords
	 */
	public GFont getFontCoords() {
		if (fontCoords == null) {
			initFontCoords();
		}
		
		// default while initing
		if (fontCoords == null) {
			return getApplication().getFontCommon(false, GFont.PLAIN,
					(int) Math.max(Math.round(getFontSize() * 0.75), 10));
		}
		
		return fontCoords;
	}

	private void setFontCoords(GFont fontCoords0) {
		this.fontCoords = fontCoords0;
	}

	/**
	 * @return font for axes
	 */
	public GFont getFontAxes() {
		return getFontCoords();
	}

	/**
	 * @return font for angles
	 */
	public GFont getFontAngle() {
		return getFontPoint();
	}

	@Override
	public ArrayList<GeoPointND> getStickyPointList() {
		return stickyPointList;
	}

	/**
	 * Sets the global size for checkboxes. Michael Borcherds 2008-05-12
	 * 
	 * @param size
	 *            13 or 26
	 */
	public void setBooleanSize(int size) {
		// only 13 and 26 currently allowed
		app.setCheckboxSize(size);

		updateAllDrawables(true);
	}

	/**
	 * @return size of booleans (13 or 26)
	 */
	final public int getBooleanSize() {
		return app.getCheckboxSize();
	}

	/**
	 * @param setto
	 *            tooltip mode
	 */
	public void setAllowToolTips(int setto) {
		tooltipsInThisView = setto;
	}

	@Override
	final public int getAllowToolTips() {
		return tooltipsInThisView;
	}

	// /////////////////////////////////////////
	// FOR EUCLIDIANVIEWFORPLANE
	// /////////////////////////////////////////

	/**
	 * return null if classic 2D view
	 * 
	 * @return matrix representation of the plane shown by this view
	 */
	public CoordMatrix getMatrix() {
		return companion.getMatrix();
	}

	/**
	 * return null if classic 2D view
	 * 
	 * @return matrix inverse representation of the plane shown by this view
	 */
	public CoordMatrix getInverseMatrix() {
		return companion.getMatrix();
	}

	@Override
	public String getFromPlaneString() {
		return companion.getFromPlaneString();
	}

	@Override
	public String getTranslatedFromPlaneString() {
		return companion.getTranslatedFromPlaneString();
	}

	@Override
	public boolean isDefault2D() {
		return companion.isDefault2D();
	}

	@Override
	public boolean isEuclidianView3D() {
		return false;
	}

	@Override
	public int getViewID() {
		switch (evNo) {
		case 1:
			return App.VIEW_EUCLIDIAN;
		case 2:
			return App.VIEW_EUCLIDIAN2;
		default:
			return App.VIEW_NONE;
		}
	}

	@Override
	public void changeLayer(GeoElement geo, int oldlayer, int newlayer) {
		if (drawableMap.containsKey(geo)) {
			invalidateDrawableList();
		}
	}

	/**
	 * 
	 * @return null (for 2D) and xOyPlane (for 3D)
	 */
	public GeoPlaneND getPlaneContaining() {
		return companion.getPlaneContaining();
	}

	/**
	 * 
	 * @return null (for 2D) and xOyPlane (for 3D)
	 */
	@Override
	public GeoDirectionND getDirection() {
		return companion.getDirection();
	}

	/**
	 * tranform in view coords
	 * 
	 * @param coords
	 *            point
	 * @return the same coords for classic 2d view
	 */
	@Override
	public Coords getCoordsForView(Coords coords) {
		return companion.getCoordsForView(coords);
	}

	@Override
	public boolean isMoveable(GeoElement geo) {
		return companion.isMoveable(geo);
	}

	@Override
	public ArrayList<GeoPointND> getFreeInputPoints(AlgoElement algoParent) {
		return companion.getFreeInputPoints(algoParent);
	}

	/**
	 * Replaces num by num2 in xmin, xmax,ymin,ymax. Does not add / remove EV
	 * listeners from these numerics
	 * 
	 * @param num
	 *            old numeric
	 * @param num2
	 *            new numeric
	 */
	@Override
	public void replaceBoundObject(GeoNumeric num, GeoNumeric num2) {
		if (xmaxObject == num) {
			xmaxObject = num2;
		}
		if (xminObject == num) {
			xminObject = num2;
		}
		if (ymaxObject == num) {
			ymaxObject = num2;
		}
		if (yminObject == num) {
			yminObject = num2;
		}
		for (int i = 0; i < axesDistanceObjects.length; i++) {
			if (axesDistanceObjects[i] == num) {
				axesDistanceObjects[i] = num2;
			}
		}
		updateBounds(true, true);
	}

	/**
	 * @return right angle style
	 */
	final public int getRightAngleStyle() {
		return getApplication().rightAngleStyle;
	}

	@Override
	public boolean isAutomaticGridDistance() {
		return automaticGridDistance;
	}

	@Override
	public double[] getGridDistances() {
		return gridDistances;
	}

	@Override
	public void setGridDistances(double[] dist) {
		if (dist == null) {
			return;
		}
		gridDistances = dist;
		setAutomaticGridDistance(false);
	}

	@Override
	public int getGridLineStyle() {
		return gridLineStyle;
	}

	@Override
	public void setAutomaticGridDistance(boolean flag) {
		automaticGridDistance = flag;
		setAxesIntervals(getXscale(), 0);
		setAxesIntervals(getYscale(), 1);
		if (flag) {
			gridDistances[2] = Math.PI / 6;
		}
	}

	@Override
	public int getAxesLineStyle() {
		return axesLineType;
	}

	@Override
	public void setAxesLineStyle(int axesLineStyle) {
		this.axesLineType = axesLineStyle;
	}

	/**
	 * @return RW => EV transform; created (but not initialized) when null
	 */
	public GAffineTransform getCoordTransform() {
		if (coordTransform == null) {
			coordTransform = AwtFactory.getPrototype().newAffineTransform();
		}
		return coordTransform;
	}

	/**
	 * @param coordTransform
	 *            RW => EV transform
	 */
	protected void setCoordTransform(GAffineTransform coordTransform) {
		this.coordTransform = coordTransform;
	}

	@Override
	public void updateBackground() {
		// make sure axis number formats are up to date
		setAxesIntervals(getXscale(), 0);
		setAxesIntervals(getYscale(), 1);

		updateBackgroundImage();
		updateAllDrawablesForView(true);
		// repaint();
	}

	/**
	 * @param fontForGraphics
	 *            font
	 * @return graphics corresponding to given font
	 */
	public abstract GGraphics2D getTempGraphics2D(GFont fontForGraphics);

	/**
	 * @return font
	 */
	public abstract GFont getFont();

	/**
	 * Initializes cursor
	 */
	protected abstract void initCursor();

	/**
	 * @param mode
	 *            new mode for sylebar
	 */
	protected abstract void setStyleBarMode(int mode);

	/**
	 * @param mode
	 *            mode
	 * @return true if given mode can use selection rectangle as input
	 */
	final public static boolean usesSelectionAsInput(int mode) {
		switch (mode) {
		case EuclidianConstants.MODE_TRANSLATE_BY_VECTOR:
			return false; // changed for new "drag" behaviour
		case EuclidianConstants.MODE_MIRROR_AT_POINT:
		case EuclidianConstants.MODE_MIRROR_AT_LINE:
		case EuclidianConstants.MODE_DILATE_FROM_POINT:
		case EuclidianConstants.MODE_ROTATE_BY_ANGLE:
		case EuclidianConstants.MODE_PEN:
			// case EuclidianConstants.MODE_PENCIL:
			return true;

		default:
			return false;
		}
	}

	/**
	 * @param mode
	 *            mode
	 * @return true if mode can handle selection rectangle as input
	 */
	final public static boolean usesSelectionRectangleAsInput(int mode) {
		switch (mode) {
		case EuclidianConstants.MODE_FITLINE:
		case EuclidianConstants.MODE_CREATE_LIST:
			// case EuclidianConstants.MODE_PEN:
		case EuclidianConstants.MODE_MIRROR_AT_LINE:
		case EuclidianConstants.MODE_MIRROR_AT_POINT:
		case EuclidianConstants.MODE_ROTATE_BY_ANGLE:
		case EuclidianConstants.MODE_TRANSLATE_BY_VECTOR:
		case EuclidianConstants.MODE_DILATE_FROM_POINT:
		case EuclidianConstants.MODE_COPY_VISUAL_STYLE:
			return true;
		default:
			return false;
		}
	}

	// getters and Setters for axis control vars

	@Override
	public void setSelectionRectangle(GRectangle selectionRectangle) {
		this.selectionRectangle = selectionRectangle;
	}

	/**
	 * @param boundingBox
	 *            - bounding box for select
	 */
	public void setBoundingBox(BoundingBox<? extends GShape> boundingBox) {
		this.boundingBox = boundingBox;
	}

	/**
	 * Reset bounding box for both the main selection and the focused group element selection.
	 */
	public void resetBoundingBoxes() {
		this.focusedGroupGeoBoundingBox = null;
		this.boundingBox = null;
	}

	/**
	 * @param boundingBox
	 *            bounding box for focused group element selection
	 */
	public void setFocusedGroupGeoBoundingBox(BoundingBox<? extends GShape> boundingBox) {
		this.focusedGroupGeoBoundingBox = boundingBox;
	}

	/**
	 * @param shapeRectangle
	 *            - preview of rectangle for ShapeRectangle
	 */
	public void setShapeRectangle(GRectangle shapeRectangle) {
		this.shapeRectangle = shapeRectangle;
	}

	/**
	 * @param maskPreview
	 *            - preview of rectangle for mask tool
	 */
	public void setMaskPreview(GRectangle maskPreview) {
		this.maskPreview = maskPreview;
	}

	/**
	 * @param shapeEllipse
	 *            - preview of ellipse for ShapeEllipse
	 */
	public void setShapeEllipse(GEllipse2DDouble shapeEllipse) {
		this.shapeEllipse = shapeEllipse;
	}

	/**
	 * @param shapeLine
	 *            - preview of line for ShapeLine
	 */
	public void setShapeLine(GLine2D shapeLine) {
		this.shapeLine = shapeLine;
	}

	/**
	 * @param shapePolygon
	 *            - preview of polygon for ShapeTriangle/RegularPolygon/Polygon
	 */
	public void setShapePolygon(GGeneralPath shapePolygon) {
		this.shapePolygon = shapePolygon;
	}

	@Override
	public double[] getAxesCross() {
		return axisCross;
	}

	@Override
	public void setAxesCross(double[] axisCross) {
		this.axisCross = axisCross;
	}

	// for xml handler
	@Override
	public void setAxisCross(int axis, double cross) {
		axisCross[axis] = cross;
	}

	@Override
	public boolean[] getPositiveAxes() {
		return positiveAxes;
	}

	@Override
	public void setPositiveAxes(boolean[] positiveAxis) {
		this.positiveAxes = positiveAxis;
	}

	// for xml handler
	@Override
	public void setPositiveAxis(int axis, boolean isPositiveAxis) {
		positiveAxes[axis] = isPositiveAxis;
	}

	@Override
	public boolean[] getDrawBorderAxes() {
		return drawBorderAxes;
	}

	@Override
	public void setDrawBorderAxes(boolean[] drawBorderAxes) {
		this.drawBorderAxes = drawBorderAxes;
		// don't show corner coordinates if one of the axes is sticky
		setAxesCornerCoordsVisible(!(drawBorderAxes[0] || drawBorderAxes[1]));
	}

	/**
	 * @return true if showing corner coords
	 */
	public boolean isAxesCornerCoordsVisible() {
		return showAxesCornerCoords;
	}

	@Override
	public void setAxesCornerCoordsVisible(boolean showAxesCornerCoords) {
		this.showAxesCornerCoords = showAxesCornerCoords;
	}

	/**
	 * @return scale factor for print
	 */
	public final double getPrintingScale() {
		return printingScale;
	}

	/**
	 * @param printingScale
	 *            scale factor for print
	 */
	public final void setPrintingScale(double printingScale) {
		this.printingScale = printingScale;
	}

	/**
	 * 
	 * setters and getters for EuclidianViewInterface
	 * 
	 */

	@Override
	public String[] getAxesLabels(boolean addBoldItalicTags) {
		String[] ret = new String[axesLabels.length];

		for (int axis = 0; axis < axesLabels.length; axis++) {
			ret[axis] = axesLabels[axis];
		}

		if (addBoldItalicTags) {
			for (int axis = 0; axis < axesLabels.length; axis++) {
				if (axesLabels[axis] != null && this.settings != null) {
					ret[axis] = this.settings.axisLabelForXML(axis);
				}
			}
		}

		return ret;
	}

	/**
	 * @param axis
	 *            axis index
	 * @param addBoldItalicTags
	 *            whether to include HTML tags
	 * @return axis label
	 */
	public String getAxisLabel(int axis, boolean addBoldItalicTags) {
		if (addBoldItalicTags) {
			if (axesLabels[axis] != null && this.settings != null) {
				return this.settings.axisLabelForXML(axis);
			}
		}
		return axesLabels[axis];
	}

	@Override
	public void setAxesLabels(String[] axesLabels) {
		setAxisLabel(0, axesLabels[0]);
		setAxisLabel(1, axesLabels[1]);
	}

	/**
	 * sets the axis label to axisLabel
	 * 
	 * @param axis
	 *            axis id
	 * @param axLabel
	 *            axis label
	 */
	@Override
	public void setAxisLabel(int axis, String axLabel) {
		String axisLabel = axLabel;
		if ((axisLabel == null) || (axisLabel.length() == 0)) {
			axesLabels[axis] = null;
		} else {
			axesLabelsStyle[axis] = GFont.PLAIN;
			if (axisLabel.startsWith("<i>") && axisLabel.endsWith("</i>")) {

				axisLabel = axisLabel.substring(3, axisLabel.length() - 4);
				axesLabelsStyle[axis] |= GFont.ITALIC;
			}

			if (axisLabel.startsWith("<b>") && axisLabel.endsWith("</b>")) {
				axisLabel = axisLabel.substring(3, axisLabel.length() - 4);
				axesLabelsStyle[axis] |= GFont.BOLD;
			}

			axesLabels[axis] = axisLabel;
		}
	}

	/**
	 * 
	 * @param i
	 *            axis index
	 * @return axis scale
	 */
	public double getScale(int i) {
		if (i == 0) {
			return getXscale();
		}
		return getYscale();
	}

	@Override
	final public void setAutomaticAxesNumberingDistance(boolean flag, int axis) {
		automaticAxesNumberingDistances[axis] = flag;
		setAxesIntervals(getScale(axis), axis);
	}

	@Override
	public boolean[] isAutomaticAxesNumberingDistance() {
		return automaticAxesNumberingDistances;
	}

	@Override
	public double[] getAxesNumberingDistances() {
		return axesNumberingDistances;
	}

	public GeoNumberValue[] getAxesDistanceObjects() {
		return axesDistanceObjects;
	}

	/**
	 * 
	 * @param dist
	 *            numbering distance
	 * @param axis
	 *            0 for xAxis, 1 for yAxis
	 */
	@Override
	public void setAxesNumberingDistance(GeoNumberValue dist, int axis) {
		if (axesDistanceObjects[axis] != null) {
			((GeoNumeric) axesDistanceObjects[axis]).removeEVSizeListener(this);
		}
		if (dist != null && !Double.isNaN(dist.getDouble())
				&& dist.getDouble() > 0) {
			axesNumberingDistances[axis] = dist.getDouble();
			axesDistanceObjects[axis] = dist;
			setAutomaticAxesNumberingDistance(false, axis);
			((GeoNumeric) dist).addEVSizeListener(this);
		} else {
			axesDistanceObjects[axis] = null;
			setAutomaticAxesNumberingDistance(true, axis);
		}
	}

	// Michael Borcherds 2008-04-11
	@Override
	public boolean getGridIsBold() {
		return gridIsBold;
	}

	@Override
	public boolean[] getShowAxesNumbers() {
		return showAxesNumbers;
	}

	@Override
	public void setShowAxesNumbers(boolean[] showAxesNumbers) {
		this.showAxesNumbers = showAxesNumbers;
	}

	@Override
	public void setShowAxisNumbers(int axis, boolean showAxisNumbers) {
		showAxesNumbers[axis] = showAxisNumbers;
	}

	@Override
	public String[] getAxesUnitLabels() {
		return axesUnitLabels;
	}

	@Override
	public void setAxesUnitLabels(String[] axesUnitLabels) {
		for (int i = 0; i < this.axesUnitLabels.length; i++) {
			this.axesUnitLabels[i] = axesUnitLabels[i];
		}

		// check if pi is an axis unit
		for (int i = 0; i < getDimension(); i++) {
			piAxisUnit[i] = (axesUnitLabels[i] != null)
					&& axesUnitLabels[i].equals(Unicode.PI_STRING);
		}
		setAxesIntervals(getXscale(), 0);
		setAxesIntervals(getYscale(), 1);
	}

	@Override
	public int[] getAxesTickStyles() {
		return axesTickStyles;
	}

	/**
	 * @param i
	 *            axis index
	 * @return tick style, EuclidianStyleConstants.AXES_TICK_STYLE_*
	 */
	public int getAxisTickStyle(int i) {
		return axesTickStyles[i];
	}

	@Override
	public void setAxisTickStyle(int axis, int tickStyle) {
		axesTickStyles[axis] = tickStyle;
	}

	@Override
	public void setAxesTickStyles(int[] axesTickStyles) {
		this.axesTickStyles = axesTickStyles;
	}

	@Override
	public void setShowMouseCoords(boolean b) {
		showMouseCoords = b;
	}

	@Override
	public boolean getAllowShowMouseCoords() {
		return allowShowMouseCoords;
	}

	@Override
	public void setAllowShowMouseCoords(boolean neverShowMouseCoords) {
		this.allowShowMouseCoords = neverShowMouseCoords;
	}

	@Override
	public boolean getShowMouseCoords() {
		return showMouseCoords;
	}

	@Override
	public void setShowAxesRatio(boolean b) {
		showAxesRatio = b;
	}

	@Override
	public Previewable getPreviewDrawable() {
		return previewDrawable;
	}

	@Override
	public double getGridDistances(int i) {
		return gridDistances[i];
	}

	@Override
	public boolean getShowGrid() {
		return showGrid;
	}

	@Override
	public boolean isGridOrAxesShown() {
		return showAxes[0] || showAxes[1] || showGrid;
	}

	/**
	 * says if the axis is shown or not
	 * 
	 * @param axis
	 *            id of the axis
	 * @return if the axis is shown
	 */
	@Override
	public boolean getShowAxis(int axis) {
		return showAxes[axis];
	}

	public boolean isAxesHidden() {
		return !showAxes[0] && !showAxes[1];
	}

	@Override
	public boolean getShowXaxis() {
		// return showAxes[0];
		return getShowAxis(AXIS_X);
	}

	@Override
	public boolean getShowYaxis() {
		return getShowAxis(AXIS_Y);
	}

	/*
	 * say if the axis is logarithmic
	 */

	/**
	 * @param axis
	 *            axis index
	 * @return whether to switch axis to log scale
	 */
	public boolean getLogAxis(int axis) {
		return logAxes[axis];
	}

	@Override
	public boolean getXaxisLog() {
		return getLogAxis(AXIS_X);
	}

	@Override
	public boolean getYaxisLog() {
		return getLogAxis(AXIS_Y);
	}

	// ///////////////////////////////////////
	// previewables

	@Override
	public Previewable createPreviewLine(ArrayList<GeoPointND> selectedPoints) {
		return new DrawLine(this, selectedPoints, PreviewType.LINE);
	}

	@Override
	public Previewable createPreviewPerpendicularBisector(
			ArrayList<GeoPointND> selectedPoints) {
		return new DrawLine(this, selectedPoints,
				PreviewType.PERPENDICULAR_BISECTOR);
	}

	@Override
	public Previewable createPreviewAngleBisector(
			ArrayList<GeoPointND> selectedPoints) {
		return new DrawLine(this, selectedPoints, PreviewType.ANGLE_BISECTOR);
	}

	@Override
	public Previewable createPreviewSegment(ArrayList<GeoPointND> selectedPoints) {
		return new DrawSegment(this, selectedPoints);
	}

	@Override
	public Previewable createPreviewRay(ArrayList<GeoPointND> selectedPoints) {
		return new DrawRay(this, selectedPoints);
	}

	@Override
	public Previewable createPreviewVector(ArrayList<GeoPointND> selectedPoints) {
		return new DrawVector(this, selectedPoints);
	}

	@Override
	public Previewable createPreviewConic(int mode1,
			ArrayList<GeoPointND> selectedPoints) {
		return new DrawConic(this, mode1, selectedPoints);
	}

	@Override
	public Previewable createPreviewParabola(
			ArrayList<GeoPointND> selectedPoints,
			ArrayList<GeoLineND> selectedLines) {
		return new DrawConic(this, selectedPoints, selectedLines);
	}

	@Override
	public Previewable createPreviewPolygon(ArrayList<GeoPointND> selectedPoints) {
		return new DrawPolygon(this, selectedPoints);
	}

	@Override
	public Previewable createPreviewAngle(ArrayList<GeoPointND> selectedPoints) {
		return new DrawAngle(this, selectedPoints);
	}

	@Override
	public Previewable createPreviewPolyLine(
			ArrayList<GeoPointND> selectedPoints) {
		return new DrawPolyLine(this, selectedPoints);
	}

	@Override
	public void updatePreviewable() {
		GPoint mouseLoc = getEuclidianController().mouseLoc;
		getPreviewDrawable().updateMousePos(toRealWorldCoordX(mouseLoc.x),
				toRealWorldCoordY(mouseLoc.y));
	}

	@Override
	public void updatePreviewableForProcessMode() {
		if (getPreviewDrawable() != null) {
			getPreviewDrawable().updatePreview();
		}
	}

	@Override
	public void mouseEntered() {
		setHasMouse(true);
	}

	@Override
	public void mouseExited() {
		setHasMouse(false);
	}

	/**
	 * set if view has mouse
	 * 
	 * @param flag
	 *            flag
	 */
	public void setHasMouse(boolean flag) {
		hasMouse = flag;
	}

	/**
	 * @return whether mouse is hovering over this view
	 */
	final public boolean hasMouse() {
		return companion.hasMouse();
	}

	/**
	 * @return whether mouse is hovering over this view
	 */
	final public boolean hasMouse2D() {
		return hasMouse;
	}

	@Override
	public Previewable createPreviewParallelLine(
			ArrayList<GeoPointND> selectedPoints,
			ArrayList<GeoLineND> selectedLines,
			ArrayList<GeoFunction> selectedFunctions) {
		return new DrawLine(this, selectedPoints, selectedLines,
				selectedFunctions, true);
	}

	@Override
	public Previewable createPreviewPerpendicularLine(
			ArrayList<GeoPointND> selectedPoints,
			ArrayList<GeoLineND> selectedLines,
			ArrayList<GeoFunction> selectedFunctions) {
		return new DrawLine(this, selectedPoints, selectedLines,
				selectedFunctions, false);
	}

	/**
	 * @param application
	 *            application
	 */
	public void setApplication(App application) {
		this.app = application;
	}

	@Override
	public App getApplication() {
		return this.app;
	}

	/**
	 * Update fonts
	 */
	public void updateFonts() {
		setFontSize(getApplication().getFontSize());

		setFontPoint(getApplication().getPlainFontCommon().deriveFont(
				GFont.PLAIN, getFontSize()));

		if (getSettings() != null) {
			initFontCoords();
		}
		updateDrawableFontSize();
		updateBackground();
	}

	void initFontCoords() {
		if (getSettings() != null) {
			setFontCoords(getApplication().getFontCommon(
					getSettings().getAxesLabelsSerif(),
					getSettings().getAxisFontStyle(),
					(int) Math.max(Math.round(getFontSize() * 0.75), 10)));
		}
	}

	public void setKeepCenter(boolean center) {
		keepCenter = center;
	}

	public boolean getKeepCenter() {
		return keepCenter;
	}

	/**
	 * Size changed, make sure our settings reflect that
	 */
	public void updateSize() {
		if (getKeepCenter()) {
			updateSizeKeepCenter();
		} else {
			updateSizeChange();
		}
	}

	/**
	 * Resize to settings size, keep centered.
	 */
	protected void updateSizeKeepCenter() {
		int w = getWidth();
		int h = getHeight();
		if (getSettings() != null) {
			int sw = getSettings().getWidth();
			int sh = getSettings().getHeight();
			double x0 = getSettings().getXZero();
			double y0 = getSettings().getYZero();
			if (sw == 0) {
				// no dimension from file: center the view
				sw = (int) Math.round(x0 * 2);
				sh = (int) Math.round(y0 * 2);
			}
			double dx = (w - sw) / 2.0;
			double dy = (h - sh) / 2.0;
			xZero = getSettings().getXZero() + dx;
			yZero = getSettings().getYZero() + dy;
			getSettings().setSize(w, h);
			getSettings().setOriginNoUpdate(xZero, yZero);
		}

		updateSizeChange();
	}

	private void updateSizeChange() {
		updateSizeKeepDrawables();
		updateAllDrawablesForView(true);
	}

	/**
	 * Size changed, make sure our settings reflect that bu do not update
	 * drawables
	 */
	protected abstract void updateSizeKeepDrawables();

	/**
	 * Try to focus this view
	 * 
	 * @return true if successful
	 */
	@Override
	public abstract boolean requestFocusInWindow();

	/**
	 * Draws all geometric objects
	 * 
	 * @param g2
	 *            graphics
	 */
	final private void drawGeometricObjects(GGraphics2D g2) {
		// only draw drawables we need
		allDrawableList.drawAll(g2);

		if (getEuclidianController().isMultiSelection()) {
			getEuclidianController()
					.setBoundingBoxFromList(app.getSelectionManager().getSelectedGeos());
		}
	}

	/**
	 * Draws all objects
	 * 
	 * @param g2
	 *            graphics
	 */
	public void drawObjects(GGraphics2D g2) {
		drawGeometricObjects(g2);
		drawActionObjects(g2);

		if (previewDrawable != null) {
			previewDrawable.drawPreview(g2);
		}
		adjustObjects();
		drawMasks(g2);
	}

	/**
	 * Fills background with background color
	 * 
	 * @param g
	 *            graphics
	 */
	protected void clearBackground(GGraphics2D g) {
		g.setColor(getBackgroundCommon());
		g.updateCanvasColor();
		g.fillRect(getMinXScreen(), getMinYScreen(),
				getMaxXScreen() - getMinXScreen(),
				getMaxYScreen() - getMinYScreen());
	}

	/**
	 * @param g
	 *            graphics
	 * @param transparency
	 *            alpha value
	 */
	protected void drawBackgroundWithImages(GGraphics2D g, boolean transparency) {
		if (!transparency) {
			clearBackground(g);
		}

		// GGB-977
		setBackgroundUpdating(true);
		bgImageList.drawAll(g);
		setBackgroundUpdating(false);

		drawBackground(g, false);
	}

	private void setBackgroundUpdating(boolean b) {
		backgroundIsUpdating = b;
	}

	public boolean isBackgroundUpdating() {
		return backgroundIsUpdating;
	}

	/**
	 * Draw axes ratio next to the mouse when mouse zooming.
	 * 
	 * @param g2
	 *            graphics
	 */
	final protected void drawAxesRatio(GGraphics2D g2) {
		GPoint pos = euclidianController.mouseLoc;
		if (pos == null) {
			return;
		}

		g2.setColor(GColor.DARK_GRAY);
		g2.setFont(getFontLine());
		g2.drawString(getXYscaleRatioString(), pos.x + 15, pos.y + 30);
	}

	/**
	 * @param g2
	 *            background graphics
	 */
	public abstract void paintBackground(GGraphics2D g2);

	/**
	 * Switches re-initing flag. If re-initing, also resets background.
	 * 
	 * @param reiniting
	 *            re-initing flag
	 */
	public void setReIniting(boolean reiniting) {
		reIniting = reiniting;
		if (reiniting) {
			firstPaint = true;
			resetBackgroundAndCache();
			openedComboBox = null;
		}
	}

	/**
	 * Paints content of this view.
	 * 
	 * @param g2
	 *            graphics
	 */
	public void paint(GGraphics2D g2) {
		synchronized (kernel.getConcurrentModificationLock()) {
			// synchronized means that no two Threads can simultaneously
			// enter any blocks locked by the same lock object,
			// but they can only wait for the active Thread to exit from
			// these blocks... as there is only one lock object and
			// these methods probably do not call other synchronized
			// code blocks, it probably does not cause any problem
			companion.paint(g2);
			getEuclidianController().getPen().repaintIfNeeded(g2);
		}
	}

	/**
	 * Allows platform specific drawing of preview lines for performance
	 * reasons.
	 * 
	 * @return new preview polyline drawing utility
	 */
	protected PenPreviewLine newPenPreview() {
		return new PenPreviewLine();
	}

	/**
	 * @param g2
	 *            graphics for background
	 */
	public void paintTheBackground(GGraphics2D g2) {
		// BACKGROUND
		// draw background image (with axes and/or grid)
		if (bgImage == null) {
			if (firstPaint) {
				if ((getWidth() > 1) && (getHeight() > 1) && (!reIniting)) {
					// only set firstPaint to false if the bgImage was generated
					companion.updateSizeKeepDrawables();
					paintBackground(g2);
					// g2.drawImage(bgImage, 0, 0, null);
					firstPaint = false;
				} else {
					drawBackgroundWithImages(g2);
				}
			} else {
				drawBackgroundWithImages(g2);
			}
		} else {
			paintBackground(g2);
		}
	}

	/**
	 * Updates background image
	 */
	final public void updateBackgroundImage() {
		if (bgGraphics != null) {
			drawBackgroundWithImages(bgGraphics, false);
		}
	}

	/**
	 * Draw MOW background;
	 * 
	 * @param g2
	 *            {@link GGraphics2D}
	 */
	public void paintMOWBackround(GGraphics2D g2) {
		if (!(app.isWhiteboardActive() && settings != null)) {
			return;
		}

		if (drawBg == null) {
			drawBg = new DrawBackground(this, settings);
		}
		drawBg.draw(g2);
	}

	/**
	 * Overridden for web
	 * 
	 * @return svg background
	 */
	public MyImage getSVGBackground() {
		return null;
	}

	/**
	 * Draws zoom rectangle
	 * 
	 * @param g2
	 *            graphics
	 */
	protected void drawZoomRectangle(GGraphics2D g2) {
		g2.setColor(colZoomRectangleFill);
		g2.setStroke(boldAxesStroke);
		g2.fill(selectionRectangle);
		g2.setColor(colZoomRectangle);
		g2.draw(selectionRectangle);
	}

	/**
	 * Draws preview of shape for ShapeTools
	 * 
	 * @param g2
	 *            - graphics
	 * @param fillCol
	 *            - filling color of shape
	 * @param objCol
	 *            - color of line of shape
	 * @param stroke
	 *            - stroke of shape
	 * @param shape
	 *            - shape to draw
	 */
	protected void drawShape(GGraphics2D g2, GColor fillCol, GColor objCol,
			GBasicStroke stroke, GShape shape) {
		g2.setColor(fillCol);
		g2.setStroke(stroke);
		g2.fill(shape);
		g2.setColor(objCol);
		if (!isRounded) {
			g2.draw(shape);
		} else {
			// rectangle with rounded edges
			g2.drawRoundRect((int) Math.round(((GRectangle) shape).getX()),
					(int) Math.round(((GRectangle) shape).getY()),
					(int) Math.round(((GRectangle) shape).getWidth()),
					(int) Math.round(((GRectangle) shape).getHeight()), 20, 20);
		}
	}

	protected void drawShape(GGraphics2D g2, GShape shape) {
		if (shape != null) {
			drawShape(g2, shapeFillCol, shapeObjCol,
					shapeStroke, shape);
		}
	}

	/**
	 * Draws rectangle with given options
	 * 
	 * @param g2
	 *            graphics
	 * @param col
	 *            color for stroke
	 * @param stroke
	 *            stroke to use
	 * @param rect
	 *            rectangle to draw
	 */
	protected void drawRect(GGraphics2D g2, GColor col, GBasicStroke stroke,
			GRectangle rect) {
		g2.setColor(col);
		g2.setStroke(stroke);
		g2.draw(rect);
	}

	/**
	 * Draws mouse coords next to the mouse
	 * 
	 * @param g2
	 *            graphics
	 */
	final protected void drawMouseCoords(GGraphics2D g2) {
		StringTemplate tpl = StringTemplate.defaultTemplate;
		if (euclidianController.mouseLoc == null) {
			return;
		}
		StringBuilder sb = new StringBuilder();
		sb.setLength(0);
		sb.append('(');
		sb.append(kernel.format(
				DoubleUtil.checkDecimalFraction(euclidianController.xRW), tpl));
		if (kernel.getCoordStyle() == Kernel.COORD_STYLE_AUSTRIAN) {
			sb.append(" | ");
		} else {
			sb.append(", ");
		}
		sb.append(kernel.format(
				DoubleUtil.checkDecimalFraction(euclidianController.yRW), tpl));
		sb.append(')');

		g2.setColor(GColor.DARK_GRAY);
		g2.setFont(getFontCoords());
		GPoint pos = euclidianController.mouseLoc;
		g2.drawString(sb.toString(), pos.x + 15, pos.y + 15);
	}

	/**
	 * @param g
	 *            background graphics
	 */
	protected void drawBackgroundWithImages(GGraphics2D g) {
		drawBackgroundWithImages(g, false);
	}

	/**
	 * Draws axes, grid and background images
	 * 
	 * @param g
	 *            graphics
	 * @param clear
	 *            clear traces before drawing
	 */
	final protected void drawBackground(GGraphics2D g, boolean clear) {
		if (clear) {
			clearBackground(g);
		}

		g.setAntialiasing();

		// handle drawing axes near the screen edge
		if (drawBorderAxes[0] || drawBorderAxes[1]) {

			// edge axes are not drawn at the exact edge, instead they
			// are inset enough to draw the labels
			// labelOffset = amount of space needed to draw labels
			GPoint labelOffset = getMaximumLabelSize(g);

			// force the the axisCross position to be near the edge
			if (drawBorderAxes[0]) {
				axisCross[0] = getYmin() + ((labelOffset.y + 10) / getYscale());
			}
			if (drawBorderAxes[1]) {
				axisCross[1] = getXmin() + ((labelOffset.x + 10) / getXscale());
			}
		}

		// reset axes label positions
		axesLabelsPositionsX.clear();

		yLabelMaxWidthPos = 0;
		yLabelMaxWidthNeg = 0;
		xLabelHeights = estimateNumberHeight(getFontAxes());

		if (showGrid) {
			drawGrid(g);
		}
		paintMOWBackround(g);

		// this will fill axesLabelsBounds with the rectangles where the axes
		// labels are
		if (showAxes[0] || showAxes[1]) {
			if (da == null) {
				da = new DrawAxis(this);
			}
			da.drawAxes(g);
		}

		if (showResetIcon()) {
			drawResetIcon(g);
		}
	}

	boolean showResetIcon() {
		if (!getApplication().showResetIcon()
				|| !(getApplication().isApplet() || getApplication()
						.isHTML5Applet())
				|| app.isWhiteboardActive()) {
			return false;
		}
		return isPrimaryEV();
	}

	/**
	 * Draws grid
	 * 
	 * @param g2
	 *            graphics
	 */
	final protected void drawGrid(GGraphics2D g2) {
		if (drawGrid == null) {
			drawGrid = new DrawGrid(this);
		}
		// vars for handling positive-only axes
		double xCrossPix = getXZero() + (axisCross[1] * getXscale());
		double yCrossPix = getYZero() - (axisCross[0] * getYscale());

		// this needs to be after setClip()
		// bug in FreeHEP (PDF export)
		// #2683
		g2.setColor(gridColor);
		g2.setStroke(gridStroke);

		switch (gridType) {

		case GRID_CARTESIAN:

			drawGrid.drawCartesianGrid(g2, xCrossPix, yCrossPix, false);

			break;
		case GRID_CARTESIAN_WITH_SUBGRID:

			drawGrid.drawCartesianGrid(g2, xCrossPix, yCrossPix, true);

			break;

		case GRID_ISOMETRIC:
			drawIsometricGrid(g2, xCrossPix, yCrossPix);
			break;

		case GRID_POLAR: // G.Sturr 2010-8-13

			// find minimum grid radius
			double min;
			if ((getXZero() > 0) && (getXZero() < getWidth())
					&& (getYZero() > 0) && (getYZero() < getHeight())) {
				// origin onscreen: min = 0
				min = 0;
			} else {
				// origin offscreen: min = distance to closest screen border
				double minW = Math.min(Math.abs(getXZero()),
						Math.abs(getXZero() - getWidth()));
				double minH = Math.min(Math.abs(getYZero()),
						Math.abs(getYZero() - getHeight()));
				min = Math.min(minW, minH);
			}

			// find maximum grid radius
			// max = max distance of origin to screen corners
			double d1 = MyMath.length(getXZero(), getYZero()); // upper left
			double d2 = MyMath.length(getXZero(), getYZero() - getHeight()); // lower
																				// left
			double d3 = MyMath.length(getXZero() - getWidth(), getYZero()); // upper
																			// right
			double d4 = MyMath.length(getXZero() - getWidth(), getYZero()
					- getHeight()); // lower
			// right
			double max = Math.max(Math.max(d1, d2), Math.max(d3, d4));

			// draw the grid circles
			// note: x tick intervals are used for the radius intervals,
			// it is assumed that the x/y scaling ratio is 1:1
			double tickStepR = getXscale() * gridDistances[0];
			double r = min - (min % tickStepR);
			while (r <= max) {
				circle.setFrame(getXZero() - r, getYZero() - r, 2 * r, 2 * r);
				g2.draw(circle);
				r = r + tickStepR;

			}

			// draw the radial grid lines
			double angleStep = gridDistances[2];
			double y1,
			y2,
			m;

			// horizontal axis
			tempLine.setLine(0, getYZero(), getWidth(), getYZero());
			g2.draw(tempLine);

			// radial lines
			for (double a = angleStep; a < Math.PI; a = a + angleStep) {

				if (Math.abs(a - (Math.PI / 2)) < 0.0001) {
					// vertical axis
					tempLine.setLine(getXZero(), 0, getXZero(), getHeight());
				} else {
					m = Math.tan(a);
					y1 = (m * (getXZero())) + getYZero();
					y2 = (m * (getXZero() - getWidth())) + getYZero();
					tempLine.setLine(0, y1, getWidth(), y2);
				}
				g2.draw(tempLine);
			}

			break;
		}

	}

	// =================================================
	// Draw Axes
	// =================================================

	private void drawIsometricGrid(GGraphics2D g2, double xCrossPix,
			double yCrossPix) {
		boolean clipX = positiveAxes[1] && yCrossPix < getHeight();
		int yAxisEnd = clipX ? (int) yCrossPix : getHeight();
		boolean clipY = positiveAxes[0] && xCrossPix > 0;
		int xAxisStart = clipY ? (int) xCrossPix : 0;

		// set the clipping region to the region defined by the axes

		double tickStepX = getXscale() * gridDistances[0] * Math.sqrt(3.0);
		double startX = getXZero() % (tickStepX);
		double startX2 = getXZero() % (tickStepX / 2);
		double tickStepY = getYscale() * gridDistances[0];
		double startY = getYZero() % tickStepY;

		// vertical
		double pix = startX2;
		for (int j = 0; pix <= getWidth(); j++) {
			if ((!DoubleUtil.isEqual(pix, xCrossPix) || !showAxes[0])
					&& pix > xAxisStart - Kernel.MIN_PRECISION) {
				tempLine.setLine(pix, 0, pix, yAxisEnd);
				g2.draw(tempLine);
			}

			pix = startX2 + ((j * tickStepX) / 2.0);
		}
		// extra lines needed because it's diagonal
		int extra = (int) ((((getHeight() * getXscale()) / getYscale()) * Math
				.sqrt(3.0)) / tickStepX) + 3;

		// negative gradient
		pix = startX + (-(extra + 1) * tickStepX);
		for (int j = -extra; pix <= getWidth(); j += 1) {
			double endx = pix
					+ (((getHeight() + tickStepY) * Math.sqrt(3) * getXscale())
							/ getYscale());
			if (clipX || clipY) {
				DrawSegment.drawClipped(
						new double[] { pix, startY - tickStepY },
						new double[] { endx,
								(startY - tickStepY) + getHeight()
										+ tickStepY },
						tempLine, xAxisStart, getWidth(), 0, yAxisEnd,
						getTmpClipPoints());
			} else {
				tempLine.setLine(pix, startY - tickStepY, endx,
						startY + getHeight());
			}
			g2.draw(tempLine);
			pix = startX + (j * tickStepX);
		}

		// positive gradient
		pix = startX;
		for (int j = 0; pix <= (getWidth()
				+ ((((getHeight() * getXscale()) / getYscale()) + tickStepY)
						* Math.sqrt(3.0))); j += 1) {
			double endx = pix
					- (((getHeight() + tickStepY) * Math.sqrt(3) * getXscale())
							/ getYscale());
			if (clipX || clipY) {
				DrawSegment.drawClipped(
						new double[] { pix, startY - tickStepY },
						new double[] { endx,
								(startY - tickStepY) + getHeight()
										+ tickStepY },
						tempLine, xAxisStart, getWidth(), 0, yAxisEnd,
						getTmpClipPoints());
			} else {
				tempLine.setLine(pix, startY - tickStepY, endx,
						startY + getHeight());
			}
			g2.draw(tempLine);
			pix = startX + (j * tickStepX);
		}

	}

	private GPoint2D[] getTmpClipPoints() {
		if (tmpClipPoints == null) {
			tmpClipPoints = new GPoint2D[2];
			tmpClipPoints[0] = new GPoint2D();
			tmpClipPoints[1] = new GPoint2D();
		}

		return tmpClipPoints;
	}

	double getXAxisCrossingPixel() {
		return getXZero() + (axisCross[1] * getXscale());
	}

	double getYAxisCrossingPixel() {
		return getYZero() - (axisCross[0] * getYscale());
	}

	boolean xAxisOnscreen() {
		return showAxes[0] && (getYmin() < axisCross[0])
				&& (getYmax() > axisCross[0]);
	}

	boolean yAxisOnscreen() {
		return showAxes[1] && (getXmin() < axisCross[1])
				&& (getXmax() > axisCross[1]);
	}

	protected int getYOffsetForXAxis(int baseFontSize) {
		return baseFontSize + 4;
	}

	/**
	 * @param g
	 *            graphics for reset icon
	 */
	protected abstract void drawResetIcon(GGraphics2D g);

	/**
	 * Draw combos
	 * 
	 * @param g
	 *            graphics
	 */
	public void drawActionObjects(GGraphics2D g) {
		DrawDropDownList selected = null;
		DrawDropDownList opened = null;
		for (Drawable d : allDrawableList) {
			if (d instanceof DrawDropDownList) {
				DrawDropDownList dl = (DrawDropDownList) d;
				if (dl.needsUpdate()) {
					dl.setNeedsUpdate(false);
					dl.update();
				}

				if (selected == null && dl.isSelected()) {
					selected = dl;
				}

				if (opened == null && dl.isOptionsVisible()) {
					opened = dl;
				}

				dl.draw(g);
			} else if (d instanceof DrawInputBox) {

				if (d.needsUpdate()) {
					d.setNeedsUpdate(false);
					d.update();
				}

				d.draw(g);
			}
		}

		DrawDropDownList topDropDown = opened != null ? opened : selected;

		if (topDropDown != null) {
			topDropDown.draw(g);
		}
	}

	/**
	 * Draw masks
	 *
	 * @param g2
	 *            graphics
	 */
	public void drawMasks(GGraphics2D g2) {
		for (Drawable d : allDrawableList) {
			if (d.geo.isMask()) {
				d.updateIfNeeded();
				d.draw(g2);
			}
		}
	}

	/**
	 * Switch antialiasing to true for given graphics
	 * 
	 * @param g2
	 *            graphics
	 */
	final public static void setAntialiasing(GGraphics2D g2) {
		g2.setAntialiasing();
	}

	/**
	 * @param g2
	 *            graphics for the animation button
	 */
	protected void drawAnimationButtons(GGraphics2D g2) {
		// it could be abstract, but mess with EuclididanView3D
	}

	@Override
	public abstract void setBackground(GColor bgColor);

	/**
	 * Update stylebar from settings
	 * 
	 * @param evs
	 *            settings
	 */
	protected void synchronizeMenuBarAndEuclidianStyleBar(EuclidianSettings evs) {
		if (styleBar != null) {
			getStyleBar().updateButtonPointCapture(evs.getPointCapturingMode());
		}
		// Actually nothing to do in Menubar since we do not have any EV
		// settings there anymore
	}

	/**
	 * @param preferredSize
	 *            prefered size
	 */
	public abstract void setPreferredSize(GDimension preferredSize);

	@Override
	public boolean showGrid(boolean show) {
		return companion.showGrid(show);
	}

	@Override
	public void setGridIsBold(boolean gridIsBold) {
		if (this.gridIsBold == gridIsBold) {
			return;
		}

		this.gridIsBold = gridIsBold;
		setGridLineStyle(gridLineStyle);

		updateBackgroundImage();
	}

	@Override
	public void setGridColor(GColor gridColor) {
		if (gridColor != null) {
			this.gridColor = gridColor;
		}
	}

	@Override
	public void setGridLineStyle(int gridLineStyle) {
		this.gridLineStyle = gridLineStyle;
		gridStroke = EuclidianStatic.getStroke(gridIsBold ? 2f : 1f,
				gridLineStyle);
	}

	/**
	 * @param settings
	 *            settings
	 */
	@Override
	public void settingsChanged(AbstractSettings settings) {
		companion.settingsChanged(settings);

		if (styleBar != null) {
			styleBar.updateGUI();
		}
	}

	@Override
	public EuclidianSettings getSettings() {
		return this.settings;
	}

	@Override
	public void updateCursor(GeoPointND point) {
		// used in 3D
	}

	@Override
	public GRectangle getSelectionRectangle() {
		return selectionRectangle;
	}

	/**
	 * @return boundingBox
	 */
	public BoundingBox<? extends GShape> getBoundingBox() {
		return boundingBox;
	}

	/**
	 *
	 * @return mask
	 */
	public GRectangle getMaskPreview() {
		return maskPreview;
	}

	/**
	 * @return shapePolygon
	 */
	public GGeneralPath getShapePolygon() {
		return shapePolygon;
	}

	/**
	 * @return true if shape is rounded (e.g. for ShapeRectangleRoundEdges)
	 */
	public boolean isRounded() {
		return isRounded;
	}

	/**
	 * @param isRounded
	 *            - true if shape is rounded (e.g. for ShapeRectangleRoundEdges)
	 */
	public void setRounded(boolean isRounded) {
		this.isRounded = isRounded;
	}

	/**
	 * @return path along border of this view
	 */
	public GeneralPathClipped getBoundingPath() {
		GeneralPathClipped gs = new GeneralPathClipped(this);
		gs.moveTo(getMinXScreen(), getMinYScreen());
		gs.lineTo(getMaxXScreen(), getMinYScreen());
		gs.lineTo(getMaxXScreen(), getMaxYScreen());
		gs.lineTo(getMinXScreen(), getMaxYScreen());
		gs.lineTo(getMinXScreen(), getMinYScreen());
		gs.closePath();
		return gs;
	}

	/**
	 * @param img
	 *            new background image
	 */
	final public void addBackgroundImage(DrawImage img) {
		bgImageList.add(img);
		allDrawableList.remove(img);
	}

	/**
	 * @param img
	 *            background image
	 */
	final public void removeBackgroundImage(DrawImage img) {
		bgImageList.remove(img);
		allDrawableList.add(img);
	}

	/**
	 * Reset lists of drawables
	 */
	protected void resetLists() {
		drawableMap.clear();
		stickyPointList.clear();
		allDrawableList.clear();
		bgImageList.clear();
		previewFromInputBarGeos = null;
		this.geosWaiting.clear();

		setToolTipText(null);
	}

	/**
	 * Returns the bounding box of all Drawable objects in this view in screen
	 * coordinates.
	 * 
	 * @return bounds of this view
	 */
	public GRectangle getBounds() {
		GRectangle result = null;

		for (Drawable d : allDrawableList) {
			GRectangle bb = d.getBounds();
			if (bb != null) {
				if (result == null) {
					// changed () to (bb) bugfix, otherwise top-left of screen
					// is always included
					result = AwtFactory.getPrototype().newRectangle(bb);
				}
				// add bounding box of list element
				result.add(bb);
			}
		}

		// Cong Liu
		if (result == null) {
			result = AwtFactory.getPrototype().newRectangle(0, 0, 0, 0);
		}
		return result;
	}

	@Override
	public void setPreview(Previewable p) {
		if (previewDrawable != null) {
			previewDrawable.disposePreview();
		}
		previewDrawable = p;
	}

	public boolean wantsUpdatePreviewForTouchStartPhone(int mode) {
        return getPreviewDrawable() == null && mode != EuclidianConstants.MODE_COPY_VISUAL_STYLE;
    }

	/**
	 * Finds maximum pixel width and height needed to draw current x and y axis
	 * labels. return[0] = max width, return[1] = max height
	 * 
	 * @param g2
	 *            graphics
	 * @return point (width,height)
	 */

	public GPoint getMaximumLabelSize(GGraphics2D g2) {
		GPoint max = new GPoint(0, 0);

		g2.setFont(getFontAxes());

		int yAxisHeight = positiveAxes[1] ? (int) getYZero() : getHeight();
		int maxY = positiveAxes[1] ? (int) getYZero() : getHeight()
				- SCREEN_BORDER;

		double rw = getYmax() - (getYmax() % axesNumberingDistances[1]);
		double pix = getYZero() - (rw * getYscale());
		double axesStep = getYscale() * axesNumberingDistances[1]; // pixelstep

		axesNumberingDistances[1] = DoubleUtil
				.checkDecimalFraction(axesNumberingDistances[1]);

		int count = 0;
		double rwBase = DoubleUtil.checkDecimalFraction(rw);

		// for (; pix <= yAxisHeight; rw -= axesNumberingDistances[1], pix +=
		// axesStep) {
		for (; pix <= yAxisHeight; count++, pix += axesStep) {

			// 285, 285.1, 285.2 -> rounding problems
			rw = rwBase
					- DoubleUtil.checkDecimalFraction(axesNumberingDistances[1]
							* count);

			if (pix <= maxY) {
				if (showAxesNumbers[1]) {
					String strNum = kernel.formatPiE(rw, axesNumberFormat[1],
							StringTemplate.defaultTemplate);
					StringBuilder sb = new StringBuilder();
					sb.setLength(0);
					sb.append(strNum);
					if ((axesUnitLabels[1] != null) && !piAxisUnit[1]) {
						sb.append(axesUnitLabels[1]);
					}
					double width = estimateTextWidth(sb.toString(),
							getFontAxes());
					if (max.x < width) {
						max.x = (int) width;
					}
					if (max.y == 0) {
						max.y = (int) estimateNumberHeight(getFontAxes());
					}
				}
			}
		}
		return max;
	}

	/**
	 * change showing flag of the axis
	 * 
	 * @param axis
	 *            id of the axis
	 * @param flag
	 *            show/hide
	 * @param update
	 *            update (or not) the background image
	 */
	@Override
	public boolean setShowAxis(int axis, boolean flag, boolean update) {
		if (flag == showAxes[axis]) {
			return false;
		}

		showAxes[axis] = flag;

		if (update) {
			updateBackgroundImage();
		}
		return true;
	}

	@Override
	public boolean setShowAxes(boolean flag, boolean update) {
		boolean changedX = setShowAxis(AXIS_X, flag, false);
		return setShowAxis(AXIS_Y, flag, true) || changedX;
	}

	/*
	 * change logarithmic flag of the axes
	 */

	@Override
	public boolean setLogAxis(int axis, boolean flag, boolean update) {
		if (flag == logAxes[axis]) {
			return false;
		}

		logAxes[axis] = flag;

		if (update) {
			updateBackgroundImage();
		}
		return true;
	}

	/**
	 * @param bold
	 *            true for bold axes
	 */
	public void setBoldAxes(boolean bold) {
		axesLineType = getBoldAxes(bold, axesLineType);
	}

	/**
	 * Tells if there are any traces in the background image.
	 * 
	 * @return true if there are any traces in background
	 */
	protected boolean isTracing() {
		for (Drawable drawable : allDrawableList) {
			if (drawable.isTracing()) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Tells if there are any images in the background.
	 * 
	 * @return whether there are any images in the background.
	 */
	protected boolean hasBackgroundImages() {
		return bgImageList.size() > 0;
	}

	/**
	 * @return background graphics
	 */
	final public GGraphics2D getBackgroundGraphics() {
		this.tracing = true;
		return bgGraphics;
	}

	/**
	 * returns settings in XML format
	 * 
	 * @param sbxml
	 *            string builder
	 * @param asPreference
	 *            true for preferences
	 */
	@Override
	public void getXML(StringBuilder sbxml, boolean asPreference) {
		companion.getXML(sbxml, asPreference);
	}

	/**
	 * start settings in XML format
	 * 
	 * @param sbxml
	 *            string builder
	 * @param asPreference
	 *            true for preferences
	 */
	public void startXML(StringBuilder sbxml, boolean asPreference) {
		StringTemplate tpl = StringTemplate.xmlTemplate;
		sbxml.append("<euclidianView>\n");

		companion.getXMLid(sbxml);

		int width = getWidth();
		int height = getHeight();
		if ((width <= MIN_WIDTH) && (height <= MIN_HEIGHT)) {
			EuclidianSettings evSettings = getSettings();
			if (evSettings != null) {
				width = evSettings.getWidth();
				height = evSettings.getHeight();
			}
		}

		if ((width > MIN_WIDTH) && (height > MIN_HEIGHT)) {
			sbxml.append("\t<size ");
			sbxml.append(" width=\"");
			sbxml.append(width);
			sbxml.append("\"");
			sbxml.append(" height=\"");
			sbxml.append(height);
			sbxml.append("\"");
			sbxml.append("/>\n");
		}
		if (!isZoomable() && !asPreference) {
			sbxml.append("\t<coordSystem");
			sbxml.append(" xMin=\"");
			StringUtil
					.encodeXML(sbxml, ((GeoNumeric) xminObject).getLabel(tpl));
			sbxml.append("\"");
			sbxml.append(" xMax=\"");
			StringUtil
					.encodeXML(sbxml, ((GeoNumeric) xmaxObject).getLabel(tpl));
			sbxml.append("\"");
			sbxml.append(" yMin=\"");
			StringUtil
					.encodeXML(sbxml, ((GeoNumeric) yminObject).getLabel(tpl));
			sbxml.append("\"");
			sbxml.append(" yMax=\"");
			StringUtil
					.encodeXML(sbxml, ((GeoNumeric) ymaxObject).getLabel(tpl));
			sbxml.append("\"");
			sbxml.append("/>\n");
		} else {
			sbxml.append("\t<coordSystem");
			sbxml.append(" xZero=\"");
			sbxml.append(getXZero());
			sbxml.append("\"");
			sbxml.append(" yZero=\"");
			sbxml.append(getYZero());
			sbxml.append("\"");
			sbxml.append(" scale=\"");
			sbxml.append(getXscale());
			sbxml.append("\"");
			sbxml.append(" yscale=\"");
			sbxml.append(getYscale());
			sbxml.append("\"");
			sbxml.append("/>\n");
		}
		// NOTE: the attribute "axes" for the visibility state of
		// both axes is no longer needed since V3.0.
		// Now there are special axis tags, see below.
		sbxml.append("\t<evSettings axes=\"");
		sbxml.append(showAxes[0] || showAxes[1]);
		sbxml.append("\" grid=\"");
		sbxml.append(showGrid);
		sbxml.append("\" gridIsBold=\""); //
		sbxml.append(gridIsBold); // Michael Borcherds 2008-04-11
		sbxml.append("\" pointCapturing=\"");

		// make sure POINT_CAPTURING_STICKY_POINTS isn't written to XML
		sbxml.append(
				getPointCapturingMode() > EuclidianStyleConstants.POINT_CAPTURING_XML_MAX
						? EuclidianStyleConstants.POINT_CAPTURING_DEFAULT
				: getPointCapturingMode());

		sbxml.append("\" rightAngleStyle=\"");
		sbxml.append(getApplication().rightAngleStyle);
		if (asPreference) {
			sbxml.append("\" allowShowMouseCoords=\"");
			sbxml.append(getAllowShowMouseCoords());

			sbxml.append("\" allowToolTips=\"");
			sbxml.append(getAllowToolTips());

			sbxml.append("\" deleteToolSize=\"");
			sbxml.append(getEuclidianController().getDeleteToolSize());
		}

		// checkbox size 13 deprecated
		sbxml.append("\" checkboxSize=\"26\" gridType=\"");
		sbxml.append(getGridType()); // cartesian/isometric/polar

		if (lockedAxesRatio > 0) {
			sbxml.append("\" lockedAxesRatio=\"");
			sbxml.append(lockedAxesRatio);
		}

		sbxml.append("\"/>\n");

		// background color
		sbxml.append("\t<bgColor");
		XMLBuilder.appendRGB(sbxml, getBackgroundCommon());
		sbxml.append("/>\n");

		// axes color
		sbxml.append("\t<axesColor");
		XMLBuilder.appendRGB(sbxml, axesColor);
		sbxml.append("/>\n");

		// grid color
		sbxml.append("\t<gridColor");
		XMLBuilder.appendRGB(sbxml, gridColor);
		sbxml.append("/>\n");

		int rulerType = settings.getBackgroundType().value();
		if (app.isWhiteboardActive()) {
			sbxml.append("\t<rulerType val=\"");
			sbxml.append(rulerType);
			sbxml.append("\" bold=\"");
			sbxml.append(settings.isRulerBold());
			sbxml.append("\"/>\n");

			// ruler color
			GColor rulerColor = settings.getBgRulerColor();
			if (!GColor.MOW_RULER.equals(rulerColor)) {
				sbxml.append("\t<rulerColor");
				XMLBuilder.appendRGB(sbxml, rulerColor);
				sbxml.append("/>\n");
			}

			if (app.getSaveController().savedAsTemplate()) {
				// size of pen
				sbxml.append("\t<penSize val=\"");
				sbxml.append(settings.getLastPenThickness());
				sbxml.append("\"/>\n");

				// color of pen
				sbxml.append("\t<penColor");
				XMLBuilder.appendRGB(sbxml, settings.getLastSelectedPenColor());
				sbxml.append("/>\n");

				// size of highlighter
				sbxml.append("\t<highlighterSize val=\"");
				sbxml.append(settings.getLastHighlighterThinckness());
				sbxml.append("\"/>\n");

				// highlighter of pen
				sbxml.append("\t<highlighterColor");
				XMLBuilder.appendRGB(sbxml, settings.getLastSelectedHighlighterColor());
				sbxml.append("/>\n");

				// size of eraser
				sbxml.append("\t<eraserSize val=\"");
				sbxml.append(settings.getDeleteToolSize());
				sbxml.append("\"/>\n");

				sbxml.append("\t<language val=\"");
				sbxml.append(app.getLocalization().getLocaleStr());
				sbxml.append("\"/>\n");
			}
		}
		// axes line style
		sbxml.append("\t<lineStyle axes=\"");
		sbxml.append(axesLineType);
		sbxml.append("\" grid=\"");
		sbxml.append(gridLineStyle);
		int rulerLineStyle = settings.getRulerLineStyle();
		if (app.isWhiteboardActive()) {
			sbxml.append("\" ruler=\"");
			sbxml.append(rulerLineStyle);
		}

		sbxml.append("\"/>\n");

		// axes label style
		int style = getSettings().getAxisFontStyle();
		boolean serif = getSettings().getAxesLabelsSerif();
		if (style != GFont.PLAIN || serif) {
			sbxml.append("\t<labelStyle axes=\"");
			sbxml.append(style);
			sbxml.append("\"");

			sbxml.append(" serif=\"");
			sbxml.append(serif);
			sbxml.append("\"");

			sbxml.append("/>\n");
		}

		// axis settings
		for (int i = 0; i < 2; i++) {
			getSettings().addAxisXML(i, sbxml);
		}

		// grid distances
		if (!automaticGridDistance) {
			sbxml.append("\t<grid distX=\"");
			sbxml.append(gridDistances[0]);
			sbxml.append("\" distY=\"");
			sbxml.append(gridDistances[1]);
			sbxml.append("\" distTheta=\"");
			// polar angle step added in v4.0
			sbxml.append(gridDistances[2]);
			sbxml.append("\"/>\n");
		}

	}

	/**
	 * end settings in XML format
	 * 
	 * @param sbxml
	 *            string builder
	 */
	public void endXML(StringBuilder sbxml) {
		sbxml.append("</euclidianView>\n");
	}

	/**
	 * Keeps the zoom, but makes sure the bound objects are free. This is
	 * necessary in File->New because there might have been dynamic xmin bounds
	 */
	public void resetXYMinMaxObjects() {
		if ((evNo == 1) || (evNo == 2)) {
			EuclidianSettings es = getApplication().getSettings().getEuclidian(
					evNo);

			GeoNumeric xmao = new GeoNumeric(kernel.getConstruction(),
					xmaxObject.getNumber().getDouble());
			GeoNumeric xmio = new GeoNumeric(kernel.getConstruction(),
					xminObject.getNumber().getDouble());
			GeoNumeric ymao = new GeoNumeric(kernel.getConstruction(),
					ymaxObject.getNumber().getDouble());
			GeoNumeric ymio = new GeoNumeric(kernel.getConstruction(),
					yminObject.getNumber().getDouble());
			es.setXmaxObject(xmao, false);
			es.setXminObject(xmio, false);
			es.setYmaxObject(ymao, false);
			es.setYminObject(ymio, true);
		}
	}

	/**
	 * Change coord system so that all objects are shown
	 *
	 * @param storeUndo
	 *            true to store undo after
	 * @param keepRatio
	 *            true to keep ratio of x and y axes
	 */
	@Override
	public void setViewShowAllObjects(boolean storeUndo, boolean keepRatio) {
		setViewShowAllObjects(storeUndo, keepRatio, 10);
	}

	/**
	 * @param storeUndo
	 *            whether to store undo afterwards
	 * @param keepRatio
	 *            whether to keep axes ratio
	 * @param steps
	 *            animation steps
	 */
	public void setViewShowAllObjects(boolean storeUndo, boolean keepRatio,
			int steps) {

		// check for functions
		TreeSet<GeoElement> allFunctions = kernel.getConstruction()
				.getGeoSetLabelOrder(GeoClass.FUNCTION);
		boolean hasFunctions = hasVisibleObjects(allFunctions);

		// check for curves
		TreeSet<GeoElement> allCurves = kernel.getConstruction()
				.getGeoSetLabelOrder(GeoClass.CURVE_CARTESIAN);
		boolean hasCurves = hasVisibleObjects(allCurves);

		// check for points, circles etc.
		GRectangle rect = getBounds();
		boolean hasObjects = hasVisibleObjects(rect);

		if (!hasObjects && !hasCurves && !hasFunctions) {
			return;
		}

		/** curves */

		double xMinCurve = Double.MAX_VALUE;
		double xMaxCurve = -Double.MAX_VALUE;
		double yMinCurve = Double.MAX_VALUE;
		double yMaxCurve = -Double.MAX_VALUE;

		if (hasCurves) {
			for (GeoElement element : allCurves) {
				GeoCurveCartesian curve = (GeoCurveCartesian) element;
				Function funX = curve.getFunX();
				Function funY = curve.getFunY();

				if (funX != null && funY != null) {

					double min = curve.getMinParameter();
					double max = curve.getMaxParameter();
					double step = curve.getAnimationStep();

					xMinCurve = funX.value(min);
					xMaxCurve = xMinCurve;
					yMinCurve = funY.value(min);
					yMaxCurve = yMinCurve;

					double helper;

					while (min < max) {
						min += step;
						helper = funX.value(min);
						if (helper < xMinCurve) {
							xMinCurve = helper;
						} else if (helper > xMaxCurve) {
							xMaxCurve = helper;
						}
						helper = funY.value(min);
						if (helper < yMinCurve) {
							yMinCurve = helper;
						} else if (helper > yMaxCurve) {
							yMaxCurve = helper;
						}
					}
				}
			}
		}

		/** objects */

		double xMinObj = Double.MAX_VALUE;
		double xMaxObj = -Double.MAX_VALUE;
		double yMinObj = Double.MAX_VALUE;
		double yMaxObj = -Double.MAX_VALUE;

		if (hasObjects) {
			// get bounds of points, circles etc
			xMinObj = toRealWorldCoordX(rect.getMinX());
			xMaxObj = toRealWorldCoordX(rect.getMaxX());
			yMinObj = toRealWorldCoordY(rect.getMaxY());
			yMaxObj = toRealWorldCoordY(rect.getMinY());
		}

		/**
		 * initialize the rectangle around all visible objects do this before
		 * handling functions, because functions need the calculated x-values
		 */

		// xMin,xMax,yMin,yMax of all objects on graphics view
		double x0RW = Double.MAX_VALUE;
		double x1RW = -Double.MAX_VALUE;
		double y0RW = Double.MAX_VALUE;
		double y1RW = -Double.MAX_VALUE;

		if (hasCurves) {
			x0RW = Math.min(x0RW, xMinCurve);
			x1RW = Math.max(x1RW, xMaxCurve);
			y0RW = Math.min(y0RW, yMinCurve);
			y1RW = Math.max(y1RW, yMaxCurve);
		}

		if (hasObjects) {
			x0RW = Math.min(x0RW, xMinObj);
			x1RW = Math.max(x1RW, xMaxObj);
			y0RW = Math.min(y0RW, yMinObj);
			y1RW = Math.max(y1RW, yMaxObj);
		}

		/** functions */

		double yMinFunc = Double.MAX_VALUE;
		double yMaxFunc = -Double.MAX_VALUE;
		boolean ok = false;

		if (hasFunctions) {

			// if there are functions we don't want to zoom in horizontally
			x0RW = Math.min(getXmin(), x0RW);
			x1RW = Math.max(getXmax(), x1RW);

			for (GeoElement elem : allFunctions) {

				GeoFunction fun = (GeoFunction) elem;

				if (fun.isEuclidianVisible()) {
					double abscissa;
					// check 100 random heights
					for (int i = 0; i < 200; i++) {

						if (i == 0) {
							abscissa = fun.value(x0RW); // check far left
						} else if (i == 1) {
							abscissa = fun.value(x1RW); // check far right
						} else {
							abscissa = fun.value(
									x0RW
									+ (Math.random() * (x1RW - x0RW)));
						}

						if (!Double.isInfinite(abscissa)
								&& !Double.isNaN(abscissa)) {
							ok = true;
							if (abscissa > yMaxFunc) {
								yMaxFunc = abscissa;
							}
							// no else: there **might** be just one value
							if (abscissa < yMinFunc) {
								yMinFunc = abscissa;
							}
						}
					}
				}
			}
		}

		if (hasFunctions && ok) {
			y0RW = Math.min(y0RW, yMinFunc);
			y1RW = Math.max(y1RW, yMaxFunc);
		}

		// don't want objects at edge
		double xGap = (x1RW - x0RW) * 0.03;
		double yGap = (y1RW - y0RW) * 0.03;

		x0RW -= xGap;
		x1RW += xGap;
		y0RW -= yGap;
		y1RW += yGap;

		// enlarge x/y if we want to keep ratio
		if (keepRatio) {
			double oldRatio = (xmax - xmin) / (ymax - ymin);
			double newRatio = (x1RW - x0RW) / (y1RW - y0RW);
			if (newRatio > oldRatio) {
				// enlarge y
				double center = (y1RW + y0RW) / 2;
				double delta = (y1RW - y0RW) / 2;
				y0RW = center - delta * newRatio / oldRatio;
				y1RW = center + delta * newRatio / oldRatio;
			} else {
				// enlarge x
				double center = (x1RW + x0RW) / 2;
				double delta = (x1RW - x0RW) / 2;
				x0RW = center - delta * oldRatio / newRatio;
				x1RW = center + delta * oldRatio / newRatio;
			}
		}

		// check if animation is needed
		if (steps == 0) {
			setRealWorldCoordSystem(x0RW, x1RW, y0RW, y1RW);
			if (storeUndo) {
				getApplication().storeUndoInfo();
			}
		} else {
			setAnimatedRealWorldCoordSystem(x0RW, x1RW, y0RW, y1RW, steps,
					storeUndo);
		}
	}

	private static boolean hasVisibleObjects(GRectangle rect) {
		return !(DoubleUtil.isZero(rect.getHeight()) || DoubleUtil.isZero(rect
				.getWidth()));
	}

	/**
	 * @return {@code true} if an object of the given TreeSet is visible in the
	 *         graphicsView
	 */
	private static boolean hasVisibleObjects(TreeSet<GeoElement> allFunctions) {
		for (GeoElement element : allFunctions) {
			if (element.isEuclidianVisible()) {
				return true;
			}
		}
		return false;
	}

	/**
	 * @return width of selection rectangle
	 */
	public int getSelectedWidth() {
		if (selectionRectangle == null) {
			return getWidth();
		}
		return (int) selectionRectangle.getWidth();
	}

	/**
	 * @return height of selection rectangle
	 */
	public int getSelectedHeight() {
		if (selectionRectangle == null) {
			return getHeight();
		}
		return (int) selectionRectangle.getHeight();
	}

	public int getSelectedWidthInPixels() {
		return getSelectedWidth();
	}

	public int getSelectedHeightInPixels() {
		return getSelectedHeight();
	}

	/**
	 * @return export width in pixels (or Export width in pixels if appropriate)
	 */
	public int getWidthCheckExport() {
		if (app.isExporting()) {
			return getExportWidth();
		}

		return getWidth();
	}

	/**
	 * @return export height in pixels (or Export height in pixels if
	 *         appropriate)
	 */
	public int getHeightCheckExport() {
		if (app.isExporting()) {
			return getExportHeight();
		}

		return getHeight();
	}

    /**
	 * default frame, for when no custom frame for exporting taking place
	 */
	private GRectangle getDefaultFrame() {
		if (tempFrame == null) {
			tempFrame = AwtFactory.getPrototype().newRectangle(0, 0, getWidth(),
					getHeight());
		} else {
			tempFrame.setBounds(0, 0, getWidth(), getHeight());
		}
		return tempFrame;

	}

	/**
	 * @return drawing frame (component or virtual in case of export)
	 */
	public GRectangle getFrame() {
		if (!app.isExporting()) {
			return getDefaultFrame();
		}

		if (selectionRectangle != null) {
			return selectionRectangle;
		}

		double[] exportCoords = getExportCoords();
		if (exportCoords == null) {
			return getDefaultFrame();
		}

		double x1 = exportCoords[0];
		double y1 = exportCoords[1];
		double x2 = exportCoords[2];
		double y2 = exportCoords[3];

		return AwtFactory.getPrototype().newRectangle((int) x1, (int) y1,
				(int) (x2 - x1), (int) (y2 - y1));
	}

	/**
	 * 
	 * @return {minX, minY, maxX, maxY} if Export_1 and Export_2 exist,
	 *         otherwise null
	 */
	private double[] getExportCoords() {
		GeoElement geo1 = kernel.lookupLabel(EXPORT1);
		GeoElement geo2 = kernel.lookupLabel(EXPORT2);

		if (geo1 instanceof GeoPoint && geo2 instanceof GeoPoint) {
			GeoPoint export1 = (GeoPoint) geo1;
			GeoPoint export2 = (GeoPoint) geo2;

			double[] ret = new double[4];
			double[] xy1 = new double[2];
			double[] xy2 = new double[2];

			export1.getInhomCoords(xy1);
			export2.getInhomCoords(xy2);
			ret[0] = toScreenCoordXd(Math.min(xy1[0], xy2[0]));
			ret[1] = toScreenCoordYd(Math.max(xy1[1], xy2[1]));
			ret[2] = toScreenCoordXd(Math.max(xy1[0], xy2[0]));
			ret[3] = toScreenCoordYd(Math.min(xy1[1], xy2[1]));

			return ret;
		}

		return null;
	}

	/**
	 * @return export width in pixels
	 */
	public int getExportWidth() {
		if (selectionRectangle != null) {
			return (int) selectionRectangle.getWidth();
		}
		double[] exportCoords = getExportCoords();

		if (exportCoords != null) {
			double x1 = exportCoords[0];
			double x2 = exportCoords[2];
			return (int) (x2 - x1 + 2);
		}

		return getWidth();
	}

	/**
	 * @return export height in pixels
	 */
	public int getExportHeight() {
		if (selectionRectangle != null) {
			return (int) selectionRectangle.getHeight();
		}

		double[] exportCoords = getExportCoords();

		if (exportCoords != null) {
			double y1 = exportCoords[1];
			double y2 = exportCoords[3];

			return (int) (y2 - y1 + 2);
		}

		return getHeight();
	}

	// for use in AlgebraController
	@Override
	final public void clickedGeo(GeoElement geo, boolean isControlDown) {
		if (geo == null) {
			return;
		}

		tempArrayList.clear();
		tempArrayList.add(geo);

		AsyncOperation<Boolean> callback = new AsyncOperation<Boolean>() {
			@Override
			public void callback(Boolean arg) {
				if (arg.equals(true)) {
					euclidianController.storeUndoInfo();
				}
			}
		};
		boolean changedKernel = euclidianController.processMode(tempArrayList,
				isControlDown, callback);

		if (changedKernel) {
			getEuclidianController().storeUndoInfo();
		}
		kernel.notifyRepaint();
	}

	/**
	 * instantiate new zoomer
	 * 
	 * @return zoomer
	 */
	protected abstract CoordSystemAnimation newZoomer();

	/**
	 * Zooms around fixed point (px, py)
	 */
	@Override
	public void zoom(double px, double py, double zoomFactor, int steps,
			boolean storeUndo) {
		if (!isZoomable()) {
			return;
		}
		if (zoomer == null) {
			zoomer = newZoomer();
		}
		zoomer.init(px, py, zoomFactor, steps, storeUndo);
		zoomer.startAnimation();

	}

	/**
	 * Zooms towards the given axes scale ratio. Note: Only the y-axis is
	 * changed here. newRatioX / newRatioY = yscale / xscale;
	 * 
	 * @param newRatioX
	 *            axis ratio numerator
	 * @param newRatioY
	 *            axis ratio denominator
	 * @param storeUndo
	 *            true to store undo step after
	 */
	@Override
	public final void zoomAxesRatio(double newRatioX, double newRatioY,
			boolean storeUndo) {
		if (!isZoomable()) {
			return;
		}
		if (isLockedAxesRatio()) {
			return;
		}
		if (axesRatioZoomer == null) {
			axesRatioZoomer = newZoomer();
		}
		axesRatioZoomer.initAxes(newRatioX, newRatioY, storeUndo);
		axesRatioZoomer.startAnimation();
	}

	/**
	 * @return true if axes are standard positioned
	 */
	public boolean isZeroStandard() {
		return DoubleUtil.isEqual(xZero, getXZeroStandard())
				&& DoubleUtil.isEqual(yZero, getYZeroStandard());
	}

	/**
	 * Restores standard zoom + origin position
	 * 
	 * @param storeUndo
	 *            true to store undo infor
	 */
	@Override
	public void setStandardView(boolean storeUndo) {
		if (!isZoomable()) {
			return;
		}
		final double xzero, yzero;

		xzero = getXZeroStandard();
		yzero = getYZeroStandard();

		if (needsZoomerForStandardRatio()) {
			// set axes ratio back to 1
			if (axesRatioZoomer == null) {
				axesRatioZoomer = newZoomer();
			}
			axesRatioZoomer.initAxes(2, 2, false);
			axesRatioZoomer.setStandardViewAfter(xzero, yzero);
			axesRatioZoomer.startAnimation();
		} else {
			setAnimatedCoordSystem(xzero, yzero, STANDARD_VIEW_STEPS, false);
		}
		if (storeUndo) {
			getApplication().storeUndoInfo();
		}
	}

	protected boolean needsZoomerForStandardRatio() {
		return Math.abs(getScaleRatio() - 1.0) > 0.001;
	}

	/**
	 * 
	 * @return true if view is not zoomed;
	 */
	public boolean isStandardView() {
		return isZeroStandard() && (zoomer == null || zoomer.isStandardZoom());
	}

	/**
	 * Sets coord system of this view to standard. Just like setCoordSystem but
	 * with previous animation.
	 * 
	 * 
	 */
	public void setAnimatedCoordSystem(double originX, double originY,
			int steps, boolean storeUndo) {
		setAnimatedCoordSystem(originX, originY, 0, SCALE_STANDARD, steps,
				storeUndo);
	}

	/**
	 * Sets coord system of this view. Just like setCoordSystem but with
	 * previous animation.
	 * 
	 * 
	 * @param originX
	 *            x coord of old origin
	 * @param originY
	 *            y coord of old origin
	 * @param newScale
	 *            x scale
	 */
	@Override
	public void setAnimatedCoordSystem(double originX, double originY,
			double f, double newScale, int steps, boolean storeUndo) {

		double ox = originX + (getXZero() - originX) * f;
		double oy = originY + (getYZero() - originY) * f;

		if (!DoubleUtil.isEqual(getXscale(), newScale)) {
			// different scales: zoom back to standard view
			double factor = newScale / getXscale();
			zoom((ox - (getXZero() * factor)) / (1.0 - factor),
					(oy - (getYZero() * factor)) / (1.0 - factor), factor,
					steps, storeUndo);
		} else {
			// same scales: translate view to standard origin
			// do this with the following action listener
			if (mover == null) {
				mover = newZoomer();
			}
			mover.init(ox, oy, storeUndo);
			mover.startAnimation();
		}
	}

	/**
	 * Sets real world coord system using min and max values for both axes in
	 * real world values.
	 */
	@Override
	final public void setAnimatedRealWorldCoordSystem(double xmin, double xmax,
			double ymin, double ymax, int steps, boolean storeUndo) {
		if (zoomerRW == null) {
			zoomerRW = newZoomer();
		}
		zoomerRW.initRW(xmin, xmax, ymin, ymax, steps, storeUndo);
		zoomerRW.startAnimation();
	}

	// for use in AlgebraController
	@Override
	final public void mouseMovedOver(GeoElement geo) {
		Hits geos = null;
		if (geo != null) {
			tempArrayList.clear();
			tempArrayList.add(geo);
			geos = tempArrayList;
		}
		boolean repaintNeeded = getEuclidianController().refreshHighlighting(
				geos, false);
		if (repaintNeeded) {
			kernel.notifyRepaint();
		}
	}

	@Override
	public void highlight(GeoElement geo) {
		if (getEuclidianController().highlight(geo)) {
			kernel.notifyRepaint();
		}
	}

	@Override
	public void highlight(ArrayList<GeoElement> geos) {
		if (getEuclidianController().highlight(geos)) {
			kernel.notifyRepaint();
		}
	}

	@Override
	final public void mouseMovedOverList(ArrayList<GeoElement> geoList) {
		Hits geos = null;
		tempArrayList.clear();
		tempArrayList.addAll(geoList);
		geos = tempArrayList;

		boolean repaintNeeded = getEuclidianController().refreshHighlighting(
				geos, false);
		if (repaintNeeded) {
			kernel.notifyRepaint();
		}
	}

	/**
	 * Updates highlighting of animation buttons.
	 * 
	 * @return whether status was changed
	 */
	@Override
	public final boolean setAnimationButtonsHighlighted(boolean flag) {
		// draw button in focused EV only
		if (!drawPlayButtonInThisView()) {
			return false;
		}

		if (flag == highlightAnimationButtons) {
			return false;
		}
		highlightAnimationButtons = flag;
		return true;
	}

	/**
	 * @return true if play button belongs to this view
	 */
	public boolean drawPlayButtonInThisView() {
		GuiManagerInterface gui = getApplication().getGuiManager();
		// just one view
		if (gui == null) {
			return true;
		}
		// eg ev1 just closed
		GetViewId evp = gui.getLayout().getDockManager()
				.getFocusedEuclidianPanel();
		if (evp == null || evp.getViewId() == App.VIEW_EUCLIDIAN3D) {
			return isPrimaryEV();
		}

		return this.getViewID() == evp.getViewId();
	}

	/**
	 * @return whether reset and fullscreen should prefer this view
	 */
	public boolean isPrimaryEV() {
		return this.getEuclidianViewNo() == 1
				|| (!app.showView(App.VIEW_EUCLIDIAN) && this.isDefault2D());
	}

	/**
	 * @return axes color
	 */
	public GColor getAxesColor() {
		return axesColor;
	}

	/**
	 * @return grid color
	 */
	public GColor getGridColor() {
		return gridColor;
	}

	/**
	 * Initializes basic properties of this view
	 * 
	 * @param repaint
	 *            true if should be repainted after
	 */
	protected void initView(boolean repaint) {
		// init grid's line type
		setGridLineStyle(EuclidianStyleConstants.LINE_TYPE_DASHED_SHORT);
		setAxesLineStyle(EuclidianStyleConstants.AXES_LINE_TYPE_ARROW);
		setAxesColor(GColor.BLACK);
		setGridColor(GColor.LIGHT_GRAY);
		setBackground(GColor.WHITE);

		pointCapturingMode = EuclidianStyleConstants.POINT_CAPTURING_AUTOMATIC;

		showAxesNumbers[0] = true;
		showAxesNumbers[1] = true;
		axesLabels[0] = null;
		axesLabels[1] = null;
		axesUnitLabels[0] = null;
		axesUnitLabels[1] = null;
		piAxisUnit[0] = false;
		piAxisUnit[1] = false;
		axesTickStyles[0] = EuclidianStyleConstants.AXES_TICK_STYLE_MAJOR;
		axesTickStyles[1] = EuclidianStyleConstants.AXES_TICK_STYLE_MAJOR;

		// for axes labeling with numbers
		automaticAxesNumberingDistances[0] = true;
		automaticAxesNumberingDistances[1] = true;

		// distances between grid lines
		automaticGridDistance = true;

		setStandardCoordSystem(repaint);

	}

	@Override
	public void setShowAxis(boolean show) {
		setShowAxis(0, show, false);
		setShowAxis(1, show, true);
	}

	/**
	 * @return graphics object (for pen)
	 */
	public abstract GGraphics2D getGraphicsForPen();

	/**
	 * @return whether stylebar of this view exists
	 */
	public final boolean hasStyleBar() {
		return styleBar != null;
	}

	/**
	 * @param mode
	 *            mode number
	 * @return whether the mode is pen, pencil or freehand
	 */
	public static boolean isPenMode(int mode) {
		return mode == EuclidianConstants.MODE_PEN
				|| mode == EuclidianConstants.MODE_FREEHAND_SHAPE
				|| mode == EuclidianConstants.MODE_HIGHLIGHTER;
	}

	/**
	 * sets the option panel for gui update
	 * 
	 * @param optionPanel
	 *            option panel
	 */
	public void setOptionPanel(OptionsEuclidian optionPanel) {
		this.optionPanel = optionPanel;
	}

	/**
	 * @return delete tool rectangle
	 */
	public GRectangle getDeletionRectangle() {
		return deletionRectangle;
	}

	/**
	 * @param deletionRectangle
	 *            delete tool rectangle
	 */
	public void setDeletionRectangle(GRectangle deletionRectangle) {
		this.deletionRectangle = deletionRectangle;
	}

	/**
	 * changes style bold <> not bold as necessary
	 * 
	 * @param bold
	 *            true for bold axes
	 * @param axesLineStyle
	 *            old style
	 * @return new style
	 */
	public static int getBoldAxes(boolean bold, int axesLineStyle) {
		if (bold) {
			return axesLineStyle | EuclidianStyleConstants.AXES_BOLD;
		}
		return axesLineStyle & (~EuclidianStyleConstants.AXES_BOLD);
	}

	/**
	 * @return whether axes are bold
	 */
	public boolean areAxesBold() {
		return (axesLineType & EuclidianStyleConstants.AXES_BOLD) != 0;

	}

	static double estimateNumberHeight(GFont fontAxes2) {
		return StringUtil.getPrototype().estimateHeight("", fontAxes2);
	}

	double estimateNumberWidth(double d, GFont fontAxes2) {
		String s = kernel.formatPiE(d, axesNumberFormat[0],
				StringTemplate.defaultTemplate);
		return StringUtil.getPrototype().estimateLength(s, fontAxes2);
	}

	static double estimateTextWidth(String s, GFont fontAxes2) {
		return StringUtil.getPrototype().estimateLength(s, fontAxes2);
	}

	@Override
	public int getSliderOffsetY() {
		return 50;
	}

	@Override
	public int getComboOffsetY() {
		return 70;
	}

	public int getAbsoluteTop() {
		return -1;
	}

	public int getAbsoluteLeft() {
		return -1;
	}

	@Override
	final public EuclidianStyleBar getStyleBar() {
		if (styleBar == null) {
			styleBar = newEuclidianStyleBar();
		}

		return styleBar;
	}

	/**
	 * 
	 * @return new dynamic style bar
	 */
	abstract protected EuclidianStyleBar newEuclidianStyleBar();

	/**
	 * 
	 * Adds dynamic stylebar to DOM if not added yet
	 */
	abstract protected void addDynamicStylebarToEV(EuclidianStyleBar dynamicStylebar);

	@Override
	final public EuclidianStyleBar getDynamicStyleBar() {
		if (dynamicStyleBar == null) {
			dynamicStyleBar = newDynamicStyleBar();
			if (dynamicStyleBar != null) {
				dynamicStyleBar.setVisible(false);
			}
		}
		addDynamicStylebarToEV(dynamicStyleBar);
		return dynamicStyleBar;
	}
	
	/**
	 * @return whether dynamic stylebar is visible
	 */
	public boolean isDynStyleBarVisible() {
		if (dynamicStyleBar != null) {
			return dynamicStyleBar.isVisible();
		}
		return false;
	}

	/**
	 * @return whether dynamic stylebar exists
	 */
	public final boolean hasDynamicStyleBar() {
		return dynamicStyleBar != null;
	}

	/**
	 * 
	 * @return new euclidian style bar
	 */
	abstract protected EuclidianStyleBar newDynamicStyleBar();

	/**
	 * @return last repaint, overridden in Web
	 */
	public long getLastRepaintTime() {
		return 0;
	}

	@Override
	public final void setLabels() {
		if (styleBar != null) {
			styleBar.setLabels();
		}
		if (dynamicStyleBar != null) {
			dynamicStyleBar.setLabels();
		}
	}

	/**
	 * 
	 * @return 2 for 2D and 3 for 3D
	 */
	public int getDimension() {
		return 2;
	}

	/**
	 * draw background image to graphics
	 * 
	 * @param g2d
	 *            graphics
	 */
	protected void drawBackgroundImage(GGraphics2D g2d) {
		g2d.drawImage(bgImage, 0, 0);
	}

	/**
	 * @param g2d
	 *            graphics
	 * @param scale
	 *            scale
	 */
	protected void exportPaintPreScale(GGraphics2D g2d, double scale) {
		g2d.scale(scale, scale);
	}

	/**
	 * Prepare export
	 * 
	 * @param g2d
	 *            graphics
	 * @param scale
	 *            scale
	 * @param transparency
	 *            use transparency?
	 */
	public void exportPaintPre(GGraphics2D g2d, double scale,
			boolean transparency) {
		exportPaintPreScale(g2d, scale);

		// clipping on selection rectangle
		if (getSelectionRectangle() != null) {
			GRectangle rect = getSelectionRectangle();

			g2d.setClip(0, 0, (int) rect.getWidth(),
						(int) rect.getHeight());

			g2d.translate(-rect.getX(), -rect.getY());
			// Application.debug(rect.x+" "+rect.y+" "+rect.width+"
			// "+rect.height);
		} else {
			// use points Export_1 and Export_2 to define corner
			double[] exportCoords = getExportCoords();
			if (exportCoords != null) {
				double x1 = exportCoords[0];
				double y1 = exportCoords[1];
				double x2 = exportCoords[2];
				double y2 = exportCoords[3];
				int exportWidth = (int) (x2 - x1 + 2);
				int exportHeight = (int) (y2 - y1 + 2);

				g2d.setClip(0, 0, exportWidth, exportHeight);
				g2d.translate(-x1, -y1);
			} else {
				// or take full euclidian view
				g2d.setClip(0, 0, getWidth(), getHeight());
			}
		}

		// DRAWING
		if (isTracing() || hasBackgroundImages()) {
			// draw background image to get the traces
			if (bgImage == null) {
				drawBackgroundWithImages(g2d, transparency);
			} else {
				drawBackgroundImage(g2d);
			}
		} else {
			// just clear the background if transparency is disabled (clear =
			// draw background color)
			drawBackground(g2d, !transparency);
		}

		g2d.setAntialiasing();
	}

	/**
	 * Scales construction and draws it to g2d.
	 * 
	 * @param g2d
	 *            export graphics
	 * @param scale
	 *            ratio of desired size and current size of the graphics
	 * 
	 * @param transparency
	 *            states if export should be optimized for eps. Note: if this is
	 *            set to false, no traces are drawn.
	 * @param exportType
	 *            SVG, PNG etc
	 * 
	 */
	public void exportPaint(GGraphics2D g2d, double scale,
			boolean transparency, ExportType exportType) {
		getApplication().setExporting(exportType, scale);

		exportFrame = getFrame();

		// EuclidianViewExporter2 evExport = new EuclidianViewExporter2(this,
		// getFrame());

		// in case they are off screen but within Export_1, Export_2 rectangle
		updateAllDrawables(false);

		exportPaintPre(g2d, scale, transparency);
		drawObjects(g2d);
		g2d.resetClip();
		getApplication().setExporting(ExportType.NONE, 1);

		exportFrame = null;

		// reset visibility for drawables
		updateAllDrawables(true);
	}

	/**
	 * 
	 * @return maxx in screen pixels
	 */
	public int getMaxXScreen() {
		if (exportFrame != null) {
			return (int) exportFrame.getMaxX();
		}
		return getWidth();
	}

	/**
	 * 
	 * @return minx in pixels
	 */
	public int getMinXScreen() {
		if (exportFrame != null) {
			return (int) exportFrame.getMinX();
		}
		return 0;
	}

	/**
	 * 
	 * @return maxx in pixels
	 */
	public int getMaxYScreen() {
		if (exportFrame != null) {
			return (int) exportFrame.getMaxY();
		}

		return getHeight();
	}

	/**
	 * 
	 * @return miny in pixels
	 */
	public int getMinYScreen() {
		if (exportFrame != null) {
			return (int) exportFrame.getMinY();
		}
		return 0;
	}

	/**
	 * Returns image of drawing pad sized according to the given scale factor.
	 * 
	 * @param scale
	 *            ratio of desired size and current size of the graphics
	 * @return image of drawing pad sized according to the given scale factor.
	 */
	public GBufferedImage getExportImage(double scale) {
		return getExportImage(scale, false, ExportType.PNG);
	}

	/**
	 * @param scale
	 *            ratio of desired size and current size of the graphics
	 * @param transparency
	 *            true for transparent image
	 * @param exportType
	 *            export type
	 * @return image
	 */
	public GBufferedImage getExportImage(double scale, boolean transparency,
			ExportType exportType) {
		
		int width = (int) Math.floor(getExportWidth() * scale);
		int height = (int) Math.floor(getExportHeight() * scale);
		try {
		GBufferedImage img = AwtFactory.getPrototype().createBufferedImage(
				width, height, transparency);
		exportPaint(img.createGraphics(), scale, transparency, exportType);
		img.flush();
		return img;
		} catch (Exception e) {
			Log.debug("problem with creating image with dimensions " + width
					+ " " + height);
			return null;
		}
	}

	@Override
	public void centerView(GeoPointND point) {
		Coords p = getCoordsForView(point.getInhomCoordsInD3());

		double px = (toRealWorldCoordX(getWidth()) - toRealWorldCoordX(0)) / 2;
		double py = (-toRealWorldCoordY(getHeight()) + toRealWorldCoordY(0)) / 2;

		setRealWorldCoordSystem(p.getX() - px, p.getX() + px, p.getY() - py,
				p.getY() + py);
	}

	/**
	 * @param list
	 *            list of dropped geos
	 * @return FormulaText / TableText command for graphics view formula
	 */
	public static String getDraggedLabels(ArrayList<String> list) {
		if (list.size() == 0) {
			return null;
		}

		// single geo
		if (list.size() == 1) {
			return "FormulaText[" + list.get(0) + ", true, true]";
		}
		StringBuilder text = new StringBuilder("TableText[");
		for (int i = 0; i < list.size(); i++) {

			text.append("{FormulaText[");
			text.append(list.get(i));
			text.append(", true, true]}");

			if (i < list.size() - 1) {
				text.append(",");
			}
		}
		text.append("]");

		return text.toString();
	}

	@Override
	public boolean isViewForPlane() {
		if (settings == null) {
			return false;
		}
		return settings.isViewForPlane();
	}

	/**
	 * @param pixelRatio
	 *            physical x logical pixel ratio
	 */
	public void setPixelRatio(double pixelRatio) {
		// TODO Auto-generated method stub
	}

	@Override
	public void closeDropDowns(int x, int y) {
		boolean repaintNeeded = false;
		for (Drawable d : allDrawableList) {
			if (d instanceof DrawDropDownList) {
				DrawDropDownList dl = (DrawDropDownList) d;
				if (!(dl.isControlHit(x, y) || dl.isOptionsHit(x, y))) {
					repaintNeeded = repaintNeeded || dl.closeOptions();
				}
			}
		}

		if (repaintNeeded) {
			repaintView();
		}
	}

	/**
	 * Close all dropdowns.
	 */
	public void closeAllDropDowns() {
		for (Drawable d : allDrawableList) {
			if (d instanceof DrawDropDownList) {
				DrawDropDownList dl = (DrawDropDownList) d;
				dl.closeOptions();
			}
		}
	}

	/**
	 * @return currently opened combo box
	 */
	public DrawDropDownList getOpenedComboBox() {
		return openedComboBox;
	}

	/**
	 * Mark combobox as opened and select it.
	 * 
	 * @param openedComboBox
	 *            combobox
	 */
	public void setOpenedComboBox(DrawDropDownList openedComboBox) {
		this.openedComboBox = openedComboBox;
		SelectionManager sm = app.getSelectionManager();
		if (openedComboBox != null) {
			GeoElement geo = openedComboBox.getGeoElement();
			if (!sm.containsSelectedGeo(geo)) {
				sm.addSelectedGeo(geo);
			}
		}
	}

	public void cancelBlur() {
		// TODO Auto-generated method stub
	}

	/**
	 * @return whether size was decreased after file was loaded
	 */
	public boolean shrinkedSinceLoad() {
		return (getSettings() != null && getWidth() > 2 && (getWidth() < getSettings()
				.getFileWidth() || getHeight() < getSettings().getFileHeight()));
	}

	@Override
	public boolean isInPlane(CoordSys sys) {
		return companion.isInPlane(sys);
	}

	/**
	 * @return pixel ratio
	 */
	public double getPixelRatio() {
		return 1;
	}

	public double getXZeroOld() {
		return xZeroOld;
	}

	public double getYZeroOld() {
		return yZeroOld;
	}

	public double getXScaleStart() {
		return xScaleStart;
	}

	public double getYScaleStart() {
		return yScaleStart;
	}

	@Override
	public void screenChanged() {
		screenChanged = true;
	}

	private void adjustObjects() {
		if (app.has(Feature.ADJUST_WIDGETS) && screenChanged) {
			app.adjustScreen(true);
			screenChanged = false;
			repaint();
		}
	}

	public AutoCompleteTextField getTextField() {
		return viewTextField == null ? null : viewTextField.getTextField();
	}

	/**
	 * @param input
	 *            input box
	 * @param drawInputBox
	 *            drawable
	 * @return text input field
	 */
	public AutoCompleteTextField getTextField(GeoInputBox input,
			DrawInputBox drawInputBox) {
		return viewTextField == null ? null : viewTextField.getTextField(
				input.getLength(), drawInputBox);
	}

	/**
	 * @param inputBox
	 *            input box
	 */
	public void focusTextField(GeoInputBox inputBox) {
		DrawableND d = getDrawableFor(inputBox);
		if (d != null) {
			DrawInputBox drawInputBox = (DrawInputBox) d;
			if (inputBox.isSymbolicMode()) {
				drawInputBox.attachMathField();
			} else if (viewTextField != null) {
				viewTextField.focusTo(drawInputBox);
			}
		}
	}

	/**
	 * Focus and show the textfield.
	 * 
	 * @param inputBox
	 *            input box
	 */
	public void focusAndShowTextField(GeoInputBox inputBox) {
		requestFocus();
		refreshTextfieldFocus(inputBox);
	}

	/**
	 * Focus textfield and make sure it's shown.
	 * 
	 * @param inputBox
	 *            input box
	 */
	public void refreshTextfieldFocus(GeoInputBox inputBox) {
		focusTextField(inputBox);
		viewTextField.getTextField().getDrawTextField().setWidgetVisible(true);
		getTextField().setSelection(0, getTextField().getText().length());
	}

	public ViewTextField getViewTextField() {
		return viewTextField;
	}

	public void setViewTextField(ViewTextField viewTextField) {
		this.viewTextField = viewTextField;
	}

	/**
	 * Remove textfield.
	 */
	public void removeTextField() {
		if (viewTextField != null) {
			viewTextField.remove();
		}
	}

	protected boolean isTraceDrawn() {
		return tracing;
	}

	/**
	 * @param i
	 *            axis index
	 * @return axis number format
	 */
	public NumberFormatAdapter getAxisNumberFormat(int i) {
		return axesNumberFormat[i];
	}

	/**
	 * @return standard screen x-coord of origin
	 */
	public double getXZeroStandard() {
		return getViewWidth() / 2.0;
	}

	/**
	 * @return standard screen y-coord of origin
	 */
	public double getYZeroStandard() {
		return getViewHeight() / 2.0;
	}

	/**
	 * 
	 * @return true if coord system is translated by internal animation, like
	 *         open/close toolbar in portrait.
	 */
	public boolean isCoordSystemTranslatedByAnimation() {
		return coordSystemTranslatedByAnimation;
	}

	/**
	 * @param shape
	 *            shape
	 * @return whether shape intersects visible region
	 */
	public boolean intersects(GShape shape) {
		// don't use getWidth()/ getHeight()
		// doesn't work for export
		return shape.intersects(getMinXScreen(), getMinYScreen(),
				getMaxXScreen() - getMinXScreen(),
				getMaxYScreen() - getMinYScreen());
	}

	/**
	 * This default implementation does NOT draw "white" outlines (actually
	 * outline color is taken from view's background color)
	 * 
	 * It's currently over-ridden in web to draw white outlines for axes numbers
	 * and GeoElement labels
	 * 
	 * @param g2
	 *            canvas
	 * @param text
	 *            text
	 * @param x
	 *            x-coord
	 * @param y
	 *            y=coord
	 * @param col
	 *            text color
	 */
	public void drawStringWithOutline(GGraphics2D g2, String text, double x,
			double y, GColor col) {

		// default implementation, no outline
		// don't change, used when no outline wanted
		g2.setColor(col);
		g2.drawString(text, x, y);
	}

	/**
	 * Invalidate cached graphics
	 */
	public void invalidateCache() {
		// web only
	}

	/**
	 * Cache all drawables
	 */
	public void cacheGraphics() {
		// web only
	}

	protected GBufferedImage getCacheGraphics() {
		return null;
	}

	/**
	 * Reset background and cache bitmaps.
	 */
	protected void resetBackgroundAndCache() {
		bgImage = null;
		bgGraphics = null;
	}

	/**
	 * 
	 * @param geo
	 *            construction element
	 * @param firstCall
	 *            whether it's the first call for the geo
	 * @return null or callback to refresh view - needed for LaTeX in web
	 */
	public Runnable getCallBack(GeoElementND geo, boolean firstCall) {
		// overridden in web project
		return null;
	}

    /**
     *
     * @return true if stylebar needs to check selected geo still in hit
     */
	public boolean checkHitForStylebar() {
		return false;
	}

	/**
	 *
	 * @return true if this kind of view can move functions (false for 3D)
	 */
	public boolean canMoveFunctions() {
		return true;
	}

	/**
	 *
	 * @return true if view can show different styles for points (false for 3D)
	 */
	public boolean canShowPointStyle() {
		return true;
	}

	/**
	 * @return whether animation button is focused
	 */
	public boolean isAnimationButtonSelected() {
		return kernel.needToShowAnimationButton() && animationButtonSelected;
	}

	/**
	 * @param value
	 *            whether animation button is focused
	 */
	public void setAnimationButtonSelected(boolean value) {
		this.animationButtonSelected = value;
		setAnimationButtonsHighlighted(value);
		if (value) {
			ScreenReader.readEVPlay(app);
		}
		repaintView();
	}

	/**
	 * 
	 * @return true if AR is enabled
	 */
    public boolean isAREnabled() {
        return false;
    }

	/**
	 * @return screen reader
	 */
	public ScreenReaderAdapter getScreenReader() {
		return ScreenReaderSilent.INSTANCE;
	}

    /**
     * reset settings
     */
    public void resetSettings() {
        // settings should have been reset before
        settingsChanged(getSettings());
    }

	/**
	 * Attaches a symbolic-capable editor to the input box
	 * @param geoInputBox
	 * 			the input box to attach
	 * @param bounds
	 * 			where the editor should be attached to.
	 */
	public void attachSymbolicEditor(GeoInputBox geoInputBox, GRectangle bounds) {
		if (symbolicEditor == null) {
			symbolicEditor = createSymbolicEditor();
		}
		if (symbolicEditor != null) {
			symbolicEditor.attach(geoInputBox, bounds);
		}
	}

	public SymbolicEditor getSymbolicEditor() {
		return symbolicEditor;
	}

	/**
	 * Hides the symbolic editor of EV input fields
	 */
	public void hideSymbolicEditor() {
		if (symbolicEditor != null) {
			symbolicEditor.hide();
		}
	}

	protected SymbolicEditor createSymbolicEditor() {
		// overridden in web and desktop
		return null;
	}

	/**
	 * @param mouseLoc
	 *            mouse coordinates
	 * @return whether symbolic editor was clicked
	 */
	public boolean isSymbolicEditorClicked(GPoint mouseLoc) {
		if (symbolicEditor == null) {
			return false;
		}
		return symbolicEditor.isClicked(mouseLoc);
	}

	/**
	 * Draw mask preview if any.
	 * @param g2 Graphics to draw to.
	 */
	void drawMaskPreview(GGraphics2D g2) {
		if (maskPreview != null) {
			drawShape(g2, GeoGebraColorConstants.MEBIS_MASK,
					GeoGebraColorConstants.MEBIS_MASK,
					null, maskPreview);
		}
	}

	/**
	 * @return hit detector
	 */
	public HitDetector getHitDetector() {
		return hitDetector;
	}

	/**
	 * @return whether the reset icon is selected
	 */
	public boolean isResetIconSelected() {
		return isResetIconSelected;
	}

	/**
	 * Sets whether the reset icon should be selected.
	 * It changes the reset icon appearance.
	 *
	 * @param selected true if the reset icon is selected
	 */
	public void setResetIconSelected(boolean selected) {
		if (isResetIconSelected != selected) {
			isResetIconSelected = selected;
			invalidateBackground();
			repaint();
		}
	}

	/**
	 * Reset partial hits for all drawables
	 *
	 * @param x
	 *            screen x-coord of pointer event
	 * @param y
	 *            screen y-coord of pointer event
	 * @param threshold
	 *            hit threshold
	 * @return whether selection changed
	 */
	public boolean resetPartialHits(int x, int y, int threshold) {
		if (boundingBox != null && boundingBox.hit(x, y, threshold)) {
			return false;
		}
		boolean deselected = false;
		for (Drawable draw : this.allDrawableList) {
			deselected = draw.resetPartialHitClip(x, y) || deselected;
		}
		return deselected;
	}

	/**
	 * Show stylebar if bounding box visible
	 */
	public void restoreDynamicStylebar() {
		if (euclidianController.isMultiSelection()) {
			euclidianController.showDynamicStylebar();
		}
	}

	/**
	 * Remove all widgets for inline texts
	 */
	public void resetInlineObjects() {
		for (Drawable dr : allDrawableList) {
			if (dr instanceof DrawInline) {
				((DrawInline) dr).remove();
			}
		}
	}

	public BoundingBox<? extends GShape> getFocusedGroupGeoBoundingBox() {
		return this.focusedGroupGeoBoundingBox;
	}

	/**
	 * Draw preview of the current shape
	 * @param g2 graphics
	 */
	public void drawShapePreview(GGraphics2D g2) {
		drawShape(g2, shapeRectangle);
		drawMaskPreview(g2);
		drawShape(g2, shapeEllipse);
		drawShape(g2, shapeLine);
		drawShape(g2, shapePolygon);
	}

	public void embed(GGraphics2D g2, DrawWidget drawEmbed) {
		//web only
	}
}
