package org.geogebra.common.kernel;

import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.geos.GeoElement;

/**
 * Class that is responsible for providing geo parent algo
 */
public interface ParentAlgorithmProviderDelegate {

	/**
	 * 
	 * @param geo
	 *            geo
	 * @return parent algo (or null) for this geo
	 */
	AlgoElement getParentAlgorithm(GeoElement geo);
}
