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
import org.geogebra.common.kernel.arithmetic.FunctionVariable;
import org.geogebra.common.kernel.arithmetic.MyDouble;
import org.geogebra.common.main.Localization;
import org.geogebra.common.plugin.Operation;

import com.himamis.retex.editor.share.util.Unicode;

public class StepByStepSolver {
	private Kernel kernel;
	private Localization loc;

	private StepByStepHelper helper;
	private TreeOperations op;

	// needed for checking the validity of the solutions.
	private ExpressionValue origLHS;
	private ExpressionValue origRHS;

	private ExpressionValue evLHS;
	private ExpressionValue evRHS;

	private boolean inverted;

	private List<String> steps;
	private List<ExpressionValue> solutions;

	private String variable;
	private ExpressionValue evVariable;

	public StepByStepSolver(Kernel kernel, String LHS, String RHS, String variable) {
		this.kernel = kernel;
		this.loc = kernel.getLocalization();

		helper = new StepByStepHelper(kernel);
		op = new TreeOperations(kernel);

		this.origLHS = helper.getExpressionTree(LHS);
		this.origRHS = helper.getExpressionTree(RHS);

		this.variable = variable;
		evVariable = helper.getExpressionTree(variable);

		evLHS = helper.getExpressionTree(LHS);
		evRHS = helper.getExpressionTree(RHS);

		inverted = false;
	}

	public List<String> getSteps() {
		steps = new ArrayList<String>();
		solutions = new ArrayList<ExpressionValue>();

		// I. step: regrouping
		addStep();
		regroup();

		// II. step: making denominators disappear
		ExpressionValue bothSides = op.multiply(evLHS, evRHS);
		if (helper.shouldMultiply(bothSides) || helper.countOperation(bothSides, Operation.DIVIDE) > 1) {
			ExpressionValue denominators = helper.getDenominator(bothSides);
			multiply(denominators);
		}

		// III. step: solving as a product
		if (isZero(evLHS) && helper.isProduct(evRHS)) {
			solveProduct(evRHS);
			return checkSolutions();
		} else if (isZero(evRHS) && helper.isProduct(evLHS)) {
			solveProduct(evLHS);
			return checkSolutions();
		}

		// IV. step: expanding parentheses
		expandParantheses();

		// V. step: getting rid of square roots
		bothSides = op.add(evLHS, evRHS);
		if (helper.countRoots(bothSides) > 0) {
			solveIrrational();
		}

		// VI. Step: equations containing absolute values
		bothSides = op.add(evLHS, evRHS);
		if (helper.countOperation(bothSides, Operation.ABS) > 0) {
			solveAbsoluteValue();
			return checkSolutions();
		}

		int degreeDiff = helper.degree(op.subtract(evLHS, evRHS));
		int degreeLHS = helper.degree(evLHS);
		int degreeRHS = helper.degree(evRHS);

		// Checking if it's in polynomial form.
		if (degreeDiff == -1) {
			steps.add(loc.getMenuLaTeX("CannotSolve", "Can't solve"));
			return steps;
		}

		// Swapping sides, if necessary
		if (degreeRHS > degreeLHS || (degreeRHS == degreeLHS && helper.getCoefficientValue(evRHS, variable + "^" + degreeRHS) > helper.getCoefficientValue(
				evLHS, variable + "^" + degreeLHS))) {
			swapSides();
		}

		// VII. step: solving linear equations
		if (degreeDiff <= 1) {
			solveLinear();
			return checkSolutions();
		}

		// VIII. step: solving quadratic equations
		if (degreeDiff <= 2) {
			solveQuadratic();
			return checkSolutions();
		}

		// XI. step: solving equations that can be reduced to a linear (ax^n = b)
		if (helper.isTrivial(evRHS, evLHS, variable)) {
			solveTrivial();
			return checkSolutions();
		}

		subtract(evRHS);

		// X. step: solving equations that can be reduced to a quadratic (ax^(2n) + bx^n + c = 0)
		if (helper.canBeReducedToQuadratic(evLHS, variable)) {
			reduceToQuadratic();
			return checkSolutions();
		}

		// XI. step: completing the cube
		if (helper.canCompleteCube(evLHS, variable)) {
			completeCube();
			return checkSolutions();
		}

		// XII. step: finding and factoring rational roots
		if (helper.integerCoefficients(evLHS, variable)) {
			findRationalRoots();

			if (solutions.size() > 0) {
				return checkSolutions();
			}
		}

		// XIII. step: numeric solutions
		numericSolutions();
		return checkSolutions();
	}

