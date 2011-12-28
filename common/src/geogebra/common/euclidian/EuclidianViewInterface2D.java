package geogebra.common.euclidian;

import java.awt.geom.AffineTransform;

import geogebra.common.awt.Color;
import geogebra.common.awt.Font;
import geogebra.common.awt.Graphics2D;
import geogebra.common.kernel.AbstractKernel;
import geogebra.common.kernel.Matrix.CoordMatrix;
import geogebra.common.kernel.Matrix.Coords;
import geogebra.common.kernel.kernelND.GeoConicND;
import geogebra.common.main.AbstractApplication;

public abstract class EuclidianViewInterface2D implements EuclidianViewInterfaceSlim{
	
	/** View other than EV1 and EV2 **/
	public static int EVNO_GENERAL = 1001;
	
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

	protected int tooltipsInThisView = EuclidianStyleConstants.TOOLTIPS_AUTOMATIC;

	// Michael Borcherds 2008-04-28
	public static final int GRID_CARTESIAN = 0;
	public static final int GRID_ISOMETRIC = 1;
	public static final int GRID_POLAR = 2;
	protected int gridType = GRID_CARTESIAN;

	
	public abstract  void updateForPlane();

	public abstract  int getMaxLayerUsed();

	public abstract  AbstractKernel getKernel();
	public abstract  AbstractApplication getApplication();

	public abstract  void updateBackgroundImage();

	public abstract  double getInvXscale();
	public abstract  double getInvYscale();

	public abstract  void updateBackground();

	public abstract  int getFontSize();

	public abstract  double toScreenCoordXd(double aRW);
	public abstract  double toScreenCoordYd(double aRW);
	public abstract  int toScreenCoordY(double aRW);
	public abstract  int toScreenCoordX(double aRW);

	public abstract  boolean toScreenCoords(double[] coords);

	public abstract  double getyZero();
	public abstract  double getxZero();
	public abstract  double getScaleRatio();
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

	public abstract  int getCapturingThreshold();

	public abstract  geogebra.common.awt.GeneralPath getBoundingPath();

	public abstract  int getPointStyle();

	public abstract  geogebra.common.awt.AffineTransform getTransform(GeoConicND conic, Coords m, Coords[] ev);

	public abstract  boolean isOnScreen(double[] a);

	public abstract  int toClippedScreenCoordX(double x);
	public abstract  int toClippedScreenCoordY(double y);

	public abstract  geogebra.common.awt.Font getFont();

	public abstract   geogebra.common.awt.Font  getFontVector();

	public abstract  Color getBackgroundCommon();
}
