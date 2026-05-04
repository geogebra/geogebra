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
import static org.geogebra.common.properties.PropertyView.ProbabilityResultRow.Item;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

import java.util.List;

import org.geogebra.common.SuiteSubApp;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.main.settings.ProbabilityCalculatorSettings.Dist;
import org.geogebra.common.properties.PropertyView;
import org.geogebra.common.properties.impl.distribution.ProbabilityResultValuesProperty;
import org.geogebra.test.BaseAppTestSetup;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ProbabilityResultRowPropertyViewTests extends BaseAppTestSetup {
	private ProbabilityCalculatorView probabilityCalculatorView;
	private ProbabilityResultValuesProperty property;
	private PropertyView.ProbabilityResultRow propertyView;

	@BeforeEach
	public void setUp() {
		setupApp(SuiteSubApp.PROBABILITY);
		getApp().setRounding("4");
		probabilityCalculatorView = new HeadlessProbabilityCalculatorView(getApp());
		probabilityCalculatorView.setProbabilityCalculator(Dist.NORMAL, new GeoNumberValue[] {
				new GeoNumeric(getKernel().getConstruction(), 0),
				new GeoNumeric(getKernel().getConstruction(), 1)
		}, false);
		property = new ProbabilityResultValuesProperty(getLocalization(), getAlgebraProcessor(),
				probabilityCalculatorView);
		propertyView = assertInstanceOf(PropertyView.ProbabilityResultRow.class,
				PropertyView.of(property));
	}

	@Test
	public void testIntervalPresentation() {
		probabilityCalculatorView.setProbabilityMode(PROB_INTERVAL);
		property.getLowerBoundProperty().setValue("-1");
		property.getUpperBoundProperty().setValue("1");
		List<PropertyView.ProbabilityResultRow.Item> items = propertyView.getItems();

		assertEquals(6, items.size());
		assertEquals("P(", assertInstanceOf(Item.Text.class, items.get(0)).text());
		assertEquals("-1", assertInstanceOf(Item.InputField.class, items.get(1)).getValue());
		assertEquals(" ≤ X ≤ ", assertInstanceOf(Item.Text.class, items.get(2)).text());
		assertEquals("1", assertInstanceOf(Item.InputField.class, items.get(3)).getValue());
		assertEquals(") = ", assertInstanceOf(Item.Text.class, items.get(4)).text());
		assertEquals("0.6827", assertInstanceOf(Item.Text.class, items.get(5)).text());
	}

	@Test
	public void testLeftTailPresentation() {
		probabilityCalculatorView.setProbabilityMode(PROB_LEFT);
		property.getUpperBoundProperty().setValue("3");
		List<PropertyView.ProbabilityResultRow.Item> items = propertyView.getItems();

		assertEquals(4, items.size());
		assertEquals("P(X ≤ ", assertInstanceOf(Item.Text.class, items.get(0)).text());
		assertEquals("3", assertInstanceOf(Item.InputField.class, items.get(1)).getValue());
		assertEquals(") = ", assertInstanceOf(Item.Text.class, items.get(2)).text());
		assertEquals("0.9987", assertInstanceOf(Item.InputField.class, items.get(3)).getValue());
	}

	@Test
	public void testRightTailPresentation() {
		probabilityCalculatorView.setProbabilityMode(PROB_RIGHT);
		property.getLowerBoundProperty().setValue("-2");
		List<PropertyView.ProbabilityResultRow.Item> items = propertyView.getItems();

		assertEquals(4, items.size());
		assertEquals("P(", assertInstanceOf(Item.Text.class, items.get(0)).text());
		assertEquals("-2", assertInstanceOf(Item.InputField.class, items.get(1)).getValue());
		assertEquals(" ≤ X) = ", assertInstanceOf(Item.Text.class, items.get(2)).text());
		assertEquals("0.9772", assertInstanceOf(Item.InputField.class, items.get(3)).getValue());
	}

	@Test
	public void testTwoTailedPresentation() {
		probabilityCalculatorView.setProbabilityMode(PROB_TWO_TAILED);
		property.getLowerBoundProperty().setValue("-1");
		property.getUpperBoundProperty().setValue("2");
		List<PropertyView.ProbabilityResultRow.Item> items = propertyView.getItems();

		assertEquals(10, items.size());
		assertEquals("P(X ≤ ", assertInstanceOf(Item.Text.class, items.get(0)).text());
		assertEquals("-1", assertInstanceOf(Item.InputField.class, items.get(1)).getValue());
		assertEquals(") + P(X ≥ ", assertInstanceOf(Item.Text.class, items.get(2)).text());
		assertEquals("2", assertInstanceOf(Item.InputField.class, items.get(3)).getValue());
		assertEquals(") = ", assertInstanceOf(Item.Text.class, items.get(4)).text());
		assertEquals("0.1587", assertInstanceOf(Item.Text.class, items.get(5)).text());
		assertEquals("+", assertInstanceOf(Item.Text.class, items.get(6)).text());
		assertEquals("0.0228", assertInstanceOf(Item.Text.class, items.get(7)).text());
		assertEquals("=", assertInstanceOf(Item.Text.class, items.get(8)).text());
		assertEquals("0.1814", assertInstanceOf(Item.Text.class, items.get(9)).text());
	}
}
