package org.geogebra.common.kernel.stepbystep.steps;

import static org.geogebra.common.kernel.stepbystep.steptree.StepExpression.nonTrivialPower;
import static org.geogebra.common.kernel.stepbystep.steptree.StepExpression.nonTrivialProduct;
import static org.geogebra.common.kernel.stepbystep.steptree.StepNode.add;
import static org.geogebra.common.kernel.stepbystep.steptree.StepNode.gcd;
import static org.geogebra.common.kernel.stepbystep.steptree.StepNode.isEqual;
import static org.geogebra.common.kernel.stepbystep.steptree.StepNode.isEven;
import static org.geogebra.common.kernel.stepbystep.steptree.StepNode.minus;
import static org.geogebra.common.kernel.stepbystep.steptree.StepNode.multiply;
import static org.geogebra.common.kernel.stepbystep.steptree.StepNode.power;
import static org.geogebra.common.kernel.stepbystep.steptree.StepNode.subtract;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.geogebra.common.kernel.stepbystep.StepHelper;
import org.geogebra.common.kernel.stepbystep.solution.SolutionBuilder;
import org.geogebra.common.kernel.stepbystep.solution.SolutionStepType;
import org.geogebra.common.kernel.stepbystep.steptree.StepConstant;
import org.geogebra.common.kernel.stepbystep.steptree.StepExpression;
import org.geogebra.common.kernel.stepbystep.steptree.StepNode;
import org.geogebra.common.kernel.stepbystep.steptree.StepOperation;
import org.geogebra.common.kernel.stepbystep.steptree.StepVariable;
import org.geogebra.common.plugin.Operation;

public enum FactorSteps implements SimplificationStepGenerator {

