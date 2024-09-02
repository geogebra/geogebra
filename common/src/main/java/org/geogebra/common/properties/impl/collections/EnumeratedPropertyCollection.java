package org.geogebra.common.properties.impl.collections;

import java.util.List;

import org.geogebra.common.exam.restrictions.ValueFilter;
import org.geogebra.common.properties.EnumeratedProperty;

abstract public class EnumeratedPropertyCollection<T extends EnumeratedProperty<S>, S>
		extends AbstractValuedPropertyCollection<T, S> implements EnumeratedProperty<S> {

	public EnumeratedPropertyCollection(T[] properties) {
		super(properties);
	}

	@Override
	public List<S> getValues() {
		return getFirstProperty().getValues();
	}

	@Override
	public int getIndex() {
		return getFirstProperty().getIndex();
	}

	@Override
	public void setIndex(int index) {
		setValue(getValues().get(index));
	}

	@Override
	public void addValueFilter(ValueFilter valueFilter) {
		getFirstProperty().addValueFilter(valueFilter);
	}

	@Override
	public void removeValueFilter(ValueFilter valueFilter) {
		getFirstProperty().removeValueFilter(valueFilter);
	}
}
