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

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

import org.geogebra.common.ownership.NonOwning;

/**
 * The PropertiesRegistry is a central lookup point for registered properties.
 */
public interface PropertiesRegistry {

	/**
	 * Add a listener.
	 *
	 * @implNote Even though the argument is marked {@link NonOwning}, the PropertiesRegistry
	 * will hold a strong reference onto the listener, because we cannot use weak references
	 * (not supported by <a href="https://www.gwtproject.org/doc/latest/RefJreEmulation.html">GWT's JRE emulation</a>).
	 *
	 * @param listener A listener.
	 */
	void addListener(@NonOwning @Nonnull PropertiesRegistryListener listener);

	/**
	 * Remove a listener.
	 *
	 * @param listener A listener.
	 */
	void removeListener(@Nonnull PropertiesRegistryListener listener);

	/**
	 * Register a property with this registry.
	 * <p>
	 * If a property with the same key has been registered previously,
	 * the old instance is overwritten with the new instance. This should prevent issues when
	 * properties are potentially created and registered several times
	 * (e.g., from UI that can be presented and dismissed multiple times).
	 * </p>
	 * @implNote Unfortunately, we cannot use `java.lang.ref.WeakReference`, because it's not
	 * supported by <a href="https://www.gwtproject.org/doc/latest/RefJreEmulation.html">GWT's JRE emulation</a>,
	 * so the property will be strongly referenced by the registry. See
	 * {@link #releaseProperties()} on how to purge the registry of all properties.
	 *
	 * @param property A property.
	 */
	void register(@Nonnull Property property);

	/**
	 * Remove a property from the registry.
	 *
	 * @param property A property that has previously been registered.
	 */
	void unregister(@Nonnull Property property);

	/**
	 * Look up a property by key.
	 *
	 * @param key A {@link PropertyKey} that uniquely identifies a property type.
	 * @return The property if found, or null if no such property has been registered.
	 */
	@CheckForNull Property lookup(@Nonnull PropertyKey key);

	/**
	 * "Release" (i.e., clear out strong references to) all registered properties.
	 *
	 * This method is a workaround for the unavailability of weak references in GWT's JRE
	 * emulation (see {@link #register(Property)}).
	 */
	void releaseProperties();
}
