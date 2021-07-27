package org.geogebra.common.kernel.stepbystep.steps;

import static org.geogebra.common.kernel.stepbystep.steptree.StepExpression.makeFraction;
import static org.geogebra.common.kernel.stepbystep.steptree.StepExpression.nonTrivialPower;
import static org.geogebra.common.kernel.stepbystep.steptree.StepExpression.nonTrivialProduct;
import static org.geogebra.common.kernel.stepbystep.steptree.StepNode.add;
import static org.geogebra.common.kernel.stepbystep.steptree.StepNode.divide;
import static org.geogebra.common.kernel.stepbystep.steptree.StepNode.gcd;
import static org.geogebra.common.kernel.stepbystep.steptree.StepNode.isEqual;
import static org.geogebra.common.kernel.stepbystep.steptree.StepNode.isOne;
import static org.geogebra.common.kernel.stepbystep.steptree.StepNode.isZero;
import static org.geogebra.common.kernel.stepbystep.steptree.StepNode.minus;
import static org.geogebra.common.kernel.stepbystep.steptree.StepNode.multiply;
import static org.geogebra.common.kernel.stepbystep.steptree.StepNode.subtract;
import static org.geogebra.common.kernel.stepbystep.steptree.StepOperation.add;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.geogebra.common.kernel.stepbystep.StepHelper;
import org.geogebra.common.kernel.stepbystep.solution.SolutionBuilder;
import org.geogebra.common.kernel.stepbystep.solution.SolutionStepType;
import org.geogebra.common.kernel.stepbystep.steptree.StepConstant;
import org.geogebra.common.kernel.stepbystep.steptree.StepExpression;
import org.geogebra.common.kernel.stepbystep.steptree.StepOperation;
import org.geogebra.common.kernel.stepbystep.steptree.StepTransformable;
import org.geogebra.common.plugin.Operation;

enum FractionSteps implements SimplificationStepGenerator {

	SPLIT_FRACTIONS {
		@Override
		public StepTransformable apply(StepTransformable sn, SolutionBuilder sb,
				RegroupTracker tracker) {
			if (sn.isOperation(Operation.DIVIDE)) {
				StepOperation fraction = (StepOperation) sn;
				StepExpression numerator = fraction.getOperand(0);
				StepExpression denominator = fraction.getOperand(1);
				StepExpression numeratorCoefficient = numerator.getIntegerCoefficient();
				StepExpression denominatorCoefficient = denominator.getIntegerCoefficient();
				StepExpression numeratorRemainder = numerator.getNonInteger();
				StepExpression denominatorRemainder = denominator.getNonInteger();

				if (!isOne(denominatorCoefficient)
						&& (!isOne(numeratorRemainder) || !isOne(denominatorRemainder))) {
					StepExpression firstPart = divide(numeratorCoefficient, denominatorCoefficient);
					StepExpression secondPart = divide(numeratorRemainder, denominatorRemainder);
					StepExpression result = multiply(firstPart, secondPart);

					fraction.setColor(tracker.getColorTracker());
					result.setColor(tracker.getColorTracker());

					sb.add(SolutionStepType.SPLIT_FRACTIONS, tracker.incColorTracker());
					return result;
				}
			}

			return sn.iterateThrough(this, sb, tracker);
		}
	},

	EVALUATE_FRACTIONS {
		@Override
		public StepTransformable apply(StepTransformable sn, SolutionBuilder sb,
				RegroupTracker tracker) {
			if (sn.isOperation(Operation.DIVIDE)) {
				StepOperation fraction = (StepOperation) sn;

				if (fraction.getOperand(0).nonSpecialConstant()
						&& fraction.getOperand(1).nonSpecialConstant()) {
					StepExpression result = StepConstant.create(fraction.getValue());

					fraction.setColor(tracker.getColorTracker());
					result.setColor(tracker.getColorTracker());

					sb.add(SolutionStepType.EVALUATE_FRACTION, tracker.incColorTracker());
					return result;
				}
			}

			return sn.iterateThrough(this, sb, tracker);
		}
	},

