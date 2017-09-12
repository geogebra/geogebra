package org.geogebra.common.kernel.stepbystep;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.stepbystep.solution.SolutionBuilder;
import org.geogebra.common.kernel.stepbystep.solution.SolutionStep;
import org.geogebra.common.kernel.stepbystep.solution.SolutionStepType;
import org.geogebra.common.kernel.stepbystep.steptree.ArbitraryConstantFactory;
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
	private boolean shouldCheckSolutions;

	private StepVariable variable;

	private StepInterval interval;

	private String solutionCase;

	private ArbitraryConstantFactory constantFactory;

	public EquationSteps(Kernel kernel, String LHS, String RHS, String variable) {
		this.kernel = kernel;
		this.loc = kernel.getLocalization();

		this.LHS = StepNode.getStepTree(LHS, kernel.getParser());
		this.RHS = StepNode.getStepTree(RHS, kernel.getParser());

		origLHS = this.LHS.deepCopy();
		origRHS = this.RHS.deepCopy();

		this.variable = new StepVariable(variable);

		this.constantFactory = new ArbitraryConstantFactory("k", StepArbitraryConstant.ConstantType.INTEGER);
	}

	public EquationSteps(Kernel kernel, StepNode LHS, StepNode RHS, StepVariable variable, ArbitraryConstantFactory constantFactory) {
		this.kernel = kernel;
		this.loc = kernel.getLocalization();

		this.LHS = LHS;
		this.RHS = RHS;

		this.origLHS = this.LHS.deepCopy();
		this.origRHS = this.RHS.deepCopy();

		this.variable = variable;

		this.constantFactory = constantFactory;
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

		steps = new SolutionBuilder(loc);
		solutions = new ArrayList<StepNode>();

		// I. step: regrouping
		if (!intermediate) {
			if (solutionCase != null && interval != null) {
				steps.add(SolutionStepType.SOLVING_IN_INTERVAL, StepNode.equal(LHS, RHS), StepNode.in(variable, interval));
				steps.levelDown();
			} else if (solutionCase != null) {
				steps.add(SolutionStepType.NEW_CASE, StepNode.equal(LHS, RHS));
				steps.levelDown();
			} else {
				steps.add(SolutionStepType.SOLVE, StepNode.equal(LHS, RHS));
				steps.levelDown();
			}
		}

		regroup();

		StepNode common = StepHelper.getCommon(LHS, RHS);
		if (!LHS.equals(common) || !RHS.equals(common)) {
			addOrSubtract(common);
		}

		StepNode bothSides = StepNode.add(LHS, RHS);

		// II. step: making denominators disappear
		if (StepHelper.shouldMultiply(bothSides)) {
			commonDenominator();
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
			double coeffLHS = StepHelper.getCoefficientValue(LHS.deepCopy().regroup(),
					degreeLHS == 1 ? variable : StepNode.power(variable, degreeLHS));
			double coeffRHS = StepHelper.getCoefficientValue(RHS.deepCopy().regroup(),
					degreeRHS == 1 ? variable : StepNode.power(variable, degreeRHS));

			if (coeffRHS > coeffLHS) {
				swapSides();
			}
		}

		// V. step: taking roots, when necessary (ax^n = constant or ay^n = bz^n, where y and z are expressions in x)
		if (StepHelper.shouldTakeRoot(RHS, LHS)) {
			if (takeRoot()) {
				return checkSolutions();
			}
		}

		// VI. step: expanding parentheses
		simplify();

		// VII. Step: equations containing absolute values
		bothSides = StepNode.add(LHS, RHS);
		if (StepHelper.countOperation(bothSides, Operation.ABS) > 0) {
			if (solveAbsoluteValue()) {
				return checkSolutions();
			}
		}

		if (StepHelper.linearInInverse(bothSides) != null) {
			solveLinearInInverse();
			return checkSolutions();
		}

		// II. step: checking if it's a trigonometric equation
		if (StepHelper.containsTrigonometric(bothSides)) {
			if (solveTrigonometric()) {
				return checkSolutions();
			}
		}

		degreeDiff = StepHelper.degree(StepNode.subtract(LHS, RHS).regroup());

		if (degreeDiff == -1) {
			steps.add(SolutionStepType.CANT_SOLVE);
			return steps.getSteps();
		}


		// IX. step: solving quadratic equations
		if (degreeDiff == 2) {
			if (solveQuadratic()) {
				return checkSolutions();
			}
		}
		
		degreeDiff = StepHelper.degree(StepNode.subtract(LHS, RHS).regroup());

		// VIII. step: solving linear equations
		if (degreeDiff <= 1) {
			solveLinear();
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

	private SolutionStep checkSolutions() {
		if (intermediate) {
			return steps.getSteps();
		}

		steps.levelUp();

		if (LHS.equals(RHS)) {
			if (interval == null) {
				interval = new StepInterval(new StepConstant(Double.NEGATIVE_INFINITY), new StepConstant(Double.POSITIVE_INFINITY), false,
						false);
			}
			steps.add(SolutionStepType.TRUE_FOR_ALL, StepNode.in(variable, interval));
			solutions.add(interval);
			return steps.getSteps();
		}

		StepNode[] variableWithSolution = new StepNode[solutions.size()];
		for (int i = 0; i < solutions.size(); i++) {
			if (solutions.get(i) instanceof StepInterval) {
				variableWithSolution[i] = StepNode.in(variable, solutions.get(i));
			} else {
				variableWithSolution[i] = StepNode.equal(variable, solutions.get(i));
			}
		}

		if (solutions.size() == 0) {
			steps.add(SolutionStepType.NO_REAL_SOLUTION);
		} else if (solutions.size() == 1) {
			steps.add(SolutionStepType.SOLUTION, variableWithSolution);
		} else if (solutions.size() > 1) {
			steps.add(SolutionStepType.SOLUTIONS, variableWithSolution);
		}

		if (solutions.size() == 0 || interval == null && !shouldCheckSolutions) {
			return steps.getSteps();
		}

		steps.add(SolutionStepType.CHECK_VALIDITY);

		steps.levelDown();

		for (int i = 0; i < solutions.size(); i++) {
			if (interval != null) {
				if (interval.contains(solutions.get(i))) {
					steps.add(SolutionStepType.VALID_SOLUTION_ABS, StepNode.equal(variable, solutions.get(i)), interval);
				} else {
					steps.add(SolutionStepType.INVALID_SOLUTION_ABS, StepNode.equal(variable, solutions.get(i)), interval);
					solutions.remove(i);
					i--;
				}
			} else {
				if (StepHelper.isValidSolution(origLHS, origRHS, solutions.get(i), variable, kernel)) {
					steps.add(SolutionStepType.VALID_SOLUTION, StepNode.equal(variable, solutions.get(i)));
				} else {
					steps.add(SolutionStepType.INVALID_SOLUTION, StepNode.equal(variable, solutions.get(i)));
					solutions.remove(i);
					i--;
				}
			}
		}

		return steps.getSteps();
	}

	private void solveProduct(StepOperation product) {
		steps.add(SolutionStepType.PRODUCT_IS_ZERO);

		int caseNumber = 1;
		for (int i = 0; i < product.noOfOperands(); i++) {
			if (!product.getSubTree(i).isConstant()) {
				EquationSteps es = new EquationSteps(kernel, product.getSubTree(i), new StepConstant(0), variable, constantFactory);
				es.setCase(subcase(caseNumber++));
				steps.addAll(es.getSteps());
				solutions.addAll(es.getSolutions());
			}
		}
	}

	private void commonDenominator() {
		StepNode bothSides = StepNode.add(LHS, RHS);
		StepNode commonDenominator = StepHelper.getCommonDenominator(bothSides, kernel);

		if (commonDenominator != null && commonDenominator.isOperation(Operation.MULTIPLY)) {
			Boolean[] changed = new Boolean[] { false };

			LHS = StepHelper.factorDenominators(LHS, kernel, changed);
			RHS = StepHelper.factorDenominators(RHS, kernel, changed);

			if (changed[0]) {
				steps.add(SolutionStepType.FACTOR_DENOMINATORS);
				addStep();
				changed[0] = false;
			}

			LHS = StepHelper.expandFractions(LHS, commonDenominator, changed);
			RHS = StepHelper.expandFractions(RHS, commonDenominator, changed);

			if (changed[0]) {
				steps.add(SolutionStepType.EXPAND_FRACTIONS, commonDenominator);
				addStep();
				changed[0] = false;
			}

			LHS = StepHelper.addFractions(LHS, commonDenominator, changed);
			RHS = StepHelper.addFractions(RHS, commonDenominator, changed);

			if (changed[0]) {
				steps.add(SolutionStepType.ADD_FRACTIONS);
				addStep();
				changed[0] = false;
			}
		}
		
		if (commonDenominator != null && !commonDenominator.isConstant()) {
			shouldCheckSolutions = true;
		}

		multiply(commonDenominator);
	}

	private boolean solveTrigonometric() {
		StepNode bothSides = StepNode.subtract(LHS, RHS).regroup();

		StepNode argument = StepHelper.findTrigonometricVariable(bothSides).getSubTree(0);
		StepNode sineSquared = StepNode.power(StepNode.apply(argument, Operation.SIN), 2);
		StepNode cosineSquared = StepNode.power(StepNode.apply(argument, Operation.COS), 2);

		StepNode coeffSineSquared = StepHelper.findCoefficient(bothSides, sineSquared);
		StepNode coeffCosineSquared = StepHelper.findCoefficient(bothSides, cosineSquared);

		if (coeffSineSquared != null && coeffSineSquared.equals(coeffCosineSquared)) {
			addOrSubtract(StepNode.add(StepHelper.findVariable(RHS, sineSquared), StepHelper.findVariable(RHS, sineSquared)));

			LHS = StepNode
					.subtract(LHS, StepNode.add(StepHelper.findVariable(LHS, sineSquared), StepHelper.findVariable(LHS, cosineSquared)))
					.regroup();
			LHS = StepNode.add(LHS, StepNode.multiply(coeffSineSquared, StepNode.add(sineSquared, cosineSquared)));

			if (!isOne(coeffSineSquared)) {
				LHS = LHS.regroup();
				RHS = RHS.regroup();
				addStep();
			}

			replace(StepNode.add(sineSquared, cosineSquared), new StepConstant(1));
			regroup();

			bothSides = StepNode.subtract(LHS, RHS).regroup();
		}

		bothSides = StepNode.subtract(LHS, RHS).regroup();

		StepNode sine = StepNode.apply(argument, Operation.SIN);
		StepNode cosine = StepNode.apply(argument, Operation.COS);

		StepNode coeffSine = StepHelper.findCoefficient(bothSides, sine);
		StepNode coeffCosine = StepHelper.findCoefficient(bothSides, cosine);
		coeffSineSquared = StepHelper.findCoefficient(bothSides, sineSquared);
		coeffCosineSquared = StepHelper.findCoefficient(bothSides, cosineSquared);

		if (coeffSine != null && coeffCosine != null && isZero(coeffSineSquared) && isZero(coeffCosineSquared)) {
			if (!isZero(StepHelper.findVariable(LHS, sine))) {
				addOrSubtract(StepHelper.findVariable(LHS, cosine));
				addOrSubtract(StepHelper.findVariable(RHS, sine));
			} else {
				addOrSubtract(StepHelper.findVariable(RHS, cosine));
			}

			square();
			bothSides = StepNode.subtract(LHS, RHS).regroup();
		}

		if (LHS.isConstant() && RHS.isConstant()) {
			return true;
		}

		StepOperation trigoVar = StepHelper.linearInTrigonometric(bothSides);

		if (trigoVar != null) {
			StepNode RHSlinear = StepHelper.findVariable(RHS, trigoVar);
			addOrSubtract(RHSlinear);

			StepNode LHSconstant = StepHelper.findConstant(LHS);
			addOrSubtract(LHSconstant);

			StepNode linearCoefficient = StepHelper.findCoefficient(LHS, trigoVar);
			multiplyOrDivide(linearCoefficient);

			return solveSimpleTrigonometric(trigoVar, RHS);
		}

		trigoVar = StepHelper.quadraticInTrigonometric(bothSides);

		if (trigoVar == null) {
			trigoVar = StepHelper.quadraticInTrigonometric(bothSides.deepCopy().replace(sineSquared, StepNode.subtract(1, cosineSquared)));
			if (trigoVar != null) {
				replace(sineSquared, StepNode.subtract(1, cosineSquared));
				regroup();
			}
		}

		if (trigoVar == null) {
			trigoVar = StepHelper.quadraticInTrigonometric(bothSides.deepCopy().replace(cosineSquared, StepNode.subtract(1, sineSquared)));
			if (trigoVar != null) {
				replace(cosineSquared, StepNode.subtract(1, sineSquared));
				regroup();
			}
		}

		if (trigoVar != null) {
			StepVariable newVar = new StepVariable("t");
			EquationSteps trigonometricReplaced = new EquationSteps(kernel, LHS.replace(trigoVar, newVar), RHS.replace(trigoVar, newVar),
					newVar, constantFactory);

			steps.addAll(trigonometricReplaced.getSteps());

			List<StepNode> tempSolutions = trigonometricReplaced.getSolutions();
			for (int i = 0; i < tempSolutions.size(); i++) {
				EquationSteps newCase = new EquationSteps(kernel, trigoVar, tempSolutions.get(i), variable, constantFactory);
				if (tempSolutions.size() > 1) {
					newCase.setCase(subcase(i + 1));
				} else {
					newCase.setIntermediate();
				}

				steps.addAll(newCase.getSteps());
				solutions.addAll(newCase.getSolutions());
			}

			return true;
		}

		return false;
	}

	private void replace(StepNode from, StepNode to) {
		steps.add(SolutionStepType.REPLACE_WITH, from, to);
		LHS = LHS.replace(from, to);
		RHS = RHS.replace(from, to);
		addStep();
	}

	private boolean solveSimpleTrigonometric(StepOperation trigoVar, StepNode constant) {
		if (!constant.canBeEvaluated() && trigoVar.getOperation() != Operation.TAN) {
			return false;
		}

		if ((trigoVar.isOperation(Operation.SIN) || trigoVar.isOperation(Operation.COS))
				&& (constant.getValue() < -1 || constant.getValue() > 1)) {
			steps.add(SolutionStepType.NO_SOLUTION_TRIGONOMETRIC, trigoVar, variable);
			return true;
		}

		Operation op = StepNode.getInverse(trigoVar.getOperation());
		StepNode newLHS = trigoVar.getSubTree(0);

		if (trigoVar.getOperation() == Operation.TAN) {
			StepNode newRHS = StepNode.add(StepNode.apply(constant, op), StepNode
					.multiply(constantFactory.getNext(), new StepConstant(Math.PI)));

			steps.add(SolutionStepType.EQUATION, StepNode.equal(newLHS, newRHS));
			EquationSteps tangent = new EquationSteps(kernel, newLHS, newRHS, variable, constantFactory);
			tangent.setIntermediate();

			steps.addAll(tangent.getSteps());
			solutions.addAll(tangent.getSolutions());

			return true;
		}

		StepNode firstRHS = StepNode.add(StepNode.apply(constant, op),
				StepNode.multiply(StepNode.multiply(2, constantFactory.getNext()),
						new StepConstant(Math.PI)));

		EquationSteps firstBranch = new EquationSteps(kernel, newLHS, firstRHS, variable, constantFactory);
		if (isEqual(Math.abs(constant.getValue()), 1)) {
			steps.add(SolutionStepType.EQUATION, StepNode.equal(newLHS, firstRHS));
			firstBranch.setIntermediate();
		} else {
			firstBranch.setCase(subcase(1));
		}

		steps.addAll(firstBranch.getSteps());
		solutions.addAll(firstBranch.getSolutions());

		if (!isEqual(Math.abs(constant.getValue()), 1)) {
			StepNode secondRHS = StepNode.add(StepNode.apply(constant, op),
					StepNode.multiply(StepNode.multiply(2, constantFactory.getNext()), new StepConstant(Math.PI)));
			EquationSteps secondBranch;
			if (trigoVar.getOperation() == Operation.SIN) {
				secondBranch = new EquationSteps(kernel, StepNode.subtract(new StepConstant(Math.PI), newLHS), secondRHS, variable,
						constantFactory);
			} else {
				secondBranch = new EquationSteps(kernel, StepNode.subtract(StepNode.multiply(2, new StepConstant(Math.PI)), newLHS),
						secondRHS,
						variable, constantFactory);
			}

			secondBranch.setCase(subcase(2));

			steps.addAll(secondBranch.getSteps());
			solutions.addAll(secondBranch.getSolutions());
		}

		return true;
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
			steps.add(SolutionStepType.RESOLVE_ABSOLUTE_VALUES);

			LHS = StepHelper.swapAbsInTree(LHS.deepCopy(), interval, variable);
			RHS = StepHelper.swapAbsInTree(RHS.deepCopy(), interval, variable);

			addStep();
			simplify();
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
			EquationSteps es = new EquationSteps(kernel, LHS, RHS, variable, constantFactory);
			es.setCase((solutionCase == null ? "" : solutionCase) + i + ".");
			es.setInterval(new StepInterval(roots.get(i - 1), roots.get(i), false, i != roots.size() - 1));

			steps.addAll(es.getSteps());
			solutions.addAll(es.getSolutions());
		}

		return true;
	}

	private void solveLinear() {
		StepNode diff = StepNode.subtract(LHS, RHS).regroup();
		StepNode constant = StepHelper.findConstant(diff);

		if (isZero(StepNode.subtract(diff, constant).regroup())) {
			return;
		}

		StepNode RHSlinear = StepHelper.findVariable(RHS, variable);
		addOrSubtract(RHSlinear);

		StepNode LHSconstant = StepHelper.findConstant(LHS);
		addOrSubtract(LHSconstant);

		StepNode linearCoefficient = StepHelper.findCoefficient(LHS, variable);
		multiplyOrDivide(linearCoefficient);

		solutions.add(RHS);
	}

	private void solveLinearInInverse() {
		StepNode inverseVar = StepHelper.linearInInverse(StepNode.add(LHS, RHS));
		
		StepNode diff = StepNode.subtract(LHS, RHS).regroup();
		StepNode constant = StepHelper.findConstant(diff);

		if (isZero(StepNode.subtract(diff, constant).regroup())) {
			return;
		}

		StepNode RHSlinear = StepHelper.findVariable(RHS, inverseVar);
		addOrSubtract(RHSlinear);

		StepNode LHSconstant = StepHelper.findConstant(LHS);
		addOrSubtract(LHSconstant);

		steps.add(SolutionStepType.INVERT_BOTH_SIDES);
		LHS = StepNode.invert(LHS);
		RHS = StepNode.invert(RHS);
		addStep();

		StepNode linearCoefficient = StepHelper.findCoefficient(LHS, inverseVar);
		multiplyOrDivide(linearCoefficient);

		solutions.add(RHS);
	}

	private boolean solveQuadratic() {
		StepNode difference = StepNode.subtract(LHS, RHS).regroup();

		StepNode a = StepHelper.findCoefficient(difference, StepNode.power(variable, 2));
		StepNode b = StepHelper.findCoefficient(difference, variable);
		StepNode c = StepHelper.findConstant(difference);

		if (isOne(a) && isEven(b.getValue()) && !isZero(c)) {
			StepNode RHSConstant = StepHelper.findConstant(RHS);
			addOrSubtract(StepNode.subtract(RHS, RHSConstant).regroup());

			StepNode LHSConstant = StepHelper.findConstant(LHS);
			StepNode toComplete = StepNode.subtract(LHSConstant, StepNode.power(StepNode.divide(b, 2), 2)).regroup();

			steps.add(SolutionStepType.COMPLETE_THE_SQUARE);

			addOrSubtract(toComplete);
			LHS = StepHelper.factor(LHS, kernel);
			addStep();

			if (RHS.getValue() < 0) {
				return true;
			}

			return takeRoot();
		}

		subtract(RHS);

		StepNode discriminant = StepNode.subtract(StepNode.power(b, 2), StepNode.multiply(4, StepNode.multiply(a, c)));

		if (isZero(c) || isSquare(discriminant.getValue())) {
			LHS = LHS.regroup();
			steps.add(SolutionStepType.FACTOR_EQUATION);
			LHS = StepHelper.factor(LHS, kernel);

			addStep();

			solveProduct((StepOperation) LHS);
			return true;
		}

		a.setColor(1);
		b.setColor(2);
		c.setColor(3);
		discriminant = StepNode.subtract(StepNode.power(b, 2), StepNode.multiply(4, StepNode.multiply(a, c)));

		// Case: default
		{
			steps.add(SolutionStepType.USE_QUADRATIC_FORMULA, a, b, c);
			steps.levelDown();

			steps.add(SolutionStepType.QUADRATIC_FORMULA, variable);
			LHS = variable;
			RHS = StepNode.divide(
					StepNode.add(StepNode.minus(b), StepNode.apply(StepNode.root(discriminant, 2), Operation.PLUSMINUS)),
					StepNode.multiply(2, a));

			regroup();

			if (discriminant.getValue() > 0) {
				StepNode solution1 = StepNode.divide(StepNode.add(StepNode.minus(b), StepNode.root(discriminant, 2)),
						StepNode.multiply(2, a));
				StepNode solution2 = StepNode.divide(StepNode.subtract(StepNode.minus(b), StepNode.root(discriminant, 2)),
						StepNode.multiply(2, a));

				solutions.add(solution1.regroup());
				solutions.add(solution2.regroup());
			}

			return true;
		}
	}

	private boolean takeRoot() {
		StepNode sn = StepNode.subtract(LHS, RHS).regroup();
		StepNode constant = StepHelper.findConstant(sn);
		sn = StepNode.subtract(sn, constant).regroup();

		if (!StepHelper.isPower(LHS) || !StepHelper.isPower(RHS)) {
			if (sn.isOperation(Operation.MULTIPLY) || sn.isOperation(Operation.DIVIDE) || sn.isOperation(Operation.POWER)) {
				addOrSubtract(StepNode.subtract(RHS, StepHelper.findConstant(RHS)).regroup());
			}

			addOrSubtract(StepHelper.findConstant(LHS));
		}

		int root = StepHelper.getPower(LHS);

		StepNode toDivide = LHS.getCoefficient();
		multiplyOrDivide(toDivide);

		if (isEven(root) && RHS.isConstant() && RHS.getValue() < 0) {
			return true;
		}

		if(root == 2 && RHS.isConstant()) {
			steps.add(SolutionStepType.SQUARE_ROOT);

			LHS = ((StepOperation) LHS).getSubTree(0);
			if (!StepNode.isEqual(RHS, 0)) {
				RHS = StepNode.root(RHS, 2);
			}

			return plusminus();
		}

		nthroot(root);
		return true;
	}

	private void reduceToQuadratic() {
		int degree = StepHelper.degree(LHS);

		StepNode coeffHigh = StepHelper.findCoefficient(LHS, StepNode.power(variable, degree));
		StepNode coeffLow = StepHelper.findCoefficient(LHS, StepNode.power(variable, ((double) degree) / 2));
		StepNode constant = StepHelper.findConstant(LHS);

		StepVariable newVariable = new StepVariable("y");

		steps.add(SolutionStepType.REPLACE_WITH, StepNode.power(variable, ((double) degree) / 2), newVariable);

		StepNode newEquation = StepNode.multiply(coeffHigh, StepNode.power(newVariable, 2));
		newEquation = StepNode.add(newEquation, StepNode.multiply(coeffLow, newVariable));
		newEquation = StepNode.add(newEquation, constant);

		EquationSteps ssbs = new EquationSteps(kernel, newEquation.regroup(), new StepConstant(0), newVariable, constantFactory);
		steps.addAll(ssbs.getSteps());

		List<StepNode> tempSolutions = ssbs.getSolutions();
		for (int i = 0; i < tempSolutions.size(); i++) {
			EquationSteps tempSsbs = new EquationSteps(kernel, StepNode.power(variable, ((double) degree) / 2), tempSolutions.get(i),
					variable, constantFactory);
			steps.addAll(tempSsbs.getSteps());
			solutions.addAll(tempSsbs.getSolutions());
		}
	}

	private void completeCube() {
		StepNode constant = StepHelper.findConstant(LHS);
		StepNode quadratic = StepHelper.findCoefficient(LHS, StepNode.power(variable, 2));

		StepNode toComplete = StepNode.subtract(constant, StepNode.power(StepNode.divide(quadratic, 3), 3)).regroup();

		steps.add(SolutionStepType.COMPLETE_THE_CUBE);

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
			steps.add(SolutionStepType.RATIONAL_ROOT_THEOREM);
			steps.add(SolutionStepType.TRIAL_AND_ERROR);

			LHS = StepNode.multiply(LHS, factored).regroup();
			addStep();

			solveProduct((StepOperation) LHS);
		}
	}

	private void numericSolutions() {
		StepNode[] CASSolutions = StepHelper.getCASSolutions(LHS.toString(), "0", variable.toString(), kernel);

		steps.add(SolutionStepType.SOLVE_NUMERICALLY);

		solutions.addAll(Arrays.asList(CASSolutions));
	}

	private void regroup() {
		if (inverted) {
			StepOperation regrouped = (StepOperation) StepNode.equal(RHS, LHS).regroup(steps);
			RHS = regrouped.getSubTree(0);
			LHS = regrouped.getSubTree(1);
		} else {
			StepOperation regrouped = (StepOperation) StepNode.equal(LHS, RHS).regroup(steps);
			LHS = regrouped.getSubTree(0);
			RHS = regrouped.getSubTree(1);
		}
	}

	private void simplify() {
		if (inverted) {
			StepOperation simplified = (StepOperation) StepNode.equal(RHS, LHS).expand(steps);
			RHS = simplified.getSubTree(0);
			LHS = simplified.getSubTree(1);
		} else {
			StepOperation simplified = (StepOperation) StepNode.equal(LHS, RHS).expand(steps);
			LHS = simplified.getSubTree(0);
			RHS = simplified.getSubTree(1);
		}
	}

	private void add(StepNode toAdd) {
		if (!isZero(toAdd)) {
			toAdd.setColor(1);

			LHS = StepNode.add(LHS, toAdd);
			RHS = StepNode.add(RHS, toAdd);

			steps.add(SolutionStepType.ADD_TO_BOTH_SIDES, toAdd);
			steps.levelDown();
			addStep();
			regroup();
			steps.levelUp();
			addStep();
		}
	}

	private void subtract(StepNode toSubtract) {
		if (!isZero(toSubtract)) {
			toSubtract.setColor(1);

			LHS = StepNode.subtract(LHS, toSubtract);
			RHS = StepNode.subtract(RHS, toSubtract);

			steps.add(SolutionStepType.SUBTRACT_FROM_BOTH_SIDES, toSubtract);
			steps.levelDown();
			addStep();
			regroup();
			steps.levelUp();
			addStep();
		}
	}

	private void addOrSubtract(StepNode ev) {
		if (ev == null) {
			return;
		}

		if (StepNode.isNegative(ev)) {
			add(StepNode.negate(ev.deepCopy()));
		} else {
			subtract(ev.deepCopy());
		}
	}

	private void multiply(StepNode toMultiply) {
		if (!isOne(toMultiply) && !isZero(toMultiply)) {
			toMultiply.setColor(1);

			if (toMultiply.isConstant()) {
				if (LHS.isOperation(Operation.PLUS)) {
					StepOperation newLHS = new StepOperation(Operation.PLUS);
					for (int i = 0; i < ((StepOperation) LHS).noOfOperands(); i++) {
						newLHS.addSubTree(StepNode.multiply(toMultiply, ((StepOperation) LHS).getSubTree(i)));
					}
					LHS = newLHS;
				} else {
					LHS = StepNode.multiply(toMultiply, LHS);
				}

				if (RHS.isOperation(Operation.PLUS)) {
					StepOperation newRHS = new StepOperation(Operation.PLUS);
					for (int i = 0; i < ((StepOperation) RHS).noOfOperands(); i++) {
						newRHS.addSubTree(StepNode.multiply(toMultiply, ((StepOperation) RHS).getSubTree(i)));
					}
					RHS = newRHS;
				} else {
					RHS = StepNode.multiply(toMultiply, RHS);
				}
			} else {
				LHS = StepNode.multiply(LHS, toMultiply);
				RHS = StepNode.multiply(RHS, toMultiply);
			}

			steps.add(SolutionStepType.MULTIPLY_BOTH_SIDES, toMultiply);
			steps.levelDown();
			addStep();
			simplify();
			steps.levelUp();
			addStep();
		}
	}

	private void divide(StepNode toDivide) {
		if (!isOne(toDivide) && !isZero(toDivide)) {
			toDivide.setColor(1);

			LHS = StepNode.divide(LHS, toDivide);
			RHS = StepNode.divide(RHS, toDivide);

			steps.add(SolutionStepType.DIVIDE_BOTH_SIDES, toDivide);
			steps.levelDown();
			addStep();
			simplify();
			steps.levelUp();
			addStep();
		}
	}

	private void multiplyOrDivide(StepNode sn) {
		if (sn == null) {
			return;
		}

		if (sn.canBeEvaluated() && isEqual(sn.getValue(), -1)) {
			multiply(sn);
		} else if (sn.isOperation(Operation.DIVIDE)) {
			StepOperation so = (StepOperation) sn;
			multiply(StepNode.divide(so.getSubTree(1), so.getSubTree(0)));
		} else {
			divide(sn);
		}
	}

	private void square() {
		LHS = StepNode.power(LHS, 2);
		RHS = StepNode.power(RHS, 2);

		steps.add(SolutionStepType.SQUARE_BOTH_SIDES);
		steps.levelDown();
		simplify();
		steps.levelUp();
		addStep();

		shouldCheckSolutions = true;
	}

	private void nthroot(int root) {
		if (root == 0 || root == 1) {
			return;
		} else if (root == 2) {
			steps.add(SolutionStepType.SQUARE_ROOT);
		} else if (root == 3) {
			steps.add(SolutionStepType.CUBE_ROOT);
		} else {
			steps.add(SolutionStepType.NTH_ROOT, new StepConstant(root));
		}

		LHS = StepNode.root(LHS, root);
		if (!isZero(RHS)) {
			RHS = StepNode.root(RHS, root);
		}

		steps.levelDown();
		regroup();
		steps.levelUp();
		addStep();

		EquationSteps es = new EquationSteps(kernel, LHS, RHS, variable, constantFactory);
		es.setIntermediate();

		steps.addAll(es.getSteps());
		solutions.addAll(es.getSolutions());
	}

	private boolean plusminus() {
		if (LHS.isOperation(Operation.ABS) || RHS.isOperation(Operation.ABS)) {
			steps.add(SolutionStepType.RESOLVE_ABSOLUTE_VALUES);
		}

		if (LHS.isOperation(Operation.ABS)) {
			LHS = ((StepOperation) LHS).getSubTree(0);
		}
		if (RHS.isOperation(Operation.ABS)) {
			RHS = ((StepOperation) RHS).getSubTree(0);
		}

		if (!isZero(RHS)) {
			steps.add(SolutionStepType.EQUATION, StepNode.equal(LHS, StepNode.apply(RHS, Operation.PLUSMINUS)));

			if (LHS.equals(variable) && RHS.isConstant()) {
				RHS = StepNode.apply(RHS, Operation.PLUSMINUS);
				regroup();

				solutions.add(((StepOperation) RHS).getSubTree(0));
				solutions.add(StepNode.minus(((StepOperation) RHS).getSubTree(0)));
			} else {
				EquationSteps positiveBranch = new EquationSteps(kernel, LHS, RHS, variable, constantFactory);
				positiveBranch.setCase(solutionCase == null ? "1." : solutionCase + "1.");
				EquationSteps negativeBranch = new EquationSteps(kernel, LHS, StepNode.minus(RHS), variable, constantFactory);
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

	private void swapSides() {
		StepNode temp = LHS;
		LHS = RHS;
		RHS = temp;

		inverted = !inverted;
	}

	private void addStep() {
		if (inverted) {
			steps.add(SolutionStepType.EQUATION, StepNode.equal(RHS, LHS));
		} else {
			steps.add(SolutionStepType.EQUATION, StepNode.equal(LHS, RHS));
		}
	}

	private String subcase(int i) {
		return (solutionCase == null ? i + "" : solutionCase) + "." + i;
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
