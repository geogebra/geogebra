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