	CONVERT_DECIMAL_TO_FRACTION_SUBSTEP {
		@Override
		public StepTransformable apply(StepTransformable sn, SolutionBuilder sb,
				RegroupTracker tracker) {
			if (sn instanceof StepExpression) {
				StepExpression se = (StepExpression) sn;

				if (se.nonSpecialConstant() && !se.isInteger()) {
					int decimalLength = Double.toString(se.getValue()).split("\\.")[1].length();

					long numerator = (long) (se.getValue() * Math.pow(10, decimalLength));
					long denominator = (long) Math.pow(10, decimalLength);

					StepExpression result = divide(numerator, denominator);

					se.setColor(tracker.getColorTracker());
					result.setColor(tracker.getColorTracker());

					sb.add(SolutionStepType.REWRITE_DECIMAL_AS_COMMON_FRACTION,
							tracker.incColorTracker());
					return result;
				}
			}

			return sn.iterateThrough(this, sb, tracker);
		}
	},

	CONVERT_DECIMAL_TO_FRACTION {
		@Override
		public StepTransformable apply(StepTransformable sn, SolutionBuilder sb,
				RegroupTracker tracker) {
			SimplificationStepGenerator[] strategy = new SimplificationStepGenerator[] {
					CONVERT_DECIMAL_TO_FRACTION_SUBSTEP,
					CANCEL_INTEGER_FRACTION
			};

			return StepStrategies
					.implementGroup(sn, SolutionStepType.CONVERT_DECIMALS, strategy, sb, tracker);
		}
	},

	FACTOR_FRACTIONS {
		@Override
		public StepTransformable apply(StepTransformable sn, SolutionBuilder sb,
				RegroupTracker tracker) {
			if (sn.isOperation(Operation.DIVIDE)) {
				StepOperation so = (StepOperation) sn;

				SolutionBuilder temp = new SolutionBuilder();

				StepOperation factored = (StepOperation) FactorSteps.FACTOR_STRATEGY
						.apply(so, temp, new RegroupTracker());

				if (!isOne(StepHelper.weakGCD(factored.getOperand(0), factored.getOperand(1)))
						&& !so.equals(factored)) {
					sb.addGroup(SolutionStepType.FACTOR, temp, factored, sn);

					tracker.incColorTracker();
					return factored;
				}
			}

			return sn.iterateThrough(this, sb, tracker);
		}
	},

	CANCEL_INTEGER_FRACTION {
		@Override
		public StepTransformable apply(StepTransformable sn, SolutionBuilder sb,
				RegroupTracker tracker) {
			if (sn.isOperation(Operation.DIVIDE)) {
				StepOperation so = (StepOperation) sn;

				List<StepExpression> basesNumerator = new ArrayList<>();
				List<StepExpression> exponentsNumerator = new ArrayList<>();
				List<StepExpression> basesDenominator = new ArrayList<>();
				List<StepExpression> exponentsDenominator = new ArrayList<>();

				so.getOperand(0).getBasesAndExponents(basesNumerator, exponentsNumerator);
				so.getOperand(1).getBasesAndExponents(basesDenominator, exponentsDenominator);

				boolean changed = false;

				for (int i = 0; i < basesNumerator.size(); i++) {
					if (!isEqual(exponentsNumerator.get(i), 1)
							|| !basesNumerator.get(i).isInteger()) {
						continue;
					}

					for (int j = 0; j < basesDenominator.size(); j++) {
						if (isEqual(exponentsDenominator.get(j), 1)
								&& basesDenominator.get(j).isInteger()) {
							long gcd = gcd(basesNumerator.get(i), basesDenominator.get(j));

							if (gcd > 1) {
								basesNumerator.get(i).setColor(tracker.getColorTracker());
								basesDenominator.get(j).setColor(tracker.getColorTracker());

								basesNumerator.set(i, StepConstant
										.create(basesNumerator.get(i).getValue() / gcd));
								basesDenominator.set(j, StepConstant
										.create(basesDenominator.get(j).getValue() / gcd));

								basesNumerator.get(i).setColor(tracker.getColorTracker());
								basesDenominator.get(j).setColor(tracker.getColorTracker());

								StepExpression toCancel = StepConstant.create(gcd);
								toCancel.setColor(tracker.incColorTracker());
								sb.add(SolutionStepType.CANCEL_FRACTION, toCancel);

								changed = true;

								break;
							}
						}
					}
				}

				if (changed) {
					return makeFraction(basesNumerator, exponentsNumerator,
							basesDenominator, exponentsDenominator);
				}
			}

			return sn.iterateThrough(this, sb, tracker);
		}
	},

