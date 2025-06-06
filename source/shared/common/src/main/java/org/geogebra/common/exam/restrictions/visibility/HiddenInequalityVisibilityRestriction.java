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
