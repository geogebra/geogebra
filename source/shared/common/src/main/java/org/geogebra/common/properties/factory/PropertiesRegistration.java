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
