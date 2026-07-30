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

package org.geogebra.common.ownership;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.geogebra.common.AppCommonFactory;
import org.geogebra.common.jre.headless.AppCommon;
import org.geogebra.common.main.settings.config.AppConfigGraphing;
import org.geogebra.common.main.settings.config.AppConfigNotes;
import org.junit.jupiter.api.Test;

class AppScopeTest {

	@Test
	void notesConfigCreatesRememberedProperties() {
		AppCommon app = AppCommonFactory.create(new AppConfigNotes());
		assertEquals(2, app.appScope.getRememberedProperties().size());
	}

	@Test
	void graphingConfigDoesNotCreateRememberedProperties() {
		AppCommon app = AppCommonFactory.create(new AppConfigGraphing());
		assertEquals(0, app.appScope.getRememberedProperties().size());
	}

	@Test
	void missingRememberedPropertiesIsNotCached() {
		AppCommon app = AppCommonFactory.create(new AppConfigGraphing());
		assertEquals(0, app.appScope.getRememberedProperties().size());

		app.setConfig(new AppConfigNotes());
		assertEquals(2, app.appScope.getRememberedProperties().size());
	}
}