	public List<ExpressionValue> getSolutions() {
		if (solutions == null) {
			getSteps();
		}

		return solutions;
	}

	// TODO: something else :-?
	private List<String> checkSolutions() {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < solutions.size(); i++) {
			sb.append(variable);
			sb.append(" = ");
			sb.append(LaTeX(solutions.get(i)));
			if (i < solutions.size() - 1) {
				sb.append(", ");
			}
		}

		if (solutions.size() == 1) {
			steps.add(loc.getMenuLaTeX("Solution", "Solution: ", sb.toString()));
		} else if (solutions.size() > 1) {
			steps.add(loc.getMenuLaTeX("Solutions", "Solutions: ", sb.toString()));
		}

		ExpressionValue bothSides = op.add(origLHS, origRHS);

		ExpressionValue denominators = helper.getDenominator(bothSides);
		ExpressionValue roots = helper.getSQRoots(bothSides);

		if (!helper.containsVariable(denominators) && !helper.containsVariable(roots) || solutions.size() == 0) {
			return steps;
		}

		steps.add(loc.getMenuLaTeX("CheckingValidityOfSolutions", "Checking validity of solutions"));

		for (int i = 0; i < solutions.size(); i++) {
			if (helper.isValidSolution(origLHS, origRHS, solutions.get(i), variable)) {
				steps.add(loc.getMenuLaTeX("ValidSolution", "Valid Solution: %0 = %1", variable, LaTeX(solutions.get(i))));
			} else {
				steps.add(loc.getMenuLaTeX("InvalidSolution", "Invalid Solution: %0 " + Unicode.NOTEQUAL + " %1", variable, LaTeX(solutions.get(i))));
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
			StepByStepSolver sbss = new StepByStepSolver(kernel, equations.get(i), "0", variable);
			steps.addAll(sbss.getSteps());
			solutions.addAll(sbss.getSolutions());
		}
	}

	private void solveIrrational() {
		int sqrtNum = helper.countRoots(evLHS) + helper.countRoots(evRHS);

		if (helper.countRoots(evRHS) > helper.countRoots(evLHS)) {
			swapSides();
		}

		if (sqrtNum > 3) {
			return;
		}

		if (sqrtNum == 1) {
			ExpressionValue nonIrrational = helper.getNonIrrational(evLHS);
			addOrSubtract(nonIrrational);
			square();
		}

		if (sqrtNum == 2) {
			ExpressionValue diff = helper.regroup(op.subtract(evLHS, evRHS));
			if (isZero(helper.getNonIrrational(diff))) {
				ExpressionValue nonIrrational = helper.getNonIrrational(evLHS);
				addOrSubtract(nonIrrational);
				if (helper.countRoots(evLHS) == 2) {
					ExpressionValue oneRoot = helper.getOneSquareRoot(evLHS);
					addOrSubtract(oneRoot);
				}
				square();
			} else {
				ExpressionValue rootsRHS = helper.getSQRoots(evRHS);
				addOrSubtract(rootsRHS);
				ExpressionValue nonIrrational = helper.getNonIrrational(evLHS);
				addOrSubtract(nonIrrational);
				square();
				solveIrrational();
			}
		}

		if (sqrtNum == 3) {
			ExpressionValue nonIrrational = helper.getNonIrrational(evLHS);
			addOrSubtract(nonIrrational);

			while (helper.countRoots(evRHS) > 1) {
				ExpressionValue oneRoot = helper.getOneSquareRoot(evRHS);
				addOrSubtract(oneRoot);
			}

			if (helper.countRoots(evLHS) == 3) {
				ExpressionValue oneRoot = helper.getOneSquareRoot(evLHS);
				addOrSubtract(oneRoot);
			}

			square();
			solveIrrational();
		}
	}

