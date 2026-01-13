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
import java.util.Objects;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

import org.geogebra.common.ownership.NonOwning;
import org.geogebra.common.properties.PropertiesRegistry;
import org.geogebra.common.properties.PropertiesRegistryListener;
import org.geogebra.common.properties.Property;
import org.geogebra.common.properties.PropertyKey;

public class DefaultPropertiesRegistry implements PropertiesRegistry {

	private Object context;
	private final Map<Key, Property> properties = new HashMap<>();
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
	public void setCurrentContext(Object context) {
		this.context = context;
	}

	@Override
	public void register(@Nonnull Property property) {
		register(property, context);
	}

	@Override
	public void register(@Nonnull Property property, Object context) {
		// TODO what if the previously registered property had registered listeners?
		properties.put(new Key(property.getKey(), context), property);
		for (PropertiesRegistryListener listener : listeners) {
			listener.propertyRegistered(property, context);
		}
	}

	@Override
	public void unregister(@Nonnull Property property) {
		unregister(property, context);
	}

	@Override
	public void unregister(@Nonnull Property property, Object context) {
		properties.remove(new Key(property.getKey(), context));
		for (PropertiesRegistryListener listener : listeners) {
			listener.propertyUnregistered(property, context);
		}
	}

	@Override
	public Property lookup(@Nonnull PropertyKey key) {
		return lookup(key, context);
	}

	@Override
	public Property lookup(@Nonnull PropertyKey key, Object context) {
		return properties.get(new Key(key, context));
	}

	@Override
	public void releaseProperties(@CheckForNull Object context) {
		List<Key> keysToRemove = new ArrayList<>();
		for (Key key : properties.keySet()) {
			if (key.context == context) {
				keysToRemove.add(key);
			}
		}
		for (Key key : keysToRemove) {
			properties.remove(key);
		}
	}

	private static final class Key {
		final PropertyKey propertyKey;
		final Object context;

		Key(PropertyKey propertyKey, Object context) {
			this.propertyKey = propertyKey;
			this.context = context;
		}

		@Override
		public int hashCode() {
			return Objects.hash(propertyKey, context);
		}

		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof Key)) {
				return false;
			}
			Key other = (Key) obj;
			return propertyKey.equals(other.propertyKey) && context == other.context;
		}
	}
}
