package org.geogebra.common.geogebra3D.kernel3D.geos;

/**
 * interface for limited quadric and quadric part
 * 
 * @author mathieu
 *
 */
public interface GeoQuadric3DLimitedOrPart {

	/**
	 * 
	 * @return bottom parameter
	 */
	public double getBottomParameter();

	/**
	 * 
	 * @return top parameter
	 */
	public double getTopParameter();
}
