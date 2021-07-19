package org.geogebra.common.kernel.stepbystep.steps;

import static org.geogebra.common.kernel.stepbystep.steptree.StepExpression.nonTrivialProduct;
import static org.geogebra.common.kernel.stepbystep.steptree.StepExpression.trigoLookup;
import static org.geogebra.common.kernel.stepbystep.steptree.StepNode.add;
import static org.geogebra.common.kernel.stepbystep.steptree.StepNode.applyOp;
import static org.geogebra.common.kernel.stepbystep.steptree.StepNode.cos;
import static org.geogebra.common.kernel.stepbystep.steptree.StepNode.divide;
import static org.geogebra.common.kernel.stepbystep.steptree.StepNode.isOne;
import static org.geogebra.common.kernel.stepbystep.steptree.StepNode.isZero;
import static org.geogebra.common.kernel.stepbystep.steptree.StepNode.minus;
import static org.geogebra.common.kernel.stepbystep.steptree.StepNode.multiply;
import static org.geogebra.common.kernel.stepbystep.steptree.StepNode.sin;
import static org.geogebra.common.kernel.stepbystep.steptree.StepNode.subtract;
import static org.geogebra.common.kernel.stepbystep.steptree.StepNode.tan;

import org.geogebra.common.kernel.stepbystep.solution.SolutionBuilder;
import org.geogebra.common.kernel.stepbystep.solution.SolutionStepType;
import org.geogebra.common.kernel.stepbystep.steptree.StepConstant;
import org.geogebra.common.kernel.stepbystep.steptree.StepExpression;
import org.geogebra.common.kernel.stepbystep.steptree.StepOperation;
import org.geogebra.common.kernel.stepbystep.steptree.StepTransformable;
import org.geogebra.common.plugin.Operation;

public enum TrigonometricSteps implements SimplificationStepGenerator {

	NEGATIVE_ARGUMENT {
		@Override
		public StepTransformable apply(StepTransformable sn, SolutionBuilder sb,
				RegroupTracker tracker) {
			if (sn.isTrigonometric()) {
				StepOperation so = (StepOperation) sn;
				StepExpression argument = so.getOperand(0);
				StepExpression result = null;

				if (argument.isNegative()) {
					switch (so.getOperation()) {
					case SIN:
						result = sin(argument.negate()).negate();
						sb.add(SolutionStepType.TRIGO_ODD_SIN, tracker.getColorTracker());
						break;
					case COS:
						result = cos(argument.negate());
						sb.add(SolutionStepType.TRIGO_EVEN_COS, tracker.getColorTracker());
						break;
					case TAN:
						result = tan(argument.negate()).negate();
						sb.add(SolutionStepType.TRIGO_ODD_TAN, tracker.getColorTracker());
						break;
					default:
						// not trigonometric: contradicts check above
						break;
					}
				}

				if (result != null) {
					so.setColor(tracker.getColorTracker());
					result.setColor(tracker.incColorTracker());
					return result;
				}
			}

			return sn.iterateThrough(this, sb, tracker);
		}
	},

