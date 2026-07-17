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

package org.geogebra.common.gui.view.probcalculator;

import static org.geogebra.common.gui.view.probcalculator.ProbabilityCalculatorView.PROB_INTERVAL;
import static org.geogebra.common.gui.view.probcalculator.ProbabilityCalculatorView.PROB_LEFT;
import static org.geogebra.common.gui.view.probcalculator.ProbabilityCalculatorView.PROB_RIGHT;
import static org.geogebra.common.gui.view.probcalculator.ProbabilityCalculatorView.PROB_TWO_TAILED;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.geogebra.common.SuiteSubApp;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.main.settings.ProbabilityCalculatorSettings.Dist;
import org.geogebra.common.properties.impl.distribution.ProbabilityResultValuesProperty;
import org.geogebra.test.BaseAppTestSetup;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ProbabilityResultValuesPropertyTests extends BaseAppTestSetup {

	private ProbabilityCalculatorView probabilityCalculatorView;
	private ProbabilityResultValuesProperty property;

	@BeforeEach
	void setUp() {
		setupApp(SuiteSubApp.PROBABILITY);
		getApp().setRounding("4");
		probabilityCalculatorView = new HeadlessProbabilityCalculatorView(getApp());
		probabilityCalculatorView.setProbabilityCalculator(Dist.NORMAL, new GeoNumberValue[] {
				new GeoNumeric(getKernel().getConstruction(), 0),
				new GeoNumeric(getKernel().getConstruction(), 1)
		}, false);
		property = new ProbabilityResultValuesProperty(getLocalization(), getAlgebraProcessor(),
				probabilityCalculatorView);
	}

	@Test
	void testModeMatchesCalculatorMode() {
		probabilityCalculatorView.setProbabilityMode(PROB_INTERVAL);
		assertEquals(PROB_INTERVAL, property.getMode());

		probabilityCalculatorView.setProbabilityMode(PROB_LEFT);
		assertEquals(PROB_LEFT, property.getMode());
	}

	@Test
	void testChangingIntervalBounds() {
		probabilityCalculatorView.setProbabilityMode(PROB_INTERVAL);

		property.getLowerBoundProperty().setValue("-1.5");
		assertEquals(probabilityCalculatorView.format(-1.5),
				property.getLowerBoundProperty().getValue());
		assertEquals(-1.5, probabilityCalculatorView.getLow(), Kernel.STANDARD_PRECISION);

		property.getUpperBoundProperty().setValue("1.25");
		assertEquals(probabilityCalculatorView.format(1.25),
				property.getUpperBoundProperty().getValue());
		assertEquals(1.25, probabilityCalculatorView.getHigh(), Kernel.STANDARD_PRECISION);
	}

	@Test
	void testChangingProbabilityUpdatesHighInLeftTailMode() {
		probabilityCalculatorView.setProbabilityMode(PROB_LEFT);
		property.getProbabilityResultProperty().setValue("0.2");

		assertEquals("-0.8416", property.getUpperBoundProperty().getValue());
		assertEquals(-0.84162, probabilityCalculatorView.getHigh(), 0.0001);
		assertEquals("0.2", property.getProbabilityResultProperty().getValue());
		assertEquals(0.2, probabilityCalculatorView.getProbability(), 0.0001);
	}

	@Test
	void testChangingProbabilityUpdatesLowInRightTailMode() {
		probabilityCalculatorView.setProbabilityMode(PROB_RIGHT);
		property.getProbabilityResultProperty().setValue("0.2");

		assertEquals("0.8416", property.getLowerBoundProperty().getValue());
		assertEquals(0.84162, probabilityCalculatorView.getLow(), 0.0001);
		assertEquals("0.2", property.getProbabilityResultProperty().getValue());
		assertEquals(0.2, probabilityCalculatorView.getProbability(), 0.0001);
	}

	@Test
	void testChangingTwoTailBoundsUpdatesProbabilityResults() {
		probabilityCalculatorView.setProbabilityMode(PROB_TWO_TAILED);
		property.getLowerBoundProperty().setValue("-1");
		property.getUpperBoundProperty().setValue("2");

		assertEquals("0.1587", property.getLeftProbability());
		assertEquals(0.1587, probabilityCalculatorView.getLeftProbability(), 0.0001);
		assertEquals("0.0228", property.getRightProbability());
		assertEquals(0.0228, probabilityCalculatorView.getRightProbability(), 0.0001);
		assertEquals("0.1814", property.getTotalProbability());
	}
}
