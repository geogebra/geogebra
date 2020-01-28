package org.geogebra.common.euclidian;

import java.util.ArrayList;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.awt.GPoint;
import org.geogebra.common.awt.GRectangle;
import org.geogebra.common.euclidian.event.PointerEventType;
import org.geogebra.common.kernel.arithmetic.NumberValue;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.kernelND.GeoDirectionND;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.kernel.kernelND.GeoLineND;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.kernel.matrix.Coords;
import org.geogebra.common.main.App;
import org.geogebra.common.main.settings.EuclidianSettings;
import org.geogebra.common.main.settings.SettingListener;

/**
 * Interface for n-dimensional Euclidian view
 *
 */
public interface EuclidianViewInterfaceCommon
		extends EuclidianViewInterfaceSlim, SettingListener {

	/** reference to x axis */
	public static final int AXIS_X = 0;
	/** reference to y axis */
	public static final int AXIS_Y = 1;
	/** reference to z axis */
	public static final int AXIS_Z = 2;

	/**
	 * Zooms around fixed point (px, py)
	 */
	@Override
	public void zoom(double px, double py, double zoomFactor, int steps,
			boolean storeUndo);

	// public void changeLayer(GeoElement geo, int oldlayer, int newlayer);

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

	// ///////////////////////////////////////
	// previewables

	/**
	 * create a previewable for line construction
	 * 
	 * @param selectedPoints
	 *            points
	 * @return the line previewable
	 */
	public Previewable createPreviewLine(ArrayList<GeoPointND> selectedPoints);

	/**
	 * create a previewable for segment construction
	 * 
	 * @param selectedPoints
	 *            points
	 * @return the segment previewable
	 */
	public Previewable createPreviewSegment(
			ArrayList<GeoPointND> selectedPoints);

	/**
	 * create a previewable for ray construction
	 * 
	 * @param selectedPoints
	 *            points
	 * @return the ray previewable
	 */
	public Previewable createPreviewRay(ArrayList<GeoPointND> selectedPoints);

	/**
	 * create a previewable for vector construction
	 * 
	 * @param selectedPoints
	 *            points
	 * @return the ray previewable
	 */
	public Previewable createPreviewVector(
			ArrayList<GeoPointND> selectedPoints);

	/**
	 * create a previewable for conic construction
	 * 
	 * @param mode
	 *            mode
	 * @param selectedPoints
	 *            points
	 * @return the conic previewable
	 */
	public Previewable createPreviewConic(int mode,
			ArrayList<GeoPointND> selectedPoints);

	/**
	 * create a previewable for parabolas
	 * 
	 * @param selectedPoints
	 *            points
	 * @param selectedLines
	 *            the directrix
	 * @return the conic previewable
	 */
	public Previewable createPreviewParabola(
			ArrayList<GeoPointND> selectedPoints,
			ArrayList<GeoLineND> selectedLines);

	/**
	 * @param selectedPoints
	 *            points
	 * @param selectedLines
	 *            lines
	 * @param selectedFunctions
	 *            functions
	 * @return preview parallel line
	 */
	public Previewable createPreviewParallelLine(
			ArrayList<GeoPointND> selectedPoints,
			ArrayList<GeoLineND> selectedLines,
			ArrayList<GeoFunction> selectedFunctions);

	/**
	 * @param selectedPoints
	 *            points
	 * @param selectedLines
	 *            lines
	 * @param selectedFunctions
	 *            functions
	 * @return preview perpendicular line
	 */
	public Previewable createPreviewPerpendicularLine(
			ArrayList<GeoPointND> selectedPoints,
			ArrayList<GeoLineND> selectedLines,
			ArrayList<GeoFunction> selectedFunctions);

	/**
	 * @param selectedPoints
	 *            points
	 * @return preview perpendicular bisector
	 */
	public Previewable createPreviewPerpendicularBisector(
			ArrayList<GeoPointND> selectedPoints);

	/**
	 * @param selectedPoints
	 *            points
	 * @return preview angle bisector
	 */
	public Previewable createPreviewAngleBisector(
			ArrayList<GeoPointND> selectedPoints);

	/**
	 * Called when mouse enters the view
	 */
	public void mouseEntered();

	/**
	 * Called when mouse exits the view
	 */
	public void mouseExited();

	/**
	 * @return application
	 */
	public App getApplication();

	/**
	 * @param geo
	 *            geo
	 * @return drawable for given geo
	 */
	public DrawableND getDrawableFor(GeoElementND geo);

	/**
	 * @return whether the view has some visible objects
	 */
	public boolean hasVisibleObjects();

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
	 * 
	 * @return array {xauto,yauto}
	 */
	public boolean[] isAutomaticAxesNumberingDistance();

	/**
	 * @return whether grid ore axes are shown
	 */
	public boolean isGridOrAxesShown();

	/**
	 * returns true if the axes ratio is 1
	 * 
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
	 * @param addBoldItalicTags
	 *            whether to add &lt;b&gt; etc
	 * @return array with axes labels
	 */
	public String[] getAxesLabels(boolean addBoldItalicTags);

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
	public GColor getBackgroundCommon();

	/**
	 * @return array of flags determining whether axes are drawn next to border
	 */
	public boolean[] getDrawBorderAxes();

	/**
	 * @param i
	 *            axis index
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
	 * @param axis
	 *            axis index
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
	 * @param automatic
	 *            true for automatic numbering
	 * @param axis
	 *            axis index
	 */
	public void setAutomaticAxesNumberingDistance(boolean automatic, int axis);

	/**
	 * @param automatic
	 *            automatic grid distance
	 */
	public void setAutomaticGridDistance(boolean automatic);

	/**
	 * @param allow
	 *            true to allow showing mouse coords
	 */
	public void setAllowShowMouseCoords(boolean allow);

	/**
	 * @param labels
	 *            array of labels
	 */
	public void setAxesLabels(String[] labels);

	/**
	 * @param style
	 *            axis style (full, arrow)
	 */
	public void setAxesLineStyle(int style);

	/**
	 * @param styles
	 *            array of axis tick styles (minor, major, ...)
	 */
	public void setAxesTickStyles(int[] styles);

	/**
	 * @param unitLabels
	 *            array of unit labels
	 */
	public void setAxesUnitLabels(String[] unitLabels);

	/**
	 * @param tickDist
	 *            tick distance
	 * @param axis
	 *            axis index
	 */
	public void setAxesNumberingDistance(GeoNumberValue tickDist, int axis);

	/**
	 * @param axisCross
	 *            array ofcrossing values
	 */
	public void setAxesCross(double[] axisCross);

	/**
	 * sets the axis crossing value
	 * 
	 * @param axis
	 *            axis index
	 * @param cross
	 *            crossing value
	 */
	public void setAxisCross(int axis, double cross);

	/**
	 * sets the axis label to axisLabel
	 * 
	 * @param axis
	 *            axis index
	 * @param axisLabel
	 *            label
	 */
	public void setAxisLabel(int axis, String axisLabel);

	/**
	 * sets the tickstyle of this axis
	 * 
	 * @param axis
	 *            axis index
	 * @param tickStyle
	 *            tick style
	 */
	public void setAxisTickStyle(int axis, int tickStyle);

	/**
	 * @param xZero
	 *            screen x-coord of origin
	 * @param yZero
	 *            screen y-coord of origin
	 * @param xscale
	 *            x scale
	 * @param yscale
	 *            y scale
	 */
	public void setCoordSystem(double xZero, double yZero, double xscale,
			double yscale);

	/**
	 * @param dx
	 *            x movement (in pixels)
	 * @param dy
	 *            y movement (in pixels)
	 * @param dz
	 *            z movement (for 3D) (in pixels)
	 */
	public void translateCoordSystemInPixels(int dx, int dy, int dz);

	/**
	 * translate coord system after page up/down key pressed
	 * 
	 * @param height
	 *            z movement in pixels
	 */
	public void pageUpDownTranslateCoordSystem(int height);

	/**
	 * @param border
	 *            array of show-axis-on-border flags
	 */
	public void setDrawBorderAxes(boolean[] border);

	/**
	 * @param ticks
	 *            {xdistance, ydistance}
	 */
	public void setGridDistances(double[] ticks);

	/**
	 * @param type
	 *            grid type (see EuclidianStyleConstants)
	 */
	public void setGridType(int type);

	/**
	 * @param positiveAxis
	 *            array of positive direction only flags
	 */
	public void setPositiveAxes(boolean[] positiveAxis);

	/**
	 * sets if the axis is drawn in the positive direction only
	 * 
	 * @param axis
	 *            axis index
	 * @param isPositive
	 *            true to positive direction only
	 */
	public void setPositiveAxis(int axis, boolean isPositive);

	/**
	 * @param b
	 *            true to show axes ratio
	 */
	public void setShowAxesRatio(boolean b);

	/**
	 * @param showNums
	 *            array of flags for axes numbering
	 */
	public void setShowAxesNumbers(boolean[] showNums);

	/**
	 * sets if numbers are shown on this axis
	 * 
	 * @param axis
	 *            axis index
	 * @param showAxisNumbers
	 *            true to show numbers
	 */
	public void setShowAxisNumbers(int axis, boolean showAxisNumbers);

	/**
	 * @param b
	 *            true to show mouse coordinates in this view
	 */
	public void setShowMouseCoords(boolean b);

	/**
	 * @param minMax
	 *            new xMin object
	 */
	public void setXminObject(NumberValue minMax);

	/**
	 * minX
	 * 
	 * @param minMax
	 *            new xMax object
	 */
	public void setXmaxObject(NumberValue minMax);

	/**
	 * @param minMax
	 *            new yMin object
	 */
	public void setYminObject(NumberValue minMax);

	/**
	 * @param minMax
	 *            new yMax object
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
	 * @param rwx
	 *            realworld y-coord
	 * @return screen y-coord
	 */
	public int toScreenCoordX(double rwx);

	/**
	 * @param rwy
	 *            realworld y-coord
	 * @return screen y-coord
	 */
	public int toScreenCoordY(double rwy);

	/**
	 * @param x
	 *            mouse event x-coord
	 * @param y
	 *            mouse event y-coord
	 * @return true if animation button was hit
	 */
	public boolean hitAnimationButton(int x, int y);

	/**
	 * Set the hits regarding to the mouse location
	 * 
	 * @param mouseLoc
	 *            update hits using mouse position
	 * @param t
	 *            event type
	 */
	public void setHits(GPoint mouseLoc, PointerEventType t);

	/**
	 * Get the hits recorded
	 * 
	 * @return current hits
	 */
	public Hits getHits();

	/**
	 * Switch to hit cursor
	 * 
	 * @param cursor
	 *            cursor
	 */
	public void setCursor(EuclidianCursor cursor);

	/**
	 * Try to focus this view
	 * 
	 * @return true if successful
	 */
	public boolean requestFocusInWindow();

	/**
	 * @return style bar
	 */
	public EuclidianStyleBar getStyleBar();

	/**
	 * @return dynamic style bar
	 */
	public EuclidianStyleBar getDynamicStyleBar();

	/**
	 * Updates highlighting of animation buttons.
	 * 
	 * @param b
	 *            true to highlight
	 * @return whether status was changed
	 */
	public boolean setAnimationButtonsHighlighted(boolean b);

	/**
	 * sets showing flag of the axis
	 * 
	 * @param axis
	 *            id of the axis
	 * @param flag
	 *            show/hide
	 * @param update
	 *            update (or not) the background image
	 * @return whether something changed
	 */
	public boolean setShowAxis(int axis, boolean flag, boolean update);

	/**
	 * @return selection rectangle
	 */
	public GRectangle getSelectionRectangle();

	/**
	 * Sets real world coord system using min and max values for both axes in
	 * real world values.
	 * 
	 * @param realWorldCoordX
	 *            new xMin
	 * @param realWorldCoordX2
	 *            new xMax
	 * @param realWorldCoordY
	 *            new yMin
	 * @param realWorldCoordY2
	 *            new yMax
	 * @param steps
	 *            number of animation steps
	 * @param storeUndo
	 *            true to store undo info
	 */
	public void setAnimatedRealWorldCoordSystem(double realWorldCoordX,
			double realWorldCoordX2, double realWorldCoordY,
			double realWorldCoordY2, int steps, boolean storeUndo);

	/**
	 * update the cursor as if mouse has moved over this point
	 * 
	 * @param point
	 *            point
	 */
	public void updateCursor(GeoPointND point);

	/**
	 * @param plainTooltip
	 *            sets tooltip text
	 */
	public void setToolTipText(String plainTooltip);

	/**
	 * @param mouseLoc
	 *            mouse location
	 * @param type
	 *            event type
	 * @return hit geo (or null)
	 */
	public GeoElement getLabelHit(GPoint mouseLoc, PointerEventType type);

	/**
	 * Updates previewable
	 */
	public void updatePreviewable();

	/**
	 * Updates previewable
	 */
	public void updatePreviewableForProcessMode();

	/**
	 * @return number of euclidian view
	 */
	public int getEuclidianViewNo();

	/**
	 * @param rwX
	 *            real world x-coord
	 * @return screen x-coord
	 */
	public double toScreenCoordXd(double rwX);

	/**
	 * @param rwY
	 *            real world y-coord
	 * @return screen y-coord
	 */
	public double toScreenCoordYd(double rwY);

	/**
	 * Zooms about P with given factor
	 * 
	 * @param originX
	 *            x coord of old origin
	 * @param originY
	 *            y coord of old origin
	 * @param factor
	 *            zoom factor
	 * @param newScale
	 *            x scale
	 * @param steps
	 *            number of animated steps
	 * @param storeUndo
	 *            to store undo info after
	 */
	public void setAnimatedCoordSystem(double originX, double originY,
			double factor, double newScale, int steps, boolean storeUndo);

	/**
	 * sets showing flag of all axes
	 * 
	 * @param flag
	 *            show/hide
	 * @param update
	 *            update (or not) the background image
	 * @return whether setting changed
	 */
	public boolean setShowAxes(boolean flag, boolean update);

	/**
	 * create a previewable for polygon construction
	 * 
	 * @param selectedPoints
	 *            points
	 * @return the polygon previewable
	 */
	public Previewable createPreviewPolygon(
			ArrayList<GeoPointND> selectedPoints);

	/**
	 * create a previewable for polyline construction
	 * 
	 * @param selectedPoints
	 *            points
	 * @return the polygon previewable
	 */
	public Previewable createPreviewPolyLine(
			ArrayList<GeoPointND> selectedPoints);

	/**
	 * @param selectedPoints
	 *            points
	 * @return preview angle
	 */
	public Previewable createPreviewAngle(ArrayList<GeoPointND> selectedPoints);

	/**
	 * @param previewable
	 *            new previewable
	 */
	public void setPreview(Previewable previewable);

	/**
	 * @param sb
	 *            string builder
	 * @param asPreference
	 *            as preference
	 */
	public void getXML(StringBuilder sb, boolean asPreference);

	/**
	 * @param showAxesCornerCoords
	 *            true to allow showing coords in corners
	 */
	public void setAxesCornerCoordsVisible(boolean showAxesCornerCoords);

	/**
	 * @param show
	 *            true to show grid
	 * @return whether setting changed
	 */
	public boolean showGrid(boolean show);

	/**
	 * @param bold
	 *            true for bold
	 */
	public void setGridIsBold(boolean bold);

	/**
	 * @param type
	 *            line type (see EuclidianStyleConstants)
	 */
	public void setGridLineStyle(int type);

	/**
	 * @param geo
	 *            geo
	 * @param isControlDown
	 *            whether control key is down (multiple selection)
	 */
	public void clickedGeo(GeoElement geo, boolean isControlDown);

	/**
	 * @param geo
	 *            geo that mouse moved over
	 */
	public void mouseMovedOver(GeoElement geo);

	/**
	 * highlight this geo
	 * 
	 * @param geo
	 *            geo
	 */
	public void highlight(GeoElement geo);

	/**
	 * highlight list of geos
	 * 
	 * @param geos
	 *            geos
	 */
	public void highlight(ArrayList<GeoElement> geos);

	/**
	 * Warning: only called by AlgebraTreeController
	 * 
	 * @param geoList
	 *            list of geos that mouse moved over
	 */
	public void mouseMovedOverList(ArrayList<GeoElement> geoList);

	/**
	 * @param bgColor
	 *            new background color
	 */
	public void setBackground(GColor bgColor);

	/**
	 * @param axesColor
	 *            new axes color
	 */
	public void setAxesColor(GColor axesColor);

	/**
	 * @param gridColor
	 *            new grid color
	 */
	public void setGridColor(GColor gridColor);

	/**
	 * @return true if focused
	 */
	@Override
	public boolean hasFocus();

	/**
	 * added so that we can easily show/hide axes in 2D & 3D
	 * 
	 * @param b
	 *            flag to show axes
	 */
	public void setShowAxis(boolean b);

	/**
	 * Restores standard view
	 * 
	 * @param storeUndo
	 *            true to store undo info
	 */
	public void setStandardView(boolean storeUndo);

	/**
	 * Request focus for this view
	 */
	public void requestFocus();

	/**
	 * Change coord system so that all objects are shown
	 *
	 * @param storeUndo
	 *            true to store undo after
	 * @param keepRatio
	 *            true to keep ratio of x and y axes
	 */
	public void setViewShowAllObjects(boolean storeUndo, boolean keepRatio);

	/**
	 * Zooms towards the given axes scale ratio. Note: Only the y-axis is
	 * changed here unless newRatioY == 1 and then the x-axis is changed.
	 * 
	 * @param newRatioX
	 *            x:y ratio is newRatioX / newRatioY
	 * @param newRatioY
	 *            x:y ratio is newRatioX / newRatioY
	 * @param storeUndo
	 *            true to store undo step after
	 */
	public void zoomAxesRatio(double newRatioX, double newRatioY,
			boolean storeUndo);

	@Override
	public EuclidianSettings getSettings();

	/**
	 * @return view direction
	 */
	public GeoDirectionND getDirection();

	/**
	 * @return whether this is a view for plane
	 */
	public boolean isViewForPlane();

	/**
	 * @param axis
	 *            0/1 for x/y axis
	 * @param flag
	 *            whether it should be log
	 * @param update
	 *            whether to update view after
	 * @return true if the axis is logarithmic
	 */
	public boolean setLogAxis(int axis, boolean flag, boolean update);

	/**
	 * @return whether x-axis is logarithmic
	 */
	public boolean getXaxisLog();

	/**
	 * @return whether y-axis is logarithmic
	 */
	public boolean getYaxisLog();

	/**
	 * Close all dropdowns
	 */
	void closeDropdowns();

	/**
	 * Close all the dropdowns but the one was hit at
	 * 
	 * @param x
	 *            hit x
	 * @param y
	 *            hit y
	 */
	void closeDropDowns(int x, int y);

	/**
	 * 
	 * @param coordsInD3
	 *            3D point coords
	 * @return 2D coords in view's coord system
	 */
	public Coords getCoordsForView(Coords coordsInD3);

	/**
	 * Notify view about screen size change
	 */
	public void screenChanged();

	/**
	 * @return whether this view is visible
	 */
	public boolean isShowing();

}
