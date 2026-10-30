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

package org.geogebra.common.kernel.geos;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.util.ArrayList;
import java.util.List;

import org.geogebra.common.AppCommonFactory;
import org.geogebra.common.cas.CASparser;
import org.geogebra.common.cas.CasEvaluation;
import org.geogebra.common.factories.CASDummy;
import org.geogebra.common.factories.CASFactory;
import org.geogebra.common.jre.headless.AppCommon;
import org.geogebra.common.kernel.CASGenericInterface;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.arithmetic.SymbolicMode;
import org.geogebra.common.kernel.arithmetic.ValidExpression;
import org.geogebra.common.main.settings.config.AppConfigCas;
import org.geogebra.common.util.SymbolicUtil;
import org.geogebra.test.TestErrorHandler;
import org.geogebra.test.annotation.Issue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/** Exercise fallback decisions independently of platform-dependent Giac timings. */
@Issue("APPS-7945")
class CasFallbackTest {
	private final List<String> attempts = new ArrayList<>();
	private AppCommon app;
	private String nsolveError;

	@BeforeEach
	void setup() {
		app = AppCommonFactory.create(new AppConfigCas());
		app.setCASFactory(new CASFactory() {
			@Override
			public CASGenericInterface newGiac(CASparser parser, Kernel kernel) {
				return new FailingCAS(parser);
			}
		});
		app.getKernel().setSymbolicMode(SymbolicMode.SYMBOLIC_AV);
		nsolveError = "GIAC_ERROR: diff of sum with boundaries or mute variable depending"
				+ " on differentiation variable Error: Bad Argument Value";
	}

	@Test
	void unsupportedNSolveIsNotRepeatedInsideNumeric() {
		GeoSymbolic result = solve();
		assertEquals("?", result.toValueString(StringTemplate.testTemplate));
		assertEquals(1, attempts.stream().filter("NSolve"::equals).count());
		assertEquals(0, attempts.stream().filter("Numeric(NSolve)"::equals).count());
		assertEquals(2, attempts.stream().filter("Numeric(Solve)"::equals).count());
	}

	@Test
	void unknownNSolveErrorRetainsNumericRecovery() {
		nsolveError = "GIAC_ERROR: unrelated";
		solve();
		assertEquals(2, attempts.stream().filter("Numeric(NSolve)"::equals).count());
	}

	@Test
	void undefinedWithoutRecognizedErrorRetainsNumericRecovery() {
		nsolveError = "?";
		solve();
		assertEquals(2, attempts.stream().filter("Numeric(NSolve)"::equals).count());
	}

	@Test
	void repeatedFormatChecksDoNotEvaluateOppositeOfUndefinedResult() {
		GeoSymbolic result = solve();
		attempts.clear();
		assertFalse(SymbolicUtil.isSymbolicSolveDiffers(result));
		assertFalse(SymbolicUtil.isSymbolicSolveDiffers(result));
		assertEquals(List.of(), attempts);
	}

	private GeoSymbolic solve() {
		return (GeoSymbolic) app.getKernel()
				.getAlgebraProcessor()
				.processAlgebraCommandNoExceptionHandling(
						"Solve(x^2=2)", false, TestErrorHandler.INSTANCE, false, null)[0];
	}

	private final class FailingCAS extends CASDummy {
		FailingCAS(CASparser parser) {
			super(parser);
		}

		@Override
		protected CasEvaluation translateAndEvaluateCAS(ValidExpression input, StringTemplate tpl) {
			Command command = input.getTopLevelCommand();
			String name = command.getName();
			if ("Numeric".equals(name)) {
				name += "(" + command.getArgument(0).getTopLevelCommand().getName() + ")";
			}
			attempts.add(name);
			return "NSolve".equals(name)
					? CasEvaluation.fromGiac(nsolveError, "?")
					: CasEvaluation.value("?");
		}
	}
}
