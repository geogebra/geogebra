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
						+ "“\\left( \\begin{align}1 \\\\ 1 \\\\ \\end{align} \\right)”"));
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
						+ "“\\left( \\begin{align}-7 \\\\ 3 \\\\ \\end{align} \\right)”"));
	}
}