	CANCEL_FRACTION {
		@Override
		public StepTransformable apply(StepTransformable sn, SolutionBuilder sb,
				RegroupTracker tracker) {
			if (sn.isOperation(Operation.DIVIDE)) {
				StepOperation so = (StepOperation) sn;

				List<StepExpression> basesNumerator = new ArrayList<>();
				List<StepExpression> exponentsNumerator = new ArrayList<>();
				List<StepExpression> basesDenominator = new ArrayList<>();
				List<StepExpression> exponentsDenominator = new ArrayList<>();

				so.getOperand(0).getBasesAndExponents(basesNumerator, exponentsNumerator);
				so.getOperand(1).getBasesAndExponents(basesDenominator, exponentsDenominator);

				int colorsAtStart = tracker.getColorTracker();

				for (int i = 0; i < basesNumerator.size(); i++) {
					if (isOne(basesNumerator.get(i))) {
						continue;
					}

					for (int j = 0; j < basesDenominator.size(); j++) {
						StepExpression common =
								exponentsNumerator.get(i).getCommon(exponentsDenominator.get(j));

						if (basesNumerator.get(i).equals(basesDenominator.get(j))
								&& !isZero(common)) {
							basesNumerator.get(i).setColor(tracker.getColorTracker());
							exponentsNumerator.get(i).setColor(tracker.getColorTracker());

							basesDenominator.get(j).setColor(tracker.getColorTracker());
							exponentsDenominator.get(j).setColor(tracker.getColorTracker());

							exponentsNumerator.set(i,
									subtract(exponentsNumerator.get(i), common).deepCopy());
							exponentsDenominator.set(j,
									subtract(exponentsDenominator.get(j), common).deepCopy());
							exponentsNumerator.set(i, (StepExpression) StepStrategies.weakRegroup(
									exponentsNumerator.get(i), null));
							exponentsDenominator.set(j, (StepExpression) StepStrategies.weakRegroup(
									exponentsDenominator.get(j), null));

							exponentsNumerator.get(i).setColor(tracker.getColorTracker());
							exponentsDenominator.get(j).setColor(tracker.getColorTracker());

							StepExpression toCancel =
									nonTrivialPower(basesNumerator.get(i), common);
							toCancel.setColor(tracker.incColorTracker());

							sb.add(SolutionStepType.CANCEL_FRACTION, toCancel);
							break;
						}
					}
				}

				if (tracker.getColorTracker() == colorsAtStart) {
					return sn.iterateThrough(this, sb, tracker);
				}

				return makeFraction(basesNumerator, exponentsNumerator, basesDenominator,
						exponentsDenominator);
			}

			return sn.iterateThrough(this, sb, tracker);
		}
	},

	REWRITE_COMPLEX_FRACTIONS {
		@Override
		public StepTransformable apply(StepTransformable sn, SolutionBuilder sb,
				RegroupTracker tracker) {
			if (sn.isOperation(Operation.DIVIDE)) {
				StepOperation so = (StepOperation) sn;

				if (so.getOperand(0).isFraction() || so.getOperand(1).isFraction()) {
					StepExpression result =
							nonTrivialProduct(so.getOperand(0), so.getOperand(1).reciprocate());

					so.setColor(tracker.getColorTracker());
					result.setColor(tracker.getColorTracker());
					sb.add(SolutionStepType.REWRITE_COMPLEX_FRACTION, tracker.incColorTracker());

					return result;
				}
			}

			return sn.iterateThrough(this, sb, tracker);
		}
	},

