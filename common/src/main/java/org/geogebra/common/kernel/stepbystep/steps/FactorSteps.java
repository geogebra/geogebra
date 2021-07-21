package org.geogebra.common.kernel.stepbystep.steps;

import static org.geogebra.common.kernel.stepbystep.steptree.StepExpression.makeProduct;
import static org.geogebra.common.kernel.stepbystep.steptree.StepExpression.nonTrivialPower;
import static org.geogebra.common.kernel.stepbystep.steptree.StepExpression.nonTrivialProduct;
import static org.geogebra.common.kernel.stepbystep.steptree.StepNode.add;
import static org.geogebra.common.kernel.stepbystep.steptree.StepNode.gcd;
import static org.geogebra.common.kernel.stepbystep.steptree.StepNode.isEqual;
import static org.geogebra.common.kernel.stepbystep.steptree.StepNode.isZero;
import static org.geogebra.common.kernel.stepbystep.steptree.StepNode.minus;
import static org.geogebra.common.kernel.stepbystep.steptree.StepNode.multiply;
import static org.geogebra.common.kernel.stepbystep.steptree.StepNode.power;
import static org.geogebra.common.kernel.stepbystep.steptree.StepNode.subtract;

import java.util.ArrayList;
import java.util.List;

import org.geogebra.common.kernel.stepbystep.StepHelper;
import org.geogebra.common.kernel.stepbystep.solution.SolutionBuilder;
import org.geogebra.common.kernel.stepbystep.solution.SolutionStepType;
import org.geogebra.common.kernel.stepbystep.steptree.StepConstant;
import org.geogebra.common.kernel.stepbystep.steptree.StepExpression;
import org.geogebra.common.kernel.stepbystep.steptree.StepOperation;
import org.geogebra.common.kernel.stepbystep.steptree.StepTransformable;
import org.geogebra.common.kernel.stepbystep.steptree.StepVariable;
import org.geogebra.common.plugin.Operation;

enum FactorSteps implements SimplificationStepGenerator {

	SPLIT_PRODUCTS {
		@Override
		public StepTransformable apply(StepTransformable sn, SolutionBuilder sb,
				RegroupTracker tracker) {
			if (sn.isOperation(Operation.PLUS)
					&& !tracker.isMarked(sn, RegroupTracker.MarkType.FACTOR)) {
				StepOperation so = (StepOperation) sn;

				List<StepExpression> commonBases = new ArrayList<>();
				List<StepExpression> commonExponents = new ArrayList<>();

				StepExpression common = so.getOperand(0);
				for (int i = 1; i < so.noOfOperands(); i++) {
					common = StepHelper.simpleGCD(common, so.getOperand(i));
				}

				if (common == null || isEqual(common, 1) || isEqual(common, -1)) {
					return sn.iterateThrough(this, sb, tracker);
				}

				common = common.deepCopy();

				int colorsAtStart = tracker.getColorTracker();

				common.getBasesAndExponents(commonBases, commonExponents);

				List<List<StepExpression>> currentBases = new ArrayList<>();
				List<List<StepExpression>> currentExponents = new ArrayList<>();

				StepExpression[] operands = new StepExpression[so.noOfOperands()];
				for (int i = 0; i < so.noOfOperands(); i++) {
					currentBases.add(new ArrayList<StepExpression>());
					currentExponents.add(new ArrayList<StepExpression>());
					so.getOperand(i)
							.getBasesAndExponents(currentBases.get(i), currentExponents.get(i));

					StepExpression current = null;
					for (int j = 0; j < currentBases.get(i).size(); j++) {
						int index = commonBases.indexOf(currentBases.get(i).get(j));
						if (index != -1 && !isZero(commonExponents.get(index))) {
							StepExpression differenceOfPowers = subtract(
									currentExponents.get(i).get(j), commonExponents.get(index));
							differenceOfPowers = differenceOfPowers.weakRegroup();
							if (!isZero(differenceOfPowers)) {
								currentExponents.get(i).get(j).setColor(tracker.getColorTracker());
								currentBases.get(i).get(j).setColor(tracker.getColorTracker());

								currentExponents.get(i).set(j, differenceOfPowers);
								currentExponents.get(i).get(j).setColor(tracker.getColorTracker());

								commonBases.get(index).setColor(tracker.getColorTracker());
								commonExponents.get(index).setColor(tracker.incColorTracker());
								current = nonTrivialProduct(current,
										nonTrivialPower(commonBases.get(index),
												commonExponents.get(index)));
								commonBases.get(index).cleanColors();
								commonExponents.get(index).cleanColors();
							}
						}

						current = nonTrivialProduct(current,
								nonTrivialPower(currentBases.get(i).get(j),
										currentExponents.get(i).get(j)));
					}

					if (common.equals(current)) {
						current.setColor(tracker.getColorTracker());
						current = multiply(current, StepConstant.create(1));
						current.setColor(tracker.incColorTracker());
					}

					operands[i] = current;
				}

				StepExpression result = StepOperation.add(operands);

				if (colorsAtStart != tracker.getColorTracker()) {
					sb.add(SolutionStepType.SPLIT_PRODUCTS);
				}

				if (!result.equals(sn)) {
					tracker.incColorTracker();
				}

				tracker.addMark(result, RegroupTracker.MarkType.FACTOR);
				return result;
			}

			return sn.iterateThrough(this, sb, tracker);
		}
	},

