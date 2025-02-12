package org.geogebra.common.kernel.arithmetic.simplifiers;

import static org.geogebra.common.kernel.arithmetic.Surds.getResolution;

import javax.annotation.Nonnull;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.ExpressionValue;
import org.geogebra.common.kernel.arithmetic.MinusOne;
import org.geogebra.common.kernel.arithmetic.Surds;
import org.geogebra.common.plugin.Operation;
import org.geogebra.common.util.DoubleUtil;

/**
 * Utility class to help manipulating ExpressionNodes in Simplifiers
 *
 */
public class SimplifyUtils {
	public final Kernel kernel;

	/**
	 *
	 * @param kernel {@link Kernel}
	 */
	public SimplifyUtils(Kernel kernel) {
		this.kernel = kernel;
	}

	/**
	 * Flips left and right trees in a new node.
	 * @param node to flip.
	 * @return a new node with the same operation, but left- rightTrees are flipped.
	 */
	public ExpressionNode flipTrees(ExpressionNode node) {
		return newNode(node.getRightTree(), node.getOperation(), node.getLeftTree());
	}

	/**
	 * Creates a new ExpressionNode with double value. If the value considered as integer within a
	 * given precision, it will be rounded to avoid things, like 1.9999999999998
	 *
	 * @param v the double value
	 * @return the node with value v.
	 */
	public ExpressionValue newDouble(double v) {
		return new ExpressionNode(kernel,
				isIntegerValue(v) ? Math.round(v) : v);
	}

	/**
	 * Creates an {@link ExpressionNode}
	 * @param left tree.
	 * @param operation the operation.
	 * @param right tree.
	 * @return the new node
	 */
	public ExpressionNode newNode(ExpressionValue left, Operation operation,
			ExpressionValue right) {
		return new ExpressionNode(kernel, left, operation, right);
	}

	/**
	 * Creates a new {@link ExpressionNode} with divide operand or
	 * if denominator is 1 or -1, the numerator with the right sign.
	 * @param numerator of div
	 * @param denominator of div
	 * @return the new node with div.
	 */

	public ExpressionNode div(ExpressionValue numerator, ExpressionValue denominator) {
		double valDenominator = denominator.evaluateDouble();
		if (valDenominator == 1) {
			return numerator.wrap();
		}

		if (valDenominator == -1) {
			return numerator.wrap().multiplyR(-1);
		}
		return newDiv(numerator, denominator);
	}

	/**
	 * Creates a new {@link ExpressionNode} with divide operand
	 * @param numerator of div
	 * @param denominator of div
	 * @return the new node with div.
	 */
	public ExpressionNode newDiv(ExpressionValue numerator, ExpressionValue denominator) {
		return newNode(numerator, Operation.DIVIDE, denominator);
	}

	/**
	 * Multiply two nodes ensuring to cancel if any of the nodes is one, and if the result
	 * is a number, it returns a single number too.
	 *
	 * @param node1 to multiply.
	 * @param node2 to multiply.
	 *
	 * @return the node with the multiplied value
	 */
	public ExpressionNode multiply(ExpressionNode node1, ExpressionNode node2) {
		if (isOne(node1)) {
			return node2;
		}

		if (isOne(node2)) {
			return node1;
		}

		if (isIntegerValue(node1) && isIntegerValue(node2)) {
			return newDouble(node1.evaluateDouble() * node2.evaluateDouble()).wrap();
		}

		return node1.multiply(node2);
	}

	/**
	 * Multiply a node by a number ensuring to cancel if any of the nodes is one, and if the result
	 * is a number, it returns a single number too.
	 *
	 * @param node to multiply.
	 * @param v to multiply.
	 *
	 * @return the node with the multiplied value
	 */
	public ExpressionNode multiply(ExpressionNode node, double v) {
		if (isOne(node)) {
			return newDouble(v).wrap();
		}

		if (isOne(v)) {
			return node;
		}

		if (isIntegerValue(node) && isIntegerValue(v)) {
			return newDouble(node.evaluateDouble() * v).wrap();
		}

		return node.multiply(v);
	}

