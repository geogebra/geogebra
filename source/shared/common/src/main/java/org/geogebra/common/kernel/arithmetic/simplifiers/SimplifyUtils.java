package org.geogebra.common.kernel.arithmetic.simplifiers;

import static org.geogebra.common.kernel.arithmetic.simplifiers.ExpressionValueUtils.*;
import static org.geogebra.common.util.DoubleUtil.isInteger;
import static org.geogebra.common.util.DoubleUtil.isOutOfSafeRoundRange;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.ExpressionValue;
import org.geogebra.common.kernel.arithmetic.MinusOne;
import org.geogebra.common.kernel.arithmetic.OperationCountChecker;
import org.geogebra.common.kernel.arithmetic.Surds;
import org.geogebra.common.plugin.Operation;
import org.geogebra.common.util.DoubleUtil;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Utility class to easily create and manipulate ExpressionValues/Nodes in Simplifiers
 * This class has a {@link Kernel} instance so no other simplifier class needs it.
 * They can call utils.newDouble(n) instead of new MyDouble(kernel, n) and so on.
 */
public final class SimplifyUtils {
	public final Kernel kernel;
	private final Surds surds;
	private final ExpressionReducer productReducer;
	public static final OperationCountChecker plusMinusChecker = new OperationCountChecker(
			Operation.PLUS, Operation.MINUS);

	/**
	 * @param kernel {@link Kernel}
	 */
	public SimplifyUtils(@NonNull Kernel kernel) {
		this.kernel = kernel;
		this.surds = new Surds();
		productReducer = new ExpressionReducer(this, Operation.MULTIPLY);
	}

	/**
	 * Creates an {@link ExpressionNode}
	 * @param left tree.
	 * @param operation the operation.
	 * @param right tree.
	 * @return the new node
	 */
	public ExpressionNode newNode(@NonNull ExpressionValue left, Operation operation,
			ExpressionValue right) {
		return new ExpressionNode(kernel, left, operation, right);
	}

	/**
	 * @param ev {@link ExpressionValue}
	 * @return a new node with the double value of ev.
	 */
	public ExpressionValue newDouble(@NonNull ExpressionValue ev) {
		return newDouble(ev.evaluateDouble());
	}

	/**
	 * Creates a new ExpressionNode with double value. If the value considered as integer within a
	 * given precision, it will be rounded to avoid things, like 1.9999999999998
	 * @param value the double value
	 * @return a new node with the given double value.
	 */
	public ExpressionValue newDouble(double value) {

		return new ExpressionNode(kernel,
				isInteger(value) && !isOutOfSafeRoundRange(value) ? Math.round(value) : value);
	}

	/**
	 * Creates a new {@link ExpressionNode} with divide operand
	 * @param numerator of div
	 * @param denominator of div
	 * @return the new node with div.
	 */
	public ExpressionNode newDiv(@NonNull ExpressionValue numerator,
			@NonNull ExpressionValue denominator) {
		return newNode(numerator, Operation.DIVIDE, denominator);
	}

	/**
	 * Flips left and right trees in a new node.
	 * @param node to flip.
	 * @return a new node with the same operation, but left- rightTrees are flipped.
	 */
	public ExpressionNode flipTrees(@NonNull ExpressionNode node) {
		return newNode(node.getRightTree(), node.getOperation(), node.getLeftTree());
	}

	/**
	 * Creates a new {@link ExpressionNode} with divide operand or
	 * if denominator is 1 or -1, the numerator with the right sign.
	 * @param numerator of div
	 * @param denominator of div
	 * @return the new node with div.
	 */

