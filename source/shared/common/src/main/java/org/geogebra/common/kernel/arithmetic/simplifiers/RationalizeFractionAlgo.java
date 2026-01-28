/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

package org.geogebra.common.kernel.arithmetic.simplifiers;

import static org.geogebra.common.kernel.arithmetic.simplifiers.ExpressionValueUtils.isAddSubNode;
import static org.geogebra.common.kernel.arithmetic.simplifiers.ExpressionValueUtils.isAtomicSurdAdditionNode;
import static org.geogebra.common.kernel.arithmetic.simplifiers.ExpressionValueUtils.isAtomicSurdMultiplicationNode;
import static org.geogebra.common.kernel.arithmetic.simplifiers.ExpressionValueUtils.isIntegerValue;
import static org.geogebra.common.kernel.arithmetic.simplifiers.ExpressionValueUtils.isMinusOne;
import static org.geogebra.common.kernel.arithmetic.simplifiers.ExpressionValueUtils.isMultiplyNode;
import static org.geogebra.common.kernel.arithmetic.simplifiers.ExpressionValueUtils.isSqrtNode;
import static org.geogebra.common.kernel.arithmetic.simplifiers.ExpressionValueUtils.radicandOf;

import javax.annotation.Nonnull;

import org.geogebra.common.kernel.Kernel;
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

		if (isSqrtNode(numerator) || isAtomicSurdMultiplicationNode(denominator)
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
		if (isMultiplyNode(denominator)) {
			return handleProductInDenominator();
		}
		return null;
	}

	private ExpressionNode handleProductInDenominator() {
		ExpressionNode expanded = utils.expand(denominator);
		// isSupported() guarantees that exactly one of the leaves is SQRT by now,
		// so no check is needed here.
		if (isAddSubNode(expanded)) {
			return factorize(expanded);
		}

		if (isSqrtNode(denominator.getRight())) {
			ExpressionNode denominatorSqrt = denominator.getRightTree();
			double denominatorCoeff = denominator.getLeftTree().evaluateDouble();
			double radicand = denominatorSqrt.getLeft().evaluateDouble();
			double newDenominatorValue = denominatorCoeff * radicand;

			ExpressionNode newNumerator;
			if (isMultiplyNode(numerator) && isSqrtNode(numerator.getRight())) {
				ExpressionNode numSqrt = numerator.getRightTree();
				double numeratorCoeff = numerator.getLeftTree().evaluateDouble();
				ExpressionNode combinedSqrt = multiplySquareRoots(numSqrt, denominatorSqrt);
				newNumerator = simplifySqrtProduct(numeratorCoeff, combinedSqrt);
			} else if (isSqrtNode(numerator)) {
				newNumerator = multiplySquareRoots(numerator, denominatorSqrt);
			} else {
				newNumerator = numerator.multiplyR(denominatorSqrt);
			}
			return simplifyFraction(newNumerator, newDenominatorValue);
		}
		return null;
	}

	private ExpressionNode simplifySqrtProduct(double coeff, ExpressionNode sqrt) {
		ExpressionNode simplifiedSqrt = utils.getSurdsOrSame(sqrt);

		double extractedCoeff = 1;
		ExpressionNode sqrtPart = simplifiedSqrt;
		if (isMultiplyNode(simplifiedSqrt) && isSqrtNode(simplifiedSqrt.getRightTree())) {
			extractedCoeff = simplifiedSqrt.getLeftTree().evaluateDouble();
			sqrtPart = simplifiedSqrt.getRightTree();
		}

		double newCoeff = coeff * extractedCoeff;
		if (DoubleUtil.isOne(newCoeff)) {
			return sqrtPart;
		}
		return utils.newNode(utils.newDouble(newCoeff), Operation.MULTIPLY, sqrtPart);
	}

	private ExpressionNode simplifyFraction(ExpressionNode numerator, double denominator) {
		double numeratorCoeff = 1;
		ExpressionNode sqrtPart;

		if (isMultiplyNode(numerator) && isSqrtNode(numerator.getRightTree())) {
			numeratorCoeff = numerator.getLeftTree().evaluateDouble();
			sqrtPart = numerator.getRightTree();
		} else if (isSqrtNode(numerator)) {
			sqrtPart = numerator;
		} else {
			return utils.newDouble(numerator.evaluateDouble() / denominator).wrap();
		}

		long gcd = Kernel.gcd((long) Math.abs(numeratorCoeff), (long) Math.abs(denominator));
		double newNumeratorCoeff = numeratorCoeff / gcd;
		double newDenominator = denominator / gcd;

		ExpressionNode simplifiedNumerator;
		if (DoubleUtil.isOne(newNumeratorCoeff)) {
			simplifiedNumerator = sqrtPart;
		} else if (DoubleUtil.isMinusOne(newNumeratorCoeff)) {
			simplifiedNumerator = utils.newNode(utils.newDouble(-1), Operation.MULTIPLY, sqrtPart);
		} else {
			simplifiedNumerator = utils.newNode(utils.newDouble(newNumeratorCoeff),
					Operation.MULTIPLY, sqrtPart);
		}

		if (DoubleUtil.isOne(newDenominator)) {
			return simplifiedNumerator;
		}
		return utils.newDiv(simplifiedNumerator, utils.newDouble(newDenominator).wrap());
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