	FACTOR_COMMON {
		@Override
		public StepTransformable apply(StepTransformable sn, SolutionBuilder sb,
				RegroupTracker tracker) {
			if (sn.isOperation(Operation.PLUS)) {
				StepOperation so = (StepOperation) sn;

				StepExpression common = so.getOperand(0);
				for (int i = 1; common != null && i < so.noOfOperands(); i++) {
					common = common.getCommonProduct(so.getOperand(i));
				}

				if (common == null || isEqual(common, 1) || isEqual(common, -1)) {
					return sn.iterateThrough(this, sb, tracker);
				}

				List<StepExpression> commonBases = new ArrayList<>();
				List<StepExpression> commonExponents = new ArrayList<>();

				common.getBasesAndExponents(commonBases, commonExponents);

				List<List<StepExpression>> currentBases = new ArrayList<>();
				List<List<StepExpression>> currentExponents = new ArrayList<>();

				int commonColor = tracker.getColorTracker();
				common.setColor(tracker.incColorTracker());

				StepExpression[] operands = new StepExpression[so.noOfOperands()];
				for (int i = 0; i < so.noOfOperands(); i++) {
					currentBases.add(new ArrayList<StepExpression>());
					currentExponents.add(new ArrayList<StepExpression>());
					so.getOperand(i)
							.getBasesAndExponents(currentBases.get(i), currentExponents.get(i));

					for (int j = 0; j < commonBases.size(); j++) {
						for (int k = 0; k < currentBases.get(i).size(); k++) {
							if (commonBases.get(j).equals(currentBases.get(i).get(k))
									&& commonExponents.get(j)
											.equals(currentExponents.get(i).get(k))) {
								currentBases.get(i).get(k).setColor(commonColor);
								currentExponents.get(i).get(k).setColor(commonColor);
								currentExponents.get(i).set(k, null);
								break;
							}
						}
					}

					for (int j = 0; j < currentBases.get(i).size(); j++) {
						if (currentExponents.get(i).get(j) != null) {
							currentBases.get(i).get(j).setColor(tracker.getColorTracker());
							currentExponents.get(i).get(j).setColor(tracker.getColorTracker());
						}
					}

					tracker.incColorTracker();

					operands[i] = makeProduct(currentBases.get(i), currentExponents.get(i));
				}

				StepExpression result = StepOperation.add(operands);

				sb.add(SolutionStepType.FACTOR_OUT, common);
				return multiply(common, result);
			}

			return sn.iterateThrough(this, sb, tracker);
		}
	},

