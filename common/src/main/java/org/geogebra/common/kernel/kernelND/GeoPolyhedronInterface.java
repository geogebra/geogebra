package org.geogebra.common.kernel.kernelND;

import org.geogebra.common.kernel.matrix.Coords;

/**
 * @author mathieu
 *
 */
public interface GeoPolyhedronInterface extends GeoElementND {
	/**
	 * Sets the point size (and/or visibility)
	 * 
	 * @param size
	 *            new point size
	 */
	public void setPointSizeOrVisibility(int size);

	/**
	 * calc pseudo centroid coords (based on segments average)
	 * 
	 * @param coords
	 *            output coords
	 */
	public void pseudoCentroid(Coords coords);
}
