
package org.geogebra.common.kernel.geos;

import org.geogebra.common.kernel.kernelND.Region3D;

/**
 * This interface is to be used by 2D and 3D elements, such as
 * GeoPolygon, GeoConicND (esp. circle and ellipse),
 * GeoQuadric3DPart (not GeoQuadric3DLimited)
 * 
 */
public interface GeoSurfaceFinite extends Region3D{
	/**
	 * 
	 * @return area of this surface
	 */
	public double getArea();

	/**
	 * SurfaceFinite, if has boundary, can bear 2 different roles: region or boundary.
	 * @param isRegion true for region role
	 */
	public void setRole(boolean isRegion);
	/**
	 * 
	 * @return true if the role is boundary
	 */
	public boolean asBoundary();
}
