package org.geogebra.common.kernel.arithmetic.simplifiers;

import static org.geogebra.common.kernel.arithmetic.simplifiers.SimplifyUtils.flip;
import static org.geogebra.common.kernel.arithmetic.simplifiers.SimplifyUtils.isIntegerValue;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.ExpressionValue;
import org.geogebra.common.plugin.Operation;

public class FactorOut implements SimplifyNode {
	private enum NodeType {
		INVALID,
		PLUS_OR_MINUS,
		MULTIPLIED,
		FRACTION
	}

	private final SimplifyUtils utils;

	public FactorOut(SimplifyUtils utils) {
		this.utils = utils;
	}

	@Override
	public boolean isAccepted(ExpressionNode node) {
		if (node.isLeaf()) {
			return true;
		}

		return getNodeType(node) != NodeType.INVALID;
	}

	private NodeType getNodeType(ExpressionNode node) {
		if (node.isOperation(Operation.PLUS) || node.isOperation(Operation.MINUS)) {
			return getPlusOrMinusType(node);
		}

		if (node.isOperation(Operation.MULTIPLY) && isAccepted(node.getRightTree())) {
			return NodeType.MULTIPLIED;
		}
		if (node.isOperation(Operation.MULTIPLY) && isAccepted(node.getLeftTree())
				&& !isIntegerValue(node.getLeft())) {
			return NodeType.MULTIPLIED;
		}
		if (node.isOperation(Operation.DIVIDE)
				&& isAccepted(node.getLeftTree()) && isAccepted(node.getRightTree())) {
			return NodeType.FRACTION;
		}

		return NodeType.INVALID;
	}

	private NodeType getPlusOrMinusType(ExpressionNode node) {
		return minusNodeAccepted(node.getLeftTree(), node.getRightTree())
				? NodeType.PLUS_OR_MINUS
				: NodeType.INVALID;
	}

	private boolean minusNodeAccepted(ExpressionNode leftTree, ExpressionNode rightTree) {
		int num1 = utils.getNumberForGCD(leftTree);
		int num2 = utils.getNumberForGCD(rightTree);
		return hasRealGCD(num1, num2);
	}

	private boolean hasRealGCD(int num1, int num2) {
		long gcd = Kernel.gcd(num1, num2);
		return Math.abs(gcd) != 1 && (num1 == gcd && num1 < num2 || gcd == num2)  ;
	}

	@Override
	public ExpressionNode apply(ExpressionNode node) {
		NodeType nodeType = getNodeType(node);
		switch (nodeType) {
		case FRACTION:
			return utils.div(apply(node.getLeftTree()), apply(node.getRightTree()));
		case PLUS_OR_MINUS:
			return factorOutPlusOrMinusNode(node);
		case MULTIPLIED:
			return factorOutMultiplied(node.getLeftTree(), node.getRightTree());
		default:
			return node;
		}
	}

	private ExpressionNode factorOutMultiplied(ExpressionNode leftTree, ExpressionNode rightTree) {
		if (isIntegerValue(leftTree)) {
			ExpressionNode node1 = factorOutPlusOrMinusNode(rightTree);
			return utils.multiplyR(node1.getRightTree(),
					leftTree.evaluateDouble() * node1.getLeft().evaluateDouble());
		}
		ExpressionNode appliedLeft = apply(leftTree);
		ExpressionNode appliedRight = apply(rightTree);
		int rightMultiplier = utils.getLeftMultiplier(appliedRight);
		return rightMultiplier == 1
				? utils.multiplyR(appliedLeft, appliedRight)
				: utils.multiply(appliedLeft, appliedRight.getRightTree())
				.multiplyR(rightMultiplier);
	}

	private ExpressionNode factorOutPlusOrMinusNode(ExpressionNode node) {
		if (node.isOperation(Operation.PLUS)) {
			double v = node.getRightTree().evaluateDouble();
			return v == -1
			? utils.newNode(node.getLeftTree(), Operation.MINUS, utils.newDouble(1).wrap())
			: factorOutAddition(node.getLeftTree(), node.getRightTree());

		}
		return factorOutSubtraction(node);
	}

	private ExpressionNode factorOutAddition(ExpressionNode leftTree,
			ExpressionNode rightTree) {
		double leftValue = leftTree.evaluateDouble();
		double rightValue = rightTree.evaluateDouble();
		Operation operation = rightValue < 0 ? Operation.MINUS : Operation.PLUS;
		if (leftTree.isLeaf()) {
			return factorOutGCD((int) leftValue, rightTree, operation);
		}
		if (rightTree.isLeaf()) {
			return factorOutGCD(leftTree, (int) Math.abs(rightValue), operation);
		}
		return new ExpressionNode(leftTree.getKernel(), leftTree, Operation.PLUS, rightTree);
	}

