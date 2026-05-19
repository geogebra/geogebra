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
import javax.annotation.Nonnull;

import org.geogebra.common.gui.view.probcalculator.ProbabilityCalculatorView;
import org.geogebra.common.kernel.commands.AlgebraProcessor;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.main.Localization;
import org.geogebra.common.properties.aliases.StringProperty;
import org.geogebra.common.properties.impl.AbstractValuedProperty;
import org.geogebra.common.properties.impl.NumericPropertyUtil;
import org.geogebra.common.properties.impl.collections.AbstractPropertyCollection;

/** {@code Property} responsible for managing probability result in the distribution view. */
public final class ProbabilityResultValuesProperty
		extends AbstractPropertyCollection<StringProperty>
		implements ProbabilityCalculatorViewDependentProperty {
	private final ProbabilityCalculatorView view;
	private final NumericPropertyUtil util;
	private final StringProperty lowerBoundProperty;
	private final StringProperty upperBoundProperty;
	private final StringProperty probabilityResultProperty;

	/**
	 * Constructs the property
	 * @param localization localization used for the property name
	 * @param algebraProcessor parser used for numeric user input
	 * @param view backing probability calculator view
	 */
	public ProbabilityResultValuesProperty(@Nonnull Localization localization,
			@Nonnull AlgebraProcessor algebraProcessor, @Nonnull ProbabilityCalculatorView view) {
		super(localization, "ProbabilityResult");
		this.view = view;
		this.util = new NumericPropertyUtil(algebraProcessor);
		this.lowerBoundProperty = new LowerBoundProperty(localization);
		this.upperBoundProperty = new UpperBoundProperty(localization);
		this.probabilityResultProperty = new ProbabilityResultProperty(localization);
		setProperties(new StringProperty[] {lowerBoundProperty, upperBoundProperty,
				probabilityResultProperty});
	}

	/**
	 * @return the current probability mode of the calculator:
	 * <ul>
	 *     <li>
	 *         {@link ProbabilityCalculatorView#PROB_INTERVAL} for the probability between the lower
	 *         and upper bounds
	 *     </li>
	 *     <li>
	 *         {@link ProbabilityCalculatorView#PROB_LEFT} for the probability below the given bound
	 *     </li>
	 *     <li>
	 *         {@link ProbabilityCalculatorView#PROB_RIGHT} for the probability above the given bound
	 *     </li>
	 *     <li>
	 *         {@link ProbabilityCalculatorView#PROB_TWO_TAILED} for the sum of both tail
	 *         probabilities outside the selected interval
	 *     </li>
	 * </ul>
	 */
	public int getMode() {
		return view.getProbMode();
	}

	/**
	 * @return the {@code Property} responsible for editing the lower bound
	 */
	public @Nonnull StringProperty getLowerBoundProperty() {
		return lowerBoundProperty;
	}

	/**
	 * @return the {@code Property} responsible for editing the upper bound
	 */
	public @Nonnull StringProperty getUpperBoundProperty() {
		return upperBoundProperty;
	}

	/**
	 * @return the {@code Property} responsible for editing the final probability result
	 */
	public @Nonnull StringProperty getProbabilityResultProperty() {
		return probabilityResultProperty;
	}

	/**
	 * @return the probability result of the left tail
	 */
	public @Nonnull String getLeftProbability() {
		return view.getProbabilityText(view.getLeftProbability());
	}

	/**
	 * @return localized text that starts the probability expression
	 */
	public @Nonnull String getProbabilityExpressionPrefix() {
		return getLocalization().getMenu("ProbabilityOf");
	}

	/**
	 * @return localized text that ends the probability expression
	 */
	public @Nonnull String getProbabilityExpressionSuffix() {
		return getLocalization().getMenu("EndProbabilityOf");
	}

	/**
	 * @return the probability result of the right tail
	 */
	public @Nonnull String getRightProbability() {
		return view.getProbabilityText(view.getRightProbability());
	}

	/**
	 * @return the sum of the left and right tail probability results
	 */
	public @Nonnull String getTotalProbability() {
		return view.getProbabilityText(view.getLeftProbability() + view.getRightProbability());
	}

	@Override
	public @Nonnull ProbabilityCalculatorView getProbabilityCalculatorView() {
		return view;
	}

	private final class LowerBoundProperty extends AbstractValuedProperty<String>
			implements StringProperty, ProbabilityCalculatorViewDependentProperty {
		LowerBoundProperty(Localization localization) {
			super(localization, "");
		}

		@Override
		public String getValue() {
			return view.format(view.getLow());
		}

		@Override
		protected void doSetValue(String value) {
			GeoNumberValue numberValue = util.parseInputString(value);
			if (numberValue == null
					|| !view.isValidInterval(numberValue.getDouble(), view.getHigh())) {
				return;
			}
			view.setLow(numberValue);
			view.setXAxisPoints();
			view.updateIntervalProbability();
		}

		@Override
		public @CheckForNull String validateValue(String value) {
			return util.isNumber(value) ? null : "";
		}

		@Override
		public boolean isAvailable() {
			return view.getProbMode() != ProbabilityCalculatorView.PROB_LEFT;
		}

		@Override
		public @Nonnull ProbabilityCalculatorView getProbabilityCalculatorView() {
			return view;
		}

		@Override
		public String getAriaLabel() {
			return view.getProbMode() == ProbabilityCalculatorView.PROB_TWO_TAILED
					? "Left.Upper.Bound" : "Lower.Bound";
		}
	}

	private final class UpperBoundProperty extends AbstractValuedProperty<String>
			implements StringProperty, ProbabilityCalculatorViewDependentProperty {
		UpperBoundProperty(Localization localization) {
			super(localization, "");
		}

		@Override
		public String getValue() {
			return view.format(view.getHigh());
		}

		@Override
		protected void doSetValue(String value) {
			GeoNumberValue numberValue = util.parseInputString(value);
			if (numberValue == null
					|| !view.isValidInterval(view.getLow(), numberValue.getDouble())) {
				return;
			}
			view.setHigh(numberValue);
			view.setXAxisPoints();
			view.updateIntervalProbability();
		}

		@Override
		public @CheckForNull String validateValue(String value) {
			return util.isNumber(value) ? null : "";
		}

		@Override
		public boolean isAvailable() {
			return view.getProbMode() != ProbabilityCalculatorView.PROB_RIGHT;
		}

		@Override
		public @Nonnull ProbabilityCalculatorView getProbabilityCalculatorView() {
			return view;
		}

		@Override
		public String getAriaLabel() {
			return view.getProbMode() == ProbabilityCalculatorView.PROB_TWO_TAILED
					? "Right.Lower.Bound" : "Upper.Bound";
		}
	}

	private final class ProbabilityResultProperty extends AbstractValuedProperty<String>
			implements StringProperty, ProbabilityCalculatorViewDependentProperty {
		ProbabilityResultProperty(Localization localization) {
			super(localization, "");
		}

		@Override
		public String getValue() {
			return view.getProbabilityText(view.getProbability());
		}

		@Override
		protected void doSetValue(String value) {
			GeoNumberValue numberValue = util.parseInputString(value);
			if (numberValue == null) {
				return;
			}
			double probability = numberValue.getDouble();
			if (probability < 0 || probability > 1) {
				return;
			}
			view.handleResultChange(probability);
			view.setXAxisPoints();
			view.updateIntervalProbability();
		}

		@Override
		public @CheckForNull String validateValue(String value) {
			return util.isNumber(value) ? null : "";
		}

		@Override
		public boolean isAvailable() {
			return view.getProbMode() == ProbabilityCalculatorView.PROB_LEFT
					|| view.getProbMode() == ProbabilityCalculatorView.PROB_RIGHT;
		}

		@Override
		public @Nonnull ProbabilityCalculatorView getProbabilityCalculatorView() {
			return view;
		}

		@Override
		public String getAriaLabel() {
			return "Probability";
		}
	}
}
