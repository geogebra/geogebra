package org.geogebra.common.kernel.arithmetic;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.plugin.Operation;
import org.geogebra.common.util.DoubleUtil;

final class RationalizeFractionAlgo {
	private final Kernel kernel;
	private final ExpressionNode numerator;
	private final ExpressionNode denominator;

	public RationalizeFractionAlgo(Kernel kernel, ExpressionNode numerator,
			ExpressionNode denominator) {
		this.kernel = kernel;
		this.numerator = processUnderSqrts(numerator);
		this.denominator = processUnderSqrts(denominator);
	}

	public ExpressionNode compute() {
		ExpressionNode result = getReducedRoot(doRationalize(), kernel);
		ExpressionNode canceledResult =
				result.isOperation(Operation.DIVIDE) ? cancelGCDs(result, kernel) : result;
		return canceledResult;
	}

	private boolean checkDecimals(ExpressionNode node) {
		return node.inspect(new Inspecting() {
			@Override
			public boolean check(ExpressionValue v) {
				return true;
			}
		});
}


	static boolean isIntegerValue(ExpressionNode node) {
		double v = node.evaluateDouble();
		long rounded = Math.round(v);
		return v - rounded > Kernel.MAX_PRECISION;
	}


	private static ExpressionNode cancelGCDs(ExpressionNode node, Kernel kernel) {
		ExpressionNode numerator = node.getLeftTree();
		ExpressionNode denominator = node.getRightTree();
		boolean numeratorLeaf = numerator.isLeaf();
		boolean denominatorLeaf = denominator.isLeaf();
		if (!numeratorLeaf && denominatorLeaf) {
			ExpressionValue canceled = denominator.getLeft();
			if (numerator.isOperation(Operation.MULTIPLY))  {
				double evalCanceled = canceled.evaluateDouble();
				double evalLeft = numerator.getLeft().evaluateDouble();
				double evalRight = numerator.getRight().evaluateDouble();
				long gcdLeft = Kernel.gcd((long) evalCanceled, (long) evalLeft);
				long gcdRight = Kernel.gcd((long) evalCanceled, (long) evalRight);
				if (DoubleUtil.isEqual(gcdLeft, evalCanceled)) {
					return numerator.getRightTree();
				} else if (gcdLeft != 1) {
					double v = evalLeft / gcdLeft;
					double canceledDominator = denominator.divide(gcdLeft).evaluateDouble();
					return new ExpressionNode(kernel, new MyDouble(kernel, v).wrap()
							.multiplyR(numerator.getRightTree()),
							Operation.DIVIDE, new MyDouble(kernel, canceledDominator)
					);
				} else if (DoubleUtil.isEqual(gcdRight, evalCanceled)) {
					if (DoubleUtil.isEqual(evalCanceled, evalRight)) {
						return numerator.getLeftTree();
					} else {
						double v = evalRight / evalCanceled;
						return new ExpressionNode(kernel, new MyDouble(kernel, v),
								numerator.getOperation(), node.getLeftTree().getLeftTree()
						);
					}
				}
			}
		}
		return node;
	}

	private ExpressionNode doRationalize() {
		if (numerator.isLeaf()) {
			return rationalizeAsLeafNumerator();
		}

		if (bothHaveSquareRoot(numerator, denominator)) {
			return rationalizeAsSquareRootProduct();
		}

		if (numerator.isOperation(Operation.SQRT) || canBeFactorized()) {
			return factorizeOrHandleProduct();
		}

		return rationalizeAsLeafSqrtDenominator();
	}

	private boolean canBeFactorized() {
		return hasTwoTags(numerator) && hasTwoTags(denominator);
	}

	/**
	 * If the fraction can be factorized, it is done here
	 * or if denominator is a product, it is handled here too.
	 *
	 * @return the altered expression described above.
	 */
	private ExpressionNode factorizeOrHandleProduct() {
		for (Operation op: new Operation[]{Operation.MINUS, Operation.PLUS}) {
			if (denominator.isOperation(op)) {
				return doFactorize(denominator);
			}
		}
		if (denominator.isOperation(Operation.MULTIPLY)) {
			return handleProductInDenominator();
		}
		return null;
	}

