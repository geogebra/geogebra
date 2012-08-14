package geogebra.common.euclidian;

import geogebra.common.awt.GPoint;
import geogebra.common.awt.GRectangle;
import geogebra.common.euclidian.event.AbstractEvent;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoNumeric;
import geogebra.common.kernel.kernelND.GeoLineND;
import geogebra.common.kernel.kernelND.GeoPointND;
import geogebra.common.main.App;
import geogebra.common.main.settings.SettingListener;

import java.util.ArrayList;

/**
 * Interface for n-dimensional Euclidian view
 *
 */
public interface EuclidianViewInterfaceCommon extends EuclidianViewInterfaceSlim, SettingListener {

	/** reference to x axis*/
	public static final int AXIS_X = 0; 
	/** reference to y axis*/	
	public static final int AXIS_Y = 1;

	/**
	 * Zooms around fixed point (px, py)
	 */
	public void zoom(double px, double py, double zoomFactor, int steps, boolean storeUndo);

	//public void changeLayer(GeoElement geo, int oldlayer, int newlayer);

	// mode
	/**
	 * Clears all selections and highlighting
	 */
	void resetMode();

	/**
	 * Repaints the whole view
	 */
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
	 * @param mode mode
	 * @param selectedPoints points
	 * @return the conic previewable
	 */		
	public Previewable createPreviewConic(int mode, ArrayList<GeoPointND> selectedPoints);

	/**
	 * @param selectedPoints points
	 * @param selectedLines lines
	 * @return preview parallel line
	 */
	public Previewable createPreviewParallelLine(ArrayList<GeoPointND> selectedPoints,
			ArrayList<GeoLineND> selectedLines);
	/**
	 * @param selectedPoints points
	 * @param selectedLines lines
	 * @return preview perpendicular line
	 */
	public Previewable createPreviewPerpendicularLine(ArrayList<GeoPointND> selectedPoints,
			ArrayList<GeoLineND> selectedLines);
	/**
	 * @param selectedPoints points
	 * @return preview perpendicular bisector
	 */
	public Previewable createPreviewPerpendicularBisector(ArrayList<GeoPointND> selectedPoints);
	/**
	 * @param selectedPoints points
	 * @return preview angle bisector
	 */
	public Previewable createPreviewAngleBisector(ArrayList<GeoPointND> selectedPoints);

	/**
	 * Called when mouse enters the view
	 */
	public void mouseEntered();
	/**
	 * Called  when mouse exits the view
	 */
	public void mouseExited();

	/**
	 * @param geo geo
	 * @return true if the geo is parent of the view
	 */
	public boolean hasForParent(GeoElement geo);

	/**
	 * @return application
	 */
	public App getApplication();

	/**
	 * @param geo geo
	 * @return drawable for given geo
	 */
	public DrawableND getDrawableFor(GeoElement geo);

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
	/**
	 * @return whether grid distance is automatic
	 */
	public boolean isAutomaticGridDistance();

	/**
	 * Whether axes have automatic number distances
	 * @return array {xauto,yauto}
	 */
	public boolean[] isAutomaticAxesNumberingDistance();

	/**
	 * @return whether grid ore axes are shown
	 */
	public boolean isGridOrAxesShown();

	/**
	 * returns true if the axes ratio is 1
	 * @return true if the axes ratio is 1
	 */
	public boolean isLockedAxesRatio();

	/**
	 * @return true if bounds are not dynamic
	 */
	public boolean isZoomable();


	/**
	 * @return tooltip mode
	 */
	public int getAllowToolTips();
	/**
	 * @return whether showing mouse coords is allowed
	 */
	public boolean getAllowShowMouseCoords();
	/**
	 * @return coordinates of axes crossing
	 */
	public double[] getAxesCross();
	/**
	 * @return array with axes numbering distances
	 */
	public double[] getAxesNumberingDistances();
	/**
	 * @return array with axes labels
	 */
	public String[] getAxesLabels();
	/**
	 * @return array with axes line styles
	 */
	public int getAxesLineStyle();
	/**
	 * @return array with axes tick styles
	 */
	public int[] getAxesTickStyles();
	/**
	 * @return array with axes units
	 */
	public String[] getAxesUnitLabels();
	/**
	 * @return background color of this view
	 */
	public geogebra.common.awt.GColor getBackgroundCommon();
	/**
	 * @return array of flags determining whether axes are drawn next to border
	 */
	public boolean[] getDrawBorderAxes();

