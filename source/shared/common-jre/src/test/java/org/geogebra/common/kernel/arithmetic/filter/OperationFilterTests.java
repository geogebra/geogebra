/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 * 
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 * 
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

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
import org.geogebra.common.cas.MockedCasGiac;
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
import org.geogebra.common.util.MockedCasValues;
import org.geogebra.common.util.MockedCasValuesExtension;
import org.geogebra.test.commands.ErrorAccumulator;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

@SuppressWarnings("checkstyle:RegexpSinglelineCheck") // Tabs in MockedCasValues
@ExtendWith(MockedCasValuesExtension.class)
public class OperationFilterTests {
	private AppCommon app;
	private AlgebraProcessor algebraProcessor;
	private final MockedCasGiac mockedCasGiac = new MockedCasGiac();

	private final OperationFilter operationFilter =
			operation -> !Set.of(RANDOM, SIN, COS).contains(operation);
	private final ExpressionFilter operationExpressionFilter =
			new DeepExpressionFilter(operationFilter.toExpressionFilter());

	private void setupApp(SuiteSubApp subApp) {
		app = AppCommonFactory.create(createConfig(subApp));
		if (subApp == SuiteSubApp.CAS) {
			mockedCasGiac.applyTo(app);
		}
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
	@ValueSource(strings = {
			"1 + 2",
			"tan(x)",
			"sqrt(3)",
	})
	@MockedCasValues({
			"Evaluate(1 + 2) 	-> 3",
			"Round(3, 2) 		-> 3.0",
			"Evaluate(tan(x)) 	-> tan(x)",
			"Evaluate(sqrt(3)) 	-> √3",
			"Round(sqrt(3), 2) 	-> 1.73",
	})
	public void testAllowedExpressionsInCas(String expression) {
		setupApp(SuiteSubApp.CAS);
		assertNotNull(evaluate(expression));
	}

	@ParameterizedTest
	@ValueSource(strings = {
			"1 / random()",
			"sin(pi / 2)",
			"cotan(x)",
	})
	@MockedCasValues({
			"Evaluate(1 / 0.54) 			-> 7038785/3278052",
			"Round(7038785 / 3278052, 2) 	-> 2.15",
			"Evaluate(sin(π / 2)) 			-> 1",
			"Round(1, 2) 					-> 1.0",
			"Evaluate(cot(x)) 				-> cos(x)/sin(x)",
	})
	public void testRestrictedExpressionsInCas(String expression) {
		setupApp(SuiteSubApp.CAS);
		assertNull(evaluate(expression));
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

	private GeoElementND[] evaluate(String expression) {
		EvalInfo evalInfo = EvalInfoFactory.getEvalInfoForAV(app, false);
		return algebraProcessor.processAlgebraCommandNoExceptionHandling(
				expression, false, new ErrorAccumulator(), evalInfo, null);
	}
}
