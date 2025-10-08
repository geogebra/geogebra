package org.geogebra.common.kernel.commands;

import static org.geogebra.common.BaseUnitTest.hasValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Map;
import java.util.Objects;

import org.geogebra.common.AppCommonFactory;
import org.geogebra.common.cas.CASparser;
import org.geogebra.common.factories.CASFactory;
import org.geogebra.common.jre.headless.AppCommon;
import org.geogebra.common.kernel.CASGenericInterface;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.arithmetic.SymbolicMode;
import org.geogebra.common.kernel.geos.GeoCasCell;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.geos.GeoSymbolic;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.main.settings.config.AppConfigCas;
import org.geogebra.desktop.cas.giac.CASgiacD;
import org.geogebra.test.annotation.Issue;
import org.junit.Before;
import org.junit.Test;

public class DelayedCasLoadingTest {

	private AppCommon app;
	private static boolean active = false;
	private boolean casInitialized = false;

	/**
	 * Init app and the delayed CAS
	 */
	@Before
	public void init() {
		active = false;
		casInitialized = false;
		app = AppCommonFactory.create3D();
		CASFactory factory = new CASFactory() {
			@Override
			public CASGenericInterface newGiac(CASparser parser, Kernel kernel) {
				casInitialized = true;
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
	@Issue("APPS-6541")
	public void finiteIntegralShouldUpdateAfterLoad() {
		GeoElementND integral = add("Integral(exp(-x),1,inf)");
		assertEquals("?", integral.toValueString(StringTemplate.testTemplate));
		active = true;
		app.getKernel().refreshCASCommands();
		assertEquals("0.36788", integral.toValueString(StringTemplate.editTemplate));
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
	public void symbolicShouldNotChangeCoordToMultiplication() {
		app.setConfig(new AppConfigCas());
		app.getKernel().setSymbolicMode(SymbolicMode.SYMBOLIC_AV);
		add("A=(1,2,3)");
		add("m1={{x-x(A),y-y(A),z-z(A)},{1,2,3},{0,-1,1}}");
		GeoSymbolic det = (GeoSymbolic) add("eq:Determinant(m1)=0");
		active = true;
		app.getKernel().refreshCASCommands();
		assertThat(det.getTwinGeo(), hasValue("5x - y - z = 0"));
		assertThat(app.getKernel().lookupLabel("m1"),
				hasValue("{{x - 1, y - 2, z - 3}, {1, 2, 3}, {0, -1, 1}}"));
		assertThat(app.getKernel().lookupLabel("eq"), hasValue("5x - y - z = 0"));
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

	@Test
	public void simplifyShouldTriggerLoad() {
		GeoFunction f = (GeoFunction) add("f(x)=x+x");
		Objects.requireNonNull(f.getFunction()).updateCASEvalMap(
				Map.of("Simplify[x + x]", "(3 * x)"));
		GeoElementND simplified = add("Simplify(f)");
		assertEquals("3x",
				simplified.toValueString(StringTemplate.defaultTemplate));
		assertTrue("CAS should be loaded", casInitialized);
		active = true;
		app.getKernel().refreshCASCommands();
		assertEquals("2x",
				simplified.toValueString(StringTemplate.defaultTemplate));
	}

	@Test
	@Issue("APPS-6872")
	public void savedArbitraryConstantShouldBeStoredWithinConstruction() {
		app.setConfig(new AppConfigCas());
		app.getKernel().setSymbolicMode(SymbolicMode.SYMBOLIC_AV);
		app.setXML("<geogebra><construction>"
				+ "<expression label=\"f\" exp=\"SolveODE(y&apos; + (2 * y) = 4)\"/>\n"
				+ "<element type=\"symbolic\" label=\"f\">\n\t<show object=\"true\""
				+ "label=\"true\"/>\n\t<labelMode val=\"0\"/>\n\t<variables val=\"y\"/>\n"
				+ "</element>\n<element type=\"numeric\" label=\"c_{1}\">\n\t<value val=\"1\"/>\n"
				+ "\t<slider min=\"-5\" max=\"5\" absoluteScreenLocation=\"true\" width=\"200\""
				+ " fixed=\"false\" horizontal=\"true\" showAlgebra=\"true\""
				+ "arbitraryConstant=\"true\"/>\n\t<show object=\"false\" label=\"true\"/>\n"
				+ "\t<labelMode val=\"1\"/>\n</element></construction></geogebra>", true);
		assertEquals(1, app.getKernel().getConstruction().getUnclaimedArbitraryConstants().size());
		active = true;
		app.getKernel().refreshCASCommands();
		assertArrayEquals(new String[]{"f", "c_{1}"}, app.getGgbApi().getAllObjectNames());
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
