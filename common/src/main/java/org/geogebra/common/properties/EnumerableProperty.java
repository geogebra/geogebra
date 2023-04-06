package org.geogebra.common.properties;

/**
 * A property that has enumerable string values.
 */
public interface EnumerableProperty extends ValuedProperty<Integer> {

	@Override
	default Integer getValue() {
		return getIndex();
	}

	@Override
	default void setValue(Integer value) {
		setIndex(value);
	}

	/**
	 * Get the possible localized values for this property.
	 * @return possible values of the property
	 */
	String[] getValues();

	/**
	 * Get the index of the current value.
	 * See {@link EnumerableProperty#getValues()}.
	 * @return the index of the current value
	 */
	int getIndex();

	/**
	 * Sets the index of the current value.
	 * See {@link EnumerableProperty#getValues()}.
	 * If the index is not in the range of the values array,
	 * a RuntimeException is thrown.
	 * @param index the index of the current value
	 */
	void setIndex(int index);
}
