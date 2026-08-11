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
import org.geogebra.test.BaseAppTestSetup;
import org.geogebra.test.annotation.Issue;
import org.junit.jupiter.api.Test;

class CrossAtPropertyTest extends BaseAppTestSetup {

	@Test
	@Issue("APPS-7766")
	void propertyHandlesExpressions() {
		setupApp(SuiteSubApp.GRAPHING);
		CrossAtProperty property = new CrossAtProperty(getAlgebraProcessor(), getLocalization(),
				getEuclidianSettings(), getApp().getActiveEuclidianView(), 0);
		property.doSetValue("π/2");
		assertEquals(1.57, getEuclidianSettings().getAxesCross()[0], 1E-2);
	}
}
