
package org.geogebra.common.kernel.arithmetic.simplifiers;

import static org.geogebra.common.kernel.arithmetic.simplifiers.SimplifyUtils.isIntegerValue;

import javax.annotation.Nonnull;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.ExpressionValue;
import org.geogebra.common.kernel.arithmetic.MyDouble;
import org.geogebra.common.kernel.arithmetic.NumberValue;
import org.geogebra.common.plugin.Operation;
import org.geogebra.common.util.DoubleUtil;

public final class RationalizeFractionAlgo {
	private final SimplifyUtils utils;
	private final ExpressionNode numerator;
	private final ExpressionNode denominator;

	/**
	 *
	 * @param utils {@link SimplifyUtils}
	 * @param numerator of the fraction.
	 * @param denominator of the fraction.
	 */
	public RationalizeFractionAlgo(@Nonnull SimplifyUtils utils,
			@Nonnull ExpressionNode numerator,
			@Nonnull ExpressionNode denominator) {
		this.utils = utils;
		this.numerator = utils.deepCopy(numerator);
		this.denominator = utils.deepCopy(denominator);
	}

	/**
	 * Run the rationalization algorithm.
	 * If node is turned to be unsupported during the algorithm (division by 0, not integer result
	 * numbers, etc.), it returns null.
	 *
	 * @return the rationalized node or null
	 */
	public ExpressionNode compute() {
		ExpressionNode node = doRationalize();
		if (node == null) {
			return null;
		}
		return checkDecimals(node) ? null : node;
	}

	/**
	 * Package private to be testable in isolation
	 *
	 * @param node to test
	 * @return if the expression has decimal numbers in it.
	 */
	static boolean checkDecimals(ExpressionNode node) {
		return node.inspect(v -> v instanceof NumberValue && !isIntegerValue(v));
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
		ExpressionNode expanded = utils.expand(denominator);
		ExpressionNode rightOperand = expanded;
		Operation rightOperandOperation = rightOperand.getOperation();
		if (hasTwoTags(rightOperand)) {
			return doFactorize(expanded);
		} if (rightOperandOperation == Operation.SQRT) {
			ExpressionNode sqrt = denominator.getRightTree();
			return utils.newNode(this.numerator.multiplyR(sqrt), Operation.DIVIDE,
					denominator.getLeftTree().multiplyR(sqrt.getLeft()));
		} else {
			ExpressionNode numerator = utils.newNode(expanded.getLeftTree(), rightOperandOperation,
					null);
			ExpressionNode denominator = expanded.getLeftTree().getLeftTree()
					.multiply(rightOperand.getLeft());
			return utils.newDiv(numerator, utils.newDouble(denominator.evaluateDouble()).wrap());
		}
	}

	private static boolean hasTwoTags(ExpressionNode node) {
		// isSupported() guarantees that exactly one of the leaves is SQRT by now,
		// so no check is needed here.
		return node.isOperation(Operation.PLUS) || node.isOperation(Operation.MINUS);
	}

	private ExpressionNode rationalizeAsLeafNumerator() {
		if (denominator.isOperation(Operation.SQRT)) {
			ExpressionNode sqrtOf = simplifyUnderSqrt(denominator);
			return utils.div(numerator.multiplyR(sqrtOf), sqrtOf.getLeftTree());
		}
		return factorizeOrHandleProduct();
	}

	static ExpressionNode processUnderSqrts(final ExpressionNode node) {
		ReduceRoot reduceRoot = new ReduceRoot(new SimplifyUtils(node.getKernel()));
		return reduceRoot.apply(node);
	}

	private static ExpressionNode simplifyUnderSqrt(ExpressionNode node) {
		if (node.getLeft().isLeaf()) {
			return node;
		}
		double underSqrt = node.getLeft().evaluateDouble();
		Kernel kernel = node.getKernel();
		MyDouble left = new MyDouble(kernel, underSqrt);
		return new ExpressionNode(kernel, left, Operation.SQRT,
				null);
	}

	private ExpressionNode doFactorize(ExpressionNode node) {
		ExpressionNode result = null;
		Operation op = node.getOperation();
		ExpressionNode conjugate = getConjugateFactor(node);
		double newDenominatorValue = node.multiply(conjugate).evaluateDouble();
		if (isOne(newDenominatorValue)) {
			result = utils.multiplyR(numerator, conjugate);
		} else if (isMinusOne(newDenominatorValue)) {
			ExpressionNode minusConjugate = utils.getMinusConjugate(node, op);
			result = utils.multiply(numerator, minusConjugate);
		} else if (DoubleUtil.isInteger(newDenominatorValue)) {
			// if new denominator is integer but not 1 or -1
			result = utils.newNode(
					numerator.multiplyR(conjugate),
					Operation.DIVIDE, utils.newDouble(newDenominatorValue));

		}
		return result;
	}

	private static boolean isMinusOne(double newDenominatorValue) {
		return DoubleUtil.isEqual(newDenominatorValue, -1, Kernel.STANDARD_PRECISION);
	}

	private static boolean isOne(double newDenominatorValue) {
		return DoubleUtil.isEqual(newDenominatorValue, 1, Kernel.STANDARD_PRECISION);
	}

	private ExpressionNode getConjugateFactor(ExpressionNode node) {
		return utils.newNode(node.getLeft(), Operation.inverse(node.getOperation()),
				node.getRight());
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
		return utils.newDiv(product, denominator.getLeft());
	}

	private static boolean bothHaveSquareRoot(ExpressionNode numerator,
			ExpressionNode denominator) {
		return numerator.isOperation(Operation.SQRT) && denominator.isOperation(Operation.SQRT);
	}

	private ExpressionNode multiplySquareRoots(ExpressionNode left, ExpressionNode right) {
		double product = left.getLeftTree().multiply(right.getLeft())
				.wrap().evaluateDouble();

		return utils.newSqrt(product);
	}

	private ExpressionNode rationalizeAsLeafSqrtDenominator() {
		ExpressionValue rationalized = denominator.getLeft();
		ExpressionNode numeratorLeft =
				simplifiedMultiply(rationalized, numerator.getLeftTree());
		ExpressionNode numeratorRight =
				simplifiedMultiply(rationalized, numerator.getRightTree());
		ExpressionNode newNumerator =
				utils.newNode(numeratorLeft, numerator.getOperation(),
						numeratorRight);
		return utils.newDiv(newNumerator, rationalized);
	}
}