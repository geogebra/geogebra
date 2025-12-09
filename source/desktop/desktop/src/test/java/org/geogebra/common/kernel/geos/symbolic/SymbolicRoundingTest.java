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
 
package org.geogebra.common.kernel.geos.symbolic;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;

import org.geogebra.common.gui.view.algebra.AlgebraItem;
import org.geogebra.common.kernel.geos.BaseSymbolicTest;
import org.geogebra.common.kernel.geos.GeoSymbolic;
import org.geogebra.common.util.SymbolicUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class SymbolicRoundingTest extends BaseSymbolicTest {

	private int printDecimals;
	private int printFigures;

	@Before
	public void storeInitialRounding() {
		printDecimals = kernel.getPrintDecimals();
		printFigures = kernel.getPrintFigures();
	}

	/** Reset rounding **/
	@After
	public void resetRounding() {
		kernel.setPrintFigures(printFigures);
		kernel.setPrintDecimals(printDecimals);
	}

	@Test
	public void testRounding() {
		kernel.setPrintFigures(20);
		GeoSymbolic number = add("11.3 * 1.5");
		SymbolicUtil.toggleSymbolic(number);
		String output = AlgebraItem.getOutputTextForGeoElement(number);
		assertThat(output, equalTo("16.95"));
	}

	@Test
	public void testNumericSolve2Rounding() {
		GeoSymbolic number = add("NSolve(x^2=6, x)");
		kernel.setPrintDecimals(3);
		String output = AlgebraItem.getOutputTextForGeoElement(number);
		assertThat(output, equalTo("\\left\\{x\\, = \\,-2.449,\\;x\\, = \\,2.449\\right\\}"));
	}
}
