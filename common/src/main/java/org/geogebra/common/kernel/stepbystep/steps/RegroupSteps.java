package org.geogebra.common.kernel.stepbystep.steps;

import static org.geogebra.common.kernel.stepbystep.steptree.StepExpression.closeToAnInteger;
import static org.geogebra.common.kernel.stepbystep.steptree.StepExpression.nonTrivialPower;
import static org.geogebra.common.kernel.stepbystep.steptree.StepExpression.nonTrivialProduct;
import static org.geogebra.common.kernel.stepbystep.steptree.StepNode.abs;
import static org.geogebra.common.kernel.stepbystep.steptree.StepNode.add;
import static org.geogebra.common.kernel.stepbystep.steptree.StepNode.closeToAnInteger;
import static org.geogebra.common.kernel.stepbystep.steptree.StepNode.divide;
import static org.geogebra.common.kernel.stepbystep.steptree.StepNode.gcd;
import static org.geogebra.common.kernel.stepbystep.steptree.StepNode.isEqual;
import static org.geogebra.common.kernel.stepbystep.steptree.StepNode.isEven;
import static org.geogebra.common.kernel.stepbystep.steptree.StepNode.isOdd;
import static org.geogebra.common.kernel.stepbystep.steptree.StepNode.lcm;
import static org.geogebra.common.kernel.stepbystep.steptree.StepNode.minus;
import static org.geogebra.common.kernel.stepbystep.steptree.StepNode.multiply;
import static org.geogebra.common.kernel.stepbystep.steptree.StepNode.power;
import static org.geogebra.common.kernel.stepbystep.steptree.StepNode.root;

import java.util.ArrayList;
import java.util.List;

import org.geogebra.common.kernel.stepbystep.solution.SolutionBuilder;
import org.geogebra.common.kernel.stepbystep.solution.SolutionStepType;
import org.geogebra.common.kernel.stepbystep.steptree.StepConstant;
import org.geogebra.common.kernel.stepbystep.steptree.StepEquation;
import org.geogebra.common.kernel.stepbystep.steptree.StepExpression;
import org.geogebra.common.kernel.stepbystep.steptree.StepNode;
import org.geogebra.common.kernel.stepbystep.steptree.StepOperation;
import org.geogebra.common.plugin.Operation;

public enum RegroupSteps implements SimplificationStepGenerator {

	ELIMINATE_OPPOSITES {
		@Override
		public StepNode apply(StepNode sn, SolutionBuilder sb, int[] colorTracker) {
			if (sn.isOperation(Operation.PLUS)) {
				StepOperation so = (StepOperation) sn;

				StepOperation newSum = new StepOperation(Operation.PLUS);

				boolean[] found = new boolean[so.noOfOperands()];
				for (int i = 0; i < so.noOfOperands(); i++) {
					if (so.getSubTree(i).isOperation(Operation.MINUS)
							&& ((StepOperation) so.getSubTree(i)).getSubTree(0).isOperation(Operation.PLUS)) {
						StepOperation innerSum = (StepOperation) ((StepOperation) so.getSubTree(i)).getSubTree(0);

						for (int j = 0; j < so.noOfOperands() - innerSum.noOfOperands(); j++) {
							boolean foundSum = true;
							for (int k = 0; foundSum && k < innerSum.noOfOperands(); k++) {
								if (!so.getSubTree(j + k).equals(innerSum.getSubTree(k))) {
									foundSum = false;
								}
							}

							if (foundSum) {
								found[i] = true;
								so.getSubTree(i).setColor(colorTracker[0]);

								for (int k = 0; k < innerSum.noOfOperands(); k++) {
									found[j + k] = true;
									so.getSubTree(j + k).setColor(colorTracker[0]);
								}
								sb.add(SolutionStepType.ELIMINATE_OPPOSITES, colorTracker[0]++);
								break;
							}
						}
					}

					for (int j = i + 1; !found[i] && j < so.noOfOperands(); j++) {
						if (so.getSubTree(i).equals(so.getSubTree(j).negate())
								|| so.getSubTree(j).equals(so.getSubTree(i).negate())) {
							so.getSubTree(i).setColor(colorTracker[0]);
							so.getSubTree(j).setColor(colorTracker[0]);
							sb.add(SolutionStepType.ELIMINATE_OPPOSITES, colorTracker[0]++);
							found[i] = true;
							found[j] = true;
						}
					}
				}

				for (int i = 0; i < so.noOfOperands(); i++) {
					if (!found[i]) {
						newSum.addSubTree(so.getSubTree(i));
					}
				}

				if (newSum.noOfOperands() == 0) {
					return new StepConstant(0);
				}

				if (newSum.noOfOperands() == 1) {
					return newSum.getSubTree(0);
				}

				return newSum;
			}

			return StepStrategies.iterateThrough(this, sn, sb, colorTracker);
		}
	},

	EXPAND_ROOT {
		@Override
		public StepNode apply(StepNode sn, SolutionBuilder sb, int[] colorTracker) {
			if (sn.isOperation(Operation.MULTIPLY)) {
				StepOperation so = (StepOperation) sn;

				int rootCount = 0;
				long commonRoot = 1;
				for (int i = 0; i < so.noOfOperands(); i++) {
					if (so.getSubTree(i).isOperation(Operation.NROOT)) {
						double currentRoot = ((StepOperation) so.getSubTree(i)).getSubTree(1).getValue();
						if (closeToAnInteger(currentRoot)) {
							rootCount++;
							commonRoot = lcm(commonRoot, Math.round(currentRoot));
						}
					}
				}

				if (rootCount > 1) {
					StepOperation newProduct = new StepOperation(Operation.MULTIPLY);
					for (int i = 0; i < so.noOfOperands(); i++) {
						if (so.getSubTree(i).isOperation(Operation.NROOT)) {
							double currentRoot = ((StepOperation) so.getSubTree(i)).getSubTree(1).getValue();
							if (closeToAnInteger(currentRoot) && !isEqual(commonRoot, currentRoot)) {
								StepExpression argument = ((StepOperation) so.getSubTree(i)).getSubTree(0);

								StepExpression result = root(power(argument, commonRoot / currentRoot), commonRoot);

								so.getSubTree(i).setColor(colorTracker[0]);
								result.setColor(colorTracker[0]);

								sb.add(SolutionStepType.EXPAND_ROOT, colorTracker[0]++);

								newProduct.addSubTree(result);
							} else {
								newProduct.addSubTree(so.getSubTree(i));
							}
						} else {
							newProduct.addSubTree(so.getSubTree(i));
						}
					}

					return newProduct;
				}
			}

			return StepStrategies.iterateThrough(this, sn, sb, colorTracker);
		}
	},

