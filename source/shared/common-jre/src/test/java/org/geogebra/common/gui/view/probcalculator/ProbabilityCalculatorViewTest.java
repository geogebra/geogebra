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
import static org.geogebra.common.main.settings.ProbabilityCalculatorSettings.Dist.BINOMIAL;
import static org.geogebra.common.main.settings.ProbabilityCalculatorSettings.Dist.PASCAL;
import static org.geogebra.common.main.settings.ProbabilityCalculatorSettings.Dist.POISSON;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.closeTo;
import static org.hamcrest.core.Is.isA;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.geogebra.common.SuiteSubApp;
import org.geogebra.common.io.XMLStringBuilder;
import org.geogebra.common.io.XmlTestUtil;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.cas.AlgoIntegralDefinite;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.main.settings.ProbabilityCalculatorSettings;
import org.geogebra.common.main.settings.ProbabilityCalculatorSettings.Dist;
import org.geogebra.common.properties.impl.distribution.ParameterProperty;
import org.geogebra.test.BaseAppTestSetup;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

public class ProbabilityCalculatorViewTest extends BaseAppTestSetup {

	private ProbabilityTableMock table;
	private ProbabilityCalculatorView probCalc;

	@BeforeEach
	public void setup() {
		setupApp(SuiteSubApp.GRAPHING);
		getApp().setRounding("2");
	}

	@Test
	public void validBounds() {
		probCalc = new HeadlessProbabilityCalculatorView(getApp());
		probCalc.settingsChanged(new ProbabilityCalculatorSettings());
		probCalc.setLow(-5);
		probCalc.setHigh(0);
		probCalc.updateAll(false);
		assertEquals("0.5", probCalc.getProbabilityText(probCalc.getProbability()));
	}

	@Test
	public void invalidBounds() {
		probCalc = new HeadlessProbabilityCalculatorView(getApp());
		probCalc.settingsChanged(new ProbabilityCalculatorSettings());
		probCalc.setLow(5);
		probCalc.setHigh(0);
		probCalc.updateAll(false);
		assertEquals("?", probCalc.getProbabilityText(probCalc.getProbability()));
	}

	@Test
	public void testExport() {
		probCalc = new HeadlessProbabilityCalculatorView(getApp());
		probCalc.settingsChanged(new ProbabilityCalculatorSettings());
		probCalc.setLow(1);
		probCalc.setHigh(2);
		probCalc.setProbabilityMode(PROB_TWO_TAILED);
		probCalc.exportGeosToEV(1);
		assertEquals("(1, 0)", lookup("A").toValueString(StringTemplate.testTemplate));
		assertEquals("(2, 0)", lookup("B").toValueString(StringTemplate.testTemplate));
		assertThat(lookup("a").getParentAlgorithm(), isA(AlgoIntegralDefinite.class));
		assertThat(lookup("b").getParentAlgorithm(), isA(AlgoIntegralDefinite.class));
	}

	@Test
	public void noTypeCastForParameters() {
		probCalc = new HeadlessProbabilityCalculatorView(getApp());
		probCalc.setProbabilityCalculator(Dist.NORMAL, null, false);
		ParameterProperty prop = new ParameterProperty(getLocalization(),
				getKernel().getAlgebraProcessor(), probCalc, 0, "");
		prop.setValue("true");
		assertThat(probCalc.getProbability(), closeTo(0.34, 0.01));
	}

	@Test
	public void testDiscreteDistributionWithDecimalInput() {
		List<Double> lows = Arrays.asList(6.5, 6.65, 6.7, 6.9);
		List<Double> highs = Arrays.asList(10.1, 10.23, 10.33, 10.45);

		forEachProbabilityShouldBe(0.72555, lows, highs, BINOMIAL, PROB_INTERVAL);
		forEachProbabilityShouldBe(0.87569, highs, BINOMIAL, PROB_LEFT);
		forEachProbabilityShouldBe(0.84985, lows, BINOMIAL, PROB_RIGHT);
		forEachProbabilityShouldBe(0.58680, lows, highs, BINOMIAL, PROB_TWO_TAILED);

		forEachProbabilityShouldBe(0.40022, lows, highs, PASCAL, PROB_INTERVAL);
		forEachProbabilityShouldBe(0.65023, highs, PASCAL, PROB_LEFT);
		forEachProbabilityShouldBe(0.74998, lows, PASCAL, PROB_RIGHT);
		forEachProbabilityShouldBe(0.79331, lows, highs,  PASCAL, PROB_TWO_TAILED);

	}

	@ParameterizedTest
	@ValueSource(ints = {PROB_LEFT, PROB_RIGHT})
	public void resultShouldBeFixedPoint(int probMode) {
		probCalc = new HeadlessProbabilityCalculatorView(getApp());
		probCalc.setProbabilityMode(probMode);
		setParams(BINOMIAL, 20, .5);
		for (int repetition = 0; repetition < 2; repetition++) {
			probCalc.handleResultChange(0.1);
			probCalc.updateAll(false);
			assertEquals(0.13158798217773438, probCalc.getProbability(),
					Kernel.STANDARD_PRECISION);
		}
	}

