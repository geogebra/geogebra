package geogebra.common.euclidian;

import java.util.ArrayList;
import java.util.HashMap;

import geogebra.common.awt.Color;
import geogebra.common.awt.Font;
import geogebra.common.awt.Graphics2D;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.Matrix.CoordMatrix;
import geogebra.common.kernel.Matrix.Coords;
import geogebra.common.kernel.algos.AlgoElementInterface;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoNumeric;
import geogebra.common.kernel.geos.GeoPoint2;
import geogebra.common.kernel.kernelND.GeoConicND;
import geogebra.common.kernel.kernelND.GeoDirectionND;
import geogebra.common.kernel.kernelND.GeoPlaneND;
import geogebra.common.kernel.kernelND.GeoPointND;
import geogebra.common.main.AbstractApplication;
import geogebra.common.util.NumberFormatAdapter;

public abstract class AbstractEuclidianView implements EuclidianViewInterfaceCommon {
	
	/** View other than EV1 and EV2 **/
	public static int EVNO_GENERAL = 1001;
	protected int evNo = 1;
	double xZeroOld, yZeroOld;
	protected int mode = EuclidianConstants.MODE_MOVE;
	
	protected static final int MIN_WIDTH = 50;
	protected static final int MIN_HEIGHT = 50;

	protected static final String EXPORT1 = "Export_1"; // Points used to define
														// corners for export
														// (if they exist)
	protected static final String EXPORT2 = "Export_2";

	// pixel per centimeter (at 72dpi)
	protected static final double PRINTER_PIXEL_PER_CM = 72.0 / 2.54;

	public static final double MODE_ZOOM_FACTOR = 1.5;

	public static final double MOUSE_WHEEL_ZOOM_FACTOR = 1.1;

	public static final double SCALE_STANDARD = 50;

	// public static final double SCALE_MAX = 10000;
	// public static final double SCALE_MIN = 0.1;
	public static final double XZERO_STANDARD = 215;

	public static final double YZERO_STANDARD = 315;

	protected Kernel kernel;
	
	public static final Integer[] getLineTypes() {
		Integer[] ret = { new Integer(EuclidianStyleConstants.LINE_TYPE_FULL),
				new Integer(EuclidianStyleConstants.LINE_TYPE_DASHED_LONG),
				new Integer(EuclidianStyleConstants.LINE_TYPE_DASHED_SHORT),
				new Integer(EuclidianStyleConstants.LINE_TYPE_DOTTED),
				new Integer(EuclidianStyleConstants.LINE_TYPE_DASHED_DOTTED) };
		return ret;
	}

	// G.Sturr added 2009-9-21
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
	private geogebra.common.awt.AffineTransform coordTransform = 
			geogebra.common.factories.AwtFactory.prototype.newAffineTransform();
	protected double[] AxesTickInterval = { 1, 1 }; // for axes =
	protected NumberFormatAdapter[] axesNumberFormat;
	protected boolean[] showAxes = { true, true };
	
	// distances between grid lines
		protected boolean automaticGridDistance = true;
		
		

		protected double[] gridDistances = { 2, 2, Math.PI / 6 };

		protected int gridLineStyle, axesLineType;

		protected boolean gridIsBold = false; // Michael Borcherds 2008-04-11

		
	protected int tooltipsInThisView = EuclidianStyleConstants.TOOLTIPS_AUTOMATIC;

	// Michael Borcherds 2008-04-28
	public static final int GRID_CARTESIAN = 0;
	public static final int GRID_ISOMETRIC = 1;
	public static final int GRID_POLAR = 2;
	protected int gridType = GRID_CARTESIAN;
	
	// FONTS
		private Font fontPoint;

		private Font fontLine;

		private Font fontVector;

		private Font fontConic;

		private Font fontCoords;

		private Font fontAxes;

		private Font fontAngle;

	
	// object is hit if mouse is within this many pixels
		// (more for points, see DrawPoint)
		private int capturingThreshold = 3;

		public void setCapturingThreshold(int i) {
			capturingThreshold = i;
		}

		public int getCapturingThreshold() {
			return capturingThreshold;
		}
		final public int getMode() {
			return mode;
		}
		
