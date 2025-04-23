package org.geogebra.common.kernel.algos;

import org.geogebra.common.kernel.FixedPathRegionAlgo;
import org.geogebra.common.kernel.kernelND.GeoPointND;

/**
 * Algo for DynamicCoordinates command.
 */
public interface AlgoDynamicCoordinatesInterface extends FixedPathRegionAlgo {

	/**
	 * @return hidden point
	 */
	GeoPointND getParentPoint();

}
