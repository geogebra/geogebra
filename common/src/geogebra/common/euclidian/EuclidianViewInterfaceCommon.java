package geogebra.common.euclidian;

import java.util.ArrayList;

import geogebra.common.awt.Point;
import geogebra.common.awt.Rectangle;
import geogebra.common.euclidian.event.AbstractEvent;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoNumeric;
import geogebra.common.kernel.kernelND.GeoDirectionND;
import geogebra.common.kernel.kernelND.GeoLineND;
import geogebra.common.kernel.kernelND.GeoPointND;
import geogebra.common.main.AbstractApplication;

public interface EuclidianViewInterfaceCommon extends EuclidianViewInterfaceSlim {

	/** reference to x axis*/
	public static final int AXIS_X = 0; 
	/** reference to y axis*/	
	public static final int AXIS_Y = 1;

	/**
	 * Zooms around fixed point (px, py)
	 */
	public void zoom(double px, double py, double zoomFactor, int steps, boolean storeUndo);

	public void changeLayer(GeoElement geo, int oldlayer, int newlayer);

	// mode
	/**
	 * clears all selections and highlighting
	 */
	void resetMode();

	public void repaint();

	/** remembers the origins values (xzero, ...) */
	public void rememberOrigins();

	/////////////////////////////////////////
	// previewables

	/**
	 * create a previewable for line construction
	 * @param selectedPoints points
	 * @return the line previewable
	 */
	public Previewable createPreviewLine(ArrayList<GeoPointND> selectedPoints);
	
	/**
	 * create a previewable for segment construction
	 * @param selectedPoints points
	 * @return the segment previewable
	 */	
	public Previewable createPreviewSegment(ArrayList<GeoPointND> selectedPoints);
	
	
	/**
	 * create a previewable for ray construction
	 * @param selectedPoints points
	 * @return the ray previewable
	 */	
	public Previewable createPreviewRay(ArrayList<GeoPointND> selectedPoints);

	/**
	 * create a previewable for vector construction
	 * @param selectedPoints points
	 * @return the ray previewable
	 */	
	public Previewable createPreviewVector(ArrayList<GeoPointND> selectedPoints);

	/**
	 * create a previewable for conic construction
	 * @param mode 
	 * @param selectedPoints points
	 * @return the conic previewable
	 */		
	public Previewable createPreviewConic(int mode, ArrayList<GeoPointND> selectedPoints);

	public Previewable createPreviewParallelLine(ArrayList<GeoPointND> selectedPoints,
			ArrayList<GeoLineND> selectedLines);
	public Previewable createPreviewPerpendicularLine(ArrayList<GeoPointND> selectedPoints,
			ArrayList<GeoLineND> selectedLines);
	public Previewable createPreviewPerpendicularBisector(ArrayList<GeoPointND> selectedPoints);
	public Previewable createPreviewAngleBisector(ArrayList<GeoPointND> selectedPoints);

	public void mouseEntered();
	public void mouseExited();

	/**
	 * 
	 * @param geo
	 * @return true if the geo is parent of the view
	 */
	public boolean hasForParent(GeoElement geo);

	public AbstractApplication getApplication();

	public Drawable getDrawableFor(GeoElement geo);

	public DrawableND getDrawableND(GeoElement geo);

	public DrawableND createDrawableND(GeoElement geo);

	/**
	 * 
	 * @return string description of plane from the view was created
	 */
	public String getFromPlaneString();

	public ArrayList<GeoPointND> getStickyPointList();

	/**
	 * 
	 * @return string translated description of plane from the view was created
	 */
	public String getTranslatedFromPlaneString();

	public boolean isAutomaticGridDistance();

	public boolean[] isAutomaticAxesNumberingDistance();

	/**
	 * 
	 * @return true if this is Graphics or Graphics 2
	 */
	public boolean isDefault2D();

	public boolean isGridOrAxesShown();

	/**
	 * returns true if the axes ratio is 1
	 * @return true if the axes ratio is 1
	 */
	public boolean isUnitAxesRatio();

	public boolean isZoomable();


	public int getAllowToolTips();
	public boolean getAllowShowMouseCoords();
	public double[] getAxesCross();
	public double[] getAxesNumberingDistances();
	public String[] getAxesLabels();
	public int getAxesLineStyle();
	public int[] getAxesTickStyles();
	public String[] getAxesUnitLabels();
	public geogebra.common.awt.Color getBackgroundCommon();
	public boolean[] getDrawBorderAxes();
	public double[] getGridDistances();
	public double getGridDistances(int i);
	public boolean getGridIsBold();
	public int getGridLineStyle();
	public int getGridType();
	public double getInvXscale();
	public double getInvYscale();
	public int getMode();

