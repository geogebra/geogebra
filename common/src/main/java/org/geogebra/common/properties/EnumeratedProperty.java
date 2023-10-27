package org.geogebra.common.properties;

/**
 * A property whose value is one of an array of predefined values,
 * similarly to an enumeration, where the value is one a finite set of possible values.
 * {@link ValuedProperty#setValue(Object)} will now throw a RuntimeException
 * if the value is not member of the {@link EnumeratedProperty#getValues()} ()}
 * array.
 */
public interface EnumeratedProperty<V> extends ValuedProperty<V> {

	/**
	 * Gets the list of possible values for this property.
	 * When calling {@link EnumeratedProperty#setValue(Object)}
	 * the value must be one of these values, otherwise it throws a RuntimeException.
	 * @return predefined values
	 */
	V[] getValues();

	/**
	 * Get the index of the value of this property in the array
	 * {@link EnumeratedProperty#getValues()}. Returns -1 if the value is null.
	 * @return index of value in the array or -1.
	 */
	int getIndex();

	/**
	 * Set the value of this property to the index-th element in the array
	 * {@link EnumeratedProperty#getValues()}. Might throw {@link ArrayIndexOutOfBoundsException}
	 * if the index is invalid with respect to the values array.
	 * @param index index of the value
	 */
	void setIndex(int index);
}
