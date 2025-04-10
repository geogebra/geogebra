package org.geogebra.common.kernel.arithmetic.filter;

import static org.geogebra.common.plugin.Operation.COS;
import static org.geogebra.common.plugin.Operation.RANDOM;
import static org.geogebra.common.plugin.Operation.SIN;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.Set;

import org.geogebra.common.AppCommonFactory;
import org.geogebra.common.GeoGebraConstants;
import org.geogebra.common.SuiteSubApp;
import org.geogebra.common.cas.MockCASGiac;
import org.geogebra.common.gui.view.algebra.EvalInfoFactory;
import org.geogebra.common.jre.headless.AppCommon;
import org.geogebra.common.kernel.commands.AlgebraProcessor;
import org.geogebra.common.kernel.commands.EvalInfo;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.main.AppConfig;
import org.geogebra.common.main.settings.config.AppConfigCas;
import org.geogebra.common.main.settings.config.AppConfigGeometry;
import org.geogebra.common.main.settings.config.AppConfigGraphing3D;
import org.geogebra.common.main.settings.config.AppConfigProbability;
import org.geogebra.common.main.settings.config.AppConfigScientific;
import org.geogebra.common.main.settings.config.AppConfigUnrestrictedGraphing;
import org.geogebra.test.commands.ErrorAccumulator;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

public class OperationFilterTests {
	private AppCommon app;
	private AlgebraProcessor algebraProcessor;
	private MockCASGiac mockCASGiac;

	private final OperationFilter operationFilter =
			operation -> !Set.of(RANDOM, SIN, COS).contains(operation);
	private final ExpressionFilter operationExpressionFilter =
			new DeepExpressionFilter(operationFilter.toExpressionFilter());

	private void setupApp(SuiteSubApp subApp) {
		app = AppCommonFactory.create(createConfig(subApp));
		mockCASGiac = subApp == SuiteSubApp.CAS ? new MockCASGiac(app) : null;
		algebraProcessor = app.getKernel().getAlgebraProcessor();
		algebraProcessor.addInputExpressionFilter(operationExpressionFilter);
		algebraProcessor.addOutputExpressionFilter(operationExpressionFilter);
	}

	@ParameterizedTest
	@ValueSource(strings = {
			"1 + 2",
			"sqrt(3)^3",
			"tan(x)",
			"cotan(pi / 4)",
			"arccos(pi / 4)",
	})
	public void testAllowedExpressionsInGraphing(String expression) {
		setupApp(SuiteSubApp.GRAPHING);
		assertNotNull(evaluate(expression));
	}

	@ParameterizedTest
	@ValueSource(strings = {
			"random()",
			"1 + random()",
			"1 + 2 / sqrt(1 - random())",
			"sin(pi / 2)",
			"cos(1)",
			"1 / cos(pi / 6)",
			"{ cos(1) }"
	})
	public void testRestrictedExpressionsInGraphing(String expression) {
		setupApp(SuiteSubApp.GRAPHING);
		assertNull(evaluate(expression));
	}

	@ParameterizedTest
	@CsvSource({
			"1 + 2,     3",
			"tan(x),    tan(x)",
			"sqrt(3),   sqrt(3)",
	})
	public void testAllowedExpressionsInCas(String expression, String mockedCasOutput) {
		setupApp(SuiteSubApp.CAS);
		assertNotNull(evaluate(expression, mockedCasOutput));
	}

	@ParameterizedTest
	@CsvSource({
			"1 / random(),  3052978 / 2737471",
			"sin(pi / 2),   1",
			"cotan(x),      cos(x) / sin(x)"
	})
	public void testRestrictedExpressionsInCas(String expression, String mockedCasOutput) {
		setupApp(SuiteSubApp.CAS);
		assertNull(evaluate(expression, mockedCasOutput));
	}

	private AppConfig createConfig(SuiteSubApp subApp) {
		switch (subApp) {
		case CAS:
			return new AppConfigCas(GeoGebraConstants.SUITE_APPCODE);
		case GRAPHING:
			return new AppConfigUnrestrictedGraphing(GeoGebraConstants.SUITE_APPCODE);
		case GEOMETRY:
			return new AppConfigGeometry(GeoGebraConstants.SUITE_APPCODE);
		case SCIENTIFIC:
			return new AppConfigScientific(GeoGebraConstants.SUITE_APPCODE);
		case G3D:
			return new AppConfigGraphing3D(GeoGebraConstants.SUITE_APPCODE);
		case PROBABILITY:
			return new AppConfigProbability(GeoGebraConstants.SUITE_APPCODE);
		}
		return null;
	}

	private GeoElementND[] evaluate(String expression, String... mockedCasOutputs) {
		if (app.getConfig().getSubApp() == SuiteSubApp.CAS && mockCASGiac != null) {
			for (String mockedCasOutput : mockedCasOutputs) {
				mockCASGiac.memorize(mockedCasOutput);
			}
		}
		EvalInfo evalInfo = EvalInfoFactory.getEvalInfoForAV(app, false);
		return algebraProcessor.processAlgebraCommandNoExceptionHandling(
				expression, false, new ErrorAccumulator(), evalInfo, null);
	}

	private GeoElementND[] evaluate(String expression) {
		return evaluate(expression, expression);
	}
}
