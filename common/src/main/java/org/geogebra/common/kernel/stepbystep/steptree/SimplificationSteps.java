package org.geogebra.common.kernel.stepbystep.steptree;

import static org.geogebra.common.kernel.stepbystep.steptree.StepNode.abs;
import static org.geogebra.common.kernel.stepbystep.steptree.StepNode.add;
import static org.geogebra.common.kernel.stepbystep.steptree.StepNode.closeToAnInteger;
import static org.geogebra.common.kernel.stepbystep.steptree.StepNode.divide;
import static org.geogebra.common.kernel.stepbystep.steptree.StepNode.gcd;
import static org.geogebra.common.kernel.stepbystep.steptree.StepNode.isEqual;
import static org.geogebra.common.kernel.stepbystep.steptree.StepNode.isEven;
import static org.geogebra.common.kernel.stepbystep.steptree.StepNode.isNegative;
import static org.geogebra.common.kernel.stepbystep.steptree.StepNode.isOdd;
import static org.geogebra.common.kernel.stepbystep.steptree.StepNode.lcm;
import static org.geogebra.common.kernel.stepbystep.steptree.StepNode.minus;
import static org.geogebra.common.kernel.stepbystep.steptree.StepNode.multiply;
import static org.geogebra.common.kernel.stepbystep.steptree.StepNode.negate;
import static org.geogebra.common.kernel.stepbystep.steptree.StepNode.nonTrivialPower;
import static org.geogebra.common.kernel.stepbystep.steptree.StepNode.nonTrivialProduct;
import static org.geogebra.common.kernel.stepbystep.steptree.StepNode.power;
import static org.geogebra.common.kernel.stepbystep.steptree.StepNode.root;
import static org.geogebra.common.kernel.stepbystep.steptree.StepNode.subtract;

import java.util.ArrayList;
import java.util.List;

import org.geogebra.common.kernel.stepbystep.StepHelper;
import org.geogebra.common.kernel.stepbystep.solution.SolutionBuilder;
import org.geogebra.common.kernel.stepbystep.solution.SolutionStepType;
import org.geogebra.common.plugin.Operation;
import org.geogebra.common.util.debug.Log;

public enum SimplificationSteps {

	EXPAND_PRODUCTS {
		@Override
		public StepNode apply(StepNode sn, SolutionBuilder sb, int[] colorTracker) {
			return expandProducts(sn, sb, colorTracker, true);
		}
	},

	EXPAND_DENOMINATORS {
		@Override
		public StepNode apply(StepNode sn, SolutionBuilder sb, int[] colorTracker) {
			return expandProducts(sn, sb, colorTracker, false);
		}
	},

