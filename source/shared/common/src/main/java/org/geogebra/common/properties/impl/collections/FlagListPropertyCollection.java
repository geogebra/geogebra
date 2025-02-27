package org.geogebra.common.properties.impl.collections;

import java.util.List;

import org.geogebra.common.properties.impl.objects.FlagListProperty;

/**
 * Handles a collection of StringProperty objects as a single StringProperty.
 */
public class FlagListPropertyCollection<T extends FlagListProperty>
		extends AbstractValuedPropertyCollection<T, List<Boolean>> {

	/**
	 * @param properties properties to handle
	 */
	public FlagListPropertyCollection(T[] properties) {
		super(List.of(properties));
	}

	/**
	 * @return localized names of the boolean properties
	 */
	public List<String> getFlagNames() {
		return getFirstProperty().getFlagNames();
	}
}

