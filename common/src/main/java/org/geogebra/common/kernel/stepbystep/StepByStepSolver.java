package org.geogebra.common.kernel.stepbystep;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.ExpressionValue;
import org.geogebra.common.main.Localization;
import org.geogebra.common.plugin.Operation;

public class StepByStepSolver {
	private Kernel kernel;
	private Localization loc;

	private StepByStepHelper helper;

	// needed for checking the validity of the solutions.
	private String origLHS;
	private String origRHS;

	private String LHS;
	private String RHS;

	private ExpressionValue evLHS;
	private ExpressionValue evRHS;

	private boolean inverted;

	private List<String> steps;
	private List<String> solutions;

	public StepByStepSolver(Kernel kernel, String LHS, String RHS) {
		this.kernel = kernel;
		this.loc = kernel.getLocalization();

		this.origLHS = this.LHS = LHS;
		this.origRHS = this.RHS = RHS;

		this.helper = new StepByStepHelper(kernel);

		inverted = false;
	}

	public List<String> getSteps() {
		steps = new ArrayList<String>();
		solutions = new ArrayList<String>();

		regenerateTrees();

		addStep();
		simplify();

		if (isZero(LHS) && helper.isProduct(evRHS)) {
			solveProduct(evRHS);
			return checkSolutions();
		} else if (isZero(RHS) && helper.isProduct(evLHS)) {
			solveProduct(evLHS);
			return checkSolutions();
		}

		ExpressionValue bothSides = helper.getExpressionTree("(" + LHS + ")(" + RHS + ")");

		String denominators = helper.getDenominator(bothSides);
		multiply(denominators);

		expandParantheses();		

		if (helper.countOperation(evRHS, Operation.SQRT) > helper.countOperation(evLHS, Operation.SQRT)) {
			swapSides();
		}

		regenerateTrees();

		int k = 0;  // Limit the number of times to square the equation || TODO: IMPLEMENT IT PROPERLY!

		// irrational equation (containing at least one square root)
		while (k < 3 && (helper.countOperation(evLHS, Operation.SQRT) > 0 || helper.countOperation(evRHS, Operation.SQRT) > 0)) {

			String LHSirrational = helper.getSQRoots(evLHS);
			String LHSnonIrrational;

			if (!isZero(LHSirrational)) {
				LHSnonIrrational = helper.simplify(LHS + " - (" + LHSirrational + ")");
			} else {
				LHSnonIrrational = LHS;
			}

			subtract(LHSnonIrrational);

			String RHSirrational = helper.getSQRoots(evRHS);
			String RHSnonIrrational;

			if (!isZero(RHSirrational)) {
				RHSnonIrrational = helper.simplify(RHS + " - (" + RHSirrational + ")");
			} else {
				RHSnonIrrational = RHS;
			}

			if (!isZero(RHSnonIrrational)) {
				subtract(RHSirrational);
			}

			square();
			k++;
		}

		// Equations containing absolute values
		bothSides = helper.getExpressionTree("(" + LHS + ")+(" + RHS + ")");
		if (helper.countOperation(bothSides, Operation.ABS) > 0) {
			ArrayList<String> absoluteValues = new ArrayList<String>();
			helper.getAbsoluteValues(absoluteValues, bothSides);

			ArrayList<String> roots = new ArrayList<String>();
			for (int i = 0; i < absoluteValues.size(); i++) {
				roots.addAll(Arrays.asList(helper.getCASSolutions(absoluteValues.get(i), "0")));
			}

			Collections.sort(roots, new Comparator<String>() {
				@Override
				public int compare(String s1, String s2) {
					return helper.getValue(s1) < helper.getValue(s2) ? -1 : 1;
				}
			});

			for (int i = 0; i <= roots.size(); i++) {
				solveAbsoulteValueEquation(i == 0 ? "-inf" : roots.get(i - 1),
						i == roots.size() ? "+inf" : roots.get(i));
			}

			return checkSolutions();
		}

		int degreeLHS = helper.degree(LHS);
		int degreeRHS = helper.degree(RHS);

		if (degreeLHS == -1 || degreeRHS == -1) {
			steps.add(loc.getMenuLaTeX("CannotSolve"));
			return steps;
		}

		if (degreeRHS > degreeLHS || (degreeRHS == degreeLHS
				&& helper.getCoefficientValue(evRHS, "x^" + degreeRHS) > helper
						.getCoefficientValue(evLHS, "x^" + degreeLHS))) {
			swapSides();
		}

		// simple linear equation
		if (degreeLHS <= 1 && degreeRHS <= 1) {
			if (degreeLHS == degreeRHS && degreeRHS == 0) {
				if (LHS.equals(RHS)) {
					solutions.add("all");
					return checkSolutions();
				}
				steps.add(loc.getMenuLaTeX("NoSolutions"));
				return steps;
			}
			regenerateTrees();
			solveLinear();
			return checkSolutions();
		}

		// quadratic equation
		if (degreeLHS <= 2 && degreeRHS <= 2) {
			if (helper.containsLinear(evLHS, "x") || helper.containsLinear(evRHS, "x")) {

				subtract(RHS);

				String a = helper.findCoefficient(evLHS, "x^2");
				String b = helper.findCoefficient(evLHS, "x");
				String c = helper.findConstant(evLHS);

				if (isZero(a)) {
					if (isZero(LHS)) {
						solutions.add("all");
					} else if (LHS.equals(c)) {
						steps.add(loc.getMenuLaTeX("NoSolutions"));
						return steps;
					} else {
						solveLinear();
					}
					return checkSolutions();
				}

				if (isZero(c)) {
					steps.add(loc.getMenuLaTeX("FactorEquation"));
					LHS = helper.factor(LHS);
					addStep();

					evLHS = helper.getExpressionTree(LHS);

					solveProduct(evLHS);
					return checkSolutions();
				}

				steps.add(loc.getMenuLaTeX("UseQuadraticFormulaWithABC", a, b, c));

				String discriminant = "(" + b + ")^2-4(" + a + ")(" + c + ")";

				double discriminantValue = helper.getValue(discriminant);

				steps.add("\\Delta = " + discriminant);
				steps.add("\\Delta = " + helper.simplify(discriminant));

				if (discriminantValue < 0) {
					steps.add(loc.getMenuLaTeX("DeltaLessThanZero"));
				} else if (discriminantValue == 0) {
					steps.add(loc.getMenuLaTeX("DeltaZero"));

					String formula = "-(" + b + ")/(2(" + a + "))";
					steps.add("x = " + formula);
					steps.add("x = " + helper.simplify(formula));
					solutions.add(helper.simplify(formula));
				} else {
					steps.add(loc.getMenuLaTeX("DeltaGreaterThanZero"));

					String formula = "(-(" + b + ")+-sqrt(" + discriminant
							+ "))/(2(" + a + "))";
					String x1 = helper.simplify("(-(" + b + ")+sqrt("
							+ discriminant + "))/(2(" + a + "))");
					String x2 = helper.simplify("(-(" + b + ")-sqrt("
							+ discriminant + "))/(2(" + a + "))");

					steps.add("x = " + formula);
					steps.add("x = " + x1);
					steps.add(loc.getMenu("Or"));
					steps.add("x = " + x2);

					solutions.add(x1);
					solutions.add(x2);
				}
			} else {
				String quadratic = helper.findVariable(evRHS, "x^2");
				String coefficient = helper.findCoefficient(evRHS, "x^2");
				addOrSubtract(coefficient, quadratic);

				String constant = helper.findConstant(evLHS);
				addOrSubtract(constant, constant);

				String toDivide = helper.findCoefficient(evLHS, "x^2");
				divide(toDivide);

				double RHSvalue = helper.getValue(RHS);
				if (RHSvalue < 0) {
					steps.add(loc.getMenuLaTeX("NoRealSolutions"));
					return steps;
				} else if (RHSvalue == 0) {
					steps.add("x = 0");
					solutions.add("0");
				} else {
					String solution = helper.simplify("sqrt(" + RHS + ")");
					steps.add("x = " + solution);
					steps.add(loc.getMenu("Or"));
					steps.add("x = -" + solution);
					solutions.add(solution);
					solutions.add("-" + solution);
				}
			}

			return checkSolutions();
		}

		subtract(RHS);

		int degree = helper.degree(LHS);

		if (degree == 3) {
			steps.add(loc.getMenuLaTeX("UseCubicFormula"));
		} else if (degree == 4) {
			steps.add(loc.getMenuLaTeX("UseQuarticFormula"));
		} else {
			steps.add(loc.getMenuLaTeX("NumericalSolutions"));
		}

		String[] CASSolutions = helper.getCASSolutions(LHS, "0");
		for (int i = 0; i < CASSolutions.length; i++) {
			steps.add("x_" + (i + 1) + " = " + CASSolutions[i]);
		}

		if (CASSolutions.length == 0) {
			steps.add(loc.getMenuLaTeX("NoRealSolutions"));
		}

		solutions.addAll(Arrays.asList(CASSolutions));

		return checkSolutions();
	}