	/**
	 * Multiply node at the right with a value.
	 * @param node1 to multiply
	 * @param v the value
	 * @return v * node
	 */
	public ExpressionNode multiplyR(ExpressionNode node1, double v) {
		if (isOne(node1)) {
			return newDouble(v).wrap();
		}

		if (isOne(v)) {
			return node1;
		}
		if (v == -1 && node1.getOperation() == Operation.MINUS) {
			return new ExpressionNode(node1.getKernel(),
					node1.getRight(), Operation.MINUS, node1.getLeft());
		}
		if (isIntegerValue(node1) && isIntegerValue(v)) {
			return newDouble(node1.evaluateDouble() * v).wrap();
		}

		return node1.multiplyR(v);
	}

	/**
	 * Multiply node2 at the right with node1.
	 * @param node1 to multiply
	 * @param node2 to multiply
	 * @return node2 * node1
	 */
	public ExpressionNode multiplyR(ExpressionNode node1, ExpressionNode node2) {
		if (isOne(node1)) {
			return node2;
		}

		if (isOne(node2)) {
			return node1;
		}

		if (isIntegerValue(node1) && isIntegerValue(node2)) {
			return newDouble(node1.evaluateDouble() * node2.evaluateDouble()).wrap();
		}
				return node1.multiplyR(node2);
	}

	private static boolean isOne(ExpressionNode node) {
		return isOne(node.evaluateDouble());
	}

	private static boolean isOne(double v) {
		return DoubleUtil.isEqual(v, 1, Kernel.STANDARD_PRECISION);
	}

	/**
	 * Expands node if it is a multiplication.
	 * @param node to expand.
	 * @return the expanded node
	 */
	public ExpressionNode expand(ExpressionNode node) {
		if (node.getOperation() != Operation.MULTIPLY) {
			return node;
		}
		ExpressionValue left = node.getLeft();
		ExpressionValue right = node.getRight();
		boolean leftInteger = isIntegerValue(left);
		boolean rightInteger = isIntegerValue(right);
		if (leftInteger && rightInteger) {
			return newDouble(left.evaluateDouble() * right.evaluateDouble()).wrap();
		}

		if (leftInteger && !rightInteger) {
			return multiplyByInteger(right, left);
		}
		if (!leftInteger && rightInteger) {
			return multiplyByInteger(left, right);
		}
		return node;
	}

	private ExpressionNode multiplyByInteger(ExpressionValue right, ExpressionValue left) {
		ExpressionNode opLeft = right.wrap().getLeftTree();
		ExpressionNode opRight = right.wrap().getRightTree();
		double mul = left.evaluateDouble();
		return newNode(multiply(opLeft, mul), right.wrap().getOperation(),
				multiplyR(opRight, mul));
	}

	private static boolean isPositiveIntegerValue(ExpressionValue ev) {
		if (ev == null) {
			return false;
		}

		double value = ev.evaluateDouble();
		return isIntegerValue(value) && value >= 0;
	}

	/**
	 *
	 * @param ev to check
	 * @return if ev holds an integer.
	 */
	static boolean isIntegerValue(ExpressionValue ev) {
		return ev != null && isIntegerValue(ev.evaluateDouble());
	}

	/**
	 *
	 * @param value to check
	 * @return if value an integer.
	 */
	static boolean isIntegerValue(double value) {
		return DoubleUtil.isEqual(Math.round(value), value, Kernel.STANDARD_PRECISION);

	}

	private ExpressionValue mulByMinusOneL(ExpressionValue ev) {
		ExpressionNode node = ev.wrap();
		ExpressionNode left = node.getLeftTree();
		ExpressionValue right = node.getRight();
		ExpressionNode leftNumber = negate(left);
		return mulByMinusOne(node, leftNumber, right);
	}

	private ExpressionValue mulByMinusOneR(ExpressionValue ev) {
		ExpressionNode node = ev.wrap();
		ExpressionValue left = node.getLeftTree().getRight().isOperation(Operation.MULTIPLY)
				? node.getLeftTree().getRight()
				: node.getLeftTree();
		ExpressionValue right = node.getRightTree();
		ExpressionNode number = newDouble(right.evaluateDouble()).wrap();
		ExpressionNode tree = left.wrap();
		return mulByMinusOne(node, negate(tree), number);
	}

	/**
	 * Negate node with minimal or no operands
	 * @param node to negate.
	 * @return the negated node.
	 */
	public ExpressionNode negate(ExpressionNode node) {
		if (node.isOperation(Operation.MULTIPLY) && node.getLeftTree().evaluateDouble() == -1) {
			return node.getRightTree();
		}
		double v = node.evaluateDouble();
		if (isIntegerValue(v)) {
			return newDouble(-v).wrap();
		}
		return node.isOperation(Operation.MINUS) ? node.multiplyR(-1) : node;
	}

