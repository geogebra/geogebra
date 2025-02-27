package org.geogebra.common.properties.impl.collections;

import java.util.List;

import org.geogebra.common.properties.NamedEnumeratedProperty;

/**
 * Handles a collection of NamedEnumeratedPropertyCollection objects
 * as a single NamedEnumeratedPropertyCollection.
 */
public class NamedEnumeratedPropertyCollection<T extends NamedEnumeratedProperty<V>, V>
		extends EnumeratedPropertyCollection<T, V> implements NamedEnumeratedProperty<V> {

	/**
	 * @param properties properties to handle
	 */
	public NamedEnumeratedPropertyCollection(List<T> properties) {
		super(properties);
	}

	@Override
	public String[] getValueNames() {
		return getFirstProperty().getValueNames();
	}

}
