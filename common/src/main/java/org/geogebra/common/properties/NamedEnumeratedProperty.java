package org.geogebra.common.properties;

/**
 * A property whose values have names associated with them.
 * @param <V> value type
 */
public interface NamedEnumeratedProperty<V> extends EnumeratedProperty<V> {

	/**
	 * Get the array of localized names for the array of values. This array has the same length
	 * as {@link EnumeratedProperty#getValues()} and the ith element of this array corresponds
	 * to name of the ith element in the values array.
	 * @return localized name array
	 */
	String[] getValueNames();
}
