/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

package org.geogebra.common.euclidian;

import java.util.ArrayList;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.awt.GPoint;
import org.geogebra.common.awt.GRectangle;
import org.geogebra.common.euclidian.event.PointerEventType;
import org.geogebra.common.io.XMLStringBuilder;
import org.geogebra.common.kernel.Kernel;
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
	int AXIS_X = 0;
	/** reference to y axis */
	int AXIS_Y = 1;
	/** reference to z axis */
	int AXIS_Z = 2;

	/**
	 * Zooms around fixed point (px, py)
	 */
	@Override
	void zoom(double px, double py, double zoomFactor, int steps,
			boolean storeUndo);

	// void changeLayer(GeoElement geo, int oldlayer, int newlayer);

	// mode
	/**
	 * Clears all selections and highlighting
	 */
	void resetMode();

	/**
	 * Repaints the whole view
	 */
	void repaint();

	/** remembers the origins values (xzero, ...) */
	void rememberOrigins();

	// ///////////////////////////////////////
	// previewables

	/**
	 * create a previewable for line construction
	 * 
	 * @param selectedPoints
	 *            points
	 * @return the line previewable
	 */
	Previewable createPreviewLine(ArrayList<GeoPointND> selectedPoints);

	/**
	 * create a previewable for segment construction
	 * 
	 * @param selectedPoints
	 *            points
	 * @return the segment previewable
	 */
	Previewable createPreviewSegment(
			ArrayList<GeoPointND> selectedPoints);

	/**
	 * create a previewable for ray construction
	 * 
	 * @param selectedPoints
	 *            points
	 * @return the ray previewable
	 */
	Previewable createPreviewRay(ArrayList<GeoPointND> selectedPoints);

	/**
	 * create a previewable for vector construction
	 * 
	 * @param selectedPoints
	 *            points
	 * @return the ray previewable
	 */
	Previewable createPreviewVector(
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
	Previewable createPreviewConic(int mode,
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
	Previewable createPreviewParabola(
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
	Previewable createPreviewParallelLine(
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
	Previewable createPreviewPerpendicularLine(
			ArrayList<GeoPointND> selectedPoints,
			ArrayList<GeoLineND> selectedLines,
			ArrayList<GeoFunction> selectedFunctions);

	/**
	 * @param selectedPoints
	 *            points
	 * @return preview perpendicular bisector
	 */
	Previewable createPreviewPerpendicularBisector(
			ArrayList<GeoPointND> selectedPoints);

	/**
	 * @param selectedPoints
	 *            points
	 * @return preview angle bisector
	 */
	Previewable createPreviewAngleBisector(
			ArrayList<GeoPointND> selectedPoints);

	/**
	 * Called when mouse enters the view
	 */
	void mouseEntered();

	/**
	 * Called when mouse exits the view
	 */
	void mouseExited();

	/**
	 * @return application
	 */
	App getApplication();

	/**
	 * @param geo
	 *            geo
	 * @return drawable for given geo
	 */
	DrawableND getDrawableFor(GeoElementND geo);

	/**
	 * @return whether the view has some visible objects
	 */
	boolean hasVisibleObjects();

	/**
	 * 
	 * @return string description of plane from the view was created
	 */
	String getFromPlaneString();

	/**
	 * 
	 * @return string translated description of plane from the view was created
	 */
	String getTranslatedFromPlaneString();

	/**
	 * @return whether grid distance is automatic
	 */
	boolean isAutomaticGridDistance();

	/**
	 * Whether axes have automatic number distances
	 * 
	 * @return array {xauto,yauto}
	 */
	boolean[] isAutomaticAxesNumberingDistance();

	/**
	 * @return whether grid ore axes are shown
	 */
	boolean isGridOrAxesShown();

	/**
	 * returns true if the axes ratio is 1
	 * 
	 * @return true if the axes ratio is 1
	 */
	boolean isLockedAxesRatio();

	/**
	 * @return true if bounds are not dynamic
	 */
	boolean isZoomable();

	/**
	 * @return tooltip mode
	 */
	int getAllowToolTips();

	/**
	 * @return whether showing mouse coords is allowed
	 */
	boolean getAllowShowMouseCoords();

	/**
	 * @return coordinates of axes crossing
	 */
	double[] getAxesCross();

	/**
	 * @return array with axes numbering distances
	 */
	double[] getAxesNumberingDistances();

	/**
	 * @param addBoldItalicTags
	 *            whether to add &lt;b&gt; etc
	 * @return array with axes labels
	 */
	String[] getAxesLabels(boolean addBoldItalicTags);

	/**
	 * @return array with axes line styles
	 */
	int getAxesLineStyle();

	/**
	 * @return array with axes tick styles
	 */
	int[] getAxesTickStyles();

	/**
	 * @return array with axes units
	 */
	String[] getAxesUnitLabels();

	/**
	 * @return background color of this view
	 */
	GColor getBackgroundCommon();

	/**
	 * @return array of flags determining whether axes are drawn next to border
	 */
	boolean[] getDrawBorderAxes();

	/**
	 * @param i
	 *            axis index
	 * @return grid distance in given direction
	 */
	double getGridDistances(int i);

	/**
	 * @return true if grid is bold
	 */
	boolean getGridIsBold();

	/**
	 * @return grid line style
	 */
	int getGridLineStyle();

	/**
	 * @return grid type (cartesian, isometric, polar)
	 */
	int getGridType();

	/**
	 * @return 1/getXScale()
	 */
	double getInvXscale();

	/**
	 * @return 1/getYScale()
	 */
	double getInvYscale();

	/**
	 * @return mode
	 */
	int getMode();

	/**
	 * @return array of flags for positive direction only of axes
	 */
	boolean[] getPositiveAxes();

	/**
	 * @return current previewable
	 */
	Previewable getPreviewDrawable();

	/**
	 * @return array of flags for showing axes numbering
	 */
	boolean[] getShowAxesNumbers();

	/**
	 * @return true if grid is shown
	 */
	boolean getShowGrid();

	/**
	 * @return true if mouse coords are shown
	 */
	boolean getShowMouseCoords();

	/**
	 * @param axis
	 *            axis index
	 * @return true if shown
	 */
	boolean getShowAxis(int axis);

	/**
	 * @return true if x-axis is shown
	 */
	boolean getShowXaxis();

	/**
	 * @return true if y-axis is shown
	 */
	boolean getShowYaxis();

	/**
	 * @return view width
	 */
	int getViewWidth();

	/**
	 * @return view height
	 */
	int getViewHeight();

	/**
	 * @return xMin as GeoNumeric (may be dependent)
	 */
	GeoNumeric getXminObject();

	/**
	 * @return xMax as GeoNumeric (may be dependent)
	 */
	GeoNumeric getXmaxObject();

	/**
	 * @return yMin as geoNumeric (may be dependent)
	 */
	GeoNumeric getYminObject();

	/**
	 * @return yMax as GeoNumeric (may be dependent)
	 */
	GeoNumeric getYmaxObject();

	/**
	 * @return screen x-coord of origin
	 */
	double getXZero();

	/**
	 * @return screen y-coord of origin
	 */
	double getYZero();

	/**
	 * @param automatic
	 *            true for automatic numbering
	 * @param axis
	 *            axis index
	 */
	void setAutomaticAxesNumberingDistance(boolean automatic, int axis);

	/**
	 * @param automatic
	 *            automatic grid distance
	 */
	void setAutomaticGridDistance(boolean automatic);

	/**
	 * @param allow
	 *            true to allow showing mouse coords
	 */
	void setAllowShowMouseCoords(boolean allow);

	/**
	 * @param labels
	 *            array of labels
	 */
	void setAxesLabels(String[] labels);

	/**
	 * @param style
	 *            axis style (full, arrow)
	 */
	void setAxesLineStyle(int style);

	/**
	 * @param styles
	 *            array of axis tick styles (minor, major, ...)
	 */
	void setAxesTickStyles(int[] styles);

	/**
	 * @param unitLabels
	 *            array of unit labels
	 */
	void setAxesUnitLabels(String[] unitLabels);

	/**
	 * @param tickDist
	 *            tick distance
	 * @param axis
	 *            axis index
	 */
	void setAxesNumberingDistance(GeoNumberValue tickDist, int axis);

	/**
	 * @param axisCross
	 *            array ofcrossing values
	 */
	void setAxesCross(double[] axisCross);

	/**
	 * sets the axis crossing value
	 * 
	 * @param axis
	 *            axis index
	 * @param cross
	 *            crossing value
	 */
	void setAxisCross(int axis, double cross);

	/**
	 * sets the axis label to axisLabel
	 * 
	 * @param axis
	 *            axis index
	 * @param axisLabel
	 *            label
	 */
	void setAxisLabel(int axis, String axisLabel);

	/**
	 * sets the tickstyle of this axis
	 * 
	 * @param axis
	 *            axis index
	 * @param tickStyle
	 *            tick style
	 */
	void setAxisTickStyle(int axis, int tickStyle);

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
	void setCoordSystem(double xZero, double yZero, double xscale,
			double yscale);

	/**
	 * @param dx
	 *            x movement (in pixels)
	 * @param dy
	 *            y movement (in pixels)
	 * @param dz
	 *            z movement (for 3D) (in pixels)
	 */
	void translateCoordSystemInPixels(int dx, int dy, int dz);

	/**
	 * translate coord system after page up/down key pressed
	 * 
	 * @param height
	 *            z movement in pixels
	 */
	void pageUpDownTranslateCoordSystem(int height);

	/**
	 * @param border
	 *            array of show-axis-on-border flags
	 */
	void setDrawBorderAxes(boolean[] border);

	/**
	 * @param ticks
	 *            {xdistance, ydistance}
	 */
	void setGridDistances(double[] ticks);

	/**
	 * @param type
	 *            grid type (see EuclidianStyleConstants)
	 */
	void setGridType(int type);

	/**
	 * @param positiveAxis
	 *            array of positive direction only flags
	 */
	void setPositiveAxes(boolean[] positiveAxis);

	/**
	 * sets if the axis is drawn in the positive direction only
	 * 
	 * @param axis
	 *            axis index
	 * @param isPositive
	 *            true to positive direction only
	 */
	void setPositiveAxis(int axis, boolean isPositive);

	/**
	 * @param b
	 *            true to show axes ratio
	 */
	void setShowAxesRatio(boolean b);

	/**
	 * @param showNums
	 *            array of flags for axes numbering
	 */
	void setShowAxesNumbers(boolean[] showNums);

	/**
	 * sets if numbers are shown on this axis
	 * 
	 * @param axis
	 *            axis index
	 * @param showAxisNumbers
	 *            true to show numbers
	 */
	void setShowAxisNumbers(int axis, boolean showAxisNumbers);

	/**
	 * @param b
	 *            true to show mouse coordinates in this view
	 */
	void setShowMouseCoords(boolean b);

	/**
	 * @param minMax
	 *            new xMin object
	 */
	void setXminObject(NumberValue minMax);

	/**
	 * minX
	 * 
	 * @param minMax
	 *            new xMax object
	 */
	void setXmaxObject(NumberValue minMax);

	/**
	 * @param minMax
	 *            new yMin object
	 */
	void setYminObject(NumberValue minMax);

	/**
	 * @param minMax
	 *            new yMax object
	 */
	void setYmaxObject(NumberValue minMax);

	/**
	 * 
	 */
	void updateBackground();

	/**
	 * 
	 */
	void updateBoundObjects();

	// screen coordinate to real world coordinate

	/**
	 * @param rwx
	 *            realworld y-coord
	 * @return screen y-coord
	 */
	int toScreenCoordX(double rwx);

	/**
	 * @param rwy
	 *            realworld y-coord
	 * @return screen y-coord
	 */
	int toScreenCoordY(double rwy);

	/**
	 * @param x
	 *            mouse event x-coord
	 * @param y
	 *            mouse event y-coord
	 * @return true if animation button was hit
	 */
	boolean hitAnimationButton(int x, int y);

	/**
	 * Set the hits regarding to the mouse location
	 * 
	 * @param mouseLoc
	 *            update hits using mouse position
	 * @param t
	 *            event type
	 */
	void setHits(GPoint mouseLoc, PointerEventType t);

	/**
	 * Get the hits recorded
	 * 
	 * @return current hits
	 */
	Hits getHits();

	/**
	 * Switch to hit cursor
	 * 
	 * @param cursor
	 *            cursor
	 */
	void setCursor(EuclidianCursor cursor);

	/**
	 * Try to focus this view
	 * 
	 * @return true if successful
	 */
	boolean requestFocusInWindow();

	/**
	 * @return style bar
	 */
	EuclidianStyleBar getStyleBar();

	/**
	 * @return dynamic style bar
	 */
	EuclidianStyleBar getDynamicStyleBar();

	/**
	 * @return whether style bar exists
	 */
	boolean hasStyleBar();

	/**
	 * @return whether dynamic style bar exists
	 */
	boolean hasDynamicStyleBar();

	/**
	 * Updates highlighting of animation buttons.
	 * 
	 * @param b
	 *            true to highlight
	 * @return whether status was changed
	 */
	boolean setAnimationButtonsHighlighted(boolean b);

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
	boolean setShowAxis(int axis, boolean flag, boolean update);

	/**
	 * @return selection rectangle
	 */
	GRectangle getSelectionRectangle();

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
	void setAnimatedRealWorldCoordSystem(double realWorldCoordX,
			double realWorldCoordX2, double realWorldCoordY,
			double realWorldCoordY2, int steps, boolean storeUndo);

	/**
	 * update the cursor as if mouse has moved over this point
	 * 
	 * @param point
	 *            point
	 */
	void updateCursor(GeoPointND point);

	/**
	 * @param plainTooltip
	 *            sets tooltip text
	 */
	void setToolTipText(String plainTooltip);

	/**
	 * @param mouseLoc
	 *            mouse location
	 * @param type
	 *            event type
	 * @return hit geo (or null)
	 */
	GeoElement getLabelHit(GPoint mouseLoc, PointerEventType type);

	/**
	 * Updates previewable
	 */
	void updatePreviewable();

	/**
	 * Updates previewable
	 */
	void updatePreviewableForProcessMode();

	/**
	 * @return number of euclidian view
	 */
	int getEuclidianViewNo();

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
	void setAnimatedCoordSystem(double originX, double originY,
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
	boolean setShowAxes(boolean flag, boolean update);

	/**
	 * create a previewable for polygon construction
	 * 
	 * @param selectedPoints
	 *            points
	 * @return the polygon previewable
	 */
	Previewable createPreviewPolygon(
			ArrayList<GeoPointND> selectedPoints);

	/**
	 * create a previewable for polyline construction
	 * 
	 * @param selectedPoints
	 *            points
	 * @return the polygon previewable
	 */
	Previewable createPreviewPolyLine(
			ArrayList<GeoPointND> selectedPoints);

	/**
	 * @param selectedPoints
	 *            points
	 * @return preview angle
	 */
	Previewable createPreviewAngle(ArrayList<GeoPointND> selectedPoints);

	/**
	 * @param previewable
	 *            new previewable
	 */
	void setPreview(Previewable previewable);

	/**
	 * @param sb
	 *            string builder
	 * @param asPreference
	 *            as preference
	 */
	void getXML(XMLStringBuilder sb, boolean asPreference);

	/**
	 * @param showAxesCornerCoords
	 *            true to allow showing coords in corners
	 */
	void setAxesCornerCoordsVisible(boolean showAxesCornerCoords);

	/**
	 * @param show
	 *            true to show grid
	 * @return whether setting changed
	 */
	boolean showGrid(boolean show);

	/**
	 * @param bold
	 *            true for bold
	 */
	void setGridIsBold(boolean bold);

	/**
	 * @param type
	 *            line type (see EuclidianStyleConstants)
	 */
	void setGridLineStyle(int type);

	/**
	 * @param geo
	 *            geo
	 * @param isControlDown
	 *            whether control key is down (multiple selection)
	 */
	void clickedGeo(GeoElement geo, boolean isControlDown);

	/**
	 * @param geo
	 *            geo that mouse moved over
	 */
	void mouseMovedOver(GeoElement geo);

	/**
	 * highlight this geo
	 * 
	 * @param geo
	 *            geo
	 */
	void highlight(GeoElement geo);

	/**
	 * highlight list of geos
	 * 
	 * @param geos
	 *            geos
	 */
	void highlight(ArrayList<GeoElement> geos);

	/**
	 * Warning: only called by AlgebraTreeController
	 * 
	 * @param geoList
	 *            list of geos that mouse moved over
	 */
	void mouseMovedOverList(ArrayList<GeoElement> geoList);

	/**
	 * @param bgColor
	 *            new background color
	 */
	void setBackground(GColor bgColor);

	/**
	 * @param axesColor
	 *            new axes color
	 */
	void setAxesColor(GColor axesColor);

	/**
	 * @param gridColor
	 *            new grid color
	 */
	void setGridColor(GColor gridColor);

	/**
	 * @return true if focused
	 */
	@Override
	boolean hasFocus();

	/**
	 * added so that we can easily show/hide axes in 2D and 3D
	 * 
	 * @param b
	 *            flag to show axes
	 */
	void setShowAxis(boolean b);

	/**
	 * Restores standard view
	 * 
	 * @param storeUndo
	 *            true to store undo info
	 */
	void setStandardView(boolean storeUndo);

	/**
	 * Request focus for this view
	 */
	void requestFocus();

	/**
	 * Change coord system so that all objects are shown
	 *
	 * @param storeUndo
	 *            true to store undo after
	 * @param keepRatio
	 *            true to keep ratio of x and y axes
	 */
	void setViewShowAllObjects(boolean storeUndo, boolean keepRatio);

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
	void zoomAxesRatio(double newRatioX, double newRatioY,
			boolean storeUndo);

	@Override
	EuclidianSettings getSettings();

	/**
	 * @return view direction
	 */
	GeoDirectionND getDirection();

	/**
	 * @return whether this is a view for plane
	 */
	boolean isViewForPlane();

	/**
	 * @param axis
	 *            0/1 for x/y axis
	 * @param flag
	 *            whether it should be log
	 * @param update
	 *            whether to update view after
	 * @return true if the axis is logarithmic
	 */
	boolean setLogAxis(int axis, boolean flag, boolean update);

	/**
	 * @return whether x-axis is logarithmic
	 */
	boolean getXaxisLog();

	/**
	 * @return whether y-axis is logarithmic
	 */
	boolean getYaxisLog();

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
	Coords getCoordsForView(Coords coordsInD3);

	/**
	 * Notify view about screen size change
	 */
	void screenChanged();

	/**
	 * @return whether this view is visible
	 */
	boolean isShowing();

	/**
	 * @return EV positioner
	 */
	EvPositioner getEvPositioner();

	/**
	 * @return width for export
	 */
	int getExportWidth();

	/**
	 * @return height for export
	 */
	int getExportHeight();

	/**
	 * @return kernel
	 */
	Kernel getKernel();

	/**
	 * @return axis distance definitions
	 */
	GeoNumberValue[] getAxesDistanceObjects();
}
