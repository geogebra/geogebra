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

package org.geogebra.web.full.main;

import static org.junit.Assert.assertEquals;

import org.geogebra.common.factories.FormatFactory;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.util.ToStringConverter;
import org.geogebra.web.test.AppMocker;
import org.geogebra.web.test.GgbMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(GgbMockitoTestRunner.class)
public class AppWFullTest {

	@Before
	public void setupFormat() {
		FormatFactory.setPrototypeIfNull(new TestFormatFactory());
	}

	@Test
	public void graphingUsesProtectiveFilter() {
		AppWFull app = AppMocker.mockGraphing();
		ToStringConverter outputFilter =
				app.getGeoElementValueConverter();
		assertEquals("Ray((0, 0), (1, 1))", outputFilter.convert(addEquation(app,
				"Ray((0,0),(1,1))")));
	}

	private GeoElement addEquation(AppWFull app, String equation) {
		return app.getKernel().getAlgebraProcessor()
				.processAlgebraCommand(equation, false)[0].toGeoElement();
	}

	@Test
	public void geometryUsesNoFilter() {
		AppWFull app = AppMocker.mockGeometry();
		ToStringConverter outputFilter = app.getGeoElementValueConverter();
		assertEquals("y = x", outputFilter.convert(addEquation(app,
				"Ray((0,0),(1,1))")));
	}

	@Test
	public void casUsesNoFilter() {
		AppWFull app = AppMocker.mockCas();
		ToStringConverter outputFilter = app.getGeoElementValueConverter();
		assertEquals("-x - y = -1.0",
				outputFilter.convert(addEquation(app, "PerpendicularBisector((0,0),(1,1))")));
	}
}