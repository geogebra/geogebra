package org.geogebra.common.kernel.stepbystep.steps;

import static org.geogebra.common.kernel.stepbystep.steptree.StepExpression.nonTrivialPower;
import static org.geogebra.common.kernel.stepbystep.steptree.StepExpression.nonTrivialProduct;
import static org.geogebra.common.kernel.stepbystep.steptree.StepExpression.nonTrivialRoot;
import static org.geogebra.common.kernel.stepbystep.steptree.StepExpression.simplifiedProduct;
import static org.geogebra.common.kernel.stepbystep.steptree.StepNode.abs;
import static org.geogebra.common.kernel.stepbystep.steptree.StepNode.add;
import static org.geogebra.common.kernel.stepbystep.steptree.StepNode.closeToAnInteger;
import static org.geogebra.common.kernel.stepbystep.steptree.StepNode.divide;
import static org.geogebra.common.kernel.stepbystep.steptree.StepNode.gcd;
import static org.geogebra.common.kernel.stepbystep.steptree.StepNode.getIntegerPower;
import static org.geogebra.common.kernel.stepbystep.steptree.StepNode.isEqual;
import static org.geogebra.common.kernel.stepbystep.steptree.StepNode.isOdd;
import static org.geogebra.common.kernel.stepbystep.steptree.StepNode.isOne;
import static org.geogebra.common.kernel.stepbystep.steptree.StepNode.isZero;
import static org.geogebra.common.kernel.stepbystep.steptree.StepNode.largestNthPower;
import static org.geogebra.common.kernel.stepbystep.steptree.StepNode.lcm;
import static org.geogebra.common.kernel.stepbystep.steptree.StepNode.minus;
import static org.geogebra.common.kernel.stepbystep.steptree.StepNode.multiply;
import static org.geogebra.common.kernel.stepbystep.steptree.StepNode.power;
import static org.geogebra.common.kernel.stepbystep.steptree.StepNode.root;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.geogebra.common.kernel.stepbystep.StepHelper;
import org.geogebra.common.kernel.stepbystep.solution.SolutionBuilder;
import org.geogebra.common.kernel.stepbystep.solution.SolutionStepType;
import org.geogebra.common.kernel.stepbystep.steptree.StepConstant;
import org.geogebra.common.kernel.stepbystep.steptree.StepExpression;
import org.geogebra.common.kernel.stepbystep.steptree.StepOperation;
import org.geogebra.common.kernel.stepbystep.steptree.StepTransformable;
import org.geogebra.common.plugin.Operation;

enum RegroupSteps implements SimplificationStepGenerator {

	DECIMAL_SIMPLIFY_ROOTS {
		@Override
		public StepTransformable apply(StepTransformable sn, SolutionBuilder sb,
				RegroupTracker tracker) {
			if (sn.isOperation(Operation.NROOT)) {
				StepOperation root = (StepOperation) sn;
				StepExpression underRoot = root.getOperand(0);
				StepExpression coefficient = underRoot.getIntegerCoefficient();
				StepExpression remainder = underRoot.getNonInteger();

				if (coefficient != null && remainder != null) {
					StepExpression firstPart = root(coefficient, root.getOperand(1));
					StepExpression secondPart = root(remainder, root.getOperand(1));
					StepExpression result = multiply(firstPart, secondPart);

					root.setColor(tracker.getColorTracker());
					result.setColor(tracker.getColorTracker());

					sb.add(SolutionStepType.SPLIT_ROOTS, tracker.incColorTracker());
					return result;
				}

				if (coefficient != null) {
					StepExpression result = StepConstant.create(root.getValue());

					root.setColor(tracker.getColorTracker());
					result.setColor(tracker.getColorTracker());

					sb.add(SolutionStepType.EVALUATE_ROOT, tracker.incColorTracker());
					return result;
				}
			}

			return sn.iterateThrough(this, sb, tracker);
		}
	},

	ELIMINATE_OPPOSITES {
		@Override
		public StepTransformable apply(StepTransformable sn, SolutionBuilder sb,
				RegroupTracker tracker) {
			if (sn.isOperation(Operation.PLUS)) {
				StepOperation so = (StepOperation) sn;

				boolean[] found = new boolean[so.noOfOperands()];
				for (int i = 0; i < so.noOfOperands(); i++) {
					if (so.getOperand(i).isOperation(Operation.MINUS)
							&& ((StepOperation) so.getOperand(i)).getOperand(0)
									.isOperation(Operation.PLUS)) {
						StepOperation innerSum =
								(StepOperation) ((StepOperation) so.getOperand(i)).getOperand(0);

						for (int j = 0; j < so.noOfOperands() - innerSum.noOfOperands(); j++) {
							boolean foundSum = true;
							for (int k = 0; foundSum && k < innerSum.noOfOperands(); k++) {
								if (!so.getOperand(j + k).equals(innerSum.getOperand(k))) {
									foundSum = false;
								}
							}

							if (foundSum) {
								found[i] = true;
								so.getOperand(i).setColor(tracker.getColorTracker());

								for (int k = 0; k < innerSum.noOfOperands(); k++) {
									found[j + k] = true;
									so.getOperand(j + k).setColor(tracker.getColorTracker());
								}
								sb.add(SolutionStepType.ELIMINATE_OPPOSITES,
										tracker.incColorTracker());
								break;
							}
						}
					}

					for (int j = i + 1; !found[i] && j < so.noOfOperands(); j++) {
						if (so.getOperand(i).equals(so.getOperand(j).negate())
								|| so.getOperand(j).equals(so.getOperand(i).negate())) {
							so.getOperand(i).setColor(tracker.getColorTracker());
							so.getOperand(j).setColor(tracker.getColorTracker());
							sb.add(SolutionStepType.ELIMINATE_OPPOSITES, tracker.incColorTracker());
							found[i] = true;
							found[j] = true;
						}
					}
				}

				List<StepExpression> newSum = new ArrayList<>();
				for (int i = 0; i < so.noOfOperands(); i++) {
					if (!found[i]) {
						newSum.add(so.getOperand(i));
					}
				}

				if (newSum.size() == 0) {
					return StepConstant.create(0);
				}

				if (tracker.stepAdded()) {
					return StepOperation.add(newSum);
				}
			}

			return sn.iterateThrough(this, sb, tracker);
		}
	},

	EXPONENTIAL_OF_LOG {
		@Override
		public StepTransformable apply(StepTransformable sn, SolutionBuilder sb,
				RegroupTracker tracker) {

			if (sn.isOperation(Operation.POWER)) {
				StepExpression base = ((StepOperation) sn).getOperand(0);
				StepExpression exponent = ((StepOperation) sn).getOperand(1);
				if ((exponent.isOperation(Operation.LOG))
						&& ((StepOperation) exponent).getOperand(0).equals(base)) {
					sb.add(SolutionStepType.EXPONENTIAL_OF_LOG, tracker.incColorTracker());
					StepExpression argument = ((StepOperation) exponent).getOperand(1);
					argument.setColor(tracker.getColorTracker());
					return argument;
				}
			}

			return sn.iterateThrough(this, sb, tracker);
		};
	},

