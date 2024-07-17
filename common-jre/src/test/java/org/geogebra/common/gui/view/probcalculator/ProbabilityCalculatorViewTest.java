package org.geogebra.common.gui.view.probcalculator;

import static org.geogebra.common.gui.view.probcalculator.ProbabilityCalculatorView.PROB_INTERVAL;
import static org.geogebra.common.gui.view.probcalculator.ProbabilityCalculatorView.PROB_LEFT;
import static org.geogebra.common.gui.view.probcalculator.ProbabilityCalculatorView.PROB_RIGHT;
import static org.geogebra.common.gui.view.probcalculator.ProbabilityCalculatorView.PROB_TWO_TAILED;
import static org.geogebra.common.main.settings.ProbabilityCalculatorSettings.Dist.BINOMIAL;
import static org.geogebra.common.main.settings.ProbabilityCalculatorSettings.Dist.PASCAL;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.closeTo;
import static org.hamcrest.core.Is.isA;
import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.kernel.cas.AlgoIntegralDefinite;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.main.settings.ProbabilityCalculatorSettings;
import org.geogebra.common.main.settings.ProbabilityCalculatorSettings.Dist;
import org.geogebra.common.properties.impl.distribution.ParameterProperty;
import org.junit.Test;

public class ProbabilityCalculatorViewTest extends BaseUnitTest {

	@Test
	public void validBounds() {
		ProbabilityCalculatorView probCalc = new HeadlessProbabilityCalculatorView(getApp());
		probCalc.settingsChanged(new ProbabilityCalculatorSettings());
		probCalc.setLow(-5);
		probCalc.setHigh(0);
		probCalc.updateAll(false);
		assertEquals(probCalc.getProbabilityText(probCalc.getProbability()), "0.5");
	}

	@Test
	public void invalidBounds() {
		ProbabilityCalculatorView probCalc = new HeadlessProbabilityCalculatorView(getApp());
		probCalc.settingsChanged(new ProbabilityCalculatorSettings());
		probCalc.setLow(5);
		probCalc.setHigh(0);
		probCalc.updateAll(false);
		assertEquals(probCalc.getProbabilityText(probCalc.getProbability()), "?");
	}

	@Test
	public void testExport() {
		ProbabilityCalculatorView probCalc = new HeadlessProbabilityCalculatorView(getApp());
		probCalc.settingsChanged(new ProbabilityCalculatorSettings());
		probCalc.setLow(1);
		probCalc.setHigh(2);
		probCalc.setProbabilityMode(PROB_TWO_TAILED);
		probCalc.exportGeosToEV(1);
		assertThat(lookup("A"), hasValue("(1, 0)"));
		assertThat(lookup("B"), hasValue("(2, 0)"));
		assertThat(lookup("a").getParentAlgorithm(), isA(AlgoIntegralDefinite.class));
		assertThat(lookup("b").getParentAlgorithm(), isA(AlgoIntegralDefinite.class));
	}

	@Test
	public void noTypeCastForParameters() {
		ProbabilityCalculatorView probCalc = new HeadlessProbabilityCalculatorView(getApp());
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

	private void forEachProbabilityShouldBe(double result, List<Double> lows, List<Double> highs,
			Dist dist, int mode) {
		assertEquals("Lows and highs size differs, please check your test",
				highs.size(), lows.size());
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
		ProbabilityCalculatorView probCalc = new HeadlessProbabilityCalculatorView(getApp());
		GeoNumberValue[] params = new GeoNumeric[]{
				new GeoNumeric(getKernel().getConstruction(), 14),
				new GeoNumeric(getKernel().getConstruction(), 0.6)};

		probCalc.setProbabilityCalculator(dist, params, false);
		probCalc.setProbabilityMode(probMode);
		if (!Double.isNaN(low)) {
			probCalc.setLow(low);
		}

		if (!Double.isNaN(high)) {
			probCalc.setHigh(high);
		}

		probCalc.updateOutput(true);
		double probability = probMode == PROB_TWO_TAILED
				? probCalc.leftProbability + probCalc.rightProbability
				: probCalc.getProbability();
		assertThat(probability, closeTo(result, 0.0001));

	}

	@Test
	public void testXAxisIntervalForDiscreteDistShouldBeOne() {
		ProbabilityCalculatorView probCalc = new HeadlessProbabilityCalculatorView(getApp());
		probCalc.plotSettings.xAxesInterval = 0.5;
		for (Dist dist: Dist.values()) {
			probCalc.setProbabilityCalculator(dist, null, false);
			double expected = probCalc.isDiscreteProbability() ? 1 : 0.5;
			assertEquals(expected, probCalc.getPlotSettings().xAxesInterval, 0);
		}
	}
}
