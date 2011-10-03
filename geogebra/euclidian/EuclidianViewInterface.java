package geogebra.euclidian;

import geogebra.kernel.AlgoElement;
import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoNumeric;
import geogebra.kernel.GeoPoint;
import geogebra.kernel.View;

import geogebra.kernel.arithmetic.NumberValue;

import geogebra.kernel.kernelND.GeoLineND;

import geogebra.kernel.kernelND.GeoPointND;
import geogebra.main.Application;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.util.ArrayList;


/**
 * 
 * Interface between EuclidianView (2D or 3D) and EuclidianController (2D or 3D)
 * 
 * (TODO) see EuclidianView for detail of methods
 * 
 */

public interface EuclidianViewInterface extends View{
	
	public static final int AXES_TICK_STYLE_MAJOR_MINOR = 0;

	public static final int AXES_TICK_STYLE_MAJOR = 1;

	public static final int AXES_TICK_STYLE_NONE = 2;
	
	/** reference to x axis*/
	public static final int AXIS_X = 0; 
	/** reference to y axis*/	
	public static final int AXIS_Y = 1; 

	
	
	public void updateSize();
//	public void repaintEuclidianView();


	/**
	 * Zooms around fixed point (px, py)
	 */
	public void zoom(double px, double py, double zoomFactor, int steps, boolean storeUndo);



	// ??
	boolean hitAnimationButton(MouseEvent e);
	void setPreview(Previewable previewDrawable);
	public Drawable getDrawableFor(GeoElement geo);
	public DrawableND getDrawableND(GeoElement geo);
	public DrawableND createDrawableND(GeoElement geo);
	void setToolTipText(String plain);

	/**
	 * Updates highlighting of animation buttons. 
	 * @return whether status was changed
	 */
	boolean setAnimationButtonsHighlighted(boolean hitAnimationButton);

	/**
	 * Returns point capturing mode.
	 */
	int getPointCapturingMode();

	
	// selection rectangle
	public void setSelectionRectangle(Rectangle selectionRectangle);
	public Rectangle getSelectionRectangle();

	
	
	
	// cursor
	void setMoveCursor();
	void setDragCursor();
	void setDefaultCursor();
	void setHitCursor();
	

	
	
	// mode
	/**
	 * clears all selections and highlighting
	 */
	void resetMode();

	
	// screen coordinate to real world coordinate
	/** convert screen coordinate x to real world coordinate x */
	public double toRealWorldCoordX(double minX);
	/** convert screen coordinate y to real world coordinate y */	
	public double toRealWorldCoordY(double maxY);

	public int toScreenCoordX(double minX);
	public int toScreenCoordY(double maxY);
	/**
	 * Sets real world coord system using min and max values for both axes in
	 * real world values.
	 */
	public void setAnimatedRealWorldCoordSystem(double xmin, double xmax,
			double ymin, double ymax, int steps, boolean storeUndo);





	
	
	
	
	//hits	
	/**get the hits recorded */
	Hits getHits();
	/** set the hits regarding to the mouse location */
	void setHits(Point p);
	
	
	/**
	 * sets array of GeoElements whose visual representation is inside of
	 * the given screen rectangle
	 */
	public void setHits(Rectangle rect);	
	
	GeoElement getLabelHit(Point mouseLoc);
	

	
	//////////////////////////////////////////////////////
	// AXIS, GRID, ETC.
	//////////////////////////////////////////////////////	
	
	
	boolean getShowXaxis();
	boolean getShowYaxis();
	
	boolean isGridOrAxesShown();
	int getGridType();
	void setCoordSystem(double x, double y, double xscale, double yscale);
	
	/**
	 * sets showing flag of the axis
	 * @param axis id of the axis
	 * @param flag show/hide
	 * @param update update (or not) the background image
	 */
	public void setShowAxis(int axis, boolean flag, boolean update);
	
	/**
	 * sets showing flag of all axes
	 * @param flag show/hide
	 * @param update update (or not) the background image
	 */	
	public void setShowAxes(boolean flag, boolean update);
	
	
	/**
	 * sets the axis label to axisLabel
	 * @param axis
	 * @param axisLabel
	 */
	public void setAxisLabel(int axis, String axisLabel);
	
	
	/** sets if numbers are shown on this axis
	 * @param axis
	 * @param showAxisNumbers
	 */
	public void setShowAxisNumbers(int axis, boolean showAxisNumbers);
	
	
	/** sets the tickstyle of this axis
	 * @param axis
	 * @param tickStyle
	 */
	public void setAxisTickStyle(int axis, int tickStyle);
	
	
	/** sets the axis crossing value
	 * @param axis
	 * @param cross
	 */
	public void setAxisCross(int axis, double cross);
	
	
	/** sets if the axis is drawn in the positive direction only
	 * @param axis
	 * @param isPositive
	 */
	public void setPositiveAxis(int axis, boolean isPositive);
	
	
	
	
	
	/** Sets coord system from mouse move */
	public void setCoordSystemFromMouseMove(int dx, int dy, int mode);
	void setAnimatedCoordSystem(double ox, double oy, double f, double newScale,int steps, boolean storeUndo);


	//setters and getters	
	public void setShowMouseCoords(boolean b);
	public boolean getShowMouseCoords();
	double getXZero();
	double getYZero();
	public double getInvXscale();
	public double getInvYscale();
	double getXscale();
	double getYscale();
	public void setShowAxesRatio(boolean b);
	public Previewable getPreviewDrawable();
	public int getViewWidth();
	public int getViewHeight();
	public double getGridDistances(int i);
	public String[] getAxesLabels();
	public void setAxesLabels(String[] labels);
	public String[] getAxesUnitLabels();
	public void setShowAxesNumbers(boolean[] showNums);
	public void setAxesUnitLabels(String[] unitLabels);
	public boolean[] getShowAxesNumbers();
	public void setAxesNumberingDistance(double tickDist, int axis);
	public int[] getAxesTickStyles();
	
