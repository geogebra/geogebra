package org.geogebra.common.properties.impl.distribution;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.MatcherAssert.assertThat;

import org.geogebra.common.gui.view.probcalculator.model.entry.AbstractEntry;
import org.geogebra.common.gui.view.probcalculator.model.resultpanel.IntervalResultModel;
import org.geogebra.common.gui.view.probcalculator.model.resultpanel.TwoTailedResultModel;
import org.geogebra.common.gui.view.spreadsheet.SpreadsheetViewInterface;
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

	@Test
	public void testShowTwoTailed() {
		probabilityResultProperty.showTwoTailed();
		assertThat(probabilityResultProperty.getModel(), instanceOf(TwoTailedResultModel.class));

		final String greaterThanOrEqualTo = SpreadsheetViewInterface.GREATER_THAN_OR_EQUAL_TO_X;
		final String greaterThan = "X > ";
		AbstractEntry greaterSign = probabilityResultProperty.getModel().getEntries().get(5);

		assertThat(greaterSign.getText(), equalTo(greaterThanOrEqualTo));
		probabilityResultProperty.setGreaterThan();
		assertThat(greaterSign.getText(), equalTo(greaterThan));
		probabilityResultProperty.setGreaterOrEqualThan();
		assertThat(greaterSign.getText(), equalTo(greaterThanOrEqualTo));

		probabilityResultProperty.updateLowHigh("-2", "2");
		AbstractEntry twoTailedResult = probabilityResultProperty.getModel().getEntries().get(8);
		assertThat(twoTailedResult.getText(), equalTo("-2 + 2 = "));
	}
}