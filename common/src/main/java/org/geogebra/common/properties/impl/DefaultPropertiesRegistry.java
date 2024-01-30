package org.geogebra.common.properties.impl;

import org.geogebra.common.properties.PropertiesRegistry;
import org.geogebra.common.properties.PropertiesRegistryListener;
import org.geogebra.common.properties.Property;

public class DefaultPropertiesRegistry implements PropertiesRegistry {

	// TODO implement

	@Override
	public void addListener(PropertiesRegistryListener listener) {
	}

	@Override
	public void removeListener(PropertiesRegistryListener listener) {
	}

	@Override
	public void setCurrentContext(Object context) {
	}

	@Override
	public void register(Property property) {
	}

	@Override
	public void register(Property property, Object context) {
	}

	@Override
	public void unregister(Property property) {
	}

	@Override
	public void unregister(Property property, Object context) {
	}

	@Override
	public Property lookup(String rawName) {
		return null;
	}

	@Override
	public Property lookup(String rawName, Object context) {
		return null;
	}
}
