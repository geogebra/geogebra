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
import org.geogebra.common.kernel.kernelND.GeoElementND;

/**
 * Restricts the visibility of inequalities.
 * <p>Examples: </p>
 * <ul>
 *     <li>x &gt; 0</li>
 *     <li>y &lt;= 1</li>
 *     <li>x - y &gt; 2</li>
 *     <li>x^2 + 2y^2 &lt; 1</li>
 *     <li>f(x) = x &gt; 5</li>
 *     <li>f: x &gt; 0</li>
 * </ul>
 */
public final class HiddenInequalityVisibilityRestriction implements VisibilityRestriction {
	@Override
	public @Nonnull Effect getEffect(GeoElement geoElement) {
		GeoElementND unwrappedSymbolic = geoElement.unwrapSymbolic();
		return (unwrappedSymbolic != null && unwrappedSymbolic.isInequality()) ? HIDE : IGNORE;
	}
}
