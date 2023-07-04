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
