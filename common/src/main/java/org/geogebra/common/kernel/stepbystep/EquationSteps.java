package org.geogebra.common.kernel.stepbystep;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.stepbystep.steptree.StepArbitraryConstant;
import org.geogebra.common.kernel.stepbystep.steptree.StepConstant;
import org.geogebra.common.kernel.stepbystep.steptree.StepInterval;
import org.geogebra.common.kernel.stepbystep.steptree.StepNode;
import org.geogebra.common.kernel.stepbystep.steptree.StepOperation;
import org.geogebra.common.kernel.stepbystep.steptree.StepVariable;
import org.geogebra.common.main.Localization;
import org.geogebra.common.plugin.Operation;

public class EquationSteps {
	private Kernel kernel;
	private Localization loc;

	// needed for checking the validity of the solutions.
	private StepNode origLHS;
	private StepNode origRHS;

	private StepNode LHS;
	private StepNode RHS;

	private SolutionBuilder steps;
	private List<StepNode> solutions;

	private boolean inverted;
	private boolean intermediate;

	private StepNode variable;

	private StepInterval interval;

	private String solutionCase;

	public EquationSteps(Kernel kernel, String LHS, String RHS, String variable) {
		this.kernel = kernel;
		this.loc = kernel.getLocalization();

		this.LHS = StepNode.getStepTree(LHS, kernel.getParser());
		this.RHS = StepNode.getStepTree(RHS, kernel.getParser());

		origLHS = this.LHS.deepCopy();
		origRHS = this.RHS.deepCopy();

		this.variable = new StepVariable(variable);
	}

	public EquationSteps(Kernel kernel, StepNode LHS, StepNode RHS, StepNode variable) {
		this.kernel = kernel;
		this.loc = kernel.getLocalization();

		this.LHS = LHS;
		this.RHS = RHS;

		this.origLHS = this.LHS.deepCopy();
		this.origRHS = this.RHS.deepCopy();

		this.variable = variable;
	}

	public void setCase(String s) {
		solutionCase = s;
	}

	public void setIntermediate() {
		intermediate = true;
	}

	public void setInterval(StepInterval interval) {
		this.interval = interval;
	}

