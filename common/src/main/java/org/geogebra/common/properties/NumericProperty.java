package org.geogebra.common.properties;

/**
 * Numeric property used internally.
 * @param <T> numeric property type.
 */
public interface NumericProperty<T extends Number & Comparable<T>> extends Property {

	/**
	 * Returns the minimal possible value for this property inclusive.
	 *
	 * @return minimal value
	 */
    T getMin();

	/**
	 * Returns the maximal possible value for this property inclusive.
	 *
	 * @return maximal value
	 */
	T getMax();

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
