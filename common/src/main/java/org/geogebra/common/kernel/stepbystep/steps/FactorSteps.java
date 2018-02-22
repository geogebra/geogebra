package org.geogebra.common.kernel.stepbystep.steps;

import static org.geogebra.common.kernel.stepbystep.steptree.StepExpression.nonTrivialPower;
import static org.geogebra.common.kernel.stepbystep.steptree.StepExpression.nonTrivialProduct;
import static org.geogebra.common.kernel.stepbystep.steptree.StepNode.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.geogebra.common.kernel.stepbystep.solution.SolutionBuilder;
import org.geogebra.common.kernel.stepbystep.solution.SolutionStepType;
import org.geogebra.common.kernel.stepbystep.steptree.StepConstant;
import org.geogebra.common.kernel.stepbystep.steptree.StepExpression;
import org.geogebra.common.kernel.stepbystep.steptree.StepNode;
import org.geogebra.common.kernel.stepbystep.steptree.StepOperation;
import org.geogebra.common.kernel.stepbystep.steptree.StepVariable;
import org.geogebra.common.plugin.Operation;

public enum FactorSteps implements SimplificationStepGenerator {

	FACTOR_COMMON_SUBSTEP {
		@Override
		public StepNode apply(StepNode sn, SolutionBuilder sb, RegroupTracker tracker) {
			if (sn.isOperation(Operation.PLUS)) {
				StepOperation so = (StepOperation) sn;

				List<StepExpression> commonBases = new ArrayList<>();
				List<StepExpression> commonExponents = new ArrayList<>();

				so.getOperand(0).getBasesAndExponents(commonBases, commonExponents);

				List<List<StepExpression>> currentBases = new ArrayList<>();
				List<List<StepExpression>> currentExponents = new ArrayList<>();

				for (int i = 0; i < so.noOfOperands(); i++) {
					currentBases.add(new ArrayList<StepExpression>());
					currentExponents.add(new ArrayList<StepExpression>());

					so.getOperand(i).getBasesAndExponents(currentBases.get(i), currentExponents.get(i));

					boolean[] found = new boolean[commonBases.size()];

					for (int j = 0; j < commonBases.size(); j++) {
						for (int k = 0; k < currentBases.get(i).size(); k++) {
							if (currentBases.get(i).get(k).equals(commonBases.get(j))) {
								StepExpression common = commonExponents.get(j).getCommon(
										currentExponents.get(i).get(k));
								commonExponents.set(j, common);

								if (!isZero(common)) {
									found[j] = true;
								}
							}
						}
					}

					for (int j = 0; j < commonBases.size(); j++) {
						if (!found[j]) {
							commonExponents.set(j, StepConstant.create(0));
						}
					}
				}

				StepOperation result = new StepOperation(Operation.PLUS);

				for (int i = 0; i < so.noOfOperands(); i++) {
					int tempTracker = tracker.getColorTracker();
					for (int j = 0; j < commonBases.size(); j++) {
						for (int k = 0; k < currentBases.get(i).size(); k++) {
							if (!isEqual(commonExponents.get(j), 0)
									&& currentBases.get(i).get(k).equals(commonBases.get(j))) {
								StepExpression differenceOfPowers = subtract(
										currentExponents.get(i).get(k), commonExponents.get(j));
								if (differenceOfPowers.canBeEvaluated() && isEqual(differenceOfPowers.getValue(), 0)) {
									differenceOfPowers = StepConstant.create(0);
								}

								currentExponents.get(i).set(k, differenceOfPowers);
								currentBases.get(i).get(k).setColor(tempTracker++);
							}
						}
					}

					StepExpression currentProduct = null;
					for (int j = 0; j < currentBases.get(i).size(); j++) {
						currentProduct = nonTrivialProduct(currentProduct,
								nonTrivialPower(currentBases.get(i).get(j), currentExponents.get(i).get(j)));
					}

					result.addOperand(currentProduct);
				}

				int tempTracker = tracker.getColorTracker();
				StepExpression common = null;
				for (int i = 0; i < commonBases.size(); i++) {
					if (!isEqual(commonExponents.get(i), 0)) {
						commonBases.get(i).setColor(tempTracker++);
						common = nonTrivialProduct(common,
								nonTrivialPower(commonBases.get(i), commonExponents.get(i)));
					}
				}

				if (isOne(common) || isEqual(common, -1)) {
					return so;
				}

				tracker.setColorTracker(tempTracker);

				sb.add(SolutionStepType.FACTOR_COMMON, common);
				return multiply(common, result);
			}

			return StepStrategies.iterateThrough(this, sn, sb, tracker);
		}
	},