	private void solveAbsoluteValue() {
		ExpressionValue bothSides = op.add(evLHS, evRHS);
		ArrayList<String> absoluteValues = new ArrayList<String>();
		helper.getAbsoluteValues(absoluteValues, bothSides);

		ArrayList<ExpressionValue> roots = new ArrayList<ExpressionValue>();
		for (int i = 0; i < absoluteValues.size(); i++) {
			roots.addAll(Arrays.asList(helper.getCASSolutions(absoluteValues.get(i), "0", variable)));
		}

		Collections.sort(roots, new Comparator<ExpressionValue>() {
			@Override
			public int compare(ExpressionValue s1, ExpressionValue s2) {
				return Double.compare(helper.getValue(s1), helper.getValue(s2));
			}
		});

		for (int i = 0; i <= roots.size(); i++) {
			solveAbsoulteValueEquation(i == 0 ? "-inf" : roots.get(i - 1).toString(StringTemplate.defaultTemplate), i == roots.size() ? "+inf"
					: roots.get(i).toString(StringTemplate.defaultTemplate));
		}
	}

	private void solveAbsoulteValueEquation(String a, String b) {
		steps.add(loc.getMenuLaTeX("SolvingBetween", "Solving %0 = %1 between %2 and %3", LaTeX(evLHS), LaTeX(evRHS), a, b));

		String LHSevaluated = asString(helper.swapAbsInTree(evLHS, a, b, variable));
		String RHSevaluated = asString(helper.swapAbsInTree(evRHS, a, b, variable));

		StepByStepSolver sbss = new StepByStepSolver(kernel, LHSevaluated, RHSevaluated, variable);
		steps.addAll(sbss.getSteps());

		double aVal = helper.getValue(a);
		double bVal = helper.getValue(b);

		List<ExpressionValue> partialSolutions = sbss.getSolutions();
		for (int i = 0; i < partialSolutions.size(); i++) {

			if (partialSolutions.get(i).equals("all")) {
				steps.add(loc.getMenuLaTeX("AllNumbersBetween", "All numbers between %0 and %1 are solutions", a, b));
				// solutions.add("(" + a + ", " + b + ")");
			} else {
				double xVal = helper.getValue(partialSolutions.get(i));

				if (aVal <= xVal && xVal <= bVal) {
					steps.add(loc.getMenuLaTeX("ValidSolutionAbs", "%0 = %1 is between %2 and %3", variable, LaTeX(partialSolutions.get(i)), a, b));
				} else {
					steps.add(loc.getMenuLaTeX("InvalidSolutionAbs", "%0 = %1 is not between %2 and %3", variable, LaTeX(partialSolutions.get(i)), a, b));
				}
			}
		}
	}

	private void solveLinear() {
		regroup();

		ExpressionValue RHSlinear = helper.findVariable(evRHS, variable);
		ExpressionValue RHSconstant = helper.findConstant(evRHS);

		ExpressionValue nonLinear = helper.regroup(op.subtract(evRHS, op.add(RHSlinear, RHSconstant)));
		addOrSubtract(nonLinear);

		addOrSubtract(RHSlinear);

		ExpressionValue constant = helper.findConstant(evLHS);
		addOrSubtract(constant);

		ExpressionValue toDivide = helper.findCoefficient(evLHS, variable);
		divide(toDivide);

		int degreeLHS = helper.degree(evLHS);
		int degreeRHS = helper.degree(evRHS);

		if (degreeLHS == degreeRHS && degreeRHS == 0) {
			if (helper.isEqual(evLHS, evRHS)) {
				// solutions.add("all");
				return;
			}
			steps.add(loc.getMenuLaTeX("NoSolutions", "No Solutions"));
			return;
		}

		solutions.add(evRHS);
	}