	EXPAND_ROOT {
		@Override
		public StepTransformable apply(StepTransformable sn, SolutionBuilder sb,
				RegroupTracker tracker) {
			if (sn.isOperation(Operation.MULTIPLY)) {
				StepOperation so = (StepOperation) sn;

				int rootCount = 0;
				long commonRoot = 1;
				for (StepExpression operand : so) {
					if (operand.isOperation(Operation.NROOT)) {
						double currentRoot = ((StepOperation) operand).getOperand(1).getValue();
						if (closeToAnInteger(currentRoot)) {
							rootCount++;
							commonRoot = lcm(commonRoot, Math.round(currentRoot));
						}
					}
				}

				if (rootCount > 1) {
					StepExpression[] newProduct = new StepExpression[so.noOfOperands()];
					for (int i = 0; i < so.noOfOperands(); i++) {
						if (so.getOperand(i).isOperation(Operation.NROOT)) {
							double currentRoot =
									((StepOperation) so.getOperand(i)).getOperand(1).getValue();
							if (closeToAnInteger(currentRoot)
									&& !isEqual(commonRoot, currentRoot)) {
								StepExpression argument =
										((StepOperation) so.getOperand(i)).getOperand(0);

								StepExpression result =
										root(power(argument, commonRoot / currentRoot), commonRoot);

								so.getOperand(i).setColor(tracker.getColorTracker());
								result.setColor(tracker.getColorTracker());

								sb.add(SolutionStepType.EXPAND_ROOT, tracker.incColorTracker());

								newProduct[i] = result;
							} else {
								newProduct[i] = so.getOperand(i);
							}
						} else {
							newProduct[i] = so.getOperand(i);
						}
					}

					return StepOperation.multiply(newProduct);
				}
			}

			return sn.iterateThrough(this, sb, tracker);
		}
	},

	COMMON_ROOT {
		@Override
		public StepTransformable apply(StepTransformable sn, SolutionBuilder sb,
				RegroupTracker tracker) {
			if (sn.isOperation(Operation.MULTIPLY)) {
				StepOperation so = (StepOperation) sn;

				int rootCount = 0;
				double commonRoot = 0;

				StepExpression[] productOperands = new StepExpression[so.noOfOperands()];
				StepExpression[] rootOperands = new StepExpression[so.noOfOperands()];
				for (int i = 0; i < so.noOfOperands(); i++) {
					if (so.getOperand(i).isOperation(Operation.NROOT)) {
						double currentRoot =
								((StepOperation) so.getOperand(i)).getOperand(1).getValue();
						if (isEqual(commonRoot, 0) || isEqual(commonRoot, currentRoot)) {
							commonRoot = currentRoot;
							rootOperands[i] = (((StepOperation) so.getOperand(i)).getOperand(0));
							rootCount++;
						} else {
							productOperands[i] = so.getOperand(i);
						}
					} else {
						productOperands[i] = so.getOperand(i);
					}
				}

				StepExpression newProduct = StepOperation.multiply(productOperands);
				StepExpression underRoot = StepOperation.multiply(rootOperands);

				if (rootCount > 1) {
					for (StepExpression operand : so) {
						if (operand.isOperation(Operation.NROOT)) {
							double currentRoot = ((StepOperation) operand).getOperand(1).getValue();
							if (isEqual(commonRoot, currentRoot)) {
								operand.setColor(tracker.getColorTracker());
							}
						}
					}

					StepExpression result = root(underRoot, commonRoot);
					result.setColor(tracker.getColorTracker());

					sb.add(SolutionStepType.PRODUCT_OF_ROOTS, tracker.incColorTracker());

					return multiply(newProduct, result);
				}
			}

			return sn.iterateThrough(this, sb, tracker);
		}
	},

	SQUARE_ROOT_MULTIPLIED_BY_ITSELF {
		@Override
		public StepTransformable apply(StepTransformable sn, SolutionBuilder sb,
				RegroupTracker tracker) {
			if (sn.isOperation(Operation.MULTIPLY)) {
				StepOperation so = (StepOperation) sn;

				StepExpression[] operands = new StepExpression[so.noOfOperands()];

				for (int i = 0; i < so.noOfOperands(); i++) {
					operands[i] = so.getOperand(i);
				}

				for (int i = 0; i < so.noOfOperands(); i++) {
					if (operands[i] != null && so.getOperand(i).isSquareRoot()) {
						for (int j = i + 1; j < so.noOfOperands(); j++) {
							if (so.getOperand(i).equals(so.getOperand(j))) {
								StepExpression result =
										((StepOperation) so.getOperand(i)).getOperand(0).deepCopy();

								so.getOperand(i).setColor(tracker.getColorTracker());
								so.getOperand(j).setColor(tracker.getColorTracker());
								result.setColor(tracker.getColorTracker());

								sb.add(SolutionStepType.SQUARE_ROOT_MULTIPLIED_BY_ITSELF,
										tracker.incColorTracker());

								operands[i] = result;
								operands[j] = null;

								break;
							}
						}
					}
				}

				return StepOperation.multiply(operands);
			}

			return sn.iterateThrough(this, sb, tracker);
		}
	},

	DOUBLE_MINUS {
		@Override
		public StepTransformable apply(StepTransformable sn, SolutionBuilder sb,
				RegroupTracker tracker) {
			if (sn.isOperation(Operation.MINUS)) {
				StepOperation so = (StepOperation) sn;

				if (so.getOperand(0).isNegative()) {
					StepExpression result = so.getOperand(0).negate();
					so.setColor(tracker.getColorTracker());
					result.setColor(tracker.getColorTracker());
					sb.add(SolutionStepType.DOUBLE_MINUS, tracker.incColorTracker());

					return result;
				}
			}

			return sn.iterateThrough(this, sb, tracker);
		}
	},

	DISTRIBUTE_POWER_OVER_FRACION {
		@Override
		public StepTransformable apply(StepTransformable sn, SolutionBuilder sb,
				RegroupTracker tracker) {
			if (sn.isOperation(Operation.POWER)) {
				StepOperation so = (StepOperation) sn;

				if (so.getOperand(0).isOperation(Operation.DIVIDE)) {
					StepExpression exponent = so.getOperand(1);
					StepOperation fraction = (StepOperation) so.getOperand(0);

					StepExpression numerator = power(fraction.getOperand(0), exponent);
					StepExpression denominator = power(fraction.getOperand(1), exponent);

					StepExpression result = divide(numerator, denominator);
					so.setColor(tracker.getColorTracker());
					result.setColor(tracker.getColorTracker());
					sb.add(SolutionStepType.DISTRIBUTE_POWER_FRAC, tracker.incColorTracker());

					return result;
				}
			}

			return sn.iterateThrough(this, sb, tracker);
		}
	},

	DISTRIBUTE_ROOT_OVER_FRACTION {
		@Override
		public StepTransformable apply(StepTransformable sn, SolutionBuilder sb,
				RegroupTracker tracker) {
			if (sn.isOperation(Operation.NROOT)) {
				StepOperation so = (StepOperation) sn;

				if (so.getOperand(0).isOperation(Operation.DIVIDE)) {
					StepExpression exponent = so.getOperand(1);
					StepOperation fraction = (StepOperation) so.getOperand(0);

					StepExpression numerator = root(fraction.getOperand(0), exponent);
					StepExpression denominator = root(fraction.getOperand(1), exponent);

					StepExpression result = divide(numerator, denominator);
					so.setColor(tracker.getColorTracker());
					result.setColor(tracker.getColorTracker());
					sb.add(SolutionStepType.DISTRIBUTE_ROOT_FRAC, tracker.incColorTracker());

					return result;
				}
			}

			return sn.iterateThrough(this, sb, tracker);
		}
	},

