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

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.geogebra.common.SuiteSubApp;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.plugin.EventType;
import org.geogebra.common.plugin.ScriptType;
import org.geogebra.common.properties.impl.objects.ScriptEventSelectionProperty.ScriptEvent;
import org.geogebra.test.BaseAppTestSetup;
import org.junit.jupiter.api.Test;

public class ScriptPropertyTests extends BaseAppTestSetup {
	@Test
	public void testSettingScript() {
		setupApp(SuiteSubApp.GRAPHING);
		GeoPoint geoPoint = evaluateGeoElement("A = (1, 2)");
		ScriptProperty property = new ScriptProperty(getLocalization(), geoPoint,
				ScriptEvent.OnDragEnd, new ScriptLanguageSelection(ScriptType.GGBSCRIPT));

		property.setValue("Drag-end script 1");
		assertEquals("Drag-end script 1", property.getValue());
		assertEquals("Drag-end script 1", geoPoint.getScript(EventType.DRAG_END).getText());

		property.setValue("Drag-end script 2");
		assertEquals("Drag-end script 2", property.getValue());
		assertEquals("Drag-end script 2", geoPoint.getScript(EventType.DRAG_END).getText());
	}

	@Test
	public void testChangingScriptTypeUpdatesStoredScript() {
		setupApp(SuiteSubApp.GRAPHING);
		GeoPoint geoPoint = evaluateGeoElement("A = (1, 2)");
		ScriptLanguageSelection scriptLanguageSelection =
				new ScriptLanguageSelection(ScriptType.GGBSCRIPT);
		ScriptProperty scriptProperty = new ScriptProperty(getLocalization(), geoPoint,
				ScriptEvent.OnClick, scriptLanguageSelection);
		ScriptLanguageSelectionProperty scriptLanguageSelectionProperty = assertDoesNotThrow(() ->
				new ScriptLanguageSelectionProperty(getLocalization(), geoPoint,
						ScriptEvent.OnClick, scriptLanguageSelection, true));

		scriptProperty.setValue("Click script");
		assertEquals("Click script", scriptProperty.getValue());
		assertEquals(ScriptType.GGBSCRIPT, geoPoint.getScript(EventType.CLICK).getType());

		scriptLanguageSelectionProperty.setValue(ScriptType.JAVASCRIPT);
		assertEquals("Click script", scriptProperty.getValue());
		assertEquals(ScriptType.JAVASCRIPT, geoPoint.getScript(EventType.CLICK).getType());
	}
}
