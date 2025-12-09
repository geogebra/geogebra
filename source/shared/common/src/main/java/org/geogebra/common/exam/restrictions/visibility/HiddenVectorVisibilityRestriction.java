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

package org.geogebra.common.exam.restrictions.visibility;

import static org.geogebra.common.exam.restrictions.visibility.VisibilityRestriction.Effect.HIDE;
import static org.geogebra.common.exam.restrictions.visibility.VisibilityRestriction.Effect.IGNORE;

import javax.annotation.Nonnull;

import org.geogebra.common.kernel.geos.GeoElement;

/**
 * Restricts the visibility of vectors.
 * <p>Examples: </p>
 * <ul>
 *     <li>a = (1, 2)</li>
 *     <li>b = a + 0</li>
 * </ul>
 */
public final class HiddenVectorVisibilityRestriction implements VisibilityRestriction {
	@Override
	public @Nonnull Effect getEffect(GeoElement geoElement) {
		return geoElement.isGeoVector() ? HIDE : IGNORE;
	}
}