	public SolutionStep getSteps() {
		if (steps != null) {
			return steps.getSteps();
		}

		steps = new SolutionBuilder();
		solutions = new ArrayList<StepNode>();

		// I. step: regrouping
		if (!intermediate) {
			if (solutionCase != null && interval != null) {
				steps.add(loc.getMenuLaTeX("SolvingInInterval", "Case %0: %1 when %2", solutionCase, LaTeX(LHS) + " = " + LaTeX(RHS),
						LaTeX(variable) + "\\in" + LaTeX(interval)), SolutionStepTypes.INSTRUCTION);
				steps.levelDown();
			} else if (solutionCase != null) {
				steps.add(loc.getMenuLaTeX("CaseA", "Case %0: %1", solutionCase, LaTeX(LHS) + " = " + LaTeX(RHS)),
						SolutionStepTypes.EQUATION);
				steps.levelDown();
			} else {
				steps.add(loc.getMenuLaTeX("Solve", "Solve: %0", LaTeX(LHS) + " = " + LaTeX(RHS)), SolutionStepTypes.EQUATION);
				steps.levelDown();
				regroup();
			}
		}

		addOrSubtract(StepHelper.getCommon(LHS, RHS));
		

		StepNode bothSides = StepNode.add(LHS, RHS);

		// II. step: making denominators disappear
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

		// IV. step: getting rid of square roots
		bothSides = StepNode.add(LHS, RHS);
		if (StepHelper.countNonConstOperation(bothSides, Operation.NROOT) > 0) {
			solveIrrational();
		}

		int degreeDiff = StepHelper.degree(StepNode.subtract(LHS, RHS).regroup());
		int degreeLHS = StepHelper.degree(LHS);
		int degreeRHS = StepHelper.degree(RHS);

		// Swapping sides, if necessary
		if (degreeRHS == -1 || degreeLHS == -1) {
			if (degreeRHS == -1 && degreeLHS != -1) {
				swapSides();
			}
		} else if (degreeRHS > degreeLHS) {
			swapSides();
		} else if (degreeRHS == degreeLHS) {
			double coeffLHS = StepHelper.getCoefficientValue(LHS.deepCopy().simplify(),
					degreeLHS == 1 ? variable : StepNode.power(variable, degreeLHS));
			double coeffRHS = StepHelper.getCoefficientValue(RHS.deepCopy().simplify(),
					degreeRHS == 1 ? variable : StepNode.power(variable, degreeRHS));

			if (coeffRHS > coeffLHS) {
				swapSides();
			}
		}

		// V. step: taking roots, when necessary (ax^n = constant or ay^n = bz^n, where y and z are expressions in x)
		if (StepHelper.shouldTakeRoot(RHS, LHS)) {
			takeRoot();
			return checkSolutions();
		}

		// VI. step: expanding parentheses
		expandParentheses();

		// VII. Step: equations containing absolute values
		bothSides = StepNode.add(LHS, RHS);
		if (StepHelper.countOperation(bothSides, Operation.ABS) > 0) {
			if (solveAbsoluteValue()) {
				return checkSolutions();
			}
		}

		// II. step: checking if it's a trigonometric equation
		if (StepHelper.containsTrigonometric(bothSides)) {
			solveTrigonometric();
			return checkSolutions();
		}

		degreeDiff = StepHelper.degree(StepNode.subtract(LHS, RHS).regroup());

		if (degreeDiff == -1) {
			steps.add(loc.getMenuLaTeX("CantSolve", "Cannot Solve"), SolutionStepTypes.COMMENT);
			return steps.getSteps();
		}

		// VIII. step: solving linear equations
		if (degreeDiff <= 1) {
			solveLinear();
			return checkSolutions();
		}

		// IX. step: solving quadratic equations
		if (degreeDiff == 2) {
			solveQuadratic();
			return checkSolutions();
		}

		subtract(RHS);

		// X. step: solving equations that can be reduced to a quadratic (ax^(2n) + bx^n + c = 0)
		if (StepHelper.canBeReducedToQuadratic(LHS, variable)) {
			reduceToQuadratic();
			return checkSolutions();
		}

		// XI. step: completing the cube
		if (StepHelper.canCompleteCube(LHS, variable)) {
			completeCube();
			return checkSolutions();
		}

		// XII. step: finding and factoring rational roots
		if (StepHelper.integerCoefficients(LHS, variable)) {
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
		if (intermediate) {
			return steps.getSteps();
		}

		steps.levelUp();

		if (isZero(LHS) && isZero(RHS)) {
			if (interval == null) {
				interval = new StepInterval(new StepConstant(Double.NEGATIVE_INFINITY), new StepConstant(Double.POSITIVE_INFINITY), false, false);
			}
			steps.add(
					loc.getMenuLaTeX("TrueForAllAInB", "The equation is true for all %0", variable.toString() + " \\in " + LaTeX(interval)),
					SolutionStepTypes.SOLUTION);
			solutions.add(interval);
			return steps.getSteps();
		}

		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < solutions.size(); i++) {
			sb.append(variable);

			if (solutions.get(i) instanceof StepInterval) {
				sb.append(" \\in ");
			} else {
				sb.append(" = ");
			}

			sb.append(LaTeX(solutions.get(i)));
			if (solutions.get(i).canBeEvaluated() && !(solutions.get(i) instanceof StepConstant)) {
				if (isSimpleFraction(solutions.get(i))) {
					sb.append(" = ");
				} else {
					sb.append(" \\approx ");
				}
				sb.append(new DecimalFormat("#0.00").format(solutions.get(i).getValue()));
			}
			if (i < solutions.size() - 1) {
				sb.append(", ");
			}
		}

		if (solutions.size() == 0) {
			steps.add(loc.getMenuLaTeX("NoRealSolutions", "No Real Solutions"), SolutionStepTypes.SOLUTION);
		} else if (solutions.size() == 1) {
			steps.add(loc.getMenuLaTeX("SolutionA", "Solution: %0", sb.toString()), SolutionStepTypes.SOLUTION);
		} else if (solutions.size() > 1) {
			steps.add(loc.getMenuLaTeX("SolutionsA", "Solutions: %0", sb.toString()), SolutionStepTypes.SOLUTION);
		}

		StepNode bothSides = StepNode.add(origLHS, origRHS);

		StepNode denominators = StepHelper.getDenominator(bothSides, kernel);
		StepNode roots = StepHelper.getAll(bothSides, Operation.NROOT);

		if (interval == null && (denominators == null || denominators.isConstant()) && (roots == null || roots.isConstant())
				|| solutions.size() == 0) {
			return steps.getSteps();
		}

		steps.add(loc.getMenuLaTeX("CheckingValidityOfSolutions", "Checking validity of solutions"), SolutionStepTypes.COMMENT);

		steps.levelDown();

		for (int i = 0; i < solutions.size(); i++) {
			if (interval != null) {
				if (interval.contains(solutions.get(i))) {
					steps.add(loc.getMenuLaTeX("ValidSolutionAbs", "%0 = %1", variable.toString(),
							LaTeX(solutions.get(i)) + " \\in " + LaTeX(interval)), SolutionStepTypes.COMMENT);
				} else {
					steps.add(loc.getMenuLaTeX("InvalidSolutionAbs", "%0 = %1", variable.toString(),
							LaTeX(solutions.get(i)) +  " \\notin " + LaTeX(interval)), SolutionStepTypes.COMMENT);
					solutions.remove(solutions.get(i));
					i--;
				}
			} else {
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
		}

		return steps.getSteps();
	}

	private void solveProduct(StepOperation product) {
		steps.add(loc.getMenuLaTeX("ProductIsZero", "Product is zero"), SolutionStepTypes.COMMENT);

		for (int i = 0; i < product.noOfOperands(); i++) {
			EquationSteps es = new EquationSteps(kernel, product.getSubTree(i), new StepConstant(0), variable);
			es.setCase((solutionCase == null ? "" : solutionCase) + (i + 1) + ".");
			steps.addAll(es.getSteps());
			solutions.addAll(es.getSolutions());
		}
	}

	private void solveTrigonometric() {
		StepNode bothSides = StepNode.subtract(LHS, RHS).regroup();

		StepOperation trigoVar = StepHelper.linearInTrigonometric(bothSides);
		
		if (trigoVar != null) {
			StepNode RHSlinear = StepHelper.findVariable(RHS, trigoVar);
			addOrSubtract(RHSlinear);

			StepNode LHSconstant = StepHelper.findConstant(LHS);
			addOrSubtract(LHSconstant);

			StepNode linearCoefficient = StepHelper.findCoefficient(LHS, trigoVar);
			divide(linearCoefficient);

			solveSimpleTrigonometric(trigoVar, RHS);
		}
		
		trigoVar = StepHelper.quadraticInTrigonometric(bothSides);
		if (trigoVar != null) {
			StepVariable newVar = new StepVariable("t");
			EquationSteps trigonometricReplaced = new EquationSteps(kernel, LHS.replace(trigoVar, newVar), RHS.replace(trigoVar, newVar),
					newVar);

			steps.addAll(trigonometricReplaced.getSteps());

			List<StepNode> tempSolutions = trigonometricReplaced.getSolutions();
			for (int i = 0; i < tempSolutions.size(); i++) {
				EquationSteps newCase = new EquationSteps(kernel, trigoVar, tempSolutions.get(i), variable);
				if (tempSolutions.size() != 1) {
					newCase.setCase(caseNumber(i + 1));
				}

				steps.addAll(newCase.getSteps());
				solutions.addAll(newCase.getSolutions());
			}
		}

	}

	private void solveSimpleTrigonometric(StepOperation trigoVar, StepNode constant) {
		if (trigoVar.isOperation(Operation.SIN) && (constant.getValue() < -1 || constant.getValue() > 1)) {
			steps.add(loc.getMenuLaTeX("NoSolutionTrigonometricSin", "sin(x) \\in [-1, 1] for all x \\in \\mathbb{R}"),
					SolutionStepTypes.COMMENT);
			return;
		} else if (trigoVar.isOperation(Operation.COS) && (constant.getValue() < -1 || constant.getValue() > 1)) {
			steps.add(loc.getMenuLaTeX("NoSolutionTrigonometricCos", "cos(x) \\in [-1, 1] for all x \\in \\mathbb{R}"),
					SolutionStepTypes.COMMENT);
			return;
		}

		Operation op = StepOperation.getInverse(trigoVar.getOperation());
		StepNode newLHS = trigoVar.getSubTree(0);
		
		if (trigoVar.getOperation() == Operation.TAN) {
			StepNode newRHS = StepNode.add(StepNode.apply(constant, op),
					StepNode.multiply(new StepArbitraryConstant("k", 0, StepArbitraryConstant.ConstantType.INTEGER),
							new StepConstant(Math.PI)));

			EquationSteps tangent = new EquationSteps(kernel, newLHS, newRHS, variable);
			tangent.setIntermediate();

			steps.addAll(tangent.getSteps());
			solutions.addAll(tangent.getSolutions());

			return;
		}
		
		StepNode newRHS = StepNode.add(StepNode.apply(constant, op),
				StepNode.multiply(StepNode.multiply(2, new StepArbitraryConstant("k", 0, StepArbitraryConstant.ConstantType.INTEGER)),
						new StepConstant(Math.PI)));
		
		EquationSteps firstBranch = new EquationSteps(kernel, newLHS, newRHS, variable);
		if (isEqual(Math.abs(constant.getValue()), 1)) {
			firstBranch.setIntermediate();
		} else {
			firstBranch.setCase(caseNumber(1));
		}

		steps.addAll(firstBranch.getSteps());
		solutions.addAll(firstBranch.getSolutions());

		if (!isEqual(Math.abs(constant.getValue()), 1)) {
			EquationSteps secondBranch;
			if (trigoVar.getOperation() == Operation.SIN) {
				secondBranch = new EquationSteps(kernel, StepNode.subtract(new StepConstant(Math.PI), newLHS), newRHS, variable);
			} else {
				secondBranch = new EquationSteps(kernel, StepNode.subtract(StepNode.multiply(2, new StepConstant(Math.PI)), newLHS), newRHS,
						variable);
			}

			secondBranch.setCase(caseNumber(2));

			steps.addAll(secondBranch.getSteps());
			solutions.addAll(secondBranch.getSolutions());
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
			StepNode nonIrrational = StepHelper.getNon(LHS, Operation.NROOT);
			addOrSubtract(nonIrrational);
			square();
		}

		if (sqrtNum == 2) {
			StepNode diff = StepNode.subtract(LHS, RHS).regroup();
			if (isZero(StepHelper.getNon(diff, Operation.NROOT))) {
				StepNode nonIrrational = StepHelper.getNon(LHS, Operation.NROOT);
				addOrSubtract(nonIrrational);
				if (StepHelper.countNonConstOperation(RHS, Operation.NROOT) == 2) {
					StepNode oneRoot = StepHelper.getOne(LHS, Operation.NROOT);
					addOrSubtract(oneRoot);
				}
				square();
			} else {
				StepNode rootsRHS = StepHelper.getAll(RHS, Operation.NROOT);
				addOrSubtract(rootsRHS);
				StepNode nonIrrational = StepHelper.getNon(LHS, Operation.NROOT);
				addOrSubtract(nonIrrational);
				square();
			}
		}

		if (sqrtNum == 3) {
			StepNode nonIrrational = StepHelper.getNon(LHS, Operation.NROOT);
			addOrSubtract(nonIrrational);

			while (StepHelper.countNonConstOperation(RHS, Operation.NROOT) > 1) {
				StepNode oneRoot = StepHelper.getOne(RHS, Operation.NROOT);
				addOrSubtract(oneRoot);
			}

			if (StepHelper.countNonConstOperation(LHS, Operation.NROOT) == 3) {
				StepNode oneRoot = StepHelper.getOne(LHS, Operation.NROOT);
				addOrSubtract(oneRoot);
			}

			square();
		}

		solveIrrational();
	}

	private boolean solveAbsoluteValue() {
		if (interval != null) {
			steps.add(loc.getMenuLaTeX("EvaluateAbsoluteValues", "Evaluate absolute values"), SolutionStepTypes.INSTRUCTION);

			LHS = StepHelper.swapAbsInTree(LHS.deepCopy(), interval, variable);
			RHS = StepHelper.swapAbsInTree(RHS.deepCopy(), interval, variable);

			addStep();
			expandParentheses();
			return false;
		}

		int absNum = StepHelper.countNonConstOperation(LHS, Operation.ABS) + StepHelper.countNonConstOperation(RHS, Operation.ABS);

		if (StepHelper.countNonConstOperation(RHS, Operation.ABS) > StepHelper.countNonConstOperation(LHS, Operation.ABS)) {
			swapSides();
		}

		StepNode nonAbsDiff = StepHelper.getNon(StepNode.subtract(LHS, RHS).regroup(), Operation.ABS);
		if (absNum == 1 && (nonAbsDiff == null || nonAbsDiff.isConstant())) {
			StepNode nonAbsolute = StepHelper.getNon(LHS, Operation.ABS);
			addOrSubtract(nonAbsolute);
			return plusminus();
		} else if (absNum == 2 && (isZero(nonAbsDiff))) {
			StepNode nonAbsolute = StepHelper.getNon(LHS, Operation.ABS);
			addOrSubtract(nonAbsolute);
			if (StepHelper.countNonConstOperation(LHS, Operation.ABS) == 2) {
				StepNode oneAbs = StepHelper.getOne(LHS, Operation.ABS);
				addOrSubtract(oneAbs);
			}
			return plusminus();
		}

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

		roots.add(0, new StepConstant(Double.NEGATIVE_INFINITY));
		roots.add(new StepConstant(Double.POSITIVE_INFINITY));

		for (int i = 1; i < roots.size(); i++) {
			EquationSteps es = new EquationSteps(kernel, LHS, RHS, variable);
			es.setCase((solutionCase == null ? "" : solutionCase) + i + ".");
			es.setInterval(new StepInterval(roots.get(i - 1), roots.get(i), false, i != roots.size() - 1));

			steps.addAll(es.getSteps());
			solutions.addAll(es.getSolutions());
		}
		
		return true;
	}

	private void solveLinear() {
		regroup();

		StepNode diff = StepNode.subtract(LHS, RHS).regroup();
		StepNode constant = StepHelper.findConstant(diff);

		if (isZero(StepNode.subtract(diff, constant).regroup())) {
			subtract(RHS);
			return;
		}

		StepNode RHSlinear = StepHelper.findVariable(RHS, variable);
		addOrSubtract(RHSlinear);

		StepNode LHSconstant = StepHelper.findConstant(LHS);
		addOrSubtract(LHSconstant);

		StepNode linearCoefficient = StepHelper.findCoefficient(LHS, variable);
		divide(linearCoefficient);

		solutions.add(RHS);
	}

	private void solveQuadratic() {
		StepNode difference = StepNode.subtract(LHS, RHS).regroup();

		StepNode a = StepHelper.findCoefficient(difference, StepNode.power(variable, 2));
		StepNode b = StepHelper.findCoefficient(difference, variable);
		StepNode c = StepHelper.findConstant(difference);

		if (isOne(a) && isEven(b.getValue()) && !isZero(c)) {
			StepNode RHSConstant = StepHelper.findConstant(RHS);
			addOrSubtract(StepNode.subtract(RHS, RHSConstant).regroup());

			StepNode LHSConstant = StepHelper.findConstant(LHS);
			StepNode toComplete = StepNode.subtract(LHSConstant, StepNode.power(StepNode.divide(b, 2), 2)).regroup();

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

		subtract(RHS);

		StepNode discriminant = StepNode.subtract(StepNode.power(b, 2), StepNode.multiply(4, StepNode.multiply(a, c)));

		if (isZero(c) || isSquare(discriminant.getValue())) {
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

			String formula = "\\frac{" + LaTeX(StepNode.minus(b)) + "\\pm \\sqrt{" + LaTeX(discriminant) + "}}{"
					+ LaTeX(StepNode.multiply(2, a))
					+ "}";
			steps.add(variable + "_{1,2} = " + formula, SolutionStepTypes.EQUATION);
			String simplifiedFormula = "\\frac{" + LaTeX(StepNode.minus(b).regroup()) + "\\pm \\sqrt{"
					+ LaTeX(discriminant.deepCopy().regroup()) + "}}{" + LaTeX(StepNode.multiply(2, a).regroup()) + "}";
			steps.add(variable + "_{1,2} = " + simplifiedFormula, SolutionStepTypes.EQUATION);

			steps.levelUp();

			if (discriminant.getValue() > 0) {
				StepNode solution1 = StepNode.divide(StepNode.add(StepNode.minus(b), StepNode.root(discriminant, 2)),
						StepNode.multiply(2, a));
				StepNode solution2 = StepNode.divide(StepNode.subtract(StepNode.minus(b), StepNode.root(discriminant, 2)),
						StepNode.multiply(2, a));

				solutions.add(solution1.regroup());
				solutions.add(solution2.regroup());
			}
		}
	}

	private void takeRoot() {
		StepNode sn = StepNode.subtract(LHS, RHS).regroup();
		StepNode constant = StepHelper.findConstant(sn);
		sn = StepNode.subtract(sn, constant).regroup();

		if (!StepHelper.isPower(LHS) || !StepHelper.isPower(RHS)) {
			if (sn.isOperation(Operation.MULTIPLY) || sn.isOperation(Operation.DIVIDE) || sn.isOperation(Operation.POWER)) {
				addOrSubtract(StepNode.subtract(RHS, StepHelper.findConstant(RHS)).regroup());
			}

			addOrSubtract(StepHelper.findConstant(LHS));
		}

		if (isNegative(LHS) && isNegative(RHS)) {
			multiply(new StepConstant(-1));
		}

		int root = StepHelper.getPower(LHS);

		StepNode toDivide = LHS.getCoefficient();
		divide(toDivide);

		if (isEven(root) && RHS.isConstant() && RHS.getValue() < 0) {
			return;
		}

		nthroot(root);
	}

	private void reduceToQuadratic() {
		int degree = StepHelper.degree(LHS);

		StepNode coeffHigh = StepHelper.findCoefficient(LHS, StepNode.power(variable, degree));
		StepNode coeffLow = StepHelper.findCoefficient(LHS, StepNode.power(variable, degree / 2));
		StepNode constant = StepHelper.findConstant(LHS);

		StepVariable newVariable = new StepVariable("y");

		steps.add(loc.getMenuLaTeX("ReplaceAWithB", "Replace %0 with %1", variable + "^" + degree / 2, "y"), SolutionStepTypes.INSTRUCTION);

		StepNode newEquation = StepNode.multiply(coeffHigh, StepNode.power(newVariable, 2));
		newEquation = StepNode.add(newEquation, StepNode.multiply(coeffLow, newVariable));
		newEquation = StepNode.add(newEquation, constant);

		EquationSteps ssbs = new EquationSteps(kernel, newEquation.regroup(), new StepConstant(0), newVariable);
		steps.addAll(ssbs.getSteps());

		List<StepNode> tempSolutions = ssbs.getSolutions();
		for (int i = 0; i < tempSolutions.size(); i++) {
			EquationSteps tempSsbs = new EquationSteps(kernel, StepNode.power(variable, degree / 2), tempSolutions.get(i), variable);
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
		int degree = StepHelper.degree(LHS);

		int highestOrder = Math.abs((int) StepHelper.getCoefficientValue(LHS, StepNode.power(variable, degree)));
		int constant = Math.abs((int) (StepHelper.findConstant(LHS).getValue()));

		StepNode factored = new StepConstant(1);

		for (int i = 1; i <= highestOrder; i++) {
			for (int j = -constant; j <= constant; j++) {
				StepNode solution = StepNode.divide(new StepConstant(j), new StepConstant(i)).regroup();
				double evaluated = LHS.getValueAt(variable, solution.getValue());

				while (evaluated == 0) {
					factored = StepNode.multiply(factored, StepNode.subtract(variable, solution)).regroup();
					LHS = StepNode.polynomialDivision(LHS, StepNode.subtract(variable, solution), variable);

					evaluated = LHS.getValueAt(variable, solution.getValue());
				}
			}
		}

		if (!isOne(factored)) {
			steps.add(loc.getMenuLaTeX("RationalRootTheorem",
					"A polynomial equation with integer coefficients has all of its rational roots in the form p/q, where p divides the constant term and q divides the coefficient of the highest order term"),
					SolutionStepTypes.COMMENT);

			steps.add(loc.getMenuLaTeX("TrialAndError", "Find the roots by trial and error, and factor them out"),
					SolutionStepTypes.COMMENT);

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

	private void expandParentheses() {
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

			steps.add(loc.getMenuLaTeX("SubtractAFromBothSides", "Subtract %0 from both sides", LaTeX(toSubtract)),
					SolutionStepTypes.INSTRUCTION);
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

			steps.add(loc.getMenuLaTeX("MultiplyBothSidesByA", "Multiply both sides by %0", LaTeX(toMultiply)),
					SolutionStepTypes.INSTRUCTION);
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
			steps.add(loc.getMenuLaTeX("TakeSquareRoot", "Take square root of both sides"), SolutionStepTypes.INSTRUCTION);
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
		addStep();
		steps.levelUp();

		LHS = LHS.regroup();
		RHS = RHS.regroup();

		addStep();

		EquationSteps es = new EquationSteps(kernel, LHS, RHS, variable);
		es.setIntermediate();

		steps.addAll(es.getSteps());
		solutions.addAll(es.getSolutions());
	}

	private boolean plusminus() {
		if (LHS.isOperation(Operation.ABS)) {
			LHS = ((StepOperation) LHS).getSubTree(0);
		}
		if (RHS.isOperation(Operation.ABS)) {
			RHS = ((StepOperation) RHS).getSubTree(0);
		}

		steps.add(loc.getMenuLaTeX("ResolveAbsoluteValues", "Resolve Absolute Values"), SolutionStepTypes.INSTRUCTION);

		if (!isZero(RHS)) {
			steps.add(LaTeX(LHS) + " = " + plusminus(RHS), SolutionStepTypes.EQUATION);

			if (LHS.equals(variable) && RHS.isConstant()) {
				solutions.add(RHS);
				solutions.add(StepNode.minus(RHS));
			} else {
				EquationSteps positiveBranch = new EquationSteps(kernel, LHS, RHS, variable);
				positiveBranch.setCase(solutionCase == null ? "1." : solutionCase + "1.");
				EquationSteps negativeBranch = new EquationSteps(kernel, LHS, StepNode.minus(RHS), variable);
				negativeBranch.setCase(solutionCase == null ? "2." : solutionCase + "2.");

				steps.addAll(positiveBranch.getSteps());
				solutions.addAll(positiveBranch.getSolutions());
				steps.addAll(negativeBranch.getSteps());
				solutions.addAll(negativeBranch.getSolutions());
			}

			return true;
		}

		addStep();
		return false;
	}

	private static String plusminus(StepNode ev) {
		if (ev != null && ev.getPriority() == 1) {
			return "\\pm (" + LaTeX(ev) + ")";
		} else if (ev != null) {
			return "\\pm " + LaTeX(ev);
		}
		return "";
	}

	private static String LaTeX(StepNode toLaTeX) {
		if (toLaTeX == null) {
			return "";
		}

		return toLaTeX.toLaTeXString();
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

	private static boolean isSimpleFraction(StepNode sn) {
		if(sn.isOperation(Operation.MINUS)) {
			return isSimpleFraction(((StepOperation) sn).getSubTree(0));
		} else if(sn.isOperation(Operation.DIVIDE)) {
			if(((StepOperation) sn).getSubTree(1) instanceof StepConstant) {
				double val = ((StepOperation) sn).getSubTree(1).getValue();
				return 100 % val == 0;
			}
		}
		return false;
	}

	private String caseNumber(int i) {
		return (solutionCase == null ? "" : solutionCase) + i + ".";
	}

	private static boolean isNegative(StepNode ev) {
		return ev.getValue() < 0 || ev.isOperation(Operation.MINUS);
	}

	private static boolean isZero(StepNode ev) {
		return ev == null || ev.isConstant() && isEqual(ev.getValue(), 0);
	}

	private static boolean isOne(StepNode ev) {
		return ev == null || ev.isConstant() && isEqual(ev.getValue(), 1);
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
