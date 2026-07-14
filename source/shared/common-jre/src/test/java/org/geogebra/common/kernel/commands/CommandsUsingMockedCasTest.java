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

package org.geogebra.common.kernel.commands;

import org.geogebra.common.cas.MockedCasGiac;
import org.geogebra.common.factories.UtilFactory;
import org.geogebra.common.factories.UtilFactoryCommon;
import org.geogebra.common.util.MockedCasValues;
import org.geogebra.common.util.MockedCasValuesExtension;
import org.geogebra.editor.share.util.Unicode;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(MockedCasValuesExtension.class)
public class CommandsUsingMockedCasTest extends CommandTestSetup {

	protected final MockedCasGiac mockedCasGiac = new MockedCasGiac();

	@BeforeAll
	public static void setupFactory() {
		UtilFactory.setPrototypeIfNull(new UtilFactoryCommon());
	}

	@BeforeEach
	public void installCas() {
		mockedCasGiac.applyTo(app);
		//
		mockedCasGiac.memorize("1+1", "2");
	}

	@Test
	@MockedCasValues("Expand(x) -> x")
	public void cmdExpand() {
		t("Expand(x)", "x");
	}

	@Test
	@MockedCasValues("Simplify(x) -> x")
	public void cmdSimplify() {
		t("Simplify(x)", "x");
		t("Simplify[\"x+-x--x\"]", "x " + Unicode.MINUS + " x + x");
	}

	@Test
	@MockedCasValues("NextPrime(3) -> 5")
	public void cmdNextPrime() {
		t("NextPrime(3)", "5");
	}

	@Test
	@MockedCasValues("PreviousPrime(5) -> 3")
	public void cmdPreviousPrime() {
		t("PreviousPrime(5)", "3");
	}

	@Test
	@MockedCasValues({
			"Solve(x = 0) -> {x = 0}",
			"Numeric(Limit(x / x, x, 0), 50) -> 1"
	})
	public void cmdRemovableDiscontinuity() {
		t("RemovableDiscontinuity(x/x)", "(0, 1)");
	}

	@Test
	public void cmdDerivative() {
		t("Derivative(x^2)", "(2 * x)");
		t("Derivative((t^3, t^2))", "((3 * t^(2)), (2 * t))");
		t("Derivative(x^3, 2)", "(6 * x)");
		t("Derivative((t^3, t^2),2)", "((6 * t), 2)");
		t("Derivative(x^3, x)", "(3 * x^(2))");
		t("Derivative(x^3, x, 2)", "(6 * x)");
	}

	@Test
	public void cmdIntegral() {
		t("Integral(x)", "(1 / 2 * x^(2))");
		t("Integral(x^2,x)", "(1 / 3 * x^(3))");
		t("Integral(x,0,4)", "8");
		t("Integral(x,0,4,false)", "NaN");
	}

	@Test
	@MockedCasValues("Numeric(Limit(x, x, 1), 50) -> 1")
	public void cmdLimit() {
		t("Limit(x,1)", "1");
	}

	@Test
	@MockedCasValues("Numeric(LimitBelow(x, x, 1), 50) -> 1")
	public void cmdLimitBelow() {
		t("LimitBelow(x,1)", "1");
	}

	@Test
	@MockedCasValues({"Numeric(LimitAbove(x, x, 1), 50) -> 1"})
	public void cmdLimitAbove() {
		t("LimitAbove(x,1)", "1");
	}

	@Test
	@MockedCasValues("PartialFractions(2 / (x² - 1), x) -> 1/(x-1) - 1/(x+1)")
	public void cmdPartialFractions() {
		t("PartialFractions(2/(x^2-1))", "1 / (x - 1) - 1 / (x + 1)");
	}

	@Test
	@MockedCasValues({
			"Limit(1 / x, x, ∞) -> 0",
			"Limit(1 / x, x, -∞) -> 0",
			"Solve(Numerator(Simplify(1 / (1 / x))) = 0, x) -> {x = 0}",
			"ExpSimplify(ℯ^Numerator(1 / x)) -> ℯ",
			"Solve(ExpSimplify(ℯ^Numerator(1 / x)) = 0, x) -> {}",
			"Numeric(Limit(1 / x, x, 0)) -> ∞"
	})
	public void cmdAsymptote() {
		t("Asymptote(1/x)", "{y = 0, x = 0}");
	}