	/**
	 * @param i axis index
	 * @return grid distance in given direction
	 */
	public double getGridDistances(int i);
	/**
	 * @return true if grid is bold
	 */
	public boolean getGridIsBold();
	/**
	 * @return grid line style
	 */
	public int getGridLineStyle();
	/**
	 * @return grid type (cartesian, isometric, polar) 
	 */
	public int getGridType();
	/**
	 * @return 1/getXScale()
	 */
	public double getInvXscale();
	/**
	 * @return 1/getYScale()
	 */
	public double getInvYscale();
	/**
	 * @return mode
	 */
	public int getMode();

	/**
	 * @return array of flags for positive direction only of axes
	 */
	public boolean[] getPositiveAxes(); 
	/**
	 * @return current previewable
	 */
	public Previewable getPreviewDrawable();
	/**
	 * @return array of flags for showing axes numbering
	 */
	public boolean[] getShowAxesNumbers();
	/**
	 * @return true if grid is shown
	 */
	public boolean getShowGrid();
	/**
	 * @return true if mouse coords are shown
	 */
	public boolean getShowMouseCoords();
	/**
	 * @param axis axis index
	 * @return true if shown
	 */
	public boolean getShowAxis(int axis);
	/**
	 * @return true if x-axis is shown
	 */
	public boolean getShowXaxis();
	/**
	 * @return true if y-axis is shown
	 */
	public boolean getShowYaxis();
	/**
	 * @return view width
	 */
	public int getViewWidth();
	/**
	 * @return view height
	 */
	public int getViewHeight();

	/**
	 * @return xMin as GeoNumeric (may be dependent)
	 */
	public GeoNumeric getXminObject();
	/**
	 * @return xMax as GeoNumeric (may be dependent)
	 */
	public GeoNumeric getXmaxObject();
	/**
	 * @return yMin as geoNumeric (may be dependent)
	 */
	public GeoNumeric getYminObject();
	/**
	 * @return yMax as GeoNumeric (may be dependent)
	 */
	public GeoNumeric getYmaxObject();
	/**
	 * @return screen x-coord of origin 
	 */
	public double getXZero();
	/**
	 * @return screen y-coord of origin 
	 */
	public double getYZero();


	/**
	 * @param automatic true for automatic numbering
	 * @param axis axis index
	 */
	public void setAutomaticAxesNumberingDistance(boolean automatic, int axis);
	/**
	 * @param automatic automatic grid distance
	 */
	public void setAutomaticGridDistance(boolean automatic);
	/**
	 * @param allow true to allow showing mouse coords
	 */
	public void setAllowShowMouseCoords(boolean allow);
	/**
	 * @param labels array of labels
	 */
	public void setAxesLabels(String[] labels);
	/**
	 * @param style axis style (full, arrow)
	 */
	public void setAxesLineStyle(int style);
	/**
	 * @param styles array of axis tick styles (minor, major, ...)
	 */
	public void setAxesTickStyles(int[] styles);
	/**
	 * @param unitLabels array of unit labels
	 */
	public void setAxesUnitLabels(String[] unitLabels);
	/**
	 * @param tickDist tick distance
	 * @param axis axis index
	 */
	public void setAxesNumberingDistance(double tickDist, int axis);

	/**
	 * @param axisCross array ofcrossing values
	 */
	public void setAxesCross(double[] axisCross);

	/** sets the axis crossing value
	 * @param axis axis index
	 * @param cross crossing value
	 */
	public void setAxisCross(int axis, double cross);

	/**
	 * sets the axis label to axisLabel
	 * @param axis axis index
	 * @param axisLabel label
	 */
	public void setAxisLabel(int axis, String axisLabel);

	/** sets the tickstyle of this axis
	 * @param axis axis index
	 * @param tickStyle tick style 
	 */
	public void setAxisTickStyle(int axis, int tickStyle);

