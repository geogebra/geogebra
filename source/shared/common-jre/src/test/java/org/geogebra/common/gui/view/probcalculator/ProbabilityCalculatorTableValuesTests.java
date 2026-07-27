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
import static org.geogebra.common.gui.view.probcalculator.ProbabilityCalculatorView.PROB_TWO_TAILED;
import static org.geogebra.common.main.settings.ProbabilityCalculatorSettings.Dist.BINOMIAL;
import static org.geogebra.common.main.settings.ProbabilityCalculatorSettings.Dist.HYPERGEOMETRIC;
import static org.geogebra.common.main.settings.ProbabilityCalculatorSettings.Dist.NORMAL;
import static org.geogebra.common.main.settings.ProbabilityCalculatorSettings.Dist.PASCAL;
import static org.geogebra.common.main.settings.ProbabilityCalculatorSettings.Dist.POISSON;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.geogebra.common.SuiteSubApp;
import org.geogebra.common.gui.view.probcalculator.ProbabilityCalculatorTableValues.Row;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.main.settings.ProbabilityCalculatorSettings.Dist;
import org.geogebra.test.BaseAppTestSetup;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ProbabilityCalculatorTableValuesTests extends BaseAppTestSetup {
	private ProbabilityCalculatorView probabilityCalculator;

	@BeforeEach
	public void setUp() {
		setupApp(SuiteSubApp.PROBABILITY);
		probabilityCalculator = new HeadlessProbabilityCalculatorView(getApp());
	}

	@Test
	public void testContinuousDistributionHasNoTableValues() {
		probabilityCalculator.setProbabilityCalculator(NORMAL, null, false);
		assertNull(ProbabilityCalculatorTableValues.from(probabilityCalculator));
	}

	@Test
	public void testDiscreteDistributionsHaveTableValues() {
		setProbabilityCalculator(BINOMIAL, false, 2, 0.5);
		assertNotNull(ProbabilityCalculatorTableValues.from(probabilityCalculator));

		setProbabilityCalculator(PASCAL, false, 1, 0.5);
		assertNotNull(ProbabilityCalculatorTableValues.from(probabilityCalculator));
	}

	@Test
	public void testHeaderReflectsCumulativeState() {
		setProbabilityCalculator(BINOMIAL, false, 2, 0.5);
		assertEquals("P(X = k)", ProbabilityCalculatorTableValues.from(probabilityCalculator)
				.header().probability());

		setProbabilityCalculator(BINOMIAL, true, 2, 0.5);
		assertEquals("P(X ≤ k)", ProbabilityCalculatorTableValues.from(probabilityCalculator)
				.header().probability());
	}

	@Test
	public void testBinomialFormattedProbabilities() {
		setProbabilityCalculator(BINOMIAL, false, 2, 0.5);
		assertEquals(List.of(
				new Row("0", "0.25", true),
				new Row("1", "0.5", true),
				new Row("2", "0.25", false)
		), ProbabilityCalculatorTableValues.from(probabilityCalculator).rows());
	}

	@Test
	public void testBinomialFormattedCumulativeProbabilities() {
		setProbabilityCalculator(BINOMIAL, true, 2, 0.5);
		assertEquals(List.of(
				new Row("0", "0.25", true),
				new Row("1", "0.75", true),
				new Row("2", "1", false)
		), ProbabilityCalculatorTableValues.from(probabilityCalculator).rows());
	}

	@Test
	public void testPascalDistributionInverseCumulativeProbabilities() {
		setProbabilityCalculator(PASCAL, false, 1, 0.9);
		assertEquals(List.of(
				new Row("0", "0.9", true),
				new Row("1", "0.09", true),
				new Row("2", "0.009", false),
				new Row("3", "0.0009", false),
				new Row("4", "0.0001", false),
				new Row("5", "0", false)
		), ProbabilityCalculatorTableValues.from(probabilityCalculator).rows());
	}

	@Test
	public void testPoissonDistributionInverseCumulativeProbabilities() {
		setProbabilityCalculator(POISSON, false, 0.1);
		assertEquals(List.of(
				new Row("0", "0.9048", true),
				new Row("1", "0.0905", true),
				new Row("2", "0.0045", false),
				new Row("3", "0.0002", false),
				new Row("4", "0", false)
		), ProbabilityCalculatorTableValues.from(probabilityCalculator).rows());
	}

	@Test
	public void testHyperGeometricRowsStartAtMinimumValidValue() {
		setProbabilityCalculator(HYPERGEOMETRIC, false, 10, 8, 5);
		assertEquals(List.of(
				new Row("3", "0.2222", false),
				new Row("4", "0.5556", false),
				new Row("5", "0.2222", false)
		), ProbabilityCalculatorTableValues.from(probabilityCalculator).rows());
	}

	@Test
	public void testInvalidBinomialParameters() {
		setProbabilityCalculator(BINOMIAL, false, -1, 0.5);
		ProbabilityCalculatorTableValues tableValues = assertDoesNotThrow(
				() -> ProbabilityCalculatorTableValues.from(probabilityCalculator));
		assertTrue(tableValues.rows().isEmpty());

		setProbabilityCalculator(BINOMIAL, false, 1, -1);
		tableValues = assertDoesNotThrow(
				() -> ProbabilityCalculatorTableValues.from(probabilityCalculator));
		assertTrue(tableValues.rows().isEmpty());
	}

	@Test
	public void testInvalidPascalParameters() {
		setProbabilityCalculator(PASCAL, false, 0, 0.5);
		ProbabilityCalculatorTableValues tableValues = assertDoesNotThrow(
				() -> ProbabilityCalculatorTableValues.from(probabilityCalculator));
		assertTrue(tableValues.rows().isEmpty());

		setProbabilityCalculator(PASCAL, false, 1, -1);
		tableValues = assertDoesNotThrow(
				() -> ProbabilityCalculatorTableValues.from(probabilityCalculator));
		assertTrue(tableValues.rows().isEmpty());
	}

	@Test
	public void testInvalidPoissonParameters() {
		setProbabilityCalculator(POISSON, false, 0);
		ProbabilityCalculatorTableValues tableValues = assertDoesNotThrow(
				() -> ProbabilityCalculatorTableValues.from(probabilityCalculator));
		assertTrue(tableValues.rows().isEmpty());
	}

	@Test
	public void testInvalidHyperGeometricParameters() {
		setProbabilityCalculator(HYPERGEOMETRIC, false, 1, 2, 1);
		ProbabilityCalculatorTableValues tableValues = assertDoesNotThrow(
				() -> ProbabilityCalculatorTableValues.from(probabilityCalculator));
		assertTrue(tableValues.rows().isEmpty());

		setProbabilityCalculator(HYPERGEOMETRIC, false, 1, 1, 2);
		tableValues = assertDoesNotThrow(
				() -> ProbabilityCalculatorTableValues.from(probabilityCalculator));
		assertTrue(tableValues.rows().isEmpty());
	}

	@Test
	public void testIntervalModeHighlightsRowsBetweenLowAndHigh() {
		setProbabilityCalculator(BINOMIAL, false, 2, 0.5);
		probabilityCalculator.setProbabilityMode(PROB_INTERVAL);
		probabilityCalculator.setLow(0.5);
		probabilityCalculator.setHigh(1.5);

		assertEquals(List.of(
				new Row("0", "0.25", false),
				new Row("1", "0.5", true),
				new Row("2", "0.25", false)
		), ProbabilityCalculatorTableValues.from(probabilityCalculator).rows());
	}

	@Test
	public void testTwoTailedModeHighlightsRowsOutsideLowAndHigh() {
		setProbabilityCalculator(BINOMIAL, false, 2, 0.5);
		probabilityCalculator.setProbabilityMode(PROB_TWO_TAILED);
		probabilityCalculator.setLow(0.5);
		probabilityCalculator.setHigh(1.5);

		assertEquals(List.of(
				new Row("0", "0.25", true),
				new Row("1", "0.5", false),
				new Row("2", "0.25", true)
		), ProbabilityCalculatorTableValues.from(probabilityCalculator).rows());
	}

	private void setProbabilityCalculator(Dist distribution, boolean cumulative,
			double... parameterValues) {
		GeoNumberValue[] parameters = new GeoNumberValue[parameterValues.length];
		for (int i = 0; i < parameterValues.length; i++) {
			parameters[i] = new GeoNumeric(getKernel().getConstruction(), parameterValues[i]);
		}
		probabilityCalculator.setProbabilityCalculator(distribution, parameters, cumulative);
	}
}
