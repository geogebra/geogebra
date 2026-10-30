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

import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.geogebra.common.cas.BinomialStepSolver;
import org.geogebra.common.gui.view.algebra.AlgebraItem;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.arithmetic.ValidExpression;
import org.geogebra.common.util.SymbolicUtil;
import org.geogebra.common.util.debug.Log;
import org.geogebra.test.annotation.Issue;
import org.junit.jupiter.api.Test;

/** Native regression coverage for the automatic binomial Solve fallback. */
@Issue("APPS-7945")
class BinomialSolveFallbackTest extends BaseSymbolicTest {
	@Test
	void unattainableProbabilityAvoidsAllRootSearches() {
		Log previous = Log.getLogger();
		CapturingLog log = new CapturingLog();
		log.setLogLevel(Log.Level.DEBUG);
		Log.setLogger(log);
		try {
			GeoSymbolic result = add("Solve(BinomialDist(2,0.5,x,true)=0.9)");
			assertEquals("?", result.toValueString(StringTemplate.testTemplate));
			assertTrue(result.isBinomialStepResult());
			t("NSolve(BinomialDist(2,0.5,x,true)=0.9)", "?");
			assertTrue(log.messages.stream().anyMatch(s -> s.contains("giac evalRaw input: ")));
			assertTrue(
					log.messages.stream().filter(s -> s.contains("giac evalRaw output: ")).count() <= 2);
			assertFalse(log.messages.stream()
					.anyMatch(s -> s.contains("giac evalRaw input: ")
							&& (s.contains("fsolve(") || s.contains("solve("))));
			result.getTwinGeo();
			((GeoSymbolic) result.copy()).getTwinGeo();
			assertFalse(log.messages.stream()
					.anyMatch(s -> s.contains("giac evalRaw input: ")
							&& (s.contains("fsolve(") || s.contains("solve("))));
			log.messages.clear();
			assertFalse(SymbolicUtil.isSymbolicSolveDiffers(result));
			assertFalse(SymbolicUtil.isSymbolicSolveDiffers(result));
			assertFalse(log.messages.stream().anyMatch(s -> s.contains("giac evalRaw input: ")));
		} finally {
			Log.setLogger(previous);
		}
	}

	@Test
	void renderingAndFormatChecksDoNotReevaluateIntervals() {
		GeoSymbolic result = add("Solve(BinomialDist(2,1/2,x,true)=3/4,x)");
		Log previous = Log.getLogger();
		CapturingLog log = new CapturingLog();
		log.setLogLevel(Log.Level.DEBUG);
		Log.setLogger(log);
		try {
			result.getTwinGeo();
			((GeoSymbolic) result.copy()).getTwinGeo();
			assertFalse(AlgebraItem.isSymbolicDiffers(result));
			SymbolicUtil.handleSolveNSolve(result);
			assertFalse(log.messages.stream().anyMatch(s -> s.contains("giac evalRaw input: ")));
		} finally {
			Log.setLogger(previous);
		}
	}

	@Test
	void inequalitiesAndEquivalentExpressionsAvoidRootSearches() {
		String cdf = "BinomialDist(2,0.5,x,true)";
		String[] expressions = {
			cdf + "<0.9", cdf + "<=0.9", cdf + "-0.9", "0.5=" + cdf, "2*" + cdf + "=1.8"
		};
		String[] expected = {"{x < 2}", "{x < 2}", "?", "?", "?"};
		Log previous = Log.getLogger();
		CapturingLog log = new CapturingLog();
		log.setLogLevel(Log.Level.DEBUG);
		Log.setLogger(log);
		try {
			for (int i = 0; i < expressions.length; i++) {
				GeoSymbolic result = add("Solve(" + expressions[i] + ")");
				assertEquals(
						expected[i], result.toValueString(StringTemplate.testTemplate), expressions[i]);
				assertTrue(result.isBinomialStepResult(), expressions[i]);
				result.getTwinGeo();
				assertFalse(SymbolicUtil.isSymbolicSolveDiffers(result));
			}
			assertTrue(log.messages.stream().anyMatch(s -> s.contains("giac evalRaw input: ")));
			assertFalse(log.messages.stream()
					.anyMatch(s -> s.contains("giac evalRaw input: ")
							&& (s.contains("solve(") || s.contains("fsolve("))));
		} finally {
			Log.setLogger(previous);
		}
	}