	SPLIT_FRACTIONS_WITH_PI {
		@Override
		public StepTransformable apply(StepTransformable sn, SolutionBuilder sb,
				RegroupTracker tracker) {
			if (sn.isTrigonometric()) {
				StepOperation so = (StepOperation) sn;
				StepExpression argument = so.getOperand(0);

				if (argument.isSum()) {
					StepOperation sum = (StepOperation) argument;
					StepExpression[] results = new StepExpression[sum.noOfOperands()];

					for (int i = 0; i < sum.noOfOperands(); i++) {
						results[i] = splitFraction(sum.getOperand(i), sb, tracker);
					}

					return applyOp(so.getOperation(), StepOperation.add(results));
				} else {
					return applyOp(so.getOperation(), splitFraction(argument, sb, tracker));
				}
			}

			return sn.iterateThrough(this, sb, tracker);
		}

		private StepExpression splitFraction(StepExpression operand, SolutionBuilder sb,
				RegroupTracker tracker) {
			StepExpression numerator = operand
					.getNumerator().findCoefficient(StepConstant.PI);
			StepExpression denominator = operand.getDenominator();

			if (numerator != null && denominator != null
					&& numerator.isInteger() && denominator.isInteger()) {
				double ratio = numerator.getValue() / (denominator.getValue() * 2);
				double value = ratio < 0 ? Math.ceil(ratio) : Math.floor(ratio);

				if (value != 0) {
					StepExpression newNumerator = nonTrivialProduct(
							numerator.getValue() - 2 * value * denominator.getValue(),
							StepConstant.PI);

					StepExpression result = add(divide(newNumerator, denominator),
							nonTrivialProduct(2 * value, StepConstant.PI));

					operand.setColor(tracker.getColorTracker());
					result.setColor(tracker.getColorTracker());
					sb.add(SolutionStepType.SPLIT_FRACTIONS_WITH_PI, tracker.incColorTracker());

					return result;
				}
			}

			return operand;
		}
	},

	REWRITING_RULES {
		@Override
		public StepTransformable apply(StepTransformable sn, SolutionBuilder sb,
				RegroupTracker tracker) {
			if (sn.isTrigonometric() && ((StepOperation) sn).getOperand(0).isSum()) {
				StepOperation so = (StepOperation) sn;
				StepOperation argument = (StepOperation) so.getOperand(0);

				boolean foundPi = false;
				boolean foundPiHalf = false;

				StepExpression pi = StepConstant.PI;
				StepExpression piHalf = divide(pi, 2);

				StepExpression[] results = new StepExpression[argument.noOfOperands()];

				for (int i = 0; i < argument.noOfOperands(); i++) {
					if (!foundPi && !foundPiHalf) {
						if (argument.getOperand(i).equals(pi)) {
							foundPi = true;
							continue;
						}

						if (argument.getOperand(i).equals(piHalf)) {
							foundPiHalf = true;
							continue;
						}
					}

					results[i] = argument.getOperand(i);
				}

				if (foundPi) {
					StepExpression value = StepOperation.add(results);
					StepExpression result;

					switch (so.getOperation()) {
					case SIN:
						if (value.isNegative()) {
							result = sin(value.negate());
							sb.add(SolutionStepType.REWRITE_SIN_PI_NEGATIVE,
									tracker.getColorTracker());
						} else {
							result = minus(sin(value));
							sb.add(SolutionStepType.REWRITE_SIN_PI_POSITIVE,
									tracker.getColorTracker());
						}
					break;
					case COS:
						if (value.isNegative()) {
							result = minus(cos(value.negate()));
							sb.add(SolutionStepType.REWRITE_COS_PI_NEGATIVE,
									tracker.getColorTracker());
						} else {
							result = minus(cos(value));
							sb.add(SolutionStepType.REWRITE_COS_PI_POSITIVE,
									tracker.getColorTracker());
						}
					break;
					default:
						return sn.iterateThrough(this, sb, tracker);
					}

					sn.setColor(tracker.getColorTracker());
					result.setColor(tracker.incColorTracker());

					return result;
				}

				if (foundPiHalf) {
					StepExpression value = StepOperation.add(results);
					StepExpression result;

					switch (so.getOperation()) {
					case SIN:
						if (value.isNegative()) {
							result = cos(value.negate());
							sb.add(SolutionStepType.REWRITE_SIN_PI_HALF_NEGATIVE,
									tracker.getColorTracker());
						} else {
							result = cos(value);
							sb.add(SolutionStepType.REWRITE_SIN_PI_HALF_POSITIVE,
									tracker.getColorTracker());
						}
					break;
					case COS:
						if (value.isNegative()) {
							result = sin(value.negate());
							sb.add(SolutionStepType.REWRITE_COS_PI_HALF_NEGATIVE,
									tracker.getColorTracker());
						} else {
							result = minus(sin(value));
							sb.add(SolutionStepType.REWRITE_COS_PI_HALF_POSITIVE,
									tracker.getColorTracker());
						}
					break;
					case TAN:
						if (value.isNegative()) {
							result = divide(1, tan(value.negate()));
							sb.add(SolutionStepType.REWRITE_TAN_PI_HALF_NEGATIVE,
									tracker.getColorTracker());
						} else {
							result = minus(divide(1, tan(value)));
							sb.add(SolutionStepType.REWRITE_TAN_PI_HALF_POSITIVE,
									tracker.getColorTracker());
						}
					break;
					default:
						return sn.iterateThrough(this, sb, tracker);
					}

					sn.setColor(tracker.getColorTracker());
					result.setColor(tracker.incColorTracker());

					return result;
				}
			}

			return sn.iterateThrough(this, sb, tracker);
		}
	},

