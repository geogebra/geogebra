package org.geogebra.desktop;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.geogebra.cas.BaseCASIntegrationTest;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.arithmetic.ExpressionNodeEvaluator;
import org.geogebra.common.kernel.commands.AlgebraProcessor;
import org.geogebra.common.kernel.geos.GeoCasCell;
import org.geogebra.common.kernel.geos.GeoDummyVariable;
import org.geogebra.common.plugin.Operation;
import org.geogebra.desktop.headless.AppDNoGui;
import org.geogebra.test.annotation.Issue;
import org.junit.Before;
import org.junit.Test;

@Issue("APPS-5656")
public class CasFunctionRedefineTest extends BaseCASIntegrationTest {

	private AlgebraProcessor ap;
	private AppDNoGui app;

	@Before
	public void setUp() throws Exception {
		app = getApp();
		ap = app.getKernel().getAlgebraProcessor();
	}

	@Test
	public void handleCoordsShouldBeNaNForDummyVariable() {
		ExpressionNodeEvaluator evaluator =
				new ExpressionNodeEvaluator(kernel.getLocalization(), kernel);
		GeoDummyVariable t = new GeoDummyVariable(kernel.getConstruction(), "t");
		assertTrue("x(t) should be NaN", Double.isNaN(evaluator.handleXcoord(t,
				Operation.XCOORD)));
		assertTrue("y(t) should be NaN", Double.isNaN(evaluator.handleYcoord(t,
				Operation.YCOORD)));
		assertTrue("z(t) should be NaN",
				Double.isNaN(evaluator.handleZcoord(t)));
	}

	@Test
	public void testCellUpdate() {
		GeoCasCell cell1 = cellFromInput("f(a, b) := a b");
		cellFromInput("g(a, b) := a + b");
		GeoCasCell cell3 = cellFromInput("Flatten({$1, $2})");
		assertEquals("{a b, a + b}",
				cell3.getOutput(StringTemplate.defaultTemplate));

		updateCellInput(cell1, "f(a, b) := a / b");

		assertEquals("{a / b, a + b}",
				cell3.getOutput(StringTemplate.defaultTemplate));
	}

	private void updateCellInput(GeoCasCell cell1, String inValue) {
		cell1.setInput(inValue);
		ap.processCasCell(cell1, false, app.getXML());
	}

	private GeoCasCell cellFromInput(String input) {
		GeoCasCell f = new GeoCasCell(kernel.getConstruction());
		updateCellInput(f, input);
		return f;
	}

	@Test
	@Issue("APPS-5838")
	public void solveButtonBroken() {
		cellFromInput("f(x) := 2x - 123");
		cellFromInput("f(x) = 5");
		GeoCasCell solve = cellFromInput("Solve($2)");
		assertEquals("{x = 64}", solve.getOutput(StringTemplate.defaultTemplate));
	}
}
