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

import javax.annotation.CheckForNull;

import org.geogebra.common.gui.view.probcalculator.ProbabilityCalculatorView;
import org.geogebra.common.main.Localization;
import org.geogebra.common.properties.IconsEnumeratedProperty;
import org.geogebra.common.properties.PropertyResource;
import org.geogebra.common.properties.impl.AbstractEnumeratedProperty;

/**
 * Interval property.
 */
public class IntervalProperty extends AbstractEnumeratedProperty<Integer> implements
		IconsEnumeratedProperty<Integer> {

	private static final PropertyResource[] icons = {
			PropertyResource.ICON_PROBABILITY_MODE_LEFT,
			PropertyResource.ICON_PROBABILITY_MODE_INTERVAL,
			PropertyResource.ICON_PROBABILITY_MODE_TWO_TAILED,
			PropertyResource.ICON_PROBABILITY_MODE_RIGHT
	};

	private final ProbabilityCalculatorView view;

	/**
	 * Create a new interval property.
	 * @param localization localization
	 * @param view view
	 */
	public IntervalProperty(Localization localization, ProbabilityCalculatorView view) {
		super(localization, "Interval");
		this.view = view;
		setValues(List.of(
				ProbabilityCalculatorView.PROB_LEFT,
				ProbabilityCalculatorView.PROB_INTERVAL,
				ProbabilityCalculatorView.PROB_TWO_TAILED,
				ProbabilityCalculatorView.PROB_RIGHT
		));
	}

	@Override
	public PropertyResource[] getValueIcons() {
		return icons;
	}

	@Override
	public @CheckForNull String[] getToolTipLabels() {
		return null;
	}

	@Override
	public Integer getValue() {
		return view.getProbMode();
	}

	@Override
	protected void doSetValue(Integer value) {
		view.setProbabilityMode(value);
	}

	@Override
	public boolean isEnabled() {
		return !view.isCumulative();
	}
}