	public List<String> getSolutions() {
		if (solutions == null) {
			getSteps();
		}

		return solutions;
	}

	private List<String> checkSolutions() { 
		ExpressionValue bothSides = helper
				.getExpressionTree(origLHS + "+" + origRHS);

		String denominators = helper.getDenominator(bothSides);
		String roots = helper.getSQRoots(bothSides);

		ExpressionValue evDenominators = helper.getExpressionTree(denominators);
		ExpressionValue evRoots = helper.getExpressionTree(roots);

		if (!helper.containsVariable(evDenominators)
				&& !helper.containsVariable(evRoots) || solutions.size() == 0) {
			return steps;
		}

		steps.add(loc.getMenuLaTeX("CheckValidityOfSolutions"));

		for (int i = 0; i < solutions.size(); i++) {
			if (helper.isValidSolution(origLHS, origRHS, solutions.get(i))) {
				steps.add(loc.getMenuLaTeX("ValidSolution", "x", solutions.get(i)));
			} else {
				steps.add(
						loc.getMenuLaTeX("InvalidSolution", "x", solutions.get(i)));
				solutions.remove(solutions.get(i));
				i--;
			}
		} 

		return steps;
	}

	private void solveLinear() {
		simplify();

		String linear = helper.findVariable(evRHS, "x");
		String coefficient = helper.findCoefficient(evRHS, "x");
		addOrSubtract(coefficient, linear);

		String constant = helper.findConstant(evLHS);
		addOrSubtract(constant, constant);

		String toDivide = helper.findCoefficient(evLHS, "x");
		divide(toDivide);

		solutions.add(RHS);
	}

