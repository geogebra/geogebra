package org.geogebra.common.exam.restrictions.visibility;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;

import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoList;

/**
 * Restrictions on geo elements regarding the visibility in graphical output.
 */
public interface VisibilityRestriction {
	/**
	 * The effect of the restriction on a geo element's visibility.
	 */
	enum Effect {
		/**
		 * The geo element should be hidden (no graphical output).
		 */
		HIDE,
		/**
		 * The restriction has no effect on the visibility of the given geo element.
		 */
		IGNORE,
		/**
		 * The geo element is always allowed to be visible regardless of other restrictions
		 * (overriding any {@link Effect#HIDE} effects in conflict scenarios).
		 * It serves as an exception for overlapping restriction conditions (e.g. restricted
		 * <em>equation</em> visibility, with the exception of <em>linear equation</em>s).
		 */
		ALLOW,
	}

	/**
	 * @param geoElement The geo element to evaluate.
	 * @return The effect of the restriction for the given geo element.
	 */
	@Nonnull
	Effect getEffect(GeoElement geoElement);

	/**
	 * Determine whether a {@code GeoElement}'s visibility is restricted for a set of restrictions.
	 * <p>
	 * If the visibility is restricted, it means that the element should not be shown in the
	 * Euclidean view, it shouldn't have a show object property in its settings, and the
	 * visibility toggle button ("marble") should be disabled in the Algebra view.
	 * <p>
	 * An element's visibility is not restricted (i.e., allowed to be visible) if either:
	 * <ol>
	 *     <li>
	 *         At least one restriction is {@link Effect#ALLOW} (overrides all others)
	 *     </li>
	 *     <li>
	 *         All the restrictions are {@link Effect#IGNORE} (none of them are {@link Effect#HIDE}
	 *     </li>
	 * </ol>
	 * If there is at least one {@link Effect#HIDE} restriction, without any {@link Effect#ALLOW},
	 * then the geo element's visibility is restricted (i.e., not allowed to be visible).
	 *
	 * @param geoElement The geo element to check.
	 * @param visibilityRestrictions The set of visibility restrictions to apply.
	 * @return {@code true} if the element's visibility is restricted, {@code false} otherwise.
	 */
	static boolean isVisibilityRestricted(GeoElement geoElement,
			Set<VisibilityRestriction> visibilityRestrictions) {
		List<Effect> effects = visibilityRestrictions.stream()
				.map(restriction -> restriction.getEffect(geoElement))
				.collect(Collectors.toList());
		boolean isAllowed = effects.stream().anyMatch(effect -> effect == Effect.ALLOW)
				|| effects.stream().noneMatch(effect -> effect == Effect.HIDE);
		if (isAllowed && geoElement.isGeoList()) {
			return ((GeoList) geoElement).elements()
					.anyMatch(el -> isVisibilityRestricted(el, visibilityRestrictions));
		}
		return !isAllowed;
	}
}