	private void solveQuadratic() {
		ExpressionValue RHSconstant = helper.findConstant(evRHS);
		ExpressionValue RHSlinear = helper.findVariable(evRHS, variable);
		ExpressionValue RHSquadratic = helper.findVariable(evRHS, variable + "^2");

		ExpressionValue nonQuadratic = helper.regroup(op.subtract(evRHS, op.add(RHSconstant, op.add(RHSlinear,
				RHSquadratic))));

		addOrSubtract(nonQuadratic);

		ExpressionValue diff = helper.regroup(op.subtract(evLHS, evRHS));

		if (isZero(helper.findVariable(diff, variable))) {
			ExpressionValue quadratic = helper.findVariable(evRHS, variable + "^2");
			addOrSubtract(quadratic);

			ExpressionValue constant = helper.findConstant(evLHS);
			addOrSubtract(constant);

			ExpressionValue toDivide = helper.findCoefficient(evLHS, variable + "^2");
			divide(toDivide);

			double RHSvalue = helper.getValue(evRHS.toString(StringTemplate.defaultTemplate));
			if (RHSvalue < 0) {
				steps.add(loc.getMenuLaTeX("NoRealSolutions", "No real solutions"));
				return;
			} else if (RHSvalue == 0) {
				nthroot("2");

				solutions.add(evRHS);
			} else {
				nthroot("2");

				solutions.add(evRHS);
				solutions.add(helper.regroup(op.minus(evRHS)));
			}

			return;
		}

		subtract(evRHS);

		String a = asString(helper.findCoefficient(evLHS, variable + "^2"));
		String b = asString(helper.findCoefficient(evLHS, variable));
		String c = asString(helper.findConstant(evLHS));

		if (isZero(a)) {
			solveLinear();
			return;
		}

		if (isZero(c)) {
			steps.add(loc.getMenuLaTeX("FactorEquation", "Factor equation"));
			evLHS = helper.factor(evLHS);
			addStep();

			solveProduct(evLHS);
			return;
		}

		if (isOne(a) && isEven(b)) {
			ExpressionValue toComplete = helper.regroup(helper.getExpressionTree(c + " - ((" + b + ")/2)^2"));

			steps.add(loc.getMenuLaTeX("CompleteSquare", "Complete the square"));

			addOrSubtract(toComplete);
			evLHS = helper.factor(evLHS);
			addStep();

			if (helper.getValue(evRHS) < 0) {
				steps.add(loc.getMenuLaTeX("NoRealSolutions", "No Real Solutions"));
				return;
			}

			nthroot("2");

			if (isZero(evRHS)) {
				solveLinear();
			} else {
				ExpressionValue constant = helper.findConstant(evLHS);

				if (helper.getValue(constant) < 0) {
					constant = helper.simplify(op.minus(constant));

					steps.add(loc.getMenuLaTeX("AddAToBothSides", "Add %0 to both sides", LaTeX(constant)));

					evLHS = helper.simplify(op.add(evLHS, constant));

					steps.add(loc.getMenuLaTeX("AOrB", "%0 or %1", LaTeX(evLHS) + " = " + LaTeX(evRHS) + " + (" + LaTeX(constant) + ")",
							LaTeX(evLHS) + " = - (" + LaTeX(evRHS) + ") + (" + LaTeX(constant) + ")"));

					solutions.add(helper.regroup(op.add(evRHS, constant)));
					solutions.add(helper.regroup(op.add(op.minus(evRHS), constant)));
				} else {
					steps.add(loc.getMenuLaTeX("SubtractAFromBothSides", "Subtract %0 from both sides", LaTeX(constant)));

					evLHS = helper.simplify(op.subtract(evLHS, constant));

					steps.add(loc.getMenuLaTeX("AOrB", "%0 or %1", LaTeX(evLHS) + " = " + LaTeX(evRHS) + " - (" + LaTeX(constant) + ")", LaTeX(evLHS) + " = - ("
							+ LaTeX(evRHS) + ") - (" + LaTeX(constant) + ")"));

					solutions.add(helper.regroup(op.subtract(evRHS, constant)));
					solutions.add(helper.regroup(op.subtract(op.minus(evRHS), constant)));
				}
			}
			return;
		}

		String discriminant = "(" + b + ")^2-4*(" + a + ")*(" + c + ")";
		double discriminantValue = helper.getValue(discriminant);

		if (isSquare(discriminantValue)) {
			steps.add(loc.getMenuLaTeX("FactorEquation", "Factor equation"));
			evLHS = helper.factor(evLHS);
			addStep();

			solveProduct(evLHS);
			return;
		}

		// Case: default
		{
			steps.add(loc.getMenuLaTeX("UseQuadraticFormulaWithABC", "Use quadratic formula with a = %0, b = %1, c = %2", LaTeX(a), LaTeX(b), LaTeX(c)));

			steps.add(variable + " = \\frac{-b \\pm \\sqrt{b^2-4ac}}{2a}");

			String formula = "\\frac{" + LaTeX("-(" + b + ")") + "\\pm \\sqrt{" + LaTeX(discriminant) + "}}{" + LaTeX("2 * (" + a + ")") + "}";
			steps.add(variable + "_{1,2} = " + formula);
			String simplifiedFormula = "\\frac{" + LaTeX(helper.regroup("-(" + b + ")")) + "\\pm \\sqrt{" + LaTeX(helper.regroup(discriminant)) + "}}{" + LaTeX(
					helper.regroup("2 * (" + a + ")")) + "}";
			steps.add(variable + "_{1,2} = " + simplifiedFormula);

			if (discriminantValue < 0) {
				steps.add(loc.getMenuLaTeX("NoRealSolutions", "No real solutions"));
			} else if (discriminantValue == 0) {
				String solution = "-(" + b + ")/(2(" + a + "))";

				solutions.add(helper.getExpressionTree(helper.simplify(solution)));
			} else {
				String solution1 = helper.simplify("(-(" + b + ")+sqrt(" + discriminant + "))/(2(" + a + "))");
				String solution2 = helper.simplify("(-(" + b + ")-sqrt(" + discriminant + "))/(2(" + a + "))");

				solutions.add(helper.getExpressionTree(solution1));
				solutions.add(helper.getExpressionTree(solution2));
			}
		}
	}