	private ExpressionNode handleProductInDenominator() {
		ExpressionNode rightOperand = denominator.getRight().wrap();
		Operation rightOperandOperation = rightOperand.getOperation();
		if (hasTwoTags(rightOperand)) {
			ExpressionNode node = doFactorize(denominator.getRightTree());
			return node.divide(denominator.getLeftTree());
		} else {
			ExpressionNode numerator = new ExpressionNode(kernel, denominator.getLeftTree(),
					rightOperandOperation, null);
			ExpressionNode denominator = this.denominator.getLeftTree().getLeftTree()
					.multiply(rightOperand.getLeft());
			return new ExpressionNode(kernel, numerator, Operation.DIVIDE,
					newNumber(denominator));
		}
	}

	private ExpressionValue newNumber(ExpressionNode node) {
		return newNumber(node.evaluateDouble());
	}

	private static boolean hasTwoTags(ExpressionNode node) {
		// isSupported() guarantees that exactly one of the leaves is SQRT by now,
		// so no check is needed here.
		return node.isOperation(Operation.PLUS) || node.isOperation(Operation.MINUS);
	}

	private ExpressionNode rationalizeAsLeafNumerator() {
		if (denominator.isOperation(Operation.SQRT)) {
			ExpressionNode sqrtOf = simplifyUnderSqrt(denominator);
			return new ExpressionNode(kernel,
					numerator.multiplyR(sqrtOf), Operation.DIVIDE, sqrtOf.getLeftTree());
		}
		return factorizeOrHandleProduct();
	}

	private ExpressionNode processUnderSqrts(final ExpressionNode node) {
		node.inspect(new Inspecting() {
			@Override
			public boolean check(ExpressionValue v) {
				if (v.isOperation(Operation.SQRT)) {
					ExpressionNode sqrtOf = simplifyUnderSqrt(node);
					v.wrap().setLeft(sqrtOf.getLeft());
				}
				return true;
			}
		});
		return node;
	}

	private ExpressionNode simplifyUnderSqrt(ExpressionNode node) {
		if (node.getLeft().isLeaf()) {
			return node;
		}
		double underSqrt = node.getLeft().evaluateDouble();
		MyDouble left = newNumber(underSqrt);
		return new ExpressionNode(kernel, left, Operation.SQRT,
				null);
	}

	private ExpressionNode doFactorize(ExpressionNode node) {
		ExpressionNode result = null;
		Operation op = node.getOperation();
		ExpressionNode conjugate = getConjugateFactor(node);
		double newDenominatorValue = node.multiply(conjugate).evaluateDouble();
		if (isOne(newDenominatorValue)) {
			result = new ExpressionNode(kernel, numerator.multiplyR(conjugate));
		} else if (isMinusOne(newDenominatorValue)) {
			ExpressionNode minusConjugate = getMinusConjugate(node, op);
			result = new ExpressionNode(kernel, numerator.multiply(minusConjugate));
		} else if (DoubleUtil.isInteger(newDenominatorValue)) {
			// if new denominator is integer but not 1 or -1

			result = new ExpressionNode(kernel, numerator.multiply(conjugate),
					Operation.DIVIDE, newNumber(newDenominatorValue));
		}
		return getOperandOrder(result);
	}

	private ExpressionNode getMinusConjugate(ExpressionNode node, Operation op) {
		ExpressionNode minusConjugate =
				new ExpressionNode(kernel, node.getLeft().wrap().multiplyR(-1),
						Operation.inverse(op),
						node.getRight().wrap().multiplyR(-1));
		return minusConjugate;
	}

	private static boolean isMinusOne(double newDenominatorValue) {
		return DoubleUtil.isEqual(newDenominatorValue, -1, Kernel.STANDARD_PRECISION);
	}

	private static boolean isOne(double newDenominatorValue) {
		return DoubleUtil.isEqual(newDenominatorValue, 1, Kernel.STANDARD_PRECISION);
	}

	private ExpressionNode getConjugateFactor(ExpressionNode node) {
		return new ExpressionNode(kernel, node.getLeft(), Operation.inverse(node.getOperation()),
				node.getRight());
	}