	FACTOR_INTEGER {
		@Override
		public StepNode apply(StepNode sn, SolutionBuilder sb, RegroupTracker tracker) {
			if (sn.isOperation(Operation.PLUS)) {
				StepOperation so = (StepOperation) sn;

				long common = 0;
				boolean allNegative = true;
				StepExpression[] integerParts = new StepExpression[so.noOfOperands() + 1];

				for (int i = 0; i < so.noOfOperands(); i++) {
					integerParts[i] = so.getOperand(i).getIntegerCoefficient();

					if (integerParts[i] == null || !integerParts[i].isInteger()) {
						return so;
					}

					if (integerParts[i].getValue() > 0) {
						allNegative = false;
					}

					common = gcd(common, Math.round(integerParts[i].getValue()));
				}

				if (!allNegative) {
					common = Math.abs(common);
				}

				if (common == 0 || common == 1) {
					return so;
				}

				StepOperation factored = new StepOperation(Operation.PLUS);

				for (int i = 0; i < so.noOfOperands(); i++) {
					StepExpression remainder = StepConstant.create(integerParts[i].getValue() / common);
					integerParts[i].setColor(tracker.getColorTracker());
					remainder.setColor(tracker.incColorTracker());

					factored.addOperand(nonTrivialProduct(remainder, so.getOperand(i).getNonInteger()));
				}

				if (isEqual(common, -1)) {
					sb.add(SolutionStepType.FACTOR_MINUS);
					return minus(factored);
				}

				integerParts[integerParts.length - 1] = StepConstant.create(common);
				integerParts[integerParts.length - 1].setColor(tracker.incColorTracker());

				sb.add(SolutionStepType.FACTOR_GCD, integerParts);
				return multiply(integerParts[integerParts.length - 1], factored);
			}

			return StepStrategies.iterateThrough(this, sn, sb, tracker);
		}
	},

	COMPLETING_THE_SQUARE {
		@Override
		public StepNode apply(StepNode sn, SolutionBuilder sb, RegroupTracker tracker) {
			if (sn.isOperation(Operation.PLUS) && !tracker.isWeakFactor()) {
				StepOperation so = (StepOperation) sn;

				if (so.noOfOperands() == 3) {
					StepExpression first = null, second = null, third = null;

					for (int i = 0; i < 3; i++) {
						if (so.getOperand(i).isSquare() && !so.getOperand(i).isConstant()) {
							first = so.getOperand(i);
						} else if (so.getOperand(i).nonSpecialConstant()) {
							third = so.getOperand(i);
						} else {
							second = so.getOperand(i);
						}
					}

					if (first == null || second == null || third == null) {
						return StepStrategies.iterateThrough(this, sn, sb, tracker);
					}

					StepExpression b = second.findCoefficient(first.getSquareRoot());

					if (b != null && isEven(b)) {
						double toComplete = third.getValue() - b.getValue() * b.getValue() / 4;

						if (toComplete < 0) {
							StepOperation newSum = new StepOperation(Operation.PLUS);
							newSum.addOperand(first);
							newSum.addOperand(second);

							StepExpression asSum = add(StepConstant.create(b.getValue() * b.getValue() / 4),
									StepConstant.create(toComplete));
							third.setColor(tracker.getColorTracker());
							asSum.setColor(tracker.incColorTracker());
							newSum.addOperand(asSum);

							sb.add(SolutionStepType.REPLACE_WITH, third, asSum);
							return newSum;
						}
					}

				}

				// DON'T go further in! factor only the outermost sum
				return so;
			}

			return StepStrategies.iterateThrough(this, sn, sb, tracker);
		}
	},

