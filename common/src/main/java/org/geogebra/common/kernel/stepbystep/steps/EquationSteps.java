package org.geogebra.common.kernel.stepbystep.steps;

import static org.geogebra.common.kernel.stepbystep.steptree.StepNode.add;
import static org.geogebra.common.kernel.stepbystep.steptree.StepNode.divide;
import static org.geogebra.common.kernel.stepbystep.steptree.StepNode.isEqual;
import static org.geogebra.common.kernel.stepbystep.steptree.StepNode.isEven;
import static org.geogebra.common.kernel.stepbystep.steptree.StepNode.isOne;
import static org.geogebra.common.kernel.stepbystep.steptree.StepNode.isZero;
import static org.geogebra.common.kernel.stepbystep.steptree.StepNode.minus;
import static org.geogebra.common.kernel.stepbystep.steptree.StepNode.multiply;
import static org.geogebra.common.kernel.stepbystep.steptree.StepNode.power;
import static org.geogebra.common.kernel.stepbystep.steptree.StepNode.root;
import static org.geogebra.common.kernel.stepbystep.steptree.StepNode.subtract;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.geogebra.common.kernel.stepbystep.StepHelper;
import org.geogebra.common.kernel.stepbystep.solution.SolutionBuilder;
import org.geogebra.common.kernel.stepbystep.solution.SolutionStepType;
import org.geogebra.common.kernel.stepbystep.steptree.StepConstant;
import org.geogebra.common.kernel.stepbystep.steptree.StepEquation;
import org.geogebra.common.kernel.stepbystep.steptree.StepExpression;
import org.geogebra.common.kernel.stepbystep.steptree.StepInterval;
import org.geogebra.common.kernel.stepbystep.steptree.StepNode;
import org.geogebra.common.kernel.stepbystep.steptree.StepOperation;
import org.geogebra.common.kernel.stepbystep.steptree.StepSet;
import org.geogebra.common.kernel.stepbystep.steptree.StepVariable;
import org.geogebra.common.plugin.Operation;

public enum EquationSteps implements SolveStepGenerator {

	REGROUP {
		@Override
		public StepNode apply(StepEquation se, StepVariable variable, SolutionBuilder steps) {
			return se.regroup(steps);
		}
	},

	EXPAND {
		@Override
		public StepNode apply(StepEquation se, StepVariable variable, SolutionBuilder steps) {
			return se.expand(steps);
		}
	},

	SUBTRACT_COMMON {
		@Override
		public StepNode apply(StepEquation se, StepVariable variable, SolutionBuilder steps) {
			StepExpression common = StepHelper.getCommon(se.getLHS(), se.getRHS());
			if (!se.getLHS().equals(common) || !se.getRHS().equals(common)) {
				se.addOrSubtract(common, steps);
			}

			return se;
		}
	},

	SOLVE_LINEAR {
		@Override
		public StepNode apply(StepEquation se, StepVariable variable, SolutionBuilder steps) {
			StepExpression diff = subtract(se.getLHS(), se.getRHS()).regroup();

			if (diff.isConstant()) {
				if (isZero(diff)) {
					if (se.getRestriction() == null) {
						return new StepSet(StepInterval.R);
					}
					return new StepSet(se.getRestriction());
				}

				return new StepSet();
			}

			if (!(StepHelper.degree(se) == 0 || StepHelper.degree(se) == 1)) {
				return se;
			}

			if (StepHelper.getCoefficientValue(se.getLHS(), variable) < StepHelper.getCoefficientValue(se.getRHS(),
					variable)) {
				se.swapSides();
			}

			StepExpression RHSlinear = StepHelper.findVariable(se.getRHS(), variable);
			se.addOrSubtract(RHSlinear, steps);

			StepExpression LHSconstant = StepHelper.findConstant(se.getLHS());
			se.addOrSubtract(LHSconstant, steps);

			StepExpression linearCoefficient = StepHelper.findCoefficient(se.getLHS(), variable);
			se.multiplyOrDivide(linearCoefficient, steps);

			return new StepSet(se.getRHS());
		}
	},

