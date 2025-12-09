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

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.MatcherAssert.assertThat;

import org.geogebra.common.gui.view.probcalculator.result.ResultEntry;
import org.geogebra.common.gui.view.probcalculator.result.impl.entries.TextEntry;
import org.geogebra.common.gui.view.probcalculator.result.impl.models.IntervalResultModel;
import org.geogebra.common.gui.view.probcalculator.result.impl.models.LeftResultModel;
import org.geogebra.common.gui.view.probcalculator.result.impl.models.RightResultModel;
import org.geogebra.common.gui.view.probcalculator.result.impl.models.TwoTailedResultModel;
import org.geogebra.common.gui.view.spreadsheet.SpreadsheetViewInterface;
import org.geogebra.common.jre.headless.LocalizationCommon;
import org.junit.Before;
import org.junit.Test;

public class PropertyResultPanelTest {

	private static final String GREATER_THAN_OR_EQUAL_TO =
			SpreadsheetViewInterface.GREATER_THAN_OR_EQUAL_TO_X;
	private static final String GREATER_THAN = "X >";

	private PropertyResultPanel propertyResultPanel;

	@Before
	public void setUp() {
		propertyResultPanel = new PropertyResultPanel(null, new LocalizationCommon(2));
	}

	@Test
	public void testShowInterval() {
		propertyResultPanel.showInterval();
		assertThat(propertyResultPanel.getModel(), instanceOf(IntervalResultModel.class));

		propertyResultPanel.updateLowHigh("-2", "2");
		ResultEntry low = propertyResultPanel.getModel().getEntries().get(1);
		assertThat(low.getText(), equalTo("-2"));
		ResultEntry high = propertyResultPanel.getModel().getEntries().get(3);
		assertThat(high.getText(), equalTo("2"));

		propertyResultPanel.updateResult("1");
		ResultEntry result = propertyResultPanel.getModel().getEntries().get(5);
		assertThat(result.getText(), equalTo("1"));
	}

	@Test
	public void testShowTwoTailed() {
		propertyResultPanel.showTwoTailed();
		assertThat(propertyResultPanel.getModel(), instanceOf(TwoTailedResultModel.class));

		assertThat(getGreaterSign().getText(), equalTo(GREATER_THAN_OR_EQUAL_TO));
		propertyResultPanel.setGreaterThan();
		assertThat(getGreaterSign().getText(), equalTo(GREATER_THAN));
		propertyResultPanel.setGreaterOrEqualThan();
		assertThat(getGreaterSign().getText(), equalTo(GREATER_THAN_OR_EQUAL_TO));

		propertyResultPanel.updateTwoTailedResult("-2", "2");
		ResultEntry twoTailedResult = propertyResultPanel.getModel().getEntries().get(7);
		assertThat(twoTailedResult.getText(), equalTo("-2 + 2 = "));
	}

	private TextEntry getGreaterSign() {
		return (TextEntry) propertyResultPanel.getModel().getEntries().get(4);
	}

	@Test
	public void testStoringOldValues() {
		propertyResultPanel.showTwoTailed();
		propertyResultPanel.setGreaterThan();

		propertyResultPanel.showInterval();
		propertyResultPanel.showTwoTailed();
		assertThat(getGreaterSign().getText(), equalTo(GREATER_THAN));
	}

	@Test
	public void testShowTwoTailedOnePoint() {
		propertyResultPanel.showTwoTailedOnePoint();
		assertThat(getGreaterSign().getText(), equalTo(GREATER_THAN));
	}

	@Test
	public void testShowLeft() {
		propertyResultPanel.showLeft();
		assertThat(propertyResultPanel.getModel(), instanceOf(LeftResultModel.class));
	}

	@Test
	public void testShowRight() {
		propertyResultPanel.showRight();
		assertThat(propertyResultPanel.getModel(), instanceOf(RightResultModel.class));
	}
}