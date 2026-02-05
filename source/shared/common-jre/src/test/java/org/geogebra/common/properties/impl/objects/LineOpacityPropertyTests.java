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
import org.geogebra.common.kernel.geos.GeoLine;
import org.geogebra.test.BaseAppTestSetup;
import org.junit.jupiter.api.Test;

public class LineOpacityPropertyTests extends BaseAppTestSetup {
	@Test
	public void testSettingLineOpacity() {
		setupApp(SuiteSubApp.GRAPHING);
		GeoLine geoLine = evaluateGeoElement("x = 0");
		LineOpacityProperty lineOpacityProperty = assertDoesNotThrow(() ->
				new LineOpacityProperty(getLocalization(), geoLine));

		lineOpacityProperty.setValue(100);
		assertEquals(100, lineOpacityProperty.getValue());
		assertEquals(255, geoLine.getLineOpacity());

		lineOpacityProperty.setValue(0);
		assertEquals(0, lineOpacityProperty.getValue());
		assertEquals(0, geoLine.getLineOpacity());

		lineOpacityProperty.setValue(50);
		assertEquals(50, lineOpacityProperty.getValue());
		assertEquals(128, geoLine.getLineOpacity());
	}
}
