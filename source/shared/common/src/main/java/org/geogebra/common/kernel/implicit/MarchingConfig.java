package org.geogebra.common.kernel.implicit;

import org.geogebra.common.kernel.MyPoint;

/**
 * Marching square/rectangle configuration.
 */
public interface MarchingConfig {

	/**
	 * @return int comprised of bits describing function signs in the corners
	 */
	int flag();

	/**
	 * @param r rectangle
	 * @return endpoints of curve segments in given rectangle
	 */
	MyPoint[] getPoints(MarchingRect r);

	/**
	 * @return whether the configuration is valid
	 */
	boolean isValid();

	/**
	 * @return whether the configuration is invalid
	 */
	boolean isInvalid();

	/**
	 * @return whether the configuration is empty
	 */
	boolean isEmpty();

}
