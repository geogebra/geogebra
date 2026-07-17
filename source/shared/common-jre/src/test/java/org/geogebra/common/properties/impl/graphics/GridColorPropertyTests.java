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

package org.geogebra.common.properties.impl.graphics;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.geogebra.common.SuiteSubApp;
import org.geogebra.common.awt.GColor;
import org.geogebra.test.BaseAppTestSetup;
import org.junit.jupiter.api.Test;

class GridColorPropertyTests extends BaseAppTestSetup {
	@Test
	void testSettingValue() {
		setupApp(SuiteSubApp.GRAPHING);
		GridColorProperty gridColorProperty = new GridColorProperty(
				getLocalization(), getEuclidianSettings());

		gridColorProperty.setValue(GColor.GREEN);
		assertEquals(GColor.GREEN, gridColorProperty.getValue());
		assertEquals(GColor.GREEN, getEuclidianSettings().getGridColor());

		gridColorProperty.setValue(GColor.BLUE);
		assertEquals(GColor.BLUE, gridColorProperty.getValue());
		assertEquals(GColor.BLUE, getEuclidianSettings().getGridColor());
	}
}
