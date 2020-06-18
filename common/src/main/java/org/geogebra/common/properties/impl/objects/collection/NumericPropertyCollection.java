package org.geogebra.common.properties.impl.objects.collection;

import org.geogebra.common.properties.NumericProperty;

/**
 * Handles a collection of NumericProperty objects as a single NumericProperty.
 */
public class NumericPropertyCollection
		<T extends NumericProperty<V>, V extends Number & Comparable<V>>
		extends AbstractTypedPropertyCollection<T, V> implements NumericProperty<V> {

	/**
	 * @param properties properties to handle
	 */
	public NumericPropertyCollection(T[] properties) {
		super(properties);
	}

	@Override
	public V getValue() {
		return getFirstProperty().getValue();
	}

	@Override
	public void setValue(V value) {
		setProperties(value);
	}

	@Override
	void setPropertyValue(T property, V value) {
		property.setValue(value);
	}
}
