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

package org.geogebra.common.cas;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.geogebra.common.AppCommonFactory;
import org.geogebra.common.cas.error.TimeoutException;
import org.geogebra.common.factories.CASDummy;
import org.geogebra.common.factories.CASFactory;
import org.geogebra.common.jre.headless.AppCommon;
import org.geogebra.common.kernel.CASGenericInterface;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.arithmetic.ValidExpression;
import org.geogebra.test.annotation.Issue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/** Unresolved level comparisons must not be reported as proven empty solution sets. */
@Issue("APPS-7945")
class BinomialStepSolverTest {
	private AppCommon app;
	private CasEvaluation response;
	private TimeoutException timeout;

	@BeforeEach
	void setup() {
		app = AppCommonFactory.create();
		timeout = null;
		response = CasEvaluation.value("{0,0,1,0}");
		app.setCASFactory(new CASFactory() {
			@Override
			public CASGenericInterface newGiac(CASparser parser, Kernel kernel) {
				return new ControlledCAS(parser);
			}
		});
	}

	@Test
	void successfulExactComparisonsProduceAnInterval() throws Exception {
		assertEquals("{1 <= x < 2}", solve());
	}

	@Test
	void inequalitiesUseTheFinitePath() throws Exception {
		response = CasEvaluation.value("{1,1,1,0}");
		assertEquals("{x < 2}", solve("Solve(BinomialDist(2,0.5,x,true)<0.9)"));
		assertEquals("{x < 2}", solve("Solve(BinomialDist(2,0.5,x,true)<=0.9)"));
	}

	@Test
	void inequalitiesCanMatchAllOrNoneOfTheLevels() throws Exception {
		response = CasEvaluation.value("{1,1,1,1}");
		assertEquals("{x = x}", solve("Solve(BinomialDist(2,1/2,x,true)<=1)"));
		response = CasEvaluation.value("{0,0,0,0}");
		assertEquals("{}", solve("Solve(BinomialDist(2,1/2,x,true)<0)"));
	}

	@Test
	void unattainableTargetPreservesUndefinedResult() throws Exception {
		response = CasEvaluation.value("{0,0,0,0}");
		assertEquals("?", solve());
	}

	@Test
	void failedComparisonQueryKeepsTheExistingCasPath() throws Exception {
		response = CasEvaluation.fromGiac("GIAC_ERROR: unsupported input", "?");
		assertNull(solve());
	}

	@Test
	void unresolvedOrMalformedComparisonsDoNotInventSolutions() throws Exception {
		for (String result : new String[] {"?", "{}", "{0,0,maybe,0}", "{0,1,0}", "{1,1,1,1}"}) {
			response = CasEvaluation.value(result);
			assertNull(solve(), result);
		}
	}

	@Test
	void timedOutComparisonQueryDoesNotStartTheRootSearch() throws Exception {
		timeout = new TimeoutException("Thread timeout from Giac");
		assertEquals("?", solve());
	}

	private String solve() throws Exception {
		return solve("Solve(BinomialDist(2,1/2,x,true)=3/4,x)");
	}

	private String solve(String expression) throws Exception {
		Kernel kernel = app.getKernel();
		ValidExpression input = kernel.getParser().parseGeoGebraExpression(expression);
		return BinomialStepSolver.trySolve(kernel, input, input.getTopLevelCommand());
	}

	private final class ControlledCAS extends CASDummy {
		ControlledCAS(CASparser parser) {
			super(parser);
		}

		@Override
		public CasEvaluation evaluateCASResult(String input) {
			if (timeout != null) {
				throw timeout;
			}
			return response;
		}
	}
}
