package org.geogebra.common.properties.impl.objects.collection;

import org.geogebra.common.properties.EnumerableProperty;

/**
 * Handles a collection of EnumerableProperty objects as a single EnumerableProperty.
 */
public class EnumerablePropertyCollection<T extends EnumerableProperty>
		extends AbstractTypedPropertyCollection<T, Integer>
		implements EnumerableProperty {

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
		return getFirstProperty().getIndex();
	}

	@Override
	public void setIndex(int index) {
		setProperties(index);
	}

	@Override
	void setPropertyValue(T property, Integer value) {
		property.setIndex(value);
	}
}