	TRIVIAL_FRACTIONS {
		@Override
		public StepTransformable apply(StepTransformable sn, SolutionBuilder sb,
				RegroupTracker tracker) {
			if (sn.isOperation(Operation.DIVIDE)) {
				StepOperation so = (StepOperation) sn;

				if (so.getOperand(0).equals(so.getOperand(1))) {
					StepExpression result = StepConstant.create(1);
					so.setColor(tracker.getColorTracker());
					result.setColor(tracker.getColorTracker());

					sb.add(SolutionStepType.DIVIDED_BY_ITSELF, tracker.incColorTracker());
					return result;
				}

				if (so.getOperand(0).isInteger() && so.getOperand(1).isInteger()
						&& so.getOperand(0).integerDivisible(so.getOperand(1))) {
					StepExpression result = so.getOperand(0).quotient(so.getOperand(1));
					so.setColor(tracker.getColorTracker());
					result.setColor(tracker.getColorTracker());

					sb.add(SolutionStepType.EVALUATE_DIVISION, tracker.incColorTracker());
					return result;
				}

				if (isEqual(so.getOperand(0), 0)) {
					StepExpression result = StepConstant.create(0);
					so.setColor(tracker.getColorTracker());
					result.setColor(tracker.getColorTracker());

					sb.add(SolutionStepType.ZERO_DIVIDED, tracker.incColorTracker());
					return result;
				}

				if (isEqual(so.getOperand(1), 0)) {
					StepExpression result = StepConstant.UNDEFINED;
					so.setColor(tracker.getColorTracker());
					result.setColor(tracker.getColorTracker());

					sb.add(SolutionStepType.DIVIDED_BY_ZERO, tracker.incColorTracker());
					return result;
				}

				if (isEqual(so.getOperand(1), 1)) {
					so.getOperand(1).setColor(tracker.getColorTracker());
					sb.add(SolutionStepType.DIVIDE_BY_ONE, tracker.incColorTracker());
					return so.getOperand(0);
				}

				if (isEqual(so.getOperand(1), -1)) {
					so.getOperand(1).setColor(tracker.getColorTracker());
					sb.add(SolutionStepType.DIVIDE_BY_NEGATVE_ONE, tracker.incColorTracker());
					return minus(so.getOperand(0));
				}
			}

			return sn.iterateThrough(this, sb, tracker);
		}
	},

	NEGATIVE_FRACTIONS {
		@Override
		public StepTransformable apply(StepTransformable sn, SolutionBuilder sb,
				RegroupTracker tracker) {
			if (sn.isOperation(Operation.DIVIDE)) {
				StepOperation so = (StepOperation) sn;

				StepExpression result = null;
				if (so.getOperand(0).isNegative() && so.getOperand(1).isNegative()) {
					result = divide(so.getOperand(0).negate(), so.getOperand(1).negate());
					sb.add(SolutionStepType.NEGATIVE_NUM_AND_DENOM, tracker.getColorTracker());
				} else if (so.getOperand(0).isNegative()) {
					result = divide(so.getOperand(0).negate(), so.getOperand(1)).negate();
					sb.add(SolutionStepType.NEGATIVE_NUM_OR_DENOM, tracker.getColorTracker());
				} else if (so.getOperand(1).isNegative()) {
					result = divide(so.getOperand(0), so.getOperand(1).negate()).negate();
					sb.add(SolutionStepType.NEGATIVE_NUM_OR_DENOM, tracker.getColorTracker());
				}

				if (result != null) {
					sn.setColor(tracker.getColorTracker());
					result.setColor(tracker.incColorTracker());
					return result;
				}
			}

			return sn.iterateThrough(this, sb, tracker);
		}
	},

