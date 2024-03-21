package org.geogebra.common.properties.factory;

import java.util.List;

import org.geogebra.common.properties.PropertiesRegistry;
import org.geogebra.common.properties.Property;
import org.geogebra.common.properties.PropertyCollection;

final class PropertiesRegistration {

	static PropertiesArray registerProperties(PropertiesRegistry registry,
			PropertiesArray properties) {
		registerProperties(registry, properties.getProperties());
		return properties;
	}

	static Property registerProperty(PropertiesRegistry registry, Property property) {
		if (property instanceof PropertyCollection<?>) {
			PropertyCollection collection = (PropertyCollection) property;
			registerProperties(registry, collection.getProperties());
		}
		registry.register(property);
		return property;
	}

	static Property[] registerProperties(PropertiesRegistry registry, Property... properties) {
		for (Property property : properties) {
			registerProperty(registry, property);
		}
		return properties;
	}

	static List<Property> registerProperties(PropertiesRegistry registry,
			List<Property> properties) {
		for (Property property : properties) {
			registerProperty(registry, property);
		}
		return properties;
	}
}