	/**
	 * @param xZero screen x-coord of origin
	 * @param yZero screen y-coord of origin
	 * @param xscale x scale
	 * @param yscale y scale
	 */
	public void setCoordSystem(double xZero, double yZero, double xscale, double yscale);
	/**
	 * @param dx mouse x movement
	 * @param dy mouse y movement
	 * @param mode current mode
	 */
	public void setCoordSystemFromMouseMove(int dx, int dy, int mode);
	/**
	 * @param border array of show-axis-on-border flags
	 */
	public void setDrawBorderAxes(boolean[] border);
	/**
	 * @param ticks {xdistance, ydistance}
	 */
	public void setGridDistances(double[] ticks);
	/**
	 * @param type grid type (see EuclidianStyleConstants)
	 */
	public void setGridType(int type);
	
	/**
	 * @param positiveAxis array of positive direction only flags
	 */
	public void setPositiveAxes(boolean[] positiveAxis); 

	/** sets if the axis is drawn in the positive direction only
	 * @param axis axis index
	 * @param isPositive true to positive direction only
	 */
	public void setPositiveAxis(int axis, boolean isPositive);

	/**
	 * @param b true to show axes ratio
	 */
	public void setShowAxesRatio(boolean b);
	/**
	 * @param showNums array of flags for axes numbering
	 */
	public void setShowAxesNumbers(boolean[] showNums);

	/** sets if numbers are shown on this axis
	 * @param axis axis index
	 * @param showAxisNumbers true to show numbers
	 */
	public void setShowAxisNumbers(int axis, boolean showAxisNumbers);

	/**
	 * @param b true to show mouse coordinates in this view
	 */
	public void setShowMouseCoords(boolean b);
	/**
	 * @param minMax new xMin object
	 */
	public void setXminObject(NumberValue minMax);
	/**minX
	 * @param minMax new xMax object
	 */
	public void setXmaxObject(NumberValue minMax);
	/**
	 * @param minMax new yMin object
	 */
	public void setYminObject(NumberValue minMax);
	/**
	 * @param minMax new yMax object
	 */
	public void setYmaxObject(NumberValue minMax);

	/**
	 * 
	 */
	public void updateBackground();
	
	/**
	 * 
	 */
	public void updateBoundObjects();

	// screen coordinate to real world coordinate
	

	/**
	 * @param rwx realworld y-coord
	 * @return screen y-coord
	 */
	public int toScreenCoordX(double rwx);
	/**
	 * @param rwy realworld y-coord
	 * @return screen y-coord
	 */
	public int toScreenCoordY(double rwy);

	/**
	 * @param event mouse event
	 * @return true if animation button was hit
	 */
	public boolean hitAnimationButton(AbstractEvent event);

	/** 
	 * Set the hits regarding to the mouse location 
	 * @param mouseLoc update hits using mouse position
	 */
	public void setHits(GPoint mouseLoc);

	/**
	 * Get the hits recorded 
	 * @return current hits 
	 */
	public Hits getHits();
	/**
	 * @param p mouse coords
	 * @return hit button (or null)
	 */
	public MyButton getHitButton(geogebra.common.awt.GPoint p);
	/**
	 * Switch to default cursor
	 */
	public void setDefaultCursor();

	/**
	 * Switch to hit cursor
	 */
	public void setHitCursor();

	/**
	 * Try to focus this view
	 * @return true if successful
	 */
	public boolean requestFocusInWindow();
	
	/**
	 * @return style bar
	 */
	public EuclidianStyleBar getStyleBar();

	/**
	 * Updates highlighting of animation buttons. 
	 * @param b true to highlight
	 * @return whether status was changed
	 */
	public boolean setAnimationButtonsHighlighted(boolean b);

	/**
	 * sets showing flag of the axis
	 * @param axis id of the axis
	 * @param flag show/hide
	 * @param update update (or not) the background image
	 */
	public void setShowAxis(int axis, boolean flag, boolean update);

	/**
	 * Change cursor to drag cursor
	 */
	public void setDragCursor();

	/**
	 * @return selection rectangle
	 */
	public GRectangle getSelectionRectangle();

	/**
	 * Sets real world coord system using min and max values for both axes in
	 * real world values.
	 * @param realWorldCoordX new xMin
	 * @param realWorldCoordX2 new xMax 
	 * @param realWorldCoordY  new yMin
	 * @param realWorldCoordY2 new yMax
	 * @param steps number of animation steps
	 * @param storeUndo true to store undo info
	 */
	public void setAnimatedRealWorldCoordSystem(double realWorldCoordX,
			double realWorldCoordX2, double realWorldCoordY,
			double realWorldCoordY2, int steps, boolean storeUndo);

