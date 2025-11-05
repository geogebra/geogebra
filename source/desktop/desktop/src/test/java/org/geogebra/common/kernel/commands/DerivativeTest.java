package org.geogebra.common.kernel.commands;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.jre.headless.AppCommon;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.util.debug.Log;
import org.geogebra.editor.share.util.Unicode;
import org.geogebra.test.annotation.Issue;
import org.junit.Test;

/**
 * Tests for derivatives.
 */
public class DerivativeTest extends BaseUnitTest {

	@Override
	public AppCommon createAppCommon() {
		return AlgebraTest.createApp();
	}

	@Test
	public void testNoCasDerivative() {
		getApp().enableCAS(false);
		t("Derivative(sin(x))", "NDerivative[sin(x)]");
	}

	@Test
	public void differentDerivativeCharsShouldReproduceDerivative() {
		getApp().enableCAS(false);
		add("f = x*x");
		add("g(x) = f‘(x)");
		add("h(x) = f’(x)");

		GeoFunction g = (GeoFunction) getKernel().lookupLabel("g");
		assertEquals("NDerivative(f)",
				g.getFunction().toString(StringTemplate.defaultTemplate));

		GeoFunction h = (GeoFunction) getKernel().lookupLabel("h");
		assertEquals("NDerivative(f)",
				h.getFunction().toString(StringTemplate.defaultTemplate));
	}

	@Test
	@Issue("APPS-5662")
	public void fastDerivativeWithMixedNumbers() {
		add("f:NDerivative(7x+1" + Unicode.INVISIBLE_PLUS + "2/3)");
		t("f(5)", "7");
	}

	@Test
	public void derivativeWithVar() {
		add("f(u,v)=2u^2+3v^2");
		add("g:Derivative(f,v)");
		assertThat(lookup("g"), hasValue("6v"));
		Log.setLogger(new Log() {
			@Override
			public void print(Level level, Object logMessage) {
				if (logMessage instanceof Throwable) {
					throw new RuntimeException((Throwable) logMessage);
				}
				System.out.println(logMessage);
			}
		});
		reload();

		assertThat(lookup("g"), hasValue("6v"));
	}

	@Test
	public void firstDerivativeResultShouldBeFactorised() {
		add("f(x) = 1 / (x-1)");
		t("Derivative(f)", "-1 / (x - 1)^(2)");
		t("Derivative(f, x)", "-1 / (x - 1)^(2)");
		t("Derivative(f, x, 1)", "-1 / (x - 1)^(2)");
		t("f'(x)", "-1 / (x - 1)^(2)");
		// Special case: Result of (1st Derivative of 1st Derivative) should also be factorised
		t("Derivative(Derivative(f))", "2 / (x - 1)^(3)");
	}

	@Test
	public void secondDerivativeResultShouldNotBeFactorised() {
		add("f(x) = 1 / (x-1)");
		t("Derivative(f, x, 2)", "2 / (x^(3) - (3 * x^(2)) + (3 * x) - 1)");
		t("f''(x)", "2 / (x^(3) - (3 * x^(2)) + (3 * x) - 1)");
	}
}
