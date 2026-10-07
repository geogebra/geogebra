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

package org.geogebra.common.gui.popup.specialpoint;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.geogebra.common.SuiteSubApp;
import org.geogebra.test.BaseAppTestSetup;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SpecialPointPopupHelperTest extends BaseAppTestSetup {

	@BeforeEach
	void setup() {
		setupApp(SuiteSubApp.GRAPHING);
	}

	@Test
	void roundingForPointOnFunction() {
		List<String> strings =
				SpecialPointPopupHelper.getContentRows(getApp(), List.of(evaluateGeoElement("(1,1/3)")));
		assertEquals(List.of("(1, 0.333)"), strings);
	}

	@Test
	void roundingForIntersection() {
		List<String> strings = SpecialPointPopupHelper.getContentRows(
				getApp(), List.of(evaluateGeoElement("Intersect(3y=x,x=1)")));
		assertEquals(List.of("Intersect", "(1, 0.3333333333333)"), strings);
	}
}