		public void setMode(int mode) {
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
		
		public Kernel getKernel() {
			return kernel;
		}
		
		/**
		 * whether to clear selection rectangle when mode selected
		 */
		final private static boolean clearRectangle(int mode) {
			switch (mode) {
			case EuclidianConstants.MODE_PEN:
				return false;
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
	
	private boolean updatingBounds = false;

	protected boolean unitAxesRatio;

	/**
	 * returns true if the axes ratio is 1
	 * 
	 * @return true if the axes ratio is 1
	 */
	public boolean isUnitAxesRatio() {
		return unitAxesRatio || (gridType == GRID_POLAR);
	}

	/**
	 * Set unit axes ratio to 1
	 * 
	 * @param flag
	 *            true to set to 1, false to allow user
	 */
	public void setUnitAxesRatio(boolean flag) {
		unitAxesRatio = flag;
		if (flag) {
			updateBounds();
		}
	}

	public void updateBounds() {
		if (updatingBounds) {
			return;
		}
		updatingBounds = true;
		double xmin2 = xminObject.getDouble();
		double xmax2 = xmaxObject.getDouble();
		double ymin2 = yminObject.getDouble();
		double ymax2 = ymaxObject.getDouble();
		if (isUnitAxesRatio() && (getHeight() > 0) && (getWidth() > 0)) {
			double newWidth = ((ymax2 - ymin2) * getWidth()) / (getHeight() + 0.0);
			double newHeight = ((xmax2 - xmin2) * getHeight()) / (getWidth() + 0.0);

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
		if (((xmax2 - xmin2) > Kernel.MIN_PRECISION)
				&& ((ymax2 - ymin2) > Kernel.MIN_PRECISION)) {
			setRealWorldCoordSystem(xmin2, xmax2, ymin2, ymax2);
		}
		updatingBounds = false;
	}

	
	public boolean isZoomable() {
		if ((xminObject != null)
				&& (!((GeoNumeric) xminObject).isIndependent() || ((GeoNumeric) xminObject)
						.isLabelSet())) {
			return false;
		}
		if ((xmaxObject != null)
				&& (!((GeoNumeric) xmaxObject).isIndependent() || ((GeoNumeric) xmaxObject)
						.isLabelSet())) {
			return false;
		}
		if ((yminObject != null)
				&& (!((GeoNumeric) yminObject).isIndependent() || ((GeoNumeric) yminObject)
						.isLabelSet())) {
			return false;
		}
		if ((ymaxObject != null)
				&& (!((GeoNumeric) ymaxObject).isIndependent() || ((GeoNumeric) ymaxObject)
						.isLabelSet())) {
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
	 * @return screen equivalent of real world x-coord
	 */
	final public int toScreenCoordX(double xRW) {
		return (int) Math.round(getxZero() + (xRW * getXscale()));
	}

	/**
	 * convert real world coordinate y to screen coordinate y
	 * 
	 * @param yRW
	 * @return screen equivalent of real world y-coord
	 */
	final public int toScreenCoordY(double yRW) {
		return (int) Math.round(getyZero() - (yRW * getYscale()));
	}

	/**
	 * convert real world coordinate x to screen coordinate x
	 * 
	 * @param xRW
	 * @return screen equivalent of real world x-coord as double
	 */
	final public double toScreenCoordXd(double xRW) {
		return getxZero() + (xRW * getXscale());
	}

	/**
	 * convert real world coordinate y to screen coordinate y
	 * 
	 * @param yRW
	 * @return screen equivalent of real world y-coord
	 */
	final public double toScreenCoordYd(double yRW) {
		return getyZero() - (yRW * getYscale());
	}

	/**
	 * convert real world coordinate x to screen coordinate x. If the value is
	 * outside the screen it is clipped to one pixel outside.
	 * 
	 * @param xRW
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
		inOut[0] = getxZero() + (inOut[0] * getXscale());
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
	 * @return true if coords are on screen
	 */
	final public boolean isOnScreen(double[] coords) {
		return (coords[0] >= 0) && (coords[0] <= getWidth()) && (coords[1] >= 0)
				&& (coords[1] <= getHeight());
	}

	// private static final double MAX_SCREEN_COORD = Float.MAX_VALUE; //10000;

	// /**
	// * Converts real world coordinates to screen coordinates. If a coord value
	// * is outside the screen it is clipped to a rectangle with border
	// * PIXEL_OFFSET around the screen.
	// *
	// * @param inOut:
	// * input and output array with x and y coords
	// * @return true iff resulting coords are on screen, note: Double.NaN is
	// NOT
	// * checked
	// */
	// final public boolean toClippedScreenCoords(double[] inOut, int
	// PIXEL_OFFSET) {
	// inOut[0] = xZero + inOut[0] * xscale;
	// inOut[1] = yZero - inOut[1] * yscale;
	//
	// boolean onScreen = true;
	//
	// // x-coord on screen?
	// if (inOut[0] < 0) {
	// inOut[0] = Math.max(inOut[0], -PIXEL_OFFSET);
	// onScreen = false;
	// } else if (inOut[0] > width) {
	// inOut[0] = Math.min(inOut[0], width + PIXEL_OFFSET);
	// onScreen = false;
	// }
	//
	// // y-coord on screen?
	// if (inOut[1] < 0) {
	// inOut[1] = Math.max(inOut[1], -PIXEL_OFFSET);
	// onScreen = false;
	// } else if (inOut[1] > height) {
	// inOut[1] = Math.min(inOut[1], height + PIXEL_OFFSET);
	// onScreen = false;
	// }
	//
	// return onScreen;
	// }

	/**
	 * convert screen coordinate x to real world coordinate x
	 * 
	 * @param x
	 * @return real world equivalent of screen x-coord
	 */
	final public double toRealWorldCoordX(double x) {
		return (x - getxZero()) * getInvXscale();
	}

	/**
	 * convert screen coordinate y to real world coordinate y
	 * 
	 * @param y
	 * @return real world equivalent of screen y-coord
	 */
	final public double toRealWorldCoordY(double y) {
		return (getyZero() - y) * getInvYscale();
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
		double calcXscale = getWidth() / (xmax - xmin);
		double calcYscale = getHeight() / (ymax - ymin);
		double calcXzero = -calcXscale * xmin;
		double calcYzero = calcYscale * ymax;

		setCoordSystem(calcXzero, calcYzero, calcXscale, calcYscale);
	}

	
	public void setCoordSystem(double xZero, double yZero, double xscale,
			double yscale, boolean repaint) {
		if (Double.isNaN(xscale)
				|| (xscale < Kernel.MAX_DOUBLE_PRECISION)
				|| (xscale > Kernel.INV_MAX_DOUBLE_PRECISION)) {
			return;
		}
		if (Double.isNaN(yscale)
				|| (yscale < Kernel.MAX_DOUBLE_PRECISION)
				|| (yscale > Kernel.INV_MAX_DOUBLE_PRECISION)) {
			return;
		}

		this.setxZero(xZero);
		this.setyZero(yZero);
		this.setXscale(xscale);
		this.setYscale(yscale);
		setScaleRatio(yscale / xscale);
		setInvXscale(1.0d / xscale);
		setInvYscale(1.0d / yscale);

		// set transform for my coord system:
		// ( xscale 0 xZero )
		// ( 0 -yscale yZero )
		// ( 0 0 1 )
		getCoordTransform().setTransform(xscale, 0.0d, 0.0d, -yscale, xZero, yZero);

		// real world values
		setRealWorldBounds();

		// if (drawMode == DRAW_MODE_BACKGROUND_IMAGE)
		if (repaint) {
			updateBackgroundImage();
			updateAllDrawables(repaint);

			// needed so that eg Corner[2,1] updates properly on zoom / pan
			if (getApplication().hasEuclidianView2()) {
				kernel.notifyRepaint();
				// app.updateStatusLabelAxesRatio();
			}
		}
	}


	/**
	 * @return the ymaxObject
	 */
	public GeoNumeric getYmaxObject() {
		return (GeoNumeric) ymaxObject;
	}

	private double xmin; // ratio yscale / xscale

	private double xmax;

	private double ymin;

	private double ymax;

	private double invXscale;

	private double invYscale;

	private double xZero;

	private double yZero;

	private double xscale;

	private double yscale;

	private double scaleRatio = 1.0;
	
	protected double printingScale;

	// Map (geo, drawable) for GeoElements and Drawables
	protected HashMap<GeoElement, Drawable> DrawableMap = new HashMap<GeoElement, Drawable>(
			500);

	protected ArrayList<GeoPointND> stickyPointList = new ArrayList<GeoPointND>();

	protected DrawableList allDrawableList = new DrawableList();

	public DrawableList drawLayers[];

	// on add: change resetLists()

	protected DrawableList bgImageList = new DrawableList();
	
	protected boolean[] piAxisUnit = { false, false };

	protected int[] axesTickStyles = {
			EuclidianStyleConstants.AXES_TICK_STYLE_MAJOR,
			EuclidianStyleConstants.AXES_TICK_STYLE_MAJOR };

	// for axes labeling with numbers
	protected boolean[] automaticAxesNumberingDistances = { true, true };

	protected double[] axesNumberingDistances = { 2, 2 };


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

	void setXscale(double xscale) {
		this.xscale = xscale;
	}

	void setYscale(double yscale) {
		this.yscale = yscale;
	}

	void setInvXscale(double invXscale) {
		this.invXscale = invXscale;
	}

	void setInvYscale(double invYscale) {
		this.invYscale = invYscale;
	}

	protected void setFontSize(int fontSize) {
		this.fontSize = fontSize;
	}

	public double getxZero() {
		return xZero;
	}

	void setxZero(double xZero) {
		this.xZero = xZero;
	}

	public double getyZero() {
		return yZero;
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
		if (type == GRID_POLAR) {
			updateBounds();
		}
	}


	void setyZero(double yZero) {
		this.yZero = yZero;
	}

	void setScaleRatio(double scaleRatio) {
		this.scaleRatio = scaleRatio;
	}

	void setXmin(double xmin) {
		this.xmin = xmin;
	}

	void setXmax(double xmax) {
		this.xmax = xmax;
	}

	void setYmin(double ymin) {
		this.ymin = ymin;
	}

	void setYmax(double ymax) {
		this.ymax = ymax;
	}

	final protected void setRealWorldBounds() {
		setXmin(-getxZero() * getInvXscale());
		setXmax((getWidth() - getxZero()) * getInvXscale());
		setYmax(getyZero() * getInvYscale());
		setYmin((getyZero() - getHeight()) * getInvYscale());
		updateBoundObjects();
		updateBounds();
		setAxesIntervals(getXscale(), 0);
		setAxesIntervals(getYscale(), 1);
		calcPrintingScale();

		// tell kernel
		if (evNo != EVNO_GENERAL) {
			kernel.setEuclidianViewBounds(evNo, getXmin(), getXmax(), getYmin(), getYmax(), getXscale(),
					getYscale());
		}

	}

	/**
	 * Zooms around fixed point (center of screen)
	 * 
	 * @param zoomFactor
	 */
	public final void zoomAroundCenter(double zoomFactor) {
		if (!isZoomable()) {
			return;
			// keep xmin, xmax, ymin, ymax constant, adjust everything else
		}

		setXscale(getXscale() * zoomFactor);
		setYscale(getYscale() * zoomFactor);

		setScaleRatio(getYscale() / getXscale());
		setInvXscale(1.0d / getXscale());
		setInvYscale(1.0d / getYscale());

		setxZero(-getXmin() * getXscale());
		setWidth((int) ((getXmax() * getXscale()) + getxZero()));
		setyZero(getYmax() * getYscale());
		setHeight((int) (getyZero() - (getYmin() * getYscale())));

		setAxesIntervals(getXscale(), 0);
		setAxesIntervals(getYscale(), 1);
		calcPrintingScale();

		// tell kernel
		if (evNo != EVNO_GENERAL) {
			kernel.setEuclidianViewBounds(evNo, getXmin(), getXmax(), getYmin(), getYmax(), getXscale(),
					getYscale());
		}

		getCoordTransform().setTransform(getXscale(), 0.0d, 0.0d, -getYscale(), getxZero(), getyZero());

		updateBackgroundImage();
		updateAllDrawables(true);

	}
	
	final public void updateAllDrawables(boolean repaint) {
		allDrawableList.updateAll();
		if (repaint) {
			repaint();
		}
	}


	protected void calcPrintingScale() {
		double unitPerCM = PRINTER_PIXEL_PER_CM / getXscale();
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
			// force same unit if scales are same, see #1082
			if ((axis == 1) && automaticAxesNumberingDistances[0]
					&& Kernel.isEqual(getXscale(), getYscale())) {
				axesNumberingDistances[1] = axesNumberingDistances[0];
			} else if (piAxisUnit[axis]) {
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
		
			NumberFormatAdapter df = axesNumberFormat[axis];

			// display large and small numbers in scienctific notation
			if ((axesNumberingDistances[axis] < 10E-6)
					|| (axesNumberingDistances[axis] > 10E6)) {
				df.applyPattern("0.##E0");
				// avoid 4.00000000000004E-11 due to rounding error when
				// computing
				// tick mark numbers
				maxFractionDigtis = Math.min(14, maxFractionDigtis);
			} else {
				df.applyPattern("###0.##");
			}
		
		axesNumberFormat[axis].setMaximumFractionDigits(maxFractionDigtis);

		if (automaticGridDistance) {
			gridDistances[axis] = axesNumberingDistances[axis]
					* EuclidianStyleConstants.automaticGridDistanceFactor;
		}
	}

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

	final public void update(GeoElement geo) {
		Object d = DrawableMap.get(geo);
		if (d != null) {
			((Drawable) d).update();
		}
	}
	
	/**
	 * adds a GeoElement to this view
	 */
	public void add(GeoElement geo) {

		// Application.printStacktrace(""+geo.isVisibleInView(this));

		// G.Sturr 2010-6-30
		// filter out any geo not marked for this view
		if (!isVisibleInThisView(geo)) {
			return;
			// END G.Sturr
		}

		// check if there is already a drawable for geo
		Drawable d = getDrawable(geo);
		if (d != null) {
			return;
		}

		d = createDrawable(geo);
		if (d != null) {
			addToDrawableLists(d);
			repaint();
		}

	}
	
	/**
	 * removes a GeoElement from this view
	 */
	final public void remove(GeoElement geo) {
		Drawable d = DrawableMap.get(geo);
		int layer = geo.getLayer();
		if(d==null)
			return;
		if (d instanceof RemoveNeeded) {
			drawLayers[layer].remove(d);
			((RemoveNeeded)d).remove();
		}
		else{
			drawLayers[layer].remove(d);
		}
		allDrawableList.remove(d);

		DrawableMap.remove(geo);
		if (geo.isGeoPoint()) {
			stickyPointList.remove(geo);
		}
		repaint();
		
	}

	
	/**
	 * Returns the drawable for the given GeoElement.
	 * 
	 * @param geo
	 * @return drawable for the given GeoElement.
	 */
	final Drawable getDrawable(GeoElement geo) {
		return DrawableMap.get(geo);
	}

	final public DrawableND getDrawableND(GeoElement geo) {
		return getDrawable(geo);
	}

	/**
	 * adds a GeoElement to this view
	 * 
	 * @param d
	 *            drawable to be added
	 */
	protected void addToDrawableLists(Drawable d) {
		if (d == null) {
			return;
		}

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


	public boolean isVisibleInThisView(GeoElement geo) {
		return geo.isVisibleInView(this.getViewID());
	}

	public DrawableND createDrawableND(GeoElement geo) {
		return createDrawable(geo);
	}
	/**
	 * adds a GeoElement to this view
	 * 
	 * @param geo
	 *            GeoElement to be added
	 * @return drawable for given GeoElement
	 */
	protected Drawable createDrawable(GeoElement geo) {
		Drawable d = newDrawable(geo);

		if (d != null) {
			DrawableMap.put(geo, d);
			if (geo.isGeoPoint()) {
				stickyPointList.add((GeoPointND) geo);
			}
		}

		return d;
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
	
	final public void repaintView() {
		repaint();
	}

	final public void repaintEuclidianView() {
		repaint();
	}


	final public void updateVisualStyle(GeoElement geo) {
		update(geo);
	}

	final public Drawable getDrawableFor(GeoElement geo) {
		return DrawableMap.get(geo);
	}

	final public void updateAuxiliaryObject(GeoElement geo) {
		// repaint();
	}

	
	final protected void updateDrawableFontSize() {
		allDrawableList.updateFontSizeAll();
		repaint();
	}

	public geogebra.common.awt.Font getFontPoint() {
		return fontPoint;
	}

	public void setFontPoint(Font fontPoint) {
		this.fontPoint = fontPoint;
	}

	public geogebra.common.awt.Font getFontLine() {
		return fontLine;
	}

	public void setFontLine(Font fontLine) {
		this.fontLine = fontLine;
	}

	public Font getFontVector() {
		return fontVector;
	}

	public void setFontVector(Font fontVector) {
		this.fontVector = fontVector;
	}

	public Font getFontConic() {
		return fontConic;
	}

	public void setFontConic(Font fontConic) {
		this.fontConic = fontConic;
	}

	public Font getFontCoords() {
		return fontCoords;
	}

	public void setFontCoords(Font fontCoords) {
		this.fontCoords = fontCoords;
	}

	public Font getFontAxes() {
		return fontAxes;
	}

	public void setFontAxes(Font fontAxes) {
		this.fontAxes = fontAxes;
	}

	public geogebra.common.awt.Font getFontAngle() {
		return fontAngle;
	}

	public void setFontAngle(Font fontAngle) {
		this.fontAngle = fontAngle;
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
		getApplication().booleanSize = (size == 13) ? 13 : 26;

		updateAllDrawables(true);
	}

	final public int getBooleanSize() {
		return getApplication().booleanSize;
	}

	/**
	 * Sets the global style for point drawing.
	 * 
	 * @param style
	 */
	public void setPointStyle(int style) {
		if ((style > 0) && (style <= EuclidianStyleConstants.MAX_POINT_STYLE)) {
			getApplication().pointStyle = style;
		} else {
			getApplication().pointStyle = EuclidianStyleConstants.POINT_STYLE_DOT;
		}

		updateAllDrawables(true);
	}

	final public int getPointStyle() {
		return getApplication().pointStyle;
	}

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
	 * tranform in view coords
	 * 
	 * @param coords
	 * @return the same coords for classic 2d view
	 */
	public Coords getCoordsForView(Coords coords) {
		return coords;
	}

	/**
	 * return null if classic 2D view
	 * 
	 * @return matrix representation of the plane shown by this view
	 */
	public CoordMatrix getMatrix() {
		return null;
	}

	/**
	 * 
	 * @param conic
	 * @param M
	 * @param ev
	 * @return affine transform of the conic for this view
	 */
	public geogebra.common.awt.AffineTransform getTransform(GeoConicND conic, Coords M, Coords[] ev) {
		return conic.getAffineTransform();
	}

	public String getFromPlaneString() {
		return "xOyPlane";
	}

	public String getTranslatedFromPlaneString() {
		return getApplication().getPlain("xOyPlane");
	}

	public boolean isDefault2D() {
		return true;
	}

	public int getViewID() {
		switch (evNo) {
		case 1:
			return AbstractApplication.VIEW_EUCLIDIAN;
		case 2:
			return AbstractApplication.VIEW_EUCLIDIAN2;
		default:
			return AbstractApplication.VIEW_NONE;
		}
	}

	// Michael Borcherds 2008-02-29
	public void changeLayer(GeoElement geo, int oldlayer, int newlayer) {
		updateMaxLayerUsed(newlayer);
		// Application.debug(drawLayers[oldlayer].size());
		drawLayers[oldlayer].remove(DrawableMap.get(geo));
		// Application.debug(drawLayers[oldlayer].size());
		drawLayers[newlayer].add(DrawableMap.get(geo));
	}

	public void updateMaxLayerUsed(int layer) {
		if (layer > EuclidianStyleConstants.MAX_LAYERS) {
			layer = EuclidianStyleConstants.MAX_LAYERS;
		}
		if (layer > getApplication().maxLayerUsed) {
			getApplication().maxLayerUsed = layer;
		}
	}

	public int getMaxLayerUsed() {
		return getApplication().maxLayerUsed;
	}

	/**
	 * 
	 * @return null (for 2D) and xOyPlane (for 3D)
	 */
	public GeoPlaneND getPlaneContaining() {
		return kernel.getDefaultPlane();
	}

	/**
	 * 
	 * @return null (for 2D) and xOyPlane (for 3D)
	 */
	public GeoDirectionND getDirection() {
		return getPlaneContaining();
	}

	public void updateForPlane() {
		// only used in EuclidianViewForPlane
	}

	public boolean hasForParent(GeoElement geo) {
		return false;
	}

	public boolean isMoveable(GeoElement geo) {
		return geo.isMoveable();
	}

	public ArrayList<GeoPoint2> getFreeInputPoints(
			AlgoElementInterface algoParent) {
		return algoParent.getFreeInputPoints();
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
		updateBounds();
	}

	/**
	 * Sets the global style for rightAngle drawing.
	 * 
	 * @param style
	 */
	public void setRightAngleStyle(int style) {
		getApplication().rightAngleStyle = style;
		updateAllDrawables(true);
	}

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
			AbstractApplication.debug("NULL");
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

	public geogebra.common.awt.AffineTransform getCoordTransform() {
		return coordTransform;
	}

	void setCoordTransform(geogebra.common.awt.AffineTransform coordTransform) {
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

	public abstract  void updateBackgroundImage();
	public abstract  Graphics2D getBackgroundGraphics();
	public abstract  Graphics2D getTempGraphics2D(geogebra.common.awt.Font plainFontCommon);
	public abstract  geogebra.common.awt.GeneralPath getBoundingPath();
	public abstract  geogebra.common.awt.Font getFont();
	protected abstract void setHeight(int h);
	protected abstract void setWidth(int h);
	protected abstract void initCursor();
	protected abstract void setStyleBarMode(int mode);
	protected abstract Drawable newDrawable(GeoElement ge);

	public abstract void zoomAxesRatio(double d, boolean b);
}