	FACTOR_INTEGER {
		@Override
		public StepTransformable apply(StepTransformable sn, SolutionBuilder sb,
				RegroupTracker tracker) {
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

				StepExpression[] operands = new StepExpression[so.noOfOperands()];
				for (int i = 0; i < so.noOfOperands(); i++) {
					StepExpression remainder =
							StepConstant.create(integerParts[i].getValue() / common);
					integerParts[i].setColor(tracker.getColorTracker());
					remainder.setColor(tracker.incColorTracker());

					operands[i] = nonTrivialProduct(remainder, so.getOperand(i).getNonInteger());
				}

				StepExpression factored = StepOperation.add(operands);
				if (isEqual(common, -1)) {
					sb.add(SolutionStepType.FACTOR_MINUS);
					return minus(factored);
				}

				integerParts[integerParts.length - 1] = StepConstant.create(common);
				integerParts[integerParts.length - 1].setColor(tracker.incColorTracker());

				sb.add(SolutionStepType.FACTOR_GCD, integerParts);
				return multiply(integerParts[integerParts.length - 1], factored);
			}

			return sn.iterateThrough(this, sb, tracker);
		}
	},

	COMPLETING_THE_SQUARE {
		@Override
		public StepTransformable apply(StepTransformable sn, SolutionBuilder sb,
				RegroupTracker tracker) {
			if (sn.isOperation(Operation.PLUS)) {
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
						return sn.iterateThrough(this, sb, tracker);
					}

					StepExpression b = second.findCoefficient(first.getSquareRoot());

					if (b != null && b.isEven()) {
						double toComplete = third.getValue() - b.getValue() * b.getValue() / 4;

						if (toComplete < 0) {
							StepExpression asSum =
									add(StepConstant.create(b.getValue() * b.getValue() / 4),
											StepConstant.create(toComplete));
							third.setColor(tracker.getColorTracker());
							asSum.setColor(tracker.incColorTracker());

							StepExpression newSum =
									StepOperation.add(first, second, asSum);

							sb.add(SolutionStepType.REPLACE_WITH, third, asSum);
							return newSum;
						}
					}

				}

				// DON'T go further in! factor only the outermost sum
				return so;
			}

			return sn.iterateThrough(this, sb, tracker);
		}
	},

	FACTOR_BINOM_SQUARED {
		@Override
		public StepTransformable apply(StepTransformable sn, SolutionBuilder sb,
				RegroupTracker tracker) {
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
						return sn.iterateThrough(this, sb, tracker);
					}

					StepExpression a = first.getSquareRoot();
					StepExpression b = third.getSquareRoot();
					StepExpression _2ab = multiply(2, multiply(a, b));

					boolean positive = isEqual(subtract(second, _2ab).weakRegroup(), 0);
					boolean negative = !positive && isEqual(add(second, _2ab).weakRegroup(), 0);

					if (positive || negative) {
						if (second.equals(_2ab) || second.equals(_2ab.negate())) {
							if (second.isOperation(Operation.MINUS)) {
								second = ((StepOperation) second).getOperand(0);
							}

							first.setColor(tracker.getColorTracker());
							a.setColor(tracker.getColorTracker());
							((StepOperation) second).getOperand(1)
									.setColor(tracker.incColorTracker());
							third.setColor(tracker.getColorTracker());
							b.setColor(tracker.getColorTracker());
							((StepOperation) second).getOperand(2)
									.setColor(tracker.incColorTracker());

							StepExpression result =
									negative ? power(subtract(a, b), 2) : power(add(a, b), 2);

							if (negative) {
								sb.add(SolutionStepType.BINOM_SQUARED_DIFF_FACTOR);
							} else {
								sb.add(SolutionStepType.BINOM_SQUARED_SUM_FACTOR);
							}

							if (so.noOfOperands() == 3) {
								return result;
							}

							StepExpression[] newSum = new StepExpression[so.noOfOperands() - 2];
							newSum[0] = result;
							for (int i = 3; i < so.noOfOperands(); i++) {
								newSum[i - 2] = so.getOperand(i);
							}
							return new StepOperation(Operation.PLUS, newSum);
						}

						second.setColor(tracker.getColorTracker());
						_2ab.setColor(tracker.incColorTracker());

						if (negative) {
							_2ab = _2ab.negate();
						}

						sb.add(SolutionStepType.REWRITE_AS, second, _2ab);

						StepExpression[] newSum = new StepExpression[so.noOfOperands()];
						newSum[0] = first;
						newSum[1] = _2ab;
						newSum[2] = third;

						for (int i = 3; i < so.noOfOperands(); i++) {
							newSum[i] = so.getOperand(i);
						}

						return new StepOperation(Operation.PLUS, newSum);
					}
				}
			}

			return sn.iterateThrough(this, sb, tracker);
		}
	},

	FACTOR_BINOM_CUBED {
		@Override
		public StepTransformable apply(StepTransformable sn, SolutionBuilder sb,
				RegroupTracker tracker) {
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
						return sn.iterateThrough(this, sb, tracker);
					}

					StepExpression a = aCube.getCubeRoot();
					StepExpression b = bCube.getCubeRoot();

					StepExpression expanded = power(add(a, b), 3).expand();

					if (isEqual(subtract(so, expanded).regroup(), 0)) {
						StepExpression result = power(add(a, b), 3);

						so.setColor(tracker.getColorTracker());
						result.setColor(tracker.getColorTracker());

						if (b.isNegative()) {
							sb.add(SolutionStepType.BINOM_CUBED_DIFF_FACTOR,
									tracker.incColorTracker());
						} else {
							sb.add(SolutionStepType.BINOM_CUBED_SUM_FACTOR,
									tracker.incColorTracker());
						}

						return result;
					}
				}
			}

			return sn.iterateThrough(this, sb, tracker);
		}
	},

	FACTOR_DIFFERENCE_OF_SQUARES {
		@Override
		public StepTransformable apply(StepTransformable sn, SolutionBuilder sb,
				RegroupTracker tracker) {
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

					StepOperation newProduct =
							new StepOperation(Operation.MULTIPLY, add(a, b), subtract(a, b));

					sb.add(SolutionStepType.DIFFERENCE_OF_SQUARES_FACTOR);
					return newProduct;
				}

				if (so.getOperand(0).negate().isSquare() && so.getOperand(1).isSquare()) {
					so.getOperand(0).setColor(tracker.incColorTracker());
					so.getOperand(1).setColor(tracker.incColorTracker());

					sb.add(SolutionStepType.REORGANIZE_EXPRESSION);
					return add(so.getOperand(1), so.getOperand(0));
				}

				// DON'T go further in! factor only the outermost sum
				return so;
			}

			return sn.iterateThrough(this, sb, tracker);
		}
	},

	FACTOR_DIFFERENCE_AND_SUM_OF_CUBES {
		@Override
		public StepTransformable apply(StepTransformable sn, SolutionBuilder sb,
				RegroupTracker tracker) {
			if (sn.isOperation(Operation.PLUS)) {
				StepOperation so = (StepOperation) sn;

				if (so.noOfOperands() != 2) {
					return so;
				}

				if (so.getOperand(0).isCube() && so.getOperand(1).isCube()) {
					StepExpression a = so.getOperand(0).getCubeRoot();
					StepExpression b = so.getOperand(1).getCubeRoot();

					if (!a.isNegative() && !b.isNegative()) {
						so.getOperand(0).setColor(tracker.getColorTracker());
						a.setColor(tracker.incColorTracker());

						so.getOperand(1).setColor(tracker.getColorTracker());
						b.setColor(tracker.incColorTracker());

						sb.add(SolutionStepType.SUM_OF_CUBES);

						return new StepOperation(Operation.MULTIPLY, add(a, b),
								add(subtract(power(a, 2), multiply(a, b)), power(b, 2)));
					} else if (!a.isNegative() && b.isNegative()) {
						StepExpression minusb = b.negate();

						so.getOperand(0).setColor(tracker.getColorTracker());
						a.setColor(tracker.incColorTracker());

						so.getOperand(1).setColor(tracker.getColorTracker());
						minusb.setColor(tracker.incColorTracker());

						sb.add(SolutionStepType.DIFFERENCE_OF_CUBES_FACTOR);

						return new StepOperation(Operation.MULTIPLY, subtract(a, minusb),
								add(add(power(a, 2), multiply(a, minusb)), power(minusb, 2)));
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

			return sn.iterateThrough(this, sb, tracker);
		}
	},

	REORGANIZE_POLYNOMIAL {
		@Override
		public StepTransformable apply(StepTransformable sn, SolutionBuilder sb,
				RegroupTracker tracker) {
			if (sn.isOperation(Operation.PLUS)) {
				StepOperation so = (StepOperation) sn;

				List<StepVariable> variableList = so.getListOfVariables();

				StepVariable var = null;
				if (variableList.size() == 1) {
					var = variableList.get(0);
				}

				if (var == null || so.degree(var) < 2) {
					return sn.iterateThrough(this, sb, tracker);
				}

				StepExpression[] polynomialForm = so.convertToPolynomial(var);

				for (StepExpression coefficient : polynomialForm) {
					if (coefficient != null && !coefficient.isInteger()) {
						return sn.iterateThrough(this, sb, tracker);
					}
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
					return sn.iterateThrough(this, sb, tracker);
				}

				for (long i = -constant; i <= constant; i++) {
					for (long j = 1; j <= highestOrder; j++) {
						if (i != 0 && constant % i == 0 && highestOrder % j == 0
								&& isEqual(so.getValueAt(var, ((double) i) / j), 0)) {

							List<StepExpression> terms = new ArrayList<>();
							for (int k = polynomialForm.length - 1; k > 0; k--) {
								long coeff = i * integerForm[k] / j;

								terms.add(
										nonTrivialProduct(integerForm[k], nonTrivialPower(var, k)));
								terms.add(nonTrivialProduct(-coeff, nonTrivialPower(var, k - 1)));

								integerForm[k - 1] += i * integerForm[k] / j;
							}

							StepExpression reorganized = StepOperation.add(terms);

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

			return sn.iterateThrough(this, sb, tracker);
		}
	},

	FACTOR_POLYNOMIAL {
		@Override
		public StepTransformable apply(StepTransformable sn, SolutionBuilder sb,
				RegroupTracker tracker) {
			if (sn.isOperation(Operation.PLUS)
					&& tracker.isMarked(sn, RegroupTracker.MarkType.EXPAND)) {
				StepOperation so = (StepOperation) sn;

				StepVariable var = so.getListOfVariables().get(0);

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

							StepExpression[] factored = new StepExpression[polynomialForm.length];

							StepExpression innerSum = add(nonTrivialProduct(j, var), -i);
							innerSum.setColor(tracker.incColorTracker());
							for (int k = polynomialForm.length - 1; k > 0; k--) {
								long coeff = integerForm[k] / j;
								if (coeff < 0) {
									factored[polynomialForm.length - k] = multiply(
											nonTrivialProduct(-coeff, nonTrivialPower(var, k - 1)),
											innerSum).negate();
								} else {
									factored[polynomialForm.length - k] = multiply(
											nonTrivialProduct(coeff, nonTrivialPower(var, k - 1)),
											innerSum);
								}

								integerForm[k - 1] += i * integerForm[k] / j;
							}

							sb.add(SolutionStepType.FACTOR_FROM_PAIR, innerSum);
							return StepOperation.add(factored);
						}
					}
				}
			}

			return sn.iterateThrough(this, sb, tracker);
		}
	},

	FACTOR_BINOM_STRATEGY {
		@Override
		public StepTransformable apply(StepTransformable sn, SolutionBuilder sb,
				RegroupTracker tracker) {
			SimplificationStepGenerator[] strategy = new SimplificationStepGenerator[] {
					COMPLETING_THE_SQUARE,
					FACTOR_BINOM_SQUARED,
			};

			return StepStrategies.implementGroup(sn, null, strategy, sb, tracker);
		}
	},

	FACTOR_POLYNOMIALS {
		@Override
		public StepTransformable apply(StepTransformable sn, SolutionBuilder sb,
				RegroupTracker tracker) {
			SimplificationStepGenerator[] strategy = new SimplificationStepGenerator[] {
					REORGANIZE_POLYNOMIAL,
					FACTOR_POLYNOMIAL,
					FACTOR_COMMON_SUBSTEP,
			};

			return StepStrategies
					.implementGroup(sn, SolutionStepType.FACTOR_POLYNOMIAL, strategy, sb, tracker);
		}
	},

	FACTOR_COMMON_SUBSTEP {
		@Override
		public StepTransformable apply(StepTransformable sn, SolutionBuilder sb,
				RegroupTracker tracker) {
			SimplificationStepGenerator[] strategy = new SimplificationStepGenerator[] {
					SPLIT_PRODUCTS,
					FACTOR_COMMON,
			};

			return StepStrategies
					.implementGroup(sn, SolutionStepType.FACTOR_COMMON, strategy, sb, tracker);
		}
	},

	FACTOR_STRATEGY {
		@Override
		public StepTransformable apply(StepTransformable sn, SolutionBuilder sb,
				RegroupTracker tracker) {
			SimplificationStepGenerator[] defaultStrategy = new SimplificationStepGenerator[] {
					FactorSteps.FACTOR_COMMON_SUBSTEP,
					RegroupSteps.REGROUP_SUMS,
					FactorSteps.FACTOR_INTEGER,
					FactorSteps.FACTOR_BINOM_STRATEGY,
					FactorSteps.FACTOR_BINOM_CUBED,
					FactorSteps.FACTOR_DIFFERENCE_OF_SQUARES,
					FactorSteps.FACTOR_DIFFERENCE_AND_SUM_OF_CUBES,
					FactorSteps.FACTOR_POLYNOMIALS
			};

			return StepStrategies.implementGroup(sn, null, defaultStrategy, sb, tracker);
		}
	},

	WEAK_FACTOR_STRATEGY {
		@Override
		public StepTransformable apply(StepTransformable sn, SolutionBuilder sb,
				RegroupTracker tracker) {
			SimplificationStepGenerator[] defaultStrategy = new SimplificationStepGenerator[] {
					FactorSteps.FACTOR_COMMON_SUBSTEP,
					RegroupSteps.REGROUP_SUMS,
					FactorSteps.FACTOR_INTEGER,
					FactorSteps.FACTOR_BINOM_SQUARED,
					FactorSteps.FACTOR_BINOM_CUBED,
					FactorSteps.FACTOR_DIFFERENCE_OF_SQUARES,
					FactorSteps.FACTOR_POLYNOMIALS
			};

			return StepStrategies.implementGroup(sn, null, defaultStrategy, sb, tracker);
		}
	},

	DEFAULT_FACTOR {
		@Override
		public StepTransformable apply(StepTransformable sn, SolutionBuilder sb,
				RegroupTracker tracker) {
			SimplificationStepGenerator[] defaultStrategy = new SimplificationStepGenerator[] {
					RegroupSteps.WEAK_REGROUP,
					FactorSteps.FACTOR_STRATEGY
			};

			return StepStrategies.implementGroup(sn, null, defaultStrategy, sb, tracker);
		}
	},

	WEAK_FACTOR {
		@Override
		public StepTransformable apply(StepTransformable sn, SolutionBuilder sb,
				RegroupTracker tracker) {
			SimplificationStepGenerator[] weakStrategy = new SimplificationStepGenerator[] {
					RegroupSteps.WEAK_REGROUP,
					FactorSteps.WEAK_FACTOR_STRATEGY
			};

			return StepStrategies.implementGroup(sn, null, weakStrategy, sb, tracker);
		}
	};

	@Override
	public boolean isGroupType() {
		return this == FACTOR_BINOM_STRATEGY
				|| this == DEFAULT_FACTOR
				|| this == WEAK_FACTOR_STRATEGY
				|| this == FACTOR_STRATEGY
				|| this == FACTOR_POLYNOMIALS
				|| this == FACTOR_COMMON_SUBSTEP;
	}
}
