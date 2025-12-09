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
import org.geogebra.common.kernel.geos.GeoText;
import org.geogebra.common.properties.impl.objects.delegate.NotApplicablePropertyException;
import org.geogebra.test.BaseAppTestSetup;
import org.junit.jupiter.api.Test;

public class LabelPropertyTests extends BaseAppTestSetup {
	@Test
	public void testSuccessfulConstruction() {
		setupApp(SuiteSubApp.GRAPHING);
		GeoElement geoElement = evaluateGeoElement("f(x) = x");
		assertDoesNotThrow(() -> new LabelProperty(getLocalization(), geoElement));
	}

	@Test
	public void testConstructingNotApplicableProperty() {
		setupApp(SuiteSubApp.GRAPHING);
		GeoText geoText = evaluateGeoElement("\"\"");
		assertThrows(NotApplicablePropertyException.class,
				() -> new LabelProperty(getLocalization(), geoText));
	}
}