	public double[] getAxesCross() ;
	public void setAxesCross(double[] axisCross); 
	
	public boolean[] getPositiveAxes(); 
	public void setPositiveAxes(boolean[] positiveAxis); 


	
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
	 * create a previewable for polygon construction
	 * @param selectedPoints points
	 * @return the polygon previewable
	 */		
	public Previewable createPreviewPolygon(ArrayList<GeoPointND> selectedPoints);
	

	/**
	 * create a previewable for polyline construction
	 * @param selectedPoints points
	 * @return the polygon previewable
	 */		
	public Previewable createPreviewPolyLine(ArrayList<GeoPointND> selectedPoints);
	
	/**
	 * create a previewable for conic construction
	 * @param mode 
	 * @param selectedPoints points
	 * @return the conic previewable
	 */		
	public Previewable createPreviewConic(int mode, ArrayList<GeoPointND> selectedPoints);
	


	public void updatePreviewable();
	
	
	public void mouseEntered();
	public void mouseExited();
	public Previewable createPreviewParallelLine(ArrayList<GeoPointND> selectedPoints,
			ArrayList<GeoLineND> selectedLines);
	public Previewable createPreviewPerpendicularLine(ArrayList<GeoPointND> selectedPoints,
			ArrayList<GeoLineND> selectedLines);
	public Previewable createPreviewPerpendicularBisector(ArrayList<GeoPointND> selectedPoints);
	public Previewable createPreviewAngleBisector(ArrayList<GeoPointND> selectedPoints);
	
	
	
	//options
	public Color getBackground();
	public Color getAxesColor();
	public Color getGridColor();
	public boolean getShowGrid();
	public boolean getGridIsBold();
	public boolean getAllowShowMouseCoords();
	public double getXmin();
	public double getXmax();
	public double getYmin();
	public double getYmax();
	public int getAxesLineStyle();
	public int getGridLineStyle();
	public boolean isAutomaticGridDistance();
	public double[] getGridDistances();
	public void setBackground(Color showColorChooser);
	public void setAxesColor(Color showColorChooser);
	public void setGridColor(Color showColorChooser);
	public void showGrid(boolean selected);
	public void setGridIsBold(boolean selected);
	public void setAllowShowMouseCoords(boolean selected);
	public void setGridType(int selectedIndex);
	public void setAxesLineStyle(int selectedIndex);
	public void setGridLineStyle(int type);
	public void setAutomaticGridDistance(boolean b);
	public void setRealWorldCoordSystem(double min, double max, double ymin,
			double ymax);
	public void updateBackground();
	public void setGridDistances(double[] ticks);
	public void setAutomaticAxesNumberingDistance(boolean b, int axis);
	public void setAxesTickStyles(int[] styles);
	public boolean[] getDrawBorderAxes();
	public void setDrawBorderAxes(boolean[] border);
	public boolean[] isAutomaticAxesNumberingDistance();
	public double[] getAxesNumberingDistances();
	
	// for AlgebraView
	public int getMode();
	public void clickedGeo(GeoElement geo, MouseEvent e);
	public void mouseMovedOver(GeoElement geo);


	
	public void changeLayer(GeoElement geo, int oldlayer, int newlayer);
	public int getWidth();
	public int getHeight();
	public boolean hasFocus();
	public boolean isZoomable();
	public GeoNumeric getXminObject();
	public GeoNumeric getXmaxObject();
	public GeoNumeric getYminObject();
	public GeoNumeric getYmaxObject();
	public void setResizeXAxisCursor();
	public void setResizeYAxisCursor();
	
	
	
	public EuclidianController getEuclidianController();
	public Application getApplication();
	public int getPointStyle();
	public void repaint();
	public void setPointCapturing(int mode);
	
	/**
	 * 
	 * @return string description of plane from the view was created
	 */
	public String getFromPlaneString();
	
	/**
	 * 
	 * @return string translated description of plane from the view was created
	 */
	public String getTranslatedFromPlaneString();
	public Previewable createPreviewAngle(ArrayList<GeoPointND> selectedPoints);

	
	/**
	 * 
	 * @return true if this is Graphics or Graphics 2
	 */
	public boolean isDefault2D();
	
	/**
	 * 
	 * @param geo
	 * @return true if the geo is parent of the view
	 */
	public boolean hasForParent(GeoElement geo);
	
	/**
	 * 
	 * @param algoParent
	 * @return free input points of the algoElement
	 */
	public ArrayList<GeoPoint> getFreeInputPoints(AlgoElement algoParent);
	
	/**
	 * 
	 * @param geo
	 * @return true if the geo is moveable in the view
	 */
	public boolean isMoveable(GeoElement geo);

	public ArrayList<GeoPointND> getStickyPointList();

	
	public void updateBoundObjects();
	public void setXminObject(NumberValue minMax);
	public void setXmaxObject(NumberValue minMax);
	public void setYminObject(NumberValue minMax);
	public void setYmaxObject(NumberValue minMax);
	public void updateBounds();
	public boolean getShowAxis(int axis);

	
	/**
	 * returns true if the axes ratio is 1
	 * @return true if the axes ratio is 1
	 */
	public boolean isUnitAxesRatio();

	public void replaceBoundObject(GeoNumeric num, GeoNumeric geoNumeric);


	public void setCursor(Cursor cursor);


	public Graphics2D getGraphicsForPen();
}