	SOLVE_QUADRATIC {
		@Override
		public StepNode apply(StepEquation se, StepVariable variable, SolutionBuilder steps) {
			if (StepHelper.degree(se) != 2) {
				return se;
			}

			if (StepHelper.getCoefficientValue(se.getRHS(), power(variable, 2)) > 
					StepHelper.getCoefficientValue(se.getLHS(), power(variable, 2))) {
				se.swapSides();
			}

			StepExpression difference = subtract(se.getLHS(), se.getRHS()).regroup();

			StepExpression a = StepHelper.findCoefficient(difference, power(variable, 2));
			StepExpression b = StepHelper.findCoefficient(difference, variable);
			StepExpression c = StepHelper.findConstant(difference);

			if (isOne(a) && isEven(b) && !isZero(c)) {
				StepExpression RHSConstant = StepHelper.findConstant(se.getRHS());
				se.addOrSubtract(subtract(se.getRHS(), RHSConstant).regroup(), steps);

				StepExpression LHSConstant = StepHelper.findConstant(se.getLHS());
				StepExpression toComplete = subtract(LHSConstant, power(divide(b, 2), 2)).regroup();

				steps.add(SolutionStepType.COMPLETE_THE_SQUARE);

				se.addOrSubtract(toComplete, steps);
				se.factor(steps);

				if (se.getRHS().getValue() < 0) {
					return new StepSet();
				}

				return se;
			}

			se.subtract(se.getRHS(), steps);

			a.setColor(1);
			b.setColor(2);
			c.setColor(3);

			StepExpression discriminant = subtract(power(b, 2), multiply(4, multiply(a, c)));

			steps.add(SolutionStepType.USE_QUADRATIC_FORMULA, a, b, c);
			steps.levelDown();

			steps.add(SolutionStepType.QUADRATIC_FORMULA, variable);
			se.modify(variable,
					divide(add(minus(b), StepNode.apply(root(discriminant, 2), Operation.PLUSMINUS)), multiply(2, a)));

			se.regroup(steps);

			if (discriminant.getValue() > 0) {
				StepExpression solution1 = divide(add(minus(b), root(discriminant, 2)), multiply(2, a));
				StepExpression solution2 = divide(subtract(minus(b), root(discriminant, 2)), multiply(2, a));

				return new StepSet(solution1.regroup(), solution2.regroup());
			}

			return new StepSet();
		}
	},

	SOLVE_PRODUCT {
		@Override
		public StepNode apply(StepEquation se, StepVariable variable, SolutionBuilder steps) {
			StepOperation product = null;

			if (isZero(se.getLHS())) {
				se.factor(steps);
				if (StepHelper.isProduct(se.getRHS())) {
					product = (StepOperation) se.getRHS();
				}
			} else if (isZero(se.getRHS())) {
				se.factor(steps);
				if (StepHelper.isProduct(se.getLHS())) {
					product = (StepOperation) se.getLHS();
				}
			} else {
				if (StepHelper.isProduct(subtract(se.getLHS(), se.getRHS()).factor())) {
					se.subtract(se.getRHS(), steps);
					se.factor(steps);

					product = (StepOperation) se.getLHS();
				} else if (StepHelper.isProduct(subtract(se.getLHS(), se.getRHS()).expand().factor())) {
					se.expand(steps);
					se.subtract(se.getRHS(), steps);
					se.factor(steps);

					product = (StepOperation) se.getLHS();
				}
			}

			if (product != null) {
				steps.add(SolutionStepType.PRODUCT_IS_ZERO);
				StepSet solutions = new StepSet();

				for (int i = 0; i < product.noOfOperands(); i++) {
					if (!product.getSubTree(i).isConstant()) {
						StepEquation newEq = new StepEquation(product.getSubTree(i), new StepConstant(0));
						solutions.addAll(newEq.solve(variable, steps));
					}
				}

				return solutions;
			}

			return se;
		}
	},