	RATIONALIZE_SIMPLE_DENOMINATOR {
		@Override
		public StepTransformable apply(StepTransformable sn, SolutionBuilder sb,
				RegroupTracker tracker) {
			if (sn.isOperation(Operation.DIVIDE)) {
				StepOperation so = (StepOperation) sn;

				List<StepOperation> irrationals = new ArrayList<>();

				if (so.getOperand(1).isOperation(Operation.NROOT)) {
					irrationals.add((StepOperation) so.getOperand(1));
				}

				if (so.getOperand(1).isOperation(Operation.MULTIPLY)) {
					for (StepExpression operand : (StepOperation) so.getOperand(1)) {
						if (operand.isOperation(Operation.NROOT)) {
							irrationals.add((StepOperation) operand);
						}
					}
				}

				StepExpression[] operands = new StepExpression[irrationals.size()];
				for (int i = 0; i < irrationals.size(); i++) {
					StepExpression root = irrationals.get(i).getOperand(1);
					StepExpression argument = irrationals.get(i).getOperand(0);

					StepExpression power = null;
					if (argument.isOperation(Operation.POWER)) {
						power = ((StepOperation) argument).getOperand(1);
						argument = ((StepOperation) argument).getOperand(0);
					}

					if ((power == null || power.isInteger()) && root.isInteger()) {
						double powerVal = power == null ? 1 : power.getValue();
						double rootVal = root.getValue();
						double newPower = rootVal - (powerVal % rootVal);

						operands[i] = root(nonTrivialPower(argument, newPower), root);
					}
				}

				StepExpression toMultiply = StepOperation.multiply(operands);

				if (toMultiply != null) {
					toMultiply.setColor(tracker.incColorTracker());

					StepExpression numerator = nonTrivialProduct(so.getOperand(0), toMultiply);
					StepExpression denominator = multiply(so.getOperand(1), toMultiply);

					StepExpression result = divide(numerator, denominator);
					sb.add(SolutionStepType.MULTIPLY_NUM_DENOM, toMultiply);

					return result;
				}
			}

			return sn.iterateThrough(this, sb, tracker);
		}
	},

	RATIONALIZE_COMPLEX_DENOMINATOR {
		@Override
		public StepTransformable apply(StepTransformable sn, SolutionBuilder sb,
				RegroupTracker tracker) {
			if (sn.isOperation(Operation.DIVIDE)) {
				StepOperation so = (StepOperation) sn;

				if (so.getOperand(1).isOperation(Operation.PLUS)) {
					StepOperation sum = (StepOperation) so.getOperand(1);

					if (sum.noOfOperands() == 2 && (sum.getOperand(0).containsSquareRoot()
							|| sum.getOperand(1).containsSquareRoot())) {
						StepExpression toMultiply;

						if (sum.getOperand(0).isNegative()) {
							toMultiply = add(sum.getOperand(0).negate(), sum.getOperand(1))
									.deepCopy();
						} else {
							toMultiply = add(sum.getOperand(0), sum.getOperand(1).negate())
									.deepCopy();
						}

						toMultiply.setColor(tracker.incColorTracker());

						StepExpression numerator = nonTrivialProduct(so.getOperand(0), toMultiply);
						StepExpression denominator = multiply(so.getOperand(1), toMultiply)
								.deepCopy();

						StepExpression result = divide(numerator, denominator);
						sb.add(SolutionStepType.MULTIPLY_NUM_DENOM, toMultiply);

						tracker.addMark(denominator, RegroupTracker.MarkType.EXPAND);
						return result;
					}
				}
			}

			return sn.iterateThrough(this, sb, tracker);
		}
	},

	DISTRIBUTE_MINUS {
		@Override
		public StepTransformable apply(StepTransformable sn, SolutionBuilder sb,
				RegroupTracker tracker) {
			if (sn.isOperation(Operation.MINUS)) {
				StepOperation so = (StepOperation) sn;

				if (so.getOperand(0).isOperation(Operation.PLUS)) {
					StepOperation sum = (StepOperation) so.getOperand(0);

					StepExpression[] operands = new StepExpression[sum.noOfOperands()];
					for (int i = 0; i < sum.noOfOperands(); i++) {
						operands[i] = sum.getOperand(i).negate();
					}
					StepOperation result = new StepOperation(Operation.PLUS, operands);

					so.setColor(tracker.getColorTracker());
					result.setColor(tracker.getColorTracker());
					sb.add(SolutionStepType.DISTRIBUTE_MINUS, tracker.incColorTracker());

					return result;
				}
			}

			return sn.iterateThrough(this, sb, tracker);
		}
	},

	REWRITE_INTEGER_UNDER_ROOT {
		@Override
		public StepTransformable apply(StepTransformable sn, SolutionBuilder sb,
				RegroupTracker tracker) {
			if (sn.isOperation(Operation.NROOT)) {
				StepOperation so = (StepOperation) sn;

				StepExpression underRoot = so.getOperand(0);
				StepExpression root = so.getOperand(1);

				List<StepExpression> irrationals = new ArrayList<>();

				if (underRoot.isOperation(Operation.MULTIPLY)) {
					for (StepExpression operand : (StepOperation) underRoot) {
						irrationals.add(operand);
					}
				} else {
					irrationals.add(underRoot);
				}

				long rootVal = Math.round(root.getValue());

				StepExpression[] newOperands = new StepExpression[irrationals.size()];
				for (int i = 0; i < irrationals.size(); i++) {
					if (irrationals.get(i).isInteger() && !irrationals.get(i).isNegative()) {
						long power = getIntegerPower(Math.round(irrationals.get(i).getValue()));
						long gcd = gcd(rootVal, power);

						long newPower = 0;
						if (gcd > 1 && irrationals.size() == 1) {
							newPower = gcd;
						} else if (power >= rootVal) {
							newPower = power;
						}

						if (newPower != 0) {
							newOperands[i] = power(StepConstant.create(Math
									.pow(irrationals.get(i).getValue(), ((double) 1) / newPower)),
									newPower);

							irrationals.get(i).setColor(tracker.getColorTracker());
							newOperands[i].setColor(tracker.incColorTracker());

							sb.add(SolutionStepType.REWRITE_AS, irrationals.get(i), newOperands[i]);
							continue;
						}

						long newCoefficient = largestNthPower(irrationals.get(i), rootVal);

						if (newCoefficient != 1) {
							StepExpression firstPart = StepConstant.create(newCoefficient);
							StepExpression secondPart = StepConstant
									.create(irrationals.get(i).getValue() / newCoefficient);

							firstPart.setColor(tracker.getColorTracker());
							secondPart.setColor(tracker.incColorTracker());

							newOperands[i] = multiply(firstPart, secondPart);

							sb.add(SolutionStepType.REWRITE_AS, irrationals.get(i), newOperands[i]);
							continue;
						}
					}

					newOperands[i] = irrationals.get(i);
				}

				StepExpression newRoot = root(StepOperation.multiply(newOperands), root);

				if (!so.equals(newRoot)) {
					return newRoot;
				}
			}

			return sn.iterateThrough(this, sb, tracker);
		}
	},

	REWRITE_ROOT_UNDER_POWER {
		@Override
		public StepTransformable apply(StepTransformable sn, SolutionBuilder sb,
				RegroupTracker tracker) {
			if (sn.isOperation(Operation.POWER)
					&& ((StepOperation) sn).getOperand(0).isOperation(Operation.NROOT)) {
				StepOperation power = (StepOperation) sn;
				StepOperation root = (StepOperation) power.getOperand(0);

				StepExpression quotient = power.getOperand(1).quotient(root.getOperand(1));
				StepExpression remainder = power.getOperand(1).remainder(root.getOperand(1));

				if (!isZero(quotient) && !isZero(remainder)) {
					StepExpression newPower = nonTrivialProduct(quotient, root.getOperand(1));
					StepExpression result = multiply(nonTrivialPower(root, newPower),
							nonTrivialPower(root, remainder));

					sn.setColor(tracker.getColorTracker());
					result.setColor(tracker.incColorTracker());

					sb.add(SolutionStepType.REWRITE_AS, sn, result);
					return result;
				}
			}

			return sn.iterateThrough(this, sb, tracker);
		}
	},