	FACTOR_MINUS_FROM_SUMS {
		@Override
		public StepTransformable apply(StepTransformable sn, SolutionBuilder sb,
				RegroupTracker tracker) {
			if (sn.isOperation(Operation.DIVIDE)) {
				StepOperation so = (StepOperation) sn;

				List<StepExpression> basesNumerator = new ArrayList<>();
				List<StepExpression> exponentsNumerator = new ArrayList<>();
				List<StepExpression> basesDenominator = new ArrayList<>();
				List<StepExpression> exponentsDenominator = new ArrayList<>();

				so.getOperand(0).getBasesAndExponents(basesNumerator, exponentsNumerator);
				so.getOperand(1).getBasesAndExponents(basesDenominator, exponentsDenominator);

				boolean changed = false;

				for (int i = 0; i < basesNumerator.size(); i++) {
					if (!basesNumerator.get(i).isSum()) {
						continue;
					}

					StepOperation sum = (StepOperation) basesNumerator.get(i);
					StepExpression[] operands = new StepExpression[sum.noOfOperands()];
					for (int k = 0; k < sum.noOfOperands(); k++) {
						operands[k] = sum.getOperand(k).negate();
					}
					StepOperation negated = new StepOperation(Operation.PLUS, operands);

					for (StepExpression denominatorBase : basesDenominator) {
						if (denominatorBase.isSum()) {
							if (negated.equals(denominatorBase)) {
								basesNumerator.get(i).setColor(tracker.getColorTracker());
								denominatorBase.setColor(tracker.getColorTracker());
								negated.setColor(tracker.incColorTracker());

								basesNumerator.set(i, negated.negate());

								sb.add(SolutionStepType.FACTOR_MINUS);
								changed = true;
							}
						}
					}
				}

				if (changed) {
					StepExpression[] numerator = new StepExpression[basesNumerator.size()];
					for (int i = 0; i < basesNumerator.size(); i++) {
						numerator[i] =
								nonTrivialPower(basesNumerator.get(i), exponentsNumerator.get(i));
					}

					return divide(new StepOperation(Operation.MULTIPLY, numerator),
							so.getOperand(1));
				}
			}

			return sn.iterateThrough(this, sb, tracker);
		}
	},

	COMMON_FRACTION {
		@Override
		public StepTransformable apply(StepTransformable sn, SolutionBuilder sb,
				RegroupTracker tracker) {
			if (sn.isOperation(Operation.MULTIPLY)) {
				StepOperation so = (StepOperation) sn;

				StepExpression[] numerator = new StepExpression[so.noOfOperands()];
				StepExpression[] denominator = new StepExpression[so.noOfOperands()];

				for (int i = 0; i < so.noOfOperands(); i++) {
					numerator[i] = so.getOperand(i).getNumerator();
					denominator[i] = so.getOperand(i).getDenominator();
				}

				StepExpression result = divide(StepOperation.multiply(numerator),
						StepOperation.multiply(denominator));

				if (!so.equals(result)) {
					so.setColor(tracker.getColorTracker());
					result.setColor(tracker.getColorTracker());
					sb.add(SolutionStepType.COMMON_FRACTION, tracker.incColorTracker());

					return result;
				}
			}

			return sn.iterateThrough(this, sb, tracker);
		}

	},

	EXPAND_FRACTIONS {
		@Override
		public StepTransformable apply(StepTransformable sn, SolutionBuilder sb,
				RegroupTracker tracker) {
			return expandFractions(sn, sb, tracker, false);
		}
	},

	EXPAND_INTEGER_FRACTIONS {
		@Override
		public StepTransformable apply(StepTransformable sn, SolutionBuilder sb,
				RegroupTracker tracker) {
			return expandFractions(sn, sb, tracker, true);
		}
	},

	ADD_NUMERATORS {
		@Override
		public StepTransformable apply(StepTransformable sn, SolutionBuilder sb,
				RegroupTracker tracker) {
			if (sn.isOperation(Operation.PLUS)) {
				StepOperation so = (StepOperation) sn;

				StepExpression[] remainder = new StepExpression[so.noOfOperands()];
				StepExpression[] newNumerator = new StepExpression[so.noOfOperands()];
				StepExpression newDenominator = null;

				List<StepExpression> fractions = new ArrayList<>();
				for (int i = 0; i < so.noOfOperands(); i++) {
					StepExpression operand = so.getOperand(i);
					StepExpression currentNumerator = operand.getNumerator();
					StepExpression currentDenominator = operand.getDenominator();

					if (newDenominator == null && currentDenominator != null) {
						newDenominator = currentDenominator;
					}

					if (currentDenominator != null && currentDenominator.equals(newDenominator)) {
						newNumerator[i] = currentNumerator;
						fractions.add(operand);
					} else {
						remainder[i] = operand;
					}
				}

				if (fractions.size() > 1) {
					int denominatorColor = tracker.incColorTracker();
					for (StepExpression fraction : fractions) {
						fraction.getNumerator().setColor(tracker.incColorTracker());
						fraction.getDenominator().setColor(denominatorColor);
					}

					StepExpression result = divide(add(newNumerator), newDenominator);
					sb.add(SolutionStepType.ADD_NUMERATORS);
					return add(add(remainder), result);
				}
			}

			return sn.iterateThrough(this, sb, tracker);
		}
	},

