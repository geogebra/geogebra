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

import org.geogebra.common.AppCommonFactory;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.main.App;
import org.geogebra.test.commands.AlgebraTestHelper;
import org.junit.Before;
import org.junit.Test;

public class CommandsTest2D {

	private AlgebraProcessor ap;

	@Before
	public void setup() {
		App app = AppCommonFactory.create();
		app.setLanguage("en");
		ap = app.getKernel().getAlgebraProcessor();
	}

	private void t(String input, String expect) {
		AlgebraTestHelper.checkSyntaxSingle(input, new String[] { expect }, ap,
				StringTemplate.testTemplate);
	}

	@Test
	public void orthogonalLineTest() {
		t("OrthogonalLine((0,0),x=y)", "x + y = 0");
		t("OrthogonalLine((0,0),x=y,space)", "x + y = 0");
	}
}