	private void solveTrivial() {
		ExpressionValue LHSconstant = helper.findConstant(evLHS);
		ExpressionValue RHSconstant = helper.findConstant(evRHS);

		subtract(helper.regroup(op.subtract(evRHS, RHSconstant)));
		addOrSubtract(LHSconstant);

		String root = helper.getPower(evLHS, variable);

		ExpressionValue toDivide = helper.findCoefficient(evLHS, variable + "^(" + root + ")");
		divide(toDivide);

		if (isEven(root) && helper.getValue(evRHS) < 0) {
			steps.add(loc.getMenuLaTeX("NoRealSolutions", "No Real Solutions"));
			return;
		}

		nthroot(root);

		solutions.add(evRHS);
		if (isEven(root)) {
			solutions.add(helper.regroup(op.minus(evRHS)));
		}
	}

	private void reduceToQuadratic() {
		int degree = helper.degree(evLHS);

		ExpressionValue coeffHigh = helper.findCoefficient(evLHS, variable + "^" + degree);
		ExpressionValue coeffLow = helper.findCoefficient(evLHS, variable + "^" + degree / 2);
		ExpressionValue constant = helper.findConstant(evLHS);
		
		ExpressionValue newVariable = new FunctionVariable(kernel, "y");

		steps.add(loc.getMenuLaTeX("ReplaceAWithB", "Replace %0 with %1", variable + "^" + degree / 2, "y"));
		
		ExpressionValue newEquation = op.multiply(coeffHigh, op.power(newVariable, "2"));
		newEquation = op.add(newEquation, op.multiply(coeffLow, newVariable));
		newEquation = op.add(newEquation, constant);
		
		StepByStepSolver ssbs = new StepByStepSolver(kernel, asString(newEquation), "0", newVariable.toString(StringTemplate.defaultTemplate));
		steps.addAll(ssbs.getSteps());

		List<ExpressionValue> tempSolutions = ssbs.getSolutions();
		for (int i = 0; i < tempSolutions.size(); i++) {
			StepByStepSolver tempSsbs = new StepByStepSolver(kernel, variable + "^" + degree / 2, asString(tempSolutions.get(i)), variable);
			steps.addAll(tempSsbs.getSteps());
			solutions.addAll(tempSsbs.getSolutions());
		}
	}

	private void completeCube() {
		ExpressionValue constant = helper.findConstant(evLHS);
		ExpressionValue quadratic = helper.findCoefficient(evLHS, variable + "^2");

		String toComplete = helper.regroup(constant + " - ((" + quadratic + ")/3)^3");

		steps.add(loc.getMenuLaTeX("CompleteCube", "Complete the cube"));

		addOrSubtract(helper.getExpressionTree(toComplete));
		evLHS = helper.factor(evLHS);
		addStep();

		nthroot("3");

		solveLinear();
	}

