package org.geogebra.common.kernel.algos;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.arithmetic.ExpressionNodeConstants;
import org.geogebra.common.kernel.arithmetic.Function;
import org.geogebra.common.kernel.geos.GeoConic;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.test.TestErrorHandler;
import org.geogebra.test.annotation.Issue;
import org.geogebra.test.commands.AlgebraTestHelper;
import org.junit.Test;

public class AlgoExtremumMultiTest extends BaseUnitTest {

	@Test
	public void extremumShouldNotOverwriteVariables() {
		GeoConic conic = add("f:y=4-x^2");
		Function eval = conic.getFunction();
		assertEquals(0, eval.value(2), Kernel.STANDARD_PRECISION);
		add("Extremum(f)");
		assertEquals(0, eval.value(2), Kernel.STANDARD_PRECISION);
	}

	@Test
	@Issue("APPS-6429")
	public void noExtremaShouldBeFoundOnConstantFunction() {
		GeoElementND[] extremum = getAlgebraProcessor().processAlgebraCommandNoExceptionHandling(
				"Extremum(cos(x)+abs(cos(x)),1,5)", false,
				TestErrorHandler.INSTANCE, false, null);
		assertEquals(1, extremum.length);
		assertFalse("Extremum should be undefined", extremum[0].isDefined());
	}

	@Test
	@Issue("APPS-5159")
	public void cmdExtremumHighDeg() {
		long time = System.currentTimeMillis();
		StringTemplate lowPrecision = StringTemplate.printDecimals(
				ExpressionNodeConstants.StringType.GEOGEBRA, 2, false);
		t("Extremum((x+1)^24)", "(-1, 0)");
		GeoElement extremum = add("Extremum((x+1)^98)");
		assertEquals("(-1, 0)", extremum.toValueString(lowPrecision));
		t("y(Extremum((x+1)^98+1))", "NaN");
		assertTrue(System.currentTimeMillis() - time < 1000);
	}

	protected void t(String input, StringTemplate tpl, String... expected) {
		AlgebraTestHelper.checkSyntaxSingle(input, expected,
				getApp().getKernel().getAlgebraProcessor(),
				tpl);
	}
}
