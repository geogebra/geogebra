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

package org.geogebra.common.properties.util;

import org.geogebra.common.properties.Property;
import org.geogebra.common.properties.PropertyValueObserver;
import org.geogebra.common.properties.ValuedProperty;
import org.geogebra.common.properties.factory.PropertiesArray;

/** Registers observers to arrays of properties. */
public final class PropertyArrayValueObserving {

	private PropertyArrayValueObserving() {
	}

	/**
	 * Adds an observer to the array of properties.
	 * @param array array of properties
	 * @param observer property observer
	 */
	public static void addObserver(PropertiesArray array, PropertyValueObserver observer) {
		Property[] propertiesArray = array.getProperties();
		addObserver(propertiesArray, observer);
	}

	/**
	 * Adds an observer to the array of properties.
	 * @param properties array of properties
	 * @param observer property observer
	 */
	public static void addObserver(Property[] properties, PropertyValueObserver observer) {
		for (int i = 0; i < properties.length; i++) {
			Property property = properties[i];
			if (property instanceof ValuedProperty) {
				ValuedProperty valuedProperty = (ValuedProperty) property;
				valuedProperty.addValueObserver(observer);
			}
		}
	}
}
