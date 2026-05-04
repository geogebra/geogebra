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

import java.util.List;
import java.util.Set;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

import org.geogebra.common.gui.view.probcalculator.ProbabilityCalculatorView;
import org.geogebra.common.gui.view.probcalculator.ProbabilityManager;
import org.geogebra.common.kernel.arithmetic.NumberValue;
import org.geogebra.common.kernel.commands.AlgebraProcessor;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.main.Localization;
import org.geogebra.common.main.settings.ProbabilityCalculatorSettings.Dist;
import org.geogebra.common.properties.impl.AbstractNumericProperty;
import org.geogebra.editor.share.util.Unicode;

/** {@code Property} responsible for managing the values of the probability parameters. */
public final class DistributionParameterProperty extends AbstractNumericProperty
		implements ProbabilityCalculatorViewDependentProperty {
	private static final Set<String> CUSTOM_PARAMETER_TRANSLATION_KEYS = Set.of("Median", "Scale",
			"Distribution.Scale", "Distribution.Shape", "Distribution.Population",
			"Hypergeometric.population", "Sample", "Hypergeometric.sample");

	private final ProbabilityCalculatorView probabilityCalculatorView;
	private final int parameterIndex;

	/**
	 * Constructs the property.
	 * @param algebraProcessor algebra processor used to parse and validate numeric input
	 * @param probabilityCalculatorView probability calculator view that owns the parameter
	 * @param localization localization used for the property label
	 * @param parameterIndex zero-based index of the distribution parameter
	 */
	public DistributionParameterProperty(@Nonnull AlgebraProcessor algebraProcessor,
			@Nonnull ProbabilityCalculatorView probabilityCalculatorView,
			@Nonnull Localization localization, int parameterIndex) {
		super(algebraProcessor, localization, "");
		this.probabilityCalculatorView = probabilityCalculatorView;
		this.parameterIndex = parameterIndex;
	}

	@Override
	public String getName() {
		String parameterTranslationKey = getParameterTranslationKey(
				probabilityCalculatorView.getSelectedDist(), parameterIndex);
		if (parameterTranslationKey == null) {
			return "";
		}

		String parameterName = getLocalization().getMenu(parameterTranslationKey);
		return CUSTOM_PARAMETER_TRANSLATION_KEYS.contains(parameterTranslationKey) ? parameterName
				: getLocalization().getPlainDefault("ParameterA", "Parameter $0", parameterName);
	}

	private static String getParameterTranslationKey(Dist dist, int parameterIndex) {
		List<String> parameterTranslationKeys = switch (dist) {
			case NORMAL, LOGNORMAL -> List.of("Mean.short", "StandardDeviation.short");
			case STUDENT, CHISQUARE -> List.of("DegreesOfFreedom.short");
			case F -> List.of("DegreesOfFreedom1.short", "DegreesOfFreedom2.short");
			case EXPONENTIAL -> List.of(Unicode.lambda + "");
			case CAUCHY -> List.of("Median", "Scale");
			case WEIBULL -> List.of("Distribution.Shape", "Scale");
			case LOGISTIC -> List.of("Mean.short", "Scale");
			case BETA, GAMMA -> List.of(Unicode.alpha + "", Unicode.beta + "");
			case BINOMIAL, PASCAL -> List.of("Binomial.number", "Binomial.probability");
			case POISSON -> List.of("Mean.short");
			case HYPERGEOMETRIC -> List.of("Distribution.Population", "Hypergeometric.number",
					"Sample");
		};
		return parameterIndex < parameterTranslationKeys.size()
				? parameterTranslationKeys.get(parameterIndex) : null;
	}

	@Override
	protected void setNumberValue(GeoNumberValue value) {
		if (parameterIndex >= probabilityCalculatorView.getParameters().length) {
			return;
		}
		if (!probabilityCalculatorView.isValidParameterChange(value.getDouble(), parameterIndex)) {
			return;
		}
		GeoNumberValue[] parameters = probabilityCalculatorView.getParameters();
		parameters[parameterIndex] = value;
		probabilityCalculatorView.onParameterUpdate();
	}

	@Override
	protected NumberValue getNumberValue() {
		if (probabilityCalculatorView.getParameters().length > parameterIndex) {
			return probabilityCalculatorView.getParameters()[parameterIndex];
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

	@Override
	public boolean isAvailable() {
		return parameterIndex
				< ProbabilityManager.getParamCount(probabilityCalculatorView.getSelectedDist());
	}

	@Override
	public @Nonnull ProbabilityCalculatorView getProbabilityCalculatorView() {
		return probabilityCalculatorView;
	}
}
