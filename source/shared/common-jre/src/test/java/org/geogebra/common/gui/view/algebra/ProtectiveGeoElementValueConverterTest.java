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

package org.geogebra.common.gui.view.algebra;

import static org.junit.Assert.assertEquals;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.gui.view.algebra.filter.FunctionAndEquationFilter;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.settings.config.AppConfigGraphing;
import org.junit.Test;

public class ProtectiveGeoElementValueConverterTest extends BaseUnitTest {

	private ProtectiveGeoElementValueConverter converter =
			new ProtectiveGeoElementValueConverter(new FunctionAndEquationFilter());

	@Test
	public void testHidesCommandInput() {
		assertConverts("c=Circle((0,0), 2)", "Circle((0, 0), 2)");
		assertConverts("j=c", "c");
		assertConverts("k=j", "j");
	}

	@Test
	public void testShowCommandInput() {
		getApp().setConfig(new AppConfigGraphing());
		assertConverts("f=Line((1,2), (3,4))", "y = x + 1");
		assertConverts("g=f", "y = x + 1");
		assertConverts("h=g", "y = x + 1");
	}

	private void assertConverts(String input, String expected) {
		GeoElement element = add(input);
		assertEquals(expected, converter.convert(element));
	}
}