	@Test
	void inequalitiesRespectStepBoundariesAndDirection() {
		String cdf = "BinomialDist(2,1/2,x,true)";
		t("Solve(" + cdf + "<3/4)", "{x < 1}");
		t("Solve(" + cdf + "<=3/4)", "{x < 2}");
		t("Solve(" + cdf + ">3/4)", "{2 ≤ x}");
		t("Solve(" + cdf + ">=3/4)", "{1 ≤ x}");
		t("Solve(3/4>" + cdf + ")", "{x < 1}");
		t("Solve(3/4>=" + cdf + ")", "{x < 2}");
		t("Solve(" + cdf + "<=1)", "{x = x}");
		t("Solve(" + cdf + "<0)", "{}");
		t("Solve(" + cdf + ">=0)", "{x = x}");
		t("Solve(" + cdf + ">1)", "{}");
		t("Solve(2*" + cdf + "=1.5)", "{1 ≤ x < 2}");
		t("Solve(-2*" + cdf + "<-1.5)", "{2 ≤ x}");
		t("Solve(1-" + cdf + "<=1/4)", "{1 ≤ x}");
		t("Solve(" + cdf + "/2<=3/8)", "{x < 2}");
		t("NSolve(" + cdf + "<0.9)", "{x < 2}");
		t("Solve(BinomialDist(2,0,x,true)<1/2)", "{x < 0}");
		t("Solve(BinomialDist(2,1,x,true)<=3/4)", "{x < 2}");
		t("Solve(BinomialDist(0,1/2,x,true)>=1)", "{0 ≤ x}");
		add("cdfalias:=BinomialDist(2,1/2,x,true)");
		t("Solve(cdfalias<3/4,x)", "{x < 1}");
		add("cdffunction(t):=BinomialDist(2,1/2,t,true)");
		t("Solve(cdffunction(x)<=3/4,x)", "{x < 2}");
	}

	@Test
	void attainableProbabilitiesReturnRealIntervals() {
		t("Solve(BinomialDist(2,0.5,x,true)=0.25)", "{0 ≤ x < 1}");
		t("Solve(BinomialDist(2,0.5,x,true)=0.75)", "{1 ≤ x < 2}");
		t("NSolve(BinomialDist(2,0.5,x,true)=0.75)", "{1 ≤ x < 2}");
	}

	@Test
	void endpointsReturnUnboundedIntervals() {
		t("Solve(BinomialDist(2,0.5,x,true)=0)", "{x < 0}");
		t("Solve(BinomialDist(2,0.5,x,true)=1)", "{2 ≤ x}");
	}

	@Test
	void decimalProbabilityIsComparedExactly() {
		t("Solve(BinomialDist(2,0.3,x,true)=0.49)", "{0 ≤ x < 1}");
		t("Solve(BinomialDist(2,0.3,x,true)=0.4900000000001)", "?");
	}

	@Test
	void storedSumsFunctionsAndEquationsKeepTheirFloorBoundaries() {
		add("s:=BinomialDist(2,1/2,x,true)");
		t("Solve(s=3/4,x)", "{1 ≤ x < 2}");
		add("f(t):=BinomialDist(2,1/2,t,true)");
		t("Solve(f(x)=3/4,x)", "{1 ≤ x < 2}");
		add("eqcdf: s=3/4");
		t("Solve(eqcdf,x)", "{1 ≤ x < 2}");
		t("Solve(3/4=BinomialDist(2,1/2,k,true),k)", "{1 ≤ k < 2}");
	}

	@Test
	void chainedAliasesPreserveDefinitionsAndComputedNumericValues() {
		GeoSymbolic base = add("cdfbase:=BinomialDist(2,1/2,x,true)");
		GeoSymbolic alias = add("cdfalias:=cdfbase");
		GeoSymbolic again = add("cdfagain:=cdfalias");
		assertSame(alias, again.getDefinition().unwrap());
		assertSame(base, alias.getDefinition().unwrap());
		t("Solve(cdfagain=3/4,x)", "{1 ≤ x < 2}");
		add("eqbase: cdfagain=3/4");
		add("eqalias:=eqbase");
		add("eqagain:=eqalias");
		t("Solve(eqagain,x)", "{1 ≤ x < 2}");

		add("probbase=1/2");
		add("probalias=probbase");
		GeoSymbolic probability = add("probagain=probalias");
		assertEquals("1 / 2", probability.getValue().toString(StringTemplate.testTemplate));
		add("goalbase=3/4");
		add("goalalias=goalbase");
		add("goalagain=goalalias");
		add("trialsbase=2");
		add("trialsalias=trialsbase");
		add("trialsagain=trialsalias");
		t("Solve(BinomialDist(trialsagain,probagain,x,true)=goalagain,x)", "{1 ≤ x < 2}");
	}

