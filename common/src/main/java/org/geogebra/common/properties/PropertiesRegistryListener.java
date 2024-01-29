package org.geogebra.common.properties;

public interface PropertiesRegistryListener {

	void propertyRegistered(Property property);
	void propertyUnregistered(Property property);
}