	private void findRationalRoots() {
		int degree = helper.degree(evLHS);

		int highestOrder = Math.abs((int) helper.getCoefficientValue(evLHS, variable + "^" + degree));
		int constant = Math.abs((int) helper.getValue(helper.findConstant(evLHS)));

		ExpressionValue factored = new MyDouble(kernel, 1);

		for (int i = 1; i <= highestOrder; i++) {
			for (int j = -constant; j <= constant; j++) {
				ExpressionValue solution = helper.getExpressionTree("((" + j + ") / (" + i + "))");
				double evaluated = helper.evaluateAt(evLHS, solution, variable);

				while (evaluated == 0) {
					factored = helper.regroup(op.multiply(factored, op.subtract(evVariable, solution)));
					evLHS = helper.simplify(op.divide(evLHS, op.subtract(evVariable, solution)));

					evaluated = helper.evaluateAt(evLHS, solution, variable);
				}
			}
		}

		if (!isOne(factored)) {
			steps.add(loc.getMenuLaTeX("RationalRootTheorem",
					"A polynomial equation with integer coefficients has all of its rational roots in the form p/q, where p divides the constant term and q divides the coefficient of the highest order term"));

			steps.add(loc.getMenuLaTeX("TrialAndError", "Find the roots by trial and error, and factor them out"));

			evLHS = helper.regroup(op.multiply(evLHS, factored));
			addStep();

			solveProduct(evLHS);
		}
	}

	private void numericSolutions() {
		ExpressionValue[] CASSolutions = helper.getCASSolutions(asString(evLHS), "0", variable);

		if (CASSolutions.length == 0) {
			steps.add(loc.getMenuLaTeX("NoRealSolutions", "No real solutions"));
		} else {
			steps.add(loc.getMenuLaTeX("SolveNumerically", "Solve numerically: "));
		}

		solutions.addAll(Arrays.asList(CASSolutions));
	}

	private void regroup() {
		// TODO: proper checking of simplification
		ExpressionValue regroupedLHS = helper.regroup(evLHS);
		ExpressionValue regroupedRHS = helper.regroup(evRHS);
		
		if (!asString(regroupedLHS).equals(asString(evLHS)) || !asString(regroupedRHS).equals(asString(evRHS))) {
			evLHS = regroupedLHS;
			evRHS = regroupedRHS;
	
			steps.add(loc.getMenuLaTeX("SimplifyExpression", "Simplify Expression"));
			addStep();			
		}
	}

	private void expandParantheses() {
		ExpressionValue expandedLHS = helper.expand(evLHS);
		ExpressionValue expandedRHS = helper.expand(evRHS);
		
		if (!isZero(helper.regroup(op.subtract(expandedLHS, evLHS))) || !isZero(helper.regroup(op.subtract(expandedRHS, evRHS)))) {
			evLHS = expandedLHS;
			evRHS = expandedRHS;

			steps.add(loc.getMenuLaTeX("ExpandParentheses", "Expand Parentheses"));
			addStep();
		}
	}

	private void add(ExpressionValue toAdd) {
		if (!isZero(toAdd)) {
			evLHS = op.add(evLHS, toAdd);
			evRHS = op.add(evRHS, toAdd);

			steps.add(loc.getMenuLaTeX("AddAToBothSides", "Add %0 to both sides", LaTeX(toAdd)));
			addStep();

			evLHS = helper.regroup(evLHS);
			evRHS = helper.regroup(evRHS);
			addStep();
		}
	}

	private void subtract(ExpressionValue toSubtract) {
		if (!isZero(toSubtract)) {
			evLHS = op.subtract(evLHS, toSubtract);
			evRHS = op.subtract(evRHS, toSubtract);

			steps.add(loc.getMenuLaTeX("SubtractAFromBothSides", "Subtract %0 from both sides", LaTeX(toSubtract)));
			addStep();

			evLHS = helper.regroup(evLHS);
			evRHS = helper.regroup(evRHS);
			addStep();
		}
	}

	private void addOrSubtract(ExpressionValue ev) {
		if(ev == null) {
			return;
		}

		if (ev.evaluatesToNumber(false) && ev.evaluateDouble() < 0) {
			add(helper.simplify(op.minus(ev)));
			return;
		} else if (ev.isExpressionNode()) {
			ExpressionNode en = (ExpressionNode) ev;

			if (en.getOperation() == Operation.MULTIPLY) {
				if (en.getLeft().isConstant() && en.getLeft().evaluateDouble() < 0) {
					add(helper.simplify(op.minus(en)));
					return;
				} else if (en.getRight().isConstant() && en.getRight().evaluateDouble() < 0) {
					add(helper.simplify(op.minus(en)));
					return;
				}
			}
		}

		subtract(ev);
	}

