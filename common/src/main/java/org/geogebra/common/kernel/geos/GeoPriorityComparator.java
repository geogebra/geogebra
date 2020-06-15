package org.geogebra.common.kernel.geos;

public interface GeoPriorityComparator {

	/**
	 * Compare drawing priority of `a` and `b`
	 *
	 * @param checkLastHitType
	 *            whether hits on boundary should be preferred to hits on
	 *            filling
	 * @return negative if `a` has a higher priority, positive, if `b` has a higher
	 *            priority, 0, if their priorities are equal
	 */
	int compare(GeoElement a, GeoElement b, boolean checkLastHitType);
}
