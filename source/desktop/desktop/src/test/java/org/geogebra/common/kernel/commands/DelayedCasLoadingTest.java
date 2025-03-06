package org.geogebra.common.kernel.commands;

import static org.geogebra.common.BaseUnitTest.hasValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertEquals;

import org.geogebra.common.AppCommonFactory;
import org.geogebra.common.cas.CASparser;
import org.geogebra.common.factories.CASFactory;
import org.geogebra.common.jre.headless.AppCommon;
import org.geogebra.common.kernel.CASGenericInterface;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.arithmetic.SymbolicMode;
import org.geogebra.common.kernel.geos.GeoCasCell;
import org.geogebra.common.kernel.geos.GeoSymbolic;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.main.settings.config.AppConfigCas;
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
		GeoElementND equalCheck = add("f==g");
		GeoElementND equalCommand = add("AreEqual(f, g)");
		assertEquals("?", equalCheck.toValueString(StringTemplate.testTemplate));
		assertEquals("false", equalCommand.toValueString(StringTemplate.testTemplate));
		active = true;
		app.getKernel().refreshCASCommands();
		assertEquals("true", equalCheck.toValueString(StringTemplate.testTemplate));
		assertEquals("true", equalCommand.toValueString(StringTemplate.testTemplate));
	}

	@Test
	public void casCellsShouldUpdateAfterLoad() {
		GeoCasCell f = new GeoCasCell(app.getKernel().getConstruction());
		app.getKernel().getConstruction().addToConstructionList(f, false);
		add("m=1");
		add("M=1.2");
		f.setInput("eq1:77/10=M v_{M}+m (v cos(t)+v_{M})");
		f.computeOutput();
		assertEquals("eq1:=?", f.getOutput(StringTemplate.testTemplate));
		active = true;
		app.getKernel().refreshCASCommands();
		assertEquals("eq1: 77 / 10 = v * cos(t) + 11 / 5 * v_{M}",
				f.getOutput(StringTemplate.testTemplate));
	}

	@Test
	public void symbolicShouldNotSwitchSymbolicFlag() {
		app.setConfig(new AppConfigCas());
		app.getKernel().setSymbolicMode(SymbolicMode.SYMBOLIC_AV);
		GeoSymbolic sym = (GeoSymbolic) add("p=IsPrime(4)");
		assertThat(sym.isSymbolicMode(), equalTo(true));
		assertThat(sym, hasValue("?"));
		active = true;
		app.getKernel().refreshCASCommands();
		assertThat(sym, hasValue("false"));
		app.setXML(app.getXML(), true);
		assertThat(app.getKernel().lookupLabel("p"), hasValue("false"));
	}

	@Test
	public void asymptoteOfAnonymousFnShouldUpdate() {
		add("f(x)=x+1/x");
		GeoElementND asymptotes = add("Asymptote(f(x))");
		active = true;
		app.getKernel().refreshCASCommands();
		assertThat(asymptotes, hasValue("{y = x, x = 0}"));
	}

	@Test
	public void zipSolveShouldWorkAfterLoad() {
		add("l1=Solve(x^2=1)");
		add("l2=NSolve(x^2=1)");
		add("l3=PlotSolve(x^2=1)");
		GeoElementND solve = add("Zip(RightSide(t),t,l1)");
		GeoElementND nSolve = add("Zip(RightSide(t),t,l2)");
		GeoElementND plotSolve = add("Zip(Length(t),t,l3)");
		active = true;
		app.getKernel().refreshCASCommands();
		assertEquals("{-1, 1}", solve.toValueString(StringTemplate.testTemplate));
		assertEquals("{-1, 1}", nSolve.toValueString(StringTemplate.testTemplate));
		assertEquals("{1, 1}", plotSolve.toValueString(StringTemplate.testTemplate));
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

		@Override
		public boolean isLoaded() {
			return active;
		}
	}
}