	private void multiply(ExpressionValue toMultiply) {
		if (!isOne(toMultiply) && !isZero(toMultiply)) {
			evLHS = op.multiply(evLHS, toMultiply);
			evRHS = op.multiply(evRHS, toMultiply);

			steps.add(loc.getMenuLaTeX("MultiplyBothSidesByA", "Multiply both sides by %0", LaTeX(toMultiply)));
			addStep();

			evLHS = helper.simplify(evLHS);
			evRHS = helper.simplify(evRHS);
			addStep();
		}
	}

	private void divide(ExpressionValue toDivide) {
		if (!isOne(toDivide) && !isZero(toDivide)) {
			evLHS = op.divide(evLHS, toDivide);
			evRHS = op.divide(evRHS, toDivide);

			steps.add(loc.getMenuLaTeX("DivideBothSidesByA", "Divide both sides by %0", LaTeX(toDivide)));
			addStep();

			evLHS = helper.regroup(evLHS);
			evRHS = helper.regroup(evRHS);
			addStep();
		}
	}

	private void square() {
		evLHS = op.power(evLHS, "2");
		evRHS = op.power(evRHS, "2");

		steps.add(loc.getMenuLaTeX("SquareBothSides", "Square both sides"));
		addStep();

		evLHS = helper.simplify(evLHS);
		evRHS = helper.simplify(evRHS);
		addStep();
	}

	private void nthroot(String root) {
		if ("2".equals(root)) {
			steps.add(loc.getMenuLaTeX("TakeSquareRoot", "Take square root"));
		} else if ("3".equals(root)) {
			steps.add(loc.getMenuLaTeX("TakeCubeRoot", "Take cube root"));
		} else {
			steps.add(loc.getMenuLaTeX("TakeNthRoot", "Take %0th root", root));
		}

		evLHS = op.root(evLHS, root);
		evRHS = op.root(evRHS, root);

		if (isEven(root) && !isZero(evRHS)) {
			steps.add(LaTeX(evLHS) + " =  \\pm (" + LaTeX(evRHS) + ")");
		} else {
			addStep();
		}

		evLHS = helper.simplify(evLHS);
		evRHS = helper.simplify(evRHS);

		if (isEven(root)) {
			evLHS = ((ExpressionNode) evLHS).getLeft();
		}

		if (isEven(root) && !isZero(evRHS)) {
			steps.add(LaTeX(evLHS) + " =  \\pm (" + LaTeX(evRHS) + ")");
		} else {
			addStep();
		}
	}

	private String LaTeX(String toLaTeX) {
		if (toLaTeX.isEmpty()) {
			return "";
		}

		ExpressionValue ev = helper.getExpressionTree(toLaTeX);
		return ev.toLaTeXString(false, StringTemplate.latexTemplate);
	}

	private static String LaTeX(ExpressionValue toLaTeX) {
		if (toLaTeX == null) {
			return "";
		}

		return toLaTeX.toLaTeXString(false, StringTemplate.latexTemplate);
	}

	private static String asString(ExpressionValue ev) {
		if(ev == null) {
			return "0";
		} 
		return ev.toString(StringTemplate.defaultTemplate);
	}

	private void swapSides() {
		ExpressionValue temp = evLHS;
		evLHS = evRHS;
		evRHS = temp;

		inverted = !inverted;
	}

	private void addStep() {
		if (inverted) {
			steps.add(LaTeX(evRHS) + " = " + LaTeX(evLHS));
		} else {
			steps.add(LaTeX(evLHS) + " = " + LaTeX(evRHS));
		}
	}

	private static String stripSpaces(String s) {
		return s.replaceAll(" ", "");
	}

	private static boolean isZero(String s) {
		return stripSpaces(s).equals("") || stripSpaces(s).equals("0");
	}

	private static boolean isZero(ExpressionValue ev) {
		return ev == null || isZero(ev.toString(StringTemplate.defaultTemplate));
	}

	private static boolean isOne(String s) {
		return stripSpaces(s).equals("") || stripSpaces(s).equals("1");
	}

	private static boolean isOne(ExpressionValue ev) {
		return ev == null || isOne(ev.toString(StringTemplate.defaultTemplate));
	}

	private boolean isEven(String s) {
		return isEven(helper.getValue(s));
	}

	private static boolean isEven(Double d) {
		return Math.floor(d / 2) * 2 == d;
	}

	private static boolean isSquare(Double d) {
		return Math.floor(Math.sqrt(d)) * Math.floor(Math.sqrt(d)) == d;
	}
}
