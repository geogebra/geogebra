package org.geogebra.common.kernel.commands;

import org.junit.Test;

/**
 * Tests for derivatives.
 */
public class DerivativeTest extends AlgebraTest {

	@Test
	public void testNoCasDerivative() {
		app.enableCAS(false);
		t("Derivative(sin(x))", "NDerivative[sin(x)]");
	}
}
