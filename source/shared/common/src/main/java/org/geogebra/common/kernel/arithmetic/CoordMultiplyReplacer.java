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

package org.geogebra.common.kernel.arithmetic;

import org.geogebra.common.kernel.commands.EvalInfo;
import org.geogebra.common.kernel.geos.GeoLine;
import org.geogebra.common.kernel.geos.GeoSymbolic;
import org.geogebra.common.plugin.Operation;

/**
 * Replaces the xcoord, ycoord and zcoord operations
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
			return nodeOrMultiplication(node, "x", xVar);
		case YCOORD:
			return nodeOrMultiplication(node, "y", yVar);
		case ZCOORD:
			return nodeOrMultiplication(node, "z", zVar);
		default:
			return node;
		}
	}

	private ExpressionValue asMultiplication(ExpressionNode node, FunctionVariable fVar) {
		ExpressionNode mul = new ExpressionNode(node.getKernel(), fVar,
				Operation.MULTIPLY_OR_FUNCTION, node.getLeft()).traverse(this).wrap();
		mul.setBrackets(node.hasBrackets());
		return mul;
	}

	private ExpressionValue nodeOrMultiplication(ExpressionNode node, String varName,
			FunctionVariable var) {
		leftResolveVariables(node);
		if (var != null && !leftHasCoord(node)) {
			return asMultiplication(node, var);
		} else if (leftHasDoubleAsDefinition(node)) {
			return asMultiplication(node, new FunctionVariable(node.getKernel(), varName));
		}
		return node;
	}

	private boolean leftHasCoord(ExpressionNode node) {
		ExpressionValue left = node.getLeft();
		return left.evaluatesToNDVector()
				|| left.getValueType() == ValueType.COMPLEX
				|| (left.unwrap() instanceof GeoLine);
	}

	private void leftResolveVariables(ExpressionNode node) {
		ExpressionValue left = node.getLeft();
		if (left.unwrap().isVariable()) {
			left.resolveVariables(new EvalInfo(false).withSymbolicMode(SymbolicMode.SYMBOLIC_AV));
		}
	}

	private boolean leftHasDoubleAsDefinition(ExpressionNode node) {
		if (node.getLeft().unwrap() instanceof GeoSymbolic) {
			ExpressionNode definition = ((GeoSymbolic) node.getLeft().unwrap()).getDefinition();
			return definition != null && definition.unwrap() instanceof MyDouble;
		}
		return false;
	}
}