	private void forEachProbabilityShouldBe(double result, List<Double> lows, List<Double> highs,
			Dist dist, int mode) {
		assertEquals(highs.size(), lows.size(),
				"Lows and highs size differs, please check your test");
		for (int i = 0; i < lows.size(); i++) {
			discreteWithRealBoundsShouldBe(lows.get(i), highs.get(i), result,
					dist, mode);

		}
	}

	private void forEachProbabilityShouldBe(double result, List<Double> values,
			Dist dist, int mode) {
		for (Double value : values) {
			boolean right = mode == PROB_RIGHT;
			discreteWithRealBoundsShouldBe(
					right ? value : Double.NaN,
					right ? Double.NaN : value, result,
					dist, mode);
		}
	}

	private void discreteWithRealBoundsShouldBe(double low, double high, double result,
			Dist dist, int probMode) {
		probCalc =
				createProbabilityCalculatorView(dist, probMode, low, high);
		probCalc.updateOutput(true);
		double probability = probMode == PROB_TWO_TAILED
				? probCalc.leftProbability + probCalc.rightProbability
				: probCalc.getProbability();
		assertThat(probability, closeTo(result, 0.0001));
	}

	private ProbabilityCalculatorView createProbabilityCalculatorView(Dist dist, int probMode,
			double low, double high) {
		probCalc = new HeadlessProbabilityCalculatorView(getApp());
		setParams(dist, 14, 0.6);
		probCalc.setProbabilityMode(probMode);
		if (!Double.isNaN(low)) {
			probCalc.setLow(low);
		}

		if (!Double.isNaN(high)) {
			probCalc.setHigh(high);
		}
		return probCalc;
	}

	private void setParams(Dist dist, int p1, double p2) {
		GeoNumberValue[] params = new GeoNumeric[]{
				new GeoNumeric(getKernel().getConstruction(), p1),
				new GeoNumeric(getKernel().getConstruction(), p2)};

		probCalc.setProbabilityCalculator(dist, params, false);
	}

	@Test
	public void testXAxisIntervalForDiscreteDistShouldBeOne() {
		probCalc = new HeadlessProbabilityCalculatorView(getApp());
		probCalc.plotSettings.xAxesInterval = 0.5;
		for (Dist dist: Dist.values()) {
			probCalc.setProbabilityCalculator(dist, null, false);
			double expected = probCalc.isDiscreteProbability() ? 1 : 0.5;
			assertEquals(expected, probCalc.getPlotSettings().xAxesInterval, 0);
		}
	}

	@Test
	public void testProbabilityTableRowSelection() {
		withProbabilityTable(BINOMIAL, PROB_INTERVAL, 1.9, 10.1)
				.shouldBeHighlightedBetween(2, 10);
		withProbabilityTable(BINOMIAL, PROB_LEFT, 1.9, Double.NaN)
				.shouldBeHighlightedFrom(2);
		withProbabilityTable(BINOMIAL, PROB_RIGHT, Double.NaN, 10)
				.shouldBeHighlightedBetween(0, 10);
		withProbabilityTable(PASCAL, PROB_INTERVAL, 0.9, 10.6)
				.shouldBeHighlightedBetween(1, 11);
		withProbabilityTable(POISSON, PROB_LEFT, 19.43, Double.NaN)
				.shouldBeHighlightedFrom(19);
		withProbabilityTable(PASCAL, PROB_RIGHT, Double.NaN, 5.55)
				.shouldBeHighlightedBetween(0, 6);
	}

	@Test
	public void testStatisticsCalculatorXML() {
		probCalc = new HeadlessProbabilityCalculatorView(getApp(),
				new HeadlessStatisticsCalculator());
	}

	@AfterEach
	public void checkXML() {
		if (probCalc != null) {
			XMLStringBuilder xs = new XMLStringBuilder();
			probCalc.getXML(xs);
			XmlTestUtil.checkXML("<geogebra><gui></gui>" + xs
					+ "<kernel></kernel></geogebra>");
		}
	}

	private ProbabilityCalculatorViewTest withProbabilityTable(Dist dist, int mode, double low,
			double high) {
		probCalc =
				createProbabilityCalculatorView(dist, mode, low, high);
		table = new ProbabilityTableMock(getApp(), probCalc);
		return this;
	}

	private void shouldBeHighlightedBetween(int from, int to) {
		assertTrue(table.isRangeHighlighted(from, to),
				"Highlight rows is not (" + from + ", " + to + ") but "
						+ table.highlightRange());
	}

	private void shouldBeHighlightedFrom(int from) {
		assertTrue(table.isHighlightedFrom(from), "Highlight rows is not from " + from + ", but "
				+ table.highlightRange());
	}

	private class HeadlessStatisticsCalculator extends StatisticsCalculator {

		public HeadlessStatisticsCalculator() {
			super(ProbabilityCalculatorViewTest.this.getApp());
		}

		@Override
		protected void updateTailCheckboxes(String tail) {
			// stub
		}

		@Override
		protected void updateResultText(String tail) {
			// stub
		}

		@Override
		protected String getSelectedTail() {
			return ">";
		}

		@Override
		protected void resetCaret() {
			// stub
		}
	}
}