	COMMON_DENOMINATOR {
		@Override
		public StepNode apply(StepEquation se, StepVariable variable, SolutionBuilder steps) {
			if (!StepHelper.shouldMultiply(se)) {
				return se;
			}

			StepExpression bothSides = add(se.getLHS(), se.getRHS());
			StepExpression commonDenominator = StepHelper.getCommonDenominator(bothSides);

			if (commonDenominator != null && commonDenominator.isOperation(Operation.MULTIPLY)) {
				Boolean[] changed = new Boolean[] { false };

				StepExpression newLHS = StepHelper.factorDenominators(se.getLHS(), changed);
				StepExpression newRHS = StepHelper.factorDenominators(se.getRHS(), changed);

				if (changed[0]) {
					se.modify(newLHS, newRHS);
					steps.add(SolutionStepType.FACTOR_DENOMINATORS);
					se.addStep(steps);
					changed[0] = false;
				}

				newLHS = StepHelper.expandFractions(se.getLHS(), commonDenominator, changed);
				newRHS = StepHelper.expandFractions(se.getRHS(), commonDenominator, changed);

				if (changed[0]) {
					se.modify(newLHS, newRHS);
					steps.add(SolutionStepType.EXPAND_FRACTIONS, commonDenominator);
					se.addStep(steps);
					changed[0] = false;
				}

				newLHS = StepHelper.addFractions(se.getLHS(), commonDenominator, changed);
				newRHS = StepHelper.addFractions(se.getRHS(), commonDenominator, changed);

				if (changed[0]) {
					se.modify(newLHS, newRHS);
					steps.add(SolutionStepType.ADD_FRACTIONS);
					se.addStep(steps);
					changed[0] = false;
				}
			}

			// if (commonDenominator != null && !commonDenominator.isConstant()) {
			// shouldCheckSolutions = true;
			// }

			se.multiply(commonDenominator, steps);

			return se;
		}
	},

	RECIPROCATE_EQUATION {
		@Override
		public StepNode apply(StepEquation se, StepVariable variable, SolutionBuilder steps) {
			if (StepHelper.shouldReciprocate(se.getLHS()) && StepHelper.shouldReciprocate(se.getRHS())
					&& !(se.getLHS().isConstant() && se.getRHS().isConstant())) {
				se.reciprocate(steps);
				se.regroup(steps);
			}

			return se;
		}
	},

