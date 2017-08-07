package org.geogebra.common.kernel.stepbystep;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.stepbystep.steptree.StepConstant;
import org.geogebra.common.kernel.stepbystep.steptree.StepNode;
import org.geogebra.common.kernel.stepbystep.steptree.StepOperation;
import org.geogebra.common.kernel.stepbystep.steptree.StepVariable;
import org.geogebra.common.main.Localization;
import org.geogebra.common.plugin.Operation;
import org.geogebra.common.util.debug.Log;

public class EquationSteps {
	private Kernel kernel;
	private Localization loc;

	// needed for checking the validity of the solutions.
	private StepNode origLHS;
	private StepNode origRHS;

	private StepNode LHS;
	private StepNode RHS;

	private boolean inverted;
	private boolean intermediate;

	private SolutionBuilder steps;
	private List<StepNode> solutions;

	private StepVariable variable;

	public EquationSteps(Kernel kernel, String LHS, String RHS, String variable) {
		this.kernel = kernel;
		this.loc = kernel.getLocalization();

		this.LHS = StepNode.getStepTree(LHS, kernel.getParser());
		this.RHS = StepNode.getStepTree(RHS, kernel.getParser());

		origLHS = this.LHS.deepCopy();
		origRHS = this.RHS.deepCopy();

		this.variable = new StepVariable(variable);

		inverted = false;
		intermediate = false;
	}

	public void setIntermediate() {
		intermediate = true;
	}

	public SolutionStep getSteps() {
		steps = new SolutionBuilder();
		solutions = new ArrayList<StepNode>();

		// I. step: regrouping
		// addStep();

		if (!intermediate) {
			steps.add(loc.getMenuLaTeX("Solve", "Solve: %0", LaTeX(LHS) + " = " + LaTeX(RHS)), SolutionStepTypes.EQUATION);
			steps.levelDown();
			regroup();
		}

		// II. step: making denominators disappear
		StepNode bothSides = StepNode.multiply(LHS, RHS);
		if (StepHelper.shouldMultiply(bothSides) || StepHelper.countOperation(bothSides, Operation.DIVIDE) > 1) {
			StepNode denominators = StepHelper.getDenominator(bothSides, kernel);
			multiply(denominators);
		}

		// III. step: solving as a product
		if (isZero(LHS) && RHS.isOperation(Operation.MULTIPLY)) {
			solveProduct((StepOperation) RHS);
			return checkSolutions();
		} else if (isZero(RHS) && LHS.isOperation(Operation.MULTIPLY)) {
			solveProduct((StepOperation) LHS);
			return checkSolutions();
		}

		// V. step: getting rid of square roots
		bothSides = StepNode.add(LHS, RHS);
		if (StepHelper.countNonConstOperation(bothSides, Operation.NROOT) > 0) {
			solveIrrational();
		}

		int degreeDiff = StepHelper.degree(StepNode.subtract(LHS, RHS), kernel);
		int degreeLHS = StepHelper.degree(LHS, kernel);
		int degreeRHS = StepHelper.degree(RHS, kernel);

		// Swapping sides, if necessary
		if (degreeRHS == -1 || degreeLHS == -1) {
			if (degreeRHS == -1 && degreeLHS != -1) {
				swapSides();
			}
		} else if (degreeRHS > degreeLHS
				|| (degreeRHS == degreeLHS && StepHelper.getCoefficientValue(RHS, StepNode.power(variable, degreeRHS)) > StepHelper
						.getCoefficientValue(LHS, StepNode.power(variable, degreeLHS)))) {
			swapSides();
		}

		// IX. step: taking roots, when necessary (ax^n = constant or ay^n = bz^n, where y and z are expressions in x)
		if (StepHelper.shouldTakeRoot(RHS, LHS)) {
			Log.error("ROOT");
			takeRoot();
			return checkSolutions();
		}
		// IV. step: expanding parentheses
		expandParantheses();

		// VI. Step: equations containing absolute values
		bothSides = StepNode.add(LHS, RHS);
		if (StepHelper.countOperation(bothSides, Operation.ABS) > 0) {
			solveAbsoluteValue();
			return checkSolutions();
		}

		degreeDiff = StepHelper.degree(StepNode.subtract(LHS, RHS), kernel);

		// VII. step: solving linear equations
		if (degreeDiff != -1 && degreeDiff <= 1) {

			solveLinear();
			return checkSolutions();
		}



		// VIII. step: solving quadratic equations
		if (degreeDiff != -1 && degreeDiff <= 2) {
			solveQuadratic();
			return checkSolutions();
		}

		// Checking if it's in polynomial form.
		if (degreeDiff == -1) {
			steps.add(loc.getMenuLaTeX("CannotSolve", "Can't solve"), SolutionStepTypes.COMMENT);
			return steps.getSteps();
		}

		subtract(RHS);

		// X. step: solving equations that can be reduced to a quadratic (ax^(2n) + bx^n + c = 0)
		if (StepHelper.canBeReducedToQuadratic(LHS, variable, kernel)) {
			reduceToQuadratic();
			return checkSolutions();
		}

		// XI. step: completing the cube
		if (StepHelper.canCompleteCube(LHS, variable, kernel)) {
			completeCube();
			return checkSolutions();
		}

		// XII. step: finding and factoring rational roots
		if (StepHelper.integerCoefficients(LHS, variable, kernel)) {
			findRationalRoots();

			if (solutions.size() > 0) {
				return checkSolutions();
			}
		}

		// XIII. step: numeric solutions
		numericSolutions();
		return checkSolutions();
	}