	REWRITE_POWER_UNDER_ROOT {
		@Override
		public StepTransformable apply(StepTransformable sn, SolutionBuilder sb,
				RegroupTracker tracker) {
			if (sn.isOperation(Operation.NROOT)) {
				StepOperation so = (StepOperation) sn;

				StepExpression underRoot = so.getOperand(0);
				StepExpression root = so.getOperand(1);

				List<StepExpression> irrationals = new ArrayList<>();

				if (underRoot.isOperation(Operation.MULTIPLY)) {
					for (StepExpression operand : (StepOperation) underRoot) {
						irrationals.add(operand);
					}
				} else {
					irrationals.add(underRoot);
				}

				StepExpression newRoot = null;
				for (StepExpression irrational : irrationals) {
					if (irrational.isOperation(Operation.POWER)) {
						StepExpression underPower = ((StepOperation) irrational).getOperand(0);
						StepExpression quotient = irrational.getPower().quotient(root);
						StepExpression remainder = irrational.getPower().remainder(root);

						if (!isZero(quotient) && !isZero(remainder)) {
							StepExpression firstPart =
									power(underPower, nonTrivialProduct(root, quotient));
							StepExpression secondPart = nonTrivialPower(underPower, remainder);

							irrational.setColor(tracker.getColorTracker());
							firstPart.setColor(tracker.getColorTracker());
							secondPart.setColor(tracker.incColorTracker());

							newRoot = multiply(newRoot, multiply(firstPart, secondPart));

							sb.add(SolutionStepType.SPLIT_POWERS, irrational,
									multiply(firstPart, secondPart));
							continue;
						}
					}

					newRoot = multiply(newRoot, irrational);
				}

				newRoot = root(newRoot, root);

				if (!so.equals(newRoot)) {
					return newRoot;
				}
			}

			return sn.iterateThrough(this, sb, tracker);
		}
	},

	SPLIT_ROOTS {
		@Override
		public StepTransformable apply(StepTransformable sn, SolutionBuilder sb,
				RegroupTracker tracker) {
			if (sn.isOperation(Operation.NROOT)) {
				StepOperation so = (StepOperation) sn;

				StepExpression underRoot = so.getOperand(0);
				StepExpression root = so.getOperand(1);

				List<StepExpression> irrationals = new ArrayList<>();

				if (underRoot.isOperation(Operation.MULTIPLY)) {
					for (StepExpression operand : (StepOperation) underRoot) {
						irrationals.add(operand);
					}
				} else {
					irrationals.add(underRoot);
				}

				List<StepExpression> newRoot = new ArrayList<>();
				List<StepExpression> separateRoots = new ArrayList<>();

				for (StepExpression irrational : irrationals) {
					if (irrational.isOperation(Operation.POWER)) {
						StepExpression remainder = irrational.getPower().remainder(root);

						if (isZero(remainder)) {
							separateRoots.add(irrational);
							continue;
						}
					}

					newRoot.add(irrational);
				}

				if (separateRoots.size() > 1 || !newRoot.isEmpty() && !separateRoots.isEmpty()) {
					for (int i = 0; i < separateRoots.size(); i++) {
						separateRoots.get(i).setColor(tracker.incColorTracker());
						separateRoots.set(i, root(separateRoots.get(i), root));
					}

					StepExpression result = multiply(
							StepOperation.multiply(separateRoots),
							root(StepOperation.multiply(newRoot), root));

					sb.add(SolutionStepType.SPLIT_ROOTS);
					return result;
				}
			}

			return sn.iterateThrough(this, sb, tracker);
		}
	},

	REGROUP_SUMS {
		@Override
		public StepTransformable apply(StepTransformable sn, SolutionBuilder sb,
				RegroupTracker tracker) {
			if (sn.isOperation(Operation.PLUS)) {
				int colorsAtStart = tracker.getColorTracker();

				StepTransformable tempResult = regroupSums((StepOperation) sn, sb, tracker, false);
				if (colorsAtStart != tracker.getColorTracker()) {
					return tempResult;
				}

				tempResult = regroupSums((StepOperation) sn, sb, tracker, true);
				if (colorsAtStart != tracker.getColorTracker()) {
					return tempResult;
				}
			}

			return sn.iterateThrough(this, sb, tracker);
		}

		private StepTransformable regroupSums(StepOperation so, SolutionBuilder sb,
				RegroupTracker tracker, boolean integer) {
			StepExpression[] coefficients = new StepExpression[so.noOfOperands()];
			StepExpression[] variables = new StepExpression[so.noOfOperands()];
			for (int i = 0; i < so.noOfOperands(); i++) {
				if (integer) {
					coefficients[i] = so.getOperand(i).getIntegerCoefficient();
					variables[i] = so.getOperand(i).getNonInteger();
				} else {
					coefficients[i] = so.getOperand(i).getCoefficient();
					variables[i] = so.getOperand(i).getVariable();
				}
			}

			boolean[] used = new boolean[so.noOfOperands()];

			List<StepExpression> constantList = new ArrayList<>();
			double constantSum = 0;
			for (int i = 0; i < so.noOfOperands(); i++) {
				if (coefficients[i] != null && variables[i] == null
						&& (coefficients[i].nonSpecialConstant() || tracker.isDecimalSimplify()
								&& coefficients[i].specialConstant())) {
					constantList.add(coefficients[i]);
					constantSum += coefficients[i].getValue();
					used[i] = true;
				}
			}

			for (int i = 0; i < so.noOfOperands(); i++) {
				if (!used[i] && variables[i] != null) {
					boolean foundCommon = false;
					for (int j = i + 1; j < so.noOfOperands(); j++) {
						if (!used[j] && variables[i].equals(variables[j])) {
							foundCommon = true;
							so.getOperand(j).setColor(tracker.getColorTracker());

							coefficients[i] = coefficients[i] == null ? StepConstant.create(1)
									: coefficients[i];
							coefficients[j] = coefficients[j] == null ? StepConstant.create(1)
									: coefficients[j];

							coefficients[i] = add(coefficients[i], coefficients[j]);
							used[j] = true;
						}
					}
					if (foundCommon) {
						so.getOperand(i).setColor(tracker.getColorTracker());
						coefficients[i].setColor(tracker.getColorTracker());
						variables[i].setColor(tracker.getColorTracker());
						sb.add(SolutionStepType.COLLECT_LIKE_TERMS, variables[i]);
						tracker.incColorTracker();
					}
				}
			}

			StepExpression[] newSum = new StepExpression[so.noOfOperands() + 1];
			for (int i = 0; i < so.noOfOperands(); i++) {
				if (!used[i]) {
					newSum[i] = simplifiedProduct(coefficients[i], variables[i]);
				}
			}

			StepExpression newConstants = StepConstant.create(constantSum);
			if (constantList.size() > 1) {
				for (StepExpression constant : constantList) {
					constant.setColor(tracker.getColorTracker());
				}
				sb.add(SolutionStepType.ADD_CONSTANTS, tracker.getColorTracker());
				newConstants.setColor(tracker.getColorTracker());
				tracker.incColorTracker();
			}

			if (isEqual(constantSum, 0) && constantList.size() == 1) {
				constantList.get(0).setColor(tracker.getColorTracker());
				sb.add(SolutionStepType.ZERO_IN_ADDITION, tracker.incColorTracker());
			}

			if (!isEqual(constantSum, 0)) {
				newSum[so.noOfOperands()] = newConstants;
			}

			StepExpression result = StepOperation.add(newSum);
			return result == null ? StepConstant.create(0) : result;
		}
	},

