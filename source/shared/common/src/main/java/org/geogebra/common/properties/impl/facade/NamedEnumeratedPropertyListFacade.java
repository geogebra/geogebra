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

package org.geogebra.common.properties.impl.facade;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.geogebra.common.properties.NamedEnumeratedProperty;

/**
 * Handles a collection of NamedEnumeratedPropertyCollection objects
 * as a single NamedEnumeratedPropertyCollection.
 */
public class NamedEnumeratedPropertyListFacade<T extends NamedEnumeratedProperty<V>, V>
		extends EnumeratedPropertyListFacade<T, V> implements NamedEnumeratedProperty<V> {

	/**
	 * @param properties properties to handle
	 */
	public NamedEnumeratedPropertyListFacade(List<T> properties) {
		super(properties);
	}

	@Override
	public List<V> getValues() {
		List<V> sharedValues = new ArrayList<>(getFirstProperty().getValues());
		for (int propertyIndex = 1; propertyIndex < properties.size(); propertyIndex++) {
			Set<V> propertyValues = new HashSet<>(properties.get(propertyIndex).getValues());
			sharedValues.removeIf(value -> !propertyValues.contains(value));
		}
		return sharedValues;
	}

	@Override
	public String[] getValueNames() {
		List<V> firstValues = getFirstProperty().getValues();
		String[] firstValueNames = getFirstProperty().getValueNames();
		Map<V, String> valueNames = new HashMap<>();
		for (int valueIndex = 0; valueIndex < firstValues.size(); valueIndex++) {
			valueNames.put(firstValues.get(valueIndex), firstValueNames[valueIndex]);
		}
		return getValues().stream().map(valueNames::get).toArray(String[]::new);
	}

	@Override
	public int getIndex() {
		V value = getFirstProperty().getValue();
		if (value == null) {
			return -1;
		}
		for (int propertyIndex = 1; propertyIndex < properties.size(); propertyIndex++) {
			if (!Objects.equals(value, properties.get(propertyIndex).getValue())) {
				return -1;
			}
		}
		return getValues().indexOf(value);
	}

	@Override
	public boolean isAvailable() {
		return super.isAvailable() && !getValues().isEmpty();
	}
}