	COMMON_ROOT {
		@Override
		public StepNode apply(StepNode sn, SolutionBuilder sb, int[] colorTracker) {
			if (sn.isOperation(Operation.MULTIPLY)) {
				StepOperation so = (StepOperation) sn;

				StepExpression newProduct = null;
				StepExpression underRoot = null;

				int rootCount = 0;
				double commonRoot = 0;
				for (int i = 0; i < so.noOfOperands(); i++) {
					if (so.getSubTree(i).isOperation(Operation.NROOT)) {
						double currentRoot = ((StepOperation) so.getSubTree(i)).getSubTree(1).getValue();
						if (isEqual(commonRoot, 0) || isEqual(commonRoot, currentRoot)) {
							commonRoot = currentRoot;
							underRoot = multiply(underRoot, (((StepOperation) so.getSubTree(i)).getSubTree(0)));
							rootCount++;
						} else {
							newProduct = multiply(newProduct, so.getSubTree(i));
						}
					} else {
						newProduct = multiply(newProduct, so.getSubTree(i));
					}
				}

				if (rootCount > 1) {
					for (int i = 0; i < so.noOfOperands(); i++) {
						if (so.getSubTree(i).isOperation(Operation.NROOT)) {
							double currentRoot = ((StepOperation) so.getSubTree(i)).getSubTree(1).getValue();
							if (isEqual(commonRoot, 0) || isEqual(commonRoot, currentRoot)) {
								so.getSubTree(i).setColor(colorTracker[0]);
							}
						}
					}

					StepExpression result = root(underRoot, commonRoot);
					result.setColor(colorTracker[0]);

					sb.add(SolutionStepType.PRODUCT_OF_ROOTS, colorTracker[0]++);

					return multiply(newProduct, result);
				}
			}

			return StepStrategies.iterateThrough(this, sn, sb, colorTracker);
		}
	},

	SQUARE_ROOT_MULTIPLIED_BY_ITSELF {
		@Override
		public StepNode apply(StepNode sn, SolutionBuilder sb, int[] colorTracker) {
			if (sn.isOperation(Operation.MULTIPLY)) {
				StepOperation so = (StepOperation) sn;

				StepOperation newProduct = new StepOperation(Operation.MULTIPLY);

				boolean[] found = new boolean[so.noOfOperands()];
				for (int i = 0; i < so.noOfOperands(); i++) {
					if (so.getSubTree(i).isSquareRoot()) {
						for (int j = i + 1; j < so.noOfOperands(); j++) {
							if (so.getSubTree(i).equals(so.getSubTree(j))) {
								StepExpression result = ((StepOperation) so.getSubTree(i)).getSubTree(0).deepCopy();

								found[i] = found[j] = true;

								so.getSubTree(i).setColor(colorTracker[0]);
								so.getSubTree(j).setColor(colorTracker[0]);
								result.setColor(colorTracker[0]);

								sb.add(SolutionStepType.SQUARE_ROOT_MULTIPLIED_BY_ITSELF, colorTracker[0]++);

								newProduct.addSubTree(result);
							}
						}
					}

					if (!found[i]) {
						newProduct.addSubTree(so.getSubTree(i));
					}
				}

				if (newProduct.noOfOperands() == 1) {
					return newProduct.getSubTree(0);
				}

				return newProduct;
			}

			return StepStrategies.iterateThrough(this, sn, sb, colorTracker);
		}
	},

	DOUBLE_MINUS {
		@Override
		public StepNode apply(StepNode sn, SolutionBuilder sb, int[] colorTracker) {
			if (sn.isOperation(Operation.MINUS)) {
				StepOperation so = (StepOperation) sn;

				if (so.getSubTree(0).isOperation(Operation.MINUS)) {
					StepExpression result = ((StepOperation) so.getSubTree(0)).getSubTree(0);
					so.setColor(colorTracker[0]);
					result.setColor(colorTracker[0]);
					sb.add(SolutionStepType.DOUBLE_MINUS, colorTracker[0]++);

					return result;
				}
				if (so.getSubTree(0).nonSpecialConstant() && so.getSubTree(0).getValue() < 0) {
					StepExpression result = new StepConstant(so.getValue());
					so.setColor(colorTracker[0]);
					result.setColor(colorTracker[0]);
					sb.add(SolutionStepType.DOUBLE_MINUS, colorTracker[0]++);

					return result;
				}
			}

			return StepStrategies.iterateThrough(this, sn, sb, colorTracker);
		}
	},

	DISTRIBUTE_ROOT_OVER_FRACTION {
		@Override
		public StepNode apply(StepNode sn, SolutionBuilder sb, int[] colorTracker) {
			if (sn.isOperation(Operation.NROOT)) {
				StepOperation so = (StepOperation) sn;

				if (so.getSubTree(0).isOperation(Operation.DIVIDE)) {
					StepExpression numerator = root(((StepOperation) so.getSubTree(0)).getSubTree(0), so.getSubTree(1));
					StepExpression denominator = root(((StepOperation) so.getSubTree(0)).getSubTree(1),
							so.getSubTree(1));

					StepExpression result = divide(numerator, denominator);
					so.setColor(colorTracker[0]);
					result.setColor(colorTracker[0]);
					sb.add(SolutionStepType.DISTRIBUTE_ROOT_FRAC, colorTracker[0]++);

					return result;
				}
			}

			return StepStrategies.iterateThrough(this, sn, sb, colorTracker);
		}
	},

