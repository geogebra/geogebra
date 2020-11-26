package org.geogebra.common.kernel.commands;

import static org.junit.Assert.assertEquals;

import org.geogebra.common.AppCommonFactory;
import org.geogebra.common.cas.CASparser;
import org.geogebra.common.factories.CASFactory;
import org.geogebra.common.jre.headless.AppCommon;
import org.geogebra.common.kernel.CASGenericInterface;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.desktop.cas.giac.CASgiacD;
import org.junit.Before;
import org.junit.Test;

public class DelayedCasLoadingTest {

	private AppCommon app;
	private static boolean active = false;

	/**
	 * Init app and the delayed CAS
	 */
	@Before
	public void init() {
		active = false;
		app = AppCommonFactory.create();
		CASFactory factory = new CASFactory() {
			@Override
			public CASGenericInterface newGiac(CASparser parser, Kernel kernel) {
				return new DelayedCasGiacD(parser);
			}
		};
		app.setCASFactory(factory);
	}

	@Test
	public void derivativeShouldUpdateAfterLoad() {
		GeoElementND derivative = add("Derivative(sin(x))");
		assertEquals("?", derivative.toValueString(StringTemplate.testTemplate));
		active = true;
		app.getKernel().refreshCASCommands();
		assertEquals("cos(x)", derivative.toValueString(StringTemplate.testTemplate));
	}

	@Test
	public void fPrimeShouldUpdateAfterLoad() {
		add("f(x)=sin(x)");
		GeoElementND derivative = add("f'(x)");
		assertEquals("?", derivative.toValueString(StringTemplate.testTemplate));
		active = true;
		app.getKernel().refreshCASCommands();
		assertEquals("cos(x)", derivative.toValueString(StringTemplate.testTemplate));
	}

	@Test
	public void equalsShouldUpdateAfterLoad() {
		add("f(x)=sin(x)+1+sin(x)");
		add("g(x)=2sin(x)+1");
		GeoElementND derivative = add("f==g");
		assertEquals("false", derivative.toValueString(StringTemplate.testTemplate));
		active = true;
		app.getKernel().refreshCASCommands();
		assertEquals("true", derivative.toValueString(StringTemplate.testTemplate));
	}

	private GeoElementND add(String s) {
		return app.getKernel().getAlgebraProcessor().processAlgebraCommand(s, false)[0];
	}

	private static class DelayedCasGiacD extends CASgiacD {
		public DelayedCasGiacD(CASparser parser) {
			super(parser);
		}

		@Override
		public String evaluateCAS(String exp) {
			return active ? super.evaluateCAS(exp) : "?";
		}

		@Override
		protected String evaluate(String exp, long timeoutMilliseconds)
				throws Throwable {
			return active ? super.evaluate(exp, timeoutMilliseconds) : "?";
		}
	}
}
