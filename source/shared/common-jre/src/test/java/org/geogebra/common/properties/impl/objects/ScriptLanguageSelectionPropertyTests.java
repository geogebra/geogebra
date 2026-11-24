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

import static org.geogebra.common.properties.impl.objects.ScriptLanguageSelectionProperty.getScriptLanguage;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.geogebra.common.SuiteSubApp;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.plugin.EventType;
import org.geogebra.common.plugin.ScriptType;
import org.geogebra.common.properties.ObjectSettingsSession;
import org.geogebra.common.properties.impl.objects.ScriptEventSelectionProperty.ScriptEvent;
import org.geogebra.common.properties.impl.objects.delegate.NotApplicablePropertyException;
import org.geogebra.test.BaseAppTestSetup;
import org.junit.jupiter.api.Test;

class ScriptLanguageSelectionPropertyTests extends BaseAppTestSetup {
	@Test
	void testApplicableForOnClickScriptEvent() {
		setupApp(SuiteSubApp.GRAPHING);
		assertDoesNotThrow(() -> new ScriptLanguageSelectionProperty(
				getLocalization(),
				evaluateGeoElement("A = (1, 2)"),
				ScriptEvent.OnClick,
				new ElementSettingsData(),
				true));
	}

	@Test
	void testNotApplicableForGlobalJavascript() {
		setupApp(SuiteSubApp.GRAPHING);
		assertThrows(
				NotApplicablePropertyException.class,
				() -> new ScriptLanguageSelectionProperty(
						getLocalization(),
						evaluateGeoElement("A = (1, 2)"),
						ScriptEvent.GlobalJavascript,
						new ElementSettingsData(),
						true));
	}

	@Test
	void testNotApplicableForDisabledJavaScript() {
		setupApp(SuiteSubApp.GRAPHING);
		assertThrows(
				NotApplicablePropertyException.class,
				() -> new ScriptLanguageSelectionProperty(
						getLocalization(),
						evaluateGeoElement("A = (1, 2)"),
						ScriptEvent.OnClick,
						new ElementSettingsData(),
						false));
	}

	@Test
	void testChangingSelection() {
		setupApp(SuiteSubApp.GRAPHING);
		GeoElement element = evaluateGeoElement("A = (1, 2)");
		ElementSettingsData data = new ElementSettingsData();
		ScriptLanguageSelectionProperty property =
				assertDoesNotThrow(() -> new ScriptLanguageSelectionProperty(
						getLocalization(), element, ScriptEvent.OnClick, data, true));

		property.setIndex(1);
		assertEquals(ScriptType.JAVASCRIPT, property.getValue());
		assertEquals(ScriptType.JAVASCRIPT, getScriptLanguage(element, ScriptEvent.OnClick, data));

		property.setValue(ScriptType.GGBSCRIPT);
		assertEquals(ScriptType.GGBSCRIPT, property.getValue());
		assertEquals(ScriptType.GGBSCRIPT, getScriptLanguage(element, ScriptEvent.OnClick, data));

		data.scriptLanguages.put(ScriptEvent.OnClick, ScriptType.JAVASCRIPT);
		assertEquals(ScriptType.JAVASCRIPT, property.getValue());
		assertEquals(ScriptType.JAVASCRIPT, getScriptLanguage(element, ScriptEvent.OnClick, data));
	}

	@Test
	void reconstructedPropertiesPreserveEmptyScriptLanguageForEachEvent()
			throws NotApplicablePropertyException {
		setupApp(SuiteSubApp.GRAPHING);
		GeoElement point = evaluateGeoElement("A = (1, 2)");
		ElementSettingsData elementSettingsData = new ElementSettingsData();
		new ScriptLanguageSelectionProperty(
						getLocalization(), point, ScriptEvent.OnClick, elementSettingsData, true)
				.setValue(ScriptType.JAVASCRIPT);
		ScriptLanguageSelectionProperty click = new ScriptLanguageSelectionProperty(
				getLocalization(), point, ScriptEvent.OnClick, elementSettingsData, true);
		ScriptLanguageSelectionProperty update = new ScriptLanguageSelectionProperty(
				getLocalization(), point, ScriptEvent.OnUpdate, elementSettingsData, true);

		assertEquals(ScriptType.JAVASCRIPT, click.getValue());
		assertEquals(ScriptType.GGBSCRIPT, update.getValue());
	}

	@Test
	void reconstructedPropertyUsesExternallyStoredScriptLanguage()
			throws NotApplicablePropertyException {
		setupApp(SuiteSubApp.GRAPHING);
		GeoElement point = evaluateGeoElement("A = (1, 2)");
		ObjectSettingsSession.Context context = new ObjectSettingsSession.Context();
		new ScriptLanguageSelectionProperty(
						getLocalization(), point, ScriptEvent.OnClick, context.forElement(point), true)
				.setValue(ScriptType.JAVASCRIPT);
		point.setScript(
				getApp().createScript(ScriptType.GGBSCRIPT, "ZoomIn(2)", true), EventType.CLICK);
		ScriptLanguageSelectionProperty reconstructed = new ScriptLanguageSelectionProperty(
				getLocalization(), point, ScriptEvent.OnClick, context.forElement(point), true);

		assertEquals(ScriptType.GGBSCRIPT, reconstructed.getValue());
	}
}