	RATIONALIZE_DENOMINATOR {
		@Override
		public StepNode apply(StepNode sn, SolutionBuilder sb, int[] colorTracker) {
			if (sn.isOperation(Operation.DIVIDE)) {
				StepOperation so = (StepOperation) sn;

				if (so.getSubTree(1).isOperation(Operation.NROOT)) {
					double root = ((StepOperation) so.getSubTree(1)).getSubTree(1).getValue();

					if (closeToAnInteger(root)) {
						StepExpression toMultiply = root(
								nonTrivialPower(((StepOperation) so.getSubTree(1)).getSubTree(0), root - 1), root);

						toMultiply.setColor(colorTracker[0]++);

						StepExpression numerator = nonTrivialProduct(so.getSubTree(0), toMultiply);
						StepExpression denominator = multiply(so.getSubTree(1), toMultiply);

						StepExpression result = divide(numerator, denominator);
						sb.add(SolutionStepType.MULTIPLY_NUM_DENOM, toMultiply);

						return result;
					}
				}

				if (so.getSubTree(1).isOperation(Operation.PLUS)) {
					StepOperation sum = (StepOperation) so.getSubTree(1);

					if (sum.noOfOperands() == 2
							&& (sum.getSubTree(0).containsSquareRoot() || sum.getSubTree(1).containsSquareRoot())) {
						StepExpression toMultiply = add(sum.getSubTree(0), sum.getSubTree(1).negate());

						toMultiply.setColor(colorTracker[0]++);

						StepExpression numerator = nonTrivialProduct(so.getSubTree(0), toMultiply);
						StepExpression denominator = multiply(so.getSubTree(1), toMultiply);

						StepExpression result = divide(numerator, denominator);
						sb.add(SolutionStepType.MULTIPLY_NUM_DENOM, toMultiply);

						return result;
					}
				}

				if (so.getSubTree(1).isOperation(Operation.MULTIPLY)) {
					StepOperation product = (StepOperation) so.getSubTree(1);

					StepExpression irrational = null;

					for (int i = 0; irrational == null && i < product.noOfOperands(); i++) {
						if (product.getSubTree(i).isSquareRoot()) {
							irrational = product.getSubTree(i).deepCopy();
						}
					}

					if (irrational != null) {
						irrational.setColor(colorTracker[0]++);

						StepExpression numerator = nonTrivialProduct(so.getSubTree(0), irrational);
						StepExpression denominator = multiply(so.getSubTree(1), irrational);

						StepExpression result = divide(numerator, denominator);
						sb.add(SolutionStepType.MULTIPLY_NUM_DENOM, irrational);

						return result;
					}
				}
			}

			return StepStrategies.iterateThrough(this, sn, sb, colorTracker);
		}
	},

	DISTRIBUTE_MINUS {
		@Override
		public StepNode apply(StepNode sn, SolutionBuilder sb, int[] colorTracker) {
			if (sn instanceof StepOperation) {
				StepOperation so = (StepOperation) sn;

				if (so.isOperation(Operation.MULTIPLY) && isEqual(so.getSubTree(0), -1)) {
					if (so.noOfOperands() == 2 && so.getSubTree(1).isOperation(Operation.PLUS)) {
						so = (StepOperation) minus(so.getSubTree(1));
					}
				}

				if (so.isOperation(Operation.MINUS)) {
					if (so.getSubTree(0).isOperation(Operation.PLUS)) {
						StepOperation result = new StepOperation(Operation.PLUS);
						for (int i = 0; i < ((StepOperation) so.getSubTree(0)).noOfOperands(); i++) {
							result.addSubTree(((StepOperation) so.getSubTree(0)).getSubTree(i).negate());
						}

						so.setColor(colorTracker[0]);
						result.setColor(colorTracker[0]);
						sb.add(SolutionStepType.DISTRIBUTE_MINUS, colorTracker[0]++);

						return result;
					}
				}
			}

			return StepStrategies.iterateThrough(this, sn, sb, colorTracker);
		}
	},

	FACTOR_SQUARE {
		@Override
		public StepNode apply(StepNode sn, SolutionBuilder sb, int[] colorTracker) {
			if (sn.isOperation(Operation.NROOT)) {
				StepOperation so = (StepOperation) sn;

				StepExpression coefficient = so.getSubTree(0).getIntegerCoefficient();
				StepExpression remainder = so.getSubTree(0).getNonInteger();

				if (closeToAnInteger(coefficient)) {
					long root = Math.round(so.getSubTree(1).getValue());

					long power = StepNode.getIntegerPower(Math.round(coefficient.getValue()));
					long gcd = gcd(root, power);

					if (gcd > 1) {
						StepExpression newValue = power(
								new StepConstant(Math.pow(so.getSubTree(0).getValue(), ((double) 1) / gcd)), gcd);

						so.getSubTree(0).setColor(colorTracker[0]);
						newValue.setColor(colorTracker[0]++);

						StepExpression result = root(newValue, root);
						sb.add(SolutionStepType.REWRITE_AS, so.getSubTree(0), newValue);

						return result;
					}

					long newCoefficient = StepNode.largestNthPower(coefficient.getValue(), so.getSubTree(1).getValue());

					if (!isEqual(newCoefficient, 1)) {
						StepExpression result = multiply(newCoefficient,
								root(multiply(coefficient.getValue() / Math.pow(newCoefficient, root), remainder),
										so.getSubTree(1)));

						so.setColor(colorTracker[0]);
						result.setColor(colorTracker[0]);

						sb.add(SolutionStepType.FACTOR_SQUARE, colorTracker[0]++);

						return result;
					}
				}
			}

			return StepStrategies.iterateThrough(this, sn, sb, colorTracker);
		}
	},

