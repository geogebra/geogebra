
package org.geogebra.common.kernel.arithmetic.simplifiers;

import static org.geogebra.common.kernel.arithmetic.simplifiers.ExpressionValueUtils.*;

import javax.annotation.Nonnull;

import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.ExpressionValue;
import org.geogebra.common.kernel.arithmetic.NumberValue;
import org.geogebra.common.plugin.Operation;
import org.geogebra.common.util.DoubleUtil;

public final class RationalizeFractionAlgo {
	private final SimplifyUtils utils;
	private final ExpressionNode numerator;
	private final ExpressionNode denominator;

	/**
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
	 * @return the rationalized node or null
	 */
	public ExpressionNode compute() {
		ExpressionNode node = rationalize();
		if (node == null || checkDecimals(node)) {
			return null;
		}
		return node;
	}

	/**
	 * Package private to be testable in isolation
	 * @param node to test
	 * @return if the expression has decimal numbers in it.
	 */
	static boolean checkDecimals(ExpressionNode node) {
		return node.any(v -> v instanceof NumberValue && !isIntegerValue(v));
	}

	private ExpressionNode rationalize() {
		if (numerator.isLeaf()) {
			return rationalizeWithLeafNumerator();
		}

		if (isSqrtNode(numerator) && isSqrtNode(denominator)) {
			return rationalizeAsSquareRootProduct();
		}

		if (isSqrtNode(numerator)
				|| isAtomicSurdAdditionNode(numerator) && isAtomicSurdAdditionNode(denominator)) {
			return factorizeOrHandleProduct();
		}

		return utils.newDiv(multiplyNumeratorWithSqrt(), radicandOf(denominator));
	}

	/**
	 * If the fraction can be factorized, it is done here
	 * or if denominator is a product, it is handled here too.
	 * @return the altered expression described above.
	 */
	private ExpressionNode factorizeOrHandleProduct() {
		if (isAddSubNode(denominator)) {
			return factorize(denominator);
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
		// isSupported() guarantees that exactly one of the leaves is SQRT by now,
		// so no check is needed here.
		if (isAddSubNode(rightOperand)) {
			return factorize(expanded);
		}
		if (rightOperandOperation == Operation.SQRT) {
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

	private ExpressionNode rationalizeWithLeafNumerator() {
		if (isSqrtNode(denominator)) {
			return utils.div(numerator.multiplyR(denominator), radicandOf(denominator));
		}
		return factorizeOrHandleProduct();
	}

	private ExpressionNode factorize(ExpressionNode node) {
		ExpressionNode result = null;
		Operation op = node.getOperation();
		ExpressionNode conjugate = getConjugateFactor(node);
		double newDenominatorValue = node.multiply(conjugate).evaluateDouble();
		if (DoubleUtil.isOne(newDenominatorValue)) {
			result = utils.multiplyR(numerator, conjugate);
		} else if (DoubleUtil.isMinusOne(newDenominatorValue)) {
			ExpressionNode minusConjugate = utils.getMinusConjugate(node, op);
			result = utils.multiply(numerator, minusConjugate);
		} else if (DoubleUtil.isInteger(newDenominatorValue)) {
			// if new denominator is integer but not 1 or -1
			result = utils.newNode(
					utils.multiplyR(numerator, conjugate),
					Operation.DIVIDE, utils.newDouble(newDenominatorValue));

		}
		return result;
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
		if (isSqrtNode(left) && isSqrtNode(right)) {
			return multiplySquareRoots(left, right);
		}

		return isMinusOne(left) ? right.multiply(left) : left.multiply(right);
	}

	private ExpressionNode rationalizeAsSquareRootProduct() {
		ExpressionNode product = multiplySquareRoots(numerator, denominator);
		return utils.newDiv(product, denominator.getLeft());
	}

	private ExpressionNode multiplySquareRoots(ExpressionNode left, ExpressionNode right) {
		double product = left.getLeftTree().multiply(right.getLeft())
				.wrap().evaluateDouble();

		return utils.newSqrt(product);
	}

	/**
	 * Multiply numerator with denominator, when numerator is a tag and denominator is
	 * a single (not tag or multiplied) sqrt(d): (a + sqrt(b) / sqrt(d)
	 * @return the multiplied numerator: (a * sqrt(d) + (sqrt(d) sqrt(b))
	 */
	private ExpressionNode multiplyNumeratorWithSqrt() {
		ExpressionValue squared = radicandOf(denominator);
		if (numerator.isOperation(Operation.PLUS) || numerator.isOperation(Operation.MINUS)) {
			ExpressionNode numeratorLeft =
					utils.reduceProduct(simplifiedMultiply(squared, numerator.getLeftTree()))
							.wrap();
			ExpressionNode numeratorRight =
					simplifiedMultiply(squared, numerator.getRightTree());
			return utils.newNode(numeratorLeft, numerator.getOperation(),
							numeratorRight);
		}
		return utils.reduceProduct(simplifiedMultiply(squared, numerator))
				.wrap();
	}
}