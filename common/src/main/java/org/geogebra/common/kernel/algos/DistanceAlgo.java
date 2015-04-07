package org.geogebra.common.kernel.algos;

import org.geogebra.common.kernel.geos.GeoNumeric;

/**
 * @author bencze Common interface for AlgoDistancePoints, AlgoDistancePointLine
 *         and AlgoShortestDistancePointObject
 */
public interface DistanceAlgo {

	/**
	 * @return distance
	 */
	GeoNumeric getDistance();
}
