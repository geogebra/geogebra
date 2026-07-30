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
import java.util.Map;

import javax.annotation.Nonnull;

import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.plugin.GeoClass;
import org.geogebra.common.properties.PropertyKey;

/**
 * Applies remembered values of a particular property type to supported construction elements.
 * @param <T> property value type
 */
public abstract class RememberedPropertyHandler<T> {
	private final Map<GeoClass, T> storedValues = new HashMap<>();

	/**
	 * @return handled property key
	 */
	protected abstract @Nonnull PropertyKey propertyKey();

	/**
	 * @param geo construction element
	 * @return whether the remembered property can be applied to the element
	 */
	public abstract boolean supports(@Nonnull GeoElement geo);

	/**
	 * Applies a remembered property value to a construction element.
	 * @param geoElement construction element
	 * @return whether the element is supported and the value was applied successfully
	 */
	public boolean apply(@Nonnull GeoElement geoElement) {
		T value = storedValues.get(geoElement.getGeoClassType());
		return value == null || apply(geoElement, value);
	}

	protected abstract boolean apply(@Nonnull GeoElement geoElement, @Nonnull T value);

	void remember(GeoClass geo, T value) {
		storedValues.put(geo, value);
	}
}
