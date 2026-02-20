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

package org.geogebra.web.full.gui.view.algebra.compositefocus;

import static org.geogebra.common.main.settings.AlgebraStyle.*;
import static org.geogebra.common.main.settings.AlgebraStyle.DEFINITION;
import static org.junit.Assert.assertEquals;

import org.geogebra.common.main.settings.AlgebraStyle;
import org.geogebra.web.test.GgbMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(GgbMockitoTestRunner.class)
public class AccessibleContentProviderTest extends AddGeosSetup {

	private AccessibleContentProvider labels;

	@Before
	public void setUp() {
		initApp();
	}

	@Test
	public void testPoint() {
		tAll("(1.0, 1.0)", "A = (1.0, 1.0)", "Point A", "A = (1.0, 1.0)");
		t("(1.0, 1.0)", DEFINITION_AND_VALUE, "A = (1.0, 1.0)");
		t("(1.0, 1.0)", LINEAR_NOTATION, "A = (1.0, 1.0)");
	}

	@Test
	public void testCircle() {
		tAll("Circle((1.0, 1.0), 2)",
				"c = Circle((1.0, 1.0), 2)",
				"c = Circle with center (1.0, 1.0) and radius 2",
				"c: (x - 1.0)\u00b2 + (y - 1.0)\u00b2 = 4.0");
	}

	@Test
	public void testFunction() {
		tAll("f(x) = 2x + 1", "f(x) = 2x + 1", "f(x) = 2x + 1", "f(x) = 2x + 1");
		t("f(x) = 2x + 1", DEFINITION_AND_VALUE, "f(x) = 2x + 1");
		t("f(x) = 2x + 1", LINEAR_NOTATION, "f(x) = 2x + 1");

	}

	private void tAll(String cmd, String... expected) {
		AlgebraStyle[] styles = new AlgebraStyle[] {DEFINITION, DESCRIPTION, VALUE};
		for (int i = 0; i < styles.length; i++) {
			t(cmd, styles[i], expected[i]);
		}
	}

	private void t(String cmd, AlgebraStyle algebraStyle, String expected) {
		getApp().fileNew();
		labels = new AccessibleContentProvider(add(cmd), algebraStyle);
		String label = labels.getContentSupplier().get();
		assertEquals(expected, label);
	}
}
