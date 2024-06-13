package org.geogebra.common.gui.view.probcalculator;

import static org.geogebra.common.gui.view.probcalculator.ProbabilityCalculatorView.PROB_TWO_TAILED;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.closeTo;
import static org.hamcrest.core.Is.isA;
import static org.junit.Assert.assertEquals;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.kernel.cas.AlgoIntegralDefinite;
import org.geogebra.common.main.settings.ProbabilityCalculatorSettings;
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
		probCalc.setProbabilityCalculator(ProbabilityCalculatorSettings.Dist.NORMAL, null, false);
		ParameterProperty prop = new ParameterProperty(getLocalization(),
				getKernel().getAlgebraProcessor(), probCalc, 0, "");
		prop.setValue("true");
		assertThat(probCalc.getProbability(), closeTo(0.34, 0.01));
	}

}
