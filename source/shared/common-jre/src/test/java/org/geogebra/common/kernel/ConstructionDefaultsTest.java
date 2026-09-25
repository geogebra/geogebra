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

package org.geogebra.common.kernel;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.geogebra.common.AppCommonFactory;
import org.geogebra.common.jre.headless.AppCommon;
import org.geogebra.common.main.GeoGebraColorConstants;
import org.geogebra.common.main.settings.config.AppConfigGraphing;
import org.junit.jupiter.api.Test;

class ConstructionDefaultsTest {

	@Test
	void blackObjectDefaultsShouldUseNeutral900() {
		AppCommon app = AppCommonFactory.create(new AppConfigGraphing());
		ConstructionDefaults defaults = app.getKernel().getConstruction().getConstructionDefaults();

		assertDefaultColor(defaults, ConstructionDefaults.DEFAULT_LINE);
		assertDefaultColor(defaults, ConstructionDefaults.DEFAULT_SEGMENT);
		assertDefaultColor(defaults, ConstructionDefaults.DEFAULT_RAY);
		assertDefaultColor(defaults, ConstructionDefaults.DEFAULT_VECTOR);
		assertDefaultColor(defaults, ConstructionDefaults.DEFAULT_POLYLINE);
		assertDefaultColor(defaults, ConstructionDefaults.DEFAULT_CONIC);
		assertDefaultColor(defaults, ConstructionDefaults.DEFAULT_NUMBER);
		assertDefaultColor(defaults, ConstructionDefaults.DEFAULT_ANGLE);
		assertDefaultColor(defaults, ConstructionDefaults.DEFAULT_TEXT);
	}

	private void assertDefaultColor(ConstructionDefaults defaults, int defaultType) {
		assertEquals(
				GeoGebraColorConstants.NEUTRAL_900, defaults.getDefaultGeo(defaultType).getObjectColor());
	}
}