	SOLVE_TRIGONOMETRIC {
		@Override
		public StepNode apply(StepEquation se, StepVariable variable, SolutionBuilder steps) {
			if (!StepHelper.containsTrigonometric(se)) {
				return se;
			}

			StepExpression bothSides = subtract(se.getLHS(), se.getRHS()).regroup();

			StepExpression argument = StepHelper.findTrigonometricVariable(bothSides).getSubTree(0);
			StepExpression sineSquared = power(StepNode.apply(argument, Operation.SIN), 2);
			StepExpression cosineSquared = power(StepNode.apply(argument, Operation.COS), 2);

			StepExpression coeffSineSquared = StepHelper.findCoefficient(bothSides, sineSquared);
			StepExpression coeffCosineSquared = StepHelper.findCoefficient(bothSides, cosineSquared);

			if (coeffSineSquared != null && coeffSineSquared.equals(coeffCosineSquared)) {
				se.addOrSubtract(StepHelper.findVariable(se, sineSquared), steps);

				StepExpression newLHS = subtract(se.getLHS(), add(StepHelper.findVariable(se.getLHS(), sineSquared),
						StepHelper.findVariable(se.getLHS(), cosineSquared))).regroup();
				StepExpression newRHS = subtract(se.getRHS(),
						multiply(coeffSineSquared, add(sineSquared, cosineSquared)));

				se.modify(newLHS, newRHS);
				se.regroup(steps);

				se.replace(add(sineSquared, cosineSquared), new StepConstant(1), steps);
				se.regroup(steps);
			}

			bothSides = subtract(se.getLHS(), se.getRHS()).regroup();

			StepExpression sine = StepNode.apply(argument, Operation.SIN);
			StepExpression cosine = StepNode.apply(argument, Operation.COS);

			StepExpression coeffSine = StepHelper.findCoefficient(bothSides, sine);
			StepExpression coeffCosine = StepHelper.findCoefficient(bothSides, cosine);
			coeffSineSquared = StepHelper.findCoefficient(bothSides, sineSquared);
			coeffCosineSquared = StepHelper.findCoefficient(bothSides, cosineSquared);

			if (coeffSine != null && coeffCosine != null && isZero(coeffSineSquared) && isZero(coeffCosineSquared)) {
				if (!isZero(StepHelper.findVariable(se.getLHS(), sine))) {
					se.addOrSubtract(StepHelper.findVariable(se.getLHS(), cosine), steps);
					se.addOrSubtract(StepHelper.findVariable(se.getRHS(), sine), steps);
				} else {
					se.addOrSubtract(StepHelper.findVariable(se.getRHS(), cosine), steps);
				}

				se.square(steps);
				bothSides = subtract(se.getLHS(), se.getRHS()).regroup();
			}

			if (se.getLHS().isConstant() && se.getRHS().isConstant()) {

				if (isEqual(se.getLHS().getValue(), se.getRHS().getValue())) {
					return new StepSet(StepInterval.R);
				}
				return new StepSet();
			}

			StepOperation trigoVar = StepHelper.linearInTrigonometric(bothSides);

			if (trigoVar != null) {
				StepExpression RHSlinear = StepHelper.findVariable(se.getRHS(), trigoVar);
				se.addOrSubtract(RHSlinear, steps);

				StepExpression LHSconstant = StepHelper.findConstant(se.getLHS());
				se.addOrSubtract(LHSconstant, steps);

				StepExpression linearCoefficient = StepHelper.findCoefficient(se.getLHS(), trigoVar);
				se.multiplyOrDivide(linearCoefficient, steps);

				return se;
			}

			trigoVar = StepHelper.quadraticInTrigonometric(bothSides);

			if (trigoVar == null) {
				trigoVar = StepHelper.quadraticInTrigonometric(
						bothSides.deepCopy().replace(sineSquared, subtract(1, cosineSquared)));
				if (trigoVar != null) {
					se.replace(sineSquared, subtract(1, cosineSquared), steps);
					se.regroup(steps);
				}
			}

			if (trigoVar == null) {
				trigoVar = StepHelper.quadraticInTrigonometric(
						bothSides.deepCopy().replace(cosineSquared, subtract(1, sineSquared)));
				if (trigoVar != null) {
					se.replace(cosineSquared, subtract(1, sineSquared), steps);
					se.regroup(steps);
				}
			}

			if (trigoVar != null) {
				StepVariable newVar = new StepVariable("t");

				StepEquation trigonometricReplaced = new StepEquation(se.getLHS().replace(trigoVar, newVar),
						se.getRHS().replace(trigoVar, newVar));

				StepSet allSolutions = new StepSet();
				StepSet tempSolutions = trigonometricReplaced.solve(newVar, steps);

				for (StepNode solution : tempSolutions.getElements()) {
					StepEquation newEq = new StepEquation(trigoVar, (StepExpression) solution);
					allSolutions.addAll(newEq.solve(variable, steps));
				}

				return allSolutions;
			}

			return se;
		}
	},

