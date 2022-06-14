package org.geogebra.common.properties.impl.distribution;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.MatcherAssert.assertThat;

import org.geogebra.common.gui.view.probcalculator.model.entry.AbstractEntry;
import org.geogebra.common.gui.view.probcalculator.model.resultpanel.IntervalResultModel;
import org.geogebra.common.jre.headless.LocalizationCommon;
import org.junit.Before;
import org.junit.Test;

public class ProbabilityResultPropertyTest {

	private ProbabilityResultProperty probabilityResultProperty;

	@Before
	public void setUp() {
		probabilityResultProperty = new ProbabilityResultProperty(
				new LocalizationCommon(2), "probabilityResult");
	}

	@Test
	public void testShowInterval() {
		probabilityResultProperty.showInterval();
		assertThat(probabilityResultProperty.getModel(), instanceOf(IntervalResultModel.class));

		probabilityResultProperty.updateLowHigh("-2", "2");
		AbstractEntry low = probabilityResultProperty.getModel().getEntries().get(1);
		assertThat(low.getText(), equalTo("-2"));
		AbstractEntry high = probabilityResultProperty.getModel().getEntries().get(3);
		assertThat(high.getText(), equalTo("2"));

		probabilityResultProperty.updateResult("1");
		AbstractEntry result = probabilityResultProperty.getModel().getEntries().get(5);
		assertThat(result.getText(), equalTo("1"));
	}
}