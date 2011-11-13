package geogebra.kernel.kernelND;


/** Simple interface for elements that have a 2D coord sys
 * @author matthieu
 *
 */
public interface GeoCoordSys2D extends GeoCoordSys, Region3D, GeoDirectionND {

	/** create a 2D view about this coord sys */
	void createView2D();
	

}
