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

import java.util.List;

import org.geogebra.common.GeoGebraConstants;
import org.geogebra.common.SuiteSubApp;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.plugin.EventType;
import org.geogebra.common.plugin.ScriptType;
import org.geogebra.common.properties.ObjectSettingsSession;
import org.geogebra.common.properties.aliases.StringProperty;
import org.geogebra.common.properties.factory.GeoElementPropertiesFactory;
import org.geogebra.common.properties.impl.facade.NamedEnumeratedPropertyListFacade;
import org.geogebra.common.properties.impl.objects.ScriptEventSelectionProperty.ScriptEvent;
import org.geogebra.test.BaseAppTestSetup;
import org.junit.jupiter.api.Test;

class ScriptEventPropertyCollectionTests extends BaseAppTestSetup {
	@Test
	void reconstructedScriptWritesEachElementsLanguage() {
		setupApp(SuiteSubApp.GRAPHING);
		getApp().setPlatform(GeoGebraConstants.Platform.WEB);
		GeoElement first = evaluateGeoElement("(1, 2)");
		GeoElement second = evaluateGeoElement("(3, 4)");
		ObjectSettingsSession.Context context = new ObjectSettingsSession.Context();
		ScriptEventPropertyCollection scripts = new ScriptEventPropertyCollection(
				new GeoElementPropertiesFactory(),
				getLocalization(),
				List.of(first, second),
				ScriptEvent.OnClick,
				true,
				context);
		NamedEnumeratedPropertyListFacade<?, ?> languages =
				(NamedEnumeratedPropertyListFacade<?, ?>) scripts.getProperties()[0];
		languages.getPropertyList().get(0).setIndex(1);
		ScriptEventPropertyCollection reconstructed = new ScriptEventPropertyCollection(
				new GeoElementPropertiesFactory(),
				getLocalization(),
				List.of(first, second),
				ScriptEvent.OnClick,
				true,
				context);
		((StringProperty) reconstructed.getProperties()[1]).setValue("value = 1");

		assertEquals(ScriptType.JAVASCRIPT, first.getScript(EventType.CLICK).getType());
		assertEquals(ScriptType.GGBSCRIPT, second.getScript(EventType.CLICK).getType());
		assertEquals("value = 1", first.getScript(EventType.CLICK).getText());
		assertEquals("value = 1", second.getScript(EventType.CLICK).getText());
	}
}