	SOLVE_SIMPLE_TRIGONOMETRIC {
		@Override
		public StepNode apply(StepEquation se, StepVariable variable, SolutionBuilder steps) {
			if (!se.getLHS().isTrigonometric() || !se.getRHS().isConstant()) {
				return se;
			}

			StepOperation trigoVar = (StepOperation) se.getLHS();

			if (!se.getRHS().canBeEvaluated() && trigoVar.getOperation() != Operation.TAN) {
				return new StepSet();
			}

			if ((trigoVar.isOperation(Operation.SIN) || trigoVar.isOperation(Operation.COS))
					&& (se.getRHS().getValue() < -1 || se.getRHS().getValue() > 1)) {
				steps.add(SolutionStepType.NO_SOLUTION_TRIGONOMETRIC, trigoVar, variable);
				return new StepSet();
			}

			Operation op = StepExpression.getInverse(trigoVar.getOperation());
			StepExpression newLHS = trigoVar.getSubTree(0);

			if (trigoVar.getOperation() == Operation.TAN) {
				StepExpression newRHS = add(StepNode.apply(se.getRHS(), op),
						multiply(se.getNextArbInt(), new StepConstant(Math.PI)));

				StepEquation newEq = new StepEquation(newLHS, newRHS);
				return newEq.solve(variable, steps);
			}

			StepExpression firstRHS = add(StepNode.apply(se.getRHS(), op),
					multiply(multiply(2, se.getNextArbInt()), new StepConstant(Math.PI)));

			StepEquation firstBranch = new StepEquation(newLHS, firstRHS);
			StepSet solutions = firstBranch.solve(variable, steps);

			if (!isEqual(se.getRHS(), 1) && !isEqual(se.getRHS(), -1)) {
				StepExpression secondRHS = add(StepNode.apply(se.getRHS(), op),
						multiply(multiply(2, se.getNextArbInt()), new StepConstant(Math.PI)));
				StepEquation secondBranch;
				if (trigoVar.getOperation() == Operation.SIN) {
					secondBranch = new StepEquation(subtract(new StepConstant(Math.PI), newLHS), secondRHS);
				} else {
					secondBranch = new StepEquation(subtract(multiply(2, new StepConstant(Math.PI)), newLHS),
							secondRHS);
				}

				solutions.addAll(secondBranch.solve(variable, steps));
			}

			return solutions;
		}
	},

	SOLVE_IRRATIONAL {
		@Override
		public StepNode apply(StepEquation se, StepVariable variable, SolutionBuilder steps) {
			int sqrtNum = StepHelper.countNonConstOperation(se.getLHS(), Operation.NROOT)
					+ StepHelper.countNonConstOperation(se.getRHS(), Operation.NROOT);

			if (StepHelper.countNonConstOperation(se.getRHS(), Operation.NROOT) > StepHelper
					.countNonConstOperation(se.getLHS(), Operation.NROOT)) {
				se.swapSides();
			}

			if (sqrtNum > 3 || sqrtNum == 0) {
				return se;
			}

			if (sqrtNum == 1) {
				StepExpression nonIrrational = StepHelper.getNon(se.getLHS(), Operation.NROOT);
				se.addOrSubtract(nonIrrational, steps);
				se.square(steps);
			}

			if (sqrtNum == 2) {
				StepExpression diff = subtract(se.getLHS(), se.getRHS()).regroup();
				if (isZero(StepHelper.getNon(diff, Operation.NROOT))) {
					StepExpression nonIrrational = StepHelper.getNon(se.getLHS(), Operation.NROOT);
					se.addOrSubtract(nonIrrational, steps);
					if (StepHelper.countNonConstOperation(se.getRHS(), Operation.NROOT) == 2) {
						StepExpression oneRoot = StepHelper.getOne(se.getLHS(), Operation.NROOT);
						se.addOrSubtract(oneRoot, steps);
					}
					se.square(steps);
				} else {
					StepExpression rootsRHS = StepHelper.getAll(se.getRHS(), Operation.NROOT);
					se.addOrSubtract(rootsRHS, steps);
					StepExpression nonIrrational = StepHelper.getNon(se.getLHS(), Operation.NROOT);
					se.addOrSubtract(nonIrrational, steps);
					se.square(steps);
				}
			}

			if (sqrtNum == 3) {
				StepExpression nonIrrational = StepHelper.getNon(se.getLHS(), Operation.NROOT);
				se.addOrSubtract(nonIrrational, steps);

				while (StepHelper.countNonConstOperation(se.getRHS(), Operation.NROOT) > 1) {
					StepExpression oneRoot = StepHelper.getOne(se.getRHS(), Operation.NROOT);
					se.addOrSubtract(oneRoot, steps);
				}

				if (StepHelper.countNonConstOperation(se.getLHS(), Operation.NROOT) == 3) {
					StepExpression oneRoot = StepHelper.getOne(se.getLHS(), Operation.NROOT);
					se.addOrSubtract(oneRoot, steps);
				}

				se.square(steps);
			}

			return se;
		}
	},

