package org.geogebra.common.kernel.stepbystep.steptree;

import static org.geogebra.common.kernel.stepbystep.steptree.StepNode.abs;
import static org.geogebra.common.kernel.stepbystep.steptree.StepNode.add;
import static org.geogebra.common.kernel.stepbystep.steptree.StepNode.closeToAnInteger;
import static org.geogebra.common.kernel.stepbystep.steptree.StepNode.divide;
import static org.geogebra.common.kernel.stepbystep.steptree.StepNode.isEqual;
import static org.geogebra.common.kernel.stepbystep.steptree.StepNode.isEven;
import static org.geogebra.common.kernel.stepbystep.steptree.StepNode.isNegative;
import static org.geogebra.common.kernel.stepbystep.steptree.StepNode.isOdd;
import static org.geogebra.common.kernel.stepbystep.steptree.StepNode.minus;
import static org.geogebra.common.kernel.stepbystep.steptree.StepNode.multiply;
import static org.geogebra.common.kernel.stepbystep.steptree.StepNode.negate;
import static org.geogebra.common.kernel.stepbystep.steptree.StepNode.power;
import static org.geogebra.common.kernel.stepbystep.steptree.StepNode.root;

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
			return apply(sn, sb, colorTracker, true);
		}

		public StepNode apply(StepNode sn, SolutionBuilder sb, int[] colorTracker, boolean all) {
			if (sn.isOperation()) {
				StepOperation so = (StepOperation) sn;

				if (all && so.isOperation(Operation.MULTIPLY)) {
					StepNode firstMultiplicand = null;
					StepOperation secondMultiplicand = null; // must be a sum
					StepNode remaining = null;

					for (int i = 0; i < so.noOfOperands(); i++) {
						if (firstMultiplicand == null && (secondMultiplicand != null || !so.getSubTree(i).isOperation(Operation.PLUS))) {
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
										product.addSubTree(multiply(firstMultiplicandS.getSubTree(i), secondMultiplicand.getSubTree(j)));
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
						toReturn.addSubTree(apply(so.getSubTree(i), sb, colorTracker, all || i == 1));
					} else {
						toReturn.addSubTree(apply(so.getSubTree(i), sb, colorTracker, all));
					}
				}
				return toReturn;
			}

			return sn;
		}
	},

	EXPAND_POWERS() {
		@Override
		public StepNode apply(StepNode sn, SolutionBuilder sb, int[] colorTracker) {
			if (sn.isOperation()) {
				StepOperation so = (StepOperation) sn;
	
				if (so.isOperation(Operation.POWER) && so.getSubTree(0).isOperation(Operation.PLUS)) {
					StepOperation sum = (StepOperation) so.getSubTree(0);
	
					if (so.getSubTree(1).getValue() > 0 && closeToAnInteger(so.getSubTree(1))) {
						if (so.getSubTree(1).getValue() + sum.noOfOperands() < 6) {
							for (int i = 0; i < sum.noOfOperands(); i++) {
								sum.getSubTree(i).setColor(colorTracker[0]++);
							}
	
							StepOperation newSum = new StepOperation(Operation.PLUS);
	
							if (isEqual(so.getSubTree(1), 2)) {
								if (sum.noOfOperands() == 2) {
									if (isNegative(sum.getSubTree(1))) {
										newSum.addSubTree(power(sum.getSubTree(0), 2));
										newSum.addSubTree(multiply(-2, multiply(sum.getSubTree(0), negate(sum.getSubTree(1)))));
										newSum.addSubTree(power(negate(sum.getSubTree(1)), 2));

										sb.add(SolutionStepType.BINOM_SQUARED_DIFF);
									} else {
										newSum.addSubTree(power(sum.getSubTree(0), 2));
										newSum.addSubTree(multiply(2, multiply(sum.getSubTree(0), sum.getSubTree(1))));
										newSum.addSubTree(power(sum.getSubTree(1), 2));

										sb.add(SolutionStepType.BINOM_SQUARED_SUM);
									}
								} else if (sum.noOfOperands() == 3) {
									newSum.addSubTree(power(sum.getSubTree(0), 2));
									newSum.addSubTree(power(sum.getSubTree(1), 2));
									newSum.addSubTree(power(sum.getSubTree(2), 2));
									newSum.addSubTree(multiply(2, multiply(sum.getSubTree(0), sum.getSubTree(1))));
									newSum.addSubTree(multiply(2, multiply(sum.getSubTree(1), sum.getSubTree(2))));
									newSum.addSubTree(multiply(2, multiply(sum.getSubTree(0), sum.getSubTree(2))));
	
									sb.add(SolutionStepType.TRINOM_SQUARED);
								}
							} else if (isEqual(so.getSubTree(1), 3)) {
								if (sum.noOfOperands() == 2) {
									newSum.addSubTree(power(sum.getSubTree(0), 3));
									newSum.addSubTree(multiply(3, multiply(power(sum.getSubTree(0), 2), sum.getSubTree(1))));
									newSum.addSubTree(multiply(3, multiply(sum.getSubTree(0), power(sum.getSubTree(1), 2))));
									newSum.addSubTree(power(sum.getSubTree(1), 3));
	
									sb.add(SolutionStepType.BINOM_CUBED);
								}
							}
	
							return newSum;
						}
	
						StepOperation asMultiplication = new StepOperation(Operation.MULTIPLY);
						for (int i = 0; i < Math.round(so.getSubTree(1).getValue()); i++) {
							asMultiplication.addSubTree(sum.deepCopy());
						}
						return asMultiplication;
					}
				}
	
				StepOperation toReturn = new StepOperation(so.getOperation());
				for (int i = 0; i < so.noOfOperands(); i++) {
					if (so.getSubTree(i).isOperation(Operation.ABS)) {
						toReturn.addSubTree(so.getSubTree(i));
					} else {
						toReturn.addSubTree(apply(so.getSubTree(i), sb, colorTracker));
					}
				}
				return toReturn;
			}
	
			return sn;
		}
	},


	ELIMINATE_OPPOSITES {
		@Override
		public StepNode apply(StepNode sn, SolutionBuilder sb, int[] colorTracker) {
			if (sn.isOperation()) {
				StepOperation so = new StepOperation(((StepOperation) sn).getOperation());

				Integer colorsAtStart = colorTracker[0];
				for (int i = 0; i < ((StepOperation) sn).noOfOperands(); i++) {
					so.addSubTree(apply(((StepOperation) sn).getSubTree(i), sb, colorTracker));
				}

				if (colorsAtStart < colorTracker[0]) {
					return so;
				}

				so = (StepOperation) sn;

				if (so.isOperation(Operation.PLUS)) {
					StepNode[] coefficients = new StepNode[so.noOfOperands()];
					StepNode[] variables = new StepNode[so.noOfOperands()];
					for (int i = 0; i < so.noOfOperands(); i++) {
						coefficients[i] = so.getSubTree(i).getIntegerCoefficient();
						variables[i] = so.getSubTree(i).getNonInteger();

						if (coefficients[i] == null) {
							coefficients[i] = new StepConstant(1);
						}
						if (variables[i] == null) {
							variables[i] = new StepConstant(1);
						}
					}

					for (int i = 0; i < so.noOfOperands(); i++) {
						if (!isEqual(variables[i], 0)) {
							for (int j = i + 1; j < so.noOfOperands(); j++) {
								if (!isEqual(variables[j], 0) && isEqual(coefficients[j], -coefficients[i].getValue())
										&& variables[i].equals(variables[j])) {
									so.getSubTree(i).setColor(colorTracker[0]);
									so.getSubTree(j).setColor(colorTracker[0]);

									variables[i] = new StepConstant(0);
									variables[j] = new StepConstant(0);

									sb.add(SolutionStepType.ELIMINATE_OPPOSITES, colorTracker[0]++);
									break;
								}
							}
						}
					}

					StepOperation newSum = new StepOperation(Operation.PLUS);

					for (int i = 0; i < so.noOfOperands(); i++) {
						if (coefficients[i].getValue() != 0 && variables[i].getValue() != 0) {
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
	
	DOUBLE_MINUS {
		@Override
		public StepNode apply(StepNode sn, SolutionBuilder sb, int[] colorTracker) {
			if (sn.isOperation()) {
				StepOperation so = (StepOperation) sn;
	
				if (so.isOperation(Operation.MINUS)) {
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
	
				StepOperation toReturn = new StepOperation(so.getOperation());
				for (int i = 0; i < so.noOfOperands(); i++) {
					toReturn.addSubTree(apply(so.getSubTree(i), sb, colorTracker));
				}
				return toReturn;
			}
	
			return sn;
		}	
	}, 
	
	DISTRIBUTE_ROOT_OVER_FRACTION {
		@Override
		public StepNode apply(StepNode sn, SolutionBuilder sb, int[] colorTracker) {
			if (sn.isOperation()) {
				StepOperation so = (StepOperation) sn;
	
				if (so.isOperation(Operation.NROOT)) {
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
	
				StepOperation toReturn = new StepOperation(so.getOperation());
				for (int i = 0; i < so.noOfOperands(); i++) {
					toReturn.addSubTree(apply(so.getSubTree(i), sb, colorTracker));
				}
				return toReturn;
			}
	
			return sn;
		}
	},
	
	RATIONALIZE_DENOMINATOR {
		@Override
		public StepNode apply(StepNode sn, SolutionBuilder sb, int[] colorTracker) {
			if (sn.isOperation()) {
				StepOperation so = (StepOperation) sn;
	
				if (so.isOperation(Operation.DIVIDE)) {
					if (so.getSubTree(1).isOperation(Operation.NROOT)) {
						double root = ((StepOperation) so.getSubTree(1)).getSubTree(1).getValue();
	
						if (closeToAnInteger(root)) {
							StepNode toMultiply = StepNode.nonTrivialPower(root(((StepOperation) so.getSubTree(1)).getSubTree(0), root),
									root - 1);
	
							toMultiply.setColor(colorTracker[0]++);
	
							StepNode numerator = StepNode.nonTrivialProduct(so.getSubTree(0), toMultiply);
							StepNode denominator = multiply(so.getSubTree(1), toMultiply);
	
							StepNode result = divide(numerator, denominator);
							sb.add(SolutionStepType.MULTIPLY_NUM_DENOM, toMultiply);
	
							return result;
						}
					}
	
					if (so.getSubTree(1).isOperation(Operation.PLUS)) {
						StepOperation sum = (StepOperation) so.getSubTree(1);
	
						if (sum.noOfOperands() == 2 && (sum.getSubTree(0).isSquareRoot() || sum.getSubTree(1).isSquareRoot())) {
							StepNode toMultiply = add(sum.getSubTree(0), negate(sum.getSubTree(1)));
	
							toMultiply.setColor(colorTracker[0]++);
	
							StepNode numerator = StepNode.nonTrivialProduct(so.getSubTree(0), toMultiply);
							StepNode denominator = multiply(so.getSubTree(1), toMultiply);
	
							StepNode result = divide(numerator, denominator);
							sb.add(SolutionStepType.MULTIPLY_NUM_DENOM, toMultiply);
	
							return result;
						}
					}
				}
	
				StepOperation toReturn = new StepOperation(so.getOperation());
				for (int i = 0; i < so.noOfOperands(); i++) {
					toReturn.addSubTree(apply(so.getSubTree(i), sb, colorTracker));
				}
				return toReturn;
			}
	
			return sn;
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
	
				StepOperation toReturn = new StepOperation(so.getOperation());
				for (int i = 0; i < so.noOfOperands(); i++) {
					toReturn.addSubTree(apply(so.getSubTree(i), sb, colorTracker));
				}
				return toReturn;
			}
	
			return sn;
		}	
	},
	
	FACTOR_SQUARE {
		@Override
		public StepNode apply(StepNode sn, SolutionBuilder sb, int[] colorTracker) {
			if (sn.isOperation()) {
				StepOperation so = (StepOperation) sn;
	
				if (so.isOperation(Operation.NROOT)) {
					StepNode coefficient = so.getSubTree(0).getIntegerCoefficient();
					StepNode remainder = so.getSubTree(0).getNonInteger();
	
					if (closeToAnInteger(coefficient)) {
						long root = Math.round(so.getSubTree(1).getValue());
						long newCoefficient = StepNode.largestNthPower(coefficient.getValue(), so.getSubTree(1).getValue());
	
						if (!isEqual(newCoefficient, 1)) {
							StepNode result = multiply(newCoefficient,
									root(multiply(coefficient.getValue() / Math.pow(newCoefficient, root), remainder), so.getSubTree(1)));
	
							so.setColor(colorTracker[0]);
							result.setColor(colorTracker[0]);
	
							sb.add(SolutionStepType.FACTOR_SQUARE, colorTracker[0]++);
	
							return result;
						}
	
						long power = StepNode.getIntegerPower(Math.round(so.getSubTree(0).getValue()));
						long gcd = StepNode.gcd(root, power);
	
						if (gcd > 1) {
							StepNode newValue = power(new StepConstant(Math.pow(so.getSubTree(0).getValue(), ((double) 1) / gcd)), gcd);
	
							so.getSubTree(0).setColor(colorTracker[0]);
							newValue.setColor(colorTracker[0]++);
	
							StepNode result = root(newValue, root);
							sb.add(SolutionStepType.REWRITE_AS_POWER, so.getSubTree(0), newValue);
	
							return result;
						}
					}
				}
	
				StepOperation toReturn = new StepOperation(so.getOperation());
				for (int i = 0; i < so.noOfOperands(); i++) {
					toReturn.addSubTree(apply(so.getSubTree(i), sb, colorTracker));
				}
	
				return toReturn;
			}
	
			return sn;
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
								if (!isEqual(coefficients[j], 0) && !isEqual(variables[i], 1) && variables[i].equals(variables[j])) {
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
						if (!isEqual(coefficients[i].getValue(), 0) && !isEqual(variables[i], 0)) {
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
			if (sn.isOperation()) {
				StepOperation so = (StepOperation) sn;

				if (so.isOperation(Operation.PLUS)) {
					StepOperation newSum = new StepOperation(Operation.PLUS);

					long newDenominator = 1;

					for (int i = 0; i < so.noOfOperands(); i++) {
						long currentDenominator = StepNode.getDenominator(so.getSubTree(i));
						if (currentDenominator != 0) {
							newDenominator = StepNode.lcm(newDenominator, currentDenominator);
						}
					}

					if (newDenominator != 1) {
						boolean wasChanged = false;

						for (int i = 0; i < so.noOfOperands(); i++) {
							long currentDenominator = StepNode.getDenominator(so.getSubTree(i));
							if (currentDenominator != 0 && currentDenominator != newDenominator) {
								wasChanged = true;

								StepNode newFraction = divide(
										StepNode.nonTrivialProduct(new StepConstant(((double) newDenominator) / currentDenominator),
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

				StepOperation toReturn = new StepOperation(so.getOperation());
				for (int i = 0; i < so.noOfOperands(); i++) {
					toReturn.addSubTree(apply(so.getSubTree(i), sb, colorTracker));
				}

				return toReturn;
			}

			return sn;
		}
	},
	
	ADD_NUMERATORS {
		@Override
		public StepNode apply(StepNode sn, SolutionBuilder sb, int[] colorTracker) {
			if (sn.isOperation()) {
				StepOperation so = (StepOperation) sn;

				if (so.isOperation(Operation.PLUS)) {
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

				StepOperation toReturn = new StepOperation(so.getOperation());
				for (int i = 0; i < so.noOfOperands(); i++) {
					toReturn.addSubTree(apply(so.getSubTree(i), sb, colorTracker));
				}

				return toReturn;
			}

			return sn;
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

				if (so.isOperation(Operation.MULTIPLY) || so.isOperation(Operation.DIVIDE)) {
					List<StepNode> bases = new ArrayList<StepNode>();
					List<StepNode> exponents = new ArrayList<StepNode>();

					StepNode.getBasesAndExponents(so, null, bases, exponents);

					for (int i = 0; i < bases.size(); i++) {
						for (int j = i + 1; j < bases.size(); j++) {
							if ((exponents.get(i).getValue() * exponents.get(j).getValue()) < 0 && bases.get(i).equals(bases.get(j))
									&& !isEqual(bases.get(i), 1)) {
								bases.get(i).setColor(colorTracker[0]);
								bases.get(j).setColor(colorTracker[0]);

								double min = Math.min(Math.abs(exponents.get(i).getValue()), Math.abs(exponents.get(j).getValue()));

								exponents.get(i).setColor(colorTracker[0]);
								exponents.get(j).setColor(colorTracker[0]);

								double newExponent1 = exponents.get(i).getValue() > 0 ? exponents.get(i).getValue() - min
										: exponents.get(i).getValue() + min;
								double newExponent2 = exponents.get(j).getValue() > 0 ? exponents.get(j).getValue() - min
										: exponents.get(j).getValue() + min;

								exponents.set(i, new StepConstant(newExponent1));
								exponents.set(j, new StepConstant(newExponent2));

								exponents.get(i).setColor(colorTracker[0]);
								exponents.get(j).setColor(colorTracker[0]);

								StepNode toCancel = StepNode.nonTrivialPower(bases.get(i), min);
								toCancel.setColor(colorTracker[0]++);
								sb.add(SolutionStepType.CANCEL_FRACTION, toCancel);

								break;
							}
							if (isEqual(exponents.get(i), 1) && isEqual(exponents.get(j), -1) && closeToAnInteger(bases.get(i))
									&& closeToAnInteger(bases.get(j))) {
								long gcd = StepNode.gcd(bases.get(i), bases.get(j));
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

				StepOperation toReturn = new StepOperation(so.getOperation());
				for (int i = 0; i < so.noOfOperands(); i++) {
					toReturn.addSubTree(apply(so.getSubTree(i), sb, colorTracker));
				}
				return toReturn;
			}

			return sn;
		}
	},
	
	COMMON_FRACTION {
		@Override
		public StepNode apply(StepNode sn, SolutionBuilder sb, int[] colorTracker) {
			if (sn.isOperation()) {
				StepOperation so = new StepOperation(((StepOperation) sn).getOperation());

				Integer colorsAtStart = colorTracker[0];
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

				return so;
			}

			return sn;
		}

	},
	
	REGROUP_PRODUCTS {
		@Override
		public StepNode apply(StepNode sn, SolutionBuilder sb, int[] colorTracker) {
			if (sn.isOperation()) {
				StepOperation so = new StepOperation(((StepOperation) sn).getOperation());

				Integer colorsAtStart = colorTracker[0];
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
			if (sn.isOperation()) {
				StepOperation so = (StepOperation) sn;

				if (so.isOperation(Operation.POWER)) {
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

				StepOperation toReturn = new StepOperation(so.getOperation());
				for (int i = 0; i < so.noOfOperands(); i++) {
					toReturn.addSubTree(apply(so.getSubTree(i), sb, colorTracker));
				}
				return toReturn;
			}

			return sn;
		}
	},

	SQUARE_MINUSES {
		@Override
		public StepNode apply(StepNode sn, SolutionBuilder sb, int[] colorTracker) {
			if (sn.isOperation()) {
				StepOperation so = (StepOperation) sn;
	
				if (so.isOperation(Operation.POWER)) {
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
	
				StepOperation toReturn = new StepOperation(so.getOperation());
				for (int i = 0; i < so.noOfOperands(); i++) {
					toReturn.addSubTree(apply(so.getSubTree(i), sb, colorTracker));
				}
				return toReturn;
			}
	
			return sn;	
		}
	},

	POWERS_AND_ROOTS {
		@Override
		public StepNode apply(StepNode sn, SolutionBuilder sb, int[] colorTracker) {
			if (sn.isOperation()) {
				StepOperation so = (StepOperation) sn;

				if ((so.isOperation(Operation.POWER) && so.getSubTree(0).isOperation(Operation.NROOT))
						|| (so.isOperation(Operation.NROOT) && so.getSubTree(0).isOperation(Operation.POWER))) {
					StepNode exponent1 = so.getSubTree(1);
					StepNode exponent2 = ((StepOperation) so.getSubTree(0)).getSubTree(1);

					if (closeToAnInteger(exponent1) && closeToAnInteger(exponent2)) {
						long gcd = StepNode.gcd(Math.round(exponent1.getValue()), Math.round(exponent2.getValue()));

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
										result = root(power(new StepConstant(Math.abs(argument.getValue())), exponent2), exponent1);
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

				if (so.isOperation(Operation.POWER)) {
					if (closeToAnInteger(so.getSubTree(0)) && closeToAnInteger(so.getSubTree(1))) {
						StepNode result = new StepConstant(Math.pow(so.getSubTree(0).getValue(), so.getSubTree(1).getValue()));

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
				} else if (so.isOperation(Operation.NROOT)) {
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

				StepOperation toReturn = new StepOperation(so.getOperation());
				for (int i = 0; i < so.noOfOperands(); i++) {
					toReturn.addSubTree(apply(so.getSubTree(i), sb, colorTracker));
				}
				return toReturn;
			}

			return sn;
		}
	},

	CALCULATE_INVERSE_TRIGO {
		@Override
		public StepNode apply(StepNode sn, SolutionBuilder sb, int[] colorTracker) {
			if (sn.isOperation()) {
				StepOperation so = (StepOperation) sn;

				if (so.isInverseTrigonometric()) {
					StepNode value = StepNode.inverseTrigoLookup(so);
					if (value != null) {
						so.setColor(colorTracker[0]);
						value.setColor(colorTracker[0]);
						sb.add(SolutionStepType.EVALUATE_INVERSE_TRIGO, colorTracker[0]++);
						return value;
					}
				}

				StepOperation toReturn = new StepOperation(so.getOperation());
				for (int i = 0; i < so.noOfOperands(); i++) {
					toReturn.addSubTree(apply(so.getSubTree(i), sb, colorTracker));
				}
				return toReturn;
			}

			return sn;
		}
	},

	ADD_FRACTIONS {
		@Override
		public StepNode apply(StepNode sn, SolutionBuilder sb, int[] colorTracker) {
			SimplificationSteps[] fractionAddition = new SimplificationSteps[] {
					SimplificationSteps.EXPAND_FRACTIONS,
					SimplificationSteps.ADD_NUMERATORS,
					SimplificationSteps.REGROUP_PRODUCTS,
					SimplificationSteps.REGROUP_SUMS,
					SimplificationSteps.SIMPLIFY_FRACTIONS
			};
			
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
			SimplificationSteps[] denominatorRationalization = new SimplificationSteps[] {
					SimplificationSteps.RATIONALIZE_DENOMINATOR,
					SimplificationSteps.REGROUP_PRODUCTS,
					SimplificationSteps.REGROUP_SUMS,
					SimplificationSteps.EXPAND_PRODUCTS, 
					SimplificationSteps.POWERS_AND_ROOTS,
					SimplificationSteps.SIMPLIFY_FRACTIONS
			};
			
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

	DEFAULT_REGROUP {
		@Override
		public StepNode apply(StepNode sn, SolutionBuilder sb, int[] colorTracker) {
			SimplificationSteps[] defaultStrategy = new SimplificationSteps[] {
					CALCULATE_INVERSE_TRIGO,
					DISTRIBUTE_ROOT_OVER_FRACTION, 
					POWERS_AND_ROOTS,
					FACTOR_SQUARE,
					POWERS_AND_ROOTS, 
					DISTRIBUTE_MINUS, 
					DOUBLE_MINUS, 
					ELIMINATE_OPPOSITES,
					SIMPLIFY_FRACTIONS, 
					COMMON_FRACTION,
					DISTRIBUTE_POWER_OVER_PRODUCT,
					ADD_FRACTIONS,
					REGROUP_PRODUCTS,
					REGROUP_SUMS,
					SQUARE_MINUSES,
					RATIONALIZE_DENOMINATORS
			};

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
			SimplificationSteps[] expandStrategy = new SimplificationSteps[] {
					EXPAND_POWERS,
					EXPAND_PRODUCTS
			};

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

	public abstract StepNode apply(StepNode sn, SolutionBuilder sb, int[] colorTracker);

	public static StepNode implementStrategy(StepNode sn, SolutionBuilder sb, SimplificationSteps[] strategy, boolean substep) {
		final boolean printDebug = false;

		int[] colorTracker = new int[] { 1 };
		SolutionBuilder changes = new SolutionBuilder(sb == null ? null : sb.getLocalization());

		StepNode origSn = sn, newSn;

		for (int i = 0; i < strategy.length; i++) {
			newSn = strategy[i].apply(origSn, changes, colorTracker);

			if (printDebug) {
				Log.error(": " + origSn);
				if (colorTracker[0] > 1) {
					Log.error("changed");
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
