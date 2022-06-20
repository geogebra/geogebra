package org.geogebra.common.kernel.interval.function;

import static org.geogebra.common.kernel.interval.function.IntervalFunction.hasMoreVariables;
import static org.geogebra.common.kernel.interval.function.IntervalFunction.isOperationSupported;

import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.ExpressionValue;
import org.geogebra.common.kernel.arithmetic.MyList;
import org.geogebra.common.kernel.arithmetic.MyNumberPair;
import org.geogebra.common.plugin.Operation;

public class ConditionalSupport {

	static boolean isSupportedIf(ExpressionNode node) {
		if (node.getOperation() == Operation.IF) {
			return isConditionSupported(node.getLeftTree())
					&& isOperationSupported(node.getRightTree());
		} else if (node.getOperation() == Operation.IF_ELSE) {
			boolean rightSupported = isOperationSupported(node.getRightTree());
			boolean rightOneVariable = !hasMoreVariables(node.getRightTree());
			ExpressionNode leftTree = node.getLeftTree();
			ExpressionValue left = leftTree.unwrap();
			if (left instanceof MyNumberPair) {
				MyNumberPair pair = (MyNumberPair) left;
				ExpressionNode ifCondition = pair.getX().wrap();
				ExpressionNode ifExpr = pair.getY().wrap();
				return isConditionSupported(ifCondition)
						&& isOperationSupported(ifExpr) && rightSupported
						&& !hasMoreVariables(ifExpr)
						&& rightOneVariable;
			}
			return rightSupported && rightOneVariable;
		} else if (node.getOperation() == Operation.IF_LIST) {
			return isIfListSupported(node);
		}
		return false;
	}

	private static boolean isConditionSupported(ExpressionNode node) {
        boolean operationSupported = isOperationForConditionals(node.getOperation());
		if (!operationSupported) {
			return false;
		}

		if (isSupportedAndInterval(node)) {
			return true;
		}

		if (isConstantAndLeaf(node.getLeftTree(), node.getRightTree())) {
			return true;
		}
		return false;
	}

	private static boolean isSupportedAndInterval(ExpressionNode node) {
		if (!Operation.AND_INTERVAL.equals(node.getOperation())) {
			return false;
		}

		ExpressionValue greaterValue = node.getLeftTree().getLeft();
		ExpressionValue targetValue = node.getLeftTree().getRight();
		ExpressionValue lessValue = node.getRightTree().getRight();
		return greaterValue.isConstant()
			     && !targetValue.isConstant() && targetValue.isLeaf()
				 && lessValue.isConstant();
	}

	private static boolean isConstantAndLeaf(ExpressionNode node1, ExpressionNode node2) {
		return (node1.isConstant() && node2.isLeaf()) || (node1.isLeaf() && node2.isConstant());
	}

	private static boolean isOperationForConditionals(Operation operation) {
		return Operation.NO_OPERATION.equals(operation) || operation.isInequality()
				|| Operation.EQUAL_BOOLEAN.equals(operation)
				|| Operation.AND_INTERVAL.equals(operation)
				|| Operation.NOT_EQUAL.equals(operation);
	}

	private static boolean isIfListSupported(ExpressionNode node) {
		MyList conditions = (MyList) node.getLeft();
		MyList conditionBodies = (MyList) node.getRight();
		for (int i = 0; i < conditions.size(); i++) {
			ExpressionValue condition = conditions.getItem(i);
			ExpressionValue body = conditionBodies.getItem(i);
			if (!(isConditionSupported(condition.wrap())
					&& isOperationSupported(body.wrap()))) {
				return false;
			}
		}
		return true;
	}
}