	SOLVE_ABSOLUTE_VALUE {
		@Override
		public StepNode apply(StepEquation se, StepVariable variable, SolutionBuilder steps) {
			if (StepHelper.countOperation(se, Operation.ABS) == 0) {
				return se;
			}

			if (se.getRestriction() != null) {
				steps.add(SolutionStepType.RESOLVE_ABSOLUTE_VALUES);

				StepExpression LHS = StepHelper.swapAbsInTree(se.getLHS().deepCopy(), se.getRestriction(), variable);
				StepExpression RHS = StepHelper.swapAbsInTree(se.getRHS().deepCopy(), se.getRestriction(), variable);
				se.modify(LHS, RHS);

				se.addStep(steps);
				se.expand(steps);

				return se;
			}

			int absNum = StepHelper.countNonConstOperation(se, Operation.ABS);

			if (StepHelper.countNonConstOperation(se.getRHS(), Operation.ABS) > StepHelper
					.countNonConstOperation(se.getLHS(), Operation.ABS)) {
				se.swapSides();
			}

			StepExpression nonAbsDiff = StepHelper.getNon(subtract(se.getLHS(), se.getRHS()).regroup(), Operation.ABS);
			if (absNum == 1 && (nonAbsDiff == null || nonAbsDiff.isConstant())) {
				StepExpression nonAbsolute = StepHelper.getNon(se.getLHS(), Operation.ABS);
				se.addOrSubtract(nonAbsolute, steps);
				return se;
			} else if (absNum == 2 && (isZero(nonAbsDiff))) {
				if (StepHelper.countNonConstOperation(se.getLHS(), Operation.ABS) == 2) {
					StepExpression oneAbs = StepHelper.getOne(se.getLHS(), Operation.ABS);
					se.addOrSubtract(oneAbs, steps);
				}
				return se;
			}

			ArrayList<StepExpression> absoluteValues = new ArrayList<StepExpression>();
			StepHelper.getAbsoluteValues(absoluteValues, se);

			StepSet tempSolutions = new StepSet();
			for (int i = 0; i < absoluteValues.size(); i++) {
				StepEquation tempEq = new StepEquation(absoluteValues.get(i), new StepConstant(0));
				tempSolutions.addAll(tempEq.solve(variable, null));
			}

			List<StepExpression> roots = new ArrayList<StepExpression>();
			for(StepNode sn : tempSolutions.getElements()) {
				roots.add((StepExpression) sn);
			}
			
			Collections.sort(roots, new Comparator<StepExpression>() {
				@Override
				public int compare(StepExpression s1, StepExpression s2) {
					return Double.compare(s1.getValue(), s2.getValue());
				}
			});

			roots.add(0, new StepConstant(Double.NEGATIVE_INFINITY));
			roots.add(new StepConstant(Double.POSITIVE_INFINITY));

			StepSet solutions = new StepSet();
			for (int i = 1; i < roots.size(); i++) {
				StepEquation newEq = new StepEquation(se.getLHS(), se.getRHS());
				newEq.setRestriction(new StepInterval(roots.get(i - 1), roots.get(i), false, i != roots.size() - 1));

				solutions.addAll(newEq.solve(variable, steps));
			}

			return solutions;
		}
	},