	/**
	 * if plusMinusNode is in form like "-1 + sqrt(2)", returns "sqrt(2) - 1"
	 * if not, returs the parameter back.
	 *
	 * @param plusMinusNode to check
	 * @return natural ordered + or - expression.
	 */
	private ExpressionNode getOperandOrder(ExpressionNode plusMinusNode) {
		if (plusMinusNode == null) {
			return null;
		}

		ExpressionNode leftTree = plusMinusNode.getLeftTree();
		ExpressionNode operandLeft = leftTree.getLeftTree();
		if (operandLeft.isLeaf() && operandLeft.evaluateDouble() < 0) {
			ExpressionNode operandRight = leftTree.getRightTree();
			return new ExpressionNode(kernel, operandRight.getLeft().evaluateDouble() == -1
					? operandRight.getRightTree()
					: operandRight,
					Operation.inverse(leftTree.getOperation()),
					operandLeft);
		}
		return plusMinusNode;
	}

	private ExpressionNode simplifiedMultiply(ExpressionValue rationalized,
			ExpressionNode node1) {
		return rationalized.equals(node1.getLeft())
				? rationalized.wrap()
				: doMultiply(node1, denominator);
	}

	private ExpressionNode doMultiply(ExpressionNode left, ExpressionNode right) {
		if (bothHaveSquareRoot(left, right)) {
			return multiplySquareRoots(left, right);
		}

		return left.multiply(right);
	}

	private ExpressionNode rationalizeAsSquareRootProduct() {
		ExpressionNode product = multiplySquareRoots(numerator, denominator);
		return new ExpressionNode(kernel,
				product,
				Operation.DIVIDE,
				denominator.getLeft());
	}

	private static boolean bothHaveSquareRoot(ExpressionNode numerator,
			ExpressionNode denominator) {
		return numerator.isOperation(Operation.SQRT) && denominator.isOperation(Operation.SQRT);
	}

	private ExpressionNode multiplySquareRoots(ExpressionNode left, ExpressionNode right) {
		double product = left.getLeftTree().multiply(right.getLeft())
				.wrap().evaluateDouble();

		return new ExpressionNode(kernel, newNumber(product),
						Operation.SQRT, null);
	}

	private ExpressionNode rationalizeAsLeafSqrtDenominator() {
		ExpressionValue rationalized = denominator.getLeft();
		ExpressionNode numeratorLeft =
				simplifiedMultiply(rationalized, numerator.getLeftTree());
		ExpressionNode numeratorRight =
				simplifiedMultiply(rationalized, numerator.getRightTree());
		ExpressionNode newNumerator =
				new ExpressionNode(kernel, numeratorLeft, numerator.getOperation(),
						numeratorRight);
		return new ExpressionNode(kernel,
				newNumerator,
				Operation.DIVIDE, rationalized);
	}

	static ExpressionNode getReducedRoot(ExpressionNode node, Kernel kernel) {
		if (node.isOperation(Operation.DIVIDE)) {
			ExpressionNode numerator = node.getLeftTree();
			if (numerator.isOperation(Operation.SQRT)) {
				ExpressionValue reducedSqrt = Surds.getResolution(numerator, kernel);
				if (reducedSqrt != null) {
					return new ExpressionNode(kernel, reducedSqrt, Operation.DIVIDE,
							node.getRightTree());
				}
			} else if (numerator.isOperation(Operation.MULTIPLY)) {
				ExpressionNode rightTree = numerator.getRightTree();
				ExpressionValue reducedSqrt = Surds.getResolution(rightTree, kernel);
				if (reducedSqrt != null) {
					ExpressionNode constantProduct =
							numerator.getLeftTree().multiplyR(reducedSqrt.wrap().getLeftTree());
					return new ExpressionNode(kernel,
							reducedSqrt.wrap().getRightTree().multiplyR(
									constantProduct.unwrap().evaluateDouble()),
							Operation.DIVIDE,
							node.getRightTree());
				}
			}
		}
		return node;
	}

	private MyDouble newNumber(double outer) {
		return new MyDouble(kernel, outer);
	}
}