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
import org.geogebra.common.kernel.stepbystep.steptree.*;
import org.geogebra.common.plugin.Operation;

public enum EquationSteps implements SolveStepGenerator {

	REGROUP {
		@Override
		public StepNode apply(StepSolvable se, StepVariable variable, SolutionBuilder steps) {
			return se.regroup(steps);
		}
	},

	EXPAND {
		@Override
		public StepNode apply(StepSolvable se, StepVariable variable, SolutionBuilder steps) {
			return se.expand(steps);
		}
	},

	FACTOR {
		@Override
		public StepNode apply(StepSolvable se, StepVariable variable, SolutionBuilder steps) {
			if (isZero(se.getLHS()) || isZero(se.getRHS())) {
				return StepStrategies.defaultFactor(se, steps, new RegroupTracker().setWeakFactor());
			}

			return se;
		}
	},

	DIFF {
		@Override
		public StepNode apply(StepSolvable se, StepVariable variable, SolutionBuilder steps) {
			se.subtract(se.getRHS(), steps);
			return se;
		}
	},

	SUBTRACT_COMMON {
		@Override
		public StepNode apply(StepSolvable se, StepVariable variable, SolutionBuilder steps) {
			StepExpression common = se.getLHS().nonIntegersOfSum().getCommon(se.getRHS().nonIntegersOfSum());
			if (!se.getLHS().equals(se.getRHS()) && common != null && !common.isInteger()) {
				se.addOrSubtract(common, steps);
			}

			return se;
		}
	},

	SOLVE_LINEAR {
		@Override
		public StepNode apply(StepSolvable se, StepVariable variable, SolutionBuilder steps) {
			StepExpression diff = subtract(se.getLHS(), se.getRHS()).regroup();

			if (diff.isConstantIn(variable)) {
				return se.trivialSolution(variable);
			}

			if (!(se.degree(variable) == 0 || se.degree(variable) == 1)) {
				return se;
			}

			if (StepHelper.getCoefficientValue(se.getLHS(), variable) < StepHelper.getCoefficientValue(se.getRHS(),
					variable)) {
				se.swapSides();
			}

			StepExpression RHSlinear = se.getRHS().findExpression(variable);
			se.addOrSubtract(RHSlinear, steps);

			StepExpression LHSconstant = se.getLHS().findConstantIn(variable);
			se.addOrSubtract(LHSconstant, steps);

			StepExpression linearCoefficient = se.getLHS().findCoefficient(variable);
			se.multiplyOrDivide(linearCoefficient, steps);

			se.cleanColors();

			return se.trivialSolution(variable);
		}
	},