	public List<StepNode> getSolutions() {
		if (solutions == null) {
			getSteps();
		}

		return solutions;
	}

	// TODO: something else :-?
	private SolutionStep checkSolutions() {
		if(intermediate) {
			return steps.getSteps();
		}

		for (int i = 0; i < solutions.size(); i++) {
			if (Double.isNaN(solutions.get(i).getValue())) {
				steps.add(loc.getMenuLaTeX("TrueForAll", "The equation is true for all values of %0", variable.toString()),
						SolutionStepTypes.SOLUTION);
				return steps.getSteps();
			}
		}

		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < solutions.size(); i++) {
			sb.append(variable);
			sb.append(" = ");
			sb.append(LaTeX(solutions.get(i)));
			if (i < solutions.size() - 1) {
				sb.append(", ");
			}
		}

		if (solutions.size() == 0) {
			steps.add(loc.getMenuLaTeX("NoRealSolutions", "No Real Solutions"), SolutionStepTypes.SOLUTION);
		} else if (solutions.size() == 1) {
			steps.add(loc.getMenuLaTeX("Solution", "Solution: %0", sb.toString()), SolutionStepTypes.SOLUTION);
		} else if (solutions.size() > 1) {
			steps.add(loc.getMenuLaTeX("Solutions", "Solutions: %0", sb.toString()), SolutionStepTypes.SOLUTION);
		}

		StepNode bothSides = StepNode.add(origLHS, origRHS);

		StepNode denominators = StepHelper.getDenominator(bothSides, kernel);
		StepNode roots = StepHelper.getSQRoots(bothSides);

		if ((denominators == null || denominators.isConstant()) && (roots == null || roots.isConstant()) || solutions.size() == 0) {
			return steps.getSteps();
		}

		steps.add(loc.getMenuLaTeX("CheckingValidityOfSolutions", "Checking validity of solutions"), SolutionStepTypes.COMMENT);

		steps.levelDown();

		for (int i = 0; i < solutions.size(); i++) {
			if (StepHelper.isValidSolution(origLHS, origRHS, solutions.get(i), variable, kernel)) {
				steps.add(loc.getMenuLaTeX("ValidSolution", "Valid Solution: %0", variable + "=" + LaTeX(solutions.get(i))),
						SolutionStepTypes.COMMENT);
			} else {
				steps.add(loc.getMenuLaTeX("InvalidSolution", "Invalid Solution: %0", variable + "\\neq" + LaTeX(solutions.get(i))),
						SolutionStepTypes.COMMENT);
				solutions.remove(solutions.get(i));
				i--;
			}
		}