	PLUSMINUS {
		@Override
		public StepNode apply(StepEquation se, StepVariable variable, SolutionBuilder steps) {
			if (se.getLHS().isOperation(Operation.ABS) && se.getRHS().isOperation(Operation.ABS)
					|| se.getRHS().isOperation(Operation.ABS) && se.getLHS().isConstant()
					|| se.getLHS().isOperation(Operation.ABS) && se.getRHS().isConstant()) {
				steps.add(SolutionStepType.RESOLVE_ABSOLUTE_VALUES);

				if (se.getLHS().isOperation(Operation.ABS)) {
					se.modify(((StepOperation) se.getLHS()).getSubTree(0), se.getRHS());
				}
				if (se.getRHS().isOperation(Operation.ABS)) {
					se.modify(se.getLHS(), ((StepOperation) se.getRHS()).getSubTree(0));
				}

				if (!isZero(se.getRHS())) {
					StepExpression newRHS = StepNode.apply(se.getRHS(), Operation.PLUSMINUS);
					se.modify(se.getLHS(), newRHS);
					se.regroup(steps);
				}
			}

			if (se.getRHS().isOperation(Operation.PLUSMINUS)) {
				StepExpression underPM = ((StepOperation) se.getRHS()).getSubTree(0);
				
				if (underPM.isConstant() && se.getLHS().equals(variable)) {
					return new StepSet(underPM, underPM.negate());
				}

				StepEquation positiveBranch = new StepEquation(se.getLHS(), underPM);
				StepEquation negativeBranch = new StepEquation(se.getLHS(), underPM.negate());

				StepSet solutions = positiveBranch.solve(variable, steps);
				solutions.addAll(negativeBranch.solve(variable, steps));

				return solutions;
			}

			return se;
		}
	},

	SOLVE_LINEAR_IN_INVERSE {
		@Override
		public StepNode apply(StepEquation se, StepVariable variable, SolutionBuilder steps) {
			StepExpression inverseVar = StepHelper.linearInInverse(se);

			if (inverseVar == null) {
				return se;
			}

			StepExpression diff = subtract(se.getLHS(), se.getRHS()).regroup();
			StepExpression constant = StepHelper.findConstant(diff);

			if (isZero(subtract(diff, constant).regroup())) {
				return new StepSet();
			}

			StepExpression RHSlinear = StepHelper.findVariable(se.getRHS(), inverseVar);
			se.addOrSubtract(RHSlinear, steps);

			StepExpression LHSconstant = StepHelper.findConstant(se.getLHS());
			se.addOrSubtract(LHSconstant, steps);

			se.reciprocate(steps);

			StepExpression linearCoefficient = StepHelper.findCoefficient(se.getLHS(), inverseVar);
			se.multiplyOrDivide(linearCoefficient, steps);

			return new StepSet(se.getRHS());
		}
	},

	TAKE_ROOT {
		@Override
		public StepNode apply(StepEquation se, StepVariable variable, SolutionBuilder steps) {
			if (!StepHelper.shouldTakeRoot(se.getRHS(), se.getLHS())) {
				return se;
			}

			if (StepHelper.getPower(se.getRHS()) > StepHelper.getPower(se.getLHS())) {
				se.swapSides();
			}

			StepExpression sn = subtract(se.getLHS(), se.getRHS()).regroup();
			StepExpression constant = StepHelper.findConstant(sn);
			sn = subtract(sn, constant).regroup();

			if (!StepHelper.isPower(se.getLHS()) || !StepHelper.isPower(se.getRHS())) {
				if (sn.isOperation(Operation.MULTIPLY) || sn.isOperation(Operation.DIVIDE)
						|| sn.isOperation(Operation.POWER)) {
					se.addOrSubtract(subtract(se.getRHS(), StepHelper.findConstant(se.getRHS())).regroup(), steps);
				}

				se.addOrSubtract(StepHelper.findConstant(se.getLHS()), steps);
			}

			int root = StepHelper.getPower(se.getLHS());

			StepExpression toDivide = se.getLHS().getCoefficient();
			se.multiplyOrDivide(toDivide, steps);

			if (isEven(root) && se.getRHS().isConstant() && se.getRHS().getValue() < 0) {
				return new StepSet();
			}

			steps.add(SolutionStepType.GROUP_WRAPPER);
			steps.levelDown();

			if (root == 2 && se.getRHS().isConstant()) {
				steps.add(SolutionStepType.SQUARE_ROOT);

				StepExpression underSquare = ((StepOperation) se.getLHS()).getSubTree(0);
				if (isEqual(se.getRHS(), 0)) {
					se.modify(underSquare, new StepConstant(0));
				} else {
					se.modify(underSquare, StepNode.apply(root(se.getRHS(), 2), Operation.PLUSMINUS));
				}
			} else {
				se.nthroot(root, steps);
			}

			se.addStep(steps);
			steps.levelUp();
			return se;
		}
	},

