package org.geogebra.common.properties.impl.facade;

import java.util.List;

import org.geogebra.common.properties.aliases.BooleanProperty;

/**
 * Handles a collection of BooleanProperty objects as a single BooleanProperty.
 */
public class BooleanPropertyListFacade<T extends BooleanProperty>
		extends AbstractValuedPropertyListFacade<T, Boolean> implements BooleanProperty {

	/**
	 * @param properties properties to handle
	 */
	public BooleanPropertyListFacade(List<T> properties) {
		super(properties);
	}

}
