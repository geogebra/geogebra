package org.geogebra.common.properties.factory;

import java.util.List;

import javax.annotation.Nullable;

import org.geogebra.common.properties.PropertiesRegistry;
import org.geogebra.common.properties.Property;
import org.geogebra.common.properties.PropertyCollection;

final class PropertiesRegistration {

	static PropertiesArray registerProperties(@Nullable PropertiesRegistry registry,
			PropertiesArray properties) {
		if (registry != null) {
			registerProperties(registry, properties.getProperties());
		}
		return properties;
	}

	static Property registerProperty(@Nullable PropertiesRegistry registry, Property property) {
		if (registry != null) {
			if (property instanceof PropertyCollection<?>) {
				PropertyCollection collection = (PropertyCollection) property;
				registerProperties(registry, collection.getProperties());
			}
			registry.register(property);
		}
		return property;
	}

	static Property[] registerProperties(@Nullable PropertiesRegistry registry, Property... properties) {
		if (registry != null) {
			for (Property property : properties) {
				registerProperty(registry, property);
			}
		}
		return properties;
	}

	static List<Property> registerProperties(@Nullable PropertiesRegistry registry,
			List<Property> properties) {
		if (registry != null) {
			for (Property property : properties) {
				registerProperty(registry, property);
			}
		}
		return properties;
	}
}
