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
 
package org.geogebra.common.kernel.arithmetic;

import static org.junit.Assert.assertEquals;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.commands.EvalInfo;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.test.TestErrorHandler;

public class SymbolicArithmeticTest extends BaseUnitTest {

	/**
	 * Evaluate inputs using symbolic flag
	 * @param string input
	 * @param string2 expected output
	 */
	protected void t(String string, String string2) {
		GeoElementND[] geos = getKernel().getAlgebraProcessor()
				.processAlgebraCommandNoExceptionHandling(string, false,
						TestErrorHandler.INSTANCE,
						new EvalInfo(true).withSymbolic(true), null);
		assertEquals(string2,
				geos[0].toValueString(StringTemplate.algebraTemplate));
	}
}