	SIMPLIFY_FRACTIONS {
		@Override
		public StepTransformable apply(StepTransformable sn, SolutionBuilder sb,
				RegroupTracker tracker) {
			SimplificationStepGenerator[] strategy = new SimplificationStepGenerator[] {
					RegroupSteps.WEAK_REGROUP,
					TRIVIAL_FRACTIONS,
					NEGATIVE_FRACTIONS,
					FACTOR_FRACTIONS,
					FACTOR_MINUS_FROM_SUMS,
					CANCEL_FRACTION,
					CANCEL_INTEGER_FRACTION
			};

			if (sn.contains(Operation.DIVIDE)) {
				return StepStrategies.implementGroup(sn, null, strategy, sb, tracker);
			}

			return sn;
		}
	},

	ADD_FRACTIONS {
        @Override
        public StepTransformable apply(StepTransformable sn, SolutionBuilder sb,
				RegroupTracker tracker) {
            SimplificationStepGenerator[] fractionAddition = new SimplificationStepGenerator[]{
                    RegroupSteps.REGROUP_SUMS,
                    RegroupSteps.REGROUP_PRODUCTS,
                    RegroupSteps.DISTRIBUTE_MINUS,
                    ExpandSteps.EXPAND_MARKED_PRODUCTS,
					FractionSteps.REWRITE_COMPLEX_FRACTIONS,
                    FractionSteps.ADD_NUMERATORS,
                    FractionSteps.EXPAND_FRACTIONS,
                    FractionSteps.SIMPLIFY_FRACTIONS
            };

			if (sn.contains(Operation.DIVIDE)) {
				return StepStrategies.implementGroup(sn, SolutionStepType.ADD_FRACTIONS,
						fractionAddition, sb, tracker);
			}

			return sn;
		}
	},

	ADD_INTEGER_FRACTIONS {
		@Override
		public StepTransformable apply(StepTransformable sn, SolutionBuilder sb,
				RegroupTracker tracker) {
			SimplificationStepGenerator[] fractionAddition = new SimplificationStepGenerator[]{
					RegroupSteps.REGROUP_SUMS,
					RegroupSteps.REGROUP_PRODUCTS,
					RegroupSteps.DISTRIBUTE_MINUS,
					ExpandSteps.EXPAND_MARKED_PRODUCTS,
					FractionSteps.REWRITE_COMPLEX_FRACTIONS,
					FractionSteps.ADD_NUMERATORS,
					FractionSteps.EXPAND_INTEGER_FRACTIONS,
					FractionSteps.SIMPLIFY_FRACTIONS
			};

			if (sn.contains(Operation.DIVIDE)) {
				return StepStrategies.implementGroup(sn, SolutionStepType.ADD_FRACTIONS,
						fractionAddition, sb, tracker);
			}

			return sn;
		}
	};

	@Override
	public boolean isGroupType() {
		return this == FACTOR_FRACTIONS
				|| this == ADD_FRACTIONS
				|| this == ADD_INTEGER_FRACTIONS
				|| this == SIMPLIFY_FRACTIONS
				|| this == CONVERT_DECIMAL_TO_FRACTION;
	}

