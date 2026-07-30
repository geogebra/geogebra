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

package org.geogebra.common.properties.remembered;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.properties.PropertyKey;
import org.geogebra.common.properties.ValuedProperty;

/**
 * Remembers property values by construction element type and applies them to new elements.
 */
public final class RememberedProperties {
	private final Map<PropertyKey, RememberedPropertyHandler<?>> rememberedPropertyHandlers
			= new HashMap<>();

	/**
	 * Creates a coordinator for the properties remembered during the current session.
	 * @param handlers initial handlers to register.
	 */
	public RememberedProperties(@Nonnull List<RememberedPropertyHandler<?>> handlers) {
		handlers.forEach(this::register);
	}

	/**
	 * Applies all property values remembered for the type of the given element.
	 * @param geo construction element to update
	 * @return whether every remembered property was applied successfully
	 */
	public boolean apply(@Nonnull GeoElement geo) {
		boolean allApplied = true;
		for (RememberedPropertyHandler<?> handler: rememberedPropertyHandlers.values()) {
			if (!handler.apply(geo)) {
				allApplied = false;
			}
		}
		return allApplied;
	}

	private void register(@Nonnull RememberedPropertyHandler<?> handler) {
		PropertyKey key = handler.propertyKey();
		if (rememberedPropertyHandlers.get(key) != null) {
			throw new UnsupportedOperationException("Duplicated registration of handler " + key);
		}
		rememberedPropertyHandlers.put(key, handler);
	}

	@CheckForNull RememberedPropertyHandler<?> getHandler(@Nonnull PropertyKey key) {
		return rememberedPropertyHandlers.get(key);
	}

	/**
	 * @param elements elements whose property is changed
	 * @param property property
	 * @param <T> property type
	 */
	public <T> void observe(List<GeoElement> elements, ValuedProperty<T> property) {
		GeoElement firstGeo = elements.get(0);
		boolean sameType = elements.stream()
				.allMatch(geo -> geo.getGeoClassType() == firstGeo.getGeoClassType());
		RememberedPropertyHandler<T> handler =
				(RememberedPropertyHandler<T>)
						getHandler(property.getKey());
		if (sameType && handler != null && handler.supports(firstGeo)) {
			property.addValueObserver(
					prop -> handler.remember(firstGeo.getGeoClassType(), property.getValue()));
		}
	}

	/**
	 * @return number of handlers
	 */
	public int size() {
		return rememberedPropertyHandlers.size();
	}
}
