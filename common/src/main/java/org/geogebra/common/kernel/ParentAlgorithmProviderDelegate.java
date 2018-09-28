package org.geogebra.common.kernel;

import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.geos.GeoElement;

/**
 * Class that is responsible for providing geo parent algo
 */
public interface ParentAlgorithmProviderDelegate {
    AlgoElement getParentAlgorithm(GeoElement geo);
}
