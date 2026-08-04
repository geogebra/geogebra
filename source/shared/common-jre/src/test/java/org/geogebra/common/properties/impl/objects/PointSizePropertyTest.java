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
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.geogebra.common.SuiteSubApp;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.properties.impl.objects.delegate.NotApplicablePropertyException;
import org.geogebra.test.BaseAppTestSetup;
import org.geogebra.test.annotation.Issue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PointSizePropertyTest extends BaseAppTestSetup {

	@BeforeEach
	void setup() {
		setupApp(SuiteSubApp.G3D);
	}

	@Test
	void testConstructorSucceeds() {
		GeoElement point = evaluateGeoElement("(1,2)");
		assertDoesNotThrow(() -> new PointSizeProperty(getLocalization(), point));
	}

	@Test
	@Issue("APPS-7769")
	void testConstructorSucceeds3D() {
		GeoElement point = evaluateGeoElement("(1,2,3)");
		assertDoesNotThrow(() -> new PointSizeProperty(getLocalization(), point));
	}

	@Test
	void testConstructorThrowsError() {
		GeoElement f = evaluateGeoElement("f: x");
		assertThrows(NotApplicablePropertyException.class,
				() -> new PointSizeProperty(getLocalization(), f));
	}
}
