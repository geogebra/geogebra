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

import com.himamis.retex.editor.share.util.Unicode;

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

		// I. step: regrouping
		addStep();
		regroup();

		// II. step: making denominators disappear
		ExpressionValue bothSides = helper.getExpressionTree("(" + LHS + ")(" + RHS + ")");
		String denominators = helper.getDenominator(bothSides);
		multiply(denominators);
		
		// III. step: solving as a product
		if (isZero(LHS) && helper.isProduct(evRHS)) {
			solveProduct(evRHS);
			return checkSolutions();
		} else if (isZero(RHS) && helper.isProduct(evLHS)) {
			solveProduct(evLHS);
			return checkSolutions();
		}

		// IV. step: expanding parentheses
		expandParantheses();		

		// V. step: getting rid of square roots
		bothSides = helper.getExpressionTree("(" + LHS + ")+(" + RHS + ")");
		if(helper.countOperation(bothSides, Operation.SQRT) > 0) {
			solveIrrational();
		}

		// VI. Step: equations containing absolute values
		bothSides = helper.getExpressionTree("(" + LHS + ")+(" + RHS + ")");
		if (helper.countOperation(bothSides, Operation.ABS) > 0) {
			solveAbsoluteValue();
			return checkSolutions();
		}

		int degreeLHS = helper.degree(LHS);
		int degreeRHS = helper.degree(RHS);

		// Checking if it's in polynomial form.
		if (degreeLHS == -1 || degreeRHS == -1) {
			steps.add(loc.getMenuLaTeX("CannotSolve", "Can't solve"));
			return steps;
		}

		// Swapping sides, if necessary
		if (degreeRHS > degreeLHS || (degreeRHS == degreeLHS
				&& helper.getCoefficientValue(evRHS, "x^" + degreeRHS) > helper
						.getCoefficientValue(evLHS, "x^" + degreeLHS))) {
			swapSides();
		}

		// VII. step: solving linear equations
		if (degreeLHS <= 1 && degreeRHS <= 1) {
			solveLinear();
			return checkSolutions();
		}

		// VIII. step: solving quadratic equations
		if (degreeLHS <= 2 && degreeRHS <= 2) {
			solveQuadratic();
			return checkSolutions();
		}
		
		// XI. step: solving trivial (ax^n = b) equations
		if(helper.isTrivial(RHS, LHS)) {
			solveTrivial();
			return checkSolutions();
		}

		// TODO: X. step: finding and factoring rational roots
		
		// XI. step: numeric solutions
		numericSolutions();
		return checkSolutions();
	}

	public List<String> getSolutions() {
		if (solutions == null) {
			getSteps();
		}

		return solutions;
	}
	
	// TODO: something else :-?
	private List<String> checkSolutions() {
		ExpressionValue bothSides = helper.getExpressionTree(origLHS + "+" + origRHS);

		String denominators = helper.getDenominator(bothSides);
		String roots = helper.getSQRoots(bothSides);

		ExpressionValue evDenominators = helper.getExpressionTree(denominators);
		ExpressionValue evRoots = helper.getExpressionTree(roots);

		if (!helper.containsVariable(evDenominators)
				&& !helper.containsVariable(evRoots) || solutions.size() == 0) {
			return steps;
		}

		steps.add(loc.getMenuLaTeX("CheckingValidityOfSolutions",
				"Checking validity of solutions"));

		for (int i = 0; i < solutions.size(); i++) {
			if (helper.isValidSolution(origLHS, origRHS, solutions.get(i))) {
				steps.add(loc.getMenuLaTeX("ValidSolution","Valid Solution: %0 = %1","x", solutions.get(i)));
			} else {
				steps.add(loc.getMenuLaTeX("InvalidSolution", "Invalid Solution: %0 "
										+ Unicode.NOTEQUAL + " %1", "x", solutions.get(i)));
				solutions.remove(solutions.get(i));
				i--;
			}
		}

		return steps;
	}
	
	private void solveProduct(ExpressionValue product) {
		ArrayList<String> equations = new ArrayList<String>();
		helper.getParts(equations, product);

		steps.add(loc.getMenuLaTeX("ProductIsZero", "Product is zero"));

		for (int i = 0; i < equations.size(); i++) {
			StepByStepSolver sbss = new StepByStepSolver(kernel, equations.get(i), "0");
			steps.addAll(sbss.getSteps());
			solutions.addAll(sbss.getSolutions());
		}

		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < solutions.size(); i++) {
			sb.append("x = ");
			sb.append(LaTeX(solutions.get(i)));
			if(i < solutions.size() - 1) {
				sb.append(", ");
			}
		}

		steps.add(sb.toString());
	}
	
	private void solveIrrational() {
		int sqrtNum = helper.countOperation(evLHS, Operation.SQRT) + helper.countOperation(evRHS, Operation.SQRT);
		
		if(helper.countOperation(evRHS, Operation.SQRT) > helper.countOperation(evLHS, Operation.SQRT)) {
			swapSides();
		}
		
		if (sqrtNum > 3) {
			return;
		}
		
		if(sqrtNum == 1) {
			String nonIrrational = helper.getNonIrrational(evLHS);
			String coeff = helper.findCoefficient(evLHS, nonIrrational);
			addOrSubtract(coeff, nonIrrational);
			square();
		}
		
		if(sqrtNum == 2) {
			ExpressionValue diff = helper.getExpressionTree(helper.regroup(LHS + " - (" + RHS + ")"));
			if(isZero(helper.getNonIrrational(diff))) {
				String nonIrrational = helper.getNonIrrational(evLHS);
				String coeff = helper.findCoefficient(evLHS, nonIrrational);
				addOrSubtract(coeff, nonIrrational);
				if(helper.countOperation(evLHS, Operation.SQRT) == 2) {
					String oneRoot = helper.getOneSquareRoot(evLHS);
					String rootCoeff =  helper.findCoefficient(evLHS, oneRoot);
					addOrSubtract(rootCoeff, oneRoot);
				}
				square();
			} else {
				String rootsRHS = helper.getSQRoots(evRHS);
				String rootsCoeff = helper.findCoefficient(evRHS, rootsRHS);
				addOrSubtract(rootsCoeff, rootsRHS);
				String nonIrrational = helper.getNonIrrational(evLHS);
				subtract(nonIrrational);
				square();
				solveIrrational();
			}
		}

		if (sqrtNum == 3) {
			String nonIrrational = helper.getNonIrrational(evLHS);
			subtract(nonIrrational);

			while (helper.countOperation(evRHS, Operation.SQRT) > 1) {
				String oneRoot = helper.getOneSquareRoot(evRHS);
				String rootCoeff = helper.findCoefficient(evRHS, oneRoot);
				addOrSubtract(rootCoeff, oneRoot);
			}

			if (helper.countOperation(evLHS, Operation.SQRT) == 3) {
				String oneRoot = helper.getOneSquareRoot(evLHS);
				String rootCoeff = helper.findCoefficient(evLHS, oneRoot);
				addOrSubtract(rootCoeff, oneRoot);
			}

			square();
			solveIrrational();
		}
	}
	
	private void solveAbsoluteValue() {
		ExpressionValue bothSides = helper.getExpressionTree("(" + LHS + ")+(" + RHS + ")");
		ArrayList<String> absoluteValues = new ArrayList<String>();
		helper.getAbsoluteValues(absoluteValues, bothSides);

		ArrayList<String> roots = new ArrayList<String>();
		for (int i = 0; i < absoluteValues.size(); i++) {
			roots.addAll(Arrays.asList(helper.getCASSolutions(absoluteValues.get(i), "0")));
		}

		Collections.sort(roots, new Comparator<String>() {
			@Override
			public int compare(String s1, String s2) {
				return  Double.compare(helper.getValue(s1), helper.getValue(s2));
			}
		});

		for (int i = 0; i <= roots.size(); i++) {
			solveAbsoulteValueEquation(i == 0 ? "-inf" : roots.get(i - 1),
					i == roots.size() ? "+inf" : roots.get(i));
		}
	}
	
	private void solveAbsoulteValueEquation(String a, String b) {
		steps.add(loc.getMenuLaTeX("SolvingBetween",
				"Solving %0 = %1 between %2 and %3", LHS, RHS, a, b));

		String LHSevaluated = helper.evaluateAbsoluteValue(LHS, a, b);
		String RHSevaluated = helper.evaluateAbsoluteValue(RHS, a, b);

		StepByStepSolver sbss = new StepByStepSolver(kernel, LHSevaluated, RHSevaluated);
		steps.addAll(sbss.getSteps());

		double aVal = helper.getValue(a);
		double bVal = helper.getValue(b);

		List<String> partialSolutions = sbss.getSolutions();
		for (int i = 0; i < partialSolutions.size(); i++) {

			if (partialSolutions.get(i).equals("all")) {
				steps.add(loc.getMenuLaTeX("AllNumbersBetween", "All numbers between %0 and %1 are solutions", a, b));
				solutions.add("(" + a + ", " + b + ")");
			} else {
				double xVal = helper.getValue(partialSolutions.get(i));

				if (aVal <= xVal && xVal <= bVal) {
					steps.add(loc.getMenuLaTeX("ValidSolutionAbs", "%0 = %1 is between %2 and %3", "x",
							partialSolutions.get(i), a, b));
				} else {
					steps.add(loc.getMenuLaTeX("InvalidSolutionAbs", "%0 = %1 is not between %2 and %3", "x",
							partialSolutions.get(i), a, b));
				}
			}
		}
	}	
	
	private void solveLinear() {
		int degreeLHS = helper.degree(LHS);
		int degreeRHS = helper.degree(RHS);
		
		if (degreeLHS == degreeRHS && degreeRHS == 0) {
			if (LHS.equals(RHS)) {
				solutions.add("all");
				return;
			}
			steps.add(loc.getMenuLaTeX("NoSolutions", "No Solutions"));
			return;
		}
	
		regroup();

		String linear = helper.findVariable(evRHS, "x");
		String coefficient = helper.findCoefficient(evRHS, "x");
		addOrSubtract(coefficient, linear);

		String constant = helper.findConstant(evLHS);
		addOrSubtract(constant, constant);

		String toDivide = helper.findCoefficient(evLHS, "x");
		divide(toDivide);

		solutions.add(RHS);
	}

	private void solveQuadratic() {
		ExpressionValue diff = helper.getExpressionTree(helper.regroup(LHS + " - (" + RHS + ")"));
		
		if(isZero(helper.findVariable(diff, "x"))) {
			String quadratic = helper.findVariable(evRHS, "x^2");
			String coefficient = helper.findCoefficient(evRHS, "x^2");
			addOrSubtract(coefficient, quadratic);

			String constant = helper.findConstant(evLHS);
			addOrSubtract(constant, constant);

			String toDivide = helper.findCoefficient(evLHS, "x^2");
			divide(toDivide);

			double RHSvalue = helper.getValue(RHS);
			if (RHSvalue < 0) {
				steps.add(loc.getMenuLaTeX("NoRealSolutions", "No real solutions"));
				return;
			} else if (RHSvalue == 0) {
				steps.add("x = 0");
				solutions.add("0");
			} else {
				String solution = helper.simplify("sqrt(" + RHS + ")");
				steps.add("x = " + solution);
				steps.add(loc.getMenu("or"));
				steps.add("x = -" + solution);
				solutions.add(solution);
				solutions.add("-" + solution);
			}
			
			return;
		}

		subtract(RHS);
		
		String a = helper.findCoefficient(evLHS, "x^2");
		String b = helper.findCoefficient(evLHS, "x");
		String c = helper.findConstant(evLHS);
		
		if (isZero(a)) {
			solveLinear();
			return;
		}

		if(isZero(c)) {
			steps.add(loc.getMenuLaTeX("FactorEquation", "Factor equation"));
			LHS = helper.factor(LHS);
			addStep();

			evLHS = helper.getExpressionTree(LHS);

			solveProduct(evLHS);
			return;
		}

		if (isOne(a) && isEven(b)) {
			String toComplete = helper.regroup(c + " - ((" + b + ")/2)^2");
			
			steps.add(loc.getMenuLaTeX("CompleteSquare", "Complete the square"));
			
			addOrSubtract(toComplete, toComplete);
			LHS = helper.factor(LHS);
			addStep();

			if (helper.getValue(RHS) < 0) {
				steps.add(loc.getMenuLaTeX("NoRealSolutions", "No Real Solutions"));
				return;
			}

			regenerateTrees();
			 
			steps.add(loc.getMenuLaTeX("TakeSquareRoot", "Take square root"));
			LHS = ((ExpressionNode) evLHS).getLeft().toString(StringTemplate.defaultTemplate);
			RHS = helper.simplify("sqrt(" + RHS + ")");

			if (isZero(RHS)) {
				steps.add(LaTeX(LHS) + " = " + LaTeX(RHS));

				evLHS = helper.getExpressionTree(LHS);

				String constant = helper.findConstant(evLHS);
				addOrSubtract(constant, constant);
			} else {
				steps.add(LaTeX(LHS) + " = " + LaTeX(RHS) + " or " + LaTeX(LHS) + " = -(" + LaTeX(RHS) + ")");

				evLHS = helper.getExpressionTree(LHS);

				String constant = helper.findConstant(evLHS);

				if (helper.getValue(constant) < 0) {
					constant = helper.simplify("-(" + constant + ")");
					
					steps.add(loc.getMenuLaTeX("AddAToBothSides", "Add %0 to both sides", constant));

					LHS = helper.simplify(LHS + " + (" + constant + ")");

					steps.add(LaTeX(LHS) + " = " + LaTeX(RHS + " +(" + constant + ")") + " or " + LaTeX(LHS) + " = "
							+ LaTeX("- (" + RHS + ") + (" + constant + ")"));
					steps.add(LaTeX(LHS) + " = " + LaTeX(helper.regroup(RHS + " +(" + constant + ")")) + " or " + LaTeX(LHS) + " = "
							+ LaTeX(helper.regroup("- (" + RHS + ") + (" + constant + ")")));
				} else {
					steps.add(loc.getMenuLaTeX("SubtractAFromBothSides", "Subtract %0 from both sides", constant));

					LHS = helper.simplify(LHS + " - (" + constant + ")");

					steps.add(LaTeX(LHS) + " = " + LaTeX(RHS + " -(" + constant + ")") + "\text{ or }" + LaTeX(LHS) + " = "
							+ LaTeX("- (" + RHS + ") - (" + constant + ")"));
					steps.add(LaTeX(LHS) + " = " + LaTeX(helper.regroup(RHS + " -(" + constant + ")")) + "\text{ or }" + LaTeX(LHS) + " = "
							+ LaTeX(helper.regroup("- (" + RHS + ") - (" + constant + ")")));
				}
			}
			return;
		}
		
		String discriminant = "(" + b + ")^2-4(" + a + ")(" + c + ")";
		double discriminantValue = helper.getValue(discriminant);
		
		if(isSquare(discriminantValue)) {
			steps.add(loc.getMenuLaTeX("FactorEquation", "Factor equation"));
			LHS = helper.factor(LHS);
			addStep();

			evLHS = helper.getExpressionTree(LHS);

			solveProduct(evLHS);
			return;
		}
		
		// Case: default
		{
			steps.add(loc.getMenuLaTeX("UseQuadraticFormulaWithABC",
					"Use quadratic formula with a = %0, b = %1, c = %1", a, b, c));

			steps.add("x = \\frac{-b \\pm \\sqrt{b^2-4ac}}{2a}");
			
			if (discriminantValue < 0) {
				String formula = "\\frac{-(" + b + ") \\pm \\sqrt{" + discriminant + "}}{2(" + a + ")}";
				steps.add("x_{1,2} = " + formula);
				steps.add(loc.getMenuLaTeX("DeltaLessThanZero", "No real solutions"));
			} else if (discriminantValue == 0) {
				String formula = "-(" + b + ")/(2(" + a + "))";
				steps.add(LaTeX("x = " + helper.simplify(formula)));
				solutions.add(helper.simplify(formula));
			} else {
				String formula = "\\frac{-(" + b + ") \\pm \\sqrt{" + discriminant + "}}{2(" + a + ")}";
				String x1 = helper.simplify("(-(" + b + ")+sqrt(" + discriminant + "))/(2(" + a + "))");
				String x2 = helper.simplify("(-(" + b + ")-sqrt(" + discriminant + "))/(2(" + a + "))");

				steps.add("x_{1,2} = " + formula);
				steps.add("x_1 = " + LaTeX(x1) + ", x_2 = " + LaTeX(x2));

				solutions.add(x1);
				solutions.add(x2);
			}	
		}
	}

	private void solveTrivial() {
		String LHSconstant = helper.findConstant(evLHS);
		String RHSconstant = helper.findConstant(evRHS);
		
		subtract(helper.regroup(RHS + "- (" + RHSconstant + ")"));
		addOrSubtract(LHSconstant, LHSconstant);
		
		String root = helper.getPower(evLHS, "x");
		
		String toDivide = helper.findCoefficient(evLHS, "x^(" + root + ")");
		divide(toDivide);
		
		if (isEven(root) && helper.getValue(RHS) < 0) {
			steps.add(loc.getMenuLaTeX("NoRealSolutions", "No Real Solutions"));
			return;
		}

		steps.add(loc.getMenuLaTeX("TakeNthRoot", "Take %0th root", root));

		LHS = helper.simplify("(" + LHS + ")^(1/(" + root + "))");
		RHS = helper.simplify("(" + RHS + ")^(1/(" + root + "))");

		if (isEven(root) && !isZero(RHS)) {
			LHS = ((ExpressionNode) evLHS).getLeft().toString(StringTemplate.defaultTemplate);

			steps.add(LaTeX(LHS) + " = " + LaTeX(RHS) + " or " + LaTeX(LHS) + " = " + LaTeX("-(" + RHS + ")"));
		} else {
			steps.add(LaTeX(LHS) + " = " + LaTeX(RHS));
		}
	}

	private void numericSolutions() {
		subtract(RHS);

		String[] CASSolutions = helper.getCASSolutions(LHS, "0");

		if (CASSolutions.length == 0) {
			steps.add(loc.getMenuLaTeX("NoRealSolutions", "No real solutions"));
		} else {
			steps.add(loc.getMenuLaTeX("NumericSolutions", "Numeric Solutions: "));
			for (int i = 0; i < CASSolutions.length; i++) {
				steps.add("x_" + (i + 1) + " = " + CASSolutions[i]);
			}
		}

		solutions.addAll(Arrays.asList(CASSolutions));
	}

	private void regroup() {
		// TODO: proper checking of simplification
		if (helper.stripSpaces(helper.regroup(LHS)).length() < helper.stripSpaces(LHS).length() || 
				helper.stripSpaces(helper.regroup(RHS)).length() < helper.stripSpaces(RHS).length()) {
			LHS = helper.regroup(LHS);
			RHS = helper.regroup(RHS);

			steps.add(loc.getMenuLaTeX("SimplifyExpression",
					"Simplify Expression"));
			addStep();
			regenerateTrees();
		}
	}

	private void expandParantheses() {
		if (!isZero(helper.regroup(helper.expand(LHS) + " - (" + LHS + ")")) || 
				!isZero(helper.regroup(helper.expand(RHS) + " - (" + RHS + ")"))) {
			LHS = helper.expand(LHS);
			RHS = helper.expand(RHS);

			steps.add(loc.getMenuLaTeX("ExpandParentheses",
					"Expand Parentheses"));
			addStep();

			regenerateTrees();
		}
	}

	private void add(String toAdd) {
		if (!isZero(toAdd)) {
			LHS = LHS + "+(" + toAdd + ")";
			RHS = RHS + "+(" + toAdd + ")";

			steps.add(loc.getMenuLaTeX("AddAToBothSides",
					"Add %0 to both sides", LaTeX(toAdd)));
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

			steps.add(loc.getMenuLaTeX("SubtractAFromBothSides",
					"Subtract %0 from both sides", LaTeX(toSubtract)));
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

			steps.add(loc.getMenuLaTeX("MultiplyBothSidesByA",
					"Multiply both sides by %0", LaTeX(toMultiply)));
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

			steps.add(loc.getMenuLaTeX("DivideBothSidesByA",
					"Divide both sides by %0", LaTeX(toDivide)));
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

		steps.add(loc.getMenuLaTeX("SquareBothSides", "Square both sides"));
		addStep();

		LHS = helper.simplify(LHS);
		RHS = helper.simplify(RHS);
		addStep();

		regenerateTrees();

	}
	
	private String LaTeX(String toLaTeX) {
		if(toLaTeX.isEmpty()) {
			return "";
		}

		ExpressionValue ev = helper.getExpressionTree(toLaTeX);
		return ev.isExpressionNode() ? ((ExpressionNode) ev).toLaTeXString(false, StringTemplate.latexTemplate) : ev.toString(StringTemplate.defaultTemplate);
	}
	
	private void swapSides() {
		String temp = LHS;
		LHS = RHS;
		RHS = temp;

		inverted = !inverted;

		regenerateTrees();
	}

	private void addStep() {
		if (inverted) {
			steps.add(LaTeX(RHS) + " = " + LaTeX(LHS));
		} else {
			steps.add(LaTeX(LHS) + " = " + LaTeX(RHS));
		}
	}	

	private void regenerateTrees() {
		evLHS = helper.getExpressionTree(LHS);
		evRHS = helper.getExpressionTree(RHS);
	}
	
	private boolean isZero(String s) {
		return helper.stripSpaces(s).equals("") || helper.stripSpaces(s).equals("0");
	}

	private boolean isOne(String s) {
		return helper.stripSpaces(s).equals("") || helper.stripSpaces(s).equals("1");
	}

	private boolean isEven(String s) {
		return isEven(helper.getValue(s));
	}

	private boolean isEven(Double d) {
		return Math.floor(d / 2) * 2 == d;
	}

	private boolean isSquare(Double d) {
		return Math.floor(Math.sqrt(d)) * Math.floor(Math.sqrt(d)) == d;
	}
}
