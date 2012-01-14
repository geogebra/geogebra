package geogebra.euclidian;

import geogebra.common.euclidian.EuclidianViewInterfaceCommon;
import geogebra.common.euclidian.Previewable;
import geogebra.common.euclidian.event.AbstractEvent;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.kernelND.GeoPointND;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics2D;
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

	
	
	
	// cursor
	void setMoveCursor();

	
	//hits	
	
	
	/**
	 * sets array of GeoElements whose visual representation is inside of
	 * the given screen rectangle
	 */
	public void setHits(Rectangle rect);	
	
	

	
	//////////////////////////////////////////////////////
	// AXIS, GRID, ETC.
	//////////////////////////////////////////////////////	

	
	
	
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

	public void clickedGeo(GeoElement geo, AbstractEvent e);
	public void mouseMovedOver(GeoElement geo);


	public boolean hasFocus();
	
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


	public JPanel getJPanel();

	//public GeoDirectionND getDirection();
}
