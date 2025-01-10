package org.geogebra.common.kernel.arithmetic;

import static org.geogebra.common.kernel.arithmetic.bernstein.BernsteinPolynomialConverter.iSupported;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.geogebra.common.BaseUnitTest;
import org.junit.Test;

public class BernsteinPolynomialConverterTest extends BaseUnitTest {
	@Test
	public void testSupported() {
		shouldSupport("x^3+2xy^2 + 1=0");
		shouldSupport("x^3y + 2xy + 1=0");
		shouldSupport("(x^2 + y^2 - 1)^3 = x^2y^3");
		shouldNotSupport("sin(x) + cos(x) = 1");
		shouldNotSupport("sin^3(x) + (2cos(x))/5 = 1");
		shouldNotSupport("abs(x^3y^4)= 1");
	}

	private void shouldSupport(String command) {
		assertTrue(isSupported(command));
	}

	private void shouldNotSupport(String command) {
		assertFalse(isSupported(command));
	}

	private boolean isSupported(String command) {
		return iSupported(add(command));
	}
}
