package org.geogebra.common.kernel.geos;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.ExpressionValue;
import org.geogebra.common.kernel.arithmetic.MyVecNDNode;

public class VectorToMatrix {
	private final Kernel kernel;

	/**
	 * @param kernel the kernel.
	 */
	public VectorToMatrix(Kernel kernel) {
		this.kernel = kernel;
	}

	/**
	 * Builds a matrix-like string {{x}, {y}, {z}} from ExpressionNode
	 *
	 * @param template StringTemplate to use.
	 * @param expressionNode as input.
	 * @return the matrix-like string.
	 */
	public String build(StringTemplate template, ExpressionNode expressionNode) {

		MyVecNDNode vecNode = (MyVecNDNode) expressionNode.getLeft();

		StringBuilder sb = new StringBuilder();
		sb.append("{");
		sb.append(surroundWithBrackets(vecNode.getX(), template));
		sb.append(", ");
		sb.append(surroundWithBrackets(vecNode.getY(), template));
		ExpressionValue z = vecNode.getZ();
		if (z != null) {
			sb.append(", ");
			sb.append(surroundWithBrackets(z, template));
		}
		sb.append("}");
		return sb.toString();

	}

	/**
	 * Builds a matrix-like string {{x}, {y}} from x, y
	 * @param tpl the StringTemplate to use.
	 * @param x value.
	 * @param y value.
	 * @return {{x}, {y}} format string.
	 */
	public String build(StringTemplate tpl, double x, double y) {
		StringBuilder sb = new StringBuilder();
		sb.append("{");
		sb.append(surroundWithBrackets(x, tpl));
		sb.append(", ");
		sb.append(surroundWithBrackets(y, tpl));
		sb.append("}");
		return sb.toString();
	}

	private String surroundWithBrackets(ExpressionValue value, StringTemplate tpl) {
		StringBuilder sb = new StringBuilder();
		sb.append("{");
		sb.append(value.toLaTeXString(true, tpl));
		sb.append("}");
		return sb.toString();
	}

	private String surroundWithBrackets(double value, StringTemplate tpl) {
		StringBuilder sb = new StringBuilder();
		sb.append("{");
		sb.append(kernel.format(value, tpl));
		sb.append("}");
		return sb.toString();
	}
}