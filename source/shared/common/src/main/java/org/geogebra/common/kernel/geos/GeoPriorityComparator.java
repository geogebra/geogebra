/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

package org.geogebra.common.kernel.geos;

/**
 * Parametrized comparator.
 */
public interface GeoPriorityComparator {

	/**
	 * Compare drawing priority of `a` and `b`
	 * @param a first compared element
	 * @param b second compared element
	 * @param checkLastHitType
	 *            whether hits on boundary should be preferred to hits on
	 *            filling
	 * @return negative if `a` has a higher priority, positive, if `b` has a higher
	 *            priority, 0, if their priorities are equal
	 */
	int compare(GeoElement a, GeoElement b, boolean checkLastHitType);
}