	FACTOR_BINOM_SQUARED {
		@Override
		public StepNode apply(StepNode sn, SolutionBuilder sb, RegroupTracker tracker) {
			if (sn instanceof StepOperation) {
				StepOperation so = (StepOperation) sn;

				if (so.noOfOperands() >= 3) {
					StepExpression first = null, second = null, third = null;

					for (int i = 0; i < 3; i++) {
						if (so.getOperand(i).isSquare() && first == null) {
							first = so.getOperand(i);
						} else if (so.getOperand(i).isSquare() && third == null) {
							third = so.getOperand(i);
						} else {
							second = so.getOperand(i);
						}
					}

					if (first == null || second == null || third == null) {
						return StepStrategies.iterateThrough(this, sn, sb, tracker);
					}

					StepExpression a = first.getSquareRoot();
					StepExpression b = third.getSquareRoot();
					StepExpression _2ab = multiply(2, multiply(a, b));

					if (isEqual(subtract(second, _2ab).regroup(), 0) || isEqual(add(second, _2ab).regroup(), 0)) {
						boolean negative = isEqual(add(second, _2ab).regroup(), 0);

						if (second.equals(_2ab) || second.equals(_2ab.negate())) {
							if (second.isOperation(Operation.MINUS)) {
								second = ((StepOperation) second).getOperand(0);
							}

							first.setColor(tracker.getColorTracker());
							a.setColor(tracker.getColorTracker());
							((StepOperation) second).getOperand(1).setColor(tracker.incColorTracker());
							third.setColor(tracker.getColorTracker());
							b.setColor(tracker.getColorTracker());
							((StepOperation) second).getOperand(2).setColor(tracker.incColorTracker());

							StepExpression result = negative ? power(subtract(a, b), 2) : power(add(a, b), 2);

							if (negative) {
								sb.add(SolutionStepType.BINOM_SQUARED_DIFF_FACTOR);
							} else {
								sb.add(SolutionStepType.BINOM_SQUARED_SUM_FACTOR);
							}

							if (so.noOfOperands() == 3) {
								return result;
							}

							StepOperation newSum = new StepOperation(Operation.PLUS);
							newSum.addOperand(result);
							for (int i = 3; i < so.noOfOperands(); i++) {
								newSum.addOperand(so.getOperand(i));
							}
							return newSum;
						}

						second.setColor(tracker.getColorTracker());
						_2ab.setColor(tracker.incColorTracker());

						if (negative) {
							_2ab = _2ab.negate();
						}

						sb.add(SolutionStepType.REWRITE_AS, second, _2ab);

						StepOperation newSum = new StepOperation(Operation.PLUS);
						newSum.addOperand(first);
						newSum.addOperand(_2ab);
						newSum.addOperand(third);

						for (int i = 3; i < so.noOfOperands(); i++) {
							newSum.addOperand(so.getOperand(i));
						}

						return newSum;
					}
				}
			}

			return StepStrategies.iterateThrough(this, sn, sb, tracker);
		}
	},

