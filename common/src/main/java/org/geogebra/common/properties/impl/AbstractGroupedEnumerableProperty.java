package org.geogebra.common.properties.impl;

import org.geogebra.common.main.Localization;
import org.geogebra.common.properties.GroupedEnumeratedProperty;

public abstract class AbstractGroupedEnumerableProperty<V>
		extends AbstractNamedEnumeratedProperty<V>
		implements GroupedEnumeratedProperty<V> {

	private int[] groupDividerIndices = new int[0];

	/**
	 * Constructs an AbstractGroupedEnumerableProperty
	 * @param localization the localization used
	 * @param name the name of the property
	 */
	public AbstractGroupedEnumerableProperty(Localization localization,
			String name) {
		super(localization, name);
	}

	// ToDo
	protected void setGroupDividerIndices(int[] groupDividerIndices) {
		this.groupDividerIndices = groupDividerIndices;
	}

	@Override
	public int[] getGroupDividerIndices() {
		return groupDividerIndices;
	}
}
