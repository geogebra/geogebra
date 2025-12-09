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

import javax.annotation.CheckForNull;

import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.arithmetic.NumberValue;
import org.geogebra.common.kernel.commands.AlgebraProcessor;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.main.Localization;
import org.geogebra.common.properties.aliases.StringProperty;

/**
 * Abstract implementation of a Double value property that has the maximal range of a Double value:
 */
public abstract class AbstractNumericProperty extends AbstractValuedProperty<String>
		implements StringProperty {

	private final NumericPropertyUtil util;

	/***/
	public AbstractNumericProperty(AlgebraProcessor algebraProcessor, Localization localization,
			String name) {
		super(localization, name);
		this.util = new NumericPropertyUtil(algebraProcessor);
	}

	@Override
	protected void doSetValue(String value) {
		GeoNumberValue numberValue = parseNumberValue(value);
		setNumberValue(numberValue);
	}

	@Override
	public String getValue() {
		NumberValue numberValue = getNumberValue();
		if (numberValue != null) {
			return numberValue.getLabel(StringTemplate.editTemplate);
		}
		return "";
	}

	@Override
	public @CheckForNull String validateValue(String value) {
		if (!util.isNumber(value)) {
			return getLocalization().getError("InvalidInput");
		}
		return null;
	}

	protected abstract void setNumberValue(GeoNumberValue value);

	protected abstract NumberValue getNumberValue();

	/**
	 * Parses a NumberValue from the given string.
	 * @param value string representing a number
	 * @return number value or null if cannot parse string
	 */
	protected GeoNumberValue parseNumberValue(String value) {
		return util.parseInputString(value);
	}
}