	private ExpressionValue mulByMinusOne(ExpressionNode node,
			ExpressionNode leftNumber, ExpressionValue right) {
		Operation operation = node.getOperation();
		if (Operation.PLUS.equals(operation)) {
			return newNode(leftNumber, Operation.MINUS, right);
		}
		if (Operation.MINUS.equals(operation)) {
			return newNode(leftNumber, Operation.PLUS, right);
		}

		return node.multiplyR(-1);
	}

	ExpressionNode negative(ExpressionNode node) {
		ExpressionValue mul = mulByMinusOne(node);
		return mul.wrap().multiply(minusOne());
	}

	/**
	 * Multiply node by {@link MinusOne}
	 * @param node to multiply
	 * @return the multiplied node
	 */
	public ExpressionValue mulByMinusOne(ExpressionNode node) {
		ExpressionValue result = isIntegerValue(node.getLeft())
				? mulByMinusOneL(node)
				: mulByMinusOneR(node);
		ExpressionNode rightTree = result.wrap().getRightTree();
		if (rightTree != null && rightTree.getLeft() instanceof MinusOne) {
			return rightTree.getRight();
		}
		return result;
	}

	/**
	 *
	 * @param ev to check
	 * @return if ev is a square root of a positive integer
	 */
	public static boolean isSqrtOfPositiveInteger(ExpressionValue ev) {
		if (!ev.isOperation(Operation.SQRT)) {
			return false;
		}
		return isPositiveIntegerValue(ev.wrap().getLeft());
	}

	/**
	 *
	 * @param node to check
	 * @return if node is supported by {@link RationalizableFraction}
	 */
	public static boolean isNodeSupported(ExpressionNode node) {
		return (node.isLeaf() && isIntegerValue(node))
				|| isSqrtOfPositiveInteger(node)
				|| isSqrtAndInteger(node);
	}

	/**
	 * @param node to check.
	 * @return if node is in (sqrt(a) +/- b) or (a +/- sqrt(b)) form
	 */
	private static boolean isSqrtAndInteger(ExpressionNode node) {
		if (!(node.isOperation(Operation.PLUS) || node.isOperation(Operation.MINUS))) {
			return false;
		}

		return (isSqrtOfPositiveInteger(node.getLeft()) && isIntegerValue(node.getRightTree()))
				|| (isIntegerValue(node.getLeftTree())
				&& isSqrtOfPositiveInteger(node.getRightTree()));
	}

	/**
	 *
	 * @return new instance of {@link MinusOne}
	 */
	public MinusOne minusOne() {
		return new MinusOne(kernel);
	}

	/**
	 * Flip + -> -
	 * @param operation to flip
	 * @return the flipped operation, or itself if it is not flippable.
	 */
	public static Operation flip(Operation operation) {
		if (operation == Operation.PLUS) {
			return Operation.MINUS;
		}
		if (operation == Operation.MINUS) {
			return Operation.PLUS;
		}
		return operation;
	}

	/**
	 * Makes node negative.
	 * It differs from multiplying the node by -1, that it ensures the simplest form
	 * avoiding -1 * 3 or -1 * 3sqrt and returns simply -3 or -3sqrt
	 * @param node to make negative.
	 * @return the result described above.
	 */
	public ExpressionNode makeNegative(@Nonnull ExpressionNode node) {
		ExpressionNode leftTree = node.getLeftTree();
		double leftNumber = leftTree.evaluateDouble();
		if (leftNumber < 0) {
			return node;
		}

		if (node.isOperation(Operation.MULTIPLY) && isIntegerValue(leftTree)) {
			node.setLeft(newDouble(-leftNumber));
			return node;
		}
		return new ExpressionNode(kernel, minusOne(), Operation.MULTIPLY, node);
	}

	/**
	 * Get the left multiplier of the node (ie: 2sqrt(2) is 2) or 1 if it does not make sense.
	 * @param node to get multiplier from
	 * @return the multiplier
	 */
	public int getLeftMultiplier(ExpressionNode node) {
		return node.isOperation(Operation.MULTIPLY) && isIntegerValue(node.getLeft())
				? (int) node.getLeft().evaluateDouble()
				: 1;
	}

