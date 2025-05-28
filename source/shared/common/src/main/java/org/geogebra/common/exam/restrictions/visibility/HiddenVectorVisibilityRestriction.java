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