	@Test
	@MockedCasValues("ImplicitDerivative(x + y) -> 1")
	public void cmdImplicitDerivative() {
		t("ImplicitDerivative(x+y)", "1");
	}

	@Test
	@MockedCasValues("CSolutions(x⁴ - 1) -> {-1 + 0i, 1 + 0i, i, i}")
	public void cmdCSolutions() {
		t("CSolutions(x^4-1)", "{-1 + (0 * ί), 1 + (0 * ί), ί, ί}");
	}

	@Test
	@MockedCasValues("CSolve(x⁴ - 1) -> {x = -1 + 0i, x = 1 + 0i, x = i, x = -i}")
	public void cmdCSolve() {
		t("CSolve(x^4-1)", "{x = -1 + (0 * ί), x = 1 + (0 * ί), x = ί, x = (-ί)}");
	}

	@Test
	@MockedCasValues("NSolve(x³ - x) -> {x = -1, x = 0, x = 1}")
	public void cmdNSolve() {
		t("NSolve(x^3-x)", "{x = -1, x = 0, x = 1}");
	}

	@Test
	@MockedCasValues("NSolutions(x³ - x) -> {-1, 0, 1}")
	public void cmdNSolutions() {
		t("NSolutions(x^3-x)", "{-1, 0, 1}");
	}

	@Test
	@MockedCasValues("Solutions(x³ - x) -> {-1, 0, 1}")
	public void cmdSolutions() {
		t("Solutions(x^3-x)", "{-1, 0, 1}");
	}

	@Test
	@MockedCasValues("Solve(x³ - x) -> {x = -1, x = 0, x = 1}")
	public void cmdSolve() {
		t("Solve(x^3-x)", "{x = -1, x = 0, x = 1}");
	}

	@Test
	@MockedCasValues("PlotSolve(x³ - x) -> {(-1,0), (0,0), (1,0)}")
	public void cmdPlotSolve() {
		t("PlotSolve(x^3-x)", "{(-1, 0), (0, 0), (1, 0)}");
	}

	@Test
	@MockedCasValues({
			"TrigExpand(sin(2x)) -> 2 * sin(x) * cos(x)",
			"TrigExpand(sin(2x), sin(x)) -> 2 * sin(x) * cos(x)"
	})
	public void cmdTrigExpand() {
		t("TrigExpand(sin(2x))", "((2 * sin(x)) * cos(x))");
		t("TrigExpand(sin(2x),sin(x))", "((2 * sin(x)) * cos(x))");
	}

	@Test
	@MockedCasValues("TrigSimplify(sin(2x)) -> sin(2x)")
	public void cmdTrigSimplify() {
		t("TrigSimplify(sin(2x))", "sin((2 * x))");
	}

	@Test
	@MockedCasValues({
			"TrigCombine(sin(2x)) -> sin(2x)",
			"TrigCombine(sin(2x), sin(x)) -> sin(2x)"
	})
	public void cmdTrigCombine() {
		t("TrigCombine(sin(2x))", "sin((2 * x))");
		t("TrigCombine(sin(2x),sin(x))", "sin((2 * x))");
	}

	@Test
	public void cmdCASLoaded() {
		t("CASLoaded()", "true");
	}

	@Test
	public void cmdProve() {
		t("Prove(true)", "false");
	}

	@Test
	public void cmdProveDetails() {
		t("ProveDetails(true)", "{}");
	}

	@Test
	public void cmdLocusEquation() {
		t("A=Point(xAxis)", "(0, 0)");
		t("LocusEquation(Locus(2*A,A))", "?");
		t("LocusEquation(2*A,A)", "?");
		t("LocusEquation(x(A)==2,A)", "?");
	}

	@Test
	public void cmdEnvelope() {
		t("A=Point(xAxis)", "(0, 0)");
		t("Envelope(Line(A,O), A)", "?");
	}
}
