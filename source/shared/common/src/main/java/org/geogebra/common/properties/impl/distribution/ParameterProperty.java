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

package org.geogebra.common.properties.impl.distribution;

import javax.annotation.CheckForNull;

import org.geogebra.common.gui.view.probcalculator.ProbabilityCalculatorView;
import org.geogebra.common.kernel.arithmetic.NumberValue;
import org.geogebra.common.kernel.commands.AlgebraProcessor;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.main.Localization;
import org.geogebra.common.properties.impl.AbstractNumericProperty;

/**
 * The parameter property of the distribution view.
 */
public class ParameterProperty extends AbstractNumericProperty {

	private final ProbabilityCalculatorView view;
	private final String localizedName;
	private final int parameterIndex;

	/**
	 * @param localization localization
	 * @param algebraProcessor processor
	 * @param view view
	 * @param parameterIndex index of the parameter
	 * @param localizedName localized name of the parameter
	 */
	public ParameterProperty(Localization localization, AlgebraProcessor algebraProcessor,
			ProbabilityCalculatorView view, int parameterIndex,
			String localizedName) {
		super(algebraProcessor, localization, localizedName);
		this.view = view;
		this.parameterIndex = parameterIndex;
		this.localizedName = localizedName;
	}

	@Override
	public String getName() {
		return localizedName;
	}

	@Override
	protected void setNumberValue(GeoNumberValue value) {
		if (view.getParameters().length > parameterIndex
				&& view.isValidParameterChange(value.getDouble(), parameterIndex)) {
			GeoNumberValue[] parameters = view.getParameters();
			parameters[parameterIndex] = value;
			view.onParameterUpdate();
		}
	}

	@Override
	protected NumberValue getNumberValue() {
		if (view.getParameters().length > parameterIndex) {
			return view.getParameters()[parameterIndex];
		}
		return parseNumberValue("0");
	}

	@Override
	public @CheckForNull String validateValue(String value) {
		if (super.validateValue(value) != null) {
			return "";
		}
		return null;
	}
}
