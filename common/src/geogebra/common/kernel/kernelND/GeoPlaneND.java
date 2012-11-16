package geogebra.common.kernel.kernelND;

import geogebra.common.kernel.Region;
import geogebra.common.kernel.Matrix.Coords;

/**
 * simple plane interface for all geos that can be considered as a plane (3D plane, polygons, ...)
 * @author mathieu
 *
 */
public interface GeoPlaneND extends Region, GeoCoordSys2D{
	
	/**
	 * sets the fading for the "ends" of the plane 
	 * @param fading fading
	 */
	public void setFading(float fading);
	
	/**
	 * 
	 * @return the fading for the "ends" of the plane 
	 */
	public float getFading();
	
	/**
	 * create a 2D view of this plane
	 */
	public void createView2D();
	
	
	/**
	 * @param coords point
	 * @return coords of normal projection of given point
	 */
	public Coords[] getNormalProjection(Coords coords);

}
