package org.geogebra.common.properties.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;

import org.geogebra.common.main.Localization;
import org.geogebra.common.properties.EnumeratedProperty;
import org.geogebra.common.properties.ValueFilter;

/**
 * Base class for enumerated properties. When overriding this class, make sure to call
 * {@link AbstractEnumeratedProperty#setValues(List)} at some point in the constructor.
 * @param <V> value type
 */
public abstract class AbstractEnumeratedProperty<V> extends AbstractValuedProperty<V> implements
		EnumeratedProperty<V> {

	private List<V> values = new ArrayList<>();
	private final List<ValueFilter> valueFilters = new ArrayList<>();

	/**
	 * Constructs an AbstractEnumeratedProperty.
	 * @param localization the localization used
	 * @param name the name of the property
	 */
	public AbstractEnumeratedProperty(Localization localization, String name) {
		super(localization, name);
	}

	protected void setValues(@Nonnull List<V> values) {
		this.values = values;
	}

	@Nonnull
	@Override
	public List<V> getValues() {
		return values.stream().filter(value ->
				valueFilters.stream().allMatch(filter ->
						filter.isValueAllowed(value))).collect(Collectors.toList());
	}

	@Override
	public void addValueFilter(@Nonnull ValueFilter valueFilter) {
		valueFilters.add(valueFilter);
	}

	@Override
	public void removeValueFilter(@Nonnull ValueFilter valueFilter) {
		valueFilters.remove(valueFilter);
	}

	@Override
	public void setIndex(int index) {
		ensureValuesPresent();
		if (index < 0 || index >= getValues().size()) {
			throw new RuntimeException("Index must be between (0, values.length-1)");
		}
		setValue(getValues().get(index));
	}

	@Override
	public int getIndex() {
		ensureValuesPresent();
		return getValues().indexOf(getValue());
	}

	private void ensureValuesPresent() {
		if (values == null) {
			throw new RuntimeException("Set values must be called in the constructor.");
		}
	}
}
