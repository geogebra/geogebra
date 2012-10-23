package geogebra.common.kernel.kernelND;

import geogebra.common.euclidian.EuclidianView;

/**
 * Geo that can create a new view (e.g. planes, polygons)
 * 
 * @author mathieu
 *
 */
public interface ViewCreator extends GeoCoordSys2D {

	/** create a 2D view about this coord sys */
	public void createView2D();
	
	/**
	 * set the euclidian view created
	 * @param view view
	 */
	public void setEuclidianViewForPlane(EuclidianView view);
	
	/** tells if the view2D is visible 
	 * @return true if the view2D is visible */
	public boolean hasView2DVisible();
	
	/** sets the view 2D visibility 
	 * @param flag visibility*/
	public void setView2DVisible(boolean flag);
	
	/**
	 * link the view creator to the view2D
	 * @param viewId view id
	 */
	public void linkToView2D(int viewId);
	
}
