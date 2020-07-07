package org.geogebra.common.properties.impl;

import org.geogebra.common.factories.FormatFactory;
import org.geogebra.common.kernel.arithmetic.NumberValue;
import org.geogebra.common.kernel.commands.AlgebraProcessor;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.main.Localization;
import org.geogebra.common.properties.StringProperty;
import org.geogebra.common.util.NumberFormatAdapter;

import com.himamis.retex.editor.share.util.Unicode;

/**
 * Abstract implementation of a Double value property that has the maximal range of a Double value:
 */
public abstract class AbstractNumericProperty extends AbstractProperty
		implements StringProperty {

	private static final double EPS = 0.00001;

	private final AlgebraProcessor algebraProcessor;
	private final NumberFormatAdapter numberFormatter;

	/***/
	public AbstractNumericProperty(AlgebraProcessor algebraProcessor, Localization localization,
			String name) {
		super(localization, name);
		this.algebraProcessor = algebraProcessor;
		this.numberFormatter = FormatFactory.getPrototype().getNumberFormat(2);
	}

	@Override
	public void setValue(String value) {
		GeoNumberValue numberValue = parseInputString(value);
		setNumberValue(numberValue);
	}

	@Override
	public String getValue() {
		NumberValue numberValue = getNumberValue();
		if (numberValue != null) {
			return getFormatted(numberValue.getDouble());
		}
		return "";
	}

	@Override
	public boolean isValid(String value) {
		NumberValue numberValue = parseInputString(value);
		return numberValue != null && !Double.isNaN(numberValue.getDouble());
	}

	private GeoNumberValue parseInputString(String value) {
		String trimmedValue = value.trim();
		if ("".equals(trimmedValue)) {
			return null;
		}
		return algebraProcessor.evaluateToNumeric(trimmedValue, true);
	}

	private String getFormatted(double distance) {
		if (equals(distance, 0)) {
			return numberFormatter.format(0);
		} else if (equals(distance, Math.PI)) {
			return Unicode.PI_STRING;
		} else if (distance % Math.PI == 0) {
			return numberFormatter.format(distance / Math.PI) + Unicode.PI_STRING;
		} else if (equals(distance, Math.PI / 2)) {
			return Unicode.PI_STRING + "/2";
		} else if (equals(distance, Math.PI / 4)) {
			return Unicode.PI_STRING + "/4";
		} else {
			return numberFormatter.format(distance);
		}
	}

	private boolean equals(double d1, double d2) {
		return Math.abs(d1 - d2) < EPS;
	}

	protected abstract void setNumberValue(GeoNumberValue value);

	protected abstract NumberValue getNumberValue();
}
