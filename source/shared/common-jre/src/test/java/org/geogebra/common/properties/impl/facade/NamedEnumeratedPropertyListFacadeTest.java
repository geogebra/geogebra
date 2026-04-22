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

package org.geogebra.common.properties.impl.facade;

import static java.util.Map.entry;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;

import java.util.List;

import org.geogebra.common.SuiteSubApp;
import org.geogebra.common.main.Localization;
import org.geogebra.common.properties.impl.AbstractNamedEnumeratedProperty;
import org.geogebra.common.properties.impl.objects.ScriptEventSelectionProperty;
import org.geogebra.common.properties.impl.objects.ScriptEventSelectionProperty.ScriptEvent;
import org.geogebra.test.BaseAppTestSetup;
import org.junit.jupiter.api.Test;

public class NamedEnumeratedPropertyListFacadeTest extends BaseAppTestSetup {
	@Test
	public void getValuesAndIndicesMatchForElementsWithSameValues() {
		setupApp(SuiteSubApp.GRAPHING);
		ScriptEventSelectionProperty property1 = new ScriptEventSelectionProperty(
				getLocalization(), evaluateGeoElement("A = (1, 2)"), true);
		ScriptEventSelectionProperty property2 = new ScriptEventSelectionProperty(
				getLocalization(), evaluateGeoElement("B = (3, 4)"), true);
		NamedEnumeratedPropertyListFacade<ScriptEventSelectionProperty, ScriptEvent> facade =
				new NamedEnumeratedPropertyListFacade<>(List.of(property1, property2));

		assertIterableEquals(List.of(ScriptEvent.OnClick, ScriptEvent.OnUpdate,
				ScriptEvent.OnDragEnd, ScriptEvent.GlobalJavascript), facade.getValues());
		assertArrayEquals(new String[]{"OnClick", "OnUpdate", "OnDragEnd", "GlobalJavaScript"},
				facade.getValueNames());
		assertEquals(0, facade.getIndex());

		facade.setIndex(2);

		assertEquals(ScriptEvent.OnDragEnd, property1.getValue());
		assertEquals(ScriptEvent.OnDragEnd, property2.getValue());
		assertEquals(2, facade.getIndex());
	}

	@Test
	public void getValuesAndIndicesMatchForElementsWithPartialOverlap() {
		setupApp(SuiteSubApp.GRAPHING);
		ScriptEventSelectionProperty pointProperty = new ScriptEventSelectionProperty(
				getLocalization(), evaluateGeoElement("A = (1, 2)"), true);
		ScriptEventSelectionProperty sliderProperty = new ScriptEventSelectionProperty(
				getLocalization(), evaluateGeoElement("a = Slider(-5, 5, 1)"), true);
		NamedEnumeratedPropertyListFacade<ScriptEventSelectionProperty, ScriptEvent> facade =
				new NamedEnumeratedPropertyListFacade<>(List.of(pointProperty, sliderProperty));

		assertIterableEquals(List.of(ScriptEvent.OnUpdate, ScriptEvent.OnDragEnd,
				ScriptEvent.GlobalJavascript), facade.getValues());
		assertArrayEquals(new String[]{"OnUpdate", "OnDragEnd", "GlobalJavaScript"},
				facade.getValueNames());
		assertEquals(-1, facade.getIndex());

		facade.setIndex(0);

		assertEquals(ScriptEvent.OnUpdate, pointProperty.getValue());
		assertEquals(ScriptEvent.OnUpdate, sliderProperty.getValue());
		assertEquals(0, facade.getIndex());

		facade.setIndex(1);

		assertEquals(ScriptEvent.OnDragEnd, pointProperty.getValue());
		assertEquals(ScriptEvent.OnDragEnd, sliderProperty.getValue());
		assertEquals(1, facade.getIndex());
	}

	@Test
	public void propertyIsUnavailableWhenValuesDoNotOverlap() {
		setupApp(SuiteSubApp.GRAPHING);
		NamedEnumeratedPropertyListFacade<TestNamedEnumeratedProperty, String> facade =
				new NamedEnumeratedPropertyListFacade<>(List.of(
						new TestNamedEnumeratedProperty(getLocalization(),
								List.of("_one", "_two")),
						new TestNamedEnumeratedProperty(getLocalization(),
								List.of("_three", "_four"))
				));

		assertIterableEquals(List.of(), facade.getValues());
		assertArrayEquals(new String[0], facade.getValueNames());
		assertEquals(-1, facade.getIndex());
		assertFalse(facade.isAvailable());
	}

	private static final class TestNamedEnumeratedProperty
			extends AbstractNamedEnumeratedProperty<String> {
		private String value;

		TestNamedEnumeratedProperty(Localization localization, List<String> values) {
			super(localization, "Test");
			setNamedValues(values.stream().map(valueName -> entry(valueName, valueName)).toList());
			value = values.get(0);
		}

		@Override
		public String getValue() {
			return value;
		}

		@Override
		protected void doSetValue(String value) {
			this.value = value;
		}
	}
}
