package org.geogebra.common.kernel.statistics;

import org.geogebra.common.annotation.MissingDoc;
import org.geogebra.common.kernel.geos.GeoFunction;

/**
 * Algo computing a distribution function.
 */
public interface AlgoDistributionDF {

	@MissingDoc
	public GeoFunction getResult();

}