	REGROUP_SUMS {
		@Override
		public StepNode apply(StepNode sn, SolutionBuilder sb, int[] colorTracker) {
			StepNode temp = apply(sn, sb, colorTracker, false);
			if (colorTracker[0] == 1) {
				return apply(temp, sb, colorTracker, true);
			}
			return temp;
		}

		public StepNode apply(StepNode sn, SolutionBuilder sb, int[] colorTracker, boolean integer) {
			if (sn instanceof StepOperation) {
				StepOperation so = new StepOperation(((StepOperation) sn).getOperation());

				int colorsAtStart = colorTracker[0];
				for (int i = 0; i < ((StepOperation) sn).noOfOperands(); i++) {
					so.addSubTree(
							(StepExpression) apply(((StepOperation) sn).getSubTree(i), sb, colorTracker, integer));
				}

				if (colorsAtStart < colorTracker[0]) {
					return so;
				}

				so = (StepOperation) sn;

				if (so.isOperation(Operation.PLUS)) {
					colorsAtStart = colorTracker[0];

					StepExpression[] coefficients = new StepExpression[so.noOfOperands()];
					StepExpression[] variables = new StepExpression[so.noOfOperands()];
					for (int i = 0; i < so.noOfOperands(); i++) {
						if (integer) {
							coefficients[i] = so.getSubTree(i).getIntegerCoefficient();
							variables[i] = so.getSubTree(i).getNonInteger();
						} else {
							coefficients[i] = so.getSubTree(i).getCoefficient();
							variables[i] = so.getSubTree(i).getVariable();
						}

						if (coefficients[i] == null) {
							coefficients[i] = new StepConstant(1);
						}
						if (variables[i] == null) {
							variables[i] = new StepConstant(1);
						}
					}

					List<StepExpression> constantList = new ArrayList<StepExpression>();
					double constantSum = 0;
					for (int i = 0; i < so.noOfOperands(); i++) {
						if (coefficients[i].nonSpecialConstant() && isEqual(variables[i], 1)) {
							constantList.add(coefficients[i]);
							constantSum += coefficients[i].getValue();
							coefficients[i] = new StepConstant(0);
						}
					}

					for (int i = 0; i < so.noOfOperands(); i++) {
						if ((integer || !variables[i].isConstant()) && !isEqual(coefficients[i], 0)) {
							boolean foundCommon = false;
							for (int j = i + 1; j < so.noOfOperands(); j++) {
								if (!isEqual(coefficients[j], 0) && !isEqual(variables[i], 1)
										&& variables[i].equals(variables[j])) {
									foundCommon = true;
									so.getSubTree(j).setColor(colorTracker[0]);
									coefficients[i] = add(coefficients[i], coefficients[j]);
									coefficients[j] = new StepConstant(0);
								}
							}
							if (foundCommon) {
								so.getSubTree(i).setColor(colorTracker[0]);
								coefficients[i].setColor(colorTracker[0]);
								variables[i].setColor(colorTracker[0]);
								sb.add(SolutionStepType.COLLECT_LIKE_TERMS, variables[i]);
								colorTracker[0]++;
							}
						}
					}

					StepOperation newSum = new StepOperation(Operation.PLUS);

					for (int i = 0; i < so.noOfOperands(); i++) {
						if (!coefficients[i].equals(new StepConstant(0)) && !variables[i].equals(new StepConstant(0))) {
							if (coefficients[i].nonSpecialConstant() && isEqual(coefficients[i], 1)) {
								newSum.addSubTree(variables[i]);
							} else if (variables[i].nonSpecialConstant() && isEqual(variables[i], 1)) {
								newSum.addSubTree(coefficients[i]);
							} else if (coefficients[i].nonSpecialConstant() && isEqual(coefficients[i], -1)) {
								newSum.addSubTree(minus(variables[i]));
							} else {
								newSum.addSubTree(multiply(coefficients[i], variables[i]));
							}
						}
					}

					StepExpression newConstants = new StepConstant(constantSum);
					if (constantList.size() > 1) {
						for (int i = 0; i < constantList.size(); i++) {
							constantList.get(i).setColor(colorTracker[0]);
						}
						sb.add(SolutionStepType.ADD_CONSTANTS, colorTracker[0]);
						newConstants.setColor(colorTracker[0]);
						colorTracker[0]++;
					}

					if (isEqual(constantSum, 0) && constantList.size() == 1) {
						constantList.get(0).setColor(colorTracker[0]);
						sb.add(SolutionStepType.ZERO_IN_ADDITION, colorTracker[0]++);
					}

					if (!isEqual(constantSum, 0)) {
						newSum.addSubTree(newConstants);
					}

					if (colorsAtStart == colorTracker[0]) {
						newSum = so;
					}

					if (newSum.noOfOperands() == 0) {
						return new StepConstant(0);
					} else if (newSum.noOfOperands() == 1) {
						return newSum.getSubTree(0);
					}

					return newSum;
				}

				return so;
			} else if (sn instanceof StepEquation) {
				StepEquation se = ((StepEquation) sn).deepCopy();

				StepExpression newLHS = (StepExpression) apply(se.getLHS(), sb, colorTracker, integer);
				StepExpression newRHS = (StepExpression) apply(se.getRHS(), sb, colorTracker, integer);

				se.modify(newLHS, newRHS);

				return se;
			}

			return sn;
		}
	},