	FACTOR_BINOM_CUBED {
		@Override
		public StepNode apply(StepNode sn, SolutionBuilder sb, RegroupTracker tracker) {
			if (sn.isOperation(Operation.PLUS)) {
				StepOperation so = (StepOperation) sn;

				if (so.noOfOperands() == 4) {
					StepExpression aCube = null, bCube = null;

					for (StepExpression operand : so) {
						if (operand.isCube() && aCube == null && !operand.isNegative()) {
							aCube = operand;
						} else if (operand.isCube() && bCube == null) {
							bCube = operand;
						}
					}

					if (aCube == null || bCube == null) {
						return StepStrategies.iterateThrough(this, sn, sb, tracker);
					}

					StepExpression a = aCube.getCubeRoot();
					StepExpression b = bCube.getCubeRoot();

					StepExpression expanded = power(add(a, b), 3).expand();

					if (isEqual(subtract(so, expanded).regroup(), 0)) {
						StepExpression result = power(add(a, b), 3);

						so.setColor(tracker.getColorTracker());
						result.setColor(tracker.getColorTracker());

						if (b.isNegative()) {
							sb.add(SolutionStepType.BINOM_CUBED_DIFF_FACTOR, tracker.incColorTracker());
						} else {
							sb.add(SolutionStepType.BINOM_CUBED_SUM_FACTOR, tracker.incColorTracker());
						}

						return result;
					}
				}
			}

			return StepStrategies.iterateThrough(this, sn, sb, tracker);
		}
	},

	FACTOR_USING_FORMULA {
		@Override
		public StepNode apply(StepNode sn, SolutionBuilder sb, RegroupTracker tracker) {
			if (sn.isOperation(Operation.PLUS)) {
				StepOperation so = (StepOperation) sn;

				if (so.noOfOperands() != 2) {
					return so;
				}

				if (so.getOperand(0).isSquare() && so.getOperand(1).negate().isSquare()) {
					StepExpression a = so.getOperand(0).getSquareRoot();
					StepExpression b = so.getOperand(1).negate().getSquareRoot();

					so.getOperand(0).setColor(tracker.getColorTracker());
					a.setColor(tracker.incColorTracker());
					so.getOperand(1).setColor(tracker.getColorTracker());
					b.setColor(tracker.incColorTracker());

					StepOperation newProduct = new StepOperation(Operation.MULTIPLY);
					newProduct.addOperand(add(a, b));
					newProduct.addOperand(subtract(a, b));

					sb.add(SolutionStepType.DIFFERENCE_OF_SQUARES_FACTOR);
					return newProduct;
				}

				if (so.getOperand(0).negate().isSquare() && so.getOperand(1).isSquare()) {
					so.getOperand(0).setColor(tracker.incColorTracker());
					so.getOperand(1).setColor(tracker.incColorTracker());

					sb.add(SolutionStepType.REORGANIZE_EXPRESSION);
					return add(so.getOperand(1), so.getOperand(0));
				}

				if (so.getOperand(0).isCube() && so.getOperand(1).isCube() && !tracker.isWeakFactor()) {
					StepExpression a = so.getOperand(0).getCubeRoot();
					StepExpression b = so.getOperand(1).getCubeRoot();

					StepOperation newProduct = new StepOperation(Operation.MULTIPLY);

					if (!a.isNegative() && !b.isNegative()) {
						so.getOperand(0).setColor(tracker.getColorTracker());
						a.setColor(tracker.incColorTracker());

						so.getOperand(1).setColor(tracker.getColorTracker());
						b.setColor(tracker.incColorTracker());

						newProduct.addOperand(add(a, b));
						newProduct.addOperand(add(subtract(power(a, 2), multiply(a, b)), power(b, 2)));

						sb.add(SolutionStepType.SUM_OF_CUBES);
						return newProduct;
					} else if (!a.isNegative() && b.isNegative()) {
						StepExpression minusb = b.negate();

						so.getOperand(0).setColor(tracker.getColorTracker());
						a.setColor(tracker.incColorTracker());

						so.getOperand(1).setColor(tracker.getColorTracker());
						minusb.setColor(tracker.incColorTracker());

						newProduct.addOperand(subtract(a, minusb));
						newProduct.addOperand(add(add(power(a, 2), multiply(a, minusb)), power(minusb, 2)));

						sb.add(SolutionStepType.DIFFERENCE_OF_CUBES_FACTOR);
						return newProduct;
					} else if (a.isNegative() && !b.isNegative()) {
						so.getOperand(0).setColor(tracker.incColorTracker());
						so.getOperand(1).setColor(tracker.incColorTracker());

						sb.add(SolutionStepType.REORGANIZE_EXPRESSION);
						return add(so.getOperand(1), so.getOperand(0));
					}
				}

				// DON'T go further in! factor only the outermost sum
				return so;
			}

			return StepStrategies.iterateThrough(this, sn, sb, tracker);
		}
	},