	/**
	 * Returns point capturing mode.
	 */
	public int getPointCapturingMode();

	public int getPointStyle();
	public boolean[] getPositiveAxes(); 
	public Previewable getPreviewDrawable();
	public boolean[] getShowAxesNumbers();
	public boolean getShowGrid();
	public boolean getShowMouseCoords();
	public boolean getShowAxis(int axis);
	public boolean getShowXaxis();
	public boolean getShowYaxis();
	public int getViewWidth();
	public int getViewHeight();
	public double getXmin();
	public double getXmax();
	public double getYmin();
	public double getYmax();
	public GeoNumeric getXminObject();
	public GeoNumeric getXmaxObject();
	public GeoNumeric getYminObject();
	public GeoNumeric getYmaxObject();
	public double getXscale();
	public double getYscale();
	public double getXZero();
	public double getYZero();


	public void setAutomaticAxesNumberingDistance(boolean b, int axis);
	public void setAutomaticGridDistance(boolean b);
	public void setAllowShowMouseCoords(boolean selected);
	public void setAxesLabels(String[] labels);
	public void setAxesLineStyle(int selectedIndex);
	public void setAxesTickStyles(int[] styles);
	public void setAxesUnitLabels(String[] unitLabels);
	public void setAxesNumberingDistance(double tickDist, int axis);

	public void setAxesCross(double[] axisCross);

	/** sets the axis crossing value
	 * @param axis
	 * @param cross
	 */
	public void setAxisCross(int axis, double cross);

	/**
	 * sets the axis label to axisLabel
	 * @param axis
	 * @param axisLabel
	 */
	public void setAxisLabel(int axis, String axisLabel);

	/** sets the tickstyle of this axis
	 * @param axis
	 * @param tickStyle
	 */
	public void setAxisTickStyle(int axis, int tickStyle);

	public void setCoordSystem(double x, double y, double xscale, double yscale);
	public void setCoordSystemFromMouseMove(int dx, int dy, int mode);
	public void setDrawBorderAxes(boolean[] border);
	public void setGridDistances(double[] ticks);
	public void setGridType(int selectedIndex);
	public void setPointCapturing(int mode);
	public void setPositiveAxes(boolean[] positiveAxis); 

	/** sets if the axis is drawn in the positive direction only
	 * @param axis
	 * @param isPositive
	 */
	public void setPositiveAxis(int axis, boolean isPositive);

	public void setRealWorldCoordSystem(double min, double max, double ymin, double ymax);

	// selection rectangle
	public void setSelectionRectangle(geogebra.common.awt.Rectangle selectionRectangle);
	public void setShowAxesRatio(boolean b);
	public void setShowAxesNumbers(boolean[] showNums);

	/** sets if numbers are shown on this axis
	 * @param axis
	 * @param showAxisNumbers
	 */
	public void setShowAxisNumbers(int axis, boolean showAxisNumbers);

	public void setShowMouseCoords(boolean b);
	public void setXminObject(NumberValue minMax);
	public void setXmaxObject(NumberValue minMax);
	public void setYminObject(NumberValue minMax);
	public void setYmaxObject(NumberValue minMax);

	public void updateBackground();
	public void updateBounds();
	public void updateBoundObjects();

	// screen coordinate to real world coordinate
	/** convert screen coordinate x to real world coordinate x */
	public double toRealWorldCoordX(double minX);
	/** convert screen coordinate y to real world coordinate y */	
	public double toRealWorldCoordY(double maxY);

	public int toScreenCoordX(double minX);
	public int toScreenCoordY(double maxY);

	public boolean hitAnimationButton(AbstractEvent event);

	public void setHits(Point mouseLoc);

	public Hits getHits();

	public void setDefaultCursor();

	public void setHitCursor();

	public boolean requestFocusInWindow();
	
	public EuclidianStyleBar getStyleBar();

	public boolean setAnimationButtonsHighlighted(boolean b);

	public void setShowAxis(int axisX, boolean b, boolean c);

	public void setDragCursor();

	public Rectangle getSelectionRectangle();

	public void setAnimatedRealWorldCoordSystem(double realWorldCoordX,
			double realWorldCoordX2, double realWorldCoordY,
			double realWorldCoordY2, int i, boolean b);

	public void setHits(geogebra.common.awt.Rectangle selectionRectangle);

}