	EXPAND_FRACTIONS {
		@Override
		public StepNode apply(StepNode sn, SolutionBuilder sb, int[] colorTracker) {
			if (sn.isOperation(Operation.PLUS)) {
				StepOperation so = (StepOperation) sn;

				StepOperation newSum = new StepOperation(Operation.PLUS);

				long newDenominator = 1;

				for (int i = 0; i < so.noOfOperands(); i++) {
					long currentDenominator = StepExpression.getConstantDenominator(so.getSubTree(i));
					if (currentDenominator != 0) {
						newDenominator = lcm(newDenominator, currentDenominator);
					}
				}

				if (newDenominator != 1) {
					boolean wasChanged = false;

					for (int i = 0; i < so.noOfOperands(); i++) {
						long currentDenominator = StepExpression.getConstantDenominator(so.getSubTree(i));
						if (currentDenominator != 0 && currentDenominator != newDenominator) {
							wasChanged = true;

							StepExpression newFraction = divide(
									nonTrivialProduct(new StepConstant(((double) newDenominator) / currentDenominator),
											StepExpression.getNumerator(so.getSubTree(i))),
									newDenominator);

							newFraction.setColor(colorTracker[0]);
							so.getSubTree(i).setColor(colorTracker[0]);

							newSum.addSubTree(newFraction);
						} else {
							newSum.addSubTree(so.getSubTree(i));
						}
					}

					if (wasChanged) {
						StepConstant denominatorNode = new StepConstant(newDenominator);
						denominatorNode.setColor(colorTracker[0]++);
						sb.add(SolutionStepType.EXPAND_FRACTIONS, denominatorNode);

						return newSum;
					}
				}
			}

			return StepStrategies.iterateThrough(this, sn, sb, colorTracker);
		}
	},

	ADD_NUMERATORS {
		@Override
		public StepNode apply(StepNode sn, SolutionBuilder sb, int[] colorTracker) {
			if (sn.isOperation(Operation.PLUS)) {
				StepOperation so = (StepOperation) sn;

				StepExpression remainder = null;
				StepExpression newNumerator = null;
				long newDenominator = 0;

				List<StepExpression> fractions = new ArrayList<StepExpression>();
				for (int i = 0; i < so.noOfOperands(); i++) {
					StepExpression currentNumerator = StepExpression.getNumerator(so.getSubTree(i));
					long currentDenominator = StepExpression.getConstantDenominator(so.getSubTree(i));

					if (newDenominator == 0 && currentDenominator != 0 && currentDenominator != 1) {
						newDenominator = currentDenominator;
					}

					if (currentDenominator != 0 && currentDenominator == newDenominator) {
						newNumerator = add(newNumerator, currentNumerator);
						fractions.add(so.getSubTree(i));
					} else {
						remainder = add(remainder, so.getSubTree(i));
					}
				}

				if (fractions.size() > 1) {
					for (int i = 0; i < fractions.size(); i++) {
						fractions.get(i).setColor(colorTracker[0]);
					}

					StepExpression result = divide(newNumerator, newDenominator);
					result.setColor(colorTracker[0]);
					sb.add(SolutionStepType.ADD_NUMERATORS, colorTracker[0]++);
					return add(remainder, result);
				}
			}

			return StepStrategies.iterateThrough(this, sn, sb, colorTracker);
		}
	},

	SIMPLIFY_FRACTIONS {
		@Override
		public StepNode apply(StepNode sn, SolutionBuilder sb, int[] colorTracker) {
			if (sn instanceof StepOperation) {
				StepOperation so = (StepOperation) sn;

				if (so.isOperation(Operation.DIVIDE) && isEqual(so.getSubTree(1), 1)) {
					so.getSubTree(1).setColor(colorTracker[0]++);
					sb.add(SolutionStepType.DIVIDE_BY_ONE);
					return so.getSubTree(0).deepCopy();
				}

				if (so.isOperation(Operation.DIVIDE) && isEqual(so.getSubTree(1), -1)) {
					so.getSubTree(1).setColor(colorTracker[0]++);
					sb.add(SolutionStepType.DIVIDE_BY_NEGATVE_ONE);
					return minus(so.getSubTree(0).deepCopy());
				}

				if (so.isOperation(Operation.MULTIPLY) || so.isOperation(Operation.DIVIDE)) {
					List<StepExpression> bases = new ArrayList<StepExpression>();
					List<StepExpression> exponents = new ArrayList<StepExpression>();

					StepExpression.getBasesAndExponents(so, null, bases, exponents);

					for (int i = 0; i < bases.size(); i++) {
						for (int j = i + 1; j < bases.size(); j++) {
							if ((exponents.get(i).getValue() * exponents.get(j).getValue()) < 0
									&& bases.get(i).equals(bases.get(j)) && !isEqual(bases.get(i), 1)) {
								bases.get(i).setColor(colorTracker[0]);
								bases.get(j).setColor(colorTracker[0]);

								double min = Math.min(Math.abs(exponents.get(i).getValue()),
										Math.abs(exponents.get(j).getValue()));

								exponents.get(i).setColor(colorTracker[0]);
								exponents.get(j).setColor(colorTracker[0]);

								double newExponent1 = exponents.get(i).getValue() > 0
										? exponents.get(i).getValue() - min
										: exponents.get(i).getValue() + min;
								double newExponent2 = exponents.get(j).getValue() > 0
										? exponents.get(j).getValue() - min
										: exponents.get(j).getValue() + min;

								exponents.set(i, new StepConstant(newExponent1));
								exponents.set(j, new StepConstant(newExponent2));

								exponents.get(i).setColor(colorTracker[0]);
								exponents.get(j).setColor(colorTracker[0]);

								StepExpression toCancel = nonTrivialPower(bases.get(i), min);
								toCancel.setColor(colorTracker[0]++);
								sb.add(SolutionStepType.CANCEL_FRACTION, toCancel);

								break;
							}
							if (isEqual(exponents.get(i), 1) && isEqual(exponents.get(j), -1)
									&& closeToAnInteger(bases.get(i)) && closeToAnInteger(bases.get(j))) {
								long gcd = gcd(bases.get(i), bases.get(j));
								if (gcd > 1) {
									bases.get(i).setColor(colorTracker[0]);
									bases.get(j).setColor(colorTracker[0]);

									bases.set(i, new StepConstant(bases.get(i).getValue() / gcd));
									bases.set(j, new StepConstant(bases.get(j).getValue() / gcd));

									bases.get(i).setColor(colorTracker[0]);
									bases.get(j).setColor(colorTracker[0]);

									StepExpression toCancel = new StepConstant(gcd);
									toCancel.setColor(colorTracker[0]++);
									sb.add(SolutionStepType.CANCEL_FRACTION, toCancel);

									break;
								}

								if (isEqual(bases.get(i).getValue() % bases.get(j).getValue(), 0)) {
									bases.set(i, new StepConstant(bases.get(i).getValue() / bases.get(j).getValue()));
									bases.set(j, new StepConstant(1));
								}
							}

							if (isEqual(exponents.get(i), 1) && isEqual(exponents.get(j), -1)) {
								StepExpression numByDenom = StepExpression.tryToDivide(bases.get(i), bases.get(j));

								if (numByDenom != null && !bases.get(j).isConstant()) {
									bases.get(i).setColor(colorTracker[0]);
									bases.get(j).setColor(colorTracker[0]);
									numByDenom.setColor(colorTracker[0]);

									sb.add(SolutionStepType.POLYNOMIAL_DIVISION, bases.get(i), bases.get(j),
											numByDenom);

									bases.set(i, numByDenom);
									bases.set(j, new StepConstant(1));
									colorTracker[0]++;

									continue;
								}

								StepExpression denomByNum = StepExpression.tryToDivide(bases.get(j), bases.get(i));

								if (denomByNum != null && !bases.get(i).isConstant()) {
									bases.get(i).setColor(colorTracker[0]);
									bases.get(j).setColor(colorTracker[0]);
									denomByNum.setColor(colorTracker[0]);

									sb.add(SolutionStepType.POLYNOMIAL_DIVISION, bases.get(j), bases.get(i),
											denomByNum);

									bases.set(i, new StepConstant(1));
									bases.set(j, denomByNum);
									colorTracker[0]++;
								}
							}
						}
					}

					StepExpression newFraction = null;
					for (int i = 0; i < bases.size(); i++) {
						if (!isEqual(exponents.get(i), 0) && !isEqual(bases.get(i), 1)) {
							newFraction = StepExpression.makeFraction(newFraction, bases.get(i), exponents.get(i));
						}
					}

					return newFraction == null ? new StepConstant(1) : newFraction;
				}
			}

			return StepStrategies.iterateThrough(this, sn, sb, colorTracker);
		}
	},

