package org.geogebra.common.properties.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.annotation.Nullable;

import org.geogebra.common.ownership.NonOwning;
import org.geogebra.common.properties.PropertiesRegistry;
import org.geogebra.common.properties.PropertiesRegistryListener;
import org.geogebra.common.properties.Property;

public class DefaultPropertiesRegistry implements PropertiesRegistry {

	private Object context;
	private final Map<Key, Property> properties = new HashMap();
	private final List<PropertiesRegistryListener> listeners = new ArrayList<>();

	/**
	 * @implNote Unfortunately, we cannot use `java.lang.ref.WeakReference`, because it's not
	 * supported by <a href="https://www.gwtproject.org/doc/latest/RefJreEmulation.html">GWT's JRE emulation</a>.
	 *
	 * @param listener A listener.
	 */
	@Override
	public void addListener(@NonOwning PropertiesRegistryListener listener) {
		listeners.add(listener);
	}

	@Override
	public void removeListener(PropertiesRegistryListener listener) {
		listeners.remove(listener);
	}

	@Override
	public void setCurrentContext(Object context) {
		this.context = context;
	}

	@Override
	public void register(Property property) {
		register(property, context);
	}

	@Override
	public void register(Property property, Object context) {
		// TODO what if the previously registered property had registered listeners?
		properties.put(new Key(property.getRawName(), context), property);
		for (PropertiesRegistryListener listener : listeners) {
			listener.propertyRegistered(property, context);
		}
	}

	@Override
	public void unregister(Property property) {
		unregister(property, context);
	}

	@Override
	public void unregister(Property property, Object context) {
		properties.remove(new Key(property.getRawName(), context));
		for (PropertiesRegistryListener listener : listeners) {
			listener.propertyUnregistered(property, context);
		}
	}

	@Override
	public Property lookup(String rawName) {
		return lookup(rawName, context);
	}

	@Override
	public Property lookup(String rawName, Object context) {
		return properties.get(new Key(rawName, context));
	}

	@Override
	public void releaseProperties(@Nullable Object context) {
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
		final String rawName;
		final Object context;

		Key(String rawName, Object context) {
			this.rawName = rawName;
			this.context = context;
		}

		@Override
		public int hashCode() {
			return Objects.hash(rawName, context);
		}

		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof Key)) {
				return false;
			}
			Key other = (Key) obj;
			return rawName.equals(other.rawName) && context == other.context;
		}
	}
}
