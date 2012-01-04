package geogebra.common.euclidian;

import java.util.ArrayList;

import geogebra.common.euclidian.event.AbstractEvent;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoNumeric;
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

	/**
	 * 
	 * @return true if this is Graphics or Graphics 2
	 */
	public boolean isDefault2D();

	/**
	 * returns true if the axes ratio is 1
	 * @return true if the axes ratio is 1
	 */
	public boolean isUnitAxesRatio();

	public boolean isZoomable();


	public int getAllowToolTips();
	public int getAxesLineStyle();
	public geogebra.common.awt.Color getBackgroundCommon();
	public double[] getGridDistances();
	public int getGridLineStyle();
	public int getGridType();
	public double getInvXscale();
	public double getInvYscale();
	public int getMode();
	public int getPointStyle();
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


	public void setAutomaticGridDistance(boolean b);
	public void setAxesLineStyle(int selectedIndex);
	public void setCoordSystem(double x, double y, double xscale, double yscale);
	public void setCoordSystemFromMouseMove(int dx, int dy, int mode);
	public void setGridDistances(double[] ticks);
	public void setGridType(int selectedIndex);
	public void setRealWorldCoordSystem(double min, double max, double ymin, double ymax);

	// selection rectangle
	public void setSelectionRectangle(geogebra.common.awt.Rectangle selectionRectangle);
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

	public boolean hitAnimationButton(AbstractEvent e);
}
