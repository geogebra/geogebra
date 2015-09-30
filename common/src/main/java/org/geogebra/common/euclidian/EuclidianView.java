package org.geogebra.common.euclidian;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;

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
import org.geogebra.common.awt.GRectangle;
import org.geogebra.common.awt.GShape;
import org.geogebra.common.awt.font.GTextLayout;
import org.geogebra.common.euclidian.DrawableList.DrawableIterator;
import org.geogebra.common.euclidian.draw.DrawAngle;
import org.geogebra.common.euclidian.draw.DrawButton;
import org.geogebra.common.euclidian.draw.DrawConic;
import org.geogebra.common.euclidian.draw.DrawImage;
import org.geogebra.common.euclidian.draw.DrawLine;
import org.geogebra.common.euclidian.draw.DrawLine.PreviewType;
import org.geogebra.common.euclidian.draw.DrawList;
import org.geogebra.common.euclidian.draw.DrawPolyLine;
import org.geogebra.common.euclidian.draw.DrawPolygon;
import org.geogebra.common.euclidian.draw.DrawRay;
import org.geogebra.common.euclidian.draw.DrawSegment;
import org.geogebra.common.euclidian.draw.DrawTextField;
import org.geogebra.common.euclidian.draw.DrawVector;
import org.geogebra.common.euclidian.event.PointerEventType;
import org.geogebra.common.factories.AwtFactory;
import org.geogebra.common.factories.FormatFactory;
import org.geogebra.common.gui.SetLabels;
import org.geogebra.common.gui.dialog.options.OptionsEuclidian;
import org.geogebra.common.javax.swing.GBox;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.ModeSetter;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.Matrix.CoordMatrix;
import org.geogebra.common.kernel.Matrix.Coords;
import org.geogebra.common.kernel.algos.AlgoAngle;
import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.arithmetic.NumberValue;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoElement.HitType;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.geos.GeoImage;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.geos.GeoText;
import org.geogebra.common.kernel.kernelND.GeoDirectionND;
import org.geogebra.common.kernel.kernelND.GeoLineND;
import org.geogebra.common.kernel.kernelND.GeoPlaneND;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.main.App;
import org.geogebra.common.main.App.ExportType;
import org.geogebra.common.main.GuiManagerInterface;
import org.geogebra.common.main.settings.AbstractSettings;
import org.geogebra.common.main.settings.EuclidianSettings;
import org.geogebra.common.plugin.EuclidianStyleConstants;
import org.geogebra.common.plugin.GeoClass;
import org.geogebra.common.util.MyMath;
import org.geogebra.common.util.NumberFormatAdapter;
import org.geogebra.common.util.StringUtil;
import org.geogebra.common.util.Unicode;

/**
 * View containing graphic representation of construction elements
 */
