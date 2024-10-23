package org.geogebra.common.kernel.commands;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertFalse;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.jre.headless.AppCommon;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.util.debug.Log;
import org.geogebra.test.annotation.Issue;
import org.junit.Assert;
import org.junit.Test;

import com.himamis.retex.editor.share.util.Unicode;

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
		Assert.assertEquals("NDerivative(f)",
				g.getFunction().toString(StringTemplate.defaultTemplate));

		GeoFunction h = (GeoFunction) getKernel().lookupLabel("h");
		Assert.assertEquals("NDerivative(f)",
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
				assertFalse(logMessage instanceof Throwable);
				System.out.println(logMessage);
			}
		});
		reload();

		assertThat(lookup("g"), hasValue("6v"));
	}

}
