package geogebra.common.kernel.kernelND;



/** Simple interface for elements that have a 2D coord sys
 * @author matthieu
 *
 */
public interface GeoCoordSys2D extends GeoCoordSys, Region3D, GeoDirectionND {

	/** create a 2D view about this coord sys */
	public void createView2D();
	
	/** tells if the view2D is visible 
	 * @return true if the view2D is visible */
	public boolean hasView2DVisible();
	
	/** sets the view 2D visibility 
	 * @param flag visibility*/
	public void setView2DVisible(boolean flag);
	

}
