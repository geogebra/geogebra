package org.geogebra.common.kernel.geos;

import java.util.ArrayList;

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

	/**
	 * @return locus points
	 */
	ArrayList<? extends MyPoint> getPoints();
}