	REORGANIZE_POLYNOMIAL {
		@Override
		public StepNode apply(StepNode sn, SolutionBuilder sb, RegroupTracker tracker) {
			if (sn.isOperation(Operation.PLUS)) {
				StepOperation so = (StepOperation) sn;

				Set<StepVariable> variableSet = new HashSet<>();
				so.getListOfVariables(variableSet);

				StepVariable var = null;
				if (variableSet.size() == 1) {
					var = (StepVariable) variableSet.toArray()[0];
				}

				if (var == null || !so.integerCoefficients(var)) {
					return StepStrategies.iterateThrough(this, sn, sb, tracker);
				}

				StepExpression[] polynomialForm = so.convertToPolynomial(var);

				if (polynomialForm.length < 3) {
					return StepStrategies.iterateThrough(this, sn, sb, tracker);
				}

				long[] integerForm = new long[polynomialForm.length];
				for (int i = 0; i < polynomialForm.length; i++) {
					if (polynomialForm[i] == null) {
						integerForm[i] = 0;
					} else {
						integerForm[i] = Math.round(polynomialForm[i].getValue());
					}
				}

				long constant = Math.abs(integerForm[0]);
				long highestOrder = Math.abs(integerForm[integerForm.length - 1]);

				if (constant > 100 || highestOrder > 100) {
					return StepStrategies.iterateThrough(this, sn, sb, tracker);
				}

				for (long i = -constant; i <= constant; i++) {
					for (long j = 1; j <= highestOrder; j++) {
						if (i != 0 && constant % i == 0 && highestOrder % j == 0
								&& isEqual(so.getValueAt(var, ((double) i) / j), 0)) {
							StepOperation reorganized = new StepOperation(Operation.PLUS);

							for (int k = polynomialForm.length - 1; k > 0; k--) {
								long coeff = i * integerForm[k] / j;

								reorganized.addOperand(nonTrivialProduct(integerForm[k], nonTrivialPower(var, k)));
								reorganized.addOperand(nonTrivialProduct(-coeff, nonTrivialPower(var, k - 1)));

								integerForm[k - 1] += i * integerForm[k] / j;
							}

							tracker.addMark(reorganized, RegroupTracker.MarkType.EXPAND);
							if (!so.equals(reorganized)) {
								tracker.incColorTracker();
								sb.add(SolutionStepType.REORGANIZE_EXPRESSION);
								return reorganized;
							}
							return so;
						}
					}
				}
			}

			return StepStrategies.iterateThrough(this, sn, sb, tracker);
		}
	},

	FACTOR_POLYNOMIAL {
		@Override
		public StepNode apply(StepNode sn, SolutionBuilder sb, RegroupTracker tracker) {
			if (sn.isOperation(Operation.PLUS) && tracker.isMarked(sn, RegroupTracker.MarkType.EXPAND)) {
				StepOperation so = (StepOperation) sn;

				Set<StepVariable> variableSet = new HashSet<>();
				so.getListOfVariables(variableSet);
				StepVariable var = (StepVariable) variableSet.toArray()[0];

				StepExpression[] polynomialForm = so.convertToPolynomial(var);

				long[] integerForm = new long[polynomialForm.length];
				for (int i = 0; i < polynomialForm.length; i++) {
					if (polynomialForm[i] == null) {
						integerForm[i] = 0;
					} else {
						integerForm[i] = Math.round(polynomialForm[i].getValue());
					}
				}

				long constant = Math.abs(integerForm[0]);
				long highestOrder = Math.abs(integerForm[integerForm.length - 1]);

				for (long i = -constant; i <= constant; i++) {
					for (long j = 1; j <= highestOrder; j++) {
						if (i != 0 && constant % i == 0 && highestOrder % j == 0
								&& isEqual(so.getValueAt(var, ((double) i) / j), 0)) {

							StepOperation factored = new StepOperation(Operation.PLUS);

							StepExpression innerSum = add(nonTrivialProduct(j, var), -i);
							innerSum.setColor(tracker.incColorTracker());
							for (int k = polynomialForm.length - 1; k > 0; k--) {
								long coeff = integerForm[k] / j;
								factored.addOperand(nonTrivialProduct(coeff, multiply(nonTrivialPower(var, k - 1),
										innerSum)));

								integerForm[k - 1] += i * integerForm[k] / j;
							}

							sb.add(SolutionStepType.FACTOR_FROM_PAIR, innerSum);
							return factored;
						}
					}
				}
			}

			return StepStrategies.iterateThrough(this, sn, sb, tracker);
		}
	},