	COMMON_FRACTION {
		@Override
		public StepNode apply(StepNode sn, SolutionBuilder sb, int[] colorTracker) {
			if (sn.isOperation(Operation.MULTIPLY)) {
				StepOperation so = (StepOperation) sn;

				List<StepExpression> bases = new ArrayList<StepExpression>();
				List<StepExpression> exponents = new ArrayList<StepExpression>();

				StepExpression.getBasesAndExponents(so, null, bases, exponents);

				StepExpression newFraction = null;

				for (int i = 0; i < bases.size(); i++) {
					newFraction = StepExpression.makeFraction(newFraction, bases.get(i), exponents.get(i));
				}

				if (newFraction.isOperation(Operation.DIVIDE)) {
					so.setColor(colorTracker[0]);
					newFraction.setColor(colorTracker[0]);
					sb.add(SolutionStepType.COMMON_FRACTION, colorTracker[0]++);

					return newFraction;
				}
			}

			return StepStrategies.iterateThrough(this, sn, sb, colorTracker);
		}

	},

	REGROUP_PRODUCTS {
		@Override
		public StepNode apply(StepNode sn, SolutionBuilder sb, int[] colorTracker) {
			if (sn.isOperation(Operation.MULTIPLY)) {
				int colorsAtStart = colorTracker[0];

				StepOperation so = (StepOperation) sn;

				List<StepExpression> bases = new ArrayList<StepExpression>();
				List<StepExpression> exponents = new ArrayList<StepExpression>();

				StepExpression.getBasesAndExponents(so, null, bases, exponents);

				List<StepExpression> constantList = new ArrayList<StepExpression>();
				double constantValue = 1;
				for (int i = 0; i < bases.size(); i++) {
					if (bases.get(i).nonSpecialConstant() && isEqual(exponents.get(i), 1)) {
						constantList.add(bases.get(i));
						constantValue *= bases.get(i).getValue();

						exponents.set(i, new StepConstant(0));
					}
				}

				if (isEqual(constantValue, 0)) {
					so.setColor(colorTracker[0]);
					StepExpression result = new StepConstant(0);
					result.setColor(colorTracker[0]);

					sb.add(SolutionStepType.MULTIPLIED_BY_ZERO, colorTracker[0]++);
					return result;
				}

				for (int i = 0; i < bases.size(); i++) {
					if (!isEqual(exponents.get(i), 0)) {
						boolean foundCommon = false;
						for (int j = i + 1; j < bases.size(); j++) {
							if (!isEqual(exponents.get(j), 0) && bases.get(i).equals(bases.get(j))) {
								foundCommon = true;
								bases.get(j).setColor(colorTracker[0]);

								exponents.set(i, add(exponents.get(i), exponents.get(j)));
								exponents.set(j, new StepConstant(0));
							}
						}
						if (foundCommon) {
							bases.get(i).setColor(colorTracker[0]);
							exponents.get(i).setColor(colorTracker[0]++);

							sb.add(SolutionStepType.REGROUP_PRODUCTS, bases.get(i));
						}
					}
				}

				StepExpression newProduct = null;

				for (int i = 0; i < bases.size(); i++) {
					if (!isEqual(exponents.get(i), 0) && !isEqual(bases.get(i), 1)) {
						newProduct = StepExpression.makeFraction(newProduct, bases.get(i), exponents.get(i));
					}
				}

				StepExpression newConstant;
				newConstant = new StepConstant(Math.abs(constantValue));
				if (constantList.size() > 1) {
					for (int i = 0; i < constantList.size(); i++) {
						constantList.get(i).setColor(colorTracker[0]);
					}
					sb.add(SolutionStepType.MULTIPLY_CONSTANTS, colorTracker[0]);
					if (newProduct == null) {
						newConstant = new StepConstant(constantValue);
						newConstant.setColor(colorTracker[0]++);
						return newConstant;
					}
					newConstant.setColor(colorTracker[0]++);
				}

				newProduct = StepExpression.makeFraction(newConstant, newProduct, new StepConstant(1));

				if (colorTracker[0] > colorsAtStart) {
					if (constantValue < 0) {
						return minus(newProduct);
					}
					return newProduct;
				}
			}

			return StepStrategies.iterateThrough(this, sn, sb, colorTracker);
		}
	},