	private boolean isZero(String s) {
		return helper.stripSpaces(s).equals("")
				|| helper.stripSpaces(s).equals("0");
	}

	private boolean isOne(String s) {
		return helper.stripSpaces(s).equals("")
				|| helper.stripSpaces(s).equals("1");
	}

	private void swapSides() {
		String temp = LHS;
		LHS = RHS;
		RHS = temp;

		inverted = !inverted;

		regenerateTrees();
	}

	private void regenerateTrees() {
		evLHS = helper.getExpressionTree(LHS);
		evRHS = helper.getExpressionTree(RHS);
	}

	private void addStep() {
		if (inverted) {
			steps.add(LaTeX(RHS) + " = " + LaTeX(LHS));
		} else {
			steps.add(LaTeX(LHS) + " = " + LaTeX(RHS));
		}
	}

	private void simplify() {
		// TODO: proper checking of simplification
		if (helper.stripSpaces(helper.regroup(LHS)).length() < helper.stripSpaces(LHS).length() || 
				helper.stripSpaces(helper.regroup(RHS)).length() < helper.stripSpaces(RHS).length()) {
			LHS = helper.regroup(LHS);
			RHS = helper.regroup(RHS);

			steps.add(loc.getMenuLaTeX("SimplifyExpression"));
			addStep();
			regenerateTrees();
		}
	}

	private void expandParantheses() {
		if (!isZero(helper.regroup(helper.expand(LHS) + " - (" + LHS + ")")) || 
				!isZero(helper.regroup(helper.expand(RHS) + " - (" + RHS + ")"))) {
			LHS = helper.expand(LHS);
			RHS = helper.expand(RHS);

			steps.add(loc.getMenuLaTeX("ExpandParantheses"));
			addStep();

			regenerateTrees();
		}
	}

	private String LaTeX(String toLaTeX) {
		if(toLaTeX.isEmpty()) {
			return "";
		}

		ExpressionValue ev = helper.getExpressionTree(toLaTeX);
		return ev.isExpressionNode() ? ((ExpressionNode) ev).toLaTeXString(false, StringTemplate.latexTemplate) : ev.toString(StringTemplate.defaultTemplate);
	}

