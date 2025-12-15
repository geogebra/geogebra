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

package org.geogebra.common.properties.impl.objects;

import javax.annotation.CheckForNull;

import org.geogebra.common.kernel.CircularDefinitionException;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.arithmetic.ValidExpression;
import org.geogebra.common.kernel.arithmetic.ValueType;
import org.geogebra.common.kernel.geos.GeoBoolean;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.parser.ParseException;
import org.geogebra.common.main.Localization;
import org.geogebra.common.properties.aliases.StringProperty;
import org.geogebra.common.properties.impl.AbstractValuedProperty;
import org.geogebra.common.util.StringUtil;

/**
 * {@code Property} responsible for setting and optional condition to decide about the visibility of its object.
 */
public final class ShowConditionProperty extends AbstractValuedProperty<String>
		implements StringProperty {
	private final GeoElement element;

	/**
	 * Constructs the property for the given element with the provided localization.
	 */
	public ShowConditionProperty(Localization localization, GeoElement element) {
		super(localization, "Condition.ShowObject");
		this.element = element;
	}

	@Override
	public @CheckForNull String validateValue(String value) {
		if (StringUtil.emptyTrim(value)) {
			return null;
		}
		try {
			ValidExpression validExpression = element.getKernel().getParser()
					.parseGeoGebraExpression(value);
			return validExpression.getValueType() == ValueType.BOOLEAN ? null : "";
		} catch (ParseException parseException) {
			return parseException.getLocalizedMessage();
		}
	}

	@Override
	protected void doSetValue(String value) {
		try {
			if (StringUtil.emptyTrim(value)) {
				element.setShowObjectCondition(null);
				return;
			}
			GeoBoolean condition = element.getKernel().getAlgebraProcessor()
					.evaluateToBoolean(value, element.getApp().getErrorHandler());
			element.setShowObjectCondition(condition);
			element.updateRepaint();
			if (condition != null) {
				condition.updateRepaint();
			}
		} catch (CircularDefinitionException ignored) {
			// Ignore exception
		}
	}

	@Override
	public String getValue() {
		if (element.getShowObjectCondition() == null) {
			return "";
		}
		return element.getShowObjectCondition().getLabel(StringTemplate.editTemplate);
	}
}
