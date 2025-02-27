package org.geogebra.common.properties.impl.collections;

import java.util.List;

import javax.annotation.Nonnull;

import org.geogebra.common.properties.EnumeratedProperty;
import org.geogebra.common.properties.ValueFilter;

abstract public class EnumeratedPropertyCollection<T extends EnumeratedProperty<S>, S>
		extends AbstractValuedPropertyCollection<T, S> implements EnumeratedProperty<S> {

	public EnumeratedPropertyCollection(List<T> properties) {
		super(properties);
	}

	@Nonnull
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
	public void addValueFilter(@Nonnull ValueFilter valueFilter) {
		getFirstProperty().addValueFilter(valueFilter);
	}

	@Override
	public void removeValueFilter(@Nonnull ValueFilter valueFilter) {
		getFirstProperty().removeValueFilter(valueFilter);
	}
}
