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

package org.geogebra.common.properties;

import java.util.Objects;

import javax.annotation.Nonnull;

/**
 * A type-safe, unique identifier for property types.
 */
public final class PropertyKey {

	private final String key;

	/**
	 * Generate a property key from a property type (class).
	 * @param cls A class implementing the {@link Property} interface.
	 * @return A key uniquely identifying the property type.
	 */
	public static @Nonnull PropertyKey of(@Nonnull Class<? extends Property> cls) {
		return new PropertyKey(cls.getCanonicalName());
	}

	/**
	 * Generate a property key from a property (instance).
	 * @param property A property instance.
	 * @return A key uniquely identifying the property's type.
	 */
	public static @Nonnull PropertyKey of(@Nonnull Property property) {
		return PropertyKey.of(property.getClass());
	}

	/**
	 * Note: keep this private - nobody except the factory methods above should create
	 * instances of this type.
	 */
	private PropertyKey(String key) {
		this.key = key;
	}

	@Override
	public boolean equals(Object object) {
		if (!(object instanceof PropertyKey other)) {
			return false;
		}
		return Objects.equals(key, other.key);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(key);
	}
}
