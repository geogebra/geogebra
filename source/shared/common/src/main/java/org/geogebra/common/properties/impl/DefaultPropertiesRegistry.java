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

package org.geogebra.common.properties.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;

import org.geogebra.common.ownership.NonOwning;
import org.geogebra.common.properties.PropertiesRegistry;
import org.geogebra.common.properties.PropertiesRegistryListener;
import org.geogebra.common.properties.Property;
import org.geogebra.common.properties.PropertyKey;

public class DefaultPropertiesRegistry implements PropertiesRegistry {

	private final Map<PropertyKey, Property> properties = new HashMap<>();
	private final List<PropertiesRegistryListener> listeners = new ArrayList<>();

	/**
	 * @implNote Unfortunately, we cannot use `java.lang.ref.WeakReference`, because it's not
	 * supported by <a href="https://www.gwtproject.org/doc/latest/RefJreEmulation.html">GWT's JRE emulation</a>.
	 *
	 * @param listener A listener.
	 */
	@Override
	public void addListener(@Nonnull @NonOwning PropertiesRegistryListener listener) {
		listeners.add(listener);
	}

	@Override
	public void removeListener(@Nonnull PropertiesRegistryListener listener) {
		listeners.remove(listener);
	}

	@Override
	public void register(@Nonnull Property property) {
		properties.put(property.getKey(), property);
		for (PropertiesRegistryListener listener : listeners) {
			listener.propertyRegistered(property);
		}
	}

	@Override
	public void unregister(@Nonnull Property property) {
		properties.remove(property.getKey());
		for (PropertiesRegistryListener listener : listeners) {
			listener.propertyUnregistered(property);
		}
	}

	@Override
	public Property lookup(@Nonnull PropertyKey key) {
		return properties.get(key);
	}

	@Override
	public void releaseProperties() {
		properties.clear();
	}
}
