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
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.Locale;

import org.geogebra.common.SuiteSubApp;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoSpace;
import org.geogebra.common.kernel.geos.GeoAxis;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.test.BaseAppTestSetup;
import org.geogebra.test.annotation.Issue;
import org.junit.Before;
import org.junit.Test;

public class KernelTest extends BaseAppTestSetup {

	@Before
	public void setup() {
		setupApp(SuiteSubApp.G3D);
	}

	@Test
	@Issue("APPS-7270")
	public void testUpdateLocalAxesNamesFromEnglish() {
		evaluate("esp=7");
		getApp().setLocale(new Locale("pt", "BR"));
		assertEquals(GeoNumeric.class, lookup("esp").getClass());
	}

	@Test
	@Issue("APPS-7270")
	public void testUpdateLocalAxesNamesToEnglish2D() {
		getApp().setLocale(new Locale("de"));
		GeoElementND[] evaluate = evaluate("xAchse=7");
		assertNull(evaluate);
		evaluate = evaluate("xAxis=7");
		assertNull(evaluate);
		assertEquals(GeoAxis.class, lookup("xAchse").getClass());
		assertEquals(GeoAxis.class, lookup("xAxis").getClass());
		getApp().setLocale(new Locale("en"));
		evaluate = evaluate("xAchse=7");
		assertEquals(GeoNumeric.class, evaluate[0].getClass());
	}

	@Test
	@Issue("APPS-7270")
	public void testUpdateLocalAxesNamesToEnglish3D() {
		getApp().setLocale(new Locale("pt", "BR"));
		GeoElementND[] evaluate = evaluate("space=7");
		assertNull(evaluate);
		evaluate = evaluate("esp=7");
		assertNull(evaluate);
		assertEquals(GeoSpace.class, lookup("esp").getClass());
		getApp().setLocale(new Locale("en"));
		evaluate = evaluate("esp=7");
		assertEquals(GeoNumeric.class, evaluate[0].getClass());
	}
}
