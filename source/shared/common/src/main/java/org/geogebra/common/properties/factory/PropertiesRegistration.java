package org.geogebra.common.properties.factory;

import java.util.List;

import javax.annotation.CheckForNull;

import org.geogebra.common.properties.PropertiesRegistry;
import org.geogebra.common.properties.Property;
import org.geogebra.common.properties.PropertyCollection;

final class PropertiesRegistration {

	static PropertiesArray registerProperties(@CheckForNull PropertiesRegistry registry,
			PropertiesArray properties) {
		if (registry != null) {
			registerProperties(registry, properties.getProperties());
		}
		return properties;
	}

	static Property registerProperty(@CheckForNull PropertiesRegistry registry, Property property) {
		if (registry != null) {
			if (property instanceof PropertyCollection<?>) {
				PropertyCollection collection = (PropertyCollection) property;
				registerProperties(registry, collection.getProperties());
			}
			registry.register(property);
		}
		return property;
	}

	static Property[] registerProperties(@CheckForNull PropertiesRegistry registry, Property... properties) {
		if (registry != null) {
			for (Property property : properties) {
				registerProperty(registry, property);
			}
		}
		return properties;
	}

	static List<Property> registerProperties(@CheckForNull PropertiesRegistry registry,
			List<Property> properties) {
		if (registry != null) {
			for (Property property : properties) {
				registerProperty(registry, property);
			}
		}
		return properties;
	}
}
