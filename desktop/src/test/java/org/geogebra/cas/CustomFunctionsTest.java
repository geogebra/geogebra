package org.geogebra.cas;

import static org.junit.Assert.assertEquals;

import org.geogebra.common.jre.headless.LocalizationCommon;
import org.geogebra.desktop.headless.AppDNoGui;
import org.geogebra.desktop.main.AppD;
import org.junit.Assume;
import org.junit.Test;

public class CustomFunctionsTest {

	private AppDNoGui app = new AppDNoGui(new LocalizationCommon(3), false);

	@Test
	public void testCheckDerivative() throws Throwable {
		Assume.assumeFalse(AppD.MAC_OS);
		String raw = evaluateRaw("check_derivative({(-1/5*√110+1/2*x^2)^2,(1/5*√110+1/2*x^2)^2},"
				+ "point(0,(44/10)))");
		assertEquals("(1/5*√110+1/2*x^2)^2", raw);
		raw = evaluateRaw("check_derivative({(1/5*√110+1/2*x^2)^2,(-1/5*√110+1/2*x^2)^2},"
				+ "point(0,(44/10)))");
		assertEquals("(1/5*√110+1/2*x^2)^2", raw);
		raw = evaluateRaw("check_derivative({(-5/2+1/2*x^2)^2},point(-3,4))");
		assertEquals("(-5/2+1/2*x^2)^2", raw);
		raw = evaluateRaw("check_derivative({(-1/2*(a^2+2*√b)+1/2*x^2)^2,"
				+ "(-1/2*(a^2-2*√b)+1/2*x^2)^2},point(a,b))");
		assertEquals(raw, "(-1/2*(a^2+2*√b)+1/2*x^2)^2");

	}

	private String evaluateRaw(String input) throws Throwable {
		return app.getKernel().getGeoGebraCAS().getCurrentCAS().evaluateRaw(input);
	}
}