	MULTIPLY_NEGATIVES {
		@Override
		public StepTransformable apply(StepTransformable sn, SolutionBuilder sb,
				RegroupTracker tracker) {
			if (sn.isOperation(Operation.MULTIPLY)) {
				StepOperation so = (StepOperation) sn;

				int negativeCount = 0;
				StepExpression result = null;

				for (StepExpression operand : so) {
					if (operand.isNegative()) {
						negativeCount++;
						result = multiply(result, operand.negate());
					} else {
						result = multiply(result, operand);
					}
				}

				if (negativeCount == 0) {
					return sn.iterateThrough(this, sb, tracker);
				}

				sn.setColor(tracker.getColorTracker());
				result.setColor(tracker.getColorTracker());

				if (negativeCount % 2 == 0) {
					sb.add(SolutionStepType.EVEN_NUMBER_OF_NEGATIVES, tracker.incColorTracker());
					return result;
				}

				sb.add(SolutionStepType.ODD_NUMBER_OF_NEGATIVES, tracker.incColorTracker());
				return minus(result);
			}

			return sn.iterateThrough(this, sb, tracker);
		}
	},

	COLLECT_LIKE_TERMS_PRODUCT {
		@Override
		public StepTransformable apply(StepTransformable sn, SolutionBuilder sb,
				RegroupTracker tracker) {
			if (sn.isOperation(Operation.MULTIPLY)) {
				int colorsAtStart = tracker.getColorTracker();

				StepOperation so = (StepOperation) sn;

				List<StepExpression> bases = new ArrayList<>();
				List<StepExpression> exponents = new ArrayList<>();

				so.getBasesAndExponents(bases, exponents);

				for (int i = 0; i < bases.size(); i++) {
					if (exponents.get(i) == null) {
						continue;
					}

					boolean foundCommon = false;
					for (int j = i + 1; j < bases.size(); j++) {
						if (exponents.get(j) == null) {
							continue;
						}

						if (bases.get(i).equals(bases.get(j))) {
							foundCommon = true;
							bases.get(j).setColor(tracker.getColorTracker());

							exponents.set(i, add(exponents.get(i), exponents.get(j)));
							exponents.set(j, null);
						}
					}

					if (foundCommon) {
						bases.get(i).setColor(tracker.getColorTracker());
						exponents.get(i).setColor(tracker.incColorTracker());

						sb.add(SolutionStepType.REGROUP_PRODUCTS, bases.get(i));
					}
				}

				StepExpression newProduct = null;
				for (int i = 0; i < bases.size(); i++) {
					if (exponents.get(i) != null) {
						newProduct = multiply(newProduct,
								nonTrivialPower(bases.get(i), exponents.get(i)));
					}
				}

				if (tracker.getColorTracker() > colorsAtStart) {
					return newProduct;
				}
			}

			return sn.iterateThrough(this, sb, tracker);
		}
	},

	MULTIPLIED_BY_ZERO {
		@Override
		public StepTransformable apply(StepTransformable sn, SolutionBuilder sb,
				RegroupTracker tracker) {
			if (sn.isOperation(Operation.MULTIPLY)) {
				StepOperation so = (StepOperation) sn;

				for (StepExpression operand : so) {
					if (isZero(operand)) {
						StepExpression result = StepConstant.create(0);

						so.setColor(tracker.getColorTracker());
						result.setColor(tracker.getColorTracker());

						sb.add(SolutionStepType.MULTIPLIED_BY_ZERO, tracker.incColorTracker());
						return result;
					}
				}
			}

			return sn.iterateThrough(this, sb, tracker);
		}
	},

	REWRITE_AS_EXPONENTIAL {
		@Override
		public StepTransformable apply(StepTransformable sn, SolutionBuilder sb,
				RegroupTracker tracker) {
			if (sn.isOperation(Operation.MULTIPLY)) {
				StepOperation so = (StepOperation) sn;

				List<StepExpression> bases = new ArrayList<>();
				List<StepExpression> exponents = new ArrayList<>();

				so.getBasesAndExponents(bases, exponents);

				List<StepExpression> basesToConvert = new ArrayList<>();
				for (int i = 0; i < bases.size(); i++) {
					for (int j = i + 1; j < bases.size(); j++) {
						if (bases.get(i).isInteger() && bases.get(j).isInteger()
								&& (!isEqual(exponents.get(i), 1)
										|| !isEqual(exponents.get(j), 1))) {
							long valA = Math.round(bases.get(i).getValue());
							long valB = Math.round(bases.get(j).getValue());

							double baseA = Math.pow(valA, 1.0 / getIntegerPower(valA));
							double baseB = Math.pow(valB, 1.0 / getIntegerPower(valB));

							if (isEqual(baseA, baseB)) {
								if (!isEqual(bases.get(i), baseA)) {
									basesToConvert.add(bases.get(i));
								}

								if (!isEqual(bases.get(j), baseB)) {
									basesToConvert.add(bases.get(j));
								}
							}
						}
					}
				}

				if (basesToConvert.isEmpty()) {
					return sn;
				}

				for (int i = 0; i < bases.size(); i++) {
					if (basesToConvert.contains(bases.get(i))) {
						long value = Math.round(bases.get(i).getValue());
						double exponent = getIntegerPower(value);
						double base = Math.pow(value, 1 / exponent);

						StepExpression result = power(StepConstant.create(base), exponent);

						bases.get(i).setColor(tracker.getColorTracker());
						result.setColor(tracker.incColorTracker());

						sb.add(SolutionStepType.REWRITE_AS, bases.get(i), result);

						bases.set(i, result);
					}
				}

				StepExpression result = null;
				for (int i = 0; i < bases.size(); i++) {
					result = multiply(result, nonTrivialPower(bases.get(i), exponents.get(i)));
				}

				return result;
			}

			return sn.iterateThrough(this, sb, tracker);
		}
	},

	MULTIPLY_CONSTANTS {
		@Override
		public StepTransformable apply(StepTransformable sn, SolutionBuilder sb,
				RegroupTracker tracker) {
			if (sn.isOperation(Operation.NROOT)) {
				StepOperation so = (StepOperation) sn;
				return root((StepExpression) multiplyConstants(so.getOperand(0), sb,
						tracker, true), so.getOperand(1));
			}

			return multiplyConstants(sn, sb, tracker, false);
		}

		private StepTransformable multiplyConstants(StepTransformable sn, SolutionBuilder sb,
				RegroupTracker tracker, boolean underRoot) {
			if (sn.isOperation(Operation.MULTIPLY)) {
				StepOperation so = (StepOperation) sn;

				List<StepExpression> constantList = new ArrayList<>();

				double constantValue = 1;
				StepExpression[] nonConstants = new StepExpression[so.noOfOperands()];
				for (int i = 0; i < so.noOfOperands(); i++) {
					StepExpression operand = so.getOperand(i);
					if (operand.nonSpecialConstant()) {
						constantList.add(operand);
						constantValue *= operand.getValue();
					} else {
						nonConstants[i] = operand;
					}
				}

				if (constantList.size() == 1 && isEqual(constantValue, 1)) {
					constantList.get(0).setColor(tracker.getColorTracker());
					sb.add(SolutionStepType.MULTIPLIED_BY_ONE, tracker.incColorTracker());

					return StepOperation.multiply(nonConstants);
				}

				if (constantList.size() < 2) {
					return sn.iterateThrough(this, sb, tracker);
				}

				/*
				If the expression is under root, then we shouldn't regroup 3*3 to 9, because
				3^2 is probably better. Also, 2*2^3 should be 2^4
				 */
				List<StepExpression> bases = new ArrayList<>();
				List<StepExpression> exponents = new ArrayList<>();
				so.getBasesAndExponents(bases, exponents);

				for (int i = 0; i < bases.size() ; i++) {
					for (int j = i + 1; j < bases.size(); j++) {
						if (bases.get(i).equals(bases.get(j))) {
							if (underRoot || !isEqual(exponents.get(i), 1)
									|| !isEqual(exponents.get(j), 1)) {
								return sn;
							}
						}
					}
				}

				StepExpression newConstant = StepConstant.create(Math.abs(constantValue));

				if (constantList.size() > 1) {
					for (StepExpression constant : constantList) {
						constant.setColor(tracker.getColorTracker());
					}

					sb.add(SolutionStepType.MULTIPLY_CONSTANTS, tracker.getColorTracker());
					newConstant.setColor(tracker.incColorTracker());
				}

				if (constantValue < 0) {
					return minus(multiply(newConstant, StepOperation.multiply(nonConstants)));
				} else {
					return multiply(newConstant, StepOperation.multiply(nonConstants));
				}
			}

			return sn.iterateThrough(this, sb, tracker);
		}
	},

