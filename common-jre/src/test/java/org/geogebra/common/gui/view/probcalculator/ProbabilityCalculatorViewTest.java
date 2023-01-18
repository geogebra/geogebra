package org.geogebra.common.gui.view.probcalculator;

import static org.junit.Assert.assertEquals;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.main.settings.ProbabilityCalculatorSettings;
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

}
