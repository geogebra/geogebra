package geogebra.common.euclidian;

import java.util.ArrayList;
import java.util.HashMap;

import geogebra.common.awt.Color;
import geogebra.common.awt.Graphics2D;
import geogebra.common.kernel.AbstractKernel;
import geogebra.common.kernel.Matrix.CoordMatrix;
import geogebra.common.kernel.Matrix.Coords;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoNumeric;
import geogebra.common.kernel.kernelND.GeoConicND;
import geogebra.common.kernel.kernelND.GeoPointND;
import geogebra.common.main.AbstractApplication;
import geogebra.common.util.NumberFormatAdapter;

public abstract class EuclidianViewInterface2D implements EuclidianViewInterfaceSlim{
	
	/** View other than EV1 and EV2 **/
	public static int EVNO_GENERAL = 1001;
	protected int evNo = 1;
	
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

	protected AbstractKernel kernel;
	
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
	
	// object is hit if mouse is within this many pixels
		// (more for points, see DrawPoint)
		private int capturingThreshold = 3;

		public void setCapturingThreshold(int i) {
			capturingThreshold = i;
		}

		public int getCapturingThreshold() {
			return capturingThreshold;
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
	
	public void setCoordSystem(double xZero, double yZero, double xscale,
			double yscale, boolean repaint) {
		if (Double.isNaN(xscale)
				|| (xscale < AbstractKernel.MAX_DOUBLE_PRECISION)
				|| (xscale > AbstractKernel.INV_MAX_DOUBLE_PRECISION)) {
			return;
		}
		if (Double.isNaN(yscale)
				|| (yscale < AbstractKernel.MAX_DOUBLE_PRECISION)
				|| (yscale > AbstractKernel.INV_MAX_DOUBLE_PRECISION)) {
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
					&& AbstractKernel.isEqual(getXscale(), getYscale())) {
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

	
	public abstract  void updateForPlane();

	public abstract  int getMaxLayerUsed();

	public abstract  AbstractKernel getKernel();
	public abstract  AbstractApplication getApplication();

	public abstract  void updateBackgroundImage();

	public abstract  void updateBackground();


	public abstract  double toScreenCoordXd(double aRW);
	public abstract  double toScreenCoordYd(double aRW);
	public abstract  int toScreenCoordY(double aRW);
	public abstract  int toScreenCoordX(double aRW);

	public abstract  boolean toScreenCoords(double[] coords);

	
	public abstract  int getMode();
	public abstract  Coords getCoordsForView(Coords coords);

	public abstract  int getRightAngleStyle();

	public abstract  CoordMatrix getMatrix();

	public abstract  Graphics2D getBackgroundGraphics();

	public abstract  Graphics2D getTempGraphics2D(geogebra.common.awt.Font plainFontCommon);

	public abstract  int getBooleanSize();

	public abstract  geogebra.common.awt.AffineTransform getCoordTransform();

	public abstract  geogebra.common.awt.Font getFontAngle();
	public abstract  geogebra.common.awt.Font getFontLine();
	public abstract  geogebra.common.awt.Font getFontPoint();
	public abstract  geogebra.common.awt.Font getFontConic();


	public abstract  geogebra.common.awt.GeneralPath getBoundingPath();

	public abstract  int getPointStyle();

	public abstract  geogebra.common.awt.AffineTransform getTransform(GeoConicND conic, Coords m, Coords[] ev);

	public abstract  boolean isOnScreen(double[] a);

	public abstract  int toClippedScreenCoordX(double x);
	public abstract  int toClippedScreenCoordY(double y);

	public abstract  geogebra.common.awt.Font getFont();

	public abstract   geogebra.common.awt.Font  getFontVector();

	public abstract  Color getBackgroundCommon();
	
	protected abstract void setHeight(int h);
	protected abstract void setWidth(int h);
	protected abstract void repaint();
	
}