	EXTRACT_PERIOD {
		@Override
		public StepTransformable apply(StepTransformable sn, SolutionBuilder sb,
				RegroupTracker tracker) {
			if (sn.isTrigonometric()) {
				StepOperation so = (StepOperation) sn;
				StepExpression argument = so.getOperand(0);

				if (argument.canBeEvaluated()) {
					double value = argument.getValue();

					if (value >= 0 && value <= 2 * Math.PI) {
						return sn.iterateThrough(this, sb, tracker);
					}
				}

				StepExpression period =
						nonTrivialProduct(so.isOperation(Operation.TAN) ? 1 : 2, StepConstant.PI);

				StepExpression remainder = null;
				StepExpression quotient = null;

				if (argument.isSum()) {
					StepOperation sum = (StepOperation) argument;
					StepExpression[] remainders = new StepExpression[sum.noOfOperands()];
					StepExpression[] quotients = new StepExpression[sum.noOfOperands()];

					for (int i = 0; i < sum.noOfOperands(); i++) {
						StepExpression quotient2 = sum.getOperand(i).quotient(period);

						if (quotient2 != null && quotient2.proveInteger()) {
							sum.getOperand(i).setColor(tracker.getColorTracker());
							quotient2.setColor(tracker.getColorTracker());

							quotients[i] = quotient2;
							remainders[i] = sum.getOperand(i).remainder(period);

							if (isZero(remainders[i])) {
								remainders[i] = null;
							}
						} else {
							remainders[i] = sum.getOperand(i);
						}
					}

					remainder = StepOperation.add(remainders);
					quotient = StepOperation.add(quotients);
				} else {
					StepExpression quotient2 = argument.quotient(period);

					if (quotient2 != null && quotient2.proveInteger()) {
						quotient = quotient2;
						remainder = argument.remainder(period);
					}
				}

				if (remainder == null) {
					remainder = StepConstant.create(0);
				}

				if (quotient == null) {
					return sn.iterateThrough(this, sb, tracker);
				}

				if (!isOne(quotient)) {
					period.setColor(tracker.incColorTracker());

					if (so.getOperation() == Operation.TAN) {
						sb.add(SolutionStepType.FACTOR_OUT_PI);
					} else {
						sb.add(SolutionStepType.FACTOR_OUT_2PI);
					}
				}

				StepExpression toMark = nonTrivialProduct(quotient, period);
				tracker.addMark(toMark, RegroupTracker.MarkType.PERIOD);

				return applyOp(so.getOperation(), add(remainder, toMark));
			}

			return sn.iterateThrough(this, sb, tracker);
		}
	},