	private void add(String toAdd) {
		if (!isZero(toAdd)) {
			LHS = LHS + "+(" + toAdd + ")";
			RHS = RHS + "+(" + toAdd + ")";

			steps.add(loc.getMenuLaTeX("AddAToBothSides", LaTeX(toAdd)));
			addStep();

			LHS = helper.regroup(LHS);
			RHS = helper.regroup(RHS);
			addStep();

			regenerateTrees();
		}
	}

	private void subtract(String toSubtract) {
		if (!isZero(toSubtract)) {
			LHS = LHS + "-(" + toSubtract + ")";
			RHS = RHS + "-(" + toSubtract + ")";

			steps.add(loc.getMenuLaTeX("SubtractAFromBothSides", LaTeX(toSubtract)));
			addStep();

			LHS = helper.regroup(LHS);
			RHS = helper.regroup(RHS);
			addStep();

			regenerateTrees();
		}

	}

	private void addOrSubtract(String coeff, String value) {
		if (helper.getValue(coeff) < 0) {
			add(helper.simplify("-(" + value + ")"));
		} else {
			subtract(value);
		}
	}

	private void multiply(String toMultiply) {
		if (!isOne(toMultiply) && !isZero(toMultiply)) {
			LHS = "(" + LHS + ")(" + toMultiply + ")";
			RHS = "(" + RHS + ")(" + toMultiply + ")";

			steps.add(loc.getMenuLaTeX("MultiplyBothSidesByA", LaTeX(toMultiply)));
			addStep();

			LHS = helper.simplify(LHS);
			RHS = helper.simplify(RHS);
			addStep();

			regenerateTrees();
		}
	}

	private void divide(String toDivide) {
		if (!isOne(toDivide) && !isZero(toDivide)) {
			LHS = "(" + LHS + ")/(" + toDivide + ")";
			RHS = "(" + RHS + ")/(" + toDivide + ")";

			steps.add(loc.getMenuLaTeX("DivideBothSidesByA", LaTeX(toDivide)));
			addStep();

			LHS = helper.regroup(LHS);
			RHS = helper.regroup(RHS);
			addStep();

			regenerateTrees();
		}
	}

	private void square() {
		LHS = "(" + LHS + ")^2";
		RHS = "(" + RHS + ")^2";

		steps.add(loc.getMenuLaTeX("SquareBothSides"));
		addStep();

		LHS = helper.simplify(LHS);
		RHS = helper.simplify(RHS);
		addStep();

		regenerateTrees();

	}

	private void solveProduct(ExpressionValue product) {
		ArrayList<String> equations = new ArrayList<String>();
		helper.getParts(equations, product);

		steps.add(loc.getMenuLaTeX("ProductIsZero"));

		for (int i = 0; i < equations.size(); i++) {
			StepByStepSolver sbss = new StepByStepSolver(kernel,
					equations.get(i), "0");
			steps.addAll(sbss.getSteps());
			solutions.addAll(sbss.getSolutions());
		}
	}

	private void solveAbsoulteValueEquation(String a, String b) {
		steps.add(loc.getMenuLaTeX("SolvingBetween", LHS, RHS, a, b));

		String LHSevaluated = helper.evaluateAbsoluteValue(LHS, a, b);
		String RHSevaluated = helper.evaluateAbsoluteValue(RHS, a, b);

		StepByStepSolver sbss = new StepByStepSolver(kernel, LHSevaluated,
				RHSevaluated);
		steps.addAll(sbss.getSteps());

		double aVal = helper.getValue(a);
		double bVal = helper.getValue(b);

		List<String> partialSolutions = sbss.getSolutions();
		for (int i = 0; i < partialSolutions.size(); i++) {

			if (partialSolutions.get(i).equals("all")) {
				steps.add(loc.getMenuLaTeX("AllNumbersBetween", a, b));
				solutions.add("(" + a + ", " + b + ")");
			} else {
				double xVal = helper.getValue(partialSolutions.get(i));

				if (aVal <= xVal && xVal <= bVal) {
					steps.add(loc.getMenuLaTeX("ValidSolutionAbs", "x",
							partialSolutions.get(i), a, b));
				} else {
					steps.add(loc.getMenuLaTeX("InvalidSolutionAbs", "x",
							partialSolutions.get(i), a, b));
				}
			}
		}
	}
}
