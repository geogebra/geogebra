package org.geogebra.common.properties;

public interface PropertiesRegistryListener {

	void propertyRegistered(Property property, Object context);
	void propertyUnregistered(Property property, Object context);
}
