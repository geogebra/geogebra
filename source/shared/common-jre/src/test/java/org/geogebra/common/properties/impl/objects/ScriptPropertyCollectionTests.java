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

import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.util.List;

import org.geogebra.common.SuiteSubApp;
import org.geogebra.common.properties.factory.GeoElementPropertiesFactory;
import org.geogebra.test.BaseAppTestSetup;
import org.junit.jupiter.api.Test;

public class ScriptPropertyCollectionTests extends BaseAppTestSetup {
	@Test
	public void testInitialScriptEventSelection() {
		setupApp(SuiteSubApp.GRAPHING);
		ScriptPropertyCollection propertyCollection = new ScriptPropertyCollection(
				new GeoElementPropertiesFactory(), getLocalization(), List.of(
				evaluateGeoElement("(1, 2)"), evaluateGeoElement("Slider(-5, 5, 1)")), true);
		assertNotEquals(-1, propertyCollection.getScriptEventSelectionProperty().getIndex());
	}
}
