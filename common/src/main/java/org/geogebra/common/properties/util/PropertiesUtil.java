package org.geogebra.common.properties.util;

import org.geogebra.common.properties.Property;
import org.geogebra.common.properties.PropertyObserver;
import org.geogebra.common.properties.ValuedProperty;
import org.geogebra.common.properties.factory.PropertiesArray;
import org.geogebra.common.properties.impl.PropertyObserverDecorator;

public abstract class PropertiesUtil {

	private PropertiesUtil() {
	}

	/**
	 * Adds an observer to the array of properties.
	 * @param array array of properties
	 * @param observer property observer
	 * @return a new properties array
	 */
	public static PropertiesArray addObserver(PropertiesArray array, PropertyObserver observer) {
		Property[] originalProperties = array.getProperties();
		Property[] newProperties = new Property[originalProperties.length];
		for (int i = 0; i < newProperties.length; i++) {
			Property property = originalProperties[i];
			newProperties[i] = property;
			if (property instanceof ValuedProperty) {
				ValuedProperty valuedProperty = (ValuedProperty) property;
				newProperties[i] = addObserver(valuedProperty, observer);
			}
		}
		return new PropertiesArray(array.getName(), newProperties);
	}

	/**
	 * Adds an observer to the property
	 * @param property property
	 * @param observer observer
	 * @return an observed property
	 */
	public static <S> ValuedProperty<S> addObserver(ValuedProperty<S> property, PropertyObserver observer) {
		return new PropertyObserverDecorator<S>(property, observer);
	}
}