	REDUCE_TO_QUADRATIC {
		@Override
		public StepNode apply(StepEquation se, StepVariable variable, SolutionBuilder steps) {
			if (!StepHelper.canBeReducedToQuadratic(subtract(se.getLHS(), se.getRHS()), variable)) {
				return se;
			}

			int degree = StepHelper.degree(se.getLHS());

			StepExpression coeffHigh = StepHelper.findCoefficient(se.getLHS(), power(variable, degree));
			StepExpression coeffLow = StepHelper.findCoefficient(se.getLHS(), power(variable, ((double) degree) / 2));
			StepExpression constant = StepHelper.findConstant(se.getLHS());

			StepVariable newVariable = new StepVariable("t");

			steps.add(SolutionStepType.REPLACE_WITH, power(variable, ((double) degree) / 2),
					newVariable);

			StepExpression newEquation = multiply(coeffHigh, power(newVariable, 2));
			newEquation = add(newEquation, multiply(coeffLow, newVariable));
			newEquation = add(newEquation, constant);

			StepEquation newEq = new StepEquation(newEquation.regroup(), new StepConstant(0));
			StepSet tempSolutions = newEq.solve(newVariable, steps);

			StepSet solutions = new StepSet();
			for (StepNode solution : tempSolutions.getElements()) {
				StepEquation tempEq = new StepEquation(power(variable, ((double) degree) / 2),
						(StepExpression) solution);
				solutions.addAll(tempEq.solve(variable, steps));
			}

			return solutions;
		}
	},

	COMPLETE_CUBE {
		@Override
		public StepNode apply(StepEquation se, StepVariable variable, SolutionBuilder steps) {
			if (!StepHelper.canCompleteCube(subtract(se.getLHS(), se.getRHS()), variable)) {
				return se;
			}

			StepExpression constant = StepHelper.findConstant(se.getLHS());
			StepExpression quadratic = StepHelper.findCoefficient(se.getLHS(), power(variable, 2));

			StepExpression toComplete = subtract(constant, power(divide(quadratic, 3), 3)).regroup();

			steps.add(SolutionStepType.COMPLETE_THE_CUBE);
			se.addOrSubtract(toComplete, steps);

			se.factor(steps);

			return se;
		}
	};

	public static StepSet checkSolutions(StepEquation se, StepSet solutions, StepVariable variable,
			SolutionBuilder steps) {

		if (solutions.size() == 0) {
			steps.add(SolutionStepType.NO_REAL_SOLUTION);
			return solutions;
		} else if (solutions.size() == 1) {
			steps.add(SolutionStepType.SOLUTION, solutions.getElements());
		} else if (solutions.size() > 1) {
			steps.add(SolutionStepType.SOLUTIONS, solutions.getElements());
		}

		SolutionBuilder temp = new SolutionBuilder();
		for (StepNode sol : solutions.getElements()) {
			if (sol instanceof StepExpression) {
				StepExpression solution = (StepExpression) sol;

				if (!se.checkSolution(solution, variable, temp)) {
					solutions.remove(solution);
				}
			}
		}

		if (se.getRestriction() != null || se.getUndefinedPoints() != null || se.shouldCheck()) {
			steps.add(SolutionStepType.CHECK_VALIDITY);
			steps.levelDown();
			steps.addAll(temp.getSteps());
		}

		return solutions;
	}
}
