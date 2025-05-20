package org.geogebra.common.kernel.cas;

import static org.junit.Assert.assertEquals;

import org.geogebra.common.AppCommonFactory;
import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.GeoGebraConstants;
import org.geogebra.common.SuiteSubApp;
import org.geogebra.common.cas.MockedCasGiac;
import org.geogebra.common.gui.view.algebra.EvalInfoFactory;
import org.geogebra.common.jre.headless.AppCommon;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.commands.AlgebraProcessor;
import org.geogebra.common.kernel.commands.EvalInfo;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.main.settings.AlgebraSettings;
import org.geogebra.common.main.settings.config.AppConfigCas;
import org.geogebra.test.commands.ErrorAccumulator;
import org.junit.Before;
import org.junit.Test;

public class AlgoSolveTest {
	private AppCommon app;
	private AlgebraProcessor algebraProcessor;
	private final MockedCasGiac mockedCasGiac = new MockedCasGiac();

	@Before
	public void setupCas() {
		app = AppCommonFactory.create(new AppConfigCas(GeoGebraConstants.SUITE_APPCODE));
		mockedCasGiac.applyTo(app);
		algebraProcessor = app.getKernel().getAlgebraProcessor();
	}

	@Test
	public void shouldNotSendTooManyZerosToCas() {
		mockedCasGiac.memorize("Evaluate(5.4sin((2π) / 365 (x - 75)) + 12)",
				"27/5*sin(2/365*pi*(x-75))+12");
		mockedCasGiac.memorize("NSolve(27 / 5 sin(2 / 365 π (x - 75)) + 12 = 17.4)",
				"{x=166.2499880055,x=166.2500119945}");
		mockedCasGiac.memorize("Solve(27 / 5 sin(2 / 365 π (x - 75)) + 12 = 17.4, x)",
				"{x=(365*arbint(0)+665/4)}");
		evaluate("f(x)=5.4 sin(((2 π)/(365)) (x-75))+12");
		GeoElementND solve = evaluate("NSolve(f(x)=17.4)");
		assertEquals("{x = 166.25, x = 166.25}",
				solve.toValueString(StringTemplate.defaultTemplate));
	}

	private GeoElement evaluate(String expression) {
		EvalInfo evalInfo = EvalInfoFactory.getEvalInfoForAV(app, false);
		return (GeoElement) algebraProcessor.processAlgebraCommandNoExceptionHandling(
				expression, false, new ErrorAccumulator(), evalInfo, null)[0];
	}
}