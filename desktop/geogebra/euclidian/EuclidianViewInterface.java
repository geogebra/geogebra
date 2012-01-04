package geogebra.euclidian;

import geogebra.common.euclidian.Drawable;
import geogebra.common.euclidian.DrawableND;
import geogebra.common.euclidian.EuclidianViewInterfaceSlim;
import geogebra.common.euclidian.EuclidianViewInterfaceCommon;
import geogebra.common.euclidian.Hits;
import geogebra.common.euclidian.Previewable;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoNumeric;
import geogebra.common.kernel.kernelND.GeoDirectionND;
import geogebra.common.kernel.kernelND.GeoLineND;
import geogebra.common.kernel.kernelND.GeoPointND;
import geogebra.common.main.AbstractApplication;




import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import javax.swing.JPanel;


/**
 * 
 * Interface between EuclidianView (2D or 3D) and EuclidianController (2D or 3D)
 * 
 * (TODO) see EuclidianView for detail of methods
 * 
 */

public interface EuclidianViewInterface extends EuclidianViewInterfaceCommon {

	public void updateSize();
//	public void repaintEuclidianView();

	// ??
	boolean hitAnimationButton(MouseEvent e);
	void setPreview(Previewable previewDrawable);
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

	public Rectangle getSelectionRectangle();

	
	
	
	// cursor
	void setMoveCursor();
	void setDragCursor();
	void setDefaultCursor();
	void setHitCursor();

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
	void setHits(geogebra.common.awt.Point mouseLoc);
	
	
	/**
	 * sets array of GeoElements whose visual representation is inside of
	 * the given screen rectangle
	 */
	public void setHits(Rectangle rect);	
	
	GeoElement getLabelHit(geogebra.common.awt.Point mouseLoc);
	

	
	//////////////////////////////////////////////////////
	// AXIS, GRID, ETC.
	//////////////////////////////////////////////////////	
	
	
	boolean getShowXaxis();
	boolean getShowYaxis();
	
	boolean isGridOrAxesShown();

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
	void setAnimatedCoordSystem(double ox, double oy, double f, double newScale,int steps, boolean storeUndo);


	//setters and getters	
	public void setShowMouseCoords(boolean b);
	public boolean getShowMouseCoords();
	double getXZero();
	double getYZero();
	public void setShowAxesRatio(boolean b);
	public Previewable getPreviewDrawable();
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
	public Color getAxesColor();
	public Color getGridColor();
	//GetBackground is still implemented in all implementations, but GetBackgroundCommon should be used instead
	public boolean getShowGrid();
	public boolean getGridIsBold();
	public boolean getAllowShowMouseCoords();
	public void setBackground(geogebra.common.awt.Color showColorChooser);
	public void setAxesColor(Color showColorChooser);
	public void setGridColor(Color showColorChooser);
	public void showGrid(boolean selected);
	public void setGridIsBold(boolean selected);
	public void setAllowShowMouseCoords(boolean selected);
	public void setGridLineStyle(int type);
	public void setAutomaticAxesNumberingDistance(boolean b, int axis);
	public void setAxesTickStyles(int[] styles);
	public boolean[] getDrawBorderAxes();
	public void setDrawBorderAxes(boolean[] border);
	public boolean[] isAutomaticAxesNumberingDistance();
	public double[] getAxesNumberingDistances();
	
	// for AlgebraView

	public void clickedGeo(GeoElement geo, MouseEvent e);
	public void mouseMovedOver(GeoElement geo);


	public int getWidth();
	public int getHeight();
	public boolean hasFocus();
	public void setResizeXAxisCursor();
	public void setResizeYAxisCursor();
	
	
	
	public EuclidianController getEuclidianController();
	public void setPointCapturing(int mode);

	public Previewable createPreviewAngle(ArrayList<GeoPointND> selectedPoints);

	/**
	 * 
	 * @param algoParent
	 * @return free input points of the algoElement
	 */
	//public ArrayList<GeoPoint2> getFreeInputPoints(AlgoElement algoParent);
	
	/**
	 * 
	 * @param geo
	 * @return true if the geo is moveable in the view
	 */
	//public boolean isMoveable(GeoElement geo);

	public boolean getShowAxis(int axis);

	public void setCursor(Cursor cursor);


	public Graphics2D getGraphicsForPen();


	public boolean requestFocusInWindow();

	public JPanel getJPanel();

	//public GeoDirectionND getDirection();
}
