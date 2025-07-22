package org.geogebra.web.full.gui.view.algebra;

import static org.junit.Assert.assertEquals;

import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.main.settings.AlgebraStyle;
import org.geogebra.web.full.main.AppWFull;
import org.geogebra.web.html5.util.AppletParameters;
import org.geogebra.web.test.AppMocker;
import org.geogebra.web.test.GgbMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(GgbMockitoTestRunner.class)
public class AVLinearNotationTest {

	private AppWFull app;

	@Before
	public void setup() {
		app = AppMocker.mockApplet(new AppletParameters("classic"));
		app.getSettings().getAlgebra().setStyle(AlgebraStyle.LINEAR_NOTATION);
	}

	@Test
	public void testLinearNotationSquareRoot1() {
		processCommand("f(x) = sqrt(x^(1 + 1))");
		assertEquals("f(x) = sqrt(x^(1 + 1))", getTextForLinearNotationItem("f"));
	}

	@Test
	public void testLinearNotationSquareRoot2() {
		processCommand("f(x) = sqrt(x^2)");
		assertEquals("f(x) = sqrt(x^2)", getTextForLinearNotationItem("f"));
	}

	@Test
	public void testLinearNotationCubeRoot() {
		processCommand("f(x) = cbrt(sqrt(x^4))");
		assertEquals("f(x) = cbrt(sqrt(x^4))", getTextForLinearNotationItem("f"));
	}

	@Test
	public void testLinearNotationSimpleMultiplication() {
		processCommand("a = 2 * 3");
		assertEquals("a = 2 * 3", getTextForLinearNotationItem("a"));
	}

	@Test
	public void testLinearNotationMultiplicationWithAddition1() {
		processCommand("b = 2 * (-2 + 1)");
		assertEquals("b = 2 * (-2 + 1)", getTextForLinearNotationItem("b"));
	}

	@Test
	public void testLinearNotationMultiplicationWithAddition2() {
		processCommand("c = 2 * 3  + 2");
		assertEquals("c = 2 * 3 + 2", getTextForLinearNotationItem("c"));
	}

	@Test
	public void testLinearNotationSimpleDivision() {
		processCommand("a = 2 / 3");
		assertEquals("a = 2 / 3", getTextForLinearNotationItem("a"));
	}

	@Test
	public void testLinearNotationDivisionWithSubtraction1() {
		processCommand("b = 2 / 3 - 2");
		assertEquals("b = 2 / 3 - 2", getTextForLinearNotationItem("b"));
	}

	@Test
	public void testLinearNotationDivisionWithSubtraction2() {
		processCommand("c = 2 / (3 - 1)");
		assertEquals("c = 2 / (3 - 1)", getTextForLinearNotationItem("c"));
	}

	@Test
	public void testLinearNotationExtraBracketsForDivision() {
		processCommand("d = 1 / 2 / 3 / 4");
		assertEquals("d = 1 / 2 / 3 / 4", getTextForLinearNotationItem("d"));
	}

	@Test
	public void testLinearNotationNestedMultiplicationNoExtraBrackets() {
		processCommand("d = 2 * 3 * 4");
		assertEquals("d = 2 * 3 * 4", getTextForLinearNotationItem("d"));
	}

	@Test
	public void testLinearNotationPiConstant() {
		processCommand("f(x) = 3 * pi * x");
		assertEquals("f(x) = 3 * pi * x", getTextForLinearNotationItem("f"));
	}

	@Test
	public void testLinearNotationImaginaryUnit() {
		processCommand("a = 3i");
		assertEquals("a = 3 * i", getTextForLinearNotationItem("a"));
	}

	@Test
	public void testLinearNotationComplexNumber() {
		processCommand("z = 2 + 3i");
		assertEquals("z_{1} = 2 + 3 * i", getTextForLinearNotationItem("z_{1}"));
	}

	@Test
	public void testLinearNotationGreaterThan() {
		processCommand("a = 1 > 2");
		assertEquals("a = 1 > 2", getTextForLinearNotationItem("a"));
	}

	@Test
	public void testLinearNotationLessThan() {
		processCommand("b = 2 < 3");
		assertEquals("b = 2 < 3", getTextForLinearNotationItem("b"));
	}

	private void processCommand(String command) {
		app.getKernel().getAlgebraProcessor().processAlgebraCommand(command, false);
	}

	private String getTextForLinearNotationItem(String label) {
		return app.getAlgebraView().getNode(app.getKernel().lookupLabel(label))
				.getTextForEditing(false, StringTemplate.linearNotation);
	}
}