	/**
	 * Sets hits using given rectangle
	 * @param rect rectangle
	 */
	public void setHits(geogebra.common.awt.GRectangle rect);

	/**
	 * @param plainTooltip sets tooltip text
	 */
	public void setToolTipText(String plainTooltip);

	/**
	 * @param mouseLoc mouse location
	 * @return hit geo (or null)
	 */
	public GeoElement getLabelHit(GPoint mouseLoc);

	/**
	 * Switch to x-resize cursor
	 */
	public void setResizeXAxisCursor();

	/**
	 * Switch to y-resize cursor
	 */
	public void setResizeYAxisCursor();

	/**
	 * Updates previewable
	 */
	public void updatePreviewable();


	/**
	 * Switch to move cursor
	 */
	public void setMoveCursor();


	/**
	 * @return number of euclidian view
	 */
	public int getEuclidianViewNo();

	/**
	 * @param rwX real world x-coord
	 * @return screen x-coord
	 */
	public double toScreenCoordXd(double rwX);
	/**
	 * @param rwY real world y-coord
	 * @return screen y-coord
	 */
	public double toScreenCoordYd(double rwY);

	/** Zooms about P with given factor 
	 * @param originX
	 *            x coord of old origin
	 * @param originY
	 *            y coord of old origin
	 * @param factor zoom factor
	 * @param newScale
	 * 			x scale
	 * @param steps number of animated steps
	 * @param storeUndo to store undo info after */
	public void setAnimatedCoordSystem(double originX, double originY, double factor,
			double newScale, int steps, boolean storeUndo);
	
	/**
	 * sets showing flag of all axes
	 * @param flag show/hide
	 * @param update update (or not) the background image
	 */	
	public void setShowAxes(boolean flag, boolean update);

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
	public Previewable createPreviewPolyLine(
			ArrayList<GeoPointND> selectedPoints);

	/**
	 * @param selectedPoints points
	 * @return preview angle
	 */
	public Previewable createPreviewAngle(ArrayList<GeoPointND> selectedPoints);

	/**
	 * @param previewable new previewable
	 */
	public void setPreview(Previewable previewable);

	/**
	 * @param sb string builder
	 * @param asPreference as preference
	 */
	public void getXML(StringBuilder sb, boolean asPreference);

	/**
	 * @param showAxesCornerCoords true to allow showing coords in corners
	 */
	public void setAxesCornerCoordsVisible(boolean showAxesCornerCoords);
	/**
	 * @param show true to show grid
	 */
	public void showGrid(boolean show);
	/**
	 * @param bold true for bold
	 */
	public void setGridIsBold(boolean bold);
	/**
	 * @param type line type (see EuclidianStyleConstants)
	 */
	public void setGridLineStyle(int type);
	/**
	 * @param geo geo
	 * @param e click event
	 */
	public void clickedGeo(GeoElement geo, AbstractEvent e);
	/**
	 * @param geo geo that mouse moved over
	 */
	public void mouseMovedOver(GeoElement geo);
	

	
	/**
	 * highlight this geo
	 * @param geo geo
	 */
	public void highlight(GeoElement geo);
	
	
	/**
	 * highlight list of geos
	 * @param geos geos
	 */
	public void highlight(ArrayList<GeoElement> geos);
	
	
	/**
	 * Warning: only called by AlgebraTreeController
	 * @param geoList list of geos that mouse moved over
	 */
	public void mouseMovedOverList(ArrayList<GeoElement> geoList);
	/**
	 * @param bgColor new background color
	 */
	public void setBackground(geogebra.common.awt.GColor bgColor);
	/**
	 * @param axesColor new axes color
	 */
	public void setAxesColor(geogebra.common.awt.GColor axesColor);
	/**
	 * @param gridColor new grid color
	 */
	public void setGridColor(geogebra.common.awt.GColor gridColor);
	

	/**
	 * @return true if focused
	 */
	public boolean hasFocus();

	/**
	 * added so that we can easily show/hide axes in 2D & 3D
	 * @param b flag to show axes
	 */
	public void setShowAxis(boolean b);

	/**
	 * Restores standard view
	 * @param storeUndo true to store undo info
	 */
	public void setStandardView(boolean storeUndo);

	public void requestFocus();


}
