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

	public AbstractApplication getApplication();


	public int getAxesLineStyle();
	public void setAxesLineStyle(int selectedIndex);

	// selection rectangle
	public void setSelectionRectangle(geogebra.common.awt.Rectangle selectionRectangle);

	public void repaint();
	public void updateBackground();
	public geogebra.common.awt.Color getBackgroundCommon();
}
