package org.geogebra.common.kernel.algos;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.geogebra.common.AppCommonFactory;
import org.geogebra.common.jre.headless.AppCommon;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoInputBox;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.kernel.prover.AlgoAreEqual;
import org.junit.BeforeClass;
import org.junit.Test;

import com.himamis.retex.editor.share.util.Unicode;

public class AlgoAreEqualTest {

	private static AppCommon app;
	private static Construction cons;

	/**
	 * Setup the app
	 */
	@BeforeClass
	public static void setup() {
		app = AppCommonFactory.create();
		cons = app.getKernel().getConstruction();
	}

	@Test
	public void simpleAlgebraicExpressions() {
		assertTrue(compare("0", "0"));
		assertTrue(compare("2/4", "1/2"));
		assertFalse(compare("0.3333333333333333", "1/3"));
		assertTrue(compare("pi/4", "atan(1)"));
		assertFalse(compare("sin(45" + Unicode.DEGREE_STRING + ")", "1/2"));
		assertTrue(compare("sin(45" + Unicode.DEGREE_STRING + ")", "sqrt(2)/2"));
		assertTrue(compare("cos(30" + Unicode.DEGREE_STRING + ")", "sqrt(3)/2"));
		assertTrue(compare("sqrt(128)", "sqrt(128)"));
		assertTrue(compare("2*sqrt(32)", "sqrt(128)"));
		assertFalse(compare("11.3137084989848", "sqrt(128)"));
		assertTrue(compare("4*sqrt(8)", "sqrt(128)"));
		assertTrue(compare("1/sqrt(2)", "sqrt(2)/2"));
		assertFalse(compare("0.707106781186547", "sqrt(2)"));
		assertFalse(compare("1.23456789012346", "1.23456789012346"));
		assertTrue(compare("12345678", "12345678"));
		assertFalse(compare("123456789", "123456789"));
		assertTrue(compare("10000000", "10000000"));
		assertFalse(compare("100000000", "100000000"));
		assertTrue(compare("1.23", "1.23"));
	}

	@Test
	public void largeNumbers() {
		assertTrue(compare("(73205*pi)/6", "73205*(pi/6)"));
		assertTrue(compare("(73205*pi)/6", "(73205/6)*pi"));
		assertTrue(compare("73205*(pi/6)", "(73205/6)*pi"));
	}

	@Test
	public void expressionsContainingVariables() {
		assertFalse(compare("1+a", "2"));
		GeoNumeric n = new GeoNumeric(cons, 1);
		n.setLabel("a");
		assertFalse(compare("1+a", "2"));
	}

	@Test
	public void incorrectSyntax() {
		assertFalse(compare("1+", "1"));
	}

	private boolean compare(String inputBoxContent, String expression) {
		GeoNumeric linkedGeo = new GeoNumeric(cons);
		GeoInputBox inputBox = new GeoInputBox(cons);
		inputBox.setLinkedGeo(linkedGeo);
		inputBox.updateLinkedGeo(inputBoxContent);

		GeoElementND[] parsed = app.getKernel().getAlgebraProcessor()
				.processAlgebraCommand(expression, false);

		AlgoAreEqual areEqual = new AlgoAreEqual(cons, inputBox, (GeoElement) parsed[0]);

		return areEqual.getResult().getBoolean();
	}
}
