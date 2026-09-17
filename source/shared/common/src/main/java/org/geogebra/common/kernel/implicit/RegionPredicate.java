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

package org.geogebra.common.kernel.implicit;

import org.geogebra.common.kernel.MyPoint;

/**
 * Strategy interface for region-membership testing.
 * <p>
 * Implementations decide whether a given {@link MyPoint} lies inside the
 * region used by clipping/filling algorithms.
 * </p>
 */
public interface RegionPredicate {

	/**
	 * Returns whether the given point is considered inside the region.
	 *
	 * @param point point in world coordinates to test
	 * @return {@code true} if the point lies inside; {@code false} otherwise
	 */
	boolean isInside(MyPoint point);
}
