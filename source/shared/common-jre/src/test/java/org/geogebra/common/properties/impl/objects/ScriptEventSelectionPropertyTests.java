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

package org.geogebra.common.properties.impl.objects;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.geogebra.common.SuiteSubApp;
import org.geogebra.common.properties.impl.objects.ScriptEventSelectionProperty.ScriptEvent;
import org.geogebra.test.BaseAppTestSetup;
import org.junit.jupiter.api.Test;

public class ScriptEventSelectionPropertyTests extends BaseAppTestSetup {
	@Test
	public void testPointScriptEvents() {
		setupApp(SuiteSubApp.GRAPHING);
		ScriptEventSelectionProperty property = new ScriptEventSelectionProperty(getLocalization(),
				evaluateGeoElement("A = (1, 2)"), true);
		assertArrayEquals(new String[]{"OnClick", "OnUpdate", "OnDragEnd", "GlobalJavaScript"},
				property.getValueNames());
	}

	@Test
	public void testPointScriptEventsWithJavascriptDisabled() {
		setupApp(SuiteSubApp.GRAPHING);
		ScriptEventSelectionProperty property = new ScriptEventSelectionProperty(getLocalization(),
				evaluateGeoElement("A = (1, 2)"), false);
		assertArrayEquals(new String[]{"OnClick", "OnUpdate", "OnDragEnd"},
				property.getValueNames());
	}

	@Test
	public void testInputBoxScriptEvents() {
		setupApp(SuiteSubApp.GRAPHING);
		ScriptEventSelectionProperty property = new ScriptEventSelectionProperty(getLocalization(),
				evaluateGeoElement("InputBox()"), true);
		assertArrayEquals(new String[]{"OnClick", "OnUpdate", "OnChange", "GlobalJavaScript"},
				property.getValueNames());
	}

	@Test
	public void testCheckboxScriptEvents() {
		setupApp(SuiteSubApp.GRAPHING);
		ScriptEventSelectionProperty property = new ScriptEventSelectionProperty(getLocalization(),
				evaluateGeoElement("b = true"), true);
		assertArrayEquals(new String[]{"OnUpdate", "GlobalJavaScript"},
				 property.getValueNames());
	}

	@Test
	public void testChangingSelection() {
		setupApp(SuiteSubApp.GRAPHING);
		ScriptEventSelectionProperty property = new ScriptEventSelectionProperty(getLocalization(),
				evaluateGeoElement("A = (1, 2)"), true);
		assertEquals(ScriptEvent.OnClick, property.getValue());

		property.setIndex(1);
		assertEquals(ScriptEvent.OnUpdate, property.getValue());

		property.setValue(ScriptEvent.GlobalJavascript);
		assertEquals(ScriptEvent.GlobalJavascript, property.getValue());

		property.setValue(ScriptEvent.OnDragEnd);
		assertEquals(ScriptEvent.OnDragEnd, property.getValue());
	}
}
