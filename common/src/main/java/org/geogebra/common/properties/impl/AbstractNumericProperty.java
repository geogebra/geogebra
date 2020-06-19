package org.geogebra.common.properties.impl;

import org.geogebra.common.main.Localization;
import org.geogebra.common.properties.NumericProperty;

/**
 * Abstract implementation of a Double value property that has the maximal range of a Double value:
 */
public abstract class AbstractNumericProperty extends AbstractProperty
		implements NumericProperty<Double> {

	public AbstractNumericProperty(Localization localization, String name) {
		super(localization, name);
	}
}
