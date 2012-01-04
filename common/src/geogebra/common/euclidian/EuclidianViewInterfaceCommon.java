package geogebra.common.euclidian;

import geogebra.common.kernel.geos.GeoElement;
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

	/**
	 * 
	 * @param geo
	 * @return true if the geo is parent of the view
	 */
	public boolean hasForParent(GeoElement geo);



	public AbstractApplication getApplication();

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
	 * 
	 * @return true if this is Graphics or Graphics 2
	 */
	public boolean isDefault2D();
	public boolean isAutomaticGridDistance();

	public int getAllowToolTips();
	public int getAxesLineStyle();
	public double[] getGridDistances();
	public int getGridLineStyle();
	public int getPointStyle();
	public void setAutomaticGridDistance(boolean b);
	public void setAxesLineStyle(int selectedIndex);
	public void setGridDistances(double[] ticks);

	// selection rectangle
	public void setSelectionRectangle(geogebra.common.awt.Rectangle selectionRectangle);

	public void repaint();
	public void updateBackground();
	public geogebra.common.awt.Color getBackgroundCommon();
}