	ELIMINATE_PERIOD {
		@Override
		public StepTransformable apply(StepTransformable sn, SolutionBuilder sb,
				RegroupTracker tracker) {
			if (sn.isTrigonometric() && ((StepOperation) sn).getOperand(0).isSum()) {
				StepOperation so = (StepOperation) sn;
				StepOperation argument = (StepOperation) so.getOperand(0);

				boolean found = false;

				StepExpression[] results = new StepExpression[argument.noOfOperands()];
				for (int i = 0; i < argument.noOfOperands(); i++) {
					if (!tracker.isMarked(argument.getOperand(i), RegroupTracker.MarkType.PERIOD)) {
						results[i] = argument.getOperand(i);
					} else {
						found = true;
					}
				}

				if (!found) {
					return sn.iterateThrough(this, sb, tracker);
				}

				StepExpression result = applyOp(so.getOperation(), StepOperation.add(results));

				so.setColor(tracker.getColorTracker());
				result.setColor(tracker.getColorTracker());

				if (so.getOperation() == Operation.SIN) {
					sb.add(SolutionStepType.ELIMINATE_THE_PERIOD_SIN, tracker.incColorTracker());
				} else if (so.getOperation() == Operation.COS) {
					sb.add(SolutionStepType.ELIMINATE_THE_PERIOD_COS, tracker.incColorTracker());
				} else {
					sb.add(SolutionStepType.ELIMINATE_THE_PERIOD_TAN, tracker.incColorTracker());
				}

				return result;
			}

			return sn.iterateThrough(this, sb, tracker);
		}
	},

	SUBTRACT_PERIOD {
		@Override
		public StepTransformable apply(StepTransformable sn, SolutionBuilder sb,
				RegroupTracker tracker) {
			if (sn.isTrigonometric()) {
				StepOperation so = (StepOperation) sn;
				StepExpression argument = so.getOperand(0);

				StepExpression period =
						nonTrivialProduct(so.isOperation(Operation.TAN) ? 1 : 2, StepConstant.PI);
				double periodVal = (so.isOperation(Operation.TAN) ? 1 : 2) * Math.PI;

				if (argument.canBeEvaluated()) {
					double value = argument.getValue();

					if (value < 0) {
						tracker.incColorTracker();

						return applyOp(so.getOperation(), add(argument,
								nonTrivialProduct(Math.floor(-value / periodVal) + 1, period)));
					} else if (value > periodVal) {
						tracker.incColorTracker();

						return applyOp(so.getOperation(), subtract(argument,
								nonTrivialProduct(Math.floor(value / periodVal), period)));
					}
				}
			}

			return sn.iterateThrough(this, sb, tracker);
		}
	},

	REDUCE_TO_FIRST_QUADRANT {
		@Override
		public StepTransformable apply(StepTransformable sn, SolutionBuilder sb,
				RegroupTracker tracker) {
			if (sn.isTrigonometric()) {
				StepOperation so = (StepOperation) sn;
				StepExpression argument = so.getOperand(0);

				if (!argument.canBeEvaluated()) {
					return sn.iterateThrough(this, sb, tracker);
				}

				double value = argument.getValue();

				if (value < Math.PI / 2 || value > 2 * Math.PI) {
					return sn.iterateThrough(this, sb, tracker);
				}

				StepExpression result = null;

				switch (so.getOperation()) {
				case SIN:
					if (Math.PI / 2 < value && value <= Math.PI) {
						sb.add(SolutionStepType.REDUCE_TO_FRIST_QUADRANT_SIN_II,
								tracker.getColorTracker());
						result = sin(subtract(StepConstant.PI, argument));
					} else if (Math.PI < value && value <= 3 * Math.PI / 2) {
						sb.add(SolutionStepType.REDUCE_TO_FRIST_QUADRANT_SIN_III,
								tracker.getColorTracker());
						result = minus(sin(subtract(argument, StepConstant.PI)));
					} else if (3 * Math.PI / 2 < value && value < 2 * Math.PI) {
						sb.add(SolutionStepType.REDUCE_TO_FRIST_QUADRANT_SIN_IV,
								tracker.getColorTracker());
						result = minus(sin(subtract(multiply(2, StepConstant.PI), argument)));
					}
					break;
				case COS:
					if (Math.PI / 2 < value && value <= Math.PI) {
						sb.add(SolutionStepType.REDUCE_TO_FRIST_QUADRANT_COS_II,
								tracker.getColorTracker());
						result = minus(cos(subtract(StepConstant.PI, argument)));
					} else if (Math.PI < value && value <= 3 * Math.PI / 2) {
						sb.add(SolutionStepType.REDUCE_TO_FRIST_QUADRANT_COS_III,
								tracker.getColorTracker());
						result = minus(cos(subtract(argument, StepConstant.PI)));
					} else if (3 * Math.PI / 2 < value && value < 2 * Math.PI) {
						sb.add(SolutionStepType.REDUCE_TO_FRIST_QUADRANT_COS_IV,
								tracker.getColorTracker());
						result = cos(subtract(multiply(2, StepConstant.PI), argument));
					}
					break;
				case TAN:
					if (Math.PI / 2 < value && value <= Math.PI) {
						sb.add(SolutionStepType.REDUCE_TO_FRIST_QUADRANT_TAN,
								tracker.getColorTracker());
						result = minus(applyOp(Operation.TAN, subtract(StepConstant.PI, argument)));
					}
					break;
				default:
					return sn.iterateThrough(this, sb, tracker);
				}

				if (result != null) {
					so.setColor(tracker.getColorTracker());
					result.setColor(tracker.incColorTracker());
					return result;
				}
			}

			return sn.iterateThrough(this, sb, tracker);
		}
	},

