/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

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

	@Override
	public @Nonnull List<V> getValues() {
		return values.stream().filter(this::filterValues).collect(Collectors.toList());
	}

	protected boolean filterValues(V value) {
		return valueFilters.stream().allMatch(filter ->
				filter.isValueAllowed(value));
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
			throw new IndexOutOfBoundsException("Index " + index + " must be between 0 and "
					+ (values.size() - 1) + ".");
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
			throw new RuntimeException("Set values must be called in the constructor for "
					+ getName());
		}
	}
}
