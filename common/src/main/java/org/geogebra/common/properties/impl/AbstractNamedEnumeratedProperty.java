package org.geogebra.common.properties.impl;

import org.geogebra.common.main.Localization;
import org.geogebra.common.properties.NamedEnumeratedProperty;

/**
 * Base class for enumerated properties whose values have names associated with them.
 * When overriding this class, make sure to call
 * {@link AbstractNamedEnumeratedProperty#setValueNames(String...)}
 * at some point in the constructor.
 */
public abstract class AbstractNamedEnumeratedProperty<V> extends AbstractEnumeratedProperty<V>
		implements NamedEnumeratedProperty<V> {

	private String[] valueNames;

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
	 * @param values localized values of this property
	 */
	protected void setValueNames(String... values) {
		this.valueNames = values;
	}

	@Override
	public String[] getValueNames() {
		ensureValueNamesPresent();
		Localization localization = getLocalization();
		String[] localizedValues = new String[valueNames.length];
		for (int i = 0; i < valueNames.length; i++) {
			localizedValues[i] = localization.getMenu(valueNames[i]);
		}
		return localizedValues;
	}

	private void ensureValueNamesPresent() {
		if (valueNames == null) {
			throw new RuntimeException("Set values must be called in the constructor.");
		}
	}

}