	DISTRIBUTE_POWER_OVER_PRODUCT {
		@Override
		public StepNode apply(StepNode sn, SolutionBuilder sb, int[] colorTracker) {
			if (sn.isOperation(Operation.POWER)) {
				StepOperation so = (StepOperation) sn;

				if (so.getSubTree(0).isOperation(Operation.MULTIPLY)) {
					StepOperation result = new StepOperation(Operation.MULTIPLY);

					so.getSubTree(1).setColor(colorTracker[0]++);
					for (int i = 0; i < ((StepOperation) so.getSubTree(0)).noOfOperands(); i++) {
						((StepOperation) so.getSubTree(0)).getSubTree(i).setColor(colorTracker[0]++);
						result.addSubTree(power(((StepOperation) so.getSubTree(0)).getSubTree(i), so.getSubTree(1)));
					}

					sb.add(SolutionStepType.DISTRIBUTE_POWER_OVER_PRODUCT);

					return result;
				}
			}

			return StepStrategies.iterateThrough(this, sn, sb, colorTracker);
		}
	},

	POWER_OF_NEGATIVE {
		@Override
		public StepNode apply(StepNode sn, SolutionBuilder sb, int[] colorTracker) {
			if (sn.isOperation(Operation.POWER)) {
				StepOperation so = (StepOperation) sn;

				if (so.getSubTree(0).isNegative()) {
					if (isEven(so.getSubTree(1))) {
						StepExpression result = power(so.getSubTree(0).negate(), so.getSubTree(1));

						so.setColor(colorTracker[0]);
						result.setColor(colorTracker[0]);
						sb.add(SolutionStepType.EVEN_POWER_NEGATIVE, colorTracker[0]++);

						return result;
					} else if (isOdd(so.getSubTree(1))) {
						StepExpression result = power(so.getSubTree(0).negate(), so.getSubTree(1)).negate();

						so.setColor(colorTracker[0]);
						result.setColor(colorTracker[0]);
						sb.add(SolutionStepType.ODD_POWER_NEGATIVE, colorTracker[0]++);

						return result;
					}
				}
			}

			return StepStrategies.iterateThrough(this, sn, sb, colorTracker);
		}
	},

	SIMPLIFY_POWERS_AND_ROOTS {
		@Override
		public StepNode apply(StepNode sn, SolutionBuilder sb, int[] colorTracker) {
			if (sn instanceof StepOperation) {
				StepOperation so = (StepOperation) sn;

				if ((so.isOperation(Operation.POWER) && so.getSubTree(0).isOperation(Operation.NROOT))
						|| (so.isOperation(Operation.NROOT) && so.getSubTree(0).isOperation(Operation.POWER))) {
					StepExpression exponent1 = so.getSubTree(1);
					StepExpression exponent2 = ((StepOperation) so.getSubTree(0)).getSubTree(1);

					if (closeToAnInteger(exponent1) && closeToAnInteger(exponent2)) {
						long gcd = gcd(Math.round(exponent1.getValue()), Math.round(exponent2.getValue()));

						if (gcd > 1) {
							exponent1 = isEqual(exponent1, gcd) ? null : new StepConstant(exponent1.getValue() / gcd);
							exponent2 = isEqual(exponent2, gcd) ? null : new StepConstant(exponent2.getValue() / gcd);

							StepConstant gcdConstant = new StepConstant(gcd);
							gcdConstant.setColor(colorTracker[0]);

							StepExpression argument = ((StepOperation) so.getSubTree(0)).getSubTree(0);

							StepExpression result;
							if (so.isOperation(Operation.NROOT) && so.getSubTree(0).isOperation(Operation.POWER)) {
								if (isEven(gcd) && (exponent2 == null || !isEven(exponent2.getValue()))) {
									if (argument.nonSpecialConstant()) {
										result = root(power(new StepConstant(Math.abs(argument.getValue())), exponent2),
												exponent1);
									} else {
										result = root(power(abs(argument), exponent2), exponent1);
									}
									sb.add(SolutionStepType.REDUCE_ROOT_AND_POWER_EVEN, gcdConstant);
								} else {
									result = root(power(argument, exponent2), exponent1);
									sb.add(SolutionStepType.REDUCE_ROOT_AND_POWER, gcdConstant);
								}
							} else {
								result = power(root(argument, exponent2), exponent1);
								sb.add(SolutionStepType.REDUCE_ROOT_AND_POWER, gcdConstant);
							}

							so.setColor(colorTracker[0]);
							result.setColor(colorTracker[0]++);
							return result;
						}
					}
				}
			}

			return StepStrategies.iterateThrough(this, sn, sb, colorTracker);
		}
	},

	SIMPLE_POWERS {
		@Override
		public StepNode apply(StepNode sn, SolutionBuilder sb, int[] colorTracker) {
			if (sn.isOperation(Operation.POWER)) {
				StepOperation so = (StepOperation) sn;

				if (so.getSubTree(0).isOperation(Operation.POWER)) {
					StepExpression result = power(((StepOperation) so.getSubTree(0)).getSubTree(0),
							so.getSubTree(1).getValue() * ((StepOperation) so.getSubTree(0)).getSubTree(1).getValue());

					so.setColor(colorTracker[0]);
					result.setColor(colorTracker[0]);
					sb.add(SolutionStepType.POWER_OF_POWER, colorTracker[0]++);

					return result;
				}

				if (closeToAnInteger(so.getSubTree(0)) && closeToAnInteger(so.getSubTree(1))) {
					StepExpression result = new StepConstant(
							Math.pow(so.getSubTree(0).getValue(), so.getSubTree(1).getValue()));

					so.setColor(colorTracker[0]);
					result.setColor(colorTracker[0]);
					sb.add(SolutionStepType.EVALUATE_POWER, colorTracker[0]++);

					return result;
				}
				if (isEqual(so.getSubTree(1), 0)) {
					StepExpression result = new StepConstant(1);

					so.setColor(colorTracker[0]);
					result.setColor(colorTracker[0]);
					sb.add(SolutionStepType.ZEROTH_POWER, colorTracker[0]++);

					return result;
				}
				if (isEqual(so.getSubTree(1), 1)) {
					StepExpression result = so.getSubTree(0);

					so.setColor(colorTracker[0]);
					result.setColor(colorTracker[0]);
					sb.add(SolutionStepType.FIRST_POWER, colorTracker[0]++);

					return result;
				}
			}

			return StepStrategies.iterateThrough(this, sn, sb, colorTracker);
		}
	},

