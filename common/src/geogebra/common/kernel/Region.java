package geogebra.common.kernel;

import geogebra.common.kernel.kernelND.GeoElementND;
import geogebra.common.kernel.kernelND.GeoPointND;



/**
 * @author Mathieu Blossier
 * 

 */


public interface Region extends GeoElementND {

	
	
	/**
	 * Sets coords of P  when
	 * the coords of P have changed.
	 * Afterwards P lies in this region.
	 * @param P point
	 * 
	 */
	public void pointChangedForRegion(GeoPointND P);
	
	
	/**
	 * Sets coords of P 
	 * when this region has changed.
	 * Afterwards P lies in this region.
	 * @param P point
	 * 
	 *
	 */
	public void regionChanged(GeoPointND P);
	
	
	
	/**
	 * Returns true if the given point lies inside this Region.
	 * @param P point
	 * @return true if the given point lies inside this Region.
	 */	
	public boolean isInRegion(GeoPointND P);
	
	/** says if the point (x0,y0) is in the region
	 * @param x0 x-coord of the point
	 * @param y0 y-coord of the point
	 * @return true if the point (x0,y0) is in the region
	 */
	public boolean isInRegion(double x0, double y0);
	
}
