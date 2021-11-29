package org.geogebra.common.gui.view.table;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.stream.Collectors;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoList;
import org.junit.Before;
import org.junit.Test;

import com.himamis.retex.editor.share.util.Unicode;

public class RegressionSpecificationTest extends BaseUnitTest {
	private TableValuesView view;
	private GeoList listY;
	private int column = 1;

	@Before
	public void setupTable() {
		GeoList list = add("{1,2,3,4}");
		listY = add("{1,8,27,64}");
		GeoList listY2 = add("{5,7,5,3}");
		getApp().getSettings().getTable().setValueList(list);
		view = new TableValuesView(getKernel());
		getKernel().attach(view);
		view.add(listY);
		view.showColumn(listY);
		view.add(listY2);
		view.showColumn(listY2);
		column = 1;
	}

	@Test
	public void testRegressionCount() {
		assertEquals(9, RegressionSpecification.getForListSize(listY.size()).size());
	}

	@Test
	public void testLinearRegression() {
		assertEquals("a\\ x+b, a = 20.8, b = -27, R\u00b2 = 0.91",
				getRegressionValues(0));
		assertEquals("20.8x - 27", getRegressionFormula(0));
	}

	@Test
	public void testLogRegression() {
		assertEquals("a + b\\cdot \\log(x), a = -7.59, b = 41.02, R\u00b2 = 0.76",
				getRegressionValues(1));
		assertEquals("-7.59 + 41.02ln(x)", getRegressionFormula(1));
	}

	@Test
	public void testPowerRegression() {
		assertEquals("a \\cdot x^b, a = 1, b = 3, R\u00b2 = 1",
				getRegressionValues(2));
		assertEquals("1x³", getRegressionFormula(2));
	}

	@Test
	public void testQuadraticRegression() {
		assertEquals("a\\ x^{2}+b\\ x+c, a = 7.5, b = -16.7, c = 10.5, R\u00b2 = 1",
				getRegressionValues(3));
		assertEquals("7.5x\u00b2 - 16.7x + 10.5", getRegressionFormula(3));
	}

	@Test
	public void testCubicRegression() {
		assertEquals("a\\ x^{3}+b\\ x^{2}+c\\ x+d, a = 1, b = 0, c = 0, d = 0, R\u00b2 = 1",
				getRegressionValues(4));
		assertEquals("x\u00b3 + 0x\u00b2 - 0x + 0", getRegressionFormula(4));
	}

	@Test
	public void testExponentialRegression() {
		assertEquals("a \\cdot e^{b\\ x}, a = 0.35, b = 1.37, R\u00b2 = 0.81",
				getRegressionValues(5));
		assertEquals("0.35" + Unicode.EULER_STRING + "^(1.37x)",
				getRegressionFormula(5));
	}

	@Test
	public void testGrowthRegression() {
		assertEquals("a \\cdot b^x, a = 0.35, b = 3.93, R\u00b2 = 0.81",
				getRegressionValues(6));
		assertEquals("0.35 * 3.93^x", getRegressionFormula(6));
	}

	@Test
	public void testSinRegression() {
		assertEquals("a \\cdot \\sin(b\\ x + c) + d,"
				+ " a = ?, b = ?, c = ?, d = ?, R\u00b2 = ?",
				getRegressionValues(7));
		assertEquals("?", getRegressionFormula(7));

		column = 2;
		assertEquals("a \\cdot \\sin(b\\ x + c) + d,"
						+ " a = 2, b = 1.57, c = -1.57, d = 5, R\u00b2 = 1",
				getRegressionValues(7));
		assertEquals("5 + 2sin(1.57x - 1.57)", getRegressionFormula(7));
	}

	@Test
	public void testLogisticRegression() {
		assertEquals("\\frac{a}{1 + b\\cdot e^{-c\\ x}},"
				+ " a = 1.5, b = 258.98, c = 105.06, R\u00b2 = 1", getRegressionValues(8));
		assertEquals("105.06 / (1 + 258.98" + Unicode.EULER_STRING + "^(-1.5x))",
				getRegressionFormula(8));
	}

	private String getRegressionFormula(int spec) {
		GeoElement plot = view.plotRegression(column, getSpec(spec));
		return plot.toValueString(StringTemplate.defaultTemplate);
	}

	private String getRegressionValues(int spec) {
		return view.getRegression(column, getSpec(spec)).stream()
				.flatMap(g -> Arrays.stream(g.getValues()))
				.collect(Collectors.joining(", "));
	}

	private RegressionSpecification getSpec(int i) {
		return RegressionSpecification.getForListSize(listY.size()).get(i);
	}
}