	SIMPLE_ROOTS {
		@Override
		public StepNode apply(StepNode sn, SolutionBuilder sb, int[] colorTracker) {
			if (sn.isOperation(Operation.NROOT)) {
				StepOperation so = (StepOperation) sn;

				if (so.getSubTree(0).isOperation(Operation.NROOT)) {
					StepExpression result = root(((StepOperation) so.getSubTree(0)).getSubTree(0),
							so.getSubTree(1).getValue() * ((StepOperation) so.getSubTree(0)).getSubTree(1).getValue());

					so.setColor(colorTracker[0]);
					result.setColor(colorTracker[0]);
					sb.add(SolutionStepType.ROOT_OF_ROOT, colorTracker[0]++);

					return result;
				}

				if (isEqual(so.getSubTree(0), 1)) {
					StepExpression result = new StepConstant(1);

					so.setColor(colorTracker[0]);
					result.setColor(colorTracker[0]);
					sb.add(SolutionStepType.ROOT_OF_ONE, colorTracker[0]++);

					return result;
				}

				if (isEqual(so.getSubTree(1), 1)) {
					StepExpression result = so.getSubTree(0);

					so.setColor(colorTracker[0]);
					result.setColor(colorTracker[0]);
					sb.add(SolutionStepType.FIRST_ROOT, colorTracker[0]++);

					return result;
				}

				if (isOdd(so.getSubTree(1).getValue()) && so.getSubTree(0).isNegative()) {
					StepExpression result = minus(root(so.getSubTree(0).negate(), so.getSubTree(1)));

					so.setColor(colorTracker[0]);
					result.setColor(colorTracker[0]);
					sb.add(SolutionStepType.ODD_ROOT_OF_NEGATIVE, colorTracker[0]++);

					return result;
				}
			}

			return StepStrategies.iterateThrough(this, sn, sb, colorTracker);
		}
	},

	CALCULATE_INVERSE_TRIGO {
		@Override
		public StepNode apply(StepNode sn, SolutionBuilder sb, int[] colorTracker) {
			if (sn instanceof StepOperation && ((StepOperation) sn).isInverseTrigonometric()) {
				StepOperation so = (StepOperation) sn;

				StepExpression value = StepExpression.inverseTrigoLookup(so);
				if (value != null) {
					so.setColor(colorTracker[0]);
					value.setColor(colorTracker[0]);
					sb.add(SolutionStepType.EVALUATE_INVERSE_TRIGO, colorTracker[0]++);
					return value;
				}
			}

			return StepStrategies.iterateThrough(this, sn, sb, colorTracker);
		}
	},

	ADD_FRACTIONS {
		@Override
		public StepNode apply(StepNode sn, SolutionBuilder sb, int[] colorTracker) {
			RegroupSteps[] fractionAddition = new RegroupSteps[] { EXPAND_FRACTIONS, ADD_NUMERATORS, REGROUP_PRODUCTS,
					REGROUP_SUMS, SIMPLIFY_FRACTIONS };

			SolutionBuilder tempSteps = new SolutionBuilder();
			int[] tempTracker = new int[] { 1 };
			StepNode tempTree = sn.deepCopy();

			tempTree = EXPAND_FRACTIONS.apply(tempTree, tempSteps, tempTracker);
			ADD_NUMERATORS.apply(tempTree, tempSteps, tempTracker);

			if (tempTracker[0] > 1) {
				SolutionBuilder additionSteps = new SolutionBuilder();

				StepNode newSn = StepStrategies.implementStrategy(sn, additionSteps, fractionAddition);

				if (sb != null) {
					sb.add(SolutionStepType.ADD_FRACTIONS);
					sb.levelDown();
					sb.addAll(additionSteps.getSteps());
					sb.levelUp();
				}

				colorTracker[0]++;

				return newSn;
			}

			return sn;
		}
	},

	RATIONALIZE_DENOMINATORS {
		@Override
		public StepNode apply(StepNode sn, SolutionBuilder sb, int[] colorTracker) {
			SimplificationStepGenerator[] denominatorRationalization = new SimplificationStepGenerator[] {
					RATIONALIZE_DENOMINATOR, SQUARE_ROOT_MULTIPLIED_BY_ITSELF, EXPAND_ROOT, COMMON_ROOT, SIMPLE_POWERS,
					SIMPLE_ROOTS, REGROUP_PRODUCTS, REGROUP_SUMS, ExpandSteps.EXPAND_DENOMINATORS, FACTOR_SQUARE,
					SIMPLIFY_POWERS_AND_ROOTS, SIMPLIFY_FRACTIONS };

			SolutionBuilder tempSteps = new SolutionBuilder();
			int[] tempTracker = new int[] { 1 };
			StepNode tempTree = sn.deepCopy();

			RATIONALIZE_DENOMINATOR.apply(tempTree, tempSteps, tempTracker);

			if (tempTracker[0] > 1) {
				SolutionBuilder rationalizationSteps = new SolutionBuilder();

				StepNode newSn = StepStrategies.implementStrategy(sn, rationalizationSteps, denominatorRationalization);

				if (sb != null) {
					sb.add(SolutionStepType.RATIONALIZE_DENOMINATOR);
					sb.levelDown();
					sb.addAll(rationalizationSteps.getSteps());
					sb.levelUp();
				}

				colorTracker[0]++;
				return newSn;
			}

			return sn;
		}
	};
}