	SOLVE_QUADRATIC {
		@Override
		public StepNode apply(StepSolvable se, StepVariable variable, SolutionBuilder steps) {
			if (se.degree(variable) != 2) {
				return se;
			}

			double leftSquareCoeff = StepHelper.getCoefficientValue(se.getLHS(), power(variable, 2));
			double rightSquareCoeff = StepHelper.getCoefficientValue(se.getRHS(), power(variable, 2));

			if (leftSquareCoeff < 0 && isZero(se.getRHS())) {
				se.multiply(StepConstant.create(-1), steps);
			} else if (rightSquareCoeff < 0 && isZero(se.getLHS())) {
				se.multiply(StepConstant.create(-1), steps);
				se.swapSides();
			} else if (leftSquareCoeff < rightSquareCoeff) {
				se.swapSides();
			}

			StepExpression difference = subtract(se.getLHS(), se.getRHS()).regroup();

			StepExpression a = difference.findCoefficient(power(variable, 2));
			StepExpression b = difference.findCoefficient(variable);
			StepExpression c = difference.findConstantIn(variable);

			if (isOne(a) && isEven(b) && !isZero(c)) {
				StepExpression RHSConstant = se.getRHS().findConstantIn(variable);
				se.addOrSubtract(subtract(se.getRHS(), RHSConstant).regroup(), steps);

				StepExpression LHSConstant = se.getLHS().findConstantIn(variable);
				StepExpression toComplete = subtract(LHSConstant, power(divide(b, 2), 2)).regroup();

				steps.add(SolutionStepType.COMPLETE_THE_SQUARE);

				se.addOrSubtract(toComplete, steps);
				se.factor(steps);

				if (se.getRHS().getValue() < 0) {
					return new StepSet();
				}

				return se;
			}

			if (!isZero(se.getRHS())) {
				se.subtract(se.getRHS(), steps);
				return se;
			}

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

				return new StepSet(solution1.adaptiveRegroup(), solution2.adaptiveRegroup());
			}

			return new StepSet();
		}
	},

	SOLVE_PRODUCT {
		@Override
		public StepNode apply(StepSolvable se, StepVariable variable, SolutionBuilder steps) {
			StepOperation product = null;

			if (se.getRHS().isOperation(Operation.MULTIPLY) && isZero(se.getLHS())) {
				product = (StepOperation) se.getRHS();
			} else if (se.getLHS().isOperation(Operation.MULTIPLY) && isZero(se.getRHS())) {
				product = (StepOperation) se.getLHS();
			}

			if (product != null) {
				steps.add(SolutionStepType.PRODUCT_IS_ZERO);
				StepSet solutions = new StepSet();

				for (StepExpression operand : product) {
					if (!operand.isConstant()) {
						StepEquation newEq = new StepEquation(operand, StepConstant.create(0));
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
		public StepNode apply(StepSolvable se, StepVariable variable, SolutionBuilder steps) {
			if (StepHelper.linearInInverse(se, variable) == null) {
				return StepStrategies.defaultRegroup(se, steps, new RegroupTracker().unsetIntegerFractions());
			}

			return se;
		}
	},

	MULTIPLY_THROUGH {
		@Override
		public StepNode apply(StepSolvable se, StepVariable variable, SolutionBuilder steps) {
			StepExpression commonDenominator = StepConstant.create(1);
			if (!se.getLHS().isConstant() && se.getLHS().getDenominator() != null) {
					commonDenominator = StepHelper.LCM(se.getLHS().getDenominator(), commonDenominator);
			}

			if (!se.getRHS().isConstant() && se.getRHS().getDenominator() != null) {
					commonDenominator = StepHelper.LCM(se.getRHS().getDenominator(), commonDenominator);
			}

			se.multiply(commonDenominator, steps);
			return se;
		}
	},

	RECIPROCATE_EQUATION {
		@Override
		public StepNode apply(StepSolvable se, StepVariable variable, SolutionBuilder steps) {
			if (StepHelper.shouldReciprocate(se.getLHS()) && StepHelper.shouldReciprocate(se.getRHS())) {
				se.reciprocate(steps);
			}

			return se;
		}
	},

	SIMPLIFY_TRIGONOMETRIC {
		@Override
		public StepNode apply(StepSolvable se, StepVariable variable, SolutionBuilder steps) {
			StepExpression bothSides = subtract(se.getLHS(), se.getRHS()).regroup();
			StepOperation trigoVar = StepHelper.findTrigonometricVariable(bothSides);

			if (trigoVar == null) {
				return se;
			}

			StepExpression argument = trigoVar.getOperand(0);
			StepExpression sineSquared = power(StepNode.apply(argument, Operation.SIN), 2);
			StepExpression cosineSquared = power(StepNode.apply(argument, Operation.COS), 2);

			StepExpression coeffSineSquared = bothSides.findCoefficient(sineSquared);
			StepExpression coeffCosineSquared = bothSides.findCoefficient(cosineSquared);

			if (coeffSineSquared != null && coeffSineSquared.equals(coeffCosineSquared)) {
				se.addOrSubtract(se.getRHS().findExpression(sineSquared), steps);

				StepExpression newLHS = subtract(se.getLHS(), add(se.getLHS().findExpression(sineSquared),
						se.getLHS().findExpression(cosineSquared))).regroup();
				StepExpression newRHS = subtract(se.getRHS(),
						multiply(coeffSineSquared, add(sineSquared, cosineSquared)));

				se.modify(newLHS, newRHS);

				se.replace(add(sineSquared, cosineSquared), StepConstant.create(1), steps);
				se.regroup(steps);
			}

			bothSides = subtract(se.getLHS(), se.getRHS()).regroup();

			StepExpression sine = StepNode.apply(argument, Operation.SIN);
			StepExpression cosine = StepNode.apply(argument, Operation.COS);

			StepExpression coeffSine = bothSides.findCoefficient(sine);
			StepExpression coeffCosine = bothSides.findCoefficient(cosine);
			coeffSineSquared = bothSides.findCoefficient(sineSquared);
			coeffCosineSquared = bothSides.findCoefficient(cosineSquared);

			if (coeffSine != null && coeffCosine == null && coeffSineSquared != null && coeffCosineSquared != null) {
				se.replace(cosineSquared, subtract(1, sineSquared), steps);
				se.regroup(steps);
			}

			if (coeffSine == null && coeffCosine != null && coeffSineSquared != null && coeffCosineSquared != null) {
				se.replace(sineSquared, subtract(1, cosineSquared), steps);
				se.regroup(steps);
			}

			if (coeffSine != null && coeffCosine != null && coeffSineSquared == null && coeffCosineSquared == null) {
				if (!isZero(se.getLHS().findExpression(sine))) {
					se.addOrSubtract(se.getLHS().findExpression(cosine), steps);
					se.addOrSubtract(se.getRHS().findExpression(sine), steps);
				} else {
					se.addOrSubtract(se.getRHS().findExpression(cosine), steps);
				}

				se.square(steps);
			}

			return se;
		}
	},

	SOLVE_LINEAR_TRIGONOMETRC {
		@Override
		public StepNode apply(StepSolvable se, StepVariable variable, SolutionBuilder steps) {
			StepOperation trigoVar = StepHelper.linearInTrigonometric(se);

			if (trigoVar == null) {
				return se;
			}

			StepExpression RHSlinear = se.getRHS().findExpression(trigoVar);
			se.addOrSubtract(RHSlinear, steps);

			StepExpression LHSconstant = se.getLHS().findConstantIn(variable);
			se.addOrSubtract(LHSconstant, steps);

			StepExpression linearCoefficient = se.getLHS().findCoefficient(trigoVar);
			se.multiplyOrDivide(linearCoefficient, steps);

			return se;
		}
	},

	SOLVE_QUADRATIC_TRIGONOMETRIC {
		@Override
		public StepNode apply(StepSolvable se, StepVariable variable, SolutionBuilder steps) {
			StepExpression trigoVar = StepHelper.quadraticInTrigonometric(se);

			if (trigoVar == null) {
				return se;
			}

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
	},

	SOLVE_SIMPLE_TRIGONOMETRIC {
		@Override
		public StepNode apply(StepSolvable se, StepVariable variable, SolutionBuilder steps) {
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
			StepExpression newLHS = trigoVar.getOperand(0);

			if (trigoVar.getOperation() == Operation.TAN) {
				StepExpression newRHS = add(StepNode.apply(se.getRHS(), op),
						multiply(se.getNextArbInt(), StepConstant.PI));

				StepEquation newEq = new StepEquation(newLHS, newRHS);
				return newEq.solve(variable, steps);
			}

			StepExpression firstRHS = add(StepNode.apply(se.getRHS(), op),
					multiply(multiply(2, se.getNextArbInt()), StepConstant.PI));

			StepEquation firstBranch = new StepEquation(newLHS, firstRHS);
			StepSet solutions = firstBranch.solve(variable, steps);

			if (!isEqual(se.getRHS(), 1) && !isEqual(se.getRHS(), -1)) {
				StepExpression secondRHS = add(StepNode.apply(se.getRHS(), op),
						multiply(multiply(2, se.getNextArbInt()), StepConstant.PI));
				StepEquation secondBranch;
				if (trigoVar.getOperation() == Operation.SIN) {
					secondBranch = new StepEquation(subtract(StepConstant.PI, newLHS), secondRHS);
				} else {
					secondBranch = new StepEquation(subtract(multiply(2, StepConstant.PI), newLHS), secondRHS);
				}

				solutions.addAll(secondBranch.solve(variable, steps));
			}

			return solutions;
		}
	},

	SOLVE_IRRATIONAL {
		@Override
		public StepNode apply(StepSolvable se, StepVariable variable, SolutionBuilder steps) {
			int sqrtNum = se.countNonConstOperation(Operation.NROOT, variable);

			if (se.getRHS().countNonConstOperation(Operation.NROOT, variable) >
					se.getLHS().countNonConstOperation(Operation.NROOT, variable)) {
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
					if (se.getRHS().countNonConstOperation(Operation.NROOT, variable) == 2) {
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

				while (se.getRHS().countNonConstOperation(Operation.NROOT, variable) > 1) {
					StepExpression oneRoot = StepHelper.getOne(se.getRHS(), Operation.NROOT);
					se.addOrSubtract(oneRoot, steps);
				}

				if (se.getLHS().countNonConstOperation(Operation.NROOT, variable) == 3) {
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
		public StepNode apply(StepSolvable se, StepVariable variable, SolutionBuilder steps) {
			if (se.countOperation(Operation.ABS) == 0) {
				return se;
			}

			if (!se.getRestriction().equals(StepInterval.R)) {
				steps.add(SolutionStepType.RESOLVE_ABSOLUTE_VALUES);

				StepExpression LHS = StepHelper.swapAbsInTree(se.getLHS().deepCopy(), se.getRestriction(), variable);
				StepExpression RHS = StepHelper.swapAbsInTree(se.getRHS().deepCopy(), se.getRestriction(), variable);
				se.modify(LHS, RHS);

				steps.add(se);
				se.expand(steps);

				return se;
			}

			int absNum = se.countNonConstOperation(Operation.ABS, variable);

			if (se.getRHS().countNonConstOperation(Operation.ABS, variable) >
					se.getLHS().countNonConstOperation(Operation.ABS, variable)) {
				se.swapSides();
			}

			StepExpression nonAbsDiff = StepHelper.getNon(subtract(se.getLHS(), se.getRHS()).regroup(), Operation.ABS);
			if (absNum == 1 && (nonAbsDiff == null || nonAbsDiff.isConstant())) {
				StepExpression nonAbsolute = StepHelper.getNon(se.getLHS(), Operation.ABS);
				se.addOrSubtract(nonAbsolute, steps);
				return se;
			} else if (absNum == 2 && (isZero(nonAbsDiff))) {
				if (se.getLHS().countNonConstOperation(Operation.ABS, variable) == 2) {
					StepExpression oneAbs = StepHelper.getOne(se.getLHS(), Operation.ABS);
					se.addOrSubtract(oneAbs, steps);
				}
				return se;
			}

			ArrayList<StepExpression> absoluteValues = new ArrayList<>();
			StepHelper.getAbsoluteValues(absoluteValues, se);

			StepSet tempSolutions = new StepSet();
			for (StepExpression absoluteValue : absoluteValues) {
				StepEquation tempEq = new StepEquation(absoluteValue, StepConstant.create(0));
				tempSolutions.addAll(tempEq.solve(variable, null));
			}

			List<StepExpression> roots = new ArrayList<>();
			for(StepNode sn : tempSolutions.getElements()) {
				roots.add((StepExpression) sn);
			}

			Collections.sort(roots, new Comparator<StepExpression>() {
				@Override
				public int compare(StepExpression s1, StepExpression s2) {
					return Double.compare(s1.getValue(), s2.getValue());
				}
			});

			roots.add(0, StepConstant.NEG_INF);
			roots.add(StepConstant.POS_INF);

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
		public StepNode apply(StepSolvable se, StepVariable variable, SolutionBuilder steps) {
			if (se.getLHS().isOperation(Operation.ABS) && se.getRHS().isOperation(Operation.ABS)
					|| se.getRHS().isOperation(Operation.ABS) && se.getLHS().isConstantIn(variable)
					|| se.getLHS().isOperation(Operation.ABS) && se.getRHS().isConstantIn(variable)) {

				if (se.getLHS().isOperation(Operation.ABS)) {
					se.modify(((StepOperation) se.getLHS()).getOperand(0), se.getRHS());
				}
				if (se.getRHS().isOperation(Operation.ABS)) {
					se.modify(se.getLHS(), ((StepOperation) se.getRHS()).getOperand(0));
				}

				if (!isZero(se.getRHS())) {
					StepExpression newRHS = StepNode.apply(se.getRHS(), Operation.PLUSMINUS);
					se.modify(se.getLHS(), newRHS);
					se.regroup(steps);
				}

				steps.add(SolutionStepType.GROUP_WRAPPER);
				steps.levelDown();
				steps.add(SolutionStepType.RESOLVE_ABSOLUTE_VALUES);
				steps.add(se);
				steps.levelUp();

				return se;
			}

			if (se.getRHS().isOperation(Operation.PLUSMINUS)) {
				StepExpression underPM = ((StepOperation) se.getRHS()).getOperand(0);
				
				if (underPM.isConstantIn(variable) && se.getLHS().equals(variable)) {
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
		public StepNode apply(StepSolvable se, StepVariable variable, SolutionBuilder steps) {
			StepExpression inverseVar = StepHelper.linearInInverse(se, variable);

			if (inverseVar == null) {
				return se;
			}

			StepExpression diff = subtract(se.getLHS(), se.getRHS()).regroup();
			StepExpression constant = diff.findConstantIn(variable);

			if (isZero(subtract(diff, constant).regroup())) {
				return new StepSet();
			}

			StepExpression RHSlinear = se.getRHS().findExpression(inverseVar);
			se.addOrSubtract(RHSlinear, steps);

			StepExpression LHSconstant = se.getLHS().findConstantIn(variable);
			se.addOrSubtract(LHSconstant, steps);

			return se;
		}
	},

	TAKE_ROOT {
		@Override
		public StepNode apply(StepSolvable se, StepVariable variable, SolutionBuilder steps) {
			StepExpression diff = subtract(se.getLHS(), se.getRHS()).regroup();
			StepExpression constant = diff.findConstantIn(variable);
			StepExpression noConstDiff = subtract(diff, constant).regroup();

			if (noConstDiff.isPower()) {
				StepExpression RHSConstant = se.getRHS().findConstantIn(variable);
				StepExpression RHSNonConst = subtract(se.getRHS(), RHSConstant).regroup();

				se.addOrSubtract(RHSNonConst, steps);
				se.addOrSubtract(se.getLHS().findConstantIn(variable), steps);
			} else if (!se.getLHS().isPower() || !se.getRHS().isPower()) {
				if (diff.isOperation(Operation.PLUS)) {
                    StepOperation so = (StepOperation) diff;

                    if (so.noOfOperands() == 2 && so.getOperand(0).isPower() && so.getOperand(1).isPower()) {
                        se.addOrSubtract(so.getOperand(1), steps);
                    } else {
                        return se;
                    }
                } else {
                    return se;
                }
			}

			long root = StepNode.gcd(se.getLHS().getPower(), se.getRHS().getPower());

			StepExpression toDivide = se.getLHS().getCoefficient();
			se.multiplyOrDivide(toDivide, steps);

			if (isEven(root) && se.getRHS().isConstant() && se.getRHS().getValue() < 0) {
				return new StepSet();
			}

			steps.add(SolutionStepType.GROUP_WRAPPER);
			steps.levelDown();

			if (root == 2 && se.getRHS().isConstant()) {
				steps.add(SolutionStepType.SQUARE_ROOT);

				StepExpression underSquare = ((StepOperation) se.getLHS()).getOperand(0);
				if (isEqual(se.getRHS(), 0)) {
					se.modify(underSquare, StepConstant.create(0));
				} else {
					se.modify(underSquare, StepNode.apply(root(se.getRHS(), 2), Operation.PLUSMINUS));
				}
			} else {
				se.nthroot(root, steps);
			}

			steps.add(se);
			steps.levelUp();
			return se;
		}
	},

	REDUCE_TO_QUADRATIC {
		@Override
		public StepNode apply(StepSolvable se, StepVariable variable, SolutionBuilder steps) {
			int degree = se.degree(variable);

			if (degree % 2 != 0) {
				return se;
			}

			for (int i = 1; i < degree; i++) {
				if (i != degree / 2 && !isZero(se.findCoefficient(power(variable, i)))) {
					return se;
				}
			}

			se.subtract(se.getRHS(), steps);

			StepExpression coeffHigh = se.getLHS().findCoefficient(power(variable, degree));
			StepExpression coeffLow = se.getLHS().findCoefficient(power(variable, ((double) degree) / 2));
			StepExpression constant = se.getLHS().findConstantIn(variable);

			StepVariable newVariable = new StepVariable("t");

			steps.add(SolutionStepType.REPLACE_WITH, power(variable, ((double) degree) / 2),
					newVariable);

			StepExpression newEquation = multiply(coeffHigh, power(newVariable, 2));
			newEquation = add(newEquation, multiply(coeffLow, newVariable));
			newEquation = add(newEquation, constant);

			StepEquation newEq = new StepEquation(newEquation.regroup(), StepConstant.create(0));
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
		public StepNode apply(StepSolvable se, StepVariable variable, SolutionBuilder steps) {
			if (se.degree(variable) != 3) {
				return se;
			}

			StepExpression diff = subtract(se.getLHS(), se.getRHS()).regroup();

			StepExpression cubic = diff.findCoefficient(power(variable, 3));
			StepExpression quadratic = diff.findCoefficient(power(variable, 2));
			StepExpression linear = diff.findCoefficient(variable);
			StepExpression constant = diff.findConstantIn(variable);

			if (!isOne(cubic) || quadratic == null ||
					!power(quadratic, 2).regroup().equals(multiply(3, linear).regroup())) {
				return se;
			}

			se.addOrSubtract(se.getRHS(), steps);

			StepExpression toComplete = subtract(constant, power(divide(quadratic, 3), 3)).regroup();

			steps.add(SolutionStepType.COMPLETE_THE_CUBE);
			se.addOrSubtract(toComplete, steps);

			StepExpression newLHS = (StepExpression) StepStrategies.defaultFactor(
					se.getLHS(), steps, new RegroupTracker().setWeakFactor());
			se.modify(newLHS, se.getRHS());

			return se;
		}
	};

	public static StepSet checkSolutions(StepSolvable se, StepSet solutions, StepVariable variable,
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

		if (!se.getRestriction().equals(StepInterval.R) || se.getUndefinedPoints() != null || se.shouldCheck()) {
			steps.add(SolutionStepType.CHECK_VALIDITY);
			steps.levelDown();
			steps.addAll(temp.getSteps());
		}

		return solutions;
	}
}