	DISTRIBUTE_POWER_OVER_PRODUCT {
		@Override
		public StepTransformable apply(StepTransformable sn, SolutionBuilder sb,
				RegroupTracker tracker) {
			if (sn.isOperation(Operation.POWER)) {
				StepOperation so = (StepOperation) sn;

				if (so.getOperand(0).isOperation(Operation.MULTIPLY)) {
					StepOperation product = (StepOperation) so.getOperand(0);
					so.getOperand(1).setColor(tracker.incColorTracker());

					StepExpression[] result = new StepExpression[product.noOfOperands()];
					for (int i = 0; i < product.noOfOperands(); i++) {
						product.getOperand(i).setColor(tracker.incColorTracker());
						result[i] = power(product.getOperand(i), so.getOperand(1));
					}

					sb.add(SolutionStepType.DISTRIBUTE_POWER_OVER_PRODUCT);

					return new StepOperation(Operation.MULTIPLY, result);
				}
			}

			return sn.iterateThrough(this, sb, tracker);
		}
	},

	POWER_OF_NEGATIVE {
		@Override
		public StepTransformable apply(StepTransformable sn, SolutionBuilder sb,
				RegroupTracker tracker) {
			if (sn.isOperation(Operation.POWER)) {
				StepOperation so = (StepOperation) sn;

				if (so.getOperand(0).isNegative()) {
					if (so.getOperand(1).isEven()) {
						StepExpression result = power(so.getOperand(0).negate(), so.getOperand(1));

						so.setColor(tracker.getColorTracker());
						result.setColor(tracker.getColorTracker());
						sb.add(SolutionStepType.EVEN_POWER_NEGATIVE, tracker.incColorTracker());

						return result;
					} else if (isOdd(so.getOperand(1))) {
						StepExpression result =
								power(so.getOperand(0).negate(), so.getOperand(1)).negate();

						so.setColor(tracker.getColorTracker());
						result.setColor(tracker.getColorTracker());
						sb.add(SolutionStepType.ODD_POWER_NEGATIVE, tracker.incColorTracker());

						return result;
					}
				}
			}

			return sn.iterateThrough(this, sb, tracker);
		}
	},

	SIMPLIFY_POWER_OF_ROOT {
		@Override
		public StepTransformable apply(StepTransformable sn, SolutionBuilder sb,
				RegroupTracker tracker) {
			if (sn.isOperation(Operation.POWER)
					&& ((StepOperation) sn).getOperand(0).isOperation(Operation.NROOT)) {
				StepOperation so = (StepOperation) sn;

				StepExpression exponent1 = so.getOperand(1);
				StepExpression exponent2 = ((StepOperation) so.getOperand(0)).getOperand(1);

				StepExpression gcd = StepHelper.weakGCD(exponent1, exponent2);

				if (!isOne(gcd)) {
					exponent1 = exponent1.quotient(gcd);
					exponent2 = exponent2.quotient(gcd);

					gcd.setColor(tracker.getColorTracker());

					StepExpression argument = ((StepOperation) so.getOperand(0)).getOperand(0);

					StepExpression result =
							nonTrivialPower(nonTrivialRoot(argument, exponent2), exponent1);
					sb.add(SolutionStepType.REDUCE_ROOT_AND_POWER, gcd);

					so.setColor(tracker.getColorTracker());
					result.setColor(tracker.incColorTracker());
					return result;
				}
			}

			return sn.iterateThrough(this, sb, tracker);
		}
	},

	SIMPLIFY_ROOT_OF_POWER {
		@Override
		public StepTransformable apply(StepTransformable sn, SolutionBuilder sb,
				RegroupTracker tracker) {
			if (sn.isOperation(Operation.NROOT)
					&& ((StepOperation) sn).getOperand(0).isOperation(Operation.POWER)) {
				StepOperation so = (StepOperation) sn;

				StepExpression exponent1 = so.getOperand(1);
				StepExpression exponent2 = ((StepOperation) so.getOperand(0)).getOperand(1);

				StepExpression gcd = StepHelper.weakGCD(exponent1, exponent2);

				if (!isOne(gcd)) {
					exponent1 = exponent1.quotient(gcd);
					exponent2 = exponent2.quotient(gcd);

					gcd.setColor(tracker.getColorTracker());

					StepExpression argument = ((StepOperation) so.getOperand(0)).getOperand(0);

					StepExpression result;

					if (gcd.isEven() && (exponent2 == null || !exponent2.isEven())
							&& !(argument.canBeEvaluated() && argument.getValue() > 0)) {
						result = nonTrivialRoot(nonTrivialPower(abs(argument), exponent2),
								exponent1);
						sb.add(SolutionStepType.REDUCE_ROOT_AND_POWER_EVEN, gcd);
					} else {
						result = nonTrivialRoot(nonTrivialPower(argument, exponent2), exponent1);
						sb.add(SolutionStepType.REDUCE_ROOT_AND_POWER, gcd);
					}

					so.setColor(tracker.getColorTracker());
					result.setColor(tracker.incColorTracker());
					return result;
				}
			}

			return sn.iterateThrough(this, sb, tracker);
		}
	},

	POWER_OF_POWER {
		@Override
		public StepTransformable apply(StepTransformable sn, SolutionBuilder sb,
				RegroupTracker tracker) {
			if (sn.isOperation(Operation.POWER)) {
				StepOperation so = (StepOperation) sn;

				if (so.getOperand(0).isOperation(Operation.POWER)) {
					StepExpression result = power(((StepOperation) so.getOperand(0)).getOperand(0),
							multiply(so.getOperand(1),
									((StepOperation) so.getOperand(0)).getOperand(1)));

					so.setColor(tracker.getColorTracker());
					result.setColor(tracker.getColorTracker());
					sb.add(SolutionStepType.POWER_OF_POWER, tracker.incColorTracker());

					return result;
				}
			}

			return sn.iterateThrough(this, sb, tracker);
		}
	},

	ROOT_OF_ROOT {
		@Override
		public StepTransformable apply(StepTransformable sn, SolutionBuilder sb,
				RegroupTracker tracker) {
			if (sn.isOperation(Operation.NROOT)) {
				StepOperation so = (StepOperation) sn;

				if (so.getOperand(0).isOperation(Operation.NROOT)) {
					StepExpression result = root(((StepOperation) so.getOperand(0)).getOperand(0),
							multiply(so.getOperand(1),
									((StepOperation) so.getOperand(0)).getOperand(1)));

					so.setColor(tracker.getColorTracker());
					result.setColor(tracker.getColorTracker());
					sb.add(SolutionStepType.ROOT_OF_ROOT, tracker.incColorTracker());

					return result;
				}
			}

			return sn.iterateThrough(this, sb, tracker);
		}
	},

	TRIVIAL_POWERS {
		@Override
		public StepTransformable apply(StepTransformable sn, SolutionBuilder sb,
				RegroupTracker tracker) {
			if (sn.isOperation(Operation.POWER)) {
				StepOperation so = (StepOperation) sn;

				if (isEqual(so.getOperand(1), 0)) {
					StepExpression result = StepConstant.create(1);

					so.setColor(tracker.getColorTracker());
					result.setColor(tracker.getColorTracker());
					sb.add(SolutionStepType.ZEROTH_POWER, tracker.incColorTracker());

					return result;
				}

				if (isEqual(so.getOperand(1), 1)) {
					StepExpression result = so.getOperand(0);

					so.setColor(tracker.getColorTracker());
					result.setColor(tracker.getColorTracker());
					sb.add(SolutionStepType.FIRST_POWER, tracker.incColorTracker());

					return result;
				}
			}

			return sn.iterateThrough(this, sb, tracker);
		}
	},