	FACTOR_COMMON {
		@Override
		public StepNode apply(StepNode sn, SolutionBuilder sb, int[] colorTracker) {
			if (sn.isOperation(Operation.PLUS)) {
				StepOperation so = (StepOperation) sn;

				List<StepExpression> commonBases = new ArrayList<StepExpression>();
				List<StepExpression> commonExponents = new ArrayList<StepExpression>();

				StepExpression.getBasesAndExponents(so.getSubTree(0), null, commonBases, commonExponents);

				List<List<StepExpression>> currentBases = new ArrayList<List<StepExpression>>();
				List<List<StepExpression>> currentExponents = new ArrayList<List<StepExpression>>();

				for (int i = 0; i < so.noOfOperands(); i++) {
					currentBases.add(new ArrayList<StepExpression>());
					currentExponents.add(new ArrayList<StepExpression>());

					StepExpression.getBasesAndExponents(so.getSubTree(i), null, currentBases.get(i),
							currentExponents.get(i));

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

					for (int j = 0; j < commonBases.size(); j++) {
						if (!found[j]) {
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
								StepExpression differenceOfPowers = new StepConstant(
										currentExponents.get(i).get(k).getValue() - commonExponents.get(j).getValue());

								currentExponents.get(i).set(k, differenceOfPowers);
								currentBases.get(i).get(k).setColor(tempTracker++);
							}
						}
					}

					StepExpression currentProduct = null;
					for (int j = 0; j < currentBases.get(i).size(); j++) {
						currentProduct = StepExpression.makeFraction(currentProduct, currentBases.get(i).get(j),
								currentExponents.get(i).get(j));
					}

					result.addSubTree(currentProduct);
				}

				int colorsAtStart = colorTracker[0];
				StepExpression common = null;
				for (int i = 0; i < commonBases.size(); i++) {
					if (!isEqual(commonExponents.get(i), 0)) {
						commonBases.get(i).setColor(colorTracker[0]++);
					}
					common = StepExpression.makeFraction(common, commonBases.get(i), commonExponents.get(i));
				}

				if (isEqual(common, 1) || isEqual(common, -1)) {
					colorTracker[0] = colorsAtStart;
					return so;
				}

				sb.add(SolutionStepType.FACTOR_COMMON, common);
				return multiply(common, result);
			}

			return StepStrategies.iterateThrough(this, sn, sb, colorTracker);
		}
	},

	FACTOR_INTEGER {
		@Override
		public StepNode apply(StepNode sn, SolutionBuilder sb, int[] colorTracker) {
			if (sn.isOperation(Operation.PLUS)) {
				StepOperation so = (StepOperation) sn;

				long common = 0;
				boolean allNegative = true;
				StepExpression[] integerParts = new StepExpression[so.noOfOperands() + 1];

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
					StepExpression remainder = new StepConstant(integerParts[i].getValue() / common);
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

			return StepStrategies.iterateThrough(this, sn, sb, colorTracker);
		}
	},

	COMPLETING_THE_SQUARE {
		@Override
		public StepNode apply(StepNode sn, SolutionBuilder sb, int[] colorTracker) {
			if (sn.isOperation(Operation.PLUS)) {
				StepOperation so = (StepOperation) sn;

				if (so.noOfOperands() == 3) {
					StepExpression first = null, second = null, third = null;

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
						return StepStrategies.iterateThrough(this, sn, sb, colorTracker);
					}

					StepExpression b = StepHelper.findCoefficient(second, first.getSquareRoot());

					if (b != null && isEven(b)) {
						double toComplete = third.getValue() - b.getValue() * b.getValue() / 4;

						if (toComplete < 0) {
							StepOperation newSum = new StepOperation(Operation.PLUS);
							newSum.addSubTree(first);
							newSum.addSubTree(second);

							StepExpression asSum = add(new StepConstant(b.getValue() * b.getValue() / 4),
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

			return StepStrategies.iterateThrough(this, sn, sb, colorTracker);
		}
	},

	FACTOR_BINOM_SQUARED {
		@Override
		public StepNode apply(StepNode sn, SolutionBuilder sb, int[] colorTracker) {
			if (sn instanceof StepOperation) {
				StepOperation so = (StepOperation) sn;

				if (so.noOfOperands() >= 3) {
					StepExpression first = null, second = null, third = null;

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
						return StepStrategies.iterateThrough(this, sn, sb, colorTracker);
					}

					StepExpression a = first.getSquareRoot();
					StepExpression b = third.getSquareRoot();
					StepExpression _2ab = multiply(2, multiply(a, b));

					if (isEqual(subtract(second, _2ab).regroup(), 0) || isEqual(add(second, _2ab).regroup(), 0)) {
						boolean negative = isEqual(add(second, _2ab).regroup(), 0);

						if (second.equals(_2ab) || second.equals(_2ab.negate())) {
							if (second.isOperation(Operation.MINUS)) {
								second = ((StepOperation) second).getSubTree(0);
							}

							first.setColor(colorTracker[0]);
							a.setColor(colorTracker[0]);
							((StepOperation) second).getSubTree(1).setColor(colorTracker[0]++);
							third.setColor(colorTracker[0]);
							b.setColor(colorTracker[0]);
							((StepOperation) second).getSubTree(2).setColor(colorTracker[0]++);

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
							newSum.addSubTree(result);
							for (int i = 3; i < so.noOfOperands(); i++) {
								newSum.addSubTree(so.getSubTree(i));
							}
							return newSum;
						}

						second.setColor(colorTracker[0]);
						_2ab.setColor(colorTracker[0]++);

						if (negative) {
							_2ab = _2ab.negate();
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

			return StepStrategies.iterateThrough(this, sn, sb, colorTracker);
		}
	},

	FACTOR_USING_FORMULA {
		@Override
		public StepNode apply(StepNode sn, SolutionBuilder sb, int[] colorTracker) {
			if (sn.isOperation(Operation.PLUS)) {
				StepOperation so = (StepOperation) sn;

				if (so.noOfOperands() == 2 && so.getSubTree(0).isSquare() && so.getSubTree(1).isSquare()) {
					if (!so.getSubTree(0).isNegative() && so.getSubTree(1).isNegative()) {
						StepExpression a = so.getSubTree(0).getSquareRoot();
						StepExpression b = so.getSubTree(1).negate().getSquareRoot();

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

					if (so.getSubTree(0).isNegative() && !so.getSubTree(1).isNegative()) {
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
					StepExpression a = so.getSubTree(0).getCubeRoot();
					StepExpression b = so.getSubTree(1).getCubeRoot();

					StepOperation newProduct = new StepOperation(Operation.MULTIPLY);

					if (!a.isNegative() && !b.isNegative()) {
						so.getSubTree(0).setColor(colorTracker[0]);
						a.setColor(colorTracker[0]++);

						so.getSubTree(1).setColor(colorTracker[0]);
						b.setColor(colorTracker[0]++);

						newProduct.addSubTree(add(a, b));
						newProduct.addSubTree(add(subtract(power(a, 2), multiply(a, b)), power(b, 2)));

						sb.add(SolutionStepType.SUM_OF_CUBES);
						return newProduct;
					} else if (!a.isNegative() && b.isNegative()) {
						StepExpression minusb = b.negate();

						so.getSubTree(0).setColor(colorTracker[0]);
						a.setColor(colorTracker[0]++);

						so.getSubTree(1).setColor(colorTracker[0]);
						minusb.setColor(colorTracker[0]++);

						newProduct.addSubTree(subtract(a, minusb));
						newProduct.addSubTree(add(add(power(a, 2), multiply(a, minusb)), power(minusb, 2)));

						sb.add(SolutionStepType.DIFFERENCE_OF_CUBES_FACTOR);
						return newProduct;
					} else if (a.isNegative() && !b.isNegative()) {
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

			return StepStrategies.iterateThrough(this, sn, sb, colorTracker);
		}
	},

	REORGANIZE_POLYNOMIAL {
		@Override
		public StepNode apply(StepNode sn, SolutionBuilder sb, int[] colorTracker) {
			if (sn.isOperation(Operation.PLUS)) {
				StepOperation so = (StepOperation) sn;


				Set<StepVariable> variableSet = new HashSet<StepVariable>();
				so.getListOfVariables(variableSet);

				StepVariable var = null;
				if (variableSet.size() == 1) {
					var = (StepVariable) variableSet.toArray()[0];
				}

				if (var != null && so.integerCoefficients(var)) {
					StepExpression[] polynomialForm = StepExpression.convertToPolynomial(so, var);
					long[] integerForm = new long[polynomialForm.length];

					if (polynomialForm.length < 3) {
						return StepStrategies.iterateThrough(this, sn, sb, colorTracker);
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

					if (Math.abs(constant) > 100 || Math.abs(highestOrder) > 100) {
						return StepStrategies.iterateThrough(this, sn, sb, colorTracker);
					}

					for (long i = -constant; i <= constant; i++) {
						for (long j = 1; j <= highestOrder; j++) {
							if (i != 0 && constant % i == 0 && highestOrder % j == 0
									&& isEqual(so.getValueAt(var, ((double) i) / j), 0)) {
								StepOperation reorganized = new StepOperation(Operation.PLUS);

								for (int k = polynomialForm.length - 1; k > 0; k--) {
									long coeff = i * integerForm[k] / j;

									reorganized.addSubTree(nonTrivialProduct(integerForm[k], nonTrivialPower(var, k)));
									reorganized
											.addSubTree(nonTrivialProduct(coeff, nonTrivialPower(var, k - 1)).negate());

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

			return StepStrategies.iterateThrough(this, sn, sb, colorTracker);
		}
	},

	FACTOR_POLYNOMIAL {
		@Override
		public StepNode apply(StepNode sn, SolutionBuilder sb, int[] colorTracker) {
			if (sn.isOperation(Operation.PLUS)) {
				StepOperation so = (StepOperation) sn;

				Set<StepVariable> variableSet = new HashSet<StepVariable>();
				so.getListOfVariables(variableSet);

				StepVariable var = null;
				if (variableSet.size() == 1) {
					var = (StepVariable) variableSet.toArray()[0];
				}
				
				if (var != null && so.integerCoefficients(var)) {
					StepExpression[] polynomialForm = StepExpression.convertToPolynomial(so, var);
					long[] integerForm = new long[polynomialForm.length];

					if (polynomialForm.length < 3) {
						return StepStrategies.iterateThrough(this, sn, sb, colorTracker);
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

					if (Math.abs(constant) > 100 || Math.abs(highestOrder) > 100) {
						return StepStrategies.iterateThrough(this, sn, sb, colorTracker);
					}

					for (long i = -constant; i <= constant; i++) {
						for (long j = 1; j <= highestOrder; j++) {
							if (i != 0 && constant % i == 0 && highestOrder % j == 0
									&& isEqual(so.getValueAt(var, ((double) i) / j), 0)) {
								StepOperation factored = new StepOperation(Operation.PLUS);

								StepExpression innerSum = add(nonTrivialProduct(j, var), -i);
								innerSum.setColor(colorTracker[0]++);
								for (int k = polynomialForm.length - 1; k > 0; k--) {
									long coeff = integerForm[k] / j;
									factored.addSubTree(
											multiply(nonTrivialProduct(coeff, nonTrivialPower(var, k - 1)), innerSum));

									integerForm[k - 1] += i * integerForm[k] / j;
								}

								sb.add(SolutionStepType.FACTOR_FROM_PAIR, innerSum);
								return factored;
							}
						}
					}
				}
			}

			return StepStrategies.iterateThrough(this, sn, sb, colorTracker);
		}
	};
}