	EVALUATE_TRIGONOMETRIC {
		@Override
		public StepTransformable apply(StepTransformable sn, SolutionBuilder sb,
				RegroupTracker tracker) {
			if (sn.isTrigonometric() || sn.isInverseTrigonometric()) {
				StepOperation so = (StepOperation) sn;

				StepExpression value = trigoLookup(so);
				if (value != null) {
					so.setColor(tracker.getColorTracker());
					value.setColor(tracker.getColorTracker());
					if (so.isTrigonometric()) {
						sb.add(SolutionStepType.EVALUATE_TRIGO, tracker.incColorTracker());
					} else {
						sb.add(SolutionStepType.EVALUATE_INVERSE_TRIGO, tracker.incColorTracker());
					}
					return value;
				}
			}

			return sn.iterateThrough(this, sb, tracker);
		}
	},

	REDUCE_TO_FIRST_QUADRANT_SS {
		@Override
		public StepTransformable apply(StepTransformable sn, SolutionBuilder sb,
				RegroupTracker tracker) {
			SimplificationStepGenerator[] strategy = new SimplificationStepGenerator[] {
					FractionSteps.ADD_INTEGER_FRACTIONS,
					TrigonometricSteps.REDUCE_TO_FIRST_QUADRANT,
			};

			return StepStrategies
					.implementGroup(sn, null, strategy, sb, tracker);
		}
	},

	SIMPLIFY_TRIGONOMETRIC {
		@Override
		public StepTransformable apply(StepTransformable sn, SolutionBuilder sb,
				RegroupTracker tracker) {
			SimplificationStepGenerator[] strategy = new SimplificationStepGenerator[] {
					TrigonometricSteps.NEGATIVE_ARGUMENT,
					TrigonometricSteps.EVALUATE_TRIGONOMETRIC,
					TrigonometricSteps.ELIMINATE_PERIOD,
					TrigonometricSteps.EXTRACT_PERIOD,
					TrigonometricSteps.REWRITING_RULES,
					TrigonometricSteps.SPLIT_FRACTIONS_WITH_PI,
					TrigonometricSteps.SUBTRACT_PERIOD,
					TrigonometricSteps.REDUCE_TO_FIRST_QUADRANT_SS,
			};

			return StepStrategies
					.implementGroup(sn, null, strategy, sb, tracker);
		}
	};

	@Override
	public boolean isGroupType() {
		return this == SIMPLIFY_TRIGONOMETRIC
				|| this == REDUCE_TO_FIRST_QUADRANT_SS;
	}
}
