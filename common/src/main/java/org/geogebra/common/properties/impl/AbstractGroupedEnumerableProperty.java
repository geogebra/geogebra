package org.geogebra.common.properties.impl;

import org.geogebra.common.main.Localization;
import org.geogebra.common.properties.GroupedEnumerableProperty;

public abstract class AbstractGroupedEnumerableProperty extends AbstractEnumerableProperty
		implements GroupedEnumerableProperty {

	/**
	 * Constructs an AbstractGroupedEnumerableProperty
	 * @param localization the localization used
	 * @param name the name of the property
	 */
	public AbstractGroupedEnumerableProperty(Localization localization,
			String name) {
		super(localization, name);
	}

	@Override
	public boolean isDivider(int index) {
		return getValues()[index].equals(DIVIDER);
	}
}
