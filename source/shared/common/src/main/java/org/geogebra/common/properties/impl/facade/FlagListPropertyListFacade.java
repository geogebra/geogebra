package org.geogebra.common.properties.impl.facade;

import java.util.List;

import org.geogebra.common.properties.impl.objects.FlagListProperty;

/**
 * Handles a collection of StringProperty objects as a single StringProperty.
 */
public class FlagListPropertyListFacade<T extends FlagListProperty>
		extends AbstractValuedPropertyListFacade<T, List<Boolean>> {

	/**
	 * @param properties properties to handle
	 */
	public FlagListPropertyListFacade(T[] properties) {
		super(List.of(properties));
	}

	/**
	 * @return localized names of the boolean properties
	 */
	public List<String> getFlagNames() {
		return getFirstProperty().getFlagNames();
	}
}

