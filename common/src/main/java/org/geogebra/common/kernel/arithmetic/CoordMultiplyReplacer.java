package org.geogebra.common.kernel.arithmetic;

import org.geogebra.common.kernel.geos.GeoLine;
import org.geogebra.common.plugin.Operation;

/**
 * Replaces the xcoode, ycoord and zcoord operations
 * with a function_or_multiply operation.
 */
public class CoordMultiplyReplacer implements Traversing {

	final private FunctionVariable xVar;
	final private FunctionVariable yVar;
	final private FunctionVariable zVar;

	/**
	 * Create a new CoordMultiplyReplacer
	 * @param xVar x variable
	 * @param yVar y variable
	 * @param zVar z variable
	 */
	public CoordMultiplyReplacer(FunctionVariable xVar,
			FunctionVariable yVar, FunctionVariable zVar) {
		this.xVar = xVar;
		this.yVar = yVar;
		this.zVar = zVar;
	}

	@Override
	public ExpressionValue process(ExpressionValue ev) {
		if (ev.isExpressionNode()) {
			return processExpressionNode((ExpressionNode) ev);
		}
		return ev;
	}

	private ExpressionValue processExpressionNode(ExpressionNode node) {
		switch (node.getOperation()) {
		case XCOORD:
			if (xVar != null && !leftHasCoord(node)) {
				return new ExpressionNode(node.getKernel(), xVar, Operation.MULTIPLY_OR_FUNCTION,
						node.getLeft());
			}
			return node;
		case YCOORD:
			if (yVar != null && !leftHasCoord(node)) {
				return new ExpressionNode(node.getKernel(), yVar, Operation.MULTIPLY_OR_FUNCTION,
						node.getLeft());
			}
			return node;
		case ZCOORD:
			if (zVar != null && !leftHasCoord(node)) {
				return new ExpressionNode(node.getKernel(), zVar, Operation.MULTIPLY_OR_FUNCTION,
						node.getLeft());
			}
			return node;
		default:
			return node;
		}
	}

	private boolean leftHasCoord(ExpressionNode node) {
		ExpressionValue left = node.getLeft();
		return left.evaluatesToNDVector()
				|| left.getValueType() == ValueType.COMPLEX
				|| (left.unwrap() instanceof GeoLine);
	}
}
