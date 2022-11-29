package org.geogebra.common.properties;

/**
 * Enumerable property where values are separated by dividers.
 */
public interface GroupedEnumerableProperty extends EnumerableProperty {

	static String DIVIDER = "<------->";

	/**
	 * Check if the value at index is a divider or not.
	 * @param index index of the item
	 * @return true iff the item should be a divider
	 */
	boolean isDivider(int index);
}
