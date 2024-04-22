package org.geogebra.common.properties.impl.collections;

import org.geogebra.common.properties.EnumeratedProperty;

abstract public class EnumeratedPropertyCollection<T extends EnumeratedProperty<S>, S>
		extends AbstractValuedPropertyCollection<T, S> implements EnumeratedProperty<S> {

	public EnumeratedPropertyCollection(T[] properties) {
		super(properties);
	}

	@Override
	public S[] getValues() {
		return getFirstProperty().getValues();
	}

	@Override
	public int getIndex() {
		return getFirstProperty().getIndex();
	}

	@Override
	public void setIndex(int index) {
		setValue(getValues()[index]);
	}
}