	SIMPLE_POWERS {
		@Override
		public StepTransformable apply(StepTransformable sn, SolutionBuilder sb,
				RegroupTracker tracker) {
			if (sn.isOperation(Operation.POWER)) {
				StepOperation so = (StepOperation) sn;

				if (so.getOperand(1).isNegative()) {
					StepExpression result = divide(1, nonTrivialPower(so.getOperand(0),
							so.getOperand(1).negate()));

					so.setColor(tracker.getColorTracker());
					result.setColor(tracker.getColorTracker());
					sb.add(SolutionStepType.NEGATIVE_POWER, tracker.incColorTracker());

					return result;
				}

				if (so.getOperand(0).nonSpecialConstant() && so.getOperand(1).isInteger()) {
					StepExpression result = StepConstant.create(Math
							.pow(so.getOperand(0).getValue(), so.getOperand(1).getValue()));

					so.setColor(tracker.getColorTracker());
					result.setColor(tracker.getColorTracker());
					sb.add(SolutionStepType.EVALUATE_POWER, tracker.incColorTracker());

					return result;
				}
			}

			return sn.iterateThrough(this, sb, tracker);
		}
	},

	SIMPLE_ROOTS {
		@Override
		public StepTransformable apply(StepTransformable sn, SolutionBuilder sb,
				RegroupTracker tracker) {
			if (sn.isOperation(Operation.NROOT)) {
				StepOperation so = (StepOperation) sn;

				if (so.getOperand(1).isEven() && so.getOperand(0).nonSpecialConstant()
						&& so.getOperand(0).isNegative()) {
					StepExpression result = StepConstant.UNDEFINED;

					so.setColor(tracker.getColorTracker());
					result.setColor(tracker.getColorTracker());
					sb.add(SolutionStepType.EVEN_ROOT_OF_NEGATIVE, tracker.incColorTracker());

					return result;
				}

				if (isEqual(so.getOperand(0), 1)) {
					StepExpression result = StepConstant.create(1);

					so.setColor(tracker.getColorTracker());
					result.setColor(tracker.getColorTracker());
					sb.add(SolutionStepType.ROOT_OF_ONE, tracker.incColorTracker());

					return result;
				}

				if (isEqual(so.getOperand(1), 1)) {
					StepExpression result = so.getOperand(0);

					so.setColor(tracker.getColorTracker());
					result.setColor(tracker.getColorTracker());
					sb.add(SolutionStepType.FIRST_ROOT, tracker.incColorTracker());

					return result;
				}

				if (isOdd(so.getOperand(1).getValue()) && so.getOperand(0).isNegative()) {
					StepExpression result =
							minus(root(so.getOperand(0).negate(), so.getOperand(1)));

					so.setColor(tracker.getColorTracker());
					result.setColor(tracker.getColorTracker());
					sb.add(SolutionStepType.ODD_ROOT_OF_NEGATIVE, tracker.incColorTracker());

					return result;
				}
			}

			return sn.iterateThrough(this, sb, tracker);
		}
	},

	ABSOLUTE_VALUE_OF_POSITIVE {
		@Override
		public StepTransformable apply(StepTransformable sn, SolutionBuilder sb,
				RegroupTracker tracker) {
			if (sn.isOperation(Operation.ABS)) {
				StepOperation so = (StepOperation) sn;

				if (so.getOperand(0).sign() > 0) {
					so.setColor(tracker.getColorTracker());

					sb.add(SolutionStepType.POSITIVE_UNDER_ABSOLUTE_VALUE,
							tracker.incColorTracker());
					return so.getOperand(0);
				}
			}

			return sn.iterateThrough(this, sb, tracker);
		}
	},

	ABSOLUTE_VALUE_OF_NEGATIVE {
		@Override
		public StepTransformable apply(StepTransformable sn, SolutionBuilder sb,
				RegroupTracker tracker) {
			if (sn.isOperation(Operation.ABS)) {
				StepOperation so = (StepOperation) sn;

				if (so.getOperand(0).isNegative()) {
					so.setColor(tracker.getColorTracker());

					sb.add(SolutionStepType.NEGATIVE_UNDER_ABSOLUTE_VALUE,
							tracker.incColorTracker());
					return so.getOperand(0).negate();
				}
			}

			return sn.iterateThrough(this, sb, tracker);
		}
	},

	EVEN_POWER_OF_ABSOLUTE_VALUE {
		@Override
		public StepTransformable apply(StepTransformable sn, SolutionBuilder sb,
				RegroupTracker tracker) {
			if (sn.isOperation(Operation.POWER)) {
				StepOperation so = (StepOperation) sn;

				if (so.getOperand(0).isOperation(Operation.ABS) && so.getOperand(1).isEven()) {
					so.setColor(tracker.getColorTracker());

					sb.add(SolutionStepType.EVEN_POWER_OF_ABSOLUTE_VALUE,
							tracker.incColorTracker());
					return power(((StepOperation) so.getOperand(0)).getOperand(0),
							so.getOperand(1));
				}
			}

			return sn.iterateThrough(this, sb, tracker);
		}
	},

	SIMPLIFY_ABSOLUTE_VALUES {
		@Override
		public StepTransformable apply(StepTransformable sn, SolutionBuilder sb,
				RegroupTracker tracker) {
			SimplificationStepGenerator[] strategy = new SimplificationStepGenerator[] {
					ABSOLUTE_VALUE_OF_POSITIVE,
					ABSOLUTE_VALUE_OF_NEGATIVE,
					EVEN_POWER_OF_ABSOLUTE_VALUE
			};

			if (sn.contains(Operation.ABS)) {
				return StepStrategies.implementGroup(sn, null, strategy, sb, tracker);
			}

			return sn;
		}
	},

	REGROUP_PRODUCTS {
		@Override
		public StepTransformable apply(StepTransformable sn, SolutionBuilder sb,
				RegroupTracker tracker) {
			SimplificationStepGenerator[] strategy = new SimplificationStepGenerator[] {
					MULTIPLIED_BY_ZERO,
					MULTIPLY_NEGATIVES,
					MULTIPLY_CONSTANTS,
					COLLECT_LIKE_TERMS_PRODUCT,
			};

			return StepStrategies.implementGroup(sn, null, strategy, sb, tracker);
		}
	},

	SIMPLIFY_POWERS {
		@Override
		public StepTransformable apply(StepTransformable sn, SolutionBuilder sb,
				RegroupTracker tracker) {
			SimplificationStepGenerator[] strategy = new SimplificationStepGenerator[] {
					TRIVIAL_POWERS,
					POWER_OF_NEGATIVE,
					POWER_OF_POWER,
					DISTRIBUTE_POWER_OVER_PRODUCT,
					DISTRIBUTE_POWER_OVER_FRACION,
			};

			if (sn.contains(Operation.POWER)) {
				return StepStrategies.implementGroup(sn, null, strategy, sb, tracker);
			}

			return sn;
		}
	},

	SIMPLIFY_ROOTS {
		@Override
		public StepTransformable apply(StepTransformable sn, SolutionBuilder sb,
				RegroupTracker tracker) {
			SimplificationStepGenerator[] strategy = new SimplificationStepGenerator[] {
					DISTRIBUTE_ROOT_OVER_FRACTION,
					REWRITE_ROOT_UNDER_POWER,
					ROOT_OF_ROOT,
					REWRITE_INTEGER_UNDER_ROOT,
					REWRITE_POWER_UNDER_ROOT,
					SPLIT_ROOTS,
					SIMPLIFY_POWER_OF_ROOT,
					SIMPLIFY_ROOT_OF_POWER,
					SIMPLE_ROOTS,
			};

			if (sn.contains(Operation.NROOT)) {
				return StepStrategies.implementGroup(sn, null, strategy, sb, tracker);
			}

			return sn;
		}
	},

