package org.geogebra.common.properties;

/**
 * Enumerated property where groups of values might be separated by dividers.
 */
public interface GroupedEnumeratedProperty<V> extends EnumeratedProperty<V> {

	/**
	 * Gets the array of indices where a divider must be inserted.
	 * Please note that indices refer to the unmodified values array.
	 * The array is sorted in ascending order.
	 * <p>
	 * For example, with values {"a", "b", "c", "d", "e"}, if this method returns
	 * {1, 3}, it must be understood as the following {"a", |, "b", "c", |, "d", "e"}
	 * @return a sorted array of indices where a divider must be inserted
	 */
	int[] getGroupDividerIndices();
}
