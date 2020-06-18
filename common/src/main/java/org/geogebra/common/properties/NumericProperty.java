package org.geogebra.common.properties;

/**
 * Numeric property used internally.
 * @param <T> numeric property type.
 */
public interface NumericProperty<T extends Number & Comparable<T>> extends Property {

	/**
	 * Get the value for the property.
	 *
	 * @return value
	 */
    T getValue();

	/**
	 * Sets the value for this property. The value should be between min and max inclusive.
	 * Otherwsie throws a RuntimeException.
	 *
	 * @param value this value should be between min and max inclusive.
	 */
	void setValue(T value);
}