	public ExpressionNode div(@NonNull ExpressionValue numerator,
			@NonNull ExpressionValue denominator) {
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
	 * Multiply two nodes ensuring to cancel if any of the nodes is one, and if the result
	 * is a number, it returns a single number too.
	 * @param node1 to multiply.
	 * @param node2 to multiply.
	 * @return the node with the multiplied value
	 */
	public ExpressionNode multiply(@NonNull ExpressionNode node1, @NonNull ExpressionNode node2) {
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
	 * @param node to multiply.
	 * @param v to multiply.
	 * @return the node with the multiplied value
	 */
	public ExpressionNode multiply(ExpressionNode node, double v) {
		if (isOne(node)) {
			return newDouble(v).wrap();
		}

		if (isIntegerValue(node) && isInteger(v)) {
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

		if (DoubleUtil.isOne(v)) {
			return node1;
		}
		if (v == -1 && node1.getOperation() == Operation.MINUS) {
			return new ExpressionNode(node1.getKernel(),
					node1.getRight(), Operation.MINUS, node1.getLeft());
		}
		if (isIntegerValue(node1) && isInteger(v)) {
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

		if (isMinusOne(node1)) {
			return negateTagByTag(node2);
		}

		if (isIntegerValue(node1) && isIntegerValue(node2)) {
			return newDouble(node1.evaluateDouble() * node2.evaluateDouble()).wrap();
		}
		return node1.multiplyR(node2);
	}

	ExpressionNode negateTagByTag(ExpressionValue value) {
		return value.traverse(this::traverseNegateTagByTag).wrap();
	}

	private ExpressionValue traverseNegateTagByTag(ExpressionValue ev) {
		if (ev.isLeaf() && isIntegerValue(ev)) {
			return minusDouble(ev);
		}

		if (isSqrtNode(ev)) {
			return ev.wrap().multiply(minusOne());
		}

		ExpressionValue left = ev.wrap().getLeft();
		ExpressionValue right = ev.wrap().getRight();

		if (isNegativeSqrt(ev)) {
			return right;
		}

		if (isMultiplyNode(ev)) {
			ExpressionValue leftTraversed = left.traverse(this::traverseNegateTagByTag);
			ExpressionValue r = isIntegerValue(right) ? newDouble(right) : right;
			return isOne(leftTraversed) ? r : leftTraversed.wrap().multiplyR(r);
		}

		if (ev.isOperation(Operation.PLUS)) {
			ExpressionValue op1 = left.traverse(this::traverseNegateTagByTag);
			ExpressionValue op2 = right.traverse(this::traverseNegateTagByTag);
			return newNode(op1, Operation.PLUS, op2);
		}

		if (ev.isOperation(Operation.MINUS)) {
			ExpressionValue op1 = left.traverse(this::traverseNegateTagByTag);
			ExpressionValue op2 = right.traverse(this::traverseNegateTagByTag);
			return newNode(op1, Operation.MINUS, op2);
		}
		return ev;
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

		if (leftInteger) {
			return multiplyByInteger(right, left);
		}
		if (rightInteger) {
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

		if (isInteger(v)) {
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
	 * TODO: check this one.
	 * @param node to flip
	 * @return flipped node
	 */
	public ExpressionValue flipSign(@NonNull ExpressionNode node) {
		ExpressionNode leftTree = node.getLeftTree();
		double leftNumber = leftTree.evaluateDouble();

		if (node.isOperation(Operation.MULTIPLY) && isInteger(leftNumber)
				&& leftNumber != -1) {
			node.setLeft(newDouble(-leftNumber));
			return node;
		}
		if (node.isOperation(Operation.MULTIPLY) && leftNumber == -1) {
			return node.getRightTree();
		}
		return multiplyR(node, -1);
	}

	/**
	 * @param ev to get
	 * @return {@link Surds} if exists or ev itself.
	 */
	public ExpressionNode getSurdsOrSame(ExpressionNode ev) {
		ExpressionNode surds = getSurds(ev);
		return surds != null ? surds : ev;
	}

	/**
	 * @param ev to get
	 * @return {@link Surds}
	 */
	public ExpressionNode getSurds(ExpressionValue ev) {
		return surds.getResolution(ev.wrap(), kernel);
	}

	/**
	 * @param v number under square root
	 * @return the SQRT node of v.
	 */
	public ExpressionNode newSqrt(double v) {
		return new ExpressionNode(kernel, newDouble(v), Operation.SQRT, null);
	}

	/**
	 * @param node to copy.
	 * @return a new, deep copied instance.
	 */
	public ExpressionNode deepCopy(ExpressionNode node) {
		return node.deepCopy(kernel);
	}

	/**
	 * @return new node with positive infinity
	 */
	public ExpressionValue infinity() {
		return newDouble(Double.POSITIVE_INFINITY);
	}

	/**
	 * @return new node with negative infinity
	 */
	public ExpressionValue negativeInfinity() {
		return newDouble(Double.NEGATIVE_INFINITY);
	}

	/**
	 * Gets the number of the node for GCD computations.
	 * It is the numeric value if the node itself is an integer (2 is 2)
	 * or the multiplier of the node (5sqrt(2) is 5.
	 * @return the number of the node for GCD computations.
	 */
	public int getNumberForGCD(ExpressionNode node) {
		if (node.isLeaf() && isIntegerValue(node)) {
			return (int) node.evaluateDouble();
		}

		return getLeftMultiplier(node);
	}

	/**
	 * @param left {@link ExpressionValue}
	 * @param right {@link ExpressionValue}
	 * @return the multiplied node of left and right.
	 */
	public ExpressionNode newMultiply(ExpressionValue left, ExpressionValue right) {
		return newNode(left, Operation.MULTIPLY, right);
	}

	/**
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
	 * Reduce node if it is a pure product.
	 *
	 *  Example: 2*sqrt(2)*3*sqrt(7)*(-1) &#8594; -6 sqrt(14)
	 *
	 * @param node to check
	 * @return the reduced product or node itself, if not applicable.
	 */
	public ExpressionNode reduceProduct(ExpressionNode node) {
		if (checkOperationCount(node, plusMinusChecker) != 0) {
			return node;
		}
		return reduceSqrts(productReducer.apply(node));
	}

	private int checkOperationCount(ExpressionNode node, OperationCountChecker checker) {
		checker.reset();
		node.any(checker);
		return checker.getCount();
	}

	/**
	 * Apply operation to the expression if exists, otherwise let result be the operand itself.
	 *
	 * @param source to apply operation to.
	 * @param operation to apply on source.
	 * @param op the second operand of the operation.
	 * @return the result.
	 */
	public ExpressionValue applyOrLet(ExpressionValue source, Operation operation,
			ExpressionValue op) {
		if (source == null) {
			return op;
		}
		return source.wrap().apply(operation, op);
	}

	/**
	 *
	 * @return creates a {@link MinusOne} instance.
	 */
	public ExpressionValue minusOne() {
		return new MinusOne(kernel);
	}

	/**
	 * Reduce expression list to one expression in the format of
	 * a*sqrt(b) + c*sqrt(c) + ... + sqrt(p) sqrt(q) + ... + n.
	 * <p>
	 * The list can contain integers, and sqrts and multiplied sqrts.
	 * For example {sqrt(2), 2, 6sqrt(3), 2sqrt(2), 5, -4sqrt(3)}
	 * &#8594; 3sqrt(2) + 2sqrt(3) + 7</p>
	 * @param list of expressions.
	 * @return the reduced {@link ExpressionNode}
	 */
	public ExpressionNode reduceExpressions(List<ExpressionValue> list) {
		if (list.size() == 1) {
			return list.get(0).wrap();
		}
		double num = 0;
		int i = 0;
		double eval = list.get(0).evaluateDouble();
		while (i < list.size() && isInteger(eval)) {
			num += eval;
			i++;
			eval = list.get(i).evaluateDouble();
		}

		Map<Integer, Integer> sqrtMap = new HashMap<>();
		ExpressionNode result = null;
		while (i < list.size()) {
			ExpressionNode item = list.get(i).wrap();
			if (isMultiplyNode(item) && isIntegerValue(item.getLeft())
					&& isSqrtNode(item.getRightTree())) {
				int amount = getLeftMultiplier(item);
				addSqrtToSum(item.getRightTree(), sqrtMap, amount);
			} else if (isSqrtNode(item)) {
				addSqrtToSum(item, sqrtMap, 1);
			} else {
				result = addOrLet(result, item);
			}
			i++;
		}
		for (Map.Entry<Integer, Integer> entry : sqrtMap.entrySet()) {
			ExpressionNode sqrtNode = newSqrt(entry.getKey()).multiplyR(entry.getValue());
			result = addOrLet(result, getSurdsOrSame(sqrtNode));
		}
		if (num != 0) {
			result = addOrLet(result, newDouble(num).wrap());
		}
		return result;
	}

	private void addSqrtToSum(ExpressionNode sqrtNode, Map<Integer, Integer> sqrtMap, int amount) {
		int radicand = (int) sqrtNode.getLeft().evaluateDouble();
		int sum = sqrtMap.getOrDefault(radicand, 0);
		sqrtMap.put(radicand, sum + amount);
	}

	/**
	 * Add expression to result if exists, otherwise let result be the expression itself.
	 *
	 * @param result to add or assign to.
	 * @param ev the value to add.
	 * @return the result.
	 */
	static ExpressionNode addOrLet(ExpressionValue result, ExpressionValue ev) {
		return result == null ? ev.wrap() : result.wrap().plus(ev);
	}

	/**
	 * Reduce sqrt statements to the most simplest surd.
	 *
	 * @param ev to check.
	 *
	 * @return the reduced form.
	 */
	public ExpressionNode reduceSqrts(ExpressionValue ev) {
		ExpressionNode node = ev.traverse(this::traverseReduceSqrts).wrap();
		if (isMultiplyNode(node)) {
			ExpressionNode leftTree = node.getLeftTree();
			ExpressionNode rightTree = node.getRightTree();
			if (isSqrtNode(rightTree)) {
				ExpressionValue surd = getSurdsOrSame(rightTree);
				return productReducer.apply(leftTree.multiply(surd));
			}
		}

		return getSurdsOrSame(node);
	}

	private ExpressionValue traverseReduceSqrts(ExpressionValue value) {
		ExpressionNode node = value.wrap();
		if (isMultiplyNode(node)) {
			ExpressionNode leftTree = node.getLeftTree();
			ExpressionNode rightTree = node.getRightTree();
			if (isSqrtNode(leftTree) && isSqrtNode(rightTree)) {
				return newSqrt(
						leftTree.getLeft().evaluateDouble()
								* rightTree.getLeft().evaluateDouble()
				);
			}
			if (isMultiplyNode(leftTree) && isSqrtNode(leftTree.getRight()) && isSqrtNode(
					rightTree)) {
				ExpressionNode reducedSqrt = newSqrt(
						leftTree.getRightTree().getLeft().evaluateDouble()
								* rightTree.getLeft().evaluateDouble());
				return productReducer.apply(multiply(getSurdsOrSame(reducedSqrt).wrap(),
						leftTree.getLeftTree()));

			}
		}
		return value;
	}

	/**
	 * Checks if a sum expression consist of all negative tags, like:
	 *   -a - bsqrt(c) - sqrt(d) - .... - n sqrt(m)
	 *
	 * @param value to check
	 * @return if value satisfies the condition above.
	 */
	public boolean isAllNegative(ExpressionValue value) {
		FlattenNode flattenNode = new FlattenNode(value.wrap(), this);
		int i = 0;
		while (i < flattenNode.size()) {
			ExpressionValue ev = flattenNode.get(i);
			if (ev.evaluateDouble() >= 0) {
				return false;
			}
			i++;
		}
		return true;
	}

	/**
	 *
	 * @param value to negate
	 * @return -value as MyDouble
	 */
	public ExpressionValue minusDouble(ExpressionValue value) {
		return newDouble(-value.evaluateDouble());
	}

	/**
	 * Multiply node tag by tag:
	 * a + sqrt(b) - c sqrt(b) with m -&gt; am + m sqrt(b) mc sqrt(d)
	 *
	 * @param node to multiply
	 * @param multiplier to multiply with
	 * @return the tag by tag produvt
	 */
	public ExpressionValue multiplyTagByTag(ExpressionNode node, long multiplier) {
		return node.traverse(ev -> {
			if (isAtomic(ev)) {
				return reduceProduct(ev.wrap().multiplyR(multiplier));
			}
			return ev;
		});
	}

	/**
	 * Negate atomic expressions in the most simplified way:
	 * <ul>
	 *   <li>a &#8594; -a where a is integer</li>
	 *   <li>sqrt(a) &#8594; -sqrt(a)</li>
	 *   <li>c sqrt(a) &#8594; -c sqrt(a)</li>
	 *</ul>
	 *
	 * @param ev to negate.
	 * @return the negated expression.
	 */
	public ExpressionValue negateAtomic(ExpressionValue ev) {
		if (isIntegerValue(ev)) {
			return minusDouble(ev);
		}
		if (isSqrtNode(ev)) {
			return ev.wrap().multiplyR(minusOne());
		}
		if (isNegativeSqrt(ev)) {
			return ev.wrap().getRight();
		}

		return reduceProduct(ev.wrap().multiplyR(-1));
	}
}