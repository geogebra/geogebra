package org.geogebra.common.properties.impl.facade;

import java.util.List;

import javax.annotation.Nonnull;

import org.geogebra.common.properties.EnumeratedProperty;
import org.geogebra.common.properties.ValueFilter;

abstract public class EnumeratedPropertyListFacade<T extends EnumeratedProperty<S>, S>
		extends AbstractValuedPropertyListFacade<T, S> implements EnumeratedProperty<S> {

	public EnumeratedPropertyListFacade(List<T> properties) {
		super(properties);
	}

	@Override
	public @Nonnull List<S> getValues() {
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