public abstract class EuclidianView implements EuclidianViewInterfaceCommon,
		SetLabels {
	/** says if the view has the mouse */
	protected boolean hasMouse;
	/** View other than EV1 and EV2 **/
	public static int EVNO_GENERAL = 1001;
	/** 3D View TODO: probably needs changing when we support more than 2 views **/
	public static int EVNO_3D = -1;
	/** euclidian view number */
	protected int evNo = 1;
	private double xZeroOld, yZeroOld;
	private int mode = EuclidianConstants.MODE_MOVE;
	/** minimal width */
	protected static final int MIN_WIDTH = 50;
	/** minimal height */
	protected static final int MIN_HEIGHT = 50;
	/** corner of export area */
	protected static final String EXPORT1 = "Export_1"; // Points used to define
														// corners for export
														// (if they exist)
	/** corner of export area */
	protected static final String EXPORT2 = "Export_2";

	/** pixel per centimeter (at 72dpi) */
	protected static final double PRINTER_PIXEL_PER_CM = 72.0 / 2.54;
	/** zoom factor of zoom mode */
	public static final double MODE_ZOOM_FACTOR = 1.5;
	/** zoom factor of mouse wheel */
	public static final double MOUSE_WHEEL_ZOOM_FACTOR = 1.1;
	/** standard pixels per unit */
	public static final double SCALE_STANDARD = 50;
	/** border in which axis numbers are not drawn */
	protected static int SCREEN_BORDER = 10;

	// public static final double SCALE_MAX = 10000;
	// public static final double SCALE_MIN = 0.1;
	/** default screen x-coord of origin */
	public static final double XZERO_STANDARD = 215;
	/** default screen y-coord of origin */
	public static final double YZERO_STANDARD = 315;
	// or use volatile image
	// protected int drawMode = DRAW_MODE_BACKGROUND_IMAGE;
	/** background image */
	protected GBufferedImage bgImage;
	/** g2d of bgImage: used for axis, grid, background images and object traces */
	protected org.geogebra.common.awt.GGraphics2D bgGraphics;
	// zoom rectangle colors
	private static final org.geogebra.common.awt.GColor colZoomRectangle = AwtFactory.prototype
			.newColor(200, 200, 230);
	private static final org.geogebra.common.awt.GColor colZoomRectangleFill = AwtFactory.prototype
			.newColor(200, 200, 230, 50);

	// deletion square design
	protected static final GColor colDeletionSquare = AwtFactory.prototype
			.newColor(128, 0, 0);
	protected static final GBasicStroke strokeDeletionSquare = AwtFactory.prototype
			.newBasicStroke(1.0f);
	protected GRectangle deletionRectangle;

	// colors: axes, grid, background
	org.geogebra.common.awt.GColor axesColor;
	private org.geogebra.common.awt.GColor gridColor;
	protected GRectangle selectionRectangle;
	/**
	 * default axes stroke
	 */
	static GBasicStroke defAxesStroke = AwtFactory.prototype
			.newBasicStroke(1.0f, org.geogebra.common.awt.GBasicStroke.CAP_BUTT,
					org.geogebra.common.awt.GBasicStroke.JOIN_MITER);

	// changed from 1.8f (same as bold grid) Michael Borcherds 2008-04-12
	static GBasicStroke boldAxesStroke = AwtFactory.prototype
			.newBasicStroke(2.0f,

			GBasicStroke.CAP_BUTT, GBasicStroke.JOIN_MITER);

	// axes and grid stroke
	org.geogebra.common.awt.GBasicStroke axesStroke;
	org.geogebra.common.awt.GBasicStroke tickStroke;
	private org.geogebra.common.awt.GBasicStroke gridStroke;
	/** kernel */
	protected Kernel kernel;

	/** @return line types */
	public static final Integer[] getLineTypes() {
		Integer[] ret = { new Integer(EuclidianStyleConstants.LINE_TYPE_FULL),
				new Integer(EuclidianStyleConstants.LINE_TYPE_DASHED_LONG),
				new Integer(EuclidianStyleConstants.LINE_TYPE_DASHED_SHORT),
				new Integer(EuclidianStyleConstants.LINE_TYPE_DOTTED),
				new Integer(EuclidianStyleConstants.LINE_TYPE_DASHED_DOTTED),
				new Integer(EuclidianStyleConstants.LINE_TYPE_POINTWISE) };
		return ret;
	}

	// G.Sturr added 2009-9-21
	/** @return point styles */
	public static final Integer[] getPointStyles() {
		Integer[] ret = {
				new Integer(EuclidianStyleConstants.POINT_STYLE_DOT),
				new Integer(EuclidianStyleConstants.POINT_STYLE_CROSS),
				new Integer(EuclidianStyleConstants.POINT_STYLE_CIRCLE),
				new Integer(EuclidianStyleConstants.POINT_STYLE_PLUS),
				new Integer(EuclidianStyleConstants.POINT_STYLE_FILLED_DIAMOND),
				new Integer(EuclidianStyleConstants.POINT_STYLE_EMPTY_DIAMOND),
				new Integer(EuclidianStyleConstants.POINT_STYLE_TRIANGLE_NORTH),
				new Integer(EuclidianStyleConstants.POINT_STYLE_TRIANGLE_SOUTH),
				new Integer(EuclidianStyleConstants.POINT_STYLE_TRIANGLE_EAST),
				new Integer(EuclidianStyleConstants.POINT_STYLE_TRIANGLE_WEST) };
		return ret;
	}

	// end
	private int fontSize;
	private org.geogebra.common.awt.GAffineTransform coordTransform = AwtFactory.prototype
			.newAffineTransform();
	protected double[] AxesTickInterval; // for axes =
	/** number formats for axes */
	protected NumberFormatAdapter[] axesNumberFormat;
	protected boolean[] showAxes = { true, true };

	protected boolean[] logAxes = { false, false };

	// distances between grid lines
	private boolean automaticGridDistance = true;

	protected double[] gridDistances;

	private int gridLineStyle;
	int axesLineType;

	protected boolean gridIsBold = false; // Michael Borcherds 2008-04-11
	/** tooltip mode in this view */
	protected int tooltipsInThisView = EuclidianStyleConstants.TOOLTIPS_AUTOMATIC;

	// Michael Borcherds 2008-04-28
	/** cartesian grid */
	public static final int GRID_CARTESIAN = 0;
	/** isometric grid */
	public static final int GRID_ISOMETRIC = 1;
	/** polar grid */
	public static final int GRID_POLAR = 2;

	private int gridType = GRID_CARTESIAN;

	// FONTS
	private GFont fontPoint;

	private GFont fontCoords;

	/** number format for print scale */
	protected NumberFormatAdapter printScaleNF;

	/** true if grid is displayed */
	protected boolean showGrid = false;
	/** true if antialiasing is used */
	protected boolean antiAliasing = true;

	protected boolean showMouseCoords = false;

	// set to false because it was set to false in Desktop anyway
	// (due to a bug in MyXMLHandler?), and it does some speedup in Web
	protected boolean allowShowMouseCoords = false;

	protected boolean showAxesRatio = false;
	/** true if animation button need highlighting */
	protected boolean highlightAnimationButtons = false;

	// only used for temporary views eg Text Preview, Spreadsheet plots
	protected int pointCapturingMode;

	boolean showAxesCornerCoords = true;// private

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

	private final EuclidianSettings settings;

	// member variables
	/** controller */
	protected EuclidianController euclidianController;

	// ggb3D 2009-02-05
	private final Hits hits;

	private static final int MAX_PIXEL_DISTANCE = 10; // pixels
	private static final double MIN_PIXEL_DISTANCE = 0.5; // pixels

	// maximum angle between two line segments
	private static final double MAX_ANGLE = 10; // degrees
	private static final double MAX_ANGLE_OFF_SCREEN = 45; // degrees
	private static final double MAX_BEND = Math.tan(MAX_ANGLE * Kernel.PI_180);
	private static final double MAX_BEND_OFF_SCREEN = Math
			.tan(MAX_ANGLE_OFF_SCREEN * Kernel.PI_180);

	// maximum number of bisections (max number of plot points = 2^MAX_DEPTH)
	private static final int MAX_DEFINED_BISECTIONS = 16;
	private static final int MAX_PROBLEM_BISECTIONS = 8;

	// maximum number of times to loop when xDiff, yDiff are both zero
	// eg Curve[0sin(t), 0t, t, 0, 6]
	private static final int MAX_ZERO_COUNT = 1000;

	// the curve is sampled at least at this many positions to plot it
	private static final int MIN_SAMPLE_POINTS = 80;

	protected EuclidianViewCompanion companion;

	/**
	 * @param ec
	 *            controller
	 * @param settings
	 *            settings
	 */
	public EuclidianView(EuclidianController ec, int viewNo,
			EuclidianSettings settings) {

		// 1, 2 or EVNO_GENERAL
		setEuclidianViewNo(viewNo);

		companion = newEuclidianViewCompanion();

		// Michael Borcherds 2008-03-01
		drawLayers = new DrawableList[EuclidianStyleConstants.MAX_LAYERS + 1];
		for (int k = 0; k <= EuclidianStyleConstants.MAX_LAYERS; k++) {
			drawLayers[k] = new DrawableList();
		}

		initAxesValues();

		this.euclidianController = ec;
		kernel = ec.getKernel();
		app = kernel.getApplication();
		this.settings = settings;
		// no repaint
		xminObject = new GeoNumeric(kernel.getConstruction());
		xmaxObject = new GeoNumeric(kernel.getConstruction());
		yminObject = new GeoNumeric(kernel.getConstruction());
		ymaxObject = new GeoNumeric(kernel.getConstruction());

		// ggb3D 2009-02-05
		hits = new Hits();

		printScaleNF = FormatFactory.prototype.getNumberFormat("#.#####", 5);

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
		drawBorderAxes = new boolean[] { false, false };
		axisCross = new double[] { 0, 0 };
		positiveAxes = new boolean[] { false, false };
		piAxisUnit = new boolean[] { false, false };
		gridDistances = new double[] { 2, 2, Math.PI / 6 };
		AxesTickInterval = new double[] { 1, 1 };

	}

	public void setAxesColor(org.geogebra.common.awt.GColor axesColor) {
		if (axesColor != null) {
			this.axesColor = axesColor;
		}
	}

	/**
	 * Sets the coord system to default
	 * 
	 * @param repaint
	 */
	protected void setStandardCoordSystem(boolean repaint) {
		setCoordSystem(XZERO_STANDARD, YZERO_STANDARD, SCALE_STANDARD,
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
	public int getPointCapturingMode() {

		if (settings != null) {
			return settings.getPointCapturingMode();
		}
		return pointCapturingMode;

	}

	/**
	 * Set capturing of points to the grid.
	 */
	public void setPointCapturing(int mode) {
		if (settings != null) {
			settings.setPointCapturing(mode);
		} else {
			pointCapturingMode = mode;
		}
	}

	public int getMode() {
		return mode;
	}

	public void setMode(int mode, ModeSetter m) {
		if (mode == this.mode) {
			return;
		}
		this.mode = mode;
		initCursor();
		getEuclidianController().clearJustCreatedGeos();
		getEuclidianController().setMode(mode);
		if (clearRectangle(mode)) {
			setSelectionRectangle(null);
		}
		setStyleBarMode(mode);
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

	protected NumberValue xminObject, xmaxObject, yminObject, ymaxObject;

	/**
	 * @return the xminObject
	 */
	public GeoNumeric getXminObject() {
		return (GeoNumeric) xminObject;
	}

	public void updateBoundObjects() {
		if (isZoomable()) {
			((GeoNumeric) xminObject).setValue(getXmin());
			((GeoNumeric) xmaxObject).setValue(getXmax());
			((GeoNumeric) yminObject).setValue(getYmin());
			((GeoNumeric) ymaxObject).setValue(getYmax());
		}
	}

	protected Double lockedAxesRatio;
	private boolean updateBackgroundOnNextRepaint;

	/**
	 * returns true if the axes ratio is 1
	 * 
	 * @return true if the axes ratio is 1
	 */
	public boolean isLockedAxesRatio() {
		return lockedAxesRatio != null || (gridType == GRID_POLAR);
	}

	/**
	 * Set unit axes ratio to 1
	 * 
	 * @param flag
	 *            true to set to 1, false to allow user
	 */
	public void setLockedAxesRatio(Double flag) {
		lockedAxesRatio = flag;
		if (flag != null) {
			updateBounds(true, true);
		}
	}

	private boolean updatingBounds = false;

	public void updateBounds(boolean updateDrawables, boolean updateSettings) {

		if (updatingBounds)
			return;

		updatingBounds = true;

		double xmin2 = xminObject.getDouble();
		double xmax2 = xmaxObject.getDouble();
		double ymin2 = yminObject.getDouble();
		double ymax2 = ymaxObject.getDouble();
		if (isLockedAxesRatio() && (getHeight() > 0) && (getWidth() > 0)) {
			double ratio = gridType == GRID_POLAR ? 1 : lockedAxesRatio
					.doubleValue();
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
		}
		// tell kernel
		if (evNo != EVNO_GENERAL) {
			kernel.setEuclidianViewBounds(evNo, getXmin(), getXmax(),
					getYmin(), getYmax(), getXscale(), getYscale());
		}

		// needed for images eg in zoomed EV2
		setCoordTransformIfNeeded();

		// tell option panel
		if (optionPanel != null)
			optionPanel.updateBounds();

		if (updateDrawables) {
			this.updateAllDrawables(true);
			this.updateBackgroundOnNextRepaint = true;
		}

		updatingBounds = false;
	}

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
	public void setXminObject(NumberValue xminObjectNew) {
		if (xminObject != null) {
			((GeoNumeric) xminObject).removeEVSizeListener(this);
		}
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
	 * @param xmaxObjectNew
	 *            the xmaxObject to set
	 */
	public void setXmaxObject(NumberValue xmaxObjectNew) {
		if (xmaxObject != null) {
			((GeoNumeric) xmaxObject).removeEVSizeListener(this);
		}
		if (xmaxObjectNew == null) {
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
	 * @param yminObjectNew
	 *            the yminObject to set
	 */
	public void setYminObject(NumberValue yminObjectNew) {
		if (yminObject != null) {
			((GeoNumeric) yminObject).removeEVSizeListener(this);
		}
		if (yminObjectNew == null) {
			this.yminObject = new GeoNumeric(kernel.getConstruction());
			updateBoundObjects();
		} else {
			this.yminObject = yminObjectNew;
		}
		setSizeListeners();
	}

	private void setSizeListeners() {
		((GeoNumeric) xminObject).addEVSizeListener(this);
		((GeoNumeric) yminObject).addEVSizeListener(this);
		((GeoNumeric) xmaxObject).addEVSizeListener(this);
		((GeoNumeric) ymaxObject).addEVSizeListener(this);
	}

	/**
	 * convert real world coordinate x to screen coordinate x
	 * 
	 * @param xRW
	 *            real world x coord
	 * @return screen equivalent of real world x-coord
	 */
	final public int toScreenCoordX(double xRW) {
		return (int) Math.round(getxZero() + xRW * getXscale());
	}

	/**
	 * convert real world coordinate y to screen coordinate y
	 * 
	 * @param yRW
	 *            real world y coord
	 * @return screen equivalent of real world y-coord
	 */
	final public int toScreenCoordY(double yRW) {
		return (int) Math.round(getyZero() - (yRW * getYscale()));
	}

	/**
	 * convert real world coordinate x to screen coordinate x
	 * 
	 * @param xRW
	 *            real world x-coord
	 * @return screen equivalent of real world x-coord as double
	 */
	final public double toScreenCoordXd(double xRW) {
		if (getXaxisLog())
			return getxZero() + (Math.log10(xRW) * getXscale());
		else
			return getxZero() + (xRW * getXscale());
	}

	/**
	 * convert real world coordinate y to screen coordinate y
	 * 
	 * @param yRW
	 *            real world y-coord
	 * @return screen equivalent of real world y-coord
	 */
	final public double toScreenCoordYd(double yRW) {
		if (getYaxisLog())
			return getYZero() + (Math.log10(yRW) * getYscale());
		else
			return getyZero() - (yRW * getYscale());
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
	public void rememberOrigins() {
		xZeroOld = getxZero();
		yZeroOld = getyZero();
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
			inOut[0] = getxZero() + (Math.log10(inOut[0]) * getXscale());
		} else
			inOut[0] = getxZero() + (inOut[0] * getXscale());

		if (getYaxisLog()) {
			inOut[1] = getyZero() - (Math.log10(inOut[1]) * getYscale());
		} else
			inOut[1] = getyZero() - (inOut[1] * getYscale());

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
		if (Kernel.isGreater(getYmin(), p1[1], tolerance)
				&& Kernel.isGreater(getYmin(), p2[1], tolerance))
			return true;

		// top
		if (Kernel.isGreater(p1[1], getYmax(), tolerance)
				&& Kernel.isGreater(p2[1], getYmax(), tolerance))
			return true;

		tolerance = EuclidianStatic.CLIP_DISTANCE / getXscale();

		// left
		if (Kernel.isGreater(getXmin(), p1[0], tolerance)
				&& Kernel.isGreater(getXmin(), p2[0], tolerance))
			return true;

		// right
		if (Kernel.isGreater(p1[0], getXmax(), tolerance)
				&& Kernel.isGreater(p2[0], getXmax(), tolerance))
			return true;

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
	final public double toRealWorldCoordX(double x) {
		return (x - getxZero()) * getInvXscale();
	}

	/**
	 * convert screen coordinate y to real world coordinate y
	 * 
	 * @param y
	 *            screen coord
	 * @return real world equivalent of screen y-coord
	 */
	final public double toRealWorldCoordY(double y) {
		return (getyZero() - y) * getInvYscale();
	}

	/**
	 * Sets real world coord system, where zero point has screen coords (xZero,
	 * yZero) and one unit is xscale pixels wide on the x-Axis and yscale pixels
	 * heigh on the y-Axis.
	 * 
	 * Also updates settings *before* all the algos that might need them are updated
	 */
	final public void setCoordSystem(double xZero, double yZero, double xscale,
			double yscale) {
		if(settings != null){
			settings.setCoordSystem(xZero, yZero, xscale, yscale, false);
		}
		setCoordSystem(xZero, yZero, xscale, yscale, true);
		
	}

	/** Sets coord system from mouse move */
	public void translateCoordSystemInPixels(int dx, int dy, int dz, int mode) {
		setCoordSystem(xZeroOld + dx, yZeroOld + dy, getXscale(), getYscale());
	}

	/** Sets coord system from mouse move */
	public void setCoordSystemFromMouseMove(int dx, int dy, int mode) {
		translateCoordSystemInPixels(dx, dy, 0, mode);
	}

	public void pageUpDownTranslateCoordSystem(int height) {
		translateCoordSystemInPixels(0, height, 0,
				EuclidianController.MOVE_VIEW);
	}

	/**
	 * Sets real world coord system using min and max values for both axes in
	 * real world values.
	 */
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

		// if (drawMode == DRAW_MODE_BACKGROUND_IMAGE)
		if (repaint) {
			updateBackgroundOnNextRepaint = true;
			updateAllDrawables(repaint);

			// needed so that eg Corner[2,1] updates properly on zoom / pan
			if (getApplication().hasEuclidianView2(1)) {
				kernel.notifyRepaint();
				// app.updateStatusLabelAxesRatio();
			}
		}

		// tells app that set coord system occured
		app.setCoordSystemOccured();
	}

	/**
	 * If the background was marked for update (axes changed), repaint it
	 */
	protected void updateBackgroundIfNecessary() {
		if (updateBackgroundOnNextRepaint) {
			this.updateBackgroundImage();
		}
		updateBackgroundOnNextRepaint = false;
	}

	/**
	 * @return the ymaxObject
	 */
	public GeoNumeric getYmaxObject() {
		return (GeoNumeric) ymaxObject;
	}

	/** minimal visible real world x */
	public double xmin;
	/** maximal visible real world x */
	public double xmax;
	/** minimal visible real world y */
	public double ymin;
	/** maximal visible real world y */
	public double ymax;

	private double invXscale;

	private double invYscale;

	private double xZero;

	private double yZero;

	private double xscale;

	private double yscale;

	// private double scaleRatio = 1.0;
	/** print scale ratio */
	protected double printingScale;

	// Map (geo, drawable) for GeoElements and Drawables
	private HashMap<GeoElement, DrawableND> DrawableMap = new HashMap<GeoElement, DrawableND>(
			500);

	private ArrayList<GeoPointND> stickyPointList = new ArrayList<GeoPointND>();

	private DrawableList allDrawableList = new DrawableList();
	/** lists of geos on different layers */
	public DrawableList drawLayers[];

	// on add: change resetLists()
	/** list of background images */
	private DrawableList bgImageList = new DrawableList();

	protected boolean[] piAxisUnit;

	protected int[] axesTickStyles;

	// for axes labeling with numbers
	protected boolean[] automaticAxesNumberingDistances = { true, true };

	protected double[] axesNumberingDistances;
	private boolean needsAllDrawablesUpdate;
	private boolean batchUpdate;

	/**
	 * @param flag
	 *            true to turn antialiasing on
	 */
	public void setAntialiasing(boolean flag) {
		if (flag == antiAliasing) {
			return;
		}
		antiAliasing = flag;
		repaint();
	}

	/**
	 * @return true if antialising is on
	 */
	public boolean getAntialiasing() {
		return antiAliasing;
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

	public int getEuclidianViewNo() {
		return evNo;
	}

	/**
	 * @param ymaxObjectNew
	 *            the ymaxObject to set
	 */
	public void setYmaxObject(NumberValue ymaxObjectNew) {
		if (ymaxObject != null) {
			((GeoNumeric) ymaxObject).removeEVSizeListener(this);
		}
		if (ymaxObjectNew == null) {
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
	 * @return x-coord of origin
	 */
	public double getxZero() {
		return xZero;
	}

	/**
	 * @return y-coord of origin
	 */
	public double getyZero() {
		return yZero;
	}

	/**
	 * Returns x coordinate of axes origin.
	 */
	public double getXZero() {
		return getxZero();
	}

	/**
	 * Returns y coordinate of axes origin.
	 */
	public double getYZero() {
		return getyZero();
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

	public double getInvXscale() {
		return invXscale;
	}

	public double getInvYscale() {
		return invYscale;
	}

	public int getViewWidth() {
		return getWidth();
	}

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
	}

	/*
	 * void setScaleRatio(double scaleRatio) { this.scaleRatio = scaleRatio; }
	 */

	/**
	 * TODO check whether this is what we want
	 * 
	 * @param minMax
	 *            minima and maxima
	 */
	public void setXYMinMax(double[][] minMax) {
		xmin = (minMax[0][0]);
		xmax = (minMax[0][1]);
		ymin = (minMax[1][0]);
		ymax = (minMax[1][1]);
	}

	protected void setRealWorldBounds() {
		updateBoundObjects();
		updateBounds(false, false);
	}

	/**
	 * Updates xmin, xmax, ... for setCoordSystem()
	 */
	protected void setXYMinMaxForSetCoordSystem() {
		xmin = (-getxZero() * getInvXscale());
		xmax = ((getWidth() - getxZero()) * getInvXscale());
		ymax = (getyZero() * getInvYscale());
		ymin = ((getyZero() - getHeight()) * getInvYscale());
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
		setWidth((int) ((getXmax() * getXscale()) + getxZero()));
		yZero = getYmax() * getYscale();
		setHeight((int) (getyZero() - (getYmin() * getYscale())));

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
		updateAllDrawables(true);

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

	final public void startBatchUpdate() {
		this.batchUpdate = true;
	}

	final public void endBatchUpdate() {
		this.batchUpdate = false;
		if (this.needsAllDrawablesUpdate) {
			allDrawableList.updateAll();
			repaint();
		}
	}

	/**
	 * @param list
	 *            list
	 * @param b
	 *            whether the list should be drawn as combobox
	 */
	public void drawListAsComboBox(GeoList list, boolean b) {

		list.setDrawAsComboBox(b);

		DrawList d = (DrawList) getDrawable(list);
		if (d != null) {
			d.resetDrawType();
		}

	}

	/**
	 * Recompute printing scale
	 */
	public void calcPrintingScale() {
		double unitPerCM = PRINTER_PIXEL_PER_CM / getXscale();
		int exp = (int) Math.round(Math.log(unitPerCM) / Math.log(10));
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
		int exp = (int) Math.floor(Math.log(units) / Math.log(10));

		// if (logAxes[axis]) {
		// exp = (int) Math.log10(exp);
		// }

		int maxFractionDigtis = Math.max(-exp, kernel.getPrintDecimals());

		if (automaticAxesNumberingDistances[axis]) {
			// force same unit if scales are same, see #1082
			if ((axis == 1) && automaticAxesNumberingDistances[0]
					&& Kernel.isEqual(xscale, yscale)) {

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
				double pot = Kernel.checkDecimalFraction(Math.pow(10, exp));
				double n = Kernel.checkDecimalFraction(units / pot);

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

		// NumberFormatAdapter df = axesNumberFormat[axis];

		// display large and small numbers in scienctific notation
		if ((axesNumberingDistances[axis] < 10E-6)
				|| (axesNumberingDistances[axis] > 10E6)) {
			// df.applyPattern("0.##E0");
			maxFractionDigtis = Math.min(14, maxFractionDigtis);
			axesNumberFormat[axis] = FormatFactory.prototype.getNumberFormat(
					"0.##E0", maxFractionDigtis);
			// avoid 4.00000000000004E-11 due to rounding error when
			// computing
			// tick mark numbers
		} else {
			axesNumberFormat[axis] = FormatFactory.prototype.getNumberFormat(
					"###0.##", maxFractionDigtis);
		}

		if (automaticGridDistance && axis < 2) {
			gridDistances[axis] = axesNumberingDistances[axis]
					* EuclidianStyleConstants.automaticGridDistanceFactor;
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
	public void rename(GeoElement geo) {
		Object d = DrawableMap.get(geo);
		if (d != null) {
			((Drawable) d).update();
			repaint();
		}
	}

	public void update(GeoElement geo) {
		DrawableND d = DrawableMap.get(geo);

		if (d != null) {
			if (d instanceof DrawImage) {
				this.updateBackgroundOnNextRepaint |= ((DrawImage) d)
						.checkInBackground();
				return;
			}
			// Keep update of input boxes synchronous #4416
			if ((!geo.isGeoText() || !((GeoText) geo)
					.isNeedsUpdatedBoundingBox())
					&& !geo.isGeoTextField()
					&& (!geo.getTrace() || d.isTracing())) {
				d.setNeedsUpdate(true);
				return;
			}
			d.resetHatch();
			d.update();
		} else if (drawableNeeded(geo) && geosWaiting.contains(geo)) {
			geosWaiting.remove(geo);
			add(geo);
			d = DrawableMap.get(geo);
			if (d != null) {
				d.setNeedsUpdate(true);
				repaint();
			}
		}
	}

	private ArrayList<GeoElement> geosWaiting = new ArrayList<GeoElement>();

	/**
	 * adds a GeoElement to this view
	 */
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
		DrawableND d = getDrawable(geo);

		if (d != null) {
			return;
		}

		d = createDrawable(geo);
		if (d != null) {
			addToDrawableLists((Drawable) d);
			repaint();
		}

	}

	private boolean drawableNeeded(GeoElement geo) {
		return (isVisibleInThisView(geo)
				&& (geo.isLabelSet() || this.isPlotPanel()) && (geo
				.isEuclidianVisible()
				|| (geo.isGeoText() && ((GeoText) geo)
						.isNeedsUpdatedBoundingBox()) || (geo.isGeoAngle() && geo
				.getParentAlgorithm() instanceof AlgoAngle)))
				|| geo.isVisibleInView(App.VIEW_FUNCTION_INSPECTOR);
	}

	public boolean isPlotPanel() {
		return false;
	}

	/**
	 * removes a GeoElement from this view
	 */
	public void remove(GeoElement geo) {
		this.geosWaiting.remove(geo);
		Drawable d = (Drawable) DrawableMap.get(geo);
		int layer = geo.getLayer();
		if (d == null)
			return;
		if (d instanceof RemoveNeeded) {
			drawLayers[layer].remove(d);
			((RemoveNeeded) d).remove();
		} else {
			drawLayers[layer].remove(d);
		}
		allDrawableList.remove(d);

		DrawableMap.remove(geo);
		if (geo.isGeoPoint()) {
			stickyPointList.remove(geo);
		}
		if (!d.isCreatedByDrawListVisible())
			repaint();

	}

	/** get the hits recorded */
	public Hits getHits() {
		return hits;
	}

	/**
	 * @param p
	 *            event coords
	 * @return whether textfield was clicked
	 */
	public boolean textfieldClicked(int x, int y, PointerEventType type) {
		DrawableIterator it = allDrawableList.getIterator();
		while (it.hasNext()) {
			Drawable d = it.next();
			if ((d instanceof DrawTextField)
					&& (d.hit(x, y, app.getCapturingThreshold(type)) || d
							.hitLabel(x, y))) {
				GeoElement geo = d.getGeoElement();
				if (geo.isEuclidianVisible()) {
					((DrawTextField) d).showIntputField(true);
					return true;
				} else {
					((DrawTextField) d).showIntputField(false);
				}
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
	public void setHits(PointerEventType type) {
		setHits(euclidianController.getMouseLoc(), type);
	}

	public void setHits(GPoint p, PointerEventType type) {
		setHits(p, app.getCapturingThreshold(type));
		if (type == PointerEventType.TOUCH && this.hits.size() == 0) {
			setHits(p, app.getCapturingThreshold(type) * 3);
		}
	}

	private ArrayList<GeoElement> hitPointOrBoundary, hitFilling, hitLabel;

	/**
	 * sets the hits of GeoElements whose visual representation is at screen
	 * coords (x,y). order: points, vectors, lines, conics
	 */
	public void setHits(GPoint p, int hitThreshold) {
		hits.init();

		if (hitPointOrBoundary == null) {
			hitPointOrBoundary = new ArrayList<GeoElement>();
			hitFilling = new ArrayList<GeoElement>();
			hitLabel = new ArrayList<GeoElement>();
		} else {
			hitPointOrBoundary.clear();
			hitFilling.clear();
			hitLabel.clear();
		}
		if (p == null) {
			return;
		}
		DrawableIterator it = allDrawableList.getIterator();
		while (it.hasNext()) {
			Drawable d = it.next();
			if (d.isEuclidianVisible()) {
				if (d.hit(p.x, p.y, hitThreshold)) {
					GeoElement geo = d.getGeoElement();
					if (geo.getLastHitType() == HitType.ON_BOUNDARY) {
						hitPointOrBoundary.add(geo);
					} else {
						hitFilling.add(geo);
					}
				} else if (d.hitLabel(p.x, p.y)) {
					GeoElement geo = d.getGeoElement();
					hitLabel.add(geo);
				}
			}
		}

		// labels first
		for (GeoElement geo : hitLabel) {
			hits.add(geo);
		}

		// then points and paths
		for (GeoElement geo : hitPointOrBoundary) {
			hits.add(geo);
		}

		// then regions
		for (GeoElement geo : hitFilling) {
			hits.add(geo);
		}

		// look for axis
		if (hits.getImageCount() == 0) {
			// x axis hit
			if (showAxes[0]
					&& (Math.abs(getYAxisCrossingPixel() - p.y) < hitThreshold)) {
				// handle positive axis only
				if (!positiveAxes[0]
						|| (getXAxisCrossingPixel() < p.x - hitThreshold)) {
					hits.add(kernel.getXAxis());
				}
			}
			// y axis hit
			if (showAxes[1]
					&& (Math.abs(getXAxisCrossingPixel() - p.x) < hitThreshold)) {
				// handle positive axis only
				if (!positiveAxes[1]
						|| (getYAxisCrossingPixel() > p.y - hitThreshold)) {
					hits.add(kernel.getYAxis());
				}
			}
		}

		// keep geoelements only on the top layer
		int maxlayer = 0;
		for (int i = 0; i < hits.size(); ++i) {
			GeoElement geo = hits.get(i);
			if (maxlayer < geo.getLayer()) {
				maxlayer = geo.getLayer();
			}
		}
		for (int i = hits.size() - 1; i >= 0; i--) {
			GeoElement geo = hits.get(i);
			if (geo.getLayer() < maxlayer) {
				hits.remove(i);
			}
		}

		// remove all lists and images if there are other objects too
		if ((hits.size() - (hits.getListCount() + hits.getImageCount())) > 0) {
			for (int i = hits.size() - 1; i >= 0; i--) {
				GeoElement geo = hits.get(i);
				if ((geo.isGeoList() && !((GeoList) geo).drawAsComboBox())
						|| geo.isGeoImage()) {
					hits.remove(i);
				}
			}
		}

	}

	public MyButton getHitButton(org.geogebra.common.awt.GPoint p,
			PointerEventType type) {

		DrawableIterator it = allDrawableList.getIterator();
		Drawable d = null;

		while (it.hasNext()) {
			Drawable d2 = it.next();

			if (d2 instanceof DrawButton
					&& d2.hit(p.x, p.y, app.getCapturingThreshold(type))) {
				if (d == null
						|| d2.getGeoElement().getLayer() >= d.getGeoElement()
								.getLayer())
					d = d2;
			}
		}
		if (d != null)
			return ((DrawButton) d).myButton;
		return null;
	}

	/**
	 * returns GeoElement whose label is at screen coords (x,y).
	 */
	public GeoElement getLabelHit(org.geogebra.common.awt.GPoint p,
			PointerEventType type) {
		if (!getApplication().isLabelDragsEnabled()) {
			return null;
		}
		DrawableIterator it = allDrawableList.getIterator();
		while (it.hasNext()) {
			Drawable d = it.next();
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
	 * Returns the drawable for the given GeoElement.
	 * 
	 * @param geo
	 *            geo
	 * @return drawable for the given GeoElement.
	 */
	protected final DrawableND getDrawable(GeoElement geo) {
		return DrawableMap.get(geo);
	}

	public DrawableND getDrawableND(GeoElement geo) {
		return getDrawable(geo);
	}

	/**
	 * adds a GeoElement to this view
	 * 
	 * @param draw
	 *            drawable to be added
	 */
	protected void addToDrawableLists(Drawable draw) {
		if (draw == null) {
			return;
		}
		Drawable d = draw;
		GeoElement geo = d.getGeoElement();
		int layer = geo.getLayer();

		switch (geo.getGeoClassType()) {

		case ANGLE:
			if (geo.isIndependent()) {
				drawLayers[layer].add(d);
			} else {
				if (geo.isDrawable()) {
					drawLayers[layer].add(d);
				} else {
					d = null;
				}
			}
			break;

		case IMAGE:
			if (!bgImageList.contains(d)) {
				drawLayers[layer].add(d);
			}
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
	 * @param geo
	 *            geo
	 * @return true if geo is visible in this view
	 */
	public boolean isVisibleInThisView(GeoElement geo) {
		return companion.isVisibleInThisView(geo);
	}

	final public DrawableND createDrawableND(GeoElement geo) {
		return createDrawable(geo);
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
			DrawableMap.put(geo, d);
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
	public DrawableND newDrawable(GeoElement geo) {
		return companion.newDrawable(geo);
	}

	public void reset() {
		resetMode();
		updateBackgroundImage();
	}

	/**
	 * clears all selections and highlighting
	 */
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

	public void repaintView() {
		repaint();
	}

	public void updateVisualStyle(GeoElement geo) {
		update(geo);
		if (styleBar != null)
			styleBar.updateVisualStyle(geo);
	}

	final public DrawableND getDrawableFor(GeoElement geo) {
		return DrawableMap.get(geo);
	}

	final public void updateAuxiliaryObject(GeoElement geo) {
		// repaint();
	}

	/**
	 * Updates font size for all drawables
	 */
	protected void updateDrawableFontSize() {
		allDrawableList.updateFontSizeAll();
		repaint();
	}

	/**
	 * @return plain font
	 */
	public org.geogebra.common.awt.GFont getFontPoint() {
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
	public org.geogebra.common.awt.GFont getFontLine() {
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
			return app.getPlainFontCommon();
		}
		return fontCoords;
	}

	private void setFontCoords(GFont fontCoords) {
		this.fontCoords = fontCoords;
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
	public org.geogebra.common.awt.GFont getFontAngle() {
		return getFontPoint();
	}

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

	public String getFromPlaneString() {
		return companion.getFromPlaneString();
	}

	public String getTranslatedFromPlaneString() {
		return companion.getTranslatedFromPlaneString();
	}

	public boolean isDefault2D() {
		return companion.isDefault2D();
	}

	public boolean isEuclidianView3D() {
		return false;
	}

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

	public void changeLayer(GeoElement geo, int oldlayer, int newlayer) {
		drawLayers[oldlayer].remove((Drawable) DrawableMap.get(geo));
		drawLayers[newlayer].add((Drawable) DrawableMap.get(geo));
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
	public Coords getCoordsForView(Coords coords) {
		return companion.getCoordsForView(coords);
	}

	public boolean isMoveable(GeoElement geo) {
		return companion.isMoveable(geo);
	}

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
		updateBounds(true, true);
	}

	/**
	 * @return right angle style
	 */
	final public int getRightAngleStyle() {
		return getApplication().rightAngleStyle;
	}

	public boolean isAutomaticGridDistance() {
		return automaticGridDistance;
	}

	public double[] getGridDistances() {
		return gridDistances;
	}

	public void setGridDistances(double[] dist) {
		if (dist == null) {
			return;
		}
		gridDistances = dist;
		setAutomaticGridDistance(false);
	}

	public int getGridLineStyle() {
		return gridLineStyle;
	}

	public void setAutomaticGridDistance(boolean flag) {
		automaticGridDistance = flag;
		setAxesIntervals(getXscale(), 0);
		setAxesIntervals(getYscale(), 1);
		if (flag) {
			gridDistances[2] = Math.PI / 6;
		}
	}

	public int getAxesLineStyle() {
		return axesLineType;
	}

	public void setAxesLineStyle(int axesLineStyle) {
		this.axesLineType = axesLineStyle;
	}

	/**
	 * @return RW => EV transform; created (but not initialized) when null
	 */
	public org.geogebra.common.awt.GAffineTransform getCoordTransform() {
		if (coordTransform == null)
			coordTransform = AwtFactory.prototype.newAffineTransform();
		return coordTransform;
	}

	/**
	 * @param coordTransform
	 *            RW => EV transform
	 */
	protected void setCoordTransform(
			org.geogebra.common.awt.GAffineTransform coordTransform) {
		this.coordTransform = coordTransform;
	}

	final public void updateBackground() {
		// make sure axis number formats are up to date
		setAxesIntervals(getXscale(), 0);
		setAxesIntervals(getYscale(), 1);

		updateBackgroundImage();
		updateAllDrawables(true);
		// repaint();
	}

	/**
	 * @param fontForGraphics
	 *            font
	 * @return graphics correnspondin to given font
	 */
	public abstract GGraphics2D getTempGraphics2D(
			org.geogebra.common.awt.GFont fontForGraphics);

	/**
	 * @return font
	 */
	public abstract org.geogebra.common.awt.GFont getFont();

	/**
	 * @param h
	 *            new height
	 */
	protected abstract void setHeight(int h);

	/**
	 * @param h
	 *            new width
	 */
	protected abstract void setWidth(int h);

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

	// axis control vars
	protected double[] axisCross;
	protected boolean[] positiveAxes;
	protected boolean[] drawBorderAxes;

	// getters and Setters for axis control vars

	public void setSelectionRectangle(
			org.geogebra.common.awt.GRectangle selectionRectangle) {
		// Application.printStacktrace("");
		this.selectionRectangle = selectionRectangle;
	}

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

	/**
	 * @return true if showing corner coords
	 */
	public boolean isAxesCornerCoordsVisible() {
		return showAxesCornerCoords;
	}

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

	public String[] getAxesLabels(boolean addBoldItalicTags) {
		String[] ret = new String[axesLabels.length];

		for (int axis = 0; axis < axesLabels.length; axis++) {
			ret[axis] = axesLabels[axis];
		}

		if (addBoldItalicTags) {
			for (int axis = 0; axis < axesLabels.length; axis++) {
				if (axesLabels[axis] != null && this.settings!=null) {
					ret[axis] = this.settings.axisLabelForXML(axis);
				}
			}
		}

		return ret;
	}

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

	public void setAutomaticAxesNumberingDistance(boolean flag, int axis) {
		automaticAxesNumberingDistances[axis] = flag;
		setAxesIntervals(getScale(axis), axis);
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
	 *            numbering distance
	 * @param axis
	 *            0 for xAxis, 1 for yAxis
	 */
	public void setAxesNumberingDistance(double dist, int axis) {
		if (!Double.isNaN(dist)) {
			axesNumberingDistances[axis] = dist;
			setAutomaticAxesNumberingDistance(false, axis);
		} else {
			setAutomaticAxesNumberingDistance(true, axis);
		}
	}

	// Michael Borcherds 2008-04-11
	public boolean getGridIsBold() {
		return gridIsBold;
	}

	public boolean[] getShowAxesNumbers() {
		return showAxesNumbers;
	}

	public void setShowAxesNumbers(boolean[] showAxesNumbers) {
		this.showAxesNumbers = showAxesNumbers;
	}

	public void setShowAxisNumbers(int axis, boolean showAxisNumbers) {
		showAxesNumbers[axis] = showAxisNumbers;
	}

	public String[] getAxesUnitLabels() {
		return axesUnitLabels;
	}

	public void setAxesUnitLabels(String[] axesUnitLabels) {
		for(int i=0;i<this.axesUnitLabels.length;i++){
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

	public int[] getAxesTickStyles() {
		return axesTickStyles;
	}

	public void setAxisTickStyle(int axis, int tickStyle) {
		axesTickStyles[axis] = tickStyle;
	}

	public void setAxesTickStyles(int[] axesTickStyles) {
		this.axesTickStyles = axesTickStyles;
	}

	public void setShowMouseCoords(boolean b) {
		showMouseCoords = b;
	}

	public boolean getAllowShowMouseCoords() {
		return allowShowMouseCoords;
	}

	public void setAllowShowMouseCoords(boolean neverShowMouseCoords) {
		this.allowShowMouseCoords = neverShowMouseCoords;
	}

	public boolean getShowMouseCoords() {
		return showMouseCoords;
	}

	public void setShowAxesRatio(boolean b) {
		showAxesRatio = b;
	}

	public Previewable getPreviewDrawable() {
		return previewDrawable;
	}

	public double getGridDistances(int i) {
		return gridDistances[i];
	}

	public boolean getShowGrid() {
		return showGrid;
	}

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
	public boolean getShowAxis(int axis) {
		return showAxes[axis];
	}

	public boolean getShowXaxis() {
		// return showAxes[0];
		return getShowAxis(AXIS_X);
	}

	public boolean getShowYaxis() {
		return getShowAxis(AXIS_Y);
	}

	/*
	 * say if the axis is logarithmic
	 */

	public boolean getLogAxis(int axis) {
		return logAxes[axis];
	}

	public boolean getXaxisLog() {
		return getLogAxis(AXIS_X);
	}

	public boolean getYaxisLog() {
		return getLogAxis(AXIS_Y);
	}

	// ///////////////////////////////////////
	// previewables

	public Previewable createPreviewLine(ArrayList<GeoPointND> selectedPoints) {
		return new DrawLine(this, selectedPoints, PreviewType.LINE);
	}

	public Previewable createPreviewPerpendicularBisector(
			ArrayList<GeoPointND> selectedPoints) {
		return new DrawLine(this, selectedPoints,
				PreviewType.PERPENDICULAR_BISECTOR);
	}

	public Previewable createPreviewAngleBisector(
			ArrayList<GeoPointND> selectedPoints) {
		return new DrawLine(this, selectedPoints, PreviewType.ANGLE_BISECTOR);
	}

	public Previewable createPreviewSegment(ArrayList<GeoPointND> selectedPoints) {
		return new DrawSegment(this, selectedPoints);
	}

	public Previewable createPreviewRay(ArrayList<GeoPointND> selectedPoints) {
		return new DrawRay(this, selectedPoints);
	}

	public Previewable createPreviewVector(ArrayList<GeoPointND> selectedPoints) {
		return new DrawVector(this, selectedPoints);
	}

	public Previewable createPreviewConic(int mode1,
			ArrayList<GeoPointND> selectedPoints) {
		return new DrawConic(this, mode1, selectedPoints);
	}

	public Previewable createPreviewParabola(
			ArrayList<GeoPointND> selectedPoints,
			ArrayList<GeoLineND> selectedLines) {
		return new DrawConic(this, selectedPoints, selectedLines);
	}

	public Previewable createPreviewPolygon(ArrayList<GeoPointND> selectedPoints) {
		return new DrawPolygon(this, selectedPoints);
	}

	public Previewable createPreviewAngle(ArrayList<GeoPointND> selectedPoints) {
		return new DrawAngle(this, selectedPoints);
	}

	public Previewable createPreviewPolyLine(
			ArrayList<GeoPointND> selectedPoints) {
		return new DrawPolyLine(this, selectedPoints);
	}

	public void updatePreviewable() {
		GPoint mouseLoc = getEuclidianController().mouseLoc;
		getPreviewDrawable().updateMousePos(toRealWorldCoordX(mouseLoc.x),
				toRealWorldCoordY(mouseLoc.y));
	}

	public void updatePreviewableForProcessMode() {
		if (getPreviewDrawable() != null) {
			getPreviewDrawable().updatePreview();
		}
	}

	public final void mouseEntered() {
		hasMouse = true;
	}

	public final void mouseExited() {
		hasMouse = false;
	}

	/**
	 * @return whether mouse is hovering over this view
	 */
	public boolean hasMouse() {
		return hasMouse2D();
	}

	/**
	 * @return whether mouse is hovering over this view
	 */
	final public boolean hasMouse2D() {
		return hasMouse;
	}

	public Previewable createPreviewParallelLine(
			ArrayList<GeoPointND> selectedPoints,
			ArrayList<GeoLineND> selectedLines) {
		return new DrawLine(this, selectedPoints, selectedLines, true);
	}

	public Previewable createPreviewPerpendicularLine(
			ArrayList<GeoPointND> selectedPoints,
			ArrayList<GeoLineND> selectedLines) {
		return new DrawLine(this, selectedPoints, selectedLines, false);
	}

	/**
	 * @param application
	 *            application
	 */
	protected void setApplication(App application) {
		this.app = application;
	}

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
		setFontCoords(getApplication().getPlainFontCommon().deriveFont(
				getSettings().getAxisFontStyle(),
				Math.max(Math.round(getFontSize() * 0.75), 10)));
		}
		updateDrawableFontSize();
		updateBackground();
	}

	/**
	 * Size changed, make sure our settings reflect that
	 */
	public void updateSize() {
		updateSizeKeepDrawables();
		updateAllDrawables(true);

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
	public abstract boolean requestFocusInWindow();

	// Michael Borcherds 2008-03-01
	/**
	 * Draws all geometric objects
	 * 
	 * @param g2
	 *            graphics
	 */
	protected void drawGeometricObjects(org.geogebra.common.awt.GGraphics2D g2) {
		// boolean
		// isSVGExtensions=g2.getClass().getName().endsWith("SVGExtensions");
		int layer;

		for (layer = 0; layer <= getApplication().getMaxLayerUsed(); layer++) // only
																				// draw
																				// layers
		// we need
		{
			// if (isSVGExtensions)
			// ((geogebra.export.SVGExtensions)g2).startGroup("layer "+layer);
			drawLayers[layer].drawAll(g2);
			// if (isSVGExtensions)
			// ((geogebra.export.SVGExtensions)g2).endGroup("layer "+layer);
		}
	}

	// Michael Borcherds 2008-03-01
	/**
	 * Draws all objects
	 * 
	 * @param g2
	 *            graphics
	 */
	protected void drawObjects(org.geogebra.common.awt.GGraphics2D g2) {

		drawGeometricObjects(g2);
		drawActionObjects(g2);

		if (previewDrawable != null) {
			previewDrawable.drawPreview(g2);
		}
	}

	/**
	 * Fills background with background color
	 * 
	 * @param g
	 *            graphics
	 */
	final protected void clearBackground(org.geogebra.common.awt.GGraphics2D g) {
		g.setColor(getBackgroundCommon());
		g.updateCanvasColor();
		g.fillRect(0, 0, getWidth(), getHeight());
	}

	/**
	 * @param g
	 *            graphics
	 * @param transparency
	 *            alpha value
	 */
	protected void drawBackgroundWithImages(org.geogebra.common.awt.GGraphics2D g,
			boolean transparency) {
		if (!transparency) {
			clearBackground(g);
		}

		bgImageList.drawAll(g);
		drawBackground(g, false);
	}

	/**
	 * Draw axes ratio next to the mouse when mouse zooming.
	 * 
	 * @param g2
	 *            graphics
	 */
	final protected void drawAxesRatio(org.geogebra.common.awt.GGraphics2D g2) {
		GPoint pos = euclidianController.mouseLoc;
		if (pos == null) {
			return;
		}

		g2.setColor(org.geogebra.common.awt.GColor.DARK_GRAY);
		g2.setFont(getFontLine());
		g2.drawString(getXYscaleRatioString(), pos.x + 15, pos.y + 30);
	}

	/**
	 * @param g2
	 *            background graphics
	 */
	public abstract void paintBackground(org.geogebra.common.awt.GGraphics2D g2);

	/** reIniting is used by GeoGebraWeb */
	protected boolean reIniting = false;

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
			bgImage = null;
			bgGraphics = null;
		}
	}

	/**
	 * Paints content of this view.
	 * 
	 * @param g2
	 *            graphics
	 */
	public void paint(org.geogebra.common.awt.GGraphics2D g2) {
		synchronized (kernel.getConcurrentModificationLock()) {
			// synchronized means that no two Threads can simultaneously
			// enter any blocks locked by the same lock object,
			// but they can only wait for the active Thread to exit from
			// these blocks... as there is only one lock object and
			// these methods probably do not call other synchronized
			// code blocks, it probably does not cause any problem
			companion.paint(g2);
		}
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
	 * Draws zoom rectangle
	 * 
	 * @param g2
	 *            graphics
	 */
	protected void drawZoomRectangle(org.geogebra.common.awt.GGraphics2D g2) {
		g2.setColor(colZoomRectangleFill);
		g2.setStroke(boldAxesStroke);
		g2.fill(selectionRectangle);
		g2.setColor(colZoomRectangle);
		g2.draw(selectionRectangle);
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
	final protected void drawMouseCoords(org.geogebra.common.awt.GGraphics2D g2) {
		StringTemplate tpl = StringTemplate.defaultTemplate;
		if (euclidianController.mouseLoc == null) {
			return;
		}
		GPoint pos = euclidianController.mouseLoc;
		StringBuilder sb = new StringBuilder();
		sb.setLength(0);
		sb.append('(');
		sb.append(kernel.format(
				Kernel.checkDecimalFraction(euclidianController.xRW), tpl));
		if (kernel.getCoordStyle() == Kernel.COORD_STYLE_AUSTRIAN) {
			sb.append(" | ");
		} else {
			sb.append(", ");
		}
		sb.append(kernel.format(
				Kernel.checkDecimalFraction(euclidianController.yRW), tpl));
		sb.append(')');

		g2.setColor(GColor.DARK_GRAY);
		g2.setFont(getFontCoords());
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

		setAntialiasing(g);

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
		axesLabelsPositionsY.clear();
		axesLabelsPositionsX.clear();

		yLabelMaxWidthPos = 0;
		yLabelMaxWidthNeg = 0;
		xLabelHeights = estimateNumberHeight(getFontAxes());

		// this will fill axesLabelsBounds with the rectangles where the axes
		// labels are
		if (showAxes[0] || showAxes[1]) {
			if (da == null) {
				da = new DrawAxis(this);
			}
			da.drawAxes(g);
			drawCornerCoordsIfNeeded(g);
		}

		if (showGrid) {
			drawGrid(g);
		}

		if (showResetIcon()) {
			drawResetIcon(g);
		}
	}

	private void drawCornerCoordsIfNeeded(GGraphics2D g2) {
		// if both axes visible and
		// one of the axes is off-screen, show upper left and lower right
		if (showAxesCornerCoords) {
			if (showAxes[0] && showAxes[1]
					&& (!xAxisOnscreen() || !yAxisOnscreen())) {
				// upper left corner
				StringBuilder sb = new StringBuilder();
				sb.setLength(0);
				sb.append('(');
				sb.append(kernel.formatPiE(getXmin(), axesNumberFormat[0],
						StringTemplate.defaultTemplate));
				sb.append(app.getLocalization().unicodeComma);
				sb.append(" ");
				sb.append(kernel.formatPiE(getYmax(), axesNumberFormat[1],
						StringTemplate.defaultTemplate));
				sb.append(')');

				int textHeight = 2 + getFontAxes().getSize();
				g2.setFont(getFontAxes());
				g2.drawString(sb.toString(), 5, textHeight);

				// lower right corner
				sb.setLength(0);
				sb.append('(');
				sb.append(kernel.formatPiE(getXmax(), axesNumberFormat[0],
						StringTemplate.defaultTemplate));
				sb.append(app.getLocalization().unicodeComma);
				sb.append(" ");
				sb.append(kernel.formatPiE(getYmin(), axesNumberFormat[1],
						StringTemplate.defaultTemplate));
				sb.append(')');

				GTextLayout layout = AwtFactory.prototype.newTextLayout(
sb.toString(), getFontAxes(),
								g2.getFontRenderContext());
				layout.draw(g2, (int) (getWidth() - 5 - EuclidianView
						.estimateTextWidth(sb.toString(), getFontAxes())),
						getHeight() - 5);
			}
		}

	}

	private DrawAxis da;

	boolean showResetIcon() {
		if(!getApplication().showResetIcon()
				|| ! (getApplication().isApplet() || getApplication().isHTML5Applet())){
			return false;
		};
		return isPrimaryEV();
	}

	private GEllipse2DDouble circle = AwtFactory.prototype.newEllipse2DDouble(); // polar
																					// grid
																					// circles
	private GLine2D tempLine = AwtFactory.prototype.newLine2D();
	GGeneralPath gp;
	/**
	 * Get styleBar
	 */
	protected org.geogebra.common.euclidian.EuclidianStyleBar styleBar;

	/**
	 * Draws grid
	 * 
	 * @param g2
	 *            graphics
	 */
	final protected void drawGrid(GGraphics2D g2) {

		// vars for handling positive-only axes
		double xCrossPix = this.getxZero() + (axisCross[1] * getXscale());
		double yCrossPix = this.getyZero() - (axisCross[0] * getYscale());
		int yAxisEnd = (positiveAxes[1] && yCrossPix < getHeight()) ? (int) yCrossPix
				: getHeight();
		int xAxisStart = (positiveAxes[0] && xCrossPix > 0) ? (int) xCrossPix
				: 0;

		// set the clipping region to the region defined by the axes
		GShape oldClip = g2.getClip();
		if (gridType != GRID_POLAR) {
			g2.setClip(xAxisStart, 0, getWidth(), yAxisEnd);
		}

		// this needs to be after setClip()
		// bug in FreeHEP (PDF export)
		// #2683
		g2.setColor(gridColor);
		g2.setStroke(gridStroke);

		switch (gridType) {

		case GRID_CARTESIAN:

			// vertical grid lines
			double tickStep = getXscale() * gridDistances[0];
			double start = getxZero() % tickStep;
			double pix = start;
			double rw = getXmin() - (getXmin() % axesNumberingDistances[0]);
			double rwBase = Kernel.checkDecimalFraction(rw);
			if (pix < SCREEN_BORDER) {
				pix += tickStep;
				if (!getXaxisLog() || getXmin() < 0)
					rw += axesNumberingDistances[0];
			}
			for (int i = 0; pix <= getWidth(); i++) {
				// don't draw the grid line x=0 if the y-axis is showing
				// or if it's too close (eg sticky axes)
				if(getXaxisLog()){
					double r = rwBase + Kernel.checkDecimalFraction(axesNumberingDistances[0] * i);
					if (Math.round(r) == r)
						rw = Math.pow(10, r); // condition of integer power
					else {
						rw = Math.pow(10, (int) r);
						double decimal = r - (int) r;
						rw = decimal * 10 * rw;
					}
					pix = toScreenCoordXd(rw);
				}
				if (!showAxes[1] || Math.abs(pix - xCrossPix) > 2d) {
					if (axesLabelsPositionsX.contains(new Integer(
							(int) (pix + Kernel.MIN_PRECISION)))) {

						// hits axis label, draw in 2 sections
						drawLineAvoidingLabelsV(g2, pix, 0, pix, getHeight(),
								yCrossPix);
					} else {

						// not hitting axis label, just draw it
						g2.drawStraightLine(pix, 0, pix, getHeight());

					}

				}

				pix = start + (i * tickStep);
			}

			// horizontal grid lines
			tickStep = getYscale() * gridDistances[1];
			start = getyZero() % tickStep;
			pix = start;
			rw = getYmin() - (getYmin() % axesNumberingDistances[1]);
			rwBase = Kernel.checkDecimalFraction(rw);
			if (pix > (getHeight() - SCREEN_BORDER)) {
				pix -= tickStep;
				if (!getYaxisLog() || getYmin() < 0)
					rw += axesNumberingDistances[1];
			}
			for (int j = 0; pix <= getHeight(); j++) {
				// don't draw the grid line x=0 if the y-axis is showing
				// or if it's too close (eg sticky axes)
				if (getYaxisLog()) {
					double r = rwBase
							+ Kernel.checkDecimalFraction(axesNumberingDistances[1]
									* j);
					if (Math.round(r) == r)
						rw = Math.pow(10, r); // condition of integer power
					else {
						rw = Math.pow(10, (int) r);
						double decimal = r - (int) r;
						rw = decimal * 10 * rw;
					}
					pix = 2 * getYZero() - toScreenCoordYd(rw);
				}

				if (!showAxes[0] || Math.abs(pix - yCrossPix) > 2d) {

					if (axesLabelsPositionsY.contains(new Integer(
							(int) (pix + Kernel.MIN_PRECISION)))) {

						// hits axis label, draw in 2 sections
						drawLineAvoidingLabelsH(g2, 0, pix, getWidth(), pix,
								xCrossPix);
					} else {

						// not hitting axis label, just draw it
						g2.drawStraightLine(0, pix, getWidth(), pix);
					}
				}

				pix = start + (j * tickStep);
			}

			break;

		case GRID_ISOMETRIC:

			double tickStepX = getXscale() * gridDistances[0] * Math.sqrt(3.0);
			double startX = getxZero() % (tickStepX);
			double startX2 = getxZero() % (tickStepX / 2);
			double tickStepY = getYscale() * gridDistances[0];
			double startY = getyZero() % tickStepY;

			// vertical
			pix = startX2;
			for (int j = 0; pix <= getWidth(); j++) {
				tempLine.setLine(pix, 0, pix, getHeight());
				g2.draw(tempLine);
				pix = startX2 + ((j * tickStepX) / 2.0);
			}

			// extra lines needed because it's diagonal
			int extra = (int) ((((getHeight() * getXscale()) / getYscale()) * Math
					.sqrt(3.0)) / tickStepX) + 3;

			// positive gradient
			pix = startX + (-(extra + 1) * tickStepX);
			for (int j = -extra; pix <= getWidth(); j += 1) {
				tempLine.setLine(
						pix,
						startY - tickStepY,
						pix
								+ (((getHeight() + tickStepY) * Math.sqrt(3) * getXscale()) / getYscale()),
						(startY - tickStepY) + getHeight() + tickStepY);
				g2.draw(tempLine);
				pix = startX + (j * tickStepX);
			}

			// negative gradient
			pix = startX;
			for (int j = 0; pix <= (getWidth() + ((((getHeight() * getXscale()) / getYscale()) + tickStepY) * Math
					.sqrt(3.0))); j += 1)
			// for (int j=0; j<=kk; j+=1)
			{
				tempLine.setLine(
						pix,
						startY - tickStepY,
						pix
								- (((getHeight() + tickStepY) * Math.sqrt(3) * getXscale()) / getYscale()),
						(startY - tickStepY) + getHeight() + tickStepY);
				g2.draw(tempLine);
				pix = startX + (j * tickStepX);
			}

			break;

		case GRID_POLAR: // G.Sturr 2010-8-13

			// find minimum grid radius
			double min;
			if ((getxZero() > 0) && (getxZero() < getWidth())
					&& (getyZero() > 0) && (getyZero() < getHeight())) {
				// origin onscreen: min = 0
				min = 0;
			} else {
				// origin offscreen: min = distance to closest screen border
				double minW = Math.min(Math.abs(getxZero()),
						Math.abs(getxZero() - getWidth()));
				double minH = Math.min(Math.abs(getyZero()),
						Math.abs(getyZero() - getHeight()));
				min = Math.min(minW, minH);
			}

			// find maximum grid radius
			// max = max distance of origin to screen corners
			double d1 = MyMath.length(getxZero(), getyZero()); // upper left
			double d2 = MyMath.length(getxZero(), getyZero() - getHeight()); // lower
																				// left
			double d3 = MyMath.length(getxZero() - getWidth(), getyZero()); // upper
																			// right
			double d4 = MyMath.length(getxZero() - getWidth(), getyZero()
					- getHeight()); // lower
			// right
			double max = Math.max(Math.max(d1, d2), Math.max(d3, d4));

			// draw the grid circles
			// note: x tick intervals are used for the radius intervals,
			// it is assumed that the x/y scaling ratio is 1:1
			double tickStepR = getXscale() * gridDistances[0];
			double r = min - (min % tickStepR);
			while (r <= max) {
				circle.setFrame(getxZero() - r, getyZero() - r, 2 * r, 2 * r);
				g2.draw(circle);
				r = r + tickStepR;

			}

			// draw the radial grid lines
			double angleStep = gridDistances[2];
			double y1,
			y2,
			m;

			// horizontal axis
			tempLine.setLine(0, getyZero(), getWidth(), getyZero());
			g2.draw(tempLine);

			// radial lines
			for (double a = angleStep; a < Math.PI; a = a + angleStep) {

				if (Math.abs(a - (Math.PI / 2)) < 0.0001) {
					// vertical axis
					tempLine.setLine(getxZero(), 0, getxZero(), getHeight());
				} else {
					m = Math.tan(a);
					y1 = (m * (getxZero())) + getyZero();
					y2 = (m * (getxZero() - getWidth())) + getyZero();
					tempLine.setLine(0, y1, getWidth(), y2);
				}
				g2.draw(tempLine);
			}

			break;
		}

		// reset the clipping region
		g2.setClip(oldClip);
	}

	// =================================================
	// Draw Axes
	// =================================================

	double getXAxisCrossingPixel() {
		return getxZero() + (axisCross[1] * getXscale());
	}

	double getYAxisCrossingPixel() {
		return this.getyZero() - (axisCross[0] * getYscale());
	}

	/**
	 * Draws axes
	 * 
	 * @param g2
	 *            graphics
	 */


	boolean xAxisOnscreen() {
		return showAxes[0] && (getYmin() < axisCross[0])
				&& (getYmax() > axisCross[0]);
	}

	boolean yAxisOnscreen() {
		return showAxes[1] && (getXmin() < axisCross[1])
				&& (getXmax() > axisCross[1]);
	}

	protected int getYOffsetForXAxis(int fontSize) {
		return fontSize + 4;
	}

	ArrayList<Integer> axesLabelsPositionsY = new ArrayList<Integer>();
	ArrayList<Integer> axesLabelsPositionsX = new ArrayList<Integer>();
	double yLabelMaxWidthPos = 0;
	double yLabelMaxWidthNeg = 0;
	double xLabelHeights = 0;

	private void drawLineAvoidingLabelsH(GGraphics2D g2, double x1, double y1,
			double x2, double y2, double xCrossPix) {

		if (xCrossPix > x1 && xCrossPix < x2) {
			// split in 2
			g2.drawStraightLine(x1, y1, xCrossPix
					- (this.toRealWorldCoordY(y1) > 0 ? yLabelMaxWidthPos
							: yLabelMaxWidthNeg) - 10, y2);
			g2.drawStraightLine(xCrossPix, y1, x2, y2);

		} else {
			g2.drawStraightLine(x1, y1, x2, y2);
		}
	}

	private void drawLineAvoidingLabelsV(GGraphics2D g2, double x1, double y1,
			double x2, double y2, double yCrossPix) {

		if (yCrossPix > y1 && yCrossPix < y2) {
			// split in 2
			g2.drawStraightLine(x1, y1, x2, yCrossPix);

			g2.drawStraightLine(x1, yCrossPix + xLabelHeights + 5, x2, y2);

		} else {
			g2.drawStraightLine(x1, y1, x2, y2);
		}

	}

	/*
	 * spaceToLeft so that minus signs are more visible next to grid
	 */
	void drawString(GGraphics2D g2, String text, double x, double y) {

		g2.setColor(axesColor);
		g2.drawString(text, (int) (x), (int) y);

	}

	/**
	 * @param g
	 *            graphics for reset icon
	 */
	protected abstract void drawResetIcon(GGraphics2D g);

	/**
	 * Draw textfields
	 * 
	 * @param g
	 *            graphics
	 */
	public abstract void drawActionObjects(GGraphics2D g);

	/**
	 * @param g2
	 *            graphics whose hints should be set
	 */
	public void setDefRenderingHints(GGraphics2D g2) {
		// for Desktop only
	}

	/**
	 * Switch antialiasing to true for given graphics
	 * 
	 * @param g2
	 *            graphics
	 */
	protected abstract void setAntialiasing(GGraphics2D g2);

	/**
	 * @param g2
	 *            graphics for the animation button
	 */
	protected void drawAnimationButtons(GGraphics2D g2) {
		// it could be abstract, but mess with EuclididanView3D
	}

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

	public boolean showGrid(boolean show) {
		return companion.showGrid(show);
	}


	public void setGridIsBold(boolean gridIsBold) {
		if (this.gridIsBold == gridIsBold) {
			return;
		}

		this.gridIsBold = gridIsBold;
		setGridLineStyle(gridLineStyle);

		updateBackgroundImage();
	}

	public void setGridColor(GColor gridColor) {
		if (gridColor != null) {
			this.gridColor = gridColor;
		}
	}

	public void setGridLineStyle(int gridLineStyle) {
		this.gridLineStyle = gridLineStyle;
		gridStroke = EuclidianStatic.getStroke(gridIsBold ? 2f : 1f,
				gridLineStyle);
	}

	/**
	 * @param settings
	 *            settings
	 */
	public void settingsChanged(AbstractSettings settings) {

		companion.settingsChanged(settings);

		if (styleBar != null) {
			styleBar.updateGUI();
		}

	}

	public EuclidianSettings getSettings() {
		return this.settings;
	}

	/**
	 * sets array of GeoElements whose visual representation is inside of the
	 * given screen rectangle
	 */
	public final void setHits(GRectangle rect) {
		hits.init();
		if (rect == null) {
			return;
		}

		DrawableIterator it = allDrawableList.getIterator();
		while (it.hasNext()) {
			Drawable d = it.next();
			GeoElement geo = d.getGeoElement();
			if (geo.isEuclidianVisible() && d.isInside(rect)) {
				hits.add(geo);
			}
		}
	}

	public void updateCursor(GeoPointND point) {
		// used in 3D
	}

	/**
	 * sets array of GeoElements whose visual representation is inside of the
	 * given screen rectangle
	 * 
	 * @param rect
	 *            rectangle
	 */
	public final void setIntersectionHits(GRectangle rect) {
		hits.init();
		if (rect == null) {
			return;
		}

		DrawableIterator it = allDrawableList.getIterator();
		while (it.hasNext()) {
			Drawable d = it.next();
			GeoElement geo = d.getGeoElement();
			if (geo.isEuclidianVisible() && d.intersectsRectangle(rect)) {
				hits.add(geo);
			}
		}
	}

	public GRectangle getSelectionRectangle() {
		return selectionRectangle;
	}

	/**
	 * @return path along border of this view
	 */
	public GeneralPathClipped getBoundingPath() {
		GeneralPathClipped gs = new GeneralPathClipped(this);// AwtFactory.prototype.newGeneralPath();
		gs.moveTo(0, 0);
		gs.lineTo(getWidth(), 0);
		gs.lineTo(getWidth(), getHeight());
		gs.lineTo(0, getHeight());
		gs.lineTo(0, 0);
		gs.closePath();
		return gs;
	}

	/**
	 * @param img
	 *            new background image
	 */
	final public void addBackgroundImage(DrawImage img) {
		bgImageList.addUnique(img);
		// drawImageList.remove(img);

		// Michael Borcherds 2008-02-29
		int layer = img.getGeoElement().getLayer();
		drawLayers[layer].remove(img);
	}

	/**
	 * @param img
	 *            background image
	 */
	final public void removeBackgroundImage(DrawImage img) {
		bgImageList.remove(img);
		// drawImageList.add(img);

		// Michael Borcherds 2008-02-29
		int layer = img.getGeoElement().getLayer();
		drawLayers[layer].add(img);
	}

	/**
	 * Reset lists of drawables
	 */
	protected void resetLists() {
		DrawableMap.clear();
		stickyPointList.clear();
		allDrawableList.clear();
		bgImageList.clear();
		this.geosWaiting.clear();

		for (int i = 0; i <= getApplication().getMaxLayerUsed(); i++) {
			drawLayers[i].clear(); // Michael Borcherds 2008-02-29
		}

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

		DrawableIterator it = allDrawableList.getIterator();
		while (it.hasNext()) {
			Drawable d = it.next();
			GRectangle bb = d.getBounds();
			if (bb != null) {
				if (result == null) {
					// changed () to (bb) bugfix, otherwise top-left of screen
					// is always included
					result = AwtFactory.prototype.newRectangle(bb);
				}
				// add bounding box of list element
				result.add(bb);
			}
		}

		// Cong Liu
		if (result == null) {
			result = AwtFactory.prototype.newRectangle(0, 0, 0, 0);
		}
		return result;
	}

	public void setPreview(Previewable p) {
		if (previewDrawable != null) {
			previewDrawable.disposePreview();
		}
		previewDrawable = p;
	}

	private int widthTemp, heightTemp;
	private double xminTemp, xmaxTemp, yminTemp, ymaxTemp;

	/**
	 * Stores current coord system to temp variables and sets new coord system
	 * for export
	 */
	final public void setTemporaryCoordSystemForExport() {
		widthTemp = getWidth();
		heightTemp = getHeight();
		xminTemp = getXmin();
		xmaxTemp = getXmax();
		yminTemp = getYmin();
		ymaxTemp = getYmax();

		try {
			GeoPoint export1 = (GeoPoint) getApplication().getKernel()
					.lookupLabel(EuclidianView.EXPORT1);
			GeoPoint export2 = (GeoPoint) getApplication().getKernel()
					.lookupLabel(EuclidianView.EXPORT2);

			if ((export1 == null) || (export2 == null)) {
				return;
			}

			double[] xy1 = new double[2];
			double[] xy2 = new double[2];
			export1.getInhomCoords(xy1);
			export2.getInhomCoords(xy2);

			setRealWorldCoordSystem(Math.min(xy1[0], xy2[0]),
					Math.max(xy1[0], xy2[0]), Math.min(xy1[1], xy2[1]),
					Math.max(xy1[1], xy2[1]));

		} catch (Exception e) {
			restoreOldCoordSystem();
		}
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

		int yAxisHeight = positiveAxes[1] ? (int) getyZero() : getHeight();
		int maxY = positiveAxes[1] ? (int) getyZero() : getHeight()
				- SCREEN_BORDER;

		double rw = getYmax() - (getYmax() % axesNumberingDistances[1]);
		double pix = getyZero() - (rw * getYscale());
		double axesStep = getYscale() * axesNumberingDistances[1]; // pixelstep

		axesNumberingDistances[1] = Kernel
				.checkDecimalFraction(axesNumberingDistances[1]);

		int count = 0;
		double rwBase = Kernel.checkDecimalFraction(rw);

		// for (; pix <= yAxisHeight; rw -= axesNumberingDistances[1], pix +=
		// axesStep) {
		for (; pix <= yAxisHeight; count++, pix += axesStep) {

			// 285, 285.1, 285.2 -> rounding problems
			rw = rwBase
					- Kernel.checkDecimalFraction(axesNumberingDistances[1]
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
					if (max.y == 0)
						max.y = (int) estimateNumberHeight(getFontAxes());
				}
			}
		}
		return max;
	}

	/**
	 * Restore coord system from temp variables
	 */
	final public void restoreOldCoordSystem() {
		setWidth(widthTemp);
		setHeight(heightTemp);
		setRealWorldCoordSystem(xminTemp, xmaxTemp, yminTemp, ymaxTemp);
	}

	/**
	 * used for rescaling applets when the reset button is hit use
	 * setTemporarySize(-1, -1) to disable
	 * 
	 * @param w
	 *            width
	 * @param h
	 *            height
	 */
	public void setTemporarySize(int w, int h) {
		setWidth(w);
		setHeight(h);
		updateSize();
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

	public boolean setShowAxes(boolean flag, boolean update) {
		boolean changedX = setShowAxis(AXIS_X, flag, false);
		return setShowAxis(AXIS_Y, flag, true) || changedX;
	}

	/*
	 * change logarithmic flag of the axes
	 */

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

	public boolean setLogAxes(boolean flag, boolean update) {
		boolean changedX = setLogAxis(AXIS_X, flag, false);
		return setLogAxis(AXIS_Y, flag, true) || changedX;
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
		DrawableIterator it = allDrawableList.getIterator();
		while (it.hasNext()) {
			if (it.next().isTracing()) {
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

	/** Whether some trace was painted in this view */
	protected boolean tracing = false;

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

		if ((getWidth() > MIN_WIDTH) && (getHeight() > MIN_HEIGHT)) {
			sbxml.append("\t<size ");
			sbxml.append(" width=\"");
			sbxml.append(getWidth());
			sbxml.append("\"");
			sbxml.append(" height=\"");
			sbxml.append(getHeight());
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
			sbxml.append(getxZero());
			sbxml.append("\"");
			sbxml.append(" yZero=\"");
			sbxml.append(getyZero());
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
		sbxml.append(getPointCapturingMode() > EuclidianStyleConstants.POINT_CAPTURING_XML_MAX ? EuclidianStyleConstants.POINT_CAPTURING_DEFAULT
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

		sbxml.append("\" checkboxSize=\"");
		sbxml.append(app.getCheckboxSize()); // Michael Borcherds
												// 2008-05-12

		sbxml.append("\" gridType=\"");
		sbxml.append(getGridType()); // cartesian/isometric/polar

		if (lockedAxesRatio != null) {
			sbxml.append("\" lockedAxesRatio=\"");
			sbxml.append(lockedAxesRatio);
		}

		sbxml.append("\"/>\n");

		// background color
		sbxml.append("\t<bgColor r=\"");
		sbxml.append(getBackgroundCommon().getRed());
		sbxml.append("\" g=\"");
		sbxml.append(getBackgroundCommon().getGreen());
		sbxml.append("\" b=\"");
		sbxml.append(getBackgroundCommon().getBlue());
		sbxml.append("\"/>\n");

		// axes color
		sbxml.append("\t<axesColor r=\"");
		sbxml.append(axesColor.getRed());
		sbxml.append("\" g=\"");
		sbxml.append(axesColor.getGreen());
		sbxml.append("\" b=\"");
		sbxml.append(axesColor.getBlue());
		sbxml.append("\"/>\n");

		// grid color
		sbxml.append("\t<gridColor r=\"");
		sbxml.append(gridColor.getRed());
		sbxml.append("\" g=\"");
		sbxml.append(gridColor.getGreen());
		sbxml.append("\" b=\"");
		sbxml.append(gridColor.getBlue());
		sbxml.append("\"/>\n");

		// axes line style
		sbxml.append("\t<lineStyle axes=\"");
		sbxml.append(axesLineType);
		sbxml.append("\" grid=\"");
		sbxml.append(gridLineStyle);
		sbxml.append("\"/>\n");

		// axes label style
		int style = getSettings().getAxisFontStyle();
		if (style == GFont.BOLD || style == GFont.ITALIC
				|| style == GFont.BOLD + GFont.ITALIC) {
			sbxml.append("\t<labelStyle axes=\"");
			sbxml.append(style);
			sbxml.append("\"/>\n");
		}

		// axis settings
		for (int i = 0; i < 2; i++) {
			getSettings().addAxisXML(i, sbxml);
		}

		// grid distances
		if (!automaticGridDistance || (// compatibility to v2.7:
				EuclidianStyleConstants.automaticGridDistanceFactor != EuclidianStyleConstants.DEFAULT_GRID_DIST_FACTOR)) {
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
	 * Draws points into an image
	 * 
	 * @param ge
	 *            image
	 * @param x
	 *            array of x-coords
	 * @param y
	 *            array of y-coords
	 */
	public void drawPoints(GeoImage ge, double[] x, double[] y) {
		ArrayList<GPoint> ptList = new ArrayList<GPoint>();
		for (int i = 0; i < x.length; i++) {
			int xi = toScreenCoordX(x[i]);
			int yi = toScreenCoordY(y[i]);
			if (ge.getCorner(1) != null) {
				int w = ge.getFillImage().getWidth();
				int h = ge.getFillImage().getHeight();

				double cx[] = new double[3], cy[] = new double[3];
				for (int j = 0; j < (ge.getCorner(2) != null ? 3 : 2); j++) {
					cx[j] = ge.getCorner(j).x;
					cy[j] = ge.getCorner(j).y;
				}
				if (ge.getCorner(2) == null) {
					cx[2] = cx[0] - ((h * (cy[1] - cy[0])) / w);
					cy[2] = cy[0] + ((h * (cx[1] - cx[0])) / w);
				}
				double dx1 = cx[1] - cx[0];
				double dx2 = cx[2] - cx[0];
				double dy1 = cy[1] - cy[0];
				double dy2 = cy[2] - cy[0];
				double ratio1 = (((x[i] - cx[0]) * dy2) - (dx2 * (y[i] - cy[0])))
						/ ((dx1 * dy2) - (dx2 * dy1));
				double ratio2 = ((-(x[i] - cx[0]) * dy1) + (dx1 * (y[i] - cy[0])))
						/ ((dx1 * dy2) - (dx2 * dy1));

				xi = (int) Math.round(w * ratio1);
				yi = (int) Math.round(h * (1 - ratio2));

			} else if (ge.getCorner(0) != null) {
				xi = xi - toScreenCoordX(ge.getCorner(0).x);
				yi = ge.getFillImage().getHeight()
						+ (yi - toScreenCoordY(ge.getCorner(0).y));
			}
			ptList.add(new GPoint(xi, yi));
		}
		doDrawPoints(ge, ptList, GColor.BLACK,
				EuclidianStyleConstants.LINE_TYPE_FULL, 1);

	}

	@SuppressWarnings("javadoc")
	protected abstract void doDrawPoints(GeoImage gi, List<GPoint> penPoints2,
			GColor penColor, int penLineStyle, int penSize);

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
	 */
	public void setViewShowAllObjects(boolean storeUndo) {

		double x0RW = getXmin();
		double x1RW;
		double y0RW;
		double y1RW;
		double y0RWfunctions = 0;
		double y1RWfunctions = 0;
		double factor = 0.03d; // don't want objects at edge
		double xGap = 0;

		TreeSet<GeoElement> allFunctions = kernel.getConstruction()
				.getGeoSetLabelOrder(GeoClass.FUNCTION);

		int noVisible = 0;
		// count no of visible functions
		Iterator<GeoElement> it = allFunctions.iterator();
		while (it.hasNext()) {
			if (((GeoFunction) (it.next())).isEuclidianVisible()) {
				noVisible++;
			}
		}

		GRectangle rect = getBounds();
		if (Kernel.isZero(rect.getHeight()) || Kernel.isZero(rect.getWidth())) {
			if (noVisible == 0) {
				return; // no functions or objects
			}

			// just functions
			x0RW = Double.MAX_VALUE;
			x1RW = -Double.MAX_VALUE;
			y0RW = Double.MAX_VALUE;
			y1RW = -Double.MAX_VALUE;

		} else {

			// get bounds of points, circles etc
			x0RW = toRealWorldCoordX(rect.getMinX());
			x1RW = toRealWorldCoordX(rect.getMaxX());
			y0RW = toRealWorldCoordY(rect.getMaxY());
			y1RW = toRealWorldCoordY(rect.getMinY());
		}

		xGap = (x1RW - x0RW) * factor;

		boolean ok = false;

		if (noVisible != 0) {

			// if there are functions we don't want to zoom in horizintally
			x0RW = Math.min(getXmin(), x0RW);
			x1RW = Math.max(getXmax(), x1RW);

			if (Kernel.isEqual(x0RW, getXmin())
					&& Kernel.isEqual(x1RW, getXmax())) {
				// just functions (at sides!), don't need a gap
				xGap = 0;
			} else {
				xGap = (x1RW - x0RW) * factor;
			}

			y0RWfunctions = Double.MAX_VALUE;
			y1RWfunctions = -Double.MAX_VALUE;

			it = allFunctions.iterator();

			while (it.hasNext()) {
				GeoFunction fun = (GeoFunction) (it.next());
				double abscissa;
				// check 100 random heights
				for (int i = 0; i < 200; i++) {

					if (i == 0) {
						abscissa = fun.evaluate(x0RW); // check far left
					} else if (i == 1) {
						abscissa = fun.evaluate(x1RW); // check far right
					} else {
						abscissa = fun.evaluate(x0RW
								+ (Math.random() * (x1RW - x0RW)));
					}

					if (!Double.isInfinite(abscissa) && !Double.isNaN(abscissa)) {
						ok = true;
						if (abscissa > y1RWfunctions) {
							y1RWfunctions = abscissa;
						}
						// no else: there **might** be just one value
						if (abscissa < y0RWfunctions) {
							y0RWfunctions = abscissa;
						}
					}
				}
			}

		}

		if (!Kernel.isZero(y1RWfunctions - y0RWfunctions) && ok) {
			y0RW = Math.min(y0RW, y0RWfunctions);
			y1RW = Math.max(y1RW, y1RWfunctions);
		}

		// don't want objects at edge
		double yGap = (y1RW - y0RW) * factor;

		final double x0RW2 = x0RW - xGap;
		final double x1RW2 = x1RW + xGap;
		final double y0RW2 = y0RW - yGap;
		final double y1RW2 = y1RW + yGap;

		setAnimatedRealWorldCoordSystem(x0RW2, x1RW2, y0RW2, y1RW2, 10,
				storeUndo);

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

	/**
	 * @return export width in pixels
	 */
	public int getExportWidth() {
		if (selectionRectangle != null) {
			return (int) selectionRectangle.getWidth();
		}
		try {
			GeoPoint export1 = (GeoPoint) kernel.lookupLabel(EXPORT1);
			GeoPoint export2 = (GeoPoint) kernel.lookupLabel(EXPORT2);
			double[] xy1 = new double[2];
			double[] xy2 = new double[2];
			export1.getInhomCoords(xy1);
			export2.getInhomCoords(xy2);
			double x1 = xy1[0];
			double x2 = xy2[0];
			x1 = (x1 / getInvXscale()) + getxZero();
			x2 = (x2 / getInvXscale()) + getxZero();

			return (int) Math.abs(x1 - x2) + 2;
		} catch (Exception e) {
			return getWidth();
		}

	}

	/**
	 * @return export height in pixels
	 */
	public int getExportHeight() {
		if (selectionRectangle != null) {
			return (int) selectionRectangle.getHeight();
		}

		try {
			GeoPoint export1 = (GeoPoint) kernel.lookupLabel(EXPORT1);
			GeoPoint export2 = (GeoPoint) kernel.lookupLabel(EXPORT2);
			double[] xy1 = new double[2];
			double[] xy2 = new double[2];
			export1.getInhomCoords(xy1);
			export2.getInhomCoords(xy2);
			double y1 = xy1[1];
			double y2 = xy2[1];
			y1 = getyZero() - (y1 / getInvYscale());
			y2 = getyZero() - (y2 / getInvYscale());

			return (int) Math.abs(y1 - y2) + 2;
		} catch (Exception e) {
			return getHeight();
		}

	}

	private Hits tempArrayList = new Hits();

	// for use in AlgebraController
	final public void clickedGeo(GeoElement geo, boolean isControlDown) {
		if (geo == null) {
			return;
		}

		tempArrayList.clear();
		tempArrayList.add(geo);

		getEuclidianController().startCollectingMinorRepaints();
		boolean changedKernel = euclidianController.processMode(tempArrayList,
				isControlDown, null);
		if (changedKernel) {
			getApplication().storeUndoInfo();
		}
		kernel.notifyRepaint();
		getEuclidianController().stopCollectingMinorRepaints();
	}

	/**
	 * instantiate new zoomer
	 * 
	 * @return zoomer
	 */
	protected abstract MyZoomer newZoomer();

	/**
	 * Zooms around fixed point (px, py)
	 */
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

	private MyZoomer zoomer;

	/**
	 * Zooms towards the given axes scale ratio. Note: Only the y-axis is
	 * changed here. ratio = yscale / xscale;
	 * 
	 * @param newRatio
	 *            new yscale / xscale ratio
	 * @param storeUndo
	 *            true to store undo step after
	 */
	public final void zoomAxesRatio(double newRatio, boolean storeUndo) {
		if (!isZoomable()) {
			return;
		}
		if (isLockedAxesRatio()) {
			return;
		}
		if (axesRatioZoomer == null) {
			axesRatioZoomer = newZoomer();
		}
		axesRatioZoomer.init(newRatio, storeUndo);
		axesRatioZoomer.startAnimation();
	}

	private MyZoomer axesRatioZoomer;

	/**
	 * Restores standard zoom + origin position
	 * 
	 * @param storeUndo
	 *            true to store undo infor
	 */
	public void setStandardView(boolean storeUndo) {
		if (!isZoomable()) {
			return;
		}
		final double xzero, yzero;

		// check if the window is so small that we need custom
		// positions.
		if (getWidth() < (XZERO_STANDARD * 3)) {
			xzero = getWidth() / 3.0;
		} else {
			xzero = XZERO_STANDARD;
		}

		if (getHeight() < (YZERO_STANDARD * 1.6)) {
			yzero = getHeight() / 1.6;
		} else {
			yzero = YZERO_STANDARD;
		}

		if (getScaleRatio() != 1.0) {
			// set axes ratio back to 1
			if (axesRatioZoomer == null) {
				axesRatioZoomer = newZoomer();
			}
			axesRatioZoomer.init(1, false);
			axesRatioZoomer.setStandardViewAfter(xzero, yzero);
			axesRatioZoomer.startAnimation();
		} else {
			setAnimatedCoordSystem(xzero, yzero, 15, false);
		}
		if (storeUndo) {
			getApplication().storeUndoInfo();
		}
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
	public void setAnimatedCoordSystem(double originX, double originY,
			double f, double newScale, int steps, boolean storeUndo) {

		double ox = originX + (getXZero() - originX) * f;
		double oy = originY + (getYZero() - originY) * f;

		if (!Kernel.isEqual(getXscale(), newScale)) {
			// different scales: zoom back to standard view
			double factor = newScale / getXscale();
			zoom((ox - (getxZero() * factor)) / (1.0 - factor),
					(oy - (getyZero() * factor)) / (1.0 - factor), factor,
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

	private MyZoomer mover;

	/**
	 * Sets real world coord system using min and max values for both axes in
	 * real world values.
	 */
	final public void setAnimatedRealWorldCoordSystem(double xmin, double xmax,
			double ymin, double ymax, int steps, boolean storeUndo) {
		if (zoomerRW == null) {
			zoomerRW = newZoomer();
		}
		zoomerRW.initRW(xmin, xmax, ymin, ymax, steps, storeUndo);
		zoomerRW.startAnimation();
	}

	private MyZoomer zoomerRW;

	// for use in AlgebraController
	final public void mouseMovedOver(GeoElement geo) {
		Hits geos = null;
		if (geo != null) {
			tempArrayList.clear();
			tempArrayList.add(geo);
			geos = tempArrayList;
		}
		getEuclidianController().startCollectingMinorRepaints();
		boolean repaintNeeded = getEuclidianController().refreshHighlighting(
				geos, false);
		if (repaintNeeded) {
			kernel.notifyRepaint();
		}
		getEuclidianController().stopCollectingMinorRepaints();
	}

	public void highlight(GeoElement geo) {
		if (getEuclidianController().highlight(geo))
			kernel.notifyRepaint();
	}

	public void highlight(ArrayList<GeoElement> geos) {
		if (getEuclidianController().highlight(geos))
			kernel.notifyRepaint();
	}

	final public void mouseMovedOverList(ArrayList<GeoElement> geoList) {
		Hits geos = null;
		tempArrayList.clear();
		tempArrayList.addAll(geoList);
		geos = tempArrayList;

		getEuclidianController().startCollectingMinorRepaints();
		boolean repaintNeeded = getEuclidianController().refreshHighlighting(
				geos, false);
		if (repaintNeeded) {
			kernel.notifyRepaint();
		}
		getEuclidianController().stopCollectingMinorRepaints();
	}

	/**
	 * Updates highlighting of animation buttons.
	 * 
	 * @return whether status was changed
	 */
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
	protected boolean drawPlayButtonInThisView() {
		GuiManagerInterface gui = getApplication().getGuiManager();
		// just one view
		if (gui == null) {
			return true;
		}
		// eg ev1 just closed
		GetViewId evp = gui.getLayout().getDockManager()
				.getFocusedEuclidianPanel();
		if (evp == null) {
			return isPrimaryEV();
		}

		return this.getViewID() == evp.getViewId();
	}

	private boolean isPrimaryEV() {
		return this.getEuclidianViewNo() ==1 || (!app.showView(App.VIEW_EUCLIDIAN) && this.isDefault2D());
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
	 * @param box
	 *            box to be added
	 */
	public abstract void add(GBox box);

	/**
	 * @param box
	 *            box to be removed
	 */
	public abstract void remove(GBox box);

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
		setAxesColor(GColor.BLACK); // Michael Borcherds
									// 2008-01-26 was
									// darkgray
		setGridColor(GColor.LIGHT_GRAY);
		setBackground(GColor.WHITE);

		// showAxes = true;
		// showGrid = false;
		pointCapturingMode = EuclidianStyleConstants.POINT_CAPTURING_AUTOMATIC;

		// added by Loic BEGIN
		// app.rightAngleStyle = EuclidianView.RIGHT_ANGLE_STYLE_SQUARE;
		// END

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

	public void setShowAxis(boolean show) {
		setShowAxis(0, show, false);
		setShowAxis(1, show, true);
	}

	public void setLogAxis(boolean log) {
		setLogAxis(0, log, false);
		setLogAxis(0, log, true);
	}

	/**
	 * Makes the cursor transparent (for pen)
	 */
	public abstract void setTransparentCursor();

	/**
	 * Swithes the cursor to eraser (for pen)
	 */
	public abstract void setEraserCursor();

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
				|| mode == EuclidianConstants.MODE_FREEHAND_SHAPE;
	}

	private OptionsEuclidian optionPanel = null;

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
		return StringUtil.prototype.estimateHeight("", fontAxes2);
	}

	double estimateNumberWidth(double d, GFont fontAxes2) {
		String s = kernel.formatPiE(d, axesNumberFormat[0],
				StringTemplate.defaultTemplate);
		return StringUtil.prototype.estimateLength(s, fontAxes2);
	}

	static double estimateTextWidth(String s, GFont fontAxes2) {
		return StringUtil.prototype.estimateLength(s, fontAxes2);
	}

	public int getSliderOffsetY() {
		return 50;
	}

	public double getMinSamplePoints() {
		return MIN_SAMPLE_POINTS;
	}

	public double getMaxBendOfScreen() {
		return MAX_BEND_OFF_SCREEN;
	}

	public double getMaxBend() {
		return MAX_BEND;
	}

	public int getMaxDefinedBisections() {
		return MAX_DEFINED_BISECTIONS;
	}

	public double getMinPixelDistance() {
		return MIN_PIXEL_DISTANCE;
	}

	public int getMaxZeroCount() {
		return MAX_ZERO_COUNT;
	}

	public double getMaxPixelDistance() {
		return MAX_PIXEL_DISTANCE;
	}

	public int getMaxProblemBisections() {
		return MAX_PROBLEM_BISECTIONS;
	}

	public int getAbsoluteTop() {
		return -1;
	}

	public int getAbsoluteLeft() {
		return -1;
	}

	final public org.geogebra.common.euclidian.EuclidianStyleBar getStyleBar() {
		if (styleBar == null) {
			styleBar = newEuclidianStyleBar();
		}

		return styleBar;
	}

	/**
	 * 
	 * @return new euclidian style bar
	 */
	abstract protected EuclidianStyleBar newEuclidianStyleBar();

	public long getLastRepaintTime() {
		return 0;
	}

	@Override
	public final void setLabels() {
		if (this.styleBar != null) {
			styleBar.setLabels();
		}
	}

	/**
	 * 
	 * @return 2 for 2D and 3 for 3D
	 */
	public int getDimension() {
		return 2;
	}

	public abstract void exportPaintPre(GGraphics2D g2d, double scale,
			boolean transparency);

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
	public void exportPaint(org.geogebra.common.awt.GGraphics2D g2d,
			double scale, boolean transparency, ExportType exportType) {
		getApplication().setExporting(exportType);
		exportPaintPre(g2d, scale, transparency);
		drawObjects(g2d);
		getApplication().setExporting(ExportType.NONE);
	}

	/**
	 * @param g2d
	 *            target graphics object
	 * @param scale
	 *            ratio of desired size and current size of the graphics
	 */
	public void exportPaintPre(org.geogebra.common.awt.GGraphics2D g2d, double scale) {
		exportPaintPre(g2d, scale, false);
	}

	public void centerView(GeoPointND point) {

		Coords p = getCoordsForView(point.getInhomCoordsInD3());

		double px = (toRealWorldCoordX(getWidth()) - toRealWorldCoordX(0)) / 2;
		double py = (-toRealWorldCoordY(getHeight()) + toRealWorldCoordY(0)) / 2;

		setRealWorldCoordSystem(p.getX() - px, p.getX() + px, p.getY() - py, p.getY() + py);
	}

	public static String getDraggedLabels(ArrayList<String> list) {
		if (list.size() == 0) {
			return null;
		}

		String text = null;

		// single geo
		if (list.size() == 1) {
			text = "FormulaText[" + list.get(0) + ", true, true]";
		}

		// multiple geos, wrap in TableText
		else {
			text = "TableText[";
			for (int i = 0; i < list.size(); i++) {

				text += "{FormulaText[" + list.get(i) + ", true, true]}";
				if (i < list.size() - 1) {
					text += ",";
				}
			}
			text += "]";
		}

		return text;
	}

	public boolean isViewForPlane() {
		if (settings == null) {
			return false;
		}
		return settings.isViewForPlane();
	}

	public void setPixelRatio(float pixelRatio) {
		// TODO Auto-generated method stub
	}
}
