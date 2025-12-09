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

import org.geogebra.common.gui.view.probcalculator.ProbabilityCalculatorView;
import org.geogebra.common.main.Localization;
import org.geogebra.common.properties.aliases.BooleanProperty;
import org.geogebra.common.properties.impl.AbstractValuedProperty;

/** The cumulative property of the distribution view. */
public class IsCumulativeProperty extends AbstractValuedProperty<Boolean>
		implements BooleanProperty {

	private final ProbabilityCalculatorView view;

	/**
	 * Create a new cumulative property
	 * @param localization localization
	 * @param view probability calculator view
	 */
	public IsCumulativeProperty(Localization localization, ProbabilityCalculatorView view) {
		super(localization, "Cumulative");
		this.view = view;
	}

	@Override
	public Boolean getValue() {
		return view.isCumulative();
	}

	@Override
	protected void doSetValue(Boolean value) {
		view.setCumulative(value);
	}
}
