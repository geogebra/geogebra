package org.geogebra.common.properties;

/**
 * Enumerable property where groups of values might separated by dividers.
 */
public interface GroupedEnumeratedProperty<V> extends EnumeratedProperty<V> {

	/**
	 * Gets the array of indices where a divider must be inserted.
	 * Please note that indices refer to the unmodified values array.
	 * <p>
	 * For example, with values {"a", "b", "c", "d", "e"}, if this method returns
	 * {1, 3}, it must be understood as the following {"a", |, "b", "c", |, "d", "e"}
	 * @return an array of indices where a divider must be inserted
	 */
	int[] getGroupDividerIndices();
}
