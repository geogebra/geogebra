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
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.geogebra.common.SuiteSubApp;
import org.geogebra.common.plugin.ScriptType;
import org.geogebra.common.properties.impl.objects.ScriptEventSelectionProperty.ScriptEvent;
import org.geogebra.common.properties.impl.objects.delegate.NotApplicablePropertyException;
import org.geogebra.test.BaseAppTestSetup;
import org.junit.jupiter.api.Test;

public class ScriptLanguageSelectionPropertyTests extends BaseAppTestSetup {
	@Test
	public void testApplicableForOnClickScriptEvent() {
		setupApp(SuiteSubApp.GRAPHING);
		assertDoesNotThrow(() -> new ScriptLanguageSelectionProperty(
				getLocalization(), evaluateGeoElement("A = (1, 2)"), ScriptEvent.OnClick,
				new ScriptLanguageSelection(ScriptType.GGBSCRIPT), true));
	}

	@Test
	public void testNotApplicableForGlobalJavascript() {
		setupApp(SuiteSubApp.GRAPHING);
		assertThrows(NotApplicablePropertyException.class,
				() -> new ScriptLanguageSelectionProperty(getLocalization(),
						evaluateGeoElement("A = (1, 2)"), ScriptEvent.GlobalJavascript,
						new ScriptLanguageSelection(ScriptType.GGBSCRIPT), true));
	}

	@Test
	public void testNotApplicableForDisabledJavaScript() {
		setupApp(SuiteSubApp.GRAPHING);
		assertThrows(NotApplicablePropertyException.class,
				() -> new ScriptLanguageSelectionProperty(getLocalization(),
						evaluateGeoElement("A = (1, 2)"), ScriptEvent.OnClick,
						new ScriptLanguageSelection(ScriptType.GGBSCRIPT), false));
	}

	@Test
	public void testChangingSelection() {
		setupApp(SuiteSubApp.GRAPHING);
		ScriptLanguageSelection scriptLanguageSelection =
				new ScriptLanguageSelection(ScriptType.GGBSCRIPT);
		ScriptLanguageSelectionProperty property = assertDoesNotThrow(() ->
				new ScriptLanguageSelectionProperty(getLocalization(),
						evaluateGeoElement("A = (1, 2)"),
						ScriptEvent.OnClick, scriptLanguageSelection, true));

		property.setIndex(1);
		assertEquals(ScriptType.JAVASCRIPT, property.getValue());
		assertEquals(ScriptType.JAVASCRIPT, scriptLanguageSelection.getSelection());

		property.setValue(ScriptType.GGBSCRIPT);
		assertEquals(ScriptType.GGBSCRIPT, property.getValue());
		assertEquals(ScriptType.GGBSCRIPT, scriptLanguageSelection.getSelection());

		scriptLanguageSelection.setSelection(ScriptType.JAVASCRIPT);
		assertEquals(ScriptType.JAVASCRIPT, property.getValue());
		assertEquals(ScriptType.JAVASCRIPT, scriptLanguageSelection.getSelection());
	}
}
