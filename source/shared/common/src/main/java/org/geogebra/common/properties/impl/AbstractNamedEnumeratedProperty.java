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

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.geogebra.common.main.Localization;
import org.geogebra.common.properties.NamedEnumeratedProperty;

/**
 * Base class for enumerated properties whose values have names associated with them.
 * When overriding this class, make sure to call
 * {@link AbstractNamedEnumeratedProperty#setNamedValues(List)} )}
 * at some point in the constructor.
 */
public abstract class AbstractNamedEnumeratedProperty<V> extends AbstractEnumeratedProperty<V>
		implements NamedEnumeratedProperty<V> {

	private Map<V, String> valueNameTranslationIds;

	/**
	 * Constructs an AbstractNamedEnumeratedProperty
	 * @param localization the localization used
	 * @param name the name of the property
	 */
	public AbstractNamedEnumeratedProperty(Localization localization, String name) {
		super(localization, name);
	}

	/**
	 * Use this method to set the values of the property. These values are
	 * not localized.
	 * @param values a list of value / translation key pairs
	 */
	protected void setNamedValues(List<Map.Entry<V, String>> values) {
		setValues(values.stream().map(Map.Entry::getKey)
				.collect(Collectors.toList()));
		this.valueNameTranslationIds = values.stream()
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
	}

	@Override
	public String[] getValueNames() {
		ensureValueNamesPresent();
		return getValues().stream().map(value -> getLocalization().getMenu(
				valueNameTranslationIds.get(value)
		)).toArray(String[]::new);
	}

	private void ensureValueNamesPresent() {
		if (valueNameTranslationIds == null) {
			throw new RuntimeException("Set values must be called in the constructor for "
					+ getName());
		}
	}

}
