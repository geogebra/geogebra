/**
 * This interface is to be used by 2D and 3D elements, such as
 * GeoPolygon, GeoConicND (esp. circle and ellipse),
 * GeoQuadric3DPart (not GeoQuadric3DLimited)
 * 
 */

package geogebra.kernel;

import geogebra.kernel.kernelND.Region3D;

public interface GeoSurfaceFinite extends Region3D{

	public double getArea();

	//SurfaceFinite, if has boundary, can bear 2 different roles: region or boundary.
	public void setRole(boolean isRegion);

	public boolean asBoundary();
}
