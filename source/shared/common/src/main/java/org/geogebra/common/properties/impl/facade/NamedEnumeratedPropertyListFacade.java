package org.geogebra.common.properties.impl.facade;

import java.util.List;

import org.geogebra.common.properties.NamedEnumeratedProperty;

/**
 * Handles a collection of NamedEnumeratedPropertyCollection objects
 * as a single NamedEnumeratedPropertyCollection.
 */
public class NamedEnumeratedPropertyListFacade<T extends NamedEnumeratedProperty<V>, V>
		extends EnumeratedPropertyListFacade<T, V> implements NamedEnumeratedProperty<V> {

	/**
	 * @param properties properties to handle
	 */
	public NamedEnumeratedPropertyListFacade(List<T> properties) {
		super(properties);
	}

	@Override
	public String[] getValueNames() {
		return getFirstProperty().getValueNames();
	}

}
