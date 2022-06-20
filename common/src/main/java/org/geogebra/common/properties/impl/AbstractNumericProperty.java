package org.geogebra.common.properties.impl;

import org.geogebra.common.kernel.arithmetic.NumberValue;
import org.geogebra.common.kernel.commands.AlgebraProcessor;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.main.Localization;
import org.geogebra.common.properties.StringProperty;

/**
 * Abstract implementation of a Double value property that has the maximal range of a Double value:
 */
public abstract class AbstractNumericProperty extends AbstractProperty
		implements StringProperty {

	private final NumericPropertyUtil util;

	/***/
	public AbstractNumericProperty(AlgebraProcessor algebraProcessor, Localization localization,
			String name) {
		super(localization, name);
		this.util = new NumericPropertyUtil(algebraProcessor);
	}

	@Override
	public void setValue(String value) {
		GeoNumberValue numberValue = util.parseInputString(value);
		setNumberValue(numberValue);
	}

	@Override
	public String getValue() {
		NumberValue numberValue = getNumberValue();
		if (numberValue != null) {
			return util.getFormatted(numberValue.getDouble());
		}
		return "";
	}

	@Override
	public boolean isValid(String value) {
		NumberValue numberValue = util.parseInputString(value);
		return numberValue != null && !Double.isNaN(numberValue.getDouble());
	}

	protected abstract void setNumberValue(GeoNumberValue value);

	protected abstract NumberValue getNumberValue();
}