	FACTOR_BINOM_STRATEGY {
		@Override
		public StepNode apply(StepNode sn, SolutionBuilder sb, RegroupTracker tracker) {
			SimplificationStepGenerator[] strategy = new SimplificationStepGenerator[] {
					COMPLETING_THE_SQUARE,
					FACTOR_BINOM_SQUARED
			};

			return StepStrategies.implementGroup(sn, null, strategy, sb, tracker);
		}
	},

	FACTOR_POLYNOMIALS {
		@Override
		public StepNode apply(StepNode sn, SolutionBuilder sb, RegroupTracker tracker) {
			SimplificationStepGenerator[] strategy = new SimplificationStepGenerator[] {
					FACTOR_COMMON_SUBSTEP,
					REORGANIZE_POLYNOMIAL,
					FACTOR_POLYNOMIAL
			};

			return StepStrategies.implementGroup(sn, SolutionStepType.FACTOR_POLYNOMIAL, strategy, sb, tracker);
		}
	},

	FACTOR_COMMON {
		@Override
		public StepNode apply(StepNode sn, SolutionBuilder sb, RegroupTracker tracker) {
			SimplificationStepGenerator[] strategy = new SimplificationStepGenerator[] {
					RegroupSteps.ELIMINATE_OPPOSITES,
					RegroupSteps.REGROUP_SUMS,
					RegroupSteps.REGROUP_PRODUCTS,
					FACTOR_COMMON_SUBSTEP
			};

			return StepStrategies.implementGroup(sn, null, strategy, sb, tracker);
		}
	},

	SIMPLE_FACTOR {
		@Override
		public StepNode apply(StepNode sn, SolutionBuilder sb, RegroupTracker tracker) {
			SimplificationStepGenerator[] defaultStrategy = new SimplificationStepGenerator[] {
					FactorSteps.FACTOR_COMMON,
					FactorSteps.FACTOR_INTEGER,
					FactorSteps.FACTOR_BINOM_STRATEGY,
					FactorSteps.FACTOR_BINOM_CUBED,
					FactorSteps.FACTOR_USING_FORMULA,
					FactorSteps.FACTOR_POLYNOMIALS
			};

			return StepStrategies.implementGroup(sn, null, defaultStrategy, sb, tracker);
		}
	},

	DEFAULT_FACTOR {
		@Override
		public StepNode apply(StepNode sn, SolutionBuilder sb, RegroupTracker tracker) {
			SimplificationStepGenerator[] defaultStrategy = new SimplificationStepGenerator[] {
					RegroupSteps.WEAK_REGROUP,
					FactorSteps.SIMPLE_FACTOR
			};

			return StepStrategies.implementGroup(sn, null, defaultStrategy, sb, tracker);
		}
	};

	@Override
	public boolean isGroupType() {
		return this == FACTOR_BINOM_STRATEGY
				|| this == FACTOR_COMMON
				|| this == DEFAULT_FACTOR
				|| this == SIMPLE_FACTOR
				|| this == FACTOR_POLYNOMIALS;
	}
}