		return steps.getSteps();
	}

	private void solveProduct(StepOperation product) {
		steps.add(loc.getMenuLaTeX("ProductIsZero", "Product is zero"), SolutionStepTypes.COMMENT);

		for (int i = 0; i < product.noOfOperands(); i++) {
			EquationSteps sbss = new EquationSteps(kernel, product.getSubTree(i).toString(), "0", variable + "");
			steps.addAll(sbss.getSteps());
			solutions.addAll(sbss.getSolutions());
		}
	}

	private void solveIrrational() {
		int sqrtNum = StepHelper.countNonConstOperation(LHS, Operation.NROOT) + StepHelper.countNonConstOperation(RHS, Operation.NROOT);

		if (StepHelper.countNonConstOperation(RHS, Operation.NROOT) > StepHelper.countNonConstOperation(LHS, Operation.NROOT)) {
			swapSides();
		}

		if (sqrtNum > 3 || sqrtNum == 0) {
			return;
		}

		if (sqrtNum == 1) {
			StepNode nonIrrational = StepHelper.getNonIrrational(LHS);
			addOrSubtract(nonIrrational);
			square();
		}

		if (sqrtNum == 2) {
			StepNode diff = StepNode.subtract(LHS, RHS).regroup();
			if (isZero(StepHelper.getNonIrrational(diff))) {
				StepNode nonIrrational = StepHelper.getNonIrrational(LHS);
				addOrSubtract(nonIrrational);
				if (StepHelper.countNonConstOperation(RHS, Operation.NROOT) == 2) {
					StepNode oneRoot = StepHelper.getOneSquareRoot(LHS);
					addOrSubtract(oneRoot);
				}
				square();
			} else {
				StepNode rootsRHS = StepHelper.getSQRoots(RHS);
				addOrSubtract(rootsRHS);
				StepNode nonIrrational = StepHelper.getNonIrrational(LHS);
				addOrSubtract(nonIrrational);
				square();
			}
		}

		if (sqrtNum == 3) {
			StepNode nonIrrational = StepHelper.getNonIrrational(LHS);
			addOrSubtract(nonIrrational);

			while (StepHelper.countNonConstOperation(RHS, Operation.NROOT) > 1) {
				StepNode oneRoot = StepHelper.getOneSquareRoot(RHS);
				addOrSubtract(oneRoot);
			}

			if (StepHelper.countNonConstOperation(LHS, Operation.NROOT) == 3) {
				StepNode oneRoot = StepHelper.getOneSquareRoot(LHS);
				addOrSubtract(oneRoot);
			}

			square();
		}

		solveIrrational();
	}

	private void solveAbsoluteValue() {
		StepNode bothSides = StepNode.add(LHS, RHS);
		ArrayList<String> absoluteValues = new ArrayList<String>();
		StepHelper.getAbsoluteValues(absoluteValues, bothSides);

		ArrayList<StepNode> roots = new ArrayList<StepNode>();
		for (int i = 0; i < absoluteValues.size(); i++) {
			roots.addAll(Arrays.asList(StepHelper.getCASSolutions(absoluteValues.get(i), "0", variable + "", kernel)));
		}

		Collections.sort(roots, new Comparator<StepNode>() {
			@Override
			public int compare(StepNode s1, StepNode s2) {
				return Double.compare(s1.getValue(), s2.getValue());
			}
		});

		for (int i = 0; i <= roots.size(); i++) {
			solveAbsoulteValueEquation(i == 0 ? new StepConstant(Double.NEGATIVE_INFINITY) : roots.get(i - 1),
					i == roots.size() ? new StepConstant(Double.POSITIVE_INFINITY) : roots.get(i));
		}
	}

	private void solveAbsoulteValueEquation(StepNode a, StepNode b) {
		steps.add(loc.getMenuLaTeX("SolvingBetween", "Solving %0 = %1 between %2 and %3", LaTeX(LHS), LaTeX(RHS),
				LaTeX(a), LaTeX(b)), SolutionStepTypes.INSTRUCTION);

		String LHSevaluated = StepHelper.swapAbsInTree(LHS.deepCopy(), a, b, variable).toString();
		String RHSevaluated = StepHelper.swapAbsInTree(RHS.deepCopy(), a, b, variable).toString();

		EquationSteps sbss = new EquationSteps(kernel, LHSevaluated, RHSevaluated, variable.toString());
		steps.addAll(sbss.getSteps());

		List<StepNode> partialSolutions = sbss.getSolutions();
		for (int i = 0; i < partialSolutions.size(); i++) {
			double xVal = partialSolutions.get(i).getValue();

			if (a.getValue() <= xVal && xVal <= b.getValue()) {
				steps.add(
						loc.getMenuLaTeX("ValidSolutionAbs", "%0 = %1 is between %2 and %3", variable.toString(),
								LaTeX(partialSolutions.get(i)), LaTeX(a), LaTeX(b)),
						SolutionStepTypes.COMMENT);
				solutions.add(partialSolutions.get(i));
			} else {
				steps.add(
						loc.getMenuLaTeX("InvalidSolutionAbs", "%0 = %1 is not between %2 and %3", variable.toString(),
								LaTeX(partialSolutions.get(i)), LaTeX(a), LaTeX(b)),
						SolutionStepTypes.COMMENT);
			}
		}
	}

	private void solveLinear() {
		regroup();

		StepNode diff = StepNode.subtract(LHS, RHS).regroup();
		StepNode constant = StepHelper.findConstant(diff);

		if (isZero(StepNode.subtract(diff, constant).regroup())) {
			addOrSubtract(RHS);

			if (isZero(diff)) {
				solutions.add(new StepConstant(Double.NaN));
			}

			return;
		}

		StepNode RHSlinear = StepHelper.findVariable(RHS, variable);
		StepNode RHSconstant = StepHelper.findConstant(RHS);

		Log.error(StepNode.subtract(RHS, StepNode.add(RHSlinear, RHSconstant)) + "");
		Log.error(StepNode.subtract(RHS, StepNode.add(RHSlinear, RHSconstant)).regroup() + "");

		StepNode nonLinear = StepNode.subtract(RHS, StepNode.add(RHSlinear, RHSconstant)).regroup();
		addOrSubtract(nonLinear);

		addOrSubtract(RHSlinear);

		StepNode LHSconstant = StepHelper.findConstant(LHS);
		addOrSubtract(LHSconstant);

		StepNode toDivide = StepHelper.findCoefficient(LHS, variable);
		divide(toDivide);

		solutions.add(RHS);
	}

	private void solveQuadratic() {
		Log.error("QUAD");

		StepNode RHSconstant = StepHelper.findConstant(RHS);
		StepNode RHSlinear = StepHelper.findVariable(RHS, variable);
		StepNode RHSquadratic = StepHelper.findVariable(RHS, StepNode.power(variable, 2));

		StepNode nonQuadratic = StepNode.subtract(RHS, StepNode.add(RHSconstant, StepNode.add(RHSlinear, RHSquadratic))).regroup();

		addOrSubtract(nonQuadratic);

		StepNode diff = StepNode.subtract(LHS, RHS).regroup();

		if (isZero(StepHelper.findVariable(diff, variable))) {
			StepNode quadratic = StepHelper.findVariable(RHS, StepNode.power(variable, 2));
			addOrSubtract(quadratic);

			StepNode constant = StepHelper.findConstant(LHS);
			addOrSubtract(constant);

			StepNode toDivide = StepHelper.findCoefficient(LHS, StepNode.power(variable, 2));
			divide(toDivide);

			if (RHS.getValue() < 0) {
				return;
			}

			nthroot(2);
			return;
		}

		subtract(RHS);

		StepNode a = StepHelper.findCoefficient(LHS, StepNode.power(variable, 2));
		StepNode b = StepHelper.findCoefficient(LHS, variable);
		StepNode c = StepHelper.findConstant(LHS);

		Log.error("a = " + a);
		Log.error("b = " + b);
		Log.error("c = " + c);

		if (isZero(a)) {
			solveLinear();
			return;
		}

		if (isZero(c)) {
			steps.add(loc.getMenuLaTeX("FactorEquation", "Factor equation"), SolutionStepTypes.COMMENT);
			LHS = StepHelper.factor(LHS, kernel);
			addStep();

			solveProduct((StepOperation) LHS);
			return;
		}

		if (isOne(a) && isEven(b.getValue())) {
			StepNode toComplete = StepNode.subtract(c, StepNode.power(StepNode.divide(b, 2), 2)).regroup();

			Log.error(StepNode.subtract(c, StepNode.power(StepNode.divide(b, 2), 2)) + " ");
			Log.error(toComplete + " ");

			steps.add(loc.getMenuLaTeX("CompleteSquare", "Complete the square"), SolutionStepTypes.COMMENT);

			addOrSubtract(toComplete);
			LHS = StepHelper.factor(LHS, kernel);
			addStep();

			if (RHS.getValue() < 0) {
				return;
			}

			nthroot(2);
			return;
		}
		StepNode discriminant = StepNode.subtract(StepNode.power(b, 2), StepNode.multiply(4, StepNode.multiply(a, c)));
		double discriminantValue = discriminant.getValue();

		if (isSquare(discriminantValue)) {
			steps.add(loc.getMenuLaTeX("FactorEquation", "Factor equation"), SolutionStepTypes.COMMENT);
			LHS = StepHelper.factor(LHS, kernel);
			addStep();

			solveProduct((StepOperation) LHS);
			return;
		}

		// Case: default
		{
			steps.add(loc.getMenuLaTeX("UseQuadraticFormulaWithABC", "Use quadratic formula with a = %0, b = %1, c = %2", LaTeX(a),
					LaTeX(b), LaTeX(c)), SolutionStepTypes.INSTRUCTION);

			steps.levelDown();

			steps.add(variable + " = \\frac{-b \\pm \\sqrt{b^2-4ac}}{2a}", SolutionStepTypes.COMMENT);

			String formula = "\\frac{" + LaTeX("-(" + b + ")") + "\\pm \\sqrt{" + LaTeX(discriminant) + "}}{" + LaTeX("2(" + a + ")") + "}";
			steps.add(variable + "_{1,2} = " + formula, SolutionStepTypes.EQUATION);
			String simplifiedFormula = "\\frac{" + LaTeX(StepNode.minus(b).regroup()) + "\\pm \\sqrt{"
					+ LaTeX(discriminant.regroup()) + "}}{" + LaTeX(StepNode.multiply(2, a).regroup()) + "}";
			steps.add(variable + "_{1,2} = " + simplifiedFormula, SolutionStepTypes.EQUATION);

			steps.levelUp();

			if (discriminantValue == 0) {
				StepNode solution = StepNode.minus(StepNode.divide(b, StepNode.multiply(2, a)));

				solutions.add(solution.simplify());
			} else if (discriminantValue > 0) {
				StepNode solution1 = StepNode
						.minus(StepNode.divide(StepNode.add(b, StepNode.root(discriminant, 2)), StepNode.multiply(2, a)));
				StepNode solution2 = StepNode
						.minus(StepNode.divide(StepNode.subtract(b, StepNode.root(discriminant, 2)), StepNode.multiply(2, a)));

				solutions.add(solution1.simplify());
				solutions.add(solution2.simplify());
			}
		}
	}

	private void takeRoot() {
		StepNode sn = StepNode.subtract(LHS, RHS).regroup();
		StepNode constant = StepHelper.findConstant(sn);
		sn = StepNode.subtract(sn, constant).regroup();

		if (!StepHelper.isPower(LHS) || !StepHelper.isPower(RHS)) {
			if (sn.isOperation()) {
				StepOperation so = (StepOperation) sn;
				if (so.isOperation(Operation.PLUS) || so.isOperation(Operation.MINUS)) {
					addOrSubtract(StepNode.subtract(LHS, so.getSubTree(0).regroup()));
				} else if (so.isOperation(Operation.MULTIPLY) || so.isOperation(Operation.DIVIDE) || so.isOperation(Operation.POWER)) {
					addOrSubtract(StepNode.subtract(RHS, StepHelper.findConstant(RHS)).regroup());
				}
			}

			addOrSubtract(StepHelper.findConstant(LHS));
		}


		if (isNegative(LHS) && isNegative(RHS)) {
			multiply(new StepConstant(-1));
		}

		int root = StepHelper.getPower(LHS);

		StepNode toDivide = StepHelper.findCoefficient(LHS);
		divide(toDivide);

		if (isEven(root) && RHS.isConstant() && RHS.getValue() < 0) {
			return;
		}

		nthroot(root);
	}

	private void reduceToQuadratic() {
		int degree = StepHelper.degree(LHS, kernel);

		StepNode coeffHigh = StepHelper.findCoefficient(LHS, StepNode.power(variable, degree));
		StepNode coeffLow = StepHelper.findCoefficient(LHS, StepNode.power(variable, degree / 2));
		StepNode constant = StepHelper.findConstant(LHS);

		StepNode newVariable = new StepVariable("y");

		steps.add(loc.getMenuLaTeX("ReplaceAWithB", "Replace %0 with %1", variable + "^" + degree / 2, "y"), SolutionStepTypes.INSTRUCTION);

		StepNode newEquation = StepNode.multiply(coeffHigh, StepNode.power(newVariable, 2));
		newEquation = StepNode.add(newEquation, StepNode.multiply(coeffLow, newVariable));
		newEquation = StepNode.add(newEquation, constant);

		EquationSteps ssbs = new EquationSteps(kernel, newEquation.toString(), "0", newVariable.toString());
		steps.addAll(ssbs.getSteps());

		List<StepNode> tempSolutions = ssbs.getSolutions();
		for (int i = 0; i < tempSolutions.size(); i++) {
			EquationSteps tempSsbs = new EquationSteps(kernel, variable + "^" + degree / 2, tempSolutions.get(i).toString(),
					variable.toString());
			steps.addAll(tempSsbs.getSteps());
			solutions.addAll(tempSsbs.getSolutions());
		}
	}

	private void completeCube() {
		StepNode constant = StepHelper.findConstant(LHS);
		StepNode quadratic = StepHelper.findCoefficient(LHS, StepNode.power(variable, 2));

		StepNode toComplete = StepNode.subtract(constant, StepNode.power(StepNode.divide(quadratic, 3), 3)).regroup();

		steps.add(loc.getMenuLaTeX("CompleteCube", "Complete the cube"), SolutionStepTypes.INSTRUCTION);

		addOrSubtract(toComplete);
		LHS = StepHelper.factor(LHS, kernel);
		addStep();

		nthroot(3);
	}

	private void findRationalRoots() {
		int degree = StepHelper.degree(LHS, kernel);

		int highestOrder = Math.abs((int) StepHelper.getCoefficientValue(LHS, StepNode.power(variable, degree)));
		int constant = Math.abs((int) (StepHelper.findConstant(LHS).getValue()));

		StepNode factored = new StepConstant(1);

		for (int i = 1; i <= highestOrder; i++) {
			for (int j = -constant; j <= constant; j++) {
				StepNode solution = StepNode.divide(new StepConstant(j), new StepConstant(i));
				double evaluated = LHS.getValueAt(variable, solution.getValue());

				while (evaluated == 0) {
					factored = StepNode.multiply(factored, StepNode.subtract(variable, solution)).regroup();
					LHS = StepNode.divide(LHS, StepNode.subtract(variable, solution)).simplify();

					evaluated = LHS.getValueAt(variable, solution.getValue());
				}
			}
		}

		if (!isOne(factored)) {
			steps.add(loc.getMenuLaTeX("RationalRootTheorem",
					"A polynomial equation with integer coefficients has all of its rational roots in the form p/q, where p divides the constant term and q divides the coefficient of the highest order term"),
					SolutionStepTypes.COMMENT);

			steps.add(loc.getMenuLaTeX("TrialAndError", "Find the roots by trial and error, and factor them out"), SolutionStepTypes.COMMENT);

			LHS = StepNode.multiply(LHS, factored).regroup();
			addStep();

			solveProduct((StepOperation) LHS);
		}
	}

	private void numericSolutions() {
		StepNode[] CASSolutions = StepHelper.getCASSolutions(LHS.toString(), "0", variable.toString(), kernel);

		steps.add(loc.getMenuLaTeX("SolveNumerically", "Solve numerically: "), SolutionStepTypes.INSTRUCTION);

		solutions.addAll(Arrays.asList(CASSolutions));
	}

	private void regroup() {
		// TODO: proper checking of simplification
		StepNode regroupedLHS = LHS.deepCopy().regroup();
		StepNode regroupedRHS = RHS.deepCopy().regroup();

		if (regroupedLHS.toString().length() < LHS.toString().length() || regroupedRHS.toString().length() < RHS.toString().length()) {
			LHS = regroupedLHS;
			RHS = regroupedRHS;

			steps.add(loc.getMenuLaTeX("SimplifyExpression", "Simplify Expression"), SolutionStepTypes.INSTRUCTION);
			addStep();
		}
	}

	private void expandParantheses() {
		StepNode expandedLHS = LHS.deepCopy().expand();
		StepNode expandedRHS = RHS.deepCopy().expand();

		if (!isZero(StepNode.subtract(expandedLHS, LHS).regroup()) || !isZero(StepNode.subtract(expandedRHS, RHS).regroup())) {
			LHS = expandedLHS;
			RHS = expandedRHS;
			steps.add(loc.getMenuLaTeX("ExpandParentheses", "Expand Parentheses"), SolutionStepTypes.INSTRUCTION);
			steps.levelDown();
			addStep();
			steps.levelUp();
		}
	}

	private void add(StepNode toAdd) {
		if (!isZero(toAdd)) {
			LHS = StepNode.add(LHS, toAdd);
			RHS = StepNode.add(RHS, toAdd);

			steps.add(loc.getMenuLaTeX("AddAToBothSides", "Add %0 to both sides", LaTeX(toAdd)), SolutionStepTypes.INSTRUCTION);
			steps.levelDown();
			addStep();
			steps.levelUp();

			LHS = LHS.regroup();
			RHS = RHS.regroup();

			addStep();
		}
	}

	private void subtract(StepNode toSubtract) {
		if (!isZero(toSubtract)) {
			LHS = StepNode.subtract(LHS, toSubtract);
			RHS = StepNode.subtract(RHS, toSubtract);

			steps.add(loc.getMenuLaTeX("SubtractAFromBothSides", "Subtract %0 from both sides", LaTeX(toSubtract)), SolutionStepTypes.INSTRUCTION);
			steps.levelDown();
			addStep();
			steps.levelUp();

			LHS = LHS.regroup();
			RHS = RHS.regroup();

			addStep();
		}
	}

	private void addOrSubtract(StepNode ev) {
		if (ev == null) {
			return;
		}

		if (isNegative(ev)) {
			add(StepNode.minus(ev).regroup());
		} else {
			subtract(ev);
		}
	}

	private void multiply(StepNode toMultiply) {
		if (!isOne(toMultiply) && !isZero(toMultiply)) {
			LHS = StepNode.multiply(LHS, toMultiply);
			RHS = StepNode.multiply(RHS, toMultiply);

			steps.add(loc.getMenuLaTeX("MultiplyBothSidesByA", "Multiply both sides by %0", LaTeX(toMultiply)), SolutionStepTypes.INSTRUCTION);
			steps.levelDown();
			addStep();
			steps.levelUp();

			LHS = LHS.simplify();
			RHS = RHS.simplify();
			addStep();
		}
	}

	private void divide(StepNode toDivide) {
		if (!isOne(toDivide) && !isZero(toDivide)) {
			LHS = StepNode.divide(LHS, toDivide);
			RHS = StepNode.divide(RHS, toDivide);

			steps.add(loc.getMenuLaTeX("DivideBothSidesByA", "Divide both sides by %0", LaTeX(toDivide)), SolutionStepTypes.INSTRUCTION);
			steps.levelDown();
			addStep();
			steps.levelUp();

			LHS = LHS.regroup();
			RHS = RHS.regroup();
			addStep();
		}
	}

	private void square() {
		LHS = StepNode.power(LHS, 2);
		RHS = StepNode.power(RHS, 2);

		steps.add(loc.getMenuLaTeX("SquareBothSides", "Square both sides"), SolutionStepTypes.INSTRUCTION);
		steps.levelDown();
		addStep();
		steps.levelUp();

		LHS = LHS.simplify();
		RHS = RHS.simplify();
		addStep();
	}

	private void nthroot(int root) {
		if (root == 0 || root == 1) {
			return;
		} else if (root == 2) {
			steps.add(loc.getMenuLaTeX("TakeSquareRoot", "Take square root"), SolutionStepTypes.INSTRUCTION);
		} else if (root == 3) {
			steps.add(loc.getMenuLaTeX("TakeCubeRoot", "Take cube root"), SolutionStepTypes.INSTRUCTION);
		} else {
			steps.add(loc.getMenuLaTeX("TakeNthRoot", "Take %0th root", root + ""), SolutionStepTypes.INSTRUCTION);
		}

		LHS = StepNode.root(LHS, root);
		if (!isZero(RHS)) {
			RHS = StepNode.root(RHS, root);
		}

		steps.levelDown();

		if (isEven(root) && !isZero(RHS)) {
			steps.add(LaTeX(LHS) + " =  " + plusminus(RHS), SolutionStepTypes.EQUATION);
		} else {
			addStep();
		}
		steps.levelUp();

		LHS = LHS.regroup();
		RHS = RHS.regroup();

		if (isEven(root) && LHS.isOperation(Operation.ABS)) {
			LHS = ((StepOperation) LHS).getSubTree(0);
		}
		if (isEven(root) && RHS.isOperation(Operation.ABS)) {
			RHS = ((StepOperation) RHS).getSubTree(0);
		}

		if (isEven(root) && !isZero(RHS)) {
			steps.add(LaTeX(LHS) + " =  " + plusminus(RHS), SolutionStepTypes.EQUATION);
		}

		if (isEven(root) && !isZero(RHS)) {
			if (variable.equals(LHS) && RHS.isConstant()) {
				solutions.add(RHS);
				solutions.add(StepNode.minus(RHS));
			} else {
				EquationSteps positiveBranch = new EquationSteps(kernel, LHS.toString(), RHS.toString(), variable.toString());
				steps.addAll(positiveBranch.getSteps());
				solutions.addAll(positiveBranch.getSolutions());


				EquationSteps negativeBranch = new EquationSteps(kernel, LHS.toString(), StepNode.minus(RHS).toString(),
						variable.toString());
				steps.addAll(negativeBranch.getSteps());
				solutions.addAll(negativeBranch.getSolutions());
			}
		} else {
			EquationSteps reduced = new EquationSteps(kernel, asString(LHS), asString(RHS), variable.toString());
			reduced.setIntermediate();
			steps.addAll(reduced.getSteps());
			solutions.addAll(reduced.getSolutions());
		}
	}

	private static String asString(StepNode toString) {
		if (toString == null || isZero(toString)) {
			return "0";
		}

		return toString.toString();
	}

	private String LaTeX(String toLaTeX) {
		if (toLaTeX.isEmpty()) {
			return "";
		}

		StepNode ev = StepNode.getStepTree(toLaTeX, kernel.getParser());
		return ev.toLaTeXString();
	}

	private static String LaTeX(StepNode toLaTeX) {
		if (toLaTeX == null) {
			return "";
		}

		return toLaTeX.toLaTeXString();
	}

	private static String plusminus(StepNode ev) {
		if (ev != null && ev.getPriority() == 1) {
			return "\\pm (" + LaTeX(ev) + ")";
		} else if (ev != null) {
			return "\\pm " + LaTeX(ev);
		}
		return "";
	}

	private void swapSides() {
		StepNode temp = LHS;
		LHS = RHS;
		RHS = temp;

		inverted = !inverted;
	}

	private void addStep() {
		if (inverted) {
			steps.add(LaTeX(RHS) + " = " + LaTeX(LHS), SolutionStepTypes.EQUATION);
		} else {
			steps.add(LaTeX(LHS) + " = " + LaTeX(RHS), SolutionStepTypes.EQUATION);
		}
	}

	private static boolean isNegative(StepNode ev) {
		return ev.getValue() < 0 || ev.isOperation(Operation.MINUS);
	}

	private static boolean isZero(StepNode ev) {
		return ev == null || isEqual(ev.getValue(), 0);
	}

	private static boolean isOne(StepNode ev) {
		return ev == null || isEqual(ev.getValue(), 1);
	}

	private static boolean isEven(double d) {
		return isEqual(Math.floor(d / 2) * 2, d);
	}

	private static boolean isSquare(double d) {
		return isEqual(Math.floor(Math.sqrt(d)) * Math.floor(Math.sqrt(d)), d);
	}

	private static boolean isEqual(double a, double b) {
		return Math.abs(a - b) < 0.0000001;
	}
}