	@Test
	void degenerateDistributionsMergeAdjacentIntervals() {
		t("Solve(BinomialDist(2,0,x,true)=1)", "{0 ≤ x}");
		t("Solve(BinomialDist(2,0,x,true)=0)", "{x < 0}");
		t("Solve(BinomialDist(2,1,x,true)=0)", "{x < 2}");
		t("Solve(BinomialDist(2,1,x,true)=1)", "{2 ≤ x}");
		t("Solve(BinomialDist(0,1/2,x,true)=1)", "{0 ≤ x}");
	}

	@Test
	void explicitVariablesAndParameterUpdatesAreRespected() {
		t("Solve(BinomialDist(2,1/2,k,true)=3/4,k)", "{1 ≤ k < 2}");
		add("p=1/2");
		GeoSymbolic result = add("root=Solve(BinomialDist(2,p,x,true)=3/4,x)");
		assertEquals("{1 ≤ x < 2}", result.toValueString(StringTemplate.testTemplate));
		add("p=3/10");
		assertEquals("?", result.toValueString(StringTemplate.testTemplate));
	}

	@Test
	void exactTailProbabilitiesAreNotLostToInverseCdfRounding() {
		// binomial_icdf returns 100 for this exact CDF(99) value on native Giac.
		String upperTail = "1-1/2^100";
		t("Solve(BinomialDist(100,1/2,x,true)=" + upperTail + ")", "{99 ≤ x < 100}");
		t("Solve(BinomialDist(100,1/2,x,true)<" + upperTail + ")", "{x < 99}");
		t("Solve(BinomialDist(100,1/2,x,true)<=" + upperTail + ")", "{x < 100}");
		// For p=10^-15, inverse CDF returns 2 even though this target is CDF(0).
		String firstLevel = "(1-1/10^15)^2";
		t("Solve(BinomialDist(2,1/10^15,x,true)=" + firstLevel + ")", "{0 ≤ x < 1}");
		t("Solve(BinomialDist(2,1/10^15,x,true)<" + firstLevel + ")", "{x < 0}");
	}

	@Test
	void hundredTrialsUseTheFinitePath() {
		t("Solve(BinomialDist(100,1/2,x,true)=1/2,x)", "?");
	}

	@Test
	void unsupportedFormsKeepTheExistingCasPath() throws Exception {
		String[] unsupported = {
			"Solve(BinomialDist(101,1/2,x,true)=3/4,x)",
			"Solve(0*BinomialDist(2,1/2,x,true)=0,x)",
			"Solve(1/BinomialDist(2,1/2,x,true)<2,x)",
			"Solve(BinomialDist(2,1/2,x,true)+x<1,x)",
			"Solve(BinomialDist(2,1/2,x,true)^2<1/2,x)",
			"Solve(BinomialDist(2.4,1/2,x,true)=3/4,x)",
			"Solve(BinomialDist(n,1/2,x,true)=3/4,x)",
			"Solve(BinomialDist(2,p,1,true)=3/4,p)",
			"Solve(BinomialDist(true,1/2,x,true)=3/4,x)",
			"Solve(BinomialDist(2,true,x,true)=3/4,x)",
			"Solve(BinomialDist(2,1/2,x,true)=true,x)",
			"Solve(BinomialDist(2,sqrt(2)/2,x,true)=1/2,x)",
			"Solve(BinomialDist(2,1/2,x,true)=sqrt(2)/2,x)",
			"Solve(BinomialDist(2,1/2,2*x,true)=3/4,x)",
			"Solve(BinomialDist(2,1/2,x,false)=3/4,x)",
			"Solve(BinomialDist(2,1/2,x,true)=3/4,{x,p})"
		};
		for (String input : unsupported) {
			ValidExpression expression = kernel.getParser().parseGeoGebraExpression(input);
			assertNull(
					BinomialStepSolver.trySolve(kernel, expression, expression.getTopLevelCommand()), input);
		}
	}

	@Test
	void closedFormSumsAndSolvingForProbabilityStillWork() {
		t("Solve(Sum(x^k,k,0,2)=7,x)", "{x = -3, x = 2}");
		t("Solve(Sum(k,k,0,x)=3,x)", "{x = -3, x = 2}");
		t(
				"Solve(BinomialDist(2,p,1,true)=0.75,p)",
				anyOf(equalTo("{p = -1 / 2, p = 1 / 2}"), equalTo("{p = 1 / 2, p = -1 / 2}")));
	}

	private static final class CapturingLog extends Log {
		private final List<String> messages = new ArrayList<>();

		@Override
		public void print(Level level, Object message) {
			messages.add(String.valueOf(message));
		}
	}
}
