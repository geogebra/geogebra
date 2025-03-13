package org.geogebra.common.kernel.arithmetic.simplifiers;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.ExpressionValue;
import org.geogebra.common.plugin.Operation;
import org.geogebra.common.util.DoubleUtil;

import edu.umd.cs.findbugs.annotations.Nullable;

/**
 * Utility class to find out properties of ExpressionValue in a short way.
 * Methods are include null checking, so for boolean methods they return false if ev is null.
 * No {@link Kernel} instance needed in this class.
 */
public final class ExpressionValueUtils {

	/**
	 *
	 * @param ev to check
	 * @return if ev holds an integer.
	 */
	static boolean isIntegerValue(@Nullable ExpressionValue ev) {
		if (ev == null) {
			return false;
		}

		return DoubleUtil.isInteger(ev.evaluateDouble());

	}

	/**
	 *
	 * @param ev to check
	 * @return if ev evaluates to 1 (within standard precision).
	 */
	public static boolean isOne(@Nullable ExpressionValue ev) {
		return ev != null && DoubleUtil.isOne(ev.evaluateDouble());
	}

	/**
	 *
	 * @param ev to check
	 * @return if ev evaluates to -1 (within standard precision).
	 */
	public static boolean isMinusOne(@Nullable ExpressionNode ev) {
		return ev != null && DoubleUtil.isMinusOne(ev.evaluateDouble());
	}

	/**
	 *
	 * @param ev to check
	 * @return if ev holds a natural number.
	 */
	static boolean isNaturalNumber(@Nullable ExpressionValue ev) {
		if (ev == null) {
			return false;
		}
		double value = ev.evaluateDouble();

		return DoubleUtil.isInteger(value) && value >= 0;
	}

	/**
	 *
	 * @param ev to check
	 * @return true if square root is valid, ie the value under it is a natural number.
	 */
	public static boolean isSqrtValid(@Nullable ExpressionValue ev) {
		return isSqrtNode(ev) && isNaturalNumber(ev.wrap().getLeft());
	}

	/**
	 *
	 * @param ev to check
	 * @return if operation of the ev is SQRT.
	 */
	public static boolean isSqrtNode(@Nullable ExpressionValue ev) {
		return ev != null && ev.isOperation(Operation.SQRT);
	}

	/**
	 *
	 * @param ev to check
	 * @return if ev is supported by {@link RationalizableFraction}
	 */
	public static boolean isNodeSupported(@Nullable ExpressionValue ev) {
		if (ev == null) {
			return false;
		}

		return (ev.isLeaf() && isIntegerValue(ev))
				|| isSqrtValid(ev)
				|| isSqrtAndInteger(ev);
	}

	/**
	 * @param ev to check.
	 * @return if ev is in (sqrt(a) +/- b) or (a +/- sqrt(b)) form
	 */
	private static boolean isSqrtAndInteger(@Nullable ExpressionValue ev) {
		if (!isAtomicSurdAdditionNode(ev)) {
			return false;
		}
		ExpressionNode node = ev.wrap();

		return isSqrtValid(node.getLeft()) || isSqrtValid(node.getRight());
	}

	/**
	 * Checks if expression is in a + b or a - b form, where a and b are also expressions.
	 *
	 * @param ev to check
	 * @return if ev is in a + b or a - b form.
	 */
	static boolean isAddSubNode(@Nullable ExpressionValue ev) {
		if (ev == null) {
			return false;
		}

		ExpressionNode node = ev.wrap();
		return node.getLeftTree() != null && node.getRightTree() != null
				&& (node.isOperation(Operation.PLUS) || node.isOperation(Operation.MINUS));
	}

	/**
	 * Checks if expression is in a + b or a - b form, where a and b are atomic expressions.
	 * @see ExpressionValueUtils#isAtomic
	 *
	 * @param ev to check
	 * @return if ev is an a+/-b expression, where a, b are atomic expressions.
	 */
	public static boolean isAtomicSurdAdditionNode(@Nullable ExpressionValue ev) {
		if (ev == null) {
			return false;
		}
		ExpressionNode node = ev.wrap();
		return isAddSubNode(ev) && isAtomic(node.getLeft()) && isAtomic(node.getRight());
	}

	/**
	 * Checks if expression is one of the main building blocks of the rationalization algo:
	 *  n, sqrt(a) or m * sqrt(a) where n, m, a are integers and a >= 0
	 *
	 * @param ev to check.
	 * @return if ev is atomic
	 */
	public static boolean isAtomic(ExpressionValue ev) {
		return ev.isLeaf() || isSqrtNode(ev)
				|| (isMultiplyNode(ev) && isSqrtNode(ev.wrap().getRight()));
	}

	/**
	 *
	 * @param ev to check
	 * @return a if ev is sqrt(a), null otherwise.
	 */
	public static ExpressionValue radicandOf(@Nullable ExpressionValue ev) {
		return isSqrtNode(ev) ? ev.wrap().getLeftTree() : null;
	}

	/**
	 * Get the left multiplier of the ev (ie: 2sqrt(2) is 2) or 1 if it does not make sense.
	 * @param ev to get multiplier from
	 * @return the multiplier
	 */
	public static int getLeftMultiplier(ExpressionValue ev) {
		return ev.isOperation(Operation.MULTIPLY) && isIntegerValue(ev.wrap().getLeft())
				? (int) ev.wrap().getLeft().evaluateDouble()
				: 1;
	}

	/**
	 *
	 * @param ev to check
	 * @return if operation of the ev is DIVIDE.
	 */
	public static boolean isDivNode(@Nullable ExpressionValue ev) {
		return ev != null && ev.isOperation(Operation.DIVIDE);
	}

	/**
	 *
	 * @param ev to check
	 * @return if operation of the ev is MULTIPLY
	 */
	public static boolean isMultiplyNode(ExpressionValue ev) {
		return ev != null && ev.isOperation(Operation.MULTIPLY);
	}

	/**
	 *
	 * @param ev to check
	 * @return if ev evaluates to -1.
	 */
	public static boolean isMinusOne(ExpressionValue ev) {
		return DoubleUtil.isMinusOne(ev.evaluateDouble());
	}

	/**
	 *
	 * @param ev to check
	 * @return if ev is an -sqrt.
	 */
	public static boolean isNegativeSqrt(ExpressionValue ev) {
		return ev.isOperation(Operation.MULTIPLY)
				&& ev.wrap().getLeft().evaluateDouble() == -1
				&& isSqrtNode(ev.wrap().getRight());
	}
}