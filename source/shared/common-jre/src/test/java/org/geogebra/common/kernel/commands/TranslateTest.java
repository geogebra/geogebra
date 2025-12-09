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
 
package org.geogebra.common.kernel.commands;

import org.geogebra.common.BaseUnitTest;
import org.junit.Test;

public class TranslateTest extends BaseUnitTest {

	@Test
	public void testTranslatePiecewise() {
		add("k = 1");
		add("f(x) = If(0 < x < k, 2 + x, 1 < x < 2, -1)");
		add("u = Vector((0, 0), (4, 0))");
		add("f_1(x) = Translate(f, u)");
		t("f_1(4.5)", "2.5");
	}
}
