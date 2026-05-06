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

package org.geogebra.common.properties.impl.objects;

import javax.annotation.Nonnull;

import org.geogebra.common.kernel.geos.GeoElement;

/**
 * Property that may change value when the underlying {@link GeoElement} changes.
 */
public interface GeoElementDependentProperty {

	/** Observer for {@code GeoElement} redefinition events */
	interface RedefinitionObserver {
		/** Called when a {@code GeoElement} got redefined to a new {@code GeoElement}). */
		void onGeoElementRedefined(@Nonnull GeoElement originalElement,
				@Nonnull GeoElement newElement);
	}

	/**
	 * Register an observer on the element-dependent property to be notified when
	 * the original element gets redefined and replaced by a new element.
	 * @param observer to be notified when the original element gets redefined.
	 * @apiNote Override this empty default implementation for any properties involving
	 * redefinition.
	 */
	default void addRedefinitionObserver(RedefinitionObserver observer) {
		// dummy body
	}

	/**
	 * @return the geo element which this property is dependent on.
	 */
	GeoElement getGeoElement();
}
