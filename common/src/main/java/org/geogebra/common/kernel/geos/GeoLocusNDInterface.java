package org.geogebra.common.kernel.geos;

import java.util.ArrayList;

import org.geogebra.common.kernel.MyPoint;

/**
 * Locus interface to avoid typecast warnings
 *
 */
public interface GeoLocusNDInterface {
	/**
	 * @return this locus
	 */
	GeoLocusND<? extends MyPoint> getLocus();

	ArrayList<? extends MyPoint> getPoints();
}
