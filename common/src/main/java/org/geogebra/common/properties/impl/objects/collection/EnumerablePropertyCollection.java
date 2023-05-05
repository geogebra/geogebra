package org.geogebra.common.properties.impl.objects.collection;

import org.geogebra.common.properties.EnumeratedProperty;

/**
 * Handles a collection of EnumerableProperty objects as a single EnumerableProperty.
 */
public class EnumerablePropertyCollection<T extends EnumeratedProperty>
		extends AbstractTypedPropertyCollection<T, Integer>
		implements EnumeratedProperty {

	/**
	 * @param properties properties to handle
	 */
	public EnumerablePropertyCollection(T[] properties) {
		super(properties);
	}

	@Override
	public String[] getValues() {
		return getFirstProperty().getValues();
	}

	@Override
	public int getIndex() {
		return getValue();
	}

	@Override
	public void setIndex(int index) {
		setValue(index);
	}
}