	StepTransformable expandFractions(StepTransformable sn, SolutionBuilder sb,
			RegroupTracker tracker, boolean integer) {
		StepTransformable temp;

		// if we are in a fraction, add all fractions to get rid of complex fractions
		if (sn.isOperation(Operation.DIVIDE)) {
			temp = sn.iterateThrough(EXPAND_FRACTIONS, sb, tracker);
		} else {
			temp = sn.iterateThrough(this, sb, tracker);
		}

		if (!temp.equals(sn)) {
			return temp;
		}

		if (sn.isOperation(Operation.PLUS)) {
			StepOperation so = (StepOperation) sn;

			StepExpression newDenominator = null;
			for (StepExpression operand : so) {
				if (operand.getDenominator() != null) {
					if (newDenominator == null) {
						newDenominator = operand.getDenominator();
					} else {
						newDenominator = StepHelper.lcm(newDenominator, operand.getDenominator());
					}
				}
			}

			if (isOne(newDenominator) || integer && !newDenominator.isInteger()) {
				return sn.iterateThrough(this, sb, tracker);
			}

			if (integer) {
				markForExpansion(so, tracker);
			}

			int tempTracker = tracker.getColorTracker();
			newDenominator.setColor(tempTracker++);

			StepExpression[] newSum = new StepExpression[so.noOfOperands()];

			boolean wasChanged = false;
			for (int i = 0; i < so.noOfOperands(); i++) {
				StepExpression currentDenominator = so.getOperand(i).getDenominator();
				if ((so.getOperand(i).isFraction() || !integer
						|| tracker.isMarked(so.getOperand(i), RegroupTracker.MarkType.EXPAND_FRAC))
						&& !newDenominator.equals(currentDenominator)) {
					wasChanged = true;

					StepExpression toExpand = newDenominator.quotient(currentDenominator);
					toExpand.setColor(tempTracker++);

					StepExpression oldNumerator;
					if (so.getOperand(i).isNegative()) {
						oldNumerator = so.getOperand(i).negate().getNumerator();
					} else {
						oldNumerator = so.getOperand(i).getNumerator();
					}

					StepExpression numerator = nonTrivialProduct(toExpand, oldNumerator);
					tracker.addMark(numerator, RegroupTracker.MarkType.EXPAND);

					StepExpression newFraction = divide(numerator, newDenominator);

					if (so.getOperand(i).isNegative()) {
						newSum[i] = newFraction.negate();
					} else {
						newSum[i] = newFraction;
					}
				} else {
					newSum[i] = so.getOperand(i);
				}
			}

			if (wasChanged) {
				tracker.setColorTracker(tempTracker);
				sb.add(SolutionStepType.EXPAND_FRACTIONS, newDenominator);

				return new StepOperation(Operation.PLUS, newSum);
			}
		}

		return sn;
	}

	private void markForExpansion(StepOperation so, RegroupTracker tracker) {
		Set<StepExpression> numerators = new HashSet<>();
		boolean foundInteger = false;

		for (StepExpression operand : so) {
			if (operand.isFraction()) {
				StepExpression numerator = operand.getNumerator();
				if (numerator.isNegative()) {
					numerator = numerator.negate();
				}

				if (numerator.isSum()) {
					for (StepExpression operand2 : (StepOperation) numerator) {
						if (operand2.isInteger()) {
							foundInteger = true;
						} else {
							numerators.add(operand2.getVariable());
							numerators.add(operand2.getNonInteger());
						}
					}
				} else {
					if (numerator.isInteger()) {
						foundInteger = true;
					} else {
						numerators.add(numerator.getVariable());
						numerators.add(numerator.getNonInteger());
					}
				}
			}
		}

		for (StepExpression operand : so) {
			if (!operand.isFraction()) {
				if (operand.isInteger()) {
					if (foundInteger) {
						tracker.addMark(operand, RegroupTracker.MarkType.EXPAND_FRAC);
					}
				} else {
					StepExpression value = operand.isNegative() ? operand.negate() : operand;

					if (numerators.contains(value)
							|| numerators.contains(value.getVariable())
							|| numerators.contains(value.getNonInteger())) {
						tracker.addMark(operand, RegroupTracker.MarkType.EXPAND_FRAC);
					}
				}
			}
		}
	}
}
