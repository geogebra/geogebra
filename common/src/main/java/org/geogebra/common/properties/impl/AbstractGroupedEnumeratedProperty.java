package org.geogebra.common.properties.impl;

import org.geogebra.common.main.Localization;
import org.geogebra.common.properties.GroupedEnumeratedProperty;

/**
 * Base class for enumerated properties that can be grouped. When overriding this class,
 * make sure to call {@link AbstractGroupedEnumeratedProperty#setGroupDividerIndices(int[])}
 * to set the indices of the dividers.
 * @param <V> value type
 */
public abstract class AbstractGroupedEnumeratedProperty<V>
		extends AbstractNamedEnumeratedProperty<V>
		implements GroupedEnumeratedProperty<V> {

	private int[] groupDividerIndices = new int[0];

	/**
	 * Constructs an AbstractGroupedEnumeratedProperty
	 * @param localization the localization used
	 * @param name the name of the property
	 */
	public AbstractGroupedEnumeratedProperty(Localization localization,
			String name) {
		super(localization, name);
	}

	/**
	 * Set the group divider indices. For the format of this array,
	 * see {@link GroupedEnumeratedProperty#getGroupDividerIndices()}.
	 * @param groupDividerIndices group divider indices
	 */
	protected void setGroupDividerIndices(int[] groupDividerIndices) {
		this.groupDividerIndices = groupDividerIndices;
	}

	@Override
	public int[] getGroupDividerIndices() {
		return groupDividerIndices;
	}
}
