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

	public geogebra.common.awt.Rectangle getSelectionRectangle();

	
	
	
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

	/** Sets coord system from mouse move */
	void setAnimatedCoordSystem(double ox, double oy, double f, double newScale,int steps, boolean storeUndo);

	/////////////////////////////////////////
	// previewables

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


	public void updatePreviewable();


	//options
	public Color getAxesColor();
	public Color getGridColor();
	//GetBackground is still implemented in all implementations, but GetBackgroundCommon should be used instead
	public void setBackground(geogebra.common.awt.Color showColorChooser);
	public void setAxesColor(geogebra.common.awt.Color showColorChooser);
	public void setGridColor(geogebra.common.awt.Color showColorChooser);
	public void showGrid(boolean selected);
	public void setGridIsBold(boolean selected);
	public void setGridLineStyle(int type);

	// for AlgebraView

	public void clickedGeo(GeoElement geo, MouseEvent e);
	public void mouseMovedOver(GeoElement geo);


	public int getWidth();
	public int getHeight();
	public boolean hasFocus();
	public void setResizeXAxisCursor();
	public void setResizeYAxisCursor();
	
	
	
	public EuclidianController getEuclidianController();

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

	public void setCursor(Cursor cursor);


	public Graphics2D getGraphicsForPen();


	public boolean requestFocusInWindow();

	public JPanel getJPanel();

	//public GeoDirectionND getDirection();
}