	/**
	 * TODO: check this one.
	 * @param node to flip
	 * @return flipped node
	 */
	public ExpressionValue flipSign(@Nonnull ExpressionNode node) {
		ExpressionNode leftTree = node.getLeftTree();
		double leftNumber = leftTree.evaluateDouble();

		if (node.isOperation(Operation.MULTIPLY) && isIntegerValue(leftNumber)
				&& leftNumber != -1) {
			node.setLeft(newDouble(-leftNumber));
			return node;
		}
		if (node.isOperation(Operation.MULTIPLY) && leftNumber == - 1) {
			return node.getRightTree();
		}
		return multiplyR(node, -1);
	}

	/**
	 *
	 * @param ev to get
	 * @return {@link Surds} if exists or ev itself.
	 */
	public ExpressionValue getSurdsOrSame(ExpressionValue ev) {
		ExpressionValue surds = getSurds(ev);
		return surds != null ? surds : ev;
	}

	/**
	 *
	 * @param ev to get
	 * @return {@link Surds}
	 */
	public ExpressionValue getSurds(ExpressionValue ev) {
		return getResolution(ev.wrap(), kernel);
	}

	/**
	 *
	 * @param v number under square root
	 * @return the SQRT node of v.
	 */
	public ExpressionNode newSqrt(double v) {
		return new ExpressionNode(kernel, newDouble(v), Operation.SQRT, null);
	}

	/**
	 *
	 * @param ev to check
	 * @return if operation of the ev is SQRT.
	 */
	public boolean isSqrt(ExpressionValue ev) {
		return ev.isOperation(Operation.SQRT);
	}

	/**
	 *
	 * @param node to copy.
	 * @return a new, deep copied instance.
	 */
	public ExpressionNode deepCopy(ExpressionNode node) {
		return node.deepCopy(kernel);
	}

	/**
	 *
	 * @param node to check
	 * @return if operation of the node is DIVIDE.
	 */
	public boolean isDivNode(ExpressionNode node) {
		return Operation.DIVIDE.equals(node.getOperation());
	}

	/**
	 *
	 * @param node to check
	 * @return if operation of the node is MULTIPLY
	 */
	public boolean isMultiplyNode(ExpressionNode node) {
		return Operation.MULTIPLY.equals(node.getOperation());
	}

	/**
	 *
	 * @return new node with positive infinity
	 */
	public ExpressionValue infinity() {
		return newDouble(Double.POSITIVE_INFINITY);
	}

	/**
	 *
	 * @return new node with negative infinity
	 */
	public ExpressionValue negativeInfinity() {
		return newDouble(Double.NEGATIVE_INFINITY);
	}

	/**
	 * Gets the number of the node for GCD computations.
	 * It is the numeric value if the node itself is an integer (2 is 2)
	 * or the multiplier of the node (5sqrt(2) is 5.
	 *
	 * @return the number of the node for GCD computations.
	 */
	public int getNumberForGCD(ExpressionNode node) {
		if (node.isLeaf() && isIntegerValue(node)) {
			return (int) node.evaluateDouble();
		}

		return getLeftMultiplier(node);
	}

	/**
	 *
	 * @param left {@link ExpressionValue}
	 * @param right {@link ExpressionValue}
	 * @return the multiplied node of left and right.
	 */
	public ExpressionNode newMultiply(ExpressionValue left, ExpressionValue right) {
		return newNode(left, Operation.MULTIPLY, right);
	}

	/**
	 *
	 * @param node to check
	 * @return  -1 or 1 whether node has operand MINUS in front of it.
	 */
	public int signFromOperand(ExpressionNode node) {
		return node.isOperation(Operation.MINUS) ? -1 : 1;
	}

	/**
	 *
	 * @param node {@link ExpressionNode}
	 * @param op {@link Operation}
	 * @return the consugate multiplied by -1.
	 */
	public ExpressionNode getMinusConjugate(ExpressionNode node, Operation op) {
        return newNode(negate(node.getLeftTree()),
					Operation.inverse(op),
					node.getRight().wrap().multiplyR(-1));
		}

	/**
	 *
	 * @param value to check
	 * @return if value evaluates to -1.
	 */
	public boolean evaluateMinusOne(ExpressionValue value) {
		return DoubleUtil.isEqual(value.evaluateDouble(), -1);
	}
}
