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

package org.geogebra.common.kernel.algos;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.gui.view.algebra.AlgebraItem;
import org.geogebra.common.kernel.geos.GeoElement;
import org.junit.Test;

public class AlgoLaTeXTest extends BaseUnitTest {

	@Test
	public void testVectorString() {
		addAvInput("v=(1,1)");
		addAvInput("u=2*v");
		GeoElement formulaText = addAvInput("FormulaText(u,false,false)");
		assertThat(
				AlgebraItem.getLatexString(formulaText, 1500, true),
				equalTo("text1 \\, = \\,“2 \\; v”"));
	}

	@Test
	public void testVectorFromPointString() {
		addAvInput("A=(1,1)");
		addAvInput("u=Vector(A)");
		GeoElement formulaText = addAvInput("FormulaText(u,false,false)");
		assertThat(
				AlgebraItem.getLatexString(formulaText, 1500, true),
				equalTo("text1 \\, = \\,"
						+ "“\\left( \\begin{align}1 \\\\ 1 \\end{align} \\right)”"));
	}

	@Test
	public void testSymbolicVector() {
		getApp().setDefaultConfig();
		addAvInput("a = -7");
		addAvInput("b = 3");
		addAvInput("u = (a,b)");
		GeoElement formulaText = addAvInput("FormulaText(u)");
		assertThat(
				AlgebraItem.getLatexString(formulaText, 1500, true),
				equalTo("text1 \\, = \\,"
						+ "“\\left( \\begin{align}-7 \\\\ 3 \\end{align} \\right)”"));
	}
}