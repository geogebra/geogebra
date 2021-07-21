package org.geogebra.common.kernel.geos;

import org.geogebra.common.kernel.MyPoint;

/**
 * Locus interface to avoid typecast warnings
 *
 */
public interface GeoLocusNDInterface extends GeoLocusable {
	/**
	 * @return this locus
	 */
	GeoLocusND<? extends MyPoint> getLocus();
}
