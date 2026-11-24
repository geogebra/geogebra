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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;

import java.util.List;

import org.geogebra.common.SuiteSubApp;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.properties.ObjectSettingsSession;
import org.geogebra.common.properties.factory.GeoElementPropertiesFactory;
import org.geogebra.common.properties.impl.objects.ScriptEventSelectionProperty.ScriptEvent;
import org.geogebra.test.BaseAppTestSetup;
import org.junit.jupiter.api.Test;

class ScriptPropertyCollectionTests extends BaseAppTestSetup {
	@Test
	void testInitialScriptEventSelection() {
		setupApp(SuiteSubApp.GRAPHING);
		ScriptPropertyCollection propertyCollection = new ScriptPropertyCollection(
				new GeoElementPropertiesFactory(),
				getLocalization(),
				List.of(evaluateGeoElement("(1, 2)"), evaluateGeoElement("Slider(-5, 5, 1)")),
				true,
				new ObjectSettingsSession.Context());
		assertNotEquals(-1, propertyCollection.getScriptEventSelectionProperty().getIndex());
	}

	@Test
	void replacementPreservesSelectedScriptEvent() {
		setupApp(SuiteSubApp.GRAPHING);
		GeoElement chart = evaluateGeoElement("p = PieChart({1, 2, 3})");
		ObjectSettingsSession.Context context = new ObjectSettingsSession.Context();
		ScriptPropertyCollection scripts = new ScriptPropertyCollection(
				new GeoElementPropertiesFactory(), getLocalization(), List.of(chart), true, context);
		scripts.getScriptEventSelectionProperty().setValue(ScriptEvent.OnUpdate);
		editGeoElement(chart, "PieChart({1, 2, 3}, (0, 0), 5)");
		context.replace(chart, lookup("p"));
		ScriptPropertyCollection reconstructed = new ScriptPropertyCollection(
				new GeoElementPropertiesFactory(), getLocalization(), List.of(lookup("p")), true, context);

		assertNotSame(chart, lookup("p"));
		assertEquals(
				ScriptEvent.OnUpdate, reconstructed.getScriptEventSelectionProperty().getValue());
	}

	@Test
	void replacementResetsUnsupportedScriptEvent() {
		setupApp(SuiteSubApp.GRAPHING);
		GeoElement point = evaluateGeoElement("p = (1, 2)");
		ObjectSettingsSession.Context context = new ObjectSettingsSession.Context();
		ScriptPropertyCollection scripts = new ScriptPropertyCollection(
				new GeoElementPropertiesFactory(), getLocalization(), List.of(point), true, context);
		scripts.getScriptEventSelectionProperty().setValue(ScriptEvent.OnDragEnd);
		evaluateGeoElement("p = true");
		context.replace(point, lookup("p"));
		ScriptPropertyCollection reconstructed = new ScriptPropertyCollection(
				new GeoElementPropertiesFactory(), getLocalization(), List.of(lookup("p")), true, context);

		assertNotSame(point, lookup("p"));
		assertEquals(
				ScriptEvent.OnUpdate, reconstructed.getScriptEventSelectionProperty().getValue());
	}
}
