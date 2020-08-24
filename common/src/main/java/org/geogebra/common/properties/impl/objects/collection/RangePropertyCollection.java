package org.geogebra.common.properties.impl.objects.collection;

import org.geogebra.common.properties.RangeProperty;

/**
 * Handles a collection of RangeProperty objects as a single RangeProperty.
 */
public class RangePropertyCollection<T extends RangeProperty<V>, V extends Number & Comparable<V>>
		extends AbstractTypedPropertyCollection<T, V> implements RangeProperty<V> {

	/**
	 * @param properties properties to handle
	 */
	public RangePropertyCollection(T[] properties) {
		super(properties);
	}

	@Override
	public V getStep() {
		return getFirstProperty().getStep();
	}

	@Override
	public V getMin() {
		return getFirstProperty().getMin();
	}

	@Override
	public V getMax() {
		return getFirstProperty().getMax();
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