	RATIONALIZE_DENOMINATORS {
		@Override
		public StepTransformable apply(StepTransformable sn, SolutionBuilder sb,
				RegroupTracker tracker) {
			SimplificationStepGenerator[] strategy = new SimplificationStepGenerator[]{
					POWER_OF_NEGATIVE,
					SQUARE_ROOT_MULTIPLIED_BY_ITSELF,
					COMMON_ROOT,
					DISTRIBUTE_POWER_OVER_PRODUCT,
					REGROUP_SUMS,
					REGROUP_PRODUCTS,
					SIMPLIFY_ROOTS,
					TRIVIAL_POWERS,
					ExpandSteps.EXPAND_DIFFERENCE_OF_SQUARES,
					ExpandSteps.EXPAND_MARKED_PRODUCTS,
					RATIONALIZE_SIMPLE_DENOMINATOR,
					RATIONALIZE_COMPLEX_DENOMINATOR
			};

			if (sn.contains(Operation.DIVIDE)) {
				return StepStrategies.implementGroup(sn, SolutionStepType.RATIONALIZE_DENOMINATOR,
						strategy, sb, tracker);
			}

			return sn;
		}
	},

	WEAK_REGROUP {
		private Map<StepTransformable, StepStrategies.CacheEntry> cache = new HashMap<>();

		@Override
		public StepTransformable apply(StepTransformable sn, SolutionBuilder sb,
				RegroupTracker tracker) {
			SimplificationStepGenerator[] weakStrategy = new SimplificationStepGenerator[] {
					RegroupSteps.ELIMINATE_OPPOSITES,
					RegroupSteps.DOUBLE_MINUS,
					RegroupSteps.DISTRIBUTE_MINUS,
					RegroupSteps.REGROUP_SUMS,
					RegroupSteps.REGROUP_PRODUCTS,
					RegroupSteps.EXPONENTIAL_OF_LOG,
			};

			return StepStrategies.implementCachedGroup(cache, sn, null, weakStrategy,
					sb, tracker);
		}
	},

	DECIMAL_REGROUP {
		@Override
		public StepTransformable apply(StepTransformable sn, SolutionBuilder sb,
				RegroupTracker tracker) {
			SimplificationStepGenerator[] evaluateStrategy = new SimplificationStepGenerator[] {
					RegroupSteps.WEAK_REGROUP,
					RegroupSteps.DECIMAL_SIMPLIFY_ROOTS,
					FractionSteps.SPLIT_FRACTIONS,
					FractionSteps.EVALUATE_FRACTIONS,
					RegroupSteps.DEFAULT_REGROUP,
			};

			return StepStrategies
					.implementGroup(sn, null, evaluateStrategy, sb,
							tracker.setDecimalSimplify());
		}
	},

	/*
	Notes for the ordering of the RegroupSteps:
		REGROUP_PRODUCTS < SIMPLE_POWERS, 3^2 * 3^(-1)
		COMMON_FRACTION < REGROUP_PRODUCTS, x * ((x * x) / 2)
	 */
	DEFAULT_REGROUP {
		private Map<StepTransformable, StepStrategies.CacheEntry> cache = new HashMap<>();

		@Override
		public StepTransformable apply(StepTransformable sn, SolutionBuilder sb,
				RegroupTracker tracker) {
			SimplificationStepGenerator[] defaultStrategy = new SimplificationStepGenerator[] {
					RegroupSteps.ELIMINATE_OPPOSITES,
					RegroupSteps.DOUBLE_MINUS,
					RegroupSteps.DISTRIBUTE_MINUS,
					RegroupSteps.REGROUP_SUMS,
					FractionSteps.COMMON_FRACTION,
					RegroupSteps.REWRITE_AS_EXPONENTIAL,
					RegroupSteps.REGROUP_PRODUCTS,
					RegroupSteps.EXPAND_ROOT,
					RegroupSteps.COMMON_ROOT,
					RegroupSteps.SIMPLIFY_ROOTS,
					RegroupSteps.SIMPLIFY_POWERS,
					RegroupSteps.SIMPLE_POWERS,
					FractionSteps.SIMPLIFY_FRACTIONS,
					ExpandSteps.EXPAND_MARKED_PRODUCTS,
					RegroupSteps.RATIONALIZE_DENOMINATORS,
					FractionSteps.ADD_INTEGER_FRACTIONS,
					ExpandSteps.WEAK_EXPAND_PRODUCTS,
					TrigonometricSteps.SIMPLIFY_TRIGONOMETRIC,
					RegroupSteps.SIMPLIFY_ABSOLUTE_VALUES,
					RegroupSteps.EXPONENTIAL_OF_LOG,
			};

			return StepStrategies.implementCachedGroup(cache, sn, null, defaultStrategy,
					sb, tracker);
		}
	},

	SOLVER_DEFAULT {
		private Map<StepTransformable, StepStrategies.CacheEntry> cache = new HashMap<>();

		@Override
		public StepTransformable apply(StepTransformable sn, SolutionBuilder sb,
				RegroupTracker tracker) {
			SimplificationStepGenerator[] defaultStrategy = new SimplificationStepGenerator[] {
					RegroupSteps.ELIMINATE_OPPOSITES,
					RegroupSteps.DOUBLE_MINUS,
					RegroupSteps.DISTRIBUTE_MINUS,
					RegroupSteps.REGROUP_SUMS,
					FractionSteps.COMMON_FRACTION,
					RegroupSteps.REWRITE_AS_EXPONENTIAL,
					RegroupSteps.REGROUP_PRODUCTS,
					RegroupSteps.EXPAND_ROOT,
					RegroupSteps.COMMON_ROOT,
					RegroupSteps.SIMPLIFY_ROOTS,
					RegroupSteps.SIMPLE_POWERS,
					FractionSteps.CANCEL_FRACTION,
					RegroupSteps.RATIONALIZE_DENOMINATORS,
					FractionSteps.ADD_INTEGER_FRACTIONS,
					TrigonometricSteps.SIMPLIFY_TRIGONOMETRIC,
					RegroupSteps.SIMPLIFY_ABSOLUTE_VALUES,
					RegroupSteps.EXPONENTIAL_OF_LOG,
			};

			return StepStrategies.implementCachedGroup(cache, sn, null, defaultStrategy,
					sb, tracker);
		}
	},

	SOLVER_DECIMAL {
		@Override
		public StepTransformable apply(StepTransformable st, SolutionBuilder sb,
				RegroupTracker tracker) {
			SimplificationStepGenerator[] evaluateStrategy = new SimplificationStepGenerator[] {
					RegroupSteps.WEAK_REGROUP,
					RegroupSteps.DECIMAL_SIMPLIFY_ROOTS,
					FractionSteps.SPLIT_FRACTIONS,
					FractionSteps.EVALUATE_FRACTIONS,
					RegroupSteps.SOLVER_DEFAULT,
			};

			return StepStrategies
					.implementGroup(st, null, evaluateStrategy, sb,
							tracker.setDecimalSimplify());
		}
	};

	@Override
	public boolean isGroupType() {
		return this == SIMPLIFY_ROOTS
				|| this == SIMPLIFY_POWERS
				|| this == DEFAULT_REGROUP
				|| this == WEAK_REGROUP
				|| this == RATIONALIZE_DENOMINATORS
				|| this == REGROUP_PRODUCTS
				|| this == SIMPLIFY_ABSOLUTE_VALUES;
	}
}