	EXPAND_POWERS {
		@Override
		public StepNode apply(StepNode sn, SolutionBuilder sb, int[] colorTracker) {
			if (sn.isOperation() && !sn.isOperation(Operation.ABS)) {
				StepOperation so = (StepOperation) sn;

				if (so.isOperation(Operation.POWER) && so.getSubTree(0).isOperation(Operation.PLUS)
						&& so.getSubTree(1).getValue() > 0 && closeToAnInteger(so.getSubTree(1))) {
					StepOperation sum = (StepOperation) so.getSubTree(0);

					if (so.getSubTree(1).getValue() + sum.noOfOperands() < 6) {
						return expandUsingFormula(so, sb, colorTracker);
					}

					StepOperation asMultiplication = new StepOperation(Operation.MULTIPLY);
					for (int i = 0; i < Math.round(so.getSubTree(1).getValue()); i++) {
						asMultiplication.addSubTree(sum.deepCopy());
					}
					return asMultiplication;
				}

				return iterateThrough(this, so, sb, colorTracker);
			}

			return sn;
		}

		private StepNode expandUsingFormula(StepOperation so, SolutionBuilder sb, int[] colorTracker) {
			StepOperation sum = (StepOperation) so.getSubTree(0);

			for (int i = 0; i < sum.noOfOperands(); i++) {
				sum.getSubTree(i).setColor(colorTracker[0]++);
			}

			StepOperation newSum = new StepOperation(Operation.PLUS);

			if (isEqual(so.getSubTree(1), 2) && sum.noOfOperands() == 2 && isNegative(sum.getSubTree(1))) {
				newSum.addSubTree(power(sum.getSubTree(0), 2));
				newSum.addSubTree(multiply(-2, multiply(sum.getSubTree(0), negate(sum.getSubTree(1)))));
				newSum.addSubTree(power(negate(sum.getSubTree(1)), 2));

				sb.add(SolutionStepType.BINOM_SQUARED_DIFF);
			} else if (isEqual(so.getSubTree(1), 2) && sum.noOfOperands() == 2) {
				newSum.addSubTree(power(sum.getSubTree(0), 2));
				newSum.addSubTree(multiply(2, multiply(sum.getSubTree(0), sum.getSubTree(1))));
				newSum.addSubTree(power(sum.getSubTree(1), 2));

				sb.add(SolutionStepType.BINOM_SQUARED_SUM);
			} else if (isEqual(so.getSubTree(1), 2) && sum.noOfOperands() == 3) {
				newSum.addSubTree(power(sum.getSubTree(0), 2));
				newSum.addSubTree(power(sum.getSubTree(1), 2));
				newSum.addSubTree(power(sum.getSubTree(2), 2));
				newSum.addSubTree(multiply(2, multiply(sum.getSubTree(0), sum.getSubTree(1))));
				newSum.addSubTree(multiply(2, multiply(sum.getSubTree(1), sum.getSubTree(2))));
				newSum.addSubTree(multiply(2, multiply(sum.getSubTree(0), sum.getSubTree(2))));

				sb.add(SolutionStepType.TRINOM_SQUARED);
			} else if (isEqual(so.getSubTree(1), 3) && sum.noOfOperands() == 2) {
				newSum.addSubTree(power(sum.getSubTree(0), 3));
				newSum.addSubTree(multiply(3, multiply(power(sum.getSubTree(0), 2), sum.getSubTree(1))));
				newSum.addSubTree(multiply(3, multiply(sum.getSubTree(0), power(sum.getSubTree(1), 2))));
				newSum.addSubTree(power(sum.getSubTree(1), 3));

				sb.add(SolutionStepType.BINOM_CUBED);
			}

			return newSum;
		}
	},

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
						if (so.getSubTree(i).equals(negate(so.getSubTree(j)))) {
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

			return iterateThrough(this, sn, sb, colorTracker);
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
								StepNode argument = ((StepOperation) so.getSubTree(i)).getSubTree(0);

								StepNode result = root(power(argument, commonRoot / currentRoot), commonRoot);

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

			return iterateThrough(this, sn, sb, colorTracker);
		}
	},

	COMMON_ROOT {
		@Override
		public StepNode apply(StepNode sn, SolutionBuilder sb, int[] colorTracker) {
			if (sn.isOperation(Operation.MULTIPLY)) {
				StepOperation so = (StepOperation) sn;

				StepOperation newProduct = new StepOperation(Operation.MULTIPLY);
				StepOperation underRoot = new StepOperation(Operation.MULTIPLY);

				int rootCount = 0;
				double commonRoot = 0;
				for (int i = 0; i < so.noOfOperands(); i++) {
					if (so.getSubTree(i).isOperation(Operation.NROOT)) {
						double currentRoot = ((StepOperation) so.getSubTree(i)).getSubTree(1).getValue();
						if (isEqual(commonRoot, 0) || isEqual(commonRoot, currentRoot)) {
							commonRoot = currentRoot;
							underRoot.addSubTree(((StepOperation) so.getSubTree(i)).getSubTree(0));
							rootCount++;
						} else {
							newProduct.addSubTree(so.getSubTree(i));
						}
					} else {
						newProduct.addSubTree(so.getSubTree(i));
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

					StepNode result = root(underRoot, commonRoot);
					result.setColor(colorTracker[0]);

					sb.add(SolutionStepType.PRODUCT_OF_ROOTS, colorTracker[0]++);

					newProduct.addSubTree(result);

					return newProduct;
				}
			}

			return iterateThrough(this, sn, sb, colorTracker);
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
								StepNode result = ((StepOperation) so.getSubTree(i)).getSubTree(0).deepCopy();

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

			return iterateThrough(this, sn, sb, colorTracker);
		}
	},

	DOUBLE_MINUS {
		@Override
		public StepNode apply(StepNode sn, SolutionBuilder sb, int[] colorTracker) {
			if (sn.isOperation(Operation.MINUS)) {
				StepOperation so = (StepOperation) sn;

				if (so.getSubTree(0).isOperation(Operation.MINUS)) {
					StepNode result = ((StepOperation) so.getSubTree(0)).getSubTree(0);
					so.setColor(colorTracker[0]);
					result.setColor(colorTracker[0]);
					sb.add(SolutionStepType.DOUBLE_MINUS, colorTracker[0]++);

					return result;
				}
				if (so.getSubTree(0).nonSpecialConstant() && so.getSubTree(0).getValue() < 0) {
					StepNode result = new StepConstant(so.getValue());
					so.setColor(colorTracker[0]);
					result.setColor(colorTracker[0]);
					sb.add(SolutionStepType.DOUBLE_MINUS, colorTracker[0]++);

					return result;
				}
			}

			return iterateThrough(this, sn, sb, colorTracker);
		}
	},

	DISTRIBUTE_ROOT_OVER_FRACTION {
		@Override
		public StepNode apply(StepNode sn, SolutionBuilder sb, int[] colorTracker) {
			if (sn.isOperation(Operation.NROOT)) {
				StepOperation so = (StepOperation) sn;

				if (so.getSubTree(0).isOperation(Operation.DIVIDE)) {
					StepNode numerator = root(((StepOperation) so.getSubTree(0)).getSubTree(0), so.getSubTree(1));
					StepNode denominator = root(((StepOperation) so.getSubTree(0)).getSubTree(1), so.getSubTree(1));

					StepNode result = divide(numerator, denominator);
					so.setColor(colorTracker[0]);
					result.setColor(colorTracker[0]);
					sb.add(SolutionStepType.DISTRIBUTE_ROOT_FRAC, colorTracker[0]++);

					return result;
				}
			}

			return iterateThrough(this, sn, sb, colorTracker);
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
						StepNode toMultiply = root(
								nonTrivialPower(((StepOperation) so.getSubTree(1)).getSubTree(0), root - 1), root);

						toMultiply.setColor(colorTracker[0]++);

						StepNode numerator = nonTrivialProduct(so.getSubTree(0), toMultiply);
						StepNode denominator = multiply(so.getSubTree(1), toMultiply);

						StepNode result = divide(numerator, denominator);
						sb.add(SolutionStepType.MULTIPLY_NUM_DENOM, toMultiply);

						return result;
					}
				}

				if (so.getSubTree(1).isOperation(Operation.PLUS)) {
					StepOperation sum = (StepOperation) so.getSubTree(1);

					if (sum.noOfOperands() == 2
							&& (sum.getSubTree(0).containsSquareRoot() || sum.getSubTree(1).containsSquareRoot())) {
						StepNode toMultiply = add(sum.getSubTree(0), negate(sum.getSubTree(1)));

						toMultiply.setColor(colorTracker[0]++);

						StepNode numerator = nonTrivialProduct(so.getSubTree(0), toMultiply);
						StepNode denominator = multiply(so.getSubTree(1), toMultiply);

						StepNode result = divide(numerator, denominator);
						sb.add(SolutionStepType.MULTIPLY_NUM_DENOM, toMultiply);

						return result;
					}
				}

				if (so.getSubTree(1).isOperation(Operation.MULTIPLY)) {
					StepOperation product = (StepOperation) so.getSubTree(1);

					StepNode irrational = null;

					for (int i = 0; irrational == null && i < product.noOfOperands(); i++) {
						if (product.getSubTree(i).isSquareRoot()) {
							irrational = product.getSubTree(i).deepCopy();
						}
					}

					if (irrational != null) {
						irrational.setColor(colorTracker[0]++);

						StepNode numerator = nonTrivialProduct(so.getSubTree(0), irrational);
						StepNode denominator = multiply(so.getSubTree(1), irrational);

						StepNode result = divide(numerator, denominator);
						sb.add(SolutionStepType.MULTIPLY_NUM_DENOM, irrational);

						return result;
					}
				}
			}

			return iterateThrough(this, sn, sb, colorTracker);
		}
	},

	DISTRIBUTE_MINUS {
		@Override
		public StepNode apply(StepNode sn, SolutionBuilder sb, int[] colorTracker) {
			if (sn.isOperation()) {
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
							result.addSubTree(negate(((StepOperation) so.getSubTree(0)).getSubTree(i)));
						}

						so.setColor(colorTracker[0]);
						result.setColor(colorTracker[0]);
						sb.add(SolutionStepType.DISTRIBUTE_MINUS, colorTracker[0]++);

						return result;
					}
				}
			}

			return iterateThrough(this, sn, sb, colorTracker);
		}
	},

	FACTOR_SQUARE {
		@Override
		public StepNode apply(StepNode sn, SolutionBuilder sb, int[] colorTracker) {
			if (sn.isOperation(Operation.NROOT)) {
				StepOperation so = (StepOperation) sn;

				StepNode coefficient = so.getSubTree(0).getIntegerCoefficient();
				StepNode remainder = so.getSubTree(0).getNonInteger();

				if (closeToAnInteger(coefficient)) {
					long root = Math.round(so.getSubTree(1).getValue());

					long power = StepNode.getIntegerPower(Math.round(coefficient.getValue()));
					long gcd = gcd(root, power);

					if (gcd > 1) {
						StepNode newValue = power(
								new StepConstant(Math.pow(so.getSubTree(0).getValue(), ((double) 1) / gcd)), gcd);

						so.getSubTree(0).setColor(colorTracker[0]);
						newValue.setColor(colorTracker[0]++);

						StepNode result = root(newValue, root);
						sb.add(SolutionStepType.REWRITE_AS, so.getSubTree(0), newValue);

						return result;
					}

					long newCoefficient = StepNode.largestNthPower(coefficient.getValue(), so.getSubTree(1).getValue());

					if (!isEqual(newCoefficient, 1)) {
						StepNode result = multiply(newCoefficient,
								root(multiply(coefficient.getValue() / Math.pow(newCoefficient, root), remainder),
										so.getSubTree(1)));

						so.setColor(colorTracker[0]);
						result.setColor(colorTracker[0]);

						sb.add(SolutionStepType.FACTOR_SQUARE, colorTracker[0]++);

						return result;
					}
				}
			}

			return iterateThrough(this, sn, sb, colorTracker);
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
			if (sn.isOperation()) {
				StepOperation so = new StepOperation(((StepOperation) sn).getOperation());

				int colorsAtStart = colorTracker[0];
				for (int i = 0; i < ((StepOperation) sn).noOfOperands(); i++) {
					so.addSubTree(apply(((StepOperation) sn).getSubTree(i), sb, colorTracker, integer));
				}

				if (colorsAtStart < colorTracker[0]) {
					return so;
				}

				so = (StepOperation) sn;

				if (so.isOperation(Operation.PLUS)) {
					colorsAtStart = colorTracker[0];

					StepNode[] coefficients = new StepNode[so.noOfOperands()];
					StepNode[] variables = new StepNode[so.noOfOperands()];
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

					List<StepNode> constantList = new ArrayList<StepNode>();
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

					StepNode newConstants = new StepConstant(constantSum);
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
					long currentDenominator = StepNode.getDenominator(so.getSubTree(i));
					if (currentDenominator != 0) {
						newDenominator = lcm(newDenominator, currentDenominator);
					}
				}

				if (newDenominator != 1) {
					boolean wasChanged = false;

					for (int i = 0; i < so.noOfOperands(); i++) {
						long currentDenominator = StepNode.getDenominator(so.getSubTree(i));
						if (currentDenominator != 0 && currentDenominator != newDenominator) {
							wasChanged = true;

							StepNode newFraction = divide(
									nonTrivialProduct(new StepConstant(((double) newDenominator) / currentDenominator),
											StepNode.getNumerator(so.getSubTree(i))),
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

			return iterateThrough(this, sn, sb, colorTracker);
		}
	},

	ADD_NUMERATORS {
		@Override
		public StepNode apply(StepNode sn, SolutionBuilder sb, int[] colorTracker) {
			if (sn.isOperation(Operation.PLUS)) {
				StepOperation so = (StepOperation) sn;

				StepNode remainder = null;
				StepNode newNumerator = null;
				long newDenominator = 0;

				List<StepNode> fractions = new ArrayList<StepNode>();
				for (int i = 0; i < so.noOfOperands(); i++) {
					StepNode currentNumerator = StepNode.getNumerator(so.getSubTree(i));
					long currentDenominator = StepNode.getDenominator(so.getSubTree(i));

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

					StepNode result = divide(newNumerator, newDenominator);
					result.setColor(colorTracker[0]);
					sb.add(SolutionStepType.ADD_NUMERATORS, colorTracker[0]++);
					return add(remainder, result);
				}
			}

			return iterateThrough(this, sn, sb, colorTracker);
		}
	},

	SIMPLIFY_FRACTIONS {
		@Override
		public StepNode apply(StepNode sn, SolutionBuilder sb, int[] colorTracker) {
			if (sn.isOperation()) {
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
					List<StepNode> bases = new ArrayList<StepNode>();
					List<StepNode> exponents = new ArrayList<StepNode>();

					StepNode.getBasesAndExponents(so, null, bases, exponents);

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

								StepNode toCancel = nonTrivialPower(bases.get(i), min);
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

									StepNode toCancel = new StepConstant(gcd);
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
								StepNode numByDenom = StepNode.tryToDivide(bases.get(i), bases.get(j));

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

								StepNode denomByNum = StepNode.tryToDivide(bases.get(j), bases.get(i));

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

					StepNode newFraction = null;
					for (int i = 0; i < bases.size(); i++) {
						if (!isEqual(exponents.get(i), 0) && !isEqual(bases.get(i), 1)) {
							newFraction = StepNode.makeFraction(newFraction, bases.get(i), exponents.get(i));
						}
					}

					return newFraction == null ? new StepConstant(1) : newFraction;
				}
			}

			return iterateThrough(this, sn, sb, colorTracker);
		}
	},

	COMMON_FRACTION {
		@Override
		public StepNode apply(StepNode sn, SolutionBuilder sb, int[] colorTracker) {
			if (sn.isOperation(Operation.MULTIPLY)) {
				StepOperation so = (StepOperation) sn;

				List<StepNode> bases = new ArrayList<StepNode>();
				List<StepNode> exponents = new ArrayList<StepNode>();

				StepNode.getBasesAndExponents(so, null, bases, exponents);

				StepNode newFraction = null;

				for (int i = 0; i < bases.size(); i++) {
					newFraction = StepNode.makeFraction(newFraction, bases.get(i), exponents.get(i));
				}

				if (newFraction.isOperation(Operation.DIVIDE)) {
					so.setColor(colorTracker[0]);
					newFraction.setColor(colorTracker[0]);
					sb.add(SolutionStepType.COMMON_FRACTION, colorTracker[0]++);

					return newFraction;
				}
			}

			return iterateThrough(this, sn, sb, colorTracker);
		}

	},

	REGROUP_PRODUCTS {
		@Override
		public StepNode apply(StepNode sn, SolutionBuilder sb, int[] colorTracker) {
			if (sn.isOperation()) {
				StepOperation so = new StepOperation(((StepOperation) sn).getOperation());

				int colorsAtStart = colorTracker[0];
				for (int i = 0; i < ((StepOperation) sn).noOfOperands(); i++) {
					so.addSubTree(apply(((StepOperation) sn).getSubTree(i), sb, colorTracker));
				}

				if (colorsAtStart < colorTracker[0]) {
					return so;
				}

				so = (StepOperation) sn;

				if (so.isOperation(Operation.MULTIPLY)) {
					List<StepNode> bases = new ArrayList<StepNode>();
					List<StepNode> exponents = new ArrayList<StepNode>();

					StepNode.getBasesAndExponents(so, null, bases, exponents);

					List<StepNode> constantList = new ArrayList<StepNode>();
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
						StepNode result = new StepConstant(0);
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

					StepNode newProduct = null;

					for (int i = 0; i < bases.size(); i++) {
						if (!isEqual(exponents.get(i), 0) && !isEqual(bases.get(i), 1)) {
							newProduct = StepNode.makeFraction(newProduct, bases.get(i), exponents.get(i));
						}
					}

					StepNode newConstant;
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

					newProduct = StepNode.makeFraction(newConstant, newProduct, new StepConstant(1));

					if (constantValue < 0) {
						return minus(newProduct);
					}
					return newProduct;
				}

				return so;
			}

			return sn;
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

			return iterateThrough(this, sn, sb, colorTracker);
		}
	},

	SQUARE_MINUSES {
		@Override
		public StepNode apply(StepNode sn, SolutionBuilder sb, int[] colorTracker) {
			if (sn.isOperation(Operation.POWER)) {
				StepOperation so = (StepOperation) sn;

				if (so.getSubTree(1).getValue() == 2) {
					if (so.getSubTree(0).isOperation(Operation.MINUS)) {
						StepNode result = power(((StepOperation) so.getSubTree(0)).getSubTree(0), 2);

						so.setColor(colorTracker[0]);
						result.setColor(colorTracker[0]);
						sb.add(SolutionStepType.SQUARE_MINUS, colorTracker[0]++);

						return result;
					}
				}
			}

			return iterateThrough(this, sn, sb, colorTracker);
		}
	},

	SIMPLIFY_POWERS_AND_ROOTS {
		@Override
		public StepNode apply(StepNode sn, SolutionBuilder sb, int[] colorTracker) {
			if (sn.isOperation()) {
				StepOperation so = (StepOperation) sn;

				if ((so.isOperation(Operation.POWER) && so.getSubTree(0).isOperation(Operation.NROOT))
						|| (so.isOperation(Operation.NROOT) && so.getSubTree(0).isOperation(Operation.POWER))) {
					StepNode exponent1 = so.getSubTree(1);
					StepNode exponent2 = ((StepOperation) so.getSubTree(0)).getSubTree(1);

					if (closeToAnInteger(exponent1) && closeToAnInteger(exponent2)) {
						long gcd = gcd(Math.round(exponent1.getValue()), Math.round(exponent2.getValue()));

						if (gcd > 1) {
							exponent1 = isEqual(exponent1, gcd) ? null : new StepConstant(exponent1.getValue() / gcd);
							exponent2 = isEqual(exponent2, gcd) ? null : new StepConstant(exponent2.getValue() / gcd);

							StepConstant gcdConstant = new StepConstant(gcd);
							gcdConstant.setColor(colorTracker[0]);

							StepNode argument = ((StepOperation) so.getSubTree(0)).getSubTree(0);

							StepNode result;
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

			return iterateThrough(this, sn, sb, colorTracker);
		}
	},

	SIMPLE_POWERS {
		@Override
		public StepNode apply(StepNode sn, SolutionBuilder sb, int[] colorTracker) {
			if (sn.isOperation(Operation.POWER)) {
				StepOperation so = (StepOperation) sn;

				if (so.getSubTree(0).isOperation(Operation.POWER)) {
					StepNode result = power(((StepOperation) so.getSubTree(0)).getSubTree(0),
							so.getSubTree(1).getValue() * ((StepOperation) so.getSubTree(0)).getSubTree(1).getValue());

					so.setColor(colorTracker[0]);
					result.setColor(colorTracker[0]);
					sb.add(SolutionStepType.POWER_OF_POWER, colorTracker[0]++);

					return result;
				}

				if (closeToAnInteger(so.getSubTree(0)) && closeToAnInteger(so.getSubTree(1))) {
					StepNode result = new StepConstant(
							Math.pow(so.getSubTree(0).getValue(), so.getSubTree(1).getValue()));

					so.setColor(colorTracker[0]);
					result.setColor(colorTracker[0]);
					sb.add(SolutionStepType.EVALUATE_POWER, colorTracker[0]++);

					return result;
				}
				if (isEqual(so.getSubTree(1), 0)) {
					StepNode result = new StepConstant(1);

					so.setColor(colorTracker[0]);
					result.setColor(colorTracker[0]);
					sb.add(SolutionStepType.ZEROTH_POWER, colorTracker[0]++);

					return result;
				}
				if (isEqual(so.getSubTree(1), 1)) {
					StepNode result = so.getSubTree(0);

					so.setColor(colorTracker[0]);
					result.setColor(colorTracker[0]);
					sb.add(SolutionStepType.FIRST_POWER, colorTracker[0]++);

					return result;
				}
			}

			return iterateThrough(this, sn, sb, colorTracker);
		}
	},

	SIMPLE_ROOTS {
		@Override
		public StepNode apply(StepNode sn, SolutionBuilder sb, int[] colorTracker) {
			if (sn.isOperation(Operation.NROOT)) {
				StepOperation so = (StepOperation) sn;

				if (so.getSubTree(0).isOperation(Operation.NROOT)) {
					StepNode result = root(((StepOperation) so.getSubTree(0)).getSubTree(0),
							so.getSubTree(1).getValue() * ((StepOperation) so.getSubTree(0)).getSubTree(1).getValue());

					so.setColor(colorTracker[0]);
					result.setColor(colorTracker[0]);
					sb.add(SolutionStepType.ROOT_OF_ROOT, colorTracker[0]++);

					return result;
				}

				if (isEqual(so.getSubTree(0), 1)) {
					StepNode result = new StepConstant(1);

					so.setColor(colorTracker[0]);
					result.setColor(colorTracker[0]);
					sb.add(SolutionStepType.ROOT_OF_ONE, colorTracker[0]++);

					return result;
				}

				if (isEqual(so.getSubTree(1), 1)) {
					StepNode result = so.getSubTree(0);

					so.setColor(colorTracker[0]);
					result.setColor(colorTracker[0]);
					sb.add(SolutionStepType.FIRST_ROOT, colorTracker[0]++);

					return result;
				}

				if (isOdd(so.getSubTree(1).getValue()) && isNegative(so.getSubTree(0))) {
					StepNode result = minus(root(negate(so.getSubTree(0)), so.getSubTree(1)));

					so.setColor(colorTracker[0]);
					result.setColor(colorTracker[0]);
					sb.add(SolutionStepType.ODD_ROOT_OF_NEGATIVE, colorTracker[0]++);

					return result;
				}
			}

			return iterateThrough(this, sn, sb, colorTracker);
		}
	},

	CALCULATE_INVERSE_TRIGO {
		@Override
		public StepNode apply(StepNode sn, SolutionBuilder sb, int[] colorTracker) {
			if (sn.isInverseTrigonometric()) {
				StepOperation so = (StepOperation) sn;

				StepNode value = StepNode.inverseTrigoLookup(so);
				if (value != null) {
					so.setColor(colorTracker[0]);
					value.setColor(colorTracker[0]);
					sb.add(SolutionStepType.EVALUATE_INVERSE_TRIGO, colorTracker[0]++);
					return value;
				}
			}

			return iterateThrough(this, sn, sb, colorTracker);
		}
	},

	ADD_FRACTIONS {
		@Override
		public StepNode apply(StepNode sn, SolutionBuilder sb, int[] colorTracker) {
			SimplificationSteps[] fractionAddition = new SimplificationSteps[] { EXPAND_FRACTIONS, ADD_NUMERATORS,
					REGROUP_PRODUCTS, REGROUP_SUMS, SIMPLIFY_FRACTIONS };

			SolutionBuilder tempSteps = new SolutionBuilder(sb.getLocalization());
			int[] tempTracker = new int[] { 1 };
			StepNode tempTree = sn.deepCopy();

			tempTree = EXPAND_FRACTIONS.apply(tempTree, tempSteps, tempTracker);
			ADD_NUMERATORS.apply(tempTree, tempSteps, tempTracker);

			if (tempTracker[0] > 1) {
				SolutionBuilder additionSteps = new SolutionBuilder(sb.getLocalization());

				StepNode newSn = implementStrategy(sn, additionSteps, fractionAddition);

				sb.add(SolutionStepType.ADD_FRACTIONS);
				sb.levelDown();
				sb.addAll(additionSteps.getSteps());
				sb.levelUp();
				colorTracker[0]++;

				return newSn;
			}

			return sn;
		}
	},

	RATIONALIZE_DENOMINATORS {
		@Override
		public StepNode apply(StepNode sn, SolutionBuilder sb, int[] colorTracker) {
			SimplificationSteps[] denominatorRationalization = new SimplificationSteps[] { RATIONALIZE_DENOMINATOR,
					SQUARE_ROOT_MULTIPLIED_BY_ITSELF, EXPAND_ROOT, COMMON_ROOT, SIMPLE_POWERS, SIMPLE_ROOTS,
					REGROUP_PRODUCTS, REGROUP_SUMS, EXPAND_DENOMINATORS, FACTOR_SQUARE, SIMPLIFY_POWERS_AND_ROOTS,
					SIMPLIFY_FRACTIONS };

			SolutionBuilder tempSteps = new SolutionBuilder(sb.getLocalization());
			int[] tempTracker = new int[] { 1 };
			StepNode tempTree = sn.deepCopy();

			RATIONALIZE_DENOMINATOR.apply(tempTree, tempSteps, tempTracker);

			if (tempTracker[0] > 1) {
				SolutionBuilder rationalizationSteps = new SolutionBuilder(sb.getLocalization());

				StepNode newSn = implementStrategy(sn, rationalizationSteps, denominatorRationalization);

				sb.add(SolutionStepType.RATIONALIZE_DENOMINATOR);
				sb.levelDown();
				sb.addAll(rationalizationSteps.getSteps());
				sb.levelUp();

				colorTracker[0]++;
				return newSn;
			}

			return sn;
		}
	},

	FACTOR_COMMON {
		@Override
		public StepNode apply(StepNode sn, SolutionBuilder sb, int[] colorTracker) {
			if (sn.isOperation(Operation.PLUS)) {
				StepOperation so = (StepOperation) sn;

				List<StepNode> commonBases = new ArrayList<StepNode>();
				List<StepNode> commonExponents = new ArrayList<StepNode>();

				StepNode.getBasesAndExponents(so.getSubTree(0), null, commonBases, commonExponents);

				List<List<StepNode>> currentBases = new ArrayList<List<StepNode>>();
				List<List<StepNode>> currentExponents = new ArrayList<List<StepNode>>();
				
				for (int i = 0; i < so.noOfOperands(); i++) {
					currentBases.add(new ArrayList<StepNode>());
					currentExponents.add(new ArrayList<StepNode>());

					StepNode.getBasesAndExponents(so.getSubTree(i), null, currentBases.get(i), currentExponents.get(i));

					boolean[] found = new boolean[commonBases.size()];

					for (int j = 0; j < commonBases.size(); j++) {
						for (int k = 0; k < currentBases.get(i).size(); k++) {
							if (currentBases.get(i).get(k).equals(commonBases.get(j))) {
								if (currentExponents.get(i).get(k).getValue() < commonExponents.get(j).getValue()) {
									commonExponents.set(j, currentExponents.get(i).get(k));
								}
								found[j] = true;
							}
						}
					}
					
					for(int j = 0; j < commonBases.size(); j++) {
						if(!found[j]) {
							commonExponents.set(j, new StepConstant(0));
						}
					}
				}

				StepOperation result = new StepOperation(Operation.PLUS);

				for (int i = 0; i < so.noOfOperands(); i++) {
					int tempTracker = colorTracker[0];
					for (int j = 0; j < commonBases.size(); j++) {
						for (int k = 0; k < currentBases.get(i).size(); k++) {
							if (!isEqual(commonExponents.get(j), 0)
									&& currentBases.get(i).get(k).equals(commonBases.get(j))) {
								StepNode differenceOfPowers = new StepConstant(
										currentExponents.get(i).get(k).getValue() - commonExponents.get(j).getValue());

								currentExponents.get(i).set(k, differenceOfPowers);
								currentBases.get(i).get(k).setColor(tempTracker++);
							}
						}
					}

					StepNode currentProduct = null;
					for (int j = 0; j < currentBases.get(i).size(); j++) {
						currentProduct = StepNode.makeFraction(currentProduct, currentBases.get(i).get(j),
								currentExponents.get(i).get(j));
					}

					result.addSubTree(currentProduct);
				}

				int colorsAtStart = colorTracker[0];
				StepNode common = null;
				for (int i = 0; i < commonBases.size(); i++) {
					if (!isEqual(commonExponents.get(i), 0)) {
						commonBases.get(i).setColor(colorTracker[0]++);
					}
					common = StepNode.makeFraction(common, commonBases.get(i), commonExponents.get(i));
				}

				if (isEqual(common, 1) || isEqual(common, -1)) {
					colorTracker[0] = colorsAtStart;
					return so;
				}

				sb.add(SolutionStepType.FACTOR_COMMON, common);
				return multiply(common, result);
			}

			return iterateThrough(this, sn, sb, colorTracker);
		}
	},

	FACTOR_INTEGER {
		@Override
		public StepNode apply(StepNode sn, SolutionBuilder sb, int[] colorTracker) {
			if (sn.isOperation(Operation.PLUS)) {
				StepOperation so = (StepOperation) sn;

				long common = 0;
				boolean allNegative = true;
				StepNode[] integerParts = new StepNode[so.noOfOperands() + 1];

				for (int i = 0; i < so.noOfOperands(); i++) {
					integerParts[i] = so.getSubTree(i).getIntegerCoefficient();

					if (integerParts[i] == null) {
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
					StepNode remainder = new StepConstant(integerParts[i].getValue() / common);
					integerParts[i].setColor(colorTracker[0]);
					remainder.setColor(colorTracker[0]++);

					factored.addSubTree(nonTrivialProduct(remainder, so.getSubTree(i).getNonInteger()));
				}

				if (isEqual(common, -1)) {
					sb.add(SolutionStepType.FACTOR_MINUS);
					return minus(factored);
				}

				integerParts[integerParts.length - 1] = new StepConstant(common);
				integerParts[integerParts.length - 1].setColor(colorTracker[0]++);

				sb.add(SolutionStepType.FACTOR_GCD, integerParts);
				return multiply(integerParts[integerParts.length - 1], factored);
			}

			return iterateThrough(this, sn, sb, colorTracker);
		}
	},

	COMPLETING_THE_SQUARE {
		@Override
		public StepNode apply(StepNode sn, SolutionBuilder sb, int[] colorTracker) {
			if (sn.isOperation(Operation.PLUS)) {
				StepOperation so = (StepOperation) sn;

				if (so.noOfOperands() == 3) {
					StepNode first = null, second = null, third = null;

					for (int i = 0; i < 3; i++) {
						if (so.getSubTree(i).isSquare() && !so.getSubTree(i).isConstant()) {
							first = so.getSubTree(i);
						} else if (so.getSubTree(i).nonSpecialConstant()) {
							third = so.getSubTree(i);
						} else {
							second = so.getSubTree(i);
						}
					}

					if (first == null || second == null || third == null) {
						return iterateThrough(this, sn, sb, colorTracker);
					}

					StepNode b = StepHelper.findCoefficient(second, first.getSquareRoot());

					if (b != null && isEven(b)) {
						double toComplete = third.getValue() - b.getValue() * b.getValue() / 4;

						if (toComplete < 0) {
							StepOperation newSum = new StepOperation(Operation.PLUS);
							newSum.addSubTree(first);
							newSum.addSubTree(second);

							StepNode asSum = add(new StepConstant(b.getValue() * b.getValue() / 4),
									new StepConstant(toComplete));
							third.setColor(colorTracker[0]);
							asSum.setColor(colorTracker[0]++);
							newSum.addSubTree(asSum);

							sb.add(SolutionStepType.REPLACE_WITH, third, asSum);
							return newSum;
						}
					}

				}

				// DON'T go further in! factor only the outermost sum
				return so;
			}

			return iterateThrough(this, sn, sb, colorTracker);
		}
	},

	FACTOR_BINOM_SQUARED {
		@Override
		public StepNode apply(StepNode sn, SolutionBuilder sb, int[] colorTracker) {
			if (sn.isOperation()) {
				StepOperation so = (StepOperation) sn;

				if (so.noOfOperands() >= 3) {
					StepNode first = null, second = null, third = null;

					for (int i = 0; i < 3; i++) {
						if (so.getSubTree(i).isSquare() && first == null) {
							first = so.getSubTree(i);
						} else if (so.getSubTree(i).isSquare() && third == null) {
							third = so.getSubTree(i);
						} else {
							second = so.getSubTree(i);
						}
					}

					if (first == null || second == null || third == null) {
						return iterateThrough(this, sn, sb, colorTracker);
					}

					StepNode a = first.getSquareRoot();
					StepNode b = third.getSquareRoot();
					StepNode _2ab = multiply(2, multiply(a, b));

					if (isEqual(subtract(second, _2ab).regroup(), 0) || isEqual(add(second, _2ab).regroup(), 0)) {
						boolean negative = isEqual(add(second, _2ab).regroup(), 0);

						if (second.equals(_2ab) || second.equals(negate(_2ab))) {
							if (second.isOperation(Operation.MINUS)) {
								second = ((StepOperation) second).getSubTree(0);
							}

							first.setColor(colorTracker[0]);
							a.setColor(colorTracker[0]);
							((StepOperation) second).getSubTree(1).setColor(colorTracker[0]++);
							third.setColor(colorTracker[0]);
							b.setColor(colorTracker[0]);
							((StepOperation) second).getSubTree(2).setColor(colorTracker[0]++);

							StepNode result = negative ? power(subtract(a, b), 2) : power(add(a, b), 2);

							if (negative) {
								sb.add(SolutionStepType.BINOM_SQUARED_DIFF_FACTOR);
							} else {
								sb.add(SolutionStepType.BINOM_SQUARED_SUM_FACTOR);
							}

							if (so.noOfOperands() == 3) {
								return result;
							}

							StepOperation newSum = new StepOperation(Operation.PLUS);
							newSum.addSubTree(result);
							for (int i = 3; i < so.noOfOperands(); i++) {
								newSum.addSubTree(so.getSubTree(i));
							}
							return newSum;
						}

						second.setColor(colorTracker[0]);
						_2ab.setColor(colorTracker[0]++);

						if (negative) {
							_2ab = negate(_2ab);
						}

						sb.add(SolutionStepType.REWRITE_AS, second, _2ab);

						StepOperation newSum = new StepOperation(Operation.PLUS);
						newSum.addSubTree(first);
						newSum.addSubTree(_2ab);
						newSum.addSubTree(third);

						for (int i = 3; i < so.noOfOperands(); i++) {
							newSum.addSubTree(so.getSubTree(i));
						}

						return newSum;
					}
				}
			}

			return iterateThrough(this, sn, sb, colorTracker);
		}
	},

	FACTOR_USING_FORMULA {
		@Override
		public StepNode apply(StepNode sn, SolutionBuilder sb, int[] colorTracker) {
			if (sn.isOperation(Operation.PLUS)) {
				StepOperation so = (StepOperation) sn;

				if (so.noOfOperands() == 2 && so.getSubTree(0).isSquare() && so.getSubTree(1).isSquare()) {
					if (!isNegative(so.getSubTree(0)) && isNegative(so.getSubTree(1))) {
						StepNode a = so.getSubTree(0).getSquareRoot();
						StepNode b = negate(so.getSubTree(1)).getSquareRoot();

						so.getSubTree(0).setColor(colorTracker[0]);
						a.setColor(colorTracker[0]++);
						so.getSubTree(1).setColor(colorTracker[0]);
						b.setColor(colorTracker[0]++);

						StepOperation newProduct = new StepOperation(Operation.MULTIPLY);
						newProduct.addSubTree(add(a, b));
						newProduct.addSubTree(subtract(a, b));
						
						sb.add(SolutionStepType.DIFFERENCE_OF_SQUARES_FACTOR);
						return newProduct;
					}

					if (isNegative(so.getSubTree(0)) && !isNegative(so.getSubTree(1))) {
						StepOperation reorganized = new StepOperation(Operation.PLUS);

						so.getSubTree(0).setColor(colorTracker[0]++);
						so.getSubTree(1).setColor(colorTracker[0]++);

						reorganized.addSubTree(so.getSubTree(1));
						reorganized.addSubTree(so.getSubTree(0));

						sb.add(SolutionStepType.REORGANIZE_EXPRESSION);
						return reorganized;
					}
				}

				if (so.noOfOperands() == 2 && so.getSubTree(0).isCube() && so.getSubTree(1).isCube()) {
					StepNode a = so.getSubTree(0).getCubeRoot();
					StepNode b = so.getSubTree(1).getCubeRoot();

					StepOperation newProduct = new StepOperation(Operation.MULTIPLY);

					if (!isNegative(a) && !isNegative(b)) {
						so.getSubTree(0).setColor(colorTracker[0]);
						a.setColor(colorTracker[0]++);

						so.getSubTree(1).setColor(colorTracker[0]);
						b.setColor(colorTracker[0]++);

						newProduct.addSubTree(add(a, b));
						newProduct.addSubTree(add(subtract(power(a, 2), multiply(a, b)), power(b, 2)));

						sb.add(SolutionStepType.SUM_OF_CUBES);
						return newProduct;
					} else if (!isNegative(a) && isNegative(b)) {
						StepNode minusb = negate(b);

						so.getSubTree(0).setColor(colorTracker[0]);
						a.setColor(colorTracker[0]++);

						so.getSubTree(1).setColor(colorTracker[0]);
						minusb.setColor(colorTracker[0]++);

						newProduct.addSubTree(subtract(a, minusb));
						newProduct.addSubTree(add(add(power(a, 2), multiply(a, minusb)), power(minusb, 2)));

						sb.add(SolutionStepType.DIFFERENCE_OF_CUBES_FACTOR);
						return newProduct;
					} else if (isNegative(a) && !isNegative(b)) {
						StepOperation reorganized = new StepOperation(Operation.PLUS);

						so.getSubTree(0).setColor(colorTracker[0]++);
						so.getSubTree(1).setColor(colorTracker[0]++);

						reorganized.addSubTree(so.getSubTree(1));
						reorganized.addSubTree(so.getSubTree(0));

						sb.add(SolutionStepType.REORGANIZE_EXPRESSION);
						return reorganized;
					}
				}

				// DON'T go further in! factor only the outermost sum
				return so;
			}

			return iterateThrough(this, sn, sb, colorTracker);
		}
	},

	REORGANIZE_POLYNOMIAL {
		@Override
		public StepNode apply(StepNode sn, SolutionBuilder sb, int[] colorTracker) {
			if (sn.isOperation(Operation.PLUS)) {
				StepOperation so = (StepOperation) sn;

				List<StepVariable> variableList = new ArrayList<StepVariable>();
				StepNode.getListOfVariables(so, variableList);

				if (variableList.size() == 1 && so.integerCoefficients(variableList.get(0))) {
					StepVariable var = variableList.get(0);
					StepNode[] polynomialForm = StepNode.convertToPolynomial(so, var);
					long[] integerForm = new long[polynomialForm.length];

					if (polynomialForm.length < 3) {
						return iterateThrough(this, sn, sb, colorTracker);
					}

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
								StepOperation reorganized = new StepOperation(Operation.PLUS);

								for (int k = polynomialForm.length - 1; k > 0; k--) {
									reorganized.addSubTree(nonTrivialProduct(integerForm[k], nonTrivialPower(var, k)));
									reorganized.addSubTree(
											negate(nonTrivialProduct(i * integerForm[k] / j,
													nonTrivialPower(var, k - 1))));
									integerForm[k - 1] += i * integerForm[k] / j;
								}

								colorTracker[0]++;
								sb.add(SolutionStepType.REORGANIZE_EXPRESSION);
								return reorganized;
							}
						}
					}
				}
			}

			return iterateThrough(this, sn, sb, colorTracker);
		}
	},

	FACTOR_POLYNOMIAL {
		@Override
		public StepNode apply(StepNode sn, SolutionBuilder sb, int[] colorTracker) {
			if (sn.isOperation(Operation.PLUS)) {
				StepOperation so = (StepOperation) sn;

				List<StepVariable> variableList = new ArrayList<StepVariable>();
				StepNode.getListOfVariables(so, variableList);

				if (variableList.size() == 1 && so.integerCoefficients(variableList.get(0))) {
					StepVariable var = variableList.get(0);
					StepNode[] polynomialForm = StepNode.convertToPolynomial(so, var);
					long[] integerForm = new long[polynomialForm.length];

					if(polynomialForm.length < 3) {
						return iterateThrough(this, sn, sb, colorTracker);
					}
					
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

								StepNode innerSum = add(nonTrivialProduct(j, var), -i);
								innerSum.setColor(colorTracker[0]++);
								for (int k = polynomialForm.length - 1; k > 0; k--) {
									factored.addSubTree(
											multiply(nonTrivialProduct(integerForm[k] / j, nonTrivialPower(var, k - 1)),
													innerSum));
									integerForm[k - 1] += i * integerForm[k] / j;
								}

								sb.add(SolutionStepType.FACTOR_FROM_PAIR, innerSum);
								return factored;
							}
						}
					}
				}
			}

			return iterateThrough(this, sn, sb, colorTracker);
		}
	},

	DEFAULT_FACTOR {
		@Override
		public StepNode apply(StepNode sn, SolutionBuilder sb, int[] colorTracker) {
			SimplificationSteps[] defaultStrategy = new SimplificationSteps[] { FACTOR_COMMON, FACTOR_INTEGER,
					COMPLETING_THE_SQUARE, FACTOR_BINOM_SQUARED, FACTOR_BINOM_SQUARED, FACTOR_USING_FORMULA,
					REORGANIZE_POLYNOMIAL, FACTOR_POLYNOMIAL };

			StepNode result = sn;
			String old = null, current = null;
			do {
				result = DEFAULT_REGROUP.apply(result, sb, new int[] { 1 });
				result = implementStrategy(result, sb, defaultStrategy);
				old = current;
				current = result.toString();
			} while (!current.equals(old));

			return result;
		}
	},

	DEFAULT_REGROUP {
		@Override
		public StepNode apply(StepNode sn, SolutionBuilder sb, int[] colorTracker) {
			SimplificationSteps[] defaultStrategy = new SimplificationSteps[] { CALCULATE_INVERSE_TRIGO,
					DISTRIBUTE_ROOT_OVER_FRACTION, EXPAND_ROOT, COMMON_ROOT, SIMPLIFY_POWERS_AND_ROOTS, SIMPLE_POWERS,
					SIMPLE_ROOTS, FACTOR_SQUARE, SIMPLIFY_POWERS_AND_ROOTS, ELIMINATE_OPPOSITES, DISTRIBUTE_MINUS,
					ELIMINATE_OPPOSITES, DOUBLE_MINUS, SIMPLIFY_FRACTIONS, COMMON_FRACTION,
					DISTRIBUTE_POWER_OVER_PRODUCT, REGROUP_PRODUCTS, REGROUP_SUMS, ADD_FRACTIONS, SQUARE_MINUSES,
					RATIONALIZE_DENOMINATORS };

			StepNode result = sn;
			String old = null, current = null;
			do {
				result = implementStrategy(result, sb, defaultStrategy);
				old = current;
				current = result.toString();
			} while (!current.equals(old));

			return result;
		}
	},

	DEFAULT_EXPAND {
		@Override
		public StepNode apply(StepNode sn, SolutionBuilder sb, int[] colorTracker) {
			SimplificationSteps[] expandStrategy = new SimplificationSteps[] { EXPAND_POWERS, EXPAND_PRODUCTS };

			StepNode result = sn;
			String old = null, current = null;
			do {
				result = DEFAULT_REGROUP.apply(result, sb, new int[] { 1 });
				result = implementStrategy(result, sb, expandStrategy);
				old = current;
				current = result.toString();
			} while (!current.equals(old));

			return result;
		}
	};

	private static StepNode expandProducts(StepNode sn, SolutionBuilder sb, int[] colorTracker, boolean all) {
		if (sn.isOperation()) {
			StepOperation so = (StepOperation) sn;

			if (all && so.isOperation(Operation.MULTIPLY)) {
				StepNode firstMultiplicand = null;
				StepOperation secondMultiplicand = null; // must be a sum
				StepNode remaining = null;

				for (int i = 0; i < so.noOfOperands(); i++) {
					if (firstMultiplicand == null
							&& (secondMultiplicand != null || !so.getSubTree(i).isOperation(Operation.PLUS))) {
						firstMultiplicand = so.getSubTree(i);
					} else if (secondMultiplicand == null && so.getSubTree(i).isOperation(Operation.PLUS)) {
						secondMultiplicand = (StepOperation) so.getSubTree(i);
					} else {
						remaining = multiply(remaining, so.getSubTree(i));
					}
				}

				if (firstMultiplicand != null && secondMultiplicand != null) {
					StepOperation product = new StepOperation(Operation.PLUS);

					if (firstMultiplicand.isOperation(Operation.PLUS)
							&& StepHelper.countOperation(secondMultiplicand, Operation.DIVIDE) == 0) {
						StepOperation firstMultiplicandS = (StepOperation) firstMultiplicand;

						if (firstMultiplicandS.noOfOperands() == 2 && secondMultiplicand.noOfOperands() == 2
								&& firstMultiplicandS.getSubTree(0).equals(secondMultiplicand.getSubTree(0))
								&& firstMultiplicandS.getSubTree(1).equals(negate(secondMultiplicand.getSubTree(1)))) {
							firstMultiplicandS.getSubTree(0).setColor(colorTracker[0]);
							secondMultiplicand.getSubTree(0).setColor(colorTracker[0]++);
							firstMultiplicandS.getSubTree(1).setColor(colorTracker[0]);
							secondMultiplicand.getSubTree(1).setColor(colorTracker[0]++);

							product.addSubTree(power(firstMultiplicandS.getSubTree(0), 2));
							if (isNegative(firstMultiplicandS.getSubTree(1))) {
								product.addSubTree(minus(power(negate(firstMultiplicandS.getSubTree(1)), 2)));
							} else {
								product.addSubTree(minus(power(firstMultiplicandS.getSubTree(1), 2)));
							}

							sb.add(SolutionStepType.DIFFERENCE_OF_SQUARES);
						} else {
							for (int i = 0; i < firstMultiplicandS.noOfOperands(); i++) {
								firstMultiplicandS.getSubTree(i).setColor(colorTracker[0]++);
							}
							for (int i = 0; i < secondMultiplicand.noOfOperands(); i++) {
								secondMultiplicand.getSubTree(i).setColor(colorTracker[0]++);
							}

							for (int i = 0; i < firstMultiplicandS.noOfOperands(); i++) {
								for (int j = 0; j < secondMultiplicand.noOfOperands(); j++) {
									product.addSubTree(multiply(firstMultiplicandS.getSubTree(i),
											secondMultiplicand.getSubTree(j)));
								}
							}

							sb.add(SolutionStepType.EXPAND_SUM_TIMES_SUM);
						}
					} else {
						firstMultiplicand.setColor(colorTracker[0]++);
						for (int i = 0; i < secondMultiplicand.noOfOperands(); i++) {
							secondMultiplicand.getSubTree(i).setColor(colorTracker[0]++);
						}

						for (int i = 0; i < secondMultiplicand.noOfOperands(); i++) {
							product.addSubTree(multiply(firstMultiplicand, secondMultiplicand.getSubTree(i)));
						}
						sb.add(SolutionStepType.EXPAND_SIMPLE_TIMES_SUM, firstMultiplicand);
					}

					return multiply(product, remaining);
				}
			}

			StepOperation toReturn = new StepOperation(so.getOperation());
			for (int i = 0; i < so.noOfOperands(); i++) {
				if (so.getSubTree(i).isOperation(Operation.ABS)) {
					toReturn.addSubTree(so.getSubTree(i));
				} else if (so.isOperation(Operation.DIVIDE)) {
					toReturn.addSubTree(expandProducts(so.getSubTree(i), sb, colorTracker, all || i == 1));
				} else {
					toReturn.addSubTree(expandProducts(so.getSubTree(i), sb, colorTracker, all));
				}
			}
			return toReturn;
		}

		return sn;
	}

	private static StepNode iterateThrough(SimplificationSteps step, StepNode sn, SolutionBuilder sb,
			int[] colorTracker) {
		if (sn.isOperation()) {
			StepOperation so = (StepOperation) sn;

			int colorsAtStart = colorTracker[0];

			StepOperation toReturn = null;
			for (int i = 0; i < so.noOfOperands(); i++) {
				StepNode a = step.apply(so.getSubTree(i), sb, colorTracker);
				if (toReturn == null && colorTracker[0] > colorsAtStart) {
					toReturn = new StepOperation(so.getOperation());

					for (int j = 0; j < i; j++) {
						toReturn.addSubTree(so.getSubTree(j));
					}
				}
				if (toReturn != null) {
					toReturn.addSubTree(a);
				}
			}

			if (toReturn == null) {
				return so;
			}

			return toReturn;
		}

		return sn;
	}

	public abstract StepNode apply(StepNode sn, SolutionBuilder sb, int[] colorTracker);

	public static StepNode implementStrategy(StepNode sn, SolutionBuilder sb, SimplificationSteps[] strategy,
			boolean substep) {
		final boolean printDebug = true;

		int[] colorTracker = new int[] { 1 };
		SolutionBuilder changes = new SolutionBuilder(sb == null ? null : sb.getLocalization());

		StepNode origSn = sn, newSn;

		for (int i = 0; i < strategy.length; i++) {
			newSn = strategy[i].apply(origSn, changes, colorTracker);

			if (printDebug) {
				if (colorTracker[0] > 1) {
					Log.error("changed at " + strategy[i]);
					Log.error("from: " + origSn);
					Log.error("to: " + newSn);
				}
			}

			if (colorTracker[0] > 1) {
				if (sb != null) {
					if (substep) {
						sb.add(SolutionStepType.SUBSTEP_WRAPPER);
						sb.levelDown();
						sb.add(SolutionStepType.EQUATION, origSn.deepCopy());
						sb.addAll(changes.getSteps());
						sb.add(SolutionStepType.EQUATION, newSn.deepCopy());
						sb.levelUp();
					} else {
						sb.addAll(changes.getSteps());
					}
				}

				newSn.cleanColors();
				colorTracker[0] = 1;
			}

			changes.reset();
			origSn = newSn;
		}

		return origSn;
	}

	public static StepNode implementStrategy(StepNode sn, SolutionBuilder sb, SimplificationSteps[] strategy) {
		return implementStrategy(sn, sb, strategy, true);
	}
}
