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
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.geogebra.common.SuiteSubApp;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.SelectionManager;
import org.geogebra.test.BaseAppTestSetup;
import org.geogebra.test.annotation.Issue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SelectionAllowedPropertyTest extends BaseAppTestSetup {

	@BeforeEach
	void setUp() {
		setupApp(SuiteSubApp.GRAPHING);
	}

	@Test
	void testApplicable() {
		GeoElement point = evaluateGeoElement("(1, 1)");
		assertDoesNotThrow(() ->
				new SelectionAllowedProperty(getLocalization(), point));
	}

	@Test
	@Issue("APPS-7768")
	void testDisablingSelectionAllowedDoesNotDeselectSelectedGeo() {
		SelectionManager selectionManager = getApp().getSelectionManager();
		GeoElement point = evaluateGeoElement("(6, 7)");
		selectionManager.addSelectedGeo(point);

		new SelectionAllowedProperty(getLocalization(), point).setValue(false);
		assertTrue(selectionManager.getSelectedGeos().contains(point));
	}
}