	private ExpressionNode factorOutSubtraction(ExpressionNode node) {
		ExpressionNode leftTree = node.getLeftTree();
		ExpressionNode rightTree = node.getRightTree();
		double leftValue = leftTree.evaluateDouble();
		double rightValue = rightTree.evaluateDouble();

		if (leftTree.isLeaf()) {
			int number = (int) leftTree.evaluateDouble();
			if (leftValue < 0 && rightValue > 0) {
				return factorOutSubOfTwoNegatives(leftTree, rightTree);
			}
			if (leftValue > 0 && rightValue > 0) {
				return factorOutSubtraction(number, rightTree);
			}
		}

		if (rightTree.isLeaf()) {
			if (leftValue < 0 && rightValue > 0) {
				return factorOutSubOfTwoNegatives(leftTree, rightTree);
			}

			if (leftValue > 0 && rightValue > 0) {
				return factorOutSubtraction(leftTree, rightTree);
			}
		}

		return node;
	}

	ExpressionNode factorOutSubOfTwoNegatives(ExpressionNode leftTree, ExpressionNode rightTree) {
		return utils.makeNegative(factorOutSubtraction(leftTree, rightTree));
	}

	private ExpressionNode factorOutSubtraction(ExpressionNode leftTree, ExpressionNode rightTree) {
		if (isIntegerValue(leftTree)) {
			int constNumber = (int) leftTree.evaluateDouble();
			if (rightTree.isOperation(Operation.MULTIPLY) && isIntegerValue(rightTree.getLeft())) {
				return factorOutGCD(constNumber, rightTree, Operation.PLUS);
			}
			return utils.newNode(
					utils.newDouble(-constNumber), Operation.PLUS, rightTree);
		}

		int constNumber = (int) rightTree.evaluateDouble();
		if (leftTree.isOperation(Operation.MULTIPLY) && isIntegerValue(leftTree.getLeft())) {
			return factorOutGCDWithSub(leftTree, constNumber);

		}
		return utils.newNode(
				leftTree.getRightTree(), Operation.PLUS, rightTree);
	}

	private ExpressionNode factorOutSubtraction(int number, ExpressionNode expr) {
		int treeMultiplier = utils.getLeftMultiplier(expr);
		if (treeMultiplier != 1) {
			return factorOut(number, treeMultiplier, expr.getRightTree());
		}

		return utils.newNode(utils.newDouble(number), Operation.PLUS, expr);
	}

	private ExpressionNode factorOut(int num, int exprMultiplier, ExpressionNode expression) {
		double gcd = Kernel.gcd(num, exprMultiplier);

		double numFactor = num / gcd;
		double exprFactor = exprMultiplier / gcd;
		return utils.multiplyR(
				utils.newNode(utils.newDouble(numFactor),
						Operation.MINUS, utils.multiplyR(expression, exprFactor)),
				gcd);
	}

	private ExpressionNode factorOutGCD(ExpressionNode node, int constNumber, Operation operation) {
		return factorOutGCD(node, constNumber, operation, false);
	}

	private ExpressionNode factorOutGCD(int constNumber, ExpressionNode node, Operation operation) {
		return factorOutGCD(node, constNumber, operation, true);
	}

	private ExpressionNode factorOutGCDWithSub(ExpressionNode node, int constNumber) {
		return factorOutGCDWithSub(node, constNumber, false);
	}

	ExpressionNode factorOutGCD(ExpressionNode rightTree, int constNumber, Operation operation,
			boolean numberFirst) {
		double treeMultiplier = rightTree.getLeft().evaluateDouble();
		double gcd = Kernel.gcd(constNumber, (int) Math.round(treeMultiplier));

		double constFactor = constNumber / gcd;
		double treeFactor = treeMultiplier / gcd;
		boolean shouldInvert = operation.equals(Operation.PLUS) && constFactor < 0;

		ExpressionValue factoredNumber = utils.newDouble(shouldInvert ? -constFactor : constFactor);
		ExpressionNode expFactor = utils.newDouble(treeFactor).wrap();

		ExpressionNode factoredExpression = utils.multiplyR(
				expFactor,
				rightTree.getRightTree()
		);

		ExpressionNode addition = numberFirst
				? getAddition(factoredNumber, operation, factoredExpression)
				: getAddition(factoredExpression, operation, factoredNumber);
		return utils.multiplyR(addition, shouldInvert ? -gcd : gcd);
	}

	private ExpressionNode getAddition(ExpressionValue factoredNumber, Operation operation,
			ExpressionValue factoredExpression) {
			if (utils.getLeftMultiplier(factoredExpression.wrap()) < 0) {
				return utils.newNode(factoredNumber, flip(operation), factoredExpression);
			}
			return utils.newNode(factoredNumber, operation, factoredExpression);
	}

	ExpressionNode factorOutGCDWithSub(ExpressionNode rightTree, int constNumber,
			boolean numberFirst) {
		int treeMultiplier = (int) rightTree.getLeft().evaluateDouble();
		double gcd = Kernel.gcd(constNumber, treeMultiplier);

		double constFactor = constNumber / gcd;
		double treeFactor = treeMultiplier / gcd;

		ExpressionValue factoredNumber = utils.newDouble(constFactor);
		ExpressionNode expFactor = utils.newDouble(treeFactor).wrap();

		ExpressionNode factoredExpression = utils.multiplyR(
				expFactor,
				rightTree.getRightTree()
		);

		ExpressionNode addition = !numberFirst
				? utils.newNode(factoredExpression, Operation.MINUS, factoredNumber)
				: utils.newNode(factoredNumber, Operation.MINUS, factoredExpression);
		return utils.multiplyR(addition, gcd);
	}
}