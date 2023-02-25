package org.geogebra.common.properties.util;

import org.geogebra.common.properties.Property;
import org.geogebra.common.properties.PropertyObserver;
import org.geogebra.common.properties.ValuedProperty;
import org.geogebra.common.properties.factory.PropertiesArray;

public abstract class PropertiesUtil {

	private PropertiesUtil() {
	}

	/**
	 * Adds an observer to the array of properties.
	 * @param array array of properties
	 * @param observer property observer
	 */
	public static void addObserver(PropertiesArray array, PropertyObserver observer) {
		Property[] originalProperties = array.getProperties();
		addObserver(originalProperties, observer);
	}

	/**
	 * Adds an observer to the array of properties.
	 * @param array array of properties
	 * @param observer property observer
	 * @return a new properties array
	 */
	public static void addObserver(Property[] properties, PropertyObserver observer) {
		for (int i = 0; i < properties.length; i++) {
			Property property = properties[i];
			if (property instanceof ValuedProperty) {
				ValuedProperty valuedProperty = (ValuedProperty) property;
				addObserver(valuedProperty, observer);
			}
		}
	}

	/**
	 * Adds an observer to the property
	 * @param property property
	 * @param observer observer
	 * @return an observed property
	 */
	public static <S> void addObserver(ValuedProperty<S> property,
			PropertyObserver observer) {
		property.addObserver(observer);
	}
}
